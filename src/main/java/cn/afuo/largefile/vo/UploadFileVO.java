package cn.afuo.largefile.vo;

import lombok.Data;

import java.util.List;

@Data
public class UploadFileVO {

    private String fileName;

    private String filePath;

    private List<Integer> chunkNumberList;
}
