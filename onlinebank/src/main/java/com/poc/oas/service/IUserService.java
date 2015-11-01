package com.poc.oas.service;

import com.poc.oas.domain.UserBean;
import com.poc.oas.dto.UserTO;

public interface IUserService {

	public UserTO getAccount(String userId);

	public UserTO createAccount(UserBean userBean);

}
