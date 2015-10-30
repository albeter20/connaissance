package com.poc.onlinebank.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.poc.common.CommonConstants;
import com.poc.onlinebank.domain.Account;
import com.poc.onlinebank.dto.AccountTO;
import com.poc.onlinebank.service.IAccountService;

@RestController
@RequestMapping(value = "/api/account")

public class CreateAccountAPI {
	private static final Logger logger = Logger
			.getLogger(CreateAccountAPI.class);

	@Autowired
	IAccountService accountService;

	// /*
	// * Generate account number
	// */
	// @RequestMapping(value="generateAccNo")
	// public Account generateAccountNumber(){
	// java.util.Random r = new java.util.Random();
	// int accNo = r.nextInt(10000000);
	// Account account=new Account();
	// account.setAccountNumber(accNo);
	// return account;
	// }

	/*
	 * This API is used to create a bank account for a customer All validation
	 * takes place by calling various business functions. For creating API's use
	 * @RequestBody and @ResponseBody in combination with
	 * headers={"Content-type=application/json"} to produce and consume JSON
	 */

	@RequestMapping(method = RequestMethod.POST, headers = { "Content-type=application/json" })
	public AccountTO createUserAccount(@Valid @RequestBody Account account,
			HttpServletResponse response, HttpServletRequest request) {
		AccountTO accountTO=new AccountTO();
		try {
			
			logger.info("From API");
			logger.info(account);
			/*
			 * TODO Call the validation logic
			 */
			/*
			 * Detect the user. If called directly from API,user will not be
			 * available in session. Hence default user will be passed. Else
			 * logged in user will be passed.
			 */
			response.addHeader("Access-Control-Allow-Origin", "*");
			if (request.getSession() == null)
				account.setCreatedBy(CommonConstants.DEFAULT_ACCOUNT_USER);
			else if(null==request.getSession().getAttribute("loggedInUserName")||"".equals(request.getSession().getAttribute("loggedInUserName")))
				account.setCreatedBy(CommonConstants.DEFAULT_ACCOUNT_USER);
			else
				account.setCreatedBy(request.getSession()
						.getAttribute("loggedInUserName").toString());
			/*
			 * Call the backend Service to insert account detail
			 */

			accountTO = accountService.createAccount(account);
			if (accountTO.getStatus().equals("FAILURE")) {
				if("VALIDATION".equals(accountTO.getCause()))
					response.setStatus(HttpStatus.BAD_REQUEST.value());
				else
					response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

			} else {

				response.setStatus(HttpStatus.CREATED.value());
			}

			return accountTO;
		} catch (Exception e) {
			logger.fatal(e.fillInStackTrace());
			e.printStackTrace();
			accountTO.setMessage("Account creation failed");
//			account.setAccountOperationMessage("Account creation failed");
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			return accountTO;
		}
	}

}
