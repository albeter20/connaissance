package com.poc.onlinebank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class BaseController {
	@RequestMapping(value="/",method=RequestMethod.GET)
	public String landing(){
		return "pages/main";
	}
	
	@RequestMapping(value="/main",method=RequestMethod.GET)
	public String goMain(){
		return "pages/main";
	}
}
