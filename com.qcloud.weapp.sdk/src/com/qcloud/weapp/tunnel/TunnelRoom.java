package com.qcloud.weapp.tunnel;

import java.util.ArrayList;
import java.util.function.Predicate;

public class TunnelRoom {
	private ArrayList<Tunnel> tunnels;
	
	public TunnelRoom(ArrayList<Tunnel> tunnels) {
		if (tunnels == null) {
			tunnels = new ArrayList<Tunnel>();
		}
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
				return t.getTunnelId().equals(tunnelId);
			}
		});
	}
	
	public int getTunnelCount() {
		return tunnels.size();
	}
	
	public EmitResult broadcast(String messageType, Object messageContent) throws EmitError {
		TunnelAPI api = new TunnelAPI();
		return api.emitMessage(tunnels.toArray(new Tunnel[] {}), messageType, messageContent);
	}
}
