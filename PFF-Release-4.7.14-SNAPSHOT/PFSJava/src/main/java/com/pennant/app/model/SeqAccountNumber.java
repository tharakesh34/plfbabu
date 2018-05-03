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
 * FileName    		:  SeqAccountNumber.java													*                           
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

public class SeqAccountNumber implements Serializable {

    private static final long serialVersionUID = -7643683412676440119L;
    
	private String accountBranch;
	private String accountHeadCode;
	private String accountCcyCode;
	private long accountSeqNo = 0;

	public SeqAccountNumber() {
		super();
	}

	public SeqAccountNumber(String accountBranch, String accountHeadCode, String accountCcyCode) {
		super();
		this.accountBranch = accountBranch;
		this.accountHeadCode = accountHeadCode;
		this.accountCcyCode = accountCcyCode;
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getAccountBranch() {
		return accountBranch;
	}
	public void setAccountBranch(String accountBranch) {
		this.accountBranch = accountBranch;
	}

	public String getAccountHeadCode() {
		return accountHeadCode;
	}
	public void setAccountHeadCode(String accountHeadCode) {
		this.accountHeadCode = accountHeadCode;
	}

	public String getAccountCcyCode() {
		return accountCcyCode;
	}
	public void setAccountCcyCode(String accountCcyCode) {
		this.accountCcyCode = accountCcyCode;
	}

	public long getAccountSeqNo() {
		return accountSeqNo;
	}
	public void setAccountSeqNo(long accountSeqNo) {
		this.accountSeqNo = accountSeqNo;
	}

}
