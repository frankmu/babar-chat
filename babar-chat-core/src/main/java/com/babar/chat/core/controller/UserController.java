package com.babar.chat.core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.babar.chat.core.service.UserService;
import com.babar.chat.dto.UserDTO;
import com.babar.chat.entity.User;

import java.util.List;

@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@RequestMapping(path = "/login")
	@ResponseBody
	public User login(@RequestParam String email, @RequestParam String password) {
		return userService.login(email, password);
	}

	@GetMapping(path = "/getUsersExceptUserId")
	@ResponseBody
	public List<User> getUsersExceptUserId(@RequestParam long exceptUid) {
		return userService.getAllUsersExcept(exceptUid);
	}

	@GetMapping(path = "/getContactByOwnerUserId")
	@ResponseBody
	public UserDTO getContactByOwnerUserId(@RequestParam long ownerUserId) {
		return userService.getContactsByOwnerId(ownerUserId);
	}
	
	@GetMapping(path = "/getAllUsers")
	@ResponseBody
	public List<User> getAllUsers() {
		return userService.getAllUsers();
	}
}