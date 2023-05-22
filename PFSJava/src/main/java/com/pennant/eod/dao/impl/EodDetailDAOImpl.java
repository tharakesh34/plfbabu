package com.pennant.eod.dao.impl;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.dao.EodDetailDAO;
import com.pennant.eod.model.EodDetail;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;

public class EodDetailDAOImpl extends BasicDao<EodDetail> implements EodDetailDAO {
	private static Logger logger = LogManager.getLogger(EodDetailDAOImpl.class);

	@Override
	public void save(EodDetail eodDetail) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("INSERT INTO EodDetails (ProcessDate,StatTime,EndTime,Status)");
		updateSql.append("  values (:ProcessDate,:StatTime,:EndTime,:Status)");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(eodDetail);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");

	}

	@Override
	public void update(EodDetail eodDetail) {
		logger.debug("Entering");

		Date processDate = DateUtil.getDate(
				DateUtil.format(eodDetail.getProcessDate(), PennantConstants.DBDateFormat),
				PennantConstants.DBDateFormat);
		eodDetail.setProcessDate(processDate);

		StringBuilder selectSql = new StringBuilder(
				"UPDATE EodDetails set EndTime=:EndTime, Status=:Status Where ProcessDate=:ProcessDate");

		logger.debug(Literal.SQL + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(eodDetail);
		this.jdbcTemplate.update(selectSql.toString(), beanParameters);

		logger.debug("Leaving");

	}

	@Override
	public void updateStatus(EodDetail eodDetail) {
		logger.debug("Entering");

		Date processDate = DateUtil.getDate(
				DateUtil.format(eodDetail.getProcessDate(), PennantConstants.DBDateFormat),
				PennantConstants.DBDateFormat);
		eodDetail.setProcessDate(processDate);

		StringBuilder selectSql = new StringBuilder(
				"UPDATE EodDetails set Status=:Status Where ProcessDate=:ProcessDate");

		logger.debug(Literal.SQL + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(eodDetail);
		this.jdbcTemplate.update(selectSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public EodDetail getEodDetailById(Date date) {
		EodDetail eodDetail = new EodDetail();
		eodDetail.setProcessDate(date);

		try {
			StringBuilder selectSql = new StringBuilder("SELECT * from EodDetails where  ProcessDate=:ProcessDate");
			logger.debug("selectSql: " + selectSql.toString());
			RowMapper<EodDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(EodDetail.class);
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(eodDetail);
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}
}
