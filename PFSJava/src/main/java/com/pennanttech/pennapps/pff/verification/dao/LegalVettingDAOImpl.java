package com.pennanttech.pennapps.pff.verification.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.pff.verification.Agencies;
import com.pennanttech.pennapps.pff.verification.DocumentType;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.model.LVDocument;
import com.pennanttech.pennapps.pff.verification.model.LegalVetting;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class LegalVettingDAOImpl extends SequenceDao<LegalVetting> implements LegalVettingDAO {
	private static final Logger logger = LogManager.getLogger(LegalVettingDAOImpl.class);

	public LegalVettingDAOImpl() {
		super();
	}

	@Override
	public String save(LegalVetting legalVetting, TableType tableType) {
		StringBuilder sql = new StringBuilder(" insert into verification_vt");
		sql.append(tableType.getSuffix());

		if (tableType == TableType.MAIN_TAB) {
			sql.append("_stage");
		}

		sql.append(" (VerificationId, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values (:VerificationId, :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		sql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalVetting);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);

		return String.valueOf(legalVetting.getVerificationId());

	}

	@Override
	public void update(LegalVetting legalVetting, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update verification_vt");
		sql.append(tableType.getSuffix());
		sql.append(" set verificationDate = :verificationDate,");
		sql.append(" agentCode = :agentCode, agentName = :agentName, status = :status, ");
		sql.append(" reason = :reason, remarks = :remarks, Version = :Version, LastMntBy = :LastMntBy,");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where verificationId = :verificationId ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalVetting);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(LegalVetting legalVetting, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from verification_vt");
		sql.append(tableType.getSuffix());
		sql.append(" where verificationId = :verificationId ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalVetting);
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
	public List<LVDocument> getLVDocuments(long id, String type) {
		StringBuilder sql = null;
		MapSqlParameterSource source = null;
		sql = new StringBuilder();

		sql.append(" Select verificationId, documentid,documentType, documentsubid,");
		if (type.contains("View")) {
			sql.append(
					" code, description, docmodule, docrefid, seqno, docname, doctype, remarks1, remarks2, remarks3, referenceid, ");
		}
		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" FROM  verification_vt_dtls");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where verificationId = :verificationId");
		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("verificationId", id);

		RowMapper<LVDocument> typeRowMapper = BeanPropertyRowMapper.newInstance(LVDocument.class);

		return jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	@Override
	public void saveDocuments(List<LVDocument> lvDocuments, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into verification_vt_dtls");
		sql.append(tableType.getSuffix());
		sql.append(" (verificationId, seqNo, documentId, documentType, documentsubId, documentrefid, documenturi, ");
		sql.append(
				"Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?,");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug("insertSql: " + sql.toString());

		jdbcTemplate.getJdbcOperations().batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				LVDocument document = lvDocuments.get(i);
				ps.setLong(1, document.getVerificationId());
				ps.setInt(2, i + 1);
				if (document.getDocumentId() == null) {
					ps.setLong(3, 0);
				} else {
					ps.setObject(3, document.getDocumentId());
				}

				ps.setInt(4, document.getDocumentType());
				ps.setString(5, document.getDocumentSubId());
				if (document.getDocumentRefId() == null) {
					ps.setLong(6, 0);
				} else {
					ps.setObject(6, document.getDocumentRefId());
				}
				ps.setString(7, document.getDocumentUri());
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
				return lvDocuments.size();
			}

		});

		logger.debug(Literal.LEAVING);

	}

	@Override
	public void deleteDocuments(long verificationId, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from verification_vt_dtls_stage");
		sql.append(tableType.getSuffix());
		sql.append(" where verificationId =:verificationId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("verificationId", verificationId);

		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);

			if (recordCount > 0) {
				sql = new StringBuilder();
				sql.append("delete from verification_vt_stage");
				sql.append(" where verificationId not in (select verificationId from verification_vt_dtls_stage)");
				jdbcTemplate.update(sql.toString(), paramSource);
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public LegalVetting getLVFromStage(long verificationId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select * from verification_vt_stage where verificationId=:verificationId");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("verificationId", verificationId);

		RowMapper<LegalVetting> typeRowMapper = BeanPropertyRowMapper.newInstance(LegalVetting.class);
		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<LVDocument> getLVDocumentsFromStage(long verificationId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select verificationId, seqno, documentId,documentRefId,documentType, documentsubId");
		sql.append(" from verification_vt_dtls_stage where verificationId=:verificationId");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("verificationId", verificationId);

		RowMapper<LVDocument> typeRowMapper = BeanPropertyRowMapper.newInstance(LVDocument.class);

		return jdbcTemplate.query(sql.toString(), paramSource, typeRowMapper);
	}

	@Override
	public List<LegalVetting> getList(String keyRef) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("select * From verification_vt_view");
		sql.append(" Where keyreference = :keyreference");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("keyreference", keyRef);

		RowMapper<LegalVetting> rowMapper = BeanPropertyRowMapper.newInstance(LegalVetting.class);

		return jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
	}

	@Override
	public void updateDocuments(LVDocument lvDocument, String tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update verification_vt_dtls");
		sql.append(tableType);
		sql.append(" set remarks1 = :remarks1, remarks2 = :remarks2, remarks3 = :remarks3, ");
		sql.append(" Version = :Version, LastMntBy = :LastMntBy,");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where verificationId = :verificationId and seqno = :seqNo");

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
		StringBuilder sql = new StringBuilder("delete from verification_vt_dtls");
		sql.append(tableType);
		sql.append(" where verificationId = :verificationId and seqno = :seqno");

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
		logger.debug(Literal.ENTERING);

		StringBuilder deleteSql = new StringBuilder("Delete From verification_vt_dtls");
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" Where verificationId = :verificationId ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(documents.toArray());
		this.jdbcTemplate.batchUpdate(deleteSql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void saveLV(LegalVetting legalVetting, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into verification_vt");
		sql.append(tableType.getSuffix());
		sql.append(" (verificationId, agentcode, agentname, status, reason, remarks, verificationdate,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append("values (:verificationId, :agentCode, :agentName, :status, :reason, :remarks, :verificationDate,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		sql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(legalVetting);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void saveDocuments(LVDocument lvDocument, String tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into verification_vt_dtls");
		sql.append(tableType);
		sql.append("(verificationId, seqno, documentid,documentType, documentsubid, remarks1,");
		sql.append(" remarks2, remarks3,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(
				"values (:verificationId, :seqNo, :documentId,:documentType, :documentSubId,:remarks1, :remarks2, :remarks3,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		sql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

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
	public List<LVDocument> getLVDocuments(String keyReference, int docTypeKey) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" vs.VerificationId, vs.DocumentId, vs.DocumentType, vs.DocumentSubId");
		sql.append(", vs.DocumentRefId, v.ReferenceFor CollateralRef");
		sql.append(" From verification_vt_dtls_stage vs");
		sql.append(" Left Join Verifications v on v.id = vs.verificationId");
		sql.append(" Where vs.VerificationId in (Select Id From Verifications");
		sql.append(" Where KeyReference = ?) and DocumentType = ?");

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, keyReference);
			ps.setInt(2, docTypeKey);
		}, (rs, i) -> {
			LVDocument doc = new LVDocument();
			doc.setVerificationId(rs.getLong("VerificationId"));
			doc.setDocumentId(JdbcUtil.getLong(rs.getObject("DocumentId")));
			doc.setDocumentType(rs.getInt("DocumentType"));
			doc.setDocumentSubId(rs.getString("DocumentSubId"));
			doc.setDocumentRefId(JdbcUtil.getLong(rs.getObject("DocumentRefId")));
			doc.setCollateralRef(rs.getString("CollateralRef"));

			return doc;
		});
	}

	@Override
	public boolean isVettingExists(long id) {
		StringBuilder sql = new StringBuilder(
				"Select count(*) FROM  verification_vt_view  Where verificationId = :verificationId");

		logger.trace(Literal.SQL + sql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("verificationId", id);

		return jdbcTemplate.queryForObject(sql.toString(), source, Integer.class) > 0;
	}

	@Override
	public List<LVDocument> getDocuments(String keyReference, TableType tableType, DocumentType documentType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder();
		sql.append(
				" select verificationid , seqno, documentid,documentType, documentsubid, documentrefid, documenturi,");
		if (documentType == DocumentType.CUSTOMER) {
			sql.append(" custdoccategory as docCategory");
		} else {
			sql.append(" doccategory as docCategory");
		}
		sql.append(" From verification_vt_dtls");

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

		RowMapper<LVDocument> rowMapper = BeanPropertyRowMapper.newInstance(LVDocument.class);

		return jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
	}

	@Override
	public List<Verification> getCollateralDocumentsStatus(String collateralReference) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		RowMapper<Verification> rowMapper = BeanPropertyRowMapper.newInstance(Verification.class);

		StringBuilder sql = new StringBuilder();
		sql.append(" select v.id, v.verificationDate, coalesce(v.status, 0) status,");
		sql.append(" a.dealerName as lastAgency,  documentId, docRefId, referenceFor");
		sql.append(" from  verification_vt_dtls_view dd");
		sql.append(" inner join verifications v on v.id=dd.verificationid");
		sql.append(" left join AMTVehicleDealer_AView a on a.dealerid = v.agency and dealerType = :dealerType");
		sql.append(" where Id = (select coalesce(max(id), 0)");
		sql.append(" from verifications where referenceFor = :referenceFor");
		sql.append(" and verificationType = :verificationType and verificationdate is not null and status !=0)");

		paramMap.addValue("referenceFor", collateralReference);
		paramMap.addValue("verificationType", VerificationType.VETTING.getKey());
		paramMap.addValue("dealerType", Agencies.LVAGENCY.getKey());

		logger.debug(Literal.SQL + sql.toString());
		return jdbcTemplate.query(sql.toString(), paramMap, rowMapper);
	}

	@Override
	public int getCollateralDocumentCount(String collateralRef) {
		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) from  verification_vt_dtls_view vd");
		sql.append(" inner join verifications v on v.id=vd.verificationid");
		sql.append(" inner join documentdetails dd on dd.docid=vd.documentid and dd.docrefid=vd.docrefid");
		sql.append(" where documentType=:documentType and verificationType=:verificationType");
		sql.append(" and vd.referenceId=:referenceId");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("documentType", DocumentType.COLLATRL.getKey());
		paramSource.addValue("verificationType", VerificationType.VETTING.getKey());
		paramSource.addValue("referenceId", collateralRef);

		return jdbcTemplate.queryForObject(sql.toString(), paramSource, Integer.class);
	}

	@Override
	public int getLVDocumentsCount(String collateralRef) {
		StringBuilder sql = null;
		MapSqlParameterSource source = null;
		sql = new StringBuilder();

		sql.append(" select count(*) from  verification_vt_dtls_view vd");
		sql.append(" inner join verifications v on v.id=vd.verificationid ");
		sql.append(" where documentType=:documentType and referenceId=:referenceId");

		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("documentType", DocumentType.COLLATRL.getKey());
		source.addValue("referenceId", collateralRef);

		return jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
	}

	@Override
	public LegalVetting getLegalVetting(long verificationId, String type) {
		StringBuilder sql = null;
		MapSqlParameterSource source = null;
		sql = new StringBuilder();

		sql.append(" Select verificationid, agentCode, agentName,  verificationDate, status, reason, ");
		sql.append(" remarks, verificationFormName,");
		if (type.contains("View")) {
			sql.append(" cif, custid, custName, keyReference, collateralType, referencefor, createdon, ");
			sql.append(" reasonCode, reasonDesc,");
		}
		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" FROM  verification_vt");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where verificationId = :verificationId");

		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("verificationId", verificationId);

		RowMapper<LegalVetting> typeRowMapper = BeanPropertyRowMapper.newInstance(LegalVetting.class);
		try {
			return jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}
}
