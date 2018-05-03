package com.pennant.backend.model.bmtmasters;

import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class ProductDeviation extends AbstractWorkflowEntity {

	private static final long	serialVersionUID	= -2063878715762100939L;

	private long				productDevID		= Long.MIN_VALUE;
	private long				deviationID			= Long.MIN_VALUE;
	private String				productCode;
	private String				deviationCode;
	private String				deviationDesc;
	private long				severity;
	private boolean				newRecord;
	private String				lovValue;
	private Product				befImage;
	private LoggedInUser		userDetails;
	private String severityCode;
	private String severityName;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("deviationCode");
		excludeFields.add("deviationDesc");
		excludeFields.add("severity");
		excludeFields.add("severityCode");
		excludeFields.add("severityName");

		return excludeFields;
	}

	public long getProductDevID() {
		return productDevID;
	}

	public void setProductDevID(long productDevID) {
		this.productDevID = productDevID;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public long getDeviationID() {
		return deviationID;
	}

	public void setDeviationID(long deviationID) {
		this.deviationID = deviationID;
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

	public Product getBefImage() {
		return befImage;
	}

	public void setBefImage(Product befImage) {
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

	public String getDeviationCode() {
		return deviationCode;
	}

	public void setDeviationCode(String deviationCode) {
		this.deviationCode = deviationCode;
	}

	public void setLoginDetails(LoggedInUser userDetails) {
		setLastMntBy(userDetails.getUserId());
		this.userDetails = userDetails;

	}

	public boolean isNew() {
		return isNewRecord();
	}

	public String getDeviationDesc() {
		return deviationDesc;
	}

	public void setDeviationDesc(String deviationDesc) {
		this.deviationDesc = deviationDesc;
	}

	public long getSeverity() {
		return severity;
	}

	public void setSeverity(long severity) {
		this.severity = severity;
	}

	public String getSeverityCode() {
		return severityCode;
	}

	public void setSeverityCode(String severityCode) {
		this.severityCode = severityCode;
	}

	public String getSeverityName() {
		return severityName;
	}

	public void setSeverityName(String severityName) {
		this.severityName = severityName;
	}
}
