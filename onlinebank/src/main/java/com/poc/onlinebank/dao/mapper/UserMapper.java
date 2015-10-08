package com.poc.onlinebank.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.poc.onlinebank.domain.User;

public class UserMapper implements RowMapper<User> {

	public User mapRow(ResultSet rs,int rowNum)throws SQLException{
		User user=new User();
		user.setUserid(rs.getLong("USER_ID"));
		user.setUsername(rs.getString("USERNAME"));
		user.setCustomerid(rs.getLong("CUSTOMER_ID"));
		user.setPubKey(rs.getString("USER_PUB_TOKEN"));
		user.setPrivateKey(rs.getString("USER_PVT_TOKEN"));
		return user;
	}
}
