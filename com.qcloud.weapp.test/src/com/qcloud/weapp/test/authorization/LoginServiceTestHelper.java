package com.qcloud.weapp.test.authorization;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.qcloud.weapp.test.HttpMock;

import static org.mockito.Mockito.*;

public class LoginServiceTestHelper {
	
	public HttpMock createLoginHttpMock(String code, String encryptData) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		
		when(request.getHeader("X-WX-Code")).thenReturn(code);
		when(request.getHeader("X-WX-Encrypt-Data")).thenReturn(encryptData);
		
		HttpMock mock = new HttpMock();
		mock.request = request;
		mock.response = response;
		mock.setupResponseWriter();
		
		return mock;
	}
	
	public HttpMock createCheckHttpMock(String id, String skey) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		
		when(request.getHeader("X-WX-Id")).thenReturn(id);
		when(request.getHeader("X-WX-Skey")).thenReturn(skey);
		
		HttpMock mock = new HttpMock();
		mock.request = request;
		mock.response = response;
		mock.setupResponseWriter();
		
		return mock;
	}
	
	public boolean checkBodyHasMagicId(JSONObject body) {
		return body.has("F2C224D4-2BCE-4C64-AF9F-A6D872000D1A");
	}
	
	public boolean checkBodyHasSession(JSONObject body) {
		if (!body.has("session")) return false;
		try {
			JSONObject session = body.getJSONObject("session");
			return session.has("id") && session.has("skey");
		} catch (JSONException e) {
			return false;
		}
	}
}
