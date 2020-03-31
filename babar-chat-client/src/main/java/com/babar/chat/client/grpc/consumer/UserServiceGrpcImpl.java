package com.babar.chat.client.grpc.consumer;

import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.babar.chat.client.service.UserService;
import com.babar.chat.core.generate.Contact;
import com.babar.chat.core.generate.LoginRequest;
import com.babar.chat.core.generate.User;
import com.babar.chat.core.generate.UserIdRequest;
import com.babar.chat.core.generate.UserList;
import com.babar.chat.core.generate.UserServiceGrpc.UserServiceBlockingStub;

@Component("userServiceGrpc")
public class UserServiceGrpcImpl implements UserService {

	@Autowired
	ManagedChannel channel;
	
	@Autowired
	UserServiceBlockingStub userServiceBlockingStub;
	
	private static final Logger logger = Logger.getLogger(UserServiceGrpcImpl.class.getName());

	@Override
	public com.babar.chat.entity.User login(String email, String password) {
		try {
			LoginRequest req = LoginRequest.newBuilder().setEmail(email).setPassword(password).build();
			User user = userServiceBlockingStub.login(req);
			com.babar.chat.entity.User res = new com.babar.chat.entity.User();
			res.setEmail(user.getEmail());
			res.setAvatar(user.getAvatar());
			res.setPassword(user.getPassword());
			res.setUid(user.getUid());
			res.setUsername(user.getUsername());
			return res;
		} catch (StatusRuntimeException e) {
			logger.log(Level.WARNING, "Grpc failed: {0}", e.getStatus());
		}
		return null;
	}

	@Override
	public List<com.babar.chat.entity.User> getAllUsersExcept(long exceptUid) {
		try {
			UserIdRequest req = UserIdRequest.newBuilder().setUid(exceptUid).build();
			UserList userList = userServiceBlockingStub.getUsersExceptUserId(req);
			List<com.babar.chat.entity.User> users = userList.getUserListList().stream().map(user -> {
				com.babar.chat.entity.User res = new com.babar.chat.entity.User();
				res.setEmail(user.getEmail());
				res.setAvatar(user.getAvatar());
				res.setPassword(user.getPassword());
				res.setUid(user.getUid());
				res.setUsername(user.getUsername());
				return res;
			}).collect(Collectors.toList());
			return users;

		} catch (StatusRuntimeException e) {
			logger.log(Level.WARNING, "Grpc failed: {0}", e.getStatus());
		}
		return null;
	}

	@Override
	public com.babar.chat.message.Contact getContacts(long ownerUserId) {
		try {
			UserIdRequest req = UserIdRequest.newBuilder().setUid(ownerUserId).build();
			Contact contact = userServiceBlockingStub.getContactByOwnerUserId(req);
			List<com.babar.chat.message.ContactInfo> contactInfoList = contact.getContactInfoListList().stream()
					.map(contactInfo -> {
						Date createTime = null;
						try {
							createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S")
									.parse(contactInfo.getCreateTime());
						} catch (ParseException e) {
							e.printStackTrace();
						}
						return new com.babar.chat.message.ContactInfo(
								contactInfo.getOtherUid(), contactInfo.getOtherName(), contactInfo.getOtherAvatar(),
								contactInfo.getMid(), contactInfo.getType(), contactInfo.getContent(),
								contactInfo.getConvUnread(), createTime);
					}).collect(Collectors.toList());
			com.babar.chat.message.Contact res = new com.babar.chat.message.Contact(contact.getOwnerUid(),
					contact.getOwnerName(), contact.getOwnerAvatar(), contact.getTotalUnread());
			res.setContactInfoList(contactInfoList);
			return res;

		} catch (StatusRuntimeException e) {
			logger.log(Level.WARNING, "Grpc failed: {0}", e.getStatus());
		}
		return null;
	}
}