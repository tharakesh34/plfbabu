package com.pennanttech.pff.commodity.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class Commodity extends CommodityType {
	private static final long serialVersionUID = 1L;

	private String HSNCode;
	private BigDecimal currentValue;
	private long CommodityType;
	private String CommodityTypeCode;
	private boolean upload;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("UnitType");
		excludeFields.add("CommodityTypeCode");
		excludeFields.add("Id");
		excludeFields.add("upload");
		return excludeFields;
	}

	public String getHSNCode() {
		return HSNCode;
	}

	public void setHSNCode(String hSNCode) {
		HSNCode = hSNCode;
	}

	public BigDecimal getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(BigDecimal currentValue) {
		this.currentValue = currentValue;
	}

	public long getCommodityType() {
		return CommodityType;
	}

	public void setCommodityType(long commodityType) {
		CommodityType = commodityType;
	}

	public String getCommodityTypeCode() {
		return CommodityTypeCode;
	}

	public void setCommodityTypeCode(String commodityTypeCode) {
		CommodityTypeCode = commodityTypeCode;
	}

	public boolean isUpload() {
		return upload;
	}

	public void setUpload(boolean upload) {
		this.upload = upload;
	}

}
