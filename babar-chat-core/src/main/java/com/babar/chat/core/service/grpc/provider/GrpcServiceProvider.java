package com.babar.chat.core.service.grpc.provider;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GrpcServiceProvider {

    private Server server;
    private static final Logger logger = Logger.getLogger(GrpcServiceProvider.class.getName());
    
	@Autowired
	UserServiceImplGrpc userServiceGrpc;
	
	@Autowired
	MessageServiceImplGrpc messageServiceGrpc;

    @PostConstruct
    private void start() throws IOException, InterruptedException {
        int port = 50051;
        server = ServerBuilder.forPort(port)
                .addService(new HelloWorldImplGrpc())
                .addService(userServiceGrpc)
                .addService(messageServiceGrpc)
                .build()
                .start();
        logger.info("==> Server started, listening on " + port);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                GrpcServiceProvider.this.stop();
                System.err.println("*** server shut down");
            }
        });
        
        this.blockUntilShutdown();
    }

    @PreDestroy
    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}