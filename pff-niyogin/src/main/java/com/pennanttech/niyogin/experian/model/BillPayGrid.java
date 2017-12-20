package com.pennanttech.niyogin.experian.model;

import java.util.Date;

public class BillPayGrid {
	private Date	billPayDate;
	private String	assetClassification;

	public Date getBillPayDate() {
		return billPayDate;
	}

	public void setBillPayDate(Date billPayDate) {
		this.billPayDate = billPayDate;
	}

	public String getAssetClassification() {
		return assetClassification;
	}

	public void setAssetClassification(String assetClassification) {
		this.assetClassification = assetClassification;
	}

}
