package com.upic.acm.deal;

import java.util.List;

import com.upic.acm.data.DealData;
import com.upic.acm.data.InitData;
import com.upic.acm.result.DealResult;
/**
 * 
 * @author DTZ
 *
 */
public interface DealCode {
	public enum DealEnum {

		BYTE,CLAZZ;
	}
//	DealResult deal(String resource);
	public DealResult deal(String resouece,DealData dealData);
}
