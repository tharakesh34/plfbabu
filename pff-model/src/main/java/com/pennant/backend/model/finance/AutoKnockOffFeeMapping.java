package com.pennant.backend.model.finance;

import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

import javax.xml.bind.annotation.XmlTransient;

public class AutoKnockOffFeeMapping extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private long knockOffId;
	private int feeTypeId;
	private String feeTypeCode;
	private int feeOrder;
	private LoggedInUser userDetails;
	private AutoKnockOffFeeMapping befImage;
	@XmlTransient
	private boolean newRecord = false;
	@XmlTransient
	private String lovValue;
	private int keyValue = 0;

	public AutoKnockOffFeeMapping() {
		super();
	}

	public AutoKnockOffFeeMapping(String id) {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("feeTypeCode");
		excludeFields.add("keyValue");

		return excludeFields;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getKnockOffId() {
		return knockOffId;
	}

	public void setKnockOffId(long knockOffId) {
		this.knockOffId = knockOffId;
	}

	public int getFeeTypeId() {
		return feeTypeId;
	}

	public void setFeeTypeId(int feeTypeId) {
		this.feeTypeId = feeTypeId;
	}

	public String getFeeTypeCode() {
		return feeTypeCode;
	}

	public void setFeeTypeCode(String feeTypeCode) {
		this.feeTypeCode = feeTypeCode;
	}

	public int getFeeOrder() {
		return feeOrder;
	}

	public void setFeeOrder(int feeOrder) {
		this.feeOrder = feeOrder;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public AutoKnockOffFeeMapping getBefImage() {
		return befImage;
	}

	public void setBefImage(AutoKnockOffFeeMapping befImage) {
		this.befImage = befImage;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public int getKeyValue() {
		return keyValue;
	}

	public void setKeyValue(int keyValue) {
		this.keyValue = keyValue;
	}

}
