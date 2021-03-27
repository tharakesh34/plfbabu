package com.pennant.backend.dao.cashmanagement.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.cashmanagement.BranchCashReplenishmentHeaderDAO;
import com.pennant.backend.model.cashmanagement.BranchCashReplenishmentHeader;
import com.pennant.backend.util.CashManagementConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class BranchCashReplenishmentHeaderDAOImpl extends SequenceDao<BranchCashReplenishmentHeader>
		implements BranchCashReplenishmentHeaderDAO {
	private static Logger logger = LogManager.getLogger(BranchCashReplenishmentHeaderDAOImpl.class);

	@Override
	public long getNewRequestId() {
		return getNextValue("SeqBranchCashRepHeader");
	}

	@Override
	public long addReplenishmentHeader(BranchCashReplenishmentHeader branchCashReplenishmentHeader) {
		logger.debug(Literal.ENTERING);
		if (branchCashReplenishmentHeader.getProcessId() == 0
				|| branchCashReplenishmentHeader.getProcessId() == Long.MIN_VALUE) {
			branchCashReplenishmentHeader.setProcessId(getNewRequestId());
		}

		StringBuilder insertSql = new StringBuilder(" INSERT INTO BranchCashRepHeader ");
		insertSql.append(" (ProcessId,TransactionDate, RequestType, BranchCode, PartnerBankId, RecordCount,");
		insertSql.append(" DownLoadStatus, DownloadBatchId, DownloadFile, DownLoadedBy, DownloadedDate,");
		insertSql.append(" UploadStatus, UploadBatchId, UploadFile, UploadedBy, UploadedDate)");
		insertSql.append(" Values ");
		insertSql.append(" (:ProcessId,:TransactionDate, :RequestType, :BranchCode, :PartnerBankId, :RecordCount,");
		insertSql.append(" :DownLoadStatus, :DownloadBatchId, :DownloadFile, :DownLoadedBy, :DownloadedDate,");
		insertSql.append(" :UploadStatus, :UploadBatchId, :UploadFile, :UploadedBy, :UploadedDate)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + insertSql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(branchCashReplenishmentHeader);
		try {
			jdbcTemplate.update(insertSql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);

		return branchCashReplenishmentHeader.getProcessId();

	}

	@Override
	public boolean updateReplenishmentHeader(BranchCashReplenishmentHeader branchCashReplenishmentHeader) {
		logger.debug(Literal.ENTERING);

		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder("Update BranchCashRepHeader Set");
		updateSql.append(
				" UploadStatus = :UploadStatus, UploadBatchId = :UploadBatchId, UploadFile = :UploadFile, UploadedBy = :UploadedBy, UploadedDate = :UploadedDate");
		updateSql.append(" Where ProcessId = :ProcessId ");
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + updateSql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(branchCashReplenishmentHeader);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), paramSource);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.ENTERING);
		return true;
	}

	@Override
	public BranchCashReplenishmentHeader getReplenishmentHeaderByProcessId(long processId) {
		if (processId != 0) {
			return getReplenishmentHeader(processId, null);
		}
		return null;
	}

	@Override
	public BranchCashReplenishmentHeader getReplenishmentHeaderByDownLoadFile(String downloadFileName) {

		if (StringUtils.isNotBlank(downloadFileName)) {
			return getReplenishmentHeader(0, downloadFileName);
		}

		return null;
	}

	private BranchCashReplenishmentHeader getReplenishmentHeader(long processId, String downloadFileName) {
		logger.debug(Literal.ENTERING);

		StringBuilder selectSql = new StringBuilder(" Select ");
		selectSql.append(" ProcessId,TransactionDate, RequestType, BranchCode, PartnerBankId, RecordCount, ");
		selectSql.append(" DownLoadStatus, DownloadBatchId, DownloadFile, DownLoadedBy, DownloadedDate, ");
		selectSql.append(" UploadStatus, UploadBatchId, UploadFile, UploadedBy, UploadedDate ");
		selectSql.append(" From BranchCashRepHeader ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + selectSql.toString());

		BranchCashReplenishmentHeader header = new BranchCashReplenishmentHeader();

		if (processId != 0) {
			header.setProcessId(processId);
			selectSql.append(" WHERE ProcessId = :ProcessId ");
		} else if (StringUtils.isNotBlank(downloadFileName)) {
			header.setDownloadFile(downloadFileName);
			selectSql.append(" WHERE DownloadFile = :DownloadFile ");
		}

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(header);
		RowMapper<BranchCashReplenishmentHeader> rowMapper = BeanPropertyRowMapper
				.newInstance(BranchCashReplenishmentHeader.class);

		try {
			header = jdbcTemplate.queryForObject(selectSql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			header = null;
		}

		logger.debug(Literal.LEAVING);
		return header;
	}

	@Override
	public List<BranchCashReplenishmentHeader> getUnprocessedDownLoads() {
		logger.debug(Literal.ENTERING);

		List<BranchCashReplenishmentHeader> list;

		StringBuilder selectSql = new StringBuilder(" Select ");
		selectSql.append(" ProcessId,TransactionDate, RequestType, BranchCode, PartnerBankId, RecordCount,");
		selectSql.append(" DownLoadStatus, DownloadBatchId, DownloadFile, DownLoadedBy, DownloadedDate, ");
		selectSql.append(" UploadStatus, UploadBatchId, UploadFile, UploadedBy, UploadedDate ");
		selectSql.append(" From BranchCashRepHeader ");
		selectSql.append(" WHERE  UploadStatus IS NULL OR UploadStatus = :UploadStatus");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + selectSql.toString());

		BranchCashReplenishmentHeader header = new BranchCashReplenishmentHeader();
		header.setUploadStatus(CashManagementConstants.FILE_STATUS_ERROR); //Status is Error

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(header);
		RowMapper<BranchCashReplenishmentHeader> rowMapper = BeanPropertyRowMapper
				.newInstance(BranchCashReplenishmentHeader.class);
		list = jdbcTemplate.query(selectSql.toString(), paramSource, rowMapper);

		logger.debug(Literal.LEAVING);

		return list;
	}
}
