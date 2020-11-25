package com.tydic.framework.config.spring.sercurity;

import com.tydic.framework.spring.security.DefaultMethodSecurityConfig;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

/**
 * @author jianghuaishuang
 * @desc 开启方法级别的拦截：如@PreAuthorize,@PostAuthorize等注解都需要@EnableGlobalMethodSecurity来支持.
 * @date 2019-07-25 16:53
 */
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MyMethodSecurityConfig extends DefaultMethodSecurityConfig {

}
