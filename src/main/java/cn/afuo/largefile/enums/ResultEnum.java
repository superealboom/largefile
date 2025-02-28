package cn.afuo.largefile.enums;

import lombok.Getter;

@Getter
public enum ResultEnum {
    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),
    ;

    ResultEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private final int code;
    private final String message;

}
