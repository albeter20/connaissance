package com.poc.oas.dto;

import com.poc.oas.domain.UserBean;

public class UserTO {

	private UserBean user;
	private String message;
	private String status;

	public UserBean getUser() {
		return user;
	}

	public void setUser(UserBean user) {
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
