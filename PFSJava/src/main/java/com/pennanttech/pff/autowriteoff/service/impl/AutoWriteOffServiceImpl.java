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
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.autowriteoff.dao.AutoWriteOffDAO;
import com.pennanttech.pff.autowriteoff.model.AutoWriteOffLoan;
import com.pennanttech.pff.autowriteoff.service.AutoWriteOffService;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.receipt.constants.AllocationType;
import com.pennanttech.pff.receipt.constants.ReceiptMode;
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

		// Loan has does not the PSL category
		PSLDetail pslDetail = pSLDetailDAO.getPSLDetail(finID, "");

		AutoWriteOffLoan awl = null;
		if (pslDetail == null || StringUtils.isEmpty(pslDetail.getCategoryCode())) {
			awl = new AutoWriteOffLoan();
			awl.setFinID(finID);
			awl.setFinRef(pslDetail.getFinReference());
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
			awl.setFinRef(fm.getFinReference());
			awl.setCode("AWL_002");
			logger.debug(Literal.LEAVING);
			return awl;
		}

		boolean isRcdFound = false;
		int curOddays = profitDetailsDAO.getCurOddays(finID);
		for (FinTypeWriteOff finTypeWrite : pslCodesList) {
			if (StringUtils.equals(pslDetail.getCategoryCode(), finTypeWrite.getPslCode())
					&& curOddays >= finTypeWrite.getDpdDays()) {
				isRcdFound = true;
				break;
			}
		}

		// Loan type PSL Categories or DPD Day not matches with Loan type Write off Master
		if (!isRcdFound) {
			awl = new AutoWriteOffLoan();
			awl.setFinID(finID);
			awl.setFinRef(fm.getFinReference());
			awl.setCode("AWL_003");
			logger.debug(Literal.LEAVING);
			return awl;
		}

		// **********************************************************
		// TILL THIS AUTO WRITEOFF ELIGIBILITY VERIFIED AND LOAN IS ELIGIBLE FOR FURTHER PROCESS
		// **********************************************************

		List<ManualAdvise> payAdvList = manualAdviseDAO.getPaybleAdvises(finID, "");
		List<FinExcessAmount> excessList = null;

		// IF No Excess or Payables against Loan , No receipt creation required
		boolean excessFetched = false;
		if (CollectionUtils.isEmpty(payAdvList)) {
			excessList = finExcessAmountDAO.getExcessList(finID);
			excessFetched = true;
			if (CollectionUtils.isEmpty(excessList)) {
				return awl;
			}
		}

		FinScheduleData schdData = new FinScheduleData();
		FinanceType ft = financeTypeDAO.getFinanceType(fm.getFinType());
		Customer customerEOD = customerDAO.getCustomerEOD(fm.getCustID());
		FinanceProfitDetail pd = null;
		List<FinanceScheduleDetail> schdList = null;

		// Payable List
		if (CollectionUtils.isNotEmpty(payAdvList)) {
			for (int i = 0; i < payAdvList.size(); i++) {

				ManualAdvise payAdv = payAdvList.get(i);
				BigDecimal balAmount = payAdv.getAdviseAmount().subtract(payAdv.getPaidAmount());
				if (balAmount.compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}

				if (i != 0) {
					fm = financeMainDAO.getFinanceMainByRef(fm.getFinReference(), "", false);
				}
				pd = profitDetailsDAO.getFinProfitDetailsById(fm.getFinID());
				schdList = financeScheduleDetailDAO.getFinSchedules(fm.getFinID(), TableType.MAIN_TAB);

				schdData.setFinanceMain(fm);
				schdData.setFinanceScheduleDetails(schdList);
				schdData.setFinanceType(ft);

				ReceiptDTO receiptDTO = new ReceiptDTO();

				receiptDTO.setFinanceMain(fm);
				receiptDTO.setSchedules(schdList);

				List<FinODDetails> odList = receiptCalculator.getValueDatePenalties(schdData, BigDecimal.ZERO, appDate,
						null, true, schdList);
				receiptDTO.setOdDetails(odList);
				receiptDTO.setManualAdvises(manualAdviseDAO.getReceivableAdvises(fm.getFinID(), appDate, "_AView"));
				receiptDTO.setRoundAdjMth(schdMethod);
				receiptDTO.setLppFeeType(feeType);
				receiptDTO.setFinType(ft);
				receiptDTO.setValuedate(appDate);
				receiptDTO.setPostDate(appDate);
				receiptDTO.setProfitDetail(pd);
				receiptDTO.setCustomer(customerEOD);

				// Prepare Receipt
				awl = createReceipt(schdData, null, payAdv, receiptDTO, awl, appDate);

				if (awl != null && awl.getCode() != null) {
					logger.debug(Literal.LEAVING);
					return awl;
				}
			}
		}

		// Excess List
		if (!excessFetched) {
			excessList = finExcessAmountDAO.getExcessList(finID);
		}

		if (CollectionUtils.isNotEmpty(excessList)) {
			for (int j = 0; j < excessList.size(); j++) {

				FinExcessAmount excess = excessList.get(j);

				if (!"A".equals(excess.getAmountType()) || !"E".equals(excess.getAmountType())
						|| !ReceiptMode.DSF.equals(excess.getAmountType())
						|| !ReceiptMode.CASHCLT.equals(excess.getAmountType())) {
					continue;
				}

				fm = financeMainDAO.getFinanceMainByRef(fm.getFinReference(), "", false);
				pd = profitDetailsDAO.getFinProfitDetailsById(fm.getFinID());
				schdList = financeScheduleDetailDAO.getFinSchedules(fm.getFinID(), TableType.MAIN_TAB);
				schdData.setFinanceMain(fm);
				schdData.setFinanceScheduleDetails(schdList);
				schdData.setFinanceType(ft);

				ReceiptDTO receiptDTO = new ReceiptDTO();
				receiptDTO.setFinanceMain(fm);
				receiptDTO.setSchedules(schdList);
				List<FinODDetails> odList = receiptCalculator.getValueDatePenalties(schdData, BigDecimal.ZERO, appDate,
						null, true, schdList);
				receiptDTO.setOdDetails(odList);

				receiptDTO.setManualAdvises(manualAdviseDAO.getReceivableAdvises(fm.getFinID(), appDate, "_AView"));
				receiptDTO.setRoundAdjMth(schdMethod);

				receiptDTO.setLppFeeType(feeType);
				receiptDTO.setFinType(ft);
				receiptDTO.setValuedate(appDate);
				receiptDTO.setPostDate(appDate);
				receiptDTO.setProfitDetail(pd);
				receiptDTO.setCustomer(customerEOD);

				// Prepare Receipt
				awl = createReceipt(schdData, excess, null, receiptDTO, awl, appDate);

				if (awl != null && awl.getCode() != null) {
					logger.debug(Literal.LEAVING);
					return awl;
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return awl;
	}

	/**
	 * Method for Processing or Creating Receipt against Excess/ payable to avoid Writeoff
	 * 
	 * @param schdData
	 * @param excess
	 * @param payAdv
	 * @param receiptDTO
	 * @param awl
	 * @param appDate
	 * @return
	 */
	private AutoWriteOffLoan createReceipt(FinScheduleData schdData, FinExcessAmount excess, ManualAdvise payAdv,
			ReceiptDTO receiptDTO, AutoWriteOffLoan awl, Date appDate) {
		logger.debug(Literal.ENTERING);

		// Prepare Fin instruction
		FinServiceInstruction fsi = prepareFinInstruction(schdData, payAdv, excess, receiptDTO, appDate);

		// calling receipt creation
		FinanceDetail fd = receiptService.receiptTransaction(fsi);

		// if error
		if (CollectionUtils.isNotEmpty(fd.getFinScheduleData().getErrorDetails())) {
			awl = new AutoWriteOffLoan();
			awl.setFinID(schdData.getFinanceMain().getFinID());
			awl.setFinRef(schdData.getFinanceMain().getFinReference());
			awl.setCode(fd.getFinScheduleData().getErrorDetails().get(0).getCode());
			awl.setErrorMsg(fd.getFinScheduleData().getErrorDetails().get(0).getMessage());
		}

		logger.debug(Literal.LEAVING);
		return awl;
	}

	/**
	 * Prepare Service Instruction to process Receipt Creation
	 * 
	 * @param schdData
	 * @param paybleAdvise
	 * @param finExcessAmount
	 * @param receiptDTO
	 * @param appDate
	 * @return
	 */
	private FinServiceInstruction prepareFinInstruction(FinScheduleData schdData, ManualAdvise paybleAdvise,
			FinExcessAmount finExcessAmount, ReceiptDTO receiptDTO, Date appDate) {
		logger.debug(Literal.ENTERING);

		BigDecimal receiptAmt = BigDecimal.ZERO;
		String receiptMode = null;
		long payAgainstID = 0;

		if (paybleAdvise != null) {

			receiptAmt = paybleAdvise.getBalanceAmt();
			payAgainstID = paybleAdvise.getAdviseID();
			receiptMode = "PAYABLE";

		} else if (finExcessAmount != null) {

			receiptAmt = finExcessAmount.getBalanceAmt();
			payAgainstID = finExcessAmount.getExcessID();

			if ("A".equals(finExcessAmount.getAmountType())) {
				receiptMode = "EMIINADV";
			} else if ("DSF".equals(finExcessAmount.getAmountType())) {
				receiptMode = "DSF";
			} else if ("CASHCLT".equals(finExcessAmount.getAmountType())) {
				receiptMode = "CASHCLT";
			} else {
				receiptMode = "EXCESS";
			}
		}

		FinanceMain fm = schdData.getFinanceMain();
		fm.setAppDate(fm.getEventProperties().getAppDate());

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

		logger.debug(Literal.LEAVING);
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

		List<FinanceScheduleDetail> financeScheduleDetails;
		try {
			financeScheduleDetails = calScheduleWriteOffDetails(header);
			header.getFinanceDetail().getFinScheduleData().setFinanceScheduleDetails(financeScheduleDetails);
		} catch (Exception e) {
		}
		logger.debug(Literal.LEAVING);

	}

	private List<FinanceScheduleDetail> calScheduleWriteOffDetails(FinanceWriteoffHeader financeWriteoffHeader) {
		logger.debug("Entering");

		FinanceWriteoff fw = financeWriteoffHeader.getFinanceWriteoff();

		BigDecimal woPriAmt = fw.getWriteoffPrincipal();
		BigDecimal woPftAmt = fw.getWriteoffProfit();
		BigDecimal woSchFee = fw.getWriteoffSchFee();

		List<FinanceScheduleDetail> effectedFinSchDetails = financeWriteoffHeader.getFinanceDetail()
				.getFinScheduleData().getFinanceScheduleDetails();

		if (effectedFinSchDetails != null && effectedFinSchDetails.size() > 0) {
			for (int i = 0; i < effectedFinSchDetails.size(); i++) {

				FinanceScheduleDetail curSchdl = effectedFinSchDetails.get(i);

				// Reset Write-off Principal Amount
				if (woPriAmt.compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal schPriBal = curSchdl.getPrincipalSchd().subtract(curSchdl.getSchdPriPaid())
							.subtract(curSchdl.getWriteoffPrincipal());
					if (schPriBal.compareTo(BigDecimal.ZERO) > 0) {
						if (woPriAmt.compareTo(schPriBal) >= 0) {
							curSchdl.setWriteoffPrincipal(curSchdl.getWriteoffPrincipal().add(schPriBal));
							woPriAmt = woPriAmt.subtract(schPriBal);
						} else {
							curSchdl.setWriteoffPrincipal(curSchdl.getWriteoffPrincipal().add(woPriAmt));
							woPriAmt = BigDecimal.ZERO;
						}
					}
				}
				// Reset Write-off Profit Amount
				if (woPftAmt.compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal schPftBal = curSchdl.getProfitSchd().subtract(curSchdl.getSchdPftPaid())
							.subtract(curSchdl.getWriteoffProfit());
					if (schPftBal.compareTo(BigDecimal.ZERO) > 0) {
						if (woPftAmt.compareTo(schPftBal) >= 0) {
							curSchdl.setWriteoffProfit(curSchdl.getWriteoffProfit().add(schPftBal));
							woPftAmt = woPftAmt.subtract(schPftBal);
						} else {
							curSchdl.setWriteoffProfit(curSchdl.getWriteoffProfit().add(woPftAmt));
							woPftAmt = BigDecimal.ZERO;
						}
					}
				}

				// Reset Write-off Schedule Fee
				if (woSchFee.compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal schFee = curSchdl.getFeeSchd()
							.subtract(curSchdl.getSchdFeePaid().subtract(curSchdl.getWriteoffSchFee()));
					if (schFee.compareTo(BigDecimal.ZERO) > 0) {
						if (woSchFee.compareTo(schFee) >= 0) {
							curSchdl.setWriteoffSchFee(curSchdl.getWriteoffSchFee().add(schFee));
							woSchFee = woSchFee.subtract(schFee);
						} else {
							curSchdl.setWriteoffSchFee(curSchdl.getWriteoffSchFee().add(woSchFee));
							woSchFee = BigDecimal.ZERO;
						}
					}
				}

			}
		}
		return effectedFinSchDetails;
	}

	private AuditHeader getAuditHeader(FinanceWriteoffHeader header, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, header);
		return new AuditHeader(String.valueOf(header.getFinReference()), String.valueOf(header.getFinReference()), null,
				null, auditDetail, header.getFinanceDetail().getFinScheduleData().getFinanceMain().getUserDetails(),
				new HashMap<String, List<ErrorDetail>>());
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
