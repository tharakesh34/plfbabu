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
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;

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
	private FinReceiptHeader receiptHeader;
	private FinanceDetail financeDetail;
	private RepledgeDetail repledgeDetail;
	private Promotion promotion;
	private boolean cashierTransaction = false;
	private FinanceProfitDetail orgFinPftDtls;
	private BigDecimal actualReceiptAmount = BigDecimal.ZERO;
	private boolean isForeClosure = false;
	private BigDecimal remBal = BigDecimal.ZERO;
	private BigDecimal partialPaidAmount = BigDecimal.ZERO;
	private BigDecimal totalDueAmount = BigDecimal.ZERO;

	private BigDecimal totalPastDues = BigDecimal.ZERO;
	private BigDecimal excessAvailable = BigDecimal.ZERO;

	private LoggedInUser userDetails;

	private boolean isPresentment = false;

	private BigDecimal inPresPri = BigDecimal.ZERO;
	private BigDecimal inPresPft = BigDecimal.ZERO;
	private BigDecimal inPresTds = BigDecimal.ZERO;
	private BigDecimal inPresNpft = BigDecimal.ZERO;

	private BigDecimal paidNow = BigDecimal.ZERO;

	private List<ReceiptAllocationDetail> allocList = new ArrayList<>();

	private List<ManualAdvise> manAdvList = new ArrayList<>();

	private List<FinReceiptHeader> inProcRchList = null;
	private List<ReceiptAllocationDetail> inProcRadList = null;
	private List<FinFeeDetail> finFeeDetails = new ArrayList<>();

	private Date valueDate;

	private boolean isDueAdjusted = true;
	private boolean isFCDueChanged = false;
	private boolean isEnquiry = false;
	private List<XcessPayables> excessPayables = new ArrayList<>();

	private List<ErrorDetail> errorDetails = new ArrayList<>(1);
	private boolean isCalReq = true;
	private boolean isEventFeePercent = false;
	private BigDecimal tdPriBal = BigDecimal.ZERO;

	public BigDecimal getTdPriBal() {
		return tdPriBal;
	}

	public void setTdPriBal(BigDecimal tdPriBal) {
		this.tdPriBal = tdPriBal;
	}

	private BigDecimal curEventFeePercent = BigDecimal.ZERO;
	private BigDecimal newEventFeePercent = BigDecimal.ZERO;

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

	public BigDecimal getTotReceiptAmount() {
		return totReceiptAmount;
	}

	public void setTotReceiptAmount(BigDecimal totReceiptAmount) {
		this.totReceiptAmount = totReceiptAmount;
	}

	public RepledgeDetail getRepledgeDetail() {
		return repledgeDetail;
	}

	public void setRepledgeDetail(RepledgeDetail repledgeDetail) {
		this.repledgeDetail = repledgeDetail;
	}

	public Promotion getPromotion() {
		return promotion;
	}

	public void setPromotion(Promotion promotion) {
		this.promotion = promotion;
	}

	public boolean isCashierTransaction() {
		return cashierTransaction;
	}

	public void setCashierTransaction(boolean cashierTransaction) {
		this.cashierTransaction = cashierTransaction;
	}

	public BigDecimal getTotalDueAmount() {
		return totalDueAmount;
	}

	public void setTotalDueAmount(BigDecimal totalDueAmount) {
		this.totalDueAmount = totalDueAmount;
	}

	public BigDecimal getRemBal() {
		return remBal;
	}

	public void setRemBal(BigDecimal remBal) {
		this.remBal = remBal;
	}

	public BigDecimal getTotalPastDues() {
		return totalPastDues;
	}

	public void setTotalPastDues(BigDecimal totalPastDues) {
		this.totalPastDues = totalPastDues;
	}

	public BigDecimal getPartialPaidAmount() {
		return partialPaidAmount;
	}

	public void setPartialPaidAmount(BigDecimal partialPaidAmount) {
		this.partialPaidAmount = partialPaidAmount;
	}

	public FinanceProfitDetail getOrgFinPftDtls() {
		return orgFinPftDtls;
	}

	public void setOrgFinPftDtls(FinanceProfitDetail orgFinPftDtls) {
		this.orgFinPftDtls = orgFinPftDtls;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public boolean isPresentment() {
		return isPresentment;
	}

	public void setPresentment(boolean isPresentment) {
		this.isPresentment = isPresentment;
	}

	public BigDecimal getInPresPri() {
		return inPresPri;
	}

	public void setInPresPri(BigDecimal inPresPri) {
		this.inPresPri = inPresPri;
	}

	public BigDecimal getInPresPft() {
		return inPresPft;
	}

	public void setInPresPft(BigDecimal inPresPft) {
		this.inPresPft = inPresPft;
	}

	public BigDecimal getInPresTds() {
		return inPresTds;
	}

	public void setInPresTds(BigDecimal inPresTds) {
		this.inPresTds = inPresTds;
	}

	public BigDecimal getInPresNpft() {
		return inPresNpft;
	}

	public void setInPresNpft(BigDecimal inPresNpft) {
		this.inPresNpft = inPresNpft;
	}

	public List<ReceiptAllocationDetail> getAllocList() {
		return allocList;
	}

	public void setAllocList(List<ReceiptAllocationDetail> allocList) {
		this.allocList = allocList;
	}

	public List<FinReceiptHeader> getInProcRchList() {
		return inProcRchList;
	}

	public void setInProcRchList(List<FinReceiptHeader> inProcRchList) {
		this.inProcRchList = inProcRchList;
	}

	public List<ReceiptAllocationDetail> getInProcRadList() {
		return inProcRadList;
	}

	public void setInProcRadList(List<ReceiptAllocationDetail> inProcRadList) {
		this.inProcRadList = inProcRadList;
	}

	public BigDecimal getPaidNow() {
		return paidNow;
	}

	public void setPaidNow(BigDecimal paidNow) {
		this.paidNow = paidNow;
	}

	public BigDecimal getExcessAvailable() {
		return excessAvailable;
	}

	public void setExcessAvailable(BigDecimal excessAvailable) {
		this.excessAvailable = excessAvailable;
	}

	public BigDecimal getActualReceiptAmount() {
		return actualReceiptAmount;
	}

	public void setActualReceiptAmount(BigDecimal actualReceiptAmount) {
		this.actualReceiptAmount = actualReceiptAmount;
	}

	public boolean isForeClosure() {
		return isForeClosure;
	}

	public void setForeClosure(boolean isForeClosure) {
		this.isForeClosure = isForeClosure;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public boolean isDueAdjusted() {
		return isDueAdjusted;
	}

	public void setDueAdjusted(boolean isDueAdjusted) {
		this.isDueAdjusted = isDueAdjusted;
	}

	public List<ManualAdvise> getManAdvList() {
		return manAdvList;
	}

	public void setManAdvList(List<ManualAdvise> manAdvList) {
		this.manAdvList = manAdvList;
	}

	public List<ErrorDetail> getErrorDetails() {
		return errorDetails;
	}

	public void setErrorDetails(List<ErrorDetail> errorDetails) {
		this.errorDetails = errorDetails;
	}

	public boolean isFCDueChanged() {
		return isFCDueChanged;
	}

	public void setFCDueChanged(boolean isFCDueChanged) {
		this.isFCDueChanged = isFCDueChanged;
	}

	public boolean isEnquiry() {
		return isEnquiry;
	}

	public void setEnquiry(boolean isEnquiry) {
		this.isEnquiry = isEnquiry;
	}

	public boolean isCalReq() {
		return isCalReq;
	}

	public void setCalReq(boolean isCalReq) {
		this.isCalReq = isCalReq;
	}

	public List<XcessPayables> getExcessPayables() {
		return excessPayables;
	}

	public void setExcessPayables(List<XcessPayables> excessPayables) {
		this.excessPayables = excessPayables;
	}

	public boolean isEventFeePercent() {
		return isEventFeePercent;
	}

	public void setEventFeePercent(boolean isEventFeePercent) {
		this.isEventFeePercent = isEventFeePercent;
	}

	public BigDecimal getCurEventFeePercent() {
		return curEventFeePercent;
	}

	public void setCurEventFeePercent(BigDecimal curEventFeePercent) {
		this.curEventFeePercent = curEventFeePercent;
	}

	public BigDecimal getNewEventFeePercent() {
		return newEventFeePercent;
	}

	public void setNewEventFeePercent(BigDecimal newEventFeePercent) {
		this.newEventFeePercent = newEventFeePercent;
	}

	public List<FinFeeDetail> getFinFeeDetails() {
		return finFeeDetails;
	}

	public void setFinFeeDetails(List<FinFeeDetail> finFeeDetails) {
		this.finFeeDetails = finFeeDetails;
	}

}
