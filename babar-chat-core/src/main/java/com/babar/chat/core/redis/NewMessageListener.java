package com.babar.chat.core.redis;

import com.babar.chat.gateway.handler.WebsocketRouterHandler;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

@Component
public class NewMessageListener implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(NewMessageListener.class);

    @Autowired
    private WebsocketRouterHandler websocketRouterHandler;

    StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
    private static final RedisSerializer<String> valueSerializer = new GenericToStringSerializer(Object.class);


    @Override
    public void onMessage(Message message, byte[] pattern) {
        String topic = stringRedisSerializer.deserialize(message.getChannel());
        String jsonMsg = valueSerializer.deserialize(message.getBody());
        logger.info("Message Received --> pattern: {}，topic:{}，message: {}", new String(pattern), topic, jsonMsg);
        JsonObject msgJson = JsonParser.parseString(jsonMsg).getAsJsonObject();
        long otherUid = msgJson.get("otherUid").getAsLong();
        JsonObject pushJson = new JsonObject();
        pushJson.add("type", new JsonPrimitive(4));
        pushJson.add("data", msgJson);

        websocketRouterHandler.pushMsg(otherUid, pushJson);
    }
}