package com.qcloud.weapp.demo;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.qcloud.weapp.authorization.UserInfo;
import com.qcloud.weapp.tunnel.EmitError;
import com.qcloud.weapp.tunnel.EmitResult;
import com.qcloud.weapp.tunnel.Tunnel;
import com.qcloud.weapp.tunnel.TunnelHandler;
import com.qcloud.weapp.tunnel.TunnelInvalidInfo;
import com.qcloud.weapp.tunnel.TunnelMessage;
import com.qcloud.weapp.tunnel.TunnelRoom;

/**
 * <h1>实现 WebSocket 信道处理器</h1>
 * <p>本示例配合客户端 Demo 实现一个简单的聊天室功能</p>
 * 
 * <p>信道处理器需要处理信道的完整声明周期，包括：</p>
 * <ul>
 *     <li>onTunnelRequest() - 当用户发起信道请求的时候，会得到用户信息，此时可以关联信道 ID 和用户信息</li>
 *     <li>onTunnelConnect() - 当用户建立了信道连接之后，可以记录下已经连接的信道</li>
 *     <li>onTunnelMessage() - 当用户消息发送到信道上时，使用该函数处理信道的消息</li>
 *     <li>onTunnelClose() -   当信道关闭时，清理关于该信道的信息，以及回收相关资源</li>
 * </ul>
 * */
public class ChatTunnelHandler implements TunnelHandler {
	
	/**
	 * 记录 WebSocket 信道对应的用户。在实际的业务中，应该使用数据库进行存储跟踪，这里作为示例只是演示其作用
	 * */
	private static HashMap<String, UserInfo> userMap = new HashMap<String, UserInfo>();
	
	/**
	 * 创建一个房间，包含当前已连接的 WebSocket 信道列表
	 * */
	private static TunnelRoom room = new TunnelRoom();

	/**
	 * 实现 OnTunnelRequest 方法<br/>
     * 在客户端请求 WebSocket 信道连接之后，会调用 OnTunnelRequest 方法，此时可以把信道 ID 和用户信息关联起来
	 * */
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

	/**
	 * 实现 OnTunnelConnect 方法<br/>
     * 在客户端成功连接 WebSocket 信道服务之后会调用该方法，此时通知所有其它在线的用户当前总人数以及刚加入的用户是谁
	 * */
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

	/**
	 * 实现 OnTunnelMessage 方法
     *  客户端推送消息到 WebSocket 信道服务器上后，会调用该方法，此时可以处理信道的消息。
     *  在本示例，我们处理 "speak" 类型的消息，该消息表示有用户发言。我们把这个发言的信息广播到所有在线的 WebSocket 信道上
	 * */
	@Override
	public void onTunnelMessage(Tunnel tunnel, TunnelMessage message) {
		if (message.getType().equals("speak") && userMap.containsKey(tunnel.getTunnelId())) {
			JSONObject speakMessage = new JSONObject();
			try {
				JSONObject messageContent = (JSONObject) message.getContent();
				speakMessage.put("word", messageContent.getString("word"));
				speakMessage.put("who", new JSONObject(userMap.get(tunnel.getTunnelId())));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			broadcast("speak", speakMessage);
		} else {
			closeTunnel(tunnel);
		}

	}

	/**
	 * 实现 OnTunnelClose 方法
     * 客户端关闭 WebSocket 信道或者被信道服务器判断为已断开后，会调用该方法，此时可以进行清理及通知操作
	 * */
	@Override
	public void onTunnelClose(Tunnel tunnel) {
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
		broadcast("people", peopleMessage);
	}

	/**
	 * 关闭指定的信道
	 * */
	private void closeTunnel(Tunnel tunnel) {
		try {
			tunnel.close();
		} catch (EmitError e) {
			e.printStackTrace();
		}
	}

	/**
	 * 广播消息到房间里所有的信道
	 * */
	private void broadcast(String messageType, JSONObject messageContent) {
		try {
			EmitResult result = room.broadcast(messageType, messageContent);
			// 广播后发现的无效信道进行清理
			for (TunnelInvalidInfo invalidInfo : result.getTunnelInvalidInfos()) {
				onTunnelClose(Tunnel.getById(invalidInfo.getTunnelId()));
			}
		} catch (EmitError e) {
			// 如果消息发送发生异常，这里可以进行错误处理或者重试的逻辑
			e.printStackTrace();
		}
	}
}
