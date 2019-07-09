package com.xms.autostudy.chromedriver;

import com.xms.autostudy.configuration.RuleConfiguration;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * xumengsi
 */
public class AutoDriver {

    private RuleConfiguration ruleConfiguration;

    public AutoDriver(RuleConfiguration ruleConfiguration){
        this.ruleConfiguration = ruleConfiguration;
    }

    public WebDriver getDriver(){
        ChromeOptions chromeOptions = new ChromeOptions();
        if(!ruleConfiguration.getBrowserEnabled()){
            chromeOptions.setHeadless(Boolean.TRUE);
        }
        return new ChromeDriver(chromeOptions);
    }

    public JavascriptExecutor getJSExecutor(WebDriver driver){
        return (JavascriptExecutor) driver;
    }
}