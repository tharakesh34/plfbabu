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

import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.verification.DocumentType;
import com.pennanttech.pennapps.pff.verification.model.RCUDocument;
import com.pennanttech.pennapps.pff.verification.model.RiskContainmentUnit;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class RiskContainmentUnitDAOImpl extends SequenceDao<RiskContainmentUnit> implements RiskContainmentUnitDAO {
	private static Logger logger = LogManager.getLogger(RiskContainmentUnitDAOImpl.class);

	public RiskContainmentUnitDAOImpl() {
		super();
	}

	// Save RiskContainmentUnit in verification_rcu Based on tableType.
	@Override
	public String save(RiskContainmentUnit rcu, TableType tableType) {// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into verification_rcu");
		sql.append(tableType.getSuffix());
		sql.append(" (verificationId, verificationDate, agentCode, agentName, status, reason,");
		sql.append(" remarks,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append("values (:verificationId, :verificationDate,  :agentCode, :agentName, :status, :reason,");
		sql.append(" :remarks,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		sql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(rcu);
		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
		return String.valueOf(rcu.getId());
	}

	@Override
	public void update(RiskContainmentUnit rcu, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update verification_rcu");
		sql.append(tableType.getSuffix());
		sql.append(
				" set verificationDate = :verificationDate, agentCode = :agentCode, agentName = :agentName, status = :status, ");
		sql.append(" reason = :reason, remarks = :remarks, Version = :Version, LastMntBy = :LastMntBy,");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where verificationId = :verificationId ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(rcu);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(RiskContainmentUnit rcu, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from verification_rcu");
		sql.append(tableType.getSuffix());
		sql.append(" where verificationId = :verificationId ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(rcu);
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
	public RiskContainmentUnit getRiskContainmentUnit(long verificationId, String type) {

		StringBuilder sql = null;
		MapSqlParameterSource source = null;
		sql = new StringBuilder();

		sql.append(" Select verificationid, agentCode, agentName,  verificationdate, status, reason,");
		sql.append(" remarks,");
		if (type.contains("View")) {
			sql.append(" cif, custId, custName, keyReference, createdon, ");
			sql.append(" reasonCode, reasonDesc,");
		}
		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" FROM  Verification_rcu");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where verificationId = :verificationId");
		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("verificationId", verificationId);

		RowMapper<RiskContainmentUnit> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(RiskContainmentUnit.class);
		try {
			return jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		} finally {
			source = null;
			sql = null;
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public void saveDocuments(RCUDocument rcuDocument, String tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into verification_rcu_details");
		sql.append(tableType);
		sql.append("(verificationId, seqno, documentid, documenttype, documentsubid, documentrefid, documenturi,");
		sql.append(" verificationtype, status, pageseyeballed, pagessampled, agentremarks, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		
		sql.append("values (:verificationId, :seqNo, :documentId, :documentType, :documentSubId, :documentRefId, :documentUri, ");
		sql.append(" :verificationType , :status, :pagesEyeballed, :pagesSampled, :agentRemarks,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		sql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(rcuDocument);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);

	}

	@Override
	public void updateDocuments(RCUDocument rcuDocument, String tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update verification_rcu_details");
		sql.append(tableType);
		sql.append(" set verificationtype = :verificationType, status = :status, pageseyeballed = :pagesEyeballed, ");
		sql.append(" pagessampled = :pagesSampled, agentremarks = :agentRemarks,");
		sql.append(" Version = :Version, LastMntBy = :LastMntBy,");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where verificationId = :verificationId and seqno = :seqNo");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(rcuDocument);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteRCUDocuments(RCUDocument rcuDocument, String tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from verification_rcu_details");
		sql.append(tableType);
		sql.append(" where verificationId = :verificationId and seqno = :seqno");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(rcuDocument);
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
	public void deleteRCUDocumentsList(List<RCUDocument> documents, String tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder deleteSql = new StringBuilder("Delete From verification_rcu_details");
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" Where verificationId = :verificationId ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(documents.toArray());
		this.jdbcTemplate.batchUpdate(deleteSql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);

	}

	@Override
	public List<RiskContainmentUnit> getList(String keyReference) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("select * From verification_rcu_view");
		sql.append(" Where keyreference = :keyreference");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("keyreference", keyReference);

		RowMapper<RiskContainmentUnit> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(RiskContainmentUnit.class);

		try {
			return jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public void saveDocuments(List<RCUDocument> rcuDocuments, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into verification_rcu_details");
		sql.append(tableType.getSuffix());

		sql.append(" (verificationId, seqNo, documentId, documenttype, documentSubId, documentrefid, documenturi, initremarks");
		sql.append(
				" ,Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(" ,?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug("insertSql: " + sql.toString());

		jdbcTemplate.getJdbcOperations().batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				RCUDocument document = rcuDocuments.get(i);
				ps.setLong(1, document.getVerificationId());
				ps.setInt(2, i + 1);
				ps.setLong(3, document.getDocumentId());
				ps.setInt(4, document.getDocumentType());
				ps.setString(5, document.getDocumentSubId());
				ps.setLong(6, document.getDocumentRefId());
				ps.setString(7, document.getDocumentUri());
				ps.setString(8, document.getInitRemarks());
				ps.setInt(9, document.getVersion());
				ps.setTimestamp(10, document.getLastMntOn());
				ps.setLong(11, document.getLastMntBy());
				ps.setString(12, document.getRecordStatus());
				ps.setString(13, document.getRoleCode());
				ps.setString(14, document.getNextRoleCode());
				ps.setString(15, document.getTaskId());
				ps.setString(16, document.getNextTaskId());
				ps.setString(17, document.getRecordType());
				ps.setLong(18, document.getWorkflowId());
			}

			@Override
			public int getBatchSize() {
				return rcuDocuments.size();
			}

		});

		logger.debug(Literal.LEAVING);

	}

	@Override
	public List<RCUDocument> getRCUDocuments(long verificationId, String type) {
		StringBuilder sql = null;
		MapSqlParameterSource source = null;
		sql = new StringBuilder();

		sql.append(" Select verificationId, documentid, documentsubid,");
		if (type.contains("View")) {
			sql.append(
					" code, description, docmodule, documentrefid, seqno, docname, doctype,  ");
			sql.append(
					" verificationtype, status, pageseyeballed, pagessampled, agentremarks, ");
		}
		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" FROM  verification_rcu_details");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where verificationId = :verificationId");
		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("verificationId", verificationId);

		RowMapper<RCUDocument> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(RCUDocument.class);
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


	@Override
	public void deleteDocuments(long verificationId, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from verification_rcu_details_stage");
		sql.append(tableType.getSuffix());
		sql.append(" where verificationId=:verificationId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("verificationId", verificationId);

		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);

			if (recordCount > 0) {
				sql = new StringBuilder();
				sql.append("delete from verification_rcu_stage");
				sql.append(" where verificationId =:verificationId");
				jdbcTemplate.update(sql.toString(), paramSource);
			}

		} catch (DataAccessException e) {

		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<RCUDocument> getDocuments(String keyReference, TableType tableType, DocumentType documentType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder();
		sql.append(" select verificationid , seqno, documentid, documentsubid, documentrefid, documenturi,");
		if (documentType == DocumentType.CUSTOMER) {
			sql.append(" custdoccategory as docCategory");
		} else {
			sql.append(" doccategory as docCategory");
		}
		sql.append(" From verification_rcu_details");

		if (tableType == TableType.BOTH_TAB) {
			sql.append("_view");
		} else {
			sql.append(tableType.getSuffix());
		}
		sql.append(" rcu ");

		if (documentType == DocumentType.CUSTOMER) {
			sql.append(" inner join customerdocuments_view doc");
			sql.append(" on doc.custid = rcu.documentId and rcu.documentsubid = doc.custdoccategory");
		} else {
			sql.append(" inner join documentdetails_view doc");
			sql.append(" on doc.docid = rcu.documentId");
		}

		sql.append(
				" Where verificationId in (select verificationId from verifications where keyReference =:keyReference)");
		
		sql.append(" and documentType = :documentType");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("keyReference", keyReference);
		paramSource.addValue("documentType", documentType.getKey());

		RowMapper<RCUDocument> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(RCUDocument.class);

		try {
			return jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}
}
