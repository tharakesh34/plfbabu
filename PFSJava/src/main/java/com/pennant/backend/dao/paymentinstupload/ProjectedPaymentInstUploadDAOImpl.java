/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

package com.pennant.backend.dao.paymentinstupload;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.app.paymentinstuploadqueue.PaymentInstBulkUploadQueuing;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class ProjectedPaymentInstUploadDAOImpl extends BasicDao<PaymentInstBulkUploadQueuing>
		implements ProjectedPaymentInstUploadDAO {

	public ProjectedPaymentInstUploadDAOImpl() {
		super();
	}

	@Override
	public int preparePaymentInstBulkUploadQueue(long uploadHeaderId) {
		logger.debug(Literal.ENTERING);

		PaymentInstBulkUploadQueuing payInsQueuing = new PaymentInstBulkUploadQueuing();
		payInsQueuing.setThreadId(0);
		payInsQueuing.setEodDate(SysParamUtil.getAppDate());
		payInsQueuing.setEodProcess(false);
		payInsQueuing.setProgress(EodConstants.PROGRESS_WAIT);
		payInsQueuing.setUploadHeaderId(uploadHeaderId);
		payInsQueuing.setStartTime(DateUtility.getSysDate());

		StringBuilder sql = new StringBuilder(
				"INSERT INTO PaymentInstUploadQueuing (FinReference, UploadHeaderId, UploadDetailId,JSONOBJECT, EODDATE, THREADID, PROGRESS, STARTTIME, EODPROCESS)");
		sql.append(
				" SELECT Finreference,HeaderId, ID,JSONOBJECT, :EodDate, :ThreadId, :Progress, :StartTime, :EodProcess From PAYMINS_UPLOADS T WHERE T.HEADERID = :UploadHeaderId");
		sql.append(" AND T.STATUS= null  order by T.finreference,T.ID ");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(payInsQueuing);
		int ruRecords = this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
		return ruRecords;
	}

	@Override
	public int[] preparePaymentInstBulkUploadQueue(List<PaymentInstBulkUploadQueuing> uploadQueuings) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO PaymentInstUploadQueuing (FinReference,  UploadHeaderId, UploadDetailId");
		sql.append(", EODDATE, THREADID, PROGRESS, STARTTIME, EODPROCESS)");
		sql.append(" SELECT FinReference, HeaderId, ID");
		sql.append(", :EodDate, :ThreadId, :Progress, :StartTime, :EodProcess");
		sql.append(" From PAYMINS_UPLOADS T WHERE T.HEADERID = :UploadHeaderId");
		sql.append(" AND T.STATUS = 'S'  order by T.Finreference,T.ID ");

		logger.trace(Literal.SQL + sql.toString());

		logger.debug(Literal.LEAVING);
		return jdbcTemplate.batchUpdate(sql.toString(), SqlParameterSourceUtils.createBatch(uploadQueuings.toArray()));
	}

	@Override
	public List<Long> getThreads() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select distinct ThreadId FROM PaymentInstUploadQueuing order by ThreadId");

		logger.trace(Literal.SQL + sql.toString());

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.queryForList(sql.toString(), new MapSqlParameterSource(), Long.class);
	}

	@Override
	public void insertLogTableAndTruncate() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder(" INSERT INTO PaymentInstUploadQueuing_log");
		sql.append(" (UploadHeaderId,UploadDetailId,Finreference,");
		sql.append(" JSONOBJECT, EODDATE, THREADID, PROGRESS, STARTTIME,");
		sql.append(" ENDTIME, ERRORLOG, EODPROCESS)");
		sql.append(" (SELECT UploadHeaderId,UploadDetailId,Finreference,");
		sql.append(" JSONOBJECT, EODDATE, THREADID, PROGRESS, STARTTIME,");
		sql.append(" ENDTIME, ERRORLOG, EODPROCESS FROM PaymentInstUploadQueuing)");

		logger.trace(Literal.SQL + sql.toString());
		this.jdbcTemplate.update(sql.toString(), new MapSqlParameterSource());

		StringBuilder truncateSql = new StringBuilder(" TRUNCATE table PaymentInstUploadQueuing");
		this.jdbcTemplate.update(truncateSql.toString(), new MapSqlParameterSource());

		logger.debug(Literal.LEAVING);
	}

	@Override
	public long getCountByProgress() {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource beanParameters = new MapSqlParameterSource();
		beanParameters.addValue("Progress", EodConstants.PROGRESS_WAIT);
		beanParameters.addValue("THREADID", 0);

		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT count(DISTINCT Finreference) from PaymentInstUploadQueuing where Progress = :Progress and THREADID = :THREADID");
		logger.trace(Literal.SQL + sql.toString());

		long progressCount = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, Long.class);

		logger.debug(Literal.LEAVING);
		return progressCount;
	}

	@Override
	public void updateStatusQueue(long uploadHeaderId, long uploadDetailId, int progress) {

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("UploadHeaderId", uploadHeaderId);
		source.addValue("uploadDetailId", uploadDetailId);
		source.addValue("Progress", progress);
		source.addValue("EndTime", DateUtility.getSysDate());

		StringBuilder sql = new StringBuilder("Update PaymentInstUploadQueuing set");
		sql.append(" EndTime = :EndTime, Progress = :Progress");
		sql.append(" Where UploadHeaderId = :UploadHeaderId and uploadDetailId = :uploadDetailId ");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcTemplate.update(sql.toString(), source);
	}

	@Override
	public void updateFailedQueue(PaymentInstBulkUploadQueuing cdSchemeUploadQueuing) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update PaymentInstUploadQueuing set");
		sql.append(" EndTime = :EndTime, ErrorLog = :ErrorLog ");
		sql.append(" Where UploadHeaderId = :UploadHeaderId and uploaddetailId =:uploadDetailId");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(cdSchemeUploadQueuing);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public int updateThreadIDByRowNumber(long rowNum, int threadId) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("RowCount", rowNum);
		source.addValue("ThreadId", threadId);
		source.addValue("AcThreadId", 0);

		try {
			if (rowNum == 0) {
				String sql = "UPDATE PaymentInstUploadQueuing set ThreadId = :ThreadId Where ThreadId = :AcThreadId";
				logger.debug("selectSql: " + sql);
				return this.jdbcTemplate.update(sql, source);

			} else {
				StringBuilder sql = new StringBuilder("UPDATE PaymentInstUploadQueuing set ThreadId = :ThreadId ");
				sql.append(" where Finreference in (Select Finreference from ( ");
				sql.append(" Select Finreference,row_number() over(order by Finreference) row_numb from ");
				sql.append(" (Select distinct Finreference from PaymentInstUploadQueuing)T)T1 ");
				sql.append(" where row_numb<=:RowCount) and ThreadId=:AcThreadId ");

				logger.trace(Literal.SQL + sql.toString());

				return this.jdbcTemplate.update(sql.toString(), source);
			}

		} catch (DataAccessException dae) {
			logger.error("Exception: ", dae);
		}

		logger.debug("Leaving");
		return 0;
	}

}
