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
 * * FileName : IRRCode.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-06-2017 * * Modified Date :
 * 21-06-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-06-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.applicationmaster;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>IRRCode table</b>.<br>
 *
 */
public class IRRCode extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long iRRID = Long.MIN_VALUE;
	private String iRRCode;
	private String iRRCodeDesc;
	private boolean active;
	private String lovValue;
	private IRRCode befImage;
	private LoggedInUser userDetails;
	private List<IRRFeeType> irrFeeTypesList = null;
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

	public IRRCode() {
		super();
	}

	public IRRCode(long id) {
		super();
		this.setId(id);
	}

	public IRRCode copyEntity() {
		IRRCode entity = new IRRCode();
		entity.setIRRID(this.iRRID);
		entity.setIRRCode(this.iRRCode);
		entity.setIRRCodeDesc(this.iRRCodeDesc);
		entity.setActive(this.active);
		entity.setLovValue(this.lovValue);
		entity.setBefImage(this.befImage == null ? null : this.befImage.copyEntity());
		entity.setUserDetails(this.userDetails);
		if (irrFeeTypesList != null) {
			entity.setIrrFeeTypesList(new ArrayList<>());
			this.irrFeeTypesList.stream()
					.forEach(e -> entity.getIrrFeeTypesList().add(e == null ? null : e.copyEntity()));
		}
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

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		return excludeFields;
	}

	public long getId() {
		return iRRID;
	}

	public void setId(long id) {
		this.iRRID = id;
	}

	public long getIRRID() {
		return iRRID;
	}

	public void setIRRID(long iRRID) {
		this.iRRID = iRRID;
	}

	public String getIRRCode() {
		return iRRCode;
	}

	public void setIRRCode(String iRRCode) {
		this.iRRCode = iRRCode;
	}

	public String getIRRCodeDesc() {
		return iRRCodeDesc;
	}

	public void setIRRCodeDesc(String iRRCodeDesc) {
		this.iRRCodeDesc = iRRCodeDesc;
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

	public IRRCode getBefImage() {
		return this.befImage;
	}

	public void setBefImage(IRRCode beforeImage) {
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

	public List<IRRFeeType> getIrrFeeTypesList() {
		return irrFeeTypesList;
	}

	public void setIrrFeeTypesList(List<IRRFeeType> irrFeeTypesList) {
		this.irrFeeTypesList = irrFeeTypesList;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

}
