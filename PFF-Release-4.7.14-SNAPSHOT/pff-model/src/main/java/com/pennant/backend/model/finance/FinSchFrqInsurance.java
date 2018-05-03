package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class FinSchFrqInsurance extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -7903112168142979313L;
	
	private long                insId;
	private String				reference;
	private String				module;
	private String				insuranceType;
	private String				insReference;
	private Date				InsSchDate;
	private BigDecimal			insuranceRate = BigDecimal.ZERO;
	private String				insuranceFrq;
	private BigDecimal			amount = BigDecimal.ZERO;
	private boolean				newRecord			= false;
	private FinSchFrqInsurance	befImage;
	private LoggedInUser		userDetails;
	private BigDecimal			closingBalance;
	private BigDecimal          insurancePaid= BigDecimal.ZERO;
	private BigDecimal          insuranceWaived= BigDecimal.ZERO;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		return excludeFields;
	}

	public FinSchFrqInsurance() {
		super();
	}

	public FinSchFrqInsurance(String id) {
		super();
		this.setId(id);
	}

	//Getter and Setter methods

	public String getId() {
		return reference;
	}

	public void setId(String id) {
		this.reference = id;
	}
	
	public long getInsId() {
		return insId;
	}

	public void setInsId(long insId) {
		this.insId = insId;
	}
	
	public boolean isNew() {
		return isNewRecord();
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getInsuranceType() {
		return insuranceType;
	}

	public void setInsuranceType(String insuranceType) {
		this.insuranceType = insuranceType;
	}

	public String getInsReference() {
		return insReference;
	}

	public void setInsReference(String insReference) {
		this.insReference = insReference;
	}

	public BigDecimal getInsuranceRate() {
		return insuranceRate;
	}
	public void setInsuranceRate(BigDecimal insuranceRate) {
		this.insuranceRate = insuranceRate;
	}

	public String getInsuranceFrq() {
		return insuranceFrq;
	}

	public void setInsuranceFrq(String insuranceFrq) {
		this.insuranceFrq = insuranceFrq;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public FinSchFrqInsurance getBefImage() {
		return befImage;
	}

	public void setBefImage(FinSchFrqInsurance befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Date getInsSchDate() {
		return InsSchDate;
	}

	public void setInsSchDate(Date insSchDate) {
		InsSchDate = insSchDate;
	}

	public BigDecimal getClosingBalance() {
		return closingBalance;
	}

	public void setClosingBalance(BigDecimal closingBalance) {
		this.closingBalance = closingBalance;
	}
	public BigDecimal getInsurancePaid() {
		return insurancePaid;
	}

	public void setInsurancePaid(BigDecimal insurancePaid) {
		this.insurancePaid = insurancePaid;
	}

	public BigDecimal getInsuranceWaived() {
		return insuranceWaived;
	}

	public void setInsuranceWaived(BigDecimal insuranceWaived) {
		this.insuranceWaived = insuranceWaived;
	}


}
