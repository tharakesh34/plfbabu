package com.pennanttech.bajaj.process.collections.model;

import java.math.BigDecimal;
import java.util.Date;

public class CollectionReceiptExtraction {

	private long extractionDetailId = Long.MIN_VALUE;
	private long extractionId;
	private String finReference;
	private String allocationType;
	private String feeDesc;
	private String primaryId;
	private Long bounceId;
	private Date schDate;
	private BigDecimal amount = BigDecimal.ZERO;
	private long receiptID;
	private long receiptSeqID;

	private BigDecimal profit = BigDecimal.ZERO;
	private BigDecimal principal = BigDecimal.ZERO;
	private BigDecimal schdFee = BigDecimal.ZERO;
	private BigDecimal penalty = BigDecimal.ZERO;
	private BigDecimal tDSSchd = BigDecimal.ZERO;
	private BigDecimal excessAmt = BigDecimal.ZERO;
	private BigDecimal bounceAmt = BigDecimal.ZERO;
	private BigDecimal adviseAmt = BigDecimal.ZERO;
	
	private Date dueDate;
	private Integer instNumber;
	private String amountType;
	
	/**
	 * default constructor
	 */
	public CollectionReceiptExtraction() {
		super();
	}

	public long getExtractionDetailId() {
		return extractionDetailId;
	}

	public void setExtractionDetailId(long extractionDetailId) {
		this.extractionDetailId = extractionDetailId;
	}

	public long getExtractionId() {
		return extractionId;
	}

	public void setExtractionId(long extractionId) {
		this.extractionId = extractionId;
	}

	public String getAllocationType() {
		return allocationType;
	}

	public void setAllocationType(String allocationType) {
		this.allocationType = allocationType;
	}

	public String getPrimaryId() {
		return primaryId;
	}

	public void setPrimaryId(String primaryId) {
		this.primaryId = primaryId;
	}

	public Date getSchDate() {
		return schDate;
	}

	public void setSchDate(Date schDate) {
		this.schDate = schDate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public long getReceiptSeqID() {
		return receiptSeqID;
	}

	public void setReceiptSeqID(long receiptSeqID) {
		this.receiptSeqID = receiptSeqID;
	}

	public BigDecimal getProfit() {
		return profit;
	}

	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}

	public BigDecimal getPrincipal() {
		return principal;
	}

	public void setPrincipal(BigDecimal principal) {
		this.principal = principal;
	}

	public BigDecimal getPenalty() {
		return penalty;
	}

	public void setPenalty(BigDecimal penalty) {
		this.penalty = penalty;
	}

	public BigDecimal getSchdFee() {
		return schdFee;
	}

	public void setSchdFee(BigDecimal schdFee) {
		this.schdFee = schdFee;
	}

	public BigDecimal getTDSSchd() {
		return tDSSchd;
	}

	public void setTDSSchd(BigDecimal tDSSchdPayNow) {
		this.tDSSchd = tDSSchdPayNow;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getFeeDesc() {
		return feeDesc;
	}

	public void setFeeDesc(String feeDesc) {
		this.feeDesc = feeDesc;
	}

	public long getReceiptID() {
		return receiptID;
	}

	public void setReceiptID(long receiptID) {
		this.receiptID = receiptID;
	}

	public BigDecimal getExcessAmt() {
		return excessAmt;
	}

	public void setExcessAmt(BigDecimal excessAmt) {
		this.excessAmt = excessAmt;
	}

	public BigDecimal getBounceAmt() {
		return bounceAmt;
	}

	public void setBounceAmt(BigDecimal bounceAmt) {
		this.bounceAmt = bounceAmt;
	}

	public BigDecimal getAdviseAmt() {
		return adviseAmt;
	}

	public void setAdviseAmt(BigDecimal adviseAmt) {
		this.adviseAmt = adviseAmt;
	}

	public Long getBounceId() {
		return bounceId;
	}

	public void setBounceId(Long bounceId) {
		this.bounceId = bounceId;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Integer getInstNumber() {
		return instNumber;
	}

	public void setInstNumber(Integer instNumber) {
		this.instNumber = instNumber;
	}

	public String getAmountType() {
		return amountType;
	}

	public void setAmountType(String amountType) {
		this.amountType = amountType;
	}
 
}
