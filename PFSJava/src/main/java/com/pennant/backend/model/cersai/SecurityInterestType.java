package com.pennant.backend.model.cersai;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>SecurityInterestType table</b>.<br>
 *
 */
@XmlType(propOrder = { "assetCategoryId", "id", "description" })
@XmlAccessorType(XmlAccessType.FIELD)
public class SecurityInterestType extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private Long assetCategoryId;
	private String assetCategoryIdName;
	private int id;
	private String description;
	@XmlTransient
	private boolean newRecord = false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private SecurityInterestType befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public SecurityInterestType() {
		super();
	}

	public SecurityInterestType(int id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("assetCategoryIdName");
		return excludeFields;
	}

	public Long getAssetCategoryId() {
		return assetCategoryId;
	}

	public void setAssetCategoryId(Long assetCategoryId) {
		this.assetCategoryId = assetCategoryId;
	}

	public String getAssetCategoryIdName() {
		return this.assetCategoryIdName;
	}

	public void setAssetCategoryIdName(String assetCategoryIdName) {
		this.assetCategoryIdName = assetCategoryIdName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public SecurityInterestType getBefImage() {
		return this.befImage;
	}

	public void setBefImage(SecurityInterestType beforeImage) {
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
