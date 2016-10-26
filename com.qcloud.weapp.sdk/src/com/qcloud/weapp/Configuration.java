package com.qcloud.weapp;

public class Configuration {
	private String serverHost;
	private String authServerUrl;
	private String tunnelServerUrl;
	private String tunnelSignatureKey;
	private String networkProxy;
	private Integer networkTimeout;
	
	public String getServerHost() {
		return serverHost;
	}
	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}
	public String getAuthServerUrl() {
		return authServerUrl;
	}
	public void setAuthServerUrl(String authServerUrl) {
		this.authServerUrl = authServerUrl;
	}
	public String getTunnelServerUrl() {
		return tunnelServerUrl;
	}
	public void setTunnelServerUrl(String tunnelServerUrl) {
		this.tunnelServerUrl = tunnelServerUrl;
	}
	public String getTunnelSignatureKey() {
		return tunnelSignatureKey;
	}
	public void setTunnelSignatureKey(String tunnelSignatureKey) {
		this.tunnelSignatureKey = tunnelSignatureKey;
	}
	public String getNetworkProxy() {
		return networkProxy;
	}
	public void setNetworkProxy(String networkProxy) {
		this.networkProxy = networkProxy;
	}
	public Integer getNetworkTimeout() {
		return networkTimeout == null ? 30 : networkTimeout;
	}
	public void setNetworkTimeout(Integer networkTimeout) {
		this.networkTimeout = networkTimeout;
	}
}
