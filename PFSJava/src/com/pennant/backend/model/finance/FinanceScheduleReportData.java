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
 * FileName    		:  FinanceScheduleReportData.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-01-2012    														*
 *                                                                  						*
 * Modified Date    :  31-01-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-01-2012       Pennant	                 0.1                                            * 
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


package com.pennant.backend.model.finance;

import org.apache.commons.lang.StringUtils;

public class FinanceScheduleReportData {

	private String label;
	private String schdSeqNo;
	private String schDate;
	private String pftAmount;
	private String schdPft;
	private String schdPri;
	private String cpzAmount;
	private String totalAmount;
	private String endBal;
	private String noOfDays;
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getSchdSeqNo() {
    	return schdSeqNo;
    }

	public void setSchdSeqNo(String schdSeqNo) {
    	this.schdSeqNo = schdSeqNo;
    }

	public String getSchDate() {
		return schDate;
	}

	public void setSchDate(String schDate) {
		this.schDate = schDate;
	}

	public String getPftAmount() {
    	return pftAmount;
    }

	public void setPftAmount(String pftAmount) {
    	this.pftAmount = pftAmount;
    }

	public String getSchdPft() {
    	return schdPft;
    }

	public void setSchdPft(String schdPft) {
    	this.schdPft = schdPft;
    }

	public String getSchdPri() {
    	return schdPri;
    }

	public void setSchdPri(String schdPri) {
    	this.schdPri = schdPri;
    }

	public String getCpzAmount() {
    	return cpzAmount;
    }

	public void setCpzAmount(String cpzAmount) {
    	this.cpzAmount = cpzAmount;
    }

	public String getTotalAmount() {
    	return totalAmount;
    }

	public void setTotalAmount(String totalAmount) {
    	this.totalAmount = totalAmount;
    }

	public String getEndBal() {
    	return endBal;
    }

	public void setEndBal(String endBal) {
    	this.endBal = endBal;
    }

	public String getNoOfDays() {
    	if(StringUtils.trimToEmpty(noOfDays).equals("") || StringUtils.trimToEmpty(noOfDays).equals("0")){
    		return "";
    	} else{
    		return noOfDays;
    	}
    }

	public void setNoOfDays(String noOfDays) {
    	this.noOfDays = noOfDays;
    }

}
