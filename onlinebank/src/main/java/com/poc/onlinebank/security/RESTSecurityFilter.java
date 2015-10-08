package com.poc.onlinebank.security;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.utils.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.GenericFilterBean;

import com.poc.onlinebank.security.token.RESTAuthenticationToken;
import com.poc.onlinebank.security.token.RESTCredentials;

public class RESTSecurityFilter extends GenericFilterBean {

	private static final Logger logger = Logger
			.getLogger(RESTSecurityFilter.class);
	// Enable Multi-Read for PUT and POST requests
	private static final Set<String> METHOD_HAS_CONTENT = new TreeSet<String>(
			String.CASE_INSENSITIVE_ORDER) {
		private static final long serialVersionUID = 1L;
		{
			add("PUT");
			add("POST");
		}
	};

	private AuthenticationManager authenticationManager;
	private AuthenticationEntryPoint authenticationEntryPoint;
	private Md5PasswordEncoder md5;

	public RESTSecurityFilter(AuthenticationManager authenticationManager) {
		this(authenticationManager, new RESTAuthenticationEntryPoint());
		((RESTAuthenticationEntryPoint) this.authenticationEntryPoint)
				.setRealmName("Secure realm");
	}

	public RESTSecurityFilter(AuthenticationManager authenticationManager,
			AuthenticationEntryPoint authenticationEntryPoint) {
		this.authenticationManager = authenticationManager;
		this.authenticationEntryPoint = authenticationEntryPoint;
		this.md5 = new Md5PasswordEncoder();
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		logger.info("Inside filter");
		
		// use wrapper to read multiple times the content
		AuthenticationRequestWrapper request = new AuthenticationRequestWrapper(
				(HttpServletRequest) req);
		HttpServletResponse response = (HttpServletResponse) resp;
        
		//Check whether headers are null or not
		// If there's not credentials return...
				if ((request.getHeader("Authorization-sig") == null)||(request.getHeader("Authorization-key")==null)||(request.getHeader("Date")==null)) {
					response.setStatus(HttpStatus.UNAUTHORIZED.value());
					chain.doFilter(request, response);
					return;
				}
		
		
		// Read all the headers
		String signatureFromRequest = URLDecoder.decode(request.getHeader("Authorization-sig"),"UTF-8");
		String apiKey=URLDecoder.decode(request.getHeader("Authorization-key"),"UTF-8");
		String timestamp = request.getHeader("Date");
		
		logger.info("Authorization-sig header from filter:"+signatureFromRequest);
		logger.info("Authorization-key header from filter:"+apiKey);
		logger.info("Date header from filter"+timestamp);

		
		// get md5 content and content-type if the request is POST or PUT method
		boolean hasContent = METHOD_HAS_CONTENT.contains(request.getMethod());
		
		String data = hasContent?request.getPayload() : "";
		String contentType = hasContent ? request.getContentType() : "";
		// calculate content to sign
		StringBuilder toSign = new StringBuilder();
		toSign.append(request.getMethod()).append("\n").append(data)
				.append("\n").append(contentType).append("\n")
				.append(timestamp).append("\n").append(request.getRequestURI());

		logger.info("Unsigned data from filter:"+toSign.toString());
		
		// a rest credential is composed by request data to sign and the
		// signature
		RESTCredentials restCredential = new RESTCredentials(toSign.toString(),
				signatureFromRequest);

		// calculate UTC time from timestamp (usually Date header is GMT but
		// still...)
		Date date = null;
		try {
			date = DateUtils.parseDate(timestamp);
		} catch (Exception e) {
			logger.fatal(e.getMessage() + ":" + e.getCause());
			logger.fatal(e.fillInStackTrace());
		}

		// Create an authentication token
		Authentication authentication = new RESTAuthenticationToken(apiKey,
				restCredential, date);
		try {

			// Request the authentication manager to authenticate the token
			// (throws exception)
			Authentication successfulAuthentication = authenticationManager
					.authenticate(authentication);
			// Pass the successful token to the SecurityHolder where it can be
			// retrieved by this thread at any stage.
			SecurityContextHolder.getContext().setAuthentication(
					successfulAuthentication);
			// Continue with the Filters
			chain.doFilter(request, response);
		} catch (AuthenticationException e) {
			logger.fatal(e.getMessage() + ":" + e.getCause());
			logger.fatal(e.fillInStackTrace());
			SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response,e);
		}

	}
}
