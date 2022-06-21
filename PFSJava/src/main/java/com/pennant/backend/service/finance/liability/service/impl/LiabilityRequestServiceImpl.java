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
 * * FileName : LiabilityRequestServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 31-12-2015 * *
 * Modified Date : 31-12-2015 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-12-2015 Pennant 0.1 * * 09-05-2018 Vinay 0.2 As per mail from Raju * NOC flag validation functionality *
 * implemented * * 13-06-2018 Siva 0.3 Stage Accounting Modifications * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.finance.liability.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.dao.finance.liability.LiabilityRequestDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.finance.liability.LiabilityRequest;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.service.finance.liability.service.LiabilityRequestService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.constants.FinServiceEvent;

/**
 * Service implementation for methods that depends on <b>LiabilityRequest</b>.<br>
 * 
 */
public class LiabilityRequestServiceImpl extends GenericFinanceDetailService implements LiabilityRequestService {
	private static final Logger logger = LogManager.getLogger(LiabilityRequestServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FinanceSuspHeadDAO financeSuspHeadDAO;
	private LiabilityRequestDAO liabilityRequestDAO;
	private FinanceProfitDetailDAO profitDetailsDAO;

	public LiabilityRequestServiceImpl() {
		super();
	}

	@Override
	public LiabilityRequest getLiabilityRequest() {
		return liabilityRequestDAO.getLiabilityRequest();
	}

	@Override
	public LiabilityRequest getNewLiabilityRequest() {
		return liabilityRequestDAO.getNewLiabilityRequest();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * FinLiabilityReq/FinLiabilityReq_Temp by using LiabilityRequestDAO's save method b) Update the Record in the
	 * table. based on the module workFlow Configuration. by using LiabilityRequestDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtFinLiabilityReq by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 * @throws InterfaceException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		return saveOrUpdate(auditHeader, false);
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * FinLiabilityReq/FinLiabilityReq_Temp by using LiabilityRequestDAO's save method b) Update the Record in the
	 * table. based on the module workFlow Configuration. by using LiabilityRequestDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtFinLiabilityReq by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @param boolean     onlineRequest
	 * @return auditHeader
	 * @throws InterfaceException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private AuditHeader saveOrUpdate(AuditHeader auditHeader, boolean online)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "saveOrUpdate", online);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType = "";
		LiabilityRequest liabilityRequest = (LiabilityRequest) auditHeader.getAuditDetail().getModelData();
		FinanceDetail fd = liabilityRequest.getFinanceDetail();
		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

		long serviceUID = Long.MIN_VALUE;
		if (fd.getFinScheduleData().getFinServiceInstructions().isEmpty()) {
			FinServiceInstruction finServInst = new FinServiceInstruction();
			finServInst.setFinID(fm.getFinID());
			finServInst.setFinReference(fm.getFinReference());
			finServInst.setFinEvent(fd.getModuleDefiner());

			fd.getFinScheduleData().setFinServiceInstruction(finServInst);
		}

		for (FinServiceInstruction finSerList : fd.getFinScheduleData().getFinServiceInstructions()) {
			if (finSerList.getInstructionUID() == Long.MIN_VALUE) {
				if (serviceUID == Long.MIN_VALUE) {
					serviceUID = Long.valueOf(ReferenceGenerator.generateNewServiceUID());
				}
				finSerList.setInstructionUID(serviceUID);
			} else {
				serviceUID = finSerList.getInstructionUID();
			}
		}

		// Finance Stage Accounting Process
		// =======================================
		auditHeader = executeStageAccounting(auditHeader);
		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}

		if (liabilityRequest.isWorkflow()) {
			tableType = "_Temp";
		}

		if (liabilityRequest.isNewRecord()) {
			liabilityRequestDAO.save(liabilityRequest, tableType);
		} else {
			liabilityRequestDAO.update(liabilityRequest, tableType);
		}

		// set Finance Check List audit details to auditDetails
		// =======================================
		if (fd.getFinanceCheckList() != null && !fd.getFinanceCheckList().isEmpty()) {
			auditDetails.addAll(checkListDetailService.saveOrUpdate(fd, tableType, serviceUID));
		}

		// Save Document Details
		if (fd.getDocumentDetailsList() != null && fd.getDocumentDetailsList().size() > 0) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, tableType, fm, liabilityRequest.getFinEvent(), serviceUID);
			auditDetails.addAll(details);
		}

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * FinLiabilityReq by using LiabilityRequestDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtFinLiabilityReq by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "delete", false);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		LiabilityRequest liabilityRequest = (LiabilityRequest) auditHeader.getAuditDetail().getModelData();
		liabilityRequestDAO.delete(liabilityRequest, "");

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getLiabilityRequestById fetch the details by using LiabilityRequestDAO's getLiabilityRequestById method.
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return LiabilityRequest
	 */

	@Override
	public LiabilityRequest getLiabilityRequestById(long id) {
		return liabilityRequestDAO.getLiabilityRequestById(id, "_View");
	}

	/**
	 * getApprovedLiabilityRequestById fetch the details by using LiabilityRequestDAO's getLiabilityRequestById method .
	 * with parameter id and type as blank. it fetches the approved records from the FinLiabilityReq.
	 * 
	 * @param id (String)
	 * @return LiabilityRequest
	 */

	public LiabilityRequest getApprovedLiabilityRequestById(long id) {
		return liabilityRequestDAO.getLiabilityRequestById(id, "_AView");
	}

	@Override
	public String getProceedingWorkflow(String finType, String finEvent) {
		logger.debug("Entering");
		String nextFinEvent = liabilityRequestDAO.getProceedingWorkflow(finType, finEvent);
		logger.debug("Leaving");
		return nextFinEvent;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using liabilityRequestDAO.delete with
	 * parameters liabilityRequest,"" b) NEW Add new record in to main table by using liabilityRequestDAO.save with
	 * parameters liabilityRequest,"" c) EDIT Update record in the main table by using liabilityRequestDAO.update with
	 * parameters liabilityRequest,"" 3) Delete the record from the workFlow table by using liabilityRequestDAO.delete
	 * with parameters liabilityRequest,"_Temp" 4) Audit the record in to AuditHeader and AdtFinLiabilityReq by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtFinLiabilityReq
	 * by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 * @throws InterfaceException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */

	public AuditHeader doApprove(AuditHeader auditHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doApprove", false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		LiabilityRequest liabilityRequest = new LiabilityRequest();
		BeanUtils.copyProperties((LiabilityRequest) auditHeader.getAuditDetail().getModelData(), liabilityRequest);

		FinanceDetail fd = liabilityRequest.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		long serviceUID = Long.MIN_VALUE;
		for (FinServiceInstruction finServInst : schdData.getFinServiceInstructions()) {
			serviceUID = finServInst.getInstructionUID();
		}

		// Finance Stage Accounting Process
		// =======================================
		auditHeader = executeStageAccounting(auditHeader);
		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}

		if (liabilityRequest.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			liabilityRequestDAO.delete(liabilityRequest, "");

		} else {
			liabilityRequest.setRoleCode("");
			liabilityRequest.setNextRoleCode("");
			liabilityRequest.setTaskId("");
			liabilityRequest.setNextTaskId("");
			liabilityRequest.setWorkflowId(0);

			if (liabilityRequest.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				liabilityRequest.setRecordType("");
				liabilityRequestDAO.save(liabilityRequest, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				liabilityRequest.setRecordType("");
				liabilityRequestDAO.update(liabilityRequest, "");
			}
		}

		// set Check list details Audit
		// =======================================
		if (fd.getFinanceCheckList() != null && !fd.getFinanceCheckList().isEmpty()) {
			auditDetails.addAll(checkListDetailService.doApprove(fd, "", serviceUID));
		}

		// Save Document Details
		if (fd.getDocumentDetailsList() != null && fd.getDocumentDetailsList().size() > 0) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, "", fm, liabilityRequest.getFinEvent(), serviceUID);
			auditDetails.addAll(details);
			listDocDeletion(fd, "_Temp");
		}

		// Fee charges deletion
		List<AuditDetail> tempAuditDetailList = new ArrayList<AuditDetail>();
		finFeeChargesDAO.deleteChargesBatch(fm.getFinID(), fd.getModuleDefiner(), false, "_Temp");

		// Checklist Details delete
		// =======================================
		tempAuditDetailList.addAll(checkListDetailService.delete(fd, "_Temp", tranType));

		// Liability Request deletion
		liabilityRequestDAO.delete(liabilityRequest, "_Temp");
		if (StringUtils.equals(liabilityRequest.getFinEvent(), FinServiceEvent.INSCLAIM)) {
			Date curDate = SysParamUtil.getAppDate();
			FinanceSuspHead finSuspHead = financeSuspHeadDAO.getFinanceSuspHeadById(fm.getFinID(), "");
			boolean isSaveRcd = false;
			if (finSuspHead == null) {
				isSaveRcd = true;
				finSuspHead = new FinanceSuspHead();
				finSuspHead.setFinSuspDate(curDate);
				finSuspHead.setFinSuspTrfDate(curDate);
				finSuspHead.setFinID(fm.getFinID());
				finSuspHead.setFinReference(fm.getFinReference());
				finSuspHead.setFinBranch(fm.getFinBranch());
				finSuspHead.setFinType(fm.getFinType());
				finSuspHead.setCustId(fm.getCustID());
			}
			// Outstanding Suspense Amount Calculation
			BigDecimal totSuspAmt = BigDecimal.ZERO;
			for (FinanceScheduleDetail curSchd : schdData.getFinanceScheduleDetails()) {
				totSuspAmt = totSuspAmt.add(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));
			}
			finSuspHead.setFinCurSuspAmt(totSuspAmt);
			finSuspHead.setFinSuspAmt(totSuspAmt);
			finSuspHead.setManualSusp(true);
			finSuspHead.setFinIsInSusp(true);
			if (isSaveRcd) {
				financeSuspHeadDAO.save(finSuspHead, "");
			} else {
				financeSuspHeadDAO.update(finSuspHead, "");
			}
		}
		finStageAccountingLogDAO.update(fm.getFinID(), fd.getModuleDefiner(), false);
		// Audit Data
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		// Temp Table Audit
		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(liabilityRequest);
		auditHeader.setAuditDetails(tempAuditDetailList);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using liabilityRequestDAO.delete with parameters liabilityRequest,"_Temp" 3) Audit the record
	 * in to AuditHeader and AdtFinLiabilityReq by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 * @throws InterfaceException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */

	public AuditHeader doReject(AuditHeader auditHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "doApprove", false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}
		String tranType = PennantConstants.TRAN_DEL;

		LiabilityRequest liabilityRequest = (LiabilityRequest) auditHeader.getAuditDetail().getModelData();
		FinanceDetail fd = liabilityRequest.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		long serviceUID = Long.MIN_VALUE;
		for (FinServiceInstruction finServInst : schdData.getFinServiceInstructions()) {
			serviceUID = finServInst.getInstructionUID();
		}
		// Cancel All Transactions done by Finance Reference
		// =======================================
		cancelStageAccounting(fm.getFinID(), fd.getModuleDefiner());

		// Liability Request details
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		liabilityRequestDAO.delete(liabilityRequest, "_Temp");

		// Save Document Details
		if (fd.getDocumentDetailsList() != null && fd.getDocumentDetailsList().size() > 0) {
			for (DocumentDetails docDetails : fd.getDocumentDetailsList()) {
				if (StringUtils.isNotBlank(docDetails.getRecordType())) {
					docDetails.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				}
			}
			List<AuditDetail> details = fd.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, "_Temp", fm, liabilityRequest.getFinEvent(), serviceUID);
			auditHeader.setAuditDetails(details);
		}

		// Fee charges deletion
		finFeeChargesDAO.deleteChargesBatch(fm.getFinID(), fd.getModuleDefiner(), false, "_Temp");

		// Checklist Details delete
		// =======================================
		auditHeader.getAuditDetails().addAll(checkListDetailService.delete(fd, "_Temp", tranType));

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), fm.getExcludeFields());
		auditHeader.setAuditDetail(
				new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], fm.getBefImage(), fm));
		auditHeaderDAO.addAudit(auditHeader);

		// Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(liabilityRequest);

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
	private AuditHeader businessValidation(AuditHeader auditHeader, String method, boolean onlineRequest) {
		logger.debug("Entering");

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method,
				onlineRequest);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader = nextProcess(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Validation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details from the
	 * tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5) for any
	 * mismatch conditions Fetch the error details from liabilityRequestDAO.getErrorDetail with Error ID and language as
	 * parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader (auditHeader)
	 * @param boolean     onlineRequest
	 * @return auditHeader
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method, boolean onlineRequest) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		LiabilityRequest liabilityRequest = (LiabilityRequest) auditDetail.getModelData();

		LiabilityRequest tempLiabilityRequest = null;
		if (liabilityRequest.isWorkflow()) {
			tempLiabilityRequest = liabilityRequestDAO.getLiabilityRequestById(liabilityRequest.getId(), "_Temp");
		}
		LiabilityRequest befLiabilityRequest = liabilityRequestDAO.getLiabilityRequestById(liabilityRequest.getId(),
				"");

		LiabilityRequest oldLiabilityRequest = liabilityRequest.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = liabilityRequest.getFinReference();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (liabilityRequest.isNewRecord()) { // for New record or new record into work flow

			if (!liabilityRequest.isWorkflow()) {// With out Work flow only new records
				if (befLiabilityRequest != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow

				// NOC Issuance checking
				int count = liabilityRequestDAO.getFinareferenceCount(liabilityRequest.getFinID(), "_View");

				if (!ImplementationConstants.NOC_GENERATION_MULTIPLE && count > 0) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41041", errParm, valueParm), usrLanguage));
				} else if (liabilityRequest.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records
																										// type is new
					if (befLiabilityRequest != null || tempLiabilityRequest != null) { // if records already exists in
																						// the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41041", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befLiabilityRequest == null || tempLiabilityRequest != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!liabilityRequest.isWorkflow()) { // With out Work flow for update and delete

				if (befLiabilityRequest == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldLiabilityRequest != null
							&& !oldLiabilityRequest.getLastMntOn().equals(befLiabilityRequest.getLastMntOn())) {
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

				if (tempLiabilityRequest == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (oldLiabilityRequest != null && tempLiabilityRequest != null
						&& !oldLiabilityRequest.getLastMntOn().equals(tempLiabilityRequest.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}
		if (StringUtils.equals(liabilityRequest.getFinEvent(), FinServiceEvent.INSCLAIM)) {
			FinanceProfitDetail financeProfitDetail = profitDetailsDAO
					.getFinProfitDetailsById(liabilityRequest.getFinID());
			if (financeProfitDetail != null) {
				BigDecimal outStanding = financeProfitDetail.getTotalpriSchd()
						.add(financeProfitDetail.getTotalPftSchd()).subtract(financeProfitDetail.getTotalPriPaid())
						.subtract(financeProfitDetail.getTotalPftPaid());
				if (liabilityRequest.getInsClaimAmount().compareTo(outStanding) > 0) {
					String[] errParm1 = new String[2];
					String[] valueParm1 = new String[2];

					int finformatter = CurrencyUtil.getFormat(
							liabilityRequest.getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

					valueParm1[0] = PennantApplicationUtil
							.formateAmount(liabilityRequest.getInsClaimAmount(), finformatter).toString();
					errParm1[0] = PennantJavaUtil.getLabel("label_InsCLaim") + ":" + valueParm1[0];
					valueParm1[1] = PennantApplicationUtil.formateAmount(outStanding, finformatter).toString();
					errParm1[1] = PennantJavaUtil.getLabel("label_OutStandingPrincipal") + ":" + valueParm1[1];
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "30568", errParm1, valueParm1), usrLanguage));

				}
			}
		}

		// ###_0.2
		for (FinFlagsDetail finFlagsDetail : liabilityRequest.getFinanceDetail().getFinFlagsDetails()) {
			if (StringUtils.equals(finFlagsDetail.getFlagCode(), "NONOC")) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("41102", null)));
				break;
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !liabilityRequest.isWorkflow()) {
			auditDetail.setBefImage(befLiabilityRequest);
		}

		return auditDetail;
	}

	// Document Details List Maintenance
	public void listDocDeletion(FinanceDetail custDetails, String tableType) {
		documentDetailsDAO.deleteList(new ArrayList<DocumentDetails>(custDetails.getDocumentDetailsList()), tableType);
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		LiabilityRequest liabilityRequest = (LiabilityRequest) auditHeader.getAuditDetail().getModelData();
		FinanceDetail financeDetail = liabilityRequest.getFinanceDetail();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		financeMain.setRecordStatus(liabilityRequest.getRecordStatus());

		String auditTranType = "";
		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (financeMain.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// Finance Document Details
		if (financeDetail.getDocumentDetailsList() != null && financeDetail.getDocumentDetailsList().size() > 0) {
			auditDetailMap.put("DocumentDetails", setDocumentDetailsAuditData(financeDetail, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
		}

		// Finance Check List Details
		// =======================================
		List<FinanceCheckListReference> financeCheckList = financeDetail.getFinanceCheckList();

		if (StringUtils.equals(method, "saveOrUpdate")) {
			if (financeCheckList != null && !financeCheckList.isEmpty()) {
				auditDetails.addAll(
						checkListDetailService.getAuditDetail(auditDetailMap, financeDetail, auditTranType, method));
			}
		} else {
			String tableType = "_Temp";
			if (StringUtils.equals(financeDetail.getFinScheduleData().getFinanceMain().getRecordType(),
					PennantConstants.RECORD_TYPE_DEL)) {
				tableType = "";
			}

			String finReference = financeDetail.getFinScheduleData().getFinReference();
			financeCheckList = checkListDetailService.getCheckListByFinRef(finReference, tableType);
			financeDetail.setFinanceCheckList(financeCheckList);

			if (financeCheckList != null && !financeCheckList.isEmpty()) {
				auditDetails.addAll(
						checkListDetailService.getAuditDetail(auditDetailMap, financeDetail, auditTranType, method));
			}
		}

		financeDetail.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(liabilityRequest);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving ");

		return auditHeader;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setFinanceSuspHeadDAO(FinanceSuspHeadDAO financeSuspHeadDAO) {
		this.financeSuspHeadDAO = financeSuspHeadDAO;
	}

	public void setLiabilityRequestDAO(LiabilityRequestDAO liabilityRequestDAO) {
		this.liabilityRequestDAO = liabilityRequestDAO;
	}

	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

}