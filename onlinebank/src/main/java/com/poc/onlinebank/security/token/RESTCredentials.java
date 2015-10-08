package com.poc.onlinebank.security.token;

public class RESTCredentials {
	private String requestData;
    private String signature;
    
    public RESTCredentials(String requestData, String signature) {
        this.requestData = requestData;
        this.signature = signature;
    }

    public String getRequestData() {
        return requestData;
    }

    public String getSignature() {
        return signature;
    }

}
