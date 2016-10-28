package com.qcloud.weapp.tunnel;

/**
 * 表示一个信道不可用的信息
 * */
public class TunnelInvalidInfo {
	private String tunnelId;
	private TunnelInvalidType type;
	
	/**
	 * 获取不可用信道的 ID
	 * */
	public String getTunnelId() {
		return tunnelId;
	}
	void setTunnelId(String tunnelId) {
		this.tunnelId = tunnelId;
	}
	/**
	 * 获取信道不可用的类型
	 * */
	public TunnelInvalidType getType() {
		return type;
	}
	void setType(TunnelInvalidType type) {
		this.type = type;
	}
	
	
}
