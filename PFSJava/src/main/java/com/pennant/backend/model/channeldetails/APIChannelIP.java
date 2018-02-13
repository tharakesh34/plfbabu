package com.pennant.backend.model.channeldetails;

import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

public class APIChannelIP extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long Id = Long.MIN_VALUE;
	private long channelId;
	private String iP;
	private String description;
	private String code;
	private boolean active = true;
	private boolean newRecord = false;
	private LoggedInUser userDetails;
	private APIChannelIP befImage;

	public boolean isNew() {
		return isNewRecord();
	}

	public APIChannelIP() {
		super();
	}

	public APIChannelIP(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("code");
		excludeFields.add("description");

		return excludeFields;
	}

	// Getter and Setter methods

	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}

	public long getChannelId() {
		return channelId;
	}

	public void setChannelId(long channelId) {
		this.channelId = channelId;
	}

	public String getiP() {
		return iP;
	}

	public void setiP(String iP) {
		this.iP = iP;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public APIChannelIP getBefImage() {
		return this.befImage;
	}

	public void setBefImage(APIChannelIP befImage2) {
		this.befImage = befImage2;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
