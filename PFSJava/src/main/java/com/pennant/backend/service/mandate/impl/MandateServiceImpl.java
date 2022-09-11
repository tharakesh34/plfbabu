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
 * * FileName : MandateServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 18-10-2016 * * Modified
 * Date : 18-10-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 18-10-2016 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.mandate.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.NumberToEnglishWords;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.MandateCheckDigitDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.bmtmasters.BankBranchDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.dao.mandate.MandateStatusDAO;
import com.pennant.backend.dao.mandate.MandateStatusUpdateDAO;
import com.pennant.backend.dao.partnerbank.PartnerBankDAO;
import com.pennant.backend.dao.rmtmasters.FinTypePartnerBankDAO;
import com.pennant.backend.model.applicationmaster.MandateCheckDigit;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.mandate.MandateStatusUpdate;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.mandate.InstrumentType;
import com.pennant.pff.mandate.MandateStatus;
import com.pennanttech.model.dms.DMSModule;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.dms.service.DMSService;
import com.pennanttech.pff.external.MandateProcesses;
import com.pennanttech.pff.presentment.model.PresentmentDetail;

public class MandateServiceImpl extends GenericService<Mandate> implements MandateService {
	private static final Logger logger = LogManager.getLogger(MandateServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private MandateDAO mandateDAO;
	private MandateStatusDAO mandateStatusDAO;
	private MandateStatusUpdateDAO mandateStatusUpdateDAO;
	private FinanceMainDAO financeMainDAO;
	private MandateCheckDigitDAO mandateCheckDigitDAO;
	private BankBranchDAO bankBranchDAO;
	private PartnerBankDAO partnerBankDAO;
	private MandateProcesses mandateProcesses;
	private MandateProcesses defaultMandateProcess;
	private FinTypePartnerBankDAO finTypePartnerBankDAO;

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		String tableType = "";
		Mandate mandate = (Mandate) auditHeader.getAuditDetail().getModelData();

		if (mandate.isWorkflow()) {
			tableType = "_Temp";
		}

		if (mandate.isNewRecord()) {
			getDocument(mandate);
			mandate.setId(mandateDAO.save(mandate, tableType));
			auditHeader.getAuditDetail().setModelData(mandate);
			auditHeader.setAuditReference(String.valueOf(mandate.getMandateID()));
		} else {
			getDocument(mandate);
			mandateDAO.update(mandate, tableType);
			if (StringUtils.trimToEmpty(mandate.getModule()).equals(MandateConstants.MODULE_REGISTRATION)) {
				com.pennant.backend.model.mandate.MandateStatus mandateStatus = new com.pennant.backend.model.mandate.MandateStatus();
				mandateStatus.setMandateID(mandate.getMandateID());
				mandateStatus.setStatus(mandate.getStatus());
				mandateStatus.setReason(mandate.getReason());
				mandateStatus.setChangeDate(mandate.getInputDate());
				mandateStatusDAO.save(mandateStatus, "");
			}
		}

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * Mandates by using MandateDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtMandates by using auditHeaderDAO.addAudit(auditHeader)
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

		Mandate mandate = (Mandate) auditHeader.getAuditDetail().getModelData();
		mandateDAO.delete(mandate, "");

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getMandateById fetch the details by using MandateDAO's getMandateById method.
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return Mandate
	 */

	@Override
	public Mandate getMandateById(long id) {
		return mandateDAO.getMandateById(id, "_View");
	}

	@Override
	public Mandate getMandateStatusUpdateById(long id, String status) {
		return mandateDAO.getMandateByStatus(id, status, "_View");
	}

	/**
	 * getApprovedMandateById fetch the details by using MandateDAO's getMandateById method . with parameter id and type
	 * as blank. it fetches the approved records from the Mandates.
	 * 
	 * @param id (int)
	 * @return Mandate
	 */

	public Mandate getApprovedMandateById(long id) {
		return mandateDAO.getMandateById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using mandateDAO.delete with parameters
	 * mandate,"" b) NEW Add new record in to main table by using mandateDAO.save with parameters mandate,"" c) EDIT
	 * Update record in the main table by using mandateDAO.update with parameters mandate,"" 3) Delete the record from
	 * the workFlow table by using mandateDAO.delete with parameters mandate,"_Temp" 4) Audit the record in to
	 * AuditHeader and AdtMandates by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to
	 * AuditHeader and AdtMandates by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		Mandate mandate = new Mandate();
		BeanUtils.copyProperties((Mandate) auditHeader.getAuditDetail().getModelData(), mandate);

		if (mandate.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			// For Mandate deletion is not required , so make it as inActive
			mandateDAO.updateActive(mandate.getMandateID(), com.pennant.pff.mandate.MandateStatus.CANCEL, false);

			com.pennant.backend.model.mandate.MandateStatus mandateStatus = new com.pennant.backend.model.mandate.MandateStatus();
			mandateStatus.setMandateID(mandate.getMandateID());
			mandateStatus.setStatus(MandateStatus.CANCEL);
			mandateStatus.setReason(mandate.getReason());
			mandateStatus.setChangeDate(mandate.getInputDate());
			mandateStatusDAO.save(mandateStatus, "");

		} else {
			mandate.setRoleCode("");
			mandate.setNextRoleCode("");
			mandate.setTaskId("");
			mandate.setNextTaskId("");
			mandate.setWorkflowId(0);

			boolean isApproved = false;

			if (MandateStatus.isApproved(mandate.getStatus())) {
				isApproved = true;
			}

			if (MandateStatus.isRelease(mandate.getStatus())) {
				mandate.setStatus(MandateStatus.APPROVED);
			} else if (!MandateStatus.isHold(mandate.getStatus())) {
				mandate.setStatus(MandateStatus.NEW);
			}

			if (isApproved) {
				mandate.setStatus(MandateStatus.APPROVED);
			}

			if (StringUtils.equals(mandate.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
				if (mandate.isApproveMandate()) {
					mandate.setStatus(MandateStatus.APPROVED);
				} else {
					mandate.setStatus(MandateStatus.NEW);
				}
			}

			if (InstrumentType.isEMandate(mandate.getMandateType())) {
				if (StringUtils.isNotBlank(mandate.getMandateRef())) {
					mandate.setStatus(MandateStatus.APPROVED);
				} else {
					mandate.setStatus(MandateStatus.AWAITCON);
				}
			}

			getDocument(mandate);

			if (mandate.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				mandate.setRecordType("");
				mandate.setMandateID(mandateDAO.save(mandate, ""));
			} else {
				tranType = PennantConstants.TRAN_UPD;
				mandate.setRecordType("");
				mandateDAO.update(mandate, "");
				mandate.setModificationDate(new Timestamp(System.currentTimeMillis()));
			}

			com.pennant.backend.model.mandate.MandateStatus mandateStatus = new com.pennant.backend.model.mandate.MandateStatus();
			mandateStatus.setMandateID(mandate.getMandateID());
			mandateStatus.setStatus(mandate.getStatus());
			mandateStatus.setReason(mandate.getReason());
			mandateStatus.setChangeDate(mandate.getInputDate());

			mandateStatusDAO.save(mandateStatus, "");

			// Mandate Registration purpose

			try {

				BigDecimal maxlimt = PennantApplicationUtil.formateAmount(mandate.getMaxLimit(),
						CurrencyUtil.getFormat(mandate.getMandateCcy()));
				mandate.setAmountInWords(NumberToEnglishWords.getNumberToWords(maxlimt.toBigInteger()));

				if (StringUtils.equals(mandate.getSourceId(), PennantConstants.FINSOURCE_ID_API)
						|| mandate.isSecondaryMandate()) {
					BankBranch bankBranch = bankBranchDAO.getBankBrachByMicr(mandate.getMICR(), "");
					mandate.setBankName(bankBranch.getBankName());
					mandate.setBranchDesc(bankBranch.getBranchDesc());
					if (!mandate.isSecondaryMandate()) {
						mandate.setApprovalID(String.valueOf(mandate.getUserDetails().getUserId()));
					}
				}

				boolean register = getMandateProcess().registerMandate(mandate);
				if (register) {
					mandate.setStatus(MandateStatus.INPROCESS);
					mandateDAO.updateStatusAfterRegistration(mandate.getMandateID(), MandateStatus.INPROCESS);
					mandateStatus.setMandateID(mandate.getMandateID());
					mandateStatus.setStatus(mandate.getStatus());
					mandateStatus.setReason(mandate.getReason());
					mandateStatus.setChangeDate(mandate.getInputDate());
					mandateStatusDAO.save(mandateStatus, "");
				}

			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}

		}

		if ((!StringUtils.equals(mandate.getSourceId(), PennantConstants.FINSOURCE_ID_API)
				&& !mandate.isSecondaryMandate())) {
			mandateDAO.delete(mandate, "_Temp");
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			auditHeaderDAO.addAudit(auditHeader);
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(mandate);

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		Mandate mandate = (Mandate) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		mandateDAO.delete(mandate, "_Temp");

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	public List<ErrorDetail> doValidations(Mandate mandate) {
		List<ErrorDetail> details = new ArrayList<>();
		if (StringUtils.isNotBlank(mandate.getBarCodeNumber())) {
			Pattern pattern = Pattern
					.compile(PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_BARCODE_NUMBER));
			Matcher matcher = pattern.matcher(mandate.getBarCodeNumber());

			if (matcher.matches() == false) {
				String[] valueParm = new String[1];
				valueParm[0] = mandate.getBarCodeNumber();
				details.add(ErrorUtil.getErrorDetail(new ErrorDetail("barCodeNumber", "90404", valueParm, valueParm)));
			}
		}
		if (StringUtils.isNotBlank(mandate.getAccHolderName())) {
			Pattern pattern = Pattern.compile(
					PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_ACCOUNT_HOLDER_NAME));

			Matcher matcher = pattern.matcher(mandate.getAccHolderName());

			if (!matcher.matches()) {
				String[] valueParm = new String[1];
				valueParm[0] = "AccHolderName";
				details.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN"));
			}
		}
		if (!StringUtils.isBlank(mandate.getJointAccHolderName())) {
			Pattern pattern = Pattern.compile(
					PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_ACCOUNT_HOLDER_NAME));

			Matcher matcher = pattern.matcher(mandate.getJointAccHolderName());

			if (!matcher.matches()) {
				String[] valueParm = new String[1];
				valueParm[0] = "JointAccHolderName";
				details.add(ErrorUtil.getErrorDetail(new ErrorDetail("90237", "", valueParm), "EN"));
			}
		}
		return details;

	}

	/**
	 * Validation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details from the
	 * tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5) for any
	 * mismatch conditions Fetch the error details from mandateDAO.getErrorDetail with Error ID and language as
	 * parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader (auditHeader)
	 * @param boolean     onlineRequest
	 * @return auditHeader
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		Mandate mandate = (Mandate) auditDetail.getModelData();

		Mandate tempMandate = null;
		if (mandate.isWorkflow()) {
			tempMandate = mandateDAO.getMandateById(mandate.getId(), "_Temp");
		}
		Mandate befMandate = mandateDAO.getMandateById(mandate.getId(), "");

		Mandate oldMandate = mandate.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(mandate.getId());
		errParm[0] = PennantJavaUtil.getLabel("label_MandateID") + ":" + valueParm[0];

		if (mandate.isNewRecord()) { // for New record or new record into work flow

			if (!mandate.isWorkflow()) {// With out Work flow only new records
				if (befMandate != null) { // Record Already Exists in the table
											// then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (mandate.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
																						// records
																						// type
																						// is
																						// new
					if (befMandate != null || tempMandate != null) { // if
																		// records
																		// already
																		// exists
																		// in
																		// the
																		// main
																		// table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befMandate == null || tempMandate != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!mandate.isWorkflow()) { // With out Work flow for update and
											// delete

				if (befMandate == null) { // if records not exists in the main
											// table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldMandate != null && !oldMandate.getLastMntOn().equals(befMandate.getLastMntOn())) {
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

				if (tempMandate == null) { // if records not exists in the Work
											// flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempMandate != null && oldMandate != null
						&& !oldMandate.getLastMntOn().equals(tempMandate.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}
		if (StringUtils.trimToEmpty(mandate.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)) {
			int count = financeMainDAO.getFinanceCountByMandateId(mandate.getMandateID());
			if (count != 0) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "41006", errParm, valueParm), usrLanguage));
			}
		}
		String barCode = mandate.getBarCodeNumber();
		if (!StringUtils.isEmpty(barCode)
				&& !StringUtils.trimToEmpty(mandate.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)
				&& !StringUtils.equals(method, PennantConstants.method_doReject)) {
			String[] errParm1 = new String[1];
			String[] valueParm1 = new String[1];
			valueParm1[0] = barCode;
			errParm1[0] = PennantJavaUtil.getLabel("label_BarCodeNumber") + " : " + valueParm1[0];

			char lastchar = (char) barCode.charAt(barCode.length() - 1);
			int reminder = checkSum(barCode);

			MandateCheckDigit checkDigit = mandateCheckDigitDAO.getMandateCheckDigit(reminder, "");
			// Validation For BarCode CheckSum
			if (checkDigit != null) {
				if (checkDigit.getLookUpValue().charAt(0) != lastchar) {

					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "90405", errParm1, valueParm1), usrLanguage));
				}
			} else {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "90405", errParm1, valueParm1), usrLanguage));
			}
			/*
			 * //BarCode Unique Validation int count = mandateDAO.getBarCodeCount(barCode, mandate.getMandateID(),
			 * "_View"); if (count > 0) { auditDetail.setErrorDetail(ErrorUtil.getErrorDetail( new
			 * ErrorDetails(PennantConstants.KEY_FIELD, "41001",errParm1, valueParm1), usrLanguage)); }
			 */
		}
		List<ErrorDetail> errorDetails = ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage);
		if (errorDetails != null) {
			auditDetail.setErrorDetails(errorDetails);
		}

		if (StringUtils.trimToEmpty(method).equals("doApprove") || !mandate.isWorkflow()) {
			auditDetail.setBefImage(befMandate);
		}

		String status = mandate.getStatus();
		if (!PennantConstants.RECORD_TYPE_NEW.equals(mandate.getRecordType()) && !((MandateStatus.isApproved(status)
				|| (MandateStatus.isRejected(status))
				|| (StringUtils.equals(status, PennantConstants.List_Select) || (MandateStatus.isHold(status)))))) {
			boolean exists = mandateDAO.checkMandateStatus(mandate.getMandateID());

			if (exists) {
				String[] valueParm1 = new String[1];
				valueParm1[0] = String.valueOf(mandate.getMandateID());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("41021", valueParm1)));
			}

		}

		if (PennantConstants.RECORD_TYPE_DEL.equals(mandate.getRecordType()) && MandateStatus.isInprocess(status)) {
			String[] valueParm3 = new String[1];
			valueParm3[0] = String.valueOf(mandate.getMandateID());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("41023", valueParm3)));
		}

		if (!MandateStatus.isInprocess(status) && !MandateStatus.isNew(status) && !mandate.isSecondaryMandate()
				&& !((MandateStatus.isApproved(status) || (MandateStatus.isRejected(status))))
				&& !StringUtils.equals(method, PennantConstants.method_doReject)) {
			boolean exists = mandateDAO.checkMandates(mandate.getOrgReference(), mandate.getMandateID());
			if (exists) {
				String[] valueParm2 = new String[1];
				valueParm2[0] = String.valueOf(mandate.getOrgReference());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("41022", valueParm2)));
			}

		}

		if (mandate.isSwapIsActive()
				&& (StringUtils.equals(PennantConstants.RCD_STATUS_SUBMITTED, mandate.getRecordStatus()))) {
			BigDecimal repayAmount = mandateDAO.getMaxRepayAmount(mandate.getOrgReference(), "_View");
			if (repayAmount != null && mandate.getMaxLimit().compareTo(repayAmount) < 0) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90320", null)));
			}

		}

		if (SysParamUtil.isAllowed(SMTParameterConstants.MANDATE_ALW_PARTNER_BANK)
				&& !(InstrumentType.isDAS(mandate.getMandateType()) || InstrumentType.isSI(mandate.getMandateType()))) {
			if (mandate.getPartnerBankId() == null || mandate.getPartnerBankId() <= 0) {
				String[] valueParm1 = new String[1];
				valueParm1[0] = PennantJavaUtil.getLabel("label_MandateDialog_PartnerBank.value") + "Id";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm1)));
			} else {
				PartnerBank partnerBank = partnerBankDAO.getPartnerBankById(mandate.getPartnerBankId(), "");
				int finTypePartnerBank = 0;
				if (partnerBank != null) {
					finTypePartnerBank = finTypePartnerBankDAO.getAssignedPartnerBankCount(mandate.getPartnerBankId(),
							"");
				}
				if (partnerBank == null || finTypePartnerBank == 0) {
					String[] valueParm1 = new String[1];
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90263", valueParm1)));

				}
			}
		}

		// Business Validation for Default Mandate
		if (mandate.isDefaultMandate()) {
			if (mandateDAO.getMandateCount(mandate.getCustID(), mandate.getMandateID()) >= 1) {
				valueParm[0] = String.valueOf(mandate.getCustID());
				errParm[0] = PennantJavaUtil.getLabel("label_MandateDialog_DefaultMandate.value") + ":" + valueParm[0];
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
			}
		}

		// Mandate Periodicity Validation
		if (StringUtils.isEmpty(mandate.getOrgReference())) {
			return auditDetail;
		}

		FinanceMain fm = financeMainDAO.getFinanceMainByRef(mandate.getOrgReference(), "", false);

		if (StringUtils.isNotBlank(mandate.getPeriodicity())) {
			if (!validatePayFrequency(fm.getRepayFrq().charAt(0), mandate.getPeriodicity().charAt(0))) {
				String[] errParmFrq = new String[2];
				errParmFrq[0] = PennantJavaUtil.getLabel("label_MandateDialog_Periodicity.value");
				errParmFrq[1] = PennantJavaUtil.getLabel("label_FinanceMainDialog_RepayFrq.value");

				auditDetail.setErrorDetail(ErrorUtil
						.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "90220", errParmFrq, null), ""));
			}
		}

		return auditDetail;
	}

	public void processDownload(Mandate mandate) {
		logger.debug(Literal.ENTERING);

		long mandateID = mandate.getMandateID();
		String approvalID = mandate.getApprovalID();

		mandateDAO.updateStatus(mandateID, MandateStatus.AWAITCON, mandate.getMandateRef(), approvalID, "");
		com.pennant.backend.model.mandate.MandateStatus mandateStatus = new com.pennant.backend.model.mandate.MandateStatus();
		mandateStatus.setMandateID(mandateID);
		mandateStatus.setStatus(MandateStatus.AWAITCON);
		mandateStatus.setReason("");
		mandateStatus.setChangeDate(SysParamUtil.getAppDate());

		mandateStatusDAO.save(mandateStatus, "");
		AuditHeader auditHeader = getAuditHeader(mandate, PennantConstants.TRAN_UPD);

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
	}

	public void processFileUpload(Mandate mandate, String status, String reasons, long fileID) {
		logger.debug(Literal.ENTERING);

		mandateDAO.updateStatus(mandate.getMandateID(), status, mandate.getMandateRef(), mandate.getApprovalID(), "");

		com.pennant.backend.model.mandate.MandateStatus mandateStatus = new com.pennant.backend.model.mandate.MandateStatus();
		mandateStatus.setMandateID(mandate.getMandateID());
		mandateStatus.setStatus(status);
		mandateStatus.setReason(reasons);
		mandateStatus.setFileID(fileID);
		mandateStatus.setChangeDate(SysParamUtil.getAppDate());

		mandateStatusDAO.save(mandateStatus, "");

		AuditHeader auditHeader = getAuditHeader(mandate, PennantConstants.TRAN_UPD);

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
	}

	public long processStatusSave(MandateStatusUpdate mandateStatusUpdate) {
		return mandateStatusUpdateDAO.save(mandateStatusUpdate, "");
	}

	public void processStatusUpdate(MandateStatusUpdate mandateStatusUpdate) {
		mandateStatusUpdateDAO.update(mandateStatusUpdate, "");
	}

	public List<FinanceEnquiry> getMandateFinanceDetailById(long id) {
		return mandateDAO.getMandateFinanceDetailById(id);
	}

	public int getFileCount(String fileName) {
		return mandateStatusUpdateDAO.getFileCount(fileName);
	}

	@Override
	public List<Mandate> getApprovedMandatesByCustomerId(long custID) {
		return mandateDAO.getApprovedMandatesByCustomerId(custID, "_AView");
	}

	private AuditHeader getAuditHeader(Mandate aMandate, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aMandate.getBefImage(), aMandate);
		return new AuditHeader(String.valueOf(aMandate.getMandateID()), null, null, null, auditDetail,
				aMandate.getUserDetails(), null);
	}

	@Override
	public byte[] getDocumentManImage(long mandateRef) {
		return getDocumentImage(mandateRef);
	}

	private void getDocument(Mandate mandate) {
		DocumentDetails dd = new DocumentDetails();
		dd.setFinReference(mandate.getOrgReference());
		dd.setDocName(mandate.getDocumentName());
		dd.setCustId(mandate.getCustID());
		if (mandate.getDocumentRef() != null && mandate.getDocumentRef() > 0 && !mandate.isNewRecord()) {
			byte[] olddocumentManager = getDocumentImage(mandate.getDocumentRef());
			if (olddocumentManager != null) {
				byte[] arr1 = olddocumentManager;
				byte[] arr2 = mandate.getDocImage();
				if (!Arrays.equals(arr1, arr2)) {

					dd.setDocImage(mandate.getDocImage());
					saveDocument(DMSModule.FINANCE, DMSModule.MANDATE, dd);
					mandate.setDocumentRef(dd.getDocRefId());
				}
			}
		} else {
			dd.setDocImage(mandate.getDocImage());
			dd.setUserDetails(mandate.getUserDetails());
			saveDocument(DMSModule.FINANCE, DMSModule.MANDATE, dd);
			mandate.setDocumentRef(dd.getDocRefId());
		}
	}

	@Override
	public MandateCheckDigit getLookUpValueByCheckDigit(int rem) {
		return mandateCheckDigitDAO.getMandateCheckDigit(rem, "_View");
	}

	private int checkSum(String barCode) {
		int value = 0;
		for (int i = 0; i < barCode.length() - 1; i++) {
			value += Integer.parseInt(barCode.charAt(i) + "");
		}
		PFSParameter parameter = SysParamUtil.getSystemParameterObject("BARCODE_DIVISOR");
		int divisor = Integer.parseInt(parameter.getSysParmValue().trim());
		int remainder = value % divisor;

		return remainder;
	}

	private Boolean validatePayFrequency(char repayFrq, char mandateFrq) {
		boolean valFrq = true;
		if (repayFrq == mandateFrq) {
			valFrq = true;
		} else {
			switch (repayFrq) {
			case 'D':
				if (mandateFrq != 'D') {
					valFrq = false;
				}
				break;
			case 'W':
				if (mandateFrq != 'D') {
					valFrq = false;
				}
				break;
			case 'X':
				if (mandateFrq != 'D' || mandateFrq != 'W') {
					valFrq = false;
				}
				break;
			case 'F':
				if (mandateFrq != 'D' || mandateFrq != 'W' || mandateFrq != 'X') {
					valFrq = false;
				}
				break;
			case 'M':
				if (mandateFrq == 'B' || mandateFrq == 'Q' || mandateFrq == 'H' || mandateFrq == 'Y') {
					valFrq = false;
				}
				break;
			case 'B':
				if (mandateFrq == 'Q' || mandateFrq == 'H' || mandateFrq == 'Y') {
					valFrq = false;
				}
				break;

			case 'Q':
				if (mandateFrq == 'H' || mandateFrq == 'Y') {
					valFrq = false;
				}
				break;
			case 'H':
				if (mandateFrq == 'Y') {
					valFrq = false;
				}
				break;
			}
		}
		return valFrq;
	}

	@Override
	public List<FinanceMain> getLoans(long custId, String finRepayMethod) {
		return mandateDAO.getLoans(custId, finRepayMethod);
	}

	/**
	 * @return the mandate
	 */
	@Override
	public Mandate getMandate() {
		return mandateDAO.getMandate();
	}

	@Override
	public int getSecondaryMandateCount(long mandateID) {
		return mandateDAO.getSecondaryMandateCount(mandateID);
	}

	@Override
	public int validateEmandateSource(String eMandateSource) {
		return mandateDAO.validateEmandateSource(eMandateSource);
	}

	@Override
	public void getDocumentImage(Mandate mandate) {
		if (mandate.getDocumentRef() != null) {
			mandate.setDocImage(getDocumentImage(mandate.getDocumentRef()));
		}
	}

	@Override
	public Mandate getMandateStatusById(String finReference, long mandateID) {
		return mandateDAO.getMandateStatusById(finReference, mandateID);
	}

	@Override
	public int updateMandateStatus(Mandate mandate) {
		return mandateDAO.updateMandateStatus(mandate);
	}

	@Override
	public List<PresentmentDetail> getPresentmentDetailsList(String finreference, long mandateID, String status) {
		return mandateDAO.getPresentmentDetailsList(finreference, mandateID, status);
	}

	@Override
	public int getMandateByMandateRef(String mandateRef) {
		return mandateDAO.getMandateByMandateRef(mandateRef);
	}

	@Autowired(required = false)
	@Qualifier(value = "mandateProcesses")
	public void setMandateProces(MandateProcesses mandateProcesses) {
		this.mandateProcesses = mandateProcesses;
	}

	@Autowired
	public void setDefaultMandateProcess(MandateProcesses defaultMandateProcess) {
		this.defaultMandateProcess = defaultMandateProcess;
	}

	@Autowired
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	@Autowired
	public void setMandateDAO(MandateDAO mandateDAO) {
		this.mandateDAO = mandateDAO;
	}

	@Autowired
	public void setMandateStatusDAO(MandateStatusDAO mandateStatusDAO) {
		this.mandateStatusDAO = mandateStatusDAO;
	}

	@Autowired
	public void setMandateStatusUpdateDAO(MandateStatusUpdateDAO mandateStatusUpdateDAO) {
		this.mandateStatusUpdateDAO = mandateStatusUpdateDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setMandateCheckDigitDAO(MandateCheckDigitDAO mandateCheckDigitDAO) {
		this.mandateCheckDigitDAO = mandateCheckDigitDAO;
	}

	@Autowired
	public void setBankBranchDAO(BankBranchDAO bankBranchDAO) {
		this.bankBranchDAO = bankBranchDAO;
	}

	@Autowired
	public void setPartnerBankDAO(PartnerBankDAO partnerBankDAO) {
		this.partnerBankDAO = partnerBankDAO;
	}

	@Autowired
	public void setMandateProcesses(MandateProcesses mandateProcesses) {
		this.mandateProcesses = mandateProcesses;
	}

	@Autowired
	public void setFinTypePartnerBankDAO(FinTypePartnerBankDAO finTypePartnerBankDAO) {
		this.finTypePartnerBankDAO = finTypePartnerBankDAO;
	}

	@Autowired
	public void setdMSService(DMSService dMSService) {
		this.dMSService = dMSService;
	}

	private MandateProcesses getMandateProcess() {
		return mandateProcesses == null ? defaultMandateProcess : mandateProcesses;
	}

}