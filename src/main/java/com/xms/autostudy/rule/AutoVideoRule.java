package com.xms.autostudy.rule;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xms.autostudy.configuration.ScoreConfiguration;
import com.xms.autostudy.constant.AutoStudyConstant;
import com.xms.autostudy.page.PageHandle;
import com.xms.autostudy.utils.AutoStudyInfoUtil;
/**
 * xumengsi
 */
public class AutoVideoRule extends AbstractAutoRule{

    private static final Logger log = LoggerFactory.getLogger(AutoVideoRule.class);

    private static final String VIDEO_URL = "https://www.xuexi.cn/lgdata/1novbsbi47k.json?_st=%S";

    private static final String REGEX = "https://www.xuexi.cn/[a-z\\d]{32}/[a-z\\d]{32}.html";

    private static Pattern pattern = Pattern.compile(REGEX);

    public AutoVideoRule(int ruleId, WebDriver webDriver, ScoreConfiguration scoreConfiguration, JavascriptExecutor jsExecutor, String token, String userId) {
        super(ruleId, webDriver, scoreConfiguration, jsExecutor, token, userId);
    }

    /**
     * 执行自动得分
     */
    @Override
    public void AutoScore() {
        log.info("开始自动学习......");
        WebDriver webDriver = this.getWebDriver();
        JavascriptExecutor jsExecutor = this.getJsExecutor();
        String token = getToken();
        String userId = getUserId();
        int ruleId = getRuleId();
        PageHandle.PageProcess pageProcess = new PageHandle.PageProcess(webDriver, jsExecutor, userId, VIDEO_URL, pattern, AutoStudyConstant.USER_ISVIDEO, REGEX);
        PageHandle.start(pageProcess);
        this.execute(ruleId, webDriver, jsExecutor, token, userId);

    }

    /**
     * 执行自动化模拟
     * @param ruleId
     * @param webDriver
     * @param jsExecutor
     * @param token
     * @param userId
     */
    @Override
    public void execute(int ruleId, WebDriver webDriver, JavascriptExecutor jsExecutor, String token, String userId) {
        List<String> windows = webDriver.getWindowHandles().stream().collect(Collectors.toList());
        for (String window: windows){
            Boolean isStudyFinish = AutoStudyInfoUtil.isRuleCorrentStudyFinish(token, ruleId);
            if(isStudyFinish){
                log.info("用户ID：{} 在规则ID：{} 下已获取今天所有的积分", userId, ruleId);
                return ;
            }
            webDriver.switchTo().window(window);
            int pageStopTime = 185000;
            try {
                pageStopTime = PageHandle.isOpenVideo(window, webDriver, jsExecutor, pageStopTime);
                if(pageStopTime > 4 * 60 * 1000){
                    log.info("该页面视频大于4分钟，直接跳过......");
                    continue;
                }
                Thread.sleep(pageStopTime + 3000);
            }catch (Exception e){
                log.warn(e.getMessage());
            }
        }
    }
}
