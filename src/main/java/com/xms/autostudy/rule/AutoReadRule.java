package com.xms.autostudy.rule;

import com.xms.autostudy.configuration.ScoreConfiguration;
import com.xms.autostudy.constant.AutoStudyConstant;
import com.xms.autostudy.page.PageHandle;
import com.xms.autostudy.utils.AutoStudyInfoUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
/**
 * xumengsi
 */
public class AutoReadRule extends AbstractAutoRule {

    private static final Logger log = LoggerFactory.getLogger(AutoReadRule.class);

    private static String READ_URL = "https://www.xuexi.cn/lgdata/index.json?_st=%s";

    private static final String REGEX = "https://www.xuexi.cn/lgpage/detail/index.html[?]id=\\d{19}";

    private static Pattern pattern = Pattern.compile(REGEX);


    public AutoReadRule(int ruleId, WebDriver webDriver, ScoreConfiguration scoreConfiguration, JavascriptExecutor jsExecutor, String token, String userId) {
        super(ruleId, webDriver, scoreConfiguration, jsExecutor, token, userId);
    }


    /**
     * 自动得分策略
     */
    @Override
    public void AutoScore() {
        log.info("开始自动学习......");
        WebDriver webDriver = this.getWebDriver();
        JavascriptExecutor jsExecutor = this.getJsExecutor();
        String userId = getUserId();
        String token = getToken();
        int ruleId = getRuleId();
        PageHandle.PageProcess pageProcess = new PageHandle.PageProcess(webDriver, jsExecutor, userId, READ_URL, pattern, AutoStudyConstant.USER_ISREAD, REGEX);
        PageHandle.start(pageProcess);
        this.execute(ruleId, webDriver, jsExecutor, token, userId);
        String currentWindow = webDriver.getWindowHandle();
        Set<String> allWindow = webDriver.getWindowHandles();
        allWindow.remove(currentWindow);
        if(allWindow != null && allWindow.size() > 0 ){
            allWindow.stream().collect(Collectors.toList()).forEach(window ->{
                webDriver.switchTo().window(window);
                webDriver.close();
            });
        }
        webDriver.switchTo().window(currentWindow);
    }

    /**
     * 执行自动化模拟
     * @param webDriver
     * @param jsExecutor
     */
    @Override
    public void execute(int ruleId, WebDriver webDriver, JavascriptExecutor jsExecutor, String token, String userId) {
        List<String> windows = webDriver.getWindowHandles().stream().collect(Collectors.toList());
        for (String window : windows){
            Boolean isStudyFinish = AutoStudyInfoUtil.isRuleCorrentStudyFinish(token, ruleId);
            if(isStudyFinish){
                log.info("用户ID：{} 在规则ID：{} 下已获取今天所有的积分", userId, ruleId);
                return ;
            }
            webDriver.switchTo().window(window);
            Actions action = new Actions(webDriver);
            int pageStopTime = 140000;
            try {
                pageStopTime = PageHandle.isOpenVideo(window, webDriver, jsExecutor, pageStopTime);
            }catch (Exception e){
                pageStopTime = 140000;
                log.info("该页面没有视频，只执行阅读操作......");
            }
            if(pageStopTime > 4 * 60 * 1000){
                log.info("该页面视频大于4分钟，直接跳过......");
                continue;
            }
            log.info("执行阅读操作，执行时间：{} 毫秒", pageStopTime);
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() <= startTime + pageStopTime + 3000) {
                try {
                    Random random = new Random();
                    Thread.sleep(random.nextInt(2000) + 200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                action.sendKeys(Keys.DOWN).build().perform();
            }
        }
    }


}
