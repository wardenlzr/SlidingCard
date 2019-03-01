package com.bg.slidingcard.aop_test;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Created by yubin on 2019/3/1 11:46
 */
@Aspect
public class TestAnnoAspect {
    @Pointcut("execution(* com.bg.slidingcard.MainActivity.test(..))")
    public void pointcut() {

    }

    /*@Before("pointcut()")
    public void before(JoinPoint point) {
        System.out.println("@Before");
    }*/

    @Around("pointcut()")
    public void around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("@Around");
        joinPoint.proceed();// 目标方法执行完毕
    }
}
