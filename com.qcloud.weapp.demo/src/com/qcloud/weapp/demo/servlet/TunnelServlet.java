package com.qcloud.weapp.demo.servlet;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.qcloud.weapp.ConfigurationException;
import com.qcloud.weapp.authorization.UserInfo;
import com.qcloud.weapp.tunnel.Tunnel;
import com.qcloud.weapp.tunnel.TunnelHandleOptions;
import com.qcloud.weapp.tunnel.TunnelHandler;
import com.qcloud.weapp.tunnel.TunnelMessage;
import com.qcloud.weapp.tunnel.TunnelRoom;
import com.qcloud.weapp.tunnel.TunnelService;

/**
 * Servlet implementation class TunnelServlet
 */
@WebServlet("/tunnel")
public class TunnelServlet extends HttpServlet {
    private static final long serialVersionUID = -6490955903032763981L;

    private static HashMap<String, UserInfo> userMap = new HashMap<String, UserInfo>();
    private static TunnelRoom room = new TunnelRoom();
    		
	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		TunnelService tunnelService = new TunnelService(request, response);
		TunnelHandleOptions options = new TunnelHandleOptions();
		
		options.setCheckLogin(true);
		
		try {
			tunnelService.handle(new TunnelHandler() {
				
				@Override
				public void OnTunnelRequest(Tunnel tunnel, UserInfo userInfo) {
					if (tunnel.getTunnelId() == "test") {
						userInfo = new UserInfo();
					}
					if (userInfo != null) {
						userMap.put(tunnel.getTunnelId(), userInfo);
					}
					System.out.println(String.format("Tunnel Connected: %s", tunnel.getTunnelId()));
				}
				
				@Override
				public void OnTunnelConnect(Tunnel tunnel) {
					if (userMap.containsKey(tunnel.getTunnelId())) {
						room.addTunnel(tunnel);
						JSONObject peopleMessage = new JSONObject();
						try {
							peopleMessage.put("total", room.getTunnelCount());
							peopleMessage.put("enter", new JSONObject(userMap.get(tunnel.getTunnelId())));
						} catch (JSONException e) {
							e.printStackTrace();
						}
						room.broadcast("people", peopleMessage);
					} else {
						tunnel.close();
					}
				}
				
				@Override
				public void OnTunnelMessage(Tunnel tunnel, TunnelMessage message) {
					if (message.getType().equals("speak") && userMap.containsKey(tunnel.getTunnelId())) {
						JSONObject speakMessage = new JSONObject();
						try {
							speakMessage.put("word", message.getContent().getString("word"));
							speakMessage.put("who", new JSONObject(userMap.get(tunnel.getTunnelId())));
						} catch (JSONException e) {
							e.printStackTrace();
						}
						room.broadcast("speak", speakMessage);
					} else {
						tunnel.close();
					}
					
				}
				
				@Override
				public void OnTunnelClose(Tunnel tunnel) {
					UserInfo leaveUser = null;
					if (userMap.containsKey(tunnel.getTunnelId())) {
						leaveUser = userMap.get(tunnel.getTunnelId());
						userMap.remove(tunnel.getTunnelId());
					}
					room.removeTunnel(tunnel);
					JSONObject peopleMessage = new JSONObject();
					try {
						peopleMessage.put("total", room.getTunnelCount());
						peopleMessage.put("leave", new JSONObject(leaveUser));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					room.broadcast("people", peopleMessage);
				}
			}, options);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
}
