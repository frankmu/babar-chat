package com.babar.chat.core.service.grpc.provider;

import com.babar.chat.core.generate.GreeterGrpc.GreeterImplBase;
import com.babar.chat.core.generate.HelloReply;
import com.babar.chat.core.generate.HelloRequest;

import io.grpc.stub.StreamObserver;

import org.springframework.stereotype.Component;

@Component
public class HelloWorldImplGrpc extends GreeterImplBase {

	@Override
	public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}