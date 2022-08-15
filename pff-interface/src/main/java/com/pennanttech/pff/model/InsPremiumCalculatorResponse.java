package com.pennanttech.pff.model;

public class InsPremiumCalculatorResponse {
	private String success;
	private String sumAssured;
	private String coverageTerm;
	private String totalPremiumExclTaxes;
	private String gst;
	private String totalPremiumInclTaxes;
	InsPremiumCalculatorErrorResponseDto ErrorResponseDtoObject;

	// Getter Methods

	public String getSuccess() {
		return success;
	}

	public String getSumAssured() {
		return sumAssured;
	}

	public String getCoverageTerm() {
		return coverageTerm;
	}

	public String getTotalPremiumExclTaxes() {
		return totalPremiumExclTaxes;
	}

	public String getGst() {
		return gst;
	}

	public String getTotalPremiumInclTaxes() {
		return totalPremiumInclTaxes;
	}

	/*
	 * public PLErrorResponseDto getErrorResponseDto() { return ErrorResponseDtoObject; }
	 */

	// Setter Methods

	public void setSuccess(String success) {
		this.success = success;
	}

	public void setSumAssured(String sumAssured) {
		this.sumAssured = sumAssured;
	}

	public void setCoverageTerm(String coverageTerm) {
		this.coverageTerm = coverageTerm;
	}

	public void setTotalPremiumExclTaxes(String totalPremiumExclTaxes) {
		this.totalPremiumExclTaxes = totalPremiumExclTaxes;
	}

	public void setGst(String gst) {
		this.gst = gst;
	}

	public void setTotalPremiumInclTaxes(String totalPremiumInclTaxes) {
		this.totalPremiumInclTaxes = totalPremiumInclTaxes;
	}

	public void setErrorResponseDto(InsPremiumCalculatorErrorResponseDto errorResponseDtoObject) {
		this.ErrorResponseDtoObject = errorResponseDtoObject;
	}
}
