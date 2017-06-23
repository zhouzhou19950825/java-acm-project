package com.upic.acm.data;
/**
 * 
 * @author DTZ
 *
 */
//一组数据
public final class InitData {

	private final String initData;

	private final String resultData;

	public InitData(String initData, String resultData) {
		super();
		this.initData = initData;
		this.resultData = resultData;
	}

	public String getInitData() {
		return initData;
	}

	public String getResultData() {
		return resultData;
	}

	public InitData replace(String initData, String resultData) {
		return new InitData(initData, resultData);
	}

}
