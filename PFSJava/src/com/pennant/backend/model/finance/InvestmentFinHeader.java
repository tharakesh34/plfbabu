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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>TreasuaryFinance table</b>.<br>
 *
 */
public class InvestmentFinHeader implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String  investmentRef = null;
	private BigDecimal totPrincipalAmt = BigDecimal.ZERO;
	private String finCcy;
	private String lovDescfinCcyName;
	private int lovDescFinFormatter;
	private String profitDaysBasis;
	private String lovDescProfitDaysBasisName;
	private Date startDate;
	private Date maturityDate;
	private BigDecimal principalInvested = BigDecimal.ZERO;
	private BigDecimal principalMaturity = BigDecimal.ZERO;
	private BigDecimal principalDueToInvest = BigDecimal.ZERO;
	private BigDecimal avgPftRate = BigDecimal.ZERO;
	private boolean approvalRequired;
	private boolean totalDealsApproved;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private String lovDescFinCcyName;
	private InvestmentFinHeader befImage;
	private LoginUserDetails userDetails;

	private String recordStatus;
	private String roleCode="";
	private String nextRoleCode= "";
	private String taskId="";
	private String nextTaskId= "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	private FinanceDetail financeDetail = null;
	private List<FinanceDetail> financeDetailsList = new ArrayList<FinanceDetail>();
	
	public boolean isNew() {
		return isNewRecord();
	}

	public InvestmentFinHeader() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("TreasuaryFinance");
	}

	public InvestmentFinHeader(String id) {
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

	public String getLovDescfinCcyName() {
		return this.lovDescfinCcyName;
	}

	public void setLovDescfinCcyName (String lovDescfinCcyName) {
		this.lovDescfinCcyName = lovDescfinCcyName;
	}

	public String getProfitDaysBasis() {
		return profitDaysBasis;
	}
	public void setProfitDaysBasis(String profitDaysBasis) {
		this.profitDaysBasis = profitDaysBasis;
	}

	public String getLovDescProfitDaysBasisName() {
		return this.lovDescProfitDaysBasisName;
	}

	public void setLovDescProfitDaysBasisName (String lovDescProfitDaysBasisName) {
		this.lovDescProfitDaysBasisName = lovDescProfitDaysBasisName;
	}
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date finStartDate) {
		this.startDate = finStartDate;
	}

	public String getLovDescFinCcyName() {
		return lovDescFinCcyName;
	}

	public void setLovDescFinCcyName(String lovDescFinCcyName) {
		this.lovDescFinCcyName = lovDescFinCcyName;
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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public long getLastMntBy() {
		return lastMntBy;
	}

	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public Timestamp getLastMntOn() {
		return lastMntOn;
	}

	public void setLastMntOn(Timestamp lastMntON) {
		this.lastMntOn = lastMntON;
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

	public LoginUserDetails getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoginUserDetails userDetails) {
		this.userDetails = userDetails;
	}

	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getNextRoleCode() {
		return nextRoleCode;
	}

	public void setNextRoleCode(String nextRoleCode) {
		this.nextRoleCode = nextRoleCode;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getNextTaskId() {
		return nextTaskId;
	}

	public void setNextTaskId(String nextTaskId) {
		this.nextTaskId = nextTaskId;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public String getUserAction() {
		return userAction;
	}

	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}

	public boolean isWorkflow() {
		if (this.workflowId==0){
			return false;
		}
		return true;
	}

	public long getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}


	// Overidden Equals method to handle the comparision
	public boolean equals(InvestmentFinHeader treasuaryFinance) {
		return getId() == treasuaryFinance.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof InvestmentFinHeader) {
			InvestmentFinHeader treasuaryFinance = (InvestmentFinHeader) obj;
			return equals(treasuaryFinance);
		}
		return false;
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

	public void setLovDescFinFormatter(int lovDescFinFormatter) {
	    this.lovDescFinFormatter = lovDescFinFormatter;
    }

	public int getLovDescFinFormatter() {
	    return lovDescFinFormatter;
    }

}
