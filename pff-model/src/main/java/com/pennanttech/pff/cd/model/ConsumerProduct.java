package com.pennanttech.pff.cd.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class ConsumerProduct extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long productId = Long.MIN_VALUE;
	private String modelId;
	private String ModelDescription;
	private String ManufacturerId;
	private String AssetDescription;
	private BigDecimal MinAmount;
	private BigDecimal MaxAmount;
	private String modelStatus;
	private String channel;
	private boolean newRecord = false;
	private ConsumerProduct befImage;
	private LoggedInUser userDetails;
	private boolean active;

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public String getModelDescription() {
		return ModelDescription;
	}

	public void setModelDescription(String modelDescription) {
		ModelDescription = modelDescription;
	}

	public String getManufacturerId() {
		return ManufacturerId;
	}

	public void setManufacturerId(String manufacturerId) {
		ManufacturerId = manufacturerId;
	}

	public String getAssetDescription() {
		return AssetDescription;
	}

	public void setAssetDescription(String assetDescription) {
		AssetDescription = assetDescription;
	}

	public BigDecimal getMinAmount() {
		return MinAmount;
	}

	public void setMinAmount(BigDecimal minAmount) {
		MinAmount = minAmount;
	}

	public BigDecimal getMaxAmount() {
		return MaxAmount;
	}

	public void setMaxAmount(BigDecimal maxAmount) {
		MaxAmount = maxAmount;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public ConsumerProduct getBefImage() {
		return befImage;
	}

	public void setBefImage(ConsumerProduct befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isNew() {
		return newRecord;
	}

	public String getModelStatus() {
		return modelStatus;
	}

	public void setModelStatus(String modelStatus) {
		this.modelStatus = modelStatus;
	}
}
