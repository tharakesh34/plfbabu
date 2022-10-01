package com.pennant.pff.presentment.model;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class PresentmentExcludeCode extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -1472467289111692722L;

	private long id = Long.MIN_VALUE;
	private String code;
	private long bounceId;
	private String description;
	private boolean createBounceOnDueDate;
	private int excludeId;
	private String bounceCode;
	private long createdBy;
	private Timestamp createdOn;
	private Timestamp approvedOn;
	private Long approvedBy;
	private boolean active;
	private LoggedInUser userDetails;
	private PresentmentExcludeCode befImage;

	public PresentmentExcludeCode() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();

		excludeFields.add("bounceCode");
		excludeFields.add("lovdesccategory");
		excludeFields.add("bounceId");

		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public long getBounceId() {
		return bounceId;
	}

	public void setBounceId(long bounceId) {
		this.bounceId = bounceId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isCreateBounceOnDueDate() {
		return createBounceOnDueDate;
	}

	public void setCreateBounceOnDueDate(boolean createBounceOnDueDate) {
		this.createBounceOnDueDate = createBounceOnDueDate;
	}

	public int getExcludeId() {
		return excludeId;
	}

	public void setExcludeId(int excludeId) {
		this.excludeId = excludeId;
	}

	public String getBounceCode() {
		return bounceCode;
	}

	public void setBounceCode(String bounceCode) {
		this.bounceCode = bounceCode;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public Long getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(Long approvedBy) {
		this.approvedBy = approvedBy;
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

	public PresentmentExcludeCode getBefImage() {
		return befImage;
	}

	public void setBefImage(PresentmentExcludeCode befImage) {
		this.befImage = befImage;
	}

	public Timestamp getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Timestamp approvedOn) {
		this.approvedOn = approvedOn;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

}
