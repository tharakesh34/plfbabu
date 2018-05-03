package com.pennant.backend.model.financemanagement.bankorcorpcreditreview;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class FinCreditRevType extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 3557119742009775415L;
	
	private String creditRevCode;
	private String creditRevDesc;
	private String creditCCY;
	private String entryCCY;
	private boolean newRecord = false;
	private String lovValue;
	private FinCreditRevType befImage;
	private LoggedInUser userDetails;
	
	public FinCreditRevType() {
		super();
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

	public FinCreditRevType getBefImage() {
		return this.befImage;
	}
	public void setBefImage(FinCreditRevType beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
	public void setCreditRevCode(String creditRevCode) {
	    this.creditRevCode = creditRevCode;
    }
	public String getCreditRevCode() {
	    return creditRevCode;
    }
	public void setCreditRevDesc(String creditRevDesc) {
	    this.creditRevDesc = creditRevDesc;
    }
	public String getCreditRevDesc() {
	    return creditRevDesc;
    }
	public void setCreditCCY(String creditCCY) {
	    this.creditCCY = creditCCY;
    }
	public String getCreditCCY() {
	    return creditCCY;
    }
	public void setEntryCCY(String entryCCY) {
	    this.entryCCY = entryCCY;
    }
	public String getEntryCCY() {
	    return entryCCY;
    }


}
