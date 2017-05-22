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
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.Literal;

public class ALMRequestProcess extends DatabaseDataEngine {
	private static final Logger logger = Logger.getLogger(ALMRequestProcess.class);

	private Date dueDateFrom;
	private Date dueDateTo;

	public ALMRequestProcess(DataSource dataSource, Date dueDateFrom, Date dueDateTo, long userId, Date valueDate) {
		super(dataSource, App.DATABASE.name(), userId, valueDate);
		this.dueDateFrom = dueDateFrom;
		this.dueDateTo = dueDateTo;
	}

	@Override
	protected void processData() {
		logger.debug(Literal.ENTERING);
		
		executionStatus.setRemarks("Loading data..");
		MapSqlParameterSource parmMap;
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT * from INT_ALM_VIEW  Where DUEDATE >= :DueDateFrom AND DUEDATE <= :DueDateTo ");
		
		parmMap = new MapSqlParameterSource();
		parmMap.addValue("DueDateFrom", dueDateFrom);
		parmMap.addValue("DueDateTo", dueDateTo);

		jdbcTemplate.query(sql.toString(), parmMap, new ResultSetExtractor<Integer>() {
			MapSqlParameterSource map = null;
			TransactionStatus txnStatus = null;
			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
				
				while (rs.next()) {
					executionStatus.setRemarks("processing the record " + ++totalRecords);
					processedCount++;
					txnStatus = transManager.getTransaction(transDef);
					try {
						map = mapData(rs);
						insertData(map, "ALM", destinationJdbcTemplate);
						successCount++;
						transManager.commit(txnStatus);
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						transManager.rollback(txnStatus);
						failedCount++;
						String keyId = rs.getString("AGREEMENTID");
						if (StringUtils.trimToNull(keyId) == null) {
							keyId = String.valueOf(processedCount);
						}
						saveBatchLog(keyId, "F", e.getMessage());
					} finally {
						map = null;
						txnStatus.flush();
						txnStatus = null;
					}
				}
				return totalRecords;
			}
		});
		logger.debug(Literal.LEAVING);
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
		map.addValue("ACCRUEDON", rs.getObject("ACCRUEDON"));
		map.addValue("CUMULATIVE_ACCRUAL_AMT", rs.getObject("CUMULATIVE_ACCRUAL_AMT"));
		map.addValue("ADVFLAG", rs.getObject("ADVFLAG"));
		return map;
	}
}
