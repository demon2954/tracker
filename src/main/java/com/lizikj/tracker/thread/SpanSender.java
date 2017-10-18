package com.lizikj.tracker.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lizikj.tracker.context.TraceContext;

/**
 * 发送span
 * 
 * @auth zone
 * @date 2017-10-18
 */
public class SpanSender {
	private static ExecutorService fixedThreadPool = null;
	private static SpanSender spanSender = null;

	private SpanSender() {
	}

	public static SpanSender init() {
		if (spanSender == null) {
			spanSender = new SpanSender();
		}
		return spanSender;
	}

	public void send() {
		if (fixedThreadPool == null) {
			fixedThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			fixedThreadPool.execute(new SendSpanTask());
		}
	}
}
