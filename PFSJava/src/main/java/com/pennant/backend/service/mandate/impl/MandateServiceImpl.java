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

import org.apache.commons.collections4.CollectionUtils;
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
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.dao.mandate.MandateStatusDAO;
import com.pennant.backend.dao.mandate.MandateStatusUpdateDAO;
import com.pennant.backend.dao.partnerbank.PartnerBankDAO;
import com.pennant.backend.dao.pennydrop.PennyDropDAO;
import com.pennant.backend.dao.rmtmasters.FinTypePartnerBankDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WSReturnStatus;
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
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.mandate.MandateStatusUpdate;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.pennydrop.BankAccountValidation;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.pff.extension.MandateExtension;
import com.pennant.pff.lien.service.LienService;
import com.pennant.pff.mandate.InstrumentType;
import com.pennant.pff.mandate.MandateStatus;
import com.pennant.pff.mandate.MandateUtil;
import com.pennanttech.model.dms.DMSModule;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.core.TableType;
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
	private CustomerDAO customerDAO;
	private FinanceTypeDAO financeTypeDAO;
	private PennyDropDAO pennyDropDAO;
	private FinTypePartnerBankDAO finTypePartnerBankDAO;
	private BankBranchService bankBranchService;
	private BankDetailService bankDetailService;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private LienService lienService;

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

			if (mandate.isSecurityMandate()) {
				Long securityMandate = mandateDAO.getSecurityMandateIdByRef(mandate.getOrgReference());
				if (securityMandate == null) {
					mandateDAO.updateFinMandateId(mandate.getMandateID(), mandate.getOrgReference());
				}
			}

			if (ImplementationConstants.ALLOW_LIEN && InstrumentType.isSI(mandateType)) {
				FinanceMain fm = financeMainDAO.getFinanceMainByRef(mandate.getOrgReference(), "", false);
				FinanceDetail fd = new FinanceDetail();
				fd.setMandate(mandate);
				fd.getFinScheduleData().setFinanceMain(fm);
				fd.setModuleDefiner("Mandate Creation");
				lienService.save(fd, true);
			}

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

		if ((!PennantConstants.FINSOURCE_ID_API.equals(mandate.getSourceId()))
				&& (!RequestSource.UPLOAD.name().equals(mandate.getSourceId())) && !mandate.isSecondaryMandate()) {
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

		ErrorDetail error = mandateDateValidation(mandate);

		if (error != null) {
			auditDetail.setErrorDetail(error);
		}

		if (!MandateStatus.isInprocess(status) && !MandateStatus.isNew(status) && !mandate.isSecondaryMandate()
				&& !((MandateStatus.isApproved(status) || (MandateStatus.isRejected(status))))
				&& !StringUtils.equals(method, PennantConstants.method_doReject)) {

			boolean exists = mandateDAO.checkMandates(mandate.getOrgReference(), mandate.getMandateID(),
					mandate.isSecurityMandate());

			if (exists && !mandate.isSecurityMandate()) {
				String[] valueParm2 = new String[1];
				valueParm2[0] = String.valueOf(mandate.getOrgReference());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("41022", valueParm2)));
			}

			if (exists && mandate.isSecurityMandate()) {
				String[] valueParm2 = new String[1];
				valueParm2[0] = String.valueOf(mandate.getOrgReference());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("41222", valueParm2)));
			}
		}

		if (mandate.isSwapIsActive()
				&& (StringUtils.equals(PennantConstants.RCD_STATUS_SUBMITTED, mandate.getRecordStatus()))) {
			BigDecimal repayAmount = mandateDAO.getMaxRepayAmount(mandate.getMandateID());
			if (repayAmount != null && mandate.getMaxLimit().compareTo(repayAmount) < 0) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90320", null)));
			}

		}

		if (mandate.getMaxLimit() != null && mandate.getMaxLimit().compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal rpyAmt = mandateDAO.getMaxRepayAmount(mandate.getOrgReference());
			if (rpyAmt != null && mandate.getMaxLimit().compareTo(rpyAmt) < 0) {
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
							TableType.MAIN_TAB);
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

		if (StringUtils.isNotBlank(fm.getRepayFrq()) && StringUtils.isNotBlank(mandate.getPeriodicity())) {
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

		if (!mandate.isUseExisting()) {
			error = validateNewSecurityMandate(fd);

			if (error != null) {
				return error;
			}
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

	@Override
	public List<BankBranch> getBankBranchByMICR(String micr) {
		return bankBranchDAO.getBankBranchByMICR(micr);
	}

	private ErrorDetail validateNew(FinanceDetail fd) {
		Mandate mandate = fd.getMandate();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		String repaymentMethod = fm.getFinRepayMethod();

		mandate.setMandateID(Long.MIN_VALUE);
		mandate.setCustID(fm.getCustID());
		mandate.setLoanMaturityDate(fm.getMaturityDate());

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

			if (errordetail == null && !InstrumentType.isSI(mandateType)) {
				errordetail = otherDetailValidation(mandate);
			}

			break;
		case DAS:
			if (mandate.isSwapIsActive() && mandate.getSwapEffectiveDate() == null) {
				return ErrorUtil.getError("90502", "swapEffectiveDate");
			}

			Mandate employerDetails = getEmployerDetails(mandate.getCustID());

			if (employerDetails == null || employerDetails.getEmployerID().compareTo(mandate.getEmployerID()) != 0) {
				return ErrorUtil.getError("MNDT01", String.valueOf(mandate.getEmployerID()));
			} else if (!employerDetails.isAllowDAS()) {
				return ErrorUtil.getError("MNDT02", String.valueOf(mandate.getEmployerID()));
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

	private ErrorDetail validateNewSecurityMandate(FinanceDetail fd) {

		Mandate mandate = fd.getSecurityMandate();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		if (mandate == null) {
			return null;
		}

		mandate.setMandateID(Long.MIN_VALUE);
		mandate.setCustID(fm.getCustID());

		String mandateType = mandate.getMandateType();

		switch (InstrumentType.valueOf(mandateType)) {
		case ECS:
		case DD:
		case NACH:
		case EMANDATE:
			ErrorDetail errordetail = validateAccountDetail(mandate);

			if (errordetail == null && !InstrumentType.isSI(mandateType)) {
				errordetail = otherDetailValidation(mandate);
			}

			return errordetail;
		default:
			return null;
		}

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
			if (mandate.getExpiryDate() == null && MandateExtension.EXPIRY_DATE_MANDATORY) {
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

		if (mandate.getExpiryDate() != null && mandate.getLoanMaturityDate() != null
				&& mandate.getExpiryDate().before(mandate.getLoanMaturityDate())) {

			String maturityDate = DateUtil.formatToLongDate(mandate.getLoanMaturityDate());
			return ErrorUtil.getError("30509", "mandate Expiry date", maturityDate, strEndDate);
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

		if (mandate.getPartnerBankId() == null || mandate.getPartnerBankId() <= 0) {
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
		String fmCustCoreBank = fm.getLovDescCustCoreBank();
		if (!PennantConstants.VLD_UPD_LOAN.equals(vldGroup) && (!curMandate.getCustCIF().equals(fmCustCIF)
				&& !curMandate.getCustCoreBank().equals(fmCustCoreBank))) {
			return ErrorUtil.getError("90310", fmCustCIF, curMandate.getCustCIF());
		}

		if (curMandate.getCustID() != fm.getCustID() && !curMandate.getCustCoreBank().equals(fmCustCoreBank)) {
			return ErrorUtil.getError("90310", fmCustCIF, curMandate.getCustCIF());
		}

		if (!repaymentMethod.equalsIgnoreCase(curMandate.getMandateType())) {
			return ErrorUtil.getError("90311", repaymentMethod, curMandate.getMandateType());
		}

		if (!mandate.isOpenMandate() && StringUtils.isNotBlank(mandate.getOrgReference())) {
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
	public FinanceMain getEntityByRef(String finReference) {
		return financeMainDAO.getEntityByRef(finReference);
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

	@Override
	public Mandate createMandates(Mandate mandate) {
		Mandate response = new Mandate();

		ErrorDetail error = doMandateValidation(mandate);

		if (error != null) {
			response.setError(error);
			return response;
		}

		if (UploadConstants.FINSOURCE_ID_UPLOAD.equals(mandate.getSourceId())) {
			if (mandate.isExternalMandate() || !mandate.getMandateRef().isEmpty()) {
				response.setError(getError("91132", "external Mandate,", "UMRN number"));
				return response;
			}
		}

		if (UploadConstants.FINSOURCE_ID_API.equals(mandate.getSourceId())) {
			if (StringUtils.isNotBlank(mandate.getMandateRef()) && !InstrumentType.isEMandate(mandate.getMandateType())
					&& !mandate.isExternalMandate()) {
				response.setError(getError("90329", "mandateRef", "createMandate"));
				return response;
			}
		}

		return createMandate(prepareMandate(mandate));
	}

	private ErrorDetail doMandateValidation(Mandate mandate) {
		logger.debug(Literal.ENTERING);

		String mandateType = mandate.getMandateType();

		ErrorDetail error = basicValidation(mandate);

		if (error != null) {
			return error;
		}

		switch (InstrumentType.valueOf(mandateType)) {
		case ECS:
		case DD:
		case NACH:
		case EMANDATE:
		case SI:
			error = validateBankDetail(mandate);
			break;
		case DAS:
			if (mandate.getEmployerID() == null) {
				return getError("90502", "employerID");
			}

			Mandate empMandate = mandateDAO.getEmployerDetails(mandate.getCustID());

			if (empMandate == null || empMandate.getEmployerID().compareTo(mandate.getEmployerID()) != 0) {
				return getError("MNDT01", String.valueOf(mandate.getEmployerID()));
			} else if (!empMandate.isAllowDAS()) {
				return getError("MNDT02", String.valueOf(mandate.getEmployerID()));
			}
			break;
		default:
			break;
		}

		if (error != null) {
			return error;
		}

		if (mandate.getPartnerBankId() != null && mandate.getPartnerBankId() <= 0
				&& StringUtils.isNotBlank(mandate.getPartnerBankCode())) {
			Long partnerBankID = partnerBankDAO.getPartnerBankID(mandate.getPartnerBankCode());
			if (partnerBankID == null || partnerBankID <= 0) {
				String pbLabel = PennantJavaUtil.getLabel("label_MandateDialog_PartnerBank.value");
				return getError("90224", pbLabel, mandate.getPartnerBankCode());
			} else {
				mandate.setPartnerBankId(partnerBankID);
			}
		}

		if (InstrumentType.isECS(mandate.getMandateType()) || InstrumentType.isNACH(mandate.getMandateType())
				|| InstrumentType.isEMandate(mandate.getMandateType())) {
			if (StringUtils.isNotEmpty(mandate.getMandateRef())) {
				mandate.setExternalMandate(true);
			}
		}

		logger.debug(Literal.LEAVING);
		return error;
	}

	private ErrorDetail basicValidation(Mandate mandate) {
		String custCIF = mandate.getCustCIF();
		String entityCode = mandate.getEntityCode();

		long custID = customerDAO.getCustIDByCIF(custCIF);

		if (custID == 0) {
			return getError("90101", custCIF);
		}

		mandate.setCustID(custID);

		ErrorDetail error = validateEntityCode(entityCode);

		if (error != null) {
			return error;
		}

		return validateCustomerByLoan(mandate);
	}

	private ErrorDetail validateBankDetail(Mandate mandate) {
		if (StringUtils.isBlank(mandate.getAccType())) {
			return getError("90502", "accType");
		}

		List<ValueLabel> accType = MandateUtil.getAccountTypes();

		if (accType.stream().noneMatch(ac -> ac.getValue().equals(mandate.getAccType()))) {
			return getError("90308", mandate.getAccType());
		}

		String mobileNumber = mandate.getPhoneNumber();
		if (StringUtils.isNotBlank(mobileNumber) && !(mobileNumber.matches("\\d{10}"))) {
			return getError("90278", mobileNumber);
		}

		String ifsc = mandate.getIFSC();
		String micr = mandate.getMICR();
		String bankCode = mandate.getBankCode();
		String branchCode = mandate.getBranchCode();

		BankBranch bankBranch = bankBranchService.getBankBranch(ifsc, micr, bankCode, branchCode);

		ErrorDetail error = bankBranch.getError();
		if (error != null) {
			return error;
		}

		if (!InstrumentType.isDAS(mandate.getMandateType()) && mandate.isSwapIsActive()) {
			if (mandate.getSwapEffectiveDate() == null) {
				return getError("90502", "SwapEffectiveDate");
			}

			if (mandate.getSwapEffectiveDate().compareTo(SysParamUtil.getAppDate()) <= 0) {
				return getError("SI001", "SwapEffectiveDate", SysParamUtil.getAppDate().toString());
			}
		}

		if (InstrumentType.isSI(mandate.getMandateType())) {
			String dftBankCode = SysParamUtil.getValueAsString(SMTParameterConstants.BANK_CODE);
			if (!StringUtils.equalsIgnoreCase(dftBankCode, bankBranch.getBankCode())) {
				return getError("MNDT03", dftBankCode);
			}
		}

		mandate.setBankBranchID(bankBranch.getBankBranchID());
		mandate.setBankCode(bankBranch.getBankCode());
		mandate.setMICR(bankBranch.getMICR());

		if (!bankBranchService.validateBranchCode(bankBranch, mandate.getMandateType())) {
			return getError("90333", mandate.getMandateType());
		}

		if (StringUtils.isBlank(mandate.getAccNumber())) {
			return getError("90502", "accNumber");
		}

		if (StringUtils.isNotBlank(mandate.getBankCode()) && StringUtils.isNotBlank(mandate.getAccNumber())) {
			BankDetail bankDetails = bankDetailService.getAccNoLengthByCode(mandate.getBankCode());
			int length = mandate.getAccNumber().length();

			if (bankDetails != null) {
				int maxAccNoLength = bankDetails.getAccNoLength();
				int minAccNolength = bankDetails.getMinAccNoLength();
				if (length < minAccNolength || length > maxAccNoLength) {
					String minMsg = String.valueOf(minAccNolength).concat(" characters");
					String maxMsg = String.valueOf(maxAccNoLength).concat(" characters");

					if (minAccNolength == maxAccNoLength) {
						return getError("30570", "AccountNumber", maxMsg);
					} else {
						return getError("BNK001", "AccountNumber", minMsg, maxMsg);
					}
				}
			}
		}

		if (StringUtils.isBlank(mandate.getAccHolderName())) {
			return getError("90502", "accHolderName");
		}

		if (!(InstrumentType.isSI(mandate.getMandateType()) || (InstrumentType.isDAS(mandate.getMandateType())))) {
			return mandateValidation(mandate);
		}

		return null;

	}

	private ErrorDetail mandateValidation(Mandate mandate) {
		String mandateStatus = mandate.getStatus();
		String periodicity = mandate.getPeriodicity();

		if (StringUtils.isNotBlank(mandateStatus)) {
			List<ValueLabel> status = MandateUtil.getMandateStatus();

			if (status.stream().noneMatch(sts -> sts.getValue().equals(mandateStatus))) {
				return getError("90309", mandateStatus);
			}
		}

		if (UploadConstants.FINSOURCE_ID_UPLOAD.equals(mandate.getSourceId())
				&& StringUtils.isEmpty(mandate.getMICR())) {
			return getError("MNDT05", "MICR", mandate.getMandateType());
		}

		if (mandate.getStartDate() == null) {
			return getError("MNDT05", "Start Date", mandate.getMandateType());
		}

		if (StringUtils.isNotBlank(periodicity)) {
			ErrorDetail error = FrequencyUtil.validateFrequency(periodicity);
			if (error != null && StringUtils.isNotBlank(error.getCode())) {
				return getError("90207", periodicity);
			}
		}

		if (ImplementationConstants.ALLOW_BARCODE && StringUtils.isBlank(mandate.getBarCodeNumber())) {
			return getError("90502", "barCode");
		}

		ErrorDetail error = mandateDateValidation(mandate);
		if (error != null) {
			return error;
		}

		if (InstrumentType.isEMandate(mandate.getMandateType())) {
			error = validateEMandate(mandate);
			if (error != null) {
				return error;
			}
		}

		List<ErrorDetail> errors = doValidations(mandate);
		if (CollectionUtils.isNotEmpty(errors)) {
			for (ErrorDetail err : errors) {
				if (StringUtils.isNotBlank(err.getCode())) {
					return getError(err.getCode(), err.getError());
				}
			}
		}

		return null;
	}

	private ErrorDetail mandateDateValidation(Mandate mandate) {
		Date expiryDate = mandate.getExpiryDate();
		int mandStartDate = SysParamUtil.getValueAsInt(SMTParameterConstants.MANDATE_STARTDATE);
		Date appDate = SysParamUtil.getAppDate();
		Date mandbackDate = DateUtil.addDays(appDate, -mandStartDate);

		long finID = financeMainDAO.getFinIDByFinReference(mandate.getOrgReference(), "", false);
		FinanceMain fm = financeMainDAO.getFinanceMain(finID);
		mandate.setLoanMaturityDate(fm.getMaturityDate());
		List<FinanceScheduleDetail> fsd = financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false);
		BigDecimal exposure = BigDecimal.ZERO;
		Date firstRepayDate = null;

		if (!mandate.isOpenMandate() && (expiryDate == null && MandateExtension.EXPIRY_DATE_MANDATORY)) {
			return getError("90502", "expiryDate");
		}

		for (FinanceScheduleDetail schedule : fsd) {
			if (exposure.compareTo(schedule.getRepayAmount()) < 0) {
				exposure = schedule.getRepayAmount();
			}

			if ((schedule.isRepayOnSchDate() || schedule.isPftOnSchDate())
					&& isHoliday(schedule.getBpiOrHoliday(), fm.getBpiTreatment())) {
				if (schedule.getSchDate().compareTo(fm.getFinStartDate()) > 0 && firstRepayDate == null) {
					firstRepayDate = schedule.getSchDate();
				}
			}
		}

		Date mandateStartDate = mandate.getStartDate();
		Date dftEndDate = null;
		if (expiryDate != null) {
			dftEndDate = SysParamUtil.getValueAsDate(SMTParameterConstants.APP_DFT_END_DATE);
			if (expiryDate.compareTo(mandateStartDate) <= 0 || expiryDate.after(dftEndDate)) {
				return getError("90318", "ExpiryDate", DateUtil.formatToLongDate(DateUtil.addDays(mandateStartDate, 1)),
						DateUtil.formatToLongDate(dftEndDate));
			}

			if (mandate.getLoanMaturityDate() != null
					&& mandate.getExpiryDate().compareTo(mandate.getLoanMaturityDate()) < 0) {
				return getError("90318", "ExpiryDate", DateUtil.formatToLongDate(DateUtil.addDays(mandateStartDate, 1)),
						DateUtil.formatToLongDate(mandate.getLoanMaturityDate()));
			}
		}

		if (mandateStartDate != null) {
			if (dftEndDate == null) {
				dftEndDate = SysParamUtil.getValueAsDate(SMTParameterConstants.APP_DFT_END_DATE);
			}

			if (mandateStartDate.before(mandbackDate) || mandateStartDate.after(dftEndDate)) {
				return getError("90318", "mandate start date " + DateUtil.formatToLongDate(mandateStartDate),
						DateUtil.formatToLongDate(mandbackDate), DateUtil.formatToLongDate(dftEndDate));
			}

			if (mandate.getStartDate().after(mandate.getLoanMaturityDate())) {
				return getError("90318", "mandate start date " + DateUtil.formatToLongDate(mandateStartDate),
						DateUtil.formatToLongDate(mandbackDate),
						DateUtil.formatToLongDate(mandate.getLoanMaturityDate()));
			}

			if (firstRepayDate != null && mandate.getStartDate().compareTo(firstRepayDate) > 0
					&& firstRepayDate.compareTo(appDate) >= 0) {
				return getError("90318", "mandate start date " + DateUtil.formatToLongDate(mandateStartDate),
						DateUtil.formatToLongDate(appDate), DateUtil.formatToLongDate(firstRepayDate));
			}
		}

		if (mandate.getExpiryDate() != null && (mandate.getExpiryDate().before(mandate.getLoanMaturityDate())
				|| mandate.getExpiryDate().before(mandbackDate))) {
			return getError("30509",
					PennantJavaUtil.getLabel("tab_label_MANDATE") + " "
							+ PennantJavaUtil.getLabel("label_MandateDialog_ExpiryDate.value"),
					PennantJavaUtil.getLabel("label_MaturityDate"), PennantJavaUtil.getLabel("label_MandateBackDate"));
		}

		return null;
	}

	private ErrorDetail validateEMandate(Mandate mandate) {
		if (StringUtils.isBlank(mandate.geteMandateReferenceNo())) {
			return getError("90502", "eMandateReferenceNo");
		}
		if (StringUtils.isBlank(mandate.geteMandateSource())) {
			return getError("90502", "eMandateSource");
		}

		if (validateEmandateSource(mandate.geteMandateSource()) == 0) {
			return getError("90501", "eMandateSource ".concat(mandate.geteMandateSource()));
		}

		return null;
	}

	public Mandate createMandate(Mandate mandate) {
		logger.debug(Literal.ENTERING);

		Mandate response = new Mandate();

		prepareRequiredData(mandate);

		if (mandate.getReturnStatus() != null) {
			response.setReturnStatus(mandate.getReturnStatus());

			logger.debug(Literal.LEAVING);
			return response;
		}

		if (mandate.isSecurityMandate()
				&& (InstrumentType.isDAS(mandate.getMandateType()) || InstrumentType.isSI(mandate.getMandateType()))) {
			WSReturnStatus status = new WSReturnStatus();
			String[] valueParm = new String[2];
			valueParm[0] = "Mandate Type,";
			valueParm[1] = "Possible Values are NACH, ECS, EMANDATE ";

			ErrorDetail err = ErrorUtil.getError("STP0012", valueParm);
			status.setReturnCode(err.getCode());

			status.setReturnText(ErrorUtil.getErrorMessage(err.getMessage(), err.getParameters()));
			response.setReturnStatus(status);

			logger.debug(Literal.LEAVING);
			return response;
		}

		BankAccountValidation validation = new BankAccountValidation();
		if (mandate.getPennyDropStatus() != null) {
			validation.setiFSC(mandate.getIFSC());
			validation.setInitiateType("M");
			validation.setAcctNum(mandate.getAccNumber());
			validation.setStatus(mandate.getPennyDropStatus());

			pennyDropDAO.savePennyDropSts(validation);
		}

		mandate.setCustID(customerDAO.getCustIDByCIF(mandate.getCustCIF()));
		mandate.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		mandate.setNewRecord(true);
		mandate.setActive(true);
		mandate.setVersion(1);
		mandate.setMandateCcy(SysParamUtil.getAppCurrency());
		mandate.setStatus(MandateStatus.NEW);

		try {
			AuditHeader ah = doApprove(getAuditHeader(mandate, PennantConstants.TRAN_WF));

			List<ErrorDetail> errors = ah.getErrorMessage();
			if (CollectionUtils.isNotEmpty(errors)) {
				response.setError(errors.get(errors.size() - 1));
				return response;
			}

			response = (Mandate) ah.getAuditDetail().getModelData();
			response.setError(null);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			response.setError(getError("9999", "Unable to process request."));
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	private void prepareRequiredData(Mandate mandate) {
		LoggedInUser loggedInUser = mandate.getUserDetails();

		mandate.setUserDetails(loggedInUser);
		mandate.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		mandate.setInputDate(SysParamUtil.getAppDate());
		mandate.setLastMntBy(loggedInUser.getUserId());
		mandate.setLastMntOn(new Timestamp(System.currentTimeMillis()));

		if (!InstrumentType.isDAS(mandate.getMandateType())) {
			if (StringUtils.isBlank(mandate.getPeriodicity())) {
				mandate.setPeriodicity(MandateConstants.MANDATE_DEFAULT_FRQ);
			}
		}
	}

	@Override
	public Mandate getMandate(long mandateId) {
		logger.debug(Literal.ENTERING);

		Mandate response = getApprovedMandateById(mandateId);

		if (response == null) {
			response = new Mandate();
			response.setError(getError("90303", String.valueOf(mandateId)));
			return response;
		}

		int format = CurrencyUtil.getFormat(response.getMandateCcy());

		try {
			BigDecimal maxlimt = PennantApplicationUtil.formateAmount(response.getMaxLimit(), format);

			response.setAmountInWords(NumberToEnglishWords.getNumberToWords(maxlimt.toBigInteger()));
			response.setError(null);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			response.setError(getError("9999", "Unable to process request."));
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public ErrorDetail updateMandate(Mandate mandate) {
		logger.debug(Literal.ENTERING);

		long mandateID = mandate.getMandateID();

		Mandate detail = getApprovedMandateById(mandateID);

		if (detail == null) {
			return getError("90303", String.valueOf(mandateID));
		}

		String mandateStatus = detail.getStatus();

		if (MandateStatus.isApproved(mandateStatus)) {
			return getError("90345", "Approved");
		}

		ErrorDetail error = doMandateValidation(mandate);

		if (error != null) {
			return error;
		}

		if (StringUtils.isNotBlank(mandate.getMandateRef())) {
			return getError("90329", "mandateRef", "updateMandate");
		}

		if (MandateStatus.isAwaitingConf(mandateStatus)) {
			return getError("90345", "Awaiting Confirmation");
		}

		if (MandateStatus.isHold(mandateStatus)) {
			return getError("90345", "Hold");
		}

		prepareRequiredData(mandate);

		if (mandate.getError() != null) {
			logger.debug(Literal.LEAVING);
			return mandate.getError();
		}

		Mandate prvMandate = getApprovedMandateById(mandate.getMandateID());
		mandate.setCustID(prvMandate.getCustID());
		mandate.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		mandate.setNewRecord(false);
		mandate.setVersion(prvMandate.getVersion() + 1);
		mandate.setActive(true);
		mandate.setMandateCcy(SysParamUtil.getAppCurrency());
		mandate.setStatus(MandateStatus.NEW);

		BeanUtils.copyProperties(mandate, prvMandate);

		try {
			AuditHeader ah = doApprove(getAuditHeader(prvMandate, PennantConstants.TRAN_WF));

			List<ErrorDetail> errors = ah.getErrorMessage();
			if (CollectionUtils.isNotEmpty(errors)) {
				error = errors.get(errors.size() - 1);
				logger.debug(Literal.LEAVING);
				return getError(error.getCode(), error.getError());
			}

			logger.debug(Literal.LEAVING);
			return null;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			return getError("9999", "Unable to process request.");
		}
	}

	@Override
	public ErrorDetail deleteMandate(long mandateID, LoggedInUser loggedInUser) {
		logger.debug(Literal.ENTERING);

		if (!mandateDAO.isValidMandate(mandateID)) {
			return getError("90303", String.valueOf(mandateID));
		}

		Mandate mandate = getApprovedMandateById(mandateID);

		mandate.setUserDetails(loggedInUser);
		prepareRequiredData(mandate);

		if (mandate.getReturnStatus() != null) {
			logger.debug(Literal.LEAVING);
			return mandate.getError();
		}

		mandate.setRecordType(PennantConstants.RECORD_TYPE_DEL);
		mandate.setNewRecord(false);
		mandate.setVersion(mandate.getVersion() + 1);
		mandate.setSourceId(RequestSource.API.name());

		try {
			AuditHeader ah = doApprove(getAuditHeader(mandate, PennantConstants.TRAN_WF));

			List<ErrorDetail> errors = ah.getErrorMessage();
			if (CollectionUtils.isNotEmpty(errors)) {
				ErrorDetail error = errors.get(errors.size() - 1);
				logger.debug(Literal.LEAVING);
				return getError(error.getCode(), error.getError());
			}

			logger.debug(Literal.LEAVING);
			return null;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			return getError("9999", "Unable to process request.");
		}
	}

	@Override
	public Mandate approveMandate(Mandate mandate) {
		Mandate response = new Mandate();

		ErrorDetail error = doMandateValidation(mandate);

		if (error != null) {
			response.setError(error);
		}

		if (StringUtils.isBlank(mandate.getMandateRef())) {
			response.setError(getError("90502", "mandateRef"));
			return response;
		}

		if (getMandateByMandateRef(mandate.getMandateRef()) > 0) {
			response.setError(getError("41001", "mandateRef with ", mandate.getMandateRef()));
			return response;
		}

		if (mandate.isSwapIsActive() && StringUtils.isBlank(mandate.getOrgReference())) {
			response.setError(getError("90502", "finReference"));
			return response;
		}

		if (mandate.isSwapIsActive()) {
			TableType tableType = TableType.MAIN_TAB;
			if (MandateExtension.APPROVE_ON_LOAN_ORG) {
				tableType = TableType.TEMP_TAB;
			}

			String finType = financeMainDAO.getFinanceType(mandate.getOrgReference(), tableType);

			String alwRepayMthds = StringUtils.trimToEmpty(financeTypeDAO.getAllowedRepayMethods(finType, ""));
			if (StringUtils.isNotBlank(alwRepayMthds) && !alwRepayMthds.contains(mandate.getMandateType())) {
				response.setError(getError("90307", mandate.getMandateType()));
				return response;
			}
		}

		response = createMandate(prepareMandate(mandate));

		return response;
	}

	@Override
	public ErrorDetail updateStatus(Mandate mandate) {
		logger.debug(Literal.ENTERING);

		ErrorDetail error = validateRequestData(mandate);
		if (error != null) {
			return error;
		}

		int count = mandateDAO.updateMandateStatus(mandate);

		if (count == 0) {
			logger.error(Literal.LEAVING);
			return getError("9999", "Unable to process request.");
		}

		long mandateID = mandate.getMandateID();
		String status = mandate.getStatus();

		com.pennant.backend.model.mandate.MandateStatus mandateStatus = new com.pennant.backend.model.mandate.MandateStatus();

		mandateStatus.setMandateID(mandateID);
		mandateStatus.setStatus(status);
		mandateStatus.setReason(mandate.getReason());
		mandateStatus.setChangeDate(SysParamUtil.getAppDate());

		try {
			saveStatus(mandateStatus);

			if ((MandateStatus.isApproved(status) || MandateStatus.isAccepted(status)) && mandate.isSwapIsActive()) {
				String type = "";
				Long finID = financeMainDAO.getFinID(mandate.getOrgReference(), TableType.MAIN_TAB);

				if (finID != null && MandateExtension.APPROVE_ON_LOAN_ORG) {
					type = "_Temp";
				}

				String mandateType = mandate.getMandateType();
				financeMainDAO.loanMandateSwapping(finID, mandateID, mandateType, type, false);
			}

			logger.debug(Literal.LEAVING);
			return null;

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			return getError("9999", "Unable to process request.");
		}
	}

	@Override
	public ErrorDetail updateApprovedMandate(Mandate oldMandate) {
		ErrorDetail error = validate(oldMandate);

		if (error != null) {
			return error;
		}

		Mandate mandate = copyBeforeImage(oldMandate);
		if (updateMandateStatus(mandate) <= 0) {
			return getError("9999", "Unable to process request.");
		}

		com.pennant.backend.model.mandate.MandateStatus mandateStatus = new com.pennant.backend.model.mandate.MandateStatus();

		mandateStatus.setMandateID(mandate.getMandateID());
		mandateStatus.setStatus(mandate.getStatus());
		mandateStatus.setChangeDate(SysParamUtil.getAppDate());

		try {
			saveStatus(mandateStatus);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			return getError("9999", "Unable to process request.");
		}

		return null;
	}

	private Mandate copyBeforeImage(Mandate request) {
		Mandate exMandate = getApprovedMandateById(request.getMandateID());

		Mandate mandate2 = new Mandate();
		BeanUtils.copyProperties(exMandate, mandate2);
		mandate2.setBefImage(exMandate);

		if (StringUtils.isNotBlank(request.getMandateRef())) {
			mandate2.setMandateRef(request.getMandateRef());
		}

		mandate2.setStatus(request.getStatus());
		return mandate2;
	}

	private ErrorDetail validate(Mandate request) {
		if (request.getMandateID() == Long.MIN_VALUE) {
			return getError("90502", "mandateID");
		}

		Mandate aMandate = mandateDAO.getMandateDetail(request.getMandateID());
		if (aMandate == null || !aMandate.isActive()) {
			return getError("90303", String.valueOf(request.getMandateID()));
		}

		if (StringUtils.isBlank(request.getStatus())) {
			return getError("90502", "status");
		}

		if (!MandateStatus.isApproved(request.getStatus()) && !MandateStatus.isRejected(request.getStatus())) {
			return getError("90281", "status", MandateStatus.APPROVED + ", " + MandateStatus.REJECTED);
		} else if (MandateStatus.isApproved(aMandate.getStatus()) || MandateStatus.isRejected(aMandate.getStatus())) {
			return getError("90345", "already ", aMandate.getStatus());
		}

		if (StringUtils.isNotEmpty(request.getMandateRef()) && MandateStatus.isRejected(request.getStatus())) {
			return getError("RU0039", "For the Status REJECTED mandateRef is");

		} else if (!StringUtils.isBlank(request.getMandateRef()) && request.getMandateRef().length() > 50) {
			return getError("90300", "mandateRef", "50");
		}

		return null;
	}

	@Override
	public List<Mandate> getMandatesByCif(String cif) {
		return getApprovedMandatesByCustomerId(customerDAO.getCustIDByCIF(cif));
	}

	@Override
	public ErrorDetail loanMandateSwapping(String finReference, Long oldMandateId, Long newMandateId) {
		logger.debug(Literal.ENTERING);

		Mandate newMandate = doLoanSwappingValidation(finReference, oldMandateId, newMandateId);

		ErrorDetail error = newMandate.getError();

		if (error != null) {
			return error;
		}

		String mandateType = newMandate.getMandateType();

		Mandate mandateById = getMandateById(oldMandateId);

		if (mandateById == null) {
			return getError("93304", "OldMandateId");
		}

		Mandate mandate = getMandateById(newMandateId);

		if (mandate == null) {
			return getError("93304", "NewMandateId");
		}

		boolean securityMandate = mandateById.isSecurityMandate();

		Long finID = financeMainDAO.getFinID(finReference, TableType.MAIN_TAB);

		if (financeMainDAO.loanMandateSwapping(finID, newMandateId, mandateType, "", securityMandate) > 0) {
			logger.debug(Literal.LEAVING);
			return null;
		}

		logger.debug(Literal.LEAVING);
		return getError("9999", "Unable to process request.");
	}

	private Mandate doLoanSwappingValidation(String finReference, Long oldMandateId, Long newMandateId) {
		logger.debug(Literal.ENTERING);

		Mandate newMandate = new Mandate();

		if (finReference == null) {
			newMandate.setError(getError("90502", "FinReference"));
		}

		if (oldMandateId == null || oldMandateId <= 0) {
			newMandate.setError(getError("90502", "OldMandateId"));
		}

		if (newMandateId == null || newMandateId <= 0) {
			newMandate.setError(getError("90502", "NewMandateId"));
		}

		long custID = mandateDAO.getCustID(oldMandateId);

		if (custID <= 0) {
			newMandate.setError(getError("90303", String.valueOf(oldMandateId)));
		}

		Long finID = financeMainDAO.getFinIDForMandate(finReference, oldMandateId);
		if (finID == null) {
			newMandate.setError(getError("90201", finReference));
		}

		newMandate = getApprovedMandateById(newMandateId);
		if (newMandate == null) {
			newMandate = new Mandate();
			newMandate.setError(getError("90303", String.valueOf(newMandateId)));
		}

		if (custID != newMandate.getCustID()) {
			newMandate.setError(getError("90342"));
		}

		if (!MandateConstants.skipRegistration().contains(newMandate.getMandateType())
				&& StringUtils.isBlank(newMandate.getMandateRef())) {
			newMandate.setError(getError("90305", String.valueOf(newMandateId)));
		}

		if (MandateStatus.isRejected(newMandate.getStatus())) {
			newMandate.setError(getError("90306", newMandate.getStatus()));
		}

		if (!newMandate.isOpenMandate() && StringUtils.isNotBlank(newMandate.getOrgReference())) {
			newMandate.setError(getError("90312", String.valueOf(newMandateId)));
		}

		logger.debug(Literal.LEAVING);
		return newMandate;

	}

	private ErrorDetail validateRequestData(Mandate mandate) {
		logger.debug(Literal.ENTERING);

		if (mandate.getMandateID() == Long.MIN_VALUE) {
			return getError("90502", "mandateID");
		}

		Mandate aMandate = mandateDAO.getMandateDetail(mandate.getMandateID());
		if (aMandate == null || !aMandate.isActive()) {
			return getError("90303", String.valueOf(mandate.getMandateID()));
		}

		String status = mandate.getStatus();

		if (StringUtils.isBlank(status)) {
			return getError("90502", "status");
		}

		String mandateStatus = "N";
		String mandateRegStatus = SysParamUtil.getValueAsString(SMTParameterConstants.MANDATE_REGISTRATION_STATUS);
		if (StringUtils.isNotBlank(mandateRegStatus)) {
			mandateStatus = mandateRegStatus;
		}

		if (StringUtils.equalsIgnoreCase(mandateStatus, status)) {
			mandate.setStatus(status.toUpperCase());
		}

		if (!StringUtils.equalsIgnoreCase(mandateStatus, status) && !MandateStatus.isRejected(status)
				&& !MandateStatus.isAcknowledge(status)) {
			String msg = mandateRegStatus.concat(", ").concat(PennantConstants.RCD_STATUS_REJECTED).concat(", ")
					.concat(MandateStatus.ACKNOWLEDGE);
			return getError("90281", "status", msg);
		}

		if ((MandateStatus.isApproved(status) || MandateStatus.isAccepted(status))
				&& StringUtils.isBlank(mandate.getMandateRef())) {
			return getError("90502", "mandateRef/UMRNNo");
		}

		if ((MandateStatus.isApproved(status) || MandateStatus.isAccepted(status) || MandateStatus.isRejected(status))
				&& StringUtils.isNotBlank(mandate.getMandateRef())
				&& !MandateStatus.isAcknowledge(aMandate.getStatus())) {
			return getError("30550", "Mandate will Approve/Reject only when it is Acknowledged.");
		}

		if (MandateStatus.isRejected(status) && StringUtils.isBlank(mandate.getReason())) {
			return getError("90502", "reason");
		}

		if (StringUtils.isNotBlank((mandateRegStatus)) && StringUtils.equalsIgnoreCase(status, mandateRegStatus)) {
			mandate.setStatus("APPROVED");
		} else {
			mandate.setStatus(status.toUpperCase());
		}

		if (StringUtils.isNotBlank(mandate.getOrgReference())) {
			Mandate tempMandate = getMandateStatusById(mandate.getOrgReference(), mandate.getMandateID());
			if (tempMandate == null) {
				StringBuilder msg = new StringBuilder("FinReference ");
				msg.append(mandate.getOrgReference());
				msg.append(" is not assign to mandateId ");
				msg.append(mandate.getMandateID());

				return getError("30550", msg.toString());
			}
		}

		if ((MandateStatus.isApproved(status) || MandateStatus.isAccepted(status)) && aMandate.isSwapIsActive()) {
			mandate.setSwapIsActive(aMandate.isSwapIsActive());
			mandate.setMandateType(aMandate.getMandateType());

			TableType tableType = TableType.MAIN_TAB;
			if (MandateExtension.APPROVE_ON_LOAN_ORG) {
				tableType = TableType.TEMP_TAB;
			}

			String finType = financeMainDAO.getFinanceType(mandate.getOrgReference(), tableType);
			String allowedRepayModes = financeTypeDAO.getAllowedRepayMethods(finType, "");

			if (!allowedRepayModes.contains(aMandate.getMandateType())) {
				return getError("90307", mandate.getMandateType());
			}
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	private ErrorDetail validateEntityCode(String entityCode) {
		if (StringUtils.isBlank(entityCode)) {
			return getError("90502", "Entity");
		}

		if (entityDAO.getEntityCount(entityCode) == 0) {
			return getError("90701", "Entity", entityCode);
		}

		return null;
	}

	private ErrorDetail validateCustomerByLoan(Mandate mandate) {
		String custCIF = mandate.getCustCIF();
		String mandateType = mandate.getMandateType();
		String orgReference = mandate.getOrgReference();

		if (StringUtils.isEmpty(orgReference)) {
			return getError("90502", "FinReference");
		}

		Mandate loanInfo = mandateDAO.getLoanInfo(orgReference);

		if (loanInfo == null) {
			return getError("90201", orgReference);
		}

		mandate.setLoanMaturityDate(financeMainDAO.getMaturityDate(orgReference));

		if (loanInfo.getCustID() != mandate.getCustID()) {
			return getError("90406", custCIF, orgReference);
		} else {
			mandate.setCustID(loanInfo.getCustID());
		}

		if (!loanInfo.getAlwdRpyMethods().contains(mandateType)) {
			return getError("90307", mandateType);
		}

		return null;
	}

	private void setBasicDetails(Mandate mandate, Mandate mndt) {
		mndt.setMandateType(mandate.getMandateType());
		mndt.setOrgReference(mandate.getOrgReference());
		mndt.setEntityCode(mandate.getEntityCode());
		mndt.setCustCIF(mandate.getCustCIF());
		mndt.setUserDetails(mandate.getUserDetails());
		mndt.setSourceId(mandate.getSourceId());
		mndt.setExternalMandate(mandate.isExternalMandate());
	}

	private void setMandateDetails(Mandate mandate, Mandate mndt) {
		mndt.setMandateRef(mandate.getMandateRef());
		mndt.setOpenMandate(mandate.isOpenMandate());
		mndt.setDefaultMandate(mandate.isDefaultMandate());
		mndt.setInputDate(mandate.getInputDate());

		if (mandate.getStartDate() == null) {
			mndt.setStartDate(SysParamUtil.getAppDate());
		}

		mndt.setStartDate(mandate.getStartDate());
		mndt.setExpiryDate(mandate.getExpiryDate());
		mndt.setPeriodicity(mandate.getPeriodicity());
		mndt.setMaxLimit(mandate.getMaxLimit());
		mndt.setSecurityMandate(mandate.isSecurityMandate());
	}

	private void setAccountDetails(Mandate mandate, Mandate mndt) {
		mndt.setBankBranchID(mandate.getBankBranchID());
		mndt.setAccNumber(mandate.getAccNumber());
		mndt.setAccHolderName(mandate.getAccHolderName());
		mndt.setJointAccHolderName(mandate.getJointAccHolderName());
		mndt.setAccType(mandate.getAccType());
		mndt.setIFSC(mandate.getIFSC());
		mndt.setMICR(mandate.getMICR());
		mndt.setBankCode(mandate.getBankCode());
		mndt.setBranchCode(mandate.getBranchCode());
	}

	private void setMandateSwapDetails(Mandate mandate, Mandate mndt) {
		mndt.setSwapIsActive(mandate.isSwapIsActive());
		mndt.setSwapEffectiveDate(mandate.getSwapEffectiveDate());
	}

	private void setDASDetails(Mandate mandate, Mandate mndt) {
		mndt.setSwapIsActive(mandate.isSwapIsActive());
		mndt.setSwapEffectiveDate(mandate.getSwapEffectiveDate());
		mndt.setEmployerID(mandate.getEmployerID());
		mndt.setEmployeeNo(mandate.getEmployeeNo());
	}

	private void setEMandateDetails(Mandate mandate, Mandate mndt) {
		mndt.seteMandateSource(mandate.geteMandateSource());
		mndt.seteMandateReferenceNo(mandate.geteMandateReferenceNo());
	}

	private void setOtherDetails(Mandate mandate, Mandate mndt) {
		mndt.setPennyDropStatus(mandate.getPennyDropStatus());
		mndt.setPartnerBankId(mandate.getPartnerBankId());
		mndt.setPartnerBankCode(mandate.getPartnerBankCode());
	}

	private Mandate prepareMandate(Mandate mandate) {
		Mandate mndt = new Mandate();

		String mandateType = mandate.getMandateType();
		setBasicDetails(mandate, mndt);

		switch (InstrumentType.valueOf(mandateType)) {
		case ECS:
		case NACH:
		case EMANDATE:
			setMandateDetails(mandate, mndt);
			setAccountDetails(mandate, mndt);
			setMandateSwapDetails(mandate, mndt);
			if (InstrumentType.isEMandate(mandateType)) {
				setEMandateDetails(mandate, mndt);
			}
			setOtherDetails(mandate, mndt);
			break;
		case SI:
			mndt.setMaxLimit(BigDecimal.ZERO);
			setAccountDetails(mandate, mndt);
			setMandateSwapDetails(mandate, mndt);
			break;
		case DAS:
			setDASDetails(mandate, mndt);
			break;
		default:
			break;

		}

		return mndt;
	}

	private boolean isHoliday(String bpiOrHoliday, String bpiTreatment) {
		if (bpiOrHoliday == null && bpiTreatment == null) {
			return true;
		}

		if (FinanceConstants.FLAG_BPI.equals(bpiOrHoliday)) {
			if (FinanceConstants.BPI_DISBURSMENT.equals(bpiTreatment)) {
				return false;
			}
		}

		if (FinanceConstants.FLAG_HOLIDAY.equals(bpiOrHoliday) || FinanceConstants.FLAG_POSTPONE.equals(bpiOrHoliday)
				|| FinanceConstants.FLAG_MORTEMIHOLIDAY.equals(bpiOrHoliday)
				|| FinanceConstants.FLAG_UNPLANNED.equals(bpiOrHoliday)
				|| FinanceConstants.FLAG_BPI.equals(bpiOrHoliday)) {
			return true;
		}

		return false;
	}

	@Autowired
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	@Autowired
	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	@Autowired
	public void setPennyDropDAO(PennyDropDAO pennyDropDAO) {
		this.pennyDropDAO = pennyDropDAO;
	}

	@Autowired
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Override
	public PaymentInstruction getBeneficiatyDetailsByMandateId(Long mandateId) {
		return mandateDAO.getBeneficiary(mandateId);
	}

	@Autowired
	public void setLienService(LienService lienService) {
		this.lienService = lienService;
	}

}