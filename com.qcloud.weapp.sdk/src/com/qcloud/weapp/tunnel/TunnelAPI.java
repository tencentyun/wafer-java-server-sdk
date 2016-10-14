package com.qcloud.weapp.tunnel;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qcloud.weapp.ConfigurationException;
import com.qcloud.weapp.ConfigurationManager;
import com.qcloud.weapp.HttpRequest;
import com.qcloud.weapp.Logger;

public class TunnelAPI {
	private String getTunnelServerUrl() throws ConfigurationException {
		return ConfigurationManager.getCurrentConfiguration().getTunnelServerUrl();
	}
	
	public Tunnel requestConnect(String skey, String receiveUrl) throws Exception {
		JSONObject data = null;
		
		try {
			data = new JSONObject();
			data.put("skey", skey);
			data.put("receiveUrl", receiveUrl);
			data.put("protocolType", "wss");
		} catch (JSONException e) {
			// impossible
		}
		
		JSONObject result = request("/get/wsurl?uin=208852691", data);
		Tunnel tunnel = new Tunnel(result.getString("tunnelId"));
		tunnel.setConnectUrl(result.getString("connectUrl"));
		
		return tunnel;
	}
	
	public boolean emitMessage(Tunnel[] tunnels, String messageType, JSONObject messageContent) {
		String[] tunnelIds = new String[tunnels.length];
		Integer i = 0;
		for (Tunnel tunnel : tunnels) {
			tunnelIds[i++] = tunnel.getTunnelId();
		}
		return emitMessage(tunnelIds, messageType, messageContent);
	}
	
	public boolean emitMessage(String[] tunnelIds, String messageType, JSONObject messageContent) {
		JSONObject packet = new JSONObject();
		try {
			packet.put("type", messageType);
			packet.put("content", messageContent);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return emitPacket(tunnelIds, "message", packet);
	}
	
	public boolean emitPacket(String[] tunnelIds, String packetType, JSONObject packetContent) {
		if (tunnelIds.length == 0) {
			return true;
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
			request("/ws/push", data);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public JSONObject request(String path, Object data) throws Exception {
		boolean isValidData = data instanceof JSONObject || data instanceof JSONArray;
		if (!isValidData) {
			throw new Exception("数据只能是 JSONObject 或者 JSONArray 类型");
		}
		
		String url = getTunnelServerUrl() + path;
		String responseContent;
		
		try {
			String requestContent = buildRequestContent(data);
			System.out.println("==============Tunnel Request==============");
			System.out.println(requestContent);
			System.out.println();
			
			responseContent = new HttpRequest(url).post(requestContent);
			
			System.out.println("==============Tunnel Response==============");
			System.out.println(responseContent);
			System.out.println();
		} catch (Exception e) {
			throw new Exception("请求信道 API 失败，网络异常或鉴权服务器错误", e);
		}
		
		try {
			JSONObject body = new JSONObject(responseContent);
			if (body.getInt("code") != 0) {
                throw new Exception(
            		String.format("信道服务调用失败：#%d - %s", body.get("code"), body.get("message"))
        		);
			}
			return body.has("data") ? body.getJSONObject("data") : null;
		} catch(JSONException e) {
			throw new Exception("信道服务器响应格式错误，无法解析 JSON 字符串", e);
		}
		
	}

	private String buildRequestContent(Object data) {
		JSONObject body = new JSONObject();
		try {
			body.put("data", data);
			body.put("signature", signature(data));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Logger.log("body: " + body.toString());
		return body.toString();
	}
	
	private String signature(Object obj) {
		return "JAVA_SIGNATURE";
	}
}
