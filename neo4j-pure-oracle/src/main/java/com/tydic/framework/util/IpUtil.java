package com.tydic.framework.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jianghuaishuang
 * @desc
 * @date 2019-08-05 11:27
 */
public class IpUtil {

	private static final String UNKNOWN = "unknown";
	private IpUtil() {
		throw new AssertionError("Instantiation prohibited.");
	}

	public static String getRemoteHost() {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if(servletRequestAttributes == null){
			return null;
		}
		return getRemoteHost(servletRequestAttributes.getRequest());
	}

	public static String getRemoteHost(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}

		if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}

		if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		return ip;
	}

}
