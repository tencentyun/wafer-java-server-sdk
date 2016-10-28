package com.qcloud.weapp;

/**
 * 表示配置时产生的异常
 * */
public class ConfigurationException extends Exception {
	private static final long serialVersionUID = 570042088042301018L;

	ConfigurationException(String message) {
		super(message);
	}
}
