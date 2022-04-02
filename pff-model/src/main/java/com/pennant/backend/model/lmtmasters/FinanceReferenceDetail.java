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
 * * FileName : FinanceReferenceDetail.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-11-2011 * *
 * Modified Date : 26-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.model.lmtmasters;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>FinanceReferenceDetail table</b>.<br>
 * 
 */
public class FinanceReferenceDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 6569842731762889262L;

	private long finRefDetailId = Long.MIN_VALUE;
	private String finType;
	private String finEvent;
	private int finRefType;
	private long finRefId;
	private String lovDescRefDesc;
	private boolean isActive;
	private String showInStage;
	private String mandInputInStage;
	private String allowInputInStage;
	private boolean lovDescRegenerate;
	private boolean overRide;
	private int overRideValue;
	private String lovDescCodelov;
	private String lovDescNamelov;
	private String lovDescAggImage;
	private boolean lovDescIsRemarksAllowed;
	private long lovDescCheckMinCount;
	private long lovDescCheckMaxCount;

	private String lovDescStgRuleValue; //////////////////// Stage Accounting with Stage Accounting Rules
										//////////////////// change///////////
	private String lovDescElgRuleValue;
	private String lovDescAggReportName;
	private String lovDescAggReportPath;
	private String lovDescAggRuleName;
	private String aggType;
	private int lovDescminScore;
	private boolean lovDescisoverride;
	private int lovDescoverrideScore;
	private String lovValue;
	private FinanceReferenceDetail befImage;
	private LoggedInUser userDetails;

	private List<CheckListDetail> lovDesccheckListDetail;
	private String lovDescElgCalVal;
	private BigDecimal lovDescRuleResult;

	private String lovDescFinCcyCode;
	private String lovDescProductCodeName;
	private String lovDescRuleReturnType;
	private String lovDescFinTypeDescName;
	private boolean allowDeviation;
	private boolean allowWaiver;
	private boolean allowPostpone;
	private boolean allowExpire;
	private String alertType;
	private boolean allowMultiple;
	private String moduleType;
	private boolean resendReq;

	public FinanceReferenceDetail() {
		super();
	}

	public FinanceReferenceDetail(long id) {
		super();
		this.setId(id);
	}

	public FinanceReferenceDetail copyEntity() {
		FinanceReferenceDetail entity = new FinanceReferenceDetail();
		entity.setFinRefDetailId(this.finRefDetailId);
		entity.setFinType(this.finType);
		entity.setFinEvent(this.finEvent);
		entity.setFinRefType(this.finRefType);
		entity.setFinRefId(this.finRefId);
		entity.setLovDescRefDesc(this.lovDescRefDesc);
		entity.setIsActive(this.isActive);
		entity.setShowInStage(this.showInStage);
		entity.setMandInputInStage(this.mandInputInStage);
		entity.setAllowInputInStage(this.allowInputInStage);
		entity.setLovDescRegenerate(this.lovDescRegenerate);
		entity.setOverRide(this.overRide);
		entity.setOverRideValue(this.overRideValue);
		entity.setLovDescCodelov(this.lovDescCodelov);
		entity.setLovDescNamelov(this.lovDescNamelov);
		entity.setLovDescAggImage(this.lovDescAggImage);
		entity.setLovDescIsRemarksAllowed(this.lovDescIsRemarksAllowed);
		entity.setLovDescCheckMinCount(this.lovDescCheckMinCount);
		entity.setLovDescCheckMaxCount(this.lovDescCheckMaxCount);
		entity.setLovDescStgRuleValue(this.lovDescStgRuleValue);
		entity.setLovDescElgRuleValue(this.lovDescElgRuleValue);
		entity.setLovDescAggReportName(this.lovDescAggReportName);
		entity.setLovDescAggReportPath(this.lovDescAggReportPath);
		entity.setLovDescAggRuleName(this.lovDescAggRuleName);
		entity.setAggType(this.aggType);
		entity.setLovDescminScore(this.lovDescminScore);
		entity.setLovDescisoverride(this.lovDescisoverride);
		entity.setLovDescoverrideScore(this.lovDescoverrideScore);
		entity.setLovValue(this.lovValue);
		entity.setBefImage(this.befImage == null ? null : this.befImage.copyEntity());
		entity.setUserDetails(this.userDetails);
		if (lovDesccheckListDetail != null) {
			entity.setLovDesccheckListDetail(new ArrayList<>());
			this.lovDesccheckListDetail.stream()
					.forEach(e -> entity.getLovDesccheckListDetail().add(e == null ? null : e.copyEntity()));
		}
		entity.setLovDescElgCalVal(this.lovDescElgCalVal);
		entity.setLovDescRuleResult(this.lovDescRuleResult);
		entity.setLovDescFinCcyCode(this.lovDescFinCcyCode);
		entity.setLovDescProductCodeName(this.lovDescProductCodeName);
		entity.setLovDescRuleReturnType(this.lovDescRuleReturnType);
		entity.setLovDescFinTypeDescName(this.lovDescFinTypeDescName);
		entity.setAllowDeviation(this.allowDeviation);
		entity.setAllowWaiver(this.allowWaiver);
		entity.setAllowPostpone(this.allowPostpone);
		entity.setAllowExpire(this.allowExpire);
		entity.setAlertType(this.alertType);
		entity.setAllowMultiple(this.allowMultiple);
		entity.setModuleType(this.moduleType);
		entity.setResendReq(this.resendReq);
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
		excludeFields.add("aggType");
		excludeFields.add("allowMultiple");
		excludeFields.add("moduleType");
		excludeFields.add("resendReq");
		return excludeFields;
	}

	// Getter and Setter methods

	// Getter and Setter methods

	public long getId() {
		return finRefDetailId;
	}

	public void setId(long id) {
		this.finRefDetailId = id;
	}

	public long getFinRefDetailId() {
		return finRefDetailId;
	}

	public void setFinRefDetailId(long finRefDetailId) {
		this.finRefDetailId = finRefDetailId;

	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;

	}

	public boolean isOverRide() {
		return overRide;
	}

	public void setOverRide(boolean overRide) {
		this.overRide = overRide;
	}

	public int getOverRideValue() {
		return overRideValue;
	}

	public void setOverRideValue(int overRideValue) {
		this.overRideValue = overRideValue;
	}

	public int getFinRefType() {
		return finRefType;
	}

	public void setFinRefType(int finRefType) {
		this.finRefType = finRefType;

	}

	public long getFinRefId() {
		return finRefId;
	}

	public void setFinRefId(long finRefId) {
		this.finRefId = finRefId;
	}

	public boolean isIsActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getShowInStage() {
		return showInStage;
	}

	public void setShowInStage(String showInStage) {
		this.showInStage = showInStage;
	}

	public String getMandInputInStage() {
		return mandInputInStage;
	}

	public void setMandInputInStage(String mandInputInStage) {
		this.mandInputInStage = mandInputInStage;
	}

	public String getAllowInputInStage() {
		return allowInputInStage;
	}

	public void setAllowInputInStage(String allowInputInStage) {
		this.allowInputInStage = allowInputInStage;
	}

	public boolean isLovDescRegenerate() {
		return lovDescRegenerate;
	}

	public void setLovDescRegenerate(boolean lovDescRegenerate) {
		this.lovDescRegenerate = lovDescRegenerate;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public FinanceReferenceDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(FinanceReferenceDetail beforeImage) {
		this.befImage = beforeImage;

	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getLovDescRefDesc() {
		return lovDescRefDesc;
	}

	public void setLovDescRefDesc(String lovDescRefDesc) {
		this.lovDescRefDesc = lovDescRefDesc;
	}

	public String getLovDescCodelov() {
		return lovDescCodelov;
	}

	public void setLovDescCodelov(String lovDescCodelov) {
		this.lovDescCodelov = lovDescCodelov;
	}

	public String getLovDescNamelov() {
		return lovDescNamelov;
	}

	public void setLovDescNamelov(String lovDescNamelov) {
		this.lovDescNamelov = lovDescNamelov;
	}

	public String getLovDescAggImage() {
		return lovDescAggImage;
	}

	public void setLovDescAggImage(String lovDescAggImage) {
		this.lovDescAggImage = lovDescAggImage;
	}

	public String getLovDescElgRuleValue() {
		return lovDescElgRuleValue;
	}

	public void setLovDescElgRuleValue(String lovDescElgRuleValue) {
		this.lovDescElgRuleValue = lovDescElgRuleValue;
	}

	public void setLovDescAggReportName(String lovDescAggReportName) {
		this.lovDescAggReportName = lovDescAggReportName;
	}

	public String getLovDescAggReportName() {
		return lovDescAggReportName;
	}

	public void setLovDescAggReportPath(String lovDescAggReportPath) {
		this.lovDescAggReportPath = lovDescAggReportPath;
	}

	public String getLovDescAggReportPath() {
		return lovDescAggReportPath;
	}

	public void setLovDesccheckListDetail(List<CheckListDetail> lovDesccheckListDetail) {
		this.lovDesccheckListDetail = lovDesccheckListDetail;
	}

	public List<CheckListDetail> getLovDesccheckListDetail() {
		return lovDesccheckListDetail;
	}

	public boolean getLovDescIsRemarksAllowed() {
		return lovDescIsRemarksAllowed;
	}

	public void setLovDescIsRemarksAllowed(boolean lovDescIsRemarksAllowed) {
		this.lovDescIsRemarksAllowed = lovDescIsRemarksAllowed;
	}

	public long getLovDescCheckMinCount() {
		return lovDescCheckMinCount;
	}

	public void setLovDescCheckMinCount(long lovDescCheckMinCount) {
		this.lovDescCheckMinCount = lovDescCheckMinCount;
	}

	public long getLovDescCheckMaxCount() {
		return lovDescCheckMaxCount;
	}

	public void setLovDescCheckMaxCount(long lovDescCheckMaxCount) {
		this.lovDescCheckMaxCount = lovDescCheckMaxCount;
	}

	public int getLovDescminScore() {
		return lovDescminScore;
	}

	public void setLovDescminScore(int lovDescminScore) {
		this.lovDescminScore = lovDescminScore;
	}

	public boolean isLovDescisoverride() {
		return lovDescisoverride;
	}

	public void setLovDescisoverride(boolean lovDescisoverride) {
		this.lovDescisoverride = lovDescisoverride;
	}

	public int getLovDescoverrideScore() {
		return lovDescoverrideScore;
	}

	public void setLovDescoverrideScore(int lovDescoverrideScore) {
		this.lovDescoverrideScore = lovDescoverrideScore;
	}

	public String getLovDescElgCalVal() {
		return lovDescElgCalVal;
	}

	public void setLovDescElgCalVal(String lovDescElgCalVal) {
		this.lovDescElgCalVal = lovDescElgCalVal;
	}

	public BigDecimal getLovDescRuleResult() {
		return lovDescRuleResult;
	}

	public void setLovDescRuleResult(BigDecimal lovDescRuleResult) {
		this.lovDescRuleResult = lovDescRuleResult;
	}

	public void setLovDescFinCcyCode(String lovDescFinCcyCode) {
		this.lovDescFinCcyCode = lovDescFinCcyCode;
	}

	public String getLovDescFinCcyCode() {
		return lovDescFinCcyCode;
	}

	public void setLovDescProductCodeName(String lovDescProductCodeName) {
		this.lovDescProductCodeName = lovDescProductCodeName;
	}

	public String getLovDescProductCodeName() {
		return lovDescProductCodeName;
	}

	public void setLovDescRuleReturnType(String lovDescRuleReturnType) {
		this.lovDescRuleReturnType = lovDescRuleReturnType;
	}

	public String getLovDescRuleReturnType() {
		return lovDescRuleReturnType;
	}

	public void setLovDescFinTypeDescName(String lovDescFinTypeDescName) {
		this.lovDescFinTypeDescName = lovDescFinTypeDescName;
	}

	public String getLovDescFinTypeDescName() {
		return lovDescFinTypeDescName;
	}

	public String getLovDescAggRuleName() {
		return lovDescAggRuleName;
	}

	public void setLovDescAggRuleName(String lovDescAggRuleName) {
		this.lovDescAggRuleName = lovDescAggRuleName;
	}

	public boolean isAllowDeviation() {
		return allowDeviation;
	}

	public void setAllowDeviation(boolean allowDeviation) {
		this.allowDeviation = allowDeviation;
	}

	public boolean isAllowWaiver() {
		return allowWaiver;
	}

	public void setAllowWaiver(boolean allowWaiver) {
		this.allowWaiver = allowWaiver;
	}

	public boolean isAllowPostpone() {
		return allowPostpone;
	}

	public void setAllowPostpone(boolean allowPostpone) {
		this.allowPostpone = allowPostpone;
	}

	public boolean isAllowExpire() {
		return allowExpire;
	}

	public void setAllowExpire(boolean allowExpire) {
		this.allowExpire = allowExpire;
	}

	public String getAggType() {
		return aggType;
	}

	public void setAggType(String aggType) {
		this.aggType = aggType;
	}

	public String getFinEvent() {
		return finEvent;
	}

	public void setFinEvent(String finEvent) {
		this.finEvent = finEvent;
	}

	public String getAlertType() {
		return alertType;
	}

	public void setAlertType(String alertType) {
		this.alertType = alertType;
	}

	public boolean isAllowMultiple() {
		return allowMultiple;
	}

	public void setAllowMultiple(boolean allowMultiple) {
		this.allowMultiple = allowMultiple;
	}

	public String getModuleType() {
		return moduleType;
	}

	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}

	public String getLovDescStgRuleValue() {
		return lovDescStgRuleValue;
	}

	public void setLovDescStgRuleValue(String lovDescStgRuleValue) {
		this.lovDescStgRuleValue = lovDescStgRuleValue;
	}

	public boolean isResendReq() {
		return resendReq;
	}

	public void setResendReq(boolean resendReq) {
		this.resendReq = resendReq;
	}

}
