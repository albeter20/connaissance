package com.poc.onlinebank.service;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poc.common.CommonConstants;
import com.poc.onlinebank.dao.IUserDAO;
import com.poc.onlinebank.domain.User;
import com.poc.onlinebank.dto.AuthenticateTO;

@Service("authenticateHMACService")
@Transactional
@Configuration
@PropertySource({ "classpath:config/BankingProperties.properties",
		"classpath:config/Message.properties" })
public class AuthenticateHMACService implements IAuthenticateHMACService {

	@Autowired
	@Qualifier("userAccountDAO")
	IUserDAO userDAO;
	@Autowired
	Environment env;
	private static final Logger logger = Logger
			.getLogger(AuthenticateHMACService.class);
	
	

	@Override
	public AuthenticateTO authenticate(AuthenticateTO authenticateTO) {
		// TODO Auto-generated method stub
		/*
		 * Get the private Key
		 */
		String publicKey = authenticateTO.getUserPublicKey();
		List<User> userList = new ArrayList<User>();
		userList = userDAO.getUserByPublicKey(publicKey);
		if (userList.size() != 0) {
			User user = userList.get(0);
			String privateKey = user.getPrivateKey();
			String generateHmacSHA256Signature = null;
			try {
				String dataTobeHashed = authenticateTO.getRequestRawData();
				
				generateHmacSHA256Signature=generateHmacSHA256Signature(dataTobeHashed, privateKey);
				
				logger.info("Service: verification end dataTobeHashed:"+dataTobeHashed);
				logger.info("Generated HMAC Code:"
						+ generateHmacSHA256Signature);
                authenticateTO.setGeneratedHMAC(generateHmacSHA256Signature);
				
			} catch (Exception e) {
				authenticateTO.setAuthenticationStatus(false);
				authenticateTO.setAuthenticationMessage(env
						.getRequiredProperty("authentication.errorMessage"));
				return authenticateTO;
			}

		} else {
			authenticateTO.setAuthenticationStatus(false);
			authenticateTO.setAuthenticationMessage(env
					.getRequiredProperty("authentication.errorMessage"));
		}

		return authenticateTO;
	}

	private static String generateHmacSHA256Signature(String rawDataToBeEncrypted, String key) {
		byte[] hmacData=null;
		try{
			SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(CommonConstants.ENCODING_FOR_ENCRYPTION), CommonConstants.ENCRYPTION_ALGORITHM);
			logger.info("Service:Private key from service after encoding:"+secretKey);
		    Mac mac=Mac.getInstance(CommonConstants.ENCRYPTION_ALGORITHM);
		    mac.init(secretKey);
		    hmacData=mac.doFinal(rawDataToBeEncrypted.getBytes(CommonConstants.ENCODING_FOR_ENCRYPTION));
		    logger.info("Service:Generated HMAC from servcie:"+hmacData);
		    if (CommonConstants.ENCODE_HASH_AS_BASE64) {
                return new String(Base64.encode(hmacData), CommonConstants.ENCODING_FOR_ENCRYPTION);
            } else {
                return new String(hmacData, CommonConstants.ENCODING_FOR_ENCRYPTION);
            }
			
		}catch(Exception e){
			logger.fatal(e.getMessage()+":"+e.getCause());
			logger.fatal(e.fillInStackTrace());
			return "";
		}
	}
}
