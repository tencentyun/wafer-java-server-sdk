package com.qcloud.weapp.tunnel;

/**
 * 表示一个信道，不可以被实例化。可以通过 Tunnel.getById() 获取。
 * */
public class Tunnel {

	private String tunnelId;
	private String connectUrl;
	
	Tunnel(String tunnelId) {
		this.tunnelId = tunnelId;
	}

	String getConnectUrl() {
		return connectUrl;
	}

	void setConnectUrl(String connectUrl) {
		this.connectUrl = connectUrl;
	}

	public String getTunnelId() {
		return tunnelId;
	}

	void setTunnelId(String tunnelId) {
		this.tunnelId = tunnelId;
	}

	/**
	 * 获取具有指定信道 ID 的信道
	 * */
	public static Tunnel getById(String tunnelId) {
		return new Tunnel(tunnelId);
	}
	
	/**
	 * 发送消息到信道中
	 * @param messageType 消息类型
	 * @param messageContent 消息内容，如果需要发送对象或者数组，需要使用 JSONObject 或 JSONArray 类型
	 * */
	public EmitResult emit(String messageType, Object messageContent) throws EmitError {
		TunnelAPI api = new TunnelAPI();
		return api.emitMessage(new String[]{ tunnelId }, messageType, messageContent);
	}
	
	/**
	 * 关闭当前信道
	 * */
	public EmitResult close() throws EmitError {
		TunnelAPI api = new TunnelAPI();
		return api.emitPacket(new String[]{ tunnelId }, "close", null);
	}
}
