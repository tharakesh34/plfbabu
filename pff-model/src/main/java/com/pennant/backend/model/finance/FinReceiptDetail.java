package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class FinReceiptDetail implements Serializable {
	private static final long serialVersionUID = 1L;
	private long receiptID = 0; // Only setting from Receipt Header
	private long receiptSeqID = 0; // Auto Generated
	private String receiptType;
	private String paymentTo;
	private String paymentType;
	private long payAgainstID = 0;
	private int payOrder = 0;
	private BigDecimal amount = BigDecimal.ZERO;
	@XmlElement
	private String favourNumber;
	@XmlElement
	private Date valueDate;
	@XmlElement
	private String bankCode;
	private String bankCodeDesc;
	// Bankbranch ID for IMD
	private long bankBranchID;
	// IFSC for IMD API
	@XmlElement(name = "ifsc")
	private String iFSC;
	private String branchDesc;
	@XmlElement
	private String favourName;
	@XmlElement
	private Date depositDate;
	private String depositNo;
	@XmlElement
	private String paymentRef;
	@XmlElement
	private String transactionRef;
	@XmlElement
	private String chequeAcNo;
	@XmlElement(name = "fundingAccount")
	private Long fundingAc = 0L;
	private String fundingAcCode;
	private String fundingAcDesc;
	@XmlElement
	private Date receivedDate;
	private String status;
	// API Specific
	@XmlElement
	private String remarks;
	private long logKey = 0;
	private boolean delRecord = false;
	private String partnerBankAc;
	private String partnerBankAcType;
	private Long finID;
	private String reference; // only for Fees
	private String feeTypeCode;
	private String feeTypeDesc;
	private boolean noReserve;
	private boolean noManualReserve;
	private String receiptPurpose;
	private ManualAdviseMovements payAdvMovement;
	private List<FinRepayHeader> repayHeaders = new ArrayList<FinRepayHeader>(1);
	private FinRepayHeader repayHeader = new FinRepayHeader();
	private List<ManualAdviseMovements> advMovements = new ArrayList<ManualAdviseMovements>(1);
	private BigDecimal partialPaidAMount = BigDecimal.ZERO;
	private BigDecimal dueAmount = BigDecimal.ZERO;
	@XmlElement
	private String bankBranchCode;
	private Date realizationDate;
	@XmlElement
	private String micr;

	public Map<String, Object> getDeclaredFieldValues() {
		Map<String, Object> receiptDetailMap = new HashMap<>();
		getDeclaredFieldValues(receiptDetailMap);
		return receiptDetailMap;
	}

	public void getDeclaredFieldValues(Map<String, Object> receiptDetailMap) {
		for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
			try {
				// "rd_" Should be in small case only, if we want to change the
				// case we need to update the configuration fields as well.
				receiptDetailMap.put("rd_" + this.getClass().getDeclaredFields()[i].getName(),
						this.getClass().getDeclaredFields()[i].get(this));
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				// Nothing TO DO
			}
		}
	}

	public FinReceiptDetail() {
	    super();
	}

	public FinReceiptDetail copyEntity() {
		FinReceiptDetail entity = new FinReceiptDetail();
		entity.setReceiptID(this.receiptID);
		entity.setReceiptSeqID(this.receiptSeqID);
		entity.setReceiptType(this.receiptType);
		entity.setPaymentTo(this.paymentTo);
		entity.setPaymentType(this.paymentType);
		entity.setPayAgainstID(this.payAgainstID);
		entity.setPayOrder(this.payOrder);
		entity.setAmount(this.amount);
		entity.setFavourNumber(this.favourNumber);
		entity.setValueDate(this.valueDate);
		entity.setBankCode(this.bankCode);
		entity.setBankCodeDesc(this.bankCodeDesc);
		entity.setBankBranchID(this.bankBranchID);
		entity.setiFSC(this.iFSC);
		entity.setBranchDesc(this.branchDesc);
		entity.setFavourName(this.favourName);
		entity.setDepositDate(this.depositDate);
		entity.setDepositNo(this.depositNo);
		entity.setPaymentRef(this.paymentRef);
		entity.setTransactionRef(this.transactionRef);
		entity.setChequeAcNo(this.chequeAcNo);
		entity.setFundingAc(this.fundingAc);
		entity.setFundingAcCode(this.fundingAcCode);
		entity.setFundingAcDesc(this.fundingAcDesc);
		entity.setReceivedDate(this.receivedDate);
		entity.setStatus(this.status);
		entity.setRemarks(this.remarks);
		entity.setLogKey(this.logKey);
		entity.setDelRecord(this.delRecord);
		entity.setPartnerBankAc(this.partnerBankAc);
		entity.setPartnerBankAcType(this.partnerBankAcType);
		entity.setReference(this.reference);
		entity.setFinID(this.finID);
		entity.setFeeTypeCode(this.feeTypeCode);
		entity.setFeeTypeDesc(this.feeTypeDesc);
		entity.setNoReserve(this.noReserve);
		entity.setNoManualReserve(this.noManualReserve);
		entity.setReceiptPurpose(this.receiptPurpose);
		entity.setPayAdvMovement(this.payAdvMovement == null ? null : this.payAdvMovement.copyEntity());
		this.repayHeaders.stream().forEach(e -> entity.getRepayHeaders().add(e == null ? null : e.copyEntity()));
		entity.setRepayHeader(this.repayHeader == null ? null : this.repayHeader.copyEntity());
		this.advMovements.stream().forEach(e -> entity.getAdvMovements().add(e == null ? null : e.copyEntity()));
		entity.setPartialPaidAMount(this.partialPaidAMount);
		entity.setDueAmount(this.dueAmount);
		entity.setBankBranchCode(this.bankBranchCode);
		entity.setRealizationDate(this.realizationDate);
		entity.setMicr(this.micr);

		return entity;
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("noReserve");
		excludeFields.add("receiptPurpose");
		excludeFields.add("bankCodeDesc");
		excludeFields.add("delRecord");
		excludeFields.add("feeTypeCode");
		excludeFields.add("feeTypeDesc");
		excludeFields.add("fundingAcCode");
		excludeFields.add("fundingAcDesc");
		excludeFields.add("logKey");
		excludeFields.add("partialPaidAMount");
		excludeFields.add("partnerBankAc");
		excludeFields.add("partnerBankAcType");
		excludeFields.add("finID");
		excludeFields.add("reference");
		excludeFields.add("receiptTaxDetail");
		excludeFields.add("repayHeaders");
		excludeFields.add("repayHeader");
		excludeFields.add("advMovements");
		excludeFields.add("dueAmount");
		excludeFields.add("payOrder");
		excludeFields.add("payAdvMovement");
		excludeFields.add("iFSC");
		excludeFields.add("branchDesc");
		excludeFields.add("noManualReserve");
		excludeFields.add("bankBranchCode");
		excludeFields.add("realizationDate");
		excludeFields.add("micr");
		return excludeFields;
	}

	public long getId() {
		return receiptSeqID;
	}

	public void setId(long id) {
		this.receiptSeqID = id;
	}

	public long getReceiptID() {
		return receiptID;
	}

	public void setReceiptID(long receiptID) {
		this.receiptID = receiptID;
	}

	public long getReceiptSeqID() {
		return receiptSeqID;
	}

	public void setReceiptSeqID(long receiptSeqID) {
		this.receiptSeqID = receiptSeqID;
	}

	public String getReceiptType() {
		return receiptType;
	}

	public void setReceiptType(String receiptType) {
		this.receiptType = receiptType;
	}

	public String getPaymentTo() {
		return paymentTo;
	}

	public void setPaymentTo(String paymentTo) {
		this.paymentTo = paymentTo;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public long getPayAgainstID() {
		return payAgainstID;
	}

	public void setPayAgainstID(long payAgainstID) {
		this.payAgainstID = payAgainstID;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getFavourNumber() {
		return favourNumber;
	}

	public void setFavourNumber(String favourNumber) {
		this.favourNumber = favourNumber;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getFavourName() {
		return favourName;
	}

	public void setFavourName(String favourName) {
		this.favourName = favourName;
	}

	public Date getDepositDate() {
		return depositDate;
	}

	public void setDepositDate(Date depositDate) {
		this.depositDate = depositDate;
	}

	public String getDepositNo() {
		return depositNo;
	}

	public void setDepositNo(String depositNo) {
		this.depositNo = depositNo;
	}

	public String getPaymentRef() {
		return paymentRef;
	}

	public void setPaymentRef(String paymentRef) {
		this.paymentRef = paymentRef;
	}

	public String getTransactionRef() {
		return transactionRef;
	}

	public void setTransactionRef(String transactionRef) {
		this.transactionRef = transactionRef;
	}

	public String getChequeAcNo() {
		return chequeAcNo;
	}

	public void setChequeAcNo(String chequeAcNo) {
		this.chequeAcNo = chequeAcNo;
	}

	public Long getFundingAc() {
		return fundingAc;
	}

	public void setFundingAc(Long fundingAc) {
		this.fundingAc = fundingAc;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<FinRepayHeader> getRepayHeaders() {
		return repayHeaders;
	}

	public void setRepayHeaders(List<FinRepayHeader> repayHeaders) {
		this.repayHeaders = repayHeaders;
	}

	public String getBankCodeDesc() {
		return bankCodeDesc;
	}

	public void setBankCodeDesc(String bankCodeDesc) {
		this.bankCodeDesc = bankCodeDesc;
	}

	public String getFundingAcDesc() {
		return fundingAcDesc;
	}

	public void setFundingAcDesc(String fundingAcDesc) {
		this.fundingAcDesc = fundingAcDesc;
	}

	public List<ManualAdviseMovements> getAdvMovements() {
		return advMovements;
	}

	public void setAdvMovements(List<ManualAdviseMovements> advMovements) {
		this.advMovements = advMovements;
	}

	public boolean isDelRecord() {
		return delRecord;
	}

	public void setDelRecord(boolean delRecord) {
		this.delRecord = delRecord;
	}

	public int getPayOrder() {
		return payOrder;
	}

	public void setPayOrder(int payOrder) {
		this.payOrder = payOrder;
	}

	public long getLogKey() {
		return logKey;
	}

	public void setLogKey(long logKey) {
		this.logKey = logKey;
	}

	public String getPartnerBankAc() {
		return partnerBankAc;
	}

	public void setPartnerBankAc(String partnerBankAc) {
		this.partnerBankAc = partnerBankAc;
	}

	public String getPartnerBankAcType() {
		return partnerBankAcType;
	}

	public void setPartnerBankAcType(String partnerBankAcType) {
		this.partnerBankAcType = partnerBankAcType;
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

	public String getFeeTypeDesc() {
		return feeTypeDesc;
	}

	public void setFeeTypeDesc(String feeTypeDesc) {
		this.feeTypeDesc = feeTypeDesc;
	}

	public boolean isNoReserve() {
		return noReserve;
	}

	public void setNoReserve(boolean noReserve) {
		this.noReserve = noReserve;
	}

	public String getFeeTypeCode() {
		return feeTypeCode;
	}

	public void setFeeTypeCode(String feeTypeCode) {
		this.feeTypeCode = feeTypeCode;
	}

	public String getFundingAcCode() {
		return fundingAcCode;
	}

	public void setFundingAcCode(String fundingAcCode) {
		this.fundingAcCode = fundingAcCode;
	}

	public String getReceiptPurpose() {
		return receiptPurpose;
	}

	public void setReceiptPurpose(String receiptPurpose) {
		this.receiptPurpose = receiptPurpose;
	}

	public BigDecimal getPartialPaidAMount() {
		return partialPaidAMount;
	}

	public void setPartialPaidAMount(BigDecimal partialPaidAMount) {
		this.partialPaidAMount = partialPaidAMount;
	}

	public BigDecimal getDueAmount() {
		return dueAmount;
	}

	public void setDueAmount(BigDecimal dueAmount) {
		this.dueAmount = dueAmount;
	}

	public FinRepayHeader getRepayHeader() {
		return repayHeader;
	}

	public void setRepayHeader(FinRepayHeader repayHeader) {
		this.repayHeader = repayHeader;
	}

	public ManualAdviseMovements getPayAdvMovement() {
		return payAdvMovement;
	}

	public void setPayAdvMovement(ManualAdviseMovements payAdvMovement) {
		this.payAdvMovement = payAdvMovement;
	}

	public long getBankBranchID() {
		return bankBranchID;
	}

	public void setBankBranchID(long bankBranchID) {
		this.bankBranchID = bankBranchID;
	}

	public String getiFSC() {
		return iFSC;
	}

	public void setiFSC(String iFSC) {
		this.iFSC = iFSC;
	}

	public String getBranchDesc() {
		return branchDesc;
	}

	public void setBranchDesc(String branchDesc) {
		this.branchDesc = branchDesc;
	}

	public boolean isNoManualReserve() {
		return noManualReserve;
	}

	public void setNoManualReserve(boolean noManualReserve) {
		this.noManualReserve = noManualReserve;
	}

	public String getBankBranchCode() {
		return bankBranchCode;
	}

	public void setBankBranchCode(String bankBranchCode) {
		this.bankBranchCode = bankBranchCode;
	}

	public String getMicr() {
		return micr;
	}

	public void setMicr(String micr) {
		this.micr = micr;
	}

	public Date getRealizationDate() {
		return realizationDate;
	}

	public void setRealizationDate(Date realizationDate) {
		this.realizationDate = realizationDate;
	}

}
