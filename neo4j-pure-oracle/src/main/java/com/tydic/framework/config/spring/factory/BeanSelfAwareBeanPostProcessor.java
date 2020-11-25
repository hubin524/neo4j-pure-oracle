package com.tydic.framework.config.spring.factory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @author jianghuaishuang
 * @desc
 * @date 2019/5/27 15:27
 */
@Component
public class BeanSelfAwareBeanPostProcessor implements BeanPostProcessor {

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof BeanSelfAware){
			BeanSelfAware beanSelfAware = (BeanSelfAware)bean;
			beanSelfAware.setSelf(bean);
			return beanSelfAware;
		}
		return  bean;
	}

}
