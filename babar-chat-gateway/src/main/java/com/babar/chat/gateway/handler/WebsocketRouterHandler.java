package com.babar.chat.gateway.handler;

import com.babar.chat.dto.MessageDTO;
import com.babar.chat.gateway.service.MessageService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@ChannelHandler.Sharable
@Component
public class WebsocketRouterHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

	@Autowired
	private MessageService messageService;
	
	@Autowired
	ScheduledExecutorService scheduledExecutorService;
	
	// Lookup table for user <-> channel
	private static final ConcurrentHashMap<Long, Channel> userChannel = new ConcurrentHashMap<>(15000);
	private static final ConcurrentHashMap<Channel, Long> channelUser = new ConcurrentHashMap<>(15000);
	
	private static final AttributeKey<AtomicLong> TID_GENERATOR = AttributeKey.valueOf("tid_generator");
	private static final AttributeKey<ConcurrentHashMap<Long, JsonObject>> NON_ACKED_MAP = AttributeKey.valueOf("non_acked_map");

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
		if (frame instanceof TextWebSocketFrame) {
			String msg = ((TextWebSocketFrame) frame).text();
			JsonObject msgJson = JsonParser.parseString(msg).getAsJsonObject();
			int type = msgJson.get("type").getAsInt();
			JsonObject data = msgJson.get("data").getAsJsonObject();
			switch (type) {
			case 0:// Heart beat
				long uid = data.get("uid").getAsLong();
				long timeout = data.get("timeout").getAsLong();
				log.info("[heartbeat]: uid = {} , current timeout is {} ms, channel = {}", uid, timeout,
						ctx.channel());
				ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":0,\"timeout\":" + timeout + "}"));
				break;
			case 1:// Connect
				long loginUid = data.get("uid").getAsLong();
				userChannel.put(loginUid, ctx.channel());
				channelUser.put(ctx.channel(), loginUid);
				ctx.channel().attr(TID_GENERATOR).set(new AtomicLong(0));
				ctx.channel().attr(NON_ACKED_MAP).set(new ConcurrentHashMap<Long, JsonObject>());
				log.info("[user bind]: uid = {} , channel = {}", loginUid, ctx.channel());
				ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":1,\"status\":\"success\"}"));
				break;
			case 2: // Query message
				long ownerUid = data.get("ownerUid").getAsLong();
				long otherUid = data.get("otherUid").getAsLong();
				List<MessageDTO> messageVO = messageService.queryConversationMsg(ownerUid, otherUid);
				String msgs = "";
				if (messageVO != null) {
					JsonObject jsonObject = new JsonObject();
					jsonObject.add("type", new JsonPrimitive(2));
					jsonObject.add("data", new Gson().toJsonTree(messageVO));
					msgs = jsonObject.toString();
				}
				ctx.writeAndFlush(new TextWebSocketFrame(msgs));
				break;

			case 3: // Send message
				long senderUid = data.get("senderUid").getAsLong();
				long recipientUid = data.get("recipientUid").getAsLong();
				String content = data.get("content").getAsString();
				int msgType = data.get("msgType").getAsInt();
				MessageDTO messageContent = messageService.sendNewMsg(senderUid, recipientUid, content, msgType);
				if (messageContent != null) {
					JsonObject jsonObject = new JsonObject();
					jsonObject.add("type", new JsonPrimitive(3));
					jsonObject.add("data", new Gson().toJsonTree(messageContent));
					ctx.writeAndFlush(new TextWebSocketFrame(jsonObject.toString()));
				}
				break;

			case 5: // Check unread count
				long unreadOwnerUid = data.get("uid").getAsLong();
				long totalUnread = messageService.queryTotalUnread(unreadOwnerUid);
				ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":5,\"data\":{\"unread\":" + totalUnread + "}}"));
				break;

			case 6: // Handle ack
				long tid = data.get("tid").getAsLong();
				ConcurrentHashMap<Long, JsonObject> nonAckedMap = ctx.channel().attr(NON_ACKED_MAP).get();
				nonAckedMap.remove(tid);
				break;
			}

		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.info("[ChannelActive]:remote address is {} ", ctx.channel().remoteAddress());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.info("[ChannelClosed]:remote address is {} ", ctx.channel().remoteAddress());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		log.error("Process error: uid is {},  channel info {}", channelUser.get(ctx.channel()), ctx.channel());
		ctx.channel().close();
	}

	public void pushMsg(long recipientUid, JsonObject message) {
		Channel channel = userChannel.get(recipientUid);
		if (channel != null && channel.isActive() && channel.isWritable()) {
			AtomicLong generator = channel.attr(TID_GENERATOR).get();
			long tid = generator.incrementAndGet();
			message.add("tid", new JsonPrimitive(tid));
			channel.writeAndFlush(new TextWebSocketFrame(message.toString())).addListener(future -> {
				if (future.isCancelled()) {
					log.warn("Future has been cancelled. {}, channel: {}", message, channel);
				} else if (future.isSuccess()) {
					addMsgToAckBuffer(channel, message);
					log.warn("Future has been successfully pushed. {}, channel: {}", message, channel);
				} else {
					log.error("Message write fail, {}, channel: {}", message, channel, future.cause());
				}
			});
		}
	}

	public void cleanUserChannel(Channel channel) {
		long uid = channelUser.remove(channel);
		userChannel.remove(uid);
		log.info("[CleanChannel]: remove uid & channel info from gateway, uid is {}, channel is {}", uid, channel);
	}

	private void addMsgToAckBuffer(Channel channel, JsonObject msgJson) {
		channel.attr(NON_ACKED_MAP).get().put(msgJson.get("tid").getAsLong(), msgJson);
		scheduledExecutorService.schedule(() -> {
			if (channel.isActive()) {
				checkAndResend(channel, msgJson);
			}
		}, 5000, TimeUnit.MILLISECONDS);
	}

	private void checkAndResend(Channel channel, JsonObject msgJson) {
		long tid = msgJson.get("tid").getAsLong();
		int tryTimes = 2;// resend twice
		while (tryTimes > 0) {
			if (channel.attr(NON_ACKED_MAP).get().containsKey(tid) && tryTimes > 0) {
				channel.writeAndFlush(new TextWebSocketFrame(msgJson.getAsString()));
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			tryTimes--;
		}
	}
}
