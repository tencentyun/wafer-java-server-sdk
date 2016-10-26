package com.qcloud.weapp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

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
	
	public static void setupFromFile(String configFilePath) throws JSONException, ConfigurationException {
		JSONObject configs = new JSONObject(getConfigJson(configFilePath));
		Configuration configuration = new Configuration();
		configuration.setServerHost(configs.getString("serverHost"));
		configuration.setAuthServerUrl(configs.getString("authServerUrl"));
		configuration.setTunnelServerUrl(configs.getString("tunnelServerUrl"));
		configuration.setTunnelSignatureKey(configs.getString("tunnelSignatureKey"));
		if (configs.has("networkProxy")) {
			configuration.setNetworkProxy(configs.getString("networkProxy"));
		}
		if (configs.has("networkTimeout")) {
			configuration.setNetworkTimeout(configs.getInt("newtorkTimeout"));
		}
		ConfigurationManager.setup(configuration);
	}

	private static String getConfigJson(String configFilePath) {
		
		String configJsonText = null;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(configFilePath));
			StringBuilder sb = new StringBuilder();
			String line;
			while((line = br.readLine()) != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
			}
			configJsonText = sb.toString();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return configJsonText;
	}
	
}

