package com.pennant.backend.model.applicationmaster;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

public class AccountTypeGroup extends AbstractWorkflowEntity implements Entity {
	private static final long	serialVersionUID	= -1472467289111692722L;
	
	private long				groupId				= Long.MIN_VALUE;
	private String				groupCode;
	private int					acctTypeLevel;
	private String				groupDescription;
	private String				parentGroup;
	private String				parentGroupDesc;
	private long				parentGroupId;
	private boolean				newRecord;
	private String				lovValue;
	private AccountTypeGroup			befImage;
	@XmlTransient
	private LoggedInUser		userDetails;
	private boolean groupIsActive;
	
	public boolean isNew() {
		return isNewRecord();
	}

	public AccountTypeGroup() {
		super();
	}

	public AccountTypeGroup(long id) {
		super();
		this.setId(id);
	}

	public long getId() {
		return groupId;
	}

	public void setId(long id) {
		this.groupId = id;
	}

	public int getAcctTypeLevel() {
		return acctTypeLevel;
	}

	public void setAcctTypeLevel(int acctTypeLevel) {
		this.acctTypeLevel = acctTypeLevel;
	}

	public String getGroupDescription() {
		return groupDescription;
	}

	public void setGroupDescription(String groupDescription) {
		this.groupDescription = groupDescription;
	}

	public String getParentGroup() {
		return parentGroup;
	}

	public void setParentGroup(String parentGroup) {
		this.parentGroup = parentGroup;
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

	public AccountTypeGroup getBefImage() {
		return befImage;
	}

	public void setBefImage(AccountTypeGroup befImage) {
		this.befImage = befImage;
	}

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
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

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("parentGroupDesc");
		excludeFields.add("parentGroup");
		return excludeFields;
	}

	public Long getParentGroupId() {
		return parentGroupId;
	}

	public void setParentGroupId(Long parentGroupId) {
		this.parentGroupId = parentGroupId;
	}

	public String getParentGroupDesc() {
		return parentGroupDesc;
	}

	public void setParentGroupDesc(String parentGroupDesc) {
		this.parentGroupDesc = parentGroupDesc;
	}

	public boolean isGroupIsActive() {
		return groupIsActive;
	}

	public void setGroupIsActive(boolean groupIsActive) {
		this.groupIsActive = groupIsActive;
	}	
		
}