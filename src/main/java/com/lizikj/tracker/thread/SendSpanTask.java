package com.lizikj.tracker.thread;

import java.util.List;

import com.lizikj.tracker.SpanManager;
import com.lizikj.tracker.context.TraceContext;
import com.lizikj.tracker.pojo.Span;
import com.lizikj.tracker.utils.HttpClientUtils;
import com.lizikj.tracker.utils.JsonUtils;

/**
 *
 * @auth zone
 * @date 2017-10-18
 */
public class SendSpanTask implements Runnable {

	@Override
	public void run() {
		String collectUrl = TraceContext.getTraceConfig().getCollectUrl();
		List<Span> spanList = SpanManager.get();
		String content = JsonUtils.toJSONString(spanList);
		HttpClientUtils.doPost(collectUrl, content);
	}

}
