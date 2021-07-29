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
 * FileName    		:  ProvisionServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-05-2012    														*
 *                                                                  						*
 * Modified Date    :  31-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-05-2012       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.financemanagement.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ProvisionCalculationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.financemanagement.ProvisionDAO;
import com.pennant.backend.dao.financemanagement.ProvisionMovementDAO;
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
import com.pennant.cache.util.AccountingConfigCache;
import com.pennanttech.pennapps.core.InterfaceException;
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
	private ProvisionMovementDAO provisionMovementDAO;
	private FinanceTypeDAO financeTypeDAO;
	private ProvisionCalculationUtil provisionCalculationUtil;

	public ProvisionServiceImpl() {
		super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public ProvisionDAO getProvisionDAO() {
		return provisionDAO;
	}

	public void setProvisionDAO(ProvisionDAO provisionDAO) {
		this.provisionDAO = provisionDAO;
	}

	public ProvisionMovementDAO getProvisionMovementDAO() {
		return provisionMovementDAO;
	}

	public void setProvisionMovementDAO(ProvisionMovementDAO provisionMovementDAO) {
		this.provisionMovementDAO = provisionMovementDAO;
	}

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public ProvisionCalculationUtil getProvisionCalculationUtil() {
		return provisionCalculationUtil;
	}

	public void setProvisionCalculationUtil(ProvisionCalculationUtil provisionCalculationUtil) {
		this.provisionCalculationUtil = provisionCalculationUtil;
	}

	@Override
	public Provision getProvision() {
		return getProvisionDAO().getProvision();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table FinProvisions/FinProvisions_Temp
	 * by using ProvisionDAO's save method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using ProvisionDAO's update method 3) Audit the record in to AuditHeader and AdtFinProvisions
	 * by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 */
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

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * FinProvisions by using ProvisionDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtFinProvisions by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
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

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public Provision getProvisionById(String finReference, TableType tableType) {
		Provision provision = provisionDAO.getProvisionById(finReference, tableType, false);
		if (provision != null) {
			provision.setProvisionAmounts(provisionDAO.getProvisionAmounts(provision.getId(), tableType));
		}
		return provision;
	}

	/**
	 * getApprovedProvisionById fetch the details by using ProvisionDAO's getProvisionById method . with parameter id
	 * and type as blank. it fetches the approved records from the FinProvisions.
	 * 
	 * @param id
	 *            (String)
	 * @return Provision
	 */
	public Provision getApprovedProvisionById(String id) {
		return provisionDAO.getProvisionById(id, TableType.AVIEW, true);
	}

	@Override
	public FinanceProfitDetail getProfitDetailById(String finReference) {
		return getProfitDetailsDAO().getFinProfitDetailsById(finReference);
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getProvisionDAO().delete with
	 * parameters provision,"" b) NEW Add new record in to main table by using getProvisionDAO().save with parameters
	 * provision,"" c) EDIT Update record in the main table by using getProvisionDAO().update with parameters
	 * provision,"" 3) Delete the record from the workFlow table by using getProvisionDAO().delete with parameters
	 * provision,"_Temp" 4) Audit the record in to AuditHeader and AdtFinProvisions by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtFinProvisions by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		Provision provision = new Provision();
		BeanUtils.copyProperties((Provision) auditHeader.getAuditDetail().getModelData(), provision);

		provisionDAO.deleteAmounts(provision.getId(), TableType.TEMP_TAB);
		provisionDAO.delete(provision, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(provision.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(getProvisionDAO().getProvisionById(provision.getId(), TableType.MAIN_TAB, false));
		}

		if (provision.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getProvisionDAO().delete(provision, TableType.MAIN_TAB);
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
				provisionDAO.saveAmounts(provision.getProvisionAmounts(), TableType.MAIN_TAB, false);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				provision.setRecordType("");
				provisionDAO.update(provision, TableType.MAIN_TAB);
				provisionDAO.updateAmounts(provision.getProvisionAmounts(), TableType.MAIN_TAB);
			}
			saveProvisionMovement(provision);
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(provision);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	// Document Details List Maintenance
	public void listDocDeletion(FinanceDetail custDetails, String tableType) {
		getDocumentDetailsDAO().deleteList(new ArrayList<DocumentDetails>(custDetails.getDocumentDetailsList()),
				tableType);
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getProvisionDAO().delete with parameters provision,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtFinProvisions by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws InterfaceException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		Provision provision = (Provision) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = provision.getFinanceDetail().getFinScheduleData().getFinanceMain();
		String tranType = PennantConstants.TRAN_DEL;
		long serviceUID = Long.MIN_VALUE;
		for (FinServiceInstruction finServInst : provision.getFinanceDetail().getFinScheduleData()
				.getFinServiceInstructions()) {
			serviceUID = finServInst.getInstructionUID();
		}
		// Cancel All Transactions done by Finance Reference
		// =======================================
		cancelStageAccounting(financeMain.getFinReference(), FinServiceEvent.PROVISION);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		provisionDAO.deleteAmounts(provision.getId(), TableType.TEMP_TAB);
		provisionDAO.delete(provision, TableType.TEMP_TAB);

		// Save Document Details
		if (provision.getFinanceDetail().getDocumentDetailsList() != null
				&& provision.getFinanceDetail().getDocumentDetailsList().size() > 0) {
			for (DocumentDetails docDetails : provision.getFinanceDetail().getDocumentDetailsList()) {
				docDetails.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
			List<AuditDetail> details = provision.getFinanceDetail().getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, "_Temp",
					provision.getFinanceDetail().getFinScheduleData().getFinanceMain(),
					provision.getFinanceDetail().getModuleDefiner(), serviceUID);
			auditHeader.setAuditDetails(details);
			listDocDeletion(provision.getFinanceDetail(), "_Temp");
		}

		// Fee charges deletion
		getFinFeeChargesDAO().deleteChargesBatch(financeMain.getFinReference(),
				provision.getFinanceDetail().getModuleDefiner(), false, "_Temp");

		// Checklist Details delete
		// =======================================
		getCheckListDetailService().delete(provision.getFinanceDetail(), "_Temp", tranType);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from getProvisionDAO().getErrorDetail with Error ID and
	 * language as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

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
			tempProvision = getProvisionDAO().getProvisionById(provision.getId(), TableType.TEMP_TAB, false);
		}
		Provision befProvision = getProvisionDAO().getProvisionById(provision.getId(), TableType.MAIN_TAB, false);
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
		FinanceDetail financeDetail = provision.getFinanceDetail();
		FinScheduleData schdule = financeDetail.getFinScheduleData();
		FinanceMain financeMain = schdule.getFinanceMain();

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
				auditDetails.addAll(getCheckListDetailService().getAuditDetail(auditDetailMap, financeDetail,
						auditTranType, method));
			}
		} else {
			String tableType = "_Temp";
			if (PennantConstants.RECORD_TYPE_DEL.equals(schdule.getFinanceMain().getRecordType())) {
				tableType = "";
			}

			String finReference = schdule.getFinReference();
			financeCheckList = getCheckListDetailService().getCheckListByFinRef(finReference, tableType);
			financeDetail.setFinanceCheckList(financeCheckList);

			if (financeCheckList != null && !financeCheckList.isEmpty()) {
				auditDetails.addAll(getCheckListDetailService().getAuditDetail(auditDetailMap, financeDetail,
						auditTranType, method));
			}
		}

		financeDetail.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(provision);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving ");

		return auditHeader;
	}

	/**
	 * Method for Execute posting Details on Core Banking Side
	 * 
	 * @param auditHeader
	 * @param curBDay
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 */

	private long executeAccountingProcess(AuditHeader auditHeader) {
		logger.debug("Entering");

		Provision provision = new Provision();
		BeanUtils.copyProperties((Provision) auditHeader.getAuditDetail().getModelData(), provision);
		FinanceMain financeMain = provision.getFinanceDetail().getFinScheduleData().getFinanceMain();

		AEEvent aeEvent = new AEEvent();
		aeEvent.setAccountingEvent(AccountingEvent.PROVSN);
		Long accountingID = AccountingConfigCache.getCacheAccountSetID(financeMain.getFinType(),
				AccountingEvent.PROVSN, FinanceConstants.MODULEID_FINTYPE);

		aeEvent.setPostingUserBranch(auditHeader.getAuditBranchCode());
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

		aeEvent.getAcSetIDList().add(accountingID);
		postingsPreparationUtil.postAccounting(aeEvent);
		logger.debug("Leaving");

		return aeEvent.getLinkedTranId();
	}

	private void saveProvisionMovement(Provision provision) {
		provision.setProvisionId(provision.getId());
		provision.setId(Long.MIN_VALUE);
		provisionDAO.saveMovements(provision, TableType.MAIN_TAB);
	}

	@Override
	public List<ProvisionAmount> getProvisionAmounts(long id, TableType type) {
		return provisionDAO.getProvisionAmounts(id, type);
	}

}