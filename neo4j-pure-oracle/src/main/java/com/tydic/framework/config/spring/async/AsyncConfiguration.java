package com.tydic.framework.config.spring.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author jianghuaishuang
 * @desc
 * @date 2019-08-05 16:12
 */
@Configuration
public class AsyncConfiguration {

	/**
	 * 自定义线程池
	 * @return
	 */
	@Bean
	public AsyncTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setThreadNamePrefix("Async-Executor");
		threadPoolTaskExecutor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
		threadPoolTaskExecutor.setMaxPoolSize(100);
		threadPoolTaskExecutor.setQueueCapacity(200);

		// 使用预定义的异常处理类
		// executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

		return threadPoolTaskExecutor;
	}
}
