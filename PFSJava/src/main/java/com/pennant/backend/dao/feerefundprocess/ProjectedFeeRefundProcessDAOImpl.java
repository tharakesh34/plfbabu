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

package com.pennant.backend.dao.feerefundprocess;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.app.feerefundqueue.FeeRefundProcessQueuing;
import com.pennant.app.util.SysParamUtil;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class ProjectedFeeRefundProcessDAOImpl extends BasicDao<FeeRefundProcessQueuing>
		implements ProjectedFeeRefundProcessDAO {
	private static Logger logger = LogManager.getLogger(ProjectedFeeRefundProcessDAOImpl.class);

	private static final String UPDATE_SQL = "UPDATE FeeRefundProcessQueuing set ThreadId = :ThreadId Where ThreadId = :AcThreadId";

	public ProjectedFeeRefundProcessDAOImpl() {
		super();
	}

	@Override
	public int prepareFeeRefundProcessQueue(long id) {
		logger.debug(Literal.ENTERING);

		FeeRefundProcessQueuing frpQueuing = new FeeRefundProcessQueuing();
		frpQueuing.setThreadId(0);
		frpQueuing.setEodDate(SysParamUtil.getAppDate());
		frpQueuing.setEodProcess(false);
		frpQueuing.setProgress(EodConstants.PROGRESS_WAIT);
		frpQueuing.setFeeRefundHeaderId(id);
		frpQueuing.setStartTime(DateUtil.getSysDate());

		StringBuilder sql = new StringBuilder(
				"INSERT INTO FeeRefundProcessQueuing (FINREFERENCE,FeeRefundHeaderId,JSONOBJECT, EODDATE, THREADID, PROGRESS, STARTTIME, EODPROCESS)");
		sql.append(
				" SELECT Reference,ID,JSONOBJECT, :EodDate, :ThreadId, :Progress, :StartTime, :EodProcess From FEE_REFUND_HEADER T WHERE T.ID = :ID");
		sql.append(" AND T.STATUS != 'Approved'  order by T.Reference,T.ID ");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(frpQueuing);
		int ruRecords = this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
		return ruRecords;
	}

	@Override
	public int[] prepareFeeRefundProcessQueue(List<FeeRefundProcessQueuing> frdQueuings) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO FeeRefundProcessQueuing (FINREFERENCE,  FeeRefundHeaderId");
		sql.append(", EODDATE, THREADID, PROGRESS, STARTTIME, EODPROCESS)");
		sql.append(" SELECT FINREFERENCE, ID");
		sql.append(", :EodDate, :ThreadId, :Progress, :StartTime, :EodProcess");
		sql.append(" From Fee_Refund_Header_view T WHERE T.ID = :FeeRefundHeaderId");
		sql.append(" AND T.APPROVALSTATUS in ('2', '3')  order by T.FinReference,T.ID ");

		logger.trace(Literal.SQL + sql.toString());

		logger.debug(Literal.LEAVING);
		return jdbcTemplate.batchUpdate(sql.toString(), SqlParameterSourceUtils.createBatch(frdQueuings.toArray()));
	}

	@Override
	public List<Long> getThreads() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select distinct ThreadId FROM FeeRefundProcessQueuing order by ThreadId");

		logger.trace(Literal.SQL + sql.toString());

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.queryForList(sql.toString(), new MapSqlParameterSource(), Long.class);
	}

	@Override
	public void insertLogTableAndTruncate() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder(" INSERT INTO FeeRefundProcessQueuing_log");
		sql.append(" (FEEREFUNDHEADERID,FINREFERENCE,");
		sql.append(" JSONOBJECT, EODDATE, THREADID, PROGRESS, STARTTIME,");
		sql.append(" ENDTIME, ERRORLOG, EODPROCESS)");
		sql.append(" SELECT FEEREFUNDHEADERID, FINREFERENCE, JSONOBJECT, EODDATE, THREADID");
		sql.append(", PROGRESS, STARTTIME, ENDTIME, ERRORLOG, EODPROCESS FROM FeeRefundProcessQueuing");

		logger.trace(Literal.SQL + sql.toString());
		this.jdbcTemplate.update(sql.toString(), new MapSqlParameterSource());

		StringBuilder truncateSql = new StringBuilder(" TRUNCATE table FeeRefundProcessQueuing");
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
				"SELECT count(DISTINCT FinReference) from FeeRefundProcessQueuing where Progress = :Progress and THREADID = :THREADID");
		logger.trace(Literal.SQL + sql.toString());

		long progressCount = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, Long.class);

		logger.debug(Literal.LEAVING);
		return progressCount;
	}

	@Override
	public long getCountByFinReference() {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource beanParameters = new MapSqlParameterSource();
		beanParameters.addValue("Progress", EodConstants.PROGRESS_WAIT);
		beanParameters.addValue("THREADID", 0);

		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT count(DISTINCT FinReference) from FeeRefundProcessQueuing where Progress = :Progress and THREADID = :THREADID");
		logger.trace(Literal.SQL + sql.toString());

		long progressCount = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, Long.class);

		logger.debug(Literal.LEAVING);
		return progressCount;
	}

	@Override
	public void updateStatusQueue(long feeRefundId, int progress) {
		// logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FeeRefundHeaderId", feeRefundId);
		source.addValue("Progress", progress);
		source.addValue("EndTime", DateUtil.getSysDate());

		StringBuilder sql = new StringBuilder("Update FeeRefundProcessQueuing set");
		sql.append(" EndTime = :EndTime, Progress = :Progress");
		sql.append(" Where FeeRefundHeaderId = :FeeRefundHeaderId ");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcTemplate.update(sql.toString(), source);
		// logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateFailedQueue(FeeRefundProcessQueuing frdQueuing) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update FeeRefundProcessQueuing set");
		sql.append(" EndTime = :EndTime, ErrorLog = :ErrorLog ");
		sql.append(" Where FeeRefundHeaderId = :FeeRefundHeaderId");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(frdQueuing);
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

				logger.debug("selectSql: " + UPDATE_SQL);
				return this.jdbcTemplate.update(UPDATE_SQL, source);

			} else {
				StringBuilder sql = new StringBuilder("UPDATE FeeRefundProcessQueuing set ThreadId = :ThreadId ");
				sql.append(" where finreference in (Select finreference from ( ");
				sql.append(" Select finreference,row_number() over(order by finreference) row_numb from ");
				sql.append(" (Select distinct finreference from FeeRefundProcessQueuing)T)T1 ");
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
