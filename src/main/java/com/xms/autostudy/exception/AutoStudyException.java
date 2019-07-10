package com.xms.autostudy.exception;

/**
 * Created by xumengsi on 2019-07-10 11:45
 */
public class AutoStudyException extends  RuntimeException{

    private String code;

    private String msg;


    public AutoStudyException(ErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.getMsg());

    }

    public AutoStudyException(ErrorCode errorCode, Throwable t) {
        this(errorCode.getCode(), errorCode.getMsg(), t);

    }

    public AutoStudyException(String msg) {
        super(msg);
    }


    public AutoStudyException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;

    }

    public AutoStudyException(String code, String msg, Throwable t) {
        super(msg, t);
        this.code = code;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
