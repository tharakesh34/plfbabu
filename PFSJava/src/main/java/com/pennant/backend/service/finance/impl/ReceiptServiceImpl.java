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
 * FileName    		:  ReceiptServiceImpl.java												*                           
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
 * 19-06-2018       Siva					 0.3        Payable Reserve Amount Not 
 * 														removing on Maintenance     	 	* 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.service.finance.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.LatePayMarkingService;
import com.pennant.app.finance.limits.LimitCheckDetails;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.app.util.RepaymentProcessUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.EntityDAO;
import com.pennant.backend.dao.applicationmaster.InstrumentwiseLimitDAO;
import com.pennant.backend.dao.applicationmaster.ReasonCodeDAO;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.FeeWaiverHeaderDAO;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.FinODAmzTaxDetailDAO;
import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.finance.OverdraftScheduleDetailDAO;
import com.pennant.backend.dao.finance.ReceiptResponseDetailDAO;
import com.pennant.backend.dao.finance.ReceiptUploadDetailDAO;
import com.pennant.backend.dao.finance.ReceiptUploadHeaderDAO;
import com.pennant.backend.dao.finance.SubventionDetailDAO;
import com.pennant.backend.dao.finance.TaxHeaderDetailsDAO;
import com.pennant.backend.dao.lmtmasters.FinanceWorkFlowDAO;
import com.pennant.backend.dao.partnerbank.PartnerBankDAO;
import com.pennant.backend.dao.receipts.DepositChequesDAO;
import com.pennant.backend.dao.receipts.DepositDetailsDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.receipts.ReceiptAllocationDetailDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.dao.rmtmasters.FinTypePartnerBankDAO;
import com.pennant.backend.dao.rmtmasters.PromotionDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.applicationmaster.Assignment;
import com.pennant.backend.model.applicationmaster.AssignmentDealExcludedFee;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.applicationmaster.InstrumentwiseLimit;
import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralMovement;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.finance.DepositCheques;
import com.pennant.backend.model.finance.DepositDetails;
import com.pennant.backend.model.finance.DepositMovements;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessAmountReserve;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinReceiptQueueLog;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinTaxReceivable;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ManualAdviseReserve;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RepayMain;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.finance.SubventionDetail;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.finance.XcessPayables;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.model.receiptupload.UploadAlloctionDetail;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.finance.FeeReceiptService;
import com.pennant.backend.service.finance.FinFeeConfigService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.service.finance.ReceiptCancellationService;
import com.pennant.backend.service.finance.ReceiptRealizationService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.finance.ReceiptUploadHeaderService;
import com.pennant.backend.service.limitservice.impl.LimitManagement;
import com.pennant.backend.util.CashManagementConstants;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.eod.constants.EodConstants;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.advancepayment.service.AdvancePaymentService;
import com.pennanttech.pff.core.TableType;
import com.rits.cloning.Cloner;

public class ReceiptServiceImpl extends GenericFinanceDetailService implements ReceiptService {
	private static final Logger logger = LogManager.getLogger(ReceiptServiceImpl.class);

	private LimitCheckDetails limitCheckDetails;
	private FinanceDetailService financeDetailService;
	private LimitManagement limitManagement;
	private FinExcessAmountDAO finExcessAmountDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private ReceiptAllocationDetailDAO allocationDetailDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private RepaymentProcessUtil repayProcessUtil;
	private OverdraftScheduleDetailDAO overdraftScheduleDetailDAO;
	private LatePayMarkingService latePayMarkingService;
	private BankDetailService bankDetailService;
	private DepositDetailsDAO depositDetailsDAO;
	private DepositChequesDAO depositChequesDAO;
	private FinODAmzTaxDetailDAO finODAmzTaxDetailDAO;
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	private PromotionDAO promotionDAO;
	private InstrumentwiseLimitDAO instrumentwiseLimitDAO;
	private ReceiptUploadDetailDAO receiptUploadDetailDAO;
	private SubventionDetailDAO subventionDetailDAO;
	private FinTypePartnerBankDAO finTypePartnerBankDAO;
	private PartnerBankDAO partnerBankDAO;
	private FinanceWorkFlowDAO financeWorkFlowDAO;
	private ReceiptRealizationService receiptRealizationService;
	private ReceiptCancellationService receiptCancellationService;;
	private ReceiptResponseDetailDAO receiptResponseDetailDAO;
	private EntityDAO entityDAO;
	private AccountingSetDAO accountingSetDAO;
	private FinanceTaxDetailDAO financeTaxDetailDAO;
	private ReceiptUploadHeaderDAO receiptUploadHeaderDAO;
	private TaxHeaderDetailsDAO taxHeaderDetailsDAO;
	private ReceiptUploadHeaderService receiptUploadHeaderService;
	private ReasonCodeDAO reasonCodeDAO;
	private AdvancePaymentService advancePaymentService;
	private FeeTypeDAO feeTypeDAO;
	private FeeWaiverHeaderDAO feeWaiverHeaderDAO;
	private FeeReceiptService feeReceiptService;
	private FinFeeConfigService finFeeConfigService;

	public ReceiptServiceImpl() {
		super();
	}

	/**
	 * Method for Fetching Receipt Details , record is waiting for Realization
	 * 
	 * @param finReference
	 * @return
	 */
	@Override
	public FinReceiptHeader getFinReceiptHeaderById(long receiptID, boolean isFeePayment, String type) {
		logger.debug("Entering");

		// Receipt Header Details
		FinReceiptHeader receiptHeader = null;
		receiptHeader = finReceiptHeaderDAO.getReceiptHeaderByID(receiptID, type);

		// Fetch Receipt Detail List
		if (receiptHeader == null) {
			logger.debug("Leaving");
			return receiptHeader;
		}

		if ("_FEView".equalsIgnoreCase(type)) {
			type = "_View";
		}

		List<FinReceiptDetail> receiptDetailList = null;
		receiptDetailList = finReceiptDetailDAO.getReceiptHeaderByID(receiptID, type);
		int size = receiptDetailList.size();
		if (size > 0) {
			receiptHeader.setValueDate(receiptDetailList.get(size - 1).getValueDate());
		}
		if (receiptDetailList != null && !receiptDetailList.isEmpty()) {
			for (FinReceiptDetail receiptDetail : receiptDetailList) {
				if (StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_PAYABLE)) {
					ManualAdviseMovements advMov = manualAdviseDAO.getAdvMovByReceiptSeq(receiptID,
							receiptDetail.getReceiptSeqID(), receiptDetail.getPayAgainstID(),
							StringUtils.equals(type, "_View") ? "_Temp" : "");
					if (advMov != null) {
						receiptDetail.setPayAdvMovement(advMov);
					}

				}
			}
		}

		receiptHeader.setReceiptDetails(receiptDetailList);
		// Receipt Allocation Details
		if (!isFeePayment) {
			List<ReceiptAllocationDetail> allocations = allocationDetailDAO.getAllocationsByReceiptID(receiptID, type);

			if (CollectionUtils.isNotEmpty(allocations)) {
				for (ReceiptAllocationDetail allocation : allocations) {
					allocation.setTotalPaid(allocation.getPaidAmount().add(allocation.getTdsPaid()));
					Long headerId = allocation.getTaxHeaderId();
					if (headerId > 0) {
						List<Taxes> taxDetails = getTaxHeaderDetailsDAO().getTaxDetailById(headerId, type);
						TaxHeader taxHeader = new TaxHeader(headerId);
						taxHeader.setTaxDetails(taxDetails);
						allocation.setTaxHeader(taxHeader);
					}
				}
			}

			receiptHeader.setAllocations(allocations);
		} else {
			// Fetch Repay Headers List
			for (FinReceiptDetail rcd : receiptDetailList) {
				rcd.setRepayHeader(financeRepaymentsDAO.getFinRepayHeadersByReceipt(rcd.getReceiptSeqID(), ""));
			}
			// Bounce reason Code
			if (RepayConstants.RECEIPTMODE_CHEQUE.equalsIgnoreCase(receiptHeader.getReceiptMode())) {
				receiptHeader.setManualAdvise(manualAdviseDAO.getManualAdviseByReceiptId(receiptID, "_View"));
			}
			if (StringUtils.isBlank(receiptHeader.getExtReference())) {
				receiptHeader.setPaidFeeList(
						feeReceiptService.getPaidFinFeeDetails(receiptHeader.getReference(), receiptID, "_View"));
			} else {
				receiptHeader.setPaidFeeList(
						feeReceiptService.getPaidFinFeeDetails(receiptHeader.getExtReference(), receiptID, "_View"));
				FinReceiptHeader frh = finReceiptHeaderDAO.getFinTypeByReceiptID(receiptHeader.getReceiptID());
				if (frh != null) {
					receiptHeader.setFinType(frh.getFinType());
					receiptHeader.setFinTypeDesc(frh.getFinTypeDesc());
					receiptHeader.setFinCcy(frh.getFinCcy());
					receiptHeader.setFinCcyDesc(frh.getFinCcyDesc());
				}

			}
		}

		logger.debug("Leaving");
		return receiptHeader;
	}

	/**
	 * Method for Fetching FInance Details & Repay Schedule Details
	 * 
	 * @param finReference
	 * @return
	 */
	@Override
	public FinReceiptData getFinReceiptDataById(String finReference, String eventCode, String procEdtEvent,
			String userRole) {
		logger.debug("Entering");

		// All the data should be from main tables OR views only.
		FinReceiptData receiptData = new FinReceiptData();
		receiptData.setFinReference(finReference);

		FinanceDetail financeDetail = new FinanceDetail();
		receiptData.setFinanceDetail(financeDetail);
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		scheduleData.setFinReference(finReference);

		// Finance Details from Main Table View
		FinanceMain financeMain = financeMainDAO.getFinanceMainById(finReference, "_AView", false);

		if (financeMain == null) {
			logger.debug("Leaving");
			return receiptData;
		}

		// FIXME: Satya
		Entity entity = entityDAO.getEntity(financeMain.getLovDescEntityCode(), "");
		if (entity != null) {
			financeMain.setEntityDesc(entity.getEntityDesc());
		}

		scheduleData.setFinanceMain(financeMain);

		// Finance Type Details from Table
		FinanceType financeType = financeTypeDAO.getOrgFinanceTypeByID(financeMain.getFinType(), "_ORGView");

		// Fetching Promotion Details from main view
		if (StringUtils.isNotBlank(financeMain.getPromotionCode())
				&& (financeMain.getPromotionSeqId() != null && financeMain.getPromotionSeqId() == 0)) {
			Promotion promotion = this.promotionDAO.getPromotionByCode(financeMain.getPromotionCode(), "_AView");
			financeType.setFInTypeFromPromotiion(promotion);
		}

		scheduleData.setFinanceType(financeType);

		// Step Policy Details List from main view
		if (financeMain.isStepFinance()) {
			scheduleData.setStepPolicyDetails(
					financeStepDetailDAO.getFinStepDetailListByFinRef(finReference, "_TView", false));
		}

		// Finance Schedule Details from Main table
		scheduleData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finReference, "", false));

		financeDetail.setFinTypeFeesList(financeDetailService.getFinTypeFees(financeMain.getFinType(), eventCode, false,
				FinanceConstants.MODULEID_FINTYPE));

		financeDetail.setFinFeeConfigList(
				finFeeConfigService.getFinFeeConfigList(financeMain.getFinReference(), eventCode, false, "_View"));

		scheduleData.setFeeEvent(eventCode);

		// Overdraft Details from main table
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
			scheduleData.setOverdraftScheduleDetails(
					overdraftScheduleDetailDAO.getOverdraftScheduleDetails(finReference, "", false));
		}

		// Finance Disbursement Details from main view
		scheduleData.setDisbursementDetails(
				financeDisbursementDAO.getFinanceDisbursementDetails(finReference, "_AView", false));

		// Finance Repayments Instruction Details from main view
		scheduleData.setRepayInstructions(repayInstructionDAO.getRepayInstructions(finReference, "_AView", false));

		// Overdue Penalty Rates from main veiw
		scheduleData.setFinODPenaltyRate(finODPenaltyRateDAO.getFinODPenaltyRateByRef(finReference, "_AView"));

		// Profit details from main table
		FinanceProfitDetail profitDetail = profitDetailsDAO.getFinProfitDetailsById(finReference);
		scheduleData.setFinPftDeatil(profitDetail);

		// Finance Customer Details from main view
		if (financeMain.getCustID() != 0 && financeMain.getCustID() != Long.MIN_VALUE) {
			financeDetail.setCustomerDetails(
					customerDetailsService.getCustomerDetailsById(financeMain.getCustID(), false, "_AView"));
		}

		// Finance Agreement Details
		// =======================================
		financeDetail.setAggrementList(
				agreementDetailService.getAggrementDetailList(financeType.getFinType(), procEdtEvent, userRole));

		// Finance Check List Details
		// =======================================
		checkListDetailService.setFinanceCheckListDetails(receiptData.getFinanceDetail(), financeType.getFinType(),
				procEdtEvent, userRole);

		// Finance Stage Accounting Posting Details
		// =======================================
		receiptData.getFinanceDetail().setStageTransactionEntries(
				transactionEntryDAO.getListTransactionEntryByRefType(financeType.getFinType(),
						StringUtils.isEmpty(procEdtEvent) ? FinanceConstants.FINSER_EVENT_ORG : procEdtEvent,
						FinanceConstants.PROCEDT_STAGEACC, userRole, "_AEView", true));

		// Repay Header Details
		if (StringUtils.isNotBlank(financeMain.getRecordType())) {
			receiptData = getWFFinReceiptDataById(receiptData, procEdtEvent);
		} else {
			FinReceiptHeader receiptHeader = new FinReceiptHeader();
			receiptData.setReceiptHeader(receiptHeader);
			receiptHeader.setReference(financeMain.getFinReference());
		}

		// Fetch Excess Amount Details
		receiptData.getReceiptHeader().setExcessAmounts(finExcessAmountDAO.getExcessAmountsByRef(finReference));

		// Fetch Payable Advise Amount Details
		receiptData.getReceiptHeader().setPayableAdvises(
				manualAdviseDAO.getManualAdviseByRef(finReference, FinanceConstants.MANUAL_ADVISE_PAYABLE, "_AView"));

		// Fee Details ( Fetch Fee Details for event fee only)
		String type = "_View";
		if (StringUtils.isBlank(financeMain.getRecordType())) {
			scheduleData.setFinFeeDetailList(finFeeDetailDAO.getFinScheduleFees(finReference, false, "_View"));
		} else {
			type = "_TView";
			scheduleData.setFinFeeDetailList(finFeeDetailDAO.getFinFeeDetailByFinRef(finReference, false, "_TView"));
		}

		if (CollectionUtils.isNotEmpty(scheduleData.getFinFeeDetailList())) {
			for (FinFeeDetail finFeeDetail : scheduleData.getFinFeeDetailList()) {
				// Tax Details
				Long headerId = finFeeDetail.getTaxHeaderId();
				if (headerId != null && headerId > 0) {
					List<Taxes> taxDetails = taxHeaderDetailsDAO.getTaxDetailById(headerId, type);
					TaxHeader taxheader = new TaxHeader();
					taxheader.setTaxDetails(taxDetails);
					taxheader.setHeaderId(headerId);
					finFeeDetail.setTaxHeader(taxheader);
				}
			}
		}

		// Finance Document Details
		List<DocumentDetails> documentList = documentDetailsDAO.getDocumentDetailsByRef(finReference,
				FinanceConstants.MODULE_NAME, procEdtEvent, "_View");
		if (financeDetail.getDocumentDetailsList() != null && !financeDetail.getDocumentDetailsList().isEmpty()) {
			financeDetail.getDocumentDetailsList().addAll(documentList);
		} else {
			financeDetail.setDocumentDetailsList(documentList);
		}

		// Collateral Details
		if (ImplementationConstants.COLLATERAL_INTERNAL) {
			financeDetail.setCollateralAssignmentList(collateralAssignmentDAO
					.getCollateralAssignmentByFinRef(finReference, FinanceConstants.MODULE_NAME, "_View"));
		} else {
			financeDetail.setFinanceCollaterals(finCollateralService.getFinCollateralsByRef(finReference, "_View"));
		}

		financeDetail.setJountAccountDetailList(jointAccountDetailService.getJoinAccountDetail(finReference, "_AView"));

		financeDetail.setGurantorsDetailList(guarantorDetailService.getGuarantorDetail(finReference, "_AView"));

		// Multi Receipts: Get In Process Receipts
		receiptData = getInProcessReceiptData(receiptData);

		logger.debug("Leaving");
		return receiptData;
	}

	public FinReceiptData getInProcessReceiptData(FinReceiptData receiptData) {
		String finReference = receiptData.getReceiptHeader().getReference();
		// Multi Receipts: Get In Process Receipts
		long curReceiptID = 0;
		if (receiptData.getReceiptHeader() != null) {
			curReceiptID = receiptData.getReceiptHeader().getReceiptID();
		}

		List<FinReceiptHeader> rchList = finReceiptHeaderDAO.getInProcessReceipts(finReference);

		if (rchList != null) {
			receiptData.setInProcRchList(rchList);
			List<ReceiptAllocationDetail> radList = allocationDetailDAO.getManualAllocationsByRef(finReference,
					curReceiptID);

			if (radList != null) {
				receiptData.setInProcRadList(radList);
			}
		}
		return receiptData;
	}

	@Override
	public FinanceType getFinanceType(String finType) {
		FinanceType financeType = financeTypeDAO.getOrgFinanceTypeByID(finType, "_ORGView");
		return financeType;
	}

	public FinReceiptData getWFFinReceiptDataById(FinReceiptData receiptData, String procEdtEvent) {

		String finReference = receiptData.getFinReference();
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		// Finance Document Details
		financeDetail.setDocumentDetailsList(documentDetailsDAO.getDocumentDetailsByRef(finReference,
				FinanceConstants.MODULE_NAME, procEdtEvent, TableType.TEMP_TAB.getSuffix()));

		// Receipt Header Details
		List<FinReceiptHeader> finReceiptHeaderList = finReceiptHeaderDAO.getReceiptHeaderByRef(finReference, "R",
				TableType.TEMP_TAB.getSuffix());
		FinReceiptHeader finReceiptHeader = new FinReceiptHeader();

		if (CollectionUtils.isNotEmpty(finReceiptHeaderList)) {
			finReceiptHeader = finReceiptHeaderList.get(0);
		}

		receiptData.setReceiptHeader(finReceiptHeader);
		// Fetch Receipt Detail List
		if (finReceiptHeader == null) {
			return receiptData;
		}

		// Cash Management
		financeMain.setDepositProcess(finReceiptHeader.isDepositProcess());

		List<FinReceiptDetail> receiptDetailList = finReceiptDetailDAO
				.getReceiptHeaderByID(receiptData.getReceiptHeader().getReceiptID(), "_TView");

		if (receiptDetailList != null && !receiptDetailList.isEmpty()) {
			for (FinReceiptDetail receiptDetail : receiptDetailList) {
				if (StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_PAYABLE)) {
					ManualAdviseMovements advMov = manualAdviseDAO.getAdvMovByReceiptSeq(
							receiptDetail.getReceiptSeqID(), receiptDetail.getReceiptSeqID(),
							TableType.TEMP_TAB.getSuffix());

					if (advMov.getTaxHeaderId() != null && advMov.getTaxHeaderId() > 0) {
						TaxHeader header = taxHeaderDetailsDAO.getTaxHeaderDetailsById(advMov.getTaxHeaderId(),
								TableType.TEMP_TAB.getSuffix());
						if (header != null) {
							header.setTaxDetails(taxHeaderDetailsDAO.getTaxDetailById(advMov.getTaxHeaderId(),
									TableType.TEMP_TAB.getSuffix()));
						}
						advMov.setTaxHeader(header);
					}
					receiptDetail.setPayAdvMovement(advMov);
				}
			}
		}

		// Fetch Repay Headers List
		List<FinRepayHeader> rpyHeaderList = financeRepaymentsDAO.getFinRepayHeadersByRef(finReference,
				TableType.TEMP_TAB.getSuffix());

		// Fetch List of Repay Schedules
		List<RepayScheduleDetail> rpySchList = financeRepaymentsDAO.getRpySchdList(finReference,
				TableType.TEMP_TAB.getSuffix());
		for (FinRepayHeader finRepayHeader : rpyHeaderList) {
			for (RepayScheduleDetail repaySchd : rpySchList) {
				if (finRepayHeader.getRepayID() == repaySchd.getRepayID()) {
					finRepayHeader.getRepayScheduleDetails().add(repaySchd);
				}
			}
		}

		// Repay Headers setting to Receipt Details
		List<FinExcessAmountReserve> excessReserves = new ArrayList<>();
		List<ManualAdviseReserve> payableReserves = new ArrayList<>();

		if (receiptDetailList == null) {
			receiptDetailList = new ArrayList<>();
		}

		for (FinReceiptDetail receiptDetail : receiptDetailList) {
			for (FinRepayHeader finRepayHeader : rpyHeaderList) {
				if (finRepayHeader.getReceiptSeqID() == receiptDetail.getReceiptSeqID()) {
					receiptDetail.setRepayHeader(finRepayHeader);
				}
			}

			// Manual Advise Movements
			int advisetype = Integer.valueOf(FinanceConstants.MANUAL_ADVISE_RECEIVABLE);
			receiptDetail.setAdvMovements(manualAdviseDAO.getAdvMovementsByReceiptSeq(receiptDetail.getReceiptID(),
					receiptDetail.getReceiptSeqID(), advisetype, "_TView"));

			for (ManualAdviseMovements advMov : receiptDetail.getAdvMovements()) {
				if (advMov.getTaxHeaderId() > 0) {
					TaxHeader header = taxHeaderDetailsDAO.getTaxHeaderDetailsById(advMov.getTaxHeaderId(),
							TableType.TEMP_TAB.getSuffix());
					if (header != null) {
						header.setTaxDetails(taxHeaderDetailsDAO.getTaxDetailById(advMov.getTaxHeaderId(),
								TableType.TEMP_TAB.getSuffix()));
					}
					advMov.setTaxHeader(header);
				}
			}

			// Excess Reserve Amounts
			excessReserves.addAll(finExcessAmountDAO.getExcessReserveList(receiptDetail.getReceiptSeqID()));

			// Payable Reserve Amounts
			payableReserves.addAll(manualAdviseDAO.getPayableReserveList(receiptDetail.getReceiptSeqID()));
		}

		receiptData.getReceiptHeader().setExcessReserves(excessReserves);
		receiptData.getReceiptHeader().setPayableReserves(payableReserves);
		receiptData.getReceiptHeader().setReceiptDetails(receiptDetailList);

		// Receipt Allocation Details
		List<ReceiptAllocationDetail> allocations = allocationDetailDAO.getAllocationsByReceiptID(
				receiptData.getReceiptHeader().getReceiptID(), TableType.TEMP_TAB.getSuffix());

		if (CollectionUtils.isNotEmpty(allocations)) {
			for (ReceiptAllocationDetail receiptAllocationDetail : allocations) {
				if (receiptAllocationDetail.getTaxHeaderId() != 0) {
					List<Taxes> taxDetailById = getTaxHeaderDetailsDAO()
							.getTaxDetailById(receiptAllocationDetail.getTaxHeaderId(), TableType.TEMP_TAB.getSuffix());
					TaxHeader taxHeader = new TaxHeader(receiptAllocationDetail.getTaxHeaderId());
					taxHeader.setTaxDetails(taxDetailById);
				}
			}
		}
		receiptData.getReceiptHeader().setAllocations(allocations);

		// 127186 --Changing table type from Temp to Tview to show
		// bounce code also along with ID
		if (StringUtils.equals(RepayConstants.PAYSTATUS_BOUNCE,
				receiptData.getReceiptHeader().getReceiptModeStatus())) {
			receiptData.getReceiptHeader().setManualAdvise(manualAdviseDAO
					.getManualAdviseByReceiptId(receiptData.getReceiptHeader().getReceiptID(), "_TView"));
		}

		// Multi Receipts: Get In Process Receipts
		receiptData = getInProcessReceiptData(receiptData);

		return receiptData;
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
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		aAuditHeader = businessValidation(aAuditHeader, "saveOrUpdate");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		//Date appDate = SysParamUtil.getAppDate();
		if (!aAuditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return aAuditHeader;
		}

		boolean changeStatus = false;

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		FinReceiptData rceiptData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();

		// Finance Stage Accounting Process
		// =======================================
		FinanceMain finMain = rceiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();
		// For Stage Accounting it is required
		finMain.setRcdMaintainSts(FinanceConstants.FINSER_EVENT_RECEIPT);
		auditHeader = executeStageAccounting(auditHeader);
		finMain.setRcdMaintainSts("");

		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}

		FinReceiptHeader receiptHeader = rceiptData.getReceiptHeader();

		String recordStatus = receiptHeader.getRecordStatus();
		if (!PennantConstants.RCD_STATUS_RESUBMITTED.equalsIgnoreCase(recordStatus)
				&& !PennantConstants.RCD_STATUS_SAVED.equalsIgnoreCase(recordStatus)) {
			changeStatus = true;
		}

		if (StringUtils.equals(FinanceConstants.DEPOSIT_MAKER, receiptHeader.getRoleCode()) && changeStatus) {
			receiptHeader.setReceiptModeStatus(RepayConstants.PAYSTATUS_INITIATED);
		} else if (StringUtils.equals(FinanceConstants.DEPOSIT_APPROVER, receiptHeader.getRoleCode()) && changeStatus) {
			receiptHeader.setReceiptModeStatus(RepayConstants.PAYSTATUS_DEPOSITED);
		} else if (StringUtils.equals(FinanceConstants.REALIZATION_MAKER, receiptHeader.getRoleCode())
				|| StringUtils.equals(FinanceConstants.KNOCKOFFCAN_MAKER, receiptHeader.getRoleCode())) {

		} else if (StringUtils.equals(FinanceConstants.REALIZATION_APPROVER, receiptHeader.getRoleCode())
				|| StringUtils.equals(FinanceConstants.KNOCKOFFCAN_APPROVER, receiptHeader.getRoleCode())) {

		} else {
			receiptHeader.setReceiptModeStatus(RepayConstants.PAYSTATUS_INITIATED);

		}

		String finReference = receiptHeader.getReference();

		long serviceUID = Long.MIN_VALUE;
		if (rceiptData.getFinanceDetail().getFinScheduleData().getFinServiceInstructions().isEmpty()) {
			FinServiceInstruction finServInst = new FinServiceInstruction();
			finServInst.setFinReference(finReference);
			finServInst.setFinEvent(rceiptData.getFinanceDetail().getModuleDefiner());

			rceiptData.getFinanceDetail().getFinScheduleData().setFinServiceInstruction(finServInst);
		}

		for (FinServiceInstruction finSerList : rceiptData.getFinanceDetail().getFinScheduleData()
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
		if (receiptHeader.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		// financeMain.setRcdMaintainSts(FinanceConstants.FINSER_EVENT_RECEIPT);
		if (tableType == TableType.MAIN_TAB) {
			receiptHeader.setRcdMaintainSts(null);
		}

		// Finance Main Details Save And Update
		// =======================================
		long receiptID = receiptHeader.getReceiptID();
		receiptHeader.setRcdMaintainSts("R");
		receiptHeader
				.setActFinReceipt(rceiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().isFinIsActive());
		if (StringUtils.equals(receiptHeader.getReceiptModeStatus(), RepayConstants.PAYSTATUS_CANCEL)) {
			receiptHeader.setBounceDate(SysParamUtil.getAppDate());
		}
		if (receiptHeader.isNew()) {

			// Save Receipt Header
			receiptID = finReceiptHeaderDAO.save(receiptHeader, tableType);

		} else {

			// Save/Update FinRepayHeader Details depends on Workflow
			if (tableType == TableType.TEMP_TAB) {

				// Update Receipt Header
				finReceiptHeaderDAO.update(receiptHeader, tableType);

				// Delete Save Receipt Detail List by Reference
				finReceiptDetailDAO.deleteByReceiptID(receiptID, tableType);

				// Delete and Save FinRepayHeader Detail list by Reference
				financeRepaymentsDAO.deleteByRef(finReference, tableType);

				// Delete and Save Repayment Schedule details by setting Repay
				// Header ID
				financeRepaymentsDAO.deleteRpySchdList(finReference, tableType.getSuffix());

				// Delete Tax Header
				deleteTaxHeaderId(receiptID, tableType.getSuffix());

				// Receipt Allocation Details
				allocationDetailDAO.deleteByReceiptID(receiptID, tableType);
			}

			// Bounce reason Code
		}
		ManualAdvise advise = manualAdviseDAO.getManualAdviseByReceiptId(receiptID, "_Temp");
		if (receiptHeader.getManualAdvise() != null) {
			if (advise == null) {
				manualAdviseDAO.save(receiptHeader.getManualAdvise(), tableType);
			} else {
				manualAdviseDAO.update(receiptHeader.getManualAdvise(), tableType);
			}
		} else {
			if (advise != null) {
				manualAdviseDAO.delete(receiptHeader.getManualAdvise(), tableType);
			}
		}

		// Save Deposit Details
		saveDepositDetails(receiptHeader, null);
		// Update Deposit Branch
		if (ImplementationConstants.DEPOSIT_PROC_REQ) {
			finReceiptHeaderDAO.updateDepositBranchByReceiptID(receiptHeader.getReceiptID(),
					receiptHeader.getUserDetails().getBranchCode(), tableType.getSuffix());
		}

		// Save Receipt Detail List by setting Receipt Header ID
		List<FinReceiptDetail> receiptDetails = receiptHeader.getReceiptDetails();

		// Manual Advise Movements
		manualAdviseDAO.deleteMovementsByReceiptID(receiptID, TableType.TEMP_TAB.getSuffix());

		for (FinReceiptDetail rd : receiptDetails) {
			rd.setReceiptID(receiptID);
			long receiptSeqID = rd.getReceiptSeqID();
			if (!rd.isDelRecord()) {
				receiptSeqID = finReceiptDetailDAO.save(rd, tableType);

				if (rd.getPayAdvMovement() != null) {
					if (rd.getPayAdvMovement().getTaxHeader() != null
							&& CollectionUtils.isNotEmpty(rd.getPayAdvMovement().getTaxHeader().getTaxDetails())) {
						List<Taxes> taxDetails = rd.getPayAdvMovement().getTaxHeader().getTaxDetails();
						Long headerId = taxHeaderDetailsDAO.save(rd.getPayAdvMovement().getTaxHeader(),
								TableType.TEMP_TAB.getSuffix());
						for (Taxes taxes : taxDetails) {
							taxes.setReferenceId(headerId);
						}
						getTaxHeaderDetailsDAO().saveTaxes(taxDetails, TableType.TEMP_TAB.getSuffix());
						rd.getPayAdvMovement().setTaxHeaderId(headerId);
					}

					rd.getPayAdvMovement().setReceiptID(receiptID);
					rd.getPayAdvMovement().setReceiptSeqID(receiptSeqID);
					manualAdviseDAO.saveMovement(rd.getPayAdvMovement(), TableType.TEMP_TAB.getSuffix());
				}
			}

			// Excess Amount Reserve
			String paymentType = rd.getPaymentType();
			if (RepayConstants.RECEIPTMODE_EXCESS.equals(paymentType)
					|| RepayConstants.RECEIPTMODE_EMIINADV.equals(paymentType)
					|| RepayConstants.RECEIPTMODE_ADVINT.equals(paymentType)
					|| RepayConstants.RECEIPTMODE_ADVEMI.equals(paymentType)
					|| RepayConstants.RECEIPTMODE_CASHCLT.equals(paymentType)
					|| RepayConstants.RECEIPTMODE_DSF.equals(paymentType)) {

				// Excess Amount make utilization
				FinExcessAmountReserve exReserve = finExcessAmountDAO.getExcessReserve(receiptSeqID,
						rd.getPayAgainstID());
				if (exReserve == null
						&& !StringUtils.equals(receiptHeader.getReceiptModeStatus(), RepayConstants.PAYSTATUS_CANCEL)) {

					// Update Excess Amount in Reserve
					finExcessAmountDAO.updateExcessReserve(rd.getPayAgainstID(), rd.getAmount());

					// Save Excess Reserve Log Amount
					finExcessAmountDAO.saveExcessReserveLog(receiptSeqID, rd.getPayAgainstID(), rd.getAmount(),
							RepayConstants.RECEIPTTYPE_RECIPT);

				} else {
					// If Receipt details re-modified in process
					if (rd.isDelRecord()) {

						// Delete Reserve Amount in FinExcessAmount
						finExcessAmountDAO.deleteExcessReserve(receiptSeqID, rd.getPayAgainstID(),
								RepayConstants.RECEIPTTYPE_RECIPT);

						// Update Reserve Amount in FinExcessAmount
						finExcessAmountDAO.updateExcessReserve(rd.getPayAgainstID(),
								exReserve.getReservedAmt().negate());

					} else {
						if (!StringUtils.equals(receiptHeader.getReceiptModeStatus(),
								RepayConstants.PAYSTATUS_CANCEL)) {
							if (rd.getAmount().compareTo(exReserve.getReservedAmt()) != 0) {
								BigDecimal diffInReserve = rd.getAmount().subtract(exReserve.getReservedAmt());

								// Update Reserve Amount in FinExcessAmount
								finExcessAmountDAO.updateExcessReserve(rd.getPayAgainstID(), diffInReserve);

								// Update Excess Reserve Log
								finExcessAmountDAO.updateExcessReserveLog(receiptSeqID, rd.getPayAgainstID(),
										diffInReserve, RepayConstants.RECEIPTTYPE_RECIPT);
							}
						}
					}
				}
			}

			// Payable Amount Reserve
			if (StringUtils.equals(paymentType, RepayConstants.RECEIPTMODE_PAYABLE)
					&& !StringUtils.equals(receiptHeader.getReceiptModeStatus(), RepayConstants.PAYSTATUS_CANCEL)) {

				// Payable Amount make utilization
				ManualAdviseReserve payableReserve = manualAdviseDAO.getPayableReserve(receiptSeqID,
						rd.getPayAgainstID());

				BigDecimal payableAmt = rd.getAmount();
				if (rd.getPayAdvMovement() != null) {
					if (rd.getPayAdvMovement().getTaxHeader() != null
							&& CollectionUtils.isNotEmpty(rd.getPayAdvMovement().getTaxHeader().getTaxDetails())) {
						List<Taxes> taxDetails = rd.getPayAdvMovement().getTaxHeader().getTaxDetails();
						for (Taxes taxes : taxDetails) {
							if (StringUtils.equals(taxes.getTaxType(), FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {
								payableAmt = payableAmt.subtract(taxes.getPaidTax());
							}
						}
					}
				}

				if (payableReserve == null) {

					// Update Payable Amount in Reserve
					manualAdviseDAO.updatePayableReserve(rd.getPayAgainstID(), payableAmt);

					// Save Payable Reserve Log Amount
					manualAdviseDAO.savePayableReserveLog(receiptSeqID, rd.getPayAgainstID(), payableAmt);

				} else {
					// If Receipt details re-modified in process
					if (rd.isDelRecord()) {

						// Delete Reserved Log against Payable Advise ID and
						// Receipt ID
						manualAdviseDAO.deletePayableReserve(receiptSeqID, rd.getPayAgainstID());

						// Update Reserve Amount in Manual Advise
						manualAdviseDAO.updatePayableReserve(rd.getPayAgainstID(),
								payableReserve.getReservedAmt().negate());

					} else {

						if (payableAmt.compareTo(payableReserve.getReservedAmt()) != 0) {
							BigDecimal diffInReserve = payableAmt.subtract(payableReserve.getReservedAmt());

							// Update Reserve Amount in Manual Advise
							manualAdviseDAO.updatePayableReserve(rd.getPayAgainstID(), diffInReserve);

							// Update Payable Reserve Log
							manualAdviseDAO.updatePayableReserveLog(receiptSeqID, rd.getPayAgainstID(), diffInReserve);
						}
					}
				}
			}

			// Manual Advise Movements
			if (CollectionUtils.isNotEmpty(rd.getAdvMovements())) {
				for (ManualAdviseMovements movement : rd.getAdvMovements()) {
					if (movement.getTaxHeader() != null
							&& CollectionUtils.isNotEmpty(movement.getTaxHeader().getTaxDetails())) {
						List<Taxes> taxDetails = movement.getTaxHeader().getTaxDetails();
						long headerId = getTaxHeaderDetailsDAO().save(movement.getTaxHeader(),
								TableType.TEMP_TAB.getSuffix());
						for (Taxes taxes : taxDetails) {
							taxes.setReferenceId(headerId);
						}
						getTaxHeaderDetailsDAO().saveTaxes(taxDetails, TableType.TEMP_TAB.getSuffix());
						movement.setTaxHeaderId(headerId);
					}
					movement.setReceiptID(receiptID);
					movement.setReceiptSeqID(receiptSeqID);
					manualAdviseDAO.saveMovement(movement, TableType.TEMP_TAB.getSuffix());
				}
			}
		}

		// Receipt Allocation Details
		for (int i = 0; i < receiptHeader.getAllocations().size(); i++) {
			ReceiptAllocationDetail allocation = receiptHeader.getAllocations().get(i);
			allocation.setReceiptID(receiptID);
			allocation.setAllocationID(i + 1);
		}

		if (CollectionUtils.isNotEmpty(receiptHeader.getAllocations())) {
			for (ReceiptAllocationDetail allocation : receiptHeader.getAllocations()) {
				if (StringUtils.isNotBlank(allocation.getTaxType())) {
					if (allocation.getTaxHeader() != null
							&& CollectionUtils.isNotEmpty(allocation.getTaxHeader().getTaxDetails())) {

						TaxHeader taxHeader = allocation.getTaxHeader();
						taxHeader.setLastMntBy(receiptHeader.getLastMntBy());
						taxHeader.setLastMntOn(receiptHeader.getLastMntOn());
						taxHeader.setRecordStatus(recordStatus);
						taxHeader.setRecordType(receiptHeader.getRecordType());
						taxHeader.setVersion(receiptHeader.getVersion());
						taxHeader.setWorkflowId(receiptHeader.getWorkflowId());
						taxHeader.setTaskId(receiptHeader.getTaskId());
						taxHeader.setNextTaskId(receiptHeader.getNextTaskId());
						taxHeader.setRoleCode(receiptHeader.getRoleCode());
						taxHeader.setNextRoleCode(receiptHeader.getNextRoleCode());
						Long headerId = getTaxHeaderDetailsDAO().save(allocation.getTaxHeader(), tableType.getSuffix());

						List<Taxes> taxDetails = taxHeader.getTaxDetails();
						for (Taxes taxes : taxDetails) {
							taxes.setReferenceId(headerId);
						}
						getTaxHeaderDetailsDAO().saveTaxes(taxDetails, tableType.getSuffix());
						allocation.setTaxHeaderId(headerId);
					}
				}
			}
		}
		allocationDetailDAO.saveAllocations(receiptHeader.getAllocations(), tableType);

		if (FinanceConstants.DEPOSIT_APPROVER.equals(receiptHeader.getRoleCode())
				&& !(PennantConstants.RCD_STATUS_SAVED.equals(recordStatus)
						|| PennantConstants.RCD_STATUS_RESUBMITTED.equals(recordStatus))) {
			executeAccounting(rceiptData);
		}

		// Finance Fee Details
		// =======================================
		if (rceiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList() != null
				&& !rceiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList().isEmpty()) {
			saveOrUpdateFees(rceiptData, tableType.getSuffix());
		}

		for (int i = 0; i < receiptHeader.getAllocations().size(); i++) {
			ReceiptAllocationDetail allocation = receiptHeader.getAllocations().get(i);
			if (StringUtils.equals(RepayConstants.ALLOCATION_FEE, allocation.getAllocationType())) {
				for (FinFeeDetail feeDtl : rceiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList()) {
					if (feeDtl.getFeeTypeID() == -(allocation.getAllocationTo())) {
						allocation.setAllocationTo(feeDtl.getFeeID());
						break;
					}
				}
			}
		}

		FinReceiptHeader befRctHeader = new FinReceiptHeader();
		if (receiptHeader.isNewRecord()) {
			BeanUtils.copyProperties(receiptHeader, befRctHeader);
		} else {
			befRctHeader = getFinReceiptHeaderById(receiptHeader.getReceiptID(), false, "_View");
		}
		// FinReceiptDetail Audit Details Preparation
		FinReceiptDetail adtFinReceiptDetail = new FinReceiptDetail();
		String[] rhFields = PennantJavaUtil.getFieldDetails(adtFinReceiptDetail,
				adtFinReceiptDetail.getExcludeFields());
		for (int i = 0; i < receiptHeader.getReceiptDetails().size(); i++) {
			if (CollectionUtils.isNotEmpty(befRctHeader.getReceiptDetails())) {
				auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), 1, rhFields[0], rhFields[1],
						befRctHeader.getReceiptDetails().get(i), receiptHeader.getReceiptDetails().get(i)));
			}
		}

		// Save Document Details

		// set Finance Check List audit details to auditDetails
		// =======================================
		if (rceiptData.getFinanceDetail().getFinanceCheckList() != null
				&& !rceiptData.getFinanceDetail().getFinanceCheckList().isEmpty()) {
			auditDetails.addAll(checkListDetailService.saveOrUpdate(rceiptData.getFinanceDetail(),
					tableType.getSuffix(), serviceUID));
		}

		// Extended field Details
		if (rceiptData.getFinanceDetail().getExtendedFieldRender() != null) {
			List<AuditDetail> details = rceiptData.getFinanceDetail().getAuditDetailMap().get("ExtendedFieldDetails");
			details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
					rceiptData.getFinanceDetail().getExtendedFieldHeader(), tableType.getSuffix(), serviceUID);
			auditDetails.addAll(details);
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), receiptHeader.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				receiptHeader.getBefImage(), receiptHeader));

		auditHeader.setAuditDetails(auditDetails);
		auditHeader.setAuditModule("Receipt");
		getAuditHeaderDAO().addAudit(auditHeader);
		auditHeader.getAuditDetail().setModelData(rceiptData);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Delete Tax Details
	 */
	public void deleteTaxHeaderId(long receiptId, String type) {
		logger.debug(Literal.ENTERING);

		List<Long> headerIdsByReceiptId = getTaxHeaderDetailsDAO().getHeaderIdsByReceiptId(receiptId, type);
		if (CollectionUtils.isNotEmpty(headerIdsByReceiptId)) {
			for (Long headerIds : headerIdsByReceiptId) {
				if (headerIds != null && headerIds > 0) {
					getTaxHeaderDetailsDAO().delete(headerIds, type);
					TaxHeader taxHeader = new TaxHeader();
					taxHeader.setHeaderId(headerIds);
					getTaxHeaderDetailsDAO().delete(taxHeader, type);
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public long executeAccounting(FinReceiptData receiptData) {
		logger.debug(Literal.ENTERING);
		FinanceMain financeMain = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();

		AEEvent aeEvent = new AEEvent();
		aeEvent.setEntityCode(financeMain.getEntityCode());
		aeEvent.setPostingUserBranch(receiptData.getReceiptHeader().getCashierBranch());
		aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_RECIP);
		aeEvent.setFinReference(receiptData.getFinReference());
		aeEvent.setValueDate(SysParamUtil.getAppDate());

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
		}
		Map<String, Object> eventMapping = null;
		String btLoan = "";
		receiptData.getReceiptHeader().getPartnerBankCode();
		// FinanceMain financeMain =
		// financeMainDAO.getFinanceMainForBatch(receiptD.getFinReference());
		amountCodes.setFinType(financeMain.getFinType());
		aeEvent.setBranch(financeMain.getFinBranch());
		aeEvent.setCcy(financeMain.getFinCcy());
		aeEvent.setCustID(financeMain.getCustID());
		amountCodes.setBusinessvertical(financeMain.getBusinessVerticalCode());
		amountCodes.setAlwflexi(financeMain.isAlwFlexi());
		amountCodes.setFinbranch(financeMain.getFinBranch());
		amountCodes.setEntitycode(financeMain.getEntityCode());
		amountCodes.setPartnerBankAc(receiptData.getReceiptHeader().getReceiptDetails().get(0).getPartnerBankAc());
		btLoan = financeMain.getLoanCategory();
		eventMapping = getFinanceMainDAO().getGLSubHeadCodes(financeMain.getFinReference());

		Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();
		if (eventMapping != null) {
			// dataMap = aeEvent.getDataMap();
			dataMap.put("emptype", eventMapping.get("EMPTYPE"));
			dataMap.put("branchcity", eventMapping.get("BRANCHCITY"));
			dataMap.put("fincollateralreq", eventMapping.get("FINCOLLATERALREQ"));
			dataMap.put("btloan", btLoan);
		}
		receiptData.getReceiptHeader().getReceiptDetails().get(0).getDeclaredFieldValues(aeEvent.getDataMap());
		aeEvent.setDataMap(dataMap);

		BigDecimal amount = BigDecimal.ZERO;
		for (FinReceiptDetail detail : receiptData.getReceiptHeader().getReceiptDetails()) {
			if (!(detail.getPaymentType().equals(RepayConstants.RECEIPTMODE_EMIINADV)
					|| detail.getPaymentType().equals(RepayConstants.RECEIPTMODE_EXCESS)
					|| detail.getPaymentType().equals(RepayConstants.RECEIPTMODE_PAYABLE))) {
				amount = amount.add(detail.getAmount());
			}
		}

		aeEvent.getDataMap().put("rd_amount", amount);

		long accountsetId = accountingSetDAO.getAccountingSetId(AccountEventConstants.ACCEVENT_RECIP,
				AccountEventConstants.ACCEVENT_RECIP);
		aeEvent.getAcSetIDList().add(accountsetId);
		aeEvent = getPostingsPreparationUtil().postAccounting(aeEvent);
		logger.debug(Literal.LEAVING);
		return aeEvent.getLinkedTranId();
	}

	public void saveOrUpdateFees(FinReceiptData receiptData, String tableType) {
		logger.debug("Entering ");

		List<FinFeeDetail> feeDetailsList = receiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList();
		String finReference = receiptData.getFinanceDetail().getFinScheduleData().getFinReference();
		boolean newRecord = receiptData.getReceiptHeader().isNew();

		List<FinFeeDetail> oldFeedetails = receiptData.getFinFeeDetails();
		if (CollectionUtils.isNotEmpty(oldFeedetails)) {
			for (FinFeeDetail finFeeDetail : oldFeedetails) {
				finFeeScheduleDetailDAO.deleteFeeScheduleBatch(finFeeDetail.getFeeID(), false, "_Temp");
				finFeeDetail.setFinReference(finReference);
				finFeeDetailDAO.delete(finFeeDetail, false, "_Temp");
				TaxHeader taxHeader = finFeeDetail.getTaxHeader();

				if (taxHeader != null && taxHeader.getId() > 0) {
					List<Taxes> taxDetails = taxHeader.getTaxDetails();
					if (CollectionUtils.isNotEmpty(taxDetails)) {
						getTaxHeaderDetailsDAO().delete(taxHeader.getId(), tableType);
						getTaxHeaderDetailsDAO().delete(taxHeader, tableType);
					}
				}
			}
		}

		if (CollectionUtils.isNotEmpty(feeDetailsList)) {

			long receiptID = receiptData.getReceiptHeader().getReceiptID();

			for (FinFeeDetail finFeeDetail : feeDetailsList) {
				finFeeDetail.setFinReference(finReference);
				TaxHeader taxHeader = finFeeDetail.getTaxHeader();

				if (!newRecord && finFeeDetail.isOriginationFee() && finFeeDetail.getFeeID() > 0) {
					finFeeDetailDAO.update(finFeeDetail, false, tableType);

					if (taxHeader != null) {
						List<Taxes> taxDetails = taxHeader.getTaxDetails();
						if (CollectionUtils.isNotEmpty(taxDetails)) {
							for (Taxes taxes : taxDetails) {
								getTaxHeaderDetailsDAO().update(taxes, tableType);
							}
						}
					}
				} else {
					if (!finFeeDetail.isOriginationFee()) {
						finFeeDetail.setFeeSeq(finFeeDetailDAO.getFeeSeq(finFeeDetail, false, tableType) + 1);
						finFeeDetail.setReferenceId(receiptID);
					}
					if (taxHeader != null) {
						List<Taxes> taxDetails = taxHeader.getTaxDetails();
						if (CollectionUtils.isNotEmpty(taxDetails)) {
							long headerId = getTaxHeaderDetailsDAO().save(taxHeader, tableType);
							for (Taxes taxes : taxDetails) {
								taxes.setReferenceId(headerId);
							}
							getTaxHeaderDetailsDAO().saveTaxes(taxDetails, tableType);
							finFeeDetail.setTaxHeaderId(headerId);
						}
					}
					finFeeDetail.setFeeID(finFeeDetailDAO.save(finFeeDetail, false, tableType));
				}

				if (CollectionUtils.isNotEmpty(finFeeDetail.getFinFeeScheduleDetailList())) {
					for (FinFeeScheduleDetail finFeeSchDetail : finFeeDetail.getFinFeeScheduleDetailList()) {
						finFeeSchDetail.setFeeID(finFeeDetail.getFeeID());
					}
					finFeeScheduleDetailDAO.saveFeeScheduleBatch(finFeeDetail.getFinFeeScheduleDetailList(), false,
							tableType);
				}
			}
		}

		logger.debug("Leaving");
	}

	public void approveFees(FinReceiptData receiptData, String tableType) {
		logger.debug("Entering ");

		List<FinFeeDetail> feeDetailsList = receiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList();
		String finReference = receiptData.getFinReference();

		List<FinFeeDetail> oldFeedetails = receiptData.getFinFeeDetails();
		if (CollectionUtils.isNotEmpty(oldFeedetails)) {
			for (FinFeeDetail finFeeDetail : oldFeedetails) {
				if (CollectionUtils.isNotEmpty(finFeeDetail.getFinFeeScheduleDetailList())) {
					finFeeScheduleDetailDAO.deleteFeeScheduleBatch(finFeeDetail.getFeeID(), false, "_Temp");
				}
				finFeeDetailDAO.delete(finFeeDetail, false, "_Temp");
				TaxHeader taxHeader = finFeeDetail.getTaxHeader();
				Long taxHeaderId = finFeeDetail.getTaxHeaderId();
				if (taxHeaderId != null && taxHeaderId > 0) {
					if (taxHeader == null) {
						taxHeader = new TaxHeader();
						taxHeader.setHeaderId(taxHeaderId);
					}
					getTaxHeaderDetailsDAO().delete(taxHeaderId, "_Temp");
					getTaxHeaderDetailsDAO().delete(taxHeader, "_Temp");
				}
			}
		}

		long receiptID = receiptData.getReceiptHeader().getReceiptID();

		for (FinFeeDetail finFeeDetail : feeDetailsList) {

			if (StringUtils.equals(finFeeDetail.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
				continue;
			}
			finFeeDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			finFeeDetail.setRecordType("");
			finFeeDetail.setRoleCode("");
			finFeeDetail.setNextRoleCode("");
			finFeeDetail.setTaskId("");
			finFeeDetail.setNextTaskId("");
			finFeeDetail.setWorkflowId(0);
			finFeeDetail.setFinReference(finReference);

			TaxHeader taxHeader = finFeeDetail.getTaxHeader();
			if (finFeeDetail.isOriginationFee()) {
				finFeeDetailDAO.update(finFeeDetail, false, tableType);
				if (taxHeader != null) {
					List<Taxes> taxDetails = taxHeader.getTaxDetails();
					if (CollectionUtils.isNotEmpty(taxDetails)) {
						for (Taxes taxes : taxDetails) {
							getTaxHeaderDetailsDAO().update(taxes, tableType);
						}
					}
				}
			} else {
				finFeeDetail.setReferenceId(receiptID);
				if (taxHeader != null) {
					List<Taxes> taxDetails = taxHeader.getTaxDetails();
					if (CollectionUtils.isNotEmpty(taxDetails)) {
						long headerId = getTaxHeaderDetailsDAO().save(taxHeader, tableType);
						for (Taxes taxes : taxDetails) {
							taxes.setReferenceId(headerId);
						}
						getTaxHeaderDetailsDAO().saveTaxes(taxDetails, tableType);
						finFeeDetail.setTaxHeaderId(headerId);
					}
				}
				finFeeDetailDAO.save(finFeeDetail, false, tableType);
			}

			List<FinFeeScheduleDetail> finFeeScheduleDetailList = finFeeDetail.getFinFeeScheduleDetailList();
			if (CollectionUtils.isNotEmpty(finFeeScheduleDetailList)) {
				for (FinFeeScheduleDetail finFeeSchDetail : finFeeScheduleDetailList) {
					finFeeSchDetail.setFeeID(finFeeDetail.getFeeID());
				}
				finFeeScheduleDetailDAO.saveFeeScheduleBatch(finFeeScheduleDetailList, false, tableType);
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Method to delete schedule, disbursement, deferment header, deferment detail,repay instruction, rate changes
	 * lists.
	 * 
	 * @param finDetail
	 * @param tableType
	 * @param isWIF
	 */
	public void listDeletion(String finReference, String tableType) {
		logger.debug("Entering ");
		financeScheduleDetailDAO.deleteByFinReference(finReference, tableType, false, 0);
		repayInstructionDAO.deleteByFinReference(finReference, tableType, false, 0);
		logger.debug("Leaving ");
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using finReceiptHeaderDAO.delete with parameters financeMain,TableType.TEMP_TAB.getSuffix() 3)
	 * Audit the record in to AuditHeader and AdtFinanceMain by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
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

		FinReceiptData receiptData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		rch.setRcdMaintainSts(null);
		FinScheduleData scheduleData = receiptData.getFinanceDetail().getFinScheduleData();
		FinanceMain financeMain = scheduleData.getFinanceMain();
		List<AuditDetail> auditDetails = new ArrayList<>();

		long serviceUID = Long.MIN_VALUE;
		for (FinServiceInstruction finServInst : receiptData.getFinanceDetail().getFinScheduleData()
				.getFinServiceInstructions()) {
			serviceUID = finServInst.getInstructionUID();
		}

		// Cancel All Transactions done by Finance Reference
		// =======================================
		cancelStageAccounting(financeMain.getFinReference(), receiptData.getReceiptHeader().getReceiptPurpose());

		// ScheduleDetails deletion
		// listDeletion(financeMain.getFinReference(),
		// TableType.TEMP_TAB.getSuffix());
		// financeMainDAO.delete(financeMain, TableType.TEMP_TAB, false, false);

		// Delete and Save Repayment Schedule details by setting Repay Header ID
		// financeRepaymentsDAO.deleteRpySchdList(financeMain.getFinReference(),
		// TableType.TEMP_TAB.getSuffix());

		// Delete and Save FinRepayHeader Detail list by Reference
		financeRepaymentsDAO.deleteByReceiptId(rch.getReceiptID(), TableType.TEMP_TAB);

		for (FinReceiptDetail receiptDetail : receiptData.getReceiptHeader().getReceiptDetails()) {
			long receiptSeqID = receiptDetail.getReceiptSeqID();

			// Excess Amount Reserve
			if (StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_EXCESS)
					|| StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_EMIINADV)
					|| StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_ADVINT)) {

				// Excess Amount make utilization
				FinExcessAmountReserve exReserve = finExcessAmountDAO.getExcessReserve(receiptSeqID,
						receiptDetail.getPayAgainstID());
				if (exReserve != null) {

					// Update Reserve Amount in FinExcessAmount
					finExcessAmountDAO.updateExcessReserve(receiptDetail.getPayAgainstID(),
							exReserve.getReservedAmt().negate());

					// Delete Reserved Log against Excess and Receipt ID
					finExcessAmountDAO.deleteExcessReserve(receiptSeqID, receiptDetail.getPayAgainstID(),
							RepayConstants.RECEIPTTYPE_RECIPT);
				}
			}

			// Payable Amount Reserve
			if (StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.RECEIPTMODE_PAYABLE)) {

				// Payable Amount make utilization
				ManualAdviseReserve payableReserve = manualAdviseDAO.getPayableReserve(receiptSeqID,
						receiptDetail.getPayAgainstID());
				if (payableReserve != null) {

					// Update Reserve Amount in ManualAdvise
					manualAdviseDAO.updatePayableReserve(receiptDetail.getPayAgainstID(),
							payableReserve.getReservedAmt().negate());

					// Delete Reserved Log against Payable Advise ID and Receipt
					// ID
					manualAdviseDAO.deletePayableReserve(receiptSeqID, receiptDetail.getPayAgainstID());

				}
			}
		}

		// Delete Save Receipt Detail List by Reference
		finReceiptDetailDAO.deleteByReceiptID(receiptData.getReceiptHeader().getReceiptID(), TableType.TEMP_TAB);

		deleteTaxHeaderId(receiptData.getReceiptHeader().getReceiptID(), TableType.TEMP_TAB.getSuffix());

		// Receipt Allocation Details
		allocationDetailDAO.deleteByReceiptID(receiptData.getReceiptHeader().getReceiptID(), TableType.TEMP_TAB);

		// Delete Manual Advise Movements
		manualAdviseDAO.deleteMovementsByReceiptID(receiptData.getReceiptHeader().getReceiptID(),
				TableType.TEMP_TAB.getSuffix());

		// Delete Receipt Header
		finReceiptHeaderDAO.deleteByReceiptID(receiptData.getReceiptHeader().getReceiptID(), TableType.TEMP_TAB);

		// FinReceiptDetail Audit Details Preparation
		String[] rFields = PennantJavaUtil.getFieldDetails(new FinReceiptDetail(),
				receiptData.getReceiptHeader().getReceiptDetails().get(0).getExcludeFields());
		for (int i = 0; i < receiptData.getReceiptHeader().getReceiptDetails().size(); i++) {
			auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), 1, rFields[0], rFields[1],
					receiptData.getReceiptHeader().getReceiptDetails().get(i),
					receiptData.getReceiptHeader().getReceiptDetails().get(i)));
		}

		// Receipt Header Audit Details Preparation
		String[] rhFields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(),
				receiptData.getReceiptHeader().getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(tranType, 1, rhFields[0], rhFields[1],
				receiptData.getReceiptHeader().getBefImage(), receiptData.getReceiptHeader()));

		// Delete Fee Details
		List<FinFeeDetail> oldFeedetails = receiptData.getFinFeeDetails();
		if (CollectionUtils.isNotEmpty(oldFeedetails)) {
			for (FinFeeDetail finFeeDetail : oldFeedetails) {
				finFeeScheduleDetailDAO.deleteFeeScheduleBatch(finFeeDetail.getFeeID(), false, "_Temp");
				finFeeDetailDAO.delete(finFeeDetail, false, "_Temp");
			}
		}

		// Delete Document Details
		if (receiptData.getFinanceDetail().getDocumentDetailsList() != null
				&& receiptData.getFinanceDetail().getDocumentDetailsList().size() > 0) {
			for (DocumentDetails docDetails : receiptData.getFinanceDetail().getDocumentDetailsList()) {
				docDetails.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
			List<AuditDetail> details = receiptData.getFinanceDetail().getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, TableType.TEMP_TAB.getSuffix(),
					receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain(),
					receiptData.getReceiptHeader().getReceiptPurpose(), serviceUID);
			auditDetails.addAll(details);
		}

		// Checklist Details delete
		// =======================================
		auditDetails.addAll(checkListDetailService.delete(receiptData.getFinanceDetail(),
				TableType.TEMP_TAB.getSuffix(), tranType));

		// Delete Extended field Render Details.
		List<AuditDetail> extendedDetails = receiptData.getFinanceDetail().getAuditDetailMap()
				.get("ExtendedFieldDetails");
		if (extendedDetails != null && extendedDetails.size() > 0) {
			auditDetails
					.addAll(extendedFieldDetailsService.delete(receiptData.getFinanceDetail().getExtendedFieldHeader(),
							financeMain.getFinReference(), "_Temp", auditHeader.getAuditTranType(), extendedDetails));
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.getAuditDetail().setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditModule("Receipt");
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		// Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(receiptData);

		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using finReceiptHeaderDAO.delete with
	 * parameters financeMain,"" b) NEW Add new record in to main table by using finReceiptHeaderDAO.save with
	 * parameters financeMain,"" c) EDIT Update record in the main table by using finReceiptHeaderDAO.update with
	 * parameters financeMain,"" 3) Delete the record from the workFlow table by using finReceiptHeaderDAO.delete with
	 * parameters financeMain,"_Temp" 4) Audit the record in to AuditHeader and AdtFinanceMain by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtFinanceMain by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws Exception
	 * @throws AccountNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader) throws Exception {
		logger.debug("Entering");

		FinReceiptData orgReceiptData = (FinReceiptData) aAuditHeader.getAuditDetail().getModelData();
		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		FinReceiptData receiptData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		String roleCode = receiptData.getReceiptHeader().getRoleCode();
		String nextRoleCode = receiptData.getReceiptHeader().getNextRoleCode();
		boolean isLoanActiveBef = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().isFinIsActive();
		Date appDate = SysParamUtil.getAppDate();

		boolean isGoldLoanProc = false;
		if (StringUtils.equals(
				receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().getProductCategory(),
				FinanceConstants.PRODUCT_GOLD)) {
			isGoldLoanProc = true;
		}

		if (financeScheduleDetailDAO.isScheduleInQueue(orgReceiptData.getFinReference())) {
			throw new AppException("Not allowed to approve the receipt, since the loan schedule under maintenance.");
		}

		// Preparing Before Image for Audit
		FinReceiptHeader befRctHeader = null;
		if (!PennantConstants.RECORD_TYPE_NEW.equals(orgReceiptData.getReceiptHeader().getRecordType())) {
			befRctHeader = getFinReceiptHeaderById(receiptData.getReceiptHeader().getReceiptID(), false, "");
		}
		List<FinReceiptDetail> befFinReceiptDetail = new ArrayList<>();
		if (befRctHeader != null) {
			befFinReceiptDetail = befRctHeader.getReceiptDetails();
		}
		// Setting Before Image to Audit Header
		aAuditHeader.getAuditDetail().setBefImage(befRctHeader);

		if (StringUtils.equals(receiptData.getReceiptHeader().getReceiptModeStatus(), RepayConstants.PAYSTATUS_BOUNCE)
				|| StringUtils.equals(receiptData.getReceiptHeader().getReceiptModeStatus(),
						RepayConstants.PAYSTATUS_CANCEL)) {

			FinReceiptData recData = (FinReceiptData) aAuditHeader.getAuditDetail().getModelData();
			FinReceiptHeader rch = recData.getReceiptHeader();

			for (FinReceiptDetail rcd : rch.getReceiptDetails()) {
				FinRepayHeader rph = financeRepaymentsDAO.getFinRepayHeadersByReceipt(rcd.getReceiptSeqID(), "");
				rcd.setRepayHeader(rph);
				if (rph != null) {
					List<RepayScheduleDetail> rpySchdList = financeRepaymentsDAO.getRpySchdList(rph.getRepayID(), "");
					rph.setRepayScheduleDetails(rpySchdList);
				}
			}
			receiptCancellationService.doApprove(aAuditHeader);
			aAuditHeader.getAuditDetail().setModelData(receiptData);
			return aAuditHeader;
		}
		if (StringUtils.equals(FinanceConstants.REALIZATION_APPROVER, roleCode)) {
			if ((StringUtils.equals(receiptData.getReceiptHeader().getReceiptPurpose(),
					FinanceConstants.FINSER_EVENT_SCHDRPY)
					&& StringUtils.isEmpty(receiptData.getReceiptHeader().getPrvReceiptPurpose()))
					&& (StringUtils.equals(receiptData.getReceiptHeader().getReceiptMode(),
							RepayConstants.RECEIPTMODE_CHEQUE)
							|| StringUtils.equals(receiptData.getReceiptHeader().getReceiptMode(),
									RepayConstants.RECEIPTMODE_DD))) {
				receiptRealizationService.doApprove(aAuditHeader);
				aAuditHeader.getAuditDetail().setModelData(receiptData);
				return aAuditHeader;
			}
		}
		//schedule pay effect on cheque/dd realization(if N schedule will effect while approve/if Y schedule will effect while realization)
		if (!SysParamUtil.isAllowed(SMTParameterConstants.CHEQUE_MODE_SCHDPAY_EFFT_ON_REALIZATION)
				&& SysParamUtil.isAllowed(SMTParameterConstants.CHQ_RECEIPTS_PAID_AT_DEPOSIT_APPROVER)) {
			if (StringUtils.equals(FinanceConstants.REALIZATION_APPROVER, roleCode)) {
				if (((StringUtils.equals(receiptData.getReceiptHeader().getReceiptPurpose(),
						FinanceConstants.FINSER_EVENT_SCHDRPY)
						&& StringUtils.isEmpty(receiptData.getReceiptHeader().getPrvReceiptPurpose())))
						&& (StringUtils.equals(receiptData.getReceiptHeader().getReceiptMode(),
								RepayConstants.RECEIPTMODE_CHEQUE)
								|| StringUtils.equals(receiptData.getReceiptHeader().getReceiptMode(),
										RepayConstants.RECEIPTMODE_DD))) {
					receiptRealizationService.doApprove(aAuditHeader);
					aAuditHeader.getAuditDetail().setModelData(receiptData);
					return aAuditHeader;
				}
			}
		}

		FinReceiptHeader receiptHeader = receiptData.getReceiptHeader();
		if (receiptData.getFinanceDetail().getFinScheduleData().getFinServiceInstructions().isEmpty()) {
			FinServiceInstruction finServInst = new FinServiceInstruction();
			finServInst.setFinReference(receiptHeader.getReference());
			finServInst.setFinEvent(receiptData.getFinanceDetail().getModuleDefiner());
			receiptData.getFinanceDetail().getFinScheduleData().setFinServiceInstruction(finServInst);
		}

		if (!StringUtils.equalsIgnoreCase(PennantConstants.FINSOURCE_ID_API, receiptData.getSourceId())
				&& !receiptData.getFinanceDetail().getFinScheduleData().getFinServiceInstruction().isReceiptUpload()) {
			if ((StringUtils.equals(receiptData.getReceiptHeader().getReceiptPurpose(),
					FinanceConstants.FINSER_EVENT_EARLYRPY)
					|| StringUtils.equals(receiptData.getReceiptHeader().getReceiptPurpose(),
							FinanceConstants.FINSER_EVENT_EARLYSETTLE))) {

				aAuditHeader = approveValidation(aAuditHeader, "doApprove");
			}
		}

		aAuditHeader = businessValidation(aAuditHeader, "doApprove");
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		long serviceUID = Long.MIN_VALUE;
		for (FinServiceInstruction finSerList : receiptData.getFinanceDetail().getFinScheduleData()
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

		// Finance Stage Accounting Process
		// =======================================
		auditHeader = executeStageAccounting(auditHeader);
		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}

		// Repayments Postings Details Process Execution
		FinanceProfitDetail profitDetail = receiptData.getFinanceDetail().getFinScheduleData().getFinPftDeatil();

		// Execute Accounting Details Process
		// =======================================
		if (receiptData.getReceiptHeader().getReceiptID() > 0
				&& StringUtils.isEmpty(receiptData.getReceiptHeader().getPrvReceiptPurpose())) {
			receiptData = recalculateReceipt(receiptData);
			if (receiptData.getErrorDetails().size() > 0) {
				auditHeader.setErrorList(receiptData.getErrorDetails());
				return auditHeader;
			}
		}

		if (!isGoldLoanProc && !receiptData.isDueAdjusted()
				&& !StringUtils.equalsIgnoreCase(receiptData.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
			auditHeader.getAuditDetail().setErrorDetail(
					ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "FC0001", null, null)));
			auditHeader.setErrorList(auditHeader.getAuditDetail().getErrorDetails());
			auditHeader = nextProcess(auditHeader);
			if (!auditHeader.isNextProcess()) {
				return auditHeader;
			}
		}

		receiptData.getReceiptHeader().setDepositProcess(orgReceiptData.getReceiptHeader().isDepositProcess());

		FinScheduleData scheduleData = receiptData.getFinanceDetail().getFinScheduleData();
		FinanceMain financeMain = scheduleData.getFinanceMain();
		String finReference = financeMain.getFinReference();

		List<FinanceScheduleDetail> finSchdDtls = cloner.deepClone(scheduleData.getFinanceScheduleDetails());
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		rch.setRoleCode(roleCode);
		rch.setNextRoleCode(nextRoleCode);
		rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_REALIZED);
		if (StringUtils.equals(FinanceConstants.DEPOSIT_APPROVER, rch.getRoleCode()) || receiptData.isPresentment()) {
			rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_DEPOSITED);
		}

		if (!StringUtils.equals(RepayConstants.RECEIPTMODE_CHEQUE, rch.getReceiptMode())) {
			rch.setRealizationDate(rch.getValueDate());
			//rch.setReceivedDate(rch.getValueDate());
			rch.setReceivedDate(rch.getReceiptDate());
		}

		finReceiptHeaderDAO.generatedReceiptID(rch);
		rch.setPostBranch(auditHeader.getAuditBranchCode());
		// rch.setCashierBranch(auditHeader.getAuditBranchCode());
		rch.setReceiptDate(appDate);
		rch.setRcdMaintainSts(null);
		rch.setRoleCode("");
		rch.setNextRoleCode("");
		rch.setTaskId("");
		rch.setNextTaskId("");
		rch.setWorkflowId(0);
		rch.setActFinReceipt(financeMain.isFinIsActive());
		rch.setValueDate(receiptData.getValueDate());
		rch.setReceiptDate(appDate);

		if (rch.getReceiptMode() != null && rch.getSubReceiptMode() == null) {
			rch.setSubReceiptMode(rch.getReceiptMode());
		}

		// Resetting Maturity Terms & Summary details rendering in case of
		// Reduce maturity cases
		if (receiptData.isDueAdjusted()) {
			scheduleData.setFinanceScheduleDetails(finSchdDtls);
		} else {
			scheduleData.setFinanceScheduleDetails(
					financeScheduleDetailDAO.getFinScheduleDetails(rch.getReference(), "", false));
		}

		if (!StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
			if (!financeMain.isSanBsdSchdle()) {
				int size = scheduleData.getFinanceScheduleDetails().size();
				for (int i = size - 1; i >= 0; i--) {
					FinanceScheduleDetail curSchd = scheduleData.getFinanceScheduleDetails().get(i);
					if (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0
							&& curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0) {
						financeMain.setMaturityDate(curSchd.getSchDate());
						break;
					} else if (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0
							&& curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0) {
						scheduleData.getFinanceScheduleDetails().remove(i);
					}
				}
			}
		}

		Date curBusDate = appDate;
		Date valueDate = rch.getValueDate();

		// Repayments Posting Process Execution
		// =====================================
		List<FinanceScheduleDetail> schdList = scheduleData.getFinanceScheduleDetails();
		profitDetail.setLpiAmount(rch.getLpiAmount());
		profitDetail.setGstLpiAmount(rch.getGstLpiAmount());
		profitDetail.setLppAmount(rch.getLppAmount());
		profitDetail.setGstLppAmount(rch.getGstLppAmount());

		// get Total SubVention Amount
		if (financeMain.isAllowSubvention()) {
			BigDecimal subVentionAmt = this.subventionDetailDAO.getTotalSubVentionAmt(finReference);
			profitDetail.setTotalSvnAmount(subVentionAmt);
		}

		if (rch.getReceiptDetails() != null && !rch.getReceiptDetails().isEmpty()) {
			for (int i = 0; i < rch.getReceiptDetails().size(); i++) {
				FinReceiptDetail receiptDetail = rch.getReceiptDetails().get(i);
				receiptDetail.getRepayHeader().setRepayID(financeRepaymentsDAO.getNewRepayID());
			}
		}
		// Postings Process
		List<Object> returnList = repayProcessUtil.doProcessReceipts(financeMain, schdList, profitDetail, rch,
				scheduleData.getFinFeeDetailList(), scheduleData, valueDate, curBusDate,
				receiptData.getFinanceDetail());
		schdList = (List<FinanceScheduleDetail>) returnList.get(0);

		// Preparing Total Principal Amount
		BigDecimal totPriPaid = BigDecimal.ZERO;
		for (FinReceiptDetail receiptDetail : rch.getReceiptDetails()) {
			FinRepayHeader repayHeader = receiptDetail.getRepayHeader();
			if (repayHeader.getRepayScheduleDetails() != null && !repayHeader.getRepayScheduleDetails().isEmpty()) {
				for (RepayScheduleDetail rpySchd : repayHeader.getRepayScheduleDetails()) {
					totPriPaid = totPriPaid.add(rpySchd.getPrincipalSchdPayNow().add(rpySchd.getPriSchdWaivedNow()));
				}
			}
		}

		financeMain.setFinRepaymentAmount(financeMain.getFinRepaymentAmount().add(totPriPaid));

		// UnRealized Income Amount Resetting
		profitDetail.setAmzTillLBD(profitDetail.getAmzTillLBD().add((BigDecimal) returnList.get(1)));
		profitDetail.setLpiTillLBD(profitDetail.getLpiTillLBD().add((BigDecimal) returnList.get(2)));
		profitDetail.setGstLpiTillLBD(profitDetail.getGstLpiTillLBD().add((BigDecimal) returnList.get(3)));
		profitDetail.setLppTillLBD(profitDetail.getLppTillLBD().add((BigDecimal) returnList.get(4)));
		profitDetail.setGstLppTillLBD(profitDetail.getGstLppTillLBD().add((BigDecimal) returnList.get(5)));

		if (schdList == null) {
			schdList = scheduleData.getFinanceScheduleDetails();
		}

		// Overdue Details updation , if Value Date is Back dated.
		scheduleData.setFinanceScheduleDetails(schdList);
		Date reqMaxODDate = curBusDate;
		List<FinODDetails> overdueList = null;

		if (StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE, rch.getReceiptPurpose())) {
			reqMaxODDate = rch.getValueDate();
		}

		if (!ImplementationConstants.LPP_CALC_SOD) {
			reqMaxODDate = DateUtility.addDays(valueDate, -1);
		}
		overdueList = finODDetailsDAO.getFinODBalByFinRef(financeMain.getFinReference());

		overdueList = calPenalty(scheduleData, receiptData, reqMaxODDate, overdueList);
		if (overdueList != null && !overdueList.isEmpty()) {
			finODDetailsDAO.updateList(overdueList);
		}

		tranType = PennantConstants.TRAN_UPD;
		rch.setRecordType("");

		// Save Receipt Header

		rch.setRcdMaintainSts(null);
		rch.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		rch.setRecordType("");
		rch.setRoleCode("");
		rch.setNextRoleCode("");
		rch.setTaskId("");
		rch.setNextTaskId("");
		rch.setWorkflowId(0);

		// save Receipt Details
		repayProcessUtil.doSaveReceipts(rch, scheduleData.getFinFeeDetailList(), true);
		long receiptID = rch.getReceiptID();
		//assigning user details to fix the 900 blocker while approve in deposit approver screen
		if (rch.getUserDetails() == null) {
			if (SessionUserDetails.getLogiedInUser() != null) {
				rch.setUserDetails(SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser()));
			}
		}

		// Save Deposit Details
		saveDepositDetails(rch, PennantConstants.method_doApprove);
		BigDecimal prvMthAmz = profitDetail.getPrvMthAmz();

		// Check whether Presentment Receipt or Not
		boolean isPresentProc = false;
		if (rch.getReceiptDetails() != null && !rch.getReceiptDetails().isEmpty()) {
			for (FinReceiptDetail rcd : rch.getReceiptDetails()) {
				if (StringUtils.equals(rcd.getPaymentType(), RepayConstants.PAYTYPE_PRESENTMENT)) {
					isPresentProc = true;
				}
			}
		}

		// Update Status Details and Profit Details
		financeMain = repayProcessUtil.updateStatus(financeMain, valueDate, schdList, profitDetail, overdueList,
				rch.getReceiptPurpose(), isPresentProc);
		if (isLoanActiveBef && !financeMain.isFinIsActive()
				&& (StringUtils.equals(FinanceConstants.FINSER_EVENT_SCHDRPY,
						receiptData.getReceiptHeader().getReceiptPurpose()))
				&& (StringUtils.equals(RepayConstants.PAYSTATUS_DEPOSITED,
						receiptData.getReceiptHeader().getReceiptModeStatus()))) {
			financeMain.setFinIsActive(true);
			financeMain.setClosedDate(null);
			financeMain.setClosingStatus(null);
			profitDetail.setFinStatus(financeMain.getFinStatus());
			profitDetail.setFinStsReason(financeMain.getFinStsReason());
			profitDetail.setFinIsActive(financeMain.isFinIsActive());
			profitDetail.setClosingStatus(financeMain.getClosingStatus());
			profitDetail.setPrvMthAmz(prvMthAmz);
			profitDetailsDAO.update(profitDetail, true);
		}

		// Finance Main Updation
		// =======================================
		if ((FinanceConstants.CLOSE_STATUS_MATURED.equals(financeMain.getClosingStatus())
				|| FinanceConstants.CLOSE_STATUS_EARLYSETTLE.equals(financeMain.getClosingStatus()))
				&& !(RepayConstants.RECEIPTMODE_PRESENTMENT.equals(receiptHeader.getReceiptMode()))) {
			financeMain.setClosedDate(valueDate);
		}

		financeMainDAO.updateFromReceipt(financeMain, TableType.MAIN_TAB);

		// ScheduleDetails delete and save
		// =======================================
		listDeletion(finReference, "");
		listSave(scheduleData, "", 0, false);

		// Finance Fee Details
		if (scheduleData.getFinFeeDetailList() != null) {
			approveFees(receiptData, TableType.MAIN_TAB.getSuffix());
		}

		if (scheduleData.getFinFeeDetailList() != null) {
			for (int i = 0; i < rch.getAllocations().size(); i++) {
				ReceiptAllocationDetail allocation = rch.getAllocations().get(i);
				if (StringUtils.equals(RepayConstants.ALLOCATION_FEE, allocation.getAllocationType())) {
					for (FinFeeDetail feeDtl : receiptData.getFinanceDetail().getFinScheduleData()
							.getFinFeeDetailList()) {
						if (feeDtl.getFeeTypeID() == -(allocation.getAllocationTo())) {
							allocation.setAllocationTo(feeDtl.getFeeID());
							break;
						}
					}
				}
			}
		}
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
		List<AuditDetail> tempAuditDetailList = new ArrayList<AuditDetail>();

		if (!StringUtils.equals(PennantConstants.FINSOURCE_ID_API, receiptData.getSourceId())
				&& !receiptData.getFinanceDetail().isDirectFinalApprove()) {
			// Save Document Details
			if (receiptData.getFinanceDetail().getDocumentDetailsList() != null
					&& receiptData.getFinanceDetail().getDocumentDetailsList().size() > 0) {
				listDocDeletion(receiptData.getFinanceDetail(), TableType.TEMP_TAB.getSuffix());
			}

			// set Check list details Audit
			// =======================================
			if (receiptData.getFinanceDetail().getFinanceCheckList() != null
					&& !receiptData.getFinanceDetail().getFinanceCheckList().isEmpty()) {
				auditDetails.addAll(checkListDetailService.doApprove(receiptData.getFinanceDetail(), "", serviceUID));
			}

			// Extended Field Details
			if (receiptData.getFinanceDetail().getExtendedFieldRender() != null) {
				List<AuditDetail> details = receiptData.getFinanceDetail().getAuditDetailMap()
						.get("ExtendedFieldDetails");
				details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
						receiptData.getFinanceDetail().getExtendedFieldHeader(), "", serviceUID);
				auditDetails.addAll(details);
			}

			// ScheduleDetails delete
			// =======================================
			listDeletion(finReference, TableType.TEMP_TAB.getSuffix());

			// Checklist Details delete
			// =======================================
			tempAuditDetailList.addAll(checkListDetailService.delete(receiptData.getFinanceDetail(),
					TableType.TEMP_TAB.getSuffix(), tranType));

			// Delete and Save Repayments Schedule details by setting Repay
			// Header ID
			financeRepaymentsDAO.deleteRpySchdList(finReference, TableType.TEMP_TAB.getSuffix());

			// Delete and Save FinRepayHeader Detail list by Reference
			financeRepaymentsDAO.deleteByRef(finReference, TableType.TEMP_TAB);

			// Delete Save Receipt Detail List by Reference
			finReceiptDetailDAO.deleteByReceiptID(receiptID, TableType.TEMP_TAB);

			deleteTaxHeaderId(receiptID, TableType.TEMP_TAB.getSuffix());

			// Receipt Allocation Details
			allocationDetailDAO.deleteByReceiptID(receiptID, TableType.TEMP_TAB);

			// Delete Manual Advise Movements
			manualAdviseDAO.deleteMovementsByReceiptID(receiptID, TableType.TEMP_TAB.getSuffix());

			// Delete Receipt Header
			finReceiptHeaderDAO.deleteByReceiptID(receiptID, TableType.TEMP_TAB);

			// Finance Main Deletion from temp
			// financeMainDAO.delete(financeMain, TableType.TEMP_TAB, false,
			// true);

			// Extended field Render Details Delete from temp.
			List<AuditDetail> extendedDetails = receiptData.getFinanceDetail().getAuditDetailMap()
					.get("ExtendedFieldDetails");
			if (extendedDetails != null && extendedDetails.size() > 0) {
				tempAuditDetailList.addAll(extendedFieldDetailsService.delete(
						receiptData.getFinanceDetail().getExtendedFieldHeader(), financeMain.getFinReference(), "_Temp",
						auditHeader.getAuditTranType(), extendedDetails));
			}

		}

		// FinReceiptDetail Audit Details Preparation
		String[] rFields = PennantJavaUtil.getFieldDetails(new FinReceiptDetail(),
				receiptData.getReceiptHeader().getReceiptDetails().get(0).getExcludeFields());
		for (int i = 0; i < receiptData.getReceiptHeader().getReceiptDetails().size(); i++) {
			tempAuditDetailList.add(new AuditDetail(aAuditHeader.getAuditTranType(), 1, rFields[0], rFields[1], null,
					orgReceiptData.getReceiptHeader().getReceiptDetails().get(i)));
		}

		// Receipt Header Audit Details Preparation
		String[] rhFields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(),
				receiptData.getReceiptHeader().getExcludeFields());
		aAuditHeader.setAuditDetail(
				new AuditDetail(aAuditHeader.getAuditTranType(), 1, rhFields[0], rhFields[1], null, rch));

		// Adding audit as deleted from TEMP table
		aAuditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		aAuditHeader.setAuditDetails(tempAuditDetailList);
		aAuditHeader.setAuditModule("Receipt");
		getAuditHeaderDAO().addAudit(aAuditHeader);
		aAuditHeader.getAuditDetail().setModelData(receiptData);

		if (orgReceiptData.getReceiptHeader().getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
			tranType = PennantConstants.TRAN_ADD;
		} else {
			tranType = PennantConstants.TRAN_UPD;
		}

		// FinReceiptDetail Audit Details Preparation
		if (befFinReceiptDetail.isEmpty()) {
			for (int i = 0; i < receiptData.getReceiptHeader().getReceiptDetails().size(); i++) {
				auditDetails.add(new AuditDetail(tranType, 1, rFields[0], rFields[1], null,
						receiptData.getReceiptHeader().getReceiptDetails().get(i)));
			}
		} else {
			for (int i = 0; i < receiptData.getReceiptHeader().getReceiptDetails().size(); i++) {
				auditDetails.add(new AuditDetail(tranType, 1, rFields[0], rFields[1], befFinReceiptDetail.get(i),
						receiptData.getReceiptHeader().getReceiptDetails().get(i)));
			}
		}

		// FinReceiptHeader Audit
		auditHeader.setAuditDetail(
				new AuditDetail(tranType, 1, rhFields[0], rhFields[1], befRctHeader, receiptData.getReceiptHeader()));

		// Adding audit as Insert/Update/deleted into main table
		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.setAuditDetails(auditDetails);
		auditHeader.setAuditModule("Receipt");
		getAuditHeaderDAO().addAudit(auditHeader);
		// Reset Finance Detail Object for Service Task Verifications
		receiptData.getFinanceDetail().getFinScheduleData().setFinanceMain(financeMain);
		auditHeader.getAuditDetail().setModelData(receiptData);

		// send DDA Cancellation Request to Interface
		// ===========================================
		// Fetch Total Repayment Amount till Maturity date for Early Settlement
		if (FinanceConstants.CLOSE_STATUS_MATURED.equals(financeMain.getClosingStatus())) {

			// send Collateral DeMark request to Interface
			// ==========================================
			if (ImplementationConstants.COLLATERAL_INTERNAL) {
				if (ImplementationConstants.COLLATERAL_DELINK_AUTO) {

					List<CollateralAssignment> colAssignList = collateralAssignmentDAO
							.getCollateralAssignmentByFinRef(finReference, FinanceConstants.MODULE_NAME, "");
					if (colAssignList != null && !colAssignList.isEmpty()) {
						for (int i = 0; i < colAssignList.size(); i++) {
							CollateralMovement movement = new CollateralMovement();
							movement.setModule(FinanceConstants.MODULE_NAME);
							movement.setCollateralRef(colAssignList.get(i).getCollateralRef());
							movement.setReference(colAssignList.get(i).getReference());
							movement.setAssignPerc(BigDecimal.ZERO);
							movement.setValueDate(curBusDate);
							movement.setProcess(CollateralConstants.PROCESS_AUTO);
							collateralAssignmentDAO.save(movement);
						}

						collateralAssignmentDAO.deLinkCollateral(financeMain.getFinReference());
					}
				}
			}
		}

		// send Limit Amendment Request to ACP Interface and save log details
		// =======================================
		if (ImplementationConstants.LIMIT_INTERNAL) {
			BigDecimal priAmt = BigDecimal.ZERO;

			for (FinReceiptDetail finReceiptDetail : rch.getReceiptDetails()) {
				FinRepayHeader header = finReceiptDetail.getRepayHeader();
				if (CollectionUtils.isNotEmpty(header.getRepayScheduleDetails())) {
					for (RepayScheduleDetail rpySchd : header.getRepayScheduleDetails()) {
						priAmt = priAmt.add(rpySchd.getPrincipalSchdPayNow().add(rpySchd.getPriSchdWaivedNow()));
					}
				} else {
					priAmt = priAmt.add(header.getPriAmount());
				}
			}
			Customer customer = getCustomerDAO().getCustomerByID(financeMain.getCustID());
			limitManagement.processLoanRepay(financeMain, customer, priAmt,
					StringUtils.trimToEmpty(financeMain.getProductCategory()));
		} else {
			limitCheckDetails.doProcessLimits(financeMain, FinanceConstants.AMENDEMENT);
		}

		// Update Deposit Branch
		if (ImplementationConstants.DEPOSIT_PROC_REQ) {
			finReceiptHeaderDAO.updateDepositBranchByReceiptID(rch.getReceiptID(), rch.getUserDetails().getBranchCode(),
					"");
		}

		/* Creating Payble advice for advance interest case in case of Early Pay */
		if (FinanceConstants.FINSER_EVENT_EARLYRPY.equals(rch.getReceiptPurpose())) {
			advancePaymentService.setAdvancePaymentDetails(scheduleData.getFinanceMain(), scheduleData);
		}

		logger.debug("Leaving");

		return auditHeader;
	}

	public List<FinODDetails> calPenalty(FinScheduleData schData, FinReceiptData receiptData, Date valueDate,
			List<FinODDetails> odList) {
		logger.debug(Literal.ENTERING);

		if (CollectionUtils.isEmpty(odList)) {
			return odList;
		}

		FinanceMain fm = schData.getFinanceMain();
		List<FinanceScheduleDetail> schdList = schData.getFinanceScheduleDetails();
		String finReference = fm.getFinReference();
		List<FinanceRepayments> repayments = financeRepaymentsDAO.getFinRepayListByFinRef(finReference, false, "");
		odList = latePayMarkingService.calPDOnBackDatePayment(fm, odList, valueDate, schdList, repayments, true, true);

		logger.debug(Literal.LEAVING);
		return odList;
	}

	/**
	 * Method for Saving Deposit Details for Both Receipt Modes of CASH & Cheque/DD
	 * 
	 * @param receiptHeader
	 */
	private void saveDepositDetails(FinReceiptHeader receiptHeader, String method) {
		logger.debug("Entering");

		// If Process is not required for Client
		if (!ImplementationConstants.DEPOSIT_PROC_REQ) {
			return;
		}

		// If Deposit Process is not other than CASH, CHEQUE & DD then no
		// process is executed
		if (!StringUtils.equals(RepayConstants.RECEIPTMODE_CASH, receiptHeader.getReceiptMode())
				&& !StringUtils.equals(RepayConstants.RECEIPTMODE_CHEQUE, receiptHeader.getReceiptMode())
				&& !StringUtils.equals(RepayConstants.RECEIPTMODE_DD, receiptHeader.getReceiptMode())) {
			return;
		}

		// If Cheque or DD Process , then on deposit process only these
		// executions should be done
		if (!StringUtils.equals(RepayConstants.RECEIPTMODE_CASH, receiptHeader.getReceiptMode())) {
			if (!receiptHeader.isDepositProcess()) {
				return;
			}
		} else if (StringUtils.equals(RepayConstants.RECEIPTMODE_CASH, receiptHeader.getReceiptMode())) {
			if (!StringUtils.equals(method, PennantConstants.method_doApprove)) {
				return;
			}
		}

		BigDecimal depositReqAmount = BigDecimal.ZERO;
		long partnerBankId = 0;
		String reqReceiptMode = null;
		Date valueDate = null;

		// Find Amount of Deposited Request
		for (FinReceiptDetail rcptDetail : receiptHeader.getReceiptDetails()) {

			// CASH / CHEQUE / DD MODE
			if (StringUtils.equals(RepayConstants.RECEIPTMODE_CASH, rcptDetail.getPaymentType())
					|| StringUtils.equals(RepayConstants.RECEIPTMODE_CHEQUE, rcptDetail.getPaymentType())
					|| StringUtils.equals(RepayConstants.RECEIPTMODE_DD, rcptDetail.getPaymentType())) {

				if (!StringUtils.equals(RepayConstants.RECEIPTMODE_CASH, rcptDetail.getPaymentType())) {
					partnerBankId = rcptDetail.getFundingAc();
					reqReceiptMode = CashManagementConstants.ACCEVENT_DEPOSIT_TYPE_CHEQUE_DD;
				} else {
					reqReceiptMode = CashManagementConstants.ACCEVENT_DEPOSIT_TYPE_CASH;
				}
				depositReqAmount = depositReqAmount.add(rcptDetail.getAmount());
				valueDate = rcptDetail.getReceivedDate();
			}
		}

		// UPDATE Deposit Branch in Receipt Header based on deposit updation
		// details TODO

		// IF Deposited Requested amount is greater than zero then Deposit
		// process details should be inserted/updated
		if (depositReqAmount.compareTo(BigDecimal.ZERO) > 0 && StringUtils.isNotBlank(reqReceiptMode)) {

			// Check Whether Deposit Details record against branch is already
			// exists or not
			DepositDetails depositDetail = depositDetailsDAO.getDepositDetails(reqReceiptMode,
					receiptHeader.getUserDetails().getBranchCode(), "");

			if (depositDetail == null) {
				depositDetail = new DepositDetails();
				depositDetail.setActualAmount(depositReqAmount);
				depositDetail.setReservedAmount(BigDecimal.ZERO);
				depositDetail.setDepositType(reqReceiptMode);
				depositDetail.setBranchCode(receiptHeader.getUserDetails().getBranchCode());
				depositDetail.setVersion(1);
				depositDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				;
				depositDetail.setLastMntBy(receiptHeader.getLastMntBy());
				depositDetail.setLastMntOn(receiptHeader.getLastMntOn());
				depositDetail.setWorkflowId(0);
				depositDetail.setNewRecord(true);
				depositDetail.setDepositId(depositDetailsDAO.save(depositDetail, TableType.MAIN_TAB));
			} else {
				depositDetailsDAO.updateActualAmount(depositDetail.getDepositId(), depositReqAmount, true, "");
			}

			// Deposit Details movement creation for the increased credit of
			// Available Amount
			DepositMovements depositMovements = new DepositMovements();
			depositMovements = new DepositMovements();
			depositMovements.setDepositId(depositDetail.getDepositId());
			depositMovements.setTransactionType(CashManagementConstants.DEPOSIT_MOVEMENT_CREDIT);
			depositMovements.setReservedAmount(depositReqAmount);
			depositMovements.setPartnerBankId(partnerBankId);
			depositMovements.setReceiptId(receiptHeader.getReceiptID());
			depositMovements.setTransactionDate(valueDate);
			depositMovements.setVersion(1);
			depositMovements.setNewRecord(true);
			depositMovements.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			;
			depositMovements.setRecordType(null);
			depositMovements.setLastMntBy(receiptHeader.getLastMntBy());
			depositMovements.setLastMntOn(receiptHeader.getLastMntOn());
			depositMovements.setWorkflowId(0);
			depositDetail.setDepositMovements(depositMovements);

			depositDetailsDAO.saveDepositMovements(depositMovements, "");
		}

		logger.debug("Leaving");
	}

	/**
	 * doReversal method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using finReceiptHeaderDAO.delete with
	 * parameters financeMain,"" b) NEW Add new record in to main table by using finReceiptHeaderDAO.save with
	 * parameters financeMain,"" c) EDIT Update record in the main table by using finReceiptHeaderDAO.update with
	 * parameters financeMain,"" 3) Delete the record from the workFlow table by using finReceiptHeaderDAO.delete with
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
	public AuditHeader doReversal(AuditHeader aAuditHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		aAuditHeader = businessValidation(aAuditHeader, "doApprove");
		if (!aAuditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		FinReceiptData rceiptData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		FinReceiptHeader receiptHeader = rceiptData.getReceiptHeader();
		receiptHeader.setPostBranch(auditHeader.getAuditBranchCode());
		FinanceMain financeMain = rceiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();

		long serviceUID = Long.MIN_VALUE;
		for (FinServiceInstruction finServInst : rceiptData.getFinanceDetail().getFinScheduleData()
				.getFinServiceInstructions()) {
			serviceUID = finServInst.getInstructionUID();
		}

		// Cancel All Transactions done by Finance Reference
		// =======================================
		cancelStageAccounting(financeMain.getFinReference(), receiptHeader.getReceiptPurpose());

		// Bounce Charge Due Postings
		if (StringUtils.equals(receiptHeader.getReceiptModeStatus(), RepayConstants.PAYSTATUS_BOUNCE)) {
			ManualAdvise bounce = receiptHeader.getManualAdvise();
			if (bounce != null && bounce.getAdviseAmount().compareTo(BigDecimal.ZERO) > 0) {

				if (bounce.getAdviseID() <= 0) {
					bounce.setAdviseID(this.manualAdviseDAO.getNewAdviseID());
				}

				AEEvent aeEvent = executeBounceDueAccounting(financeMain, receiptHeader.getBounceDate(), bounce,
						auditHeader.getAuditBranchCode(), RepayConstants.ALLOCATION_BOUNCE);
				if (aeEvent != null && StringUtils.isNotEmpty(aeEvent.getErrorMessage())) {
					ArrayList<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
					errorDetails.add(new ErrorDetail("Accounting Engine", PennantConstants.ERR_UNDEF, "E",
							aeEvent.getErrorMessage(), new String[] {}, new String[] {}));
					logger.debug("Leaving");
					return auditHeader;
				}
				receiptHeader.getManualAdvise().setLinkedTranId(aeEvent.getLinkedTranId());
			}
		}

		// Accounting Execution Process for Deposit Reversal
		if (StringUtils.equals(RepayConstants.RECEIPTMODE_CHEQUE, receiptHeader.getReceiptMode())
				|| StringUtils.equals(RepayConstants.RECEIPTMODE_DD, receiptHeader.getReceiptMode())) {

			// Verify Cheque or DD Details exists in Deposited Cheques
			DepositCheques depositCheque = depositChequesDAO.getDepositChequeByReceiptID(receiptHeader.getReceiptID());
			if (depositCheque != null) {
				if (depositCheque.getLinkedTranId() > 0) {
					// Postings Reversal
					postingsPreparationUtil.postReversalsByLinkedTranID(depositCheque.getLinkedTranId());
					// Make Deposit Cheque to Reversal Status
					depositChequesDAO.reverseChequeStatus(depositCheque.getMovementId(), receiptHeader.getReceiptID(),
							depositCheque.getLinkedTranId());
				} else {
					logger.info("Postings Id is not available in deposit cheques");
					throw new InterfaceException("CHQ001", "Issue with deposit details postings prepartion.");
				}
			}
		}

		tranType = PennantConstants.TRAN_UPD;
		receiptHeader.setRcdMaintainSts(null);
		receiptHeader.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		receiptHeader.setRecordType("");
		receiptHeader.setRoleCode("");
		receiptHeader.setNextRoleCode("");
		receiptHeader.setTaskId("");
		receiptHeader.setNextTaskId("");
		receiptHeader.setWorkflowId(0);

		// save Receipt Details
		repayProcessUtil.doSaveReceipts(receiptHeader, null, false);
		long receiptID = receiptHeader.getReceiptID();

		// Bounce reason Code
		if (StringUtils.equals(receiptHeader.getReceiptModeStatus(), RepayConstants.PAYSTATUS_BOUNCE)) {
			if (receiptHeader.getManualAdvise() != null) {
				receiptHeader.getManualAdvise().setDueCreation(true);
				manualAdviseDAO.save(receiptHeader.getManualAdvise(), TableType.MAIN_TAB);
			}
		}

		String finReference = receiptHeader.getReference();
		if (!StringUtils.equals(PennantConstants.FINSOURCE_ID_API, rceiptData.getSourceId())) {

			// Save Document Details
			if (rceiptData.getFinanceDetail().getDocumentDetailsList() != null
					&& rceiptData.getFinanceDetail().getDocumentDetailsList().size() > 0) {
				List<AuditDetail> details = rceiptData.getFinanceDetail().getAuditDetailMap().get("DocumentDetails");
				details = processingDocumentDetailsList(details, "",
						rceiptData.getFinanceDetail().getFinScheduleData().getFinanceMain(),
						receiptHeader.getReceiptPurpose(), serviceUID);
				auditDetails.addAll(details);
				listDocDeletion(rceiptData.getFinanceDetail(), TableType.TEMP_TAB.getSuffix());
			}

			// set Check list details Audit
			// =======================================
			if (rceiptData.getFinanceDetail().getFinanceCheckList() != null
					&& !rceiptData.getFinanceDetail().getFinanceCheckList().isEmpty()) {
				auditDetails.addAll(checkListDetailService.doApprove(rceiptData.getFinanceDetail(), "", serviceUID));
			}

			String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());

			// ScheduleDetails delete
			// =======================================
			listDeletion(finReference, TableType.TEMP_TAB.getSuffix());

			// Fee charges deletion
			List<AuditDetail> tempAuditDetailList = new ArrayList<AuditDetail>();

			// Fin Fee Details Deletion
			List<FinFeeDetail> oldFeedetails = rceiptData.getFinFeeDetails();
			if (CollectionUtils.isNotEmpty(oldFeedetails)) {
				for (FinFeeDetail finFeeDetail : oldFeedetails) {
					finFeeScheduleDetailDAO.deleteFeeScheduleBatch(finFeeDetail.getFeeID(), false, "_Temp");
					finFeeDetailDAO.delete(finFeeDetail, false, "_Temp");
				}
			}

			// Checklist Details delete
			// =======================================
			tempAuditDetailList.addAll(checkListDetailService.delete(rceiptData.getFinanceDetail(),
					TableType.TEMP_TAB.getSuffix(), tranType));

			// Delete and Save Repayments Schedule details by setting Repay
			// Header ID
			financeRepaymentsDAO.deleteRpySchdList(finReference, TableType.TEMP_TAB.getSuffix());

			// Delete and Save FinRepayHeader Detail list by Reference
			financeRepaymentsDAO.deleteByRef(finReference, TableType.TEMP_TAB);

			// Delete Save Receipt Detail List by Reference
			finReceiptDetailDAO.deleteByReceiptID(receiptID, TableType.TEMP_TAB);

			deleteTaxHeaderId(receiptID, TableType.TEMP_TAB.getSuffix());
			// Receipt Allocation Details
			allocationDetailDAO.deleteByReceiptID(receiptID, TableType.TEMP_TAB);

			// Delete Manual Advise Movements
			manualAdviseDAO.deleteMovementsByReceiptID(receiptID, TableType.TEMP_TAB.getSuffix());

			// Delete Bounce reason Code
			ManualAdvise advise = manualAdviseDAO.getManualAdviseByReceiptId(receiptID, TableType.TEMP_TAB.getSuffix());
			if (advise != null) {
				manualAdviseDAO.deleteByAdviseId(advise, TableType.TEMP_TAB);
			}

			// Delete Receipt Header
			finReceiptHeaderDAO.deleteByReceiptID(receiptID, TableType.TEMP_TAB);

			// Finance Main Deletion from temp
			financeMainDAO.delete(financeMain, TableType.TEMP_TAB, false, true);

			FinReceiptData tempRepayData = (FinReceiptData) aAuditHeader.getAuditDetail().getModelData();
			FinanceMain tempfinanceMain = tempRepayData.getFinanceDetail().getFinScheduleData().getFinanceMain();
			auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1],
					tempfinanceMain.getBefImage(), tempfinanceMain));

			// Receipt Header Audit Details Preparation
			String[] rhFields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(),
					rceiptData.getReceiptHeader().getExcludeFields());
			tempAuditDetailList.add(new AuditDetail(aAuditHeader.getAuditTranType(), 1, rhFields[0], rhFields[1],
					rceiptData.getReceiptHeader().getBefImage(), rceiptData.getReceiptHeader()));

			// Adding audit as deleted from TEMP table
			auditHeader.setAuditDetails(tempAuditDetailList);
			auditHeader.setAuditModule("FinanceDetail");
			getAuditHeaderDAO().addAudit(auditHeader);

			// Receipt Header Audit Details Preparation
			auditDetails.add(new AuditDetail(tranType, 1, rhFields[0], rhFields[1],
					rceiptData.getReceiptHeader().getBefImage(), rceiptData.getReceiptHeader()));

			auditHeader.setAuditTranType(tranType);
			auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
					financeMain.getBefImage(), financeMain));

			// Adding audit as Insert/Update/deleted into main table
			auditHeader.setAuditDetails(auditDetails);
			auditHeader.setAuditModule("Receipt");
			getAuditHeaderDAO().addAudit(auditHeader);
		}

		// Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(rceiptData);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Method for Saving List of Finance Details
	 * 
	 * @param scheduleData
	 * @param tableType
	 * @param logKey
	 */
	private void listSave(FinScheduleData scheduleData, String tableType, long logKey, boolean saveDisb) {
		logger.debug("Entering ");
		HashMap<Date, Integer> mapDateSeq = new HashMap<Date, Integer>();

		// Finance Schedule Details
		if (StringUtils.isEmpty(tableType)) {
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

			financeScheduleDetailDAO.saveList(scheduleData.getFinanceScheduleDetails(), tableType, false);
		}

		if (logKey != 0 || saveDisb) {
			// Finance Disbursement Details
			mapDateSeq = new HashMap<Date, Integer>();
			for (int i = 0; i < scheduleData.getDisbursementDetails().size(); i++) {
				scheduleData.getDisbursementDetails().get(i).setFinReference(scheduleData.getFinReference());
				scheduleData.getDisbursementDetails().get(i).setDisbIsActive(true);
				scheduleData.getDisbursementDetails().get(i).setDisbDisbursed(true);
				scheduleData.getDisbursementDetails().get(i).setLogKey(logKey);
			}
			financeDisbursementDAO.saveList(scheduleData.getDisbursementDetails(), tableType, false);

		}

		// Finance Repay Instruction Details
		if (scheduleData.getRepayInstructions() != null) {
			for (int i = 0; i < scheduleData.getRepayInstructions().size(); i++) {
				RepayInstruction curSchd = scheduleData.getRepayInstructions().get(i);

				curSchd.setFinReference(scheduleData.getFinReference());
				curSchd.setLogKey(logKey);
			}
			repayInstructionDAO.saveList(scheduleData.getRepayInstructions(), tableType, false);
		}

		logger.debug("Leaving ");
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

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);

		FinReceiptData repayData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		FinanceDetail financeDetail = repayData.getFinanceDetail();
		// String usrLanguage = auditHeader.getUsrLanguage();
		// Need to check with kranthi
		String usrLanguage = repayData.getReceiptHeader().getUserDetails().getLanguage();
		// Extended field details Validation
		if (financeDetail.getExtendedFieldRender() != null) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("ExtendedFieldDetails");
			ExtendedFieldHeader extHeader = financeDetail.getExtendedFieldHeader();
			details = extendedFieldDetailsService.validateExtendedDdetails(extHeader, details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		//Dedup Check
		if (!RepayConstants.PAYSTATUS_BOUNCE.equals(repayData.getReceiptHeader().getReceiptModeStatus())
				&& !RepayConstants.PAYSTATUS_CANCEL.equals(repayData.getReceiptHeader().getReceiptModeStatus())) {
			FinServiceInstruction fsi = new FinServiceInstruction();
			FinReceiptHeader receiptHeader = repayData.getReceiptHeader();
			FinReceiptDetail finReceiptDetail = receiptHeader.getReceiptDetails().get(0);
			fsi.setFinReference(receiptHeader.getReference());
			fsi.setValueDate(receiptHeader.getValueDate());
			fsi.setAmount(receiptHeader.getReceiptAmount());
			fsi.setTransactionRef(finReceiptDetail.getTransactionRef());
			fsi.setPaymentMode(receiptHeader.getReceiptMode());
			List<ErrorDetail> errors = dedupCheck(fsi);
			if (CollectionUtils.isNotEmpty(errors)) {
				auditHeader.setErrorDetails(errors.get(0));
			}
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

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
		FinReceiptData repayData = (FinReceiptData) auditDetail.getModelData();
		FinReceiptHeader finReceiptHeader = repayData.getReceiptHeader();
		FinReceiptData receiptData = new FinReceiptData();

		receiptData = getFinReceiptDataById(finReceiptHeader.getReference(), AccountEventConstants.ACCEVENT_EARLYPAY,
				FinanceConstants.FINSER_EVENT_RECEIPT, "");
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceProfitDetail finPftDetail = finScheduleData.getFinPftDeatil();

		FinReceiptHeader tempFinReceiptHeader = null;
		if (finReceiptHeader.isWorkflow()) {
			tempFinReceiptHeader = finReceiptHeaderDAO.getReceiptHeaderByID(finReceiptHeader.getReceiptID(),
					TableType.TEMP_TAB.getSuffix());
		}
		FinReceiptHeader befFinReceiptHeader = finReceiptHeaderDAO.getReceiptHeaderByID(finReceiptHeader.getReceiptID(),
				"");
		FinReceiptHeader oldFinReceiptHeader = finReceiptHeader.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(finReceiptHeader.getReceiptID());
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (finReceiptHeader.isNew()) { // for New record or new record into
											// work
										// flow

			if (!finReceiptHeader.isWorkflow()) {// With out Work flow only new
				// records
				if (befFinReceiptHeader != null) { // Record Already Exists in
														// the
													// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (finReceiptHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
					// records type is new
					if (befFinReceiptHeader != null || tempFinReceiptHeader != null) { // if
						// records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befFinReceiptHeader == null || tempFinReceiptHeader != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!finReceiptHeader.isWorkflow()) { // With out Work flow for
														// update
													// and delete

				if (befFinReceiptHeader == null) { // if records not exists in
														// the
													// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldFinReceiptHeader != null
							&& !oldFinReceiptHeader.getLastMntOn().equals(befFinReceiptHeader.getLastMntOn())) {
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

				if (tempFinReceiptHeader == null) { // if records not exists in
														// the
													// Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempFinReceiptHeader != null && oldFinReceiptHeader != null
						&& !oldFinReceiptHeader.getLastMntOn().equals(tempFinReceiptHeader.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		// validation for not allowing early settlement when presentation is in progress.
		if (FinanceConstants.FINSER_EVENT_EARLYSETTLE.equals(finReceiptHeader.getReceiptPurpose())) {
			boolean isPending = isReceiptsPending(finReceiptHeader.getReference(), finReceiptHeader.getReceiptID());
			if (isPending) {
				valueParm[0] = "Not allowed to do Early Settlement due to previous Presentments/Receipts are in process";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
			}
		}

		// Checking , if Customer is in EOD process or not. if Yes, not allowed
		// to do an action
		int eodProgressCount = customerQueuingDAO.getProgressCountByCust(finReceiptHeader.getCustID());

		// If Customer Exists in EOD Processing, Not allowed to Maintenance till
		// completion
		if (eodProgressCount > 0) {
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "60203", errParm, valueParm), usrLanguage));
		}

		// Validation For Received Date except Reject Case
		// PSD : 137382
		/*
		 * if (!StringUtils.equals(method, PennantConstants.method_doReject)) { boolean isPresentment = false;
		 * List<FinReceiptDetail> ReceiptDetailList = repayData.getReceiptHeader().getReceiptDetails(); Date
		 * receivedDate = DateUtility.getAppDate(); for (FinReceiptDetail receiptDetail : ReceiptDetailList) { if
		 * (StringUtils.equals(repayData.getReceiptHeader().getReceiptMode(), receiptDetail.getPaymentType())) { if
		 * (RepayConstants.RECEIPTMODE_PRESENTMENT.equals(receiptDetail. getPaymentType())) { isPresentment = true; }
		 * receivedDate = receiptDetail.getReceivedDate(); } }
		 * 
		 * if (!isPresentment) { Date prvMaxReceivedDate = getMaxReceiptDate(finReceiptHeader.getReference()); if
		 * (prvMaxReceivedDate != null && receivedDate != null) { if (DateUtility.compare(prvMaxReceivedDate,
		 * receivedDate) > 0) { valueParm[0] = DateUtil.formatToLongDate(prvMaxReceivedDate); errParm[0] = valueParm[0];
		 * auditDetail.setErrorDetail(ErrorUtil.getErrorDetail( new ErrorDetail(PennantConstants.KEY_FIELD, "60211",
		 * errParm, valueParm), usrLanguage)); } } } }
		 */

		// Validation For Received Date except Reject Case
		if (!StringUtils.equals(method, PennantConstants.method_doReject)) {
			List<FinReceiptDetail> receiptDetailList = repayData.getReceiptHeader().getReceiptDetails();
			for (FinReceiptDetail receiptDetail : receiptDetailList) {
				if (StringUtils.equals(repayData.getReceiptHeader().getReceiptMode(), receiptDetail.getPaymentType())) {

					// Instrument wise Limit Validations (Max Amounts per Day
					// per Customer)
					if (StringUtils.isNotEmpty(receiptDetail.getPaymentType())
							&& !StringUtils.equals(receiptDetail.getPaymentType(), PennantConstants.List_Select)) {

						ErrorDetail errorDetail = doInstrumentValidation(repayData);
						if (errorDetail != null) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(errorDetail, usrLanguage));
						}
					}
				}

			}

		}

		/*
		 * // Checking For Commitment , Is it In Maintenance Or not if
		 * (StringUtils.trimToEmpty(finReceiptHeader.getRecordType()).equals( PennantConstants.RECORD_TYPE_NEW) &&
		 * "doApprove".equals(method) && StringUtils.isNotEmpty(finReceiptHeader.getFinCommitmentRef())) {
		 * 
		 * Commitment tempcommitment = getCommitmentDAO().getCommitmentById(finReceiptHeader. getFinCommitmentRef(),
		 * TableType.TEMP_TAB.getSuffix()); if (tempcommitment != null && tempcommitment.isRevolving()) {
		 * auditDetail.setErrorDetail(ErrorUtil.getErrorDetail( new ErrorDetail(PennantConstants.KEY_FIELD, "30538",
		 * errParm, valueParm), usrLanguage)); } }
		 */

		String recordStatus = finReceiptHeader.getRecordStatus();
		if ((StringUtils.equals(recordStatus, PennantConstants.RCD_STATUS_SAVED)
				|| StringUtils.equals(recordStatus, PennantConstants.RCD_STATUS_SUBMITTED)
				|| StringUtils.equals(recordStatus, PennantConstants.RCD_STATUS_APPROVED))) {

			String[] parms = new String[2];
			if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOWED_BACKDATED_RECEIPT)) {
				Date prvSchdDate = finPftDetail.getPrvRpySchDate();
				if (finReceiptHeader.getValueDate().compareTo(prvSchdDate) < 0) {
					parms[0] = DateUtility.formatToLongDate(finReceiptHeader.getValueDate());
					parms[1] = DateUtility.formatToLongDate(prvSchdDate);
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("RU0012", parms)));
				}
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !finReceiptHeader.isWorkflow()) {
			finReceiptHeader.setBefImage(befFinReceiptHeader);
		}

		return auditDetail;
	}

	/**
	 * Method for Fetching Max Receipt Received Date
	 * 
	 * @param finReference
	 * @return
	 */
	@Override
	public Date getMaxReceiptDate(String finReference) {
		return finReceiptDetailDAO.getMaxReceivedDateByReference(finReference);
	}

	@Override
	public boolean isInSubVention(FinanceMain financeMain, Date receivedDate) {
		boolean isInSubVention = false;
		Date grcPeriodEndDate = financeMain.getGrcPeriodEndDate();
		Date toDate = null;
		SubventionDetail subDetail = subventionDetailDAO.getSubventionDetail(financeMain.getFinReference(), "");
		if (subDetail != null) {
			if (grcPeriodEndDate.compareTo(subDetail.getEndDate()) <= 0) {
				toDate = grcPeriodEndDate;
			} else {
				toDate = subDetail.getEndDate();
			}
			if (DateUtility.compare(toDate, receivedDate) >= 0) {
				isInSubVention = true;
			}
		}
		return isInSubVention;
	}

	/**
	 * Method for prepare AuditHeader
	 * 
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinReceiptData repayData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		FinanceDetail financeDetail = repayData.getFinanceDetail();
		FinReceiptHeader financeMain = repayData.getReceiptHeader();

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

		// Finance Fee details
		if (!financeDetail.isExtSource()) {
			if (financeDetail.getFinScheduleData().getFinFeeDetailList() != null) {
				auditDetails.addAll(finFeeDetailService.validate(
						financeDetail.getFinScheduleData().getFinFeeDetailList(), financeMain.getWorkflowId(), method,
						auditTranType, auditHeader.getUsrLanguage(), false));
			}
		}

		// Extended Field Details
		if (financeDetail.getExtendedFieldRender() != null) {
			auditDetailMap.put("ExtendedFieldDetails", extendedFieldDetailsService
					.setExtendedFieldsAuditData(financeDetail.getExtendedFieldRender(), auditTranType, method, null));
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldDetails"));
		}

		//Check List Details
		if (!CollectionUtils.isEmpty(financeDetail.getFinanceCheckList())) {
			auditDetails.addAll(
					checkListDetailService.getAuditDetail(auditDetailMap, financeDetail, auditTranType, method));
		}

		financeDetail.setAuditDetailMap(auditDetailMap);
		repayData.setFinanceDetail(financeDetail);
		auditHeader.getAuditDetail().setModelData(repayData);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving ");

		return auditHeader;
	}

	/**
	 * Method for Calculate Payment Details based on Entered Receipts
	 * 
	 * @param financeMain
	 * @param finSchDetails
	 * @param isReCal
	 * @param method
	 * @param valueDate
	 * @return
	 */
	@Override
	public FinReceiptData calculateRepayments(FinReceiptData finReceiptData, boolean isPresentment) {
		logger.debug("Entering");

		finReceiptData.setBuildProcess("R");
		finReceiptData.getRepayMain().setRepayAmountNow(BigDecimal.ZERO);
		finReceiptData.getRepayMain().setPrincipalPayNow(BigDecimal.ZERO);
		finReceiptData.getRepayMain().setProfitPayNow(BigDecimal.ZERO);

		// Prepare Receipt Details Data
		FinReceiptHeader receiptHeader = finReceiptData.getReceiptHeader();
		receiptHeader.setReceiptAmount(receiptHeader.getReceiptAmount());

		for (ReceiptAllocationDetail allocate : finReceiptData.getReceiptHeader().getAllocations()) {
			allocate.setPaidAvailable(allocate.getPaidAmount());
			allocate.setWaivedAvailable(allocate.getWaivedAmount());
			allocate.setPaidAmount(BigDecimal.ZERO);
			allocate.setPaidGST(BigDecimal.ZERO);
			allocate.setTotalPaid(BigDecimal.ZERO);
			allocate.setBalance(allocate.getTotalDue());
			allocate.setWaivedAmount(BigDecimal.ZERO);
			allocate.setWaivedGST(BigDecimal.ZERO);
		}

		// FIXME: PV
		finReceiptData.setReceiptHeader(receiptHeader);
		Cloner cloner = new Cloner();
		List<FinanceScheduleDetail> finSchdDtls = cloner
				.deepClone(finReceiptData.getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails());
		finReceiptData = receiptCalculator.initiateReceipt(finReceiptData, isPresentment);
		finReceiptData.getFinanceDetail().getFinScheduleData().setFinanceScheduleDetails(finSchdDtls);

		logger.debug("Leaving");
		return finReceiptData;
	}

	/**
	 * Method for Fetch Overdue Penalty details as per passing Value Date
	 * 
	 * @param finScheduleData
	 * @param receiptData
	 * @param valueDate
	 * @return
	 */
	@Override
	public List<FinODDetails> getValueDatePenalties(FinScheduleData finScheduleData, BigDecimal orgReceiptAmount,
			Date valueDate, List<FinanceRepayments> finRepayments, boolean resetReq) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();
		List<FinODDetails> overdueList = getFinODDetailsDAO().getFinODBalByFinRef(financeMain.getFinReference());
		if (overdueList == null || overdueList.isEmpty()) {
			logger.debug("Leaving");
			return overdueList;
		}

		// Repayment Details
		List<FinanceRepayments> repayments = new ArrayList<FinanceRepayments>();
		if (finRepayments != null && !finRepayments.isEmpty()) {
			repayments = finRepayments;
		} else {
			repayments = financeRepaymentsDAO.getFinRepayListByFinRef(financeMain.getFinReference(), false, "");
		}
		BigDecimal totReceiptAmt = orgReceiptAmount;

		// Newly Paid Amount Repayment Details
		List<FinanceScheduleDetail> schdList = finScheduleData.getFinanceScheduleDetails();
		if (totReceiptAmt.compareTo(BigDecimal.ZERO) > 0) {
			char[] rpyOrder = finScheduleData.getFinanceType().getRpyHierarchy().replace("CS", "C").toCharArray();
			FinanceScheduleDetail curSchd = null;
			for (int i = 0; i < schdList.size(); i++) {
				curSchd = schdList.get(i);
				if (curSchd.getSchDate().compareTo(valueDate) > 0) {
					break;
				}

				if (totReceiptAmt.compareTo(BigDecimal.ZERO) == 0) {
					break;
				}

				if ((curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid())).compareTo(BigDecimal.ZERO) > 0
						|| (curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()))
								.compareTo(BigDecimal.ZERO) > 0
						|| (curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid())).compareTo(BigDecimal.ZERO) > 0) {

					FinanceRepayments repayment = new FinanceRepayments();
					repayment.setFinValueDate(valueDate);
					repayment.setFinRpyFor(FinanceConstants.SCH_TYPE_SCHEDULE);
					repayment.setFinSchdDate(curSchd.getSchDate());
					repayment.setFinRpyAmount(orgReceiptAmount);

					for (int j = 0; j < rpyOrder.length; j++) {

						char repayTo = rpyOrder[j];
						if (repayTo == RepayConstants.REPAY_PENALTY) {
							continue;
						}
						BigDecimal balAmount = BigDecimal.ZERO;
						if (repayTo == RepayConstants.REPAY_PRINCIPAL) {
							balAmount = curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid());
							if (totReceiptAmt.compareTo(balAmount) < 0) {
								balAmount = totReceiptAmt;
							}
							repayment.setFinSchdPriPaid(balAmount);
							totReceiptAmt = totReceiptAmt.subtract(balAmount);

						} else if (repayTo == RepayConstants.REPAY_PROFIT) {

							balAmount = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
							if (totReceiptAmt.compareTo(balAmount) < 0) {
								balAmount = totReceiptAmt;
							}
							repayment.setFinSchdPftPaid(balAmount);
							totReceiptAmt = totReceiptAmt.subtract(balAmount);

						} else if (repayTo == RepayConstants.REPAY_FEE) {

							balAmount = curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid());
							if (totReceiptAmt.compareTo(balAmount) < 0) {
								balAmount = totReceiptAmt;
							}
							repayment.setSchdFeePaid(balAmount);
							totReceiptAmt = totReceiptAmt.subtract(balAmount);
						}

					}
					repayment.setFinTotSchdPaid(repayment.getFinSchdPftPaid().add(repayment.getFinSchdPriPaid()));
					repayment.setFinType(financeMain.getFinType());
					repayment.setFinBranch(financeMain.getFinBranch());
					repayment.setFinCustID(financeMain.getCustID());
					repayment.setFinPaySeq(100);
					repayments.add(repayment);
				}
			}
		}

		overdueList = latePayMarkingService.calPDOnBackDatePayment(financeMain, overdueList, valueDate, schdList,
				repayments, resetReq, true);

		logger.debug("Leaving");
		return overdueList;
	}

	/**
	 * Method for Fetch Overdue Penalty details as per passing Value Date
	 * 
	 * @param finScheduleData
	 * @param receiptData
	 * @param receiptData
	 * @param valueDate
	 * @return
	 */
	public List<FinODDetails> calCurDatePenalties(FinScheduleData finScheduleData, FinReceiptData receiptData,
			Date valueDate) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();

		List<FinODDetails> overdueList = getFinODDetailsDAO().getFinODBalByFinRef(financeMain.getFinReference());
		if (overdueList == null || overdueList.isEmpty()) {
			logger.debug("Leaving");
			return overdueList;
		}

		List<FinanceScheduleDetail> schdList = finScheduleData.getFinanceScheduleDetails();
		List<FinanceRepayments> repayments = financeRepaymentsDAO.getFinRepayListByFinRef(financeMain.getFinReference(),
				false, "");

		// recreate the od as per allocated.
		for (FinODDetails fod : overdueList) {
			BigDecimal penalty = getPenaltyPaid(fod.getFinODSchdDate(), receiptData);
			fod.setTotPenaltyPaid(fod.getTotPenaltyPaid().subtract(penalty));
			fod.setTotPenaltyBal(fod.getTotPenaltyAmt().subtract(fod.getTotWaived()).subtract(fod.getTotPenaltyPaid()));
		}

		overdueList = latePayMarkingService.calPDOnBackDatePayment(financeMain, overdueList, valueDate, schdList,
				repayments, true, true);

		/*
		 * for (FinODDetails fod : overdueList) { BigDecimal penalty = getPenaltyPaid(fod.getFinODSchdDate(),
		 * receiptData); fod.setTotPenaltyPaid(fod.getTotPenaltyPaid().add(penalty));
		 * fod.setTotPenaltyBal(fod.getTotPenaltyAmt().subtract(fod.getTotWaived()).subtract(fod.getTotPenaltyPaid()));
		 * }
		 */

		logger.debug("Leaving");
		return overdueList;
	}

	private BigDecimal getPenaltyPaid(Date schDate, FinReceiptData receiptData) {
		List<FinReceiptDetail> receiptDetailList = receiptData.getReceiptHeader().getReceiptDetails();
		BigDecimal penaltypaidNow = BigDecimal.ZERO;
		for (FinReceiptDetail finReceiptDetail : receiptDetailList) {
			FinRepayHeader rpyheader = finReceiptDetail.getRepayHeader();
			List<RepayScheduleDetail> repaysch = rpyheader.getRepayScheduleDetails();
			if (repaysch != null) {
				for (RepayScheduleDetail repayScheduleDetail : repaysch) {
					if (DateUtility.compare(repayScheduleDetail.getSchDate(), schDate) == 0) {
						penaltypaidNow = penaltypaidNow.add(repayScheduleDetail.getPenaltyPayNow());
					}
				}
			}
		}
		return penaltypaidNow;
	}

	/**
	 * Method for Fetch Overdue Penalty details as per passing Value Date
	 * 
	 * @param finScheduleData
	 * @param receiptData
	 * @param valueDate
	 * @return
	 */
	public List<FinODDetails> calculateODDetails(FinScheduleData finScheduleData, List<FinODDetails> overdueList,
			BigDecimal orgReceiptAmount, Date valueDate, List<FinanceRepayments> finRepayments, boolean resetReq) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();
		if (overdueList == null || overdueList.isEmpty()) {
			logger.debug("Leaving");
			return overdueList;
		}

		// Repayment Details
		List<FinanceRepayments> repayments = new ArrayList<>();
		if (finRepayments != null && !finRepayments.isEmpty()) {
			repayments = finRepayments;
		} else {
			repayments = financeRepaymentsDAO.getFinRepayListByFinRef(financeMain.getFinReference(), false, "");
		}
		BigDecimal totReceiptAmt = orgReceiptAmount;

		// Newly Paid Amount Repayment Details
		List<FinanceScheduleDetail> schdList = finScheduleData.getFinanceScheduleDetails();
		if (totReceiptAmt.compareTo(BigDecimal.ZERO) > 0) {
			char[] rpyOrder = finScheduleData.getFinanceType().getRpyHierarchy().replace("CS", "C").toCharArray();
			FinanceScheduleDetail curSchd = null;
			for (int i = 0; i < schdList.size(); i++) {
				curSchd = schdList.get(i);
				if (curSchd.getSchDate().compareTo(valueDate) > 0) {
					break;
				}

				if (totReceiptAmt.compareTo(BigDecimal.ZERO) == 0) {
					break;
				}

				if ((curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid())).compareTo(BigDecimal.ZERO) > 0
						|| (curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()))
								.compareTo(BigDecimal.ZERO) > 0
						|| (curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid())).compareTo(BigDecimal.ZERO) > 0) {

					FinanceRepayments repayment = new FinanceRepayments();
					repayment.setFinValueDate(valueDate);
					repayment.setFinRpyFor(FinanceConstants.SCH_TYPE_SCHEDULE);
					repayment.setFinSchdDate(curSchd.getSchDate());
					repayment.setFinRpyAmount(orgReceiptAmount);

					for (int j = 0; j < rpyOrder.length; j++) {

						char repayTo = rpyOrder[j];
						if (repayTo == RepayConstants.REPAY_PENALTY) {
							continue;
						}
						BigDecimal balAmount = BigDecimal.ZERO;
						if (repayTo == RepayConstants.REPAY_PRINCIPAL) {
							balAmount = curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid());
							if (totReceiptAmt.compareTo(balAmount) < 0) {
								balAmount = totReceiptAmt;
							}
							repayment.setFinSchdPriPaid(balAmount);
							totReceiptAmt = totReceiptAmt.subtract(balAmount);

						} else if (repayTo == RepayConstants.REPAY_PROFIT) {

							balAmount = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
							if (totReceiptAmt.compareTo(balAmount) < 0) {
								balAmount = totReceiptAmt;
							}
							repayment.setFinSchdPftPaid(balAmount);
							totReceiptAmt = totReceiptAmt.subtract(balAmount);

						} else if (repayTo == RepayConstants.REPAY_FEE) {

							balAmount = curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid());
							if (totReceiptAmt.compareTo(balAmount) < 0) {
								balAmount = totReceiptAmt;
							}
							repayment.setSchdFeePaid(balAmount);
							totReceiptAmt = totReceiptAmt.subtract(balAmount);
						}

					}
					repayment.setFinTotSchdPaid(repayment.getFinSchdPftPaid().add(repayment.getFinSchdPriPaid()));
					repayment.setFinType(financeMain.getFinType());
					repayment.setFinBranch(financeMain.getFinBranch());
					repayment.setFinCustID(financeMain.getCustID());
					repayment.setFinPaySeq(100);
					repayments.add(repayment);
				}
			}
		}

		overdueList = latePayMarkingService.calPDOnBackDatePayment(financeMain, overdueList, valueDate, schdList,
				repayments, resetReq, true);

		logger.debug("Leaving");
		return overdueList;
	}

	@Override
	public ErrorDetail doInstrumentValidation(FinReceiptData receiptData) {
		FinanceMain finMain = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		String payMode = rch.getReceiptMode();

		BigDecimal amount = rch.getReceiptAmount().subtract(receiptData.getExcessAvailable());
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			return null;
		}

		if (StringUtils.equals(RepayConstants.RECEIPTMODE_ONLINE, rch.getReceiptMode())) {
			payMode = rch.getSubReceiptMode();
		}
		InstrumentwiseLimit instrumentwiseLimit = instrumentwiseLimitDAO.getInstrumentWiseModeLimit(payMode, "");

		if (instrumentwiseLimit != null) {
			BigDecimal perDayAmt = finReceiptDetailDAO.getReceiptAmountPerDay(SysParamUtil.getAppDate(), payMode,
					finMain.getCustID());
			if (amount.compareTo(instrumentwiseLimit.getReceiptMinAmtperTran()) < 0
					|| amount.compareTo(instrumentwiseLimit.getReceiptMaxAmtperTran()) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = PennantApplicationUtil.amountFormate(instrumentwiseLimit.getReceiptMaxAmtperTran(),
						PennantConstants.defaultCCYDecPos);
				valueParm[1] = PennantApplicationUtil.amountFormate(instrumentwiseLimit.getReceiptMinAmtperTran(),
						PennantConstants.defaultCCYDecPos);
				return ErrorUtil.getErrorDetail(new ErrorDetail("RU0024", "", valueParm));
			}
			BigDecimal dayAmt = BigDecimal.ZERO;
			if (receiptData.isEnquiry()) {
				dayAmt = perDayAmt.add(amount);
			} else {
				dayAmt = perDayAmt;
			}
			if (dayAmt.compareTo(instrumentwiseLimit.getReceiptMaxAmtperDay()) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = PennantApplicationUtil.amountFormate(instrumentwiseLimit.getReceiptMaxAmtperTran(),
						PennantConstants.defaultCCYDecPos);
				valueParm[1] = StringUtils.isEmpty(rch.getSubReceiptMode()) ? rch.getSubReceiptMode()
						: rch.getReceiptMode();
				return ErrorUtil.getErrorDetail(new ErrorDetail("IWL0004", valueParm));
			}
		}
		return null;
	}

	/**
	 * Method for validate Receipt details
	 * 
	 * @param finServiceInstruction
	 * @param method
	 * @return AuditDetail
	 */
	@Override
	public FinReceiptData doReceiptValidations(FinanceDetail financeDetail, String method) {

		logger.debug("Entering");
		FinReceiptData receiptData = new FinReceiptData();
		receiptData.setFinanceDetail(financeDetail);
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinServiceInstruction fsi = toUpperCase(finScheduleData.getFinServiceInstruction());
		String finReference = fsi.getFinReference();
		String receiptPurpose = fsi.getReceiptPurpose();
		String parm1 = null;
		String eventCode = null;
		BigDecimal amount = new BigDecimal(
				PennantApplicationUtil.amountFormate(fsi.getAmount(), 2).replaceAll(",", ""));

		int methodCtg = receiptCalculator.setReceiptCategory(method);
		if (methodCtg == 0) {
			eventCode = AccountEventConstants.ACCEVENT_REPAY;
		} else if (methodCtg == 1) {
			eventCode = AccountEventConstants.ACCEVENT_EARLYPAY;
		} else if (methodCtg == 2) {
			eventCode = AccountEventConstants.ACCEVENT_EARLYSTL;
		}

		// FIXME: Temporary Fix for API
		String receiptMode = fsi.getPaymentMode();
		// if (!fsi.isReceiptUpload()) {
		if (StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_EXPERIA)
				|| StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_IMPS)
				|| StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_NEFT)
				|| StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_RTGS)
				|| StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_BILLDESK)
				|| StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_PAYU)
				|| StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_PAYTM)
				|| StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_PORTAL)) {
			fsi.setPaymentMode(RepayConstants.RECEIPTMODE_ONLINE);
			fsi.setSubReceiptMode(receiptMode);
			if (StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_PORTAL)
					|| StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_BILLDESK)) {
				fsi.setReceiptChannel(DisbursementConstants.RECEIPT_CHANNEL_POR);
			}
		} else if (StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_CASH)
				|| StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_CHEQUE)
				|| StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_DD)) {
			fsi.setReceiptChannel("OTC");
		}
		if (StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_MOBILE)) {
			fsi.setPaymentMode(RepayConstants.RECEIPTMODE_MOBILE);
		}
		// }

		// set Default date formats
		setDefaultDateFormats(fsi);
		// closed loan
		if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)
				|| StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {

			boolean finactive = financeMainDAO.isFinActive(StringUtils.trimToEmpty(finReference));
			if (!finactive) {
				finScheduleData = setErrorToFSD(finScheduleData, "RU0049", null);
				return receiptData;
			}
		}

		if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
			if (fsi.getEarlySettlementReason() != null && fsi.getEarlySettlementReason() > 0) {
				ReasonCode reasonCode = reasonCodeDAO.getReasonCode(fsi.getEarlySettlementReason(), "_AView");
				if (reasonCode == null) {
					finScheduleData = setErrorToFSD(finScheduleData, "90501", "earlySettlementReason");
					return receiptData;
				} else {
					if (!StringUtils.endsWithIgnoreCase(reasonCode.getReasonTypeCode(),
							PennantConstants.REASON_CODE_EARLYSETTLEMENT)) {
						finScheduleData = setErrorToFSD(finScheduleData, "90501", "earlySettlementReason");
						return receiptData;
					}
				}
			}
		}
		if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)
				|| StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)) {

			boolean initiated = isEarlySettlementInitiated(StringUtils.trimToEmpty(finReference));
			if (initiated) {
				finScheduleData = setErrorToFSD(finScheduleData, "90498", null);
				return receiptData;
			}
		}

		if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)
				|| StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {

			boolean initiated = isPartialSettlementInitiated(StringUtils.trimToEmpty(finReference));
			if (initiated) {
				setErrorToFSD(finScheduleData, "90499", null);
				return receiptData;
			}

		}

		if (StringUtils.isBlank(finReference)) {
			setErrorToFSD(finScheduleData, "90502", "Loan Reference");
			return receiptData;
		}

		// Do First level Validation for Upload
		receiptData = doBasicValidations(receiptData, methodCtg);

		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			logger.debug("Leaving - Basic Validations Error");
			return receiptData;
		}

		int count = financeMainDAO.getCountByBlockedFinances(finReference);
		if (count > 0) {
			parm1 = "FinReference: " + finReference;
			finScheduleData = financeDetail.getFinScheduleData();
			finScheduleData = setErrorToFSD(finScheduleData, "90204", receiptPurpose, parm1);
			financeDetail.setFinScheduleData(finScheduleData);
			return receiptData;
		}

		// RECEIPT UPLOAD INQUIRY or API/Receipt Upload Post
		if (fsi.isReceiptUpload() && !StringUtils.equals(fsi.getReqType(), "Post")) {
			FinanceMain financeMain = financeMainDAO.getFinanceMainById(finReference, "_AView", false);
			finScheduleData.setFinanceMain(financeMain);
		} else {
			Cloner cloner = new Cloner();
			FinServiceInstruction tempFsi = cloner.deepClone(finScheduleData.getFinServiceInstruction());
			FinReceiptHeader rch = cloner.deepClone(receiptData.getReceiptHeader());
			receiptData = getFinReceiptDataById(finReference, eventCode, FinanceConstants.FINSER_EVENT_RECEIPT, "");

			if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
				logger.debug("Leaving");
				return receiptData;
			}
			financeDetail = receiptData.getFinanceDetail();
			receiptData.setReceiptHeader(rch);
			finScheduleData = financeDetail.getFinScheduleData();
			finScheduleData.setFinServiceInstruction(tempFsi);
		}
		Date valueDate = fsi.getValueDate();
		if (fsi.getReceiptPurpose().equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE) && fsi.isReceiptUpload()
				&& !StringUtils.equals(fsi.getReqType(), "Post")) {
			FinReceiptDetail rcd = fsi.getReceiptDetail();
			FinScheduleData fsd = financeDetail.getFinScheduleData();
			FinanceMain finMain = fsd.getFinanceMain();
			if (!finMain.isFinIsActive()) {
				fsi.setExcessAdjustTo(RepayConstants.EXAMOUNTTYPE_EXCESS);
			}
			receiptData = getFinReceiptDataById(finReference, eventCode, FinanceConstants.FINSER_EVENT_RECEIPT, "");
			FinReceiptHeader rch = receiptData.getReceiptHeader();
			rch.setReference(finReference);
			rch.setReceiptAmount(amount);
			rch.setReceiptPurpose(receiptPurpose);
			if (StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_CHEQUE)
					|| StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_DD)) {
				int defaultClearingDays = SysParamUtil.getValueAsInt("EARLYSETTLE_CHQ_DFT_DAYS");
				fsi.setValueDate(DateUtility.addDays(fsi.getReceivedDate(), defaultClearingDays));
			}
			rch.setReceiptDate(fsi.getReceivedDate());
			rch.setValueDate(fsi.getValueDate());
			rcd.setValueDate(fsi.getValueDate());
			rcd.setReceivedDate(fsi.getReceivedDate());
			receiptData.setReceiptHeader(rch);
			receiptData = calcuateDues(receiptData);
			if (receiptData != null) {
				BigDecimal pastDues = rch.getTotalPastDues().getTotalDue();
				BigDecimal totalBounces = rch.getTotalBounces().getTotalDue();
				BigDecimal totalRcvAdvises = rch.getTotalRcvAdvises().getTotalDue();
				BigDecimal totalFees = rch.getTotalFees().getTotalDue();
				BigDecimal excessAvailable = receiptData.getExcessAvailable();
				BigDecimal totalDues = pastDues.add(totalBounces).add(totalRcvAdvises).add(totalFees)
						.subtract(excessAvailable);
				int finFormatter = CurrencyUtil
						.getFormat(receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
				totalDues = PennantApplicationUtil.formateAmount(totalDues, finFormatter);
				if (fsi.getReceiptPurpose().equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE)
						&& fsi.isReceiptUpload()) {
					if (totalDues.compareTo(amount) > 0) {
						finScheduleData = setErrorToFSD(finScheduleData, "RU0051", null);
						receiptData.getFinanceDetail().setFinScheduleData(finScheduleData);
						return receiptData;
					}
				}
			}
			receiptData.setFinanceDetail(financeDetail);
		}
		fsi.setValueDate(valueDate);

		receiptData = doDataValidations(receiptData, methodCtg);
		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			logger.debug("Leaving - Data Validations Error");
			return receiptData;
		}
		// receiptData = doBusinessValidations(receiptData, methodCtg);
		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			logger.debug("Leaving - Business Validations Error");
			return receiptData;
		}

		//Dedup Check
		finScheduleData.setErrorDetails(dedupCheck(fsi));
		if (CollectionUtils.isNotEmpty(finScheduleData.getErrorDetails())) {
			logger.info("Dedup Validation Failed.");
			return receiptData;
		}

		if (fsi.isReceiptUpload() && !StringUtils.equals(fsi.getReqType(), "Post")) {
			logger.debug("Leaving");
			return receiptData;
		}

		receiptData = doFunctionalValidations(receiptData, methodCtg);
		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			logger.debug("Leaving - Functional Validations Error");
			return receiptData;
		}

		logger.debug("Leaving");
		return receiptData;

	}

	public FinServiceInstruction toUpperCase(FinServiceInstruction fsi) {
		if (StringUtils.isNotBlank(fsi.getPaymentMode())) {
			fsi.setPaymentMode(fsi.getPaymentMode().toUpperCase());
		}

		if (StringUtils.isNotBlank(fsi.getAllocationType())) {
			fsi.setAllocationType(fsi.getAllocationType().toUpperCase());
		}

		if (StringUtils.isNotBlank(fsi.getStatus())) {
			fsi.setStatus(fsi.getStatus().toUpperCase());
		}

		if (StringUtils.isNotBlank(fsi.getRecalType())) {
			fsi.setRecalType(fsi.getRecalType().toUpperCase());
		}

		if (StringUtils.isNotBlank(fsi.getExcessAdjustTo())) {
			fsi.setExcessAdjustTo(fsi.getExcessAdjustTo().toUpperCase());
		}

		if (StringUtils.isNotBlank(fsi.getSubReceiptMode())) {
			fsi.setSubReceiptMode(fsi.getSubReceiptMode().toUpperCase());
		}

		if (StringUtils.isNotBlank(fsi.getReceiptChannel())) {
			fsi.setReceiptChannel(fsi.getReceiptChannel());
		}

		if (StringUtils.isNotBlank(fsi.getReceivedFrom())) {
			fsi.setReceivedFrom(fsi.getReceivedFrom().toUpperCase());
		}

		if (StringUtils.isNotBlank(fsi.getPanNumber())) {
			fsi.setPanNumber(fsi.getPanNumber().toUpperCase());
		}

		return fsi;
	}

	@Override
	public FinReceiptData doBasicValidations(FinReceiptData receiptData, int methodCtg) {
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinServiceInstruction fsi = finScheduleData.getFinServiceInstruction();

		String receiptMode = fsi.getPaymentMode();
		String subReceiptMode = fsi.getSubReceiptMode();
		String receiptChannel = fsi.getReceiptChannel();

		String instructstatus = fsi.getStatus();
		String allocationType = fsi.getAllocationType();
		String excessAdjustTo = fsi.getExcessAdjustTo();
		String parm0 = null;
		String parm1 = null;
		boolean autoReceipt = false;

		Date appDate = SysParamUtil.getAppDate();

		if (financeDetail.getFinScheduleData().getFinPftDeatil() != null
				&& StringUtils.equals(FinanceConstants.PRODUCT_CD,
						financeDetail.getFinScheduleData().getFinPftDeatil().getFinCategory())
				&& (receiptMode.equals(RepayConstants.RECEIPTMODE_PAYABLE))) {
			autoReceipt = true;
		}

		// Valid Receipt Mode
		if (!StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_ONLINE)
				&& !StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_CHEQUE)
				&& !StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_DD)
				&& !StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_CASH)
				&& !StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_MOBILE) && !autoReceipt) {

			parm0 = "Receipt mode";
			parm1 = RepayConstants.RECEIPTMODE_CASH + "," + RepayConstants.RECEIPTMODE_CHEQUE + ","
					+ RepayConstants.RECEIPTMODE_DD + "," + RepayConstants.RECEIPTMODE_ONLINE + ","
					+ RepayConstants.RECEIPTMODE_MOBILE;

			finScheduleData = setErrorToFSD(finScheduleData, "90281", parm0, parm1);
			return receiptData;
		}

		// Channel
		if (StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_CHEQUE)
				|| StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_DD)
				|| StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_CASH)) {
			if (!StringUtils.equals(receiptChannel, "OTC") && !StringUtils.equals(receiptChannel, "MOB")) {
				parm0 = "Channel";
				parm1 = "OTC / MOB";

				finScheduleData = setErrorToFSD(finScheduleData, "90281", parm0, parm1);
				return receiptData;
			}
		}

		// Sub Receipt Sub Mode
		if (StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_ONLINE)) {
			if (!StringUtils.equals(subReceiptMode, RepayConstants.RECEIPTMODE_EXPERIA)
					&& !StringUtils.equals(subReceiptMode, RepayConstants.RECEIPTMODE_IMPS)
					&& !StringUtils.equals(subReceiptMode, RepayConstants.RECEIPTMODE_NEFT)
					&& !StringUtils.equals(subReceiptMode, RepayConstants.RECEIPTMODE_RTGS)
					&& !StringUtils.equals(subReceiptMode, RepayConstants.RECEIPTMODE_EXPERIA)
					&& !StringUtils.equals(subReceiptMode, RepayConstants.RECEIPTMODE_BILLDESK)
					&& !StringUtils.equals(subReceiptMode, RepayConstants.RECEIPTMODE_ESCROW)
					&& !StringUtils.equals(subReceiptMode, RepayConstants.RECEIPTMODE_PAYU)
					&& !StringUtils.equals(subReceiptMode, RepayConstants.RECEIPTMODE_PAYTM)
					&& !StringUtils.equals(subReceiptMode, RepayConstants.RECEIPTMODE_PORTAL)) {

				parm0 = "Sub Receipt Mode";
				parm1 = RepayConstants.RECEIPTMODE_IMPS + ", " + RepayConstants.RECEIPTMODE_NEFT + ", "
						+ RepayConstants.RECEIPTMODE_RTGS + ", " + RepayConstants.RECEIPTMODE_EXPERIA + ", "
						+ RepayConstants.RECEIPTMODE_ESCROW + ", " + RepayConstants.RECEIPTMODE_PAYU + ", "
						+ RepayConstants.RECEIPTMODE_PAYTM + ", " + RepayConstants.RECEIPTMODE_PORTAL;
				finScheduleData = setErrorToFSD(finScheduleData, "90281", parm0, parm1);
				return receiptData;
			}
		}

		// Allocation Type
		if (StringUtils.isBlank(allocationType)) {
			allocationType = "A";
			fsi.setAllocationType(allocationType);
		} else {
			if (!StringUtils.equals(allocationType, "A") && !StringUtils.equals(allocationType, "M")) {
				finScheduleData = setErrorToFSD(finScheduleData, "90281", "Allocation Type", "A/M");
				return receiptData;
			}
		}

		// Excess Adjust To
		if (StringUtils.isBlank(excessAdjustTo)) {
			if (methodCtg == 0 || methodCtg == 2) {
				fsi.setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_EXCESS);
			} else {
				fsi.setExcessAdjustTo(PennantConstants.List_Select);
			}
			excessAdjustTo = fsi.getExcessAdjustTo();
		} else {
			if (!StringUtils.equals(excessAdjustTo, RepayConstants.EXCESSADJUSTTO_EXCESS)
					&& !StringUtils.equals(excessAdjustTo, RepayConstants.EXCESSADJUSTTO_EMIINADV)
					&& !StringUtils.equals(excessAdjustTo, PennantConstants.List_Select)) {
				finScheduleData = setErrorToFSD(finScheduleData, "90281", "Excess Adjustment", "E/A");
				return receiptData;
			}
		}

		// Set Receipt Detail Record
		receiptData = setReceiptDetail(receiptData);
		FinReceiptDetail rcd = receiptData.getReceiptHeader().getReceiptDetails().get(0);
		if (rcd == null) {
			finScheduleData = setErrorToFSD(finScheduleData, "90502", "Receipt Details");
			return receiptData;
		}

		if (fsi.isReceiptUpload()) {
			if (fsi.getValueDate() == null) {
				finScheduleData = setErrorToFSD(finScheduleData, "90502", "ValueDate");
				return receiptData;
			}

			/*
			 * if (!(fsi.getValueDate().compareTo(fsi.getReceivedDate()) == 0)) { finScheduleData =
			 * setErrorToFSD(finScheduleData, "RU0047", "[VALUEDATE] [RECEIVEDDATE]"); return receiptData; }
			 */
			if (StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_CHEQUE)
					|| StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_DD)) {
				if (fsi.getDepositDate() == null) {
					finScheduleData = setErrorToFSD(finScheduleData, "90502", "DepositDate");
					return receiptData;

				}
				if (!(fsi.getValueDate().compareTo(fsi.getDepositDate()) == 0)) {
					finScheduleData = setErrorToFSD(finScheduleData, "RU0017", null);
					return receiptData;

				}
				if (fsi.getReceivedDate().compareTo(fsi.getDepositDate()) > 0) {
					finScheduleData = setErrorToFSD(finScheduleData, "RU0018", null);
					return receiptData;

				}
			}

		}
		// Funding account is mandatory for all modes
		if (!StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_CASH)
				&& !StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_CHEQUE) && rcd.getFundingAc() <= 0) {
			finScheduleData = setErrorToFSD(finScheduleData, "90502", "Funding Account");
			return receiptData;
		} else if (ImplementationConstants.ALLOW_PARTNERBANK_FOR_RECEIPTS_IN_CASHMODE
				&& StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_CASH) && rcd.getFundingAc() <= 0) {
			finScheduleData = setErrorToFSD(finScheduleData, "90502", "Funding Account");
			return receiptData;
		}

		// Cheque OR DD
		if (StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_CHEQUE)
				|| StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_DD)) {
			rcd.setFavourName(rcd.getTransactionRef());
			finScheduleData = validateForChequeOrDD(rcd, finScheduleData);
		} else if (StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_CASH)
				|| StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_ONLINE)) {
			// CASH OR ONLINE
			// finScheduleData = validateForNonChequeOrDD(rcd, finScheduleData);
		}

		if (StringUtils.equals(fsi.getReqType(), RepayConstants.REQTYPE_INQUIRY)) {
			return receiptData;
		}

		// Transaction Reference mandatory for all non CASH modes
		if (!StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_CASH)
				&& !StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_CHEQUE)
				&& !StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_DD) && !autoReceipt) {
			if (StringUtils.isBlank(rcd.getTransactionRef())) {
				finScheduleData = setErrorToFSD(finScheduleData, "90502", "Transaction Reference");
				return receiptData;
			}
		}

		// Received From
		if (StringUtils.isBlank(fsi.getReceivedFrom())) {
			fsi.setReceivedFrom(RepayConstants.RECEIVED_CUSTOMER);
		} else {
			if (!StringUtils.equalsIgnoreCase(fsi.getReceivedFrom(), RepayConstants.RECEIVED_CUSTOMER)
					&& !StringUtils.equalsIgnoreCase(fsi.getReceivedFrom(), RepayConstants.RECEIVED_GOVT)) {
				parm0 = "Received From";
				parm1 = RepayConstants.RECEIVED_CUSTOMER + "," + RepayConstants.RECEIVED_GOVT;
				finScheduleData = setErrorToFSD(finScheduleData, "90281", parm0, parm1);
				return receiptData;
			}
		}

		if (methodCtg == 2) {
			Date waiverDate = getLastWaiverDate(financeDetail.getFinReference(), appDate, fsi.getValueDate());
			if (waiverDate != null) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtil.formatToLongDate(appDate);
				valueParm[1] = DateUtil.formatToLongDate(waiverDate);

				finScheduleData = setErrorToFSD(finScheduleData, "RU0099", valueParm[0], valueParm[1]);
				return receiptData;
			}
		}
		receiptData.getReceiptHeader().setReceivedFrom(fsi.getReceivedFrom());

		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			return receiptData;
		}

		receiptData = validateBasicAllocations(receiptData, methodCtg);
		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			return receiptData;
		}

		// =======================================================================
		// Receipt Upload Related Code
		// =======================================================================

		if (!fsi.isReceiptUpload()) {
			return receiptData;
		}

		if (StringUtils.isBlank(instructstatus)) {
			finScheduleData = setErrorToFSD(finScheduleData, "90502", "Status");
			return receiptData;
		} else if (!StringUtils.equals(instructstatus, RepayConstants.PAYSTATUS_APPROVED)
				&& !StringUtils.equals(instructstatus, RepayConstants.PAYSTATUS_REALIZED)) {
			parm1 = RepayConstants.PAYSTATUS_APPROVED + "," + RepayConstants.PAYSTATUS_REALIZED;
			finScheduleData = setErrorToFSD(finScheduleData, "90298", "Status", parm1);
			return receiptData;
		} else if (!StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_DD)
				&& !StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_CHEQUE)) {

			if (StringUtils.equals(instructstatus, RepayConstants.PAYSTATUS_REALIZED)) {
				parm1 = RepayConstants.PAYSTATUS_APPROVED;
				finScheduleData = setErrorToFSD(finScheduleData, "90298", "Status", parm1);
				return receiptData;
			}
		}

		return receiptData;
	}

	@Override
	public FinReceiptData doDataValidations(FinReceiptData receiptData, int methodCtg) {
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinServiceInstruction fsi = finScheduleData.getFinServiceInstruction();
		FinanceMain financeMain = finScheduleData.getFinanceMain();

		// FinanceMain is Null. No Loans Available
		if (financeMain == null) {
			finScheduleData = setErrorToFSD(finScheduleData, "90201", fsi.getFinReference());
			return receiptData;
		}
		boolean autoReceipt = false;
		if (financeDetail.getFinScheduleData().getFinPftDeatil() != null
				&& StringUtils.equals(FinanceConstants.PRODUCT_CD, financeMain.getFinCategory())
				&& (fsi.getPaymentMode().equals(RepayConstants.RECEIPTMODE_PAYABLE))) {
			autoReceipt = true;
		}
		// Partner Bank Validation against Loan Type, If not exists
		long fundingAccount = fsi.getReceiptDetail().getFundingAc();
		fsi.setFundingAc(fundingAccount);
		String receiptMode = fsi.getPaymentMode();

		if (StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_ONLINE)) {
			receiptMode = fsi.getSubReceiptMode();
		}
		if (!StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_CASH)
				&& !StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_CHEQUE)
				|| (ImplementationConstants.ALLOW_PARTNERBANK_FOR_RECEIPTS_IN_CASHMODE
						&& StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_CASH))) {
			int count = finTypePartnerBankDAO.getPartnerBankCount(financeMain.getFinType(), receiptMode,
					AccountConstants.PARTNERSBANK_RECEIPTS, fundingAccount);
			if (count <= 0 && !autoReceipt) {
				finScheduleData = setErrorToFSD(finScheduleData, "RU0020", null);
				return receiptData;
			}
		}
		Date finStartDate = financeMain.getFinStartDate();

		if (fsi.isReceiptUpload() && fsi.getValueDate().compareTo(finStartDate) < 0) {
			finScheduleData = setErrorToFSD(finScheduleData, "RU0048", null);
			return receiptData;
		}

		/*
		 * if (StringUtils.equals(fsi.getReqType(), RepayConstants.REQTYPE_INQUIRY)) { return receiptData; }
		 */

		// Entity Mismatch
		/*
		 * if (StringUtils.equals(financeMain.getEntityCode(), fsi.getEntity())) { finScheduleData =
		 * setErrorToFSD(finScheduleData, "RU004", fsi.getFinReference()); return receiptData; }
		 */

		if (StringUtils.isBlank(fsi.getEntity())) {
			fsi.setEntity(financeMain.getEntityCode());
		}

		// FinanceConstants.PRODUCT_GOLD
		if (StringUtils.isNotBlank(financeMain.getProductCategory())) {
			if (StringUtils.equals(financeMain.getProductCategory(), FinanceConstants.PRODUCT_GOLD)) {
				finScheduleData = setErrorToFSD(finScheduleData, "RU0002", null);
				return receiptData;
			}
		}

		receiptData = validateDual(receiptData, methodCtg);

		return receiptData;
	}

	/**
	 * Method for validate Receipt details
	 * 
	 * @param finServiceInstruction
	 * @param method
	 * @return FinanceDetail
	 */
	@Override
	public FinReceiptData doFunctionalValidations(FinReceiptData receiptData, int methodCtg) {
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinServiceInstruction fsi = finScheduleData.getFinServiceInstruction();
		FinReceiptDetail rcd = fsi.getReceiptDetail();

		String finReference = fsi.getFinReference();
		Date appDate = SysParamUtil.getAppDate();
		String receiptMode = fsi.getPaymentMode();
		String subReceiptMode = fsi.getSubReceiptMode();
		String allocationType = fsi.getAllocationType();
		String excessAdjustTo = fsi.getExcessAdjustTo();
		String parm0 = null;
		String parm1 = null;

		receiptData = validateRecalType(receiptData, methodCtg);
		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			logger.debug("Leaving");
			return receiptData;
		}

		Date fromDate = fsi.getFromDate();
		if (fromDate == null) {
			fromDate = appDate;
			fsi.setFromDate(fromDate);
		}

		// FIXME: PV ExtendedFieldDetails Validation - Needs relook
		List<ExtendedField> apiExtendedFields = fsi.getExtendedDetails();
		List<ErrorDetail> errorDetailList = null;
		if (apiExtendedFields != null && apiExtendedFields.size() > 0) {
			String subModule = financeDetail.getFinScheduleData().getFinanceType().getFinCategory();
			errorDetailList = extendedFieldDetailsService.validateExtendedFieldDetails(apiExtendedFields,
					ExtendedFieldConstants.MODULE_LOAN, subModule, FinanceConstants.FINSER_EVENT_RECEIPT);
			finScheduleData.setErrorDetails(errorDetailList);
			return receiptData;
		}

		// Excess Adjust To
		if (methodCtg == 0 || methodCtg == 2) {
			if (StringUtils.isBlank(excessAdjustTo)) {
				finScheduleData = setErrorToFSD(finScheduleData, "90502", "Excess Adjust to ");
				return receiptData;
			}

			if (!StringUtils.equals(excessAdjustTo, RepayConstants.EXAMOUNTTYPE_EXCESS)
					&& !StringUtils.equals(excessAdjustTo, RepayConstants.EXAMOUNTTYPE_EMIINADV)) {
				parm0 = "Excess Adjust to " + excessAdjustTo;
				parm1 = RepayConstants.EXAMOUNTTYPE_EXCESS + "," + RepayConstants.EXAMOUNTTYPE_EMIINADV;
				finScheduleData = setErrorToFSD(finScheduleData, "90337", parm0, parm1);
				return receiptData;
			}

			if (methodCtg == 2 && !StringUtils.equals(excessAdjustTo, RepayConstants.EXAMOUNTTYPE_EXCESS)) {
				parm0 = "Excess Adjust to " + excessAdjustTo;
				parm1 = RepayConstants.EXAMOUNTTYPE_EXCESS;
				finScheduleData = setErrorToFSD(finScheduleData, "90337", parm0, parm1);
				return receiptData;
			}
		}

		// Allocation Type
		if (!allocationType.equals(RepayConstants.ALLOCATIONTYPE_MANUAL)
				&& !allocationType.equals(RepayConstants.ALLOCATIONTYPE_AUTO)) {
			parm0 = "Allocation Type : " + fsi.getAllocationType();
			parm1 = RepayConstants.ALLOCATIONTYPE_MANUAL + "," + RepayConstants.ALLOCATIONTYPE_AUTO;
			finScheduleData = setErrorToFSD(finScheduleData, "90337", parm0, parm1);
			return receiptData;
		} else if (!allocationType.equals(RepayConstants.ALLOCATIONTYPE_AUTO) && (methodCtg == 1)) {
			parm0 = "Allocation Type: " + fsi.getAllocationType();
			parm1 = RepayConstants.ALLOCATIONTYPE_AUTO;
			finScheduleData = setErrorToFSD(finScheduleData, "90337", parm0, parm1);
			return receiptData;
		} else if (fsi.isReceiptResponse() && fsi.getAllocationType().equals(RepayConstants.ALLOCATIONTYPE_MANUAL)) {
			parm0 = "Allocation Type: " + fsi.getAllocationType();
			parm1 = RepayConstants.ALLOCATIONTYPE_AUTO;
			finScheduleData = setErrorToFSD(finScheduleData, "90337", parm0, parm1);
			return receiptData;
		}

		// remarks
		if (!StringUtils.isBlank(rcd.getRemarks()) && 100 < rcd.getRemarks().length()) {
			finScheduleData = setErrorToFSD(finScheduleData, "RU0005", null);
			return receiptData;
		}

		// Sub Receipt Mode Mode
		boolean isDeveloperFinance = financeMainDAO.isDeveloperFinance(finReference, "", false);
		if (StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_ONLINE)) {
			if (!isDeveloperFinance && StringUtils.equals(subReceiptMode, RepayConstants.RECEIPTMODE_ESCROW)) {
				parm0 = "Sub Receipt Mode";
				parm1 = RepayConstants.RECEIPTMODE_ESCROW + " Allowed only for developer finance";
				finScheduleData = setErrorToFSD(finScheduleData, "90281", parm0, parm1);
				return receiptData;
			}
		}

		// DE#555: In receipt upload, If the sub receipt mode is ESCROW and receipt mode is ONLINE ,
		// system not allowing to upload. It is allowing only, if loan is related developer finance.
		// Same functionality is working fine in front end screen’s(Receipt maker screen). Now we are removing the
		// validation.

		/*
		 * boolean isDeveloperFinance = financeMainDAO.isDeveloperFinance(finReference, "", false); if
		 * (StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_ONLINE)) { if (!isDeveloperFinance &&
		 * StringUtils.equals(subReceiptMode, RepayConstants.RECEIPTMODE_ESCROW)) { parm0 = "Sub Receipt Mode"; parm1 =
		 * RepayConstants.RECEIPTMODE_ESCROW + " Allowed only for developer finance"; finScheduleData =
		 * setErrorToFSD(finScheduleData, "90281", parm0, parm1); return receiptData; } }
		 */
		// Partial Settlement
		if (methodCtg == 1) {
			if (fsi.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
				parm0 = "Amount:" + fsi.getAmount();
				finScheduleData = setErrorToFSD(finScheduleData, "91121", parm0, "Zero");
				return receiptData;
			}
		}

		logger.debug("Leaving");
		return receiptData;
	}

	@Override
	public FinReceiptData doBusinessValidations(FinReceiptData receiptData, int methodCtg) {
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinServiceInstruction fsi = finScheduleData.getFinServiceInstruction();

		String finReference = fsi.getFinReference();
		String parm0 = null;

		// Validate duplicate record
		if (StringUtils.equals(fsi.getReqType(), "Inquiry")) {
			String txnReference = fsi.getTransactionRef();
			BigDecimal receiptAmount = fsi.getAmount();
			boolean dedupFound = finReceiptDetailDAO.isDuplicateReceipt(finReference, txnReference, receiptAmount);
			if (dedupFound) {
				parm0 = "Txn Reference: " + txnReference + " with Amount";
				finScheduleData = setErrorToFSD(finScheduleData, "90273", parm0);
				return receiptData;
			}
		}

		logger.debug("Leaving");
		return receiptData;
	}

	public FinReceiptData validateBasicAllocations(FinReceiptData receiptData, int methodCtg) {
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinServiceInstruction fsi = finScheduleData.getFinServiceInstruction();
		String excessAdjustTo = fsi.getExcessAdjustTo().toUpperCase();
		List<UploadAlloctionDetail> ulAllocations = fsi.getUploadAllocationDetails();
		BigDecimal receiptAmount = fsi.getAmount();
		BigDecimal allocatedAmount = BigDecimal.ZERO;
		BigDecimal totalWaivedAmt = BigDecimal.ZERO;
		String allocationType = fsi.getAllocationType();
		String parm0 = null;
		String parm1 = null;
		boolean isAllocationFound = false;

		if (ulAllocations != null && !ulAllocations.isEmpty()) {
			isAllocationFound = true;
		}

		if (StringUtils.equals(allocationType, RepayConstants.ALLOCATIONTYPE_AUTO)) {
			if (isAllocationFound) {
				setErrorToFSD(finScheduleData, "RU0028", null);
				return receiptData;
			} else {
				return receiptData;
			}
		}

		// for Manual allocationType for earlySettelment
		if (RepayConstants.ALLOCATIONTYPE_MANUAL.equals(allocationType) && isAllocationFound) {
			for (UploadAlloctionDetail alc : ulAllocations) {
				String allocationType2 = alc.getAllocationType();

				if (!("F".equals(allocationType2) || "M".equals(allocationType2) || "B".equals(allocationType2))) {
					continue;
				}

				if (StringUtils.isBlank(alc.getReferenceCode())) {
					parm0 = "for allocationItem: " + allocationType2 + ", referenceCode is mandatory";
					setErrorToFSD(finScheduleData, "30550", parm0);
					return receiptData;
				}

				if (StringUtils.isNotBlank(alc.getReferenceCode())) {
					FeeType feeType = feeTypeDAO.getApprovedFeeTypeByFeeCode(alc.getReferenceCode());
					if (feeType == null) {
						parm0 = "referenceCode :" + alc.getReferenceCode();
						setErrorToFSD(finScheduleData, "90501", parm0);
						return receiptData;
					}
				}
			}
		}

		if (!StringUtils.equals(allocationType, RepayConstants.ALLOCATIONTYPE_AUTO) && !isAllocationFound) {
			setErrorToFSD(finScheduleData, "90502", "Manual Allocations");
			return receiptData;
		}

		boolean isEMIFound = false;
		boolean isPriOrIntFound = false;
		boolean isExcessFound = false;
		boolean isFuturePayFound = false;
		// Check for Duplicate Allocation Detail
		for (int i = 0; i < ulAllocations.size(); i++) {
			UploadAlloctionDetail alc = ulAllocations.get(i);
			String alcType = alc.getAllocationType();
			String referenceCode = alc.getReferenceCode();
			if (StringUtils.isBlank(alcType)) {
				setErrorToFSD(finScheduleData, "90502", "Allocation Type");
				return receiptData;
			}

			if (StringUtils.equals(excessAdjustTo, RepayConstants.EXCESSADJUSTTO_EXCESS)
					&& StringUtils.equals(alcType, RepayConstants.EXCESSADJUSTTO_EMIINADV)) {
				setErrorToFSD(finScheduleData, "90503", "Allocation Item");
				return receiptData;
			} else if (StringUtils.equals(excessAdjustTo, RepayConstants.EXCESSADJUSTTO_EMIINADV)
					&& StringUtils.equals(alcType, RepayConstants.EXCESSADJUSTTO_EXCESS)) {
				setErrorToFSD(finScheduleData, "90504", "Allocation Item");
				return receiptData;
			}

			if (!StringUtils.equals(alcType, "P") && !StringUtils.equals(alcType, "I")
					&& !StringUtils.equals(alcType, "EM") && !StringUtils.equals(alcType, "L")
					&& !StringUtils.equals(alcType, "O") && !StringUtils.equals(alcType, "F")
					&& !StringUtils.equals(alcType, "M") && !StringUtils.equals(alcType, "B")
					&& !StringUtils.equals(alcType, "FP") && !StringUtils.equals(alcType, "FI")
					&& !StringUtils.equals(alcType, "E") && !StringUtils.equals(alcType, "A")) {
				setErrorToFSD(finScheduleData, "90502", "Allocation Type");
				return receiptData;
			}

			if (alc.getPaidAmount().compareTo(BigDecimal.ZERO) < 0) {
				parm0 = PennantApplicationUtil.amountFormate(allocatedAmount, 2);
				setErrorToFSD(finScheduleData, "RU0034", parm0, alcType);
				return receiptData;
			}

			if (alc.getWaivedAmount().compareTo(BigDecimal.ZERO) < 0) {
				parm0 = PennantApplicationUtil.amountFormate(allocatedAmount, 2);
				setErrorToFSD(finScheduleData, "RU0035", parm0, alcType);
				return receiptData;
			}

			if (methodCtg < 2 && alc.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0) {
				if ((StringUtils.equals(alcType, "P") || StringUtils.equals(alcType, "I")
						|| StringUtils.equals(alcType, "E"))) {
					setErrorToFSD(finScheduleData, "RU0030", "Principal/Interest/EMI");
					return receiptData;
				}
			}

			// Check for duplicate
			for (int j = 0; j < ulAllocations.size(); j++) {
				if (j == i) {
					continue;
				}
				String dupeAlcType = ulAllocations.get(j).getAllocationType();
				String dupeReferenceCode = ulAllocations.get(j).getReferenceCode();
				if ((("F".equals(dupeAlcType) && !StringUtils.equals(referenceCode, dupeReferenceCode))
						|| ("M".equals(dupeAlcType) && !StringUtils.equals(referenceCode, dupeReferenceCode))
						|| ("B".equals(dupeAlcType) && !StringUtils.equals(referenceCode, dupeReferenceCode)))) {
					continue;
				}
				if (StringUtils.equals(alcType, dupeAlcType)) {
					parm0 = "Duplicate Allocations" + dupeAlcType;
					setErrorToFSD(finScheduleData, "90273", parm0);
					return receiptData;
				}
			}

			if (StringUtils.equals(alcType, "EM")) {
				isEMIFound = true;
			} else if (StringUtils.equals(alcType, "P") || StringUtils.equals(alcType, "I")) {
				isPriOrIntFound = true;
			} else if (StringUtils.equals(alcType, "E") || StringUtils.equals(alcType, "A")) {
				isExcessFound = true;
			} else if (StringUtils.equals(alcType, "FP") || StringUtils.equals(alcType, "FI")) {
				isFuturePayFound = true;
			}
		}

		if (isEMIFound && isPriOrIntFound) {
			finScheduleData = setErrorToFSD(finScheduleData, "30511",
					"EMI AND (Principal / Interest) are mutually Exclusive");
			return receiptData;
		}

		if (methodCtg == 1 && isExcessFound) {
			finScheduleData = setErrorToFSD(finScheduleData, "RU0043", null);
			return receiptData;
		}

		if (methodCtg < 2 && isFuturePayFound) {
			finScheduleData = setErrorToFSD(finScheduleData, "RU0044", null);
			return receiptData;
		}

		for (int i = 0; i < ulAllocations.size(); i++) {
			UploadAlloctionDetail alc = ulAllocations.get(i);
			String alcType = alc.getAllocationType();

			if (StringUtils.equals(alcType, "P") || StringUtils.equals(alcType, "I")
					|| StringUtils.equals(alcType, "EM") || StringUtils.equals(alcType, "L")
					|| StringUtils.equals(alcType, "O") || StringUtils.equals(alcType, "F")
					|| StringUtils.equals(alcType, "M") || StringUtils.equals(alcType, "B")
					|| StringUtils.equals(alcType, "FP") || StringUtils.equals(alcType, "FI")
					|| StringUtils.equals(alcType, "E") || StringUtils.equals(alcType, "A")) {
				totalWaivedAmt = totalWaivedAmt.add(alc.getWaivedAmount());
				allocatedAmount = allocatedAmount.add(alc.getPaidAmount().add(alc.getWaivedAmount()));
			}
		}
		allocatedAmount = allocatedAmount.subtract(totalWaivedAmt);
		if (allocatedAmount.compareTo(receiptAmount) != 0 && methodCtg != 2) {
			parm0 = PennantApplicationUtil.amountFormate(receiptAmount, 2);
			parm1 = PennantApplicationUtil.amountFormate(allocatedAmount, 2);

			finScheduleData = setErrorToFSD(finScheduleData, "RU0042", parm0, parm1);
			return receiptData;
		}

		return receiptData;
	}

	// Validations required both at the time of Receipt U/L and Approve
	public FinReceiptData validateDual(FinReceiptData receiptData, int methodCtg) {
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinServiceInstruction fsi = finScheduleData.getFinServiceInstruction();
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		FinanceProfitDetail finPftDetail = finScheduleData.getFinPftDeatil();
		FinReceiptDetail rcd = fsi.getReceiptDetail();
		FinReceiptHeader rch = receiptData.getReceiptHeader();

		if (rch.getReceiptDate() == null) {
			rch.setReceiptDate(rcd.getReceivedDate());
		}

		if (fsi.getValueDate() == null) {
			fsi.setValueDate(rch.getValueDate());
		}

		if (rch.getValueDate() == null) {
			//rch.setValueDate(rcd.getReceivedDate());
			rch.setValueDate(rcd.getValueDate());
		}

		String finReference = fsi.getFinReference();

		Date appDate = SysParamUtil.getAppDate();
		// Date derivedAppDate = DateUtility.getDerivedAppDate();
		// Date fromDate = fsi.getFromDate();
		Date derivedAppDate = appDate;

		String parm0 = null;
		String parm1 = null;
		String parm2 = null;

		receiptData = validateClosingStatus(receiptData, methodCtg);
		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			logger.debug("Leaving - Basic Validations Error");
			return receiptData;
		}

		// validate loan maturity date
		if (methodCtg != 0 && financeMain.getMaturityDate().compareTo(appDate) < 0 && !fsi.isNormalLoanClosure()) {
			if (methodCtg == 1) {
				parm0 = FinanceConstants.FINSER_EVENT_EARLYRPY;
			} else {
				parm0 = FinanceConstants.FINSER_EVENT_EARLYSETTLE;
			}
			finScheduleData = setErrorToFSD(finScheduleData, "RU0000", parm0);
			return receiptData;
		}

		// ================================================================================
		// Value Date
		// ================================================================================
		// Back value validation will be with application date and received date
		// validation with derived date
		if (rcd.getReceivedDate().compareTo(derivedAppDate) > 0) {
			finScheduleData = setErrorToFSD(finScheduleData, "RU0006", DateUtility.formatToLongDate(appDate));
			return receiptData;
		}

		// Early settlement with Cheque/DD then value date must be <= derived
		// date + Clearing Days
		if (methodCtg == 2 && (StringUtils.equals(rch.getReceiptMode(), RepayConstants.RECEIPTMODE_CHEQUE)
				|| StringUtils.equals(rch.getReceiptMode(), RepayConstants.RECEIPTMODE_DD))) {
			int maxChqClearingDays = SysParamUtil.getValueAsInt("EARLYSETTLE_CHQ_CLR_DAYS");
			int clearingDays = DateUtility.getDaysBetween(fsi.getValueDate(), derivedAppDate);
			if (clearingDays > maxChqClearingDays) {
				parm0 = String.valueOf(clearingDays);
				finScheduleData = setErrorToFSD(finScheduleData, "90913", parm0);
				return receiptData;
			}

		} else {
			if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOWED_BACKDATED_RECEIPT)) {
				if (fsi.getValueDate().compareTo(derivedAppDate) > 0) {
					finScheduleData = setErrorToFSD(finScheduleData, "RU0007", DateUtility.formatToLongDate(appDate));
					return receiptData;
				}
			}
		}

		int paramDays = SysParamUtil.getValueAsInt("ALW_SP_BACK_DAYS");

		if (methodCtg == 0) {
			// check with system parameter value
			if (fsi.getValueDate().compareTo(appDate) < 0) {
				int days = DateUtility.getDaysBetween(fsi.getValueDate(), derivedAppDate);
				if (days > paramDays) {
					parm0 = DateUtility.formatToLongDate(fsi.getValueDate());
					parm1 = DateUtility.formatToLongDate(appDate);
					parm2 = String.valueOf(paramDays);
					finScheduleData = setErrorToFSD(finScheduleData, "RU0009", parm0, parm1, parm2);
					return receiptData;
				}
			}
			if (finScheduleData.getDisbursementDetails().size() > 0) {
				Date disbDate = finScheduleData.getDisbursementDetails().get(0).getDisbDate();
				if (DateUtil.compare(rcd.getReceivedDate(), disbDate) < 0) {
					finScheduleData = setErrorToFSD(finScheduleData, "RU0050", DateUtility.formatToLongDate(disbDate));
					return receiptData;
				}
			}

		}

		if (methodCtg == 1 || methodCtg == 2) {
			if (fsi.getValueDate().compareTo(DateUtility.addDays(DateUtility.getMonthStart(appDate), -1)) < 0) {
				parm0 = DateUtility.formatToLongDate(fsi.getValueDate());
				parm1 = DateUtility.formatToLongDate(DateUtility.addDays(DateUtility.getMonthStart(appDate), -1));
				// parm2 = String.valueOf(paramDays);
				finScheduleData = setErrorToFSD(finScheduleData, "RU0010", parm0, parm1);
				return receiptData;
			}
		}

		if (methodCtg >= 1) {
			// last schedule change date
			Date lastServDate = getFinLogEntryDetailDAO().getMaxPostDate(finReference);
			if (lastServDate != null && fsi.getValueDate().compareTo(lastServDate) < 0) {
				parm0 = DateUtility.formatToLongDate(fsi.getValueDate());
				parm1 = DateUtility.formatToLongDate(lastServDate);
				finScheduleData = setErrorToFSD(finScheduleData, "RU0013", parm0, parm1);
				return receiptData;
			}

			Date prvSchdDate = finPftDetail.getPrvRpySchDate();
			if (fsi.getValueDate().compareTo(prvSchdDate) < 0) {
				parm0 = DateUtility.formatToLongDate(fsi.getValueDate());
				parm1 = DateUtility.formatToLongDate(prvSchdDate);
				finScheduleData = setErrorToFSD(finScheduleData, "RU0012", parm0, parm1);
				return receiptData;
			}

			// Early Settlement OR Early Settlement Inquiry
			Date lastReceivedDate = null;
			if (methodCtg == 2 || methodCtg == 3) {
				lastReceivedDate = getMaxReceiptDate(fsi.getFinReference());
			} else if (methodCtg == 1) {
				lastReceivedDate = finReceiptDetailDAO.getMaxReceiptDate(finReference,
						FinanceConstants.FINSER_EVENT_EARLYRPY, TableType.VIEW);
			}
			if (lastReceivedDate != null && fsi.getValueDate().compareTo(lastReceivedDate) < 0) {
				parm0 = DateUtility.formatToLongDate(fsi.getValueDate());
				parm1 = DateUtility.formatToLongDate(lastReceivedDate);
				finScheduleData = setErrorToFSD(finScheduleData, "RU0011", parm0, parm1);
				return receiptData;
			}
		}
		// Instrumentwise Limits
		ErrorDetail errorDetail = doInstrumentValidation(receiptData);
		if (errorDetail != null) {
			finScheduleData.setErrorDetail(errorDetail);
		}

		return receiptData;
	}

	public FinReceiptData validateRecalType(FinReceiptData receiptData, int methodCtg) {
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceType finType = finScheduleData.getFinanceType();
		FinServiceInstruction fsi = finScheduleData.getFinServiceInstruction();
		String recalType = fsi.getRecalType();

		if (methodCtg == 0
				&& (StringUtils.isBlank(recalType) || StringUtils.equals(recalType, PennantConstants.List_Select))) {
			return receiptData;
		}

		if (methodCtg == 2
				&& (StringUtils.isBlank(recalType) || StringUtils.equals(recalType, PennantConstants.List_Select))) {
			fsi.setRecalType(CalculationConstants.EARLYPAY_ADJMUR);
			return receiptData;
		}

		if (methodCtg == 1 && StringUtils.isBlank(recalType)) {
			recalType = finType.getFinScheduleOn();
			if (finScheduleData.getFinanceMain().isAlwFlexi() || finType.isDeveloperFinance()) {
				recalType = CalculationConstants.EARLYPAY_PRIHLD;
			}
		}

		List<ValueLabel> alwEarlyPayMethodsList = getEarlyPaySchdMethods(financeDetail, fsi.getValueDate());
		List<String> methodsList = new ArrayList<>();
		boolean found = false;
		for (int i = 0; i < alwEarlyPayMethodsList.size(); i++) {
			methodsList.add(alwEarlyPayMethodsList.get(i).getValue());
			if (StringUtils.equals(recalType, alwEarlyPayMethodsList.get(i).getValue())) {
				found = true;
				break;
			}
		}

		if (!found) {
			finScheduleData = setErrorToFSD(finScheduleData, "90281", "Recal type code", methodsList.toString());
			return receiptData;
		}

		return receiptData;
	}

	public FinReceiptData validateClosingStatus(FinReceiptData receiptData, int methodCtg) {
		FinScheduleData finScheduleData = receiptData.getFinanceDetail().getFinScheduleData();
		FinServiceInstruction fsi = finScheduleData.getFinServiceInstruction();
		String finReference = fsi.getFinReference();
		String excessAdjustTo = fsi.getExcessAdjustTo().toUpperCase();
		String parm0 = null;
		String parm1 = null;

		// validating closing status
		String closingStaus = finScheduleData.getFinanceMain().getClosingStatus();
		if (StringUtils.isBlank(closingStaus)) {
			return receiptData;
		}

		if (StringUtils.equals(closingStaus, FinanceConstants.CLOSE_STATUS_CANCELLED)) {
			finScheduleData = setErrorToFSD(finScheduleData, "RU0044", finReference);
			return receiptData;
		}

		if (methodCtg == 1 || methodCtg == 2) {
			finScheduleData = setErrorToFSD(finScheduleData, "RU0043", finReference);
			return receiptData;
		}

		if (StringUtils.isBlank(excessAdjustTo)) {
			finScheduleData = setErrorToFSD(finScheduleData, "90502", "Excess Adjust to ");
			return receiptData;
		}

		if (StringUtils.isNotBlank(excessAdjustTo)
				&& StringUtils.equals(excessAdjustTo, RepayConstants.EXCESSADJUSTTO_EMIINADV)) {
			parm0 = "Excess Adjust to " + fsi.getExcessAdjustTo();
			parm1 = RepayConstants.EXCESSADJUSTTO_EXCESS;
			finScheduleData = setErrorToFSD(finScheduleData, "90337", parm0, parm1);
			return receiptData;
		}

		return receiptData;
	}

	@Override
	public FinReceiptData setReceiptDetail(FinReceiptData receiptData) {
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinServiceInstruction fsi = finScheduleData.getFinServiceInstruction();
		receiptData.setReceiptHeader(new FinReceiptHeader());
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		FinReceiptDetail rcd = fsi.getReceiptDetail();

		// PSD Ticket: 135820(1759046 Issue in Part payment Inquiry API in BHFL
		// PROD instance)
		if (rcd == null) {
			rcd = new FinReceiptDetail();
			fsi.setReceiptDetail(rcd);
		}

		rcd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rcd.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
		if (StringUtils.equals(fsi.getPaymentMode(), RepayConstants.RECEIPTMODE_ONLINE)) {
			rcd.setPaymentType(fsi.getSubReceiptMode());
		} else {
			rcd.setPaymentType(fsi.getPaymentMode());
		}
		rcd.setAmount(fsi.getAmount());
		rch.getReceiptDetails().add(rcd);
		return receiptData;
	}

	@Override
	public FinReceiptData setReceiptData(FinReceiptData receiptData) {
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		String receiptPurpose = financeDetail.getFinScheduleData().getFinServiceInstruction().getReceiptPurpose();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinServiceInstruction fsi = finScheduleData.getFinServiceInstruction();
		String finReference = fsi.getFinReference();
		receiptData.setFinReference(finReference);
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		FinReceiptDetail rcd = rch.getReceiptDetails().get(0);
		FinanceMain financeMain = finScheduleData.getFinanceMain();

		LoggedInUser userDetails = null;
		if (SessionUserDetails.getLogiedInUser() != null) {
			userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		} else {
			userDetails = fsi.getLoggedInUser();
		}
		receiptData.setUserDetails(userDetails);

		financeMain.setUserDetails(userDetails);
		financeDetail.setUserDetails(userDetails);

		Date appDate = SysParamUtil.getAppDate();
		receiptData.setBuildProcess("I");
		rch.setReference(finReference);
		rch.setExcessAdjustTo(fsi.getExcessAdjustTo());
		rch.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rch.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		rch.setReceiptDate(appDate);
		rch.setReceiptPurpose(receiptPurpose);
		rch.setEffectSchdMethod(fsi.getRecalType());
		rch.setExcessAdjustTo(fsi.getExcessAdjustTo());
		rch.setAllocationType(RepayConstants.ALLOCATIONTYPE_AUTO);
		rch.setReceiptAmount(fsi.getAmount());
		rch.setReceiptMode(fsi.getPaymentMode());
		rch.setSubReceiptMode(fsi.getSubReceiptMode());
		rch.setReceiptChannel(fsi.getReceiptChannel());
		rch.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		rch.setNewRecord(true);
		rch.setLastMntBy(userDetails.getUserId());
		rch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		rch.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		rch.setUserDetails(userDetails);

		if (FinanceConstants.FINSER_EVENT_SCHDRPY.equals(fsi.getReceiptPurpose()) && fsi.isBckdtdWthOldDues()) {
			Date derivedDate = getDerivedValueDate(receiptData, fsi, appDate);

			if (derivedDate != null) {
				rch.setValueDate(derivedDate);
			}
		}

		rcd.setValueDate(rch.getValueDate());
		rch.setRemarks(rcd.getRemarks());
		rch.setSource(PennantConstants.FINSOURCE_ID_API);

		if (fsi.isReceiptUpload()) {
			rch.setReceiptDate(rcd.getReceivedDate());
		} else {
			rcd.setValueDate(rcd.getReceivedDate());
			rch.setReceiptDate(SysParamUtil.getAppDate());
		}

		if (rch.getReceiptMode() != null && (rch.getSubReceiptMode() == null || rch.getSubReceiptMode().isEmpty())) {
			rch.setSubReceiptMode(rch.getReceiptMode());
		}

		if (fsi.getReceiptPurpose().equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE) && fsi.isReceiptUpload()
				&& StringUtils.equals(rcd.getPaymentType(), RepayConstants.RECEIPTMODE_CHEQUE)
				|| StringUtils.equals(rcd.getPaymentType(), RepayConstants.RECEIPTMODE_DD)) {
			int defaultClearingDays = SysParamUtil.getValueAsInt("EARLYSETTLE_CHQ_DFT_DAYS");
			fsi.setValueDate(DateUtility.addDays(fsi.getReceivedDate(), defaultClearingDays));
			rch.setValueDate(fsi.getValueDate());
			rcd.setValueDate(rch.getValueDate());
		}

		rch.setValueDate(fsi.getValueDate());
		receiptData.setSourceId(PennantConstants.FINSOURCE_ID_API);

		// set additional data
		// Setting data
		rch.setDepositDate(rcd.getDepositDate());
		if (StringUtils.equals(RepayConstants.RECEIPTMODE_CHEQUE, rcd.getPaymentType())
				|| StringUtils.equals(RepayConstants.RECEIPTMODE_DD, rcd.getPaymentType())) {
			rch.setTransactionRef(rcd.getChequeAcNo());
		} else {
			rch.setTransactionRef(rcd.getTransactionRef());
		}
		rch.setValueDate(rcd.getValueDate());
		rch.setPartnerBankId(rcd.getFundingAc());

		if (rch.getReceiptMode() != null && rch.getSubReceiptMode() == null) {
			rch.setSubReceiptMode(rch.getSubReceiptMode());
		}

		if (StringUtils.equals(fsi.getReqType(), RepayConstants.REQTYPE_INQUIRY)) {
			receiptData.setValueDate(SysParamUtil.getAppDate());
			rch.setValueDate(SysParamUtil.getAppDate());
		}
		rch.setAllocationType(fsi.getAllocationType());
		if (fsi.isReceiptUpload()) {

			if (StringUtils.equals(rcd.getStatus(), RepayConstants.PAYSTATUS_REALIZED)) {
				rch.setRealizationDate(fsi.getRealizationDate());
				rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_REALIZED);
			}

			rch.setRemarks(fsi.getRemarks());
		}

		int moduleID = FinanceConstants.MODULEID_FINTYPE;

		if (StringUtils.isNotBlank(financeMain.getPromotionCode())) {
			moduleID = FinanceConstants.MODULEID_PROMOTION;
		}

		finScheduleData.getFinanceMain().setReceiptPurpose(receiptPurpose);
		String event = null;
		if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_SCHDRPY)) {
			event = AccountEventConstants.ACCEVENT_REPAY;
		} else if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)) {
			event = AccountEventConstants.ACCEVENT_EARLYPAY;
		} else if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
			event = AccountEventConstants.ACCEVENT_EARLYSTL;
			/*
			 * finScheduleData = ScheduleCalculator.recalEarlyPaySchedule(finScheduleData, rcd.getReceivedDate(), null,
			 * receiptData.getFinanceDetail().getFinScheduleData(). getFinPftDeatil().getTotalPriBal(),
			 * CalculationConstants.EARLYPAY_ADJMUR);
			 */
		} else if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSTLENQ)) {
			event = AccountEventConstants.ACCEVENT_EARLYSTL;
		}

		// Finance Type Fee details based on Selected Receipt Purpose Event
		List<FinTypeFees> finTypeFeesList = this.financeDetailService.getFinTypeFees(financeMain.getFinType(), event,
				false, moduleID);
		receiptData.getFinanceDetail().setFinTypeFeesList(finTypeFeesList);
		FinServiceInstruction service = finScheduleData.getFinServiceInstruction();
		rch.setExcessAmounts(finExcessAmountDAO.getExcessAmountsByRef(rch.getReference()));
		calcuateDues(receiptData);
		receiptData.getFinanceDetail().getFinScheduleData().setFinServiceInstruction(service);

		String allocateMthd = receiptData.getReceiptHeader().getAllocationType();
		if (StringUtils.equals(allocateMthd, RepayConstants.ALLOCATIONTYPE_AUTO)) {
			receiptData = getReceiptCalculator().recalAutoAllocation(receiptData, rch.getValueDate(), false);
		}
		if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)) {
			if (receiptData.getReceiptHeader().getPartPayAmount().compareTo(BigDecimal.ZERO) <= 0) {
				finScheduleData = financeDetail.getFinScheduleData();
				finScheduleData = setErrorToFSD(finScheduleData, "90332", receiptPurpose, "");
				financeDetail.setFinScheduleData(finScheduleData);
				return receiptData;
			}
			BigDecimal closingBal = getClosingBalance(rch.getReference(), rch.getValueDate());
			BigDecimal diff = closingBal.subtract(receiptData.getReceiptHeader().getPartPayAmount());
			if (diff.compareTo(new BigDecimal(100)) < 0) {
				finScheduleData = setErrorToFSD(finScheduleData, "91127", String.valueOf(closingBal));
				financeDetail.setFinScheduleData(finScheduleData);
				return receiptData;
			}
		}

		if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
			boolean initiated = isEarlySettlementInitiated(StringUtils.trimToEmpty(finScheduleData.getFinReference()));
			if (initiated) {
				finScheduleData = financeDetail.getFinScheduleData();
				finScheduleData = setErrorToFSD(finScheduleData, "90332", receiptPurpose, "");
				financeDetail.setFinScheduleData(finScheduleData);
			}
		}

		if (fsi.isReceiptUpload()) {
			fsi.setFinFeeDetails(receiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList());
		}

		if (!StringUtils.equals(allocateMthd, RepayConstants.ALLOCATIONTYPE_AUTO)) {
			if (StringUtils.equals(PennantConstants.FINSOURCE_ID_API, receiptData.getSourceId())
					&& StringUtils.equals(allocateMthd, RepayConstants.ALLOCATIONTYPE_MANUAL)
					&& StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE, receiptPurpose)) {
				receiptData = validateAllocationsAmount(receiptData);
			}
			receiptData = updateAllocationsPaid(receiptData);
		}
		if (StringUtils.equals(FinanceConstants.PRODUCT_CD, financeMain.getProductCategory())
				&& fsi.getPaymentMode().equals(RepayConstants.RECEIPTMODE_PAYABLE)) {
			ManualAdvise payable = manualAdviseDAO.getManualAdviseById(fsi.getAdviseId(), "_AView");
			payable.setReservedAmt(payable.getBalanceAmt());
			manualAdviseDAO.update(payable, TableType.MAIN_TAB);
			rch.setPayAgainstId(payable.getAdviseID());
			rch.getReceiptDetails().get(0).setPayAgainstID(payable.getAdviseID());
			rch.getReceiptDetails().get(0).setNoManualReserve(true);
			List<XcessPayables> xcessPayableList = new ArrayList<>();
			XcessPayables xcessPayable = new XcessPayables();
			String feeDesc = payable.getFeeTypeDesc();
			xcessPayable.setPayableID(payable.getAdviseID());
			xcessPayable.setPayableType("P");
			xcessPayable.setAmount(payable.getBalanceAmt());
			xcessPayable.setFeeTypeCode(payable.getFeeTypeCode());
			if (payable.isTaxApplicable()) {
				xcessPayable.setTaxType(payable.getTaxComponent());
			} else {
				xcessPayable.setTaxType(null);
			}

			xcessPayable.setAmount(xcessPayable.getAmount().add(xcessPayable.getReserved()));
			TaxAmountSplit taxSplit = new TaxAmountSplit();
			taxSplit.setAmount(xcessPayable.getAmount());
			taxSplit.setTaxType(xcessPayable.getTaxType());
			xcessPayable.setAvailableAmt(payable.getAdviseAmount());
			xcessPayable.setTaxApplicable(payable.isTaxApplicable());
			xcessPayable.setPayableDesc(feeDesc);
			xcessPayable.setGstAmount(taxSplit.gettGST());
			xcessPayable.setTotPaidNow(fsi.getAmount());
			xcessPayable.setReserved(BigDecimal.ZERO);
			xcessPayable.setBalanceAmt(payable.getAdviseAmount().subtract(xcessPayable.getTotPaidNow()));
			xcessPayableList.add(xcessPayable);
			rch.setXcessPayables(xcessPayableList);
		}
		return receiptData;
	}

	/**
	 * Set the Value date to receipt header to handle old dues adjustment through API in case of back dated dues.
	 * 
	 * <p>
	 * Example:
	 * </p>
	 * <p>
	 * Receipt Date 3rd
	 * </p>
	 * <p>
	 * Schedule Date 5th
	 * </p>
	 * <p>
	 * Value Date 7th
	 * </p>
	 * <p>
	 * In this case system should adjust the dues without charges, so we are changing the value date from 7th to 5th
	 * </p>
	 * 
	 */
	private Date getDerivedValueDate(FinReceiptData receiptData, FinServiceInstruction fsi, Date appDate) {
		if (receiptData.getFinanceDetail() != null) {
			FinScheduleData schData = receiptData.getFinanceDetail().getFinScheduleData();
			List<FinanceScheduleDetail> list = schData.getFinanceScheduleDetails();
			Date receivedDate = fsi.getReceiptDetail().getReceivedDate();

			String parm0 = "";

			if (CollectionUtils.isNotEmpty(list)) {
				for (FinanceScheduleDetail sch : list) {
					if (appDate.compareTo(sch.getSchDate()) >= 0 && receivedDate.compareTo(sch.getSchDate()) <= 0) {
						break;
					}
					if (!sch.isSchPriPaid() || !sch.isSchPftPaid()) {
						parm0 = parm0 + sch.getSchDate().toString() + ",";
					}
				}
				if (parm0.length() > 0) {
					setErrorToFSD(schData, "90356", parm0);
					return null;
				}

				for (FinanceScheduleDetail sch : list) {
					if (appDate.compareTo(sch.getSchDate()) >= 0 && receivedDate.compareTo(sch.getSchDate()) <= 0) {
						return sch.getSchDate();
					}
				}
			}
		}
		return null;
	}

	public FinReceiptData updateAllocationsPaid(FinReceiptData receiptData) {
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinServiceInstruction fsi = finScheduleData.getFinServiceInstruction();
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		List<UploadAlloctionDetail> ulAllocations = fsi.getUploadAllocationDetails();
		List<ReceiptAllocationDetail> allocationsList = rch.getAllocations();
		String parm0 = null;
		String parm1 = null;
		BigDecimal emiAmount = BigDecimal.ZERO;
		BigDecimal priPaid = BigDecimal.ZERO;
		BigDecimal npftPaid = BigDecimal.ZERO;
		BigDecimal pftPaid = BigDecimal.ZERO;
		BigDecimal fnpftPaid = BigDecimal.ZERO;
		BigDecimal emiPaidAmt = BigDecimal.ZERO;
		BigDecimal emiWaivedAmt = BigDecimal.ZERO;
		BigDecimal fiWaivedAmt = BigDecimal.ZERO;
		boolean emiFound = false;
		int priIdx = -1;
		int pftIdx = -1;
		int fPftIdx = -1;
		int emiInx = -1;

		for (int i = 0; i < ulAllocations.size(); i++) {
			UploadAlloctionDetail ulAlc = ulAllocations.get(i);
			String ulAlcType = ulAlc.getAllocationType();

			if (StringUtils.equals(ulAlcType, "EM")) {
				if (ulAllocations.get(i).getWaivedAmount().compareTo(BigDecimal.ZERO) > 0) {
					parm0 = ulAlcType;
					parm1 = PennantApplicationUtil.amountFormate(ulAllocations.get(i).getWaivedAmount(), 2);
					finScheduleData = setErrorToFSD(finScheduleData, "RU0038", parm0, parm1);
					return receiptData;
				}
				emiAmount = ulAllocations.get(i).getPaidAmount();
				BigDecimal[] split = receiptCalculator.getEmiSplit(receiptData, emiAmount);
				priPaid = split[0];
				pftPaid = split[1];
				npftPaid = split[2];
				emiFound = true;
			}

			// Find the due amount in application prepared list
			for (int j = 0; j < allocationsList.size(); j++) {
				ReceiptAllocationDetail allocate = allocationsList.get(j);
				String alcType = null;

				String allocationType = allocate.getAllocationType();

				switch (allocationType) {
				case RepayConstants.ALLOCATION_PFT:
					alcType = "I";
					pftIdx = j;
					break;
				case RepayConstants.ALLOCATION_PRI:
					alcType = "P";
					priIdx = j;
					break;
				case RepayConstants.ALLOCATION_LPFT:
					alcType = "L";
					break;
				case RepayConstants.ALLOCATION_FEE:
					alcType = "F";
					break;
				case RepayConstants.ALLOCATION_ODC:
					alcType = "O";
					break;
				case RepayConstants.ALLOCATION_FUT_PFT:
					alcType = "FI";
					fnpftPaid = allocate.getPaidAmount();
					fPftIdx = j;
					break;
				case RepayConstants.ALLOCATION_FUT_PRI:
					alcType = "FP";
					break;
				case RepayConstants.ALLOCATION_EMI:
					alcType = "EM";
					emiInx = j;
					break;
				case RepayConstants.ALLOCATION_MANADV:
					alcType = "M";
					break;
				default:
					alcType = "B";
					break;
				}

				if (!StringUtils.equals(ulAlcType, alcType)) {
					continue;
				}

				if ("M".equals(ulAlcType) || "F".equals(ulAlcType)) {
					if (!StringUtils.equals(ulAlc.getReferenceCode(), (allocate.getFeeTypeCode()))) {
						continue;
					}
				}
				if ("I".equals(ulAlcType) || "P".equals(ulAlcType)) {
					emiPaidAmt = emiPaidAmt.add(ulAlc.getPaidAmount());
					emiWaivedAmt = emiWaivedAmt.add(ulAlc.getWaivedAmount());
				}
				if ("FI".equals(ulAlcType)) {
					fiWaivedAmt = ulAlc.getWaivedAmount();
				}
				if (ulAlc.getWaivedAmount().compareTo(allocate.getTotalDue()) > 0) {
					parm0 = alcType;
					parm1 = PennantApplicationUtil.amountFormate(allocate.getTotalDue(), 2);
					setErrorToFSD(finScheduleData, "RU0038", parm0, parm1);
					return receiptData;
				}

				if (ulAlc.getPaidAmount().add(ulAlc.getWaivedAmount()).compareTo(allocate.getTotalDue()) > 0) {
					parm0 = alcType;
					parm1 = PennantApplicationUtil.amountFormate(allocate.getTotalDue(), 2);
					setErrorToFSD(finScheduleData, "RU0038", parm0, parm1);
					return receiptData;
				}

				allocate.setTotalPaid(ulAlc.getPaidAmount());
				allocate.setPaidAmount(ulAlc.getPaidAmount());
				allocate.setWaivedAmount(ulAlc.getWaivedAmount());
			}
		}

		if (priIdx >= 0 && priPaid.compareTo(BigDecimal.ZERO) > 0) {
			allocationsList.get(priIdx).setPaidAmount(priPaid);
		}

		if (pftIdx >= 0 && pftPaid.compareTo(BigDecimal.ZERO) > 0) {
			allocationsList.get(pftIdx).setPaidAmount(npftPaid);
			allocationsList.get(pftIdx).setTdsPaid(pftPaid.subtract(npftPaid));
		}

		if (fPftIdx >= 0 && FinanceConstants.FINSER_EVENT_EARLYSETTLE.equals(rch.getReceiptPurpose())) {
			allocationsList.get(fPftIdx).setPaidAmount(fnpftPaid);
			allocationsList.get(fPftIdx).setTotalPaid(fnpftPaid);
			allocationsList.get(fPftIdx).setWaivedAmount(fiWaivedAmt);
		}
		if (emiInx >= 0 && !emiFound && FinanceConstants.FINSER_EVENT_EARLYSETTLE.equals(rch.getReceiptPurpose())) {
			allocationsList.get(emiInx).setPaidAmount(emiPaidAmt);
			allocationsList.get(emiInx).setTotalPaid(emiPaidAmt);
			allocationsList.get(emiInx).setWaivedAmount(emiWaivedAmt);
		}
		return receiptData;
	}

	@Override
	public FinScheduleData setErrorToFSD(FinScheduleData finScheduleData, String errorCode, String parm0) {
		String[] valueParm = new String[1];
		valueParm[0] = parm0;
		ErrorDetail errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail(errorCode, "", valueParm));
		finScheduleData.setErrorDetail(errorDetail);
		return finScheduleData;
	}

	@Override
	public FinScheduleData setErrorToFSD(FinScheduleData finScheduleData, String errorCode, String parm0,
			String parm1) {
		String[] valueParm = new String[2];
		valueParm[0] = parm0;
		valueParm[1] = parm1;
		ErrorDetail errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail(errorCode, "", valueParm));
		finScheduleData.setErrorDetail(errorDetail);
		return finScheduleData;
	}

	@Override
	public FinScheduleData setErrorToFSD(FinScheduleData finScheduleData, String errorCode, String parm0, String parm1,
			String parm2) {
		String[] valueParm = new String[3];
		valueParm[0] = parm0;
		valueParm[1] = parm1;
		valueParm[2] = parm2;
		ErrorDetail errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail(errorCode, "", valueParm));
		finScheduleData.setErrorDetail(errorDetail);
		return finScheduleData;
	}

	@Override
	public FinScheduleData setErrorToFSD(FinScheduleData finScheduleData, String errorCode, String parm0, String parm1,
			String parm2, String parm3) {
		String[] valueParm = new String[4];
		valueParm[0] = parm0;
		valueParm[1] = parm1;
		valueParm[2] = parm2;
		valueParm[3] = parm3;
		ErrorDetail errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail(errorCode, "", valueParm));
		finScheduleData.setErrorDetail(errorDetail);
		return finScheduleData;
	}

	public FinScheduleData validateForChequeOrDD(FinReceiptDetail receiptDetail, FinScheduleData finScheduleData) {
		// Bank Code is Mandatory
		if (StringUtils.isBlank(receiptDetail.getBankCode())) {
			finScheduleData = setErrorToFSD(finScheduleData, "90502", "BankCode");
			return finScheduleData;
		}

		// Bank Details should be configured
		BankDetail bankDetail = bankDetailService.getBankDetailById(receiptDetail.getBankCode());
		if (bankDetail == null) {
			finScheduleData = setErrorToFSD(finScheduleData, "90224", "BankCode", receiptDetail.getBankCode());
			return finScheduleData;
		}

		// Value Date must be present
		if (receiptDetail.getValueDate() == null) {
			finScheduleData = setErrorToFSD(finScheduleData, "90502", "ValueDate");
			return finScheduleData;
		}

		// Favour Name is mandatory
		/*
		 * if (StringUtils.isBlank(receiptDetail.getFavourName())) { finScheduleData = setErrorToFSD(finScheduleData,
		 * "90502", "FavourName"); return finScheduleData; }
		 */

		return finScheduleData;
	}

	public FinScheduleData validateForNonChequeOrDD(FinReceiptDetail receiptDetail, FinScheduleData finScheduleData) {

		String parm1 = RepayConstants.RECEIPTMODE_CASH + "," + RepayConstants.RECEIPTMODE_ONLINE;
		boolean isReceiptUpload = finScheduleData.getFinServiceInstruction().isReceiptUpload();

		// Value Date must not be sent
		if (!isReceiptUpload && receiptDetail.getValueDate() != null) {
			finScheduleData = setErrorToFSD(finScheduleData, "90329", "ValueDate", parm1);
			return finScheduleData;
		}

		// Favour Name must be blank
		if (StringUtils.isNotBlank(receiptDetail.getFavourName())) {
			finScheduleData = setErrorToFSD(finScheduleData, "90329", "FavourName", parm1);
			return finScheduleData;
		}

		// Bank code must be blank
		if (StringUtils.isNotBlank(receiptDetail.getBankCode())) {
			finScheduleData = setErrorToFSD(finScheduleData, "90329", "BankCode", parm1);
			return finScheduleData;
		}
		return finScheduleData;
	}

	// #### 04-09-2018 for core (Checking for inprocess receipts and
	// presentments)

	@Override
	public boolean isReceiptsPending(String finreference, long receiptId) {
		boolean isPending = finReceiptHeaderDAO.checkInProcessPresentments(finreference);
		if (isPending) {
			return true;
		}

		isPending = finReceiptHeaderDAO.checkInProcessReceipts(finreference, receiptId);
		return isPending;
	}

	@Override
	public String getClosingStatus(String finReference, TableType tempTab, boolean wif) {
		return financeMainDAO.getClosingStatus(finReference, tempTab, wif);
	}

	/**
	 * Ticket id:124998 check whether same details present already
	 * 
	 * @param receiptHeader
	 * @param purpose
	 */
	@Override
	public boolean dedupCheckRequest(FinReceiptHeader receiptHeader, String purpose) {

		boolean isExits = false;
		if (StringUtils.equals(receiptHeader.getReceiptMode(), RepayConstants.RECEIPTMODE_CHEQUE)
				|| StringUtils.equals(receiptHeader.getReceiptMode(), RepayConstants.RECEIPTMODE_DD)) {
			isExits = finReceiptDetailDAO.isFinReceiptDetailExitsByFavourNo(receiptHeader, purpose);
		} else {
			isExits = finReceiptDetailDAO.isFinReceiptDetailExitsByTransactionRef(receiptHeader, purpose);
		}

		return isExits;
	}

	@Override
	public FinanceDetail getFinanceDetail(FinServiceInstruction finServiceInst, String eventCode,
			FinanceDetail financeDetail) {
		logger.debug("Entering");

		String finReference = finServiceInst.getFinReference();
		String finSerEvent = "";
		if ("SP".equalsIgnoreCase(finServiceInst.getReceiptPurpose())) {
			finSerEvent = FinanceConstants.FINSER_EVENT_SCHDRPY;
		} else if ("EP".equalsIgnoreCase(finServiceInst.getReceiptPurpose())) {
			finSerEvent = FinanceConstants.FINSER_EVENT_EARLYRPY;
		} else {
			finSerEvent = FinanceConstants.FINSER_EVENT_EARLYSETTLE;
		}

		if (!finServiceInst.isWif()) {
			financeDetail = financeDetailService.getFinanceDetailById(finReference, false, "", false, finSerEvent, "");
		} else {
			financeDetail = financeDetailService.getWIFFinance(finReference, false, null);
		}

		List<FinFeeDetail> newList = new ArrayList<FinFeeDetail>();
		if (financeDetail != null) {
			financeDetail.getFinScheduleData().setFinFeeDetailList(newList);
			financeDetail.setAccountingEventCode(eventCode);
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			financeDetail.getFinScheduleData().getFinanceMain().setUserDetails(userDetails);
		}

		logger.debug("Leaving");
		return financeDetail;
	}

	@Override
	public long CheckDedupSP(FinReceiptHeader receiptHeader, String method) {
		if (StringUtils.equals(method, FinanceConstants.FINSER_EVENT_SCHDRPY)
				&& (StringUtils.equals(receiptHeader.getReceiptMode(), RepayConstants.RECEIPTMODE_CHEQUE)
						|| StringUtils.equals(receiptHeader.getReceiptMode(), RepayConstants.RECEIPTMODE_DD))
				&& StringUtils.equals(receiptHeader.getReceiptDetails().get(0).getStatus(),
						RepayConstants.PAYSTATUS_REALIZED)) {
			long receiptHeaderId = this.finReceiptDetailDAO.getReceiptIdByReceiptDetails(receiptHeader, method);
			return receiptHeaderId;
		}

		return 0;
	}

	/**
	 * get first Installment Date
	 * 
	 * @param financeScheduleDetail
	 * @return
	 */
	@Override
	public Date getFirstInstDate(List<FinanceScheduleDetail> financeScheduleDetail) {

		// Finding First Installment Date
		Date firstInstDate = null;
		for (FinanceScheduleDetail scheduleDetail : financeScheduleDetail) {

			BigDecimal repayAmt = scheduleDetail.getProfitSchd().add(scheduleDetail.getPrincipalSchd())
					.subtract(scheduleDetail.getPartialPaidAmt());

			// InstNumber issue with Partial Settlement before first installment
			if (repayAmt.compareTo(BigDecimal.ZERO) > 0) {
				firstInstDate = scheduleDetail.getSchDate();
				break;
			}
		}
		return firstInstDate;
	}

	private void setDefaultDateFormats(FinServiceInstruction finServInst) {
		if (finServInst.getFromDate() != null) {
			finServInst.setFromDate(DateUtility
					.getDBDate(DateUtility.format(finServInst.getFromDate(), PennantConstants.DBDateFormat)));
		}

		if (finServInst.getToDate() != null) {
			finServInst.setToDate(
					DateUtility.getDBDate(DateUtility.format(finServInst.getToDate(), PennantConstants.DBDateFormat)));
		}
		if (finServInst.getRecalFromDate() != null) {
			finServInst.setRecalFromDate(DateUtility
					.getDBDate(DateUtility.format(finServInst.getRecalFromDate(), PennantConstants.DBDateFormat)));
		}
		if (finServInst.getRecalToDate() != null) {
			finServInst.setRecalToDate(DateUtility
					.getDBDate(DateUtility.format(finServInst.getRecalToDate(), PennantConstants.DBDateFormat)));
		}
		if (finServInst.getGrcPeriodEndDate() != null) {
			finServInst.setGrcPeriodEndDate(DateUtility
					.getDBDate(DateUtility.format(finServInst.getGrcPeriodEndDate(), PennantConstants.DBDateFormat)));
		}
		if (finServInst.getNextGrcRepayDate() != null) {
			finServInst.setNextGrcRepayDate(DateUtility
					.getDBDate(DateUtility.format(finServInst.getNextGrcRepayDate(), PennantConstants.DBDateFormat)));
		}
		if (finServInst.getNextRepayDate() != null) {
			finServInst.setNextRepayDate(DateUtility
					.getDBDate(DateUtility.format(finServInst.getNextRepayDate(), PennantConstants.DBDateFormat)));
		}
		if (finServInst.getReceivedDate() != null) {
			finServInst.setReceivedDate(DateUtility
					.getDBDate(DateUtility.format(finServInst.getReceivedDate(), PennantConstants.DBDateFormat)));
		}
		if (finServInst.getValueDate() != null) {
			finServInst.setValueDate(DateUtility
					.getDBDate(DateUtility.format(finServInst.getValueDate(), PennantConstants.DBDateFormat)));
		}
		if (finServInst.getDepositDate() != null) {
			finServInst.setDepositDate(DateUtility
					.getDBDate(DateUtility.format(finServInst.getDepositDate(), PennantConstants.DBDateFormat)));
		}
		if (finServInst.getRealizationDate() != null) {
			finServInst.setRealizationDate(DateUtility
					.getDBDate(DateUtility.format(finServInst.getRealizationDate(), PennantConstants.DBDateFormat)));
		}
		if (finServInst.getInstrumentDate() != null) {
			finServInst.setInstrumentDate(DateUtility
					.getDBDate(DateUtility.format(finServInst.getInstrumentDate(), PennantConstants.DBDateFormat)));
		}
		if (finServInst.getReceiptDetail() != null && finServInst.getReceiptDetail().getReceivedDate() != null) {
			finServInst.getReceiptDetail().setReceivedDate(DateUtility.getDBDate(DateUtility
					.format(finServInst.getReceiptDetail().getReceivedDate(), PennantConstants.DBDateFormat)));
		}
	}

	public List<ValueLabel> getEarlyPaySchdMethods(FinanceDetail financeDetail, Date valueDate) {
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinanceType financeType = financeDetail.getFinScheduleData().getFinanceType();

		List<ValueLabel> epyMethodList = new ArrayList<>();
		if (StringUtils.isNotEmpty(financeType.getAlwEarlyPayMethods())) {
			String[] epMthds = financeType.getAlwEarlyPayMethods().trim().split(",");
			if (epMthds.length > 0) {
				List<String> list = Arrays.asList(epMthds);
				for (ValueLabel epMthd : PennantStaticListUtil.getEarlyPayEffectOn()) {
					if (list.contains(epMthd.getValue().trim())) {
						if (StringUtils.equals(CalculationConstants.RPYCHG_STEPPOS, epMthd.getValue().trim())) {
							if (financeMain.isStepFinance() && financeMain.isAllowGrcPeriod()
									&& StringUtils.equals(financeMain.getStepType(), FinanceConstants.STEPTYPE_PRIBAL)
									&& DateUtility.compare(valueDate, financeMain.getGrcPeriodEndDate()) <= 0
									&& (StringUtils.equals(financeMain.getScheduleMethod(),
											CalculationConstants.SCHMTHD_PRI)
											|| StringUtils.equals(financeMain.getScheduleMethod(),
													CalculationConstants.SCHMTHD_PRI_PFT))) {
								epyMethodList.add(epMthd);
							}
						} else if (StringUtils.equals(FinanceConstants.PRODUCT_HYBRID_FLEXI,
								financeType.getFinTypeClassification())) {
							epyMethodList.add(epMthd);
						} else if (StringUtils.equals(CalculationConstants.EARLYPAY_PRIHLD, epMthd.getValue().trim())) {
							if (financeType.isDeveloperFinance()) {
								epyMethodList.clear();
								epyMethodList.add(epMthd);
								break;
							} else {
								epyMethodList.add(epMthd);
							}
						} else {
							epyMethodList.add(epMthd);
						}
					}
				}
			}
		}

		return epyMethodList;
	}

	@Override
	public FinServiceInstruction buildFinServiceInstruction(ReceiptUploadDetail rud, String entity) {
		FinServiceInstruction fsi = new FinServiceInstruction();
		fsi.setFinReference(rud.getReference());
		fsi.setExternalReference(rud.getExtReference());
		fsi.setModule("Receipts");
		fsi.setValueDate(rud.getValueDate());
		fsi.setAmount(rud.getReceiptAmount());
		fsi.setAllocationType(rud.getAllocationType());
		if (StringUtils.isNotEmpty(rud.getFundingAc())) {
			fsi.setFundingAc(Long.parseLong(rud.getFundingAc()));
		}
		fsi.setPaymentRef(rud.getPaymentRef());
		fsi.setFavourNumber(rud.getFavourNumber());
		fsi.setRecalType(rud.getEffectSchdMethod());

		fsi.setFavourNumber(rud.getFavourNumber());
		fsi.setBankCode(rud.getBankCode());
		fsi.setChequeNo(rud.getChequeNo());
		fsi.setTransactionRef(rud.getTransactionRef());
		fsi.setStatus(rud.getStatus());
		fsi.setDepositDate(rud.getDepositDate());
		fsi.setRealizationDate(rud.getRealizationDate());
		fsi.setInstrumentDate(rud.getInstrumentDate());
		fsi.setReceivedDate(rud.getReceivedDate());
		fsi.setRemarks(rud.getRemarks());
		fsi.setPaymentMode(rud.getReceiptMode());
		fsi.setExcessAdjustTo(rud.getExcessAdjustTo());

		fsi.setUploadAllocationDetails(rud.getListAllocationDetails());
		fsi.setEntity(entity);
		fsi.setSubReceiptMode(rud.getSubReceiptMode());
		fsi.setReceiptChannel(rud.getReceiptChannel());
		fsi.setCollectionAgentId(rud.getCollectionAgentId());
		fsi.setReceivedFrom(rud.getReceivedFrom());
		fsi.setPanNumber(rud.getPanNumber());
		fsi.setUploadDetailId(rud.getUploadDetailId());

		if (StringUtils.equals(rud.getStatus(), "A")) {
			fsi.setReceiptdetailExits(false);
		} else {

			if ((StringUtils.equalsIgnoreCase(rud.getReceiptMode(), RepayConstants.RECEIPTMODE_CHEQUE)
					|| StringUtils.equalsIgnoreCase(rud.getReceiptMode(), RepayConstants.RECEIPTMODE_DD))
					&& StringUtils.equalsIgnoreCase(rud.getStatus(), RepayConstants.PAYSTATUS_REALIZED)) {
				String mode = rud.getReceiptMode();
				boolean isreceiptdataExits = false;

				if (StringUtils.equalsIgnoreCase(rud.getReceiptPurpose(), "SP")) {
					isreceiptdataExits = receiptUploadHeaderService.isReceiptDetailsExits(rud.getReference(), mode,
							rud.getChequeNo(), rud.getFavourNumber(), "");
				} else {
					isreceiptdataExits = receiptUploadHeaderService.isReceiptDetailsExits(rud.getReference(), mode,
							rud.getChequeNo(), rud.getFavourNumber(), "_Temp");
				}

				if (isreceiptdataExits) {
					fsi.setReceiptdetailExits(true);
				}
			}

		}

		FinReceiptDetail rcd = new FinReceiptDetail();
		rcd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rcd.setAmount(fsi.getAmount());

		rcd.setValueDate(rud.getValueDate());
		rcd.setBankCode(rud.getBankCode());
		rcd.setDepositDate(rud.getDepositDate());
		rcd.setPaymentRef(rud.getPaymentRef());
		rcd.setTransactionRef(rud.getTransactionRef());
		rcd.setFavourNumber(fsi.getFavourNumber());

		// FIXME: PV: Is it cheque account number or cheque number
		rcd.setChequeAcNo(rud.getChequeNo());

		if (StringUtils.isNotEmpty(rud.getFundingAc())) {
			rcd.setFundingAc(Long.parseLong(rud.getFundingAc()));
		}
		rcd.setReceivedDate(rud.getReceivedDate());
		rcd.setStatus(fsi.getStatus());
		rcd.setRemarks(rud.getRemarks());
		rcd.setReference(rud.getReference());

		if (StringUtils.equals(rud.getReceiptPurpose(), "SP")) {
			fsi.setReceiptPurpose(FinanceConstants.FINSER_EVENT_SCHDRPY);
			rcd.setReceiptPurpose(FinanceConstants.FINSER_EVENT_SCHDRPY);
		} else if (StringUtils.equals(rud.getReceiptPurpose(), "EP")) {
			fsi.setReceiptPurpose(FinanceConstants.FINSER_EVENT_EARLYRPY);
			rcd.setReceiptPurpose(FinanceConstants.FINSER_EVENT_EARLYRPY);
		} else if (StringUtils.equals(rud.getReceiptPurpose(), "ES")) {
			fsi.setReceiptPurpose(FinanceConstants.FINSER_EVENT_EARLYSETTLE);
			rcd.setReceiptPurpose(FinanceConstants.FINSER_EVENT_EARLYSETTLE);
		}
		fsi.setReceiptDetail(rcd);
		return fsi;
	}

	@Override
	public BigDecimal getClosingBalance(String finReference, Date valueDate) {
		return financeScheduleDetailDAO.getClosingBalance(finReference, valueDate);
	}

	@Override
	public FinReceiptData getServicingFinance(String id, String nextRoleCode, String screenEvent, String role) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Long, String> getOrnamentDescriptions(List<Long> idList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Assignment getAssignment(long id, String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AssignmentDealExcludedFee> getApprovedAssignmentDealExcludedFeeList(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getManualAdviseMaxDate(String reference, Date valueDate) {
		return manualAdviseDAO.getManualAdviseDate(reference, valueDate, "", FinanceConstants.MANUAL_ADVISE_RECEIVABLE);
	}

	@Override
	public FinReceiptData getFinReceiptDataByReceiptId(long receiptId, String eventCode, String procEdtEvent,
			String userRole) {

		logger.debug("Entering");

		FinReceiptHeader finReceiptHeader = getFinReceiptHeaderById(receiptId, false, "_View");
		finReceiptHeader.setValueDate(finReceiptHeader.getReceiptDate());

		List<FinReceiptDetail> receiptDetailList = null;
		if (SysParamUtil.isAllowed(SMTParameterConstants.CHQ_RECEIPTS_PAID_AT_DEPOSIT_APPROVER)
				&& finReceiptHeader.getReceiptPurpose().equals(FinanceConstants.FINSER_EVENT_SCHDRPY)
				&& (StringUtils.equals(finReceiptHeader.getReceiptModeStatus(), RepayConstants.PAYSTATUS_BOUNCE)
						|| StringUtils.equals(finReceiptHeader.getReceiptModeStatus(),
								RepayConstants.PAYSTATUS_CANCEL))) {
			receiptDetailList = finReceiptDetailDAO.getReceiptHeaderByID(receiptId, "");
		} else {

			receiptDetailList = finReceiptDetailDAO.getReceiptHeaderByID(receiptId, "_View");
		}
		finReceiptHeader.setReceiptDetails(receiptDetailList);
		int size = receiptDetailList.size();
		if (size > 0) {
			FinReceiptDetail recDtl = receiptDetailList.get(size - 1);
			finReceiptHeader.setValueDate(recDtl.getValueDate());
			if (finReceiptHeader.getReceiptPurpose().equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE)
					&& finReceiptHeader.getRealizationDate() != null) {
				finReceiptHeader.setValueDate(finReceiptHeader.getRealizationDate());
			}
			if (ImplementationConstants.ALLOW_SCDREPAY_REALIZEDATE_AS_VALUEDATE) {
				if (StringUtils.equals(finReceiptHeader.getReceiptPurpose(), FinanceConstants.FINSER_EVENT_SCHDRPY)
						&& (StringUtils.equals(finReceiptHeader.getReceiptMode(), RepayConstants.RECEIPTMODE_CHEQUE)
								|| StringUtils.equals(finReceiptHeader.getReceiptMode(), RepayConstants.RECEIPTMODE_DD))
						&& finReceiptHeader.getRealizationDate() != null) {
					FinanceScheduleDetail finScheduleDetail = financeScheduleDetailDAO
							.getNextUnpaidSchPayment(finReceiptHeader.getReference(), finReceiptHeader.getValueDate());
					if (finScheduleDetail != null) {
						Date schDate = finScheduleDetail.getSchDate();
						if (!finReceiptHeader.getValueDate().after(schDate)) {
							finReceiptHeader.setValueDate(schDate);
						}
					}
				}
			}
		}

		for (FinReceiptDetail rcd : receiptDetailList) {
			rcd.setRepayHeader(financeRepaymentsDAO.getFinRepayHeadersByReceipt(rcd.getReceiptSeqID(), ""));
		}

		// All the data should be from main tables OR views only.
		FinReceiptData receiptData = new FinReceiptData();
		String finReference = finReceiptHeader.getReference();
		receiptData.setFinReference(finReference);
		receiptData.setReceiptHeader(finReceiptHeader);

		FinanceDetail financeDetail = new FinanceDetail();
		receiptData.setFinanceDetail(financeDetail);
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		scheduleData.setFinReference(finReference);

		if (!StringUtils.equals(PennantConstants.RECORD_TYPE_NEW, finReceiptHeader.getRecordType())) {
			receiptData.setCalReq(false);
		}

		// Multi Receipts: Get In Process Receipts
		receiptData = getInProcessReceiptData(receiptData);

		// Finance Details from Main Table View
		FinanceMain financeMain = financeMainDAO.getFinanceMainById(finReference, "_AView", false);

		if (financeMain == null) {
			logger.debug("Leaving");
			return receiptData;
		}
		FinanceMain finMain = financeMainDAO.getEntityNEntityDesc(finReference, "_Aview", false);

		if (finMain != null) {
			financeMain.setEntityCode(finMain.getEntityCode());
			financeMain.setEntityDesc(finMain.getEntityDesc());
		}

		List<ReceiptAllocationDetail> allocations = allocationDetailDAO.getAllocationsByReceiptID(receiptId, "_View");
		if (CollectionUtils.isNotEmpty(allocations)) {
			for (ReceiptAllocationDetail receiptAllocationDetail : allocations) {
				receiptAllocationDetail.setTotalPaid(
						receiptAllocationDetail.getPaidAmount().add(receiptAllocationDetail.getTdsPaid()));
				receiptAllocationDetail
						.setTotRecv(receiptAllocationDetail.getTotalDue().add(receiptAllocationDetail.getTdsDue()));
				if (receiptAllocationDetail.getTaxHeaderId() != 0) {
					List<Taxes> taxDetailById = taxHeaderDetailsDAO
							.getTaxDetailById(receiptAllocationDetail.getTaxHeaderId(), "_View");
					TaxHeader taxHeader = new TaxHeader(receiptAllocationDetail.getTaxHeaderId());
					taxHeader.setTaxDetails(taxDetailById);
				}
			}
		}
		finReceiptHeader.setAllocations(allocations);

		finReceiptHeader.setManualAdvise(manualAdviseDAO.getManualAdviseByReceiptId(receiptId, "_View"));

		scheduleData.setFinanceMain(financeMain);

		// Finance Type Details from Table
		FinanceType financeType = financeTypeDAO.getOrgFinanceTypeByID(financeMain.getFinType(), "_ORGView");

		// Fetching Promotion Details from main view
		if (StringUtils.isNotBlank(financeMain.getPromotionCode())
				&& (financeMain.getPromotionSeqId() != null && financeMain.getPromotionSeqId() == 0)) {
			Promotion promotion = this.promotionDAO.getPromotionByCode(financeMain.getPromotionCode(), "_AView");
			financeType.setFInTypeFromPromotiion(promotion);
		}

		scheduleData.setFinanceType(financeType);

		// Step Policy Details List from main view
		if (financeMain.isStepFinance()) {
			scheduleData.setStepPolicyDetails(
					getFinanceStepDetailDAO().getFinStepDetailListByFinRef(finReference, "_TView", false));
		}

		financeDetail.setFinTypeFeesList(financeDetailService.getFinTypeFees(financeMain.getFinType(), eventCode, false,
				FinanceConstants.MODULEID_FINTYPE));

		financeDetail.setFinFeeConfigList(
				finFeeConfigService.getFinFeeConfigList(financeMain.getFinReference(), eventCode, false, "_View"));

		scheduleData.setFeeEvent(eventCode);

		// Finance Schedule Details from Main table
		scheduleData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finReference, "", false));

		// Overdraft Details from main table
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
			scheduleData.setOverdraftScheduleDetails(
					overdraftScheduleDetailDAO.getOverdraftScheduleDetails(finReference, "", false));
		}

		// Finance Disbursement Details from main view
		scheduleData.setDisbursementDetails(
				financeDisbursementDAO.getFinanceDisbursementDetails(finReference, "_AView", false));

		// Finance Repayments Instruction Details from main view
		scheduleData.setRepayInstructions(repayInstructionDAO.getRepayInstructions(finReference, "_AView", false));

		// Overdue Penalty Rates from main veiw
		scheduleData.setFinODPenaltyRate(finODPenaltyRateDAO.getFinODPenaltyRateByRef(finReference, "_AView"));

		// Profit details from main table
		FinanceProfitDetail profitDetail = profitDetailsDAO.getFinProfitDetailsById(finReference);
		scheduleData.setFinPftDeatil(profitDetail);

		// Finance Customer Details from main view
		if (financeMain.getCustID() != 0 && financeMain.getCustID() != Long.MIN_VALUE) {
			financeDetail.setCustomerDetails(
					customerDetailsService.getCustomerDetailsById(financeMain.getCustID(), true, "_AView"));
		}

		// Finance Stage Accounting Posting Details
		// =======================================
		receiptData.getFinanceDetail().setStageTransactionEntries(
				transactionEntryDAO.getListTransactionEntryByRefType(financeType.getFinType(),
						StringUtils.isEmpty(procEdtEvent) ? FinanceConstants.FINSER_EVENT_ORG : procEdtEvent,
						FinanceConstants.PROCEDT_STAGEACC, userRole, "_AEView", true));

		// Repay Header Details

		// Fetch Excess Amount Details
		receiptData.getReceiptHeader().setExcessAmounts(finExcessAmountDAO.getExcessAmountsByRef(finReference));

		// Fetch Payable Advise Amount Details
		receiptData.getReceiptHeader().setPayableAdvises(
				manualAdviseDAO.getManualAdviseByRef(finReference, FinanceConstants.MANUAL_ADVISE_PAYABLE, "_AView"));

		// Fee Details ( Fetch Fee Details for event fee only)
		List<FinFeeDetail> feesList = getFinFeeDetailService().getFinFeeDetailsByReferenceId(receiptId, eventCode,
				"_TView");
		if (CollectionUtils.isNotEmpty(feesList)) {
			for (FinFeeDetail finFeeDetail : feesList) {
				Long taxHeaderId = finFeeDetail.getTaxHeaderId();
				if (taxHeaderId != null && taxHeaderId > 0) {
					List<Taxes> taxDetailById = getTaxHeaderDetailsDAO().getTaxDetailById(taxHeaderId, "_TView");
					TaxHeader taxheader = new TaxHeader();
					taxheader.setTaxDetails(taxDetailById);
					taxheader.setHeaderId(taxHeaderId);
					finFeeDetail.setTaxHeader(taxheader);
				}
			}
			scheduleData.setFinFeeDetailList(feesList);
			receiptData.setFinFeeDetails(feesList);
		}

		if (scheduleData.getFinFeeDetailList() != null && !scheduleData.getFinFeeDetailList().isEmpty()) {
			for (int i = 0; i < receiptData.getReceiptHeader().getAllocations().size(); i++) {
				ReceiptAllocationDetail allocation = receiptData.getReceiptHeader().getAllocations().get(i);
				if (StringUtils.equals(RepayConstants.ALLOCATION_FEE, allocation.getAllocationType())) {
					for (FinFeeDetail feeDtl : receiptData.getFinanceDetail().getFinScheduleData()
							.getFinFeeDetailList()) {
						if (feeDtl.getFeeID() == allocation.getAllocationTo()) {
							allocation.setAllocationTo(-(feeDtl.getFeeTypeID()));
							break;
						}
					}
				}
			}
		}

		// Setting fin tax details
		financeDetail.setFinanceTaxDetail(getFinanceTaxDetailDAO().getFinanceTaxDetail(finReference, "_AView"));

		// Finance Document Details
		List<DocumentDetails> documentList = documentDetailsDAO.getDocumentDetailsByRef(finReference,
				FinanceConstants.MODULE_NAME, procEdtEvent, "_View");
		if (financeDetail.getDocumentDetailsList() != null && !financeDetail.getDocumentDetailsList().isEmpty()) {
			financeDetail.getDocumentDetailsList().addAll(documentList);
		} else {
			financeDetail.setDocumentDetailsList(documentList);
		}

		// Collateral Details
		if (ImplementationConstants.COLLATERAL_INTERNAL) {
			financeDetail.setCollateralAssignmentList(collateralAssignmentDAO
					.getCollateralAssignmentByFinRef(finReference, FinanceConstants.MODULE_NAME, "_View"));
		} else {
			financeDetail.setFinanceCollaterals(finCollateralService.getFinCollateralsByRef(finReference, "_View"));
		}
		logger.debug("Leaving");
		return receiptData;

	}

	@Override
	public FinReceiptData recalEarlyPaySchedule(FinReceiptData receiptData) {
		String entityDesc = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().getEntityDesc();
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		FinanceDetail fd = receiptData.getFinanceDetail();
		int receiptPurposeCtg = receiptCalculator.setReceiptCategory(rch.getReceiptPurpose());
		FinScheduleData finScheduleData = financeDetailService.getFinSchDataForReceipt(rch.getReference(), "_AView");
		finScheduleData.setFinODDetails(receiptData.getFinanceDetail().getFinScheduleData().getFinODDetails());
		FinanceMain aFinanceMain = fd.getFinScheduleData().getFinanceMain();
		finScheduleData.setFinFeeDetailList(receiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList());
		receiptData.getFinanceDetail().setFinScheduleData(finScheduleData);
		RepayMain repayMain = receiptData.getRepayMain();

		// Setting Effective Recalculation Schedule Method
		String method = null;
		if (receiptPurposeCtg == 1) {
			repayMain.setEarlyPayAmount(receiptData.getRemBal());
			method = rch.getEffectSchdMethod();
		} else if (receiptPurposeCtg == 2) {
			method = CalculationConstants.EARLYPAY_ADJMUR;
			repayMain.setEarlyPayAmount(repayMain.getCurFinAmount());
		}

		boolean isStepLoan = false;
		String valueAsString = SysParamUtil.getValueAsString("STEP_LOAN_SERVICING_REQ");
		if (StringUtils.equalsIgnoreCase(valueAsString, PennantConstants.YES)) {
			if (aFinanceMain.isStepFinance()) {
				if (StringUtils.isNotBlank(aFinanceMain.getStepPolicy())
						|| (aFinanceMain.isAlwManualSteps() && aFinanceMain.getNoOfSteps() > 0)) {
					isStepLoan = true;
					finScheduleData.setStepPolicyDetails(getFinanceStepDetailDAO()
							.getFinStepDetailListByFinRef(finScheduleData.getFinReference(), "", false));
				}
			}
		}

		if (isStepLoan) {

			List<RepayInstruction> rpst = finScheduleData.getRepayInstructions();
			if (repayMain.getEarlyPayOnSchDate().compareTo(aFinanceMain.getGrcPeriodEndDate()) < 0) {

				finScheduleData.getFinanceMain().setRecalSteps(true);
				finScheduleData.getFinanceMain().setRecalType(CalculationConstants.RPYCHG_STEPINST);
				for (RepayInstruction repayInstruction : rpst) {
					if (repayInstruction.getRepayDate().compareTo(aFinanceMain.getGrcPeriodEndDate()) > 0) {
						finScheduleData.getFinanceMain().setRecalFromDate(repayInstruction.getRepayDate());
						break;
					}
				}
			}
		}

		// Step POS Case , setting Step Details to Object
		if (receiptPurposeCtg == 1 && StringUtils.equals(method, CalculationConstants.RPYCHG_STEPPOS)) {
			finScheduleData.setStepPolicyDetails(
					financeDetailService.getFinStepPolicyDetails(finScheduleData.getFinReference(), "", false));
		}

		// Calculation of Schedule Changes for Early Payment to change
		// Schedule Effects Depends On Method
		finScheduleData.getFinanceMain().setReceiptPurpose(rch.getReceiptPurpose());
		finScheduleData = ScheduleCalculator.recalEarlyPaySchedule(finScheduleData, repayMain.getEarlyPayOnSchDate(),
				null, repayMain.getEarlyPayAmount(), method);

		receiptData.getFinanceDetail().setFinScheduleData(finScheduleData);
		receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().setEntityDesc(entityDesc);
		return receiptData;
	}

	public FinReceiptData recalculateReceipt(FinReceiptData receiptData) {
		if (receiptData.isPresentment()) {
			return receiptData;
		}
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		String eventCode = "";
		Date valueDate = rch.getValueDate();
		boolean isForeClosure = receiptData.isForeClosure();
		int receiptPurposeCtg = receiptCalculator.setReceiptCategory(rch.getReceiptPurpose());
		if (StringUtils.equals(rch.getReceiptPurpose(), FinanceConstants.FINSER_EVENT_SCHDRPY)) {
			eventCode = AccountEventConstants.ACCEVENT_REPAY;
		} else if (StringUtils.equals(rch.getReceiptPurpose(), FinanceConstants.FINSER_EVENT_EARLYRPY)) {
			eventCode = AccountEventConstants.ACCEVENT_EARLYPAY;
		} else if (StringUtils.equals(rch.getReceiptPurpose(), FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
			eventCode = AccountEventConstants.ACCEVENT_EARLYSTL;
		}

		// FIXME Bharat Only get finschedule details and finprofitdetails
		FinReceiptData recData = null;
		if (rch.getReceiptID() <= 0 || receiptData.isPresentment()) {
			recData = receiptData;
		} else {
			recData = getFinReceiptDataByReceiptId(rch.getReceiptID(), eventCode, FinanceConstants.FINSER_EVENT_RECEIPT,
					"");
		}

		FinanceMain finmain = recData.getFinanceDetail().getFinScheduleData().getFinanceMain();
		Date maturityDate = finmain.getMaturityDate();

		rch = recData.getReceiptHeader();
		recData.setValueDate(rch.getValueDate());
		rch.setValueDate(null);
		rch.setReceiptDate(SysParamUtil.getAppDate());
		recData.setBuildProcess("I");
		recData.setAllocList(rch.getAllocations());
		recData.setForeClosure(isForeClosure);
		recData = receiptCalculator.initiateReceipt(recData, false);

		if (receiptPurposeCtg == 2) {
			recData.getReceiptHeader().setValueDate(null);
			recData.setOrgFinPftDtls(recData.getFinanceDetail().getFinScheduleData().getFinPftDeatil());
			receiptPurposeCtg = receiptCalculator.setReceiptCategory(rch.getReceiptPurpose());
			recData.getRepayMain().setEarlyPayOnSchDate(valueDate);
			recalEarlyPaySchedule(recData);
			recData.setBuildProcess("I");
			recData = getReceiptCalculator().initiateReceipt(receiptData, false);
			recData.setActualReceiptAmount(rch.getReceiptAmount().subtract(receiptData.getExcessAvailable()));
			recData.setExcessAvailable(getReceiptCalculator().getExcessAmount(receiptData));
			if (receiptData.isForeClosure()) {
				rch.setReceiptAmount(BigDecimal.ZERO);
				rch.setReceiptAmount(receiptData.getExcessAvailable());
			} else {
				if (rch.getReceiptDetails().size() > 1) {
					recData.getReceiptHeader().setReceiptAmount(
							receiptData.getReceiptHeader().getReceiptAmount().add(receiptData.getExcessAvailable()));
				}
			}
			// recData = receiptCalculator.initiateReceipt(recData, false);
		} else {
			recData.getReceiptHeader().setReceiptAmount(receiptData.getReceiptHeader().getReceiptAmount());
		}
		if (!RepayConstants.ALLOCATIONTYPE_MANUAL.equals(recData.getReceiptHeader().getAllocationType())) {
			recData = receiptCalculator.recalAutoAllocation(recData, recData.getReceiptHeader().getValueDate(), false);
		}

		if (receiptPurposeCtg == 2) {
			boolean duesAdjusted = checkDueAdjusted(recData.getReceiptHeader().getAllocations(), receiptData);
			if (!duesAdjusted) {
				if (isForeClosure) {
					recData.getErrorDetails().add(ErrorUtil.getErrorDetail(new ErrorDetail("FC0000")));
					return recData;
				} else {
					recData = adjustToExcess(recData);
					recData.setDueAdjusted(false);
					return recData;
				}
			}
		}

		boolean simulateAccounting = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain()
				.isSimulateAccounting();
		if (receiptPurposeCtg == 1 && !simulateAccounting) {
			recalEarlyPaySchedule(recData);
		}

		BigDecimal pastDues = receiptCalculator.getTotalNetPastDue(recData);
		recData.setTotalPastDues(pastDues);
		for (FinReceiptDetail rcd : recData.getReceiptHeader().getReceiptDetails()) {
			recData = updateExcessPay(recData, payType(rcd.getPaymentType()), rcd.getPayAgainstID(), rcd.getAmount());
			if (recData.getTotalPastDues().compareTo(rcd.getAmount()) >= 0) {
				rcd.setDueAmount(rcd.getAmount());
				recData.setTotalPastDues(recData.getTotalPastDues().subtract(rcd.getAmount()));
			} else {
				rcd.setDueAmount(recData.getTotalPastDues());
				recData.setTotalPastDues(BigDecimal.ZERO);
			}
		}
		Cloner cloner = new Cloner();
		List<FinanceScheduleDetail> finSchdDtls = cloner
				.deepClone(recData.getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails());

		if (StringUtils.equals(rch.getReceiptPurpose(), FinanceConstants.FINSER_EVENT_EARLYRPY)) {
			receiptCalculator.addPartPaymentAlloc(recData);
		}
		recData.setBuildProcess("R");
		for (ReceiptAllocationDetail allocate : recData.getReceiptHeader().getAllocations()) {
			allocate.setPaidAvailable(allocate.getPaidAmount());
			allocate.setWaivedAvailable(allocate.getWaivedAmount());
			allocate.setPaidAmount(BigDecimal.ZERO);
			allocate.setPaidGST(BigDecimal.ZERO);
			allocate.setTotalPaid(BigDecimal.ZERO);
			allocate.setBalance(allocate.getTotalDue());
			allocate.setWaivedAmount(BigDecimal.ZERO);
			allocate.setWaivedGST(BigDecimal.ZERO);
			allocate.setTdsPaid(BigDecimal.ZERO);
			allocate.setTdsWaived(BigDecimal.ZERO);
		}
		receiptCalculator.initiateReceipt(recData, false);
		recData.getFinanceDetail().getFinScheduleData().setFinanceScheduleDetails(finSchdDtls);
		recData.getFinanceDetail().getFinScheduleData().setFeeEvent(eventCode);

		return recData;
	}

	public boolean checkDueAdjusted(List<ReceiptAllocationDetail> allocations, FinReceiptData receiptData) {
		boolean isDueAdjusted = true;
		for (ReceiptAllocationDetail allocate : allocations) {
			BigDecimal bal = allocate.getTotalDue().subtract(allocate.getTotalPaid())
					.subtract(allocate.getWaivedAmount());

			if (allocate.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0
					&& FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(allocate.getTaxType())) {
				TaxAmountSplit taxSplit = new TaxAmountSplit();
				taxSplit.setAmount(allocate.getWaivedAmount());
				taxSplit.setTaxType(allocate.getTaxType());
				taxSplit = getReceiptCalculator().getGST(receiptData.getFinanceDetail(), taxSplit);
				bal = bal.subtract(taxSplit.gettGST());
			}

			if (bal.compareTo(BigDecimal.ZERO) > 0 && allocate.isEditable()) {
				isDueAdjusted = false;
			}
		}
		return isDueAdjusted;
	}

	public FinReceiptData adjustToExcess(FinReceiptData receiptData) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		rch.setPrvReceiptPurpose(rch.getReceiptPurpose());
		receiptData.getFinanceDetail().getFinScheduleData().setFinanceScheduleDetails(
				getFinanceScheduleDetailDAO().getFinScheduleDetails(rch.getReference(), "", false));
		rch.setReceiptPurpose(FinanceConstants.FINSER_EVENT_SCHDRPY);
		List<FinReceiptDetail> recDtls = rch.getReceiptDetails();
		List<FinReceiptDetail> newRecDtls = new ArrayList<>();
		rch.getTotalPastDues().setTotalPaid(BigDecimal.ZERO);
		rch.getTotalRcvAdvises().setTotalPaid(BigDecimal.ZERO);
		rch.getTotalBounces().setTotalPaid(BigDecimal.ZERO);
		rch.getTotalFees().setTotalPaid(BigDecimal.ZERO);
		for (FinReceiptDetail rcd : recDtls) {
			if (!PennantStaticListUtil.getExcessList().contains(rcd.getPaymentType())) {
				FinRepayHeader rph = new FinRepayHeader();
				rph.setFinReference(rch.getReference());
				rph.setValueDate(rch.getValueDate());
				rph.setFinEvent(rch.getReceiptPurpose());
				rph.setRepayAmount(rcd.getAmount());
				rph.setExcessAmount(rcd.getAmount());
				rcd.setRepayHeader(rph);
				newRecDtls.add(rcd);
			}
		}
		rch.setReceiptDetails(newRecDtls);
		return receiptData;
	}

	public FinanceDetail receiptTransaction(FinServiceInstruction fsi, String moduleDefiner) throws ServiceException {
		logger.debug("Entering");

		String eventCode = null;
		if (StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_SCHDRPY)) {
			eventCode = AccountEventConstants.ACCEVENT_REPAY;
			fsi.setModuleDefiner(FinanceConstants.FINSER_EVENT_SCHDRPY);
		} else if (StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_EARLYRPY)) {
			eventCode = AccountEventConstants.ACCEVENT_EARLYPAY;
			fsi.setModuleDefiner(FinanceConstants.FINSER_EVENT_EARLYRPY);
		} else if (StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
			eventCode = AccountEventConstants.ACCEVENT_EARLYSTL;
			fsi.setModuleDefiner(FinanceConstants.FINSER_EVENT_EARLYSETTLE);
		}

		int moduleCtg = receiptCalculator.setReceiptCategory(moduleDefiner);

		// Method for validate instruction details
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		fsi.setReceiptPurpose(moduleDefiner);
		finScheduleData.setFinServiceInstruction(fsi);
		FinanceProfitDetail fpd = profitDetailsDAO.getFinProfitDetailsById(fsi.getFinReference());
		finScheduleData.setFinPftDeatil(fpd);
		financeDetail = validateInstructions(financeDetail, moduleDefiner, eventCode);

		FinReceiptData receiptData = doReceiptValidations(financeDetail, moduleDefiner);
		financeDetail = receiptData.getFinanceDetail();
		finScheduleData = financeDetail.getFinScheduleData();

		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			logger.debug("Leaving - doReceiptValidations Error");
			return setReturnStatus(financeDetail);
		}

		LoggedInUser userDetails = null;
		if (SessionUserDetails.getLogiedInUser() != null) {
			userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		} else {
			userDetails = fsi.getLoggedInUser();
		}

		receiptData.setUserDetails(userDetails);

		if (fsi.isReceiptUpload() && StringUtils.equals(fsi.getReqType(), "Inquiry")) {
			return financeDetail;
		}

		receiptData = setReceiptData(receiptData);
		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			return setReturnStatus(financeDetail);
		}

		financeDetail = doReceiptTransaction(receiptData, eventCode);

		if (financeDetail.getFinScheduleData() != null && financeDetail.getFinScheduleData().getErrorDetails() != null
				&& !financeDetail.getFinScheduleData().getErrorDetails().isEmpty()) {
			financeDetail = setReturnStatus(financeDetail);
		}

		logger.debug("Leaving");
		return financeDetail;
	}

	public FinanceDetail setReturnStatus(FinanceDetail financeDetail) {
		WSReturnStatus returnStatus = new WSReturnStatus();
		ErrorDetail errorDetail = financeDetail.getFinScheduleData().getErrorDetails().get(0);
		returnStatus.setReturnCode(errorDetail.getCode());
		returnStatus.setReturnText(errorDetail.getError());
		financeDetail.setFinScheduleData(null);
		financeDetail.setDocumentDetailsList(null);
		financeDetail.setJountAccountDetailList(null);
		financeDetail.setGurantorsDetailList(null);
		financeDetail.setCollateralAssignmentList(null);
		financeDetail.setReturnDataSetList(null);
		// financeDetail.setInterfaceDetailList(null);
		financeDetail.setFinFlagsDetails(null);
		financeDetail.setReturnStatus(returnStatus);
		return financeDetail;
	}

	private FinanceDetail validateInstructions(FinanceDetail financeDetail, String moduleDefiner, String eventCode) {
		logger.debug("Entering");

		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinServiceInstruction finServiceInstruction = finScheduleData.getFinServiceInstruction();
		String finReference = finServiceInstruction.getFinReference();
		financeDetail.setFinReference(finReference);

		if (!StringUtils.equals(finServiceInstruction.getReqType(), "Inquiry")
				&& !StringUtils.equals(finServiceInstruction.getReqType(), "Post")) {
			finScheduleData = setErrorToFSD(finScheduleData, "91113", finServiceInstruction.getReqType());
		}

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * 
	 * @param finServiceInstruction
	 * @param eventCode
	 * @return
	 */
	public FinanceDetail doReceiptTransaction(FinReceiptData receiptData, String eventCode) {
		logger.debug("Enteing");

		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		if (financeDetail.getFinScheduleData().getErrorDetails() != null
				&& !financeDetail.getFinScheduleData().getErrorDetails().isEmpty()) {
			logger.debug("Leaving - doReceiptTransaction");
			return financeDetail;
		}
		FinServiceInstruction finServiceInstruction = finScheduleData.getFinServiceInstruction();

		if (StringUtils.equals(finServiceInstruction.getReqType(), "Inquiry")) {
			if (finServiceInstruction.getToDate() == null) {
				finServiceInstruction.setToDate(finScheduleData.getFinanceMain().getMaturityDate());
			}

		}

		String receiptPurpose = financeDetail.getFinScheduleData().getFinServiceInstruction().getModuleDefiner();
		if (!finServiceInstruction.isReceiptUpload()) {
			financeDetail = validateFees(financeDetail);
		}

		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			logger.debug("Leaving");
			return financeDetail;
		}

		try {
			financeDetail = doProcessReceipt(receiptData, receiptPurpose);

			if (!finServiceInstruction.isReceiptUpload()
					&& StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
				if (finScheduleData.getErrorDetails() == null || !finScheduleData.getErrorDetails().isEmpty()) {
					FinanceSummary summary = financeDetail.getFinScheduleData().getFinanceSummary();
					summary.setFinStatus("M");
				}
			}

		} catch (InterfaceException ex) {
			logger.error("InterfaceException", ex);
			finScheduleData = setErrorToFSD(finScheduleData, "9998", ex.getMessage());
			setReturnStatus(financeDetail);
			return financeDetail;
		} catch (AppException appEx) {
			logger.error("AppException", appEx);
			finScheduleData = setErrorToFSD(finScheduleData, "9999", appEx.getMessage());
			setReturnStatus(financeDetail);
			return financeDetail;
		} catch (Exception e) {
			logger.error("Exception", e);
			finScheduleData = setErrorToFSD(finScheduleData, "9999", "Unable to process request.");
			setReturnStatus(financeDetail);
			return financeDetail;
		}

		logger.debug("Leaving");
		return financeDetail;
	}

	public void setReceiptModeStatus(FinReceiptHeader rch) {
		if ((rch.getNextRoleCode() == null || rch.getNextRoleCode().equals(FinanceConstants.REALIZATION_MAKER))
				&& StringUtils.equals(RepayConstants.PAYSTATUS_INITIATED, rch.getReceiptModeStatus())) {
			rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_DEPOSITED);
		}
	}

	public FinanceDetail validateFees(FinanceDetail financeDetail) {
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinServiceInstruction finServiceInst = finScheduleData.getFinServiceInstruction();
		String roundingMode = finScheduleData.getFinanceMain().getCalRoundingMode();
		int roundingTarget = finScheduleData.getFinanceMain().getRoundingTarget();

		// FIXME: PV AS OF NOW, PNLY ONE FEE IS HANDLED. AFTER FIRST RELEASE
		// MULTI FEES TO BE DEVELOPED.
		// GST to be tested
		boolean isAPIFeeRequested = false;
		boolean isEventFeeRequired = false;
		String apiFeeCode = null;
		BigDecimal apiActualFee = BigDecimal.ZERO;
		BigDecimal apiPaidFee = BigDecimal.ZERO;
		BigDecimal apiWaived = BigDecimal.ZERO;
		String eventFeeCode = null;
		BigDecimal eventActualFee = BigDecimal.ZERO;
		BigDecimal maxWaiver = BigDecimal.ZERO;
		BigDecimal maxWaiverAllowed = BigDecimal.ZERO;

		// Validate Fees
		if (finServiceInst.getFinFeeDetails() != null && !finServiceInst.getFinFeeDetails().isEmpty()) {
			isAPIFeeRequested = true;
			apiFeeCode = finServiceInst.getFinFeeDetails().get(0).getFeeTypeCode().toUpperCase();
			apiActualFee = finServiceInst.getFinFeeDetails().get(0).getActualAmount();
			apiPaidFee = finServiceInst.getFinFeeDetails().get(0).getPaidAmount();
			apiWaived = finServiceInst.getFinFeeDetails().get(0).getWaivedAmount();
		}

		if (finScheduleData.getFinFeeDetailList() != null && !finScheduleData.getFinFeeDetailList().isEmpty()) {
			isEventFeeRequired = true;
			eventFeeCode = finScheduleData.getFinFeeDetailList().get(0).getFeeTypeCode();
			eventActualFee = finScheduleData.getFinFeeDetailList().get(0).getActualAmount();
			maxWaiver = finScheduleData.getFinFeeDetailList().get(0).getMaxWaiverPerc();

			if (maxWaiver.compareTo(BigDecimal.valueOf(100)) == 0) {
				maxWaiverAllowed = eventActualFee;
			} else if (maxWaiver.compareTo(BigDecimal.ZERO) > 0) {
				maxWaiverAllowed = eventActualFee.multiply(maxWaiver).divide(BigDecimal.valueOf(100), 0,
						RoundingMode.HALF_DOWN);
				maxWaiverAllowed = CalculationUtil.roundAmount(maxWaiverAllowed, roundingMode, roundingTarget);
			}
		}

		// Event fees not applicable and API not requested.
		if (!isAPIFeeRequested && !isEventFeeRequired) {
			return financeDetail;
		}

		// Fee is Mandatory but API does not requested
		if (!isAPIFeeRequested && isEventFeeRequired) {
			finScheduleData = setErrorToFSD(finScheduleData, "65019", eventFeeCode);
			return financeDetail;
		}

		// Mismatch in the Fees requirement.
		if (isAPIFeeRequested && !isEventFeeRequired) {
			finScheduleData = setErrorToFSD(finScheduleData, "90245", null);
			return financeDetail;
		}

		// Mismatch in the Fees requirement.
		if (!StringUtils.equalsIgnoreCase(apiFeeCode, eventFeeCode)) {
			finScheduleData = setErrorToFSD(finScheduleData, "90247", null);
			return financeDetail;
		}

		// Negative Amounts
		if (apiActualFee.compareTo(BigDecimal.ZERO) < 0 || apiPaidFee.compareTo(BigDecimal.ZERO) < 0
				|| apiWaived.compareTo(BigDecimal.ZERO) < 0) {
			finScheduleData = setErrorToFSD(finScheduleData, "90259", apiFeeCode);
			return financeDetail;
		}

		String parm0 = null;
		String parm1 = null;
		int formatter = CurrencyUtil.getFormat(finScheduleData.getFinanceMain().getFinCcy());

		// Waiver Exceeds the limit
		if (apiWaived.compareTo(maxWaiverAllowed) > 0) {
			parm0 = "Fee Waiver";
			parm1 = PennantApplicationUtil.amountFormate(maxWaiverAllowed, formatter);
			finScheduleData = setErrorToFSD(finScheduleData, "90257", parm0, parm1, apiFeeCode);
			return financeDetail;
		}

		// API Actual Amount <> EVENT Actual Amount
		if ((apiActualFee.compareTo(eventActualFee) != 0)) {
			parm0 = "Fee Amount";
			parm1 = PennantApplicationUtil.amountFormate(eventActualFee, formatter);
			finScheduleData = setErrorToFSD(finScheduleData, "90258", parm0, parm1, apiFeeCode);
			return financeDetail;
		}

		// Actual Amount - Paid - Waived <> 0
		if ((apiActualFee.subtract(apiPaidFee).subtract(apiWaived)).compareTo(BigDecimal.ZERO) != 0) {
			parm0 = "Fee Amount - Fee Waived";
			parm1 = "Fee Paid";
			finScheduleData = setErrorToFSD(finScheduleData, "90258", parm0, parm1, apiFeeCode);
			return financeDetail;
		}

		return financeDetail;
	}

	private boolean isSchdFullyPaid(String finReference, List<FinanceScheduleDetail> scheduleDetails) {
		// Check Total Finance profit Amount
		boolean fullyPaid = true;
		for (int i = 1; i < scheduleDetails.size(); i++) {
			FinanceScheduleDetail curSchd = scheduleDetails.get(i);

			// Profit
			if ((curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid())).compareTo(BigDecimal.ZERO) > 0) {
				fullyPaid = false;
				break;
			}

			// Principal
			if ((curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid())).compareTo(BigDecimal.ZERO) > 0) {
				fullyPaid = false;
				break;
			}

			// Fees
			if ((curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid())).compareTo(BigDecimal.ZERO) > 0) {
				fullyPaid = false;
				break;
			}

			// Insurance
			if ((curSchd.getInsSchd().subtract(curSchd.getSchdInsPaid())).compareTo(BigDecimal.ZERO) > 0) {
				fullyPaid = false;
				break;
			}

			// Supplementary Rent
			if ((curSchd.getSuplRent().subtract(curSchd.getSuplRentPaid())).compareTo(BigDecimal.ZERO) > 0) {
				fullyPaid = false;
				break;
			}

			// Increased Cost
			if ((curSchd.getIncrCost().subtract(curSchd.getIncrCostPaid())).compareTo(BigDecimal.ZERO) > 0) {
				fullyPaid = false;
				break;
			}
		}

		// Check Penalty Paid Fully or not
		if (fullyPaid) {
			FinODDetails overdue = getFinODDetailsDAO().getTotals(finReference);
			if (overdue != null) {
				BigDecimal balPenalty = overdue.getTotPenaltyAmt().subtract(overdue.getTotPenaltyPaid())
						.add(overdue.getLPIAmt().subtract(overdue.getLPIPaid()));

				// Penalty Not fully Paid
				if (balPenalty.compareTo(BigDecimal.ZERO) > 0) {
					fullyPaid = false;
				}
			}
		}

		return fullyPaid;
	}

	private FinanceDetail doProcessReceipt(FinReceiptData receiptData, String receiptPurpose) throws Exception {
		logger.debug("Entering");

		// FinReceiptData receiptData = setReceiptData(financeDetail,
		// receiptPurpose);
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		FinReceiptDetail rcd = rch.getReceiptDetails().get(0);
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinServiceInstruction fsi = finScheduleData.getFinServiceInstruction();
		FinanceMain financeMain = finScheduleData.getFinanceMain();

		receiptData.setTotalPastDues(receiptCalculator.getTotalNetPastDue(receiptData));
		if (receiptData.getTotalPastDues().compareTo(rch.getReceiptAmount()) >= 0) {
			rcd.setDueAmount(rch.getReceiptAmount());
			receiptData.setTotalPastDues(receiptData.getTotalPastDues().subtract(rch.getReceiptAmount()));
		} else {
			rcd.setDueAmount(receiptData.getTotalPastDues());
			receiptData.setTotalPastDues(BigDecimal.ZERO);
		}

		int receiptPurposeCtg = receiptCalculator.setReceiptCategory(receiptPurpose);

		if (receiptPurposeCtg == 2) {
			rch.getReceiptDetails().clear();
			createXcessRCD(receiptData);
			rch.getReceiptDetails().add(rcd);
		}

		if (!fsi.isReceiptUpload() && !StringUtils.equals(fsi.getReqType(), "Post")) {

			BigDecimal earlyPayAmount = receiptData.getRemBal();
			String recalType = rch.getEffectSchdMethod();
			finScheduleData.getFinanceMain().setReceiptPurpose(receiptPurpose);

			// Calculate Schedule if Part Payment case
			if (receiptPurposeCtg == 1) {
				finScheduleData = ScheduleCalculator.recalEarlyPaySchedule(finScheduleData, rch.getValueDate(), null,
						earlyPayAmount, recalType);
				receiptData = receiptCalculator.addPartPaymentAlloc(receiptData);
			}
			receiptData.getFinanceDetail().setFinScheduleData(finScheduleData);

			// Allocations Preparation
			for (ReceiptAllocationDetail allocate : receiptData.getReceiptHeader().getAllocations()) {
				allocate.setPaidAvailable(allocate.getPaidAmount());
				allocate.setWaivedAvailable(allocate.getWaivedAmount());
				allocate.setPaidAmount(BigDecimal.ZERO);
				allocate.setPaidGST(BigDecimal.ZERO);
				allocate.setTotalPaid(BigDecimal.ZERO);
				allocate.setBalance(allocate.getTotalDue());
				allocate.setWaivedAmount(BigDecimal.ZERO);
				allocate.setWaivedGST(BigDecimal.ZERO);
			}
			receiptData.setBuildProcess("R");
			receiptData = receiptCalculator.initiateReceipt(receiptData, false);
			// financeDetail =
			// getServiceInstResponse(receiptData.getFinanceDetail().getFinScheduleData());
			FinanceSummary summary = financeDetail.getFinScheduleData().getFinanceSummary();
			summary.setFinODDetail(rch.getFinODDetails());
			financeDetail.getFinScheduleData().setFinODDetails(rch.getFinODDetails());

			logger.debug("Leaving");
			return financeDetail;
		}

		if (fsi.isReceiptUpload() && dedupCheckRequest(rch, receiptPurpose)) {
			long rchID = CheckDedupSP(rch, receiptPurpose);

			// If receipt already exists, and status update required for
			// realization
			if (rchID > 0) {
				finReceiptHeaderDAO.updateReceiptStatusAndRealizationDate(rchID, RepayConstants.PAYSTATUS_REALIZED,
						rch.getRealizationDate());
				finReceiptDetailDAO.updateReceiptStatusByReceiptId(rchID, RepayConstants.PAYSTATUS_REALIZED);
				setErrorToFSD(finScheduleData, "0000", "Success");
				return financeDetail;
			}
		}

		// fetch partner bank details
		PartnerBank partnerBank = partnerBankDAO.getPartnerBankById(rcd.getFundingAc(), "");
		if (partnerBank != null) {
			rcd.setPartnerBankAc(partnerBank.getAccountNo());
			rcd.setPartnerBankAcType(partnerBank.getAcType());
		}

		int version = 0;
		// Receipt upload process
		if (fsi.isReceiptdetailExits()) {
			FinReceiptData oldReceiptData = this.getFinReceiptDataById(fsi.getFinReference(),
					AccountEventConstants.ACCEVENT_REPAY, FinanceConstants.FINSER_EVENT_RECEIPT,
					FinanceConstants.REALIZATION_MAKER);
			receiptData = oldReceiptData;

			receiptData.getReceiptHeader().setRealizationDate(fsi.getRealizationDate());
		} else {
			// Set Version value
			version = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().getVersion();
			receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().setVersion(version + 1);
			receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().setRecordType("");
			receiptData.getFinanceDetail().getFinScheduleData().setSchduleGenerated(true);
		}

		// Save the Schedule details
		AuditHeader auditHeader = getAuditHeader(receiptData, PennantConstants.TRAN_WF);

		// setting to temp table
		if (!fsi.isReceiptdetailExits() && fsi.isReceiptUpload() && receiptPurposeCtg != 0
				&& StringUtils.equals(fsi.getStatus(), "A") && (StringUtils.equals(fsi.getPaymentMode(), "CHEQUE")
						|| StringUtils.equals(fsi.getPaymentMode(), "DD"))) {

			WorkFlowDetails workFlowDetails = null;
			String roleCode = FinanceConstants.DEPOSIT_APPROVER;// default value
			String nextRolecode = FinanceConstants.REALIZATION_MAKER;// defaulting
																		// role
																		// codes
			String taskid = null;
			String nextTaskId = null;
			long workFlowId = 0;

			String finEvent = FinanceConstants.FINSER_EVENT_RECEIPT;
			FinanceWorkFlow financeWorkFlow = financeWorkFlowDAO.getFinanceWorkFlowById(financeMain.getFinType(),
					finEvent, PennantConstants.WORFLOW_MODULE_FINANCE, "");

			if (financeWorkFlow != null) {
				workFlowDetails = WorkFlowUtil.getDetailsByType(financeWorkFlow.getWorkFlowType());
				if (workFlowDetails != null) {
					WorkflowEngine workFlow = new WorkflowEngine(workFlowDetails.getWorkFlowXml());
					taskid = workFlow.getUserTaskId(roleCode);
					workFlowId = workFlowDetails.getWorkFlowId();
					nextTaskId = workFlow.getUserTaskId(nextRolecode);
				}

				rch.setWorkflowId(workFlowId);
				rch.setTaskId(taskid);
				rch.setRoleCode(roleCode);
				rch.setNextRoleCode(nextRolecode);
				rch.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				rch.setNextTaskId(nextTaskId + ";");
				rch.setNewRecord(true);
				rch.setVersion(version + 1);
				rch.setRcdMaintainSts(FinanceConstants.FINSER_EVENT_RECEIPT);
				rch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				rch.setRecordStatus(PennantConstants.RCD_STATUS_SUBMITTED);
			}

			receiptData.getFinanceDetail().getFinScheduleData().setFinanceMain(financeMain);
			/*
			 * APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
			 * .get(APIHeader.API_HEADER_KEY); auditHeader.setApiHeader(reqHeaderDetails);
			 */

			auditHeader = saveOrUpdate(auditHeader);

		} else {

			/*
			 * APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
			 * .get(APIHeader.API_HEADER_KEY); auditHeader.setApiHeader(reqHeaderDetails);
			 */
			BigDecimal earlyPayAmount = receiptData.getRemBal();
			String recalType = rch.getEffectSchdMethod();
			finScheduleData.getFinanceMain().setReceiptPurpose(receiptPurpose);
			if (receiptPurposeCtg == 1) {
				finScheduleData = ScheduleCalculator.recalEarlyPaySchedule(finScheduleData, rch.getValueDate(), null,
						earlyPayAmount, recalType);
				receiptData = receiptCalculator.addPartPaymentAlloc(receiptData);
			}
			Cloner cloner = new Cloner();
			FinReceiptData tempReceiptData = cloner.deepClone(receiptData);
			receiptData.getFinanceDetail().setFinScheduleData(finScheduleData);
			receiptData.getReceiptHeader().setValueDate(rch.getValueDate());

			receiptData.setDueAdjusted(true);
			if (receiptPurposeCtg == 2) {
				boolean duesAdjusted = checkDueAdjusted(receiptData.getReceiptHeader().getAllocations(), receiptData);
				if (!duesAdjusted) {
					receiptData = adjustToExcess(receiptData);
					receiptData.setDueAdjusted(false);
				}
			}

			if (receiptData.isDueAdjusted()) {
				for (ReceiptAllocationDetail allocate : receiptData.getReceiptHeader().getAllocations()) {
					allocate.setPaidAvailable(allocate.getPaidAmount());
					allocate.setWaivedAvailable(allocate.getWaivedAmount());
					allocate.setPaidAmount(BigDecimal.ZERO);
					allocate.setPaidGST(BigDecimal.ZERO);
					allocate.setTotalPaid(BigDecimal.ZERO);
					allocate.setBalance(allocate.getTotalDue());
					allocate.setWaivedAmount(BigDecimal.ZERO);
					allocate.setWaivedGST(BigDecimal.ZERO);
				}
			} else {
				finScheduleData = financeDetail.getFinScheduleData();
				finScheduleData = setErrorToFSD(finScheduleData, "90330", receiptPurpose, "");
				financeDetail.setFinScheduleData(finScheduleData);
				return financeDetail;
			}

			receiptData.setBuildProcess("R");
			receiptData = receiptCalculator.initiateReceipt(receiptData, false);

			receiptData.getFinanceDetail().getFinScheduleData().setFinanceScheduleDetails(
					tempReceiptData.getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails());
			auditHeader = doApprove(auditHeader);
		}

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail auditErrorDetail : auditHeader.getErrorMessage()) {
				setErrorToFSD(finScheduleData, auditErrorDetail.getCode(), auditErrorDetail.getError());
				return financeDetail;
			}
		}

		receiptData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		// FIXME: PV re-look at it
		// financeDetail =
		// getServiceInstResponse(receiptData.getFinanceDetail().getFinScheduleData());

		List<FinServiceInstruction> finServInstList = new ArrayList<>();
		for (FinReceiptDetail recDtl : rch.getReceiptDetails()) {
			FinRepayHeader rpyHeader = recDtl.getRepayHeader();
			FinServiceInstruction finServInst = new FinServiceInstruction();
			finServInst.setFinReference(financeMain.getFinReference());
			finServInst.setFinEvent(rpyHeader.getFinEvent());
			finServInst.setAmount(rpyHeader.getRepayAmount());
			finServInst.setAppDate(SysParamUtil.getAppDate());
			finServInst.setSystemDate(DateUtility.getSysDate());
			finServInst.setMaker(auditHeader.getAuditUsrId());
			finServInst.setMakerAppDate(SysParamUtil.getAppDate());
			finServInst.setMakerSysDate(DateUtil.getSysDate());
			finServInst.setChecker(auditHeader.getAuditUsrId());
			finServInst.setCheckerAppDate(SysParamUtil.getAppDate());
			finServInst.setCheckerSysDate(DateUtil.getSysDate());
			finServInst.setReference(String.valueOf(rch.getReceiptID()));
			finServInstList.add(finServInst);
		}
		if (finServInstList.size() > 0) {
			finServiceInstructionDAO.saveList(finServInstList, "");
		}

		// set receipt id in data
		if (fsi.isReceiptUpload() && !fsi.isReceiptResponse()) {
			this.receiptUploadDetailDAO.updateReceiptId(fsi.getUploadDetailId(),
					receiptData.getReceiptHeader().getReceiptID());
		}

		// set receipt id response job
		if (fsi.isReceiptUpload() && fsi.isReceiptResponse()) {
			this.receiptResponseDetailDAO.updateReceiptResponseId(fsi.getRootId(),
					receiptData.getReceiptHeader().getReceiptID());
		}

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Get Audit Header Details
	 */
	private AuditHeader getAuditHeader(FinReceiptData repayData, String tranType) {
		// FIXME: PV: CODE REVIEW PENDING
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, repayData);
		return new AuditHeader(repayData.getFinReference(), null, null, null, auditDetail,
				repayData.getFinanceDetail().getFinScheduleData().getFinanceMain().getUserDetails(), null);
	}

	@Override
	public FinReceiptData updateExcessPay(FinReceiptData receiptData, String rcMode, long id, BigDecimal amount) {
		if (!StringUtils.equals(RepayConstants.EXAMOUNTTYPE_EMIINADV, rcMode)
				&& !StringUtils.equals(RepayConstants.EXAMOUNTTYPE_EXCESS, rcMode)
				&& !StringUtils.equals(RepayConstants.EXAMOUNTTYPE_PAYABLE, rcMode)
				&& !StringUtils.equals(RepayConstants.EXAMOUNTTYPE_CASHCLT, rcMode)
				&& !StringUtils.equals(RepayConstants.EXAMOUNTTYPE_DSF, rcMode)) {
			return receiptData;
		}

		int receiptPurposeCtg = getReceiptCalculator()
				.setReceiptCategory(receiptData.getReceiptHeader().getReceiptPurpose());

		FinReceiptHeader rch = receiptData.getReceiptHeader();
		List<XcessPayables> xcessPayables = rch.getXcessPayables();
		for (int i = 0; i < xcessPayables.size(); i++) {
			XcessPayables payable = xcessPayables.get(i);
			if (rcMode.equals(payable.getPayableType()) && id == payable.getPayableID()) {
				if (receiptPurposeCtg == 2 && (StringUtils.equals(receiptData.getReceiptHeader().getRoleCode(),
						FinanceConstants.RECEIPT_MAKER)
						|| StringUtils.equals(receiptData.getReceiptHeader().getRoleCode(),
								FinanceConstants.RECEIPT_APPROVER))) {
					payable.setTotPaidNow(receiptData.getExcessAvailable());
				} else if (receiptPurposeCtg == 2 && (StringUtils.equals(receiptData.getReceiptHeader().getRoleCode(),
						FinanceConstants.CLOSURE_MAKER)
						|| StringUtils.equals(receiptData.getReceiptHeader().getRoleCode(),
								FinanceConstants.CLOSURE_APPROVER))) {
					payable.setTotPaidNow(
							receiptData.getTotalPastDues().add(receiptData.getReceiptHeader().getBalAmount()));
				} else {
					payable.setTotPaidNow(amount);
				}
				break;
			}
		}
		return receiptData;
	}

	private String payType(String mode) {
		String payType = "";
		if (StringUtils.equals(mode, RepayConstants.RECEIPTMODE_EMIINADV)) {
			payType = RepayConstants.EXAMOUNTTYPE_EMIINADV;
		} else if (StringUtils.equals(mode, RepayConstants.RECEIPTMODE_EXCESS)) {
			payType = RepayConstants.EXAMOUNTTYPE_EXCESS;
		} else if (StringUtils.equals(mode, RepayConstants.RECEIPTMODE_PAYABLE)) {
			payType = RepayConstants.EXAMOUNTTYPE_PAYABLE;
		} else if (StringUtils.equals(mode, RepayConstants.RECEIPTMODE_CASHCLT)) {
			payType = RepayConstants.EXAMOUNTTYPE_CASHCLT;
		} else if (StringUtils.equals(mode, RepayConstants.RECEIPTMODE_DSF)) {
			payType = RepayConstants.EXAMOUNTTYPE_DSF;
		}
		return payType;
	}

	@Override
	public FinReceiptData calcuateDues(FinReceiptData receiptData) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		int receiptPurposeCtg = receiptCalculator.setReceiptCategory(rch.getReceiptPurpose());
		FinanceMain finMain = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();
		Date valueDate = rch.getValueDate();
		receiptData.setBuildProcess("I");
		receiptData.setAllocList(rch.getAllocations());
		receiptData.setValueDate(valueDate);
		rch.setValueDate(null);
		receiptData = receiptCalculator.initiateReceipt(receiptData, false);
		if (receiptPurposeCtg == 2 && DateUtility.compare(valueDate, finMain.getMaturityDate()) < 0
				&& finMain.isFinIsActive()) {
			receiptData.getReceiptHeader().setValueDate(null);
			receiptData.setOrgFinPftDtls(receiptData.getFinanceDetail().getFinScheduleData().getFinPftDeatil());
			receiptData.getRepayMain().setEarlyPayOnSchDate(valueDate);
			recalEarlyPaySchedule(receiptData);
			receiptData.setBuildProcess("I");
			receiptData = getReceiptCalculator().initiateReceipt(receiptData, false);
			receiptData.setActualReceiptAmount(rch.getReceiptAmount().subtract(receiptData.getExcessAvailable()));
			receiptData.setExcessAvailable(getReceiptCalculator().getExcessAmount(receiptData));
			if (receiptData.isForeClosure()) {
				rch.setReceiptAmount(BigDecimal.ZERO);
				rch.setReceiptAmount(receiptData.getExcessAvailable());
			} else {
				if (rch.getReceiptDetails().size() > 1) {
					rch.setReceiptAmount(receiptData.getActualReceiptAmount());
				}
			}
		}
		return receiptData;
	}

	public FinanceDetail getFinanceDetail(FinServiceInstruction finServiceInst, String eventCode) {
		logger.debug("Entering");

		FinanceDetail financeDetail = null;

		String finReference = finServiceInst.getFinReference();
		String finSerEvent = "";
		if ("SP".equalsIgnoreCase(finServiceInst.getReceiptPurpose())) {
			finSerEvent = FinanceConstants.FINSER_EVENT_SCHDRPY;
		} else if ("EP".equalsIgnoreCase(finServiceInst.getReceiptPurpose())) {
			finSerEvent = FinanceConstants.FINSER_EVENT_EARLYRPY;
		} else {
			finSerEvent = FinanceConstants.FINSER_EVENT_EARLYSETTLE;
		}
		if (!finServiceInst.isWif()) {
			financeDetail = financeDetailService.getFinanceDetailById(finReference, false, "", false, finSerEvent, "");
		} else {
			financeDetail = financeDetailService.getWIFFinance(finReference, false, null);
		}

		List<FinFeeDetail> newList = new ArrayList<FinFeeDetail>();
		if (financeDetail != null) {
			if (financeDetail.getFinScheduleData().getFinFeeDetailList() != null) {
				for (FinFeeDetail feeDetail : financeDetail.getFinScheduleData().getFinFeeDetailList()) {
					if (finSerEvent.equalsIgnoreCase(feeDetail.getFinEvent())) {
						if (feeDetail.isOriginationFee()) {
							feeDetail.setOriginationFee(true);
							feeDetail.setRcdVisible(false);
							feeDetail.setRecordType(PennantConstants.RCD_UPD);
							feeDetail.setRecordStatus(PennantConstants.RECORD_TYPE_UPD);
							newList.add(feeDetail);
						}
					}
				}
			}
			financeDetail.getFinScheduleData().setFinFeeDetailList(newList);
			financeDetail.setAccountingEventCode(eventCode);
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			financeDetail.getFinScheduleData().getFinanceMain().setUserDetails(userDetails);
			financeDetail.setEtihadCreditBureauDetail(null);
		}

		logger.debug("Leaving");

		return financeDetail;
	}

	/**
	 * Method for Validating Partial Settlement amount collected form Customer with in Year(Find Based on System
	 * Parameters)
	 * 
	 * Year(Find Based on System Parameters)
	 */
	private ErrorDetail validatePPPercAmount(FinReceiptData receiptData) {

		FinReceiptHeader receiptHeader = receiptData.getReceiptHeader();
		if (!StringUtils.equals(receiptHeader.getReceiptPurpose(), FinanceConstants.FINSER_EVENT_EARLYRPY)) {
			return null;
		}

		if (StringUtils.equals(receiptHeader.getReceiptModeStatus(), RepayConstants.PAYSTATUS_BOUNCE)
				|| StringUtils.equals(receiptHeader.getReceiptModeStatus(), RepayConstants.PAYSTATUS_CANCEL)) {
			return null;
		}

		// validation for partial payment based on percentage
		List<FinanceScheduleDetail> scheduleList = receiptData.getFinanceDetail().getFinScheduleData()
				.getFinanceScheduleDetails();
		int finFormatter = CurrencyUtil
				.getFormat(receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

		// Value Date Finding
		Date valueDate = SysParamUtil.getAppDate();
		for (FinReceiptDetail receiptDetail : receiptHeader.getReceiptDetails()) {
			if (StringUtils.equals(receiptHeader.getReceiptMode(), receiptDetail.getPaymentType())) {
				valueDate = receiptDetail.getReceivedDate();
				break;
			}
		}

		Date startDate = null;
		Date endDate = null;

		int startPeriodMonth = SysParamUtil.getValueAsInt((SMTParameterConstants.EARLYPAY_FY_STARTMONTH));
		int startYear = DateUtility.getYear(valueDate);
		int startDay = 1;
		Date date = DateUtility.getDate(startYear, startPeriodMonth, startDay);
		startDate = date;
		if (DateUtility.compare(date, valueDate) == 1) {
			date = DateUtility.addYears(date, -1);
			startDate = date;
		}
		date = DateUtility.addMonths(date, 11);
		date = DateUtility.getMonthEnd(date);
		endDate = date;

		// Finding Closing Balance at FY Start Date
		BigDecimal closingBal = BigDecimal.ZERO;
		if (CollectionUtils.isNotEmpty(scheduleList)) {
			for (int i = 0; i < scheduleList.size(); i++) {
				FinanceScheduleDetail curSchd = scheduleList.get(i);
				if (i == 0 || DateUtility.compare(curSchd.getSchDate(), startDate) <= 0) {
					closingBal = PennantApplicationUtil.formateAmount(curSchd.getClosingBalance(), finFormatter);
				}
			}
		}

		BigDecimal utilizedPartPayAmt = PennantApplicationUtil.formateAmount(
				finReceiptDetailDAO.getUtilizedPartPayAmtByDate(receiptHeader, startDate, endDate), finFormatter);
		BigDecimal alwdPPPerc = new BigDecimal(
				SysParamUtil.getValueAsInt(SMTParameterConstants.ALWD_EARLYPAY_PERC_BYYEAR));

		BigDecimal maxAlwdPPByFY = (closingBal.multiply(alwdPPPerc)).divide(new BigDecimal(100), 0,
				RoundingMode.HALF_DOWN);

		// Current Part Payment Amount
		BigDecimal curPPAmount = BigDecimal.ZERO;
		for (FinReceiptDetail rcd : receiptHeader.getReceiptDetails()) {
			for (FinRepayHeader rph : rcd.getRepayHeaders()) {
				if (!StringUtils.equals(rph.getFinEvent(), FinanceConstants.FINSER_EVENT_EARLYRPY)) {
					continue;
				}
				curPPAmount = curPPAmount.add(PennantApplicationUtil.formateAmount(rph.getPriAmount(), finFormatter));
			}
		}

		if ((utilizedPartPayAmt.add(curPPAmount)).compareTo(maxAlwdPPByFY) > 0) {
			if (utilizedPartPayAmt.compareTo(maxAlwdPPByFY) >= 0) {
				return ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "RU0046", null, null));
			} else {
				BigDecimal maxAlwdCurPP = maxAlwdPPByFY.subtract(utilizedPartPayAmt);
				String[] valueParm = new String[1];
				valueParm[0] = maxAlwdCurPP.toString();
				return ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "RU0047", valueParm, null));
			}
		}
		return null;
	}

	public FinReceiptData validateAllocationsAmount(FinReceiptData receiptData) {
		logger.debug(Literal.ENTERING);

		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinServiceInstruction fsi = finScheduleData.getFinServiceInstruction();
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		List<UploadAlloctionDetail> ulAllocations = fsi.getUploadAllocationDetails();
		List<ReceiptAllocationDetail> allocationsList = rch.getAllocations();
		Cloner cloner = new Cloner();
		FinReceiptData aReceiptData = cloner.deepClone(receiptData);
		BigDecimal totalWaivedAmt = BigDecimal.ZERO;
		BigDecimal excessPaid = BigDecimal.ZERO;
		String parm0 = null;

		if (CollectionUtils.isNotEmpty(financeDetail.getFinTypeFeesList())) {
			for (FinTypeFees feeType : financeDetail.getFinTypeFeesList()) {
				BigDecimal maxWaiverPerc = feeType.getMaxWaiverPerc();
				for (UploadAlloctionDetail ulAlc : ulAllocations) {
					String allocationType = ulAlc.getAllocationType();
					totalWaivedAmt = totalWaivedAmt.add(ulAlc.getWaivedAmount());
					if ("E".equals(allocationType)) {
						excessPaid = ulAlc.getPaidAmount();
					}
					if (!("F".equals(allocationType) || "M".equals(allocationType) || "B".equals(allocationType))) {
						continue;
					}
					if (maxWaiverPerc.compareTo(BigDecimal.ZERO) == 0
							&& ulAlc.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0) {
						parm0 = "max waiver percentage is " + maxWaiverPerc.toString() + " for type :"
								+ feeType.getFeeTypeCode();
						setErrorToFSD(finScheduleData, "30550", parm0);
						return receiptData;
					}
				}
			}
		} else {
			for (UploadAlloctionDetail ulAlc : ulAllocations) {
				String allocationType = ulAlc.getAllocationType();
				totalWaivedAmt = totalWaivedAmt.add(ulAlc.getWaivedAmount());
				if ("E".equals(allocationType)) {
					excessPaid = ulAlc.getPaidAmount();
				}
			}
		}

		rch.setBalAmount(fsi.getAmount().add(totalWaivedAmt));
		receiptData.setReceiptHeader(rch);

		aReceiptData = calcuateDues(aReceiptData);
		if (receiptData != null) {
			BigDecimal pastDues = rch.getTotalPastDues().getTotalDue();
			BigDecimal totalBounces = rch.getTotalBounces().getTotalDue();
			BigDecimal totalRcvAdvises = rch.getTotalRcvAdvises().getTotalDue();
			BigDecimal totalFees = rch.getTotalFees().getTotalDue();
			BigDecimal excessAvailable = receiptData.getExcessAvailable();
			BigDecimal totalDues = pastDues.add(totalBounces).add(totalRcvAdvises).add(totalFees)
					.subtract(excessAvailable).subtract(totalWaivedAmt).add(excessPaid);
			if (totalDues.compareTo(fsi.getAmount()) != 0) {
				parm0 = "Invalid receipt amount. It should equal to total due :" + totalDues.toString();
				finScheduleData = setErrorToFSD(finScheduleData, "30550", parm0);
				return receiptData;
			}
		}

		for (int i = 0; i < ulAllocations.size(); i++) {
			UploadAlloctionDetail ulAlc = ulAllocations.get(i);
			String ulAlcType = ulAlc.getAllocationType();
			for (int j = 0; j < allocationsList.size(); j++) {
				ReceiptAllocationDetail allocate = allocationsList.get(j);
				String alcType = null;

				if (StringUtils.equals(allocate.getAllocationType(), RepayConstants.ALLOCATION_NPFT)) {
					alcType = "I";
					if (StringUtils.equals(alcType, ulAlcType) && allocate.getTotalDue()
							.compareTo(ulAlc.getPaidAmount().add(ulAlc.getWaivedAmount())) != 0) {
						parm0 = "Paid/Waived amount should be equal to Total due " + allocate.getTotalDue()
								+ " for allocation type " + alcType;
						finScheduleData = setErrorToFSD(finScheduleData, "30550", parm0);
						return receiptData;
					}
				} else if (StringUtils.equals(allocate.getAllocationType(), RepayConstants.ALLOCATION_PRI)) {
					alcType = "P";
					if (StringUtils.equals(alcType, ulAlcType) && allocate.getTotalDue()
							.compareTo(ulAlc.getPaidAmount().add(ulAlc.getWaivedAmount())) != 0) {
						parm0 = "Paid amount should be equal to Total due " + allocate.getTotalDue()
								+ " for allocation type " + alcType;
						finScheduleData = setErrorToFSD(finScheduleData, "30550", parm0);
						return receiptData;
					}
				} else if (StringUtils.equals(allocate.getAllocationType(), RepayConstants.ALLOCATION_LPFT)) {
					alcType = "L";
					if (StringUtils.equals(alcType, ulAlcType) && allocate.getTotalDue()
							.compareTo(ulAlc.getPaidAmount().add(ulAlc.getWaivedAmount())) != 0) {
						parm0 = "Paid/Waived amount should be equal to Total due " + allocate.getTotalDue()
								+ " for allocation type " + alcType;
						finScheduleData = setErrorToFSD(finScheduleData, "30550", parm0);
						return receiptData;
					}
				} else if (StringUtils.equals(allocate.getAllocationType(), RepayConstants.ALLOCATION_FEE)) {
					alcType = "F";
					if (StringUtils.equals(alcType, ulAlcType) && allocate.getTotalDue()
							.compareTo(ulAlc.getPaidAmount().add(ulAlc.getWaivedAmount())) != 0) {
						parm0 = "Paid/Waived amount should be equal to Total due " + allocate.getTotalDue()
								+ " for allocation type " + alcType;
						finScheduleData = setErrorToFSD(finScheduleData, "30550", parm0);
						return receiptData;
					}
				} else if (StringUtils.equals(allocate.getAllocationType(), RepayConstants.ALLOCATION_ODC)) {
					alcType = "O";
					if (StringUtils.equals(alcType, ulAlcType) && allocate.getTotalDue()
							.compareTo(ulAlc.getPaidAmount().add(ulAlc.getWaivedAmount())) != 0) {
						parm0 = "Paid/Waived amount should be equal to Total due " + allocate.getTotalDue()
								+ " for allocation type " + alcType;
						finScheduleData = setErrorToFSD(finScheduleData, "30550", parm0);
						return receiptData;
					}
				} else if (StringUtils.equals(allocate.getAllocationType(), RepayConstants.ALLOCATION_FUT_NPFT)) {
					alcType = "FI";
					if (StringUtils.equals(alcType, ulAlcType) && allocate.getTotalDue()
							.compareTo(ulAlc.getPaidAmount().add(ulAlc.getWaivedAmount())) != 0) {
						parm0 = "Paid amount should be equal to Total due " + allocate.getTotalDue()
								+ " for allocation type " + alcType;
						setErrorToFSD(finScheduleData, "30550", parm0);
						return receiptData;
					}
				} else if (StringUtils.equals(allocate.getAllocationType(), RepayConstants.ALLOCATION_FUT_PFT)) {
					alcType = "FI";
					if (StringUtils.equals(alcType, ulAlcType) && allocate.getTotalDue()
							.compareTo(ulAlc.getPaidAmount().add(ulAlc.getWaivedAmount())) != 0) {
						parm0 = "Paid/Waived amount shoule be equal to Total due " + allocate.getTotalDue()
								+ " for allocation type " + alcType;
						finScheduleData = setErrorToFSD(finScheduleData, "30550", parm0);
						return receiptData;
					}
				} else if (StringUtils.equals(allocate.getAllocationType(), RepayConstants.ALLOCATION_FUT_PRI)) {
					alcType = "FP";
					if (StringUtils.equals(alcType, ulAlcType) && allocate.getTotalDue()
							.compareTo(ulAlc.getPaidAmount().add(ulAlc.getWaivedAmount())) != 0) {
						parm0 = "Paid amount should be equal to Total due " + allocate.getTotalDue()
								+ " for allocation type " + alcType;
						setErrorToFSD(finScheduleData, "30550", parm0);
						return receiptData;
					}

					if (StringUtils.equals(alcType, ulAlcType)
							&& allocate.getTotalDue().compareTo(ulAlc.getWaivedAmount()) < 0) {
						parm0 = "Waived amount should not be equal to Total due " + allocate.getTotalDue()
								+ " for allocation type " + alcType;
						setErrorToFSD(finScheduleData, "30550", parm0);
						return receiptData;
					}
				} else if (StringUtils.equals(allocate.getAllocationType(), RepayConstants.ALLOCATION_EMI)) {
					alcType = "EM";
					if (StringUtils.equals(alcType, ulAlcType)
							&& allocate.getTotalDue().compareTo(ulAlc.getPaidAmount()) != 0) {
						parm0 = "Paid amount should be equal to Total due " + allocate.getTotalDue()
								+ " for allocation type " + alcType;
						setErrorToFSD(finScheduleData, "30550", parm0);
						return receiptData;
					}
				} else if (StringUtils.equals(allocate.getAllocationType(), RepayConstants.ALLOCATION_TDS)) {
					continue;
				} else if (StringUtils.equals(allocate.getAllocationType(), RepayConstants.ALLOCATION_PFT)) {
					alcType = "PFT";
					if (StringUtils.equals(alcType, ulAlcType) && allocate.getTotalDue()
							.compareTo(ulAlc.getPaidAmount().add(ulAlc.getWaivedAmount())) != 0) {
						parm0 = "Paid/Waived amount shoule be equal to Total due " + allocate.getTotalDue()
								+ " for allocation type " + alcType;
						finScheduleData = setErrorToFSD(finScheduleData, "30550", parm0);
						return receiptData;
					}
				} else if (StringUtils.equals(allocate.getAllocationType(), RepayConstants.ALLOCATION_MANADV)) {
					alcType = "M";
				} else {
					alcType = "B";
					if (StringUtils.equals(alcType, ulAlcType) && allocate.getTotalDue()
							.compareTo(ulAlc.getPaidAmount().add(ulAlc.getWaivedAmount())) != 0) {
						parm0 = "Paid/Waived amount should be equal to Total due " + allocate.getTotalDue()
								+ " for allocation type " + alcType;
						setErrorToFSD(finScheduleData, "30550", parm0);
						return receiptData;
					}
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return receiptData;
	}

	/**
	 * Checking for duplicate receipt..
	 * 
	 * @param fsi
	 * @return errors
	 */
	public List<ErrorDetail> dedupCheck(FinServiceInstruction fsi) {
		List<Long> receiptIdList = finReceiptHeaderDAO.isDedupReceiptExists(fsi);

		if (receiptIdList.isEmpty()) {
			return new ArrayList<>();
		}

		List<ErrorDetail> errors = new ArrayList<>();

		StringBuilder message = new StringBuilder();
		message.append("Receipt for the Fin Reference {0} , Value Date {1}, Receipt Amount {2}");
		message.append(" and Transaction Reference {3} already exists with Receipt Id {4} .");

		String[] valueParm = new String[5];

		valueParm[0] = fsi.getFinReference();
		valueParm[1] = String.valueOf(fsi.getValueDate());
		valueParm[2] = PennantApplicationUtil.amountFormate(fsi.getAmount(),
				CurrencyUtil.getFormat(ImplementationConstants.BASE_CCY));
		valueParm[3] = fsi.getTransactionRef();
		valueParm[4] = String.valueOf(receiptIdList.get(0));

		errors.add(new ErrorDetail("21005", message.toString(), valueParm));

		return errors;
	}

	/**
	 * Method for Fetching Tax Receivable for Accounting Purpose
	 */
	@Override
	public FinTaxReceivable getTaxReceivable(String finReference, String taxFor) {
		return finODAmzTaxDetailDAO.getFinTaxReceivable(finReference, taxFor);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AccountingSetDAO getAccountingSetDAO() {
		return accountingSetDAO;
	}

	public void setAccountingSetDAO(AccountingSetDAO accountingSetDAO) {
		this.accountingSetDAO = accountingSetDAO;
	}

	public LimitCheckDetails getLimitCheckDetails() {
		return limitCheckDetails;
	}

	public void setLimitCheckDetails(LimitCheckDetails limitCheckDetails) {
		this.limitCheckDetails = limitCheckDetails;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setLimitManagement(LimitManagement limitManagement) {
		this.limitManagement = limitManagement;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	@Override
	public FinFeeDetailDAO getFinFeeDetailDAO() {
		return finFeeDetailDAO;
	}

	@Override
	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	public void setAllocationDetailDAO(ReceiptAllocationDetailDAO allocationDetailDAO) {
		this.allocationDetailDAO = allocationDetailDAO;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public void setRepayProcessUtil(RepaymentProcessUtil repayProcessUtil) {
		this.repayProcessUtil = repayProcessUtil;
	}

	public void setOverdraftScheduleDetailDAO(OverdraftScheduleDetailDAO overdraftScheduleDetailDAO) {
		this.overdraftScheduleDetailDAO = overdraftScheduleDetailDAO;
	}

	public void setLatePayMarkingService(LatePayMarkingService latePayMarkingService) {
		this.latePayMarkingService = latePayMarkingService;
	}

	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}

	public void setDepositDetailsDAO(DepositDetailsDAO depositDetailsDAO) {
		this.depositDetailsDAO = depositDetailsDAO;
	}

	public void setDepositChequesDAO(DepositChequesDAO depositChequesDAO) {
		this.depositChequesDAO = depositChequesDAO;
	}

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	public void setPromotionDAO(PromotionDAO promotionDAO) {
		this.promotionDAO = promotionDAO;
	}

	public void setInstrumentwiseLimitDAO(InstrumentwiseLimitDAO instrumentwiseLimitDAO) {
		this.instrumentwiseLimitDAO = instrumentwiseLimitDAO;
	}

	public void setReceiptUploadDetailDAO(ReceiptUploadDetailDAO receiptUploadDetailDAO) {
		this.receiptUploadDetailDAO = receiptUploadDetailDAO;
	}

	public void setSubventionDetailDAO(SubventionDetailDAO subventionDetailDAO) {
		this.subventionDetailDAO = subventionDetailDAO;
	}

	@Autowired
	public void setFinTypePartnerBankDAO(FinTypePartnerBankDAO finTypePartnerBankDAO) {
		this.finTypePartnerBankDAO = finTypePartnerBankDAO;
	}

	@Autowired
	public void setPartnerBankDAO(PartnerBankDAO partnerBankDAO) {
		this.partnerBankDAO = partnerBankDAO;
	}

	@Autowired
	public void setFinanceWorkFlowDAO(FinanceWorkFlowDAO financeWorkFlowDAO) {
		this.financeWorkFlowDAO = financeWorkFlowDAO;
	}

	@Autowired
	public void setReceiptResponseDetailDAO(ReceiptResponseDetailDAO receiptResponseDetailDAO) {
		this.receiptResponseDetailDAO = receiptResponseDetailDAO;
	}

	public void setReceiptCancellationService(ReceiptCancellationService receiptCancellationService) {
		this.receiptCancellationService = receiptCancellationService;
	}

	public void setReceiptRealizationService(ReceiptRealizationService receiptRealizationService) {
		this.receiptRealizationService = receiptRealizationService;
	}

	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}

	public FinReceiptData createXcessRCD(FinReceiptData receiptData) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		List<XcessPayables> xcessPayables = rch.getXcessPayables();
		List<FinReceiptDetail> rcdList = rch.getReceiptDetails();
		receiptData.getReceiptHeader().setReceiptDetails(rcdList);
		int receiptPurposeCtg = getReceiptCalculator().setReceiptCategory(rch.getReceiptPurpose());

		Map<String, BigDecimal> taxPercMap = null;

		// Create a new Receipt Detail for every type of excess/payable
		for (int i = 0; i < xcessPayables.size(); i++) {
			XcessPayables payable = xcessPayables.get(i);

			if (payable.getTotPaidNow().compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			FinReceiptDetail rcd = new FinReceiptDetail();
			rcd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			rcd.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
			rcd.setValueDate(rch.getValueDate());

			if (StringUtils.equals(payable.getPayableType(), RepayConstants.EXAMOUNTTYPE_EMIINADV)) {
				rcd.setPaymentType(RepayConstants.RECEIPTMODE_EMIINADV);
			} else if (StringUtils.equals(payable.getPayableType(), RepayConstants.EXAMOUNTTYPE_EXCESS)) {
				rcd.setPaymentType(RepayConstants.RECEIPTMODE_EXCESS);
			} else if (StringUtils.equals(payable.getPayableType(), RepayConstants.EXAMOUNTTYPE_CASHCLT)) {
				rcd.setPaymentType(RepayConstants.EXAMOUNTTYPE_CASHCLT);
			} else if (StringUtils.equals(payable.getPayableType(), RepayConstants.EXAMOUNTTYPE_DSF)) {
				rcd.setPaymentType(RepayConstants.EXAMOUNTTYPE_DSF);
			} else {
				rcd.setPaymentType(RepayConstants.RECEIPTMODE_PAYABLE);
			}

			rcd.setPayAgainstID(payable.getPayableID());
			if (receiptData.getTotalPastDues().compareTo(payable.getTotPaidNow()) >= 0) {
				rcd.setDueAmount(payable.getTotPaidNow());
				receiptData.setTotalPastDues(receiptData.getTotalPastDues().subtract(payable.getPaidNow()));
			} else {
				rcd.setDueAmount(receiptData.getTotalPastDues());
				receiptData.setTotalPastDues(BigDecimal.ZERO);
			}
			if (receiptPurposeCtg == 1) {
				rcd.setAmount(receiptData.getReceiptHeader().getReceiptAmount());
			} else {
				rcd.setAmount(rcd.getDueAmount());
			}
			rcd.setNoReserve(true);
			rcd.setValueDate(rch.getValueDate());
			// rcd.setReceivedDate(this.receivedDate.getValue());
			rcd.setPayOrder(rcdList.size() + 1);
			// rcd.setReceiptSeqID(getReceiptSeqID(rcd));

			if (payable.getPaidGST().compareTo(BigDecimal.ZERO) > 0) {
				ManualAdviseMovements payAdvMovement = new ManualAdviseMovements();

				payAdvMovement.setAdviseID(payable.getPayableID());
				payAdvMovement.setMovementDate(rcd.getReceivedDate());
				payAdvMovement.setMovementAmount(payable.getTotPaidNow());
				payAdvMovement.setTaxComponent(payable.getTaxType());
				payAdvMovement.setPaidAmount(payable.getPaidNow());
				payAdvMovement.setFeeTypeCode(payable.getFeeTypeCode());

				// GST Calculations
				if (StringUtils.isNotBlank(payable.getTaxType())) {

					if (taxPercMap == null) {
						taxPercMap = GSTCalculator.getTaxPercentages(rch.getReference());
					}

					TaxHeader taxHeader = new TaxHeader();
					taxHeader.setNewRecord(true);
					taxHeader.setRecordType(PennantConstants.RCD_ADD);
					taxHeader.setVersion(taxHeader.getVersion() + 1);
					taxHeader.getTaxDetails().add(getTaxDetail(RuleConstants.CODE_CGST,
							taxPercMap.get(RuleConstants.CODE_CGST), payable.getPaidCGST()));
					taxHeader.getTaxDetails().add(getTaxDetail(RuleConstants.CODE_SGST,
							taxPercMap.get(RuleConstants.CODE_SGST), payable.getPaidSGST()));
					taxHeader.getTaxDetails().add(getTaxDetail(RuleConstants.CODE_IGST,
							taxPercMap.get(RuleConstants.CODE_IGST), payable.getPaidIGST()));
					taxHeader.getTaxDetails().add(getTaxDetail(RuleConstants.CODE_UGST,
							taxPercMap.get(RuleConstants.CODE_UGST), payable.getPaidUGST()));
					taxHeader.getTaxDetails().add(getTaxDetail(RuleConstants.CODE_CESS,
							taxPercMap.get(RuleConstants.CODE_CESS), payable.getPaidCESS()));
					payAdvMovement.setTaxHeader(taxHeader);
				} else {
					payAdvMovement.setTaxHeader(null);
				}
				rcd.setPayAdvMovement(payAdvMovement);
			}

			if (rcd.getReceiptSeqID() <= 0) {
				rcdList.add(rcd);
			}

			if (receiptData.getTotalPastDues().compareTo(BigDecimal.ZERO) == 0) {
				break;
			}
		}

		rch.setReceiptDetails(rcdList);
		return receiptData;
	}

	private Taxes getTaxDetail(String taxType, BigDecimal taxPerc, BigDecimal taxAmount) {
		Taxes taxes = new Taxes();
		taxes.setTaxType(taxType);
		taxes.setTaxPerc(taxPerc);
		taxes.setNetTax(taxAmount);
		taxes.setActualTax(taxAmount);
		return taxes;
	}

	@Override
	public boolean canProcessReceipt(long receiptId) {
		boolean canProcessReceipt = true;
		FinReceiptHeader rch = finReceiptHeaderDAO.getReceiptHeaderByID(receiptId, "");
		if (rch != null) {
			boolean isLanActive = getFinanceMainDAO().isFinActive(rch.getReference());
			if (RepayConstants.PAYSTATUS_REALIZED.equals(rch.getReceiptModeStatus()) && !isLanActive
					&& rch.isActFinReceipt()) {
				canProcessReceipt = false;
			}
		}

		return canProcessReceipt;
	}

	public FinODAmzTaxDetailDAO getFinODAmzTaxDetailDAO() {
		return finODAmzTaxDetailDAO;
	}

	public void setFinODAmzTaxDetailDAO(FinODAmzTaxDetailDAO finODAmzTaxDetailDAO) {
		this.finODAmzTaxDetailDAO = finODAmzTaxDetailDAO;
	}

	@Override
	public void saveMultiReceipt(List<AuditHeader> auditHeaderList) throws Exception {
		logger.debug("Entering");
		for (AuditHeader auditHeader : auditHeaderList) {
			if (PennantConstants.method_doApprove.equals(auditHeader.getAuditDetail().getLovDescRecordStatus())) {
				doApprove(auditHeader);
			} else {
				saveOrUpdate(auditHeader);
			}
			FinReceiptData finReceiptData = (FinReceiptData) auditHeader.getModelData();
			FinReceiptDetail finReceiptDetail = new FinReceiptDetail();
			for (FinReceiptDetail receiptDetail : finReceiptData.getReceiptHeader().getReceiptDetails()) {
				if (!(RepayConstants.RECEIPTMODE_EMIINADV.equals(receiptDetail.getPaymentType())
						|| RepayConstants.RECEIPTMODE_EXCESS.equals(receiptDetail.getPaymentType())
						|| RepayConstants.RECEIPTMODE_PAYABLE.equals(receiptDetail.getPaymentType()))) {
					finReceiptDetail = receiptDetail;
					finReceiptDetail.setStatus(finReceiptData.getReceiptHeader().getReceiptModeStatus());
				}
			}

			// finReceiptHeaderDAO.saveMultiReceipt(finReceiptData.getReceiptHeader(),
			// finReceiptDetail);
		}
		logger.debug("Leaving");
	}

	public ErrorDetail receiptCancelValidation(String finReference, Date lastReceivedDate) {
		if (lastReceivedDate == null) {
			return null;
		}

		Date appDate = SysParamUtil.getAppDate();

		Date monthStart = DateUtil.getMonthStart(appDate);

		if (lastReceivedDate.compareTo(monthStart) < 0) {
			String[] valueParm = new String[4];
			String msg = "Post Receipt Creation, EOM is completed. Hence System will not allow to cancel the receipt.";
			return new ErrorDetail("21005", msg, valueParm);
		}

		Date maxPostDate = finLogEntryDetailDAO.getMaxPostDateByRef(finReference);

		if (DateUtil.compare(lastReceivedDate, maxPostDate) <= 0) {
			String[] valueParm = new String[4];
			String msg = "Post Receipt Creation, Schedule is effected due to other Schedule Change event. Hence System will not allow to cancel the receipt.";
			return new ErrorDetail("21005", msg, valueParm);
		}

		return null;
	}

	private AuditHeader approveValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		FinReceiptData receiptData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		Date valueDate = receiptData.getReceiptHeader().getValueDate();

		Date lastReceivedDate = getMaxReceiptDate(receiptData.getFinReference());
		if (lastReceivedDate != null
				&& DateUtility.compare(receiptData.getReceiptHeader().getValueDate(), lastReceivedDate) < 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToLongDate(receiptData.getReceiptHeader().getValueDate());
			valueParm[1] = DateUtility.formatToLongDate(lastReceivedDate);
			auditHeader.getAuditDetail().setErrorDetail(ErrorUtil
					.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "RU0011", valueParm, valueParm)));
			auditHeader.setErrorList(auditHeader.getAuditDetail().getErrorDetails());
		}
		Date prvSchdDate = null;
		FinanceScheduleDetail prvSchd = null;
		prvSchd = getFinanceScheduleDetailDAO().getPrvSchd(receiptData.getFinReference(), DateUtility.getAppDate());

		if (prvSchd != null) {
			prvSchdDate = prvSchd.getSchDate();
		}

		if (prvSchdDate != null && receiptData.getReceiptHeader().getValueDate() != null
				&& DateUtility.compare(receiptData.getReceiptHeader().getValueDate(), prvSchdDate) < 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToLongDate(receiptData.getReceiptHeader().getValueDate());
			valueParm[1] = DateUtility.formatToLongDate(prvSchdDate);
			auditHeader.getAuditDetail().setErrorDetail(ErrorUtil
					.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "RU0012", valueParm, valueParm)));
			auditHeader.setErrorList(auditHeader.getAuditDetail().getErrorDetails());
		}

		if (valueDate != null && valueDate.compareTo(DateUtility.getMonthStart(DateUtility.getAppDate())) < 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToLongDate(valueDate);
			valueParm[1] = DateUtility.formatToLongDate(DateUtility.getMonthStart(DateUtility.getAppDate()));
			auditHeader.getAuditDetail().setErrorDetail(ErrorUtil
					.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "RU0010", valueParm, valueParm)));
			auditHeader.setErrorList(auditHeader.getAuditDetail().getErrorDetails());
		}

		if (StringUtils.equals(receiptData.getReceiptHeader().getReceiptPurpose(),
				FinanceConstants.FINSER_EVENT_EARLYRPY)) {
			if (receiptData.getReceiptHeader().getPartPayAmount().compareTo(BigDecimal.ZERO) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = receiptData.getReceiptHeader().getReceiptPurpose();
				valueParm[1] = receiptData.getReceiptHeader().getReceiptPurpose();
				auditHeader.getAuditDetail().setErrorDetail(ErrorUtil
						.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "90332", valueParm, valueParm)));
				auditHeader.setErrorList(auditHeader.getAuditDetail().getErrorDetails());
			}
			BigDecimal closingBal = getClosingBalance(receiptData.getReceiptHeader().getReference(),
					receiptData.getReceiptHeader().getValueDate());
			BigDecimal diff = closingBal.subtract(receiptData.getReceiptHeader().getPartPayAmount());

			FinanceMain financeMain = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();

			if (diff.compareTo(new BigDecimal(100)) < 0) {

				String[] valueParm = new String[2];
				valueParm[0] = String.valueOf(PennantApplicationUtil.formateAmount(closingBal,
						CurrencyUtil.getFormat(financeMain.getFinCcy())));
				valueParm[1] = String.valueOf(PennantApplicationUtil.formateAmount(closingBal,
						CurrencyUtil.getFormat(financeMain.getFinCcy())));
				auditHeader.getAuditDetail().setErrorDetail(ErrorUtil
						.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "91127", valueParm, valueParm)));
				auditHeader.setErrorList(auditHeader.getAuditDetail().getErrorDetails());
			}
		}
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public void saveMultiReceipt(AuditHeader auditHeader) throws Exception {
		logger.debug("Entering");
		boolean flag = false;
		String error = "";
		FinReceiptQueueLog finReceiptQueue = new FinReceiptQueueLog();
		finReceiptQueue.setStartTime(LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + ":"
				+ LocalDateTime.now().getSecond()); // Thread Processing Start
																																						// Time

		Map<String, String> valueMap = new HashMap<>();
		FinReceiptData finReceiptData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		FinReceiptDetail finReceiptDetail = new FinReceiptDetail();

		for (FinReceiptDetail receiptDetail : finReceiptData.getReceiptHeader().getReceiptDetails()) {
			if (!(RepayConstants.RECEIPTMODE_EMIINADV.equals(receiptDetail.getPaymentType())
					|| RepayConstants.RECEIPTMODE_EXCESS.equals(receiptDetail.getPaymentType())
					|| RepayConstants.RECEIPTMODE_PAYABLE.equals(receiptDetail.getPaymentType()))) {
				finReceiptDetail = receiptDetail;
				finReceiptDetail.setStatus(finReceiptData.getReceiptHeader().getReceiptModeStatus());
			}
		}

		try {
			// saveOrUpdate Or Approve method
			if (PennantConstants.method_doApprove.equals(auditHeader.getAuditDetail().getLovDescRecordStatus())) {
				doApprove(auditHeader);
			} else {
				saveOrUpdate(auditHeader);
			}

		} catch (Exception e) {
			flag = true;
			error = e.getMessage();
			e.printStackTrace();
		}
		if (flag) {
			valueMap.put("uploadStatus", UploadConstants.UPLOAD_STATUS_FAIL);
			finReceiptQueue.setProgress(EodConstants.PROGRESS_FAILED);
		} else {
			valueMap.put("uploadStatus", UploadConstants.UPLOAD_STATUS_SUCCESS);
			finReceiptQueue.setProgress(EodConstants.PROGRESS_SUCCESS);
		}

		if (error != null && error.length() >= 1000) {
			error = error.substring(0, 999);
		}
		valueMap.put("reason", error);
		finReceiptHeaderDAO.saveMultiReceipt(finReceiptData.getReceiptHeader(), finReceiptDetail, valueMap); // Saving
																												// MultiReceiptApproval
																												// Table

		finReceiptQueue.setUploadId(finReceiptData.getReceiptHeader().getBatchId());
		finReceiptQueue.setReceiptId(finReceiptData.getReceiptHeader().getReceiptID());
		finReceiptQueue.setThreadId(Thread.currentThread().getId());
		finReceiptQueue.setEndTime(LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + ":"
				+ LocalDateTime.now().getSecond());
		finReceiptQueue.setErrorLog(error);
		finReceiptHeaderDAO.updateMultiReceiptLog(finReceiptQueue); // Updating
																	// Thread
																	// Progress
																	// Status

		logger.debug("Leaving");
	}

	@Override
	public long getUploadSeqId() {
		return receiptUploadHeaderDAO.generateSeqId();
	}

	@Override
	public void saveMultiReceiptLog(List<FinReceiptQueueLog> finReceiptQueueList) {
		logger.debug("Entering");
		finReceiptHeaderDAO.saveMultiReceiptLog(finReceiptQueueList);
		logger.debug("Leaving");

	}

	@Override
	public List<Long> getInProcessMultiReceiptRecord() {
		return finReceiptHeaderDAO.getInProcessMultiReceiptRecord();
	}

	@Override
	public void batchUpdateMultiReceiptLog(List<FinReceiptQueueLog> finReceiptQueueList) {
		logger.debug("Entering");
		finReceiptHeaderDAO.batchUpdateMultiReceiptLog(finReceiptQueueList);
		logger.debug("Leaving");
	}

	@Override
	public ErrorDetail getWaiverValidation(String finReference, String receiptPurpose, Date valueDate) {
		if (!(FinanceConstants.EARLYSETTLEMENT.equals(receiptPurpose)
				|| FinanceConstants.FINSER_EVENT_EARLYSETTLE.equals(receiptPurpose))) {
			return null;
		}

		Date lastWaiverDate = getLastWaiverDate(finReference, SysParamUtil.getAppDate(), valueDate);
		if (lastWaiverDate == null) {
			return null;
		}

		String[] valueParm = new String[2];
		valueParm[0] = DateUtil.formatToLongDate(valueDate);
		valueParm[1] = DateUtil.formatToLongDate(lastWaiverDate);

		return ErrorUtil.getErrorDetail(new ErrorDetail("RU0099", valueParm));
	}

	@Override
	public boolean isEarlySettlementInitiated(String finreference) {
		boolean isInitiated = finReceiptHeaderDAO.checkEarlySettlementInitiation(finreference);
		return isInitiated;
	}

	public void setReceiptUploadHeaderService(ReceiptUploadHeaderService receiptUploadHeaderService) {
		this.receiptUploadHeaderService = receiptUploadHeaderService;
	}

	@Override
	public boolean isPartialSettlementInitiated(String finreference) {
		boolean isInitiated = finReceiptHeaderDAO.checkPartialSettlementInitiation(finreference);
		return isInitiated;
	}

	@Override
	public String getLoanReferenc(String finreference, String fileName) {
		String isInitiated = finReceiptHeaderDAO.getLoanReferenc(finreference, fileName);
		return isInitiated;
	}

	@Override
	public int geFeeReceiptCountByExtReference(String reference, String receiptPurpose, String extReference) {
		return finReceiptHeaderDAO.geFeeReceiptCountByExtReference(reference, receiptPurpose, extReference);
	}

	public Date getLastWaiverDate(String finReference, Date appDate, Date receiptDate) {
		return feeWaiverHeaderDAO.getLastWaiverDate(finReference, appDate, receiptDate);
	}

	public TaxHeaderDetailsDAO getTaxHeaderDetailsDAO() {
		return taxHeaderDetailsDAO;
	}

	public void setTaxHeaderDetailsDAO(TaxHeaderDetailsDAO taxHeaderDetailsDAO) {
		this.taxHeaderDetailsDAO = taxHeaderDetailsDAO;
	}

	public void setReasonCodeDAO(ReasonCodeDAO reasonCodeDAO) {
		this.reasonCodeDAO = reasonCodeDAO;
	}

	public void setAdvancePaymentService(AdvancePaymentService advancePaymentService) {
		this.advancePaymentService = advancePaymentService;
	}

	public FinanceTaxDetailDAO getFinanceTaxDetailDAO() {
		return financeTaxDetailDAO;
	}

	public void setReceiptUploadHeaderDAO(ReceiptUploadHeaderDAO receiptUploadHeaderDAO) {
		this.receiptUploadHeaderDAO = receiptUploadHeaderDAO;
	}

	public void setFinanceTaxDetailDAO(FinanceTaxDetailDAO financeTaxDetailDAO) {
		this.financeTaxDetailDAO = financeTaxDetailDAO;
	}

	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	public void setFinFeeConfigService(FinFeeConfigService finFeeConfigService) {
		this.finFeeConfigService = finFeeConfigService;
	}

	public void setFeeReceiptService(FeeReceiptService feeReceiptService) {
		this.feeReceiptService = feeReceiptService;
	}

	public List<FinExcessAmount> xcessList(String finreference) {
		return finExcessAmountDAO.getExcessAmountsByRef(finreference);
	}

	public void setFeeWaiverHeaderDAO(FeeWaiverHeaderDAO feeWaiverHeaderDAO) {
		this.feeWaiverHeaderDAO = feeWaiverHeaderDAO;
	}

}
