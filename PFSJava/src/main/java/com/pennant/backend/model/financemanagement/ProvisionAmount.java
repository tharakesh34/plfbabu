package com.pennant.backend.model.financemanagement;

import java.math.BigDecimal;

public class ProvisionAmount {
	private long id = Long.MIN_VALUE;
	private long provisionId;
	private String provisionType;
	private BigDecimal provisionPer = BigDecimal.ZERO;
	private BigDecimal provisionAmtCal = BigDecimal.ZERO;
	private String assetCode;

	public ProvisionAmount() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getProvisionId() {
		return provisionId;
	}

	public void setProvisionId(long provisionId) {
		this.provisionId = provisionId;
	}

	public String getProvisionType() {
		return provisionType;
	}

	public void setProvisionType(String provisionType) {
		this.provisionType = provisionType;
	}

	public BigDecimal getProvisionPer() {
		return provisionPer;
	}

	public void setProvisionPer(BigDecimal provisionPer) {
		this.provisionPer = provisionPer;
	}

	public BigDecimal getProvisionAmtCal() {
		return provisionAmtCal;
	}

	public void setProvisionAmtCal(BigDecimal provisionAmtCal) {
		this.provisionAmtCal = provisionAmtCal;
	}

	public String getAssetCode() {
		return assetCode;
	}

	public void setAssetCode(String assetCode) {
		this.assetCode = assetCode;
	}
}
