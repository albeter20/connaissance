package com.poc.onlinebank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DepositAccountController {
	
	@RequestMapping(value="/depositAccount",method=RequestMethod.GET)
	public ModelAndView depositAccount(){
		ModelAndView mav=new ModelAndView("pages/deposit");
		return mav;
	}

}
