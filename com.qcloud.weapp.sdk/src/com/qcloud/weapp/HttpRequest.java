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
	private String url;
	
	public HttpRequest(String url) {
		this.url = url;
	}
	
	public String post(String body) throws IOException {
		URL url = new URL(this.url);
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
		
		if (proxy != null) {
			connection = (HttpURLConnection) url.openConnection(proxy);
		} else {
			connection = (HttpURLConnection) url.openConnection();
		}
		
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
		
		// send the request
		OutputStreamWriter requestWriter = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
		Logger.log("send request: " + body);
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
