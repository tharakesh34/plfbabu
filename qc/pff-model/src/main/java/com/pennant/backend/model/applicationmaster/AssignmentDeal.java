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
 * FileName    		:  AssignmentDeal.java                                                   * 	  
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>AssignmentDeal table</b>.<br>
 *
 */
@XmlType(propOrder = { "id", "code", "description", "partnerCode", "active" })
@XmlAccessorType(XmlAccessType.FIELD)
public class AssignmentDeal extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private String code;
	private String description;
	private Long partnerCode;
	private String partnerCodeName;
	private String partnerCodeDesc;

	private List<AssignmentDealLoanType> assignmentDealLoanType = new ArrayList<>();
	private List<AssignmentDealExcludedFee> assignmentDealExcludedFee = new ArrayList<>();
	private boolean active = true;
	@XmlTransient
	private boolean newRecord = false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private AssignmentDeal befImage;
	@XmlTransient
	private LoggedInUser userDetails;
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

	public boolean isNew() {
		return isNewRecord();
	}

	public AssignmentDeal() {
		super();
	}

	public AssignmentDeal(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("partnerCodeName");
		excludeFields.add("auditDetailMap");
		excludeFields.add("assignmentDealLoanType");
		excludeFields.add("assignmentDealExcludedFee");
		excludeFields.add("partnerCodeDesc");
		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getPartnerCode() {
		return partnerCode;
	}

	public void setPartnerCode(Long partnerCode) {
		this.partnerCode = partnerCode;
	}

	public String getPartnerCodeName() {
		return this.partnerCodeName;
	}

	public void setPartnerCodeName(String partnerCodeName) {
		this.partnerCodeName = partnerCodeName;
	}

	public List<AssignmentDealLoanType> getAssignmentDealLoanType() {
		return assignmentDealLoanType;
	}

	public void setAssignmentDealLoanType(List<AssignmentDealLoanType> assignmentDealLoanType) {
		this.assignmentDealLoanType = assignmentDealLoanType;
	}

	public List<AssignmentDealExcludedFee> getAssignmentDealExcludedFee() {
		return assignmentDealExcludedFee;
	}

	public void setAssignmentDealExcludedFee(List<AssignmentDealExcludedFee> assignmentDealExcludedFee) {
		this.assignmentDealExcludedFee = assignmentDealExcludedFee;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
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

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public AssignmentDeal getBefImage() {
		return this.befImage;
	}

	public void setBefImage(AssignmentDeal beforeImage) {
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

	public String getPartnerCodeDesc() {
		return partnerCodeDesc;
	}

	public void setPartnerCodeDesc(String partnerCodeDesc) {
		this.partnerCodeDesc = partnerCodeDesc;
	}

}
