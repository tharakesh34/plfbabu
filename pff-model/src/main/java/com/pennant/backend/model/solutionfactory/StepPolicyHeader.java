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
 * FileName    		:  StepPolicyHeader.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-12-2011    														*
 *                                                                  						*
 * Modified Date    :  14-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.solutionfactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>StepPolicyHeader table</b>.<br>
 *
 */
@XmlType(propOrder = {
		"policyCode","policyDesc","stepType","stepPolicyDetails","returnStatus"
})
@XmlAccessorType(XmlAccessType.NONE)
public class StepPolicyHeader extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 3832850641524383002L;
	
	@XmlElement
	private String policyCode;
	@XmlElement
	private String policyDesc;
	
	private String stepNumber;
	private String tenorSplitPerc;
	private String rateMargin;
	private String emiSplitPerc;
	
	private boolean newRecord;
	private String lovValue;
	private StepPolicyHeader befImage;
	private LoggedInUser userDetails;
	@XmlElement
	private String stepType;

	@XmlElementWrapper(name="stepDetails")
	@XmlElement(name="stepDetail")
	private List<StepPolicyDetail> stepPolicyDetails = null;
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

	@XmlElement
	private WSReturnStatus returnStatus;
	
	public boolean isNew() {
		return isNewRecord();
	}

	public StepPolicyHeader() {
		super();
	}
	
	public StepPolicyHeader(String id) {
		super();
		this.setPolicyCode(id);
	}
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("stepPolicyDetails");
		excludeFields.add("stepNumber");
		excludeFields.add("tenorSplitPerc");
		excludeFields.add("rateMargin");
		excludeFields.add("emiSplitPerc");
		excludeFields.add("returnStatus");
		return excludeFields;
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//	

	public String getId() {
		return policyCode;
	}

	public void setId(String id) {
		this.policyCode = id;
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

	public StepPolicyHeader getBefImage(){
		return this.befImage;
	}
	public void setBefImage(StepPolicyHeader beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public List<StepPolicyDetail> getStepPolicyDetails() {
		return stepPolicyDetails;
	}
	public void setStepPolicyDetails(List<StepPolicyDetail> stepPolicyDetails) {
		this.stepPolicyDetails = stepPolicyDetails;
	}

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}
	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public String getStepNumber() {
		return stepNumber;
	}

	public void setStepNumber(String stepNumber) {
		this.stepNumber = stepNumber;
	}

	public String getTenorSplitPerc() {
		return tenorSplitPerc;
	}

	public void setTenorSplitPerc(String tenorSplitPerc) {
		this.tenorSplitPerc = tenorSplitPerc;
	}

	public String getRateMargin() {
		return rateMargin;
	}

	public void setRateMargin(String rateMargin) {
		this.rateMargin = rateMargin;
	}

	public String getEmiSplitPerc() {
		return emiSplitPerc;
	}

	public void setEmiSplitPerc(String emiSplitPerc) {
		this.emiSplitPerc = emiSplitPerc;
	}

	public String getStepType() {
		return stepType;
	}

	public void setStepType(String stepType) {
		this.stepType = stepType;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	
}
