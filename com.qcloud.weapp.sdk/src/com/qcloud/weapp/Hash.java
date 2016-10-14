package com.qcloud.weapp;

import java.security.MessageDigest;

public class Hash {
 
	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
 
	public static String compute(String str, String algorithm) {
		if (str == null) {
			return null;
		}
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			messageDigest.update(str.getBytes());
			return getHashText(messageDigest.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
 
	}
	
	public static String sha1(String str) {
		return compute(str, "SHA1");
	}
 
	public static String md5(String str) {
		return compute(str, "MD5");
	}
 
	private static String getHashText(byte[] bytes) {
		int len = bytes.length;
		StringBuilder buf = new StringBuilder(len * 2);
		for (int j = 0; j < len; j++) {
			buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
			buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
		}
		return buf.toString();
	}
}