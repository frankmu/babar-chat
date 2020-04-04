package com.babar.chat.entity;

import javax.persistence.*;

import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "MESSAGE")
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long mid;
    private Long senderId;
    private Long recipientId;
    private String content;
    private Integer msgType;
    private Date createTime;
}