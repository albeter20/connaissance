package com.poc.test;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.crypto.codec.Base64;
public class TestHMAC {
	
	public static void main(String[] args){
		new TestHMAC().getHMAC();
	}
	
	private static String getHMAC(){
		String privateKey="api-pk-9999";
		String salt="hmaciscool";
		String publicKey="api-1234";
		String generateHmacSHA256Signature=null;
		try{
			generateHmacSHA256Signature=generateHmacSHA256Signature(salt,privateKey);
			System.out.println(generateHmacSHA256Signature);
		}catch(Exception e){
			e.printStackTrace();
		}
		return generateHmacSHA256Signature;
	}
	private static String generateHmacSHA256Signature(String data,String key){
		byte[] hmacData=null;
		try{
			SecretKeySpec secretKey=new SecretKeySpec(key.getBytes("UTF-8"),"HmacSHA256");
			Mac mac=Mac.getInstance("HmacSHA256");
			mac.init(secretKey);
			hmacData=mac.doFinal(data.getBytes("UTF-8"));
			return new String(Base64.encode(hmacData), "UTF-8");
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
	}

}
