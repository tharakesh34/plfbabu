package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.amtmasters.VehicleDealerDAO;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.applicationmaster.EntityDAO;
import com.pennant.backend.dao.customermasters.CustomerAddresDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.customermasters.GSTDetailDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.dao.finance.GSTInvoiceTxnDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.systemmasters.ProvinceDAO;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.applicationmaster.TaxDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.GSTDetail;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.AdviseDueTaxDetail;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.GSTInvoiceTxn;
import com.pennant.backend.model.finance.GSTInvoiceTxnDetails;
import com.pennant.backend.model.finance.InvoiceDetail;
import com.pennant.backend.model.finance.ManualAdvise;
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
import com.pennant.pff.accounting.model.PostingDTO;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.AccountingEvent;

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
	private VehicleDealerDAO vehicleDealerDAO;
	private GSTDetailDAO gstDetailDAO;
	private ManualAdviseDAO manualAdviseDAO;

	public GSTInvoiceTxnServiceImpl() {
		super();
	}

	@Override
	public Long feeTaxInvoicePreparation(InvoiceDetail invoiceDetail) {
		long linkedTranId = invoiceDetail.getLinkedTranId();
		String invoiceType = invoiceDetail.getInvoiceType();
		FinanceDetail financeDetail = invoiceDetail.getFinanceDetail();
		List<FinFeeDetail> feeList = invoiceDetail.getFinFeeDetailsList();
		boolean origination = invoiceDetail.isOrigination();
		boolean isWaiver = invoiceDetail.isWaiver();
		boolean dbInvSetReq = invoiceDetail.isDbInvSetReq();

		if (financeDetail == null || CollectionUtils.isEmpty(feeList)) {
			logger.warn("Fee Details not avilabe for the Linked Transaction ID  {} and Invoice Type {}", linkedTranId,
					invoiceType);
			return null;
		}

		GSTInvoiceTxn gstInvTxnHeader = null;
		if (!invoiceDetail.isSubventionFeeInv()) {
			gstInvTxnHeader = getGSTTransaction(invoiceType, linkedTranId, financeDetail);
		} else {
			gstInvTxnHeader = getGSTTransactionForDealer(invoiceType, linkedTranId, financeDetail);
		}

		if (gstInvTxnHeader == null) {
			logger.warn("GSTInvTxnHeader empty for the Linked Transaction ID  {} and Invoice Type {}", linkedTranId,
					invoiceType);
			return null;
		}

		BigDecimal invoiceAmout = BigDecimal.ZERO;
		List<GSTInvoiceTxnDetails> gstInvoiceTxnDetails = new ArrayList<>();

		// Invoice Transaction details preparation for Fee Details if any exists
		GSTInvoiceTxnDetails gstInvTxn = null;
		String subventionFeeCode = PennantConstants.FEETYPE_SUBVENTION;

		long dueInvoiceID = 0;
		for (FinFeeDetail fee : feeList) {

			if (!invoiceDetail.isSubventionFeeInv()) {
				if (subventionFeeCode.equals(fee.getFeeTypeCode())) {
					continue;
				}
			} else {
				if (!subventionFeeCode.equals(fee.getFeeTypeCode())) {
					continue;
				}
			}

			if (!fee.isTaxApplicable() || StringUtils.isBlank(fee.getFeeTypeCode())) {
				continue;
			}

			if (!origination && fee.isOriginationFee()) {
				continue;
			}

			if (isWaiver) {
				if (BigDecimal.ZERO.compareTo(fee.getWaivedAmount()) == 0) {
					continue;
				}
			} else {
				if (BigDecimal.ZERO.compareTo(fee.getNetAmountOriginal()) == 0) {
					continue;
				}
			}

			TaxHeader taxHeader = fee.getTaxHeader();
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

			gstInvTxn = new GSTInvoiceTxnDetails();
			gstInvTxn.setFeeCode(fee.getFeeTypeCode());
			gstInvTxn.setFeeDescription(fee.getFeeTypeDesc());
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

				if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(fee.getTaxComponent())) {
					gstInvTxn.setFeeAmount(fee.getWaivedAmount().subtract(gstAmount)); // Fee Amount with out GST
				} else {
					gstInvTxn.setFeeAmount(fee.getWaivedAmount()); // Fee Amount with out GST
				}

				gstInvTxn.setCGST_AMT(cgstTax.getWaivedTax());
				gstInvTxn.setSGST_AMT(sgstTax.getWaivedTax());
				gstInvTxn.setIGST_AMT(igstTax.getWaivedTax());
				gstInvTxn.setUGST_AMT(ugstTax.getWaivedTax());
				gstInvTxn.setCESS_AMT(cessTax.getWaivedTax());
				invoiceAmout = invoiceAmout.add(gstAmount);

			} else {

				BigDecimal gstAmount = BigDecimal.ZERO;
				if (origination || RepayConstants.ALLOCATION_ODC.equals(fee.getFeeTypeCode())
						|| AccountingEvent.RESTRUCTURE.equals(fee.getFinEvent())) {
					if (fee.isPaidFromLoanApproval()) {
						BigDecimal netGstAmt = cgstTax.getNetTax().add(sgstTax.getNetTax()).add(igstTax.getNetTax())
								.add(ugstTax.getNetTax()).add(cessTax.getNetTax());
						BigDecimal paidGstAmt = cgstTax.getPaidTax().add(sgstTax.getPaidTax()).add(igstTax.getPaidTax())
								.add(ugstTax.getPaidTax()).add(cessTax.getPaidTax());
						gstAmount = netGstAmt.subtract(paidGstAmt);

						if (gstAmount.compareTo(BigDecimal.ZERO) <= 0) {
							continue;
						}

						/* Fee Amount with out GST */
						gstInvTxn.setFeeAmount(fee.getNetAmountOriginal().subtract(fee.getPaidAmountOriginal()));
						gstInvTxn.setCGST_AMT(cgstTax.getNetTax().subtract(cgstTax.getPaidTax()));
						gstInvTxn.setSGST_AMT(sgstTax.getNetTax().subtract(sgstTax.getPaidTax()));
						gstInvTxn.setIGST_AMT(igstTax.getNetTax().subtract(igstTax.getPaidTax()));
						gstInvTxn.setUGST_AMT(ugstTax.getNetTax().subtract(ugstTax.getPaidTax()));
						gstInvTxn.setCESS_AMT(cessTax.getNetTax());

					} else {
						gstAmount = cgstTax.getNetTax().add(sgstTax.getNetTax()).add(igstTax.getNetTax())
								.add(ugstTax.getNetTax()).add(cessTax.getNetTax());
						if (gstAmount.compareTo(BigDecimal.ZERO) <= 0) {
							continue;
						}

						/* Fee Amount with out GST */
						gstInvTxn.setFeeAmount(fee.getNetAmountOriginal());
						gstInvTxn.setCGST_AMT(cgstTax.getNetTax());
						gstInvTxn.setSGST_AMT(sgstTax.getNetTax());
						gstInvTxn.setIGST_AMT(igstTax.getNetTax());
						gstInvTxn.setUGST_AMT(ugstTax.getNetTax());
						gstInvTxn.setCESS_AMT(cessTax.getNetTax());
					}
				} else {

					FinFeeDetail befImage = fee.getBefImage();
					if (befImage != null && befImage.getRemainingFee().compareTo(fee.getPaidAmount()) == 0
							&& fee.getTaxHeaderId() != null
							&& (befImage.getRemainingFeeGST().compareTo(fee.getPaidAmountGST()) != 0)) {

						gstAmount = cgstTax.getRemFeeTax().add(sgstTax.getRemFeeTax()).add(igstTax.getRemFeeTax())
								.add(ugstTax.getRemFeeTax()).add(cessTax.getRemFeeTax());

						if (gstAmount.compareTo(BigDecimal.ZERO) <= 0) {
							continue;
						}

						gstInvTxn.setCGST_AMT(cgstTax.getRemFeeTax());
						gstInvTxn.setSGST_AMT(sgstTax.getRemFeeTax());
						gstInvTxn.setIGST_AMT(igstTax.getRemFeeTax());
						gstInvTxn.setUGST_AMT(ugstTax.getRemFeeTax());
						gstInvTxn.setCESS_AMT(cessTax.getRemFeeTax());
						gstInvTxn.setFeeAmount(fee.getPaidAmount().add(fee.getPaidTDS()).subtract(gstAmount));

					} else {
						gstAmount = cgstTax.getPaidTax().add(sgstTax.getPaidTax()).add(igstTax.getPaidTax())
								.add(ugstTax.getPaidTax()).add(cessTax.getPaidTax());

						if (gstAmount.compareTo(BigDecimal.ZERO) <= 0) {
							continue;
						}

						gstInvTxn.setFeeAmount(fee.getPaidAmountOriginal());
						gstInvTxn.setCGST_AMT(cgstTax.getPaidTax());
						gstInvTxn.setSGST_AMT(sgstTax.getPaidTax());
						gstInvTxn.setIGST_AMT(igstTax.getPaidTax());
						gstInvTxn.setUGST_AMT(ugstTax.getPaidTax());
						gstInvTxn.setCESS_AMT(cessTax.getPaidTax());
					}
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
					gstInvTxn.setFeeAmount(movement.getPaidAmount().subtract(gstAmount)); // Fee Amount with out GST
				} else {
					gstInvTxn.setFeeAmount(movement.getPaidAmount()); // Fee Amount with out GST
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
		long linkedTranId = invoiceDetail.getLinkedTranId();
		FinanceDetail financeDetail = invoiceDetail.getFinanceDetail();
		BigDecimal pftAmount = invoiceDetail.getPftAmount();
		BigDecimal priAmount = invoiceDetail.getPriAmount();
		BigDecimal fpriAmount = invoiceDetail.getFpriAmount();
		BigDecimal fpftAmount = invoiceDetail.getFpftAmount();
		String invoiceType = invoiceDetail.getInvoiceType();
		Long dbInvoiceID = invoiceDetail.getDbInvoiceID();
		EventProperties eventProperties = invoiceDetail.getEventProperties();

		String pftInvFeeCode = null;
		String priInvFeeCode = null;
		String fpftInvFeeCode = null;
		String fpriInvFeeCode = null;

		if (eventProperties.isParameterLoaded()) {
			pftInvFeeCode = eventProperties.getPftInvFeeCode();
			priInvFeeCode = eventProperties.getPriInvFeeCode();
			fpftInvFeeCode = eventProperties.getFpftInvFeeCode();
			fpriInvFeeCode = eventProperties.getFpriInvFeeCode();
		} else {
			pftInvFeeCode = SysParamUtil.getValueAsString(PennantConstants.FEETYPE_PFT_EXEMPTED);
			priInvFeeCode = SysParamUtil.getValueAsString(PennantConstants.FEETYPE_PRI_EXEMPTED);
			fpftInvFeeCode = SysParamUtil.getValueAsString(PennantConstants.FEETYPE_FPFT_EXEMPTED);
			fpriInvFeeCode = SysParamUtil.getValueAsString(PennantConstants.FEETYPE_FPRI_EXEMPTED);
		}

		if (financeDetail == null) {
			logger.warn("Linked Transaction ID : " + linkedTranId + " & Invoice Type : " + invoiceType
					+ " --> Loan Details are empty.");
			return null;
		}

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

		return invoiceId;
	}

	/**
	 * Method for Preparation of GST Transaction header details
	 * 
	 * @param invoiceType
	 * @param linkedTranId
	 * @param fd
	 * @return
	 */
	private GSTInvoiceTxn getGSTTransaction(String invoiceType, long linkedTranId, FinanceDetail fd) {
		FinScheduleData fsd = fd.getFinScheduleData();
		FinanceMain fm = fsd.getFinanceMain();
		FinanceType financeType = fsd.getFinanceType();
		long finID = fm.getFinID();
		String finReference = fm.getFinReference();
		EventProperties eventProperties = fm.getEventProperties();

		// Tax Details fetching
		if (fd.getFinanceTaxDetail() == null) {
			fd.setFinanceTaxDetail(financeTaxDetailDAO.getFinanceTaxDetail(finID, "_AView"));
		}

		// Invoice Transaction Preparation
		GSTInvoiceTxn invoice = new GSTInvoiceTxn();
		invoice.setTransactionID(linkedTranId);
		invoice.setInvoiceType(invoiceType);
		invoice.setInvoice_Status(PennantConstants.GST_INVOICE_STATUS_INITIATED);
		invoice.setInvoiceFor("C");

		if (fm.getEodValueDate() != null) {
			invoice.setInvoiceDate(fm.getEodValueDate());
		} else {
			/* Need to confirm either it is system date or application date */
			invoice.setInvoiceDate(SysParamUtil.getAppDate());
		}

		Entity entity = null;
		if (StringUtils.isNotBlank(fm.getLovDescEntityCode())) {
			entity = this.entityDAO.getEntity(fm.getLovDescEntityCode(), "_AView");
		} else {
			if (financeType != null) {
				entity = this.entityDAO.getEntityByFinDivision(financeType.getFinDivision(), "_AView");
			} else {
				entity = this.entityDAO.getEntityByFinType(fm.getFinType(), "_AView");
			}
		}

		if (entity == null) {
			logger.warn("Linked Transaction ID {} & Invoice Type {} Entity Details are empty.", linkedTranId,
					invoiceType);
			return null; // FIXME write this case as a error message
		}

		String entityCode = entity.getEntityCode();
		invoice.setCompanyCode(entityCode);
		invoice.setCompanyName(entity.getEntityDesc());
		invoice.setPanNumber(entity.getPANNumber());
		invoice.setLoanAccountNo(finReference);

		// Checking Finance Branch exist or not
		if (StringUtils.isBlank(fm.getFinBranch())) {
			String loanBranch = financeMainDAO.getFinBranch(finID);
			if (StringUtils.isBlank(loanBranch)) {
				logger.warn("Fin Branch not avilabe for FinReference {}", finReference);

				logger.warn("Linked Transaction ID {} & Invoice Type {} Loan branch is empty.", linkedTranId,
						invoiceType);
				return null; // FIXME write this case as a error message
			}
			fm.setFinBranch(loanBranch);
		}

		Branch fromBranch = branchDAO.getBranchById(fm.getFinBranch(), "_AView");

		if (SysParamUtil.isAllowed(SMTParameterConstants.GST_DEFAULT_FROM_STATE)) {
			String defaultFinBranch = SysParamUtil.getValueAsString(SMTParameterConstants.GST_DEFAULT_STATE_CODE);
			fromBranch = branchDAO.getBranchById(defaultFinBranch, "");
		}

		if (fromBranch == null) {
			logger.warn("Linked Transaction ID {}  & Invoice Type {} Branch details are Empty.", linkedTranId,
					invoiceType);
			return null; // FIXME write this case as a error message
		}

		String branchCountry = fromBranch.getBranchCountry();
		String branchProvince = fromBranch.getBranchProvince();

		Province companyProvince = this.provinceService.getApprovedProvinceByEntityCode(branchCountry, branchProvince,
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

			boolean invAddrEntityBasis = false;
			if (eventProperties.isParameterLoaded()) {
				invAddrEntityBasis = eventProperties.isInvAddrEntityBasis();
			} else {
				invAddrEntityBasis = SysParamUtil.isAllowed(SMTParameterConstants.INVOICE_ADDRESS_ENTITY_BASIS);
			}

			if (invAddrEntityBasis) {
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

		FinanceTaxDetail finTaxDetail = fd.getFinanceTaxDetail();
		GSTDetail gstDetail = gstDetailDAO.getDefaultGSTDetailById(fm.getCustID(), "_AView");

		Province customerProvince = null;
		String country = "";
		String province = "";

		// If tax Details Exists on against Finance
		if (finTaxDetail != null && finTaxDetail.getApplicableFor() != null
				&& !PennantConstants.List_Select.equals(finTaxDetail.getApplicableFor())) {
			if (StringUtils.isBlank(finTaxDetail.getCustShrtName()) && fd.getCustomerDetails() != null) {
				finTaxDetail.setCustShrtName(fd.getCustomerDetails().getCustomer().getCustShrtName());
			}
			country = finTaxDetail.getCountry();
			province = finTaxDetail.getProvince();
			invoice.setCustomerID(finTaxDetail.getCustCIF());
			invoice.setCustomerName(finTaxDetail.getCustShrtName());
			invoice.setCustomerGSTIN(finTaxDetail.getTaxNumber());
			invoice.setCustomerAddress(getAddress(finTaxDetail));
		} else {
			if (gstDetail != null) {
				country = gstDetail.getCountryCode();
				province = gstDetail.getStateCode();
				invoice.setCustomerID(gstDetail.getCustCIF());
				invoice.setCustomerName(gstDetail.getCustShrtName());
				invoice.setCustomerGSTIN(gstDetail.getGstNumber());
				invoice.setCustomerAddress(getAddress(gstDetail));
			} else {
				CustomerAddres address = null;
				if (fd.getCustomerDetails() != null && fd.getCustomerDetails().getAddressList() != null) {
					List<CustomerAddres> addresses = fd.getCustomerDetails().getAddressList();
					if (CollectionUtils.isNotEmpty(addresses)) {
						for (CustomerAddres ca : addresses) {
							if (ca.getCustAddrPriority() != Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
								continue;
							}
							address = ca;
							break;
						}
					} else {
						address = customerAddresDAO.getHighPriorityCustAddr(fm.getCustID(), "_AView");
					}
				} else {
					address = customerAddresDAO.getHighPriorityCustAddr(fm.getCustID(), "_AView");
				}

				if (address == null) {
					logger.warn("Linked Transaction ID : " + linkedTranId + " & Invoice Type : " + invoiceType
							+ " --> Customer Address Details are Empty.");
					return null; // write this case as a error message
				}

				country = address.getCustAddrCountry();
				province = address.getCustAddrProvince();

				Customer cust = null;
				if (fd.getCustomerDetails() != null && fd.getCustomerDetails().getCustomer() != null) {
					cust = fd.getCustomerDetails().getCustomer();
				}
				if (cust == null) {
					cust = this.customerDAO.checkCustomerByID(fm.getCustID(), "");
				}

				if (cust == null) {
					logger.warn("Linked Transaction ID : " + linkedTranId + " & Invoice Type : " + invoiceType
							+ " --> Customer Details are Empty.");
					return null; // write this case as a error message
				}
				invoice.setCustomerID(cust.getCustCIF());
				invoice.setCustomerName(cust.getCustShrtName());

				// Preparing customer Address
				invoice.setCustomerAddress(prepareCustAddress(address));
			}
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

	private GSTInvoiceTxn getGSTTransactionForDealer(String invoiceType, long linkedTranId, FinanceDetail fd) {
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		FinanceType financeType = schdData.getFinanceType();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		// Invoice Transaction Preparation
		GSTInvoiceTxn invoice = new GSTInvoiceTxn();
		invoice.setTransactionID(linkedTranId);
		invoice.setInvoiceType(invoiceType);
		invoice.setInvoice_Status(PennantConstants.GST_INVOICE_STATUS_INITIATED);

		switch (fm.getSubVentionFrom()) {
		case FinanceConstants.SUBVN_FROM_DEALER:
			invoice.setInvoiceFor("D");
			break;
		case FinanceConstants.SUBVN_FROM_MANUFACTURER:
			invoice.setInvoiceFor("M");
			break;
		default:
			break;
		}

		if (fm.getEodValueDate() != null) {
			invoice.setInvoiceDate(fm.getEodValueDate());
		} else {
			/* Need to confirm either it is system date or application date */
			invoice.setInvoiceDate(SysParamUtil.getAppDate());
		}

		Entity entity = null;
		String lovDescEntityCode = fm.getLovDescEntityCode();

		if (StringUtils.isNotBlank(lovDescEntityCode)) {
			entity = this.entityDAO.getEntity(lovDescEntityCode, "_AView");
		} else {
			if (financeType != null) {
				entity = this.entityDAO.getEntityByFinDivision(financeType.getFinDivision(), "_AView");
			} else {
				entity = this.entityDAO.getEntityByFinType(fm.getFinType(), "_AView");
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
		if (StringUtils.isBlank(fm.getFinBranch())) {
			String loanBranch = financeMainDAO.getFinBranch(finID);
			if (StringUtils.isBlank(loanBranch)) {
				logger.warn("Fin Brance not avilabe for FinReference {}", finReference);

				logger.warn("Linked Transaction ID : " + linkedTranId + " & Invoice Type : " + invoiceType
						+ " --> Loan branch is empty.");
				return null; // write this case as a error message
			}
			fm.setFinBranch(loanBranch);
		}

		Branch fromBranch = branchDAO.getBranchById(fm.getFinBranch(), "_AView");

		if (fromBranch == null) {
			logger.warn("Linked Transaction ID : " + linkedTranId + " & Invoice Type : " + invoiceType
					+ " --> Branch details are Empty.");
			return null; // write this case as a error message
		}

		String branchCountry = fromBranch.getBranchCountry();
		String branchProvince = fromBranch.getBranchProvince();

		Province companyProvince = this.provinceService.getApprovedProvinceByEntityCode(branchCountry, branchProvince,
				entityCode);

		if (companyProvince != null) {
			String cpProvince = companyProvince.getTaxStateCode();

			if (StringUtils.isBlank(cpProvince)) {
				cpProvince = companyProvince.getCPProvince();
			}

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

		// FinanceTaxDetail finTaxDetail = financeDetail.getFinanceTaxDetail();
		Province customerProvince = null;
		String country = "";
		String province = "";

		// If tax Details Exists on against Finance
		/*
		 * if (finTaxDetail != null && !PennantConstants.List_Select.equals(finTaxDetail.getApplicableFor()) &&
		 * StringUtils.isNotBlank(finTaxDetail.getApplicableFor())) { country = finTaxDetail.getCountry(); province =
		 * finTaxDetail.getProvince(); invoice.setCustomerID(finTaxDetail.getCustCIF());
		 * invoice.setCustomerName(finTaxDetail.getCustShrtName());
		 * invoice.setCustomerGSTIN(finTaxDetail.getTaxNumber()); invoice.setCustomerAddress(getAddress(finTaxDetail));
		 * } else {
		 */

		if (StringUtils.isNotEmpty(fm.getSubVentionFrom())) {

			VehicleDealer dealer = vehicleDealerDAO.getVehicleDealerById(fm.getManufacturerDealerId(), "_AView");
			if (dealer == null) {
				logger.warn("Linked Transaction ID : " + linkedTranId + " & Invoice Type : " + invoiceType
						+ " --> Customer Address Details are Empty.");
				return null; // write this case as a error message
			}

			country = dealer.getDealerCountry();
			province = dealer.getDealerProvince();

			invoice.setCustomerID(String.valueOf(dealer.getDealerId()));
			invoice.setCustomerName(dealer.getDealerName());

			// Preparing customer Address
			invoice.setCustomerAddress(prepareDealerAddress(dealer));
			invoice.setCustomerGSTIN(dealer.getTaxNumber());

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

	private void createGSTInvoiceForBounce(PostingDTO postingDTO) {
		FinanceMain fm = postingDTO.getFinanceMain();
		ManualAdvise ma = postingDTO.getManualAdvise();
		FeeType feeType = postingDTO.getFeeType();
		TaxHeader taxHeader = postingDTO.getTaxHeader();

		long linkedTranID = ma.getLinkedTranId();

		if (linkedTranID <= 0) {
			return;
		}

		if (taxHeader == null || CollectionUtils.isEmpty(taxHeader.getTaxDetails())) {
			return;
		}

		if (!SysParamUtil.isAllowed(SMTParameterConstants.GST_INV_ON_DUE)) {
			return;
		}

		ma.setDueCreation(true);

		ManualAdviseMovements advMovement = new ManualAdviseMovements();
		advMovement.setFeeTypeCode(ma.getFeeTypeCode());
		advMovement.setFeeTypeDesc(ma.getFeeTypeDesc());
		advMovement.setMovementAmount(ma.getAdviseAmount());

		advMovement.setFeeTypeCode(feeType.getFeeTypeCode());
		advMovement.setFeeTypeDesc(feeType.getFeeTypeDesc());
		advMovement.setTaxApplicable(feeType.isTaxApplicable());
		advMovement.setTaxComponent(feeType.getTaxComponent());
		advMovement.setStatus("D");

		advMovement.setPaidAmount(ma.getAdviseAmount());
		advMovement.setTaxHeader(taxHeader);

		List<Taxes> taxDetails = taxHeader.getTaxDetails();
		BigDecimal gstAmount = BigDecimal.ZERO;
		for (Taxes taxes : taxDetails) {
			gstAmount = gstAmount.add(taxes.getPaidTax());
		}

		if (gstAmount.compareTo(BigDecimal.ZERO) > 0) {
			List<ManualAdviseMovements> advMovements = new ArrayList<>();
			advMovements.add(advMovement);

			FinanceDetail financeDetail = new FinanceDetail();
			financeDetail.getFinScheduleData().setFinanceMain(fm);

			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setLinkedTranId(linkedTranID);
			invoiceDetail.setFinanceDetail(financeDetail);
			invoiceDetail.setMovements(advMovements);
			invoiceDetail.setWaiver(false);
			invoiceDetail.setDbInvSetReq(false);
			invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_DEBIT);

			Long invoiceID = advTaxInvoicePreparation(invoiceDetail);

			saveDueTaxDetail(ma, taxDetails, invoiceID);
		}
	}

	private void saveDueTaxDetail(ManualAdvise ma, List<Taxes> taxes, Long invoiceID) {
		AdviseDueTaxDetail detail = new AdviseDueTaxDetail();

		detail.setAdviseID(ma.getAdviseID());
		detail.setTaxType(ma.getTaxComponent());
		detail.setTaxType(ma.getTaxComponent());
		detail.setInvoiceID(invoiceID);

		detail.setAmount(ma.getAdviseAmount());
		detail.setCGST(GSTCalculator.getPaidTax(RuleConstants.CODE_CGST, taxes));
		detail.setSGST(GSTCalculator.getPaidTax(RuleConstants.CODE_SGST, taxes));
		detail.setUGST(GSTCalculator.getPaidTax(RuleConstants.CODE_UGST, taxes));
		detail.setIGST(GSTCalculator.getPaidTax(RuleConstants.CODE_IGST, taxes));
		detail.setCESS(GSTCalculator.getPaidTax(RuleConstants.CODE_CESS, taxes));
		detail.setTotalGST(CalculationUtil.getTotalGST(detail));

		manualAdviseDAO.saveDueTaxDetail(detail);
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

	private String getAddress(GSTDetail gstDetail) {
		String addrLine1 = StringUtils.trimToEmpty(gstDetail.getAddressLine1());
		String addrLine2 = StringUtils.trimToNull(gstDetail.getAddressLine2());
		String addrLine3 = StringUtils.trimToNull(gstDetail.getAddressLine3());
		String addrLine4 = StringUtils.trimToNull(gstDetail.getAddressLine4());
		String city = StringUtils.trimToNull(gstDetail.getCityCode());
		String pinCode = StringUtils.trimToNull(gstDetail.getPinCode());

		return getCommaSeperate(addrLine1, addrLine2, addrLine3, addrLine4, city, pinCode);
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

	private String prepareDealerAddress(VehicleDealer dealer) {
		String dealerAddress1 = dealer.getDealerAddress1();
		String dealerAddress2 = dealer.getDealerAddress2();
		String dealerAddress3 = dealer.getDealerAddress3();
		String city = dealer.getDealerCity();
		String province = dealer.getDealerProvince();
		String country = dealer.getDealerCountry();
		String postBox = dealer.getPOBox();
		return getCommaSeperate(dealerAddress1, dealerAddress2, dealerAddress3, city, province, country, postBox);
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

	public void setVehicleDealerDAO(VehicleDealerDAO vehicleDealerDAO) {
		this.vehicleDealerDAO = vehicleDealerDAO;
	}

	public void setGstDetailDAO(GSTDetailDAO gstDetailDAO) {
		this.gstDetailDAO = gstDetailDAO;
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

}
