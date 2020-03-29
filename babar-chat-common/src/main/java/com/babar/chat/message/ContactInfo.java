package com.babar.chat.message;

import java.util.Date;

public class ContactInfo {
    private Long otherUid;
    private String otherName;
    private String otherAvatar;
    private Long mid;
    private Integer type;
    private String content;

    public Long getConvUnread() {
        return convUnread;
    }

    private Long convUnread;
    private Date createTime;

    public ContactInfo(Long otherUid, String otherName, String otherAvatar, Long mid, Integer type, String content, Long convUnread, Date createTime) {
        this.otherUid = otherUid;
        this.otherName = otherName;
        this.otherAvatar = otherAvatar;
        this.mid = mid;
        this.type = type;
        this.content = content;
        this.convUnread = convUnread;
        this.createTime = createTime;
    }

    public Long getOtherUid() {
        return otherUid;
    }

    public Long getMid() {
        return mid;
    }

    public Integer getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getOtherName() {
        return otherName;
    }

    public String getOtherAvatar() {
        return otherAvatar;
    }

    public Date getCreateTime() {
        return createTime;
    }
}