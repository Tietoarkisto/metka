package test;


import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AOPTest {

    private AOPBean bean;

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:context.xml");
        bean = context.getBean("bean", AOPBean.class);
    }

    @Test
    public void test() {
        bean.hello();
    }
}
