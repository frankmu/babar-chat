package com.babar.chat.gateway.application;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({ "com.babar.chat.gateway" })
public class GatewayStarter {
	public static void main(String[] args) {
		new SpringApplicationBuilder(GatewayStarter.class).logStartupInfo(false).run(args);
	}
}
