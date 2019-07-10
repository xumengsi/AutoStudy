package com.xms.autostudy.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xms.autostudy.exception.AutoStudyException;
import com.xms.autostudy.exception.ErrorCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by xumengsi on 2019-07-10 14:18
 */
@Getter
@Setter
public class AutoStudyResponse<T> {

    private T data;

    private String code = "0";

    private String msg = "SUCCESS";

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timestamp = new Date();

    public static <T> AutoStudyResponse<T> success(T data) {
        AutoStudyResponse<T> response = new AutoStudyResponse<>();
        response.setData(data);
        return response;
    }

    public static <T> AutoStudyResponse<T> fail(ErrorCode errorCode) {
        AutoStudyResponse<T> response = new AutoStudyResponse<T>();
        response.setMsg(errorCode.getMsg());
        response.setCode(errorCode.getCode());
        return response;
    }

    public static <T> AutoStudyResponse<T> fail(String code, String msg) {
        AutoStudyResponse<T> response = new AutoStudyResponse<T>();
        response.setMsg(msg);
        response.setCode(code);
        return response;
    }

    public static <T> AutoStudyResponse<T> fail(AutoStudyException e) {
        AutoStudyResponse<T> response = new AutoStudyResponse<T>();
        response.setMsg(e.getMsg());
        response.setCode(e.getCode());
        return response;
    }

    public static <T> AutoStudyResponse<T> paramError(String msg) {
        AutoStudyResponse<T> response = new AutoStudyResponse<T>();
        response.setMsg(msg);
        response.setCode("PARAM_ERROR");
        return response;
    }
}
