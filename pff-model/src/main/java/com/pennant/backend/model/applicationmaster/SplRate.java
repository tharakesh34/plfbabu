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
 * * FileName : SplRate.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * * Modified Date :
 * 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.applicationmaster;

import java.math.BigDecimal;
import java.util.Date;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>SplRate table</b>.<br>
 *
 */
public class SplRate extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -5025446277606784735L;

	private String sRType;
	private String lovDescSRTypeName;
	private Date sREffDate;
	private Date lastMdfDate;
	private BigDecimal sRRate;
	private boolean delExistingRates;
	private String lovValue;
	private SplRate befImage;
	private LoggedInUser userDetails;

	public SplRate() {
		super();
	}

	public SplRate copyEntity() {
		SplRate entity = new SplRate();
		entity.setSRType(this.sRType);
		entity.setLovDescSRTypeName(this.lovDescSRTypeName);
		entity.setSREffDate(this.sREffDate);
		entity.setLastMdfDate(this.lastMdfDate);
		entity.setSRRate(this.sRRate);
		entity.setDelExistingRates(this.delExistingRates);
		entity.setLovValue(this.lovValue);
		entity.setBefImage(this.befImage == null ? null : this.befImage.copyEntity());
		entity.setUserDetails(this.userDetails);
		entity.setRecordStatus(super.getRecordStatus());
		entity.setRoleCode(super.getRoleCode());
		entity.setNextRoleCode(super.getNextRoleCode());
		entity.setTaskId(super.getTaskId());
		entity.setNextTaskId(super.getNextTaskId());
		entity.setRecordType(super.getRecordType());
		entity.setWorkflowId(super.getWorkflowId());
		entity.setUserAction(super.getUserAction());
		entity.setVersion(super.getVersion());
		entity.setLastMntBy(super.getLastMntBy());
		entity.setLastMntOn(super.getLastMntOn());
		return entity;
	}

	public SplRate(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return sRType;
	}

	public void setId(String id) {
		this.sRType = id;
	}

	public String getSRType() {
		return sRType;
	}

	public void setSRType(String sRType) {
		this.sRType = sRType;
	}

	public String getLovDescSRTypeName() {
		return this.lovDescSRTypeName;
	}

	public void setLovDescSRTypeName(String lovDescSRTypeName) {
		this.lovDescSRTypeName = lovDescSRTypeName;
	}

	public Date getSREffDate() {
		return sREffDate;
	}

	public void setSREffDate(Date sREffDate) {
		this.sREffDate = sREffDate;
	}

	public BigDecimal getSRRate() {
		return sRRate;
	}

	public void setSRRate(BigDecimal sRRate) {
		this.sRRate = sRRate;
	}

	public boolean isDelExistingRates() {
		return delExistingRates;
	}

	public void setDelExistingRates(boolean delExistingRates) {
		this.delExistingRates = delExistingRates;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public SplRate getBefImage() {
		return this.befImage;
	}

	public void setBefImage(SplRate beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Date getLastMdfDate() {
		return lastMdfDate;
	}

	public void setLastMdfDate(Date lastMdfDate) {
		this.lastMdfDate = lastMdfDate;
	}
}
