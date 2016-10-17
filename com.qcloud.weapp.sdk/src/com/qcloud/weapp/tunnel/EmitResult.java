package com.qcloud.weapp.tunnel;

import java.util.ArrayList;

public class EmitResult {
	private ArrayList<TunnelInvalidInfo> tunnelInvalidInfos;
	
	public EmitResult(ArrayList<TunnelInvalidInfo> tunnelInvalidInfos) {
		this.tunnelInvalidInfos = tunnelInvalidInfos;
	}

	public ArrayList<TunnelInvalidInfo> getTunnelInvalidInfos() {
		return tunnelInvalidInfos;
	}
}
