package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

public class FinInsurances extends AbstractWorkflowEntity  implements Entity {

	private static final long	serialVersionUID	= 1L;
	private long insId          = Long.MIN_VALUE;
	private String				reference;
	private String				module;
	private String				insuranceType;
	private String				insReference;
	private String				insuranceTypeDesc;
	private String				policyCode;
	private String				policyDesc;
	private boolean				insuranceReq;
	private String				provider;
	private String				providerName;
	private String				paymentMethod;
	private String				calType;
	private BigDecimal			insuranceRate;
	private String				waiverReason;
	private String				insuranceStatus;	
	private String				insuranceFrq;
	private BigDecimal			amount;
	private String              calRule;
	private BigDecimal          calPerc=BigDecimal.ZERO;
	private String              calOn;
	private boolean				newRecord			= false;
	private FinInsurances		befImage;
	private LoggedInUser		userDetails;
	private List<FinSchFrqInsurance> finSchFrqInsurances;
	private boolean 			alwRateChange;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("insuranceTypeDesc");
		excludeFields.add("providerName");
		excludeFields.add("finSchFrqInsurances");
		excludeFields.add("policyDesc");
		excludeFields.add("alwRateChange");
		return excludeFields;
	}

	public FinInsurances() {
		super();
	}

	public FinInsurances(long insId) {
		super();
		this.setId(insId);
	}

	//Getter and Setter methods

	public long getId() {
		return insId;
	}

	public void setId(long id) {
		this.insId = id;
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

	public String getInsuranceTypeDesc() {
		return insuranceTypeDesc;
	}

	public void setInsuranceTypeDesc(String insuranceTypeDesc) {
		this.insuranceTypeDesc = insuranceTypeDesc;
	}

	public String getPolicyCode() {
		return policyCode;
	}

	public void setPolicyCode(String policyCode) {
		this.policyCode = policyCode;
	}

	public String getPolicyDesc() {
		return policyDesc;
	}

	public void setPolicyDesc(String policyDesc) {
		this.policyDesc = policyDesc;
	}

	public boolean isInsuranceReq() {
		return insuranceReq;
	}

	public void setInsuranceReq(boolean insuranceReq) {
		this.insuranceReq = insuranceReq;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getCalType() {
		return calType;
	}

	public void setCalType(String calType) {
		this.calType = calType;
	}

	public BigDecimal getInsuranceRate() {
		return insuranceRate;
	}

	public void setInsuranceRate(BigDecimal insuranceRate) {
		this.insuranceRate = insuranceRate;
	}

	public String getWaiverReason() {
		return waiverReason;
	}

	public void setWaiverReason(String waiverReason) {
		this.waiverReason = waiverReason;
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

	public String getCalRule() {
		return calRule;
	}

	public void setCalRule(String calRule) {
		this.calRule = calRule;
	}

	public BigDecimal getCalPerc() {
		return calPerc;
	}

	public void setCalPerc(BigDecimal calPerc) {
		this.calPerc = calPerc;
	}

	public String getCalOn() {
		return calOn;
	}

	public void setCalOn(String calOn) {
		this.calOn = calOn;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public FinInsurances getBefImage() {
		return befImage;
	}

	public void setBefImage(FinInsurances befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getInsuranceStatus() {
		return insuranceStatus;
	}

	public void setInsuranceStatus(String insuranceStatus) {
		this.insuranceStatus = insuranceStatus;
	}

	public List<FinSchFrqInsurance> getFinSchFrqInsurances() {
		return finSchFrqInsurances;
	}

	public void setFinSchFrqInsurances(List<FinSchFrqInsurance> finSchFrqInsurances) {
		this.finSchFrqInsurances = finSchFrqInsurances;
	}

	public boolean isAlwRateChange() {
		return alwRateChange;
	}

	public void setAlwRateChange(boolean alwRateChange) {
		this.alwRateChange = alwRateChange;
	}

}
