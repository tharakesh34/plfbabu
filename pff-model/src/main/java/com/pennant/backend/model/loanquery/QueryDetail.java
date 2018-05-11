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
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>QueryDetail table</b>.<br>
 *
 */
@XmlType(propOrder = {"id","finReference","categoryId","qryNotes","assignedRole","notifyTo","status","raisedBy","raisedOn","responsNotes"
,"responseBy","responseOn","closerNotes","closerBy","closerOn"})
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryDetail extends AbstractWorkflowEntity  implements Entity {
private static final long serialVersionUID = 1L;

private long id = Long.MIN_VALUE;
private String finReference;
//private String finReferenceName;
private long categoryId= 0;
private String categoryIdName;
private String qryNotes;
private String assignedRole;
private String notifyTo;
private String status;
private long raisedBy= 0;
//	@XmlJavaTypeAdapter(TimestampFormatterAdapter.class)
private Timestamp raisedOn;
private String responsNotes;
private long responseBy= 0;
//	@XmlJavaTypeAdapter(TimestampFormatterAdapter.class)
private Timestamp responseOn;
private String closerNotes;

private long closerBy= 0;
//	@XmlJavaTypeAdapter(TimestampFormatterAdapter.class)
private Timestamp closerOn;
	@XmlTransient
	private boolean newRecord=false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private QueryDetail befImage;
	@XmlTransient
	private  LoggedInUser userDetails;
	private String categoryCode;
	private String categoryDescription;
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
	
	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
			//excludeFields.add("finReferenceName");
			excludeFields.add("categoryIdName");
			excludeFields.add("categoryCode");
	return excludeFields;
	}

	public long getId() {
		return id;
	}
	
	public void setId (long id) {
		this.id = id;
	}
	
	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}
	/*
	public String getFinReferenceName() {
		return this.finReferenceName;
	}

	public void setFinReferenceName (String finReferenceName) {
		this.finReferenceName = finReferenceName;
	}
	*/
	public long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}
	public String getCategoryIdName() {
		return this.categoryIdName;
	}

	public void setCategoryIdName (String categoryIdName) {
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

	public QueryDetail getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(QueryDetail beforeImage){
		this.befImage=beforeImage;
	}

	public  LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails( LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getCategoryDescription() {
		return categoryDescription;
	}

	public void setCategoryDescription(String categoryDescription) {
		this.categoryDescription = categoryDescription;
	}
}
