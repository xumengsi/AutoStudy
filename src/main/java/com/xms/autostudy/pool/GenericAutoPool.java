package com.xms.autostudy.pool;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * Created by xumengsi on 2019-07-24 17:31
 */
public class GenericAutoPool<AutoDriverInterface> extends GenericObjectPool<AutoDriverInterface> {
    /**
     * Creates a new <code>GenericObjectPool</code> using defaults from
     * {@link GenericObjectPoolConfig}.
     *
     * @param factory The object factory to be used to create object instances
     *                used by this pool
     */
    public GenericAutoPool(PoolAutoDriverFactory factory) {
        super((PooledObjectFactory<AutoDriverInterface>) factory);
    }

    /**
     *
     * @param factory
     * @param genericAutoDriverPoolConfig
     */
    public GenericAutoPool(PoolAutoDriverFactory factory, GenericAutoDriverPoolConfig genericAutoDriverPoolConfig) {
        super((PooledObjectFactory<AutoDriverInterface>)factory, (GenericObjectPoolConfig<AutoDriverInterface>)genericAutoDriverPoolConfig);
    }
}
