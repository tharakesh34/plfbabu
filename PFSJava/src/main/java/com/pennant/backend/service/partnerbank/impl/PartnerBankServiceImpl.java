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
 * * FileName : PartnerBankServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 09-03-2017 * *
 * Modified Date : 09-03-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 09-03-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.partnerbank.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.fees.FeePostingsDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.dao.partnerbank.PartnerBankDAO;
import com.pennant.backend.dao.payment.PaymentInstructionDAO;
import com.pennant.backend.dao.rmtmasters.FinTypePartnerBankDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.partnerbank.PartnerBankModes;
import com.pennant.backend.model.partnerbank.PartnerBranchModes;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.partnerbank.PartnerBankService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>PartnerBank</b>.<br>
 * 
 */
public class PartnerBankServiceImpl extends GenericService<PartnerBank> implements PartnerBankService {
	private static final Logger logger = LogManager.getLogger(PartnerBankServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;

	private PartnerBankDAO partnerBankDAO;

	private FinAdvancePaymentsDAO finAdvancePaymentsDAO;

	private FinTypePartnerBankDAO finTypePartnerBankDAO;

	private PaymentInstructionDAO paymentInstructionDAO;

	private FeePostingsDAO feePostingsDAO;

	private PresentmentDetailDAO presentmentDetailDAO;

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		PartnerBank partnerBank = (PartnerBank) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;

		if (partnerBank.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		List<PartnerBankModes> modes = partnerBank.getPartnerBankModesList();
		List<PartnerBranchModes> branches = partnerBank.getPartnerBranchModesList();
		long partnerBankId = partnerBank.getPartnerBankId();

		if (partnerBank.isNewRecord()) {
			partnerBankDAO.save(partnerBank, tableType);
			if (modes != null && modes.size() > 0) {
				partnerBankDAO.deletePartner(partnerBank);
				partnerBankDAO.saveList(modes, partnerBankId);

			}
			if (branches != null && branches.size() > 0) {
				partnerBankDAO.deletePartnerBranch(partnerBank);
				partnerBankDAO.saveBranchList(branches, partnerBankId);
			}

		} else {
			partnerBankDAO.update(partnerBank, tableType);
			if (modes != null && modes.size() > 0) {
				partnerBankDAO.deletePartner(partnerBank);
				partnerBankDAO.saveList(modes, partnerBankId);
			}
			if (branches != null && branches.size() > 0) {
				partnerBankDAO.deletePartnerBranch(partnerBank);
				partnerBankDAO.saveBranchList(branches, partnerBankId);
			}
		}

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		PartnerBank partnerBank = (PartnerBank) auditHeader.getAuditDetail().getModelData();
		partnerBankDAO.delete(partnerBank, TableType.MAIN_TAB);
		partnerBankDAO.deletePartner(partnerBank);
		partnerBankDAO.deletePartnerBranch(partnerBank);
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public PartnerBank getPartnerBankById(long id) {
		return partnerBankDAO.getPartnerBankById(id, "_View");
	}

	@Override
	public List<PartnerBankModes> getPartnerBankModesId(long partnerBankId) {
		return partnerBankDAO.getPartnerBankModesId(partnerBankId);

	}

	public PartnerBank getApprovedPartnerBankById(long id) {
		return partnerBankDAO.getPartnerBankById(id, "_AView");
	}

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		String tranType = "";
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		PartnerBank partnerBank = new PartnerBank();
		BeanUtils.copyProperties((PartnerBank) auditHeader.getAuditDetail().getModelData(), partnerBank);
		partnerBankDAO.delete(partnerBank, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(partnerBank.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(partnerBankDAO.getPartnerBankById(partnerBank.getPartnerBankId(), ""));
		}

		if (partnerBank.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			partnerBankDAO.delete(partnerBank, TableType.MAIN_TAB);

		} else {
			partnerBank.setRoleCode("");
			partnerBank.setNextRoleCode("");
			partnerBank.setTaskId("");
			partnerBank.setNextTaskId("");
			partnerBank.setWorkflowId(0);

			if (partnerBank.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				partnerBank.setRecordType("");
				partnerBankDAO.save(partnerBank, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				partnerBank.setRecordType("");
				partnerBankDAO.update(partnerBank, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(partnerBank);

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		PartnerBank partnerBank = (PartnerBank) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		partnerBankDAO.delete(partnerBank, TableType.TEMP_TAB);

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader) {
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
		PartnerBank partnerBank = (PartnerBank) auditDetail.getModelData();
		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = partnerBank.getPartnerBankCode();
		errParm[0] = PennantJavaUtil.getLabel("label_PartnerBankCode") + ":" + valueParm[0];

		// Check the unique keys.
		if (partnerBank.isNewRecord() && partnerBankDAO.isDuplicateKey(partnerBank.getPartnerBankId(),
				partnerBank.getPartnerBankCode(), partnerBank.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
		}

		// Duplicate Partner Bank Code
		if (partnerBank.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW) && partnerBank.isNewRecord()
				&& getPartnerCodeExist(partnerBank.getPartnerBankCode(), "_View")) {
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41008", errParm, valueParm));
		}

		// Check Dependency Validation
		if (PennantConstants.RECORD_TYPE_DEL.equals(partnerBank.getRecordType())) {
			boolean isPartnerBankUsed = checkDependencyValidation(partnerBank.getPartnerBankId());

			if (isPartnerBankUsed) {
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", errParm, valueParm));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public boolean getPartnerCodeExist(String partnerBankCodeValue, String type) {
		logger.debug(Literal.ENTERING);

		boolean codeExist = false;

		if (partnerBankDAO.geBankCodeCount(partnerBankCodeValue, type) != 0) {
			codeExist = true;
		}

		logger.debug(Literal.LEAVING);

		return codeExist;
	}

	@Override
	public List<PartnerBranchModes> getPartnerBranchModesId(long id) {
		return partnerBankDAO.getPartnerBranchModesId(id);
	}

	@Override
	public String getBankCodeById(long partnerBankId) {
		return partnerBankDAO.getBankCodeById(partnerBankId);
	}

	private boolean checkDependencyValidation(long partnerBankId) {
		logger.debug(Literal.ENTERING);

		int count = 0;

		count = finTypePartnerBankDAO.getAssignedPartnerBankCount(partnerBankId, TableType.VIEW);
		if (count > 0) {
			return true;
		}

		count = finAdvancePaymentsDAO.getAssignedPartnerBankCount(partnerBankId, "_View");
		if (count > 0) {
			return true;
		}

		count = paymentInstructionDAO.getAssignedPartnerBankCount(partnerBankId, "_View");
		if (count > 0) {
			return true;
		}

		count = feePostingsDAO.getAssignedPartnerBankCount(partnerBankId, "_View");
		if (count > 0) {
			return true;
		}

		count = presentmentDetailDAO.getAssignedPartnerBankCount(partnerBankId, "_View");
		if (count > 0) {
			return true;
		}

		logger.debug(Literal.LEAVING);
		return false;
	}

	@Autowired
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	@Autowired
	public void setPartnerBankDAO(PartnerBankDAO partnerBankDAO) {
		this.partnerBankDAO = partnerBankDAO;
	}

	@Autowired
	public void setFinAdvancePaymentsDAO(FinAdvancePaymentsDAO finAdvancePaymentsDAO) {
		this.finAdvancePaymentsDAO = finAdvancePaymentsDAO;
	}

	@Autowired
	public void setFinTypePartnerBankDAO(FinTypePartnerBankDAO finTypePartnerBankDAO) {
		this.finTypePartnerBankDAO = finTypePartnerBankDAO;
	}

	@Autowired
	public void setPaymentInstructionDAO(PaymentInstructionDAO paymentInstructionDAO) {
		this.paymentInstructionDAO = paymentInstructionDAO;
	}

	@Autowired
	public void setFeePostingsDAO(FeePostingsDAO feePostingsDAO) {
		this.feePostingsDAO = feePostingsDAO;
	}

	@Autowired
	public void setPresentmentDetailDAO(PresentmentDetailDAO presentmentDetailDAO) {
		this.presentmentDetailDAO = presentmentDetailDAO;
	}

}