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
 * * FileName : ProvisionServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 31-05-2012 * * Modified
 * Date : 31-05-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-05-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.financemanagement.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.financemanagement.ProvisionDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.financemanagement.ProvisionAmount;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.service.financemanagement.ProvisionService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>Provision</b>.<br>
 * 
 */
public class ProvisionServiceImpl extends GenericFinanceDetailService implements ProvisionService {
	private static final Logger logger = LogManager.getLogger(ProvisionServiceImpl.class);

	private ProvisionDAO provisionDAO;

	public ProvisionServiceImpl() {
		super();
	}

	@Override
	public Provision getProvision() {
		return provisionDAO.getProvision();
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		TableType tableType = TableType.MAIN_TAB;
		Provision provision = (Provision) auditHeader.getAuditDetail().getModelData();

		if (provision.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (provision.isNewRecord()) {
			provisionDAO.save(provision, tableType);
			provisionDAO.saveAmounts(provision.getProvisionAmounts(), tableType, false);
		} else {
			provisionDAO.update(provision, tableType);
			provisionDAO.updateAmounts(provision.getProvisionAmounts(), tableType);
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

		Provision provision = (Provision) auditHeader.getAuditDetail().getModelData();
		provisionDAO.deleteAmounts(provision.getId(), TableType.MAIN_TAB);
		provisionDAO.delete(provision, TableType.MAIN_TAB);

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public Provision getProvisionByFinId(long finID) {
		return provisionDAO.getProvisionByFinId(finID, TableType.MAIN_TAB, false);
	}

	@Override
	public Provision getProvisionByFinId(long finID, TableType tableType) {
		Provision provision = provisionDAO.getProvisionByFinId(finID, tableType, false);
		if (provision != null) {
			provision.setProvisionAmounts(provisionDAO.getProvisionAmounts(provision.getId(), tableType));
		}

		return provision;
	}

	public Provision getProvisionById(long id, TableType tableType) {
		Provision provision = provisionDAO.getProvisionById(id, tableType, false);

		if (provision != null) {
			provision.setProvisionAmounts(provisionDAO.getProvisionAmounts(provision.getId(), tableType));
		}
		return provision;
	}

	public Provision getApprovedProvisionById(long finID) {
		return provisionDAO.getProvisionByFinId(finID, TableType.AVIEW, true);
	}

	@Override
	public FinanceProfitDetail getProfitDetailById(long finID) {
		return profitDetailsDAO.getFinProfitDetailsById(finID);
	}

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		Provision provision = new Provision();
		BeanUtils.copyProperties((Provision) auditHeader.getAuditDetail().getModelData(), provision);

		provisionDAO.delete(provision, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(provision.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(provisionDAO.getProvisionById(provision.getId(), TableType.MAIN_TAB, false));
		}

		if (provision.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			provisionDAO.delete(provision, TableType.MAIN_TAB);
		} else {
			provision.setRoleCode("");
			provision.setNextRoleCode("");
			provision.setTaskId("");
			provision.setNextTaskId("");
			provision.setWorkflowId(0);

			long linkiedTranId = executeAccountingProcess(auditHeader);
			provision.setLinkedTranId(linkiedTranId);

			if (provision.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				provision.setRecordType("");
				provisionDAO.save(provision, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				provision.setRecordType("");
				provisionDAO.update(provision, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(provision);

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	// Document Details List Maintenance
	public void listDocDeletion(FinanceDetail custDetails, String tableType) {
		documentDetailsDAO.deleteList(new ArrayList<DocumentDetails>(custDetails.getDocumentDetailsList()), tableType);
	}

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		Provision provision = (Provision) auditHeader.getAuditDetail().getModelData();
		FinanceDetail fd = provision.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();

		FinanceMain fm = schdData.getFinanceMain();
		String tranType = PennantConstants.TRAN_DEL;
		long serviceUID = Long.MIN_VALUE;

		for (FinServiceInstruction finServInst : schdData.getFinServiceInstructions()) {
			serviceUID = finServInst.getInstructionUID();
		}
		// Cancel All Transactions done by Finance Reference
		// =======================================
		AccountingEngine.cancelStageAccounting(fm.getFinID(), FinServiceEvent.PROVISION);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		provisionDAO.deleteAmounts(provision.getId(), TableType.TEMP_TAB);
		provisionDAO.delete(provision, TableType.TEMP_TAB);

		// Save Document Details
		if (fd.getDocumentDetailsList() != null && fd.getDocumentDetailsList().size() > 0) {
			for (DocumentDetails docDetails : fd.getDocumentDetailsList()) {
				docDetails.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
			List<AuditDetail> details = fd.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, "_Temp", schdData.getFinanceMain(), fd.getModuleDefiner(),
					serviceUID);
			auditHeader.setAuditDetails(details);
			listDocDeletion(fd, "_Temp");
		}

		// Fee charges deletion
		finFeeChargesDAO.deleteChargesBatch(fm.getFinID(), fd.getModuleDefiner(), false, "_Temp");

		// Checklist Details delete
		// =======================================
		checkListDetailService.delete(fd, "_Temp", tranType);

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader = nextProcess(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		Provision provision = (Provision) auditDetail.getModelData();

		Provision tempProvision = null;
		if (provision.isWorkflow()) {
			tempProvision = provisionDAO.getProvisionById(provision.getId(), TableType.TEMP_TAB, false);
		}
		Provision befProvision = provisionDAO.getProvisionById(provision.getId(), TableType.MAIN_TAB, false);
		Provision oldProvision = provision.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = provision.getFinReference();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (provision.isNewRecord()) { // for New record or new record into work flow

			if (!provision.isWorkflow()) {// With out Work flow only new records
				if (befProvision != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (provision.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befProvision != null || tempProvision != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					/*
					 * if (befProvision == null || tempProvision != null) {
					 * auditDetail.setErrorDetail(ErrorUtil.getErrorDetail( new ErrorDetail(PennantConstants.KEY_FIELD,
					 * "41005", errParm, valueParm), usrLanguage)); }
					 */
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!provision.isWorkflow()) { // With out Work flow for update and delete

				if (befProvision == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldProvision != null && !oldProvision.getLastMntOn().equals(befProvision.getLastMntOn())) {
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

				/*
				 * if (tempProvision == null) { // if records not exists in the Work flow table
				 * auditDetail.setErrorDetail(ErrorUtil.getErrorDetail( new ErrorDetail(PennantConstants.KEY_FIELD,
				 * "41005", errParm, valueParm), usrLanguage)); }
				 * 
				 * if (oldProvision != null && !oldProvision.getLastMntOn().equals(tempProvision.getLastMntOn())) {
				 * auditDetail.setErrorDetail(ErrorUtil.getErrorDetail( new ErrorDetail(PennantConstants.KEY_FIELD,
				 * "41005", errParm, valueParm), usrLanguage)); }
				 */
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !provision.isWorkflow()) {
			provision.setBefImage(befProvision);
		}

		return auditDetail;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		Provision provision = (Provision) auditHeader.getAuditDetail().getModelData();
		FinanceDetail fd = provision.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		String auditTranType = "";
		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (fm.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// Finance Document Details
		if (fd.getDocumentDetailsList() != null && fd.getDocumentDetailsList().size() > 0) {
			auditDetailMap.put("DocumentDetails", setDocumentDetailsAuditData(fd, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
		}

		// Finance Check List Details
		// =======================================
		List<FinanceCheckListReference> financeCheckList = fd.getFinanceCheckList();

		if (StringUtils.equals(method, "saveOrUpdate")) {
			if (financeCheckList != null && !financeCheckList.isEmpty()) {
				auditDetails.addAll(checkListDetailService.getAuditDetail(auditDetailMap, fd, auditTranType, method));
			}
		} else {
			String tableType = "_Temp";
			if (PennantConstants.RECORD_TYPE_DEL.equals(schdData.getFinanceMain().getRecordType())) {
				tableType = "";
			}

			long finID = fm.getFinID();
			financeCheckList = checkListDetailService.getCheckListByFinRef(finID, tableType);
			fd.setFinanceCheckList(financeCheckList);

			if (financeCheckList != null && !financeCheckList.isEmpty()) {
				auditDetails.addAll(checkListDetailService.getAuditDetail(auditDetailMap, fd, auditTranType, method));
			}
		}

		fd.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(provision);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving ");

		return auditHeader;
	}

	private long executeAccountingProcess(AuditHeader auditHeader) {
		logger.debug("Entering");

		Provision provision = new Provision();
		BeanUtils.copyProperties((Provision) auditHeader.getAuditDetail().getModelData(), provision);
		FinanceMain financeMain = provision.getFinanceDetail().getFinScheduleData().getFinanceMain();

		AEEvent aeEvent = new AEEvent();
		aeEvent.setAccountingEvent(AccountingEvent.PROVSN);
		Long accountingID = AccountingEngine.getAccountSetID(financeMain, AccountingEvent.PROVSN,
				FinanceConstants.MODULEID_FINTYPE);

		aeEvent.setPostingUserBranch(auditHeader.getAuditBranchCode());
		aeEvent.setFinID(provision.getFinID());
		aeEvent.setFinReference(provision.getFinReference());
		aeEvent.setValueDate(SysParamUtil.getAppDate());
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
		}

		amountCodes.setFinType(financeMain.getFinType());
		aeEvent.setBranch(financeMain.getFinBranch());
		aeEvent.setCcy(financeMain.getFinCcy());
		aeEvent.setCustID(financeMain.getCustID());
		aeEvent.setCcy(SysParamUtil.getAppCurrency());

		// amountCodes.setProvDue(provision.getProfitAccruedAndDue());
		amountCodes.setProvAmt(provision.getProvisionedAmt());

		aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());

		if (accountingID != null && accountingID > 0) {
			aeEvent.getAcSetIDList().add(accountingID);
		}
		postingsPreparationUtil.postAccounting(aeEvent);
		logger.debug("Leaving");

		return aeEvent.getLinkedTranId();
	}

	@Override
	public List<ProvisionAmount> getProvisionAmounts(long id, TableType type) {
		return provisionDAO.getProvisionAmounts(id, type);
	}

	public void setProvisionDAO(ProvisionDAO provisionDAO) {
		this.provisionDAO = provisionDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}
}