package com.pennant.backend.dao.finance.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinLogEntryDetailDAO;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

public class FinLogEntryDetailDAOImpl extends SequenceDao<FinLogEntryDetail> implements FinLogEntryDetailDAO {
	private static Logger logger = Logger.getLogger(FinLogEntryDetailDAOImpl.class);

	public FinLogEntryDetailDAOImpl() {
		super();
	}

	@Override
	public long save(FinLogEntryDetail entryDetail) {
		logger.debug("Entering");

		entryDetail.setLogKey(getNextValue("seqFinLogEntryDetail"));

		StringBuilder insertSql = new StringBuilder(" Insert Into FinLogEntryDetail ");
		insertSql.append(" (FinReference, LogKey, EventAction, SchdlRecal, PostDate, ReversalCompleted ) ");
		insertSql.append(" Values (:FinReference, :LogKey, :EventAction, :SchdlRecal, :PostDate, :ReversalCompleted)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(entryDetail);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
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
		RowMapper<FinLogEntryDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinLogEntryDetail.class);
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
		RowMapper<FinLogEntryDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinLogEntryDetail.class);
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

}
