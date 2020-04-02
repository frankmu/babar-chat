package com.babar.chat.core.service.grpc.provider;

import com.babar.chat.core.generate.Contact;
import com.babar.chat.core.generate.ContactInfo;
import com.babar.chat.core.generate.LoginRequest;
import com.babar.chat.core.generate.User;
import com.babar.chat.core.generate.UserIdRequest;
import com.babar.chat.core.generate.UserList;
import com.babar.chat.core.generate.UserServiceGrpc.UserServiceImplBase;
import com.babar.chat.core.service.UserService;

import io.grpc.stub.StreamObserver;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserServiceImplGrpc extends UserServiceImplBase {

	@Autowired
	private UserService userService;
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

	@Override
	public void login(LoginRequest req, StreamObserver<User> responseObserver) {
		com.babar.chat.entity.User loginUser = userService.login(req.getEmail(), req.getPassword());
		User user = User.newBuilder()
				.setUid(loginUser.getUid())
				.setEmail(loginUser.getEmail())
				.setPassword(loginUser.getPassword())
				.setUsername(loginUser.getUsername())
				.setAvatar(loginUser.getAvatar())
				.build();
		responseObserver.onNext(user);
		responseObserver.onCompleted();
	}
	
	@Override
	public void getUsersExceptUserId(UserIdRequest req, StreamObserver<UserList> responseObserver) {
		List<com.babar.chat.entity.User> users = userService.getAllUsersExcept(req.getUid());
		List<User> userList = users.stream().map(user -> {
			return User.newBuilder()
					.setUid(user.getUid())
					.setEmail(user.getEmail())
					.setPassword(user.getPassword())
					.setUsername(user.getUsername())
					.setAvatar(user.getAvatar())
					.build();
		}).collect(Collectors.toList());
		UserList res = UserList.newBuilder().addAllUserList(userList).build();
		responseObserver.onNext(res);
		responseObserver.onCompleted();
	}
	
	@Override
	public void getContactByOwnerUserId(UserIdRequest req, StreamObserver<Contact> responseObserver) {
		com.babar.chat.message.Contact contact = userService.getContactsByOwnerId(req.getUid());
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
}