package com.pennanttech.pff.blockautolettergenerate.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.model.blockautolettergenerate.upload.BlockAutoGenLetterUpload;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.blockautolettergenerate.dao.BlockAutoGenLetterUploadDAO;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class BlockAutoGenLetterUploadDAOImpl extends SequenceDao<BlockAutoGenLetterUpload>
		implements BlockAutoGenLetterUploadDAO {

	public BlockAutoGenLetterUploadDAOImpl() {
		super();
	}

	@Override
	public List<BlockAutoGenLetterUpload> getDetails(long headerID) {
		StringBuilder sql = new StringBuilder("Select ID, HeaderId");
		sql.append(", FinReference, FinID, Action, Remarks");
		sql.append(", Progress, Status, ErrorCode, ErrorDesc");
		sql.append(" From Block_Auto_Gen_Ltr_Upload");
		sql.append(" Where HeaderId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			BlockAutoGenLetterUpload bg = new BlockAutoGenLetterUpload();

			bg.setId(rs.getLong("ID"));
			bg.setHeaderId(rs.getLong("HeaderId"));
			bg.setReference(rs.getString("FinReference"));
			bg.setReferenceID(JdbcUtil.getLong(rs.getObject("FinID")));
			bg.setAction(rs.getString("Action"));
			bg.setRemarks(rs.getString("Remarks"));
			bg.setProgress(rs.getInt("Progress"));
			bg.setStatus(rs.getString("Status"));
			bg.setErrorCode(rs.getString("ErrorCode"));
			bg.setErrorDesc(rs.getString("ErrorDesc"));

			return bg;
		}, headerID);
	}

	@Override
	public void update(List<BlockAutoGenLetterUpload> details) {
		String sql = "Update Block_Auto_Gen_Ltr_Upload set FinID = ?, Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				BlockAutoGenLetterUpload detail = details.get(i);

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
		String sql = "Update Block_Auto_Gen_Ltr_Upload set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where HeaderId = ?";

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
		sql.append("  bg.FinReference, bg.Action, bg.Remarks, bg.Status");
		sql.append(", bg.ErrorCode, bg.ErrorDesc, uh.ApprovedOn, uh.CreatedOn");
		sql.append(", su1.UsrLogin CreatedName, su2.UsrLogin ApprovedName");
		sql.append("  From Block_Auto_Gen_Ltr_Upload bg");
		sql.append("  Inner Join File_Upload_Header uh on uh.Id = bg.HeaderId");
		sql.append("  Inner Join SecUsers su1 on su1.UsrID = uh.CreatedBy");
		sql.append("  Left Join SecUsers su2 on su2.UsrID = uh.ApprovedBy");
		sql.append("  Where uh.Id = :HEADER_ID");

		return sql.toString();
	}

	@Override
	public boolean isValidateAction(String reference, String action, int progressSuccess) {
		String sql = "Select count(FinReference) From Block_Auto_Gen_Ltr_Upload Where FinReference = ? and Action = ? and Progress = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return jdbcOperations.queryForObject(sql, Integer.class, reference, action, progressSuccess) > 0;
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return false;
		}
	}

	@Override
	public int getReference(String reference, int progressSuccess) {
		String sql = "Select count(FinReference) From Block_Auto_Gen_Ltr_Upload Where FinReference = ? and Progress = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, reference, progressSuccess);
	}

	@Override
	public void delete(String reference, int progressSuccess) {
		String sql = "Delete from Block_Auto_Gen_Ltr_Upload where FinReference = ? and Progress = ?";

		logger.debug(Literal.SQL.concat(sql));

		jdbcOperations.update(sql, ps -> {
			ps.setString(1, reference);
			ps.setInt(2, progressSuccess);
		});
	}

	@Override
	public long save(BlockAutoGenLetterUpload bu) {
		StringBuilder sql = new StringBuilder("Insert into Block_Auto_Gen_Ltr_Log");
		sql.append(" (HeaderId, FinID, FinReference, Action, Progress, Remarks, Status");
		sql.append(", ErrorCode, ErrorDesc, CreatedOn, CreatedBy, ApprovedOn, ApprovedBy)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.update(sql.toString(), ps -> {

			int index = 0;

			ps.setLong(++index, bu.getHeaderId());
			ps.setObject(++index, bu.getReferenceID());
			ps.setString(++index, bu.getReference());
			ps.setString(++index, bu.getAction());
			ps.setInt(++index, bu.getProgress());
			ps.setString(++index, bu.getRemarks());
			ps.setString(++index, bu.getStatus());
			ps.setString(++index, bu.getErrorCode());
			ps.setString(++index, bu.getErrorDesc());
			ps.setTimestamp(++index, bu.getCreatedOn());
			ps.setLong(++index, bu.getCreatedBy());
			ps.setTimestamp(++index, bu.getApprovedOn());
			ps.setLong(++index, bu.getApprovedBy());

		});

		return bu.getId();
	}
}