package com.babar.chat.dto;

import java.util.ArrayList;
import java.util.List;

public class UserDTO {
    private Long ownerUid;
    private String ownerAvatar;
    private String ownerName;
    private Long totalUnread;
    private List<ContactDTO> contactInfoList;

    public UserDTO(Long ownerUid, String ownerName, String ownerAvatar, Long totalUnread) {
        this.ownerUid = ownerUid;
        this.ownerAvatar = ownerAvatar;
        this.ownerName = ownerName;
        this.totalUnread = totalUnread;
    }

    public Long getOwnerUid() {
        return ownerUid;
    }

    public String getOwnerAvatar() {
        return ownerAvatar;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Long getTotalUnread() {
        return totalUnread;
    }

    public List<ContactDTO> getContactInfoList() {
        return contactInfoList;
    }
    
    public void setContactInfoList(List<ContactDTO> contactInfoList) {
        this.contactInfoList = contactInfoList;
    }

    public void appendContact(ContactDTO contactInfo) {
        if (contactInfoList != null) {
        } else {
            contactInfoList = new ArrayList<>();
        }
        contactInfoList.add(contactInfo);
    }
}
