package com.babar.chat.core.service.impl;

import com.babar.chat.core.service.MessageService;
import com.babar.chat.dao.ContactRepository;
import com.babar.chat.dao.MessageRepository;
import com.babar.chat.dao.UserMessageRepository;
import com.babar.chat.dao.UserRepository;
import com.babar.chat.dto.ContactDTO;
import com.babar.chat.dto.ContactInfo;
import com.babar.chat.dto.MessageDTO;
import com.babar.chat.entity.ContactMultiKeys;
import com.babar.chat.entity.Contact;
import com.babar.chat.entity.Message;
import com.babar.chat.entity.UserMessage;
import com.babar.chat.entity.User;
import com.babar.chat.util.Constants;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository contentRepository;
    @Autowired
    private UserMessageRepository relationRepository;
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public MessageDTO sendNewMsg(long senderUid, long recipientUid, String content, int msgType) {
        Date currentTime = new Date();
        // Save the message content
        Message messageContent = new Message();
        messageContent.setSenderId(senderUid);
        messageContent.setRecipientId(recipientUid);
        messageContent.setContent(content);
        messageContent.setMsgType(msgType);
        messageContent.setCreateTime(currentTime);
        messageContent = contentRepository.saveAndFlush(messageContent);
        Long mid = messageContent.getMid();

        // Save sender's outbox
        UserMessage messageRelationSender = new UserMessage();
        messageRelationSender.setMid(mid);
        messageRelationSender.setOwnerUid(senderUid);
        messageRelationSender.setOtherUid(recipientUid);
        messageRelationSender.setType(0);
        messageRelationSender.setCreateTime(currentTime);
        relationRepository.save(messageRelationSender);

        // Save recipient's inbox
        UserMessage messageRelationRecipient = new UserMessage();
        messageRelationRecipient.setMid(mid);
        messageRelationRecipient.setOwnerUid(recipientUid);
        messageRelationRecipient.setOtherUid(senderUid);
        messageRelationRecipient.setType(1);
        messageRelationRecipient.setCreateTime(currentTime);
        relationRepository.save(messageRelationRecipient);

        // Update recent contact
        Contact messageContactSender = contactRepository.findById(new ContactMultiKeys(senderUid, recipientUid)).get();
        if (messageContactSender != null) {
            messageContactSender.setMid(mid);
        } else {
            messageContactSender = new Contact();
            messageContactSender.setOwnerUid(senderUid);
            messageContactSender.setOtherUid(recipientUid);
            messageContactSender.setMid(mid);
            messageContactSender.setCreateTime(currentTime);
            messageContactSender.setType(0);
        }
        contactRepository.save(messageContactSender);

        // Update recipient's recent contact
        Contact messageContactRecipient = contactRepository.findById(new ContactMultiKeys(recipientUid, senderUid)).get();
        if (messageContactRecipient != null) {
            messageContactRecipient.setMid(mid);
        } else {
            messageContactRecipient = new Contact();
            messageContactRecipient.setOwnerUid(recipientUid);
            messageContactRecipient.setOtherUid(senderUid);
            messageContactRecipient.setMid(mid);
            messageContactRecipient.setCreateTime(currentTime);
            messageContactRecipient.setType(1);
        }
        contactRepository.save(messageContactRecipient);

        // Update unread count
        redisTemplate.opsForValue().increment(recipientUid + "_T", 1); //加总未读
        redisTemplate.opsForHash().increment(recipientUid + "_C", senderUid, 1); //加会话未读

        // Push the message to redis
        User self = userRepository.findById(senderUid).get();
        User other = userRepository.findById(recipientUid).get();
        MessageDTO messageDTO = new MessageDTO(mid, content, self.getUid(), messageContactSender.getType(), other.getUid(), messageContent.getCreateTime(), self.getAvatar(), other.getAvatar(), self.getUsername(), other.getUsername());
        redisTemplate.convertAndSend(Constants.WEBSOCKET_MSG_TOPIC, new Gson().toJson(messageDTO));

        return messageDTO;
    }

    @Override
    public List<MessageDTO> queryConversationMsg(long ownerUid, long otherUid) {
        List<UserMessage> relationList = relationRepository.findAllByOwnerUidAndOtherUidOrderByMidAsc(ownerUid, otherUid);
        return composeMessageVO(relationList, ownerUid, otherUid);
    }

    @Override
    public List<MessageDTO> queryNewerMsgFrom(long ownerUid, long otherUid, long fromMid) {
        List<UserMessage> relationList = relationRepository.findAllByOwnerUidAndOtherUidAndMidIsGreaterThanOrderByMidAsc(ownerUid, otherUid, fromMid);
        return composeMessageVO(relationList, ownerUid, otherUid);
    }

    private List<MessageDTO> composeMessageVO(List<UserMessage> relationList, long ownerUid, long otherUid) {
        if (null != relationList && !relationList.isEmpty()) {
        		// Compose message index and content
            List<MessageDTO> msgList = Lists.newArrayList();
            User self = userRepository.findById(ownerUid).get();
            User other = userRepository.findById(otherUid).get();
            relationList.stream().forEach(relation -> {
                Long mid = relation.getMid();
                Message contentVO = contentRepository.findById(mid).get();
                if (null != contentVO) {
                    String content = contentVO.getContent();
                    MessageDTO messageDTO = new MessageDTO(mid, content, relation.getOwnerUid(), relation.getType(), relation.getOtherUid(), relation.getCreateTime(), self.getAvatar(), other.getAvatar(), self.getUsername(), other.getUsername());
                    msgList.add(messageDTO);
                }
            });

            // Update unread
            Object convUnreadObj = redisTemplate.opsForHash().get(ownerUid + Constants.CONVERSION_UNREAD_SUFFIX, otherUid);
            if (null != convUnreadObj) {
                long convUnread = Long.parseLong((String) convUnreadObj);
                redisTemplate.opsForHash().delete(ownerUid + Constants.CONVERSION_UNREAD_SUFFIX, otherUid);
                long afterCleanUnread = redisTemplate.opsForValue().increment(ownerUid + Constants.TOTAL_UNREAD_SUFFIX, -convUnread);
                /** 修正总未读 */
                if (afterCleanUnread <= 0) {
                    redisTemplate.delete(ownerUid + Constants.TOTAL_UNREAD_SUFFIX);
                }
            }
            return msgList;
        }
        return new ArrayList<>();
    }

    @Override
    public ContactDTO queryContacts(long ownerUid) {
        List<Contact> contacts = contactRepository.findContactsByOwnerUidOrderByMidDesc(ownerUid);
        if (contacts != null) {
            User user = userRepository.findById(ownerUid).get();
            long totalUnread = 0;
            Object totalUnreadObj = redisTemplate.opsForValue().get(user.getUid() + Constants.TOTAL_UNREAD_SUFFIX);
            if (null != totalUnreadObj) {
                totalUnread = Long.parseLong((String) totalUnreadObj);
            }

            ContactDTO contactVO = new ContactDTO(user.getUid(), user.getUsername(), user.getAvatar(), totalUnread);
            contacts.stream().forEach(contact -> {
                Long mid = contact.getMid();
                Message contentVO = contentRepository.findById(mid).get();
                User otherUser = userRepository.findById(contact.getOtherUid()).get();

                if (null != contentVO) {
                    long convUnread = 0;
                    Object convUnreadObj = redisTemplate.opsForHash().get(user.getUid() + Constants.CONVERSION_UNREAD_SUFFIX, otherUser.getUid());
                    if (null != convUnreadObj) {
                        convUnread = Long.parseLong((String) convUnreadObj);
                    }
                    ContactInfo contactInfo = new ContactInfo(otherUser.getUid(), otherUser.getUsername(), otherUser.getAvatar(), mid, contact.getType(), contentVO.getContent(), convUnread, contact.getCreateTime());
                    contactVO.appendContact(contactInfo);
                }
            });
            return contactVO;
        }
        return null;
    }

    @Override
    public long queryTotalUnread(long ownerUid) {
        long totalUnread = 0;
        Object totalUnreadObj = redisTemplate.opsForValue().get(ownerUid + Constants.TOTAL_UNREAD_SUFFIX);
        if (null != totalUnreadObj) {
            totalUnread = Long.parseLong((String) totalUnreadObj);
        }
        return totalUnread;
    }
}
