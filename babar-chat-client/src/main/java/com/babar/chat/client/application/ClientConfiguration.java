package com.babar.chat.client.application;

import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.babar.chat.client.grpc.consumer.MessageServiceGrpcImpl;
import com.babar.chat.client.grpc.consumer.UserServiceGrpcImpl;
import com.babar.chat.client.service.MessageService;
import com.babar.chat.client.service.UserService;
import com.babar.chat.client.service.impl.MessageServiceRestImpl;
import com.babar.chat.client.service.impl.UserServiceRestImpl;
import com.babar.chat.core.generate.MessageServiceGrpc;
import com.babar.chat.core.generate.MessageServiceGrpc.MessageServiceBlockingStub;
import com.babar.chat.core.generate.UserServiceGrpc;
import com.babar.chat.core.generate.UserServiceGrpc.UserServiceBlockingStub;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Configuration
public class ClientConfiguration {
	
	@Value("${babar.chat.rest.service.url}")
	private String restServiceUri;
	
	@Value("${babar.chat.grpc.service.host}")
	private String grpcServiceHost;
	
	@Value("${babar.chat.grpc.service.port}")
	private int grpcServicePort;
	
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.rootUri(restServiceUri).build();
	}
	
	@Bean
	public ManagedChannel channel() {
		return ManagedChannelBuilder.forAddress(grpcServiceHost, grpcServicePort).usePlaintext().build();
	}
	
	@Bean
	public UserServiceBlockingStub userServiceBlockingStub() {
		return UserServiceGrpc.newBlockingStub(channel());
	}
	
	@Bean
	public MessageServiceBlockingStub messageServiceBlockingStub() {
		return MessageServiceGrpc.newBlockingStub(channel());
	}
	
	@Bean
	@ConditionalOnProperty(name = "babar.chat.service.type", havingValue = "rest", matchIfMissing = true)
	public MessageService messageServiceRest() {
		return new MessageServiceRestImpl();
	}
	
	@Bean
	@ConditionalOnProperty(name = "babar.chat.service.type", havingValue = "grpc")
	public MessageService messageServiceGrpc() {
		return new MessageServiceGrpcImpl();
	}
	
	@Bean
	@ConditionalOnProperty(name = "babar.chat.service.type", havingValue = "rest", matchIfMissing = true)
	public UserService userServiceRest() {
		return new UserServiceRestImpl();
	}
	
	@Bean
	@ConditionalOnProperty(name = "babar.chat.service.type", havingValue = "grpc")
	public UserService userServiceGrpc() {
		return new UserServiceGrpcImpl();
	}
	
	@PreDestroy
	public void shutdown() throws InterruptedException {
		channel().shutdown().awaitTermination(5, TimeUnit.SECONDS);
	}
}