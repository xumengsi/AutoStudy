package com.xms.autostudy.configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * xumengsi
 */
@Getter
@Setter
public class ScoreConfiguration {

    /**
     * 有效次数或者分钟
     */
    private int numbers;

    /**
     * 有效学习的得分（一次多少分或者满足多少分钟的多少分）
     */
    private int score;

    /**
     * 时间 （针对时长对分）
     */
    private int time;

    /**
     * 有效学习的上限总分
     */
    private int totalNumber;

}
