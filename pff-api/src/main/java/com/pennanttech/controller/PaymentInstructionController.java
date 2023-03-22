package com.pennanttech.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.APIHeader;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.BankDetailDAO;
import com.pennant.backend.dao.bmtmasters.BankBranchDAO;
import com.pennant.backend.dao.payment.PaymentHeaderDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.service.payment.PaymentHeaderService;
import com.pennant.backend.service.payment.PaymentInstructionService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.pff.payment.model.PaymentDetail;
import com.pennant.pff.payment.model.PaymentHeader;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class PaymentInstructionController extends ExtendedTestClass {
	private final Logger logger = LogManager.getLogger(getClass());

	private PaymentHeaderService paymentHeaderService;
	private BankBranchDAO bankBranchDAO;
	private PaymentHeaderDAO paymentHeaderDAO;
	private BankDetailDAO bankDetailDAO;
	private PaymentInstructionService paymentInstructionService;

	public WSReturnStatus createPaymentInstruction(PaymentHeader ph) {
		logger.debug(Literal.ENTERING);

		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
				.get(APIHeader.API_HEADER_KEY);

		preparePH(ph);

		preparePI(ph);

		preparePD(ph);

		prepareGSTForMA(ph);

		WSReturnStatus returnStatus = validatePaymentDetails(ph);

		if (returnStatus != null) {
			return returnStatus;
		}

		setPaymentDetailAmount(ph);

		AuditHeader auditHeader = getAuditHeader(ph, PennantConstants.TRAN_WF);
		auditHeader.setApiHeader(reqHeaderDetails);

		try {
			auditHeader = paymentHeaderService.doApprove(auditHeader);

			List<ErrorDetail> errors = auditHeader.getErrorMessage();
			if (CollectionUtils.isEmpty(errors)) {
				return APIErrorHandlerService.getSuccessStatus();
			}

			ErrorDetail error = errors.get(0);
			return APIErrorHandlerService.getFailedStatus(error.getCode(), error.getError());

		} catch (Exception e) {
			return APIErrorHandlerService.getFailedStatus();
		}
	}

	public PaymentHeader getPaymentDetails(long paymentId) {
		PaymentHeader exePH = paymentHeaderService.getPaymentHeader(paymentId);

		PaymentHeader ph = new PaymentHeader();
		ph.setFinID(exePH.getFinID());
		ph.setFinReference(exePH.getFinReference());
		ph.setPaymentId(exePH.getPaymentId());
		ph.setPaymentType(exePH.getPaymentType());
		ph.setPaymentAmount(exePH.getPaymentAmount());

		PaymentInstruction pi = new PaymentInstruction();

		PaymentInstruction exePI = exePH.getPaymentInstruction();

		pi.setRemarks(exePI.getRemarks());
		pi.setPartnerBankName(exePI.getPartnerBankName());
		pi.setPaymentType(exePI.getPaymentType());
		pi.setAccountNo(exePI.getAccountNo());
		pi.setAcctHolderName(exePI.getAcctHolderName());
		pi.setPhoneNumber(exePI.getPhoneNumber());
		pi.setIssuingBank(exePI.getIssuingBank());
		pi.setPayableLoc(exePI.getPayableLoc());
		pi.setPrintingLoc(exePI.getPrintingLoc());
		pi.setValueDate(exePI.getValueDate());
		pi.setLei(exePI.getLei());
		pi.setFavourName(exePI.getFavourName());
		pi.setFavourNumber(exePI.getFavourNumber());
		pi.setBankBranchIFSC(exePI.getBankBranchIFSC());
		pi.setPostDate(exePI.getPostDate());
		pi.setIssuingBank(exePI.getIssuingBank());
		pi.setPaymentId(paymentId);
		pi.setPaymentInstructionId(0);
		pi.setPaymentAmount(null);
		pi.setBankBranchId(0);
		pi.setFinReference(null);
		pi.setPaymentInstructionId(0);
		pi.setRoleCode(null);
		pi.setNextRoleCode(null);
		pi.setTaskId(null);
		pi.setNextTaskId(null);
		pi.setWorkflowId(0);
		ph.setPaymentDetailList(exePH.getPaymentDetailList());
		ph.setPaymentInstruction(pi);

		return ph;
	}

	public WSReturnStatus updatePaymentInstruction(PaymentHeader ph) {
		PaymentInstruction pi = ph.getPaymentInstruction();
		paymentHeaderDAO.updateTransactionRef(pi.getPaymentId(), pi.getTransactionRef());

		return APIErrorHandlerService.getSuccessStatus();
	}

	private void preparePH(PaymentHeader ph) {
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

		Date appDate = SysParamUtil.getAppDate();

		ph.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		ph.setNewRecord(true);
		ph.setVersion(1);
		ph.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		ph.setApprovedOn(new Timestamp(System.currentTimeMillis()));
		ph.setSourceId(PennantConstants.FINSOURCE_ID_API);
		ph.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		ph.setStatus(RepayConstants.PAYMENT_APPROVE);
		ph.setPaymentType(DisbursementConstants.CHANNEL_PAYMENT);
		ph.setUserDetails(userDetails);
		ph.setPaymentType(DisbursementConstants.CHANNEL_PAYMENT);
		ph.setLastMntBy(ph.getUserDetails().getUserId());
		ph.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		ph.setAppDate(appDate);
	}

	private void preparePD(PaymentHeader ph) {
		logger.debug(Literal.ENTERING);

		List<FinExcessAmount> excessDetails = this.paymentHeaderService.getfinExcessAmount(ph.getFinID());

		List<PaymentDetail> detailList = new ArrayList<>();

		for (FinExcessAmount fea : excessDetails) {
			PaymentDetail pd = new PaymentDetail();

			pd.setNewRecord(true);
			pd.setReferenceId(fea.getId());
			pd.setAvailableAmount(fea.getBalanceAmt());
			pd.setAmountType(fea.getAmountType());
			pd.setAmount(fea.getAmount());
			pd.setSourceId(PennantConstants.FINSOURCE_ID_API);

			detailList.add(pd);
		}

		List<ManualAdvise> maList = this.paymentHeaderService.getManualAdvise(ph.getFinID());

		for (ManualAdvise ma : maList) {
			PaymentDetail pd = new PaymentDetail();

			pd.setNewRecord(true);
			pd.setReferenceId(ma.getAdviseID());
			pd.setAvailableAmount(ma.getAdviseAmount().subtract(ma.getPaidAmount()).subtract(ma.getWaivedAmount()));
			pd.setAmountType(String.valueOf(ma.getAdviseType()));
			pd.setFeeTypeCode(ma.getFeeTypeCode());
			pd.setFeeTypeDesc(ma.getFeeTypeDesc());
			pd.setAdviseAmount(ma.getAdviseAmount());

			BigDecimal paidTGST = CalculationUtil.getTotalPaidGST(ma);
			BigDecimal waivedTGST = CalculationUtil.getTotalWaivedGST(ma);

			pd.setPrvGST(paidTGST.add(waivedTGST));
			pd.setManualAdvise(ma);
			pd.setTaxApplicable(ma.isTaxApplicable());
			pd.setTaxComponent(ma.getTaxComponent());
			pd.setAmount(ma.getAdviseAmount());
			pd.setSourceId(PennantConstants.FINSOURCE_ID_API);
			detailList.add(pd);
		}

		List<PaymentDetail> paymentDetails = new ArrayList<>();

		for (PaymentDetail detail : detailList) {
			if (detail.getAvailableAmount().compareTo(BigDecimal.ZERO) == 0) {
				continue;
			}

			paymentDetails.add(detail);
		}

		ph.setCalPaymentDetailList(paymentDetails);

		logger.debug(Literal.LEAVING);
	}

	private void prepareGSTForMA(PaymentHeader ph) {
		List<PaymentDetail> paymentDetails = ph.getCalPaymentDetailList();

		Map<String, BigDecimal> taxPercMap = GSTCalculator.getTaxPercentages(ph.getFinID());

		for (PaymentDetail detail : paymentDetails) {
			if (!String.valueOf(FinanceConstants.MANUAL_ADVISE_PAYABLE).equals(detail.getAmountType())) {
				continue;
			}

			if (detail.isTaxApplicable()) {
				processTax(taxPercMap, detail);
			}
		}
	}

	private void processTax(Map<String, BigDecimal> taxPercMap, PaymentDetail detail) {
		TaxHeader taxHeader = detail.getTaxHeader();

		if (taxHeader == null) {
			taxHeader = new TaxHeader();
			taxHeader.setNewRecord(true);
			taxHeader.setRecordType(PennantConstants.RCD_ADD);
			taxHeader.setVersion(taxHeader.getVersion() + 1);
			detail.setTaxHeader(taxHeader);
		}

		if (taxHeader.getTaxDetails() == null || CollectionUtils.isEmpty(taxHeader.getTaxDetails())) {
			taxHeader.setTaxDetails(new ArrayList<>());

			taxHeader.getTaxDetails().add(getTaxDetail(RuleConstants.CODE_CGST, taxPercMap));
			taxHeader.getTaxDetails().add(getTaxDetail(RuleConstants.CODE_SGST, taxPercMap));
			taxHeader.getTaxDetails().add(getTaxDetail(RuleConstants.CODE_IGST, taxPercMap));
			taxHeader.getTaxDetails().add(getTaxDetail(RuleConstants.CODE_UGST, taxPercMap));
			taxHeader.getTaxDetails().add(getTaxDetail(RuleConstants.CODE_CESS, taxPercMap));
		}

		TaxAmountSplit taxSplit = null;

		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(detail.getTaxComponent())) {
			taxSplit = GSTCalculator.getExclusiveGST(detail.getAvailableAmount(), taxPercMap);
		} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(detail.getTaxComponent())) {
			taxSplit = GSTCalculator.getInclusiveGST(detail.getAvailableAmount(), taxPercMap);
		}

		paymentInstructionService.getActualGST(detail, taxSplit);
		if (taxSplit != null) {
			processTaxDetails(taxPercMap, taxHeader, taxSplit);
		}
	}

	private void processTaxDetails(Map<String, BigDecimal> taxPercMap, TaxHeader taxHeader, TaxAmountSplit taxSplit) {
		Taxes cgstTax = null;
		Taxes sgstTax = null;
		Taxes igstTax = null;
		Taxes ugstTax = null;
		Taxes cessTax = null;

		List<Taxes> taxDetails = taxHeader.getTaxDetails();

		for (Taxes taxes : taxDetails) {
			switch (taxes.getTaxType()) {
			case RuleConstants.CODE_CGST:
				cgstTax = taxes;
				cgstTax.setTaxPerc(taxPercMap.get(RuleConstants.CODE_CGST));
				cgstTax.setActualTax(taxSplit.getcGST());
				cgstTax.setNetTax(taxSplit.getcGST());
				break;
			case RuleConstants.CODE_IGST:
				igstTax = taxes;
				igstTax.setTaxPerc(taxPercMap.get(RuleConstants.CODE_IGST));
				igstTax.setActualTax(taxSplit.getiGST());
				igstTax.setNetTax(taxSplit.getiGST());
				break;
			case RuleConstants.CODE_SGST:
				sgstTax = taxes;
				sgstTax.setTaxPerc(taxPercMap.get(RuleConstants.CODE_SGST));
				sgstTax.setActualTax(taxSplit.getsGST());
				sgstTax.setNetTax(taxSplit.getsGST());
				break;
			case RuleConstants.CODE_UGST:
				ugstTax = taxes;
				ugstTax.setTaxPerc(taxPercMap.get(RuleConstants.CODE_UGST));
				ugstTax.setActualTax(taxSplit.getuGST());
				ugstTax.setNetTax(taxSplit.getuGST());
				break;
			case RuleConstants.CODE_CESS:
				cessTax = taxes;
				cessTax.setTaxPerc(taxPercMap.get(RuleConstants.CODE_CESS));
				cessTax.setActualTax(taxSplit.getCess());
				cessTax.setNetTax(taxSplit.getCess());
				break;
			default:
				break;
			}
		}
	}

	private void preparePI(PaymentHeader ph) {
		PaymentInstruction pi = ph.getPaymentInstruction();

		pi.setPaymentAmount(ph.getPaymentAmount());
		pi.setBankBranchIFSC(pi.getBankBranchIFSC());
		pi.setAccountNo(pi.getAccountNo());
		pi.setAcctHolderName(pi.getAcctHolderName());
		pi.setPhoneNumber(pi.getPhoneNumber());
		pi.setIssuingBank(pi.getIssuingBank());
		pi.setValueDate(pi.getValueDate());
		pi.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		pi.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		pi.setLastMntBy(ph.getUserDetails().getUserId());
		pi.setUserDetails(ph.getUserDetails());
		pi.setLei(pi.getLei());

		prepareBankBranchDetails(pi);
	}

	private void prepareBankBranchDetails(PaymentInstruction pi) {
		logger.debug(Literal.ENTERING);

		BankBranch bb = bankBranchDAO.getBankBrachByIFSC(pi.getBankBranchIFSC(), "");

		if (bb != null) {
			pi.setBankBranchIFSC(bb.getIFSC());
			pi.setBankBranchId(bb.getBankBranchID());
			pi.setBankBranchCode(bb.getBankCode());
			pi.setBankName(bb.getBankName());
			pi.setBranchDesc(bb.getBranchDesc());
			pi.setpCCityName(bb.getPCCityName());
		}

		logger.debug(Literal.LEAVING);
	}

	private WSReturnStatus validatePaymentDetails(PaymentHeader ph) {
		BigDecimal amount = BigDecimal.ZERO;

		List<PaymentDetail> calPDList = ph.getCalPaymentDetailList();

		if (CollectionUtils.isEmpty(calPDList)) {
			return getError("90502", "Payments details not exists for the lan.");
		}

		for (PaymentDetail calPD : calPDList) {
			int i = 0;

			for (PaymentDetail pd : ph.getPaymentDetailList()) {
				if (calPD.getAmountType().equals(pd.getAmountType())) {
					continue;
				}

				String paymentType = pd.getPaymentType();
				if (String.valueOf(FinanceConstants.MANUAL_ADVISE_PAYABLE).equals(calPD.getAmountType())) {
					if (calPD.getFeeTypeCode().equals(pd.getFeeTypeCode())) {
						continue;
					}

					if (calPD.getAvailableAmount().compareTo(pd.getAmount()) < 0) {
						return getError("30550", "Payable amount should match with amounts in payable details");
					}

					if (paymentType == null || StringUtils.isEmpty(paymentType)) {
						return getError("30550", "Payment Type is Invalid");
					}

					i++;
					amount = amount.add(pd.getAmount());
				} else {
					if (calPD.getAvailableAmount().compareTo(pd.getAmount()) < 0) {
						return getError("30550", "Payable amount should match with amounts in payable details");
					}

					if (!isValidPaymentMethod(paymentType)) {
						return getError("30550", "Payment Type is Invalid");
					}

					i++;
					amount = amount.add(pd.getAmount());
				}
			}

			if (i == 0) {
				return getError("30550", "FeeType Codes Should be Matched With Existing:", ph.getFinReference());
			}
		}

		if (ph.getPaymentAmount().compareTo(amount) != 0) {
			return getError("30550", "Total Payment should equal to :" + amount);
		}

		Date appDate = ph.getAppDate();

		PaymentInstruction pi = ph.getPaymentInstruction();

		Date postDate = pi.getPostDate();

		if (postDate == null) {
			return getError("90502", "Payment Date");
		}
		if (DateUtil.compare(postDate, appDate) < 0) {
			return getError("30550", "Payment Date should be greater than or equal to :" + appDate);
		}

		Date todate = DateUtil.addMonths(appDate, 6);
		String paymentType = pi.getPaymentType();

		if (pi.getValueDate() != null && DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(paymentType)
				&& (pi.getValueDate().compareTo(todate) > 0 || pi.getValueDate().compareTo(appDate) < 0)) {
			return getError("30550", "Value Date should on/after :" + appDate + " and on/before :" + todate);
		}

		WSReturnStatus error = validatePattern(pi);

		if (error != null) {
			return error;
		}

		switch (paymentType) {
		case DisbursementConstants.PAYMENT_TYPE_IMPS:
			if (StringUtils.isEmpty(pi.getPhoneNumber())) {
				return getError("90502", "Phone Number");
			}
			break;
		case RepayConstants.PAYTYPE_CHEQUE:
		case RepayConstants.PAYTYPE_DD:
			if (!bankDetailDAO.isBankCodeExits(pi.getIssuingBank())) {
				return getError("30550", pi.getIssuingBank() + "is not Invalid");
			}
			break;
		default:
			break;
		}

		return null;
	}

	private WSReturnStatus validatePattern(PaymentInstruction pi) {
		WSReturnStatus error = validatePattern(pi.getLei(), PennantRegularExpressions.REGEX_ALPHANUM, "Lei is Invalid");

		if (error != null) {
			return error;
		}

		error = validatePattern(pi.getAcctHolderName(), PennantRegularExpressions.REGEX_ACCOUNT_HOLDER_NAME,
				"Account Holder Name is Invalid");

		if (error != null) {
			return error;
		}

		error = validatePattern(pi.getAccountNo(), PennantRegularExpressions.REGEX_ACCOUNTNUMBER,
				"AccountNo is Invalid");

		if (error != null) {
			return error;
		}

		error = validatePattern(pi.getFavourName(), PennantRegularExpressions.REGEX_NAME, "Favour Name is Invalid");

		if (error != null) {
			return error;
		}

		error = validatePattern(pi.getPayableLoc(), PennantRegularExpressions.REGEX_ADDRESS, "PayableLoc is Invalid");

		if (error != null) {
			return error;
		}

		error = validatePattern(pi.getPrintingLoc(), PennantRegularExpressions.REGEX_ADDRESS, "PrintingLoc is Invalid");

		return error;
	}

	private WSReturnStatus validatePattern(String code, String regex, String desc) {
		if (StringUtils.isBlank(code)) {
			return null;
		}

		Pattern pattern = Pattern.compile(PennantRegularExpressions.getRegexMapper(regex));

		Matcher matcher = pattern.matcher(code);
		if (matcher.matches()) {
			return null;
		}

		return getError("90347", desc);
	}

	private void setPaymentDetailAmount(PaymentHeader ph) {
		processPDAmount(ph);

		List<PaymentDetail> list = new ArrayList<>();
		if (ph.isNewRecord()) {
			for (PaymentDetail detail : ph.getCalPaymentDetailList()) {
				if (detail.getAmount() != null && (detail.getAmount().compareTo(BigDecimal.ZERO) == 0)) {
					continue;
				}

				detail.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
				detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				detail.setNewRecord(true);

				list.add(detail);
			}
		}

		ph.setPaymentDetailList(list);
	}

	private void processPDAmount(PaymentHeader ph) {
		for (PaymentDetail pdetails : ph.getCalPaymentDetailList()) {
			for (PaymentDetail pd : ph.getPaymentDetailList()) {
				if (String.valueOf(FinanceConstants.MANUAL_ADVISE_PAYABLE).equals(pdetails.getAmountType())) {
					if (pdetails.getFeeTypeCode().equals(pd.getAmountType())) {
						pdetails.setAmount(pd.getAmount());
					}
				} else {
					if (pdetails.getAmountType().equals(pd.getAmountType())) {
						pdetails.setAmount(pd.getAmount());
					}
				}
			}
		}
	}

	private boolean isValidPaymentMethod(String paymentType) {
		if (StringUtils.isEmpty(paymentType)) {
			return false;
		}

		switch (paymentType) {
		case RepayConstants.EXAMOUNTTYPE_EMIINADV:
		case RepayConstants.EXAMOUNTTYPE_ADVEMI:
		case RepayConstants.EXAMOUNTTYPE_ADVINT:
		case RepayConstants.EXAMOUNTTYPE_EXCESS:
			return true;
		default:
			return false;
		}
	}

	private Taxes getTaxDetail(String taxType, Map<String, BigDecimal> taxPercMap) {
		Taxes taxes = new Taxes();
		taxes.setTaxType(taxType);
		taxes.setTaxPerc(taxPercMap.get(taxType));

		return taxes;
	}

	private AuditHeader getAuditHeader(PaymentHeader ph, String tranType) {
		AuditDetail ad = new AuditDetail(tranType, 1, ph.getBefImage(), ph);
		String strPaymentID = String.valueOf(ph.getPaymentId());
		return new AuditHeader(strPaymentID, strPaymentID, null, null, ad, ph.getUserDetails(), new HashMap<>());
	}

	public WSReturnStatus getError(String errorCode, String... valueParm) {
		WSReturnStatus response = APIErrorHandlerService.getFailedStatus(errorCode, valueParm);

		if (StringUtils.isBlank(response.getReturnCode())) {
			response = APIErrorHandlerService.getFailedStatus(APIConstants.RES_FAILED_CODE,
					APIConstants.RES_FAILED_DESC);
		}

		return response;
	}

	@Autowired
	public void setPaymentHeaderService(PaymentHeaderService paymentHeaderService) {
		this.paymentHeaderService = paymentHeaderService;
	}

	@Autowired
	public void setBankBranchDAO(BankBranchDAO bankBranchDAO) {
		this.bankBranchDAO = bankBranchDAO;
	}

	@Autowired
	public void setPaymentHeaderDAO(PaymentHeaderDAO paymentHeaderDAO) {
		this.paymentHeaderDAO = paymentHeaderDAO;
	}

	@Autowired
	public void setPaymentInstructionService(PaymentInstructionService paymentInstructionService) {
		this.paymentInstructionService = paymentInstructionService;
	}

	@Autowired
	public void setBankDetailDAO(BankDetailDAO bankDetailDAO) {
		this.bankDetailDAO = bankDetailDAO;
	}

}
