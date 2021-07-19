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
 * FileName    		:  ManualPaymentServiceImpl.java												*                           
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.Interface.service.CustomerLimitIntefaceService;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.finance.limits.LimitCheckDetails;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.app.util.RepayCalculator;
import com.pennant.app.util.RepaymentPostingsUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.FinRepayQueue.FinRepayQueueDAO;
import com.pennant.backend.dao.finance.FinanceRepayPriorityDAO;
import com.pennant.backend.dao.limits.LimitInterfaceDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeFeesDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceRepayPriority;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RepayData;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.rulefactory.SubHeadRule;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.service.finance.ManualPaymentService;
import com.pennant.backend.service.limitservice.impl.LimitManagement;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.rits.cloning.Cloner;

public class ManualPaymentServiceImpl extends GenericFinanceDetailService implements ManualPaymentService {
	private static final Logger logger = LogManager.getLogger(ManualPaymentServiceImpl.class);

	private FinanceRepayPriorityDAO financeRepayPriorityDAO;
	private FinRepayQueueDAO finRepayQueueDAO;
	private RepaymentPostingsUtil repayPostingUtil;
	private AccountingSetDAO accountingSetDAO;
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;
	private LimitInterfaceDAO limitInterfaceDAO;
	private CustomerLimitIntefaceService custLimitIntefaceService;
	private LimitCheckDetails limitCheckDetails;
	private RuleService ruleService;
	private FinanceDetailService financeDetailService;
	private RepayCalculator repayCalculator;
	private LimitManagement limitManagement;

	private FinTypeFeesDAO finTypeFeesDAO;
	private FinFeeDetailService finFeeDetailService;

	public ManualPaymentServiceImpl() {
		super();
	}

	/**
	 * Method for Fetching FInance Details & Repay Schedule Details
	 * 
	 * @param finReference
	 * @return
	 */
	@Override
	public RepayData getRepayDataById(String finReference, String eventCode, String procEdtEvent, String userRole) {
		logger.debug("Entering");

		// Finance Details
		RepayData repayData = new RepayData();
		repayData.setFinReference(finReference);

		// Finance Details
		FinanceDetail financeDetail = new FinanceDetail();
		repayData.setFinanceDetail(financeDetail);
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		scheduleData.setFinReference(finReference);
		FinanceMain financeMain = getFinanceMainDAO().getFinanceMainById(finReference, "_View", false);
		scheduleData.setFinanceMain(financeMain);

		if (financeMain != null) {

			// Finance Schedule Details
			scheduleData.setFinanceScheduleDetails(
					getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, "_View", false));

			// Finance Disbursement Details
			scheduleData.setDisbursementDetails(
					getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference, "_View", false));

			// Finance Repayments Instruction Details
			scheduleData
					.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference, "_View", false));

			// Finance Type Details
			FinanceType financeType = getFinanceTypeDAO().getFinanceTypeByID(financeMain.getFinType(), "_AView");
			scheduleData.setFinanceType(financeType);

			if (StringUtils.isNotBlank(financeMain.getPromotionCode())) {
				financeDetail.setFinTypeFeesList(getFinTypeFeesDAO().getFinTypeFeesList(financeMain.getPromotionCode(),
						AccountEventConstants.ACCEVENT_EARLYSTL, "_AView", false, FinanceConstants.MODULEID_PROMOTION));
			} else {
				financeDetail.setFinTypeFeesList(getFinTypeFeesDAO().getFinTypeFeesList(financeMain.getFinType(),
						AccountEventConstants.ACCEVENT_EARLYSTL, "_AView", false, FinanceConstants.MODULEID_FINTYPE));
			}

			// Finance Fee Details
			scheduleData
					.setFinFeeDetailList(getFinFeeDetailService().getFinFeeDetailById(finReference, false, "_TView"));

			// Finance Customer Details
			if (financeMain.getCustID() != 0 && financeMain.getCustID() != Long.MIN_VALUE) {
				financeDetail.setCustomerDetails(
						getCustomerDetailsService().getCustomerDetailsById(financeMain.getCustID(), true, "_View"));
			}

			// Finance Check List Details
			// =======================================
			getCheckListDetailService().setFinanceCheckListDetails(repayData.getFinanceDetail(),
					financeType.getFinType(), procEdtEvent, userRole);

			// Finance Stage Accounting Posting Details
			// =======================================
			repayData.getFinanceDetail().setStageTransactionEntries(
					getTransactionEntryDAO().getListTransactionEntryByRefType(financeType.getFinType(),
							StringUtils.isEmpty(procEdtEvent) ? FinServiceEvent.ORG : procEdtEvent,
							FinanceConstants.PROCEDT_STAGEACC, userRole, "_AEView", true));

			if (StringUtils.isNotBlank(financeMain.getRecordType())) {

				// Repay Header Details
				repayData.setFinRepayHeader(getFinanceRepaymentsDAO().getFinRepayHeader(finReference, "_Temp"));

				// Repay Schedule Details
				repayData.setRepayScheduleDetails(getFinanceRepaymentsDAO().getRpySchdList(finReference, "_Temp"));

				// Fee Rule Details
				scheduleData.setFeeRules(
						getFinFeeChargesDAO().getFeeChargesByFinRef(finReference, procEdtEvent, false, "_Temp"));

				// Finance Document Details
				financeDetail.setDocumentDetailsList(getDocumentDetailsDAO().getDocumentDetailsByRef(finReference,
						FinanceConstants.MODULE_NAME, procEdtEvent, "_Temp"));

			} else {

				// Repay Header Details
				repayData.setFinRepayHeader(null);

				// Repay Schedule Details
				repayData.setRepayScheduleDetails(null);

				// Finance Fee Charge Details
				// =======================================
				List<Long> accSetIdList = new ArrayList<Long>();
				Long accSetId = returnAccountingSetid(eventCode, financeType);
				if (accSetId != Long.MIN_VALUE) {
					accSetIdList.add(Long.valueOf(accSetId));
				}
				accSetIdList.addAll(getFinanceReferenceDetailDAO().getRefIdListByFinType(financeType.getFinType(),
						procEdtEvent, null, "_ACView"));
				if (!accSetIdList.isEmpty()) {
					financeDetail.setFeeCharges(
							getTransactionEntryDAO().getListFeeChargeRules(accSetIdList, eventCode, "_AView", 0));
				}

			}
		}

		logger.debug("Leaving");
		return repayData;
	}

	/**
	 * Get AccountingSet Id based on event code.<br>
	 * 
	 * @param eventCode
	 * @param financeType
	 * @return
	 */
	private Long returnAccountingSetid(String eventCode, FinanceType financeType) {
		logger.debug("Entering ");
		// Execute entries depend on Finance Event
		Long accountingSetId = Long.MIN_VALUE;
		if (eventCode.equals(AccountEventConstants.ACCEVENT_EARLYSTL)) {
			accountingSetId = getFinTypeAccountingDAO().getAccountSetID(financeType.getFinType(),
					AccountEventConstants.ACCEVENT_EARLYSTL, FinanceConstants.MODULEID_FINTYPE);
		} else if (eventCode.equals(AccountEventConstants.ACCEVENT_EARLYPAY)) {
			accountingSetId = getFinTypeAccountingDAO().getAccountSetID(financeType.getFinType(),
					AccountEventConstants.ACCEVENT_EARLYPAY, FinanceConstants.MODULEID_FINTYPE);
		} else if (eventCode.equals(AccountEventConstants.ACCEVENT_REPAY)) {
			accountingSetId = getFinTypeAccountingDAO().getAccountSetID(financeType.getFinType(),
					AccountEventConstants.ACCEVENT_REPAY, FinanceConstants.MODULEID_FINTYPE);
		}
		logger.debug("Leaving");
		return accountingSetId;
	}

	/**
	 * Method for Fetching Accounting Entries
	 * 
	 * @param financeDetail
	 * @return
	 */
	@Override
	public FinanceDetail getAccountingDetail(FinanceDetail financeDetail, String eventCodeRef) {
		logger.debug("Entering");

		String commitmentRef = financeDetail.getFinScheduleData().getFinanceMain().getFinCommitmentRef();

		if (StringUtils.isEmpty(commitmentRef)) {

			Commitment commitment = getCommitmentDAO().getCommitmentById(commitmentRef, "");
			if (commitment != null && commitment.isRevolving()) {
				long accountingSetId = getAccountingSetDAO().getAccountingSetId(AccountEventConstants.ACCEVENT_CMTRPY,
						AccountEventConstants.ACCEVENT_CMTRPY);
				if (accountingSetId != 0) {
					financeDetail.setCmtFinanceEntries(
							getTransactionEntryDAO().getListTransactionEntryById(accountingSetId, "_AEView", true));
				}
			}
		}

		logger.debug("Leaving");
		return financeDetail;
	}

	@Override
	public FinanceProfitDetail getPftDetailForEarlyStlReport(String finReference) {
		return getProfitDetailsDAO().getPftDetailForEarlyStlReport(finReference);
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table FinanceMain/FinanceMain_Temp by
	 * using FinanceMainDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using FinanceMainDAO's update method 3) Audit the record in to AuditHeader and AdtFinanceMain by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 * @throws AccountNotFoundException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		aAuditHeader = businessValidation(aAuditHeader, "saveOrUpdate");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!aAuditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);

		RepayData repayData = (RepayData) auditHeader.getAuditDetail().getModelData();

		// Finance Stage Accounting Process
		// =======================================
		auditHeader = executeStageAccounting(auditHeader);
		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}

		FinScheduleData scheduleData = repayData.getFinanceDetail().getFinScheduleData();
		FinanceMain financeMain = scheduleData.getFinanceMain();
		FinRepayHeader finRepayHeader = repayData.getFinRepayHeader();
		String finReference = financeMain.getFinReference();
		Date curBDay = DateUtility.getAppDate();

		long serviceUID = Long.MIN_VALUE;

		if (repayData.getFinanceDetail().getFinScheduleData().getFinServiceInstructions().isEmpty()) {
			FinServiceInstruction finServInst = new FinServiceInstruction();
			finServInst.setFinReference(finReference);
			finServInst.setFinEvent(repayData.getFinanceDetail().getModuleDefiner());
			repayData.getFinanceDetail().getFinScheduleData().setFinServiceInstruction(finServInst);
		}

		for (FinServiceInstruction finSerList : repayData.getFinanceDetail().getFinScheduleData()
				.getFinServiceInstructions()) {
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
		financeMain.setRcdMaintainSts(finRepayHeader.getFinEvent());
		if (tableType == TableType.MAIN_TAB) {
			financeMain.setRcdMaintainSts("");
		}

		// Repayments Postings Details Process Execution
		long linkedTranId = 0;
		boolean partialPay = false;
		FinanceProfitDetail profitDetail = null;
		AEAmountCodes aeAmountCodes = null;

		List<FinRepayQueue> finRepayQueues = new ArrayList<FinRepayQueue>();

		boolean emptyRepayInstructions = scheduleData.getRepayInstructions() == null ? true : false;

		if (!financeMain.isWorkflow()) {
			profitDetail = getProfitDetailsDAO().getFinProfitDetailsById(finReference);

			List<RepayScheduleDetail> repaySchdList = repayData.getRepayScheduleDetails();
			List<Object> returnList = processRepaymentPostings(financeMain, scheduleData.getFinanceScheduleDetails(),
					profitDetail, repaySchdList, repayData.getEventCodeRef(), scheduleData.getFeeRules(),
					scheduleData.getFinanceType().getFinDivision());

			if (!(Boolean) returnList.get(0)) {
				String errParm = (String) returnList.get(1);
				throw new InterfaceException("9999", errParm);
			}

			linkedTranId = (Long) returnList.get(1);
			partialPay = (Boolean) returnList.get(2);
			aeAmountCodes = (AEAmountCodes) returnList.get(3);
			finRepayQueues = (List<FinRepayQueue>) returnList.get(5);
		}

		// Finance Main Details Save And Update
		// =======================================
		if (financeMain.isNew()) {
			getFinanceMainDAO().save(financeMain, tableType, false);

			// Save FinRepayHeader Details
			finRepayHeader.setLinkedTranId(linkedTranId);
			getFinanceRepaymentsDAO().saveFinRepayHeader(finRepayHeader, TableType.MAIN_TAB);

			// Save Repay Schedule Details
			getFinanceRepaymentsDAO().saveRpySchdList(repayData.getRepayScheduleDetails(), tableType);

		} else {
			getFinanceMainDAO().update(financeMain, tableType, false);

			// Save/Update FinRepayHeader Details depends on Workflow
			if (tableType == TableType.TEMP_TAB) {
				finRepayHeader.setLinkedTranId(linkedTranId);
				getFinanceRepaymentsDAO().updateFinRepayHeader(finRepayHeader, tableType.getSuffix());
				getFinanceRepaymentsDAO().deleteRpySchdList(finReference, tableType.getSuffix());
				getFinanceRepaymentsDAO().saveRpySchdList(repayData.getRepayScheduleDetails(), tableType);
			}
		}

		// Save schedule details
		// =======================================
		if (!financeMain.isNewRecord()) {

			if (tableType == TableType.MAIN_TAB
					&& financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
				// Fetch Existing data before Modification

				FinScheduleData oldFinSchdData = null;
				if (finRepayHeader.isSchdRegenerated()) {
					oldFinSchdData = getFinSchDataByFinRef(finReference, "");
					oldFinSchdData.setFinanceMain(financeMain);
					oldFinSchdData.setFinReference(finReference);
				}

				// Create log entry for Action for Schedule Modification
				FinLogEntryDetail entryDetail = new FinLogEntryDetail();
				entryDetail.setFinReference(finReference);
				entryDetail.setEventAction(finRepayHeader.getFinEvent());
				entryDetail.setSchdlRecal(finRepayHeader.isSchdRegenerated());
				entryDetail.setPostDate(curBDay);
				entryDetail.setReversalCompleted(false);
				long logKey = getFinLogEntryDetailDAO().save(entryDetail);

				// Save Schedule Details For Future Modifications
				if (finRepayHeader.isSchdRegenerated()) {
					listSave(oldFinSchdData, "_Log", logKey);
				}
			}
		}

		// Finance Schedule Details
		listDeletion(finReference, tableType.getSuffix(), emptyRepayInstructions);
		listSave(scheduleData, tableType.getSuffix(), 0);

		// Fee Charge Details Clearing before
		if (tableType == TableType.TEMP_TAB) {
			getFinFeeChargesDAO().deleteChargesBatch(finReference, finRepayHeader.getFinEvent(), false,
					tableType.getSuffix());
		}

		saveFeeChargeList(repayData, repayData.getFinRepayHeader().getFinEvent(), tableType.getSuffix());

		// Save Document Details
		if (repayData.getFinanceDetail().getDocumentDetailsList() != null
				&& repayData.getFinanceDetail().getDocumentDetailsList().size() > 0) {
			List<AuditDetail> details = repayData.getFinanceDetail().getAuditDetailMap().get("DocumentDetails");
			auditDetails.addAll(processingDocumentDetailsList(details, tableType.getSuffix(), financeMain,
					finRepayHeader.getFinEvent(), serviceUID));
		}

		// set Finance Check List audit details to auditDetails
		// =======================================
		if (repayData.getFinanceDetail().getFinanceCheckList() != null
				&& !repayData.getFinanceDetail().getFinanceCheckList().isEmpty()) {
			auditDetails.addAll(getCheckListDetailService().saveOrUpdate(repayData.getFinanceDetail(),
					tableType.getSuffix(), serviceUID));
		}

		// Process Updations For Postings
		if (!financeMain.isWorkflow()) {
			getRepayPostingUtil().UpdateScreenPaymentsProcess(financeMain, scheduleData.getFinanceScheduleDetails(),
					profitDetail, finRepayQueues, linkedTranId, partialPay, aeAmountCodes);

			getFinanceRepaymentsDAO().saveFinRepayHeader(finRepayHeader, tableType);

			// Update Linked Transaction ID after Repayments Postings Process if workflow not found
			for (RepayScheduleDetail rpySchd : repayData.getRepayScheduleDetails()) {
				rpySchd.setLinkedTranId(linkedTranId);
			}
			getFinanceRepaymentsDAO().saveRpySchdList(repayData.getRepayScheduleDetails(), tableType);
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				financeMain.getBefImage(), financeMain));

		auditHeader.setAuditDetails(auditDetails);
		auditHeader.setAuditModule("FinanceDetail");
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Method to delete schedule, disbursement, deferment header, deferment detail,repay instruction, rate changes
	 * lists.
	 * 
	 * @param finDetail
	 * @param tableType
	 * @param isWIF
	 */
	public void listDeletion(String finReference, String tableType, boolean emptyRepayInstructions) {
		logger.debug("Entering ");
		getFinanceScheduleDetailDAO().deleteByFinReference(finReference, tableType, false, 0);
		if (!emptyRepayInstructions) {
			getRepayInstructionDAO().deleteByFinReference(finReference, tableType, false, 0);
		}
		logger.debug("Leaving ");
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getFinanceMainDAO().delete with parameters financeMain,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtFinanceMain by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 * @throws InterfaceException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tranType = PennantConstants.TRAN_DEL;

		RepayData repayData = (RepayData) auditHeader.getAuditDetail().getModelData();
		FinScheduleData scheduleData = repayData.getFinanceDetail().getFinScheduleData();
		FinanceMain financeMain = scheduleData.getFinanceMain();
		long serviceUID = Long.MIN_VALUE;
		for (FinServiceInstruction finServInst : repayData.getFinanceDetail().getFinScheduleData()
				.getFinServiceInstructions()) {
			serviceUID = finServInst.getInstructionUID();
		}
		// Cancel All Transactions done by Finance Reference
		// =======================================
		cancelStageAccounting(financeMain.getFinReference(), repayData.getFinRepayHeader().getFinEvent());

		// ScheduleDetails deletion
		listDeletion(financeMain.getFinReference(), "_Temp", false);
		getFinFeeChargesDAO().deleteChargesBatch(financeMain.getFinReference(),
				repayData.getFinRepayHeader().getFinEvent(), false, "_Temp");
		getFinanceMainDAO().delete(financeMain, TableType.TEMP_TAB, false, false);

		// Delete Finance Repay Header
		getFinanceRepaymentsDAO().deleteFinRepayHeader(repayData.getFinRepayHeader(), "_Temp");
		getFinanceRepaymentsDAO().deleteRpySchdList(financeMain.getFinReference(), "_Temp");

		// Save Document Details
		if (repayData.getFinanceDetail().getDocumentDetailsList() != null
				&& repayData.getFinanceDetail().getDocumentDetailsList().size() > 0) {
			for (DocumentDetails docDetails : repayData.getFinanceDetail().getDocumentDetailsList()) {
				docDetails.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
			List<AuditDetail> details = repayData.getFinanceDetail().getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, "_Temp",
					repayData.getFinanceDetail().getFinScheduleData().getFinanceMain(),
					repayData.getFinRepayHeader().getFinEvent(), serviceUID);
			auditHeader.setAuditDetails(details);
		}

		// Fee charges deletion
		getFinFeeChargesDAO().deleteChargesBatch(financeMain.getFinReference(),
				repayData.getFinanceDetail().getModuleDefiner(), false, "_Temp");

		// Checklist Details delete
		// =======================================
		auditHeader.getAuditDetails()
				.addAll(getCheckListDetailService().delete(repayData.getFinanceDetail(), "_Temp", tranType));

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				financeMain.getBefImage(), financeMain));
		auditHeader.setAuditModule("FinanceDetail");
		getAuditHeaderDAO().addAudit(auditHeader);

		// Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(repayData);

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
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 * @throws AccountNotFoundException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		aAuditHeader = businessValidation(aAuditHeader, "doApprove");
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);

		RepayData repayData = (RepayData) auditHeader.getAuditDetail().getModelData();
		Date curBDay = DateUtility.getAppDate();

		// Finance Stage Accounting Process
		// =======================================
		auditHeader = executeStageAccounting(auditHeader);
		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}

		// Repayment Postings Details Process Execution
		long linkedTranId = 0;
		boolean partialPay = false;
		FinanceProfitDetail profitDetail = null;
		List<FinRepayQueue> finRepayQueues = new ArrayList<FinRepayQueue>();

		// Execute Accounting Details Process
		// =======================================
		FinScheduleData scheduleData = repayData.getFinanceDetail().getFinScheduleData();
		FinanceMain financeMain = scheduleData.getFinanceMain();
		String finReference = financeMain.getFinReference();
		FinRepayHeader finRepayHeader = repayData.getFinRepayHeader();
		AEAmountCodes aeAmountCodes = null;

		financeMain.setRcdMaintainSts("");
		financeMain.setRoleCode("");
		financeMain.setNextRoleCode("");
		financeMain.setTaskId("");
		financeMain.setNextTaskId("");
		financeMain.setWorkflowId(0);
		long serviceUID = Long.MIN_VALUE;
		for (FinServiceInstruction finServInst : repayData.getFinanceDetail().getFinScheduleData()
				.getFinServiceInstructions()) {
			serviceUID = finServInst.getInstructionUID();
		}
		boolean emptyRepayInstructions = scheduleData.getRepayInstructions() == null ? true : false;

		// Fetch Next Payment Details from Finance for Salaried Postings Verification
		FinanceScheduleDetail orgNextSchd = getFinanceScheduleDetailDAO()
				.getNextSchPayment(financeMain.getFinReference(), curBDay);

		// Repayments Posting Process Execution
		// =====================================
		profitDetail = getProfitDetailsDAO().getFinProfitDetailsById(finReference);

		List<RepayScheduleDetail> repaySchdList = repayData.getRepayScheduleDetails();
		List<Object> returnList = processRepaymentPostings(financeMain, scheduleData.getFinanceScheduleDetails(),
				profitDetail, repaySchdList, repayData.getEventCodeRef(), scheduleData.getFeeRules(),
				scheduleData.getFinanceType().getFinDivision());

		if (!(Boolean) returnList.get(0)) {
			String errParm = (String) returnList.get(1);
			throw new InterfaceException("9999", errParm);
		}

		linkedTranId = (Long) returnList.get(1);
		partialPay = (Boolean) returnList.get(2);
		aeAmountCodes = (AEAmountCodes) returnList.get(3);
		finRepayQueues = (List<FinRepayQueue>) returnList.get(5);

		tranType = PennantConstants.TRAN_UPD;
		financeMain.setRecordType("");

		FinScheduleData oldFinSchdData = null;
		if (finRepayHeader.isSchdRegenerated()) {
			oldFinSchdData = getFinSchDataByFinRef(finReference, "");
			oldFinSchdData.setFinanceMain(financeMain);
			oldFinSchdData.setFinReference(finReference);
		}

		// Create log entry for Action for Schedule Modification
		FinLogEntryDetail entryDetail = new FinLogEntryDetail();
		entryDetail.setFinReference(finReference);
		entryDetail.setEventAction(finRepayHeader.getFinEvent());
		entryDetail.setSchdlRecal(finRepayHeader.isSchdRegenerated());
		entryDetail.setPostDate(curBDay);
		entryDetail.setReversalCompleted(false);
		long logKey = getFinLogEntryDetailDAO().save(entryDetail);

		// Save Schedule Details For Future Modifications
		if (finRepayHeader.isSchdRegenerated()) {
			listSave(oldFinSchdData, "_Log", logKey);
		}

		// Repayment Postings Details Process
		returnList = getRepayPostingUtil().UpdateScreenPaymentsProcess(financeMain,
				scheduleData.getFinanceScheduleDetails(), profitDetail, finRepayQueues, linkedTranId, partialPay,
				aeAmountCodes);

		// Finance Main Updation
		// =======================================
		financeMain = (FinanceMain) returnList.get(3);
		getFinanceMainDAO().update(financeMain, TableType.MAIN_TAB, false);

		// ScheduleDetails delete and save
		// =======================================
		listDeletion(finReference, "", emptyRepayInstructions);
		scheduleData.setFinanceScheduleDetails((List<FinanceScheduleDetail>) returnList.get(4));
		listSave(scheduleData, "", 0);

		// Save Fee Charges List
		// =======================================
		saveFeeChargeList(repayData, repayData.getFinRepayHeader().getFinEvent(), "");

		// Save Finance Repay Header Details
		finRepayHeader.setLinkedTranId(linkedTranId);
		getFinanceRepaymentsDAO().saveFinRepayHeader(finRepayHeader, TableType.MAIN_TAB);

		// Update Linked Transaction ID after Repayment Postings Process if workflow not found
		for (RepayScheduleDetail rpySchd : repayData.getRepayScheduleDetails()) {
			rpySchd.setLinkedTranId(linkedTranId);
			rpySchd.setFinReference(repayData.getFinanceDetail().getFinScheduleData().getFinReference());
		}
		getFinanceRepaymentsDAO().saveRpySchdList(repayData.getRepayScheduleDetails(), TableType.MAIN_TAB);

		if (!StringUtils.equals("API", repayData.getSourceId())) {
			// Save Document Details
			if (repayData.getFinanceDetail().getDocumentDetailsList() != null
					&& repayData.getFinanceDetail().getDocumentDetailsList().size() > 0) {
				List<AuditDetail> details = repayData.getFinanceDetail().getAuditDetailMap().get("DocumentDetails");
				details = processingDocumentDetailsList(details, "",
						repayData.getFinanceDetail().getFinScheduleData().getFinanceMain(),
						finRepayHeader.getFinEvent(), serviceUID);
				auditDetails.addAll(details);
				listDocDeletion(repayData.getFinanceDetail(), "_Temp");
			}

			// set Check list details Audit
			// =======================================
			if (repayData.getFinanceDetail().getFinanceCheckList() != null
					&& !repayData.getFinanceDetail().getFinanceCheckList().isEmpty()) {
				auditDetails
						.addAll(getCheckListDetailService().doApprove(repayData.getFinanceDetail(), "", serviceUID));
			}

			String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());

			// ScheduleDetails delete
			// =======================================
			listDeletion(finReference, "_Temp", false);

			// Fee charges deletion
			List<AuditDetail> tempAuditDetailList = new ArrayList<AuditDetail>();
			getFinFeeChargesDAO().deleteChargesBatch(financeMain.getFinReference(),
					repayData.getFinanceDetail().getModuleDefiner(), false, "_Temp");

			// Checklist Details delete
			// =======================================
			tempAuditDetailList
					.addAll(getCheckListDetailService().delete(repayData.getFinanceDetail(), "_Temp", tranType));

			// Delete Finance Repay Header
			getFinanceRepaymentsDAO().deleteFinRepayHeader(repayData.getFinRepayHeader(), "_Temp");
			getFinanceRepaymentsDAO().deleteRpySchdList(financeMain.getFinReference(), "_Temp");

			// Reset Repay Account ID On Finance Main for Correcting Audit Data
			getFinanceMainDAO().delete(financeMain, TableType.TEMP_TAB, false, true);

			RepayData tempRepayData = (RepayData) aAuditHeader.getAuditDetail().getModelData();
			FinanceMain tempfinanceMain = tempRepayData.getFinanceDetail().getFinScheduleData().getFinanceMain();
			auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1],
					tempfinanceMain.getBefImage(), tempfinanceMain));

			// Adding audit as deleted from TEMP table
			auditHeader.setAuditDetails(tempAuditDetailList);
			auditHeader.setAuditModule("FinanceDetail");
			getAuditHeaderDAO().addAudit(auditHeader);

			auditHeader.setAuditTranType(tranType);
			auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
					financeMain.getBefImage(), financeMain));

			// Adding audit as Insert/Update/deleted into main table
			auditHeader.setAuditDetails(auditDetails);
			auditHeader.setAuditModule("FinanceDetail");
			getAuditHeaderDAO().addAudit(auditHeader);
		}

		// Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(repayData);

		// ===========================================
		// Fetch Total Repayment Amount till Maturity date for Early Settlement
		if (FinanceConstants.CLOSE_STATUS_MATURED.equals(financeMain.getClosingStatus())) {

			// send Collateral DeMark request to Interface
			// ==========================================
			if (ImplementationConstants.COLLATERAL_INTERNAL) {
				if (ImplementationConstants.COLLATERAL_DELINK_AUTO) {
					getCollateralAssignmentValidation().saveCollateralMovements(financeMain.getFinReference());
				}
			} else {
				if (repayData.getFinanceDetail().getFinanceCollaterals() != null) {
					// Release the collateral.
				}
			}
		}

		// send Limit Amendment Request to ACP Interface and save log details
		// =======================================

		if (ImplementationConstants.LIMIT_INTERNAL) {

			FinanceDetail finDetails = repayData.getFinanceDetail();
			FinRepayHeader header = repayData.getFinRepayHeader();
			BigDecimal priAmt = BigDecimal.ZERO;

			for (RepayScheduleDetail rpySchd : header.getRepayScheduleDetails()) {
				priAmt = priAmt.add(rpySchd.getPrincipalSchdPayNow().add(rpySchd.getPriSchdWaivedNow()));
			}

			getLimitManagement().processLoanRepay(financeMain, finDetails.getCustomerDetails().getCustomer(), priAmt,
					StringUtils.trimToEmpty(finDetails.getFinScheduleData().getFinanceType().getProductCategory()));
		} else {
			getLimitCheckDetails().doProcessLimits(financeMain, FinanceConstants.AMENDEMENT);
		}
		// Save Salaried Posting Details
		saveFinSalPayment(repayData.getFinanceDetail().getFinScheduleData(), orgNextSchd, false);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Method for Repayment Details Posting Process
	 * 
	 * @param financeMain
	 * @param scheduleDetails
	 * @param repaySchdList
	 * @return
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 * @throws InvocationTargetException
	 */
	public List<Object> processRepaymentPostings(FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,
			FinanceProfitDetail profitDetail, List<RepayScheduleDetail> repaySchdList, String eventCodeRef,
			List<FeeRule> feeRuleList, String finDivision)
			throws IllegalAccessException, InterfaceException, InvocationTargetException {
		logger.debug("Entering");

		List<Object> returnList = new ArrayList<Object>();
		try {

			Map<String, FeeRule> feeRuleDetailsMap = null;
			if (feeRuleList != null && !feeRuleList.isEmpty()) {

				feeRuleDetailsMap = new HashMap<String, FeeRule>();
				for (FeeRule feeRule : feeRuleList) {
					if (!feeRuleDetailsMap.containsKey(feeRule.getFeeCode())) {
						feeRuleDetailsMap.put(feeRule.getFeeCode(), feeRule);
					}
				}
			}

			// FETCH Finance type Repayment Priority
			FinanceRepayPriority repayPriority = getFinanceRepayPriorityDAO()
					.getFinanceRepayPriorityById(financeMain.getFinType(), "");

			// Check Finance is RIA Finance Type or Not
			BigDecimal totRpyPri = BigDecimal.ZERO;
			BigDecimal totRpyPft = BigDecimal.ZERO;
			BigDecimal totRpyTds = BigDecimal.ZERO;
			BigDecimal totRefund = BigDecimal.ZERO;
			BigDecimal totSchdFee = BigDecimal.ZERO;

			List<FinRepayQueue> finRepayQueues = new ArrayList<FinRepayQueue>();
			Map<String, BigDecimal> totalsMap = new HashMap<String, BigDecimal>();
			FinRepayQueue finRepayQueue = null;

			for (int i = 0; i < repaySchdList.size(); i++) {

				finRepayQueue = new FinRepayQueue();
				finRepayQueue.setFinReference(financeMain.getFinReference());
				finRepayQueue.setRpyDate(repaySchdList.get(i).getSchDate());
				finRepayQueue.setFinRpyFor(repaySchdList.get(i).getSchdFor());
				finRepayQueue.setRcdNotExist(true);
				finRepayQueue = doWriteDataToBean(finRepayQueue, financeMain, repaySchdList.get(i), repayPriority);

				finRepayQueue.setRefundAmount(repaySchdList.get(i).getRefundReq());
				finRepayQueue.setPenaltyPayNow(repaySchdList.get(i).getPenaltyPayNow());
				finRepayQueue.setWaivedAmount(repaySchdList.get(i).getWaivedAmt());
				finRepayQueue.setPenaltyBal(
						repaySchdList.get(i).getPenaltyAmt().subtract(repaySchdList.get(i).getPenaltyPayNow()));
				finRepayQueue.setChargeType(repaySchdList.get(i).getChargeType());

				// Total Repayments Calculation for Principal, Profit & Refunds
				totRpyPri = totRpyPri.add(repaySchdList.get(i).getPrincipalSchdPayNow());
				totRpyPft = totRpyPft.add(repaySchdList.get(i).getProfitSchdPayNow());
				totRpyTds = totRpyTds.add(repaySchdList.get(i).getTdsSchdPayNow());
				totRefund = totRefund.add(repaySchdList.get(i).getRefundReq());

				// Fee Details
				totSchdFee = totSchdFee.add(repaySchdList.get(i).getSchdFeePayNow());

				finRepayQueues.add(finRepayQueue);

			}

			totalsMap.put("totRpyTot", totRpyPri.add(totRpyPft));
			totalsMap.put("totRpyPri", totRpyPri);
			totalsMap.put("totRpyPft", totRpyPft);
			totalsMap.put("totRpyTds", totRpyTds);
			totalsMap.put("totRefund", totRefund);
			// Schedule Early Settlement Insurance Refund

			// Fee Details
			totalsMap.put("schFeePay", totSchdFee);

			// Repayments Process For Schedule Repay List
			returnList = getRepayPostingUtil().postingsScreenRepayProcess(financeMain, scheduleDetails, profitDetail,
					finRepayQueues, totalsMap, eventCodeRef, feeRuleDetailsMap, finDivision);

			if ((Boolean) returnList.get(0)) {
				returnList.add(finRepayQueues);
			}

		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
			throw e;
		} catch (IllegalAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		} catch (InvocationTargetException e) {
			logger.error("Exception: ", e);
			throw e;
		}

		logger.debug("Leaving");
		return returnList;
	}

	/**
	 * Method for prepare RepayQueue data
	 * 
	 * @param resultSet
	 * @return
	 */
	private FinRepayQueue doWriteDataToBean(FinRepayQueue finRepayQueue, FinanceMain financeMain,
			RepayScheduleDetail rsd, FinanceRepayPriority repayPriority) {
		logger.debug("Entering");

		finRepayQueue.setBranch(financeMain.getFinBranch());
		finRepayQueue.setFinType(financeMain.getFinType());
		finRepayQueue.setCustomerID(financeMain.getCustID());

		if (repayPriority != null) {
			finRepayQueue.setFinPriority(repayPriority.getFinPriority());
		} else {
			finRepayQueue.setFinPriority(9999);
		}

		finRepayQueue.setSchdPft(rsd.getProfitSchd());
		finRepayQueue.setSchdPri(rsd.getPrincipalSchd());
		finRepayQueue.setSchdPftBal(rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid()));
		finRepayQueue.setSchdPriBal(rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid()));
		finRepayQueue.setSchdPriPayNow(rsd.getPrincipalSchdPayNow());
		finRepayQueue.setSchdPftPayNow(rsd.getProfitSchdPayNow());
		finRepayQueue.setSchdTdsPayNow(rsd.getTdsSchdPayNow());
		finRepayQueue.setSchdPriPaid(rsd.getPrincipalSchdPaid());
		finRepayQueue.setSchdPftPaid(rsd.getProfitSchdPaid());

		// Fee Details
		// 1. Schedule Fee Amount
		finRepayQueue.setSchdFee(rsd.getSchdFee());
		finRepayQueue.setSchdFeeBal(rsd.getSchdFeeBal());
		finRepayQueue.setSchdFeePayNow(rsd.getSchdFeePayNow());
		finRepayQueue.setSchdFeePaid(rsd.getSchdFeePaid());

		logger.debug("Leaving");
		return finRepayQueue;
	}

	@Override
	public List<FinanceRepayments> getFinRepayListByFinRef(String finRef, boolean isRpyCancelProc, String type) {
		return getFinanceRepaymentsDAO().getFinRepayListByFinRef(finRef, isRpyCancelProc, type);
	}

	@Override
	public FinanceProfitDetail getFinProfitDetailsById(String finReference) {
		return getProfitDetailsDAO().getFinProfitDetailsById(finReference);
	}

	/**
	 * Method to get Schedule related data.
	 * 
	 * @param finReference (String)
	 * @param isWIF        (boolean)
	 **/
	public FinScheduleData getFinSchDataByFinRef(String finReference, String type) {
		logger.debug("Entering");

		FinScheduleData finSchData = new FinScheduleData();
		finSchData.setFinanceScheduleDetails(
				getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type, false));
		finSchData.setDisbursementDetails(
				getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference, type, false));
		finSchData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference, type, false));
		logger.debug("Leaving");
		return finSchData;
	}

	public void listSave(FinScheduleData scheduleData, String tableType, long logKey) {
		logger.debug("Entering ");
		Map<Date, Integer> mapDateSeq = new HashMap<>();

		String finReference = scheduleData.getFinReference();
		FinanceMain fm = scheduleData.getFinanceMain();

		// Finance Schedule Details
		for (FinanceScheduleDetail curSchd : scheduleData.getFinanceScheduleDetails()) {
			curSchd.setLastMntBy(fm.getLastMntBy());
			curSchd.setFinReference(finReference);
			int seqNo = 0;

			if (mapDateSeq.containsKey(curSchd.getSchDate())) {
				seqNo = mapDateSeq.get(curSchd.getSchDate());
				mapDateSeq.remove(curSchd.getSchDate());
			}
			seqNo = seqNo + 1;
			mapDateSeq.put(curSchd.getSchDate(), seqNo);
			curSchd.setSchSeq(seqNo);
			curSchd.setLogKey(logKey);
		}

		financeScheduleDetailDAO.saveList(scheduleData.getFinanceScheduleDetails(), tableType, false);

		// Schedule Version Updating
		if (StringUtils.isBlank(tableType)) {
			financeMainDAO.updateSchdVersion(fm, false);
		}

		if (logKey != 0) {
			// Finance Disbursement Details
			mapDateSeq = new HashMap<>();
			Date curBDay = SysParamUtil.getAppDate();
			for (FinanceDisbursement dd : scheduleData.getDisbursementDetails()) {
				dd.setFinReference(finReference);
				dd.setDisbReqDate(curBDay);
				dd.setDisbIsActive(true);
				dd.setLogKey(logKey);
				dd.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				dd.setLastMntBy(scheduleData.getFinanceMain().getLastMntBy());
			}
			financeDisbursementDAO.saveList(scheduleData.getDisbursementDetails(), tableType, false);

		}

		// Finance Repay Instruction Details
		if (scheduleData.getRepayInstructions() != null) {
			for (RepayInstruction curSchd : scheduleData.getRepayInstructions()) {
				curSchd.setFinReference(finReference);
				curSchd.setLogKey(logKey);
			}
			repayInstructionDAO.saveList(scheduleData.getRepayInstructions(), tableType, false);
		}

		logger.debug("Leaving ");
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from getFinanceMainDAO().getErrorDetail with Error ID and
	 * language as parameters. 6) if any error/Warnings then assign the to auditHeader
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

		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
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
		RepayData repayData = (RepayData) auditDetail.getModelData();
		FinanceMain financeMain = repayData.getFinanceDetail().getFinScheduleData().getFinanceMain();

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

		// Checking For Commitment , Is it In Maintenance Or not
		if (StringUtils.trimToEmpty(financeMain.getRecordType()).equals(PennantConstants.RECORD_TYPE_NEW)
				&& "doApprove".equals(method) && StringUtils.isNotEmpty(financeMain.getFinCommitmentRef())) {

			Commitment tempcommitment = getCommitmentDAO().getCommitmentById(financeMain.getFinCommitmentRef(),
					"_Temp");
			if (tempcommitment != null && tempcommitment.isRevolving()) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "30538", errParm, valueParm), usrLanguage));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !financeMain.isWorkflow()) {
			financeMain.setBefImage(befFinanceMain);
		}

		return auditDetail;
	}

	/**
	 * Method for saving List of Fee Charge details
	 * 
	 * @param finDetail
	 * @param tableType
	 */
	public void saveFeeChargeList(RepayData repayData, String finEvent, String tableType) {
		logger.debug("Entering");

		String finReference = repayData.getFinanceDetail().getFinScheduleData().getFinanceMain().getFinReference();
		List<FeeRule> feeRuleList = repayData.getFinanceDetail().getFinScheduleData().getFeeRules();

		if (feeRuleList != null && feeRuleList.size() > 0) {
			// Finance Fee Charge Details
			for (int i = 0; i < feeRuleList.size(); i++) {
				feeRuleList.get(i).setFinReference(finReference);
				feeRuleList.get(i).setFinEvent(finEvent);
			}
			getFinFeeChargesDAO().saveChargesBatch(feeRuleList, false, tableType);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for prepare AuditHeader
	 * 
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		RepayData repayData = (RepayData) auditHeader.getAuditDetail().getModelData();
		FinanceDetail financeDetail = repayData.getFinanceDetail();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

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
		repayData.setFinanceDetail(financeDetail);
		auditHeader.getAuditDetail().setModelData(repayData);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving ");

		return auditHeader;
	}

	/**
	 * Method for process Repay calculations(Early, Partial)
	 * 
	 * @param financeDetail
	 * @param finServiceInst
	 * 
	 * @return RepayData
	 */
	public RepayData doCalcRepayments(RepayData repayData, FinanceDetail financeDetail,
			FinServiceInstruction finServiceInst) {
		logger.debug("Entering");

		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		String moduleDefiner = finServiceInst.getModuleDefiner();

		repayData.setFinReference(finServiceInst.getFinReference());

		// calculate repayments
		repayData = calculateRepayments(repayData, financeDetail, finServiceInst, false, null);

		if (moduleDefiner.equals(FinServiceEvent.EARLYSTLENQ)) {
			Cloner cloner = new Cloner();
			List<FinanceScheduleDetail> finschDetailList = cloner
					.deepClone(finScheduleData.getFinanceScheduleDetails());
			if (finServiceInst.getToDate() != null) {
				finschDetailList = rePrepareScheduleTerms(finschDetailList, finServiceInst.getToDate());

				financeDetail.getFinScheduleData().setFinanceScheduleDetails(finschDetailList);
			}
		}

		if (repayData != null) {
			repayData.setFinReference(financeDetail.getFinReference());
			repayData.setFinanceDetail(financeDetail);
		}

		logger.debug("Leaving");
		return repayData;
	}

	private RepayData calculateRepayments(RepayData repayData, FinanceDetail financeDetail,
			FinServiceInstruction finServiceInst, boolean isReCal, String method) {
		logger.debug("Entering");

		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceType financeType = finScheduleData.getFinanceType();
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchDetails = finScheduleData.getFinanceScheduleDetails();

		String moduleDefiner = finServiceInst.getModuleDefiner();
		repayData.setBuildProcess("R");
		repayData.getRepayMain().setRepayAmountNow(finServiceInst.getAmount());

		if (moduleDefiner.equals(FinServiceEvent.EARLYRPY)
				|| moduleDefiner.equals(FinServiceEvent.SCHDRPY)) {
			repayData.getRepayMain().setPayApportionment(PennantConstants.List_Select);
		} else {
			repayData.getRepayMain().setPayApportionment(PennantConstants.List_Select);
		}

		SubHeadRule subHeadRule = null;
		String sqlRule = null;

		if (moduleDefiner.equals(FinServiceEvent.EARLYSETTLE)
				|| moduleDefiner.equals(FinServiceEvent.EARLYSTLENQ)) {
			Rule rule = getRuleService().getApprovedRuleById("REFUND", RuleConstants.MODULE_REFUND,
					RuleConstants.EVENT_REFUND);
			if (rule != null) {
				sqlRule = rule.getSQLRule();
			}

			Customer customer = financeDetail.getCustomerDetails().getCustomer();
			if (customer == null) {
				customer = getCustomerDetailsService().getCustomerForPostings(financeMain.getCustID());
			}
			subHeadRule = new SubHeadRule();

			try {
				BeanUtils.copyProperties(subHeadRule, customer);
				// subHeadRule.setReqFinCcy(financeType.getFinCcy());
				subHeadRule.setReqProduct(financeType.getFinCategory());
				subHeadRule.setReqFinType(financeType.getFinType());
				subHeadRule.setReqFinPurpose(financeMain.getFinPurpose());
				subHeadRule.setReqFinDivision(financeType.getFinDivision());

				// Profit Details
				subHeadRule.setTOTALPFT(repayData.getRepayMain().getProfit());
				subHeadRule.setTOTALPFTBAL(repayData.getRepayMain().getProfitBalance());

				// Check For Early Settlement Enquiry -- on Selecting Future Date
				BigDecimal accrueValue = getFinanceDetailService().getAccrueAmount(financeMain.getFinReference());
				subHeadRule.setACCRUE(accrueValue);

				// Total Tenure
				int months = DateUtility.getMonthsBetween(financeMain.getMaturityDate(), financeMain.getFinStartDate(),
						false);
				subHeadRule.setTenure(months);

			} catch (IllegalAccessException e) {
				logger.error("Exception: ", e);
			} catch (InvocationTargetException e) {
				logger.error("Exception: ", e);
			}
		}

		repayData.getRepayMain().setPrincipalPayNow(BigDecimal.ZERO);
		repayData.getRepayMain().setProfitPayNow(BigDecimal.ZERO);
		repayData = getRepayCalculator().initiateRepay(repayData, financeMain, finSchDetails, sqlRule, subHeadRule,
				isReCal, method, finServiceInst.getFromDate(), moduleDefiner);

		// Calculation for Insurance Refund
		if (moduleDefiner.equals(FinServiceEvent.EARLYSETTLE)
				|| moduleDefiner.equals(FinServiceEvent.EARLYSTLENQ)) {
			int months = DateUtility.getMonthsBetween(financeMain.getMaturityDate(),
					repayData.getRepayMain().getRefundCalStartDate() == null ? financeMain.getMaturityDate()
							: repayData.getRepayMain().getRefundCalStartDate(),
					true);
			subHeadRule.setRemTenure(months);
		}

		logger.debug("Leaving");
		return repayData;
	}

	/**
	 * Method for Schedule Modifications with Effective Schedule Method
	 * 
	 * @param repayData
	 */
	public RepayData setEarlyRepayEffectOnSchedule(RepayData repayData, FinServiceInstruction finServiceInst) {
		logger.debug("Entering");

		// Schedule Recalculation Depends on Earlypay Effective Schedule method
		FinanceDetail financeDetail = repayData.getFinanceDetail();
		FinanceMain aFinanceMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();

		String method = null;
		// Schedule remodifications only when Effective Schedule Method modified
		if (!finServiceInst.getRecalType().equals(CalculationConstants.EARLYPAY_NOEFCT)) {

			method = finServiceInst.getRecalType();

			if (CalculationConstants.EARLYPAY_RECPFI.equals(method)
					|| CalculationConstants.EARLYPAY_ADMPFI.equals(method)) {
				aFinanceMain.setPftIntact(true);
			}

			if (repayData.getRepayMain().getEarlyRepayNewSchd() != null) {
				if (CalculationConstants.EARLYPAY_RECPFI.equals(method)) {
					repayData.getRepayMain().getEarlyRepayNewSchd().setRepayOnSchDate(false);
					repayData.getRepayMain().getEarlyRepayNewSchd().setPftOnSchDate(false);
					repayData.getRepayMain().getEarlyRepayNewSchd().setRepayAmount(BigDecimal.ZERO);
				}
				finScheduleData.getFinanceScheduleDetails().add(repayData.getRepayMain().getEarlyRepayNewSchd());
			}

			for (FinanceScheduleDetail detail : finScheduleData.getFinanceScheduleDetails()) {
				if (detail.getDefSchdDate().compareTo(repayData.getRepayMain().getEarlyPayOnSchDate()) == 0) {
					if (CalculationConstants.EARLYPAY_RECPFI.equals(method)) {
						detail.setEarlyPaid(detail.getEarlyPaid().add(repayData.getRepayMain().getEarlyPayAmount())
								.subtract(detail.getRepayAmount()));
						break;
					} else {
						final BigDecimal earlypaidBal = detail.getEarlyPaidBal();
						repayData.getRepayMain()
								.setEarlyPayAmount(repayData.getRepayMain().getEarlyPayAmount().add(earlypaidBal));
					}
				}
				if (detail.getDefSchdDate().compareTo(repayData.getRepayMain().getEarlyPayOnSchDate()) >= 0) {
					detail.setEarlyPaid(BigDecimal.ZERO);
					detail.setEarlyPaidBal(BigDecimal.ZERO);
				}
			}

			finScheduleData.setFinanceScheduleDetails(sortSchdDetails(finScheduleData.getFinanceScheduleDetails()));
			finScheduleData.setFinanceType(repayData.getFinanceDetail().getFinScheduleData().getFinanceType());

			// Calculation of Schedule Changes for Early Payment to change Schedule Effects Depends On Method
			finScheduleData = ScheduleCalculator.recalEarlyPaySchedule(finScheduleData,
					repayData.getRepayMain().getEarlyPayOnSchDate(), repayData.getRepayMain().getEarlyPayNextSchDate(),
					repayData.getRepayMain().getEarlyPayAmount(), method);

			financeDetail.setFinScheduleData(finScheduleData);
			aFinanceMain = finScheduleData.getFinanceMain();
			aFinanceMain
					.setWorkflowId(repayData.getFinanceDetail().getFinScheduleData().getFinanceMain().getWorkflowId());
			repayData.setFinanceDetail(financeDetail);// Object Setting for Future save purpose
			repayData.setFinanceDetail(financeDetail);

		}

		logger.debug("Leaving");
		return repayData;
	}

	public List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> financeScheduleDetail) {

		if (financeScheduleDetail != null && financeScheduleDetail.size() > 0) {
			Collections.sort(financeScheduleDetail, new Comparator<FinanceScheduleDetail>() {
				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					return DateUtility.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return financeScheduleDetail;
	}

	/**
	 * Method for Re=Prepare Schedule Term Data Based upon Till Paid Schedule Term
	 * 
	 * @param scheduleDetails
	 * @param toDate
	 * @return
	 */
	private List<FinanceScheduleDetail> rePrepareScheduleTerms(List<FinanceScheduleDetail> scheduleDetails,
			Date toDate) {
		logger.debug("Entering");

		Date paidTillTerm = toDate;

		for (FinanceScheduleDetail curSchd : scheduleDetails) {

			if (curSchd.getSchDate().compareTo(paidTillTerm) > 0) {
				break;
			}

			curSchd.setSchdPriPaid(curSchd.getPrincipalSchd());
			curSchd.setSchdPftPaid(curSchd.getProfitSchd());

			curSchd.setSchPftPaid(true);
			curSchd.setSchPriPaid(true);
		}

		logger.debug("Leaving");
		return scheduleDetails;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinanceRepayPriorityDAO getFinanceRepayPriorityDAO() {
		return financeRepayPriorityDAO;
	}

	public void setFinanceRepayPriorityDAO(FinanceRepayPriorityDAO financeRepayPriorityDAO) {
		this.financeRepayPriorityDAO = financeRepayPriorityDAO;
	}

	public RepaymentPostingsUtil getRepayPostingUtil() {
		return repayPostingUtil;
	}

	public void setRepayPostingUtil(RepaymentPostingsUtil repayPostingUtil) {
		this.repayPostingUtil = repayPostingUtil;
	}

	public FinRepayQueueDAO getFinRepayQueueDAO() {
		return finRepayQueueDAO;
	}

	public void setFinRepayQueueDAO(FinRepayQueueDAO finRepayQueueDAO) {
		this.finRepayQueueDAO = finRepayQueueDAO;
	}

	public AccountingSetDAO getAccountingSetDAO() {
		return accountingSetDAO;
	}

	public void setAccountingSetDAO(AccountingSetDAO accountingSetDAO) {
		this.accountingSetDAO = accountingSetDAO;
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

	public LimitCheckDetails getLimitCheckDetails() {
		return limitCheckDetails;
	}

	public void setLimitCheckDetails(LimitCheckDetails limitCheckDetails) {
		this.limitCheckDetails = limitCheckDetails;
	}

	public RuleService getRuleService() {
		return ruleService;
	}

	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public RepayCalculator getRepayCalculator() {
		return repayCalculator;
	}

	public void setRepayCalculator(RepayCalculator repayCalculator) {
		this.repayCalculator = repayCalculator;
	}

	public LimitManagement getLimitManagement() {
		return limitManagement;
	}

	public void setLimitManagement(LimitManagement limitManagement) {
		this.limitManagement = limitManagement;
	}

	public FinFeeDetailService getFinFeeDetailService() {
		return finFeeDetailService;
	}

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}

	public FinTypeFeesDAO getFinTypeFeesDAO() {
		return finTypeFeesDAO;
	}

	public void setFinTypeFeesDAO(FinTypeFeesDAO finTypeFeesDAO) {
		this.finTypeFeesDAO = finTypeFeesDAO;
	}
}
