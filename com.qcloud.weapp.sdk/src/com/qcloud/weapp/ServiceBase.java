package com.qcloud.weapp;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.qcloud.weapp.authorization.Constants;
import com.qcloud.weapp.authorization.LoginServiceException;

public class ServiceBase {

	protected HttpServletRequest request;
	protected HttpServletResponse response;
	
	protected ServiceBase(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}
	

	protected void writeJson(JSONObject json) {
		try {
			this.response.setContentType("application/json");
			this.response.setCharacterEncoding("utf-8");
			this.response.getWriter().print(json.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected JSONObject prepareResponseJson() {
		JSONObject json = new JSONObject();
		try {
			json.put(Constants.WX_SESSION_MAGIC_ID, 1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	protected JSONObject getJsonForError(Exception error) {
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
