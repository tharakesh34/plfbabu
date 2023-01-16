package com.pennant.backend.dao.excessheadmaster.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.model.excessheadmaster.ExcessTransferUpload;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.presentment.dao.ExcessTransferUploadDAO;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class ExcessTransferUploadDAOImpl extends SequenceDao<ExcessTransferUpload> implements ExcessTransferUploadDAO {

	@Override
	public List<ExcessTransferUpload> getDetails(long headerID) {
		StringBuilder sql = new StringBuilder("Select HeaderId, Id, FinID, FinReference");
		sql.append(", TransferFromType, TransferFromId, TransferToType, TransferToId");
		sql.append(", TransferAmount, Status, Progress, ErrorCode, ErrorDesc");
		sql.append(" From EXCESS_TRANSFER_DETAILS_UPLOAD");
		sql.append(" Where HeaderId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, headerID), (rs, Num) -> {
			ExcessTransferUpload rpud = new ExcessTransferUpload();

			rpud.setHeaderId(rs.getLong("HeaderId"));
			rpud.setId(rs.getLong("Id"));
			rpud.setReferenceID(JdbcUtil.getLong(rs.getObject("FinID")));
			rpud.setReference(rs.getString("FinReference"));
			rpud.setTransferFromType(rs.getString("TransferFromType"));
			rpud.setTransferFromId(rs.getInt("TransferFromId"));
			rpud.setTransferToType(rs.getString("TransferToType"));
			rpud.setTransferToId(rs.getInt("TransferToId"));
			rpud.setTransferAmount(rs.getBigDecimal("TransferAmount"));
			rpud.setStatus(rs.getString("Status"));
			rpud.setProgress(rs.getInt("Progress"));
			rpud.setErrorCode(rs.getString("ErrorCode"));
			rpud.setErrorDesc(rs.getString("ErrorDesc"));
			return rpud;
		});
	}

	@Override
	public void update(List<ExcessTransferUpload> detailsList) {
		StringBuilder sql = new StringBuilder("Update EXCESS_TRANSFER_DETAILS_UPLOAD set");
		sql.append(" FinID = ?, Progress = ?, Status = ?, ErrorCode = ?");
		sql.append(", ErrorDesc = ? ");
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				ExcessTransferUpload detail = detailsList.get(i);

				ps.setLong(++index, detail.getReferenceID());
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
	public void update(List<Long> headerIds, String errorCode, String errorDesc, int progress) {
		String sql = "Update EXCESS_TRANSFER_DETAILS_UPLOAD set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where HeaderID = ?";

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
		sql.append(" ru.FinReference, ru.TransferFromType, ru.TransferFromId,");
		sql.append(", ru.TransferToType, ru.TransferToId, ru.TransferAmount");
		sql.append(" From EXCESS_TRANSFER_DETAILS_UPLOAD ru");
		sql.append(" Inner Join FILE_UPLOAD_HEADER uh on uh.ID = ru.HeaderID");
		sql.append(" Where uh.ID = :HEADER_ID");

		return sql.toString();
	}

	@Override
	public boolean isDuplicateExists(String reference, String transferfromtype, long headerID) {
		StringBuilder sql = new StringBuilder("Select count(ID) From EXCESS_TRANSFER_DETAILS_UPLOAD");
		sql.append(" Where FinReference = ? and TransferFromType = ? and HeaderId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, reference, transferfromtype,
				headerID) > 1;
	}

	@Override
	public BigDecimal getBalanceAmount(long finID, String amountType) {
		String sql = "Select sum(BalanceAmt) from FinExcessAmount Where FinID = ? and AmountType = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finID, amountType);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return BigDecimal.ZERO;
		}
	}

	public String getRecords(long headerID, String status) {
		String sql = "Select HeaderId, Status from EXCESS_TRANSFER_DETAILS_UPLOAD Where HeaderId = ? and Status = ? ";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), String.class, headerID, status);
		} catch (Exception e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return sql.toString();
		}
	}
}
