package com.qcloud.weapp.demo;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

/**
 * Servlet implementation class Startup
 */
@WebServlet(name="Startup", urlPatterns = {}, loadOnStartup = 1)
public class Startup extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see Servlet#init(ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		QCloud.setupSDK();
	}
}
