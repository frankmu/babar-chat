package com.babar.chat.client.service;

import java.util.List;

import com.babar.chat.dto.UserDTO;
import com.babar.chat.entity.User;

public interface UserService {

    User login(String email, String password);

    List<User> getAllUsersExcept(long exceptUid);

    UserDTO getContacts(long ownerUserId);
}
