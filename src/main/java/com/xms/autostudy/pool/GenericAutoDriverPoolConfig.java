package com.xms.autostudy.pool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * Created by xumengsi on 2019-07-24 10:52
 */
public class GenericAutoDriverPoolConfig extends GenericObjectPoolConfig<AutoDriverInterface> {


    public GenericAutoDriverPoolConfig(PoolConfigProperties poolConfigProperties) {
        this.setMaxIdle(poolConfigProperties.getMaxIdle());
        this.setMaxTotal(poolConfigProperties.getMaxTotal());
        this.setMinIdle(poolConfigProperties.getMinIdle());
        setJmxEnabled(false);
    }


}
