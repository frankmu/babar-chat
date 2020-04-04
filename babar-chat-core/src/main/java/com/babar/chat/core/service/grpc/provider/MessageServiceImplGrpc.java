package com.babar.chat.core.service.grpc.provider;

import com.babar.chat.core.generate.Contact;
import com.babar.chat.core.generate.ContactInfo;
import com.babar.chat.core.generate.ConversationMessageRequest;
import com.babar.chat.core.generate.Message;
import com.babar.chat.core.generate.MessageList;
import com.babar.chat.core.generate.MessageServiceGrpc.MessageServiceImplBase;
import com.babar.chat.core.generate.NewMessageRequest;
import com.babar.chat.core.generate.SendMessageRequest;
import com.babar.chat.core.generate.TotalUnreadCount;
import com.babar.chat.core.generate.UserIdRequest;
import com.babar.chat.core.service.MessageService;

import io.grpc.stub.StreamObserver;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageServiceImplGrpc extends MessageServiceImplBase {

	@Autowired
	private MessageService messageService;
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

	@Override
	public void sendMessage(SendMessageRequest req, StreamObserver<Message> responseObserver) {
		com.babar.chat.dto.MessageDTO message = messageService.sendNewMsg(req.getSenderUid(), req.getRecipientUid(), req.getContent(), req.getType());
		Message res = Message.newBuilder()
				.setMid(message.getMid())
				.setContent(message.getContent())
				.setOwnerUid(message.getOwnerUid())
				.setType(message.getType())
				.setOtherUid(message.getOtherUid())
				.setCreateTime(sdf.format(message.getCreateTime()))
				.setOwnerUidAvatar(message.getOwnerUidAvatar())
				.setOtherUidAvatar(message.getOtherUidAvatar())
				.setOwnerName(message.getOwnerName())
				.setOtherName(message.getOtherName())
				.build();
		responseObserver.onNext(res);
		responseObserver.onCompleted();
	}
	
	@Override
	public void getConversationMessage(ConversationMessageRequest req, StreamObserver<MessageList> responseObserver) {
		List<com.babar.chat.dto.MessageDTO> messages = messageService.queryConversationMsg(req.getOwnerUid(), req.getOtherUid());
		List<Message> messageList = messages.stream().map(message -> {
			return Message.newBuilder()
					.setMid(message.getMid())
					.setContent(message.getContent())
					.setOwnerUid(message.getOwnerUid())
					.setType(message.getType())
					.setOtherUid(message.getOtherUid())
					.setCreateTime(sdf.format(message.getCreateTime()))
					.setOwnerUidAvatar(message.getOwnerUidAvatar())
					.setOtherUidAvatar(message.getOtherUidAvatar())
					.setOwnerName(message.getOwnerName())
					.setOtherName(message.getOtherName())
					.build();
		}).collect(Collectors.toList());
		MessageList res = MessageList.newBuilder().addAllMessageList(messageList).build();
		responseObserver.onNext(res);
		responseObserver.onCompleted();
	}
	
	@Override
	public void getNewMessageFrom(NewMessageRequest req, StreamObserver<MessageList> responseObserver) {
		List<com.babar.chat.dto.MessageDTO> messages = messageService.queryNewerMsgFrom(req.getOwnerUid(), req.getOtherUid(), req.getFromMid());
		List<Message> messageList = messages.stream().map(message -> {
			return Message.newBuilder()
					.setMid(message.getMid())
					.setContent(message.getContent())
					.setOwnerUid(message.getOwnerUid())
					.setType(message.getType())
					.setOtherUid(message.getOtherUid())
					.setCreateTime(sdf.format(message.getCreateTime()))
					.setOwnerUidAvatar(message.getOwnerUidAvatar())
					.setOtherUidAvatar(message.getOtherUidAvatar())
					.setOwnerName(message.getOwnerName())
					.setOtherName(message.getOtherName())
					.build();
		}).collect(Collectors.toList());
		MessageList res = MessageList.newBuilder().addAllMessageList(messageList).build();
		responseObserver.onNext(res);
		responseObserver.onCompleted();
	}
	
	@Override
	public void getContacts(UserIdRequest req, StreamObserver<Contact> responseObserver) {
		com.babar.chat.dto.UserDTO contact = messageService.queryContacts(req.getUid());
		List<ContactInfo> contactInfoList = contact.getContactInfoList().stream().map(contactInfo -> {
			return ContactInfo.newBuilder()
					.setOtherUid(contactInfo.getOtherUid())
					.setOtherName(contactInfo.getOtherName())
					.setOtherAvatar(contactInfo.getOtherAvatar())
					.setMid(contactInfo.getMid())
					.setType(contactInfo.getType())
					.setContent(contactInfo.getContent())
					.setConvUnread(contactInfo.getConvUnread())
					.setCreateTime(sdf.format(contactInfo.getCreateTime()))
					.build();
		}).collect(Collectors.toList());
		Contact res = Contact.newBuilder()
				.setOwnerUid(contact.getOwnerUid())
				.setOwnerAvatar(contact.getOwnerAvatar())
				.setOwnerName(contact.getOwnerName())
				.setTotalUnread(contact.getTotalUnread())
				.addAllContactInfoList(contactInfoList)
				.build();
		responseObserver.onNext(res);
		responseObserver.onCompleted();
	}
	
	@Override
	public void getTotalUnread(UserIdRequest req, StreamObserver<TotalUnreadCount> responseObserver) {
		long totalUnread = messageService.queryTotalUnread(req.getUid());
		TotalUnreadCount res = TotalUnreadCount.newBuilder().setTotalUnread(totalUnread).build();
		responseObserver.onNext(res);
		responseObserver.onCompleted();
	}
}