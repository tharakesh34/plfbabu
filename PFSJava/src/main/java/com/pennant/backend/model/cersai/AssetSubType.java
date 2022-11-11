package com.pennant.backend.model.cersai;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>AssetSubType table</b>.<br>
 *
 */
public class AssetSubType extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private Long assetTypeId;
	private String assetTypeIdName;

	private int id;
	private String description;
	private boolean newRecord = false;
	private String lovValue;
	private AssetSubType befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public AssetSubType() {
		super();
	}

	public AssetSubType(int id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("assetTypeIdName");
		return excludeFields;
	}

	public Long getAssetTypeId() {
		return assetTypeId;
	}

	public void setAssetTypeId(Long assetTypeId) {
		this.assetTypeId = assetTypeId;
	}

	public String getAssetTypeIdName() {
		return assetTypeIdName;
	}

	public void setAssetTypeIdName(String assetTypeIdName) {
		this.assetTypeIdName = assetTypeIdName;
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

	public AssetSubType getBefImage() {
		return this.befImage;
	}

	public void setBefImage(AssetSubType beforeImage) {
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
