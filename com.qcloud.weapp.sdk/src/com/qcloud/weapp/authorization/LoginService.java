package com.qcloud.weapp.authorization;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.qcloud.weapp.ConfigurationException;
import com.qcloud.weapp.ServiceBase;

public class LoginService extends ServiceBase {
	
	public LoginService(HttpServletRequest request, HttpServletResponse response) {
		super(request, response);
	}
	
	public UserInfo login() throws IllegalArgumentException, LoginServiceException, ConfigurationException {
		String code = getHeader(Constants.WX_HEADER_CODE);
		String encryptData = getHeader(Constants.WX_HEADER_ENCRYPT_DATA);
		
		AuthorizationAPI api = new AuthorizationAPI();
		JSONObject loginResult;
		
		try {
			loginResult = api.login(code, encryptData);
		} catch (AuthorizationAPIException apiError) {
			LoginServiceException error = new LoginServiceException(Constants.ERR_LOGIN_FAILED, apiError.getMessage(), apiError);
			writeJson(getJsonForError(error));
			throw error;
		}
		
		JSONObject json = prepareResponseJson();
		JSONObject session = new JSONObject();
		JSONObject userInfo = null;
		try {
			session.put("id", loginResult.get("id"));
			session.put("skey", loginResult.get("skey"));
			json.put("session", session);
			writeJson(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		try {
			userInfo = loginResult.getJSONObject("user_info");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return UserInfo.BuildFromJson(userInfo);
	}
	
	public UserInfo check() throws IllegalArgumentException, LoginServiceException, ConfigurationException {
		String id = getHeader(Constants.WX_HEADER_ID);
		String skey = getHeader(Constants.WX_HEADER_SKEY);
		
		AuthorizationAPI api = new AuthorizationAPI();
		JSONObject checkLoginResult = null;
		try {
			checkLoginResult = api.checkLogin(id, skey);
		} catch (AuthorizationAPIException apiError) {
			String errorType = Constants.ERR_CHECK_LOGIN_FAILED;
			if (apiError.getCode() == 60011 || apiError.getCode() == 60012) {
				errorType = Constants.ERR_INVALID_SESSION;
			}
			LoginServiceException error = new LoginServiceException(errorType, apiError.getMessage(), apiError);
			writeJson(getJsonForError(error));
			throw error;
		}
		JSONObject userInfo = null;
		try {
			userInfo = checkLoginResult.getJSONObject("user_info");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return UserInfo.BuildFromJson(userInfo);
	}
	
	private String getHeader(String key) throws IllegalArgumentException {
		String value = request.getHeader(key);
		if (value == null || value.isEmpty()) {
			IllegalArgumentException error = new IllegalArgumentException(String.format("请求头不包含 %s，请配合客户端 SDK 使用", key));
			writeJson(getJsonForError(error));
			throw error;
		}
		return value;
	}
	
}
