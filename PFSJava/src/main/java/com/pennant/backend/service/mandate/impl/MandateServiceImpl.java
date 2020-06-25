/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  MandateServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-10-2016    														*
 *                                                                  						*
 * Modified Date    :  18-10-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-10-2016       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
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
import org.apache.log4j.Logger;
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
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.mandate.MandateStatus;
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
import com.pennanttech.model.dms.DMSModule;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.MandateProcesses;

/**
 * Service implementation for methods that depends on <b>Mandate</b>.<br>
 * 
 */
public class MandateServiceImpl extends GenericService<Mandate> implements MandateService {
	private static final Logger logger = Logger.getLogger(MandateServiceImpl.class);

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

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table Mandates/Mandates_Temp by using
	 * MandateDAO's save method b) Update the Record in the table. based on the module workFlow Configuration. by using
	 * MandateDAO's update method 3) Audit the record in to AuditHeader and AdtMandates by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param boolean
	 *            onlineRequest
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
		Mandate mandate = (Mandate) auditHeader.getAuditDetail().getModelData();

		if (mandate.isWorkflow()) {
			tableType = "_Temp";
		}

		if (mandate.isNew()) {
			getDocument(mandate);
			mandate.setId(getMandateDAO().save(mandate, tableType));
			auditHeader.getAuditDetail().setModelData(mandate);
			auditHeader.setAuditReference(String.valueOf(mandate.getMandateID()));
		} else {
			getDocument(mandate);
			getMandateDAO().update(mandate, tableType);
			if (StringUtils.trimToEmpty(mandate.getModule()).equals(MandateConstants.MODULE_REGISTRATION)) {
				MandateStatus mandateStatus = new MandateStatus();
				mandateStatus.setMandateID(mandate.getMandateID());
				mandateStatus.setStatus(mandate.getStatus());
				mandateStatus.setReason(mandate.getReason());
				mandateStatus.setChangeDate(mandate.getInputDate());
				getMandateStatusDAO().save(mandateStatus, "");
			}
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * Mandates by using MandateDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtMandates by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
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
		getMandateDAO().delete(mandate, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getMandateById fetch the details by using MandateDAO's getMandateById method.
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Mandate
	 */

	@Override
	public Mandate getMandateById(long id) {
		return getMandateDAO().getMandateById(id, "_View");
	}

	@Override
	public Mandate getMandateStatusUpdateById(long id, String status) {
		return getMandateDAO().getMandateByStatus(id, status, "_View");
	}

	/**
	 * getApprovedMandateById fetch the details by using MandateDAO's getMandateById method . with parameter id and type
	 * as blank. it fetches the approved records from the Mandates.
	 * 
	 * @param id
	 *            (int)
	 * @return Mandate
	 */

	public Mandate getApprovedMandateById(long id) {
		return getMandateDAO().getMandateById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getMandateDAO().delete with parameters
	 * mandate,"" b) NEW Add new record in to main table by using getMandateDAO().save with parameters mandate,"" c)
	 * EDIT Update record in the main table by using getMandateDAO().update with parameters mandate,"" 3) Delete the
	 * record from the workFlow table by using getMandateDAO().delete with parameters mandate,"_Temp" 4) Audit the
	 * record in to AuditHeader and AdtMandates by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the
	 * record in to AuditHeader and AdtMandates by using auditHeaderDAO.addAudit(auditHeader) based on the transaction
	 * Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
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
			getMandateDAO().updateActive(mandate.getMandateID(), MandateConstants.STATUS_CANCEL, false);

			MandateStatus mandateStatus = new MandateStatus();
			mandateStatus.setMandateID(mandate.getMandateID());
			mandateStatus.setStatus(MandateConstants.STATUS_CANCEL);
			mandateStatus.setReason(mandate.getReason());
			mandateStatus.setChangeDate(mandate.getInputDate());
			getMandateStatusDAO().save(mandateStatus, "");

		} else {
			mandate.setRoleCode("");
			mandate.setNextRoleCode("");
			mandate.setTaskId("");
			mandate.setNextTaskId("");
			mandate.setWorkflowId(0);
			boolean isApproved= false;
			if (StringUtils.trimToEmpty(mandate.getStatus()).equals(MandateConstants.STATUS_APPROVED)) {
				isApproved = true;
			}

			if (StringUtils.trimToEmpty(mandate.getStatus()).equals(MandateConstants.STATUS_RELEASE)) {
				mandate.setStatus(MandateConstants.STATUS_APPROVED);
			} else if (!StringUtils.trimToEmpty(mandate.getStatus()).equals(MandateConstants.STATUS_HOLD)) {
				mandate.setStatus(MandateConstants.STATUS_NEW);
			} 
			
			if(isApproved) {
				mandate.setStatus(MandateConstants.STATUS_APPROVED);
			}
			
			if (StringUtils.equals(mandate.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
				if (mandate.isApproveMandate()) {
					mandate.setStatus(MandateConstants.STATUS_APPROVED);
				} else {
					mandate.setStatus(MandateConstants.STATUS_NEW);
				}
			}
			if (StringUtils.equals(MandateConstants.TYPE_EMANDATE, mandate.getMandateType())) {
				if (StringUtils.isNotBlank(mandate.getMandateRef())) {
					mandate.setStatus(MandateConstants.STATUS_APPROVED);
				} else {
					mandate.setStatus(MandateConstants.STATUS_AWAITCON);
				}
			}

			getDocument(mandate);

			if (mandate.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				mandate.setRecordType("");
				mandate.setMandateID(getMandateDAO().save(mandate, ""));
			} else {
				tranType = PennantConstants.TRAN_UPD;
				mandate.setRecordType("");
				getMandateDAO().update(mandate, "");
				mandate.setModificationDate(new Timestamp(System.currentTimeMillis()));
			}

			MandateStatus mandateStatus = new MandateStatus();
			mandateStatus.setMandateID(mandate.getMandateID());
			mandateStatus.setStatus(mandate.getStatus());
			mandateStatus.setReason(mandate.getReason());
			mandateStatus.setChangeDate(mandate.getInputDate());

			getMandateStatusDAO().save(mandateStatus, "");

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
					mandate.setStatus(MandateConstants.STATUS_INPROCESS);
					getMandateDAO().updateStatusAfterRegistration(mandate.getMandateID(),
							MandateConstants.STATUS_INPROCESS);
					mandateStatus.setMandateID(mandate.getMandateID());
					mandateStatus.setStatus(mandate.getStatus());
					mandateStatus.setReason(mandate.getReason());
					mandateStatus.setChangeDate(mandate.getInputDate());
					getMandateStatusDAO().save(mandateStatus, "");
				}

			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}

		}

		if ((!StringUtils.equals(mandate.getSourceId(), PennantConstants.FINSOURCE_ID_API)
				&& !mandate.isSecondaryMandate())) {
			getMandateDAO().delete(mandate, "_Temp");
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAuditHeaderDAO().addAudit(auditHeader);
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(mandate);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getMandateDAO().delete with parameters mandate,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtMandates by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		Mandate mandate = (Mandate) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getMandateDAO().delete(mandate, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) validate the audit detail 2) if any error/Warnings then
	 * assign the to auditHeader 3) identify the nextprocess
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param boolean
	 *            onlineRequest
	 * @return auditHeader
	 */

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
		List<ErrorDetail> details = null;
		if (StringUtils.isNotBlank(mandate.getBarCodeNumber())) {
			details = new ArrayList<>();
			Pattern pattern = Pattern
					.compile(PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_BARCODE_NUMBER));
			Matcher matcher = pattern.matcher(mandate.getBarCodeNumber());

			if (matcher.matches() == false) {
				String[] valueParm = new String[1];
				valueParm[0] = mandate.getBarCodeNumber();
				details.add(ErrorUtil.getErrorDetail(new ErrorDetail("barCodeNumber", "90404", valueParm, valueParm)));
			}
		}
		return details;

	}

	/**
	 * Validation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details from the
	 * tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5) for any
	 * mismatch conditions Fetch the error details from getMandateDAO().getErrorDetail with Error ID and language as
	 * parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param boolean
	 *            onlineRequest
	 * @return auditHeader
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		Mandate mandate = (Mandate) auditDetail.getModelData();

		Mandate tempMandate = null;
		if (mandate.isWorkflow()) {
			tempMandate = getMandateDAO().getMandateById(mandate.getId(), "_Temp");
		}
		Mandate befMandate = getMandateDAO().getMandateById(mandate.getId(), "");

		Mandate oldMandate = mandate.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(mandate.getId());
		errParm[0] = PennantJavaUtil.getLabel("label_MandateID") + ":" + valueParm[0];

		if (mandate.isNew()) { // for New record or new record into work flow

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
			 * //BarCode Unique Validation int count = getMandateDAO().getBarCodeCount(barCode, mandate.getMandateID(),
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

		if (!StringUtils.equals(mandate.getRecordType(), PennantConstants.RECORD_TYPE_NEW) && !((StringUtils
				.equals(mandate.getStatus(), MandateConstants.STATUS_APPROVED)
				|| (StringUtils.equals(mandate.getStatus(), MandateConstants.STATUS_REJECTED)
						|| (StringUtils.equals(mandate.getStatus(), PennantConstants.List_Select)
								|| (StringUtils.equals(mandate.getStatus(), MandateConstants.STATUS_HOLD))))))) {
			boolean exists = getMandateDAO().checkMandateStatus(mandate.getMandateID());
			if (exists) {
				String[] valueParm1 = new String[1];
				valueParm1[0] = String.valueOf(mandate.getMandateID());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("41021", valueParm1)));
			}

		}

		if (StringUtils.equals(mandate.getRecordType(), PennantConstants.RECORD_TYPE_DEL)
				&& StringUtils.equals(mandate.getStatus(), MandateConstants.STATUS_INPROCESS)) {
			String[] valueParm3 = new String[1];
			valueParm3[0] = String.valueOf(mandate.getMandateID());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("41023", valueParm3)));
		}

		if (!StringUtils.equals(mandate.getStatus(), MandateConstants.STATUS_INPROCESS)
				&& !StringUtils.equals(mandate.getStatus(), MandateConstants.STATUS_NEW)
				&& !mandate.isSecondaryMandate()
				&& !((StringUtils.equals(mandate.getStatus(), MandateConstants.STATUS_APPROVED)
						|| (StringUtils.equals(mandate.getStatus(), MandateConstants.STATUS_REJECTED))))
				&& !StringUtils.equals(method, PennantConstants.method_doReject)) {
			boolean exists = getMandateDAO().checkMandates(mandate.getOrgReference(), mandate.getMandateID());
			if (exists) {
				String[] valueParm2 = new String[1];
				valueParm2[0] = String.valueOf(mandate.getOrgReference());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("41022", valueParm2)));
			}

		}

		if (mandate.isSwapIsActive()
				&& (StringUtils.equals(PennantConstants.RCD_STATUS_SUBMITTED, mandate.getRecordStatus()))) {
			BigDecimal repayAmount = getMandateDAO().getMaxRepayAmount(mandate.getOrgReference(), "_View");
			if (mandate.getMaxLimit().compareTo(repayAmount) < 0) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90320", null)));
			}

		}

		if (SysParamUtil.isAllowed(SMTParameterConstants.MANDATE_ALW_PARTNER_BANK)) {
			if (mandate.getPartnerBankId() <= 0) {
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
			if (getMandateDAO().getMandateCount(mandate.getCustID(), mandate.getMandateID()) >= 1) {
				valueParm[0] = String.valueOf(mandate.getCustID());
				errParm[0] = PennantJavaUtil.getLabel("label_MandateDialog_DefaultMandate.value") + ":" + valueParm[0];
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
			}
		}
		return auditDetail;
	}

	public void processDownload(Mandate mandate) {
		logger.debug("Entering");

		getMandateDAO().updateStatus(mandate.getMandateID(), MandateConstants.STATUS_AWAITCON, mandate.getMandateRef(),
				mandate.getApprovalID(), "");
		MandateStatus mandateStatus = new MandateStatus();
		mandateStatus.setMandateID(mandate.getMandateID());
		mandateStatus.setStatus(MandateConstants.STATUS_AWAITCON);
		mandateStatus.setReason("");
		mandateStatus.setChangeDate(SysParamUtil.getAppDate());
		getMandateStatusDAO().save(mandateStatus, "");
		AuditHeader auditHeader = getAuditHeader(mandate, PennantConstants.TRAN_UPD);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
	}

	public void processFileUpload(Mandate mandate, String status, String reasons, long fileID) {
		logger.debug("Entering");

		getMandateDAO().updateStatus(mandate.getMandateID(), status, mandate.getMandateRef(), mandate.getApprovalID(),
				"");
		MandateStatus mandateStatus = new MandateStatus();
		mandateStatus.setMandateID(mandate.getMandateID());
		mandateStatus.setStatus(status);
		mandateStatus.setReason(reasons);
		mandateStatus.setFileID(fileID);
		mandateStatus.setChangeDate(SysParamUtil.getAppDate());
		getMandateStatusDAO().save(mandateStatus, "");
		AuditHeader auditHeader = getAuditHeader(mandate, PennantConstants.TRAN_UPD);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
	}

	public long processStatusSave(MandateStatusUpdate mandateStatusUpdate) {
		return getMandateStatusUpdateDAO().save(mandateStatusUpdate, "");
	}

	public void processStatusUpdate(MandateStatusUpdate mandateStatusUpdate) {
		getMandateStatusUpdateDAO().update(mandateStatusUpdate, "");
	}

	public List<FinanceEnquiry> getMandateFinanceDetailById(long id) {
		return getMandateDAO().getMandateFinanceDetailById(id);
	}

	public int getFileCount(String fileName) {
		return getMandateStatusUpdateDAO().getFileCount(fileName);
	}

	@Override
	public List<Mandate> getApprovedMandatesByCustomerId(long custID) {
		return getMandateDAO().getApprovedMandatesByCustomerId(custID, "_AView");
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */
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
		dd.setFinReference(mandate.getFinReference());
		dd.setDocName(mandate.getDocumentName());
		dd.setCustId(mandate.getCustID());
		if (mandate.getDocumentRef() != 0 && !mandate.isNewRecord()) {
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

		return getMandateCheckDigitDAO().getMandateCheckDigit(rem, "_View");
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

	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	/**
	 * @param auditHeaderDAO
	 *            the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	/**
	 * @return the mandateDAO
	 */
	public MandateDAO getMandateDAO() {
		return mandateDAO;
	}

	/**
	 * @param mandateDAO
	 *            the mandateDAO to set
	 */
	public void setMandateDAO(MandateDAO mandateDAO) {
		this.mandateDAO = mandateDAO;
	}

	/**
	 * @return the mandate
	 */
	@Override
	public Mandate getMandate() {
		return getMandateDAO().getMandate();
	}

	/**
	 * @return the mandateStatusDAO
	 */
	public MandateStatusDAO getMandateStatusDAO() {
		return mandateStatusDAO;
	}

	/**
	 * @param mandateStatusDAO
	 *            the mandateStatusDAO to set
	 */
	public void setMandateStatusDAO(MandateStatusDAO mandateStatusDAO) {
		this.mandateStatusDAO = mandateStatusDAO;
	}

	/**
	 * mandateCheckDigitDAO
	 * 
	 * @return
	 */
	public MandateCheckDigitDAO getMandateCheckDigitDAO() {
		return mandateCheckDigitDAO;
	}

	/**
	 * 
	 * @param mandateCheckDigitDAO
	 */
	public void setMandateCheckDigitDAO(MandateCheckDigitDAO mandateCheckDigitDAO) {
		this.mandateCheckDigitDAO = mandateCheckDigitDAO;
	}

	public void setPartnerBankDAO(PartnerBankDAO partnerBankDAO) {
		this.partnerBankDAO = partnerBankDAO;
	}

	@Override
	public int getSecondaryMandateCount(long mandateID) {
		return mandateDAO.getSecondaryMandateCount(mandateID);
	}

	public void setBankBranchDAO(BankBranchDAO bankBranchDAO) {
		this.bankBranchDAO = bankBranchDAO;
	}

	private MandateProcesses getMandateProcess() {
		return mandateProcesses == null ? defaultMandateProcess : mandateProcesses;
	}

	public MandateStatusUpdateDAO getMandateStatusUpdateDAO() {
		return mandateStatusUpdateDAO;
	}

	public void setMandateStatusUpdateDAO(MandateStatusUpdateDAO mandateStatusUpdateDAO) {
		this.mandateStatusUpdateDAO = mandateStatusUpdateDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
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

	@Override
	public int validateEmandateSource(String eMandateSource) {
		return mandateDAO.validateEmandateSource(eMandateSource);
	}

	@Override
	public void getDocumentImage(Mandate mandate) {
		mandate.setDocImage(getDocumentImage(mandate.getDocumentRef()));
	}

	@Override
	public Mandate getMandateStatusById(String finReference, long mandateID) {
		return mandateDAO.getMandateStatusById(finReference, mandateID);
	}

	@Override
	public int updateMandateStatus(Mandate mandate) {
		return mandateDAO.updateMandateStatus(mandate);
	}

	public FinTypePartnerBankDAO getFinTypePartnerBankDAO() {
		return finTypePartnerBankDAO;
	}

	public void setFinTypePartnerBankDAO(FinTypePartnerBankDAO finTypePartnerBankDAO) {
		this.finTypePartnerBankDAO = finTypePartnerBankDAO;
	}

}