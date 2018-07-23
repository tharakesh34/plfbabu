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
 * FileName    		:  ReceiptCancellationServiceImpl.java												*                           
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.LatePayMarkingService;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.RepaymentPostingsUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.applicationmaster.BounceReasonDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinFeeReceiptDAO;
import com.pennant.backend.dao.finance.FinLogEntryDetailDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinStageAccountingLogDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.insurancedetails.FinInsurancesDAO;
import com.pennant.backend.dao.receipts.DepositChequesDAO;
import com.pennant.backend.dao.receipts.DepositDetailsDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.rulefactory.FinFeeScheduleDetailDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.applicationmaster.BounceReason;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.DepositCheques;
import com.pennant.backend.model.finance.DepositMovements;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinSchFrqInsurance;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.cashmanagement.impl.CashManagementAccounting;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.GSTInvoiceTxnService;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.service.finance.ReceiptCancellationService;
import com.pennant.backend.service.limitservice.impl.LimitManagement;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.rits.cloning.Cloner;

public class ReceiptCancellationServiceImpl extends GenericFinanceDetailService implements
		ReceiptCancellationService {
	private static final Logger logger = Logger.getLogger(ReceiptCancellationServiceImpl.class);

	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private FinLogEntryDetailDAO finLogEntryDetailDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private FinanceMainDAO financeMainDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceDisbursementDAO financeDisbursementDAO;
	private RepayInstructionDAO repayInstructionDAO;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private RepaymentPostingsUtil repaymentPostingsUtil;
	private FinODDetailsDAO finODDetailsDAO;
	private PostingsDAO postingsDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private FinFeeScheduleDetailDAO finFeeScheduleDetailDAO;
	private FinInsurancesDAO finInsurancesDAO;
	private AuditHeaderDAO auditHeaderDAO;
	private BounceReasonDAO bounceReasonDAO;
	private RuleDAO ruleDAO;
	private RuleExecutionUtil ruleExecutionUtil;
	private LimitManagement	limitManagement;
	private CustomerDAO		customerDAO;
	private FinFeeReceiptDAO		finFeeReceiptDAO;
	private LatePayMarkingService latePayMarkingService; 
	private FinStageAccountingLogDAO	finStageAccountingLogDAO;
	
	//GST Invoice Report
	private FinanceDetailService 		financeDetailService;
	private GSTInvoiceTxnService		gstInvoiceTxnService;
	private CashManagementAccounting	cashManagementAccounting;
	private DepositChequesDAO			depositChequesDAO;
	private DepositDetailsDAO			depositDetailsDAO;

	public ReceiptCancellationServiceImpl() {
		super();
	}

	/**
	 * Method for Fetching Receipt Details , record is waiting for Realization
	 * 
	 * @param finReference
	 * @return
	 */
	@Override
	public FinReceiptHeader getFinReceiptHeaderById(long receiptID, boolean isFeePayment) {
		logger.debug("Entering");

		// Receipt Header Details
		FinReceiptHeader receiptHeader = null;
		
		String tableType = "_View";
		if(isFeePayment){
			tableType = "_FCView";
		}
		receiptHeader = getFinReceiptHeaderDAO().getReceiptHeaderByID(receiptID, tableType);

		// Fetch Receipt Detail List
		if (receiptHeader != null) {
			List<FinReceiptDetail> receiptDetailList = getFinReceiptDetailDAO().getReceiptHeaderByID(receiptID,
					"_AView");
			receiptHeader.setReceiptDetails(receiptDetailList);

			// Fetch Repay Headers List
			List<FinRepayHeader> rpyHeaderList = getFinanceRepaymentsDAO().getFinRepayHeadersByRef(
					receiptHeader.getReference(), "");

			// Fetch List of Repay Schedules
			if(!isFeePayment){
				List<RepayScheduleDetail> rpySchList = getFinanceRepaymentsDAO().getRpySchdList(
						receiptHeader.getReference(), "");
				if(rpySchList != null && !rpySchList.isEmpty()){
					for (FinRepayHeader finRepayHeader : rpyHeaderList) {
						for (RepayScheduleDetail repaySchd : rpySchList) {
							if (finRepayHeader.getRepayID() == repaySchd.getRepayID()) {
								finRepayHeader.getRepayScheduleDetails().add(repaySchd);
							}
						}
					}
				}
			}

			// Repay Headers setting to Receipt Details
			for (FinReceiptDetail receiptDetail : receiptDetailList) {
				for (FinRepayHeader finRepayHeader : rpyHeaderList) {
					if (finRepayHeader.getReceiptSeqID() == receiptDetail.getReceiptSeqID()) {
						receiptDetail.getRepayHeaders().add(finRepayHeader);
					}
				}
			}
			receiptHeader.setReceiptDetails(receiptDetailList);

			// Bounce reason Code
			if (StringUtils.isNotEmpty(receiptHeader.getRecordType()) && 
					StringUtils.equals(receiptHeader.getReceiptModeStatus(), RepayConstants.MODULETYPE_BOUNCE)) {
				receiptHeader.setManualAdvise(getManualAdviseDAO().getManualAdviseByReceiptId(receiptID, "_TView"));
			}
		}

		logger.debug("Leaving");
		return receiptHeader;
	}

	/**
	 * Method for fetching List of Postings details which are executed for the Transaction
	 */
	@Override
	public List<ReturnDataSet> getPostingsByTranIdList(List<Long> tranIdList) {
		return getPostingsDAO().getPostingsByTransIdList(tranIdList);
	}
	@Override
	public List<ReturnDataSet> getPostingsByPostRef(long postRef) {
		return getPostingsDAO().getPostingsByPostRef(postRef);
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * FinReceiptHeader/FinReceiptHeader_Temp by using FinReceiptHeaderDAO's save method b) Update the Record in the
	 * table. based on the module workFlow Configuration. by using FinReceiptHeaderDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtFinReceiptHeader by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws AccountNotFoundException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader) {
		logger.debug("Entering");

		aAuditHeader = businessValidation(aAuditHeader, "saveOrUpdate");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!aAuditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		FinReceiptHeader receiptHeader = (FinReceiptHeader) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (receiptHeader.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		// Receipt Header Details Save And Update
		// =======================================
		if (receiptHeader.isNew()) {
			getFinReceiptHeaderDAO().save(receiptHeader, tableType);

			// Bounce reason Code
			if (receiptHeader.getManualAdvise() != null) {
				getManualAdviseDAO().save(receiptHeader.getManualAdvise(), tableType);
			}

		} else {
			getFinReceiptHeaderDAO().update(receiptHeader, tableType);

			// Bounce reason Code
			if (receiptHeader.getManualAdvise() != null) {
				getManualAdviseDAO().update(receiptHeader.getManualAdvise(), tableType);
			}
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), receiptHeader.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				receiptHeader.getBefImage(), receiptHeader));

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getFinReceiptHeaderDAO().delete with parameters finReceiptHeader,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtFinReceiptHeader by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws InterfaceException
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) throws InterfaceException {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		FinReceiptHeader receiptHeader = (FinReceiptHeader) auditHeader.getAuditDetail().getModelData();

		// Bounce Reason Code
		if (receiptHeader.getManualAdvise() != null) {
			getManualAdviseDAO().delete(receiptHeader.getManualAdvise(), TableType.TEMP_TAB);
		}

		// Delete Receipt Header
		getFinReceiptHeaderDAO().deleteByReceiptID(receiptHeader.getReceiptID(), TableType.TEMP_TAB);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), receiptHeader.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				receiptHeader.getBefImage(), receiptHeader));
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doApprove method do the following steps. Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. based on the Record type do
	 * following actions Update record in the main table by using getFinReceiptHeaderDAO().update with parameters
	 * FinReceiptHeader. Audit the record in to AuditHeader and AdtFinReceiptHeader by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws Exception 
	 * @throws AccountNotFoundException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader) throws Exception{
		logger.debug("Entering");

		String tranType = "";
		aAuditHeader = businessValidation(aAuditHeader, "doApprove");
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		FinReceiptHeader receiptHeader = (FinReceiptHeader) auditHeader.getAuditDetail().getModelData();

		// Finance Repayment Cancellation Posting Process Execution
		// =====================================
		String errorCode = "";
		if(StringUtils.equals(receiptHeader.getReceiptPurpose(), FinanceConstants.FINSER_EVENT_FEEPAYMENT)){
			errorCode = procFeeReceiptCancellation(receiptHeader);
		}else{
			errorCode = procReceiptCancellation(receiptHeader, auditHeader.getAuditBranchCode());
		}
		if (StringUtils.isNotBlank(errorCode)) {
			throw new InterfaceException("9999", errorCode);
		}

		// Receipt Header Updation
		// =======================================
		tranType = PennantConstants.TRAN_UPD;
		receiptHeader.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		receiptHeader.setRecordType("");
		receiptHeader.setRoleCode("");
		receiptHeader.setNextRoleCode("");
		receiptHeader.setTaskId("");
		receiptHeader.setNextTaskId("");
		receiptHeader.setWorkflowId(0);
		getFinReceiptHeaderDAO().update(receiptHeader, TableType.MAIN_TAB);

		// Bounce Reason Code
		if (receiptHeader.getManualAdvise() != null) {
			getManualAdviseDAO().delete(receiptHeader.getManualAdvise(), TableType.TEMP_TAB);
		}

		// Delete Receipt Header
		getFinReceiptHeaderDAO().deleteByReceiptID(receiptHeader.getReceiptID(), TableType.TEMP_TAB);
		
		
		if (ImplementationConstants.LIMIT_INTERNAL) {
			BigDecimal priAmt = BigDecimal.ZERO;

			for (FinReceiptDetail finReceiptDetail : receiptHeader.getReceiptDetails()) {
				for (FinRepayHeader header : finReceiptDetail.getRepayHeaders()) {
					for (RepayScheduleDetail rpySchd : header.getRepayScheduleDetails()) {
						priAmt = priAmt.add(rpySchd.getPrincipalSchdPayNow().add(rpySchd.getPriSchdWaivedNow()));
					}
				}
			}
			if (priAmt.compareTo(BigDecimal.ZERO) > 0) {
				FinanceMain main = getFinanceMainDAO().getFinanceMainForBatch(receiptHeader.getReference());
				Customer customer = getCustomerDAO().getCustomerByID(main.getCustID());
				getLimitManagement().processLoanRepayCancel(main, customer, priAmt,
						StringUtils.trimToEmpty(main.getProductCategory()));
			}

		} 
		

		String[] fields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), receiptHeader.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1],
				receiptHeader.getBefImage(), receiptHeader));

		// Adding audit as deleted from TEMP table
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				receiptHeader.getBefImage(), receiptHeader));

		// Adding audit as Insert/Update/deleted into main table
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from getFinReceiptHeaderDAO().getErrorDetail with Error ID
	 * and language as parameters. 6) if any error/Warnings then assign the to auditHeader
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
		FinReceiptHeader receiptHeader = (FinReceiptHeader) auditDetail.getModelData();

		FinReceiptHeader tempReceiptHeader = null;
		if (receiptHeader.isWorkflow()) {
			tempReceiptHeader = getFinReceiptHeaderDAO().getReceiptHeaderByID(receiptHeader.getReceiptID(), "_Temp");
		}
		FinReceiptHeader beFinReceiptHeader = getFinReceiptHeaderDAO().getReceiptHeaderByID(
				receiptHeader.getReceiptID(), "");
		FinReceiptHeader oldReceiptHeader = receiptHeader.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(receiptHeader.getReceiptID());
		errParm[0] = PennantJavaUtil.getLabel("label_ReceiptID") + ":" + valueParm[0];

		if (receiptHeader.isNew()) { // for New record or new record into work flow

			if (!receiptHeader.isWorkflow()) {// With out Work flow only new
				// records
				if (beFinReceiptHeader != null) { // Record Already Exists in the
					// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
							"41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (receiptHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
					// records type is new
					if (beFinReceiptHeader != null || tempReceiptHeader != null) { // if
						// records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (beFinReceiptHeader == null || tempReceiptHeader != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!receiptHeader.isWorkflow()) { // With out Work flow for update
				// and delete

				if (beFinReceiptHeader == null) { // if records not exists in the
					// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
							"41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldReceiptHeader != null
							&& !oldReceiptHeader.getLastMntOn().equals(beFinReceiptHeader.getLastMntOn())) {
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

				if (tempReceiptHeader == null) { // if records not exists in the
					// Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
							"41005", errParm, valueParm), usrLanguage));
				}

				if (tempReceiptHeader != null && oldReceiptHeader != null
						&& !oldReceiptHeader.getLastMntOn().equals(tempReceiptHeader.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
							"41005", errParm, valueParm), usrLanguage));
				}
			}
		}
		
		// Fee Payment Cancellation or Bounce cancellation stopped When Loan is not in Workflow Process
		if(StringUtils.equals(receiptHeader.getReceiptPurpose(), FinanceConstants.FINSER_EVENT_FEEPAYMENT)){
			String finReference = receiptHeader.getReference();
			boolean rcdAvailable = getFinanceMainDAO().isFinReferenceExists(finReference, "_Temp", false);
			if (!rcdAvailable) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("60209", null),usrLanguage));
			}
			boolean rcdAssigned = getFinFeeReceiptDAO().isFinFeeReceiptAllocated(receiptHeader.getReceiptID(), "_View");
			if (rcdAssigned) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("60210", null),usrLanguage));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !receiptHeader.isWorkflow()) {
			receiptHeader.setBefImage(beFinReceiptHeader);
		}

		return auditDetail;
	}

	/**
	 * Method for Canceling the Presentment
	 * 
	 * @param receiptId
	 * @param returnCode
	 * @return errorMsg
	 * @throws Exception 
	 */
	@Override
	public PresentmentDetail presentmentCancellation(PresentmentDetail presentmentDetail, String returnCode) throws Exception {
		logger.debug(Literal.ENTERING);

		FinReceiptHeader receiptHeader = getFinReceiptHeaderById(presentmentDetail.getReceiptID(), false);
		
		if(receiptHeader == null){
			presentmentDetail.setErrorDesc(PennantJavaUtil.getLabel("label_FinReceiptHeader_Notavailable"));
			return presentmentDetail;
		}
		 
		BounceReason bounceReason = getBounceReasonDAO().getBounceReasonByReturnCode(returnCode, "");
		if (bounceReason == null) {
			presentmentDetail.setErrorDesc(PennantJavaUtil.getLabel("label_BounceReason_Notavailable") + returnCode);
			return presentmentDetail;
		}
		
		FinReceiptDetail finReceiptDetail = null;
		if (receiptHeader.getReceiptDetails() != null && !receiptHeader.getReceiptDetails().isEmpty()) {
			for (FinReceiptDetail item : receiptHeader.getReceiptDetails()) {
				if (item.getPaymentType().equals(RepayConstants.RECEIPTMODE_PRESENTMENT)) {
					finReceiptDetail = item;
					break;
				}
			}
		}
		
		if(finReceiptDetail == null){
			presentmentDetail.setErrorDesc(PennantJavaUtil.getLabel("label_FinReceiptDetails_Notavailable")  +  presentmentDetail.getMandateType());
			return presentmentDetail;
		}
		
		ManualAdvise manualAdvise = getManualAdvise(receiptHeader,  bounceReason, finReceiptDetail);
		
		if(manualAdvise == null){
			presentmentDetail.setErrorDesc(PennantJavaUtil.getLabel("label_ManualAdvise_Notavailable") +  presentmentDetail.getMandateType());
			return presentmentDetail;
		}
		receiptHeader.setManualAdvise(manualAdvise);
		receiptHeader.setReceiptModeStatus(RepayConstants.PAYSTATUS_BOUNCE);
		receiptHeader.setBounceDate(DateUtility.getAppDate());
		
		// Receipts Cancellation Process
		String errorMsg = procReceiptCancellation(receiptHeader, PennantConstants.APP_PHASE_EOD);
		
		if (StringUtils.trimToNull(errorMsg) == null) {

			// Update ReceiptHeader 
			getFinReceiptHeaderDAO().update(receiptHeader, TableType.MAIN_TAB);

			// Limits update against PresentmentCancellation 
			if (ImplementationConstants.LIMIT_INTERNAL) {

				BigDecimal priAmt = BigDecimal.ZERO;
				for (FinReceiptDetail receiptDetail : receiptHeader.getReceiptDetails()) {
					for (FinRepayHeader header : receiptDetail.getRepayHeaders()) {
						for (RepayScheduleDetail rpySchd : header.getRepayScheduleDetails()) {
							priAmt = priAmt.add(rpySchd.getPrincipalSchdPayNow().add(rpySchd.getPriSchdWaivedNow()));
						}
					}
				}
				if (priAmt.compareTo(BigDecimal.ZERO) > 0) {

					FinanceMain main = getFinanceMainDAO().getFinanceMainForBatch(receiptHeader.getReference());
					Customer customer = getCustomerDAO().getCustomerByID(main.getCustID());

					// Update Limit Exposures
					getLimitManagement().processLoanRepayCancel(main, customer, priAmt, StringUtils.trimToEmpty(main.getProductCategory()));
				}
			}
		}
		 
		presentmentDetail.setErrorDesc(errorMsg);
		manualAdvise = receiptHeader.getManualAdvise();
		presentmentDetail.setBounceID(manualAdvise.getBounceID());
		presentmentDetail.setBounceReason(bounceReason.getReason());
		presentmentDetail.setManualAdviseId(manualAdvise.getAdviseID());

		logger.debug(Literal.LEAVING);
		return presentmentDetail;
	}

	/**
	 * Method for preparing the ManualAdvise bean object
	 * 
	 * @param receiptHeader
	 * @param returnCode
	 * @param bounceReason
	 * @return ManualAdvise
	 */
	private ManualAdvise getManualAdvise(FinReceiptHeader receiptHeader, BounceReason bounceReason, FinReceiptDetail finReceiptDetail) {
		logger.debug(Literal.ENTERING);

		Rule rule = getRuleDAO().getRuleByID(bounceReason.getRuleID(), "");
		BigDecimal bounceAmt = BigDecimal.ZERO;
		
		HashMap<String, Object> fieldsAndValues = finReceiptDetail.getDeclaredFieldValues();
		bounceReason.getDeclaredFieldValues(fieldsAndValues);
		
		if (rule != null) {
			fieldsAndValues.put("br_finType", receiptHeader.getFinType());
			bounceAmt = (BigDecimal) getRuleExecutionUtil().executeRule(rule.getSQLRule(), fieldsAndValues, receiptHeader.getFinCcy(), RuleReturnType.DECIMAL);
		}
		
		ManualAdvise manualAdvise = new ManualAdvise();
		manualAdvise.setAdviseType(FinanceConstants.MANUAL_ADVISE_RECEIVABLE);
		manualAdvise.setFinReference(receiptHeader.getReference());
		manualAdvise.setFeeTypeID(0);
		manualAdvise.setSequence(0);
		manualAdvise.setAdviseAmount(PennantApplicationUtil.unFormateAmount(bounceAmt, CurrencyUtil.getFormat(receiptHeader.getFinCcy())));
		manualAdvise.setPaidAmount(BigDecimal.ZERO);
		manualAdvise.setWaivedAmount(BigDecimal.ZERO);
		manualAdvise.setRemarks("");
		manualAdvise.setReceiptID(receiptHeader.getReceiptID());
		manualAdvise.setBounceID(bounceReason.getBounceID());
		manualAdvise.setValueDate(DateUtility.getAppDate());
		manualAdvise.setPostDate(DateUtility.getAppDate());
		logger.debug(Literal.LEAVING);

		return manualAdvise;
	}

	/**
	 * Method for Processing Repayments Cancellation Based on Log Entry Details
	 * 
	 * @param receiptHeader
	 * @return String(Error Description)
	 * @throws Exception 
	 * @throws InterfaceException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private String procReceiptCancellation(FinReceiptHeader receiptHeader, String postBranch) throws Exception {
		logger.debug("Entering");
		
		boolean alwSchdReversalByLog = false;
		long postingId=getPostingsDAO().getPostingId();

		// Valid Check for Finance Reversal On Active Finance Or not with ValueDate CheckUp
		String finReference = receiptHeader.getReference();
		FinanceMain financeMain = getFinanceMainDAO().getFinanceMainForBatch(finReference);
		if (!financeMain.isFinIsActive()) {
			ErrorDetail errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("60204", "", null),
					PennantConstants.default_Language);
			// Not Allowed for Inactive Finances
			return errorDetail.getMessage();
		}

		boolean isRcdFound = false;
		boolean isBounceProcess = false;
		if (StringUtils.equals(receiptHeader.getReceiptModeStatus(), RepayConstants.PAYSTATUS_BOUNCE)) {
			isBounceProcess = true;
		}

		List<RepayScheduleDetail> rpySchdList = new ArrayList<>();
		long logKey = 0;
		BigDecimal totalPriAmount = BigDecimal.ZERO;
		List<FinReceiptDetail> receiptDetails = sortReceiptDetails(receiptHeader.getReceiptDetails());
		// Posting Reversal Case Program Calling in Equation
		// ============================================
		//getPostingsPreparationUtil().postReversalsByLinkedTranID(linkedTranId);
		getPostingsPreparationUtil().postReversalsByPostRef(receiptHeader.getReceiptID(),postingId);
		
		if(receiptHeader.getReceiptDetails() != null && !receiptHeader.getReceiptDetails().isEmpty()){
			for (FinReceiptDetail detail : receiptHeader.getReceiptDetails()) {
				if(StringUtils.equals(detail.getPaymentType(), receiptHeader.getReceiptMode()) &&
						!StringUtils.equals(receiptHeader.getReceiptMode(), RepayConstants.RECEIPTMODE_EXCESS)){
					String receiptNumber = detail.getPaymentRef();
					if(StringUtils.isNotBlank(receiptNumber)){
						List<Long> tranIdList = getFinStageAccountingLogDAO().getTranIdListByReceipt(receiptNumber);
						if(tranIdList != null && !tranIdList.isEmpty()){
							for (Long linkedTranID : tranIdList) {
								getPostingsPreparationUtil().postReversalsByLinkedTranID(linkedTranID);
							}
							getFinStageAccountingLogDAO().deleteByReceiptNo(receiptNumber);
						}
					}
				}
			}
		}
		
		BigDecimal unRealizeAmz = BigDecimal.ZERO;
		if (receiptDetails != null && !receiptDetails.isEmpty()) {
			for (int i = receiptDetails.size() - 1; i >= 0; i--) {

				FinReceiptDetail receiptDetail = receiptDetails.get(i);

				if (isBounceProcess
						&& (StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_EXCESS)
								|| StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_EMIINADV) 
								|| StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_PAYABLE))) {
					continue;
				}

				// Fetch Log Entry Details Greater than this Repayments Entry , which are having Schedule Recalculation
				// If Any Exist Case after this Repayments with Schedule Recalculation then Stop Process
				// ============================================
				if(alwSchdReversalByLog){
					List<FinLogEntryDetail> list = getFinLogEntryDetailDAO().getFinLogEntryDetailList(finReference,
							receiptDetail.getLogKey());
					if (list != null && !list.isEmpty()) {
						ErrorDetail errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("60206", "", null),
								PennantConstants.default_Language);
						return errorDetail.getMessage();
					}
				}

				// Finance Repayments Amount Updation if Principal Amount Exists
				long linkedTranId = 0;
				List<FinRepayHeader> rpyHeaders = receiptDetail.getRepayHeaders();
				for (int j = rpyHeaders.size() - 1; j >= 0; j--) {

					FinRepayHeader rpyHeader = rpyHeaders.get(j);
					linkedTranId = rpyHeader.getLinkedTranId();
					
					if (!StringUtils.equals(rpyHeader.getFinEvent(), FinanceConstants.FINSER_EVENT_SCHDRPY)
							&& !StringUtils.equals(rpyHeader.getFinEvent(), FinanceConstants.FINSER_EVENT_EARLYRPY)
							&& !StringUtils.equals(rpyHeader.getFinEvent(), FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
						
						// Accounting Reversals
						

						// Fetch Excess Amount Details
						FinExcessAmount excess = getFinExcessAmountDAO().getExcessAmountsByRefAndType(finReference,
								receiptHeader.getExcessAdjustTo());

						// Update Reserve Amount in FinExcessAmount
						if (excess == null || excess.getBalanceAmt().compareTo(rpyHeader.getRepayAmount()) < 0) {
							ErrorDetail errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("60205", "", null),
									PennantConstants.default_Language);
							return errorDetail.getMessage();
						}
						
						// Excess Amounts reversal Updations
						getFinExcessAmountDAO().updateExcessBal(excess.getExcessID(),
								rpyHeader.getRepayAmount().negate());

						isRcdFound = true;
						continue;
					}

					if (rpyHeader.getPriAmount().compareTo(BigDecimal.ZERO) > 0) {
						totalPriAmount = totalPriAmount.add(rpyHeader.getPriAmount());
					}

					isRcdFound = true;

					// Remove Repayments Terms based on Linked Transaction ID
					// ============================================
					getFinanceRepaymentsDAO().deleteRpyDetailbyLinkedTranId(linkedTranId, finReference);

					// Remove FinRepay Header Details
					// getFinanceRepaymentsDAO().deleteFinRepayHeaderByTranId(finReference, linkedTranId, "");

					// Remove Repayment Schedule Details
					// getFinanceRepaymentsDAO().deleteFinRepaySchListByTranId(finReference, linkedTranId, "");

					// Gathering All repayments Schedule List
					if (rpyHeader.getRepayScheduleDetails() != null && !rpyHeader.getRepayScheduleDetails().isEmpty()) {
						rpySchdList.addAll(rpyHeader.getRepayScheduleDetails());
					}
					
					// Update Profit Details for UnRealized Income
					unRealizeAmz = unRealizeAmz.add(rpyHeader.getRealizeUnAmz());

				}

				// Update Log Entry Based on FinPostDate and Reference
				// ============================================
				if(receiptDetail.getLogKey() != 0 && receiptDetail.getLogKey() != Long.MIN_VALUE){
					FinLogEntryDetail detail = getFinLogEntryDetailDAO().getFinLogEntryDetail(receiptDetail.getLogKey());
					if (detail == null) {
						ErrorDetail errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("60207", "", null),
								PennantConstants.default_Language);
						return errorDetail.getMessage();
					}
					logKey = detail.getLogKey();
					detail.setReversalCompleted(true);
					getFinLogEntryDetailDAO().updateLogEntryStatus(detail);
				}

				// Manual Advise Movements Reversal
				List<ManualAdviseMovements> advMovements = getManualAdviseDAO().getAdvMovementsByReceiptSeq(
						receiptDetail.getReceiptID(), receiptDetail.getReceiptSeqID(), "_AView");
				List<ManualAdviseMovements> advMovementsTemp = new ArrayList<ManualAdviseMovements> ();
				if (advMovements != null && !advMovements.isEmpty()) {
					isRcdFound = true;
					for (ManualAdviseMovements movement : advMovements) {
						
						if((movement.getPaidAmount().add(movement.getWaivedAmount())).compareTo(BigDecimal.ZERO) == 0){
							continue;
						}
						
						ManualAdvise advise = new ManualAdvise();
						advise.setAdviseID(movement.getAdviseID());
						advise.setPaidAmount(movement.getPaidAmount().negate());
						advise.setWaivedAmount(movement.getWaivedAmount().negate());
						advise.setPaidCGST(movement.getPaidCGST().negate());
						advise.setPaidSGST(movement.getPaidSGST().negate());
						advise.setPaidIGST(movement.getPaidIGST().negate());
						advise.setPaidUGST(movement.getPaidUGST().negate());
						advMovementsTemp.add(movement);
						
						getManualAdviseDAO().updateAdvPayment(advise, TableType.MAIN_TAB);
					}

					// Update Movement Status
					getManualAdviseDAO().updateMovementStatus(receiptDetail.getReceiptID(),
							receiptDetail.getReceiptSeqID(), receiptHeader.getReceiptModeStatus(), "");
					
					// GST Invoice Preparation
					long postingSeqId = 0;	//TODO should be pass linkedTranId
					FinanceDetail  financeDetail = financeDetailService.getFinSchdDetailById(finReference, "", false);
					this.gstInvoiceTxnService.gstInvoicePreparation(postingSeqId, financeDetail, null, advMovementsTemp, PennantConstants.GST_INVOICE_TRANSACTION_TYPE_CREDIT, finReference);
				}
				
			}

			if(!rpySchdList.isEmpty()){
				
				// Making Single Set of Repay Schedule Details and sent to Rendering
				Map<Date, RepayScheduleDetail> rpySchdMap = new HashMap<>();
				for (RepayScheduleDetail rpySchd : rpySchdList) {

					RepayScheduleDetail curRpySchd = null;
					if (rpySchdMap.containsKey(rpySchd.getSchDate())) {
						curRpySchd = rpySchdMap.get(rpySchd.getSchDate());
						curRpySchd.setPrincipalSchdPayNow(curRpySchd.getPrincipalSchdPayNow().add(
								rpySchd.getPrincipalSchdPayNow()));
						curRpySchd.setProfitSchdPayNow(curRpySchd.getProfitSchdPayNow().add(rpySchd.getProfitSchdPayNow()));
						curRpySchd.setTdsSchdPayNow(curRpySchd.getTdsSchdPayNow().add(rpySchd.getTdsSchdPayNow()));
						curRpySchd.setLatePftSchdPayNow(curRpySchd.getLatePftSchdPayNow().add(
								rpySchd.getLatePftSchdPayNow()));
						curRpySchd.setSchdFeePayNow(curRpySchd.getSchdFeePayNow().add(rpySchd.getSchdFeePayNow()));
						curRpySchd.setSchdInsPayNow(curRpySchd.getSchdInsPayNow().add(rpySchd.getSchdInsPayNow()));
						curRpySchd.setPenaltyPayNow(curRpySchd.getPenaltyPayNow().add(rpySchd.getPenaltyPayNow()));
						rpySchdMap.remove(rpySchd.getSchDate());
					} else {
						curRpySchd = rpySchd;
					}

					// Adding New Repay Schedule Object to Map after Summing data
					rpySchdMap.put(rpySchd.getSchDate(), curRpySchd);
				}

				rpySchdList = sortRpySchdDetails(new ArrayList<>(rpySchdMap.values()));
				List<FinanceScheduleDetail> updateSchdList = new ArrayList<>();
				List<FinFeeScheduleDetail> updateFeeList = new ArrayList<>();
				List<FinSchFrqInsurance> updateInsList = new ArrayList<>();
				Map<String, FinanceScheduleDetail> schdMap = null;
				
				FinScheduleData scheduleData = null;
				if(!alwSchdReversalByLog){
					
					schdMap = new HashMap<>();
					scheduleData = new FinScheduleData();
					scheduleData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, "", false));
					
					for (FinanceScheduleDetail schd : scheduleData.getFinanceScheduleDetails()) {
						schdMap.put(DateUtility.formatDate(schd.getSchDate(), PennantConstants.DBDateFormat), schd);
					}
				}

				for (RepayScheduleDetail rpySchd : rpySchdList) {

					// Schedule Detail Reversals
					if(!alwSchdReversalByLog){

						FinanceScheduleDetail curSchd = null;
						boolean schdUpdated = false;
						if(schdMap.containsKey(DateUtility.formatDate(rpySchd.getSchDate(), PennantConstants.DBDateFormat))){
							curSchd = schdMap.get(DateUtility.formatDate(rpySchd.getSchDate(), PennantConstants.DBDateFormat));

							// Principal Payment 
							if(rpySchd.getPrincipalSchdPayNow().compareTo(BigDecimal.ZERO) > 0){
								curSchd.setSchdPriPaid(curSchd.getSchdPriPaid().subtract(rpySchd.getPrincipalSchdPayNow()));
								curSchd.setSchPriPaid(false);
								schdUpdated = true;
							}
							
							// Profit Payment 
							if(rpySchd.getProfitSchdPayNow().compareTo(BigDecimal.ZERO) > 0){
								curSchd.setSchdPftPaid(curSchd.getSchdPftPaid().subtract(rpySchd.getProfitSchdPayNow()));
								curSchd.setSchPftPaid(false);
								schdUpdated = true;
							}
							
							// TDS Payment 
							if(rpySchd.getTdsSchdPayNow().compareTo(BigDecimal.ZERO) > 0){
								curSchd.setTDSPaid(curSchd.getTDSPaid().subtract(rpySchd.getTdsSchdPayNow()));
								schdUpdated = true;
							}
							
							// Fee Detail Payment 
							if(rpySchd.getSchdFeePayNow().compareTo(BigDecimal.ZERO) > 0){
								curSchd.setSchdFeePaid(curSchd.getSchdFeePaid().subtract(rpySchd.getSchdFeePayNow()));
								schdUpdated = true;
							}
							
							// Insurance Detail Payment 
							if(rpySchd.getSchdInsPayNow().compareTo(BigDecimal.ZERO) > 0){
								curSchd.setSchdInsPaid(curSchd.getSchdInsPaid().subtract(rpySchd.getSchdInsPayNow()));
								schdUpdated = true;
							}
							
							// Prepare List Schedules which will be updated
							if(schdUpdated){
								updateSchdList.add(curSchd);
							}
						}
					}

					// Overdue Recovery Details Reset Back to Original State , If any penalties Paid On this Repayments
					// Process
					// ============================================
					if (rpySchd.getPenaltyPayNow().compareTo(BigDecimal.ZERO) > 0
							|| rpySchd.getLatePftSchdPayNow().compareTo(BigDecimal.ZERO) > 0) {
						getFinODDetailsDAO().updateReversals(finReference, rpySchd.getSchDate(),
								rpySchd.getPenaltyPayNow(), rpySchd.getLatePftSchdPayNow());
					}

					// Update Fee Balance
					// ============================================
					if (rpySchd.getSchdFeePayNow().compareTo(BigDecimal.ZERO) > 0) {
						List<FinFeeScheduleDetail> feeList = getFinFeeScheduleDetailDAO().getFeeScheduleBySchDate(
								finReference, rpySchd.getSchDate());
						BigDecimal feebal = rpySchd.getSchdFeePayNow();
						for (int j = feeList.size() - 1; j >= 0; j--) {
							FinFeeScheduleDetail feeSchd = feeList.get(j);
							BigDecimal paidReverse = BigDecimal.ZERO;
							if (feebal.compareTo(BigDecimal.ZERO) == 0) {
								continue;
							}
							if (feeSchd.getPaidAmount().compareTo(BigDecimal.ZERO) == 0) {
								continue;
							}

							if (feebal.compareTo(feeSchd.getPaidAmount()) > 0) {
								paidReverse = feeSchd.getPaidAmount();
							} else {
								paidReverse = feebal;
							}
							feebal = feebal.subtract(paidReverse);

							// Create list of updated objects to save one time
							FinFeeScheduleDetail updFeeSchd = new FinFeeScheduleDetail();
							updFeeSchd.setFeeID(feeSchd.getFeeID());
							updFeeSchd.setSchDate(feeSchd.getSchDate());
							updFeeSchd.setPaidAmount(paidReverse.negate());
							updateFeeList.add(updFeeSchd);
						}
					}

					// Update Insurance Balance
					// ============================================
					if (rpySchd.getSchdInsPayNow().compareTo(BigDecimal.ZERO) > 0) {
						List<FinSchFrqInsurance> insList = getFinInsurancesDAO().getInsScheduleBySchDate(finReference,
								rpySchd.getSchDate());
						BigDecimal insBal = rpySchd.getSchdInsPayNow();
						for (int j = insList.size() - 1; j >= 0; j--) {
							FinSchFrqInsurance insSchd = insList.get(j);
							BigDecimal paidReverse = BigDecimal.ZERO;

							if (insBal.compareTo(BigDecimal.ZERO) == 0) {
								continue;
							}
							if (insSchd.getInsurancePaid().compareTo(BigDecimal.ZERO) == 0) {
								continue;
							}

							if (insBal.compareTo(insSchd.getInsurancePaid()) > 0) {
								paidReverse = insSchd.getInsurancePaid();
							} else {
								paidReverse = insBal;
							}
							insBal = insBal.subtract(paidReverse);

							// Create list of updated objects to save one time
							FinSchFrqInsurance updInsSchd = new FinSchFrqInsurance();
							updInsSchd.setInsId(insSchd.getInsId());
							updInsSchd.setInsSchDate(insSchd.getInsSchDate());
							updInsSchd.setInsurancePaid(paidReverse.negate());
							updateInsList.add(updInsSchd);
						}
					}
				}

				// Schedule Details Updation
				if (!updateSchdList.isEmpty()) {
					getFinanceScheduleDetailDAO().updateListForRpy(updateSchdList);
				}
				
				// Fee Schedule Details Updation
				if (!updateFeeList.isEmpty()) {
					getFinFeeScheduleDetailDAO().updateFeeSchdPaids(updateFeeList);
				}

				// Insurance Schedule Details Updation
				if (!updateInsList.isEmpty()) {
					getFinInsurancesDAO().updateInsSchdPaids(updateInsList);
				}

				rpySchdList = null;
				rpySchdMap = null;
				updateSchdList = null;
				updateFeeList = null;
				updateInsList = null;

				// Deletion of Finance Schedule Related Details From Main Table
				FinanceProfitDetail pftDetail = getFinanceProfitDetailDAO().getFinProfitDetailsById(finReference);
				if(alwSchdReversalByLog){
					listDeletion(finReference, "", false, 0);

					// Fetching Last Log Entry Finance Details
					scheduleData = getFinSchDataByFinRef(finReference, logKey, "_Log");
					scheduleData.setFinanceMain(financeMain);

					// Re-Insert Log Entry Data before Repayments Process Recalculations
					listSave(scheduleData, "", 0);

					// Delete Data from Log Entry Tables After Inserting into Main Tables
					for (int i = 0; i < receiptDetails.size(); i++) {
						listDeletion(finReference, "_Log", false, receiptDetails.get(i).getLogKey());
					}
				}else{
					scheduleData.setFinanceScheduleDetails(new ArrayList<>(schdMap.values()));
				}
				
				// Update Profit Details for UnRealized Income & Capitalization Difference
				pftDetail.setAmzTillLBD(pftDetail.getAmzTillLBD().subtract(unRealizeAmz));

				// Check Current Finance Max Status For updation
				// ============================================
				
				Date valueDate = DateUtility.getAppDate();
				if(!ImplementationConstants.LPP_CALC_SOD){
					valueDate = DateUtility.addDays(valueDate, -1);
				}
				List<FinODDetails> overdueList = getFinODDetailsDAO().getFinODBalByFinRef(financeMain.getFinReference());
				List<FinanceRepayments> repayments = getFinanceRepaymentsDAO().getFinRepayListByFinRef(financeMain.getFinReference(), false, "");
				overdueList = getLatePayMarkingService().calPDOnBackDatePayment(financeMain, overdueList, valueDate, 
						scheduleData.getFinanceScheduleDetails(), repayments, true,true);
				
				// Status Updation
				getRepaymentPostingsUtil().updateStatus(financeMain, valueDate,	scheduleData.getFinanceScheduleDetails(), pftDetail,overdueList, null);
				
				// Overdue Details Updation after Recalculation with Current Data
				if (overdueList != null && !overdueList.isEmpty()) {
					getFinODDetailsDAO().updateList(overdueList);
				}
				
				if (totalPriAmount.compareTo(BigDecimal.ZERO) > 0) {

					// Finance Main Details Update
					financeMain.setFinRepaymentAmount(financeMain.getFinRepaymentAmount().subtract(totalPriAmount));
					getFinanceMainDAO().updateRepaymentAmount(finReference,
							financeMain.getFinCurrAssetValue().add(financeMain.getFeeChargeAmt())
							.add(financeMain.getInsuranceAmt()), financeMain.getFinRepaymentAmount(),
							financeMain.getFinStatus(), FinanceConstants.FINSTSRSN_MANUAL, true, false);
				}
			}
		}

		if (!isRcdFound) {
			ErrorDetail errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("60208", "", null),
					PennantConstants.default_Language);
			return errorDetail.getMessage();
		} else {

			if (receiptHeader.getManualAdvise() != null) {

				// Bounce Charge Due Postings
				if (receiptHeader.getManualAdvise().getAdviseAmount().compareTo(BigDecimal.ZERO) > 0) {

					FinanceDetail financeDetail = new FinanceDetail();
					financeDetail.getFinScheduleData().setFinanceMain(financeMain);

					AEEvent aeEvent = executeDueAccounting(financeDetail, receiptHeader.getBounceDate(), 
							receiptHeader.getManualAdvise().getAdviseAmount(), postBranch, RepayConstants.ALLOCATION_BOUNCE);
					if (aeEvent != null && StringUtils.isNotEmpty(aeEvent.getErrorMessage())) {
						logger.debug("Leaving");
						return aeEvent.getErrorMessage();
					}
					
					// GST Invoice Preparation
					if (aeEvent != null) {
						long postingSeqId = aeEvent.getLinkedTranId();
						ManualAdviseMovements adviseMovements = new ManualAdviseMovements();
						ManualAdvise advise = receiptHeader.getManualAdvise();
						adviseMovements.setFeeTypeCode(advise.getFeeTypeCode());
						adviseMovements.setFeeTypeDesc(advise.getFeeTypeDesc());
						adviseMovements.setMovementAmount(advise.getAdviseAmount());
						adviseMovements.setPaidCGST(advise.getPaidCGST());
						adviseMovements.setPaidSGST(advise.getPaidSGST());
						adviseMovements.setPaidIGST(advise.getPaidIGST());
						adviseMovements.setPaidUGST(advise.getPaidUGST());
						List<ManualAdviseMovements> advMovementsTemp = new ArrayList<ManualAdviseMovements>();
						advMovementsTemp.add(adviseMovements);
						FinanceDetail  financeDetailTemp = financeDetailService.getFinSchdDetailById(finReference, "", false);
						this.gstInvoiceTxnService.gstInvoicePreparation(postingSeqId, financeDetailTemp, null, advMovementsTemp, PennantConstants.GST_INVOICE_TRANSACTION_TYPE_CREDIT, finReference);
					}
				}

				String adviseId = getManualAdviseDAO().save(receiptHeader.getManualAdvise(), TableType.MAIN_TAB);
				receiptHeader.getManualAdvise().setAdviseID(Long.parseLong(adviseId));
			}

			// Update Receipt Details based on Receipt Mode
			for (int i = 0; i < receiptHeader.getReceiptDetails().size(); i++) {
				FinReceiptDetail receiptDetail = receiptHeader.getReceiptDetails().get(i);
				if (!isBounceProcess
						|| (StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_CHEQUE) || StringUtils
								.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_DD))) {
					getFinReceiptDetailDAO().updateReceiptStatus(receiptDetail.getReceiptID(),
							receiptDetail.getReceiptSeqID(), receiptHeader.getReceiptModeStatus());
					
					// Receipt Reversal for Excess or Payable
					if (!isBounceProcess) {
						
						if(StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_EXCESS)
								|| StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_EMIINADV)){
							
							// Excess utilize Reversals
							getFinExcessAmountDAO().updateExcessAmount(receiptDetail.getPayAgainstID(), "U", receiptDetail.getAmount().negate());
							
						}else if(StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_PAYABLE)){
							
							// Payable Utilize reversals
							getManualAdviseDAO().reverseUtilise(receiptDetail.getPayAgainstID(), receiptDetail.getAmount());
						}
					}
				}
			}
			
			// Accounting Execution Process for Deposit Reversal
			if(StringUtils.equals(RepayConstants.RECEIPTMODE_CHEQUE,receiptHeader.getReceiptMode()) ||
					StringUtils.equals(RepayConstants.RECEIPTMODE_DD,receiptHeader.getReceiptMode())){
				
				// Verify Cheque or DD Details exists in Deposited Cheques 
				DepositCheques depositCheque = getDepositChequesDAO().getDepositChequeByReceiptID(receiptHeader.getReceiptID());
				
				if(depositCheque != null){
					DepositMovements movement = getDepositDetailsDAO().getDepositMovementsById(depositCheque.getMovementId(), "_AView");
					if(movement != null){
						AEEvent aeEvent = this.cashManagementAccounting.generateAccounting(AccountEventConstants.ACCEVENT_CHEQUETOBANK_REVERSAL,
								movement.getBranchCode(), movement.getBranchCode(), depositCheque.getAmount(),
								movement.getPartnerBankId(), movement.getMovementId(), null);
						
						// Make Deposit Cheque to Reversal Status
						if(aeEvent.isPostingSucess()){
							getDepositChequesDAO().reverseChequeStatus(movement.getMovementId(), 
									receiptHeader.getReceiptID(), aeEvent.getLinkedTranId());
						}
					}
				}
			}
		}

		return null;
	}
	
	/**
	 * Method for Processing Fee Payments Cancellation Based on Event and Reference
	 * 
	 * @param receiptHeader
	 * @throws InterfaceException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private String procFeeReceiptCancellation(FinReceiptHeader receiptHeader) {
		logger.debug("Entering");
		
		long linkedTranId = 0;
		for (FinReceiptDetail receiptDetail : receiptHeader.getReceiptDetails()) {
			for (FinRepayHeader rpyHeader : receiptDetail.getRepayHeaders()) {
				linkedTranId = rpyHeader.getLinkedTranId();
				break;
			}
		}

		// Posting Reversal Case Program Calling in Equation
		// ============================================
		getPostingsPreparationUtil().postReversalsByLinkedTranID(linkedTranId);

		// Update Receipt Detail Status
		for (FinReceiptDetail receiptDetail : receiptHeader.getReceiptDetails()) {
			getFinReceiptDetailDAO().updateReceiptStatus(receiptDetail.getReceiptID(),
					receiptDetail.getReceiptSeqID(), receiptHeader.getReceiptModeStatus());
		}
		
		return null;
	}

	/**
	 * Sorting Repay Schedule Details
	 * 
	 * @param rpySchdList
	 * @return
	 */
	public List<RepayScheduleDetail> sortRpySchdDetails(List<RepayScheduleDetail> rpySchdList) {

		if (rpySchdList != null && rpySchdList.size() > 1) {
			Collections.sort(rpySchdList, new Comparator<RepayScheduleDetail>() {
				@Override
				public int compare(RepayScheduleDetail rpySchd1, RepayScheduleDetail rpySchd2) {
					return DateUtility.compare(rpySchd1.getSchDate(), rpySchd2.getSchDate());
				}
			});
		}

		return rpySchdList;
	}

	/**
	 * Method for Sorting Receipt Details From Receipts
	 * 
	 * @param receipts
	 * @return
	 */
	private List<FinReceiptDetail> sortReceiptDetails(List<FinReceiptDetail> receipts) {

		if (receipts != null && receipts.size() > 1) {
			Collections.sort(receipts, new Comparator<FinReceiptDetail>() {
				@Override
				public int compare(FinReceiptDetail detail1, FinReceiptDetail detail2) {
					if (detail1.getPayOrder() > detail2.getPayOrder()) {
						return 1;
					} else if (detail1.getPayOrder() < detail2.getPayOrder()) {
						return -1;
					}
					return 0;
				}
			});
		}
		return receipts;
	}

	/**
	 * Method to get Schedule related data.
	 * 
	 * @param finReference
	 *            (String)
	 * @param isWIF
	 *            (boolean)
	 * **/
	private FinScheduleData getFinSchDataByFinRef(String finReference, long logKey, String type) {
		logger.debug("Entering");
		FinScheduleData finSchData = new FinScheduleData();
		finSchData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type,
				false, logKey));
		finSchData.setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference, type,
				false, logKey));
		finSchData.setRepayInstructions(getRepayInstructionDAO()
				.getRepayInstructions(finReference, type, false, logKey));
		logger.debug("Leaving");
		return finSchData;
	}

	/**
	 * Method to delete schedule, disbursement, repayinstruction lists.
	 * 
	 * @param finDetail
	 * @param tableType
	 * @param isWIF
	 */
	private void listDeletion(String finReference, String tableType, boolean isWIF, long logKey) {
		logger.debug("Entering");
		getFinanceScheduleDetailDAO().deleteByFinReference(finReference, tableType, isWIF, logKey);
		getFinanceDisbursementDAO().deleteByFinReference(finReference, tableType, isWIF, logKey);
		getRepayInstructionDAO().deleteByFinReference(finReference, tableType, isWIF, logKey);
		logger.debug("Leaving");
	}

	private void listSave(FinScheduleData finDetail, String tableType, long logKey) {
		logger.debug("Entering ");
		HashMap<Date, Integer> mapDateSeq = new HashMap<Date, Integer>();

		// Finance Schedule Details
		for (int i = 0; i < finDetail.getFinanceScheduleDetails().size(); i++) {
			finDetail.getFinanceScheduleDetails().get(i).setLastMntBy(finDetail.getFinanceMain().getLastMntBy());
			finDetail.getFinanceScheduleDetails().get(i).setFinReference(finDetail.getFinanceMain().getFinReference());
			int seqNo = 0;

			if (mapDateSeq.containsKey(finDetail.getFinanceScheduleDetails().get(i).getSchDate())) {
				seqNo = mapDateSeq.get(finDetail.getFinanceScheduleDetails().get(i).getSchDate());
				mapDateSeq.remove(finDetail.getFinanceScheduleDetails().get(i).getSchDate());
			}
			seqNo = seqNo + 1;
			mapDateSeq.put(finDetail.getFinanceScheduleDetails().get(i).getSchDate(), seqNo);
			finDetail.getFinanceScheduleDetails().get(i).setSchSeq(seqNo);
			finDetail.getFinanceScheduleDetails().get(i).setLogKey(logKey);
		}
		getFinanceScheduleDetailDAO().saveList(finDetail.getFinanceScheduleDetails(), tableType, false);

		// Finance Disbursement Details
		mapDateSeq = new HashMap<Date, Integer>();
		Date curBDay = DateUtility.getAppDate();
		for (int i = 0; i < finDetail.getDisbursementDetails().size(); i++) {
			finDetail.getDisbursementDetails().get(i).setFinReference(finDetail.getFinanceMain().getFinReference());
			finDetail.getDisbursementDetails().get(i).setDisbReqDate(curBDay);
			finDetail.getDisbursementDetails().get(i).setDisbIsActive(true);
			finDetail.getDisbursementDetails().get(i).setDisbDisbursed(true);
			finDetail.getDisbursementDetails().get(i).setLogKey(logKey);
		}
		getFinanceDisbursementDAO().saveList(finDetail.getDisbursementDetails(), tableType, false);

		// Finance Repay Instruction Details
		for (int i = 0; i < finDetail.getRepayInstructions().size(); i++) {
			finDetail.getRepayInstructions().get(i).setFinReference(finDetail.getFinanceMain().getFinReference());
			finDetail.getRepayInstructions().get(i).setLogKey(logKey);
		}
		getRepayInstructionDAO().saveList(finDetail.getRepayInstructions(), tableType, false);

		logger.debug("Leaving ");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public FinanceRepaymentsDAO getFinanceRepaymentsDAO() {
		return financeRepaymentsDAO;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public FinLogEntryDetailDAO getFinLogEntryDetailDAO() {
		return finLogEntryDetailDAO;
	}

	public void setFinLogEntryDetailDAO(FinLogEntryDetailDAO finLogEntryDetailDAO) {
		this.finLogEntryDetailDAO = finLogEntryDetailDAO;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public FinanceDisbursementDAO getFinanceDisbursementDAO() {
		return financeDisbursementDAO;
	}

	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	public RepayInstructionDAO getRepayInstructionDAO() {
		return repayInstructionDAO;
	}

	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}

	public FinanceProfitDetailDAO getFinanceProfitDetailDAO() {
		return financeProfitDetailDAO;
	}

	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public RepaymentPostingsUtil getRepaymentPostingsUtil() {
		return repaymentPostingsUtil;
	}

	public void setRepaymentPostingsUtil(RepaymentPostingsUtil repaymentPostingsUtil) {
		this.repaymentPostingsUtil = repaymentPostingsUtil;
	}

	public FinODDetailsDAO getFinODDetailsDAO() {
		return finODDetailsDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public PostingsDAO getPostingsDAO() {
		return postingsDAO;
	}

	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public ManualAdviseDAO getManualAdviseDAO() {
		return manualAdviseDAO;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public FinExcessAmountDAO getFinExcessAmountDAO() {
		return finExcessAmountDAO;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public FinFeeScheduleDetailDAO getFinFeeScheduleDetailDAO() {
		return finFeeScheduleDetailDAO;
	}

	public void setFinFeeScheduleDetailDAO(FinFeeScheduleDetailDAO finFeeScheduleDetailDAO) {
		this.finFeeScheduleDetailDAO = finFeeScheduleDetailDAO;
	}

	public FinInsurancesDAO getFinInsurancesDAO() {
		return finInsurancesDAO;
	}

	public void setFinInsurancesDAO(FinInsurancesDAO finInsurancesDAO) {
		this.finInsurancesDAO = finInsurancesDAO;
	}

	public BounceReasonDAO getBounceReasonDAO() {
		return bounceReasonDAO;
	}

	public void setBounceReasonDAO(BounceReasonDAO bounceReasonDAO) {
		this.bounceReasonDAO = bounceReasonDAO;
	}

	public RuleDAO getRuleDAO() {
		return ruleDAO;
	}

	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	public RuleExecutionUtil getRuleExecutionUtil() {
		return ruleExecutionUtil;
	}

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}

	public LimitManagement getLimitManagement() {
		return limitManagement;
	}

	public void setLimitManagement(LimitManagement limitManagement) {
		this.limitManagement = limitManagement;
	}

	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public FinFeeReceiptDAO getFinFeeReceiptDAO() {
		return finFeeReceiptDAO;
	}

	public void setFinFeeReceiptDAO(FinFeeReceiptDAO finFeeReceiptDAO) {
		this.finFeeReceiptDAO = finFeeReceiptDAO;
	}

	public LatePayMarkingService getLatePayMarkingService() {
		return latePayMarkingService;
	}

	public void setLatePayMarkingService(LatePayMarkingService latePayMarkingService) {
		this.latePayMarkingService = latePayMarkingService;
	}

	public FinStageAccountingLogDAO getFinStageAccountingLogDAO() {
		return finStageAccountingLogDAO;
	}

	public void setFinStageAccountingLogDAO(FinStageAccountingLogDAO finStageAccountingLogDAO) {
		this.finStageAccountingLogDAO = finStageAccountingLogDAO;
	}

	// GST Development
	public GSTInvoiceTxnService getGstInvoiceTxnService() {
		return gstInvoiceTxnService;
	}

	public void setGstInvoiceTxnService(GSTInvoiceTxnService gstInvoiceTxnService) {
		this.gstInvoiceTxnService = gstInvoiceTxnService;
	}
	
	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public CashManagementAccounting getCashManagementAccounting() {
		return cashManagementAccounting;
	}

	public void setCashManagementAccounting(CashManagementAccounting cashManagementAccounting) {
		this.cashManagementAccounting = cashManagementAccounting;
	}

	public DepositChequesDAO getDepositChequesDAO() {
		return depositChequesDAO;
	}

	public void setDepositChequesDAO(DepositChequesDAO depositChequesDAO) {
		this.depositChequesDAO = depositChequesDAO;
	}

	public DepositDetailsDAO getDepositDetailsDAO() {
		return depositDetailsDAO;
	}

	public void setDepositDetailsDAO(DepositDetailsDAO depositDetailsDAO) {
		this.depositDetailsDAO = depositDetailsDAO;
	}
}
