package com.babar.chat.client.controller;

import com.babar.chat.client.service.MessageService;
import com.babar.chat.client.service.UserService;
import com.babar.chat.message.Contact;
import com.babar.chat.message.Message;
import com.google.gson.Gson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;
    
    @Autowired
    private UserService userService;


    @PostMapping(path = "/sendMsg")
    @ResponseBody
    public String sendMsg(@RequestParam Long senderUid, @RequestParam Long recipientUid, String content, Integer msgType, Model model, HttpSession session) {
        Message messageContent = messageService.sendNewMsg(senderUid, recipientUid, content, msgType);
        if (null != messageContent) {
            return new Gson().toJson(messageContent);
        } else {
            return "";
        }
    }

    @GetMapping(path = "/queryMsg")
    @ResponseBody
    public String queryMsg(@RequestParam Long ownerUid, @RequestParam Long otherUid, Model model, HttpSession session) {
        List<Message> messageVO = messageService.queryConversationMsg(ownerUid, otherUid);
        if (messageVO != null) {
            return new Gson().toJson(messageVO);
        } else {
            return "";
        }
    }

    @GetMapping(path = "/queryMsgSinceMid")
    @ResponseBody
    public String queryMsgSinceMid(@RequestParam Long ownerUid, @RequestParam Long otherUid, @RequestParam Long lastMid, Model model, HttpSession session) {
        List<Message> messageVO = messageService.queryNewerMsgFrom(ownerUid, otherUid, lastMid);
        if (messageVO != null) {
            return new Gson().toJson(messageVO);
        } else {
            return "";
        }
    }

    @GetMapping(path = "/queryContacts")
    @ResponseBody
    public String queryContacts(@RequestParam Long ownerUid, Model model, HttpSession session) {
        Contact contactVO = userService.getContacts(ownerUid);
        if (contactVO != null) {
            return new Gson().toJson(contactVO);
        } else {
            return "";
        }
    }
}
