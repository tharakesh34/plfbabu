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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.AccrualService;
import com.pennant.app.core.LatePayMarkingService;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.RepaymentPostingsUtil;
import com.pennant.app.util.RepaymentProcessUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.applicationmaster.BounceReasonDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.cashmanagement.BranchCashDetailDAO;
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
import com.pennant.backend.dao.receipts.ReceiptAllocationDetailDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rmtmasters.PromotionDAO;
import com.pennant.backend.dao.rulefactory.FinFeeScheduleDetailDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.applicationmaster.BounceReason;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.DepositCheques;
import com.pennant.backend.model.finance.DepositDetails;
import com.pennant.backend.model.finance.DepositMovements;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptData;
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
import com.pennant.backend.model.finance.ReceiptCancelDetail;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
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
import com.pennant.cache.util.AccountingConfigCache;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.rits.cloning.Cloner;

public class ReceiptCancellationServiceImpl extends GenericFinanceDetailService implements ReceiptCancellationService {
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
	private LimitManagement limitManagement;
	private CustomerDAO customerDAO;
	private FinFeeReceiptDAO finFeeReceiptDAO;
	private LatePayMarkingService latePayMarkingService;
	private BranchCashDetailDAO branchCashDetailDAO;
	private PromotionDAO promotionDAO;
	private FinanceProfitDetailDAO profitDetailDAO;
	private ReceiptCalculator receiptCalculator;
	private FinanceTypeDAO financeTypeDAO;
	private RepaymentProcessUtil repayProcessUtil;
	private AccrualService accrualService;
	private FinStageAccountingLogDAO finStageAccountingLogDAO;
	private ReceiptAllocationDetailDAO allocationDetailDAO;
	private CustomerDetailsService customerDetailsService;
	private FinanceDetailService financeDetailService;
	private GSTInvoiceTxnService gstInvoiceTxnService;
	private DepositChequesDAO depositChequesDAO;
	private DepositDetailsDAO depositDetailsDAO;

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
		if (isFeePayment) {
			tableType = "_FCView";
		}
		receiptHeader = finReceiptHeaderDAO.getReceiptHeaderByID(receiptID, tableType);

		// Fetch Receipt Detail List
		if (receiptHeader != null) {
			List<FinReceiptDetail> receiptDetailList = finReceiptDetailDAO.getReceiptHeaderByID(receiptID, "_AView");
			receiptHeader.setReceiptDetails(receiptDetailList);

			// Fetch Repay Headers List
			List<FinRepayHeader> rpyHeaderList = financeRepaymentsDAO
					.getFinRepayHeadersByRef(receiptHeader.getReference(), "");

			// Fetch List of Repay Schedules
			if (!isFeePayment) {
				List<RepayScheduleDetail> rpySchList = financeRepaymentsDAO.getRpySchdList(receiptHeader.getReference(),
						"");
				if (rpySchList != null && !rpySchList.isEmpty()) {
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
						receiptDetail.setRepayHeader(finRepayHeader);
					}
				}
			}
			receiptHeader.setReceiptDetails(receiptDetailList);

			// Bounce reason Code
			if (StringUtils.isNotEmpty(receiptHeader.getRecordType())
					&& StringUtils.equals(receiptHeader.getReceiptModeStatus(), RepayConstants.MODULETYPE_BOUNCE)) {
				receiptHeader.setManualAdvise(manualAdviseDAO.getManualAdviseByReceiptId(receiptID, "_TView"));
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
		return postingsDAO.getPostingsByTransIdList(tranIdList);
	}

	@Override
	public List<ReturnDataSet> getPostingsByPostRef(long postRef) {
		return postingsDAO.getPostingsByPostRef(postRef);
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
		FinReceiptData receiptData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		FinReceiptHeader receiptHeader = receiptData.getReceiptHeader();

		TableType tableType = TableType.MAIN_TAB;
		if (receiptHeader.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		receiptHeader.setRcdMaintainSts("R");
		// Receipt Header Details Save And Update
		// =======================================
		if (receiptHeader.isNew()) {
			finReceiptHeaderDAO.save(receiptHeader, tableType);
		} else {
			finReceiptHeaderDAO.update(receiptHeader, tableType);
		}

		// Bounce reason Code
		if (receiptHeader.getManualAdvise() != null) {
			if (receiptHeader.getManualAdvise().isNew()) {
				manualAdviseDAO.save(receiptHeader.getManualAdvise(), tableType);
			} else {
				manualAdviseDAO.update(receiptHeader.getManualAdvise(), tableType);
			}
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), receiptHeader.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				receiptHeader.getBefImage(), receiptHeader));

		auditHeader.setAuditDetails(auditDetails);
		// auditHeaderDAO.addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using finReceiptHeaderDAO.delete with parameters finReceiptHeader,"_Temp" 3) Audit the record
	 * in to AuditHeader and AdtFinReceiptHeader by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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
			manualAdviseDAO.delete(receiptHeader.getManualAdvise(), TableType.TEMP_TAB);
		}

		// Delete Receipt Header
		finReceiptHeaderDAO.deleteByReceiptID(receiptHeader.getReceiptID(), TableType.TEMP_TAB);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), receiptHeader.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				receiptHeader.getBefImage(), receiptHeader));
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doApprove method do the following steps. Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. based on the Record type do
	 * following actions Update record in the main table by using finReceiptHeaderDAO.update with parameters
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
	public AuditHeader doApprove(AuditHeader aAuditHeader) throws Exception {
		logger.debug("Entering");

		String tranType = "";
		aAuditHeader = businessValidation(aAuditHeader, "doApprove");
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		FinReceiptData receiptData = (FinReceiptData) aAuditHeader.getAuditDetail().getModelData();
		FinReceiptHeader receiptHeader = receiptData.getReceiptHeader();

		// Finance Repayment Cancellation Posting Process Execution
		// =====================================
		String errorCode = "";
		boolean isGoldLoanProcess = false;
		if (FinanceConstants.FINSER_EVENT_FEEPAYMENT.equals(receiptHeader.getReceiptPurpose())) {
			errorCode = procFeeReceiptCancellation(receiptHeader);
			if (StringUtils.isBlank(errorCode)) {
				tranType = PennantConstants.TRAN_UPD;
				receiptHeader.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				receiptHeader.setRecordType("");
				receiptHeader.setRoleCode("");
				receiptHeader.setNextRoleCode("");
				receiptHeader.setTaskId("");
				receiptHeader.setNextTaskId("");
				receiptHeader.setWorkflowId(0);
				receiptHeader.setRcdMaintainSts(null);
				finReceiptHeaderDAO.update(receiptHeader, TableType.MAIN_TAB);
			}
		} else {

			// For Gold Product Category Loans. Total schedule Should be
			// reversed(Partial Settlement)
			FinanceMain financeMain = financeMainDAO.getFinanceMainForBatch(receiptHeader.getReference());
			if (StringUtils.equals(financeMain.getProductCategory(), FinanceConstants.PRODUCT_GOLD)) {
				isGoldLoanProcess = true;
			}

			// For Gold Loan Process total Future Payment should be reversed and
			// Paid
			// allocations should happen based on schedule recalculation
			if (isGoldLoanProcess) {
				errorCode = procGoldReceiptCancellation(receiptHeader, auditHeader.getAuditBranchCode());
			} else {
				errorCode = procReceiptCancellation(receiptHeader, auditHeader.getAuditBranchCode(), financeMain);
			}
		}
		if (StringUtils.isNotBlank(errorCode)) {
			throw new InterfaceException("9999", errorCode);
		}

		// Receipt Header Updation
		// =======================================

		if (!FinanceConstants.FINSER_EVENT_FEEPAYMENT.equals(receiptHeader.getReceiptPurpose())) {

			tranType = PennantConstants.TRAN_UPD;
			receiptHeader.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			receiptHeader.setRecordType("");
			receiptHeader.setRoleCode("");
			receiptHeader.setNextRoleCode("");
			receiptHeader.setTaskId("");
			receiptHeader.setNextTaskId("");
			receiptHeader.setWorkflowId(0);
			receiptHeader.setRcdMaintainSts(null);
			if (FinanceConstants.FINSER_EVENT_SCHDRPY.equals(receiptHeader.getReceiptPurpose())) {
				finReceiptHeaderDAO.update(receiptHeader, TableType.MAIN_TAB);
			} else {
				finReceiptHeaderDAO.save(receiptHeader, TableType.MAIN_TAB);
				for (FinReceiptDetail finRecpt : receiptHeader.getReceiptDetails()) {
					finReceiptDetailDAO.save(finRecpt, TableType.MAIN_TAB);

				}
			}

			List<FinReceiptDetail> finRcptDtlList = finReceiptDetailDAO
					.getReceiptHeaderByID(receiptHeader.getReceiptID(), "");

			for (FinReceiptDetail finRecpt : finRcptDtlList) {
				List<RepayScheduleDetail> repaySchdList = finReceiptDetailDAO
						.fetchRepaySchduleList(finRecpt.getReceiptSeqID());

				for (RepayScheduleDetail rpySchd : repaySchdList) {
					if (rpySchd.getWaivedAmt().compareTo(BigDecimal.ZERO) > 0) {
						finODDetailsDAO.updateWaiverAmount(receiptHeader.getReference(), rpySchd.getSchDate(),
								rpySchd.getWaivedAmt(), rpySchd.getPenaltyPayNow());
					}
				}
			}
		}

		// Bounce Reason Code
		if (receiptHeader.getManualAdvise() != null) {
			manualAdviseDAO.deleteByAdviseId(receiptHeader.getManualAdvise(), TableType.TEMP_TAB);
		}

		finReceiptDetailDAO.deleteByReceiptID(receiptHeader.getReceiptID(), TableType.TEMP_TAB);

		// Receipt Allocation Details
		allocationDetailDAO.deleteByReceiptID(receiptHeader.getReceiptID(), TableType.TEMP_TAB);

		// Delete Manual Advise Movements
		manualAdviseDAO.deleteMovementsByReceiptID(receiptHeader.getReceiptID(), TableType.TEMP_TAB.getSuffix());
		// Delete Receipt Header
		finReceiptHeaderDAO.deleteByReceiptID(receiptHeader.getReceiptID(), TableType.TEMP_TAB);

		if (ImplementationConstants.LIMIT_INTERNAL && !isGoldLoanProcess) {
			BigDecimal priAmt = BigDecimal.ZERO;

			for (FinReceiptDetail finReceiptDetail : receiptHeader.getReceiptDetails()) {
				for (FinRepayHeader header : finReceiptDetail.getRepayHeaders()) {
					for (RepayScheduleDetail rpySchd : header.getRepayScheduleDetails()) {
						priAmt = priAmt.add(rpySchd.getPrincipalSchdPayNow().add(rpySchd.getPriSchdWaivedNow()));
					}
				}
			}
			if (priAmt.compareTo(BigDecimal.ZERO) > 0) {
				FinanceMain main = financeMainDAO.getFinanceMainForBatch(receiptHeader.getReference());
				Customer customer = customerDAO.getCustomerByID(main.getCustID());
				limitManagement.processLoanRepayCancel(main, customer, priAmt,
						StringUtils.trimToEmpty(main.getProductCategory()));
			}

		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), receiptHeader.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1],
				receiptHeader.getBefImage(), receiptHeader));

		// Adding audit as deleted from TEMP table
		// auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				receiptHeader.getBefImage(), receiptHeader));

		// Adding audit as Insert/Update/deleted into main table
		// auditHeaderDAO.addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from finReceiptHeaderDAO.getErrorDetail with Error ID and
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
		FinReceiptData receiptData = (FinReceiptData) auditDetail.getModelData();
		FinReceiptHeader receiptHeader = receiptData.getReceiptHeader();

		FinReceiptHeader tempReceiptHeader = null;
		if (receiptHeader.isWorkflow()) {
			tempReceiptHeader = finReceiptHeaderDAO.getReceiptHeaderByID(receiptHeader.getReceiptID(), "_Temp");
		}
		FinReceiptHeader beFinReceiptHeader = finReceiptHeaderDAO.getReceiptHeaderByID(receiptHeader.getReceiptID(),
				"");
		FinReceiptHeader oldReceiptHeader = receiptHeader.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(receiptHeader.getReceiptID());
		errParm[0] = PennantJavaUtil.getLabel("label_ReceiptID") + ":" + valueParm[0];

		if (receiptHeader.isNew()) { // for New record or new record into work
											// flow

			if (!receiptHeader.isWorkflow()) {// With out Work flow only new
				// records
				if (beFinReceiptHeader != null) { // Record Already Exists in
														// the
													// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (receiptHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
					// records type is new
					if (beFinReceiptHeader != null || tempReceiptHeader != null) { // if
						// records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (beFinReceiptHeader == null || tempReceiptHeader != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!receiptHeader.isWorkflow()) { // With out Work flow for update
				// and delete

				if (beFinReceiptHeader == null) { // if records not exists in
														// the
													// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldReceiptHeader != null
							&& !oldReceiptHeader.getLastMntOn().equals(beFinReceiptHeader.getLastMntOn())) {
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

				if (tempReceiptHeader == null) { // if records not exists in the
					// Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempReceiptHeader != null && oldReceiptHeader != null
						&& !oldReceiptHeader.getLastMntOn().equals(tempReceiptHeader.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		// Fee Payment Cancellation or Bounce cancellation stopped When Loan is
		// not in Workflow Process
		if (StringUtils.equals(receiptHeader.getReceiptPurpose(), FinanceConstants.FINSER_EVENT_FEEPAYMENT)) {
			String finReference = receiptHeader.getReference();

			if (RepayConstants.RECEIPTTO_FINANCE.equals(receiptHeader.getRecAgainst())) {
				boolean rcdAvailable = financeMainDAO.isFinReferenceExists(finReference, "_Temp", false);
				if (!rcdAvailable) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("60209", null), usrLanguage));
				}
			}
			boolean rcdAssigned = finFeeReceiptDAO.isFinFeeReceiptAllocated(receiptHeader.getReceiptID(), "_View");
			if (rcdAssigned) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("60210", null), usrLanguage));
			}
		}

		/*
		 * =====================================================================
		 * ======================================== ================ // For Gold Loans, Not allowed to cancel when
		 * finance is in Other Maintenance if(StringUtils.equals(receiptHeader.getProductCategory(),
		 * FinanceConstants.PRODUCT_GOLD)){ String finReference = receiptHeader.getReference(); boolean rcdAvailable =
		 * financeMainDAO.isFinReferenceExists(finReference, "_Temp", false); if (rcdAvailable) {
		 * auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("60213", null),usrLanguage)); }else{
		 * 
		 * // If any receipt after cancellation receipt and including that having Waiver amount // then should not allow
		 * for cancellation Date cancelReqDate = receiptHeader.getReceiptDetails().get(0).getReceivedDate();
		 * 
		 * FinanceMain financeMain = financeMainDAO.getFinanceMainById(receiptHeader.getReference(), "", false); int
		 * minPftPeriod = getPromotionDAO().getPromtionMinPftPeriod(financeMain. getPromotionSeqId(), ""); Date
		 * maxAlwCancelRcptDate = DateUtility.addDays(financeMain.getFinStartDate(), minPftPeriod);
		 * 
		 * if(DateUtility.compare(cancelReqDate, maxAlwCancelRcptDate) <= 0){
		 * auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("60215", null),usrLanguage)); }else{
		 * List<ReceiptCancelDetail> cancelReceipts = finReceiptHeaderDAO.getReceiptCancelDetailList(cancelReqDate,
		 * finReference);
		 * 
		 * boolean waiverExists = false; sortReceipts(cancelReceipts); for (int i = cancelReceipts.size() - 1; i >= 0;
		 * i--) {
		 * 
		 * ReceiptCancelDetail receipt = cancelReceipts.get(i); if(receipt.getReceiptId() <=
		 * receiptHeader.getReceiptID()){ continue; } if(receipt.getWaviedAmt() != null &&
		 * receipt.getWaviedAmt().compareTo(BigDecimal.ZERO) > 0){ waiverExists = true; } }
		 * 
		 * // If waiver exists not allowed to do cancellation if(waiverExists){
		 * auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("60214", null),usrLanguage)); } } }
		 * 
		 * } =====================================================================
		 * ======================================== ================
		 */

		if (!PennantConstants.method_doReject.equals(method)
				&& !PennantConstants.RCD_STATUS_RESUBMITTED.equals(receiptHeader.getRecordStatus())
				&& RepayConstants.RECEIPTMODE_CASH.equals(receiptHeader.getReceiptMode())) {

			DepositMovements movement = depositDetailsDAO.getDepositMovementsByReceiptId(receiptHeader.getReceiptID(),
					"_AView");
			if (movement != null) {
				// Find Amount of Deposited Request
				BigDecimal reqAmount = BigDecimal.ZERO;
				for (FinReceiptDetail rcptDetail : receiptHeader.getReceiptDetails()) {
					if (RepayConstants.RECEIPTMODE_CASH.equals(rcptDetail.getPaymentType())) { // CASH
						reqAmount = reqAmount.add(rcptDetail.getAmount());
					}
				}
				// getDepositDetailsById
				DepositDetails depositDetails = depositDetailsDAO.getDepositDetailsById(movement.getDepositId(), "");

				// if deposit Details amount is less than Requested amount throw
				// an error
				if (depositDetails != null && reqAmount.compareTo(depositDetails.getActualAmount()) > 0) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("65036", null), usrLanguage));
				}
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
	public PresentmentDetail presentmentCancellation(PresentmentDetail presentmentDetail, String returnCode)
			throws Exception {
		logger.debug(Literal.ENTERING);

		FinReceiptHeader receiptHeader = getFinReceiptHeaderById(presentmentDetail.getReceiptID(), false);

		if (receiptHeader == null) {
			presentmentDetail.setErrorDesc(PennantJavaUtil.getLabel("label_FinReceiptHeader_Notavailable"));
			return presentmentDetail;
		}

		BounceReason bounceReason = bounceReasonDAO.getBounceReasonByReturnCode(returnCode, "");
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

		if (finReceiptDetail == null) {
			presentmentDetail.setErrorDesc(PennantJavaUtil.getLabel("label_FinReceiptDetails_Notavailable")
					+ presentmentDetail.getMandateType());
			return presentmentDetail;
		}

		ManualAdvise manualAdvise = getManualAdvise(receiptHeader, bounceReason, finReceiptDetail);

		if (manualAdvise == null) {
			presentmentDetail.setErrorDesc(
					PennantJavaUtil.getLabel("label_ManualAdvise_Notavailable") + presentmentDetail.getMandateType());
			return presentmentDetail;
		}
		receiptHeader.setManualAdvise(manualAdvise);
		receiptHeader.setReceiptModeStatus(RepayConstants.PAYSTATUS_BOUNCE);
		receiptHeader.setBounceDate(DateUtility.getAppDate());

		FinanceMain financeMain = financeMainDAO.getFinanceMainForBatch(receiptHeader.getReference());

		// Receipts Cancellation Process
		String errorMsg = procReceiptCancellation(receiptHeader, PennantConstants.APP_PHASE_EOD, financeMain);

		if (StringUtils.trimToNull(errorMsg) == null) {

			// Update ReceiptHeader
			finReceiptHeaderDAO.update(receiptHeader, TableType.MAIN_TAB);

			// Limits update against PresentmentCancellation
			if (ImplementationConstants.LIMIT_INTERNAL) {

				BigDecimal priAmt = BigDecimal.ZERO;
				for (FinReceiptDetail receiptDetail : receiptHeader.getReceiptDetails()) {
					FinRepayHeader header = receiptDetail.getRepayHeader();
					for (RepayScheduleDetail rpySchd : header.getRepayScheduleDetails()) {
						priAmt = priAmt.add(rpySchd.getPrincipalSchdPayNow().add(rpySchd.getPriSchdWaivedNow()));
					}
				}
				if (priAmt.compareTo(BigDecimal.ZERO) > 0) {

					FinanceMain main = financeMainDAO.getFinanceMainForBatch(receiptHeader.getReference());
					Customer customer = customerDAO.getCustomerByID(main.getCustID());

					// Update Limit Exposures
					limitManagement.processLoanRepayCancel(main, customer, priAmt,
							StringUtils.trimToEmpty(main.getProductCategory()));
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
	private ManualAdvise getManualAdvise(FinReceiptHeader receiptHeader, BounceReason bounceReason,
			FinReceiptDetail finReceiptDetail) {
		logger.debug(Literal.ENTERING);

		Rule rule = ruleDAO.getRuleByID(bounceReason.getRuleID(), "");
		BigDecimal bounceAmt = BigDecimal.ZERO;

		Map<String, Object> eventMapping = null;
		Map<String, Object> fieldsAndValues = finReceiptDetail.getDeclaredFieldValues();
		bounceReason.getDeclaredFieldValues(fieldsAndValues);

		eventMapping = financeMainDAO.getGLSubHeadCodes(receiptHeader.getReference());

		if (rule != null) {
			fieldsAndValues.put("br_finType", receiptHeader.getFinType());
			if (eventMapping != null) {
				fieldsAndValues.put("emptype", eventMapping.get("Emptype"));
				fieldsAndValues.put("branchcity", eventMapping.get("Branchcity"));
				fieldsAndValues.put("fincollateralreq", eventMapping.get("fincollateralreq"));
				fieldsAndValues.put("btloan", eventMapping.get("btloan"));
				fieldsAndValues.put("ae_businessvertical", eventMapping.get("Businessvertical"));
				fieldsAndValues.put("ae_alwflexi", eventMapping.get("AlwFlexi"));
				fieldsAndValues.put("ae_finbranch", eventMapping.get("FinBranch"));
				fieldsAndValues.put("ae_entitycode", eventMapping.get("Entitycode"));
			}

			bounceAmt = (BigDecimal) ruleExecutionUtil.executeRule(rule.getSQLRule(), fieldsAndValues,
					receiptHeader.getFinCcy(), RuleReturnType.DECIMAL);
		}

		ManualAdvise manualAdvise = new ManualAdvise();
		manualAdvise.setAdviseType(FinanceConstants.MANUAL_ADVISE_RECEIVABLE);
		manualAdvise.setFinReference(receiptHeader.getReference());
		manualAdvise.setFeeTypeID(0);
		manualAdvise.setSequence(0);
		manualAdvise.setAdviseAmount(
				PennantApplicationUtil.unFormateAmount(bounceAmt, CurrencyUtil.getFormat(receiptHeader.getFinCcy())));
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
	private String procReceiptCancellation(FinReceiptHeader receiptHeader, String postBranch, FinanceMain financeMain)
			throws Exception {
		logger.debug("Entering");

		boolean alwSchdReversalByLog = false;
		long postingId = postingsDAO.getPostingId();
		String curStatus = finReceiptHeaderDAO.getReceiptModeStatus(receiptHeader.getReceiptID(), "");

		// Valid Check for Finance Reversal On Active Finance Or not with
		// ValueDate CheckUp
		String finReference = receiptHeader.getReference();

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
		// postingsPreparationUtil.postReversalsByLinkedTranID(linkedTranId);
		postingsPreparationUtil.postReversalsByPostRef(receiptHeader.getReceiptID(), postingId);

		if (receiptHeader.getReceiptDetails() != null && !receiptHeader.getReceiptDetails().isEmpty()) {
			for (FinReceiptDetail detail : receiptHeader.getReceiptDetails()) {
				if (StringUtils.equals(detail.getPaymentType(), receiptHeader.getReceiptMode())
						&& !StringUtils.equals(receiptHeader.getReceiptMode(), RepayConstants.RECEIPTMODE_EXCESS)) {
					String receiptNumber = detail.getPaymentRef();
					if (StringUtils.isNotBlank(receiptNumber)) {
						List<Long> tranIdList = finStageAccountingLogDAO.getTranIdListByReceipt(receiptNumber);
						if (tranIdList != null && !tranIdList.isEmpty()) {
							for (Long linkedTranID : tranIdList) {
								postingsPreparationUtil.postReversalsByLinkedTranID(linkedTranID);
							}
							finStageAccountingLogDAO.deleteByReceiptNo(receiptNumber);
						}
					}
				}
			}
		}

		BigDecimal unRealizeAmz = BigDecimal.ZERO;
		BigDecimal unRealizeLpi = BigDecimal.ZERO;
		BigDecimal unRealizeLpiGst = BigDecimal.ZERO;
		BigDecimal unRealizeLpp = BigDecimal.ZERO;
		BigDecimal unRealizeLppGst = BigDecimal.ZERO;
		if (receiptDetails != null && !receiptDetails.isEmpty()) {
			for (int i = receiptDetails.size() - 1; i >= 0; i--) {

				FinReceiptDetail receiptDetail = receiptDetails.get(i);

				if (isBounceProcess && (StringUtils.equals(receiptDetail.getPaymentType(),
						RepayConstants.RECEIPTMODE_EXCESS)
						|| StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_EMIINADV)
						|| StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_PAYABLE))) {
					continue;
				}

				// Fetch Log Entry Details Greater than this Repayments Entry ,
				// which are having Schedule Recalculation
				// If Any Exist Case after this Repayments with Schedule
				// Recalculation then Stop Process
				// ============================================
				if (alwSchdReversalByLog) {
					List<FinLogEntryDetail> list = finLogEntryDetailDAO.getFinLogEntryDetailList(finReference,
							receiptDetail.getLogKey());
					if (list != null && !list.isEmpty()) {
						ErrorDetail errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("60206", "", null),
								PennantConstants.default_Language);
						return errorDetail.getMessage();
					}
				}

				// Finance Repayments Amount Updation if Principal Amount Exists
				long linkedTranId = 0;
				FinRepayHeader rpyHeader = receiptDetail.getRepayHeader();
				isRcdFound = true;
				if (rpyHeader != null) {
					linkedTranId = rpyHeader.getLinkedTranId();

					if (rpyHeader.getExcessAmount().compareTo(BigDecimal.ZERO) > 0) {

						// Fetch Excess Amount Details
						FinExcessAmount excess = finExcessAmountDAO.getExcessAmountsByRefAndType(finReference,
								receiptHeader.getExcessAdjustTo());

						if ((StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_CHEQUE)
								|| StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_DD))
								&& StringUtils.equals(RepayConstants.PAYSTATUS_DEPOSITED, curStatus)) {
							if (excess == null || excess.getReservedAmt().compareTo(rpyHeader.getExcessAmount()) < 0) {
								ErrorDetail errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("60205", "", null),
										PennantConstants.default_Language);
								return errorDetail.getMessage();
							}

						} else {

							if (excess == null || excess.getBalanceAmt().compareTo(rpyHeader.getExcessAmount()) < 0) {
								ErrorDetail errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("60205", "", null),
										PennantConstants.default_Language);
								return errorDetail.getMessage();
							}
						}

						// Update Reserve Amount in FinExcessAmount

						// Excess Amounts reversal Updations
						if ((StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_CHEQUE)
								|| StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_DD))
								&& StringUtils.equals(RepayConstants.PAYSTATUS_DEPOSITED, curStatus)) {
							finExcessAmountDAO.deductExcessReserve(excess.getExcessID(), rpyHeader.getExcessAmount());
						} else {

							finExcessAmountDAO.updateExcessBal(excess.getExcessID(),
									rpyHeader.getExcessAmount().negate());
						}

						isRcdFound = true;
					}

					if (rpyHeader.getPriAmount().compareTo(BigDecimal.ZERO) > 0) {
						totalPriAmount = totalPriAmount.add(rpyHeader.getPriAmount());
					}

					isRcdFound = true;

					// Remove Repayments Terms based on Linked Transaction ID
					// ============================================
					if (linkedTranId > 0) {
						financeRepaymentsDAO.deleteRpyDetailbyLinkedTranId(linkedTranId, finReference);
					}

					// Remove FinRepay Header Details
					// financeRepaymentsDAO.deleteFinRepayHeaderByTranId(finReference,
					// linkedTranId, "");

					// Remove Repayment Schedule Details
					// financeRepaymentsDAO.deleteFinRepaySchListByTranId(finReference,
					// linkedTranId, "");

					// Gathering All repayments Schedule List
					if (rpyHeader.getRepayScheduleDetails() != null && !rpyHeader.getRepayScheduleDetails().isEmpty()) {
						rpySchdList.addAll(rpyHeader.getRepayScheduleDetails());
					}

					// Update Profit Details for UnRealized Income
					unRealizeAmz = unRealizeAmz.add(rpyHeader.getRealizeUnAmz());

					// Update Profit Details for UnRealized LPI
					unRealizeLpi = unRealizeLpi.add(rpyHeader.getRealizeUnLPI());
					unRealizeLpiGst = unRealizeLpiGst.add(rpyHeader.getRealizeUnLPIGst());

					// Update Profit Details for UnRealized LPP
					unRealizeLpp = unRealizeLpp.add(rpyHeader.getRealizeUnLPP());
					unRealizeLppGst = unRealizeLppGst.add(rpyHeader.getRealizeUnLPPGst());
				}

				// Update Log Entry Based on FinPostDate and Reference
				// ============================================
				if (receiptDetail.getLogKey() != 0 && receiptDetail.getLogKey() != Long.MIN_VALUE) {
					FinLogEntryDetail detail = finLogEntryDetailDAO.getFinLogEntryDetail(receiptDetail.getLogKey());
					if (detail == null) {
						ErrorDetail errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("60207", "", null),
								PennantConstants.default_Language);
						return errorDetail.getMessage();
					}
					logKey = detail.getLogKey();
					detail.setReversalCompleted(true);
					finLogEntryDetailDAO.updateLogEntryStatus(detail);
				}

				// Manual Advise Movements Reversal
				List<ManualAdviseMovements> advMovements = manualAdviseDAO.getAdvMovementsByReceiptSeq(
						receiptDetail.getReceiptID(), receiptDetail.getReceiptSeqID(), "_AView");
				List<ManualAdviseMovements> advMovementsTemp = new ArrayList<ManualAdviseMovements>();
				if (advMovements != null && !advMovements.isEmpty()) {
					isRcdFound = true;
					for (ManualAdviseMovements movement : advMovements) {

						if ((movement.getPaidAmount().add(movement.getWaivedAmount()))
								.compareTo(BigDecimal.ZERO) == 0) {
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

						manualAdviseDAO.updateAdvPayment(advise, TableType.MAIN_TAB);
					}

					// Update Movement Status
					manualAdviseDAO.updateMovementStatus(receiptDetail.getReceiptID(), receiptDetail.getReceiptSeqID(),
							receiptHeader.getReceiptModeStatus(), "");

					// GST Invoice Preparation
					long postingSeqId = 0; // TODO should be pass linkedTranId
					FinanceDetail financeDetail = financeDetailService.getFinSchdDetailById(finReference, "", false);
					this.gstInvoiceTxnService.gstInvoicePreparation(postingSeqId, financeDetail, null, advMovementsTemp,
							PennantConstants.GST_INVOICE_TRANSACTION_TYPE_CREDIT, finReference, false);
				}

			}

			if (!rpySchdList.isEmpty()) {

				// Making Single Set of Repay Schedule Details and sent to
				// Rendering
				Map<Date, RepayScheduleDetail> rpySchdMap = new HashMap<>();
				for (RepayScheduleDetail rpySchd : rpySchdList) {

					RepayScheduleDetail curRpySchd = null;
					if (rpySchdMap.containsKey(rpySchd.getSchDate())) {
						curRpySchd = rpySchdMap.get(rpySchd.getSchDate());
						curRpySchd.setPrincipalSchdPayNow(
								curRpySchd.getPrincipalSchdPayNow().add(rpySchd.getPrincipalSchdPayNow()));
						curRpySchd.setProfitSchdPayNow(
								curRpySchd.getProfitSchdPayNow().add(rpySchd.getProfitSchdPayNow()));
						curRpySchd.setTdsSchdPayNow(curRpySchd.getTdsSchdPayNow().add(rpySchd.getTdsSchdPayNow()));
						curRpySchd.setLatePftSchdPayNow(
								curRpySchd.getLatePftSchdPayNow().add(rpySchd.getLatePftSchdPayNow()));
						curRpySchd.setSchdFeePayNow(curRpySchd.getSchdFeePayNow().add(rpySchd.getSchdFeePayNow()));
						curRpySchd.setSchdInsPayNow(curRpySchd.getSchdInsPayNow().add(rpySchd.getSchdInsPayNow()));
						curRpySchd.setPenaltyPayNow(curRpySchd.getPenaltyPayNow().add(rpySchd.getPenaltyPayNow()));
						rpySchdMap.remove(rpySchd.getSchDate());
					} else {
						curRpySchd = rpySchd;
					}

					// Adding New Repay Schedule Object to Map after Summing
					// data
					rpySchdMap.put(rpySchd.getSchDate(), curRpySchd);
				}

				rpySchdList = sortRpySchdDetails(new ArrayList<>(rpySchdMap.values()));
				List<FinanceScheduleDetail> updateSchdList = new ArrayList<>();
				List<FinFeeScheduleDetail> updateFeeList = new ArrayList<>();
				List<FinSchFrqInsurance> updateInsList = new ArrayList<>();
				Map<String, FinanceScheduleDetail> schdMap = null;

				FinScheduleData scheduleData = null;
				if (!alwSchdReversalByLog) {

					schdMap = new HashMap<>();
					scheduleData = new FinScheduleData();
					scheduleData.setFinanceScheduleDetails(
							financeScheduleDetailDAO.getFinScheduleDetails(finReference, "", false));

					for (FinanceScheduleDetail schd : scheduleData.getFinanceScheduleDetails()) {
						schdMap.put(DateUtility.format(schd.getSchDate(), PennantConstants.DBDateFormat), schd);
					}
				}

				for (RepayScheduleDetail rpySchd : rpySchdList) {

					// Schedule Detail Reversals
					if (!alwSchdReversalByLog) {

						FinanceScheduleDetail curSchd = null;
						boolean schdUpdated = false;
						if (schdMap
								.containsKey(DateUtility.format(rpySchd.getSchDate(), PennantConstants.DBDateFormat))) {
							curSchd = schdMap
									.get(DateUtility.format(rpySchd.getSchDate(), PennantConstants.DBDateFormat));

							// Principal Payment
							if (rpySchd.getPrincipalSchdPayNow().compareTo(BigDecimal.ZERO) > 0) {
								curSchd.setSchdPriPaid(
										curSchd.getSchdPriPaid().subtract(rpySchd.getPrincipalSchdPayNow()));
								curSchd.setSchPriPaid(false);
								schdUpdated = true;
							}

							// Profit Payment
							if (rpySchd.getProfitSchdPayNow().compareTo(BigDecimal.ZERO) > 0) {
								curSchd.setSchdPftPaid(
										curSchd.getSchdPftPaid().subtract(rpySchd.getProfitSchdPayNow()));
								curSchd.setSchPftPaid(false);
								schdUpdated = true;
							}

							// TDS Payment
							if (rpySchd.getTdsSchdPayNow().compareTo(BigDecimal.ZERO) > 0) {
								curSchd.setTDSPaid(curSchd.getTDSPaid().subtract(rpySchd.getTdsSchdPayNow()));
								schdUpdated = true;
							}

							// Fee Detail Payment
							if (rpySchd.getSchdFeePayNow().compareTo(BigDecimal.ZERO) > 0) {
								curSchd.setSchdFeePaid(curSchd.getSchdFeePaid().subtract(rpySchd.getSchdFeePayNow()));
								schdUpdated = true;
							}

							// Insurance Detail Payment
							if (rpySchd.getSchdInsPayNow().compareTo(BigDecimal.ZERO) > 0) {
								curSchd.setSchdInsPaid(curSchd.getSchdInsPaid().subtract(rpySchd.getSchdInsPayNow()));
								schdUpdated = true;
							}

							// Prepare List Schedules which will be updated
							if (schdUpdated) {
								updateSchdList.add(curSchd);
							}
						}
					}

					// Overdue Recovery Details Reset Back to Original State ,
					// If any penalties Paid On this Repayments
					// Process
					// ============================================
					if (rpySchd.getPenaltyPayNow().compareTo(BigDecimal.ZERO) > 0
							|| rpySchd.getLatePftSchdPayNow().compareTo(BigDecimal.ZERO) > 0) {
						finODDetailsDAO.updateReversals(finReference, rpySchd.getSchDate(), rpySchd.getPenaltyPayNow(),
								rpySchd.getLatePftSchdPayNow());
					}

					// Update Fee Balance
					// ============================================
					if (rpySchd.getSchdFeePayNow().compareTo(BigDecimal.ZERO) > 0) {
						List<FinFeeScheduleDetail> feeList = finFeeScheduleDetailDAO
								.getFeeScheduleBySchDate(finReference, rpySchd.getSchDate());
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
						List<FinSchFrqInsurance> insList = finInsurancesDAO.getInsScheduleBySchDate(finReference,
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
					financeScheduleDetailDAO.updateListForRpy(updateSchdList);
				}

				// Fee Schedule Details Updation
				if (!updateFeeList.isEmpty()) {
					finFeeScheduleDetailDAO.updateFeeSchdPaids(updateFeeList);
				}

				// Insurance Schedule Details Updation
				if (!updateInsList.isEmpty()) {
					finInsurancesDAO.updateInsSchdPaids(updateInsList);
				}

				rpySchdList = null;
				rpySchdMap = null;
				updateSchdList = null;
				updateFeeList = null;
				updateInsList = null;

				// Deletion of Finance Schedule Related Details From Main Table
				FinanceProfitDetail pftDetail = financeProfitDetailDAO.getFinProfitDetailsById(finReference);

				if (scheduleData != null) {
					scheduleData.setFinanceMain(financeMain);
					if (alwSchdReversalByLog) {
						listDeletion(finReference, "", false, 0);

						// Fetching Last Log Entry Finance Details
						scheduleData = getFinSchDataByFinRef(finReference, logKey, "_Log");
						scheduleData.setFinanceMain(financeMain);

						// Re-Insert Log Entry Data before Repayments Process
						// Recalculations
						listSave(scheduleData, "", 0);

						// Delete Data from Log Entry Tables After Inserting into
						// Main Tables
						for (int i = 0; i < receiptDetails.size(); i++) {
							listDeletion(finReference, "_Log", false, receiptDetails.get(i).getLogKey());
						}
					} else {
						scheduleData.setFinanceScheduleDetails(new ArrayList<>(schdMap.values()));
					}
				}

				pftDetail.setLpiAmount(receiptHeader.getLpiAmount());
				pftDetail.setLppAmount(receiptHeader.getLppAmount());
				pftDetail.setGstLpiAmount(receiptHeader.getGstLpiAmount());
				pftDetail.setGstLppAmount(receiptHeader.getGstLppAmount());

				// Update Profit Details for UnRealized Income & Late Payment
				// Difference
				pftDetail.setAmzTillLBD(pftDetail.getAmzTillLBD().subtract(unRealizeAmz));
				pftDetail.setLpiTillLBD(pftDetail.getLpiTillLBD().subtract(unRealizeLpi));
				pftDetail.setGstLpiTillLBD(pftDetail.getGstLpiTillLBD().subtract(unRealizeLpiGst));
				pftDetail.setLppTillLBD(pftDetail.getLppTillLBD().subtract(unRealizeLpp));
				pftDetail.setGstLppTillLBD(pftDetail.getGstLppTillLBD().subtract(unRealizeLppGst));

				// Profit Details Recalculation Process
				pftDetail = accrualService.calProfitDetails(financeMain, scheduleData.getFinanceScheduleDetails(),
						pftDetail, DateUtility.getAppDate());
				if (unRealizeLpi.compareTo(BigDecimal.ZERO) > 0 || unRealizeLpp.compareTo(BigDecimal.ZERO) > 0) {
					pftDetail = postReceiptCanAdjust(scheduleData, pftDetail, 0);
				}

				// Check Current Finance Max Status For updation
				// ============================================

				Date valueDate = DateUtility.getAppDate();
				if (!ImplementationConstants.LPP_CALC_SOD && !StringUtils
						.equals(scheduleData.getFinanceMain().getProductCategory(), FinanceConstants.PRODUCT_GOLD)) {
					valueDate = DateUtility.addDays(valueDate, -1);
				}
				List<FinODDetails> overdueList = finODDetailsDAO.getFinODBalByFinRef(financeMain.getFinReference());
				List<FinanceRepayments> repayments = financeRepaymentsDAO
						.getFinRepayListByFinRef(financeMain.getFinReference(), false, "");
				overdueList = latePayMarkingService.calPDOnBackDatePayment(financeMain, overdueList, valueDate,
						scheduleData.getFinanceScheduleDetails(), repayments, true, true);

				// Status Updation
				repaymentPostingsUtil.updateStatus(financeMain, valueDate, scheduleData.getFinanceScheduleDetails(),
						pftDetail, overdueList, null);

				// Overdue Details Updation after Recalculation with Current Data
				if (overdueList != null && !overdueList.isEmpty()) {
					finODDetailsDAO.updateList(overdueList);
				}
				if (totalPriAmount.compareTo(BigDecimal.ZERO) > 0) {

					// Finance Main Details Update
					financeMain.setFinRepaymentAmount(financeMain.getFinRepaymentAmount().subtract(totalPriAmount));
					financeMainDAO.updateRepaymentAmount(finReference,
							financeMain.getFinCurrAssetValue().add(financeMain.getFeeChargeAmt())
									.add(financeMain.getInsuranceAmt()),
							financeMain.getFinRepaymentAmount(), financeMain.getFinStatus(),
							FinanceConstants.FINSTSRSN_MANUAL, true, false);
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
							receiptHeader.getManualAdvise().getAdviseAmount(), postBranch,
							RepayConstants.ALLOCATION_BOUNCE);
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
						FinanceDetail financeDetailTemp = financeDetailService.getFinSchdDetailById(finReference, "",
								false);
						this.gstInvoiceTxnService.gstInvoicePreparation(postingSeqId, financeDetailTemp, null,
								advMovementsTemp, PennantConstants.GST_INVOICE_TRANSACTION_TYPE_CREDIT, finReference,
								false);
					}
				}

				String adviseId = manualAdviseDAO.save(receiptHeader.getManualAdvise(), TableType.MAIN_TAB);
				receiptHeader.getManualAdvise().setAdviseID(Long.parseLong(adviseId));
			}

			// Update Receipt Details based on Receipt Mode
			for (int i = 0; i < receiptHeader.getReceiptDetails().size(); i++) {
				FinReceiptDetail receiptDetail = receiptHeader.getReceiptDetails().get(i);
				if (!isBounceProcess
						|| (StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_CHEQUE)
								|| StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_DD))) {
					finReceiptDetailDAO.updateReceiptStatus(receiptDetail.getReceiptID(),
							receiptDetail.getReceiptSeqID(), receiptHeader.getReceiptModeStatus());

					// Receipt Reversal for Excess or Payable
					if (!isBounceProcess) {

						if (StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_EXCESS)
								|| StringUtils.equals(receiptDetail.getPaymentType(),
										RepayConstants.RECEIPTMODE_EMIINADV)) {

							// Excess utilize Reversals
							finExcessAmountDAO.updateExcessAmount(receiptDetail.getPayAgainstID(), "U",
									receiptDetail.getAmount().negate());

						} else if (StringUtils.equals(receiptDetail.getPaymentType(),
								RepayConstants.RECEIPTMODE_PAYABLE)) {

							// Payable Utilize reversals
							manualAdviseDAO.reverseUtilise(receiptDetail.getPayAgainstID(), receiptDetail.getAmount());
						}
					}
				}
			}

			// Accounting Execution Process for Deposit Reversal for CASH
			if (RepayConstants.RECEIPTMODE_CASH.equals(receiptHeader.getReceiptMode())) {

				DepositMovements movement = depositDetailsDAO
						.getDepositMovementsByReceiptId(receiptHeader.getReceiptID(), "_AView");
				if (movement != null) {
					// Find Amount of Deposited Request
					BigDecimal reqAmount = BigDecimal.ZERO;
					for (FinReceiptDetail rcptDetail : receiptHeader.getReceiptDetails()) {
						if (RepayConstants.RECEIPTMODE_CASH.equals(rcptDetail.getPaymentType())) { // CASH
							reqAmount = reqAmount.add(rcptDetail.getAmount());
						}
					}

					// need to check accounting should be reversal or not for
					// Bank To Cash
					/*
					 * AEEvent aeEvent = this.cashManagementAccounting.generateAccounting(
					 * AccountEventConstants.ACCEVENT_BANKTOCASH, movement.getBranchCode(), movement.getBranchCode(),
					 * movement.getReservedAmount(), movement.getPartnerBankId(), movement.getMovementId(), null);
					 */

					if (reqAmount.compareTo(BigDecimal.ZERO) > 0) {
						// DECRESE Available amount in Deposit Details
						depositDetailsDAO.updateActualAmount(movement.getDepositId(), reqAmount, false, "");

						// Movement update by Transaction Type to Reversal
						depositDetailsDAO.reverseMovementTranType(movement.getMovementId());
					}
				}
			}

			// Accounting Execution Process for Deposit Reversal for Cheque / DD
			if (RepayConstants.RECEIPTMODE_CHEQUE.equals(receiptHeader.getReceiptMode())
					|| RepayConstants.RECEIPTMODE_DD.equals(receiptHeader.getReceiptMode())) {

				// Verify Cheque or DD Details exists in Deposited Cheques
				DepositCheques depositCheque = depositChequesDAO
						.getDepositChequeByReceiptID(receiptHeader.getReceiptID());

				if (depositCheque != null) {
					if (depositCheque.getLinkedTranId() > 0) {
						// Postings Reversal
						postingsPreparationUtil.postReversalsByLinkedTranID(depositCheque.getLinkedTranId());
						// Make Deposit Cheque to Reversal Status
						depositChequesDAO.reverseChequeStatus(depositCheque.getMovementId(),
								receiptHeader.getReceiptID(), depositCheque.getLinkedTranId());
					} else {
						logger.info("Postings Id is not available in deposit cheques");
						throw new InterfaceException("CHQ001", "Issue with deposit details postings prepartion.");
					}
				} else {
					// Available Decrease
					DepositMovements movement = depositDetailsDAO
							.getDepositMovementsByReceiptId(receiptHeader.getReceiptID(), "_AView");
					if (movement != null) {

						// Find Amount of Deposited Request
						BigDecimal reqAmount = BigDecimal.ZERO;
						for (FinReceiptDetail rcptDetail : receiptHeader.getReceiptDetails()) {
							if (RepayConstants.RECEIPTMODE_CHEQUE.equals(rcptDetail.getPaymentType())
									|| RepayConstants.RECEIPTMODE_DD.equals(rcptDetail.getPaymentType())) { // Cheque/DD
								reqAmount = reqAmount.add(rcptDetail.getAmount());
							}
						}

						// DECRESE Available amount in Deposit Details
						depositDetailsDAO.updateActualAmount(movement.getDepositId(), reqAmount, false, "");

						// Movement update by Transaction Type to Reversal
						depositDetailsDAO.reverseMovementTranType(movement.getDepositId());
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
			FinRepayHeader rpyHeader = receiptDetail.getRepayHeader();
			linkedTranId = rpyHeader.getLinkedTranId();
			break;
		}

		// Posting Reversal Case Program Calling in Equation
		// ============================================
		postingsPreparationUtil.postReversalsByLinkedTranID(linkedTranId);

		// Update Receipt Detail Status
		for (FinReceiptDetail receiptDetail : receiptHeader.getReceiptDetails()) {
			finReceiptDetailDAO.updateReceiptStatus(receiptDetail.getReceiptID(), receiptDetail.getReceiptSeqID(),
					receiptHeader.getReceiptModeStatus());
		}

		return null;
	}

	/**
	 * Method for Processing Cancellation for the Gold Loan Receipt
	 * 
	 * @param cancelRequestedReceipt
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private String procGoldReceiptCancellation(FinReceiptHeader cancelRequestedReceipt, String postBranch)
			throws Exception {
		// FIXME Enable Gold Loan module
		return null;
	}

	/**
	 * Method for Posting Capitalization Differences
	 * 
	 * @param scheduleData
	 * @param profitDetail
	 * @param postBranch
	 * @return
	 * @throws Exception
	 */
	private FinanceProfitDetail postReceiptCanAdjust(FinScheduleData scheduleData, FinanceProfitDetail profitDetail,
			long oldLinkedTranId) throws Exception {

		FinanceMain financeMain = scheduleData.getFinanceMain();
		// Accrual Difference Postings
		long accountingID = AccountingConfigCache.getCacheAccountSetID(financeMain.getFinType(),
				AccountEventConstants.ACCEVENT_AMZ, FinanceConstants.MODULEID_FINTYPE);

		Date derivedAppDate = DateUtility.getDerivedAppDate();
		if (accountingID != Long.MIN_VALUE) {

			AEEvent aeEvent = AEAmounts.procCalAEAmounts(profitDetail, scheduleData.getFinanceScheduleDetails(),
					AccountEventConstants.ACCEVENT_AMZ, derivedAppDate, derivedAppDate);

			// UnAccrual amount should not be zero in case of "UMFC" accounting
			// aeEvent.getAeAmountCodes().setdAmz(BigDecimal.ZERO);

			BigDecimal unAmz = aeEvent.getAeAmountCodes().getdAmz();
			BigDecimal unLPIAmz = aeEvent.getAeAmountCodes().getdLPIAmz();
			BigDecimal unLPPAmz = aeEvent.getAeAmountCodes().getdLPPAmz();
			BigDecimal unGstLPIAmz = aeEvent.getAeAmountCodes().getdGSTLPIAmz();
			BigDecimal unGstLPPAmz = aeEvent.getAeAmountCodes().getdGSTLPPAmz();

			if (unAmz.compareTo(BigDecimal.ZERO) != 0 || unLPIAmz.compareTo(BigDecimal.ZERO) != 0
					|| unLPPAmz.compareTo(BigDecimal.ZERO) != 0) {
				aeEvent.setDataMap(aeEvent.getAeAmountCodes().getDeclaredFieldValues());

				// GST parameters
				String custDftBranch = null;
				String highPriorityState = null;
				String highPriorityCountry = null;

				// Customer Details
				CustomerDetails customerDetails = customerDetailsService.getCustomerDetailsById(financeMain.getCustID(),
						true, "_View");

				if (customerDetails != null) {
					custDftBranch = customerDetails.getCustomer().getCustDftBranch();
					List<CustomerAddres> addressList = customerDetails.getAddressList();
					if (CollectionUtils.isNotEmpty(addressList)) {
						for (CustomerAddres customerAddres : addressList) {
							if (customerAddres.getCustAddrPriority() == Integer
									.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
								highPriorityState = customerAddres.getCustAddrProvince();
								highPriorityCountry = customerAddres.getCustAddrCountry();
								break;
							}
						}
					}
				}

				// Set Tax Details if Already exists
				FinanceTaxDetail taxDetail = getFinanceTaxDetailDAO().getFinanceTaxDetail(financeMain.getFinReference(),
						"");

				Map<String, Object> gstExecutionMap = getFinFeeDetailService().prepareGstMappingDetails(
						financeMain.getFinBranch(), custDftBranch, highPriorityState, highPriorityCountry, taxDetail,
						financeMain.getFinBranch());

				if (gstExecutionMap != null) {
					for (String key : gstExecutionMap.keySet()) {
						if (StringUtils.isNotBlank(key)) {
							aeEvent.getDataMap().put(key, gstExecutionMap.get(key));
						}
					}
				}

				aeEvent.getAcSetIDList().add(accountingID);

				// Amortization Difference Postings
				postingsPreparationUtil.postAccounting(aeEvent);

				// Unadjusted Posting amount addition
				profitDetail.setAmzTillLBD(profitDetail.getAmzTillLBD().add(unAmz));
				profitDetail.setLpiTillLBD(profitDetail.getLpiTillLBD().add(unLPIAmz));
				profitDetail.setGstLpiTillLBD(profitDetail.getGstLpiTillLBD().add(unGstLPIAmz));
				profitDetail.setLppTillLBD(profitDetail.getLppTillLBD().add(unLPPAmz));
				profitDetail.setGstLppTillLBD(profitDetail.getGstLppTillLBD().add(unGstLPPAmz));
			}
		}

		return profitDetail;
	}

	private Date getPostDate() {
		Calendar cal = Calendar.getInstance();
		Calendar appCal = Calendar.getInstance();
		cal.setTime(DateUtility.getSysDate());
		appCal.setTime(DateUtility.getAppDate());
		cal.set(Calendar.YEAR, appCal.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, appCal.get(Calendar.MONTH));
		cal.set(Calendar.DATE, appCal.get(Calendar.DATE));
		return cal.getTime();
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

	public List<ReceiptCancelDetail> sortReceipts(List<ReceiptCancelDetail> receipts) {

		if (receipts != null && receipts.size() > 0) {
			Collections.sort(receipts, new Comparator<ReceiptCancelDetail>() {
				@Override
				public int compare(ReceiptCancelDetail detail1, ReceiptCancelDetail detail2) {
					if (detail1.getReceiptId() > detail2.getReceiptId()) {
						return 1;
					} else if (detail1.getReceiptId() < detail2.getReceiptId()) {
						return -1;
					}
					return 0;
				}
			});
		}

		return receipts;
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
	 **/
	private FinScheduleData getFinSchDataByFinRef(String finReference, long logKey, String type) {
		logger.debug("Entering");
		FinScheduleData finSchData = new FinScheduleData();
		finSchData.setFinanceScheduleDetails(
				financeScheduleDetailDAO.getFinScheduleDetails(finReference, type, false, logKey));
		finSchData.setDisbursementDetails(
				financeDisbursementDAO.getFinanceDisbursementDetails(finReference, type, false, logKey));
		finSchData.setRepayInstructions(repayInstructionDAO.getRepayInstructions(finReference, type, false, logKey));
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
		financeScheduleDetailDAO.deleteByFinReference(finReference, tableType, isWIF, logKey);
		financeDisbursementDAO.deleteByFinReference(finReference, tableType, isWIF, logKey);
		repayInstructionDAO.deleteByFinReference(finReference, tableType, isWIF, logKey);
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
		financeScheduleDetailDAO.saveList(finDetail.getFinanceScheduleDetails(), tableType, false);

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
		financeDisbursementDAO.saveList(finDetail.getDisbursementDetails(), tableType, false);

		// Finance Repay Instruction Details
		for (int i = 0; i < finDetail.getRepayInstructions().size(); i++) {
			finDetail.getRepayInstructions().get(i).setFinReference(finDetail.getFinanceMain().getFinReference());
			finDetail.getRepayInstructions().get(i).setLogKey(logKey);
		}
		repayInstructionDAO.saveList(finDetail.getRepayInstructions(), tableType, false);

		logger.debug("Leaving ");
	}

	@Autowired
	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	@Autowired
	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	@Autowired
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	@Autowired
	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	@Autowired
	public void setFinLogEntryDetailDAO(FinLogEntryDetailDAO finLogEntryDetailDAO) {
		this.finLogEntryDetailDAO = finLogEntryDetailDAO;
	}

	@Autowired
	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	@Autowired
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Autowired
	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	@Autowired
	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}

	@Autowired
	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setRepaymentPostingsUtil(RepaymentPostingsUtil repaymentPostingsUtil) {
		this.repaymentPostingsUtil = repaymentPostingsUtil;
	}

	@Autowired
	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	@Autowired
	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	@Autowired
	public void setFinFeeScheduleDetailDAO(FinFeeScheduleDetailDAO finFeeScheduleDetailDAO) {
		this.finFeeScheduleDetailDAO = finFeeScheduleDetailDAO;
	}

	@Autowired
	public void setFinInsurancesDAO(FinInsurancesDAO finInsurancesDAO) {
		this.finInsurancesDAO = finInsurancesDAO;
	}

	@Autowired
	public void setBounceReasonDAO(BounceReasonDAO bounceReasonDAO) {
		this.bounceReasonDAO = bounceReasonDAO;
	}

	@Autowired
	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	@Autowired
	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}

	@Autowired
	public void setLimitManagement(LimitManagement limitManagement) {
		this.limitManagement = limitManagement;
	}

	@Autowired
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	@Autowired
	public void setFinFeeReceiptDAO(FinFeeReceiptDAO finFeeReceiptDAO) {
		this.finFeeReceiptDAO = finFeeReceiptDAO;
	}

	@Autowired
	public void setLatePayMarkingService(LatePayMarkingService latePayMarkingService) {
		this.latePayMarkingService = latePayMarkingService;
	}

	@Autowired
	public void setGstInvoiceTxnService(GSTInvoiceTxnService gstInvoiceTxnService) {
		this.gstInvoiceTxnService = gstInvoiceTxnService;
	}

	@Autowired
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	@Autowired
	public void setDepositChequesDAO(DepositChequesDAO depositChequesDAO) {
		this.depositChequesDAO = depositChequesDAO;
	}

	@Autowired
	public void setDepositDetailsDAO(DepositDetailsDAO depositDetailsDAO) {
		this.depositDetailsDAO = depositDetailsDAO;
	}

	@Autowired
	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public BranchCashDetailDAO getBranchCashDetailDAO() {
		return branchCashDetailDAO;
	}

	public void setBranchCashDetailDAO(BranchCashDetailDAO branchCashDetailDAO) {
		this.branchCashDetailDAO = branchCashDetailDAO;
	}

	public PromotionDAO getPromotionDAO() {
		return promotionDAO;
	}

	public void setPromotionDAO(PromotionDAO promotionDAO) {
		this.promotionDAO = promotionDAO;
	}

	public FinanceProfitDetailDAO getProfitDetailDAO() {
		return profitDetailDAO;
	}

	public void setProfitDetailDAO(FinanceProfitDetailDAO profitDetailDAO) {
		this.profitDetailDAO = profitDetailDAO;
	}

	@Autowired
	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public RepaymentProcessUtil getRepayProcessUtil() {
		return repayProcessUtil;
	}

	public void setRepayProcessUtil(RepaymentProcessUtil repayProcessUtil) {
		this.repayProcessUtil = repayProcessUtil;
	}

	@Autowired
	public void setAccrualService(AccrualService accrualService) {
		this.accrualService = accrualService;
	}

	@Autowired
	public void setFinStageAccountingLogDAO(FinStageAccountingLogDAO finStageAccountingLogDAO) {
		this.finStageAccountingLogDAO = finStageAccountingLogDAO;
	}

	@Override
	public Map<String, Object> getGLSubHeadCodes(String reference) {
		return this.financeMainDAO.getGLSubHeadCodes(reference);
	}

	@Autowired
	public void setAllocationDetailDAO(ReceiptAllocationDetailDAO allocationDetailDAO) {
		this.allocationDetailDAO = allocationDetailDAO;
	}
}