package com.qcloud.weapp.tunnel;

import java.util.ArrayList;

/**
 * 表示向信道发送消息后反馈的结果，可能会包含无效信道的列表
 * @see com.qcloud.weapp.tunnel.Tunnel
 * @see com.qcloud.weapp.tunnel.TunnelRoom
 * */
public class EmitResult {
	private ArrayList<TunnelInvalidInfo> tunnelInvalidInfos;
	
	EmitResult(ArrayList<TunnelInvalidInfo> tunnelInvalidInfos) {
		this.tunnelInvalidInfos = tunnelInvalidInfos;
	}

	/**
	 * 获取无效信道列表
	 * */
	public ArrayList<TunnelInvalidInfo> getTunnelInvalidInfos() {
		return tunnelInvalidInfos;
	}
}
