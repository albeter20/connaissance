package com.poc.onlinebank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.poc.onlinebank.domain.Account;

@Controller
public class WithdrawController {
	
	@RequestMapping(value="withdrawMoney",method=RequestMethod.GET)
	public ModelAndView withdrawMoney(){
		ModelAndView mav=null;
		Account account=null;
		if(account==null){
			mav=new ModelAndView("pages/noAcc");
			return mav;
		}
		else
			mav=new ModelAndView("pages/doWithdraw");
		return mav;
	}

}
