package com.qcloud.weapp.tunnel;

/**
 * 表示一个信道
 * */
public class Tunnel {

	private String tunnelId;
	private String connectUrl;
	
	public Tunnel(String tunnelId) {
		this.tunnelId = tunnelId;
	}

	public String getConnectUrl() {
		return connectUrl;
	}

	public void setConnectUrl(String connectUrl) {
		this.connectUrl = connectUrl;
	}

	public String getTunnelId() {
		return tunnelId;
	}

	public void setTunnelId(String tunnelId) {
		this.tunnelId = tunnelId;
	}

	public static Tunnel getById(String tunnelId) {
		return new Tunnel(tunnelId);
	}
	
	public EmitResult emit(String messageType, Object messageContent) throws EmitError {
		TunnelAPI api = new TunnelAPI();
		return api.emitMessage(new String[]{ tunnelId }, messageType, messageContent);
	}
	
	public EmitResult close() throws EmitError {
		TunnelAPI api = new TunnelAPI();
		return api.emitPacket(new String[]{ tunnelId }, "close", null);
	}
}
