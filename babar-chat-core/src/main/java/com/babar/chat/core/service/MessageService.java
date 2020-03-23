package com.babar.chat.core.service;

import java.util.List;

import com.babar.chat.message.Contact;
import com.babar.chat.message.Message;

public interface MessageService {

    /**
     *
     * @param senderUid
     * @param recipientUid
     * @param content
     * @param msgType
     * @return
     */
    Message sendNewMsg(long senderUid, long recipientUid, String content, int msgType);

    /**
     * @param ownerUid
     * @param otherUid
     * @return
     */
    List<Message> queryConversationMsg(long ownerUid, long otherUid);

    /**
     * @param ownerUid
     * @param otherUid
     * @param fromMid
     * @return
     */
    List<Message> queryNewerMsgFrom(long ownerUid, long otherUid, long fromMid);

    /**
     * @param ownerUid
     * @return
     */
    Contact queryContacts(long ownerUid);

    /**
     * @param ownerUid
     * @return
     */
    long queryTotalUnread(long ownerUid);
}
