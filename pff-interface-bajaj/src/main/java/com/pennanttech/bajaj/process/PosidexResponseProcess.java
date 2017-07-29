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
import org.springframework.transaction.TransactionStatus;

import com.pennanttech.dataengine.DatabaseDataEngine;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.baja.BajajInterfaceConstants;
import com.pennanttech.pff.baja.BajajInterfaceConstants.Status;
import com.pennanttech.pff.core.App;

public class PosidexResponseProcess extends DatabaseDataEngine {
	private static final Logger	logger	= Logger.getLogger(PosidexResponseProcess.class);

	public PosidexResponseProcess(DataSource dataSource, long userId, Date valueDate) {
		super(dataSource, App.DATABASE.name(), userId, false, valueDate, BajajInterfaceConstants.POSIDEX_RESPONSE_STATUS);
	}

	@Override
	protected void processData() {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource parmMap;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT UCIN_NO, CUSTOMER_NO FROM PSX_UCIN_REVERSE_FEED WHERE PROCESSED_FLAG = :PROCESSED_FLAG ");
		parmMap = new MapSqlParameterSource();

		parmMap.addValue("PROCESSED_FLAG", Status.N.name());

		destinationJdbcTemplate.query(sql.toString(), parmMap, new ResultSetExtractor<Long>() {
			TransactionStatus	txnStatus	= null;

			@Override
			public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
				while (rs.next()) {

					MapSqlParameterSource custMap = null;
					MapSqlParameterSource dataMap = null;

					executionStatus.setProcessedRecords(processedCount++);
					try {
						custMap = mapCustData(rs);
						dataMap = mapData(rs);

						updateCustomer(custMap);
						updateDataStatus(dataMap);

						executionStatus.setSuccessRecords(successCount++);
					} catch (Exception e) {
						logger.error(Literal.ENTERING);
						executionStatus.setFailedRecords(failedCount++);

						String keyId = rs.getString("CUSTOMER_ID");
						if (StringUtils.trimToNull(keyId) == null) {
							keyId = String.valueOf(processedCount);
						}

						saveBatchLog(keyId, "F", e.getMessage());
					} finally {
						txnStatus.flush();
						txnStatus = null;
					}
				}
				return totalRecords;
			}
		});
	}

	@Override
	protected MapSqlParameterSource mapData(ResultSet rs) throws Exception {
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue("PROCESSED_FLAG", Status.Y.name());
		map.addValue("UCIN_NO", rs.getObject("UCIN_NO"));
		return map;
	}

	private MapSqlParameterSource mapCustData(ResultSet rs) throws SQLException {
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue("CUSTADDLVAR1", rs.getObject("UCIN_NO"));
		map.addValue("CUSTID", rs.getObject("CUSTOMER_NO"));
		return map;
	}

	private void updateCustomer(MapSqlParameterSource custMap) throws Exception {

		StringBuilder sql;
		try {
			sql = new StringBuilder();
			sql.append("UPDATE CUSTOMERS SET CUSTADDLVAR1 = :CUSTADDLVAR1 WHERE CUSTID = :CUSTID");

			this.jdbcTemplate.update(sql.toString(), custMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		} 
	}

	private void updateDataStatus(MapSqlParameterSource dataMap) throws Exception {
		StringBuilder sql = null;
		try {
			sql = new StringBuilder();
			sql.append(" UPDATE PSX_UCIN_REVERSE_FEED SET PROCESSED_FLAG = :PROCESSED_FLAG WHERE UCIN_NO = :UCIN_NO ");

			this.jdbcTemplate.update(sql.toString(), dataMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		} 
	}

}
