package com.pennanttech.pff.commodity.model;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class CommodityType extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private String code;
	private String Description;
	private int UnitType;
	private boolean Active;
	private CommodityType befImage;
	private LoggedInUser userDetails;
	private long id = Long.MIN_VALUE;
	private String lovValue;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		return excludeFields;
	}

	public CommodityType getBefImage() {
		return this.befImage;
	}

	public void setBefImage(CommodityType beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public int getUnitType() {
		return UnitType;
	}

	public void setUnitType(int unitType) {
		UnitType = unitType;
	}

	public boolean isActive() {
		return Active;
	}

	public void setActive(boolean active) {
		Active = active;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

}
