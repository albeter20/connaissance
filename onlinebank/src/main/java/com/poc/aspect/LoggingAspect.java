package com.poc.aspect;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.poc.oas.api.UserAPI;

@Aspect
@Component
public class LoggingAspect {
	private static final Logger logger = Logger
			.getLogger(LoggingAspect.class);
	@Pointcut("execution(* com.poc.oas.*.create*(..))")
	public void logBefore(JoinPoint joinPoint) {

		logger.info("logBefore() is running!");
		logger.info("hijacked : " + joinPoint.getSignature().getName());
		logger.info("******************************************************");
	}
}
