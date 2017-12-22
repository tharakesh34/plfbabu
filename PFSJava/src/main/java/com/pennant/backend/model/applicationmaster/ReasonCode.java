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
 * FileName    		:  ReasionCode.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-12-2017    														*
 *                                                                  						*
 * Modified Date    :  19-12-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-12-2017       PENNANT	                 0.1                                            * 
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
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>ReasionCode table</b>.<br>
 *
 */
@XmlType(propOrder = {"id","reasonTypeID","reasonCategoryID","code","description","active"})
@XmlAccessorType(XmlAccessType.FIELD)
public class ReasonCode extends AbstractWorkflowEntity  implements Entity {
private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private long reasonTypeID;
	private long reasonCategoryID;
	private String reasonTypeCode;
	private String reasonCategoryCode;
    private String code;
    private String description;
    private boolean active;
	@XmlTransient
	private boolean newRecord=false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private ReasonCode befImage;
	@XmlTransient
	private  LoggedInUser userDetails;
	
	public boolean isNew() {
		return isNewRecord();
	}

	public ReasonCode() {
		super();
	}

	public ReasonCode(long id) {
		super();
		this.setId(id);
	}
	
	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
			excludeFields.add("reasonTypeCode");
			excludeFields.add("reasonCategoryCode");
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

	public String getReasonTypeCode() {
		return reasonTypeCode;
	}

	public void setReasonTypeCode(String reasonTypeCode) {
		this.reasonTypeCode = reasonTypeCode;
	}

	public String getReasonCategoryCode() {
		return reasonCategoryCode;
	}

	public void setReasonCategoryCode(String reasonCategoryCode) {
		this.reasonCategoryCode = reasonCategoryCode;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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

	public ReasonCode getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(ReasonCode beforeImage){
		this.befImage=beforeImage;
	}

	public  LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails( LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}
	public long getReasonTypeID() {
		return reasonTypeID;
	}

	public void setReasonTypeID(long reasonTypeID) {
		this.reasonTypeID = reasonTypeID;
	}

	public long getReasonCategoryID() {
		return reasonCategoryID;
	}

	public void setReasonCategoryID(long reasonCategoryID) {
		this.reasonCategoryID = reasonCategoryID;
	}

}
