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
 * FileName    		:  QueryDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-05-2018    														*
 *                                                                  						*
 * Modified Date    :  09-05-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-05-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.model.loanquery;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>QueryDetail table</b>.<br>
 *
 */
@XmlType(propOrder = { "finReference", "id", "usrLogin", "raisedOn", "description", "qryNotes", "status", "raisedBy",
		"code", "categoryId", "assignedRole", "notifyTo", "responsNotes", "responseBy", "responseOn", "closerNotes",
		"closerBy", "closerOn" })

@XmlAccessorType(XmlAccessType.FIELD)
public class QueryDetail extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private String finReference;
	// private String finReferenceName;
	private long categoryId = 0;
	private String categoryIdName;
	private String qryNotes;
	private String assignedRole;
	private String notifyTo;
	private String status;
	private long raisedBy = 0;
	private String usrLogin;

	// @XmlJavaTypeAdapter(TimestampFormatterAdapter.class)
	private Timestamp raisedOn;
	private String responsNotes;
	private long responseBy = 0;
	// @XmlJavaTypeAdapter(TimestampFormatterAdapter.class)
	private Timestamp responseOn;
	private String closerNotes;
	private String code;
	private String description;
	private long closerBy = 0;
	// @XmlJavaTypeAdapter(TimestampFormatterAdapter.class)
	private Timestamp closerOn;
	@XmlTransient
	private boolean newRecord = false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private QueryDetail befImage;
	@XmlTransient
	private LoggedInUser userDetails;
	private String categoryCode;
	private String responseUser;
	private String closerUser;
	private String categoryDescription;
	private String module;
	private List<DocumentDetails> documentDetailsList = new ArrayList<>();

	public boolean isNew() {
		return isNewRecord();
	}

	public QueryDetail() {
		super();
	}

	public QueryDetail(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("categoryIdName");
		excludeFields.add("categoryCode");
		excludeFields.add("documentDetailsList");
		excludeFields.add("responseUser");
		excludeFields.add("closerUser");
		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryIdName() {
		return this.categoryIdName;
	}

	public void setCategoryIdName(String categoryIdName) {
		this.categoryIdName = categoryIdName;
	}

	public String getQryNotes() {
		return qryNotes;
	}

	public void setQryNotes(String qryNotes) {
		this.qryNotes = qryNotes;
	}

	public String getAssignedRole() {
		return assignedRole;
	}

	public void setAssignedRole(String assignedRole) {
		this.assignedRole = assignedRole;
	}

	public String getNotifyTo() {
		return notifyTo;
	}

	public void setNotifyTo(String notifyTo) {
		this.notifyTo = notifyTo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getRaisedBy() {
		return raisedBy;
	}

	public void setRaisedBy(long raisedBy) {
		this.raisedBy = raisedBy;
	}

	public Timestamp getRaisedOn() {
		return raisedOn;
	}

	public void setRaisedOn(Timestamp raisedOn) {
		this.raisedOn = raisedOn;
	}

	public String getResponsNotes() {
		return responsNotes;
	}

	public void setResponsNotes(String responsNotes) {
		this.responsNotes = responsNotes;
	}

	public long getResponseBy() {
		return responseBy;
	}

	public void setResponseBy(long responseBy) {
		this.responseBy = responseBy;
	}

	public Timestamp getResponseOn() {
		return responseOn;
	}

	public void setResponseOn(Timestamp responseOn) {
		this.responseOn = responseOn;
	}

	public String getCloserNotes() {
		return closerNotes;
	}

	public void setCloserNotes(String closerNotes) {
		this.closerNotes = closerNotes;
	}

	public long getCloserBy() {
		return closerBy;
	}

	public void setCloserBy(long closerBy) {
		this.closerBy = closerBy;
	}

	public Timestamp getCloserOn() {
		return closerOn;
	}

	public void setCloserOn(Timestamp closerOn) {
		this.closerOn = closerOn;
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

	public QueryDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(QueryDetail beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
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

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getUsrLogin() {
		return usrLogin;
	}

	public void setUsrLogin(String usrLogin) {
		this.usrLogin = usrLogin;
	}

	public String getCategoryDescription() {
		return categoryDescription;
	}

	public void setCategoryDescription(String categoryDescription) {
		this.categoryDescription = categoryDescription;
	}

	public List<DocumentDetails> getDocumentDetailsList() {
		return documentDetailsList;
	}

	public void setDocumentDetailsList(List<DocumentDetails> documentDetailsList) {
		this.documentDetailsList = documentDetailsList;
	}

	public String getResponseUser() {
		return responseUser;
	}

	public void setResponseUser(String responseUser) {
		this.responseUser = responseUser;
	}

	public String getCloserUser() {
		return closerUser;
	}

	public void setCloserUser(String closerUser) {
		this.closerUser = closerUser;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

}
