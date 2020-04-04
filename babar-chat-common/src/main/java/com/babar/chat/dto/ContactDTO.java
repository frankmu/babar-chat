package com.babar.chat.dto;

import java.util.Date;

import lombok.Value;

@Value
public class ContactDTO {
	Long otherUid;
	String otherName;
	String otherAvatar;
	Long mid;
	Integer type;
	String content;
	Long convUnread;
	Date createTime;
}