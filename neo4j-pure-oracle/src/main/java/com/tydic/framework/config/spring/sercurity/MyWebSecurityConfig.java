package com.tydic.framework.config.spring.sercurity;

import com.tydic.framework.spring.security.DefaultWebSecurityConfig;
import com.tydic.framework.spring.security.oauth2.code.Parameter2HeaderAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * @author jianghuaishuang
 * @desc spring-security 全局配置。
 * @date 2019-07-25 16:52
 */
@EnableWebSecurity
public class MyWebSecurityConfig extends DefaultWebSecurityConfig {

	/**
	 * 添加不需要经过拦截的urls
	 * 开放swagger-api页面,仅开放dev,test环境
	 * @param web
	 * @throws Exception
	 */
	@Profile({"dev", "test"})
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/swagger-ui.html", "/swagger-resources/**",
				"/v2/api-docs", "/webjars/springfox-swagger-ui/**", "/error");
	}

	/**
	 * 拦截使用参数形式传递jwtToken的请求（如附件下载请求）
	 * @return
	 */
	@Override
	public Parameter2HeaderAuthenticationFilter parameter2HeaderAuthenticationFilter() {
		Parameter2HeaderAuthenticationFilter parameter2HeaderAuthenticationFilter = new Parameter2HeaderAuthenticationFilter(new String[]{"/common/attachment/download/*"});
		return parameter2HeaderAuthenticationFilter;
	}
}
