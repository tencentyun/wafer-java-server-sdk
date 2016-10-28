package com.qcloud.weapp.tunnel;

import java.util.ArrayList;
import java.util.function.Predicate;

/**
 * 房间维护一批信道的集合，可以通过广播方法向房间里的所有信道推送消息
 * */
public class TunnelRoom {
	private ArrayList<Tunnel> tunnels;
	
	/**
	 * 实例化一个信道房间，初始包含指定的信道集合
	 * */
	public TunnelRoom(ArrayList<Tunnel> tunnels) {
		if (tunnels == null) {
			tunnels = new ArrayList<Tunnel>();
		}
		this.tunnels = tunnels;
	}
	
	/**
	 * 实例化一个信道房间，初始不包含任何信道
	 * */
	public TunnelRoom() {
		this(new ArrayList<Tunnel>());
	}
	
	/**
	 * 向房间里添加信道
	 * @param tunnel 要添加的信道
	 * */
	public void addTunnel(Tunnel tunnel) {
		tunnels.add(tunnel);
	}
	
	/**
	 * 从房间移除指定的信道
	 * @param tunnel 要移除的信道
	 * */
	public void removeTunnel(Tunnel tunnel) {
		removeTunnelById(tunnel.getTunnelId());
	}
	
	/**
	 * 从房间里移除具有指定 ID 的信道
	 * @param tunnelId 要移除的信道的 ID
	 * */
	public void removeTunnelById (final String tunnelId) {
		tunnels.removeIf(new Predicate<Tunnel>() {
			@Override
			public boolean test(Tunnel t) {
				return t.getTunnelId().equals(tunnelId);
			}
		});
	}
	
	/**
	 * 获取房间里信道的数量
	 * */
	public int getTunnelCount() {
		return tunnels.size();
	}
	
	/**
	 * 向房间里的每一个信道广播消息
	 * @param messageType 要广播的消息的类型
	 * @param messageContent 要广播的消息的内容
	 * */
	public EmitResult broadcast(String messageType, Object messageContent) throws EmitError {
		TunnelAPI api = new TunnelAPI();
		return api.emitMessage(tunnels.toArray(new Tunnel[] {}), messageType, messageContent);
	}
}
