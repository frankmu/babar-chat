package com.babar.chat.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.babar.chat.entity.MessageRelation;
import com.babar.chat.entity.RelationMultiKeys;

import java.util.List;

@Repository
public interface MessageRelationRepository extends JpaRepository<MessageRelation, RelationMultiKeys>{

    List<MessageRelation> findAllByOwnerUidAndOtherUidOrderByMidAsc(Long ownerUid, Long otherUid);

    List<MessageRelation> findAllByOwnerUidAndOtherUidAndMidIsGreaterThanOrderByMidAsc(Long ownerUid, Long otherUid, Long lastMid);
}
