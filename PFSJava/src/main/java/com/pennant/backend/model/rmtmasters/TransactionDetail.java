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
 * FileName    		:  TransactionDetail.java                                               * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.rmtmasters;

import java.io.Serializable;

public class TransactionDetail implements Serializable {
	
    private static final long serialVersionUID = 2227130675528572705L;
    
	private String eventCode;
	private String eventDesc;
	private String tranType;
	private String transDesc;
	private String transactionCode;
	private String account;
	private String postAmount;
	private String ccy;

	public TransactionDetail() {
		
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getEventCode() {
    	return eventCode;
    }
	public void setEventCode(String eventCode) {
    	this.eventCode = eventCode;
    }
	
	public String getEventDesc() {
    	return eventDesc;
    }
	public void setEventDesc(String eventDesc) {
    	this.eventDesc = eventDesc;
    }
	
	public String getTranType() {
    	return tranType;
    }
	public void setTranType(String tranType) {
    	this.tranType = tranType;
    }
	
	public String getTransDesc() {
    	return transDesc;
    }
	public void setTransDesc(String transDesc) {
    	this.transDesc = transDesc;
    }
	
	public String getTransactionCode() {
    	return transactionCode;
    }
	public void setTransactionCode(String transactionCode) {
    	this.transactionCode = transactionCode;
    }
	
	public String getAccount() {
    	return account;
    }
	public void setAccount(String account) {
    	this.account = account;
    }
	
	public String getPostAmount() {
    	return postAmount;
    }
	public void setPostAmount(String postAmount) {
    	this.postAmount = postAmount;
    }
	public String getCcy() {
	    return ccy;
    }
	public void setCcy(String ccy) {
	    this.ccy = ccy;
    }
	
}
