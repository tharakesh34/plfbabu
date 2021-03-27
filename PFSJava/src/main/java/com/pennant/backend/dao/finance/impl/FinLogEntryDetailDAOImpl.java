package com.pennant.backend.dao.finance.impl;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.FinLogEntryDetailDAO;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class FinLogEntryDetailDAOImpl extends SequenceDao<FinLogEntryDetail> implements FinLogEntryDetailDAO {
	private static Logger logger = LogManager.getLogger(FinLogEntryDetailDAOImpl.class);

	public FinLogEntryDetailDAOImpl() {
		super();
	}

	@Override
	public long save(FinLogEntryDetail entryDetail) {
		StringBuilder sql = new StringBuilder("insert into");
		sql.append(" FinLogEntryDetail ");
		sql.append("(FinReference, LogKey, EventAction, SchdlRecal, PostDate, ReversalCompleted");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?");
		sql.append(")");

		entryDetail.setLogKey(getNextValue("seqFinLogEntryDetail"));

		logger.trace(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
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
	public List<FinLogEntryDetail> getFinLogEntryDetailList(String finReference, long logKey) {
		logger.debug("Entering");

		FinLogEntryDetail finLogEntryDetail = new FinLogEntryDetail();
		finLogEntryDetail.setFinReference(finReference);
		finLogEntryDetail.setLogKey(logKey);

		StringBuilder selectSql = new StringBuilder(
				" Select T1.FinReference, T1.LogKey, T1.EventAction, T1.SchdlRecal, T1.PostDate, T1.ReversalCompleted ");
		selectSql.append(" From FinLogEntryDetail T1 ");
		selectSql.append(" Where T1.FinReference =:FinReference AND T1.LogKey >:LogKey AND T1.ReversalCompleted = 0 ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finLogEntryDetail);
		RowMapper<FinLogEntryDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(FinLogEntryDetail.class);
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public FinLogEntryDetail getFinLogEntryDetail(long logKey) {
		logger.debug("Entering");

		FinLogEntryDetail finLogEntryDetail = new FinLogEntryDetail();
		finLogEntryDetail.setLogKey(logKey);

		StringBuilder selectSql = new StringBuilder(
				" Select T1.FinReference, T1.LogKey, T1.EventAction, T1.SchdlRecal, T1.PostDate, T1.ReversalCompleted ");
		selectSql.append(" From FinLogEntryDetail T1 Where T1.LogKey =:LogKey AND T1.ReversalCompleted = 0 ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finLogEntryDetail);
		RowMapper<FinLogEntryDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(FinLogEntryDetail.class);
		logger.debug("Leaving");
		try {
			finLogEntryDetail = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (Exception e) {
			logger.warn("Exception: ", e);
			finLogEntryDetail = null;
		}
		return finLogEntryDetail;

	}

	@Override
	public void updateLogEntryStatus(FinLogEntryDetail finLogEntryDetail) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("  Update FinLogEntryDetail SET ReversalCompleted = 1 ");
		selectSql.append(" WHERE LogKey =:LogKey ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finLogEntryDetail);

		logger.debug("Leaving");
		this.jdbcTemplate.update(selectSql.toString(), beanParameters);
	}

	/**
	 * get postdate for particular finreference where schdRecal is happend
	 * 
	 * @param finReference
	 *            Ticket id:124998
	 */
	@Override
	public Date getMaxPostDate(String finReference) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("  select max(POSTDATE) from FINLOGENTRYDETAIL");
		selectSql.append(" where FinReference = :FinReference and SCHDLRECAL=1 ");

		logger.debug("selectSql: " + selectSql.toString());

		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();

		mapSqlParameterSource.addValue("FinReference", finReference);

		Date maxPostDate = null;
		try {
			maxPostDate = this.jdbcTemplate.queryForObject(selectSql.toString(), mapSqlParameterSource, Date.class);
		} catch (DataAccessException e) {
			logger.warn("Exception: ", e);
			maxPostDate = null;
		}
		return maxPostDate;
	}

	@Override
	public long getPrevSchedLogKey(String finReference, Date date) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("MonthEndDate", date);
		source.addValue("SchdlRecal", 1);

		StringBuilder selectSql = new StringBuilder(" Select MIN(LOGKEY)  ");
		selectSql.append(" From FINLOGENTRYDETAIL");
		selectSql.append(
				" Where PostDate > :MonthEndDate AND SchdlRecal = :SchdlRecal AND  FinReference = :FinReference ");

		logger.debug("selectSql: " + selectSql.toString());

		Long logKey;
		try {
			logKey = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.info(e);
			logKey = 0L;
		}

		if (logKey == null) {
			logKey = 0L;
		}

		logger.debug("Leaving");
		return logKey;
	}

	@Override
	public Date getMaxPostDateByRef(String finReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" max(POSTDATE) from FINLOGENTRYDETAIL");
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference }, Date.class);
		} catch (DataAccessException e) {
			logger.warn("Record is not found in FINLOGENTRYDETAIL table for the specified FinReference >> {}",
					finReference);
		}

		return null;
	}

}
