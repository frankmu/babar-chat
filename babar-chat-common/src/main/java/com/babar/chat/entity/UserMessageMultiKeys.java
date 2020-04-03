package com.babar.chat.entity;

import java.io.Serializable;

public class UserMessageMultiKeys implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected Long mid;
    protected Long ownerUid;

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public Long getOwnerUid() {
        return ownerUid;
    }

    public void setOwnerUid(Long ownerUid) {
        this.ownerUid = ownerUid;
    }
}