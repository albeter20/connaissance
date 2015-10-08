package com.poc.onlinebank.security.token;

import java.util.Collection;
import java.util.Date;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class RESTAuthenticationToken extends
		UsernamePasswordAuthenticationToken {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Date timestamp;

    // this constructor creates a non-authenticated token (see super-class)
    public RESTAuthenticationToken(String principal, RESTCredentials credentials, Date timestamp) {
        super(principal, credentials);
        this.timestamp = timestamp;
    }
	
    // this constructor creates an authenticated token (see super-class)
    public RESTAuthenticationToken(String principal, RESTCredentials credentials, Date timestamp, Collection authorities) {
        super(principal, credentials, authorities);
        this.timestamp = timestamp;
    }

    @Override
    public String getPrincipal() {
        return (String) super.getPrincipal();
    }
    
    @Override
    public RESTCredentials getCredentials() {
        return (RESTCredentials) super.getCredentials();
    }
    
    public Date getTimestamp() {
        return timestamp;
    }

}
