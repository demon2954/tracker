package com.lizikj.tracker;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

import com.lizikj.tracker.annotation.EnableTraceAutoConfigurationProperties;


/**
 *
 * @auth zone
 * @date 2017-10-18
 */
@SpringBootApplication
@ImportResource({ "classpath:xml/xxxxxx.xml" })
@ComponentScan(basePackages = { "com.lizikj.tracker" })
@EnableTraceAutoConfigurationProperties
public class Bootstrap {
	public static void main(String[] args) {
		ApplicationContext ctx = new SpringApplicationBuilder().sources(Bootstrap.class).web(false).run(args);
	}
}
