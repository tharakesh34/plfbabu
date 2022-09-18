package com.pennant.backend.model.cersai;

import java.io.Serializable;
import java.util.Date;

public class CersaiIntangibleAsset implements Serializable {

	private static final long serialVersionUID = 7861483910436310105L;

	private long batchId;
	private String rowType;
	private Long assetCategoryId;
	private Long assetTypeId;
	private String assetTypeOthers;
	private Long assetSubTypeId;
	private String assetUniqueId;
	private String assetSerialNumber;
	private String assetDescription;
	private String dairyNumber;
	private String assetClass;
	private String assetTitle;
	private String patentNumber;
	private Date patentDate;
	private String licenseNumber;
	private String licenseIssuingAuthority;
	private String licenseCategory;
	private String designNumber;
	private String designClass;
	private String tradeMarkAppNumber;
	private Date tradeMarkAppDate;

	public long getBatchId() {
		return batchId;
	}

	public void setBatchId(long batchId) {
		this.batchId = batchId;
	}

	public String getRowType() {
		return rowType;
	}

	public void setRowType(String rowType) {
		this.rowType = rowType;
	}

	public Long getAssetCategoryId() {
		return assetCategoryId;
	}

	public void setAssetCategoryId(Long assetCategoryId) {
		this.assetCategoryId = assetCategoryId;
	}

	public Long getAssetTypeId() {
		return assetTypeId;
	}

	public void setAssetTypeId(Long assetTypeId) {
		this.assetTypeId = assetTypeId;
	}

	public String getAssetTypeOthers() {
		return assetTypeOthers;
	}

	public void setAssetTypeOthers(String assetTypeOthers) {
		this.assetTypeOthers = assetTypeOthers;
	}

	public Long getAssetSubTypeId() {
		return assetSubTypeId;
	}

	public void setAssetSubTypeId(Long assetSubTypeId) {
		this.assetSubTypeId = assetSubTypeId;
	}

	public String getAssetUniqueId() {
		return assetUniqueId;
	}

	public void setAssetUniqueId(String assetUniqueId) {
		this.assetUniqueId = assetUniqueId;
	}

	public String getAssetSerialNumber() {
		return assetSerialNumber;
	}

	public void setAssetSerialNumber(String assetSerialNumber) {
		this.assetSerialNumber = assetSerialNumber;
	}

	public String getAssetDescription() {
		return assetDescription;
	}

	public void setAssetDescription(String assetDescription) {
		this.assetDescription = assetDescription;
	}

	public String getDairyNumber() {
		return dairyNumber;
	}

	public void setDairyNumber(String dairyNumber) {
		this.dairyNumber = dairyNumber;
	}

	public String getAssetClass() {
		return assetClass;
	}

	public void setAssetClass(String assetClass) {
		this.assetClass = assetClass;
	}

	public String getAssetTitle() {
		return assetTitle;
	}

	public void setAssetTitle(String assetTitle) {
		this.assetTitle = assetTitle;
	}

	public String getPatentNumber() {
		return patentNumber;
	}

	public void setPatentNumber(String patentNumber) {
		this.patentNumber = patentNumber;
	}

	public Date getPatentDate() {
		return patentDate;
	}

	public void setPatentDate(Date patentDate) {
		this.patentDate = patentDate;
	}

	public String getLicenseNumber() {
		return licenseNumber;
	}

	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}

	public String getLicenseIssuingAuthority() {
		return licenseIssuingAuthority;
	}

	public void setLicenseIssuingAuthority(String licenseIssuingAuthority) {
		this.licenseIssuingAuthority = licenseIssuingAuthority;
	}

	public String getLicenseCategory() {
		return licenseCategory;
	}

	public void setLicenseCategory(String licenseCategory) {
		this.licenseCategory = licenseCategory;
	}

	public String getDesignNumber() {
		return designNumber;
	}

	public void setDesignNumber(String designNumber) {
		this.designNumber = designNumber;
	}

	public String getDesignClass() {
		return designClass;
	}

	public void setDesignClass(String designClass) {
		this.designClass = designClass;
	}

	public String getTradeMarkAppNumber() {
		return tradeMarkAppNumber;
	}

	public void setTradeMarkAppNumber(String tradeMarkAppNumber) {
		this.tradeMarkAppNumber = tradeMarkAppNumber;
	}

	public Date getTradeMarkAppDate() {
		return tradeMarkAppDate;
	}

	public void setTradeMarkAppDate(Date tradeMarkAppDate) {
		this.tradeMarkAppDate = tradeMarkAppDate;
	}
}
