package com.pennant.backend.model.systemmasters;

import java.sql.Timestamp;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

import jakarta.xml.bind.annotation.XmlTransient;

public class ProductGroup extends AbstractWorkflowEntity implements Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long productGroupId;
	private String modelId;
	private String productCategoryId;
	private boolean active;
	private String channel;

	@XmlTransient
	private boolean newRecord = false;
	@XmlTransient
	private ProductGroup befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	public ProductGroup() {
		super();

	}

	public ProductGroup(long id) {
		super();
		this.setId(id);
	}

	public long getProductGroupId() {
		return productGroupId;
	}

	public void setProductGroupId(long productGroupId) {
		this.productGroupId = productGroupId;
	}

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public String getProductCategoryId() {
		return productCategoryId;
	}

	public void setProductCategoryId(String productCategoryId) {
		this.productCategoryId = productCategoryId;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
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

	public ProductGroup getBefImage() {
		return befImage;
	}

	public void setBefImage(ProductGroup befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	@Override
	public boolean isNew() {
		// TODO Auto-generated method stub
		return isNewRecord();
	}

	@Override
	public long getId() {
		// TODO Auto-generated method stub
		return this.productGroupId;
	}

	@Override
	public void setId(long id) {
		this.productGroupId = id;

	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

}
