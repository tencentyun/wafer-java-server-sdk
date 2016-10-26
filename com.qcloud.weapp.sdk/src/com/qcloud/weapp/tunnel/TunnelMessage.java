package com.qcloud.weapp.tunnel;

import org.json.JSONException;
import org.json.JSONObject;

public class TunnelMessage {
	private String type;
	private Object content;
	
	public TunnelMessage(String messageRaw) {
		try {
			JSONObject resolved = new JSONObject(messageRaw);
			this.type = resolved.getString("type");
			this.content = resolved.get("content");
		} catch (JSONException e) {
			this.type = "UnknownRaw";
			this.content = messageRaw;
		}
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Object getContent() {
		return content;
	}
	public void setContent(JSONObject content) {
		this.content = content;
	}
}
