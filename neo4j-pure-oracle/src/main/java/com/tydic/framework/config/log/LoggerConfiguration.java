package com.tydic.framework.config.log;

import com.tydic.framework.config.log.bean.LoggerDto;
import com.tydic.framework.config.log.service.AbstractLoggerService;
import com.tydic.framework.config.log.service.LoggerService;
import com.tydic.framework.config.log.service.SimpleLoggerServiceImpl;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jianghuaishuang
 * @desc
 * @date 2019-06-10 18:59
 */
@Configuration
public class LoggerConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public LoggerService gecLoggerService() {
		return new NoneLoggerServiceImpl();
	}

	private static class NoneLoggerServiceImpl extends AbstractLoggerService {

		@Override
		public void processLog(ProceedingJoinPoint point, Object target, Object result, String errorMsg) {
		  	//do nothing .
		}

		@Override
		public void log(LoggerDto bean) {

		}
	}
}
