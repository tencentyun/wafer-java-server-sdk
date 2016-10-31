/*
 * 
 */
package com.qcloud.weapp;

/**
 * 表示 SDK 配置
 * @see com.qcloud.weapp.ConfigurationManager
 * @see <a target="_blank" href="https://github.com/tencentyun/weapp-solution/wiki/%E6%9C%8D%E5%8A%A1%E7%AB%AF-SDK-%E9%85%8D%E7%BD%AE">服务端 SDK 配置</a>
 */
public class Configuration {
	
	/** The server host. */
	private String serverHost;
	
	/** The auth server url. */
	private String authServerUrl;
	
	/** The tunnel server url. */
	private String tunnelServerUrl;
	
	/** The tunnel signature key. */
	private String tunnelSignatureKey;
	
	/** The network proxy. */
	private String networkProxy;
	
	/** The network timeout. */
	private int networkTimeout;
	
	/**
	 * Gets the server host.
	 *
	 * @return the server host
	 */
	public String getServerHost() {
		return serverHost;
	}
	
	/**
	 * Sets the server host.
	 *
	 * @param serverHost the new server host
	 */
	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}
	
	/**
	 * Gets the auth server url.
	 *
	 * @return the auth server url
	 */
	public String getAuthServerUrl() {
		return authServerUrl;
	}
	
	/**
	 * Sets the auth server url.
	 *
	 * @param authServerUrl the new auth server url
	 */
	public void setAuthServerUrl(String authServerUrl) {
		this.authServerUrl = authServerUrl;
	}
	
	/**
	 * Gets the tunnel server url.
	 *
	 * @return the tunnel server url
	 */
	public String getTunnelServerUrl() {
		return tunnelServerUrl;
	}
	
	/**
	 * Sets the tunnel server url.
	 *
	 * @param tunnelServerUrl the new tunnel server url
	 */
	public void setTunnelServerUrl(String tunnelServerUrl) {
		this.tunnelServerUrl = tunnelServerUrl;
	}
	
	/**
	 * Gets the tunnel signature key.
	 *
	 * @return the tunnel signature key
	 */
	public String getTunnelSignatureKey() {
		return tunnelSignatureKey;
	}
	
	/**
	 * Sets the tunnel signature key.
	 *
	 * @param tunnelSignatureKey the new tunnel signature key
	 */
	public void setTunnelSignatureKey(String tunnelSignatureKey) {
		this.tunnelSignatureKey = tunnelSignatureKey;
	}
	
	/**
	 * Gets the network proxy.
	 *
	 * @return the network proxy
	 */
	public String getNetworkProxy() {
		return networkProxy;
	}
	
	/**
	 * Sets the network proxy.
	 *
	 * @param networkProxy the new network proxy
	 */
	public void setNetworkProxy(String networkProxy) {
		this.networkProxy = networkProxy;
	}
	
	/**
	 * Gets the network timeout.
	 *
	 * @return the network timeout
	 */
	public int getNetworkTimeout() {
		return networkTimeout;
	}
	
	/**
	 * Sets the network timeout.
	 *
	 * @param networkTimeout the new network timeout
	 */
	public void setNetworkTimeout(Integer networkTimeout) {
		this.networkTimeout = networkTimeout;
	}
}
