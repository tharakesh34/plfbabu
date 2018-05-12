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
import com.pennanttech.pennapps.pff.verification.model.LVDocument;
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
				" set verificationId = :verificationId, verificationDate = :verificationDate, agentCode = :agentCode, agentName = :agentName, status = :status, ");
		sql.append(" reason = :reason, remarks = :remarks, Version = :Version, LastMntBy = :LastMntBy,");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where id = :id ");
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
		sql.append(" where id = :id ");
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
	public RiskContainmentUnit getRiskContainmentUnit(long id, long documetId, String documentSubId, String type) {

		StringBuilder sql = null;
		MapSqlParameterSource source = null;
		sql = new StringBuilder();

		sql.append(" Select id, verificationid, agentCode, agentName,  verificationDate, status, reason, documentId,");
		sql.append(" remarks,");
		if (type.contains("View")) {
			sql.append(" cif, custId, custName, keyReference, rcureference, createdon, ");
			sql.append(" reasonCode, reasonDesc,");
		}
		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" FROM  Verification_lv");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where id = :id and documentId = :documentId ");
		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("id", id);
		source.addValue("documentId", documetId);

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
		sql.append("(lvid, seqno, documentid, documentsubid, remarks1,");
		sql.append(" remarks2, remarks3,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");

		sql.append("values (:lvId, :seqNo, :documentId, :documentSubId,:remarks1, :remarks2, :remarks3,");
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
		sql.append(" set remarks1 = :remarks1, remarks2 = :remarks2, remarks3 = :remarks3, ");
		sql.append(" Version = :Version, LastMntBy = :LastMntBy,");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where lvid = :lvId AND seqno = :seqNo");

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
		sql.append(" where lvid = :lvid and seqno = :seqno");

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
	public void deleteRCUDocumentsList(List<LVDocument> documents, String tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder deleteSql = new StringBuilder("Delete From verification_rcu_details");
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" Where lvid = :lvId ");
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

		sql.append(" (verificationId, seqNo, documentId, documentSubId, documentrefid, documenturi, initremarks");
		sql.append(
				" ,Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?");
		sql.append(" ,?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug("insertSql: " + sql.toString());

		jdbcTemplate.getJdbcOperations().batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				RCUDocument document = rcuDocuments.get(i);
				ps.setLong(1, document.getVerificationId());
				ps.setInt(2, i + 1);
				ps.setLong(3, document.getDocumentId());
				ps.setString(4, document.getDocumentSubId());
				ps.setLong(5, document.getDocumentRefId());
				ps.setString(6, document.getDocumentUri());
				ps.setString(7, document.getInitRemarks());
				ps.setInt(8, document.getVersion());
				ps.setTimestamp(9, document.getLastMntOn());
				ps.setLong(10, document.getLastMntBy());
				ps.setString(11, document.getRecordStatus());
				ps.setString(12, document.getRoleCode());
				ps.setString(13, document.getNextRoleCode());
				ps.setString(14, document.getTaskId());
				ps.setString(15, document.getNextTaskId());
				ps.setString(16, document.getRecordType());
				ps.setLong(17, document.getWorkflowId());
			}

			@Override
			public int getBatchSize() {
				return rcuDocuments.size();
			}

		});

		logger.debug(Literal.LEAVING);

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
		if(documentType == DocumentType.CUSTOMER) {
			sql.append(" custdoccategory as docCategory");
		} else {
			sql.append(" doccategory as docCategory");
		}
		sql.append(" From verification_rcu_details");
		sql.append(tableType.getSuffix());
		sql.append(" rcu ");
		
		if(documentType == DocumentType.CUSTOMER) {
			sql.append(" inner join customerdocuments_view doc");
			sql.append(" on doc.custid = rcu.documentId and rcu.documentsubid = doc.custdoccategory");
		} else {
			sql.append(" inner join documentdetails_view doc");
			sql.append(" on doc.docid = rcu.documentId");
		}
			
		sql.append(" Where verificationId in (select verificationId from verifications where keyReference =:keyReference)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("keyReference", keyReference);

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
