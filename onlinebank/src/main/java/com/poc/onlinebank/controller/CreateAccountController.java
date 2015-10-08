package com.poc.onlinebank.controller;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.poc.common.AccountType;
import com.poc.onlinebank.domain.Account;

@Controller
@RequestMapping(value="/createAccount")
@Configuration
@PropertySource({"classpath:config/BankingProperties.properties","classpath:config/Message.properties"})
public class CreateAccountController {
	private Account account;
	@Autowired
	Environment env;
	RestTemplate restTemplate=new RestTemplate();
	private static final Logger logger = Logger.getLogger(CreateAccountController.class);
	
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView openCreateForm(){
		ModelAndView mav=new ModelAndView("pages/account");
		account=new Account();
		/*String URL=env.getRequiredProperty("createService.generateAccNo.url");
		*/
		try{
//			/*
//			 * Get the auto-generated account number
//			 * Call rest API
//			 */
//			
//			account=restTemplate.getForObject(URL,Account.class);
//			logger.info("Account number is "+account.getAccountNumber());
			
			List<AccountType> accountTypeList=new LinkedList<AccountType>();
			accountTypeList.add(AccountType.CURRENT);
			accountTypeList.add(AccountType.SAVINGS);
			accountTypeList.add(AccountType.STUDENT);
			
			mav.addObject("createForm",account);
			mav.addObject("accountType",accountTypeList);
		}catch(Exception e){
			e.printStackTrace();
		}
		return mav;
	}
	
	@RequestMapping(value="create",method=RequestMethod.POST)
	public ModelAndView createAccount(@ModelAttribute Account account){

		System.out.println("Account controller");
		System.out.println("Account "+account.getAccountNumber());
		System.out.println("Account "+account.getAccountNumber());
		System.out.println("Account "+account.getAccountType());
		System.out.println("Account "+account.getAccountDescription());
		
		// Changes on 09/21-- kingshuk
		// Changes on 09/22-- Somenath

		ModelAndView mav;
		try{
		
		String URL=env.getRequiredProperty("createService.createAccount.url");
		
		/*
		 * Create account: Call rest API
		 */
		account=restTemplate.postForObject(URL,account,Account.class);
		
		mav=new ModelAndView("pages/doAccount");

		mav.addObject("account",account);
		}catch(Exception e){
		   logger.fatal("service invocation failed with message"+e.getMessage());
		   e.printStackTrace();
		   mav=new ModelAndView("pages/account");
		   account.setAccountOperationMessage(env.getRequiredProperty("accountCreation.errorMessage"));
		   mav.addObject("createForm",account);
		}
		return mav;
		
	}
}
