package com.babar.chat.client.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.babar.chat.client.service.UserService;
import com.babar.chat.entity.User;
import com.babar.chat.message.Contact;
import com.babar.chat.message.ContactInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class UserServiceRestImpl implements UserService {

	@Autowired
	RestTemplate restTemplate;

	@Override
	public User login(String email, String password) {
		User user = restTemplate.postForObject("/login?email={email}&password={password}", null, User.class, email,
				password);

		return user;
	}

	@Override
	public List<User> getAllUsersExcept(long exceptUid) {
		User[] users = restTemplate.getForObject("/getAllUsersExceptId?exceptUid={exceptUid}", User[].class, exceptUid);
		return Arrays.asList(users);
	}

	@Override
	public Contact getContacts(long ownerUserId) {
		try {
			ResponseEntity<String> response = restTemplate.getForEntity("/getContacts?ownerUserId={ownerUserId}",
					String.class, ownerUserId);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(response.getBody());
			JsonNode ownerUid = root.path("ownerUid");
			JsonNode ownerAvatar = root.path("ownerAvatar");
			JsonNode ownerName = root.path("ownerName");
			JsonNode totalUnread = root.path("totalUnread");
			JsonNode contactInfoList = root.path("contactInfoList");

			Contact contact = new Contact(ownerUid.asLong(), ownerName.asText(), ownerAvatar.asText(),
					totalUnread.asLong());
			for (JsonNode node : contactInfoList) {
				ContactInfo contactInfo = new ContactInfo(node.path("otherUid").asLong(),
						node.path("otherName").asText(), node.path("otherAvatar").asText(), node.path("mid").asLong(),
						node.path("type").asInt(), node.path("content").asText(), node.path("convUnread").asLong(),
						new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(node.path("createTime").asText()));
				contact.appendContact(contactInfo);
			}

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
