package com.xms.autostudy.pool;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Created by xumengsi on 2019-07-24 10:36
 */
public class PoolAutoDriverFactory implements PooledObjectFactory<AutoDriverInterface> {

    static {
        System.setProperty("webdriver.chrome.driver", "/Users/xumengsi/chromedriver");
    }

    /**
     * Creates an instance that can be served by the pool and wrap it in a
     * {@link PooledObject} to be managed by the pool.
     *
     * @return a {@code PooledObject} wrapping an instance that can be served by the pool
     * @throws Exception if there is a problem creating a new instance,
     *                   this will be propagated to the code requesting an object.
     */
    @Override
    public PooledObject<AutoDriverInterface> makeObject() throws Exception {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setHeadless(Boolean.FALSE);
        WebDriver webDriver = new ChromeDriver(chromeOptions);
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) webDriver;
        AutoDriverInterface autoDriver = new AutoDriver(webDriver, javascriptExecutor);
        return new DefaultPooledObject<>(autoDriver);
    }

    /**
     * Destroys an instance no longer needed by the pool.
     * <p>
     * It is important for implementations of this method to be aware that there
     * is no guarantee about what state <code>obj</code> will be in and the
     * implementation should be prepared to handle unexpected errors.
     * </p>
     * <p>
     * Also, an implementation must take in to consideration that instances lost
     * to the garbage collector may never be destroyed.
     * </p>
     *
     * @param pooledObject a {@code PooledObject} wrapping the instance to be destroyed
     * @throws Exception should be avoided as it may be swallowed by
     *                   the pool implementation.
     * @see #validateObject
     */
    @Override
    public void destroyObject(PooledObject<AutoDriverInterface> pooledObject) throws Exception {
        AutoDriverInterface autoDriver = pooledObject.getObject();
        autoDriver.getWebDriver().quit();
    }

    /**
     * Ensures that the instance is safe to be returned by the pool.
     *
     * @param p a {@code PooledObject} wrapping the instance to be validated
     * @return <code>false</code> if <code>obj</code> is not valid and should
     * be dropped from the pool, <code>true</code> otherwise.
     */
    @Override
    public boolean validateObject(PooledObject<AutoDriverInterface> p) {
        return false;
    }

    /**
     * Reinitializes an instance to be returned by the pool.
     *
     * @param p a {@code PooledObject} wrapping the instance to be activated
     * @throws Exception if there is a problem activating <code>obj</code>,
     *                   this exception may be swallowed by the pool.
     * @see #destroyObject
     */
    @Override
    public void activateObject(PooledObject<AutoDriverInterface> p) throws Exception {

    }

    /**
     * Uninitializes an instance to be returned to the idle object pool.
     *
     * @param pooledObject a {@code PooledObject} wrapping the instance to be passivated
     * @throws Exception if there is a problem passivating <code>obj</code>,
     *                   this exception may be swallowed by the pool.
     * @see #destroyObject
     */
    @Override
    public void passivateObject(PooledObject<AutoDriverInterface> pooledObject) throws Exception {
        WebDriver webDriver = pooledObject.getObject().getWebDriver();
        webDriver.manage().deleteAllCookies();
        int windowSize = webDriver.getWindowHandles().size();
        if(windowSize > 1){
            for (int window = 0; window < windowSize; window++) {
                webDriver.close();
            }
        }
    }
}
