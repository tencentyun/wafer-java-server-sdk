package com.qcloud.weapp.authorization;

public class AuthorizationAPIException extends Exception {
	private static final long serialVersionUID = -3088657611850871775L;
	private int code;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public AuthorizationAPIException(String message, Exception inner) {
		super(message, inner);
	}

	public AuthorizationAPIException(String message) {
		this(message, null);
	}
}
