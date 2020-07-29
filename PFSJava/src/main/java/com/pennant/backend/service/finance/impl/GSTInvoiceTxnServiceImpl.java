package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.applicationmaster.EntityDAO;
import com.pennant.backend.dao.customermasters.CustomerAddresDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.dao.finance.GSTInvoiceTxnDAO;
import com.pennant.backend.dao.systemmasters.ProvinceDAO;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.applicationmaster.TaxDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.GSTInvoiceTxn;
import com.pennant.backend.model.finance.GSTInvoiceTxnDetails;
import com.pennant.backend.model.finance.InvoiceDetail;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.finance.GSTInvoiceTxnService;
import com.pennant.backend.service.systemmasters.ProvinceService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.resource.Literal;

public class GSTInvoiceTxnServiceImpl implements GSTInvoiceTxnService {
	private static final Logger logger = LogManager.getLogger(GSTInvoiceTxnServiceImpl.class);

	private GSTInvoiceTxnDAO gstInvoiceTxnDAO;
	private EntityDAO entityDAO;
	private ProvinceService provinceService;
	private ProvinceDAO provinceDAO;
	private CustomerAddresDAO customerAddresDAO;
	private CustomerDAO customerDAO;
	private FinanceTaxDetailDAO financeTaxDetailDAO;
	private BranchDAO branchDAO;
	private FinanceMainDAO financeMainDAO;

	public GSTInvoiceTxnServiceImpl() {
		super();
	}

	@Override
	public Long feeTaxInvoicePreparation(InvoiceDetail invoiceDetail) {
		long linkedTranId = invoiceDetail.getLinkedTranId();
		String invoiceType = invoiceDetail.getInvoiceType();
		FinanceDetail financeDetail = invoiceDetail.getFinanceDetail();
		List<FinFeeDetail> finFeeDetailsList = invoiceDetail.getFinFeeDetailsList();
		boolean origination = invoiceDetail.isOrigination();
		boolean isWaiver = invoiceDetail.isWaiver();
		boolean dbInvSetReq = invoiceDetail.isDbInvSetReq();

		if (financeDetail == null || CollectionUtils.isEmpty(finFeeDetailsList)) {
			logger.warn("Fee Details not avilabe for the Linked Transaction ID  {} and Invoice Type {}", linkedTranId,
					invoiceType);
			return null;
		}

		GSTInvoiceTxn gstInvTxnHeader = getGSTTransaction(invoiceType, linkedTranId, financeDetail);
		if (gstInvTxnHeader == null) {
			logger.warn("GSTInvTxnHeader empty for the Linked Transaction ID  {} and Invoice Type {}", linkedTranId,
					invoiceType);
			return null;
		}

		BigDecimal invoiceAmout = BigDecimal.ZERO;
		List<GSTInvoiceTxnDetails> gstInvoiceTxnDetails = new ArrayList<GSTInvoiceTxnDetails>();

		// Invoice Transaction details preparation for Fee Details if any exists
		GSTInvoiceTxnDetails gstInvTxn = null;
		long dueInvoiceID = 0;
		for (FinFeeDetail feeDetail : finFeeDetailsList) {
			if (!feeDetail.isTaxApplicable() || StringUtils.isBlank(feeDetail.getFeeTypeCode())) {
				continue;
			}
			if (!origination && feeDetail.isOriginationFee()) {
				continue;
			}

			if (isWaiver) {
				if (BigDecimal.ZERO.compareTo(feeDetail.getWaivedAmount()) == 0) {
					continue;
				}
			} else {
				if (BigDecimal.ZERO.compareTo(feeDetail.getNetAmountOriginal()) == 0) {
					continue;
				}
			}

			TaxHeader taxHeader = feeDetail.getTaxHeader();
			if (taxHeader == null || CollectionUtils.isEmpty(taxHeader.getTaxDetails())) {
				continue;
			}
			Taxes cgstTax = new Taxes();
			Taxes sgstTax = new Taxes();
			Taxes igstTax = new Taxes();
			Taxes ugstTax = new Taxes();
			Taxes cessTax = new Taxes();

			List<Taxes> taxDetails = taxHeader.getTaxDetails();
			if (CollectionUtils.isNotEmpty(taxDetails)) {
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

			gstInvTxn = new GSTInvoiceTxnDetails();
			gstInvTxn.setFeeCode(feeDetail.getFeeTypeCode());
			gstInvTxn.setFeeDescription(feeDetail.getFeeTypeDesc());
			gstInvTxn.setCGST_RATE(cgstTax.getTaxPerc());
			gstInvTxn.setIGST_RATE(igstTax.getTaxPerc());
			gstInvTxn.setSGST_RATE(sgstTax.getTaxPerc());
			gstInvTxn.setUGST_RATE(ugstTax.getTaxPerc());
			gstInvTxn.setCESS_RATE(cessTax.getTaxPerc());

			if (isWaiver) {

				BigDecimal gstAmount = cgstTax.getWaivedTax().add(sgstTax.getWaivedTax()).add(igstTax.getWaivedTax())
						.add(ugstTax.getWaivedTax()).add(cessTax.getWaivedTax());
				if (gstAmount.compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}

				if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(feeDetail.getTaxComponent())) {
					gstInvTxn.setFeeAmount(feeDetail.getWaivedAmount().subtract(gstAmount)); //Fee Amount with out GST
				} else {
					gstInvTxn.setFeeAmount(feeDetail.getWaivedAmount()); //Fee Amount with out GST
				}

				gstInvTxn.setCGST_AMT(cgstTax.getWaivedTax());
				gstInvTxn.setSGST_AMT(sgstTax.getWaivedTax());
				gstInvTxn.setIGST_AMT(igstTax.getWaivedTax());
				gstInvTxn.setUGST_AMT(ugstTax.getWaivedTax());
				gstInvTxn.setCESS_AMT(cessTax.getWaivedTax());
				invoiceAmout = invoiceAmout.add(gstAmount);

			} else {

				BigDecimal gstAmount = BigDecimal.ZERO;
				if (origination || StringUtils.equals(RepayConstants.ALLOCATION_ODC, feeDetail.getFeeTypeCode())) {

					gstAmount = cgstTax.getNetTax().add(sgstTax.getNetTax()).add(igstTax.getNetTax())
							.add(ugstTax.getNetTax()).add(cessTax.getNetTax());
					if (gstAmount.compareTo(BigDecimal.ZERO) <= 0) {
						continue;
					}

					gstInvTxn.setFeeAmount(feeDetail.getNetAmountOriginal()); //Fee Amount with out GST
					gstInvTxn.setCGST_AMT(cgstTax.getNetTax());
					gstInvTxn.setSGST_AMT(sgstTax.getNetTax());
					gstInvTxn.setIGST_AMT(igstTax.getNetTax());
					gstInvTxn.setUGST_AMT(ugstTax.getNetTax());
					gstInvTxn.setCESS_AMT(cessTax.getNetTax());

				} else {

					gstAmount = cgstTax.getPaidTax().add(sgstTax.getPaidTax()).add(igstTax.getPaidTax())
							.add(ugstTax.getPaidTax()).add(cessTax.getPaidTax());
					if (gstAmount.compareTo(BigDecimal.ZERO) <= 0) {
						continue;
					}

					gstInvTxn.setFeeAmount(feeDetail.getPaidAmountOriginal());
					gstInvTxn.setCGST_AMT(cgstTax.getPaidTax());
					gstInvTxn.setSGST_AMT(sgstTax.getPaidTax());
					gstInvTxn.setIGST_AMT(igstTax.getPaidTax());
					gstInvTxn.setUGST_AMT(ugstTax.getPaidTax());
					gstInvTxn.setCESS_AMT(cessTax.getPaidTax());
				}
				invoiceAmout = invoiceAmout.add(gstAmount);
			}

			if (dbInvSetReq && taxHeader.getInvoiceID() != null) {
				dueInvoiceID = taxHeader.getInvoiceID();
			}

			gstInvoiceTxnDetails.add(gstInvTxn);
		}

		if (CollectionUtils.isEmpty(gstInvoiceTxnDetails)) {
			return null;
		}

		gstInvTxnHeader.setDueInvoiceId(dueInvoiceID);
		gstInvTxnHeader.setInvoice_Amt(invoiceAmout);
		gstInvTxnHeader.setGstInvoiceTxnDetailsList(gstInvoiceTxnDetails);

		Long invoiceId = this.gstInvoiceTxnDAO.save(gstInvTxnHeader);

		logger.debug(Literal.LEAVING);
		return invoiceId;
	}

	@Override
	public Long advTaxInvoicePreparation(InvoiceDetail invoiceDetail) {
		logger.debug(Literal.ENTERING);

		long linkedTranId = invoiceDetail.getLinkedTranId();
		FinanceDetail financeDetail = invoiceDetail.getFinanceDetail();

		List<ManualAdviseMovements> movements = invoiceDetail.getMovements();
		String invoiceType = invoiceDetail.getInvoiceType();
		boolean isWaiver = invoiceDetail.isWaiver();

		if (CollectionUtils.isEmpty(movements)) {
			logger.warn("Linked Transaction ID : " + linkedTranId + " & Invoice Type : " + invoiceType
					+ " --> Movement Entries are empty.");
			return null;
		}

		if (financeDetail == null) {
			logger.warn("Linked Transaction ID : " + linkedTranId + " & Invoice Type : " + invoiceType
					+ " --> Loan Details are empty.");
			return null;
		}

		GSTInvoiceTxn gstInvTxnHeader = getGSTTransaction(invoiceType, linkedTranId, financeDetail);
		if (gstInvTxnHeader == null) {
			logger.warn("Linked Transaction ID : " + linkedTranId + " & Invoice Type : " + invoiceType
					+ " --> GST transaction Details are empty.");
			return null;
		}

		BigDecimal invoiceAmout = BigDecimal.ZERO;
		List<GSTInvoiceTxnDetails> gstInvoiceTxnDetails = new ArrayList<GSTInvoiceTxnDetails>();
		Long dbInvoiceID = null;

		// Invoice Transaction details preparation for Fee Details if any exists
		for (ManualAdviseMovements movement : movements) {

			if (movement == null) {
				continue;
			}

			if (StringUtils.isBlank(movement.getFeeTypeCode())) {
				continue;
			}

			TaxHeader taxHeader = movement.getTaxHeader();
			if (taxHeader == null || CollectionUtils.isEmpty(taxHeader.getTaxDetails())) {
				continue;
			}

			Taxes cgstTax = new Taxes();
			Taxes sgstTax = new Taxes();
			Taxes igstTax = new Taxes();
			Taxes ugstTax = new Taxes();
			Taxes cessTax = new Taxes();
			List<Taxes> taxDetails = taxHeader.getTaxDetails();
			if (CollectionUtils.isNotEmpty(taxDetails)) {
				for (Taxes taxes : taxDetails) {
					switch (taxes.getTaxType()) {
					case RuleConstants.CODE_CGST:
						cgstTax = taxes;
						break;
					case RuleConstants.CODE_SGST:
						sgstTax = taxes;
						break;
					case RuleConstants.CODE_IGST:
						igstTax = taxes;
						break;
					case RuleConstants.CODE_UGST:
						ugstTax = taxes;
						break;
					case RuleConstants.CODE_CESS:
						cessTax = taxes;
						break;
					default:
						break;
					}
				}
			}

			GSTInvoiceTxnDetails gstInvTxn = new GSTInvoiceTxnDetails();
			gstInvTxn.setFeeCode(movement.getFeeTypeCode());
			gstInvTxn.setFeeDescription(movement.getFeeTypeDesc());
			gstInvTxn.setCGST_RATE(cgstTax.getTaxPerc());
			gstInvTxn.setSGST_RATE(sgstTax.getTaxPerc());
			gstInvTxn.setIGST_RATE(igstTax.getTaxPerc());
			gstInvTxn.setUGST_RATE(ugstTax.getTaxPerc());
			gstInvTxn.setCESS_RATE(cessTax.getTaxPerc());

			if (isWaiver) {
				BigDecimal gstAmount = cgstTax.getWaivedTax().add(sgstTax.getWaivedTax()).add(igstTax.getWaivedTax())
						.add(ugstTax.getWaivedTax()).add(cessTax.getWaivedTax());

				if (BigDecimal.ZERO.compareTo(movement.getWaivedAmount()) == 0
						|| BigDecimal.ZERO.compareTo(gstAmount) == 0) {
					continue;
				}

				if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(movement.getTaxComponent())) {
					gstInvTxn.setFeeAmount(movement.getWaivedAmount().subtract(gstAmount));
				} else {
					gstInvTxn.setFeeAmount(movement.getWaivedAmount());
				}

				gstInvTxn.setCGST_AMT(cgstTax.getWaivedTax());
				gstInvTxn.setSGST_AMT(sgstTax.getWaivedTax());
				gstInvTxn.setIGST_AMT(igstTax.getWaivedTax());
				gstInvTxn.setUGST_AMT(ugstTax.getWaivedTax());
				gstInvTxn.setCESS_AMT(cessTax.getWaivedTax());

				invoiceAmout = invoiceAmout.add(gstAmount);
			} else {

				BigDecimal gstAmount = cgstTax.getPaidTax().add(sgstTax.getPaidTax()).add(igstTax.getPaidTax())
						.add(ugstTax.getPaidTax()).add(cessTax.getPaidTax());

				if (BigDecimal.ZERO.compareTo(movement.getPaidAmount()) == 0
						|| BigDecimal.ZERO.compareTo(gstAmount) == 0) {
					continue;
				}

				if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(movement.getTaxComponent())) {
					gstInvTxn.setFeeAmount(movement.getPaidAmount().subtract(gstAmount)); //Fee Amount with out GST
				} else {
					gstInvTxn.setFeeAmount(movement.getPaidAmount()); //Fee Amount with out GST
				}

				gstInvTxn.setCGST_AMT(cgstTax.getPaidTax());
				gstInvTxn.setSGST_AMT(sgstTax.getPaidTax());
				gstInvTxn.setIGST_AMT(igstTax.getPaidTax());
				gstInvTxn.setUGST_AMT(ugstTax.getPaidTax());
				gstInvTxn.setCESS_AMT(cessTax.getPaidTax());

				invoiceAmout = invoiceAmout.add(gstAmount);
			}

			if (movement.getDebitInvoiceId() != null && movement.getDebitInvoiceId() > 0) {
				dbInvoiceID = movement.getDebitInvoiceId();
			}

			gstInvoiceTxnDetails.add(gstInvTxn);
		}

		if (CollectionUtils.isEmpty(gstInvoiceTxnDetails)) {
			return null;
		}

		gstInvTxnHeader.setDueInvoiceId(dbInvoiceID);
		gstInvTxnHeader.setInvoice_Amt(invoiceAmout);
		gstInvTxnHeader.setGstInvoiceTxnDetailsList(gstInvoiceTxnDetails);

		Long invoiceId = this.gstInvoiceTxnDAO.save(gstInvTxnHeader);

		logger.debug(Literal.LEAVING);
		return invoiceId;
	}

	@Override
	public Long schdDueTaxInovicePrepration(InvoiceDetail invoiceDetail) {
		logger.debug(Literal.ENTERING);

		long linkedTranId = invoiceDetail.getLinkedTranId();
		FinanceDetail financeDetail = invoiceDetail.getFinanceDetail();
		BigDecimal pftAmount = invoiceDetail.getPftAmount();
		BigDecimal priAmount = invoiceDetail.getPriAmount();
		BigDecimal fpriAmount = invoiceDetail.getFpriAmount();
		BigDecimal fpftAmount = invoiceDetail.getFpftAmount();
		String invoiceType = invoiceDetail.getInvoiceType();
		Long dbInvoiceID = invoiceDetail.getDbInvoiceID();

		String pftInvFeeCode = SysParamUtil.getValueAsString(PennantConstants.FEETYPE_PFT_EXEMPTED);
		String priInvFeeCode = SysParamUtil.getValueAsString(PennantConstants.FEETYPE_PRI_EXEMPTED);
		String fpftInvFeeCode = SysParamUtil.getValueAsString(PennantConstants.FEETYPE_FPFT_EXEMPTED);
		String fpriInvFeeCode = SysParamUtil.getValueAsString(PennantConstants.FEETYPE_FPRI_EXEMPTED);

		if (StringUtils.isBlank(pftInvFeeCode)) {
			logger.warn("Linked Transaction ID : " + linkedTranId + " & Invoice Type : " + invoiceType
					+ " --> Fee Code is empty.");
			return null;
		}
		if (StringUtils.isBlank(priInvFeeCode)) {
			logger.warn("Linked Transaction ID : " + linkedTranId + " & Invoice Type : " + invoiceType
					+ " --> Fee Code is empty.");
			return null;
		}
		if (StringUtils.isBlank(fpftInvFeeCode)) {
			logger.warn("Linked Transaction ID : " + linkedTranId + " & Invoice Type : " + invoiceType
					+ " --> Fee Code is empty.");
			return null;
		}
		if (StringUtils.isBlank(fpriInvFeeCode)) {
			logger.warn("Linked Transaction ID : " + linkedTranId + " & Invoice Type : " + invoiceType
					+ " --> Fee Code is empty.");
			return null;
		}

		if (financeDetail == null) {
			logger.warn("Linked Transaction ID : " + linkedTranId + " & Invoice Type : " + invoiceType
					+ " --> Loan Details are empty.");
			return null;
		}

		GSTInvoiceTxn gstInvTxnHeader = getGSTTransaction(invoiceType, linkedTranId, financeDetail);
		if (gstInvTxnHeader == null) {
			logger.warn("Linked Transaction ID : " + linkedTranId + " & Invoice Type : " + invoiceType
					+ " --> GST transaction Details are empty.");
			return null;
		}

		List<GSTInvoiceTxnDetails> gstInvoiceTxnDetails = new ArrayList<GSTInvoiceTxnDetails>();

		if (pftAmount.compareTo(BigDecimal.ZERO) > 0) {
			GSTInvoiceTxnDetails gstInvTxn = new GSTInvoiceTxnDetails();
			gstInvTxn.setFeeCode(pftInvFeeCode);
			gstInvTxn.setFeeDescription(pftInvFeeCode);
			gstInvTxn.setFeeAmount(pftAmount);
			gstInvTxn.setCGST_RATE(BigDecimal.ZERO);
			gstInvTxn.setIGST_RATE(BigDecimal.ZERO);
			gstInvTxn.setSGST_RATE(BigDecimal.ZERO);
			gstInvTxn.setUGST_RATE(BigDecimal.ZERO);
			gstInvTxn.setCESS_RATE(BigDecimal.ZERO);
			gstInvTxn.setCGST_AMT(BigDecimal.ZERO);
			gstInvTxn.setIGST_AMT(BigDecimal.ZERO);
			gstInvTxn.setSGST_AMT(BigDecimal.ZERO);
			gstInvTxn.setCESS_AMT(BigDecimal.ZERO);
			gstInvoiceTxnDetails.add(gstInvTxn);
		}

		if (priAmount.compareTo(BigDecimal.ZERO) > 0) {
			GSTInvoiceTxnDetails gstInvTxn = new GSTInvoiceTxnDetails();
			gstInvTxn.setFeeCode(priInvFeeCode);
			gstInvTxn.setFeeDescription(priInvFeeCode);
			gstInvTxn.setFeeAmount(priAmount);
			gstInvTxn.setCGST_RATE(BigDecimal.ZERO);
			gstInvTxn.setIGST_RATE(BigDecimal.ZERO);
			gstInvTxn.setSGST_RATE(BigDecimal.ZERO);
			gstInvTxn.setUGST_RATE(BigDecimal.ZERO);
			gstInvTxn.setCESS_RATE(BigDecimal.ZERO);
			gstInvTxn.setCGST_AMT(BigDecimal.ZERO);
			gstInvTxn.setIGST_AMT(BigDecimal.ZERO);
			gstInvTxn.setSGST_AMT(BigDecimal.ZERO);
			gstInvTxn.setCESS_AMT(BigDecimal.ZERO);
			gstInvoiceTxnDetails.add(gstInvTxn);
		}
		if (fpftAmount.compareTo(BigDecimal.ZERO) > 0) {
			GSTInvoiceTxnDetails gstInvTxn = new GSTInvoiceTxnDetails();
			gstInvTxn.setFeeCode(fpftInvFeeCode);
			gstInvTxn.setFeeDescription(fpftInvFeeCode);
			gstInvTxn.setFeeAmount(fpftAmount);
			gstInvTxn.setCGST_RATE(BigDecimal.ZERO);
			gstInvTxn.setIGST_RATE(BigDecimal.ZERO);
			gstInvTxn.setSGST_RATE(BigDecimal.ZERO);
			gstInvTxn.setUGST_RATE(BigDecimal.ZERO);
			gstInvTxn.setCESS_RATE(BigDecimal.ZERO);
			gstInvTxn.setCGST_AMT(BigDecimal.ZERO);
			gstInvTxn.setIGST_AMT(BigDecimal.ZERO);
			gstInvTxn.setSGST_AMT(BigDecimal.ZERO);
			gstInvTxn.setCESS_AMT(BigDecimal.ZERO);
			gstInvoiceTxnDetails.add(gstInvTxn);
		}

		if (fpriAmount.compareTo(BigDecimal.ZERO) > 0) {
			GSTInvoiceTxnDetails gstInvTxn = new GSTInvoiceTxnDetails();
			gstInvTxn.setFeeCode(fpriInvFeeCode);
			gstInvTxn.setFeeDescription(fpriInvFeeCode);
			gstInvTxn.setFeeAmount(fpriAmount);
			gstInvTxn.setCGST_RATE(BigDecimal.ZERO);
			gstInvTxn.setIGST_RATE(BigDecimal.ZERO);
			gstInvTxn.setSGST_RATE(BigDecimal.ZERO);
			gstInvTxn.setUGST_RATE(BigDecimal.ZERO);
			gstInvTxn.setCESS_RATE(BigDecimal.ZERO);
			gstInvTxn.setCGST_AMT(BigDecimal.ZERO);
			gstInvTxn.setIGST_AMT(BigDecimal.ZERO);
			gstInvTxn.setSGST_AMT(BigDecimal.ZERO);
			gstInvTxn.setCESS_AMT(BigDecimal.ZERO);
			gstInvoiceTxnDetails.add(gstInvTxn);
		}

		gstInvTxnHeader.setDueInvoiceId(dbInvoiceID);
		if (fpriAmount.compareTo(BigDecimal.ZERO) > 0) {
			gstInvTxnHeader.setInvoice_Amt(fpftAmount.add(fpriAmount));
		} else {
			gstInvTxnHeader.setInvoice_Amt(pftAmount.add(priAmount));
		}

		gstInvTxnHeader.setGstInvoiceTxnDetailsList(gstInvoiceTxnDetails);

		long invoiceId = this.gstInvoiceTxnDAO.save(gstInvTxnHeader);

		logger.debug(Literal.LEAVING);
		return invoiceId;
	}

	/**
	 * Method for Preparation of GST Transaction header details
	 * 
	 * @param invoiceType
	 * @param linkedTranId
	 * @param financeDetail
	 * @return
	 */
	private GSTInvoiceTxn getGSTTransaction(String invoiceType, long linkedTranId, FinanceDetail financeDetail) {
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();
		String finReference = financeMain.getFinReference();

		// Tax Details fetching
		if (financeDetail.getFinanceTaxDetail() == null) {
			financeDetail.setFinanceTaxDetail(financeTaxDetailDAO.getFinanceTaxDetail(finReference, "_AView"));
		}

		// Invoice Transaction Preparation
		GSTInvoiceTxn invoice = new GSTInvoiceTxn();
		invoice.setTransactionID(linkedTranId);
		invoice.setInvoiceType(invoiceType);
		invoice.setInvoice_Status(PennantConstants.GST_INVOICE_STATUS_INITIATED);

		if (financeMain.getEodValueDate() != null) {
			invoice.setInvoiceDate(financeMain.getEodValueDate());
		} else {
			invoice.setInvoiceDate(SysParamUtil.getAppDate()); //Need to confirm either it is system date or application date
		}

		Entity entity = null;
		if (StringUtils.isNotBlank(financeMain.getLovDescEntityCode())) {
			entity = this.entityDAO.getEntity(financeMain.getLovDescEntityCode(), "_AView");
		} else {
			if (financeType != null) {
				entity = this.entityDAO.getEntityByFinDivision(financeType.getFinDivision(), "_AView");
			} else {
				entity = this.entityDAO.getEntityByFinType(financeMain.getFinType(), "_AView");
			}
		}

		if (entity == null) {
			logger.warn("Linked Transaction ID : " + linkedTranId + " & Invoice Type : " + invoiceType
					+ " --> Entity Details are empty.");
			return null; // write this case as a error message
		}

		String entityCode = entity.getEntityCode();
		invoice.setCompanyCode(entityCode);
		invoice.setCompanyName(entity.getEntityDesc());
		invoice.setPanNumber(entity.getPANNumber());
		invoice.setLoanAccountNo(finReference);

		// Checking Finance Branch exist or not
		if (StringUtils.isBlank(financeMain.getFinBranch())) {
			String loanBranch = financeMainDAO.getFinBranch(finReference);
			if (StringUtils.isBlank(loanBranch)) {
				logger.warn("Fin Brance not avilabe for FinReference {}", finReference);

				logger.warn("Linked Transaction ID : " + linkedTranId + " & Invoice Type : " + invoiceType
						+ " --> Loan branch is empty.");
				return null; // write this case as a error message
			}
			financeMain.setFinBranch(loanBranch);
		}

		Branch fromBranch = branchDAO.getBranchById(financeMain.getFinBranch(), "_AView");

		if (fromBranch == null) {
			logger.warn("Linked Transaction ID : " + linkedTranId + " & Invoice Type : " + invoiceType
					+ " --> Branch details are Empty.");
			return null; // write this case as a error message
		}

		String branchCountry = fromBranch.getBranchCountry();
		String branchProvince = fromBranch.getBranchProvince();
		Province companyProvince = this.provinceService.getApprovedProvinceById(branchCountry, branchProvince);

		companyProvince = this.provinceService.getApprovedProvinceByEntityCode(branchCountry, branchProvince,
				entityCode);

		if (companyProvince != null) {
			String cpProvince = companyProvince.getCPProvince();
			String cpProvinceName = companyProvince.getCPProvinceName();
			if (StringUtils.isBlank(cpProvince) || StringUtils.isBlank(cpProvinceName)) {
				return null;
			}

			List<TaxDetail> taxDetailList = companyProvince.getTaxDetailList();
			if (CollectionUtils.isEmpty(taxDetailList)) {
				return null;
			}

			TaxDetail taxDetail = taxDetailList.get(0);

			invoice.setCompany_GSTIN(taxDetail.getTaxCode());
			invoice.setHsnNumber(taxDetail.getHsnNumber());
			invoice.setNatureService(taxDetail.getNatureService());

			String city = fromBranch.getLovDescBranchCityName();
			if (SysParamUtil.isAllowed(SMTParameterConstants.INVOICE_ADDRESS_ENTITY_BASIS)) {
				if (StringUtils.isBlank(cpProvince) || StringUtils.isBlank(cpProvinceName)) {
					return null; // write this case as a error message
				}

				TaxDetail taxDtl = taxDetailList.get(0);

				invoice.setCompany_State_Code(cpProvince);
				invoice.setCompany_State_Name(cpProvinceName);
				invoice.setCompany_Address1(taxDtl.getAddressLine1());
				invoice.setCompany_Address2(taxDtl.getAddressLine2());
				invoice.setCompany_Address3(getCommaSeperate(taxDtl.getAddressLine3(), city));
				invoice.setCompany_PINCode(taxDtl.getPinCode());

			} else {

				String hoseNumber = fromBranch.getBranchAddrHNbr();
				String flatNumber = fromBranch.getBranchFlatNbr();
				String street = fromBranch.getBranchAddrStreet();
				String addrLine2 = fromBranch.getBranchAddrLine2();

				invoice.setCompany_Address1(getCommaSeperate(hoseNumber, flatNumber, street));
				invoice.setCompany_Address2(fromBranch.getBranchAddrLine1());
				invoice.setCompany_Address3(getCommaSeperate(addrLine2, city));
				invoice.setCompany_PINCode(fromBranch.getPinCode());
				invoice.setCompany_State_Code(branchProvince);
				invoice.setCompany_State_Name(fromBranch.getLovDescBranchProvinceName());

			}
		}

		FinanceTaxDetail finTaxDetail = financeDetail.getFinanceTaxDetail();
		Province customerProvince = null;
		String country = "";
		String province = "";

		// If tax Details Exists on against Finance
		if (finTaxDetail != null && !PennantConstants.List_Select.equals(finTaxDetail.getApplicableFor())) {
			country = finTaxDetail.getCountry();
			province = finTaxDetail.getProvince();
			invoice.setCustomerID(finTaxDetail.getCustCIF());
			invoice.setCustomerName(finTaxDetail.getCustShrtName());
			invoice.setCustomerGSTIN(finTaxDetail.getTaxNumber());
			invoice.setCustomerAddress(getAddress(finTaxDetail));
		} else {
			CustomerAddres address = null;
			if (financeDetail.getCustomerDetails() != null) {
				List<CustomerAddres> addressList = financeDetail.getCustomerDetails().getAddressList();
				if (CollectionUtils.isNotEmpty(addressList)) {
					for (CustomerAddres customerAddres : addressList) {
						if (customerAddres.getCustAddrPriority() != Integer
								.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
							continue;
						}
						address = customerAddres;
						break;
					}
				}
			} else {
				address = customerAddresDAO.getHighPriorityCustAddr(financeMain.getCustID(), "_AView");
			}

			if (address == null) {
				logger.warn("Linked Transaction ID : " + linkedTranId + " & Invoice Type : " + invoiceType
						+ " --> Customer Address Details are Empty.");
				return null; // write this case as a error message
			}

			country = address.getCustAddrCountry();
			province = address.getCustAddrProvince();

			Customer cust = null;
			if (financeDetail.getCustomerDetails() != null
					&& financeDetail.getCustomerDetails().getCustomer() != null) {
				cust = financeDetail.getCustomerDetails().getCustomer();
			}
			if (cust == null) {
				cust = this.customerDAO.checkCustomerByID(financeMain.getCustID(), "");
			}

			if (cust == null) {
				logger.warn("Linked Transaction ID : " + linkedTranId + " & Invoice Type : " + invoiceType
						+ " --> Customer Details are Empty.");
				return null; // write this case as a error message
			}
			invoice.setCustomerID(cust.getCustCIF());
			invoice.setCustomerName(cust.getCustShrtName());

			//Preparing customer Address
			invoice.setCustomerAddress(prepareCustAddress(address));

		}

		customerProvince = this.provinceDAO.getProvinceById(country, province, "_AView");
		if (customerProvince == null) {
			logger.warn("Linked Transaction ID : " + linkedTranId + " & Invoice Type : " + invoiceType
					+ " --> Customer Address Province Details are Empty.");
			return null; // write this case as a error message
		}

		invoice.setCustomerStateCode(customerProvince.getTaxStateCode());
		invoice.setCustomerStateName(customerProvince.getCPProvinceName());

		return invoice;
	}

	private String getCommaSeperate(String... values) {
		StringBuilder address = new StringBuilder();

		for (String value : values) {
			value = StringUtils.trimToNull(value);

			if (value == null) {
				continue;
			}

			if (address.length() > 0) {
				address.append(", ");
			}

			address.append(value);
		}
		return address.toString();
	}

	private String getAddress(FinanceTaxDetail finTaxDetail) {
		String addrLine1 = StringUtils.trimToEmpty(finTaxDetail.getAddrLine1());
		String addrLine2 = StringUtils.trimToNull(finTaxDetail.getAddrLine2());
		String addrLine3 = StringUtils.trimToNull(finTaxDetail.getAddrLine3());
		String addrLine4 = StringUtils.trimToNull(finTaxDetail.getAddrLine4());
		String city = StringUtils.trimToNull(finTaxDetail.getCityName());
		String pinnCode = StringUtils.trimToNull(finTaxDetail.getPinCode());

		return getCommaSeperate(addrLine1, addrLine2, addrLine3, addrLine4, city, pinnCode);

	}

	/**
	 * Method for preparing Customer Address for Invoice Report Generation
	 * 
	 * @param gstInvoiceTxn
	 * @param customerAddres
	 * @return
	 */
	private String prepareCustAddress(CustomerAddres customerAddres) {
		String houseNumber = customerAddres.getCustAddrHNbr();
		String flanNumber = customerAddres.getCustFlatNbr();
		String street = customerAddres.getCustAddrStreet();
		String city = customerAddres.getLovDescCustAddrCityName();
		String province = customerAddres.getLovDescCustAddrProvinceName();
		String country = customerAddres.getLovDescCustAddrCountryName();
		String postBox = customerAddres.getCustPOBox();
		return getCommaSeperate(houseNumber, flanNumber, street, city, province, country, postBox);
	}

	public void setGstInvoiceTxnDAO(GSTInvoiceTxnDAO gstInvoiceTxnDAO) {
		this.gstInvoiceTxnDAO = gstInvoiceTxnDAO;
	}

	public void setBranchDAO(BranchDAO branchDAO) {
		this.branchDAO = branchDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setCustomerAddresDAO(CustomerAddresDAO customerAddresDAO) {
		this.customerAddresDAO = customerAddresDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public void setFinanceTaxDetailDAO(FinanceTaxDetailDAO financeTaxDetailDAO) {
		this.financeTaxDetailDAO = financeTaxDetailDAO;
	}

	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}

	public void setProvinceDAO(ProvinceDAO provinceDAO) {
		this.provinceDAO = provinceDAO;
	}

	public void setProvinceService(ProvinceService provinceService) {
		this.provinceService = provinceService;
	}
}
