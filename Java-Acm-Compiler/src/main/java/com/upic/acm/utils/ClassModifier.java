package com.upic.acm.utils;

/**
 * 修改Class文件,暂时只提供修改常量池常量的功能
 * 
 * @author DTZ
 */
public class ClassModifier {
	/**
	 * Class文件中常量池的起始偏移
	 *  前四个字节为魔术 
	 *  五六字节为副版本号 
	 *  七八字节为住版本号 
	 *  从第九位开始常量计数器
	 *  常量池计数器是从1开始计数的，而不是从0开始的 如果常量池计数器值为22 则后面的常量池项(cp_info)的个数为21
	 */
	private static final int CONSTANT_POOL_COUNT_INDEX = 8;
	/**
	 * CONSTANT_Utf8_info常量的tag标志
	 */
	private static final int CONSTANT_Utf8_info = 1;
	/**
	 * 常量池中11种常量所占的长度,CONSTANT_Utf8_info型常量除外,因为它不是定长的
	 */
	private static final int[] CONSTANT_ITEM_LENGTH = { -1, -1, -1, 5, 5, 9, 9, 3, 3, 5, 5, 5, 5 };
	private static final int u1 = 1;
	private static final int u2 = 2;
	private byte[] classByte;

	public ClassModifier(byte[] classByte) {
		this.classByte = classByte;
	}

	/**
	 * 修改常量池中CONSTANT_Utf8_info常量的内容
	 * 
	 * @param oldStr修改前的字符串
	 * @param newStr修改后的字符串
	 * @return修改结果
	 */
	public byte[] modifyUTF8Constant(String oldStr, String newStr) {
		//获取常量池中常量的数量 因为常量池是从1开始计数 所以长度-1为真实长度
		int cpc = getConstantPoolCount();
		//Class文件中常量池的起始偏移  
		int offset = CONSTANT_POOL_COUNT_INDEX + u2;
		for (int i = 0; i < cpc; i++) {
			int tag = ByteUtils.bytes2Int(classByte, offset, u1);
			if (tag == CONSTANT_Utf8_info) {
				int len = ByteUtils.bytes2Int(classByte, offset + u1, u2);
				offset += (u1 + u2);
				String str = ByteUtils.bytes2String(classByte, offset, len);
				if (str.equalsIgnoreCase(oldStr)) {
					byte[] strBytes = ByteUtils.string2Bytes(newStr);
					byte[] strLen = ByteUtils.int2Bytes(newStr.length(), u2);
					classByte = ByteUtils.bytesReplace(classByte, offset - u2, u2, strLen);
					classByte = ByteUtils.bytesReplace(classByte, offset, len, strBytes);
					return classByte;
				} else {
					offset += len;
				}
			} else {
				offset += CONSTANT_ITEM_LENGTH[tag];
			}
		}
		return classByte;
	}

	/**
	 * 获取常量池中常量的数量
	 * 
	 * @return常量池数量
	 */
	public int getConstantPoolCount() {
		return ByteUtils.bytes2Int(classByte, CONSTANT_POOL_COUNT_INDEX, u2);
	}
}
