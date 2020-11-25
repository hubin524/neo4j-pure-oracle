package com.tydic.framework.config.log.aop;

import com.tydic.framework.config.log.aop.anno.Loggable;
import com.tydic.framework.config.log.service.LoggerService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * @author jianghuaishuang
 * @desc
 * @date 2019-06-10 16:05
 */
@Aspect
@Component
public class LoggableAspect {


	private Set<LoggerService> loggerServices;

	@Autowired(required = false)
	public void setLoggerServices(Set<LoggerService> loggerServices) {
		this.loggerServices = loggerServices;
	}


	/**
	 * 针对带有@Loggable 注解的方法进行拦截
	 *
	 * @param point
	 * @param loggable
	 * @return
	 */
	@Around("@annotation(loggable)")
	public Object around(ProceedingJoinPoint point, Loggable loggable) throws Throwable {
		return processAround(point, loggable);
	}

	/**
	 * 针对@RestController 的所有方法进行拦截
	 *
	 * @param point
	 * @param controller
	 * @return
	 * @throws Throwable
	 */
	@Around("@within(controller)")
	public Object around(ProceedingJoinPoint point, RestController controller) throws Throwable {
		return processAround(point, controller);
	}

	private Object processAround(ProceedingJoinPoint point, Object target) throws Throwable {
		Object result;
		try {
			result = point.proceed();
			for (LoggerService loggerService : loggerServices) {
				loggerService.processLog(point, target, result, null);
			}
		} catch (Throwable throwable) {
			for (LoggerService loggerService : loggerServices) {
				loggerService.processLog(point, target, null, throwable.getMessage());
			}
			throw throwable;
		}
		return result;
	}
}
