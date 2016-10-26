package com.qcloud.weapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRequest {
	
	public interface ConnectionProvider {
		HttpURLConnection getConnection(String url, Proxy proxy) throws IOException;
	}
	
	private static ConnectionProvider connectionProvider = new ConnectionProvider() {
		
		@Override
		public HttpURLConnection getConnection(String url, Proxy proxy) throws IOException {
			if (proxy == null) {
				return (HttpURLConnection) new URL(url).openConnection();
			} else {
				return (HttpURLConnection) new URL(url).openConnection(proxy);
			}
		}
	};
	
	public static void setUrlProvider(ConnectionProvider provider) {
		connectionProvider = provider;
	}

	public static ConnectionProvider getUrlProvider() {
		return connectionProvider;
	}
	
	private String url;
	
	public HttpRequest(String url) {
		this.url = url;
	}
	
	public String post(String body) throws IOException {
		HttpURLConnection connection;
		Proxy proxy = null;
		try {
			String proxyString = ConfigurationManager.getCurrentConfiguration().getNetworkProxy();
			if (proxyString != null) {
				Pattern proxyPattern = Pattern.compile("^(.+)\\:(\\d+)$");
				Matcher proxyMatch = proxyPattern.matcher(proxyString);
				if (proxyMatch.find()) {
					String host = proxyMatch.group(1);
					String port = proxyMatch.group(2);
					proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, Integer.parseInt(port)));
				}
			}
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}

		connection = connectionProvider.getConnection(url, proxy);
		
		int networkTimeout = 60000;
		
		try {
			networkTimeout = 1000 * ConfigurationManager.getCurrentConfiguration().getNetworkTimeout();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		
		connection.setConnectTimeout(networkTimeout);
		connection.setReadTimeout(networkTimeout);
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
		
		// send the request
		OutputStreamWriter requestWriter = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
		requestWriter.write(body);
		requestWriter.flush();

		// read the response
		BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuffer responseBuffer = new StringBuffer();
		for (String line; (line = responseReader.readLine()) != null;) {
			responseBuffer.append(line);
		}
		return responseBuffer.toString();
	}
	
}
