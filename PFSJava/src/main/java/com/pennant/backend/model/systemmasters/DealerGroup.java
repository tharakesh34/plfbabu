package com.pennant.backend.model.systemmasters;

import java.sql.Timestamp;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

@XmlType(propOrder = { "dealerGroupId", "dealerCode", "dealerCategory", "channel", "active" })
@XmlAccessorType(XmlAccessType.FIELD)
public class DealerGroup extends AbstractWorkflowEntity implements Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long dealerGroupId;
	private String dealerCode;
	private String dealerCategoryId;
	private boolean active;
	private String channel;

	@XmlTransient
	private boolean newRecord = false;
	@XmlTransient
	private DealerGroup befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	public DealerGroup() {
		super();

	}

	public DealerGroup(long id) {
		super();
		this.setId(id);
	}

	public String getDealerCode() {
		return dealerCode;
	}

	public void setDealerCode(String dealerCode) {
		this.dealerCode = dealerCode;
	}

	public String getDealerCategoryId() {
		return dealerCategoryId;
	}

	public void setDealerCategoryId(String dealerCategoryId) {
		this.dealerCategoryId = dealerCategoryId;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public DealerGroup getBefImage() {
		return befImage;
	}

	public void setBefImage(DealerGroup befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	@Override
	public boolean isNew() {
		// TODO Auto-generated method stub
		return isNewRecord();
	}

	@Override
	public long getId() {
		// TODO Auto-generated method stub
		return this.dealerGroupId;
	}

	@Override
	public void setId(long id) {
		this.dealerGroupId = id;

	}

	public long getDealerGroupId() {
		return dealerGroupId;
	}

	public void setDealerGroupId(long dealerGroupId) {
		this.dealerGroupId = dealerGroupId;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

}
