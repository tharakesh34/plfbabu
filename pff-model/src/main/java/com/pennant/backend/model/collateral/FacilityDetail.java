/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  FacilityDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-12-2013    														*
 *                                                                  						*
 * Modified Date    :  04-12-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-12-2013       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/

package com.pennant.backend.model.collateral;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>FacilityDetail table</b>.<br>
 *
 */
public class FacilityDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;
	
	private String cAFReference = null;
	private String facilityRef;
	private String termSheetRef;
	private String facilityFor;
	private String facilityType;
	private String facilityTypeDesc;
	private String facilityCCY;
	private BigDecimal exposure;
	private BigDecimal existingLimit;
	private BigDecimal newLimit;
	private BigDecimal financeAmount;
	private String pricing;
	private String repayments;
	private String rateType;
	private String lCPeriod;
	private String usancePeriod;
	private boolean securityClean;
	private String securityDesc;
	private String utilization;
	private String commission;
	private String purpose;
	private long custID;
	private Date startDate;
	private Date maturityDate;
	
	private boolean newRecord=false;
	private String lovValue;
	private FacilityDetail befImage;
	private LoggedInUser userDetails;
	private String revolving;

	private String guarantee;
	private String covenants;
	private String documentsRequired;
	
	private int tenorYear;
	private int tenorMonth;
	private String tenorDesc;

	private String     transactionType;
	private String     agentBank;
	private String     otherDetails;
	private BigDecimal totalFacility = BigDecimal.ZERO;
	private BigDecimal underWriting = BigDecimal.ZERO;
	private BigDecimal propFinalTake = BigDecimal.ZERO;
	private String     totalFacilityCcy;
	private String     underWritingCcy;
	private String     propFinalTakeCcy;

	
	public FacilityDetail() {
		super();
	}

	public FacilityDetail(String id) {
		super();
		this.setId(id);
	}
	
	public boolean isNew() {
		return isNewRecord();
	}

	public Set<String> getExcludeFields() {
		
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("revolving");
		excludeFields.add("CCYformat");
		excludeFields.add("facilityCCYName");
		excludeFields.add("facilityTypeDesc");
		excludeFields.add("totalFacilityFormatter");
		excludeFields.add("underWritingFormatter");
		excludeFields.add("propFinalTakeFormatter");
		
		excludeFields.add("totalFacilityCcyName");
		excludeFields.add("underWritingCcyName");
		excludeFields.add("propFinalTakeCcyName");
		
		return excludeFields;
	}
	
	public String getId() {
		return cAFReference;
	}
	public void setId (String id) {
		this.cAFReference = id;
	}
	
	public String getCAFReference() {
		return cAFReference;
	}
	public void setCAFReference(String cAFReference) {
		this.cAFReference = cAFReference;
	}
	
	public String getTermSheetRef() {
    	return termSheetRef;
    }
	public void setTermSheetRef(String termSheetRef) {
    	this.termSheetRef = termSheetRef;
    }

	public String getFacilityRef() {
		return facilityRef;
	}
	public void setFacilityRef(String facilityRef) {
		this.facilityRef = facilityRef;
	}
	
	public String getFacilityFor() {
		return facilityFor;
	}
	public void setFacilityFor(String facilityFor) {
		this.facilityFor = facilityFor;
	}
	
	public String getFacilityType() {
		return facilityType;
	}
	public void setFacilityType(String facilityType) {
		this.facilityType = facilityType;
	}
	
	public String getFacilityTypeDesc() {
    	return facilityTypeDesc;
    }
	public void setFacilityTypeDesc(String facilityTypeDesc) {
    	this.facilityTypeDesc = facilityTypeDesc;
    }

	public String getFacilityCCY() {
		return facilityCCY;
	}
	public void setFacilityCCY(String facilityCCY) {
		this.facilityCCY = facilityCCY;
	}
	 
	public BigDecimal getExposure() {
		if (exposure != null) {
			return exposure;
		}
		return BigDecimal.ZERO;
	}
	public void setExposure(BigDecimal exposure) {
		this.exposure = exposure;
	}
	
	public BigDecimal getExistingLimit() {
		if (existingLimit != null) {
	        return existingLimit;
        }
        return BigDecimal.ZERO;
	}
	public void setExistingLimit(BigDecimal existingLimit) {
		this.existingLimit = existingLimit;
	}
	
	public BigDecimal getNewLimit() {
		if (newLimit != null) {
	        return newLimit;
        }
        return BigDecimal.ZERO;
	}
	public void setNewLimit(BigDecimal newLimit) {
		this.newLimit = newLimit;
	}
	
	public BigDecimal getFinanceAmount() {
		return financeAmount;
	}
	public void setFinanceAmount(BigDecimal financeAmount) {
		this.financeAmount = financeAmount;
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
	
	public String getRateType() {
		return rateType;
	}
	public void setRateType(String rateType) {
		this.rateType = rateType;
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
	
	public String getSecurityDesc() {
		return securityDesc;
	}
	public void setSecurityDesc(String securityDesc) {
		this.securityDesc = securityDesc;
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
	
	public long getCustID() {
    	return custID;
    }
	public void setCustID(long custID) {
    	this.custID = custID;
    }

	public Date getStartDate() {
    	return startDate;
    }
	public void setStartDate(Date startDate) {
    	this.startDate = startDate;
    }

	public Date getMaturityDate() {
    	return maturityDate;
    }
	public void setMaturityDate(Date maturityDate) {
    	this.maturityDate = maturityDate;
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

	public FacilityDetail getBefImage(){
		return this.befImage;
	}
	public void setBefImage(FacilityDetail beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setRevolving(String revolving) {
	    this.revolving = revolving;
    }
	public String getRevolving() {
	    return revolving;
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

	public int getTenorYear() {
    	return tenorYear;
    }
	public int getTenorMonth() {
    	return tenorMonth;
    }

	public String getTenorDesc() {
    	return tenorDesc;
    }
	public void setTenorYear(int tenorYear) {
    	this.tenorYear = tenorYear;
    }

	public void setTenorMonth(int tenorMonth) {
    	this.tenorMonth = tenorMonth;
    }
	public void setTenorDesc(String tenorDesc) {
    	this.tenorDesc = tenorDesc;
    }

	public String getDocumentsRequired() {
	    return documentsRequired;
    }
	public void setDocumentsRequired(String documentsRequired) {
	    this.documentsRequired = documentsRequired;
    }
	
	public String getHtmlDocumentsRequired() {
		return documentsRequired;
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

	public BigDecimal getUnderWriting() {
    	return underWriting;
    }
	public void setUnderWriting(BigDecimal underWriting) {
    	this.underWriting = underWriting;
    }

	public BigDecimal getPropFinalTake() {
    	return propFinalTake;
    }
	public void setPropFinalTake(BigDecimal propFinalTake) {
    	this.propFinalTake = propFinalTake;
    }

	public String getTotalFacilityCcy() {
    	return totalFacilityCcy;
    }
	public void setTotalFacilityCcy(String totalFacilityCcy) {
    	this.totalFacilityCcy = totalFacilityCcy;
    }

	public String getUnderWritingCcy() {
    	return underWritingCcy;
    }
	public void setUnderWritingCcy(String underWritingCcy) {
    	this.underWritingCcy = underWritingCcy;
    }

	public String getPropFinalTakeCcy() {
    	return propFinalTakeCcy;
    }
	public void setPropFinalTakeCcy(String propFinalTakeCcy) {
    	this.propFinalTakeCcy = propFinalTakeCcy;
    }
	
}
