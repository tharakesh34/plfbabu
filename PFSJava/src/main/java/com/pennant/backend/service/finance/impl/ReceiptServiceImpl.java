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
 *
 * FileName : ReceiptServiceImpl.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 30-07-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 *
 * 
 * 13-06-2018 Siva 0.2 Stage Accounting Modifications * * 19-06-2018 Siva 0.3 Payable Reserve Amount Not removing on
 * Maintenance * * *
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.pennant.app.constants.AccountConstants;
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
import com.pennant.backend.dao.rmtmasters.FinTypeFeesDAO;
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
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldExtension;
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
import com.pennant.backend.model.finance.FinanceDisbursement;
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
import com.pennant.backend.model.finance.SubventionScheduleDetail;
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
import com.pennant.backend.service.extendedfieldsExtension.ExtendedFieldExtensionService;
import com.pennant.backend.service.finance.FeeReceiptService;
import com.pennant.backend.service.finance.FinFeeConfigService;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.service.finance.ReceiptCancellationService;
import com.pennant.backend.service.finance.ReceiptRealizationService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.finance.ReceiptUploadHeaderService;
import com.pennant.backend.service.limitservice.impl.LimitManagement;
import com.pennant.backend.util.CashManagementConstants;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.eod.constants.EodConstants;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.framework.security.core.User;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.pff.service.hook.PostValidationHook;
import com.pennanttech.pff.advancepayment.service.AdvancePaymentService;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.rits.cloning.Cloner;

public class ReceiptServiceImpl extends GenericFinanceDetailService implements ReceiptService {
	private static final Logger logger = LogManager.getLogger(ReceiptServiceImpl.class);

	private LimitCheckDetails limitCheckDetails;
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
	@Autowired(required = false)
	@Qualifier("receiptDetailPostValidationHook")
	private PostValidationHook postValidationHook;
	private FinTypeFeesDAO finTypeFeesDAO;
	private ExtendedFieldExtensionService extendedFieldExtensionService;

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
		logger.debug(Literal.ENTERING);

		// Receipt Header Details
		FinReceiptHeader rch = finReceiptHeaderDAO.getReceiptHeaderByID(receiptID, type);

		if (rch == null) {
			logger.debug(Literal.LEAVING);
			return rch;
		}

		if ("_FEView".equalsIgnoreCase(type)) {
			type = "_View";
		}

		List<FinReceiptDetail> rcdList = null;
		rcdList = finReceiptDetailDAO.getReceiptHeaderByID(receiptID, type);
		int size = rcdList.size();
		if (size > 0) {
			rch.setValueDate(rcdList.get(size - 1).getValueDate());
		}
		if (rcdList != null && !rcdList.isEmpty()) {
			for (FinReceiptDetail rcd : rcdList) {
				if (RepayConstants.RECEIPTMODE_PAYABLE.equals(rcd.getPaymentType())) {
					ManualAdviseMovements advMov = manualAdviseDAO.getAdvMovByReceiptSeq(receiptID,
							rcd.getReceiptSeqID(), rcd.getPayAgainstID(),
							StringUtils.equals(type, "_View") ? "_Temp" : "");
					if (advMov != null) {
						rcd.setPayAdvMovement(advMov);
					}

				}
			}
		}

		rch.setReceiptDetails(rcdList);
		// Receipt Allocation Details
		if (!isFeePayment) {
			List<ReceiptAllocationDetail> allocations = allocationDetailDAO.getAllocationsByReceiptID(receiptID, type);

			if (CollectionUtils.isNotEmpty(allocations)) {
				for (ReceiptAllocationDetail allocation : allocations) {
					allocation.setTotalPaid(allocation.getPaidAmount().add(allocation.getTdsPaid()));
					Long headerId = allocation.getTaxHeaderId();
					if (headerId != null && headerId > 0) {
						List<Taxes> taxDetails = taxHeaderDetailsDAO.getTaxDetailById(headerId, type);
						TaxHeader taxHeader = new TaxHeader(headerId);
						taxHeader.setTaxDetails(taxDetails);
						allocation.setTaxHeader(taxHeader);
					}
				}
			}

			rch.setAllocations(allocations);
		} else {
			// Fetch Repay Headers List
			for (FinReceiptDetail rcd : rcdList) {
				rcd.setRepayHeader(financeRepaymentsDAO.getFinRepayHeadersByReceipt(rcd.getReceiptSeqID(), ""));
			}
			// Bounce reason Code
			if (RepayConstants.RECEIPTMODE_CHEQUE.equalsIgnoreCase(rch.getReceiptMode())) {
				rch.setManualAdvise(manualAdviseDAO.getManualAdviseByReceiptId(receiptID, "_View"));
			}
			if (StringUtils.isBlank(rch.getExtReference())) {
				rch.setPaidFeeList(feeReceiptService.getPaidFinFeeDetails(rch.getReference(), receiptID, "_View"));
			} else {
				rch.setPaidFeeList(feeReceiptService.getPaidFinFeeDetails(rch.getExtReference(), receiptID, "_View"));
				FinReceiptHeader frh = finReceiptHeaderDAO.getFinTypeByReceiptID(rch.getReceiptID());
				if (frh != null) {
					rch.setFinType(frh.getFinType());
					rch.setFinTypeDesc(frh.getFinTypeDesc());
					rch.setFinCcy(frh.getFinCcy());
					rch.setFinCcyDesc(frh.getFinCcyDesc());
				}

			}
		}

		logger.debug(Literal.LEAVING);
		return rch;
	}

	@Override
	public FinReceiptData getFinReceiptDataById(String finReference, String eventCode, String procEdtEvent,
			String userRole) {
		logger.debug(Literal.ENTERING);

		FinReceiptData receiptData = new FinReceiptData();

		FinanceDetail fd = new FinanceDetail();
		receiptData.setFinanceDetail(fd);

		FinScheduleData schdData = fd.getFinScheduleData();

		FinanceMain fm = financeMainDAO.getFinanceMainByRef(finReference, "_AView", false);

		if (fm == null) {
			logger.debug(Literal.LEAVING);
			return receiptData;
		}

		schdData.setFinID(fm.getFinID());
		schdData.setFinReference(finReference);

		schdData.setFinanceMain(fm);

		long finID = fm.getFinID();
		long custID = fm.getCustID();
		String finType = fm.getFinType();
		String promotionCode = fm.getPromotionCode();
		String productCategory = fm.getProductCategory();

		receiptData.setFinID(finID);
		receiptData.setFinReference(finReference);

		Entity entity = entityDAO.getEntity(fm.getLovDescEntityCode(), "");
		if (entity != null) {
			fm.setEntityDesc(entity.getEntityDesc());
		}

		FinanceType financeType = financeTypeDAO.getOrgFinanceTypeByID(finType, "_ORGView");

		if (StringUtils.isNotBlank(promotionCode) && (fm.getPromotionSeqId() != null && fm.getPromotionSeqId() == 0)) {
			Promotion promotion = this.promotionDAO.getPromotionByCode(promotionCode, "_AView");
			financeType.setFInTypeFromPromotiion(promotion);
		}

		schdData.setFinanceType(financeType);

		if (fm.isStepFinance()) {
			schdData.setStepPolicyDetails(financeStepDetailDAO.getFinStepDetailListByFinRef(finID, "_AView", false));
		}

		schdData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false));

		fd.setFinTypeFeesList(finTypeFeesDAO.getFinTypeFeesList(finType, eventCode, "_AView", false,
				FinanceConstants.MODULEID_FINTYPE));

		fd.setFinFeeConfigList(finFeeConfigService.getFinFeeConfigList(finID, eventCode, false, "_View"));

		schdData.setFeeEvent(eventCode);

		if (FinanceConstants.PRODUCT_ODFACILITY.equals(productCategory)) {
			schdData.setOverdraftScheduleDetails(
					overdraftScheduleDetailDAO.getOverdraftScheduleDetails(finID, "", false));
		}

		schdData.setDisbursementDetails(financeDisbursementDAO.getFinanceDisbursementDetails(finID, "_AView", false));
		schdData.setRepayInstructions(repayInstructionDAO.getRepayInstructions(finID, "_AView", false));
		schdData.setFinODPenaltyRate(finODPenaltyRateDAO.getFinODPenaltyRateByRef(finID, "_AView"));
		schdData.setFinPftDeatil(profitDetailsDAO.getFinProfitDetailsById(finID));

		if (custID != 0 && custID != Long.MIN_VALUE) {
			fd.setCustomerDetails(customerDetailsService.getCustomerDetailsById(custID, false, "_AView"));
		}

		checkListDetailService.setFinanceCheckListDetails(fd, finType, procEdtEvent, userRole);

		fd.setStageTransactionEntries(transactionEntryDAO.getListTransactionEntryByRefType(finType,
				StringUtils.isEmpty(procEdtEvent) ? FinServiceEvent.ORG : procEdtEvent,
				FinanceConstants.PROCEDT_STAGEACC, userRole, "_AEView", true));

		if (StringUtils.isNotBlank(fm.getRecordType())) {
			receiptData = getWFFinReceiptDataById(receiptData, procEdtEvent);
		} else {
			FinReceiptHeader receiptHeader = new FinReceiptHeader();
			receiptData.setReceiptHeader(receiptHeader);
			receiptHeader.setFinID(fm.getFinID());
			receiptHeader.setReference(fm.getFinReference());
		}

		FinReceiptHeader rch = receiptData.getReceiptHeader();
		rch.setExcessAmounts(finExcessAmountDAO.getExcessAmountsByRef(finID));

		rch.setPayableAdvises(
				manualAdviseDAO.getManualAdviseByRef(finID, FinanceConstants.MANUAL_ADVISE_PAYABLE, "_AView"));

		// Fee Details ( Fetch Fee Details for event fee only)
		String type = "_View";
		if (StringUtils.isBlank(fm.getRecordType())) {
			schdData.setFinFeeDetailList(finFeeDetailDAO.getFinScheduleFees(finID, false, "_View"));
		} else {
			type = "_TView";
			schdData.setFinFeeDetailList(finFeeDetailDAO.getFinFeeDetailByFinRef(finID, false, "_TView"));
		}

		if (CollectionUtils.isNotEmpty(schdData.getFinFeeDetailList())) {
			for (FinFeeDetail finFeeDetail : schdData.getFinFeeDetailList()) {
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

		List<DocumentDetails> documents = documentDetailsDAO.getDocumentDetailsByRef(finReference,
				FinanceConstants.MODULE_NAME, procEdtEvent, "_View");
		if (fd.getDocumentDetailsList() != null && !fd.getDocumentDetailsList().isEmpty()) {
			fd.getDocumentDetailsList().addAll(documents);
		} else {
			fd.setDocumentDetailsList(documents);
		}

		if (ImplementationConstants.COLLATERAL_INTERNAL) {
			fd.setCollateralAssignmentList(collateralAssignmentDAO.getCollateralAssignmentByFinRef(finReference,
					FinanceConstants.MODULE_NAME, "_View"));
		} else {
			fd.setFinanceCollaterals(finCollateralService.getFinCollateralsByRef(finID, "_View"));
		}

		fd.setJointAccountDetailList(jointAccountDetailService.getJoinAccountDetail(finID, "_AView"));

		fd.setGurantorsDetailList(guarantorDetailService.getGuarantorDetail(finID, "_AView"));

		// Multi Receipts: Get In Process Receipts
		receiptData = getInProcessReceiptData(receiptData);

		logger.debug(Literal.ENTERING);
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

	public FinReceiptData getWFFinReceiptDataById(FinReceiptData rd, String procEdtEvent) {
		String finReference = rd.getFinReference();
		FinanceDetail fd = rd.getFinanceDetail();
		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

		// Finance Document Details
		String tableType = TableType.TEMP_TAB.getSuffix();
		fd.setDocumentDetailsList(documentDetailsDAO.getDocumentDetailsByRef(finReference, FinanceConstants.MODULE_NAME,
				procEdtEvent, tableType));

		List<FinReceiptHeader> rchList = finReceiptHeaderDAO.getReceiptHeaderByRef(finReference, "R", tableType);

		FinReceiptHeader rch = new FinReceiptHeader();
		if (CollectionUtils.isNotEmpty(rchList)) {
			rch = rchList.get(0);
		}

		rd.setReceiptHeader(rch);
		if (rch == null) {
			return rd;
		}

		// Cash Management
		fm.setDepositProcess(rch.isDepositProcess());

		long receiptID = rch.getReceiptID();
		long finID = fm.getFinID();

		List<FinReceiptDetail> rcdList = finReceiptDetailDAO.getReceiptHeaderByID(receiptID, "_TView");

		for (FinReceiptDetail rcd : rcdList) {
			ManualAdviseMovements mam = null;
			if (RepayConstants.RECEIPTMODE_PAYABLE.equals(rcd.getPaymentType())) {
				long receiptSeqID = rcd.getReceiptSeqID();

				mam = manualAdviseDAO.getAdvMovByReceiptSeq(receiptSeqID, receiptSeqID, tableType);

				Long taxHeaderId = mam.getTaxHeaderId();

				if (taxHeaderId != null && taxHeaderId > 0) {
					TaxHeader header = taxHeaderDetailsDAO.getTaxHeaderDetailsById(taxHeaderId, tableType);
					if (header != null) {
						header.setTaxDetails(taxHeaderDetailsDAO.getTaxDetailById(taxHeaderId, tableType));
					}
					mam.setTaxHeader(header);
				}
				rcd.setPayAdvMovement(mam);
			}
		}

		// Fetch Repay Headers List
		List<FinRepayHeader> rphList = financeRepaymentsDAO.getFinRepayHeadersByRef(finID, tableType);

		// Fetch List of Repay Schedules
		List<RepayScheduleDetail> rsdList = financeRepaymentsDAO.getRpySchdList(finID, tableType);
		for (FinRepayHeader rph : rphList) {
			for (RepayScheduleDetail rsd : rsdList) {
				if (rph.getRepayID() == rsd.getRepayID()) {
					rph.getRepayScheduleDetails().add(rsd);
				}
			}
		}

		// Repay Headers setting to Receipt Details
		List<FinExcessAmountReserve> excessReserves = new ArrayList<>();
		List<ManualAdviseReserve> payableReserves = new ArrayList<>();

		if (rcdList == null) {
			rcdList = new ArrayList<>();
		}

		for (FinReceiptDetail rcd : rcdList) {
			for (FinRepayHeader rph : rphList) {
				if (rph.getReceiptSeqID() == rcd.getReceiptSeqID()) {
					rcd.setRepayHeader(rph);
				}
			}

			// Manual Advise Movements
			int advisetype = Integer.valueOf(FinanceConstants.MANUAL_ADVISE_RECEIVABLE);
			rcd.setAdvMovements(manualAdviseDAO.getAdvMovementsByReceiptSeq(rcd.getReceiptID(), rcd.getReceiptSeqID(),
					advisetype, "_TView"));

			for (ManualAdviseMovements mam : rcd.getAdvMovements()) {
				if (mam.getTaxHeaderId() > 0) {
					TaxHeader header = taxHeaderDetailsDAO.getTaxHeaderDetailsById(mam.getTaxHeaderId(), tableType);
					if (header != null) {
						header.setTaxDetails(taxHeaderDetailsDAO.getTaxDetailById(mam.getTaxHeaderId(), tableType));
					}
					mam.setTaxHeader(header);
				}
			}

			// Excess Reserve Amounts
			excessReserves.addAll(finExcessAmountDAO.getExcessReserveList(rcd.getReceiptSeqID()));

			// Payable Reserve Amounts
			payableReserves.addAll(manualAdviseDAO.getPayableReserveList(rcd.getReceiptSeqID()));
		}

		rch.setExcessReserves(excessReserves);
		rch.setPayableReserves(payableReserves);
		rch.setReceiptDetails(rcdList);

		// Receipt Allocation Details
		List<ReceiptAllocationDetail> radList = allocationDetailDAO.getAllocationsByReceiptID(receiptID, tableType);
		for (ReceiptAllocationDetail rad : radList) {
			Long taxHeaderId = rad.getTaxHeaderId();
			if (taxHeaderId != null && taxHeaderId != 0) {
				List<Taxes> taxDetailById = taxHeaderDetailsDAO.getTaxDetailById(taxHeaderId, tableType);
				TaxHeader taxHeader = new TaxHeader(taxHeaderId);
				taxHeader.setTaxDetails(taxDetailById);
			}
		}
		rch.setAllocations(radList);

		// 127186 --Changing table type from Temp to Tview to show
		// bounce code also along with ID
		if (StringUtils.equals(RepayConstants.PAYSTATUS_BOUNCE, rch.getReceiptModeStatus())) {
			rch.setManualAdvise(manualAdviseDAO.getManualAdviseByReceiptId(receiptID, "_TView"));
		}

		// Multi Receipts: Get In Process Receipts
		rd = getInProcessReceiptData(rd);

		return rd;
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader) {
		logger.debug(Literal.ENTERING);

		FinReceiptData rcData = (FinReceiptData) aAuditHeader.getAuditDetail().getModelData();

		long serviceUID = Long.MIN_VALUE;
		if (rcData.getFinanceDetail().getExtendedFieldRender() != null) {
			serviceUID = extendedFieldDetailsService.getInstructionUID(
					rcData.getFinanceDetail().getExtendedFieldRender(),
					rcData.getFinanceDetail().getExtendedFieldExtension());
		}

		aAuditHeader = businessValidation(aAuditHeader, "saveOrUpdate");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!aAuditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return aAuditHeader;
		}

		boolean changeStatus = false;

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		FinReceiptData rceiptData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();

		FinReceiptHeader rch = rceiptData.getReceiptHeader();

		FinanceDetail fd = rceiptData.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();

		fm.setRcdMaintainSts(FinServiceEvent.RECEIPT);

		auditHeader = executeStageAccounting(auditHeader);
		fm.setRcdMaintainSts("");

		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}

		String recordStatus = rch.getRecordStatus();
		if (!PennantConstants.RCD_STATUS_RESUBMITTED.equalsIgnoreCase(recordStatus)
				&& !PennantConstants.RCD_STATUS_SAVED.equalsIgnoreCase(recordStatus)) {
			changeStatus = true;
		}

		String roleCode = rch.getRoleCode();
		if (FinanceConstants.DEPOSIT_MAKER.equals(roleCode) && changeStatus) {
			rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_INITIATED);
		} else if (FinanceConstants.DEPOSIT_APPROVER.equals(roleCode) && changeStatus) {
			rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_DEPOSITED);
		} else if (FinanceConstants.REALIZATION_MAKER.equals(roleCode)
				|| FinanceConstants.KNOCKOFFCAN_MAKER.equals(roleCode)) {

		} else if (FinanceConstants.REALIZATION_APPROVER.equals(roleCode)
				|| FinanceConstants.KNOCKOFFCAN_APPROVER.equals(roleCode)) {

		} else {
			rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_INITIATED);

		}

		TableType tableType = TableType.MAIN_TAB;
		if (rch.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		// financeMain.setRcdMaintainSts(FinanceConstants.FINSER_EVENT_RECEIPT);
		if (tableType == TableType.MAIN_TAB) {
			rch.setRcdMaintainSts(null);
		}

		// Finance Main Details Save And Update
		// =======================================
		long receiptID = rch.getReceiptID();
		rch.setRcdMaintainSts("R");
		rch.setActFinReceipt(schdData.getFinanceMain().isFinIsActive());
		if (RepayConstants.PAYSTATUS_CANCEL.equals(rch.getReceiptModeStatus())) {
			rch.setBounceDate(SysParamUtil.getAppDate());
		}
		if (rch.isNewRecord()) {
			receiptID = finReceiptHeaderDAO.save(rch, tableType);
		} else {
			if (tableType == TableType.TEMP_TAB) {

				// Update Receipt Header
				finReceiptHeaderDAO.update(rch, tableType);

				// Delete Save Receipt Detail List by Reference
				finReceiptDetailDAO.deleteByReceiptID(receiptID, tableType);

				// Delete and Save FinRepayHeader Detail list by Reference
				financeRepaymentsDAO.deleteByRef(finID, tableType);

				// Delete and Save Repayment Schedule details by setting Repay
				// Header ID
				financeRepaymentsDAO.deleteRpySchdList(finID, tableType.getSuffix());

				// Delete Tax Header
				deleteTaxHeaderId(receiptID, tableType.getSuffix());

				// Receipt Allocation Details
				allocationDetailDAO.deleteByReceiptID(receiptID, tableType);
			}

			// Bounce reason Code
		}

		ManualAdvise advise = manualAdviseDAO.getManualAdviseByReceiptId(receiptID, "_Temp");
		if (rch.getManualAdvise() != null) {
			if (advise == null) {
				manualAdviseDAO.save(rch.getManualAdvise(), tableType);
			} else {
				manualAdviseDAO.update(rch.getManualAdvise(), tableType);
			}
		} else {
			if (advise != null) {
				manualAdviseDAO.delete(rch.getManualAdvise(), tableType);
			}
		}

		// Save Deposit Details
		saveDepositDetails(rch, null);
		// Update Deposit Branch

		if (ImplementationConstants.DEPOSIT_PROC_REQ) {
			finReceiptHeaderDAO.updateDepositBranchByReceiptID(receiptID, rch.getUserDetails().getBranchCode(),
					tableType.getSuffix());
		}

		// Save Receipt Detail List by setting Receipt Header ID
		List<FinReceiptDetail> rcdList = rch.getReceiptDetails();

		// Manual Advise Movements
		manualAdviseDAO.deleteMovementsByReceiptID(receiptID, TableType.TEMP_TAB.getSuffix());

		for (FinReceiptDetail rcd : rcdList) {
			rcd.setReceiptID(receiptID);
			long receiptSeqID = rcd.getReceiptSeqID();

			ManualAdviseMovements payAdvMovement = rcd.getPayAdvMovement();

			if (!rcd.isDelRecord()) {
				receiptSeqID = finReceiptDetailDAO.save(rcd, tableType);

				if (payAdvMovement != null) {
					TaxHeader taxHeader = payAdvMovement.getTaxHeader();
					if (taxHeader != null && CollectionUtils.isNotEmpty(taxHeader.getTaxDetails())) {
						List<Taxes> taxDetails = taxHeader.getTaxDetails();
						Long headerId = taxHeaderDetailsDAO.save(taxHeader, TableType.TEMP_TAB.getSuffix());
						for (Taxes taxes : taxDetails) {
							taxes.setReferenceId(headerId);
						}
						taxHeaderDetailsDAO.saveTaxes(taxDetails, TableType.TEMP_TAB.getSuffix());
						payAdvMovement.setTaxHeaderId(headerId);
					}

					payAdvMovement.setReceiptID(receiptID);
					payAdvMovement.setReceiptSeqID(receiptSeqID);
					manualAdviseDAO.saveMovement(payAdvMovement, TableType.TEMP_TAB.getSuffix());
				}
			}

			// Excess Amount Reserve
			String paymentType = rcd.getPaymentType();
			if (RepayConstants.RECEIPTMODE_EXCESS.equals(paymentType)
					|| RepayConstants.RECEIPTMODE_EMIINADV.equals(paymentType)
					|| RepayConstants.RECEIPTMODE_ADVINT.equals(paymentType)
					|| RepayConstants.RECEIPTMODE_ADVEMI.equals(paymentType)
					|| RepayConstants.RECEIPTMODE_CASHCLT.equals(paymentType)
					|| RepayConstants.RECEIPTMODE_DSF.equals(paymentType)) {

				// Excess Amount make utilization
				FinExcessAmountReserve exReserve = finExcessAmountDAO.getExcessReserve(receiptSeqID,
						rcd.getPayAgainstID());
				if (exReserve == null
						&& !StringUtils.equals(rch.getReceiptModeStatus(), RepayConstants.PAYSTATUS_CANCEL)) {

					// Update Excess Amount in Reserve
					finExcessAmountDAO.updateExcessReserve(rcd.getPayAgainstID(), rcd.getAmount());

					// Save Excess Reserve Log Amount
					finExcessAmountDAO.saveExcessReserveLog(receiptSeqID, rcd.getPayAgainstID(), rcd.getAmount(),
							RepayConstants.RECEIPTTYPE_RECIPT);

				} else {
					// If Receipt details re-modified in process
					if (rcd.isDelRecord()) {

						// Delete Reserve Amount in FinExcessAmount
						finExcessAmountDAO.deleteExcessReserve(receiptSeqID, rcd.getPayAgainstID(),
								RepayConstants.RECEIPTTYPE_RECIPT);

						// Update Reserve Amount in FinExcessAmount
						finExcessAmountDAO.updateExcessReserve(rcd.getPayAgainstID(),
								exReserve.getReservedAmt().negate());

					} else {
						if (!StringUtils.equals(rch.getReceiptModeStatus(), RepayConstants.PAYSTATUS_CANCEL)) {
							if (rcd.getAmount().compareTo(exReserve.getReservedAmt()) != 0) {
								BigDecimal diffInReserve = rcd.getAmount().subtract(exReserve.getReservedAmt());

								// Update Reserve Amount in FinExcessAmount
								finExcessAmountDAO.updateExcessReserve(rcd.getPayAgainstID(), diffInReserve);

								// Update Excess Reserve Log
								finExcessAmountDAO.updateExcessReserveLog(receiptSeqID, rcd.getPayAgainstID(),
										diffInReserve, RepayConstants.RECEIPTTYPE_RECIPT);
							}
						}
					}
				}
			}

			// Payable Amount Reserve
			if (StringUtils.equals(paymentType, RepayConstants.RECEIPTMODE_PAYABLE)
					&& !StringUtils.equals(rch.getReceiptModeStatus(), RepayConstants.PAYSTATUS_CANCEL)) {

				// Payable Amount make utilization
				ManualAdviseReserve payableReserve = manualAdviseDAO.getPayableReserve(receiptSeqID,
						rcd.getPayAgainstID());

				BigDecimal payableAmt = rcd.getAmount();
				if (payAdvMovement != null) {
					TaxHeader taxHeader = payAdvMovement.getTaxHeader();
					if (taxHeader != null && CollectionUtils.isNotEmpty(taxHeader.getTaxDetails())) {
						List<Taxes> taxDetails = taxHeader.getTaxDetails();
						for (Taxes taxes : taxDetails) {
							if (StringUtils.equals(taxes.getTaxType(), FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {
								payableAmt = payableAmt.subtract(taxes.getPaidTax());
							}
						}
					}
				}

				if (payableReserve == null) {

					// Update Payable Amount in Reserve
					manualAdviseDAO.updatePayableReserve(rcd.getPayAgainstID(), payableAmt);

					// Save Payable Reserve Log Amount
					manualAdviseDAO.savePayableReserveLog(receiptSeqID, rcd.getPayAgainstID(), payableAmt);

				} else {
					// If Receipt details re-modified in process
					if (rcd.isDelRecord()) {

						// Delete Reserved Log against Payable Advise ID and
						// Receipt ID
						manualAdviseDAO.deletePayableReserve(receiptSeqID, rcd.getPayAgainstID());

						// Update Reserve Amount in Manual Advise
						manualAdviseDAO.updatePayableReserve(rcd.getPayAgainstID(),
								payableReserve.getReservedAmt().negate());

					} else {

						if (payableAmt.compareTo(payableReserve.getReservedAmt()) != 0) {
							BigDecimal diffInReserve = payableAmt.subtract(payableReserve.getReservedAmt());

							// Update Reserve Amount in Manual Advise
							manualAdviseDAO.updatePayableReserve(rcd.getPayAgainstID(), diffInReserve);

							// Update Payable Reserve Log
							manualAdviseDAO.updatePayableReserveLog(receiptSeqID, rcd.getPayAgainstID(), diffInReserve);
						}
					}
				}
			}

			// Manual Advise Movements
			if (CollectionUtils.isNotEmpty(rcd.getAdvMovements())) {
				for (ManualAdviseMovements movement : rcd.getAdvMovements()) {
					if (movement.getTaxHeader() != null
							&& CollectionUtils.isNotEmpty(movement.getTaxHeader().getTaxDetails())) {
						List<Taxes> taxDetails = movement.getTaxHeader().getTaxDetails();
						long headerId = taxHeaderDetailsDAO.save(movement.getTaxHeader(),
								TableType.TEMP_TAB.getSuffix());
						for (Taxes taxes : taxDetails) {
							taxes.setReferenceId(headerId);
						}
						taxHeaderDetailsDAO.saveTaxes(taxDetails, TableType.TEMP_TAB.getSuffix());
						movement.setTaxHeaderId(headerId);
					}
					movement.setReceiptID(receiptID);
					movement.setReceiptSeqID(receiptSeqID);
					manualAdviseDAO.saveMovement(movement, TableType.TEMP_TAB.getSuffix());
				}
			}
		}

		// Receipt Allocation Details
		for (int i = 0; i < rch.getAllocations().size(); i++) {
			ReceiptAllocationDetail allocation = rch.getAllocations().get(i);
			allocation.setReceiptID(receiptID);
			allocation.setAllocationID(i + 1);
		}

		if (CollectionUtils.isNotEmpty(rch.getAllocations())) {
			for (ReceiptAllocationDetail allocation : rch.getAllocations()) {
				if (StringUtils.isNotBlank(allocation.getTaxType())) {
					if (allocation.getTaxHeader() != null
							&& CollectionUtils.isNotEmpty(allocation.getTaxHeader().getTaxDetails())) {

						TaxHeader taxHeader = allocation.getTaxHeader();
						taxHeader.setLastMntBy(rch.getLastMntBy());
						taxHeader.setLastMntOn(rch.getLastMntOn());
						taxHeader.setRecordStatus(recordStatus);
						taxHeader.setRecordType(rch.getRecordType());
						taxHeader.setVersion(rch.getVersion());
						taxHeader.setWorkflowId(rch.getWorkflowId());
						taxHeader.setTaskId(rch.getTaskId());
						taxHeader.setNextTaskId(rch.getNextTaskId());
						taxHeader.setRoleCode(roleCode);
						taxHeader.setNextRoleCode(rch.getNextRoleCode());
						Long headerId = taxHeaderDetailsDAO.save(allocation.getTaxHeader(), tableType.getSuffix());

						List<Taxes> taxDetails = taxHeader.getTaxDetails();
						for (Taxes taxes : taxDetails) {
							taxes.setReferenceId(headerId);
						}
						taxHeaderDetailsDAO.saveTaxes(taxDetails, tableType.getSuffix());
						allocation.setTaxHeaderId(headerId);
					}
				}
			}
		}
		allocationDetailDAO.saveAllocations(rch.getAllocations(), tableType);

		if (FinanceConstants.DEPOSIT_APPROVER.equals(roleCode)
				&& !(PennantConstants.RCD_STATUS_SAVED.equals(recordStatus)
						|| PennantConstants.RCD_STATUS_RESUBMITTED.equals(recordStatus))) {
			executeAccounting(rceiptData);
		}

		// Finance Fee Details
		// =======================================
		if (schdData.getFinFeeDetailList() != null && !schdData.getFinFeeDetailList().isEmpty()) {
			saveOrUpdateFees(rceiptData, tableType.getSuffix());
		}

		for (int i = 0; i < rch.getAllocations().size(); i++) {
			ReceiptAllocationDetail allocation = rch.getAllocations().get(i);
			if (StringUtils.equals(RepayConstants.ALLOCATION_FEE, allocation.getAllocationType())) {
				for (FinFeeDetail feeDtl : schdData.getFinFeeDetailList()) {
					if (feeDtl.getFeeTypeID() == -(allocation.getAllocationTo())) {
						allocation.setAllocationTo(feeDtl.getFeeID());
						break;
					}
				}
			}
		}

		FinReceiptHeader befRctHeader = new FinReceiptHeader();
		if (rch.isNewRecord()) {
			BeanUtils.copyProperties(rch, befRctHeader);
		} else {
			befRctHeader = getFinReceiptHeaderById(rch.getReceiptID(), false, "_View");
		}
		// FinReceiptDetail Audit Details Preparation
		FinReceiptDetail adtFinReceiptDetail = new FinReceiptDetail();
		String[] rhFields = PennantJavaUtil.getFieldDetails(adtFinReceiptDetail,
				adtFinReceiptDetail.getExcludeFields());
		for (int i = 0; i < rch.getReceiptDetails().size(); i++) {
			if (CollectionUtils.isNotEmpty(befRctHeader.getReceiptDetails())) {
				auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), 1, rhFields[0], rhFields[1],
						befRctHeader.getReceiptDetails().get(i), rch.getReceiptDetails().get(i)));
			}
		}

		// Save Document Details

		// set Finance Check List audit details to auditDetails
		// =======================================
		if (fd.getFinanceCheckList() != null && !fd.getFinanceCheckList().isEmpty()) {
			auditDetails.addAll(checkListDetailService.saveOrUpdate(fd, tableType.getSuffix(), serviceUID));
		}

		// Extended field Details
		if (rceiptData.getFinanceDetail().getExtendedFieldRender() != null) {
			List<AuditDetail> details = rceiptData.getFinanceDetail().getAuditDetailMap().get("ExtendedFieldDetails");
			details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
					ExtendedFieldConstants.MODULE_LOAN,
					rceiptData.getFinanceDetail().getExtendedFieldHeader().getEvent(), tableType.getSuffix(),
					serviceUID);
			auditDetails.addAll(details);
		}

		// Extended field Extensions Details
		if (rceiptData.getFinanceDetail().getExtendedFieldExtension() != null) {
			List<AuditDetail> details = rceiptData.getFinanceDetail().getAuditDetailMap().get("ExtendedFieldExtension");
			details = extendedFieldExtensionService.processingExtendedFieldExtList(details, rceiptData, serviceUID,
					tableType);

			auditDetails.addAll(details);
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), rch.getExcludeFields());
		auditHeader.setAuditDetail(
				new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], rch.getBefImage(), rch));

		auditHeader.setAuditDetails(auditDetails);
		auditHeader.setAuditModule("Receipt");
		auditHeaderDAO.addAudit(auditHeader);
		auditHeader.getAuditDetail().setModelData(rceiptData);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * Delete Tax Details
	 */
	public void deleteTaxHeaderId(long receiptId, String type) {
		logger.debug(Literal.ENTERING);

		List<Long> headerIdsByReceiptId = taxHeaderDetailsDAO.getHeaderIdsByReceiptId(receiptId, type);
		if (CollectionUtils.isNotEmpty(headerIdsByReceiptId)) {
			for (Long headerIds : headerIdsByReceiptId) {
				if (headerIds != null && headerIds > 0) {
					taxHeaderDetailsDAO.delete(headerIds, type);
					TaxHeader taxHeader = new TaxHeader();
					taxHeader.setHeaderId(headerIds);
					taxHeaderDetailsDAO.delete(taxHeader, type);
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public long executeAccounting(FinReceiptData rd) {
		logger.debug(Literal.ENTERING);
		FinanceDetail fd = rd.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		AEEvent aeEvent = new AEEvent();
		aeEvent.setEntityCode(fm.getEntityCode());
		aeEvent.setPostingUserBranch(rd.getReceiptHeader().getCashierBranch());
		aeEvent.setAccountingEvent(AccountingEvent.RECIP);
		aeEvent.setFinID(rd.getFinID());
		aeEvent.setFinReference(rd.getFinReference());
		aeEvent.setValueDate(SysParamUtil.getAppDate());

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
		}
		Map<String, Object> eventMapping = null;
		String btLoan = "";
		rd.getReceiptHeader().getPartnerBankCode();
		// FinanceMain financeMain =
		// financeMainDAO.getFinanceMainForBatch(receiptD.getFinReference());
		amountCodes.setFinType(fm.getFinType());
		aeEvent.setBranch(fm.getFinBranch());
		aeEvent.setCcy(fm.getFinCcy());
		aeEvent.setCustID(fm.getCustID());
		amountCodes.setBusinessvertical(fm.getBusinessVerticalCode());
		amountCodes.setAlwflexi(fm.isAlwFlexi());
		amountCodes.setFinbranch(fm.getFinBranch());
		amountCodes.setEntitycode(fm.getEntityCode());
		amountCodes.setPartnerBankAc(rd.getReceiptHeader().getReceiptDetails().get(0).getPartnerBankAc());
		btLoan = fm.getLoanCategory();

		eventMapping = financeMainDAO.getGLSubHeadCodes(fm.getFinID());

		Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();
		if (eventMapping != null) {
			// dataMap = aeEvent.getDataMap();
			dataMap.put("emptype", eventMapping.get("EMPTYPE"));
			dataMap.put("branchcity", eventMapping.get("BRANCHCITY"));
			dataMap.put("fincollateralreq", eventMapping.get("FINCOLLATERALREQ"));
			dataMap.put("btloan", btLoan);
		}
		rd.getReceiptHeader().getReceiptDetails().get(0).getDeclaredFieldValues(aeEvent.getDataMap());
		aeEvent.setDataMap(dataMap);

		BigDecimal amount = BigDecimal.ZERO;
		for (FinReceiptDetail detail : rd.getReceiptHeader().getReceiptDetails()) {
			if (!(detail.getPaymentType().equals(RepayConstants.RECEIPTMODE_EMIINADV)
					|| detail.getPaymentType().equals(RepayConstants.RECEIPTMODE_EXCESS)
					|| detail.getPaymentType().equals(RepayConstants.RECEIPTMODE_PAYABLE))) {
				amount = amount.add(detail.getAmount());
			}
		}

		aeEvent.getDataMap().put("rd_amount", amount);

		long accountsetId = accountingSetDAO.getAccountingSetId(AccountingEvent.RECIP, AccountingEvent.RECIP);
		aeEvent.getAcSetIDList().add(accountsetId);
		aeEvent = postingsPreparationUtil.postAccounting(aeEvent);
		logger.debug(Literal.LEAVING);
		return aeEvent.getLinkedTranId();
	}

	public void saveOrUpdateFees(FinReceiptData receiptData, String tableType) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = receiptData.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();

		List<FinFeeDetail> feeDetailsList = schdData.getFinFeeDetailList();
		long finID = schdData.getFinID();
		String finReference = schdData.getFinReference();
		boolean newRecord = receiptData.getReceiptHeader().isNewRecord();

		List<FinFeeDetail> oldFeedetails = receiptData.getFinFeeDetails();
		if (CollectionUtils.isNotEmpty(oldFeedetails)) {
			for (FinFeeDetail fee : oldFeedetails) {
				finFeeScheduleDetailDAO.deleteFeeScheduleBatch(fee.getFeeID(), false, "_Temp");
				fee.setFinID(finID);
				fee.setFinReference(finReference);
				finFeeDetailDAO.delete(fee, false, "_Temp");
				TaxHeader taxHeader = fee.getTaxHeader();

				if (taxHeader != null && taxHeader.getId() > 0) {
					List<Taxes> taxDetails = taxHeader.getTaxDetails();
					if (CollectionUtils.isNotEmpty(taxDetails)) {
						taxHeaderDetailsDAO.delete(taxHeader.getId(), tableType);
						taxHeaderDetailsDAO.delete(taxHeader, tableType);
					}
				}
			}
		}

		if (CollectionUtils.isEmpty(feeDetailsList)) {
			return;
		}

		long receiptID = receiptData.getReceiptHeader().getReceiptID();

		for (FinFeeDetail fee : feeDetailsList) {
			fee.setFinID(finID);
			fee.setFinReference(finReference);
			fee.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			TaxHeader taxHeader = fee.getTaxHeader();

			if (!newRecord && fee.isOriginationFee() && fee.getFeeID() > 0) {
				finFeeDetailDAO.update(fee, false, tableType);

				if (taxHeader != null) {
					List<Taxes> taxDetails = taxHeader.getTaxDetails();
					if (CollectionUtils.isNotEmpty(taxDetails)) {
						for (Taxes taxes : taxDetails) {
							taxHeaderDetailsDAO.update(taxes, tableType);
						}
					}
				}
			} else {
				if (!fee.isOriginationFee()) {
					fee.setFeeSeq(finFeeDetailDAO.getFeeSeq(fee, false, tableType) + 1);
					fee.setReferenceId(receiptID);
				}
				if (taxHeader != null) {
					List<Taxes> taxDetails = taxHeader.getTaxDetails();
					if (CollectionUtils.isNotEmpty(taxDetails)) {
						long headerId = taxHeaderDetailsDAO.save(taxHeader, tableType);
						for (Taxes taxes : taxDetails) {
							taxes.setReferenceId(headerId);
						}
						taxHeaderDetailsDAO.saveTaxes(taxDetails, tableType);
						fee.setTaxHeaderId(headerId);
					}
				}
				fee.setFeeID(finFeeDetailDAO.save(fee, false, tableType));
			}

			if (CollectionUtils.isNotEmpty(fee.getFinFeeScheduleDetailList())) {
				for (FinFeeScheduleDetail feeSchedule : fee.getFinFeeScheduleDetailList()) {
					feeSchedule.setFeeID(fee.getFeeID());
				}
				finFeeScheduleDetailDAO.saveFeeScheduleBatch(fee.getFinFeeScheduleDetailList(), false, tableType);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public void approveFees(FinReceiptData receiptData, String tableType) {
		logger.debug("Entering ");

		FinanceDetail fd = receiptData.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		List<FinFeeDetail> feeDetailsList = schdData.getFinFeeDetailList();
		String finReference = receiptData.getFinReference();

		List<FinFeeDetail> oldFeedetails = receiptData.getFinFeeDetails();
		if (CollectionUtils.isNotEmpty(oldFeedetails)) {
			for (FinFeeDetail fee : oldFeedetails) {
				if (CollectionUtils.isNotEmpty(fee.getFinFeeScheduleDetailList())) {
					finFeeScheduleDetailDAO.deleteFeeScheduleBatch(fee.getFeeID(), false, "_Temp");
				}
				finFeeDetailDAO.delete(fee, false, "_Temp");
				TaxHeader taxHeader = fee.getTaxHeader();
				Long taxHeaderId = fee.getTaxHeaderId();
				if (taxHeaderId != null && taxHeaderId > 0) {
					if (taxHeader == null) {
						taxHeader = new TaxHeader();
						taxHeader.setHeaderId(taxHeaderId);
					}
					taxHeaderDetailsDAO.delete(taxHeaderId, "_Temp");
					taxHeaderDetailsDAO.delete(taxHeader, "_Temp");
				}
			}
		}

		long receiptID = receiptData.getReceiptHeader().getReceiptID();

		for (FinFeeDetail fee : feeDetailsList) {
			if (PennantConstants.RECORD_TYPE_CAN.equals(fee.getRecordType())) {
				continue;
			}

			fee.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			fee.setRecordType("");
			fee.setRoleCode("");
			fee.setNextRoleCode("");
			fee.setTaskId("");
			fee.setNextTaskId("");
			fee.setWorkflowId(0);
			fee.setFinReference(finReference);

			TaxHeader taxHeader = fee.getTaxHeader();
			if (fee.isOriginationFee()) {
				finFeeDetailDAO.update(fee, false, tableType);
				if (taxHeader != null) {
					List<Taxes> taxDetails = taxHeader.getTaxDetails();
					if (CollectionUtils.isNotEmpty(taxDetails)) {
						for (Taxes taxes : taxDetails) {
							taxHeaderDetailsDAO.update(taxes, tableType);
						}
					}
				}
			} else {
				fee.setReferenceId(receiptID);
				if (taxHeader != null) {
					List<Taxes> taxDetails = taxHeader.getTaxDetails();
					if (CollectionUtils.isNotEmpty(taxDetails)) {
						long headerId = taxHeaderDetailsDAO.save(taxHeader, tableType);
						for (Taxes taxes : taxDetails) {
							taxes.setReferenceId(headerId);
						}
						taxHeaderDetailsDAO.saveTaxes(taxDetails, tableType);
						fee.setTaxHeaderId(headerId);
					}
				}
				finFeeDetailDAO.save(fee, false, tableType);
			}

			List<FinFeeScheduleDetail> finFeeScheduleDetailList = fee.getFinFeeScheduleDetailList();
			if (CollectionUtils.isNotEmpty(finFeeScheduleDetailList)) {
				for (FinFeeScheduleDetail finFeeSchDetail : finFeeScheduleDetailList) {
					finFeeSchDetail.setFeeID(fee.getFeeID());
				}
				finFeeScheduleDetailDAO.saveFeeScheduleBatch(finFeeScheduleDetailList, false, tableType);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public void listDeletion(long finID, String tableType) {
		financeScheduleDetailDAO.deleteByFinReference(finID, tableType, false, 0);
		repayInstructionDAO.deleteByFinReference(finID, tableType, false, 0);
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}
		String tranType = PennantConstants.TRAN_DEL;

		FinReceiptData rd = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		FinReceiptHeader rch = rd.getReceiptHeader();
		rch.setRcdMaintainSts(null);
		FinanceDetail fd = rd.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		List<AuditDetail> auditDetails = new ArrayList<>();

		long serviceUID = Long.MIN_VALUE;
		for (FinServiceInstruction finServInst : fd.getFinScheduleData().getFinServiceInstructions()) {
			serviceUID = finServInst.getInstructionUID();
		}

		// Cancel All Transactions done by Finance Reference
		// =======================================
		cancelStageAccounting(fm.getFinID(), rch.getReceiptPurpose());

		// ScheduleDetails deletion
		// listDeletion(financeMain.getFinReference(),
		// TableType.TEMP_TAB.getSuffix());
		// financeMainDAO.delete(financeMain, TableType.TEMP_TAB, false, false);

		// Delete and Save Repayment Schedule details by setting Repay Header ID
		// financeRepaymentsDAO.deleteRpySchdList(financeMain.getFinReference(),
		// TableType.TEMP_TAB.getSuffix());

		// Delete and Save FinRepayHeader Detail list by Reference
		financeRepaymentsDAO.deleteByReceiptId(rch.getReceiptID(), TableType.TEMP_TAB);

		for (FinReceiptDetail receiptDetail : rch.getReceiptDetails()) {
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
		finReceiptDetailDAO.deleteByReceiptID(rch.getReceiptID(), TableType.TEMP_TAB);

		deleteTaxHeaderId(rch.getReceiptID(), TableType.TEMP_TAB.getSuffix());

		// Receipt Allocation Details
		allocationDetailDAO.deleteByReceiptID(rch.getReceiptID(), TableType.TEMP_TAB);

		// Delete Manual Advise Movements
		manualAdviseDAO.deleteMovementsByReceiptID(rch.getReceiptID(), TableType.TEMP_TAB.getSuffix());

		// Delete Receipt Header
		finReceiptHeaderDAO.deleteByReceiptID(rch.getReceiptID(), TableType.TEMP_TAB);

		// FinReceiptDetail Audit Details Preparation
		String[] rFields = PennantJavaUtil.getFieldDetails(new FinReceiptDetail(),
				rch.getReceiptDetails().get(0).getExcludeFields());
		for (int i = 0; i < rch.getReceiptDetails().size(); i++) {
			auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), 1, rFields[0], rFields[1],
					rch.getReceiptDetails().get(i), rch.getReceiptDetails().get(i)));
		}

		// Receipt Header Audit Details Preparation
		String[] rhFields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), rch.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(tranType, 1, rhFields[0], rhFields[1], rch.getBefImage(), rch));

		// Delete Fee Details
		List<FinFeeDetail> oldFeedetails = rd.getFinFeeDetails();
		if (CollectionUtils.isNotEmpty(oldFeedetails)) {
			for (FinFeeDetail finFeeDetail : oldFeedetails) {
				finFeeScheduleDetailDAO.deleteFeeScheduleBatch(finFeeDetail.getFeeID(), false, "_Temp");
				finFeeDetailDAO.delete(finFeeDetail, false, "_Temp");
			}
		}

		// Delete Document Details
		if (fd.getDocumentDetailsList() != null && fd.getDocumentDetailsList().size() > 0) {
			for (DocumentDetails docDetails : fd.getDocumentDetailsList()) {
				docDetails.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
			List<AuditDetail> details = fd.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, TableType.TEMP_TAB.getSuffix(),
					fd.getFinScheduleData().getFinanceMain(), rch.getReceiptPurpose(), serviceUID);
			auditDetails.addAll(details);
		}

		// Checklist Details delete
		// =======================================
		auditDetails.addAll(checkListDetailService.delete(fd, TableType.TEMP_TAB.getSuffix(), tranType));

		// Delete Extended field Render Details.
		List<AuditDetail> extendedDetails = fd.getAuditDetailMap().get("ExtendedFieldDetails");
		if (extendedDetails != null && extendedDetails.size() > 0) {
			auditDetails.addAll(extendedFieldDetailsService.delete(rd.getFinanceDetail().getExtendedFieldHeader(),
					fm.getFinReference(), rd.getFinanceDetail().getExtendedFieldRender().getSeqNo(), "_Temp",
					auditHeader.getAuditTranType(), extendedDetails));
		}

		if (rd.getFinanceDetail().getExtendedFieldExtension() != null) {
			List<AuditDetail> details = rd.getFinanceDetail().getAuditDetailMap().get("ExtendedFieldExtension");
			details = extendedFieldExtensionService.delete(details, tranType, TableType.TEMP_TAB);
			auditDetails.addAll(details);
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.getAuditDetail().setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditModule("Receipt");
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		// Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(rd);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	@SuppressWarnings("unchecked")
	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader) throws Exception {
		logger.debug(Literal.ENTERING);

		FinReceiptData orgReceiptData = (FinReceiptData) aAuditHeader.getAuditDetail().getModelData();
		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		FinReceiptData receiptData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();

		String roleCode = receiptData.getReceiptHeader().getRoleCode();
		String nextRoleCode = receiptData.getReceiptHeader().getNextRoleCode();
		FinanceDetail fd = receiptData.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		boolean isLoanActiveBef = fm.isFinIsActive();
		Date appDate = SysParamUtil.getAppDate();

		boolean isGoldLoanProc = false;
		if (StringUtils.equals(fm.getProductCategory(), FinanceConstants.PRODUCT_GOLD)) {
			isGoldLoanProc = true;
		}

		long finID = fm.getFinID();
		if (!RepayConstants.RECEIPTMODE_RESTRUCT.equals(receiptData.getReceiptHeader().getReceiptMode())
				&& financeScheduleDetailDAO.isScheduleInQueue(finID)) {
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
					List<RepayScheduleDetail> rpySchdList = financeRepaymentsDAO
							.getRpySchdListByRepayID(rph.getRepayID(), "");
					rph.setRepayScheduleDetails(rpySchdList);
				}
			}
			receiptCancellationService.doApprove(aAuditHeader);
			aAuditHeader.getAuditDetail().setModelData(receiptData);
			return aAuditHeader;
		}
		if (StringUtils.equals(FinanceConstants.REALIZATION_APPROVER, roleCode)) {
			if ((StringUtils.equals(receiptData.getReceiptHeader().getReceiptPurpose(), FinServiceEvent.SCHDRPY)
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
		// schedule pay effect on cheque/dd realization(if N schedule will effect while approve/if Y schedule will
		// effect while realization)
		if (!SysParamUtil.isAllowed(SMTParameterConstants.CHEQUE_MODE_SCHDPAY_EFFT_ON_REALIZATION)
				&& SysParamUtil.isAllowed(SMTParameterConstants.CHQ_RECEIPTS_PAID_AT_DEPOSIT_APPROVER)) {
			if (StringUtils.equals(FinanceConstants.REALIZATION_APPROVER, roleCode)) {
				if (((StringUtils.equals(receiptData.getReceiptHeader().getReceiptPurpose(), FinServiceEvent.SCHDRPY)
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
		if (schdData.getFinServiceInstructions().isEmpty()) {
			FinServiceInstruction finServInst = new FinServiceInstruction();
			finServInst.setFinID(receiptHeader.getFinID());
			finServInst.setFinReference(receiptHeader.getReference());
			finServInst.setFinEvent(fd.getModuleDefiner());
			schdData.setFinServiceInstruction(finServInst);
		}

		if (!StringUtils.equalsIgnoreCase(PennantConstants.FINSOURCE_ID_API, receiptData.getSourceId())
				&& !schdData.getFinServiceInstruction().isReceiptUpload()) {
			if ((StringUtils.equals(receiptData.getReceiptHeader().getReceiptPurpose(), FinServiceEvent.EARLYRPY)
					|| StringUtils.equals(receiptData.getReceiptHeader().getReceiptPurpose(),
							FinServiceEvent.EARLYSETTLE))) {

				aAuditHeader = approveValidation(aAuditHeader, "doApprove");
			}
		}

		aAuditHeader = businessValidation(aAuditHeader, "doApprove");
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		long serviceUID = Long.MIN_VALUE;

		BigDecimal restructBpiAmount = BigDecimal.ZERO;
		if (receiptData.getFinanceDetail().getExtendedFieldRender() != null) {
			serviceUID = extendedFieldDetailsService.getInstructionUID(
					receiptData.getFinanceDetail().getExtendedFieldRender(),
					receiptData.getFinanceDetail().getExtendedFieldExtension());
		}

		for (FinServiceInstruction finSerList : schdData.getFinServiceInstructions()) {
			if (finSerList.getInstructionUID() == Long.MIN_VALUE) {
				if (serviceUID == Long.MIN_VALUE) {
					serviceUID = Long.valueOf(ReferenceGenerator.generateNewServiceUID());
				}
				finSerList.setInstructionUID(serviceUID);
			} else {
				serviceUID = finSerList.getInstructionUID();
			}

			restructBpiAmount = finSerList.getBpiAmount();
		}

		// Finance Stage Accounting Process
		// =======================================
		auditHeader = executeStageAccounting(auditHeader);
		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}

		// Repayments Postings Details Process Execution
		FinanceProfitDetail profitDetail = schdData.getFinPftDeatil();

		// Execute Accounting Details Process
		// =======================================
		FinReceiptData rcdata = (FinReceiptData) aAuditHeader.getAuditDetail().getModelData();
		FinanceDetail financeDetail = rcdata.getFinanceDetail();
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

		// For Extended fields
		receiptData.getFinanceDetail().setAuditDetailMap(financeDetail.getAuditDetailMap());
		receiptData.getFinanceDetail().setExtendedFieldExtension(financeDetail.getExtendedFieldExtension());
		receiptData.getFinanceDetail().setExtendedFieldHeader(financeDetail.getExtendedFieldHeader());
		receiptData.getFinanceDetail().setExtendedFieldRender(financeDetail.getExtendedFieldRender());
		receiptData.getReceiptHeader().setDepositProcess(orgReceiptData.getReceiptHeader().isDepositProcess());

		FinScheduleData scheduleData = schdData;
		FinanceMain financeMain = scheduleData.getFinanceMain();

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
			// rch.setReceivedDate(rch.getValueDate());
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

		// Restructured BPI Amount
		rch.setBpiAmount(restructBpiAmount);

		// Resetting Maturity Terms & Summary details rendering in case of
		// Reduce maturity cases
		if (receiptData.isDueAdjusted()) {
			scheduleData.setFinanceScheduleDetails(finSchdDtls);
		} else {
			scheduleData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false));
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
			BigDecimal subVentionAmt = this.subventionDetailDAO.getTotalSubVentionAmt(finID);
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
				scheduleData.getFinFeeDetailList(), scheduleData, valueDate, curBusDate, fd);
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

		if (StringUtils.equals(FinServiceEvent.EARLYSETTLE, rch.getReceiptPurpose())) {
			reqMaxODDate = rch.getValueDate();
		}

		if (!ImplementationConstants.LPP_CALC_SOD) {
			reqMaxODDate = DateUtility.addDays(valueDate, -1);
		}
		overdueList = finODDetailsDAO.getFinODBalByFinRef(finID);

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
		// assigning user details to fix the 900 blocker while approve in deposit approver screen
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
				&& (StringUtils.equals(FinServiceEvent.SCHDRPY, receiptData.getReceiptHeader().getReceiptPurpose()))
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

		User logiedInUser = SessionUserDetails.getLogiedInUser();
		if (logiedInUser != null) {
			financeMain.setLastMntBy(logiedInUser.getUserId());
			financeMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		}

		financeMainDAO.updateFromReceipt(financeMain, TableType.MAIN_TAB);

		// ScheduleDetails delete and save
		// =======================================
		listDeletion(finID, "");
		listSave(scheduleData, "", 0, false);

		// Finance Fee Details
		if (scheduleData.getFinFeeDetailList() != null) {
			if (!FinServiceEvent.RESTRUCTURE.equals(rch.getReceiptPurpose())) {
				approveFees(receiptData, TableType.MAIN_TAB.getSuffix());
			}
		}

		if (scheduleData.getFinFeeDetailList() != null) {
			for (int i = 0; i < rch.getAllocations().size(); i++) {
				ReceiptAllocationDetail allocation = rch.getAllocations().get(i);
				if (StringUtils.equals(RepayConstants.ALLOCATION_FEE, allocation.getAllocationType())) {
					for (FinFeeDetail feeDtl : schdData.getFinFeeDetailList()) {
						if (feeDtl.getFeeTypeID() == -(allocation.getAllocationTo())) {
							allocation.setAllocationTo(feeDtl.getFeeID());
							break;
						}
					}
				}
			}
		}
		List<AuditDetail> tempAuditDetailList = new ArrayList<AuditDetail>();

		if (!StringUtils.equals(PennantConstants.FINSOURCE_ID_API, receiptData.getSourceId())
				&& !fd.isDirectFinalApprove()) {
			// Save Document Details
			if (fd.getDocumentDetailsList() != null && fd.getDocumentDetailsList().size() > 0) {
				listDocDeletion(fd, TableType.TEMP_TAB.getSuffix());
			}

			// set Check list details Audit
			// =======================================
			if (fd.getFinanceCheckList() != null && !fd.getFinanceCheckList().isEmpty()) {
				auditDetails.addAll(checkListDetailService.doApprove(fd, "", serviceUID));
			}

			// Extended Field Details
			List<AuditDetail> extendedDetails = receiptData.getFinanceDetail().getAuditDetailMap()
					.get("ExtendedFieldDetails");
			if (extendedDetails != null && extendedDetails.size() > 0) {
				extendedDetails = extendedFieldDetailsService.processingExtendedFieldDetailList(extendedDetails,
						ExtendedFieldConstants.MODULE_LOAN,
						receiptData.getFinanceDetail().getExtendedFieldHeader().getEvent(), "", serviceUID);
				auditDetails.addAll(extendedDetails);
			}

			// Extended field Extensions Details
			List<AuditDetail> extensionDetails = receiptData.getFinanceDetail().getAuditDetailMap()
					.get("ExtendedFieldExtension");
			if (extensionDetails != null && extensionDetails.size() > 0) {
				extensionDetails = extendedFieldExtensionService.processingExtendedFieldExtList(extensionDetails,
						receiptData, serviceUID, TableType.MAIN_TAB);

				auditDetails.addAll(extensionDetails);
			}

			// ScheduleDetails delete
			// =======================================
			listDeletion(finID, TableType.TEMP_TAB.getSuffix());

			// Checklist Details delete
			// =======================================
			tempAuditDetailList.addAll(checkListDetailService.delete(fd, TableType.TEMP_TAB.getSuffix(), tranType));

			// Delete and Save Repayments Schedule details by setting Repay
			// Header ID
			financeRepaymentsDAO.deleteRpySchdList(finID, TableType.TEMP_TAB.getSuffix());

			// Delete and Save FinRepayHeader Detail list by Reference
			financeRepaymentsDAO.deleteByRef(finID, TableType.TEMP_TAB);

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
			if (extendedDetails != null && extendedDetails.size() > 0) {
				extendedDetails = extendedFieldDetailsService.delete(
						receiptData.getFinanceDetail().getExtendedFieldHeader(), financeMain.getFinReference(),
						receiptData.getFinanceDetail().getExtendedFieldRender().getSeqNo(), "_Temp",
						auditHeader.getAuditTranType(), extendedDetails);
				auditDetails.addAll(extendedDetails);
			}

			if (extensionDetails != null && extensionDetails.size() > 0) {
				extensionDetails = extendedFieldExtensionService.delete(extensionDetails, tranType, TableType.TEMP_TAB);
				auditDetails.addAll(extensionDetails);
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
		auditHeaderDAO.addAudit(auditHeader);
		// Reset Finance Detail Object for Service Task Verifications
		schdData.setFinanceMain(financeMain);
		auditHeader.getAuditDetail().setModelData(receiptData);

		// ===========================================
		// Fetch Total Repayment Amount till Maturity date for Early Settlement
		if (FinanceConstants.CLOSE_STATUS_MATURED.equals(financeMain.getClosingStatus())) {

			// send Collateral DeMark request to Interface
			// ==========================================
			if (ImplementationConstants.COLLATERAL_INTERNAL) {
				if (ImplementationConstants.COLLATERAL_DELINK_AUTO) {
					getCollateralAssignmentValidation().saveCollateralMovements(financeMain.getFinReference());
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
			Customer customer = customerDAO.getCustomerByID(financeMain.getCustID());
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
		if (FinServiceEvent.EARLYRPY.equals(rch.getReceiptPurpose())) {
			advancePaymentService.setAdvancePaymentDetails(scheduleData.getFinanceMain(), scheduleData);
		}

		logger.debug(Literal.LEAVING);

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
		long finID = fm.getFinID();
		List<FinanceRepayments> repayments = financeRepaymentsDAO.getFinRepayListByFinRef(finID, false, "");
		latePayMarkingService.calPDOnBackDatePayment(fm, odList, valueDate, schdList, repayments, true, true);

		logger.debug(Literal.LEAVING);
		return odList;
	}

	/**
	 * Method for Saving Deposit Details for Both Receipt Modes of CASH & Cheque/DD
	 * 
	 * @param receiptHeader
	 */
	private void saveDepositDetails(FinReceiptHeader receiptHeader, String method) {
		logger.debug(Literal.ENTERING);

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

		logger.debug(Literal.LEAVING);
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
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 * @throws AccountNotFoundException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Override
	public AuditHeader doReversal(AuditHeader aAuditHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		aAuditHeader = businessValidation(aAuditHeader, "doApprove");
		if (!aAuditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		FinReceiptData rceiptData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		FinReceiptHeader receiptHeader = rceiptData.getReceiptHeader();
		receiptHeader.setPostBranch(auditHeader.getAuditBranchCode());
		FinanceDetail fd = rceiptData.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();

		long serviceUID = Long.MIN_VALUE;
		for (FinServiceInstruction finServInst : schdData.getFinServiceInstructions()) {
			serviceUID = finServInst.getInstructionUID();
		}

		// Cancel All Transactions done by Finance Reference
		// =======================================

		cancelStageAccounting(finID, receiptHeader.getReceiptPurpose());

		// Bounce Charge Due Postings
		if (StringUtils.equals(receiptHeader.getReceiptModeStatus(), RepayConstants.PAYSTATUS_BOUNCE)) {
			ManualAdvise bounce = receiptHeader.getManualAdvise();
			if (bounce != null && bounce.getAdviseAmount().compareTo(BigDecimal.ZERO) > 0) {

				if (bounce.getAdviseID() <= 0) {
					bounce.setAdviseID(this.manualAdviseDAO.getNewAdviseID());
				}

				AEEvent aeEvent = executeBounceDueAccounting(fm, receiptHeader.getBounceDate(), bounce,
						auditHeader.getAuditBranchCode(), RepayConstants.ALLOCATION_BOUNCE);
				if (aeEvent != null && StringUtils.isNotEmpty(aeEvent.getErrorMessage())) {
					ArrayList<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
					errorDetails.add(new ErrorDetail("Accounting Engine", PennantConstants.ERR_UNDEF, "E",
							aeEvent.getErrorMessage(), new String[] {}, new String[] {}));
					logger.debug(Literal.LEAVING);
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

		if (!StringUtils.equals(PennantConstants.FINSOURCE_ID_API, rceiptData.getSourceId())) {

			// Save Document Details
			if (fd.getDocumentDetailsList() != null && fd.getDocumentDetailsList().size() > 0) {
				List<AuditDetail> details = fd.getAuditDetailMap().get("DocumentDetails");
				details = processingDocumentDetailsList(details, "", schdData.getFinanceMain(),
						receiptHeader.getReceiptPurpose(), serviceUID);
				auditDetails.addAll(details);
				listDocDeletion(fd, TableType.TEMP_TAB.getSuffix());
			}

			// set Check list details Audit
			// =======================================
			if (fd.getFinanceCheckList() != null && !fd.getFinanceCheckList().isEmpty()) {
				auditDetails.addAll(checkListDetailService.doApprove(fd, "", serviceUID));
			}

			String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), fm.getExcludeFields());

			// ScheduleDetails delete
			// =======================================
			listDeletion(finID, TableType.TEMP_TAB.getSuffix());

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
			tempAuditDetailList.addAll(checkListDetailService.delete(fd, TableType.TEMP_TAB.getSuffix(), tranType));

			// Delete and Save Repayments Schedule details by setting Repay
			// Header ID
			financeRepaymentsDAO.deleteRpySchdList(finID, TableType.TEMP_TAB.getSuffix());

			// Delete and Save FinRepayHeader Detail list by Reference
			financeRepaymentsDAO.deleteByRef(finID, TableType.TEMP_TAB);

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
			financeMainDAO.delete(fm, TableType.TEMP_TAB, false, true);

			// Extended field Render Details Delete from temp.
			FinReceiptData recData = (FinReceiptData) aAuditHeader.getAuditDetail().getModelData();
			List<AuditDetail> extendedDetails = recData.getFinanceDetail().getAuditDetailMap()
					.get("ExtendedFieldDetails");
			if (extendedDetails != null && extendedDetails.size() > 0) {
				tempAuditDetailList
						.addAll(extendedFieldDetailsService.delete(recData.getFinanceDetail().getExtendedFieldHeader(),
								fm.getFinReference(), recData.getFinanceDetail().getExtendedFieldRender().getSeqNo(),
								"_Temp", auditHeader.getAuditTranType(), extendedDetails));
			}

			if (recData.getFinanceDetail().getExtendedFieldExtension() != null) {
				List<AuditDetail> details = recData.getFinanceDetail().getAuditDetailMap()
						.get("ExtendedFieldExtension");
				details = extendedFieldExtensionService.delete(details, tranType, TableType.TEMP_TAB);
				auditDetails.addAll(details);
			}

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
			auditHeaderDAO.addAudit(auditHeader);

			// Receipt Header Audit Details Preparation
			auditDetails.add(new AuditDetail(tranType, 1, rhFields[0], rhFields[1],
					rceiptData.getReceiptHeader().getBefImage(), rceiptData.getReceiptHeader()));

			auditHeader.setAuditTranType(tranType);
			auditHeader.setAuditDetail(
					new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], fm.getBefImage(), fm));

			// Adding audit as Insert/Update/deleted into main table
			auditHeader.setAuditDetails(auditDetails);
			auditHeader.setAuditModule("Receipt");
			auditHeaderDAO.addAudit(auditHeader);
		}

		// Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(rceiptData);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * Method for Saving List of Finance Details
	 * 
	 * @param schdData
	 * @param tableType
	 * @param logKey
	 */
	private void listSave(FinScheduleData schdData, String tableType, long logKey, boolean saveDisb) {
		Map<Date, Integer> mapDateSeq = new HashMap<>();

		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();
		List<FinanceDisbursement> disbursements = schdData.getDisbursementDetails();
		List<RepayInstruction> repayInstructions = schdData.getRepayInstructions();

		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		// Finance Schedule Details
		if (StringUtils.isEmpty(tableType)) {
			for (FinanceScheduleDetail curSchd : schedules) {
				curSchd.setLastMntBy(fm.getLastMntBy());
				curSchd.setFinID(finID);
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

			financeScheduleDetailDAO.saveList(schedules, tableType, false);

			// Schedule Version Updating
			financeMainDAO.updateSchdVersion(fm, false);
		}

		if (logKey != 0 || saveDisb) {
			// Finance Disbursement Details
			mapDateSeq = new HashMap<>();
			for (FinanceDisbursement dd : disbursements) {
				dd.setFinID(finID);
				dd.setFinReference(finReference);
				dd.setDisbIsActive(true);
				dd.setLogKey(logKey);
				dd.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				dd.setLastMntBy(schdData.getFinanceMain().getLastMntBy());
			}
			financeDisbursementDAO.saveList(disbursements, tableType, false);

		}

		// Finance Repay Instruction Details
		if (repayInstructions != null) {
			for (RepayInstruction repayInst : repayInstructions) {
				repayInst.setFinID(finID);
				repayInst.setFinReference(finReference);
				repayInst.setLogKey(logKey);
			}
			repayInstructionDAO.saveList(repayInstructions, tableType, false);
		}

	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from finReceiptHeaderDAO.getErrorDetail with Error ID and
	 * language as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		FinReceiptData repayData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		if (StringUtils.equals(repayData.getReceiptHeader().getRoleCode(), FinanceConstants.CLOSURE_MAKER)) {
			doPostHookValidation(auditHeader);
		}
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);

		FinanceDetail fd = repayData.getFinanceDetail();
		FinReceiptHeader rch = repayData.getReceiptHeader();
		// String usrLanguage = auditHeader.getUsrLanguage();
		// Need to check with kranthi
		String usrLanguage = rch.getUserDetails().getLanguage();
		// Extended field details Validation
		if (fd.getExtendedFieldRender() != null) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("ExtendedFieldDetails");
			ExtendedFieldHeader extHeader = fd.getExtendedFieldHeader();
			details = extendedFieldDetailsService.validateExtendedDdetails(extHeader, details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		if (fd.getExtendedFieldExtension() != null) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("ExtendedFieldExtension");
			details = extendedFieldExtensionService.vaildateDetails(details, usrLanguage);
			auditDetails.addAll(details);
		}

		// Dedup Check
		if (!RepayConstants.PAYSTATUS_BOUNCE.equals(repayData.getReceiptHeader().getReceiptModeStatus())
				&& !RepayConstants.PAYSTATUS_CANCEL.equals(repayData.getReceiptHeader().getReceiptModeStatus())
				&& rch.isDedupCheckRequired()) {
			FinServiceInstruction fsi = new FinServiceInstruction();
			FinReceiptDetail finReceiptDetail = rch.getReceiptDetails().get(0);
			fsi.setFinID(rch.getFinID());
			fsi.setFinReference(rch.getReference());
			fsi.setValueDate(rch.getValueDate());
			fsi.setAmount(rch.getReceiptAmount());
			fsi.setTransactionRef(finReceiptDetail.getTransactionRef());
			fsi.setFavourNumber(finReceiptDetail.getFavourNumber());
			fsi.setPaymentMode(rch.getReceiptMode());
			List<ErrorDetail> errors = dedupCheck(fsi);
			if (CollectionUtils.isNotEmpty(errors)) {
				auditHeader.setErrorDetails(errors.get(0));
			}
		}

		// check if fee waivers in progress
		List<ErrorDetail> errors = checkFeeWaiverInProcess(rch.getFinID(), rch.getReference());
		if (CollectionUtils.isNotEmpty(errors)) {
			auditHeader.setErrorDetails(errors.get(0));
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinReceiptData repayData = (FinReceiptData) auditDetail.getModelData();
		FinReceiptHeader finReceiptHeader = repayData.getReceiptHeader();
		FinReceiptData receiptData = new FinReceiptData();

		receiptData = getFinReceiptDataById(finReceiptHeader.getReference(), AccountingEvent.EARLYPAY,
				FinServiceEvent.RECEIPT, "");
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

		if (finReceiptHeader.isNewRecord()) { // for New record or new record into
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
		if (FinServiceEvent.EARLYSETTLE.equals(finReceiptHeader.getReceiptPurpose())) {
			boolean isPending = isReceiptsPending(finReceiptHeader.getFinID(), finReceiptHeader.getReceiptID());
			if (isPending) {
				valueParm[0] = "Not allowed to do Early Settlement due to previous Presentments/Receipts are in process";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("EXT001", valueParm)));
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

		if (ImplementationConstants.VALIDATION_ON_CHECKER_APPROVER_ALLOWED) {
			doCheckerApproverValidation(auditDetail, usrLanguage, repayData, finReceiptHeader);
		}

		String recordStatus = finReceiptHeader.getRecordStatus();
		String receiptModeSts = finReceiptHeader.getReceiptModeStatus();
		if (!RepayConstants.PAYSTATUS_BOUNCE.equals(receiptModeSts)
				&& !RepayConstants.PAYSTATUS_CANCEL.equals(receiptModeSts)) {
			if ((PennantConstants.RCD_STATUS_SAVED.equals(recordStatus)
					|| PennantConstants.RCD_STATUS_SUBMITTED.equals(recordStatus)
					|| PennantConstants.RCD_STATUS_APPROVED.equals(recordStatus))) {

				String[] parms = new String[2];
				if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOWED_BACKDATED_RECEIPT)) {
					Date prvSchdDate = finPftDetail.getPrvRpySchDate();
					if (finReceiptHeader.getValueDate().compareTo(prvSchdDate) < 0) {
						parms[0] = DateUtil.formatToLongDate(finReceiptHeader.getValueDate());
						parms[1] = DateUtil.formatToLongDate(prvSchdDate);
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("RU0012", parms)));
					}
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
	 * Method to validate if schedule date is falling between maker and checker date for receipts.
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param repayData
	 * @param frh
	 */
	private void doCheckerApproverValidation(AuditDetail auditDetail, String usrLanguage, FinReceiptData repayData,
			FinReceiptHeader frh) {

		String recordStatus = frh.getRecordStatus();
		if (!(PennantConstants.RCD_STATUS_SAVED.equals(recordStatus)
				|| PennantConstants.RCD_STATUS_SUBMITTED.equals(recordStatus)
				|| PennantConstants.RCD_STATUS_APPROVED.equals(recordStatus))) {
			return;
		}

		String receiptPurpose = frh.getReceiptPurpose();
		String roleCode = frh.getRoleCode();
		if (!((FinServiceEvent.EARLYRPY.equals(receiptPurpose) || FinServiceEvent.EARLYSETTLE.equals(receiptPurpose))
				|| (FinServiceEvent.SCHDRPY.equals(receiptPurpose) && (FinanceConstants.KNOCKOFF_MAKER.equals(roleCode)
						|| FinanceConstants.KNOCKOFF_APPROVER.equals(roleCode))))) {
			return;
		}

		if (frh != null) {
			Date receiptDate = frh.getValueDate();
			if (ObjectUtils.isEmpty(receiptDate)) {
				return;
			}

			Date appDate = SysParamUtil.getAppDate();
			List<Date> schDates = financeScheduleDetailDAO.getScheduleDates(repayData.getFinID(), appDate);

			for (Date schDate : schDates) {
				if (schDate.after(receiptDate) && schDate.before(appDate)) {
					String[] value = new String[3];
					value[0] = DateUtility.formatToLongDate(schDate);
					value[1] = DateUtility.formatToLongDate(receiptDate);
					value[2] = DateUtility.formatToLongDate(appDate);

					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30550", value), usrLanguage));
					break;
				}
			}
		}
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
	public boolean isInSubVention(FinanceMain fm, Date receivedDate) {
		boolean isInSubVention = false;
		Date grcPeriodEndDate = fm.getGrcPeriodEndDate();
		Date toDate = null;
		long finID = fm.getFinID();

		SubventionDetail subDetail = subventionDetailDAO.getSubventionDetail(finID, "");
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
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

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
			auditDetailMap.put("ExtendedFieldDetails",
					extendedFieldDetailsService.setExtendedFieldsAuditData(financeDetail.getExtendedFieldHeader(),
							financeDetail.getExtendedFieldRender(), auditTranType, method,
							ExtendedFieldConstants.MODULE_LOAN));
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldDetails"));
		}

		// Check List Details
		if (!CollectionUtils.isEmpty(financeDetail.getFinanceCheckList())) {
			auditDetails.addAll(
					checkListDetailService.getAuditDetail(auditDetailMap, financeDetail, auditTranType, method));
		}

		ExtendedFieldExtension extendedFieldExtension = financeDetail.getExtendedFieldExtension();
		if (extendedFieldExtension != null) {
			auditDetailMap.put("ExtendedFieldExtension", extendedFieldExtensionService
					.setExtendedFieldExtAuditData(extendedFieldExtension, auditTranType, method));
			financeDetail.setAuditDetailMap(auditDetailMap);
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldExtension"));
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
		logger.debug(Literal.ENTERING);

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

		logger.debug(Literal.LEAVING);
		return finReceiptData;
	}

	@Override
	public List<FinODDetails> getValueDatePenalties(FinScheduleData schdData, BigDecimal receiptAmount, Date valueDate,
			List<FinanceRepayments> finRepayments, boolean resetReq) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = schdData.getFinanceMain();
		List<FinODDetails> overdueList = finODDetailsDAO.getFinODBalByFinRef(fm.getFinID());

		calculateODDetails(schdData, overdueList, receiptAmount, valueDate, finRepayments, resetReq);
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

		FinanceMain fm = finScheduleData.getFinanceMain();

		long finID = fm.getFinID();

		List<FinODDetails> overdueList = finODDetailsDAO.getFinODBalByFinRef(finID);
		if (overdueList == null || overdueList.isEmpty()) {
			logger.debug(Literal.LEAVING);
			return overdueList;
		}

		List<FinanceScheduleDetail> schdList = finScheduleData.getFinanceScheduleDetails();
		List<FinanceRepayments> repayments = financeRepaymentsDAO.getFinRepayListByFinRef(finID, false, "");

		// recreate the od as per allocated.
		for (FinODDetails fod : overdueList) {
			BigDecimal penalty = getPenaltyPaid(fod.getFinODSchdDate(), receiptData);
			fod.setTotPenaltyPaid(fod.getTotPenaltyPaid().subtract(penalty));
			fod.setTotPenaltyBal(fod.getTotPenaltyAmt().subtract(fod.getTotWaived()).subtract(fod.getTotPenaltyPaid()));
		}

		latePayMarkingService.calPDOnBackDatePayment(fm, overdueList, valueDate, schdList, repayments, true, true);

		logger.debug(Literal.LEAVING);
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

	public List<FinODDetails> calculateODDetails(FinScheduleData schdData, List<FinODDetails> overdueList,
			BigDecimal receiptAmount, Date valueDate, List<FinanceRepayments> finRepayments, boolean resetReq) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = schdData.getFinanceMain();
		if (CollectionUtils.isEmpty(overdueList)) {
			logger.debug(Literal.LEAVING);
			return overdueList;
		}

		List<FinanceRepayments> repayments = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(finRepayments)) {
			repayments.addAll(finRepayments);
		} else {
			repayments.addAll(financeRepaymentsDAO.getFinRepayListByFinRef(fm.getFinID(), false, ""));
		}

		if (receiptAmount.compareTo(BigDecimal.ZERO) > 0) {
			repayments.addAll(receiptCalculator.getRepayListByHierarchy(schdData, receiptAmount, valueDate));
		}

		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();

		latePayMarkingService.calPDOnBackDatePayment(fm, overdueList, valueDate, schedules, repayments, resetReq, true);

		logger.debug(Literal.LEAVING);
		return overdueList;
	}

	@Override
	public ErrorDetail doInstrumentValidation(FinReceiptData rd) {
		logger.debug(Literal.ENTERING);
		FinanceDetail fd = rd.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		FinReceiptHeader rch = rd.getReceiptHeader();

		String payMode = rch.getReceiptMode();

		BigDecimal amount = rch.getReceiptAmount().subtract(rd.getExcessAvailable());
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			return null;
		}

		String subReceiptMode = rch.getSubReceiptMode();
		if (RepayConstants.RECEIPTMODE_ONLINE.equals(rch.getReceiptMode())) {
			payMode = subReceiptMode;
		}

		InstrumentwiseLimit instLimit = instrumentwiseLimitDAO.getInstrumentWiseModeLimit(payMode, "");

		if (instLimit == null) {
			return null;
		}

		Date appDate = SysParamUtil.getAppDate();
		long custID = fm.getCustID();

		BigDecimal perDayAmt = finReceiptDetailDAO.getReceiptAmountPerDay(appDate, payMode, custID);
		BigDecimal minReceiptTranLimit = instLimit.getReceiptMinAmtperTran();
		BigDecimal maxReceiptTranLimit = instLimit.getReceiptMaxAmtperTran();
		BigDecimal MaxReceiptAmtPerDay = instLimit.getReceiptMaxAmtperDay();

		if (amount.compareTo(minReceiptTranLimit) < 0 || amount.compareTo(maxReceiptTranLimit) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = PennantApplicationUtil.amountFormate(maxReceiptTranLimit, PennantConstants.defaultCCYDecPos);
			valueParm[1] = PennantApplicationUtil.amountFormate(minReceiptTranLimit, PennantConstants.defaultCCYDecPos);
			logger.debug(Literal.ENTERING);
			return ErrorUtil.getErrorDetail(new ErrorDetail("RU0024", "", valueParm));
		}

		BigDecimal dayAmt = BigDecimal.ZERO;

		if (rd.isEnquiry()) {
			dayAmt = perDayAmt.add(amount);
		} else {
			dayAmt = perDayAmt;
		}

		if (dayAmt.compareTo(MaxReceiptAmtPerDay) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = PennantApplicationUtil.amountFormate(maxReceiptTranLimit, PennantConstants.defaultCCYDecPos);
			valueParm[1] = StringUtils.isEmpty(subReceiptMode) ? subReceiptMode : rch.getReceiptMode();
			logger.debug(Literal.ENTERING);
			return ErrorUtil.getErrorDetail(new ErrorDetail("IWL0004", valueParm));
		}

		logger.debug(Literal.ENTERING);
		return null;
	}

	@Override
	public FinReceiptData doReceiptValidations(FinanceDetail fd, String method) {
		logger.debug(Literal.ENTERING);
		FinReceiptData receiptData = new FinReceiptData();
		receiptData.setFinanceDetail(fd);
		FinScheduleData schdData = fd.getFinScheduleData();
		FinServiceInstruction fsi = toUpperCase(schdData.getFinServiceInstruction());
		String finReference = fsi.getFinReference();
		String receiptPurpose = fsi.getReceiptPurpose();
		String parm1 = null;
		String eventCode = null;
		BigDecimal amount = new BigDecimal(
				PennantApplicationUtil.amountFormate(fsi.getAmount(), 2).replaceAll(",", ""));

		int methodCtg = receiptCalculator.setReceiptCategory(method);
		if (methodCtg == 0) {
			eventCode = AccountingEvent.REPAY;
		} else if (methodCtg == 1) {
			eventCode = AccountingEvent.EARLYPAY;
		} else if (methodCtg == 2) {
			eventCode = AccountingEvent.EARLYSTL;
		} else if (methodCtg == 4) {
			eventCode = AccountingEvent.RESTRUCTURE;
		}

		if (StringUtils.isBlank(finReference)) {
			setErrorToFSD(schdData, "90502", "Loan Reference");
			return receiptData;
		}

		Long finID = financeMainDAO.getFinID(finReference, TableType.MAIN_TAB);

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
			if (fsi.getReceiptChannel() == null) {
				fsi.setReceiptChannel("OTC");
			}

		} else if (RepayConstants.RECEIPTMODE_RESTRUCT.equals(receiptMode)) {
			fsi.setPaymentMode(RepayConstants.RECEIPTMODE_RESTRUCT);
			fsi.setSubReceiptMode(RepayConstants.RECEIPTMODE_RESTRUCT);
		}

		if (StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_MOBILE)) {
			fsi.setPaymentMode(RepayConstants.RECEIPTMODE_MOBILE);
		}
		// }

		// set Default date formats
		setDefaultDateFormats(fsi);
		// closed loan
		if (StringUtils.equals(receiptPurpose, FinServiceEvent.EARLYRPY)
				|| StringUtils.equals(receiptPurpose, FinServiceEvent.EARLYSETTLE)) {

			boolean finactive = financeMainDAO.isFinActive(finID);

			if (finID != null) {
				finactive = true;
			}

			if (!finactive) {
				schdData = setErrorToFSD(schdData, "RU0049", null);
				return receiptData;
			}
		}

		if (StringUtils.equals(receiptPurpose, FinServiceEvent.EARLYSETTLE)) {
			if (fsi.getEarlySettlementReason() != null && fsi.getEarlySettlementReason() > 0) {
				ReasonCode reasonCode = reasonCodeDAO.getReasonCode(fsi.getEarlySettlementReason(), "_AView");
				if (reasonCode == null) {
					schdData = setErrorToFSD(schdData, "90501", "earlySettlementReason");
					return receiptData;
				} else {
					if (!StringUtils.endsWithIgnoreCase(reasonCode.getReasonTypeCode(),
							PennantConstants.REASON_CODE_EARLYSETTLEMENT)) {
						schdData = setErrorToFSD(schdData, "90501", "earlySettlementReason");
						return receiptData;
					}
				}
			}
		}
		if (StringUtils.equals(receiptPurpose, FinServiceEvent.EARLYSETTLE)
				|| StringUtils.equals(receiptPurpose, FinServiceEvent.EARLYRPY)) {

			boolean initiated = isEarlySettlementInitiated(StringUtils.trimToEmpty(finReference));
			if (initiated) {
				schdData = setErrorToFSD(schdData, "90498", null);
				return receiptData;
			}
		}

		if (StringUtils.equals(receiptPurpose, FinServiceEvent.EARLYRPY)
				|| StringUtils.equals(receiptPurpose, FinServiceEvent.EARLYSETTLE)) {

			boolean initiated = isPartialSettlementInitiated(StringUtils.trimToEmpty(finReference));
			if (initiated) {
				setErrorToFSD(schdData, "90499", null);
				return receiptData;
			}

		}

		// Do First level Validation for Upload
		receiptData = doBasicValidations(receiptData, methodCtg);

		if (schdData.getErrorDetails() != null && !schdData.getErrorDetails().isEmpty()) {
			logger.debug("Leaving - Basic Validations Error");
			return receiptData;
		}

		int count = financeMainDAO.getCountByBlockedFinances(finID);
		if (count > 0) {
			parm1 = "FinReference: " + finReference;
			schdData = fd.getFinScheduleData();
			schdData = setErrorToFSD(schdData, "90204", receiptPurpose, parm1);
			fd.setFinScheduleData(schdData);
			return receiptData;
		}

		// RECEIPT UPLOAD INQUIRY or API/Receipt Upload Post
		if (fsi.isReceiptUpload() && !StringUtils.equals(fsi.getReqType(), "Post")) {
			FinanceMain financeMain = financeMainDAO.getFinanceMainById(finID, "_AView", false);
			schdData.setFinanceMain(financeMain);
		} else {
			Cloner cloner = new Cloner();
			FinServiceInstruction tempFsi = cloner.deepClone(schdData.getFinServiceInstruction());
			FinReceiptHeader rch = cloner.deepClone(receiptData.getReceiptHeader());
			receiptData = getFinReceiptDataById(finReference, eventCode, FinServiceEvent.RECEIPT, "");

			if (schdData.getErrorDetails() != null && !schdData.getErrorDetails().isEmpty()) {
				logger.debug(Literal.LEAVING);
				return receiptData;
			}
			fd = receiptData.getFinanceDetail();
			receiptData.setReceiptHeader(rch);
			schdData = fd.getFinScheduleData();
			schdData.setFinServiceInstruction(tempFsi);
		}
		Date valueDate = fsi.getValueDate();
		if (fsi.getReceiptPurpose().equals(FinServiceEvent.EARLYSETTLE) && fsi.isReceiptUpload()
				&& !StringUtils.equals(fsi.getReqType(), "Post")) {
			FinReceiptDetail rcd = fsi.getReceiptDetail();
			FinScheduleData fsd = fd.getFinScheduleData();
			FinanceMain fm = fsd.getFinanceMain();

			if (!fm.isFinIsActive()) {
				fsi.setExcessAdjustTo(RepayConstants.EXAMOUNTTYPE_EXCESS);
			}

			receiptData = getFinReceiptDataById(finReference, eventCode, FinServiceEvent.RECEIPT, "");
			FinReceiptHeader rch = receiptData.getReceiptHeader();

			rch.setFinID(receiptData.getFinID());
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

			try {
				if (!receiptData.getFinanceDetail().getFinScheduleData().getFinanceType().isAlwCloBefDUe()) {
					Date firstInstDate = getFirstInstDate(
							receiptData.getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails());
					if (firstInstDate != null && valueDate.compareTo(firstInstDate) < 0) {
						fsd = setErrorToFSD(fsd, "21005",
								"Not allowed to do Early Settlement before first installment");
						receiptData.getFinanceDetail().setFinScheduleData(fsd);
						return receiptData;
					}
				}
			} catch (NullPointerException e) {
				logger.error(Literal.EXCEPTION, e);
			}

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
				if (fsi.getReceiptPurpose().equals(FinServiceEvent.EARLYSETTLE) && fsi.isReceiptUpload()) {
					if (totalDues.compareTo(amount) > 0) {
						schdData = setErrorToFSD(schdData, "RU0051", null);
						receiptData.getFinanceDetail().setFinScheduleData(schdData);
						return receiptData;
					}

					try {
						if (!receiptData.getFinanceDetail().getFinScheduleData().getFinanceType().isAlwCloBefDUe()) {
							Date firstInstDate = getFirstInstDate(
									receiptData.getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails());
							if (firstInstDate != null && fsi.getValueDate().compareTo(firstInstDate) < 0) {
								schdData = setErrorToFSD(schdData, "21005",
										"Not allowed to do Early Settlement before first installment");
								receiptData.getFinanceDetail().setFinScheduleData(schdData);
								return receiptData;
							}
						}
					} catch (NullPointerException e) {
						logger.error(Literal.EXCEPTION, e);
					}
				}
			}
			receiptData.setFinanceDetail(fd);
		}
		fsi.setValueDate(valueDate);

		receiptData = doDataValidations(receiptData, methodCtg);
		if (schdData.getErrorDetails() != null && !schdData.getErrorDetails().isEmpty()) {
			logger.debug("Leaving - Data Validations Error");
			return receiptData;
		}
		// receiptData = doBusinessValidations(receiptData, methodCtg);
		if (schdData.getErrorDetails() != null && !schdData.getErrorDetails().isEmpty()) {
			logger.debug("Leaving - Business Validations Error");
			return receiptData;
		}

		// Dedup Check
		schdData.setErrorDetails(dedupCheck(fsi));
		if (CollectionUtils.isNotEmpty(schdData.getErrorDetails())) {
			logger.info("Dedup Validation Failed.");
			return receiptData;
		}

		if (fsi.isReceiptUpload() && !StringUtils.equals(fsi.getReqType(), "Post")) {
			logger.debug(Literal.LEAVING);
			return receiptData;
		}

		receiptData = doFunctionalValidations(receiptData, methodCtg);
		if (schdData.getErrorDetails() != null && !schdData.getErrorDetails().isEmpty()) {
			logger.debug("Leaving - Functional Validations Error");
			return receiptData;
		}

		logger.debug(Literal.LEAVING);
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

		if (receiptMode.equals(RepayConstants.RECEIPTMODE_RESTRUCT)) {
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
		if ((!RepayConstants.RECEIPTMODE_CASH.equals(receiptMode)
				&& !RepayConstants.RECEIPTMODE_CHEQUE.equals(receiptMode))
				&& !RepayConstants.RECEIPTMODE_DD.equals(receiptMode)
				&& !RepayConstants.RECEIPTMODE_RESTRUCT.equals(receiptMode) && rcd.getFundingAc() <= 0) {
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
			Date waiverDate = getLastWaiverDate(financeDetail.getFinID(), appDate, fsi.getValueDate());
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
				&& FinanceConstants.PRODUCT_CD.equals(financeMain.getFinCategory())
				&& (RepayConstants.RECEIPTMODE_PAYABLE.equals(fsi.getPaymentMode()))) {
			autoReceipt = true;
		}
		// Partner Bank Validation against Loan Type, If not exists
		long fundingAccount = fsi.getReceiptDetail().getFundingAc();
		fsi.setFundingAc(fundingAccount);
		String receiptMode = fsi.getPaymentMode();

		if (RepayConstants.RECEIPTMODE_ONLINE.equals(receiptMode)) {
			receiptMode = fsi.getSubReceiptMode();
		}
		if (!RepayConstants.RECEIPTMODE_CASH.equals(receiptMode)
				&& !RepayConstants.RECEIPTMODE_CHEQUE.equals(receiptMode)
				&& !RepayConstants.RECEIPTMODE_DD.equals(receiptMode)
				&& !RepayConstants.RECEIPTMODE_RESTRUCT.equals(receiptMode)
				|| (ImplementationConstants.ALLOW_PARTNERBANK_FOR_RECEIPTS_IN_CASHMODE
						&& RepayConstants.RECEIPTMODE_CASH.equals(receiptMode))) {
			int count = finTypePartnerBankDAO.getPartnerBankCount(financeMain.getFinType(), receiptMode,
					AccountConstants.PARTNERSBANK_RECEIPTS, fundingAccount);
			if (count <= 0 && !autoReceipt) {
				finScheduleData = setErrorToFSD(finScheduleData, "RU0020", null);
				return receiptData;
			}
		}
		Date finStartDate = financeMain.getFinStartDate();

		boolean receiptUpload = fsi.isReceiptUpload();
		if (receiptUpload && fsi.getValueDate().compareTo(finStartDate) < 0) {
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

		int formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());

		if (receiptUpload && SysParamUtil.isAllowed(SMTParameterConstants.RECEIPT_CASH_PAN_MANDATORY)) {
			BigDecimal recAmount = PennantApplicationUtil.formateAmount(fsi.getAmount(), formatter);
			BigDecimal cashLimit = new BigDecimal(
					SysParamUtil.getSystemParameterObject("RECEIPT_CASH_PAN_LIMIT").getSysParmValue());

			String panNumber = fsi.getPanNumber();
			if (StringUtils.isEmpty(panNumber)) {
				if (recAmount.compareTo(cashLimit) > 0
						&& StringUtils.equals(fsi.getPaymentMode(), DisbursementConstants.PAYMENT_TYPE_CASH)) {
					String valueParm = "PanNumber";
					finScheduleData = setErrorToFSD(finScheduleData, "30561", valueParm);
					return receiptData;
				}
			} else {
				Pattern pattern = Pattern
						.compile(PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_PANNUMBER));
				Matcher matcher = pattern.matcher(panNumber);
				if (!matcher.matches()) {
					finScheduleData = setErrorToFSD(finScheduleData, "90251", panNumber);
					return receiptData;
				}
			}
		}

		if (finScheduleData.getFinanceMain() != null) {
			if (fsi.getTdsAmount().compareTo(BigDecimal.ZERO) > 0
					&& !PennantConstants.TDS_MANUAL.equalsIgnoreCase(finScheduleData.getFinanceMain().getTdsType())) {
				finScheduleData = setErrorToFSD(finScheduleData, "RU00060", null);
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

		Date appDate = SysParamUtil.getAppDate();
		String allocationType = fsi.getAllocationType();
		String excessAdjustTo = fsi.getExcessAdjustTo();
		String parm0 = null;
		String parm1 = null;

		receiptData = validateRecalType(receiptData, methodCtg);
		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			logger.debug(Literal.LEAVING);
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
					ExtendedFieldConstants.MODULE_LOAN, subModule, FinServiceEvent.RECEIPT);
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

		logger.debug(Literal.LEAVING);
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

		logger.debug(Literal.LEAVING);
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
		String receiptPurpose = fsi.getReceiptPurpose();

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

		// for Manual allocationType for earlySettelment && Below condition added for Receipt Upload
		if (RepayConstants.ALLOCATIONTYPE_MANUAL.equals(allocationType) && isAllocationFound
				&& FinanceConstants.EARLYSETTLEMENT.equals(receiptPurpose)) {
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
			// rch.setValueDate(rcd.getReceivedDate());
			rch.setValueDate(rcd.getValueDate());
		}

		long finID = fsi.getFinID();
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
		if (methodCtg != 0 && financeMain.getMaturityDate().compareTo(appDate) < 0 && !fsi.isNormalLoanClosure()
				&& !receiptData.isForeClosure()) {
			if (methodCtg == 1) {
				parm0 = FinServiceEvent.EARLYRPY;
			} else {
				parm0 = FinServiceEvent.EARLYSETTLE;
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
			if (fsi.getValueDate().compareTo(DateUtil.addDays(DateUtil.getMonthStart(appDate), -1)) < 0) {
				parm0 = DateUtil.formatToLongDate(fsi.getValueDate());
				parm1 = DateUtil.formatToLongDate(DateUtil.addDays(DateUtil.getMonthStart(appDate), -1));
				// parm2 = String.valueOf(paramDays);
				finScheduleData = setErrorToFSD(finScheduleData, "RU0010", parm0, parm1);
				return receiptData;
			}
		}

		if (methodCtg >= 1 && methodCtg != 4) {
			// last schedule change date
			Date lastServDate = finLogEntryDetailDAO.getMaxPostDate(finID);
			if (lastServDate != null && fsi.getValueDate().compareTo(lastServDate) < 0) {
				parm0 = DateUtil.formatToLongDate(fsi.getValueDate());
				parm1 = DateUtil.formatToLongDate(lastServDate);
				finScheduleData = setErrorToFSD(finScheduleData, "RU0013", parm0, parm1);
				return receiptData;
			}

			String receiptModeSts = rch.getReceiptModeStatus();
			if (!RepayConstants.PAYSTATUS_BOUNCE.equals(receiptModeSts)
					&& !RepayConstants.PAYSTATUS_CANCEL.equals(receiptModeSts)) {
				Date prvSchdDate = finPftDetail.getPrvRpySchDate();
				if (fsi.getValueDate().compareTo(prvSchdDate) < 0) {
					parm0 = DateUtil.formatToLongDate(fsi.getValueDate());
					parm1 = DateUtil.formatToLongDate(prvSchdDate);
					finScheduleData = setErrorToFSD(finScheduleData, "RU0012", parm0, parm1);
					return receiptData;
				}
			}

			// Early Settlement OR Early Settlement Inquiry
			Date lastReceivedDate = null;
			if (methodCtg == 2 || methodCtg == 3) {
				lastReceivedDate = getMaxReceiptDate(fsi.getFinReference());
			} else if (methodCtg == 1) {
				lastReceivedDate = finReceiptDetailDAO.getMaxReceiptDate(fsi.getFinReference(),
						FinServiceEvent.EARLYRPY, TableType.VIEW);
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

		if (methodCtg == 4) {
			List<ValueLabel> recalMethods = PennantStaticListUtil.getRecalTypeList();
			List<String> methodsList = new ArrayList<>();
			boolean found = false;
			for (ValueLabel valueLabel : recalMethods) {
				methodsList.add(valueLabel.getValue());
				if (StringUtils.equals(recalType, valueLabel.getValue())) {
					found = true;
					break;
				}
			}

			if (!found) {
				finScheduleData = setErrorToFSD(finScheduleData, "90281", "Recal type code", methodsList.toString());
				return receiptData;
			} else {
				return receiptData;
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
		FinanceMain fm = finScheduleData.getFinanceMain();
		String closingStaus = fm.getClosingStatus();
		if (StringUtils.isEmpty(closingStaus) || fm.isWriteoffLoan()) {
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
		FinanceDetail fd = receiptData.getFinanceDetail();
		String receiptPurpose = fd.getFinScheduleData().getFinServiceInstruction().getReceiptPurpose();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinServiceInstruction fsi = schdData.getFinServiceInstruction();

		FinReceiptHeader rch = receiptData.getReceiptHeader();
		FinReceiptDetail rcd = rch.getReceiptDetails().get(0);

		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		receiptData.setFinID(finID);
		receiptData.setFinReference(finReference);

		LoggedInUser userDetails = null;
		if (SessionUserDetails.getLogiedInUser() != null) {
			userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		} else {
			userDetails = fsi.getLoggedInUser();
		}
		receiptData.setUserDetails(userDetails);

		fm.setUserDetails(userDetails);
		fd.setUserDetails(userDetails);

		Date appDate = SysParamUtil.getAppDate();
		receiptData.setBuildProcess("I");

		rch.setFinID(finID);
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
		rch.setExtReference(fsi.getExternalReference());
		rch.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		rch.setNewRecord(true);
		rch.setLastMntBy(userDetails.getUserId());
		rch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		rch.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		rch.setUserDetails(userDetails);
		rch.setTdsAmount(fsi.getTdsAmount());

		if (FinServiceEvent.SCHDRPY.equals(fsi.getReceiptPurpose()) && fsi.isBckdtdWthOldDues()) {
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
			rch.setPanNumber(fsi.getPanNumber());
			rch.setExtReference(fsi.getExternalReference());
			rch.setReceivedDate(fsi.getReceivedDate());
		} else {
			rcd.setValueDate(rcd.getReceivedDate());
			rch.setReceiptDate(SysParamUtil.getAppDate());
		}

		if (rch.getReceiptMode() != null && (rch.getSubReceiptMode() == null || rch.getSubReceiptMode().isEmpty())) {
			rch.setSubReceiptMode(rch.getReceiptMode());
		}

		if (fsi.getReceiptPurpose().equals(FinServiceEvent.EARLYSETTLE) && fsi.isReceiptUpload()
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
		// fetch partner bank details
		if (rcd.getFundingAc() > 0) {
			PartnerBank partnerBank = partnerBankDAO.getPartnerBankById(rcd.getFundingAc(), "");
			if (partnerBank != null) {
				rcd.setPartnerBankAc(partnerBank.getAccountNo());
				rcd.setPartnerBankAcType(partnerBank.getAcType());
			}
		}

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
			rch.setPanNumber(fsi.getPanNumber());
			rch.setExtReference(fsi.getExternalReference());
			rch.setReceivedDate(fsi.getReceivedDate());
		}

		int moduleID = FinanceConstants.MODULEID_FINTYPE;

		if (StringUtils.isNotBlank(fm.getPromotionCode())) {
			moduleID = FinanceConstants.MODULEID_PROMOTION;
		}

		schdData.getFinanceMain().setReceiptPurpose(receiptPurpose);
		String event = null;
		switch (receiptPurpose) {
		case FinServiceEvent.SCHDRPY:
			event = AccountingEvent.REPAY;
			break;
		case FinServiceEvent.EARLYRPY:
			event = AccountingEvent.EARLYPAY;
			break;
		case FinServiceEvent.EARLYSETTLE:
			event = AccountingEvent.EARLYSTL;
			/*
			 * finScheduleData = ScheduleCalculator.recalEarlyPaySchedule(finScheduleData, rcd.getReceivedDate(), null,
			 * receiptData.getFinanceDetail().getFinScheduleData(). getFinPftDeatil().getTotalPriBal(),
			 * CalculationConstants.EARLYPAY_ADJMUR);
			 */
			break;
		case FinServiceEvent.EARLYSTLENQ:
			event = AccountingEvent.EARLYSTL;
			break;
		case FinServiceEvent.RESTRUCTURE:
			event = AccountingEvent.RESTRUCTURE;
			break;
		default:
			break;
		}

		// Finance Type Fee details based on Selected Receipt Purpose Event
		List<FinTypeFees> finTypeFeesList = finTypeFeesDAO.getFinTypeFeesList(fm.getFinType(), event, "_AView", false,
				moduleID);
		receiptData.getFinanceDetail().setFinTypeFeesList(finTypeFeesList);
		FinServiceInstruction service = schdData.getFinServiceInstruction();
		rch.setExcessAmounts(finExcessAmountDAO.getExcessAmountsByRef(rch.getFinID()));
		calcuateDues(receiptData);
		receiptData.getFinanceDetail().getFinScheduleData().setFinServiceInstruction(service);

		String allocateMthd = receiptData.getReceiptHeader().getAllocationType();
		if (StringUtils.equals(allocateMthd, RepayConstants.ALLOCATIONTYPE_AUTO)) {
			receiptData = receiptCalculator.recalAutoAllocation(receiptData, false);
		}
		if (StringUtils.equals(receiptPurpose, FinServiceEvent.EARLYRPY)) {
			if (receiptData.getReceiptHeader().getPartPayAmount().compareTo(BigDecimal.ZERO) <= 0) {
				schdData = fd.getFinScheduleData();
				schdData = setErrorToFSD(schdData, "90332", receiptPurpose, "");
				fd.setFinScheduleData(schdData);
				return receiptData;
			}
			BigDecimal closingBal = getClosingBalance(rch.getFinID(), rch.getValueDate());
			BigDecimal diff = closingBal.subtract(receiptData.getReceiptHeader().getPartPayAmount());
			if (diff.compareTo(new BigDecimal(100)) < 0) {
				schdData = setErrorToFSD(schdData, "91127", String.valueOf(closingBal));
				fd.setFinScheduleData(schdData);
				return receiptData;
			}
		}

		if (StringUtils.equals(receiptPurpose, FinServiceEvent.EARLYSETTLE)) {
			boolean initiated = isEarlySettlementInitiated(StringUtils.trimToEmpty(schdData.getFinReference()));
			if (initiated) {
				schdData = fd.getFinScheduleData();
				schdData = setErrorToFSD(schdData, "90332", receiptPurpose, "");
				fd.setFinScheduleData(schdData);
			}
		}

		if (fsi.isReceiptUpload()) {
			fsi.setFinFeeDetails(receiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList());
		}

		if (!StringUtils.equals(allocateMthd, RepayConstants.ALLOCATIONTYPE_AUTO)) {
			if (StringUtils.equals(PennantConstants.FINSOURCE_ID_API, receiptData.getSourceId())
					&& StringUtils.equals(allocateMthd, RepayConstants.ALLOCATIONTYPE_MANUAL)
					&& StringUtils.equals(FinServiceEvent.EARLYSETTLE, receiptPurpose)) {
				receiptData = validateAllocationsAmount(receiptData);
			}
			receiptData = updateAllocationsPaid(receiptData);
		}
		if (StringUtils.equals(FinanceConstants.PRODUCT_CD, fm.getProductCategory())
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
				// FIXME:Satish.K
				if ("I".equals(ulAlcType)) {
					pftPaid = pftPaid.add(ulAlc.getPaidAmount());
					emiWaivedAmt = emiWaivedAmt.add(ulAlc.getWaivedAmount());
				}
				// FIXME:Satish.K
				if ("P".equals(ulAlcType)) {
					priPaid = priPaid.add(ulAlc.getPaidAmount());
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
			allocationsList.get(pftIdx).setPaidAmount(pftPaid);
			allocationsList.get(pftIdx).setTdsPaid(pftPaid.subtract(npftPaid));
		}

		if (fPftIdx >= 0 && FinServiceEvent.EARLYSETTLE.equals(rch.getReceiptPurpose())) {
			allocationsList.get(fPftIdx).setPaidAmount(fnpftPaid);
			allocationsList.get(fPftIdx).setTotalPaid(fnpftPaid);
			allocationsList.get(fPftIdx).setWaivedAmount(fiWaivedAmt);
		}
		if (emiInx >= 0 && !emiFound && FinServiceEvent.EARLYSETTLE.equals(rch.getReceiptPurpose())) {
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
	public boolean isReceiptsPending(long finID, long receiptId) {
		boolean isPending = finReceiptHeaderDAO.checkInProcessPresentments(finID);
		if (isPending) {
			return true;
		}

		isPending = finReceiptHeaderDAO.checkInProcessReceipts(finID, receiptId);
		return isPending;
	}

	@Override
	public FinanceMain getClosingStatus(long finID, TableType tempTab, boolean wif) {
		return financeMainDAO.getClosingStatus(finID, tempTab, wif);
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
	public long CheckDedupSP(FinReceiptHeader receiptHeader, String method) {
		if (StringUtils.equals(method, FinServiceEvent.SCHDRPY)
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
		fsi.setTdsAmount(rud.getTdsAmount());

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
			fsi.setReceiptPurpose(FinServiceEvent.SCHDRPY);
			rcd.setReceiptPurpose(FinServiceEvent.SCHDRPY);
		} else if (StringUtils.equals(rud.getReceiptPurpose(), "EP")) {
			fsi.setReceiptPurpose(FinServiceEvent.EARLYRPY);
			rcd.setReceiptPurpose(FinServiceEvent.EARLYRPY);
		} else if (StringUtils.equals(rud.getReceiptPurpose(), "ES")) {
			fsi.setReceiptPurpose(FinServiceEvent.EARLYSETTLE);
			rcd.setReceiptPurpose(FinServiceEvent.EARLYSETTLE);
		}
		fsi.setReceiptDetail(rcd);
		return fsi;
	}

	@Override
	public BigDecimal getClosingBalance(long finID, Date valueDate) {
		return financeScheduleDetailDAO.getClosingBalance(finID, valueDate);
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
	public Date getManualAdviseMaxDate(long finID, Date valueDate) {
		return manualAdviseDAO.getManualAdviseDate(finID, valueDate, "", FinanceConstants.MANUAL_ADVISE_RECEIVABLE);
	}

	@Override
	public FinReceiptData getFinReceiptDataByReceiptId(long receiptId, String eventCode, String procEdtEvent,
			String userRole) {

		logger.debug(Literal.ENTERING);

		FinReceiptHeader rch = getFinReceiptHeaderById(receiptId, false, "_View");
		rch.setValueDate(rch.getReceiptDate());

		FinanceMain fm = financeMainDAO.getFinanceMainByRef(rch.getReference(), "_AView", false);

		FinReceiptData rd = new FinReceiptData();
		rd.setFinID(rch.getFinID());
		rd.setFinReference(rch.getReference());
		rd.setReceiptHeader(rch);

		FinanceDetail fd = new FinanceDetail();
		rd.setFinanceDetail(fd);

		FinScheduleData schdData = fd.getFinScheduleData();

		if (fm == null) {
			logger.debug(Literal.LEAVING);
			return rd;
		}

		String finReference = fm.getFinReference();
		long finID = fm.getFinID();
		long custID = fm.getCustID();

		schdData.setFinID(finID);
		schdData.setFinReference(finReference);

		List<FinReceiptDetail> rcdList = null;
		if (SysParamUtil.isAllowed(SMTParameterConstants.CHQ_RECEIPTS_PAID_AT_DEPOSIT_APPROVER)
				&& FinServiceEvent.SCHDRPY.equals(rch.getReceiptPurpose())
				&& (RepayConstants.PAYSTATUS_BOUNCE.equals(rch.getReceiptModeStatus())
						|| RepayConstants.PAYSTATUS_CANCEL.equals(rch.getReceiptModeStatus()))) {
			rcdList = finReceiptDetailDAO.getReceiptHeaderByID(receiptId, "_AView");
		} else {
			rcdList = finReceiptDetailDAO.getReceiptHeaderByID(receiptId, "_View");
		}

		rch.setReceiptDetails(rcdList);

		int size = rcdList.size();

		if (size > 0) {
			FinReceiptDetail rcd = rcdList.get(size - 1);
			rch.setValueDate(rcd.getValueDate());
			// PSD#165780 Setting realization date to Value date irrespective of receipt purpose
			/*
			 * if (finReceiptHeader.getReceiptPurpose().equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE) &&
			 * finReceiptHeader.getRealizationDate() != null) {
			 */
			if (rch.getRealizationDate() != null) {
				rch.setValueDate(rch.getRealizationDate());
			}

			if (ImplementationConstants.ALLOW_SCDREPAY_REALIZEDATE_AS_VALUEDATE) {
				Date appDate = SysParamUtil.getAppDate();
				String receiptMode = rch.getReceiptMode();

				if (FinServiceEvent.SCHDRPY.equals(rch.getReceiptPurpose())
						&& (RepayConstants.RECEIPTMODE_CHEQUE.equals(receiptMode)
								|| RepayConstants.RECEIPTMODE_DD.equals(receiptMode))
						&& rch.getRealizationDate() != null) {

					Date valueDate = rch.getValueDate();
					FinanceScheduleDetail schdule = financeScheduleDetailDAO.getNextUnpaidSchPayment(finID, valueDate);
					if (schdule != null) {
						Date schDate = schdule.getSchDate();
						if (!valueDate.after(schDate)) {
							// Schedule Date should be less than or equal to App date to skip future Installments
							if (schDate.compareTo(appDate) <= 0) {
								rch.setValueDate(schDate);
							}
						}
					}
				}
			}
		}

		for (FinReceiptDetail rcd : rcdList) {
			rcd.setRepayHeader(financeRepaymentsDAO.getFinRepayHeadersByReceipt(rcd.getReceiptSeqID(), ""));
		}

		if (!StringUtils.equals(PennantConstants.RECORD_TYPE_NEW, rch.getRecordType())) {
			rd.setCalReq(false);
		}

		rd = getInProcessReceiptData(rd);

		FinanceMain finMain = financeMainDAO.getEntityNEntityDesc(finID, "_Aview", false);

		if (finMain != null) {
			fm.setEntityCode(finMain.getEntityCode());
			fm.setEntityDesc(finMain.getEntityDesc());
		}

		List<ReceiptAllocationDetail> allocations = allocationDetailDAO.getAllocationsByReceiptID(receiptId, "_View");
		for (ReceiptAllocationDetail rad : allocations) {
			rad.setTotalPaid(rad.getPaidAmount().add(rad.getTdsPaid()));
			rad.setTotRecv(rad.getTotalDue().add(rad.getTdsDue()));

			Long taxHeaderId = rad.getTaxHeaderId();

			if (taxHeaderId != null && taxHeaderId != 0) {
				List<Taxes> taxDetailById = taxHeaderDetailsDAO.getTaxDetailById(taxHeaderId, "_View");
				TaxHeader taxHeader = new TaxHeader(taxHeaderId);
				taxHeader.setTaxDetails(taxDetailById);
			}
		}

		rch.setAllocations(allocations);

		rch.setManualAdvise(manualAdviseDAO.getManualAdviseByReceiptId(receiptId, "_View"));

		schdData.setFinanceMain(fm);

		// Finance Type Details from Table
		FinanceType financeType = financeTypeDAO.getOrgFinanceTypeByID(fm.getFinType(), "_ORGView");

		// Fetching Promotion Details from main view
		if (StringUtils.isNotBlank(fm.getPromotionCode())
				&& (fm.getPromotionSeqId() != null && fm.getPromotionSeqId() == 0)) {
			Promotion promotion = this.promotionDAO.getPromotionByCode(fm.getPromotionCode(), "_AView");
			financeType.setFInTypeFromPromotiion(promotion);
		}

		schdData.setFinanceType(financeType);

		// Step Policy Details List from main view
		if (fm.isStepFinance()) {
			schdData.setStepPolicyDetails(financeStepDetailDAO.getFinStepDetailListByFinRef(finID, "_AView", false));
		}

		fd.setFinTypeFeesList(finTypeFeesDAO.getFinTypeFeesList(fm.getFinType(), eventCode, "_AView", false,
				FinanceConstants.MODULEID_FINTYPE));

		fd.setFinFeeConfigList(finFeeConfigService.getFinFeeConfigList(finID, eventCode, false, "_View"));

		schdData.setFeeEvent(eventCode);

		// Finance Schedule Details from Main table
		schdData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false));

		// Overdraft Details from main table
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, fm.getProductCategory())) {
			schdData.setOverdraftScheduleDetails(
					overdraftScheduleDetailDAO.getOverdraftScheduleDetails(finID, "", false));
		}

		schdData.setDisbursementDetails(financeDisbursementDAO.getFinanceDisbursementDetails(finID, "_AView", false));
		schdData.setRepayInstructions(repayInstructionDAO.getRepayInstructions(finID, "_AView", false));
		schdData.setFinODPenaltyRate(finODPenaltyRateDAO.getFinODPenaltyRateByRef(finID, "_AView"));
		schdData.setFinPftDeatil(profitDetailsDAO.getFinProfitDetailsById(finID));

		if (custID != 0 && custID != Long.MIN_VALUE) {
			fd.setCustomerDetails(customerDetailsService.getCustomerDetailsById(custID, true, "_AView"));
		}

		// Finance Stage Accounting Posting Details
		// =======================================
		rd.getFinanceDetail().setStageTransactionEntries(transactionEntryDAO.getListTransactionEntryByRefType(
				financeType.getFinType(), StringUtils.isEmpty(procEdtEvent) ? FinServiceEvent.ORG : procEdtEvent,
				FinanceConstants.PROCEDT_STAGEACC, userRole, "_AEView", true));

		// Repay Header Details

		// Fetch Excess Amount Details
		rch.setExcessAmounts(finExcessAmountDAO.getExcessAmountsByRef(finID));

		// Fetch Payable Advise Amount Details
		rch.setPayableAdvises(
				manualAdviseDAO.getManualAdviseByRef(finID, FinanceConstants.MANUAL_ADVISE_PAYABLE, "_AView"));

		// Fee Details ( Fetch Fee Details for event fee only)
		List<FinFeeDetail> feesList = finFeeDetailService.getFinFeeDetailsByReferenceId(receiptId, eventCode, "_TView");
		if (CollectionUtils.isNotEmpty(feesList)) {
			for (FinFeeDetail finFeeDetail : feesList) {
				Long taxHeaderId = finFeeDetail.getTaxHeaderId();
				if (taxHeaderId != null && taxHeaderId > 0) {
					List<Taxes> taxDetailById = taxHeaderDetailsDAO.getTaxDetailById(taxHeaderId, "_TView");
					TaxHeader taxheader = new TaxHeader();
					taxheader.setTaxDetails(taxDetailById);
					taxheader.setHeaderId(taxHeaderId);
					finFeeDetail.setTaxHeader(taxheader);
				}
			}
			schdData.setFinFeeDetailList(feesList);
			rd.setFinFeeDetails(feesList);
		}

		if (schdData.getFinFeeDetailList() != null && !schdData.getFinFeeDetailList().isEmpty()) {
			for (int i = 0; i < rd.getReceiptHeader().getAllocations().size(); i++) {
				ReceiptAllocationDetail allocation = rd.getReceiptHeader().getAllocations().get(i);
				if (StringUtils.equals(RepayConstants.ALLOCATION_FEE, allocation.getAllocationType())) {
					for (FinFeeDetail feeDtl : rd.getFinanceDetail().getFinScheduleData().getFinFeeDetailList()) {
						if (feeDtl.getFeeID() == allocation.getAllocationTo()) {
							allocation.setAllocationTo(-(feeDtl.getFeeTypeID()));
							break;
						}
					}
				}
			}
		}

		// Setting fin tax details
		fd.setFinanceTaxDetail(financeTaxDetailDAO.getFinanceTaxDetail(finID, "_AView"));

		// Finance Document Details
		List<DocumentDetails> documents = documentDetailsDAO.getDocumentDetailsByRef(finReference,
				FinanceConstants.MODULE_NAME, procEdtEvent, "_View");
		if (fd.getDocumentDetailsList() != null && !fd.getDocumentDetailsList().isEmpty()) {
			fd.getDocumentDetailsList().addAll(documents);
		} else {
			fd.setDocumentDetailsList(documents);
		}

		if (ImplementationConstants.COLLATERAL_INTERNAL) {
			fd.setCollateralAssignmentList(collateralAssignmentDAO.getCollateralAssignmentByFinRef(finReference,
					FinanceConstants.MODULE_NAME, "_View"));
		} else {
			fd.setFinanceCollaterals(finCollateralService.getFinCollateralsByRef(finID, "_View"));
		}

		logger.debug(Literal.ENTERING);
		return rd;

	}

	@Override
	public FinReceiptData recalEarlyPaySchedule(FinReceiptData receiptData) {
		String entityDesc = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().getEntityDesc();
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		FinanceDetail fd = receiptData.getFinanceDetail();
		int receiptPurposeCtg = receiptCalculator.setReceiptCategory(rch.getReceiptPurpose());

		Long finID = financeMainDAO.getFinID(rch.getReference(), TableType.MAIN_TAB);

		FinScheduleData schdData = getFinSchDataForReceipt(finID, "_AView");
		schdData.setFinODDetails(receiptData.getFinanceDetail().getFinScheduleData().getFinODDetails());
		FinanceMain aFinanceMain = fd.getFinScheduleData().getFinanceMain();
		schdData.setFinFeeDetailList(receiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList());
		receiptData.getFinanceDetail().setFinScheduleData(schdData);
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
		if (PennantConstants.YES.equalsIgnoreCase(valueAsString)) {
			if (aFinanceMain.isStepFinance()) {
				if (StringUtils.isNotBlank(aFinanceMain.getStepPolicy())
						|| (aFinanceMain.isAlwManualSteps() && aFinanceMain.getNoOfSteps() > 0)) {
					isStepLoan = true;
					schdData.setStepPolicyDetails(financeStepDetailDAO.getFinStepDetailListByFinRef(finID, "", false));
				}
			}
		}

		if (isStepLoan) {

			List<RepayInstruction> rpst = schdData.getRepayInstructions();
			if (repayMain.getEarlyPayOnSchDate().compareTo(aFinanceMain.getGrcPeriodEndDate()) < 0) {

				schdData.getFinanceMain().setRecalSteps(true);
				schdData.getFinanceMain().setRecalType(CalculationConstants.RPYCHG_STEPINST);
				for (RepayInstruction repayInstruction : rpst) {
					if (repayInstruction.getRepayDate().compareTo(aFinanceMain.getGrcPeriodEndDate()) > 0) {
						schdData.getFinanceMain().setRecalFromDate(repayInstruction.getRepayDate());
						break;
					}
				}
			}
		}

		// Step POS Case , setting Step Details to Object
		if (receiptPurposeCtg == 1 && StringUtils.equals(method, CalculationConstants.RPYCHG_STEPPOS)) {
			schdData.setStepPolicyDetails(financeStepDetailDAO.getFinStepDetailListByFinRef(finID, "", false));
		} else if (aFinanceMain.isStepFinance()) {
			schdData.setStepPolicyDetails(financeStepDetailDAO.getFinStepDetailListByFinRef(finID, "", false));
		}

		// Calculation of Schedule Changes for Early Payment to change
		// Schedule Effects Depends On Method
		schdData.getFinanceMain().setReceiptPurpose(rch.getReceiptPurpose());
		schdData = ScheduleCalculator.recalEarlyPaySchedule(schdData, repayMain.getEarlyPayOnSchDate(), null,
				repayMain.getEarlyPayAmount(), method);

		receiptData.getFinanceDetail().setFinScheduleData(schdData);
		receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain().setEntityDesc(entityDesc);
		return receiptData;
	}

	private FinScheduleData getFinSchDataForReceipt(long finID, String type) {
		logger.debug(Literal.ENTERING);

		FinScheduleData scheduleData = new FinScheduleData();

		FinanceType finType = scheduleData.getFinanceType();

		FinanceMain fm = financeMainDAO.getFinanceMainById(finID, type, false);

		scheduleData.setFinID(fm.getFinID());
		scheduleData.setFinReference(fm.getFinReference());
		scheduleData.setFinanceMain(fm);

		// Schedule details
		scheduleData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, type, false));

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, fm.getProductCategory())) {
			scheduleData.setOverdraftScheduleDetails(
					overdraftScheduleDetailDAO.getOverdraftScheduleDetails(finID, "", false));
		}

		FinanceMain finMain = financeMainDAO.getEntityNEntityDesc(finID, "_Aview", false);

		if (finMain != null) {
			fm.setEntityCode(finMain.getEntityCode());
			fm.setEntityDesc(finMain.getEntityDesc());
		}

		if (fm.isAllowSubvention()) {
			// Disbursement Details
			List<FinanceDisbursement> disbursementDetails = financeDisbursementDAO.getFinanceDisbursementDetails(finID,
					type, false);
			scheduleData.setDisbursementDetails(disbursementDetails);
			if (CollectionUtils.isNotEmpty(disbursementDetails)) {
				for (FinanceDisbursement disbursement : disbursementDetails) {
					List<SubventionScheduleDetail> subventionScheduleDetails = subventionDetailDAO
							.getSubventionScheduleDetails(finID, disbursement.getDisbSeq(), type);
					disbursement.setSubventionSchedules(subventionScheduleDetails);
				}
			}

			scheduleData.setSubventionDetail(subventionDetailDAO.getSubventionDetail(finID, "_View"));
		} else {
			scheduleData.setSubventionDetail(null);
		}

		// Repay instructions
		scheduleData.setRepayInstructions(repayInstructionDAO.getRepayInstructions(finID, type, false));

		// Finance Disbursement Details
		scheduleData.setDisbursementDetails(financeDisbursementDAO.getFinanceDisbursementDetails(finID, type, false));

		// Finance Type
		scheduleData.setFinanceType(financeTypeDAO.getFinanceTypeByID(fm.getFinType(), type));

		Long promotionSeqId = fm.getPromotionSeqId();
		if (StringUtils.isNotBlank(fm.getPromotionCode()) && (promotionSeqId != null && promotionSeqId == 0)) {
			finType.setFInTypeFromPromotiion(this.promotionDAO.getPromotionByCode(fm.getPromotionCode(), type));
		}

		List<FinFeeDetail> fees = finFeeDetailDAO.getFinScheduleFees(finID, false, "_View");
		scheduleData.setFinFeeDetailList(fees);

		scheduleData.setFinPftDeatil(profitDetailsDAO.getFinProfitDetailsById(finID));

		// Finance Fee Schedule Details
		List<Long> feeIDList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(fees)) {
			for (FinFeeDetail fee : fees) {
				String schdMthd = fee.getFeeScheduleMethod();
				if (CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT.equals(schdMthd)
						|| CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS.equals(schdMthd)
						|| CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR.equals(schdMthd)) {
					feeIDList.add(fee.getFeeID());
				}
			}

			List<FinFeeScheduleDetail> feeSchedules = new ArrayList<>();
			if (CollectionUtils.isNotEmpty(feeIDList)) {
				feeSchedules = finFeeScheduleDetailDAO.getFeeScheduleByFinID(feeIDList, false, "");

				if (CollectionUtils.isNotEmpty(feeSchedules)) {
					Map<Long, List<FinFeeScheduleDetail>> schFeeMap = new HashMap<>();
					for (FinFeeScheduleDetail schdFee : feeSchedules) {
						List<FinFeeScheduleDetail> schList = new ArrayList<>();
						if (schFeeMap.containsKey(schdFee.getFeeID())) {
							schList = schFeeMap.get(schdFee.getFeeID());
							schFeeMap.remove(schdFee.getFeeID());
						}
						schList.add(schdFee);
						schFeeMap.put(schdFee.getFeeID(), schList);

					}

					for (FinFeeDetail fee : fees) {
						if (schFeeMap.containsKey(fee.getFeeID())) {
							fee.setFinFeeScheduleDetailList(schFeeMap.get(fee.getFeeID()));
						}
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return scheduleData;
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
		if (StringUtils.equals(rch.getReceiptPurpose(), FinServiceEvent.SCHDRPY)) {
			eventCode = AccountingEvent.REPAY;
		} else if (StringUtils.equals(rch.getReceiptPurpose(), FinServiceEvent.EARLYRPY)) {
			eventCode = AccountingEvent.EARLYPAY;
		} else if (StringUtils.equals(rch.getReceiptPurpose(), FinServiceEvent.EARLYSETTLE)) {
			eventCode = AccountingEvent.EARLYSTL;
		}

		// Set WriteOffAccounting
		FinanceMain finmain = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();
		if (FinanceConstants.CLOSE_STATUS_WRITEOFF.equals(finmain.getClosingStatus())) {
			eventCode = AccountingEvent.WRITEBK;
		}

		// FIXME Bharat Only get finschedule details and finprofitdetails
		FinReceiptData recData = null;
		if (rch.getReceiptID() <= 0 || receiptData.isPresentment()) {
			recData = receiptData;
		} else {
			recData = getFinReceiptDataByReceiptId(rch.getReceiptID(), eventCode, FinServiceEvent.RECEIPT, "");
		}

		FinanceMain fm = recData.getFinanceDetail().getFinScheduleData().getFinanceMain();

		rch = recData.getReceiptHeader();
		recData.setValueDate(rch.getValueDate());
		rch.setValueDate(null);
		rch.setReceiptDate(SysParamUtil.getAppDate());
		recData.setBuildProcess("I");
		recData.setAllocList(rch.getAllocations());
		recData.setForeClosure(isForeClosure);
		recData = receiptCalculator.initiateReceipt(recData, false);

		if (receiptPurposeCtg == 2 && DateUtil.compare(valueDate, fm.getMaturityDate()) < 0) {
			recData.getReceiptHeader().setValueDate(null);
			recData.setOrgFinPftDtls(recData.getFinanceDetail().getFinScheduleData().getFinPftDeatil());
			receiptPurposeCtg = receiptCalculator.setReceiptCategory(rch.getReceiptPurpose());
			recData.getRepayMain().setEarlyPayOnSchDate(valueDate);
			recalEarlyPaySchedule(recData);
			recData.setBuildProcess("I");
			recData = receiptCalculator.initiateReceipt(receiptData, false);
			recData.setActualReceiptAmount(rch.getReceiptAmount().subtract(receiptData.getExcessAvailable()));
			recData.setExcessAvailable(receiptCalculator.getExcessAmount(receiptData));
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
			recData = receiptCalculator.recalAutoAllocation(recData, false);
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

		if (StringUtils.equals(rch.getReceiptPurpose(), FinServiceEvent.EARLYRPY)) {
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

	public boolean checkDueAdjusted(List<ReceiptAllocationDetail> allocations, FinReceiptData rd) {
		boolean isDueAdjusted = true;
		for (ReceiptAllocationDetail allocate : allocations) {
			BigDecimal waivedAmount = allocate.getWaivedAmount();
			BigDecimal bal = allocate.getTotalDue().subtract(allocate.getTotalPaid()).subtract(waivedAmount);

			String taxType = allocate.getTaxType();
			if (waivedAmount.compareTo(BigDecimal.ZERO) > 0
					&& FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxType)) {
				FinanceDetail fd = rd.getFinanceDetail();
				TaxAmountSplit taxSplit = GSTCalculator.calculateGST(fd, taxType, waivedAmount, BigDecimal.ZERO);
				bal = bal.subtract(taxSplit.gettGST());
			}

			if (bal.compareTo(BigDecimal.ZERO) > 0 && allocate.isEditable()) {
				isDueAdjusted = false;
			}
		}
		return isDueAdjusted;
	}

	public FinReceiptData adjustToExcess(FinReceiptData rd) {
		FinReceiptHeader rch = rd.getReceiptHeader();
		rch.setPrvReceiptPurpose(rch.getReceiptPurpose());
		FinanceDetail fd = rd.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		schdData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(rch.getFinID(), "", false));
		rch.setReceiptPurpose(FinServiceEvent.SCHDRPY);
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
		return rd;
	}

	public FinanceDetail receiptTransaction(FinServiceInstruction fsi, String moduleDefiner) throws ServiceException {
		logger.debug(Literal.ENTERING);

		String eventCode = null;

		switch (moduleDefiner) {
		case FinServiceEvent.SCHDRPY:
			eventCode = AccountingEvent.REPAY;
			fsi.setModuleDefiner(FinServiceEvent.SCHDRPY);
			break;
		case FinServiceEvent.EARLYRPY:
			eventCode = AccountingEvent.EARLYPAY;
			fsi.setModuleDefiner(FinServiceEvent.EARLYRPY);
			break;
		case FinServiceEvent.EARLYSETTLE:
			eventCode = AccountingEvent.EARLYSTL;
			fsi.setModuleDefiner(FinServiceEvent.EARLYSETTLE);
			break;
		case FinServiceEvent.RESTRUCTURE:
			eventCode = AccountingEvent.RESTRUCTURE;
			fsi.setModuleDefiner(FinServiceEvent.RESTRUCTURE);
			break;
		default:
			break;
		}

		// Method for validate instruction details
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		fsi.setReceiptPurpose(moduleDefiner);
		finScheduleData.setFinServiceInstruction(fsi);
		FinanceProfitDetail fpd = profitDetailsDAO.getFinProfitDetailsById(fsi.getFinID());
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

		receiptData.getReceiptHeader().setExcldTdsCal(fsi.isExcldTdsCal());
		receiptData = setReceiptData(receiptData);
		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			return setReturnStatus(financeDetail);
		}

		financeDetail = doReceiptTransaction(receiptData, eventCode);

		if (financeDetail.getFinScheduleData() != null && financeDetail.getFinScheduleData().getErrorDetails() != null
				&& !financeDetail.getFinScheduleData().getErrorDetails().isEmpty()) {
			financeDetail = setReturnStatus(financeDetail);
		}

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	public FinanceDetail setReturnStatus(FinanceDetail financeDetail) {
		WSReturnStatus returnStatus = new WSReturnStatus();
		ErrorDetail errorDetail = financeDetail.getFinScheduleData().getErrorDetails().get(0);
		returnStatus.setReturnCode(errorDetail.getCode());
		returnStatus.setReturnText(errorDetail.getError());
		financeDetail.setFinScheduleData(null);
		financeDetail.setDocumentDetailsList(null);
		financeDetail.setJointAccountDetailList(null);
		financeDetail.setGurantorsDetailList(null);
		financeDetail.setCollateralAssignmentList(null);
		financeDetail.setReturnDataSetList(null);
		// financeDetail.setInterfaceDetailList(null);
		financeDetail.setFinFlagsDetails(null);
		financeDetail.setReturnStatus(returnStatus);
		return financeDetail;
	}

	private FinanceDetail validateInstructions(FinanceDetail financeDetail, String moduleDefiner, String eventCode) {
		logger.debug(Literal.ENTERING);

		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinServiceInstruction finServiceInstruction = finScheduleData.getFinServiceInstruction();
		String finReference = finServiceInstruction.getFinReference();
		financeDetail.setFinReference(finReference);

		if (!StringUtils.equals(finServiceInstruction.getReqType(), "Inquiry")
				&& !StringUtils.equals(finServiceInstruction.getReqType(), "Post")) {
			finScheduleData = setErrorToFSD(finScheduleData, "91113", finServiceInstruction.getReqType());
		}

		logger.debug(Literal.LEAVING);
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
		if (!finServiceInstruction.isReceiptUpload() && !AccountingEvent.RESTRUCTURE.equals(eventCode)) {
			financeDetail = validateFees(financeDetail);
		}

		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			logger.debug(Literal.LEAVING);
			return financeDetail;
		}

		try {
			financeDetail = doProcessReceipt(receiptData, receiptPurpose);

			if (!finServiceInstruction.isReceiptUpload()
					&& StringUtils.equals(receiptPurpose, FinServiceEvent.EARLYSETTLE)) {
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

		logger.debug(Literal.LEAVING);
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

	private boolean isSchdFullyPaid(long finID, List<FinanceScheduleDetail> scheduleDetails) {
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

		}

		// Check Penalty Paid Fully or not
		if (fullyPaid) {
			FinODDetails overdue = finODDetailsDAO.getTotals(finID);
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
		logger.debug(Literal.ENTERING);

		// FinReceiptData receiptData = setReceiptData(financeDetail,
		// receiptPurpose);
		FinanceDetail fd = receiptData.getFinanceDetail();
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		FinReceiptDetail rcd = rch.getReceiptDetails().get(0);
		FinScheduleData schd = fd.getFinScheduleData();
		FinServiceInstruction fsi = schd.getFinServiceInstruction();
		FinanceMain fm = schd.getFinanceMain();

		Date appDate = SysParamUtil.getAppDate();
		Date sysDate = DateUtility.getSysDate();

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
			schd.getFinanceMain().setReceiptPurpose(receiptPurpose);

			// Calculate Schedule if Part Payment case
			if (receiptPurposeCtg == 1) {
				schd = ScheduleCalculator.recalEarlyPaySchedule(schd, rch.getValueDate(), null, earlyPayAmount,
						recalType);
				receiptData = receiptCalculator.addPartPaymentAlloc(receiptData);
			}
			receiptData.getFinanceDetail().setFinScheduleData(schd);

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
			FinanceSummary summary = fd.getFinScheduleData().getFinanceSummary();
			summary.setFinODDetail(rch.getFinODDetails());
			fd.getFinScheduleData().setFinODDetails(rch.getFinODDetails());

			logger.debug(Literal.LEAVING);
			return fd;
		}

		if (fsi.isReceiptUpload() && dedupCheckRequest(rch, receiptPurpose)) {
			long rchID = CheckDedupSP(rch, receiptPurpose);

			// If receipt already exists, and status update required for
			// realization
			if (rchID > 0) {
				finReceiptHeaderDAO.updateReceiptStatusAndRealizationDate(rchID, RepayConstants.PAYSTATUS_REALIZED,
						rch.getRealizationDate());
				finReceiptDetailDAO.updateReceiptStatusByReceiptId(rchID, RepayConstants.PAYSTATUS_REALIZED);
				setErrorToFSD(schd, "0000", "Success");
				return fd;
			}
		}

		// fetch partner bank details
		if (rcd.getFundingAc() > 0) {
			PartnerBank partnerBank = partnerBankDAO.getPartnerBankById(rcd.getFundingAc(), "");
			if (partnerBank != null) {
				rcd.setPartnerBankAc(partnerBank.getAccountNo());
				rcd.setPartnerBankAcType(partnerBank.getAcType());
			}
		}

		int version = 0;
		// Receipt upload process
		if (fsi.isReceiptdetailExits()) {
			FinReceiptData oldReceiptData = this.getFinReceiptDataById(fsi.getFinReference(), AccountingEvent.REPAY,
					FinServiceEvent.RECEIPT, FinanceConstants.REALIZATION_MAKER);
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

			String finEvent = FinServiceEvent.RECEIPT;
			FinanceWorkFlow financeWorkFlow = financeWorkFlowDAO.getFinanceWorkFlowById(fm.getFinType(), finEvent,
					PennantConstants.WORFLOW_MODULE_FINANCE, "");

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
				rch.setRcdMaintainSts(FinServiceEvent.RECEIPT);
				rch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				rch.setRecordStatus(PennantConstants.RCD_STATUS_SUBMITTED);
			}

			receiptData.getFinanceDetail().getFinScheduleData().setFinanceMain(fm);
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
			schd.getFinanceMain().setReceiptPurpose(receiptPurpose);
			if (receiptPurposeCtg == 1) {
				schd = ScheduleCalculator.recalEarlyPaySchedule(schd, rch.getValueDate(), null, earlyPayAmount,
						recalType);
				receiptData = receiptCalculator.addPartPaymentAlloc(receiptData);
			}
			Cloner cloner = new Cloner();
			FinReceiptData tempReceiptData = cloner.deepClone(receiptData);
			receiptData.getFinanceDetail().setFinScheduleData(schd);
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
				schd = fd.getFinScheduleData();
				schd = setErrorToFSD(schd, "90330", receiptPurpose, "");
				fd.setFinScheduleData(schd);
				return fd;
			}

			receiptData.setBuildProcess("R");
			receiptData = receiptCalculator.initiateReceipt(receiptData, false);

			receiptData.getFinanceDetail().getFinScheduleData().setFinanceScheduleDetails(
					tempReceiptData.getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails());
			auditHeader = doApprove(auditHeader);
		}

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail auditErrorDetail : auditHeader.getErrorMessage()) {
				setErrorToFSD(schd, auditErrorDetail.getCode(), auditErrorDetail.getError());
				return fd;
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
			finServInst.setFinID(fm.getFinID());
			finServInst.setFinReference(fm.getFinReference());
			finServInst.setFinReference(fm.getFinReference());
			finServInst.setFinEvent(rpyHeader.getFinEvent());
			finServInst.setAmount(rpyHeader.getRepayAmount());
			finServInst.setAppDate(appDate);
			finServInst.setSystemDate(sysDate);
			finServInst.setMaker(auditHeader.getAuditUsrId());
			finServInst.setMakerAppDate(appDate);
			finServInst.setMakerSysDate(DateUtil.getSysDate());
			finServInst.setChecker(auditHeader.getAuditUsrId());
			finServInst.setCheckerAppDate(appDate);
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

		logger.debug(Literal.LEAVING);
		return fd;
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

		int receiptPurposeCtg = receiptCalculator
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
					payable.setTotPaidNow(amount);
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

		if (receiptData.isForeClosure()) {
			rch.setReceiptAmount(BigDecimal.ZERO);
			receiptData.setExcessAvailable(receiptCalculator.getExcessAmount(receiptData));
			rch.setReceiptAmount(receiptData.getExcessAvailable());
		}

		if (receiptPurposeCtg == 2 && DateUtility.compare(valueDate, finMain.getMaturityDate()) < 0
				&& finMain.isFinIsActive()) {
			receiptData.getReceiptHeader().setValueDate(null);
			receiptData.setOrgFinPftDtls(receiptData.getFinanceDetail().getFinScheduleData().getFinPftDeatil());
			receiptData.getRepayMain().setEarlyPayOnSchDate(valueDate);
			recalEarlyPaySchedule(receiptData);
			receiptData.setBuildProcess("I");
			receiptData = receiptCalculator.initiateReceipt(receiptData, false);
			receiptData.setActualReceiptAmount(rch.getReceiptAmount().subtract(receiptData.getExcessAvailable()));
			receiptData.setExcessAvailable(receiptCalculator.getExcessAmount(receiptData));
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

	@Override
	public List<ErrorDetail> dedupCheck(FinServiceInstruction fsi) {
		List<ErrorDetail> errors = new ArrayList<>();
		StringBuilder message = new StringBuilder();

		List<Long> receiptIdList = finReceiptHeaderDAO.isDedupReceiptExists(fsi);

		if (receiptIdList.isEmpty()) {
			return new ArrayList<>();
		}

		message = new StringBuilder();
		message.append("Receipt for the Fin Reference {0} , Value Date {1}, Receipt Amount {2} ");
		if (fsi.getTransactionRef() != null) {
			message.append(" and Transaction Reference");
		}
		message.append("{3} already exists with Receipt Id {4} .");

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
	public FinTaxReceivable getTaxReceivable(long finId, String taxFor) {
		return finODAmzTaxDetailDAO.getFinTaxReceivable(finId, taxFor);
	}

	public FinReceiptData createXcessRCD(FinReceiptData receiptData) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		List<XcessPayables> xcessPayables = rch.getXcessPayables();
		List<FinReceiptDetail> rcdList = rch.getReceiptDetails();
		receiptData.getReceiptHeader().setReceiptDetails(rcdList);
		int receiptPurposeCtg = receiptCalculator.setReceiptCategory(rch.getReceiptPurpose());

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
						taxPercMap = GSTCalculator.getTaxPercentages(rch.getFinID());
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
			boolean isLanActive = financeMainDAO.isFinActive(rch.getFinID());
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
		logger.debug(Literal.ENTERING);
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
		logger.debug(Literal.LEAVING);
	}

	public ErrorDetail receiptCancelValidation(long finID, Date lastReceivedDate) {
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

		Date maxPostDate = finLogEntryDetailDAO.getMaxPostDateByRef(finID);

		if (DateUtil.compare(lastReceivedDate, maxPostDate) <= 0) {
			String[] valueParm = new String[4];
			String msg = "Post Receipt Creation, Schedule is effected due to other Schedule Change event. Hence System will not allow to cancel the receipt.";
			return new ErrorDetail("21005", msg, valueParm);
		}

		return null;
	}

	private AuditHeader approveValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		FinReceiptData receiptData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		FinReceiptHeader frh = receiptData.getReceiptHeader();
		Date valueDate = frh.getValueDate();

		Date lastReceivedDate = getMaxReceiptDate(receiptData.getFinReference());
		if (lastReceivedDate != null && DateUtil.compare(valueDate, lastReceivedDate) < 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtil.formatToLongDate(valueDate);
			valueParm[1] = DateUtil.formatToLongDate(lastReceivedDate);
			auditHeader.getAuditDetail().setErrorDetail(ErrorUtil
					.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "RU0011", valueParm, valueParm)));
			auditHeader.setErrorList(auditHeader.getAuditDetail().getErrorDetails());
		}
		Date prvSchdDate = null;
		FinanceScheduleDetail prvSchd = null;
		Date appDate = SysParamUtil.getAppDate();
		prvSchd = financeScheduleDetailDAO.getPrvSchd(receiptData.getFinID(), appDate);

		if (prvSchd != null) {
			prvSchdDate = prvSchd.getSchDate();
		}

		String receiptModeSts = frh.getReceiptModeStatus();
		if (!RepayConstants.PAYSTATUS_BOUNCE.equals(receiptModeSts)
				&& !RepayConstants.PAYSTATUS_CANCEL.equals(receiptModeSts)) {
			if (prvSchdDate != null && valueDate != null && DateUtil.compare(valueDate, prvSchdDate) < 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtil.formatToLongDate(valueDate);
				valueParm[1] = DateUtil.formatToLongDate(prvSchdDate);
				auditHeader.getAuditDetail().setErrorDetail(ErrorUtil
						.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "RU0012", valueParm, valueParm)));
				auditHeader.setErrorList(auditHeader.getAuditDetail().getErrorDetails());
			}
		}

		Date monthStart = DateUtil.getMonthStart(appDate);
		if (valueDate != null && valueDate.compareTo(monthStart) < 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtil.formatToLongDate(valueDate);
			valueParm[1] = DateUtil.formatToLongDate(monthStart);
			auditHeader.getAuditDetail().setErrorDetail(ErrorUtil
					.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "RU0010", valueParm, valueParm)));
			auditHeader.setErrorList(auditHeader.getAuditDetail().getErrorDetails());
		}

		if (StringUtils.equals(frh.getReceiptPurpose(), FinServiceEvent.EARLYRPY)) {
			if (frh.getPartPayAmount().compareTo(BigDecimal.ZERO) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = frh.getReceiptPurpose();
				valueParm[1] = frh.getReceiptPurpose();
				auditHeader.getAuditDetail().setErrorDetail(ErrorUtil
						.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "90332", valueParm, valueParm)));
				auditHeader.setErrorList(auditHeader.getAuditDetail().getErrorDetails());
			}
			BigDecimal closingBal = getClosingBalance(frh.getFinID(), valueDate);
			BigDecimal diff = closingBal.subtract(frh.getPartPayAmount());

			FinanceDetail fd = receiptData.getFinanceDetail();
			FinScheduleData schdData = fd.getFinScheduleData();
			FinanceMain fm = schdData.getFinanceMain();

			if (diff.compareTo(new BigDecimal(100)) < 0) {
				String[] valueParm = new String[2];
				int formatee = CurrencyUtil.getFormat(fm.getFinCcy());
				BigDecimal formateAmount = PennantApplicationUtil.formateAmount(closingBal, formatee);
				valueParm[0] = String.valueOf(formateAmount);
				valueParm[1] = String.valueOf(formateAmount);
				auditHeader.getAuditDetail().setErrorDetail(ErrorUtil
						.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "91127", valueParm, valueParm)));
				auditHeader.setErrorList(auditHeader.getAuditDetail().getErrorDetails());
			}
		}

		auditHeader = nextProcess(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public void saveMultiReceipt(AuditHeader auditHeader) throws Exception {
		logger.debug(Literal.ENTERING);
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

		logger.debug(Literal.LEAVING);
	}

	@Override
	public long getUploadSeqId() {
		return receiptUploadHeaderDAO.generateSeqId();
	}

	@Override
	public void saveMultiReceiptLog(List<FinReceiptQueueLog> finReceiptQueueList) {
		finReceiptHeaderDAO.saveMultiReceiptLog(finReceiptQueueList);
	}

	@Override
	public List<Long> getInProcessMultiReceiptRecord() {
		return finReceiptHeaderDAO.getInProcessMultiReceiptRecord();
	}

	@Override
	public void batchUpdateMultiReceiptLog(List<FinReceiptQueueLog> finReceiptQueueList) {
		finReceiptHeaderDAO.batchUpdateMultiReceiptLog(finReceiptQueueList);
	}

	@Override
	public ErrorDetail getWaiverValidation(long finID, String receiptPurpose, Date valueDate) {
		if (!(FinanceConstants.EARLYSETTLEMENT.equals(receiptPurpose)
				|| FinServiceEvent.EARLYSETTLE.equals(receiptPurpose))) {
			return null;
		}

		Date lastWaiverDate = getLastWaiverDate(finID, SysParamUtil.getAppDate(), valueDate);
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

	private void doPostHookValidation(AuditHeader auditHeader) {
		if (postValidationHook != null) {
			List<ErrorDetail> errorDetails = postValidationHook.validation(auditHeader);

			if (errorDetails != null) {
				errorDetails = ErrorUtil.getErrorDetails(errorDetails, auditHeader.getUsrLanguage());
				auditHeader.setErrorList(errorDetails);
			}
		}
	}

	@Override
	public int geFeeReceiptCountByExtReference(String reference, String receiptPurpose, String extReference) {
		return finReceiptHeaderDAO.geFeeReceiptCountByExtReference(reference, receiptPurpose, extReference);
	}

	public List<ErrorDetail> checkFeeWaiverInProcess(Long finID, String finReference) {
		logger.debug(Literal.ENTERING);

		List<ErrorDetail> errors = new ArrayList<>();
		StringBuilder message = new StringBuilder();

		if (feeWaiverHeaderDAO.isFeeWaiverInProcess(finID)) {
			String[] valueParm = new String[2];
			message.append("Fee Waivers is in progress for the selected Loan {0}");
			valueParm[0] = finReference;
			errors.add(new ErrorDetail("90500", message.toString(), valueParm));
		}

		logger.debug(Literal.LEAVING);
		return errors;
	}

	@Override
	public boolean checkPresentmentsInQueue(long finID) {
		return finReceiptHeaderDAO.checkPresentmentsInQueue(finID);
	}

	@Override
	public Date getFinSchdDate(FinReceiptHeader rh) {
		String receiptPurpose = rh.getReceiptPurpose();

		Date appDate = SysParamUtil.getAppDate();

		if (FinServiceEvent.EARLYRPY.equals(receiptPurpose)) {
			return financeScheduleDetailDAO.getSchdDateForDPD(rh.getFinID(), appDate);
		}

		if (FinServiceEvent.EARLYSETTLE.equals(receiptPurpose)) {
			return financeScheduleDetailDAO.getSchdDateForDPD(rh.getFinID(), appDate);
		}

		return financeRepaymentsDAO.getFinSchdDateByReceiptId(rh.getReceiptID(), "");
	}

	@Override
	public List<FinExcessAmount> xcessList(long finID) {
		return finExcessAmountDAO.getExcessAmountsByRef(finID);
	}

	@Override
	public Date getLastWaiverDate(long finID, Date appDate, Date receiptDate) {
		return feeWaiverHeaderDAO.getLastWaiverDate(finID, appDate, receiptDate);
	}

	public void setLimitCheckDetails(LimitCheckDetails limitCheckDetails) {
		this.limitCheckDetails = limitCheckDetails;
	}

	public void setLimitManagement(LimitManagement limitManagement) {
		this.limitManagement = limitManagement;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
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

	public void setFinTypePartnerBankDAO(FinTypePartnerBankDAO finTypePartnerBankDAO) {
		this.finTypePartnerBankDAO = finTypePartnerBankDAO;
	}

	public void setPartnerBankDAO(PartnerBankDAO partnerBankDAO) {
		this.partnerBankDAO = partnerBankDAO;
	}

	public void setFinanceWorkFlowDAO(FinanceWorkFlowDAO financeWorkFlowDAO) {
		this.financeWorkFlowDAO = financeWorkFlowDAO;
	}

	public void setReceiptRealizationService(ReceiptRealizationService receiptRealizationService) {
		this.receiptRealizationService = receiptRealizationService;
	}

	public void setReceiptCancellationService(ReceiptCancellationService receiptCancellationService) {
		this.receiptCancellationService = receiptCancellationService;
	}

	public void setReceiptResponseDetailDAO(ReceiptResponseDetailDAO receiptResponseDetailDAO) {
		this.receiptResponseDetailDAO = receiptResponseDetailDAO;
	}

	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}

	public void setAccountingSetDAO(AccountingSetDAO accountingSetDAO) {
		this.accountingSetDAO = accountingSetDAO;
	}

	public void setFinanceTaxDetailDAO(FinanceTaxDetailDAO financeTaxDetailDAO) {
		this.financeTaxDetailDAO = financeTaxDetailDAO;
	}

	public void setReceiptUploadHeaderDAO(ReceiptUploadHeaderDAO receiptUploadHeaderDAO) {
		this.receiptUploadHeaderDAO = receiptUploadHeaderDAO;
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

	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	public void setFeeWaiverHeaderDAO(FeeWaiverHeaderDAO feeWaiverHeaderDAO) {
		this.feeWaiverHeaderDAO = feeWaiverHeaderDAO;
	}

	public void setFeeReceiptService(FeeReceiptService feeReceiptService) {
		this.feeReceiptService = feeReceiptService;
	}

	public void setFinFeeConfigService(FinFeeConfigService finFeeConfigService) {
		this.finFeeConfigService = finFeeConfigService;
	}

	public void setFinTypeFeesDAO(FinTypeFeesDAO finTypeFeesDAO) {
		this.finTypeFeesDAO = finTypeFeesDAO;
	}

	public void setExtendedFieldExtensionService(ExtendedFieldExtensionService extendedFieldExtensionService) {
		this.extendedFieldExtensionService = extendedFieldExtensionService;
	}
}
