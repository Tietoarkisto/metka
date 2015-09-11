package test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CacheTests {

    private CacheService service;

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:context.xml");
        service = context.getBean("cacheService", CacheService.class);
    }

    @Test
    public void testGet() {
        service.getBean("test");
        service.getBean("test");
    }

    @Test
    public void evictTest() {
        CacheTestBean bean = service.getBean("evictTest");
        System.out.println("Value is "+bean.getValue()+".");
        CacheTestBean newBean = new CacheTestBean("evictTest", 100);
        service.insertBean(newBean);
        bean = service.getBean("evictTest");
        System.out.println("Value is "+bean.getValue()+". Should be "+newBean.getValue()+".");
    }

}
