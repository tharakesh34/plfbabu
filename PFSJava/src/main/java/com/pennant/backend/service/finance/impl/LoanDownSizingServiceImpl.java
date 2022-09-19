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
 * FileName : LoanDownSizingServiceImpl.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 16-11-2018 *
 * 
 * Modified Date : 16-11-2018 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 16-11-2018 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.core.ChangeGraceEndService;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinAssetAmtMovementDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinAssetAmtMovement;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.service.finance.LoanDownSizingService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;

public class LoanDownSizingServiceImpl extends GenericFinanceDetailService implements LoanDownSizingService {
	private static final Logger logger = LogManager.getLogger(LoanDownSizingServiceImpl.class);

	private ChangeGraceEndService changeGraceEndService;
	private FinAssetAmtMovementDAO finAssetAmtMovementDAO;
	private ExtendedFieldDetailsService extendedFieldDetailsService;

	@Override
	public FinanceDetail getDownSizingFinance(FinanceMain fm, String rcdMaintainSts) {
		logger.debug(Literal.ENTERING);

		long finID = fm.getFinID();

		// Finance Details
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData finScheduleData = getFinSchDataByFinRef(finID, "", 0);

		List<FinServiceInstruction> finservInstList = new ArrayList<FinServiceInstruction>();
		if (StringUtils.isNotBlank(rcdMaintainSts)) {
			finservInstList = finServiceInstructionDAO.getFinServiceInstructions(finID, "_Temp", rcdMaintainSts);
		} else {
			finservInstList.add(new FinServiceInstruction());
		}

		// TODO : Customer Details
		financeDetail.setCustomerDetails(getCustomerDetails(fm));

		finScheduleData.setFinServiceInstructions(finservInstList);

		fm = finScheduleData.getFinanceMain();
		if (fm.isStepFinance() && StringUtils.equals(fm.getCalcOfSteps(), PennantConstants.STEPPING_CALC_AMT)) {
			List<FinanceStepPolicyDetail> financeStepPolicyDetailList = financeStepDetailDAO
					.getFinStepDetailListByFinRef(fm.getFinID(), "_View", false);
			finScheduleData.setStepPolicyDetails(financeStepPolicyDetailList, true);
		}

		// Plan EMI Holiday Details
		if (fm.isPlanEMIHAlw()) {
			if (StringUtils.equals(fm.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
				finScheduleData.setPlanEMIHmonths(finPlanEmiHolidayDAO.getPlanEMIHMonthsByRef(finID, ""));
			} else if (StringUtils.equals(fm.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
				finScheduleData.setPlanEMIHDates(finPlanEmiHolidayDAO.getPlanEMIHDatesByRef(finID, ""));
			}
		}
		financeDetail.setFinScheduleData(finScheduleData);

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	private CustomerDetails getCustomerDetails(FinanceMain fm) {
		CustomerDetails cd = new CustomerDetails();
		cd.setCustID(fm.getCustID());

		Customer customer = new Customer();
		customer.setCustID(fm.getCustID());
		customer.setCustCIF(fm.getLovDescCustCIF());
		customer.setCustShrtName(fm.getLovDescCustShrtName());

		cd.setCustomer(customer);
		return cd;
	}

	public FinScheduleData getFinSchDataByFinRef(long finID, String type, long logKey) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = new FinScheduleData();

		if (logKey == 0) {
			FinanceMain fm = financeMainDAO.getFinanceMainById(finID, "_View", false);

			schdData.setFinID(fm.getFinID());
			schdData.setFinReference(fm.getFinReference());

			schdData.setFinanceMain(fm);

			String finType = fm.getFinType();
			schdData.setFinanceType(financeTypeDAO.getFinanceTypeByID(finType, "_AView"));
		}

		// Schedule, Disbursement Details and Repay Instructions
		schdData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, type, false));
		schdData.setDisbursementDetails(financeDisbursementDAO.getFinanceDisbursementDetails(finID, type, false));
		schdData.setRepayInstructions(repayInstructionDAO.getRepayInstructions(finID, type, false));

		logger.debug(Literal.LEAVING);

		return schdData;
	}

	public FinScheduleData changeGraceEndAfterFullDisb(FinScheduleData schdData) {
		FinanceMain fm = schdData.getFinanceMain();
		fm.setEventFromDate(SysParamUtil.getAppDate());

		changeGraceEndService.changeGraceEnd(schdData, true);

		return schdData;
	}

	public AEEvent getChangeGrcEndPostings(FinScheduleData schdData) {
		return changeGraceEndService.getChangeGrcEndPostings(schdData);

	}

	public List<FinAssetAmtMovement> getFinAssetAmtMovements(long finID, String movementType) {
		return finAssetAmtMovementDAO.getFinAssetAmtMovements(finID, movementType);
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = (FinanceDetail) auditHeader.getAuditDetail().getModelData();

		List<FinServiceInstruction> serviceInstructions = getServiceInstructions(fd);

		long serviceUID = Long.MIN_VALUE;

		if (fd.getExtendedFieldRender() != null && fd.getExtendedFieldRender().getInstructionUID() != Long.MIN_VALUE) {
			serviceUID = fd.getExtendedFieldRender().getInstructionUID();
		}

		for (FinServiceInstruction finServInst : serviceInstructions) {
			serviceUID = finServInst.getInstructionUID();
			if (ObjectUtils.isEmpty(finServInst.getInitiatedDate())) {
				finServInst.setInitiatedDate(SysParamUtil.getAppDate());
			}
		}

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, PennantConstants.method_saveOrUpdate);
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinScheduleData fsd = fd.getFinScheduleData();
		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();
		FinServiceInstruction fsi = fsd.getFinServiceInstructions().get(0);
		fsi.setInstructionUID(serviceUID);

		String rcdMaintainSts = "";
		TableType tableType = TableType.MAIN_TAB;

		if (fm.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
			rcdMaintainSts = FinServiceEvent.LOANDOWNSIZING;
		}

		fm.setRcdMaintainSts(rcdMaintainSts);

		if (fm.isNewRecord()) {

			financeMainDAO.save(fm, tableType, false);
			finServiceInstructionDAO.save(fsi, tableType.getSuffix());

			if (fm.isStepFinance() && CollectionUtils.isNotEmpty(fsd.getStepPolicyDetails())) {
				financeStepDetailDAO.deleteList(fm.getFinID(), false, tableType.getSuffix());
				saveStepDetailList(fsd, false, tableType.getSuffix());
				List<AuditDetail> financeStepPolicyDetail = fd.getAuditDetailMap().get("FinanceStepPolicyDetail");
				auditDetails.addAll(financeStepPolicyDetail);
			}

			auditHeader.setAuditReference(fm.getFinReference());
			auditHeader.getAuditDetail().setModelData(fm);

		} else {
			financeMainDAO.update(fm, tableType, false);

			finServiceInstructionDAO.deleteList(fm.getFinID(), rcdMaintainSts, tableType.getSuffix());
			finServiceInstructionDAO.save(fsi, tableType.getSuffix());

			if (fm.isStepFinance() && CollectionUtils.isNotEmpty(fsd.getStepPolicyDetails())) {
				financeStepDetailDAO.deleteList(fm.getFinID(), false, tableType.getSuffix());
				saveStepDetailList(fsd, false, tableType.getSuffix());
				List<AuditDetail> financeStepPolicyDetail = fd.getAuditDetailMap().get("FinanceStepPolicyDetail");
				auditDetails.addAll(financeStepPolicyDetail);
			}

		}

		// Extended field Details
		if (fd.getExtendedFieldRender() != null) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("ExtendedFieldDetails");

			details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
					ExtendedFieldConstants.MODULE_LOAN, fd.getExtendedFieldHeader().getEvent(), tableType.getSuffix(),
					serviceUID);
			auditDetails.addAll(details);
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}
		auditHeader.setAuditDetails(auditDetails);
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), fm.getExcludeFields());
		auditHeader.setAuditDetail(
				new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], fm.getBefImage(), fm));

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, PennantConstants.method_doApprove);
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinanceDetail fd = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		fd.setModuleDefiner(FinServiceEvent.LOANDOWNSIZING);
		List<AuditDetail> auditDetails = new ArrayList<>();

		List<FinServiceInstruction> serviceInstructions = getServiceInstructions(fd);

		long serviceUID = Long.MIN_VALUE;

		if (fd.getExtendedFieldRender() != null && fd.getExtendedFieldRender().getInstructionUID() != Long.MIN_VALUE) {
			serviceUID = fd.getExtendedFieldRender().getInstructionUID();
		}

		Date appDate = SysParamUtil.getAppDate();
		for (FinServiceInstruction finServInst : serviceInstructions) {
			serviceUID = finServInst.getInstructionUID();
			if (ObjectUtils.isEmpty(finServInst.getInitiatedDate())) {
				finServInst.setInitiatedDate(appDate);
			}
		}

		FinScheduleData fsd = fd.getFinScheduleData();
		FinServiceInstruction fsi = fsd.getFinServiceInstructions().get(0);
		BigDecimal downSizingAmt = fsi.getAmount();
		fsi.setInstructionUID(serviceUID);

		FinanceMain fm = new FinanceMain();
		BeanUtils.copyProperties(fsd.getFinanceMain(), fm);

		fm.setRcdMaintainSts("");
		fm.setRoleCode("");
		fm.setNextRoleCode("");
		fm.setTaskId("");
		fm.setNextTaskId("");
		fm.setWorkflowId(0);

		// Revised FinAssetValue Value
		FinAssetAmtMovement assetAmtMovt = prepareSanAmtMovement(fm, fsi);
		fm.setFinAssetValue(fm.getFinAssetValue().subtract(downSizingAmt));

		if (fm.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
			tranType = PennantConstants.TRAN_ADD;
			fm.setRecordType("");
		} else {
			tranType = PennantConstants.TRAN_UPD;
			fm.setRecordType("");

			// FULL DISB : Schedule Changed By Grace Period Ended
			if (fm.isScheduleRegenerated()) {

				// 1. Postings ----> Execute Accounting Details Process
				auditHeader = executeAccountingProcess(auditHeader, appDate);

				if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
					auditHeader.setErrorList(auditHeader.getErrorMessage());
					return auditHeader;
				}

				// 2. Data Saving ----> Schedule Tables
				processScheduleDetails(fd, fsi);
				financeMainDAO.update(fm, TableType.MAIN_TAB, false);
			} else {

				// Update Revised Sanctioned Amount and Save FinServiceInstructions
				finServiceInstructionDAO.save(fsi, "");
				financeMainDAO.updateFinAssetValue(fm);
			}
		}

		// Sanctioned Amount Movements
		finAssetAmtMovementDAO.saveFinAssetAmtMovement(assetAmtMovt);

		// Delete from Staging Tables
		financeMainDAO.delete(fm, TableType.TEMP_TAB, false, true);
		finServiceInstructionDAO.deleteList(fm.getFinID(), FinServiceEvent.LOANDOWNSIZING, "_Temp");

		if (fm.isStepFinance() && CollectionUtils.isNotEmpty(fsd.getStepPolicyDetails())) {
			financeStepDetailDAO.deleteList(fm.getFinID(), false, "");
			saveStepDetailList(fsd, false, "");
			List<AuditDetail> financeStepPolicyDetail = fd.getAuditDetailMap().get("FinanceStepPolicyDetail");
			auditDetails.addAll(financeStepPolicyDetail);
		}

		// Extended field Render Details.
		List<AuditDetail> details = fd.getAuditDetailMap().get("ExtendedFieldDetails");
		if (fd.getExtendedFieldRender() != null) {
			details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
					ExtendedFieldConstants.MODULE_LOAN, fd.getExtendedFieldHeader().getEvent(), "", serviceUID);

			auditDetails.addAll(details);
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), fm.getExcludeFields());
		auditHeader.setAuditDetail(
				new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], fm.getBefImage(), fm));

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(fm);

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private void processScheduleDetails(FinanceDetail fd, FinServiceInstruction finServInst) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		// START : prepare FinLogEntryDetail data
		FinLogEntryDetail entryDetail = new FinLogEntryDetail();

		entryDetail.setReversalCompleted(false);
		entryDetail.setFinID(finID);
		entryDetail.setFinReference(finReference);
		entryDetail.setPostDate(SysParamUtil.getAppDate());
		entryDetail.setSchdlRecal(fm.isScheduleRegenerated());
		entryDetail.setEventAction(AccountingEvent.GRACEEND);

		long logKey = finLogEntryDetailDAO.save(entryDetail);
		// END

		// START : Fetch Existing data before Modification
		FinScheduleData oldFinSchdData = getFinSchDataByFinRef(finID, "", -1);

		oldFinSchdData.setFinID(finID);
		oldFinSchdData.setFinReference(finReference);

		oldFinSchdData.setFinanceMain(fm);

		// Save Schedule Details For Future Modifications
		listSave(oldFinSchdData, "_Log", false, logKey, finServInst.getServiceSeqId());

		// END

		// delete from Staging table and schedule tables delete and insert
		listDeletion(schdData, fd.getModuleDefiner(), "", false);
		finServiceInstructionDAO.deleteList(fm.getFinID(), fd.getModuleDefiner(), "");

		// save latest schedule details
		listSave(schdData, "", false, 0, finServInst.getServiceSeqId());

		logger.debug(Literal.LEAVING);
	}

	private FinAssetAmtMovement prepareSanAmtMovement(FinanceMain fm, FinServiceInstruction finServInst) {
		FinAssetAmtMovement assetAmtMovt = new FinAssetAmtMovement();

		assetAmtMovt.setFinServiceInstID(finServInst.getServiceSeqId());
		assetAmtMovt.setFinID(fm.getFinID());
		assetAmtMovt.setFinReference(fm.getFinReference());
		assetAmtMovt.setMovementDate(SysParamUtil.getAppDate());

		// TODO : Not Always 1
		assetAmtMovt.setMovementOrder(1);

		// DS : DownSizing, US : UpSizing
		assetAmtMovt.setMovementType(FinanceConstants.MOVEMENTTYPE_DOWNSIZING);

		BigDecimal rvsdSanAmount = fm.getFinAssetValue().subtract(finServInst.getAmount());

		// DownSizing Amount
		assetAmtMovt.setMovementAmount(finServInst.getAmount());
		assetAmtMovt.setRevisedSanctionedAmt(rvsdSanAmount);
		assetAmtMovt.setSanctionedAmt(fm.getFinAssetValue());
		assetAmtMovt.setDisbursedAmt(fm.getFinCurrAssetValue());
		assetAmtMovt.setAvailableAmt(rvsdSanAmount.subtract(fm.getFinCurrAssetValue()));

		assetAmtMovt.setLastMntBy(fm.getLastMntBy());
		assetAmtMovt.setLastMntOn(fm.getLastMntOn());

		return assetAmtMovt;
	}

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, PennantConstants.method_doReject);
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		List<AuditDetail> auditDetails = new ArrayList<>();
		FinanceDetail fd = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		financeMainDAO.delete(fm, TableType.TEMP_TAB, false, true);
		finServiceInstructionDAO.deleteList(fm.getFinID(), FinServiceEvent.LOANDOWNSIZING, "_Temp");

		// Step Details
		// =======================================
		financeStepDetailDAO.deleteList(fm.getFinID(), false, "_Temp");
		if (CollectionUtils.isNotEmpty(schdData.getStepPolicyDetails())) {
			List<AuditDetail> financeStepPolicyDetail = fd.getAuditDetailMap().get("FinanceStepPolicyDetail");
			auditDetails.addAll(financeStepPolicyDetail);
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), fm.getExcludeFields());
		auditHeader.setAuditDetail(
				new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], fm.getBefImage(), fm));

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		String usrLanguage = PennantConstants.default_Language;
		if (financeMain.getUserDetails() == null) {
			financeMain.setUserDetails(new LoggedInUser());
			usrLanguage = financeMain.getUserDetails().getLanguage();
		}
		auditHeader = getAuditDetails(auditHeader, method);

		// Extended field details Validation
		if (financeDetail.getExtendedFieldRender() != null) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("LoanExtendedFieldDetails");
			ExtendedFieldHeader extendedFieldHeader = financeDetail.getExtendedFieldHeader();
			if (extendedFieldHeader != null) {
				StringBuilder sb = new StringBuilder();
				sb.append(extendedFieldHeader.getModuleName());
				sb.append("_");
				sb.append(extendedFieldHeader.getSubModuleName());
				if (extendedFieldHeader.getEvent() != null) {
					sb.append("_");
					sb.append(PennantStaticListUtil.getFinEventCode(extendedFieldHeader.getEvent()));
				}
				sb.append("_ED");
				details = extendedFieldDetailsService.vaildateDetails(details, method, usrLanguage, sb.toString());
				auditDetails.addAll(details);
			}
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = (FinanceDetail) auditDetail.getModelData();
		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];

		valueParm[0] = fm.getFinReference();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + " : " + valueParm[0];

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.equals(PennantConstants.method_doApprove, method)
				&& !PennantConstants.RECORD_TYPE_NEW.equals(fm.getRecordType())) {
			auditDetail.setBefImage(financeMainDAO.getFinanceMainById(fm.getFinID(), "", false));
		}

		if (fm.isStepFinance() && StringUtils.equals(fm.getCalcOfSteps(), PennantConstants.STEPPING_CALC_AMT)) {
			FinServiceInstruction finServInst = fd.getFinScheduleData().getFinServiceInstructions().get(0);
			BigDecimal revisedSanAmt = fm.getFinAssetValue().subtract(finServInst.getAmount());
			BigDecimal totalAmt = fm.getTotalCpz().add(fm.getFinCurrAssetValue());
			if ((totalAmt.compareTo(revisedSanAmt) > 0)) {
				auditDetail.setErrorDetail(
						ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "STP002", null, null)));
			}

			List<FinanceScheduleDetail> fsdList = fd.getFinScheduleData().getFinanceScheduleDetails();
			List<FinanceStepPolicyDetail> spdList = fd.getFinScheduleData().getStepPolicyDetails();
			List<FinanceStepPolicyDetail> rpyList = new ArrayList<>(1);
			for (FinanceStepPolicyDetail financeStepPolicyDetail : spdList) {
				if (StringUtils.equals(financeStepPolicyDetail.getStepSpecifier(),
						PennantConstants.STEP_SPECIFIER_REG_EMI)) {
					rpyList.add(financeStepPolicyDetail);
				}
			}

			int idxStart = 0;
			idxStart = idxStart + fm.getGraceTerms();

			if (CollectionUtils.isNotEmpty(rpyList)) {
				boolean isValidEMI = true;
				Collections.sort(rpyList, (step1, step2) -> step1.getStepNo() > step2.getStepNo() ? 1
						: step1.getStepNo() < step2.getStepNo() ? -1 : 0);
				FinanceStepPolicyDetail lastStp = rpyList.get(rpyList.size() - 1);

				if (revisedSanAmt.subtract(fm.getTotalCpz()).compareTo(fm.getFinCurrAssetValue()) == 0
						&& fm.getMaturityDate().compareTo(lastStp.getStepEnd()) != 0) {
					auditDetail.setErrorDetail(ErrorUtil
							.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "STP013", null, null)));
					return auditDetail;
				}
				for (FinanceStepPolicyDetail spd : rpyList) {
					if (fm.getNoOfSteps() != spd.getStepNo()) {
						int instCount = 0;
						for (int iFsd = idxStart; iFsd < fsdList.size(); iFsd++) {
							FinanceScheduleDetail fsd = fsdList.get(iFsd);
							if (fsd.isRepayOnSchDate()
									&& StringUtils.equals(fsd.getSpecifier(), CalculationConstants.SCH_SPECIFIER_REPAY)
									&& fsd.getSchDate().compareTo(spd.getStepStart()) >= 0) {
								if (StringUtils.equals(fsd.getSchdMethod(), CalculationConstants.SCHMTHD_EQUAL)) {
									if (spd.getSteppedEMI().compareTo(BigDecimal.ZERO) > 0
											&& fsd.getProfitCalc().compareTo(spd.getSteppedEMI()) > 0) {
										auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
												new ErrorDetail(PennantConstants.KEY_FIELD, "STP003", null, null)));
										isValidEMI = false;
										break;
									}
								}
								instCount = instCount + 1;
							}

							if (spd.getInstallments() == instCount) {
								idxStart = iFsd + 1;
								break;
							}
						}

						if (!isValidEMI) {
							break;
						}
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);

		return auditDetail;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

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

		// Asset Type Extended Field Details
		List<ExtendedFieldRender> renderList = financeDetail.getExtendedFieldRenderList();
		if (renderList != null && !renderList.isEmpty()) {
			auditDetailMap.put("ExtendedFieldDetails",
					extendedFieldDetailsService.setExtendedFieldsAuditData(renderList, auditTranType, method, null));
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldDetails"));
		}

		// Loan Field Details
		if (financeDetail.getExtendedFieldRender() != null) {
			ExtendedFieldRender extendedFieldRender = financeDetail.getExtendedFieldRender();
			if (extendedFieldRender.getInstructionUID() == Long.MIN_VALUE
					&& financeMain.getInstructionUID() != Long.MIN_VALUE) {
				extendedFieldRender.setInstructionUID(financeMain.getInstructionUID());
			}
			auditDetailMap.put("LoanExtendedFieldDetails",
					extendedFieldDetailsService.setExtendedFieldsAuditData(financeDetail.getExtendedFieldHeader(),
							extendedFieldRender, auditTranType, method, ExtendedFieldConstants.MODULE_LOAN));
			financeDetail.setAuditDetailMap(auditDetailMap);
			auditDetails.addAll(auditDetailMap.get("LoanExtendedFieldDetails"));
		}

		// Extended Field Details
		if (financeDetail.getExtendedFieldRender() != null) {
			auditDetailMap.put("ExtendedFieldDetails",
					extendedFieldDetailsService.setExtendedFieldsAuditData(financeDetail.getExtendedFieldHeader(),
							financeDetail.getExtendedFieldRender(), auditTranType, method,
							financeDetail.getExtendedFieldHeader().getModuleName()));
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldDetails"));
		}

		if (CollectionUtils.isNotEmpty(financeDetail.getFinScheduleData().getStepPolicyDetails())) {
			auditDetailMap.put("FinanceStepPolicyDetail",
					setFinStepDetailAuditData(financeDetail.getFinScheduleData(), auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinanceStepPolicyDetail"));
		}

		financeDetail.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(financeDetail);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private List<FinServiceInstruction> getServiceInstructions(FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		FinScheduleData fsd = fd.getFinScheduleData();

		String event = fd.getExtendedFieldHeader().getEvent();

		List<FinServiceInstruction> fsi = fsd.getFinServiceInstructions();

		if (CollectionUtils.isEmpty(fsi)) {
			FinServiceInstruction fi = new FinServiceInstruction();
			fi.setFinReference(fsd.getFinReference());
			fi.setFinEvent(event);

			fsd.setFinServiceInstruction(fi);
		}

		for (FinServiceInstruction si : fsd.getFinServiceInstructions()) {
			if (si.getInstructionUID() == Long.MIN_VALUE) {
				si.setInstructionUID(Long.valueOf(ReferenceGenerator.generateNewServiceUID()));
			}

			String finEvent = si.getFinEvent();

			if (StringUtils.isEmpty(finEvent) || FinServiceEvent.ORG.equals(finEvent)) {
				if (!FinServiceEvent.ORG.equals(finEvent) && !StringUtils.contains(finEvent, "_O")) {
					si.setFinEvent(finEvent.concat("_O"));
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return fsd.getFinServiceInstructions();
	}

	public void setChangeGraceEndService(ChangeGraceEndService changeGraceEndService) {
		this.changeGraceEndService = changeGraceEndService;
	}

	public void setFinAssetAmtMovementDAO(FinAssetAmtMovementDAO finAssetAmtMovementDAO) {
		this.finAssetAmtMovementDAO = finAssetAmtMovementDAO;
	}

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

}
