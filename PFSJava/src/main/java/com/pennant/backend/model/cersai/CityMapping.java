package com.pennant.backend.model.cersai;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>CityMapping table</b>.<br>
 *
 */
public class CityMapping extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private int mappingType;
	private String cityCode;
	private String cityCodeName;
	private String mappingValue;
	private boolean newRecord = false;
	private String lovValue;
	private CityMapping befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public CityMapping() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("cityCodeName");
		return excludeFields;
	}

	public int getMappingType() {
		return mappingType;
	}

	public void setMappingType(int mappingType) {
		this.mappingType = mappingType;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getCityCodeName() {
		return this.cityCodeName;
	}

	public void setCityCodeName(String cityCodeName) {
		this.cityCodeName = cityCodeName;
	}

	public String getMappingValue() {
		return mappingValue;
	}

	public void setMappingValue(String mappingValue) {
		this.mappingValue = mappingValue;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public CityMapping getBefImage() {
		return this.befImage;
	}

	public void setBefImage(CityMapping beforeImage) {
		this.befImage = beforeImage;
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

}
