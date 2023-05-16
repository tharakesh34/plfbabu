package com.pennant.pff.noc.upload.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.noc.upload.dao.BlockAutoGenLetterUploadDAO;
import com.pennant.pff.noc.upload.model.BlockAutoGenLetterUpload;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class BlockAutoGenLetterUploadDAOImpl extends SequenceDao<BlockAutoGenLetterUpload>
		implements BlockAutoGenLetterUploadDAO {

	public BlockAutoGenLetterUploadDAOImpl() {
		super();
	}

	@Override
	public List<BlockAutoGenLetterUpload> getDetails(long headerID) {
		StringBuilder sql = new StringBuilder("Select ID, HeaderId, RecordSeq");
		sql.append(", FinID, FinReference, Action, Remarks");
		sql.append(", Progress, Status, ErrorCode, ErrorDesc");
		sql.append(" From Block_Auto_Gen_Ltr_Upload");
		sql.append(" Where HeaderId = ? and Status = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			BlockAutoGenLetterUpload bg = new BlockAutoGenLetterUpload();

			bg.setId(rs.getLong("ID"));
			bg.setHeaderId(rs.getLong("HeaderId"));
			bg.setRecordSeq(rs.getLong("RecordSeq"));
			bg.setReferenceID(JdbcUtil.getLong(rs.getObject("FinID")));
			bg.setReference(rs.getString("FinReference"));
			bg.setAction(rs.getString("Action"));
			bg.setRemarks(rs.getString("Remarks"));
			bg.setProgress(rs.getInt("Progress"));
			bg.setStatus(rs.getString("Status"));
			bg.setErrorCode(rs.getString("ErrorCode"));
			bg.setErrorDesc(rs.getString("ErrorDesc"));

			return bg;
		}, headerID, "S");
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
	public void update(List<Long> headerIds, String errorCode, String errorDesc) {
		String sql = "Update Block_Auto_Gen_Ltr_Upload set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where HeaderId = ?";

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
	public String getSqlQuery() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" bg.FinReference, bg.Action, bg.Remarks, bg.Status");
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
	public boolean isValidateAction(long finid) {
		String sql = "Select count(FinID) From Letter_Blocking Where FinID = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, finid) > 0;
	}

	@Override
	public void delete(long finid) {
		String sql = "Delete from Letter_Blocking where FinID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, finid);
	}

	@Override
	public void save(BlockAutoGenLetterUpload bu) {
		StringBuilder sql = new StringBuilder("Insert into Letter_Blocking");
		sql.append(" (FinID, CreatedOn)");
		sql.append(" Values(?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));
		try {
			this.jdbcOperations.update(sql.toString(), ps -> {

				ps.setObject(1, bu.getReferenceID());
				ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));

			});

		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public void savebyLog(BlockAutoGenLetterUpload bu) {
		StringBuilder sql = new StringBuilder("Insert into Letter_Blocking_Log");
		sql.append(" (FinID, BlockType, UpdatedOn)");
		sql.append(" Values(?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));
		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 0;

				ps.setObject(++index, bu.getReferenceID());
				ps.setString(++index, bu.getAction());
				ps.setTimestamp(++index, new Timestamp(System.currentTimeMillis()));

			});

		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

}