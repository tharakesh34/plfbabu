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
 * * FileName : ProfitCenter.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 22-04-2017 * * Modified Date :
 * 22-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 22-04-2017 PENNANT 0.1 * * * * * * * * *
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

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>ProfitCenter table</b>.<br>
 *
 */
@XmlType(propOrder = { "profitCenterID", "profitCenterCode", "profitCenterDesc", "active" })
@XmlAccessorType(XmlAccessType.FIELD)
public class ProfitCenter extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private Long profitCenterID = Long.MIN_VALUE;
	private String profitCenterCode;
	private String profitCenterDesc;
	private boolean active;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private ProfitCenter befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	public ProfitCenter() {
		super();
	}

	public ProfitCenter(Long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		return excludeFields;
	}

	public Long getId() {
		return profitCenterID;
	}

	public void setId(Long id) {
		this.profitCenterID = id;
	}

	public Long getProfitCenterID() {
		return profitCenterID;
	}

	public void setProfitCenterID(Long profitCenterID) {
		this.profitCenterID = profitCenterID;
	}

	public String getProfitCenterCode() {
		return profitCenterCode;
	}

	public void setProfitCenterCode(String profitCenterCode) {
		this.profitCenterCode = profitCenterCode;
	}

	public String getProfitCenterDesc() {
		return profitCenterDesc;
	}

	public void setProfitCenterDesc(String profitCenterDesc) {
		this.profitCenterDesc = profitCenterDesc;
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

	public ProfitCenter getBefImage() {
		return this.befImage;
	}

	public void setBefImage(ProfitCenter beforeImage) {
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
