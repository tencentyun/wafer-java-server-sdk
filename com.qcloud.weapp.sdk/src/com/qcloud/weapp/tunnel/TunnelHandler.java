package com.qcloud.weapp.tunnel;

import com.qcloud.weapp.authorization.UserInfo;

/**
 * <p>信道事件处理接口，实现该接口处理信道事件。</p>
 * <p>信道处理器需要处理信道的完整声明周期，包括：</p>
 * <ul>
 *     <li>onTunnelRequest() - 当用户发起信道请求的时候，会得到用户信息，此时可以关联信道 ID 和用户信息</li>
 *     <li>onTunnelConnect() - 当用户建立了信道连接之后，可以记录下已经连接的信道</li>
 *     <li>onTunnelMessage() - 当用户消息发送到信道上时，使用该函数处理信道的消息</li>
 *     <li>onTunnelClose() - 当信道关闭时，清理关于该信道的信息，以及回收相关资源</li>
 * </ul>
 * */
public interface TunnelHandler {
	/**
	 * 当用户发起信道请求的时候调用，会得到用户信息，此时可以关联信道 ID 和用户信息
	 * @param tunnel 发起连接请求的信道
	 * @param userInfo 发起连接对应的用户（需要信道服务配置 checkLogin 为 true）
	 * */
	void onTunnelRequest(Tunnel tunnel, UserInfo userInfo);
	
	/**
	 * 当用户建立了信道连接之后调用，此时可以记录下已经连接的信道
	 * @param tunnel 已经建立连接的信道，此时可以向信道发送消息
	 * */
	void onTunnelConnect(Tunnel tunnel);
	
	/**
	 * 当信道收到消息时调用，此时可以处理消息，也可以向信道发送消息
	 * @param tunnel 收到消息的信道
	 * @param message 收到的消息
	 * */
	void onTunnelMessage(Tunnel tunnel, TunnelMessage message);
	
	/**
	 * 当信道关闭的时候调用，此时可以清理信道使用的资源
	 * @param tunnel 已经关闭的信道
	 * */
	void onTunnelClose(Tunnel tunnel);
}
