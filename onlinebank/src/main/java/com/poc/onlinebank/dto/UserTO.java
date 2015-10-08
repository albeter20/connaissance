package com.poc.onlinebank.dto;

import com.poc.onlinebank.domain.User;

public class UserTO {
	
	private User user;
	private String message;
	private String status;
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
    
	
}
