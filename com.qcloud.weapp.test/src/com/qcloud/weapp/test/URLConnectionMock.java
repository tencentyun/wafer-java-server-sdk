package com.qcloud.weapp.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;

import static org.mockito.Mockito.*;
import com.qcloud.weapp.*;

public class URLConnectionMock implements HttpRequest.ConnectionProvider {
	
	private ByteArrayInputStream responseStream;
	private ByteArrayOutputStream requestStream;
	
	public URLConnectionMock() {
		requestStream = new ByteArrayOutputStream();
	}
	
	public void setResponseBody(String body) {
		try {
			responseStream = new ByteArrayInputStream(body.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public String getRequestBody() {
		try {
			return new String(requestStream.toByteArray(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public HttpURLConnection getConnection(String url, Proxy proxy) throws IOException {
		HttpURLConnection connMock = mock(HttpURLConnection.class);
		when(connMock.getInputStream()).thenReturn(responseStream);
		when(connMock.getOutputStream()).thenReturn(requestStream);
		return connMock;
	}

}
