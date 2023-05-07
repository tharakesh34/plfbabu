package com.pennant.backend.model.loanclosure;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.pff.upload.model.UploadDetails;

public class LoanClosureUpload extends UploadDetails {
	private static final long serialVersionUID = -58727889587717168L;

	private long receiptID = 0;
	private String remarks;
	private String reasonCode;
	private String closureType;
	private String source;
	private String feeTypeCode;
	private String allocationType;
	private Long feeId;
	private String code;
	private BigDecimal amount;

	private List<LoanClosureUpload> allocations = new ArrayList<>();
	private List<FinExcessAmount> excessList = new ArrayList<>();
	private List<ManualAdvise> advises = new ArrayList<>();

	public LoanClosureUpload() {
		super();
	}

	public long getReceiptID() {
		return receiptID;
	}

	public void setReceiptID(long receiptID) {
		this.receiptID = receiptID;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

	public String getClosureType() {
		return closureType;
	}

	public void setClosureType(String closureType) {
		this.closureType = closureType;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getFeeTypeCode() {
		return feeTypeCode;
	}

	public void setFeeTypeCode(String feeTypeCode) {
		this.feeTypeCode = feeTypeCode;
	}

	public String getAllocationType() {
		return allocationType;
	}

	public void setAllocationType(String allocationType) {
		this.allocationType = allocationType;
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

	public List<LoanClosureUpload> getAllocations() {
		return allocations;
	}

	public void setAllocations(List<LoanClosureUpload> allocations) {
		this.allocations = allocations;
	}

	public List<FinExcessAmount> getExcessList() {
		return excessList;
	}

	public void setExcessList(List<FinExcessAmount> excessList) {
		this.excessList = excessList;
	}

	public List<ManualAdvise> getAdvises() {
		return advises;
	}

	public void setAdvises(List<ManualAdvise> advises) {
		this.advises = advises;
	}
}