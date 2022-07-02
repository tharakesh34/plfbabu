package com.pennant.backend.dao.finance.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.finance.MiscPostingUploadDAO;
import com.pennant.backend.model.miscPostingUpload.MiscPostingUpload;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class MiscPostingUploadDAOImpl extends SequenceDao<MiscPostingUpload> implements MiscPostingUploadDAO {
	private static Logger logger = LogManager.getLogger(MiscPostingUploadDAOImpl.class);

	public MiscPostingUploadDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Finance Types details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return FinanceType
	 */
	@Override
	public List<MiscPostingUpload> getMiscPostingUploadsByUploadId(long uploadId, String type) {
		logger.debug(Literal.ENTERING);
		MiscPostingUpload miscPostingUpload = new MiscPostingUpload();
		miscPostingUpload.setUploadId(uploadId);

		StringBuilder sql = new StringBuilder();
		sql.append(
				" select MiscPostingId, UploadId, Batch, Branch, BatchPurpose, PostAgainst, Reference, PostingDivision, Account, TxnEntry,");
		sql.append(
				" ValueDate, TxnAmount, NarrLine1, NarrLine2, NarrLine3, NarrLine4, UploadStatus, Reason,TRANSACTIONID,");

		sql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		sql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		sql.append(" FROM MiscPostingUploads");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where UploadId = :UploadId");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(miscPostingUpload);
		RowMapper<MiscPostingUpload> typeRowMapper = BeanPropertyRowMapper.newInstance(MiscPostingUpload.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * This method insert new Records into MiscPostingUploads or MiscPostingUploads_Temp.
	 * 
	 * save Finance Types
	 * 
	 * @param Finance Types (financeType)
	 * @param type    (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(MiscPostingUpload miscPostingUpload) {
		logger.debug(Literal.ENTERING);

		if (miscPostingUpload.getMiscPostingId() < 0) {
			miscPostingUpload.setMiscPostingId(getNextValue("SeqJVpostings"));
			logger.debug("get NextValue:" + miscPostingUpload.getMiscPostingId());
		}

		StringBuilder sql = new StringBuilder("Insert Into MiscPostingUploads");
		sql.append(
				" (MiscPostingId, UploadId, Batch, Branch, BatchPurpose, PostAgainst, Reference, PostingDivision, Account, TxnEntry,");
		sql.append(
				" ValueDate, TxnAmount, NarrLine1, NarrLine2, NarrLine3, NarrLine4, UploadStatus, Reason,TransactionId,");
		sql.append(
				" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(
				" Values(:MiscPostingId, :UploadId, :Batch, :Branch, :BatchPurpose, :PostAgainst, :Reference, :PostingDivision, :Account, :TxnEntry,");
		sql.append(
				" :ValueDate, :TxnAmount, :NarrLine1, :NarrLine2, :NarrLine3, :NarrLine4, :UploadStatus, :Reason,:TransactionId,");
		sql.append(
				" :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(miscPostingUpload);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);

		return miscPostingUpload.getReference();
	}

	/**
	 * This method updates the Record MiscPostingUploads or MiscPostingUploads_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Finance Types by key FinType and Version
	 * 
	 * @param Finance Types (financeType)
	 * @param type    (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(MiscPostingUpload miscPostingUpload) {
		int recordCount = 0;
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update MiscPostingUploads");
		sql.append(" Set Branch =:Branch, BatchPurpose =:BatchPurpose,");
		sql.append(
				" PostAgainst =:PostAgainst, Reference =:Reference, PostingDivision =:PostingDivision, Account =:Account, TxnEntry =:TxnEntry,");
		sql.append(
				" ValueDate =:ValueDate, TxnAmount =:TxnAmount, NarrLine1 =:NarrLine1, NarrLine2 =:NarrLine2, NarrLine3 =:NarrLine3, NarrLine4 =:NarrLine4,");
		sql.append(" UploadStatus = :UploadStatus, Reason = :Reason,TransactionId = :TransactionId, ");
		sql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		sql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		sql.append(" TaskId = :TaskId,  NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" Where MiscPostingId = :MiscPostingId");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(miscPostingUpload);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param FinanceType (financeType)
	 * @return FinanceType
	 */
	@Override
	public void deleteByUploadId(long uploadId) {
		logger.debug(Literal.ENTERING);

		MiscPostingUpload miscPostingUpload = new MiscPostingUpload();
		miscPostingUpload.setUploadId(uploadId);
		StringBuilder deleteSql = new StringBuilder("Delete From MiscPostingUploads");
		deleteSql.append(" Where UploadId = :UploadId");
		logger.trace(Literal.SQL + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(miscPostingUpload);

		try {
			this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean getMiscPostingUploadsByReference(String reference, long uploadId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		MapSqlParameterSource source = null;

		sql = new StringBuilder();
		sql.append(" Select Count(FinReference) from MiscPostingUploads");
		sql.append(type);
		sql.append(" Where Reference = :Reference And UploadStatus = :UploadStatus");
		if (uploadId > 0) {
			sql.append(" And UploadId != :UploadId");
		}
		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("Reference", reference);
		source.addValue("Status", "SUCCESS");

		return this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class) > 0;
	}

	@Override
	public long getMiscPostingBranchSeq() {
		logger.debug(Literal.ENTERING);
		long seq = getNextValue("SeqJVpostings");
		logger.trace("get NextID:" + seq);
		return seq;
	}

	@Override
	public MiscPostingUpload getMiscPostingUploadsByMiscId(long miscPostingId, String type) {
		logger.debug(Literal.ENTERING);
		MiscPostingUpload miscPostingUpload = new MiscPostingUpload();
		miscPostingUpload.setMiscPostingId(miscPostingId);

		StringBuilder sql = new StringBuilder(
				"Select MiscPostingId, UploadId, Batch, Branch, BatchPurpose, PostAgainst, Reference, PostingDivision, Account, TxnEntry,");
		sql.append(
				" ValueDate, TxnAmount, NarrLine1, NarrLine2, NarrLine3, NarrLine4, UploadStatus, Reason,TransactionId,");
		if (type.contains("View")) {
			sql.append("");
		}
		sql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		sql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		sql.append(" FROM MiscPostingUploads");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where miscPostingId = :miscPostingId");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(miscPostingUpload);
		RowMapper<MiscPostingUpload> typeRowMapper = BeanPropertyRowMapper.newInstance(MiscPostingUpload.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public void saveList(List<MiscPostingUpload> miscPostingUpload) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Insert Into MiscPostingUploads");
		sql.append(
				" (MiscPostingId, UploadId, Batch, Branch, BatchPurpose, PostAgainst, Reference, PostingDivision, Account, TxnEntry,");
		sql.append(
				" ValueDate, TxnAmount, NarrLine1, NarrLine2, NarrLine3, NarrLine4, UploadStatus, Reason,TransactionId,");
		sql.append(
				" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(
				" Values(:MiscPostingId, :UploadId, :Batch, :Branch, :BatchPurpose, :PostAgainst, :Reference, :PostingDivision, :Account, :TxnEntry,");
		sql.append(
				" :ValueDate, :TxnAmount, :NarrLine1, :NarrLine2, :NarrLine3, :NarrLine4, :UploadStatus, :Reason,:TransactionId,");
		sql.append(
				" :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(miscPostingUpload.toArray());
		this.jdbcTemplate.batchUpdate(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateList(List<MiscPostingUpload> miscPostingUpload) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update MiscPostingUploads");
		sql.append(" Set Branch =:Branch, BatchPurpose =:BatchPurpose,");
		sql.append(
				" PostAgainst =:PostAgainst, Reference =:Reference, PostingDivision =:PostingDivision, Account =:Account, TxnEntry =:TxnEntry,");
		sql.append(
				" ValueDate =:ValueDate, TxnAmount =:TxnAmount, NarrLine1 =:NarrLine1, NarrLine2 =:NarrLine2, NarrLine3 =:NarrLine3, NarrLine4 =:NarrLine4,");
		sql.append(" UploadStatus = :UploadStatus, Reason = :Reason,TransactionId = :TransactionId, ");
		sql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		sql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		sql.append(" TaskId = :TaskId,  NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" Where MiscPostingId = :MiscPostingId");

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(miscPostingUpload.toArray());
		int[] count = this.jdbcTemplate.batchUpdate(sql.toString(), beanParameters);

		for (int i : count) {
			if (i <= 0) {
				throw new ConcurrencyException();
			}
		}

		logger.debug(Literal.LEAVING);
	}

}
