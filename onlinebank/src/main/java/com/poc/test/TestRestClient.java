package com.poc.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.crypto.codec.Base64;

import com.google.gson.Gson;
import com.poc.common.CommonConstants;
import com.poc.onlinebank.dto.AccountTO;
import com.poc.test.ssl.HttpsClientFactory;

public class TestRestClient {
	
	public static void main(String[] args){
		try{
			new TestRestClient().testService();
		}catch(Exception e){
			logger.fatal(e.getMessage()+":"+e.getCause());
			logger.fatal(e.fillInStackTrace());
		}
	}
	private Md5PasswordEncoder md5=new Md5PasswordEncoder();
	private static final Logger logger = Logger.getLogger(TestRestClient.class);
	public void testService() throws IOException {
		String URI="https://localhost:8443/onlinebank/api/account";
	    HttpPost request = new HttpPost(URI);
		    
	    // content plain text
	       
	    String json=IOUtils.toString(new FileInputStream(TestConstants.pathToJsonFile+"account-creation.json"));
	    String contentType = "application/json";
	    StringEntity entity = new StringEntity(json);
	    	    
	    String date = DateUtils.formatDate(new Date());

	    // create signature: method + content md5 + content-type + date + uri
	    StringBuilder toSign = new StringBuilder();
	    toSign.append(HttpMethod.POST).append("\n")
	          .append(json).append("\n")
	          .append(contentType).append("\n")
	          .append(date).append("\n")
	          .append("/onlinebank/api/account");
	    
	    logger.info("Client: Data to be hashed:"+toSign.toString());

	    request.addHeader(new BasicHeader("Date", date));
	    String apiKey=TestConstants.publicKey;
	    String signature =generateHmacSHA256Signature(toSign.toString(),TestConstants.privateKey);
	    /*
	     * Need to send api key and signature in two different header
	     */
	    request.addHeader("Authorization-key",URLEncoder.encode(apiKey,"UTF-8"));
	    request.addHeader("Authorization-sig",URLEncoder.encode(signature,"UTF-8"));
	    request.addHeader("Content-type","application/json");
	    request.setEntity(entity);
	    
	    logger.info("Authorization-sig header from client:"+signature);
	    logger.info("Authorization-key header from client:"+apiKey);
	    logger.info("Date header from client"+date);

	    // add data
	    // send request
	    HttpClient client=null;
	    try{
	    client = HttpsClientFactory.getHttpsClient();
	    
	    HttpResponse response = client.execute(request);
	    
	    String jsonResponse=EntityUtils.toString(response.getEntity(),"UTF-8");
//	    Gson gson=new Gson();
//	    AccountTO responseData=gson.fromJson(jsonResponse,AccountTO.class);
	    logger.info("Status codes:"+response.getStatusLine());
	    logger.info("Response received:"+jsonResponse);
	    
//	    int status = response.getStatusLine().getStatusCode();
//	    assert status == 200 : "Test failed";
	    }catch(Exception e){
	    	logger.fatal(e.getMessage()+":"+e.getCause());
	    	logger.fatal(e.fillInStackTrace());
	    }finally{
	    	HttpsClientFactory.releaseInstance((CloseableHttpClient)client);
	    }
	    
	}
	
	private static String generateHmacSHA256Signature(String rawDataToBeEncrypted, String key) {
		byte[] hmacData=null;
		try{
			SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(CommonConstants.ENCODING_FOR_ENCRYPTION), CommonConstants.ENCRYPTION_ALGORITHM);
			logger.info("Client:Private key from client after encoding:"+secretKey);
		    Mac mac=Mac.getInstance(CommonConstants.ENCRYPTION_ALGORITHM);
		    mac.init(secretKey);
		    hmacData=mac.doFinal(rawDataToBeEncrypted.getBytes(CommonConstants.ENCODING_FOR_ENCRYPTION));
		    logger.info("Client:Generated HMAC from client:"+hmacData);
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
