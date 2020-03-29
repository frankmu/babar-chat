package com.babar.chat.client.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ClientConfiguration {
	
	@Value("${babar.chat.service.url}")
	private String serviceUri;
	
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.rootUri(serviceUri).build();
	}
}