package com.babar.chat.client.grpc.consumer;

import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.babar.chat.client.service.UserService;
import com.babar.chat.core.generate.Contact;
import com.babar.chat.core.generate.LoginRequest;
import com.babar.chat.core.generate.User;
import com.babar.chat.core.generate.UserIdRequest;
import com.babar.chat.core.generate.UserList;
import com.babar.chat.core.generate.UserServiceGrpc.UserServiceBlockingStub;
import com.babar.chat.util.Constants;

@Slf4j
public class UserServiceGrpcImpl implements UserService {

	@Autowired
	ManagedChannel channel;
	
	@Autowired
	UserServiceBlockingStub userServiceBlockingStub;
	
	SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
	
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
			log.warn("Grpc failed: {0}", e.getStatus());
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
			log.warn("Grpc failed: {0}", e.getStatus());
		}
		return null;
	}

	@Override
	public com.babar.chat.dto.UserDTO getContacts(long ownerUserId) {
		try {
			UserIdRequest req = UserIdRequest.newBuilder().setUid(ownerUserId).build();
			Contact contact = userServiceBlockingStub.getContactByOwnerUserId(req);
			List<com.babar.chat.dto.ContactDTO> contactInfoList = contact.getContactInfoListList().stream()
					.map(contactInfo -> {
						Date createTime = null;
						try {
							createTime = sdf.parse(contactInfo.getCreateTime());
						} catch (ParseException e) {
							e.printStackTrace();
						}
						return new com.babar.chat.dto.ContactDTO(
								contactInfo.getOtherUid(), contactInfo.getOtherName(), contactInfo.getOtherAvatar(),
								contactInfo.getMid(), contactInfo.getType(), contactInfo.getContent(),
								contactInfo.getConvUnread(), createTime);
					}).collect(Collectors.toList());
			com.babar.chat.dto.UserDTO res = new com.babar.chat.dto.UserDTO(contact.getOwnerUid(),
					contact.getOwnerName(), contact.getOwnerAvatar(), contact.getTotalUnread(), contactInfoList);
			return res;

		} catch (StatusRuntimeException e) {
			log.warn("Grpc failed: {0}", e.getStatus());
		}
		return null;
	}
}