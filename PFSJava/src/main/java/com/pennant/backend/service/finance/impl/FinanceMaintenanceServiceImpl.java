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
 *
 * FileName : FinanceMaintenanceServiceImpl.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 30-07-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 *
 * 
 * 13-06-2018 Siva 0.2 Stage Accounting Modifications * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceWriteoffDAO;
import com.pennant.backend.dao.finance.GuarantorDetailDAO;
import com.pennant.backend.dao.finance.JointAccountDetailDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.dao.pdc.ChequeHeaderDAO;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.model.finance.FinCollaterals;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinWriteoffPayment;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.collateral.impl.FlagDetailValidation;
import com.pennant.backend.service.customermasters.impl.CustomerDataService;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.finance.FinanceMaintenanceService;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.service.finance.ISRADetailService;
import com.pennant.backend.service.finance.validation.FinGuarantorDetailValidation;
import com.pennant.backend.service.finance.validation.FinJointAccountDetailValidation;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.pff.accounting.model.PostingDTO;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennant.pff.mandate.InstrumentType;
import com.pennanttech.finance.tds.cerificate.model.TanAssignment;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.overdue.constants.ChargeType;
import com.rits.cloning.Cloner;

public class FinanceMaintenanceServiceImpl extends GenericFinanceDetailService implements FinanceMaintenanceService {
	private static final Logger logger = LogManager.getLogger(FinanceMaintenanceServiceImpl.class);

	private FinanceWriteoffDAO financeWriteoffDAO;
	private GuarantorDetailDAO guarantorDetailDAO;
	private JointAccountDetailDAO jointAccountDetailDAO;
	private FlagDetailValidation flagDetailValidation;
	private FinJointAccountDetailValidation finJointAccountDetailValidation;
	private FinGuarantorDetailValidation finGuarantorDetailValidation;
	private MandateDAO mandateDAO;
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	private ChequeHeaderDAO chequeHeaderDAO;
	private ISRADetailService israDetailService;
	private CustomerDataService customerDataService;
	private CollateralSetupService collateralSetupService;

	public FinanceMaintenanceServiceImpl() {
		super();
	}

	@Override
	public FinanceDetail getFinanceDetailById(long finID, String type, String userRole, String procEdtEvent,
			String eventCode) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = getFinanceDetail(finID, type);
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		schdData.setFinServiceInstructions(finServiceInstructionDAO.getFinServiceInstructions(finID, procEdtEvent));

		List<FinFeeDetail> feeList = schdData.getFinFeeDetailList();

		String finReference = fm.getFinReference();
		String finType = fm.getFinType();
		long custID = fm.getCustID();

		setDasAndDmaData(fm);

		List<Long> feeIDList = new ArrayList<>();
		for (FinFeeDetail fee : feeList) {
			String feeScheduleMethod = fee.getFeeScheduleMethod();
			if (CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT.equals(feeScheduleMethod)
					|| CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS.equals(feeScheduleMethod)
					|| CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR.equals(feeScheduleMethod)) {
				feeIDList.add(fee.getFeeID());
			}
		}

		List<FinFeeScheduleDetail> feeSchedules = null;
		if (!feeIDList.isEmpty()) {
			feeSchedules = finFeeScheduleDetailDAO.getFeeScheduleByFinID(feeIDList, false, "");

			Map<Long, List<FinFeeScheduleDetail>> schFeeMap = new HashMap<>();
			for (FinFeeScheduleDetail schdFee : feeSchedules) {
				List<FinFeeScheduleDetail> schList = new ArrayList<>();
				if (schFeeMap.containsKey(schdFee.getFeeID())) {
					schList = schFeeMap.get(schdFee.getFeeID());
					schFeeMap.remove(schdFee.getFeeID());
				}
				schList.add(schdFee);
				schFeeMap.put(schdFee.getFeeID(), schList);

			}

			for (FinFeeDetail fee : feeList) {
				if (schFeeMap.containsKey(fee.getFeeID())) {
					fee.setFinFeeScheduleDetailList(schFeeMap.get(fee.getFeeID()));
				}
			}
		}

		if (custID != 0 && custID != Long.MIN_VALUE) {
			fd.setCustomerDetails(customerDataService.getCustomerDetailsbyID(custID, true, "_View"));
		}

		checkListDetailService.setFinanceCheckListDetails(fd, finType, procEdtEvent, userRole);

		List<Long> accSetIdList = new ArrayList<Long>();
		accSetIdList.addAll(financeReferenceDetailDAO.getRefIdListByFinType(finType, procEdtEvent, null, "_ACView"));

		if (!accSetIdList.isEmpty()) {
			fd.setFeeCharges(transactionEntryDAO.getListFeeChargeRules(accSetIdList, eventCode, "_AView", 0));
		}

		// Finance Flag Details
		fd.setFinFlagsDetails(
				finFlagDetailsDAO.getFinFlagsByFinRef(finReference, FinanceConstants.MODULE_NAME, "_View"));

		// Finance Stage Accounting Posting Details
		// =======================================
		fd.setStageTransactionEntries(transactionEntryDAO.getListTransactionEntryByRefType(finType,
				StringUtils.isEmpty(procEdtEvent) ? FinServiceEvent.ORG : procEdtEvent,
				FinanceConstants.PROCEDT_STAGEACC, userRole, "_AEView", true));

		// Finance Joint Account Details
		fd.setJointAccountDetailList(jointAccountDetailService.getJoinAccountDetail(finID, "_View"));

		// Finance Guaranteer Details
		if (StringUtils.equals(procEdtEvent, FinServiceEvent.BASICMAINTAIN)) {
			fd.setGurantorsDetailList(guarantorDetailService.getGuarantorDetail(finID, "_View"));

			// Collateral Details
			if (ImplementationConstants.COLLATERAL_INTERNAL) {
				fd.setCollateralAssignmentList(collateralAssignmentDAO.getCollateralAssignmentByFinRef(finReference,
						FinanceConstants.MODULE_NAME, "_View"));
			} else {
				fd.setFinanceCollaterals(finCollateralService.getFinCollateralsByRef(finID, "_View"));
			}
		}

		// Mandate
		fd.setMandate(mandateDAO.getMandateById(fm.getMandateID(), ""));

		// ISRA Details
		fd.setIsraDetail(this.israDetailService.getIsraDetailsByRef(finReference, "_View"));

		// Finance Overdue Penalty Rate Details
		schdData.setFinODPenaltyRate(finODPenaltyRateDAO.getEffectivePenaltyRate(finID, type));

		fd.setDocumentDetailsList(documentDetailsDAO.getDocumentDetailsByRef(finReference, FinanceConstants.MODULE_NAME,
				procEdtEvent, "_View"));

		fd.setTanAssignments(tanAssignmentService.getTanDetailsByFinReference(fm.getCustID(), finReference));

		if (FinServiceEvent.WRITEOFFPAY.equals(procEdtEvent)) {
			if (StringUtils.isNotBlank(fm.getRecordType())) {
				fd.setFinwriteoffPayment(financeWriteoffDAO.getFinWriteoffPaymentById(finID, "_Temp"));
			}

			if (fd.getFinwriteoffPayment() == null) {
				FinWriteoffPayment finwriteoffPay = new FinWriteoffPayment();
				fd.setFinwriteoffPayment(finwriteoffPay);
			}

			fd.getFinwriteoffPayment().setWriteoffAmount(financeWriteoffDAO.getTotalFinWriteoffDetailAmt(finID));
			fd.getFinwriteoffPayment().setWriteoffDate(financeWriteoffDAO.getFinWriteoffDate(finID));
			fd.getFinwriteoffPayment().setWriteoffPaidAmount(financeWriteoffDAO.getTotalWriteoffPaymentAmount(finID));
		}

		logger.debug(Literal.LEAVING);
		return fd;
	}

	@Override
	public List<FinServiceInstruction> getFinServiceInstructions(long finID, String finEvent) {
		return finServiceInstructionDAO.getFinServiceInstructions(finID, "_Temp", finEvent);
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader) {
		logger.debug("Entering");

		FinanceDetail fd = (FinanceDetail) aAuditHeader.getAuditDetail().getModelData();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		List<FinServiceInstruction> serviceInstructions = getServiceInstructions(fd);

		Date appDate = SysParamUtil.getAppDate();
		long serviceUID = Long.MIN_VALUE;
		if (fd.getExtendedFieldRender() != null && fd.getExtendedFieldRender().getInstructionUID() != Long.MIN_VALUE) {
			serviceUID = fd.getExtendedFieldRender().getInstructionUID();
		}

		for (FinServiceInstruction fsi : serviceInstructions) {
			serviceUID = fsi.getInstructionUID();
			fm.setInstructionUID(serviceUID);
			if (ObjectUtils.isEmpty(fsi.getInitiatedDate())) {
				fsi.setInitiatedDate(appDate);
			}
		}

		aAuditHeader = businessValidation(aAuditHeader, "saveOrUpdate");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!aAuditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);

		fd = (FinanceDetail) auditHeader.getAuditDetail().getModelData();

		// Finance Stage Accounting Process
		// =======================================
		PostingDTO postingDTO = new PostingDTO();
		postingDTO.setFinanceMain(fm);
		postingDTO.setFinanceDetail(fd);
		postingDTO.setValueDate(SysParamUtil.getAppDate());
		postingDTO.setUserBranch(auditHeader.getAuditBranchCode());

		AccountingEngine.post(AccountingEvent.STAGE, postingDTO);
		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}

		TableType tableType = TableType.MAIN_TAB;
		if (fm.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		if (tableType == TableType.MAIN_TAB) {
			fm.setRcdMaintainSts("");
		}

		// Finance Penalty OD Rate Details
		FinODPenaltyRate penaltyRate = schdData.getFinODPenaltyRate();
		FinWriteoffPayment finWriteoffPay = fd.getFinwriteoffPayment();

		if (penaltyRate == null) {
			penaltyRate = new FinODPenaltyRate();
			penaltyRate.setApplyODPenalty(false);
			penaltyRate.setODIncGrcDays(false);
			penaltyRate.setODChargeType("");
			penaltyRate.setODChargeAmtOrPerc(BigDecimal.ZERO);
			penaltyRate.setODChargeCalOn("");
			penaltyRate.setODGraceDays(0);
			penaltyRate.setODAllowWaiver(false);
			penaltyRate.setODMaxWaiverPerc(BigDecimal.ZERO);
			penaltyRate.setODRuleCode("");
		}

		penaltyRate.setFinID(finID);
		penaltyRate.setFinReference(fm.getFinReference());
		penaltyRate.setFinEffectDate(SysParamUtil.getAppDate());

		// Finance Main Details Save And Update
		// =======================================
		if (fm.isNewRecord()) {
			financeMainDAO.save(fm, tableType, false);

			// Finance Penalty OD Rate Details
			finODPenaltyRateDAO.delete(penaltyRate.getFinID(), penaltyRate.getFinEffectDate(), tableType.getSuffix());
			finODPenaltyRateDAO.save(penaltyRate, tableType.getSuffix());

			if (finWriteoffPay != null) {
				finWriteoffPay.setFinReference(fm.getFinReference());
				long seqNo = financeWriteoffDAO.getfinWriteoffPaySeqNo(finID, "");
				finWriteoffPay.setSeqNo(seqNo + 1);
				financeWriteoffDAO.saveFinWriteoffPayment(finWriteoffPay, "_Temp");
			}
		} else {
			financeMainDAO.update(fm, tableType, false);

			// Finance Penalty OD Rate Details
			if (tableType == TableType.MAIN_TAB) {
				List<FinODPenaltyRate> list = finODPenaltyRateDAO.getFinODPenaltyRateByRef(penaltyRate.getFinID(), "");

				FinODPenaltyRate effectiveDue = null;
				FinODPenaltyRate oldFinODRate = null;
				boolean isExsist = false;

				for (FinODPenaltyRate penalRate : list) {
					if (ChargeType.PERC_ON_EFF_DUE_DAYS.equals(penalRate.getODChargeType())) {
						effectiveDue = penalRate;

						String newPenalityEffDate = DateUtil.formatToShortDate(penaltyRate.getFinEffectDate());
						String finEffectDate = DateUtil.formatToShortDate(effectiveDue.getFinEffectDate());

						if (effectiveDue != null && newPenalityEffDate.equals(finEffectDate)) {
							isExsist = true;
						}
					} else {
						oldFinODRate = penalRate;
					}
				}

				if (isExsist) {
					finODPenaltyRateDAO.saveLog(effectiveDue, "_Log");
					finODPenaltyRateDAO.update(penaltyRate, "");
				} else {
					if (!ChargeType.PERC_ON_EFF_DUE_DAYS.equals(penaltyRate.getODChargeType())) {
						finODPenaltyRateDAO.saveLog(oldFinODRate, "_Log");
						finODPenaltyRateDAO.delete(oldFinODRate.getFinID(), oldFinODRate.getFinEffectDate(), "");
						finODPenaltyRateDAO.delete(oldFinODRate.getFinID(), oldFinODRate.getFinEffectDate(), "_Temp");
					}
					finODPenaltyRateDAO.save(penaltyRate, "");
					finODPenaltyRateDAO.delete(oldFinODRate.getFinID(), oldFinODRate.getFinEffectDate(), "_Temp");
				}
			} else {
				finODPenaltyRateDAO.update(penaltyRate, tableType.getSuffix());
			}

			if (finWriteoffPay != null) {
				financeWriteoffDAO.updateFinWriteoffPayment(finWriteoffPay, "_Temp");
			}

		}

		// Save Fee Charges List
		// =======================================
		if (tableType == TableType.TEMP_TAB) {
			finFeeChargesDAO.deleteChargesBatch(finID, fd.getModuleDefiner(), false, tableType.getSuffix());
		}
		saveFeeChargeList(schdData, fd.getModuleDefiner(), false, tableType.getSuffix());

		// set Finance Check List audit details to auditDetails
		// =======================================
		if (fd.getFinanceCheckList() != null && !fd.getFinanceCheckList().isEmpty()) {
			auditDetails.addAll(checkListDetailService.saveOrUpdate(fd, tableType.getSuffix(), serviceUID));
		}

		// set Guarantor Details Audit
		// =======================================
		// String auditTranType = auditHeader.getAuditTranType();
		if (fd.getGurantorsDetailList() != null && !fd.getGurantorsDetailList().isEmpty()
				&& fd.getGurantorsDetailList().size() > 0) {
			fd.setGurantorsDetailList(fd.getGurantorsDetailList());
			List<AuditDetail> details = fd.getAuditDetailMap().get("GuarantorDetails");
			details = processingGuarantorDetailList(details, tableType.getSuffix(), finID, finReference);
			auditDetails.addAll(details);
		}

		// set JointAccount Details Audit
		// =======================================

		if (fd.getJointAccountDetailList() != null && !fd.getJointAccountDetailList().isEmpty()
				&& fd.getJointAccountDetailList().size() > 0) {
			fd.setJointAccountDetailList(fd.getJointAccountDetailList());
			List<AuditDetail> details = fd.getAuditDetailMap().get("JointAccountDetails");
			details = processingJointAccountDetailList(details, tableType.getSuffix(), finID, finReference);
			auditDetails.addAll(details);
		}

		// Save Document Details
		if (fd.getDocumentDetailsList() != null && fd.getDocumentDetailsList().size() > 0) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, tableType.getSuffix(), fm, fd.getModuleDefiner(),
					serviceUID);
			auditDetails.addAll(details);
		}

		// ISRA Details
		if (fd.getIsraDetail() != null) {
			auditDetails
					.addAll(israDetailService.saveOrUpdate(fd, tableType.getSuffix(), auditHeader.getAuditTranType()));
		}

		boolean isRegCol = false;
		if (ImplementationConstants.COLLATERAL_INTERNAL) {
			// set Finance Collateral Details Audit
			// =======================================
			if (fd.getCollateralAssignmentList() != null && !fd.getCollateralAssignmentList().isEmpty()) {
				List<AuditDetail> details = fd.getAuditDetailMap().get("CollateralAssignments");
				details = processingCollateralAssignmentList(details, tableType.getSuffix(), fm);
				for (CollateralAssignment ca : fd.getCollateralAssignmentList()) {
					int count = collateralAssignmentDAO.getAssignedCollateralCount(ca.getCollateralRef(), "_AView");
					Date regDate = collateralSetupService.getRegistrationDate(ca.getCollateralRef());
					if (PennantConstants.RECORD_TYPE_NEW.equals(ca.getRecordType())) {
						if (count > 1 && regDate != null) {
							isRegCol = true;
						}
					}
					if (isRegCol) {
						CollateralAssignment collass = collateralSetupService.getCollDetails(ca.getCollateralRef());
						collateralSetupService.updateCersaiDetails(ca.getCollateralRef(), collass.getSiid(),
								collass.getAssetid());
					}
				}
				auditDetails.addAll(details);
			}
		} else {
			// =======================================

			if (fd.getFinanceCollaterals() != null && !fd.getFinanceCollaterals().isEmpty()) {
				fd.setFinanceCollaterals(fd.getFinanceCollaterals());
				List<AuditDetail> details = fd.getAuditDetailMap().get("FinCollateral");
				details = processingFinCollateralDetailList(details, tableType.getSuffix(), finReference);
				auditDetails.addAll(details);
			}
		}
		// FinFlag Details
		// =======================================
		if (fd.getFinFlagsDetails() != null && fd.getFinFlagsDetails().size() > 0) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("FinFlagsDetail");
			details = processingFinFlagDetailList(details, fd, tableType.getSuffix());
			auditDetails.addAll(details);
		}

		FinScheduleData finScheduleData = fd.getFinScheduleData();
		FinanceMain aFinanceMain = finScheduleData.getFinanceMain();
		if (CollectionUtils.isNotEmpty(finScheduleData.getFinServiceInstructions()) && aFinanceMain.isNewRecord()) {
			finServiceInstructionDAO.saveList(finScheduleData.getFinServiceInstructions(), tableType.getSuffix());
		}

		// Extended field Details
		if (fd.getExtendedFieldRender() != null) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("ExtendedFieldDetails");
			details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
					ExtendedFieldConstants.MODULE_LOAN, fd.getExtendedFieldHeader().getEvent(), tableType.getSuffix(),
					serviceUID);
			auditDetails.addAll(details);
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), fm.getExcludeFields());
		auditHeader.setAuditDetail(
				new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], fm.getBefImage(), fm));

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public boolean isFinActive(long finID) {
		return financeMainDAO.isFinActive(finID);
	}

	private List<AuditDetail> setGuarantorDetailAuditData(FinanceDetail financeDetail, String auditTranType,
			String method) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		for (int i = 0; i < financeDetail.getGurantorsDetailList().size(); i++) {
			GuarantorDetail finGuarantorDetailList = financeDetail.getGurantorsDetailList().get(i);

			if (StringUtils.isEmpty(finGuarantorDetailList.getRecordType())) {
				continue;
			}

			finGuarantorDetailList.setWorkflowId(financeMain.getWorkflowId());

			boolean isRcdType = false;

			if (finGuarantorDetailList.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finGuarantorDetailList.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (finGuarantorDetailList.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finGuarantorDetailList.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (financeMain.isWorkflow()) {
					isRcdType = true;
				}
			} else if (finGuarantorDetailList.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				finGuarantorDetailList.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				finGuarantorDetailList.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finGuarantorDetailList.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finGuarantorDetailList.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| finGuarantorDetailList.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			finGuarantorDetailList.setRecordStatus(financeMain.getRecordStatus());
			finGuarantorDetailList.setLastMntOn(financeMain.getLastMntOn());

			GuarantorDetail guarantorDetail = new GuarantorDetail();
			String[] fields = PennantJavaUtil.getFieldDetails(guarantorDetail, guarantorDetail.getExcludeFields());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
					finGuarantorDetailList.getBefImage(), finGuarantorDetailList));
		}

		return auditDetails;
	}

	private List<AuditDetail> setJointAccountDetailAuditData(FinanceDetail financeDetail, String auditTranType,
			String method) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		for (int i = 0; i < financeDetail.getJointAccountDetailList().size(); i++) {
			JointAccountDetail jointAccount = financeDetail.getJointAccountDetailList().get(i);

			if (StringUtils.isEmpty(jointAccount.getRecordType())) {
				continue;
			}

			jointAccount.setWorkflowId(financeMain.getWorkflowId());

			boolean isRcdType = false;

			if (jointAccount.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				jointAccount.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (jointAccount.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				jointAccount.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (financeMain.isWorkflow()) {
					isRcdType = true;
				}
			} else if (jointAccount.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				jointAccount.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				jointAccount.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (jointAccount.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (jointAccount.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| jointAccount.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			jointAccount.setRecordStatus(financeMain.getRecordStatus());
			jointAccount.setLastMntOn(financeMain.getLastMntOn());
			JointAccountDetail jointAccountDetail = new JointAccountDetail();
			String[] fields = PennantJavaUtil.getFieldDetails(jointAccountDetail,
					jointAccountDetail.getExcludeFields());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], jointAccount.getBefImage(),
					jointAccount));
		}

		return auditDetails;
	}

	/*
	 * FinCollteralDetail Audit Map
	 */
	private List<AuditDetail> setFinCollateralDetailAuditData(FinanceDetail financeDetail, String auditTranType,
			String method) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinCollaterals collateralDetail = new FinCollaterals();
		String[] fields = PennantJavaUtil.getFieldDetails(collateralDetail, collateralDetail.getExcludeFields());

		for (int i = 0; i < financeDetail.getFinanceCollaterals().size(); i++) {
			FinCollaterals finCollateralList = financeDetail.getFinanceCollaterals().get(i);

			if (StringUtils.isEmpty(finCollateralList.getRecordType())) {
				continue;
			}

			finCollateralList.setWorkflowId(collateralDetail.getWorkflowId());

			boolean isRcdType = false;

			if (finCollateralList.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finCollateralList.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (finCollateralList.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finCollateralList.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (collateralDetail.isWorkflow()) {
					isRcdType = true;
				}
			} else if (finCollateralList.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				finCollateralList.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				finCollateralList.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finCollateralList.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finCollateralList.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| finCollateralList.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			finCollateralList.setRecordStatus(collateralDetail.getRecordStatus());
			finCollateralList.setLastMntOn(collateralDetail.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
					finCollateralList.getBefImage(), finCollateralList));
		}

		return auditDetails;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		List<AuditDetail> auditDetailList = new ArrayList<>();

		FinanceDetail fd = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		List<FinServiceInstruction> serviceInstructions = getServiceInstructions(fd);
		long serviceUID = Long.MIN_VALUE;

		for (FinServiceInstruction finServInst : serviceInstructions) {
			serviceUID = finServInst.getInstructionUID();
			fm.setInstructionUID(serviceUID);
			if (ObjectUtils.isEmpty(finServInst.getInitiatedDate())) {
				finServInst.setInitiatedDate(SysParamUtil.getAppDate());
			}
		}

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tranType = PennantConstants.TRAN_DEL;

		// Cancel All Transactions done by Finance Reference
		// =======================================

		AccountingEngine.cancelStageAccounting(finID, fd.getModuleDefiner());

		// Fee charges deletion
		finFeeChargesDAO.deleteChargesBatch(finID, fd.getModuleDefiner(), false, "_Temp");

		// Checklist Details delete
		// =======================================
		auditDetailList.addAll(checkListDetailService.delete(fd, "_Temp", tranType));

		// Save Document Details
		if (fd.getDocumentDetailsList() != null && fd.getDocumentDetailsList().size() > 0) {
			for (DocumentDetails docDetails : fd.getDocumentDetailsList()) {
				docDetails.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
			List<AuditDetail> details = fd.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, "_Temp", fm, fd.getModuleDefiner(), serviceUID);
			auditDetailList.addAll(details);
		}

		// ISRA Details
		if (fd.getIsraDetail() != null) {
			auditDetailList.addAll(israDetailService.doReject(fd, "_Temp", auditHeader.getAuditTranType()));
		}
		// set Guarantor Details Audit
		// =======================================
		if (fd.getGurantorsDetailList() != null && !fd.getGurantorsDetailList().isEmpty()) {
			for (GuarantorDetail guarantorDetails : fd.getGurantorsDetailList()) {
				guarantorDetails.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
			List<AuditDetail> details = fd.getAuditDetailMap().get("GuarantorDetails");
			details = processingGuarantorDetailList(details, "_Temp", finID, finReference);
			auditDetailList.addAll(details);
		}

		// set JointAccount Details Audit
		// =======================================
		if (fd.getJointAccountDetailList() != null && !fd.getJointAccountDetailList().isEmpty()) {
			for (JointAccountDetail jointAccountDetail : fd.getJointAccountDetailList()) {
				jointAccountDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
			List<AuditDetail> details = fd.getAuditDetailMap().get("JointAccountDetails");
			details = processingJointAccountDetailList(details, "_Temp", finID, finReference);
			auditDetailList.addAll(details);
		}

		// Finance Flag Details
		if (fd.getFinFlagsDetails() != null && !fd.getFinFlagsDetails().isEmpty()) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("FinFlagsDetail");
			for (int i = 0; i < details.size(); i++) {
				FinFlagsDetail finFlagsDetail = (FinFlagsDetail) details.get(i).getModelData();
				finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}

			finFlagDetailsDAO.deleteList(finReference, FinanceConstants.MODULE_NAME, "_Temp");
			auditDetailList.addAll(details);

		}

		// Collateral assignment Details
		if (fd.getCollateralAssignmentList() != null && !fd.getCollateralAssignmentList().isEmpty()) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("CollateralAssignments");
			for (int i = 0; i < details.size(); i++) {
				CollateralAssignment assignment = (CollateralAssignment) details.get(i).getModelData();
				assignment.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
			details = processingCollateralAssignmentList(details, "_Temp", schdData.getFinanceMain());
			auditDetailList.addAll(details);
		}

		if (fd.getFinanceCollaterals() != null && !fd.getFinanceCollaterals().isEmpty()) {
			fd.setFinanceCollaterals(fd.getFinanceCollaterals());
			List<AuditDetail> details = fd.getAuditDetailMap().get("FinCollateral");
			details = processingFinCollateralDetailList(details, "_Temp", finReference);
			auditDetailList.addAll(details);
		}

		finServiceInstructionDAO.deleteList(finID, fd.getModuleDefiner(), "_Temp");

		// Extended field Render Details.
		List<AuditDetail> extendedDetails = fd.getAuditDetailMap().get("ExtendedFieldDetails");
		if (extendedDetails != null && extendedDetails.size() > 0) {
			auditDetailList.addAll(extendedFieldDetailsService.delete(fd.getExtendedFieldHeader(),
					fd.getExtendedFieldRender().getReference(), fd.getExtendedFieldRender().getSeqNo(), "_Temp",
					auditHeader.getAuditTranType(), extendedDetails));
		}

		finODPenaltyRateDAO.delete(finID, null, "_Temp");

		if (fd.getFinwriteoffPayment() != null) {
			financeWriteoffDAO.deletefinWriteoffPayment(finID, fd.getFinwriteoffPayment().getSeqNo(), "_Temp");
		}
		financeMainDAO.delete(fm, TableType.TEMP_TAB, false, false);
		// Process Tan Assignment -doReject
		List<TanAssignment> tanAssignments = fd.getTanAssignments();
		if (CollectionUtils.isNotEmpty(tanAssignments)) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("TanAssignments");
			tanAssignmentService.delete(tanAssignments, TableType.TEMP_TAB);
			if (details != null) {
				auditDetailList.addAll(details);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), fm.getExcludeFields());
		auditHeader.setAuditDetail(
				new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], fm.getBefImage(), fm));
		auditHeader.setAuditDetails(auditDetailList);
		auditHeaderDAO.addAudit(auditHeader);

		// Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(fd);

		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader) {
		logger.debug("Entering");

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		aAuditHeader = businessValidation(aAuditHeader, "doApprove");
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		FinanceDetail fd = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		Date appDate = SysParamUtil.getAppDate();

		// Finance Stage Accounting Process
		// =======================================
		PostingDTO postingDTO = new PostingDTO();
		postingDTO.setFinanceMain(fm);
		postingDTO.setFinanceDetail(fd);
		postingDTO.setValueDate(appDate);
		postingDTO.setUserBranch(auditHeader.getAuditBranchCode());

		AccountingEngine.post(AccountingEvent.STAGE, postingDTO);
		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}

		// Finance Write off Posting Process Execution
		// =====================================

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		FinanceProfitDetail profitDetail = profitDetailsDAO.getFinProfitDetailsById(finID);
		List<FinServiceInstruction> serviceInstructions = getServiceInstructions(fd);

		long serviceUID = Long.MIN_VALUE;
		if (fd.getExtendedFieldRender() != null && fd.getExtendedFieldRender().getInstructionUID() != Long.MIN_VALUE) {
			serviceUID = fd.getExtendedFieldRender().getInstructionUID();
		}

		for (FinServiceInstruction fsi : serviceInstructions) {
			serviceUID = fsi.getInstructionUID();
			fm.setInstructionUID(serviceUID);
			if (ObjectUtils.isEmpty(fsi.getInitiatedDate())) {
				fsi.setInitiatedDate(appDate);
			}
		}
		String accEventCode = "";
		if (StringUtils.equals(fd.getModuleDefiner(), FinServiceEvent.WRITEOFFPAY)) {
			accEventCode = AccountingEvent.WRITEBK;
		} else if (StringUtils.equals(fd.getModuleDefiner(), FinServiceEvent.BASICMAINTAIN)) {
			accEventCode = AccountingEvent.AMENDMENT;
		} else if (StringUtils.equals(fd.getModuleDefiner(), FinServiceEvent.RPYBASICMAINTAIN)) {
			accEventCode = AccountingEvent.SEGMENT;
		}

		long linkedTranId = 0;
		long accountsetID = AccountingConfigCache.getAccountSetID(fm.getFinType(), accEventCode,
				FinanceConstants.MODULEID_FINTYPE);

		if (accountsetID > 0) {
			AEEvent aeEvent = AEAmounts.procAEAmounts(fm, schdData.getFinanceScheduleDetails(), profitDetail,
					accEventCode, appDate, fm.getMaturityDate());
			AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

			// FinanceWriteoffPayment set the writeoffPayAmount,WriteoffPayAccount
			if (fd.getFinwriteoffPayment() != null) {
				amountCodes.setWoPayAmt(fd.getFinwriteoffPayment().getWriteoffPayAmount());
			}

			Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();
			aeEvent.setDataMap(dataMap);
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(aeEvent.getFinType(),
					aeEvent.getAccountingEvent(), FinanceConstants.MODULEID_FINTYPE));
			aeEvent = postingsPreparationUtil.postAccounting(aeEvent);

			if (!aeEvent.isPostingSucess()) {
				String errParm = aeEvent.getErrorMessage();
				throw new InterfaceException("9999", errParm);
			}

			linkedTranId = aeEvent.getLinkedTranId();
		}

		fm.setRcdMaintainSts("");
		fm.setRoleCode("");
		fm.setNextRoleCode("");
		fm.setTaskId("");
		fm.setNextTaskId("");
		fm.setWorkflowId(0);

		tranType = PennantConstants.TRAN_UPD;
		fm.setRecordType("");
		financeMainDAO.update(fm, TableType.MAIN_TAB, false);

		// Save Finance WriteOffPayment Details
		FinWriteoffPayment financeWriteoffPayment = fd.getFinwriteoffPayment();
		if (fd.getFinwriteoffPayment() != null) {
			financeWriteoffPayment.setLinkedTranId(linkedTranId);
			financeWriteoffDAO.saveFinWriteoffPayment(financeWriteoffPayment, "");
		}

		// Finance Penalty OD Rate Details
		FinODPenaltyRate penaltyRate = schdData.getFinODPenaltyRate();
		if (penaltyRate == null) {
			penaltyRate = new FinODPenaltyRate();
			penaltyRate.setApplyODPenalty(false);
			penaltyRate.setODIncGrcDays(false);
			penaltyRate.setODChargeType("");
			penaltyRate.setODChargeAmtOrPerc(BigDecimal.ZERO);
			penaltyRate.setODChargeCalOn("");
			penaltyRate.setODGraceDays(0);
			penaltyRate.setODAllowWaiver(false);
			penaltyRate.setODMaxWaiverPerc(BigDecimal.ZERO);
		}

		penaltyRate.setFinID(finID);
		penaltyRate.setFinReference(finReference);
		penaltyRate.setFinEffectDate(SysParamUtil.getAppDate());

		List<FinODPenaltyRate> list = finODPenaltyRateDAO.getFinODPenaltyRateByRef(penaltyRate.getFinID(), "");

		FinODPenaltyRate effectiveDue = null;
		FinODPenaltyRate oldFinODRate = null;
		boolean isExsist = false;

		for (FinODPenaltyRate penalRate : list) {
			effectiveDue = penalRate;

			String newPenalityEffDate = DateUtil.formatToShortDate(penaltyRate.getFinEffectDate());
			String finEffectDate = DateUtil.formatToShortDate(effectiveDue.getFinEffectDate());

			if (effectiveDue != null && newPenalityEffDate.equals(finEffectDate)) {
				isExsist = true;
				break;
			} else {
				oldFinODRate = penalRate;
			}
		}

		if (isExsist) {
			finODPenaltyRateDAO.saveLog(effectiveDue, "_Log");
			finODPenaltyRateDAO.delete(penaltyRate.getFinID(), penaltyRate.getFinEffectDate(), "_Temp");
			finODPenaltyRateDAO.update(penaltyRate, "");
		} else {
			if (!ChargeType.PERC_ON_EFF_DUE_DAYS.equals(penaltyRate.getODChargeType())) {
				finODPenaltyRateDAO.saveLog(oldFinODRate, "_Log");
				finODPenaltyRateDAO.delete(oldFinODRate.getFinID(), oldFinODRate.getFinEffectDate(), "");
				finODPenaltyRateDAO.delete(oldFinODRate.getFinID(), oldFinODRate.getFinEffectDate(), "_Temp");
			}
			finODPenaltyRateDAO.delete(penaltyRate.getFinID(), penaltyRate.getFinEffectDate(), "_Temp");
			finODPenaltyRateDAO.save(penaltyRate, "");
		}

		// Save Document Details
		if (fd.getDocumentDetailsList() != null && fd.getDocumentDetailsList().size() > 0) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, "", fm, fd.getModuleDefiner(), serviceUID);
			auditDetails.addAll(details);
			listDocDeletion(fd, "_Temp");
		}

		// Fee Charge Details
		// =======================================
		saveFeeChargeList(schdData, fd.getModuleDefiner(), false, "");

		// ISRA Details
		if (fd.getIsraDetail() != null) {
			auditDetails.addAll(israDetailService.doApprove(fd, "", tranType));
		}

		// set Check list details Audit
		// =======================================
		if (fd.getFinanceCheckList() != null && !fd.getFinanceCheckList().isEmpty()) {
			auditDetails.addAll(checkListDetailService.doApprove(fd, "", serviceUID));
		}

		// Guarantor Details
		if (fd.getGurantorsDetailList() != null && !fd.getGurantorsDetailList().isEmpty()) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("GuarantorDetails");
			details = processingGuarantorDetailList(details, "", finID, finReference);
			auditDetails.addAll(details);

		}

		// JointAccount Details
		if (fd.getJointAccountDetailList() != null && !fd.getJointAccountDetailList().isEmpty()) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("JointAccountDetails");
			details = processingJointAccountDetailList(details, "", finID, finReference);
			auditDetails.addAll(details);

		}

		// Fin Flag Details
		if (fd.getFinFlagsDetails() != null && fd.getFinFlagsDetails().size() > 0) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("FinFlagsDetail");
			details = processingFinFlagDetailList(details, fd, "");
			auditDetails.addAll(details);
		}

		if (ImplementationConstants.COLLATERAL_INTERNAL) {
			// Collateral Assignments Details
			// =======================================
			if (fd.getCollateralAssignmentList() != null && !fd.getCollateralAssignmentList().isEmpty()) {
				List<AuditDetail> details = fd.getAuditDetailMap().get("CollateralAssignments");
				details = processingCollateralAssignmentList(details, "", fm);
				auditDetails.addAll(details);
			}
		} else {
			// set Finance Collateral Details Audit
			if (fd.getFinanceCollaterals() != null && !fd.getFinanceCollaterals().isEmpty()) {
				fd.setFinanceCollaterals(fd.getFinanceCollaterals());
				List<AuditDetail> details = fd.getAuditDetailMap().get("FinCollateral");
				details = processingFinCollateralDetailList(details, "", finReference);
				auditDetails.addAll(details);
			}
		}

		FinScheduleData finScheduleData = fd.getFinScheduleData();

		if (CollectionUtils.isNotEmpty(finScheduleData.getFinServiceInstructions())) {
			List<FinServiceInstruction> oldList = finServiceInstructionDAO.getFinServiceInstructions(finID, "",
					FinServiceEvent.BASICMAINTAIN);

			List<FinServiceInstruction> newList = new ArrayList<>();
			if (CollectionUtils.isNotEmpty(oldList)) {
				for (FinServiceInstruction newFr : finScheduleData.getFinServiceInstructions()) {
					boolean isOld = false;
					for (FinServiceInstruction oldFr : oldList) {
						if (oldFr.getServiceSeqId() == newFr.getServiceSeqId()) {
							isOld = true;
							break;
						}
					}
					if (!isOld) {
						newList.add(newFr);
					}
				}
			} else {
				newList.addAll(finScheduleData.getFinServiceInstructions());
			}
			if (CollectionUtils.isNotEmpty(newList)) {
				finServiceInstructionDAO.saveList(newList, "");
			}

		}

		// Extended field Details
		if (fd.getExtendedFieldRender() != null) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("ExtendedFieldDetails");
			details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
					ExtendedFieldConstants.MODULE_LOAN, fd.getExtendedFieldHeader().getEvent(), "", serviceUID);
			auditDetails.addAll(details);
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), fm.getExcludeFields());
		List<AuditDetail> auditDetailList = new ArrayList<AuditDetail>();

		// finODPenaltyRateDAO.delete(finID, "_Temp");

		if (fd.getFinwriteoffPayment() != null) {
			financeWriteoffDAO.deletefinWriteoffPayment(finID, fd.getFinwriteoffPayment().getSeqNo(), "_Temp");
		}

		// Guarantor Details
		if (fd.getGurantorsDetailList() != null && !fd.getGurantorsDetailList().isEmpty()) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("GuarantorDetails");
			List<GuarantorDetail> guarantorDetail = new ArrayList<GuarantorDetail>();
			for (int i = 0; i < details.size(); i++) {
				GuarantorDetail guarantor = (GuarantorDetail) details.get(i).getModelData();
				guarantorDetail.add(guarantor);
			}
			auditDetailList.addAll(guarantorDetailService.delete(guarantorDetail, "_Temp", PennantConstants.TRAN_WF));
		}
		// JointAccount Details
		if (fd.getJointAccountDetailList() != null && !fd.getJointAccountDetailList().isEmpty()) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("JointAccountDetails");
			List<JointAccountDetail> jointAccountDetail = new ArrayList<JointAccountDetail>();
			for (int i = 0; i < details.size(); i++) {
				JointAccountDetail jointAcctDetail = (JointAccountDetail) details.get(i).getModelData();
				jointAccountDetail.add(jointAcctDetail);
			}
			auditDetailList
					.addAll(jointAccountDetailService.delete(jointAccountDetail, "_Temp", PennantConstants.TRAN_WF));
		}

		// Checklist Details delete
		// =======================================
		auditDetailList.addAll(checkListDetailService.delete(fd, "_Temp", tranType));

		// Fee Charge Details Clearing before
		// =======================================
		finFeeChargesDAO.deleteChargesBatch(finID, fd.getModuleDefiner(), false, "_Temp");

		// Finance Flag Details
		if (fd.getFinFlagsDetails() != null && !fd.getFinFlagsDetails().isEmpty()) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("FinFlagsDetail");
			for (int i = 0; i < details.size(); i++) {
				FinFlagsDetail finFlagsDetail = (FinFlagsDetail) details.get(i).getModelData();
				finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
			finFlagDetailsDAO.deleteList(fm.getFinReference(), FinanceConstants.MODULE_NAME, "_Temp");
			auditDetailList.addAll(details);

		}

		// FinanceMain Details Clearing before
		// =======================================
		if (!RequestSource.UPLOAD.equals(penaltyRate.getRequestSource())) {
			financeMainDAO.delete(fm, TableType.TEMP_TAB, false, true);
		}

		// Collateral assignment Details
		if (fd.getCollateralAssignmentList() != null && !fd.getCollateralAssignmentList().isEmpty()) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("CollateralAssignments");
			auditDetailList.addAll(details);
			collateralAssignmentDAO.deleteByReference(fm.getFinReference(), "_Temp");
		}

		if (fd.getFinanceCollaterals() != null) {
			auditDetailList.addAll(
					finCollateralService.delete(fd.getFinanceCollaterals(), "_Temp", auditHeader.getAuditTranType()));
		}

		finServiceInstructionDAO.deleteList(finID, fd.getModuleDefiner(), "_Temp");

		// Delete Tan Assignment on doApprove in temp
		if (CollectionUtils.isNotEmpty(fd.getTanAssignments())) {
			tanAssignmentService.delete(fd.getTanAssignments(), TableType.TEMP_TAB);
		}

		// Cheque detail maintaince Inserting the entry in the cheque details
		// if the repayment mode changed to pdc
		if (fm.getBefImage() != null) {
			if (InstrumentType.isPDC(fm.getFinRepayMethod())) {
				if (!fm.getFinRepayMethod().equals(fm.getBefImage().getFinRepayMethod())) {
					if (!chequeHeaderDAO.isChequeDetilsExists(finID)) {
						ChequeHeader chequeHeader = new ChequeHeader();
						chequeHeader.setRoleCode("");
						chequeHeader.setNextRoleCode("");
						chequeHeader.setTaskId("");
						chequeHeader.setNextTaskId("");
						chequeHeader.setVersion(1);
						chequeHeader.setLastMntBy(fm.getLastMntBy());
						chequeHeader.setLastMntOn(fm.getLastMntOn());
						chequeHeader.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						chequeHeader.setRecordType("");
						chequeHeader.setWorkflowId(0);
						chequeHeader.setFinID(fm.getFinID());
						chequeHeader.setFinReference(fm.getFinReference());
						chequeHeader.setNoOfCheques(0);
						chequeHeader.setTotalAmount(BigDecimal.ZERO);
						chequeHeader.setActive(true);
						chequeHeaderDAO.save(chequeHeader, TableType.MAIN_TAB);
					}
				}
			}
		}

		FinanceDetail tempFd = (FinanceDetail) aAuditHeader.getAuditDetail().getModelData();
		FinanceMain tempFm = tempFd.getFinScheduleData().getFinanceMain();
		auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1],
				tempFm.getBefImage(), tempFm));

		// Adding audit as deleted from TEMP table
		auditHeader.setAuditDetails(auditDetailList);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(
				new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], fm.getBefImage(), fm));

		// Adding audit as Insert/Update/deleted into main table
		auditHeader.setAuditDetails(getListAuditDetails(auditDetails));
		auditHeaderDAO.addAudit(auditHeader);

		// Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(fd);
		finStageAccountingLogDAO.update(finID, fd.getModuleDefiner(), false);

		// Extended field Render Details.
		List<AuditDetail> extendedDetails = fd.getAuditDetailMap().get("ExtendedFieldDetails");
		if (extendedDetails != null && extendedDetails.size() > 0) {
			extendedFieldDetailsService.delete(fd.getExtendedFieldHeader(), fd.getExtendedFieldRender().getReference(),
					fd.getExtendedFieldRender().getSeqNo(), "_Temp", auditHeader.getAuditTranType(), extendedDetails);
		}

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from financeMainDAO.getErrorDetail with Error ID and language
	 * as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String usrLanguage = financeMain.getUserDetails().getLanguage();

		// Collateral Assignments details
		// =======================================
		if (financeDetail.getCollateralAssignmentList() != null
				&& !financeDetail.getCollateralAssignmentList().isEmpty()) {

			// CoOwnerDetails Validation
			List<CollateralAssignment> assignments = financeDetail.getCollateralAssignmentList();
			if (assignments != null && !assignments.isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("CollateralAssignments");
				details = getCollateralAssignmentValidation().vaildateDetails(details, method, usrLanguage);
				auditDetails.addAll(details);
			}
		}
		// Finance Flag details Validation
		List<FinFlagsDetail> finFlagsDetailList = financeDetail.getFinFlagsDetails();
		if (finFlagsDetailList != null && !finFlagsDetailList.isEmpty()) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinFlagsDetail");
			details = getFlagDetailValidation().vaildateDetails(details, method, usrLanguage);
			auditDetails.addAll(details);
		}
		// Joint Account Details Validation
		List<JointAccountDetail> finJoinAccDetailList = financeDetail.getJointAccountDetailList();
		if (finJoinAccDetailList != null && !finJoinAccDetailList.isEmpty()) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("JointAccountDetails");
			details = getFinJointAccountDetailValidation().jointAccountDetailsListValidation(details, method,
					usrLanguage);
			auditDetails.addAll(details);
		}
		// Guarantor Account Details Validation
		List<GuarantorDetail> guarantorAccDetailList = financeDetail.getGurantorsDetailList();
		if (guarantorAccDetailList != null && !guarantorAccDetailList.isEmpty()) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("GuarantorDetails");
			details = getFinGuarantorDetailValidation().gurantorDetailsListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		// Extended field details Validation
		if (financeDetail.getExtendedFieldRender() != null) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("ExtendedFieldDetails");
			ExtendedFieldHeader extHeader = financeDetail.getExtendedFieldHeader();
			details = extendedFieldDetailsService.validateExtendedDdetails(extHeader, details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		// TAN Assignments Validation
		if (financeMain.isTDSApplicable() && financeDetail.getTanAssignments() != null) {
			List<AuditDetail> details = tanAssignmentService.validate(financeDetail, financeMain.getWorkflowId(),
					method, auditHeader.getAuditTranType(), usrLanguage);
			financeDetail.getAuditDetailMap().put("TanAssignments", details);
			auditDetails.addAll(details);
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinanceDetail fd = (FinanceDetail) auditDetail.getModelData();
		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

		long finID = fm.getFinID();

		int format = CurrencyUtil.getFormat(fm.getFinCcy());

		FinanceMain tempFinanceMain = null;
		if (fm.isWorkflow()) {
			tempFinanceMain = financeMainDAO.getFinanceMainById(finID, "_Temp", false);
		}
		FinanceMain befFinanceMain = financeMainDAO.getFinanceMainById(finID, "", false);
		FinanceMain oldFinanceMain = fm.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = fm.getFinReference();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (fm.isNewRecord()) { // for New record or new record into work flow

			if (!fm.isWorkflow()) {// With out Work flow only new
				// records
				if (befFinanceMain != null) { // Record Already Exists in the
					// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (fm.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
					// records type is new
					if (befFinanceMain != null || tempFinanceMain != null) { // if
						// records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befFinanceMain == null || tempFinanceMain != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!fm.isWorkflow()) { // With out Work flow for update
				// and delete

				if (befFinanceMain == null) { // if records not exists in the
					// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldFinanceMain != null
							&& !oldFinanceMain.getLastMntOn().equals(befFinanceMain.getLastMntOn())) {
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

				if (tempFinanceMain == null) { // if records not exists in the
					// Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempFinanceMain != null && oldFinanceMain != null
						&& !oldFinanceMain.getLastMntOn().equals(tempFinanceMain.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		// Checking , if Customer is in EOD process or not. if Yes, not allowed to do an action
		int eodProgressCount = customerQueuingDAO.getProgressCountByCust(fm.getCustID());

		// If Customer Exists in EOD Processing, Not allowed to Maintenance till completion
		if (eodProgressCount > 0) {
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "60203", errParm, valueParm), usrLanguage));
		}

		FinWriteoffPayment finWriteoffPay = fd.getFinwriteoffPayment();
		if (finWriteoffPay != null) {
			// Save Finance WriteOff Details
			BigDecimal financeWriteoffDetailAmt = financeWriteoffDAO.getTotalFinWriteoffDetailAmt(finID);
			BigDecimal finWriteoffPayAmt = financeWriteoffDAO.getTotalWriteoffPaymentAmount(finID);
			BigDecimal finWriteoffTotAmt = financeWriteoffDetailAmt.subtract(finWriteoffPayAmt);

			if (finWriteoffPay.getWriteoffPayAmount().compareTo(finWriteoffTotAmt) > 0) {
				String[] errParm1 = new String[2];
				String[] valueParm1 = new String[2];
				valueParm1[0] = PennantApplicationUtil.amountFormate(finWriteoffPay.getWriteoffPayAmount(), format)
						.toString();
				errParm1[0] = PennantJavaUtil.getLabel("label_WriteoffPayAmount") + ":" + valueParm1[0];
				valueParm1[1] = PennantApplicationUtil.formateAmount(finWriteoffTotAmt, format).toString();
				errParm1[1] = PennantJavaUtil.getLabel("label_OutstandWriteoffAmount") + ":" + valueParm1[1];
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "30568", errParm1, valueParm1), usrLanguage));
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !fm.isWorkflow()) {
			fm.setBefImage(befFinanceMain);
		}

		return auditDetail;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		String auditTranType = "";
		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (financeMain.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// Finance Flag details
		if (financeDetail.getFinFlagsDetails() != null && financeDetail.getFinFlagsDetails().size() > 0) {
			auditDetailMap.put("FinFlagsDetail", setFinFlagAuditData(financeDetail, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinFlagsDetail"));
		}

		// Finance Document Details
		if (financeDetail.getDocumentDetailsList() != null && financeDetail.getDocumentDetailsList().size() > 0) {
			auditDetailMap.put("DocumentDetails", setDocumentDetailsAuditData(financeDetail, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
		}

		// ISRA Details
		if (financeDetail.getIsraDetail() != null) {
			auditDetailMap.put("ISRALiquidDeatils",
					this.israDetailService.getISRALiquidDeatils(financeDetail.getIsraDetail(), auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("ISRALiquidDeatils"));
		}

		// Finance Checklist Details
		// =======================================
		List<FinanceCheckListReference> financeCheckList = financeDetail.getFinanceCheckList();

		if (StringUtils.equals(method, "saveOrUpdate")) {
			if (financeCheckList != null && !financeCheckList.isEmpty()) {
				auditDetails.addAll(
						checkListDetailService.getAuditDetail(auditDetailMap, financeDetail, auditTranType, method));
			}
		}

		// Finance Guarantor Details
		// =======================================
		List<GuarantorDetail> finGuarantor = financeDetail.getGurantorsDetailList();
		if (finGuarantor != null && !finGuarantor.isEmpty()) {
			auditDetailMap.put("GuarantorDetails", setGuarantorDetailAuditData(financeDetail, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("GuarantorDetails"));
		}

		// Joint Account Details
		// =======================================
		List<JointAccountDetail> finJointAccoutnDetail = financeDetail.getJointAccountDetailList();
		if (finJointAccoutnDetail != null && !finJointAccoutnDetail.isEmpty()) {
			auditDetailMap.put("JointAccountDetails",
					setJointAccountDetailAuditData(financeDetail, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("JointAccountDetails"));
		}

		if (ImplementationConstants.COLLATERAL_INTERNAL) {

			// Collateral Assignment Details
			// =======================================
			if (financeDetail.getCollateralAssignmentList() != null
					&& financeDetail.getCollateralAssignmentList().size() > 0) {
				auditDetailMap.put("CollateralAssignments",
						setCollateralAssignmentAuditData(financeDetail, auditTranType, method));
				auditDetails.addAll(auditDetailMap.get("CollateralAssignments"));
			}
		} else {
			// Finance Collaterals Details
			// =======================================
			List<FinCollaterals> finCollateral = financeDetail.getFinanceCollaterals();
			if (finCollateral != null && !finCollateral.isEmpty()) {
				auditDetailMap.put("FinCollateral",
						setFinCollateralDetailAuditData(financeDetail, auditTranType, method));
				auditDetails.addAll(auditDetailMap.get("FinCollateral"));
			}
		}

		// Extended Field Details
		if (financeDetail.getExtendedFieldRender() != null) {
			ExtendedFieldHeader extendedFieldHeader = financeDetail.getExtendedFieldHeader();
			ExtendedFieldRender extendedFieldRender = financeDetail.getExtendedFieldRender();
			if (extendedFieldRender.getInstructionUID() == Long.MIN_VALUE
					&& financeMain.getInstructionUID() != Long.MIN_VALUE) {
				extendedFieldRender.setInstructionUID(financeMain.getInstructionUID());
			}
			financeDetail.getExtendedFieldRender().setTableName(
					extendedFieldHeader.getModuleName() + "_" + extendedFieldHeader.getSubModuleName() + "_ED");
			auditDetailMap.put("ExtendedFieldDetails",
					extendedFieldDetailsService.setExtendedFieldsAuditData(financeDetail.getExtendedFieldRender(),
							auditTranType, method, financeDetail.getExtendedFieldHeader().getModuleName()));
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldDetails"));
		}

		// set Tan Assignments Audit
		// =======================================
		if (financeDetail.getTanAssignments() != null && financeDetail.getTanAssignments().size() > 0) {
			auditDetailMap.put("TanAssignment", tanAssignmentService.setTanAssignmentAuditData(financeDetail,
					auditTranType, method, financeDetail.getFinScheduleData().getFinanceMain().getWorkflowId()));
			auditDetails.addAll(auditDetailMap.get("TanAssignment"));
		}

		financeDetail.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(financeDetail);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving ");

		return auditHeader;

	}

	/**
	 * Methods for Creating List Finance Flag of Audit Details with detailed fields
	 * 
	 * @param financeDetail
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setFinFlagAuditData(FinanceDetail financeDetail, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		for (int i = 0; i < financeDetail.getFinFlagsDetails().size(); i++) {

			FinFlagsDetail finFlagsDetail = financeDetail.getFinFlagsDetails().get(i);
			boolean isRcdType = false;
			if (StringUtils.isEmpty(finFlagsDetail.getRecordType())) {
				continue;
			}

			if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (finFlagsDetail.isWorkflow()) {
					isRcdType = true;
				}
			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				finFlagsDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			if (StringUtils.isNotEmpty(finFlagsDetail.getRecordType())) {
				String[] fields = PennantJavaUtil.getFieldDetails(new FinFlagsDetail(),
						finFlagsDetail.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						finFlagsDetail.getBefImage(), finFlagsDetail));
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for Guarantor Details
	 * 
	 * @param auditDetails
	 * @param type
	 * @param Finreference
	 * @return
	 */
	private List<AuditDetail> processingGuarantorDetailList(List<AuditDetail> auditDetails, String type, long finID,
			String finReference) {

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			GuarantorDetail guarantorDetail = (GuarantorDetail) auditDetails.get(i).getModelData();

			if (StringUtils.isBlank(guarantorDetail.getRecordType())) {
				continue;
			}

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				guarantorDetail.setRoleCode("");
				guarantorDetail.setNextRoleCode("");
				guarantorDetail.setTaskId("");
				guarantorDetail.setNextTaskId("");
			}

			guarantorDetail.setWorkflowId(0);
			guarantorDetail.setFinID(finID);
			guarantorDetail.setFinReference(finReference);

			if (guarantorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (guarantorDetail.isNewRecord()) {
				saveRecord = true;
				if (guarantorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					guarantorDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (guarantorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					guarantorDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (guarantorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					guarantorDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (guarantorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (guarantorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (guarantorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (guarantorDetail.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = guarantorDetail.getRecordType();
				recordStatus = guarantorDetail.getRecordStatus();
				guarantorDetail.setRecordType("");
				guarantorDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				// 10-Jul-2018 BUG FIX related to Audit issue TktNo:126609
				if (!guarantorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					guarantorDetail.setBefImage(
							guarantorDetailDAO.getGuarantorDetailById(guarantorDetail.getGuarantorId(), ""));
				}
			}
			if (saveRecord) {
				guarantorDetailDAO.save(guarantorDetail, type);
			}

			if (updateRecord) {
				guarantorDetailDAO.update(guarantorDetail, type);
			}

			if (deleteRecord) {
				guarantorDetailDAO.delete(guarantorDetail, type);
			}

			if (approveRec) {
				guarantorDetail.setRecordType(rcdType);
				guarantorDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(guarantorDetail);
		}

		return auditDetails;

	}

	/**
	 * Method For Preparing List of AuditDetails for Joint Details
	 * 
	 * @param auditDetails
	 * @param type
	 * @param Finreference
	 * @return
	 */
	private List<AuditDetail> processingJointAccountDetailList(List<AuditDetail> auditDetails, String type, long finID,
			String finReference) {

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			JointAccountDetail jointAccountDetail = (JointAccountDetail) auditDetails.get(i).getModelData();

			if (StringUtils.isBlank(jointAccountDetail.getRecordType())) {
				continue;
			}

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				jointAccountDetail.setRoleCode("");
				jointAccountDetail.setNextRoleCode("");
				jointAccountDetail.setTaskId("");
				jointAccountDetail.setNextTaskId("");
			}

			jointAccountDetail.setWorkflowId(0);
			jointAccountDetail.setFinID(finID);
			jointAccountDetail.setFinReference(finReference);

			if (jointAccountDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (jointAccountDetail.isNewRecord()) {
				saveRecord = true;
				if (jointAccountDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					jointAccountDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (jointAccountDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					jointAccountDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (jointAccountDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					jointAccountDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (jointAccountDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (jointAccountDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (jointAccountDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (jointAccountDetail.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = jointAccountDetail.getRecordType();
				recordStatus = jointAccountDetail.getRecordStatus();
				jointAccountDetail.setRecordType("");
				jointAccountDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				// 10-Jul-2018 BUG FIX related to Audit issue TktNo:126609
				if (!jointAccountDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					jointAccountDetail.setBefImage(jointAccountDetailDAO
							.getJointAccountDetailById(jointAccountDetail.getJointAccountId(), ""));
				}
			}
			if (saveRecord) {
				jointAccountDetailDAO.save(jointAccountDetail, type);
			}

			if (updateRecord) {
				jointAccountDetailDAO.update(jointAccountDetail, type);
			}

			if (deleteRecord) {
				jointAccountDetailDAO.delete(jointAccountDetail, type);
			}

			if (approveRec) {
				jointAccountDetail.setRecordType(rcdType);
				jointAccountDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(jointAccountDetail);
		}

		return auditDetails;

	}

	/**
	 * Method For Preparing List of AuditDetails for FinCollateral Details
	 * 
	 * @param auditDetails
	 * @param type
	 * @param Finreference
	 * @return
	 */
	private List<AuditDetail> processingFinCollateralDetailList(List<AuditDetail> auditDetails, String type,
			String finReference) {

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			FinCollaterals finCollaterals = (FinCollaterals) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				finCollaterals.setRoleCode("");
				finCollaterals.setNextRoleCode("");
				finCollaterals.setTaskId("");
				finCollaterals.setNextTaskId("");
			}

			finCollaterals.setWorkflowId(0);
			finCollaterals.setFinReference(finReference);

			if (finCollaterals.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (finCollaterals.isNewRecord()) {
				saveRecord = true;
				if (finCollaterals.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					finCollaterals.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (finCollaterals.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					finCollaterals.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (finCollaterals.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					finCollaterals.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (finCollaterals.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (finCollaterals.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (finCollaterals.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (finCollaterals.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = finCollaterals.getRecordType();
				recordStatus = finCollaterals.getRecordStatus();
				finCollaterals.setRecordType("");
				finCollaterals.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				finCollateralsDAO.save(finCollaterals, type);
			}
			if (updateRecord) {
				finCollateralsDAO.update(finCollaterals, type);
			}

			if (deleteRecord) {
				finCollateralsDAO.delete(finCollaterals, type);
			}

			if (approveRec) {
				finCollaterals.setRecordType(rcdType);
				finCollaterals.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(finCollaterals);
		}

		return auditDetails;

	}

	/**
	 * Method For Preparing List of AuditDetails for Check List for Fin Flag Details
	 * 
	 * @param auditDetails
	 * @param financeDetail
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingFinFlagDetailList(List<AuditDetail> auditDetails, FinanceDetail financeDetail,
			String type) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			FinFlagsDetail finFlagsDetail = (FinFlagsDetail) auditDetails.get(i).getModelData();
			if (StringUtils.isEmpty(finFlagsDetail.getRecordType())) {
				continue;
			}
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				finFlagsDetail.setRoleCode("");
				finFlagsDetail.setNextRoleCode("");
				finFlagsDetail.setTaskId("");
				finFlagsDetail.setNextTaskId("");
				finFlagsDetail.setWorkflowId(0);
			}

			if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (finFlagsDetail.isNewRecord()) {
				saveRecord = true;
				if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (finFlagsDetail.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = finFlagsDetail.getRecordType();
				recordStatus = finFlagsDetail.getRecordStatus();
				finFlagsDetail.setRecordType("");
				finFlagsDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				finFlagDetailsDAO.save(finFlagsDetail, type);
			}

			if (updateRecord) {
				finFlagDetailsDAO.update(finFlagsDetail, type);
			}

			if (deleteRecord) {
				finFlagDetailsDAO.delete(finFlagsDetail.getReference(), finFlagsDetail.getFlagCode(),
						finFlagsDetail.getModuleName(), type);
			}

			if (approveRec) {
				finFlagsDetail.setRecordType(rcdType);
				finFlagsDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(finFlagsDetail);
		}

		logger.debug("Leaving");
		return auditDetails;

	}

	/**
	 * Method for setting the DMA and DSA data to the financeMain.
	 * 
	 * @param financeMain
	 */
	private void setDasAndDmaData(FinanceMain financeMain) {
		logger.debug(Literal.ENTERING);
		List<Long> dealerIds = new ArrayList<Long>();
		long dsaCode = 0;
		long dmaCode = 0;
		if (StringUtils.isNotBlank(financeMain.getDsaCode()) && StringUtils.isNumeric(financeMain.getDsaCode())) {
			dsaCode = Long.valueOf(financeMain.getDsaCode());
			dealerIds.add(dsaCode);
		}
		if (StringUtils.isNotBlank(financeMain.getDmaCode()) && StringUtils.isNumeric(financeMain.getDmaCode())) {
			dmaCode = Long.valueOf(financeMain.getDmaCode());
			dealerIds.add(dmaCode);
		}
		if (dealerIds.size() > 0) {
			List<VehicleDealer> vehicleDealerList = vehicleDealerService.getVehicleDealerById(dealerIds);
			if (vehicleDealerList != null && !vehicleDealerList.isEmpty()) {
				for (VehicleDealer dealer : vehicleDealerList) {
					if (dealer.getDealerId() == dmaCode) {
						financeMain.setDmaName(dealer.getDealerName());
						financeMain.setDmaCodeDesc(dealer.getCode());
					} else if (dealer.getDealerId() == dsaCode) {
						financeMain.setDsaName(dealer.getDealerName());
						financeMain.setDsaCodeDesc(dealer.getCode());
					}
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private List<FinServiceInstruction> getServiceInstructions(FinanceDetail financeDetail) {
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		List<FinServiceInstruction> serviceInstructions = finScheduleData.getFinServiceInstructions();

		if (CollectionUtils.isEmpty(serviceInstructions)) {
			FinServiceInstruction finServInst = new FinServiceInstruction();
			finServInst.setFinID(financeMain.getFinID());
			finServInst.setFinReference(financeMain.getFinReference());
			finServInst.setFinEvent(financeDetail.getModuleDefiner());

			finScheduleData.setFinServiceInstruction(finServInst);
		}

		for (FinServiceInstruction serviceInstruction : finScheduleData.getFinServiceInstructions()) {
			if (serviceInstruction.getInstructionUID() == Long.MIN_VALUE) {
				serviceInstruction.setInstructionUID(Long.valueOf(ReferenceGenerator.generateNewServiceUID()));
			}

			if (StringUtils.isEmpty(financeDetail.getModuleDefiner())
					|| FinServiceEvent.ORG.equals(financeDetail.getModuleDefiner())) {

				if (!FinServiceEvent.ORG.equals(serviceInstruction.getFinEvent())
						&& !StringUtils.contains(serviceInstruction.getFinEvent(), "_O")) {
					serviceInstruction.setFinEvent(serviceInstruction.getFinEvent().concat("_O"));
				}
			}
		}

		return finScheduleData.getFinServiceInstructions();
	}

	@Override
	public int getSchdVersion(long finID) {
		return financeMainDAO.getSchdVersion(finID);
	}

	public void setFinanceWriteoffDAO(FinanceWriteoffDAO financeWriteoffDAO) {
		this.financeWriteoffDAO = financeWriteoffDAO;
	}

	public void setGuarantorDetailDAO(GuarantorDetailDAO guarantorDetailDAO) {
		this.guarantorDetailDAO = guarantorDetailDAO;
	}

	public void setJointAccountDetailDAO(JointAccountDetailDAO jointAccountDetailDAO) {
		this.jointAccountDetailDAO = jointAccountDetailDAO;
	}

	public FlagDetailValidation getFlagDetailValidation() {
		if (flagDetailValidation == null) {
			this.flagDetailValidation = new FlagDetailValidation(finFlagDetailsDAO);
		}
		return this.flagDetailValidation;
	}

	public void setMandateDAO(MandateDAO mandateDAO) {
		this.mandateDAO = mandateDAO;
	}

	public FinJointAccountDetailValidation getFinJointAccountDetailValidation() {
		if (finJointAccountDetailValidation == null) {
			this.finJointAccountDetailValidation = new FinJointAccountDetailValidation(jointAccountDetailDAO,
					financeTaxDetailDAO);
		}
		return this.finJointAccountDetailValidation;
	}

	public void setFinJointAccountDetailValidation(FinJointAccountDetailValidation finJointAccountDetailValidation) {
		this.finJointAccountDetailValidation = finJointAccountDetailValidation;
	}

	public FinGuarantorDetailValidation getFinGuarantorDetailValidation() {
		if (finGuarantorDetailValidation == null) {
			this.finGuarantorDetailValidation = new FinGuarantorDetailValidation(guarantorDetailDAO,
					financeTaxDetailDAO);
		}
		return this.finGuarantorDetailValidation;
	}

	public void setFinGuarantorDetailValidation(FinGuarantorDetailValidation finGuarantorDetailValidation) {
		this.finGuarantorDetailValidation = finGuarantorDetailValidation;
	}

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	public void setChequeHeaderDAO(ChequeHeaderDAO chequeHeaderDAO) {
		this.chequeHeaderDAO = chequeHeaderDAO;
	}

	public void setIsraDetailService(ISRADetailService israDetailService) {
		this.israDetailService = israDetailService;
	}

	@Autowired
	public void setCustomerDataService(CustomerDataService customerDataService) {
		this.customerDataService = customerDataService;
	}

	public void setCollateralSetupService(CollateralSetupService collateralSetupService) {
		this.collateralSetupService = collateralSetupService;
	}
}
