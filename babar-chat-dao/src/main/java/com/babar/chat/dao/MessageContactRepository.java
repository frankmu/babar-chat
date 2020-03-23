package com.babar.chat.dao;

import com.babar.chat.entity.ContactMultiKeys;
import com.babar.chat.entity.MessageContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageContactRepository extends JpaRepository<MessageContact, ContactMultiKeys> {

    public List<MessageContact> findMessageContactsByOwnerUidOrderByMidDesc(Long ownerUid);
}
