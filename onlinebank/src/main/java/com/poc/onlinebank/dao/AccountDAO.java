package com.poc.onlinebank.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
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

import com.poc.common.CommonConstants;
import com.poc.onlinebank.dao.mapper.AccountMapper;
import com.poc.onlinebank.domain.Account;
import com.poc.onlinebank.dto.AccountTO;

@Configuration
@PropertySource({"classpath:config/BankingProperties.properties","classpath:config/Message.properties"})
@Repository("accountDAO")
public class AccountDAO implements IAccountDAO {

	private static final Logger logger = Logger.getLogger(AccountDAO.class);
	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	Environment env;

	@Override
	public List getAccount(String accountId) {
		// TODO Auto-generated method stub
		List<Account> accountList=new ArrayList<Account>();
		try{
			String sql = "select * from ACCOUNT where ACCOUNT_NO=?";
			accountList = jdbcTemplate.query(sql, new Object[] { accountId },
					new AccountMapper());
			logger.info(accountList.size()+" no of rows returned");
		}catch(Exception e){
			logger.fatal(e.getMessage()+":"+e.getCause());
			logger.fatal(e.fillInStackTrace());
		}
		return accountList;
	}
    
	/*
	 * (non-Javadoc)
	 * @see com.poc.onlinebank.dao.IAccountDAO#createAccount(com.poc.onlinebank.domain.Account)
	 * Account creation DAO layer. It accepts Account object as input and sends back transfer
	 * object as output. Based on the fact whether account creation is a success or failure,
	 * appropriate status code and message is returned.Created account information is returned if 
	 * creation is success
	 * 
	 */
	@Override
	public AccountTO createAccount(Account account) {
		// TODO Auto-generated method stub
		
		AccountTO accountTO=new AccountTO();
		final Account accountParam=account;
		final String createStatement="INSERT INTO ACCOUNT(HOLDER_NAME,DESCRIPTION,ACCOUNT_TYPE,STATUS,OPENING_DATE,CREATE_DATE,CREATED_BY) VALUES(?,?,?,?,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,?)";
		final KeyHolder keyholder=new GeneratedKeyHolder();
		try{
			jdbcTemplate.update(
					new PreparedStatementCreator() {
						
						@Override
						public PreparedStatement createPreparedStatement(Connection conn)
								throws SQLException {
							// TODO Auto-generated method stub
							PreparedStatement ps=conn.prepareStatement(createStatement,new String[]{"ACCOUNT_NO"});
							ps.setString(1,accountParam.getAccountName());
							ps.setString(2,accountParam.getAccountDescription());
							ps.setInt(3,accountParam.getAccountType().ordinal());
							ps.setString(4,accountParam.getAccountStatus());
							ps.setString(5,"SYS");
							return ps;
						}
					},
					keyholder);
			account.setAccountNumber(keyholder.getKey().longValue());
			accountTO.setAccount(account);
			accountTO.setStatus(CommonConstants.STATUS_SUCCESS);
			accountTO.setMessage(env.getRequiredProperty("accountCreation.successMessage"));
			logger.info("Account created successfully");
		}catch(Exception e){
			accountTO.setMessage(env.getRequiredProperty("accountCreation.errorMessage"));
			accountTO.setStatus(CommonConstants.STATUS_FAILURE);
			logger.fatal(e.fillInStackTrace());
			
		}
		return accountTO;
	}

	@Override
	public long getAccountSeqNumber() {
		// TODO Auto-generated method stub

		return 0;
	}

}
