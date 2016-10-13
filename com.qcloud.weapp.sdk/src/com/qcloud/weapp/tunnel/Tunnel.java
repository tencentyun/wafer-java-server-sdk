package com.qcloud.weapp.tunnel;

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

}
