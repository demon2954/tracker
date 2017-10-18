package com.lizikj.tracker.context;

import com.lizikj.tracker.config.TraceConfig;

/**
 * 链路跟踪上下文
 * 
 * @auth zone
 * @date 2017-10-17
 */
public class TraceContext {
	private static ThreadLocal<Long> SPAN_ID = new InheritableThreadLocal<>();
	private static ThreadLocal<Long> TRACE_ID = new InheritableThreadLocal<>();

	public static final String ANNO_CS = "cs";
	public static final String ANNO_CR = "cr";
	public static final String ANNO_SR = "sr";
	public static final String ANNO_SS = "ss";

	public static final String TRACE_ID_MARK = "traceId";
	public static final String SPAN_ID_MARK = "spanId";
	
    private static TraceConfig traceConfig;

	public static long getSpanId() {
		return SPAN_ID.get();
	}
	public static void setSpanId(long spanId) {
		SPAN_ID.set(spanId);
	}

	public static long getTraceId() {
		if (TRACE_ID.get() == null) {
			TRACE_ID.set(0L);
		}

		return TRACE_ID.get();
	}
	public static void setTraceId(long TraceId) {
		TRACE_ID.set(TraceId);
	}
	public static TraceConfig getTraceConfig() {
		return traceConfig;
	}
	public static void setTraceConfig(TraceConfig traceConfig) {
		TraceContext.traceConfig = traceConfig;
	}
	
}
