package com.pennanttech.bajaj.process;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennanttech.dataengine.DatabaseDataEngine;
import com.pennanttech.pff.baja.BajajInterfaceConstants;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.Literal;

public class ALMRequestProcess extends DatabaseDataEngine {
	private static final Logger	logger	= Logger.getLogger(ALMRequestProcess.class);

	private Date				appDate;

	public ALMRequestProcess(DataSource dataSource, long userId, Date valueDate, Date appDate) {
		super(dataSource, App.DATABASE.name(), userId, true, valueDate, BajajInterfaceConstants.ALM_EXTRACT_STATUS);
		this.appDate = appDate;
	}

	@Override
	protected void processData() {
		logger.debug(Literal.ENTERING);
		
		loadCount();
		
		MapSqlParameterSource parmMap;
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT * from INT_ALM_VIEW");

		parmMap = new MapSqlParameterSource();

		jdbcTemplate.query(sql.toString(), parmMap, new ResultSetExtractor<Long>() {
			MapSqlParameterSource	map	= null;

			@Override
			public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
				String[] filterFields = new String[1];
				filterFields[0] = "AGREEMENTNO";
				while (rs.next()) {
					processedCount++;
					executionStatus.setProcessedRecords(processedCount);
					try {
						map = mapData(rs);
						saveOrUpdate(map, "ALM", destinationJdbcTemplate, filterFields);
						successCount++;
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						failedCount++;
						String keyId = rs.getString("AGREEMENTNO");

						if (StringUtils.trimToNull(keyId) == null) {
							keyId = String.valueOf(processedCount);
						}

						saveBatchLog(keyId, "F", e.getMessage());
					} finally {
						map = null;
					}
				}
				return totalRecords;
			}
		});
		logger.debug(Literal.LEAVING);
	}

	private void loadCount() {
		StringBuilder sql = new StringBuilder();

		MapSqlParameterSource parmMap = new MapSqlParameterSource();
		sql.append(" SELECT count(*) from INT_ALM_VIEW");

		try {
			totalRecords = jdbcTemplate.queryForObject(sql.toString(), parmMap, Integer.class);
			executionStatus.setTotalRecords(totalRecords);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	@Override
	protected MapSqlParameterSource mapData(ResultSet rs) throws Exception {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("AGREEMENTID", rs.getObject("AGREEMENTID"));
		map.addValue("AGREEMENTNO", rs.getObject("AGREEMENTNO"));
		map.addValue("PRODUCTFLAG", rs.getObject("PRODUCTFLAG"));
		map.addValue("NPA_STAGEID", rs.getObject("NPA_STAGEID"));
		map.addValue("INSTLAMT", rs.getObject("INSTLAMT"));
		map.addValue("PRINCOMP", rs.getObject("PRINCOMP"));
		map.addValue("INTCOMP", rs.getObject("INTCOMP"));
		map.addValue("DUEDATE", rs.getObject("DUEDATE"));
		map.addValue("ACCRUEDAMT", rs.getObject("ACCRUEDAMT"));
		map.addValue("ACCRUEDON", appDate);
		map.addValue("CUMULATIVE_ACCRUAL_AMT", rs.getObject("CUMULATIVE_ACCRUAL_AMT"));
		map.addValue("ADVFLAG", rs.getObject("ADVFLAG"));
		return map;
	}
}
