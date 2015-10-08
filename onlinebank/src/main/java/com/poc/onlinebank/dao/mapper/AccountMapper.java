package com.poc.onlinebank.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;

import com.poc.onlinebank.domain.Account;

public class AccountMapper implements RowMapper<Account> {
	
	public Account mapRow(ResultSet rs,int rowNum)throws SQLException{
		Account account=new Account();
		account.setAccountNumber(rs.getInt("ACCOUNT_NO"));
		account.setAccountName(rs.getString("HOLDER_NAME"));
//		account.setAccountType(rs.getString("ACCOUNT_TYPE"));
		account.setAccountDescription(rs.getString("DESCRIPTION"));
		account.setAccountStatus(rs.getString("STATUS"));
		account.setAccountOpeningDate(rs.getTimestamp("OPENING_DATE"));
//		account.setAccountClosingDate(rs.getTimestamp("CLOSING_DATE"));
		System.out.println("heelo"+rs.getString("CLOSING_DATE"));
		if(null==rs.getString("CLOSING_DATE")||"null".equals(rs.getString("CLOSING_DATE")))
			account.setAccountClosingDate(null);
		else
			account.setAccountClosingDate(Timestamp.valueOf(rs.getString("CLOSING_DATE")));
		account.setBalance(rs.getBigDecimal("BALANCE"));
		account.setAvailableBalance(rs.getBigDecimal("AVAILABLE_BALANCE"));
		return account;
	}

}
