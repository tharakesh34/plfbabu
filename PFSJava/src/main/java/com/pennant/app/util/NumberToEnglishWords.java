/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  NumberToEnglishWords.java                                           * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-09-2011    														*
 *                                                                  						*
 * Modified Date    :  17-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *17-09-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 * 13-06-2018       Siva					 0.4        Receipt Print Option Added 			*
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.app.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.dao.applicationmaster.CurrencyDAO;
import com.pennant.backend.model.applicationmaster.Currency;

public class NumberToEnglishWords {
	private static Logger logger = Logger.getLogger(NumberToEnglishWords.class);
	private static CurrencyDAO currencyDAO;

	static String[] wrdOnes = { "zero",  "one",   "two",  "three", "four",   "five",   "six",
		"seven", "eight", "nine", "ten",   "eleven", "twelve", "thirteen",
		"fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen" };

	static String[] wrdtens  = {"", "ten","twenty", "thirty", "fourty", "fifty", "sixty", "seventy", "eighty", "ninety"};

	static String[] wrdmillion= { "",
		"thousand",     "million",         "billion",       "trillion",       "quadrillion",
		"quintillion",  "sextillion",      "septillion",    "octillion",      "nonillion",
		"decillion",    "undecillion",     "duodecillion",  "tredecillion",   "quattuordecillion",
		"sexdecillion", "septendecillion", "octodecillion", "novemdecillion", "vigintillion" };
	static String[] wrdCrores= { "thousand" ,"lakhs"};

	/**
	 * This method returns text value of given number  in Billions or lakhs format
	 * @param number
	 * @return number in words like "ten" for 10 (String)
	 * @throws Exception
	 */
	public static String  getNumberToWords(BigInteger number) throws Exception{
		logger.debug("Entering ");
		String sign = "";
		if(number.compareTo(BigInteger.ZERO)<0){
			sign="-";
		}
		if(ImplementationConstants.INDIAN_IMPLEMENTATION){
			return StringUtils.trimToEmpty(sign+" "+convertWordToCrores(number));
		}
		logger.debug("Leaving ");
		return StringUtils.trimToEmpty(sign+" "+convertWordToBillions(number));

	}
	/**
	 * This method returns text value of given amount with currency symbol of given ccyCode
	 * @param amount
	 * @param ccyCode
	 * @return Amount in words with currency symbol (String)
	 * @throws Exception
	 */

	public static String getAmountInText(BigDecimal amount,String ccyCode) throws Exception{
		logger.debug("Entering ");
		boolean minus = false;
		String strAmount="";
		String minorCcyDesc="";
		String currnecySymbol="";
		if(amount.compareTo(BigDecimal.ZERO)<0){
			minus=true;
		}
		int decPos = amount.toPlainString().lastIndexOf("."); 
		int ccyDecPos = 0;
		if(StringUtils.isNotEmpty(ccyCode.trim())){
			Currency currency = CurrencyUtil.getCurrencyObject(ccyCode);
			if(currency!=null){
				/*	if minor currency is more than zero and  minor currency Description is not null in Currency table */
				if(currency.getCcyMinorCcyDesc()!=null &&(decPos>0)){
					minorCcyDesc=currency.getCcyMinorCcyDesc();
				}
				/*if Currency symbol is not null in Currency table*/
				if(currency.getCcySymbol()!=null ){
					currnecySymbol=currency.getCcySymbol();
				}
				ccyDecPos = currency.getCcyEditField();
			}
		}
		if(decPos>0){// if after decimal point is greater than zero than only add minorCcyDesc
			String majorCcy = amount.toString().substring(0, decPos);
			String minorCcy = amount.toString().substring(decPos+1);
			minorCcy = StringUtils.rightPad(minorCcy, ccyDecPos, '0');
			
			if(StringUtils.isNotBlank(currnecySymbol)){
				strAmount = StringUtils.trimToEmpty(currnecySymbol +" "+ getNumberToWords(new BigInteger(majorCcy)) + " and " 
				+ getNumberToWords(new BigInteger(minorCcy)) +" " +minorCcyDesc);
			}else{
				strAmount = StringUtils.trimToEmpty(getNumberToWords(new BigInteger(majorCcy)) + " " + ccyCode +  " and " 
				+ getNumberToWords(new BigInteger(minorCcy)) +" " +minorCcyDesc);
			}

		}else{
			strAmount = currnecySymbol +" "+ getNumberToWords(amount.toBigInteger());
		}		
		if(minus){
			strAmount = "Dr "+ strAmount;
		}
		logger.debug("Leaving ");
		return strAmount;	
	}
	/**
	 * This method converts given number into words in millions format 
	 * @param number
	 * @return number in words (String)
	 * @throws Exception
	 */

	private static String convertWordToBillions(BigInteger number) throws Exception {
		logger.debug("Entering ");
		String word="";
		if(number.compareTo(BigInteger.ZERO) == 0){
			return wrdOnes[0];
		}
		DecimalFormat df = new DecimalFormat();
		df.applyPattern("#,##0");
		String value =  df.format(number);
		String[] values = value.split(",");
		word = convertThousands(Long.parseLong(values[values.length - 1]));

		for (int i = 1; i < values.length; i++) {
			String converted = convertThousands(Long.parseLong(values[(values.length - 1) - i])) + " " + wrdmillion[i];
			word = converted.concat(" " +word);
		}
		logger.debug("Leaving ");
		return word;
	}
	/**
	 * This method converts given number into words in Crores format 
	 * @param number
	 * @return number in words (String)
	 * @throws Exception
	 */
	private static String convertWordToCrores(BigInteger number) throws Exception {
		logger.debug("Entering ");
		String result="";
		int count=0;
		if(number.compareTo(BigInteger.ZERO) == 0){
			return wrdOnes[0];
		}
		String numString[]=new String[10];
		String valString=number.toString();
		/*Take number as string and split the string with length 7 e.g. if number is 12345678912345
		 * numString[0]=1234567,numString[1]=8912345
		 * 
		 * */
		for(int i=0;i<=valString.length();i++){ 
			count++;
			numString[i]=valString.substring(valString.length()<7?0:valString.length()-7, valString.length());
			valString=valString.substring(0,valString.length()-numString[i].length());

		}
		/*convert number to words of each element in numString[] and append the result by calling formatInLakhs()*/
		for (int j = 0; j < count; j++) {
			DecimalFormat df = new DecimalFormat();
			df.applyPattern("0000000");

			if(j==count-1){
				result=formatInLakhs(df.format(Long.valueOf(numString[j])),result);
			} else {
				result=" crores "+formatInLakhs(df.format(Long.valueOf(numString[j])),result);
			}
		}
		logger.debug("Leaving ");
		return result;
	}
	/**
	 * This method returns amount in crores format
	 * @param snumber
	 * @return
	 * @throws Exception 
	 */
	private static String formatInLakhs(String snumber,String oldresult) throws Exception{
		logger.debug("Entering ");
		String result="";
		//XXnnnnn
		int lakhs  = Integer.parseInt(snumber.substring(0,2)); 
		//nnXXnnn
		int thousands = Integer.parseInt(snumber.substring(2,4)); 
		//nnnnXXX
		int hundreds= Integer.parseInt(snumber.substring(4,7));  

		String tradLakhs;
		switch (lakhs) {
		case 0:
			tradLakhs = "";
			break;
		case 1 :
			tradLakhs = convertThousands(lakhs) 
			+ " lakh ";
			break;
		default :
			tradLakhs = convertThousands(lakhs) 
			+ " lakh ";
		}
		result =  result + tradLakhs;
		String tradThousands;
		switch (thousands) {
		case 0:
			tradThousands = "";
			break;
		case 1 :
			tradThousands = convertThousands(thousands)+" Thousand ";
			break;
		default :
			tradThousands = convertThousands(thousands)+" Thousand "; 
		}
		result =    result + tradThousands;
		String tradhundreds;
		switch (hundreds) {
		case 0:
			tradhundreds = "";
			break;
		case 1 :
			tradhundreds = convertThousands(hundreds);
			break;
		default :
			tradhundreds = convertThousands(hundreds); 
		}
		result =    result + tradhundreds;
		logger.debug("Leaving ");
		/*append old result after new result e.g result="two hundred" old result="crores ,twenty lakhs twenty thousand"
		 *  than return  "two hundred crores ,twenty lakhs twenty thousand
		 *  */
		return result+oldresult;
	}

	/**
	 * Convert number to words below one thousand by calling convertOnes()
	 * @param number
	 * @return
	 * @throws Exception
	 */
	private static String convertThousands(long number) throws Exception {
		logger.debug("Entering ");
		String word = "";
		long rem = number/100;
		long mod = number % 100;
		if (rem > 0) {
			word = wrdOnes[Long.valueOf(rem).intValue()] + " hundred";
			if (mod > 0) {
				word = word + " ";
			}
		}
		if (mod > 0) {
			word = word + convertOnes(Long.valueOf(mod).intValue());
		}
		logger.debug("Leaving ");
		return word;
	}

	/**
	 * This method convert number to words below hundred
	 * @param number
	 * @return
	 * @throws Exception
	 */
	private static String convertOnes(int number) throws Exception {
		logger.debug("Entering ");

		if (number < 20){
			return wrdOnes[number];
		}
		long mod = number % 10;
		if(mod==0){
			return  (wrdtens[number/10]).trim();
		}
		logger.debug("Leaving ");
		return  (wrdtens[number/10]+" "+wrdOnes[number%10]).trim();

	}
	
	/**
	 * This method reduces multiple database hits 
	 * 1 )This method takes input as List of objects and sort it with "sortField" and compare the values against 
	 * compareMethod(like getFloorCode()) .Then splits the main list into  sublists against compareMethod value 
	 * 2) Put Sublists into map Where key is compareMethod's value and value is sublist created against that value
	 * E.g : Main list is List of  PremiseUnits.This method splits this list into sublists by floorCode .
	 *      Keeps floor code as key and value is  sublist i.e get against FloorCode from main list . 
	 *    
	 * @param <T>
	 * @param childRecordsList
	 * @param sortField
	 * @param compareMethod
	 * @return 
	 */
	public static <T> Map<Object,List<T>>  getSubListMapGroupingByField(List<T> childRecordsList,String sortField,String compareMethod){
		Map<Object,List<T>> groupRecordsMap=new HashMap<Object,List<T>>();
		@SuppressWarnings("unchecked")
		Comparator<Object> comp = new BeanComparator(sortField);
		List<T> aGroupedObjectsList=new ArrayList<T>();
		int size=childRecordsList.size();
		try {
			//Sorting the list with sortField
			Collections.sort(childRecordsList,comp);
			for (int i = 0; i < size; i++) {
				Object objectOne = childRecordsList.get(i).getClass().getMethod(compareMethod).invoke(childRecordsList.get(i));
				Object objectTwo = null ;
				if(i!=size-1){
					objectTwo =childRecordsList.get(i+1).getClass().getMethod(compareMethod).invoke(childRecordsList.get(i+1));
				}
				//if not last Element and field is Equal to next field
				if( i!=size-1  &&  objectOne.equals(objectTwo)){
					//If object is already in Map 
					if(groupRecordsMap.containsKey(objectOne)){
						aGroupedObjectsList.add(childRecordsList.get(i));
					}else{
						//If object is  new Entry
						aGroupedObjectsList=new ArrayList<T>();
						aGroupedObjectsList.add(childRecordsList.get(i));
					}
					groupRecordsMap.put(objectOne, aGroupedObjectsList);
				}else{
					if(groupRecordsMap.containsKey(objectOne)){
						//If object is already in Map (in case last element is already in map)
						List<T> tempList=(List<T>) groupRecordsMap.get(objectOne);
						tempList.add(childRecordsList.get(i));
						groupRecordsMap.put(objectOne, tempList);	
					}else{
						//If object is  new Entry
						aGroupedObjectsList=new ArrayList<T>();
						aGroupedObjectsList.add(childRecordsList.get(i));
						groupRecordsMap.put(objectOne, aGroupedObjectsList);
					}
				}   
			}
		} catch (Exception e) {
			logger.error("Exception: Grouping the data ", e);
		}
		
		return groupRecordsMap;
	}

	//GETTERS AND SETTERS
	public  void setCurrencyDAO(CurrencyDAO currencyDAO) {
		NumberToEnglishWords.currencyDAO = currencyDAO;
	}

	public static CurrencyDAO getCurrencyDAO() {
		return currencyDAO;
	}
}

