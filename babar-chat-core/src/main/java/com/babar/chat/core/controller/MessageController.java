package com.babar.chat.core.controller;

import com.babar.chat.core.service.MessageService;
import com.babar.chat.message.Contact;
import com.babar.chat.message.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class MessageController {

	@Autowired
	private MessageService messageService;

	@PostMapping(path = "/sendNewMsg")
	@ResponseBody
	public Message sendNewMsg(@RequestParam long senderUid, @RequestParam long recipientUid, @RequestParam String content, @RequestParam int msgType) {
		return messageService.sendNewMsg(senderUid, recipientUid, content, msgType);
	}

	@GetMapping(path = "/queryConversationMsg")
	@ResponseBody
	public List<Message> queryConversationMsg(@RequestParam long ownerUid, @RequestParam long otherUid) {
		return messageService.queryConversationMsg(ownerUid, otherUid);
	}

	@GetMapping(path = "/queryNewerMsgFrom")
	@ResponseBody
	public List<Message> queryNewerMsgFrom(@RequestParam long ownerUid, @RequestParam long otherUid, @RequestParam long fromMid) {
		return messageService.queryNewerMsgFrom(ownerUid, otherUid, fromMid);
	}

	@GetMapping(path = "/queryContacts")
	@ResponseBody
	public Contact queryContacts(@RequestParam long ownerUid) {
		return messageService.queryContacts(ownerUid);
	}

	@GetMapping(path = "/queryTotalUnread")
	@ResponseBody
	public long queryTotalUnread(@RequestParam long ownerUid) {
		return messageService.queryTotalUnread(ownerUid);
	}
}