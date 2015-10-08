package com.poc.onlinebank.domain;

import java.util.Date;

public class User {
	
	private long userid;
	private String username;
	private long customerid;
	private String pubKey;
	private String privateKey;
	private Date createdDate;
	private Date modifyDate;
	private String createdBy;
	private String modifiedBy;
	
	@Override
	public String toString() {
		return "User [userid=" + userid + ", username=" + username
				+ ", customerid=" + customerid + ", pubKey=" + pubKey
				+ ", privateKey=" + privateKey + ", createdDate=" + createdDate
				+ ", modifyDate=" + modifyDate + ", createdBy=" + createdBy
				+ ", modifiedBy=" + modifiedBy + "]";
	}

	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public long getCustomerid() {
		return customerid;
	}

	public void setCustomerid(long customerid) {
		this.customerid = customerid;
	}

	public String getPubKey() {
		return pubKey;
	}

	public void setPubKey(String pubKey) {
		this.pubKey = pubKey;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	
}
