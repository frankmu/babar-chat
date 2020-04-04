package com.babar.chat.entity;

import javax.persistence.*;

import lombok.Data;

@Entity
@Table(name = "USER")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long uid;
    private String username;
    private String password;
    private String email;
    private String avatar;
}