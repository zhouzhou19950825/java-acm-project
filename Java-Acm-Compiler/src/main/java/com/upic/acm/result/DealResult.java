package com.upic.acm.result;
/**
 * 返回结果(包括错误返回)
 * @author DTZ
 *
 */
public class DealResult {
	//0 success 1Error
	private int code;
	
	//错误信息
	private String error;
	
	//正确率
	private double success;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public double getSuccess() {
		return success;
	}

	public void setSuccess(double success) {
		this.success = success;
	}
	
}
