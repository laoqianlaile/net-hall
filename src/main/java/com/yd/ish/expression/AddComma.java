package com.yd.ish.expression;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class AddComma {
	
	public String addcomma(String je){
		//给金额增加千分符。
		BigDecimal ydhkeqff = new BigDecimal(je);		      
		DecimalFormat d1 =new DecimalFormat("#,##0.00");	     
		//d1.setRoundingMode(RoundingMode.FLOOR); 	 
		//System.out.println(d1.format(ydhkeqff));
		return d1.format(ydhkeqff);
	}
}
