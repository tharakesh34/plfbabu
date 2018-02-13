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
 * FileName    		:  TreasuaryFinance.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-11-2013    														*
 *                                                                  						*
 * Modified Date    :  04-11-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-11-2013       Pennant	                 0.1                                            * 
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>TreasuaryFinance table</b>.<br>
 *
 */
public class InvestmentFinHeader extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;
	private String  investmentRef = null;
	private BigDecimal totPrincipalAmt = BigDecimal.ZERO;
	private String finCcy;
	private String profitDaysBasis;
	private Date startDate;
	private Date maturityDate;
	private BigDecimal principalInvested = BigDecimal.ZERO;
	private BigDecimal principalMaturity = BigDecimal.ZERO;
	private BigDecimal principalDueToInvest = BigDecimal.ZERO;
	private BigDecimal avgPftRate = BigDecimal.ZERO;
	private boolean approvalRequired;
	private boolean totalDealsApproved;
	private boolean newRecord=false;
	private String lovValue;
	private InvestmentFinHeader befImage;
	private LoggedInUser userDetails;

	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	private FinanceDetail financeDetail = null;
	private List<FinanceDetail> financeDetailsList = new ArrayList<FinanceDetail>();
	
	public boolean isNew() {
		return isNewRecord();
	}

	public InvestmentFinHeader() {
		super();
	}

	public InvestmentFinHeader(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();

		excludeFields.add("financeDetail");
		excludeFields.add("financeDetailsList");

		return excludeFields;
	}

	//Getter and Setter methods

	public String getId() {
		return investmentRef;
	}

	public void setId (String id) {
		this.investmentRef = id;
	}

	public String getInvestmentRef() {
		return investmentRef;
	}

	public void setInvestmentRef(String investmentRef) {
		this.investmentRef = investmentRef;
	}

	public BigDecimal getTotPrincipalAmt() {
		return totPrincipalAmt;
	}

	public void setTotPrincipalAmt(BigDecimal totPrincipalAmt) {
		this.totPrincipalAmt = totPrincipalAmt;
	}

	public String getFinCcy() {
		return finCcy;
	}
	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public String getProfitDaysBasis() {
		return profitDaysBasis;
	}
	public void setProfitDaysBasis(String profitDaysBasis) {
		this.profitDaysBasis = profitDaysBasis;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date finStartDate) {
		this.startDate = finStartDate;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}
	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public BigDecimal getPrincipalInvested() {
		return principalInvested;
	}

	public void setPrincipalInvested(BigDecimal principalInvested) {
		this.principalInvested = principalInvested;
	}

	public BigDecimal getPrincipalMaturity() {
		return principalMaturity;
	}

	public void setPrincipalMaturity(BigDecimal principalMaturity) {
		this.principalMaturity = principalMaturity;
	}

	public BigDecimal getPrincipalDueToInvest() {
		return principalDueToInvest;
	}

	public void setPrincipalDueToInvest(BigDecimal principalDueToInvest) {
		this.principalDueToInvest = principalDueToInvest;
	}

	public BigDecimal getAvgPftRate() {
		return avgPftRate;
	}
	public void setAvgPftRate(BigDecimal avgPftRate) {
		this.avgPftRate = avgPftRate;
	}

	public boolean isApprovalRequired() {
		return approvalRequired;
	}
	public void setApprovalRequired(boolean approvalRequired) {
		this.approvalRequired = approvalRequired;
	}

	public void setTotalDealsApproved(boolean totalDealsApproved) {
	    this.totalDealsApproved = totalDealsApproved;
    }

	public boolean isTotalDealsApproved() {
	    return totalDealsApproved;
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

	public InvestmentFinHeader getBefImage(){
		return this.befImage;
	}

	public void setBefImage(InvestmentFinHeader beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public List<FinanceDetail> getFinanceDetailsList() {
    	return financeDetailsList;
    }

	public void setFinanceDetailsList(List<FinanceDetail> financeDetailsList) {
    	this.financeDetailsList = financeDetailsList;
    }

	public FinanceDetail getFinanceDetail() {
    	return financeDetail;
    }

	public void setFinanceDetail(FinanceDetail financeDetail) {
    	this.financeDetail = financeDetail;
    }

}
