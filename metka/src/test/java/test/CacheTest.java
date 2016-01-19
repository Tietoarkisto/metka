package test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:testContext.xml" })
public class CacheTest {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    CacheService cacheService;

    @Before
    public void init() {
        cacheService.dismissCache();
    }

    @Test
    public void testGet() {
        cacheService.getBean("test");
        cacheService.getBean("test");
        cacheService.getBean("test");

        Assert.assertEquals(cacheService.getCachedMethodCallCount(), 1);
    }

    @Test
    public void evictTest() {
        CacheTestBean bean = cacheService.getBean("evictTest");
        System.out.println("Value is "+bean.getValue()+".");
        CacheTestBean newBean = new CacheTestBean("evictTest", 100);
        cacheService.insertBean(newBean);
        bean = cacheService.getBean("evictTest");
        System.out.println("Value is "+bean.getValue()+". Should be "+newBean.getValue()+".");
        Assert.assertEquals(bean.getValue(),newBean.getValue());
        Assert.assertEquals(cacheService.getCachedMethodCallCount(), 2);
    }

    @Test
    public void evictChainTest() {
        CacheTestBean bean = cacheService.getBean("evictTest");
        System.out.println("Value is "+bean.getValue()+".");
        CacheTestBean newBean = new CacheTestBean("evictTest", 200);
        //chained method does not evict cache
        cacheService.insertBeanChain(newBean);
        //getBean-method returns cached value.
        bean = cacheService.getBean("evictTest");
        System.out.println("Cached value is "+bean.getValue()+". Correct value would be "+newBean.getValue()+".");
        //because chained method does not evict cache, method is actually only called once
        //and values are different.
        Assert.assertNotSame(bean.getValue(),newBean.getValue());
        Assert.assertEquals(cacheService.getCachedMethodCallCount(), 1);
    }

}
