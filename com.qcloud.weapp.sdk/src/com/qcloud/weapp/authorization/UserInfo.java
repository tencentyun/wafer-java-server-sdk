package com.qcloud.weapp.authorization;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 表示微信用户信息.
 */
public class UserInfo {
	
	/** The open id. */
	private String openId;
	
	/** The nick name. */
	private String nickName;
	
	/** The avatar url. */
	private String avatarUrl;
	
	/** The gender. */
	private Integer gender;
	
	/** The language. */
	private String language;
	
	/** The city. */
	private String city;
	
	/** The province. */
	private String province;
	
	/** The country. */
	private String country;
	
	/**
	 * Builds the from json.
	 *
	 * @param json the json
	 * @return the user info
	 */
	static UserInfo BuildFromJson(JSONObject json) {
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

	/**
	 * Gets the open id.
	 *
	 * @return the open id
	 */
	public String getOpenId() {
		return openId;
	}

	/**
	 * Gets the nick name.
	 *
	 * @return the nick name
	 */
	public String getNickName() {
		return nickName;
	}

	/**
	 * Gets the avatar url.
	 *
	 * @return the avatar url
	 */
	public String getAvatarUrl() {
		return avatarUrl;
	}

	/**
	 * Gets the gender.
	 *
	 * @return the gender
	 */
	public Integer getGender() {
		return gender;
	}

	/**
	 * Gets the language.
	 *
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Gets the city.
	 *
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * Gets the province.
	 *
	 * @return the province
	 */
	public String getProvince() {
		return province;
	}

	/**
	 * Gets the country.
	 *
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}
}
