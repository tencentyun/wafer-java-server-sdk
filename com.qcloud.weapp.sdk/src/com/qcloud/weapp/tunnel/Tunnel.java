package com.qcloud.weapp.tunnel;

import org.json.JSONObject;

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
	
	public boolean emit(String messageType, JSONObject messageContent) {
		TunnelAPI api = new TunnelAPI();
		return api.emitMessage(new String[]{ tunnelId }, messageType, messageContent);
	}
	
	public boolean close() {
		TunnelAPI api = new TunnelAPI();
		return api.emitPacket(new String[]{ tunnelId }, "close", null);
	}
}
