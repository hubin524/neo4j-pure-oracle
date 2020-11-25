package com.tydic.framework.config.file.util;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author jianghuaishuang
 * @desc
 * @date 2019-06-10 10:30
 */
public class FileNameEncodeUtil {
	private static final String USER_AGENT_KEY = "User-Agent";


	private static final String AGENT_FIREFOX = "firefox";
	private static final String AGENT_IE = "msie";
	private static final String AGENT_CHROME = "chrome";
	private static final String AGENT_SAFARI = "safari";

	private static final String CHARSET_UTF_8 = "UTF-8";
	private static final String CHARSET_ISO8859_1 = "ISO8859-1";

	public static String encode(String fileName, HttpServletRequest request) throws UnsupportedEncodingException {
		return encode(fileName, request.getHeader(USER_AGENT_KEY));
	}

	public static String encode(String fileName, String userAgent) throws UnsupportedEncodingException {
		if(userAgent == null){
			return fileName;
		}
		userAgent = userAgent.toLowerCase();
		if (userAgent.indexOf(AGENT_FIREFOX) > 0) {
			fileName = new String(fileName.getBytes(CHARSET_UTF_8), CHARSET_ISO8859_1);// firefox浏览器
		} else if (userAgent.indexOf(AGENT_IE) > 0) {
			fileName = URLEncoder.encode(fileName, CHARSET_UTF_8);// IE浏览器
		} else if (userAgent.indexOf(AGENT_CHROME) > 0 || userAgent.toLowerCase().indexOf(AGENT_SAFARI) > 0) {
			fileName = new String(fileName.getBytes(CHARSET_UTF_8), CHARSET_ISO8859_1);// firefox浏览器
		} else {
			fileName = URLEncoder.encode(fileName, CHARSET_UTF_8);
		}
		return fileName;
	}

}
