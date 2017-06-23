package com.upic.acm.data;

import java.util.List;
/**
 * 
 * @author DTZ
 *
 */
public class DealData {

	private boolean newLine;
	
	private List<InitData> initData;

	public boolean isNewLine() {
		return newLine;
	}

	public void setNewLine(boolean newLine) {
		this.newLine = newLine;
	}

	public List<InitData> getInitData() {
		return initData;
	}

	public void setInitData(List<InitData> initData) {
		this.initData = initData;
	}

	public DealData(boolean newLine, List<InitData> initData) {
		super();
		this.newLine = newLine;
		this.initData = initData;
	}
	
	
}
