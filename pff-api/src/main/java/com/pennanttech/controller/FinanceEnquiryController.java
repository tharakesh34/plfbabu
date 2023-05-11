package com.pennanttech.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.BounceReasonDAO;
import com.pennant.backend.dao.customermasters.CustomerAddresDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.customermasters.CustomerEMailDAO;
import com.pennant.backend.dao.customermasters.CustomerEmploymentDetailDAO;
import com.pennant.backend.dao.customermasters.CustomerPhoneNumberDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.GuarantorDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.dao.pdc.ChequeDetailDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.model.applicant.ApplicantDetails;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.loanbalance.LoanBalance;
import com.pennant.backend.model.loandetail.LoanDetail;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.paymentmode.PaymentMode;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.pff.api.controller.AbstractController;
import com.pennant.pff.core.loan.util.LoanClosureCalculator;
import com.pennant.pff.data.loader.AddressDataLoader;
import com.pennant.pff.data.loader.CustomerDataLoader;
import com.pennant.pff.data.loader.EmailDataLoader;
import com.pennant.pff.data.loader.EmploymentDataLoader;
import com.pennant.pff.data.loader.FMDataLoader;
import com.pennant.pff.data.loader.PhoneNumberDataLoader;
import com.pennant.pff.data.loader.ProfitDetailDataLoader;
import com.pennant.pff.data.loader.ScheduleDataLoader;
import com.pennant.pff.mandate.InstrumentType;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceType;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.CustomerUtil;
import com.pennanttech.pff.core.util.SchdUtil;
import com.pennattech.pff.receipt.model.ReceiptDTO;

public class FinanceEnquiryController extends AbstractController {

	private FinanceMainDAO financeMainDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private SummaryDetailService summaryDetailService;
	private CustomerDAO customerDAO;
	private CustomerAddresDAO customerAddresDAO;
	private CustomerPhoneNumberDAO customerPhoneNumberDAO;
	private CustomerEMailDAO customerEMailDAO;
	private CustomerEmploymentDetailDAO customerEmploymentDetailDAO;
	private FinanceDetailService financeDetailService;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private ChequeDetailDAO chequeDetailDAO;
	private MandateDAO mandateDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private BounceReasonDAO bounceReasonDAO;
	private GuarantorDetailDAO guarantorDetailDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private FinServiceInstrutionDAO finServiceInstrutionDAO;

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
		CustomerDetails cd = new CustomerDetails();

		CountDownLatch latch = new CountDownLatch(5);

		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor("getCustomerDetails");

		CustomerDataLoader cdLoader = new CustomerDataLoader(latch, custID, cd);
		cdLoader.setCustomerDAO(customerDAO);
		taskExecutor.execute(cdLoader);

		AddressDataLoader addressLoader = new AddressDataLoader(latch, custID, cd);
		addressLoader.setCustomerAddresDAO(customerAddresDAO);
		taskExecutor.execute(addressLoader);

		PhoneNumberDataLoader phoneDataLoader = new PhoneNumberDataLoader(latch, custID, cd);
		phoneDataLoader.setCustomerPhoneNumberDAO(customerPhoneNumberDAO);
		taskExecutor.execute(phoneDataLoader);

		EmailDataLoader emailDataLoader = new EmailDataLoader(latch, custID, cd);
		emailDataLoader.setCustomerEMailDAO(customerEMailDAO);
		taskExecutor.execute(emailDataLoader);

		EmploymentDataLoader employmentDataLoader = new EmploymentDataLoader(latch, custID, cd);
		employmentDataLoader.setCustomerEmploymentDetailDAO(customerEmploymentDetailDAO);
		taskExecutor.execute(employmentDataLoader);

		try {
			latch.await();
		} catch (InterruptedException e) {

		}

		cd.setCustomer(cd.getCustomer());
		cd.setAddressList(cd.getAddressList().stream()
				.sorted(Comparator.comparingInt(CustomerAddres::getCustAddrPriority).reversed())
				.collect(Collectors.toList()));
		cd.setCustomerPhoneNumList(cd.getCustomerPhoneNumList().stream()
				.sorted(Comparator.comparingInt(CustomerPhoneNumber::getPhoneTypePriority).reversed())
				.collect(Collectors.toList()));
		cd.setCustomerEMailList(cd.getCustomerEMailList().stream()
				.sorted(Comparator.comparingInt(CustomerEMail::getCustEMailPriority).reversed())
				.collect(Collectors.toList()));
		cd.setEmploymentDetailsList(cd.getEmploymentDetailsList());
		return cd;
	}

	public FinanceDetail getLoanDetails(Long finID) {
		FinanceDetail fd = new FinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();

		CountDownLatch latch = new CountDownLatch(3);

		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor("getLoanDetails");

		FMDataLoader fmDataLoader = new FMDataLoader(latch, finID, fd);
		fmDataLoader.setFinanceMainDAO(financeMainDAO);
		taskExecutor.execute(fmDataLoader);

		ScheduleDataLoader scheduleDataLoader = new ScheduleDataLoader(latch, finID, fd);
		scheduleDataLoader.setFinanceScheduleDetailDAO(financeScheduleDetailDAO);
		taskExecutor.execute(scheduleDataLoader);

		ProfitDetailDataLoader profitDataLoader = new ProfitDetailDataLoader(latch, finID, fd);
		profitDataLoader.setFinanceProfitDetailDAO(financeProfitDetailDAO);
		taskExecutor.execute(profitDataLoader);

		try {
			latch.await();
		} catch (InterruptedException e) {
			logger.warn("Interrupted!", e);
			Thread.currentThread().interrupt();
		}

		FinanceMain fm = schdData.getFinanceMain();
		FinanceProfitDetail fpd = schdData.getFinPftDeatil();

		if (fm == null) {
			fm = new FinanceMain();
		}

		Customer c = customerDAO.getBasicDetails(fm.getCustID(), TableType.MAIN_TAB);
		fm.setLoanName(CustomerUtil.getCustomerFullName(c));
		fm.setCustDOB(c.getCustDOB());

		schdData.setFinanceMain(fm);

		FinanceType ft = new FinanceType();

		ft.setFinType(fm.getFinType());

		schdData.setFinanceType(ft);

		schdData.setFinanceScheduleDetails(schdData.getFinanceScheduleDetails());

		if (fpd == null) {
			fpd = new FinanceProfitDetail();
		}

		schdData.setFinPftDeatil(fpd);

		schdData.setFinanceSummary(getLoanSummary(fd));
		return fd;
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

	private FinanceSummary getLoanSummary(FinanceDetail fd) {
		FinanceSummary summary = summaryDetailService.getFinanceSummary(fd);

		Date appDate = SysParamUtil.getAppDate();
		Date businessDate = appDate;
		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();
		List<FinanceScheduleDetail> schedules = fd.getFinScheduleData().getFinanceScheduleDetails();

		if (appDate.compareTo(fm.getMaturityDate()) >= 0) {
			businessDate = DateUtil.addDays(fm.getMaturityDate(), -1);
		}

		FinanceScheduleDetail curSchd = SchdUtil.getNextInstalment(businessDate, schedules);

		if (curSchd != null) {
			summary.setInstallmentNo(curSchd.getInstNumber());
			summary.setLoanEMI(curSchd.getRepayAmount());
			summary.setDueDate(curSchd.getSchDate());
			summary.setLoanTotPrincipal(curSchd.getPrincipalSchd());
			summary.setLoanTotInterest(curSchd.getProfitSchd());
			summary.setDueDate(curSchd.getSchDate());
		}

		summary.setTotalPriSchd(SchdUtil.getTotalPrincipalSchd(schedules));
		summary.setForeClosureAmount(getForeClosureAmount(fd));
		summary.setFutureInst(SchdUtil.getFutureInstalments(appDate, schedules));
		summary.setLastInstDate(schedules.get(schedules.size() - 1).getSchDate());
		summary.setVehicleNo("");
		summary.setMigratedNo("");
		summary.setFinCurODDays(fd.getFinScheduleData().getFinPftDeatil().getCurODDays());

		return summary;
	}

	private BigDecimal getForeClosureAmount(FinanceDetail fd) {
		ReceiptDTO receiptDTO = financeDetailService.prepareReceiptDTO(fd);

		return LoanClosureCalculator.computeClosureAmount(receiptDTO, true);
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Autowired
	public void setSummaryDetailService(SummaryDetailService summaryDetailService) {
		this.summaryDetailService = summaryDetailService;
	}

	@Autowired
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	@Autowired
	public void setCustomerAddresDAO(CustomerAddresDAO customerAddresDAO) {
		this.customerAddresDAO = customerAddresDAO;
	}

	@Autowired
	public void setCustomerPhoneNumberDAO(CustomerPhoneNumberDAO customerPhoneNumberDAO) {
		this.customerPhoneNumberDAO = customerPhoneNumberDAO;
	}

	@Autowired
	public void setCustomerEMailDAO(CustomerEMailDAO customerEMailDAO) {
		this.customerEMailDAO = customerEMailDAO;
	}

	@Autowired
	public void setCustomerEmploymentDetailDAO(CustomerEmploymentDetailDAO customerEmploymentDetailDAO) {
		this.customerEmploymentDetailDAO = customerEmploymentDetailDAO;
	}

	@Autowired
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	@Autowired
	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
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
}
