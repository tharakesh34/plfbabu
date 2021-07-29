/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : CustTypePANMapping.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * * Modified
 * Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.systemmasters;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>CustTypePANMapping table</b>.<br>
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CustTypePANMapping extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -1472467289111692722L;

	private long mappingID = Long.MIN_VALUE;
	private String custCategory;
	private String custType;
	private String custTypeDesc;
	private String panLetter;
	private boolean active;
	private String lovValue;
	private CustTypePANMapping befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("custTypeDesc");
		return excludeFields;
	}

	public CustTypePANMapping() {
		super();
	}

	public long getId() {
		return mappingID;
	}

	public void setId(long id) {
		this.mappingID = id;
	}

	public long getMappingID() {
		return mappingID;
	}

	public void setMappingID(long mappingID) {
		this.mappingID = mappingID;
	}

	public String getCustCategory() {
		return custCategory;
	}

	public void setCustCategory(String custCategory) {
		this.custCategory = custCategory;
	}

	public String getCustType() {
		return custType;
	}

	public void setCustType(String custType) {
		this.custType = custType;
	}

	public String getPanLetter() {
		return panLetter;
	}

	public void setPanLetter(String panLetter) {
		this.panLetter = panLetter;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public CustTypePANMapping getBefImage() {
		return befImage;
	}

	public void setBefImage(CustTypePANMapping befImage) {
		this.befImage = befImage;
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

	public String getCustTypeDesc() {
		return custTypeDesc;
	}

	public void setCustTypeDesc(String custTypeDesc) {
		this.custTypeDesc = custTypeDesc;
	}

}