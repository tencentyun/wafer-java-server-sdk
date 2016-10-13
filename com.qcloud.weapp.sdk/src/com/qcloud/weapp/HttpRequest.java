package com.qcloud.weapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

public class HttpRequest {
	private String url;
	
	public HttpRequest(String url) {
		this.url = url;
	}
	
	public String post(String body) throws IOException {
		
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8888));
		HttpURLConnection connection = (HttpURLConnection) new URL(this.url).openConnection(proxy);
		
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
		
		// send the request
		OutputStreamWriter requestWriter = new OutputStreamWriter(connection.getOutputStream());
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
