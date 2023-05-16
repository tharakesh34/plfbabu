package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.receiptupload.UploadAlloctionDetail;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.receipt.ReceiptPurpose;

@XmlAccessorType(XmlAccessType.NONE)
public class FinServiceInstruction extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 2803331023129230226L;

	public FinServiceInstruction() {
		super();
	}

	private long serviceSeqId = Long.MIN_VALUE;
	private long finID;
	@XmlElement
	private String finReference;
	@XmlElement
	private String finEvent;
	@XmlElement
	private String externalReference;
	private String module;
	@XmlElement
	private Date valueDate;
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
	@XmlElement
	private String graceBaseRate;
	@XmlElement
	private String graceSpecialRate;
	@XmlElement
	private BigDecimal grcMargin = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal grcPftRate = BigDecimal.ZERO;
	@XmlElement(name = "reCalType")
	private String recalType;
	@XmlElement(name = "reCalFromDate")
	private Date recalFromDate;
	@XmlElement(name = "reCalToDate")
	private Date recalToDate;
	@XmlElement(name = "stpProcess")
	private boolean nonStp = true;
	@XmlElement
	private String processStage;
	@XmlElement
	private String reqType;
	// ### 27-09-2019 Ticket id:124998
	@XmlElement
	private boolean receiptdetailExits;
	@XmlElement
	private BigDecimal amount = BigDecimal.ZERO;
	@XmlElement
	private String schdMethod;
	@XmlElement
	private String allocationType;
	@XmlElement(name = "fundingAccount")
	private long fundingAc = 0;
	@XmlElementWrapper(name = "allocationDetails")
	@XmlElement(name = "allocationDetail")
	private List<UploadAlloctionDetail> uploadAllocationDetails;
	@XmlElement
	private ReceiptPurpose receiptPurpose;
	private Map<String, BigDecimal> dueMap = new HashMap<>();
	@XmlElement
	private boolean pftIntact = false;
	@XmlElement
	private int terms = 0;
	@XmlElement
	private List<WSReturnStatus> returnStatus = null;
	@XmlElement
	private String paymentRef;
	@XmlElement
	private String favourNumber;
	@XmlElement
	private String bankCode;
	@XmlElement
	private String chequeNo;
	@XmlElement
	private String transactionRef;
	@XmlElement
	private String status;
	@XmlElement
	private Date depositDate;
	@XmlElement
	private Date realizationDate;
	@XmlElement
	private Date instrumentDate;
	@XmlElement
	private String serviceReqNo;
	@XmlElement
	private Date receivedDate;
	@XmlElement
	private String remarks;
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
	private BigDecimal pftChg = BigDecimal.ZERO;

	@XmlElement
	private int tenor = 0;
	@XmlElement
	private Date maturityDate;
	@XmlElement
	private String droplineFrq;
	@XmlElement
	private Date droplineDate;
	@XmlElement
	private String rateReviewFrq;

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
	private long accountsOfficer;
	@XmlElement
	private String referralId;
	private int adjRpyTerms = 0;
	@XmlElement
	private String paymentMode;
	@XmlElement
	private String excessAdjustTo;
	@XmlElement
	private FinReceiptDetail receiptDetail;
	@XmlElementWrapper(name = "disbursements")
	@XmlElement(name = "disbursement")
	private List<FinAdvancePayments> disbursementDetails;
	@XmlElementWrapper(name = "fees")
	@XmlElement(name = "fee")
	private List<FinFeeDetail> finFeeDetails = new ArrayList<>();
	@XmlElement(name = "overdue")
	private FinODPenaltyRate finODPenaltyRate;
	private String moduleDefiner;
	private boolean wif;
	private BigDecimal remPartPayAmt = BigDecimal.ZERO;
	@XmlElement(name = "UploadDetailId")
	private long uploadDetailId;// ### 18-07-2018 Ticket ID : 124998,receipt upload

	// ### 27-07-2018 Ticket id:124998
	@XmlElement
	private String receiptFileName;

	@XmlElement
	private String entity;

	@XmlElement
	private String entityDesc;

	@XmlElement
	private String restructuringType;

	private boolean quickDisb;

	private int strtPrdHdays;

	@XmlElement
	private Long custBankId;

	@XmlElement
	private String subReceiptMode;
	@XmlElement
	private String receiptChannel;
	@XmlElement
	private Long collectionAgentId;
	@XmlElement
	private String receivedFrom;
	@XmlElement
	private String panNumber;
	@XmlElement
	private Long earlySettlementReason;
	private long adviseId;
	private BigDecimal adviseAmount;
	private boolean excldTdsCal = false;
	private BigDecimal bpiAmount = BigDecimal.ZERO; // Restructure Purpose

	private Date initiatedDate;
	private Date approvedDate;
	private Map<String, BigDecimal> taxPercentages = new HashMap<>();
	@XmlElement
	private String moduleType;

	private List<FinanceScheduleDetail> oldShedules = new ArrayList<>();
	private List<FinReceiptDetail> receiptDetails = new ArrayList<>();

	public String getRestructuringType() {
		return restructuringType;
	}

	public void setRestructuringType(String restructuringType) {
		this.restructuringType = restructuringType;
	}

	@XmlElement(name = "depositAccount")
	private String depositAcc;

	// ### 16-08-2018 Ticket ID : 124998,receipt upload
	private BigDecimal closingBal = BigDecimal.ZERO;

	@XmlElement
	private String rootId;

	@XmlElement
	private int grcTerms;

	@XmlElement
	private boolean bckdtdWthOldDues;

	public int getGrcTerms() {
		return grcTerms;
	}

	public void setGrcTerms(int grcTerms) {
		this.grcTerms = grcTerms;
	}

	@XmlElement
	private boolean receiptResponse;

	private Map<String, BigDecimal> manualAllocMap = new HashMap<>();

	private Map<String, BigDecimal> manualWaiverMap = new HashMap<>();

	// Bean validation purpose
	private FinServiceInstruction validateAddRateChange = this;

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	@XmlElement(name = "isUpload")
	private boolean isReceiptUpload;

	private FinServiceInstruction validateChangeRepayment = this;

	@XmlElement
	private String finType;
	@XmlElement
	private String currency;
	private List<FinTypeFees> finTypeFeeList = new ArrayList<>(1);

	private long receiptId;

	@XmlElement
	private String fromBranch;
	@XmlElement
	private String toBranch;
	@XmlElement
	private String fromState;
	@XmlElement
	private String toState;

	private long instructionUID = Long.MIN_VALUE;
	private long linkedTranID = 0;

	@XmlElement
	private String reqFrom;

	private Date appDate;
	private Date systemDate;
	private Date makerAppDate;
	private Date makerSysDate;
	private Date checkerAppDate;

	private Date checkerSysDate;
	private String reference;
	private long maker = Long.MIN_VALUE;
	private long checker = Long.MIN_VALUE;

	@XmlElementWrapper(name = "extendedDetails")
	@XmlElement(name = "extendedDetail")
	private List<ExtendedField> extendedDetails;

	private LoggedInUser loggedInUser;

	// AddFlexiDisbursement
	@XmlElement
	private boolean isFlexiDisb;
	@XmlElement
	private boolean normalLoanClosure = false;
	@XmlElement
	private long paymentId = Long.MIN_VALUE;
	private Long logKey;
	private RequestSource requestSource = RequestSource.UI;

	@XmlElement
	private String closureType;
	private String bounceReason;
	private String cancelReason;
	private Date bounceDate;
	private boolean isNewReceipt;

	// Charge Details
	@XmlElement(name = "txnChrgReq")
	private boolean overdraftTxnChrgReq;
	@XmlElement(name = "oDCalculatedCharge")
	private String overdraftCalcChrg;
	@XmlElement(name = "oDChargeCalOn")
	private String overdraftChrCalOn;
	@XmlElement(name = "oDChargeAmtOrPerc")
	private BigDecimal overdraftChrgAmtOrPerc = BigDecimal.ZERO;

	@XmlElement
	private BigDecimal finAssetValue = BigDecimal.ZERO;
	@XmlElement
	private int numberOfTerms = 0;

	private long custID;
	@XmlElement(name = "cif")
	private String custCIF;
	@XmlElement
	private BigDecimal tdsAmount = BigDecimal.ZERO;
	@XmlElement
	private String receiptSource;
	@XmlElement
	private String recAgainst;
	@XmlElement
	private String collectionAgency;
	@XmlElement
	private String division;
	private boolean knockOffReceipt;
	private String knockoffType;
	private boolean loanCancellation;
	private boolean closureReceipt;

	public FinServiceInstruction copyEntity() {
		FinServiceInstruction fsi = new FinServiceInstruction();

		fsi.setServiceSeqId(this.serviceSeqId);
		fsi.setFinID(this.finID);
		fsi.setFinReference(this.finReference);
		fsi.setFinEvent(this.finEvent);
		fsi.setExternalReference(this.externalReference);
		fsi.setModule(this.module);
		fsi.setValueDate(this.valueDate);
		fsi.setFromDate(this.fromDate);
		fsi.setToDate(this.toDate);
		fsi.setPftDaysBasis(this.pftDaysBasis);
		fsi.setActualRate(this.actualRate);
		fsi.setBaseRate(this.baseRate);
		fsi.setSplRate(this.splRate);
		fsi.setMargin(this.margin);
		fsi.setRecalType(this.recalType);
		fsi.setRecalFromDate(this.recalFromDate);
		fsi.setRecalToDate(this.recalToDate);
		fsi.setNonStp(this.nonStp);
		fsi.setProcessStage(this.processStage);
		fsi.setReqType(this.reqType);
		fsi.setReceiptdetailExits(this.receiptdetailExits);
		fsi.setAmount(this.amount);
		fsi.setSchdMethod(this.schdMethod);
		fsi.setAllocationType(this.allocationType);
		fsi.setFundingAc(this.fundingAc);
		fsi.setInitiatedDate(this.initiatedDate);
		fsi.setApprovedDate(this.approvedDate);

		if (uploadAllocationDetails != null) {
			fsi.setUploadAllocationDetails(new ArrayList<>());
			this.uploadAllocationDetails.stream()
					.forEach(e -> fsi.getUploadAllocationDetails().add(e == null ? null : e.copyEntity()));
		}

		if (this.receiptPurpose != null) {
			fsi.setReceiptPurpose(this.receiptPurpose.code());
		}

		this.dueMap.entrySet().stream().forEach(e -> fsi.getDueMap().put(e.getKey(), e.getValue()));
		fsi.setPftIntact(this.pftIntact);
		fsi.setTerms(this.terms);

		if (returnStatus != null) {
			fsi.setReturnStatus(new ArrayList<>());
			this.returnStatus.stream().forEach(e -> fsi.getReturnStatus().add(e == null ? null : e.copyEntity()));
		}

		fsi.setPaymentRef(this.paymentRef);
		fsi.setFavourNumber(this.favourNumber);
		fsi.setBankCode(this.bankCode);
		fsi.setChequeNo(this.chequeNo);
		fsi.setTransactionRef(this.transactionRef);
		fsi.setStatus(this.status);
		fsi.setDepositDate(this.depositDate);
		fsi.setRealizationDate(this.realizationDate);
		fsi.setInstrumentDate(this.instrumentDate);
		fsi.setServiceReqNo(this.serviceReqNo);
		fsi.setReceivedDate(this.receivedDate);
		fsi.setRemarks(this.remarks);
		fsi.setRepayFrq(this.repayFrq);
		fsi.setRepayPftFrq(this.repayPftFrq);
		fsi.setRepayRvwFrq(this.repayRvwFrq);
		fsi.setRepayCpzFrq(this.repayCpzFrq);
		fsi.setGrcPftFrq(this.grcPftFrq);
		fsi.setGrcRvwFrq(this.grcRvwFrq);
		fsi.setGrcCpzFrq(this.grcCpzFrq);
		fsi.setGrcPeriodEndDate(this.grcPeriodEndDate);
		fsi.setNextGrcRepayDate(this.nextGrcRepayDate);
		fsi.setNextRepayDate(this.nextRepayDate);
		fsi.setPftChg(this.pftChg);
		fsi.setTenor(this.tenor);
		fsi.setMaturityDate(this.maturityDate);
		fsi.setDroplineFrq(this.droplineFrq);
		fsi.setDroplineDate(this.droplineDate);
		fsi.setRateReviewFrq(this.rateReviewFrq);
		fsi.setFrqDay(this.frqDay);
		fsi.setRefund(this.refund);
		fsi.setDsaCode(this.dsaCode);
		fsi.setSalesDepartment(this.salesDepartment);
		fsi.setDmaCode(this.dmaCode);
		fsi.setAccountsOfficer(this.accountsOfficer);
		fsi.setReferralId(this.referralId);
		fsi.setAdjRpyTerms(this.adjRpyTerms);
		fsi.setPaymentMode(this.paymentMode);
		fsi.setExcessAdjustTo(this.excessAdjustTo);
		fsi.setReceiptDetail(this.receiptDetail == null ? null : this.receiptDetail.copyEntity());

		if (disbursementDetails != null) {
			fsi.setDisbursementDetails(new ArrayList<>());
			this.disbursementDetails.stream()
					.forEach(e -> fsi.getDisbursementDetails().add(e == null ? null : e.copyEntity()));
		}

		this.finFeeDetails.stream().forEach(e -> fsi.getFinFeeDetails().add(e == null ? null : e.copyEntity()));
		fsi.setFinODPenaltyRate(this.finODPenaltyRate == null ? null : this.finODPenaltyRate.copyEntity());
		fsi.setModuleDefiner(this.moduleDefiner);
		fsi.setNewRecord(super.isNewRecord());
		fsi.setWif(this.wif);
		fsi.setRemPartPayAmt(this.remPartPayAmt);
		fsi.setUploadDetailId(this.uploadDetailId);
		fsi.setReceiptFileName(this.receiptFileName);
		fsi.setEntity(this.entity);
		fsi.setEntityDesc(this.entityDesc);
		fsi.setRestructuringType(this.restructuringType);
		fsi.setQuickDisb(this.quickDisb);
		fsi.setStrtPrdHdays(this.strtPrdHdays);
		fsi.setSubReceiptMode(this.subReceiptMode);
		fsi.setReceiptChannel(this.receiptChannel);
		fsi.setCollectionAgentId(this.collectionAgentId);
		fsi.setReceivedFrom(this.receivedFrom);
		fsi.setPanNumber(this.panNumber);
		fsi.setEarlySettlementReason(this.earlySettlementReason);
		fsi.setAdviseId(this.adviseId);
		fsi.setAdviseAmount(this.adviseAmount);
		fsi.setDepositAcc(this.depositAcc);
		fsi.setClosingBal(this.closingBal);
		fsi.setRootId(this.rootId);
		fsi.setGrcTerms(this.grcTerms);
		fsi.setBckdtdWthOldDues(this.bckdtdWthOldDues);
		fsi.setReceiptResponse(this.receiptResponse);
		this.manualAllocMap.entrySet().stream().forEach(e -> fsi.getManualAllocMap().put(e.getKey(), e.getValue()));
		this.manualWaiverMap.entrySet().stream().forEach(e -> fsi.getManualWaiverMap().put(e.getKey(), e.getValue()));
		fsi.setReceiptUpload(this.isReceiptUpload);
		fsi.setFinType(this.finType);
		fsi.setCurrency(this.currency);
		this.finTypeFeeList.stream().forEach(e -> fsi.getFinTypeFeeList().add(e == null ? null : e.copyEntity()));
		fsi.setReceiptId(this.receiptId);
		fsi.setFromBranch(this.fromBranch);
		fsi.setToBranch(this.toBranch);
		fsi.setInstructionUID(this.instructionUID);
		fsi.setLinkedTranID(this.linkedTranID);
		fsi.setReqFrom(this.reqFrom);
		fsi.setAppDate(this.appDate);
		fsi.setSystemDate(this.systemDate);
		fsi.setMakerAppDate(this.makerAppDate);
		fsi.setMakerSysDate(this.makerSysDate);
		fsi.setCheckerAppDate(this.checkerAppDate);
		fsi.setCheckerSysDate(this.checkerSysDate);
		fsi.setReference(this.reference);
		fsi.setMaker(this.maker);
		fsi.setChecker(this.checker);
		fsi.setExcldTdsCal(this.excldTdsCal);
		fsi.setBpiAmount(this.bpiAmount);

		if (extendedDetails != null) {
			fsi.setExtendedDetails(new ArrayList<>());
			this.extendedDetails.stream().forEach(e -> fsi.getExtendedDetails().add(e == null ? null : e.copyEntity()));
		}

		fsi.setLoggedInUser(this.loggedInUser);
		fsi.setFlexiDisb(this.isFlexiDisb);
		fsi.setNormalLoanClosure(this.normalLoanClosure);
		fsi.setPaymentId(this.paymentId);
		fsi.setCustID(this.custID);
		fsi.setCustCIF(this.custCIF);
		fsi.setTdsAmount(this.tdsAmount);
		fsi.setReceiptSource(this.receiptSource);
		fsi.setRecAgainst(this.recAgainst);
		fsi.setCollectionAgency(this.collectionAgency);
		fsi.setDivision(this.division);
		fsi.setLogKey(this.logKey);
		fsi.setRequestSource(this.requestSource);

		fsi.setGraceBaseRate(this.graceBaseRate);
		fsi.setGraceSpecialRate(this.graceSpecialRate);
		fsi.setGrcMargin(this.grcMargin);
		fsi.setGrcPftRate(this.grcPftRate);
		fsi.setCustBankId(this.custBankId);
		fsi.setTaxPercentages(this.taxPercentages);
		fsi.setModuleType(this.moduleType);
		this.oldShedules.stream().forEach(e -> fsi.getOldShedules().add(e == null ? null : e.copyEntity()));
		fsi.setFromState(this.fromState);
		fsi.setToState(this.toState);
		fsi.setClosureType(this.closureType);
		fsi.setBounceReason(this.bounceReason);
		fsi.setCancelReason(this.cancelReason);
		fsi.setBounceDate(this.bounceDate);
		fsi.setNewReceipt(this.isNewReceipt);
		fsi.setOverdraftTxnChrgReq(this.overdraftTxnChrgReq);
		fsi.setOverdraftCalcChrg(this.overdraftCalcChrg);
		fsi.setOverdraftChrCalOn(this.overdraftChrCalOn);
		fsi.setOverdraftChrgAmtOrPerc(this.overdraftChrgAmtOrPerc);
		fsi.setFinAssetValue(this.finAssetValue);
		fsi.setNumberOfTerms(this.numberOfTerms);
		fsi.setKnockOffReceipt(this.knockOffReceipt);
		fsi.setLoanCancellation(this.loanCancellation);
		fsi.setClosureReceipt(this.closureReceipt);
		fsi.setRecordStatus(super.getRecordStatus());
		fsi.setRoleCode(super.getRoleCode());
		fsi.setNextRoleCode(super.getNextRoleCode());
		fsi.setTaskId(super.getTaskId());
		fsi.setNextTaskId(super.getNextTaskId());
		fsi.setRecordType(super.getRecordType());
		fsi.setWorkflowId(super.getWorkflowId());
		fsi.setUserAction(super.getUserAction());
		fsi.setVersion(super.getVersion());
		fsi.setLastMntBy(super.getLastMntBy());
		fsi.setLastMntOn(super.getLastMntOn());
		return fsi;
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

	public BigDecimal getGrcPftRate() {
		return grcPftRate;
	}

	public void setGrcPftRate(BigDecimal grcPftRate) {
		this.grcPftRate = grcPftRate;
	}

	public String getGraceBaseRate() {
		return graceBaseRate;
	}

	public void setGraceBaseRate(String graceBaseRate) {
		this.graceBaseRate = graceBaseRate;
	}

	public String getGraceSpecialRate() {
		return graceSpecialRate;
	}

	public void setGraceSpecialRate(String graceSpecialRate) {
		this.graceSpecialRate = graceSpecialRate;
	}

	public BigDecimal getGrcMargin() {
		return grcMargin;
	}

	public void setGrcMargin(BigDecimal grcMargin) {
		this.grcMargin = grcMargin;
	}

	public String getRecalType() {
		return recalType == null ? "" : recalType;
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

	public long getId() {
		return serviceSeqId;
	}

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

	public long getAccountsOfficer() {
		return accountsOfficer;
	}

	public void setAccountsOfficer(long accountsOfficer) {
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

	public Long getCustBankId() {
		return custBankId;
	}

	public void setCustBankId(Long custBankId) {
		this.custBankId = custBankId;
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
		return paymentMode == null ? "" : paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getExcessAdjustTo() {
		return excessAdjustTo == null ? "" : excessAdjustTo;
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

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public List<FinTypeFees> getFinTypeFeeList() {
		return finTypeFeeList;
	}

	public void setFinTypeFeeList(List<FinTypeFees> finTypeFeeList) {
		this.finTypeFeeList = finTypeFeeList;
	}

	public long getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(long receiptId) {
		this.receiptId = receiptId;
	}

	public String getExternalReference() {
		return externalReference;
	}

	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getFromBranch() {
		return fromBranch;
	}

	public void setFromBranch(String fromBranch) {
		this.fromBranch = fromBranch;
	}

	public String getToBranch() {
		return toBranch;
	}

	public void setToBranch(String toBranch) {
		this.toBranch = toBranch;
	}

	public String getFromState() {
		return fromState;
	}

	public void setFromState(String fromState) {
		this.fromState = fromState;
	}

	public String getToState() {
		return toState;
	}

	public void setToState(String toState) {
		this.toState = toState;
	}

	public BigDecimal getPftChg() {
		return pftChg;
	}

	public void setPftChg(BigDecimal pftChg) {
		this.pftChg = pftChg;
	}

	public int getTenor() {
		return tenor;
	}

	public void setTenor(int tenor) {
		this.tenor = tenor;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public String getDroplineFrq() {
		return droplineFrq;
	}

	public void setDroplineFrq(String droplineFrq) {
		this.droplineFrq = droplineFrq;
	}

	public Date getDroplineDate() {
		return droplineDate;
	}

	public void setDroplineDate(Date droplineDate) {
		this.droplineDate = droplineDate;
	}

	public String getRateReviewFrq() {
		return rateReviewFrq;
	}

	public void setRateReviewFrq(String rateReviewFrq) {
		this.rateReviewFrq = rateReviewFrq;
	}

	public List<ExtendedField> getExtendedDetails() {
		return extendedDetails;
	}

	public void setExtendedDetails(List<ExtendedField> extendedDetails) {
		this.extendedDetails = extendedDetails;
	}

	public long getInstructionUID() {
		return instructionUID;
	}

	public void setInstructionUID(long instructionUID) {
		this.instructionUID = instructionUID;
	}

	public String getPaymentRef() {
		return paymentRef;
	}

	public void setPaymentRef(String paymentRef) {
		this.paymentRef = paymentRef;
	}

	public String getFavourNumber() {
		return favourNumber;
	}

	public void setFavourNumber(String favourNumber) {
		this.favourNumber = favourNumber;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public String getTransactionRef() {
		return transactionRef;
	}

	public void setTransactionRef(String transactionRef) {
		this.transactionRef = transactionRef;
	}

	public String getStatus() {
		return status == null ? "" : status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getDepositDate() {
		return depositDate;
	}

	public void setDepositDate(Date depositDate) {
		this.depositDate = depositDate;
	}

	public Date getRealizationDate() {
		return realizationDate;
	}

	public void setRealizationDate(Date realizationDate) {
		this.realizationDate = realizationDate;
	}

	public Date getInstrumentDate() {
		return instrumentDate;
	}

	public void setInstrumentDate(Date instrumentDate) {
		this.instrumentDate = instrumentDate;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	public boolean isReceiptdetailExits() {
		return receiptdetailExits;
	}

	public void setReceiptdetailExits(boolean receiptdetailExits) {
		this.receiptdetailExits = receiptdetailExits;
	}

	public FinServiceInstruction getValidateAddRateChange() {
		return validateAddRateChange;
	}

	public void setValidateAddRateChange(FinServiceInstruction validateAddRateChange) {
		this.validateAddRateChange = validateAddRateChange;
	}

	public FinServiceInstruction getValidateChangeRepayment() {
		return validateChangeRepayment;
	}

	public void setValidateChangeRepayment(FinServiceInstruction validateChangeRepayment) {
		this.validateChangeRepayment = validateChangeRepayment;
	}

	public BigDecimal getClosingBal() {
		return closingBal;
	}

	public void setClosingBal(BigDecimal closingBal) {
		this.closingBal = closingBal;
	}

	public Map<String, BigDecimal> getManualAllocMap() {
		return manualAllocMap;
	}

	public void setManualAllocMap(Map<String, BigDecimal> manualAllocMap) {
		this.manualAllocMap = manualAllocMap;
	}

	public Map<String, BigDecimal> getManualWaiverMap() {
		return manualWaiverMap;
	}

	public void setManualWaiverMap(Map<String, BigDecimal> manualWaiverMap) {
		this.manualWaiverMap = manualWaiverMap;
	}

	public long getUploadDetailId() {
		return uploadDetailId;
	}

	public void setUploadDetailId(long uploadDetailId) {
		this.uploadDetailId = uploadDetailId;
	}

	public String getReceiptFileName() {
		return receiptFileName;
	}

	public void setReceiptFileName(String receiptFileName) {
		this.receiptFileName = receiptFileName;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getEntityDesc() {
		return entityDesc;
	}

	public void setEntityDesc(String entityDesc) {
		this.entityDesc = entityDesc;
	}

	public String getDepositAcc() {
		return depositAcc;
	}

	public void setDepositAcc(String depositAcc) {
		this.depositAcc = depositAcc;
	}

	public String getRootId() {
		return rootId;
	}

	public void setRootId(String rootId) {
		this.rootId = rootId;
	}

	public boolean isReceiptResponse() {
		return receiptResponse;
	}

	public void setReceiptResponse(boolean receiptResponse) {
		this.receiptResponse = receiptResponse;
	}

	public boolean isReceiptUpload() {
		return isReceiptUpload;
	}

	public void setReceiptUpload(boolean isReceiptUpload) {
		this.isReceiptUpload = isReceiptUpload;
	}

	public String getAllocationType() {
		return allocationType == null ? "" : allocationType;
	}

	public void setAllocationType(String allocationType) {
		this.allocationType = allocationType;
	}

	public Map<String, BigDecimal> getDueMap() {
		return dueMap;
	}

	public void setDueMap(Map<String, BigDecimal> dueMap) {
		this.dueMap = dueMap;
	}

	public long getFundingAc() {
		return fundingAc;
	}

	public void setFundingAc(long fundingAc) {
		this.fundingAc = fundingAc;
	}

	public List<UploadAlloctionDetail> getUploadAllocationDetails() {
		return uploadAllocationDetails;
	}

	public void setUploadAllocationDetails(List<UploadAlloctionDetail> uploadAllocationDetails) {
		this.uploadAllocationDetails = uploadAllocationDetails;
	}

	public ReceiptPurpose getReceiptPurpose() {
		return receiptPurpose;
	}

	public void setReceiptPurpose(String receiptPurpose) {
		this.receiptPurpose = ReceiptPurpose.purpose(receiptPurpose);
	}

	public long getLinkedTranID() {
		return linkedTranID;
	}

	public void setLinkedTranID(long linkedTranID) {
		this.linkedTranID = linkedTranID;
	}

	public boolean isQuickDisb() {
		return quickDisb;
	}

	public void setQuickDisb(boolean quickDisb) {
		this.quickDisb = quickDisb;
	}

	public int getStrtPrdHdays() {
		return strtPrdHdays;
	}

	public void setStrtPrdHdays(int strtPrdHdays) {
		this.strtPrdHdays = strtPrdHdays;
	}

	public Date getSystemDate() {
		return systemDate;
	}

	public void setSystemDate(Date systemDate) {
		this.systemDate = systemDate;
	}

	public Date getMakerAppDate() {
		return makerAppDate;
	}

	public void setMakerAppDate(Date makerAppDate) {
		this.makerAppDate = makerAppDate;
	}

	public Date getMakerSysDate() {
		return makerSysDate;
	}

	public void setMakerSysDate(Date makerSysDate) {
		this.makerSysDate = makerSysDate;
	}

	public Date getCheckerAppDate() {
		return checkerAppDate;
	}

	public void setCheckerAppDate(Date checkerAppDate) {
		this.checkerAppDate = checkerAppDate;
	}

	public Date getCheckerSysDate() {
		return checkerSysDate;
	}

	public void setCheckerSysDate(Date checkerSysDate) {
		this.checkerSysDate = checkerSysDate;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public Date getAppDate() {
		return appDate;
	}

	public String getReqFrom() {
		return reqFrom;
	}

	public void setReqFrom(String reqFrom) {
		this.reqFrom = reqFrom;
	}

	public long getMaker() {
		return maker;
	}

	public void setMaker(long maker) {
		this.maker = maker;
	}

	public long getChecker() {
		return checker;
	}

	public void setChecker(long checker) {
		this.checker = checker;
	}

	public String getSubReceiptMode() {
		return subReceiptMode;
	}

	public void setSubReceiptMode(String subReceiptMode) {
		this.subReceiptMode = subReceiptMode;
	}

	public String getReceiptChannel() {
		return receiptChannel;
	}

	public void setReceiptChannel(String receiptChannel) {
		this.receiptChannel = receiptChannel;
	}

	public Long getCollectionAgentId() {
		return collectionAgentId;
	}

	public void setCollectionAgentId(Long collectionAgentId) {
		this.collectionAgentId = collectionAgentId;
	}

	public String getReceivedFrom() {
		return receivedFrom;
	}

	public void setReceivedFrom(String receivedFrom) {
		this.receivedFrom = receivedFrom;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}

	public LoggedInUser getLoggedInUser() {
		return loggedInUser;
	}

	public void setLoggedInUser(LoggedInUser loggedInUser) {
		this.loggedInUser = loggedInUser;
	}

	public boolean isFlexiDisb() {
		return isFlexiDisb;
	}

	public void setFlexiDisb(boolean isFlexiDisb) {
		this.isFlexiDisb = isFlexiDisb;
	}

	public Long getEarlySettlementReason() {
		return earlySettlementReason;
	}

	public void setEarlySettlementReason(Long earlySettlementReason) {
		this.earlySettlementReason = earlySettlementReason;
	}

	public long getAdviseId() {
		return adviseId;
	}

	public void setAdviseId(long adviseId) {
		this.adviseId = adviseId;
	}

	public BigDecimal getAdviseAmount() {
		return adviseAmount;
	}

	public void setAdviseAmount(BigDecimal adviseAmount) {
		this.adviseAmount = adviseAmount;
	}

	public boolean isNormalLoanClosure() {
		return normalLoanClosure;
	}

	public void setNormalLoanClosure(boolean normalLoanClosure) {
		this.normalLoanClosure = normalLoanClosure;
	}

	public boolean isBckdtdWthOldDues() {
		return bckdtdWthOldDues;
	}

	public void setBckdtdWthOldDues(boolean bckdtdWthOldDues) {
		this.bckdtdWthOldDues = bckdtdWthOldDues;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(long paymentId) {
		this.paymentId = paymentId;
	}

	public BigDecimal getTdsAmount() {
		return tdsAmount;
	}

	public void setTdsAmount(BigDecimal tdsAmount) {
		this.tdsAmount = tdsAmount;
	}

	public String getReceiptSource() {
		return receiptSource;
	}

	public void setReceiptSource(String receiptSource) {
		this.receiptSource = receiptSource;
	}

	public String getRecAgainst() {
		return recAgainst;
	}

	public void setRecAgainst(String recAgainst) {
		this.recAgainst = recAgainst;
	}

	public String getCollectionAgency() {
		return collectionAgency;
	}

	public void setCollectionAgency(String collectionAgency) {
		this.collectionAgency = collectionAgency;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public Long getLogKey() {
		return logKey;
	}

	public void setLogKey(Long logKey) {
		this.logKey = logKey;
	}

	public boolean isExcldTdsCal() {
		return excldTdsCal;
	}

	public void setExcldTdsCal(boolean excldTdsCal) {
		this.excldTdsCal = excldTdsCal;
	}

	public BigDecimal getBpiAmount() {
		return bpiAmount;
	}

	public void setBpiAmount(BigDecimal bpiAmount) {
		this.bpiAmount = bpiAmount;
	}

	public Date getInitiatedDate() {
		return initiatedDate;
	}

	public void setInitiatedDate(Date initiatedDate) {
		this.initiatedDate = initiatedDate;
	}

	public Date getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	public RequestSource getRequestSource() {
		return requestSource;
	}

	public void setRequestSource(RequestSource requestSource) {
		this.requestSource = requestSource;
	}

	public String getClosureType() {
		return closureType;
	}

	public void setClosureType(String closureType) {
		this.closureType = closureType;
	}

	public String getBounceReason() {
		return bounceReason;
	}

	public void setBounceReason(String bounceReason) {
		this.bounceReason = bounceReason;
	}

	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	public Date getBounceDate() {
		return bounceDate;
	}

	public void setBounceDate(Date bounceDate) {
		this.bounceDate = bounceDate;
	}

	public boolean isNewReceipt() {
		return isNewReceipt;
	}

	public void setNewReceipt(boolean isNewReceipt) {
		this.isNewReceipt = isNewReceipt;
	}

	public Map<String, BigDecimal> getTaxPercentages() {
		return taxPercentages;
	}

	public void setTaxPercentages(Map<String, BigDecimal> taxPercentages) {
		this.taxPercentages = taxPercentages;
	}

	public boolean isOverdraftTxnChrgReq() {
		return overdraftTxnChrgReq;
	}

	public void setOverdraftTxnChrgReq(boolean overdraftTxnChrgReq) {
		this.overdraftTxnChrgReq = overdraftTxnChrgReq;
	}

	public String getOverdraftCalcChrg() {
		return overdraftCalcChrg;
	}

	public void setOverdraftCalcChrg(String overdraftCalcChrg) {
		this.overdraftCalcChrg = overdraftCalcChrg;
	}

	public String getOverdraftChrCalOn() {
		return overdraftChrCalOn;
	}

	public void setOverdraftChrCalOn(String overdraftChrCalOn) {
		this.overdraftChrCalOn = overdraftChrCalOn;
	}

	public BigDecimal getOverdraftChrgAmtOrPerc() {
		return overdraftChrgAmtOrPerc;
	}

	public void setOverdraftChrgAmtOrPerc(BigDecimal overdraftChrgAmtOrPerc) {
		this.overdraftChrgAmtOrPerc = overdraftChrgAmtOrPerc;
	}

	public BigDecimal getFinAssetValue() {
		return finAssetValue;
	}

	public void setFinAssetValue(BigDecimal finAssetValue) {
		this.finAssetValue = finAssetValue;
	}

	public int getNumberOfTerms() {
		return numberOfTerms;
	}

	public void setNumberOfTerms(int numberOfTerms) {
		this.numberOfTerms = numberOfTerms;
	}

	public String getModuleType() {
		return moduleType;
	}

	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}

	public List<FinanceScheduleDetail> getOldShedules() {
		return oldShedules;
	}

	public void setOldShedules(List<FinanceScheduleDetail> oldShedules) {
		this.oldShedules = oldShedules;
	}

	public boolean isKnockOffReceipt() {
		return knockOffReceipt;
	}

	public void setKnockOffReceipt(boolean knockOffReceipt) {
		this.knockOffReceipt = knockOffReceipt;
	}

	public List<FinReceiptDetail> getReceiptDetails() {
		return receiptDetails;
	}

	public void setReceiptDetails(List<FinReceiptDetail> receiptDetails) {
		this.receiptDetails = receiptDetails;
	}

	public String getKnockoffType() {
		return knockoffType;
	}

	public void setKnockoffType(String knockoffType) {
		this.knockoffType = knockoffType;
	}

	public boolean isLoanCancellation() {
		return loanCancellation;
	}

	public void setLoanCancellation(boolean loanCancellation) {
		this.loanCancellation = loanCancellation;
	}

	public boolean isClosureReceipt() {
		return closureReceipt;
	}

	public void setClosureReceipt(boolean closureReceipt) {
		this.closureReceipt = closureReceipt;
	}

}
