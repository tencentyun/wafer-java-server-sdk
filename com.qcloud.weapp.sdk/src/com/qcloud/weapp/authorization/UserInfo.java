package com.qcloud.weapp.authorization;

import org.json.JSONException;
import org.json.JSONObject;

public class UserInfo {
	private String openId;
	private String nickName;
	private String avatarUrl;
	private Integer gender;
	private String language;
	private String city;
	private String province;
	private String country;
	
	public static UserInfo BuildFromJson(JSONObject json) {
		if (json == null) return null;
		
		UserInfo userInfo = new UserInfo();
		try {
			if (json.has("openId")) userInfo.openId = json.getString("openId");
			if (json.has("nickName")) userInfo.nickName = json.getString("nickName");
			if (json.has("avatarUrl")) userInfo.avatarUrl = json.getString("avatarUrl");
			if (json.has("gender")) userInfo.gender = json.getInt("gender");
			if (json.has("language")) userInfo.language = json.getString("language");
			if (json.has("city")) userInfo.city = json.getString("city");
			if (json.has("province")) userInfo.province = json.getString("province");
			if (json.has("country")) userInfo.country = json.getString("country");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return userInfo;
	}

	public String getOpenId() {
		return openId;
	}

	public String getNickName() {
		return nickName;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public Integer getGender() {
		return gender;
	}

	public String getLanguage() {
		return language;
	}

	public String getCity() {
		return city;
	}

	public String getProvince() {
		return province;
	}

	public String getCountry() {
		return country;
	}
}
