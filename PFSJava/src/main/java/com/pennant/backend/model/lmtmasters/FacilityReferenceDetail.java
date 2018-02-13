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
 * FileName    		:  FinanceReferenceDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-11-2011    														*
 *                                                                  						*
 * Modified Date    :  26-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.lmtmasters;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>FinanceReferenceDetail table</b>.<br>
 * 
 */
public class FacilityReferenceDetail extends AbstractWorkflowEntity implements Entity {

	private static final long serialVersionUID = 6569842731762889262L;
	
	private long finRefDetailId = Long.MIN_VALUE;
	private String finType;
	private int finRefType;
	private long finRefId;
	private String lovDescRefDesc;
	private boolean isActive;
	private String showInStage;
	private String mandInputInStage;
	private String allowInputInStage;
	private boolean lovDescRegenerate;
	private boolean overRide;
	private int overRideValue;
	private String lovDescCodelov;
	private String lovDescNamelov;
	private String lovDescAggImage;
	private boolean lovDescIsRemarksAllowed;
	private long lovDescCheckMinCount;
	private long lovDescCheckMaxCount;
	

	private String lovDescElgRuleValue;
	private String lovDescAggReportName;
	private String aggType;
	private String lovDescAggReportPath;
	private int lovDescminScore;
	private boolean lovDescisoverride;
	private int lovDescoverrideScore;
	private boolean newRecord = false;
	private String lovValue;
	private FacilityReferenceDetail befImage;
	private LoggedInUser userDetails;

	private String lovDescElgCalVal;
	private BigDecimal lovDescRuleResult;
	
	private String lovDescFinCcyCode;
	private String lovDescProductCodeName;
	private String lovDescRuleReturnType;
	
	private List<CheckListDetail> lovDescCheckListAnsDetails;
	
	public boolean isNew() {
		return isNewRecord();
	}

	public FacilityReferenceDetail() {
		super();
	}

	public FacilityReferenceDetail(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("aggType");
		return excludeFields;
	}

	//Getter and Setter methods


	// Getter and Setter methods

	public long getId() {
		return finRefDetailId;
	}


	public void setId(long id) {
		this.finRefDetailId = id;
	}

	public long getFinRefDetailId() {
		return finRefDetailId;
	}

	public void setFinRefDetailId(long finRefDetailId) {
		this.finRefDetailId = finRefDetailId;

	}		



	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;

	}		



	public boolean isOverRide() {
		return overRide;
	}

	public void setOverRide(boolean overRide) {
		this.overRide = overRide;
	}

	public int getOverRideValue() {
		return overRideValue;
	}

	public void setOverRideValue(int overRideValue) {
		this.overRideValue = overRideValue;
	}

	public int getFinRefType() {
		return finRefType;
	}

	public void setFinRefType(int finRefType) {
		this.finRefType = finRefType;

	}		



	public long getFinRefId() {
		return finRefId;
	}

	public void setFinRefId(long finRefId) {
		this.finRefId = finRefId;
	}

	public boolean isIsActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getShowInStage() {
		return showInStage;
	}

	public void setShowInStage(String showInStage) {
		this.showInStage = showInStage;
	}

	public String getMandInputInStage() {
		return mandInputInStage;
	}

	public void setMandInputInStage(String mandInputInStage) {
		this.mandInputInStage = mandInputInStage;
	}

	public String getAllowInputInStage() {
		return allowInputInStage;
	}

	public void setAllowInputInStage(String allowInputInStage) {
		this.allowInputInStage = allowInputInStage;
	}

	public boolean isLovDescRegenerate() {
		return lovDescRegenerate;
	}

	public void setLovDescRegenerate(boolean lovDescRegenerate) {
		this.lovDescRegenerate = lovDescRegenerate;
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

	public FacilityReferenceDetail getBefImage() {
		return this.befImage;
	}


	public void setBefImage(FacilityReferenceDetail beforeImage) {
		this.befImage = beforeImage;

	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getLovDescRefDesc() {
		return lovDescRefDesc;
	}

	public void setLovDescRefDesc(String lovDescRefDesc) {
		this.lovDescRefDesc = lovDescRefDesc;
	}


	public String getLovDescCodelov() {
		return lovDescCodelov;
	}

	public void setLovDescCodelov(String lovDescCodelov) {
		this.lovDescCodelov = lovDescCodelov;
	}

	public String getLovDescNamelov() {
		return lovDescNamelov;
	}

	public void setLovDescNamelov(String lovDescNamelov) {
		this.lovDescNamelov = lovDescNamelov;
	}
	
	public String getLovDescAggImage() {
    	return lovDescAggImage;
    }

	public void setLovDescAggImage(String lovDescAggImage) {
    	this.lovDescAggImage = lovDescAggImage;
    }

	public String getLovDescElgRuleValue() {
		return lovDescElgRuleValue;
	}

	public void setLovDescElgRuleValue(String lovDescElgRuleValue) {
		this.lovDescElgRuleValue = lovDescElgRuleValue;
	}

	public void setLovDescAggReportName(String lovDescAggReportName) {
		this.lovDescAggReportName = lovDescAggReportName;
	}

	public String getLovDescAggReportName() {
		return lovDescAggReportName;
	}

	public void setLovDescAggReportPath(String lovDescAggReportPath) {
		this.lovDescAggReportPath = lovDescAggReportPath;
	}

	public String getLovDescAggReportPath() {
		return lovDescAggReportPath;
	}

	public boolean getLovDescIsRemarksAllowed() {
		return lovDescIsRemarksAllowed;
	}

	public void setLovDescIsRemarksAllowed(boolean lovDescIsRemarksAllowed) {
		this.lovDescIsRemarksAllowed = lovDescIsRemarksAllowed;
	}

	public long getLovDescCheckMinCount() {
		return lovDescCheckMinCount;
	}

	public void setLovDescCheckMinCount(long lovDescCheckMinCount) {
		this.lovDescCheckMinCount = lovDescCheckMinCount;
	}

	public long getLovDescCheckMaxCount() {
		return lovDescCheckMaxCount;
	}

	public void setLovDescCheckMaxCount(long lovDescCheckMaxCount) {
		this.lovDescCheckMaxCount = lovDescCheckMaxCount;
	}



	public int getLovDescminScore() {
		return lovDescminScore;
	}

	public void setLovDescminScore(int lovDescminScore) {
		this.lovDescminScore = lovDescminScore;
	}

	public boolean isLovDescisoverride() {
		return lovDescisoverride;
	}

	public void setLovDescisoverride(boolean lovDescisoverride) {
		this.lovDescisoverride = lovDescisoverride;
	}

	public int getLovDescoverrideScore() {
		return lovDescoverrideScore;
	}

	public void setLovDescoverrideScore(int lovDescoverrideScore) {
		this.lovDescoverrideScore = lovDescoverrideScore;
	}

	public String getLovDescElgCalVal() {
		return lovDescElgCalVal;
	}

	public void setLovDescElgCalVal(String lovDescElgCalVal) {
		this.lovDescElgCalVal = lovDescElgCalVal;
	}

	public BigDecimal getLovDescRuleResult() {
		return lovDescRuleResult;
	}

	public void setLovDescRuleResult(BigDecimal lovDescRuleResult) {
		this.lovDescRuleResult = lovDescRuleResult;
	}

	public void setLovDescFinCcyCode(String lovDescFinCcyCode) {
	    this.lovDescFinCcyCode = lovDescFinCcyCode;
    }

	public String getLovDescFinCcyCode() {
	    return lovDescFinCcyCode;
    }

	public void setLovDescProductCodeName(String lovDescProductCodeName) {
	    this.lovDescProductCodeName = lovDescProductCodeName;
    }

	public String getLovDescProductCodeName() {
	    return lovDescProductCodeName;
    }

	public void setLovDescRuleReturnType(String lovDescRuleReturnType) {
	    this.lovDescRuleReturnType = lovDescRuleReturnType;
    }

	public String getLovDescRuleReturnType() {
	    return lovDescRuleReturnType;
    }

	public void setLovDescCheckListAnsDetails(List<CheckListDetail> lovDescCheckListAnsDetails) {
	    this.lovDescCheckListAnsDetails = lovDescCheckListAnsDetails;
    }

	public List<CheckListDetail> getLovDescCheckListAnsDetails() {
	    return lovDescCheckListAnsDetails;
    }

	public String getAggType() {
	    return aggType;
    }

	public void setAggType(String aggType) {
	    this.aggType = aggType;
    }
}
