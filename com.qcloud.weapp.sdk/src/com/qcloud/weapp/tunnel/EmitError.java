package com.qcloud.weapp.tunnel;

public class EmitError extends Exception {
	private static final long serialVersionUID = 4722717669710824633L;

	public EmitError(String message, Exception inner) {
		super(message, inner);
	}
}
