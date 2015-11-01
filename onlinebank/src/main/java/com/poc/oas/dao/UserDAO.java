package com.poc.oas.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.poc.common.CommonConstants;
import com.poc.oas.domain.UserBean;
import com.poc.oas.dto.UserTO;

@Configuration
@PropertySource({"classpath:config/OASProperties.properties","classpath:config/Message.properties"})
@Repository("userDAO")
public class UserDAO implements IUserDAO{

	private static final Logger logger = Logger.getLogger(UserDAO.class);
	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	Environment env;
	
	@Override
	public List getUser(String accountId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserTO createUser(UserBean user) {
		// TODO Auto-generated method stub
		logger.info("User data:"+user);
		UserTO userTO=new UserTO();
		final UserBean userParam=user;
		final String createStatement="INSERT INTO ACCOUNT(HOLDER_NAME,DESCRIPTION,ACCOUNT_TYPE,STATUS,OPENING_DATE,CREATE_DATE,CREATED_BY) VALUES(?,?,?,?,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,?)";
		final KeyHolder keyholder=new GeneratedKeyHolder();
		try{
			
			
			userTO.setUser(user);
			userTO.setStatus(CommonConstants.STATUS_SUCCESS);
			userTO.setMessage(env.getRequiredProperty("accountCreation.successMessage"));
			logger.info("Account created successfully");
		}catch(Exception e){
			userTO.setMessage(env.getRequiredProperty("userCreation.errorMessage"));
			userTO.setStatus(CommonConstants.STATUS_FAILURE);
			logger.fatal(e.fillInStackTrace());
		}
		return userTO;
	}

	@Override
	public long getUserSeqNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	
}
