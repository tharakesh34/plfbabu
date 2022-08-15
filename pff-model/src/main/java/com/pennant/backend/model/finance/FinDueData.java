package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class FinDueData implements Serializable {
	private static final long serialVersionUID = -6980706459904425002L;

	private BigDecimal dueAmount = BigDecimal.ZERO;
	private int hierarchy;
	private int relativeHierarchy;
	private int internalOrder;
	private Date dueDate;
	private String allocType;
	private long adviseId;
	private long feeTypeId;
	private String feeTypeCode;
	private int schdIdx;
	private boolean isAdjust = false;

	public FinDueData() {
		super();
	}

	public BigDecimal getDueAmount() {
		return dueAmount;
	}

	public void setDueAmount(BigDecimal dueAmount) {
		this.dueAmount = dueAmount;
	}

	public int getHierarchy() {
		return hierarchy;
	}

	public void setHierarchy(int hierarchy) {
		this.hierarchy = hierarchy;
	}

	public int getRelativeHierarchy() {
		return relativeHierarchy;
	}

	public void setRelativeHierarchy(int relativeHierarchy) {
		this.relativeHierarchy = relativeHierarchy;
	}

	public int getInternalOrder() {
		return internalOrder;
	}

	public void setInternalOrder(int internalOrder) {
		this.internalOrder = internalOrder;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public String getAllocType() {
		return allocType;
	}

	public void setAllocType(String allocType) {
		this.allocType = allocType;
	}

	public long getAdviseId() {
		return adviseId;
	}

	public void setAdviseId(long adviseId) {
		this.adviseId = adviseId;
	}

	public long getFeeTypeId() {
		return feeTypeId;
	}

	public void setFeeTypeId(long feeTypeId) {
		this.feeTypeId = feeTypeId;
	}

	public String getFeeTypeCode() {
		return feeTypeCode;
	}

	public void setFeeTypeCode(String feeTypeCode) {
		this.feeTypeCode = feeTypeCode;
	}

	public int getSchdIdx() {
		return schdIdx;
	}

	public void setSchdIdx(int schdIdx) {
		this.schdIdx = schdIdx;
	}

	public boolean isAdjust() {
		return isAdjust;
	}

	public void setAdjust(boolean isAdjust) {
		this.isAdjust = isAdjust;
	}

}
