package com.xms.autostudy.exception.handler;

import com.xms.autostudy.exception.AutoStudyException;
import com.xms.autostudy.response.AutoStudyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by xumengsi on 2019-07-10 14:16
 */
public class RbacGlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RbacGlobalExceptionHandler.class);


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public AutoStudyResponse<?> excpetionHandler(HttpServletRequest request,
                                                 MethodArgumentNotValidException exception) {
        log.error("MethodArgumentNotValidException Handler ", exception);
        AutoStudyResponse<?> response = new AutoStudyResponse<>();
        // 解析原错误信息，封装后返回，此处返回非法的字段名称，原始值，错误信息
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            return AutoStudyResponse.paramError(error.getDefaultMessage());
        }
        return response;
    };


    @ExceptionHandler(value = AutoStudyException.class)
    @ResponseStatus(HttpStatus.OK)
    public AutoStudyResponse<?> autoStudyExceptionHandler(HttpServletRequest request, AutoStudyException exception) {
        return AutoStudyResponse.fail(exception);
    }
}
