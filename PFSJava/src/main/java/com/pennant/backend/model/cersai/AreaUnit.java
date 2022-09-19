package com.pennant.backend.model.cersai;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>AreaUnit table</b>.<br>
 *
 */
public class AreaUnit extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String description;
	private boolean newRecord = false;
	private String lovValue;
	private AreaUnit befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public AreaUnit() {
		super();
	}

	public AreaUnit(Long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		return new HashSet<String>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	public AreaUnit getBefImage() {
		return this.befImage;
	}

	public void setBefImage(AreaUnit beforeImage) {
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
