package com.tydic.framework.config.spring.mvc.handler;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author jianghuaishuang
 * @desc
 * @date 2019-08-02 17:14
 */
@ControllerAdvice
public class AdapterController {

	/**
	 * 全局的 解决日期参数绑定 问题
	 * 也可以在属性字段上增加： @DateTimeFormat(pattern="yyyy-MM-dd") 设置;
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"),false));
	}
}
