package test;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class AOPAspect {

    @Before("execution(* test.AOPBean.hello())")
    public void aop() {
        System.out.println("AOP");
    }
}
