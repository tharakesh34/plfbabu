package com.pennant.backend.model.customermasters;

import java.util.HashSet;
import java.util.Set;

import jakarta.xml.bind.annotation.XmlElement;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class ExtLiabilityPaymentdetails extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private long liabilityId;
	@XmlElement
	private String emiType;
	private int emiClearedDay;
	private ExtLiabilityPaymentdetails befImage;
	private LoggedInUser userDetails;
	private boolean newRecord = false;
	private String lovValue;
	private int keyValue = 0;
	@XmlElement
	private String emiClearance;

	public ExtLiabilityPaymentdetails() {
		super();
	}

	public Set<String> getExcludeFields() {
		return new HashSet<>();
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getLiabilityId() {
		return liabilityId;
	}

	public void setLiabilityId(long liabilityId) {
		this.liabilityId = liabilityId;
	}

	public String getEmiType() {
		return emiType;
	}

	public void setEmiType(String emiType) {
		this.emiType = emiType;
	}

	public ExtLiabilityPaymentdetails getBefImage() {
		return befImage;
	}

	public void setBefImage(ExtLiabilityPaymentdetails befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public int getKeyValue() {
		return keyValue;
	}

	public void setKeyValue(int keyValue) {
		this.keyValue = keyValue;
	}

	public String getEmiClearance() {
		return emiClearance;
	}

	public void setEmiClearance(String emiClearance) {
		this.emiClearance = emiClearance;
	}

	public int getEmiClearedDay() {
		return emiClearedDay;
	}

	public void setEmiClearedDay(int emiClearedDay) {
		this.emiClearedDay = emiClearedDay;
	}

}
