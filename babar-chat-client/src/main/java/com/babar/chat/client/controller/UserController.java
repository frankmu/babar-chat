package com.babar.chat.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.babar.chat.client.service.UserService;
import com.babar.chat.entity.User;
import com.babar.chat.exception.InvalidUserInfoException;
import com.babar.chat.exception.UserNotExistException;
import com.babar.chat.message.Contact;
import com.babar.chat.util.Constants;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(path = "/")
    public String welcomePage(@RequestParam(name = "username", required = false)
                                      String username, HttpSession session) {
        if (session.getAttribute(Constants.SESSION_KEY) != null) {
            return "index";
        } else {
            return "login";
        }
    }

    @RequestMapping(path = "/login")
    public String login(@RequestParam String email, @RequestParam String password, Model model, HttpSession session) {
        try {
            User loginUser = userService.login(email, password);
            model.addAttribute("loginUser", loginUser);
            session.setAttribute(Constants.SESSION_KEY, loginUser);

            List<User> otherUsers = userService.getAllUsersExcept(loginUser.getUid());
            model.addAttribute("otherUsers", otherUsers);

            Contact contactVO = userService.getContacts(loginUser.getUid());
            model.addAttribute("contactVO", contactVO);
            return "index";

        } catch (UserNotExistException e1) {
            model.addAttribute("errormsg", email + " account doesn't exists!");
            return "login";
        } catch (InvalidUserInfoException e2) {
            model.addAttribute("errormsg", "Username/password wrong!");
            return "login";
        }
    }

    @RequestMapping(path = "/ws")
    public String ws(Model model, HttpSession session) {
        User loginUser = (User)session.getAttribute(Constants.SESSION_KEY);
        model.addAttribute("loginUser", loginUser);
        List<User> otherUsers = userService.getAllUsersExcept(loginUser.getUid());
        model.addAttribute("otherUsers", otherUsers);

        Contact contactVO = userService.getContacts(loginUser.getUid());
        model.addAttribute("contactVO", contactVO);
        return "index_ws";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute(Constants.SESSION_KEY);
        return "redirect:/";
    }

}