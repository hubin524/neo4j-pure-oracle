package com.tydic.framework.config.log.bean;

import lombok.Data;

import java.io.Serializable;


@Data
public class LoggerDto implements Serializable {
	private static final long serialVersionUID = -1L;
	private String logLevel; //日志级别
	private String optTime; //操作时间
	private String transactionId; //事物流水标识
	private String optUserCode; //当前登录人code
	private String latnId;    //本地网
	private String ip;//IP地址
	private String loggableType;
	private String module; //模块名称
	private String features; //业务方法名称
	private String operateObject; //操作对象： 类全路径+方法签名

	private String params; //入参
	private String result; //出参(结果)

	private String serviceCode; //系统编码
	private String serviceIp;//服务器端IP地址

	private String errorStackMsg; //异常堆栈信息


}
