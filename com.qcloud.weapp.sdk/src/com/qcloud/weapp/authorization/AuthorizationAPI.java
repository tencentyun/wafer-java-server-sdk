package com.qcloud.weapp.authorization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthorizationAPI {
	private static String APIEndpoint = "http://mina.auth.com:9447/";

	public JSONObject login(String code, String encryptData) throws AuthorizationAPIException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		params.put("encrypt_data", encryptData);
		return request("qcloud.cam.id_skey", params);
	}
	
	public JSONObject checkLogin(String id, String skey) throws AuthorizationAPIException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		params.put("skey", skey);
		return request("qcloud.cam.auth", params);
	}

	public JSONObject request(String apiName, Map<String, Object> apiParams) throws AuthorizationAPIException {
		HttpURLConnection connection = null;
		String requestBody = null;
		String responseBody = null;

		try {
			boolean useDebugProxy = false;
			
			if (useDebugProxy) {
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8888));
				connection = (HttpURLConnection) new URL(APIEndpoint).openConnection(proxy);
			} else {
				connection = (HttpURLConnection) new URL(APIEndpoint).openConnection();
			}
			
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			
			requestBody = buildRequestBody(apiName, apiParams);
			System.out.println("==============Request=============");
			System.out.println(requestBody);

			// send the request
			OutputStreamWriter requestWriter = new OutputStreamWriter(connection.getOutputStream());
			requestWriter.write(requestBody);
			requestWriter.flush();

			// read the response
			BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer responseBuffer = new StringBuffer();
			for (String line; (line = responseReader.readLine()) != null;) {
				responseBuffer.append(line);
			}
			responseBody = responseBuffer.toString();
			System.out.println("==============Response=============");
			System.out.println(responseBody);
			
			requestWriter.close();
			responseReader.close();
		} catch (IOException e) {
			throw new AuthorizationAPIException("连接鉴权服务错误，请检查网络状态");
		}

		JSONObject body = null;
		Integer returnCode = null;
		String returnMessage = null;
		
		try {
			body = new JSONObject(responseBody);
			returnCode = body.getInt("returnCode");
			returnMessage = body.getString("returnMessage");
		} catch (JSONException e) {
			throw new AuthorizationAPIException("调用鉴权服务失败：返回了非法的 JSON 字符串", e);
		}

		if (returnCode != 0) {
			throw new AuthorizationAPIException(String.format("调用鉴权服务失败：#%d - %s", returnCode, returnMessage));
		}
		JSONObject returnData = null;
		try {
			returnData = body.getJSONObject("returnData");
		} catch (JSONException e) {}

		return returnData;
	}

	private String buildRequestBody(String apiName, Map<String, Object> apiParams) {
		JSONObject jsonObject = new JSONObject();
		try {
			JSONObject interfaceJson = new JSONObject();
			interfaceJson.put("interfaceName", apiName);
			interfaceJson.put("para", apiParams);
			
			jsonObject.put("version", 1);
			jsonObject.put("componentName", "MA");
			jsonObject.put("interface", interfaceJson);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject.toString();
	}
}
