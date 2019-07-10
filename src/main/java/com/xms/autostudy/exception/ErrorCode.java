package com.xms.autostudy.exception;

/**
 * Created by xumengsi on 2019-07-10 13:57
 */
public enum ErrorCode {

    E1000001("E1000001", "用户名或密码错误"),

    E1000002("E1000002", "您正在队列等待自动学习中，请勿重复登录"),

    E1000003("E1000003", "您正在自动学习中，请勿重复登录"),

    E1000004("E1000004", "您今天已经学满25分");


    private String code;

    private String msg;

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    private ErrorCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static String setErrorCode(ErrorCode rbacErrorCode, Object... params) {
        String msg = rbacErrorCode.getMsg();
        return String.format(msg, params);
    }

}
