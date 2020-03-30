package com.babar.chat.client.grpc.consumer;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.babar.chat.core.generate.GreeterGrpc;
import com.babar.chat.core.generate.HelloReply;
import com.babar.chat.core.generate.HelloRequest;
import com.babar.chat.core.generate.LoginRequest;
import com.babar.chat.core.generate.User;
import com.babar.chat.core.generate.UserServiceGrpc;

public class HelloWorldConsumer {

    private final ManagedChannel channel;
    private final GreeterGrpc.GreeterBlockingStub blockingStub;
    private final UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;
    private static final Logger logger = Logger.getLogger(HelloWorldConsumer.class.getName());

    public HelloWorldConsumer(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build());
    }

    HelloWorldConsumer(ManagedChannel channel) {
        this.channel = channel;
        blockingStub = GreeterGrpc.newBlockingStub(channel);
        userServiceBlockingStub = UserServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void greet(String name) {
        logger.info("==> Will try to greet " + name + " ...");
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply response;
        try {
            response = blockingStub.sayHello(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("==> Greeting: " + response.getMessage());
    }
    
    public void login(String email, String password) {
    	LoginRequest req = LoginRequest.newBuilder().setEmail(email).setPassword(password).build();
    	User user;
    	try {
    		user = userServiceBlockingStub.login(req);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("==> Greeting: " + user.toString());
    	
    }

    public static void main(String[] args) throws Exception {
        HelloWorldConsumer client = new HelloWorldConsumer("localhost", 50051);
        try {
            String user = "World";
//            client.greet(user);
            client.login("a@gmail.com", "1234");
        } finally {
            client.shutdown();
        }
    }
}