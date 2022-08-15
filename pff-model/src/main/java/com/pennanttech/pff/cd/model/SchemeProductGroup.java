package com.pennanttech.pff.cd.model;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class SchemeProductGroup extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long schemeProductGroupId = Long.MIN_VALUE;
	private String promotionId;
	private int productGroupCode;
	private boolean POSVendor;
	private boolean active;
	private SchemeProductGroup befImage;
	private LoggedInUser userDetails;
	private String lovValue;
	private boolean newRecord = false;
	private boolean isSave = false;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("isSave");
		return excludeFields;
	}

	public long getSchemeProductGroupId() {
		return schemeProductGroupId;
	}

	public void setSchemeProductGroupId(long schemeProductGroupId) {
		this.schemeProductGroupId = schemeProductGroupId;
	}

	public String getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(String promotionId) {
		this.promotionId = promotionId;
	}

	public int getProductGroupCode() {
		return productGroupCode;
	}

	public void setProductGroupCode(int productGroupCode) {
		this.productGroupCode = productGroupCode;
	}

	public boolean isPOSVendor() {
		return POSVendor;
	}

	public void setPOSVendor(boolean pOSVendor) {
		POSVendor = pOSVendor;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public SchemeProductGroup getBefImage() {
		return befImage;
	}

	public void setBefImage(SchemeProductGroup befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public boolean isSave() {
		return isSave;
	}

	public void setSave(boolean isSave) {
		this.isSave = isSave;
	}
}
