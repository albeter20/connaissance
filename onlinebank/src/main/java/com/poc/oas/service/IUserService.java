package com.poc.oas.service;

import com.poc.oas.domain.UserBean;
import com.poc.oas.dto.UserTO;

public interface IUserService {

	public UserTO getUser(String userId);

	public UserTO createUser(UserBean userBean);
	
	public String generateUserName(String firstName,String lastName);

}
