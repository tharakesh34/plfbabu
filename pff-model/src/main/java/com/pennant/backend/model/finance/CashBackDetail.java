package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;

public class CashBackDetail {

	private long Id;
	private long finID;
	private String finReference;
	private String type;
	private BigDecimal amount = BigDecimal.ZERO;
	private long adviseId;
	private boolean refunded;
	private long mandateId;
	private Date finStartDate;
	private long promotionSeqId = 0;
	private String feeTypeCode;
	private String hostReference;
	private long feeTypeId;
	private BigDecimal retainedAmount = BigDecimal.ZERO;
	private BigDecimal cGST = BigDecimal.ZERO;
	private BigDecimal sGST = BigDecimal.ZERO;
	private BigDecimal uGST = BigDecimal.ZERO;
	private BigDecimal iGST = BigDecimal.ZERO;
	private BigDecimal tGST = BigDecimal.ZERO;
	private BigDecimal cess = BigDecimal.ZERO;
	private String manfMerchId;
	private String storeName;
	private long linkedTranId;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public boolean isRefunded() {
		return refunded;
	}

	public void setRefunded(boolean refunded) {
		this.refunded = refunded;
	}

	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}

	public long getAdviseId() {
		return adviseId;
	}

	public void setAdviseId(long adviseId) {
		this.adviseId = adviseId;
	}

	public long getMandateId() {
		return mandateId;
	}

	public void setMandateId(long mandateId) {
		this.mandateId = mandateId;
	}

	public Date getFinStartDate() {
		return finStartDate;
	}

	public void setFinStartDate(Date finStartDate) {
		this.finStartDate = finStartDate;
	}

	public long getPromotionSeqId() {
		return promotionSeqId;
	}

	public void setPromotionSeqId(long promotionSeqId) {
		this.promotionSeqId = promotionSeqId;
	}

	public String getFeeTypeCode() {
		return feeTypeCode;
	}

	public void setFeeTypeCode(String feeTypeCode) {
		this.feeTypeCode = feeTypeCode;
	}

	public String getHostReference() {
		return hostReference;
	}

	public void setHostReference(String hostReference) {
		this.hostReference = hostReference;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public long getFeeTypeId() {
		return feeTypeId;
	}

	public void setFeeTypeId(long feeTypeId) {
		this.feeTypeId = feeTypeId;
	}

	public BigDecimal getRetainedAmount() {
		return retainedAmount;
	}

	public void setRetainedAmount(BigDecimal retainedAmount) {
		this.retainedAmount = retainedAmount;
	}

	public BigDecimal getcGST() {
		return cGST;
	}

	public void setcGST(BigDecimal cGST) {
		this.cGST = cGST;
	}

	public BigDecimal getsGST() {
		return sGST;
	}

	public void setsGST(BigDecimal sGST) {
		this.sGST = sGST;
	}

	public BigDecimal getuGST() {
		return uGST;
	}

	public void setuGST(BigDecimal uGST) {
		this.uGST = uGST;
	}

	public BigDecimal getiGST() {
		return iGST;
	}

	public void setiGST(BigDecimal iGST) {
		this.iGST = iGST;
	}

	public BigDecimal gettGST() {
		return tGST;
	}

	public void settGST(BigDecimal tGST) {
		this.tGST = tGST;
	}

	public BigDecimal getCess() {
		return cess;
	}

	public void setCess(BigDecimal cess) {
		this.cess = cess;
	}

	public String getManfMerchId() {
		return manfMerchId;
	}

	public void setManfMerchId(String manfMerchId) {
		this.manfMerchId = manfMerchId;
	}

	public long getLinkedTranId() {
		return linkedTranId;
	}

	public void setLinkedTranId(long linkedTranId) {
		this.linkedTranId = linkedTranId;
	}

}
