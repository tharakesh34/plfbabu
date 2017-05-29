package com.pennant.eod.dao.impl;

import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.dao.EodDetailDAO;
import com.pennant.eod.model.EodDetail;

public class EodDetailDAOImpl implements EodDetailDAO {

	private static Logger				logger	= Logger.getLogger(EodDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	@Override
	public void save(EodDetail eodDetail) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("INSERT INTO EodDetails (ProcessDate,StatTime,EndTime,Status)");
		updateSql.append("  values (:ProcessDate,:StatTime,:EndTime,:Status)");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(eodDetail);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");

	}

	@Override
	public void update(EodDetail eodDetail) {
		logger.debug("Entering");

		Date processDate = DateUtility.getDate(
				DateUtility.formateDate(eodDetail.getProcessDate(), PennantConstants.DBDateFormat),
				PennantConstants.DBDateFormat);
		eodDetail.setProcessDate(processDate);

		StringBuilder selectSql = new StringBuilder(
				"UPDATE EodDetails set EndTime=:EndTime, Status=:Status Where ProcessDate=:ProcessDate");

		logger.debug("selectSql: " + selectSql.toString());

		try {
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(eodDetail);
			this.namedParameterJdbcTemplate.update(selectSql.toString(), beanParameters);
		} catch (EmptyResultDataAccessException dae) {
			logger.error(dae);
		}
		logger.debug("Leaving");

	}

	@Override
	public void updateStatus(EodDetail eodDetail) {
		logger.debug("Entering");

		Date processDate = DateUtility.getDate(
				DateUtility.formateDate(eodDetail.getProcessDate(), PennantConstants.DBDateFormat),
				PennantConstants.DBDateFormat);
		eodDetail.setProcessDate(processDate);

		StringBuilder selectSql = new StringBuilder(
				"UPDATE EodDetails set Status=:Status Where ProcessDate=:ProcessDate");

		logger.debug("selectSql: " + selectSql.toString());

		try {
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(eodDetail);
			this.namedParameterJdbcTemplate.update(selectSql.toString(), beanParameters);
		} catch (EmptyResultDataAccessException dae) {
			logger.error(dae);
		}
		logger.debug("Leaving");

	}

	@Override
	public EodDetail getEodDetailById(Date date) {
		EodDetail eodDetail = new EodDetail();
		eodDetail.setProcessDate(date);

		try {
			StringBuilder selectSql = new StringBuilder("SELECT * from EodDetails where  ProcessDate=:ProcessDate");
			logger.debug("selectSql: " + selectSql.toString());
			RowMapper<EodDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(EodDetail.class);
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(eodDetail);
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			return null;
		}
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

}
