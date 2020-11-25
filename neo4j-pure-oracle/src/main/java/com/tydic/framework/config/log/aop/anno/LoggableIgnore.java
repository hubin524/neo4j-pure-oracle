package com.tydic.framework.config.log.aop.anno;

import java.lang.annotation.*;

/**
 * @author jianghuaishuang
 * @desc 当需要记录日志的字段过长时，选用此注解可以忽略记录
 * 该注解忽略记录入参,返回值，以及方法 ;
 *
 * @date 2019-07-02 17:23
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER,ElementType.METHOD})
public @interface LoggableIgnore {
	/**
	 * 加上该注解后，记录的内容被替换为value();
	 * @return
	 */
	String value() default "";

	/**
	 * 当注解在方法上时
	 *  如果methodIgnore == false, 表示仅忽略返回值，即记录日志时不记录返回值(如附件下载接口,无需记录的附件二进制流)
	 *  若 methodIgnore == true， 表示忽略方法,即该方法不记录进日志
	 * @return
	 */
	boolean methodIgnore() default false;
}
