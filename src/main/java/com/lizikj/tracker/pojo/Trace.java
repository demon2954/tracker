package com.lizikj.tracker.pojo;

import java.util.Set;

import com.twitter.zipkin.gen.Span;

/**
 *
 * @auth zone
 * @date 2017-10-17
 */
public class Trace {
	private long traceId;
	private Set<Span> spans;
}
