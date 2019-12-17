package com.chatopera.cc.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.ClassUtils;

import java.io.File;

/**
 * 公共工具类
 */
public class CommonUtil {
	/**
	 * 字符串转换为特定数量的余数1~n
	 *
	 * @param orginalString
	 * @param divider
	 * @return
	 */
	public static int string2NumberByMod(String orginalString, int divider) {
		if (StringUtils.isBlank(orginalString) || divider <= 1) {
			return 1;
		}
		return (orginalString.hashCode() % divider) + 1;
//		final String md5 = DigestUtils.md5Hex(orginalString);
//		int result = 1;
//		for (int i = 0; i < 16; ++i) {
//			String part = md5.substring(i * 2, i * 2 + 2);
//			int value = Integer.parseInt(part, 16);
//			result += value;
//		}
//		return (result % divider) + 1;
	}

	/**
	 * 获取给定路径的文件夹下的文件个数(不包括文件夹)
	 *
	 * @param filepath
	 */
	public static int getFileCount(String filepath) {
		String classpath = ClassUtils.getDefaultClassLoader().getResource("").getPath();

		File file = new File(filepath);
		File[] listfile = file.listFiles();
		int count = 0;
		for (int i = 0; i < listfile.length; i++) {
			if (listfile[i].isFile()) {
				count++;
			}
		}
		return count;
	}
}
