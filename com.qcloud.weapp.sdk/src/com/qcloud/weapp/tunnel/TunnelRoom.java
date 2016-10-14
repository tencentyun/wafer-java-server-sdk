package com.qcloud.weapp.tunnel;

import java.util.ArrayList;
import java.util.function.Predicate;

import org.json.JSONObject;

public class TunnelRoom {
	private ArrayList<Tunnel> tunnels;
	
	public TunnelRoom(ArrayList<Tunnel> tunnels) {
		this.tunnels = tunnels;
	}
	
	public TunnelRoom() {
		this(new ArrayList<Tunnel>());
	}
	
	public void addTunnel(Tunnel tunnel) {
		tunnels.add(tunnel);
	}
	
	public void removeTunnel(Tunnel tunnel) {
		removeTunnelById(tunnel.getTunnelId());
	}
	
	public void removeTunnelById (final String tunnelId) {
		tunnels.removeIf(new Predicate<Tunnel>() {
			@Override
			public boolean test(Tunnel t) {
				return t.getTunnelId() == tunnelId;
			}
		});
	}
	
	public Integer getTunnelCount() {
		return tunnels.size();
	}
	
	public boolean broadcast(String messageType, JSONObject messageContent) {
		if (tunnels != null) {
			TunnelAPI api = new TunnelAPI();
			return api.emitMessage(tunnels.toArray(new Tunnel[] {}), messageType, messageContent);
		}
		return true;
	}
}
