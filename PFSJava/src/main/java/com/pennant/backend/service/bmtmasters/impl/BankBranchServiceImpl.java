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
 * * FileName : BankBranchServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 17-10-2016 * *
 * Modified Date : 17-10-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 17-10-2016 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.bmtmasters.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.beneficiary.BeneficiaryDAO;
import com.pennant.backend.dao.bmtmasters.BankBranchDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.dao.partnerbank.PartnerBankDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.pff.mandate.InstrumentType;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.service.hook.PostValidationHook;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>BankBranch</b>.<br>
 * 
 */
public class BankBranchServiceImpl extends GenericService<BankBranch> implements BankBranchService {
	private static final Logger logger = LogManager.getLogger(BankBranchServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private BankBranchDAO bankBranchDAO;
	private MandateDAO mandateDAO;
	private FinAdvancePaymentsDAO finAdvancePaymentsDAO;
	private BeneficiaryDAO beneficiaryDAO;
	private PartnerBankDAO partnerBankDAO;
	private PostValidationHook postValidationHook;

	@Override
	public BankBranch getBankBranch() {
		return bankBranchDAO.getBankBranch();
	}

	@Override
	public BankBranch getNewBankBranch() {
		return bankBranchDAO.getNewBankBranch();
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}
		String tableType = "";
		BankBranch bankBranch = (BankBranch) auditHeader.getAuditDetail().getModelData();

		if (bankBranch.isWorkflow()) {
			tableType = TableType.TEMP_TAB.getSuffix();
		}

		if (bankBranch.isNewRecord()) {
			bankBranch.setId(bankBranchDAO.save(bankBranch, tableType));
			auditHeader.getAuditDetail().setModelData(bankBranch);
			auditHeader.setAuditReference(String.valueOf(bankBranch.getBankBranchID()));
		} else {
			bankBranchDAO.update(bankBranch, tableType);
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

		BankBranch bankBranch = (BankBranch) auditHeader.getAuditDetail().getModelData();
		bankBranchDAO.delete(bankBranch, "");

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public BankBranch getBankBranchById(long id) {
		return bankBranchDAO.getBankBranchById(id, TableType.VIEW.getSuffix());
	}

	@Override
	public BankBranch getApprovedBankBranchById(long id) {
		return bankBranchDAO.getBankBranchById(id, "_AView");
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		String tranType = "";
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		BankBranch bankBranch = new BankBranch();
		BeanUtils.copyProperties(auditHeader.getAuditDetail().getModelData(), bankBranch);

		if (bankBranch.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			bankBranchDAO.delete(bankBranch, "");

		} else {
			bankBranch.setRoleCode("");
			bankBranch.setNextRoleCode("");
			bankBranch.setTaskId("");
			bankBranch.setNextTaskId("");
			bankBranch.setWorkflowId(0);

			if (bankBranch.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				bankBranch.setRecordType("");
				bankBranchDAO.save(bankBranch, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				bankBranch.setRecordType("");
				bankBranchDAO.update(bankBranch, "");
			}
		}

		bankBranchDAO.delete(bankBranch, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(bankBranch);

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		BankBranch bankBranch = (BankBranch) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		bankBranchDAO.delete(bankBranch, "_Temp");

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		doPostHookValidation(auditHeader);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private void doPostHookValidation(AuditHeader auditHeader) {
		if (postValidationHook != null) {
			List<ErrorDetail> errorDetails = postValidationHook.validation(auditHeader);

			if (errorDetails != null) {
				errorDetails = ErrorUtil.getErrorDetails(errorDetails, auditHeader.getUsrLanguage());
				auditHeader.setErrorList(errorDetails);
			}
		}
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		BankBranch bankBranch = (BankBranch) auditDetail.getModelData();

		if (bankBranch.isNewRecord() && PennantConstants.RECORD_TYPE_NEW.equals(bankBranch.getRecordType())
				&& bankBranchDAO.isDuplicateKey(bankBranch.getBankCode(), bankBranch.getBranchCode(),
						bankBranch.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];
			parameters[0] = PennantJavaUtil.getLabel("label_BranchCode") + ": " + bankBranch.getBranchCode() + " And ";
			parameters[1] = PennantJavaUtil.getLabel("label_BankCode") + ": " + bankBranch.getBankCode();
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		if (PennantConstants.RECORD_TYPE_DEL.equals(StringUtils.trimToEmpty(bankBranch.getRecordType()))) {
			long bankBranchID = bankBranch.getBankBranchID();

			int mandateCount = mandateDAO.getBranch(bankBranchID, "");
			int disbCount = finAdvancePaymentsDAO.getBranch(bankBranchID, "");
			int beneficiaryCount = beneficiaryDAO.getBranch(bankBranchID, "");
			boolean bankBnkcount = partnerBankDAO.getPartnerBankbyBankBranch(bankBranch.getBranchCode());

			if (mandateCount != 0 || beneficiaryCount != 0 || disbCount != 0 || bankBnkcount) {
				String[] errParm = new String[1];
				String[] valueParm = new String[1];
				valueParm[0] = String.valueOf(bankBranch.getBranchCode());

				errParm[0] = PennantJavaUtil.getLabel("label_BankBranchCode") + ":" + valueParm[0];

				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "41006", errParm, valueParm), usrLanguage));
			}
		} else {
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			String ifsc = bankBranch.getIFSC();
			valueParm[0] = ifsc;
			errParm[0] = PennantJavaUtil.getLabel("label_IFSC") + " : " + valueParm[0];

			String recordStatus = StringUtils.trimToEmpty(bankBranch.getRecordStatus());
			if (!PennantConstants.RCD_STATUS_REJECTED.equals(recordStatus)
					&& !PennantConstants.RCD_STATUS_RESUBMITTED.equals(recordStatus)
					&& !PennantConstants.RCD_STATUS_CANCELLED.equals(recordStatus)) {
				if (!bankBranch.isAllowMultipleIFSC()) {
					int count = bankBranchDAO.getBankBranchByIFSC(ifsc, bankBranch.getId(), "_View");
					if (count != 0) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41014", errParm, valueParm), usrLanguage));
					}
				}

				if (bankBranch.isNewRecord() && PennantConstants.RECORD_TYPE_NEW.equals(bankBranch.getRecordType())) {
					BankBranch bb = bankBranchDAO.getBankBranchByIFSCMICR(ifsc, bankBranch.getMICR());
					if (bb != null) {
						StringBuilder error = new StringBuilder();
						error.append(PennantJavaUtil.getLabel("label_IFSC")).append(" : ");
						error.append(ifsc).append(" and  ");
						error.append(PennantJavaUtil.getLabel("label_MICR")).append(" : ");
						error.append(bankBranch.getMICR());
						errParm[0] = error.toString();

						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41014", errParm, valueParm), usrLanguage));
					}
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public BankBranch getBankBrachByIFSC(String ifsc) {
		return bankBranchDAO.getBankBrachByIFSC(ifsc, "");
	}

	@Override
	public BankBranch getBankBrachByCode(String bankCode, String branchCode) {
		return bankBranchDAO.getBankBrachByCode(bankCode, branchCode, "");
	}

	@Override
	public BankBranch getBankBrachByMicr(String micr) {
		return bankBranchDAO.getBankBrachByMicr(micr, "");
	}

	@Override
	public BankBranch getBankBranchByIFSC(String ifsc) {
		return bankBranchDAO.getBankBranchByIFSC(ifsc, "");
	}

	@Override
	public int getBankBranchCountByIFSC(final String iFSC, String type) {
		return bankBranchDAO.getBankBranchCountByIFSC(iFSC, type);
	}

	@Override
	public BankBranch getBankBranchByIFSCMICR(String iFSC, String micr) {
		return bankBranchDAO.getBankBranchByIFSCMICR(iFSC, micr);
	}

	public BankBranch getBankBranch(String iFSC, String micr, String bankCode, String branchCode) {
		logger.debug(Literal.ENTERING);

		BankBranch bankBranch = new BankBranch();

		if (StringUtils.isBlank(iFSC) && (StringUtils.isBlank(bankCode) || StringUtils.isBlank(branchCode))) {
			bankBranch.setError(getError("90313", ""));
			return bankBranch;
		}

		if (StringUtils.isNotBlank(bankCode) && StringUtils.isNotBlank(branchCode)) {
			bankBranch = bankBranchDAO.getBankBrachByCode(bankCode, branchCode, "");
			if (bankBranch == null) {
				bankBranch = new BankBranch();
				bankBranch.setError(getError("90302", bankCode, branchCode));
				return bankBranch;
			}
		}

		if (StringUtils.isNotEmpty(iFSC) && StringUtils.isNotEmpty(micr)) {
			bankBranch = bankBranchDAO.getBankBranchByIFSCMICR(iFSC, micr);
			if (bankBranch == null) {
				bankBranch = new BankBranch();
				bankBranch.setError(getError("90703", iFSC, micr));
				return bankBranch;
			}
		}

		if (StringUtils.isNotBlank(micr) && !micr.equals(bankBranch.getMICR())) {
			bankBranch.setError(getError("90701", "MICR", micr));
			return bankBranch;
		}

		if (StringUtils.isNotBlank(iFSC) && bankBranchDAO.getBankBranchCountByIFSC(iFSC, "") > 1) {
			if (StringUtils.isEmpty(micr)) {
				bankBranch.setError(getError("90702", iFSC));
			}
			return bankBranch;
		}

		bankBranch = bankBranchDAO.getBankBranchByIFSC(iFSC, "");
		if (bankBranch == null) {
			bankBranch = new BankBranch();
			bankBranch.setError(getError("90301", "IFSC", iFSC));
			return bankBranch;
		}

		if (StringUtils.isNotBlank(micr) && !micr.equals(bankBranch.getMICR())) {
			bankBranch.setError(getError("90701", "MICR", micr));
			return bankBranch;
		}

		if (StringUtils.isNotBlank(bankCode) && !StringUtils.trimToEmpty(bankBranch.getBankCode()).equals(bankCode)) {
			bankBranch.setError(getError("99020", iFSC));
			return bankBranch;
		}

		logger.debug(Literal.LEAVING);
		return bankBranch;
	}

	@Override
	public boolean validateBranchCode(BankBranch bankBranch, String mandateType) {
		InstrumentType instrumentType = InstrumentType.valueOf(mandateType);

		if (instrumentType == null) {
			return false;
		}

		switch (instrumentType) {
		case ECS:
			if (!bankBranch.isEcs()) {
				return false;
			}
			break;
		case DD:
			if (!bankBranch.isDd()) {
				return false;
			}
			break;
		case NACH:
			if (!bankBranch.isNach()) {
				return false;
			}
			break;
		default:
			break;
		}

		return true;
	}

	@Override
	public BankBranch getBankBrachDetails(String ifsc, String bankCode) {
		return bankBranchDAO.getBankBrachDetails(ifsc, bankCode);
	}

	@Override
	public int getAccNoLengthByIFSC(String ifscCode) {
		return bankBranchDAO.getAccNoLengthByIFSC(ifscCode, "_View");
	}

	@Autowired
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	@Autowired
	public void setBankBranchDAO(BankBranchDAO bankBranchDAO) {
		this.bankBranchDAO = bankBranchDAO;
	}

	@Autowired
	public void setMandateDAO(MandateDAO mandateDAO) {
		this.mandateDAO = mandateDAO;
	}

	@Autowired
	public void setFinAdvancePaymentsDAO(FinAdvancePaymentsDAO finAdvancePaymentsDAO) {
		this.finAdvancePaymentsDAO = finAdvancePaymentsDAO;
	}

	@Autowired
	public void setBeneficiaryDAO(BeneficiaryDAO beneficiaryDAO) {
		this.beneficiaryDAO = beneficiaryDAO;
	}

	@Autowired(required = false)
	@Qualifier("bankBranchPostValidationHook")
	public void setPostValidationHook(PostValidationHook postValidationHook) {
		this.postValidationHook = postValidationHook;
	}

	@Autowired
	public void setPartnerBankDAO(PartnerBankDAO partnerBankDAO) {
		this.partnerBankDAO = partnerBankDAO;
	}
}