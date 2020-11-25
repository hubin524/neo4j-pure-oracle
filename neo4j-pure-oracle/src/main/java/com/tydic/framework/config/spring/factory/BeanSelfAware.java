package com.tydic.framework.config.spring.factory;

/**
 * @author jianghuaishuang
 * @desc 避免事务增强丢失
 * @date 2019/5/27 15:24
 */
public interface BeanSelfAware<T> {
	/**
	 * 设置proxy
	 * @param proxy
	 */
	void setSelf(T proxy);
}
