package com.pennanttech.pff.cd.model;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class SchemeDealerGroup extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long schemeDealerGroupId = Long.MIN_VALUE;
	private String promotionId;
	private int dealerGroupCode;
	private boolean active;
	private SchemeDealerGroup befImage;
	private LoggedInUser userDetails;
	private String lovValue;
	private boolean newRecord = false;
	private boolean isSave = false;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("isSave");
		return excludeFields;
	}

	public int getDealerGroupCode() {
		return dealerGroupCode;
	}

	public void setDealerGroupCode(int dealerGroupCode) {
		this.dealerGroupCode = dealerGroupCode;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public SchemeDealerGroup getBefImage() {
		return befImage;
	}

	public void setBefImage(SchemeDealerGroup befImage) {
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

	public long getSchemeDealerGroupId() {
		return schemeDealerGroupId;
	}

	public void setSchemeDealerGroupId(long schemeDealerGroupId) {
		this.schemeDealerGroupId = schemeDealerGroupId;
	}

	public String getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(String promotionId) {
		this.promotionId = promotionId;
	}

	public boolean isSave() {
		return isSave;
	}

	public void setSave(boolean isSave) {
		this.isSave = isSave;
	}
}
