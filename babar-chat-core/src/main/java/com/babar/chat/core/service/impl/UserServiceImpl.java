package com.babar.chat.core.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.babar.chat.core.service.UserService;
import com.babar.chat.dao.ContactRepository;
import com.babar.chat.dao.MessageRepository;
import com.babar.chat.dao.UserRepository;
import com.babar.chat.dto.ContactDTO;
import com.babar.chat.dto.ContactInfo;
import com.babar.chat.entity.Contact;
import com.babar.chat.entity.Message;
import com.babar.chat.entity.User;
import com.babar.chat.exception.InvalidUserInfoException;
import com.babar.chat.exception.UserNotExistException;

import java.util.List;
import java.util.Optional;

@Component
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MessageRepository contentRepository;

    @Override
    public User login(String email, String password) {
        List<User> users = userRepository.findByEmail(email);
        if (null == users || users.isEmpty()) {
            log.warn("User doesn't exist" + email);
            throw new UserNotExistException("User doesn't exist: " + email);
        } else {
            User user = users.get(0);
            if (user.getPassword().equals(password)) {
                log.info(user.getUsername() + " logged in!");
                return user;
            } else {
                log.warn(user.getUsername() + " failed to log in!");
                throw new InvalidUserInfoException("invalid user info:" + user.getUsername());
            }
        }
    }


    @SuppressWarnings("unlikely-arg-type")
	@Override
    public List<User> getAllUsersExcept(long exceptUid) {
        List<User> otherUsers = userRepository.findAll();
        otherUsers.remove(userRepository.findById(exceptUid));
        return otherUsers;
    }

    @Override
    public List<User> getAllUsersExcept(User exceptUser) {
        List<User> otherUsers = userRepository.findUsersByUidIsNot(exceptUser.getUid());
        return otherUsers;
    }

    @Override
    public ContactDTO getContacts(User ownerUser) {
        List<Contact> contacts = contactRepository.findContactsByOwnerUidOrderByMidDesc(ownerUser.getUid());
        if (contacts != null) {
            long totalUnread = 0;
            Object totalUnreadObj = redisTemplate.opsForValue().get(ownerUser.getUid() + "_T");
            if (null != totalUnreadObj) {
                totalUnread = Long.parseLong((String) totalUnreadObj);
            }

            final ContactDTO contactVO = new ContactDTO(ownerUser.getUid(), ownerUser.getUsername(), ownerUser.getAvatar(), totalUnread);
            contacts.stream().forEach(contact -> {
                Long mid = contact.getMid();
                Optional<Message> contentVO = contentRepository.findById(mid);
                Optional<User> otherUser = userRepository.findById(contact.getOtherUid());

                if (null != contentVO) {
                    long convUnread = 0;
                    Object convUnreadObj = redisTemplate.opsForHash().get(ownerUser.getUid() + "_C", otherUser.get().getUid());
                    if (null != convUnreadObj) {
                        convUnread = Long.parseLong((String) convUnreadObj);
                    }
                    ContactInfo contactInfo = new ContactInfo(otherUser.get().getUid(), otherUser.get().getUsername(), otherUser.get().getAvatar(), mid, contact.getType(), contentVO.get().getContent(), convUnread, contact.getCreateTime());
                    contactVO.appendContact(contactInfo);
                }
            });
            return contactVO;
        }
        return null;
    }
    
    @Override
    public List<User> getAllUsers(){
    		return userRepository.findAll();
    }


	@Override
	@Transactional
	public ContactDTO getContactsByOwnerId(long ownerUserId) {
		return getContacts(userRepository.getOne(ownerUserId));
	}
}
