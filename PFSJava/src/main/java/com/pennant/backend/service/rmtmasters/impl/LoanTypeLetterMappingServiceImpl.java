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
 * * FileName : FinanceTypeServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 30-06-2011 * *
 * Modified Date : 30-06-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 30-06-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.rmtmasters.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.rmtmasters.LoanTypeLetterMappingDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.LoanTypeLetterMapping;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.rmtmasters.LoanTypeLetterMappingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class LoanTypeLetterMappingServiceImpl extends GenericService<LoanTypeLetterMapping>
		implements LoanTypeLetterMappingService {
	private static final Logger logger = LogManager.getLogger(LoanTypeLetterMappingServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private LoanTypeLetterMappingDAO loanTypeLetterMappingDAO;

	public LoanTypeLetterMappingServiceImpl() {
		super();
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader ah) {
		logger.debug(Literal.ENTERING);

		setPropertiesToChailds(ah);
		List<AuditDetail> auditDetails = new ArrayList<>();
		ah = businessValidation(ah, PennantConstants.method_saveOrUpdate);
		if (!ah.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return ah;
		}

		TableType tableType = TableType.MAIN_TAB;
		LoanTypeLetterMapping lm = (LoanTypeLetterMapping) ah.getAuditDetail().getModelData();

		if (lm.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (CollectionUtils.isNotEmpty(lm.getLoanTypeLetterMappingList())) {
			List<AuditDetail> details = lm.getAuditDetailMap().get("loanTypeLetterMapping");
			details = processingLetterMappingList(details, tableType.getSuffix());
			auditDetails.addAll(details);
		}

		ah.setAuditDetail(null);
		auditHeaderDAO.addAudit(ah);

		logger.debug(Literal.LEAVING);
		return ah;
	}

	public void setPropertiesToChailds(AuditHeader auditHeader) {
		LoanTypeLetterMapping lm = (LoanTypeLetterMapping) auditHeader.getAuditDetail().getModelData();
		for (LoanTypeLetterMapping lmd : lm.getLoanTypeLetterMappingList()) {
			lmd.setLastMntBy(lm.getLastMntBy());
			lmd.setLastMntOn(lm.getLastMntOn());
			lmd.setUserDetails(lm.getUserDetails());
			lmd.setRecordStatus(lm.getRecordStatus());
			lmd.setTaskId(lm.getTaskId());
			lmd.setNextTaskId(lm.getNextTaskId());
			lmd.setRoleCode(lm.getRoleCode());
			lmd.setNextRoleCode(lm.getNextRoleCode());
		}
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, PennantConstants.RCD_DEL);
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		LoanTypeLetterMapping lm = (LoanTypeLetterMapping) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(lm, "", auditHeader.getAuditTranType())));
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		auditHeader = businessValidation(auditHeader, PennantConstants.method_doApprove);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		LoanTypeLetterMapping lm = new LoanTypeLetterMapping();
		BeanUtils.copyProperties(auditHeader.getAuditDetail().getModelData(), lm);

		String recordType = lm.getRecordType();

		if (PennantConstants.RECORD_TYPE_DEL.equals(recordType)) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetails.addAll(listDeletion(lm, "", auditHeader.getAuditTranType()));
		} else {
			lm.setRoleCode("");
			lm.setNextRoleCode("");
			lm.setTaskId("");
			lm.setNextTaskId("");
			lm.setWorkflowId(0);
		}

		// Retrieving List of Audit Details For checkList details modules
		if (lm.getLoanTypeLetterMappingList() != null && lm.getLoanTypeLetterMappingList().size() > 0) {
			List<AuditDetail> details = lm.getAuditDetailMap().get("loanTypeLetterMapping");
			details = processingLetterMappingList(details, "");
			auditDetails.addAll(details);
		}

		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(lm, "_Temp", auditHeader.getAuditTranType())));
		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(lm);
		auditHeader.setAuditDetail(null);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, PennantConstants.method_doApprove);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		LoanTypeLetterMapping aletterMapping = (LoanTypeLetterMapping) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader
				.setAuditDetails(getListAuditDetails(listDeletion(aletterMapping, "", auditHeader.getAuditTranType())));
		auditHeaderDAO.addAudit(auditHeader);

		for (LoanTypeLetterMapping lm : aletterMapping.getLoanTypeLetterMappingList()) {
			loanTypeLetterMappingDAO.delete(lm, TableType.TEMP_TAB.getSuffix());
		}

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), false);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);

		List<AuditDetail> auditDetails = new ArrayList<>();

		LoanTypeLetterMapping lm = (LoanTypeLetterMapping) auditHeader.getAuditDetail().getModelData();
		String usrLanguage = lm.getUserDetails().getLanguage();

		List<LoanTypeLetterMapping> finTypeLetterMapping = lm.getLoanTypeLetterMappingList();
		if (CollectionUtils.isNotEmpty(finTypeLetterMapping)) {
			List<AuditDetail> details = lm.getAuditDetailMap().get("loanTypeLetterMapping");
			auditDetails.addAll(validateLetterMapping(details, usrLanguage, method));
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);

		return auditHeader;

	}

	public List<AuditDetail> validateLetterMapping(List<AuditDetail> auditDetails, String usrLanguage, String method) {
		List<AuditDetail> aAuditDetails = new ArrayList<>();
		logger.debug(Literal.ENTERING);

		for (AuditDetail auditDetail : auditDetails) {
			validate(auditDetail, usrLanguage, method);
			aAuditDetails.add(auditDetail);
		}

		return aAuditDetails;
	}

	private void validate(AuditDetail auditDetail, String usrLanguage, String method) {
		auditDetail.setErrorDetails(new ArrayList<>());
		LoanTypeLetterMapping ltlm = (LoanTypeLetterMapping) auditDetail.getModelData();
		LoanTypeLetterMapping tempLoanOption = null;

		if (ltlm.isWorkflow()) {
			tempLoanOption = loanTypeLetterMappingDAO.getLoanTypeLetterMappingByID(ltlm,
					TableType.TEMP_TAB.getSuffix());
		}
		LoanTypeLetterMapping befFinOption = loanTypeLetterMappingDAO.getLoanTypeLetterMappingByID(ltlm, "");
		LoanTypeLetterMapping oldFinOption = ltlm.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = ltlm.getLetterType();
		errParm[0] = PennantJavaUtil.getLabel("label_LetterType") + ":" + valueParm[0];

		if (ltlm.isNewRecord()) {
			if (!ltlm.isWorkflow()) {
				if (befFinOption != null) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else {
				if (ltlm.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					if (befFinOption != null || tempLoanOption != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else {
					if (befFinOption == null || tempLoanOption != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			if (!ltlm.isWorkflow()) {
				if (befFinOption == null) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldFinOption != null && !oldFinOption.getLastMntOn().equals(befFinOption.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm),
									usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm),
									usrLanguage));
						}
					}
				}
			} else {

				if (tempLoanOption == null) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempLoanOption != null && oldFinOption != null
						&& !oldFinOption.getLastMntOn().equals(tempLoanOption.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (PennantConstants.method_doApprove.equals(StringUtils.trimToEmpty(method)) || !ltlm.isWorkflow()) {
			auditDetail.setBefImage(befFinOption);
		}
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, boolean isUniqueCheckReq) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		LoanTypeLetterMapping lm = (LoanTypeLetterMapping) auditDetail.getModelData();

		// Check the unique keys.
		if (isUniqueCheckReq && lm.isNewRecord() && loanTypeLetterMappingDAO.isDuplicateKey(lm.getFinType(),
				lm.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];
			parameters[0] = PennantJavaUtil.getLabel("label_FinMaintainInstruction_Event") + ": " + lm.getFinType();
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);

		return auditDetail;
	}

	private List<AuditDetail> processingLetterMappingList(List<AuditDetail> auditDetails, String type) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			LoanTypeLetterMapping ltlm = (LoanTypeLetterMapping) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			ltlm.setId(ltlm.getId());
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				ltlm.setVersion(ltlm.getVersion() + 1);
				ltlm.setRoleCode("");
				ltlm.setNextRoleCode("");
				ltlm.setTaskId("");
				ltlm.setNextTaskId("");
			}

			ltlm.setWorkflowId(0);

			String recordType = ltlm.getRecordType();
			if (recordType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (ltlm.isNewRecord()) {
				saveRecord = true;
				if (recordType.equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					ltlm.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (recordType.equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					ltlm.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (recordType.equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					ltlm.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (recordType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (recordType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (recordType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (ltlm.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = recordType;
				recordStatus = ltlm.getRecordStatus();
				ltlm.setRecordType("");
				ltlm.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				loanTypeLetterMappingDAO.delete(ltlm, TableType.TEMP_TAB.getSuffix());

			}

			if (saveRecord) {
				loanTypeLetterMappingDAO.save(ltlm, type);
			}

			if (updateRecord) {
				loanTypeLetterMappingDAO.update(ltlm, type);
			}

			if (deleteRecord) {
				loanTypeLetterMappingDAO.delete(ltlm, type);
			}

			if (approveRec) {
				ltlm.setRecordType(rcdType);
				ltlm.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(ltlm);
		}

		logger.debug(Literal.LEAVING);

		return auditDetails;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();
		LoanTypeLetterMapping lm = (LoanTypeLetterMapping) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ((PennantConstants.method_saveOrUpdate.equals(method) || PennantConstants.method_doApprove.equals(method)
				|| PennantConstants.method_doReject.equals(method)) && lm.isWorkflow()) {
			auditTranType = PennantConstants.TRAN_WF;
		}

		if (lm.getLoanTypeLetterMappingList() != null && lm.getLoanTypeLetterMappingList().size() > 0) {
			auditDetailMap.put("loanTypeLetterMapping", setLetterMappingAuditData(lm, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("loanTypeLetterMapping"));
		}

		lm.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(lm);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	private List<AuditDetail> setLetterMappingAuditData(LoanTypeLetterMapping ltlm, String auditTranType,
			String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new LoanTypeLetterMapping(),
				new LoanTypeLetterMapping().getExcludeFields());

		for (int i = 0; i < ltlm.getLoanTypeLetterMappingList().size(); i++) {
			LoanTypeLetterMapping lm = ltlm.getLoanTypeLetterMappingList().get(i);

			// Skipping the process of current iteration when the child was not
			// modified to avoid unnecessary processing
			String recordType = lm.getRecordType();
			if (StringUtils.isEmpty(recordType)) {
				continue;
			}

			lm.setWorkflowId(ltlm.getWorkflowId());

			boolean isRcdType = false;

			if (recordType.equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				lm.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (recordType.equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				lm.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (ltlm.isWorkflow()) {
					isRcdType = true;
				}
			} else if (recordType.equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				lm.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if (PennantConstants.method_saveOrUpdate.equals(method) && isRcdType) {
				lm.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (recordType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (recordType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| recordType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			lm.setRecordStatus(ltlm.getRecordStatus());
			lm.setUserDetails(ltlm.getUserDetails());
			lm.setLastMntOn(ltlm.getLastMntOn());
			lm.setLastMntBy(ltlm.getLastMntBy());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], lm.getBefImage(), lm));
		}

		logger.debug(Literal.LEAVING);

		return auditDetails;
	}

	public List<AuditDetail> listDeletion(LoanTypeLetterMapping lm, String tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditList = new ArrayList<>();
		if (lm.getLoanTypeLetterMappingList() == null && lm.getLoanTypeLetterMappingList().isEmpty()) {
			return auditList;
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new LoanTypeLetterMapping());
		for (int i = 0; i < lm.getLoanTypeLetterMappingList().size(); i++) {
			LoanTypeLetterMapping alm = lm.getLoanTypeLetterMappingList().get(i);
			if (!StringUtils.isEmpty(alm.getRecordType()) || StringUtils.isEmpty(tableType)) {
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], alm.getBefImage(), alm));
			}
		}
		LoanTypeLetterMapping mapping = lm.getLoanTypeLetterMappingList().get(0);
		loanTypeLetterMappingDAO.delete(mapping.getFinType(), tableType);

		logger.debug(Literal.LEAVING);

		return auditList;
	}

	private List<AuditDetail> getListAuditDetails(List<AuditDetail> list) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetailsList = new ArrayList<>();

		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {

				String transType = "";
				String rcdType = "";
				LoanTypeLetterMapping loanMapping = (LoanTypeLetterMapping) ((AuditDetail) list.get(i)).getModelData();
				rcdType = loanMapping.getRecordType();

				if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(rcdType)) {
					transType = PennantConstants.TRAN_ADD;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(rcdType)
						|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(rcdType)) {
					transType = PennantConstants.TRAN_DEL;
				} else {
					transType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isNotEmpty(transType)) {
					// check and change below line for Complete code
					auditDetailsList.add(new AuditDetail(transType, ((AuditDetail) list.get(i)).getAuditSeq(),
							loanMapping.getBefImage(), loanMapping));
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return auditDetailsList;
	}

	@Override
	public List<LoanTypeLetterMapping> getLoanTypeLetterMapping(List<String> roleCodes) {
		return this.loanTypeLetterMappingDAO.getLoanTypeLetterMapping(roleCodes);
	}

	@Override
	public List<LoanTypeLetterMapping> getLoanTypeLetterMappingById(String finType) {
		return loanTypeLetterMappingDAO.getLoanTypeLettterMappingListByLoanType(finType);
	}

	@Autowired
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	@Autowired
	public void setFinTypeLetterMappingDAO(LoanTypeLetterMappingDAO loanTypeLetterMappingDAO) {
		this.loanTypeLetterMappingDAO = loanTypeLetterMappingDAO;
	}
}