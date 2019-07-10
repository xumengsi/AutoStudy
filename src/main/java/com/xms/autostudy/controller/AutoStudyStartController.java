package com.xms.autostudy.controller;

import com.xms.autostudy.analysis.Login;
import com.xms.autostudy.analysis.StudyStatus;
import com.xms.autostudy.configuration.Redis;
import com.xms.autostudy.configuration.RuleConfiguration;
import com.xms.autostudy.constant.AutoStudyConstant;
import com.xms.autostudy.exception.AutoStudyException;
import com.xms.autostudy.exception.ErrorCode;
import com.xms.autostudy.queue.StudyQueue;
import com.xms.autostudy.response.AutoStudyResponse;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * xumengsi
 */
@Controller
public class AutoStudyStartController {

    @Autowired
    private RuleConfiguration ruleConfiguration;

    @Autowired
    private Redis redis;

    @Autowired
    private StudyQueue studyQueue;

    @GetMapping(value="/autoStart")
    public AutoStudyResponse<String> autoStart(@RequestBody @Validated User user)throws IOException {
        //判断用户密码
        String userNameKey = AutoStudyConstant.formatKey(AutoStudyConstant.USER_NAME_KEY, user.getUsername());
        String password = redis.get(userNameKey);
        if(StringUtils.isEmpty(password) || !user.getPassword().equals(password)){
              throw new AutoStudyException(ErrorCode.E1000001);
        }
        //判断学习状态
        String newDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String userStudyKey = AutoStudyConstant.formatKey(AutoStudyConstant.USER_STUDY_STATUS, user.getUsername(), newDate);
        StudyQueue.QueueInfo queueInfo = studyQueue.getUserStudyStatus(userStudyKey);
        if(queueInfo != null && queueInfo.getStatus().equals(StudyStatus.QUEUESTUDY.name())){
            throw new AutoStudyException(ErrorCode.E1000002);
        }
        if(queueInfo != null  && queueInfo.getStatus().equals(StudyStatus.STUDYING.name())){
            throw new AutoStudyException(ErrorCode.E1000003);
        }
        if(queueInfo != null  && queueInfo.getStatus().equals(StudyStatus.FILISH.name())){
            throw new AutoStudyException(ErrorCode.E1000004);
        }
        //登录
        Login login = new Login(ruleConfiguration, user.getUsername());
        String base64Str =login.getQRcode();
        return AutoStudyResponse.success(base64Str);
    }

    @Getter
    @Setter
    public static class User{

        @NotEmpty(message = "用户名不能为空")
        private String username;

        @NotEmpty(message = "密码不能为空")
        private String password;

    }
}
