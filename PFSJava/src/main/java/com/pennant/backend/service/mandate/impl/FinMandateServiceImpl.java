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
 * * FileName : FinMandateServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-10-2016 * *
 * Modified Date : 26-10-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-10-2016 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.mandate.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.util.resource.Labels;

import com.pennant.app.constants.ImplementationConstants;
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
import com.pennant.backend.dao.pdc.ChequeDetailDAO;
import com.pennant.backend.model.applicationmaster.MandateCheckDigit;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.service.mandate.FinMandateService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.pff.lien.service.LienService;
import com.pennant.pff.mandate.InstrumentType;
import com.pennant.pff.mandate.MandateStatus;
import com.pennanttech.model.dms.DMSModule;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.external.MandateProcesses;

/**
 * Service implementation for methods that depends on <b>FinMandate</b>.<br>
 * 
 */
public class FinMandateServiceImpl extends GenericService<Mandate> implements FinMandateService {
	private static final Logger logger = LogManager.getLogger(FinMandateServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private MandateDAO mandateDAO;
	private MandateStatusDAO mandateStatusDAO;
	private FinanceMainDAO financeMainDAO;
	private BankBranchService bankBranchService;
	private MandateProcesses defaultMandateProcess;
	private MandateCheckDigitDAO mandateCheckDigitDAO;
	private BankBranchDAO bankBranchDAO;
	private ChequeDetailDAO chequeDetailDAO;
	private LienService lienService;
	@Autowired(required = false)
	private MandateProcesses mandateProcesses;

	public FinMandateServiceImpl() {
		super();
	}

	@Override
	public Mandate getMnadateByID(Long mandateID) {
		Mandate mandate = mandateDAO.getMandateById(mandateID, "_View");
		if (mandate != null && mandate.getDocumentRef() != null && mandate.getDocumentRef() > 0) {
			byte[] data = getDocumentImage(mandate.getDocumentRef());
			if (data != null) {
				mandate.setDocImage(data);
			}
		}
		return mandate;
	}

	@Override
	public Mandate getSecurityMandate(String finReference) {
		Mandate mandate = mandateDAO.getMandateByFinReference(finReference, "_View");
		if (mandate != null && mandate.getDocumentRef() != null && mandate.getDocumentRef() > 0) {
			byte[] data = getDocumentImage(mandate.getDocumentRef());
			if (data != null) {
				mandate.setDocImage(data);
			}
		}
		return mandate;
	}

	@Override
	public List<Mandate> getMnadateByCustID(long custID, long mandateID) {
		return mandateDAO.getMnadateByCustID(custID, mandateID);
	}

	@Override
	public void saveOrUpdate(FinanceMain fm, Mandate mandate, AuditHeader auditHeader, String tableType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		boolean isMandateReq = true;

		if (!mandate.isSecurityMandate()) {
			isMandateReq = InstrumentType.mandateRequired(fm.getFinRepayMethod());
		}

		String finReference = fm.getFinReference();

		if (!isMandateReq) {
			deleteMandate(finReference, mandate, auditDetails);
			fm.setMandateID(0L);
			addAudit(auditHeader, auditDetails);

			logger.debug(Literal.LEAVING);

			return;
		}

		if (mandate.getCustID() == 0 || mandate.getCustID() == Long.MIN_VALUE) {
			mandate.setCustID(fm.getCustID());
		}

		Mandate useExisting = checkExistingMandate(mandate.getMandateID());

		boolean isSecurityMandate = mandate.isSecurityMandate();

		if (useExisting != null) {
			deleteMandate(finReference, mandate, auditDetails);
			if (mandate.isSecurityMandate()) {
				fm.setSecurityMandateID(mandate.getMandateID());
			} else {
				fm.setMandateID(mandate.getMandateID());
			}
		} else {
			Mandate oldmandate = mandateDAO.getMandateByOrgReference(finReference, isSecurityMandate, MandateStatus.FIN,
					tableType);

			/*
			 * if (mandate.isSecurityMandate() && !oldmandate.isSecurityMandate()) { oldmandate = null; }
			 */

			mandate.setOrgReference(finReference);

			if (oldmandate != null) {
				mandate.setMandateID(oldmandate.getMandateID());
				mandate.setOrgReference(fm.getFinReference());
				mandate.setStatus(MandateStatus.FIN);
				getDocument(mandate);
				mandateDAO.updateFinMandate(mandate, tableType);
				auditDetails.add(getAuditDetails(mandate, 1, PennantConstants.TRAN_UPD));
			} else {
				mandate.setStatus(MandateStatus.FIN);
				mandate.setOrgReference(fm.getFinReference());
				getDocument(mandate);
				long mandateID = mandateDAO.save(mandate, tableType);
				if (mandate.isSecurityMandate()) {
					fm.setSecurityMandateID(mandateID);
				} else {
					fm.setMandateID(mandateID);
				}
				auditDetails.add(getAuditDetails(mandate, 1, PennantConstants.TRAN_ADD));
			}

			mandate.setOrgReference(finReference);
			mandate.setStatus(MandateStatus.FIN);
			getDocument(mandate);
		}

		addAudit(auditHeader, auditDetails);
		logger.debug(" Leaving ");

	}

	@Override
	public void doApprove(FinanceDetail fd, Mandate mandate, AuditHeader auditHeader, String tableType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

		List<CustomerEMail> customer = new ArrayList<>();
		if (fd.getCustomerDetails() != null) {
			customer = fd.getCustomerDetails().getCustomerEMailList();
		}

		boolean isMandateReq = true;

		if (!mandate.isSecurityMandate()) {
			isMandateReq = InstrumentType.mandateRequired(fm.getFinRepayMethod());
		}

		if (!isMandateReq) {
			fm.setMandateID(0L);

			deleteMandate(fm.getFinReference(), mandate, auditDetails);
			addAudit(auditHeader, auditDetails);

			logger.debug(Literal.LEAVING);

			return;
		}

		if (mandate.getCustID() == 0 || mandate.getCustID() == Long.MIN_VALUE) {
			mandate.setCustID(fm.getCustID());
		}

		Mandate useExisting = checkExistingMandate(mandate.getMandateID());

		if (useExisting != null) {
			fm.setMandateID(mandate.getMandateID());
			if (StringUtils.isEmpty(useExisting.getOrgReference())) {
				mandateDAO.updateOrgReferecne(mandate.getMandateID(), fm.getFinReference(), "");
			}
		} else {
			mandate.setOrgReference(fm.getFinReference());
			mandate.setStatus(MandateStatus.NEW);
			mandate.setRecordType("");
			mandate.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			if (mandate.getMandateStatus() != null && mandate.getMandateStatus() == true) {
				mandate.setStatus(DisbursementConstants.STATUS_AWAITCON);
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
			// PSD : 194021
			mandate.setRoleCode("");
			mandate.setNextRoleCode("");
			mandate.setTaskId("");
			mandate.setNextTaskId("");
			mandate.setWorkflowId(0);
			getDocument(mandate);

			long mandateID = mandateDAO.save(mandate, tableType);

			if (mandate.isSecurityMandate()) {
				fm.setSecurityMandateID(mandateID);
			} else {
				fm.setMandateID(mandateID);
			}

			auditDetails.add(getAuditDetails(mandate, 1, PennantConstants.TRAN_ADD));

			com.pennant.backend.model.mandate.MandateStatus mandateStatus = new com.pennant.backend.model.mandate.MandateStatus();
			mandateStatus.setMandateID(mandate.getMandateID());
			mandateStatus.setStatus(mandate.getStatus());
			mandateStatus.setReason(mandate.getReason());
			mandateStatus.setChangeDate(SysParamUtil.getAppDate());

			mandateStatusDAO.save(mandateStatus, "");

			try {
				BigDecimal maxlimt = PennantApplicationUtil.formateAmount(mandate.getMaxLimit(),
						CurrencyUtil.getFormat(mandate.getMandateCcy()));
				mandate.setAmountInWords(NumberToEnglishWords.getNumberToWords(maxlimt.toBigInteger()));
				mandate.setDiDate(fm.getFinStartDate());
				mandate.setFinType(fm.getFinType());
				mandate.setAppFormNo(fm.getApplicationNo());
				mandate.setLoanBranch(fm.getLovDescFinBranchName());

				if (PennantConstants.FINSOURCE_ID_API.equals(fm.getFinSourceID())
						&& auditHeader.getApiHeader() != null) {
					BankBranch bankBranch = bankBranchDAO.getBankBrachByMicr(mandate.getMICR(), "");

					if (bankBranch != null) {
						mandate.setBankName(bankBranch.getBankName());
						mandate.setBranchDesc(bankBranch.getBranchDesc());
						mandate.setApprovalID(String.valueOf(mandate.getUserDetails().getUserId()));
					}
				}

				for (CustomerEMail customerEMail : customer) {
					if (customerEMail.getCustEMailPriority() == 5) {
						mandate.setEmailId(customerEMail.getCustEMail());
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

		deleteMandate(fm.getFinReference(), mandate, auditDetails);
		addAudit(auditHeader, auditDetails);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void doRejct(FinanceDetail fd, AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();
		Mandate mandate = fd.getMandate();

		if (mandate != null) {
			Mandate useExisting = checkExistingMandate(mandate.getMandateID());
			if (useExisting == null) {
				if (mandate.getBankBranchID() != 0 && mandate.getBankBranchID() != Long.MIN_VALUE) {
					// PSD : 194021
					mandate.setRoleCode("");
					mandate.setNextRoleCode("");
					mandate.setTaskId("");
					mandate.setNextTaskId("");
					mandate.setWorkflowId(0);
					mandate.setRecordType("");
					mandate.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					mandate.setStatus(com.pennant.pff.mandate.MandateStatus.REJECTED);
					mandate.setReason(Labels.getLabel("Mandate_Rejected_In_Loan"));
					long mandateID = mandateDAO.save(mandate, "");
					fm.setMandateID(mandateID);
					auditDetails.add(getAuditDetails(mandate, 1, PennantConstants.TRAN_ADD));

					com.pennant.backend.model.mandate.MandateStatus mandateStatus = new com.pennant.backend.model.mandate.MandateStatus();
					mandateStatus.setMandateID(mandate.getMandateID());
					mandateStatus.setStatus(mandate.getStatus());
					mandateStatus.setReason(Labels.getLabel("Mandate_Rejected_In_Loan"));
					mandateStatus.setChangeDate(SysParamUtil.getAppDate());
					mandateStatusDAO.save(mandateStatus, "");
				}

			}
		}

		deleteMandate(fm.getFinReference(), mandate, auditDetails);
		addAudit(auditHeader, auditDetails);

		logger.debug(Literal.LEAVING);
	}

	private void deleteMandate(String finreferece, Mandate mandate, List<AuditDetail> auditDetails) {
		boolean securityMandate = false;

		if (mandate != null) {
			securityMandate = mandate.isSecurityMandate();
		}

		Mandate oldmandate = mandateDAO.getMandateByOrgReference(finreferece, securityMandate, MandateStatus.FIN,
				"_Temp");

		if (oldmandate != null) {
			mandateDAO.delete(oldmandate, "_Temp");
			auditDetails.add(getAuditDetails(oldmandate, 2, PennantConstants.TRAN_DEL));
		}

	}

	private Mandate checkExistingMandate(long mandateID) {
		return mandateDAO.getMandateById(mandateID, "");
	}

	private void addAudit(AuditHeader auditHeader, List<AuditDetail> auditDetails) {
		if (auditDetails.isEmpty()) {
			return;
		}

		AuditHeader header = getAuditHeader(auditHeader);
		header.setAuditDetails(auditDetails);

		auditHeaderDAO.addAudit(header);
	}

	@Override
	public void validateMandate(AuditDetail auditDetail, FinanceDetail fd, Mandate mandate) {
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		if (InstrumentType.mandateRequired(fm.getFinRepayMethod()) && !fd.isActionSave() && mandate != null) {

			BigDecimal exposure = BigDecimal.ZERO;

			Date firstRepayDate = null;

			for (FinanceScheduleDetail schedule : schdData.getFinanceScheduleDetails()) {

				if (exposure.compareTo(schedule.getRepayAmount()) < 0) {
					exposure = schedule.getRepayAmount();
				}

				if ((schedule.isRepayOnSchDate() || schedule.isPftOnSchDate())
						&& !isHoliday(schedule.getBpiOrHoliday(), fm.getBpiTreatment())) {
					if (schedule.getSchDate().compareTo(fm.getFinStartDate()) > 0 && firstRepayDate == null) {
						firstRepayDate = schedule.getSchDate();
					}
				}
			}

			if (mandate.getMaxLimit() != null && mandate.getMaxLimit().compareTo(BigDecimal.ZERO) > 0) {
				if (mandate.isUseExisting()) {
					exposure = exposure.add(mandateDAO.getMaxRepayAmount(fm.getFinReference()));
				}

				if (mandate.getMaxLimit().compareTo(exposure) < 0) {
					auditDetail.setErrorDetail(ErrorUtil
							.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "90320", null, null), ""));
				}

			}

			if (mandate.getStartDate() != null && fm.getNextRepayDate() != null
					&& fm.getNextRepayDate().compareTo(mandate.getStartDate()) < 0) {
				String[] errParmFrq = new String[2];
				errParmFrq[0] = DateUtil.formatToShortDate(mandate.getStartDate());
				errParmFrq[1] = DateUtil.formatToShortDate(firstRepayDate);

				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("65020", errParmFrq)));

			}

			if (StringUtils.isNotBlank(mandate.getPeriodicity())) {

				if (!validatePayFrequency(fm.getRepayFrq().charAt(0), mandate.getPeriodicity().charAt(0))) {

					String[] errParmFrq = new String[2];
					errParmFrq[0] = PennantJavaUtil.getLabel("label_MandateDialog_Periodicity.value");
					errParmFrq[1] = PennantJavaUtil.getLabel("label_FinanceMainDialog_RepayFrq.value");

					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "90220", errParmFrq, null), ""));
				}

				if (fm.isFinRepayPftOnFrq()) {
					if (!validatePayFrequency(fm.getRepayPftFrq().charAt(0), mandate.getPeriodicity().charAt(0))) {

						String[] errParmFrq = new String[2];
						errParmFrq[0] = PennantJavaUtil.getLabel("label_MandateDialog_Periodicity.value");
						errParmFrq[1] = PennantJavaUtil.getLabel("label_FinanceMainDialog_RepayPftFrq.value");

						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "90220", errParmFrq, null), ""));

					}
				}
			}

			if (mandate.getExpiryDate() != null && mandate.getExpiryDate().before(fm.getMaturityDate())) {
				String[] errParmFrq = new String[2];
				errParmFrq[0] = PennantJavaUtil.getLabel("tab_label_MANDATE") + " "
						+ PennantJavaUtil.getLabel("label_MandateDialog_ExpiryDate.value");
				errParmFrq[1] = PennantJavaUtil.getLabel("label_MaturityDate");

				auditDetail.setErrorDetail(ErrorUtil
						.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "30509", errParmFrq, null), ""));
			}

			String barCode = mandate.getBarCodeNumber();
			if (StringUtils.isNotEmpty(barCode)
					&& !StringUtils.trimToEmpty(mandate.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)) {
				String[] errParm1 = new String[1];
				String[] valueParm1 = new String[1];
				valueParm1[0] = barCode;
				errParm1[0] = PennantJavaUtil.getLabel("label_BarCodeNumber") + " : " + valueParm1[0];

				char lastchar = barCode.charAt(barCode.length() - 1);
				int reminder = checkSum(barCode);

				MandateCheckDigit checkDigit = mandateCheckDigitDAO.getMandateCheckDigit(reminder, "");

				if (checkDigit != null) {
					if (checkDigit.getLookUpValue().charAt(0) != lastchar) {

						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "90405", errParm1, valueParm1), null));
					}
				} else {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "90405", errParm1, valueParm1), null));
				}
			}
		}

	}

	private boolean isHoliday(String bpiOrHoliday, String bpiTreatment) {
		if (StringUtils.equals(bpiOrHoliday, FinanceConstants.FLAG_HOLIDAY)
				|| StringUtils.equals(bpiOrHoliday, FinanceConstants.FLAG_POSTPONE)
				|| StringUtils.equals(bpiOrHoliday, FinanceConstants.FLAG_MORTEMIHOLIDAY)
				|| StringUtils.equals(bpiOrHoliday, FinanceConstants.FLAG_UNPLANNED)
				|| StringUtils.equals(bpiOrHoliday, FinanceConstants.FLAG_BPI)) {

			if (StringUtils.equals(bpiOrHoliday, FinanceConstants.FLAG_BPI)) {
				if (StringUtils.equals(bpiTreatment, FinanceConstants.BPI_DISBURSMENT)) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void promptMandate(AuditDetail auditDetail, FinanceDetail financeDetail) {
		Mandate mandate = financeDetail.getMandate();
		FinanceMain fm = financeDetail.getFinScheduleData().getFinanceMain();
		if (InstrumentType.mandateRequired(fm.getFinRepayMethod()) && !financeDetail.isActionSave()
				&& mandate != null) {
			if (!mandate.isUseExisting()) {
				// prompt for Open Mandate
				int count = getMnadateByCustID(mandate.getCustID(), mandate.getMandateID()).size();
				if (count != 0) {
					String[] errParmMan = new String[2];
					String[] valueParmMan = new String[2];
					valueParmMan[0] = String.valueOf(mandate.getCustCIF());
					valueParmMan[1] = String.valueOf(count);

					errParmMan[0] = " CustCIF : " + valueParmMan[0];
					errParmMan[1] = valueParmMan[1];

					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "65013", errParmMan, valueParmMan), ""));
				}

				if (mandate.getBankBranchID() != null && mandate.getBankBranchID() != 0) {
					BankBranch bankBranch = bankBranchService.getBankBranchById(mandate.getBankBranchID());

					String mandateType = StringUtils.trimToEmpty(mandate.getMandateType());
					// prompt for Auto Debit

					InstrumentType.isECS(mandateType);
					if ((InstrumentType.isECS(mandateType) || InstrumentType.isNACH(mandateType))
							&& bankBranch.isDda()) {

						String[] errParmBranch = new String[1];
						String[] valueParmBranch = new String[1];
						valueParmBranch[0] = bankBranch.getBankName();
						errParmBranch[0] = valueParmBranch[0];

						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "65016", errParmBranch, valueParmBranch),
								""));
					} else if (InstrumentType.isECS(mandateType) && bankBranch.isNach()) {

						String[] errParmBranch = new String[1];
						String[] valueParmBranch = new String[1];
						valueParmBranch[0] = bankBranch.getBankName();
						errParmBranch[0] = valueParmBranch[0];

						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "65017", errParmBranch, valueParmBranch),
								""));
					}
				}
			}
		}
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

	public AuditHeader getAuditHeader(AuditHeader auditHeader) {
		AuditHeader newauditHeader = new AuditHeader();
		newauditHeader.setAuditModule(ModuleUtil.getTableName(Mandate.class.getSimpleName()));
		newauditHeader.setAuditReference(auditHeader.getAuditReference());
		newauditHeader.setAuditUsrId(auditHeader.getAuditUsrId());
		newauditHeader.setAuditBranchCode(auditHeader.getAuditBranchCode());
		newauditHeader.setAuditDeptCode(auditHeader.getAuditDeptCode());
		newauditHeader.setAuditSystemIP(auditHeader.getAuditSystemIP());
		newauditHeader.setAuditSessionID(auditHeader.getAuditSessionID());
		newauditHeader.setUsrLanguage(auditHeader.getUsrLanguage());
		return newauditHeader;
	}

	private void getDocument(Mandate mandate) {
		DocumentDetails dd = new DocumentDetails();
		dd.setFinReference(mandate.getOrgReference());
		dd.setDocName(mandate.getDocumentName());
		dd.setCustId(mandate.getCustID());

		if (mandate.getDocumentRef() != null && mandate.getDocumentRef() != Long.MIN_VALUE
				&& mandate.getDocumentRef() != 0 && !mandate.isNewRecord()) {
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
			if (mandate.getDocImage() != null) {
				dd.setDocImage(mandate.getDocImage());
				saveDocument(DMSModule.FINANCE, DMSModule.MANDATE, dd);
				mandate.setDocumentRef(dd.getDocRefId());
			}
		}
	}

	public AuditDetail getAuditDetails(Mandate mandate, int auditSeq, String transType) {
		String[] fields = PennantJavaUtil.getFieldDetails(new Mandate(), new Mandate().getExcludeFields());
		return new AuditDetail(transType, auditSeq, fields[0], fields[1], mandate.getBefImage(), mandate);
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

	@Override
	public void autoSwaping(long custID) {
		logger.debug(Literal.ENTERING);
		Date appDate = SysParamUtil.getAppDate();
		List<Mandate> mandatesForAutoSwap = mandateDAO.getMandatesForAutoSwap(custID, appDate);

		for (Mandate mandate : mandatesForAutoSwap) {
			long mandateID = mandate.getMandateID();
			Long oldmandateID = mandate.getOldMandate();
			String finRepayMethod = mandate.getFinRepayMethod();

			FinanceDetail fd = new FinanceDetail();
			FinanceMain fm = financeMainDAO.getFinanceMainForLien(mandate.getFinID());

			fd.getFinScheduleData().setFinanceMain(fm);
			fd.getFinScheduleData().getFinanceMain().setBefImage(fm);

			fd.setMandate(mandate);

			boolean securityMandate = mandate.isSecurityMandate();
			if (securityMandate) {
				oldmandateID = mandate.getOldSecMandate();
			}

			if (oldmandateID == null || oldmandateID == mandateID) {
				continue;
			}

			String mandateType = mandate.getMandateType();

			if (InstrumentType.isPDC(finRepayMethod)) {
				boolean relisedAllCheques = chequeDetailDAO.isRelisedAllCheques(mandate.getFinID());

				if (relisedAllCheques) {
					financeMainDAO.loanMandateSwapping(mandate.getFinID(), mandateID, mandateType, "", securityMandate);
				}
			} else {
				financeMainDAO.loanMandateSwapping(mandate.getFinID(), mandateID, mandateType, "", securityMandate);
			}

			fd.setAppDate(appDate);
			if (ImplementationConstants.ALLOW_LIEN) {
				fd.setModuleDefiner(FinServiceEvent.RPYBASICMAINTAIN);
				if (InstrumentType.isSI(mandateType)) {
					lienService.save(fd, true);
				} else {
					lienService.update(fd);
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void autoSwapingFromPDC(long finID) {
		boolean relisedAllCheques = chequeDetailDAO.isRelisedAllCheques(finID);

		if (!relisedAllCheques) {
			return;
		}

		List<Mandate> mandatesForAutoSwap = mandateDAO.getMandatesForAutoSwap(finID);

		for (Mandate mandate : mandatesForAutoSwap) {
			long mandateID = mandate.getMandateID();
			Long oldmandateID = mandate.getOldMandate();
			String finRepayMethod = mandate.getFinRepayMethod();

			boolean securityMandate = mandate.isSecurityMandate();
			if (securityMandate) {
				oldmandateID = mandate.getOldSecMandate();
			}

			if (oldmandateID == null || oldmandateID == mandateID) {
				continue;
			}

			String mandateType = mandate.getMandateType();

			if (InstrumentType.isPDC(finRepayMethod)) {
				financeMainDAO.loanMandateSwapping(mandate.getFinID(), mandateID, mandateType, "", securityMandate);
			}
		}
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
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired(required = false)
	public void setBankBranchService(BankBranchService bankBranchService) {
		this.bankBranchService = bankBranchService;
	}

	@Autowired
	public void setMandateCheckDigitDAO(MandateCheckDigitDAO mandateCheckDigitDAO) {
		this.mandateCheckDigitDAO = mandateCheckDigitDAO;
	}

	@Autowired(required = false)
	public void setBankBranchDAO(BankBranchDAO bankBranchDAO) {
		this.bankBranchDAO = bankBranchDAO;
	}

	@Autowired
	public void setChequeDetailDAO(ChequeDetailDAO chequeDetailDAO) {
		this.chequeDetailDAO = chequeDetailDAO;
	}

	private MandateProcesses getMandateProcess() {
		return mandateProcesses == null ? defaultMandateProcess : mandateProcesses;
	}

	@Autowired
	public void setLienService(LienService lienService) {
		this.lienService = lienService;
	}
}