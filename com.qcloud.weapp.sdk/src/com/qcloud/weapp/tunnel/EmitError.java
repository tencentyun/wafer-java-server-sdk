package com.qcloud.weapp.tunnel;

/**
 * 表示信道消息发送时发生的异常
 * */
public class EmitError extends Exception {
	private static final long serialVersionUID = 4722717669710824633L;

	EmitError(String message, Exception inner) {
		super(message, inner);
	}
}
