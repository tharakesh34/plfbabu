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
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.FinRepayQueue.FinRepayQueueDAO;
import com.pennant.backend.dao.finance.FinanceRepayPriorityDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.finance.FinanceRepayPriority;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.eod.constants.EodSql;

public class RepayQueueService {
	private static Logger			logger	= Logger.getLogger(RepayQueueService.class);

	private FinRepayQueueDAO		finRepayQueueDAO;
	private FinanceRepayPriorityDAO	financeRepayPriorityDAO;
	private LatePaymentService		latePaymentService;

	private Map<String, Integer>	priorityMap;

	public RepayQueueService() {
		super();
	}

	/**
	 * @param custId
	 * @param connection
	 * @param date
	 * @throws Exception
	 */
	public void saveQueue(Connection connection, long custId, Date date) throws Exception {
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		try {
			deleteByCustID(custId);
			sqlStatement = connection.prepareStatement(EodSql.customeFinance);
			sqlStatement.setDate(1, DateUtility.getDBDate(date.toString()));
			sqlStatement.setLong(2, custId);
			resultSet = sqlStatement.executeQuery();
			List<FinRepayQueue> repayQueuList = new ArrayList<FinRepayQueue>();
			while (resultSet.next()) {
				FinRepayQueue finRepayQueue = doWriteDataToBean(resultSet);
				repayQueuList.add(finRepayQueue);
			}
			if (repayQueuList.size() > 0) {
				finRepayQueueDAO.setFinRepayQueueRecords(repayQueuList);
				repayQueuList = null;
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

		BeanPropertyRowMapper<FinRepayQueue> beanPropertyRowMapper = new BeanPropertyRowMapper<>(FinRepayQueue.class);
		FinRepayQueue finRepayQueue = beanPropertyRowMapper.mapRow(resultSet, resultSet.getRow());

		try {

			String rpyFor = FinanceConstants.SCH_TYPE_SCHEDULE;

			if (this.priorityMap != null && this.priorityMap.containsKey(finRepayQueue.getFinType())) {
				finRepayQueue.setFinPriority(this.priorityMap.get(finRepayQueue.getFinType()));
			} else {
				finRepayQueue.setFinPriority(9999);
			}

			finRepayQueue.setFinRpyFor(rpyFor);
			finRepayQueue.setPenaltyPayNow(BigDecimal.ZERO);

			finRepayQueue.setSchdPft(resultSet.getBigDecimal("ProfitSchd"));
			finRepayQueue.setSchdPri(resultSet.getBigDecimal("PrincipalSchd"));
			finRepayQueue.setSchdPftPaid(resultSet.getBigDecimal("SchdPftpaid"));
			finRepayQueue.setSchdPriPaid(resultSet.getBigDecimal("SchdPriPaid"));
			finRepayQueue.setSchdPftBal(resultSet.getBigDecimal("SchdPftBal"));
			finRepayQueue.setSchdPriBal(resultSet.getBigDecimal("SchdPriBal"));

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
			
			BigDecimal schdRate=BigDecimal.ZERO;
			BigDecimal adivedRate = resultSet.getBigDecimal("AdvCalRate");
			BigDecimal calculateRate = resultSet.getBigDecimal("CalculatedRate");
			
			if (adivedRate != null && adivedRate.compareTo(BigDecimal.ZERO) != 0) {
				schdRate = adivedRate;
			} else {
				schdRate = calculateRate;
			}
			
			finRepayQueue.setSchdRate(schdRate);

			finRepayQueue.setRebate(latePaymentService.getScheduledRebateAmount(finRepayQueue));

		} catch (SQLException e) {
			logger.error("Finrefernce :", e);
			throw e;
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
