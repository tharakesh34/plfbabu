package com.pennanttech.pff.documents.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.documents.model.Document;
import com.pennanttech.pff.documents.model.DocumentStatus;
import com.pennanttech.pff.documents.model.DocumentStatusDetail;

public class DocumentDaoImpl extends SequenceDao<Document> implements DocumentDao {

	@Override
	public DocumentStatusDetail getDocumentStatusById(long id, String tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, HeaderId, DocId, Status, Remarks, Covenants, Processed");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkFlowId");
		sql.append(" From Document_Status_Details").append(tableType);
		sql.append(" Where Id = ?");

		logger.trace(Literal.SQL + sql.toString());

		return jdbcOperations.queryForObject(sql.toString(), new DocumentStatusRowMapper(), id);
	}

	@Override
	public List<DocumentStatusDetail> getDocumentStatus(List<Long> list) {
		StringBuilder sql = new StringBuilder("Select * from (");
		sql.append(" Select Id, HeaderId, DocId, Status, Remarks, Covenants, Processed");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkFlowId");
		sql.append(" From Document_Status_Details_Temp");
		sql.append(" Union All");
		sql.append(" Select Id, HeaderId, DocId, Status, Remarks, Covenants, Processed");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkFlowId");
		sql.append(" From Document_Status_Details");
		sql.append(" Where not exists (select 1 from Document_Status_Details_Temp");
		sql.append(" Where Id = Document_Status_Details.Id)");
		sql.append(") ds");

		if (CollectionUtils.isNotEmpty(list)) {
			sql.append(" Where DocId in (");
			sql.append(list.stream().map(e -> "?").collect(Collectors.joining(",")));
			sql.append(")");
		}

		logger.trace(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			for (Long docId : list) {
				ps.setLong(index++, docId);
			}
		}, new DocumentStatusRowMapper());
	}

	@Override
	public DocumentStatus getDocumentStatus(String finReference) {
		StringBuilder sql = new StringBuilder("select * from (");
		sql.append(" Select Id, FinReference, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkFlowId");
		sql.append(" from Document_Status_Temp");
		sql.append(" Union All");
		sql.append(" Select Id, FinReference, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkFlowId");
		sql.append(" from Document_Status");
		sql.append(" Where not exists (select 1 from Document_Status_Temp Where Id = Document_Status.Id)) T");
		sql.append(" Where FinReference = ?");
		logger.trace(Literal.SQL + sql.toString());

		try {

			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				DocumentStatus ds = new DocumentStatus();
				ds.setId(rs.getLong("Id"));
				ds.setFinReference(rs.getString("FinReference"));
				ds.setVersion(rs.getInt("Version"));
				ds.setLastMntBy(rs.getLong("LastMntBy"));
				ds.setLastMntOn(rs.getTimestamp("LastMntOn"));
				ds.setRecordStatus(rs.getString("RecordStatus"));
				ds.setRoleCode(rs.getString("RoleCode"));
				ds.setNextRoleCode(rs.getString("NextRoleCode"));
				ds.setTaskId(rs.getString("TaskId"));
				ds.setNextTaskId(rs.getString("NextTaskId"));
				ds.setRecordType(rs.getString("RecordType"));
				ds.setWorkflowId(rs.getLong("WorkFlowId"));

				return ds;
			}, finReference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}
		return null;
	}

	@Override
	public List<Document> getDocuments(String finReferece) {
		StringBuilder sql = new StringBuilder("select * from (");
		sql.append(" Select Id, fm.CustId, fm.FinReference, 'CD' DocType, CustDocCategory DocCategory");
		sql.append(", CustDocName DocName");
		sql.append(" From CustomerDocuments_View cd");
		sql.append(" Inner join FinanceMain_View fm on fm.CustId = cd.CustId");
		sql.append(" Union All");
		sql.append(" Select DocId Id, fm.CustId, fm.FinReference, 'DD' DocType, DocCategory, DocName");
		sql.append(" From DocumentDetails_View dd");
		sql.append(" Inner join FinanceMain_View fm on fm.FinReference = dd.ReferenceId) doc");
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			Document doc = new Document();
			doc.setId(rs.getLong("Id"));
			doc.setCustId(rs.getLong("CustId"));
			doc.setFinReference(rs.getString("FinReference"));
			doc.setDocType(rs.getString("DocType"));
			doc.setDocName(rs.getString("DocName"));
			doc.setDocCategory(rs.getString("DocCategory"));

			return doc;
		}, finReferece);
	}

	@Override
	public int update(DocumentStatusDetail ds, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update Document_Status_Details");
		sql.append(tableType.getSuffix());
		sql.append(" Set Status = ?, Remarks = ?, Covenants = ?, Processed = ?");
		sql.append(" Where Id = ?");

		logger.trace(Literal.SQL + sql.toString());

		return jdbcOperations.update(sql.toString(), ps -> {
			ps.setString(1, ds.getStatus());
			ps.setString(2, ds.getRemarks());
			ps.setString(3, ds.getCovenants());
			ps.setInt(4, ds.getProcessed());
			ps.setLong(5, ds.getId());
		});
	}

	@Override
	public long save(DocumentStatus ds, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert into Document_Status");
		sql.append(tableType.getSuffix());
		sql.append("(Id, FinReference");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkFlowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.trace(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			if (ds.getId() == Long.MIN_VALUE || ds.getId() == 0) {
				ds.setId(getNextValue("SeqDocument_Status"));
			}

			ps.setLong(index++, ds.getId());
			ps.setString(index++, ds.getFinReference());
			ps.setInt(index++, ds.getVersion());
			ps.setLong(index++, ds.getLastMntBy());
			ps.setTimestamp(index++, ds.getLastMntOn());
			ps.setString(index++, ds.getRecordStatus());
			ps.setString(index++, ds.getRoleCode());
			ps.setString(index++, ds.getNextRoleCode());
			ps.setString(index++, ds.getTaskId());
			ps.setString(index++, ds.getNextTaskId());
			ps.setString(index++, ds.getRecordType());
			ps.setLong(index, ds.getWorkflowId());
		});

		return ds.getId();
	}

	@Override
	public long save(DocumentStatusDetail ds, TableType tableType) {
		List<DocumentStatusDetail> list = new ArrayList<>();
		list.add(ds);
		save(list, tableType);
		return ds.getId();
	}

	private void save(List<DocumentStatusDetail> doclist, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert into Document_Status_Details");
		sql.append(tableType.getSuffix());
		sql.append("(Id, HeaderId, DocId, Status, Remarks, Covenants, Processed");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkFlowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.trace(Literal.SQL + sql.toString());

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				DocumentStatusDetail ds = doclist.get(i);
				int index = 1;

				if (ds.getId() == Long.MIN_VALUE || ds.getId() == 0) {
					ds.setId(getNextValue("SeqDocument_Status_Details"));
				}

				ps.setLong(index++, ds.getId());
				ps.setLong(index++, ds.getHeaderId());
				ps.setLong(index++, ds.getDocId());
				ps.setString(index++, ds.getStatus());
				ps.setString(index++, ds.getRemarks());
				ps.setString(index++, ds.getCovenants());
				ps.setInt(index++, ds.getProcessed());
				ps.setInt(index++, ds.getVersion());
				ps.setLong(index++, ds.getLastMntBy());
				ps.setTimestamp(index++, ds.getLastMntOn());
				ps.setString(index++, ds.getRecordStatus());
				ps.setString(index++, ds.getRoleCode());
				ps.setString(index++, ds.getNextRoleCode());
				ps.setString(index++, ds.getTaskId());
				ps.setString(index++, ds.getNextTaskId());
				ps.setString(index++, ds.getRecordType());
				ps.setLong(index, ds.getWorkflowId());
			}

			@Override
			public int getBatchSize() {
				return doclist.size();
			}
		});
	}

	@Override
	public void delete(DocumentStatus ds, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete");
		sql.append(" From Document_Status");
		sql.append(tableType.getSuffix());
		sql.append(" Where Id = ?");

		logger.trace(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), ds.getId());
	}

	@Override
	public void delete(DocumentStatusDetail ds, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delet Document_Status_Details");
		sql.append(tableType.getSuffix());
		sql.append(" Where Id = ?");

		logger.trace(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), ds.getId());
	}

	@Override
	public void deleteChildrens(long headerId, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete");
		sql.append(" From Document_Status_Details");
		sql.append(tableType.getSuffix());
		sql.append(" Where HeaderId = ?");

		logger.trace(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), headerId);
	}

	private class DocumentStatusRowMapper implements RowMapper<DocumentStatusDetail> {

		@Override
		public DocumentStatusDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
			DocumentStatusDetail ds = new DocumentStatusDetail();

			ds.setId(rs.getLong("Id"));
			ds.setHeaderId(rs.getLong("HeaderId"));
			ds.getDocument().setId(rs.getLong("DocId"));
			ds.setDocId(rs.getLong("DocId"));
			ds.setStatus(rs.getString("Status"));
			ds.setRemarks(rs.getString("Remarks"));
			ds.setCovenants(rs.getString("Covenants"));
			ds.setProcessed(rs.getInt("Processed"));
			ds.setVersion(rs.getInt("Version"));
			ds.setLastMntBy(rs.getLong("LastMntBy"));
			ds.setLastMntOn(rs.getTimestamp("LastMntOn"));
			ds.setRecordStatus(rs.getString("RecordStatus"));
			ds.setRoleCode(rs.getString("RoleCode"));
			ds.setNextRoleCode(rs.getString("NextRoleCode"));
			ds.setTaskId(rs.getString("TaskId"));
			ds.setNextTaskId(rs.getString("NextTaskId"));
			ds.setRecordType(rs.getString("RecordType"));
			ds.setWorkflowId(rs.getLong("WorkFlowId"));

			return ds;
		}
	}

	@Override
	public void update(DocumentStatus ds, TableType tableType) {
		StringBuilder updateSql = new StringBuilder("Update Document_Status");
		updateSql.append(tableType.getSuffix());
		updateSql.append(" Set Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		updateSql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		updateSql.append(" Where Id = ?");

		logger.trace(Literal.SQL + updateSql.toString());

		this.jdbcOperations.update(updateSql.toString(), ps -> {
			int index = 1;
			ps.setInt(index++, ds.getVersion());
			ps.setLong(index++, ds.getLastMntBy());
			ps.setTimestamp(index++, ds.getLastMntOn());
			ps.setString(index++, ds.getRecordStatus());
			ps.setString(index++, ds.getRoleCode());
			ps.setString(index++, ds.getNextRoleCode());
			ps.setString(index++, ds.getTaskId());
			ps.setString(index++, ds.getNextTaskId());
			ps.setString(index++, ds.getRecordType());
			ps.setLong(index++, ds.getWorkflowId());
			ps.setLong(index, ds.getId());
		});

	}

	@Override
	public void updateStaus(List<DocumentStatusDetail> list) {
		jdbcOperations.batchUpdate("Update Document_Status_Details set Processed = ? where Id = ?",
				new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ps.setInt(1, 1);
						ps.setLong(2, list.get(i).getId());

					}

					@Override
					public int getBatchSize() {
						return list.size();
					}
				});
	}

	@Override
	public int resetStatus(long docId) {
		StringBuilder updateSql = new StringBuilder("Update Document_Status_Details");
		updateSql.append(" Set Processed = ?, Status = ?");
		updateSql.append(" Where DocId = ? and Status ='R'");

		return this.jdbcOperations.update(updateSql.toString(), ps -> {
			int index = 1;
			ps.setInt(index++, 0);
			ps.setString(index++, " ");
			ps.setLong(index, docId);
		});

	}

	@Override
	public DocumentStatusDetail getDocumentStatusByDocId(long id, String tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, HeaderId, DocId, Status, Remarks, Covenants, Processed");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkFlowId");
		sql.append(" From Document_Status_Details").append(tableType);
		sql.append(" Where DocId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), new DocumentStatusRowMapper(), id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}
}
