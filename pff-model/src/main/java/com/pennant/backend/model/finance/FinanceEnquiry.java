/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : FinanceEnquiry.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 16-03-2012 * * Modified Date
 * : 16-03-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 16-03-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.CustomerDetails;

/**
 * Model class for the <b>FinanceMain table</b>.<br>
 * 
 */
public class FinanceEnquiry implements Serializable {
	private static final long serialVersionUID = -7702107666101609103L;

	private long finID;
	private String finReference = null;
	private String finStatus;
	private boolean finIsActive;
	private boolean blacklisted;
	private String finBranch = null;
	private String lovDescFinBranchName;
	private String finType;
	private String lovDescFinTypeName;
	private String lovDescProductCodeName;
	private String finCcy;
	private int finCcyNumber;
	private String scheduleMethod;
	private String profitDaysBasis;
	private Date finStartDate;
	private int numberOfTerms = 0;
	private long custID;
	private String lovDescCustCIF;
	private String lovDescCustShrtName;
	private BigDecimal finAmount = BigDecimal.ZERO;
	private BigDecimal finCurrAssetValue = BigDecimal.ZERO;
	private BigDecimal feeChargeAmt = BigDecimal.ZERO;
	private BigDecimal downPayment = BigDecimal.ZERO;
	private BigDecimal finRepaymentAmount = BigDecimal.ZERO;
	private Date grcPeriodEndDate;
	private Date maturityDate;
	private String closingStatus;
	private String custTypeCtg;
	private BigDecimal nextDueAmount = BigDecimal.ZERO;
	private Date nextDueDate;
	private Long mandateID;
	private Long securityMandateID;
	private String finRepayMethod;
	private Date latestRpyDate;
	private BigDecimal currentBalance;
	private BigDecimal amountOverdue;
	private BigDecimal finAssetValue;
	private int odDays;
	private int curODDays;
	private BigDecimal collateralValue;
	private String collateralType;
	private BigDecimal repayProfitRate;
	private BigDecimal firstRepay = BigDecimal.ZERO;
	private BigDecimal writtenOffAmount = BigDecimal.ZERO;
	private BigDecimal writtenOffPrincipal = BigDecimal.ZERO;
	private BigDecimal settlementAmount = BigDecimal.ZERO;
	private BigDecimal paymentAmount = BigDecimal.ZERO;
	private String repayFrq;
	private String ownership;
	private int NOInst = 0;
	private int NOPaidinst = 0;
	private BigDecimal maxInstAmount = BigDecimal.ZERO;
	private Date finApprovedDate;
	private BigDecimal futureSchedulePrin;
	private BigDecimal instalmentDue;
	private BigDecimal instalmentPaid;
	private BigDecimal bounceDue;
	private BigDecimal bouncePaid;
	private BigDecimal latePaymentPenaltyDue;
	private BigDecimal latePaymentPenaltyPaid;
	private BigDecimal totalPriSchd;
	private BigDecimal totalPriPaid;
	private BigDecimal totalPftSchd;
	private BigDecimal totalPftPaid;
	private BigDecimal excessAmount;
	private BigDecimal excessAmtPaid;
	private List<FinODDetails> finOdDetails;
	private List<CollateralSetup> collateralSetupDetails;
	private List<ChequeDetail> chequeDetail;
	private List<CustomerDetails> finGuarenters;
	private List<CustomerDetails> finCoApplicants;
	private BigDecimal svAmount = BigDecimal.ZERO;
	private boolean allowGrcPeriod;
	private String lovDescFinScheduleOn;
	private int graceTerms;
	private String lovDescFinDivision;
	private int defferments;
	private boolean sanBsdSchdle;
	private long promotionSeqId;
	private BigDecimal cbAmount;
	private BigDecimal totalCpz = BigDecimal.ZERO;
	private boolean finOcrRequired;
	private String loanStsDesc;
	private String recordStatus;
	private long createdBy;
	private Timestamp createdOn;
	private long approvedBy;
	private Timestamp approvedOn;
	// Loan Closed Date
	private Date closedDate;
	private boolean writeoffLoan;
	private String entityCode;
	private BigDecimal odProfit = BigDecimal.ZERO;
	private String customerType;
	private BigDecimal advanceEMI = BigDecimal.ZERO;

	public FinanceEnquiry() {

	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("latestRpyDate");
		excludeFields.add("currentBalance");
		excludeFields.add("amountOverdue");
		excludeFields.add("finAssetValue");
		excludeFields.add("odDays");
		excludeFields.add("curODDays");
		excludeFields.add("collateralValue");
		excludeFields.add("collateralType");
		excludeFields.add("repayProfitRate");
		excludeFields.add("firstRepay");
		excludeFields.add("writtenOffAmount");
		excludeFields.add("writtenOffPrincipal");
		excludeFields.add("settlementAmount");
		excludeFields.add("paymentAmount");
		excludeFields.add("repayFrq");
		excludeFields.add("ownership");
		excludeFields.add("NOInst");
		excludeFields.add("NOPaidinst");
		excludeFields.add("MaxInstAmount");
		excludeFields.add("finApprovedDate");
		excludeFields.add("futureSchedulePrin");
		excludeFields.add("instalmentDue");
		excludeFields.add("instalmentPaid");
		excludeFields.add("bounceDue");
		excludeFields.add("bouncePaid");
		excludeFields.add("latePaymentPenaltyDue");
		excludeFields.add("latePaymentPenaltyPaid");
		excludeFields.add("totalPriSchd");
		excludeFields.add("totalPriPaid");
		excludeFields.add("totalPftSchd");
		excludeFields.add("totalPftPaid");
		excludeFields.add("excessAmount");
		excludeFields.add("excessAmtPaid");
		excludeFields.add("totalCpz");
		excludeFields.add("loanStsDesc");
		excludeFields.add("recordStatus");
		excludeFields.add("entityCode");
		excludeFields.add("odProfit");
		excludeFields.add("customerType");
		return excludeFields;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getFinStatus() {
		return finStatus;
	}

	public void setFinStatus(String finStatus) {
		this.finStatus = finStatus;
	}

	public void setFinIsActive(boolean finIsActive) {
		this.finIsActive = finIsActive;
	}

	public boolean isFinIsActive() {
		return finIsActive;
	}

	public void setBlacklisted(boolean blacklisted) {
		this.blacklisted = blacklisted;
	}

	public boolean isBlacklisted() {
		return blacklisted;
	}

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}

	public String getLovDescFinBranchName() {
		return lovDescFinBranchName;
	}

	public void setLovDescFinBranchName(String lovDescFinBranchName) {
		this.lovDescFinBranchName = lovDescFinBranchName;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getLovDescFinTypeName() {
		return lovDescFinTypeName;
	}

	public void setLovDescFinTypeName(String lovDescFinTypeName) {
		this.lovDescFinTypeName = lovDescFinTypeName;
	}

	public String getLovDescProductCodeName() {
		return lovDescProductCodeName;
	}

	public void setLovDescProductCodeName(String lovDescProductCodeName) {
		this.lovDescProductCodeName = lovDescProductCodeName;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public int getFinCcyNumber() {
		return finCcyNumber;
	}

	public void setFinCcyNumber(int finCcyNumber) {
		this.finCcyNumber = finCcyNumber;
	}

	public String getScheduleMethod() {
		return scheduleMethod;
	}

	public void setScheduleMethod(String scheduleMethod) {
		this.scheduleMethod = scheduleMethod;
	}

	public String getProfitDaysBasis() {
		return profitDaysBasis;
	}

	public void setProfitDaysBasis(String profitDaysBasis) {
		this.profitDaysBasis = profitDaysBasis;
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

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getLovDescCustCIF() {
		return lovDescCustCIF;
	}

	public void setLovDescCustCIF(String lovDescCustCIF) {
		this.lovDescCustCIF = lovDescCustCIF;
	}

	public String getLovDescCustShrtName() {
		return lovDescCustShrtName;
	}

	public void setLovDescCustShrtName(String lovDescCustShrtName) {
		this.lovDescCustShrtName = lovDescCustShrtName;
	}

	public BigDecimal getFinAmount() {
		return finAmount;
	}

	public void setFinAmount(BigDecimal finAmount) {
		this.finAmount = finAmount;
	}

	public BigDecimal getFeeChargeAmt() {
		return feeChargeAmt;
	}

	public void setFeeChargeAmt(BigDecimal feeChargeAmt) {
		this.feeChargeAmt = feeChargeAmt;
	}

	public BigDecimal getFinRepaymentAmount() {
		return finRepaymentAmount;
	}

	public void setFinRepaymentAmount(BigDecimal finRepaymentAmount) {
		this.finRepaymentAmount = finRepaymentAmount;
	}

	public Date getGrcPeriodEndDate() {
		return grcPeriodEndDate;
	}

	public void setGrcPeriodEndDate(Date grcPeriodEndDate) {
		this.grcPeriodEndDate = grcPeriodEndDate;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public void setClosingStatus(String closingStatus) {
		this.closingStatus = closingStatus;
	}

	public String getClosingStatus() {
		return closingStatus;
	}

	public String getCustTypeCtg() {
		return custTypeCtg;
	}

	public void setCustTypeCtg(String custTypeCtg) {
		this.custTypeCtg = custTypeCtg;
	}

	public BigDecimal getDownPayment() {
		return downPayment;
	}

	public void setDownPayment(BigDecimal downPayment) {
		this.downPayment = downPayment;
	}

	public BigDecimal getNextDueAmount() {
		return nextDueAmount;
	}

	public void setNextDueAmount(BigDecimal nextDueAmount) {
		this.nextDueAmount = nextDueAmount;
	}

	public Date getNextDueDate() {
		return nextDueDate;
	}

	public void setNextDueDate(Date nextDueDate) {
		this.nextDueDate = nextDueDate;
	}

	public Long getMandateID() {
		return mandateID;
	}

	public void setMandateID(Long mandateID) {
		this.mandateID = mandateID;
	}

	public String getFinRepayMethod() {
		return finRepayMethod;
	}

	public void setFinRepayMethod(String finRepayMethod) {
		this.finRepayMethod = finRepayMethod;
	}

	public Date getLatestRpyDate() {
		return latestRpyDate;
	}

	public void setLatestRpyDate(Date latestRpyDate) {
		this.latestRpyDate = latestRpyDate;
	}

	public BigDecimal getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(BigDecimal currentBalance) {
		this.currentBalance = currentBalance;
	}

	public BigDecimal getAmountOverdue() {
		return amountOverdue;
	}

	public void setAmountOverdue(BigDecimal amountOverdue) {
		this.amountOverdue = amountOverdue;
	}

	public BigDecimal getFinAssetValue() {
		return finAssetValue;
	}

	public void setFinAssetValue(BigDecimal finAssetValue) {
		this.finAssetValue = finAssetValue;
	}

	public int getOdDays() {
		return odDays;
	}

	public void setOdDays(int odDays) {
		this.odDays = odDays;
	}

	public int getCurODDays() {
		return curODDays;
	}

	public void setCurODDays(int curODDays) {
		this.curODDays = curODDays;
	}

	public BigDecimal getCollateralValue() {
		return collateralValue;
	}

	public void setCollateralValue(BigDecimal collateralValue) {
		this.collateralValue = collateralValue;
	}

	public String getCollateralType() {
		return collateralType;
	}

	public void setCollateralType(String collateralType) {
		this.collateralType = collateralType;
	}

	public BigDecimal getRepayProfitRate() {
		return repayProfitRate;
	}

	public void setRepayProfitRate(BigDecimal repayProfitRate) {
		this.repayProfitRate = repayProfitRate;
	}

	public BigDecimal getFirstRepay() {
		return firstRepay;
	}

	public void setFirstRepay(BigDecimal firstRepay) {
		this.firstRepay = firstRepay;
	}

	public BigDecimal getWrittenOffAmount() {
		return writtenOffAmount;
	}

	public void setWrittenOffAmount(BigDecimal writtenOffAmount) {
		this.writtenOffAmount = writtenOffAmount;
	}

	public BigDecimal getWrittenOffPrincipal() {
		return writtenOffPrincipal;
	}

	public void setWrittenOffPrincipal(BigDecimal writtenOffPrincipal) {
		this.writtenOffPrincipal = writtenOffPrincipal;
	}

	public BigDecimal getSettlementAmount() {
		return settlementAmount;
	}

	public void setSettlementAmount(BigDecimal settlementAmount) {
		this.settlementAmount = settlementAmount;
	}

	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public String getRepayFrq() {
		return repayFrq;
	}

	public void setRepayFrq(String repayFrq) {
		this.repayFrq = repayFrq;
	}

	public String getOwnership() {
		return ownership;
	}

	public void setOwnership(String ownership) {
		this.ownership = ownership;
	}

	public BigDecimal getFinCurrAssetValue() {
		return finCurrAssetValue;
	}

	public void setFinCurrAssetValue(BigDecimal finCurrAssetValue) {
		this.finCurrAssetValue = finCurrAssetValue;
	}

	public int getNOInst() {
		return NOInst;
	}

	public void setNOInst(int nOInst) {
		NOInst = nOInst;
	}

	public BigDecimal getMaxInstAmount() {
		return maxInstAmount;
	}

	public void setMaxInstAmount(BigDecimal maxInstAmount) {
		this.maxInstAmount = maxInstAmount;
	}

	public Date getFinApprovedDate() {
		return finApprovedDate;
	}

	public void setFinApprovedDate(Date finApprovedDate) {
		this.finApprovedDate = finApprovedDate;
	}

	public BigDecimal getFutureSchedulePrin() {
		return futureSchedulePrin;
	}

	public void setFutureSchedulePrin(BigDecimal futureSchedulePrin) {
		this.futureSchedulePrin = futureSchedulePrin;
	}

	public BigDecimal getInstalmentDue() {
		return instalmentDue;
	}

	public void setInstalmentDue(BigDecimal instalmentDue) {
		this.instalmentDue = instalmentDue;
	}

	public BigDecimal getInstalmentPaid() {
		return instalmentPaid;
	}

	public void setInstalmentPaid(BigDecimal instalmentPaid) {
		this.instalmentPaid = instalmentPaid;
	}

	public BigDecimal getBounceDue() {
		return bounceDue;
	}

	public void setBounceDue(BigDecimal bounceDue) {
		this.bounceDue = bounceDue;
	}

	public BigDecimal getBouncePaid() {
		return bouncePaid;
	}

	public void setBouncePaid(BigDecimal bouncePaid) {
		this.bouncePaid = bouncePaid;
	}

	public BigDecimal getLatePaymentPenaltyDue() {
		return latePaymentPenaltyDue;
	}

	public void setLatePaymentPenaltyDue(BigDecimal latePaymentPenaltyDue) {
		this.latePaymentPenaltyDue = latePaymentPenaltyDue;
	}

	public BigDecimal getLatePaymentPenaltyPaid() {
		return latePaymentPenaltyPaid;
	}

	public void setLatePaymentPenaltyPaid(BigDecimal latePaymentPenaltyPaid) {
		this.latePaymentPenaltyPaid = latePaymentPenaltyPaid;
	}

	public BigDecimal getTotalPriSchd() {
		return totalPriSchd;
	}

	public void setTotalPriSchd(BigDecimal totalPriSchd) {
		this.totalPriSchd = totalPriSchd;
	}

	public BigDecimal getTotalPriPaid() {
		return totalPriPaid;
	}

	public void setTotalPriPaid(BigDecimal totalPriPaid) {
		this.totalPriPaid = totalPriPaid;
	}

	public BigDecimal getTotalPftSchd() {
		return totalPftSchd;
	}

	public void setTotalPftSchd(BigDecimal totalPftSchd) {
		this.totalPftSchd = totalPftSchd;
	}

	public BigDecimal getTotalPftPaid() {
		return totalPftPaid;
	}

	public void setTotalPftPaid(BigDecimal totalPftPaid) {
		this.totalPftPaid = totalPftPaid;
	}

	public BigDecimal getExcessAmount() {
		return excessAmount;
	}

	public void setExcessAmount(BigDecimal excessAmount) {
		this.excessAmount = excessAmount;
	}

	public BigDecimal getExcessAmtPaid() {
		return excessAmtPaid;
	}

	public void setExcessAmtPaid(BigDecimal excessAmtPaid) {
		this.excessAmtPaid = excessAmtPaid;
	}

	public List<FinODDetails> getFinOdDetails() {
		return finOdDetails;
	}

	public void setFinOdDetails(List<FinODDetails> finOdDetails) {
		this.finOdDetails = finOdDetails;
	}

	public List<CollateralSetup> getCollateralSetupDetails() {
		return collateralSetupDetails;
	}

	public void setCollateralSetupDetails(List<CollateralSetup> collateralSetupDetails) {
		this.collateralSetupDetails = collateralSetupDetails;
	}

	public List<ChequeDetail> getChequeDetail() {
		return chequeDetail;
	}

	public void setChequeDetail(List<ChequeDetail> chequeDetail) {
		this.chequeDetail = chequeDetail;
	}

	public List<CustomerDetails> getFinGuarenters() {
		return finGuarenters;
	}

	public void setFinGuarenters(List<CustomerDetails> finGuarenters) {
		this.finGuarenters = finGuarenters;
	}

	public BigDecimal getSvAmount() {
		return svAmount;
	}

	public void setSvAmount(BigDecimal svAmount) {
		this.svAmount = svAmount;
	}

	public boolean getAllowGrcPeriod() {
		return allowGrcPeriod;
	}

	public void setAllowGrcPeriod(boolean allowGrcPeriod) {
		this.allowGrcPeriod = allowGrcPeriod;
	}

	public String getLovDescFinScheduleOn() {
		return lovDescFinScheduleOn;
	}

	public void setLovDescFinScheduleOn(String lovDescFinScheduleOn) {
		this.lovDescFinScheduleOn = lovDescFinScheduleOn;
	}

	public int getGraceTerms() {
		return graceTerms;
	}

	public void setGraceTerms(int graceTerms) {
		this.graceTerms = graceTerms;
	}

	public String getLovDescFinDivision() {
		return lovDescFinDivision;
	}

	public void setLovDescFinDivision(String lovDescFinDivision) {
		this.lovDescFinDivision = lovDescFinDivision;
	}

	public int getDefferments() {
		return defferments;
	}

	public void setDefferments(int defferments) {
		this.defferments = defferments;
	}

	public boolean isSanBsdSchdle() {
		return sanBsdSchdle;
	}

	public void setSanBsdSchdle(boolean sanBsdSchdle) {
		this.sanBsdSchdle = sanBsdSchdle;
	}

	public long getPromotionSeqId() {
		return promotionSeqId;
	}

	public void setPromotionSeqId(long promotionSeqId) {
		this.promotionSeqId = promotionSeqId;
	}

	public BigDecimal getCbAmount() {
		return cbAmount;
	}

	public void setCbAmount(BigDecimal cbAmount) {
		this.cbAmount = cbAmount;
	}

	public BigDecimal getTotalCpz() {
		return totalCpz;
	}

	public void setTotalCpz(BigDecimal totalCpz) {
		this.totalCpz = totalCpz;
	}

	public boolean isFinOcrRequired() {
		return finOcrRequired;
	}

	public void setFinOcrRequired(boolean finOcrRequired) {
		this.finOcrRequired = finOcrRequired;
	}

	public String getLoanStsDesc() {
		return loanStsDesc;
	}

	public void setLoanStsDesc(String loanStsDesc) {
		this.loanStsDesc = loanStsDesc;
	}

	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public long getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(long approvedBy) {
		this.approvedBy = approvedBy;
	}

	public Timestamp getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Timestamp approvedOn) {
		this.approvedOn = approvedOn;
	}

	public Date getClosedDate() {
		return closedDate;
	}

	public void setClosedDate(Date closedDate) {
		this.closedDate = closedDate;
	}

	public boolean isWriteoffLoan() {
		return writeoffLoan;
	}

	public void setWriteoffLoan(boolean writeoffLoan) {
		this.writeoffLoan = writeoffLoan;
	}

	public List<CustomerDetails> getFinCoApplicants() {
		return finCoApplicants;
	}

	public void setFinCoApplicants(List<CustomerDetails> finCoApplicants) {
		this.finCoApplicants = finCoApplicants;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public BigDecimal getOdProfit() {
		return odProfit;
	}

	public void setOdProfit(BigDecimal odProfit) {
		this.odProfit = odProfit;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public Long getSecurityMandateID() {
		return securityMandateID;
	}

	public void setSecurityMandateID(Long securityMandateID) {
		this.securityMandateID = securityMandateID;
	}

	public BigDecimal getAdvanceEMI() {
		return advanceEMI;
	}

	public void setAdvanceEMI(BigDecimal advanceEMI) {
		this.advanceEMI = advanceEMI;
	}

	public int getNOPaidinst() {
		return NOPaidinst;
	}

	public void setNOPaidinst(int nOPaidinst) {
		this.NOPaidinst = nOPaidinst;
	}
}
