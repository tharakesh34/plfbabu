package com.pennant.backend.dao.FinRepayQueue.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.dao.FinRepayQueue.FinRepayQueueDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;

public class FinRepayQueueDAOImpl extends BasicDao<FinRepayQueue> implements FinRepayQueueDAO {
	private static Logger logger = LogManager.getLogger(FinRepayQueueDAOImpl.class);

	public FinRepayQueueDAOImpl() {
		super();
	}

	public void saveBatch(List<FinRepayQueue> finRepayQueueList, String type) {
		StringBuilder sql = new StringBuilder("Insert Into FinRpyQueue");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (RpyDate, FinPriority, FinType, FinID, FinReference, FinRpyFor, Branch, CustomerID");
		sql.append(", SchdPft, SchdPri, SchdPftPaid, SchdPriPaid, SchdPftBal, SchdPriBal");
		sql.append(", SchdIsPftPaid, SchdIsPriPaid, SchdFee, SchdFeePaid, SchdFeeBal");
		sql.append(", SchdFeePayNow, PenaltyPayNow, LatePayPftPayNow, SchdRate, LinkedFinRef)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinRepayQueue frq = finRepayQueueList.get(i);
				int index = 1;

				ps.setDate(index++, JdbcUtil.getDate(frq.getRpyDate()));
				ps.setInt(index++, frq.getFinPriority());
				ps.setString(index++, frq.getFinType());
				ps.setLong(index++, frq.getFinID());
				ps.setString(index++, frq.getFinReference());
				ps.setString(index++, frq.getFinRpyFor());
				ps.setString(index++, frq.getBranch());
				ps.setLong(index++, frq.getCustomerID());
				ps.setBigDecimal(index++, frq.getSchdPft());
				ps.setBigDecimal(index++, frq.getSchdPri());
				ps.setBigDecimal(index++, frq.getSchdPftPaid());
				ps.setBigDecimal(index++, frq.getSchdPriPaid());
				ps.setBigDecimal(index++, frq.getSchdPftBal());
				ps.setBigDecimal(index++, frq.getSchdPriBal());
				ps.setBoolean(index++, frq.isSchdIsPftPaid());
				ps.setBoolean(index++, frq.isSchdIsPriPaid());
				ps.setBigDecimal(index++, frq.getSchdFee());
				ps.setBigDecimal(index++, frq.getSchdFeePaid());
				ps.setBigDecimal(index++, frq.getSchdFeeBal());
				ps.setBigDecimal(index++, frq.getSchdFeePayNow());
				ps.setBigDecimal(index++, frq.getPenaltyPayNow());
				ps.setBigDecimal(index++, frq.getLatePayPftPayNow());
				ps.setBigDecimal(index++, frq.getSchdRate());
				ps.setString(index++, frq.getLinkedFinRef());
			}

			@Override
			public int getBatchSize() {
				return finRepayQueueList.size();
			}
		});
	}

	@Override
	public void update(FinRepayQueue frq, String type) {
		StringBuilder sql = new StringBuilder("Update FinRpyQueue");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set SchdPftPaid = ?, SchdPriPaid = ?, SchdIsPftPaid = ?, SchdIsPriPaid = ?");
		sql.append(" PenaltyPayNow = ?, LatePayPftPayNow = ?");
		sql.append(" Where FinID = ? and FinRpyFor= ? and RpyDate = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, frq.getSchdPftPaid());
			ps.setBigDecimal(index++, frq.getSchdPriPaid());
			ps.setBoolean(index++, frq.isSchdIsPftPaid());
			ps.setBoolean(index++, frq.isSchdIsPriPaid());
			ps.setBigDecimal(index++, frq.getPenaltyPayNow());
			ps.setBigDecimal(index++, frq.getLatePayPftPayNow());

			ps.setLong(index++, frq.getFinID());
			ps.setString(index++, frq.getFinRpyFor());
			ps.setDate(index++, JdbcUtil.getDate(frq.getRpyDate()));
		});
	}

	@Override
	public void setFinRepayQueueRecords(List<FinRepayQueue> finRepayQueueList) {
		if (finRepayQueueList.size() > 0) {
			saveBatch(finRepayQueueList, "");
		}

		finRepayQueueList = null;
	}

}
