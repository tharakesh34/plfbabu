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
 * FileName : OverDueRecoveryPostingsUtil.java *
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
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.app.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.dao.applicationmaster.AssignmentDAO;
import com.pennant.backend.dao.applicationmaster.AssignmentDealDAO;
import com.pennant.backend.dao.finance.FinODAmzTaxDetailDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.finance.TaxHeaderDetailsDAO;
import com.pennant.backend.dao.financemanagement.OverdueChargeRecoveryDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueueHeader;
import com.pennant.backend.model.applicationmaster.Assignment;
import com.pennant.backend.model.applicationmaster.AssignmentDealExcludedFee;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.FinODAmzTaxDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinTaxIncomeDetail;
import com.pennant.backend.model.finance.FinTaxReceivable;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.InvoiceDetail;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.OverdueTaxMovement;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.finance.GSTInvoiceTxnService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.overdue.constants.ChargeType;
import com.pennanttech.pff.overdue.constants.PenaltyCalculator;

public class OverDueRecoveryPostingsUtil implements Serializable {
	private static final long serialVersionUID = 6161809223570900644L;
	private static Logger logger = LogManager.getLogger(OverDueRecoveryPostingsUtil.class);

	private FinODDetailsDAO finODDetailsDAO;
	private OverdueChargeRecoveryDAO recoveryDAO;
	private FinODPenaltyRateDAO finODPenaltyRateDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private AssignmentDAO assignmentDAO;
	private AssignmentDealDAO assignmentDealDAO;
	private FinODAmzTaxDetailDAO finODAmzTaxDetailDAO;
	private GSTInvoiceTxnService gstInvoiceTxnService;
	private TaxHeaderDetailsDAO taxHeaderDetailsDAO;

	private final BigDecimal HUNDRED = new BigDecimal(100);
	private String TDS_ROUND_MODE = null;
	private int TDS_ROUND_TRAGET = 0;
	private BigDecimal TDS_PERCENTAGE = BigDecimal.ZERO;

	/**
	 * Default constructor
	 */
	public OverDueRecoveryPostingsUtil() {
		super();
	}

	public List<Object> recoveryPayment(FinanceMain fm, Date dateValueDate, Date postDate,
			ManualAdviseMovements movement, Date movementDate, AEEvent aeEvent, FinRepayQueueHeader rpyQueueHeader)
			throws AppException {
		logger.debug(Literal.ENTERING);

		// AmountCodes Preparation
		// EOD Repayments should pass the value date as schedule for which
		// Repayment is processing
		AEAmountCodes amountCodes = null;

		EventProperties eventProperties = fm.getEventProperties();

		String phase = null;
		boolean isGSTInvOnDue = false;
		if (eventProperties.isParameterLoaded()) {
			phase = eventProperties.getPhase();
			isGSTInvOnDue = eventProperties.isGstInvOnDue();
		} else {
			phase = SysParamUtil.getValueAsString(PennantConstants.APP_PHASE);
			isGSTInvOnDue = SysParamUtil.isAllowed(SMTParameterConstants.GST_INV_ON_DUE);
		}

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		if (aeEvent == null) {
			aeEvent = new AEEvent();
			aeEvent.setAeAmountCodes(new AEAmountCodes());
			amountCodes = aeEvent.getAeAmountCodes();

			aeEvent.setFinID(finID);
			aeEvent.setFinReference(finReference);
			aeEvent.setCustID(fm.getCustID());
			aeEvent.setFinType(fm.getFinType());
			aeEvent.setBranch(fm.getFinBranch());
			aeEvent.setCcy(fm.getFinCcy());
			aeEvent.setPostingUserBranch(rpyQueueHeader.getPostBranch());
			aeEvent.setAccountingEvent(AccountingEvent.LATEPAY);
			aeEvent.setValueDate(dateValueDate);
			aeEvent.setPostDate(postDate);
			aeEvent.setEntityCode(fm.getLovDescEntityCode());
			amountCodes.setPartnerBankAc(rpyQueueHeader.getPartnerBankAc());
			amountCodes.setPartnerBankAcType(rpyQueueHeader.getPartnerBankAcType());
			amountCodes.setFinType(fm.getFinType());

		} else {
			amountCodes = aeEvent.getAeAmountCodes();
		}

		amountCodes.setPenaltyPaid(movement.getPaidAmount());
		amountCodes.setPenaltyWaived(movement.getWaivedAmount());
		amountCodes.setPaymentType(rpyQueueHeader.getPayType());
		amountCodes.setLpiPaid(rpyQueueHeader.getLateProfit());
		amountCodes.setLpiWaived(rpyQueueHeader.getLatePftWaived());
		aeEvent.setPostRefId(rpyQueueHeader.getReceiptId());
		aeEvent.setPostingId(fm.getPostingId());
		aeEvent.setEOD(false);
		aeEvent.setEventProperties(eventProperties);

		if (!phase.equals(PennantConstants.APP_PHASE_DAY)) {
			aeEvent.setEOD(true);
		}

		// LPP Receivable Amounts setting for Accounting difference
		FinTaxReceivable taxRcv = finODAmzTaxDetailDAO.getFinTaxReceivable(finID, "LPP");
		if (taxRcv != null) {
			if (taxRcv.getReceivableAmount()
					.compareTo(amountCodes.getPenaltyPaid().add(amountCodes.getPenaltyWaived())) < 0) {
				amountCodes.setPenaltyRcv(taxRcv.getReceivableAmount());
				amountCodes.setPenaltyAccr(rpyQueueHeader.getPenalty().subtract(taxRcv.getReceivableAmount()));
			} else {
				amountCodes.setPenaltyRcv(amountCodes.getPenaltyPaid().add(amountCodes.getPenaltyWaived()));
				amountCodes.setPenaltyAccr(BigDecimal.ZERO);
			}
		}

		Set<String> excludeFees = null;
		if (fm.getAssignmentId() > 0) {
			Assignment assignment = assignmentDAO.getAssignment(fm.getAssignmentId(), "");
			if (assignment != null) {
				amountCodes.setAssignmentPerc(assignment.getSharingPercentage());
				List<AssignmentDealExcludedFee> excludeFeesList = this.assignmentDealDAO
						.getApprovedAssignmentDealExcludedFeeList(assignment.getDealId());
				if (CollectionUtils.isNotEmpty(excludeFeesList)) {
					excludeFees = new HashSet<String>();
					for (AssignmentDealExcludedFee excludeFee : excludeFeesList) {
						excludeFees.add(excludeFee.getFeeTypeCode());
					}
				}
			}
		}

		Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

		if (excludeFees != null) {
			dataMap.put(AccountConstants.POSTINGS_EXCLUDE_FEES, excludeFees);
		}

		BigDecimal cgstPaid = BigDecimal.ZERO;
		BigDecimal sgstPaid = BigDecimal.ZERO;
		BigDecimal igstPaid = BigDecimal.ZERO;
		BigDecimal ugstPaid = BigDecimal.ZERO;
		BigDecimal cessPaid = BigDecimal.ZERO;

		BigDecimal cgstWaived = BigDecimal.ZERO;
		BigDecimal sgstWaived = BigDecimal.ZERO;
		BigDecimal igstWaived = BigDecimal.ZERO;
		BigDecimal ugstWaived = BigDecimal.ZERO;
		BigDecimal cessWaived = BigDecimal.ZERO;

		TaxHeader taxHeader = movement.getTaxHeader();
		if (taxHeader != null && CollectionUtils.isNotEmpty(taxHeader.getTaxDetails())) {
			for (Taxes taxes : taxHeader.getTaxDetails()) {
				switch (taxes.getTaxType()) {
				case RuleConstants.CODE_CGST:
					cgstPaid = cgstPaid.add(taxes.getPaidTax());
					cgstWaived = cgstWaived.add(taxes.getWaivedTax());
					break;
				case RuleConstants.CODE_SGST:
					sgstPaid = sgstPaid.add(taxes.getPaidTax());
					sgstWaived = sgstWaived.add(taxes.getWaivedTax());
					break;
				case RuleConstants.CODE_UGST:
					ugstPaid = ugstPaid.add(taxes.getPaidTax());
					ugstWaived = ugstWaived.add(taxes.getWaivedTax());
					break;
				case RuleConstants.CODE_IGST:
					igstPaid = igstPaid.add(taxes.getPaidTax());
					igstWaived = igstWaived.add(taxes.getWaivedTax());
					break;
				case RuleConstants.CODE_CESS:
					cessPaid = cessPaid.add(taxes.getPaidTax());
					cessWaived = cessWaived.add(taxes.getWaivedTax());
					break;

				default:
					break;
				}
			}
		}

		// GST Field Details
		dataMap.put("LPP_CGST_P", cgstPaid);
		dataMap.put("LPP_SGST_P", sgstPaid);
		dataMap.put("LPP_IGST_P", igstPaid);
		dataMap.put("LPP_UGST_P", ugstPaid);
		dataMap.put("LPP_CESS_P", cessPaid);

		// GST Waivers Details
		dataMap.put("LPP_CGST_W", cgstWaived);
		dataMap.put("LPP_SGST_W", sgstWaived);
		dataMap.put("LPP_UGST_W", ugstWaived);
		dataMap.put("LPP_IGST_W", igstWaived);
		dataMap.put("LPP_CESS_W", cessWaived);

		if (taxRcv != null) {
			if (taxRcv.getCGST().compareTo(cgstPaid) < 0) {
				dataMap.put("LPP_CGST_R", taxRcv.getCGST());
			} else {
				dataMap.put("LPP_CGST_R", cgstPaid);
			}

			if (taxRcv.getSGST().compareTo(sgstPaid) < 0) {
				dataMap.put("LPP_SGST_R", taxRcv.getSGST());
			} else {
				dataMap.put("LPP_SGST_R", sgstPaid);
			}

			if (taxRcv.getUGST().compareTo(ugstPaid) < 0) {
				dataMap.put("LPP_UGST_R", taxRcv.getUGST());
			} else {
				dataMap.put("LPP_UGST_R", ugstPaid);
			}

			if (taxRcv.getIGST().compareTo(igstPaid) < 0) {
				dataMap.put("LPP_IGST_R", taxRcv.getIGST());
			} else {
				dataMap.put("LPP_IGST_R", igstPaid);
			}

			if (taxRcv.getCESS().compareTo(cessPaid) < 0) {
				dataMap.put("LPP_CESS_R", taxRcv.getCESS());
			} else {
				dataMap.put("LPP_CESS_R", cessPaid);
			}
		} else {
			addZeroifNotContains(dataMap, "LPP_CGST_R");
			addZeroifNotContains(dataMap, "LPP_SGST_R");
			addZeroifNotContains(dataMap, "LPP_UGST_R");
			addZeroifNotContains(dataMap, "LPP_IGST_R");
			addZeroifNotContains(dataMap, "LPP_CESS_R");
		}

		if (TDSCalculator.isTDSApplicable(fm) && movement.isTdsReq()) {
			BigDecimal totalGST = cgstPaid.add(sgstPaid).add(igstPaid).add(ugstPaid).add(cessPaid);
			BigDecimal paidAmount = movement.getPaidAmount();
			movement.setTdsPaid(calcTDSOnFee(paidAmount, totalGST, movement.getTaxComponent()));
		}

		dataMap.put("LPP_TDS_P", movement.getTdsPaid());

		if (rpyQueueHeader.getProfit().compareTo(BigDecimal.ZERO) == 0
				&& rpyQueueHeader.getPrincipal().compareTo(BigDecimal.ZERO) == 0
				&& rpyQueueHeader.getTds().compareTo(BigDecimal.ZERO) == 0
				&& rpyQueueHeader.getLateProfit().compareTo(BigDecimal.ZERO) == 0
				&& rpyQueueHeader.getPenalty().compareTo(BigDecimal.ZERO) > 0) {

			if (rpyQueueHeader.getExtDataMap() != null) {
				dataMap.putAll(rpyQueueHeader.getExtDataMap());
			}
		} else {
			if (rpyQueueHeader.getExtDataMap() != null) {
				if (rpyQueueHeader.getExtDataMap().containsKey("LPP_TDS_P")) {
					dataMap.put("LPP_TDS_P", rpyQueueHeader.getExtDataMap().get("LPP_TDS_P"));
				}
				if (rpyQueueHeader.getExtDataMap().containsKey("LPP_TDS_N")) {
					dataMap.put("LPP_TDS_N", rpyQueueHeader.getExtDataMap().get("LPP_TDS_N"));
				}
			}
		}

		if (rpyQueueHeader.getGstExecutionMap() != null) {
			dataMap.putAll(rpyQueueHeader.getGstExecutionMap());
		}

		aeEvent.setAccountingEvent(AccountingEvent.LATEPAY);

		aeEvent.setDataMap(dataMap);
		aeEvent.getAcSetIDList().clear();

		aeEvent.getAcSetIDList().add(AccountingEngine.getAccountSetID(fm, aeEvent.getAccountingEvent()));

		// Posting details calling
		aeEvent.setSimulateAccounting(fm.isSimulateAccounting());
		aeEvent = postingsPreparationUtil.postAccounting(aeEvent);
		aeEvent.getAeAmountCodes().setPenaltyPaid(BigDecimal.ZERO);
		aeEvent.getAeAmountCodes().setPenaltyWaived(BigDecimal.ZERO);

		// GST Invoice data resetting based on Accounting Process
		if (isGSTInvOnDue) {
			movement.setPaidAmount(movement.getPaidAmount().subtract(amountCodes.getPenaltyRcv()));
			cgstPaid = cgstPaid.subtract(new BigDecimal(dataMap.get("LPP_CGST_R").toString()));
			sgstPaid = sgstPaid.subtract(new BigDecimal(dataMap.get("LPP_SGST_R").toString()));
			igstPaid = igstPaid.subtract(new BigDecimal(dataMap.get("LPP_IGST_R").toString()));
			ugstPaid = ugstPaid.subtract(new BigDecimal(dataMap.get("LPP_UGST_R").toString()));
			cessPaid = cessPaid.subtract(new BigDecimal(dataMap.get("LPP_CESS_R").toString()));

			// Update Receivable Tax details to make future postings correctly
			if (taxRcv != null) {
				taxRcv.setReceivableAmount(taxRcv.getReceivableAmount().subtract(amountCodes.getPenaltyRcv()));
				taxRcv.setCGST(taxRcv.getCGST().subtract(new BigDecimal(dataMap.get("LPP_CGST_R").toString())));
				taxRcv.setSGST(taxRcv.getSGST().subtract(new BigDecimal(dataMap.get("LPP_SGST_R").toString())));
				taxRcv.setUGST(taxRcv.getUGST().subtract(new BigDecimal(dataMap.get("LPP_UGST_R").toString())));
				taxRcv.setIGST(taxRcv.getIGST().subtract(new BigDecimal(dataMap.get("LPP_IGST_R").toString())));
				taxRcv.setCESS(taxRcv.getCESS().subtract(new BigDecimal(dataMap.get("LPP_CESS_R").toString())));
				if (!aeEvent.isSimulateAccounting()) {
					finODAmzTaxDetailDAO.updateTaxReceivable(taxRcv);
				}
			}
		}

		List<Object> returnList = new ArrayList<>();
		returnList.add(aeEvent);

		// GST Invoice
		if (movement != null && movement.getPaidAmount().compareTo(BigDecimal.ZERO) > 0 && aeEvent.isPostingSucess()
				&& aeEvent.getLinkedTranId() > 0) {

			List<ManualAdviseMovements> advMovements = new ArrayList<>();
			advMovements.add(movement);

			FinanceDetail financeDetail = new FinanceDetail();
			financeDetail.getFinScheduleData().setFinanceMain(fm);

			if (!aeEvent.isSimulateAccounting()) {
				InvoiceDetail invoiceDetail = new InvoiceDetail();
				invoiceDetail.setLinkedTranId(aeEvent.getLinkedTranId());
				invoiceDetail.setFinanceDetail(financeDetail);
				invoiceDetail.setMovements(advMovements);
				invoiceDetail.setWaiver(false);
				invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_DEBIT);

				this.gstInvoiceTxnService.advTaxInvoicePreparation(invoiceDetail);
			}

			// Saving Tax Income Details
			FinTaxIncomeDetail taxIncome = new FinTaxIncomeDetail();
			taxIncome.setRepayID(rpyQueueHeader.getRepayID());
			taxIncome.setTaxFor("LPP");
			taxIncome.setReceivedAmount(movement.getPaidAmount());
			taxIncome.setCGST(cgstPaid);
			taxIncome.setSGST(sgstPaid);
			taxIncome.setUGST(ugstPaid);
			taxIncome.setIGST(igstPaid);
			taxIncome.setCESS(cessPaid);
			if (!aeEvent.isSimulateAccounting()) {
				finODAmzTaxDetailDAO.saveTaxIncome(taxIncome);
			}

			returnList.add(taxIncome);

		} else {
			returnList.add(null);
		}

		if (fm.isSimulateAccounting()) {
			if (CollectionUtils.isNotEmpty(fm.getReturnDataSet())) {
				fm.getReturnDataSet().addAll(aeEvent.getReturnDataSet());
			} else {
				fm.setReturnDataSet(aeEvent.getReturnDataSet());
			}
		}

		// Method for Creating Invoice Details for the Waived amount against Created Due invoices
		prepareTaxMovement(rpyQueueHeader, fm, movement, aeEvent.getLinkedTranId());

		logger.debug("Leaving");
		return returnList;
	}

	private BigDecimal calcTDSOnFee(BigDecimal paidAmount, BigDecimal totalGst, String taxComponent) {
		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equalsIgnoreCase(taxComponent)) {
			paidAmount = paidAmount.subtract(totalGst);
		}

		return getTDSAmount(paidAmount);
	}

	private void prepareTaxMovement(FinRepayQueueHeader rpyQueueHeader, FinanceMain fm, ManualAdviseMovements advMov,
			long linkedTranID) {

		// Prepare GST Details based on Invoice ID which was incomized
		List<OverdueTaxMovement> odTaxMovList = new ArrayList<>();
		List<FinODAmzTaxDetail> dueTaxList = finODAmzTaxDetailDAO.getODTaxList(fm.getFinID());

		// Sorting Due Tax details based on Valuedate
		dueTaxList = sortDueTaxDetails(dueTaxList);
		OverdueTaxMovement movement = null;
		List<FinODAmzTaxDetail> updateDueList = new ArrayList<>();

		for (FinRepayQueue queue : rpyQueueHeader.getQueueList()) {

			BigDecimal lppPaid = queue.getPenaltyPayNow();
			BigDecimal lppWaived = queue.getWaivedAmount();

			// if No Waiver Amount & Paid Amounts on the schedule date
			if (lppPaid.compareTo(BigDecimal.ZERO) == 0 && lppWaived.compareTo(BigDecimal.ZERO) == 0) {
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
				movement.setSchDate(queue.getRpyDate());
				movement.setFinReference(queue.getFinReference());
				movement.setTaxFor(taxDetail.getTaxFor());

				if (balAmount.compareTo(queue.getPenaltyPayNow()) > 0) {
					movement.setPaidAmount(queue.getPenaltyPayNow());
				} else {
					movement.setPaidAmount(balAmount);
				}

				balAmount = balAmount.subtract(movement.getPaidAmount());
				lppPaid = lppPaid.subtract(movement.getPaidAmount());
				taxDetail.setPaidAmount(taxDetail.getPaidAmount().add(movement.getPaidAmount()));

				if (balAmount.compareTo(queue.getWaivedAmount()) > 0) {
					movement.setWaivedAmount(queue.getWaivedAmount());
				} else {
					movement.setWaivedAmount(balAmount);
				}
				taxDetail.setWaivedAmount(taxDetail.getWaivedAmount().add(movement.getWaivedAmount()));
				lppWaived = lppWaived.subtract(movement.getWaivedAmount());

				updateDueList.add(taxDetail);

				// Tax Header Preparation
				TaxHeader header = queue.getTaxHeader();
				if (header != null) {
					TaxHeader taxHeader = header.copyEntity();

					taxHeader.setHeaderId(0);
					if (CollectionUtils.isNotEmpty(taxHeader.getTaxDetails())) {

						for (Taxes taxes : taxHeader.getTaxDetails()) {
							BigDecimal paidAmount = movement.getPaidAmount();
							BigDecimal waivedAmount = movement.getWaivedAmount();

							if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(advMov.getTaxComponent())) {
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
				if (lppPaid.compareTo(BigDecimal.ZERO) == 0 && lppWaived.compareTo(BigDecimal.ZERO) == 0) {
					break;
				}
			}
		}

		// If Amount was incomized on month end then waivers should be created as Credit invoice
		if (!odTaxMovList.isEmpty()) {
			if (rpyQueueHeader.isLppAmzReqonME()) {

				FinanceDetail financeDetail = new FinanceDetail();
				financeDetail.getFinScheduleData().setFinanceMain(fm);

				for (OverdueTaxMovement taxMovement : odTaxMovList) {

					advMov.setMovementAmount(taxMovement.getPaidAmount().add(taxMovement.getWaivedAmount()));
					advMov.setPaidAmount(taxMovement.getPaidAmount());
					advMov.setWaivedAmount(taxMovement.getWaivedAmount());
					advMov.setTaxHeader(taxMovement.getTaxHeader());
					advMov.setDebitInvoiceId(taxMovement.getInvoiceID());

					List<ManualAdviseMovements> advMovements = new ArrayList<>();
					advMovements.add(advMov);

					InvoiceDetail invoiceDetail = new InvoiceDetail();
					invoiceDetail.setLinkedTranId(linkedTranID);
					invoiceDetail.setFinanceDetail(financeDetail);
					invoiceDetail.setMovements(advMovements);
					invoiceDetail.setWaiver(true);
					invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_CREDIT);

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
				TaxHeader taxHeader = taxMovement.getTaxHeader();

				if (taxHeader != null) {
					long headerId = taxHeaderDetailsDAO.save(taxHeader, "");
					List<Taxes> taxDetails = taxHeader.getTaxDetails();
					if (CollectionUtils.isNotEmpty(taxDetails)) {
						for (Taxes taxes : taxDetails) {
							taxes.setReferenceId(headerId);
						}
						taxHeaderDetailsDAO.saveTaxes(taxDetails, "");
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

	private void addZeroifNotContains(Map<String, Object> dataMap, String key) {
		if (dataMap != null) {
			if (!dataMap.containsKey(key)) {
				dataMap.put(key, BigDecimal.ZERO);
			}
		}
	}

	public FinODDetails overDueDetailPreparation(FinRepayQueue finRepayQueue, String profitDayBasis, Date dateValueDate,
			boolean isAfterRecovery, boolean isEnqPurpose) throws AppException {
		logger.debug(Literal.ENTERING);

		Date curBussDate = SysParamUtil.getAppValueDate();

		long finID = finRepayQueue.getFinID();

		FinODDetails odDetails = finODDetailsDAO.getFinODDetailsForBatch(finID, finRepayQueue.getRpyDate());

		boolean isSave = false;
		if (odDetails == null) {
			isSave = true;
			odDetails = new FinODDetails();
		}

		prepareOverDueData(odDetails, dateValueDate, finRepayQueue, isAfterRecovery, isSave);

		if (isEnqPurpose) {
			logger.debug(Literal.LEAVING);
			return odDetails;
		}

		if (isSave && odDetails.getFinODSchdDate().compareTo(curBussDate) <= 0) {
			List<FinODDetails> odDList = new ArrayList<>();

			odDetails.setLppDueTillDate(dateValueDate);
			odDetails.setLppDueAmt(odDetails.getTotPenaltyPaid().add(odDetails.getTotWaived()));

			odDList.add(odDetails);
			finODDetailsDAO.saveList(odDList);
		} else {
			finODDetailsDAO.updateBatch(odDetails);
		}

		logger.debug(Literal.LEAVING);
		return odDetails;
	}

	public List<Object> recoveryCalculation(FinRepayQueue repayQueue, String pftDaysBasis, Date valueDate,
			boolean isRecovery, boolean isEnqPurpose) throws AppException {
		logger.debug(Literal.ENTERING);

		List<Object> odObjDetails = new ArrayList<>();

		// Overdue Detail Calculation
		FinODDetails details = overDueDetailPreparation(repayQueue, pftDaysBasis, valueDate, isRecovery, isEnqPurpose);

		// Preparation for Overdue Penalty Recovery Details
		OverdueChargeRecovery rec = overdueRecoverCalculation(details, valueDate, pftDaysBasis, isRecovery,
				isEnqPurpose, false);

		odObjDetails.add(details);
		odObjDetails.add(rec);

		logger.debug(Literal.LEAVING);
		return odObjDetails;
	}

	private OverdueChargeRecovery overdueRecoverCalculation(FinODDetails od, Date valueDate, String profitDayBasis,
			boolean isAfterRecovery, boolean isEnqPurpose, boolean doPostings) {

		Date schdDate = od.getFinODSchdDate();
		Date newPenaltyDate = DateUtil.addDays(schdDate, od.getODGraceDays());

		OverdueChargeRecovery recovery = null;
		// Check Condition for Overdue Recovery Details Entries
		if (newPenaltyDate.compareTo(valueDate) <= 0) {

			boolean searchForPenalty = true;
			// Condition Checking for Overdue Penalty Exist or not
			if (!od.isApplyODPenalty()) {
				searchForPenalty = false;
			} else {
				if (od.getODChargeAmtOrPerc().compareTo(BigDecimal.ZERO) == 0) {
					searchForPenalty = false;
				}
			}

			// Delete Max Finance Effective Date Recovery Record if Overdue Payment
			// will not happen, only for Percentage on Due Days Charge type
			OverdueChargeRecovery prvRecovery = null;
			long finID = od.getFinID();
			String finReference = od.getFinReference();
			String odFor = od.getFinODFor();
			String odChargeType = od.getODChargeType();

			if (searchForPenalty) {
				prvRecovery = recoveryDAO.getMaxOverdueChargeRecoveryById(finID, schdDate, odFor, "_AMView");

				if (ChargeType.PERC_ON_DUE_DAYS.equals(odChargeType) && prvRecovery != null && prvRecovery.isRcdCanDel()
						&& !isEnqPurpose) {
					recoveryDAO.deleteUnpaid(finID, schdDate, odFor, "");
				}
			} else if (!searchForPenalty) {
				prvRecovery = recoveryDAO.getMaxOverdueChargeRecoveryById(finID, schdDate, odFor, "_AMView");
				if (prvRecovery != null && prvRecovery.isRcdCanDel()) {
					if (!isEnqPurpose) {
						recoveryDAO.deleteUnpaid(finID, schdDate, odFor, "");
					}

					BigDecimal prvPenalty = BigDecimal.ZERO.subtract(prvRecovery.getPenalty());
					BigDecimal prvPenaltyBal = BigDecimal.ZERO.subtract(prvRecovery.getPenaltyBal());

					// Overdue Details Updation for Totals
					FinODDetails fod = new FinODDetails();
					fod.setFinID(finID);
					fod.setFinReference(finReference);
					fod.setFinODSchdDate(schdDate);
					fod.setFinODFor(odFor);
					fod.setTotPenaltyAmt(prvPenalty);
					fod.setTotPenaltyPaid(BigDecimal.ZERO);
					fod.setTotPenaltyBal(prvPenaltyBal);
					fod.setTotWaived(BigDecimal.ZERO);

					if (!isEnqPurpose) {
						finODDetailsDAO.updateTotals(fod);
					}
				}
			}

			// Save/Update Overdue Recovery Details based upon Search Criteria
			if (searchForPenalty) {

				BigDecimal prvPenalty = BigDecimal.ZERO;
				BigDecimal prvPenaltyBal = BigDecimal.ZERO;

				if (ChargeType.PERC_ON_DUE_DAYS.equals(odChargeType) && prvRecovery != null
						&& prvRecovery.isRcdCanDel()) {

					String tableType = "_AMView";
					if (isEnqPurpose) {
						tableType = "_ATView";
					}

					recovery = recoveryDAO.getMaxOverdueChargeRecoveryById(finID, schdDate, odFor, tableType);
				} else {
					recovery = prvRecovery;
				}

				boolean resetTotals = true;
				int seqNo = 1;
				// Stop calculation for paid penalty for Charge Type 'FLAT' & 'PERCONETIME'
				if (!ChargeType.PERC_ON_DUE_DAYS.equals(odChargeType) && recovery != null
						&& (recovery.getPenaltyPaid().compareTo(BigDecimal.ZERO) > 0
								|| recovery.getWaivedAmt().compareTo(BigDecimal.ZERO) > 0)) {

					return recovery;

				} else {

					// Store Previous values for Total Calculations
					if (recovery != null) {
						seqNo = recovery.getSeqNo() + 1;

						if (prvRecovery != null && prvRecovery.isRcdCanDel()) {
							prvPenalty = prvPenalty.subtract(prvRecovery.getPenalty());
							prvPenaltyBal = prvPenaltyBal.subtract(prvRecovery.getPenalty());
						}
						resetTotals = false;
					}

					if (!ChargeType.PERC_ON_DUE_DAYS.equals(odChargeType) && prvRecovery != null) {
						if (!isEnqPurpose) {
							recoveryDAO.deleteUnpaid(finID, schdDate, odFor, "");
						}
						recovery = null;
					}
				}

				Date odDate = null;
				boolean isRecordSave = true;

				if (recovery == null) {

					recovery = new OverdueChargeRecovery();
					recovery.setFinReference(finReference);
					recovery.setFinODSchdDate(schdDate);
					recovery.setFinODFor(odFor);
					recovery.setODDays(DateUtil.getDaysBetween(valueDate, schdDate));
					odDate = schdDate;

					if (od.isODIncGrcDays()) {
						odDate = schdDate;
					} else {
						odDate = DateUtil.addDays(schdDate, od.getODGraceDays());
					}
					recovery.setPenaltyPaid(BigDecimal.ZERO);
					recovery.setPenaltyBal(BigDecimal.ZERO);

				} else {
					odDate = recovery.getMovementDate();
				}

				recovery.setSeqNo(seqNo);
				recovery.setMovementDate(valueDate);
				recovery.setODDays(DateUtil.getDaysBetween(valueDate, odDate));
				if (isAfterRecovery) {
					recovery.setMovementDate(DateUtil.addDays(valueDate, 1));
					recovery.setODDays(recovery.getODDays() + 1);
				}

				recovery.setFinCurODAmt(od.getFinCurODAmt());
				recovery.setFinCurODPri(od.getFinCurODPri());
				recovery.setFinCurODPft(od.getFinCurODPft());
				recovery.setPenaltyType(odChargeType);
				recovery.setPenaltyCalOn(od.getODChargeCalOn());
				recovery.setPenaltyAmtPerc(od.getODChargeAmtOrPerc());
				recovery.setMaxWaiver(od.getODMaxWaiverPerc());

				// Overdue Penalty Amount Calculation Depends on applied Charge Type
				switch (odChargeType) {
				case ChargeType.FLAT:
					recovery.setPenalty(od.getODChargeAmtOrPerc());
					break;
				case ChargeType.PERC_ONE_TIME:
					penatlyOnOneTime(od, recovery);
					break;
				case ChargeType.PERC_ON_DUE_DAYS:
					penaltyOnDueDays(od, valueDate, profitDayBasis, isAfterRecovery, recovery, odDate);
					break;
				default:
					break;
				}

				// Total Penalty Details Recalculation
				prvPenalty = recovery.getPenalty().add(prvPenalty);
				prvPenaltyBal = recovery.getPenalty().add(prvPenaltyBal);

				if (ChargeType.PERC_ON_DUE_DAYS.equals(odChargeType)) {
					recovery.setPenaltyBal(recovery.getPenalty().add(recovery.getPenaltyBal()));
					recovery.setPenaltyPaid(BigDecimal.ZERO);
					recovery.setWaivedAmt(BigDecimal.ZERO);
				} else {
					recovery.setPenaltyBal(recovery.getPenalty().subtract(recovery.getPenaltyPaid()));
				}

				recovery.setRcdCanDel(true);

				Date curBussDate = SysParamUtil.getAppValueDate();
				if (!isEnqPurpose && schdDate.compareTo(curBussDate) <= 0) {
					// Recovery Record Saving : Check to Add the Record/Not with "ZERO" penalty balance Amount while
					// Recovery calculation
					if (isRecordSave) {
						if (recovery.getPenaltyBal().compareTo(BigDecimal.ZERO) > 0
								&& recovery.getFinCurODAmt().compareTo(BigDecimal.ZERO) > 0) {
							recoveryDAO.save(recovery, "");
						}
					} else {
						recoveryDAO.update(recovery, "");
					}

					// Overdue Details Updation for Totals
					FinODDetails detail = new FinODDetails();
					detail.setFinReference(finReference);
					detail.setFinODSchdDate(schdDate);
					detail.setFinODFor(odFor);
					detail.setTotPenaltyAmt(prvPenalty);
					detail.setTotPenaltyPaid(BigDecimal.ZERO);
					detail.setTotPenaltyBal(prvPenaltyBal);
					detail.setTotWaived(BigDecimal.ZERO);

					if (resetTotals) {
						finODDetailsDAO.resetTotals(detail);
					} else {
						finODDetailsDAO.updateTotals(detail);
					}
				}
			}
		}

		return recovery;
	}

	private void penaltyOnDueDays(FinODDetails details, Date valueDate, String profitDayBasis, boolean isAfterRecovery,
			OverdueChargeRecovery recovery, Date odDate) {
		BigDecimal odCharge = details.getODChargeAmtOrPerc();
		switch (details.getODChargeCalOn()) {
		case FinanceConstants.ODCALON_SPFT:
			recovery.setPenalty(getDayPercValue(recovery.getFinCurODPft(), odCharge, odDate, valueDate, isAfterRecovery,
					profitDayBasis));
			break;
		case FinanceConstants.ODCALON_SPRI:
			recovery.setPenalty(getDayPercValue(recovery.getFinCurODPri(), odCharge, odDate, valueDate, isAfterRecovery,
					profitDayBasis));
			break;
		default:
			recovery.setPenalty(getDayPercValue(recovery.getFinCurODAmt(), odCharge, odDate, valueDate, isAfterRecovery,
					profitDayBasis));
			break;
		}
	}

	private void penatlyOnOneTime(FinODDetails details, OverdueChargeRecovery recovery) {
		BigDecimal odCharge = details.getODChargeAmtOrPerc();
		switch (details.getODChargeCalOn()) {
		case FinanceConstants.ODCALON_SPFT:
			recovery.setPenalty(getPercentageValue(recovery.getFinCurODPft(), odCharge));
			break;
		case FinanceConstants.ODCALON_SPRI:
			recovery.setPenalty(getPercentageValue(recovery.getFinCurODPri(), odCharge));
			break;
		default:
			recovery.setPenalty(getPercentageValue(recovery.getFinCurODAmt(), odCharge));
			break;
		}
	}

	private void prepareOverDueData(FinODDetails fod, Date valueDate, FinRepayQueue queue, boolean isAfterRecovery,
			boolean isSave) {

		fod.setFinID(queue.getFinID());
		fod.setFinReference(queue.getFinReference());
		fod.setFinODSchdDate(queue.getRpyDate());
		fod.setFinODFor(queue.getFinRpyFor());
		fod.setFinBranch(queue.getBranch());
		fod.setFinType(queue.getFinType());
		fod.setCustID(queue.getCustomerID());

		// Prepare Overdue Penalty rate Details & set to Finance Overdue Details
		List<FinODPenaltyRate> penaltyRates = finODPenaltyRateDAO.getFinODPenaltyRateByRef(queue.getFinID(), "");
		FinODPenaltyRate penaltyRate = PenaltyCalculator.getEffectiveRate(queue.getRpyDate(), penaltyRates);

		fod.setApplyODPenalty(penaltyRate.isApplyODPenalty());
		fod.setODIncGrcDays(penaltyRate.isODIncGrcDays());
		fod.setODChargeType(penaltyRate.getODChargeType());
		fod.setODChargeAmtOrPerc(penaltyRate.getODChargeAmtOrPerc());
		fod.setODChargeCalOn(penaltyRate.getODChargeCalOn());
		fod.setODGraceDays(penaltyRate.getODGraceDays());
		fod.setODAllowWaiver(penaltyRate.isODAllowWaiver());
		fod.setODMaxWaiverPerc(penaltyRate.getODMaxWaiverPerc());
		fod.setOdMinAmount(penaltyRate.getOdMinAmount());
		fod.setFinCurODAmt(queue.getSchdPft().add(queue.getSchdPri()).subtract(queue.getSchdPftPaid())
				.subtract(queue.getSchdPriPaid()));
		fod.setFinCurODPri(queue.getSchdPri().subtract(queue.getSchdPriPaid()));
		fod.setFinCurODPft(queue.getSchdPft().subtract(queue.getSchdPftPaid()));

		if (isSave) {
			fod.setFinMaxODAmt(fod.getFinCurODAmt());
			fod.setFinMaxODPri(fod.getFinCurODPri());
			fod.setFinMaxODPft(fod.getFinCurODPft());
			fod.setTotPenaltyAmt(BigDecimal.ZERO);
			fod.setTotWaived(BigDecimal.ZERO);
			fod.setTotPenaltyPaid(BigDecimal.ZERO);
			fod.setTotPenaltyBal(BigDecimal.ZERO);
		}

		if (fod.getTotPenaltyPaid().compareTo(BigDecimal.ZERO) == 0) {
			fod.setGraceDays(fod.getODGraceDays());
			fod.setIncGraceDays(fod.isODIncGrcDays());
		}

		fod.setFinODTillDate(valueDate);
		fod.setFinCurODDays(DateUtil.getDaysBetween(valueDate, fod.getFinODSchdDate()));

		if (isAfterRecovery) {
			fod.setFinCurODDays(fod.getFinCurODDays() + 1);
		}
		// TODO ###124902 - New field to be included for future use which stores the last payment date. This needs to be
		// worked.
		fod.setFinLMdfDate(SysParamUtil.getAppDate());
	}

	private BigDecimal getDayPercValue(BigDecimal odCalculatedBalance, BigDecimal odPercent, Date odEffectiveDate,
			Date dateValueDate, boolean isAfterRecovery, String profitDayBasis) {

		if (isAfterRecovery) {
			dateValueDate = DateUtil.addDays(dateValueDate, 1);
		}

		BigDecimal value = ((odCalculatedBalance.multiply(odPercent))
				.multiply(CalculationUtil.getInterestDays(odEffectiveDate, dateValueDate, profitDayBasis)))
				.divide(new BigDecimal(10000), RoundingMode.HALF_DOWN);

		return value.setScale(0, RoundingMode.HALF_DOWN);
	}

	private BigDecimal getPercentageValue(BigDecimal odCalculatedBalance, BigDecimal odPercent) {
		return (odCalculatedBalance.multiply(odPercent)).divide(new BigDecimal(10000), RoundingMode.HALF_DOWN);
	}

	/**
	 * Method for Posting OverDue Recoveries .
	 * 
	 * @param fm
	 * @param scheduleDetails
	 * @param financeProfitDetail
	 * @param valueDate
	 * @param curSchDate
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 */
	public List<Object> oDRPostingProcess(FinanceMain fm, Date valueDate, Date schdDate, String finODFor,
			Date movementDate, BigDecimal penalty, BigDecimal prvPenaltyPaid, BigDecimal waiverAmt, String chargeType,
			long linkedTranId, String finDivision)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {

		boolean isPostingSuccess = true;
		String errorCode = null;
		BigDecimal paidAmount = BigDecimal.ZERO;

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		// Calculate Pending Penalty Balance
		BigDecimal pendingPenalty = penalty.subtract(prvPenaltyPaid);

		if (pendingPenalty.compareTo(BigDecimal.ZERO) > 0) {

			// Get Finance Details From DB
			// financeMain = getFinanceMainDAO().getFinanceMainForBatch(finReference);
			boolean isPayNow = false;

			// ### 06-11-2015 End
			BigDecimal penaltyPaidNow = BigDecimal.ZERO;
			boolean isPaidClear = false;

			paidAmount = penaltyPaidNow;
			if (isPayNow) {
				// AmountCodes Preparation
				// EOD Repayments should pass the value date as schedule for which
				// Repayment is processing
				AEEvent aeEvent = new AEEvent();
				AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

				aeEvent.setFinID(fm.getFinID());
				aeEvent.setFinReference(fm.getFinReference());
				amountCodes.setPenalty(penaltyPaidNow);
				amountCodes.setWaiver(waiverAmt);
				aeEvent.setAccountingEvent(AccountingEvent.LATEPAY);
				aeEvent.setValueDate(valueDate);
				aeEvent.setSchdDate(schdDate);
				aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());

				/*
				 * String phase = SysParamUtil.getValueAsString("PHASE"); boolean isEODProcess = false; if
				 * (!"DAY".equals(phase)) { isEODProcess = true; }
				 */

				Date dateAppDate = SysParamUtil.getAppDate();
				aeEvent.setPostDate(dateAppDate);
				aeEvent.setValueDate(valueDate);

				aeEvent = postingsPreparationUtil.processPostingDetails(aeEvent);

				isPostingSuccess = aeEvent.isPostingSucess();
				linkedTranId = aeEvent.getLinkedTranId();
				errorCode = aeEvent.getErrorMessage();

				// Overdue Details Updation for Paid Penalty
				if (isPostingSuccess) {

					// Overdue Recovery Details Updation for Paid Amounts & Record Deletion Status
					doUpdateRecoveryData(finReference, schdDate, finODFor, movementDate, chargeType, penaltyPaidNow,
							waiverAmt, isPaidClear);

					// Overdue Details Updation for Totals
					FinODDetails fod = new FinODDetails();
					fod.setFinID(finID);
					fod.setFinReference(finReference);
					fod.setFinODSchdDate(schdDate);
					fod.setFinODFor(finODFor);
					fod.setTotPenaltyAmt(BigDecimal.ZERO);
					fod.setTotPenaltyPaid(penaltyPaidNow);
					fod.setTotPenaltyBal(penaltyPaidNow.negate());
					fod.setTotWaived(waiverAmt);
					finODDetailsDAO.updateTotals(fod);

				}
			}
		}

		List<Object> returnList = new ArrayList<Object>(2);
		returnList.add(isPostingSuccess);
		returnList.add(linkedTranId);
		returnList.add(errorCode);
		returnList.add(paidAmount);

		logger.debug("Leaving");
		return returnList;
	}

	private OverdueChargeRecovery doUpdateRecoveryData(String finReference, Date schdDate, String finODFor,
			Date movementDate, String chargeType, BigDecimal penaltyPaid, BigDecimal waiverPaid, boolean isPaidClear) {

		OverdueChargeRecovery recovery = new OverdueChargeRecovery();
		recovery.setFinReference(finReference);
		recovery.setFinODSchdDate(schdDate);
		recovery.setFinODFor(finODFor);
		recovery.setMovementDate(movementDate);
		recovery.setPenaltyPaid(penaltyPaid);
		recovery.setPenaltyBal(penaltyPaid);
		recovery.setWaivedAmt(waiverPaid);

		if ("D".equals(chargeType)) {
			recovery.setRcdCanDel(false);
		} else {
			recovery.setRcdCanDel(!isPaidClear);
		}

		recoveryDAO.updatePenaltyPaid(recovery, "");

		return recovery;
	}

	public BigDecimal getTDSAmount(BigDecimal amount) {
		if (StringUtils.isEmpty(TDS_ROUND_MODE) || TDS_PERCENTAGE.compareTo(BigDecimal.ZERO) == 0) {
			TDS_ROUND_MODE = SysParamUtil.getValue(CalculationConstants.TDS_ROUNDINGMODE).toString();
			TDS_ROUND_TRAGET = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);
			TDS_PERCENTAGE = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
		}

		BigDecimal netAmount = amount.multiply(TDS_PERCENTAGE.divide(HUNDRED));
		netAmount = CalculationUtil.roundAmount(netAmount, TDS_ROUND_MODE, TDS_ROUND_TRAGET);

		return netAmount;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public void setRecoveryDAO(OverdueChargeRecoveryDAO recoveryDAO) {
		this.recoveryDAO = recoveryDAO;
	}

	public void setFinODPenaltyRateDAO(FinODPenaltyRateDAO finODPenaltyRateDAO) {
		this.finODPenaltyRateDAO = finODPenaltyRateDAO;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public void setFinODAmzTaxDetailDAO(FinODAmzTaxDetailDAO finODAmzTaxDetailDAO) {
		this.finODAmzTaxDetailDAO = finODAmzTaxDetailDAO;
	}

	public void setAssignmentDAO(AssignmentDAO assignmentDAO) {
		this.assignmentDAO = assignmentDAO;
	}

	public void setAssignmentDealDAO(AssignmentDealDAO assignmentDealDAO) {
		this.assignmentDealDAO = assignmentDealDAO;
	}

	public void setGstInvoiceTxnService(GSTInvoiceTxnService gstInvoiceTxnService) {
		this.gstInvoiceTxnService = gstInvoiceTxnService;
	}

	public void setTaxHeaderDetailsDAO(TaxHeaderDetailsDAO taxHeaderDetailsDAO) {
		this.taxHeaderDetailsDAO = taxHeaderDetailsDAO;
	}

}
