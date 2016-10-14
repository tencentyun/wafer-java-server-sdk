package com.qcloud.weapp.tunnel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.qcloud.weapp.ConfigurationException;
import com.qcloud.weapp.ConfigurationManager;
import com.qcloud.weapp.ServiceBase;
import com.qcloud.weapp.authorization.LoginService;
import com.qcloud.weapp.authorization.UserInfo;


public class TunnelService extends ServiceBase {
	private String secretKey = "JAVA_SECRET";
	private String tunnelServerUrl = "http://ws.qcloud.com";

	public TunnelService(HttpServletRequest request, HttpServletResponse response) {
		super(request, response);
	}

	/**
	 * 处理 WebSocket 信道请求
	 * @throws ConfigurationException 
	 */
	public void handle(TunnelHandler handler, TunnelHandleOptions options) throws ConfigurationException {
		if (request.getMethod().toUpperCase() == "GET") {
			handleGet(handler, options);
		}
		if (request.getMethod().toUpperCase() == "POST") {
			handlePost(handler, options);
		}
	}

	/**
	 * 处理 GET 请求
	 * 
	 * GET 请求表示客户端请求进行信道连接，此时会向 SDK 申请信道连接地址，并且返回给客户端
	 * 如果配置指定了要求登陆，还会调用登陆服务来校验登陆态并获得用户信息
	 * @throws ConfigurationException 
	 * 
	 * @throws Exception
	 */
	private void handleGet(TunnelHandler handler, TunnelHandleOptions options) throws ConfigurationException {
		Tunnel tunnel = null;
		UserInfo user = null;

		if (options != null && options.isCheckLogin()) {
			try {
				LoginService loginService = new LoginService(request, response);
				user = loginService.check();
			} catch (Exception e) {
				return;
			}
		}

		TunnelAPI api = new TunnelAPI();
		try {
			String receiveUrl = buildReceiveUrl();
			tunnel = api.requestConnect(secretKey, receiveUrl);
		} catch (Exception e) {
			writeJson(getJsonForError(e));
			return;
		}

		JSONObject result = new JSONObject();
		try {
			result.put("url", tunnel.getConnectUrl());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		writeJson(result);

		handler.OnTunnelRequest(tunnel, user);
	}

	private String buildReceiveUrl() throws ConfigurationException {
		URI tunnelServerUri = URI.create(tunnelServerUrl);
		String schema = tunnelServerUri.getScheme();
		String host = ConfigurationManager.getCurrentConfiguration().getServerHost();
		String path = request.getRequestURI();
		return schema + "://" + host + path;
	}

	private void handlePost(TunnelHandler handler, TunnelHandleOptions options) {
		String requestBody = null;
		String tunnelId = null;
		String packetType = null;
		String packetContent = null;

		try {
			BufferedReader requestReader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
			requestBody = "";
			for (String line; (line = requestReader.readLine()) != null;) {
				requestBody += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
			writeJson(getJsonForError(e));
			return;
		}

		try {
			JSONObject body = new JSONObject(requestBody);
			JSONObject packet = body.getJSONObject("data");
			tunnelId = packet.getString("tunnelId");
			packetType = packet.getString("type");
			packetContent = (String) packet.get("content");
			// String signature = body.getString("signature");
		} catch (JSONException e) {
			JSONObject errJson = new JSONObject();
			try {
				errJson.put("code", 9001);
				errJson.put("message", "Cant not parse the request body: invalid json");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			writeJson(errJson);
		}

		// response first
		try {
			JSONObject response = new JSONObject();
			response.put("code", 0);
			response.put("message", "OK");
			writeJson(response);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Tunnel tunnel = Tunnel.getById(tunnelId);
		if (packetType.equals("connect")) {
			handler.OnTunnelConnect(tunnel);
		}
		else if (packetType.equals("message")) {
			handler.OnTunnelMessage(tunnel, new TunnelMessage(packetContent));
		}
		else if (packetType.equals("close")) {
			handler.OnTunnelClose(tunnel);
		}
	}

}
