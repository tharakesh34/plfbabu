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
 * * FileName : FinCovenantMaintanceServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 11-06-2015 *
 * * Modified Date : 11-06-2015 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 11-06-2015 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.finance.impl;

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
import com.pennant.backend.dao.finance.FinCovenantTypeDAO;
import com.pennant.backend.dao.finance.FinMaintainInstructionDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.finance.FinMaintainInstruction;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinCovenantMaintanceService;
import com.pennant.backend.service.finance.covenant.CovenantsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>FinMaintainInstruction</b>.<br>
 * 
 */
public class FinCovenantMaintanceServiceImpl extends GenericService<FinMaintainInstruction>
		implements FinCovenantMaintanceService {

	private static Logger logger = LogManager.getLogger(FinCovenantMaintanceServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FinCovenantTypeDAO finCovenantTypeDAO;
	private FinMaintainInstructionDAO finMaintainInstructionDAO;
	private FinanceMainDAO financeMainDAO;

	@Autowired
	private CovenantsService covenantsService;

	public FinCovenantMaintanceServiceImpl() {
		super();
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinMaintainInstruction fmi = (FinMaintainInstruction) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (fmi.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (fmi.isNewRecord()) {
			FinMaintainInstruction finInst = null;
			if (tableType.getSuffix().equals("_Temp")) {
				finInst = finMaintainInstructionDAO.getFinMaintainInstructionByFinRef(fmi.getFinID(), fmi.getEvent(),
						tableType.getSuffix());
			}
			if (finInst == null) {
				fmi.setFinMaintainId(Long.parseLong(finMaintainInstructionDAO.save(fmi, tableType)));
				auditHeader.getAuditDetail().setModelData(fmi);
				auditHeader.setAuditReference(String.valueOf(fmi.getFinMaintainId()));
			} else {
				throw new ConcurrencyException();
			}
		} else {
			finMaintainInstructionDAO.update(fmi, tableType);
		}

		List<FinCovenantType> covenantTypes = fmi.getFinCovenantTypeList();
		if (CollectionUtils.isNotEmpty(covenantTypes)) {
			List<AuditDetail> details = fmi.getAuditDetailMap().get("FinCovenants");
			details = processingFinCovenantsList(details, fmi.getFinReference(), "_Temp");
			auditDetails.addAll(details);
		}

		List<Covenant> covenants = fmi.getCovenants();
		if (CollectionUtils.isNotEmpty(covenants)) {
			auditDetails.addAll(covenantsService.doProcess(covenants, TableType.TEMP_TAB,
					auditHeader.getAuditTranType(), false, 0));
		}

		String rcdMaintainSts = FinServiceEvent.COVENANT;
		financeMainDAO.updateMaintainceStatus(fmi.getFinID(), rcdMaintainSts);

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinMaintainInstruction fmi = (FinMaintainInstruction) auditHeader.getAuditDetail().getModelData();
		finMaintainInstructionDAO.delete(fmi, TableType.MAIN_TAB);

		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(fmi, "", auditHeader.getAuditTranType())));

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	public FinMaintainInstruction getFinMaintainInstructionByFinRef(long finID, String event) {
		return finMaintainInstructionDAO.getFinMaintainInstructionByFinRef(finID, event, "_Temp");
	}

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		AuditDetail auditDetail = auditHeader.getAuditDetail();

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		FinMaintainInstruction fmi = new FinMaintainInstruction();

		BeanUtils.copyProperties((FinMaintainInstruction) auditDetail.getModelData(), fmi);

		long finMaintainId = fmi.getFinMaintainId();

		long finID = fmi.getFinID();

		if (!PennantConstants.RECORD_TYPE_NEW.equals(fmi.getRecordType())) {
			auditDetail.setBefImage(finMaintainInstructionDAO.getFinMaintainInstructionById(finMaintainId, ""));
		}

		if (fmi.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {

			tranType = PennantConstants.TRAN_DEL;
			finMaintainInstructionDAO.delete(fmi, TableType.MAIN_TAB);
			auditDetails.addAll(listDeletion(fmi, "", auditHeader.getAuditTranType()));

		} else {

			fmi.setRoleCode("");
			fmi.setNextRoleCode("");
			fmi.setTaskId("");
			fmi.setNextTaskId("");
			fmi.setWorkflowId(0);

			if (fmi.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				fmi.setRecordType("");
				finMaintainInstructionDAO.save(fmi, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				fmi.setRecordType("");
				finMaintainInstructionDAO.update(fmi, TableType.MAIN_TAB);
			}
		}

		List<FinCovenantType> covenantTypes = fmi.getFinCovenantTypeList();
		if (CollectionUtils.isNotEmpty(covenantTypes)) {
			List<AuditDetail> details = fmi.getAuditDetailMap().get("FinCovenants");
			details = processingFinCovenantsList(details, fmi.getFinReference(), "");
			auditDetails.addAll(details);
		}

		List<Covenant> covenants = fmi.getCovenants();
		if (CollectionUtils.isNotEmpty(covenants)) {
			auditDetails.addAll(covenantsService.doProcess(covenants, TableType.MAIN_TAB, "", true, 0));
		}

		financeMainDAO.updateMaintainceStatus(finID, "");

		if (auditHeader.getApiHeader() == null) {
			finMaintainInstructionDAO.delete(fmi, TableType.TEMP_TAB);
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(fmi, "_Temp", auditHeader.getAuditTranType())));
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, auditDetail.getBefImage(),
				auditDetail.getModelData()));
		covenantsService.delete(covenants, TableType.TEMP_TAB, auditHeader.getAuditTranType());
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetails(auditDetails);
		auditDetail.setAuditTranType(tranType);
		auditDetail.setModelData(fmi);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fmi.getBefImage(), fmi));

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doReject");
		AuditDetail auditDetail = auditHeader.getAuditDetail();

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinMaintainInstruction fmi = (FinMaintainInstruction) auditDetail.getModelData();
		long finID = fmi.getFinID();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		finMaintainInstructionDAO.delete(fmi, TableType.TEMP_TAB);
		financeMainDAO.updateMaintainceStatus(finID, "");

		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fmi.getBefImage(), fmi));
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(fmi, "_Temp", auditHeader.getAuditTranType())));

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method, false);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		FinMaintainInstruction fmi = (FinMaintainInstruction) auditHeader.getAuditDetail().getModelData();
		String usrLanguage = fmi.getUserDetails().getLanguage();

		List<FinCovenantType> covenantTypes = fmi.getFinCovenantTypeList();
		if (CollectionUtils.isNotEmpty(covenantTypes)) {
			List<AuditDetail> details = fmi.getAuditDetailMap().get("FinCovenants");
			details = finCovenantListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		List<Covenant> covenants = fmi.getCovenants();
		if (CollectionUtils.isNotEmpty(covenants)) {
			List<AuditDetail> details = fmi.getAuditDetailMap().get("Covenants");
			auditDetails.addAll(covenantsService.validateCovenant(details, usrLanguage, method));
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method,
			boolean isUniqueCheckReq) {
		logger.debug(Literal.ENTERING);

		FinMaintainInstruction fmi = (FinMaintainInstruction) auditDetail.getModelData();

		// Check the unique keys.
		if (isUniqueCheckReq && fmi.isNewRecord() && finMaintainInstructionDAO.isDuplicateKey(fmi.getEvent(),
				fmi.getFinID(), fmi.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];
			parameters[0] = PennantJavaUtil.getLabel("label_FinMaintainInstruction_Event") + ": " + fmi.getEvent();
			parameters[1] = PennantJavaUtil.getLabel("label_FinReference") + " : " + fmi.getFinReference();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	/**
	 * 
	 * @param auditDetails
	 * @param finReference
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingFinCovenantsList(List<AuditDetail> auditDetails, String finReference,
			String type) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); ++i) {

			FinCovenantType finCovenantType = (FinCovenantType) auditDetails.get(i).getModelData();

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;

			String rcdType = "";
			String recordStatus = "";

			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				finCovenantType.setRoleCode("");
				finCovenantType.setNextRoleCode("");
				finCovenantType.setTaskId("");
				finCovenantType.setNextTaskId("");
			}

			finCovenantType.setFinReference(finReference);
			finCovenantType.setWorkflowId(0);

			if (finCovenantType.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				// getFinCovenantsDAO().delete(FinCovenants,
				// TableType.TEMP_TAB);
				deleteRecord = true;
			} else if (finCovenantType.isNewRecord()) {
				saveRecord = true;
				if (finCovenantType.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					finCovenantType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (finCovenantType.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					finCovenantType.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (finCovenantType.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					finCovenantType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (finCovenantType.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (finCovenantType.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (finCovenantType.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (finCovenantType.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = finCovenantType.getRecordType();
				recordStatus = finCovenantType.getRecordStatus();
				finCovenantType.setRecordType("");
				finCovenantType.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				finCovenantTypeDAO.save(finCovenantType, type);
			}

			if (updateRecord) {
				finCovenantTypeDAO.update(finCovenantType, type);
			}

			if (deleteRecord) {
				finCovenantTypeDAO.delete(finCovenantType, type);
			}

			if (approveRec) {
				finCovenantType.setRecordType(rcdType);
				finCovenantType.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(finCovenantType);
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	/**
	 * 
	 * @param finMaintainInstruction
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setFinCovenantAuditData(FinMaintainInstruction finMaintainInstruction,
			String auditTranType, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinCovenantType finCovenantType = new FinCovenantType();

		String[] fields = PennantJavaUtil.getFieldDetails(finCovenantType, finCovenantType.getExcludeFields());

		for (int i = 0; i < finMaintainInstruction.getFinCovenantTypeList().size(); i++) {
			FinCovenantType fincovenantType = finMaintainInstruction.getFinCovenantTypeList().get(i);

			if (StringUtils.isEmpty(fincovenantType.getRecordType())) {
				continue;
			}

			fincovenantType.setFinReference(finMaintainInstruction.getFinReference());
			fincovenantType.setWorkflowId(finMaintainInstruction.getWorkflowId());

			boolean isRcdType = false;

			if (fincovenantType.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				fincovenantType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (fincovenantType.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				fincovenantType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (finMaintainInstruction.isWorkflow()) {
					isRcdType = true;
				}
			} else if (fincovenantType.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				fincovenantType.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && isRcdType) {
				fincovenantType.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (fincovenantType.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (fincovenantType.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| fincovenantType.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			fincovenantType.setRecordStatus(finMaintainInstruction.getRecordStatus());
			fincovenantType.setUserDetails(finMaintainInstruction.getUserDetails());
			fincovenantType.setLastMntOn(finMaintainInstruction.getLastMntOn());
			fincovenantType.setLastMntBy(finMaintainInstruction.getLastMntBy());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], fincovenantType.getBefImage(),
					fincovenantType));
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	private List<AuditDetail> setCovenantAuditData(FinMaintainInstruction finMaintainInstruction, String auditTranType,
			String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Covenant covenant = new Covenant();

		String[] fields = PennantJavaUtil.getFieldDetails(covenant, covenant.getExcludeFields());

		for (int i = 0; i < finMaintainInstruction.getCovenants().size(); i++) {
			Covenant covenants = finMaintainInstruction.getCovenants().get(i);

			if (StringUtils.isEmpty(covenants.getRecordType())) {
				continue;
			}

			covenants.setKeyReference(finMaintainInstruction.getFinReference());
			covenants.setWorkflowId(finMaintainInstruction.getWorkflowId());

			boolean isRcdType = false;

			if (covenants.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				covenants.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (covenants.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				covenants.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (finMaintainInstruction.isWorkflow()) {
					isRcdType = true;
				}
			} else if (covenants.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				covenants.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && isRcdType) {
				covenants.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (covenants.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (covenants.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| covenants.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			covenants.setRecordStatus(finMaintainInstruction.getRecordStatus());
			covenants.setUserDetails(finMaintainInstruction.getUserDetails());
			covenants.setLastMntOn(finMaintainInstruction.getLastMntOn());
			covenants.setLastMntBy(finMaintainInstruction.getLastMntBy());

			auditDetails.add(
					new AuditDetail(auditTranType, i + 1, fields[0], fields[1], covenants.getBefImage(), covenants));
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	/**
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinMaintainInstruction finMaintainInstruction = (FinMaintainInstruction) auditHeader.getAuditDetail()
				.getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (finMaintainInstruction.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		if (finMaintainInstruction.getFinCovenantTypeList() != null
				&& finMaintainInstruction.getFinCovenantTypeList().size() > 0) {
			auditDetailMap.put("FinCovenants", setFinCovenantAuditData(finMaintainInstruction, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinCovenants"));
		}

		if (finMaintainInstruction.getCovenants() != null && finMaintainInstruction.getCovenants().size() > 0) {
			auditDetailMap.put("Covenants", setCovenantAuditData(finMaintainInstruction, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("Covenants"));
		}

		finMaintainInstruction.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(finMaintainInstruction);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	public List<AuditDetail> listDeletion(FinMaintainInstruction fmi, String tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();

		List<FinCovenantType> covenantTypes = fmi.getFinCovenantTypeList();
		if (CollectionUtils.isNotEmpty(covenantTypes)) {
			FinCovenantType finCovenantType = new FinCovenantType();
			String[] fields = PennantJavaUtil.getFieldDetails(finCovenantType, finCovenantType.getExcludeFields());

			for (FinCovenantType covenant : covenantTypes) {
				auditList.add(new AuditDetail(auditTranType, auditList.size() + 1, fields[0], fields[1],
						covenant.getBefImage(), covenant));
			}
			finCovenantTypeDAO.deleteByFinRef(fmi.getFinReference(), tableType);
		}

		logger.debug(Literal.LEAVING);
		return auditList;
	}

	private List<AuditDetail> getListAuditDetails(List<AuditDetail> list) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();

		if (list == null) {
			logger.debug(Literal.LEAVING);
			return auditDetailsList;
		}

		for (int i = 0; i < list.size(); i++) {
			String transType = "";
			String rcdType = "";
			Object object = ((AuditDetail) list.get(i)).getModelData();
			try {

				rcdType = object.getClass().getMethod("getRecordType").invoke(object).toString();

				if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					transType = PennantConstants.TRAN_ADD;
				} else if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					transType = PennantConstants.TRAN_DEL;
				} else if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
					transType = PennantConstants.TRAN_UPD;
				} else {
					auditDetailsList.remove(object);
				}

				if (StringUtils.isNotEmpty(transType)) {

					// check and change below line for Complete code
					Object befImg = object.getClass().getMethod("getBefImage", object.getClass().getClasses())
							.invoke(object, object.getClass().getClasses());

					auditDetailsList
							.add(new AuditDetail(transType, ((AuditDetail) list.get(i)).getAuditSeq(), befImg, object));
				}
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}

		logger.debug(Literal.LEAVING);
		return auditDetailsList;
	}

	public List<AuditDetail> finCovenantListValidation(List<AuditDetail> auditDetails, String method,
			String usrLanguage) {
		logger.debug(Literal.ENTERING);

		if (auditDetails != null && auditDetails.size() > 0) {
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail = validateFinCovenantType(auditDetails.get(i), method, usrLanguage);
				details.add(auditDetail);
			}
			return details;
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<AuditDetail>();
	}

	private AuditDetail validateFinCovenantType(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinCovenantType covenantType = (FinCovenantType) auditDetail.getModelData();
		FinCovenantType tempFinCovenantPay = null;

		if (covenantType.isWorkflow()) {
			tempFinCovenantPay = finCovenantTypeDAO.getFinCovenantTypeById(covenantType, "_Temp");
		}
		FinCovenantType befFinCovenant = finCovenantTypeDAO.getFinCovenantTypeById(covenantType, "");
		FinCovenantType oldFinAdvancePay = covenantType.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = covenantType.getFinReference();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (covenantType.isNewRecord()) { // for New record or new record into work
											// flow

			if (!covenantType.isWorkflow()) {// With out Work flow only new
												// records
				if (befFinCovenant != null) { // Record Already Exists in the
												// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (covenantType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					if (befFinCovenant != null || tempFinCovenantPay != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befFinCovenant == null || tempFinCovenantPay != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!covenantType.isWorkflow()) { // With out Work flow for update
												// and delete

				if (befFinCovenant == null) { // if records not exists in the
												// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldFinAdvancePay != null
							&& !oldFinAdvancePay.getLastMntOn().equals(befFinCovenant.getLastMntOn())) {
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

				if (tempFinCovenantPay == null) { // if records not exists in
													// the
													// Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempFinCovenantPay != null && oldFinAdvancePay != null
						&& !oldFinAdvancePay.getLastMntOn().equals(tempFinCovenantPay.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !covenantType.isWorkflow()) {
			auditDetail.setBefImage(befFinCovenant);
		}
		return auditDetail;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setFinMaintainInstructionDAO(FinMaintainInstructionDAO finMaintainInstructionDAO) {
		this.finMaintainInstructionDAO = finMaintainInstructionDAO;
	}

	public void setFinCovenantTypeDAO(FinCovenantTypeDAO finCovenantTypeDAO) {
		this.finCovenantTypeDAO = finCovenantTypeDAO;
	}
}