package com.babar.chat.dto;

import java.util.Date;

import lombok.Value;

@Value
public class MessageDTO {
	Long mid;
	String content;
	Long ownerUid;
	Integer type;
	Long otherUid;
	Date createTime;
	String ownerUidAvatar;
	String otherUidAvatar;
	String ownerName;
	String otherName;
}
