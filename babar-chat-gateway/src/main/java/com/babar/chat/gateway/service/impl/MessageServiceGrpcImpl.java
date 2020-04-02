package com.babar.chat.gateway.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.babar.chat.gateway.service.MessageService;
import com.babar.chat.core.generate.ConversationMessageRequest;
import com.babar.chat.core.generate.Message;
import com.babar.chat.core.generate.MessageList;
import com.babar.chat.core.generate.MessageServiceGrpc.MessageServiceBlockingStub;
import com.babar.chat.core.generate.NewMessageRequest;
import com.babar.chat.core.generate.SendMessageRequest;
import com.babar.chat.core.generate.TotalUnreadCount;
import com.babar.chat.core.generate.UserIdRequest;

import io.grpc.ManagedChannel;

public class MessageServiceGrpcImpl implements MessageService {

	@Autowired
	ManagedChannel channel;

	@Autowired
	MessageServiceBlockingStub messageServiceBlockingStub;

	@Override
	public com.babar.chat.message.Message sendNewMsg(long senderUid, long recipientUid, String content, int msgType) {
		SendMessageRequest req = SendMessageRequest.newBuilder().setSenderUid(senderUid).setRecipientUid(recipientUid)
				.setContent(content).setType(msgType).build();
		Message message = messageServiceBlockingStub.sendMessage(req);
		Date createTime = null;
		try {
			createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(message.getCreateTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		com.babar.chat.message.Message res = new com.babar.chat.message.Message(message.getMid(), message.getContent(),
				message.getOwnerUid(), message.getType(), message.getOtherUid(), createTime,
				message.getOwnerUidAvatar(), message.getOtherUidAvatar(), message.getOwnerName(),
				message.getOtherName());
		return res;
	}

	@Override
	public List<com.babar.chat.message.Message> queryConversationMsg(long ownerUid, long otherUid) {
		ConversationMessageRequest req = ConversationMessageRequest.newBuilder().setOwnerUid(ownerUid)
				.setOtherUid(otherUid).build();

		MessageList messageList = messageServiceBlockingStub.getConversationMessage(req);
		List<com.babar.chat.message.Message> messages = messageList.getMessageListList().stream().map(message -> {
			Date createTime = null;
			try {
				createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(message.getCreateTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return new com.babar.chat.message.Message(message.getMid(), message.getContent(), message.getOwnerUid(),
					message.getType(), message.getOtherUid(), createTime, message.getOwnerUidAvatar(),
					message.getOtherUidAvatar(), message.getOwnerName(), message.getOtherName());
		}).collect(Collectors.toList());
		return messages;
	}

	@Override
	public List<com.babar.chat.message.Message> queryNewerMsgFrom(long ownerUid, long otherUid, long fromMid) {
		NewMessageRequest req = NewMessageRequest.newBuilder().setOwnerUid(ownerUid).setOtherUid(otherUid).setFromMid(fromMid).build();

		MessageList messageList = messageServiceBlockingStub.getNewMessageFrom(req);
		List<com.babar.chat.message.Message> messages = messageList.getMessageListList().stream().map(message -> {
			Date createTime = null;
			try {
				createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(message.getCreateTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return new com.babar.chat.message.Message(message.getMid(), message.getContent(), message.getOwnerUid(),
					message.getType(), message.getOtherUid(), createTime, message.getOwnerUidAvatar(),
					message.getOtherUidAvatar(), message.getOwnerName(), message.getOtherName());
		}).collect(Collectors.toList());
		return messages;
	}

	@Override
	public long queryTotalUnread(long ownerUid) {
		UserIdRequest req = UserIdRequest.newBuilder().setUid(ownerUid).build();
		TotalUnreadCount unread = messageServiceBlockingStub.getTotalUnread(req);
		return unread.getTotalUnread();
	}
}