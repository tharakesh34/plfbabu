package com.pennant.pff.model.paymentmethodupload;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class PaymentMethodUpload implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String finReference;
	private Long batchId;
	private Long mandateId;
	private String finRepayMethod;
	private List<ErrorDetail> errors = new ArrayList<>();
	private String status;
	private String uploadStatusRemarks;
	private FinanceMain fm;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public Long getBatchId() {
		return batchId;
	}

	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}

	public Long getMandateId() {
		return mandateId;
	}

	public void setMandateId(Long mandateId) {
		this.mandateId = mandateId;
	}

	public String getFinRepayMethod() {
		return finRepayMethod;
	}

	public void setFinRepayMethod(String finRepayMethod) {
		this.finRepayMethod = finRepayMethod;
	}

	public List<ErrorDetail> getErrorDetails() {
		return errors;
	}

	public void setErrorDetail(ErrorDetail errorDetails) {
		if (errorDetails == null) {
			return;
		}
		errors.add(errorDetails);
	}

	public void setErrorDetail(long errorCode) {
		setErrorDetail(new ErrorDetail("Key", String.valueOf(errorCode), null, null));
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUploadStatusRemarks() {
		return uploadStatusRemarks;
	}

	public void setUploadStatusRemarks(String uploadStatusRemarks) {
		this.uploadStatusRemarks = uploadStatusRemarks;
	}

	public FinanceMain getFinanceMain() {
		return fm;
	}

	public void setFinanceMain(FinanceMain fm) {
		this.fm = fm;
	}
}
