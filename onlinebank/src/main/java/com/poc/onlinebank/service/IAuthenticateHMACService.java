package com.poc.onlinebank.service;

import com.poc.onlinebank.dto.AuthenticateTO;

public interface IAuthenticateHMACService {
	
	public AuthenticateTO authenticate(AuthenticateTO authenticateTO);

}
