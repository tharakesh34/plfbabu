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
 * FileName : ReceiptServiceImpl.java * receiptTransaction Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 30-07-2011 *
 * 
 * Description : *
 * 
 ******************************************************************************************** receiptTransaction Date Author Version Comments *
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
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
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.app.util.RepaymentProcessUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.BankDetailDAO;
import com.pennant.backend.dao.applicationmaster.InstrumentwiseLimitDAO;
import com.pennant.backend.dao.applicationmaster.ReasonCodeDAO;
import com.pennant.backend.dao.finance.FeeWaiverHeaderDAO;
import com.pennant.backend.dao.finance.FinODAmzTaxDetailDAO;
import com.pennant.backend.dao.finance.OverdraftScheduleDetailDAO;
import com.pennant.backend.dao.finance.ReceiptResponseDetailDAO;
import com.pennant.backend.dao.finance.ReceiptUploadDetailDAO;
import com.pennant.backend.dao.finance.ReceiptUploadHeaderDAO;
import com.pennant.backend.dao.finance.SubventionDetailDAO;
import com.pennant.backend.dao.finance.TaxHeaderDetailsDAO;
import com.pennant.backend.dao.partnerbank.PartnerBankDAO;
import com.pennant.backend.dao.receipts.DepositChequesDAO;
import com.pennant.backend.dao.receipts.DepositDetailsDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.receipts.ReceiptAllocationDetailDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeFeesDAO;
import com.pennant.backend.dao.rmtmasters.FinTypePartnerBankDAO;
import com.pennant.backend.dao.rmtmasters.PromotionDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.applicationmaster.InstrumentwiseLimit;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldExtension;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.finance.DepositCheques;
import com.pennant.backend.model.finance.DepositDetails;
import com.pennant.backend.model.finance.DepositMovements;
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
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.extendedfieldsExtension.ExtendedFieldExtensionService;
import com.pennant.backend.service.finance.FeeReceiptService;
import com.pennant.backend.service.finance.FinFeeConfigService;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.service.finance.ReceiptCancellationService;
import com.pennant.backend.service.finance.ReceiptRealizationService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.limitservice.impl.LimitManagement;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
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
import com.pennanttech.framework.security.core.User;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.pff.service.hook.PostValidationHook;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.receipt.ReceiptPurpose;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennanttech.pff.receipt.constants.AllocationType;
import com.pennanttech.pff.receipt.constants.ReceiptMode;
import com.pennanttech.pff.receipt.util.ReceiptUtil;

public class ReceiptServiceImpl extends GenericFinanceDetailService implements ReceiptService {
	private static final Logger logger = LogManager.getLogger(ReceiptServiceImpl.class);

	private static final String EXCESS_ADJUST_TO = "Excess Adjust to ";
	private static final BigDecimal ZERO = BigDecimal.ZERO;
	private static final BigDecimal HUNDRED = new BigDecimal(100);

	private LimitCheckDetails limitCheckDetails;
	private LimitManagement limitManagement;
	private FinExcessAmountDAO finExcessAmountDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private ReceiptAllocationDetailDAO allocationDetailDAO;
	private RepaymentProcessUtil repayProcessUtil;
	private OverdraftScheduleDetailDAO overdraftScheduleDetailDAO;
	private LatePayMarkingService latePayMarkingService;
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
	private ReceiptRealizationService receiptRealizationService;
	private ReceiptCancellationService receiptCancellationService;
	private ReceiptResponseDetailDAO receiptResponseDetailDAO;
	private ReceiptUploadHeaderDAO receiptUploadHeaderDAO;
	private TaxHeaderDetailsDAO taxHeaderDetailsDAO;
	private ReasonCodeDAO reasonCodeDAO;
	private FeeWaiverHeaderDAO feeWaiverHeaderDAO;
	private FeeReceiptService feeReceiptService;
	private FinFeeConfigService finFeeConfigService;
	private PostValidationHook postValidationHook;
	private FinTypeFeesDAO finTypeFeesDAO;
	private ExtendedFieldExtensionService extendedFieldExtensionService;
	private BankDetailDAO bankDetailDAO;
	private FinanceWorkFlowService financeWorkFlowService;

	public ReceiptServiceImpl() {
		super();
	}

	@Override
	public FinReceiptHeader getFinReceiptHeaderById(long receiptID, boolean isFeePayment, String type) {
		logger.debug(Literal.ENTERING);

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
				if (ReceiptMode.PAYABLE.equals(rcd.getPaymentType())) {
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

		List<ReceiptAllocationDetail> allocations = allocationDetailDAO.getAllocationsByReceiptID(receiptID, type);

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

		rch.setAllocations(allocations);

		for (FinReceiptDetail rcd : rcdList) {
			rcd.setRepayHeader(financeRepaymentsDAO.getFinRepayHeadersByReceipt(rcd.getReceiptSeqID(), ""));
		}

		if (ReceiptMode.CHEQUE.equalsIgnoreCase(rch.getReceiptMode())
				|| RepayConstants.PAYSTATUS_BOUNCE.equals(rch.getReceiptModeStatus())) {
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

		FinanceType financeType = financeTypeDAO.getOrgFinanceTypeByID(finType, "_ORGView");

		fm.setEntityDesc(financeType.getLovDescEntityDesc());

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
		getInProcessReceiptData(receiptData);

		logger.debug(Literal.LEAVING);
		return receiptData;
	}

	@Override
	public void setFinanceData(FinReceiptData rd) {
		FinanceDetail fd = rd.getFinanceDetail();

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();
		long custID = fm.getCustID();
		String finReference = fm.getFinReference();
		String finType = fm.getFinType();
		String promotionCode = fm.getPromotionCode();
		String productCategory = fm.getProductCategory();
		String eventCode = schdData.getFeeEvent();

		FinReceiptHeader rch = rd.getReceiptHeader();

		rch.setFinID(finID);
		rch.setReference(finReference);

		FinanceType financeType = schdData.getFinanceType();
		if (financeType == null) {
			financeType = financeTypeDAO.getOrgFinanceTypeByID(finType, "_ORGView");

			fm.setEntityDesc(financeType.getLovDescEntityDesc());

			schdData.setFinanceType(financeType);
		}

		if (StringUtils.isNotBlank(promotionCode) && (fm.getPromotionSeqId() != null && fm.getPromotionSeqId() == 0)) {
			Promotion promotion = this.promotionDAO.getPromotionForLMSEvent(promotionCode);
			promotion.setFinCcy(financeType.getFinCcy());
			financeType.setFInTypeFromPromotiion(promotion);
		}

		if (fm.isStepFinance()) {
			schdData.setStepPolicyDetails(financeStepDetailDAO.getStepDetailsForLMSEvent(finID));
		}

		schdData.setFinanceScheduleDetails(financeScheduleDetailDAO.getSchedulesForLMSEvent(finID));

		fd.setFinTypeFeesList(finTypeFeesDAO.getFinTypeFeesForLMSEvent(finType, eventCode));

		fd.setFinFeeConfigList(finFeeConfigService.getFinFeeConfigList(finID, eventCode, false, "_View"));

		if (FinanceConstants.PRODUCT_ODFACILITY.equals(productCategory)) {
			schdData.setOverdraftScheduleDetails(overdraftScheduleDetailDAO.getOverdraftScheduleForLMSEvent(finID));
		}

		schdData.setDisbursementDetails(financeDisbursementDAO.getFinanceDisbursementForLMSEvent(finID));
		schdData.setRepayInstructions(repayInstructionDAO.getRepayInstructionsForLMSEvent(finID));
		schdData.setFinODPenaltyRate(finODPenaltyRateDAO.getFinODPenaltyRateForLMSEvent(finID));
		schdData.setFinPftDeatil(profitDetailsDAO.getFinProfitDetailsById(finID));

		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setCustomer(customerDAO.getCustomerEOD(custID));
		customerDetails.setCustID(fm.getCustID());
		fd.setCustomerDetails(customerDetails);

		rch.setExcessAmounts(finExcessAmountDAO.getExcessAmountsByRef(finID));
		rch.setPayableAdvises(manualAdviseDAO.getManualAdviseForLMSEvent(finID));

		String type = "_View";

		schdData.setFinFeeDetailList(finFeeDetailDAO.getFinScheduleFees(finID, false, type));

		for (FinFeeDetail fee : schdData.getFinFeeDetailList()) {
			Long headerId = fee.getTaxHeaderId();
			if (headerId != null && headerId > 0) {
				TaxHeader header = new TaxHeader();
				header.setTaxDetails(taxHeaderDetailsDAO.getTaxDetailById(headerId, type));
				header.setHeaderId(headerId);
				fee.setTaxHeader(header);
			}
		}

		if (ImplementationConstants.COLLATERAL_INTERNAL) {
			List<CollateralAssignment> assignements = new ArrayList<>();
			String module = FinanceConstants.MODULE_NAME;
			assignements.addAll(collateralAssignmentDAO.getCollateralAssignmentByFinRef(finReference, module, "_View"));
			fd.setCollateralAssignmentList(assignements);
		} else {
			fd.setFinanceCollaterals(finCollateralService.getFinCollateralsByRef(finID, ""));
		}

		fd.setFinanceTaxDetail(financeTaxDetailDAO.getFinanceTaxDetailForLMSEvent(finID));

		getInProcessReceiptData(rd);
	}

	private void getInProcessReceiptData(FinReceiptData rd) {
		FinReceiptHeader rch = rd.getReceiptHeader();

		rd.setInProcRchList(finReceiptHeaderDAO.getInprocessReceipts(rch.getFinID()));
		rd.setInProcRadList(allocationDetailDAO.getManualAllocationsByRef(rch.getFinID(), rch.getReceiptID()));
	}

	@Override
	public FinanceType getFinanceType(String finType) {
		return financeTypeDAO.getOrgFinanceTypeByID(finType, "_ORGView");
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
			if (ReceiptMode.PAYABLE.equals(rcd.getPaymentType())) {
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
		getInProcessReceiptData(rd);

		return rd;
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		FinReceiptData rcData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();

		long serviceUID = Long.MIN_VALUE;
		if (rcData.getFinanceDetail().getExtendedFieldRender() != null) {
			serviceUID = extendedFieldDetailsService.getInstructionUID(
					rcData.getFinanceDetail().getExtendedFieldRender(),
					rcData.getFinanceDetail().getExtendedFieldExtension());
		}

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		boolean changeStatus = false;

		// AuditHeader auditHeader = copy(aAuditHeader);
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
			if (ReceiptMode.EXCESS.equals(paymentType) || ReceiptMode.EMIINADV.equals(paymentType)
					|| ReceiptMode.ADVINT.equals(paymentType) || ReceiptMode.ADVEMI.equals(paymentType)
					|| ReceiptMode.CASHCLT.equals(paymentType) || ReceiptMode.DSF.equals(paymentType)) {

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
			if (StringUtils.equals(paymentType, ReceiptMode.PAYABLE)
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
			if (StringUtils.equals(Allocation.FEE, allocation.getAllocationType())) {
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

		eventMapping = fm.getGlSubHeadCodes();

		if (MapUtils.isEmpty(eventMapping)) {
			eventMapping.putAll(financeMainDAO.getGLSubHeadCodes(fm.getFinID()));
		}

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
			if (!(detail.getPaymentType().equals(ReceiptMode.EMIINADV)
					|| detail.getPaymentType().equals(ReceiptMode.EXCESS)
					|| detail.getPaymentType().equals(ReceiptMode.PAYABLE))) {
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
			if (StringUtils.equals(receiptDetail.getPaymentType(), ReceiptMode.EXCESS)
					|| StringUtils.equals(receiptDetail.getPaymentType(), ReceiptMode.EMIINADV)
					|| StringUtils.equals(receiptDetail.getPaymentType(), ReceiptMode.ADVINT)) {

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
			if (StringUtils.equals(receiptDetail.getPaymentType(), ReceiptMode.PAYABLE)) {

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

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		FinReceiptData ard = (FinReceiptData) auditHeader.getAuditDetail().getModelData();

		List<AuditDetail> auditDetails = new ArrayList<>();

		FinReceiptData rd = (FinReceiptData) auditHeader.getAuditDetail().getModelData();

		FinanceDetail fd = rd.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		FinReceiptHeader rch = rd.getReceiptHeader();

		Date appDate = fm.getAppDate();

		if (appDate == null) {
			appDate = SysParamUtil.getAppDate();
		}

		long finID = fm.getFinID();

		String receiptMode = rch.getReceiptMode();

		if (!ReceiptMode.RESTRUCT.equals(receiptMode) && financeScheduleDetailDAO.isScheduleInQueue(finID)) {
			throw new AppException("Not allowed to approve the receipt, since the loan schedule under maintenance.");
		}

		FinReceiptHeader befRch = null;
		if (!PennantConstants.RECORD_TYPE_NEW.equals(rch.getRecordType())) {
			befRch = getFinReceiptHeaderById(rch.getReceiptID(), false, "");
		}

		List<FinReceiptDetail> befFinReceiptDetail = new ArrayList<>();
		if (befRch != null) {
			befFinReceiptDetail = befRch.getReceiptDetails();
		}

		auditHeader.getAuditDetail().setBefImage(befRch);

		String rmStatus = rch.getReceiptModeStatus();
		if (RepayConstants.PAYSTATUS_BOUNCE.equals(rmStatus) || RepayConstants.PAYSTATUS_CANCEL.equals(rmStatus)) {
			for (FinReceiptDetail rcd : rch.getReceiptDetails()) {
				FinRepayHeader rph = financeRepaymentsDAO.getFinRepayHeadersByReceipt(rcd.getReceiptSeqID(), "");
				rcd.setRepayHeader(rph);
				if (rph != null) {
					List<RepayScheduleDetail> rpySchdList = financeRepaymentsDAO
							.getRpySchdListByRepayID(rph.getRepayID(), "");
					rph.setRepayScheduleDetails(rpySchdList);
				}
			}
			receiptCancellationService.doApprove(auditHeader);
			auditHeader.getAuditDetail().setModelData(rd);
			return auditHeader;
		}

		String roleCode = rd.getReceiptHeader().getRoleCode();
		String nextRoleCode = rd.getReceiptHeader().getNextRoleCode();

		ReceiptPurpose receiptPurpose = ReceiptPurpose.purpose(rch.getReceiptPurpose());
		ReceiptPurpose prvReceiptPurpose = ReceiptPurpose.purpose(rch.getPrvReceiptPurpose());
		if (FinanceConstants.REALIZATION_APPROVER.equals(roleCode)) {
			if (receiptPurpose == ReceiptPurpose.SCHDRPY && prvReceiptPurpose == null
					&& (ReceiptMode.CHEQUE.equals(receiptMode) || ReceiptMode.DD.equals(receiptMode))) {
				receiptRealizationService.doApprove(auditHeader);
				auditHeader.getAuditDetail().setModelData(rd);
				return auditHeader;
			}
		}
		// schedule pay effect on cheque/dd realization(if N schedule will effect while approve/if Y schedule will
		// effect while realization)
		if (!SysParamUtil.isAllowed(SMTParameterConstants.CHEQUE_MODE_SCHDPAY_EFFT_ON_REALIZATION)
				&& SysParamUtil.isAllowed(SMTParameterConstants.CHQ_RECEIPTS_PAID_AT_DEPOSIT_APPROVER)) {
			if (FinanceConstants.REALIZATION_APPROVER.equals(roleCode)) {
				if (ReceiptPurpose.SCHDRPY == receiptPurpose && prvReceiptPurpose == null
						&& (ReceiptMode.CHEQUE.equals(receiptMode) || ReceiptMode.DD.equals(receiptMode))) {
					receiptRealizationService.doApprove(auditHeader);
					auditHeader.getAuditDetail().setModelData(rd);
					return auditHeader;
				}
			}
		}

		FinReceiptHeader receiptHeader = rd.getReceiptHeader();
		if (schdData.getFinServiceInstructions().isEmpty()) {
			FinServiceInstruction finServInst = new FinServiceInstruction();
			finServInst.setFinID(receiptHeader.getFinID());
			finServInst.setFinReference(receiptHeader.getReference());
			finServInst.setFinEvent(fd.getModuleDefiner());
			finServInst.setReceiptPurpose(receiptHeader.getReceiptPurpose());
			schdData.setFinServiceInstruction(finServInst);
		}

		if (!PennantConstants.FINSOURCE_ID_API.equalsIgnoreCase(rd.getSourceId())
				&& !schdData.getFinServiceInstruction().isReceiptUpload()) {
			if (ReceiptPurpose.EARLYRPY == receiptPurpose || ReceiptPurpose.EARLYSETTLE == receiptPurpose) {
				auditHeader = approveValidation(auditHeader, "doApprove");
			}
		}

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		long serviceUID = Long.MIN_VALUE;

		BigDecimal restructBpiAmount = BigDecimal.ZERO;
		if (rd.getFinanceDetail().getExtendedFieldRender() != null) {
			serviceUID = extendedFieldDetailsService.getInstructionUID(rd.getFinanceDetail().getExtendedFieldRender(),
					rd.getFinanceDetail().getExtendedFieldExtension());
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
		FinanceProfitDetail pfd = schdData.getFinPftDeatil();

		// Execute Accounting Details Process
		// =======================================
		FinReceiptData rcdata = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		FinanceDetail financeDetail = rcdata.getFinanceDetail();
		if (rd.getReceiptHeader().getReceiptID() > 0
				&& StringUtils.isEmpty(rd.getReceiptHeader().getPrvReceiptPurpose())) {
			rd = recalculateReceipt(rd);
			if (rd.getErrorDetails().size() > 0) {
				auditHeader.setErrorList(rd.getErrorDetails());
				return auditHeader;
			}
		}

		if (!rd.isDueAdjusted() && !StringUtils.equalsIgnoreCase(rd.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
			auditHeader.getAuditDetail().setErrorDetail(
					ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "FC0001", null, null)));
			auditHeader.setErrorList(auditHeader.getAuditDetail().getErrorDetails());
			auditHeader = nextProcess(auditHeader);
			if (!auditHeader.isNextProcess()) {
				return auditHeader;
			}
		}

		// For Extended fields
		fd.setAuditDetailMap(financeDetail.getAuditDetailMap());
		fd.setExtendedFieldExtension(financeDetail.getExtendedFieldExtension());
		fd.setExtendedFieldHeader(financeDetail.getExtendedFieldHeader());
		fd.setExtendedFieldRender(financeDetail.getExtendedFieldRender());

		rch.setDepositProcess(ard.getReceiptHeader().isDepositProcess());

		FinScheduleData scheduleData = schdData;

		List<FinanceScheduleDetail> schedules = scheduleData.getFinanceScheduleDetails();
		List<FinanceScheduleDetail> finSchdDtls = copy(schedules);

		rch.setRoleCode(roleCode);
		rch.setNextRoleCode(nextRoleCode);
		rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_REALIZED);

		if (StringUtils.equals(FinanceConstants.DEPOSIT_APPROVER, rch.getRoleCode()) || rd.isPresentment()) {
			rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_DEPOSITED);
		}

		if (!StringUtils.equals(ReceiptMode.CHEQUE, rch.getReceiptMode())) {
			rch.setRealizationDate(rch.getValueDate());
			rch.setReceivedDate(rch.getReceiptDate());
		}

		finReceiptHeaderDAO.generatedReceiptID(rch);
		rch.setPostBranch(auditHeader.getAuditBranchCode());
		rch.setReceiptDate(appDate);
		rch.setRcdMaintainSts(null);
		rch.setRoleCode("");
		rch.setNextRoleCode("");
		rch.setTaskId("");
		rch.setNextTaskId("");
		rch.setWorkflowId(0);
		rch.setActFinReceipt(fm.isFinIsActive());
		rch.setValueDate(rd.getValueDate());
		rch.setReceiptDate(appDate);

		if (rch.getReceiptMode() != null && rch.getSubReceiptMode() == null) {
			rch.setSubReceiptMode(rch.getReceiptMode());
		}

		rch.setBpiAmount(restructBpiAmount);

		if (rd.isDueAdjusted()) {
			scheduleData.setFinanceScheduleDetails(finSchdDtls);
		} else {
			scheduleData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false));
		}

		if (!StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, fm.getProductCategory())) {
			if (!fm.isSanBsdSchdle()) {
				int size = schedules.size();
				for (int i = size - 1; i >= 0; i--) {
					FinanceScheduleDetail curSchd = schedules.get(i);
					if (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0
							&& curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0) {
						fm.setMaturityDate(curSchd.getSchDate());
						break;
					} else if (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0
							&& curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0) {
						schedules.remove(i);
					}
				}
			}
		}

		Date curBusDate = appDate;
		Date valueDate = rch.getValueDate();

		List<FinanceScheduleDetail> schdList = schedules;
		pfd.setLpiAmount(rch.getLpiAmount());
		pfd.setGstLpiAmount(rch.getGstLpiAmount());
		pfd.setLppAmount(rch.getLppAmount());
		pfd.setGstLppAmount(rch.getGstLppAmount());

		if (fm.isAllowSubvention()) {
			BigDecimal subVentionAmt = this.subventionDetailDAO.getTotalSubVentionAmt(finID);
			pfd.setTotalSvnAmount(subVentionAmt);
		}

		if (rch.getReceiptDetails() != null && !rch.getReceiptDetails().isEmpty()) {
			for (int i = 0; i < rch.getReceiptDetails().size(); i++) {
				FinReceiptDetail receiptDetail = rch.getReceiptDetails().get(i);
				receiptDetail.getRepayHeader().setRepayID(financeRepaymentsDAO.getNewRepayID());
			}
		}

		List<Object> returnList = repayProcessUtil.doProcessReceipts(fm, schdList, pfd, rch,
				scheduleData.getFinFeeDetailList(), scheduleData, valueDate, curBusDate, fd);
		schdList = (List<FinanceScheduleDetail>) returnList.get(0);

		BigDecimal totPriPaid = BigDecimal.ZERO;
		for (FinReceiptDetail receiptDetail : rch.getReceiptDetails()) {
			FinRepayHeader repayHeader = receiptDetail.getRepayHeader();
			if (repayHeader.getRepayScheduleDetails() != null && !repayHeader.getRepayScheduleDetails().isEmpty()) {
				for (RepayScheduleDetail rpySchd : repayHeader.getRepayScheduleDetails()) {
					totPriPaid = totPriPaid.add(rpySchd.getPrincipalSchdPayNow().add(rpySchd.getPriSchdWaivedNow()));
				}
			}
		}

		fm.setFinRepaymentAmount(fm.getFinRepaymentAmount().add(totPriPaid));

		pfd.setAmzTillLBD(pfd.getAmzTillLBD().add((BigDecimal) returnList.get(1)));
		pfd.setLpiTillLBD(pfd.getLpiTillLBD().add((BigDecimal) returnList.get(2)));
		pfd.setGstLpiTillLBD(pfd.getGstLpiTillLBD().add((BigDecimal) returnList.get(3)));
		pfd.setLppTillLBD(pfd.getLppTillLBD().add((BigDecimal) returnList.get(4)));
		pfd.setGstLppTillLBD(pfd.getGstLppTillLBD().add((BigDecimal) returnList.get(5)));

		if (schdList == null) {
			schdList = schedules;
		}

		scheduleData.setFinanceScheduleDetails(schdList);
		Date reqMaxODDate = curBusDate;
		List<FinODDetails> overdueList = null;

		if (StringUtils.equals(FinServiceEvent.EARLYSETTLE, rch.getReceiptPurpose())) {
			reqMaxODDate = rch.getValueDate();
		}

		if (!ImplementationConstants.LPP_CALC_SOD) {
			reqMaxODDate = DateUtil.addDays(valueDate, -1);
		}

		overdueList = finODDetailsDAO.getFinODBalByFinRef(finID);

		overdueList = calPenalty(scheduleData, rd, reqMaxODDate, overdueList);
		if (overdueList != null && !overdueList.isEmpty()) {
			finODDetailsDAO.updateList(overdueList);
		}

		String tranType = PennantConstants.TRAN_UPD;
		rch.setRecordType("");

		rch.setRcdMaintainSts(null);
		rch.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		rch.setRecordType("");
		rch.setRoleCode("");
		rch.setNextRoleCode("");
		rch.setTaskId("");
		rch.setNextTaskId("");
		rch.setWorkflowId(0);

		repayProcessUtil.doSaveReceipts(rch, scheduleData.getFinFeeDetailList(), true);
		long receiptID = rch.getReceiptID();

		if (rch.getUserDetails() == null && SessionUserDetails.getLogiedInUser() != null) {
			rch.setUserDetails(SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser()));
		}

		saveDepositDetails(rch, PennantConstants.method_doApprove);
		BigDecimal prvMthAmz = pfd.getPrvMthAmz();

		boolean isPresentProc = false;
		if (rch.getReceiptDetails() != null && !rch.getReceiptDetails().isEmpty()) {
			for (FinReceiptDetail rcd : rch.getReceiptDetails()) {
				if (RepayConstants.PAYTYPE_PRESENTMENT.equals(rcd.getPaymentType())) {
					isPresentProc = true;
				}
			}
		}

		boolean finIsActive = fm.isFinIsActive();
		repayProcessUtil.updateStatus(fm, valueDate, schdList, pfd, overdueList, rch.getReceiptPurpose(),
				isPresentProc);
		if (finIsActive && !fm.isFinIsActive() && receiptPurpose == ReceiptPurpose.SCHDRPY
				&& (RepayConstants.PAYSTATUS_DEPOSITED.equals(rmStatus))) {

			fm.setFinIsActive(true);
			fm.setClosedDate(null);
			fm.setClosingStatus(null);

			pfd.setFinStatus(fm.getFinStatus());
			pfd.setFinStsReason(fm.getFinStsReason());
			pfd.setFinIsActive(fm.isFinIsActive());
			pfd.setClosingStatus(fm.getClosingStatus());
			pfd.setPrvMthAmz(prvMthAmz);

			profitDetailsDAO.update(pfd, true);
		}

		if ((FinanceConstants.CLOSE_STATUS_MATURED.equals(fm.getClosingStatus())
				|| FinanceConstants.CLOSE_STATUS_EARLYSETTLE.equals(fm.getClosingStatus()))
				&& !(ReceiptMode.PRESENTMENT.equals(receiptHeader.getReceiptMode()))) {
			fm.setClosedDate(valueDate);
		}

		User logiedInUser = SessionUserDetails.getLogiedInUser();
		if (logiedInUser != null) {
			fm.setLastMntBy(logiedInUser.getUserId());
			fm.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		}

		financeMainDAO.updateFromReceipt(fm, TableType.MAIN_TAB);

		listDeletion(finID, "");
		listSave(scheduleData, "", 0, false);

		if (scheduleData.getFinFeeDetailList() != null) {
			if (!FinServiceEvent.RESTRUCTURE.equals(rch.getReceiptPurpose())) {
				approveFees(rd, TableType.MAIN_TAB.getSuffix());
			}
		}

		if (scheduleData.getFinFeeDetailList() != null) {
			for (int i = 0; i < rch.getAllocations().size(); i++) {
				ReceiptAllocationDetail allocation = rch.getAllocations().get(i);
				if (StringUtils.equals(Allocation.FEE, allocation.getAllocationType())) {
					for (FinFeeDetail feeDtl : schdData.getFinFeeDetailList()) {
						if (feeDtl.getFeeTypeID() == -(allocation.getAllocationTo())) {
							allocation.setAllocationTo(feeDtl.getFeeID());
							break;
						}
					}
				}
			}
		}
		List<AuditDetail> tempAuditDetailList = new ArrayList<>();

		if (!StringUtils.equals(PennantConstants.FINSOURCE_ID_API, rd.getSourceId()) && !fd.isDirectFinalApprove()) {
			// Save Document Details
			if (CollectionUtils.isNotEmpty(fd.getDocumentDetailsList())) {
				listDocDeletion(fd, TableType.TEMP_TAB.getSuffix());
			}

			// set Check list details Audit
			// =======================================
			if (CollectionUtils.isNotEmpty(fd.getFinanceCheckList())) {
				auditDetails.addAll(checkListDetailService.doApprove(fd, "", serviceUID));
			}

			// Extended Field Details
			List<AuditDetail> extendedDetails = rd.getFinanceDetail().getAuditDetailMap().get("ExtendedFieldDetails");
			if (CollectionUtils.isNotEmpty(extendedDetails)) {
				extendedDetails = extendedFieldDetailsService.processingExtendedFieldDetailList(extendedDetails,
						ExtendedFieldConstants.MODULE_LOAN, rd.getFinanceDetail().getExtendedFieldHeader().getEvent(),
						"", serviceUID);
				auditDetails.addAll(extendedDetails);
			}

			// Extended field Extensions Details
			List<AuditDetail> extensionDetails = rd.getFinanceDetail().getAuditDetailMap()
					.get("ExtendedFieldExtension");
			if (CollectionUtils.isNotEmpty(extensionDetails)) {
				extensionDetails = extendedFieldExtensionService.processingExtendedFieldExtList(extensionDetails, rd,
						serviceUID, TableType.MAIN_TAB);

				auditDetails.addAll(extensionDetails);
			}

			// ScheduleDetails delete
			// =======================================
			listDeletion(finID, TableType.TEMP_TAB.getSuffix());

			// Checklist Details delete
			// =======================================
			tempAuditDetailList.addAll(checkListDetailService.delete(fd, TableType.TEMP_TAB.getSuffix(), tranType));

			financeRepaymentsDAO.deleteRpySchdList(finID, TableType.TEMP_TAB.getSuffix());
			financeRepaymentsDAO.deleteByRef(finID, TableType.TEMP_TAB);
			finReceiptDetailDAO.deleteByReceiptID(receiptID, TableType.TEMP_TAB);
			deleteTaxHeaderId(receiptID, TableType.TEMP_TAB.getSuffix());
			allocationDetailDAO.deleteByReceiptID(receiptID, TableType.TEMP_TAB);
			manualAdviseDAO.deleteMovementsByReceiptID(receiptID, TableType.TEMP_TAB.getSuffix());
			finReceiptHeaderDAO.deleteByReceiptID(receiptID, TableType.TEMP_TAB);

			if (CollectionUtils.isNotEmpty(extendedDetails)) {
				extendedDetails = extendedFieldDetailsService.delete(rd.getFinanceDetail().getExtendedFieldHeader(),
						fm.getFinReference(), rd.getFinanceDetail().getExtendedFieldRender().getSeqNo(), "_Temp",
						auditHeader.getAuditTranType(), extendedDetails);
				auditDetails.addAll(extendedDetails);
			}

			if (CollectionUtils.isNotEmpty(extensionDetails)) {
				extensionDetails = extendedFieldExtensionService.delete(extensionDetails, tranType, TableType.TEMP_TAB);
				auditDetails.addAll(extensionDetails);
			}

		}

		String[] rFields = PennantJavaUtil.getFieldDetails(new FinReceiptDetail(),
				rd.getReceiptHeader().getReceiptDetails().get(0).getExcludeFields());
		for (int i = 0; i < rd.getReceiptHeader().getReceiptDetails().size(); i++) {
			tempAuditDetailList.add(new AuditDetail(auditHeader.getAuditTranType(), 1, rFields[0], rFields[1], null,
					ard.getReceiptHeader().getReceiptDetails().get(i)));
		}

		String[] rhFields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(),
				rd.getReceiptHeader().getExcludeFields());
		auditHeader.setAuditDetail(
				new AuditDetail(auditHeader.getAuditTranType(), 1, rhFields[0], rhFields[1], null, rch));

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(tempAuditDetailList);
		auditHeader.setAuditModule("Receipt");
		auditHeaderDAO.addAudit(auditHeader);
		auditHeader.getAuditDetail().setModelData(rd);

		if (ard.getReceiptHeader().getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
			tranType = PennantConstants.TRAN_ADD;
		} else {
			tranType = PennantConstants.TRAN_UPD;
		}

		if (befFinReceiptDetail.isEmpty()) {
			for (int i = 0; i < rd.getReceiptHeader().getReceiptDetails().size(); i++) {
				auditDetails.add(new AuditDetail(tranType, 1, rFields[0], rFields[1], null,
						rd.getReceiptHeader().getReceiptDetails().get(i)));
			}
		} else {
			for (int i = 0; i < rd.getReceiptHeader().getReceiptDetails().size(); i++) {
				auditDetails.add(new AuditDetail(tranType, 1, rFields[0], rFields[1], befFinReceiptDetail.get(i),
						rd.getReceiptHeader().getReceiptDetails().get(i)));
			}
		}

		auditHeader
				.setAuditDetail(new AuditDetail(tranType, 1, rhFields[0], rhFields[1], befRch, rd.getReceiptHeader()));

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.setAuditDetails(auditDetails);
		auditHeader.setAuditModule("Receipt");

		auditHeader.getAuditDetail().setModelData(rd);

		if (FinanceConstants.CLOSE_STATUS_MATURED.equals(fm.getClosingStatus())) {
			if (ImplementationConstants.COLLATERAL_INTERNAL) {
				if (ImplementationConstants.COLLATERAL_DELINK_AUTO) {
					getCollateralAssignmentValidation().saveCollateralMovements(fm.getFinReference());
				}
			}
		}

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
			Customer customer = fd.getCustomerDetails().getCustomer();
			limitManagement.processLoanRepay(fm, customer, priAmt);
		} else {
			limitCheckDetails.doProcessLimits(fm, FinanceConstants.AMENDEMENT);
		}

		if (ImplementationConstants.DEPOSIT_PROC_REQ) {
			finReceiptHeaderDAO.updateDepositBranchByReceiptID(rch.getReceiptID(), rch.getUserDetails().getBranchCode(),
					"");
		}

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
		List<FinanceRepayments> repayments = financeRepaymentsDAO.getFinRepayList(finID);
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
		if (!StringUtils.equals(ReceiptMode.CASH, receiptHeader.getReceiptMode())
				&& !StringUtils.equals(ReceiptMode.CHEQUE, receiptHeader.getReceiptMode())
				&& !StringUtils.equals(ReceiptMode.DD, receiptHeader.getReceiptMode())) {
			return;
		}

		// If Cheque or DD Process , then on deposit process only these
		// executions should be done
		if (!StringUtils.equals(ReceiptMode.CASH, receiptHeader.getReceiptMode())) {
			if (!receiptHeader.isDepositProcess()) {
				return;
			}
		} else if (StringUtils.equals(ReceiptMode.CASH, receiptHeader.getReceiptMode())) {
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
			if (StringUtils.equals(ReceiptMode.CASH, rcptDetail.getPaymentType())
					|| StringUtils.equals(ReceiptMode.CHEQUE, rcptDetail.getPaymentType())
					|| StringUtils.equals(ReceiptMode.DD, rcptDetail.getPaymentType())) {

				if (!StringUtils.equals(ReceiptMode.CASH, rcptDetail.getPaymentType())) {
					partnerBankId = rcptDetail.getFundingAc();
					reqReceiptMode = CashManagementConstants.ACCEVENT_DEPOSIT_TYPE_CHEQUE_DD;
				} else {
					reqReceiptMode = CashManagementConstants.ACCEVENT_DEPOSIT_TYPE_CASH;
				}
				depositReqAmount = depositReqAmount.add(rcptDetail.getAmount());
				valueDate = rcptDetail.getReceivedDate();
			}
		}

		if (depositReqAmount.compareTo(BigDecimal.ZERO) > 0 && StringUtils.isNotBlank(reqReceiptMode)) {
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
	public AuditHeader doReversal(AuditHeader auditHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		// AuditHeader auditHeader = copy(aAuditHeader);
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
						auditHeader.getAuditBranchCode(), Allocation.BOUNCE);
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
		if (StringUtils.equals(ReceiptMode.CHEQUE, receiptHeader.getReceiptMode())
				|| StringUtils.equals(ReceiptMode.DD, receiptHeader.getReceiptMode())) {

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
			if (CollectionUtils.isNotEmpty(fd.getDocumentDetailsList())) {
				List<AuditDetail> details = fd.getAuditDetailMap().get("DocumentDetails");
				details = processingDocumentDetailsList(details, "", schdData.getFinanceMain(),
						receiptHeader.getReceiptPurpose(), serviceUID);
				auditDetails.addAll(details);
				listDocDeletion(fd, TableType.TEMP_TAB.getSuffix());
			}

			// set Check list details Audit
			// =======================================
			if (CollectionUtils.isNotEmpty(fd.getFinanceCheckList())) {
				auditDetails.addAll(checkListDetailService.doApprove(fd, "", serviceUID));
			}

			String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), fm.getExcludeFields());

			// ScheduleDetails delete
			// =======================================
			listDeletion(finID, TableType.TEMP_TAB.getSuffix());

			// Fee charges deletion
			List<AuditDetail> tempAuditDetailList = new ArrayList<>();

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
			FinReceiptData recData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
			List<AuditDetail> extendedDetails = recData.getFinanceDetail().getAuditDetailMap()
					.get("ExtendedFieldDetails");
			if (CollectionUtils.isNotEmpty(extendedDetails)) {
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

			FinReceiptData tempRepayData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
			FinanceMain tempfinanceMain = tempRepayData.getFinanceDetail().getFinScheduleData().getFinanceMain();
			auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
					tempfinanceMain.getBefImage(), tempfinanceMain));

			// Receipt Header Audit Details Preparation
			String[] rhFields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(),
					rceiptData.getReceiptHeader().getExcludeFields());
			tempAuditDetailList.add(new AuditDetail(auditHeader.getAuditTranType(), 1, rhFields[0], rhFields[1],
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

		List<AuditDetail> auditDetails = new ArrayList<>();
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

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		auditDetail.setErrorDetails(new ArrayList<>());
		FinReceiptData rd = (FinReceiptData) auditDetail.getModelData();
		FinReceiptHeader rch = rd.getReceiptHeader();
		FinanceDetail fd = rd.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceProfitDetail pd = schdData.getFinPftDeatil();

		List<FinReceiptHeader> list = finReceiptHeaderDAO.getLastMntOn(rch.getReceiptID());

		FinReceiptHeader oldFinReceiptHeader = rch.getBefImage();
		FinReceiptHeader tempFinReceiptHeader = null;
		FinReceiptHeader befFinReceiptHeader = null;

		for (FinReceiptHeader finReceiptHeader : list) {
			if (finReceiptHeader.getWorkflowId() == 1) {
				tempFinReceiptHeader = finReceiptHeader;
			}

			if (finReceiptHeader.getWorkflowId() == 0) {
				befFinReceiptHeader = finReceiptHeader;
			}
		}

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(rch.getReceiptID());
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (rch.isNewRecord()) {
			if (!rch.isWorkflow()) {
				if (befFinReceiptHeader != null) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else {
				if (rch.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
					if (befFinReceiptHeader != null || tempFinReceiptHeader != null) { // if
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else {
					if (befFinReceiptHeader == null || tempFinReceiptHeader != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			if (!rch.isWorkflow()) {

				if (befFinReceiptHeader == null) {
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

				if (tempFinReceiptHeader == null) {
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

		if (FinServiceEvent.EARLYSETTLE.equals(rch.getReceiptPurpose())) {
			boolean isPending = isReceiptsPending(rch.getFinID(), rch.getReceiptID());
			if (isPending) {
				valueParm[0] = "Not allowed to do Early Settlement due to previous Presentments/Receipts are in process";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("EXT001", valueParm)));
			}
		}

		int eodProgressCount = customerQueuingDAO.getProgressCountByCust(rch.getCustID());

		if (eodProgressCount > 0) {
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "60203", errParm, valueParm), usrLanguage));
		}

		if (!StringUtils.equals(method, PennantConstants.method_doReject)) {
			List<FinReceiptDetail> receiptDetailList = rd.getReceiptHeader().getReceiptDetails();
			for (FinReceiptDetail receiptDetail : receiptDetailList) {
				if (StringUtils.equals(rd.getReceiptHeader().getReceiptMode(), receiptDetail.getPaymentType())) {

					if (StringUtils.isNotEmpty(receiptDetail.getPaymentType())
							&& !StringUtils.equals(receiptDetail.getPaymentType(), PennantConstants.List_Select)) {

						doInstrumentValidation(rd);
					}
				}

			}

		}

		if (ImplementationConstants.VALIDATION_ON_CHECKER_APPROVER_ALLOWED) {
			doCheckerApproverValidation(auditDetail, usrLanguage, rd, rch);
		}

		if (!FinServiceEvent.SCHDRPY.equals(rch.getReceiptPurpose())) {
			String recordStatus = rch.getRecordStatus();
			String receiptModeSts = rch.getReceiptModeStatus();
			if (!RepayConstants.PAYSTATUS_BOUNCE.equals(receiptModeSts)
					&& !RepayConstants.PAYSTATUS_CANCEL.equals(receiptModeSts)) {
				if ((PennantConstants.RCD_STATUS_SAVED.equals(recordStatus)
						|| PennantConstants.RCD_STATUS_SUBMITTED.equals(recordStatus)
						|| PennantConstants.RCD_STATUS_APPROVED.equals(recordStatus))) {

					String[] parms = new String[2];
					if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOWED_BACKDATED_RECEIPT)) {
						Date prvSchdDate = pd.getPrvRpySchDate();
						if (rch.getValueDate().compareTo(prvSchdDate) < 0) {
							parms[0] = DateUtil.formatToLongDate(rch.getValueDate());
							parms[1] = DateUtil.formatToLongDate(prvSchdDate);
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("RU0012", parms)));
						}
					}
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !rch.isWorkflow()) {
			rch.setBefImage(befFinReceiptHeader);
		}

		return auditDetail;
	}

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
					value[0] = DateUtil.formatToLongDate(schDate);
					value[1] = DateUtil.formatToLongDate(receiptDate);
					value[2] = DateUtil.formatToLongDate(appDate);

					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30550", value), usrLanguage));
					break;
				}
			}
		}
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
			if (DateUtil.compare(toDate, receivedDate) >= 0) {
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

		List<AuditDetail> auditDetails = new ArrayList<>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();

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
		if (CollectionUtils.isNotEmpty(financeDetail.getDocumentDetailsList())) {
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
	public FinReceiptData calculateRepayments(FinReceiptData rd, boolean isPresentment) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = rd.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();

		rd.setBuildProcess("R");
		rd.getRepayMain().setRepayAmountNow(BigDecimal.ZERO);
		rd.getRepayMain().setPrincipalPayNow(BigDecimal.ZERO);
		rd.getRepayMain().setProfitPayNow(BigDecimal.ZERO);

		FinReceiptHeader rch = rd.getReceiptHeader();
		rch.setReceiptAmount(rch.getReceiptAmount());

		List<ReceiptAllocationDetail> allocations = rch.getAllocations();
		for (ReceiptAllocationDetail allocate : allocations) {
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

		List<FinanceScheduleDetail> finSchdDtls = copy(schedules);

		rd = receiptCalculator.initiateReceipt(rd, isPresentment);
		schdData.setFinanceScheduleDetails(finSchdDtls);

		logger.debug(Literal.LEAVING);
		return rd;
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
		List<FinanceRepayments> repayments = financeRepaymentsDAO.getFinRepayList(finID);

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
					if (DateUtil.compare(repayScheduleDetail.getSchDate(), schDate) == 0) {
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
			repayments.addAll(financeRepaymentsDAO.getFinRepayList(fm.getFinID()));
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
	public void doInstrumentValidation(FinReceiptData rd) {
		logger.debug(Literal.ENTERING);
		FinanceDetail fd = rd.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		FinReceiptHeader rch = rd.getReceiptHeader();

		String payMode = rch.getReceiptMode();

		BigDecimal amount = rch.getReceiptAmount().subtract(rd.getExcessAvailable());
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}

		String subReceiptMode = rch.getSubReceiptMode();
		if (ReceiptMode.ONLINE.equals(rch.getReceiptMode())) {
			payMode = subReceiptMode;
		}

		InstrumentwiseLimit instLimit = instrumentwiseLimitDAO.getInstrumentForLMSEvent(payMode);

		if (instLimit == null) {
			return;
		}

		Date appDate = fm.getAppDate();
		long custID = fm.getCustID();

		BigDecimal minReceiptTranLimit = instLimit.getReceiptMinAmtperTran();
		BigDecimal maxReceiptTranLimit = instLimit.getReceiptMaxAmtperTran();

		if (amount.compareTo(minReceiptTranLimit) < 0 || amount.compareTo(maxReceiptTranLimit) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = PennantApplicationUtil.amountFormate(maxReceiptTranLimit, PennantConstants.defaultCCYDecPos);
			valueParm[1] = PennantApplicationUtil.amountFormate(minReceiptTranLimit, PennantConstants.defaultCCYDecPos);
			setError(schdData, "RU0024", valueParm);
			return;
		}

		BigDecimal dayAmt = finReceiptDetailDAO.getReceiptAmountPerDay(appDate, payMode, custID);

		if (rd.isEnquiry()) {
			dayAmt = dayAmt.add(amount);
		}

		if (dayAmt.compareTo(instLimit.getReceiptMaxAmtperDay()) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = PennantApplicationUtil.amountFormate(maxReceiptTranLimit, PennantConstants.defaultCCYDecPos);
			valueParm[1] = StringUtils.isEmpty(subReceiptMode) ? subReceiptMode : rch.getReceiptMode();
			setError(schdData, "IWL0004", valueParm);
		}
	}

	private boolean isValidEarlySettleReason(Long earlySettlementReason) {
		if (earlySettlementReason != null && earlySettlementReason > 0) {
			String reasonCode = StringUtils.trimToEmpty(reasonCodeDAO.getReasonTypeCode(earlySettlementReason));
			if (!StringUtils.endsWithIgnoreCase(reasonCode, PennantConstants.REASON_CODE_EARLYSETTLEMENT)) {
				return false;
			}
		}

		return true;
	}

	private void doReceiptValidations(FinReceiptData rd) {
		FinanceDetail fd = rd.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();

		FinServiceInstruction fsi = toUpperCase(schdData.getFinServiceInstruction());
		ReceiptPurpose receiptPurpose = fsi.getReceiptPurpose();
		RequestSource requestSource = fsi.getRequestSource();

		FinReceiptHeader rch = rd.getReceiptHeader();
		FinReceiptDetail rcd = rch.getReceiptDetails().get(0);

		doDataValidations(rd);

		if (CollectionUtils.isNotEmpty(schdData.getErrorDetails())) {
			return;
		}

		schdData.setErrorDetails(dedupCheck(fsi));
		if (CollectionUtils.isNotEmpty(schdData.getErrorDetails())) {
			return;
		}

		if (requestSource == RequestSource.UPLOAD && !"Post".equals(fsi.getReqType())) {
			return;
		}

		validateEffectSchdMethod(rd);

		if (CollectionUtils.isNotEmpty(schdData.getErrorDetails())) {
			return;
		}

		String excessAdjustTo = rch.getExcessAdjustTo();
		if (receiptPurpose == ReceiptPurpose.SCHDRPY || receiptPurpose == ReceiptPurpose.EARLYSETTLE) {
			validateExcessAdjustTo(schdData, excessAdjustTo);
		}

		if (CollectionUtils.isNotEmpty(schdData.getErrorDetails())) {
			return;
		}

		validateAllocationType(schdData);

		if (CollectionUtils.isNotEmpty(schdData.getErrorDetails())) {
			return;
		}

		if (StringUtils.isNotBlank(rcd.getRemarks()) && 100 < rcd.getRemarks().length()) {
			setError(schdData, "RU0005");
			return;
		}

		BigDecimal receiptAmount = rch.getReceiptAmount();
		if (receiptPurpose == ReceiptPurpose.EARLYRPY && receiptAmount.compareTo(BigDecimal.ZERO) <= 0) {
			setError(schdData, "91121", "Amount:" + receiptAmount, "Zero");
			return;
		}

		validateExtendedFields(schdData);
	}

	private void setReceiptDataForEarlySettlement(FinReceiptData rd) {
		logger.info(Literal.ENTERING);

		FinanceDetail fd = rd.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		FinServiceInstruction fsi = schdData.getFinServiceInstruction();

		FinReceiptDetail rcd = fsi.getReceiptDetail();

		ReceiptPurpose receiptPurpose = fsi.getReceiptPurpose();

		BigDecimal amount = new BigDecimal(PennantApplicationUtil.amountFormate(fsi.getAmount(), 2).replace(",", ""));
		String receiptMode = fsi.getPaymentMode();

		if (!fm.isFinIsActive()) {
			fsi.setExcessAdjustTo(RepayConstants.EXAMOUNTTYPE_EXCESS);
		}

		FinReceiptHeader rch = rd.getReceiptHeader();

		rch.setReceiptAmount(amount);
		rch.setReceiptPurpose(receiptPurpose.code());

		if (ReceiptMode.CHEQUE.equals(receiptMode) || ReceiptMode.DD.equals(receiptMode)) {
			int defaultClearingDays = SysParamUtil.getValueAsInt(SMTParameterConstants.EARLYSETTLE_CHQ_DFT_DAYS);
			fsi.setValueDate(DateUtil.addDays(fsi.getReceivedDate(), defaultClearingDays));
		}

		rch.setReceiptDate(fsi.getReceivedDate());
		rch.setValueDate(fsi.getValueDate());
		rcd.setValueDate(fsi.getValueDate());
		rcd.setReceivedDate(fsi.getReceivedDate());
		rd.setReceiptHeader(rch);

		logger.info(Literal.LEAVING);
	}

	private BigDecimal getTotalDues(FinReceiptData receiptData) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();

		BigDecimal pastDues = rch.getTotalPastDues().getTotalDue();
		BigDecimal totalBounces = rch.getTotalBounces().getTotalDue();
		BigDecimal totalRcvAdvises = rch.getTotalRcvAdvises().getTotalDue();
		BigDecimal totalFees = rch.getTotalFees().getTotalDue();
		BigDecimal excessAvailable = receiptData.getExcessAvailable();

		BigDecimal totalDues = BigDecimal.ZERO;
		totalDues = totalDues.add(pastDues);
		totalDues = totalDues.add(totalBounces);
		totalDues = totalDues.add(totalRcvAdvises);
		totalDues = totalDues.add(totalFees);
		totalDues = totalDues.subtract(excessAvailable);

		return totalDues;
	}

	private void validateEarlySettlement(FinScheduleData schdData, FinReceiptData receiptData, Date valueDate) {
		FinanceDetail fd = receiptData.getFinanceDetail();
		FinScheduleData aSchdData = fd.getFinScheduleData();
		FinanceMain fm = aSchdData.getFinanceMain();
		FinanceType finType = aSchdData.getFinanceType();

		if (finType.isAlwCloBefDUe()) {
			return;
		}

		List<FinanceScheduleDetail> schedules = aSchdData.getFinanceScheduleDetails();
		Date firstInstDate = ScheduleCalculator.getFirstInstallmentDate(schedules);

		if (DateUtil.compare(valueDate, firstInstDate) < 0) {
			setError(schdData, "21005", "Not allowed to do Early Settlement before first installment");
		}

		BigDecimal totalDues = getTotalDues(receiptData);
		totalDues = PennantApplicationUtil.formateAmount(totalDues, CurrencyUtil.getFormat(fm.getFinCcy()));

		if (totalDues.compareTo(receiptData.getReceiptHeader().getReceiptAmount()) > 0) {
			setError(schdData, "RU0051");
			return;
		}
	}

	private void validateCheque(FinScheduleData schdData, String favourNumber) {
		favourNumber = StringUtils.trimToEmpty(favourNumber);

		if (StringUtils.isEmpty(favourNumber)) {
			setError(schdData, "90502", "Cheque Number");
			return;
		}

		if (favourNumber.length() > 6) {
			setError(schdData, "30508", "Cheque Number: " + favourNumber, "6");
			return;
		}

		String regex = PennantRegularExpressions.REGEX_NUMERIC;
		Pattern pattern = Pattern.compile(PennantRegularExpressions.getRegexMapper(regex));

		if (!pattern.matcher(favourNumber).matches()) {
			setError(schdData, "90405", favourNumber);
		}
	}

	@Override
	public ErrorDetail checkInprocessReceipts(long finID, ReceiptPurpose receiptPurpose) {
		List<FinReceiptHeader> inprocessReceipts = finReceiptHeaderDAO.getInprocessReceipts(finID);

		for (FinReceiptHeader rh : inprocessReceipts) {
			String receiptModeStatus = rh.getReceiptModeStatus();
			if ("B".equals(receiptModeStatus) || "C".equals(receiptModeStatus)) {
				continue;
			}

			String code = null;
			String description = null;

			switch (receiptPurpose) {
			case EARLYSETTLE:
				code = "90498";
				description = "Receipt For Early Settlement already Initiated and is in process";
				break;
			case EARLYRPY:
				code = "90499";
				description = "Receipt For Partial Settlement already Initiated and is in process";
				break;
			default:
				break;
			}

			if (receiptPurpose.code().equals(rh.getReceiptPurpose())) {
				return ErrorUtil.getError(code, "", description);
			}
		}

		return null;
	}

	private void checkInprocessReceipts(FinScheduleData schdData, ReceiptPurpose receiptPurpose) {
		if (!(receiptPurpose == ReceiptPurpose.EARLYRPY || receiptPurpose == ReceiptPurpose.EARLYSETTLE)) {
			return;
		}

		ErrorDetail error = checkInprocessReceipts(schdData.getFinID(), receiptPurpose);

		if (error != null) {
			schdData.getErrorDetails().add(error);
		}
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

	private void doBasicValidations(FinScheduleData schdData) {
		FinanceMain fm = schdData.getFinanceMain();
		FinServiceInstruction fsi = schdData.getFinServiceInstruction();
		RequestSource requestSource = fsi.getRequestSource();
		String instructstatus = fsi.getStatus();

		if (String.valueOf(fsi.getAmount()).length() > 18) {
			setError(schdData, "92021", "Amount exceeded the maximum range.");
			return;
		}

		ReceiptPurpose receiptPurpose = fsi.getReceiptPurpose();

		String receiptMode = fsi.getPaymentMode();
		String productCategory = fm.getProductCategory();

		boolean autoReceipt = ReceiptUtil.isAutoReceipt(receiptMode, productCategory);

		if (!(ReceiptMode.isValidReceiptMode(receiptMode) || autoReceipt)) {
			setError(schdData, "90281", "Receipt mode", ReceiptMode.getValidReceiptModes());
			return;
		}

		String receiptChannel = fsi.getReceiptChannel();
		if (!ReceiptMode.isValidReceiptChannel(receiptMode, receiptChannel)) {
			setError(schdData, "90281", "Channel", "OTC / MOB");
			return;
		}

		String subReceiptMode = fsi.getSubReceiptMode();
		if (ReceiptMode.ONLINE.equals(receiptMode) && !ReceiptMode.isValidSubReceiptMode(subReceiptMode)) {
			setError(schdData, "90281", "Sub Receipt Mode", ReceiptMode.getValidSubReceiptModes());
			return;
		}

		String allocationType = fsi.getAllocationType();
		if (!"A".equals(allocationType) && !"M".equals(allocationType)) {
			setError(schdData, "90281", "Allocation Type", "A/M");
			return;
		}

		String excessAdjustTo = fsi.getExcessAdjustTo();
		if (!RepayConstants.EXCESSADJUSTTO_EXCESS.equals(excessAdjustTo)
				&& !RepayConstants.EXCESSADJUSTTO_EMIINADV.equals(excessAdjustTo)
				&& !PennantConstants.List_Select.equals(excessAdjustTo)) {
			setError(schdData, "90281", "Excess Adjustment", "E/A");
			return;
		}

		String receivedFrom = fsi.getReceivedFrom();
		if (!RepayConstants.RECEIVED_CUSTOMER.equalsIgnoreCase(receivedFrom)
				&& !RepayConstants.RECEIVED_GOVT.equalsIgnoreCase(receivedFrom)) {
			String msg = RepayConstants.RECEIVED_CUSTOMER + "," + RepayConstants.RECEIVED_GOVT;
			setError(schdData, "90281", "Received From", msg);
			return;
		}

		checkInprocessReceipts(schdData, receiptPurpose);

		if (CollectionUtils.isNotEmpty(schdData.getErrorDetails())) {
			return;
		}

		if (ReceiptMode.CHEQUE.equals(receiptMode) || ReceiptMode.DD.equals(receiptMode)) {
			validateCheque(schdData, fsi.getReceiptDetail().getFavourNumber());
		}

		if (CollectionUtils.isNotEmpty(schdData.getErrorDetails())) {
			return;
		}

		if (receiptPurpose == ReceiptPurpose.EARLYSETTLE) {
			Date appDate = fm.getAppDate();
			Date waiverDate = feeWaiverHeaderDAO.getLastWaiverDate(fm.getFinID(), appDate, fsi.getValueDate());

			if (waiverDate != null) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtil.formatToLongDate(appDate);
				valueParm[1] = DateUtil.formatToLongDate(waiverDate);

				setError(schdData, "RU0099", valueParm[0], valueParm[1]);
				return;
			}
		}

		if (requestSource == RequestSource.UPLOAD) {
			validateUploadEvent(schdData, fsi, receiptMode);
		} else {
			return;
		}

		if (StringUtils.isBlank(instructstatus)) {
			setError(schdData, "90502", "Status");
		} else if (!RepayConstants.PAYSTATUS_APPROVED.equals(instructstatus)
				&& !RepayConstants.PAYSTATUS_REALIZED.equals(instructstatus)) {
			setError(schdData, "90298", "Status ",
					RepayConstants.PAYSTATUS_APPROVED + "," + RepayConstants.PAYSTATUS_REALIZED);
		} else if (!ReceiptMode.DD.equals(receiptMode) && !ReceiptMode.CHEQUE.equals(receiptMode)
				&& (RepayConstants.PAYSTATUS_REALIZED.equals(instructstatus))) {
			setError(schdData, "90298", "Status", RepayConstants.PAYSTATUS_APPROVED);
		}
	}

	private void validateReceiptData(final FinReceiptData rd) {
		logger.info(Literal.ENTERING);

		FinanceDetail fd = rd.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		FinServiceInstruction fsi = schdData.getFinServiceInstruction();

		String receiptMode = fsi.getPaymentMode();
		String productCategory = fm.getProductCategory();

		FinReceiptDetail rcd = rd.getReceiptHeader().getReceiptDetails().get(0);

		if (rcd.getFundingAc() <= 0 && !ReceiptMode.isFundingAccountReq(receiptMode)
				|| (ImplementationConstants.ALLOW_PARTNERBANK_FOR_RECEIPTS_IN_CASHMODE
						&& ReceiptMode.CASH.equals(receiptMode))) {
			setError(schdData, "90502", "Funding Account");
			logger.info(Literal.LEAVING);
			return;
		}

		if (ReceiptMode.CHEQUE.equals(receiptMode) || ReceiptMode.DD.equals(receiptMode)) {
			validateChequeOrDD(schdData, rcd.getBankCode(), rd.getValueDate());
		}

		if (RepayConstants.REQTYPE_INQUIRY.equals(fsi.getReqType())) {
			logger.info(Literal.LEAVING);
			return;
		}

		boolean autoReceipt = ReceiptUtil.isAutoReceipt(receiptMode, productCategory);
		if (!ReceiptMode.isOfflineMode(receiptMode) && !autoReceipt && StringUtils.isBlank(rcd.getTransactionRef())) {
			setError(schdData, "90281", "Transaction Reference");
		}

		logger.info(Literal.LEAVING);
	}

	private void validateChequeOrDD(FinScheduleData schdData, String bankCode, Date valueDate) {
		if (valueDate == null) {
			setError(schdData, "90502", "ValueDate");
			return;
		}

		if (StringUtils.isBlank(bankCode)) {
			setError(schdData, "90502", "Bank Code");
			return;
		}

		if (!bankDetailDAO.isBankCodeExits(bankCode)) {
			setError(schdData, "90224", "Bank Code", bankCode);
		}
	}

	private void validateUploadEvent(FinScheduleData schdData, FinServiceInstruction fsi, String receiptMode) {
		String errorCode = "90502";

		if (fsi.getValueDate() == null) {
			setError(schdData, errorCode, "Value Date");
			return;
		}

		if (!(ReceiptMode.CHEQUE.equals(receiptMode) || ReceiptMode.DD.equals(receiptMode))) {
			return;
		}

		if (fsi.getDepositDate() == null) {
			setError(schdData, errorCode, "Deposit Date");
			return;
		}

		if ((fsi.getValueDate().compareTo(fsi.getDepositDate()) != 0)) {
			setError(schdData, "RU0017");
			return;
		}

		if (fsi.getReceivedDate().compareTo(fsi.getDepositDate()) > 0) {
			setError(schdData, "RU0018");
		}
	}

	private void doDataValidations(FinReceiptData rd) {
		FinanceDetail fd = rd.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinServiceInstruction fsi = schdData.getFinServiceInstruction();
		FinanceMain fm = schdData.getFinanceMain();

		boolean autoReceipt = ReceiptUtil.isAutoReceipt(fsi.getPaymentMode(), fm.getFinCategory());

		long fundingAccount = fsi.getReceiptDetail().getFundingAc();
		fsi.setFundingAc(fundingAccount);
		String receiptMode = fsi.getPaymentMode();

		if (ReceiptMode.ONLINE.equals(receiptMode)) {
			receiptMode = fsi.getSubReceiptMode();
		}

		boolean alwCashMode = ImplementationConstants.ALLOW_PARTNERBANK_FOR_RECEIPTS_IN_CASHMODE;

		if (!autoReceipt && !ReceiptMode.isFundingAccountReq(receiptMode)
				|| (alwCashMode && ReceiptMode.CASH.equals(receiptMode))) {
			String receipts = AccountConstants.PARTNERSBANK_RECEIPTS;
			String finType = fm.getFinType();
			if (finTypePartnerBankDAO.getPartnerBankCount(finType, receiptMode, receipts, fundingAccount) <= 0) {
				setError(schdData, "RU0020");
				return;
			}
		}

		Date finStartDate = fm.getFinStartDate();

		boolean receiptUpload = fsi.isReceiptUpload();
		if (receiptUpload && fsi.getValueDate().compareTo(finStartDate) < 0) {
			setError(schdData, "RU0048");
			return;
		}

		if (StringUtils.isBlank(fsi.getEntity())) {
			fsi.setEntity(fm.getEntityCode());
		}

		if (FinanceConstants.PRODUCT_GOLD.equals(fm.getProductCategory())) {
			setError(schdData, "RU0002");
			return;
		}

		if (SysParamUtil.isAllowed(SMTParameterConstants.RECEIPT_CASH_PAN_MANDATORY)) {
			panValidation(schdData, fsi);
		}

		if (CollectionUtils.isNotEmpty(schdData.getErrorDetails())) {
			return;
		}

		if ((fsi.getTdsAmount().compareTo(BigDecimal.ZERO) > 0
				&& !PennantConstants.TDS_MANUAL.equalsIgnoreCase(fm.getTdsType()))) {
			setError(schdData, "RU00060");
			return;
		}

		validateDual(rd);
	}

	private void panValidation(FinScheduleData schdData, FinServiceInstruction fsi) {
		FinanceMain fm = schdData.getFinanceMain();

		int formatter = CurrencyUtil.getFormat(fm.getFinCcy());

		BigDecimal recAmount = PennantApplicationUtil.formateAmount(fsi.getAmount(), formatter);
		String cashPanLimit = SysParamUtil.getValueAsString(SMTParameterConstants.RECEIPT_CASH_PAN_LIMIT);
		BigDecimal cashLimit = new BigDecimal(cashPanLimit);

		String panNumber = fsi.getPanNumber();

		if (StringUtils.isEmpty(panNumber)) {
			if (recAmount.compareTo(cashLimit) > 0
					&& DisbursementConstants.PAYMENT_TYPE_CASH.equals(fsi.getPaymentMode())) {
				String valueParm = "PanNumber";
				setError(schdData, "30561", valueParm);
			}
		} else {
			String panRegex = PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_PANNUMBER);
			if (!Pattern.compile(panRegex).matcher(panNumber).matches()) {
				setError(schdData, "90251", panNumber);
			}
		}

	}

	private void validateExtendedFields(FinScheduleData schdData) {
		FinServiceInstruction fsi = schdData.getFinServiceInstruction();

		List<ExtendedField> apiExtendedFields = fsi.getExtendedDetails();
		List<ErrorDetail> errorDetailList = null;

		if (CollectionUtils.isNotEmpty(apiExtendedFields)) {
			String subModule = schdData.getFinanceType().getFinCategory();
			errorDetailList = extendedFieldDetailsService.validateExtendedFieldDetails(apiExtendedFields,
					ExtendedFieldConstants.MODULE_LOAN, subModule, FinServiceEvent.RECEIPT);
			schdData.setErrorDetails(errorDetailList);
			return;
		}
	}

	private void validateAllocationType(FinScheduleData schdData) {
		FinServiceInstruction fsi = schdData.getFinServiceInstruction();
		String allocationType = fsi.getAllocationType();
		boolean receiptResponse = fsi.isReceiptResponse();

		String param0 = "Allocation Type : " + allocationType;
		String param1 = null;

		ReceiptPurpose receiptPurpose = fsi.getReceiptPurpose();

		if (!AllocationType.MANUAL.equals(allocationType) && !AllocationType.AUTO.equals(allocationType)) {
			param1 = AllocationType.MANUAL + "," + AllocationType.AUTO;
		} else if (!AllocationType.AUTO.equals(allocationType) && (receiptPurpose == ReceiptPurpose.EARLYRPY)) {
			param1 = AllocationType.AUTO;
		} else if (receiptResponse && AllocationType.MANUAL.equals(allocationType)) {
			param1 = AllocationType.MANUAL;
		}

		if (param1 != null) {
			setError(schdData, "90337", param0, param1);
		}
	}

	private void validateExcessAdjustTo(FinScheduleData schdData, String excessAdjustTo) {
		if (StringUtils.isBlank(excessAdjustTo)) {
			setError(schdData, "90502", EXCESS_ADJUST_TO);
			return;
		}

		String excess = RepayConstants.EXAMOUNTTYPE_EXCESS;
		String emiInAdvance = RepayConstants.EXAMOUNTTYPE_EMIINADV;

		if (!excess.equals(excessAdjustTo) && !emiInAdvance.equals(excessAdjustTo)) {
			setError(schdData, "90337", EXCESS_ADJUST_TO + excessAdjustTo, excess + "," + emiInAdvance);
			return;
		}

		ReceiptPurpose receiptPurpose = schdData.getFinServiceInstruction().getReceiptPurpose();
		if (receiptPurpose == ReceiptPurpose.EARLYSETTLE && !excess.equals(excessAdjustTo)) {
			setError(schdData, "90337", EXCESS_ADJUST_TO + excessAdjustTo, excess);
		}
	}

	@Override
	public FinReceiptData doBusinessValidations(FinReceiptData receiptData, int methodCtg) {
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinServiceInstruction fsi = finScheduleData.getFinServiceInstruction();

		String finReference = fsi.getFinReference();
		String parm0 = null;

		if ("Inquiry".equals(fsi.getReqType())) {
			String txnReference = fsi.getTransactionRef();
			BigDecimal receiptAmount = fsi.getAmount();
			boolean dedupFound = finReceiptDetailDAO.isDuplicateReceipt(finReference, txnReference, receiptAmount);
			if (dedupFound) {
				parm0 = "Txn Reference: " + txnReference + " with Amount";
				setError(finScheduleData, "90273", parm0);
				return receiptData;
			}
		}

		logger.debug(Literal.LEAVING);
		return receiptData;
	}

	private void validateAllocations(FinReceiptData rd) {
		logger.info(Literal.ENTERING);

		FinanceDetail fd = rd.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		final FinServiceInstruction fsi = schdData.getFinServiceInstruction();

		List<UploadAlloctionDetail> ulAllocations = fsi.getUploadAllocationDetails();

		if (ulAllocations == null) {
			ulAllocations = new ArrayList<>();
		}

		String excessAdjustTo = fsi.getExcessAdjustTo().toUpperCase();
		BigDecimal receiptAmount = fsi.getAmount();
		ReceiptPurpose receiptPurpose = fsi.getReceiptPurpose();
		String allocationType = fsi.getAllocationType();

		boolean isAllocationFound = false;
		if (CollectionUtils.isNotEmpty(ulAllocations)) {
			isAllocationFound = true;
		}

		if (AllocationType.AUTO.equals(allocationType)) {
			if (isAllocationFound) {
				setError(schdData, "RU0028");
				logger.info(Literal.LEAVING);
				return;
			} else {
				logger.info(Literal.LEAVING);
				return;
			}
		}

		if (AllocationType.MANUAL.equals(allocationType) && receiptPurpose == ReceiptPurpose.EARLYSETTLE) {
			for (UploadAlloctionDetail alc : ulAllocations) {
				String upldAllocType = alc.getAllocationType();

				if (!("F".equals(upldAllocType) || "M".equals(upldAllocType) || "B".equals(upldAllocType))) {
					logger.info(Literal.LEAVING);
					continue;
				}

				String referenceCode = alc.getReferenceCode();
				if (StringUtils.isBlank(referenceCode)) {
					String parm0 = "for allocationItem: " + upldAllocType + ", referenceCode is mandatory";
					setError(schdData, "30550", parm0);
					logger.info(Literal.LEAVING);
					return;
				}

				if (StringUtils.isNotBlank(referenceCode) && feeTypeDAO.getFeeTypeId(referenceCode) == null) {
					setError(schdData, "90501", "referenceCode :" + referenceCode);
					logger.info(Literal.LEAVING);
					return;
				}
			}
		}

		if (!AllocationType.AUTO.equals(allocationType) && !isAllocationFound) {
			setError(schdData, "90502", "Manual Allocations");
			logger.info(Literal.LEAVING);
			return;
		}

		boolean isEMIFound = false;
		boolean isPriOrIntFound = false;
		boolean isExcessFound = false;
		boolean isFuturePayFound = false;

		for (int i = 0; i < ulAllocations.size(); i++) {
			UploadAlloctionDetail alc = ulAllocations.get(i);
			String alcType = alc.getAllocationType();
			String referenceCode = alc.getReferenceCode();
			if (StringUtils.isBlank(alcType)) {
				setError(schdData, "90502", "Allocation Type");
				logger.info(Literal.LEAVING);
				return;
			}

			if (RepayConstants.EXCESSADJUSTTO_EXCESS.equals(excessAdjustTo)
					&& RepayConstants.EXCESSADJUSTTO_EMIINADV.equals(alcType)) {
				setError(schdData, "90503", "Allocation Item");
				logger.info(Literal.LEAVING);
				return;
			} else if (RepayConstants.EXCESSADJUSTTO_EMIINADV.equals(excessAdjustTo)
					&& RepayConstants.EXCESSADJUSTTO_EXCESS.equals(alcType)) {
				setError(schdData, "90504", "Allocation Item");
				logger.info(Literal.LEAVING);
				return;
			}

			if (!isValidAllocType(alcType)) {
				setError(schdData, "90502", "Allocation Type");
				logger.info(Literal.LEAVING);
				return;
			}

			BigDecimal paidAmt = alc.getPaidAmount();
			if (paidAmt.compareTo(BigDecimal.ZERO) < 0) {
				String parm0 = PennantApplicationUtil.amountFormate(paidAmt, 2);
				setError(schdData, "RU0034", parm0, alcType);
				logger.info(Literal.LEAVING);
				return;
			}

			BigDecimal waivedAmt = alc.getWaivedAmount();
			if (waivedAmt.compareTo(BigDecimal.ZERO) < 0) {
				String parm0 = PennantApplicationUtil.amountFormate(waivedAmt, 2);
				setError(schdData, "RU0035", parm0, alcType);
				logger.info(Literal.LEAVING);
				return;
			}

			if (receiptPurpose.index() < 2 && waivedAmt.compareTo(BigDecimal.ZERO) > 0
					&& isValidBasicAllocType(alcType)) {
				setError(schdData, "RU0030", "Principal/Interest/EMI");
				logger.info(Literal.LEAVING);
				return;
			}

			for (int j = 0; j < ulAllocations.size(); j++) {
				UploadAlloctionDetail updAlloc = ulAllocations.get(j);

				String dupeAlcType = updAlloc.getAllocationType();
				String dupeReferenceCode = StringUtils.trimToEmpty(updAlloc.getReferenceCode());

				if ((j == i) || (("F".equals(dupeAlcType) && !dupeReferenceCode.equals(referenceCode))
						|| ("M".equals(dupeAlcType) && !dupeReferenceCode.equals(referenceCode))
						|| ("B".equals(dupeAlcType) && !dupeReferenceCode.equals(referenceCode)))) {
					continue;
				}

				if (StringUtils.equals(alcType, dupeAlcType)) {
					String parm0 = "Duplicate Allocations" + dupeAlcType;
					setError(schdData, "90273", parm0);
					logger.info(Literal.LEAVING);
					return;
				}
			}

			switch (alcType) {
			case "EM":
				isEMIFound = true;
				break;
			case "P":
			case "I":
				isPriOrIntFound = true;
				break;
			case "E":
			case "A":
				isExcessFound = true;
				break;
			case "FP":
			case "FI":
				isFuturePayFound = true;
				break;
			default:
				break;
			}
		}

		if (isEMIFound && isPriOrIntFound) {
			setError(schdData, "30511", "EMI AND (Principal / Interest) are mutually Exclusive");
			logger.info(Literal.LEAVING);
			return;
		}

		if (receiptPurpose == ReceiptPurpose.EARLYRPY && isExcessFound) {
			setError(schdData, "RU0043");
			logger.info(Literal.LEAVING);
			return;
		}

		if (receiptPurpose.index() < 2 && isFuturePayFound) {
			setError(schdData, "RU0044");
			logger.info(Literal.LEAVING);
			return;
		}

		BigDecimal totalWaivedAmt = BigDecimal.ZERO;
		BigDecimal allocatedAmount = BigDecimal.ZERO;

		for (UploadAlloctionDetail alc : ulAllocations) {
			if (isValidAllocType(alc.getAllocationType())) {
				totalWaivedAmt = totalWaivedAmt.add(alc.getWaivedAmount());
				allocatedAmount = allocatedAmount.add(alc.getPaidAmount().add(alc.getWaivedAmount()));
			}
		}

		allocatedAmount = allocatedAmount.subtract(totalWaivedAmt);

		if (allocatedAmount.compareTo(receiptAmount) != 0 && receiptPurpose != ReceiptPurpose.EARLYSETTLE) {
			String parm0 = PennantApplicationUtil.amountFormate(receiptAmount, 2);
			String parm1 = PennantApplicationUtil.amountFormate(allocatedAmount, 2);

			setError(schdData, "RU0042", parm0, parm1);
		}

		logger.info(Literal.LEAVING);
	}

	private boolean isValidBasicAllocType(String alcType) {
		switch (alcType) {
		case "P":
			return true;
		case "I":
			return true;
		case "E":
			return true;
		default:
			return false;
		}
	}

	private boolean isValidAllocType(String alcType) {
		if (isValidBasicAllocType(alcType)) {
			return true;
		}

		switch (alcType) {
		case "EM":
			return true;
		case "L":
			return true;
		case "O":
			return true;
		case "F":
			return true;
		case "M":
			return true;
		case "B":
			return true;
		case "FP":
			return true;
		case "FI":
			return true;
		case "A":
			return true;
		default:
			return false;
		}
	}

	@Override
	public void validateDual(FinReceiptData rd) {
		FinanceDetail fd = rd.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinServiceInstruction fsi = schdData.getFinServiceInstruction();
		FinReceiptDetail rcd = fsi.getReceiptDetail();
		FinReceiptHeader rch = rd.getReceiptHeader();

		if (rch.getReceiptDate() == null) {
			rch.setReceiptDate(rcd.getReceivedDate());
		}

		if (fsi.getValueDate() == null) {
			fsi.setValueDate(rch.getValueDate());
		}

		if (rch.getValueDate() == null) {
			rch.setValueDate(rcd.getValueDate());
		}

		validateClosingStatus(rd);

		validateMaturityDate(rd);

		validateValueDate(rd);

		if (CollectionUtils.isNotEmpty(schdData.getErrorDetails())) {
			return;
		}

		validateDatesForPayMode(rd);

		if (CollectionUtils.isNotEmpty(schdData.getErrorDetails())) {
			return;
		}

		doInstrumentValidation(rd);
	}

	private void validateDatesForPayMode(FinReceiptData rd) {
		FinanceDetail fd = rd.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinServiceInstruction fsi = schdData.getFinServiceInstruction();
		FinanceMain fm = schdData.getFinanceMain();
		FinanceProfitDetail pd = schdData.getFinPftDeatil();
		FinReceiptDetail rcd = fsi.getReceiptDetail();
		FinReceiptHeader rch = rd.getReceiptHeader();

		long finID = fm.getFinID();
		Date appDate = fm.getAppDate();
		Date valueDate = fsi.getValueDate();
		String valueDateFormat = DateUtil.formatToLongDate(valueDate);

		ReceiptPurpose receiptPurpose = fsi.getReceiptPurpose();
		if (receiptPurpose == ReceiptPurpose.SCHDRPY) {
			if (valueDate.compareTo(appDate) < 0) {
				int days = DateUtil.getDaysBetween(valueDate, appDate);
				int paramDays = SysParamUtil.getValueAsInt(SMTParameterConstants.ALW_SP_BACK_DAYS);
				if (days > paramDays) {
					setError(schdData, "RU0009", valueDateFormat, DateUtil.formatToLongDate(appDate),
							String.valueOf(paramDays));
					return;
				}
			}

			List<FinanceDisbursement> disbursementDetails = schdData.getDisbursementDetails();
			if (!disbursementDetails.isEmpty()) {
				Date disbDate = disbursementDetails.get(0).getDisbDate();
				if (DateUtil.compare(rcd.getReceivedDate(), disbDate) < 0) {
					setError(schdData, "RU0050", DateUtil.formatToLongDate(disbDate));
					return;
				}
			}
		}

		if (receiptPurpose == ReceiptPurpose.EARLYRPY || receiptPurpose == ReceiptPurpose.EARLYSETTLE) {
			Date befEomDate = DateUtil.addDays(DateUtil.getMonthStart(appDate), -1);
			if (valueDate.compareTo(befEomDate) < 0) {
				setError(schdData, "RU0010", valueDateFormat, DateUtil.formatToLongDate(befEomDate));
				return;
			}
		}

		if (receiptPurpose == ReceiptPurpose.EARLYSETTLE || receiptPurpose == ReceiptPurpose.EARLYSTLENQ) {
			Date lastServDate = finLogEntryDetailDAO.getMaxPostDate(finID);
			if (DateUtil.compare(valueDate, lastServDate) < 0) {
				setError(schdData, "RU0013", valueDateFormat, DateUtil.formatToLongDate(lastServDate));
				return;
			}

			String receiptModeSts = rch.getReceiptModeStatus();
			if (!RepayConstants.PAYSTATUS_BOUNCE.equals(receiptModeSts)
					&& !RepayConstants.PAYSTATUS_CANCEL.equals(receiptModeSts)) {
				Date prvSchdDate = pd.getPrvRpySchDate();
				if (valueDate.compareTo(prvSchdDate) < 0) {
					setError(schdData, "RU0012", valueDateFormat, DateUtil.formatToLongDate(prvSchdDate));
					return;
				}
			}

			Date lastReceivedDate = finReceiptDetailDAO.getMaxReceivedDate(finID);

			if (DateUtil.compare(valueDate, lastReceivedDate) < 0) {
				setError(schdData, "RU0011", valueDateFormat, DateUtil.formatToLongDate(lastReceivedDate));
			}
		}
	}

	private void validateEffectSchdMethod(FinReceiptData rd) {
		FinanceDetail fd = rd.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinServiceInstruction fsi = schdData.getFinServiceInstruction();

		String effectSchdMethod = rd.getReceiptHeader().getEffectSchdMethod();

		ReceiptPurpose receiptPurpose = fsi.getReceiptPurpose();

		if (StringUtils.isBlank(effectSchdMethod) || PennantConstants.List_Select.equals(effectSchdMethod)) {
			if (receiptPurpose == ReceiptPurpose.SCHDRPY) {
				return;
			}

			if (receiptPurpose == ReceiptPurpose.EARLYSETTLE) {
				return;
			}
		}

		List<ValueLabel> recalMethods = new ArrayList<>();

		if (receiptPurpose == ReceiptPurpose.RESTRUCTURE) {
			recalMethods.addAll(PennantStaticListUtil.getRecalTypeList());
		} else {
			recalMethods.addAll(getEarlyPaySchdMethods(fd, fsi.getValueDate()));
		}

		validateRecalMethods(schdData, recalMethods, effectSchdMethod);
	}

	private void validateRecalMethods(FinScheduleData schdData, List<ValueLabel> recalMethods, String recalType) {
		recalType = StringUtils.trimToEmpty(recalType);

		List<String> methodsList = new ArrayList<>();
		boolean found = false;

		for (ValueLabel valueLabel : recalMethods) {
			String mthd = valueLabel.getValue();
			methodsList.add(mthd);
			if (recalType.equals(mthd)) {
				found = true;
				break;
			}
		}

		if (!found) {
			setError(schdData, "90281", "Recal type code", methodsList.toString());
		}
	}

	private void validateClosingStatus(FinReceiptData rd) {
		FinanceDetail fd = rd.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinServiceInstruction fsi = schdData.getFinServiceInstruction();
		FinanceMain fm = schdData.getFinanceMain();

		String closingStatus = fm.getClosingStatus();

		if (StringUtils.isEmpty(closingStatus) || fm.isWriteoffLoan()) {
			return;
		}

		ReceiptPurpose receiptPurpose = fsi.getReceiptPurpose();
		if (receiptPurpose == ReceiptPurpose.EARLYRPY || receiptPurpose == ReceiptPurpose.EARLYSETTLE) {
			setError(schdData, "RU0043", fm.getFinReference());
			return;
		}

		String excessAdjustTo = fsi.getExcessAdjustTo().toUpperCase();
		if (StringUtils.isBlank(excessAdjustTo)) {
			setError(schdData, "90502", EXCESS_ADJUST_TO);
			return;
		}

		if (RepayConstants.EXCESSADJUSTTO_EMIINADV.equals(excessAdjustTo)) {
			setError(schdData, "90337", EXCESS_ADJUST_TO + fsi.getExcessAdjustTo(),
					RepayConstants.EXCESSADJUSTTO_EXCESS);
		}
	}

	private void validateMaturityDate(FinReceiptData rd) {
		FinanceDetail fd = rd.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinServiceInstruction fsi = schdData.getFinServiceInstruction();
		FinanceMain fm = schdData.getFinanceMain();
		FinReceiptDetail rcd = fsi.getReceiptDetail();

		Date appDate = fm.getAppDate();

		Date maturityDate = fm.getMaturityDate();
		boolean normalLoanClosure = fsi.isNormalLoanClosure();
		boolean foreClosure = rd.isForeClosure();

		if (rcd.getReceivedDate().compareTo(appDate) > 0) {
			setError(schdData, "RU0006", DateUtil.formatToLongDate(appDate));
			return;
		}

		ReceiptPurpose receiptPurpose = fsi.getReceiptPurpose();

		if (receiptPurpose != ReceiptPurpose.SCHDRPY && maturityDate.compareTo(appDate) < 0 && !normalLoanClosure
				&& !foreClosure) {
			setError(schdData, "RU0000", receiptPurpose.code());
		}
	}

	private void validateValueDate(FinReceiptData rd) {
		FinanceDetail fd = rd.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinServiceInstruction fsi = schdData.getFinServiceInstruction();
		FinanceMain fm = schdData.getFinanceMain();
		FinReceiptHeader rch = rd.getReceiptHeader();

		Date appDate = fm.getAppDate();

		String receiptMode = rch.getReceiptMode();

		ReceiptPurpose receiptPurpose = fsi.getReceiptPurpose();

		if (receiptPurpose == ReceiptPurpose.EARLYSETTLE
				&& (ReceiptMode.CHEQUE.equals(receiptMode) || ReceiptMode.DD.equals(receiptMode))) {
			int maxChqClearingDays = SysParamUtil.getValueAsInt(SMTParameterConstants.EARLYSETTLE_CHQ_CLR_DAYS);
			int clearingDays = DateUtil.getDaysBetween(fsi.getValueDate(), appDate);
			if (clearingDays > maxChqClearingDays) {
				setError(schdData, "90913", String.valueOf(clearingDays));
			}
		} else {
			if (fsi.getValueDate().compareTo(appDate) > 0
					&& SysParamUtil.isAllowed(SMTParameterConstants.ALLOWED_BACKDATED_RECEIPT)) {
				setError(schdData, "RU0007", DateUtil.formatToLongDate(appDate));
			}
		}
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
	private Date getDerivedValueDate(FinReceiptData rd, FinServiceInstruction fsi, Date appDate) {
		FinanceDetail fd = rd.getFinanceDetail();

		if (fd == null) {
			return null;
		}

		FinScheduleData schData = fd.getFinScheduleData();
		List<FinanceScheduleDetail> list = schData.getFinanceScheduleDetails();
		Date receivedDate = fsi.getReceiptDetail().getReceivedDate();

		StringBuilder builder = new StringBuilder();

		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		for (FinanceScheduleDetail sch : list) {
			if (appDate.compareTo(sch.getSchDate()) >= 0 && receivedDate.compareTo(sch.getSchDate()) <= 0) {
				break;
			}
			if (!sch.isSchPriPaid() || !sch.isSchPftPaid()) {
				builder.append(sch.getSchDate().toString()).append(",");
			}
		}
		if (builder.length() > 0) {
			setError(schData, "90356", builder.toString());
			return null;
		}

		for (FinanceScheduleDetail sch : list) {
			if (appDate.compareTo(sch.getSchDate()) >= 0 && receivedDate.compareTo(sch.getSchDate()) <= 0) {
				return sch.getSchDate();
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

		for (UploadAlloctionDetail ulAlc : ulAllocations) {
			String ulAlcType = ulAlc.getAllocationType();

			if (StringUtils.equals(ulAlcType, "EM")) {
				if (ulAlc.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0) {
					parm0 = ulAlcType;
					parm1 = PennantApplicationUtil.amountFormate(ulAlc.getWaivedAmount(), 2);
					setError(finScheduleData, "RU0038", parm0, parm1);
					return receiptData;
				}
				BigDecimal emiAmount = ulAlc.getPaidAmount();
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
				case Allocation.PFT:
					alcType = "I";
					pftIdx = j;
					break;
				case Allocation.PRI:
					alcType = "P";
					priIdx = j;
					break;
				case Allocation.LPFT:
					alcType = "L";
					break;
				case Allocation.FEE:
					alcType = "F";
					break;
				case Allocation.ODC:
					alcType = "O";
					break;
				case Allocation.FUT_PFT:
					alcType = "FI";
					fnpftPaid = allocate.getPaidAmount();
					fPftIdx = j;
					break;
				case Allocation.FUT_PRI:
					alcType = "FP";
					break;
				case Allocation.EMI:
					alcType = "EM";
					emiInx = j;
					break;
				case Allocation.MANADV:
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

				if ("I".equals(ulAlcType)) {
					pftPaid = pftPaid.add(ulAlc.getPaidAmount());
					emiWaivedAmt = emiWaivedAmt.add(ulAlc.getWaivedAmount());
				}

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
					setError(finScheduleData, "RU0038", parm0, parm1);
					return receiptData;
				}

				if (ulAlc.getPaidAmount().add(ulAlc.getWaivedAmount()).compareTo(allocate.getTotalDue()) > 0) {
					parm0 = alcType;
					parm1 = PennantApplicationUtil.amountFormate(allocate.getTotalDue(), 2);
					setError(finScheduleData, "RU0038", parm0, parm1);
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

	private boolean dedupCheckRequest(FinReceiptHeader rch) {
		String receiptMode = rch.getReceiptMode();
		boolean cheqOrDD = ReceiptMode.CHEQUE.equals(receiptMode) || ReceiptMode.DD.equals(receiptMode);

		List<FinReceiptHeader> receipts = finReceiptDetailDAO.getReceiptsForDuplicateCheck(rch.getFinID());

		String receiptPurpose = rch.getReceiptPurpose();
		String transactionRef = rch.getTransactionRef();
		String bankCode = rch.getBankCode();

		for (FinReceiptHeader hdr : receipts) {
			if ("C".equals(hdr.getReceiptModeStatus())) {
				continue;
			}

			String rp = hdr.getReceiptPurpose();
			String rm = hdr.getReceiptMode();
			String tr = hdr.getTransactionRef();

			if (receiptPurpose.equals(rp) && receiptMode.equals(rm) && transactionRef.equals(tr)) {
				if (!cheqOrDD) {
					return true;
				}

				if (bankCode.equals(hdr.getBankCode())) {
					return true;
				}
			}
		}

		return false;
	}

	private long checkDedupSP(FinReceiptHeader rh) {
		String receiptMode = rh.getReceiptMode();
		FinReceiptDetail rcd = rh.getReceiptDetails().get(0);

		String receiptPurpose = rh.getReceiptPurpose();

		if (FinServiceEvent.SCHDRPY.equals(receiptPurpose)
				&& (ReceiptMode.CHEQUE.equals(receiptMode) || ReceiptMode.DD.equals(receiptMode))
				&& RepayConstants.PAYSTATUS_REALIZED.equals(rcd.getStatus())) {
			return finReceiptDetailDAO.getReceiptIDForSP(rh);
		}

		return 0;
	}

	private void setDefaults(FinServiceInstruction fsi) {
		FinReceiptDetail rd = fsi.getReceiptDetail();
		if (rd != null) {
			rd.setReceivedDate(DateUtil.getDatePart(rd.getReceivedDate()));
			fsi.setBankCode(rd.getBankCode());
		}

		ReceiptPurpose receiptPurpose = fsi.getReceiptPurpose();

		if (StringUtils.isBlank(fsi.getExcessAdjustTo())) {
			fsi.setExcessAdjustTo(PennantConstants.List_Select);
			if (receiptPurpose == ReceiptPurpose.SCHDRPY || receiptPurpose == ReceiptPurpose.EARLYSETTLE) {
				fsi.setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_EXCESS);
			}
		}

		if (StringUtils.isBlank(fsi.getReceivedFrom())) {
			fsi.setReceivedFrom(RepayConstants.RECEIVED_CUSTOMER);
		}

		if (StringUtils.isBlank(fsi.getAllocationType())) {
			fsi.setAllocationType(AllocationType.AUTO);
		}

		fsi.setFromDate(DateUtil.getDatePart(fsi.getFromDate()));
		fsi.setToDate(DateUtil.getDatePart(fsi.getToDate()));
		fsi.setRecalFromDate(DateUtil.getDatePart(fsi.getRecalFromDate()));
		fsi.setRecalToDate(DateUtil.getDatePart(fsi.getRecalToDate()));
		fsi.setGrcPeriodEndDate(DateUtil.getDatePart(fsi.getGrcPeriodEndDate()));
		fsi.setNextGrcRepayDate(DateUtil.getDatePart(fsi.getNextGrcRepayDate()));
		fsi.setNextRepayDate(DateUtil.getDatePart(fsi.getNextRepayDate()));
		fsi.setReceivedDate(DateUtil.getDatePart(fsi.getReceivedDate()));
		fsi.setValueDate(DateUtil.getDatePart(fsi.getValueDate()));
		fsi.setDepositDate(DateUtil.getDatePart(fsi.getDepositDate()));
		fsi.setRealizationDate(DateUtil.getDatePart(fsi.getRealizationDate()));
		fsi.setInstrumentDate(DateUtil.getDatePart(fsi.getInstrumentDate()));
	}

	public List<ValueLabel> getEarlyPaySchdMethods(FinanceDetail fd, Date valueDate) {
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		FinanceType ft = schdData.getFinanceType();

		if (StringUtils.isEmpty(ft.getAlwEarlyPayMethods())) {
			return new ArrayList<>();
		}

		List<ValueLabel> epyMethodList = new ArrayList<>();

		String[] epMthds = ft.getAlwEarlyPayMethods().trim().split(",");

		if (epMthds.length <= 0) {
			return epyMethodList;
		}

		List<String> list = Arrays.asList(epMthds);
		List<ValueLabel> earlyPayEffectOn = PennantStaticListUtil.getEarlyPayEffectOn();

		boolean developerFinance = ft.isDeveloperFinance();
		boolean validEarlyPayMethod = isValidEarlyPayMethod(valueDate, fm);
		boolean flexi = FinanceConstants.PRODUCT_HYBRID_FLEXI.equals(ft.getFinTypeClassification());

		for (ValueLabel epMthd : earlyPayEffectOn) {
			String mthd = epMthd.getValue().trim();
			if (!list.contains(mthd)) {
				continue;
			}
			if (CalculationConstants.RPYCHG_STEPPOS.equals(mthd) && validEarlyPayMethod) {
				epyMethodList.add(epMthd);
			} else {
				if (flexi) {
					epyMethodList.add(epMthd);
				} else if (CalculationConstants.EARLYPAY_PRIHLD.equals(mthd)) {
					if (developerFinance) {
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

		return epyMethodList;
	}

	private boolean isValidEarlyPayMethod(Date valueDate, FinanceMain fm) {
		return fm.isStepFinance() && fm.isAllowGrcPeriod() && FinanceConstants.STEPTYPE_PRIBAL.equals(fm.getStepType())
				&& DateUtil.compare(valueDate, fm.getGrcPeriodEndDate()) <= 0
				&& (CalculationConstants.SCHMTHD_PRI.equals(fm.getScheduleMethod())
						|| CalculationConstants.SCHMTHD_PRI_PFT.equals(fm.getScheduleMethod()));
	}

	@Override
	public FinServiceInstruction buildFinServiceInstruction(ReceiptUploadDetail rud, String entity) {
		FinServiceInstruction fsi = new FinServiceInstruction();
		String reference = rud.getReference();
		String chequeNo = rud.getChequeNo();

		Long finID = financeMainDAO.getFinIDByFinReference(reference, "", false);

		if (finID == null) {
			setErrorToRUD(rud, "RU0004", reference);
		} else {
			rud.setFinID(finID);
		}
		fsi.setFinID(rud.getFinID());
		fsi.setFinReference(reference);
		fsi.setExternalReference(rud.getExtReference());
		fsi.setModule("Receipts");
		fsi.setValueDate(rud.getValueDate());
		fsi.setAmount(rud.getReceiptAmount());
		fsi.setAllocationType(rud.getAllocationType());

		if (StringUtils.isNotEmpty(rud.getFundingAc())) {
			fsi.setFundingAc(Long.parseLong(rud.getFundingAc()));
		}

		fsi.setPaymentRef(rud.getPaymentRef());
		String favourNumber = rud.getFavourNumber();
		fsi.setFavourNumber(favourNumber);
		fsi.setRecalType(rud.getEffectSchdMethod());
		fsi.setFavourNumber(favourNumber);
		fsi.setBankCode(rud.getBankCode());
		fsi.setChequeNo(chequeNo);
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
		fsi.setLoggedInUser(rud.getLoggedInUser());

		if ("A".equals(rud.getStatus())) {
			fsi.setReceiptdetailExits(false);
		} else {
			String mode = rud.getReceiptMode();
			if ((ReceiptMode.CHEQUE.equalsIgnoreCase(mode) || ReceiptMode.DD.equalsIgnoreCase(mode))
					&& RepayConstants.PAYSTATUS_REALIZED.equalsIgnoreCase(rud.getStatus())) {

				String type = "";
				if (!"SP".equalsIgnoreCase(rud.getReceiptPurpose())) {
					type = "_Temp";
				}

				if (finReceiptHeaderDAO.isReceiptDetailsExits(reference, mode, chequeNo, favourNumber, type)) {
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

		rcd.setChequeAcNo(chequeNo);

		if (StringUtils.isNotEmpty(rud.getFundingAc())) {
			rcd.setFundingAc(Long.parseLong(rud.getFundingAc()));
		}
		rcd.setReceivedDate(rud.getReceivedDate());
		rcd.setStatus(fsi.getStatus());
		rcd.setRemarks(rud.getRemarks());
		rcd.setReference(reference);

		String receiptPurpose = null;
		switch (rud.getReceiptPurpose()) {
		case "SP":
			receiptPurpose = FinServiceEvent.SCHDRPY;
			break;
		case "EP":
			receiptPurpose = FinServiceEvent.EARLYRPY;
			break;
		case "ES":
			receiptPurpose = FinServiceEvent.EARLYSETTLE;
			break;
		default:
			break;
		}

		fsi.setReceiptPurpose(receiptPurpose);
		rcd.setReceiptPurpose(receiptPurpose);
		fsi.setReceiptDetail(rcd);

		if (fsi.getFinID() <= 0) {
			fsi.setFinID(rud.getFinID() == null ? 0 : rud.getFinID());
		}
		return fsi;
	}

	public void setErrorToRUD(ReceiptUploadDetail rud, String errorCode, String parm0) {
		String[] valueParm = new String[1];
		valueParm[0] = parm0;
		rud.getErrorDetails().add(ErrorUtil.getErrorDetail(new ErrorDetail(errorCode, "", valueParm)));
	}

	@Override
	public BigDecimal getClosingBalance(long finID, Date valueDate) {
		return financeScheduleDetailDAO.getClosingBalance(finID, valueDate);
	}

	@Override
	public Date getManualAdviseMaxDate(long finID, Date valueDate) {
		return manualAdviseDAO.getManualAdviseDate(finID, valueDate, "", FinanceConstants.MANUAL_ADVISE_RECEIVABLE);
	}

	public void setFinReceiptData(FinReceiptData rd) {

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
		if (FinServiceEvent.SCHDRPY.equals(rch.getReceiptPurpose())
				&& (RepayConstants.PAYSTATUS_BOUNCE.equals(rch.getReceiptModeStatus())
						|| RepayConstants.PAYSTATUS_CANCEL.equals(rch.getReceiptModeStatus()))
				&& SysParamUtil.isAllowed(SMTParameterConstants.CHQ_RECEIPTS_PAID_AT_DEPOSIT_APPROVER)) {
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
						&& (ReceiptMode.CHEQUE.equals(receiptMode) || ReceiptMode.DD.equals(receiptMode))
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

		getInProcessReceiptData(rd);

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
				if (StringUtils.equals(Allocation.FEE, allocation.getAllocationType())) {
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
		fd.setFinanceTaxDetail(financeTaxDetailDAO.getFinanceTaxDetailForLMSEvent(finID));

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
		int receiptPurposeCtg = ReceiptUtil.getReceiptPurpose(rch.getReceiptPurpose());

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

	private void recalEarlyPay(FinReceiptData rd) {
		FinReceiptHeader rch = rd.getReceiptHeader();
		FinanceDetail fd = rd.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		ReceiptPurpose receiptPurpose = ReceiptPurpose.purpose(rch.getReceiptPurpose());

		RepayMain repayMain = rd.getRepayMain();

		String method = null;
		if (receiptPurpose == ReceiptPurpose.EARLYRPY) {
			method = rch.getEffectSchdMethod();
			repayMain.setEarlyPayAmount(rd.getRemBal());
		} else if (receiptPurpose == ReceiptPurpose.EARLYSETTLE) {
			method = CalculationConstants.EARLYPAY_ADJMUR;
			repayMain.setEarlyPayAmount(repayMain.getCurFinAmount());
		}

		if (fm.isStepFinance()
				&& (StringUtils.isNotBlank(fm.getStepPolicy()) || (fm.isAlwManualSteps() && fm.getNoOfSteps() > 0))
				&& SysParamUtil.isAllowed(SMTParameterConstants.STEP_LOAN_SERVICING_REQ)) {
			List<RepayInstruction> rpst = schdData.getRepayInstructions();
			if (repayMain.getEarlyPayOnSchDate().compareTo(fm.getGrcPeriodEndDate()) < 0) {
				fm.setRecalSteps(true);
				fm.setRecalType(CalculationConstants.RPYCHG_STEPINST);
				for (RepayInstruction repayInstruction : rpst) {
					if (repayInstruction.getRepayDate().compareTo(fm.getGrcPeriodEndDate()) > 0) {
						fm.setRecalFromDate(repayInstruction.getRepayDate());
						break;
					}
				}
			}
		}

		ScheduleCalculator.recalEarlyPaySchedule(schdData, repayMain.getEarlyPayOnSchDate(), null,
				repayMain.getEarlyPayAmount(), method);
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

	public FinReceiptData recalculateReceipt(FinReceiptData rd) {
		if (rd.isPresentment()) {
			return rd;
		}
		FinReceiptHeader rch = rd.getReceiptHeader();
		String eventCode = "";

		if (!PennantConstants.RECORD_TYPE_NEW.equals(rch.getRecordType())) {
			rd.setCalReq(false);
		}

		ReceiptPurpose peceiptPurpose = ReceiptPurpose.purpose(rch.getReceiptPurpose());
		switch (peceiptPurpose) {
		case SCHDRPY:
			eventCode = AccountingEvent.REPAY;
			break;
		case EARLYRPY:
			eventCode = AccountingEvent.EARLYPAY;
			break;
		case EARLYSETTLE:
			eventCode = AccountingEvent.EARLYSTL;
			break;
		default:
			break;
		}

		FinanceDetail fd = rd.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();

		List<FinanceScheduleDetail> finSchdDtls = copy(schdData.getFinanceScheduleDetails());
		FinanceMain fm = schdData.getFinanceMain();

		if (FinanceConstants.CLOSE_STATUS_WRITEOFF.equals(fm.getClosingStatus())) {
			eventCode = AccountingEvent.WRITEBK;
		}

		rd.setValueDate(rch.getValueDate());
		rch.setReceiptDate(fm.getAppDate());
		rd.setAllocList(rch.getAllocations());
		rd.setForeClosure(rd.isForeClosure());

		if (!AllocationType.MANUAL.equals(rch.getAllocationType())) {
			rd = receiptCalculator.recalAutoAllocation(rd, false);
		}

		if (peceiptPurpose == ReceiptPurpose.EARLYSETTLE) {
			boolean duesAdjusted = checkDueAdjusted(rch.getAllocations(), rd);
			if (!duesAdjusted) {
				if (rd.isForeClosure()) {
					rd.getErrorDetails().add(ErrorUtil.getErrorDetail(new ErrorDetail("FC0000")));
					return rd;
				} else {
					adjustToExcess(rd);
					rd.setDueAdjusted(false);
					return rd;
				}
			}
		}

		if (peceiptPurpose == ReceiptPurpose.EARLYRPY && !fm.isSimulateAccounting()) {
			recalEarlyPay(rd);
		}

		BigDecimal pastDues = receiptCalculator.getTotalNetPastDue(rd);
		rd.setTotalPastDues(pastDues);
		for (FinReceiptDetail rcd : rd.getReceiptHeader().getReceiptDetails()) {
			rd = updateExcessPay(rd, payType(rcd.getPaymentType()), rcd.getPayAgainstID(), rcd.getAmount());
			if (rd.getTotalPastDues().compareTo(rcd.getAmount()) >= 0) {
				rcd.setDueAmount(rcd.getAmount());
				rd.setTotalPastDues(rd.getTotalPastDues().subtract(rcd.getAmount()));
			} else {
				rcd.setDueAmount(rd.getTotalPastDues());
				rd.setTotalPastDues(BigDecimal.ZERO);
			}
		}

		if (peceiptPurpose == ReceiptPurpose.EARLYRPY) {
			receiptCalculator.addPartPaymentAlloc(rd);
		}

		rd.setBuildProcess("R");
		for (ReceiptAllocationDetail allocate : rd.getReceiptHeader().getAllocations()) {
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

		receiptCalculator.initiateReceipt(rd, false);

		schdData.setFinanceScheduleDetails(finSchdDtls);
		schdData.setFeeEvent(eventCode);

		return rd;
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
				TaxAmountSplit tax = GSTCalculator.calculateGST(fd, taxType, waivedAmount, BigDecimal.ZERO);
				bal = bal.subtract(tax.gettGST());
			}

			if (bal.compareTo(BigDecimal.ZERO) > 0 && allocate.isEditable()) {
				isDueAdjusted = false;
			}
		}
		return isDueAdjusted;
	}

	private void adjustToExcess(FinReceiptData rd) {
		FinReceiptHeader rch = rd.getReceiptHeader();
		rch.setPrvReceiptPurpose(rch.getReceiptPurpose());

		rch.setReceiptPurpose(FinServiceEvent.SCHDRPY);
		rch.getTotalPastDues().setTotalPaid(BigDecimal.ZERO);
		rch.getTotalRcvAdvises().setTotalPaid(BigDecimal.ZERO);
		rch.getTotalBounces().setTotalPaid(BigDecimal.ZERO);
		rch.getTotalFees().setTotalPaid(BigDecimal.ZERO);

		List<FinReceiptDetail> recDtls = rch.getReceiptDetails();
		List<FinReceiptDetail> newRecDtls = new ArrayList<>();

		List<String> excessList = PennantStaticListUtil.getExcessList();
		for (FinReceiptDetail rcd : recDtls) {
			if (!excessList.contains(rcd.getPaymentType())) {
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
	}

	private FinReceiptData prepareFinReceiptData(FinServiceInstruction fsi, FinanceDetail fd) {
		logger.info(Literal.ENTERING);

		FinReceiptData rd = new FinReceiptData();
		rd.setFinanceDetail(fd);

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		FinanceType financeType = schdData.getFinanceType();

		LoggedInUser loggedInUser = fsi.getLoggedInUser();
		ReceiptPurpose receiptPurpose = fsi.getReceiptPurpose();

		Date fromDate = fsi.getFromDate();
		if (fromDate == null) {
			fsi.setFromDate(fm.getAppDate());
		}

		String recalType = fsi.getRecalType();
		if (StringUtils.isBlank(recalType) || PennantConstants.List_Select.equals(recalType)) {
			if (receiptPurpose == ReceiptPurpose.EARLYSETTLE) {
				recalType = CalculationConstants.EARLYPAY_ADJMUR;
			}
		}

		if (receiptPurpose == ReceiptPurpose.EARLYRPY && StringUtils.isBlank(recalType)) {
			recalType = financeType.getFinScheduleOn();
			if (schdData.getFinanceMain().isAlwFlexi() || financeType.isDeveloperFinance()) {
				recalType = CalculationConstants.EARLYPAY_PRIHLD;
			}
		}

		FinReceiptHeader rch = new FinReceiptHeader();
		rch.setFinID(fsi.getFinID());
		rch.setReference(fsi.getFinReference());
		rch.setExcessAdjustTo(fsi.getExcessAdjustTo());
		rch.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rch.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		rch.setReceiptDate(fm.getAppDate());

		rch.setReceiptPurpose(receiptPurpose.code());
		rch.setEffectSchdMethod(recalType);
		rch.setAllocationType(fsi.getAllocationType());
		rch.setReceiptAmount(fsi.getAmount());
		rch.setReceiptMode(fsi.getPaymentMode());
		rch.setSubReceiptMode(fsi.getSubReceiptMode());
		rch.setReceiptChannel(fsi.getReceiptChannel());
		rch.setExtReference(fsi.getExternalReference());
		rch.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		rch.setNewRecord(true);
		rch.setLastMntBy(loggedInUser.getUserId());
		rch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		rch.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		rch.setUserDetails(loggedInUser);
		rch.setTdsAmount(fsi.getTdsAmount());
		rch.setExcldTdsCal(fsi.isExcldTdsCal());
		rch.setReceivedFrom(fsi.getReceivedFrom());
		rch.setPanNumber(fsi.getPanNumber());
		rch.setBankCode(fsi.getBankCode());

		rd.setBuildProcess("I");
		rd.setReceiptHeader(rch);
		rd.setFinID(fsi.getFinID());
		rd.setFinReference(fsi.getFinReference());
		rd.setValueDate(fsi.getValueDate());
		rd.setUserDetails(loggedInUser);

		if (receiptPurpose == ReceiptPurpose.SCHDRPY && fsi.isBckdtdWthOldDues()) {
			Date derivedDate = getDerivedValueDate(rd, fsi, fm.getAppDate());

			if (derivedDate != null) {
				rch.setValueDate(derivedDate);
			}
		}

		FinReceiptDetail rcd = fsi.getReceiptDetail();

		if (rcd == null) {
			rcd = new FinReceiptDetail();
			fsi.setReceiptDetail(rcd);
		}

		rch.getReceiptDetails().add(rcd);
		rcd.setValueDate(rd.getValueDate());
		rcd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rcd.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);

		if (ReceiptMode.ONLINE.equals(fsi.getPaymentMode())) {
			rcd.setPaymentType(fsi.getSubReceiptMode());
		} else {
			rcd.setPaymentType(fsi.getPaymentMode());
		}

		rcd.setAmount(fsi.getAmount());
		rch.setRemarks(rcd.getRemarks());
		rch.setSource(PennantConstants.FINSOURCE_ID_API);

		if (fsi.isReceiptUpload()) {
			rch.setReceiptDate(rcd.getReceivedDate());
			rch.setPanNumber(fsi.getPanNumber());
			rch.setExtReference(fsi.getExternalReference());
			rch.setReceivedDate(fsi.getReceivedDate());
		} else {
			rcd.setValueDate(rcd.getReceivedDate());
			rch.setReceiptDate(fm.getAppDate());
		}

		if (rch.getReceiptMode() != null && (rch.getSubReceiptMode() == null || rch.getSubReceiptMode().isEmpty())) {
			rch.setSubReceiptMode(rch.getReceiptMode());
		}

		String paymentType = rcd.getPaymentType();
		if (receiptPurpose == ReceiptPurpose.EARLYSETTLE && fsi.isReceiptUpload()
				&& ReceiptMode.CHEQUE.equals(paymentType) || ReceiptMode.DD.equals(paymentType)) {
			int defaultClearingDays = SysParamUtil.getValueAsInt("EARLYSETTLE_CHQ_DFT_DAYS");
			fsi.setValueDate(DateUtil.addDays(fsi.getReceivedDate(), defaultClearingDays));
			rch.setValueDate(fsi.getValueDate());
			rcd.setValueDate(rch.getValueDate());
		}

		rd.setSourceId(PennantConstants.FINSOURCE_ID_API);

		rch.setDepositDate(rcd.getDepositDate());

		if (ReceiptMode.CHEQUE.equals(paymentType) || ReceiptMode.DD.equals(paymentType)) {
			rch.setTransactionRef(rcd.getFavourNumber());
		} else {
			rch.setTransactionRef(rcd.getTransactionRef());
		}

		rch.setPartnerBankId(rcd.getFundingAc());
		if (rcd.getFundingAc() > 0) {
			PartnerBank partnerBank = partnerBankDAO.getPartnerBankById(rcd.getFundingAc());
			if (partnerBank != null) {
				rcd.setPartnerBankAc(partnerBank.getAccountNo());
				rcd.setPartnerBankAcType(partnerBank.getAcType());
			}
		}

		if (rch.getReceiptMode() != null && rch.getSubReceiptMode() == null) {
			rch.setSubReceiptMode(rch.getSubReceiptMode());
		}

		if (StringUtils.equals(fsi.getReqType(), RepayConstants.REQTYPE_INQUIRY)) {
			rd.setValueDate(fm.getAppDate());
			rch.setValueDate(fm.getAppDate());
		}

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

		String receiptMode = fsi.getPaymentMode();
		if (ReceiptMode.CHEQUE.equals(receiptMode) || ReceiptMode.DD.equals(receiptMode)) {
			rcd.setFavourName(rcd.getTransactionRef());
		}

		// int moduleID = FinanceConstants.MODULEID_FINTYPE;

		// if (StringUtils.isNotBlank(fm.getPromotionCode())) {
		// moduleID = FinanceConstants.MODULEID_PROMOTION;
		// }

		// List<FinTypeFees> finTypeFeesList = finTypeFeesDAO.getFinTypeFeesList(fm.getFinType(), eventCode, "_AView",
		// false, moduleID);
		// fd.setFinTypeFeesList(finTypeFeesList);
		// rch.setExcessAmounts(finExcessAmountDAO.getExcessAmountsByRef(rch.getFinID()));

		if (FinanceConstants.PRODUCT_CD.equals(fm.getProductCategory())
				&& ReceiptMode.PAYABLE.equals(fsi.getPaymentMode())) {
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

		logger.info(Literal.LEAVING);
		return rd;
	}

	@Override
	public FinanceDetail receiptTransaction(FinServiceInstruction fsi) {
		logger.info(Literal.ENTERING);

		String eventCode = null;

		ReceiptPurpose receiptPurpose = fsi.getReceiptPurpose();

		switch (receiptPurpose) {
		case SCHDRPY:
			eventCode = AccountingEvent.REPAY;
			break;
		case EARLYRPY:
			eventCode = AccountingEvent.EARLYPAY;
			break;
		case EARLYSETTLE:
			eventCode = AccountingEvent.EARLYSTL;
			break;
		case RESTRUCTURE:
			eventCode = AccountingEvent.RESTRUCTURE;
			break;
		default:
			break;
		}

		fsi.setModuleDefiner(receiptPurpose.code());

		long finID = fsi.getFinID();
		String finReference = fsi.getFinReference();
		RequestSource requestSource = fsi.getRequestSource();
		String reqType = fsi.getReqType();
		String paymentMode = fsi.getPaymentMode();

		String receiptMode = ReceiptMode.getReceiptMode(paymentMode);
		String subReceiptMode = fsi.getSubReceiptMode();
		String receiptChannel = fsi.getReceiptChannel();

		fsi.setPaymentMode(receiptMode);
		fsi.setSubReceiptMode(subReceiptMode == null ? ReceiptMode.getSubReceiptMode(paymentMode) : subReceiptMode);
		fsi.setReceiptChannel(receiptChannel == null ? ReceiptMode.getReceiptChannel(paymentMode) : receiptChannel);

		LoggedInUser userDetails = fsi.getLoggedInUser();
		Long userId = null;
		String userName = "";
		if (userDetails == null) {
			User logiedInUser = SessionUserDetails.getLogiedInUser();

			if (logiedInUser != null) {
				userDetails = SessionUserDetails.getUserDetails(logiedInUser);
			}

			if (userDetails != null) {
				userId = userDetails.getUserId();
				userName = userDetails.getUserName();
			}
		}

		fsi.setLoggedInUser(userDetails);

		StringBuilder logMsg = new StringBuilder();
		logMsg.append("\n");
		logMsg.append(" ========================================================\n");
		logMsg.append("User-ID: ").append(userId).append("\n");
		logMsg.append("User-Name: ").append(userName).append("\n");
		logMsg.append("Fin-Reference: ").append(finReference).append("\n");
		logMsg.append("Fin-ID: ").append(finID).append("\n");
		logMsg.append("Receipt-Purpose: ").append(receiptPurpose.code()).append("\n");
		logMsg.append("Request-Source: ").append(requestSource.name()).append("\n");
		logMsg.append("Request-Type: ").append(reqType).append("\n");
		logMsg.append("Payment-Mode: ").append(fsi.getPaymentMode()).append("\n");
		logMsg.append("Receipt-Mode: ").append(receiptMode).append("\n");
		logMsg.append("Sub-Receipt-Mode: ").append(fsi.getSubReceiptMode()).append("\n");
		logMsg.append("Receipt-Channel: ").append(fsi.getReceiptChannel()).append("\n");
		logMsg.append("Allocation-Type: ").append(fsi.getAllocationType()).append("\n");
		logMsg.append("Excess-Adjust-To: ").append(fsi.getExcessAdjustTo()).append("\n");

		logMsg.append(" Defaulting empty data...");

		setDefaults(fsi);

		logMsg.append("Allocation-Type: ").append(fsi.getAllocationType()).append("\n");
		logMsg.append("Excess-Adjust-To: ").append(fsi.getExcessAdjustTo()).append("\n");

		logMsg.append("\n");
		logMsg.append("=======================================================\n");

		logger.info(logMsg);

		FinanceDetail fd = new FinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		schdData.setFinID(finID);
		schdData.setFinReference(finReference);
		schdData.setFeeEvent(eventCode);
		schdData.setFinServiceInstruction(fsi);

		switch (reqType) {
		case "Inquiry":
		case "Post":
			break;
		default:
			setError(schdData, "91113", reqType);
			logger.info(Literal.LEAVING);
			return fd;
		}

		if (receiptPurpose == ReceiptPurpose.EARLYSETTLE && !isValidEarlySettleReason(fsi.getEarlySettlementReason())) {
			setError(schdData, "90501", "earlySettlementReason");
			logger.info(Literal.LEAVING);
			return fd;
		}

		setFinanceMain(schdData, receiptPurpose);

		FinanceMain fm = schdData.getFinanceMain();
		fm.setUserDetails(userDetails);

		if (CollectionUtils.isNotEmpty(schdData.getErrorDetails())) {
			logger.info(Literal.LEAVING);
			return fd;
		}

		schdData.setFinServiceInstruction(fsi);

		doBasicValidations(schdData);

		if (CollectionUtils.isNotEmpty(schdData.getErrorDetails())) {
			logger.info(Literal.LEAVING);
			return fd;
		}

		FinReceiptData rd = prepareFinReceiptData(fsi, fd);

		validateReceiptData(rd);

		if (CollectionUtils.isNotEmpty(schdData.getErrorDetails())) {
			logger.info(Literal.LEAVING);
			return fd;
		}

		validateAllocations(rd);

		if (CollectionUtils.isNotEmpty(schdData.getErrorDetails())) {
			return fd;
		}

		if (ReceiptPurpose.EARLYSETTLE == receiptPurpose && fsi.isReceiptUpload() && !"Post".equals(fsi.getReqType())) {
			setReceiptDataForEarlySettlement(rd);
		}

		setFinanceData(rd);

		doReceiptValidations(rd);

		if (CollectionUtils.isNotEmpty(schdData.getErrorDetails())) {
			fd.setFinScheduleData(schdData);
			logger.info(Literal.LEAVING);
			return fd;
		}

		calcuateDues(rd);

		if (ReceiptPurpose.EARLYRPY == receiptPurpose) {
			validateEarlyPay(rd);
			if (CollectionUtils.isNotEmpty(schdData.getErrorDetails())) {
				logger.info(Literal.LEAVING);
				return fd;
			}
		}

		Date valueDate = fsi.getValueDate();

		if (ReceiptPurpose.EARLYSETTLE == receiptPurpose) {
			validateEarlySettlement(schdData, rd, valueDate);

			if (CollectionUtils.isNotEmpty(schdData.getErrorDetails())) {
				return fd;
			}
		}

		if (RequestSource.UPLOAD != requestSource) {
			FinServiceInstruction tempFsi = schdData.getFinServiceInstruction().copyEntity();
			FinReceiptHeader rch = rd.getReceiptHeader().copyEntity();

			if (CollectionUtils.isNotEmpty(schdData.getErrorDetails())) {
				logger.info(Literal.LEAVING);
				return fd;
			}

			fd = rd.getFinanceDetail();
			rd.setReceiptHeader(rch);

			schdData = fd.getFinScheduleData();
			schdData.setFinServiceInstruction(tempFsi);
		}

		String allocateMthd = rd.getReceiptHeader().getAllocationType();
		if (AllocationType.AUTO.equals(allocateMthd)) {
			rd = receiptCalculator.recalAutoAllocation(rd, false);
		}

		checkInprocessReceipts(schdData, receiptPurpose);

		if (AllocationType.MANUAL.equals(allocateMthd)) {
			if (requestSource == RequestSource.API && receiptPurpose == ReceiptPurpose.EARLYSETTLE) {
				rd = validateAllocationsAmount(rd);
			}
			rd = updateAllocationsPaid(rd);
		}

		if (CollectionUtils.isNotEmpty(schdData.getErrorDetails())) {
			logger.info(Literal.LEAVING);
			return fd;
		}

		if (requestSource == RequestSource.UPLOAD && "Inquiry".equals(reqType)) {
			logger.info(Literal.LEAVING);
			return fd;
		}

		doReceiptTransaction(rd, eventCode);

		logger.info(Literal.LEAVING);
		return fd;
	}

	private void validateEarlyPay(FinReceiptData rd) {
		FinanceDetail fd = rd.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		if (rd.getReceiptHeader().getPartPayAmount().compareTo(BigDecimal.ZERO) <= 0) {
			setError(schdData, "90332", ReceiptPurpose.EARLYRPY.code(), "");
			return;
		}

		if (rd.getReceiptHeader().getValueDate().compareTo(fm.getFinStartDate()) == 0) {
			setError(schdData, "21006",
					"First Disbursement date is same as Current Business date. Not allowed for Payment.");
			return;
		}

		BigDecimal closingBal = getClosingBalance(fm.getFinID(), rd.getReceiptHeader().getValueDate());
		BigDecimal diff = closingBal.subtract(rd.getReceiptHeader().getPartPayAmount());
		if (diff.compareTo(new BigDecimal(100)) < 0) {
			setError(schdData, "91127", String.valueOf(closingBal));
			return;
		}
	}

	private void setFinanceMain(FinScheduleData schdData, ReceiptPurpose receiptPurpose) {
		long finID = schdData.getFinID();
		String finReference = schdData.getFinReference();

		if (financeMainDAO.getCountByBlockedFinances(finID) > 0) {
			setError(schdData, "90204", receiptPurpose.code(), "FinReference : " + finReference);
			return;
		}

		FinanceMain fm = financeMainDAO.getFinanceMainForLMSEvent(finID);

		if (!fm.isFinIsActive()) {
			switch (receiptPurpose) {
			case EARLYRPY:
			case EARLYSETTLE:
				setError(schdData, "RU0049");
				break;
			default:
				break;
			}
		}

		FinanceType financeType = financeTypeDAO.getOrgFinanceTypeByID(fm.getFinType(), "_ORGView");

		fm.setReceiptPurpose(receiptPurpose.code());
		fm.setAppDate(SysParamUtil.getAppDate());
		fm.setEntityDesc(financeType.getLovDescEntityDesc());

		schdData.setFinanceType(financeType);
		schdData.setFinanceMain(fm);
	}

	private void doReceiptTransaction(FinReceiptData rd, String eventCode) {
		logger.info(Literal.ENTERING);
		FinanceDetail fd = rd.getFinanceDetail();

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		FinServiceInstruction fsi = schdData.getFinServiceInstruction();

		List<ErrorDetail> errors = schdData.getErrorDetails();

		if (CollectionUtils.isNotEmpty(errors)) {
			logger.info(Literal.LEAVING);
			return;
		}

		if ("Inquiry".equals(fsi.getReqType()) && fsi.getToDate() == null) {
			fsi.setToDate(fm.getMaturityDate());
		}

		ReceiptPurpose receiptPurpose = fsi.getReceiptPurpose();
		RequestSource requestSource = fsi.getRequestSource();
		String allocationType = fsi.getAllocationType();

		switch (requestSource) {
		case UI:
			if (!AccountingEvent.RESTRUCTURE.equals(eventCode)) {
				validateFees(rd);
			}
			break;
		case API:
			if (!(AllocationType.MANUAL.equals(allocationType) || AccountingEvent.RESTRUCTURE.equals(eventCode))) {
				validateFees(rd);
			}
			break;
		case UPLOAD:
			break;
		default:
			break;
		}

		if (CollectionUtils.isNotEmpty(errors)) {
			logger.info(Literal.LEAVING);
			return;
		}

		try {
			doProcessReceipt(rd, receiptPurpose);
		} catch (AppException e) {
			logger.error(Literal.EXCEPTION, e);
			setError(schdData, "9999", e.getMessage());
			logger.info(Literal.LEAVING);
			return;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			setError(schdData, "9999", "Unable to process request.");
			logger.info(Literal.LEAVING);
			return;
		}

		logger.info(Literal.LEAVING);
	}

	public void setReceiptModeStatus(FinReceiptHeader rch) {
		if ((rch.getNextRoleCode() == null || rch.getNextRoleCode().equals(FinanceConstants.REALIZATION_MAKER))
				&& StringUtils.equals(RepayConstants.PAYSTATUS_INITIATED, rch.getReceiptModeStatus())) {
			rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_DEPOSITED);
		}
	}

	private void validateFees(FinReceiptData rd) {
		FinanceDetail fd = rd.getFinanceDetail();

		FinScheduleData schdData = fd.getFinScheduleData();

		FinanceMain fm = schdData.getFinanceMain();
		FinServiceInstruction fsi = schdData.getFinServiceInstruction();

		String roundingMode = fm.getCalRoundingMode();
		int roundingTarget = fm.getRoundingTarget();

		boolean feesExists = CollectionUtils.isNotEmpty(fsi.getFinFeeDetails());
		boolean feesApplicable = CollectionUtils.isNotEmpty(schdData.getFinFeeDetailList());

		/**
		 * Fees applicable, but fee details are not available in the request.
		 */
		if (feesApplicable && !feesExists) {
			setError(schdData, "65019", schdData.getFinFeeDetailList().get(0).getFeeTypeCode());
			return;
		}

		/**
		 * Fees not applicable, but fee details are available in the request.
		 */
		if (!feesApplicable && feesExists) {
			setError(schdData, "90245");
			return;
		}

		/**
		 * Fees not applicable, nothing to do.
		 */
		if (!feesExists) {
			return;
		}

		FinFeeDetail reqFee = fsi.getFinFeeDetails().get(0);
		FinFeeDetail confFee = schdData.getFinFeeDetailList().get(0);

		String reqFeeCode = reqFee.getFeeTypeCode();
		String confFeeCode = confFee.getFeeTypeCode();

		/**
		 * Fee Code in the request and configuration not matched.
		 */
		if (!StringUtils.equalsIgnoreCase(reqFeeCode, confFeeCode)) {
			setError(schdData, "90247");
			return;
		}

		BigDecimal actualFee = reqFee.getActualAmount();
		BigDecimal paidFee = reqFee.getPaidAmount();
		BigDecimal waivedFee = reqFee.getWaivedAmount();

		BigDecimal eventActualFee = confFee.getActualAmount();
		BigDecimal maxWaiver = confFee.getMaxWaiverPerc();

		BigDecimal maxWaiverAllowed = BigDecimal.ZERO;
		if (maxWaiver.compareTo(HUNDRED) == 0) {
			maxWaiverAllowed = eventActualFee;
		} else if (maxWaiver.compareTo(ZERO) > 0) {
			maxWaiverAllowed = eventActualFee.multiply(maxWaiver).divide(HUNDRED, 0, RoundingMode.HALF_DOWN);
			maxWaiverAllowed = CalculationUtil.roundAmount(maxWaiverAllowed, roundingMode, roundingTarget);
		}

		if (actualFee.compareTo(ZERO) < 0 || paidFee.compareTo(ZERO) < 0 || waivedFee.compareTo(ZERO) < 0) {
			setError(schdData, "90259", reqFeeCode);
			return;
		}

		int formatter = CurrencyUtil.getFormat(fm.getFinCcy());

		if (waivedFee.compareTo(maxWaiverAllowed) > 0) {
			String feeAmount = PennantApplicationUtil.amountFormate(maxWaiverAllowed, formatter);
			setError(schdData, "90257", "Fee Waiver", feeAmount, reqFeeCode);
			return;
		}

		if ((actualFee.subtract(paidFee).subtract(waivedFee)).compareTo(ZERO) != 0) {
			setError(schdData, "90258", "Fee Amount - Fee Waived", "Fee Paid", reqFeeCode);
			return;
		}

		validateAllocationFees(rd);
	}

	private void validateAllocationFees(FinReceiptData rd) {
		FinanceDetail fd = rd.getFinanceDetail();

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		FinServiceInstruction fsi = schdData.getFinServiceInstruction();

		FinReceiptHeader rch = rd.getReceiptHeader();
		List<ReceiptAllocationDetail> allocations = rch.getAllocations();

		if (CollectionUtils.isEmpty(allocations)) {
			return;
		}

		FinFeeDetail reqFee = fsi.getFinFeeDetails().get(0);
		FinFeeDetail confFee = schdData.getFinFeeDetailList().get(0);

		String reqFeeCode = reqFee.getFeeTypeCode();
		String confFeeCode = confFee.getFeeTypeCode();

		BigDecimal actualFee = reqFee.getActualAmount();
		BigDecimal eventActualFee = confFee.getActualAmount();

		for (ReceiptAllocationDetail allocate : allocations) {
			String allocationType = allocate.getAllocationType();
			String feeTypeCode = allocate.getFeeTypeCode();

			if (fm.istDSApplicable() && StringUtils.equalsIgnoreCase(confFeeCode, feeTypeCode)
					&& (Allocation.ODC.equals(allocationType) || Allocation.FEE.equals(allocationType))) {
				eventActualFee = allocate.getPaidAmount();
				break;
			}
		}

		int formatter = CurrencyUtil.getFormat(fm.getFinCcy());

		if (actualFee.compareTo(eventActualFee) != 0) {
			String feeAmount = PennantApplicationUtil.amountFormate(eventActualFee, formatter);
			setError(schdData, "90258", "Fee Amount", feeAmount, reqFeeCode);
		}

	}

	private void doProcessReceipt(FinReceiptData rd, ReceiptPurpose receiptPurpose) throws Exception {
		logger.info(Literal.ENTERING);

		FinanceDetail fd = rd.getFinanceDetail();
		FinReceiptHeader rch = rd.getReceiptHeader();
		FinReceiptDetail rcd = rch.getReceiptDetails().get(0);

		FinScheduleData schdData = fd.getFinScheduleData();
		FinServiceInstruction fsi = schdData.getFinServiceInstruction();
		FinanceMain fm = schdData.getFinanceMain();

		Date appDate = fm.getAppDate();
		Date sysDate = DateUtil.getSysDate();

		RequestSource requestSource = fsi.getRequestSource();

		rd.setTotalPastDues(receiptCalculator.getTotalNetPastDue(rd));

		if (receiptPurpose == ReceiptPurpose.EARLYSETTLE) {
			rch.getReceiptDetails().clear();
			createXcessRCD(rd);
			rch.getReceiptDetails().add(rcd);
		}

		BigDecimal amount = rch.getReceiptAmount().subtract(rd.getExcessAvailable());

		if (rd.getTotalPastDues().compareTo(amount) >= 0) {
			rcd.setDueAmount(amount);
			rd.setTotalPastDues(rd.getTotalPastDues().subtract(amount));
		} else {
			rcd.setDueAmount(rd.getTotalPastDues());
			rd.setTotalPastDues(BigDecimal.ZERO);
		}

		if (requestSource != RequestSource.UPLOAD && !"Post".equals(fsi.getReqType())) {
			BigDecimal earlyPayAmount = rd.getRemBal();
			String recalType = rch.getEffectSchdMethod();
			fm.setReceiptPurpose(receiptPurpose.code());

			if (receiptPurpose == ReceiptPurpose.EARLYRPY) {
				ScheduleCalculator.recalEarlyPaySchedule(schdData, rch.getValueDate(), null, earlyPayAmount, recalType);
				receiptCalculator.addPartPaymentAlloc(rd);
			}

			for (ReceiptAllocationDetail allocate : rch.getAllocations()) {
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

			rd.setBuildProcess("R");

			receiptCalculator.initiateReceipt(rd, false);
			prepareFinanceSummary(fd);
			FinanceSummary summary = fd.getFinScheduleData().getFinanceSummary();
			summary.setFinODDetail(rch.getFinODDetails());
			fd.getFinScheduleData().setFinODDetails(rch.getFinODDetails());

			logger.info(Literal.LEAVING);
			return;
		}

		if ((requestSource == RequestSource.UPLOAD || requestSource == RequestSource.API) && dedupCheckRequest(rch)) {
			long rchID = checkDedupSP(rch);

			if (rchID > 0) {
				finReceiptHeaderDAO.updateReceiptStatusAndRealizationDate(rchID, RepayConstants.PAYSTATUS_REALIZED,
						rch.getRealizationDate());
				finReceiptDetailDAO.updateReceiptStatusByReceiptId(rchID, RepayConstants.PAYSTATUS_REALIZED);
				setError(schdData, "0000", "Success");

				logger.info(Literal.LEAVING);
				return;
			}
		}

		if (requestSource == RequestSource.API && CollectionUtils.isNotEmpty(rch.getAllocations())) {
			fd.getFinScheduleData().setReceiptAllocationList(rch.getAllocations());
		}

		if (rcd.getFundingAc() > 0) {
			PartnerBank partnerBank = partnerBankDAO.getPartnerBankById(rcd.getFundingAc(), "");
			if (partnerBank != null) {
				rcd.setPartnerBankAc(partnerBank.getAccountNo());
				rcd.setPartnerBankAcType(partnerBank.getAcType());
			}
		}

		String receiptMode = fsi.getPaymentMode();
		if (ReceiptMode.ONLINE.equals(receiptMode)) {
			receiptMode = fsi.getSubReceiptMode();
		}

		if (!ReceiptMode.CHEQUE.equals(receiptMode) && !ReceiptMode.DD.equals(receiptMode)) {
			PartnerBank partnerBank = partnerBankDAO.getPartnerBankById(rcd.getFundingAc(), "");
			if (partnerBank != null) {
				rcd.setPartnerBankAc(partnerBank.getAccountNo());
				rcd.setPartnerBankAcType(partnerBank.getAcType());
			}

		}

		AuditHeader auditHeader = getAuditHeader(rd, PennantConstants.TRAN_WF);

		fm.setVersion(fm.getVersion() + 1);
		fm.setRecordType("");

		initiateReceipt(rd, receiptPurpose);

		if (CollectionUtils.isNotEmpty(schdData.getErrorDetails())) {
			return;
		}

		if (fsi.isNonStp()) {
			auditHeader = doApprove(auditHeader);
		} else {
			setWorkflowDetails(rd);
			if (CollectionUtils.isNotEmpty(schdData.getErrorDetails())) {
				logger.info(Literal.LEAVING);
				return;
			}

			auditHeader = saveOrUpdate(auditHeader);
		}

		if (CollectionUtils.isNotEmpty(auditHeader.getErrorMessage())) {
			ErrorDetail error = auditHeader.getErrorMessage().get(0);
			setError(schdData, error.getCode(), error.getError());
			return;
		}

		List<FinServiceInstruction> finServInstList = new ArrayList<>();
		for (FinReceiptDetail recDtl : rch.getReceiptDetails()) {
			FinRepayHeader rpyHeader = recDtl.getRepayHeader();

			FinServiceInstruction instruction = new FinServiceInstruction();
			instruction.setFinID(fm.getFinID());
			instruction.setFinReference(fm.getFinReference());
			instruction.setFinReference(fm.getFinReference());
			instruction.setFinEvent(rpyHeader.getFinEvent());
			instruction.setAmount(rpyHeader.getRepayAmount());
			instruction.setAppDate(appDate);
			instruction.setSystemDate(sysDate);
			instruction.setMaker(auditHeader.getAuditUsrId());
			instruction.setMakerAppDate(appDate);
			instruction.setMakerSysDate(DateUtil.getSysDate());
			instruction.setChecker(auditHeader.getAuditUsrId());
			instruction.setCheckerAppDate(appDate);
			instruction.setCheckerSysDate(DateUtil.getSysDate());
			instruction.setReference(String.valueOf(rch.getReceiptID()));

			finServInstList.add(instruction);
		}

		if (CollectionUtils.isNotEmpty(finServInstList)) {
			finServiceInstructionDAO.saveList(finServInstList, "");
		}

		if (!fsi.isReceiptResponse() && requestSource == RequestSource.UPLOAD) {
			this.receiptUploadDetailDAO.updateReceiptId(fsi.getUploadDetailId(), rch.getReceiptID());
		}

		if (fsi.isReceiptResponse() && requestSource == RequestSource.UPLOAD) {
			this.receiptResponseDetailDAO.updateReceiptResponseId(fsi.getRootId(), rch.getReceiptID());
		}

		logger.info(Literal.LEAVING);
	}

	private void setWorkflowDetails(FinReceiptData rd) {
		FinanceDetail fd = rd.getFinanceDetail();
		FinReceiptHeader rch = rd.getReceiptHeader();

		FinScheduleData schdData = fd.getFinScheduleData();
		FinServiceInstruction fsi = schdData.getFinServiceInstruction();
		FinanceMain fm = schdData.getFinanceMain();

		WorkFlowDetails workFlowDetails = null;
		String roleCode = fsi.getProcessStage();
		String nextRolecode = fsi.getProcessStage();

		String taskid = null;
		String nextTaskId = null;
		long workFlowId = 0;
		String finEvent = FinServiceEvent.RECEIPT;

		FinanceWorkFlow financeWorkFlow = financeWorkFlowService.getApprovedFinanceWorkFlowById(fm.getFinType(),
				finEvent, PennantConstants.WORFLOW_MODULE_FINANCE);

		if (financeWorkFlow == null) {
			setError(schdData, "90339", "");
			return;
		}

		workFlowDetails = WorkFlowUtil.getDetailsByType(financeWorkFlow.getWorkFlowType());
		String[] workFlowRoles = workFlowDetails.getWorkFlowRoles().split(";");

		if (StringUtils.isBlank(fsi.getProcessStage())) {
			roleCode = workFlowDetails.getFirstTaskOwner();
			nextRolecode = roleCode;
		}
		boolean roleNotFound = false;
		for (String workFlowRole : workFlowRoles) {
			if (StringUtils.equals(workFlowRole, roleCode)) {
				roleNotFound = true;
				break;
			}
		}

		if (!roleNotFound) {
			setError(schdData, "API004", roleCode);
			return;
		}

		String paymentMode = fsi.getPaymentMode();
		if ("CASH".equals(paymentMode) && !"RECEIPT_MAKER".equals(roleCode)
				&& !"REALIZATION_APPROVER".equals(roleCode)) {
			setError(schdData, "30556", "CASH PAYMENT MODE");
			return;
		}

		WorkflowEngine workFlow = new WorkflowEngine(workFlowDetails.getWorkFlowXml());
		taskid = workFlow.getUserTaskId(roleCode);
		workFlowId = workFlowDetails.getWorkFlowId();
		nextTaskId = workFlow.getUserTaskId(nextRolecode);

		fm.setWorkflowId(workFlowId);
		fm.setTaskId(taskid);
		fm.setRoleCode(roleCode);
		fm.setNextRoleCode(nextRolecode);
		fm.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		fm.setNextTaskId(nextTaskId + ";");
		fm.setNewRecord(true);
		fm.setVersion(1);
		fm.setRcdMaintainSts(FinServiceEvent.RECEIPT);
		fm.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		fm.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);

		rch.setTaskId(taskid);
		rch.setNextTaskId(nextTaskId + ";");
		rch.setRoleCode(roleCode);
		rch.setNextRoleCode(nextRolecode);
		rch.setWorkflowId(workFlowId);
		rch.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
		rch.setFinType(fm.getFinType());
	}

	private void doEmptyResponseObject(FinanceDetail fd) {
		fd.setFinScheduleData(null);
		fd.setDocumentDetailsList(null);
		fd.setJointAccountDetailList(null);
		fd.setGurantorsDetailList(null);
		fd.setCollateralAssignmentList(null);
		fd.setReturnDataSetList(null);
		fd.setInterfaceDetailList(null);
		fd.setFinFlagsDetails(null);
		fd.setCustomerDetails(null);
	}

	private WSReturnStatus getWSReturnStatus(String code, String message) {
		WSReturnStatus status = new WSReturnStatus();
		status.setReturnCode(code);
		status.setReturnText(message);

		return status;
	}

	private void initiateReceipt(FinReceiptData rd, ReceiptPurpose receiptPurpose) {
		FinanceDetail fd = rd.getFinanceDetail();
		FinReceiptHeader rch = rd.getReceiptHeader();

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		FinServiceInstruction fsi = schdData.getFinServiceInstruction();

		BigDecimal earlyPayAmount = rd.getRemBal();
		String recalType = rch.getEffectSchdMethod();
		fm.setReceiptPurpose(receiptPurpose.code());

		if (ReceiptPurpose.EARLYRPY == receiptPurpose) {
			schdData = ScheduleCalculator.recalEarlyPaySchedule(schdData, rch.getValueDate(), null, earlyPayAmount,
					recalType);
			receiptCalculator.addPartPaymentAlloc(rd);
			schdData.setSchduleGenerated(true);
		}

		List<FinanceScheduleDetail> originalSchedules = copy(schdData.getFinanceScheduleDetails());

		rd.setDueAdjusted(true);
		List<ReceiptAllocationDetail> allocations = rch.getAllocations();

		if (ReceiptPurpose.EARLYSETTLE == receiptPurpose && !checkDueAdjusted(allocations, rd)) {
			adjustToExcess(rd);
			rd.setDueAdjusted(false);
		}

		if (RequestSource.UPLOAD == fsi.getRequestSource()) {
			if (rd.isDueAdjusted()) {
				for (ReceiptAllocationDetail allocate : allocations) {
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
			} else {
				setError(schdData, "90330", receiptPurpose.code(), "");
				return;
			}

			rd.setBuildProcess("R");
			receiptCalculator.initiateReceipt(rd, false);

			schdData.setFinanceScheduleDetails(originalSchedules);
		}
	}

	/**
	 * Get Audit Header Details
	 */
	private AuditHeader getAuditHeader(FinReceiptData repayData, String tranType) {
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

		int receiptPurposeCtg = ReceiptUtil.getReceiptPurpose(receiptData.getReceiptHeader().getReceiptPurpose());

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
		if (StringUtils.equals(mode, ReceiptMode.EMIINADV)) {
			payType = RepayConstants.EXAMOUNTTYPE_EMIINADV;
		} else if (StringUtils.equals(mode, ReceiptMode.EXCESS)) {
			payType = RepayConstants.EXAMOUNTTYPE_EXCESS;
		} else if (StringUtils.equals(mode, ReceiptMode.PAYABLE)) {
			payType = RepayConstants.EXAMOUNTTYPE_PAYABLE;
		} else if (StringUtils.equals(mode, ReceiptMode.CASHCLT)) {
			payType = RepayConstants.EXAMOUNTTYPE_CASHCLT;
		} else if (StringUtils.equals(mode, ReceiptMode.DSF)) {
			payType = RepayConstants.EXAMOUNTTYPE_DSF;
		}
		return payType;
	}

	@Override
	public FinReceiptData calcuateDues(FinReceiptData rd) {
		logger.info(Literal.ENTERING);

		FinReceiptHeader rch = rd.getReceiptHeader();
		FinanceDetail fd = rd.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		Date valueDate = rch.getValueDate();

		rd.setAllocList(rch.getAllocations());
		rd.setValueDate(valueDate);
		rch.setValueDate(null);

		rd.setBuildProcess("I");
		rd = receiptCalculator.initiateReceipt(rd, false);

		if (rd.isForeClosure()) {
			rch.setReceiptAmount(BigDecimal.ZERO);
			rd.setExcessAvailable(receiptCalculator.getExcessAmount(rd));
			rch.setReceiptAmount(rd.getExcessAvailable());
		}

		ReceiptPurpose receiptPurpose = ReceiptPurpose.purpose(rch.getReceiptPurpose());

		if (receiptPurpose == ReceiptPurpose.EARLYSETTLE && DateUtil.compare(valueDate, fm.getMaturityDate()) < 0
				&& fm.isFinIsActive()) {
			rch.setValueDate(null);

			if (!fm.isSimulateAccounting()) {
				rd.setOrgFinPftDtls(schdData.getFinPftDeatil().copyEntity());
			}

			rd.getRepayMain().setEarlyPayOnSchDate(valueDate);

			recalEarlyPay(rd);

			rd.setBuildProcess("I");
			rd = receiptCalculator.initiateReceipt(rd, false);

			rd.setActualReceiptAmount(rch.getReceiptAmount().subtract(rd.getExcessAvailable()));
			rd.setExcessAvailable(receiptCalculator.getExcessAmount(rd));

			if (rd.isForeClosure()) {
				rch.setReceiptAmount(BigDecimal.ZERO);
				rch.setReceiptAmount(rd.getExcessAvailable());
			} else {
				if (rch.getReceiptDetails().size() > 1) {
					rch.setReceiptAmount(rd.getActualReceiptAmount().add(rd.getExcessAvailable()));
				}
			}
		}

		logger.info(Literal.LEAVING);
		return rd;
	}

	public FinReceiptData validateAllocationsAmount(FinReceiptData receiptData) {
		logger.debug(Literal.ENTERING);

		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinServiceInstruction fsi = finScheduleData.getFinServiceInstruction();
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		List<UploadAlloctionDetail> ulAllocations = fsi.getUploadAllocationDetails();
		List<ReceiptAllocationDetail> allocationsList = rch.getAllocations();
		FinReceiptData aReceiptData = receiptData.copyEntity();
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
						setError(finScheduleData, "30550", parm0);
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

		calcuateDues(aReceiptData);

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
				setError(finScheduleData, "30550", parm0);
				return receiptData;
			}
		}

		for (int i = 0; i < ulAllocations.size(); i++) {
			UploadAlloctionDetail ulAlc = ulAllocations.get(i);
			String ulAlcType = ulAlc.getAllocationType();
			for (int j = 0; j < allocationsList.size(); j++) {
				ReceiptAllocationDetail allocate = allocationsList.get(j);
				String alcType = null;

				if (StringUtils.equals(allocate.getAllocationType(), Allocation.NPFT)) {
					alcType = "I";
					if (StringUtils.equals(alcType, ulAlcType) && allocate.getTotalDue()
							.compareTo(ulAlc.getPaidAmount().add(ulAlc.getWaivedAmount())) != 0) {
						parm0 = "Paid/Waived amount should be equal to Total due " + allocate.getTotalDue()
								+ " for allocation type " + alcType;
						setError(finScheduleData, "30550", parm0);
						return receiptData;
					}
				} else if (StringUtils.equals(allocate.getAllocationType(), Allocation.PRI)) {
					alcType = "P";
					if (StringUtils.equals(alcType, ulAlcType) && allocate.getTotalDue()
							.compareTo(ulAlc.getPaidAmount().add(ulAlc.getWaivedAmount())) != 0) {
						parm0 = "Paid amount should be equal to Total due " + allocate.getTotalDue()
								+ " for allocation type " + alcType;
						setError(finScheduleData, "30550", parm0);
						return receiptData;
					}
				} else if (StringUtils.equals(allocate.getAllocationType(), Allocation.LPFT)) {
					alcType = "L";
					if (StringUtils.equals(alcType, ulAlcType) && allocate.getTotalDue()
							.compareTo(ulAlc.getPaidAmount().add(ulAlc.getWaivedAmount())) != 0) {
						parm0 = "Paid/Waived amount should be equal to Total due " + allocate.getTotalDue()
								+ " for allocation type " + alcType;
						setError(finScheduleData, "30550", parm0);
						return receiptData;
					}
				} else if (StringUtils.equals(allocate.getAllocationType(), Allocation.FEE)) {
					alcType = "F";
					if (StringUtils.equals(alcType, ulAlcType) && allocate.getTotalDue()
							.compareTo(ulAlc.getPaidAmount().add(ulAlc.getWaivedAmount())) != 0) {
						parm0 = "Paid/Waived amount should be equal to Total due " + allocate.getTotalDue()
								+ " for allocation type " + alcType;
						setError(finScheduleData, "30550", parm0);
						return receiptData;
					}
				} else if (StringUtils.equals(allocate.getAllocationType(), Allocation.ODC)) {
					alcType = "O";
					if (StringUtils.equals(alcType, ulAlcType) && allocate.getTotalDue()
							.compareTo(ulAlc.getPaidAmount().add(ulAlc.getWaivedAmount())) != 0) {
						parm0 = "Paid/Waived amount should be equal to Total due " + allocate.getTotalDue()
								+ " for allocation type " + alcType;
						setError(finScheduleData, "30550", parm0);
						return receiptData;
					}
				} else if (StringUtils.equals(allocate.getAllocationType(), Allocation.FUT_NPFT)) {
					alcType = "FI";
					if (StringUtils.equals(alcType, ulAlcType) && allocate.getTotalDue()
							.compareTo(ulAlc.getPaidAmount().add(ulAlc.getWaivedAmount())) != 0) {
						parm0 = "Paid amount should be equal to Total due " + allocate.getTotalDue()
								+ " for allocation type " + alcType;
						setError(finScheduleData, "30550", parm0);
						return receiptData;
					}
				} else if (StringUtils.equals(allocate.getAllocationType(), Allocation.FUT_PFT)) {
					alcType = "FI";
					if (StringUtils.equals(alcType, ulAlcType) && allocate.getTotalDue()
							.compareTo(ulAlc.getPaidAmount().add(ulAlc.getWaivedAmount())) != 0) {
						parm0 = "Paid/Waived amount shoule be equal to Total due " + allocate.getTotalDue()
								+ " for allocation type " + alcType;
						setError(finScheduleData, "30550", parm0);
						return receiptData;
					}
				} else if (StringUtils.equals(allocate.getAllocationType(), Allocation.FUT_PRI)) {
					alcType = "FP";
					if (StringUtils.equals(alcType, ulAlcType) && allocate.getTotalDue()
							.compareTo(ulAlc.getPaidAmount().add(ulAlc.getWaivedAmount())) != 0) {
						parm0 = "Paid amount should be equal to Total due " + allocate.getTotalDue()
								+ " for allocation type " + alcType;
						setError(finScheduleData, "30550", parm0);
						return receiptData;
					}

					if (StringUtils.equals(alcType, ulAlcType)
							&& allocate.getTotalDue().compareTo(ulAlc.getWaivedAmount()) < 0) {
						parm0 = "Waived amount should not be equal to Total due " + allocate.getTotalDue()
								+ " for allocation type " + alcType;
						setError(finScheduleData, "30550", parm0);
						return receiptData;
					}
				} else if (StringUtils.equals(allocate.getAllocationType(), Allocation.EMI)) {
					alcType = "EM";
					if (StringUtils.equals(alcType, ulAlcType)
							&& allocate.getTotalDue().compareTo(ulAlc.getPaidAmount()) != 0) {
						parm0 = "Paid amount should be equal to Total due " + allocate.getTotalDue()
								+ " for allocation type " + alcType;
						setError(finScheduleData, "30550", parm0);
						return receiptData;
					}
				} else if (StringUtils.equals(allocate.getAllocationType(), Allocation.TDS)) {
					continue;
				} else if (StringUtils.equals(allocate.getAllocationType(), Allocation.PFT)) {
					alcType = "PFT";
					if (StringUtils.equals(alcType, ulAlcType) && allocate.getTotalDue()
							.compareTo(ulAlc.getPaidAmount().add(ulAlc.getWaivedAmount())) != 0) {
						parm0 = "Paid/Waived amount shoule be equal to Total due " + allocate.getTotalDue()
								+ " for allocation type " + alcType;
						setError(finScheduleData, "30550", parm0);
						return receiptData;
					}
				} else if (StringUtils.equals(allocate.getAllocationType(), Allocation.MANADV)) {
					alcType = "M";
				} else {
					alcType = "B";
					if (StringUtils.equals(alcType, ulAlcType) && allocate.getTotalDue()
							.compareTo(ulAlc.getPaidAmount().add(ulAlc.getWaivedAmount())) != 0) {
						parm0 = "Paid/Waived amount should be equal to Total due " + allocate.getTotalDue()
								+ " for allocation type " + alcType;
						setError(finScheduleData, "30550", parm0);
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
		List<Long> receiptIdList = finReceiptHeaderDAO.isDedupReceiptExists(fsi);

		if (receiptIdList.isEmpty()) {
			return new ArrayList<>();
		}

		StringBuilder message = new StringBuilder();
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

		List<ErrorDetail> errors = new ArrayList<>();
		errors.add(new ErrorDetail("21005", message.toString(), valueParm));

		return errors;
	}

	@Override
	public FinTaxReceivable getTaxReceivable(long finId, String taxFor) {
		return finODAmzTaxDetailDAO.getFinTaxReceivable(finId, taxFor);
	}

	public FinReceiptData createXcessRCD(FinReceiptData rd) {
		FinReceiptHeader rch = rd.getReceiptHeader();
		List<XcessPayables> xcessPayables = rch.getXcessPayables();
		List<FinReceiptDetail> rcdList = rch.getReceiptDetails();
		rd.getReceiptHeader().setReceiptDetails(rcdList);
		int receiptPurposeCtg = ReceiptUtil.getReceiptPurpose(rch.getReceiptPurpose());

		Map<String, BigDecimal> taxPercMap = null;

		FinScheduleData schdData = rd.getFinanceDetail().getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		for (XcessPayables payable : xcessPayables) {
			if (payable.getTotPaidNow().compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			FinReceiptDetail rcd = new FinReceiptDetail();
			rcd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			rcd.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
			rcd.setValueDate(rch.getValueDate());

			String payableType = payable.getPayableType();
			switch (payableType) {
			case RepayConstants.EXAMOUNTTYPE_EMIINADV:
				rcd.setPaymentType(ReceiptMode.EMIINADV);
				break;
			case RepayConstants.EXAMOUNTTYPE_EXCESS:
				rcd.setPaymentType(ReceiptMode.EXCESS);
				break;
			case RepayConstants.EXAMOUNTTYPE_CASHCLT:
				rcd.setPaymentType(RepayConstants.EXAMOUNTTYPE_CASHCLT);
				break;
			case RepayConstants.EXAMOUNTTYPE_DSF:
				rcd.setPaymentType(RepayConstants.EXAMOUNTTYPE_DSF);
				break;
			default:
				rcd.setPaymentType(ReceiptMode.PAYABLE);
				break;
			}

			rcd.setPayAgainstID(payable.getPayableID());
			if (rd.getTotalPastDues().compareTo(payable.getTotPaidNow()) >= 0) {
				rcd.setDueAmount(payable.getTotPaidNow());
				rd.setTotalPastDues(rd.getTotalPastDues().subtract(payable.getPaidNow()));
			} else {
				rcd.setDueAmount(rd.getTotalPastDues());
				rd.setTotalPastDues(BigDecimal.ZERO);
			}

			if (receiptPurposeCtg == 1) {
				rcd.setAmount(rd.getReceiptHeader().getReceiptAmount());
			} else {
				rcd.setAmount(rcd.getDueAmount());
			}

			rcd.setNoReserve(true);
			rcd.setValueDate(rch.getValueDate());
			rcd.setPayOrder(rcdList.size() + 1);

			if (payable.getPaidGST().compareTo(BigDecimal.ZERO) > 0) {
				ManualAdviseMovements mam = new ManualAdviseMovements();

				mam.setAdviseID(payable.getPayableID());
				mam.setMovementDate(rcd.getReceivedDate());
				mam.setMovementAmount(payable.getTotPaidNow());
				mam.setTaxComponent(payable.getTaxType());
				mam.setPaidAmount(payable.getPaidNow());
				mam.setFeeTypeCode(payable.getFeeTypeCode());

				if (StringUtils.isNotBlank(payable.getTaxType())) {
					if (taxPercMap == null) {
						taxPercMap = GSTCalculator.getTaxPercentages(fm);
					}

					TaxHeader taxHeader = new TaxHeader();
					taxHeader.setNewRecord(true);
					taxHeader.setRecordType(PennantConstants.RCD_ADD);
					taxHeader.setVersion(taxHeader.getVersion() + 1);

					List<Taxes> taxDetails = taxHeader.getTaxDetails();
					taxDetails.add(getTaxDetail(RuleConstants.CODE_CGST, taxPercMap.get(RuleConstants.CODE_CGST),
							payable.getPaidCGST()));
					taxDetails.add(getTaxDetail(RuleConstants.CODE_SGST, taxPercMap.get(RuleConstants.CODE_SGST),
							payable.getPaidSGST()));
					taxDetails.add(getTaxDetail(RuleConstants.CODE_IGST, taxPercMap.get(RuleConstants.CODE_IGST),
							payable.getPaidIGST()));
					taxDetails.add(getTaxDetail(RuleConstants.CODE_UGST, taxPercMap.get(RuleConstants.CODE_UGST),
							payable.getPaidUGST()));
					taxDetails.add(getTaxDetail(RuleConstants.CODE_CESS, taxPercMap.get(RuleConstants.CODE_CESS),
							payable.getPaidCESS()));

					mam.setTaxHeader(taxHeader);
				} else {
					mam.setTaxHeader(null);
				}
				rcd.setPayAdvMovement(mam);
			}

			if (rcd.getReceiptSeqID() <= 0) {
				rcdList.add(rcd);
			}

			if (rd.getTotalPastDues().compareTo(BigDecimal.ZERO) == 0) {
				break;
			}
		}

		rch.setReceiptDetails(rcdList);
		return rd;
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
				if (!(ReceiptMode.EMIINADV.equals(receiptDetail.getPaymentType())
						|| ReceiptMode.EXCESS.equals(receiptDetail.getPaymentType())
						|| ReceiptMode.PAYABLE.equals(receiptDetail.getPaymentType()))) {
					finReceiptDetail = receiptDetail;
					finReceiptDetail.setStatus(finReceiptData.getReceiptHeader().getReceiptModeStatus());
				}
			}
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

		Date lastReceivedDate = finReceiptDetailDAO.getMaxReceivedDate(frh.getFinID());
		if (DateUtil.compare(valueDate, lastReceivedDate) < 0) {
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

		if (!FinServiceEvent.SCHDRPY.equals(receiptData.getReceiptHeader().getReceiptPurpose())) {
			String receiptModeSts = frh.getReceiptModeStatus();
			if (!RepayConstants.PAYSTATUS_BOUNCE.equals(receiptModeSts)
					&& !RepayConstants.PAYSTATUS_CANCEL.equals(receiptModeSts)) {
				if (prvSchdDate != null && valueDate != null && DateUtil.compare(valueDate, prvSchdDate) < 0) {
					String[] valueParm = new String[2];
					valueParm[0] = DateUtil.formatToLongDate(valueDate);
					valueParm[1] = DateUtil.formatToLongDate(prvSchdDate);
					auditHeader.getAuditDetail().setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "RU0012", valueParm, valueParm)));
					auditHeader.setErrorList(auditHeader.getAuditDetail().getErrorDetails());
				}
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
			if (!(ReceiptMode.EMIINADV.equals(receiptDetail.getPaymentType())
					|| ReceiptMode.EXCESS.equals(receiptDetail.getPaymentType())
					|| ReceiptMode.PAYABLE.equals(receiptDetail.getPaymentType()))) {
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
			logger.error(Literal.EXCEPTION, e);
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
		finReceiptHeaderDAO.saveMultiReceipt(finReceiptData.getReceiptHeader(), finReceiptDetail, valueMap);

		finReceiptQueue.setUploadId(finReceiptData.getReceiptHeader().getBatchId());
		finReceiptQueue.setReceiptId(finReceiptData.getReceiptHeader().getReceiptID());
		finReceiptQueue.setThreadId(Thread.currentThread().getId());
		finReceiptQueue.setEndTime(LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + ":"
				+ LocalDateTime.now().getSecond());
		finReceiptQueue.setErrorLog(error);
		finReceiptHeaderDAO.updateMultiReceiptLog(finReceiptQueue);

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
	public String getLoanReferenc(String finreference, String fileName) {
		return finReceiptHeaderDAO.getLoanReferenc(finreference, fileName);
	}

	@Override
	public int geFeeReceiptCountByExtReference(String reference, String receiptPurpose, String extReference) {
		return finReceiptHeaderDAO.geFeeReceiptCountByExtReference(reference, receiptPurpose, extReference);
	}

	private List<ErrorDetail> checkFeeWaiverInProcess(Long finID, String finReference) {
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

	private void prepareFinanceSummary(FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		FinanceSummary summary = new FinanceSummary();

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		summary.setEffectiveRateOfReturn(fm.getEffectiveRateOfReturn());
		summary.setTotalGracePft(fm.getTotalGracePft());
		summary.setTotalGraceCpz(fm.getTotalGraceCpz());
		summary.setTotalGrossGrcPft(fm.getTotalGrossGrcPft());
		summary.setLoanTenor(DateUtil.getMonthsBetween(fm.getFinStartDate(), fm.getMaturityDate()));

		if (FinanceConstants.PRODUCT_ODFACILITY.equals(fm.getProductCategory())) {
			summary.setSanctionAmt(fm.getFinAssetValue());
		}

		if (StringUtils.isBlank(fm.getClosingStatus())) {
			summary.setFinStatus("A");
		} else {
			summary.setFinStatus(fm.getClosingStatus());
		}

		summary.setAdvPaymentAmount(BigDecimal.ZERO);
		summary.setFeeChargeAmt(getTotalFeeAmount(fd));
		fm.setRecordType(PennantConstants.RECORD_TYPE_NEW);

		resetScheduleDetail(schdData);

		prepareProfitDetailSummary(summary, schdData);
		prepareDisbursementSummary(summary, schdData);
		prepareODSummary(fd, summary);

		schdData.setFinanceSummary(summary);

		logger.debug(Literal.LEAVING);
	}

	private BigDecimal getTotalFeeAmount(FinanceDetail fd) {
		List<FinFeeDetail> fees = fd.getFinScheduleData().getFinFeeDetailList();

		BigDecimal totFeeAmount = BigDecimal.ZERO;
		for (FinFeeDetail fee : fees) {
			String schdMthd = fee.getFeeScheduleMethod();
			if (CalculationConstants.REMFEE_PART_OF_DISBURSE.equals(schdMthd)
					|| CalculationConstants.REMFEE_PART_OF_SALE_PRICE.equals(schdMthd)) {
				totFeeAmount = totFeeAmount.add(fee.getActualAmount().subtract(fee.getWaivedAmount()));
			} else {
				totFeeAmount = totFeeAmount.add(fee.getPaidAmount());
			}
		}

		return totFeeAmount;
	}

	public void resetScheduleDetail(FinScheduleData schdData) {
		if (schdData == null || schdData.getFinanceScheduleDetails() == null) {
			return;
		}

		int size = schdData.getFinanceScheduleDetails().size();

		FinanceMain fm = schdData.getFinanceMain();
		if (!FinanceConstants.PRODUCT_ODFACILITY.equals(fm.getProductCategory())) {
			for (int i = size - 1; i >= 0; i--) {
				FinanceScheduleDetail curSchd = schdData.getFinanceScheduleDetails().get(i);
				boolean closingBalance = curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0;

				if (closingBalance && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0) {
					fm.setMaturityDate(curSchd.getSchDate());
					break;
				} else if (closingBalance && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0) {
					schdData.getFinanceScheduleDetails().remove(i);
				}
			}
		}
	}

	private void prepareProfitDetailSummary(FinanceSummary summary, FinScheduleData schdData) {
		FinanceMain fm = schdData.getFinanceMain();

		FinanceProfitDetail fpd = new FinanceProfitDetail();

		fpd = accrualService.calProfitDetails(fm, schdData.getFinanceScheduleDetails(), fpd, fm.getAppDate());
		fm.setRepayProfitRate(fpd.getCurReducingRate());

		summary.setTotalCpz(fpd.getTotalPftCpz());
		summary.setTotalProfit(fpd.getTotalPftSchd());
		summary.setTotalRepayAmt(fpd.getTotalpriSchd().add(fpd.getTotalPftSchd()));
		summary.setNumberOfTerms(fpd.getNOInst());
		summary.setMaturityDate(fpd.getMaturityDate());
		summary.setFirstEmiAmount(fpd.getFirstRepayAmt());
		summary.setNextSchDate(fpd.getNSchdDate());
		summary.setNextRepayAmount(fpd.getNSchdPri().add(fpd.getNSchdPft()));
		summary.setFutureInst(fpd.getFutureInst());
		summary.setFutureTenor(DateUtil.getMonthsBetween(fpd.getNSchdDate(), fpd.getMaturityDate()));
		summary.setFirstInstDate(fpd.getFirstRepayDate());
		summary.setSchdPriPaid(fpd.getTotalPriPaid());
		summary.setSchdPftPaid(fpd.getTotalPftPaid());
		summary.setPaidTotal(fpd.getTotalPriPaid().add(fpd.getTotalPftPaid()));
		summary.setFinLastRepayDate(fpd.getPrvRpySchDate());
		summary.setOutStandPrincipal(fpd.getTotalPriBal());
		summary.setOutStandProfit(fpd.getTotalPftBal());
		summary.setTotalOutStanding(fpd.getTotalPriBal().add(fpd.getTotalPftBal()));
		summary.setPrincipal(fpd.getTdSchdPriBal());
		summary.setFuturePrincipal(fpd.getTotalPriBal().subtract(fpd.getTdSchdPriBal()));
		summary.setInterest(fpd.getTdSchdPftBal());

		if (FinanceConstants.PRODUCT_ODFACILITY.equals(fm.getProductCategory())) {
			summary.setUtilizedAmt(fpd.getTotalPriBal());
			summary.setAvailableAmt(summary.getSanctionAmt().subtract(summary.getUtilizedAmt()));
		}
	}

	private void prepareDisbursementSummary(FinanceSummary summary, FinScheduleData schdData) {
		FinanceMain fm = schdData.getFinanceMain();

		List<FinanceDisbursement> disbList = schdData.getDisbursementDetails();

		if (CollectionUtils.isEmpty(disbList)) {
			return;
		}

		Date disbDate = disbList.get(0).getDisbDate();
		if (disbList.size() == 1) {
			summary.setFirstDisbDate(disbDate);
			summary.setLastDisbDate(disbDate);
		} else {
			disbList = disbList.stream().sorted((b1, b2) -> Integer.compare(b1.getDisbSeq(), b2.getDisbSeq()))
					.collect(Collectors.toList());

			summary.setFirstDisbDate(disbDate);
			summary.setLastDisbDate(disbList.get(disbList.size() - 1).getDisbDate());
		}

		BigDecimal totDisbAmt = BigDecimal.ZERO;
		for (FinanceDisbursement finDisb : disbList) {
			totDisbAmt = totDisbAmt.add(finDisb.getDisbAmount());
		}

		BigDecimal assetValue = fm.getFinAssetValue() == null ? BigDecimal.ZERO : fm.getFinAssetValue();
		if (assetValue.compareTo(BigDecimal.ZERO) == 0 || assetValue.compareTo(totDisbAmt) == 0) {
			summary.setFullyDisb(true);
		}
	}

	private void prepareODSummary(FinanceDetail fd, FinanceSummary summary) {
		FinScheduleData schdData = fd.getFinScheduleData();

		List<FinODDetails> odDetails = schdData.getFinODDetails();

		if (odDetails == null) {
			return;
		}

		BigDecimal overDuePrincipal = BigDecimal.ZERO;
		BigDecimal overDueProfit = BigDecimal.ZERO;
		BigDecimal overDueCharges = BigDecimal.ZERO;
		BigDecimal latePayPftBal = BigDecimal.ZERO;
		BigDecimal totPenaltyBal = BigDecimal.ZERO;
		int odInst = 0;

		for (FinODDetails odDetail : odDetails) {
			overDuePrincipal = overDuePrincipal.add(odDetail.getFinCurODPri());
			overDueProfit = overDueProfit.add(odDetail.getFinCurODPft());
			overDueCharges = overDueCharges.add(odDetail.getTotPenaltyAmt());
			totPenaltyBal = totPenaltyBal.add(odDetail.getTotPenaltyBal());
			latePayPftBal = latePayPftBal.add(odDetail.getLPIBal());
			if (odDetail.getFinCurODAmt().compareTo(BigDecimal.ZERO) > 0) {
				odInst++;
			}
		}

		summary.setOverDuePrincipal(overDuePrincipal);
		summary.setOverDueProfit(overDueProfit);
		summary.setOverDueCharges(overDueCharges);
		summary.setTotalOverDue(overDuePrincipal.add(overDueProfit));
		summary.setDueCharges(totPenaltyBal.add(latePayPftBal));
		summary.setTotalOverDueIncCharges(summary.getTotalOverDue().add(summary.getDueCharges()));
		summary.setFinODDetail(odDetails);
		summary.setOverDueInstlments(odInst);
		summary.setOverDueAmount(summary.getTotalOverDueIncCharges());

		schdData.setFinODDetails(odDetails);
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

	private void setError(FinScheduleData schdData, String errorCode, String... parms) {
		ErrorDetail error = ErrorUtil.getError(errorCode, parms);

		if ("9999".equals(errorCode) && parms.length > 0) {
			error.setMessage(parms[0]);
		}

		StringBuilder logMsg = new StringBuilder();
		logMsg.append("\n");
		logMsg.append("=======================================================\n");
		logMsg.append("Error-Code: ").append(error.getCode()).append("\n");
		logMsg.append("Error-Message: ").append(error.getMessage()).append("\n");
		logMsg.append("=======================================================");
		logMsg.append("\n");

		logger.error(Literal.EXCEPTION, logMsg);

		schdData.setErrorDetail(error);
	}

	private List<FinanceScheduleDetail> copy(List<FinanceScheduleDetail> schedules) {
		List<FinanceScheduleDetail> list = new ArrayList<>();
		schedules.forEach(schedule -> list.add(schedule.copyEntity()));
		return list;
	}

	@Autowired
	public void setLimitCheckDetails(LimitCheckDetails limitCheckDetails) {
		this.limitCheckDetails = limitCheckDetails;
	}

	@Autowired
	public void setLimitManagement(LimitManagement limitManagement) {
		this.limitManagement = limitManagement;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
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
	public void setAllocationDetailDAO(ReceiptAllocationDetailDAO allocationDetailDAO) {
		this.allocationDetailDAO = allocationDetailDAO;
	}

	@Autowired
	public void setRepayProcessUtil(RepaymentProcessUtil repayProcessUtil) {
		this.repayProcessUtil = repayProcessUtil;
	}

	@Autowired
	public void setOverdraftScheduleDetailDAO(OverdraftScheduleDetailDAO overdraftScheduleDetailDAO) {
		this.overdraftScheduleDetailDAO = overdraftScheduleDetailDAO;
	}

	@Autowired
	public void setLatePayMarkingService(LatePayMarkingService latePayMarkingService) {
		this.latePayMarkingService = latePayMarkingService;
	}

	@Autowired
	public void setDepositDetailsDAO(DepositDetailsDAO depositDetailsDAO) {
		this.depositDetailsDAO = depositDetailsDAO;
	}

	@Autowired
	public void setDepositChequesDAO(DepositChequesDAO depositChequesDAO) {
		this.depositChequesDAO = depositChequesDAO;
	}

	@Autowired
	public void setFinODAmzTaxDetailDAO(FinODAmzTaxDetailDAO finODAmzTaxDetailDAO) {
		this.finODAmzTaxDetailDAO = finODAmzTaxDetailDAO;
	}

	@Autowired
	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	@Autowired
	public void setPromotionDAO(PromotionDAO promotionDAO) {
		this.promotionDAO = promotionDAO;
	}

	@Autowired
	public void setInstrumentwiseLimitDAO(InstrumentwiseLimitDAO instrumentwiseLimitDAO) {
		this.instrumentwiseLimitDAO = instrumentwiseLimitDAO;
	}

	@Autowired
	public void setReceiptUploadDetailDAO(ReceiptUploadDetailDAO receiptUploadDetailDAO) {
		this.receiptUploadDetailDAO = receiptUploadDetailDAO;
	}

	@Autowired
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
	public void setReceiptRealizationService(ReceiptRealizationService receiptRealizationService) {
		this.receiptRealizationService = receiptRealizationService;
	}

	@Autowired
	public void setReceiptCancellationService(ReceiptCancellationService receiptCancellationService) {
		this.receiptCancellationService = receiptCancellationService;
	}

	@Autowired
	public void setReceiptResponseDetailDAO(ReceiptResponseDetailDAO receiptResponseDetailDAO) {
		this.receiptResponseDetailDAO = receiptResponseDetailDAO;
	}

	@Autowired
	public void setReceiptUploadHeaderDAO(ReceiptUploadHeaderDAO receiptUploadHeaderDAO) {
		this.receiptUploadHeaderDAO = receiptUploadHeaderDAO;
	}

	@Autowired
	public void setTaxHeaderDetailsDAO(TaxHeaderDetailsDAO taxHeaderDetailsDAO) {
		this.taxHeaderDetailsDAO = taxHeaderDetailsDAO;
	}

	@Autowired
	public void setReasonCodeDAO(ReasonCodeDAO reasonCodeDAO) {
		this.reasonCodeDAO = reasonCodeDAO;
	}

	@Autowired
	public void setFeeWaiverHeaderDAO(FeeWaiverHeaderDAO feeWaiverHeaderDAO) {
		this.feeWaiverHeaderDAO = feeWaiverHeaderDAO;
	}

	@Autowired
	public void setFeeReceiptService(FeeReceiptService feeReceiptService) {
		this.feeReceiptService = feeReceiptService;
	}

	@Autowired
	public void setFinFeeConfigService(FinFeeConfigService finFeeConfigService) {
		this.finFeeConfigService = finFeeConfigService;
	}

	@Autowired(required = false)
	@Qualifier("receiptDetailPostValidationHook")
	public void setPostValidationHook(PostValidationHook postValidationHook) {
		this.postValidationHook = postValidationHook;
	}

	@Autowired
	public void setFinTypeFeesDAO(FinTypeFeesDAO finTypeFeesDAO) {
		this.finTypeFeesDAO = finTypeFeesDAO;
	}

	@Autowired
	public void setExtendedFieldExtensionService(ExtendedFieldExtensionService extendedFieldExtensionService) {
		this.extendedFieldExtensionService = extendedFieldExtensionService;
	}

	@Autowired
	public void setBankDetailDAO(BankDetailDAO bankDetailDAO) {
		this.bankDetailDAO = bankDetailDAO;
	}

	@Autowired
	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

}
