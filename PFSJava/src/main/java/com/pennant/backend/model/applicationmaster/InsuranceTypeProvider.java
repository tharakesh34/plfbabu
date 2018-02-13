package com.pennant.backend.model.applicationmaster;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

public class InsuranceTypeProvider extends AbstractWorkflowEntity {

	private static final long		serialVersionUID	= 1L;
	private String					insuranceType;
	private String					insuranceTypeDesc;
	private String					providerCode;
	private String					providerName;
	private BigDecimal				insuranceRate		= BigDecimal.ZERO;
	private boolean					newRecord			= false;
	private InsuranceTypeProvider	befImage;
	private LoggedInUser			userDetails;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("providerName");
		excludeFields.add("insuranceRate");
		excludeFields.add("insuranceTypeDesc");
		return excludeFields;
	}
	
	public InsuranceTypeProvider(String id) {
		super();
		this.setId(id);
	}
	
	public void setId (String id) {
		this.insuranceType = id;
	}
	
	public String getId() {
		return insuranceType;
	}
	
	public String getFinType() {
    	return insuranceType;
    }

	public InsuranceTypeProvider() {
		super();
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public String getInsuranceType() {
		return insuranceType;
	}

	public void setInsuranceType(String insuranceType) {
		this.insuranceType = insuranceType;
	}

	public String getProviderCode() {
		return providerCode;
	}

	public void setProviderCode(String providerCode) {
		this.providerCode = providerCode;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public BigDecimal getInsuranceRate() {
		return insuranceRate;
	}

	public void setInsuranceRate(BigDecimal insuranceRate) {
		this.insuranceRate = insuranceRate;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public InsuranceTypeProvider getBefImage() {
		return befImage;
	}

	public void setBefImage(InsuranceTypeProvider befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getInsuranceTypeDesc() {
		return insuranceTypeDesc;
	}

	public void setInsuranceTypeDesc(String insuranceTypeDesc) {
		this.insuranceTypeDesc = insuranceTypeDesc;
	}

}
