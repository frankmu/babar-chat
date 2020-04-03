package com.babar.chat.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.babar.chat.entity.UserMessage;
import com.babar.chat.entity.UserMessageMultiKeys;

import java.util.List;

@Repository
public interface UserMessageRepository extends JpaRepository<UserMessage, UserMessageMultiKeys>{

    List<UserMessage> findAllByOwnerUidAndOtherUidOrderByMidAsc(Long ownerUid, Long otherUid);

    List<UserMessage> findAllByOwnerUidAndOtherUidAndMidIsGreaterThanOrderByMidAsc(Long ownerUid, Long otherUid, Long lastMid);
}
