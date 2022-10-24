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
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.NumberToEnglishWords;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.EntityDAO;
import com.pennant.backend.dao.applicationmaster.MandateCheckDigitDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.bmtmasters.BankBranchDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.dao.mandate.MandateStatusDAO;
import com.pennant.backend.dao.mandate.MandateStatusUpdateDAO;
import com.pennant.backend.dao.partnerbank.PartnerBankDAO;
import com.pennant.backend.dao.rmtmasters.FinTypePartnerBankDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.applicationmaster.MandateCheckDigit;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.mandate.MandateStatusUpdate;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.extension.MandateExtension;
import com.pennant.pff.mandate.InstrumentType;
import com.pennant.pff.mandate.MandateStatus;
import com.pennant.pff.mandate.MandateUtil;
import com.pennanttech.model.dms.DMSModule;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
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
	private EntityDAO entityDAO;
	private FinTypePartnerBankDAO finTypePartnerBankDAO;
	private BankBranchService bankBranchService;
	private BankDetailService bankDetailService;

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		String tableType = "";
		Mandate mandate = (Mandate) auditHeader.getAuditDetail().getModelData();

		if (mandate.isWorkflow()) {
			tableType = "_Temp";
		}

		if (mandate.isNewRecord()) {
			getDocument(mandate);
			mandate.setMandateID(mandateDAO.save(mandate, tableType));
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

		Mandate mandate = (Mandate) auditHeader.getAuditDetail().getModelData();
		mandateDAO.delete(mandate, "");

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public Mandate getMandateById(long id) {
		return mandateDAO.getMandateById(id, "_View");
	}

	@Override
	public Mandate getMandateStatusUpdateById(long id, String status) {
		return mandateDAO.getMandateByStatus(id, status, "_View");
	}

	public Mandate getApprovedMandateById(long id) {
		return mandateDAO.getMandateById(id, "_AView");
	}

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
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

			String orgStatus = mandate.getStatus();

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

			String mandateType = mandate.getMandateType();
			InstrumentType instrumentType = InstrumentType.valueOf(mandateType);
			switch (instrumentType) {
			case EMANDATE:
				if (StringUtils.isNotBlank(mandate.getMandateRef())) {
					mandate.setStatus(MandateStatus.APPROVED);
				} else {
					mandate.setStatus(MandateStatus.AWAITCON);
				}

				break;
			case DAS:
			case SI:
				mandate.setStatus(MandateStatus.APPROVED);
				break;

			default:
				break;
			}

			if (StringUtils.isNotBlank(mandate.getMandateRef())) {
				mandate.setStatus(MandateStatus.APPROVED);
			}

			if (MandateStatus.isHold(orgStatus)) {
				mandate.setStatus(MandateStatus.HOLD);
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
		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		Mandate mandate = (Mandate) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		mandateDAO.delete(mandate, "_Temp");

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

	public List<ErrorDetail> doValidations(Mandate mandate) {
		List<ErrorDetail> details = new ArrayList<>();

		if (StringUtils.isNotBlank(mandate.getBarCodeNumber())) {
			Pattern pattern = Pattern
					.compile(PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_BARCODE_NUMBER));
			Matcher matcher = pattern.matcher(mandate.getBarCodeNumber());

			if (matcher.matches()) {
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
				details.add(getError("90502", "AccHolderName"));
			}
		}

		if (!StringUtils.isBlank(mandate.getJointAccHolderName())) {
			Pattern pattern = Pattern.compile(
					PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_ACCOUNT_HOLDER_NAME));

			Matcher matcher = pattern.matcher(mandate.getJointAccHolderName());

			if (!matcher.matches()) {
				details.add(getError("90237", "JointAccHolderName"));
			}
		}

		return details;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		Mandate mandate = (Mandate) auditDetail.getModelData();

		Mandate tempMandate = null;
		if (mandate.isWorkflow()) {
			tempMandate = mandateDAO.getMandateById(mandate.getMandateID(), "_Temp");
		}
		Mandate befMandate = mandateDAO.getMandateById(mandate.getMandateID(), "");

		Mandate oldMandate = mandate.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(mandate.getMandateID());
		errParm[0] = PennantJavaUtil.getLabel("label_MandateID") + ":" + valueParm[0];

		if (mandate.isNewRecord()) {

			if (!mandate.isWorkflow()) {
				if (befMandate != null) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else {
				if (mandate.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					if (befMandate != null || tempMandate != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else {
					if (befMandate == null || tempMandate != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			if (!mandate.isWorkflow()) {

				if (befMandate == null) {
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

				if (tempMandate == null) {
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
			BigDecimal repayAmount = mandateDAO.getMaxRepayAmount(mandate.getMandateID());
			if (repayAmount != null && mandate.getMaxLimit().compareTo(repayAmount) < 0) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90320", null)));
			}

		}

		if (MandateExtension.PARTNER_BANK_REQ
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

		if (mandate.isDefaultMandate()) {
			if (mandateDAO.getMandateCount(mandate.getCustID(), mandate.getMandateID()) >= 1) {
				valueParm[0] = String.valueOf(mandate.getCustID());
				errParm[0] = PennantJavaUtil.getLabel("label_MandateDialog_DefaultMandate.value") + ":" + valueParm[0];
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
			}
		}

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
	public List<Mandate> getLoans(long custId, String finRepayMethod) {
		List<Mandate> loans = mandateDAO.getLoans(custId, finRepayMethod);

		return loans.stream().filter(fm -> fm.getAlwdRpyMethods().contains(finRepayMethod))
				.collect(Collectors.toList());
	}

	@Override
	public Mandate getEmployerDetails(long custID) {
		return mandateDAO.getEmployerDetails(custID);
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

	@Override
	public ErrorDetail validate(FinanceDetail fd, String vldGroup) {
		Mandate mandate = fd.getMandate();

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		String repaymentMethod = fm.getFinRepayMethod();

		if (mandate == null && (!(InstrumentType.isManual(repaymentMethod) || InstrumentType.isPDC(repaymentMethod))
				&& fd.isStp())) {
			return ErrorUtil.getError("90502", "Mandate");
		}

		if (mandate == null) {
			return null;
		}
		if (InstrumentType.isManual(repaymentMethod)) {
			return ErrorUtil.getError("90329", "Mandate", "finRepayMethod is " + repaymentMethod);
		}

		ErrorDetail error = null;
		if (mandate.isUseExisting()) {
			error = validateExisting(fd, vldGroup);
		} else {
			error = validateNew(fd);
		}

		if (error != null) {
			return error;
		}

		if (!InstrumentType.isEMandate(mandate.getMandateType())) {
			return null;
		}

		if (StringUtils.isBlank(mandate.geteMandateReferenceNo())) {
			return ErrorUtil.getError("90502", "eMandateReferenceNo");
		}

		if (StringUtils.isBlank(mandate.geteMandateSource())) {
			return ErrorUtil.getError("90502", "eMandateSource");
		}

		if (mandateDAO.validateEmandateSource(mandate.geteMandateSource()) == 0) {
			return ErrorUtil.getError("90501", "eMandateSource" + mandate.geteMandateSource());
		}

		return null;
	}

	private ErrorDetail validateNew(FinanceDetail fd) {
		Mandate mandate = fd.getMandate();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		String repaymentMethod = fm.getFinRepayMethod();

		mandate.setMandateID(Long.MIN_VALUE);
		mandate.setCustID(fm.getCustID());

		ErrorDetail errordetail = basicValidation(mandate, repaymentMethod);

		if (errordetail != null) {
			return errordetail;
		}

		String mandateType = mandate.getMandateType();

		switch (InstrumentType.valueOf(mandateType)) {
		case ECS:
		case DD:
		case NACH:
		case EMANDATE:
		case SI:
			errordetail = validateAccountDetail(mandate);

			if (errordetail != null && !InstrumentType.isSI(mandateType)) {
				errordetail = otherDetailValidation(mandate);
			}

			break;
		case DAS:
			if (mandate.getEmployerID() == null) {
				return ErrorUtil.getError("90502", "employerID");
			}

			Mandate employerDetails = getEmployerDetails(mandate.getCustID());

			if (employerDetails == null || employerDetails.getEmployerID().compareTo(mandate.getEmployerID()) != 0) {
				ErrorUtil.getError("MNDT01", String.valueOf(mandate.getEmployerID()));
			} else if (!employerDetails.isAllowDAS()) {
				ErrorUtil.getError("MNDT02", String.valueOf(mandate.getEmployerID()));
			}

			break;
		default:
			break;
		}

		if (errordetail != null) {
			return errordetail;
		}

		return errordetail;
	}

	private ErrorDetail basicValidation(Mandate mandate, String repaymentMethod) {
		if (!StringUtils.equalsIgnoreCase(mandate.getMandateType(), repaymentMethod)) {
			return ErrorUtil.getError("90311", repaymentMethod, mandate.getMandateType());
		}

		if (StringUtils.isBlank(mandate.getMandateType())) {
			return ErrorUtil.getError("90502", "mandateType");
		}
		return null;
	}

	private ErrorDetail mandateDetailValidation(Mandate mandate) {
		if (mandate.getStartDate() == null) {
			return ErrorUtil.getError("90502", "startDate");
		}

		if (!mandate.isOpenMandate()) {
			if (mandate.getExpiryDate() == null) {
				return ErrorUtil.getError("90502", "expiryDate");
			}
		} else {
			if (mandate.getExpiryDate() != null) {
				return ErrorUtil.getError("90329", "expiryDate", "open mandate");
			}
		}

		if (ImplementationConstants.ALLOW_BARCODE && StringUtils.isBlank(mandate.getBarCodeNumber())) {
			return ErrorUtil.getError("90502", "BarCodeNumber");
		}

		if (StringUtils.isNotBlank(mandate.getBarCodeNumber())) {
			Pattern pattern = Pattern
					.compile(PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_BARCODE_NUMBER));
			Matcher matcher = pattern.matcher(mandate.getBarCodeNumber());

			if (matcher.matches() == false) {
				return ErrorUtil.getError("90404", "barCodeNumber", mandate.getBarCodeNumber());
			}
		}

		if (mandate.getMaxLimit() == null) {
			return ErrorUtil.getError("90242", "maxLimit");
		}

		if (mandate.getMaxLimit().compareTo(BigDecimal.ZERO) <= 0) {
			return ErrorUtil.getError("91121", "maxLimit", "0");
		}

		Date dftAppEndDate = SysParamUtil.getValueAsDate(SMTParameterConstants.APP_DFT_END_DATE);
		String strEndDate = DateUtil.formatToLongDate(dftAppEndDate);
		if (mandate.getExpiryDate() != null) {
			if (mandate.getExpiryDate().compareTo(mandate.getStartDate()) <= 0
					|| mandate.getExpiryDate().after(dftAppEndDate)) {
				String strStartDate = DateUtil.formatToLongDate(DateUtil.addDays(mandate.getStartDate(), 1));

				return ErrorUtil.getError("90318", "Mandate ExpiryDate", strStartDate, strEndDate);
			}
		}

		if (mandate.getStartDate() != null) {
			Date mandbackDate = DateUtil.addDays(SysParamUtil.getAppDate(),
					-SysParamUtil.getValueAsInt(SMTParameterConstants.MANDATE_STARTDATE));
			if (mandate.getStartDate().before(mandbackDate) || mandate.getStartDate().after(dftAppEndDate)) {
				String strStartDate = DateUtil.formatToLongDate(mandbackDate);
				return ErrorUtil.getError("90318", "mandate start date", strStartDate, strEndDate);
			}
		}

		if (StringUtils.isNotBlank(mandate.getMandateType())) {
			List<ValueLabel> mandateType = MandateUtil.getInstrumentTypes();
			boolean mandateTypeSts = false;
			for (ValueLabel value : mandateType) {
				if (StringUtils.equals(value.getValue(), mandate.getMandateType())) {
					mandateTypeSts = true;
					break;
				}
			}
			if (!mandateTypeSts) {
				return ErrorUtil.getError("90307", mandate.getMandateType());
			}
		}

		if (StringUtils.isNotBlank(mandate.getPeriodicity())) {
			ErrorDetail error = FrequencyUtil.validateFrequency(mandate.getPeriodicity());
			if (error != null && StringUtils.isNotBlank(error.getCode())) {
				return ErrorUtil.getError("90207", mandate.getPeriodicity());
			}
		} else {
			mandate.setPeriodicity(MandateConstants.MANDATE_DEFAULT_FRQ);
		}

		if (StringUtils.isNotBlank(mandate.getStatus())) {
			List<ValueLabel> status = MandateUtil.getMandateStatus();
			boolean sts = false;
			for (ValueLabel value : status) {
				if (StringUtils.equals(value.getValue(), mandate.getStatus())) {
					sts = true;
					break;
				}
			}
			if (!sts) {
				return ErrorUtil.getError("90309", mandate.getStatus());
			}
		}

		if (mandate.getDocImage() == null && StringUtils.isBlank(mandate.getExternalRef())) {
			return ErrorUtil.getError("90123", "docContent", "docRefId");
		} else if (StringUtils.isBlank(mandate.getDocumentName())) {
			return ErrorUtil.getError("90502", "Document Name");
		}

		if (StringUtils.isNotBlank(mandate.getDocumentName())) {
			String docName = mandate.getDocumentName().toLowerCase();
			if (!docName.contains(".")) {
				return ErrorUtil.getError("90291", mandate.getDocumentName());
			}

			if (StringUtils.isEmpty(docName.substring(0, docName.lastIndexOf(".")))) {
				return ErrorUtil.getError("90502", "Document Name");
			}

			if (!docName.endsWith(".jpg") && !docName.endsWith(".jpeg") && !docName.endsWith(".png")
					&& !docName.endsWith(".pdf")) {
				return ErrorUtil.getError("90122", "Document Extension available ext are:JPG,JPEG,PNG,PDF ");
			}
		}

		return null;
	}

	private ErrorDetail otherDetailValidation(Mandate mandate) {
		if (!MandateExtension.PARTNER_BANK_REQ) {
			return null;
		}

		if (mandate.getPartnerBankId() <= 0) {
			return ErrorUtil.getError("90502", "partnerBankId");
		}

		PartnerBank partnerBank = partnerBankDAO.getPartnerBankById(mandate.getPartnerBankId(), "");

		if (partnerBank == null) {
			return ErrorUtil.getError("90263", "");
		}

		return null;
	}

	private ErrorDetail validateAccountDetail(Mandate mandate) {
		String ifsc = mandate.getIFSC();
		String micr = mandate.getMICR();
		if (StringUtils.isBlank(ifsc)
				&& (StringUtils.isBlank(mandate.getBankCode()) || StringUtils.isBlank(mandate.getBranchCode()))) {
			return ErrorUtil.getError("90313", "");
		}

		if (StringUtils.isBlank(mandate.getAccType())) {
			return ErrorUtil.getError("90502", "accType");
		}

		if (StringUtils.isBlank(mandate.getAccNumber())) {
			return ErrorUtil.getError("90502", "accNumber");
		}

		if (mandate.getAccNumber().length() > 50) {
			return ErrorUtil.getError("30568", "accNumber length", "50");
		}

		if (StringUtils.isBlank(mandate.getAccHolderName())) {
			return ErrorUtil.getError("90502", "accHolderName");
		}

		String bankCode = mandate.getBankCode();
		String branchCode = mandate.getBranchCode();

		BankBranch bankBranch = bankBranchService.getBankBranch(ifsc, micr, bankCode, branchCode);

		if (bankBranch.getError() != null) {
			return bankBranch.getError();
		}

		mandate.setBankCode(bankBranch.getBankCode());
		mandate.setMICR(bankBranch.getMICR());

		if (!bankBranchService.validateBranchCode(bankBranch, mandate.getMandateType())) {
			return ErrorUtil.getError("90333", mandate.getMandateType());
		}

		if (StringUtils.isNotBlank(mandate.getBankCode()) && StringUtils.isNotBlank(mandate.getAccNumber())) {
			BankDetail bankDetail = bankDetailService.getAccNoLengthByCode(mandate.getBankCode());
			if (bankDetail != null) {
				int maxAccNoLength = bankDetail.getAccNoLength();
				int minAccNolength = bankDetail.getMinAccNoLength();
				int length = mandate.getAccNumber().length();
				if (length < minAccNolength || length > maxAccNoLength) {
					String minMsg = String.valueOf(minAccNolength).concat(" characters");
					String maxMsg = String.valueOf(maxAccNoLength).concat(" characters");

					if (minAccNolength == maxAccNoLength) {
						return ErrorUtil.getError("30570", "AccountNumber(Mandate)", minMsg, maxMsg);
					} else {
						return ErrorUtil.getError("BNK001", "AccountNumber(Mandate)", minMsg, maxMsg);
					}
				}
			}
		}

		if (StringUtils.isNotBlank(mandate.getAccType())) {
			List<ValueLabel> accType = MandateUtil.getAccountTypes();
			boolean accTypeSts = false;
			for (ValueLabel value : accType) {
				if (StringUtils.equals(value.getValue(), mandate.getAccType())) {
					accTypeSts = true;
					break;
				}
			}
			if (!accTypeSts) {
				return ErrorUtil.getError("90308", mandate.getAccType());
			}
		}

		String mobileNumber = mandate.getPhoneNumber();
		if (StringUtils.isNotBlank(mobileNumber) && !(mobileNumber.matches("\\d{10}"))) {
			return ErrorUtil.getError("90278", "");
		}

		String accHolderRegix = "^$|^[A-Za-z]+[A-Za-z.\\s]*";

		String accHolderName = mandate.getAccHolderName();
		if (StringUtils.isNotBlank(accHolderName) && !(accHolderName.matches(accHolderRegix))) {
			return ErrorUtil.getError("90237", "AccHolderName");
		}

		String jointAccHolderName = mandate.getJointAccHolderName();
		if (StringUtils.isNotBlank(jointAccHolderName) && !(jointAccHolderName.matches(accHolderRegix))) {
			return ErrorUtil.getError("90237", "JointAccHolderName");
		}

		if (!InstrumentType.isSI(mandate.getMandateType())) {
			return mandateDetailValidation(mandate);
		}

		return null;
	}

	private ErrorDetail validateExisting(FinanceDetail fd, String vldGroup) {
		Mandate mandate = fd.getMandate();

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		String repaymentMethod = fm.getFinRepayMethod();

		if (mandate.getMandateID() == Long.MIN_VALUE) {
			return ErrorUtil.getError("90502", "MandateID");
		}

		Mandate curMandate = mandateDAO.getMandateById(mandate.getMandateID(), "_AView");
		if (curMandate == null) {
			return ErrorUtil.getError("90303", String.valueOf(mandate.getMandateID()));
		}

		String fmCustCIF = fm.getLovDescCustCIF();
		if (!PennantConstants.VLD_UPD_LOAN.equals(vldGroup) && !curMandate.getCustCIF().equals(fmCustCIF)) {
			return ErrorUtil.getError("90310", fmCustCIF, curMandate.getCustCIF());
		}

		if (curMandate.getCustID() != fm.getCustID()) {
			return ErrorUtil.getError("90310", fmCustCIF, curMandate.getCustCIF());
		}

		if (!repaymentMethod.equalsIgnoreCase(curMandate.getMandateType())) {
			return ErrorUtil.getError("90311", repaymentMethod, curMandate.getMandateType());
		}

		if (!mandate.isOpenMandate()) {
			return ErrorUtil.getError("MDT04", String.valueOf(mandate.getMandateID()));
		}

		if (StringUtils.isNotBlank(mandate.getOrgReference())) {
			return ErrorUtil.getError("90312", String.valueOf(mandate.getMandateID()));
		}

		if (!curMandate.isActive()) {
			return ErrorUtil.getError("81004", "mandate:", String.valueOf(mandate.getMandateID()));
		}

		fd.setMandate(curMandate);
		return null;
	}

	@Override
	public List<Entity> getEntities() {
		return this.entityDAO.getEntites();
	}

	@Override
	public long saveStatus(com.pennant.backend.model.mandate.MandateStatus mandateStatus) {
		return mandateStatusDAO.save(mandateStatus, "");
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
	public void setFinTypePartnerBankDAO(FinTypePartnerBankDAO finTypePartnerBankDAO) {
		this.finTypePartnerBankDAO = finTypePartnerBankDAO;
	}

	@Autowired
	public void setBankBranchService(BankBranchService bankBranchService) {
		this.bankBranchService = bankBranchService;
	}

	@Autowired
	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}

	@Autowired
	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}

	private MandateProcesses getMandateProcess() {
		return mandateProcesses == null ? defaultMandateProcess : mandateProcesses;
	}
}