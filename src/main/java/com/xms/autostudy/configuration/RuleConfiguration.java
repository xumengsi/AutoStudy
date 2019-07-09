package com.xms.autostudy.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * xumengsi
 */
@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "auto.study")
public class RuleConfiguration {

    private Map<String,ScoreConfiguration> rules = new LinkedHashMap<>();

    private Boolean browserEnabled = false;

    private String chromeDriverAddress;
}
