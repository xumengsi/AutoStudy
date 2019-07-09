package com.xms.autostudy.rule;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import com.xms.autostudy.configuration.ScoreConfiguration;

/**
 * xumengsi
 */
public abstract class AbstractAutoRule {


    private WebDriver webDriver;

    private JavascriptExecutor jsExecutor;

    private String token;

    private String userId;

    private int ruleId;

    private ScoreConfiguration scoreConfiguration;

    public AbstractAutoRule(int ruleId, WebDriver webDriver, ScoreConfiguration scoreConfiguration, JavascriptExecutor jsExecutor, String token, String userId){
            this.ruleId = ruleId;
            this.webDriver = webDriver;
            this.jsExecutor = jsExecutor;
            this.scoreConfiguration = scoreConfiguration;
            this.token = token;
            this.userId = userId;
            if(StringUtils.isEmpty(this.token)){
                webDriver.quit();
                throw new RuntimeException("token is error,please return login");
            }
    }

    public abstract void AutoScore();

    public abstract void execute(int ruleId, WebDriver webDriver, JavascriptExecutor jsExecutor, String token, String userId);

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public void setWebDriver(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ScoreConfiguration getScoreConfiguration() {
        return scoreConfiguration;
    }

    public void setScoreConfiguration(ScoreConfiguration scoreConfiguration) {
        this.scoreConfiguration = scoreConfiguration;
    }

    public JavascriptExecutor getJsExecutor() {
        return jsExecutor;
    }

    public void setJsExecutor(JavascriptExecutor jsExecutor) {
        this.jsExecutor = jsExecutor;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getRuleId() {
        return ruleId;
    }

    public void setRuleId(int ruleId) {
        this.ruleId = ruleId;
    }
}
