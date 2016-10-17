package com.qcloud.weapp.tunnel;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qcloud.weapp.ConfigurationException;
import com.qcloud.weapp.ConfigurationManager;
import com.qcloud.weapp.Hash;
import com.qcloud.weapp.HttpRequest;
import com.qcloud.weapp.Logger;

public class TunnelAPI {
	private String getTunnelServerUrl() throws ConfigurationException {
		return ConfigurationManager.getCurrentConfiguration().getTunnelServerUrl();
	}

	public Tunnel requestConnect(String receiveUrl) throws Exception {
		JSONObject data = null;

		try {
			data = new JSONObject();
			data.put("receiveUrl", receiveUrl);
			data.put("protocolType", "wss");
		} catch (JSONException e) {
			// impossible
		}

		JSONObject result = request("/get/wsurl", data, true);
		Tunnel tunnel = new Tunnel(result.getString("tunnelId"));
		tunnel.setConnectUrl(result.getString("connectUrl"));

		return tunnel;
	}

	public EmitResult emitMessage(Tunnel[] tunnels, String messageType, JSONObject messageContent) throws EmitError {
		String[] tunnelIds = new String[tunnels.length];
		Integer i = 0;
		for (Tunnel tunnel : tunnels) {
			tunnelIds[i++] = tunnel.getTunnelId();
		}
		return emitMessage(tunnelIds, messageType, messageContent);
	}

	public EmitResult emitMessage(String[] tunnelIds, String messageType, JSONObject messageContent) throws EmitError {
		JSONObject packet = new JSONObject();
		try {
			packet.put("type", messageType);
			packet.put("content", messageContent);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return emitPacket(tunnelIds, "message", packet);
	}

	public EmitResult emitPacket(String[] tunnelIds, String packetType, JSONObject packetContent) throws EmitError {
		if (tunnelIds.length == 0) {
			return new EmitResult(new ArrayList<TunnelInvalidInfo>());
		}
		JSONArray data = new JSONArray();
		JSONObject packet = new JSONObject();
		try {
			packet.put("type", packetType);
			packet.put("tunnelIds", tunnelIds);
			packet.put("content", packetContent == null ? null : packetContent.toString());
			data.put(packet);
			Logger.log("data: " + data.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			JSONObject emitReturn = request("/ws/push", data, false);
			JSONArray invalidTunnelIds = emitReturn.getJSONArray("invalidTunnelIds");
			ArrayList<TunnelInvalidInfo> infos = new ArrayList<TunnelInvalidInfo>();
			for(int i = 0; i < invalidTunnelIds.length(); i++) {
				TunnelInvalidInfo info = new TunnelInvalidInfo();
				info.setTunnelId(invalidTunnelIds.getString(i));
				infos.add(info);
			}
			EmitResult emitResult = new EmitResult(infos);
			return emitResult;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EmitError("网络不可用或者信道服务器不可用", e);
		}
	}

	public JSONObject request(String path, Object data, Boolean isSendTcKey) throws Exception {
		boolean isValidData = data instanceof JSONObject || data instanceof JSONArray;
		if (!isValidData) {
			throw new Exception("数据只能是 JSONObject 或者 JSONArray 类型");
		}

		String url = getTunnelServerUrl() + path;
		String responseContent;

		try {
			String requestContent = buildRequestContent(data, isSendTcKey);
			responseContent = new HttpRequest(url).post(requestContent);
		} catch (Exception e) {
			throw new Exception("请求信道 API 失败，网络异常或鉴权服务器错误", e);
		}

		try {
			JSONObject body = new JSONObject(responseContent);
			if (body.getInt("code") != 0) {
				throw new Exception(String.format("信道服务调用失败：#%d - %s", body.get("code"), body.get("message")));
			}
			return body.has("data") ? new JSONObject(body.getString("data")) : null;
		} catch (JSONException e) {
			throw new Exception("信道服务器响应格式错误，无法解析 JSON 字符串", e);
		}

	}

	private String buildRequestContent(Object data, boolean includeTckey) throws ConfigurationException {
		// data must be JsonObject or JsonArray
		String encodeData = data.toString();
		JSONObject requestPayload = new JSONObject();
		try {
			requestPayload.put("data", encodeData);
			requestPayload.put("dataEncode", "json");
			requestPayload.put("tcId", TunnelClient.getId());
			if (includeTckey) {
				requestPayload.put("tcKey", TunnelClient.getKey());
			}
			requestPayload.put("signature", signature(encodeData));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return requestPayload.toString();
	}

	private String signature(String data) throws ConfigurationException {
		return Hash.sha1(data + TunnelClient.getKey());
	}
}
