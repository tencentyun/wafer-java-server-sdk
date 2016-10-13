package com.qcloud.weapp.demo;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.qcloud.weapp.authorization.LoginService;
import com.qcloud.weapp.authorization.LoginServiceException;
import com.qcloud.weapp.authorization.UserInfo;

/**
 * Servlet implementation class UserServlet
 */
@WebServlet("/user")
public class UserServlet extends HttpServlet {
       
	private static final long serialVersionUID = 6579706670441711811L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LoginService service = new LoginService(request, response);		
		try {
			UserInfo userInfo = service.check();
			System.out.println("========= CheckLoginSuccess, UserInfo: ==========");
			System.out.println(userInfo.toString());
			response.setContentType("application/json");
			response.setCharacterEncoding("utf-8");
			
			JSONObject result = new JSONObject();
			JSONObject data = new JSONObject();
			data.put("userInfo", new JSONObject(userInfo));
			result.put("code", 0);
			result.put("message", "OK");
			result.put("data", data);
			response.getWriter().write(result.toString());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (LoginServiceException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
