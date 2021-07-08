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
 * FileName    		:  Assignment.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-09-2018    														*
 *                                                                  						*
 * Modified Date    :  12-09-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-09-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.model.applicationmaster;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class Assignment extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private String description;
	private Long dealId;
	private String dealCode;
	private String dealCodeDesc;
	private String loanType;
	private String loanTypeDesc;
	private Date disbDate;
	private BigDecimal sharingPercentage = BigDecimal.ZERO;
	private boolean gst;
	private String opexFeeType;
	private boolean active;
	@XmlTransient
	private boolean newRecord = false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private Assignment befImage;
	@XmlTransient
	private LoggedInUser userDetails;
	private List<AssignmentRate> assignmentRateList;
	private AssignmentRate assignmentRate;
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	private String entityCode;

	public boolean isNew() {
		return isNewRecord();
	}

	public Assignment() {
		super();
	}

	public Assignment(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("dealCode");
		excludeFields.add("dealCodeDesc");
		excludeFields.add("loanTypeDesc");
		excludeFields.add("assignmentRate");
		excludeFields.add("entityCode");
		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getDealId() {
		return dealId;
	}

	public void setDealId(Long dealId) {
		this.dealId = dealId;
	}

	public String getDealCode() {
		return dealCode;
	}

	public void setDealCode(String dealCode) {
		this.dealCode = dealCode;
	}

	public String getLoanType() {
		return loanType;
	}

	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}

	public Date getDisbDate() {
		return disbDate;
	}

	public void setDisbDate(Date disbDate) {
		this.disbDate = disbDate;
	}

	public BigDecimal getSharingPercentage() {
		return sharingPercentage;
	}

	public void setSharingPercentage(BigDecimal sharingPercentage) {
		this.sharingPercentage = sharingPercentage;
	}

	public String getOpexFeeType() {
		return opexFeeType;
	}

	public void setOpexFeeType(String opexFeeType) {
		this.opexFeeType = opexFeeType;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getDealCodeDesc() {
		return dealCodeDesc;
	}

	public void setDealCodeDesc(String dealCodeDesc) {
		this.dealCodeDesc = dealCodeDesc;
	}

	public String getLoanTypeDesc() {
		return loanTypeDesc;
	}

	public void setLoanTypeDesc(String loanTypeDesc) {
		this.loanTypeDesc = loanTypeDesc;
	}

	public boolean isGst() {
		return gst;
	}

	public void setGst(boolean gst) {
		this.gst = gst;
	}

	public List<AssignmentRate> getAssignmentRateList() {
		return assignmentRateList;
	}

	public void setAssignmentRateList(List<AssignmentRate> assignmentRateList) {
		this.assignmentRateList = assignmentRateList;
	}

	public AssignmentRate getAssignmentRate() {
		return assignmentRate;
	}

	public void setAssignmentRate(AssignmentRate assignmentRate) {
		this.assignmentRate = assignmentRate;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public Assignment getBefImage() {
		return this.befImage;
	}

	public void setBefImage(Assignment beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

}
