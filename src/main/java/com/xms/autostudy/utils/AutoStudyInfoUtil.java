package com.xms.autostudy.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.xms.autostudy.analysis.StudyStatus;
import com.xms.autostudy.configuration.Redis;
import com.xms.autostudy.constant.AutoStudyConstant;
import com.xms.autostudy.queue.StudyQueue;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
/**
 * xumengsi
 */
public class AutoStudyInfoUtil {

    private static final String INFO_URL = "https://pc-api.xuexi.cn/open/api/score/get";

    private static final String SCORE_INFO_URL = "https://pc-api.xuexi.cn/open/api/score/today/queryrate";

    /**
     * 获取当前登录用户ID
     * @param token
     * @return
     */
    @SuppressWarnings("rawtypes")
	public static String getUserId(String token){
        ResponseEntity<StudyCountryPowerResponse> responseEntity = sendData(token, INFO_URL);
        return JSON.parseObject(JSON.toJSONString(responseEntity.getBody().getData())).get("userId").toString();
    }

    /**
     * 获取用户得分详情
     * @param token
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static List<StudyCountryUserScore> getUserScoreInfo(String token){
        ResponseEntity<StudyCountryPowerResponse> responseEntity = sendData(token, SCORE_INFO_URL);
        Object dayScoreDtos = JSON.parseObject(JSON.toJSONString(responseEntity.getBody().getData())).get("dayScoreDtos");
        List<StudyCountryUserScore> userScoreList = JSONArray.parseArray(JSON.toJSONString(dayScoreDtos), StudyCountryUserScore.class);
        return userScoreList;
    }

    /**
     * 判断该用户在该得分规则下是否获取今天所有的积分
     * @param token
     * @param ruleId
     * @return
     */
    public static Boolean isRuleCorrentStudyFinish(String token, int ruleId){
        List<StudyCountryUserScore> userScoreList = getUserScoreInfo(token);
        for (StudyCountryUserScore userScore: userScoreList){
            if(userScore.getRuleId().intValue() == ruleId && userScore.getCurrentScore() == userScore.getDayMaxScore()){
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    public static void updateUserStudyStatus(String qiangguoId, String username, StudyStatus studyStatus){
        StudyQueue studyQueue = SpringUtil.getBean(StudyQueue.class);
        String newDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String key = AutoStudyConstant.formatKey(AutoStudyConstant.USER_STUDY_STATUS, username, newDate);
        studyQueue.setUserStudyStatus(key, new StudyQueue.QueueInfo(username, qiangguoId, studyStatus.name(), new Date()));
    }

    /**
     * 发送数据
     * @param token
     * @param url
     * @return
     */
    @SuppressWarnings("rawtypes")
    private static ResponseEntity<StudyCountryPowerResponse> sendData(String token, String url){
        HttpHeaders headers = new HttpHeaders();
        headers.put(HttpHeaders.COOKIE, Arrays.asList("token="+ token));
        HttpEntity<String> req = new HttpEntity<>(null, headers);
        ResponseEntity<StudyCountryPowerResponse> responseEntity = getRestTemplate().exchange(url, HttpMethod.GET, req, StudyCountryPowerResponse.class);
        return responseEntity;
    }

    public static RestTemplate getRestTemplate(){
        return SpringUtil.getBean(RestTemplate.class);
    }

    @Getter
    @Setter
    public static class StudyCountryPowerResponse<T>{

        private T data;

        private String message;

        private String code;

        private String error;

        private String ok;

    }

    @Getter
    @Setter
    public static class StudyCountryUserScore{

        private Long  ruleId;

        private String name;

        private String desc;

        private Integer currentScore;

        private Integer dayMaxScore;
    }
}
