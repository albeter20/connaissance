package com.poc.onlinebank.security;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.poc.onlinebank.dto.AuthenticateTO;
import com.poc.onlinebank.security.token.RESTAuthenticationToken;
import com.poc.onlinebank.security.token.RESTCredentials;
import com.poc.onlinebank.service.AuthenticateHMACService;
import com.poc.onlinebank.service.IAuthenticateHMACService;

@Component
public class RESTAuthenticationProvider implements AuthenticationProvider {

	private static final Logger logger = Logger
			.getLogger(RESTAuthenticationProvider.class);
	@Autowired
	@Qualifier("authenticateHMACService")
	IAuthenticateHMACService authenticateHMACService;
	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		// TODO Auto-generated method stub
		AuthenticateTO authenticateTO=new AuthenticateTO();
		RESTAuthenticationToken restToken=(RESTAuthenticationToken)authentication;
		
		//API Key
		String apiKey=restToken.getPrincipal();
		//Hashed blob
		RESTCredentials credentials=restToken.getCredentials();
		
		authenticateTO.setUserPublicKey(apiKey);
		authenticateTO.setRequestRawData(credentials.getRequestData());
		// Calculate HMAC of content with  secret key
		String HMAC=authenticateHMACService.authenticate(authenticateTO).getGeneratedHMAC();
		
		logger.info("Calculated HMAC signature:"+HMAC);
		logger.info("Signature from request authentication provider:"+credentials.getSignature());
		// Check if signatures match
		if(!credentials.getSignature().equals(HMAC)){
			logger.error("Service layer error:Signature didn't match");
			throw new BadCredentialsException("Invalid username or password");
		}
		logger.info("Authentication successful");
		restToken=new RESTAuthenticationToken(apiKey,credentials,restToken.getTimestamp(),null);
		return restToken;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		// TODO Auto-generated method stub
		return RESTAuthenticationToken.class.equals(authentication);
	}

}
