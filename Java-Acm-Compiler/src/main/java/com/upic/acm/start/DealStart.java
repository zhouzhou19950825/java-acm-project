package com.upic.acm.start;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;

import com.upic.acm.data.DealData;
import com.upic.acm.deal.DealCode;
import com.upic.acm.dealCode.support.DealCodeCenter;
import com.upic.acm.enums.CompilerEnum;
import com.upic.acm.result.DealResult;
/**
 * 
 * @author DTZ
 *
 */
public class DealStart {
	// false表示此类名可用
	protected static final ConcurrentHashMap<String, Boolean> EVENT = new ConcurrentHashMap<String, Boolean>();
	private static final int LENGTH = 25;
	private static final String CLASSNAME = "Main";
	private static Object obj=new Object();
	static {
		for (int i = 0; i < LENGTH; i++) {
			EVENT.put(CLASSNAME + i, false);
		}
	}
	
	public static DealResult getResult( CompilerEnum defaultCompile, DealData dealData,String resource){
		DealCode dealCode=DealCodeCenter.getDealCodeCenter(defaultCompile);
		return dealCode.deal(resource,dealData);
	}
	//存在问题，如果并发太大，25个的空间是不够的，需要合理扩容，又能收缩
	public static String getCanUseName() {
		String useName=null;
		synchronized (obj) {
			KeySetView<String, Boolean> keySet = EVENT.keySet();
			for(String s:keySet){
				if(EVENT.get(s)){
					continue;
				}
				EVENT.put(s, true);
				useName=s;
				break;
			}
		}
		return useName;
	}

	public static boolean getClassNameStatus(String name) {
		return EVENT.get(name);
	}

	public static boolean reSetting(String className) {
		return EVENT.replace(className, false);
	}
}
