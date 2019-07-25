package com.xms.autostudy.pool;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/**
 * Created by xumengsi on 2019-07-24 16:12
 */
public interface AutoDriverInterface {

    public WebDriver getWebDriver();

    public void setWebDriver(WebDriver webDriver);

    public JavascriptExecutor getJavascriptExecutor();

    public void setJavascriptExecutor(JavascriptExecutor javascriptExecutor);
}
