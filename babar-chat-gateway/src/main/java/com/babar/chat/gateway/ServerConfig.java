package com.babar.chat.gateway;

import org.springframework.context.annotation.PropertySource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;

/**
 * Config for Netty Server
 */
@Component
@PropertySource("classpath:application.yml")
public class ServerConfig {

	@Value("${babar.chat.websocket.port}")
    public int port;
	
	@Value("${babar.chat.websocket.useEpoll}")
    public boolean useEpoll;
	
	@Value("${babar.chat.websocket.useMemPool}")
    public boolean useMemPool;
	
	@Value("${babar.chat.websocket.useDirectBuffer}")
    public boolean useDirectBuffer;
	
	@Value("${babar.chat.websocket.bossThreads}")
    public int bossThreads;
	
	@Value("${babar.chat.websocket.workerThreads}")
    public int workerThreads;
	
	@Value("${babar.chat.websocket.userThreads}")
    public int userThreads;
	
	@Value("${babar.chat.websocket.connTimeoutMills}")
    public int connTimeoutMills;
	
	@Value("${babar.chat.websocket.soLinger}")
    public int soLinger;
    
    @Value("${babar.chat.websocket.backlog}")
    public int backlog;
    
    @Value("${babar.chat.websocket.reuseAddr}")
    public boolean reuseAddr;
    
    @Value("${babar.chat.websocket.sendBuff}")
    public int sendBuff;
    
    @Value("${babar.chat.websocket.recvBuff}")
    public int recvBuff;
    
    @Value("${babar.chat.websocket.readIdleSecond}")
    public int readIdleSecond;
    
    @Value("${babar.chat.websocket.writeIdleSecond}")
    public int writeIdleSecond;
    
    @Value("${babar.chat.websocket.allIdleSecond}")
    public int allIdleSecond;
    
    @Value("${babar.chat.websocket.readIdleSecond}")
    public int idleTimes;
}
