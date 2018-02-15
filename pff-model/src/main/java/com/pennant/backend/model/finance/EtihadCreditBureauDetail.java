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
 * FileName    		:  EtihadCreditBureauDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-10-2011    														*
 *                                                                  						*
 * Modified Date    :  13-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-10-2011       Pennant	                 0.1                                            * 
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

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>EtihadCreditBureauDetail table</b>.<br>
 *
 */
public class EtihadCreditBureauDetail extends AbstractWorkflowEntity {
	
	private static final long serialVersionUID = -6234931333270161797L;

	private String finReference;
	private int bureauScore;
	private BigDecimal totOutstandingAmt = BigDecimal.ZERO;
	private BigDecimal totOverdueAmt = BigDecimal.ZERO;
	private int defaultContracts;
	private BigDecimal totMonthlyInst = BigDecimal.ZERO;
	private String worstCurrPayDelay;
	private String worstPayDelay;
	private String worstStatus;
	private Date oldConStartDate;
	private Date newConStartDate;
	private String otherBankFinType;
	private int noOfInquiry;
	private int noOfContractsInst;
	private int noOfContractsNonInst;
	private int noOfContractsCredit;
	
	private boolean newRecord=false;
	private String lovValue;
	private EtihadCreditBureauDetail befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public EtihadCreditBureauDetail() {
		super();
	}

	public EtihadCreditBureauDetail(String id) {
		super();
		this.setId(id);
	}
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		
		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getId() {
		return finReference;
	}
	public void setId (String finReference) {
		this.finReference = finReference;
	}
	
	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public int getBureauScore() {
		return bureauScore;
	}
	public void setBureauScore(int bureauScore) {
		this.bureauScore = bureauScore;
	}

	public BigDecimal getTotOutstandingAmt() {
		return totOutstandingAmt;
	}
	public void setTotOutstandingAmt(BigDecimal totOutstandingAmt) {
		this.totOutstandingAmt = totOutstandingAmt;
	}

	public BigDecimal getTotOverdueAmt() {
		return totOverdueAmt;
	}
	public void setTotOverdueAmt(BigDecimal totOverdueAmt) {
		this.totOverdueAmt = totOverdueAmt;
	}

	public int getDefaultContracts() {
		return defaultContracts;
	}
	public void setDefaultContracts(int defaultContracts) {
		this.defaultContracts = defaultContracts;
	}

	public BigDecimal getTotMonthlyInst() {
		return totMonthlyInst;
	}
	public void setTotMonthlyInst(BigDecimal totMonthlyInst) {
		this.totMonthlyInst = totMonthlyInst;
	}

	public String getWorstCurrPayDelay() {
		return worstCurrPayDelay;
	}
	public void setWorstCurrPayDelay(String worstCurrPayDelay) {
		this.worstCurrPayDelay = worstCurrPayDelay;
	}

	public String getWorstPayDelay() {
		return worstPayDelay;
	}
	public void setWorstPayDelay(String worstPayDelay) {
		this.worstPayDelay = worstPayDelay;
	}

	public String getWorstStatus() {
		return worstStatus;
	}
	public void setWorstStatus(String worstStatus) {
		this.worstStatus = worstStatus;
	}

	public Date getOldConStartDate() {
		return oldConStartDate;
	}
	public void setOldConStartDate(Date oldConStartDate) {
		this.oldConStartDate = oldConStartDate;
	}

	public Date getNewConStartDate() {
		return newConStartDate;
	}
	public void setNewConStartDate(Date newConStartDate) {
		this.newConStartDate = newConStartDate;
	}

	public String getOtherBankFinType() {
		return otherBankFinType;
	}
	public void setOtherBankFinType(String otherBankFinType) {
		this.otherBankFinType = otherBankFinType;
	}

	public int getNoOfInquiry() {
		return noOfInquiry;
	}
	public void setNoOfInquiry(int noOfInquiry) {
		this.noOfInquiry = noOfInquiry;
	}

	public int getNoOfContractsInst() {
		return noOfContractsInst;
	}
	public void setNoOfContractsInst(int noOfContractsInst) {
		this.noOfContractsInst = noOfContractsInst;
	}

	public int getNoOfContractsNonInst() {
		return noOfContractsNonInst;
	}
	public void setNoOfContractsNonInst(int noOfContractsNonInst) {
		this.noOfContractsNonInst = noOfContractsNonInst;
	}

	public int getNoOfContractsCredit() {
		return noOfContractsCredit;
	}
	public void setNoOfContractsCredit(int noOfContractsCredit) {
		this.noOfContractsCredit = noOfContractsCredit;
	}

	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	
	public String getLovValue() {
		return lovValue;
	}
	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public EtihadCreditBureauDetail getBefImage(){
		return this.befImage;
	}
	public void setBefImage(EtihadCreditBureauDetail beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
