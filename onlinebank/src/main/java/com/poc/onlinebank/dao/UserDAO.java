package com.poc.onlinebank.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.poc.onlinebank.dao.mapper.UserMapper;
import com.poc.onlinebank.domain.User;

@Configuration
@PropertySource({ "classpath:config/BankingProperties.properties",
		"classpath:config/Message.properties" })
@Repository("userAccountDAO")
public class UserDAO implements IUserDAO {

	private static final Logger logger = Logger.getLogger(UserDAO.class);
	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	Environment env;

	@Override
	public List<User> getUserByPublicKey(String publicKey) {
		// TODO Auto-generated method stub
		List<User> userList = new ArrayList<User>();
		try {
			String sql = "select * from USER where USER_PUB_TOKEN=?";
			userList = jdbcTemplate.query(sql, new Object[] { publicKey },
					new UserMapper());
			logger.info(userList.size() + " no of rows returned");
		} catch (Exception e) {
			logger.fatal(e.getMessage() + ":" + e.getCause());
			logger.fatal(e.fillInStackTrace());
		}
		return userList;
	}

}
