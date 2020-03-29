package com.babar.chat.client.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.babar.chat.client.service.MessageService;
import com.babar.chat.message.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MessageServiceRestImpl implements MessageService {

	@Autowired
	RestTemplate restTemplate;

	@Override
	public Message sendNewMsg(long senderUid, long recipientUid, String content, int msgType) {
		try {
			ResponseEntity<String> response = restTemplate.postForEntity(
					"/sendNewMsg?senderUid={senderUid}&recipientUid={recipientUid}&content={content}&msgType={msgType}",
					null, String.class, senderUid, recipientUid, content, msgType);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(response.getBody());
			return getMessage(root);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Message> queryConversationMsg(long ownerUid, long otherUid) {
		List<Message> list = new ArrayList<Message>();
		try {
			ResponseEntity<String> response = restTemplate.getForEntity(
					"/queryConversationMsg?ownerUid={ownerUid}&otherUid={otherUid}", String.class, ownerUid, otherUid);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(response.getBody());
			for (JsonNode node : root) {
				list.add(getMessage(node));
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<Message> queryNewerMsgFrom(long ownerUid, long otherUid, long fromMid) {		
		List<Message> list = new ArrayList<Message>();
		try {
			ResponseEntity<String> response = restTemplate.getForEntity(
					"/queryNewerMsgFrom?ownerUid={ownerUid}&otherUid={otherUid}&fromMid={fromMid}", String.class,
					ownerUid, otherUid, fromMid);
			if(response.getBody() != null) {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode root = mapper.readTree(response.getBody());
				for (JsonNode node : root) {
					list.add(getMessage(node));
				}
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public long queryTotalUnread(long ownerUid) {
		Long totalUnread = restTemplate.getForObject("/queryTotalUnread?ownerUid={ownerUid}", Long.class, ownerUid);
		return totalUnread;
	}

	private Message getMessage(JsonNode root) {
		JsonNode mid = root.path("mid");
		JsonNode content = root.path("content");
		JsonNode ownerUid = root.path("ownerUid");
		JsonNode type = root.path("type");
		JsonNode otherUid = root.path("otherUid");
		JsonNode createTime = root.path("createTime");
		JsonNode ownerUidAvatar = root.path("ownerUidAvatar");
		JsonNode otherUidAvatar = root.path("otherUidAvatar");
		JsonNode ownerName = root.path("ownerName");
		JsonNode otherName = root.path("otherUid");
		try {
			return new Message(mid.asLong(), content.asText(), ownerUid.asLong(), type.asInt(), otherUid.asLong(),
					new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(createTime.asText()),
					ownerUidAvatar.asText(), otherUidAvatar.asText(), ownerName.asText(), otherName.asText());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}