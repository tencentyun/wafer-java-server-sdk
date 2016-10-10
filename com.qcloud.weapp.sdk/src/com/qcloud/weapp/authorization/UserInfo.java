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
			userInfo.openId = (String) json.get("openId");
			userInfo.nickName = (String) json.get("nickName");
			userInfo.avatarUrl = (String) json.get("avatarUrl");
			userInfo.gender = (Integer) json.get("gender");
			userInfo.language = (String) json.get("language");
			userInfo.city = (String) json.get("city");
			userInfo.province = (String) json.get("province");
			userInfo.country = (String) json.get("country");
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
