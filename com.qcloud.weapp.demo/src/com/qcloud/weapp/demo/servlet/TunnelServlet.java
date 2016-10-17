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
import com.qcloud.weapp.Logger;
import com.qcloud.weapp.authorization.UserInfo;
import com.qcloud.weapp.tunnel.EmitError;
import com.qcloud.weapp.tunnel.EmitResult;
import com.qcloud.weapp.tunnel.Tunnel;
import com.qcloud.weapp.tunnel.TunnelHandleOptions;
import com.qcloud.weapp.tunnel.TunnelHandler;
import com.qcloud.weapp.tunnel.TunnelInvalidInfo;
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
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		TunnelService tunnelService = new TunnelService(request, response);
		TunnelHandleOptions options = new TunnelHandleOptions();

		options.setCheckLogin(true);

		try {
			tunnelService.handle(new TunnelHandler() {

				@Override
				public void onTunnelRequest(Tunnel tunnel, UserInfo userInfo) {
					if (tunnel.getTunnelId() == "test") {
						userInfo = new UserInfo();
					}
					if (userInfo != null) {
						userMap.put(tunnel.getTunnelId(), userInfo);
					}
					System.out.println(String.format("Tunnel Connected: %s", tunnel.getTunnelId()));
				}

				@Override
				public void onTunnelConnect(Tunnel tunnel) {
					if (userMap.containsKey(tunnel.getTunnelId())) {
						room.addTunnel(tunnel);
						JSONObject peopleMessage = new JSONObject();
						try {
							peopleMessage.put("total", room.getTunnelCount());
							peopleMessage.put("enter", new JSONObject(userMap.get(tunnel.getTunnelId())));
						} catch (JSONException e) {
							e.printStackTrace();
						}
						broadcast("people", peopleMessage);
					} else {
						closeTunnel(tunnel);
					}
				}

				@Override
				public void onTunnelMessage(Tunnel tunnel, TunnelMessage message) {
					if (message.getType().equals("speak") && userMap.containsKey(tunnel.getTunnelId())) {
						JSONObject speakMessage = new JSONObject();
						try {
							speakMessage.put("word", message.getContent().getString("word"));
							speakMessage.put("who", new JSONObject(userMap.get(tunnel.getTunnelId())));
						} catch (JSONException e) {
							e.printStackTrace();
						}
						broadcast("speak", speakMessage);
					} else {
						closeTunnel(tunnel);
					}

				}

				@Override
				public void onTunnelClose(Tunnel tunnel) {
					Logger.log("onTunnelClose()");
					UserInfo leaveUser = null;
					if (userMap.containsKey(tunnel.getTunnelId())) {
						Logger.log("contains()");
						leaveUser = userMap.get(tunnel.getTunnelId());
						userMap.remove(tunnel.getTunnelId());
					}
					room.removeTunnel(tunnel);
					JSONObject peopleMessage = new JSONObject();
					try {
						peopleMessage.put("total", room.getTunnelCount());
						peopleMessage.put("leave", new JSONObject(leaveUser));
					} catch (JSONException e) {
						Logger.log("error: " + e.getMessage());
						e.printStackTrace();
					}
					broadcast("people", peopleMessage);
				}

				private void closeTunnel(Tunnel tunnel) {
					try {
						tunnel.close();
					} catch (EmitError e) {
						e.printStackTrace();
					}
				}

				private void broadcast(String messageType, JSONObject messageContent) {
					try {
						EmitResult result = room.broadcast(messageType, messageContent);
						for (TunnelInvalidInfo invalidInfo : result.getTunnelInvalidInfos()) {
							onTunnelClose(Tunnel.getById(invalidInfo.getTunnelId()));
						}
					} catch (EmitError e) {
						Logger.log("broadcast error: " + e.getMessage());
						// 如果消息发送发生异常，这里可以进行错误处理或者重试的逻辑
						e.printStackTrace();
					}
				}
			}, options);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
}
