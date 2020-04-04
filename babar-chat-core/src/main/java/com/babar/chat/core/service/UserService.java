package com.babar.chat.core.service;

import java.util.List;

import com.babar.chat.dto.UserDTO;
import com.babar.chat.entity.User;

public interface UserService {

    User login(String email, String password);

    List<User> getAllUsersExcept(long exceptUid);

    List<User> getAllUsersExcept(User exceptUser);

    UserDTO getContacts(User ownerUser);
    
    UserDTO getContactsByOwnerId(long ownerUserId);
    
    List<User> getAllUsers();
}
