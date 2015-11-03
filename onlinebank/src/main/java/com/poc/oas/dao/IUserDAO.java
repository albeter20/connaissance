package com.poc.oas.dao;

import java.util.List;

import com.poc.oas.domain.UserBean;
import com.poc.oas.dto.UserTO;

public interface IUserDAO {

	public List getUser(String accountId);
	
	public UserTO createUser(UserBean user);
	
	public long getUserSeqNumber();
	
	public List<String> fetchUserName(String userName);
}
