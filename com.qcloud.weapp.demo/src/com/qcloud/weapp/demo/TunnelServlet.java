package com.qcloud.weapp.demo;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.qcloud.weapp.authorization.UserInfo;
import com.qcloud.weapp.tunnel.Tunnel;
import com.qcloud.weapp.tunnel.TunnelHandleOptions;
import com.qcloud.weapp.tunnel.TunnelHandler;
import com.qcloud.weapp.tunnel.TunnelMessage;
import com.qcloud.weapp.tunnel.TunnelService;

/**
 * Servlet implementation class TunnelServlet
 */
@WebServlet("/tunnel")
public class TunnelServlet extends HttpServlet {
    private static final long serialVersionUID = -6490955903032763981L;

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		TunnelService tunnelService = new TunnelService(request, response);
		TunnelHandleOptions options = new TunnelHandleOptions();
		
		options.setCheckLogin(true);
		
		tunnelService.handle(new TunnelHandler() {
			
			@Override
			public void OnTunnelRequest(Tunnel tunnel, UserInfo userInfo) {
				System.out.println(String.format("Tunnel Connected: %s", tunnel.getTunnelId()));
			}
			
			@Override
			public void OnTunnelMessage(Tunnel tunnel, TunnelMessage message) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void OnTunnelConnect(Tunnel tunnel) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void OnTunnelClose(Tunnel tunnel) {
				// TODO Auto-generated method stub
				
			}
		}, options);
	}
}
