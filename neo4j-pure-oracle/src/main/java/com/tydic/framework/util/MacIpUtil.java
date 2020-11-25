package com.tydic.framework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jianghuaishuang
 * @desc
 * @date 2019-08-05 11:30
 */
public class MacIpUtil {

	private static Logger logger = LoggerFactory.getLogger(MacIpUtil.class);
	private static final Map<String, String> CACHE_INFO = new HashMap<>();
	private static final String MAC = "MAC";
	private static final String IPV4 = "IPV4";
	private static final String IPV6 = "IPV6";

	private static final boolean USE_CACHE;

	// 获取mac地址
	public static String getMacAddress() {
		if (USE_CACHE && CACHE_INFO.containsKey(MAC)) {
			return CACHE_INFO.get(MAC);
		}
		try {
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			byte[] mac = null;
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
					continue;
				} else {
					mac = netInterface.getHardwareAddress();
					if (mac != null) {
						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < mac.length; i++) {
							sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
						}
						if (sb.length() > 0) {
							//插入缓存
							if (USE_CACHE) {
								CACHE_INFO.put(MAC, sb.toString());
							}
							return sb.toString();
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("MAC地址获取失败", e);
		}
		return "";
	}

	// 获取ip地址
	private static String getIpAddress(String type) {
		if (USE_CACHE && CACHE_INFO.containsKey(type)) {
			return CACHE_INFO.get(type);
		}
		try {
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
					continue;
				} else {
					Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
					while (addresses.hasMoreElements()) {
						ip = addresses.nextElement();
						if (ip != null && ip instanceof Inet4Address) {
							//无法解决,安装虚拟机，多个网卡的情况
							if (USE_CACHE) {
								CACHE_INFO.put(type, ip.getHostAddress());
							}
							return ip.getHostAddress();
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("IP地址获取失败", e);
		}
		return "";
	}

	public static String getIpv4() {
		return getIpAddress(IPV4);
	}

	public static String getIpv6() {
		return getIpAddress(IPV6);
	}

	static {
		USE_CACHE = true;

		logger.debug("初始化IPV4[" + getIpv4() + "]");
		logger.debug("初始化IPV6[" + getIpv6() + "]");
		logger.debug("初始化MAC[" + getMacAddress() + "]");
	}

}
