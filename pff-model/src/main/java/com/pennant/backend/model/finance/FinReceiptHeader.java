package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class FinReceiptHeader extends AbstractWorkflowEntity implements Entity {

	private static final long serialVersionUID = -58727889587717168L;

	private long receiptID = 0;// Auto Generated Sequence
	private Date receiptDate;
	private String receiptType;
	private String recAgainst;
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
	private String bounceReason;
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

	private Date depositeDate;
	private String depositBank;
	private Date cancelDate;
	private String lovDescRequestStage;

	private BigDecimal lpiAmount = BigDecimal.ZERO;
	private BigDecimal lppAmount = BigDecimal.ZERO;
	private BigDecimal gstLpiAmount = BigDecimal.ZERO;
	private BigDecimal gstLppAmount = BigDecimal.ZERO;

	private String remarks;
	private boolean depositProcess = false; // added for Cash Management 
	private String depositBranch; // added for Cash Management 
	private boolean newRecord;
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

	//Upfront Fees
	private String finDivision;
	private String customerCIF;
	private String customerName;
	private String postBranchDesc;
	private String cashierBranchDesc;
	private String finDivisionDesc;
	private String entityCode;

	private List<FinReceiptDetail> receiptDetails = new ArrayList<>(1);
	private List<FinExcessAmount> excessAmounts = new ArrayList<>(1);
	private List<FinExcessAmountReserve> excessReserves = new ArrayList<>(1);
	private List<ManualAdvise> payableAdvises = new ArrayList<>(1);
	private List<ManualAdvise> receivableAdvises = new ArrayList<>(1);
	private List<ManualAdviseReserve> payableReserves = new ArrayList<>(1);
	private List<ReceiptAllocationDetail> allocations = new ArrayList<>(1);
	private List<ReceiptAllocationDetail> allocationsSummary = new ArrayList<>(1);
	private ManualAdvise manualAdvise; // Bounce Reason
	private List<FinFeeDetail> paidFeeList; // Paid Fee Detail List for Fee Receipt
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

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
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
		excludeFields.add("valueDate");
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
		excludeFields.add("depositeDate");
		excludeFields.add("depositBank");
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

		return excludeFields;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public FinReceiptHeader() {
		super();
	}

	public FinReceiptHeader(long id) {
		super();
		this.setId(id);
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

	public Date getDepositeDate() {
		return depositeDate;
	}

	public void setDepositeDate(Date depositeDate) {
		this.depositeDate = depositeDate;
	}

	public String getDepositBank() {
		return depositBank;
	}

	public void setDepositBank(String depositBank) {
		this.depositBank = depositBank;
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

	public String getBounceReason() {
		return bounceReason;
	}

	public void setBounceReason(String bounceReason) {
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

}
