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
 * * FileName : HoldDisbursementServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 09-10-2018 * *
 * Modified Date : 09-10-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 09-10-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.finance.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.HoldDisbursementDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.HoldDisbursement;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.HoldDisbursementService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>HoldDisbursement</b>.<br>
 */
public class HoldDisbursementServiceImpl extends GenericService<HoldDisbursement> implements HoldDisbursementService {
	private static final Logger logger = LogManager.getLogger(HoldDisbursementServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private HoldDisbursementDAO holdDisbursementDAO;
	private FinServiceInstrutionDAO finServiceInstructionDAO;
	private FinanceDisbursementDAO financeDisbursementDAO;
	private FinanceMainDAO financeMainDAO;

	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		HoldDisbursement hd = (HoldDisbursement) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (hd.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		if (hd.isNewRecord()) {
			holdDisbursementDAO.save(hd, tableType);
		} else {
			holdDisbursementDAO.update(hd, tableType);
		}
		String rcdMaintainSts = FinServiceEvent.HOLDDISB;
		financeMainDAO.updateMaintainceStatus(hd.getFinID(), rcdMaintainSts);

		auditHeaderDAO.addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		HoldDisbursement hd = (HoldDisbursement) auditHeader.getAuditDetail().getModelData();
		holdDisbursementDAO.delete(hd, TableType.MAIN_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public HoldDisbursement getHoldDisbursement(long finID) {
		return holdDisbursementDAO.getHoldDisbursement(finID, "_View");
	}

	public HoldDisbursement getApprovedHoldDisbursement(long finID) {
		return holdDisbursementDAO.getHoldDisbursement(finID, "_AView");
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		HoldDisbursement hd = new HoldDisbursement();
		BeanUtils.copyProperties((HoldDisbursement) auditHeader.getAuditDetail().getModelData(), hd);

		holdDisbursementDAO.delete(hd, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(hd.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(holdDisbursementDAO.getHoldDisbursement(hd.getFinID(), ""));
		}

		if (hd.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			holdDisbursementDAO.delete(hd, TableType.MAIN_TAB);
		} else {
			hd.setRoleCode("");
			hd.setNextRoleCode("");
			hd.setTaskId("");
			hd.setNextTaskId("");
			hd.setWorkflowId(0);

			if (hd.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				hd.setRecordType("");
				holdDisbursementDAO.save(hd, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				hd.setRecordType("");
				holdDisbursementDAO.update(hd, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		// save FinInstruction to maintain records
		FinServiceInstruction finServiceInstruction = new FinServiceInstruction();
		finServiceInstruction.setFinReference(hd.getFinReference());
		finServiceInstruction.setFromDate(SysParamUtil.getAppDate());
		finServiceInstruction.setFinEvent(FinServiceEvent.HOLDDISB);
		finServiceInstruction.setAmount(hd.getHoldLimitAmount());
		finServiceInstructionDAO.save(finServiceInstruction, "");
		financeMainDAO.updateMaintainceStatus(hd.getFinID(), "");

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(hd);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		HoldDisbursement hd = (HoldDisbursement) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		holdDisbursementDAO.delete(hd, TableType.TEMP_TAB);
		financeMainDAO.updateMaintainceStatus(hd.getFinID(), "");

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		HoldDisbursement hd = (HoldDisbursement) auditDetail.getModelData();

		String[] parameters = new String[2];
		parameters[0] = PennantJavaUtil.getLabel("label_FinReference") + ": " + hd.getFinReference();

		// Check the unique keys.
		if (hd.isNewRecord() && PennantConstants.RECORD_TYPE_NEW.equals(hd.getRecordType()) && holdDisbursementDAO
				.isDuplicateKey(hd.getFinID(), hd.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public boolean getFinanceDisbursementById(long finID) {
		return financeDisbursementDAO.getFinanceDisbursementById(finID, "_TEMP", false) == null ? true : false;
	}

	@Override
	public boolean isFinServiceInstructionExist(long finID, String type, String finEvent) {
		boolean isExist = false;
		List<FinServiceInstruction> finServiceInstructions = finServiceInstructionDAO.getFinServiceInstructions(finID,
				type, finEvent);
		if (finServiceInstructions.size() > 0) {
			isExist = true;
		}
		return isExist;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setHoldDisbursementDAO(HoldDisbursementDAO holdDisbursementDAO) {
		this.holdDisbursementDAO = holdDisbursementDAO;
	}

	public void setFinServiceInstructionDAO(FinServiceInstrutionDAO finServiceInstructionDAO) {
		this.finServiceInstructionDAO = finServiceInstructionDAO;
	}

	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

}