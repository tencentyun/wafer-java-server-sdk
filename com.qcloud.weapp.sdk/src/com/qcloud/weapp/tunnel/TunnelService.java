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
import com.qcloud.weapp.Hash;
import com.qcloud.weapp.Logger;
import com.qcloud.weapp.ServiceBase;
import com.qcloud.weapp.authorization.LoginService;
import com.qcloud.weapp.authorization.UserInfo;


public class TunnelService extends ServiceBase {

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
			Logger.log("receiverUrl = " + receiveUrl);
			tunnel = api.requestConnect(receiveUrl);
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

		handler.onTunnelRequest(tunnel, user);
	}

	private String buildReceiveUrl() throws ConfigurationException {
		URI tunnelServerUri = URI.create(ConfigurationManager.getCurrentConfiguration().getTunnelServerUrl());
		String schema = tunnelServerUri.getScheme();
		String host = ConfigurationManager.getCurrentConfiguration().getServerHost();
		String path = request.getRequestURI();
		return schema + "://" + host + path;
	}

	private void handlePost(TunnelHandler handler, TunnelHandleOptions options) throws ConfigurationException {
		String requestContent = null;

		// 1. 读取报文内容
		try {
			BufferedReader requestReader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
			requestContent = "";
			for (String line; (line = requestReader.readLine()) != null;) {
				requestContent += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
			writeJson(getJsonForError(e));
			return;
		}
		Logger.log(requestContent);

		// 2. 读取报文内容成 JSON 并保存在 body 变量中
		JSONObject body = null;
		String data = null, signature = null;
		try {
			body = new JSONObject(requestContent);
			data = body.getString("data");
			signature = body.getString("signature");
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
		
		// 3. 检查报文签名
		String computedSignature = Hash.sha1(data + TunnelClient.getKey());
		if (!computedSignature.equals(signature)) {
			Logger.log("computedSignature = " + computedSignature);
			JSONObject json = new JSONObject();
			try {
				json.put("code", 9003);
				json.put("message", "Bad Request - 签名错误");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			writeJson(json);
			return;
		}
		
		// 4. 解析报文中携带的数据
		JSONObject packet;
		String tunnelId = null;
		String packetType = null;
		String packetContent = null;
		try {
			packet = new JSONObject(data);
			tunnelId = packet.getString("tunnelId");
			packetType = packet.getString("type");
			if (packet.has("content")) {
				Logger.log("has content");
				packetContent = packet.getString("content");
			}
			
			JSONObject response = new JSONObject();
			response.put("code", 0);
			response.put("message", "OK");
			writeJson(response);
		} catch (JSONException e) {
			Logger.log("ex222222222222: " + e.getMessage());
			Logger.log(e.getStackTrace().toString());
			JSONObject response = new JSONObject();
			try {
				response.put("code", 9004);
				response.put("message", "Bad Request - 无法解析的数据包");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			writeJson(response);
			e.printStackTrace();
		}
		
		Logger.log(String.format("PacketType = %s, TunnelId = %s", packetType, tunnelId));
		// 5. 交给客户处理实例处理报文
		Tunnel tunnel = Tunnel.getById(tunnelId);
		if (packetType.equals("connect")) {
			handler.onTunnelConnect(tunnel);
		}
		else if (packetType.equals("message")) {
			handler.onTunnelMessage(tunnel, new TunnelMessage(packetContent));
		} else if (packetType.equals("close")) {
			handler.onTunnelClose(tunnel);
		}
	}

}
