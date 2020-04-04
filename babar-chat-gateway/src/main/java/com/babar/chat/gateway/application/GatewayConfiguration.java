package com.babar.chat.gateway.application;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.babar.chat.core.generate.MessageServiceGrpc;
import com.babar.chat.core.generate.MessageServiceGrpc.MessageServiceBlockingStub;
import com.babar.chat.gateway.service.MessageService;
import com.babar.chat.gateway.service.impl.MessageServiceGrpcImpl;
import com.babar.chat.gateway.util.EnhancedThreadFactory;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Configuration
public class GatewayConfiguration {

	@Value("${babar.chat.grpc.service.host}")
	private String grpcServiceHost;

	@Value("${babar.chat.grpc.service.port}")
	private int grpcServicePort;
	
	@Value("${babar.chat.websocket.ackThreads}")
	private int ackThreads;

	@Bean
	public ManagedChannel channel() {
		return ManagedChannelBuilder.forAddress(grpcServiceHost, grpcServicePort).usePlaintext().build();
	}

	@Bean
	public MessageServiceBlockingStub messageServiceBlockingStub() {
		return MessageServiceGrpc.newBlockingStub(channel());
	}

	@Bean
	public MessageService messageServiceGrpc() {
		return new MessageServiceGrpcImpl();
	}

	@Bean
	public ScheduledExecutorService scheduledExecutorService() {
		return new ScheduledThreadPoolExecutor(ackThreads, new EnhancedThreadFactory("ackCheckingThreadPool"));
	}

	@PreDestroy
	public void shutdown() throws InterruptedException {
		channel().shutdown().awaitTermination(5, TimeUnit.SECONDS);
	}
}