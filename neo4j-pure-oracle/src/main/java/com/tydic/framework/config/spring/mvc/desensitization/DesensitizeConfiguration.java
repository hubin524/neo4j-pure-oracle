package com.tydic.framework.config.spring.mvc.desensitization;

import com.tydic.framework.spring.mvc.desensitization.processor.DesensitizeProcessor;
import com.tydic.framework.spring.mvc.desensitization.processor.SimpleDesensitizeProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jianghuaishuang
 * @desc
 * @date 2019-08-06 15:24
 */
@Configuration
public class DesensitizeConfiguration {

	@Bean
	public DesensitizeProcessor desensitizeProcessor(){
		return new SimpleDesensitizeProcessor();
	}
}
