package com.pennant.backend.dao.finance.impl;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.finance.FinLogEntryDetailDAO;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class FinLogEntryDetailDAOImpl extends SequenceDao<FinLogEntryDetail> implements FinLogEntryDetailDAO {
	private static Logger logger = LogManager.getLogger(FinLogEntryDetailDAOImpl.class);

	public FinLogEntryDetailDAOImpl() {
		super();
	}

	@Override
	public long save(FinLogEntryDetail entryDetail) {
		StringBuilder sql = new StringBuilder("insert into");
		sql.append(" FinLogEntryDetail");
		sql.append("( FinID, FinReference, LogKey, EventAction, SchdlRecal, PostDate, ReversalCompleted");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		entryDetail.setLogKey(getNextValue("SeqFinLogEntryDetail"));

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, entryDetail.getFinID());
			ps.setString(index++, entryDetail.getFinReference());
			ps.setLong(index++, entryDetail.getLogKey());
			ps.setString(index++, entryDetail.getEventAction());
			ps.setBoolean(index++, entryDetail.isSchdlRecal());
			ps.setDate(index++, JdbcUtil.getDate(entryDetail.getPostDate()));
			ps.setBoolean(index++, entryDetail.isReversalCompleted());
		});

		return entryDetail.getLogKey();
	}

	@Override
	public List<FinLogEntryDetail> getFinLogEntryDetailList(long finID, long logKey) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, LogKey, EventAction, SchdlRecal, PostDate, ReversalCompleted");
		sql.append(" From FinLogEntryDetail");
		sql.append(" Where LogKey > ? and FinID = ? and ReversalCompleted = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, logKey);
			ps.setLong(2, finID);
			ps.setBoolean(3, false);
		}, (rs, i) -> {
			FinLogEntryDetail logDtls = new FinLogEntryDetail();

			logDtls.setFinID(rs.getLong("FinID"));
			logDtls.setFinReference(rs.getString("FinReference"));
			logDtls.setLogKey(rs.getLong("LogKey"));
			logDtls.setEventAction(rs.getString("EventAction"));
			logDtls.setSchdlRecal(rs.getBoolean("SchdlRecal"));
			logDtls.setPostDate(JdbcUtil.getDate(rs.getDate("PostDate")));
			logDtls.setReversalCompleted(rs.getBoolean("ReversalCompleted"));

			return logDtls;
		});
	}

	@Override
	public FinLogEntryDetail getFinLogEntryDetailByLog(long logKey) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, LogKey, EventAction, SchdlRecal, PostDate, ReversalCompleted");
		sql.append(" From FinLogEntryDetail");
		sql.append(" Where LogKey = ? and ReversalCompleted = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, i) -> {
				FinLogEntryDetail logDtls = new FinLogEntryDetail();

				logDtls.setFinID(rs.getLong("FinID"));
				logDtls.setFinReference(rs.getString("FinReference"));
				logDtls.setLogKey(rs.getLong("LogKey"));
				logDtls.setEventAction(rs.getString("EventAction"));
				logDtls.setSchdlRecal(rs.getBoolean("SchdlRecal"));
				logDtls.setPostDate(JdbcUtil.getDate(rs.getDate("PostDate")));
				logDtls.setReversalCompleted(rs.getBoolean("ReversalCompleted"));

				return logDtls;
			}, logKey, 0);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void updateLogEntryStatus(FinLogEntryDetail logEntryDtls) {
		String sql = "Update FinLogEntryDetail set ReversalCompleted = ? Where LogKey = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			ps.setInt(1, 1);
			ps.setLong(2, logEntryDtls.getLogKey());
		});
	}

	@Override
	public Date getMaxPostDate(long finID) {
		String sql = "Select max(PostDate) from FinLogEntryDetail Where FinID = ? and SchdlRecal = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Date.class, finID, 1);
	}

	@Override
	public long getPrevSchedLogKey(long finID, Date date) {
		String sql = "Select min(LogKey) From FinLogEntryDetail Where PostDate > ? and SchdlRecal = ? and FinID = ?";

		logger.debug(Literal.SQL + sql);

		java.sql.Date postDate = JdbcUtil.getDate(date);

		return this.jdbcOperations.queryForObject(sql, Long.class, postDate, 1, finID);
	}

	@Override
	public Date getMaxPostDateByRef(long finID) {
		String sql = "Select max(PostDate) from FinLogEntryDetail Where FinID = ?";

		logger.trace(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Date.class, finID);
	}

	@Override
	public FinLogEntryDetail getFinLogEntryDetail(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, LogKey, EventAction, SchdlRecal, PostDate, ReversalCompleted");
		sql.append(" From FinLogEntryDetail");
		sql.append(" Where FinID = ? and LogKey = ");
		sql.append("(Select max(LogKey) From FinLogEntryDetail where FinID = ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, i) -> {
				FinLogEntryDetail logDtls = new FinLogEntryDetail();

				logDtls.setFinID(rs.getLong("FinID"));
				logDtls.setFinReference(rs.getString("FinReference"));
				logDtls.setLogKey(rs.getLong("LogKey"));
				logDtls.setEventAction(rs.getString("EventAction"));
				logDtls.setSchdlRecal(rs.getBoolean("SchdlRecal"));
				logDtls.setPostDate(JdbcUtil.getDate(rs.getDate("PostDate")));
				logDtls.setReversalCompleted(rs.getBoolean("ReversalCompleted"));

				return logDtls;
			}, finID, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}
}
