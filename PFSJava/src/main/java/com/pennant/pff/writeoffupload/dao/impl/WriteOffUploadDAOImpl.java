package com.pennant.pff.writeoffupload.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.backend.util.ReceiptUploadConstants.ReceiptDetailStatus;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.writeoffupload.dao.WriteOffUploadDAO;
import com.pennant.pff.writeoffupload.model.WriteOffUploadDetail;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.file.UploadContants.Status;
import com.pennanttech.pff.file.UploadTypes;

public class WriteOffUploadDAOImpl extends SequenceDao<WriteOffUploadDetail> implements WriteOffUploadDAO {

	public WriteOffUploadDAOImpl() {
		super();
	}

	@Override
	public List<WriteOffUploadDetail> getDetails(long headerID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" HeaderId, Id, FinID, FinReference, RecordSeq");
		sql.append(", Remarks, Progress, ErrorCode, ErrorDesc, Status");
		sql.append(" From WRITE_OFF_UPLOAD");
		sql.append(" Where HeaderId = ? and Status = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), (rs, rownum) -> {
			WriteOffUploadDetail hrud = new WriteOffUploadDetail();

			hrud.setHeaderId(rs.getLong("HeaderId"));
			hrud.setId(rs.getLong("Id"));
			hrud.setReferenceID(JdbcUtil.getLong(rs.getObject("FinID")));
			hrud.setReference(rs.getString("FinReference"));
			hrud.setRecordSeq(rs.getLong("RecordSeq"));
			hrud.setRemarks(rs.getString("Remarks"));
			hrud.setProgress(rs.getInt("Progress"));
			hrud.setErrorCode(rs.getString("ErrorCode"));
			hrud.setErrorDesc(rs.getString("ErrorDesc"));
			hrud.setStatus(rs.getString("Status"));
			return hrud;
		}, headerID, "S");
	}

	@Override
	public List<String> isDuplicateExists(String reference, long headerID) {
		StringBuilder sql = new StringBuilder("Select FileName From FILE_UPLOAD_HEADER");
		sql.append(" Where Type = ? and Id IN (");
		sql.append(" Select HeaderId From WRITE_OFF_UPLOAD");
		sql.append(" Where FinReference = ?");
		sql.append(" and HeaderId <> ? and Progress = ?");
		sql.append(")");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 0;

			ps.setString(++index, UploadTypes.HOLD_REFUND.name());
			ps.setString(++index, reference);
			ps.setLong(++index, headerID);
			ps.setInt(++index, ReceiptDetailStatus.SUCCESS.getValue());

		}, (rs, roNum) -> {
			return rs.getString(1);
		});
	}

	@Override
	public void update(List<WriteOffUploadDetail> detailsList) {
		String sql = "Update WRITE_OFF_UPLOAD set FinID = ?, Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				WriteOffUploadDetail detail = detailsList.get(i);

				ps.setObject(++index, detail.getReferenceID());
				ps.setInt(++index, detail.getProgress());
				ps.setString(++index, (detail.getProgress() == EodConstants.PROGRESS_SUCCESS) ? "S" : "F");
				ps.setString(++index, detail.getErrorCode());
				ps.setString(++index, detail.getErrorDesc());

				ps.setLong(++index, detail.getId());
			}

			@Override
			public int getBatchSize() {
				return detailsList.size();
			}
		});
	}

	@Override
	public void update(List<Long> headerIds, String errorCode, String errorDesc) {
		String sql = "Update WRITE_OFF_UPLOAD set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where HeaderID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;

				long headerID = headerIds.get(i);

				ps.setInt(++index, -1);
				ps.setString(++index, "R");
				ps.setString(++index, errorCode);
				ps.setString(++index, errorDesc);

				ps.setLong(++index, headerID);
			}

			@Override
			public int getBatchSize() {
				return headerIds.size();
			}
		});
	}

	@Override
	public void update(WriteOffUploadDetail detail) {
		String sql = "Update WRITE_OFF_UPLOAD set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, ps -> {
			int index = 0;

			ps.setInt(++index, detail.getProgress());
			ps.setString(++index, (detail.getProgress() == EodConstants.PROGRESS_SUCCESS) ? "S" : "F");
			ps.setString(++index, detail.getErrorCode());
			ps.setString(++index, detail.getErrorDesc());

			ps.setLong(++index, detail.getId());
		});
	}

	@Override
	public String getSqlQuery() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" hu.FinReference, hu.Remarks, hu.Status, hu.ErrorDesc");
		sql.append(", uh.CreatedBy, uh.ApprovedBy,uh.CreatedOn, uh.ApprovedOn");
		sql.append(" From WRITE_OFF_UPLOAD hu");
		sql.append(" Inner Join FILE_UPLOAD_HEADER uh on uh.ID = hu.HeaderID");
		sql.append(" Where uh.ID = :HEADER_ID");

		return sql.toString();
	}

	@Override
	public long saveLog(WriteOffUploadDetail detail, FileUploadHeader header) {
		StringBuilder sql = new StringBuilder("Insert into WRITE_OFF_UPLOAD_LOG");
		sql.append(" (FinId, FinReference, Remarks, CreatedBy, CreatedOn");
		sql.append(", ApprovedBy, ApprovedOn, ReceiptId, Event");
		sql.append(") Values(?, ?, ?, ?, ?, ?, ?, ?, ? )");

		logger.debug(Literal.SQL.concat(sql.toString()));

		KeyHolder keyHolder = new GeneratedKeyHolder();

		this.jdbcOperations.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql.toString(), new String[] { "id" });

				int index = 0;

				ps.setObject(++index, detail.getReferenceID());
				ps.setString(++index, detail.getReference());
				ps.setString(++index, detail.getRemarks());
				ps.setLong(++index, header.getCreatedBy());
				ps.setTimestamp(++index, header.getCreatedOn());
				ps.setLong(++index, header.getLastMntBy());
				ps.setTimestamp(++index, header.getLastMntOn());
				ps.setLong(++index, detail.getReceiptId());
				ps.setString(++index, detail.getEvent());

				return ps;
			}
		}, keyHolder);

		Number key = keyHolder.getKey();

		if (key == null) {
			return 0;
		}

		return key.longValue();
	}

	@Override
	public String getStatus(String finReference) {
		String sql = "Select Status From WRITE_OFF_UPLOAD Where FinReference = ? and Status is null";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, String.class, finReference);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isInProgress(String finReference, long headerID) {
		StringBuilder sql = new StringBuilder("Select Count(FinReference)");
		sql.append(" From WRITE_OFF_UPLOAD wu");
		sql.append(" Inner Join FILE_UPLOAD_HEADER uh on uh.Id = wu.HeaderID");
		sql.append(" Where wu.FinReference = ? and uh.Id <> ? and uh.progress in (?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.queryForObject(sql.toString(), Integer.class, finReference, headerID,
				Status.DOWNLOADED.getValue(), Status.IMPORT_IN_PROCESS.getValue(), Status.IMPORTED.getValue(),
				Status.IN_PROCESS.getValue()) > 0;
	}
}
