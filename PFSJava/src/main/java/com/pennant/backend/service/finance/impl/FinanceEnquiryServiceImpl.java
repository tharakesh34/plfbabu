package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import com.pennant.app.util.FeeCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeFeesDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.customermasters.CustomerEnquiryService;
import com.pennant.backend.service.finance.FinanceEnquiryService;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.core.loan.util.LoanClosureCalculator;
import com.pennant.pff.data.loader.FMDataLoader;
import com.pennant.pff.data.loader.ProfitDetailDataLoader;
import com.pennant.pff.data.loader.ScheduleDataLoader;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.CustomerUtil;
import com.pennanttech.pff.core.util.SchdUtil;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennattech.pff.receipt.model.ReceiptDTO;

public class FinanceEnquiryServiceImpl implements FinanceEnquiryService {
	private static final Logger logger = LogManager.getLogger(FinanceEnquiryServiceImpl.class);

	private FinanceMainDAO financeMainDAO;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private CustomerDAO customerDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private FeeTypeDAO feeTypeDAO;
	private FinTypeFeesDAO finTypeFeesDAO;

	private SummaryDetailService summaryDetailService;
	private CustomerEnquiryService customerEnquiryService;

	private FeeCalculator feeCalculator;

	@Override
	public FinanceDetail getLoanDetails(long finID) {
		logger.debug(Literal.ENTERING);

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

		logger.debug(Literal.LEAVING);

		return fd;
	}

	@Override
	public FinanceDetail getLoanBasicDetails(long finID) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = getLoanDetails(finID);

		long custID = fd.getFinScheduleData().getFinanceMain().getCustID();

		fd.setCustomerDetails(getCustomerDetails(custID));

		logger.debug(Literal.LEAVING);

		return fd;
	}

	public CustomerDetails getCustomerDetails(long custID) {
		return customerEnquiryService.getCustomerDetails(custID);
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
		ReceiptDTO receiptDTO = prepareReceiptDTO(fd);

		return LoanClosureCalculator.computeClosureAmount(receiptDTO, true);
	}

	private ReceiptDTO prepareReceiptDTO(FinanceDetail fd) {
		Date appDate = SysParamUtil.getAppDate();

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceType financeType = schdData.getFinanceType();
		FinanceMain fm = schdData.getFinanceMain();

		ReceiptDTO receiptDTO = new ReceiptDTO();

		FinReceiptData rd = prepareReceiptData(fd);
		feeCalculator.calculateFees(rd);

		receiptDTO.setFinanceMain(fm);
		receiptDTO.setSchedules(schdData.getFinanceScheduleDetails());
		receiptDTO.setOdDetails(schdData.getFinODDetails());
		receiptDTO.setManualAdvises(manualAdviseDAO.getReceivableAdvises(fm.getFinID(), appDate, "_AView"));
		receiptDTO.setFees(rd.getFinanceDetail().getFinScheduleData().getFinFeeDetailList());

		receiptDTO.setRoundAdjMth(SysParamUtil.getValueAsString(SMTParameterConstants.ROUND_ADJ_METHOD));
		receiptDTO.setLppFeeType(feeTypeDAO.getTaxDetailByCode(Allocation.ODC));
		receiptDTO.setFinType(financeType);
		receiptDTO.setValuedate(appDate);

		return receiptDTO;
	}

	private FinReceiptData prepareReceiptData(FinanceDetail fd) {
		FinReceiptData frd = new FinReceiptData();
		FinReceiptHeader frh = new FinReceiptHeader();
		List<CustomerAddres> ca = null;
		CustomerDetails cd = fd.getCustomerDetails();

		if (cd == null) {
			cd = new CustomerDetails();
		}

		ca = cd.getAddressList();

		if (ca == null) {
			ca = new ArrayList<>();
			cd.setAddressList(ca);
		}

		FinScheduleData fsd = fd.getFinScheduleData();

		String finType = fsd.getFinanceType().getFinType();

		fsd.setFeeEvent(AccountingEvent.EARLYSTL);
		fd.setFinTypeFeesList(finTypeFeesDAO.getFinTypeFeesForLMSEvent(finType, AccountingEvent.EARLYSTL));

		frd.setFinanceDetail(fd);
		frd.setTdPriBal(fsd.getFinPftDeatil().getTdSchdPriBal());
		frd.setReceiptHeader(frh);

		return frd;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
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
	public void setSummaryDetailService(SummaryDetailService summaryDetailService) {
		this.summaryDetailService = summaryDetailService;
	}

	@Autowired
	public void setCustomerEnquiryService(CustomerEnquiryService customerEnquiryService) {
		this.customerEnquiryService = customerEnquiryService;
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	@Autowired
	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	@Autowired
	public void setFinTypeFeesDAO(FinTypeFeesDAO finTypeFeesDAO) {
		this.finTypeFeesDAO = finTypeFeesDAO;
	}

	@Autowired
	public void setFeeCalculator(FeeCalculator feeCalculator) {
		this.feeCalculator = feeCalculator;
	}
}
