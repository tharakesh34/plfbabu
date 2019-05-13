package com.pennant.backend.model.limits;

import java.io.Serializable;
import java.util.Date;

public class ClosedFacilityDetail implements Serializable {

	private static final long serialVersionUID = 4668781042104594551L;

	public ClosedFacilityDetail() {
		super();
	}

	private String limitReference;
	private String facilityStatus;
	private Date closedDate;
	private boolean processed;
	private Date processedDate;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getLimitReference() {
		return limitReference;
	}

	public void setLimitReference(String limitReference) {
		this.limitReference = limitReference;
	}

	public String getFacilityStatus() {
		return facilityStatus;
	}

	public void setFacilityStatus(String facilityStatus) {
		this.facilityStatus = facilityStatus;
	}

	public Date getClosedDate() {
		return closedDate;
	}

	public void setClosedDate(Date closedDate) {
		this.closedDate = closedDate;
	}

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	public Date getProcessedDate() {
		return processedDate;
	}

	public void setProcessedDate(Date processedDate) {
		this.processedDate = processedDate;
	}
}
