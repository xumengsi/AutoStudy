package com.xms.autostudy.pool;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.proxy.JdkProxySource;
import org.apache.commons.pool2.proxy.ProxiedObjectPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



/**
 * Created by xumengsi on 2019-07-24 11:01
 */
@Configuration
@EnableConfigurationProperties({PoolConfigProperties.class})
public class PoolAutoDriverConfiguration {

    @Autowired
    private PoolConfigProperties poolConfigProperties;

    @Bean
    public GenericAutoPool<AutoDriverInterface> getProxiedObjectPool() {
        PoolAutoDriverFactory poolAutoDriverFactory = new PoolAutoDriverFactory();
        GenericAutoDriverPoolConfig genericAutoDriverPoolConfig = new GenericAutoDriverPoolConfig(poolConfigProperties);
        GenericAutoPool genericAutoPool = new GenericAutoPool<>(poolAutoDriverFactory, genericAutoDriverPoolConfig);
        return genericAutoPool;
    }
}
