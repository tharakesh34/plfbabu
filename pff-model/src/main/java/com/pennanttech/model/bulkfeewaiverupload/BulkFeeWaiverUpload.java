package com.pennanttech.model.bulkfeewaiverupload;

import java.math.BigDecimal;

import com.pennant.backend.model.finance.FeeWaiverHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.pff.upload.model.UploadDetails;

public class BulkFeeWaiverUpload extends UploadDetails {
	private static final long serialVersionUID = 1L;

	private Long feeTypeID;
	private String feeTypeCode;
	private BigDecimal waivedAmount = BigDecimal.ZERO;
	private String remarks;
	private FinanceMain financeMain;
	private FeeWaiverHeader waiverHeader;

	public BulkFeeWaiverUpload() {
		super();
	}

	public Long getFeeTypeID() {
		return feeTypeID;
	}

	public void setFeeTypeID(Long feeTypeID) {
		this.feeTypeID = feeTypeID;
	}

	public String getFeeTypeCode() {
		return feeTypeCode;
	}

	public void setFeeTypeCode(String feeTypeCode) {
		this.feeTypeCode = feeTypeCode;
	}

	public BigDecimal getWaivedAmount() {
		return waivedAmount;
	}

	public void setWaivedAmount(BigDecimal waivedAmount) {
		this.waivedAmount = waivedAmount;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public FeeWaiverHeader getWaiverHeader() {
		return waiverHeader;
	}

	public void setWaiverHeader(FeeWaiverHeader waiverHeader) {
		this.waiverHeader = waiverHeader;
	}

}
