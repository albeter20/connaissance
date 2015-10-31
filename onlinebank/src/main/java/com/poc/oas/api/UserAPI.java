package com.poc.oas.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.poc.oas.domain.UserBean;
import com.poc.oas.dto.UserTO;
import com.poc.onlinebank.api.CreateAccountAPI;

@RestController
@RequestMapping(value="/api/user")
public class UserAPI {
	private static final Logger logger = Logger
			.getLogger(UserAPI.class);
	
	@RequestMapping(method=RequestMethod.POST,produces="application/json")
	public UserTO createUser(@RequestBody UserBean user,HttpServletRequest request,HttpServletResponse response ){
		response.addHeader("Access-Control-Allow-Origin", "https://oastest.com");
		UserTO userTO=new UserTO();
		try{
			logger.info("inside create account");
			logger.info(user);
			response.setStatus(HttpStatus.CREATED.value());
			userTO.setUser(user);
			userTO.setMessage("User created successfully");
			return userTO;
		}catch(Exception e){
			logger.fatal(e.getCause()+":"+e.getMessage());
			logger.fatal(e.fillInStackTrace());
			userTO.setMessage("Failed to create user");
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			return userTO;
		}
	}
}
