package com.lizikj.tracker.utils;

import java.util.Map;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.RpcContext;

/**
 * RpcContext工具类
 * @auth zone
 * @date 2017-10-14
 */
public class RpcContextUtils {

	public static String getParameter(String name) {
		RpcContext rpcContext = RpcContext.getContext();
		URL url = rpcContext.getUrl();
		return url.getParameter(name);
	}

	public static int getPort() {
		RpcContext rpcContext = RpcContext.getContext();
		URL url = rpcContext.getUrl();
		return url.getPort();
	}

	public static String getHost() {
		RpcContext rpcContext = RpcContext.getContext();
		URL url = rpcContext.getUrl();
		return url.getHost();
	}
	
	public static boolean isConsumer() {
		RpcContext rpcContext = RpcContext.getContext();
		return rpcContext.isConsumerSide();
	}
	
	public static boolean isProvider() {
		RpcContext rpcContext = RpcContext.getContext();
		return rpcContext.isProviderSide();
	}
	
	public static void put(String key, Object value) {
		RpcContext rpcContext = RpcContext.getContext();
		rpcContext.set(key, value);
	}
	
	public static Object get(String key) {
		RpcContext rpcContext = RpcContext.getContext();
		return rpcContext.get(key);
	}
}
