package com.qcloud.weapp.tunnel;

import org.json.JSONException;
import org.json.JSONObject;

public class TunnelMessage {
	private String type;
	private JSONObject content;
	
	public TunnelMessage(String messageRaw) {
		try {
			JSONObject resolved = new JSONObject(messageRaw);
			this.type = resolved.getString("type");
			this.content = resolved.getJSONObject("content");
		} catch (JSONException e) {
			this.type = "UnknownRaw";
		}
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public JSONObject getContent() {
		return content;
	}
	public void setContent(JSONObject content) {
		this.content = content;
	}
}
