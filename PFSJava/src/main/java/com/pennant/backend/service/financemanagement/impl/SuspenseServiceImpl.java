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
 * FileName    		:  SuspenseServiceImpl.java                                                   * 	  
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.service.financemanagement.SuspenseService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.InterfaceException;

/**
 * Service implementation for methods that depends on <b>FinanceSuspHead</b>.<br>
 * 
 */
public class SuspenseServiceImpl extends GenericFinanceDetailService implements SuspenseService {

	private static final Logger			logger	= Logger.getLogger(SuspenseServiceImpl.class);

	private FinanceSuspHeadDAO			financeSuspHeadDAO;
	private FinanceReferenceDetailDAO	financeReferenceDetailDAO;

	public SuspenseServiceImpl() {
		super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinanceSuspHeadDAO getFinanceSuspHeadDAO() {
		return financeSuspHeadDAO;
	}

	public void setFinanceSuspHeadDAO(FinanceSuspHeadDAO financeSuspHeadDAO) {
		this.financeSuspHeadDAO = financeSuspHeadDAO;
	}

	public FinanceReferenceDetailDAO getFinanceReferenceDetailDAO() {
		return financeReferenceDetailDAO;
	}

	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
	}

	@Override
	public FinanceSuspHead getFinanceSuspHead() {
		return getFinanceSuspHeadDAO().getFinanceSuspHead();
	}

	@Override
	public FinanceSuspHead getNewFinanceSuspHead() {
		return getFinanceSuspHeadDAO().getNewFinanceSuspHead();
	}

	/**
	 * getFinanceSuspHeadById fetch the details by using FinanceSuspHeadDAO's getFinanceSuspHeadById method.
	 * 
	 * @param finRef
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceSuspHead
	 */
	@Override
	public FinanceSuspHead getFinanceSuspHeadById(String finRef, boolean isEnquiry, String userRole, String procEdtEvent) {
		FinanceSuspHead suspHead = getFinanceSuspHeadDAO().getFinanceSuspHeadById(finRef, "_View");
		if (suspHead == null) {
			return null;
		}

		if (isEnquiry) {
			suspHead.setSuspDetailsList(getFinanceSuspHeadDAO().getFinanceSuspDetailsListById(finRef));
			suspHead.setSuspPostingsList(getPostingsDAO().getPostingsByFinRefAndEvent(suspHead.getFinReference(),
					"'PIS_NORM','NORM_PIS'", true, ""));
		}

		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();

		scheduleData.setFinanceMain(getFinanceMainDAO().getFinanceMainById(finRef, "_AView", false));

		if (scheduleData.getFinanceMain() != null) {

			if (StringUtils.isNotBlank(suspHead.getRecordType())) {
				scheduleData.getFinanceMain().setNewRecord(false);
			}

			if (StringUtils.isNotEmpty(suspHead.getRecordType())) {
				scheduleData.getFinanceMain().setNewRecord(false);
			}

			//Finance Schedule Details
			scheduleData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(finRef, "",
					false));
			scheduleData.setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails(finRef, "",
					false));

			scheduleData.setFeeRules(getFinFeeChargesDAO().getFeeChargesByFinRef(finRef, procEdtEvent, false, ""));
			scheduleData.setRepayDetails(getFinanceRepaymentsDAO().getFinRepayListByFinRef(finRef, false, ""));
			scheduleData.setPenaltyDetails(getRecoveryDAO().getFinancePenaltysByFinRef(finRef, ""));

			scheduleData.setFinanceType(getFinanceTypeDAO().getFinanceTypeByID(
					scheduleData.getFinanceMain().getFinType(), "_AView"));

			//Finance Customer Details			
			if (scheduleData.getFinanceMain().getCustID() != 0
					&& scheduleData.getFinanceMain().getCustID() != Long.MIN_VALUE) {
				financeDetail.setCustomerDetails(getCustomerDetailsService().getCustomerDetailsById(
						scheduleData.getFinanceMain().getCustID(), true, "_View"));
			}

			//Finance Agreement Details	
			//=======================================
			String finType = scheduleData.getFinanceType().getFinType();
			financeDetail.setAggrementList(getAgreementDetailService().getAggrementDetailList(finType, procEdtEvent,
					userRole));

			// Finance Check List Details 
			//=======================================
			getCheckListDetailService().setFinanceCheckListDetails(financeDetail, finType, procEdtEvent, userRole);

			//Finance Fee Charge Details
			//=======================================
			List<Long> accSetIdList = new ArrayList<Long>();
			accSetIdList.addAll(getFinanceReferenceDetailDAO().getRefIdListByFinType(finType, procEdtEvent, null,
					"_ACView"));
			if (!accSetIdList.isEmpty()) {
				financeDetail.setFeeCharges(getTransactionEntryDAO().getListFeeChargeRules(accSetIdList,
						AccountEventConstants.ACCEVENT_NORM_PIS, "_AView", 0));
			}

			//Finance Stage Accounting Posting Details 
			//=======================================
			financeDetail.setStageTransactionEntries(getTransactionEntryDAO().getListTransactionEntryByRefType(finType,
					StringUtils.isEmpty(procEdtEvent) ? FinanceConstants.FINSER_EVENT_ORG : procEdtEvent,
					FinanceConstants.PROCEDT_STAGEACC, userRole, "_AEView", true));

			// Docuument Details
			financeDetail.setDocumentDetailsList(getDocumentDetailsDAO().getDocumentDetailsByRef(finRef,
					FinanceConstants.MODULE_NAME, procEdtEvent, "_View"));

			suspHead.setFinanceDetail(financeDetail);
		}
		return suspHead;
	}

	/**
	 * getSuspFinanceList fetch the FinReference details by using FinanceSuspHeadDAO's .
	 * 
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceSuspHead
	 */
	@Override
	public List<String> getSuspFinanceList() {
		return getFinanceSuspHeadDAO().getSuspFinanceList();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * FinFinanceSuspHeads/FinFinanceSuspHeads_Temp by using FinanceSuspHeadDAO's save method b) Update the Record in
	 * the table. based on the module workFlow Configuration. by using FinanceSuspHeadDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtFinFinanceSuspHeads by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) throws InterfaceException, IllegalAccessException,
			InvocationTargetException {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType = "";
		FinanceSuspHead financeSuspHead = (FinanceSuspHead) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeSuspHead.getFinanceDetail().getFinScheduleData().getFinanceMain();
		String finReference = financeMain.getFinReference();
		Date curBDay = DateUtility.getAppDate();

		//Finance Stage Accounting Process
		//=======================================
		if (financeSuspHead.getFinanceDetail().getStageAccountingList() != null
				&& financeSuspHead.getFinanceDetail().getStageAccountingList().size() > 0) {

			List<ReturnDataSet> list = new ArrayList<ReturnDataSet>();
			auditHeader = executeStageAccounting(auditHeader, list);
			if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
				return auditHeader;
			}
			list = null;
		}

		if (financeSuspHead.isWorkflow()) {
			tableType = "_Temp";
		}

		FinanceProfitDetail profitDetail = null;
		if (!financeSuspHead.isWorkflow()) {
			profitDetail = getProfitDetailsDAO().getFinProfitDetailsById(finReference);

			AEEvent aeEvent = AEAmounts.procAEAmounts(financeMain, financeSuspHead.getFinanceDetail().getFinScheduleData()
					.getFinanceScheduleDetails(), profitDetail, AccountEventConstants.ACCEVENT_WRITEOFF, curBDay,
					financeMain.getMaturityDate());
			AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

			HashMap<String, Object> dataMap = amountCodes.getDeclaredFieldValues();
			aeEvent.setDataMap(dataMap);

			try {
				aeEvent = getPostingsPreparationUtil().processPostingDetails(aeEvent);
			} catch (AccountNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (!aeEvent.isPostingSucess()) {
				String errParm = aeEvent.getErrorMessage();
				throw new InterfaceException("9999", errParm);
			}

		}

		if (financeSuspHead.isManualSusp()) {
			financeSuspHead.setFinIsInSusp(true);
			financeSuspHead.setFinSuspTrfDate(financeSuspHead.getFinSuspDate());
		}

		if (financeSuspHead.isNew()) {
			getFinanceSuspHeadDAO().save(financeSuspHead, tableType);
			auditHeader.getAuditDetail().setModelData(financeSuspHead);
			auditHeader.setAuditReference(financeSuspHead.getFinReference());
		} else {
			getFinanceSuspHeadDAO().update(financeSuspHead, tableType);
		}

		// Save Fee Charges List
		//=======================================
		if (StringUtils.isNotBlank(tableType)) {
			getFinFeeChargesDAO().deleteChargesBatch(financeMain.getFinReference(),
					financeSuspHead.getFinanceDetail().getModuleDefiner(), false, tableType);
		}
		saveFeeChargeList(financeSuspHead.getFinanceDetail().getFinScheduleData(), financeSuspHead.getFinanceDetail()
				.getModuleDefiner(), false, tableType);

		// Save Document Details
		if (financeSuspHead.getFinanceDetail().getDocumentDetailsList() != null
				&& financeSuspHead.getFinanceDetail().getDocumentDetailsList().size() > 0) {
			List<AuditDetail> details = financeSuspHead.getFinanceDetail().getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, tableType, financeSuspHead.getFinanceDetail()
					.getFinScheduleData().getFinanceMain(), financeSuspHead.getFinanceDetail().getModuleDefiner());
			auditDetails.addAll(details);
		}

		// set Finance Check List audit details to auditDetails
		//=======================================
		if (financeSuspHead.getFinanceDetail().getFinanceCheckList() != null
				&& !financeSuspHead.getFinanceDetail().getFinanceCheckList().isEmpty()) {
			auditDetails
					.addAll(getCheckListDetailService().saveOrUpdate(financeSuspHead.getFinanceDetail(), tableType));
		}

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * FinFinanceSuspHeads by using FinanceSuspHeadDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtFinFinanceSuspHeads by using auditHeaderDAO.addAudit(auditHeader)
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

		FinanceSuspHead financeSuspHead = (FinanceSuspHead) auditHeader.getAuditDetail().getModelData();
		getFinanceSuspHeadDAO().delete(financeSuspHead, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getFinanceSuspHeadDAO().delete with
	 * parameters financeSuspHead,"" b) NEW Add new record in to main table by using getFinanceSuspHeadDAO().save with
	 * parameters financeSuspHead,"" c) EDIT Update record in the main table by using getFinanceSuspHeadDAO().update
	 * with parameters financeSuspHead,"" 3) Delete the record from the workFlow table by using
	 * getFinanceSuspHeadDAO().delete with parameters financeSuspHead,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtFinFinanceSuspHeads by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to
	 * AuditHeader and AdtFinFinanceSuspHeads by using auditHeaderDAO.addAudit(auditHeader) based on the transaction
	 * Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) throws InterfaceException, IllegalAccessException,
			InvocationTargetException {
		logger.debug("Entering");
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		FinanceSuspHead financeSuspHead = new FinanceSuspHead();
		BeanUtils.copyProperties((FinanceSuspHead) auditHeader.getAuditDetail().getModelData(), financeSuspHead);
		String finReference = financeSuspHead.getFinReference();

		//Finance Stage Accounting Process
		//=======================================
		if (financeSuspHead.getFinanceDetail().getStageAccountingList() != null
				&& financeSuspHead.getFinanceDetail().getStageAccountingList().size() > 0) {

			List<ReturnDataSet> list = new ArrayList<ReturnDataSet>();
			auditHeader = executeStageAccounting(auditHeader, list);
			if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
				return auditHeader;
			}
			list = null;
		}

		if (financeSuspHead.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getFinanceSuspHeadDAO().delete(financeSuspHead, "");
		} else {
			financeSuspHead.setRoleCode("");
			financeSuspHead.setNextRoleCode("");
			financeSuspHead.setTaskId("");
			financeSuspHead.setNextTaskId("");
			financeSuspHead.setWorkflowId(0);

			if (financeSuspHead.isManualSusp()) {
				financeSuspHead.setFinIsInSusp(true);
				financeSuspHead.setFinSuspTrfDate(financeSuspHead.getFinSuspDate());
			}

			if (financeSuspHead.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				financeSuspHead.setRecordType("");
				getFinanceSuspHeadDAO().save(financeSuspHead, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				financeSuspHead.setRecordType("");
				getFinanceSuspHeadDAO().update(financeSuspHead, "");
			}
		}

		// Save Fee Charges List
		//=======================================
		saveFeeChargeList(financeSuspHead.getFinanceDetail().getFinScheduleData(), financeSuspHead.getFinanceDetail()
				.getModuleDefiner(), false, "");

		// Save Document Details
		if (financeSuspHead.getFinanceDetail().getDocumentDetailsList() != null
				&& financeSuspHead.getFinanceDetail().getDocumentDetailsList().size() > 0) {
			List<AuditDetail> details = financeSuspHead.getFinanceDetail().getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, "", financeSuspHead.getFinanceDetail()
					.getFinScheduleData().getFinanceMain(), financeSuspHead.getFinanceDetail().getModuleDefiner());
			auditDetails.addAll(details);
		}

		// set Finance Check List audit details to auditDetails
		//=======================================
		if (financeSuspHead.getFinanceDetail().getFinanceCheckList() != null
				&& !financeSuspHead.getFinanceDetail().getFinanceCheckList().isEmpty()) {
			auditDetails.addAll(getCheckListDetailService().doApprove(financeSuspHead.getFinanceDetail(), ""));
		}

		//Finance Profit Details Updation
		FinanceProfitDetail finPftDetail = getProfitDetailsDAO().getFinProfitDetailsById(finReference);
		Date curBussDate = DateUtility.getAppDate();

		FinanceMain financeMain = getFinanceMainDAO().getFinanceMainById(finReference, "", false);
		List<FinanceScheduleDetail> scheduleDetailList = getFinanceScheduleDetailDAO().getFinSchdDetailsForBatch(
				finReference);

		//Commitment Set Non-Performing Status
		if (StringUtils.isNotBlank(financeMain.getFinCommitmentRef())) {
			if (financeSuspHead.isManualSusp() || financeSuspHead.isFinIsInSusp()) {
				getCommitmentDAO().updateNonPerformStatus(financeMain.getFinCommitmentRef());
			}
		}

		// Document Details delete
		//=======================================
		listDocDeletion(financeSuspHead.getFinanceDetail(), "_Temp");

		// Checklist Details delete
		//=======================================
		getCheckListDetailService().delete(financeSuspHead.getFinanceDetail(), "_Temp", tranType);

		// Fee charges deletion
		getFinFeeChargesDAO().deleteChargesBatch(financeMain.getFinReference(),
				financeSuspHead.getFinanceDetail().getModuleDefiner(), false, "_Temp");

		finPftDetail = getAccrualService().calProfitDetails(financeMain, scheduleDetailList, finPftDetail, curBussDate);

		String worstSts = getCustomerStatusCodeDAO().getFinanceStatus(finReference, false);
		finPftDetail.setFinWorstStatus(worstSts);
		getProfitDetailsDAO().update(finPftDetail, false);

		getFinanceSuspHeadDAO().delete(financeSuspHead, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(financeSuspHead);

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		//updating the processed with 1 in finstageAccountingLog
		getFinStageAccountingLogDAO().update(financeMain.getFinReference(),
				financeSuspHead.getFinanceDetail().getModuleDefiner(), false);

		logger.debug("Leaving");

		return auditHeader;
	}

	//Document Details List Maintenance
	public void listDocDeletion(FinanceDetail custDetails, String tableType) {
		getDocumentDetailsDAO().deleteList(new ArrayList<DocumentDetails>(custDetails.getDocumentDetailsList()),
				tableType);
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getFinanceSuspHeadDAO().delete with parameters financeSuspHead,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtFinFinanceSuspHeads by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws InterfaceException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FinanceSuspHead financeSuspHead = (FinanceSuspHead) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeSuspHead.getFinanceDetail().getFinScheduleData().getFinanceMain();
		String tranType = PennantConstants.TRAN_DEL;

		// Cancel All Transactions done by Finance Reference
		//=======================================
		cancelStageAccounting(financeMain.getFinReference(), FinanceConstants.FINSER_EVENT_SUSPHEAD);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getFinanceSuspHeadDAO().delete(financeSuspHead, "_Temp");

		// Save Document Details
		if (financeSuspHead.getFinanceDetail().getDocumentDetailsList() != null
				&& financeSuspHead.getFinanceDetail().getDocumentDetailsList().size() > 0) {
			for (DocumentDetails docDetails : financeSuspHead.getFinanceDetail().getDocumentDetailsList()) {
				docDetails.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
			List<AuditDetail> details = financeSuspHead.getFinanceDetail().getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, "_Temp", financeSuspHead.getFinanceDetail()
					.getFinScheduleData().getFinanceMain(), financeSuspHead.getFinanceDetail().getModuleDefiner());
			auditHeader.setAuditDetails(details);
			listDocDeletion(financeSuspHead.getFinanceDetail(), "_Temp");
		}

		// Fee charges deletion
		getFinFeeChargesDAO().deleteChargesBatch(financeMain.getFinReference(),
				financeSuspHead.getFinanceDetail().getModuleDefiner(), false, "_Temp");

		// Checklist Details delete
		//=======================================
		getCheckListDetailService().delete(financeSuspHead.getFinanceDetail(), "_Temp", tranType);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from getFinanceSuspHeadDAO().getErrorDetail with Error ID and
	 * language as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinanceSuspHead financeSuspHead = (FinanceSuspHead) auditHeader.getAuditDetail().getModelData();
		FinanceDetail financeDetail = financeSuspHead.getFinanceDetail();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		String auditTranType = "";
		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (financeMain.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		//Finance Document Details
		if (financeDetail.getDocumentDetailsList() != null && financeDetail.getDocumentDetailsList().size() > 0) {
			auditDetailMap.put("DocumentDetails", setDocumentDetailsAuditData(financeDetail, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
		}

		//Finance Check List Details 
		//=======================================
		List<FinanceCheckListReference> financeCheckList = financeDetail.getFinanceCheckList();

		if (StringUtils.equals(method, "saveOrUpdate")) {
			if (financeCheckList != null && !financeCheckList.isEmpty()) {
				auditDetails.addAll(getCheckListDetailService().getAuditDetail(auditDetailMap, financeDetail,
						auditTranType, method));
			}
		} else {
			String tableType = "_Temp";
			if (financeDetail.getFinScheduleData().getFinanceMain().getRecordType()
					.equals(PennantConstants.RECORD_TYPE_DEL)) {
				tableType = "";
			}

			String finReference = financeDetail.getFinScheduleData().getFinReference();
			financeCheckList = getCheckListDetailService().getCheckListByFinRef(finReference, tableType);
			financeDetail.setFinanceCheckList(financeCheckList);

			if (financeCheckList != null && !financeCheckList.isEmpty()) {
				auditDetails.addAll(getCheckListDetailService().getAuditDetail(auditDetailMap, financeDetail,
						auditTranType, method));
			}
		}

		financeDetail.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(financeSuspHead);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving ");

		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());
		FinanceSuspHead financeSuspHead = (FinanceSuspHead) auditDetail.getModelData();

		FinanceSuspHead tempFinanceSuspHead = null;
		if (financeSuspHead.isWorkflow()) {
			tempFinanceSuspHead = getFinanceSuspHeadDAO().getFinanceSuspHeadById(financeSuspHead.getId(), "_Temp");
		}
		FinanceSuspHead befFinanceSuspHead = getFinanceSuspHeadDAO()
				.getFinanceSuspHeadById(financeSuspHead.getId(), "");
		FinanceSuspHead oldFinanceSuspHead = financeSuspHead.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = financeSuspHead.getId();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (financeSuspHead.isNew()) { // for New record or new record into work flow

			if (!financeSuspHead.isWorkflow()) {// With out Work flow only new records  
				if (befFinanceSuspHead != null) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							"41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (financeSuspHead.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befFinanceSuspHead != null || tempFinanceSuspHead != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befFinanceSuspHead == null || tempFinanceSuspHead != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!financeSuspHead.isWorkflow()) { // With out Work flow for update and delete

				if (befFinanceSuspHead == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							"41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldFinanceSuspHead != null
							&& !oldFinanceSuspHead.getLastMntOn().equals(befFinanceSuspHead.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41003", errParm, valueParm), usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41004", errParm, valueParm), usrLanguage));
						}
					}
				}
			} else {

				if (tempFinanceSuspHead == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							"41005", errParm, valueParm), usrLanguage));
				}

				if (oldFinanceSuspHead != null && tempFinanceSuspHead != null
						&& !oldFinanceSuspHead.getLastMntOn().equals(tempFinanceSuspHead.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							"41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !financeSuspHead.isWorkflow()) {
			financeSuspHead.setBefImage(befFinanceSuspHead);
		}

		return auditDetail;
	}

}