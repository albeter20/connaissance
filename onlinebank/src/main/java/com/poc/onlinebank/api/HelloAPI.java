package com.poc.onlinebank.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.poc.test.PrimaryData;

@RestController
@RequestMapping(value="/api/hello")
public class HelloAPI {
	@RequestMapping(method = RequestMethod.GET,produces="application/json")
	public PrimaryData sayHello(HttpServletRequest request,HttpServletResponse response){
		response.addHeader("Access-Control-Allow-Origin", "https://oastest.com");
		return new PrimaryData("kingshuk","chakraborty");
	}
}
