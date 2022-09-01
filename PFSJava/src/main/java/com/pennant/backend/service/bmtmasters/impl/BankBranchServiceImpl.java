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

	@Autowired(required = false)
	@Qualifier("bankBranchPostValidationHook")
	private PostValidationHook postValidationHook;

	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	/**
	 * @param auditHeaderDAO the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	/**
	 * @return the bankBranchDAO
	 */
	public BankBranchDAO getBankBranchDAO() {
		return bankBranchDAO;
	}

	/**
	 * @param bankBranchDAO the bankBranchDAO to set
	 */
	public void setBankBranchDAO(BankBranchDAO bankBranchDAO) {
		this.bankBranchDAO = bankBranchDAO;
	}

	/**
	 * @return the bankBranch
	 */
	@Override
	public BankBranch getBankBranch() {
		return getBankBranchDAO().getBankBranch();
	}

	/**
	 * @return the bankBranch for New Record
	 */
	@Override
	public BankBranch getNewBankBranch() {
		return getBankBranchDAO().getNewBankBranch();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table BankBranches/BankBranches_Temp
	 * by using BankBranchDAO's save method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using BankBranchDAO's update method 3) Audit the record in to AuditHeader and AdtBankBranches
	 * by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @param boolean     onlineRequest
	 * @return auditHeader
	 */

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType = "";
		BankBranch bankBranch = (BankBranch) auditHeader.getAuditDetail().getModelData();

		if (bankBranch.isWorkflow()) {
			tableType = "_Temp";
		}

		if (bankBranch.isNewRecord()) {
			bankBranch.setId(getBankBranchDAO().save(bankBranch, tableType));
			auditHeader.getAuditDetail().setModelData(bankBranch);
			auditHeader.setAuditReference(String.valueOf(bankBranch.getBankBranchID()));
		} else {
			getBankBranchDAO().update(bankBranch, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * BankBranches by using BankBranchDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtBankBranches by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		BankBranch bankBranch = (BankBranch) auditHeader.getAuditDetail().getModelData();
		getBankBranchDAO().delete(bankBranch, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getBankBranchById fetch the details by using BankBranchDAO's getBankBranchById method.
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return BankBranch
	 */

	@Override
	public BankBranch getBankBranchById(long id) {
		return getBankBranchDAO().getBankBranchById(id, "_View");
	}

	/**
	 * getApprovedBankBranchById fetch the details by using BankBranchDAO's getBankBranchById method . with parameter id
	 * and type as blank. it fetches the approved records from the BankBranches.
	 * 
	 * @param id (int)
	 * @return BankBranch
	 */

	@Override
	public BankBranch getApprovedBankBranchById(long id) {
		return getBankBranchDAO().getBankBranchById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getBankBranchDAO().delete with
	 * parameters bankBranch,"" b) NEW Add new record in to main table by using getBankBranchDAO().save with parameters
	 * bankBranch,"" c) EDIT Update record in the main table by using getBankBranchDAO().update with parameters
	 * bankBranch,"" 3) Delete the record from the workFlow table by using getBankBranchDAO().delete with parameters
	 * bankBranch,"_Temp" 4) Audit the record in to AuditHeader and AdtBankBranches by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtBankBranches by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		BankBranch bankBranch = new BankBranch();
		BeanUtils.copyProperties(auditHeader.getAuditDetail().getModelData(), bankBranch);

		if (bankBranch.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getBankBranchDAO().delete(bankBranch, "");

		} else {
			bankBranch.setRoleCode("");
			bankBranch.setNextRoleCode("");
			bankBranch.setTaskId("");
			bankBranch.setNextTaskId("");
			bankBranch.setWorkflowId(0);

			if (bankBranch.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				bankBranch.setRecordType("");
				getBankBranchDAO().save(bankBranch, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				bankBranch.setRecordType("");
				getBankBranchDAO().update(bankBranch, "");
			}
		}

		getBankBranchDAO().delete(bankBranch, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(bankBranch);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getBankBranchDAO().delete with parameters bankBranch,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtBankBranches by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		BankBranch bankBranch = (BankBranch) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getBankBranchDAO().delete(bankBranch, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) validate the audit detail 2) if any error/Warnings then
	 * assign the to auditHeader 3) identify the nextprocess
	 * 
	 * @param AuditHeader (auditHeader)
	 * @param boolean     onlineRequest
	 * @return auditHeader
	 */

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		doPostHookValidation(auditHeader);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
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

	/**
	 * Validation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details from the
	 * tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5) for any
	 * mismatch conditions Fetch the error details from getBankBranchDAO().getErrorDetail with Error ID and language as
	 * parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader (auditHeader)
	 * @param boolean     onlineRequest
	 * @return auditHeader
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		BankBranch bankBranch = (BankBranch) auditDetail.getModelData();
		// Check the unique keys.
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

			if (mandateCount != 0 || beneficiaryCount != 0 || disbCount != 0) {
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

	/**
	 * fetch Bank Branch details by IFSC code.
	 * 
	 * @param ifsc
	 * @return BankBranch
	 */
	@Override
	public BankBranch getBankBrachByIFSC(String ifsc) {
		return bankBranchDAO.getBankBrachByIFSC(ifsc, "");
	}

	/**
	 * fetch Bank Branch details by bankCode and branchCode.
	 * 
	 * @param bankCode
	 * @param branchCode
	 * @return BankBranch
	 */
	@Override
	public BankBranch getBankBrachByCode(String bankCode, String branchCode) {
		return bankBranchDAO.getBankBrachByCode(bankCode, branchCode, "");
	}

	@Override
	public BankBranch getBankBrachByMicr(String micr) {
		return getBankBranchDAO().getBankBrachByMicr(micr, "");
	}

	/**
	 * fetch Bank Branch details by IFSC code.
	 * 
	 * @param ifsc
	 * @return BankBranch
	 */
	@Override
	public BankBranch getBankBranchByIFSC(String ifsc) {
		return bankBranchDAO.getBankBranchByIFSC(ifsc, "");
	}

	/**
	 * fetch Bank Branch Count by IFSC code.
	 * 
	 * @param ifsc
	 * @return BankBranchCount
	 */
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

		String[] valueParm = new String[2];

		BankBranch bankBranch = new BankBranch();

		if (StringUtils.isBlank(iFSC) && (StringUtils.isBlank(bankCode) || StringUtils.isBlank(branchCode))) {
			valueParm[0] = "IFSC";
			valueParm[1] = "Bank/Branch code";

			bankBranch.setError(ErrorUtil.getErrorDetail(new ErrorDetail("90215", valueParm)));
			return bankBranch;
		}

		if (StringUtils.isNotBlank(bankCode) && StringUtils.isNotBlank(branchCode)) {
			bankBranch = getBankBrachByCode(bankCode, branchCode);
			if (bankBranch == null) {
				valueParm[0] = bankCode;
				valueParm[1] = branchCode;

				bankBranch = new BankBranch();
				bankBranch.setError(ErrorUtil.getErrorDetail(new ErrorDetail("90302", valueParm)));
				return bankBranch;
			} else {
				if (StringUtils.isNotBlank(micr)) {
					if (!micr.equals(bankBranch.getMICR())) {
						valueParm[0] = "MICR";
						valueParm[1] = micr;

						bankBranch.setError(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));
						return bankBranch;
					}
				}
			}
		} else if (StringUtils.isNotEmpty(iFSC) && StringUtils.isNotEmpty(micr)) {
			bankBranch = getBankBranchByIFSCMICR(iFSC, micr);
			if (bankBranch == null) {
				valueParm[0] = iFSC;
				valueParm[1] = micr;

				bankBranch = new BankBranch();
				bankBranch.setError(ErrorUtil.getErrorDetail(new ErrorDetail("90703", valueParm)));
				return bankBranch;
			} else {
				if (StringUtils.isNotBlank(micr)) {
					if (!micr.equals(bankBranch.getMICR())) {
						valueParm[0] = "MICR";
						valueParm[1] = micr;

						bankBranch.setError(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));
						return bankBranch;
					}
				}
			}
		} else if (StringUtils.isNotBlank(iFSC)) {
			if (getBankBranchCountByIFSC(iFSC, "") > 1) {
				String[] parm = new String[1];
				parm[0] = iFSC;

				bankBranch.setError(ErrorUtil.getErrorDetail(new ErrorDetail("90702", valueParm)));
				return bankBranch;
			}

			bankBranch = getBankBranchByIFSC(iFSC);

			if (bankBranch == null) {
				valueParm = new String[2];
				valueParm[0] = "IFSC";
				valueParm[1] = iFSC;
				bankBranch = new BankBranch();
				bankBranch.setError(ErrorUtil.getErrorDetail(new ErrorDetail("90301", valueParm)));
				return bankBranch;
			} else {
				if (StringUtils.isNotBlank(micr)) {
					if (!micr.equals(bankBranch.getMICR())) {
						valueParm[0] = "MICR";
						valueParm[1] = micr;

						bankBranch.setError(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));
						return bankBranch;
					}
				}
			}
		}

		if (StringUtils.isNotBlank(bankCode)) {
			String branchBankCode = StringUtils.trimToEmpty(bankBranch.getBankCode());
			if (!branchBankCode.equals(bankCode)) {
				valueParm = new String[1];
				valueParm[0] = iFSC;

				bankBranch.setError(ErrorUtil.getErrorDetail(new ErrorDetail("99020", valueParm)));
				return bankBranch;
			}
		}
		logger.debug(Literal.LEAVING);

		return bankBranch;
	}

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
		case DDM:
			if (!bankBranch.isDda()) {
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

	public MandateDAO getMandateDAO() {
		return mandateDAO;
	}

	public void setMandateDAO(MandateDAO mandateDAO) {
		this.mandateDAO = mandateDAO;
	}

	public FinAdvancePaymentsDAO getFinAdvancePaymentsDAO() {
		return finAdvancePaymentsDAO;
	}

	public void setFinAdvancePaymentsDAO(FinAdvancePaymentsDAO finAdvancePaymentsDAO) {
		this.finAdvancePaymentsDAO = finAdvancePaymentsDAO;
	}

	public BeneficiaryDAO getBeneficiaryDAO() {
		return beneficiaryDAO;
	}

	public void setBeneficiaryDAO(BeneficiaryDAO beneficiaryDAO) {
		this.beneficiaryDAO = beneficiaryDAO;
	}

	@Override
	public int getAccNoLengthByIFSC(String ifscCode) {
		return getBankBranchDAO().getAccNoLengthByIFSC(ifscCode, "_View");
	}

}