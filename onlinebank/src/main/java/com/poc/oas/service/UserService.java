package com.poc.oas.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poc.oas.dao.IUserDAO;
import com.poc.oas.domain.UserBean;
import com.poc.oas.dto.UserTO;
import com.poc.onlinebank.dto.ValidationTO;

@Service
@Transactional
@Configuration
@PropertySource({"classpath:config/OASProperties.properties","classpath:config/Message.properties"})
public class UserService implements IUserService {
	private static final Logger logger = Logger.getLogger(UserService.class);
	
	@Autowired
	@Qualifier("userDAO")
	IUserDAO userDAO;
	@Autowired
	Environment env;
	
	@Override
	public UserTO getUser(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
//	@Transactional
	public UserTO createUser(UserBean userBean) {
		// TODO Auto-generated method stub
		UserTO userTO=new UserTO();
		ValidationTO validationTO=new ValidationTO();
		
		/*
		 * Implement the create user logic
		 * 
		 */
		
		try{
			/*
			 * Generate userName first
			 * 
			 */
			String processedUserName=generateUserName(userBean.getFirstName(),userBean.getLastName());
			logger.info("Generated user name:"+processedUserName);
			
			userBean.setDisplayUserName(processedUserName);
			userBean.setUserName(processedUserName);
			userBean.setResetPassword("T");
			userBean.setDisplayNewMessage("Y");
			
			userTO=userDAO.createUser(userBean);
			logger.info(userTO.getStatus());
			if("SUCCESS".equals(userTO.getStatus())){
				long generatedUserId=userTO.getUser().getUserId();
				List<UserBean> userList=userDAO.getUser(generatedUserId+"");
				if(userList==null||userList.size()==0){
					userTO.setMessage(env.getRequiredProperty("userCreation.errorRetrieving"));
				}else
					userTO.setUser(userList.get(0));
				return userTO;
			}else{
				return userTO;
			}
		}catch(Exception e){
			logger.fatal(e.getCause()+":"+e.getMessage());
			userTO.setStatus("FAILURE");
			userTO.setMessage(env.getRequiredProperty("userCreation.errorMessage"));
			return userTO;
		}
	}
	
	public String generateUserName(String firstName,String lastName){
		String unprocessedUserName=((firstName.length()>10?firstName.substring(0,10):firstName)+"_"+(lastName.length()>10?lastName.substring(0,10):lastName)).toLowerCase();
		String processedUserName=new String();
		try{
			List<String> userNameList=userDAO.fetchUserName(unprocessedUserName);
			if(userNameList==null||userNameList.size()==0)
				processedUserName=unprocessedUserName;
			else if(userNameList.size()==1)
				processedUserName=unprocessedUserName+"_01";
			else{
				processedUserName=unprocessedUserName+"_"+(String.format("%02d",userNameList.size()));
			}
		}catch(Exception e){
			logger.fatal(e.getCause()+":"+e.getMessage());
			logger.fatal(e.fillInStackTrace());
		}
		return processedUserName;
	}

}
