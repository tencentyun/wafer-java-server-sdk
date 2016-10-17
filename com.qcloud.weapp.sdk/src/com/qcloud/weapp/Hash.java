package com.qcloud.weapp;

import java.security.MessageDigest;

public class Hash {

	public static String sha1(String str) {
		return compute(str, "SHA-1");
	}

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