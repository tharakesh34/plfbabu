package com.pennant.pff.excess.dao.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.excess.dao.ExcessTransferUploadDAO;
import com.pennant.pff.excess.model.ExcessTransferUpload;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class ExcessTransferUploadDAOImpl extends SequenceDao<ExcessTransferUpload> implements ExcessTransferUploadDAO {

	@Override
	public List<ExcessTransferUpload> getDetails(long headerID) {
		StringBuilder sql = new StringBuilder("Select HeaderId, Id, FinID, FinReference");
		sql.append(", TransferFromType, TransferToType");
		sql.append(", TransferAmount, Status, Progress, ErrorCode, ErrorDesc");
		sql.append(" From EXCESS_TRANSFER_DETAILS_UPLOAD");
		sql.append(" Where HeaderId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, headerID), (rs, rowNum) -> {
			ExcessTransferUpload rpud = new ExcessTransferUpload();

			rpud.setHeaderId(rs.getLong("HeaderId"));
			rpud.setId(rs.getLong("Id"));
			rpud.setReferenceID(JdbcUtil.getLong(rs.getObject("FinID")));
			rpud.setReference(rs.getString("FinReference"));
			rpud.setTransferFromType(rs.getString("TransferFromType"));
			rpud.setTransferToType(rs.getString("TransferToType"));
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

				ps.setObject(++index, detail.getReferenceID());
				ps.setInt(++index, detail.getProgress());
				ps.setString(++index, (detail.getProgress() == EodConstants.PROGRESS_SUCCESS) ? "C" : "R");
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
				ps.setString(++index, (progress == EodConstants.PROGRESS_SUCCESS) ? "C" : "R");
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
		sql.append(" ru.FinReference, ru.TransferFromType");
		sql.append(", ru.TransferToType, ru.TransferAmount, ru.Progress, ru.ErrorCode, ru.ErrorDesc,");
		sql.append(" Case When ru.Status = 'C' then 'Cleared' else 'Rejected' end Status");
		sql.append(", uh.CreatedOn, uh.CreatedBy, uh.ApprovedOn, uh.ApprovedBy");
		sql.append(", su1.UsrLogin CreatedName, su2.UsrLogin ApprovedName");
		sql.append(" From EXCESS_TRANSFER_DETAILS_UPLOAD ru");
		sql.append(" Inner Join FILE_UPLOAD_HEADER uh on uh.ID = ru.HeaderID");
		sql.append(" Inner Join SecUsers su1 on su1.UsrID = uh.CreatedBy");
		sql.append(" Left Join SecUsers su2 on su2.UsrID = uh.ApprovedBy");
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
		String sql = "Select coalesce(sum(BalanceAmt), 0) from FinExcessAmount Where FinID = ? and AmountType = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finID, amountType);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return BigDecimal.ZERO;
		}
	}

	public List<ExcessTransferUpload> getProcess(long headerID) {
		StringBuilder sql = new StringBuilder("Select eu.HeaderId, eu.Id, eu.FinID, eu.FinReference");
		sql.append(", eu.TransferFromType, eu.TransferToType");
		sql.append(", eu.TransferAmount, eu.Status, eu.Progress, eu.ErrorCode, eu.ErrorDesc");
		sql.append(", fh.CreatedOn, fh.CreatedBy, fh.ApprovedOn, fh.ApprovedBy");
		sql.append(" From EXCESS_TRANSFER_DETAILS_UPLOAD eu");
		sql.append(" Inner Join File_Upload_Header fh on fh.ID = eu.HeaderID");
		sql.append(" Where HeaderId = ? and Status = ?");
		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			ExcessTransferUpload etup = new ExcessTransferUpload();

			etup.setHeaderId(rs.getLong("HeaderId"));
			etup.setId(rs.getLong("Id"));
			etup.setReferenceID(JdbcUtil.getLong(rs.getObject("FinID")));
			etup.setReference(rs.getString("FinReference"));
			etup.setTransferFromType(rs.getString("TransferFromType"));
			etup.setTransferToType(rs.getString("TransferToType"));
			etup.setTransferAmount(rs.getBigDecimal("TransferAmount"));
			etup.setStatus(rs.getString("Status"));
			etup.setProgress(rs.getInt("Progress"));
			etup.setErrorCode(rs.getString("ErrorCode"));
			etup.setErrorDesc(rs.getString("ErrorDesc"));
			etup.setCreatedOn(rs.getTimestamp("CreatedOn"));
			etup.setCreatedBy(JdbcUtil.getLong(rs.getObject("CreatedBy")));
			etup.setApprovedOn(rs.getTimestamp("ApprovedOn"));
			etup.setApprovedBy(JdbcUtil.getLong(rs.getObject("ApprovedBy")));

			return etup;
		}, headerID, "C");
	}

	@Override
	public void updateFailure(ExcessTransferUpload detail) {
		String sql = "Update Excess_Transfer_Details_Upload Set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ?  Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		jdbcOperations.update(sql, ps -> {
			int index = 0;

			ps.setInt(++index, EodConstants.PROGRESS_FAILED);
			ps.setString(++index, "R");
			ps.setString(++index, detail.getErrorCode());
			ps.setString(++index, detail.getErrorDesc());

			ps.setLong(++index, detail.getId());
		});
	}

	@Override
	public long getNextValue() {
		return getNextValue("SeqExcess_Transfer_Details");
	}

}
