package com.qcloud.weapp.demo;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.qcloud.weapp.authorization.LoginService;
import com.qcloud.weapp.authorization.LoginServiceException;

/**
 * Servlet implementation class UserServlet
 */
@WebServlet("/user")
public class UserServlet extends HttpServlet {
       
	private static final long serialVersionUID = 6579706670441711811L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LoginService service = new LoginService(request, response);
		try {
			JSONObject userInfo = service.checkLogin();
			System.out.println("========= CheckLoginSuccess, UserInfo: ==========");
			System.out.println(userInfo.toString());
			response.setContentType("application/json");
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(userInfo.toString());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (LoginServiceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
