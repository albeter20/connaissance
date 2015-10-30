package com.poc.common.filter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CORSFilter extends OncePerRequestFilter {
	private static final Logger logger = Logger.getLogger(CORSFilter.class);
//	@Override
//	public void destroy() {
//		// TODO Auto-generated method stub
//
//	}

	@Override
	public void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		// HttpServletResponse response=(HttpServletResponse)res;
		// HttpServletRequest request=(HttpServletRequest)req;
		// response.setHeader("Access-Control-Allow-Origin", "*");
		// response.setHeader("Access-Control-Allow-Methods",
		// "POST, PUT, GET, OPTIONS, DELETE");
		// response.setHeader("Access-Control-Allow-Headers",
		// "x-requested-with");
		// response.setHeader("Access-Control-Max-Age", "3600");
		// if(request.getMethod()!="OPTIONS"){
		// chain.doFilter(req,res);
		// }else{
		//
		// }
		logger.info("Inside filter");
//		HttpServletResponse response=(HttpServletResponse)res;
//		HttpServletRequest request=(HttpServletRequest)req;
		Properties prop = new Properties();
		InputStream in = getClass().getResourceAsStream(
				"/config/BankingProperties.properties");
		prop.load(in);
		in.close();
		Set<String> allowedOrigins = new HashSet<String>(Arrays.asList(prop
				.getProperty("allowed.origins").split(",")));
		System.out.println(request.getHeader("Origin"));
		if (request.getHeader("Access-Control-Request-Method") != null
				&& "OPTIONS".equals(request.getMethod())) {
			String originHeader = request.getHeader("Origin");
			if (allowedOrigins.contains(request.getHeader("Origin")))
				response.addHeader("Access-Control-Allow-Origin",originHeader);

			response.addHeader("Access-Control-Allow-Methods",
					"GET, POST, PUT, DELETE");
			response.addHeader("Access-Control-Allow-Headers", "Content-Type");
			response.addHeader("Access-Control-Max-Age", "1800");
		}
		chain.doFilter(request,response);

	}

//	@Override
//	public void init(FilterConfig arg0) throws ServletException {
//		// TODO Auto-generated method stub
//		
//	}

}
