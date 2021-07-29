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
 * * FileName : NPAProvisionDetail.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 04-05-2020 * * Modified
 * Date : 04-05-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 04-05-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.applicationmaster;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>NPAProvisionDetail table</b>.<br>
 *
 */
public class NPAProvisionDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private long headerId = 0;
	private String headerIdName;
	private boolean nPAActive = false;
	private int dPDdays;
	private String nPARepayApprtnmnt;
	private BigDecimal intSecPerc = BigDecimal.ZERO;
	private BigDecimal intUnSecPerc = BigDecimal.ZERO;
	private BigDecimal regSecPerc = BigDecimal.ZERO;
	private BigDecimal regUnSecPerc = BigDecimal.ZERO;
	private String entity;
	private String finType;
	private String lovValue;
	private NPAProvisionDetail befImage;
	private LoggedInUser userDetails;

	private long assetClassificationId = 0;
	private String assetCode;
	private int assetStageOrder;
	private boolean newPrvDetail;
	private Long ruleId;
	private boolean active;

	public NPAProvisionDetail() {
		super();
	}

	public NPAProvisionDetail(long id) {
		super();
		this.setId(id);
	}

	public NPAProvisionDetail copyEntity() {
		NPAProvisionDetail entity = new NPAProvisionDetail();
		entity.setId(this.id);
		entity.setHeaderId(this.headerId);
		entity.setHeaderIdName(this.headerIdName);
		entity.setNPAActive(this.nPAActive);
		entity.setDPDdays(this.dPDdays);
		entity.setNPARepayApprtnmnt(this.nPARepayApprtnmnt);
		entity.setIntSecPerc(this.intSecPerc);
		entity.setIntUnSecPerc(this.intUnSecPerc);
		entity.setRegSecPerc(this.regSecPerc);
		entity.setRegUnSecPerc(this.regUnSecPerc);
		entity.setEntity(this.entity);
		entity.setFinType(this.finType);
		entity.setNewRecord(super.isNewRecord());
		entity.setLovValue(this.lovValue);
		entity.setBefImage(this.befImage == null ? null : this.befImage.copyEntity());
		entity.setUserDetails(this.userDetails);
		entity.setAssetClassificationId(this.assetClassificationId);
		entity.setAssetCode(this.assetCode);
		entity.setAssetStageOrder(this.assetStageOrder);
		entity.setNewPrvDetail(this.newPrvDetail);
		entity.setRuleId(this.ruleId);
		entity.setActive(this.active);
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
		excludeFields.add("headerIdName");
		excludeFields.add("assetCode");
		excludeFields.add("assetStageOrder");
		excludeFields.add("entity");
		excludeFields.add("finType");
		excludeFields.add("newPrvDetail");
		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getHeaderId() {
		return headerId;
	}

	public void setHeaderId(long headerId) {
		this.headerId = headerId;
	}

	public String getHeaderIdName() {
		return this.headerIdName;
	}

	public void setHeaderIdName(String headerIdName) {
		this.headerIdName = headerIdName;
	}

	public boolean isNPAActive() {
		return nPAActive;
	}

	public void setNPAActive(boolean nPAActive) {
		this.nPAActive = nPAActive;
	}

	public int getDPDdays() {
		return dPDdays;
	}

	public void setDPDdays(int dPDdays) {
		this.dPDdays = dPDdays;
	}

	public String getNPARepayApprtnmnt() {
		return nPARepayApprtnmnt;
	}

	public void setNPARepayApprtnmnt(String nPARepayApprtnmnt) {
		this.nPARepayApprtnmnt = nPARepayApprtnmnt;
	}

	public BigDecimal getIntSecPerc() {
		return intSecPerc;
	}

	public void setIntSecPerc(BigDecimal intSecPerc) {
		this.intSecPerc = intSecPerc;
	}

	public BigDecimal getIntUnSecPerc() {
		return intUnSecPerc;
	}

	public void setIntUnSecPerc(BigDecimal intUnSecPerc) {
		this.intUnSecPerc = intUnSecPerc;
	}

	public BigDecimal getRegSecPerc() {
		return regSecPerc;
	}

	public void setRegSecPerc(BigDecimal regSecPerc) {
		this.regSecPerc = regSecPerc;
	}

	public BigDecimal getRegUnSecPerc() {
		return regUnSecPerc;
	}

	public void setRegUnSecPerc(BigDecimal regUnSecPerc) {
		this.regUnSecPerc = regUnSecPerc;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public NPAProvisionDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(NPAProvisionDetail beforeImage) {
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

	public String getAssetCode() {
		return assetCode;
	}

	public void setAssetCode(String assetCode) {
		this.assetCode = assetCode;
	}

	public int getAssetStageOrder() {
		return assetStageOrder;
	}

	public void setAssetStageOrder(int assetStageOrder) {
		this.assetStageOrder = assetStageOrder;
	}

	public long getAssetClassificationId() {
		return assetClassificationId;
	}

	public void setAssetClassificationId(long assetClassificationId) {
		this.assetClassificationId = assetClassificationId;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public boolean isNewPrvDetail() {
		return newPrvDetail;
	}

	public void setNewPrvDetail(boolean newPrvDetail) {
		this.newPrvDetail = newPrvDetail;
	}

	public Long getRuleId() {
		return ruleId;
	}

	public void setRuleId(Long ruleId) {
		this.ruleId = ruleId;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
