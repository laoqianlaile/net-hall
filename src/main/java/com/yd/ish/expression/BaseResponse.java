package com.yd.ish.expression;

public class BaseResponse {
	/**
	 * 返回编号 0成功1失败
	 */
	private String returnCode;
	/**
	 * 返回信息
	 */
	private String message;

	public BaseResponse() {
		this.returnCode = "1";
		this.message = "失败";
	}
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

}
