package com.pennanttech.bajaj.process;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennanttech.dataengine.DatabaseDataEngine;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;

public class DisbursemenIMPSResponseProcess extends DatabaseDataEngine {
	private static final Logger logger = Logger.getLogger(DisbursemenIMPSResponseProcess.class);

	List<Long> disbIdList = null;

	public DisbursemenIMPSResponseProcess(DataSource dataSource, long userId, Date valueDate, List<Long> disbIdList) {
		super(dataSource, App.DATABASE.name(), userId, true, valueDate);
		this.disbIdList = disbIdList;
		this.logBatch = true;
	}

	@Override
	protected void processData() {
		logger.debug(Literal.ENTERING);

		try {
			int count = updateDisbursements(disbIdList, executionStatus.getId());
			if (count > 0) {
				count = updateDisbResponse(disbIdList, executionStatus.getId());
			}

			if (count == 0) {
				throw new Exception("Unable to process the imps responce");
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			failedCount++;
			saveBatchLog(String.valueOf(executionStatus.getId()), "F", e.getMessage());
		} 

		logger.debug(Literal.LEAVING);
	}

	private int updateDisbursements(List<Long> disbIdList, long batchId) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuffer sql = new StringBuffer();

		try {
			sql.append("  UPDATE DISBURSEMENT_REQUESTS DR SET (DR.TRANSACTIONREF, DR.STATUS, DR.REMARKS, DR.REJECT_REASON, RESP_BATCH_ID) =");
			sql.append(" (SELECT IDR.TRANSACTIONID, CASE WHEN IDR.STATUS = 0 THEN :E ELSE :R END, IDR.DESCRIPTION, IDR.DESCRIPTION, :RESP_BATCH_ID ");
			sql.append(" FROM INT_DSBIMPS_RESPONSE IDR WHERE IDR.CHANNELPARTNERREFNO = DR.ID AND IDR.CHANNELPARTNERREFNO IN (:RESP_IDS))");
			sql.append(" WHERE EXISTS(SELECT IDR.TRANSACTIONID, CASE WHEN IDR.STATUS = 0 THEN :E ELSE :R END, IDR.DESCRIPTION,");
			sql.append(" :RESP_BATCH_ID FROM INT_DSBIMPS_RESPONSE IDR WHERE IDR.CHANNELPARTNERREFNO = DR.ID AND IDR.CHANNELPARTNERREFNO IN (:RESP_IDS))");

			source.addValue("RESP_IDS", disbIdList);
			source.addValue("RESP_BATCH_ID", batchId);
			source.addValue("E", "E");
			source.addValue("R", "R");

			return this.parameterJdbcTemplate.update(sql.toString(), source);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return 0;
	}

	private int updateDisbResponse(List<Long> disbIdList, long batchId) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuffer sql = new StringBuffer();

		try {
			sql.append(" UPDATE INT_DSBIMPS_RESPONSE SET PROCESSFLAG = :PROCESSFLAG WHERE CHANNELPARTNERREFNO");
			sql.append(" IN (SELECT ID FROM DISBURSEMENT_REQUESTS WHERE RESP_BATCH_ID = :RESP_BATCH_ID");
			sql.append(" AND STATUS IN (:STATUS)) ");

			source.addValue("STATUS", Arrays.asList("E", "R"));
			source.addValue("PROCESSFLAG", "Y");
			source.addValue("RESP_BATCH_ID", batchId);

			return this.parameterJdbcTemplate.update(sql.toString(), source);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return 0;
	}

	@Override
	protected MapSqlParameterSource mapData(ResultSet rs) throws Exception {
		return null;
	}
}
