package com.xms.autostudy.queue;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by xumengsi on 2019-07-10 15:37
 */
@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "auto.study.queue.rule")
public class QueueRule {

    private int maxNumber = 5;

    private int threadNumber = 5;
}
