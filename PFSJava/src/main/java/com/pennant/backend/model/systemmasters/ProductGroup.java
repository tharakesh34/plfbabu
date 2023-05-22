package com.pennant.backend.model.systemmasters;

import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlTransient;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class ProductGroup extends AbstractWorkflowEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long productGroupId;
	private String modelId;
	private int productCategoryId;
	private boolean active;
	private String channel;
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

	public int getProductCategoryId() {
		return productCategoryId;
	}

	public void setProductCategoryId(int productCategoryId) {
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

	public long getId() {
		return this.productGroupId;
	}

	public void setId(long id) {
		this.productGroupId = id;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

}
