package cn.afuo.largefile.controller;

import cn.afuo.largefile.domain.Result;
import cn.afuo.largefile.service.LargeFileService;
import cn.afuo.largefile.vo.MergeChunksVO;
import cn.afuo.largefile.vo.UploadFileVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;


@Slf4j
@Controller
@RequestMapping("/file")
public class LargeFileController {

    private final LargeFileService largeFileService;

    public LargeFileController(LargeFileService largeFileService) {
        this.largeFileService = largeFileService;
    }

    /**
     * 大文件上传页面
     */
    @GetMapping("/index")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("modules/largefile/index");
        return modelAndView;
    }

    /**
     * 获取文件（入参json）
     */
    @ResponseBody
    @PostMapping("/fileByJson")
    public Result<?> fileByJson(@RequestBody UploadFileVO uploadFileVO) {
        return largeFileService.getFile(uploadFileVO);
    }

    /**
     * 获取文件（入参form）
     */
    @ResponseBody
    @PostMapping("/fileByForm")
    public Result<?> fileByForm(UploadFileVO uploadFileVO) {
        return largeFileService.getFile(uploadFileVO);
    }

    /**
     * 上传文件
     */
    @ResponseBody
    @PostMapping("/upload")
    public Result<?> upload(@RequestParam("chunkCount") Integer chunkCount,
                            @RequestParam("file") MultipartFile file) throws Exception {
        return largeFileService.uploadFile(file, chunkCount);
    }

    /**
     * 合并文件
     */
    @ResponseBody
    @PostMapping("/marge")
    public Result<?> marge(MergeChunksVO mergeChunksVO) {
        return largeFileService.mergeChunks(mergeChunksVO.getFileName(), mergeChunksVO.getChunkCount());
    }

}
