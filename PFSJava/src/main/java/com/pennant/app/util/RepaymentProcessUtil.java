package com.pennant.app.util;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueueTotals;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.exception.PFFInterfaceException;

public class RepaymentProcessUtil {
	private final static Logger		logger	= Logger.getLogger(RepaymentProcessUtil.class);

	private RepaymentPostingsUtil	repayPostingUtil;
	private FinODDetailsDAO			finODDetailsDAO;
	private FinExcessAmountDAO				finExcessAmountDAO;

	public RepaymentProcessUtil() {
		super();
	}

	/**
	 * Method for Calculation of Schedule payment based on Allocated Details from Receipts
	 * 
	 * @param receiptData
	 * @param scheduleData
	 * @return
	 * @throws PFFInterfaceException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public void recalReceipt(FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,
			FinanceProfitDetail profitDetail, FinReceiptDetail receiptDetail, String repayHierarchy)
			throws IllegalAccessException, InvocationTargetException, PFFInterfaceException {
		logger.debug("Entering");

		// If no balance for repayment then return with out calculation
		BigDecimal totalReceiptAmt = receiptDetail.getAmount();
		if (totalReceiptAmt.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		// Fetch total overdue details
		Map<Date, FinODDetails> overdueMap = new HashMap<Date, FinODDetails>();
		List<FinODDetails> overdueList = getFinODDetailsDAO().getFinODBalByFinRef(financeMain.getFinReference());
		if (overdueList != null && !overdueList.isEmpty()) {
			for (int m = 0; m < overdueList.size(); m++) {
				if (overdueMap.containsKey(overdueList.get(m).getFinODSchdDate())) {
					overdueMap.remove(overdueList.get(m).getFinODSchdDate());
				}
				overdueMap.put(overdueList.get(m).getFinODSchdDate(), overdueList.get(m));
			}
		}

		scheduleDetails = sortSchdDetails(scheduleDetails);

		List<FinRepayHeader> repayHeaderList = new ArrayList<>();
		List<RepayScheduleDetail> pastdueRpySchdList = new ArrayList<>();
		char[] rpyOrder = repayHierarchy.replace("CS", "C").toCharArray();
		BigDecimal totPriPaidNow = BigDecimal.ZERO;
		BigDecimal totPftPaidNow = BigDecimal.ZERO;
		BigDecimal totLPftPaidNow = BigDecimal.ZERO;
		BigDecimal totFeePaidNow = BigDecimal.ZERO;
		BigDecimal totInsPaidNow = BigDecimal.ZERO;
		BigDecimal totPenaltyPaidNow = BigDecimal.ZERO;
		Date valueDate = receiptDetail.getValueDate();

		// Load Pending Schedules until balance available for payment
		for (int s = 1; s < scheduleDetails.size(); s++) {
			FinanceScheduleDetail curSchd = scheduleDetails.get(s);
			Date schdDate = curSchd.getSchDate();
			RepayScheduleDetail rsd = null;

			// Skip if Repayment date after Current Business date
			if (schdDate.compareTo(valueDate) != 0) {
				continue;
			}

			for (int j = 0; j < rpyOrder.length; j++) {

				char repayTo = rpyOrder[j];
				if (repayTo == RepayConstants.REPAY_PRINCIPAL) {

					BigDecimal balPri = curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid());
					if (balPri.compareTo(BigDecimal.ZERO) > 0) {
						if (balPri.compareTo(totalReceiptAmt) > 0) {
							balPri = totalReceiptAmt;
						}
						rsd = prepareRpyRecord(curSchd, rsd, repayTo, balPri, valueDate);
						// Reset Total Receipt Amount
						totalReceiptAmt = totalReceiptAmt.subtract(balPri);
						totPriPaidNow = totPriPaidNow.add(balPri);
					}
				} else if (repayTo == RepayConstants.REPAY_PROFIT) {

					String profit = ImplementationConstants.REPAY_INTEREST_HIERARCHY;
					char[] pftPayOrder = profit.toCharArray();
					for (char pftPayTo : pftPayOrder) {
						if (pftPayTo == RepayConstants.REPAY_PROFIT) {

							BigDecimal balPft = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
							if (balPft.compareTo(BigDecimal.ZERO) > 0) {
								if (balPft.compareTo(totalReceiptAmt) > 0) {
									balPft = totalReceiptAmt;
								}
								rsd = prepareRpyRecord(curSchd, rsd, repayTo, balPft, valueDate);
								// Reset Total Receipt Amount
								totalReceiptAmt = totalReceiptAmt.subtract(balPft);
								totPftPaidNow = totPftPaidNow.add(balPft);
							}

						} else if (pftPayTo == RepayConstants.REPAY_LATEPAY_PROFIT) {

							FinODDetails overdue = overdueMap.get(schdDate);
							if (overdue != null) {
								BigDecimal balLatePft = overdue.getLPIBal();
								if (balLatePft.compareTo(BigDecimal.ZERO) > 0) {
									if (balLatePft.compareTo(totalReceiptAmt) > 0) {
										balLatePft = totalReceiptAmt;
									}
									rsd = prepareRpyRecord(curSchd, rsd, repayTo, balLatePft, valueDate);

									// Reset Total Receipt Amount
									totalReceiptAmt = totalReceiptAmt.subtract(balLatePft);
									totLPftPaidNow = totLPftPaidNow.add(balLatePft);
								}
							}
						}
					}

				} else if (repayTo == RepayConstants.REPAY_PENALTY) {

					FinODDetails overdue = overdueMap.get(schdDate);
					if (overdue != null) {
						BigDecimal balPenalty = overdue.getTotPenaltyBal();
						if (balPenalty.compareTo(BigDecimal.ZERO) > 0) {
							if (balPenalty.compareTo(totalReceiptAmt) > 0) {
								balPenalty = totalReceiptAmt;
							}
							rsd = prepareRpyRecord(curSchd, rsd, repayTo, balPenalty, valueDate);

							// Reset Total Receipt Amount
							totalReceiptAmt = totalReceiptAmt.subtract(balPenalty);
							totPenaltyPaidNow = totPenaltyPaidNow.add(balPenalty);
						}
					}

				} else if (repayTo == RepayConstants.REPAY_OTHERS) {

					// If Schedule has Unpaid Fee Amount
					BigDecimal balFee = curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid());
					if (balFee.compareTo(BigDecimal.ZERO) > 0) {
						if (balFee.compareTo(totalReceiptAmt) > 0) {
							balFee = totalReceiptAmt;
						}
						rsd = prepareRpyRecord(curSchd, rsd, RepayConstants.REPAY_FEE, balFee, valueDate);

						// Reset Total Receipt Amount
						totalReceiptAmt = totalReceiptAmt.subtract(balFee);
						totFeePaidNow = totFeePaidNow.add(balFee);
					}

					// If Schedule has Unpaid Insurance Amount
					BigDecimal balIns = curSchd.getInsSchd().subtract(curSchd.getSchdInsPaid());
					if (balIns.compareTo(BigDecimal.ZERO) > 0) {
						if (balIns.compareTo(totalReceiptAmt) > 0) {
							balIns = totalReceiptAmt;
						}
						rsd = prepareRpyRecord(curSchd, rsd, RepayConstants.REPAY_INS, balIns, valueDate);

						// Reset Total Receipt Amount
						totalReceiptAmt = totalReceiptAmt.subtract(balIns);
						totInsPaidNow = totInsPaidNow.add(balIns);
					}
				}

				// No more Receipt amount left for next schedules
				if (totalReceiptAmt.compareTo(BigDecimal.ZERO) == 0) {
					break;
				}
			}

			// Add Repay Schedule detail List
			if (rsd != null) {
				pastdueRpySchdList.add(rsd);
			}

			// No more Receipt amount left for next schedules
			if (totalReceiptAmt.compareTo(BigDecimal.ZERO) == 0) {
				break;
			}
		}

		FinRepayHeader repayHeader = null;
		if (receiptDetail.getAmount().compareTo(totalReceiptAmt) > 0) {
			// Prepare Repay Header Details
			repayHeader = new FinRepayHeader();
			repayHeader.setFinReference(financeMain.getFinReference());
			repayHeader.setValueDate(valueDate);
			repayHeader.setRepayAmount(receiptDetail.getAmount().subtract(totalReceiptAmt));
			repayHeader.setFinEvent(FinanceConstants.FINSER_EVENT_SCHDRPY);
			repayHeader.setPriAmount(totPriPaidNow);
			repayHeader.setPftAmount(totPftPaidNow);
			repayHeader.setLatePftAmount(totLPftPaidNow);
			repayHeader.setTotalPenalty(totPenaltyPaidNow);
			repayHeader.setTotalIns(totInsPaidNow);
			repayHeader.setTotalSchdFee(totFeePaidNow);
			repayHeader.setTotalWaiver(BigDecimal.ZERO);

			// Adding Repay Schedule Details to Repay Header
			repayHeader.setRepayScheduleDetails(pastdueRpySchdList);
			repayHeaderList.add(repayHeader);
		}

		if (totalReceiptAmt.compareTo(BigDecimal.ZERO) > 0) {

			// Prepare Repay Header Details
			repayHeader = new FinRepayHeader();
			repayHeader.setFinReference(financeMain.getFinReference());
			repayHeader.setValueDate(valueDate);
			repayHeader.setRepayAmount(totalReceiptAmt);
			repayHeader.setFinEvent("Excess");
			repayHeader.setPriAmount(totalReceiptAmt);
			repayHeader.setPftAmount(BigDecimal.ZERO);
			repayHeader.setLatePftAmount(BigDecimal.ZERO);
			repayHeader.setTotalPenalty(BigDecimal.ZERO);
			repayHeader.setTotalIns(BigDecimal.ZERO);
			repayHeader.setTotalSchdFee(BigDecimal.ZERO);
			repayHeader.setTotalWaiver(BigDecimal.ZERO);

			// Adding Repay Schedule Details to Repay Header
			repayHeader.setRepayScheduleDetails(null);
			repayHeaderList.add(repayHeader);
		}

		// Adding Repay Headers to Receipt Details
		receiptDetail.setRepayHeaders(repayHeaderList);
		long linkedTranId = 0;

		for (int j = 0; j < repayHeaderList.size(); j++) {

			repayHeader = repayHeaderList.get(j);
			if (!StringUtils.equals(FinanceConstants.FINSER_EVENT_SCHDRPY, repayHeader.getFinEvent())
					&& !StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYRPY, repayHeader.getFinEvent())
					&& !StringUtils.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE, repayHeader.getFinEvent())) {

				// Update Excess amount (Adding amount and balance updation)
				getFinExcessAmountDAO().updateExcessBal(receiptDetail.getPayAgainstID(), repayHeader.getRepayAmount());
				continue;
			}

			List<RepayScheduleDetail> repaySchdList = repayHeader.getRepayScheduleDetails();
			List<Object> returnList = processRepaymentPostings(financeMain, scheduleDetails,
					profitDetail, repaySchdList, repayHeader.getFinEvent());

			if (!(Boolean) returnList.get(0)) {
				String errParm = (String) returnList.get(1);
				throw new PFFInterfaceException("9999", errParm);
			}

			//Update Linked Transaction ID
			linkedTranId = (long) returnList.get(1);
			repayHeader.setLinkedTranId(linkedTranId);

			String finAccount = (String) returnList.get(2);
			if (finAccount != null) {
				financeMain.setFinAccount(finAccount);
			}
			scheduleDetails = (List<FinanceScheduleDetail>) returnList.get(3);
		}

		overdueList = null;
		overdueMap = null;

		logger.debug("Leaving");
	}

	/**
	 * Method for Repayment Details Posting Process
	 * 
	 * @param financeMain
	 * @param scheduleDetails
	 * @param repaySchdList
	 * @param insRefund
	 * @return
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 * @throws InvocationTargetException
	 */
	public List<Object> processRepaymentPostings(FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,
			FinanceProfitDetail profitDetail, List<RepayScheduleDetail> repaySchdList, String finEvent)
			throws IllegalAccessException, PFFInterfaceException, InvocationTargetException {
		return doRepayPostings(financeMain, scheduleDetails, profitDetail, repaySchdList, finEvent);
	}

	/**
	 * Method for Repayment Details Posting Process
	 * 
	 * @param financeMain
	 * @param scheduleDetails
	 * @param repaySchdList
	 * @param insRefund
	 * @return
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 * @throws InvocationTargetException
	 */
	private List<Object> doRepayPostings(FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,
			FinanceProfitDetail profitDetail, List<RepayScheduleDetail> repaySchdList, String finEvent)
			throws IllegalAccessException, PFFInterfaceException, InvocationTargetException {
		logger.debug("Entering");

		List<Object> returnList = new ArrayList<Object>();
		try {

			List<FinRepayQueue> finRepayQueues = new ArrayList<FinRepayQueue>();
			FinRepayQueue finRepayQueue = null;
			FinRepayQueueTotals repayQueueTotals = new FinRepayQueueTotals();

			if (repaySchdList != null && !repaySchdList.isEmpty()) {
				for (int i = 0; i < repaySchdList.size(); i++) {

					finRepayQueue = new FinRepayQueue();
					finRepayQueue.setFinReference(financeMain.getFinReference());
					finRepayQueue.setRpyDate(repaySchdList.get(i).getSchDate());
					finRepayQueue.setFinRpyFor(repaySchdList.get(i).getSchdFor());
					finRepayQueue.setRcdNotExist(true);
					finRepayQueue = doWriteDataToBean(finRepayQueue, financeMain, repaySchdList.get(i));

					finRepayQueue.setRefundAmount(repaySchdList.get(i).getRefundReq());
					finRepayQueue.setPenaltyPayNow(repaySchdList.get(i).getPenaltyPayNow());
					finRepayQueue.setWaivedAmount(repaySchdList.get(i).getWaivedAmt());
					finRepayQueue.setPenaltyBal(repaySchdList.get(i).getPenaltyAmt()
							.subtract(repaySchdList.get(i).getPenaltyPayNow()));
					finRepayQueue.setChargeType(repaySchdList.get(i).getChargeType());

					// Total Repayments Calculation for Principal, Profit 
					repayQueueTotals.setPrincipal(repayQueueTotals.getPrincipal().add(
							repaySchdList.get(i).getPrincipalSchdPayNow()));
					repayQueueTotals.setProfit(repayQueueTotals.getProfit().add(
							repaySchdList.get(i).getProfitSchdPayNow()));
					repayQueueTotals.setLateProfit(repayQueueTotals.getLateProfit().add(
							repaySchdList.get(i).getLatePftSchdPayNow()));
					repayQueueTotals.setPenalty(repayQueueTotals.getPenalty().add(
							repaySchdList.get(i).getPenaltyPayNow()));

					// Fee Details
					repayQueueTotals.setFee(repayQueueTotals.getFee().add(repaySchdList.get(i).getSchdFeePayNow()));
					repayQueueTotals.setInsurance(repayQueueTotals.getInsurance().add(
							repaySchdList.get(i).getSchdInsPayNow()));
					repayQueueTotals.setSuplRent(repayQueueTotals.getSuplRent().add(
							repaySchdList.get(i).getSchdSuplRentPayNow()));
					repayQueueTotals.setIncrCost(repayQueueTotals.getIncrCost().add(
							repaySchdList.get(i).getSchdIncrCostPayNow()));

					finRepayQueues.add(finRepayQueue);
				}
			}

			//Repayments Process For Schedule Repay List	
			repayQueueTotals.setQueueList(finRepayQueues);

			returnList = getRepayPostingUtil().postingProcess(financeMain, scheduleDetails, profitDetail,
					repayQueueTotals, finEvent);

		} catch (PFFInterfaceException e) {
			logger.error("Exception: ", e);
			throw e;
		} catch (IllegalAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		} catch (InvocationTargetException e) {
			logger.error("Exception: ", e);
			throw e;
		}

		logger.debug("Leaving");
		return returnList;
	}

	/**
	 * Method for prepare RepayQueue data
	 * 
	 * @param resultSet
	 * @return
	 */
	private FinRepayQueue doWriteDataToBean(FinRepayQueue finRepayQueue, FinanceMain financeMain,
			RepayScheduleDetail rsd) {
		logger.debug("Entering");

		finRepayQueue.setBranch(financeMain.getFinBranch());
		finRepayQueue.setFinType(financeMain.getFinType());
		finRepayQueue.setCustomerID(financeMain.getCustID());
		finRepayQueue.setFinPriority(9999);

		// Principal Amount
		finRepayQueue.setSchdPft(rsd.getProfitSchd());
		finRepayQueue.setSchdPftPaid(rsd.getProfitSchdPaid());
		finRepayQueue.setSchdPftBal(rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid()));
		finRepayQueue.setSchdPftPayNow(rsd.getProfitSchdPayNow());
		finRepayQueue.setSchdPftWaivedNow(rsd.getPftSchdWaivedNow());
		
		// Profit Amount
		finRepayQueue.setSchdPri(rsd.getPrincipalSchd());
		finRepayQueue.setSchdPriBal(rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid()));
		finRepayQueue.setSchdPriPaid(rsd.getPrincipalSchdPaid());
		finRepayQueue.setSchdPriPayNow(rsd.getPrincipalSchdPayNow());
		finRepayQueue.setSchdPriWaivedNow(rsd.getPriSchdWaivedNow());
		
		// Late Pay Profit Amount
		finRepayQueue.setLatePayPftPayNow(rsd.getLatePftSchdPayNow());
		finRepayQueue.setLatePayPftWaivedNow(rsd.getLatePftSchdWaivedNow());

		// Fee Details
		//	1. Schedule Fee Amount
		finRepayQueue.setSchdFee(rsd.getSchdFee());
		finRepayQueue.setSchdFeeBal(rsd.getSchdFeeBal());
		finRepayQueue.setSchdFeePayNow(rsd.getSchdFeePayNow());
		finRepayQueue.setSchdFeePaid(rsd.getSchdFeePaid());
		finRepayQueue.setSchdFeeWaivedNow(rsd.getSchdFeeWaivedNow());

		//	2. Schedule Insurance Amount
		finRepayQueue.setSchdIns(rsd.getSchdIns());
		finRepayQueue.setSchdInsBal(rsd.getSchdInsBal());
		finRepayQueue.setSchdInsPayNow(rsd.getSchdInsPayNow());
		finRepayQueue.setSchdInsPaid(rsd.getSchdInsPaid());
		finRepayQueue.setSchdInsWaivedNow(rsd.getSchdInsWaivedNow());

		//	3. Schedule Supplementary Rent Amount
		finRepayQueue.setSchdSuplRent(rsd.getSchdSuplRent());
		finRepayQueue.setSchdSuplRentBal(rsd.getSchdSuplRentBal());
		finRepayQueue.setSchdSuplRentPayNow(rsd.getSchdSuplRentPayNow());
		finRepayQueue.setSchdSuplRentPaid(rsd.getSchdSuplRentPaid());
		finRepayQueue.setSchdSuplRentWaivedNow(rsd.getSchdSuplRentWaivedNow());

		//	4. Schedule Increased Cost Amount
		finRepayQueue.setSchdIncrCost(rsd.getSchdIncrCost());
		finRepayQueue.setSchdIncrCostBal(rsd.getSchdIncrCostBal());
		finRepayQueue.setSchdIncrCostPayNow(rsd.getSchdIncrCostPayNow());
		finRepayQueue.setSchdIncrCostPaid(rsd.getSchdIncrCostPaid());
		finRepayQueue.setSchdIncrCostWaivedNow(rsd.getSchdIncrCostWaivedNow());

		logger.debug("Leaving");
		return finRepayQueue;
	}

	/**
	 * Method for Preparation of Repayment Schedule Details
	 * 
	 * @param curSchd
	 * @param rsd
	 * @param rpyTo
	 * @param balPayNow
	 * @return
	 */
	private RepayScheduleDetail prepareRpyRecord(FinanceScheduleDetail curSchd, RepayScheduleDetail rsd, char rpyTo,
			BigDecimal balPayNow, Date valueDate) {

		if (rsd == null) {
			rsd = new RepayScheduleDetail();
			rsd.setFinReference(curSchd.getFinReference());
			rsd.setSchDate(curSchd.getSchDate());
			rsd.setDefSchdDate(curSchd.getSchDate());

			rsd.setSchdFor(FinanceConstants.SCH_TYPE_SCHEDULE);
			rsd.setProfitSchd(curSchd.getProfitSchd());
			rsd.setProfitSchdPaid(curSchd.getSchdPftPaid());
			rsd.setProfitSchdBal(rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid()));

			rsd.setPrincipalSchd(curSchd.getPrincipalSchd());
			rsd.setPrincipalSchdPaid(curSchd.getSchdPriPaid());
			rsd.setPrincipalSchdBal(rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid()));

			rsd.setSchdIns(curSchd.getInsSchd());
			rsd.setSchdInsPaid(curSchd.getSchdInsPaid());
			rsd.setSchdInsBal(rsd.getSchdIns().subtract(rsd.getSchdInsPaid()));

			rsd.setSchdSuplRent(curSchd.getSuplRent());
			rsd.setSchdSuplRentPaid(curSchd.getSuplRentPaid());
			rsd.setSchdSuplRentBal(rsd.getSchdSuplRent().subtract(rsd.getSchdSuplRentPaid()));

			rsd.setSchdIncrCost(curSchd.getIncrCost());
			rsd.setSchdIncrCostPaid(curSchd.getIncrCostPaid());
			rsd.setSchdIncrCostBal(rsd.getSchdIncrCost().subtract(rsd.getSchdIncrCostPaid()));

			rsd.setSchdFee(curSchd.getFeeSchd());
			rsd.setSchdFeePaid(curSchd.getSchdFeePaid());
			rsd.setSchdFeeBal(rsd.getSchdFee().subtract(rsd.getSchdFeePaid()));

			rsd.setDaysLate(DateUtility.getDaysBetween(curSchd.getSchDate(), valueDate));
			rsd.setDaysEarly(0);
		}

		// Principal Payment 
		if (rpyTo == RepayConstants.REPAY_PRINCIPAL) {
			rsd.setPrincipalSchdPayNow(balPayNow);
		}

		// Profit Payment 
		if (rpyTo == RepayConstants.REPAY_PROFIT) {
			rsd.setProfitSchdPayNow(balPayNow);
		}

		// Late Payment Profit Payment 
		if (rpyTo == RepayConstants.REPAY_LATEPAY_PROFIT) {
			rsd.setLatePftSchdPayNow(balPayNow);
		}

		// Fee Detail Payment 
		if (rpyTo == RepayConstants.REPAY_FEE) {
			rsd.setSchdFeePayNow(balPayNow);

		}

		// Insurance Detail Payment 
		if (rpyTo == RepayConstants.REPAY_INS) {
			rsd.setSchdInsPayNow(balPayNow);
		}

		// Penalty Charge Detail Payment 
		if (rpyTo == RepayConstants.REPAY_PENALTY) {
			rsd.setPenaltyPayNow(balPayNow);
		}

		return rsd;

	}

	/**
	 * Method for Sorting Schedule Details
	 * 
	 * @param financeScheduleDetail
	 * @return
	 */
	public List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> financeScheduleDetail) {

		if (financeScheduleDetail != null && financeScheduleDetail.size() > 0) {
			Collections.sort(financeScheduleDetail, new Comparator<FinanceScheduleDetail>() {
				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					return DateUtility.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return financeScheduleDetail;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public RepaymentPostingsUtil getRepayPostingUtil() {
		return repayPostingUtil;
	}
	public void setRepayPostingUtil(RepaymentPostingsUtil repayPostingUtil) {
		this.repayPostingUtil = repayPostingUtil;
	}

	public FinODDetailsDAO getFinODDetailsDAO() {
		return finODDetailsDAO;
	}
	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public FinExcessAmountDAO getFinExcessAmountDAO() {
		return finExcessAmountDAO;
	}
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

}
