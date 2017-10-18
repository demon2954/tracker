package com.lizikj.tracker.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

import com.lizikj.tracker.annotation.EnableTraceAutoConfigurationProperties;
import com.lizikj.tracker.context.TraceContext;

/**
 * 日志追踪自动配置
 * @auth zone
 * @date 2017-10-14
 */
@Configuration
@ConditionalOnBean(annotation = EnableTraceAutoConfigurationProperties.class)
public class EnableTraceAutoConfiguration {

	@Autowired
	private TraceConfig traceConfig;

	@PostConstruct
	public void init() throws Exception {
		TraceContext.setTraceConfig(traceConfig);
	}
}
