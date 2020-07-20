package com.yd.ish.dto;

/**
 * 扫码登录返回状态请求参数
 * 名称：QrCodeLoginStatusDTO.java
 * <p>功能： <br> 
 * @author 张洪超
 * @version 0.1	2017年11月3日	张洪超创建
 */
public class QrCodeLoginStatusDTO {
	/**
	 * 登录Key
	 */
	private String loginKey;
	/**
	 * 登录状态 0 确认登录 1 不登录
	 */
	private String loginStatus;
	/**
	 * 返回标志
	 */
	private String returnCode;
	/**
	 * 返回信息
	 */
	private String message;
	
	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getLoginKey() {
		return loginKey;
	}

	public void setLoginKey(String loginKey) {
		this.loginKey = loginKey;
	}

	public String getLoginStatus() {
		return loginStatus;
	}

	public void setLoginStatus(String loginStatus) {
		this.loginStatus = loginStatus;
	}

}
