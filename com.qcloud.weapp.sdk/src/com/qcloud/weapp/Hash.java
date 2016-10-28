package com.qcloud.weapp;

import java.security.MessageDigest;

/**
 * 工具类，用于计算哈希值（SDK 内部使用）
 * */
public class Hash {

	/**
	 * 计算字符串的 sha1 哈希值
	 * */
	public static String sha1(String str) {
		return compute(str, "SHA-1");
	}

	/**
	 * 计算字符串的 md5 哈希值
	 * */
	public static String md5(String str) {
		return compute(str, "MD5");
	}

	public static String compute(String str, String algorithm) {
		if (str == null) {
			return null;
		}
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			messageDigest.update(str.getBytes("utf-8"));
			return byteArrayToHexString(messageDigest.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String byteArrayToHexString(byte[] b) {
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

}