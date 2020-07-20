package com.yd.ish.dto;

import java.util.HashMap;

/**
 * 名称：LoginMap.java
 * <p>功能：扫码登录用户数据静态池 <br> 
 * @author 张洪超
 * @version 0.1	2018年4月3日	张洪超创建
 */
public class LoginMap {
	/**
	 * 用户登录信息静态池
	 */
	private static  HashMap<String, QrCodeLoginInfoDTO> loginInfoMap = new HashMap<String, QrCodeLoginInfoDTO>();
	/**
	 * 用户登录状态静态池
	 */
	private static  HashMap<String, QrCodeLoginStatusDTO> loginStatusMap = new HashMap<String, QrCodeLoginStatusDTO>();
	public static HashMap<String, QrCodeLoginInfoDTO> getLoginInfoMap() {
		return loginInfoMap;
	}
	public static void setLoginInfoMap(HashMap<String, QrCodeLoginInfoDTO> loginInfoMap) {
		LoginMap.loginInfoMap = loginInfoMap;
	}
	public static HashMap<String, QrCodeLoginStatusDTO> getLoginStatusMap() {
		return loginStatusMap;
	}
	public static void setLoginStatusMap(HashMap<String, QrCodeLoginStatusDTO> loginStatusMap) {
		LoginMap.loginStatusMap = loginStatusMap;
	}

}
