package com.tydic.framework.config.log.service;

import com.tydic.framework.config.log.bean.LoggerDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jianghuaishuang
 * @desc 简单记录日志到log4j, logback....
 * @date 2019-06-10 16:38
 */
public class SimpleLoggerServiceImpl extends AbstractLoggerService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final String MSG_SEPARATOR = " |&| ";

	@Override
	public void log(LoggerDto bean) {
		StringBuilder sb = new StringBuilder();
		sb.append(bean.getLogLevel()).append(MSG_SEPARATOR)
				.append(bean.getOptTime()).append(MSG_SEPARATOR)
				.append(bean.getTransactionId()).append(MSG_SEPARATOR)
				.append(bean.getOptUserCode()).append(MSG_SEPARATOR)
				.append(bean.getLatnId()).append(MSG_SEPARATOR)
				.append(bean.getIp()).append(MSG_SEPARATOR)
				.append(bean.getLoggableType()).append(MSG_SEPARATOR)
				.append(bean.getModule()).append(MSG_SEPARATOR)
				.append(bean.getFeatures()).append(MSG_SEPARATOR)
				.append(bean.getOperateObject()).append(MSG_SEPARATOR)
				.append(bean.getParams()).append(MSG_SEPARATOR)
				.append(bean.getResult()).append(MSG_SEPARATOR)
				.append(bean.getServiceCode()).append(MSG_SEPARATOR)
				.append(bean.getServiceIp()).append(MSG_SEPARATOR)
				.append(bean.getErrorStackMsg());

		if (bean.getErrorStackMsg() == null) {
			logger.info(sb.toString());
		} else {
			logger.error(sb.toString());
		}


	}
}
