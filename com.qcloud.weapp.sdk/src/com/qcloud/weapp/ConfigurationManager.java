package com.qcloud.weapp;

public class ConfigurationManager {
	
	private static Configuration currentConfiguration;
	public static Configuration getCurrentConfiguration() throws ConfigurationException {
		if (currentConfiguration == null) {
			throw new ConfigurationException("SDK 还没有进行配置，请调用 ConfigurationManager.setup() 方法配置 SDK");
		}
		return currentConfiguration;
	}
	
	public static void setup(Configuration configuration) throws ConfigurationException {
		if (configuration == null) {
			throw new ConfigurationException("配置不能为空");
		}
		if (configuration.getServerHost() == null) throw new ConfigurationException("服务器主机配置不能为空");
		if (configuration.getAuthServerUrl() == null) throw new ConfigurationException("鉴权服务器配置不能为空");
		if (configuration.getTunnelServerUrl() == null) throw new ConfigurationException("信道服务器配置不能为空");
		if (configuration.getTunnelSignatureKey() == null) throw new ConfigurationException("SDK 密钥配置不能为空");
		currentConfiguration = configuration;
	}
}

