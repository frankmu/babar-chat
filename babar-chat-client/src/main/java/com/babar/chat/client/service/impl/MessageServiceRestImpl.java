package com.babar.chat.client.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.babar.chat.client.service.MessageService;
import com.babar.chat.dto.MessageDTO;
import com.babar.chat.util.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageServiceRestImpl implements MessageService {

	@Autowired
	RestTemplate restTemplate;

	SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);

	@Override
	public MessageDTO sendNewMsg(long senderUid, long recipientUid, String content, int msgType) {
		try {
			ResponseEntity<String> response = restTemplate.postForEntity(
					"/sendMessage?senderUid={senderUid}&recipientUid={recipientUid}&content={content}&msgType={msgType}",
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
	public List<MessageDTO> queryConversationMsg(long ownerUid, long otherUid) {
		List<MessageDTO> list = new ArrayList<MessageDTO>();
		try {
			ResponseEntity<String> response = restTemplate.getForEntity(
					"/getConversationMessage?ownerUid={ownerUid}&otherUid={otherUid}", String.class, ownerUid,
					otherUid);
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
	public List<MessageDTO> queryNewerMsgFrom(long ownerUid, long otherUid, long fromMid) {
		List<MessageDTO> list = new ArrayList<MessageDTO>();
		try {
			ResponseEntity<String> response = restTemplate.getForEntity(
					"/getNewMessageFrom?ownerUid={ownerUid}&otherUid={otherUid}&fromMid={fromMid}", String.class,
					ownerUid, otherUid, fromMid);
			if (response.getBody() != null) {
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
		Long totalUnread = restTemplate.getForObject("/getTotalUnread?ownerUid={ownerUid}", Long.class, ownerUid);
		return totalUnread;
	}

	private MessageDTO getMessage(JsonNode root) {
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
			return new MessageDTO(mid.asLong(), content.asText(), ownerUid.asLong(), type.asInt(), otherUid.asLong(),
					sdf.parse(createTime.asText()), ownerUidAvatar.asText(), otherUidAvatar.asText(),
					ownerName.asText(), otherName.asText());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}