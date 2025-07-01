package com.example.quizz_b.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.example.quizz_b.service..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();

        try {
            Object result = joinPoint.proceed();
            long end = System.currentTimeMillis();
            System.out.printf("[LOG] %s completed in %d ms%n", methodName, end - start);
            return result;
        } catch (Throwable ex) {
            System.err.printf("[ERROR] %s failed: %s%n", methodName, ex.getMessage());
            throw ex;
        }
    }
}
