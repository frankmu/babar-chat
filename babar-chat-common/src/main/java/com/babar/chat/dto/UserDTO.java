package com.babar.chat.dto;

import java.util.List;

import lombok.Value;

@Value
public class UserDTO {
	Long ownerUid;
	String ownerAvatar;
	String ownerName;
	Long totalUnread;
	List<ContactDTO> contactInfoList;
}