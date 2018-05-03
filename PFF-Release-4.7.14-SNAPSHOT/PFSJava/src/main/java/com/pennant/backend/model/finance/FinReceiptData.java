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
import java.util.HashMap;
import java.util.Map;

public class FinReceiptData {
	
	private String finReference = null;	
	private String buildProcess = "";
	private BigDecimal accruedTillLBD = BigDecimal.ZERO;
	private BigDecimal pendingODC = BigDecimal.ZERO;
	private boolean sufficientRefund = true;
	private BigDecimal maxRefundAmt = BigDecimal.ZERO;
	private BigDecimal actInsRefundAmt = BigDecimal.ZERO;
	private String eventCodeRef = "";
	private String sourceId;
	private BigDecimal totReceiptAmount = BigDecimal.ZERO;

	private RepayMain repayMain = new RepayMain();
	private Map<String, BigDecimal> allocationMap = new HashMap<>();
	private Map<String, String> allocationDescMap = new HashMap<>();
	private FinReceiptHeader receiptHeader;
	private FinanceDetail financeDetail;
	
	public FinReceiptData() {
		
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
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
	
	public BigDecimal getActInsRefundAmt() {
	    return actInsRefundAmt;
    }
	public void setActInsRefundAmt(BigDecimal actInsRefundAmt) {
	    this.actInsRefundAmt = actInsRefundAmt;
    }

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}
	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public FinReceiptHeader getReceiptHeader() {
		return receiptHeader;
	}
	public void setReceiptHeader(FinReceiptHeader receiptHeader) {
		this.receiptHeader = receiptHeader;
	}

	public Map<String, BigDecimal> getAllocationMap() {
		return allocationMap;
	}

	public void setAllocationMap(Map<String, BigDecimal> allocationMap) {
		this.allocationMap = allocationMap;
	}

	public Map<String, String> getAllocationDescMap() {
		return allocationDescMap;
	}

	public void setAllocationDescMap(Map<String, String> allocationDescMap) {
		this.allocationDescMap = allocationDescMap;
	}

	public BigDecimal getTotReceiptAmount() {
		return totReceiptAmount;
	}

	public void setTotReceiptAmount(BigDecimal totReceiptAmount) {
		this.totReceiptAmount = totReceiptAmount;
	}

}
