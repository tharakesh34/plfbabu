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
 * FileName    		:  FinScheduleData.java                                                 * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-03-2011    														*
 *                                                                  						*
 * Modified Date    :  22-03-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-03-2011       Pennant	                 0.1                                            * 
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
import java.util.HashMap;
import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.Rule;

public class RepayData {
	
	private String finReference = null;	
	private String buildProcess = "";
	private BigDecimal accruedTillLBD = BigDecimal.ZERO;
	private BigDecimal pendingODC = BigDecimal.ZERO;
	private boolean sufficientRefund = true;
	private BigDecimal maxRefundAmt = BigDecimal.ZERO;
	private BigDecimal actInsRefundAmt = BigDecimal.ZERO;
	private String eventCodeRef = "";

	private RepayMain repayMain = new RepayMain();
	
	private FinRepayHeader finRepayHeader = new FinRepayHeader();
	private List<RepayScheduleDetail> repayScheduleDetails = new ArrayList<RepayScheduleDetail>();
	private List<DocumentDetails> documentDetailList = new ArrayList<DocumentDetails>();
	
	//Finance Details
	private FinanceMain financeMain;
	private FinanceType financeType;
	private List<FinanceScheduleDetail> scheduleDetails = new ArrayList<FinanceScheduleDetail>();
	private List<RepayInstruction> repayInstructions =  new ArrayList<RepayInstruction>();
	private List<FeeRule> feeRuleList =  new ArrayList<FeeRule>();
	private List<Rule> feeCharges = new ArrayList<Rule>();	
	private List<FinanceReferenceDetail> aggrementList = null;
	
	private HashMap<String, List<AuditDetail>> auditDetailMap = null;
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}
	
	public String getBuildProcess() {
		return buildProcess;
	}
	public void setBuildProcess(String buildProcess) {
		this.buildProcess = buildProcess;
	}
	
	public RepayMain getRepayMain() {
		return repayMain;
	}
	public void setRepayMain(RepayMain repayMain) {
		this.repayMain = repayMain;
	}
	
	public List<RepayScheduleDetail> getRepayScheduleDetails() {
		return repayScheduleDetails;
	}
	public void setRepayScheduleDetails(
			List<RepayScheduleDetail> repayScheduleDetails) {
		this.repayScheduleDetails = repayScheduleDetails;
	}
	
	public BigDecimal getAccruedTillLBD() {
		return accruedTillLBD;
	}
	public void setAccruedTillLBD(BigDecimal accruedTillLBD) {
		this.accruedTillLBD = accruedTillLBD;
	}
	
	public BigDecimal getPendingODC() {
		return pendingODC;
	}
	public void setPendingODC(BigDecimal pendingODC) {
		this.pendingODC = pendingODC;
	}

	//Finance Details
	public FinanceMain getFinanceMain() {
    	return financeMain;
    }
	public void setFinanceMain(FinanceMain financeMain) {
    	this.financeMain = financeMain;
    }
	
	public List<FinanceScheduleDetail> getScheduleDetails() {
    	return scheduleDetails;
    }
	public void setScheduleDetails(List<FinanceScheduleDetail> scheduleDetails) {
    	this.scheduleDetails = scheduleDetails;
    }
	
	public List<RepayInstruction> getRepayInstructions() {
    	return repayInstructions;
    }
	public void setRepayInstructions(List<RepayInstruction> repayInstructions) {
    	this.repayInstructions = repayInstructions;
    }
	
	public FinRepayHeader getFinRepayHeader() {
	    return finRepayHeader;
    }
	public void setFinRepayHeader(FinRepayHeader finRepayHeader) {
	    this.finRepayHeader = finRepayHeader;
    }
	
	public boolean isSufficientRefund() {
	    return sufficientRefund;
    }
	public void setSufficientRefund(boolean sufficientRefund) {
	    this.sufficientRefund = sufficientRefund;
    }
	
	public BigDecimal getMaxRefundAmt() {
	    return maxRefundAmt;
    }
	public void setMaxRefundAmt(BigDecimal maxRefundAmt) {
	    this.maxRefundAmt = maxRefundAmt;
    }
	
	public String getEventCodeRef() {
	    return eventCodeRef;
    }
	public void setEventCodeRef(String eventCodeRef) {
	    this.eventCodeRef = eventCodeRef;
    }
	
	public List<FeeRule> getFeeRuleList() {
	    return feeRuleList;
    }
	public void setFeeRuleList(List<FeeRule> feeRuleList) {
	    this.feeRuleList = feeRuleList;
    }
	
	public List<Rule> getFeeCharges() {
	    return feeCharges;
    }
	public void setFeeCharges(List<Rule> feeCharges) {
	    this.feeCharges = feeCharges;
    }
	
	public FinanceType getFinanceType() {
	    return financeType;
    }
	public void setFinanceType(FinanceType financeType) {
	    this.financeType = financeType;
    }
	
	public BigDecimal getActInsRefundAmt() {
	    return actInsRefundAmt;
    }
	public void setActInsRefundAmt(BigDecimal actInsRefundAmt) {
	    this.actInsRefundAmt = actInsRefundAmt;
    }
	
	public List<DocumentDetails> getDocumentDetailList() {
	    return documentDetailList;
    }
	public void setDocumentDetailList(List<DocumentDetails> documentDetailList) {
	    this.documentDetailList = documentDetailList;
    }
	
	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
	    return auditDetailMap;
    }
	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
	    this.auditDetailMap = auditDetailMap;
    }
	
	public List<FinanceReferenceDetail> getAggrementList() {
	    return aggrementList;
    }
	public void setAggrementList(List<FinanceReferenceDetail> aggrementList) {
	    this.aggrementList = aggrementList;
    }
	
}
