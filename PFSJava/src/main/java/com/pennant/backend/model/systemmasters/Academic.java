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
 * FileName    		:  Academic.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.systemmasters;

import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>Academic table</b>.<br>
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Academic extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = -1472467289111692722L;

	private long academicID = Long.MIN_VALUE;
	private String academicLevel;
	private String academicDecipline;
	private String academicDesc;
	private boolean newRecord;
	private String lovValue;
	private Academic befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public Academic() {
		super();
	}

	public Academic(long id) {
		super();
		this.setId(id);
	}

	public long getId() {
		return academicID;
	}

	public void setId(long id) {
		this.academicID = id;
	}

	public String getAcademicLevel() {
		return academicLevel;
	}

	public void setAcademicLevel(String academicLevel) {
		this.academicLevel = academicLevel;
	}

	public String getAcademicDecipline() {
		return academicDecipline;
	}

	public void setAcademicDecipline(String academicDecipline) {
		this.academicDecipline = academicDecipline;
	}

	public long getAcademicID() {
		return academicID;
	}

	public void setAcademicID(long academicID) {
		this.academicID = academicID;
	}

	public String getAcademicDesc() {
		return academicDesc;
	}

	public void setAcademicDesc(String academicDesc) {
		this.academicDesc = academicDesc;
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

	public Academic getBefImage() {
		return this.befImage;
	}

	public void setBefImage(Academic beforeImage) {
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
}
