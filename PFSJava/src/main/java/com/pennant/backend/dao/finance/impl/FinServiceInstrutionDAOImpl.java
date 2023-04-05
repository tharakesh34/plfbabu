package com.pennant.backend.dao.finance.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.LMSServiceLog;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class FinServiceInstrutionDAOImpl extends SequenceDao<FinServiceInstruction> implements FinServiceInstrutionDAO {
	private static Logger logger = LogManager.getLogger(FinServiceInstrutionDAOImpl.class);

	public FinServiceInstrutionDAOImpl() {
		super();
	}

	public int saveList(List<FinServiceInstruction> fsiList, String type) {
		for (FinServiceInstruction fsi : fsiList) {
			if (fsi.getServiceSeqId() == Long.MIN_VALUE) {
				fsi.setServiceSeqId(getNextValue("SeqFinInstruction"));
			}
		}

		String sql = getInsertQuery(type);

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinServiceInstruction fsd = fsiList.get(i);
				parameterizedSetter(ps, fsd);
			}

			@Override
			public int getBatchSize() {
				return fsiList.size();
			}
		}).length;
	}

	public void save(FinServiceInstruction fsd, String type) {
		if (fsd.getServiceSeqId() == Long.MIN_VALUE) {
			fsd.setServiceSeqId(getNextValue("SeqFinInstruction"));
		}

		String sql = getInsertQuery(type);

		logger.debug(Literal.SQL + sql);

		try {
			jdbcOperations.update(sql, ps -> {
				parameterizedSetter(ps, fsd);
			});
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void deleteList(long finID, String finEvent, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinServiceInstruction");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, finID);
		});
	}

	@Override
	public List<FinServiceInstruction> getFinServiceInstructions(long finID, String type, String finEvent) {
		StringBuilder sql = sqlSelectQuery();
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ? and FinEvent = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinServiceInstructionRowMapper rowMapper = new FinServiceInstructionRowMapper();

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, finID);
			ps.setString(index, finEvent);
		}, rowMapper);
	}

	@Override
	public List<FinServiceInstruction> getFinServiceInstructions(long finID, String finEvent) {
		StringBuilder sql = new StringBuilder("Select * From (");
		sql.append(" Select ");
		sql.append(" ServiceSeqId, FinEvent, FinID, FinReference, FromDate, ToDate, PftDaysBasis, SchdMethod");
		sql.append(", ActualRate, BaseRate, SplRate, Margin, GrcPeriodEndDate, NextGrcRepayDate, RepayPftFrq");
		sql.append(", RepayRvwFrq, RepayCpzFrq, GrcPftFrq, GrcRvwFrq, GrcCpzFrq, RepayFrq, NextRepayDate");
		sql.append(", Amount, RecalType, RecalFromDate, RecalToDate, PftIntact, Terms, ServiceReqNo");
		sql.append(", Remarks, PftChg, InstructionUID, LinkedTranID, InitiatedDate, ApprovedDate");
		sql.append(", GrcPftRate, GraceBaseRate, GraceSpecialRate, GrcMargin");
		sql.append(" From FinServiceInstruction_Temp T1 ");
		sql.append(" Union All ");
		sql.append(" Select ");
		sql.append(" ServiceSeqId, FinEvent, FinID, FinReference, FromDate, ToDate, PftDaysBasis, SchdMethod");
		sql.append(", ActualRate, BaseRate, SplRate, Margin, GrcPeriodEndDate, NextGrcRepayDate, RepayPftFrq");
		sql.append(", RepayRvwFrq, RepayCpzFrq, GrcPftFrq, GrcRvwFrq, GrcCpzFrq, RepayFrq, NextRepayDate");
		sql.append(", Amount, RecalType, RecalFromDate, RecalToDate, PftIntact, Terms, ServiceReqNo");
		sql.append(", Remarks, PftChg, InstructionUID, LinkedTranID, InitiatedDate, ApprovedDate");
		sql.append(", GrcPftRate, GraceBaseRate, GraceSpecialRate, GrcMargin");
		sql.append(" From FinServiceInstruction T2");
		sql.append(" WHERE NOT (EXISTS (SELECT 1 FROM FinServiceInstruction_Temp T3");
		sql.append(" WHERE T3.Serviceseqid = T2.Serviceseqid))) T");
		sql.append(" Where FinID = ? and FinEvent = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinServiceInstructionRowMapper rowMapper = new FinServiceInstructionRowMapper();

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, finID);
			ps.setString(index, finEvent);
		}, rowMapper);
	}

	@Override
	public List<FinServiceInstruction> getFinServiceInstAddDisbDetail(long finID, Date fromDate, String finEvent) {
		StringBuilder sql = sqlSelectQuery();
		sql.append(" Where FinID = ? and FromDate = ? and FinEvent = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinServiceInstructionRowMapper rowMapper = new FinServiceInstructionRowMapper();

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, finID);
			ps.setDate(index++, JdbcUtil.getDate(fromDate));
			ps.setString(index, finEvent);
		}, rowMapper);
	}

	@Override
	public boolean getFinServInstDetails(String finEvent, String serviceReqNo) {
		String sql = "Select count(*) From FinServiceInstruction  Where FinEvent = ? and ServiceReqNo = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, finEvent, serviceReqNo) > 0;
	}

	@Override
	public List<FinServiceInstruction> getFinServInstByServiceReqNo(long finID, Date fromDate, String serviceReqNo,
			String finEvent) {
		StringBuilder sql = sqlSelectQuery();
		sql.append(" Where FinID = ? and FromDate = ? and FinEvent = ? and ServiceReqNo = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinServiceInstructionRowMapper rowMapper = new FinServiceInstructionRowMapper();

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, finID);
			ps.setDate(index++, JdbcUtil.getDate(fromDate));
			ps.setString(index++, finEvent);
			ps.setString(index, serviceReqNo);
		}, rowMapper);
	}

	@Override
	public List<LMSServiceLog> getLMSServiceLogList(String notificationFlag) {
		String sql = "Select Id, Event, FinID, FinReference, OldRate, NewRate, EffectiveDate, NotificationFlag From LMSServiceLog Where NotificationFlag = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.query(sql, ps -> {
			int index = 1;

			ps.setString(index, notificationFlag);
		}, (rs, num) -> {
			LMSServiceLog lmsLog = new LMSServiceLog();

			lmsLog.setId(rs.getLong("Id"));
			lmsLog.setEvent(rs.getString("Event"));
			lmsLog.setFinID(rs.getLong("FinID"));
			lmsLog.setFinReference(rs.getString("FinReference"));
			lmsLog.setOldRate(rs.getBigDecimal("OldRate"));
			lmsLog.setNewRate(rs.getBigDecimal("NewRate"));
			lmsLog.setEffectiveDate(rs.getDate("EffectiveDate"));
			lmsLog.setNotificationFlag(rs.getString("NotificationFlag"));

			return lmsLog;
		});
	}

	@Override
	public void updateNotificationFlag(String notificationFlag, long id) {
		String sql = "Update LMSServiceLog Set NotificationFlag = ? Where Id = ?";

		logger.debug(Literal.SQL + sql);

		try {
			this.jdbcOperations.update(sql, ps -> {
				int index = 1;

				ps.setString(index++, notificationFlag);
				ps.setLong(index, id);
			});
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public BigDecimal getOldRate(long finID, Date schdate) {
		String sql = "Select CalculatedRate From FinScheduleDetails Where FinID = ? and Schdate = (Select max(SchDate) From FinScheduleDetails Where FinID = ? and Schdate <= ?)";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finID, finID, schdate);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return BigDecimal.ZERO;
		}
	}

	@Override
	public BigDecimal getNewRate(long finID, Date schdate) {
		String sql = "Select CalculatedRate From FinScheduleDetails_Temp Where FinID = ? and Schdate = (Select max(SchDate) From FinScheduleDetails_Temp Where FinID = ? and Schdate <= ?)";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finID, finID, schdate);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return BigDecimal.ZERO;
		}
	}

	@Override
	public void saveLMSServiceLOGList(List<LMSServiceLog> slList) {
		StringBuilder sql = new StringBuilder("Insert Into");
		sql.append(" LMSServiceLog");
		sql.append("(Event, FinID, FinReference, OldRate, NewRate, EffectiveDate, NotificationFlag");
		sql.append(") values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				LMSServiceLog sl = slList.get(i);

				int index = 1;

				ps.setString(index++, sl.getEvent());
				ps.setLong(index++, sl.getFinID());
				ps.setString(index++, sl.getFinReference());
				ps.setBigDecimal(index++, sl.getOldRate());
				ps.setBigDecimal(index++, sl.getNewRate());
				ps.setDate(index++, JdbcUtil.getDate(sl.getEffectiveDate()));
				ps.setString(index, sl.getNotificationFlag());
			}

			@Override
			public int getBatchSize() {
				return slList.size();
			}
		});

	}

	@Override
	public List<String> getFinEventByFinRef(String finReference, String type) {
		StringBuilder sql = new StringBuilder("Select FinEvent");
		sql.append(" From FinServiceInstruction");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForList(sql.toString(), String.class, finReference);

	}

	@Override
	public List<FinServiceInstruction> getOrgFinServiceInstructions(long finID, String type) {
		StringBuilder sql = sqlSelectQuery();
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ? ");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index, finID);
		}, new FinServiceInstructionRowMapper());
	}

	@Override
	public boolean isFinServiceInstExists(long finID, String table) {
		StringBuilder sql = new StringBuilder("Select Count(FinID)");
		sql.append(" From FinServiceInstruction");
		sql.append(table);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, finID) > 0;
	}

	@Override
	public FinServiceInstruction getFinServiceInstDetailsBySerReqNo(long finID, String serviceReqNo) {
		StringBuilder sql = sqlSelectQuery();
		sql.append(" Where FinID = ? and ServiceReqNo = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new FinServiceInstructionRowMapper(), finID,
					serviceReqNo);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	private String getInsertQuery(String type) {
		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" FinServiceInstruction");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (ServiceSeqId, FinEvent, FinID, FinReference, FromDate, ToDate, PftDaysBasis");
		sql.append(", SchdMethod, ActualRate, BaseRate, SplRate, Margin, GrcPeriodEndDate");
		sql.append(", GrcPftRate, GraceBaseRate, GraceSpecialRate, GrcMargin");
		sql.append(", RepayPftFrq, RepayRvwFrq, RepayCpzFrq, GrcPftFrq, GrcRvwFrq, GrcCpzFrq");
		sql.append(", NextGrcRepayDate, RepayFrq, NextRepayDate, Amount, RecalType, RecalFromDate");
		sql.append(", RecalToDate, PftIntact, Terms, ServiceReqNo, Remarks, PftChg");
		sql.append(", InstructionUID, LinkedTranID, LogKey, InitiatedDate, ApprovedDate)");
		sql.append(" values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		return sql.toString();
	}

	private void parameterizedSetter(PreparedStatement ps, FinServiceInstruction fsd) throws SQLException {
		int index = 1;

		ps.setLong(index++, fsd.getServiceSeqId());
		ps.setString(index++, fsd.getFinEvent());
		ps.setLong(index++, fsd.getFinID());
		ps.setString(index++, fsd.getFinReference());
		ps.setDate(index++, JdbcUtil.getDate(fsd.getFromDate()));
		ps.setDate(index++, JdbcUtil.getDate(fsd.getToDate()));
		ps.setString(index++, fsd.getPftDaysBasis());
		ps.setString(index++, fsd.getSchdMethod());
		ps.setBigDecimal(index++, fsd.getActualRate());
		ps.setString(index++, fsd.getBaseRate());
		ps.setString(index++, fsd.getSplRate());
		ps.setBigDecimal(index++, fsd.getMargin());
		ps.setDate(index++, JdbcUtil.getDate(fsd.getGrcPeriodEndDate()));
		ps.setBigDecimal(index++, fsd.getGrcPftRate());
		ps.setString(index++, fsd.getGraceBaseRate());
		ps.setString(index++, fsd.getGraceSpecialRate());
		ps.setBigDecimal(index++, fsd.getGrcMargin());
		ps.setString(index++, fsd.getRepayPftFrq());
		ps.setString(index++, fsd.getRepayRvwFrq());
		ps.setString(index++, fsd.getRepayCpzFrq());
		ps.setString(index++, fsd.getGrcPftFrq());
		ps.setString(index++, fsd.getGrcRvwFrq());
		ps.setString(index++, fsd.getGrcCpzFrq());
		ps.setDate(index++, JdbcUtil.getDate(fsd.getNextGrcRepayDate()));
		ps.setString(index++, fsd.getRepayFrq());
		ps.setDate(index++, JdbcUtil.getDate(fsd.getNextRepayDate()));
		ps.setBigDecimal(index++, fsd.getAmount());
		ps.setString(index++, fsd.getRecalType());
		ps.setDate(index++, JdbcUtil.getDate(fsd.getRecalFromDate()));
		ps.setDate(index++, JdbcUtil.getDate(fsd.getRecalToDate()));
		ps.setBoolean(index++, fsd.isPftIntact());
		ps.setInt(index++, fsd.getTerms());
		ps.setString(index++, fsd.getServiceReqNo());
		ps.setString(index++, fsd.getRemarks());
		ps.setBigDecimal(index++, fsd.getPftChg());
		ps.setLong(index++, fsd.getInstructionUID());
		ps.setLong(index++, fsd.getLinkedTranID());
		ps.setObject(index++, fsd.getLogKey());
		ps.setDate(index++, JdbcUtil.getDate(fsd.getInitiatedDate()));
		ps.setDate(index, JdbcUtil.getDate(fsd.getApprovedDate()));

	}

	private StringBuilder sqlSelectQuery() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ServiceSeqId, FinEvent, FinID, FinReference, FromDate, ToDate, PftDaysBasis, SchdMethod");
		sql.append(", ActualRate, BaseRate, SplRate, Margin, GrcPeriodEndDate, NextGrcRepayDate, RepayPftFrq");
		sql.append(", RepayRvwFrq, RepayCpzFrq, GrcPftFrq, GrcRvwFrq, GrcCpzFrq, RepayFrq, NextRepayDate");
		sql.append(", Amount, RecalType, RecalFromDate, RecalToDate, PftIntact, Terms, ServiceReqNo");
		sql.append(", Remarks, PftChg, InstructionUID, LinkedTranID, InitiatedDate, ApprovedDate");
		sql.append(", GrcPftRate, GraceBaseRate, GraceSpecialRate, GrcMargin");
		sql.append(" From FinServiceInstruction");

		return sql;
	}

	private class FinServiceInstructionRowMapper implements RowMapper<FinServiceInstruction> {
		private FinServiceInstructionRowMapper() {
			super();
		}

		@Override
		public FinServiceInstruction mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinServiceInstruction fsi = new FinServiceInstruction();

			fsi.setServiceSeqId(rs.getLong("ServiceSeqId"));
			fsi.setFinEvent(rs.getString("FinEvent"));
			fsi.setFinID(rs.getLong("FinID"));
			fsi.setFinReference(rs.getString("FinReference"));
			fsi.setFromDate(rs.getTimestamp("FromDate"));
			fsi.setToDate(rs.getTimestamp("ToDate"));
			fsi.setPftDaysBasis(rs.getString("PftDaysBasis"));
			fsi.setSchdMethod(rs.getString("SchdMethod"));
			fsi.setActualRate(rs.getBigDecimal("ActualRate"));
			fsi.setBaseRate(rs.getString("BaseRate"));
			fsi.setSplRate(rs.getString("SplRate"));
			fsi.setMargin(rs.getBigDecimal("Margin"));
			fsi.setGrcPeriodEndDate(rs.getTimestamp("GrcPeriodEndDate"));
			fsi.setNextGrcRepayDate(rs.getTimestamp("NextGrcRepayDate"));
			fsi.setRepayPftFrq(rs.getString("RepayPftFrq"));
			fsi.setRepayRvwFrq(rs.getString("RepayRvwFrq"));
			fsi.setRepayCpzFrq(rs.getString("RepayCpzFrq"));
			fsi.setGrcPftFrq(rs.getString("GrcPftFrq"));
			fsi.setGrcRvwFrq(rs.getString("GrcRvwFrq"));
			fsi.setGrcCpzFrq(rs.getString("GrcCpzFrq"));
			fsi.setRepayFrq(rs.getString("RepayFrq"));
			fsi.setNextRepayDate(rs.getTimestamp("NextRepayDate"));
			fsi.setAmount(rs.getBigDecimal("Amount"));
			fsi.setRecalType(rs.getString("RecalType"));
			fsi.setRecalFromDate(rs.getTimestamp("RecalFromDate"));
			fsi.setRecalToDate(rs.getTimestamp("RecalToDate"));
			fsi.setPftIntact(rs.getBoolean("PftIntact"));
			fsi.setTerms(rs.getInt("Terms"));
			fsi.setServiceReqNo(rs.getString("ServiceReqNo"));
			fsi.setRemarks(rs.getString("Remarks"));
			fsi.setPftChg(rs.getBigDecimal("PftChg"));
			fsi.setInstructionUID(rs.getLong("InstructionUID"));
			fsi.setLinkedTranID(rs.getLong("LinkedTranID"));
			fsi.setInitiatedDate(rs.getTimestamp("InitiatedDate"));
			fsi.setApprovedDate(rs.getTimestamp("ApprovedDate"));
			fsi.setGrcPftRate(rs.getBigDecimal("GrcPftRate"));
			fsi.setGraceBaseRate(rs.getString("GraceBaseRate"));
			fsi.setGraceSpecialRate(rs.getString("GraceSpecialRate"));
			fsi.setGrcMargin(rs.getBigDecimal("GrcMargin"));

			return fsi;
		}
	}

	@Override
	public List<Date> getListDates(long finID, Date approvedDate) {
		String sql = "Select ApprovedDate From FinServiceInstruction Where FinID = ? and ApprovedDate > ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.query(sql, ps -> {
			ps.setLong(1, finID);
			ps.setDate(2, JdbcUtil.getDate(approvedDate));
		}, (rs, rowNum) -> {
			return rs.getDate("ApprovedDate");
		});
	}

}