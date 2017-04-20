package com.pennant.app.core;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.FinRepayQueue.FinRepayQueueDAO;
import com.pennant.backend.dao.finance.FinanceRepayPriorityDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.finance.FinanceRepayPriority;
import com.pennant.backend.util.FinanceConstants;

public class RepayQueueService extends ServiceHelper{
	private static final long	serialVersionUID	= 2478149362113489563L;

	private static Logger			logger				= Logger.getLogger(RepayQueueService.class);

	private FinRepayQueueDAO		finRepayQueueDAO;
	private FinanceRepayPriorityDAO	financeRepayPriorityDAO;
	private LatePaymentService		latePaymentService;

	private Map<String, Integer>	priorityMap;

	public static final String		customeFinance		= " SELECT F.FinReference, F.FinBranch Branch, F.FinType ,F.CustID CustomerID ,F.LinkedFinRef,  S.SchDate RpyDate,"
																+ "  S.PrincipalSchd, S.SchdPriPaid, S.ProfitSchd, S.SchdPftpaid, S.FeeSchd SchdFee , S.SchdFeePaid ,"
																+ " S.InsSchd SchdIns, S.SchdInsPaid , S.CalculatedRate FROM FinanceMain F , FinScheduleDetails S "
																+ " WHERE F.FinReference = S.FinReference  AND S.SchDate <= ? AND F.FinIsActive = 1 "
																+ " AND (S.PrincipalSchd <> S.SchdPriPaid OR S.ProfitSchd <> S.SchdPftPaid "
																+ " OR S.SuplRent <> S.SuplRentPaid OR  S.IncrCost <> S.IncrCostPaid "
																+ " OR S.FeeSchd <> S.SchdFeePaid OR S.InsSchd <>  S.SchdInsPaid ) "
																+ " AND CustID=? ";

	public static final String		customeFinance_ISLM	= " SELECT F.FinReference, F.FinBranch Branch, F.FinType ,F.CustID CustomerID ,F.LinkedFinRef, S.SchDate RpyDate,"
																+ "  S.PrincipalSchd, S.SchdPriPaid, S.ProfitSchd, S.SchdPftpaid, S.SuplRent SchdSuplRent, S.SuplRentPaid SchdSuplRentPaid,"
																+ " S.IncrCost SchdIncrCost, S.IncrCostPaid SchdIncrCostPaid, S.FeeSchd SchdFee , S.SchdFeePaid , "
																+ " S.InsSchd SchdIns, S.SchdInsPaid SchdInsPaid, S.AdvCalRate, S.AdvProfit, S.CalculatedRate "
																+ " FROM FinanceMain F , FinScheduleDetails S WHERE F.FinReference = S.FinReference  AND S.SchDate <= ? "
																+ " AND F.FinIsActive = 1  AND (S.PrincipalSchd <> S.SchdPriPaid OR S.ProfitSchd <> S.SchdPftPaid "
																+ "  OR S.SuplRent <> S.SuplRentPaid OR  S.IncrCost <> S.IncrCostPaid "
																+ " OR S.FeeSchd <> S.SchdFeePaid OR S.InsSchd <>  S.SchdInsPaid ) "
																+ " AND CustID=? ";

	public RepayQueueService() {
		super();
	}

	/**
	 * @param custId
	 * @param connection
	 * @param date
	 * @throws Exception
	 */
	public void prepareRepayQueue(Connection connection, long custId, Date date) throws Exception {
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		String finreference = "";
		try {
			deleteByCustID(custId);
			if (ImplementationConstants.IMPLEMENTATION_ISLAMIC) {
				sqlStatement = connection.prepareStatement(customeFinance_ISLM);
			} else {
				sqlStatement = connection.prepareStatement(customeFinance);
			}
			sqlStatement.setDate(1, DateUtility.getDBDate(date.toString()));
			sqlStatement.setLong(2, custId);
			resultSet = sqlStatement.executeQuery();
			List<FinRepayQueue> repayQueuList = new ArrayList<FinRepayQueue>();
			while (resultSet.next()) {
				finreference = resultSet.getString("FinReference");
				FinRepayQueue finRepayQueue = doWriteDataToBean(resultSet);
				repayQueuList.add(finRepayQueue);
			}
			if (repayQueuList.size() > 0) {
				finRepayQueueDAO.setFinRepayQueueRecords(repayQueuList);
				repayQueuList = null;
			}
		} catch (Exception e) {
			logger.error("Exception: Finreference :" + finreference, e);
			throw new Exception("Exception: Finreference" + finreference, e);
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
	 * To load repay priority based on finance type
	 */
	public void loadFinanceRepayPriority() {
		logger.debug(" Entering ");

		if (this.priorityMap == null) {
			priorityMap = new HashMap<String, Integer>(1);
		}
		List<FinanceRepayPriority> finRpyPriorities = financeRepayPriorityDAO.getFinanceRepayPriorities();
		for (FinanceRepayPriority financeRepayPriority : finRpyPriorities) {
			this.priorityMap.put(financeRepayPriority.getFinType(), financeRepayPriority.getFinPriority());
		}

		logger.debug(" Leaving ");
	}

	/**
	 * to clear the priority queue
	 */
	public void clearFinanceRepayPriority() {
		logger.debug(" Entering ");
		if (priorityMap != null) {
			this.priorityMap.clear();
			this.priorityMap = null;
		}
		logger.debug(" Leaving ");
	}

	/**
	 * @param repayQueuList
	 */
	public void save(List<FinRepayQueue> repayQueuList) {
		finRepayQueueDAO.setFinRepayQueueRecords(repayQueuList);

	}

	/**
	 * delete records from the queue
	 */
	public void deleteRepayQueue() {
		finRepayQueueDAO.deleteRepayQueue();

	}

	/**
	 * delete records from the queue
	 * 
	 * @param customerID
	 */
	public void deleteByCustID(long customerID) {
		finRepayQueueDAO.deleteByCustID(customerID);

	}

	/**
	 * @param resultSet
	 * @param priorityMap
	 * @return
	 * @throws SQLException
	 */
	public FinRepayQueue doWriteDataToBean(ResultSet resultSet) throws SQLException {
		logger.debug("Entering");

		FinRepayQueue finRepayQueue = new FinRepayQueue();
		finRepayQueue.setFinReference(resultSet.getString("FinReference"));
		finRepayQueue.setBranch(resultSet.getString("FinBranch"));
		finRepayQueue.setFinType(resultSet.getString("FinType"));
		finRepayQueue.setCustomerID(resultSet.getLong("CustID"));
		finRepayQueue.setLinkedFinRef(resultSet.getString("LinkedFinRef"));
		finRepayQueue.setRpyDate(resultSet.getDate("SchDate"));

		finRepayQueue.setSchdPri(getDecimal(resultSet, "PrincipalSchd"));
		finRepayQueue.setSchdPriPaid(getDecimal(resultSet, "SchdPriPaid"));
		finRepayQueue.setSchdPriBal(finRepayQueue.getSchdPri().subtract(finRepayQueue.getSchdPriPaid()));

		finRepayQueue.setSchdPft(getDecimal(resultSet, "ProfitSchd"));
		finRepayQueue.setSchdPftPaid(getDecimal(resultSet, "SchdPftpaid"));
		finRepayQueue.setSchdPftBal(finRepayQueue.getSchdPft().subtract(finRepayQueue.getSchdPftPaid()));

		finRepayQueue.setSchdFee(getDecimal(resultSet, "FeeSchd"));
		finRepayQueue.setSchdFeePaid(getDecimal(resultSet, "SchdFeePaid"));
		finRepayQueue.setSchdPftBal(finRepayQueue.getSchdFee().subtract(finRepayQueue.getSchdFeePaid()));

		finRepayQueue.setSchdIns(getDecimal(resultSet, "InsSchd"));
		finRepayQueue.setSchdInsPaid(getDecimal(resultSet, "SchdInsPaid"));
		finRepayQueue.setSchdPftBal(finRepayQueue.getSchdIns().subtract(finRepayQueue.getSchdInsPaid()));

		finRepayQueue.setSchdRate(getDecimal(resultSet, "CalculatedRate"));

		if (this.priorityMap != null && this.priorityMap.containsKey(finRepayQueue.getFinType())) {
			finRepayQueue.setFinPriority(this.priorityMap.get(finRepayQueue.getFinType()));
		} else {
			finRepayQueue.setFinPriority(9999);
		}

		finRepayQueue.setFinRpyFor(FinanceConstants.SCH_TYPE_SCHEDULE);
		finRepayQueue.setPenaltyPayNow(BigDecimal.ZERO);

		if (finRepayQueue.getSchdPftBal().compareTo(BigDecimal.ZERO) == 0) {
			finRepayQueue.setSchdIsPftPaid(true);
		} else {
			finRepayQueue.setSchdIsPftPaid(false);
		}

		if (finRepayQueue.isSchdIsPftPaid() && finRepayQueue.getSchdPriBal().compareTo(BigDecimal.ZERO) == 0) {
			finRepayQueue.setSchdIsPriPaid(true);
		} else {
			finRepayQueue.setSchdIsPriPaid(false);
		}

		if (ImplementationConstants.IMPLEMENTATION_ISLAMIC) {
			finRepayQueue.setSchdSuplRent(getDecimal(resultSet, "SuplRent"));
			finRepayQueue.setSchdSuplRentPaid(getDecimal(resultSet, "SuplRentPaid"));
			finRepayQueue.setSchdSuplRentBal(finRepayQueue.getSchdSuplRent().subtract(
					finRepayQueue.getSchdSuplRentPaid()));

			finRepayQueue.setSchdIncrCost(getDecimal(resultSet, "IncrCost"));
			finRepayQueue.setSchdIncrCostPaid(getDecimal(resultSet, "IncrCostPaid"));
			finRepayQueue.setSchdIncrCostBal(finRepayQueue.getSchdIncrCost().subtract(
					finRepayQueue.getSchdIncrCostPaid()));
			BigDecimal adivedRate = getDecimal(resultSet, "AdvCalRate");
			if (adivedRate.compareTo(BigDecimal.ZERO) != 0) {
				finRepayQueue.setSchdRate(adivedRate);
			}
			finRepayQueue.setRebate(latePaymentService.getScheduledRebateAmount(finRepayQueue));
		}

		logger.debug("Leaving");
		return finRepayQueue;
	}


	public void setLatePaymentService(LatePaymentService latePaymentService) {
		this.latePaymentService = latePaymentService;
	}

	public void setFinanceRepayPriorityDAO(FinanceRepayPriorityDAO financeRepayPriorityDAO) {
		this.financeRepayPriorityDAO = financeRepayPriorityDAO;
	}

	public void setFinRepayQueueDAO(FinRepayQueueDAO finRepayQueueDAO) {
		this.finRepayQueueDAO = finRepayQueueDAO;
	}

}
