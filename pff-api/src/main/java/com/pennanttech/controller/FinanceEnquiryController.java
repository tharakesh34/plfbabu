package com.pennanttech.controller;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.customermasters.CustomerAddresDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.customermasters.CustomerEMailDAO;
import com.pennant.backend.dao.customermasters.CustomerEmploymentDetailDAO;
import com.pennant.backend.dao.customermasters.CustomerPhoneNumberDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSummary;
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
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
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

}
