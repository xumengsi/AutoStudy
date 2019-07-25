package com.xms.autostudy.pool;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/**
 * Created by xumengsi on 2019-07-24 10:37
 */
public class AutoDriver implements AutoDriverInterface{

    private WebDriver webDriver;

    private JavascriptExecutor javascriptExecutor;

    public AutoDriver(){};

    public AutoDriver(WebDriver webDriver, JavascriptExecutor javascriptExecutor){
        this.webDriver = webDriver;
        this.javascriptExecutor = javascriptExecutor;
    }

    @Override
    public WebDriver getWebDriver() {
        return this.webDriver;
    }

    @Override
    public void setWebDriver(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @Override
    public JavascriptExecutor getJavascriptExecutor() {
        return this.javascriptExecutor;
    }

    @Override
    public void setJavascriptExecutor(JavascriptExecutor javascriptExecutor) {
        this.javascriptExecutor = javascriptExecutor;
    }

}
