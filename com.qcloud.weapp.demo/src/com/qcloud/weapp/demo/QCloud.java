package com.qcloud.weapp.demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.qcloud.weapp.Configuration;
import com.qcloud.weapp.ConfigurationManager;
import com.qcloud.weapp.ConfigurationException;

public class QCloud {
	
	public static void setupSDK() {
		try {
			JSONObject configs = new JSONObject(getConfigJson());
			Configuration configuration = new Configuration();
			configuration.setServerHost(configs.getString("serverHost"));
			configuration.setAuthServerUrl(configs.getString("authServerUrl"));
			configuration.setTunnelServerUrl(configs.getString("tunnelServerUrl"));
			configuration.setTunnelSignatureKey(configs.getString("tunnelSignatureKey"));
			if (configs.has("networkProxy")) {
				configuration.setNetworkProxy(configs.getString("networkProxy"));
			}
			ConfigurationManager.setup(configuration);
			System.out.println("QCloud SDK 已成功配置！");
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	private static String getConfigJson() {
		String configFilePath = getConfigFilePath();
		System.out.println("QCloud SDK 配置文件路径：" + configFilePath);
		
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
	
	private static String getConfigFilePath() {
		String osName = System.getProperty("os.name").toLowerCase();
		String defaultConfigFilePath = null;
		boolean isWindows = osName.indexOf("windows") > -1;
		boolean isLinux = osName.indexOf("linux") > -1;
		
		if (isWindows) {
			defaultConfigFilePath = "C:\\qcloud\\sdk.config";
		}
		else if (isLinux) {
			defaultConfigFilePath = "/etc/qcloud/sdk.config";
		}
		return defaultConfigFilePath;
	}
	
}
