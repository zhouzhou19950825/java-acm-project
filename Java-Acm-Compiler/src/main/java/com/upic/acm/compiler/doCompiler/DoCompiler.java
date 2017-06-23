package com.upic.acm.compiler.doCompiler;

import com.upic.acm.compiler.support.AbsractCodeCompiler;
import com.upic.acm.deal.DealCode.DealEnum;
import com.upic.acm.utils.UpicJdkCompiler;
/**
 * 
 * @author DTZ
 *
 */
public class DoCompiler extends AbsractCodeCompiler{

	private UpicJdkCompiler upicJdkCompiler;
	
	public DoCompiler(UpicJdkCompiler upicJdkCompiler,DealEnum compilerEnum) {
		
		this.upicJdkCompiler=upicJdkCompiler;
	}
	

	@Override
	public Class<?> returnClass(String resource,String className)  {
		try {
			return (Class<?>) upicJdkCompiler.doCompile(className, resource, compilerEnum);
		} catch (Throwable e) {
			return null;
		}
	}

	@Override
	public byte[] returnByte(String resource,String className) {
		try {
			return (byte[]) upicJdkCompiler.doCompile(className, resource, compilerEnum);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

}
