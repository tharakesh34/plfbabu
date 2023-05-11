package com.pennant.pff.lien.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.lien.dao.LienUploadDAO;
import com.pennanttech.model.lien.LienUpload;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class LienUploadDAOImpl extends SequenceDao<LienUpload> implements LienUploadDAO {

	public LienUploadDAOImpl() {
		super();
	}

	@Override
	public List<LienUpload> getDetails(long headerID) {
		StringBuilder sql = new StringBuilder("Select Id, HeaderId, RecordSeq,");
		sql.append(" LienId, Source, Reference, AccNumber, Marking, MarkingDate, MarkingReason,");
		sql.append(" DeMarking, DeMarkingReason, DeMarkingDate, LienReference, LienStatus, InterFaceStatus,");
		sql.append(" Remarks, Status, Action,");
		sql.append(" Progress, ErrorCode, ErrorDesc");
		sql.append(" From Lien_Upload");
		sql.append(" Where HeaderId = ? and Status = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			LienUpload lu = new LienUpload();

			lu.setId(rs.getLong("Id"));
			lu.setHeaderId(rs.getLong("HeaderId"));
			lu.setRecordSeq(rs.getLong("RecordSeq"));
			lu.setLienID(rs.getLong("LienId"));
			lu.setSource(rs.getString("Source"));
			lu.setReference(rs.getString("Reference"));
			lu.setAccNumber(rs.getString("AccNumber"));
			lu.setMarking(rs.getString("Marking"));
			lu.setMarkingDate(rs.getTimestamp("MarkingDate"));
			lu.setMarkingReason(rs.getString("MarkingReason"));
			lu.setDemarking(rs.getString(("DeMarking")));
			lu.setDemarkingReason(rs.getString("DeMarkingReason"));
			lu.setDemarkingDate(rs.getDate("DeMarkingDate"));
			lu.setLienReference(rs.getString("LienReference"));
			lu.setLienstatus(rs.getBoolean("LienStatus"));
			lu.setInterfaceStatus(rs.getString("InterFaceStatus"));
			lu.setRemarks(rs.getString("Remarks"));
			lu.setStatus(rs.getString("Status"));
			lu.setAction(rs.getString("Action"));
			lu.setProgress(rs.getInt("Progress"));
			lu.setErrorCode(rs.getString("ErrorCode"));
			lu.setErrorDesc(rs.getString("ErrorDesc"));

			return lu;
		}, headerID, "S");
	}

	@Override
	public void update(LienUpload lu, long id) {

		StringBuilder sql = new StringBuilder("Update Lien_Upload");
		sql.append(" Set HeaderID = ?, LienId = ?, Source = ?, Reference = ?,");
		sql.append(" AccNumber = ?, Action = ?, Marking = ?, MarkingDate = ?,");
		sql.append(" MarkingReason = ?, DeMarking = ?, DeMarkingReason = ?,");
		sql.append(" DeMarkingDate = ?, LienReference = ?, LienStatus = ?,");
		sql.append(" InterFaceStatus = ?,");
		sql.append(" Remarks = ?, Status = ?,");
		sql.append(" Progress = ?, ErrorCode = ?, ErrorDesc = ?");
		sql.append(" Where Id = ?");

		if (lu.getLienID() <= 0) {
			lu.setLienReference(String.valueOf((getNextValue("SEQ_LIEN_HEADER_LIEN_REF"))));
			lu.setLienID((getNextValue("SEQ_LIEN_HEADER_LIEN_ID")));
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, lu.getHeaderId());
			ps.setLong(index++, lu.getLienID());
			ps.setString(index++, lu.getSource());
			ps.setString(index++, lu.getReference());
			ps.setString(index++, lu.getAccNumber());
			ps.setString(index++, lu.getAction());
			ps.setString(index++, lu.getMarking());
			ps.setDate(index++, JdbcUtil.getDate(lu.getMarkingDate()));
			ps.setString(index++, lu.getMarkingReason());
			ps.setString(index++, lu.getDemarking());
			ps.setString(index++, lu.getDemarkingReason());
			ps.setDate(index++, JdbcUtil.getDate(lu.getDemarkingDate()));
			ps.setString(index++, lu.getLienReference());
			ps.setBoolean(index++, lu.getLienstatus());
			ps.setString(index++, lu.getInterfaceStatus());
			ps.setString(index++, lu.getRemarks());
			ps.setString(index++, lu.getStatus());
			ps.setInt(index++, lu.getProgress());
			ps.setString(index++, lu.getErrorCode());
			ps.setString(index++, lu.getErrorDesc());
			ps.setLong(index, id);

		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

		logger.debug("Leaving");
	}

	@Override
	public void updateStatus(List<LienUpload> details) {
		StringBuilder sql = new StringBuilder("Update LIEN_UPLOAD set");
		sql.append(" Reference = ?, AccNumber = ?, Progress = ?");
		sql.append(", Status = ?, ErrorCode = ?, ErrorDesc = ?");
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				LienUpload detail = details.get(i);

				ps.setString(++index, detail.getReference());
				ps.setString(++index, detail.getAccNumber());
				ps.setInt(++index, detail.getProgress());
				ps.setString(++index, (detail.getProgress() == EodConstants.PROGRESS_SUCCESS) ? "S" : "F");
				ps.setString(++index, detail.getErrorCode());
				ps.setString(++index, detail.getErrorDesc());
				ps.setLong(++index, detail.getId());
			}

			@Override
			public int getBatchSize() {
				return details.size();
			}
		});

	}

	@Override
	public void updateRejectStatus(List<Long> headerIds, String errorCode, String errorDesc, int progress) {
		String sql = "Update LIEN_UPLOAD set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where HeaderId = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;

				long headerID = headerIds.get(i);

				ps.setInt(++index, progress);
				ps.setString(++index, (progress == EodConstants.PROGRESS_SUCCESS) ? "S" : "F");
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
	public String getSqlQuery() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append("  lu.Source, lu.Reference, lu.AccNumber,");
		sql.append("  lu.Action, lu.Status,");
		sql.append("  lu.ErrorCode, lu.ErrorDesc");
		sql.append("  From Lien_Upload lu");
		sql.append("  Inner Join File_Upload_Header uh on uh.ID = lu.HeaderId");
		sql.append("  Where uh.ID = :Header_Id");

		return sql.toString();
	}

}