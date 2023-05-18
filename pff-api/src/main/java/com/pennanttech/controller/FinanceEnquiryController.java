package com.pennanttech.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.BounceReasonDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.GuarantorDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.dao.pdc.ChequeDetailDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.model.applicant.ApplicantDetails;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.loanbalance.LoanBalance;
import com.pennant.backend.model.loandetail.LoanDetail;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.paymentmode.PaymentMode;
import com.pennant.backend.service.customermasters.CustomerEnquiryService;
import com.pennant.backend.service.finance.FinanceEnquiryService;
import com.pennant.pff.api.controller.AbstractController;
import com.pennant.pff.mandate.InstrumentType;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceType;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.CustomerUtil;
import com.pennanttech.pff.core.util.SchdUtil;

public class FinanceEnquiryController extends AbstractController {

	private CustomerEnquiryService customerEnquiryService;
	private FinanceEnquiryService financeEnquiryService;

	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private CustomerDAO customerDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private ChequeDetailDAO chequeDetailDAO;
	private MandateDAO mandateDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private BounceReasonDAO bounceReasonDAO;
	private GuarantorDetailDAO guarantorDetailDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private FinServiceInstrutionDAO finServiceInstrutionDAO;
	private FinFeeDetailDAO finFeeDetailDAO;

	private static final String ERROR_92021 = "92021";

	public FinanceDetail getLoanBasicDetails(Long finID) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = getLoanDetails(finID);

		long custID = fd.getFinScheduleData().getFinanceMain().getCustID();

		CustomerDetails cd = getCustomerDetails(custID);

		fd.setCustomerDetails(cd);

		logger.debug(Literal.LEAVING);
		return fd;
	}

	public CustomerDetails getCustomerDetails(long custID) {
		return customerEnquiryService.getCustomerDetails(custID);
	}

	public FinanceDetail getLoanDetails(Long finID) {
		return financeEnquiryService.getLoanDetails(finID);
	}

	public List<PaymentMode> getPDCEnquiry(FinanceMain fm) {
		logger.debug(Literal.ENTERING);

		List<PaymentMode> paymentModes = new ArrayList<>();

		PaymentMode response = new PaymentMode();
		Date appDate = SysParamUtil.getAppDate();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false);
		schedules = SchdUtil.sort(schedules);
		List<ChequeDetail> chequeDetails = chequeDetailDAO.getChequeDetailsByFinReference(finReference, "_AView");

		Mandate mandate = null;
		Long mandateID = fm.getMandateID();

		if (mandateID != null && mandateID > 0) {
			mandate = mandateDAO.getMandateById(mandateID, "_AView");
		}

		if (CollectionUtils.isEmpty(chequeDetails) && mandate == null) {
			response.setReturnStatus(
					getFailedStatus(ERROR_92021, "Mandate or PDC details does not exists for the requested details."));

			paymentModes.add(response);

			logger.debug(Literal.LEAVING);
			return paymentModes;
		}

		for (FinanceScheduleDetail schd : schedules) {
			if (!schd.isRepayOnSchDate()) {
				continue;
			}

			for (ChequeDetail cd : chequeDetails) {
				if (cd.geteMIRefNo() != schd.getInstNumber()) {
					continue;
				}

				cd.setSchdDate(schd.getSchDate());
				String chequeSerialNo = Integer.toString(cd.getChequeSerialNo());
				Long receiptId = finReceiptHeaderDAO.getReceiptIdByChequeSerialNo(chequeSerialNo);

				if (receiptId != null) {
					String bounceReason = bounceReasonDAO.getReasonByReceiptId(receiptId);
					cd.setChequeBounceReason(bounceReason);
				}

				paymentModes.add(preparePaymentMode(cd));
				continue;
			}

			if (mandate == null) {
				continue;
			}

			if (mandate.isSwapIsActive() && schd.getSchDate().compareTo(mandate.getSwapEffectiveDate()) >= 0
					&& mandate.getMandateID() == mandateID) {
				List<Mandate> mandatesForAutoSwap = mandateDAO.getMandatesForAutoSwap(fm.getCustID(), appDate);
				if (!CollectionUtils.isEmpty(mandatesForAutoSwap)) {
					mandate = mandatesForAutoSwap.get(0);
				}
			}

			mandate.setSchdDate(schd.getSchDate());
			mandate.setInstalmentNo(schd.getInstNumber());

			paymentModes.add(preparePaymentMode(mandate));
		}

		Long secMandateID = fm.getSecurityMandateID();

		if (secMandateID != null && secMandateID > 0) {
			Mandate secMandate = mandateDAO.getMandateById(secMandateID, "_AView");
			paymentModes.add(preparePaymentMode(secMandate));
		}

		logger.debug(Literal.LEAVING);
		return paymentModes;
	}

	public List<PaymentMode> getPDCDetails(FinanceMain fm) {
		logger.debug(Literal.ENTERING);

		List<PaymentMode> paymentModes = new ArrayList<>();

		PaymentMode response = new PaymentMode();
		Date appDate = SysParamUtil.getAppDate();

		long finID = fm.getFinID();
		List<FinanceScheduleDetail> fsdList = SchdUtil
				.sort(financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false));
		List<ChequeDetail> chequeDetailList = chequeDetailDAO.getChequeDetailsByFinReference(fm.getFinReference(),
				"_AView");
		boolean isEmpty = CollectionUtils.isEmpty(chequeDetailList);

		Mandate mandate = null;
		Long mandateID = fm.getMandateID();
		if (mandateID != null) {
			mandate = mandateDAO.getMandateById(mandateID, "_AView");
		}

		for (FinanceScheduleDetail fsd : fsdList) {
			if (isEmpty && mandate == null) {
				response.setReturnStatus(
						getFailedStatus(ERROR_92021, "Mandate and PDC does not exist for the given LAN"));
				paymentModes.add(response);
				break;
			}
			if (appDate.compareTo(fsd.getSchDate()) <= 0 && fsd.isRepayOnSchDate()) {
				if (isEmpty) {
					for (ChequeDetail cd : chequeDetailList) {
						if (cd.geteMIRefNo() != fsd.getInstNumber()) {
							continue;
						}
						cd.setSchdDate(fsd.getSchDate());
						response = preparePaymentMode(cd);
						paymentModes.add(response);
						continue;
					}
				}

				if (mandate == null) {
					continue;
				}

				if (mandate.isSwapIsActive() && fsd.getSchDate().compareTo(mandate.getSwapEffectiveDate()) >= 0
						&& mandate.getMandateID() == mandateID) {
					List<Mandate> mandatesForAutoSwap = mandateDAO.getMandatesForAutoSwap(fm.getCustID(), appDate);
					if (!CollectionUtils.isEmpty(mandatesForAutoSwap)) {
						mandate = mandatesForAutoSwap.get(0);
					}

				}
				mandate.setSchdDate(fsd.getSchDate());
				mandate.setInstalmentNo(fsd.getInstNumber());
				response = preparePaymentMode(mandate);
				paymentModes.add(response);
			}
		}

		if (CollectionUtils.isEmpty(paymentModes)) {
			response.setReturnStatus(
					getFailedStatus(ERROR_92021, "No Future Instalments are present for the requested FinReference"));
			paymentModes.add(response);
			logger.debug(Literal.LEAVING);
			return paymentModes;
		}

		return paymentModes;
	}

	private PaymentMode preparePaymentMode(ChequeDetail cd) {
		PaymentMode response = new PaymentMode();

		response.setLoanInstrumentMode(cd.getChequeType());
		response.setLoanDueDate(cd.getSchdDate());
		response.setBankName(cd.getBankName());
		response.setBankCityName(cd.getCity());
		response.setMicr(cd.getMicr());
		response.setBankBranchName(cd.getBankName());
		response.setAccountNo(cd.getAccountNo());
		response.setAccountHolderName(cd.getAccHolderName());
		response.setAccountType(cd.getAccountType());
		response.setInstallmentNo(cd.geteMIRefNo());
		response.setPdcType(InstrumentType.isPDC(cd.getChequeType()) ? "Normal" : "Security");
		response.setChqDate(cd.getChequeDate());
		response.setChqNo(cd.getChequeSerialNumber());
		response.setChqStatus(cd.getChequeStatus());
		response.setBounceReason(cd.getChequeBounceReason());

		return response;

	}

	private PaymentMode preparePaymentMode(Mandate mndt) {
		PaymentMode response = new PaymentMode();

		response.setLoanInstrumentMode(mndt.getMandateType());
		response.setLoanDueDate(mndt.getSchdDate());
		response.setBankName(mndt.getBankName());
		response.setBankCityName(mndt.getCity());
		response.setMicr(mndt.getMICR());
		response.setBankBranchName(mndt.getBankName());
		response.setAccountNo(mndt.getAccNumber());
		response.setAccountHolderName(mndt.getAccHolderName());
		response.setAccountType(mndt.getAccType());
		response.setInstallmentNo(mndt.getInstalmentNo());

		return response;

	}

	public List<ApplicantDetails> getApplicantDetails(FinanceMain fm) {
		logger.debug(Literal.ENTERING);

		List<ApplicantDetails> response = new ArrayList<>();

		Customer applicant = customerDAO.getBasicDetails(fm.getCustID(), TableType.MAIN_TAB);
		applicant.setApplicantType("Applicant");
		addApplicantDetails(applicant, response);

		List<Customer> coApplicants = customerDAO.getBasicDetailsForJointCustomers(fm.getFinID(), TableType.MAIN_TAB);

		for (Customer coApplicant : coApplicants) {
			coApplicant.setApplicantType("CoApplicant");
			addApplicantDetails(coApplicant, response);
		}

		List<GuarantorDetail> guarantors = guarantorDetailDAO.getGuarantorDetailByFinRef(fm.getFinID(), "_AView");

		Customer guarantator;
		for (GuarantorDetail guarantor : guarantors) {
			if (guarantor.isBankCustomer()) {
				guarantator = customerDAO.getBasicDetails(fm.getCustID(), TableType.MAIN_TAB);
				guarantator.setApplicantType("Guarantor");
				guarantator.setCustCIF(guarantor.getGuarantorCIF());

				addApplicantDetails(guarantator, response);
			} else {
				guarantator = new Customer();
				ApplicantDetails ad = new ApplicantDetails();
				ad.setApplicantType("Guarantor");
				ad.setCustID(guarantor.getCustID());
				ad.setFullName(guarantor.getGuarantorCIFName());

				response.add(ad);
			}
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	public LoanBalance getBalanceDetails(FinanceMain fm) {
		logger.debug(Literal.ENTERING);

		LoanBalance response = new LoanBalance();

		long finID = fm.getFinID();
		Date appDate = SysParamUtil.getAppDate();

		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getBasicDetails(finID);
		List<FinODDetails> odDetails = finODDetailsDAO.getLPPDueAmount(finID);
		List<BigDecimal> bounceCharges = manualAdviseDAO.getBounceChargesByFinID(finID);

		Date businessDate = appDate;
		if (appDate.compareTo(fm.getMaturityDate()) >= 0) {
			businessDate = DateUtil.addDays(fm.getMaturityDate(), -1);
		}

		int instalments = Collections
				.max(schedules.stream().map(schd -> schd.getInstNumber()).collect(Collectors.toList()));

		response.setInstalmentsPaid(SchdUtil.getPaidInstalments(schedules));
		response.setExcessMoney(finExcessAmountDAO.getExcessBalance(finID));
		response.setOverDueInstallment(SchdUtil.getOverDueEMI(businessDate, schedules));
		response.setDueDate(SchdUtil.getNextInstalment(businessDate, schedules).getSchDate());
		response.setTotalInstalments(instalments - fm.getAdvTerms());
		response.setAdvanceInstalments(AdvanceType.AE.getCode().equals(fm.getAdvType()) ? fm.getAdvTerms() : 0);
		response.setRepayMethod(fm.getFinRepayMethod());
		response.setOutstandingPri(SchdUtil.getOutStandingPrincipal(schedules, appDate));
		response.setOverDueCharges(SchdUtil.getLPPDueAmount(odDetails));
		response.setChequeBounceCharges(bounceCharges.stream().reduce(BigDecimal.ZERO, BigDecimal::add));

		logger.debug(Literal.LEAVING);

		return response;
	}

	public List<LoanDetail> getRateChangeDetails(FinanceMain fm) {
		logger.debug(Literal.ENTERING);

		List<LoanDetail> ldList = new ArrayList<>();

		LoanDetail ld = new LoanDetail();
		long finID = fm.getFinID();

		List<FinServiceInstruction> fsiList = finServiceInstrutionDAO.getFinServiceInstructions(finID, "",
				FinServiceEvent.RATECHG);

		if (CollectionUtils.isEmpty(fsiList)) {
			setRateCahngeDetails(fm, ld);

			ldList.add(ld);

			logger.debug(Literal.LEAVING);
			return ldList;
		}

		getRateChange(fm, ldList, ld, fsiList);

		return ldList;
	}

	/*
	 * public List<ChargeDetails> getChargeDetails(long finID) { logger.debug(Literal.ENTERING);
	 * 
	 * List<ChargeDetails> response = new ArrayList<>();
	 * 
	 * List<ManualAdvise> recAdvises = manualAdviseDAO.getReceivableAdvises(finID, "_AView"); List<FinFeeDetail> fees =
	 * finFeeDetailDAO.getFinFeeDetailByFinRef(finID, false, "_AView"); List<FinODDetails> lppDues =
	 * finODDetailsDAO.getLPPDueAmount(finID);
	 * 
	 * Map<String, ChargeDetails> chargeDetails = new HashMap<>();
	 * 
	 * ChargeDetails cd;
	 * 
	 * for (ManualAdvise ma : recAdvises) { if (!chargeDetails.containsKey(ma.getFeeTypeCode()) &&
	 * BigDecimal.ZERO.compareTo(ma.getBalanceAmt()) < 0) { cd = new ChargeDetails();
	 * cd.setChargeTypeDesc(ma.getFeeTypeDesc()); cd.setDueAmount(ma.getBalanceAmt());
	 * 
	 * chargeDetails.put(ma.getFeeTypeCode(), cd); continue; } if (BigDecimal.ZERO.compareTo(ma.getBalanceAmt()) < 0) {
	 * cd = chargeDetails.get(ma.getFeeTypeCode()); cd.setDueAmount(cd.getDueAmount().add(ma.getBalanceAmt())); } }
	 * 
	 * for (FinFeeDetail fee : fees) { if (!chargeDetails.containsKey(fee.getFeeTypeCode()) &&
	 * BigDecimal.ZERO.compareTo(fee.getRemainingFee()) < 0) { cd = new ChargeDetails();
	 * cd.setChargeTypeDesc(fee.getFeeTypeDesc()); cd.setDueAmount(fee.getRemainingFee());
	 * cd.setChargeRate(fee.getPercentage());
	 * 
	 * chargeDetails.put(fee.getFeeTypeCode(), cd); continue; } if (BigDecimal.ZERO.compareTo(fee.getRemainingFee()) <
	 * 0) { cd = chargeDetails.get(fee.getFeeTypeCode()); cd.setDueAmount(cd.getDueAmount().add(fee.getRemainingFee()));
	 * } }
	 * 
	 * cd = new ChargeDetails();
	 * 
	 * cd.setChargeTypeDesc("Late Pay Penalty"); cd.setDueAmount(SchdUtil.getLPPDueAmount(lppDues));
	 * 
	 * if (BigDecimal.ZERO.compareTo(cd.getDueAmount()) < 0) { chargeDetails.put("LPP", cd); }
	 * 
	 * chargeDetails.forEach((k, v) -> response.add(v));
	 * 
	 * return response;
	 * 
	 * }
	 */

	private void getRateChange(FinanceMain fm, List<LoanDetail> ldList, LoanDetail ld,
			List<FinServiceInstruction> fsiList) {

		FinServiceInstruction fsi = null;
		for (int i = 0; i < fsiList.size(); i++) {
			LoanDetail loanDetail = null;

			fsi = fsiList.get(i);
			Date fromDate = fsi.getRecalFromDate();
			Date toDate = null;

			if (i == 0) {
				loanDetail = new LoanDetail();
				loanDetail.setFromDate(fm.getFinStartDate());
				loanDetail.setToDate(fromDate == null ? fsi.getFromDate() : fromDate);
				loanDetail.setRepayProfitRate(fm.getRepayProfitRate());
				ldList.add(loanDetail);
			}

			if (fsiList.size() - 1 > i) {
				toDate = fsiList.get(i + 1).getRecalFromDate();
			}

			loanDetail = new LoanDetail();
			switch (fsi.getRecalType()) {
			case CalculationConstants.RPYCHG_TILLMDT:
				loanDetail.setFromDate(fromDate);
				loanDetail.setToDate(toDate == null ? fm.getMaturityDate() : toDate);
				loanDetail.setRepayProfitRate(fsi.getActualRate());
				break;
			case CalculationConstants.RPYCHG_TILLDATE:
				loanDetail.setFromDate(fromDate);
				loanDetail.setToDate(toDate);
				loanDetail.setRepayProfitRate(fsi.getActualRate());
				break;
			default:
				loanDetail.setFromDate(fsi.getFromDate());
				loanDetail.setToDate(toDate == null ? fsi.getToDate() : toDate);
				loanDetail.setRepayProfitRate(fsi.getActualRate());
				break;
			}

			ldList.add(loanDetail);
		}

		if ((CalculationConstants.RPYCHG_TILLMDT.equals(fsi.getRecalType())
				&& fm.getMaturityDate().compareTo(fsi.getToDate()) == 0)
				|| (CalculationConstants.RPYCHG_TILLDATE.equals(fsi.getRecalType())
						&& fm.getMaturityDate().compareTo(fsi.getRecalToDate()) == 0)
				|| fm.getMaturityDate().compareTo(fsi.getToDate()) == 0) {

			return;
		}

		LoanDetail loanDetail = new LoanDetail();
		loanDetail.setFromDate(fsi.getRecalToDate() == null ? fsi.getToDate() : fsi.getRecalToDate());
		loanDetail.setToDate(fm.getMaturityDate());
		loanDetail.setRepayProfitRate(fm.getRepayProfitRate());
		ldList.add(loanDetail);
	}

	private void setRateCahngeDetails(FinanceMain fm, LoanDetail ld) {
		ld.setFromDate(fm.getFinStartDate());
		ld.setToDate(fm.getMaturityDate());
		ld.setRepayProfitRate(fm.getRepayProfitRate());
	}

	private void addApplicantDetails(Customer customer, List<ApplicantDetails> applicantDetails) {
		ApplicantDetails ad = new ApplicantDetails();

		ad.setFullName(CustomerUtil.getCustomerFullName(customer));
		ad.setApplicantType(customer.getApplicantType());
		ad.setRelation(customer.getRelationWithCust());
		ad.setCustCIF(customer.getCustCIF());

		applicantDetails.add(ad);
	}

	@Autowired
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Autowired
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	@Autowired
	public void setChequeDetailDAO(ChequeDetailDAO chequeDetailDAO) {
		this.chequeDetailDAO = chequeDetailDAO;
	}

	@Autowired
	public void setMandateDAO(MandateDAO mandateDAO) {
		this.mandateDAO = mandateDAO;
	}

	@Autowired
	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	@Autowired
	public void setBounceReasonDAO(BounceReasonDAO bounceReasonDAO) {
		this.bounceReasonDAO = bounceReasonDAO;
	}

	@Autowired
	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	@Autowired
	public void setGuarantorDetailDAO(GuarantorDetailDAO guarantorDetailDAO) {
		this.guarantorDetailDAO = guarantorDetailDAO;
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	@Autowired
	public void setFinServiceInstrutionDAO(FinServiceInstrutionDAO finServiceInstrutionDAO) {
		this.finServiceInstrutionDAO = finServiceInstrutionDAO;
	}

	@Autowired
	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}

	@Autowired
	public void setCustomerEnquiryService(CustomerEnquiryService customerEnquiryService) {
		this.customerEnquiryService = customerEnquiryService;
	}

	@Autowired
	public void setFinanceEnquiryService(FinanceEnquiryService financeEnquiryService) {
		this.financeEnquiryService = financeEnquiryService;
	}
}
