package com.babar.chat.core.service.grpc.provider;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GrpcServiceProvider {

    private Server server;
    private static final Logger logger = Logger.getLogger(GrpcServiceProvider.class.getName());
    
	@Autowired
	UserServiceImplGrpc userServiceGrpc;
	
	@Autowired
	MessageServiceImplGrpc messageServiceGrpc;
	
	@Value("${babar.chat.grpc.service.port}")
	private int grpcServicePort;

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
            logger.info("Grpc Server started, listening on " + grpcServicePort);

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