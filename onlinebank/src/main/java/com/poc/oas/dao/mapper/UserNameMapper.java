package com.poc.oas.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class UserNameMapper implements RowMapper<String> {
	public String mapRow(ResultSet rs, int rowNum) throws SQLException {
		String userName = new String();
		userName = rs.getString("USER_NAME");
		return userName;
	}
}
