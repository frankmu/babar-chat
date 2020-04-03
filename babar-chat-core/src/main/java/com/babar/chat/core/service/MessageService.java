package com.babar.chat.core.service;

import java.util.List;

import com.babar.chat.dto.Contact;
import com.babar.chat.dto.MessageDTO;

public interface MessageService {

    /**
     *
     * @param senderUid
     * @param recipientUid
     * @param content
     * @param msgType
     * @return
     */
    MessageDTO sendNewMsg(long senderUid, long recipientUid, String content, int msgType);

    /**
     * @param ownerUid
     * @param otherUid
     * @return
     */
    List<MessageDTO> queryConversationMsg(long ownerUid, long otherUid);

    /**
     * @param ownerUid
     * @param otherUid
     * @param fromMid
     * @return
     */
    List<MessageDTO> queryNewerMsgFrom(long ownerUid, long otherUid, long fromMid);

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
