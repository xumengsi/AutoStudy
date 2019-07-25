package com.xms.autostudy.pool;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by xumengsi on 2019-07-24 10:56
 */
@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "auto.study.pool")
public class PoolConfigProperties {

    private int maxTotal = 8;

    private int maxIdle = 8;

    private int minIdle = 1;
}
