package com.pennant.util;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class SimpleLoggingAspect {
	
    
	//@Before("execution(* com.pennant*..*(..))")
    public void logBefore(JoinPoint joinPoint) {
		System.out.println("logBefore() is running!");
		System.out.println("hijacked : " + joinPoint.getClass().getName() 
				+ "." + joinPoint.getSignature().getName());
		System.out.println("******");
    }
    
	//@After("execution(* com.pennant*..*(..))")
    public void logAfter(JoinPoint joinPoint) {
		System.out.println("logAfter() is running!");
		System.out.println("hijacked : " + joinPoint.getSignature().getName());
		System.out.println("******");
    }
	
	//@Around("execution(* com.pennant*..*(..))")
    public Object logAround(ProceedingJoinPoint  joinPoint) throws Throwable {
		System.out.println("logAround() is running!");
		System.out.println("hijacked : " + joinPoint.getSignature().getName() + "()");
		System.out.println("******");
		return joinPoint.proceed();
    }

	@AfterThrowing(pointcut="execution(public com.pennant*.backend..*)", throwing = "exception")
    public void logException(JoinPoint joinPoint, Exception exception) {
		System.out.println("logException is running!");
		System.out.println("Method : " + joinPoint.getSignature().getName() 
				+ ", Exception : " + exception.getMessage());
		//exception.printStackTrace();
		System.out.println("******");
    }
}