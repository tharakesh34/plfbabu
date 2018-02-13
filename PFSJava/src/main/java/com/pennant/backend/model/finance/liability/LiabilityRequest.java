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
 * FileName    		:  LiabilityRequest.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-12-2015    														*
 *                                                                  						*
 * Modified Date    :  31-12-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-12-2015       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.finance.liability;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>LiabilityRequest table</b>.<br>
 *
 */
public class LiabilityRequest extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private String finReference;
	private long initiatedBy;
	private String finEvent;
	private String insPaidStatus;
	private BigDecimal insClaimAmount;
	private String insClaimReason;

	private boolean newRecord=false;
	private String lovValue;
	private LiabilityRequest befImage;
	private LoggedInUser userDetails;

	//External Fields
	private FinanceDetail financeDetail;
	private String finType;
	private String finBranch;
	private Date finStartDate;
	private int numberOfTerms;
	private Date maturityDate;
	private String finCcy;
	private String custCIF;
	private BigDecimal 	finAmount = BigDecimal.ZERO;

	public boolean isNew() {
		return isNewRecord();
	}

	public LiabilityRequest() {
		super();
		setWorkflowId(WorkFlowUtil.getWorkFlowID("LiabilityRequest"));
	}

	public LiabilityRequest(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
		excludeFields.add("finType");
		excludeFields.add("finBranch");
		excludeFields.add("finStartDate");
		excludeFields.add("numberOfTerms");
		excludeFields.add("maturityDate");
		excludeFields.add("finCcy");
		excludeFields.add("custCIF");
		excludeFields.add("finAmount");
		excludeFields.add("financeDetail");

		return excludeFields;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//


	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public String getId() {
		return finReference;
	}

	public void setId (String id) {
		this.finReference = id;
	}

	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public long getInitiatedBy() {
		return initiatedBy;
	}
	public void setInitiatedBy(long initiatedBy) {
		this.initiatedBy = initiatedBy;
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

	public LiabilityRequest getBefImage(){
		return this.befImage;
	}

	public void setBefImage(LiabilityRequest beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getFinEvent() {
		return finEvent;
	}

	public void setFinEvent(String finEvent) {
		this.finEvent = finEvent;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public BigDecimal getFinAmount() {
		return finAmount;
	}

	public void setFinAmount(BigDecimal finAmount) {
		this.finAmount = finAmount;
	}

	public Date getFinStartDate() {
		return finStartDate;
	}

	public void setFinStartDate(Date finStartDate) {
		this.finStartDate = finStartDate;
	}

	public int getNumberOfTerms() {
		return numberOfTerms;
	}

	public void setNumberOfTerms(int numberOfTerms) {
		this.numberOfTerms = numberOfTerms;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public String getInsPaidStatus() {
		return insPaidStatus;
	}

	public void setInsPaidStatus(String insPaidStatus) {
		this.insPaidStatus = insPaidStatus;
	}

	public BigDecimal getInsClaimAmount() {
		return insClaimAmount;
	}

	public void setInsClaimAmount(BigDecimal insClaimAmount) {
		this.insClaimAmount = insClaimAmount;
	}

	public String getInsClaimReason() {
		return insClaimReason;
	}

	public void setInsClaimReason(String insClaimReason) {
		this.insClaimReason = insClaimReason;
	}
}
