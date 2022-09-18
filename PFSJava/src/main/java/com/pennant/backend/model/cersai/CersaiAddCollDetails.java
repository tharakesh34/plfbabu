package com.pennant.backend.model.cersai;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class CersaiAddCollDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	private long batchId;
	private String rowType;
	private long serialNumber;
	private long noOfBrrowers;
	private long noOfAssetOwners;
	private long noOfConsortiumMemebers;
	private Long siTypeId;
	private String siTypeOthers;
	private String financingTypeId;
	private Date siCreationDate;
	private BigDecimal totalSecuredAmt;
	private String entityMISToken;
	private String narration;
	private String typeOfCharge;
	private boolean tpm = false;
	private String batchRefNumber;
	private String collateralType;

	public String getRowType() {
		return rowType;
	}

	public void setRowType(String rowType) {
		this.rowType = rowType;
	}

	public long getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(long serialNumber) {
		this.serialNumber = serialNumber;
	}

	public long getNoOfBrrowers() {
		return noOfBrrowers;
	}

	public void setNoOfBrrowers(long noOfBrrowers) {
		this.noOfBrrowers = noOfBrrowers;
	}

	public long getNoOfAssetOwners() {
		return noOfAssetOwners;
	}

	public void setNoOfAssetOwners(long noOfAssetOwners) {
		this.noOfAssetOwners = noOfAssetOwners;
	}

	public long getNoOfConsortiumMemebers() {
		return noOfConsortiumMemebers;
	}

	public void setNoOfConsortiumMemebers(long noOfConsortiumMemebers) {
		this.noOfConsortiumMemebers = noOfConsortiumMemebers;
	}

	public Long getSiTypeId() {
		return siTypeId;
	}

	public void setSiTypeId(Long siTypeId) {
		this.siTypeId = siTypeId;
	}

	public String getSiTypeOthers() {
		return siTypeOthers;
	}

	public void setSiTypeOthers(String siTypeOthers) {
		this.siTypeOthers = siTypeOthers;
	}

	public String getFinancingTypeId() {
		return financingTypeId;
	}

	public void setFinancingTypeId(String financingTypeId) {
		this.financingTypeId = financingTypeId;
	}

	public Date getSiCreationDate() {
		return siCreationDate;
	}

	public void setSiCreationDate(Date siCreationDate) {
		this.siCreationDate = siCreationDate;
	}

	public BigDecimal getTotalSecuredAmt() {
		return totalSecuredAmt;
	}

	public void setTotalSecuredAmt(BigDecimal totalSecuredAmt) {
		this.totalSecuredAmt = totalSecuredAmt;
	}

	public String getEntityMISToken() {
		return entityMISToken;
	}

	public void setEntityMISToken(String entityMISToken) {
		this.entityMISToken = entityMISToken;
	}

	public String getNarration() {
		return narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}

	public String getTypeOfCharge() {
		return typeOfCharge;
	}

	public void setTypeOfCharge(String typeOfCharge) {
		this.typeOfCharge = typeOfCharge;
	}

	public boolean isTpm() {
		return tpm;
	}

	public void setTpm(boolean tpm) {
		this.tpm = tpm;
	}

	public String getBatchRefNumber() {
		return batchRefNumber;
	}

	public void setBatchRefNumber(String batchRefNumber) {
		this.batchRefNumber = batchRefNumber;
	}

	public long getBatchId() {
		return batchId;
	}

	public void setBatchId(long batchId) {
		this.batchId = batchId;
	}

	public String getCollateralType() {
		return collateralType;
	}

	public void setCollateralType(String collateralType) {
		this.collateralType = collateralType;
	}

}
