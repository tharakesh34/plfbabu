package com.pennant.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.app.util.DateUtility;
import com.pennant.coreinterface.exception.EquationInterfaceException;


public class Testing {

	/**
	 * @param args
	 * @throws EquationInterfaceException 
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws EquationInterfaceException, UnknownHostException {
		
		List<String> list = new ArrayList<String>();
		System.out.println("Date is"+ new Date());
		int month = DateUtility.getMonth(new Date());
	
		if(month == 11) {
			month = month + 2;
		}
		
		System.out.println("Test");
		//System.out.println(DateUtility.getYearEndDate(new Date()));
		String ipAddress[] = InetAddress.getLocalHost().toString().split("/");
		System.out.println(ipAddress[0]+"----"+ipAddress[1]);
		/*list.add("noOfDays");
		list.add("PRI");
		list.add("PFTSB");
		list.add("PFT");


		String str = "if((noOfDays <=  30)){ " +
				"Result =0;  return;}else if((noOfDays >  30) " +
				"&& (noOfDays <=  90)){  Result =(PRISB + PFTSB) " +
				"* 20/100;  return;}else {  Result =PFTSB+PRISB;  return;}" ;
		
		
		String[] strings = (str.split("[\\s\\(\\)\\+\\>\\<\\=\\-\\/\\*\\;]"));
		
		for (int i = 0; i < strings.length; i++) {
			if(list.contains(strings[i])){
				System.out.println(strings[i]);
			}
		}*/
		
		
		/*
		Map<String,String> map = new HashMap<String, String>(2);
		if(map.size() >0 ){
			list.addAll(map.values());
		}
		
		if(list.size()>0){
			System.out.println("wrong");
		}*/
		
		
	//	System.out.println(getPercentageValue(new BigDecimal(91667),new BigDecimal(80)));
		
		
		//new CalendarInterfaceServiceEquationImpl().calendarUpdate();

	
	
	}
	
	private static BigDecimal getPercentageValue(BigDecimal dividend, BigDecimal divider){
		return (dividend.multiply(unFormateAmount(divider,2).divide(
				new BigDecimal(100)))).divide(new BigDecimal(100),RoundingMode.HALF_DOWN);
	}
	
	public static BigDecimal unFormateAmount(BigDecimal amount, int dec) {
		if (amount == null) {
			return new BigDecimal(0);
		}
		BigInteger bigInteger = amount.multiply(new BigDecimal(Math.pow(10, dec))).toBigInteger();
		return new BigDecimal(bigInteger);
	}
	
	
}
