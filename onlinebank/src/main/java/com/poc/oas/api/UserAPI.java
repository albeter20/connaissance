package com.poc.oas.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.poc.common.CommonConstants;
import com.poc.oas.domain.UserBean;
import com.poc.oas.dto.UserTO;
import com.poc.oas.service.IUserService;

@Configuration
@PropertySource({"classpath:config/OASProperties.properties","classpath:config/Message.properties"})
@RestController
@RequestMapping(value="/api/user")
public class UserAPI {
	private static final Logger logger = Logger
			.getLogger(UserAPI.class);
	@Autowired
	IUserService userService;
	
	@Autowired
	Environment env;
	
	@RequestMapping(method=RequestMethod.POST,produces="application/json")
	public UserTO createUser(@RequestBody UserBean user,HttpServletRequest request,HttpServletResponse response ){
		response.addHeader("Access-Control-Allow-Origin", "https://oastest.com");
		UserTO userTO=new UserTO();
		try{
			logger.info("inside create account");
			logger.info(user);
			if (request.getSession() == null)
				user.setCreatedBy(CommonConstants.DEFAULT_ACCOUNT_USER);
			else if(null==request.getSession().getAttribute("loggedInUserName")||"".equals(request.getSession().getAttribute("loggedInUserName")))
				user.setCreatedBy(CommonConstants.DEFAULT_ACCOUNT_USER);
			else
				user.setCreatedBy(request.getSession()
						.getAttribute("loggedInUserName").toString());
			
			userTO = userService.createAccount(user);
			if (userTO.getStatus().equals("FAILURE")) {
				if("VALIDATION".equals(userTO.getCause()))
					response.setStatus(HttpStatus.BAD_REQUEST.value());
				else
					response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

			} else {

				response.setStatus(HttpStatus.CREATED.value());
			}

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
