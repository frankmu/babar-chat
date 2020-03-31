package com.babar.chat.client.service;

import java.util.List;

import com.babar.chat.message.Message;

public interface MessageService {

    Message sendNewMsg(long senderUid, long recipientUid, String content, int msgType);

    List<Message> queryConversationMsg(long ownerUid, long otherUid);

    List<Message> queryNewerMsgFrom(long ownerUid, long otherUid, long fromMid);
    
    long queryTotalUnread(long ownerUid);
}