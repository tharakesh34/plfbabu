package com.pennanttech.pff.mmfl.cd.model;

import java.sql.Timestamp;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class SchemeDealerGroup extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long promotionId = Long.MIN_VALUE;
	private String schemeId;
	private int dealerGroupCode;
	private boolean active;
	private SchemeDealerGroup befImage;
	private LoggedInUser userDetails;
	private String lovValue;
	private boolean newRecord = false;

	public long getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(long promotionId) {
		this.promotionId = promotionId;
	}

	public String getSchemeId() {
		return schemeId;
	}

	public void setSchemeId(String schemeId) {
		this.schemeId = schemeId;
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
}
