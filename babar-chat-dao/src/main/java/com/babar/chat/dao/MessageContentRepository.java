package com.babar.chat.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.babar.chat.entity.MessageContent;

@Repository
public interface MessageContentRepository extends JpaRepository<MessageContent, Long> {

}
