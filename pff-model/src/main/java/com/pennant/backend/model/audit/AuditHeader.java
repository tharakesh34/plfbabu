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
 *
 * FileName : AuditHeader.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 26-04-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.audit;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;

import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class AuditHeader implements java.io.Serializable {
	private static final long serialVersionUID = -53442181146054373L;

	private long auditId = Long.MIN_VALUE;
	private Timestamp auditDate;
	private long auditUsrId;
	private String auditModule;
	private String auditBranchCode;
	private String auditDeptCode;
	private String auditTranType;
	private String auditCustNo;
	private String auditAccNo;
	private String auditLoanNo;
	private String auditReference;
	private String auditSystemIP;
	private String auditSessionID;
	private boolean overide;
	private String auditInfo;
	private String auditOveride;
	private String auditError;
	private boolean auditPrinted = false;
	private boolean auditRecovered = false;
	private String auditErrorForRecocvery;
	private List<ErrorDetail> infoMessage;
	private List<ErrorDetail> overideMessage;
	private List<ErrorDetail> errorMessage;
	private Object modelData;
	private AuditDetail auditDetail;
	private List<AuditDetail> auditDetails;
	private int overideCount = 0;
	private String usrLanguage;
	private Map<String, List<ErrorDetail>> overideMap = new HashMap<String, List<ErrorDetail>>();
	private int processStatus = 0;
	private boolean nextProcess = false;
	private Object apiHeader;
	private boolean deleteNotes;
	private boolean processCompleted;

	public AuditHeader() {
		super();
	}

	public ErrorDetail getCopy(ErrorDetail error) {
		ErrorDetail err = new ErrorDetail();
		err.setCode(error.getCode());
		err.setLanguage(error.getLanguage());
		err.setSeverity(error.getSeverity());
		err.setMessage(error.getMessage());
		err.setExtendedMessage(error.getExtendedMessage());
		err.setNewRecord(error.isNewRecord());
		err.setBefImage(error.getBefImage() == null ? null : getCopy(error));
		err.setUserDetails(error.getUserDetails());
		err.setField(error.getField());
		err.setParameters(error.getParameters());
		err.setFieldValues(error.getFieldValues());
		err.setOveride(error.isOveride());
		err.setFieldName(error.getFieldName());
		err.setComponentName(error.getComponentName());

		return err;
	}

	public List<ErrorDetail> getCopy(List<ErrorDetail> list) {
		List<ErrorDetail> errors = new ArrayList<>();

		if (list == null) {
			return errors;
		}

		list.forEach(error -> errors.add(getCopy(error)));

		return errors;
	}

	public AuditHeader copyEntity() {
		AuditHeader entity = new AuditHeader();
		entity.setAuditId(this.auditId);
		entity.setAuditDate(this.auditDate);
		entity.setAuditUsrId(this.auditUsrId);
		entity.setAuditModule(this.auditModule);
		entity.setAuditBranchCode(this.auditBranchCode);
		entity.setAuditDeptCode(this.auditDeptCode);
		entity.setAuditTranType(this.auditTranType);
		entity.setAuditCustNo(this.auditCustNo);
		entity.setAuditAccNo(this.auditAccNo);
		entity.setAuditLoanNo(this.auditLoanNo);
		entity.setAuditReference(this.auditReference);
		entity.setAuditSystemIP(this.auditSystemIP);
		entity.setAuditSessionID(this.auditSessionID);
		entity.setOveride(this.overide);
		entity.setAuditInfo(this.auditInfo);
		entity.setAuditOveride(this.auditOveride);
		entity.setAuditError(this.auditError);
		entity.setAuditPrinted(this.auditPrinted);
		entity.setAuditRecovered(this.auditRecovered);
		entity.setAuditErrorForRecocvery(this.auditErrorForRecocvery);

		if (infoMessage != null) {
			this.infoMessage.stream().forEach(e -> entity.getInfoMessage().add(e == null ? null : getCopy(e)));
		}

		if (overideMessage != null) {
			this.overideMessage.stream().forEach(e -> entity.getOverideMessage().add(e == null ? null : getCopy(e)));
		}

		if (errorMessage != null) {
			this.errorMessage.stream().forEach(e -> entity.getErrorMessage().add(e == null ? null : getCopy(e)));
		}

		entity.setAuditDetail(this.auditDetail == null ? null : this.auditDetail.copyEntity());

		if (auditDetails != null) {
			this.auditDetails.stream().forEach(e -> entity.getAuditDetails().add(e == null ? null : e.copyEntity()));
		}

		entity.setOverideCount(this.overideCount);
		entity.setUsrLanguage(this.usrLanguage);

		entity.setProcessStatus(this.processStatus);
		entity.setNextProcess(this.nextProcess);
		entity.setDeleteNotes(this.deleteNotes);
		entity.setProcessCompleted(this.processCompleted);
		return entity;
	}

	// New methods for copying the properties of AuditHeader
	public AuditHeader getNewCopyInstance() {
		AuditHeader auditHeader = new AuditHeader();
		BeanUtils.copyProperties(this, auditHeader);
		if (this.auditDate != null) {
			BeanUtils.copyProperties(this.auditDate, auditHeader.auditDate);
		}
		if (this.infoMessage != null) {
			auditHeader.setInfoMessage(new ArrayList<ErrorDetail>());
			BeanUtils.copyProperties(this.infoMessage, auditHeader.infoMessage);
		}
		if (this.overideMessage != null) {
			auditHeader.setOverideMessage(new ArrayList<ErrorDetail>());
			BeanUtils.copyProperties(this.overideMessage, auditHeader.overideMessage);
			auditHeader.setAuditOveride(this.getAuditOveride());
		}
		if (this.errorMessage != null) {
			auditHeader.setErrorMessage(new ArrayList<ErrorDetail>());
			BeanUtils.copyProperties(this.errorMessage, auditHeader.errorMessage);
		}
		if (this.modelData != null) {
			BeanUtils.copyProperties(this.modelData, auditHeader.modelData);
		}
		AuditDetail auditDetail = new AuditDetail();
		auditHeader.auditDetail = auditDetail.getNewCopyInstance();
		if (this.auditDetail != null) {
			auditHeader.setAuditDetail(new AuditDetail());
			BeanUtils.copyProperties(this.auditDetail, auditHeader.auditDetail);
		}
		if (this.overideMap != null) {
			auditHeader.setOverideMap(new HashMap<String, List<ErrorDetail>>());
			for (Entry<String, List<ErrorDetail>> moveMap : this.overideMap.entrySet()) {
				auditHeader.overideMap.put(moveMap.getKey(), moveMap.getValue());
			}
		}
		if (this.auditDetails != null) {
			auditHeader.setAuditDetails(new ArrayList<AuditDetail>());
			auditHeader.auditDetails.addAll(this.auditDetails);
		}
		return auditHeader;
	}

	public long getId() {
		return auditId;
	}

	public void setId(long id) {
		this.auditId = id;
	}

	public long getAuditId() {
		return auditId;
	}

	public void setAuditId(long auditId) {
		this.auditId = auditId;
	}

	public Timestamp getAuditDate() {
		return auditDate;
	}

	public void setAuditDate(Timestamp auditDate) {
		this.auditDate = auditDate;
	}

	public long getAuditUsrId() {
		return auditUsrId;
	}

	public void setAuditUsrId(long auditUsrId) {
		this.auditUsrId = auditUsrId;
	}

	public String getAuditModule() {
		return auditModule;
	}

	public void setAuditModule(String auditModule) {
		this.auditModule = auditModule;
	}

	public String getAuditBranchCode() {
		return auditBranchCode;
	}

	public void setAuditBranchCode(String auditBranchCode) {
		this.auditBranchCode = auditBranchCode;
	}

	public String getAuditDeptCode() {
		return auditDeptCode;
	}

	public void setAuditDeptCode(String auditDeptCode) {
		this.auditDeptCode = auditDeptCode;
	}

	public String getAuditTranType() {
		return auditTranType;
	}

	public void setAuditTranType(String auditTranType) {
		this.auditTranType = auditTranType;
	}

	public String getAuditCustNo() {
		return auditCustNo;
	}

	public void setAuditCustNo(String auditCustNo) {
		this.auditCustNo = auditCustNo;
	}

	public String getAuditAccNo() {
		return auditAccNo;
	}

	public void setAuditAccNo(String auditAccNo) {
		this.auditAccNo = auditAccNo;
	}

	public String getAuditLoanNo() {
		return auditLoanNo;
	}

	public void setAuditLoanNo(String auditLoanNo) {
		this.auditLoanNo = auditLoanNo;
	}

	public String getAuditSystemIP() {
		return auditSystemIP;
	}

	public void setAuditSystemIP(String auditSystemIP) {
		this.auditSystemIP = auditSystemIP;
	}

	public String getAuditReference() {
		return auditReference;
	}

	public void setAuditReference(String auditReference) {
		this.auditReference = auditReference;
	}

	public String getAuditSessionID() {
		return auditSessionID;
	}

	public void setAuditSessionID(String auditSessionID) {
		this.auditSessionID = auditSessionID;
	}

	public boolean isAuditPrinted() {
		return auditPrinted;
	}

	public void setAuditPrinted(boolean auditPrinted) {
		this.auditPrinted = auditPrinted;
	}

	public boolean isAuditRecovered() {
		return auditRecovered;
	}

	public void setAuditRecovered(boolean auditRecovered) {
		this.auditRecovered = auditRecovered;
	}

	public String getAuditErrorForRecocvery() {
		return auditErrorForRecocvery;
	}

	public void setAuditErrorForRecocvery(String auditErrorForRecocvery) {
		this.auditErrorForRecocvery = auditErrorForRecocvery;
	}

	public Object getModelData() {
		return modelData;
	}

	public void setModelData(Object modelData) {
		this.modelData = modelData;
	}

	public boolean isOveride() {
		return overide;
	}

	public void setOveride(boolean overide) {
		this.overide = overide;
	}

	public String getAuditInfo() {
		return auditInfo;
	}

	public void setAuditInfo(String auditInfo) {
		this.auditInfo = auditInfo;
	}

	public String getAuditOveride() {
		return auditOveride;
	}

	public void setAuditOveride(String auditOveride) {
		this.auditOveride = auditOveride;
	}

	public String getAuditError() {
		return auditError;
	}

	public void setAuditError(String auditError) {
		this.auditError = auditError;
	}

	public List<ErrorDetail> getInfoMessage() {
		return infoMessage;
	}

	public void setInfoMessage(List<ErrorDetail> infoMessage) {
		if (infoMessage != null && !infoMessage.isEmpty()) {
			for (int i = 0; i < infoMessage.size(); i++) {
				setInfoMessage((ErrorDetail) infoMessage.get(i));
			}
		} else {
			this.auditInfo = null;
		}
	}

	private void setInfoMessage(ErrorDetail infoMessage) {
		if (infoMessage != null) {

			if (this.infoMessage == null) {
				this.auditInfo = null;
				this.infoMessage = new ArrayList<ErrorDetail>();
				this.infoMessage.add(infoMessage);
			} else {
				this.infoMessage.add(infoMessage);
			}

			if (auditInfo == null) {
				auditInfo = infoMessage.getError();
			} else {
				auditInfo = auditInfo.concat("\n");
				auditInfo = auditInfo.concat(infoMessage.getError());
			}
		}
	}

	public List<ErrorDetail> getOverideMessage() {

		return overideMessage;
	}

	public void setOverideMessage(List<ErrorDetail> overideMessage) {
		if (overideMessage != null && !overideMessage.isEmpty()) {
			for (int i = 0; i < overideMessage.size(); i++) {
				setOverideMessage((ErrorDetail) overideMessage.get(i));
			}
		} else {
			this.auditOveride = null;
		}
	}

	private void setOverideMessage(ErrorDetail overideMessage) {
		if (overideMessage != null) {

			if (this.overideMessage == null) {
				this.auditOveride = null;
				this.overideMessage = new ArrayList<ErrorDetail>();
				this.overideMessage.add(overideMessage);
			} else {
				this.overideMessage.add(overideMessage);
			}

			if (auditOveride == null) {
				auditOveride = overideMessage.getError();
			} else {
				auditOveride = auditOveride.concat("\n");
				auditOveride = auditOveride.concat(overideMessage.getError());
			}
			checkOveride(overideMessage);
		}
	}

	public List<ErrorDetail> getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(List<ErrorDetail> errorMessage) {

		if (CollectionUtils.isEmpty(errorMessage)) {
			this.auditError = null;
			return;
		}

		for (ErrorDetail ed : errorMessage) {
			setErrorMessage(ed);
		}
	}

	private void setErrorMessage(ErrorDetail errorMessage) {
		if (errorMessage != null) {

			if (this.errorMessage == null) {
				this.auditError = null;
				this.errorMessage = new ArrayList<ErrorDetail>();
				this.errorMessage.add(errorMessage);
			} else {
				this.errorMessage.add(errorMessage);
			}

			if (auditError == null) {
				auditError = errorMessage.getError();
			} else {
				auditError = auditError.concat("\n");
				auditError = auditError.concat(errorMessage.getError());
			}

			errorMessage.setMessage(errorMessage.getError());
		}
	}

	public void setErrorList(List<ErrorDetail> errorDetails) {
		if (CollectionUtils.isEmpty(errorDetails)) {
			return;
		}

		for (ErrorDetail ed : errorDetails) {
			setErrorDetails(ed);
		}
	}

	public void setErrorDetails(ErrorDetail errorDetails) {
		if (errorDetails == null) {
			return;
		}

		if (StringUtils.trimToEmpty(errorDetails.getSeverity()).equalsIgnoreCase("I")) { // PennantConstants.ERR_SEV_INFO
			setInfoMessage(errorDetails);
		} else if (StringUtils.trimToEmpty(errorDetails.getSeverity()).equalsIgnoreCase("W")) { // PennantConstants.ERR_SEV_WARNING
			setOverideMessage(errorDetails);
		} else if (StringUtils.trimToEmpty(errorDetails.getSeverity()).equalsIgnoreCase("E")) { // PennantConstants.ERR_SEV_ERROR
			setErrorMessage(errorDetails);
		}
	}

	public AuditHeader(String reference, String custNo, String accNo, String loanNo, AuditDetail auditDetail,
			LoggedInUser userDetails, Map<String, List<ErrorDetail>> overideMap) {

		this.auditReference = reference;
		this.auditCustNo = custNo;
		this.auditAccNo = accNo;
		this.auditLoanNo = loanNo;

		if (userDetails != null) {
			this.auditUsrId = userDetails.getUserId();
			this.auditBranchCode = userDetails.getBranchCode();
			this.auditDeptCode = userDetails.getDepartmentCode();
			this.auditSystemIP = userDetails.getIpAddress();
			this.auditSessionID = userDetails.getSessionId();
			this.usrLanguage = userDetails.getLanguage();
		}

		this.auditTranType = auditDetail.getAuditTranType();
		this.auditModule = auditDetail.getModelData().getClass().getSimpleName();
		this.auditDetail = auditDetail;
		this.overideMap = overideMap;
	}

	public int getOverideCount() {
		return overideCount;
	}

	public void setOverideCount(int overideCount) {
		this.overideCount = overideCount;
	}

	public String getUsrLanguage() {
		return usrLanguage;
	}

	public void setUsrLanguage(String usrLanguage) {
		this.usrLanguage = usrLanguage;
	}

	public List<AuditDetail> getAuditDetails() {
		return auditDetails;
	}

	public void setAuditDetails(List<AuditDetail> auditDetails) {
		this.auditDetails = auditDetails;
	}

	public Map<String, List<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

	public void setOverideMap(Map<String, List<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public void addAuditDetail(AuditDetail auditDetail) {
		if (auditDetail != null) {
			if (this.auditDetails == null) {
				this.auditDetails = new ArrayList<AuditDetail>();
			}
			this.auditDetails.add(auditDetail);
		}

	}

	public AuditDetail getAuditDetail() {
		return auditDetail;
	}

	public void setAuditDetail(AuditDetail auditDetail) {
		this.auditDetail = auditDetail;
	}

	public int getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(int processStatus) {
		this.processStatus = processStatus;
	}

	public boolean isNextProcess() {
		return nextProcess;
	}

	public void setNextProcess(boolean nextProcess) {
		this.nextProcess = nextProcess;
	}

	private void checkOveride(ErrorDetail overideMessage) {

		if (overideMap != null) {

			if (overideMap.containsKey(overideMessage.getField())) {
				List<ErrorDetail> overDetails = this.overideMap.get(overideMessage.getField());

				for (int i = 0; i < overDetails.size(); i++) {
					if (!isEqual(overideMessage, overDetails.get(i))) {
						this.overideMap.remove(overideMessage.getField());
						break;
					}
				}
			}
		}
	}

	private boolean isEqual(ErrorDetail oldDetails, ErrorDetail newDetails) {
		boolean isSame = true;
		if (StringUtils.trimToEmpty(oldDetails.getCode()).equals(StringUtils.trimToEmpty(newDetails.getCode()))) {
			if (oldDetails.getFieldValues() == null && newDetails.getFieldValues() == null) {
				isSame = true;
			} else if (oldDetails.getFieldValues() == null || newDetails.getFieldValues() == null) {
				isSame = false;
			} else {
				for (int i = 0; i < oldDetails.getFieldValues().length; i++) {
					if (!oldDetails.getFieldValues()[i].equals(newDetails.getFieldValues()[i])) {
						isSame = false;
						break;
					}
				}
			}
		}
		return isSame;
	}

	public Object getApiHeader() {
		return apiHeader;
	}

	public void setApiHeader(Object apiHeader) {
		this.apiHeader = apiHeader;
	}

	public boolean isDeleteNotes() {
		return deleteNotes;
	}

	public void setDeleteNotes(boolean deleteNotes) {
		this.deleteNotes = deleteNotes;
	}

	public boolean isProcessCompleted() {
		return processCompleted;
	}

	public void setProcessCompleted(boolean processCompleted) {
		this.processCompleted = processCompleted;
	}

}