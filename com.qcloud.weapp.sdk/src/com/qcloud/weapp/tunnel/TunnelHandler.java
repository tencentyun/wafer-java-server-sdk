package com.qcloud.weapp.tunnel;

import com.qcloud.weapp.authorization.UserInfo;

public interface TunnelHandler {
	void onTunnelRequest(Tunnel tunnel, UserInfo userInfo);
	void onTunnelConnect(Tunnel tunnel);
	void onTunnelMessage(Tunnel tunnel, TunnelMessage message);
	void onTunnelClose(Tunnel tunnel);
}
