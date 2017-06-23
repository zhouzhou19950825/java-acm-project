package com.upic.acm.dealCode;

import java.util.List;

import com.upic.acm.compiler.CodeCompiler;
import com.upic.acm.compiler.doCompiler.DoCompiler;
import com.upic.acm.data.DealData;
import com.upic.acm.data.InitData;
import com.upic.acm.deal.DealCode;
import com.upic.acm.enums.CompilerEnum;
import com.upic.acm.replace.ReplaceCode;
import com.upic.acm.replace.doReplace.DoReplace;
import com.upic.acm.result.DealResult;
import com.upic.acm.utils.ClassModifier;
import com.upic.acm.utils.UpicJdkCompiler;

/**
 * 核心处理类
 * 
 * @author DTZ
 *
 */
public abstract class AbstraceDealCodeCenter implements DealCode {
	// 测试数据默认为不换行
//	protected boolean newLine = false;
	// 默认是二进制
	protected DealEnum dealEnum = DealEnum.BYTE;
	// 默认是jdk编译
	protected CompilerEnum defaultCompile = CompilerEnum.JDK;

	protected CodeCompiler codeCompiler;

	protected UpicJdkCompiler upicJdkCompiler;
	
	protected ReplaceCode replaceCode;
	public AbstraceDealCodeCenter( CompilerEnum defaultCompile) {
//		this.newLine = newLine == true ? newLine : this.newLine;
		this.defaultCompile = defaultCompile == null ? this.defaultCompile : defaultCompile;
		initCompiler();
	}

	public AbstraceDealCodeCenter() {
		this(CompilerEnum.JDK);
	}

	private void initCompiler() {
		replaceCode=new DoReplace(dealEnum);
		switch (defaultCompile) {
		case JDK:
			upicJdkCompiler = new UpicJdkCompiler();
			codeCompiler = new DoCompiler(upicJdkCompiler,dealEnum);
			
			break;
		case JAVASSIST:
			// 待开发
			break;
		default:
			break;
		}

	}

	@Override
	public DealResult deal(String resouece, DealData dealData) {
		if (codeCompiler == null) {
			throw new NullPointerException("codeCompiler未初始化");
		}
		DealResult dealResult=null;
		switch (dealEnum) {
		case BYTE:
			dealResult=dealByte(resouece,dealData);
			break;
		case CLAZZ:
			dealResult=dealClass(resouece,dealData);
			break;
		default:
			break;
		}
		return dealResult;
	}

	public abstract DealResult dealByte(String resouece, DealData dealData);

	public abstract DealResult dealClass(String resouece, DealData dealData);
}
