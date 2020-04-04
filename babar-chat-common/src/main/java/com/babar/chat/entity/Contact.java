package com.babar.chat.entity;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "CONTACT")
@IdClass(ContactMultiKeys.class)
@Data
public class Contact {
	@Id
	private Long ownerUid;
	@Id
	private Long otherUid;
	private Long mid;
	private Integer type;
	private Date createTime;
}