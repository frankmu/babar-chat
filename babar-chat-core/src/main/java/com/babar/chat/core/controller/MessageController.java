package com.babar.chat.core.controller;

import com.babar.chat.core.service.MessageService;
import com.babar.chat.dto.Contact;
import com.babar.chat.dto.MessageDTO;

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

	@PostMapping(path = "/sendMessage")
	@ResponseBody
	public MessageDTO sendMessage(@RequestParam long senderUid, @RequestParam long recipientUid, @RequestParam String content, @RequestParam int msgType) {
		return messageService.sendNewMsg(senderUid, recipientUid, content, msgType);
	}

	@GetMapping(path = "/getConversationMessage")
	@ResponseBody
	public List<MessageDTO> getConversationMessage(@RequestParam long ownerUid, @RequestParam long otherUid) {
		return messageService.queryConversationMsg(ownerUid, otherUid);
	}

	@GetMapping(path = "/getNewMessageFrom")
	@ResponseBody
	public List<MessageDTO> getNewMessageFrom(@RequestParam long ownerUid, @RequestParam long otherUid, @RequestParam long fromMid) {
		return messageService.queryNewerMsgFrom(ownerUid, otherUid, fromMid);
	}

	@GetMapping(path = "/getContacts")
	@ResponseBody
	public Contact getContacts(@RequestParam long ownerUid) {
		return messageService.queryContacts(ownerUid);
	}

	@GetMapping(path = "/getTotalUnread")
	@ResponseBody
	public long getTotalUnread(@RequestParam long ownerUid) {
		return messageService.queryTotalUnread(ownerUid);
	}
}