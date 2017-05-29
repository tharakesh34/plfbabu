package com.pennant.backend.model.applicationmaster;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

public class FinTypeInsurances extends AbstractWorkflowEntity {
	private static final long	serialVersionUID	= 1L;
	private String				finType=null;
	private String              finTypeDesc; 
	private String				insuranceType;
	private String              insuranceTypeDesc;
	private String				insuranceProvider;
	private String				takafulName;
	private String 				policyRate;
	private String				policyType;
	private String              policyDesc;
	private String				dftPayType;
	private String				calType;
	private String				amountRule;
	private String				ruleCodeDesc;
	private BigDecimal          constAmt= BigDecimal.ZERO;
	private BigDecimal          percentage=BigDecimal.ZERO;
	private String              calculateOn;
	private boolean				mandatory;
	private boolean				alwRateChange;
	private boolean				newRecord			= false;
	private FinTypeInsurances	befImage;
	private LoggedInUser		userDetails;
	
	private int 				moduleId;
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("insuranceTypeDesc");
		excludeFields.add("finTypeDesc");
		excludeFields.add("ruleCodeDesc");
		excludeFields.add("policyDesc");
		excludeFields.add("insuranceProvider");
		excludeFields.add("takafulName");
		excludeFields.add("policyRate");
		return excludeFields;
	}
	
	public FinTypeInsurances() {
		super();
	}
	
	public FinTypeInsurances(String id) {
		super();
		this.setId(id);
	}
	
	//Getter and Setter methods
	
	public String getId() {
		return finType;
	}
	
	public void setId (String id) {
		this.finType = id;
	}
	public boolean isNew() {
		return isNewRecord();
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getInsuranceType() {
		return insuranceType;
	}

	public void setInsuranceType(String insuranceType) {
		this.insuranceType = insuranceType;
	}

	public String getDftPayType() {
		return dftPayType;
	}

	public void setDftPayType(String dftPayType) {
		this.dftPayType = dftPayType;
	}

	public String getCalType() {
		return calType;
	}

	public void setCalType(String calType) {
		this.calType = calType;
	}


	public String getAmountRule() {
		return amountRule;
	}

	public void setAmountRule(String amountRule) {
		this.amountRule = amountRule;
	}

	public String getRuleCodeDesc() {
		return ruleCodeDesc;
	}

	public void setRuleCodeDesc(String ruleCodeDesc) {
		this.ruleCodeDesc = ruleCodeDesc;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public boolean isAlwRateChange() {
		return alwRateChange;
	}

	public void setAlwRateChange(boolean alwRateChange) {
		this.alwRateChange = alwRateChange;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public FinTypeInsurances getBefImage() {
		return befImage;
	}

	public void setBefImage(FinTypeInsurances befImage) {
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
	
	public String getPolicyType() {
		return policyType;
	}

	public void setPolicyType(String policyType) {
		this.policyType = policyType;
	}

	public String getPolicyDesc() {
		return policyDesc;
	}

	public void setPolicyDesc(String policyDesc) {
		this.policyDesc = policyDesc;
	}

	public BigDecimal getConstAmt() {
		return constAmt;
	}

	public void setConstAmt(BigDecimal constAmt) {
		this.constAmt = constAmt;
	}

	public BigDecimal getPercentage() {
		return percentage;
	}

	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}

	public String getCalculateOn() {
		return calculateOn;
	}

	public void setCalculateOn(String calculateOn) {
		this.calculateOn = calculateOn;
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}

	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public String getInsuranceProvider() {
		return insuranceProvider;
	}

	public void setInsuranceProvider(String insuranceProvider) {
		this.insuranceProvider = insuranceProvider;
	}

	public String getTakafulName() {
		return takafulName;
	}

	public void setTakafulName(String takafulName) {
		this.takafulName = takafulName;
	}

	public String getPolicyRate() {
		return policyRate;
	}

	public void setPolicyRate(String policyRate) {
		this.policyRate = policyRate;
	}

	public int getModuleId() {
		return moduleId;
	}

	public void setModuleId(int moduleId) {
		this.moduleId = moduleId;
	}

}
