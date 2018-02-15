package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class IndicativeTermDetail extends AbstractWorkflowEntity {

    private static final long serialVersionUID = -9145740108425516287L;
    
    private String finReference;
	private String facilityType;
	private String lovDescFacilityType;
	private String rpsnName;
	private String rpsnDesg;
	private String lovDescRpsnDesgName;
	private long custId;
	private String lovDescCustCIF;
	private String lovDescCustShrtName;
	private String pricing;
	private String repayments;
	private String lCPeriod;
	private String usancePeriod;
	private boolean securityClean;
	private String securityName;
	private String utilization;
	private String commission;
	private String purpose;
	private String guarantee;
	private String covenants;
	private String lovDescRevolving;
	private String tenor;
	private String finCcy;
	private String finPurpose;
	private String finAmount;
	private String poBox;
	private String fax;
	private String city;
	private String country;
	private String appDate;
	private String appPastYear;
	private String appLastYear;
	private String documentsRequired;
	private String transactionType;
	private String agentBank;
	private String otherDetails;
	private BigDecimal totalFacility = BigDecimal.ZERO;
	private String totalFacilityCCY;
	private BigDecimal underWriting = BigDecimal.ZERO;
	private String underWritingCCY;
	private BigDecimal propFinalTake = BigDecimal.ZERO;
	private String propFinalTakeCCY;
	
	private boolean newRecord=false;
	private String lovValue;
	private IndicativeTermDetail befImage;
	private LoggedInUser userDetails;

	private Date lovDescFinStartDate;
	private Date lovDescMaturityDate;
	
	private int tenorYear = 0;
	private int tenorMonth = 0;
	private String tenorDesc="";

	public boolean isNew() {
		return isNewRecord();
	}

	public IndicativeTermDetail() {
		super();
	}

	public IndicativeTermDetail(String id) {
		super();
		this.setId(id);
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getId() {
		return finReference;
	}	
	public void setId (String id) {
		this.finReference = id;
	}
	
	public String getFinReference() {
    	return finReference;
    }
	public void setFinReference(String finReference) {
    	this.finReference = finReference;
    }

	public String getFacilityType() {
    	return facilityType;
    }
	public void setFacilityType(String facilityType) {
    	this.facilityType = facilityType;
    }
	
	public String getLovDescFacilityType() {
    	return lovDescFacilityType;
    }
	public void setLovDescFacilityType(String lovDescFacilityType) {
    	this.lovDescFacilityType = lovDescFacilityType;
    }
	
	public String getRpsnName() {
    	return rpsnName;
    }
	public void setRpsnName(String rpsnName) {
    	this.rpsnName = rpsnName;
    }
	
	public String getRpsnDesg() {
    	return rpsnDesg;
    }
	public void setRpsnDesg(String rpsnDesg) {
    	this.rpsnDesg = rpsnDesg;
    }

	public String getPricing() {
    	return pricing;
    }
	public void setPricing(String pricing) {
    	this.pricing = pricing;
    }
	
	public String getRepayments() {
    	return repayments;
    }
	public void setRepayments(String repayments) {
    	this.repayments = repayments;
    }
	
	public String getLCPeriod() {
    	return lCPeriod;
    }
	public void setLCPeriod(String lCPeriod) {
    	this.lCPeriod = lCPeriod;
    }
	
	public String getUsancePeriod() {
    	return usancePeriod;
    }
	public void setUsancePeriod(String usancePeriod) {
    	this.usancePeriod = usancePeriod;
    }
	
	public boolean isSecurityClean() {
    	return securityClean;
    }
	public void setSecurityClean(boolean securityClean) {
    	this.securityClean = securityClean;
    }
	
	public String getSecurityName() {
    	return securityName;
    }
	public void setSecurityName(String securityName) {
    	this.securityName = securityName;
    }
	
	public String getUtilization() {
    	return utilization;
    }
	public void setUtilization(String utilization) {
    	this.utilization = utilization;
    }
	
	public String getCommission() {
    	return commission;
    }
	public void setCommission(String commission) {
    	this.commission = commission;
    }
	
	public String getPurpose() {
    	return purpose;
    }
	public void setPurpose(String purpose) {
    	this.purpose = purpose;
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

	public IndicativeTermDetail getBefImage() {
	    return this.befImage;
    }
	public void setBefImage(IndicativeTermDetail befImage) {
	    this.befImage = befImage;
    }

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getLovDescCustCIF() {
    	return lovDescCustCIF;
    }

	public void setLovDescCustCIF(String lovDescCustCIF) {
    	this.lovDescCustCIF = lovDescCustCIF;
    }

	public void setLovDescCustShrtName(String lovDescCustShrtName) {
	    this.lovDescCustShrtName = lovDescCustShrtName;
    }

	public String getLovDescCustShrtName() {
	    return lovDescCustShrtName;
    }

	public void setCustId(long custId) {
	    this.custId = custId;
    }

	public long getCustId() {
	    return custId;
    }

	public void setLovDescRpsnDesgName(String lovDescRpsnDesgName) {
	    this.lovDescRpsnDesgName = lovDescRpsnDesgName;
    }

	public String getLovDescRpsnDesgName() {
	    return lovDescRpsnDesgName;
    }

	public void setLovDescRevolving(String lovDescRevolving) {
	    this.lovDescRevolving = lovDescRevolving;
    }

	public String getLovDescRevolving() {
	    return lovDescRevolving;
    }

	public void setLovDescFinStartDate(Date lovDescFinStartDate) {
	    this.lovDescFinStartDate = lovDescFinStartDate;
    }

	public Date getLovDescFinStartDate() {
	    return lovDescFinStartDate;
    }

	public void setLovDescMaturityDate(Date lovDescMaturityDate) {
	    this.lovDescMaturityDate = lovDescMaturityDate;
    }

	public Date getLovDescMaturityDate() {
	    return lovDescMaturityDate;
    }

	public void setTenor(String tenor) {
	    this.tenor = tenor;
    }

	public String getTenor() {
	    return tenor;
    }

	public void setFinCcy(String ccy) {
	    this.finCcy = ccy;
    }

	public String getFinCcy() {
	    return finCcy;
    }

	public void setFinPurpose(String finPurpose) {
	    this.finPurpose = finPurpose;
    }

	public String getFinPurpose() {
	    return finPurpose;
    }

	public String getGuarantee() {
	    return guarantee;
    }

	public void setGuarantee(String guarantee) {
	    this.guarantee = guarantee;
    }

	public String getCovenants() {
	    return covenants;
    }

	public void setCovenants(String covenants) {
	    this.covenants = covenants;
    }

	public void setFinAmount(String finAmount) {
	    this.finAmount = finAmount;
    }

	public String getFinAmount() {
	    return finAmount;
    }
	public String getPoBox() {
    	return poBox;
    }

	public void setPoBox(String poBox) {
    	this.poBox = poBox;
    }

	public String getFax() {
    	return fax;
    }

	public void setFax(String fax) {
    	this.fax = fax;
    }

	public String getCity() {
    	return city;
    }

	public void setCity(String city) {
    	this.city = city;
    }

	public String getCountry() {
    	return country;
    }

	public void setCountry(String country) {
    	this.country = country;
    }

	public String getAppDate() {
    	return appDate;
    }

	public void setAppDate(String appDate) {
    	this.appDate = appDate;
    }

	public String getAppPastYear() {
    	return appPastYear;
    }

	public void setAppPastYear(String appPastYear) {
    	this.appPastYear = appPastYear;
    }

	public String getAppLastYear() {
    	return appLastYear;
    }

	public void setAppLastYear(String appLastYear) {
    	this.appLastYear = appLastYear;
    }
	//Ck Editor Display fields
	public String getHtmlCommission() {
    	return commission;
    }
	public String getHtmlPurpose() {
		return purpose;
	}
	public String getHtmlSecurityName() {
		return securityName;
	}

	public int getTenorYear() {
	    return tenorYear;
    }

	public void setTenorYear(int tenorYear) {
	    this.tenorYear = tenorYear;
    }

	public int getTenorMonth() {
	    return tenorMonth;
    }

	public void setTenorMonth(int tenorMonth) {
	    this.tenorMonth = tenorMonth;
    }

	public String getTenorDesc() {
	    return tenorDesc;
    }

	public void setTenorDesc(String tenorDesc) {
	    this.tenorDesc = tenorDesc;
    }

	public String getDocumentsRequired() {
	    return documentsRequired;
    }

	public String getHtmlDocumentsRequired() {
		return documentsRequired;
	}
	
	public void setDocumentsRequired(String documentsRequired) {
	    this.documentsRequired = documentsRequired;
    }

	public String getTransactionType() {
	    return transactionType;
    }

	public void setTransactionType(String transactionType) {
	    this.transactionType = transactionType;
    }

	public String getAgentBank() {
	    return agentBank;
    }

	public void setAgentBank(String agentBank) {
	    this.agentBank = agentBank;
    }

	public String getOtherDetails() {
	    return otherDetails;
    }

	public void setOtherDetails(String otherDetails) {
	    this.otherDetails = otherDetails;
    }

	public BigDecimal getTotalFacility() {
	    return totalFacility;
    }

	public void setTotalFacility(BigDecimal totalFacility) {
	    this.totalFacility = totalFacility;
    }

	public String getTotalFacilityCCY() {
	    return totalFacilityCCY;
    }

	public void setTotalFacilityCCY(String totalFacilityCCY) {
	    this.totalFacilityCCY = totalFacilityCCY;
    }

	public BigDecimal getUnderWriting() {
	    return underWriting;
    }

	public void setUnderWriting(BigDecimal underWriting) {
	    this.underWriting = underWriting;
    }

	public String getUnderWritingCCY() {
	    return underWritingCCY;
    }

	public void setUnderWritingCCY(String underWritingCCY) {
	    this.underWritingCCY = underWritingCCY;
    }

	public BigDecimal getPropFinalTake() {
	    return propFinalTake;
    }

	public void setPropFinalTake(BigDecimal propFinalTake) {
	    this.propFinalTake = propFinalTake;
    }

	public String getPropFinalTakeCCY() {
	    return propFinalTakeCCY;
    }

	public void setPropFinalTakeCCY(String propFinalTakeCCY) {
	    this.propFinalTakeCCY = propFinalTakeCCY;
    }


}
