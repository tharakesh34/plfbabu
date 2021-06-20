package com.pennant.backend.dao.FinRepayQueue.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.FinRepayQueue.FinRepayQueueDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennanttech.pennapps.core.jdbc.BasicDao;

public class FinRepayQueueDAOImpl extends BasicDao<FinRepayQueue> implements FinRepayQueueDAO {
	private static Logger logger = LogManager.getLogger(FinRepayQueueDAOImpl.class);

	public FinRepayQueueDAOImpl() {
		super();
	}

	/**
	 * Method for Save Finance Repay Queue list
	 * 
	 * @param finRepayQueueList
	 * @param type
	 */
	public void saveBatch(List<FinRepayQueue> finRepayQueueList, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into FinRpyQueue");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (RpyDate, FinPriority, FinType, FinReference, FinRpyFor, Branch, CustomerID,");
		insertSql.append(
				" SchdPft, SchdPri, SchdPftPaid, SchdPriPaid, SchdPftBal, SchdPriBal, SchdIsPftPaid, SchdIsPriPaid,");
		insertSql.append(
				" SchdFee, SchdFeePaid, SchdFeeBal, SchdFeePayNow, PenaltyPayNow, LatePayPftPayNow, SchdRate, ");
		insertSql.append(" LinkedFinRef ) ");
		insertSql.append(" Values(:RpyDate, :FinPriority, :FinType, :FinReference, :FinRpyFor, :Branch, :CustomerID, ");
		insertSql.append(
				" :SchdPft, :SchdPri, :SchdPftPaid , :SchdPriPaid, :SchdPftBal, :SchdPriBal, :SchdIsPftPaid, :SchdIsPriPaid ,");
		insertSql.append(
				" :SchdFee, :SchdFeePaid, :SchdFeeBal, :SchdFeePayNow, :PenaltyPayNow, :LatePayPftPayNow, :SchdRate , ");
		insertSql.append(" :LinkedFinRef ) ");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(finRepayQueueList.toArray());
		this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Method for Save Finance Repay Queue list
	 * 
	 * @param repayQueue
	 * @param type
	 */
	@Override
	public void update(FinRepayQueue repayQueue, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Update FinRpyQueue");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" set  SchdPftPaid=:SchdPftPaid, SchdPriPaid=:SchdPriPaid, SchdIsPftPaid=:SchdIsPftPaid ,SchdIsPriPaid=:SchdIsPriPaid,  ");
		insertSql.append("   PenaltyPayNow = :PenaltyPayNow, LatePayPftPayNow = :LatePayPftPayNow ");
		insertSql.append(" where FinReference=:FinReference and FinRpyFor=:FinRpyFor and RpyDate=:RpyDate");

		//		insertSql.append("  (RpyDate, FinPriority, FinType, FinReference, FinRpyFor, Branch, CustomerID,");
		//		insertSql.append(" SchdPft, SchdPri, SchdPftPaid, SchdPriPaid, SchdPftBal, SchdPriBal, SchdIsPftPaid, SchdIsPriPaid,");
		//		insertSql.append(" SchdFee, SchdFeePaid, SchdFeeBal, SchdFeePayNow,");
		//		insertSql.append(" Values(:RpyDate, :FinPriority, :FinType, :FinReference, :FinRpyFor, :Branch, :CustomerID, ");
		//		insertSql.append(" :SchdPft, :SchdPri, :SchdPftPaid , :SchdPriPaid, :SchdPftBal, :SchdPriBal, :SchdIsPftPaid, :SchdIsPriPaid ,");
		//		insertSql.append(" :SchdFee, :SchdFeePaid, :SchdFeeBal, :SchdFeePayNow)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(repayQueue);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public void setFinRepayQueueRecords(List<FinRepayQueue> finRepayQueueList) {
		logger.debug("Entering");

		if (finRepayQueueList.size() > 0) {
			saveBatch(finRepayQueueList, "");
		}
		finRepayQueueList = null;
	}

	@Override
	public void deleteRepayQueue() {
		logger.debug("Entering");
		StringBuilder deleteSql = new StringBuilder(" DELETE FROM FinRpyQueue");
		logger.debug("updateSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new FinRepayQueue());
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public void deleteByCustID(long customerID) {
		logger.debug("Entering");
		FinRepayQueue repayQueue = new FinRepayQueue();
		repayQueue.setCustomerID(customerID);
		StringBuilder deleteSql = new StringBuilder(" DELETE FROM FinRpyQueue");
		deleteSql.append(" where CustomerID=:CustomerID");
		logger.debug("updateSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(repayQueue);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Get FinRepay Details for Auto Hunting
	 * 
	 * @param finReference
	 * @return
	 */
	public FinRepayQueue getFinRePayDetails(String finReference, Date repayDate) {
		logger.debug("Entering");

		FinRepayQueue finRepayQueue = new FinRepayQueue();
		finRepayQueue.setFinReference(finReference);
		finRepayQueue.setRpyDate(repayDate);

		StringBuilder selectSql = new StringBuilder(
				"SELECT RQ.FinReference, RQ.FinType, RQ.RpyDate, RQ.FinPriority, RQ.Branch, ");
		selectSql.append(" RQ.CustomerID, RQ.FinRpyFor, RQ.SchdPft, RQ.SchdPri, RQ.SchdPftPaid, RQ.SchdPriPaid, ");
		selectSql.append(" RQ.SchdPftBal, RQ.SchdPriBal, RQ.SchdIsPftPaid, RQ.SchdIsPriPaid, ");
		selectSql.append(
				" (RQ.SchdPftBal+ RQ.SchdPriBal) AS RepayQueueBal, PD.AcrTillLBD, PD.PftAmzSusp, PD.AmzTillLBD,PD.LpiTillLBD,PD.LppTillLBD,");
		selectSql.append(" RQ.SchdFee, RQ.SchdFeePaid, RQ.SchdFeeBal ");
		selectSql.append(" FROM FinRpyQueue RQ  INNER JOIN FinPftDetails PD ON PD.FinReference = RQ.FinReference");
		selectSql.append(
				" WHERE RQ.RpyDate <= :RpyDate AND (SchdIsPftPaid = 0 OR SchdIsPriPaid = 0) and RQ.FinReference =:FinReference ");
		selectSql.append(" ORDER BY RQ.RpyDate, RQ.FinPriority, RQ.FinReference, RQ.FinRpyFor ASC ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finRepayQueue);
		RowMapper<FinRepayQueue> typeRowMapper = BeanPropertyRowMapper.newInstance(FinRepayQueue.class);

		try {
			finRepayQueue = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finRepayQueue = null;
		}
		logger.debug("Leaving");
		return finRepayQueue;
	}

}
