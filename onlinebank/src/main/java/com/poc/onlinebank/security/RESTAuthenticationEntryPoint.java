package com.poc.onlinebank.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

public class RESTAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

	private static final Logger logger = Logger
			.getLogger(RESTAuthenticationEntryPoint.class);
	@Override
	public void commence(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException authException)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		
		logger.info("From entry point Authorization header:"+request.getHeader("Authorization"));
		logger.info("From entry point Date header:"+request.getHeader("Date"));
		response.addHeader("WWW-Authenticate", "Basic realm=\"" + getRealmName() + "\"");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter writer = response.getWriter();
        writer.println("HTTP Status " + HttpServletResponse.SC_UNAUTHORIZED + " - " + authException.getMessage());
	}
	
	

}
