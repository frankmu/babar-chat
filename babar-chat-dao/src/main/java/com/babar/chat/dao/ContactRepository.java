package com.babar.chat.dao;

import com.babar.chat.entity.ContactMultiKeys;
import com.babar.chat.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, ContactMultiKeys> {

    public List<Contact> findContactsByOwnerUidOrderByMidDesc(Long ownerUid);
}
