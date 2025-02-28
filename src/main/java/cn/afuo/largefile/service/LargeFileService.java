package cn.afuo.largefile.service;


import cn.afuo.largefile.constant.RedisConstants;
import cn.afuo.largefile.domain.Result;
import cn.afuo.largefile.util.LargeFileUtil;
import cn.afuo.largefile.vo.UploadFileVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LargeFileService {


    private final RedissonClient redissonClient;

    public LargeFileService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 获取文件
     */
    public Result<UploadFileVO> getFile(UploadFileVO uploadFileVO) {
        String filePath = LargeFileUtil.getFilePathByFileName(uploadFileVO.getFileName());
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            uploadFileVO.setFilePath(filePath);
            return Result.success(uploadFileVO).code(201).message("文件已存在");
        }

        List<Integer> chunkNumberList = LargeFileUtil.chunksCountByFileName(uploadFileVO.getFileName());
        uploadFileVO.setChunkNumberList(chunkNumberList);
        return Result.success(uploadFileVO);
    }


    /**
     * 上传文件
     */
    public Result<?> uploadFile(MultipartFile file, Integer chunkCount) throws Exception {
        if (file == null || file.isEmpty() || StringUtils.isBlank(file.getOriginalFilename())) {
            return Result.fail();
        }
        // 文件名
        String fileName = LargeFileUtil.getFileNameByChunkName(file.getOriginalFilename());
        // 块路径
        String chunkFilePath = LargeFileUtil.getChunkDirectoryByFileName(fileName) + file.getOriginalFilename();
        Path path = Paths.get(chunkFilePath);
        if (!Files.exists(path)) {
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());
            log.info("{} 创建成功", path);
        }

        List<Integer> chunkNumberList = LargeFileUtil.chunksCountByFileName(fileName);

        if (!CollectionUtils.isEmpty(chunkNumberList) && Integer.valueOf(chunkNumberList.size()).equals(chunkCount)) {
            String lockName = RedisConstants.LARGEFILE_LOCK_PREFIX + fileName;
            RLock lock = redissonClient.getLock(lockName);
            // 参数：尝试获取锁的最大等待时间、锁持有的超时时间、两个时间参数的单位
            boolean flag = lock.tryLock(0, RedisConstants.MERGE_LARGEFILE_LOCK_TIME, TimeUnit.MILLISECONDS);
            if (flag) {
                return mergeChunks(fileName, chunkCount);
            }
        }
        return Result.success().message("上传块成功，未合并");
    }

    /**
     * 合并块
     */
    public Result<?> mergeChunks(String fileName, int chunkCount) {
        String filePath = LargeFileUtil.getFilePathByFileName(fileName);
        Path finalPath = Paths.get(filePath);
        try (OutputStream outputStream = Files.newOutputStream(finalPath)) {
            String chunkFilePrefixPath = LargeFileUtil.getChunkDirectoryByFileName(fileName) + fileName;
            for (int i = 0; i < chunkCount; i++) {
                Path tempFilePath = Paths.get(chunkFilePrefixPath + "_" + i);
                try (InputStream inputStream = Files.newInputStream(tempFilePath)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
                // Files.delete(tempFilePath);
            }
        } catch (IOException e) {
            log.error("文件合并异常", e);
            return Result.fail();
        }
        return Result.success().code(201);
    }
}
