package com.pennant.app.core;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.eod.PaymentRecoveryService;
import com.pennant.eod.beans.PaymentRecoveryHeader;
import com.pennant.eod.constants.EodSql;

public class ServiceUtil {

	private static Logger			logger	= Logger.getLogger(ServiceUtil.class);

	private RepaymentService		repaymentService;
	private LatePaymentService		latePaymentService;
	private SuspenseService			suspenseService;
	private StatusService			statusService;

	private PaymentRecoveryService	paymentRecoveryService;

	public ServiceUtil() {
		super();
	}

	public void processRepayRequest(Date date, FinRepayQueue finRepayQueue, PaymentRecoveryHeader header)
			throws Exception {

		String rpyMethod = ImplementationConstants.REPAY_HIERARCHY_METHOD;
		String finRef = finRepayQueue.getFinReference();
		FinanceMain financeMain = repaymentService.getFinanceMain(finRef);
		String repaymethod = StringUtils.trimToEmpty(financeMain.getFinRepayMethod());
		String repayAcc = StringUtils.trimToEmpty(financeMain.getRepayAccountId());

		boolean processrecord = false;
		// Repayments Only for "AUTO" Payment Finances
		if ((repaymethod.equals(FinanceConstants.REPAYMTH_AUTO) && StringUtils.isNotEmpty(repayAcc))
				|| repaymethod.equals(FinanceConstants.REPAYMTH_AUTODDA)) {
			processrecord = true;
		}

		if (processrecord) {

			financeMain.setSecondaryAccount(repaymentService.getSecondaryAccounts(finRef));
			if (rpyMethod.equals(RepayConstants.REPAY_HIERARCHY_FCIP)
					|| rpyMethod.equals(RepayConstants.REPAY_HIERARCHY_FCPI)) {

				List<ReturnDataSet> odreturnList = latePaymentService.processLPPenaltyRequest(financeMain,
						finRepayQueue);
				paymentRecoveryService.save(odreturnList, finRepayQueue, header, false);
			}

			List<ReturnDataSet> returnList = repaymentService.processRepayRequest(date, financeMain, finRepayQueue);
			paymentRecoveryService.save(returnList, finRepayQueue, header, false);

			if (rpyMethod.equals(RepayConstants.REPAY_HIERARCHY_FIPC)
					|| rpyMethod.equals(RepayConstants.REPAY_HIERARCHY_FPIC)) {
				List<ReturnDataSet> odreturnList = latePaymentService.processLPPenaltyRequest(financeMain,
						finRepayQueue);
				paymentRecoveryService.save(odreturnList, finRepayQueue, header, false);
			}

			if (rpyMethod.equals(RepayConstants.REPAY_HIERARCHY_FIPCS)
					|| rpyMethod.equals(RepayConstants.REPAY_HIERARCHY_FPICS)) {
				List<ReturnDataSet> odreturnList = latePaymentService.processLPPenaltyRequest(financeMain,
						finRepayQueue);
				paymentRecoveryService.save(odreturnList, finRepayQueue, header, true);
			}

		}

		financeMain = null;
		finRepayQueue = null;

	}

	/**
	 * Helper for Auto Hunting
	 * 
	 * @param scheduleDetail
	 * @param amoutPaid
	 * @return
	 * @throws Exception
	 */
	public List<ReturnDataSet> processAutoHunting(Date date, FinanceScheduleDetail scheduleDetail, BigDecimal amoutPaid)
			throws Exception {
		logger.debug(" Entering ");

		FinanceMain financeMain = repaymentService.getFinanceMain(scheduleDetail.getFinReference());
		FinRepayQueue finRepayQueue = prepareRepayQueue(financeMain, scheduleDetail);

		logger.debug(" Leaving ");
		return processScheduledePayments(date, financeMain, finRepayQueue, amoutPaid);
	}

	/**
	 * Helper for Customer based EOD . will loop through the customer based finance
	 * 
	 * @param custId
	 * @param connection
	 * @param date
	 * @throws Exception
	 */
	public void processQueue(Connection connection, long custId, Date date) throws Exception {
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		try {
			//payments
			sqlStatement = connection.prepareStatement(EodSql.customerRepayQueue);
			sqlStatement.setLong(1, custId);
			resultSet = sqlStatement.executeQuery();

			while (resultSet.next()) {
				BeanPropertyRowMapper<FinRepayQueue> beanPropertyRowMapper = new BeanPropertyRowMapper<>(
						FinRepayQueue.class);
				FinRepayQueue finRepayQueue = beanPropertyRowMapper.mapRow(resultSet, resultSet.getRow());
				processPayments(date, finRepayQueue);
			}

		} catch (Exception e) {
			logger.error("Exception :", e);
			throw e;
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			if (sqlStatement != null) {
				sqlStatement.close();
			}
		}
	}

	/**
	 * Used for No eod where overdue's will be calculated on the payment success or fail
	 * 
	 * @param date
	 * @param finRepay
	 * @param amoutPaid
	 * @return
	 * @throws Exception
	 */
	private List<ReturnDataSet> processPayments(Date date, FinRepayQueue finRepay) throws Exception {
		logger.debug(" Entering ");

		FinanceMain finMain = repaymentService.getFinanceMain(finRepay.getFinReference());

		List<ReturnDataSet> lplist = latePaymentService.processLatePayInEOD(finMain, finRepay, date);
		suspenseService.processSuspense(date, finMain, finRepay);
		statusService.processStatus(date, finRepay);
		List<ReturnDataSet> listToPost = new ArrayList<>();

		if (lplist != null && !lplist.isEmpty()) {
			listToPost.addAll(lplist);
		}
		logger.debug(" Leaving ");
		return listToPost;

	}

	/**
	 * Used for Auto Hunting.where overdue's will calculated based on the payment success or fail
	 * 
	 * @param finMain
	 * @param finRepay
	 * @param totalpaidAmount
	 * @return
	 * @throws Exception
	 */
	private List<ReturnDataSet> processScheduledePayments(Date date, FinanceMain finMain, FinRepayQueue finRepay,
			BigDecimal totalpaidAmount) throws Exception {
		logger.debug(" Entering ");

		List<ReturnDataSet> list = repaymentService.processRepayments(date, finMain, finRepay, totalpaidAmount);
		List<ReturnDataSet> lplist = latePaymentService.processLatePay(finMain, finRepay);
		suspenseService.processSuspense(date, finMain, finRepay);
		statusService.processStatus(date, finRepay);

		List<ReturnDataSet> listToPost = new ArrayList<>();
		if (list != null && !list.isEmpty()) {
			listToPost.addAll(list);
		}

		if (lplist != null && !lplist.isEmpty()) {
			listToPost.addAll(lplist);
		}
		logger.debug(" Leaving ");
		return listToPost;

	}

	private FinRepayQueue prepareRepayQueue(FinanceMain financeMain, FinanceScheduleDetail schd) {

		FinRepayQueue finRpy = new FinRepayQueue();
		finRpy.setFinReference(financeMain.getFinReference());
		finRpy.setBranch(financeMain.getFinBranch());
		finRpy.setFinType(financeMain.getFinType());
		finRpy.setCustomerID(financeMain.getCustID());
		finRpy.setRpyDate(schd.getSchDate());
		finRpy.setFinRpyFor(FinanceConstants.SCH_TYPE_SCHEDULE);
		finRpy.setSchdPft(schd.getProfitSchd());
		finRpy.setSchdPri(schd.getPrincipalSchd());
		finRpy.setSchdPftPaid(schd.getSchdPftPaid());
		finRpy.setSchdPriPaid(schd.getSchdPriPaid());
		finRpy.setSchdPftBal(finRpy.getSchdPft().subtract(finRpy.getSchdPftPaid()));
		finRpy.setSchdPriBal(finRpy.getSchdPri().subtract(finRpy.getSchdPriPaid()));
		if (finRpy.getSchdPftBal().compareTo(BigDecimal.ZERO) == 0) {
			finRpy.setSchdIsPftPaid(true);
		}
		if (finRpy.getSchdPriBal().compareTo(BigDecimal.ZERO) == 0) {
			finRpy.setSchdIsPriPaid(true);
		}

		// Schedule Fee Details
		// 1. Schedule Fee Amount
		finRpy.setSchdFee(schd.getFeeSchd());
		finRpy.setSchdFeePaid(schd.getSchdFeePaid());
		finRpy.setSchdFeeBal(schd.getFeeSchd().subtract(schd.getSchdFeePaid()));

		// 2. Schedule Insurance Amount
		finRpy.setSchdIns(schd.getInsSchd());
		finRpy.setSchdInsPaid(schd.getSchdInsPaid());
		finRpy.setSchdInsBal(schd.getInsSchd().subtract(schd.getSchdInsPaid()));

		// 3. Schedule Supplementary Rent Amount
		finRpy.setSchdSuplRent(schd.getSuplRent());
		finRpy.setSchdSuplRentPaid(schd.getSuplRentPaid());
		finRpy.setSchdSuplRentBal(schd.getSuplRent().subtract(schd.getSuplRentPaid()));

		// 4. Schedule Fee Amount
		finRpy.setSchdIncrCost(schd.getIncrCost());
		finRpy.setSchdIncrCostPaid(schd.getIncrCostPaid());
		finRpy.setSchdIncrCostBal(schd.getIncrCost().subtract(schd.getIncrCostPaid()));

		// finRpy.setAcrTillLBD(resultSet.getBigDecimal("AcrTillLBD"));
		// finRpy.setTdPftAmortizedSusp(resultSet.getBigDecimal("TdPftAmortizedSusp"));
		// finRpy.setAmzTillLBD(resultSet.getBigDecimal("AmzTillLBD"));

		finRpy.setAdvProfit(schd.getAdvProfit());
		if (schd.getAdvCalRate() != null) {
			finRpy.setSchdRate(schd.getAdvCalRate());
		} else {
			finRpy.setSchdRate(schd.getCalculatedRate());
		}
		finRpy.setSchdRate(schd.getAdvCalRate());
		finRpy.setFinStatus(financeMain.getFinStatus());

		FinODDetails odDetails = getFinODDetailsForBatch(finRpy.getFinReference(), finRpy.getRpyDate(),
				finRpy.getFinRpyFor());
		if (odDetails != null) {
			finRpy.setPenaltyBal(odDetails.getTotPenaltyBal());
			finRpy.setLatePayPftBal(odDetails.getTotPftBal());
		}

		finRpy.setRebate(latePaymentService.getScheduledRebateAmount(finRpy));

		return finRpy;
	}

	/**
	 * @param scheduleDetail
	 * @return
	 */
	public final BigDecimal getTotDueBySchedule(FinanceScheduleDetail scheduleDetail) {
		BigDecimal paidAmount = BigDecimal.ZERO;
		if (scheduleDetail == null) {
			return paidAmount;
		}
		paidAmount = paidAmount.add(scheduleDetail.getProfitSchd().add(scheduleDetail.getPrincipalSchd()));
		paidAmount = paidAmount.subtract(scheduleDetail.getSchdPftPaid().add(scheduleDetail.getSchdPriPaid()));
		paidAmount = paidAmount.add(scheduleDetail.getFeeSchd().subtract(scheduleDetail.getSchdFeePaid()));
		paidAmount = paidAmount.add(scheduleDetail.getInsSchd().subtract(scheduleDetail.getSchdInsPaid()));
		paidAmount = paidAmount.add(scheduleDetail.getSuplRent().subtract(scheduleDetail.getSuplRentPaid()));
		paidAmount = paidAmount.add(scheduleDetail.getIncrCost().subtract(scheduleDetail.getIncrCostPaid()));
		FinODDetails odDetails = getFinODDetailsForBatch(scheduleDetail.getFinReference(), scheduleDetail.getSchDate(),
				"");

		if (odDetails != null) {
			paidAmount = paidAmount.add(odDetails.getTotPenaltyBal());
			paidAmount = paidAmount.add(odDetails.getTotPftBal());
		}

		return paidAmount;
	}

	/**
	 * @param finReferencem
	 * @param rpyDate
	 * @param finRpyFor
	 * @return
	 */
	public FinODDetails getFinODDetailsForBatch(String finReferencem, Date rpyDate, String finRpyFor) {
		return latePaymentService.getFinODDetailsForBatch(finReferencem, rpyDate, finRpyFor);
	}

	public void setRepaymentService(RepaymentService repaymentService) {
		this.repaymentService = repaymentService;
	}

	public void setLatePaymentService(LatePaymentService latePaymentService) {
		this.latePaymentService = latePaymentService;
	}

	public void setSuspenseService(SuspenseService suspenseService) {
		this.suspenseService = suspenseService;
	}

	public void setStatusService(StatusService statusService) {
		this.statusService = statusService;
	}

	public void setPaymentRecoveryService(PaymentRecoveryService paymentRecoveryService) {
		this.paymentRecoveryService = paymentRecoveryService;
	}

}
