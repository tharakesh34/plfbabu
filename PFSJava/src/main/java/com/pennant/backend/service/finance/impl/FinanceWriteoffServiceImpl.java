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
 * FileName    		:  FinanceWriteoffServiceImpl.java												*                           
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
import com.pennant.backend.dao.finance.FinanceWriteoffDAO;
import com.pennant.backend.dao.financemanagement.ProvisionDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeFeesDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceWriteoff;
import com.pennant.backend.model.finance.FinanceWriteoffHeader;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.FinanceWriteoffService;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.TableType;
import com.rits.cloning.Cloner;

public class FinanceWriteoffServiceImpl extends GenericFinanceDetailService implements FinanceWriteoffService {

	private static final Logger			logger	= Logger.getLogger(FinanceWriteoffServiceImpl.class);

	private FinanceWriteoffDAO			financeWriteoffDAO;
	private ProvisionDAO				provisionDAO;
	private FinanceReferenceDetailDAO	financeReferenceDetailDAO;
	private FinFeeDetailService			finFeeDetailService;
	private FinTypeFeesDAO				finTypeFeesDAO;

	public FinanceWriteoffServiceImpl() {
		super();
	}

	/**
	 * Method for Fetching FInance Details & Repay Schedule Details
	 * 
	 * @param finReference
	 * @return
	 */
	@Override
	public FinanceWriteoffHeader getFinanceWriteoffDetailById(String finReference, String type, String userRole,
			String procEdtEvent) {
		logger.debug("Entering");

		//Finance Details
		FinanceWriteoffHeader writeoffHeader = new FinanceWriteoffHeader();
		writeoffHeader.setFinReference(finReference);
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		scheduleData.setFinReference(finReference);
		writeoffHeader.setFinanceDetail(financeDetail);

		scheduleData.setFinanceMain(getFinanceMainDAO().getFinanceMainById(finReference, type, false));

		if (scheduleData.getFinanceMain() != null) {

			//Finance Schedule Details
			scheduleData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference,
					type, false));
			scheduleData.setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference,
					"", false));

			scheduleData.setFeeRules(getFinFeeChargesDAO().getFeeChargesByFinRef(finReference,
					FinanceConstants.FINSER_EVENT_WRITEOFF, false, ""));

			if (StringUtils.isNotBlank(scheduleData.getFinanceMain().getPromotionCode())) {
				financeDetail.setFinTypeFeesList(getFinTypeFeesDAO().getFinTypeFeesList(
						scheduleData.getFinanceMain().getPromotionCode(), FinanceConstants.FINSER_EVENT_WRITEOFF,
						"_AView", false, FinanceConstants.MODULEID_PROMOTION));
			} else {
				financeDetail.setFinTypeFeesList(getFinTypeFeesDAO().getFinTypeFeesList(
						scheduleData.getFinanceMain().getFinType(), FinanceConstants.FINSER_EVENT_WRITEOFF, "_AView",
						false, FinanceConstants.MODULEID_FINTYPE));
			}

			scheduleData.setRepayDetails(getFinanceRepaymentsDAO().getFinRepayListByFinRef(finReference, false, ""));
			scheduleData.setPenaltyDetails(getRecoveryDAO().getFinancePenaltysByFinRef(finReference, ""));

			scheduleData.setFinanceType(getFinanceTypeDAO().getFinanceTypeByID(
					scheduleData.getFinanceMain().getFinType(), "_AView"));

			//Finance Customer Details			
			if (scheduleData.getFinanceMain().getCustID() != 0
					&& scheduleData.getFinanceMain().getCustID() != Long.MIN_VALUE) {
				financeDetail.setCustomerDetails(getCustomerDetailsService().getCustomerDetailsById(
						scheduleData.getFinanceMain().getCustID(), true, "_View"));
			}

			// Finance Fee Details
			scheduleData.setFinFeeDetailList(getFinFeeDetailService()
					.getFinFeeDetailById(finReference, false, "_TView"));

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
						AccountEventConstants.ACCEVENT_WRITEOFF, "_AView", 0));
			}

			//Finance Stage Accounting Posting Details 
			//=======================================
			financeDetail.setStageTransactionEntries(getTransactionEntryDAO().getListTransactionEntryByRefType(finType,
					StringUtils.isEmpty(procEdtEvent) ? FinanceConstants.FINSER_EVENT_ORG : procEdtEvent,
					FinanceConstants.PROCEDT_STAGEACC, userRole, "_AEView", true));

			// Docuument Details
			financeDetail.setDocumentDetailsList(getDocumentDetailsDAO().getDocumentDetailsByRef(finReference,
					FinanceConstants.MODULE_NAME, procEdtEvent, "_View"));

			if (StringUtils.isNotBlank(scheduleData.getFinanceMain().getRecordType())) {

				//Finance Writeoff Details
				writeoffHeader
						.setFinanceWriteoff(getFinanceWriteoffDAO().getFinanceWriteoffById(finReference, "_Temp"));

			} else {

				scheduleData.getFinanceMain().setNewRecord(true);

				//Finance Writeoff Details
				FinanceWriteoff financeWriteoff = getFinanceScheduleDetailDAO().getWriteoffTotals(finReference);
				FinanceProfitDetail detail = getProfitDetailsDAO().getProfitDetailForWriteOff(finReference);
				if (detail != null) {
					financeWriteoff.setCurODPri(detail.getODPrincipal());
					financeWriteoff.setCurODPft(detail.getODProfit());
					financeWriteoff.setPenaltyAmount(detail.getPenaltyDue());
				}
				Provision provision = getProvisionDAO().getProvisionById(finReference, "");
				if (provision != null) {
					financeWriteoff.setProvisionedAmount(provision.getProvisionedAmt());
				}

				writeoffHeader.setFinanceWriteoff(financeWriteoff);
			}
		}

		logger.debug("Leaving");
		return writeoffHeader;
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
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws InterfaceException, IllegalAccessException,
			InvocationTargetException {
		logger.debug("Entering");

		aAuditHeader = businessValidation(aAuditHeader, "saveOrUpdate");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!aAuditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);

		FinanceWriteoffHeader header = (FinanceWriteoffHeader) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = header.getFinanceDetail().getFinScheduleData().getFinanceMain();

		//Finance Stage Accounting Process
		//=======================================
		auditHeader = executeStageAccounting(auditHeader);
		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}

		String finReference = financeMain.getFinReference();
		Date curBDay = DateUtility.getAppDate();

		TableType tableType = TableType.MAIN_TAB;
		if (financeMain.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		financeMain.setRcdMaintainSts(FinanceConstants.FINSER_EVENT_WRITEOFF);
		if (tableType == TableType.MAIN_TAB) {
			financeMain.setRcdMaintainSts("");
		}

		//Repayments Postings Details Process Execution
		long linkedTranId = 0;
		FinanceProfitDetail profitDetail = null;

		FinScheduleData scheduleData = header.getFinanceDetail().getFinScheduleData();

		if (!financeMain.isWorkflow()) {
			profitDetail = getProfitDetailsDAO().getFinProfitDetailsById(finReference);

			AEEvent aeEvent = AEAmounts.procAEAmounts(financeMain, scheduleData.getFinanceScheduleDetails(),
					profitDetail, AccountEventConstants.ACCEVENT_WRITEOFF, curBDay, financeMain.getMaturityDate());

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

			linkedTranId = aeEvent.getLinkedTranId();
		}

		//Linked Transaction Id Updation
		FinanceWriteoff financeWriteoff = header.getFinanceWriteoff();
		financeWriteoff.setLinkedTranId(linkedTranId);

		// Finance Main Details Save And Update
		//=======================================
		if (financeMain.isNew()) {

			getFinanceMainDAO().save(financeMain, tableType, false);

			//Save Finance Writeoff Details
			int seqNo = getFinanceWriteoffDAO().getMaxFinanceWriteoffSeq(finReference,
					financeWriteoff.getWriteoffDate(), "");
			financeWriteoff.setSeqNo(seqNo + 1);
			getFinanceWriteoffDAO().save(financeWriteoff, tableType.getSuffix());

		} else {
			getFinanceMainDAO().update(financeMain, tableType, false);

			//Update Writeoff Details depends on Workflow
			getFinanceWriteoffDAO().update(financeWriteoff, tableType.getSuffix());
		}

		// Save schedule details
		//=======================================
		if (!financeMain.isNewRecord()) {

			/*
			 * if(tableType.equals("") && financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){ //Fetch
			 * Existing data before Modification
			 * 
			 * FinScheduleData old_finSchdData = null; if(finRepayHeader.isSchdRegenerated()){ old_finSchdData =
			 * getFinSchDataByFinRef(finReference, ""); old_finSchdData.setFinanceMain(financeMain);
			 * old_finSchdData.setFinReference(finReference); }
			 * 
			 * //Create log entry for Action for Schedule Modification FinLogEntryDetail entryDetail = new
			 * FinLogEntryDetail(); entryDetail.setFinReference(finReference);
			 * entryDetail.setEventAction(finRepayHeader.getFinEvent());
			 * entryDetail.setSchdlRecal(finRepayHeader.isSchdRegenerated()); entryDetail.setPostDate(curBDay);
			 * entryDetail.setReversalCompleted(false); long logKey = getFinLogEntryDetailDAO().save(entryDetail);
			 * 
			 * //Save Schedule Details For Future Modifications if(finRepayHeader.isSchdRegenerated()){
			 * listSave(old_finSchdData, "_Log", logKey); } }
			 */

			listDeletion(finReference, tableType.getSuffix());
			listSave(scheduleData, tableType.getSuffix(), 0);
		} else {
			listDeletion(finReference, tableType.getSuffix());
			listSave(scheduleData, tableType.getSuffix(), 0);
		}

		// Save Fee Charges List
		//=======================================
		if (tableType == TableType.TEMP_TAB) {
			getFinFeeChargesDAO().deleteChargesBatch(financeMain.getFinReference(),
					header.getFinanceDetail().getModuleDefiner(), false, tableType.getSuffix());
		}
		saveFeeChargeList(header.getFinanceDetail().getFinScheduleData(), header.getFinanceDetail().getModuleDefiner(),
				false, tableType.getSuffix());

		// Save Document Details
		if (header.getFinanceDetail().getDocumentDetailsList() != null
				&& header.getFinanceDetail().getDocumentDetailsList().size() > 0) {
			List<AuditDetail> details = header.getFinanceDetail().getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, tableType.getSuffix(), header.getFinanceDetail()
					.getFinScheduleData().getFinanceMain(), header.getFinanceDetail().getModuleDefiner());
			auditDetails.addAll(details);
		}

		// set Finance Check List audit details to auditDetails
		//=======================================
		if (header.getFinanceDetail().getFinanceCheckList() != null
				&& !header.getFinanceDetail().getFinanceCheckList().isEmpty()) {
			auditDetails.addAll(getCheckListDetailService().saveOrUpdate(header.getFinanceDetail(),
					tableType.getSuffix()));
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], financeMain
				.getBefImage(), financeMain));

		auditHeader.setAuditDetails(auditDetails);
		auditHeader.setAuditModule("FinanceDetail");
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Method to delete schedule lists.
	 * 
	 * @param finDetail
	 * @param tableType
	 * @param isWIF
	 */
	public void listDeletion(String finReference, String tableType) {
		getFinanceScheduleDetailDAO().deleteByFinReference(finReference, tableType, false, 0);
	}

	@Override
	public List<FinanceScheduleDetail> getFinScheduleDetails(String finReference) {
		return getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, "", false);
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
	public AuditHeader doReject(AuditHeader auditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tranType = PennantConstants.TRAN_DEL;

		FinanceWriteoffHeader header = (FinanceWriteoffHeader) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = header.getFinanceDetail().getFinScheduleData().getFinanceMain();

		// Cancel All Transactions done by Finance Reference
		//=======================================
		cancelStageAccounting(financeMain.getFinReference(), header.getFinanceDetail().getModuleDefiner());

		//Finance Writeoff Details
		getFinanceWriteoffDAO().delete(financeMain.getFinReference(), "_Temp");

		// Save Document Details
		if (header.getFinanceDetail().getDocumentDetailsList() != null
				&& header.getFinanceDetail().getDocumentDetailsList().size() > 0) {
			for (DocumentDetails docDetails : header.getFinanceDetail().getDocumentDetailsList()) {
				docDetails.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
			List<AuditDetail> details = header.getFinanceDetail().getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, "_Temp", header.getFinanceDetail().getFinScheduleData()
					.getFinanceMain(), header.getFinanceDetail().getModuleDefiner());
			auditHeader.setAuditDetails(details);
		}

		// Fee charges deletion
		getFinFeeChargesDAO().deleteChargesBatch(financeMain.getFinReference(),
				header.getFinanceDetail().getModuleDefiner(), false, "_Temp");

		// Checklist Details delete
		//=======================================
		auditHeader.getAuditDetails().addAll(
				getCheckListDetailService().delete(header.getFinanceDetail(), "_Temp", tranType));

		// ScheduleDetails deletion
		listDeletion(financeMain.getFinReference(), "_Temp");
		getFinanceMainDAO().delete(financeMain, TableType.TEMP_TAB, false, false);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], financeMain
				.getBefImage(), financeMain));
		auditHeader.setAuditModule("FinanceDetail");
		getAuditHeaderDAO().addAudit(auditHeader);

		//Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(header);

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
	 * @throws AccountNotFoundException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader) throws InterfaceException, IllegalAccessException,
			InvocationTargetException {
		logger.debug("Entering");

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		aAuditHeader = businessValidation(aAuditHeader, "doApprove");
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);

		FinanceWriteoffHeader header = (FinanceWriteoffHeader) auditHeader.getAuditDetail().getModelData();
		Date curBDay = DateUtility.getAppDate();

		//Execute Accounting Details Process
		//=======================================
		FinanceMain financeMain = header.getFinanceDetail().getFinScheduleData().getFinanceMain();
		String finReference = financeMain.getFinReference();

		// Fetch Next Payment Details from Finance for Salaried Postings Verification
		FinanceScheduleDetail orgNextSchd = getFinanceScheduleDetailDAO().getNextSchPayment(finReference, curBDay);

		//Finance Stage Accounting Process
		//=======================================
		auditHeader = executeStageAccounting(auditHeader);
		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}

		financeMain.setRcdMaintainSts("");
		financeMain.setRoleCode("");
		financeMain.setNextRoleCode("");
		financeMain.setTaskId("");
		financeMain.setNextTaskId("");
		financeMain.setWorkflowId(0);

		FinScheduleData scheduleData = header.getFinanceDetail().getFinScheduleData();

		//Finance Write off Posting Process Execution
		//=====================================
		
		executeAccountingProcess(aAuditHeader);
		
/*
		FinanceProfitDetail profitDetail = getProfitDetailsDAO().getFinPftDetailForBatch(finReference);
		profitDetail = getAccrualService().calProfitDetails(financeMain, scheduleData.getFinanceScheduleDetails(),
				profitDetail, curBDay);

		AEEvent aeEvent = AEAmounts.procCalAEAmounts(profitDetail, AccountEventConstants.ACCEVENT_WRITEOFF, curBDay,
				financeMain.getMaturityDate());

		BigDecimal totalPftSchdOld = BigDecimal.ZERO;
		BigDecimal totalPftCpzOld = BigDecimal.ZERO;
		//For New Records Profit Details will be set inside the AEAmounts 
		if (profitDetail == null) {
			profitDetail = new FinanceProfitDetail();
		} else {
			totalPftSchdOld = profitDetail.getTotalPftSchd();
			totalPftCpzOld = profitDetail.getTotalPftCpz();
		}

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		HashMap<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

		BigDecimal totalPftSchdNew = profitDetail.getTotalPftSchd();
		BigDecimal totalPftCpzNew = profitDetail.getTotalPftCpz();

		amountCodes.setPftChg(totalPftSchdNew.subtract(totalPftSchdOld));
		amountCodes.setCpzChg(totalPftCpzNew.subtract(totalPftCpzOld));
		aeEvent.setModuleDefiner(FinanceConstants.FINSER_EVENT_WRITEOFF);
		amountCodes.setDisburse(financeMain.getFinCurrAssetValue());
		amountCodes.getDeclaredFieldValues(dataMap);
		header.getFinanceDetail().getFinScheduleData().getFinanceType().getDeclaredFieldValues(dataMap);
		financeMain.getDeclaredFieldValues(dataMap);

		prepareFeeRulesMap(amountCodes, dataMap, header.getFinanceDetail());

 		aeEvent.setDataMap(dataMap);
		
		getPostingsPreparationUtil().postAccounting(aeEvent);
	//	linkedTranId = getAccountingResults(auditHeader, header.getFinanceDetail(), accountingSetEntries, curBDay,
	//			aeEvent);
*/		

		//Update the financemain
		tranType = PennantConstants.TRAN_UPD;
		financeMain.setRecordType("");
		financeMain.setFinIsActive(false);
		financeMain.setClosingStatus(FinanceConstants.CLOSE_STATUS_WRITEOFF);
		getFinanceMainDAO().update(financeMain, TableType.MAIN_TAB, false);

		//Save Finance WriteOff Details
		FinanceWriteoff financeWriteoff = header.getFinanceWriteoff();
	//	financeWriteoff.setLinkedTranId(aeEvent.getLinkedTranId());
		getFinanceWriteoffDAO().save(financeWriteoff, "");

		/*
		 * FinScheduleData old_finSchdData = null; if(finRepayHeader.isSchdRegenerated()){ old_finSchdData =
		 * getFinSchDataByFinRef(finReference, ""); old_finSchdData.setFinanceMain(financeMain);
		 * old_finSchdData.setFinReference(finReference); }
		 * 
		 * //Create log entry for Action for Schedule Modification FinLogEntryDetail entryDetail = new
		 * FinLogEntryDetail(); entryDetail.setFinReference(finReference);
		 * entryDetail.setEventAction(finRepayHeader.getFinEvent());
		 * entryDetail.setSchdlRecal(finRepayHeader.isSchdRegenerated()); entryDetail.setPostDate(curBDay);
		 * entryDetail.setReversalCompleted(false); long logKey = getFinLogEntryDetailDAO().save(entryDetail);
		 * 
		 * //Save Schedule Details For Future Modifications if(finRepayHeader.isSchdRegenerated()){
		 * listSave(old_finSchdData, "_Log", logKey); }
		 */

		// ScheduleDetails delete and save
		//=======================================
		listDeletion(finReference, "");
		listSave(scheduleData, "", 0);

		// Save Fee Charges List
		//=======================================
		saveFeeChargeList(header.getFinanceDetail().getFinScheduleData(), header.getFinanceDetail().getModuleDefiner(),
				false, "");

		// Save Document Details
		if (header.getFinanceDetail().getDocumentDetailsList() != null
				&& header.getFinanceDetail().getDocumentDetailsList().size() > 0) {
			List<AuditDetail> details = header.getFinanceDetail().getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, "", header.getFinanceDetail().getFinScheduleData()
					.getFinanceMain(), header.getFinanceDetail().getModuleDefiner());
			auditDetails.addAll(details);
			listDocDeletion(header.getFinanceDetail(), "_Temp");
		}

		// set Finance Check List audit details to auditDetails
		//=======================================
		if (header.getFinanceDetail().getFinanceCheckList() != null
				&& !header.getFinanceDetail().getFinanceCheckList().isEmpty()) {
			auditDetails.addAll(getCheckListDetailService().doApprove(header.getFinanceDetail(), ""));
		}

		//Update Profit Details 
	//	getProfitDetailsDAO().update(profitDetail, false);

		// Schedule Details delete
		//=======================================
		listDeletion(finReference, "_Temp");

		// Fee charges deletion
		List<AuditDetail> tempAuditDetailList = new ArrayList<AuditDetail>();
		getFinFeeChargesDAO().deleteChargesBatch(financeMain.getFinReference(),
				header.getFinanceDetail().getModuleDefiner(), false, "_Temp");

		// Checklist Details delete
		//=======================================
		tempAuditDetailList.addAll(getCheckListDetailService().delete(header.getFinanceDetail(), "_Temp", tranType));

		//Finance Writeoff Details
		getFinanceWriteoffDAO().delete(financeMain.getFinReference(), "_Temp");

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
		getFinanceMainDAO().delete(financeMain, TableType.TEMP_TAB, false, true);

		// Adding audit as deleted from TEMP table
		auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1],
				financeMain.getBefImage(), financeMain));
		auditHeader.setAuditDetails(tempAuditDetailList);
		auditHeader.setAuditModule("FinanceDetail");
		getAuditHeaderDAO().addAudit(auditHeader);

		// Adding audit as Insert/Update/deleted into main table
		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], financeMain
				.getBefImage(), financeMain));
		auditHeader.setAuditDetails(auditDetails);
		auditHeader.setAuditModule("FinanceDetail");
		getAuditHeaderDAO().addAudit(auditHeader);

		//Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(header);

		// Save Salaried Posting Details
		saveFinSalPayment(header.getFinanceDetail().getFinScheduleData(), orgNextSchd, true);
		//updating the processed with 1 in finstageAccountingLog
		getFinStageAccountingLogDAO().update(financeMain.getFinReference(),
				header.getFinanceDetail().getModuleDefiner(), false);

		logger.debug("Leaving");
		return auditHeader;
	}
		
	
	/**
	 * Method for Execute posting Details on Core Banking Side
	 * 
	 * @param auditHeader
	 * @param curBDay
	 * @return
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 */
	public AuditHeader executeAccountingProcess(AuditHeader auditHeader)  throws InterfaceException{
		logger.debug("Entering");

		List<ReturnDataSet> accountingSetEntries = new ArrayList<ReturnDataSet>();

		FinanceWriteoffHeader financeWriteoffHeader = (FinanceWriteoffHeader) auditHeader.getAuditDetail().getModelData();
		FinanceWriteoff financeWriteoff = financeWriteoffHeader.getFinanceWriteoff();
		FinanceDetail financeDetail = financeWriteoffHeader.getFinanceDetail();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		AEEvent aeEvent = new AEEvent();
		FinanceProfitDetail pftDetail = new FinanceProfitDetail();
		pftDetail = getProfitDetailsDAO().getFinProfitDetailsById(financeMain.getFinReference());

		try {
			aeEvent = prepareAccountingData(financeDetail, aeEvent, pftDetail);
			aeEvent.setPostingUserBranch(auditHeader.getAuditBranchCode());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		HashMap<String, Object>	dataMap = aeEvent.getDataMap();
		dataMap = prepareFeeRulesMap(amountCodes, dataMap, financeDetail);
		dataMap = amountCodes.getDeclaredFieldValues(dataMap);
		financeWriteoff.getDeclaredFieldValues(dataMap);
		aeEvent.setDataMap(dataMap);

		//getAccountingResults(auditHeader, financeDetail, accountingSetEntries, curBDay, aeEvent);
		getPostingsPreparationUtil().postAccounting(aeEvent);
		financeWriteoff.setLinkedTranId(aeEvent.getLinkedTranId());
 
		if (auditHeader.getErrorMessage() == null || auditHeader.getErrorMessage().size() == 0) {

			// save Postings
			if (accountingSetEntries != null && !accountingSetEntries.isEmpty()) {
				getPostingsDAO().saveBatch(accountingSetEntries);
			}

			// Save/Update Finance Profit Details
			boolean isNew = false;

			if (StringUtils.equals(financeMain.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
				isNew = true;
			}

			doSave_PftDetails(pftDetail, isNew);

			//Account Details Update
			if (accountingSetEntries != null && !accountingSetEntries.isEmpty()) {
				getAccountProcessUtil().procAccountUpdate(accountingSetEntries);
			}
		}

		logger.debug("Leaving");
		return auditHeader;
	}
	
	
	/**
	 * Preparing Accounting Data 
	 * @param financeDetail
	 * @param executingMap
	 * @return
	 * @throws InterruptedException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private AEEvent prepareAccountingData(
			FinanceDetail financeDetail, AEEvent aeEvent, FinanceProfitDetail profitDetail) throws InterruptedException,
			IllegalAccessException, InvocationTargetException {

		Date curBDay = DateUtility.getAppDate();
		String eventCode = financeDetail.getAccountingEventCode();

		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		if (StringUtils.isBlank(eventCode)) {
			eventCode = PennantApplicationUtil.getEventCode(finMain.getFinStartDate());
		}
		
		BigDecimal totalPftSchdOld = BigDecimal.ZERO;
		BigDecimal totalPftCpzOld = BigDecimal.ZERO;
		FinanceProfitDetail newProfitDetail = new FinanceProfitDetail();
		if (profitDetail != null) {//FIXME
			BeanUtils.copyProperties(profitDetail, newProfitDetail);
			totalPftSchdOld = profitDetail.getTotalPftSchd();
			totalPftCpzOld = profitDetail.getTotalPftCpz();
		}
		
		aeEvent = AEAmounts.procAEAmounts(finMain, finSchdDetails, profitDetail, eventCode, curBDay, curBDay);
		if (StringUtils.isNotBlank(finMain.getPromotionCode())) {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getPromotionCode(), eventCode, FinanceConstants.MODULEID_PROMOTION));
		} else {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getFinType(), eventCode, FinanceConstants.MODULEID_FINTYPE));
		}

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		getAccrualService().calProfitDetails(finMain, finSchdDetails, newProfitDetail, curBDay);
		if (!FinanceConstants.BPI_NO.equals(finMain.getBpiTreatment())) {
			amountCodes.setBpi(finMain.getBpiAmount());
		}
		
		BigDecimal totalPftSchdNew = newProfitDetail.getTotalPftSchd();
		BigDecimal totalPftCpzNew = newProfitDetail.getTotalPftCpz();

		amountCodes.setPftChg(totalPftSchdNew.subtract(totalPftSchdOld));
		amountCodes.setCpzChg(totalPftCpzNew.subtract(totalPftCpzOld));

		aeEvent.setModuleDefiner(FinanceConstants.FINSER_EVENT_ORG);
		amountCodes.setDisburse(finMain.getFinCurrAssetValue());

		if (finMain.isNewRecord() || PennantConstants.RECORD_TYPE_NEW.equals(finMain.getRecordType())) {
			aeEvent.setNewRecord(true);
		}
		return aeEvent;
	}
	
	public void listSave(FinScheduleData scheduleData, String tableType, long logKey) {
		logger.debug("Entering ");
		HashMap<Date, Integer> mapDateSeq = new HashMap<Date, Integer>();

		// Finance Schedule Details
		for (int i = 0; i < scheduleData.getFinanceScheduleDetails().size(); i++) {

			FinanceScheduleDetail curSchd = scheduleData.getFinanceScheduleDetails().get(i);
			curSchd.setLastMntBy(scheduleData.getFinanceMain().getLastMntBy());
			curSchd.setFinReference(scheduleData.getFinReference());
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

		getFinanceScheduleDetailDAO().saveList(scheduleData.getFinanceScheduleDetails(), tableType, false);

		if (logKey != 0) {

			/*
			 * // Finance Disbursement Details mapDateSeq = new HashMap<Date, Integer>(); Date curBDay =
			 * DateUtility.getAppDate(); for (int i = 0; i < scheduleData.getDisbursementDetails().size(); i++) {
			 * scheduleData.getDisbursementDetails().get(i).setFinReference(scheduleData.getFinReference());
			 * scheduleData.getDisbursementDetails().get(i).setDisbReqDate(curBDay); int seqNo = 0;
			 * 
			 * if (mapDateSeq.containsKey(scheduleData.getDisbursementDetails().get(i).getDisbDate())) { seqNo =
			 * mapDateSeq.get(scheduleData.getDisbursementDetails().get(i).getDisbDate());
			 * mapDateSeq.remove(scheduleData.getDisbursementDetails().get(i).getDisbDate()); } seqNo = seqNo + 1;
			 * 
			 * mapDateSeq.put(scheduleData.getDisbursementDetails().get(i).getDisbDate(), seqNo);
			 * scheduleData.getDisbursementDetails().get(i).setDisbSeq(seqNo);
			 * scheduleData.getDisbursementDetails().get(i).setDisbIsActive(true);
			 * scheduleData.getDisbursementDetails().get(i).setDisbDisbursed(true);
			 * scheduleData.getDisbursementDetails().get(i).setLogKey(logKey); }
			 * getFinanceDisbursementDAO().saveList(scheduleData.getDisbursementDetails(), tableType, false);
			 * 
			 * //Finance Repay Instruction Details if(scheduleData.getRepayInstructions() != null){ for (int i = 0; i <
			 * scheduleData.getRepayInstructions().size(); i++) { RepayInstruction curSchd =
			 * scheduleData.getRepayInstructions().get(i);
			 * 
			 * curSchd.setFinReference(scheduleData.getFinReference()); curSchd.setLogKey(logKey); }
			 * getRepayInstructionDAO().saveList(scheduleData.getRepayInstructions(), tableType, false); }
			 */
		}

		logger.debug("Leaving ");
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
		FinanceWriteoffHeader header = (FinanceWriteoffHeader) auditDetail.getModelData();
		FinanceMain financeMain = header.getFinanceDetail().getFinScheduleData().getFinanceMain();

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
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
							"41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
					// records type is new
					if (befFinanceMain != null || tempFinanceMain != null) { // if
						// records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befFinanceMain == null || tempFinanceMain != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
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
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
							"41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldFinanceMain != null && !oldFinanceMain.getLastMntOn().equals(befFinanceMain.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41003", errParm, valueParm), usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41004", errParm, valueParm), usrLanguage));
						}
					}
				}
			} else {

				if (tempFinanceMain == null) { // if records not exists in the
					// Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
							"41005", errParm, valueParm), usrLanguage));
				}

				if (tempFinanceMain != null && oldFinanceMain != null
						&& !oldFinanceMain.getLastMntOn().equals(tempFinanceMain.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
							"41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		// Checking , if Customer is in EOD process or not. if Yes, not allowed to do an action
		int eodProgressCount = getCustomerQueuingDAO().getProgressCountByCust(financeMain.getCustID());

		// If Customer Exists in EOD Processing, Not allowed to Maintenance till completion
		if (eodProgressCount > 0) {
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "60203",
					errParm, valueParm), usrLanguage));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !financeMain.isWorkflow()) {
			auditDetail.setBefImage(befFinanceMain);
		}

		return auditDetail;
	}

	/**
	 * Method for prepare AuditHeader
	 * 
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinanceWriteoffHeader financeWriteoffHeader = (FinanceWriteoffHeader) auditHeader.getAuditDetail()
				.getModelData();
		FinanceDetail financeDetail = financeWriteoffHeader.getFinanceDetail();
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
		financeWriteoffHeader.setFinanceDetail(financeDetail);
		auditHeader.getAuditDetail().setModelData(financeWriteoffHeader);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving ");

		return auditHeader;
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

	public FinanceWriteoffDAO getFinanceWriteoffDAO() {
		return financeWriteoffDAO;
	}

	public void setFinanceWriteoffDAO(FinanceWriteoffDAO financeWriteoffDAO) {
		this.financeWriteoffDAO = financeWriteoffDAO;
	}

	public FinanceReferenceDetailDAO getFinanceReferenceDetailDAO() {
		return financeReferenceDetailDAO;
	}

	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
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
