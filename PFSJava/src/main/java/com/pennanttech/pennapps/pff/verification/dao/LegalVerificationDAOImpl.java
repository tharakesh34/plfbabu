package com.pennanttech.pennapps.pff.verification.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.verification.model.LVDocument;
import com.pennanttech.pennapps.pff.verification.model.LegalVerification;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class LegalVerificationDAOImpl extends SequenceDao<LegalVerification> implements LegalVerificationDAO {
	private static Logger logger = LogManager.getLogger(LegalVerificationDAOImpl.class);

	public LegalVerificationDAOImpl() {
		super();
	}

	@Override
	public String save(LegalVerification legalVerification, TableType tableType) {

		StringBuilder sql = new StringBuilder(" insert into verification_lv");
		sql.append(tableType.getSuffix());

		if (tableType == TableType.MAIN_TAB) {
			sql.append("_stage");
			sql.append(" (VerificationId, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
			sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
			sql.append(" values (:VerificationId, :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
			sql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		} else {
			sql.append(" (VerificationId, Id, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
			sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
			sql.append(" values (:VerificationId, :Id, :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
			sql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		}

		logger.trace(Literal.SQL + sql.toString());

		KeyHolder keyHolder = new GeneratedKeyHolder();
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalVerification);

		try {
			if (tableType == TableType.MAIN_TAB) {
				jdbcTemplate.update(sql.toString(), paramSource, keyHolder, new String[] { "id" });
			} else {
				jdbcTemplate.update(sql.toString(), paramSource);
			}
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);

		if (tableType == TableType.MAIN_TAB) {
			legalVerification.setId(keyHolder.getKey().longValue());
		}

		return String.valueOf(legalVerification.getId());

	}

	public String saveLV(LegalVerification legalVerification, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into verification_lv");
		sql.append(tableType.getSuffix());
		sql.append(" (verificationId, id, agentcode, agentname, status, reason, remarks, date,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append("values (:verificationId, :id,:agentCode, :agentName, :status, :reason, :remarks, :date,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		sql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalVerification);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(legalVerification.getId());
	}

	@Override
	public void update(LegalVerification legalVerification, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update verification_lv");
		sql.append(tableType.getSuffix());
		sql.append(" set verificationId = :verificationId, date = :date, agentCode = :agentCode, agentName = :agentName, status = :status, ");
		sql.append(" reason = :reason, remarks = :remarks, Version = :Version, LastMntBy = :LastMntBy,");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalVerification);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(LegalVerification legalVerification, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from verification_lv");
		sql.append(tableType.getSuffix());
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalVerification);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public LegalVerification getLegalVerification(long id, long documetId, String documentSubId, String type) {

		StringBuilder sql = null;
		MapSqlParameterSource source = null;
		sql = new StringBuilder();

		sql.append(" Select id,verificationid, agentCode, agentName,  date, status, reason, documentId,");
		sql.append(" remarks, verificationFormName,");
		if (type.contains("View")) {
			sql.append(" cif, custid, custName, keyReference, collateralType, referencefor, createdon, ");
			sql.append(" reasonCode, reasonDesc,");
		}
		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" FROM  Verification_lv");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where id = :id and documentId = :documentId");
		if (documentSubId != null) {
			sql.append(" and documentSubId = :documentSubId");
		}
		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("id", id);
		source.addValue("documentId", documetId);
		source.addValue("documentSubId", documentSubId);

		RowMapper<LegalVerification> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(LegalVerification.class);
		try {
			return jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public boolean isLVExists(long id) {

		StringBuilder sql = null;
		MapSqlParameterSource source = null;
		sql = new StringBuilder("Select count(*) FROM  Verification_lv_view  Where verificationId = :id");

		logger.trace(Literal.SQL + sql.toString());
		source = new MapSqlParameterSource();
		source.addValue("id", id);

		try {
			int recordCount = jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
			if (recordCount > 0) {
				return true;
			}
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		}

		logger.debug(Literal.LEAVING);
		return false;
	}

	@Override
	public void saveDocuments(List<LVDocument> lvDocuments, TableType tableType) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into verification_lv_details");
		sql.append(tableType.getSuffix());

		if (tableType == TableType.MAIN_TAB) {
			sql.append("_stage");
		}

		sql.append(" (lvId, seqNo, verificationId, documentId,documentSubId,");
		sql.append(
				" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?,?");
		sql.append(" ,?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug("insertSql: " + sql.toString());

		jdbcTemplate.getJdbcOperations().batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				LVDocument document = lvDocuments.get(i);
				ps.setLong(1, document.getLvId());
				if (tableType == TableType.MAIN_TAB) {
					ps.setInt(2, i + 1);
				} else {
					ps.setInt(2, document.getSeqNo());
				}
				ps.setLong(3, document.getVerificationId());
				ps.setLong(4, document.getDocumentId());
				ps.setString(5, document.getDocumentSubId());
				ps.setInt(6, document.getVersion());
				ps.setTimestamp(7, document.getLastMntOn());
				ps.setLong(8, document.getLastMntBy());
				ps.setString(9, document.getRecordStatus());
				ps.setString(10, document.getRoleCode());
				ps.setString(11, document.getNextRoleCode());
				ps.setString(12, document.getTaskId());
				ps.setString(13, document.getNextTaskId());
				ps.setString(14, document.getRecordType());
				ps.setLong(15, document.getWorkflowId());
			}

			@Override
			public int getBatchSize() {
				return lvDocuments.size();
			}

		});

		logger.debug("Leaving");

	}

	@Override
	public void deleteDocuments(String reference, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from verification_lv_details_stage");
		sql.append(tableType.getSuffix());
		sql.append(" where verificationId in(select id from verifications where referenceFor=:referenceFor) ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("referenceFor", reference);

		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);

			if (recordCount > 0) {
				sql = new StringBuilder();
				sql.append("delete from verification_lv_stage");
				sql.append(" where id not in (select lvid from verification_lv_details_stage)");
				jdbcTemplate.update(sql.toString(), paramSource);
			}

		} catch (DataAccessException e) {

		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public LegalVerification getLVFromStage(long verificationId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select * from verification_lv_stage where id in (");
		sql.append("select lvid from verification_lv_details_stage where verificationId=:verificationId)");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("verificationId", verificationId);

		RowMapper<LegalVerification> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(LegalVerification.class);
		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public List<LVDocument> getLVDocumentsFromStage(long verificationId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select lvid, seqno, verificationId, documentId,documentsubId from verification_lv_details_stage");
		sql.append(" where verificationId=:verificationId");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("verificationId", verificationId);

		RowMapper<LVDocument> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LVDocument.class);
		try {
			return jdbcTemplate.query(sql.toString(), paramSource, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public List<String> getLVDocumentsIds(String keyReference) {
		StringBuilder sql = new StringBuilder("select");
		sql.append(" documentId ");
		sql.append(QueryUtil.getQueryConcat());
		sql.append(" COALESCE(documentSubId,'') documentId");
		sql.append(" from  verification_lv_details_stage where verificationid in(select verificationid");
		sql.append(" from verifications where keyReference=:keyReference)");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("keyReference", keyReference);

		try {
			return jdbcTemplate.queryForList(sql.toString(), paramSource, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public List<LegalVerification> getList(String keyReference) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("select * From verification_lv_view");
		sql.append(" Where keyreference = :keyreference");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("keyreference", keyReference);

		RowMapper<LegalVerification> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(LegalVerification.class);

		try {
			return jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	public List<LVDocument> getLVDocuments(long id, String type) {

		StringBuilder sql = null;
		MapSqlParameterSource source = null;
		sql = new StringBuilder();

		sql.append(" Select lvid, documentid, documentsubid,");
		if (type.contains("View")) {
			sql.append(
					" verificationid, code, description, docmodule, docrefid, seqno, docname, doctype, remarks1, remarks2, remarks3, ");
		}
		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" FROM  verification_lv_details");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where lvid = :lvId ");
		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("lvId", id);

		RowMapper<LVDocument> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LVDocument.class);
		try {
			return jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		} finally {
			source = null;
			sql = null;
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	public void saveDocuments(LVDocument lvDocument, String tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into verification_lv_details");
		sql.append(tableType);
		sql.append("(lvid, seqno, documentid, documentsubid, remarks1,");
		sql.append(" remarks2, remarks3,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");

		sql.append(
				"values (:lvId, :seqNo, :documentId, :documentSubId,:remarks1, :remarks2, :remarks3,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		sql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		// sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(lvDocument);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	public void updateDocuments(LVDocument lvDocument, String tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update verification_lv_details");
		sql.append(tableType);
		sql.append(" set remarks1 = :remarks1, remarks2 = :remarks2, remarks3 = :remarks3, ");
		sql.append(" Version = :Version, LastMntBy = :LastMntBy,");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where lvid = :lvId AND seqno = :seqNo");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(lvDocument);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteLVDocuments(LVDocument lvDocument, String tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from verification_lv_details");
		sql.append(tableType);
		sql.append(" where lvid = :lvid and seqno = :seqno");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(lvDocument);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteLVDocumentsList(List<LVDocument> documents, String tableType) {
		logger.debug("Entering");

		StringBuilder deleteSql = new StringBuilder("Delete From verification_lv_details");
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" Where lvid = :lvId ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(documents.toArray());
		this.jdbcTemplate.batchUpdate(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

}
