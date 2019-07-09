package com.play.english.data;

/**
 * @author chaiqx on 2019/6/4
 */
public class Result<T> {

    public static final int SUCCEED = 1;
    public static final int FAILED = -1;

    private int code;
    private String msg;
    private T data;

    public Result() {
        this(SUCCEED, "", null);
    }

    public Result(T data) {
        this(SUCCEED, "", data);
    }

    public Result(int code, String msg) {
        this(code, msg, null);
    }

    public Result(int code, String msg, T data) {
        this.data = data;
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
