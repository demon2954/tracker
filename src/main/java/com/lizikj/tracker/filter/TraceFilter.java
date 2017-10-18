package com.lizikj.tracker.filter;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.lizikj.tracker.context.TraceContext;
import com.lizikj.tracker.pojo.Annotation;
import com.lizikj.tracker.pojo.Endpoint;
import com.lizikj.tracker.pojo.Span;
import com.lizikj.tracker.thread.SpanSender;
import com.lizikj.tracker.utils.IdUtils;
import com.lizikj.tracker.utils.NetworkUtils;
import com.lizikj.tracker.utils.RpcContextUtils;
import com.lizikj.tracker.utils.SpanManager;

/**
 * 消费端日志过滤器
 * @auth zone
 * @date 2017-10-14
 */
@Activate
public class TraceFilter implements Filter {
	Logger logger = Logger.getLogger(TraceFilter.class);

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		Span span = null;
		try {
			span = startTrace(invocation);
		} catch (Exception e) {
			if(logger.isErrorEnabled()) {
				logger.error("TraceFilter Error on start",e);
			}
		}
		Result result = invoker.invoke(invocation);
		try {
			if (span != null) {
				endTrace(span);
			}
		} catch (Exception e) {
			if(logger.isErrorEnabled()) {
				logger.error("TraceFilter Error on end",e);
			}
		}
		return result;
	}

	/**
	 * 开始追踪
	 * @param invocation
	 * @return
	 */
	private Span startTrace(Invocation invocation) {
		Span span = null;
		if (RpcContextUtils.isConsumer()) {// 消费者
			span = makeConsumerSpan(invocation);
		} else if (RpcContextUtils.isProvider()) {// 生产者
			span = makeProviderSpan(invocation);
		}
		return span;
	}

	/**
	 * 结束追踪
	 * @param span
	 */
	private void endTrace(Span span) {
		long now = System.currentTimeMillis();
		int port = RpcContextUtils.getPort();
		int ipv4 = NetworkUtils.ip2Num(NetworkUtils.getSiteIp());
		Endpoint endpoint = Endpoint.create(span.getName(), ipv4, port);
		Annotation annotation = null;
		if (RpcContextUtils.isConsumer()) {// 消费者
			annotation = Annotation.create(now, TraceContext.ANNO_CR, endpoint);
		} else if (RpcContextUtils.isProvider()) {// 生产者
			annotation = Annotation.create(now, TraceContext.ANNO_SS, endpoint);
		}
		span.addToAnnotations(annotation);
		span.setDuration(now - span.getTimestamp());
		SpanManager.add(span);
		SpanSender sender = SpanSender.init();
		sender.send();
	}

	/**
	 * 生成消费者span
	 * @param invocation
	 * @return
	 */
	private Span makeConsumerSpan(Invocation invocation) {
		Span span = new Span();
		Map<String, String> attachments = invocation.getAttachments();
		long now = System.currentTimeMillis();
		String methodName = invocation.getMethodName();// 调用的方法名
		String interfaceName = RpcContextUtils.getParameter("interface");// facade全名
		int port = RpcContextUtils.getPort();// 端口
		interfaceName = interfaceName.substring(interfaceName.lastIndexOf(".") + 1);// facade名
		String name = "consumer." + interfaceName + "." + methodName;

		long traceId = 0;
		long spanId = 0;
		long parentId = 0;
		if (StringUtils.isBlank(attachments.get(TraceContext.TRACE_ID_MARK)) && RpcContextUtils.get(TraceContext.TRACE_ID_MARK) == null) {// 新的trace
			traceId = IdUtils.get();
			spanId = traceId;
		} else {
			parentId = attachments.get(TraceContext.SPAN_ID_MARK) == null ? Long.parseLong(RpcContextUtils.get(TraceContext.SPAN_ID_MARK) + "")
					: Long.parseLong(attachments.get(TraceContext.SPAN_ID_MARK));
			traceId = attachments.get(TraceContext.TRACE_ID_MARK) == null ? Long.parseLong(RpcContextUtils.get(TraceContext.TRACE_ID_MARK) + "")
					: Long.parseLong(attachments.get(TraceContext.TRACE_ID_MARK));
			spanId = IdUtils.get();
		}

		span.setId(spanId);
		span.setName(name);
		span.setParent_id(parentId);
		span.setTimestamp(now);
		span.setTrace_id(traceId);

		int ipv4 = NetworkUtils.ip2Num(NetworkUtils.getSiteIp());
		Endpoint endpoint = Endpoint.create(name, ipv4, port);
		Annotation annotation = Annotation.create(now, TraceContext.ANNO_CS, endpoint);
		span.addToAnnotations(annotation);

		RpcContextUtils.put(TraceContext.TRACE_ID_MARK, traceId + "");
		RpcContextUtils.put(TraceContext.SPAN_ID_MARK, spanId + "");
		attachments.put(TraceContext.TRACE_ID_MARK, traceId + "");
		attachments.put(TraceContext.SPAN_ID_MARK, spanId + "");
		return span;
	}

	/**
	 * 生成生产者span
	 * @param invocation
	 * @return
	 */
	private Span makeProviderSpan(Invocation invocation) {
		Span span = new Span();
		Map<String, String> attachments = invocation.getAttachments();
		long now = System.currentTimeMillis();
		String methodName = invocation.getMethodName();// 调用的方法名
		String interfaceName = RpcContextUtils.getParameter("interface");// facade全名
		int port = RpcContextUtils.getPort();// 端口
		interfaceName = interfaceName.substring(interfaceName.lastIndexOf(".") + 1);// facade名
		String name = "provider." + interfaceName + "." + methodName;

		long traceId = 0;
		long spanId = 0;
		long parentId = 0;
		if (StringUtils.isBlank(attachments.get(TraceContext.TRACE_ID_MARK))) {// 新的trace
			traceId = IdUtils.get();
			spanId = traceId;
		} else {
			parentId = attachments.get(TraceContext.SPAN_ID_MARK) == null ? Long.parseLong(RpcContextUtils.get(TraceContext.SPAN_ID_MARK) + "")
					: Long.parseLong(attachments.get(TraceContext.SPAN_ID_MARK));
			traceId = attachments.get(TraceContext.TRACE_ID_MARK) == null ? Long.parseLong(RpcContextUtils.get(TraceContext.TRACE_ID_MARK) + "")
					: Long.parseLong(attachments.get(TraceContext.TRACE_ID_MARK));
			spanId = IdUtils.get();
		}

		span.setId(spanId);
		span.setName(name);
		span.setParent_id(parentId);
		span.setTimestamp(now);
		span.setTrace_id(traceId);

		int ipv4 = NetworkUtils.ip2Num(NetworkUtils.getSiteIp());
		Endpoint endpoint = Endpoint.create(name, ipv4, port);
		Annotation annotation = Annotation.create(now, TraceContext.ANNO_SR, endpoint);
		span.addToAnnotations(annotation);

		attachments.put(TraceContext.TRACE_ID_MARK, traceId + "");
		attachments.put(TraceContext.SPAN_ID_MARK, spanId + "");
		return span;
	}
}