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
import org.apache.log4j.Logger;

import com.pennant.Interface.model.IAccounts;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.backend.dao.applicationmaster.AssignmentDAO;
import com.pennant.backend.dao.applicationmaster.AssignmentDealDAO;
import com.pennant.backend.dao.finance.FinODAmzTaxDetailDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.TaxHeaderDetailsDAO;
import com.pennant.backend.dao.financemanagement.OverdueChargeRecoveryDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueueHeader;
import com.pennant.backend.model.applicationmaster.Assignment;
import com.pennant.backend.model.applicationmaster.AssignmentDealExcludedFee;
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
import com.pennant.cache.util.AccountingConfigCache;
import com.pennanttech.pennapps.core.InterfaceException;
import com.rits.cloning.Cloner;

public class OverDueRecoveryPostingsUtil implements Serializable {
	private static final long serialVersionUID = 6161809223570900644L;
	private static Logger logger = Logger.getLogger(OverDueRecoveryPostingsUtil.class);

	private FinanceMainDAO financeMainDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private OverdueChargeRecoveryDAO recoveryDAO;
	private FinODPenaltyRateDAO finODPenaltyRateDAO;
	private AccountInterfaceService accountInterfaceService;
	private PostingsPreparationUtil postingsPreparationUtil;
	private AssignmentDAO assignmentDAO;
	private AssignmentDealDAO assignmentDealDAO;
	private FinODAmzTaxDetailDAO finODAmzTaxDetailDAO;
	private FinanceTypeDAO financeTypeDAO;
	private GSTInvoiceTxnService gstInvoiceTxnService;
	private TaxHeaderDetailsDAO taxHeaderDetailsDAO;

	/**
	 * Default constructor
	 */
	public OverDueRecoveryPostingsUtil() {
		super();
	}

	/**
	 * Method for Posting OverDue Recoveries .
	 * 
	 * @param financeMain
	 * @param scheduleDetails
	 * @param financeProfitDetail
	 * @param dateValueDate
	 * @param curSchDate
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InterfaceException
	 */
	public List<Object> recoveryPayment(FinanceMain financeMain, Date dateValueDate, Date postDate,
			ManualAdviseMovements movement, Date movementDate, AEEvent aeEvent, FinRepayQueueHeader rpyQueueHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		// AmountCodes Preparation
		// EOD Repayments should pass the value date as schedule for which
		// Repayment is processing
		AEAmountCodes amountCodes = null;

		if (aeEvent == null) {
			aeEvent = new AEEvent();
			aeEvent.setAeAmountCodes(new AEAmountCodes());
			amountCodes = aeEvent.getAeAmountCodes();
			aeEvent.setFinReference(financeMain.getFinReference());
			aeEvent.setCustID(financeMain.getCustID());
			aeEvent.setFinType(financeMain.getFinType());
			aeEvent.setBranch(financeMain.getFinBranch());
			aeEvent.setCcy(financeMain.getFinCcy());
			aeEvent.setPostingUserBranch(rpyQueueHeader.getPostBranch());
			aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_LATEPAY);
			aeEvent.setValueDate(dateValueDate);
			aeEvent.setPostDate(postDate);
			aeEvent.setEntityCode(financeMain.getLovDescEntityCode());
			amountCodes.setPartnerBankAc(rpyQueueHeader.getPartnerBankAc());
			amountCodes.setPartnerBankAcType(rpyQueueHeader.getPartnerBankAcType());
			amountCodes.setFinType(financeMain.getFinType());

		} else {
			amountCodes = aeEvent.getAeAmountCodes();
		}

		amountCodes.setPenaltyPaid(movement.getPaidAmount());
		amountCodes.setPenaltyWaived(movement.getWaivedAmount());
		amountCodes.setPaymentType(rpyQueueHeader.getPayType());
		aeEvent.setPostRefId(rpyQueueHeader.getReceiptId());
		aeEvent.setPostingId(financeMain.getPostingId());
		aeEvent.setEOD(false);

		String phase = SysParamUtil.getValueAsString(PennantConstants.APP_PHASE);
		if (!phase.equals(PennantConstants.APP_PHASE_DAY)) {
			aeEvent.setEOD(true);
		}

		// LPP Receivable Amounts setting for Accounting difference
		FinTaxReceivable taxRcv = getFinODAmzTaxDetailDAO().getFinTaxReceivable(financeMain.getFinReference(), "LPP");
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
		if (financeMain.getAssignmentId() > 0) {
			Assignment assignment = assignmentDAO.getAssignment(financeMain.getAssignmentId(), "");
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

		//GST Waivers Details
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

		if (rpyQueueHeader.getProfit().compareTo(BigDecimal.ZERO) == 0
				&& rpyQueueHeader.getPrincipal().compareTo(BigDecimal.ZERO) == 0
				&& rpyQueueHeader.getTds().compareTo(BigDecimal.ZERO) == 0
				&& rpyQueueHeader.getLateProfit().compareTo(BigDecimal.ZERO) == 0
				&& rpyQueueHeader.getPenalty().compareTo(BigDecimal.ZERO) > 0) {

			if (rpyQueueHeader.getExtDataMap() != null) {
				dataMap.putAll(rpyQueueHeader.getExtDataMap());
			}
		}

		if (rpyQueueHeader.getGstExecutionMap() != null) {
			dataMap.putAll(rpyQueueHeader.getGstExecutionMap());
		}

		aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_LATEPAY);

		aeEvent.setDataMap(dataMap);
		aeEvent.getAcSetIDList().clear();

		if (StringUtils.isNotBlank(financeMain.getPromotionCode())
				&& (financeMain.getPromotionSeqId() != null && financeMain.getPromotionSeqId() == 0)) {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(financeMain.getPromotionCode(),
					aeEvent.getAccountingEvent(), FinanceConstants.MODULEID_PROMOTION));
		} else {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(financeMain.getFinType(),
					aeEvent.getAccountingEvent(), FinanceConstants.MODULEID_FINTYPE));
		}

		// Posting details calling
		aeEvent.setSimulateAccounting(financeMain.isSimulateAccounting());
		aeEvent = getPostingsPreparationUtil().postAccounting(aeEvent);
		aeEvent.getAeAmountCodes().setPenaltyPaid(BigDecimal.ZERO);
		aeEvent.getAeAmountCodes().setPenaltyWaived(BigDecimal.ZERO);

		// GST Invoice data resetting based on Accounting Process
		String isGSTInvOnDue = SysParamUtil.getValueAsString("GST_INV_ON_DUE");
		if (StringUtils.equals(isGSTInvOnDue, PennantConstants.YES)) {
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
					getFinODAmzTaxDetailDAO().updateTaxReceivable(taxRcv);
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
			financeDetail.getFinScheduleData().setFinanceMain(financeMain);

			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setLinkedTranId(aeEvent.getLinkedTranId());
			invoiceDetail.setFinanceDetail(financeDetail);
			invoiceDetail.setMovements(advMovements);
			invoiceDetail.setWaiver(false);
			invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_DEBIT);

			this.gstInvoiceTxnService.advTaxInvoicePreparation(invoiceDetail);

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
			finODAmzTaxDetailDAO.saveTaxIncome(taxIncome);

			returnList.add(taxIncome);

		} else {
			returnList.add(null);
		}

		if (financeMain.isSimulateAccounting()) {
			if (CollectionUtils.isNotEmpty(financeMain.getReturnDataSet())) {
				financeMain.getReturnDataSet().addAll(aeEvent.getReturnDataSet());
			} else {
				financeMain.setReturnDataSet(aeEvent.getReturnDataSet());
			}
		}

		// Method for Creating Invoice Details for the Waived amount against Created Due invoices
		prepareTaxMovement(rpyQueueHeader, financeMain, movement, aeEvent.getLinkedTranId());

		logger.debug("Leaving");
		return returnList;
	}

	/**
	 * Method for Preparing Credit Invoice details for the waived amount & Tax Movements saving
	 * 
	 * @param rpyQueueHeader
	 * @param financeMain
	 * @param advMov
	 * @param linkedTranID
	 */
	private void prepareTaxMovement(FinRepayQueueHeader rpyQueueHeader, FinanceMain financeMain,
			ManualAdviseMovements advMov, long linkedTranID) {

		// Prepare GST Details based on Invoice ID which was incomized
		List<OverdueTaxMovement> odTaxMovList = new ArrayList<>();
		List<FinODAmzTaxDetail> dueTaxList = finODAmzTaxDetailDAO.getODTaxList(financeMain.getFinReference());

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
				if (queue.getTaxHeader() != null) {
					Cloner cloner = new Cloner();
					TaxHeader taxHeader = cloner.deepClone(queue.getTaxHeader());
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
				if (lppPaid.compareTo(BigDecimal.ZERO) == 0 && lppWaived.compareTo(BigDecimal.ZERO) == 0) {
					break;
				}
			}
		}

		// If Amount was incomized on month end then waivers should be created as Credit invoice
		if (!odTaxMovList.isEmpty()) {
			if (rpyQueueHeader.isLppAmzReqonME()) {

				FinanceDetail financeDetail = new FinanceDetail();
				financeDetail.getFinScheduleData().setFinanceMain(financeMain);

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
					return DateUtility.compare(detail1.getValueDate(), detail2.getValueDate());
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

	/**
	 * Method for Preparation or Update of OverDue Details data
	 * 
	 * @param finRepayQueue
	 * @param scheduleDetail
	 * @param dateValueDate
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InterfaceException
	 */
	public FinODDetails overDueDetailPreparation(FinRepayQueue finRepayQueue, String profitDayBasis, Date dateValueDate,
			boolean isAfterRecovery, boolean isEnqPurpose)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		Date curBussDate = DateUtility.getAppValueDate();

		FinODDetails odDetails = getFinODDetailsDAO().getFinODDetailsForBatch(finRepayQueue.getFinReference(),
				finRepayQueue.getRpyDate());

		//Finance Overdue Details Save or Updation
		if (odDetails != null) {
			odDetails = prepareOverDueData(odDetails, dateValueDate, finRepayQueue, isAfterRecovery);
			if (!isEnqPurpose) {
				getFinODDetailsDAO().updateBatch(odDetails);
			}
		} else {
			odDetails = prepareOverDueData(odDetails, dateValueDate, finRepayQueue, isAfterRecovery);
			if (!isEnqPurpose && odDetails.getFinODSchdDate().compareTo(curBussDate) <= 0) {
				getFinODDetailsDAO().save(odDetails);
			}
		}

		logger.debug("Leaving");
		return odDetails;
	}

	/**
	 * Method for Preparation or Update of OverDue Details data
	 * 
	 * @param finRepayQueue
	 * @param pftDaysBasis
	 * @param dateValueDate
	 * @param isAfterRecovery
	 * @param isEnqPurpose
	 * @return
	 * @throws InterfaceException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<Object> recoveryCalculation(FinRepayQueue finRepayQueue, String pftDaysBasis, Date dateValueDate,
			boolean isAfterRecovery, boolean isEnqPurpose)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		List<Object> odObjDetails = new ArrayList<Object>();

		//Overdue Detail Calculation
		FinODDetails odDetails = overDueDetailPreparation(finRepayQueue, pftDaysBasis, dateValueDate, isAfterRecovery,
				isEnqPurpose);

		//Preparation for Overdue Penalty Recovery Details
		OverdueChargeRecovery rec = overdueRecoverCalculation(odDetails, dateValueDate, pftDaysBasis, isAfterRecovery,
				isEnqPurpose, false);

		odObjDetails.add(odDetails);
		odObjDetails.add(rec);

		logger.debug("Leaving");
		return odObjDetails;
	}

	/**
	 * Method for Preparation of Overdue Recovery Penalty Record
	 * 
	 * @param odDetails
	 * @param penaltyRate
	 * @param dateValueDate
	 * @param profitDayBasis
	 */
	private OverdueChargeRecovery overdueRecoverCalculation(FinODDetails odDetails, Date dateValueDate,
			String profitDayBasis, boolean isAfterRecovery, boolean isEnqPurpose, boolean doPostings) {
		logger.debug("Entering");

		Date newPenaltyDate = DateUtility.addDays(odDetails.getFinODSchdDate(), odDetails.getODGraceDays());

		OverdueChargeRecovery recovery = null;
		//Check Condition for Overdue Recovery Details Entries
		if (newPenaltyDate.compareTo(dateValueDate) <= 0) {

			boolean searchForPenalty = true;
			//Condition Checking for Overdue Penalty Exist or not
			if (!odDetails.isApplyODPenalty()) {
				searchForPenalty = false;
			} else {
				if (odDetails.getODChargeAmtOrPerc().compareTo(BigDecimal.ZERO) == 0) {
					searchForPenalty = false;
				}
			}

			//Delete Max Finance Effective Date Recovery Record if Overdue Payment
			//will not happen, only for Percentage on Due Days Charge type
			OverdueChargeRecovery prvRecovery = null;
			if (searchForPenalty) {

				prvRecovery = getRecoveryDAO().getMaxOverdueChargeRecoveryById(odDetails.getFinReference(),
						odDetails.getFinODSchdDate(), odDetails.getFinODFor(), "_AMView");

				if (FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS.equals(odDetails.getODChargeType())
						&& prvRecovery != null && prvRecovery.isRcdCanDel() && !isEnqPurpose) {
					getRecoveryDAO().deleteUnpaid(odDetails.getFinReference(), odDetails.getFinODSchdDate(),
							odDetails.getFinODFor(), "");
				}
			} else if (!searchForPenalty) {

				prvRecovery = getRecoveryDAO().getMaxOverdueChargeRecoveryById(odDetails.getFinReference(),
						odDetails.getFinODSchdDate(), odDetails.getFinODFor(), "_AMView");

				if (prvRecovery != null && prvRecovery.isRcdCanDel()) {
					if (!isEnqPurpose) {
						getRecoveryDAO().deleteUnpaid(odDetails.getFinReference(), odDetails.getFinODSchdDate(),
								odDetails.getFinODFor(), "");
					}

					BigDecimal prvPenalty = BigDecimal.ZERO.subtract(prvRecovery.getPenalty());
					BigDecimal prvPenaltyBal = BigDecimal.ZERO.subtract(prvRecovery.getPenaltyBal());

					//Overdue Details Updation for Totals
					FinODDetails detail = new FinODDetails();
					detail.setFinReference(odDetails.getFinReference());
					detail.setFinODSchdDate(odDetails.getFinODSchdDate());
					detail.setFinODFor(odDetails.getFinODFor());
					detail.setTotPenaltyAmt(prvPenalty);
					detail.setTotPenaltyPaid(BigDecimal.ZERO);
					detail.setTotPenaltyBal(prvPenaltyBal);
					detail.setTotWaived(BigDecimal.ZERO);

					if (!isEnqPurpose) {
						getFinODDetailsDAO().updateTotals(detail);
					}
				}
			}

			//Save/Update Overdue Recovery Details based upon Search Criteria
			if (searchForPenalty) {

				BigDecimal prvPenalty = BigDecimal.ZERO;
				BigDecimal prvPenaltyBal = BigDecimal.ZERO;

				if (FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS.equals(odDetails.getODChargeType())
						&& prvRecovery != null && prvRecovery.isRcdCanDel()) {

					String tableType = "_AMView";
					if (isEnqPurpose) {
						tableType = "_ATView";
					}

					recovery = getRecoveryDAO().getMaxOverdueChargeRecoveryById(odDetails.getFinReference(),
							odDetails.getFinODSchdDate(), odDetails.getFinODFor(), tableType);
				} else {
					recovery = prvRecovery;
				}

				boolean resetTotals = true;
				int seqNo = 1;
				//Stop calculation for paid penalty for Charge Type 'FLAT' & 'PERCONETIME'
				if (!FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS.equals(odDetails.getODChargeType())
						&& recovery != null && (recovery.getPenaltyPaid().compareTo(BigDecimal.ZERO) > 0
								|| recovery.getWaivedAmt().compareTo(BigDecimal.ZERO) > 0)) {

					return recovery;

				} else {

					//Store Previous values for Total Calculations
					if (recovery != null) {
						seqNo = recovery.getSeqNo() + 1;

						if (prvRecovery != null && prvRecovery.isRcdCanDel()) {
							prvPenalty = prvPenalty.subtract(prvRecovery.getPenalty());
							prvPenaltyBal = prvPenaltyBal.subtract(prvRecovery.getPenalty());
						}
						resetTotals = false;
					}

					if (!FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS.equals(odDetails.getODChargeType())
							&& prvRecovery != null) {
						if (!isEnqPurpose) {
							getRecoveryDAO().deleteUnpaid(odDetails.getFinReference(), odDetails.getFinODSchdDate(),
									odDetails.getFinODFor(), "");
						}
						recovery = null;
					}
				}

				Date finODDate = null;
				boolean isRecordSave = true;

				if (recovery == null) {

					recovery = new OverdueChargeRecovery();
					recovery.setFinReference(odDetails.getFinReference());
					recovery.setFinODSchdDate(odDetails.getFinODSchdDate());
					recovery.setFinODFor(odDetails.getFinODFor());
					recovery.setODDays(DateUtility.getDaysBetween(dateValueDate, odDetails.getFinODSchdDate()));
					finODDate = odDetails.getFinODSchdDate();

					if (odDetails.isODIncGrcDays()) {
						finODDate = odDetails.getFinODSchdDate();
					} else {
						finODDate = DateUtility.addDays(odDetails.getFinODSchdDate(), odDetails.getODGraceDays());
					}
					recovery.setPenaltyPaid(BigDecimal.ZERO);
					recovery.setPenaltyBal(BigDecimal.ZERO);

				} else {
					finODDate = recovery.getMovementDate();
				}

				recovery.setSeqNo(seqNo);
				recovery.setMovementDate(dateValueDate);
				recovery.setODDays(DateUtility.getDaysBetween(dateValueDate, finODDate));
				if (isAfterRecovery) {
					recovery.setMovementDate(DateUtility.addDays(dateValueDate, 1));
					recovery.setODDays(recovery.getODDays() + 1);
				}

				recovery.setFinCurODAmt(odDetails.getFinCurODAmt());
				recovery.setFinCurODPri(odDetails.getFinCurODPri());
				recovery.setFinCurODPft(odDetails.getFinCurODPft());
				recovery.setPenaltyType(odDetails.getODChargeType());
				recovery.setPenaltyCalOn(odDetails.getODChargeCalOn());
				recovery.setPenaltyAmtPerc(odDetails.getODChargeAmtOrPerc());
				recovery.setMaxWaiver(odDetails.getODMaxWaiverPerc());

				//Overdue Penalty Amount Calculation Depends on applied Charge Type
				if (FinanceConstants.PENALTYTYPE_FLAT.equals(odDetails.getODChargeType())) {

					recovery.setPenalty(odDetails.getODChargeAmtOrPerc());

				} else if (FinanceConstants.PENALTYTYPE_PERC_ONETIME.equals(odDetails.getODChargeType())) {

					if (odDetails.getODChargeCalOn().equals(FinanceConstants.ODCALON_SPFT)) {

						recovery.setPenalty(
								getPercentageValue(recovery.getFinCurODPft(), odDetails.getODChargeAmtOrPerc()));

					} else if (odDetails.getODChargeCalOn().equals(FinanceConstants.ODCALON_SPRI)) {

						recovery.setPenalty(
								getPercentageValue(recovery.getFinCurODPri(), odDetails.getODChargeAmtOrPerc()));

					} else {
						recovery.setPenalty(
								getPercentageValue(recovery.getFinCurODAmt(), odDetails.getODChargeAmtOrPerc()));
					}

				} else if (FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS.equals(odDetails.getODChargeType())) {

					if (odDetails.getODChargeCalOn().equals(FinanceConstants.ODCALON_SPFT)) {

						recovery.setPenalty(getDayPercValue(recovery.getFinCurODPft(), odDetails.getODChargeAmtOrPerc(),
								finODDate, dateValueDate, isAfterRecovery, profitDayBasis));

					} else if (odDetails.getODChargeCalOn().equals(FinanceConstants.ODCALON_SPRI)) {

						recovery.setPenalty(getDayPercValue(recovery.getFinCurODPri(), odDetails.getODChargeAmtOrPerc(),
								finODDate, dateValueDate, isAfterRecovery, profitDayBasis));

					} else {
						recovery.setPenalty(getDayPercValue(recovery.getFinCurODAmt(), odDetails.getODChargeAmtOrPerc(),
								finODDate, dateValueDate, isAfterRecovery, profitDayBasis));
					}
				}

				//Total Penalty Details Recalculation 
				prvPenalty = recovery.getPenalty().add(prvPenalty);
				prvPenaltyBal = recovery.getPenalty().add(prvPenaltyBal);

				if (FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS.equals(odDetails.getODChargeType())) {
					recovery.setPenaltyBal(recovery.getPenalty().add(recovery.getPenaltyBal()));
					recovery.setPenaltyPaid(BigDecimal.ZERO);
					recovery.setWaivedAmt(BigDecimal.ZERO);
				} else {
					recovery.setPenaltyBal(recovery.getPenalty().subtract(recovery.getPenaltyPaid()));
				}

				recovery.setRcdCanDel(true);

				Date curBussDate = DateUtility.getAppValueDate();
				if (!isEnqPurpose && odDetails.getFinODSchdDate().compareTo(curBussDate) <= 0) {
					//Recovery Record Saving : Check to Add the Record/Not with "ZERO" penalty balance Amount while Recovery calculation
					if (isRecordSave) {
						if (recovery.getPenaltyBal().compareTo(BigDecimal.ZERO) > 0
								&& recovery.getFinCurODAmt().compareTo(BigDecimal.ZERO) > 0) {
							getRecoveryDAO().save(recovery, "");
						}
					} else {
						getRecoveryDAO().update(recovery, "");
					}

					//Overdue Details Updation for Totals
					FinODDetails detail = new FinODDetails();
					detail.setFinReference(odDetails.getFinReference());
					detail.setFinODSchdDate(odDetails.getFinODSchdDate());
					detail.setFinODFor(odDetails.getFinODFor());
					detail.setTotPenaltyAmt(prvPenalty);
					detail.setTotPenaltyPaid(BigDecimal.ZERO);
					detail.setTotPenaltyBal(prvPenaltyBal);
					detail.setTotWaived(BigDecimal.ZERO);

					if (resetTotals) {
						getFinODDetailsDAO().resetTotals(detail);
					} else {
						getFinODDetailsDAO().updateTotals(detail);
					}
				}
			}
		}

		logger.debug("Leaving");
		return recovery;
	}

	/**
	 * Method for Preparing OverDue Details
	 * 
	 * @param odDetails
	 * @param scheduleDetail
	 * @param valueDate
	 * @param queue
	 * @return
	 */
	private FinODDetails prepareOverDueData(FinODDetails odDetails, Date valueDate, FinRepayQueue queue,
			boolean isAfterRecovery) {
		logger.debug("Entering");

		FinODDetails details = null;
		boolean isSave = false;
		if (odDetails != null) {
			details = odDetails;
		} else {

			isSave = true;
			details = new FinODDetails();
			details.setFinReference(queue.getFinReference());
			details.setFinODSchdDate(queue.getRpyDate());
			details.setFinODFor(queue.getFinRpyFor());
			details.setFinBranch(queue.getBranch());
			details.setFinType(queue.getFinType());
			details.setCustID(queue.getCustomerID());

			//Prepare Overdue Penalty rate Details & set to Finance Overdue Details
			FinODPenaltyRate penaltyRate = getFinODPenaltyRateDAO().getFinODPenaltyRateByRef(queue.getFinReference(),
					"");

			if (penaltyRate != null) {
				details.setApplyODPenalty(penaltyRate.isApplyODPenalty());
				details.setODIncGrcDays(penaltyRate.isODIncGrcDays());
				details.setODChargeType(penaltyRate.getODChargeType());
				details.setODChargeAmtOrPerc(penaltyRate.getODChargeAmtOrPerc());
				details.setODChargeCalOn(penaltyRate.getODChargeCalOn());
				details.setODGraceDays(penaltyRate.getODGraceDays());
				details.setODAllowWaiver(penaltyRate.isODAllowWaiver());
				details.setODMaxWaiverPerc(penaltyRate.getODMaxWaiverPerc());
			} else {
				details.setApplyODPenalty(false);
				details.setODIncGrcDays(false);
				details.setODChargeType("");
				details.setODChargeAmtOrPerc(BigDecimal.ZERO);
				details.setODChargeCalOn("");
				details.setODGraceDays(0);
				details.setODAllowWaiver(false);
				details.setODMaxWaiverPerc(BigDecimal.ZERO);
			}

		}

		details.setFinCurODAmt(queue.getSchdPft().add(queue.getSchdPri()).subtract(queue.getSchdPftPaid())
				.subtract(queue.getSchdPriPaid()));
		details.setFinCurODPri(queue.getSchdPri().subtract(queue.getSchdPriPaid()));
		details.setFinCurODPft(queue.getSchdPft().subtract(queue.getSchdPftPaid()));

		if (isSave) {
			details.setFinMaxODAmt(details.getFinCurODAmt());
			details.setFinMaxODPri(details.getFinCurODPri());
			details.setFinMaxODPft(details.getFinCurODPft());
			details.setTotPenaltyAmt(BigDecimal.ZERO);
			details.setTotWaived(BigDecimal.ZERO);
			details.setTotPenaltyPaid(BigDecimal.ZERO);
			details.setTotPenaltyBal(BigDecimal.ZERO);
		}

		if (details.getTotPenaltyPaid().compareTo(BigDecimal.ZERO) == 0) {
			details.setGraceDays(details.getODGraceDays());
			details.setIncGraceDays(details.isODIncGrcDays());
		}

		details.setFinODTillDate(valueDate);
		details.setFinCurODDays(DateUtility.getDaysBetween(valueDate, details.getFinODSchdDate()));
		if (isAfterRecovery) {
			details.setFinCurODDays(details.getFinCurODDays() + 1);
		}
		//TODO ###124902 - New field to be included for future use which stores the last payment date. This needs to be worked.
		details.setFinLMdfDate(DateUtility.getAppDate());

		logger.debug("Leaving");
		return details;
	}

	/**
	 * Method for get the Percentage of given value
	 * 
	 * @param odCalculatedBalance
	 * @param odPercent
	 * @return
	 */
	private BigDecimal getDayPercValue(BigDecimal odCalculatedBalance, BigDecimal odPercent, Date odEffectiveDate,
			Date dateValueDate, boolean isAfterRecovery, String profitDayBasis) {

		if (isAfterRecovery) {
			dateValueDate = DateUtility.addDays(dateValueDate, 1);
		}

		BigDecimal value = ((odCalculatedBalance.multiply(odPercent))
				.multiply(CalculationUtil.getInterestDays(odEffectiveDate, dateValueDate, profitDayBasis)))
						.divide(new BigDecimal(10000), RoundingMode.HALF_DOWN);

		return value.setScale(0, RoundingMode.HALF_DOWN);
	}

	/**
	 * Method for get the Percentage of given value
	 * 
	 * @param odCalculatedBalance
	 * @param odPercent
	 * @return
	 */
	private BigDecimal getPercentageValue(BigDecimal odCalculatedBalance, BigDecimal odPercent) {
		return (odCalculatedBalance.multiply(odPercent)).divide(new BigDecimal(10000), RoundingMode.HALF_DOWN);
	}

	/**
	 * Method for Posting OverDue Recoveries .
	 * 
	 * @param financeMain
	 * @param scheduleDetails
	 * @param financeProfitDetail
	 * @param dateValueDate
	 * @param curSchDate
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 */
	public List<Object> oDRPostingProcess(FinanceMain financeMain, Date dateValueDate, Date schdDate, String finODFor,
			Date movementDate, BigDecimal penalty, BigDecimal prvPenaltyPaid, BigDecimal waiverAmt, String chargeType,
			long linkedTranId, String finDivision)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {

		logger.debug("Entering" + "', financeMain='" + financeMain + "', dateValueDate='" + dateValueDate
				+ "', SchdDate='" + schdDate + "', finODFor='" + finODFor + "', movementDate='" + movementDate
				+ "', penalty='" + penalty + "', prvPenaltyPaid='" + prvPenaltyPaid + "', waiverAmt='" + waiverAmt
				+ "', chargeType='" + chargeType + "', linkedTranId='" + linkedTranId + "', finDivision='" + finDivision
				+ "'");
		boolean isPostingSuccess = true;
		String errorCode = null;
		BigDecimal paidAmount = BigDecimal.ZERO;

		String finReference = financeMain.getFinReference();
		//Calculate Pending Penalty Balance
		BigDecimal pendingPenalty = penalty.subtract(prvPenaltyPaid);

		if (pendingPenalty.compareTo(BigDecimal.ZERO) > 0) {

			//Get Finance Details From DB
			//financeMain = getFinanceMainDAO().getFinanceMainForBatch(finReference);
			boolean isPayNow = false;

			// Check Available Funding Account Balance
			//### 06-11-2015 Start - PSD Ticket ID 123992
			//As per the new approach in case of failure from equation., application should not stop the EOD process and should proceed to the next Repayments 
			IAccounts iAccount = null;
			try {
				iAccount = getAccountInterfaceService().fetchAccountAvailableBal(financeMain.getRepayAccountId());
			} catch (InterfaceException e) {
				logger.error("Exception: ", e);
				List<Object> returnList = new ArrayList<Object>(2);
				returnList.add(isPostingSuccess);
				returnList.add(linkedTranId);
				returnList.add(e.getErrorMessage());
				returnList.add(paidAmount.subtract(BigDecimal.ONE));
				return returnList;
			}
			//### 06-11-2015 End
			BigDecimal penaltyPaidNow = BigDecimal.ZERO;
			boolean isPaidClear = false;
			boolean accFound = false;

			// Account Type Check
			if ("TREASURY".equals(StringUtils.trimToEmpty(finDivision))) {
				String acType = SysParamUtil.getValueAsString("ALWFULLPAY_TSR_ACTYPE");

				String[] acTypeList = acType.split(",");
				for (int i = 0; i < acTypeList.length; i++) {
					if (iAccount.getAcType().equals(acTypeList[i].trim())) {
						accFound = true;
						break;
					}
				}
			} else {
				String acType = SysParamUtil.getValueAsString("ALWFULLPAY_NONTSR_ACTYPE");

				String[] acTypeList = acType.split(",");
				for (int i = 0; i < acTypeList.length; i++) {/*
																 * if(iAccount.getAcType().equals(acTypeList[i].trim()))
																 * { accFound = true; break; }
																 */
				}
			}

			// Set Requested Repayment Amount as RepayAmount Balance
			if (iAccount.getAcAvailableBal().compareTo(pendingPenalty) >= 0) {
				penaltyPaidNow = pendingPenalty;
				isPayNow = true;
				isPaidClear = true;
			} else if (accFound) {

				penaltyPaidNow = pendingPenalty;
				isPayNow = true;
				isPaidClear = true;

			} else {
				if (iAccount.getAcAvailableBal().compareTo(BigDecimal.ZERO) > 0) {
					penaltyPaidNow = iAccount.getAcAvailableBal();
					isPayNow = true;
				}
			}
			paidAmount = penaltyPaidNow;
			if (isPayNow) {
				// AmountCodes Preparation
				// EOD Repayments should pass the value date as schedule for which
				// Repayment is processing
				AEEvent aeEvent = new AEEvent();
				AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
				aeEvent.setFinReference(financeMain.getFinReference());
				amountCodes.setPenalty(penaltyPaidNow);
				amountCodes.setWaiver(waiverAmt);
				aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_LATEPAY);
				aeEvent.setValueDate(dateValueDate);
				aeEvent.setSchdDate(schdDate);
				aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());

				/*
				 * String phase = SysParamUtil.getValueAsString("PHASE"); boolean isEODProcess = false; if
				 * (!"DAY".equals(phase)) { isEODProcess = true; }
				 */

				Date dateAppDate = DateUtility.getAppDate();
				aeEvent.setPostDate(dateAppDate);
				aeEvent.setValueDate(dateValueDate);
				try {
					aeEvent = getPostingsPreparationUtil().processPostingDetails(aeEvent);
				} catch (AccountNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				isPostingSuccess = aeEvent.isPostingSucess();
				linkedTranId = aeEvent.getLinkedTranId();
				errorCode = aeEvent.getErrorMessage();

				//Overdue Details Updation for Paid Penalty
				if (isPostingSuccess) {

					//Overdue Recovery Details Updation for Paid Amounts & Record Deletion Status
					doUpdateRecoveryData(finReference, schdDate, finODFor, movementDate, chargeType, penaltyPaidNow,
							waiverAmt, isPaidClear);

					//Overdue Details Updation for Totals
					FinODDetails detail = new FinODDetails();
					detail.setFinReference(finReference);
					detail.setFinODSchdDate(schdDate);
					detail.setFinODFor(finODFor);
					detail.setTotPenaltyAmt(BigDecimal.ZERO);
					detail.setTotPenaltyPaid(penaltyPaidNow);
					detail.setTotPenaltyBal(penaltyPaidNow.negate());
					detail.setTotWaived(waiverAmt);
					getFinODDetailsDAO().updateTotals(detail);

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

	/**
	 * Method for Overdue Recovery Penalty Detail Updation
	 * 
	 * @param set
	 * @param isPaidClear
	 * @param penaltyPaid
	 * @param waiverPaid
	 * @param dbUpdate
	 * @return
	 */
	private OverdueChargeRecovery doUpdateRecoveryData(String finReference, Date schdDate, String finODFor,
			Date movementDate, String chargeType, BigDecimal penaltyPaid, BigDecimal waiverPaid, boolean isPaidClear) {
		logger.debug("Entering");

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
		getRecoveryDAO().updatePenaltyPaid(recovery, "");

		logger.debug("Leaving");
		return recovery;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public FinODDetailsDAO getFinODDetailsDAO() {
		return finODDetailsDAO;
	}

	public OverdueChargeRecoveryDAO getRecoveryDAO() {
		return recoveryDAO;
	}

	public void setRecoveryDAO(OverdueChargeRecoveryDAO recoveryDAO) {
		this.recoveryDAO = recoveryDAO;
	}

	public FinODPenaltyRateDAO getFinODPenaltyRateDAO() {
		return finODPenaltyRateDAO;
	}

	public void setFinODPenaltyRateDAO(FinODPenaltyRateDAO finODPenaltyRateDAO) {
		this.finODPenaltyRateDAO = finODPenaltyRateDAO;
	}

	public void setAccountInterfaceService(AccountInterfaceService accountInterfaceService) {
		this.accountInterfaceService = accountInterfaceService;
	}

	public AccountInterfaceService getAccountInterfaceService() {
		return accountInterfaceService;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public FinODAmzTaxDetailDAO getFinODAmzTaxDetailDAO() {
		return finODAmzTaxDetailDAO;
	}

	public void setFinODAmzTaxDetailDAO(FinODAmzTaxDetailDAO finODAmzTaxDetailDAO) {
		this.finODAmzTaxDetailDAO = finODAmzTaxDetailDAO;
	}

	public AssignmentDAO getAssignmentDAO() {
		return assignmentDAO;
	}

	public void setAssignmentDAO(AssignmentDAO assignmentDAO) {
		this.assignmentDAO = assignmentDAO;
	}

	public AssignmentDealDAO getAssignmentDealDAO() {
		return assignmentDealDAO;
	}

	public void setAssignmentDealDAO(AssignmentDealDAO assignmentDealDAO) {
		this.assignmentDealDAO = assignmentDealDAO;
	}

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public GSTInvoiceTxnService getGstInvoiceTxnService() {
		return gstInvoiceTxnService;
	}

	public void setGstInvoiceTxnService(GSTInvoiceTxnService gstInvoiceTxnService) {
		this.gstInvoiceTxnService = gstInvoiceTxnService;
	}

	public void setTaxHeaderDetailsDAO(TaxHeaderDetailsDAO taxHeaderDetailsDAO) {
		this.taxHeaderDetailsDAO = taxHeaderDetailsDAO;
	}

}
