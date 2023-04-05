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
 * * FileName : FinTypeAccountingServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-03-2017 * *
 * Modified Date : 21-03-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-03-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.rmtmasters.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeAccountingDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.AccountEngineEvent;
import com.pennant.backend.model.rmtmasters.FinTypeAccounting;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.rmtmasters.FinTypeAccountingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.AccountingEvent;

/**
 * Service implementation for methods that depends on <b>FinTypeAccounting</b>.<br>
 * 
 */
public class FinTypeAccountingServiceImpl extends GenericService<FinTypeAccounting>
		implements FinTypeAccountingService {
	private static final Logger logger = LogManager.getLogger(FinTypeAccountingServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FinTypeAccountingDAO finTypeAccountingDAO;

	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		String tableType = "";
		FinTypeAccounting finTypeAccounting = (FinTypeAccounting) auditHeader.getAuditDetail().getModelData();

		if (finTypeAccounting.isWorkflow()) {
			tableType = "_TEMP";
		}

		if (finTypeAccounting.isNewRecord()) {
			finTypeAccounting.setId(finTypeAccountingDAO.save(finTypeAccounting, tableType));
			auditHeader.getAuditDetail().setModelData(finTypeAccounting);
			auditHeader.setAuditReference(String.valueOf(finTypeAccounting.getFinType()));
		} else {
			finTypeAccountingDAO.update(finTypeAccounting, tableType);
		}

		if (StringUtils.isEmpty(tableType)) {
			AccountingConfigCache.clearAccountSetCache(finTypeAccounting.getFinType(), finTypeAccounting.getEvent(),
					finTypeAccounting.getModuleId());
		}

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);

		return auditHeader;

	}

	@Override
	public List<FinTypeAccounting> getFinTypeAccountingListByID(String finType, int moduleId) {
		return finTypeAccountingDAO.getFinTypeAccountingListByID(finType, moduleId, "_View");
	}

	@Override
	public List<FinTypeAccounting> getApprovedFinTypeAccountingListByID(String finType, int moduleId) {
		return finTypeAccountingDAO.getFinTypeAccountingListByID(finType, moduleId, "_AView");
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		FinTypeAccounting fta = new FinTypeAccounting("");
		BeanUtils.copyProperties((FinTypeAccounting) auditHeader.getAuditDetail().getModelData(), fta);

		if (PennantConstants.RECORD_TYPE_DEL.equals(fta.getRecordType())) {
			tranType = PennantConstants.TRAN_DEL;
			finTypeAccountingDAO.delete(fta, "");
		} else {
			fta.setRoleCode("");
			fta.setNextRoleCode("");
			fta.setTaskId("");
			fta.setNextTaskId("");
			fta.setWorkflowId(0);

			if (PennantConstants.RECORD_TYPE_NEW.equals(fta.getRecordType())) {
				tranType = PennantConstants.TRAN_ADD;
				fta.setRecordType("");
				finTypeAccountingDAO.save(fta, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				fta.setRecordType("");
				finTypeAccountingDAO.update(fta, "");
			}
		}

		AccountingConfigCache.clearAccountSetCache(fta.getFinType(), fta.getEvent(), fta.getModuleId());

		finTypeAccountingDAO.delete(fta, "_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(fta);

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		FinTypeAccounting finTypeAccounting = (FinTypeAccounting) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		finTypeAccountingDAO.delete(finTypeAccounting, "_TEMP");

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	@Override
	public AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinTypeAccounting finTypeAccounting = (FinTypeAccounting) auditDetail.getModelData();

		FinTypeAccounting tempFinTypeAccounting = null;

		if (finTypeAccounting.isWorkflow()) {
			tempFinTypeAccounting = finTypeAccountingDAO.getFinTypeAccountingByID(finTypeAccounting, "_Temp");
		}
		FinTypeAccounting befFinTypeAccounting = finTypeAccountingDAO.getFinTypeAccountingByID(finTypeAccounting, "");

		FinTypeAccounting oldFinTypeAccounting = finTypeAccounting.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[2];
		valueParm[0] = finTypeAccounting.getEvent();
		valueParm[1] = finTypeAccounting.getLovDescEventAccountingName();
		errParm[0] = PennantJavaUtil.getLabel("label_FinTypeAccountingDialog_Event.value") + ":" + valueParm[0] + ","
				+ PennantJavaUtil.getLabel("label_FinTypeAccountingDialog_AccountSetCode.value") + ":" + valueParm[1];

		if (finTypeAccounting.isNewRecord()) {

			if (!finTypeAccounting.isWorkflow()) {
				if (befFinTypeAccounting != null) {
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
				}
			} else { // with work flow
				if (finTypeAccounting.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {

					if (befFinTypeAccounting != null || tempFinTypeAccounting != null) {

						auditDetail.setErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
					}
				} else {
					if (befFinTypeAccounting == null || tempFinTypeAccounting != null) {
						auditDetail.setErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
					}
				}
			}
		} else {
			if (!finTypeAccounting.isWorkflow()) {

				if (befFinTypeAccounting == null) {
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm));
				} else {
					if (oldFinTypeAccounting != null
							&& !oldFinTypeAccounting.getLastMntOn().equals(befFinTypeAccounting.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm));
						} else {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm));
						}
					}
				}
			} else {

				if (tempFinTypeAccounting == null) { // if records not exists in the Work flow table
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}

				if (tempFinTypeAccounting != null && oldFinTypeAccounting != null
						&& !oldFinTypeAccounting.getLastMntOn().equals(tempFinTypeAccounting.getLastMntOn())) {
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !finTypeAccounting.isWorkflow()) {
			auditDetail.setBefImage(befFinTypeAccounting);
		}

		return auditDetail;
	}

	@Override
	public List<AuditDetail> setFinTypeAccountingAuditData(List<FinTypeAccounting> finTypeAccountingList,
			String auditTranType, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinTypeAccounting(),
				new FinTypeAccounting().getExcludeFields());
		for (int i = 0; i < finTypeAccountingList.size(); i++) {
			FinTypeAccounting finTypeAccounting = finTypeAccountingList.get(i);

			if (StringUtils.isEmpty(finTypeAccounting.getRecordType())) {
				continue;
			}

			boolean isRcdType = false;
			if (finTypeAccounting.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finTypeAccounting.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (finTypeAccounting.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finTypeAccounting.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (finTypeAccounting.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				finTypeAccounting.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			if ("saveOrUpdate".equals(method) && isRcdType) {
				finTypeAccounting.setNewRecord(true);
			}
			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finTypeAccounting.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finTypeAccounting.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| finTypeAccounting.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
					finTypeAccounting.getBefImage(), finTypeAccounting));
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public List<AuditDetail> processFinTypeAccountingDetails(List<AuditDetail> auditDetails, String type) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			FinTypeAccounting finTypeAccounting = (FinTypeAccounting) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				finTypeAccounting.setRoleCode("");
				finTypeAccounting.setNextRoleCode("");
				finTypeAccounting.setTaskId("");
				finTypeAccounting.setNextTaskId("");
				finTypeAccounting.setWorkflowId(0);
			}

			if (finTypeAccounting.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (finTypeAccounting.isNewRecord()) {
				saveRecord = true;
				if (finTypeAccounting.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					finTypeAccounting.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (finTypeAccounting.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					finTypeAccounting.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (finTypeAccounting.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					finTypeAccounting.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (finTypeAccounting.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (finTypeAccounting.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (finTypeAccounting.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (finTypeAccounting.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = finTypeAccounting.getRecordType();
				recordStatus = finTypeAccounting.getRecordStatus();
				finTypeAccounting.setRecordType("");
				finTypeAccounting.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				finTypeAccountingDAO.save(finTypeAccounting, type);
			}
			if (updateRecord) {
				finTypeAccountingDAO.update(finTypeAccounting, type);
			}
			if (deleteRecord) {
				finTypeAccountingDAO.delete(finTypeAccounting, type);
			}
			if (approveRec) {
				finTypeAccounting.setRecordType(rcdType);
				finTypeAccounting.setRecordStatus(recordStatus);
			}
			if (StringUtils.isEmpty(type)) {
				AccountingConfigCache.clearAccountSetCache(finTypeAccounting.getFinType(), finTypeAccounting.getEvent(),
						finTypeAccounting.getModuleId());
			}

			auditDetails.get(i).setModelData(finTypeAccounting);
		}

		logger.debug(Literal.LEAVING);

		return auditDetails;
	}

	@Override
	public List<AuditDetail> delete(List<FinTypeAccounting> finTypeAccountingList, String tableType,
			String auditTranType, String finType, int moduleId) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (finTypeAccountingList != null && !finTypeAccountingList.isEmpty()) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FinTypeAccounting(),
					new FinTypeAccounting().getExcludeFields());
			for (int i = 0; i < finTypeAccountingList.size(); i++) {
				FinTypeAccounting finTypeAccounting = finTypeAccountingList.get(i);
				if (StringUtils.isNotEmpty(finTypeAccounting.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							finTypeAccounting.getBefImage(), finTypeAccounting));
				}
				if (StringUtils.isEmpty(tableType)) {
					AccountingConfigCache.clearAccountSetCache(finTypeAccounting.getFinType(),
							finTypeAccounting.getEvent(), finTypeAccounting.getModuleId());
				}
			}

			finTypeAccountingDAO.deleteByFinType(finType, moduleId, tableType);
		}

		logger.debug(Literal.LEAVING);

		return auditDetails;
	}

	@Override
	public AuditDetail validationByRef(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinTypeAccounting finTypeAccounting = (FinTypeAccounting) auditDetail.getModelData();
		FinTypeAccounting tempFinTypeAccounting = null;
		if (finTypeAccounting.isWorkflow()) {
			tempFinTypeAccounting = finTypeAccountingDAO.getFinTypeAccountingByRef(finTypeAccounting, "_Temp");
		}
		FinTypeAccounting befFinTypeAccounting = finTypeAccountingDAO.getFinTypeAccountingByRef(finTypeAccounting, "");
		FinTypeAccounting oldFinTypeAccounting = finTypeAccounting.getBefImage();
		String[] errParm = new String[1];
		String[] valueParm = new String[2];
		valueParm[0] = finTypeAccounting.getEvent();
		valueParm[1] = finTypeAccounting.getLovDescEventAccountingName();
		errParm[0] = PennantJavaUtil.getLabel("label_FinTypeAccountingDialog_Event.value") + ":" + valueParm[0] + ","
				+ PennantJavaUtil.getLabel("label_FinTypeAccountingDialog_AccountSetCode.value") + ":" + valueParm[1];
		if (finTypeAccounting.isNewRecord()) {
			if (!finTypeAccounting.isWorkflow()) {
				if (befFinTypeAccounting != null) {
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
				}
			} else {
				if (finTypeAccounting.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {

					if (befFinTypeAccounting != null || tempFinTypeAccounting != null) {

						auditDetail.setErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
					}
				} else {
					if (befFinTypeAccounting == null || tempFinTypeAccounting != null) {
						auditDetail.setErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
					}
				}
			}
		} else {
			if (!finTypeAccounting.isWorkflow()) {
				if (befFinTypeAccounting == null) {
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm));
				} else {
					if (oldFinTypeAccounting != null
							&& !oldFinTypeAccounting.getLastMntOn().equals(befFinTypeAccounting.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm));
						} else {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm));
						}
					}
				}
			} else {
				if (tempFinTypeAccounting == null) {
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}
				if (tempFinTypeAccounting != null && oldFinTypeAccounting != null
						&& !oldFinTypeAccounting.getLastMntOn().equals(tempFinTypeAccounting.getLastMntOn())) {
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !finTypeAccounting.isWorkflow()) {
			auditDetail.setBefImage(befFinTypeAccounting);
		}
		return auditDetail;
	}

	@Override
	public List<AccountEngineEvent> getAccountEngineEvents(String categoryCode) {
		List<AccountEngineEvent> list = new ArrayList<>();

		List<String> excludedAccEvents = AccountingEvent.getExcludedAccEvents();

		finTypeAccountingDAO.getAccountEngineEvents(categoryCode).forEach(ae -> {

			boolean exclude = false;
			for (String code : excludedAccEvents) {
				if (ae.getAEEventCode().startsWith(code)) {
					exclude = true;
				}
			}

			if (!exclude) {
				list.add(ae);
			}

		});

		return list;
	}

	@Autowired
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	@Autowired
	public void setFinTypeAccountingDAO(FinTypeAccountingDAO finTypeAccountingDAO) {
		this.finTypeAccountingDAO = finTypeAccountingDAO;
	}

}