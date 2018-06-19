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
 * FileName    		:  RelationshipOfficer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-09-2011    														*
 *                                                                  						*
 * Modified Date    :  12-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-09-2011       Pennant	                 0.1                                            * 
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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>RelationshipOfficer table</b>.<br>
 *
 */
public class RelationshipOfficer extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -6954546690866975110L;
	
	private String rOfficerCode;
	private String rOfficerDesc;
	private String rOfficerDeptCode;
	private String lovDescROfficerDeptCodeName;
	private boolean rOfficerIsActive;
	private String grade;
	private String mobileNO;
	private Date   dateOfJoin;
	private String genDesignation;
	private String gendesgdesc;
	private boolean newRecord;
	private String lovValue;
	private RelationshipOfficer befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public RelationshipOfficer() {
		super();
	}

	public RelationshipOfficer(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("gendesgdesc");
		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getId() {
		return rOfficerCode;
	}
	
	public void setId (String id) {
		this.rOfficerCode = id;
	}
	
	public String getROfficerCode() {
		return rOfficerCode;
	}
	public void setROfficerCode(String rOfficerCode) {
		this.rOfficerCode = rOfficerCode;
	}
	
	public String getROfficerDesc() {
		return rOfficerDesc;
	}
	public void setROfficerDesc(String rOfficerDesc) {
		this.rOfficerDesc = rOfficerDesc;
	}
		
	public String getROfficerDeptCode() {
		return rOfficerDeptCode;
	}
	public void setROfficerDeptCode(String rOfficerDeptCode) {
		this.rOfficerDeptCode = rOfficerDeptCode;
	}

	public String getLovDescROfficerDeptCodeName() {
		return this.lovDescROfficerDeptCodeName;
	}
	public void setLovDescROfficerDeptCodeName (String lovDescROfficerDeptCodeName) {
		this.lovDescROfficerDeptCodeName = lovDescROfficerDeptCodeName;
	}
	
	public boolean isROfficerIsActive() {
		return rOfficerIsActive;
	}
	public void setROfficerIsActive(boolean rOfficerIsActive) {
		this.rOfficerIsActive = rOfficerIsActive;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getMobileNO() {
		return mobileNO;
	}

	public void setMobileNO(String mobileNO) {
		this.mobileNO = mobileNO;
	}

	public Date getDateOfJoin() {
		return dateOfJoin;
	}

	public void setDateOfJoin(Date dateOfJoin) {
		this.dateOfJoin = dateOfJoin;
	}

	public String getGenDesignation() {
		return genDesignation;
	}

	public void setGenDesignation(String genDesignation) {
		this.genDesignation = genDesignation;
	}

	public String getGendesgdesc() {
		return gendesgdesc;
	}

	public void setGendesgdesc(String gendesgdesc) {
		this.gendesgdesc = gendesgdesc;
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

	public RelationshipOfficer getBefImage(){
		return this.befImage;
	}
	public void setBefImage(RelationshipOfficer beforeImage){
		this.befImage=beforeImage;
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
}
