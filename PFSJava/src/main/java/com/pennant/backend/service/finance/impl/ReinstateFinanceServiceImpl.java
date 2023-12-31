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
 * * FileName : ReinstateFinanceServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * *
 * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.finance.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.TaskOwnersDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.ReinstateFinanceDAO;
import com.pennant.backend.dao.reason.deatil.ReasonDetailDAO;
import com.pennant.backend.model.TaskOwners;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ReinstateFinance;
import com.pennant.backend.model.reason.details.ReasonDetailsLog;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.finance.ReinstateFinanceService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>ReinstateFinance</b>.<br>
 * 
 */
public class ReinstateFinanceServiceImpl extends GenericService<ReinstateFinance> implements ReinstateFinanceService {

	private static Logger logger = LogManager.getLogger(ReinstateFinanceServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private ReinstateFinanceDAO reinstateFinanceDAO;
	private FinanceMainDAO financeMainDAO;
	private TaskOwnersDAO taskOwnersDAO;
	protected ReasonDetailDAO reasonDetailDAO;
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	private FinServiceInstrutionDAO finServiceInstructionDAO;

	public ReinstateFinanceServiceImpl() {
		super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public ReinstateFinanceDAO getReinstateFinanceDAO() {
		return reinstateFinanceDAO;
	}

	public void setReinstateFinanceDAO(ReinstateFinanceDAO reinstateFinanceDAO) {
		this.reinstateFinanceDAO = reinstateFinanceDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public TaskOwnersDAO getTaskOwnersDAO() {
		return taskOwnersDAO;
	}

	public void setTaskOwnersDAO(TaskOwnersDAO taskOwnersDAO) {
		this.taskOwnersDAO = taskOwnersDAO;
	}

	public ReinstateFinance getReinstateFinance() {
		return getReinstateFinanceDAO().getReinstateFinance();
	}

	public ReinstateFinance getNewReinstateFinance() {
		return getReinstateFinanceDAO().getNewReinstateFinance();
	}

	public void setReasonDetailDAO(ReasonDetailDAO reasonDetailDAO) {
		this.reasonDetailDAO = reasonDetailDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * BMTReinstateFinances/BMTReinstateFinances_Temp by using ReinstateFinanceDAO's save method b) Update the Record in
	 * the table. based on the module workFlow Configuration. by using ReinstateFinanceDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtBMTReinstateFinances by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		ReinstateFinance rif = (ReinstateFinance) auditHeader.getAuditDetail().getModelData();

		List<FinServiceInstruction> serviceInstructions = getServiceInstructions(rif);

		long serviceUID = Long.MIN_VALUE;

		if (rif.getExtendedFieldRender() != null
				&& rif.getExtendedFieldRender().getInstructionUID() != Long.MIN_VALUE) {
			serviceUID = rif.getExtendedFieldRender().getInstructionUID();
		}

		for (FinServiceInstruction finServInst : serviceInstructions) {
			serviceUID = finServInst.getInstructionUID();
			if (ObjectUtils.isEmpty(finServInst.getInitiatedDate())) {
				finServInst.setInitiatedDate(SysParamUtil.getAppDate());
			}
		}

		List<AuditDetail> auditDetails = new ArrayList<>();

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		String tableType = "";
		ReinstateFinance reinstateFinance = (ReinstateFinance) auditHeader.getAuditDetail().getModelData();

		if (reinstateFinance.isWorkflow()) {
			tableType = "_Temp";
		}

		if (reinstateFinance.isNewRecord()) {
			reinstateFinance.setFinReference(getReinstateFinanceDAO().save(reinstateFinance, tableType));
			auditHeader.getAuditDetail().setModelData(reinstateFinance);
			auditHeader.setAuditReference(reinstateFinance.getFinReference());
		} else {
			reinstateFinanceDAO.update(reinstateFinance, tableType);
		}

		// FinServiceInstrution
		if (CollectionUtils.isNotEmpty(reinstateFinance.getFinServiceInstructions())
				&& reinstateFinance.isNewRecord()) {
			finServiceInstructionDAO.saveList(reinstateFinance.getFinServiceInstructions(), tableType);
		}

		// Extended field Details
		if (reinstateFinance.getExtendedFieldRender() != null) {
			List<AuditDetail> details = reinstateFinance.getAuditDetailMap().get("ExtendedFieldDetails");

			details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
					ExtendedFieldConstants.MODULE_LOAN, reinstateFinance.getExtendedFieldHeader().getEvent(), tableType,
					serviceUID);
			auditDetails.addAll(details);
		}

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	private List<FinServiceInstruction> getServiceInstructions(ReinstateFinance rif) {
		logger.debug(Literal.ENTERING);

		List<FinServiceInstruction> serviceInstructions = rif.getFinServiceInstructions();

		if (CollectionUtils.isEmpty(serviceInstructions)) {
			FinServiceInstruction finServInst = new FinServiceInstruction();
			finServInst.setFinID(rif.getFinID());
			finServInst.setFinReference(rif.getFinReference());
			finServInst.setFinEvent(rif.getFinEvent());

			rif.setFinServiceInstruction(finServInst);
		}

		for (FinServiceInstruction serviceInstruction : rif.getFinServiceInstructions()) {
			if (serviceInstruction.getInstructionUID() == Long.MIN_VALUE) {
				serviceInstruction.setInstructionUID(Long.valueOf(ReferenceGenerator.generateNewServiceUID()));
			}

			if (StringUtils.isEmpty(rif.getFinEvent()) || FinServiceEvent.ORG.equals(rif.getFinEvent())) {
				if (!FinServiceEvent.ORG.equals(serviceInstruction.getFinEvent())
						&& !StringUtils.contains(serviceInstruction.getFinEvent(), "_O")) {
					serviceInstruction.setFinEvent(serviceInstruction.getFinEvent().concat("_O"));
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return rif.getFinServiceInstructions();
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * BMTReinstateFinances by using ReinstateFinanceDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtBMTReinstateFinances by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<>();

		auditHeader = businessValidation(auditHeader, "delete");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		ReinstateFinance reinstateFinance = (ReinstateFinance) auditHeader.getAuditDetail().getModelData();
		reinstateFinanceDAO.delete(reinstateFinance, "");

		finServiceInstructionDAO.deleteList(reinstateFinance.getFinID(), reinstateFinance.getFinEvent(), "_Temp");

		// Extended field Render Details.
		List<AuditDetail> extendedDetails = reinstateFinance.getAuditDetailMap().get("ExtendedFieldDetails");
		if (extendedDetails != null && extendedDetails.size() > 0) {
			auditDetails.addAll(extendedFieldDetailsService.delete(reinstateFinance.getExtendedFieldHeader(),
					reinstateFinance.getFinReference(), reinstateFinance.getExtendedFieldRender().getSeqNo(), "_Temp",
					auditHeader.getAuditTranType(), extendedDetails));
		}

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getReinstateFinanceById fetch the details by using ReinstateFinanceDAO's getReinstateFinanceById method.
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return ReinstateFinance
	 */
	@Override
	public ReinstateFinance getReinstateFinanceById(long finID) {
		return getReinstateFinanceDAO().getReinstateFinanceById(finID, "_View");
	}

	/**
	 * getApprovedReinstateFinanceById fetch the details by using ReinstateFinanceDAO's getReinstateFinanceById method .
	 * with parameter id and type as blank. it fetches the approved records from the BMTReinstateFinances.
	 * 
	 * @param id (String)
	 * @return ReinstateFinance
	 */
	public ReinstateFinance getApprovedReinstateFinanceById(long finID) {
		return getReinstateFinanceDAO().getReinstateFinanceById(finID, "_AView");
	}

	public ReinstateFinance getFinanceDetailsById(long finID) {
		return getReinstateFinanceDAO().getFinanceDetailsById(finID);
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getReinstateFinanceDAO().delete with
	 * parameters reinstateFinance,"" b) NEW Add new record in to main table by using getReinstateFinanceDAO().save with
	 * parameters reinstateFinance,"" c) EDIT Update record in the main table by using getReinstateFinanceDAO().update
	 * with parameters reinstateFinance,"" 3) Delete the record from the workFlow table by using
	 * getReinstateFinanceDAO().delete with parameters reinstateFinance,"_Temp" 4) Audit the record in to AuditHeader
	 * and AdtBMTReinstateFinances by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to
	 * AuditHeader and AdtBMTReinstateFinances by using auditHeaderDAO.addAudit(auditHeader) based on the transaction
	 * Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		ReinstateFinance reinstateFinance = new ReinstateFinance();
		BeanUtils.copyProperties((ReinstateFinance) auditHeader.getAuditDetail().getModelData(), reinstateFinance);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		List<FinServiceInstruction> serviceInstructions = getServiceInstructions(reinstateFinance);

		long serviceUID = Long.MIN_VALUE;

		if (reinstateFinance.getExtendedFieldRender() != null
				&& reinstateFinance.getExtendedFieldRender().getInstructionUID() != Long.MIN_VALUE) {
			serviceUID = reinstateFinance.getExtendedFieldRender().getInstructionUID();
		}

		for (FinServiceInstruction finServInst : serviceInstructions) {
			serviceUID = finServInst.getInstructionUID();
			if (ObjectUtils.isEmpty(finServInst.getInitiatedDate())) {
				finServInst.setInitiatedDate(SysParamUtil.getAppDate());
			}
		}

		FinanceMain financeMain = financeMainDAO.getRejectFinanceMainByRef(reinstateFinance.getFinReference());
		financeMain.setApproved(null);
		financeMain.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
		// Workflow fields
		// String finNextRoleCode = reinstateFinance.getLovDescNextRoleCode() ;
		financeMain.setFinIsActive(true);

		// Workflow fields
		String finNextRoleCode = financeMain.getRoleCode();
		String nextTaskId = financeMain.getTaskId();
		financeMain.setNextRoleCode(finNextRoleCode);
		financeMain.setNextTaskId(nextTaskId == null ? null : nextTaskId.concat(";"));

		if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
			financeMainDAO.updateRejectFinanceMain(financeMain, TableType.TEMP_TAB, false);
		}

		if (CollectionUtils.isNotEmpty(reinstateFinance.getFinServiceInstructions())) {
			finServiceInstructionDAO.saveList(reinstateFinance.getFinServiceInstructions(), "");
		}

		// Extended field Render Details.
		List<AuditDetail> details = reinstateFinance.getAuditDetailMap().get("ExtendedFieldDetails");
		if (reinstateFinance.getExtendedFieldRender() != null) {
			details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
					ExtendedFieldConstants.MODULE_LOAN, reinstateFinance.getExtendedFieldHeader().getEvent(), "",
					serviceUID);

			auditDetails.addAll(details);
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		// getFinanceMainDAO().save(financeMain, TableType.TEMP_TAB, false);

		// getReinstateFinanceDAO().processReInstateFinance(financeMain); //Moving finance details from rejected tables
		// to actual finance tables

		TaskOwners taskOwner = new TaskOwners();
		taskOwner.setReference(financeMain.getFinReference());
		taskOwner.setRoleCode(finNextRoleCode);
		taskOwner.setActualOwner(0);
		taskOwner.setCurrentOwner(0);
		taskOwner.setProcessed(false);

		if (StringUtils.equalsIgnoreCase("N", SysParamUtil.getValueAsString("ALLOW_LOAN_APP_LOCK"))) {
			taskOwnersDAO.delete(taskOwner); // delete the existing task owners for finance

			taskOwnersDAO.save(taskOwner); // save new task owner for finance
		}

		getReinstateFinanceDAO().delete(reinstateFinance, "_Temp");

		getReinstateFinanceDAO().deleteRejectFinance(reinstateFinance);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(reinstateFinance);
		getAuditHeaderDAO().addAudit(auditHeader);

		finServiceInstructionDAO.deleteList(reinstateFinance.getFinID(), reinstateFinance.getFinEvent(), "_Temp");

		if (details != null && details.size() > 0) {
			extendedFieldDetailsService.delete(reinstateFinance.getExtendedFieldHeader(),
					reinstateFinance.getFinReference(), reinstateFinance.getExtendedFieldRender().getSeqNo(), "_Temp",
					auditHeader.getAuditTranType(), details);
		}

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getReinstateFinanceDAO().delete with parameters reinstateFinance,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtBMTReinstateFinances by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<>();

		auditHeader = businessValidation(auditHeader, "doReject");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		ReinstateFinance reinstateFinance = (ReinstateFinance) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getReinstateFinanceDAO().delete(reinstateFinance, "_Temp");

		finServiceInstructionDAO.deleteList(reinstateFinance.getFinID(), reinstateFinance.getFinEvent(), "_Temp");
		// Extended field Render Details.
		List<AuditDetail> extendedDetails = reinstateFinance.getAuditDetailMap().get("ExtendedFieldDetails");
		if (extendedDetails != null && extendedDetails.size() > 0) {
			auditDetails.addAll(extendedFieldDetailsService.delete(reinstateFinance.getExtendedFieldHeader(),
					reinstateFinance.getFinReference(), reinstateFinance.getExtendedFieldRender().getSeqNo(), "_Temp",
					auditHeader.getAuditTranType(), extendedDetails));
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		auditHeader = getAuditDetails(auditHeader, method);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		ReinstateFinance reinstateFinance = (ReinstateFinance) auditDetail.getModelData();

		// Extended field details Validation
		if (reinstateFinance.getExtendedFieldRender() != null) {
			List<AuditDetail> details = reinstateFinance.getAuditDetailMap().get("ExtendedFieldDetails");
			ExtendedFieldHeader extHeader = reinstateFinance.getExtendedFieldHeader();
			details = extendedFieldDetailsService.validateExtendedDdetails(extHeader, details, method,
					auditHeader.getUsrLanguage());
			auditDetails.addAll(details);
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();

		ReinstateFinance reinstateFinance = (ReinstateFinance) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (reinstateFinance.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// Extended Field Details
		if (reinstateFinance.getExtendedFieldRender() != null) {
			auditDetailMap.put("ExtendedFieldDetails",
					extendedFieldDetailsService.setExtendedFieldsAuditData(reinstateFinance.getExtendedFieldHeader(),
							reinstateFinance.getExtendedFieldRender(), auditTranType, method,
							reinstateFinance.getExtendedFieldHeader().getModuleName()));
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldDetails"));
		}
		reinstateFinance.setAuditDetailMap(auditDetailMap);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getReinstateFinanceDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());

		ReinstateFinance reinstateFinance = (ReinstateFinance) auditDetail.getModelData();

		ReinstateFinance tempReinstateFinance = null;
		if (reinstateFinance.isWorkflow()) {
			tempReinstateFinance = getReinstateFinanceDAO().getReinstateFinanceById(reinstateFinance.getFinID(),
					"_Temp");
		}

		ReinstateFinance oldReinstateFinance = reinstateFinance.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = reinstateFinance.getFinReference();

		errParm[0] = PennantJavaUtil.getLabel("label_ReinstateFinance_FinReference") + ":" + valueParm[0];

		if (reinstateFinance.isNewRecord()) { // for New record or new record into work flow

			if (!reinstateFinance.isWorkflow()) {// With out Work flow only new records
				/*
				 * if (befReinstateFinance != null) { // Record Already Exists in the table // then error
				 * auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null)); }
				 */
			} else { // with work flow
				if (reinstateFinance.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type
																									// is new
					if (tempReinstateFinance != null) { // if records already exists in
						// the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (tempReinstateFinance != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (reinstateFinance.isWorkflow()) { // With out Work flow for update and

				if (tempReinstateFinance == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempReinstateFinance != null && oldReinstateFinance != null
						&& !oldReinstateFinance.getLastMntOn().equals(tempReinstateFinance.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !reinstateFinance.isWorkflow()) {
			auditDetail.setBefImage(oldReinstateFinance);
		}

		logger.debug("Leaving");
		return auditDetail;
	}

	/*
	 * Method to get the schedule change module list from the ScheduleEffectModule table
	 * 
	 */
	public List<String> getScheduleEffectModuleList(boolean schdChangeReq) {
		return getFinanceMainDAO().getScheduleEffectModuleList(schdChangeReq);
	}

	@Override
	public List<ReasonDetailsLog> getResonDetailsLog(String reference) {
		return reasonDetailDAO.getReasonDetailsLog(reference);
	}

	@Override
	public List<FinServiceInstruction> getFinServiceInstructions(long finID, String type, String finEvent) {
		return this.finServiceInstructionDAO.getFinServiceInstructions(finID, type, finEvent);
	}

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	public void setFinServiceInstructionDAO(FinServiceInstrutionDAO finServiceInstructionDAO) {
		this.finServiceInstructionDAO = finServiceInstructionDAO;
	}
}