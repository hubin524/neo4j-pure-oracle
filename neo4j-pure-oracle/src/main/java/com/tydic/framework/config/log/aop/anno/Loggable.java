package com.tydic.framework.config.log.aop.anno;

import com.tydic.framework.config.log.aop.enums.LoggableTypeEnum;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Loggable {
	LoggableTypeEnum type() default LoggableTypeEnum.VISIT;

	//模块名称
	String module() default "";

	//功能名称
	String features();
}
