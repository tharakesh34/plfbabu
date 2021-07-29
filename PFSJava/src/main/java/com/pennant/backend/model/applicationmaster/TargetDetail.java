package com.pennant.backend.model.applicationmaster;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class TargetDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 71023783624451043L;

	private String targetCode;
	private String targetDesc;
	private boolean active;
	private String lovValue;
	private TargetDetail befImage;
	private LoggedInUser userDetails;

	public TargetDetail() {
		super();
	}

	public TargetDetail(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return targetCode;
	}

	public void setId(String id) {
		this.targetCode = id;
	}

	public String getTargetCode() {
		return targetCode;
	}

	public void setTargetCode(String targetCode) {
		this.targetCode = targetCode;
	}

	public String getTargetDesc() {
		return targetDesc;
	}

	public void setTargetDesc(String targetDesc) {
		this.targetDesc = targetDesc;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public TargetDetail getBefImage() {
		return befImage;
	}

	public void setBefImage(TargetDetail befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
