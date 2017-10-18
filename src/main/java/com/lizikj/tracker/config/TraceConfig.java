package com.lizikj.tracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 日志追踪 自动配置的相关属性
 * @auth zone
 * @date 2017-10-14
 * 
 */
@ConfigurationProperties
@Component
public class TraceConfig {
	@Value("${dubbo.trace.enabled}")
	private boolean enabled = true;
	
	@Value("${dubbo.trace.connectTimeout}")
	private int connectTimeout;
	
	@Value("${dubbo.trace.readTimeout}")
	private int readTimeout;
	
	@Value("${dubbo.trace.collectUrl}")
	private String collectUrl;

	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public int getConnectTimeout() {
		return connectTimeout;
	}
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
	public int getReadTimeout() {
		return readTimeout;
	}
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}
	public String getCollectUrl() {
		return collectUrl;
	}
	public void setCollectUrl(String collectUrl) {
		this.collectUrl = collectUrl;
	}
}
