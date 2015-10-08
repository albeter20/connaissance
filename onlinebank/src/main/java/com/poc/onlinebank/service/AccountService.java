package com.poc.onlinebank.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poc.onlinebank.dao.IAccountDAO;
import com.poc.onlinebank.domain.Account;
import com.poc.onlinebank.dto.AccountTO;
import com.poc.onlinebank.dto.ValidationTO;
import com.poc.onlinebank.exception.ValidationProcessException;

@Service
@Transactional
@Configuration
@PropertySource({"classpath:config/BankingProperties.properties","classpath:config/Message.properties"})
public class AccountService implements IAccountService {
	private static final Logger logger = Logger.getLogger(AccountService.class);

	@Autowired
	@Qualifier("accountDAO")
	IAccountDAO accountDAO;
	@Autowired
	Environment env;

	@Override
	public AccountTO getAccount(String accountId) {
		// TODO Auto-generated method stub
		AccountTO accountTO=new AccountTO();
		List<Account> accountList=accountDAO.getAccount(accountId);
//		if(accountList.size()==0){
//			accountTO.setStatusCode("NO_DATA");
//			accountTO.setServiceMessage("No data found for the given criteria");
//		}else{
//			accountTO.setStatusCode("SUCCESS");
//			accountTO.setServiceMessage("Account information found");
//			accountTO.setAccount(accountList.get(0));
//		}
		return accountTO; 
	}

	@Override
	public AccountTO createAccount(Account account) {
		// TODO Auto-generated method stub
		AccountTO accountTO=new AccountTO();
		ValidationTO validationTO=new ValidationTO();
		/*
		 * Implement validation logic
		 */
//		try{
//			validationTO=validate(account);
//			if("FAILURE".equals(validationTO.getValidationStatus())){
//				accountTO.setStatus(validationTO.getValidationStatus());
//				accountTO.setMessage(validationTO.getValidationMessage());
//				accountTO.setCause("VALIDATION");
//				return accountTO;
//			}
//		}catch(Exception e){
//			accountTO.setStatus("FAILURE");
//			accountTO.setCause("VALIDATION");
//			return accountTO;
//		}
		
		/*
		 * Implement the create user logic
		 * 
		 */
		
		try{
			accountTO=accountDAO.createAccount(account);
			logger.info(accountTO.getStatus());
			if("SUCCESS".equals(accountTO.getStatus())){
				long generatedAccountNo=accountTO.getAccount().getAccountNumber();
				List<Account> accountList=accountDAO.getAccount(generatedAccountNo+"");
				if(accountList.size()==0){
					accountTO.setMessage(env.getRequiredProperty("accountCreation.errorRetrieving"));
				}else
					accountTO.setAccount(accountList.get(0));
				return accountTO;
			}else{
				return accountTO;
			}
		}catch(Exception e){
			accountTO.setStatus("FAILURE");
			return accountTO;
		}
	}
	
	public ValidationTO validate(Account account){
		ValidationTO validationTO=new ValidationTO();
		try{
			
			
			validationTO.setValidationStatus("FAILURE");
			validationTO.setValidationMessage("Failure in processing input validation");
			throw new ValidationProcessException("Failure in processing input validation");
			
		}catch(ValidationProcessException e){
			validationTO.setValidationStatus("FAILURE");
			validationTO.setValidationMessage("Failure in processing input validation");
			return validationTO;
		}
	}
	
	
	
	
	
}
