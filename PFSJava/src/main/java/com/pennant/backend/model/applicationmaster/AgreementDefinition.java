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
 * * FileName : AgreementDefinition.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 23-11-2011 * * Modified
 * Date : 23-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 23-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.applicationmaster;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>AgreementDefinition table</b>.<br>
 *
 */
public class AgreementDefinition extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 6547333014929558827L;

	private long aggId = Long.MIN_VALUE;
	private String aggCode;
	private String aggName;
	private String aggDesc;
	private String aggReportName;
	private String aggReportPath;
	private String agrRule;
	private String docType;
	private boolean autoGeneration;
	private boolean autoDownload;
	private String lovDescDocumentType;
	private String lovDescAgrRuleDesc;
	private boolean aggIsActive;
	private String aggtype;
	private String aggImage;
	private String lovValue;
	private AgreementDefinition befImage;
	private LoggedInUser userDetails;
	private String moduleName;
	private boolean allowMultiple;
	private String moduleType;
	private boolean pwdProtected;

	public AgreementDefinition() {
		super();
	}

	public AgreementDefinition(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("pwdProtected");

		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getId() {
		return aggId;
	}

	public void setId(long id) {
		this.aggId = id;
	}

	public long getAggId() {
		return aggId;
	}

	public void setAggId(long aggId) {
		this.aggId = aggId;
	}

	public String getAggCode() {
		return aggCode;
	}

	public void setAggCode(String aggCode) {
		this.aggCode = aggCode;
	}

	public String getAggName() {
		return aggName;
	}

	public void setAggName(String aggName) {
		this.aggName = aggName;
	}

	public String getAggDesc() {
		return aggDesc;
	}

	public void setAggDesc(String aggDesc) {
		this.aggDesc = aggDesc;
	}

	public String getLovDescDocumentType() {
		return lovDescDocumentType;
	}

	public void setLovDescDocumentType(String lovDescDocumentType) {
		this.lovDescDocumentType = lovDescDocumentType;
	}

	public String getAggReportName() {
		return aggReportName;
	}

	public void setAggReportName(String aggReportName) {
		this.aggReportName = aggReportName;
	}

	public String getAggReportPath() {
		return aggReportPath;
	}

	public void setAggReportPath(String aggReportPath) {
		this.aggReportPath = aggReportPath;
	}

	public String getAgrRule() {
		return agrRule;
	}

	public void setAgrRule(String agrRule) {
		this.agrRule = agrRule;
	}

	public String getLovDescAgrRuleDesc() {
		return lovDescAgrRuleDesc;
	}

	public void setLovDescAgrRuleDesc(String lovDescAgrRuleDesc) {
		this.lovDescAgrRuleDesc = lovDescAgrRuleDesc;
	}

	public boolean isAggIsActive() {
		return aggIsActive;
	}

	public void setAggIsActive(boolean aggIsActive) {
		this.aggIsActive = aggIsActive;
	}

	public String getAggtype() {
		return aggtype;
	}

	public void setAggtype(String aggtype) {
		this.aggtype = aggtype;
	}

	public String getAggImage() {
		return aggImage;
	}

	public void setAggImage(String aggImage) {
		this.aggImage = aggImage;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public AgreementDefinition getBefImage() {
		return this.befImage;
	}

	public void setBefImage(AgreementDefinition beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
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

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public boolean isAutoGeneration() {
		return autoGeneration;
	}

	public AgreementDefinition setAutoGeneration(boolean autoGeneration) {
		this.autoGeneration = autoGeneration;
		return befImage;
	}

	public boolean isAutoDownload() {
		return autoDownload;
	}

	public void setAutoDownload(boolean autoDownload) {
		this.autoDownload = autoDownload;
	}

	public boolean isPwdProtected() {
		return pwdProtected;
	}

	public void setPwdProtected(boolean pwdProtected) {
		this.pwdProtected = pwdProtected;
	}
}
