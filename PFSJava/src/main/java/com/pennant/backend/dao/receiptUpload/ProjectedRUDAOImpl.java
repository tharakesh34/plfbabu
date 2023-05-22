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

package com.pennant.backend.dao.receiptUpload;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.app.receiptuploadqueue.ReceiptUploadQueuing;
import com.pennant.app.util.SysParamUtil;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class ProjectedRUDAOImpl extends BasicDao<ReceiptUploadQueuing> implements ProjectedRUDAO {
	private static Logger logger = LogManager.getLogger(ProjectedRUDAOImpl.class);

	private static final String UPDATE_SQL = "UPDATE ReceiptUploadQueuing set ThreadId = :ThreadId Where ThreadId = :AcThreadId";

	public ProjectedRUDAOImpl() {
		super();
	}

	@Override
	public int prepareReceiptUploadQueue(long receiptUploadHeaderId) {
		logger.debug(Literal.ENTERING);

		ReceiptUploadQueuing ruQueuing = new ReceiptUploadQueuing();
		ruQueuing.setThreadId(0);
		ruQueuing.setEodDate(SysParamUtil.getAppDate());
		ruQueuing.setEodProcess(false);
		ruQueuing.setProgress(EodConstants.PROGRESS_WAIT);
		ruQueuing.setUploadHeaderId(receiptUploadHeaderId);
		ruQueuing.setStartTime(DateUtil.getSysDate());

		StringBuilder sql = new StringBuilder(
				"INSERT INTO ReceiptUploadQueuing (FINREFERENCE,UploadHeaderId, UPLOADDETAILID,JSONOBJECT, EODDATE, THREADID, PROGRESS, STARTTIME, EODPROCESS)");
		sql.append(
				" SELECT Reference,UploadHeaderId, UPLOADDETAILID,JSONOBJECT, :EodDate, :ThreadId, :Progress, :StartTime, :EodProcess From RECEIPTUPLOADDETAILS T WHERE T.UPLOADHEADERID = :UploadHeaderId");
		sql.append(" AND T.UPLOADSTATUS='SUCCESS'  order by T.Reference,T.UPLOADDETAILID ");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(ruQueuing);
		int ruRecords = this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
		return ruRecords;
	}

	@Override
	public int[] prepareReceiptUploadQueue(List<ReceiptUploadQueuing> uploadQueuings) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO ReceiptUploadQueuing (FINREFERENCE,  UploadHeaderId, UPLOADDETAILID, JSONOBJECT");
		sql.append(", EODDATE, THREADID, PROGRESS, STARTTIME, EODPROCESS)");
		sql.append(" SELECT Reference, UploadHeaderId, UPLOADDETAILID, JSONOBJECT");
		sql.append(", :EodDate, :ThreadId, :Progress, :StartTime, :EodProcess");
		sql.append(" From RECEIPTUPLOADDETAILS T WHERE T.UPLOADHEADERID = :UploadHeaderId");
		sql.append(" AND T.UPLOADSTATUS='SUCCESS'  order by T.Reference,T.UPLOADDETAILID ");

		logger.trace(Literal.SQL + sql.toString());

		logger.debug(Literal.LEAVING);
		return jdbcTemplate.batchUpdate(sql.toString(), SqlParameterSourceUtils.createBatch(uploadQueuings.toArray()));
	}

	@Override
	public List<Long> getThreads() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select distinct ThreadId FROM ReceiptUploadQueuing order by ThreadId");

		logger.trace(Literal.SQL + sql.toString());

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.queryForList(sql.toString(), new MapSqlParameterSource(), Long.class);
	}

	@Override
	public void insertLogTableAndTruncate() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder(" INSERT INTO ReceiptUploadQueuing_log");
		sql.append(" SELECT * FROM ReceiptUploadQueuing");

		logger.trace(Literal.SQL + sql.toString());
		this.jdbcTemplate.update(sql.toString(), new MapSqlParameterSource());

		StringBuilder truncateSql = new StringBuilder(" TRUNCATE table RECEIPTUPLOADQUEUING");
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
				"SELECT count(DISTINCT FinReference) from ReceiptUploadQueuing where Progress = :Progress and THREADID = :THREADID");
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
				"SELECT count(DISTINCT FinReference) from ReceiptUploadQueuing where Progress = :Progress and THREADID = :THREADID");
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
		source.addValue("EndTime", DateUtil.getSysDate());

		StringBuilder sql = new StringBuilder("Update ReceiptUploadQueuing set");
		sql.append(" EndTime = :EndTime, Progress = :Progress");
		sql.append(" Where UploadHeaderId = :UploadHeaderId and uploadDetailId = :uploadDetailId ");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcTemplate.update(sql.toString(), source);
	}

	@Override
	public void updateFailedQueue(ReceiptUploadQueuing receiptUploadQueuing) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update ReceiptUploadQueuing set");
		sql.append(" EndTime = :EndTime, ErrorLog = :ErrorLog ");
		sql.append(" Where UploadHeaderId = :UploadHeaderId and uploaddetailId =:uploadDetailId");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(receiptUploadQueuing);
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

		if (rowNum == 0) {

			logger.debug("selectSql: " + UPDATE_SQL);
			return this.jdbcTemplate.update(UPDATE_SQL, source);

		} else {
			StringBuilder sql = new StringBuilder("UPDATE ReceiptUploadQueuing set ThreadId = :ThreadId ");
			sql.append(" where finreference in (Select finreference from ( ");
			sql.append(" Select finreference,row_number() over(order by finreference) row_numb from ");
			sql.append(" (Select distinct finreference from ReceiptUploadQueuing)T)T1 ");
			sql.append(" where row_numb<=:RowCount) and ThreadId=:AcThreadId ");

			logger.trace(Literal.SQL + sql.toString());

			return this.jdbcTemplate.update(sql.toString(), source);
		}
	}
}
