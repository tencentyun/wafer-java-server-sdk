package com.qcloud.weapp.test.tunnel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qcloud.weapp.ConfigurationException;
import com.qcloud.weapp.ConfigurationManager;
import com.qcloud.weapp.Hash;
import com.qcloud.weapp.HttpRequest;
import com.qcloud.weapp.test.HttpMock;
import com.qcloud.weapp.test.URLConnectionMock;

import static org.mockito.Mockito.*;

public class TunnelServiceTestHelper {
	
	public HttpMock createTunnelHttpMock(String method, String sessionType) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		
		when(request.getMethod()).thenReturn(method);
		
		if (sessionType != null) {
			if (sessionType.equals("valid")) {
				when(request.getHeader("X-WX-Id")).thenReturn("valid-id");
				when(request.getHeader("X-WX-Skey")).thenReturn("valid-key");
			}
			else if (sessionType.equals("invalid")) {
				when(request.getHeader("X-WX-Id")).thenReturn("invalid-id");
				when(request.getHeader("X-WX-Skey")).thenReturn("invalid-key");
			}
		}
		
		HttpMock mock = new HttpMock();
		mock.request = request;
		mock.response = response;
		mock.setupResponseWriter();
		
		return mock;
	}
	
	public HttpMock createTunnelHttpMock(String method) {
		return createTunnelHttpMock(method, null);
	}
		
	public boolean checkBodyHasMagicId(JSONObject body) {
		return body.has("F2C224D4-2BCE-4C64-AF9F-A6D872000D1A");
	}
	
	public String buildPacket(String data, boolean fakeSignature) {
		JSONObject json = new JSONObject();
		try {
			json.put("data", data);
			json.put("dataEncode", "json");
			json.put("signature", fakeSignature ? "fake-signature" : Hash.sha1(data + ConfigurationManager.getCurrentConfiguration().getTunnelSignatureKey()));
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	
	public String buildPacket(String data) {
		return buildPacket(data, false);
	}
	
	public JSONArray resolvePackets(String body) throws JSONException {
		return new JSONArray(new JSONObject(body).getString(("data")));
	}
	
	public boolean checkPostResponseSuccess(String responseText) {
		try {
			JSONObject response = new JSONObject(responseText);
			return response.getInt("code") == 0;
		} catch (JSONException e) {
			return false;
		}
	}
	
	private HttpRequest.ConnectionProvider originProvider;
	public URLConnectionMock useURLConnectionMock() {
		originProvider = HttpRequest.getUrlProvider();
		URLConnectionMock mock = new URLConnectionMock();
		HttpRequest.setUrlProvider(mock);
		return mock;
	}
	
	public void restoreURLConnectionMock() {
		HttpRequest.setUrlProvider(originProvider);
	}
}
