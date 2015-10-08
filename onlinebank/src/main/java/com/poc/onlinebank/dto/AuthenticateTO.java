package com.poc.onlinebank.dto;

import com.poc.onlinebank.domain.User;

public class AuthenticateTO {

	// private User user;
	// private String callingCountry;
	// private String requestHMACCode;

	private String userPublicKey;
	private String requestRawData;
	
	private String generatedHMAC;

	private String authenticationMessage;

	private boolean authenticationStatus;

	public String getAuthenticationMessage() {
		return authenticationMessage;
	}

	public void setAuthenticationMessage(String authenticationMessage) {
		this.authenticationMessage = authenticationMessage;
	}

	public boolean isAuthenticationStatus() {
		return authenticationStatus;
	}

	public void setAuthenticationStatus(boolean authenticationStatus) {
		this.authenticationStatus = authenticationStatus;
	}

	public String getUserPublicKey() {
		return userPublicKey;
	}

	public void setUserPublicKey(String userPublicKey) {
		this.userPublicKey = userPublicKey;
	}

	public String getRequestRawData() {
		return requestRawData;
	}

	public void setRequestRawData(String requestRawData) {
		this.requestRawData = requestRawData;
	}

	public String getGeneratedHMAC() {
		return generatedHMAC;
	}

	public void setGeneratedHMAC(String generatedHMAC) {
		this.generatedHMAC = generatedHMAC;
	}
	
	

}
