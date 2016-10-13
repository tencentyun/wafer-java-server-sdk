package com.qcloud.weapp.tunnel;

import com.qcloud.weapp.authorization.UserInfo;

public interface TunnelHandler {
	void OnTunnelRequest(Tunnel tunnel, UserInfo userInfo);
	void OnTunnelConnect(Tunnel tunnel);
	void OnTunnelMessage(Tunnel tunnel, TunnelMessage message);
	void OnTunnelClose(Tunnel tunnel);
}
