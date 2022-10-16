package com.pennant.pff.presentment.dao.impl;

import java.sql.Timestamp;
import java.util.Date;

import com.pennant.pff.presentment.dao.ConsecutiveBounceDAO;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.presentment.model.ConsecutiveBounce;

public class ConsecutiveBounceDAOImpl extends SequenceDao<ConsecutiveBounce> implements ConsecutiveBounceDAO {

	public ConsecutiveBounceDAOImpl() {
		super();
	}

	@Override
	public ConsecutiveBounce getBounces(long mandateId) {
		String sql = "Select ID, BounceId, LastBounceDate, BounceCount From Presentment_Consecutive_Bounce Where MandateId = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				ConsecutiveBounce cb1 = new ConsecutiveBounce();

				cb1.setId(rs.getLong("ID"));
				cb1.setBounceID(rs.getLong("BounceId"));
				cb1.setLastBounceDate(rs.getTimestamp("LastBounceDate"));
				cb1.setBounceCount(rs.getInt("BounceCount"));

				return cb1;
			}, mandateId);
		} catch (Exception e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void create(long mandateId, long bounceId, Date schdDate) {
		StringBuilder sql = new StringBuilder("Insert Into Presentment_Consecutive_Bounce");
		sql.append("(MandateId, BounceId, LastBounceDate, BounceCount, CreatedOn, LastMnton");
		sql.append(") Values (");
		sql.append("?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, mandateId);
			ps.setLong(index++, bounceId);
			ps.setDate(index++, JdbcUtil.getDate(schdDate));
			ps.setInt(index++, 1);
			ps.setDate(index++, JdbcUtil.getDate(DateUtil.getSysDate()));
			ps.setTimestamp(index++, new Timestamp(System.currentTimeMillis()));
		});
	}

	@Override
	public void update(long mandateId, Date schdDate, int bounceCount) {
		String sql = "Update Presentment_Consecutive_Bounce Set LastBounceDate = ?, BounceCount = ?, LastMnton = ? Where MandateId = ?";

		logger.debug(Literal.SQL.concat(sql));

		int recordCount = this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setDate(index++, JdbcUtil.getDate(schdDate));
			ps.setLong(index++, bounceCount);
			ps.setTimestamp(index++, new Timestamp(System.currentTimeMillis()));

			ps.setLong(index++, mandateId);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void resetConter(long mandateId, long bounceId, Date schdDate) {
		String sql = "Update Presentment_Consecutive_Bounce Set BounceId = ?, LastBounceDate = ?, BounceCount = ?, LastMnton = ? Where MandateId = ?";

		logger.debug(Literal.SQL.concat(sql));

		int recordCount = this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setLong(index++, bounceId);
			ps.setDate(index++, JdbcUtil.getDate(schdDate));
			ps.setLong(index++, 1);
			ps.setTimestamp(index++, new Timestamp(System.currentTimeMillis()));

			ps.setLong(index++, mandateId);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public int delete(long mandateId) {
		String sql = "Delete From Presentment_Consecutive_Bounce Where MandateId = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.update(sql, mandateId);
	}

}
