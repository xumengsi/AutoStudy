package com.xms.autostudy.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * xumengsi
 */
@Configuration
public class RedisConfiguration {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Bean
    public ListOperations<String, String> getListOperations(){
        return redisTemplate.opsForList();
    }
}
