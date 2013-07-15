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
 *
 * FileName    		:  ApplicationUtil.java								        *                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  5-09-2012															*
 *                                                                  
 * Modified Date    :  5-09-2012														    *
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 5-09-2012	       Pennant	                 0.1                                        * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */

package com.pennant.app.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.log4j.Logger;

public class ApplicationUtil {
	private static Logger logger = Logger.getLogger(ApplicationUtil .class);
	static String[] wrdOnes = { "zero",  "one",   "two",  "three", "four",   "five",   "six",
		"seven", "eight", "nine", "ten",   "eleven", "twelve", "thirteen",
		"fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen" };

	static String[] wrdtens  = {"", "ten","twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"};


	static String[] wrdmillion= { "",
		"thousand",     "million",         "billion",       "trillion",       "quadrillion",
		"quintillion",  "sextillion",      "septillion",    "octillion",      "nonillion",
		"decillion",    "undecillion",     "duodecillion",  "tredecillion",   "quattuordecillion",
		"sexdecillion", "septendecillion", "octodecillion", "novemdecillion", "vigintillion" };

	public static String convertWord(long val) throws Exception {

		DecimalFormat df = new DecimalFormat();
		df.applyPattern("#,##0");
		String value =  df.format(val).toString();
		String[] values = value.split(",");

		String word =convertThousands(Long.valueOf(values[values.length-1]));

		for (int i = 1; i < values.length; i++) {
			String converted = convertThousands(Long.valueOf(values[(values.length-1)-i])) +" " + wrdmillion[i];   
			word = converted +" " +word;
		}

		return word;
	}



	private static String convertOnes(int val) throws Exception {

		if (val < 20){
			return wrdOnes[val];
		}

		long mod = val % 10;

		if(mod==0){
			return  (wrdtens[val/10]).trim();
		}
		return  (wrdtens[val/10]+" "+wrdOnes[val%10]).trim();
	}

	private static String convertThousands(long val) throws Exception {
		String word = "";

		long rem = val/100;
		long mod = val % 100;

		if (rem > 0) {
			word = wrdOnes[Long.valueOf(rem).intValue()] + " hundred";
			if (mod > 0) {
				word = word + " ";
			}
		}

		if (mod > 0) {
			word = word + convertOnes(Long.valueOf(mod).intValue());
		}
		return word;
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
		}
		catch (Exception e) {
			logger.error("Error while grouping data "+e.toString());
		} 
		return groupRecordsMap;
	}


}
