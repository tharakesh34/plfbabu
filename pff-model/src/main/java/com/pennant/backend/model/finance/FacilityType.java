package com.pennant.backend.model.finance;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class FacilityType extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 2404202219653834252L;

	private String facilityType;
	private String facilityDesc;
	private String facilityFor;
	private String revolving;
	private String lovValue;
	private FacilityType befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public FacilityType() {
		super();
	}

	public FacilityType(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return facilityType;
	}

	public void setId(String id) {
		this.facilityType = id;
	}

	public String getFacilityType() {
		return facilityType;
	}

	public void setFacilityType(String facilityType) {
		this.facilityType = facilityType;
	}

	public String getFacilityDesc() {
		return facilityDesc;
	}

	public void setFacilityDesc(String facilityDesc) {
		this.facilityDesc = facilityDesc;
	}

	public String getFacilityFor() {
		return facilityFor;
	}

	public void setFacilityFor(String facilityFor) {
		this.facilityFor = facilityFor;
	}

	public String getRevolving() {
		return revolving;
	}

	public void setRevolving(String revolving) {
		this.revolving = revolving;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public FacilityType getBefImage() {
		return this.befImage;
	}

	public void setBefImage(FacilityType befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
