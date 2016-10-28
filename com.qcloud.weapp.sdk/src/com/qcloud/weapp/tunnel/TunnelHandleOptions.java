package com.qcloud.weapp.tunnel;

/**
 * 表示信道服务选项
 * */
public class TunnelHandleOptions {
	private boolean checkLogin;

	/**
	 * 是否配置为检查登录态
	 * */
	public boolean isCheckLogin() {
		return checkLogin;
	}

	/**
	 * 设置是否检查登录态，如果检查登录态，则在连接请求时可以获取到用户信息
	 * */
	public void setCheckLogin(boolean checkLogin) {
		this.checkLogin = checkLogin;
	}
}
