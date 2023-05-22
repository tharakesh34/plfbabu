package com.pennanttech.pff.provision.model;

import java.math.BigDecimal;

import com.pennant.pff.upload.model.UploadDetails;

public class ProvisionUpload extends UploadDetails {
	private static final long serialVersionUID = 1L;

	private Long assetClassId;
	private Long assetSubClassId;
	private String assetClassCode;
	private String assetSubClassCode;
	private String overrideProvision;
	private BigDecimal provisionPercentage = BigDecimal.ZERO;

	public ProvisionUpload() {
		super();
	}

	public Long getAssetClassId() {
		return assetClassId;
	}

	public void setAssetClassId(Long assetClassId) {
		this.assetClassId = assetClassId;
	}

	public Long getAssetSubClassId() {
		return assetSubClassId;
	}

	public void setAssetSubClassId(Long assetSubClassId) {
		this.assetSubClassId = assetSubClassId;
	}

	public String getAssetClassCode() {
		return assetClassCode;
	}

	public void setAssetClassCode(String assetClassCode) {
		this.assetClassCode = assetClassCode;
	}

	public String getAssetSubClassCode() {
		return assetSubClassCode;
	}

	public void setAssetSubClassCode(String assetSubClassCode) {
		this.assetSubClassCode = assetSubClassCode;
	}

	public String getOverrideProvision() {
		return overrideProvision;
	}

	public void setOverrideProvision(String overrideProvision) {
		this.overrideProvision = overrideProvision;
	}

	public BigDecimal getProvisionPercentage() {
		return provisionPercentage;
	}

	public void setProvisionPercentage(BigDecimal provisionPercentage) {
		this.provisionPercentage = provisionPercentage;
	}

}
