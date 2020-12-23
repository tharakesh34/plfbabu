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
 *
 * FileName    		:  FinanceCancellationServiceImpl.java												*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 

 * 13-06-2018       Siva					 0.2        Stage Accounting Modifications      * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.service.finance.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.Interface.service.CustomerLimitIntefaceService;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.finance.limits.LimitCheckDetails;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.backend.dao.configuration.VASRecordingDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.limits.LimitInterfaceDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.reason.deatil.ReasonDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeFeesDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralMovement;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.commitment.CommitmentMovement;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinCollaterals;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.InvoiceDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.reason.details.ReasonHeader;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.collateral.CollateralMarkProcess;
import com.pennant.backend.service.dda.DDAControllerService;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.finance.FinanceCancellationService;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.service.limitservice.impl.LimitManagement;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.VASConsatnts;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.notifications.service.NotificationService;
import com.rits.cloning.Cloner;

public class FinanceCancellationServiceImpl extends GenericFinanceDetailService implements FinanceCancellationService {
	private static final Logger logger = Logger.getLogger(FinanceCancellationServiceImpl.class);

	private DDAControllerService ddaControllerService;
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;
	private LimitInterfaceDAO limitInterfaceDAO;
	private CustomerLimitIntefaceService custLimitIntefaceService;
	private CollateralMarkProcess collateralMarkProcess;
	private LimitCheckDetails limitCheckDetails;
	private LimitManagement limitManagement;
	private FinAdvancePaymentsDAO finAdvancePaymentsDAO;
	private FinTypeFeesDAO finTypeFeesDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	@Autowired(required = false)
	private NotificationService notificationService;
	private VASRecordingDAO vASRecordingDAO;
	private long tempWorkflowId;

	ReasonDetailDAO reasonDetailDAO;

	public FinanceCancellationServiceImpl() {
		super();
	}

	/**
	 * Method for Fetching FInance Details & Repay Schedule Details
	 * 
	 * @param finReference
	 * @return
	 */
	@Override
	public FinanceDetail getFinanceDetailById(String finReference, String type, String userRole, String procEdtEvent) {
		logger.debug("Entering");

		//Finance Details
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		scheduleData.setFinReference(finReference);
		scheduleData.setFinanceMain(getFinanceMainDAO().getFinanceMainById(finReference, type, false));
		scheduleData.setFinanceType(
				getFinanceTypeDAO().getFinanceTypeByID(scheduleData.getFinanceMain().getFinType(), "_AView"));

		//Finance Schedule Details
		scheduleData.setFinanceScheduleDetails(
				getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type, false));

		//Finance Disbursement Details
		scheduleData.setDisbursementDetails(
				getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference, type, false));

		//Finance Accounting Fee Charge Details
		scheduleData
				.setFeeRules(getFinFeeChargesDAO().getFeeChargesByFinRef(finReference, procEdtEvent, false, "_TView"));

		//Finance Customer Details			
		if (scheduleData.getFinanceMain().getCustID() != 0
				&& scheduleData.getFinanceMain().getCustID() != Long.MIN_VALUE) {
			financeDetail.setCustomerDetails(getCustomerDetailsService()
					.getCustomerDetailsById(scheduleData.getFinanceMain().getCustID(), true, "_View"));
		}

		//Finance Agreement Details	
		//=======================================
		String finType = scheduleData.getFinanceType().getFinType();
		financeDetail
				.setAggrementList(getAgreementDetailService().getAggrementDetailList(finType, procEdtEvent, userRole));

		// Finance Check List Details 
		//=======================================
		getCheckListDetailService().setFinanceCheckListDetails(financeDetail, finType, procEdtEvent, userRole);

		//Finance Stage Accounting Posting Details 
		//=======================================
		financeDetail.setStageTransactionEntries(getTransactionEntryDAO().getListTransactionEntryByRefType(finType,
				procEdtEvent, FinanceConstants.PROCEDT_STAGEACC, userRole, "_AEView", true));

		//Finance Guaranteer Details			
		financeDetail.setGurantorsDetailList(getGuarantorDetailService().getGuarantorDetail(finReference, "_View"));

		//Finance Joint Account Details
		financeDetail
				.setJountAccountDetailList(getJointAccountDetailService().getJoinAccountDetail(finReference, "_View"));

		//Finance Overdue Penalty Rate Details
		scheduleData.setFinODPenaltyRate(getFinODPenaltyRateDAO().getFinODPenaltyRateByRef(finReference, type));

		// Document Details
		financeDetail.setDocumentDetailsList(getDocumentDetailsDAO().getDocumentDetailsByRef(finReference,
				FinanceConstants.MODULE_NAME, procEdtEvent, "_View"));

		financeDetail
				.setAdvancePaymentsList(finAdvancePaymentsDAO.getFinAdvancePaymentsByFinRef(finReference, "_View"));

		financeDetail.setFinTypeFeesList(getFinTypeFeesDAO().getFinTypeFeesList(finType,
				AccountEventConstants.ACCEVENT_CANCELFIN, "_AView", false, FinanceConstants.MODULEID_FINTYPE));

		// Finance Fee Details
		scheduleData.setFinFeeDetailList(getFinFeeDetailService().getFinFeeDetailById(finReference, false, "_TView"));

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table FinanceMain/FinanceMain_Temp by
	 * using FinanceMainDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using FinanceMainDAO's update method 3) Audit the record in to AuditHeader and AdtFinanceMain by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws AccountNotFoundException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws InterfaceException {
		logger.debug("Entering");

		aAuditHeader = businessValidation(aAuditHeader, "saveOrUpdate");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!aAuditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		long serviceUID = Long.MIN_VALUE;
		String finReference = financeMain.getFinReference();

		if (financeDetail.getFinScheduleData().getFinServiceInstructions().isEmpty()) {
			FinServiceInstruction finServInst = new FinServiceInstruction();
			finServInst.setFinReference(finReference);
			finServInst.setFinEvent(financeDetail.getModuleDefiner());

			financeDetail.getFinScheduleData().setFinServiceInstruction(finServInst);
		}

		for (FinServiceInstruction finSerList : financeDetail.getFinScheduleData().getFinServiceInstructions()) {
			if (finSerList.getInstructionUID() == Long.MIN_VALUE) {
				if (serviceUID == Long.MIN_VALUE) {
					serviceUID = Long.valueOf(ReferenceGenerator.generateNewServiceUID());
				}
				finSerList.setInstructionUID(serviceUID);
			} else {
				serviceUID = finSerList.getInstructionUID();
			}
		}

		TableType tableType = TableType.MAIN_TAB;
		if (financeMain.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		financeMain.setRcdMaintainSts(FinanceConstants.FINSER_EVENT_CANCELFIN);
		if (tableType == TableType.MAIN_TAB) {
			financeMain.setRcdMaintainSts("");
			financeMain.setFinIsActive(false);
			financeMain.setClosedDate(DateUtility.getAppDate());
			financeMain.setClosingStatus(FinanceConstants.CLOSE_STATUS_CANCELLED);
		}

		//Finance Stage Accounting Process
		//=======================================
		auditHeader = executeStageAccounting(auditHeader);
		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}

		//Repayments Postings Details Process Execution
		if (!financeMain.isWorkflow()) {
			if (FinanceConstants.ACCOUNTING_TOTALREVERSAL) {
				//Cancel All Transactions for Finance Disbursement including Commitment Postings, Stage Accounting on Reversal
				getPostingsPreparationUtil().postReveralsExceptFeePay(finReference);
				logger.debug("Reverse Transaction Success for Reference : " + finReference);
			} else {
				//Event Based Accounting on Final Stage
				Date curBDay = DateUtility.getAppDate();
				auditHeader = executeAccountingProcess(auditHeader, curBDay);

				if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
					return auditHeader;
				}
			}
		}

		// Finance Main Details Save And Update
		//=======================================
		if (financeMain.isNew()) {
			getFinanceMainDAO().save(financeMain, tableType, false);
		} else {
			getFinanceMainDAO().update(financeMain, tableType, false);
		}
		// ***cancel loans reason implemented.
		if (financeMain.getDetailsList().size() > 0) {
			saveCancelReasonData(financeMain);
		}

		// Save Fee Charges List
		//=======================================
		if (tableType == TableType.TEMP_TAB) {
			getFinFeeChargesDAO().deleteChargesBatch(financeMain.getFinReference(), financeDetail.getModuleDefiner(),
					false, tableType.getSuffix());
		}

		// set Finance Check List audit details to auditDetails
		//=======================================
		if (financeDetail.getFinanceCheckList() != null && !financeDetail.getFinanceCheckList().isEmpty()) {
			auditDetails
					.addAll(getCheckListDetailService().saveOrUpdate(financeDetail, tableType.getSuffix(), serviceUID));
		}

		// Save Document Details
		if (financeDetail.getDocumentDetailsList() != null && financeDetail.getDocumentDetailsList().size() > 0) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, tableType.getSuffix(),
					financeDetail.getFinScheduleData().getFinanceMain(), financeDetail.getModuleDefiner(), serviceUID);
			auditDetails.addAll(details);
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				financeMain.getBefImage(), financeMain));

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	private void saveCancelReasonData(FinanceMain financeMain) {
		reasonDetailDAO.deleteCancelReasonDetails(financeMain.getFinReference());
		ReasonHeader reasonHeader = new ReasonHeader();
		reasonHeader.setModule("FINANCE");
		reasonHeader.setReference(financeMain.getFinReference());
		reasonHeader.setRemarks(financeMain.getCancelRemarks());
		reasonHeader.setActivity("Cancled");
		reasonHeader.setDetailsList(financeMain.getDetailsList());
		reasonDetailDAO.save(reasonHeader);
	}

	@Override
	public List<ReasonHeader> getCancelReasonDetails(String reference) {
		return reasonDetailDAO.getCancelReasonDetails(reference);
	}

	@Override
	public List<FinAdvancePayments> getFinAdvancePaymentsByFinRef(String finReference) {
		return finAdvancePaymentsService.getFinAdvancePaymentByFinRef(finReference);
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getFinanceMainDAO().delete with parameters financeMain,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtFinanceMain by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws InterfaceException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tranType = PennantConstants.TRAN_DEL;

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		long serviceUID = Long.MIN_VALUE;
		for (FinServiceInstruction finServInst : financeDetail.getFinScheduleData().getFinServiceInstructions()) {
			serviceUID = finServInst.getInstructionUID();
		}

		// Cancel All Transactions done by Finance Reference
		//=======================================
		cancelStageAccounting(financeMain.getFinReference(), financeDetail.getModuleDefiner());

		// Fee charges deletion
		getFinFeeChargesDAO().deleteChargesBatch(financeMain.getFinReference(), financeDetail.getModuleDefiner(), false,
				"_Temp");

		// Checklist Details delete
		//=======================================
		auditDetails.addAll(getCheckListDetailService().delete(financeDetail, "_Temp", tranType));

		// Save Document Details
		if (financeDetail.getDocumentDetailsList() != null && financeDetail.getDocumentDetailsList().size() > 0) {
			for (DocumentDetails docDetails : financeDetail.getDocumentDetailsList()) {
				docDetails.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, "_Temp",
					financeDetail.getFinScheduleData().getFinanceMain(), financeDetail.getModuleDefiner(), serviceUID);
			auditDetails.addAll(details);
		}

		// ScheduleDetails deletion
		getFinanceMainDAO().delete(financeMain, TableType.TEMP_TAB, false, false);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				financeMain.getBefImage(), financeMain));
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		//Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(financeDetail);

		reasonDetailDAO.deleteCancelReasonDetails(financeMain.getFinReference());

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getFinanceMainDAO().delete with
	 * parameters financeMain,"" b) NEW Add new record in to main table by using getFinanceMainDAO().save with
	 * parameters financeMain,"" c) EDIT Update record in the main table by using getFinanceMainDAO().update with
	 * parameters financeMain,"" 3) Delete the record from the workFlow table by using getFinanceMainDAO().delete with
	 * parameters financeMain,"_Temp" 4) Audit the record in to AuditHeader and AdtFinanceMain by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtFinanceMain by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws JaxenException
	 * @throws AccountNotFoundException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader, boolean isNotReqEOD) {
		logger.debug("Entering");

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (isNotReqEOD) {
			aAuditHeader = businessValidation(aAuditHeader, "doApprove");
		}
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		Date curBDay = DateUtility.getAppDate();

		//Execute Accounting Details Process
		//=======================================
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String finReference = financeMain.getFinReference();

		long serviceUID = Long.MIN_VALUE;
		for (FinServiceInstruction finServInst : financeDetail.getFinScheduleData().getFinServiceInstructions()) {
			serviceUID = finServInst.getInstructionUID();
		}

		// Fetch Next Payment Details from Finance for Salaried Postings Verification
		FinanceScheduleDetail orgNextSchd = getFinanceScheduleDetailDAO().getNextSchPayment(finReference, curBDay);

		financeMain.setFinIsActive(false);
		financeMain.setClosedDate(DateUtility.getAppDate());
		financeMain.setClosingStatus(FinanceConstants.CLOSE_STATUS_CANCELLED);
		financeMain.setRcdMaintainSts("");
		financeMain.setRoleCode("");
		financeMain.setNextRoleCode("");
		financeMain.setTaskId("");
		financeMain.setNextTaskId("");
		financeMain.setWorkflowId(0);

		//Finance Cancellation Posting Process Execution
		//=====================================
		//Event Based Accounting on Final Stage
		List<ReturnDataSet> returnDataSets = getPostingsPreparationUtil().postReveralsExceptFeePay(finReference);
		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}

		// GST Invoice Details Reversal on Loan Cancellation
		if (returnDataSets != null && !returnDataSets.isEmpty()) {
			createGSTInvoiceForCancellLoan(returnDataSets.get(0).getLinkedTranId(), financeDetail);
		}

		// Finance Commitment Reference Posting Details
		Commitment commitment = null;
		if (StringUtils.isNotBlank(financeMain.getFinCommitmentRef())) {
			commitment = getCommitmentDAO().getCommitmentById(financeMain.getFinCommitmentRef().trim(), "");

			BigDecimal cmtUtlAmt = CalculationUtil.getConvertedAmount(financeMain.getFinCcy(), commitment.getCmtCcy(),
					financeMain.getFinAmount());
			getCommitmentDAO().updateCommitmentAmounts(commitment.getCmtReference(), cmtUtlAmt.negate(),
					commitment.getCmtExpDate());
			CommitmentMovement cmtMovement = prepareCommitMovement(commitment, financeMain, cmtUtlAmt, 0);
			if (cmtMovement != null) {
				getCommitmentMovementDAO().save(cmtMovement, "");
			}
		}

		//Cancelling IMD receipts
		if (isNotReqEOD) {
			List<Long> receiptIdList = getFinReceiptHeaderDAO().fetchReceiptIdList(finReference);
			if (receiptIdList != null && receiptIdList.size() > 0) {
				getFinReceiptHeaderDAO().cancelReceipts(finReference);
				getFinReceiptDetailDAO().cancelReceiptDetails(receiptIdList);
			}
		}
		tranType = PennantConstants.TRAN_UPD;
		financeMain.setRecordType("");
		getFinanceMainDAO().update(financeMain, TableType.MAIN_TAB, false);

		// Profit Details Inactive status Updation
		// Bug FIX: Closing status not updated in FinPftDetails while cancel the loan. 
		getProfitDetailsDAO().updateFinPftMaturity(finReference,
				financeDetail.getFinScheduleData().getFinanceMain().getClosingStatus(), false);

		// Save Document Details
		if (financeDetail.getDocumentDetailsList() != null && financeDetail.getDocumentDetailsList().size() > 0) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, "", financeDetail.getFinScheduleData().getFinanceMain(),
					financeDetail.getModuleDefiner(), serviceUID);
			auditDetails.addAll(details);
			listDocDeletion(financeDetail, "_Temp");
		}

		// set Check list details Audit
		//=======================================
		if (financeDetail.getFinanceCheckList() != null && !financeDetail.getFinanceCheckList().isEmpty()) {
			auditDetails.addAll(getCheckListDetailService().doApprove(financeDetail, "", serviceUID));
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());

		// Fee charges deletion
		List<AuditDetail> tempAuditDetailList = new ArrayList<AuditDetail>();
		if (isNotReqEOD) {
			getFinFeeChargesDAO().deleteChargesBatch(financeMain.getFinReference(), financeDetail.getModuleDefiner(),
					false, "_Temp");
		}

		//Disbursement Cancellation
		if (isNotReqEOD) {
			processDisbursmentCancellation(financeDetail);
		}
		// Checklist Details delete
		//=======================================
		if (isNotReqEOD) {
			tempAuditDetailList.addAll(getCheckListDetailService().delete(financeDetail, "_Temp", tranType));
		}

		// Adding audit as deleted from TEMP table
		if (isNotReqEOD && auditHeader.getApiHeader() == null) {
			getFinanceMainDAO().delete(financeMain, TableType.TEMP_TAB, false, true);
		}
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				financeMain.getBefImage(), financeMain));
		auditHeader.setAuditDetails(tempAuditDetailList);
		getAuditHeaderDAO().addAudit(auditHeader);

		// Adding audit as Insert/Update/deleted into main table
		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				financeMain.getBefImage(), financeMain));
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		//Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(financeDetail);

		// Delinking collateral Assigned to Finance
		//==========================================
		if (ImplementationConstants.COLLATERAL_INTERNAL) {
			if (ImplementationConstants.COLLATERAL_DELINK_AUTO) {
				List<CollateralAssignment> colAssignList = getCollateralAssignmentDAO()
						.getCollateralAssignmentByFinRef(finReference, FinanceConstants.MODULE_NAME, "");

				if (colAssignList != null && !colAssignList.isEmpty()) {
					for (int i = 0; i < colAssignList.size(); i++) {
						CollateralMovement movement = new CollateralMovement();
						movement.setModule(FinanceConstants.MODULE_NAME);
						movement.setCollateralRef(colAssignList.get(i).getCollateralRef());
						movement.setReference(colAssignList.get(i).getReference());
						movement.setAssignPerc(BigDecimal.ZERO);
						movement.setValueDate(DateUtility.getAppDate());
						movement.setProcess(CollateralConstants.PROCESS_AUTO);
						getCollateralAssignmentDAO().save(movement);
					}

					getCollateralAssignmentDAO().deLinkCollateral(financeMain.getFinReference());
				}
			}
		} else {
			List<FinCollaterals> collateralList = financeDetail.getFinanceCollaterals();
			if (collateralList != null && !collateralList.isEmpty()) {
				getCollateralMarkProcess().deMarkCollateral(financeDetail.getFinanceCollaterals());
			}
		}

		// send DDA Cancellation Request to Interface
		//==========================================
		getDdaControllerService().cancelDDARegistration(financeMain.getFinReference());

		// send Cancel Utilization Request to ACP Interface and save log details
		//=======================================
		if (ImplementationConstants.LIMIT_INTERNAL) {
			if (isNotReqEOD) {
				getLimitManagement().processLoanCancel(financeDetail, false);
			}
		} else {
			getLimitCheckDetails().doProcessLimits(financeDetail.getFinScheduleData().getFinanceMain(),
					FinanceConstants.CANCEL_UTILIZATION);
		}

		// Save Salaried Posting Details
		saveFinSalPayment(financeDetail.getFinScheduleData(), orgNextSchd, true);
		//updating the processed with 1 in finstageAccountingLog
		getFinStageAccountingLogDAO().update(financeMain.getFinReference(), financeDetail.getModuleDefiner(), false);

		// Extended Field Details
		if (financeDetail.getExtendedFieldRender() != null) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("ExtendedFieldDetails");
			details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
					financeDetail.getExtendedFieldHeader(), "", serviceUID);
			auditDetails.addAll(details);
		}

		saveCancelReasonData(financeMain);

		// Notification
		tempWorkflowId = financeMain.getWorkflowId();
		Notification notification = new Notification();
		notification.getTemplates().add(NotificationConstants.TEMPLATE_FOR_AE);
		notification.getTemplates().add(NotificationConstants.TEMPLATE_FOR_CN);
		notification.setModule("LOAN_CANCELLATION");
		notification.setSubModule(FinanceConstants.FINSER_EVENT_CANCELFIN);
		notification.setKeyReference(financeMain.getFinReference());
		notification.setStage(PennantConstants.REC_ON_APPR);
		notification.setReceivedBy(financeMain.getLastMntBy());
		financeMain.setWorkflowId(tempWorkflowId);
		try {

			if (notificationService != null) {
				notificationService.sendNotifications(notification, financeDetail, financeMain.getFinType(),
						financeDetail.getDocumentDetailsList());
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug("Leaving");
		return auditHeader;
	}

	private void createGSTInvoiceForCancellLoan(long linkedTranID, FinanceDetail financeDetail) {

		// GST Invoice Preparation
		if (linkedTranID <= 0) {
			return;
		}

		// GST Credit Entries against Fee Details on Loan Cancellation
		if (CollectionUtils.isEmpty(financeDetail.getFinScheduleData().getFinFeeDetailList())) {
			List<FinFeeDetail> finFeedetails = getFinFeeDetailService().getFinFeeDetailById(
					financeDetail.getFinScheduleData().getFinanceMain().getFinReference(), false, "_AView");
			if (CollectionUtils.isEmpty(finFeedetails)) {
				return;
			}
			financeDetail.getFinScheduleData().setFinFeeDetailList(finFeedetails);
		}

		InvoiceDetail invoiceDetail = new InvoiceDetail();
		invoiceDetail.setLinkedTranId(linkedTranID);
		invoiceDetail.setFinanceDetail(financeDetail);
		invoiceDetail.setFinFeeDetailsList(financeDetail.getFinScheduleData().getFinFeeDetailList());
		invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_CREDIT);
		invoiceDetail.setOrigination(true);
		invoiceDetail.setWaiver(false);
		invoiceDetail.setDbInvSetReq(true);

		//Normal Fees invoice preparation
		//In Case of Loan Cancel Approval GST Invoice is happen only for remaining fee after IMD.
		if (CollectionUtils.isNotEmpty(financeDetail.getFinScheduleData().getFinFeeDetailList())) {
			for (FinFeeDetail fee : financeDetail.getFinScheduleData().getFinFeeDetailList()) {
				fee.setPaidFromLoanApproval(true);
			}
		}

		// Normal Fees invoice preparation
		Long dueInvoiceID = this.gstInvoiceTxnService.feeTaxInvoicePreparation(invoiceDetail);

		for (int i = 0; i < financeDetail.getFinScheduleData().getFinFeeDetailList().size(); i++) {
			FinFeeDetail finFeeDetail = financeDetail.getFinScheduleData().getFinFeeDetailList().get(i);
			if (finFeeDetail.getTaxHeader() != null && finFeeDetail.getNetAmount().compareTo(BigDecimal.ZERO) > 0) {
				if (dueInvoiceID == null) {
					dueInvoiceID = finFeeDetail.getTaxHeader().getInvoiceID();
				}
				finFeeDetail.getTaxHeader().setInvoiceID(dueInvoiceID);
			}
		}

		// Waiver Fees Invoice Preparation
		if (ImplementationConstants.TAX_DFT_CR_INV_REQ) {
			List<FinFeeDetail> waiverFees = new ArrayList<>();
			for (FinFeeDetail fee : financeDetail.getFinScheduleData().getFinFeeDetailList()) {
				if (fee.isTaxApplicable() && fee.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0) {
					waiverFees.add(fee);
				}
			}

			invoiceDetail = new InvoiceDetail();
			invoiceDetail.setLinkedTranId(linkedTranID);
			invoiceDetail.setFinanceDetail(financeDetail);
			invoiceDetail.setFinFeeDetailsList(waiverFees);
			invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_DEBIT);
			invoiceDetail.setOrigination(true);
			invoiceDetail.setWaiver(false);
			invoiceDetail.setDbInvSetReq(true);

			if (CollectionUtils.isNotEmpty(waiverFees)) {
				invoiceDetail.setFinFeeDetailsList(waiverFees);
				invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_DEBIT);
				invoiceDetail.setOrigination(true);
				invoiceDetail.setWaiver(true);
				invoiceDetail.setDbInvSetReq(true);

				gstInvoiceTxnService.feeTaxInvoicePreparation(invoiceDetail);
			}
		}

	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from getFinanceMainDAO().getErrorDetail with Error ID and
	 * language as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		String usrLanguage = auditHeader.getUsrLanguage();

		// Extended field details Validation
		if (financeDetail.getExtendedFieldRender() != null) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("ExtendedFieldDetails");
			ExtendedFieldHeader extHeader = financeDetail.getExtendedFieldHeader();
			details = extendedFieldDetailsService.validateExtendedDdetails(extHeader, details, method, usrLanguage);
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
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
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

		// Extended Field Details
		if (financeDetail.getExtendedFieldRender() != null) {
			auditDetailMap.put("ExtendedFieldDetails", extendedFieldDetailsService
					.setExtendedFieldsAuditData(financeDetail.getExtendedFieldRender(), auditTranType, method, null));
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldDetails"));
		}
		//Finance Checklist Details
		//=======================================
		List<FinanceCheckListReference> financeCheckList = financeDetail.getFinanceCheckList();

		if (StringUtils.equals(method, "saveOrUpdate")) {
			if (financeCheckList != null && !financeCheckList.isEmpty()) {
				auditDetails.addAll(getCheckListDetailService().getAuditDetail(auditDetailMap, financeDetail,
						auditTranType, method));
			}
		}
		financeDetail.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(financeDetail);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving ");

		return auditHeader;

	}

	/**
	 * Method for Validate Finance Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @param isWIF
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinanceDetail financeDetail = (FinanceDetail) auditDetail.getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		FinanceMain tempFinanceMain = null;
		if (financeMain.isWorkflow()) {
			tempFinanceMain = getFinanceMainDAO().getFinanceMainById(financeMain.getId(), "_Temp", false);
		}
		FinanceMain befFinanceMain = getFinanceMainDAO().getFinanceMainById(financeMain.getId(), "", false);
		FinanceMain oldFinanceMain = financeMain.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = financeMain.getId();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (financeMain.isNew()) { // for New record or new record into work flow

			if (!financeMain.isWorkflow()) {// With out Work flow only new
				// records
				if (befFinanceMain != null) { // Record Already Exists in the
					// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
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
			if (!financeMain.isWorkflow()) { // With out Work flow for update
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
		int eodProgressCount = getCustomerQueuingDAO().getProgressCountByCust(financeMain.getCustID());

		// If Customer Exists in EOD Processing, Not allowed to Maintenance till completion
		if (eodProgressCount > 0) {
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "60203", errParm, valueParm), usrLanguage));
		}

		List<FinAdvancePayments> list = financeDetail.getAdvancePaymentsList();
		if (CollectionUtils.isNotEmpty(list)) {
			//Disbursement instructions should be reversed before canceling loan
			for (FinAdvancePayments finAdvPayment : list) {
				if (StringUtils.equals(finAdvPayment.getStatus(), DisbursementConstants.STATUS_PAID)) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "60406", errParm, valueParm), usrLanguage));
				}

				// instructions should be cancelled before canceling a loan.
				if (StringUtils.equals(finAdvPayment.getStatus(), DisbursementConstants.STATUS_AWAITCON)) { //Disbursement
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "60408", errParm, valueParm), usrLanguage));
				}
			}
		}

		List<VASRecording> vasRecordings = vASRecordingDAO
				.getVASRecordingsStatusByReference(financeMain.getFinReference(), "");
		//Checking VAS instruction status.
		if (CollectionUtils.isNotEmpty(vasRecordings)) {
			for (VASRecording vasRecording : vasRecordings) {
				if (!StringUtils.equals(vasRecording.getVasStatus(), VASConsatnts.STATUS_CANCEL)) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "60214", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !financeMain.isWorkflow()) {
			auditDetail.setBefImage(befFinanceMain);
		}

		return auditDetail;
	}

	/**
	 * Method for Add a Movement Entry for Commitment Repayment Event, if Only for Revolving Commitment
	 * 
	 * @param commitment
	 * @param dataSet
	 * @param postAmount
	 * @param linkedtranId
	 * @return
	 */
	public CommitmentMovement prepareCommitMovement(Commitment commitment, FinanceMain financeMain,
			BigDecimal postAmount, long linkedtranId) {

		CommitmentMovement movement = new CommitmentMovement();
		Date curBussDate = DateUtility.getAppDate();

		movement.setCmtReference(commitment.getCmtReference());
		movement.setFinReference(financeMain.getFinReference());
		movement.setFinBranch(financeMain.getFinBranch());
		movement.setFinType(financeMain.getFinType());
		movement.setMovementDate(curBussDate);
		movement.setMovementOrder(
				getCommitmentMovementDAO().getMaxMovementOrderByRef(commitment.getCmtReference()) + 1);
		movement.setMovementType("FC");//Finance Cancellation
		movement.setMovementAmount(postAmount);
		movement.setCmtAmount(commitment.getCmtAmount());
		movement.setCmtUtilizedAmount(commitment.getCmtUtilizedAmount().subtract(postAmount));
		if (commitment.getCmtExpDate().compareTo(curBussDate) < 0) {
			movement.setCmtAvailable(BigDecimal.ZERO);
		} else {
			movement.setCmtAvailable(commitment.getCmtAvailable().add(postAmount));
		}
		movement.setCmtCharges(BigDecimal.ZERO);
		movement.setLinkedTranId(linkedtranId);
		movement.setVersion(1);
		movement.setLastMntBy(9999);
		movement.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		movement.setRecordStatus("Approved");
		movement.setRoleCode("");
		movement.setNextRoleCode("");
		movement.setTaskId("");
		movement.setNextTaskId("");
		movement.setRecordType("");
		movement.setWorkflowId(0);

		return movement;

	}

	private List<AuditDetail> processDisbursmentCancellation(FinanceDetail financeDetail) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		List<FinAdvancePayments> list = financeDetail.getAdvancePaymentsList();
		if (list != null && !list.isEmpty()) {
			int count = 0;
			for (FinAdvancePayments finAdvpay : list) {
				if (!StringUtils.trimToEmpty(finAdvpay.getStatus()).equals(DisbursementConstants.STATUS_CANCEL)) {
					count = count + 1;
					String[] fields = PennantJavaUtil.getFieldDetails(new FinAdvancePayments(),
							new FinAdvancePayments().getExcludeFields());
					AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_UPD, count, fields[0], fields[1],
							finAdvpay.getBefImage(), finAdvpay);
					auditDetails.add(auditDetail);
					finAdvpay.setStatus(DisbursementConstants.STATUS_CANCEL);
					finAdvancePaymentsDAO.update(finAdvpay, "");

				}
			}
			if (auditDetails.size() > 0) {
				finAdvancePaymentsDAO
						.deleteByFinRef(financeDetail.getFinScheduleData().getFinanceMain().getFinReference(), "_Temp");
			}
		}
		return auditDetails;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public DDAControllerService getDdaControllerService() {
		return ddaControllerService;
	}

	public void setDdaControllerService(DDAControllerService ddaControllerService) {
		this.ddaControllerService = ddaControllerService;
	}

	public FinanceReferenceDetailDAO getFinanceReferenceDetailDAO() {
		return financeReferenceDetailDAO;
	}

	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
	}

	public LimitInterfaceDAO getLimitInterfaceDAO() {
		return limitInterfaceDAO;
	}

	public void setLimitInterfaceDAO(LimitInterfaceDAO limitInterfaceDAO) {
		this.limitInterfaceDAO = limitInterfaceDAO;
	}

	public CustomerLimitIntefaceService getCustLimitIntefaceService() {
		return custLimitIntefaceService;
	}

	public void setCustLimitIntefaceService(CustomerLimitIntefaceService custLimitIntefaceService) {
		this.custLimitIntefaceService = custLimitIntefaceService;
	}

	public CollateralMarkProcess getCollateralMarkProcess() {
		return collateralMarkProcess;
	}

	public void setCollateralMarkProcess(CollateralMarkProcess collateralMarkProcess) {
		this.collateralMarkProcess = collateralMarkProcess;
	}

	public LimitCheckDetails getLimitCheckDetails() {
		return limitCheckDetails;
	}

	public void setLimitCheckDetails(LimitCheckDetails limitCheckDetails) {
		this.limitCheckDetails = limitCheckDetails;
	}

	public LimitManagement getLimitManagement() {
		return limitManagement;
	}

	public void setLimitManagement(LimitManagement limitManagement) {
		this.limitManagement = limitManagement;
	}

	public void setFinAdvancePaymentsDAO(FinAdvancePaymentsDAO finAdvancePaymentsDAO) {
		this.finAdvancePaymentsDAO = finAdvancePaymentsDAO;
	}

	public FinTypeFeesDAO getFinTypeFeesDAO() {
		return finTypeFeesDAO;
	}

	public void setFinTypeFeesDAO(FinTypeFeesDAO finTypeFeesDAO) {
		this.finTypeFeesDAO = finTypeFeesDAO;
	}

	public FinReceiptHeaderDAO getFinReceiptHeaderDAO() {
		return finReceiptHeaderDAO;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public FinReceiptDetailDAO getFinReceiptDetailDAO() {
		return finReceiptDetailDAO;
	}

	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	public ExtendedFieldDetailsService getExtendedFieldDetailsService() {
		return extendedFieldDetailsService;
	}

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	public ReasonDetailDAO getReasonDetailDAO() {
		return reasonDetailDAO;
	}

	public void setReasonDetailDAO(ReasonDetailDAO reasonDetailDAO) {
		this.reasonDetailDAO = reasonDetailDAO;
	}

	public void setvASRecordingDAO(VASRecordingDAO vASRecordingDAO) {
		this.vASRecordingDAO = vASRecordingDAO;
	}
}
