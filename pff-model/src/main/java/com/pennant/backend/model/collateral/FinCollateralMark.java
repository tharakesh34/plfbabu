package com.pennant.backend.model.collateral;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class FinCollateralMark implements Serializable {

	private static final long serialVersionUID = -5665749691631296093L;

	private long FinCollateralId = Long.MIN_VALUE;
	private long finID;
	private String finReference;
	private String referenceNum;
	private String status;
	private String reason;
	private String branchCode;
	private String returnCode;
	private String returnText;
	private String depositID;
	private BigDecimal insAmount = BigDecimal.ZERO;
	private Date blockingDate;
	private boolean processed;

	public FinCollateralMark() {
	    super();
	}

	public long getId() {
		return FinCollateralId;
	}

	public void setId(long id) {
		this.FinCollateralId = id;
	}

	public long getFinCollateralId() {
		return FinCollateralId;
	}

	public void setFinCollateralId(long finCollateralId) {
		FinCollateralId = finCollateralId;
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

	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnText() {
		return returnText;
	}

	public void setReturnText(String returnText) {
		this.returnText = returnText;
	}

	public String getDepositID() {
		return depositID;
	}

	public void setDepositID(String depositID) {
		this.depositID = depositID;
	}

	public BigDecimal getInsAmount() {
		return insAmount;
	}

	public void setInsAmount(BigDecimal insAmount) {
		this.insAmount = insAmount;
	}

	public Date getBlockingDate() {
		return blockingDate;
	}

	public void setBlockingDate(Date blockingDate) {
		this.blockingDate = blockingDate;
	}

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

}
