package com.pennant.pff.model.ratechangeupload;

public class RateChangeUploadLog {
	private Long Id;
	private Long detailId;
	private Long errorCode;
	private String errorDescription;

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public Long getDetailId() {
		return detailId;
	}

	public void setDetailId(Long detailId) {
		this.detailId = detailId;
	}

	public Long getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Long errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}

}
