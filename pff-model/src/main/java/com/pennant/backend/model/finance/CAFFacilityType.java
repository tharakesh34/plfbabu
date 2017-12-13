package com.pennant.backend.model.finance;

import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

public class CAFFacilityType extends AbstractWorkflowEntity {

    private static final long serialVersionUID = 2404202219653834252L;
    
	private String facilityType;
	private String facilityDesc;
	private boolean newRecord=false;
	private String lovValue;
	private CAFFacilityType befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public CAFFacilityType() {
		super();
	}

	public CAFFacilityType(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
		
	public String getId() {
		return facilityType;
	}	
	public void setId (String id) {
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

	public CAFFacilityType getBefImage() {
	    return this.befImage;
    }
	public void setBefImage(CAFFacilityType befImage) {
	    this.befImage = befImage;
    }

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
