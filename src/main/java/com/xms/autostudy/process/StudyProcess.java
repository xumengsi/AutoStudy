package com.xms.autostudy.process;

import com.xms.autostudy.analysis.StudyStatus;
import com.xms.autostudy.configuration.ScoreConfiguration;
import com.xms.autostudy.rule.AutoReadRule;
import com.xms.autostudy.rule.AutoVideoRule;
import com.xms.autostudy.utils.AutoStudyInfoUtil;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by xumengsi on 2019-07-15 10:54
 */
public class StudyProcess {

    private static final Logger log = LoggerFactory.getLogger(StudyProcess.class);

    private static final String READ_KEY = "read";

    private static final String READ_TIME_KEY = "readTime";

    private static final String VIDEO_KEY = "video";

    private static final String VIDEO_TIME_KEY = "videoTime";

    /**
     * 执行自动加分策略分析
     *
     * @param webDriver chrome驱动
     * @param jsExecutor js执行驱动
     * @param rules 得分规则
     * @param token 强国的登录token
     * @param qiangguoId 强国ID
     */
    public void autoAnalysis(WebDriver webDriver, JavascriptExecutor jsExecutor, Map<String, ScoreConfiguration> rules, String token, String qiangguoId, String username) {
        List<AutoStudyInfoUtil.StudyCountryUserScore> userScoreList = AutoStudyInfoUtil.getUserScoreInfo(token);
        Long userCurrentScore = userScoreList.stream().map(AutoStudyInfoUtil.StudyCountryUserScore::getCurrentScore).count();
        log.info("用户ID：{} 当前得分： {}", qiangguoId, userCurrentScore);
        if (!CollectionUtils.isEmpty(userScoreList)) {
            userScoreList.forEach(userScore -> {
                String ruleKey = null;
                int ruleId = userScore.getRuleId().intValue();
                switch (ruleId) {
                    case 1:
                        ruleKey = READ_KEY;
                        break;
                    case 2:
                        ruleKey = VIDEO_KEY;
                        break;
                    case 1002:
                        ruleKey = READ_TIME_KEY;
                        break;
                    case 1003:
                        ruleKey = VIDEO_TIME_KEY;
                        break;
                }
                this.execute(ruleKey, webDriver, rules, jsExecutor, token, qiangguoId, userScore);
            });
        }
        Boolean isLoop = this.isAutoStudyFinish(token);
        if (isLoop) {
            log.info("自动学习未达到25分，重复执行");
            autoAnalysis(webDriver, jsExecutor, rules, token, qiangguoId, username);
        }else{
            AutoStudyInfoUtil.updateUserStudyStatus(qiangguoId, username, StudyStatus.FILISH);
            AutoStudyInfoUtil.deleteUserStudyDriverInfo(qiangguoId);
        }
    }

    /**
     * 判断加分规则
     * @param ruleKey 规则ID
     * @param webDriver chrome驱动
     * @param rules 得分规则
     * @param jsExecutor js驱动
     * @param token 登录token
     * @param userId 强国ID
     * @param userScore 得分详情
     */
    private void execute(String ruleKey, WebDriver webDriver, Map<String, ScoreConfiguration> rules, JavascriptExecutor jsExecutor, String token, String userId, AutoStudyInfoUtil.StudyCountryUserScore userScore) {
        if (StringUtils.isEmpty(ruleKey)) {
            return;
        }
        if (this.isNotExecute(userId, userScore)) {
            return;
        }
        ScoreConfiguration scoreConfiguration = rules.get(ruleKey);
        int ruleId = userScore.getRuleId().intValue();
        if (ruleId == 1 || ruleId == 1002) {
            AutoReadRule autoReadRule = new AutoReadRule(ruleId, webDriver, scoreConfiguration, jsExecutor, token, userId);
            autoReadRule.AutoScore();
        }
        if (ruleId == 2 || ruleId == 1003) {
            AutoVideoRule autoVideoRule = new AutoVideoRule(ruleId, webDriver, scoreConfiguration, jsExecutor, token, userId);
            autoVideoRule.AutoScore();
        }

    }

    /**
     * 是否执行加分规则
     * @param userId 强国ID
     * @param userScore 得分详情
     * @return
     */
    private Boolean isNotExecute(String userId, AutoStudyInfoUtil.StudyCountryUserScore userScore) {
        if (userScore.getCurrentScore() != userScore.getDayMaxScore()) {
            return Boolean.FALSE;
        }
        log.info("用户ID：{} >>> {}当天得分已完成", userId, userScore.getName());
        return Boolean.TRUE;
    }


    private Boolean isAutoStudyFinish(String token){
        List<AutoStudyInfoUtil.StudyCountryUserScore> userScoreList = AutoStudyInfoUtil.getUserScoreInfo(token);
        Boolean isLoop = Boolean.FALSE;
        for (AutoStudyInfoUtil.StudyCountryUserScore x : userScoreList) {
            if (x.getRuleId().intValue() == 1 ||
                    x.getRuleId().intValue() == 2 ||
                    x.getRuleId().intValue() == 1002 ||
                    x.getRuleId().intValue() == 1003) {
                if (x.getCurrentScore() != x.getDayMaxScore()) {
                    isLoop = Boolean.TRUE;
                }
            }
        }
        return isLoop;
    }

}
