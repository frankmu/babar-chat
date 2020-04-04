package com.babar.chat.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.babar.chat.core.service.UserService;
import com.babar.chat.dao.ContactRepository;
import com.babar.chat.dao.MessageRepository;
import com.babar.chat.dao.UserRepository;
import com.babar.chat.dto.UserDTO;
import com.babar.chat.dto.ContactDTO;
import com.babar.chat.entity.Contact;
import com.babar.chat.entity.Message;
import com.babar.chat.entity.User;
import com.babar.chat.exception.InvalidUserInfoException;
import com.babar.chat.exception.UserNotExistException;
import com.babar.chat.util.Constants;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class UserServiceImpl implements UserService {

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
    public UserDTO getContacts(User ownerUser) {
        List<Contact> contacts = contactRepository.findContactsByOwnerUidOrderByMidDesc(ownerUser.getUid());
        if (contacts != null) {
            long totalUnread = 0;
            Object totalUnreadObj = redisTemplate.opsForValue().get(ownerUser.getUid() + Constants.TOTAL_UNREAD_SUFFIX);
            if (null != totalUnreadObj) {
                totalUnread = Long.parseLong((String) totalUnreadObj);
            }

            List<ContactDTO> contactDTOs = new ArrayList<>();
            contacts.stream().forEach(contact -> {
                Long mid = contact.getMid();
                Optional<Message> contentVO = contentRepository.findById(mid);
                Optional<User> otherUser = userRepository.findById(contact.getOtherUid());

                if (null != contentVO) {
                    long convUnread = 0;
                    Object convUnreadObj = redisTemplate.opsForHash().get(ownerUser.getUid() + Constants.CONVERSION_UNREAD_SUFFIX, otherUser.get().getUid());
                    if (null != convUnreadObj) {
                        convUnread = Long.parseLong((String) convUnreadObj);
                    }
                    ContactDTO contactInfo = new ContactDTO(otherUser.get().getUid(), otherUser.get().getUsername(), otherUser.get().getAvatar(), mid, contact.getType(), contentVO.get().getContent(), convUnread, contact.getCreateTime());
                    contactDTOs.add(contactInfo);
                }
            });
            final UserDTO contactVO = new UserDTO(ownerUser.getUid(), ownerUser.getUsername(), ownerUser.getAvatar(), totalUnread, contactDTOs);
            
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
	public UserDTO getContactsByOwnerId(long ownerUserId) {
		return getContacts(userRepository.getOne(ownerUserId));
	}
}
