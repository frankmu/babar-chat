package com.babar.chat.core.service;

import java.util.List;

import com.babar.chat.entity.User;
import com.babar.chat.message.Contact;

public interface UserService {

    User login(String email, String password);

    List<User> getAllUsersExcept(long exceptUid);

    List<User> getAllUsersExcept(User exceptUser);

    Contact getContacts(User ownerUser);
}
