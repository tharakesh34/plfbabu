package com.pennanttech.model.knockoff;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.pennant.pff.upload.model.UploadDetails;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class ManualKnockOffUpload extends UploadDetails {
	private static final long serialVersionUID = 5L;

	private String excessType;
	private String allocationType;
	private BigDecimal receiptAmount;
	private Long adviseId;
	private Long feeId;
	private String code;
	private BigDecimal amount;
	private List<ManualKnockOffUpload> allocations = new ArrayList<>();
	private LoggedInUser userDetails;

	public ManualKnockOffUpload() {
		super();
	}

	public String getExcessType() {
		return excessType;
	}

	public void setExcessType(String excessType) {
		this.excessType = excessType;
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

	public Long getAdviseId() {
		return adviseId;
	}

	public void setAdviseId(Long adviseId) {
		this.adviseId = adviseId;
	}

	public Long getFeeId() {
		return feeId;
	}

	public void setFeeId(Long feeId) {
		this.feeId = feeId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public List<ManualKnockOffUpload> getAllocations() {
		return allocations;
	}

	public void setAllocations(List<ManualKnockOffUpload> allocations) {
		this.allocations = allocations;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

}