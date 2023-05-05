package com.pennant.backend.dao.loancancel.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.dao.loancancel.FinanceCancellationUploadDAO;
import com.pennant.backend.model.finance.FinCancelUploadDetail;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class FinanceCancellationUploadDAOImpl extends SequenceDao<FinCancelUploadDetail>
		implements FinanceCancellationUploadDAO {

	@Override
	public void update(List<Long> headerIds, String errorCode, String errorDesc, int progress) {
		String sql = "Update LOAN_CANCEL_UPLOADS set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where HeaderID = ?";

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
		sql.append(" FinReference, CancelType, Reason, lcu.Remarks, lcu.Status, ");
		sql.append(" ErrorDesc, uh.CreatedBy, uh.ApprovedBy, uh.CreatedOn, uh.ApprovedOn ");
		sql.append(" From LOAN_CANCEL_UPLOADS lcu");
		sql.append(" Inner Join File_Upload_Header uh on uh.ID = lcu.HeaderID");
		sql.append(" Inner Join SecUsers su1 on su1.UsrID = uh.CreatedBy");
		sql.append(" Left Join SecUsers su2 on su2.UsrID = uh.ApprovedBy");
		sql.append(" Where uh.ID = :HEADER_ID");

		return sql.toString();
	}

	@Override
	public List<FinCancelUploadDetail> getDetails(long headerID) {
		String sql = "Select HeaderId, Id, FinID, FinReference, RecordSeq, CancelType, Reason, Remarks, Progress, Status, ErrorCode, ErrorDesc From LOAN_CANCEL_UPLOADS Where HeaderId = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.query(sql, ps -> ps.setLong(1, headerID), (rs, rowNum) -> {
			FinCancelUploadDetail fcud = new FinCancelUploadDetail();

			fcud.setHeaderId(rs.getLong("HeaderId"));
			fcud.setId(rs.getLong("Id"));
			fcud.setReferenceID(JdbcUtil.getLong(rs.getObject("FinID")));
			fcud.setReference(rs.getString("FinReference"));
			fcud.setRecordSeq(rs.getLong("RecordSeq"));
			fcud.setCancelType(rs.getString("CancelType"));
			fcud.setReason(rs.getString("Reason"));
			fcud.setRemarks(rs.getString("Remarks"));
			fcud.setProgress(rs.getInt("Progress"));
			fcud.setStatus(rs.getString("Status"));
			fcud.setErrorCode(rs.getString("ErrorCode"));
			fcud.setErrorDesc(rs.getString("ErrorDesc"));
			return fcud;
		});
	}

	@Override
	public void update(List<FinCancelUploadDetail> detailsList) {
		String sql = "Update LOAN_CANCEL_UPLOADS set Progress = ?, FinId = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				FinCancelUploadDetail detail = detailsList.get(i);

				ps.setInt(++index, detail.getProgress());
				ps.setObject(++index, detail.getReferenceID());
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
	public void update(FinCancelUploadDetail detail) {
		String sql = "Update LOAN_CANCEL_UPLOADS set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where ID = ?";

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
}