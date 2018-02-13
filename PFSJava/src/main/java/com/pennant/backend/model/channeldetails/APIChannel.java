package com.pennant.backend.model.channeldetails;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

public class APIChannel extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long Id = Long.MIN_VALUE;
	private String Code;
	private String Description;
	private boolean active;
	private boolean newRecord = false;
	private APIChannel befImage;
	private LoggedInUser userDetails;
	private List<APIChannelIP> ipList = new ArrayList<APIChannelIP>();

	public boolean isNew() {
		return isNewRecord();
	}

	public APIChannel() {
		super();
	}

	public APIChannel(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("channelAuthDetails");

		return excludeFields;
	}

	// Getter and Setter methods

	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public APIChannel getBefImage() {
		return this.befImage;
	}

	public void setBefImage(APIChannel beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public List<APIChannelIP> getIpList() {
		return ipList;
	}

	public void setIpList(List<APIChannelIP> ipList) {
		this.ipList = ipList;
	}

}
