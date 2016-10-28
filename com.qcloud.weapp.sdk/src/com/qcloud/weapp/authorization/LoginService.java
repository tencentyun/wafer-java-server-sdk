package com.qcloud.weapp.authorization;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.qcloud.weapp.ConfigurationException;

/**
 * 提供登录服务
 * */
public class LoginService {
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	/**
	 * 从 Servlet Request 和 Servlet Response 创建登录服务
	 * @param request Servlet Request
	 * @param response Servlet Response
	 * */
	public LoginService(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
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
	
	private JSONObject getJsonForError(Exception error, int errorCode) {
		JSONObject json = prepareResponseJson();
		try {
			json.put("code", errorCode);
			if (error instanceof LoginServiceException) {
				json.put("error", ((LoginServiceException) error).getType());
			}
			json.put("message", error.getMessage());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	private JSONObject getJsonForError(Exception error) {
		return getJsonForError(error, -1);
	}
	
	/**
	 * 处理登录请求
	 * @return 登录成功将返回用户信息
	 * */
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
	
	/**
	 * 检查当前请求的会话状态
	 * @return 如果包含可用会话，将会返回会话对应的用户信息
	 * */
	public UserInfo check() throws LoginServiceException, ConfigurationException {
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
	
	private String getHeader(String key) throws LoginServiceException {
		String value = request.getHeader(key);
		if (value == null || value.isEmpty()) {
			LoginServiceException error = new LoginServiceException("INVALID_REQUEST", String.format("请求头不包含 %s，请配合客户端 SDK 使用", key));
			writeJson(getJsonForError(error));
			throw error;
		}
		return value;
	}
	
}
