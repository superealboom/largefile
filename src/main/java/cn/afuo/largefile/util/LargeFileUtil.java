package cn.afuo.largefile.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LargeFileUtil {

    public static String getFileNameByChunkName(String chunkName) {
        return chunkName.substring(0, chunkName.lastIndexOf("_"));
    }

    public static String getFilePathByFileName(String fileName) {
        return getProjectPath() + "src/main/resources/largeFile/" + fileName;
    }

    public static String getChunkDirectoryByFileName(String fileName) {
        return getProjectPath() + "uploadChunks" + File.separator + fileName + "_directory" + File.separator;
    }


    public static List<Integer> chunksCountByFileName(String fileName) {
        List<Integer> chunkNumberList = new ArrayList<>();
        String directoryPath = getChunkDirectoryByFileName(fileName);
        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        String chunkNumber = file.getName().substring(file.getName().lastIndexOf("_") + 1);
                        chunkNumberList.add(Integer.parseInt(chunkNumber));
                    }
                }
            }
        }
        return chunkNumberList;
    }


    private static String getProjectPath() {
        return System.getProperty("user.dir") + File.separator;
    }
}
