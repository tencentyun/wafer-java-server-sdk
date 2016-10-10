package com.qcloud.weapp.authorization;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginService {
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	public LoginService(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}
	
	public JSONObject login() throws IllegalArgumentException, LoginServiceException {
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
		
		return userInfo;
	}
	
	public JSONObject checkLogin() throws IllegalArgumentException, LoginServiceException {
		String id = getHeader(Constants.WX_HEADER_ID);
		String skey = getHeader(Constants.WX_HEADER_SKEY);
		
		AuthorizationAPI api = new AuthorizationAPI();
		JSONObject checkLoginResult = null;
		try {
			checkLoginResult = api.checkLogin(id, skey);
		} catch (AuthorizationAPIException apiError) {
			LoginServiceException error = new LoginServiceException(Constants.ERR_CHECK_LOGIN_FAILED, apiError.getMessage(), apiError);
			writeJson(getJsonForError(error));
			throw error;
		}
		JSONObject userInfo = null;
		try {
			userInfo = checkLoginResult.getJSONObject("user_info");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return userInfo;
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
	
	private void writeJson(JSONObject json) {
		try {
			this.response.setContentType("application/json");
			this.response.setCharacterEncoding("utf-8");
			this.response.getWriter().print(json.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private JSONObject prepareResponseJson() {
		JSONObject json = new JSONObject();
		try {
			json.put(Constants.WX_SESSION_MAGIC_ID, 1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	private JSONObject getJsonForError(Exception error) {
		JSONObject json = prepareResponseJson();
		try {
			if (error instanceof LoginServiceException) {
				json.put("error", ((LoginServiceException) error).getType());
			}
			json.put("message", error.getMessage());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	
}
