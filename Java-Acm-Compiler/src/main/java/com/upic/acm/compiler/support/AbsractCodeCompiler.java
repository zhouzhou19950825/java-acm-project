package com.upic.acm.compiler.support;

import com.upic.acm.compiler.CodeCompiler;
import com.upic.acm.deal.DealCode.DealEnum;
import com.upic.acm.start.DealStart;
/**
 * 
 * @author DTZ
 *
 */
public abstract class AbsractCodeCompiler implements CodeCompiler {

	// 默认返回byte[]
	public DealEnum compilerEnum = DealEnum.BYTE;

	public AbsractCodeCompiler(DealEnum compilerEnum) {
		this.compilerEnum = compilerEnum;
	}

	public AbsractCodeCompiler() {
	}

	@Override
	public Object compile(String resource,String className) {
		// 检查和替换部分代码
		return compilerEnum == DealEnum.BYTE ? returnByte(dealWithCode(resource,className),className) : returnClass(dealWithCode(resource,className),className);
	}

	//并不完善，主要是把输入这一块替换
	protected String dealWithCode(String resource,String canUseName) {
		String replaceAll=resource.replaceAll("Main",canUseName );
		replaceAll = replaceAll.replaceAll("System.in", "getResult()");
		String substring = replaceAll.substring(0, replaceAll.length() - 2);
		StringBuffer sb = new StringBuffer(substring);
		sb.append("public static String getResult(){return testData;}");
		sb.append("public static String testData;");
		sb.append("}");
		return sb.toString();
	}

	public abstract Class<?> returnClass(String resource,String className);

	public abstract byte[] returnByte(String resource,String className);

}
