package com.tydic.framework.config.log.service;

import com.tydic.framework.config.log.bean.LoggerDto;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.scheduling.annotation.Async;

public interface LoggerService {
	/**
	 *
	 * @param point
	 * @param target
	 * @param result
	 * @param errorMsg
	 */
	void processLog(ProceedingJoinPoint point, Object target, Object result, String errorMsg);


	/**
	 *
	 * @param logType
	 * @param logModule
	 * @param logFeatures
	 * @param clazzName
	 * @param methodName
	 * @param params
	 * @param result
	 * @param errorStackMsg
	 */
	void log(String logType, String logModule, String logFeatures, String clazzName, String methodName, Object[] params, Object result, String errorStackMsg);

	/**
	 *
	 * @param bean
	 */
	@Async
	void log(LoggerDto bean);
}
