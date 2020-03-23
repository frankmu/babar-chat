package com.babar.chat.core.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@Component
public class EmbededRedis {

    @Value("${spring.redis.port}")
    private int redisPort;

    private RedisServer redisServer;

    @PostConstruct
    public void startRedis() throws IOException {
		if (redisServer == null || !redisServer.isActive()) {
			redisServer = new RedisServer(redisPort);
			redisServer.start();
		}
    }

    @PreDestroy
    public void stopRedis() {
        redisServer.stop();
    }
}
