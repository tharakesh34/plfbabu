package com.pennant.backend.model.solutionfactory;

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
 * FileName    		:  StepPolicyDetail.java                                                   * 	  
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


import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>StepPolicyDetail table</b>.<br>
 *
 */
@XmlType(propOrder = {
		"stepNumber","tenorSplitPerc","rateMargin","emiSplitPerc"
})
@XmlAccessorType(XmlAccessType.NONE)
public class StepPolicyDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private String policyCode = null;
	@XmlElement
	private int stepNumber;
	@XmlElement
	private BigDecimal tenorSplitPerc = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal rateMargin = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal emiSplitPerc = BigDecimal.ZERO;
	private boolean newRecord=false;
	private String lovValue;
	private StepPolicyDetail befImage;
	private LoggedInUser userDetails;
	
	public boolean isNew() {
		return isNewRecord();
	}

	public StepPolicyDetail() {
		super();
	}

	public StepPolicyDetail(String id) {
		super();
		this.setId(id);
	}
	
	public Set<String> getExcludeFields() {
		return new HashSet<String>();
	}
	
	//Getter and Setter methods
	
	public String getId() {
		return policyCode;
	}
	
	public void setId (String id) {
		this.policyCode = id;
	}
		
	public String getPolicyCode() {
		return policyCode;
	}

	public void setPolicyCode(String policyCode) {
		this.policyCode = policyCode;
	}

	public int getStepNumber() {
		return stepNumber;
	}

	public void setStepNumber(int stepNumber) {
		this.stepNumber = stepNumber;
	}

	public BigDecimal getTenorSplitPerc() {
		return tenorSplitPerc;
	}
	public void setTenorSplitPerc(BigDecimal tenorSplitPerc) {
		this.tenorSplitPerc = tenorSplitPerc;
	}
	
	public BigDecimal getRateMargin() {
		return rateMargin;
	}

	public void setRateMargin(BigDecimal rateMargin) {
		this.rateMargin = rateMargin;
	}

	public BigDecimal getEmiSplitPerc() {
		return emiSplitPerc;
	}

	public void setEmiSplitPerc(BigDecimal emiSplitPerc) {
		this.emiSplitPerc = emiSplitPerc;
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

	public StepPolicyDetail getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(StepPolicyDetail beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}

