package cn.afuo.largefile.domain;


import cn.afuo.largefile.enums.ResultEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {

    private int code;
    private String message;
    private T data;

    private Result(ResultEnum resultEnum) {
        this.code = resultEnum.getCode();
        this.message = resultEnum.getMessage();
    }
    private Result(ResultEnum resultEnum, T data) {
        this.code = resultEnum.getCode();
        this.message = resultEnum.getMessage();
        this.data = data;
    }


    public static <T> Result<T> success() {
        return new Result<>(ResultEnum.SUCCESS);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ResultEnum.SUCCESS, data);
    }

    public static <T> Result<T> fail() {
        return new Result<>(ResultEnum.FAIL);
    }

    public Result<T> code(int code) {
        this.setCode(code);
        return this;
    }

    public Result<T> message(String message) {
        this.setMessage(message);
        return this;
    }
}
