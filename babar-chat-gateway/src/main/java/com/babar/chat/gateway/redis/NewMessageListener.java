package com.babar.chat.gateway.redis;

import com.babar.chat.gateway.handler.WebsocketRouterHandler;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NewMessageListener implements MessageListener {

    @Autowired
    private WebsocketRouterHandler websocketRouterHandler;

    private static final StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
    private static final RedisSerializer<String> valueSerializer = new GenericToStringSerializer<String>(String.class);

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String topic = stringRedisSerializer.deserialize(message.getChannel());
        String jsonMsg = valueSerializer.deserialize(message.getBody());
        log.info("Message Received --> pattern: {}，topic:{}，message: {}", new String(pattern), topic, jsonMsg);
        JsonObject msgJson = JsonParser.parseString(jsonMsg).getAsJsonObject();
        long otherUid = msgJson.get("otherUid").getAsLong();
        JsonObject pushJson = new JsonObject();
        pushJson.add("type", new JsonPrimitive(4));
        pushJson.add("data", msgJson);

        websocketRouterHandler.pushMsg(otherUid, pushJson);
    }
}