package com.lizikj.tracker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.lizikj.tracker.pojo.Span;
import com.lizikj.tracker.thread.SpanSender;

/**
 * span管理者
 * @auth zone
 * @date 2017-10-18
 */
public class SpanManager {
	private final static BlockingQueue<Span> SPAN_QUEUE = new ArrayBlockingQueue<Span>(1000);

	public static void add(Span span) {
		SPAN_QUEUE.offer(span);
		SpanSender.init();
	}

	public static List<Span> get() {
		List<Span> list = new ArrayList<>(SPAN_QUEUE.size());
		SPAN_QUEUE.drainTo(list);
		return list;
	}
}
