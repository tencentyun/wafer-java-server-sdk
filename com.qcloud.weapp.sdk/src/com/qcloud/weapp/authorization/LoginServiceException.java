package com.qcloud.weapp.authorization;

public class LoginServiceException extends Exception {
	
	private static final long serialVersionUID = 7179434716738339025L;
	private String type;
	
	public LoginServiceException(String type, String message, Exception innerException) {
		super(message, innerException);
		this.type = type;
	}
	
	public LoginServiceException(String type, String message) {
		this(type, message, null);
	}

	public String getType() {
		return this.type;
	}

}
