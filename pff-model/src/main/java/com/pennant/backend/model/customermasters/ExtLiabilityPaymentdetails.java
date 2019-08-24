package com.pennant.backend.model.customermasters;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class ExtLiabilityPaymentdetails extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private long liabilityId;
	@XmlElement
	private String EMIType;
	@XmlElement
	private boolean installmentCleared;
	private ExtLiabilityPaymentdetails befImage;
	private LoggedInUser userDetails;
	private boolean newRecord = false;
	private String lovValue;
	private int keyValue = 0;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();

		return excludeFields;
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

	public long getLiabilityId() {
		return liabilityId;
	}

	public String getEMIType() {
		return EMIType;
	}

	public boolean isInstallmentCleared() {
		return installmentCleared;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setLiabilityId(long liabilityId) {
		this.liabilityId = liabilityId;
	}

	public void setEMIType(String eMIType) {
		EMIType = eMIType;
	}

	public void setInstallmentCleared(boolean installmentCleared) {
		this.installmentCleared = installmentCleared;
	}

	public ExtLiabilityPaymentdetails getBefImage() {
		return befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setBefImage(ExtLiabilityPaymentdetails befImage) {
		this.befImage = befImage;
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

}
