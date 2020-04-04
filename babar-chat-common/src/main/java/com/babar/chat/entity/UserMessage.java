package com.babar.chat.entity;

import javax.persistence.*;

import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "USER_MESSAGE")
@IdClass(UserMessageMultiKeys.class)
@Data
public class UserMessage {
    @Id
    private Long mid;
    @Id
    private Long ownerUid;
    private Integer type;
    private Long otherUid;
    private Date createTime;
}