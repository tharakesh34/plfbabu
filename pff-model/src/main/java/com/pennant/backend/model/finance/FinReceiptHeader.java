package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldExtension;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class FinReceiptHeader extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -58727889587717168L;

	private long receiptID = 0;
	private Date receiptDate;
	private String receiptType;
	private String recAgainst;
	private Long finID;
	private String reference;
	private String receiptPurpose;
	private String receiptMode;
	private String excessAdjustTo;
	private String finType;
	private String finTypeDesc;
	private String finBranch;
	private String finBranchDesc;
	private String finCcy;
	private String finCcyDesc;
	private long custID;
	private String custCIF;
	private String custShrtName;
	private String allocationType;
	private BigDecimal receiptAmount = BigDecimal.ZERO;
	private String effectSchdMethod;
	private String receiptModeStatus;
	private Date realizationDate;
	private String cancelReason;
	private String cancelReasonDesc;
	private boolean finIsActive;
	private String scheduleMethod;
	private String pftDaysBasis;
	private BigDecimal waviedAmt = BigDecimal.ZERO;
	private BigDecimal totFeeAmount = BigDecimal.ZERO;
	private Date bounceDate;
	private String rcdMaintainSts;
	private String cashierBranch;
	private Date initiateDate;
	private String partnerBankCode;
	private String knockoffId;
	private String knockoffFrom;
	private Date knockoffRefDate;
	private String knockoffAmount;
	private String slipNo;
	private String realizeStatus;
	private long bounceReason;
	private String realizeRemarks;
	private String extReference;
	private String module;
	private String subReceiptMode;
	private String receiptChannel;
	private long collectionAgentId;
	private String collectionAgentCode;
	private String collectionAgentDesc;
	private String receivedFrom;
	private String panNumber;
	private String paymentType;
	private String feeTypeCode;
	private boolean actFinReceipt;

	private String loanClosureCustCIF;
	protected String loanClosureFinReference;
	protected String loanClosureKnockOffFrom;
	protected String loanClosureRefId;
	protected Date loanClosureReceiptDate;
	protected Date loanClosureIntTillDate;

	private Date depositDate;
	private Date cancelDate;
	private String lovDescRequestStage;

	private Long custBankId;
	private String custAcctNumber;
	private String custAcctHolderName;

	private BigDecimal lpiAmount = BigDecimal.ZERO;
	private BigDecimal lppAmount = BigDecimal.ZERO;
	private BigDecimal gstLpiAmount = BigDecimal.ZERO;
	private BigDecimal gstLppAmount = BigDecimal.ZERO;

	private String remarks;
	private boolean depositProcess = false;
	private String depositBranch;
	private String lovValue;
	private FinReceiptHeader befImage;
	@XmlTransient
	private LoggedInUser userDetails;
	private String postBranch;
	private boolean logSchInPresentment;

	private boolean gDRAvailable = false;
	private String releaseType;
	private String thirdPartyName;
	private String thirdPartyMobileNum;
	private String transactionRef;
	private String promotionCode;
	private String productCategory;
	private Date nextRepayRvwDate;
	private long knockOffRefId = 0;

	// Upfront Fees
	private String finDivision;
	private String customerCIF;
	private String customerName;
	private String postBranchDesc;
	private String cashierBranchDesc;
	private String finDivisionDesc;
	private String entityCode;
	private String cancelRemarks;
	private String knockOffType;
	private String fromLanReference;
	private long fromLanFinId;

	private List<FinReceiptDetail> receiptDetails = new ArrayList<>(1);
	private List<FinExcessAmount> excessAmounts = new ArrayList<>(1);
	private List<FinExcessAmountReserve> excessReserves = new ArrayList<>(1);
	private List<ManualAdvise> payableAdvises = new ArrayList<>(1);
	private List<ManualAdvise> receivableAdvises = new ArrayList<>(1);
	private List<ManualAdviseReserve> payableReserves = new ArrayList<>(1);
	private List<ReceiptAllocationDetail> allocations = new ArrayList<>(1);
	private List<ReceiptAllocationDetail> allocationsSummary = new ArrayList<>(1);
	private ManualAdvise manualAdvise;
	private List<FinFeeDetail> paidFeeList = new ArrayList<FinFeeDetail>(1);
	private List<FinODDetails> finODDetails = new ArrayList<>(1);
	private List<ManualAdviseMovements> payablesMovements = new ArrayList<>(1);
	private List<FinExcessMovement> finExcessMovements = new ArrayList<>(1);

	private List<XcessPayables> xcessPayables = new ArrayList<>(1);
	private BigDecimal balAmount = BigDecimal.ZERO;
	private BigDecimal partPayAmount = BigDecimal.ZERO;
	private BigDecimal tds = BigDecimal.ZERO;
	private int schdIdx = 0;
	private int odIdx = 0;
	private int pftIdx = 0;
	private int futPftIdx = 0;
	private int tdsIdx = 0;
	private int futTdsIdx = 0;
	private int nPftIdx = 0;
	private int futNPftIdx = 0;
	private int priIdx = 0;
	private int futPriIdx = 0;
	private int lpiIdx = 0;
	private int lppIdx = 0;
	private int emiIdx = 0;
	private int ppIdx = 0;
	private boolean isPenalSeparate = false;
	private Date valueDate;
	private ReceiptAllocationDetail totalPastDues = new ReceiptAllocationDetail();
	private ReceiptAllocationDetail totalRcvAdvises = new ReceiptAllocationDetail();
	private ReceiptAllocationDetail totalXcess = new ReceiptAllocationDetail();
	private ReceiptAllocationDetail totalFees = new ReceiptAllocationDetail();
	private ReceiptAllocationDetail totalBounces = new ReceiptAllocationDetail();
	private boolean loanInActive = false;

	private long payAgainstId = 0;
	private long batchId;
	private long bounceId;
	private String custBaseCcy;
	private Long reasonCode;
	private List<DocumentDetails> documentDetails = new ArrayList<>(1);
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();
	private String prvReceiptPurpose;
	private Long partnerBankId;
	private String receiptSource;
	private Long linkedTranId;
	private BigDecimal refWaiverAmt = BigDecimal.ZERO; // default value 0
	private String source = "PLF";
	private Date recAppDate;
	private Date receivedDate; // Payment date
	private String bankCode;
	private Date presentmentSchDate;
	private boolean finTDSApplicable;
	private String sourceofFund;
	private BigDecimal tdsAmount = BigDecimal.ZERO;
	private String closureType;
	private boolean writeoffLoan;
	private boolean dedupCheckRequired = true;
	private String presentmentType;
	private boolean excldTdsCal = false;
	private BigDecimal bpiAmount = BigDecimal.ZERO; // Restructuring Purpose
	private ExtendedFieldExtension extendedFieldExtension = null;
	private ExtendedFieldHeader extendedFieldHeader = new ExtendedFieldHeader();
	private ExtendedFieldRender extendedFieldRender;
	private String finCategory;
	private BigDecimal recAdjAmt = BigDecimal.ZERO;
	private String toState;
	private String fromState;
	private boolean isClosureWithFullWaiver = false;
	private Map<String, BigDecimal> taxPercentages = new HashMap<>();
	private String moduleType;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	private String receiptSourceAcType;
	private String receiptSourceAcDesc;
	private String entityDesc;
	private String sourceId;
	private BigDecimal closureThresholdLimit = BigDecimal.ZERO;
	private String loanCancellationType;

	public FinReceiptHeader() {
		super();
	}

	public FinReceiptHeader(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("receiptDetails");
		excludeFields.add("excessAmounts");
		excludeFields.add("allocations");
		excludeFields.add("manualAdvise");
		excludeFields.add("finType");
		excludeFields.add("finCcy");
		excludeFields.add("finBranch");
		excludeFields.add("custID");
		excludeFields.add("custCIF");
		excludeFields.add("custShrtName");
		excludeFields.add("excessReserves");
		excludeFields.add("logSchInPresentment");
		excludeFields.add("finTypeDesc");
		excludeFields.add("finBranchDesc");
		excludeFields.add("finCcyDesc");
		excludeFields.add("cancelReasonDesc");
		excludeFields.add("finIsActive");
		excludeFields.add("scheduleMethod");
		excludeFields.add("pftDaysBasis");
		excludeFields.add("payableAdvises");
		excludeFields.add("payableReserves");
		excludeFields.add("transactionRef");
		excludeFields.add("promotionCode");
		excludeFields.add("productCategory");
		excludeFields.add("nextRepayRvwDate");
		excludeFields.add("xcessPayables");
		excludeFields.add("balAmount");
		excludeFields.add("tds");
		excludeFields.add("schdIdx");
		excludeFields.add("lpiIdx");
		excludeFields.add("lppIdx");
		excludeFields.add("isPenalSeparate");
		excludeFields.add("totalPastDues");
		excludeFields.add("totalAdvises");
		excludeFields.add("totalXcess");
		excludeFields.add("totalFees");
		excludeFields.add("partPayAmount");
		excludeFields.add("pftIdx");
		excludeFields.add("tdsIdx");
		excludeFields.add("nPftIdx");
		excludeFields.add("priIdx");
		excludeFields.add("finODDetails");
		excludeFields.add("odIdx");
		excludeFields.add("payablesMovements");
		excludeFields.add("finExcessMovements");
		excludeFields.add("receivableAdvises");
		excludeFields.add("repayHeader");
		excludeFields.add("emiIdx");
		excludeFields.add("futPftIdx");
		excludeFields.add("futTdsIdx");
		excludeFields.add("futNPftIdx");
		excludeFields.add("futPriIdx");
		excludeFields.add("subReceiptMode");
		excludeFields.add("payAgainstId");
		excludeFields.add("ppIdx");
		excludeFields.add("paymentType");
		excludeFields.add("feeTypeCode");
		excludeFields.add("knockOffRefId");
		excludeFields.add("repayHeader");
		excludeFields.add("loanInActive");
		excludeFields.add("customerCIF");
		excludeFields.add("customerName");
		excludeFields.add("postBranchDesc");
		excludeFields.add("cashierBranchDesc");
		excludeFields.add("finDivisionDesc");
		excludeFields.add("entityCode");
		excludeFields.add("collectionAgentDesc");
		excludeFields.add("collectionAgentCode");
		excludeFields.add("loanClosureCustCIF");
		excludeFields.add("loanClosureFinReference");
		excludeFields.add("loanClosureKnockOffFrom");
		excludeFields.add("loanClosureRefId");
		excludeFields.add("loanClosureReceiptDate");
		excludeFields.add("loanClosureIntTillDate");
		excludeFields.add("depositDate");
		excludeFields.add("cancelDate");
		excludeFields.add("partnerBankCode");
		excludeFields.add("waivedAmt");
		excludeFields.add("knockoffId");
		excludeFields.add("knockoffFrom");
		excludeFields.add("knockoffRefDate");
		excludeFields.add("knockoffAmount");
		excludeFields.add("slipNo");
		excludeFields.add("realizeStatus");
		excludeFields.add("bounceReason");
		excludeFields.add("realizeRemarks");
		excludeFields.add("allocationsSummary");
		excludeFields.add("totalRcvAdvises");
		excludeFields.add("totalBounces");
		excludeFields.add("cashierBranch");
		excludeFields.add("initiateDate");
		excludeFields.add("gDRAvailable");
		excludeFields.add("releaseType");
		excludeFields.add("thirdPartyName");
		excludeFields.add("thirdPartyMobileNum");
		excludeFields.add("batchId");
		excludeFields.add("bounceId");
		excludeFields.add("custBaseCcy");
		excludeFields.add("receiptSource");
		excludeFields.add("linkedTranId");
		excludeFields.add("presentmentSchDate");
		excludeFields.add("documentDetails");
		excludeFields.add("finTDSApplicable");
		excludeFields.add("entityDesc");
		excludeFields.add("receiptSourceAcType");
		excludeFields.add("receiptSourceAcDesc");
		excludeFields.add("sourceId");
		excludeFields.add("dedupCheckRequired");
		excludeFields.add("writeoffLoan");
		excludeFields.add("excldTdsCal");
		excludeFields.add("bpiAmount");
		excludeFields.add("presentmentType");
		excludeFields.add("auditDetailMap");
		excludeFields.add("extendedFieldExtension");
		excludeFields.add("extendedFieldHeader");
		excludeFields.add("extendedFieldRender");
		excludeFields.add("finCategory");
		excludeFields.add("toState");
		excludeFields.add("fromState");
		excludeFields.add("finType");
		excludeFields.add("isClosureWithFullWaiver");
		excludeFields.add("custAcctNumber");
		excludeFields.add("custAcctHolderName");
		excludeFields.add("taxPercentages");
		excludeFields.add("moduleType");
		excludeFields.add("closureThresholdLimit");
		excludeFields.add("fromLanReference");
		excludeFields.add("fromLanFinId");
		excludeFields.add("loanCancellationType");
		return excludeFields;
	}

	public FinReceiptHeader copyEntity() {
		FinReceiptHeader entity = new FinReceiptHeader();
		entity.setReceiptID(this.receiptID);
		entity.setReceiptDate(this.receiptDate);
		entity.setReceiptType(this.receiptType);
		entity.setRecAgainst(this.recAgainst);
		entity.setFinID(this.finID);
		entity.setReference(this.reference);
		entity.setReceiptPurpose(this.receiptPurpose);
		entity.setReceiptMode(this.receiptMode);
		entity.setExcessAdjustTo(this.excessAdjustTo);
		entity.setFinType(this.finType);
		entity.setFinTypeDesc(this.finTypeDesc);
		entity.setFinBranch(this.finBranch);
		entity.setFinBranchDesc(this.finBranchDesc);
		entity.setFinCcy(this.finCcy);
		entity.setFinCcyDesc(this.finCcyDesc);
		entity.setCustID(this.custID);
		entity.setCustCIF(this.custCIF);
		entity.setCustShrtName(this.custShrtName);
		entity.setAllocationType(this.allocationType);
		entity.setReceiptAmount(this.receiptAmount);
		entity.setEffectSchdMethod(this.effectSchdMethod);
		entity.setReceiptModeStatus(this.receiptModeStatus);
		entity.setRealizationDate(this.realizationDate);
		entity.setCancelReason(this.cancelReason);
		entity.setCancelReasonDesc(this.cancelReasonDesc);
		entity.setFinIsActive(this.finIsActive);
		entity.setScheduleMethod(this.scheduleMethod);
		entity.setPftDaysBasis(this.pftDaysBasis);
		entity.setWaviedAmt(this.waviedAmt);
		entity.setTotFeeAmount(this.totFeeAmount);
		entity.setBounceDate(this.bounceDate);
		entity.setRcdMaintainSts(this.rcdMaintainSts);
		entity.setCashierBranch(this.cashierBranch);
		entity.setInitiateDate(this.initiateDate);
		entity.setPartnerBankCode(this.partnerBankCode);
		entity.setKnockoffId(this.knockoffId);
		entity.setKnockoffFrom(this.knockoffFrom);
		entity.setKnockoffRefDate(this.knockoffRefDate);
		entity.setKnockoffAmount(this.knockoffAmount);
		entity.setSlipNo(this.slipNo);
		entity.setRealizeStatus(this.realizeStatus);
		entity.setBounceReason(this.bounceReason);
		entity.setRealizeRemarks(this.realizeRemarks);
		entity.setExtReference(this.extReference);
		entity.setModule(this.module);
		entity.setSubReceiptMode(this.subReceiptMode);
		entity.setReceiptChannel(this.receiptChannel);
		entity.setCollectionAgentId(this.collectionAgentId);
		entity.setCollectionAgentCode(this.collectionAgentCode);
		entity.setCollectionAgentDesc(this.collectionAgentDesc);
		entity.setReceivedFrom(this.receivedFrom);
		entity.setPanNumber(this.panNumber);
		entity.setPaymentType(this.paymentType);
		entity.setFeeTypeCode(this.feeTypeCode);
		entity.setActFinReceipt(this.actFinReceipt);
		entity.setLoanClosureCustCIF(this.loanClosureCustCIF);
		entity.setLoanClosureFinReference(this.loanClosureFinReference);
		entity.setLoanClosureKnockOffFrom(this.loanClosureKnockOffFrom);
		entity.setLoanClosureRefId(this.loanClosureRefId);
		entity.setLoanClosureReceiptDate(this.loanClosureReceiptDate);
		entity.setLoanClosureIntTillDate(this.loanClosureIntTillDate);
		entity.setDepositDate(this.depositDate);
		entity.setCancelDate(this.cancelDate);
		entity.setLovDescRequestStage(this.lovDescRequestStage);
		entity.setLpiAmount(this.lpiAmount);
		entity.setLppAmount(this.lppAmount);
		entity.setGstLpiAmount(this.gstLpiAmount);
		entity.setGstLppAmount(this.gstLppAmount);
		entity.setRemarks(this.remarks);
		entity.setDepositProcess(this.depositProcess);
		entity.setDepositBranch(this.depositBranch);
		entity.setNewRecord(super.isNewRecord());
		entity.setLovValue(this.lovValue);
		entity.setBefImage(this.befImage == null ? null : this.befImage.copyEntity());
		entity.setUserDetails(this.userDetails);
		entity.setPostBranch(this.postBranch);
		entity.setLogSchInPresentment(this.logSchInPresentment);
		entity.setGDRAvailable(this.gDRAvailable);
		entity.setReleaseType(this.releaseType);
		entity.setThirdPartyName(this.thirdPartyName);
		entity.setThirdPartyMobileNum(this.thirdPartyMobileNum);
		entity.setTransactionRef(this.transactionRef);
		entity.setPromotionCode(this.promotionCode);
		entity.setProductCategory(this.productCategory);
		entity.setNextRepayRvwDate(this.nextRepayRvwDate);
		entity.setKnockOffRefId(this.knockOffRefId);
		entity.setFinDivision(this.finDivision);
		entity.setCustomerCIF(this.customerCIF);
		entity.setCustomerName(this.customerName);
		entity.setPostBranchDesc(this.postBranchDesc);
		entity.setCashierBranchDesc(this.cashierBranchDesc);
		entity.setFinDivisionDesc(this.finDivisionDesc);
		entity.setEntityCode(this.entityCode);
		entity.setCancelRemarks(this.cancelRemarks);
		entity.setKnockOffType(this.knockOffType);
		entity.setDepositProcess(this.depositProcess);
		entity.setDepositBranch(this.depositBranch);
		entity.setExcldTdsCal(this.excldTdsCal);
		entity.setBpiAmount(this.bpiAmount);
		entity.setPresentmentType(this.presentmentType);
		entity.setExtendedFieldExtension(this.extendedFieldExtension);
		entity.setExtendedFieldHeader(this.extendedFieldHeader);
		entity.setExtendedFieldRender(this.extendedFieldRender);
		entity.setFinCategory(this.finCategory);
		this.receiptDetails.stream().forEach(e -> entity.getReceiptDetails().add(e == null ? null : e.copyEntity()));
		this.excessAmounts.stream().forEach(e -> entity.getExcessAmounts().add(e == null ? null : e.copyEntity()));
		this.excessReserves.stream().forEach(e -> entity.getExcessReserves().add(e == null ? null : e.copyEntity()));
		this.payableAdvises.stream().forEach(e -> entity.getPayableAdvises().add(e == null ? null : e.copyEntity()));
		this.receivableAdvises.stream()
				.forEach(e -> entity.getReceivableAdvises().add(e == null ? null : e.copyEntity()));
		this.payableReserves.stream().forEach(e -> entity.getPayableReserves().add(e == null ? null : e.copyEntity()));
		this.allocations.stream().forEach(e -> entity.getAllocations().add(e == null ? null : e.copyEntity()));
		this.allocationsSummary.stream()
				.forEach(e -> entity.getAllocationsSummary().add(e == null ? null : e.copyEntity()));
		entity.setManualAdvise(this.manualAdvise == null ? null : this.manualAdvise.copyEntity());
		this.paidFeeList.stream().forEach(e -> entity.getPaidFeeList().add(e == null ? null : e.copyEntity()));
		this.finODDetails.stream().forEach(e -> entity.getFinODDetails().add(e == null ? null : e.copyEntity()));
		this.payablesMovements.stream()
				.forEach(e -> entity.getPayablesMovements().add(e == null ? null : e.copyEntity()));
		this.finExcessMovements.stream()
				.forEach(e -> entity.getFinExcessMovements().add(e == null ? null : e.copyEntity()));
		this.xcessPayables.stream().forEach(e -> entity.getXcessPayables().add(e == null ? null : e.copyEntity()));
		entity.setBalAmount(this.balAmount);
		entity.setPartPayAmount(this.partPayAmount);
		entity.setTds(this.tds);
		entity.setSchdIdx(this.schdIdx);
		entity.setOdIdx(this.odIdx);
		entity.setPftIdx(this.pftIdx);
		entity.setFutPftIdx(this.futPftIdx);
		entity.setTdsIdx(this.tdsIdx);
		entity.setFutTdsIdx(this.futTdsIdx);
		entity.setNPftIdx(this.nPftIdx);
		entity.setFutNPftIdx(this.futNPftIdx);
		entity.setPriIdx(this.priIdx);
		entity.setFutPriIdx(this.futPriIdx);
		entity.setLpiIdx(this.lpiIdx);
		entity.setLppIdx(this.lppIdx);
		entity.setEmiIdx(this.emiIdx);
		entity.setPpIdx(this.ppIdx);
		entity.setPenalSeparate(this.isPenalSeparate);
		entity.setValueDate(this.valueDate);
		entity.setTotalPastDues(this.totalPastDues == null ? null : this.totalPastDues.copyEntity());
		entity.setTotalRcvAdvises(this.totalRcvAdvises == null ? null : this.totalRcvAdvises.copyEntity());
		entity.setTotalXcess(this.totalXcess == null ? null : this.totalXcess.copyEntity());
		entity.setTotalFees(this.totalFees == null ? null : this.totalFees.copyEntity());
		entity.setTotalBounces(this.totalBounces == null ? null : this.totalBounces.copyEntity());
		entity.setLoanInActive(this.loanInActive);
		entity.setPayAgainstId(this.payAgainstId);
		entity.setBatchId(this.batchId);
		entity.setBounceId(this.bounceId);
		entity.setCustBaseCcy(this.custBaseCcy);
		entity.setReasonCode(this.reasonCode);
		this.documentDetails.stream().forEach(e -> entity.getDocumentDetails().add(e == null ? null : e.copyEntity()));

		entity.setPrvReceiptPurpose(this.prvReceiptPurpose);
		entity.setPartnerBankId(this.partnerBankId);
		entity.setReceiptSource(this.receiptSource);
		entity.setLinkedTranId(this.linkedTranId);
		entity.setRefWaiverAmt(this.refWaiverAmt);
		entity.setSource(this.source);
		entity.setRecAppDate(this.recAppDate);
		entity.setReceivedDate(this.receivedDate);
		entity.setBankCode(this.bankCode);
		entity.setPresentmentSchDate(this.presentmentSchDate);
		entity.setFinTDSApplicable(this.finTDSApplicable);
		entity.setSourceofFund(this.sourceofFund);
		entity.setTdsAmount(this.tdsAmount);
		entity.setClosureType(this.closureType);
		entity.setDedupCheckRequired(this.dedupCheckRequired);
		entity.setWriteoffLoan(this.writeoffLoan);
		entity.setReceiptSourceAcType(this.receiptSourceAcType);
		entity.setReceiptSourceAcDesc(this.receiptSourceAcDesc);
		entity.setEntityDesc(this.entityDesc);
		entity.setSourceId(this.sourceId);
		entity.setRecordStatus(super.getRecordStatus());
		entity.setRoleCode(super.getRoleCode());
		entity.setNextRoleCode(super.getNextRoleCode());
		entity.setTaskId(super.getTaskId());
		entity.setNextTaskId(super.getNextTaskId());
		entity.setRecordType(super.getRecordType());
		entity.setWorkflowId(super.getWorkflowId());
		entity.setUserAction(super.getUserAction());
		entity.setVersion(super.getVersion());
		entity.setLastMntBy(super.getLastMntBy());
		entity.setLastMntOn(super.getLastMntOn());
		entity.setCustBankId(this.custBankId);
		entity.setCustAcctNumber(this.custAcctNumber);
		entity.setCustAcctHolderName(this.custAcctHolderName);
		entity.setTaxPercentages(this.taxPercentages);
		entity.setRecAdjAmt(this.recAdjAmt);
		entity.setToState(this.toState);
		entity.setFromState(this.fromState);
		entity.setClosureWithFullWaiver(this.isClosureWithFullWaiver);
		entity.setModuleType(this.moduleType);
		entity.setClosureThresholdLimit(this.closureThresholdLimit);
		entity.setLoanCancellationType(this.loanCancellationType);
		return entity;
	}

	public long getId() {
		return receiptID;
	}

	public void setId(long id) {
		this.receiptID = id;
	}

	public long getReceiptID() {
		return getId();
	}

	public void setReceiptID(long receiptID) {
		this.receiptID = receiptID;
	}

	public Date getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
	}

	public String getReceiptType() {
		return receiptType;
	}

	public void setReceiptType(String receiptType) {
		this.receiptType = receiptType;
	}

	public String getRecAgainst() {
		return recAgainst;
	}

	public void setRecAgainst(String recAgainst) {
		this.recAgainst = recAgainst;
	}

	public Long getFinID() {
		return finID;
	}

	public void setFinID(Long finID) {
		this.finID = finID;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getReceiptPurpose() {
		return receiptPurpose;
	}

	public void setReceiptPurpose(String receiptPurpose) {
		this.receiptPurpose = receiptPurpose;
	}

	public String getExcessAdjustTo() {
		return excessAdjustTo;
	}

	public void setExcessAdjustTo(String excessAdjustTo) {
		this.excessAdjustTo = excessAdjustTo;
	}

	public String getAllocationType() {
		return allocationType;
	}

	public void setAllocationType(String allocationType) {
		this.allocationType = allocationType;
	}

	public BigDecimal getReceiptAmount() {
		return receiptAmount;
	}

	public void setReceiptAmount(BigDecimal receiptAmount) {
		this.receiptAmount = receiptAmount;
	}

	public String getEffectSchdMethod() {
		return effectSchdMethod;
	}

	public void setEffectSchdMethod(String effectSchdMethod) {
		this.effectSchdMethod = effectSchdMethod;
	}

	public List<FinReceiptDetail> getReceiptDetails() {
		return receiptDetails;
	}

	public void setReceiptDetails(List<FinReceiptDetail> receiptDetails) {
		this.receiptDetails = receiptDetails;
	}

	public String getReceiptMode() {
		return receiptMode;
	}

	public void setReceiptMode(String receiptMode) {
		this.receiptMode = receiptMode;
	}

	public List<FinExcessAmount> getExcessAmounts() {
		return excessAmounts;
	}

	public void setExcessAmounts(List<FinExcessAmount> excessAmounts) {
		this.excessAmounts = excessAmounts;
	}

	public List<ReceiptAllocationDetail> getAllocations() {
		return allocations;
	}

	public void setAllocations(List<ReceiptAllocationDetail> allocations) {
		this.allocations = allocations;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public FinReceiptHeader getBefImage() {
		return this.befImage;
	}

	public void setBefImage(FinReceiptHeader beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public String getReceiptModeStatus() {
		return receiptModeStatus;
	}

	public void setReceiptModeStatus(String receiptModeStatus) {
		this.receiptModeStatus = receiptModeStatus;
	}

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

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public List<FinExcessAmountReserve> getExcessReserves() {
		return excessReserves;
	}

	public void setExcessReserves(List<FinExcessAmountReserve> excessReserves) {
		this.excessReserves = excessReserves;
	}

	public ManualAdvise getManualAdvise() {
		return manualAdvise;
	}

	public void setManualAdvise(ManualAdvise manualAdvise) {
		this.manualAdvise = manualAdvise;
	}

	public String getPostBranch() {
		return postBranch;
	}

	public void setPostBranch(String postBranch) {
		this.postBranch = postBranch;
	}

	public Date getRealizationDate() {
		return realizationDate;
	}

	public void setRealizationDate(Date realizationDate) {
		this.realizationDate = realizationDate;
	}

	public boolean isLogSchInPresentment() {
		return logSchInPresentment;
	}

	public void setLogSchInPresentment(boolean logSchInPresentment) {
		this.logSchInPresentment = logSchInPresentment;
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}

	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public String getFinBranchDesc() {
		return finBranchDesc;
	}

	public void setFinBranchDesc(String finBranchDesc) {
		this.finBranchDesc = finBranchDesc;
	}

	public String getFinCcyDesc() {
		return finCcyDesc;
	}

	public void setFinCcyDesc(String finCcyDesc) {
		this.finCcyDesc = finCcyDesc;
	}

	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	public String getCancelReasonDesc() {
		return cancelReasonDesc;
	}

	public void setCancelReasonDesc(String cancelReasonDesc) {
		this.cancelReasonDesc = cancelReasonDesc;
	}

	public boolean isFinIsActive() {
		return finIsActive;
	}

	public void setFinIsActive(boolean finIsActive) {
		this.finIsActive = finIsActive;
	}

	public List<FinFeeDetail> getPaidFeeList() {
		return paidFeeList;
	}

	public void setPaidFeeList(List<FinFeeDetail> paidFeeList) {
		this.paidFeeList = paidFeeList;
	}

	public String getPftDaysBasis() {
		return pftDaysBasis;
	}

	public void setPftDaysBasis(String pftDaysBasis) {
		this.pftDaysBasis = pftDaysBasis;
	}

	public String getScheduleMethod() {
		return scheduleMethod;
	}

	public void setScheduleMethod(String scheduleMethod) {
		this.scheduleMethod = scheduleMethod;
	}

	public BigDecimal getWaviedAmt() {
		return waviedAmt;
	}

	public BigDecimal getWaivedAmt() {
		return waviedAmt;
	}

	public void setWaviedAmt(BigDecimal waviedAmt) {
		this.waviedAmt = waviedAmt;
	}

	public List<ManualAdvise> getPayableAdvises() {
		return payableAdvises;
	}

	public void setPayableAdvises(List<ManualAdvise> payableAdvises) {
		this.payableAdvises = payableAdvises;
	}

	public List<ManualAdviseReserve> getPayableReserves() {
		return payableReserves;
	}

	public void setPayableReserves(List<ManualAdviseReserve> payableReserves) {
		this.payableReserves = payableReserves;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public BigDecimal getTotFeeAmount() {
		return totFeeAmount;
	}

	public void setTotFeeAmount(BigDecimal totFeeAmount) {
		this.totFeeAmount = totFeeAmount;
	}

	public Date getBounceDate() {
		return bounceDate;
	}

	public void setBounceDate(Date bounceDate) {
		this.bounceDate = bounceDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getRcdMaintainSts() {
		return rcdMaintainSts;
	}

	public void setRcdMaintainSts(String rcdMaintainSts) {
		this.rcdMaintainSts = rcdMaintainSts;
	}

	public boolean isGDRAvailable() {
		return gDRAvailable;
	}

	public void setGDRAvailable(boolean gDRAvailable) {
		this.gDRAvailable = gDRAvailable;
	}

	public String getReleaseType() {
		return releaseType;
	}

	public void setReleaseType(String releaseType) {
		this.releaseType = releaseType;
	}

	public String getThirdPartyName() {
		return thirdPartyName;
	}

	public void setThirdPartyName(String thirdPartyName) {
		this.thirdPartyName = thirdPartyName;
	}

	public boolean isDepositProcess() {
		return depositProcess;
	}

	public void setDepositProcess(boolean depositProcess) {
		this.depositProcess = depositProcess;
	}

	public String getDepositBranch() {
		return depositBranch;
	}

	public void setDepositBranch(String depositBranch) {
		this.depositBranch = depositBranch;
	}

	public BigDecimal getLppAmount() {
		return lppAmount;
	}

	public void setLppAmount(BigDecimal lppAmount) {
		this.lppAmount = lppAmount;
	}

	public BigDecimal getGstLpiAmount() {
		return gstLpiAmount;
	}

	public void setGstLpiAmount(BigDecimal gstLpiAmount) {
		this.gstLpiAmount = gstLpiAmount;
	}

	public BigDecimal getGstLppAmount() {
		return gstLppAmount;
	}

	public void setGstLppAmount(BigDecimal gstLppAmount) {
		this.gstLppAmount = gstLppAmount;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getExtReference() {
		return extReference;
	}

	public void setExtReference(String extReference) {
		this.extReference = extReference;
	}

	public String getThirdPartyMobileNum() {
		return thirdPartyMobileNum;
	}

	public void setThirdPartyMobileNum(String thirdPartyMobileNum) {
		this.thirdPartyMobileNum = thirdPartyMobileNum;
	}

	public String getTransactionRef() {
		return transactionRef;
	}

	public void setTransactionRef(String transactionRef) {
		this.transactionRef = transactionRef;
	}

	public String getPromotionCode() {
		return promotionCode;
	}

	public void setPromotionCode(String promotionCode) {
		this.promotionCode = promotionCode;
	}

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public Date getNextRepayRvwDate() {
		return nextRepayRvwDate;
	}

	public void setNextRepayRvwDate(Date nextRepayRvwDate) {
		this.nextRepayRvwDate = nextRepayRvwDate;
	}

	public String getCashierBranch() {
		return cashierBranch;
	}

	public void setCashierBranch(String cashierBranch) {
		this.cashierBranch = cashierBranch;
	}

	public Date getInitiateDate() {
		return initiateDate;
	}

	public void setInitiateDate(Date initiateDate) {
		this.initiateDate = initiateDate;
	}

	public BigDecimal getLpiAmount() {
		return lpiAmount;
	}

	public void setLpiAmount(BigDecimal lpiAmount) {
		this.lpiAmount = lpiAmount;
	}

	public List<XcessPayables> getXcessPayables() {
		return xcessPayables;
	}

	public void setXcessPayables(List<XcessPayables> xcessPayables) {
		this.xcessPayables = xcessPayables;
	}

	public BigDecimal getBalAmount() {
		return balAmount;
	}

	public void setBalAmount(BigDecimal balAmount) {
		this.balAmount = balAmount;
	}

	public int getSchdIdx() {
		return schdIdx;
	}

	public void setSchdIdx(int schdIdx) {
		this.schdIdx = schdIdx;
	}

	public boolean isPenalSeparate() {
		return isPenalSeparate;
	}

	public void setPenalSeparate(boolean isPenalSeparate) {
		this.isPenalSeparate = isPenalSeparate;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public BigDecimal getTds() {
		return tds;
	}

	public void setTds(BigDecimal tds) {
		this.tds = tds;
	}

	public int getLpiIdx() {
		return lpiIdx;
	}

	public void setLpiIdx(int lpiIdx) {
		this.lpiIdx = lpiIdx;
	}

	public int getLppIdx() {
		return lppIdx;
	}

	public void setLppIdx(int lppIdx) {
		this.lppIdx = lppIdx;
	}

	public List<ManualAdvise> getReceivableAdvises() {
		return receivableAdvises;
	}

	public void setReceivableAdvises(List<ManualAdvise> receivableAdvises) {
		this.receivableAdvises = receivableAdvises;
	}

	public ReceiptAllocationDetail getTotalPastDues() {
		return totalPastDues;
	}

	public void setTotalPastDues(ReceiptAllocationDetail totalPastDues) {
		this.totalPastDues = totalPastDues;
	}

	public ReceiptAllocationDetail getTotalRcvAdvises() {
		return totalRcvAdvises;
	}

	public void setTotalRcvAdvises(ReceiptAllocationDetail totalRcvAdvises) {
		this.totalRcvAdvises = totalRcvAdvises;
	}

	public ReceiptAllocationDetail getTotalXcess() {
		return totalXcess;
	}

	public void setTotalXcess(ReceiptAllocationDetail totalXcess) {
		this.totalXcess = totalXcess;
	}

	public ReceiptAllocationDetail getTotalFees() {
		return totalFees;
	}

	public void setTotalFees(ReceiptAllocationDetail totalFees) {
		this.totalFees = totalFees;
	}

	public BigDecimal getPartPayAmount() {
		return partPayAmount;
	}

	public void setPartPayAmount(BigDecimal partPayAmount) {
		this.partPayAmount = partPayAmount;
	}

	public int getPftIdx() {
		return pftIdx;
	}

	public void setPftIdx(int pftIdx) {
		this.pftIdx = pftIdx;
	}

	public int getTdsIdx() {
		return tdsIdx;
	}

	public void setTdsIdx(int tdsIdx) {
		this.tdsIdx = tdsIdx;
	}

	public int getNPftIdx() {
		return nPftIdx;
	}

	public void setNPftIdx(int nPftIdx) {
		this.nPftIdx = nPftIdx;
	}

	public int getPriIdx() {
		return priIdx;
	}

	public void setPriIdx(int priIdx) {
		this.priIdx = priIdx;
	}

	public List<FinODDetails> getFinODDetails() {
		return finODDetails;
	}

	public void setFinODDetails(List<FinODDetails> finODDetails) {
		this.finODDetails = finODDetails;
	}

	public List<ManualAdviseMovements> getPayablesMovements() {
		return payablesMovements;
	}

	public void setPayablesMovements(List<ManualAdviseMovements> payablesMovements) {
		this.payablesMovements = payablesMovements;
	}

	public List<FinExcessMovement> getFinExcessMovements() {
		return finExcessMovements;
	}

	public void setFinExcessMovements(List<FinExcessMovement> finExcessMovements) {
		this.finExcessMovements = finExcessMovements;
	}

	public int getOdIdx() {
		return odIdx;
	}

	public void setOdIdx(int odIdx) {
		this.odIdx = odIdx;
	}

	public int getEmiIdx() {
		return emiIdx;
	}

	public void setEmiIdx(int emiIdx) {
		this.emiIdx = emiIdx;
	}

	public int getFutPftIdx() {
		return futPftIdx;
	}

	public void setFutPftIdx(int futPftIdx) {
		this.futPftIdx = futPftIdx;
	}

	public int getFutTdsIdx() {
		return futTdsIdx;
	}

	public void setFutTdsIdx(int futTdsIdx) {
		this.futTdsIdx = futTdsIdx;
	}

	public int getFutNPftIdx() {
		return futNPftIdx;
	}

	public void setFutNPftIdx(int futNPftIdx) {
		this.futNPftIdx = futNPftIdx;
	}

	public int getFutPriIdx() {
		return futPriIdx;
	}

	public void setFutPriIdx(int futPriIdx) {
		this.futPriIdx = futPriIdx;
	}

	public Date getDepositDate() {
		return depositDate;
	}

	public void setDepositDate(Date depositDate) {
		this.depositDate = depositDate;
	}

	public Date getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}

	public String getLovDescRequestStage() {
		return lovDescRequestStage;
	}

	public void setLovDescRequestStage(String lovDescRequestStage) {
		this.lovDescRequestStage = lovDescRequestStage;
	}

	public String getReceiptChannel() {
		return receiptChannel == null ? "" : receiptChannel;
	}

	public void setReceiptChannel(String receiptChannel) {
		this.receiptChannel = receiptChannel;
	}

	public long getCollectionAgentId() {
		return collectionAgentId;
	}

	public void setCollectionAgentId(long collectionAgentId) {
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

	public String getKnockoffId() {
		return knockoffId;
	}

	public void setKnockoffId(String knockoffId) {
		this.knockoffId = knockoffId;
	}

	public String getKnockoffFrom() {
		return knockoffFrom;
	}

	public void setKnockoffFrom(String knockoffFrom) {
		this.knockoffFrom = knockoffFrom;
	}

	public Date getKnockoffRefDate() {
		return knockoffRefDate;
	}

	public void setKnockoffRefDate(Date knockoffRefDate) {
		this.knockoffRefDate = knockoffRefDate;
	}

	public String getKnockoffAmount() {
		return knockoffAmount;
	}

	public void setKnockoffAmount(String knockoffAmount) {
		this.knockoffAmount = knockoffAmount;
	}

	public String getSlipNo() {
		return slipNo;
	}

	public void setSlipNo(String slipNo) {
		this.slipNo = slipNo;
	}

	public String getRealizeStatus() {
		return realizeStatus;
	}

	public void setRealizeStatus(String realizeStatus) {
		this.realizeStatus = realizeStatus;
	}

	public long getBounceReason() {
		return bounceReason;
	}

	public void setBounceReason(long bounceReason) {
		this.bounceReason = bounceReason;
	}

	public String getSubReceiptMode() {
		return subReceiptMode;
	}

	public void setSubReceiptMode(String subReceiptMode) {
		this.subReceiptMode = subReceiptMode;
	}

	public int getPpIdx() {
		return ppIdx;
	}

	public void setPpIdx(int ppIdx) {
		this.ppIdx = ppIdx;
	}

	public String getRealizeRemarks() {
		return realizeRemarks;
	}

	public void setRealizeRemarks(String realizeRemarks) {
		this.realizeRemarks = realizeRemarks;
	}

	public ReceiptAllocationDetail getTotalBounces() {
		return totalBounces;
	}

	public void setTotalBounces(ReceiptAllocationDetail totalBounces) {
		this.totalBounces = totalBounces;
	}

	public long getPayAgainstId() {
		return payAgainstId;
	}

	public void setPayAgainstId(long payAgainstId) {
		this.payAgainstId = payAgainstId;
	}

	public String getPartnerBankCode() {
		return partnerBankCode;
	}

	public void setPartnerBankCode(String partnerBankCode) {
		this.partnerBankCode = partnerBankCode;
	}

	public List<ReceiptAllocationDetail> getAllocationsSummary() {
		return allocationsSummary;
	}

	public void setAllocationsSummary(List<ReceiptAllocationDetail> allocationsSummary) {
		this.allocationsSummary = allocationsSummary;
	}

	public String getLoanClosureCustCIF() {
		return loanClosureCustCIF;
	}

	public void setLoanClosureCustCIF(String loanClosureCustCIF) {
		this.loanClosureCustCIF = loanClosureCustCIF;
	}

	public String getLoanClosureFinReference() {
		return loanClosureFinReference;
	}

	public void setLoanClosureFinReference(String loanClosureFinReference) {
		this.loanClosureFinReference = loanClosureFinReference;
	}

	public String getLoanClosureKnockOffFrom() {
		return loanClosureKnockOffFrom;
	}

	public void setLoanClosureKnockOffFrom(String loanClosureKnockOffFrom) {
		this.loanClosureKnockOffFrom = loanClosureKnockOffFrom;
	}

	public String getLoanClosureRefId() {
		return loanClosureRefId;
	}

	public void setLoanClosureRefId(String loanClosureRefId) {
		this.loanClosureRefId = loanClosureRefId;
	}

	public Date getLoanClosureReceiptDate() {
		return loanClosureReceiptDate;
	}

	public void setLoanClosureReceiptDate(Date loanClosureReceiptDate) {
		this.loanClosureReceiptDate = loanClosureReceiptDate;
	}

	public Date getLoanClosureIntTillDate() {
		return loanClosureIntTillDate;
	}

	public void setLoanClosureIntTillDate(Date loanClosureIntTillDate) {
		this.loanClosureIntTillDate = loanClosureIntTillDate;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getFeeTypeCode() {
		return feeTypeCode;
	}

	public void setFeeTypeCode(String feeTypeCode) {
		this.feeTypeCode = feeTypeCode;
	}

	public long getKnockOffRefId() {
		return knockOffRefId;
	}

	public void setKnockOffRefId(long knockOffRefId) {
		this.knockOffRefId = knockOffRefId;
	}

	/*
	 * public FinRepayHeader getRepayHeader() { return repayHeader; }
	 * 
	 * public void setRepayHeader(FinRepayHeader repayHeader) { this.repayHeader = repayHeader; }
	 */

	public String getCollectionAgentCode() {
		return collectionAgentCode;
	}

	public void setCollectionAgentCode(String collectionAgentCode) {
		this.collectionAgentCode = collectionAgentCode;
	}

	public String getCollectionAgentDesc() {
		return collectionAgentDesc;
	}

	public void setCollectionAgentDesc(String collectionAgentDesc) {
		this.collectionAgentDesc = collectionAgentDesc;
	}

	public String getCustomerCIF() {
		return customerCIF;
	}

	public void setCustomerCIF(String customerCIF) {
		this.customerCIF = customerCIF;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getFinDivision() {
		return finDivision;
	}

	public void setFinDivision(String finDivision) {
		this.finDivision = finDivision;
	}

	public String getCashierBranchDesc() {
		return cashierBranchDesc;
	}

	public void setCashierBranchDesc(String cashierBranchDesc) {
		this.cashierBranchDesc = cashierBranchDesc;
	}

	public String getPostBranchDesc() {
		return postBranchDesc;
	}

	public void setPostBranchDesc(String postBranchDesc) {
		this.postBranchDesc = postBranchDesc;
	}

	public String getFinDivisionDesc() {
		return finDivisionDesc;
	}

	public void setFinDivisionDesc(String finDivisionDesc) {
		this.finDivisionDesc = finDivisionDesc;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public boolean isLoanInActive() {
		return loanInActive;
	}

	public void setLoanInActive(boolean loanInActive) {
		this.loanInActive = loanInActive;
	}

	public long getBatchId() {
		return batchId;
	}

	public void setBatchId(long batchId) {
		this.batchId = batchId;
	}

	public boolean isActFinReceipt() {
		return actFinReceipt;
	}

	public void setActFinReceipt(boolean actFinReceipt) {
		this.actFinReceipt = actFinReceipt;
	}

	public long getBounceId() {
		return bounceId;
	}

	public void setBounceId(long bounceId) {
		this.bounceId = bounceId;
	}

	public String getCustBaseCcy() {
		return custBaseCcy;
	}

	public void setCustBaseCcy(String custBaseCcy) {
		this.custBaseCcy = custBaseCcy;
	}

	public Long getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(Long reasonCode) {
		this.reasonCode = reasonCode;
	}

	public List<DocumentDetails> getDocumentDetails() {
		return documentDetails;
	}

	public void setDocumentDetails(List<DocumentDetails> documentDetails) {
		this.documentDetails = documentDetails;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public String getPrvReceiptPurpose() {
		return prvReceiptPurpose;
	}

	public void setPrvReceiptPurpose(String prvReceiptPurpose) {
		this.prvReceiptPurpose = prvReceiptPurpose;
	}

	public Long getPartnerBankId() {
		return partnerBankId;
	}

	public void setPartnerBankId(Long partnerBankId) {
		this.partnerBankId = partnerBankId;
	}

	public String getCancelRemarks() {
		return cancelRemarks;
	}

	public void setCancelRemarks(String cancelRemarks) {
		this.cancelRemarks = cancelRemarks;
	}

	public String getKnockOffType() {
		return knockOffType;
	}

	public void setKnockOffType(String knockOffType) {
		this.knockOffType = knockOffType;
	}

	public String getReceiptSource() {
		return receiptSource;
	}

	public void setReceiptSource(String receiptSource) {
		this.receiptSource = receiptSource;
	}

	public Long getLinkedTranId() {
		return linkedTranId;
	}

	public void setLinkedTranId(Long linkedTranId) {
		this.linkedTranId = linkedTranId;
	}

	public BigDecimal getRefWaiverAmt() {
		return refWaiverAmt;
	}

	public void setRefWaiverAmt(BigDecimal refWaiverAmt) {
		this.refWaiverAmt = refWaiverAmt;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Date getRecAppDate() {
		return recAppDate;
	}

	public void setRecAppDate(Date recAppDate) {
		this.recAppDate = recAppDate;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public Date getPresentmentSchDate() {
		return presentmentSchDate;
	}

	public void setPresentmentSchDate(Date presentmentSchDate) {
		this.presentmentSchDate = presentmentSchDate;
	}

	public boolean isFinTDSApplicable() {
		return finTDSApplicable;
	}

	public void setFinTDSApplicable(boolean finTDSApplicable) {
		this.finTDSApplicable = finTDSApplicable;
	}

	public String getSourceofFund() {
		return sourceofFund;
	}

	public void setSourceofFund(String sourceofFund) {
		this.sourceofFund = sourceofFund;
	}

	public BigDecimal getTdsAmount() {
		return tdsAmount;
	}

	public void setTdsAmount(BigDecimal tdsAmount) {
		this.tdsAmount = tdsAmount;
	}

	public String getClosureType() {
		return closureType;
	}

	public void setClosureType(String closureType) {
		this.closureType = closureType;
	}

	public String getEntityDesc() {
		return entityDesc;
	}

	public void setEntityDesc(String entityDesc) {
		this.entityDesc = entityDesc;
	}

	public String getReceiptSourceAcType() {
		return receiptSourceAcType;
	}

	public void setReceiptSourceAcType(String receiptSourceAcType) {
		this.receiptSourceAcType = receiptSourceAcType;
	}

	public String getReceiptSourceAcDesc() {
		return receiptSourceAcDesc;
	}

	public void setReceiptSourceAcDesc(String receiptSourceAcDesc) {
		this.receiptSourceAcDesc = receiptSourceAcDesc;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public boolean isDedupCheckRequired() {
		return dedupCheckRequired;
	}

	public void setDedupCheckRequired(boolean dedupCheckRequired) {
		this.dedupCheckRequired = dedupCheckRequired;
	}

	public boolean isWriteoffLoan() {
		return writeoffLoan;
	}

	public void setWriteoffLoan(boolean writeoffLoan) {
		this.writeoffLoan = writeoffLoan;
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

	public String getPresentmentType() {
		return presentmentType;
	}

	public void setPresentmentType(String presentmentType) {
		this.presentmentType = presentmentType;
	}

	public ExtendedFieldExtension getExtendedFieldExtension() {
		return extendedFieldExtension;
	}

	public void setExtendedFieldExtension(ExtendedFieldExtension extendedFieldExtension) {
		this.extendedFieldExtension = extendedFieldExtension;
	}

	public ExtendedFieldHeader getExtendedFieldHeader() {
		return extendedFieldHeader;
	}

	public void setExtendedFieldHeader(ExtendedFieldHeader extendedFieldHeader) {
		this.extendedFieldHeader = extendedFieldHeader;
	}

	public ExtendedFieldRender getExtendedFieldRender() {
		return extendedFieldRender;
	}

	public void setExtendedFieldRender(ExtendedFieldRender extendedFieldRender) {
		this.extendedFieldRender = extendedFieldRender;
	}

	public String getFinCategory() {
		return finCategory;
	}

	public void setFinCategory(String finCategory) {
		this.finCategory = finCategory;
	}

	public BigDecimal getRecAdjAmt() {
		return recAdjAmt;
	}

	public void setRecAdjAmt(BigDecimal recAdjAmt) {
		this.recAdjAmt = recAdjAmt;
	}

	public String getToState() {
		return toState;
	}

	public void setToState(String toState) {
		this.toState = toState;
	}

	public String getFromState() {
		return fromState;
	}

	public void setFromState(String fromState) {
		this.fromState = fromState;
	}

	public boolean isClosureWithFullWaiver() {
		return isClosureWithFullWaiver;
	}

	public void setClosureWithFullWaiver(boolean isClosureWithFullWaiver) {
		this.isClosureWithFullWaiver = isClosureWithFullWaiver;
	}

	public String getCustAcctNumber() {
		return custAcctNumber;
	}

	public void setCustAcctNumber(String custAcctNumber) {
		this.custAcctNumber = custAcctNumber;
	}

	public String getCustAcctHolderName() {
		return custAcctHolderName;
	}

	public void setCustAcctHolderName(String custAcctHolderName) {
		this.custAcctHolderName = custAcctHolderName;
	}

	public Long getCustBankId() {
		return custBankId;
	}

	public void setCustBankId(Long custBankId) {
		this.custBankId = custBankId;
	}

	public Map<String, BigDecimal> getTaxPercentages() {
		return taxPercentages;
	}

	public void setTaxPercentages(Map<String, BigDecimal> taxPercentages) {
		this.taxPercentages = taxPercentages;
	}

	public String getModuleType() {
		return moduleType;
	}

	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}

	public BigDecimal getClosureThresholdLimit() {
		return closureThresholdLimit;
	}

	public void setClosureThresholdLimit(BigDecimal closureThresholdLimit) {
		this.closureThresholdLimit = closureThresholdLimit;
	}

	public String getFromLanReference() {
		return fromLanReference;
	}

	public void setFromLanReference(String fromLanReference) {
		this.fromLanReference = fromLanReference;
	}

	public long getFromLanFinId() {
		return fromLanFinId;
	}

	public void setFromLanFinId(long fromLanFinId) {
		this.fromLanFinId = fromLanFinId;
	}

	public String getLoanCancellationType() {
		return loanCancellationType;
	}

	public void setLoanCancellationType(String loanCancellationType) {
		this.loanCancellationType = loanCancellationType;
	}
}
