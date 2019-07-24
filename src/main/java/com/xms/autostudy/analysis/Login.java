package com.xms.autostudy.analysis;

import com.xms.autostudy.chromedriver.AutoDriver;
import com.xms.autostudy.configuration.RuleConfiguration;
import com.xms.autostudy.configuration.ScoreConfiguration;
import com.xms.autostudy.utils.AutoStudyInfoUtil;
import com.xms.autostudy.utils.FileUtil;
import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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

    private static final String LOGIN_URL = "https://pc.xuexi.cn/points/login.html";

    public Login(RuleConfiguration ruleConfiguration, String usernmae){
        this.ruleConfiguration = ruleConfiguration;
        this.username = usernmae;
    }

    /**
     * 获取登录二维码
     *
     * @return 二维码
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
                    Map<String, ScoreConfiguration> rules = ruleConfiguration.getRules();
                    String token = webDriver.manage().getCookieNamed("token").getValue();
                    String qiangguoId = AutoStudyInfoUtil.getUserId(token);
                    log.info("扫码登录成功，加入执行学习强国自动加分队列......");
                    AutoStudyInfoUtil.updateUserStudyStatus(qiangguoId, username, StudyStatus.STUDYING);
                    AutoStudyInfoUtil.setUserStudyDriverInfo(webDriver, jsExecutor, rules, token, qiangguoId, username);
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

    public abstract static class FutureRunnable implements Runnable {

        private Future<?> future;

        public Future<?> getFuture() {
            return future;
        }

        public void setFuture(Future<?> future) {
            this.future = future;
        }
    }

}
