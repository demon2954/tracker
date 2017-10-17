package com.lizikj.tracker.filter;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;

/**
 * 服务端日志过滤器
 * @auth zone
 * @date 2017-10-14
 */
@Activate
public class TraceProviderFilter implements Filter {

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		System.out.println(invocation.getAttachments().get("myNum"));
		Result result = invoker.invoke(invocation);
		return result;
	}
}
