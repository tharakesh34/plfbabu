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
 * * FileName : FeeWaiverHeaderServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 11-06-2015 * *
 * Modified Date : 11-06-2015 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 11-06-2015 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.app.util.RepaymentPostingsUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.FeeWaiverDetailDAO;
import com.pennant.backend.dao.finance.FeeWaiverHeaderDAO;
import com.pennant.backend.dao.finance.FinODAmzTaxDetailDAO;
import com.pennant.backend.dao.finance.FinODCAmountDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.finance.TaxHeaderDetailsDAO;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FeeWaiverDetail;
import com.pennant.backend.model.finance.FeeWaiverHeader;
import com.pennant.backend.model.finance.FinODAmzTaxDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinOverDueChargeMovement;
import com.pennant.backend.model.finance.FinOverDueCharges;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.InvoiceDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.OverdueTaxMovement;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.finance.ScheduleDueTaxDetail;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.finance.FeeWaiverHeaderService;
import com.pennant.backend.service.finance.GSTInvoiceTxnService;
import com.pennant.backend.service.finance.TaxHeaderDetailsService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.payment.model.LoanPayment;
import com.pennanttech.pff.payment.service.LoanPaymentService;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.rits.cloning.Cloner;

/**
 * Service implementation for methods that depends on <b>FeeWaiverHeader</b>.<br>
 * 
 */
public class FeeWaiverHeaderServiceImpl extends GenericService<FeeWaiverHeader> implements FeeWaiverHeaderService {
	private static Logger logger = LogManager.getLogger(FeeWaiverHeaderServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FeeWaiverHeaderDAO feeWaiverHeaderDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private FeeWaiverDetailDAO feeWaiverDetailDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private FeeTypeDAO feeTypeDAO;
	private TaxHeaderDetailsService taxHeaderDetailsService;
	private GSTInvoiceTxnService gstInvoiceTxnService;
	private FinanceTypeDAO financeTypeDAO;
	private FinODAmzTaxDetailDAO finODAmzTaxDetailDAO;
	private TaxHeaderDetailsDAO taxHeaderDetailsDAO;
	private ReceiptCalculator receiptCalculator;
	private RepaymentPostingsUtil repaymentPostingsUtil;
	private FinanceProfitDetailDAO profitDetailsDAO;
	private PresentmentDetailDAO presentmentDetailDAO;
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	private FinServiceInstrutionDAO finServiceInstrutionDAO;
	private LoanPaymentService loanPaymentService;
	private FinODCAmountDAO finODCAmountDAO;

	List<ManualAdvise> manualAdviseList; // TODO remove this

	public FeeWaiverHeaderServiceImpl() {
		super();
	}

	@Override
	public FeeWaiverHeader getFeeWaiverByFinRef(FeeWaiverHeader fwh) {
		logger.debug(Literal.ENTERING);

		long finID = fwh.getFinID();
		String finReference = fwh.getFinReference();

		if (!fwh.isNewRecord()) {
			fwh = getFeeWaiverHeaderByFinRef(finID, "_TView");

			if (fwh != null) {
				setManualAdvise(fwh);
			}

		} else {
			FeeWaiverDetail fwd;
			BigDecimal receivableAmt = BigDecimal.ZERO;
			BigDecimal receivedAmt = BigDecimal.ZERO;
			BigDecimal gstAmt = BigDecimal.ZERO;
			BigDecimal waivedAmt = BigDecimal.ZERO;
			BigDecimal adviseAmt = BigDecimal.ZERO;
			BigDecimal waivedGstAmt = BigDecimal.ZERO;
			BigDecimal waivedGstBounceAmt = BigDecimal.ZERO;

			List<FeeWaiverDetail> detailList = new ArrayList<>();

			boolean receiptExists = finReceiptHeaderDAO.isReceiptExists(finReference, "_Temp");

			if (receiptExists) {
				fwh.setAlwtoProceed(false);
				return fwh;
			}
			/*
			 * if (list != null) { feeWaiverHeader.setAlwtoProceed(false); return feeWaiverHeader; }
			 */

			// For GST Calculations
			FinanceMain fm = new FinanceMain();
			fm.setFinID(finID);
			Map<String, BigDecimal> gstPercentages = GSTCalculator.getTaxPercentages(fm);

			// Manual Advise and Bounce Waivers
			List<ManualAdvise> adviseList = manualAdviseDAO.getManualAdvise(finID);

			String taxComponent = null;
			for (ManualAdvise ma : adviseList) {

				if (ma.getAdviseAmount().subtract(ma.getPaidAmount().add(ma.getWaivedAmount()))
						.compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}

				BigDecimal recAmount = ma.getAdviseAmount().subtract(ma.getWaivedAmount());
				BigDecimal totPaidGst = ma.getPaidCGST()
						.add(ma.getPaidIGST().add(ma.getPaidSGST().add(ma.getPaidUGST()).add(ma.getPaidCESS())));

				if (ma.getBounceID() != 0) {
					ma.setAdviseID(-3);
				}

				BigDecimal currWaiverGst = feeWaiverDetailDAO.getFeeWaiverDetailList(finReference, ma.getAdviseID());

				if (ma.getBounceID() != 0) {
					receivableAmt = receivableAmt.add(recAmount);
					adviseAmt = receivableAmt;
					receivedAmt = receivedAmt.add(ma.getPaidAmount());
					gstAmt = gstAmt.add(totPaidGst);
					waivedAmt = waivedAmt.add(ma.getWaivedAmount());

					if (currWaiverGst != null && currWaiverGst.compareTo(BigDecimal.ZERO) > 0) {
						waivedGstBounceAmt = currWaiverGst;
					} else {
						waivedGstAmt = BigDecimal.ZERO;
						waivedGstBounceAmt = BigDecimal.ZERO;
					}
				} else {
					fwd = new FeeWaiverDetail();
					fwd.setFinID(finID);
					fwd.setFinReference(finReference);
					fwd.setNewRecord(true);
					fwd.setAdviseId(ma.getAdviseID());
					fwd.setFeeTypeCode(ma.getFeeTypeCode());
					fwd.setFeeTypeDesc(ma.getFeeTypeDesc());
					fwd.setWaivedAmount(ma.getWaivedAmount());
					fwd.setTaxApplicable(ma.isTaxApplicable());
					fwd.setTaxComponent(ma.getTaxComponent());
					fwd.setReceivedAmount(ma.getPaidAmount());

					taxComponent = fwd.getTaxComponent();
					if (currWaiverGst != null && currWaiverGst.compareTo(BigDecimal.ZERO) > 0) {
						waivedGstAmt = currWaiverGst;
						fwd.setWaiverGST(waivedGstAmt);
					}
					TaxAmountSplit taxSplit = null;
					if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
						taxSplit = GSTCalculator.getExclusiveGST(ma.getAdviseAmount(), gstPercentages);
					} else {
						taxSplit = GSTCalculator.getInclusiveGST(ma.getAdviseAmount(), gstPercentages);
					}

					fwd.setAdviseAmount(ma.getAdviseAmount());
					fwd.setAdviseGST(taxSplit.gettGST());

					prepareGST(fwd, recAmount, gstPercentages);

					if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
						fwd.setReceivedAmount(fwd.getReceivedAmount().add(totPaidGst));
					}
					fwd.setBalanceAmount(fwd.getReceivableAmount().subtract(fwd.getCurrWaiverAmount()));

					detailList.add(fwd);
				}
			}
			// get Bounce charges
			fwd = new FeeWaiverDetail();
			fwd.setFinID(finID);
			fwd.setFinReference(finReference);
			fwd.setNewRecord(true);
			fwd.setAdviseId(-3);
			fwd.setFeeTypeCode(Allocation.BOUNCE);
			FeeType bounce = this.feeTypeDAO.getApprovedFeeTypeByFeeCode(Allocation.BOUNCE);
			if (bounce != null) {
				fwd.setFeeTypeDesc(bounce.getFeeTypeDesc());
				fwd.setTaxApplicable(bounce.isTaxApplicable());
				fwd.setTaxComponent(bounce.getTaxComponent());
			} else {
				fwd.setFeeTypeDesc(Labels.getLabel("label_ReceiptDialog_BounceCharge.value"));
			}

			taxComponent = fwd.getTaxComponent();
			if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
				receivedAmt = receivedAmt.add(gstAmt);
				gstAmt = BigDecimal.ZERO;
			}
			fwd.setReceivedAmount(receivedAmt);

			if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
				TaxAmountSplit taxSplit = GSTCalculator.getExclusiveGST(adviseAmt, gstPercentages);
				fwd.setAdviseAmount(adviseAmt);
				fwd.setWaivedAmount(waivedAmt);
				fwd.setWaiverGST(waivedGstBounceAmt);
				fwd.setAdviseGST(taxSplit.gettGST());
			}
			prepareGST(fwd, receivableAmt, gstPercentages);

			fwd.setWaivedAmount(waivedAmt);
			fwd.setBalanceAmount(fwd.getReceivableAmount().subtract(fwd.getCurrWaiverAmount()));
			detailList.add(fwd);
			receivableAmt = BigDecimal.ZERO;
			receivedAmt = BigDecimal.ZERO;
			waivedAmt = BigDecimal.ZERO;
			Date reqMaxODDate = SysParamUtil.getAppDate();
			// Late Pay Penalty Waiver
			List<FinODDetails> finODPenaltyList = finODDetailsDAO.getFinODPenalityByFinRef(finID, false, true);

			if (CollectionUtils.isNotEmpty(finODPenaltyList)) {
				for (FinODDetails finoddetails : finODPenaltyList) {
					// lpi amount getting crossed schedule date.
					if (finoddetails.getFinODSchdDate().compareTo(reqMaxODDate) > 0) {
						break;
					}
					/*
					 * receivableAmt = receivableAmt .add(finoddetails.getTotPenaltyBal());
					 */
					receivableAmt = receivableAmt
							.add(finoddetails.getTotPenaltyAmt().subtract(finoddetails.getTotWaived()));

					receivedAmt = receivedAmt.add(finoddetails.getTotPenaltyPaid());
					waivedAmt = waivedAmt.add(finoddetails.getTotWaived());
				}

				if (receivableAmt.subtract(receivedAmt).compareTo(BigDecimal.ZERO) > 0) {
					fwd = new FeeWaiverDetail();
					fwd.setFinID(finID);
					fwd.setFinReference(finReference);
					fwd.setNewRecord(true);
					fwd.setAdviseId(-1);
					fwd.setFeeTypeCode(Allocation.ODC);

					FeeType lpp = this.feeTypeDAO.getApprovedFeeTypeByFeeCode(Allocation.ODC);
					if (lpp != null && StringUtils.isNotBlank(lpp.getTaxComponent())) {
						fwd.setFeeTypeDesc(lpp.getFeeTypeDesc());
						fwd.setTaxApplicable(lpp.isTaxApplicable());
						fwd.setTaxComponent(lpp.getTaxComponent());
					} else {
						fwd.setFeeTypeDesc(Labels.getLabel("label_feeWaiver_WaiverType_ODC"));
					}

					prepareGST(fwd, receivableAmt, gstPercentages);

					fwd.setWaivedAmount(waivedAmt);
					fwd.setBalanceAmount(fwd.getReceivableAmount().subtract(fwd.getCurrWaiverAmount()));
					fwd.setReceivedAmount(receivedAmt);
					detailList.add(fwd);
				}
			}

			receivableAmt = BigDecimal.ZERO;
			receivedAmt = BigDecimal.ZERO;
			waivedAmt = BigDecimal.ZERO;
			List<FinODDetails> finODProfitList = finODDetailsDAO.getFinODPenalityByFinRef(finID, true, true);

			// Late pay profit Waivers
			if (CollectionUtils.isNotEmpty(finODProfitList)) {
				for (FinODDetails finoddetails : finODProfitList) {
					// lpp amount getting crossed schedule date.
					if (finoddetails.getFinODSchdDate().compareTo(reqMaxODDate) > 0) {
						break;
					}
					receivableAmt = receivableAmt.add(finoddetails.getLPIAmt().subtract(finoddetails.getLPIWaived()));
					receivedAmt = receivedAmt.add(finoddetails.getLPIPaid());
					waivedAmt = waivedAmt.add(finoddetails.getLPIWaived());
				}

				fwd = new FeeWaiverDetail();
				fwd.setFinID(finID);
				fwd.setFinReference(finReference);
				fwd.setNewRecord(true);
				fwd.setAdviseId(-2);
				fwd.setFeeTypeCode(Allocation.LPFT);
				fwd.setFeeTypeDesc(Labels.getLabel("label_feeWaiver_WaiverType_LPFT"));
				fwd.setReceivableAmount(receivableAmt);
				fwd.setReceivedAmount(receivedAmt);
				fwd.setWaivedAmount(waivedAmt);

				prepareGST(fwd, receivableAmt, gstPercentages);

				fwd.setBalanceAmount(fwd.getReceivableAmount().subtract(fwd.getCurrWaiverAmount()));

				detailList.add(fwd);
			}

			// Schedule Profit Waiver
			if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_PROFIT_WAIVER)) {
				// Get Interest and profits.
				List<FinanceScheduleDetail> schedules = this.financeScheduleDetailDAO.getFinScheduleDetails(finID, "",
						false);

				receivableAmt = BigDecimal.ZERO;
				receivedAmt = BigDecimal.ZERO;
				waivedAmt = BigDecimal.ZERO;

				Date appDate = SysParamUtil.getAppDate();
				for (FinanceScheduleDetail detail : schedules) {
					if (detail.getSchDate().compareTo(appDate) > 0) {
						break;
					}
					if ((detail.getProfitSchd()).compareTo(detail.getSchdPftPaid()) > 0) {
						receivableAmt = receivableAmt.add(detail.getProfitSchd());
						receivedAmt = receivedAmt.add(detail.getSchdPftPaid());
						waivedAmt = waivedAmt.add(detail.getSchdPftWaiver());
					}
				}

				fwd = new FeeWaiverDetail();
				fwd.setFinID(finID);
				fwd.setFinReference(finReference);
				fwd.setNewRecord(true);
				fwd.setAdviseId(-4);
				fwd.setFeeTypeCode(Allocation.PFT);
				fwd.setFeeTypeDesc(Labels.getLabel("label_feeWaiver_WaiverType_Interest"));
				fwd.setReceivableAmount(receivableAmt);
				fwd.setReceivedAmount(receivedAmt);
				fwd.setWaivedAmount(waivedAmt);

				prepareGST(fwd, receivableAmt, gstPercentages);

				fwd.setBalanceAmount(fwd.getReceivableAmount().subtract(fwd.getCurrWaiverAmount()));
				fwd.setWaiverType(RepayConstants.INTEREST_WAIVER);
				detailList.add(fwd);
			}

			fwh.setFeeWaiverDetails(detailList);
		}

		logger.debug(Literal.LEAVING);
		return fwh;
	}

	private void setManualAdvise(FeeWaiverHeader fwh) {
		List<FeeWaiverDetail> fwdList = fwh.getFeeWaiverDetails();

		if (fwdList.isEmpty()) {
			return;
		}

		List<ManualAdvise> adviseList = manualAdviseDAO.getManualAdvise(fwh.getFinID());
		if (adviseList.isEmpty()) {
			return;
		}

		for (FeeWaiverDetail fwd : fwdList) {
			for (ManualAdvise ma : adviseList) {
				if (ma.getAdviseID() == fwd.getAdviseId()) {
					fwd.setAdviseAmount(ma.getAdviseAmount());
					break;
				}
			}
		}
	}

	private void prepareGST(FeeWaiverDetail fwd, BigDecimal receivableAmt, Map<String, BigDecimal> gstPercentages) {

		if (!fwd.isTaxApplicable()) {
			fwd.setActualReceivable(receivableAmt);
			fwd.setReceivableAmount(receivableAmt);
			fwd.setReceivableGST(BigDecimal.ZERO);
			return;
		}

		BigDecimal receivableAmount = BigDecimal.ZERO;
		TaxAmountSplit taxSplit = null;
		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(fwd.getTaxComponent())) {
			taxSplit = GSTCalculator.getExclusiveGST(receivableAmt, gstPercentages);
			fwd.setActualReceivable(receivableAmt);
			receivableAmount = receivableAmt.add(taxSplit.gettGST());
		} else {
			taxSplit = GSTCalculator.getInclusiveGST(receivableAmt, gstPercentages);
			fwd.setActualReceivable(receivableAmt.subtract(taxSplit.gettGST()));
			receivableAmount = taxSplit.getNetAmount();
		}

		fwd.setReceivableGST(taxSplit.gettGST());
		fwd.setReceivableAmount(receivableAmount);

		List<Taxes> taxes = new ArrayList<>();

		for (String gstType : gstPercentages.keySet()) {
			Taxes tax = new Taxes();

			if (RuleConstants.CODE_CGST.equals(gstType)) {
				tax.setActualTax(taxSplit.getcGST());
			} else if (RuleConstants.CODE_SGST.equals(gstType)) {
				tax.setActualTax(taxSplit.getsGST());
			} else if (RuleConstants.CODE_IGST.equals(gstType)) {
				tax.setActualTax(taxSplit.getiGST());
			} else if (RuleConstants.CODE_UGST.equals(gstType)) {
				tax.setActualTax(taxSplit.getuGST());
			} else if (RuleConstants.CODE_CESS.equals(gstType)) {
				tax.setActualTax(taxSplit.getCess());
			} else {
				continue;
			}

			tax.setTaxPerc(gstPercentages.get(gstType));
			tax.setNetTax(tax.getActualTax().subtract(tax.getWaivedTax()));
			tax.setRemFeeTax(tax.getNetTax().subtract(tax.getPaidTax()));

			tax.setTaxType(gstType);
			tax.setNewRecord(true);
			tax.setRecordType(PennantConstants.RCD_ADD);
			tax.setVersion(1);
			taxes.add(tax);
		}

		TaxHeader taxHeader = new TaxHeader();
		taxHeader.setNewRecord(true);
		taxHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		taxHeader.setVersion(1);
		taxHeader.setTaxDetails(taxes);
		fwd.setTaxHeader(taxHeader);
	}

	public FeeWaiverHeader getFeeWiaverEnquiryList(FeeWaiverHeader fwh) {
		List<FeeWaiverDetail> details = feeWaiverDetailDAO.getFeeWaiverEnqDetailList(fwh.getFinReference());
		fwh.setFeeWaiverDetails(details);
		return fwh;
	}

	@Override
	public FeeWaiverHeader getFeeWaiverHeaderByFinRef(long finID, String type) {
		FeeWaiverHeader fwh = feeWaiverHeaderDAO.getFeeWaiverHeaderByFinRef(finID, type);

		if (fwh == null) {
			return null;
		}

		// Fetch Fee Waiver Details
		List<FeeWaiverDetail> fwd = feeWaiverDetailDAO.getFeeWaiverByWaiverId(fwh.getWaiverId(), "_Temp");
		fwh.setFeeWaiverDetails(fwd);

		String finReference = fwh.getFinReference();
		for (FeeWaiverDetail feeWaiver : fwd) {
			feeWaiver.setFinID(finID);
			feeWaiver.setFinReference(finReference);
			if (feeWaiver.getTaxHeaderId() != null) {
				// Fetch Tax Details
				TaxHeader header = taxHeaderDetailsService.getTaxHeaderById(feeWaiver.getTaxHeaderId(), "_Temp");
				feeWaiver.setTaxHeader(header);
			}
		}

		return fwh;
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		FeeWaiverHeader fwh = (FeeWaiverHeader) auditHeader.getAuditDetail().getModelData();

		List<FinServiceInstruction> fsiList = getServiceInstructions(fwh);

		long serviceUID = Long.MIN_VALUE;

		if (fwh.getExtendedFieldRender() != null
				&& fwh.getExtendedFieldRender().getInstructionUID() != Long.MIN_VALUE) {
			serviceUID = fwh.getExtendedFieldRender().getInstructionUID();
		}

		for (FinServiceInstruction finServInst : fsiList) {
			serviceUID = finServInst.getInstructionUID();
			if (ObjectUtils.isEmpty(finServInst.getInitiatedDate())) {
				finServInst.setInitiatedDate(SysParamUtil.getAppDate());
			}
		}

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		List<AuditDetail> auditDetails = new ArrayList<>();
		fwh = (FeeWaiverHeader) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (fwh.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		fwh.setStatus("I");
		fwh.setLinkedTranId(0);
		if (fwh.isNewRecord()) {
			fwh.setWaiverId(Long.parseLong(feeWaiverHeaderDAO.save(fwh, tableType)));
			auditHeader.getAuditDetail().setModelData(fwh);
			auditHeader.setAuditReference(String.valueOf(fwh.getWaiverId()));
		} else {
			feeWaiverHeaderDAO.update(fwh, tableType);
		}

		if (CollectionUtils.isNotEmpty(fwh.getFeeWaiverDetails())) {
			List<AuditDetail> details = fwh.getAuditDetailMap().get("FeeWaiverDetails");
			for (FeeWaiverDetail feewaiver : fwh.getFeeWaiverDetails()) {
				feewaiver.setWaiverId(fwh.getWaiverId());
				TaxHeader taxHeader = feewaiver.getTaxHeader();
				if (taxHeader != null && CollectionUtils.isNotEmpty(taxHeader.getTaxDetails())) {
					taxHeaderDetailsService.saveOrUpdate(taxHeader, tableType.getSuffix(),
							auditHeader.getAuditTranType());
					feewaiver.setTaxHeaderId(taxHeader.getHeaderId());
				}
			}
			details = processingFeeWaiverdetails(details, tableType);
			auditDetails.addAll(details);
		}

		String rcdMaintainSts = FinServiceEvent.FEEWAIVERS;
		financeMainDAO.updateMaintainceStatus(fwh.getFinID(), rcdMaintainSts);

		// FinServiceInstrution
		if (CollectionUtils.isNotEmpty(fwh.getFinServiceInstructions()) && fwh.isNewRecord()) {
			finServiceInstrutionDAO.saveList(fwh.getFinServiceInstructions(), tableType.getSuffix());
		}

		// Extended field Details
		if (fwh.getExtendedFieldRender() != null) {
			List<AuditDetail> details = fwh.getAuditDetailMap().get("ExtendedFieldDetails");
			details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
					ExtendedFieldConstants.MODULE_LOAN, fwh.getExtendedFieldHeader().getEvent(), tableType.getSuffix(),
					serviceUID);
			auditDetails.addAll(details);
		}

		// Add Audit
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	private List<FinServiceInstruction> getServiceInstructions(FeeWaiverHeader fwh) {
		logger.debug(Literal.ENTERING);

		List<FinServiceInstruction> siList = fwh.getFinServiceInstructions();

		if (CollectionUtils.isEmpty(siList)) {
			FinServiceInstruction fsi = new FinServiceInstruction();
			fsi.setFinID(fwh.getFinID());
			fsi.setFinReference(fwh.getFinReference());
			fsi.setFinEvent(fwh.getEvent());

			fwh.setFinServiceInstruction(fsi);
		}

		for (FinServiceInstruction fsi : fwh.getFinServiceInstructions()) {
			if (fsi.getInstructionUID() == Long.MIN_VALUE) {
				fsi.setInstructionUID(Long.valueOf(ReferenceGenerator.generateNewServiceUID()));
			}

			if (StringUtils.isEmpty(fwh.getEvent()) || FinServiceEvent.ORG.equals(fwh.getEvent())) {
				if (!FinServiceEvent.ORG.equals(fsi.getFinEvent()) && !StringUtils.contains(fsi.getFinEvent(), "_O")) {
					fsi.setFinEvent(fsi.getFinEvent().concat("_O"));
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return fwh.getFinServiceInstructions();
	}

	private List<AuditDetail> processingFeeWaiverdetails(List<AuditDetail> auditDetails, TableType type) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			FeeWaiverDetail feeWaiverDetail = (FeeWaiverDetail) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type.getSuffix())) {
				approveRec = true;
				feeWaiverDetail.setRoleCode("");
				feeWaiverDetail.setNextRoleCode("");
				feeWaiverDetail.setTaskId("");
				feeWaiverDetail.setNextTaskId("");
				feeWaiverDetail.setWorkflowId(0);
			}

			if (feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (feeWaiverDetail.isNewRecord()) {
				saveRecord = true;
				if (feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					feeWaiverDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					feeWaiverDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					feeWaiverDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (feeWaiverDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (feeWaiverDetail.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = feeWaiverDetail.getRecordType();
				recordStatus = feeWaiverDetail.getRecordStatus();
				feeWaiverDetail.setRecordType("");
				feeWaiverDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				feeWaiverDetailDAO.save(feeWaiverDetail, type);
			}

			if (updateRecord) {
				feeWaiverDetailDAO.update(feeWaiverDetail, type);
			}

			if (deleteRecord) {
				feeWaiverDetailDAO.delete(feeWaiverDetail, type);
			}

			if (approveRec) {
				feeWaiverDetail.setRecordType(rcdType);
				feeWaiverDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(feeWaiverDetail);
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;

	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();

		auditHeader = businessValidation(auditHeader, "delete");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FeeWaiverHeader feeWaiverHeader = (FeeWaiverHeader) auditHeader.getAuditDetail().getModelData();
		feeWaiverHeaderDAO.delete(feeWaiverHeader, TableType.MAIN_TAB);

		finServiceInstrutionDAO.deleteList(feeWaiverHeader.getFinID(), feeWaiverHeader.getEvent(), "_Temp");
		// Extended field Render Details.

		List<AuditDetail> extendedDetails = feeWaiverHeader.getAuditDetailMap().get("ExtendedFieldDetails");
		if (extendedDetails != null && extendedDetails.size() > 0) {
			auditDetails.addAll(extendedFieldDetailsService.delete(feeWaiverHeader.getExtendedFieldHeader(),
					feeWaiverHeader.getFinReference(), feeWaiverHeader.getExtendedFieldRender().getSeqNo(), "_Temp",
					auditHeader.getAuditTranType(), extendedDetails));
		}

		auditHeader.setAuditDetails(auditDetails);

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();

		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		String tranType = "";

		FeeWaiverHeader fwh = new FeeWaiverHeader();
		BeanUtils.copyProperties((FeeWaiverHeader) auditHeader.getAuditDetail().getModelData(), fwh);

		List<FinServiceInstruction> serviceInstructions = getServiceInstructions(fwh);

		long serviceUID = Long.MIN_VALUE;

		if (fwh.getExtendedFieldRender() != null
				&& fwh.getExtendedFieldRender().getInstructionUID() != Long.MIN_VALUE) {
			serviceUID = fwh.getExtendedFieldRender().getInstructionUID();
		}

		Date appDate = SysParamUtil.getAppDate();
		for (FinServiceInstruction finServInst : serviceInstructions) {
			serviceUID = finServInst.getInstructionUID();
			if (ObjectUtils.isEmpty(finServInst.getInitiatedDate())) {
				finServInst.setInitiatedDate(appDate);
			}
		}

		if (!PennantConstants.RECORD_TYPE_NEW.equals(fwh.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(feeWaiverHeaderDAO.getFeeWaiverHeaderById(fwh.getWaiverId(), ""));
		}

		if (fwh.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			feeWaiverHeaderDAO.delete(fwh, TableType.MAIN_TAB);
		} else {
			fwh.setRoleCode("");
			fwh.setNextRoleCode("");
			fwh.setTaskId("");
			fwh.setNextTaskId("");
			fwh.setWorkflowId(0);
			fwh.setStatus("R");

			allocateWaiverAmounts(fwh);

			if (PennantConstants.RECORD_TYPE_NEW.equals(fwh.getRecordType())) {
				tranType = PennantConstants.TRAN_ADD;
				feeWaiverHeaderDAO.save(fwh, TableType.MAIN_TAB);
				fwh.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				fwh.setRecordType("");
				feeWaiverHeaderDAO.update(fwh, TableType.MAIN_TAB);
			}

			// Fee Waivers List
			if (CollectionUtils.isNotEmpty(fwh.getFeeWaiverDetails())) {
				for (FeeWaiverDetail fwd : fwh.getFeeWaiverDetails()) {
					fwd.setWaiverId(fwh.getWaiverId());
					TaxHeader taxHeader = fwd.getTaxHeader();
					if (taxHeader != null && CollectionUtils.isNotEmpty(taxHeader.getTaxDetails())) {
						taxHeaderDetailsService.doApprove(taxHeader, "", auditHeader.getAuditTranType());
						fwd.setTaxHeaderId(taxHeader.getHeaderId());
					}

					if (PennantConstants.RECORD_TYPE_NEW.equals(fwh.getRecordType())) {
						feeWaiverDetailDAO.save(fwd, TableType.MAIN_TAB);
						fwh.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else {
						feeWaiverDetailDAO.update(fwd, TableType.MAIN_TAB);
					}
					if (!PennantConstants.FINSOURCE_ID_API.equals(fwh.getFinSourceID())
							&& !UploadConstants.FINSOURCE_ID_UPLOAD.equals(fwh.getFinSourceID())) {
						feeWaiverDetailDAO.delete(fwd, TableType.TEMP_TAB);
					}
				}

			}

		}
		fwh.setRecordType("");

		if (!PennantConstants.FINSOURCE_ID_API.equals(fwh.getFinSourceID())
				&& !UploadConstants.FINSOURCE_ID_UPLOAD.equals(fwh.getFinSourceID())) {
			feeWaiverHeaderDAO.delete(fwh, TableType.TEMP_TAB);
		}

		financeMainDAO.updateMaintainceStatus(fwh.getFinID(), "");

		if (CollectionUtils.isNotEmpty(fwh.getFinServiceInstructions())) {
			finServiceInstrutionDAO.saveList(fwh.getFinServiceInstructions(), "");
		}

		// Extended field Render Details.
		List<AuditDetail> details = fwh.getAuditDetailMap().get("ExtendedFieldDetails");
		if (fwh.getExtendedFieldRender() != null) {
			details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
					ExtendedFieldConstants.MODULE_LOAN, fwh.getExtendedFieldHeader().getEvent(), "", serviceUID);
			auditDetails.addAll(details);
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(fwh);
		auditHeaderDAO.addAudit(auditHeader);

		finServiceInstrutionDAO.deleteList(fwh.getFinID(), fwh.getEvent(), "_Temp");

		if (details != null && details.size() > 0) {
			extendedFieldDetailsService.delete(fwh.getExtendedFieldHeader(), fwh.getFinReference(),
					fwh.getExtendedFieldRender().getSeqNo(), "_Temp", auditHeader.getAuditTranType(), details);
		}

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private void allocateWaiverAmounts(FeeWaiverHeader fwh) {
		long finID = fwh.getFinID();

		List<FinODDetails> odList = finODDetailsDAO.getFinODPenalityByFinRef(finID, true, false);
		List<FinODDetails> lppList = finODDetailsDAO.getFinODPenalityByFinRef(finID, false, false);

		List<ManualAdviseMovements> movements = new ArrayList<>();

		// Update ManualAdvise and Bounce Waivers
		List<ManualAdviseMovements> advMovements = allocateWaiverToBounceAndAdvise(fwh);
		movements.addAll(advMovements);

		// Update Late Pay Penalty(LPP) and Late Pay Interest(LPI) waivers
		List<ManualAdviseMovements> lppMovements = allocateWaivedAmtToPenalities(fwh, odList, lppList);
		movements.addAll(lppMovements);

		FinanceDetail fd = new FinanceDetail();
		FinanceMain fm = this.financeMainDAO.getFinanceMainById(finID, "", false);
		FinScheduleData schdData = fd.getFinScheduleData();
		schdData.setFinanceMain(fm);

		List<FinanceScheduleDetail> schedules = this.financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false);
		schdData.setFinanceScheduleDetails(schedules);

		schdData.setFinanceType(financeTypeDAO.getFinanceTypeByFinType(fm.getFinType()));

		fd.setCustomerDetails(null);
		fd.setFinanceTaxDetail(null);

		// Profit Details
		Date appDate = SysParamUtil.getAppDate();
		FinanceProfitDetail pftDetail = profitDetailsDAO.getFinProfitDetailsById(finID);
		boolean isPresentmentInProcess = presentmentDetailDAO.isPresentmentInProcess(finID);

		// Overdue Details
		List<FinODDetails> overdueList = finODDetailsDAO.getFinODBalByFinRef(finID);

		fm = repaymentPostingsUtil.updateStatus(fm, appDate, schedules, pftDetail, overdueList, null,
				isPresentmentInProcess);

		if (!fm.isFinIsActive()) {
			financeMainDAO.updateMaturity(finID, FinanceConstants.CLOSE_STATUS_MATURED, false, appDate);
		}

		AEEvent aeEvent = null;

		if (CollectionUtils.isEmpty(movements)) {
			// Update Profit waivers
			if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_PROFIT_WAIVER)) {
				fd = allocateWaiverToSchduleDetails(fwh, fd, aeEvent, true);
			}
		}

		if (CollectionUtils.isNotEmpty(movements)) {
			BigDecimal totPftBal = BigDecimal.ZERO;

			// Total Pft bal
			if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_PROFIT_WAIVER)) {
				totPftBal = getTotPftBal(fwh, fd, aeEvent);
			}

			// Execute Accounting
			aeEvent = executeAcctProcessing(totPftBal, fm, fwh, aeEvent, movements);

			// Prepare GST Invoice for Bounce/LPP Waiver(when it is due base accounting)
			if (aeEvent.getLinkedTranId() > 0 && aeEvent.isPostingSucess()) {
				if (SysParamUtil.isAllowed(SMTParameterConstants.GST_INV_ON_DUE)) {
					List<ManualAdviseMovements> waiverMovements = new ArrayList<>();

					InvoiceDetail id = new InvoiceDetail();
					id.setLinkedTranId(aeEvent.getLinkedTranId());
					id.setFinanceDetail(fd);
					id.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_CREDIT);
					id.setWaiver(true);

					for (ManualAdviseMovements advMov : advMovements) {
						waiverMovements.add(advMov);

						id.setMovements(waiverMovements);
						Long invoiceID = this.gstInvoiceTxnService.advTaxInvoicePreparation(id);

						if (advMov.getTaxHeader() != null) {
							advMov.getTaxHeader().setInvoiceID(invoiceID);
						}

						waiverMovements.clear();
					}

					// Penalty Overdue GST Creation
					prepareTaxMovement(fm, lppMovements, aeEvent.getLinkedTranId());

				}
				if (fwh.getRpyList() != null && !fwh.getRpyList().isEmpty()) {
					for (int i = 0; i < fwh.getRpyList().size(); i++) {
						FinanceRepayments repayment = fwh.getRpyList().get(i);
						repayment.setLinkedTranId(aeEvent.getLinkedTranId());
						this.financeRepaymentsDAO.save(repayment, TableType.MAIN_TAB.getSuffix());
					}
				}
			}

			// Profit Waiver GST Invoice Preparation and Schedule details updation
			if (aeEvent.getLinkedTranId() > 0 && aeEvent.isPostingSucess()) {
				fd = allocateWaiverToSchduleDetails(fwh, fd, aeEvent, false);
			}
		} else {

		}

		// Common issue#21
		// Loan Active Status Verification
		List<FinanceScheduleDetail> schdList = this.financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false);

		LoanPayment lp = new LoanPayment(finID, fm.getFinReference(), schdList, appDate);
		boolean isFinFullyPaid = loanPaymentService.isSchdFullyPaid(lp);

		if (isFinFullyPaid) {
			financeMainDAO.updateMaturity(finID, FinanceConstants.CLOSE_STATUS_MATURED, false, null);
			profitDetailsDAO.updateFinPftMaturity(finID, FinanceConstants.CLOSE_STATUS_MATURED, false);
		}

		fm = repaymentPostingsUtil.updateStatus(fm, appDate, schedules, pftDetail, overdueList, null, false);

		if (!fm.isFinIsActive()) {
			financeMainDAO.updateMaturity(finID, FinanceConstants.CLOSE_STATUS_MATURED, false, appDate);
		}
	}

	private void prepareTaxMovement(FinanceMain fm, List<ManualAdviseMovements> lppMovList, long linkedTranID) {

		if (lppMovList == null || lppMovList.isEmpty()) {
			return;
		}

		// Prepare GST Details based on Invoice ID which was incomized
		List<OverdueTaxMovement> odTaxMovList = new ArrayList<>();
		List<FinODAmzTaxDetail> dueTaxList = finODAmzTaxDetailDAO.getODTaxList(fm.getFinID());

		// Sorting Due Tax details based on Valuedate
		dueTaxList = sortDueTaxDetails(dueTaxList);
		OverdueTaxMovement movement = null;
		List<FinODAmzTaxDetail> updateDueList = new ArrayList<>();

		for (ManualAdviseMovements advMov : lppMovList) {

			// if No Waiver Amount & Paid Amounts on the schedule date
			BigDecimal lppWaived = advMov.getWaivedAmount();
			if (lppWaived.compareTo(BigDecimal.ZERO) == 0) {
				continue;
			}

			// Looping Due Details and adjust based on balance Amount
			for (FinODAmzTaxDetail taxDetail : dueTaxList) {

				BigDecimal balAmount = taxDetail.getAmount().subtract(taxDetail.getPaidAmount())
						.subtract(taxDetail.getWaivedAmount());
				if (balAmount.compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}

				movement = new OverdueTaxMovement();
				movement.setInvoiceID(taxDetail.getInvoiceID());
				movement.setValueDate(taxDetail.getValueDate());
				movement.setSchDate(advMov.getSchDate());
				movement.setFinReference(fm.getFinReference());
				movement.setTaxFor(taxDetail.getTaxFor());

				if (balAmount.compareTo(advMov.getWaivedAmount()) > 0) {
					movement.setWaivedAmount(advMov.getWaivedAmount());
				} else {
					movement.setWaivedAmount(balAmount);
				}
				taxDetail.setWaivedAmount(taxDetail.getWaivedAmount().add(movement.getWaivedAmount()));
				lppWaived = lppWaived.subtract(movement.getWaivedAmount());

				updateDueList.add(taxDetail);

				// Tax Header Preparation
				if (advMov.getTaxHeader() != null) {
					Cloner cloner = new Cloner();
					TaxHeader taxHeader = cloner.deepClone(advMov.getTaxHeader());
					taxHeader.setHeaderId(0);
					if (CollectionUtils.isNotEmpty(taxHeader.getTaxDetails())) {

						for (Taxes taxes : taxHeader.getTaxDetails()) {
							BigDecimal paidAmount = movement.getPaidAmount();
							BigDecimal waivedAmount = movement.getWaivedAmount();
							if (StringUtils.equals(advMov.getTaxComponent(),
									FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE)) {
								if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
									paidAmount = GSTCalculator.getInclusiveAmount(paidAmount, taxes.getTaxPerc());
								}
								if (waivedAmount.compareTo(BigDecimal.ZERO) > 0) {
									waivedAmount = GSTCalculator.getInclusiveAmount(waivedAmount, taxes.getTaxPerc());
								}
							}

							if (movement.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
								taxes.setPaidTax(
										GSTCalculator.getExclusiveTax(movement.getPaidAmount(), taxes.getTaxPerc()));
							} else {
								taxes.setPaidTax(BigDecimal.ZERO);
							}
							if (movement.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0) {
								taxes.setWaivedTax(
										GSTCalculator.getExclusiveTax(movement.getWaivedAmount(), taxes.getTaxPerc()));
							} else {
								taxes.setWaivedTax(BigDecimal.ZERO);
							}
						}
					}
					movement.setTaxHeader(taxHeader);
				}

				odTaxMovList.add(movement);
				if (lppWaived.compareTo(BigDecimal.ZERO) == 0) {
					break;
				}
			}
		}

		// If Amount was incomized on month end then waivers should be created as Credit invoice
		if (!odTaxMovList.isEmpty()) {

			// Fee Type is amortization Req?
			boolean isFeeTypeAmortzReq = feeTypeDAO.isFeeTypeAmortzReq(Allocation.ODC);
			if (isFeeTypeAmortzReq) {

				FinanceDetail financeDetail = new FinanceDetail();
				financeDetail.getFinScheduleData().setFinanceMain(fm);

				InvoiceDetail invoiceDetail = new InvoiceDetail();
				invoiceDetail.setLinkedTranId(linkedTranID);
				invoiceDetail.setFinanceDetail(financeDetail);
				invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_CREDIT);
				invoiceDetail.setWaiver(true);

				for (OverdueTaxMovement taxMovement : odTaxMovList) {

					ManualAdviseMovements advMov = lppMovList.get(0);

					advMov.setMovementAmount(taxMovement.getPaidAmount().add(taxMovement.getWaivedAmount()));
					advMov.setPaidAmount(taxMovement.getPaidAmount());
					advMov.setWaivedAmount(taxMovement.getWaivedAmount());
					advMov.setTaxHeader(taxMovement.getTaxHeader());
					advMov.setDebitInvoiceId(taxMovement.getInvoiceID());

					List<ManualAdviseMovements> advMovements = new ArrayList<>();
					advMovements.add(advMov);

					invoiceDetail.setMovements(advMovements);

					Long invoiceID = this.gstInvoiceTxnService.advTaxInvoicePreparation(invoiceDetail);

					if (taxMovement.getTaxHeader() != null) {
						taxMovement.getTaxHeader().setInvoiceID(invoiceID);
					}
				}
			}

			// Updating OD Due tax details
			if (!updateDueList.isEmpty()) {
				finODAmzTaxDetailDAO.updateODTaxDueList(updateDueList);
			}

			// Saving Overdue Tax Movements
			for (OverdueTaxMovement taxMovement : odTaxMovList) {
				if (taxMovement.getTaxHeader() != null) {
					long headerId = taxHeaderDetailsDAO.save(taxMovement.getTaxHeader(), "");
					if (CollectionUtils.isNotEmpty(taxMovement.getTaxHeader().getTaxDetails())) {
						for (Taxes taxes : taxMovement.getTaxHeader().getTaxDetails()) {
							taxes.setReferenceId(headerId);
						}
						taxHeaderDetailsDAO.saveTaxes(taxMovement.getTaxHeader().getTaxDetails(), "");
					}
					taxMovement.setTaxHeaderId(headerId);
				}
			}
			finODAmzTaxDetailDAO.saveTaxList(odTaxMovList);
		}

	}

	private List<FinODAmzTaxDetail> sortDueTaxDetails(List<FinODAmzTaxDetail> dueTaxList) {
		if (dueTaxList != null && dueTaxList.size() > 0) {
			Collections.sort(dueTaxList, new Comparator<FinODAmzTaxDetail>() {
				@Override
				public int compare(FinODAmzTaxDetail detail1, FinODAmzTaxDetail detail2) {
					return DateUtil.compare(detail1.getValueDate(), detail2.getValueDate());
				}
			});
		}
		return dueTaxList;
	}

	private List<ManualAdviseMovements> allocateWaivedAmtToPenalities(FeeWaiverHeader fwh,
			List<FinODDetails> finodPftdetails, List<FinODDetails> odPenalityList) {
		logger.debug(Literal.ENTERING);

		List<ManualAdviseMovements> movements = new ArrayList<>();
		Date appDate = SysParamUtil.getAppDate();
		List<FinanceRepayments> rpyList = new ArrayList<>();

		FinanceMain fm = new FinanceMain();
		fm.setFinID(fwh.getFinID());
		long finID = fwh.getFinID();
		Date postDate = fwh.getPostingDate();

		Map<String, BigDecimal> gstPercentages = GSTCalculator.getTaxPercentages(fm);

		for (FeeWaiverDetail fwd : fwh.getFeeWaiverDetails()) {
			if (fwd.getCurrWaiverAmount().compareTo(BigDecimal.ZERO) == 0) {
				continue;
			}

			// update late pay penalty waived amounts to the Finoddetails table.
			if (Allocation.ODC.equals(StringUtils.trimToEmpty(fwd.getFeeTypeCode()))) {

				BigDecimal curwaivedAmt = fwd.getCurrWaiverAmount();
				BigDecimal curActualwaivedAmt = fwd.getCurrActualWaiver();
				BigDecimal amountWaived = BigDecimal.ZERO;

				TaxAmountSplit taxSplit = null;
				TaxHeader taxHeader = null;

				for (FinODDetails pdPenality : odPenalityList) {

					BigDecimal penalWaived = BigDecimal.ZERO;

					if (pdPenality.getTotPenaltyBal().compareTo(BigDecimal.ZERO) == 0) {
						continue;
					}

					if (curActualwaivedAmt.compareTo(BigDecimal.ZERO) == 0) {
						break;
					}

					if (fwd.isTaxApplicable()) {
						if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(fwd.getTaxComponent())) {
							if (pdPenality.getTotPenaltyBal().compareTo(curActualwaivedAmt) >= 0) {
								pdPenality.setTotWaived(pdPenality.getTotWaived().add(curActualwaivedAmt));
								pdPenality.setTotPenaltyBal(pdPenality.getTotPenaltyBal().subtract(curActualwaivedAmt));
								penalWaived = curActualwaivedAmt;
								amountWaived = curActualwaivedAmt;
								curActualwaivedAmt = BigDecimal.ZERO;
							} else {
								pdPenality.setTotWaived(pdPenality.getTotWaived().add(pdPenality.getTotPenaltyBal()));
								penalWaived = pdPenality.getTotPenaltyBal();
								amountWaived = curActualwaivedAmt;
								curActualwaivedAmt = curActualwaivedAmt.subtract(pdPenality.getTotPenaltyBal());
								pdPenality.setTotPenaltyBal(BigDecimal.ZERO);
							}

							taxSplit = GSTCalculator.getExclusiveGST(amountWaived, gstPercentages);
						} else {
							if (pdPenality.getTotPenaltyBal().compareTo(curwaivedAmt) >= 0) {
								pdPenality.setTotWaived(pdPenality.getTotWaived().add(curwaivedAmt));
								pdPenality.setTotPenaltyBal(pdPenality.getTotPenaltyBal().subtract(curwaivedAmt));
								penalWaived = curActualwaivedAmt;
								amountWaived = curwaivedAmt;
								curwaivedAmt = BigDecimal.ZERO;
							} else {
								pdPenality.setTotWaived(pdPenality.getTotWaived().add(pdPenality.getTotPenaltyBal()));
								curwaivedAmt = curwaivedAmt.subtract(pdPenality.getTotPenaltyBal());
								penalWaived = pdPenality.getTotPenaltyBal();
								amountWaived = curwaivedAmt;
								pdPenality.setTotPenaltyBal(BigDecimal.ZERO);
							}

							taxSplit = GSTCalculator.getInclusiveGST(amountWaived, gstPercentages);
						}

						// Taxes Splitting
						taxHeader = taxSplitting(gstPercentages, taxSplit);

					} else {

						if (pdPenality.getTotPenaltyBal().compareTo(curwaivedAmt) >= 0) {
							pdPenality.setTotWaived(pdPenality.getTotWaived().add(curwaivedAmt));
							pdPenality.setTotPenaltyBal(pdPenality.getTotPenaltyBal().subtract(curwaivedAmt));
							penalWaived = curwaivedAmt;
							amountWaived = curwaivedAmt;
							curwaivedAmt = BigDecimal.ZERO;
						} else {
							pdPenality.setTotWaived(pdPenality.getTotWaived().add(pdPenality.getTotPenaltyBal()));
							curwaivedAmt = curwaivedAmt.subtract(pdPenality.getTotPenaltyBal());
							penalWaived = pdPenality.getTotPenaltyBal();
							amountWaived = curwaivedAmt;
							pdPenality.setTotPenaltyBal(BigDecimal.ZERO);
						}
					}

					BigDecimal prvMnthPenaltyAmt = BigDecimal.ZERO;
					List<FinOverDueChargeMovement> dueMovements = new ArrayList<>();
					BigDecimal currWaivedAmt = BigDecimal.ZERO;
					BigDecimal waivedAmt = amountWaived;

					List<FinOverDueCharges> odcAmounts = finODCAmountDAO.getFinODCAmtByFinRef(finID,
							pdPenality.getFinODSchdDate(), RepayConstants.FEE_TYPE_LPP);

					boolean createOdc = true;
					Date lpiDueTillDate = pdPenality.getLppDueTillDate();
					for (FinOverDueCharges finODCAmount : odcAmounts) {
						BigDecimal balanceAmt = finODCAmount.getBalanceAmt();
						if (postDate.compareTo(finODCAmount.getValueDate()) == 0) {
							createOdc = false;
						}
						if (balanceAmt.compareTo(BigDecimal.ZERO) <= 0 || waivedAmt.compareTo(BigDecimal.ZERO) <= 0) {
							continue;
						}
						prvMnthPenaltyAmt = prvMnthPenaltyAmt.add(finODCAmount.getAmount());
						// Waived Amount Update
						if (waivedAmt.compareTo(balanceAmt) >= 0) {
							finODCAmount.setWaivedAmount(finODCAmount.getWaivedAmount().add(balanceAmt));
							currWaivedAmt = balanceAmt;
							waivedAmt = waivedAmt.subtract(balanceAmt);
							balanceAmt = BigDecimal.ZERO;
						} else {
							finODCAmount.setWaivedAmount(finODCAmount.getWaivedAmount().add(waivedAmt));
							currWaivedAmt = waivedAmt;
							balanceAmt = balanceAmt.subtract(waivedAmt);
							waivedAmt = BigDecimal.ZERO;
						}
						finODCAmount.setBalanceAmt(balanceAmt);
						pdPenality.setLppDueTillDate(lpiDueTillDate);
						FinOverDueChargeMovement movement = new FinOverDueChargeMovement();
						movement.setMovementDate(appDate);
						movement.setChargeId(finODCAmount.getId());
						movement.setMovementAmount(currWaivedAmt);
						movement.setWaivedAmount(currWaivedAmt);
						movement.setWaiverID(fwd.getWaiverId());
						dueMovements.add(movement);
					}
					if (createOdc) {
						if (waivedAmt.compareTo(BigDecimal.ZERO) > 0) {
							FinOverDueCharges finod = new FinOverDueCharges();
							BigDecimal penaltyAmt = pdPenality.getTotPenaltyAmt()
									.subtract(prvMnthPenaltyAmt.add(waivedAmt));
							finod.setFinID(pdPenality.getFinID());
							finod.setSchDate(pdPenality.getFinODSchdDate());
							finod.setPostDate(appDate);
							finod.setValueDate(postDate);
							finod.setAmount(pdPenality.getTotPenaltyAmt().subtract(prvMnthPenaltyAmt));
							finod.setPaidAmount(BigDecimal.ZERO);
							finod.setWaivedAmount(waivedAmt);
							finod.setNewRecord(true);
							finod.setBalanceAmt(penaltyAmt);
							finod.setOdPri(pdPenality.getFinCurODPri());
							finod.setOdPft(pdPenality.getFinCurODPft());
							finod.setFinOdTillDate(postDate);
							finod.setDueDays(DateUtil.getDaysBetween(pdPenality.getFinODSchdDate(), postDate));
							finod.setChargeType(RepayConstants.FEE_TYPE_LPP);
							pdPenality.setLpiDueTillDate(fwh.getValueDate());
							pdPenality.setLpiDueAmt(
									pdPenality.getLpiDueAmt().add(pdPenality.getLPIAmt().subtract(prvMnthPenaltyAmt)));

							long referenceID = finODCAmountDAO.saveFinODCAmt(finod);
							FinOverDueChargeMovement movement = new FinOverDueChargeMovement();
							movement.setMovementDate(appDate);
							movement.setChargeId(referenceID);
							movement.setMovementAmount(finod.getWaivedAmount());
							movement.setPaidAmount(BigDecimal.ZERO);
							movement.setWaivedAmount(finod.getWaivedAmount());
							movement.setReceiptID(fwh.getWaiverId());
							dueMovements.add(movement);
						}
					}

					finODCAmountDAO.updateFinODCBalAmts(odcAmounts);
					finODCAmountDAO.saveMovement(dueMovements);

					finODDetailsDAO.updatePenaltyTotals(pdPenality);

					if (penalWaived.compareTo(BigDecimal.ZERO) > 0) {
						FinanceRepayments repayment = new FinanceRepayments();
						repayment.setFinID(pdPenality.getFinID());
						repayment.setFinReference(pdPenality.getFinReference());
						repayment.setFinPostDate(appDate);
						repayment.setFinRpyFor(pdPenality.getFinODFor());
						repayment.setFinRpyAmount(penalWaived);
						repayment.setFinSchdDate(pdPenality.getFinODSchdDate());
						repayment.setFinValueDate(appDate);
						repayment.setFinBranch(pdPenality.getFinBranch());
						repayment.setFinType(pdPenality.getFinType());
						repayment.setFinCustID(pdPenality.getCustID());
						repayment.setPenaltyWaived(penalWaived);
						repayment.setWaiverId(fwh.getWaiverId());
						rpyList.add(repayment);
					}

					// TODO update LPP related GST Table data
					if (fwd.getCurrWaiverAmount().compareTo(BigDecimal.ZERO) > 0) {
						if (amountWaived.compareTo(BigDecimal.ZERO) > 0) {
							ManualAdviseMovements movement = new ManualAdviseMovements();
							movement.setMovementDate(appDate);
							movement.setMovementAmount(fwd.getCurrWaiverAmount());
							movement.setSchDate(pdPenality.getFinODSchdDate());
							movement.setPaidAmount(BigDecimal.ZERO);
							movement.setWaivedAmount(amountWaived);
							movement.setReceiptID(0);
							movement.setReceiptSeqID(0);
							movement.setWaiverID(fwd.getWaiverId());
							movement.setFeeTypeCode(fwd.getFeeTypeCode());
							movement.setFeeTypeDesc(fwd.getFeeTypeDesc());
							movement.setTaxApplicable(fwd.isTaxApplicable());
							movement.setTaxComponent(fwd.getTaxComponent());

							if (taxHeader != null) {
								movement.setTaxHeaderId(taxHeader.getHeaderId());
								movement.setTaxHeader(taxHeader);
							}
							movements.add(movement);
						}
					}

				}
			}

			// update late pay profit waived amounts to the Finoddetails table.
			if (Allocation.LPFT.equals(StringUtils.trimToEmpty(fwd.getFeeTypeCode()))) {
				BigDecimal curwaivedAmt = fwd.getCurrWaiverAmount();
				for (FinODDetails oddetail : finodPftdetails) {
					if (oddetail.getLPIBal().compareTo(curwaivedAmt) >= 0) {
						oddetail.setLPIWaived(curwaivedAmt);
						curwaivedAmt = BigDecimal.ZERO;
					} else {
						oddetail.setLPIWaived(oddetail.getLPIWaived().add(oddetail.getLPIBal()));
						curwaivedAmt = curwaivedAmt.subtract(oddetail.getLPIBal());
						oddetail.setLPIBal(BigDecimal.ZERO);
					}

					saveLPIWaiver(fwh, appDate, finID, postDate, fwd, curwaivedAmt, oddetail);

					finODDetailsDAO.updateLatePftTotals(oddetail.getFinID(), oddetail.getFinODSchdDate(),
							BigDecimal.ZERO, oddetail.getLPIWaived());
				}
			}
		}

		fwh.setRpyList(rpyList);

		logger.debug(Literal.LEAVING);
		return movements;
	}

	private void saveLPIWaiver(FeeWaiverHeader fwh, Date appDate, long finID, Date postDate, FeeWaiverDetail fwd,
			BigDecimal curwaivedAmt, FinODDetails oddetail) {
		BigDecimal prvMnthLPIAmt = BigDecimal.ZERO;
		List<FinOverDueCharges> lpiAmtList = finODCAmountDAO.getFinODCAmtByFinRef(finID, oddetail.getFinODSchdDate(),
				RepayConstants.FEE_TYPE_LPI);

		List<FinOverDueChargeMovement> lpiMovementList = new ArrayList<>();
		BigDecimal currWaivedAmt = BigDecimal.ZERO;
		boolean createOdc = true;
		Date lpiDueTillDate = oddetail.getLpiDueTillDate();
		for (FinOverDueCharges finLPIAmt : lpiAmtList) {
			BigDecimal lpiBalanceAmt = finLPIAmt.getBalanceAmt();
			if (postDate.compareTo(finLPIAmt.getValueDate()) == 0) {
				createOdc = false;
			}
			if (lpiBalanceAmt.compareTo(BigDecimal.ZERO) <= 0 || curwaivedAmt.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}
			prvMnthLPIAmt = prvMnthLPIAmt.add(finLPIAmt.getAmount());
			// Waived Amount Update
			if (curwaivedAmt.compareTo(lpiBalanceAmt) >= 0) {
				finLPIAmt.setWaivedAmount(finLPIAmt.getWaivedAmount().add(lpiBalanceAmt));
				currWaivedAmt = lpiBalanceAmt;
				curwaivedAmt = curwaivedAmt.subtract(lpiBalanceAmt);
				lpiBalanceAmt = BigDecimal.ZERO;
			} else {
				finLPIAmt.setWaivedAmount(finLPIAmt.getWaivedAmount().add(curwaivedAmt));
				currWaivedAmt = curwaivedAmt;
				lpiBalanceAmt = lpiBalanceAmt.subtract(curwaivedAmt);
				curwaivedAmt = BigDecimal.ZERO;
			}
			finLPIAmt.setBalanceAmt(lpiBalanceAmt);
			oddetail.setLpiDueTillDate(lpiDueTillDate);
			FinOverDueChargeMovement movement = new FinOverDueChargeMovement();
			movement.setMovementDate(appDate);
			movement.setChargeId(finLPIAmt.getId());
			movement.setMovementAmount(currWaivedAmt);
			movement.setWaivedAmount(currWaivedAmt);
			movement.setWaiverID(fwd.getWaiverId());
			lpiMovementList.add(movement);
		}
		if (createOdc) {
			if (curwaivedAmt.compareTo(BigDecimal.ZERO) > 0) {
				FinOverDueCharges finLPIAmt = new FinOverDueCharges();
				BigDecimal lpiAmt = oddetail.getLPIAmt().subtract(prvMnthLPIAmt.add(curwaivedAmt));
				finLPIAmt.setFinID(oddetail.getFinID());
				finLPIAmt.setSchDate(oddetail.getFinODSchdDate());
				finLPIAmt.setPostDate(appDate);
				finLPIAmt.setValueDate(postDate);
				finLPIAmt.setAmount(oddetail.getTotPenaltyAmt().subtract(prvMnthLPIAmt));
				finLPIAmt.setPaidAmount(BigDecimal.ZERO);
				finLPIAmt.setWaivedAmount(curwaivedAmt);
				finLPIAmt.setNewRecord(true);
				finLPIAmt.setBalanceAmt(lpiAmt);
				finLPIAmt.setOdPri(oddetail.getFinCurODPri());
				finLPIAmt.setOdPft(oddetail.getFinCurODPft());
				finLPIAmt.setFinOdTillDate(postDate);
				finLPIAmt.setDueDays(DateUtil.getDaysBetween(oddetail.getFinODSchdDate(), postDate));
				finLPIAmt.setChargeType(RepayConstants.FEE_TYPE_LPI);

				oddetail.setLpiDueTillDate(fwh.getValueDate());
				oddetail.setLpiDueAmt(oddetail.getLpiDueAmt().add(oddetail.getLPIAmt().subtract(prvMnthLPIAmt)));

				long referenceID = finODCAmountDAO.saveFinODCAmt(finLPIAmt);
				FinOverDueChargeMovement movement = new FinOverDueChargeMovement();
				movement.setMovementDate(appDate);
				movement.setChargeId(referenceID);
				movement.setMovementAmount(finLPIAmt.getWaivedAmount());
				movement.setPaidAmount(BigDecimal.ZERO);
				movement.setWaivedAmount(finLPIAmt.getWaivedAmount());
				movement.setReceiptID(fwh.getWaiverId());
				lpiMovementList.add(movement);
			}
		}

		finODCAmountDAO.updateFinODCAmts(lpiAmtList);
		finODCAmountDAO.saveMovement(lpiMovementList);
	}

	/**
	 * Updating the finSchdule details and insert into FINREPAYDETAILS, FinRepaySchdetails tables.
	 */
	private FinanceDetail allocateWaiverToSchduleDetails(FeeWaiverHeader fwh, FinanceDetail fd, AEEvent aeEvent,
			boolean isAcctRequired) {
		logger.debug(Literal.ENTERING);

		List<FeeWaiverDetail> fwDetails = fwh.getFeeWaiverDetails();
		FeeWaiverDetail feeWaiverDetail = null;

		if (CollectionUtils.isEmpty(fwDetails)) {
			return fd;
		}

		for (FeeWaiverDetail fw : fwDetails) {
			if (Allocation.PFT.equals(StringUtils.trimToEmpty(fw.getFeeTypeCode()))
					&& fw.getCurrWaiverAmount().compareTo(BigDecimal.ZERO) > 0) {
				feeWaiverDetail = fw;
				break;
			}
		}

		if (feeWaiverDetail == null) {
			return fd;
		}

		long finID = fwh.getFinID();
		String finReference = fwh.getFinReference();

		List<FinanceScheduleDetail> schedules = this.financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false);

		if (CollectionUtils.isEmpty(schedules)) {
			return fd;
		}

		Date appDate = SysParamUtil.getAppDate();

		BigDecimal curwaivedAmt = feeWaiverDetail.getCurrWaiverAmount();

		List<FinanceRepayments> finRepaymentList = new ArrayList<>();
		List<RepayScheduleDetail> rsdList = new ArrayList<>();
		BigDecimal totPftBal = BigDecimal.ZERO;

		// TDS Parameters
		String tdsRoundMode = SysParamUtil.getValueAsString(CalculationConstants.TDS_ROUNDINGMODE);
		int tdsRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);
		BigDecimal tdsPerc = new BigDecimal(SysParamUtil.getValueAsString(CalculationConstants.TDS_PERCENTAGE));
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain financeMain = schdData.getFinanceMain();

		List<ScheduleDueTaxDetail> dueTaxList = new ArrayList<>();

		for (FinanceScheduleDetail schd : schedules) {
			if (schd.getSchDate().compareTo(appDate) > 0) {
				break;
			}

			if (curwaivedAmt.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}

			if (schd.getProfitSchd().compareTo(schd.getSchdPftPaid()) <= 0) {
				continue;
			}

			BigDecimal profitBal = schd.getProfitSchd().subtract(schd.getSchdPftPaid());
			ScheduleDueTaxDetail sdtd = new ScheduleDueTaxDetail();
			sdtd.setFinReference(finReference);
			sdtd.setSchDate(schd.getSchDate());

			// Full adjustment
			if (profitBal.compareTo(curwaivedAmt) <= 0) {
				schd.setSchdPftWaiver(schd.getSchdPftWaiver().add(profitBal));
				schd.setSchdPftPaid(schd.getSchdPftPaid().add(profitBal));
				schd.setSchPftPaid(true);

				BigDecimal tds = profitBal.multiply(tdsPerc);
				tds = tds.divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_UP);
				tds = CalculationUtil.roundAmount(tds, tdsRoundMode, tdsRoundingTarget);

				schd.setTDSPaid(schd.getTDSPaid().add(tds));
				curwaivedAmt = curwaivedAmt.subtract(profitBal);
				finRepaymentList.add(prepareRepayments(profitBal, tds, schd, financeMain, fwh));
				rsdList.add(prepareRepaySchDetails(profitBal, schd, financeMain, fwh, tds));
				totPftBal = totPftBal.add(profitBal);
				// Partial Adjustment
			} else if (profitBal.compareTo(curwaivedAmt) > 0) {

				BigDecimal tds = curwaivedAmt.multiply(tdsPerc);
				tds = tds.divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_UP);
				tds = CalculationUtil.roundAmount(tds, tdsRoundMode, tdsRoundingTarget);

				schd.setTDSPaid(schd.getTDSPaid().add(tds));
				schd.setSchdPftWaiver(schd.getSchdPftWaiver().add(curwaivedAmt));
				schd.setSchdPftPaid(schd.getSchdPftPaid().add(curwaivedAmt));

				finRepaymentList.add(prepareRepayments(curwaivedAmt, tds, schd, financeMain, fwh));
				rsdList.add(prepareRepaySchDetails(curwaivedAmt, schd, financeMain, fwh, tds));
				totPftBal = totPftBal.add(curwaivedAmt);
				curwaivedAmt = BigDecimal.ZERO;
			}

			dueTaxList.add(sdtd);
		}
		// Executing accounting
		if (isAcctRequired) {
			aeEvent = executeAcctProcessing(totPftBal, financeMain, fwh, aeEvent, null);
		}
		long linkedTranId = aeEvent.getLinkedTranId();

		// Schedule details
		this.financeScheduleDetailDAO.deleteByFinReference(finID, TableType.MAIN_TAB.getSuffix(), false, 0);
		this.financeScheduleDetailDAO.saveList(schedules, TableType.MAIN_TAB.getSuffix(), false);

		// Repay Schedule details
		for (RepayScheduleDetail rsd : rsdList) {
			rsd.setLinkedTranId(linkedTranId);
		}
		this.financeRepaymentsDAO.saveRpySchdList(rsdList, TableType.MAIN_TAB);

		// Fin Repay Details
		for (FinanceRepayments repayments : finRepaymentList) {
			repayments.setLinkedTranId(linkedTranId);
			this.financeRepaymentsDAO.save(repayments, TableType.MAIN_TAB.getSuffix());
		}

		schedules = this.financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false);
		schdData.setFinanceScheduleDetails(schedules);

		List<FinODDetails> overdueList = this.receiptCalculator.getValueDatePenalties(schdData, BigDecimal.ZERO,
				appDate, null, true, schedules);

		if (CollectionUtils.isNotEmpty(overdueList)) {
			finODDetailsDAO.updateList(overdueList);
		}

		// Profit Waiver GST Invoice Preparation
		if (ImplementationConstants.ALW_PROFIT_SCHD_INVOICE && linkedTranId > 0) {
			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setLinkedTranId(linkedTranId);
			invoiceDetail.setFinanceDetail(fd);

			invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_EXEMPTED_TAX_CREDIT);
			invoiceDetail.setWaiver(false);

			for (ScheduleDueTaxDetail sdtd : dueTaxList) {
				Long dbInvoiceID = financeScheduleDetailDAO.getSchdDueInvoiceID(sdtd.getFinID(), sdtd.getSchDate());
				invoiceDetail.setPftAmount(sdtd.getAmount());
				invoiceDetail.setPriAmount(BigDecimal.ZERO);
				invoiceDetail.setDbInvoiceID(dbInvoiceID);
				gstInvoiceTxnService.schdDueTaxInovicePrepration(invoiceDetail);
			}
		}
		fwh.setLinkedTranId(aeEvent.getLinkedTranId());

		logger.debug(Literal.LEAVING);
		return fd;
	}

	/**
	 * Updating the finSchdule details and insert into FINREPAYDETAILS, FinRepaySchdetails tables.
	 */
	private BigDecimal getTotPftBal(FeeWaiverHeader fwh, FinanceDetail financeDetail, AEEvent aeEvent) {
		logger.debug(Literal.ENTERING);

		List<FeeWaiverDetail> fwdList = fwh.getFeeWaiverDetails();
		FeeWaiverDetail feeWaiverDetail = null;

		if (CollectionUtils.isEmpty(fwdList)) {
			return BigDecimal.ZERO;
		}

		for (FeeWaiverDetail fwd : fwdList) {
			if (Allocation.PFT.equals(StringUtils.trimToEmpty(fwd.getFeeTypeCode()))
					&& fwd.getCurrWaiverAmount().compareTo(BigDecimal.ZERO) > 0) {
				feeWaiverDetail = fwd;
				break;
			}
		}

		if (feeWaiverDetail == null) {
			return BigDecimal.ZERO;
		}

		long finID = fwh.getFinID();

		List<FinanceScheduleDetail> schedules = this.financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false);

		if (CollectionUtils.isEmpty(schedules)) {
			return BigDecimal.ZERO;
		}

		Date appDate = SysParamUtil.getAppDate();

		BigDecimal curwaivedAmt = feeWaiverDetail.getCurrWaiverAmount();

		List<FinanceRepayments> finRepaymentList = new ArrayList<>();
		List<RepayScheduleDetail> repaySchDetList = new ArrayList<>();
		BigDecimal totPftBal = BigDecimal.ZERO;

		// TDS Parameters
		String tdsRoundMode = SysParamUtil.getValueAsString(CalculationConstants.TDS_ROUNDINGMODE);
		int tdsRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);
		BigDecimal tdsPerc = new BigDecimal(SysParamUtil.getValueAsString(CalculationConstants.TDS_PERCENTAGE));
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		for (FinanceScheduleDetail schDetail : schedules) {

			if (schDetail.getSchDate().compareTo(appDate) > 0) {
				break;
			}

			if (curwaivedAmt.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}

			if (schDetail.getProfitSchd().compareTo(schDetail.getSchdPftPaid()) <= 0) {
				continue;
			}

			BigDecimal profitBal = schDetail.getProfitSchd().subtract(schDetail.getSchdPftPaid());

			// Full adjustment
			if (profitBal.compareTo(curwaivedAmt) <= 0) {
				schDetail.setSchdPftWaiver(schDetail.getSchdPftWaiver().add(profitBal));
				schDetail.setSchdPftPaid(schDetail.getSchdPftPaid().add(profitBal));
				schDetail.setSchPftPaid(true);

				BigDecimal tds = curwaivedAmt.multiply(tdsPerc);
				tds = tds.divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_UP);
				tds = CalculationUtil.roundAmount(tds, tdsRoundMode, tdsRoundingTarget);

				schDetail.setTDSPaid(schDetail.getTDSPaid().add(tds));
				curwaivedAmt = curwaivedAmt.subtract(profitBal);
				finRepaymentList.add(prepareRepayments(profitBal, tds, schDetail, financeMain, fwh));
				repaySchDetList.add(prepareRepaySchDetails(profitBal, schDetail, financeMain, fwh, tds));
				totPftBal = totPftBal.add(profitBal);
				// Partial Adjustment
			} else if (profitBal.compareTo(curwaivedAmt) > 0) {

				BigDecimal tds = curwaivedAmt.multiply(tdsPerc);
				tds = tds.divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_UP);
				tds = CalculationUtil.roundAmount(tds, tdsRoundMode, tdsRoundingTarget);

				schDetail.setTDSPaid(schDetail.getTDSPaid().add(tds));
				schDetail.setSchdPftWaiver(schDetail.getSchdPftWaiver().add(curwaivedAmt));
				schDetail.setSchdPftPaid(schDetail.getSchdPftPaid().add(curwaivedAmt));

				finRepaymentList.add(prepareRepayments(curwaivedAmt, tds, schDetail, financeMain, fwh));
				repaySchDetList.add(prepareRepaySchDetails(curwaivedAmt, schDetail, financeMain, fwh, tds));
				totPftBal = totPftBal.add(curwaivedAmt);
				curwaivedAmt = BigDecimal.ZERO;
			}
		}
		logger.debug(Literal.LEAVING);

		return totPftBal;
	}

	private AEEvent executeAcctProcessing(BigDecimal totPftBal, FinanceMain fm, FeeWaiverHeader fwh, AEEvent aeEvent,
			List<ManualAdviseMovements> movements) {
		logger.debug(Literal.ENTERING);

		if (aeEvent == null) {
			aeEvent = prepareAEEvent(fm, fwh);
		}

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
		}
		amountCodes.setFinType(fm.getFinType());

		Map<String, Object> detailsMap = amountCodes.getDeclaredFieldValues();
		detailsMap.put("ae_pftWaiver", totPftBal);
		aeEvent.setDataMap(detailsMap);

		BigDecimal totBounce = BigDecimal.ZERO;
		BigDecimal bounceCGST = BigDecimal.ZERO;
		BigDecimal bounceSGST = BigDecimal.ZERO;
		BigDecimal bounceIGST = BigDecimal.ZERO;
		BigDecimal bounceUGST = BigDecimal.ZERO;
		BigDecimal bounceCESS = BigDecimal.ZERO;

		BigDecimal totLPP = BigDecimal.ZERO;
		BigDecimal lppCGST = BigDecimal.ZERO;
		BigDecimal lppSGST = BigDecimal.ZERO;
		BigDecimal lppIGST = BigDecimal.ZERO;
		BigDecimal lppUGST = BigDecimal.ZERO;
		BigDecimal lppCESS = BigDecimal.ZERO;

		BigDecimal bounceWithGst = BigDecimal.ZERO;

		if (CollectionUtils.isNotEmpty(movements)) {

			for (ManualAdviseMovements movement : movements) {

				TaxHeader taxHeader = movement.getTaxHeader();
				if (taxHeader != null) {
					Taxes cgstTax = new Taxes();
					Taxes sgstTax = new Taxes();
					Taxes igstTax = new Taxes();
					Taxes ugstTax = new Taxes();
					Taxes cessTax = new Taxes();
					List<Taxes> taxDetails = taxHeader.getTaxDetails();
					if (taxHeader != null && CollectionUtils.isNotEmpty(taxDetails)) {
						for (Taxes taxes : taxDetails) {
							if (StringUtils.equals(RuleConstants.CODE_CGST, taxes.getTaxType())) {
								cgstTax = taxes;
							} else if (StringUtils.equals(RuleConstants.CODE_SGST, taxes.getTaxType())) {
								sgstTax = taxes;
							} else if (StringUtils.equals(RuleConstants.CODE_IGST, taxes.getTaxType())) {
								igstTax = taxes;
							} else if (StringUtils.equals(RuleConstants.CODE_UGST, taxes.getTaxType())) {
								ugstTax = taxes;
							} else if (StringUtils.equals(RuleConstants.CODE_CESS, taxes.getTaxType())) {
								cessTax = taxes;
							}
						}
					}

					if (Allocation.BOUNCE.equals(movement.getFeeTypeCode())) {
						totBounce = totBounce.add(movement.getWaivedAmount());
						bounceCGST = bounceCGST.add(cgstTax.getWaivedTax());
						bounceSGST = bounceSGST.add(sgstTax.getWaivedTax());
						bounceIGST = bounceIGST.add(igstTax.getWaivedTax());
						bounceUGST = bounceUGST.add(ugstTax.getWaivedTax());
						bounceCESS = bounceCESS.add(cessTax.getWaivedTax());
						bounceWithGst = totBounce.add(bounceCGST).add(bounceSGST).add(bounceIGST).add(bounceUGST)
								.add(bounceCESS);
					} else if (Allocation.ODC.equals(movement.getFeeTypeCode())) {
						totLPP = totLPP.add(movement.getMovementAmount());
						lppCGST = lppCGST.add(cgstTax.getWaivedTax());
						lppSGST = lppSGST.add(sgstTax.getWaivedTax());
						lppIGST = lppIGST.add(igstTax.getWaivedTax());
						lppUGST = lppUGST.add(ugstTax.getWaivedTax());
						lppCESS = lppCESS.add(cessTax.getWaivedTax());
					} else {
						if (StringUtils.isNotEmpty(movement.getFeeTypeCode())) {
							FeeType feeType = this.feeTypeDAO.getApprovedFeeTypeByFeeCode(movement.getFeeTypeCode());
							if (feeType != null) {

								String feeTypeCode = feeType.getFeeTypeCode();
								detailsMap.put(feeTypeCode + "_W", movement.getWaivedAmount());

								// Waiver GST Amounts (GST Waiver Changes)
								detailsMap.put(feeTypeCode + "_CGST_W", cgstTax.getWaivedTax());
								detailsMap.put(feeTypeCode + "_SGST_W", sgstTax.getWaivedTax());
								detailsMap.put(feeTypeCode + "_IGST_W", igstTax.getWaivedTax());
								detailsMap.put(feeTypeCode + "_UGST_W", ugstTax.getWaivedTax());
								detailsMap.put(feeTypeCode + "_CESS_W", cessTax.getWaivedTax());
							}
						}
					}
				} else {
					if (Allocation.BOUNCE.equals(movement.getFeeTypeCode())) {
						bounceWithGst = totBounce.add(movement.getWaivedAmount());
					} else if (Allocation.ODC.equals(movement.getFeeTypeCode())) {
						totLPP = totLPP.add(movement.getMovementAmount());
					} else {
						if (StringUtils.isNotEmpty(movement.getFeeTypeCode())) {
							FeeType feeType = this.feeTypeDAO.getApprovedFeeTypeByFeeCode(movement.getFeeTypeCode());
							if (feeType != null) {
								String feeTypeCode = feeType.getFeeTypeCode();
								detailsMap.put(feeTypeCode + "_W", movement.getWaivedAmount());
							}
						}
					}

				}
			}

		}

		detailsMap.put("bounceCharge_CGST_W", bounceCGST);
		detailsMap.put("bounceCharge_SGST_W", bounceSGST);
		detailsMap.put("bounceCharge_IGST_W", bounceIGST);
		detailsMap.put("bounceCharge_UGST_W", bounceUGST);
		detailsMap.put("bounceCharge_CESS_W", bounceCESS);
		detailsMap.put("bounceChargeWaived", bounceWithGst);
		// TODO add Cess

		detailsMap.put("LPP_CGST_W", lppCGST);
		detailsMap.put("LPP_SGST_W", lppSGST);
		detailsMap.put("LPP_IGST_W", lppIGST);
		detailsMap.put("LPP_UGST_W", lppUGST);
		detailsMap.put("LPP_CESS_W", lppCESS);
		detailsMap.put("ae_penaltyWaived", totLPP);

		aeEvent = this.postingsPreparationUtil.postAccounting(aeEvent);

		fwh.setLinkedTranId(aeEvent.getLinkedTranId());

		logger.debug(Literal.LEAVING);
		return aeEvent;
	}

	private AEEvent prepareAEEvent(FinanceMain fm, FeeWaiverHeader feeWaiverHeader) {
		AEEvent aeEvent;
		aeEvent = new AEEvent();
		aeEvent.setPostingUserBranch(feeWaiverHeader.getUserDetails().getBranchCode());
		aeEvent.setEntityCode(fm.getEntityCode());
		aeEvent.setAccountingEvent(AccountingEvent.WAIVER);
		aeEvent.setFinID(fm.getFinID());
		aeEvent.setFinReference(fm.getFinReference());
		aeEvent.setValueDate(SysParamUtil.getAppDate());
		aeEvent.setBranch(fm.getFinBranch());
		aeEvent.setCcy(fm.getFinCcy());
		aeEvent.setCustID(fm.getCustID());
		aeEvent.setPostRefId(feeWaiverHeader.getWaiverId());
		aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(fm.getFinType(), AccountingEvent.WAIVER,
				FinanceConstants.MODULEID_FINTYPE));

		return aeEvent;
	}

	private FinanceRepayments prepareRepayments(BigDecimal profitBal, BigDecimal tds, FinanceScheduleDetail schDetail,
			FinanceMain fm, FeeWaiverHeader waiverHeader) {
		FinanceRepayments repayment = new FinanceRepayments();

		repayment.setWaiverId(waiverHeader.getWaiverId());
		repayment.setFinID(schDetail.getFinID());
		repayment.setFinReference(schDetail.getFinReference());
		repayment.setFinSchdDate(schDetail.getSchDate());
		repayment.setFinRpyFor(FinanceConstants.SCH_TYPE_SCHEDULE);
		repayment.setLinkedTranId(0);
		repayment.setReceiptId(0);
		repayment.setFinPostDate(SysParamUtil.getAppDate());
		repayment.setFinValueDate(SysParamUtil.getAppValueDate());
		repayment.setFinBranch(fm.getFinBranch());
		repayment.setFinType(fm.getFinType());
		repayment.setFinCustID(fm.getCustID());

		repayment.setFinSchdPriPaid(BigDecimal.ZERO);
		repayment.setFinSchdPftPaid(profitBal);
		repayment.setFinTotSchdPaid(profitBal);

		// Fee Details
		repayment.setFinFee(BigDecimal.ZERO);
		repayment.setFinWaiver(BigDecimal.ZERO);
		repayment.setFinRefund(BigDecimal.ZERO);
		repayment.setSchdFeePaid(BigDecimal.ZERO);

		repayment.setFinRpyAmount(profitBal);
		repayment.setFinSchdTdsPaid(tds);

		return repayment;
	}

	private RepayScheduleDetail prepareRepaySchDetails(BigDecimal profitBal, FinanceScheduleDetail schedule,
			FinanceMain fm, FeeWaiverHeader fwh, BigDecimal tdsSchdPayNow) {

		Date appDate = SysParamUtil.getAppDate();

		FinRepayHeader rph = new FinRepayHeader();
		rph.setFinReference(fm.getFinReference());
		rph.setValueDate(appDate);
		rph.setFinEvent(AccountingEvent.WAIVER);

		long id = financeRepaymentsDAO.saveFinRepayHeader(rph, TableType.MAIN_TAB);

		RepayScheduleDetail rsd = new RepayScheduleDetail();

		rsd.setWaiverId(fwh.getWaiverId());
		rsd.setFinID(schedule.getFinID());
		rsd.setFinReference(schedule.getFinReference());
		rsd.setSchDate(schedule.getSchDate());
		rsd.setSchdFor(FinanceConstants.SCH_TYPE_SCHEDULE);
		rsd.setProfitSchdBal(schedule.getProfitSchd());
		rsd.setPrincipalSchdBal(schedule.getPrincipalSchd());
		rsd.setProfitSchdPayNow(profitBal);
		rsd.setPrincipalSchdPayNow(schedule.getSchdPriPaid());
		rsd.setTdsSchdPayNow(tdsSchdPayNow);

		int daysLate = DateUtil.getDaysBetween(schedule.getSchDate(), SysParamUtil.getAppValueDate());
		rsd.setDaysLate(daysLate);

		rsd.setRepayBalance(schedule.getProfitSchd().add(schedule.getPrincipalSchd()));
		rsd.setProfitSchd(schedule.getProfitSchd());
		rsd.setProfitSchdPaid(profitBal);

		rsd.setPrincipalSchd(schedule.getPrincipalSchd());
		rsd.setPrincipalSchdPaid(schedule.getSchdPriPaid());
		rsd.setPenaltyPayNow(BigDecimal.ZERO);

		rsd.setRepayID(id);// wAIVERiD
		rsd.setRepaySchID(1);
		rsd.setLinkedTranId(0);// pOSTiD
		return rsd;
	}

	private List<ManualAdviseMovements> allocateWaiverToBounceAndAdvise(FeeWaiverHeader fwh) {
		List<ManualAdviseMovements> movements = new ArrayList<>();

		for (FeeWaiverDetail fwd : fwh.getFeeWaiverDetails()) {
			String feeTypeCode = StringUtils.trimToEmpty(fwd.getFeeTypeCode());

			if (!Allocation.ODC.equals(feeTypeCode) && !Allocation.LPFT.equals(feeTypeCode)
					&& !Allocation.PFT.equals(feeTypeCode)
					&& fwd.getCurrWaiverAmount().compareTo(BigDecimal.ZERO) > 0) {

				BigDecimal curwaivedAmt = fwd.getCurrWaiverAmount();
				BigDecimal curActualwaivedAmt = fwd.getCurrActualWaiver();

				long adviseId = fwd.getAdviseId();
				for (ManualAdvise advise : manualAdviseList) {
					ManualAdviseMovements movement = null;
					if (advise.getBounceID() != 0 && adviseId == -3) {
						movement = prepareAdviseWaiver(fwd, advise);
					} else {
						if (advise.getAdviseID() == adviseId) {
							movement = prepareAdviseWaiver(fwd, advise);
						}
					}

					if (movement != null) {
						movements.add(movement);
					}
				}

				fwd.setCurrWaiverAmount(curwaivedAmt);
				fwd.setCurrActualWaiver(curActualwaivedAmt);
			}
		}

		return movements;
	}

	private ManualAdviseMovements prepareAdviseWaiver(FeeWaiverDetail waiverdetail, ManualAdvise advise) {
		logger.debug(Literal.ENTERING);

		BigDecimal curwaivedAmt = waiverdetail.getCurrWaiverAmount();
		BigDecimal curActualwaivedAmt = waiverdetail.getCurrActualWaiver();
		BigDecimal amountWaived = BigDecimal.ZERO;

		FinanceMain fm = new FinanceMain();
		fm.setFinID(waiverdetail.getFinID());
		Map<String, BigDecimal> gstPercentages = GSTCalculator.getTaxPercentages(fm);

		TaxAmountSplit taxSplit = null;
		TaxHeader taxHeader = null;

		if (waiverdetail.isTaxApplicable()) {
			if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(waiverdetail.getTaxComponent())) {
				if (advise.getBalanceAmt().compareTo(curActualwaivedAmt) >= 0) {
					advise.setWaivedAmount(advise.getWaivedAmount().add(curActualwaivedAmt));
					advise.setBalanceAmt(advise.getBalanceAmt().subtract(curActualwaivedAmt));
					amountWaived = curActualwaivedAmt;
					waiverdetail.setCurrActualWaiver(BigDecimal.ZERO);
					curActualwaivedAmt = BigDecimal.ZERO;

				} else {
					amountWaived = advise.getAdviseAmount().subtract(advise.getWaivedAmount());
					advise.setWaivedAmount(advise.getWaivedAmount().add(advise.getBalanceAmt()));
					curActualwaivedAmt = curActualwaivedAmt.subtract(advise.getBalanceAmt());
					waiverdetail.setCurrActualWaiver(curActualwaivedAmt);
					amountWaived = advise.getBalanceAmt();
					advise.setBalanceAmt(BigDecimal.ZERO);
					curActualwaivedAmt = BigDecimal.ZERO;
					taxSplit = getExclusiveTax(advise, gstPercentages);
				}

				if (amountWaived.compareTo(BigDecimal.ZERO) > 0) {
					taxSplit = GSTCalculator.getInclusiveGST(curwaivedAmt, gstPercentages);
					waiverdetail.setCurrWaiverAmount(amountWaived.add(taxSplit.gettGST()));
				}
			} else {
				if (advise.getBalanceAmt().compareTo(curwaivedAmt) >= 0) {
					advise.setWaivedAmount(advise.getWaivedAmount().add(curwaivedAmt));
					advise.setBalanceAmt(advise.getBalanceAmt().subtract(curwaivedAmt));
					amountWaived = curwaivedAmt;
					waiverdetail.setCurrWaiverAmount(BigDecimal.ZERO);
					curwaivedAmt = BigDecimal.ZERO;
				} else {
					amountWaived = advise.getAdviseAmount().subtract(advise.getWaivedAmount());
					advise.setWaivedAmount(advise.getWaivedAmount().add(advise.getBalanceAmt()));
					curwaivedAmt = curwaivedAmt.subtract(advise.getBalanceAmt());
					waiverdetail.setCurrWaiverAmount(curwaivedAmt);
					curwaivedAmt = BigDecimal.ZERO;
					amountWaived = advise.getBalanceAmt();
					advise.setBalanceAmt(BigDecimal.ZERO);
				}

				taxSplit = GSTCalculator.getInclusiveGST(amountWaived, gstPercentages);
				waiverdetail.setCurrWaiverAmount(amountWaived);
			}

			// Taxes Splitting
			if (taxSplit != null) {
				taxHeader = taxSplitting(gstPercentages, taxSplit, advise);
			}

		} else {

			if (advise.getBalanceAmt().compareTo(curwaivedAmt) >= 0) {
				advise.setWaivedAmount(advise.getWaivedAmount().add(curwaivedAmt));
				advise.setBalanceAmt(advise.getBalanceAmt().subtract(curwaivedAmt));
				amountWaived = curwaivedAmt;
				waiverdetail.setCurrWaiverAmount(BigDecimal.ZERO);
				curwaivedAmt = BigDecimal.ZERO;
			} else {
				amountWaived = advise.getAdviseAmount().subtract(advise.getWaivedAmount());
				advise.setWaivedAmount(advise.getWaivedAmount().add(advise.getBalanceAmt()));
				curwaivedAmt = curwaivedAmt.subtract(advise.getBalanceAmt());
				waiverdetail.setCurrWaiverAmount(curwaivedAmt);
				curwaivedAmt = BigDecimal.ZERO;
				advise.setBalanceAmt(BigDecimal.ZERO);
			}
			waiverdetail.setCurrWaiverAmount(waiverdetail.getCurrWaiverAmount().subtract(amountWaived));
		}

		advise.setVersion(advise.getVersion() + 1);
		manualAdviseDAO.update(advise, TableType.MAIN_TAB);

		ManualAdviseMovements movement = new ManualAdviseMovements();
		if (amountWaived.compareTo(BigDecimal.ZERO) > 0) {
			movement.setAdviseID(advise.getAdviseID());
			movement.setMovementDate(SysParamUtil.getAppDate());
			movement.setMovementAmount(waiverdetail.getCurrWaiverAmount());
			movement.setPaidAmount(BigDecimal.ZERO);
			movement.setWaivedAmount(amountWaived);
			movement.setReceiptID(0);
			movement.setReceiptSeqID(0);
			movement.setWaiverID(waiverdetail.getWaiverId());
			movement.setFeeTypeCode(waiverdetail.getFeeTypeCode());
			movement.setFeeTypeDesc(waiverdetail.getFeeTypeDesc());
			movement.setTaxApplicable(waiverdetail.isTaxApplicable());
			movement.setTaxComponent(waiverdetail.getTaxComponent());

			if (taxHeader != null) {
				// Setting of Debit Invoice ID
				movement.setDebitInvoiceId(manualAdviseDAO.getDebitInvoiceID(advise.getAdviseID()));

				movement.setTaxHeaderId(taxHeader.getHeaderId());
				movement.setTaxHeader(taxHeader);
				movement.setWaivedCGST(taxSplit.getcGST());
				movement.setWaivedSGST(taxSplit.getsGST());
				movement.setWaivedIGST(taxSplit.getiGST());
				movement.setWaivedUGST(taxSplit.getuGST());
			}

			manualAdviseDAO.saveMovement(movement, "");
		}

		logger.debug(Literal.LEAVING);

		// GST Invoice data resetting based on Accounting Process
		if (SysParamUtil.isAllowed(SMTParameterConstants.GST_INV_ON_DUE) && advise.isDueCreation()) {
			return movement;
		}

		return null;
	}

	private TaxAmountSplit getExclusiveTax(ManualAdvise advise, Map<String, BigDecimal> gstPercentages) {
		TaxAmountSplit taxSplit = GSTCalculator.getExclusiveGST(advise.getAdviseAmount(), gstPercentages);

		taxSplit.setcGST(taxSplit.getcGST().subtract(advise.getPaidCGST()).subtract(advise.getWaivedCGST()));
		taxSplit.setsGST(taxSplit.getsGST().subtract(advise.getPaidSGST()).subtract(advise.getWaivedSGST()));
		taxSplit.setiGST(taxSplit.getiGST().subtract(advise.getPaidIGST()).subtract(advise.getWaivedIGST()));
		taxSplit.setuGST(taxSplit.getuGST().subtract(advise.getPaidUGST()).subtract(advise.getWaivedUGST()));

		return taxSplit;
	}

	private TaxHeader taxSplitting(Map<String, BigDecimal> gstPercentages, TaxAmountSplit taxSplit,
			ManualAdvise advise) {
		logger.debug(Literal.ENTERING);

		TaxHeader taxHeader = new TaxHeader();
		List<Taxes> taxes = new ArrayList<>();

		for (String gstType : gstPercentages.keySet()) {
			Taxes tax = new Taxes();

			if (RuleConstants.CODE_CGST.equals(gstType)) {
				tax.setWaivedTax(taxSplit.getcGST());
				advise.setWaivedCGST(advise.getWaivedCGST().add(taxSplit.getcGST()));
			} else if (RuleConstants.CODE_SGST.equals(gstType)) {
				tax.setWaivedTax(taxSplit.getsGST());
				advise.setWaivedSGST(advise.getWaivedSGST().add(taxSplit.getsGST()));
			} else if (RuleConstants.CODE_IGST.equals(gstType)) {
				tax.setWaivedTax(taxSplit.getiGST());
				advise.setWaivedIGST(advise.getWaivedIGST().add(taxSplit.getiGST()));
			} else if (RuleConstants.CODE_UGST.equals(gstType)) {
				tax.setWaivedTax(taxSplit.getuGST());
				advise.setWaivedUGST(advise.getWaivedUGST().add(taxSplit.getuGST()));
			} else if (RuleConstants.CODE_CESS.equals(gstType)) {
				tax.setWaivedTax(taxSplit.getCess());
				advise.setWaivedCESS(advise.getWaivedCESS().add(taxSplit.getCess()));
			} else {
				continue;
			}

			tax.setTaxPerc(gstPercentages.get(gstType));
			tax.setTaxType(gstType);
			tax.setNewRecord(true);
			tax.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			tax.setVersion(1);
			taxes.add(tax);
		}

		taxHeader.setNewRecord(true);
		taxHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		taxHeader.setVersion(1);
		taxHeader.setTaxDetails(taxes);

		// Saving the Tax Header and Tax Details
		taxHeaderDetailsService.doApprove(taxHeader, "", "");

		logger.debug(Literal.LEAVING);

		return taxHeader;
	}

	private TaxHeader taxSplitting(Map<String, BigDecimal> gstPercentages, TaxAmountSplit taxSplit) {
		logger.debug(Literal.ENTERING);

		TaxHeader taxHeader = new TaxHeader();
		List<Taxes> taxes = new ArrayList<>();

		for (String gstType : gstPercentages.keySet()) {
			Taxes tax = new Taxes();

			if (RuleConstants.CODE_CGST.equals(gstType)) {
				tax.setWaivedTax(taxSplit.getcGST());
			} else if (RuleConstants.CODE_SGST.equals(gstType)) {
				tax.setWaivedTax(taxSplit.getsGST());
			} else if (RuleConstants.CODE_IGST.equals(gstType)) {
				tax.setWaivedTax(taxSplit.getiGST());
			} else if (RuleConstants.CODE_UGST.equals(gstType)) {
				tax.setWaivedTax(taxSplit.getuGST());
			} else if (RuleConstants.CODE_CESS.equals(gstType)) {
				tax.setWaivedTax(taxSplit.getCess());
			} else {
				continue;
			}

			tax.setTaxPerc(gstPercentages.get(gstType));
			tax.setTaxType(gstType);
			tax.setNewRecord(true);
			tax.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			tax.setVersion(1);
			taxes.add(tax);
		}

		taxHeader.setNewRecord(true);
		taxHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		taxHeader.setVersion(1);
		taxHeader.setTaxDetails(taxes);

		// Saving the Tax Header and Tax Details
		taxHeaderDetailsService.doApprove(taxHeader, "", "");

		logger.debug(Literal.LEAVING);

		return taxHeader;
	}

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();

		auditHeader = businessValidation(auditHeader, "doReject");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FeeWaiverHeader fwh = (FeeWaiverHeader) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		// List Delete
		auditHeader.setAuditDetails(processChildsAudit(deleteChilds(fwh, "_Temp", auditHeader.getAuditTranType())));

		feeWaiverHeaderDAO.delete(fwh, TableType.TEMP_TAB);

		// auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, feeWaiverHeader.getBefImage(),
		// feeWaiverHeader));

		financeMainDAO.updateMaintainceStatus(fwh.getFinID(), "");

		finServiceInstrutionDAO.deleteList(fwh.getFinID(), fwh.getEvent(), "_Temp");
		// Extended field Render Details.
		List<AuditDetail> extendedDetails = fwh.getAuditDetailMap().get("ExtendedFieldDetails");
		if (extendedDetails != null && extendedDetails.size() > 0) {
			auditDetails.addAll(extendedFieldDetailsService.delete(fwh.getExtendedFieldHeader(), fwh.getFinReference(),
					fwh.getExtendedFieldRender().getSeqNo(), "_Temp", auditHeader.getAuditTranType(), extendedDetails));
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private List<AuditDetail> processChildsAudit(List<AuditDetail> list) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (list == null || list.isEmpty()) {
			return auditDetails;
		}

		for (AuditDetail detail : list) {
			String transType = "";
			String rcdType = "";
			Object object = detail.getModelData();

			if (object instanceof FeeWaiverDetail) {
				rcdType = ((FeeWaiverDetail) object).getRecordType();
			}

			if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(rcdType)) {
				transType = PennantConstants.TRAN_ADD;
			} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(rcdType)
					|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(rcdType)) {
				transType = PennantConstants.TRAN_DEL;
			} else {
				transType = PennantConstants.TRAN_UPD;
			}

			auditDetails.add(new AuditDetail(transType, detail.getAuditSeq(), detail.getBefImage(), object));
		}

		logger.debug(Literal.LEAVING);

		return auditDetails;
	}

	public List<AuditDetail> deleteChilds(FeeWaiverHeader feeWaiverHeader, String tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (CollectionUtils.isNotEmpty(feeWaiverHeader.getFeeWaiverDetails())) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FeeWaiverDetail(),
					new FeeWaiverDetail().getExcludeFields());
			for (int i = 0; i < feeWaiverHeader.getFeeWaiverDetails().size(); i++) {
				FeeWaiverDetail feeDetail = feeWaiverHeader.getFeeWaiverDetails().get(i);
				if (StringUtils.isNotEmpty(feeDetail.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							feeDetail.getBefImage(), feeDetail));
				}
				if (feeDetail.getTaxHeader() != null) {
					taxHeaderDetailsService.doReject(feeDetail.getTaxHeader());
				}
				feeWaiverDetailDAO.delete(feeDetail, TableType.TEMP_TAB);
			}
		}

		logger.debug(Literal.LEAVING);

		return auditDetails;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		FeeWaiverHeader fwh = (FeeWaiverHeader) auditHeader.getAuditDetail().getModelData();

		// Extended field details Validation
		if (fwh.getExtendedFieldRender() != null) {
			List<AuditDetail> details = fwh.getAuditDetailMap().get("ExtendedFieldDetails");
			ExtendedFieldHeader extHeader = fwh.getExtendedFieldHeader();
			details = extendedFieldDetailsService.validateExtendedDdetails(extHeader, details, method,
					auditHeader.getUsrLanguage());
			auditDetails.addAll(details);
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FeeWaiverHeader feeWaiverHeader = (FeeWaiverHeader) auditHeader.getAuditDetail().getModelData();
		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (feeWaiverHeader.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		List<FeeWaiverDetail> feeWaiverDetails = feeWaiverHeader.getFeeWaiverDetails();

		if (CollectionUtils.isNotEmpty(feeWaiverDetails)) {
			auditDetailMap.put("FeeWaiverDetails", setFeeWaiverAuditData(feeWaiverHeader, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FeeWaiverDetails"));
		}

		// Extended Field Details
		if (feeWaiverHeader.getExtendedFieldRender() != null) {
			auditDetailMap.put("ExtendedFieldDetails",
					extendedFieldDetailsService.setExtendedFieldsAuditData(feeWaiverHeader.getExtendedFieldHeader(),
							feeWaiverHeader.getExtendedFieldRender(), auditTranType, method,
							feeWaiverHeader.getExtendedFieldHeader().getModuleName()));
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldDetails"));
		}

		feeWaiverHeader.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(feeWaiverHeader);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	private List<AuditDetail> setFeeWaiverAuditData(FeeWaiverHeader fwh, String auditTranType, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		int i = 0;
		for (FeeWaiverDetail fwd : fwh.getFeeWaiverDetails()) {
			if (StringUtils.isEmpty(StringUtils.trimToEmpty(fwd.getRecordType()))) {
				continue;
			}

			fwd.setWorkflowId(fwh.getWorkflowId());
			boolean isRcdType = false;

			if (fwd.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				fwd.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (fwd.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				fwd.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (fwh.isWorkflow()) {
					isRcdType = true;
				}
			} else if (fwd.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				fwd.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				fwd.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (fwd.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (fwd.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| fwd.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			fwd.setRecordStatus(fwh.getRecordStatus());
			fwd.setUserDetails(fwh.getUserDetails());
			fwd.setLastMntOn(fwh.getLastMntOn());

			String[] fields = PennantJavaUtil.getFieldDetails(fwd, fwd.getExcludeFields());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], fwd.getBefImage(), fwd));
			i++;
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		FeeWaiverHeader fwh = (FeeWaiverHeader) auditDetail.getModelData();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		BigDecimal totalPenalityBal = BigDecimal.ZERO;
		BigDecimal totalLPIBal = BigDecimal.ZERO;

		// update the waiver amounts to the tables.
		for (FeeWaiverDetail fwd : fwh.getFeeWaiverDetails()) {

			if (!Allocation.ODC.equals(fwd.getFeeTypeCode()) && !Allocation.LPFT.equals(fwd.getFeeTypeCode())) {

				manualAdviseList = manualAdviseDAO.getManualAdvise(fwh.getFinID());

				for (ManualAdvise manualAdvise : manualAdviseList) {

					// validate the current waived amount against the manual advise.
					if (manualAdvise.getAdviseID() == fwd.getAdviseId()) {
						BigDecimal waiverAmount = BigDecimal.ZERO;
						if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(fwd.getTaxComponent())) {
							waiverAmount = fwd.getCurrActualWaiver();
						} else {
							waiverAmount = fwd.getCurrWaiverAmount();
						}

						if (waiverAmount.compareTo(manualAdvise.getBalanceAmt()) > 0) {
							valueParm[0] = String.valueOf(waiverAmount);
							errParm[0] = fwd.getFeeTypeDesc();
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "91136", errParm, valueParm),
									usrLanguage));
						}
					}
				}
			} else {

				List<FinODDetails> odLIst = finODDetailsDAO.getFinODPenalityByFinRef(fwh.getFinID(), false, false);
				for (FinODDetails od : odLIst) {
					totalPenalityBal = totalPenalityBal.add(od.getTotPenaltyBal());
				}

				List<FinODDetails> finodprofitdetails = finODDetailsDAO.getFinODPenalityByFinRef(fwh.getFinID(), true,
						false);
				for (FinODDetails oddetails : finodprofitdetails) {
					totalLPIBal = totalLPIBal.add(oddetails.getLPIBal());
				}

				// validate the current waived amount against Late pay penalty.
				if (fwd.getFeeTypeCode().equals(Allocation.ODC)) {
					BigDecimal waiverAmount = BigDecimal.ZERO;
					if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(fwd.getTaxComponent())) {
						waiverAmount = fwd.getCurrActualWaiver();
					} else {
						waiverAmount = fwd.getCurrWaiverAmount();
					}

					if (waiverAmount.compareTo(totalPenalityBal) > 0) {
						valueParm[0] = String.valueOf(
								PennantApplicationUtil.amountFormate(waiverAmount, PennantConstants.defaultCCYDecPos));
						errParm[0] = fwd.getFeeTypeDesc() + ": " + valueParm[0];
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "91136", errParm, valueParm), usrLanguage));
					}
				}

				// validate the current waived amount against the late pay profit
				if (fwd.getFeeTypeCode().equals(Allocation.LPFT)
						&& fwd.getCurrWaiverAmount().compareTo(totalLPIBal) > 0) {
					valueParm[0] = String.valueOf(fwd.getCurrWaiverAmount());
					errParm[0] = fwd.getFeeTypeDesc() + ": " + valueParm[0];
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "91136", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);

		return auditDetail;
	}

	@Override
	public List<FeeWaiverHeader> getFeeWaiverHeaderByFinReference(long finID, String type) {
		return feeWaiverHeaderDAO.getFeeWaiverHeaderByFinReference(finID, type);
	}

	@Override
	public Date getMaxFullFillDate(long finID) {
		return feeWaiverHeaderDAO.getMaxFullFillDate(finID);
	}

	@Override
	public List<ManualAdvise> getManualAdviseByFinRef(long finID) {
		return this.manualAdviseDAO.getManualAdvise(finID);
	}

	@Override
	public List<FinODDetails> getFinODBalByFinRef(long finID) {
		return finODDetailsDAO.getFinODBalByFinRef(finID);
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setFeeWaiverHeaderDAO(FeeWaiverHeaderDAO feeWaiverHeaderDAO) {
		this.feeWaiverHeaderDAO = feeWaiverHeaderDAO;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public void setFeeWaiverDetailDAO(FeeWaiverDetailDAO feeWaiverDetailDAO) {
		this.feeWaiverDetailDAO = feeWaiverDetailDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	public void setTaxHeaderDetailsService(TaxHeaderDetailsService taxHeaderDetailsService) {
		this.taxHeaderDetailsService = taxHeaderDetailsService;
	}

	public void setGstInvoiceTxnService(GSTInvoiceTxnService gstInvoiceTxnService) {
		this.gstInvoiceTxnService = gstInvoiceTxnService;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public void setFinODAmzTaxDetailDAO(FinODAmzTaxDetailDAO finODAmzTaxDetailDAO) {
		this.finODAmzTaxDetailDAO = finODAmzTaxDetailDAO;
	}

	public void setTaxHeaderDetailsDAO(TaxHeaderDetailsDAO taxHeaderDetailsDAO) {
		this.taxHeaderDetailsDAO = taxHeaderDetailsDAO;
	}

	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	public void setRepaymentPostingsUtil(RepaymentPostingsUtil repaymentPostingsUtil) {
		this.repaymentPostingsUtil = repaymentPostingsUtil;
	}

	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	public void setPresentmentDetailDAO(PresentmentDetailDAO presentmentDetailDAO) {
		this.presentmentDetailDAO = presentmentDetailDAO;
	}

	public void setManualAdviseList(List<ManualAdvise> manualAdviseList) {
		this.manualAdviseList = manualAdviseList;
	}

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	public void setFinServiceInstrutionDAO(FinServiceInstrutionDAO finServiceInstrutionDAO) {
		this.finServiceInstrutionDAO = finServiceInstrutionDAO;
	}

	@Autowired
	public void setLoanPaymentService(LoanPaymentService loanPaymentService) {
		this.loanPaymentService = loanPaymentService;
	}

	@Autowired
	public void setFinODCAmountDAO(FinODCAmountDAO finODCAmountDAO) {
		this.finODCAmountDAO = finODCAmountDAO;
	}
}