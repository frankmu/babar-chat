package com.babar.chat.core.service.grpc.provider;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GrpcServiceProvider {
    
	@Autowired
	UserServiceImplGrpc userServiceGrpc;
	
	@Autowired
	MessageServiceImplGrpc messageServiceGrpc;
	
	@Value("${babar.chat.grpc.service.port}")
	private int grpcServicePort;
	
    private Server server;

    @PostConstruct
    private void start() throws IOException, InterruptedException {
    	new Thread(() -> {
            try {
				server = ServerBuilder.forPort(grpcServicePort)
				        .addService(userServiceGrpc)
				        .addService(messageServiceGrpc)
				        .build()
				        .start();
			} catch (IOException e) {
				e.printStackTrace();
			}
            log.info("Grpc Server started, listening on " + grpcServicePort);

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    System.err.println("*** shutting down gRPC server since JVM is shutting down");
                    GrpcServiceProvider.this.stop();
                    System.err.println("*** server shut down");
                }
            });
            if (server != null) {
                try {
					server.awaitTermination();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            }
    	}).start();
    }

    @PreDestroy
    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }
}