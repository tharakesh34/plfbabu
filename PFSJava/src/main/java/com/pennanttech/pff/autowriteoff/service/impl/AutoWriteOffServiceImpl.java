package com.pennanttech.pff.autowriteoff.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.ReceiptCalculator;
import com.pennant.backend.dao.applicationmaster.LoanTypeWriteOffDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.psl.PSLDetailDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.endofday.main.PFSBatchAdmin;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinTypeWriteOff;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceWriteoff;
import com.pennant.backend.model.finance.FinanceWriteoffHeader;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.psl.PSLDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.finance.FinanceWriteoffService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.pff.excess.ExcessHead;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.autowriteoff.dao.AutoWriteOffDAO;
import com.pennanttech.pff.autowriteoff.model.AutoWriteOffLoan;
import com.pennanttech.pff.autowriteoff.service.AutoWriteOffService;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.core.util.SchdUtil;
import com.pennanttech.pff.receipt.constants.AllocationType;
import com.pennattech.pff.receipt.model.ReceiptDTO;

public class AutoWriteOffServiceImpl implements AutoWriteOffService {
	protected static final Logger logger = LogManager.getLogger(AutoWriteOffServiceImpl.class);

	private AutoWriteOffDAO autoWriteOffDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceTypeDAO financeTypeDAO;
	private ReceiptCalculator receiptCalculator;
	private FinanceProfitDetailDAO profitDetailsDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private CustomerDAO customerDAO;
	private ReceiptService receiptService;
	private FinanceWriteoffService financeWriteoffService;
	private PSLDetailDAO pSLDetailDAO;
	private LoanTypeWriteOffDAO loanTypeWriteOffDAO;

	@Override
	public long prepareQueueForEOM() {
		autoWriteOffDAO.deleteQueue();
		return autoWriteOffDAO.prepareQueueForEOM();
	}

	@Override
	public long getQueueCount() {
		return autoWriteOffDAO.getQueueCount();
	}

	@Override
	public int updateThreadID(long from, long to, int i) {
		return autoWriteOffDAO.updateThreadID(from, to, i);
	}

	@Override
	public void updateProgress(long finID, int progressInProcess) {
		autoWriteOffDAO.updateProgress(finID, progressInProcess);
	}

	@Override
	public AutoWriteOffLoan processReceipts(long finID, Date appDate, FeeType feeType, String schdMethod) {
		logger.debug(Literal.ENTERING);

		PSLDetail pslDetail = pSLDetailDAO.getPSLDetail(finID, "");

		AutoWriteOffLoan awl = null;

		if (pslDetail == null || StringUtils.isEmpty(pslDetail.getCategoryCode())) {
			awl = new AutoWriteOffLoan();
			awl.setFinID(finID);
			awl.setCode("AWL_001");
			logger.debug(Literal.LEAVING);
			return awl;
		}

		FinanceMain fm = financeMainDAO.getFinanceMainById(finID, "", false);
		List<FinTypeWriteOff> pslCodesList = loanTypeWriteOffDAO.getLoanWriteOffMappingListByLoanType(fm.getFinType(),
				"");

		// Loan type PSL Categories not available in the Loan type Write off Master
		if (CollectionUtils.isEmpty(pslCodesList)) {
			awl = new AutoWriteOffLoan();
			awl.setFinID(finID);
			awl.setFinReference(fm.getFinReference());
			awl.setCode("AWL_002");
			logger.debug(Literal.LEAVING);
			return awl;
		}

		if (!isValid(pslCodesList, pslDetail.getCategoryCode(), finID)) {
			awl = new AutoWriteOffLoan();
			awl.setFinID(finID);
			awl.setFinReference(fm.getFinReference());
			awl.setCode("AWL_003");
			logger.debug(Literal.LEAVING);
			return awl;
		}

		List<ManualAdvise> payables = manualAdviseDAO.getPaybleAdvises(finID, "");
		List<FinExcessAmount> excessDetails = finExcessAmountDAO.getExcessList(finID);

		if (CollectionUtils.isEmpty(payables) && CollectionUtils.isEmpty(excessDetails)) {
			return awl;
		}

		FinanceType ft = financeTypeDAO.getFinanceType(fm.getFinType());
		Customer customerEOD = customerDAO.getCustomerEOD(fm.getCustID());
		FinanceProfitDetail pd = profitDetailsDAO.getFinProfitDetailsById(fm.getFinID());
		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getSchedulesForLMSEvent(fm.getFinID());
		List<ManualAdvise> advises = manualAdviseDAO.getReceivableAdvises(fm.getFinID(), appDate, "_AView");

		FinScheduleData schdData = new FinScheduleData();
		schdData.setFinanceMain(fm);
		schdData.setFinanceScheduleDetails(schedules);
		schdData.setFinanceType(ft);

		List<FinODDetails> odList = receiptCalculator.getValueDatePenalties(schdData, BigDecimal.ZERO, appDate, null,
				true, schedules);

		ReceiptDTO receiptDTO = new ReceiptDTO();
		receiptDTO.setFinanceMain(fm);
		receiptDTO.setSchedules(schedules);
		receiptDTO.setManualAdvises(advises);
		receiptDTO.setRoundAdjMth(schdMethod);
		receiptDTO.setLppFeeType(feeType);
		receiptDTO.setFinType(ft);
		receiptDTO.setValuedate(appDate);
		receiptDTO.setPostDate(appDate);
		receiptDTO.setProfitDetail(pd);
		receiptDTO.setCustomer(customerEOD);
		receiptDTO.setOdDetails(odList);
		receiptDTO.setAppDate(appDate);

		for (ManualAdvise payAdv : payables) {
			BigDecimal balAmount = payAdv.getAdviseAmount().subtract(payAdv.getPaidAmount());
			if (balAmount.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			awl = createReceipt(schdData, null, payAdv, receiptDTO, awl);
			if (awl != null && awl.getCode() != null) {
				logger.debug(Literal.LEAVING);
				return awl;
			}
		}

		for (FinExcessAmount excess : excessDetails) {
			String amountType = excess.getAmountType();

			if (!(ExcessHead.isEmiInAdv(amountType) || ExcessHead.isExcess(amountType) || ExcessHead.isDsf(amountType)
					|| ExcessHead.isCashclt(amountType))) {
				continue;
			}

			awl = createReceipt(schdData, excess, null, receiptDTO, awl);
			if (awl != null && awl.getCode() != null) {
				logger.debug(Literal.LEAVING);
				return awl;
			}
		}

		logger.debug(Literal.LEAVING);
		return awl;
	}

	private boolean isValid(List<FinTypeWriteOff> writeOffList, String code, long finID) {
		int curOddays = profitDetailsDAO.getCurOddays(finID);

		for (FinTypeWriteOff ftw : writeOffList) {
			if (StringUtils.equals(code, ftw.getPslCode()) && curOddays >= ftw.getDpdDays()) {
				return true;
			}
		}

		return false;
	}

	private AutoWriteOffLoan createReceipt(FinScheduleData schdData, FinExcessAmount excess, ManualAdvise advise,
			ReceiptDTO receiptDTO, AutoWriteOffLoan awl) {
		FinServiceInstruction fsi = prepareFinInstruction(schdData, advise, excess, receiptDTO);

		FinanceDetail fd = receiptService.receiptTransaction(fsi);

		List<ErrorDetail> errors = fd.getFinScheduleData().getErrorDetails();

		if (CollectionUtils.isEmpty(errors)) {
			return awl;
		}

		ErrorDetail error = errors.get(0);
		FinanceMain fm = schdData.getFinanceMain();

		awl = new AutoWriteOffLoan();
		awl.setFinID(fm.getFinID());
		awl.setFinReference(fm.getFinReference());
		awl.setCode(error.getCode());
		awl.setErrorMsg(error.getMessage());

		return awl;
	}

	private FinServiceInstruction prepareFinInstruction(FinScheduleData schdData, ManualAdvise ma, FinExcessAmount fea,
			ReceiptDTO receiptDTO) {

		BigDecimal receiptAmt = BigDecimal.ZERO;
		String receiptMode = null;
		long payAgainstID = 0;

		if (ma != null) {
			receiptAmt = ma.getBalanceAmt();
			payAgainstID = ma.getAdviseID();
			receiptMode = "PAYABLE";
		} else if (fea != null) {
			receiptAmt = fea.getBalanceAmt();
			payAgainstID = fea.getExcessID();
			receiptMode = ExcessHead.valueOf(fea.getAmountType()).name();
		}

		FinanceMain fm = schdData.getFinanceMain();
		fm.setAppDate(fm.getEventProperties().getAppDate());

		Date appDate = receiptDTO.getAppDate();

		FinServiceInstruction fsi = new FinServiceInstruction();

		fsi.setFinReference(fm.getFinReference());
		fsi.setFinID(fm.getFinID());

		fsi.setFromDate(appDate);
		fsi.setAmount(receiptAmt);

		fsi.setPaymentMode(receiptMode);
		fsi.setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_EXCESS);
		fsi.setPanNumber(receiptDTO.getCustomer().getCustCRCPR());
		fsi.setReqType("Post");
		fsi.setNonStp(true);
		fsi.setRequestSource(RequestSource.EOD);
		fsi.setReceiptPurpose(FinServiceEvent.SCHDRPY);
		fsi.setValueDate(appDate);
		fsi.setLoggedInUser(PFSBatchAdmin.loggedInUser);
		fsi.setAllocationType(AllocationType.AUTO);

		FinReceiptDetail rcd = new FinReceiptDetail();
		rcd.setReceivedDate(appDate);
		rcd.setValueDate(appDate);
		rcd.setPayAgainstID(payAgainstID);
		rcd.setNoReserve(true);
		fsi.setReceiptDetail(rcd);

		FinReceiptDetail rd = fsi.getReceiptDetail();

		if (fsi.getValueDate() == null) {
			fsi.setValueDate(rd.getReceivedDate());
			rd.setValueDate(rd.getReceivedDate());
		}

		if (fsi.getReceiptDetail().getReceivedDate() == null) {
			fsi.getReceiptDetail().setReceivedDate(appDate);
		}

		fsi.setReceivedDate(rd.getReceivedDate());
		fsi.setNewReceipt(true);
		fsi.setRealizationDate(appDate);

		return fsi;
	}

	@Override
	public String prepareWriteOff(long finID, Date appDate) {
		logger.debug(Literal.ENTERING);
		FinanceWriteoffHeader header = financeWriteoffService.getFinanceWriteoffDetailById(finID, "_View", null,
				FinServiceEvent.WRITEOFFPAY);

		setWriteOffTotals(header);
		header.setFinSource(UploadConstants.FINSOURCE_ID_AUTOPROCESS);

		AuditHeader auditHeader = getAuditHeader(header, PennantConstants.TRAN_WF);
		this.financeWriteoffService.doApprove(auditHeader);

		logger.debug(Literal.LEAVING);
		return header.getFinReference();
	}

	private void setWriteOffTotals(FinanceWriteoffHeader header) {
		logger.debug(Literal.ENTERING);
		FinanceWriteoff fw = header.getFinanceWriteoff();

		if (fw.getWriteoffPrincipal().compareTo(BigDecimal.ZERO) == 0
				&& fw.getWriteoffProfit().compareTo(BigDecimal.ZERO) == 0
				&& fw.getWriteoffSchFee().compareTo(BigDecimal.ZERO) == 0) {

			fw.setWriteoffPrincipal(fw.getUnPaidSchdPri());
			fw.setWriteoffProfit(fw.getUnPaidSchdPft());
			fw.setWriteoffSchFee(fw.getUnpaidSchFee());
		}

		try {
			calScheduleWriteOffDetails(header);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);

	}

	private void calScheduleWriteOffDetails(FinanceWriteoffHeader header) {
		FinanceWriteoff fw = header.getFinanceWriteoff();
		FinanceDetail fd = header.getFinanceDetail();

		BigDecimal prinicipalAmt = fw.getWriteoffPrincipal();
		BigDecimal profitAmt = fw.getWriteoffProfit();
		BigDecimal feeAmount = fw.getWriteoffSchFee();

		List<FinanceScheduleDetail> schedules = fd.getFinScheduleData().getFinanceScheduleDetails();

		if (CollectionUtils.isEmpty(schedules)) {
			return;
		}

		for (FinanceScheduleDetail schedule : schedules) {
			prinicipalAmt = prepareWriteOffPri(schedule, prinicipalAmt);
			profitAmt = prepareWriteOffPft(schedule, profitAmt);
			feeAmount = prepareWriteOffFee(schedule, feeAmount);
		}

	}

	private BigDecimal prepareWriteOffPri(FinanceScheduleDetail schedule, BigDecimal woPriAmt) {
		BigDecimal schPriBal = SchdUtil.principalBal(schedule);

		if (schPriBal.compareTo(BigDecimal.ZERO) <= 0) {
			return woPriAmt;
		}

		if (woPriAmt.compareTo(schPriBal) >= 0) {
			schedule.setWriteoffPrincipal(schedule.getWriteoffPrincipal().add(schPriBal));
			return woPriAmt.subtract(schPriBal);
		}

		schedule.setWriteoffPrincipal(schedule.getWriteoffPrincipal().add(woPriAmt));
		return BigDecimal.ZERO;
	}

	private BigDecimal prepareWriteOffPft(FinanceScheduleDetail schedule, BigDecimal woPftAmt) {
		BigDecimal schPftBal = SchdUtil.profitBal(schedule);

		if (schPftBal.compareTo(BigDecimal.ZERO) <= 0) {
			return woPftAmt;
		}

		if (woPftAmt.compareTo(schPftBal) >= 0) {
			schedule.setWriteoffProfit(schedule.getWriteoffProfit().add(schPftBal));
			return woPftAmt.subtract(schPftBal);
		}

		schedule.setWriteoffProfit(schedule.getWriteoffProfit().add(woPftAmt));
		return BigDecimal.ZERO;
	}

	private BigDecimal prepareWriteOffFee(FinanceScheduleDetail schedule, BigDecimal woSchFee) {
		BigDecimal schFee = SchdUtil.feeBal(schedule);

		if (schFee.compareTo(BigDecimal.ZERO) <= 0) {
			return woSchFee;
		}

		if (woSchFee.compareTo(schFee) >= 0) {
			schedule.setWriteoffSchFee(schedule.getWriteoffSchFee().add(schFee));
			return woSchFee.subtract(schFee);
		}

		schedule.setWriteoffSchFee(schedule.getWriteoffSchFee().add(woSchFee));
		return BigDecimal.ZERO;
	}

	private AuditHeader getAuditHeader(FinanceWriteoffHeader header, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, header);
		return new AuditHeader(String.valueOf(header.getFinReference()), String.valueOf(header.getFinReference()), null,
				null, auditDetail, header.getFinanceDetail().getFinScheduleData().getFinanceMain().getUserDetails(),
				new HashMap<>());
	}

	@Override
	public void insertlog(AutoWriteOffLoan awl) {
		autoWriteOffDAO.insertlog(awl);

	}

	public void setAutoWriteOffDAO(AutoWriteOffDAO autoWriteOffDAO) {
		this.autoWriteOffDAO = autoWriteOffDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	public void setFinanceWriteoffService(FinanceWriteoffService financeWriteoffService) {
		this.financeWriteoffService = financeWriteoffService;
	}

	public void setpSLDetailDAO(PSLDetailDAO pSLDetailDAO) {
		this.pSLDetailDAO = pSLDetailDAO;
	}

	public void setLoanTypeWriteOffDAO(LoanTypeWriteOffDAO loanTypeWriteOffDAO) {
		this.loanTypeWriteOffDAO = loanTypeWriteOffDAO;
	}

}
