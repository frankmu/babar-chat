package com.babar.chat.client.application;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({ "com.babar.chat.client" })
public class ClientApplication {
	public static void main(String[] args) {
		new SpringApplicationBuilder(ClientApplication.class).logStartupInfo(false).run(args);
	}
}
