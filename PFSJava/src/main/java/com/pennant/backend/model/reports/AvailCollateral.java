package com.pennant.backend.model.reports;

public class AvailCollateral {

	private String collateralReference = "";
	private String collateralType = "";
	private String collateralComplete = "";
	private String collateralCcy = "";
	private String collateralExpiry = "";
	private String collateralValue = "";
	private String margin = "";
	private String bankValuation = "";
	private String lastReview = "";
	private String collateralLoc = "";
	private String collateralDesc = "";

	public AvailCollateral() {
	    super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getCollateralReference() {
		return collateralReference;
	}

	public void setCollateralReference(String collateralReference) {
		this.collateralReference = collateralReference;
	}

	public String getCollateralType() {
		return collateralType;
	}

	public void setCollateralType(String collateralType) {
		this.collateralType = collateralType;
	}

	public String getCollateralComplete() {
		return collateralComplete;
	}

	public void setCollateralComplete(String collateralComplete) {
		this.collateralComplete = collateralComplete;
	}

	public String getCollateralCcy() {
		return collateralCcy;
	}

	public void setCollateralCcy(String collateralCcy) {
		this.collateralCcy = collateralCcy;
	}

	public String getCollateralExpiry() {
		return collateralExpiry;
	}

	public void setCollateralExpiry(String collateralExpiry) {
		this.collateralExpiry = collateralExpiry;
	}

	public String getCollateralValue() {
		return collateralValue;
	}

	public void setCollateralValue(String collateralValue) {
		this.collateralValue = collateralValue;
	}

	public String getMargin() {
		return margin;
	}

	public void setMargin(String margin) {
		this.margin = margin;
	}

	public String getBankValuation() {
		return bankValuation;
	}

	public void setBankValuation(String bankValuation) {
		this.bankValuation = bankValuation;
	}

	public String getLastReview() {
		return lastReview;
	}

	public void setLastReview(String lastReview) {
		this.lastReview = lastReview;
	}

	public String getCollateralLoc() {
		return collateralLoc;
	}

	public void setCollateralLoc(String collateralLoc) {
		this.collateralLoc = collateralLoc;
	}

	public String getCollateralDesc() {
		return collateralDesc;
	}

	public void setCollateralDesc(String collateralDesc) {
		this.collateralDesc = collateralDesc;
	}

}
