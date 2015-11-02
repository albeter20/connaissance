package com.poc.oas.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
		
		final String createAddress="INSERT INTO ADDRESS(STREET_LINE1,STREET_LINE2,CITY,STATEPR,COUNTRY,ZIPCODE,PRIMARY_PHONE,SECONDARY_PHONE,FAX,DATA_IMPORT_HISTORY_ID,"+
					"SECONDARY_PHONE_EXT,PRIMARY_PHONE_EXT,CREATED_BY,CREATED_DATE_TIME) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP)";
		final KeyHolder addressKey=new GeneratedKeyHolder();
		
		final String createUser="INSERT INTO USERS(USER_NAME,DISPLAY_USER_NAME,PASSWORD,FIRST_NAME,MIDDLE_NAME,LAST_NAME,PREFERRED_NAME,PREFIX,SUFFIX,TIMEZONE,EMAIL,PASSWORD_HINT_QUESTION_ID,"+
		"PASSWORD_EXPIRATION_DATE,PASSWORD_HINT_ANSWER,ADDRESS_ID,ACTIVE_SESSION,RESET_PASSWORD,LAST_LOGIN_DATE_TIME,EXT_PIN1,EXT_PIN2,EXT_PIN3,EXT_SCHOOL_ID,ACTIVATION_STATUS,DATA_IMPORT_HISTORY_ID,"+
				"DISPLAY_NEW_MESSAGE,CREATED_BY,CREATED_DATE_TIME) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP,?,?,?,?,CURRENT_TIMESTAMP,?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP)";
		final KeyHolder userKey=new GeneratedKeyHolder();
		
		final String createUserRole="INSERT INTO USER_ROLE(USER_ID,ROLE_ID,ORG_NODE_ID,ACTIVATION_STATUS,DATA_IMPORT_HISTORY_ID,CREATED_BY,CREATED_DATE_TIME)"+
					"VALUES(?,?,?,?,?,?,CURRENT_TIMESTAMP)";
		try{
			// Insert address data first
			jdbcTemplate.update(
					new PreparedStatementCreator() {
						@Override
						public PreparedStatement createPreparedStatement(Connection conn)
								throws SQLException {
							// TODO Auto-generated method stub
							PreparedStatement ps=conn.prepareStatement(createAddress,new String[]{"ADDRESS_ID"});
							ps.setString(1,userParam.getAddress().getAddressLine1());
							ps.setString(2,userParam.getAddress().getAddressLine2());
							ps.setString(3,userParam.getAddress().getCity());
							ps.setString(4,userParam.getAddress().getState());
							ps.setString(5,userParam.getAddress().getCountry());
							ps.setString(6,userParam.getAddress().getZipcode());
							ps.setString(7,userParam.getAddress().getPrimaryPhone());
							ps.setString(8,userParam.getAddress().getSecondaryPhone());
							ps.setString(9,userParam.getAddress().getFax());
							ps.setLong(10,userParam.getAddress().getDataImportHistoryId());
							ps.setString(11,userParam.getAddress().getSecondaryPhoneExt());
							ps.setString(12,userParam.getAddress().getPrimaryPhoneExt());
							ps.setInt(13,1);
							return ps;
						}
						
					},addressKey);
            
			logger.info("Generated address id:"+addressKey.getKey().longValue());
			
			// insert user data
			
			jdbcTemplate.update(
					new PreparedStatementCreator() {
						@Override
						public PreparedStatement createPreparedStatement(Connection conn)
								throws SQLException {
							// TODO Auto-generated method stub
							PreparedStatement ps=conn.prepareStatement(createUser,new String[]{"USER_ID"});
							ps.setString(1,userParam.getUserName());
							ps.setString(2,userParam.getDisplayUserName());
							ps.setString(3,userParam.getPassword());
							ps.setString(4,userParam.getFirstName());
							ps.setString(5,userParam.getMiddleName());
							ps.setString(6,userParam.getLastName());
							ps.setString(7,userParam.getPreferredName());
							ps.setString(8,userParam.getPrefix());
							ps.setString(9,userParam.getSuffix());
							ps.setString(10,userParam.getTimezone());
							ps.setString(11,userParam.getEmail());
							ps.setNull(12,java.sql.Types.NUMERIC);
							ps.setString(13,userParam.getPasswordHintAnswer());
							ps.setLong(14, addressKey.getKey().longValue());
							ps.setString(15,userParam.getActiveSession());
							ps.setString(16,"N");
							ps.setString(17, userParam.getExtPin1());
							ps.setString(18, userParam.getExtPin2());
							ps.setString(19, userParam.getExtPin3());
							ps.setString(20,userParam.getExtSchoolId());
							ps.setString(21,"AC");
							ps.setInt(22,12);
							ps.setString(23,userParam.getDisplayNewMessage());
							ps.setInt(24,1);
							return ps;
						}
						
					},userKey);
			logger.info("Generated user id:"+userKey.getKey().longValue());
			
			// Insert user_role
			
			jdbcTemplate.update(createUserRole,userKey.getKey().longValue(),userParam.getRoleId(),userParam.getOrgNodeId(),"AC",userParam.getDataImportHistoryId(),1);
			
			
			logger.info("USER,ADDRESS,USER_ROLE data inserted successfully");
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
