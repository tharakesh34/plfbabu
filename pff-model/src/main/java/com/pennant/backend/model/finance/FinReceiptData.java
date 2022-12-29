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
 * * FileName : FinScheduleData.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 22-03-2011 * * Modified Date
 * : 22-03-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 22-03-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.extendedfield.ExtendedFieldExtension;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class FinReceiptData implements Serializable {
	private static final long serialVersionUID = -6980706459904425002L;

	private Long finID = null;
	private String finReference = null;
	private String buildProcess = "";
	private BigDecimal accruedTillLBD = BigDecimal.ZERO;
	private BigDecimal pendingODC = BigDecimal.ZERO;
	private boolean sufficientRefund = true;
	private BigDecimal maxRefundAmt = BigDecimal.ZERO;
	private String eventCodeRef = "";
	private String sourceId;
	private BigDecimal totReceiptAmount = BigDecimal.ZERO;
	private BigDecimal paidNow = BigDecimal.ZERO;

	private RepayMain repayMain = new RepayMain();
	private Map<String, BigDecimal> allocationMap = new HashMap<>();
	private Map<String, BigDecimal> waiverMap = new HashMap<>();

	private Map<String, String> allocationDescMap = new HashMap<>();
	private FinReceiptHeader receiptHeader;
	private FinanceDetail financeDetail;
	private RepledgeDetail repledgeDetail;
	private Promotion promotion;
	private boolean cashierTransaction = false;
	private boolean isCalReq = true;
	private BigDecimal excessAvailable = BigDecimal.ZERO;
	private boolean isEnquiry = false;

	private Date valueDate;
	private Date dueDate;

	private boolean isDueAdjusted = true;
	private boolean isFCDueChanged = false;
	private LoggedInUser userDetails;
	private BigDecimal remBal = BigDecimal.ZERO;
	private BigDecimal partialPaidAmount = BigDecimal.ZERO;
	private BigDecimal totalDueAmount = BigDecimal.ZERO;

	private BigDecimal inPresPri = BigDecimal.ZERO;
	private BigDecimal inPresPft = BigDecimal.ZERO;
	private BigDecimal inPresTds = BigDecimal.ZERO;
	private BigDecimal inPresNpft = BigDecimal.ZERO;

	private BigDecimal totalPastDues = BigDecimal.ZERO;
	private boolean isPresentment = false;
	private boolean isRepresentment = false;
	private boolean isForeClosure = false;
	private Date presentmentSchDate;
	private BigDecimal actualReceiptAmount = BigDecimal.ZERO;
	private List<ReceiptAllocationDetail> allocList = new ArrayList<>();
	private FinanceProfitDetail orgFinPftDtls;

	private List<FinFeeDetail> finFeeDetails = new ArrayList<>();
	private List<FinReceiptHeader> inProcRchList = null;
	private List<ReceiptAllocationDetail> inProcRadList = null;
	private List<ErrorDetail> errorDetails = new ArrayList<>(1);
	private List<FinanceRepayments> inProcRepayments = new ArrayList<>(1);

	private boolean isEventFeePercent = false;
	private boolean isEarlySettle = false;
	private boolean isAdjSchedule = false;

	private BigDecimal curEventFeePercent = BigDecimal.ZERO;
	private BigDecimal newEventFeePercent = BigDecimal.ZERO;
	private boolean isInitiation;
	private boolean setPaidValues = true;

	private List<ManualAdvise> manAdvList = new ArrayList<>();
	private List<XcessPayables> excessPayables = new ArrayList<>();
	private BigDecimal tdPriBal = BigDecimal.ZERO;
	private FeeType lppFeeType = null;
	private boolean isForeClosureEnq = false;
	private List<FinanceScheduleDetail> schedules = new ArrayList<>();
	private List<Long> inProcessReceipts = new ArrayList<>();
	private BigDecimal actualOdPaid = BigDecimal.ZERO;
	private int rcdIdx = -1;
	private BigDecimal intTdsUnpaid = BigDecimal.ZERO;
	private List<FinDueData> dueDataList = new ArrayList<>();
	private FinDueData dueData;
	private boolean isClosrMaturedLAN = false;
	private String excessType;
	private BigDecimal calculatedClosureAmt = BigDecimal.ZERO;;

	public FinReceiptData() {
		super();
	}

	public FinReceiptData copyEntity() {
		FinReceiptData entity = new FinReceiptData();
		entity.setFinID(this.finID);
		entity.setFinReference(this.finReference);
		entity.setBuildProcess(this.buildProcess);
		entity.setAccruedTillLBD(this.accruedTillLBD);
		entity.setPendingODC(this.pendingODC);
		entity.setSufficientRefund(this.sufficientRefund);
		entity.setMaxRefundAmt(this.maxRefundAmt);
		entity.setEventCodeRef(this.eventCodeRef);
		entity.setSourceId(this.sourceId);
		entity.setTotReceiptAmount(this.totReceiptAmount);
		entity.setPaidNow(this.paidNow);
		entity.setRepayMain(this.repayMain == null ? null : this.repayMain.copyEntity());
		this.allocationMap.entrySet().stream().forEach(e -> entity.getAllocationMap().put(e.getKey(), e.getValue()));
		this.waiverMap.entrySet().stream().forEach(e -> entity.getWaiverMap().put(e.getKey(), e.getValue()));
		this.allocationDescMap.entrySet().stream()
				.forEach(e -> entity.getAllocationDescMap().put(e.getKey(), e.getValue()));
		entity.setReceiptHeader(this.receiptHeader == null ? null : this.receiptHeader.copyEntity());
		entity.setRepledgeDetail(this.repledgeDetail == null ? null : this.repledgeDetail.copyEntity());
		entity.setPromotion(this.promotion == null ? null : this.promotion.copyEntity());
		entity.setCashierTransaction(this.cashierTransaction);
		entity.setCalReq(this.isCalReq);
		entity.setExcessAvailable(this.excessAvailable);
		entity.setEnquiry(this.isEnquiry);
		entity.setValueDate(this.valueDate);
		entity.setDueAdjusted(this.isDueAdjusted);
		entity.setFCDueChanged(this.isFCDueChanged);
		entity.setUserDetails(this.userDetails);
		entity.setRemBal(this.remBal);
		entity.setPartialPaidAmount(this.partialPaidAmount);
		entity.setTotalDueAmount(this.totalDueAmount);
		entity.setInPresPri(this.inPresPri);
		entity.setInPresPft(this.inPresPft);
		entity.setInPresTds(this.inPresTds);
		entity.setInPresNpft(this.inPresNpft);
		entity.setTotalPastDues(this.totalPastDues);
		entity.setPresentment(this.isPresentment);
		entity.setForeClosure(this.isForeClosure);
		entity.setActualReceiptAmount(this.actualReceiptAmount);
		this.allocList.stream().forEach(e -> entity.getAllocList().add(e == null ? null : e.copyEntity()));
		entity.setOrgFinPftDtls(this.orgFinPftDtls == null ? null : this.orgFinPftDtls.copyEntity());
		this.finFeeDetails.stream().forEach(e -> entity.getFinFeeDetails().add(e == null ? null : e.copyEntity()));
		if (inProcRchList != null) {
			entity.setInProcRchList(new ArrayList<FinReceiptHeader>());
			this.inProcRchList.stream().forEach(e -> entity.getInProcRchList().add(e == null ? null : e.copyEntity()));
		}
		if (inProcRadList != null) {
			entity.setInProcRadList(new ArrayList<ReceiptAllocationDetail>());
			this.inProcRadList.stream().forEach(e -> entity.getInProcRadList().add(e == null ? null : e.copyEntity()));
		}
		this.errorDetails.stream().forEach(e -> entity.getErrorDetails().add(e));
		this.inProcRepayments.stream()
				.forEach(e -> entity.getInProcRepayments().add(e == null ? null : e.copyEntity()));
		entity.setEventFeePercent(this.isEventFeePercent);
		entity.setEarlySettle(this.isEarlySettle);
		entity.setAdjSchedule(this.isAdjSchedule);
		entity.setCurEventFeePercent(this.curEventFeePercent);
		entity.setNewEventFeePercent(this.newEventFeePercent);
		entity.setInitiation(this.isInitiation);
		entity.setSetPaidValues(this.setPaidValues);
		this.manAdvList.stream().forEach(e -> entity.getManAdvList().add(e == null ? null : e.copyEntity()));
		this.excessPayables.stream().forEach(e -> entity.getExcessPayables().add(e == null ? null : e.copyEntity()));
		entity.setTdPriBal(this.tdPriBal);
		entity.setLppFeeType(this.lppFeeType == null ? null : this.lppFeeType.copyEntity());
		entity.setForeClosureEnq(this.isForeClosureEnq);
		this.schedules.forEach(e -> entity.getSchedules().add(e == null ? null : e.copyEntity()));
		entity.getInProcessReceipts().addAll(this.inProcessReceipts);
		entity.setActualOdPaid(this.actualOdPaid);
		entity.setRcdIdx(this.rcdIdx);

		FinanceDetail fd = new FinanceDetail();

		FinScheduleData schD = this.getFinanceDetail().getFinScheduleData();
		fd.setFinScheduleData(schD.copyEntity());

		fd.getFinScheduleData().setFinPftDeatil(schD.getFinPftDeatil().copyEntity());
		fd.getFinScheduleData().setFinanceMain(schD.getFinanceMain().copyEntity());
		this.financeDetail.getFinTypeFeesList().stream()
				.forEach(e -> fd.getFinTypeFeesList().add(e == null ? null : e.copyEntity()));

		ExtendedFieldExtension extendedFieldExtension = this.getFinanceDetail().getExtendedFieldExtension();
		if (extendedFieldExtension != null) {
			fd.setExtendedFieldExtension(extendedFieldExtension.copyEntity());
		}

		entity.setFinanceDetail(fd);
		entity.setIntTdsUnpaid(this.intTdsUnpaid);
		entity.setClosrMaturedLAN(this.isClosrMaturedLAN);
		entity.setDueData(this.dueData);
		entity.setRepresentment(this.isRepresentment);
		entity.setPresentmentSchDate(this.presentmentSchDate);
		entity.setIntTdsUnpaid(this.intTdsUnpaid);
		entity.setDueDataList(this.dueDataList);
		entity.setDueData(this.dueData);
		entity.setClosrMaturedLAN(this.isClosrMaturedLAN);
		entity.setExcessType(this.excessType);
		entity.setCalculatedClosureAmt(this.calculatedClosureAmt);

		return entity;
	}

	public Long getFinID() {
		return finID;
	}

	public void setFinID(Long finID) {
		this.finID = finID;
	}

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

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public BigDecimal getTotReceiptAmount() {
		return totReceiptAmount;
	}

	public void setTotReceiptAmount(BigDecimal totReceiptAmount) {
		this.totReceiptAmount = totReceiptAmount;
	}

	public BigDecimal getPaidNow() {
		return paidNow;
	}

	public void setPaidNow(BigDecimal paidNow) {
		this.paidNow = paidNow;
	}

	public RepayMain getRepayMain() {
		return repayMain;
	}

	public void setRepayMain(RepayMain repayMain) {
		this.repayMain = repayMain;
	}

	public Map<String, BigDecimal> getAllocationMap() {
		return allocationMap;
	}

	public void setAllocationMap(Map<String, BigDecimal> allocationMap) {
		this.allocationMap = allocationMap;
	}

	public Map<String, BigDecimal> getWaiverMap() {
		return waiverMap;
	}

	public void setWaiverMap(Map<String, BigDecimal> waiverMap) {
		this.waiverMap = waiverMap;
	}

	public Map<String, String> getAllocationDescMap() {
		return allocationDescMap;
	}

	public void setAllocationDescMap(Map<String, String> allocationDescMap) {
		this.allocationDescMap = allocationDescMap;
	}

	public FinReceiptHeader getReceiptHeader() {
		return receiptHeader;
	}

	public void setReceiptHeader(FinReceiptHeader receiptHeader) {
		this.receiptHeader = receiptHeader;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
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

	public boolean isCalReq() {
		return isCalReq;
	}

	public void setCalReq(boolean isCalReq) {
		this.isCalReq = isCalReq;
	}

	public BigDecimal getExcessAvailable() {
		return excessAvailable;
	}

	public void setExcessAvailable(BigDecimal excessAvailable) {
		this.excessAvailable = excessAvailable;
	}

	public boolean isEnquiry() {
		return isEnquiry;
	}

	public void setEnquiry(boolean isEnquiry) {
		this.isEnquiry = isEnquiry;
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

	public boolean isFCDueChanged() {
		return isFCDueChanged;
	}

	public void setFCDueChanged(boolean isFCDueChanged) {
		this.isFCDueChanged = isFCDueChanged;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public BigDecimal getRemBal() {
		return remBal;
	}

	public void setRemBal(BigDecimal remBal) {
		this.remBal = remBal;
	}

	public BigDecimal getPartialPaidAmount() {
		return partialPaidAmount;
	}

	public void setPartialPaidAmount(BigDecimal partialPaidAmount) {
		this.partialPaidAmount = partialPaidAmount;
	}

	public BigDecimal getTotalDueAmount() {
		return totalDueAmount;
	}

	public void setTotalDueAmount(BigDecimal totalDueAmount) {
		this.totalDueAmount = totalDueAmount;
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

	public BigDecimal getTotalPastDues() {
		return totalPastDues;
	}

	public void setTotalPastDues(BigDecimal totalPastDues) {
		this.totalPastDues = totalPastDues;
	}

	public boolean isPresentment() {
		return isPresentment;
	}

	public void setPresentment(boolean isPresentment) {
		this.isPresentment = isPresentment;
	}

	public boolean isForeClosure() {
		return isForeClosure;
	}

	public void setForeClosure(boolean isForeClosure) {
		this.isForeClosure = isForeClosure;
	}

	public BigDecimal getActualReceiptAmount() {
		return actualReceiptAmount;
	}

	public void setActualReceiptAmount(BigDecimal actualReceiptAmount) {
		this.actualReceiptAmount = actualReceiptAmount;
	}

	public List<ReceiptAllocationDetail> getAllocList() {
		return allocList;
	}

	public void setAllocList(List<ReceiptAllocationDetail> allocList) {
		this.allocList = allocList;
	}

	public FinanceProfitDetail getOrgFinPftDtls() {
		return orgFinPftDtls;
	}

	public void setOrgFinPftDtls(FinanceProfitDetail orgFinPftDtls) {
		this.orgFinPftDtls = orgFinPftDtls;
	}

	public List<FinFeeDetail> getFinFeeDetails() {
		return finFeeDetails;
	}

	public void setFinFeeDetails(List<FinFeeDetail> finFeeDetails) {
		this.finFeeDetails = finFeeDetails;
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

	public List<ErrorDetail> getErrorDetails() {
		return errorDetails;
	}

	public void setErrorDetails(List<ErrorDetail> errorDetails) {
		this.errorDetails = errorDetails;
	}

	public List<FinanceRepayments> getInProcRepayments() {
		return inProcRepayments;
	}

	public void setInProcRepayments(List<FinanceRepayments> inProcRepayments) {
		this.inProcRepayments = inProcRepayments;
	}

	public boolean isEventFeePercent() {
		return isEventFeePercent;
	}

	public void setEventFeePercent(boolean isEventFeePercent) {
		this.isEventFeePercent = isEventFeePercent;
	}

	public boolean isEarlySettle() {
		return isEarlySettle;
	}

	public void setEarlySettle(boolean isEarlySettle) {
		this.isEarlySettle = isEarlySettle;
	}

	public boolean isAdjSchedule() {
		return isAdjSchedule;
	}

	public void setAdjSchedule(boolean isAdjSchedule) {
		this.isAdjSchedule = isAdjSchedule;
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

	public boolean isInitiation() {
		return isInitiation;
	}

	public void setInitiation(boolean isInitiation) {
		this.isInitiation = isInitiation;
	}

	public boolean isSetPaidValues() {
		return setPaidValues;
	}

	public void setSetPaidValues(boolean setPaidValues) {
		this.setPaidValues = setPaidValues;
	}

	public List<ManualAdvise> getManAdvList() {
		return manAdvList;
	}

	public void setManAdvList(List<ManualAdvise> manAdvList) {
		this.manAdvList = manAdvList;
	}

	public List<XcessPayables> getExcessPayables() {
		return excessPayables;
	}

	public void setExcessPayables(List<XcessPayables> excessPayables) {
		this.excessPayables = excessPayables;
	}

	public BigDecimal getTdPriBal() {
		return tdPriBal;
	}

	public void setTdPriBal(BigDecimal tdPriBal) {
		this.tdPriBal = tdPriBal;
	}

	public FeeType getLppFeeType() {
		return lppFeeType;
	}

	public void setLppFeeType(FeeType lppFeeType) {
		this.lppFeeType = lppFeeType;
	}

	public boolean isForeClosureEnq() {
		return isForeClosureEnq;
	}

	public void setForeClosureEnq(boolean isForeClosureEnq) {
		this.isForeClosureEnq = isForeClosureEnq;
	}

	public List<FinanceScheduleDetail> getSchedules() {
		return schedules;
	}

	public void setSchedules(List<FinanceScheduleDetail> schedules) {
		this.schedules = schedules;
	}

	public int getRcdIdx() {
		return rcdIdx;
	}

	public void setRcdIdx(int rcdIdx) {
		this.rcdIdx = rcdIdx;
	}

	public List<Long> getInProcessReceipts() {
		return inProcessReceipts;
	}

	public void setInProcessReceipts(List<Long> inProcessReceipts) {
		this.inProcessReceipts = inProcessReceipts;
	}

	public BigDecimal getActualOdPaid() {
		return actualOdPaid;
	}

	public void setActualOdPaid(BigDecimal actualOdPaid) {
		this.actualOdPaid = actualOdPaid;
	}

	public Date getPresentmentSchDate() {
		return presentmentSchDate;
	}

	public void setPresentmentSchDate(Date presentmentSchDate) {
		this.presentmentSchDate = presentmentSchDate;
	}

	public boolean isRepresentment() {
		return isRepresentment;
	}

	public void setRepresentment(boolean isRepresentment) {
		this.isRepresentment = isRepresentment;
	}

	public BigDecimal getIntTdsUnpaid() {
		return intTdsUnpaid;
	}

	public void setIntTdsUnpaid(BigDecimal intTdsUnpaid) {
		this.intTdsUnpaid = intTdsUnpaid;
	}

	public List<FinDueData> getDueDataList() {
		return dueDataList;
	}

	public void setDueDataList(List<FinDueData> dueDataList) {
		this.dueDataList = dueDataList;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public FinDueData getDueData() {
		return dueData;
	}

	public void setDueData(FinDueData dueData) {
		this.dueData = dueData;
	}

	public boolean isClosrMaturedLAN() {
		return isClosrMaturedLAN;
	}

	public void setClosrMaturedLAN(boolean isClosrMaturedLAN) {
		this.isClosrMaturedLAN = isClosrMaturedLAN;
	}

	public String getExcessType() {
		return excessType;
	}

	public void setExcessType(String excessType) {
		this.excessType = excessType;
	}

	public BigDecimal getCalculatedClosureAmt() {
		return calculatedClosureAmt;
	}

	public void setCalculatedClosureAmt(BigDecimal calculatedClosureAmt) {
		this.calculatedClosureAmt = calculatedClosureAmt;
	}
}
