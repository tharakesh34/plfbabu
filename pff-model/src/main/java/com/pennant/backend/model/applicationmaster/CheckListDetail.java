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
 * * FileName : CheckListDetail.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-12-2011 * * Modified Date
 * : 12-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.applicationmaster;

import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>CheckListDetail table</b>.<br>
 *
 */
public class CheckListDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -3176600783924484359L;

	private long checkListId = 0;
	private long ansSeqNo = Long.MIN_VALUE;
	private String ansDesc;
	private String ansCond;
	private boolean remarksMand;
	private boolean remarksAllow;
	private boolean docRequired;
	private String docType;
	private String categoryCode;
	private String lovValue;
	private CheckListDetail befImage;
	private LoggedInUser userDetails;

	private String lovDescCheckListDesc;
	private long lovDescCheckMinCount;
	private long lovDescCheckMaxCount;
	private String lovDescRemarks;
	private FinanceReferenceDetail lovDescFinRefDetail;
	private String lovDescUserRole;
	private String lovDescDocType;
	private String lovDescDocCategory;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("categoryCode");
		excludeFields.add("lovDescDocType");
		excludeFields.add("lovDescDocCategory");
		return excludeFields;
	}

	public CheckListDetail() {
		super();
	}

	public CheckListDetail copyEntity() {
		CheckListDetail entity = new CheckListDetail();
		entity.setCheckListId(this.checkListId);
		entity.setAnsSeqNo(this.ansSeqNo);
		entity.setAnsDesc(this.ansDesc);
		entity.setAnsCond(this.ansCond);
		entity.setRemarksMand(this.remarksMand);
		entity.setRemarksAllow(this.remarksAllow);
		entity.setDocRequired(this.docRequired);
		entity.setDocType(this.docType);
		entity.setCategoryCode(this.categoryCode);
		entity.setLovValue(this.lovValue);
		entity.setBefImage(this.befImage == null ? null : this.befImage.copyEntity());
		entity.setUserDetails(this.userDetails);
		entity.setLovDescCheckListDesc(this.lovDescCheckListDesc);
		entity.setLovDescCheckMinCount(this.lovDescCheckMinCount);
		entity.setLovDescCheckMaxCount(this.lovDescCheckMaxCount);
		entity.setLovDescRemarks(this.lovDescRemarks);
		entity.setLovDescFinRefDetail(this.lovDescFinRefDetail == null ? null : this.lovDescFinRefDetail.copyEntity());
		entity.setLovDescUserRole(this.lovDescUserRole);
		entity.setLovDescDocType(this.lovDescDocType);
		entity.setLovDescDocCategory(this.lovDescDocCategory);
		entity.setLovDescFinRefId(this.lovDescFinRefId);
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

	public CheckListDetail(long id) {
		super();
		this.setId(id);
	}

	// Getter and Setter methods

	public long getId() {
		return ansSeqNo;
	}

	public void setId(long id) {
		this.ansSeqNo = id;
	}

	public long getCheckListId() {
		return checkListId;
	}

	public void setCheckListId(long checkListId) {
		this.checkListId = checkListId;
	}

	public long getAnsSeqNo() {
		return ansSeqNo;
	}

	public void setAnsSeqNo(long ansSeqNo) {
		this.ansSeqNo = ansSeqNo;
	}

	public String getAnsDesc() {
		return ansDesc;
	}

	public void setAnsDesc(String ansDesc) {
		this.ansDesc = ansDesc;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public CheckListDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(CheckListDetail beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public boolean isDocRequired() {
		return docRequired;
	}

	public void setDocRequired(boolean docRequired) {
		this.docRequired = docRequired;
	}

	public void setLovDescCheckListDesc(String lovDescCheckListDesc) {
		this.lovDescCheckListDesc = lovDescCheckListDesc;
	}

	public String getLovDescCheckListDesc() {
		return lovDescCheckListDesc;
	}

	public void setLovDescFinRefDetail(FinanceReferenceDetail lovDescFinRefDetail) {
		this.lovDescFinRefDetail = lovDescFinRefDetail;
	}

	public FinanceReferenceDetail getLovDescFinRefDetail() {
		return lovDescFinRefDetail;
	}

	public void setLovDescUserRole(String lovDescUserRole) {
		this.lovDescUserRole = lovDescUserRole;
	}

	public String getLovDescUserRole() {
		return lovDescUserRole;
	}

	public void setLovDescRemarks(String lovDescRemarks) {
		this.lovDescRemarks = lovDescRemarks;
	}

	public String getLovDescRemarks() {
		return lovDescRemarks;
	}

	public void setAnsCond(String ansCond) {
		this.ansCond = ansCond;
	}

	public String getAnsCond() {
		return ansCond;
	}

	public void setRemarksMand(boolean remarksMand) {
		this.remarksMand = remarksMand;
	}

	public boolean isRemarksMand() {
		return remarksMand;
	}

	public void setRemarksAllow(boolean remarksAllow) {
		this.remarksAllow = remarksAllow;
	}

	public boolean isRemarksAllow() {
		return remarksAllow;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getDocType() {
		return docType;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public void setLovDescCheckMinCount(long lovDescCheckMinCount) {
		this.lovDescCheckMinCount = lovDescCheckMinCount;
	}

	public long getLovDescCheckMinCount() {
		return lovDescCheckMinCount;
	}

	public void setLovDescCheckMaxCount(long lovDescCheckMaxCount) {
		this.lovDescCheckMaxCount = lovDescCheckMaxCount;
	}

	public long getLovDescCheckMaxCount() {
		return lovDescCheckMaxCount;
	}

	private long lovDescFinRefId;

	public long getLovDescFinRefId() {
		return lovDescFinRefId;
	}

	public void setLovDescFinRefId(long lovDescFinRefId) {
		this.lovDescFinRefId = lovDescFinRefId;
	}

	public String getLovDescDocType() {
		return lovDescDocType;
	}

	public void setLovDescDocType(String lovDescDocType) {
		this.lovDescDocType = lovDescDocType;
	}

	public String getLovDescDocCategory() {
		return lovDescDocCategory;
	}

	public void setLovDescDocCategory(String lovDescDocCategory) {
		this.lovDescDocCategory = lovDescDocCategory;
	}

}
