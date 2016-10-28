package com.qcloud.weapp.tunnel;

import com.qcloud.weapp.ConfigurationException;
import com.qcloud.weapp.ConfigurationManager;
import com.qcloud.weapp.Hash;

class TunnelClient {
	private static String _id = null;
	public static String getId() throws ConfigurationException {
		if (_id == null) {
			_id = Hash.md5(ConfigurationManager.getCurrentConfiguration().getServerHost());
		}
		return _id;
	}
	
	public static String getKey() throws ConfigurationException {
		return ConfigurationManager.getCurrentConfiguration().getTunnelSignatureKey();
	}
}
