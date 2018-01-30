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
 * FileName    		:  FrequencyDetails.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
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
package com.pennant.app.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.backend.model.ErrorDetail;

public class FrequencyDetails implements Serializable {
    
    private static final long serialVersionUID = -7274099191177820109L;
    
	private String frequency;
	private String frequencyCode;
	private int frequencyMonth;
	private int frequencyDay;
	private int terms;
	private String  frequencyDescription;
	private Date  nextFrequencyDate;
	private ErrorDetail errorDetails;
	private List<Calendar> scheduleList;
	
	public FrequencyDetails(){
		super();
	} 
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public FrequencyDetails(String frequency ){
		this.frequency = frequency;
	}
	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	public char getCharFrequencyCode() {
		if(StringUtils.trimToEmpty(frequencyCode).length()>0){
			return frequencyCode.trim().charAt(0);
		}
		return ' ';
	}

	public String getFrequencyCode() {
		return frequencyCode;
	}
	public void setFrequencyCode(String frequencyCode) {
		this.frequencyCode = frequencyCode;
	}

	public int getFrequencyMonth() {
		return frequencyMonth;
	}
	public void setFrequencyMonth(int frequencyMonth) {
		this.frequencyMonth = frequencyMonth;
	}

	public int getFrequencyDay() {
		return frequencyDay;
	}
	public void setFrequencyDay(int frequencyDay) {
		this.frequencyDay = frequencyDay;
	}

	public int getTerms() {
		return terms;
	}
	public void setTerms(int terms) {
		this.terms = terms;
	} 
	
	public String getFrequencyDescription() {
		return frequencyDescription;
	}
	public void setFrequencyDescription(String frequencyDescription) {
		this.frequencyDescription = frequencyDescription;
	}

	public Date getNextFrequencyDate() {
		return nextFrequencyDate;
	}
	public void setNextFrequencyDate(Date nextFrequencyDate) {
		this.nextFrequencyDate = nextFrequencyDate;
	}

	public ErrorDetail getErrorDetails() {
		return errorDetails;
	}
	public void setErrorDetails(ErrorDetail errorDetails) {
		this.errorDetails = errorDetails;
	}

	public List<Calendar> getScheduleList() {
		return scheduleList;
	}
	public void setScheduleList(List<Calendar> scheduleList) {
		this.scheduleList = scheduleList;
	}
	
}
