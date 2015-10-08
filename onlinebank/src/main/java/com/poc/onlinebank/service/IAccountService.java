package com.poc.onlinebank.service;

import com.poc.onlinebank.domain.Account;
import com.poc.onlinebank.dto.AccountTO;

public interface IAccountService {
	
	public AccountTO getAccount(String accountId);
	
	public AccountTO createAccount(Account account);

}
