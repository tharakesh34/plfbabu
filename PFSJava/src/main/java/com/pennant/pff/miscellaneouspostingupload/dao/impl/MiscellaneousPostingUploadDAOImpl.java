package com.pennant.pff.miscellaneouspostingupload.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.model.miscellaneousposting.upload.MiscellaneousPostingUpload;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.miscellaneouspostingupload.dao.MiscellaneousPostingUploadDAO;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class MiscellaneousPostingUploadDAOImpl extends SequenceDao<MiscellaneousPostingUpload>
		implements MiscellaneousPostingUploadDAO {

	@Override
	public List<MiscellaneousPostingUpload> getDetails(long headerID) {
		StringBuilder sql = new StringBuilder("Select ID, HeaderId, RecordSeq, BatchName");
		sql.append(", BatchPurpose, Reference, FinID, BatchReference, DebitGL, CreditGL");
		sql.append(", TxnAmount, ValueDate, NarrLine1, NarrLine2, NarrLine3, NarrLine4");
		sql.append(", Progress, Status, ErrorCode, ErrorDesc");
		sql.append(" From MISCELLANEOUS_POSTING_UPLOAD");
		sql.append(" Where HeaderId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			MiscellaneousPostingUpload mp = new MiscellaneousPostingUpload();

			mp.setId(rs.getLong("ID"));
			mp.setHeaderId(rs.getLong("HeaderId"));
			mp.setRecordSeq(rs.getLong("RecordSeq"));
			mp.setBatchName(rs.getString("BatchName"));
			mp.setBatchPurpose(rs.getString("BatchPurpose"));
			mp.setReferenceID(JdbcUtil.getLong(rs.getObject("FinID")));
			mp.setBatchReference(rs.getLong("BatchReference"));
			mp.setReference(rs.getString("Reference"));
			mp.setDebitGL(rs.getString("DebitGL"));
			mp.setCreditGL(rs.getString("CreditGL"));
			mp.setTxnAmount(rs.getBigDecimal("TxnAmount"));
			mp.setValueDate(rs.getDate("ValueDate"));
			mp.setNarrLine1(rs.getString("NarrLine1"));
			mp.setNarrLine2(rs.getString("NarrLine2"));
			mp.setNarrLine2(rs.getString("NarrLine3"));
			mp.setNarrLine4(rs.getString("NarrLine4"));
			mp.setProgress(rs.getInt("Progress"));
			mp.setStatus(rs.getString("Status"));
			mp.setErrorCode(rs.getString("ErrorCode"));
			mp.setErrorDesc(rs.getString("ErrorDesc"));

			return mp;
		}, headerID);
	}

	@Override
	public void update(List<MiscellaneousPostingUpload> details) {
		String sql = "Update MISCELLANEOUS_POSTING_UPLOAD set FinID = ?, Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				MiscellaneousPostingUpload detail = details.get(i);

				ps.setObject(++index, detail.getReferenceID());
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
	public void update(List<Long> headerIds, String errorCode, String errorDesc, int progress) {
		String sql = "Update MISCELLANEOUS_POSTING_UPLOAD set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where HeaderId = ?";

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
		sql.append("  mp.BatchName, mp.BatchPurpose, mp.Reference, mp.DebitGL, mp.CreditGL, mp.TxnAmount");
		sql.append(", mp.ValueDate, mp.NarrLine1, mp.NarrLine2, mp.NarrLine3, mp.NarrLine4, mp.BatchReference");
		sql.append(", mp.Status, mp.ErrorCode, mp.ErrorDesc, uh.ApprovedOn, uh.CreatedOn");
		sql.append(", su1.UsrLogin CreatedName, su2.UsrLogin ApprovedName");
		sql.append("  From MISCELLANEOUS_POSTING_UPLOAD mp");
		sql.append("  Inner Join File_Upload_Header uh on uh.Id = mp.HeaderId");
		sql.append("  Inner Join SecUsers su1 on su1.UsrID = uh.CreatedBy");
		sql.append("  Left Join SecUsers su2 on su2.UsrID = uh.ApprovedBy");
		sql.append("  Where uh.Id = :HEADER_ID");

		return sql.toString();
	}

	@Override
	public void updateBatchReference(List<MiscellaneousPostingUpload> details, long batchReference) {
		String sql = "Update MISCELLANEOUS_POSTING_UPLOAD  Set BatchReference = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				MiscellaneousPostingUpload detail = details.get(i);

				ps.setLong(++index, batchReference);

				ps.setObject(++index, detail.getId());
			}

			@Override
			public int getBatchSize() {
				return details.size();
			}
		});
	}

}
