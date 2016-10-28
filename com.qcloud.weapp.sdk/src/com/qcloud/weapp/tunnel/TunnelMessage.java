package com.qcloud.weapp.tunnel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 表示一个信道消息
 * */
public class TunnelMessage {
	private String type;
	private Object content;
	
	TunnelMessage(String messageRaw) {
		try {
			JSONObject resolved = new JSONObject(messageRaw);
			this.type = resolved.getString("type");
			this.content = resolved.get("content");
		} catch (JSONException e) {
			this.type = "UnknownRaw";
			this.content = messageRaw;
		}
	}
	/**
	 * 获取信道消息的类型
	 * */
	public String getType() {
		return type;
	}
	/**
	 * 获取信道消息的内容
	 * */
	public Object getContent() {
		return content;
	}
	void setType(String type) {
		this.type = type;
	}
	void setContent(JSONObject content) {
		this.content = content;
	}
}
