package com.pennant.backend.dao.finance.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.finance.OverdraftScheduleDetailDAO;
import com.pennant.backend.model.finance.OverdraftMovements;
import com.pennant.backend.model.finance.OverdraftScheduleDetail;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.util.DateUtil;

public class OverdraftScheduleDetailDAOImpl extends BasicDao<OverdraftScheduleDetail>
		implements OverdraftScheduleDetailDAO {
	private static Logger logger = LogManager.getLogger(OverdraftScheduleDetailDAOImpl.class);

	public OverdraftScheduleDetailDAOImpl() {
		super();
	}

	@Override
	public void saveList(List<OverdraftScheduleDetail> overdraftScheduleDetail, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into ");

		insertSql.append(" OverdraftScheduleDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, DroplineDate, ActualRate, BaseRate,");
		insertSql.append(" SplRate, Margin, DroplineRate, LimitDrop,ODLimit,LimitIncreaseAmt)");

		insertSql.append(" Values(:FinReference, :DroplineDate, :ActualRate, :BaseRate,");
		insertSql.append(" :SplRate, :Margin, :DroplineRate, :LimitDrop, :ODLimit,:LimitIncreaseAmt) ");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(overdraftScheduleDetail.toArray());
		try {
			this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error("Exception", e);
			throw e;
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for fetch Overdraft Details
	 */
	@Override
	public List<OverdraftScheduleDetail> getOverdraftScheduleDetails(String finRef, String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, DroplineDate, ActualRate, BaseRate");
		sql.append(", SplRate, Margin, DroplineRate, LimitDrop, ODLimit, LimitIncreaseAmt");

		if (isWIF) {
			sql.append(" From WIFOverdraftScheduleDetail");
		} else {
			sql.append(" From OverdraftScheduleDetail");
		}
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ?");

		List<OverdraftScheduleDetail> odSchedules = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, finRef);
		}, (rs, rowNum) -> {
			OverdraftScheduleDetail schd = new OverdraftScheduleDetail();

			schd.setFinReference(rs.getString("FinReference"));
			schd.setDroplineDate(JdbcUtil.getDate(rs.getDate("DroplineDate")));
			schd.setActualRate(rs.getBigDecimal("ActualRate"));
			schd.setBaseRate(rs.getString("BaseRate"));
			schd.setSplRate(rs.getString("SplRate"));
			schd.setMargin(rs.getBigDecimal("Margin"));
			schd.setDroplineRate(rs.getBigDecimal("DroplineRate"));
			schd.setLimitDrop(rs.getBigDecimal("LimitDrop"));
			schd.setODLimit(rs.getBigDecimal("ODLimit"));
			schd.setLimitIncreaseAmt(rs.getBigDecimal("LimitIncreaseAmt"));

			return schd;
		});

		return sortODSchedules(odSchedules);

	}

	private List<OverdraftScheduleDetail> sortODSchedules(List<OverdraftScheduleDetail> odSchedules) {
		return odSchedules.stream().sorted((od1, od2) -> DateUtil.compare(od1.getDroplineDate(), od2.getDroplineDate()))
				.collect(Collectors.toList());
	}

	/**
	 * Method for deletion overdraft schedule details
	 * 
	 * @param id
	 * @param type
	 * @param isWIF
	 */
	@Override
	public void deleteByFinReference(String id, String type, boolean isWIF) {
		logger.debug("Entering");
		OverdraftScheduleDetail scheduleDetail = new OverdraftScheduleDetail();
		scheduleDetail.setFinReference(id);

		StringBuilder deleteSql = new StringBuilder("Delete From ");
		if (isWIF) {
			deleteSql.append(" WIFOverdraftScheduleDetail");
		} else {
			deleteSql.append(" OverdraftScheduleDetail");
		}

		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scheduleDetail);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Method to save the overdraft movements
	 * 
	 * @param Overdraft
	 *            Movements
	 */
	@Override
	public void saveOverdraftMovement(OverdraftMovements overdraftMovements) {
		logger.debug("Entering");

		overdraftMovements.setODSeqID(getodSeqID() + 1);

		StringBuilder insertSql = new StringBuilder("Insert Into ");
		insertSql.append(" OverdraftMovements");
		insertSql.append(" (FinReference,ODSeqID, DroplineDate, Tenor, ODExpiryDate,");
		insertSql.append(" DroplineFrq, LimitChange, ODLimit,ValueDate)");
		insertSql.append(" Values(:FinReference, :ODSeqID,:DroplineDate, :Tenor, :ODExpiryDate,");
		insertSql.append(" :DroplineFrq, :LimitChange, :ODLimit, :ValueDate) ");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(overdraftMovements);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");

	}

	private long getodSeqID() {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select COALESCE(max(odSeqID), 0) From OverdraftMovements");
		long odSeq = 0;
		logger.debug("selectSql: " + selectSql.toString());
		try {
			odSeq = this.jdbcTemplate.queryForObject(selectSql.toString(), new MapSqlParameterSource(), Long.class);

		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			odSeq = Long.MIN_VALUE;
		}
		logger.debug("Leaving");
		return odSeq;
	}
}