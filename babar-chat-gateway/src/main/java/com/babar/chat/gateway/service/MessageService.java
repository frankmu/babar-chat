package com.babar.chat.gateway.service;

import java.util.List;

import com.babar.chat.dto.MessageDTO;

public interface MessageService {

    MessageDTO sendNewMsg(long senderUid, long recipientUid, String content, int msgType);

    List<MessageDTO> queryConversationMsg(long ownerUid, long otherUid);

    List<MessageDTO> queryNewerMsgFrom(long ownerUid, long otherUid, long fromMid);
    
    long queryTotalUnread(long ownerUid);
}