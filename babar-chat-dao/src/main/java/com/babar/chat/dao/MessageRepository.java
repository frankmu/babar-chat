package com.babar.chat.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.babar.chat.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

}
