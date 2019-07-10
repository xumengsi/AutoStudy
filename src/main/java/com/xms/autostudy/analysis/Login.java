package com.xms.autostudy.analysis;

import com.alibaba.fastjson.JSON;
import com.xms.autostudy.chromedriver.AutoDriver;
import com.xms.autostudy.configuration.RuleConfiguration;
import com.xms.autostudy.configuration.ScoreConfiguration;
import com.xms.autostudy.rule.AutoReadRule;
import com.xms.autostudy.rule.AutoVideoRule;
import com.xms.autostudy.utils.AutoStudyInfoUtil;
import com.xms.autostudy.utils.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * xumengsi
 */
public class Login {

    private static final Logger log = LoggerFactory.getLogger(Login.class);

    private String username;

    private RuleConfiguration ruleConfiguration;

    private static final String LOGIN_KEY = "login";

    private static final String READ_KEY = "read";

    private static final String READ_TIME_KEY = "readTime";

    private static final String VIDEO_KEY = "video";

    private static final String VIDEO_TIME_KEY = "videoTime";

    private Map<String, Boolean> AUTO_RESULT = new HashMap<String, Boolean>();


    private static final String LOGIN_URL = "https://pc.xuexi.cn/points/login.html";

    public Login(RuleConfiguration ruleConfiguration, String usernmae){
        this.ruleConfiguration = ruleConfiguration;
        this.username = usernmae;
    }

    /**
     * 获取登录二维码
     *
     * @return
     */
    public String getQRcode() {
        File file = null;
        AutoDriver autoDriver = new AutoDriver(ruleConfiguration);
        WebDriver webDriver = autoDriver.getDriver();
        JavascriptExecutor jsExecutor = autoDriver.getJSExecutor(webDriver);
        try {
            webDriver.get(LOGIN_URL);
            webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            WebElement element = webDriver.findElement((By.id("ddlogin-iframe")));
            file = element.getScreenshotAs(OutputType.FILE);
        } catch (Exception e) {
            log.error("获取二维码失败，失败原因：{}", e.getMessage());
            webDriver.quit();
        }
        autoContinue(webDriver, jsExecutor, username);
        return FileUtil.file2Base64(file);
    }

    /**
     * 执行自动加分
     * @param webDriver
     * @param jsExecutor
     * @param username
     */
    public void autoContinue(WebDriver webDriver, JavascriptExecutor jsExecutor, String username) {
        FutureRunnable runnable = new FutureRunnable() {

            long startTime = System.currentTimeMillis();

            @Override
            public void run() {
                if (System.currentTimeMillis() > startTime + 240000) {
                    log.warn("扫码超时......");
                    webDriver.quit();
                    getFuture().cancel(true);
                }
                if (!webDriver.getCurrentUrl().equals(LOGIN_URL)) {
                    log.info("扫码成功，执行学习强国自动加分策略......");
                    AutoStudyInfoUtil.updateUserStudyStatus(username, StudyStatus.STUDYING);
                    Map<String, ScoreConfiguration> rules = ruleConfiguration.getRules();
                    String token = webDriver.manage().getCookieNamed("token").getValue();
                    String userId = AutoStudyInfoUtil.getUserId(token);
                    log.info("用户ID：{} ,令牌token：{}; 登录成功！！！", userId, token);
                    if (StringUtils.isNotEmpty(token)) {
                        log.info("每日登录得分已完成......");
                        AUTO_RESULT.put(LOGIN_KEY, Boolean.TRUE);
                    }
                    try {
                        autoAnalysis(webDriver, jsExecutor, rules, token, userId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        Boolean isLoop = isAutoStudyFinish(token);
                        if(isLoop){
                            autoAnalysis(webDriver, jsExecutor, rules, token, userId);
                        }else{
                            log.info("用户ID：{} ,学习强国自动加分策略完成，退出模拟...", userId);
                            webDriver.quit();
                        }
                    }
                    getFuture().cancel(true);
                    return;
                }
                log.info("请使用手机APP进行扫码登录......");
            }
        };
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        Future<?> future = executorService.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS);
        runnable.setFuture(future);
    }

    /**
     * 执行自动加分策略分析
     *
     * @param webDriver
     * @param jsExecutor
     * @param rules
     * @param token
     * @param userId
     */
    public void autoAnalysis(WebDriver webDriver, JavascriptExecutor jsExecutor, Map<String, ScoreConfiguration> rules, String token, String userId) {
        List<AutoStudyInfoUtil.StudyCountryUserScore> userScoreList = AutoStudyInfoUtil.getUserScoreInfo(token);
        Long userCurrentScore = userScoreList.stream().map(AutoStudyInfoUtil.StudyCountryUserScore::getCurrentScore).count();
        log.info("用户ID：{} 当前得分： {}", userId, userCurrentScore);
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
                    case 1003:
                        ruleKey = VIDEO_TIME_KEY;
                        break;
                }
                this.execute(ruleKey, webDriver, rules, jsExecutor, token, userId, userScore);
            });
        }
        Boolean isLoop = this.isAutoStudyFinish(token);
        if (isLoop) {
            log.info("自动学习未达到25分，重复执行");
            autoAnalysis(webDriver, jsExecutor, rules, token, userId);
        }
    }

    /**
     * 判断加分规则
     * @param ruleKey
     * @param webDriver
     * @param rules
     * @param jsExecutor
     * @param token
     * @param userId
     * @param userScore
     */
    private void execute(String ruleKey, WebDriver webDriver, Map<String, ScoreConfiguration> rules, JavascriptExecutor jsExecutor, String token, String userId, AutoStudyInfoUtil.StudyCountryUserScore userScore) {
        if (StringUtils.isEmpty(ruleKey)) {
            return;
        }
        if (!this.isNotExecute(userId, userScore)) {
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
    }

    /**
     * 是否执行加分规则
     * @param userId
     * @param userScore
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

    public abstract class FutureRunnable implements Runnable {

        private Future<?> future;

        public Future<?> getFuture() {
            return future;
        }

        public void setFuture(Future<?> future) {
            this.future = future;
        }
    }

}
