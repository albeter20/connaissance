package com.poc.onlinebank.dao;

import java.util.List;

import com.poc.onlinebank.domain.User;

public interface IUserDAO {

	public List<User> getUserByPublicKey(String publicKey);
}
