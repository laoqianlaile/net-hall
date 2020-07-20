package com.yd.ish.dto;

/**
 * 扫码登录返回信息请求参数
 * 名称：QrCodeLoginInfoDTO.java
 * <p>功能： <br> 
 * @author 张洪超
 * @version 0.1	2017年11月3日	张洪超创建
 */
public class QrCodeLoginInfoDTO {
	/**
	 * 身份证号
	 */
	private String sfzh;
	/**
	 * 姓名
	 */
	private String xingming;
	/**
	 * 登录Key
	 */
	private String loginKey;
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

	public String getSfzh() {
		return sfzh;
	}

	public void setSfzh(String sfzh) {
		this.sfzh = sfzh;
	}

	public String getXingming() {
		return xingming;
	}

	public void setXingming(String xingming) {
		this.xingming = xingming;
	}

	public String getLoginKey() {
		return loginKey;
	}

	public void setLoginKey(String loginKey) {
		this.loginKey = loginKey;
	}

}
