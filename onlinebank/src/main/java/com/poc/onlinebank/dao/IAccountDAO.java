package com.poc.onlinebank.dao;

import java.util.List;

import com.poc.onlinebank.domain.Account;
import com.poc.onlinebank.dto.AccountTO;

public interface IAccountDAO {
	
	public List getAccount(String accountId);
	
	public AccountTO createAccount(Account account);
	
	public long getAccountSeqNumber();

}
