package com.babar.chat.gateway;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.babar.chat.gateway.handler.CloseIdleChannelHandler;
import com.babar.chat.gateway.handler.WebsocketRouterHandler;
import com.babar.chat.gateway.util.EnhancedThreadFactory;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class WebSocketServer {

	@Autowired
	private WebSocketConfig serverConfig;

	@Autowired
	private WebsocketRouterHandler websocketRouterHandler;

	@Autowired
	private CloseIdleChannelHandler closeIdleChannelHandler;

	private ServerBootstrap bootstrap;
	private ChannelFuture channelFuture;

	@PostConstruct
	public void start() throws InterruptedException {
		if (serverConfig.port == 0) {
			log.info("WebSocket Server not config.");
			return;
		}

		log.info("WebSocket Server is starting");
		EventExecutorGroup eventExecutorGroup = new DefaultEventExecutorGroup(serverConfig.handlerThreads,
				new EnhancedThreadFactory("WebSocketThreadPool"));

		ChannelInitializer<SocketChannel> initializer = new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				// Add websocket related codec and protocol
				pipeline.addLast(new HttpServerCodec());
				pipeline.addLast(new HttpObjectAggregator(65536));
				pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
				pipeline.addLast(new WebSocketServerProtocolHandler("/", null, true));
				// Add application logic handler with a customized thread pool
				pipeline.addLast(eventExecutorGroup, websocketRouterHandler);
				// Add the idel processor
				pipeline.addLast(new IdleStateHandler(0, 0, serverConfig.allIdleSecond));
				pipeline.addLast(closeIdleChannelHandler);
			}
		};

		bootstrap = newServerBootstrap();
		bootstrap.childHandler(initializer);
		channelFuture = bootstrap.bind(serverConfig.port).sync();

		Runtime.getRuntime().addShutdownHook(new ShutdownThread());

		log.info("WebSocket Server start successfully on: " + serverConfig.port);

		new Thread() {
			@Override
			public void run() {
				try {
					channelFuture.channel().closeFuture().sync();
				} catch (Exception e) {
					log.error("WebSocket Server start failed!", e);
				}
			}
		}.start();
	}

	class ShutdownThread extends Thread {
		@Override
		public void run() {
			close();
		}
	}

	public void close() {
		if (bootstrap == null) {
			log.info("WebSocket server is not running!");
			return;
		}

		log.info("WebSocket server is stopping");
		if (channelFuture != null) {
			channelFuture.channel().close().awaitUninterruptibly(10, TimeUnit.SECONDS);
			channelFuture = null;
		}
		if (bootstrap != null && bootstrap.config().group() != null) {
			bootstrap.config().group().shutdownGracefully();
		}
		if (bootstrap != null && bootstrap.config().childGroup() != null) {
			bootstrap.config().childGroup().shutdownGracefully();
		}
		bootstrap = null;

		log.info("WebSocket server stopped");
	}

	// Use EpollEventLoopGroup if epoll is supported, else use NioServerSocketChannel
	private ServerBootstrap newServerBootstrap() {
		if (Epoll.isAvailable() && serverConfig.useEpoll) {
			EventLoopGroup bossGroup = new EpollEventLoopGroup(serverConfig.bossThreads,
					new DefaultThreadFactory("WebSocketBossGroup", true));
			EventLoopGroup workerGroup = new EpollEventLoopGroup(serverConfig.workerThreads,
					new DefaultThreadFactory("WebSocketWorkerGroup", true));
			return new ServerBootstrap().group(bossGroup, workerGroup).channel(EpollServerSocketChannel.class);
		} else {
			EventLoopGroup bossGroup;
			EventLoopGroup workerGroup;
			if (serverConfig.bossThreads >= 0 && serverConfig.workerThreads >= 0) {
				bossGroup = new NioEventLoopGroup(serverConfig.bossThreads);
				workerGroup = new NioEventLoopGroup(serverConfig.workerThreads);
			} else {
				bossGroup = new NioEventLoopGroup();
				workerGroup = new NioEventLoopGroup();
			}
			return new ServerBootstrap().group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
		}
	}
}
