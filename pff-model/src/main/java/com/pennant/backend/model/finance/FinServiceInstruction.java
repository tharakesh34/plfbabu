package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.WSReturnStatus;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

@XmlAccessorType(XmlAccessType.NONE)
public class FinServiceInstruction  extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 2803331023129230226L;

	public FinServiceInstruction() {
		super();
	}
	private long  serviceSeqId =  Long.MIN_VALUE;
	@XmlElement
	private String finReference;
	private String finEvent;
	@XmlElement
	private Date fromDate;
	@XmlElement
	private Date toDate;
	@XmlElement
	private String pftDaysBasis;
	@XmlElement
	private BigDecimal actualRate = BigDecimal.ZERO;
	@XmlElement
	private String baseRate;
	@XmlElement
	private String splRate;
	@XmlElement
	private BigDecimal margin = BigDecimal.ZERO;
	@XmlElement(name="reCalType")
	private String recalType;
	@XmlElement(name="reCalFromDate")
	private Date recalFromDate;
	@XmlElement(name="reCalToDate")
	private Date recalToDate;
	@XmlElement(name="stpProcess")
	private boolean nonStp;
	@XmlElement
	private String processStage;
	@XmlElement
	private String reqType;
	@XmlElement
	private BigDecimal amount = BigDecimal.ZERO;
	@XmlElement
	private String schdMethod;
	@XmlElement
	private boolean pftIntact = false;
	@XmlElement
	private int terms = 0;
	@XmlElement
	private List<WSReturnStatus> returnStatus = null;
	@XmlElement
	private String repayFrq;
	private String repayPftFrq;
	private String repayRvwFrq;
	private String repayCpzFrq;
	
	private String grcPftFrq;
	private String grcRvwFrq;
	private String grcCpzFrq;
	@XmlElement
	private Date grcPeriodEndDate;
	@XmlElement
	private Date nextGrcRepayDate;
	@XmlElement
	private Date nextRepayDate;
	@XmlElement
	private int frqDay;
	@XmlElement
	private BigDecimal refund = BigDecimal.ZERO;
	@XmlElement
	private String dsaCode;
	@XmlElement
	private String salesDepartment;
	@XmlElement
	private String dmaCode;
	@XmlElement
	private String accountsOfficer;
	@XmlElement
	private String referralId;
	private int adjRpyTerms = 0;
	@XmlElement
	private String paymentMode;
	@XmlElement
	private String excessAdjustTo;
	@XmlElement
	private FinReceiptDetail receiptDetail;
	@XmlElementWrapper(name="disbursements")
	@XmlElement(name="disbursement")
	private List<FinAdvancePayments> disbursementDetails;
	@XmlElementWrapper(name="fees")
	@XmlElement(name="fee")
	private List<FinFeeDetail> finFeeDetails;
	@XmlElement(name="overdue")
	private FinODPenaltyRate finODPenaltyRate;
	private String moduleDefiner;
	private boolean newRecord;
	private boolean wif;
	@XmlElement
	private String 	serviceReqNo;
	private String	remarks;
	private BigDecimal remPartPayAmt = BigDecimal.ZERO;

	// Bean validation purpose
	@SuppressWarnings("unused")
	private FinServiceInstruction validateAddRateChange = this;
	
	@SuppressWarnings("unused")
	private FinServiceInstruction validateChangeRepayment = this;


	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public BigDecimal getActualRate() {
		return actualRate;
	}

	public void setActualRate(BigDecimal actualRate) {
		this.actualRate = actualRate;
	}

	public String getBaseRate() {
		return baseRate;
	}

	public void setBaseRate(String baseRate) {
		this.baseRate = baseRate;
	}

	public String getSplRate() {
		return splRate;
	}

	public void setSplRate(String splRate) {
		this.splRate = splRate;
	}

	public BigDecimal getMargin() {
		return margin;
	}

	public void setMargin(BigDecimal margin) {
		this.margin = margin;
	}

	public String getRecalType() {
		return recalType;
	}

	public void setRecalType(String recalType) {
		this.recalType = recalType;
	}

	public String getReqType() {
		return reqType;
	}

	public void setReqType(String reqType) {
		this.reqType = reqType;
	}

	public String getPftDaysBasis() {
		return pftDaysBasis;
	}

	public void setPftDaysBasis(String pftDaysBasis) {
		this.pftDaysBasis = pftDaysBasis;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getSchdMethod() {
		return schdMethod;
	}

	public void setSchdMethod(String schdMethod) {
		this.schdMethod = schdMethod;
	}

	public boolean isPftIntact() {
		return pftIntact;
	}

	public void setPftIntact(boolean pftIntact) {
		this.pftIntact = pftIntact;
	}

	public Date getRecalFromDate() {
		return recalFromDate;
	}

	public void setRecalFromDate(Date recalFromDate) {
		this.recalFromDate = recalFromDate;
	}

	public Date getRecalToDate() {
		return recalToDate;
	}

	public void setRecalToDate(Date recalToDate) {
		this.recalToDate = recalToDate;
	}

	public int getTerms() {
		return terms;
	}

	public void setTerms(int terms) {
		this.terms = terms;
	}

	public boolean isNonStp() {
		return nonStp;
	}

	public void setNonStp(boolean nonStp) {
		this.nonStp = nonStp;
	}

	public String getProcessStage() {
		return processStage;
	}

	public void setProcessStage(String processStage) {
		this.processStage = processStage;
	}

	public List<WSReturnStatus> getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(List<WSReturnStatus> returnStatus) {
		this.returnStatus = returnStatus;
	}

	public Date getGrcPeriodEndDate() {
		return grcPeriodEndDate;
	}

	public void setGrcPeriodEndDate(Date grcPeriodEndDate) {
		this.grcPeriodEndDate = grcPeriodEndDate;
	}

	public Date getNextGrcRepayDate() {
		return nextGrcRepayDate;
	}

	public void setNextGrcRepayDate(Date nextGrcRepayDate) {
		this.nextGrcRepayDate = nextGrcRepayDate;
	}

	public Date getNextRepayDate() {
		return nextRepayDate;
	}

	public void setNextRepayDate(Date nextRepayDate) {
		this.nextRepayDate = nextRepayDate;
	}

	public String getRepayFrq() {
		return repayFrq;
	}

	public void setRepayFrq(String repayFrq) {
		this.repayFrq = repayFrq;
	}
	
	public List<FinAdvancePayments> getDisbursementDetails() {
		return disbursementDetails;
	}

	public void setDisbursementDetails(List<FinAdvancePayments> disbursementDetails) {
		this.disbursementDetails = disbursementDetails;
	}
	
	public List<FinFeeDetail> getFinFeeDetails() {
		return finFeeDetails;
	}

	public void setFinFeeDetails(List<FinFeeDetail> finFeeDetails) {
		this.finFeeDetails = finFeeDetails;
	}

	public long getServiceSeqId() {
		return serviceSeqId;
	}

	public void setServiceSeqId(long serviceSeqId) {
		this.serviceSeqId = serviceSeqId;
	}

	public String getFinEvent() {
		return finEvent;
	}

	public void setFinEvent(String finEvent) {
		this.finEvent = finEvent;
	}

	public boolean isNew() {
		return isNewRecord();
	}
	
	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	
	@Override
	public long getId() {
		return serviceSeqId;
	}

	@Override
	public void setId(long id) {
		this.serviceSeqId = id;
	}
	
	public boolean isWif() {
		return wif;
	}

	public void setWif(boolean wif) {
		this.wif = wif;
	}

	public int getFrqDay() {
		return frqDay;
	}

	public void setFrqDay(int frqDay) {
		this.frqDay = frqDay;
	}

	public BigDecimal getRefund() {
		return refund;
	}

	public void setRefund(BigDecimal refund) {
		this.refund = refund;
	}

	public String getDsaCode() {
		return dsaCode;
	}

	public void setDsaCode(String dsaCode) {
		this.dsaCode = dsaCode;
	}

	public String getSalesDepartment() {
		return salesDepartment;
	}

	public void setSalesDepartment(String salesDepartment) {
		this.salesDepartment = salesDepartment;
	}

	public String getDmaCode() {
		return dmaCode;
	}

	public void setDmaCode(String dmaCode) {
		this.dmaCode = dmaCode;
	}

	public String getAccountsOfficer() {
		return accountsOfficer;
	}

	public void setAccountsOfficer(String accountsOfficer) {
		this.accountsOfficer = accountsOfficer;
	}

	public String getReferralId() {
		return referralId;
	}

	public void setReferralId(String referralId) {
		this.referralId = referralId;
	}
	
	public FinODPenaltyRate getFinODPenaltyRate() {
		return finODPenaltyRate;
	}
	public void setFinODPenaltyRate(FinODPenaltyRate finODPenaltyRate) {
		this.finODPenaltyRate = finODPenaltyRate;
	}
	
	public String getModuleDefiner() {
		return moduleDefiner;
	}

	public void setModuleDefiner(String moduleDefiner) {
		this.moduleDefiner = moduleDefiner;
	}

	public String getServiceReqNo() {
		return serviceReqNo;
	}

	public void setServiceReqNo(String serviceReqNo) {
		this.serviceReqNo = serviceReqNo;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public int getAdjRpyTerms() {
		return adjRpyTerms;
	}

	public void setAdjRpyTerms(int adjRpyTerms) {
		this.adjRpyTerms = adjRpyTerms;
	}
	
	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getExcessAdjustTo() {
		return excessAdjustTo;
	}

	public void setExcessAdjustTo(String excessAdjustTo) {
		this.excessAdjustTo = excessAdjustTo;
	}

	public FinReceiptDetail getReceiptDetail() {
		return receiptDetail;
	}

	public void setReceiptDetail(FinReceiptDetail receiptDetail) {
		this.receiptDetail = receiptDetail;
	}

	public String getRepayPftFrq() {
		return repayPftFrq;
	}
	public void setRepayPftFrq(String repayPftFrq) {
		this.repayPftFrq = repayPftFrq;
	}

	public String getRepayRvwFrq() {
		return repayRvwFrq;
	}
	public void setRepayRvwFrq(String repayRvwFrq) {
		this.repayRvwFrq = repayRvwFrq;
	}

	public String getRepayCpzFrq() {
		return repayCpzFrq;
	}
	public void setRepayCpzFrq(String repayCpzFrq) {
		this.repayCpzFrq = repayCpzFrq;
	}

	public String getGrcPftFrq() {
		return grcPftFrq;
	}
	public void setGrcPftFrq(String grcPftFrq) {
		this.grcPftFrq = grcPftFrq;
	}

	public String getGrcRvwFrq() {
		return grcRvwFrq;
	}
	public void setGrcRvwFrq(String grcRvwFrq) {
		this.grcRvwFrq = grcRvwFrq;
	}

	public String getGrcCpzFrq() {
		return grcCpzFrq;
	}
	public void setGrcCpzFrq(String grcCpzFrq) {
		this.grcCpzFrq = grcCpzFrq;
	}

	public BigDecimal getRemPartPayAmt() {
		return remPartPayAmt;
	}

	public void setRemPartPayAmt(BigDecimal remPartPayAmt) {
		this.remPartPayAmt = remPartPayAmt;
	}
	
}
