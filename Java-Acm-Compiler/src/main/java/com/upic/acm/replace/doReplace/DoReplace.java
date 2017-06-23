package com.upic.acm.replace.doReplace;

import com.upic.acm.deal.DealCode.DealEnum;
import com.upic.acm.replace.support.AbstractReplaceCode;
import com.upic.acm.utils.ClassModifier;

/**
 * 将输出替换
 * @author DTZ
 *
 */
public class DoReplace extends AbstractReplaceCode{
    private final String oldStr="java/lang/System";
    private final String newStr="com/upic/acm/System/HackSystem";
	public DoReplace(DealEnum compilerEnum) {
		super(compilerEnum);
	}

	@Override
	public byte[] replaceByte(byte[] code) {
		ClassModifier c=new ClassModifier(code);
		return c.modifyUTF8Constant(oldStr, newStr);
	}

	@Override
	public Class<?> replaceClass(byte[] code) {
		return null;
	}

}
