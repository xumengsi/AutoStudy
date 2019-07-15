package com.xms.autostudy.queue;

import com.xms.autostudy.configuration.ScoreConfiguration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.util.Map;

/**
 * Created by xumengsi on 2019-07-15 10:45
 */
@Getter
@Setter
@AllArgsConstructor
public class DriverInfo{

    private WebDriver driver;

    private JavascriptExecutor jsExecutor;

    private Map<String, ScoreConfiguration> rules;

    private String token;

    private String qiangguoId;

    private String username;
}
