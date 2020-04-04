package com.babar.chat.client.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.babar.chat.client.service.UserService;
import com.babar.chat.dto.UserDTO;
import com.babar.chat.dto.ContactDTO;
import com.babar.chat.entity.User;
import com.babar.chat.util.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserServiceRestImpl implements UserService {

	@Autowired
	RestTemplate restTemplate;
	
	SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);

	@Override
	public User login(String email, String password) {
		User user = restTemplate.postForObject("/login?email={email}&password={password}", null, User.class, email,
				password);

		return user;
	}

	@Override
	public List<User> getAllUsersExcept(long exceptUid) {
		User[] users = restTemplate.getForObject("/getUsersExceptUserId?exceptUid={exceptUid}", User[].class, exceptUid);
		return Arrays.asList(users);
	}

	@Override
	public UserDTO getContacts(long ownerUserId) {
		try {
			ResponseEntity<String> response = restTemplate.getForEntity("/getContactByOwnerUserId?ownerUserId={ownerUserId}",
					String.class, ownerUserId);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(response.getBody());
			JsonNode ownerUid = root.path("ownerUid");
			JsonNode ownerAvatar = root.path("ownerAvatar");
			JsonNode ownerName = root.path("ownerName");
			JsonNode totalUnread = root.path("totalUnread");
			JsonNode contactInfoList = root.path("contactInfoList");

			List<com.babar.chat.dto.ContactDTO> contacts = new ArrayList<>();
			for (JsonNode node : contactInfoList) {
				ContactDTO contactInfo = new ContactDTO(node.path("otherUid").asLong(),
						node.path("otherName").asText(), node.path("otherAvatar").asText(), node.path("mid").asLong(),
						node.path("type").asInt(), node.path("content").asText(), node.path("convUnread").asLong(),
						sdf.parse(node.path("createTime").asText()));
				contacts.add(contactInfo);
			}
			UserDTO contact = new UserDTO(ownerUid.asLong(), ownerName.asText(), ownerAvatar.asText(),
					totalUnread.asLong(), contacts);
			return contact;
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
