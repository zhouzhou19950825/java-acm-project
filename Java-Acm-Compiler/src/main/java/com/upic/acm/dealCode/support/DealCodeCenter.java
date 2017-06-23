package com.upic.acm.dealCode.support;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Pattern;

import com.upic.acm.System.HackSystem;
import com.upic.acm.data.DealData;
import com.upic.acm.data.InitData;
import com.upic.acm.dealCode.AbstraceDealCodeCenter;
import com.upic.acm.enums.CompilerEnum;
import com.upic.acm.result.DealResult;
import com.upic.acm.start.DealStart;
import com.upic.classLoader.UpicClassLoader;
/**
 * 
 * @author DTZ
 *
 */
public class DealCodeCenter extends AbstraceDealCodeCenter {

	private static final Pattern DELETE_SPACEBAR = Pattern.compile("\\s{1,}");
	
	private static final Pattern DELETE_SPACEBAR_SECOND = Pattern.compile("\\s{2,}");
	
	private static final String RENEWLINE = "\r\n";
	
	private static final String LINE = " ";

	private static final String TESTDATA = "testData";
	
	private static final Object obj=new Object();

	private static DealCodeCenter dealCodeCenter;

	private DealCodeCenter( CompilerEnum defaultCompile) {
		super( defaultCompile);
	}
	public static DealCodeCenter getDealCodeCenter(CompilerEnum defaultCompile){
		if(dealCodeCenter==null){
			synchronized (obj) {
				if(dealCodeCenter==null){
					dealCodeCenter=new DealCodeCenter( defaultCompile);
				}
			}
		}
		return dealCodeCenter;
	}

	@Override
	public DealResult dealByte(String resouece, DealData dealData) {

		DealResult deal = new DealResult();
		try {
			if (dealData.getInitData().isEmpty()) {
				throw new NullPointerException("测试数据为空");
			}
			//类名替换
			String canUseName = DealStart.getCanUseName();
			System.out.println(canUseName);
			byte[] compile = (byte[]) codeCompiler.compile(resouece,canUseName);
			if (compile == null) {
				throw new NullPointerException("编译错误");
			}
			// 替换代码 败笔（需要修改）
			byte[] compileReplace = (byte[]) replaceCode.replace(compile);
			// 获得可以测试的类了
			Class<?> resultClass = doCompilerByByte(compileReplace,canUseName);
			if (resultClass == null) {
				throw new NullPointerException("编译错误");
			}
			// 对数据进行评分、卸载类、返回结果
			return dealResult0(resultClass,canUseName,dealData,deal);
		} catch (Exception e) {
			deal.setCode(1);
			deal.setError(e.getMessage());
			return deal;
		}
	}

	private DealResult dealResult0(Class<?> resultClass, String canUseName,DealData dealData,DealResult deal) throws Exception {
		checkDate(dealData);
		return doRevision(resultClass,canUseName,dealData.getInitData(),deal);
	}

	// 做计算处理
	private DealResult doRevision(Class<?> resultClass, String canUseName,List<InitData> initData,DealResult deal) throws Exception {
			double successNum=0;
			double accuracy = doAccuracy(resultClass,canUseName,initData,successNum);
			doCalculate(accuracy,initData,deal);
		return deal;
	}

	private void doCalculate(double accuracy, List<InitData> initData,DealResult deal) {
		deal.setSuccess(accuracy / initData.size());
		deal.setCode(0);
	}

	//启动计算
	private double doAccuracy(Class<?> resultClass,String canUseName,List<InitData> initData,double successNum) throws Exception {
		Method method;
		Field f;
		
		for (InitData i : initData) {
			f = resultClass.getField(TESTDATA);
			f.set(String.class, i.getInitData());
			method = resultClass.getMethod("main", new Class[] { String[].class });
			method.invoke(null, new String[] { null });
			if (HackSystem.getBufferString().equals(i.getResultData())) {
				successNum++;
			}
			HackSystem.clearBuffer();
		}
		DealStart.reSetting(canUseName);
		return successNum;
	}

	private void checkDate(DealData dealData) {
		if (dealData.isNewLine()) {
			dealNewLine(DELETE_SPACEBAR.pattern(), RENEWLINE,dealData.getInitData());
		} else {
			dealNewLine(DELETE_SPACEBAR_SECOND.pattern(), LINE,dealData.getInitData());
		}
	}

	//对数据的整理
	private void dealNewLine(String regex, String appnedWhat,List<InitData> initData) {
		for (int i = 0; i < initData.size(); i++) {
			String[] split = initData.get(i).getInitData().split(regex);
			StringBuffer sb = new StringBuffer();
			InitData replace = null;
			for (int j = 0; j < split.length; j++) {
				if (j == split.length - 1) {
					sb.append(split[j]);
					continue;
				}
				sb.append(split[j]).append(appnedWhat);
			}
			replace = initData.get(i).replace(sb.toString(), initData.get(i).getResultData());
			initData.set(i, replace);
		}
	}

	private Class<?> doCompilerByByte(byte[] compileReplace,String name) {
		return UpicClassLoader.getUpicClassLoader().loadByte(compileReplace,true,name);
	}
	@Override
	public DealResult dealClass(String resouece,DealData dealData) {
		return null;
	}

}
