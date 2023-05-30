package com.pennant.pff.revwriteoffupload.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.backend.model.finance.FinanceWriteoff;
import com.pennant.backend.util.ReceiptUploadConstants.ReceiptDetailStatus;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.revwriteoffupload.dao.RevWriteOffUploadDAO;
import com.pennant.pff.revwriteoffupload.model.RevWriteOffUploadDetail;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.file.UploadContants.Status;
import com.pennanttech.pff.file.UploadTypes;

public class RevWriteOffUploadDAOImpl extends SequenceDao<RevWriteOffUploadDetail> implements RevWriteOffUploadDAO {

	public RevWriteOffUploadDAOImpl() {
		super();
	}

	@Override
	public List<RevWriteOffUploadDetail> getDetails(long headerID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" HeaderId, Id, FinID, FinReference, RecordSeq, Remarks");
		sql.append(", Progress, ErrorCode, ErrorDesc, Status");
		sql.append(" From REV_WRITE_OFF_UPLOAD");
		sql.append(" Where HeaderId = ? and Status = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			RevWriteOffUploadDetail rwud = new RevWriteOffUploadDetail();

			rwud.setHeaderId(rs.getLong("HeaderId"));
			rwud.setId(rs.getLong("Id"));
			rwud.setReferenceID(JdbcUtil.getLong(rs.getObject("FinID")));
			rwud.setReference(rs.getString("FinReference"));
			rwud.setRecordSeq(rs.getLong("RecordSeq"));
			rwud.setRemarks(rs.getString("Remarks"));
			rwud.setProgress(rs.getInt("Progress"));
			rwud.setErrorCode(rs.getString("ErrorCode"));
			rwud.setErrorDesc(rs.getString("ErrorDesc"));
			rwud.setStatus(rs.getString("Status"));
			return rwud;
		}, headerID, "S");
	}

	@Override
	public List<String> isDuplicateExists(String reference, long headerID) {
		StringBuilder sql = new StringBuilder("Select FileName From FILE_UPLOAD_HEADER");
		sql.append(" Where Type = ? and Id IN (");
		sql.append(" Select HeaderId From REV_WRITE_OFF_UPLOAD");
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

		}, (rs, roNum) -> rs.getString(1));
	}

	@Override
	public void update(List<RevWriteOffUploadDetail> detailsList) {
		String sql = "Update REV_WRITE_OFF_UPLOAD set FinID = ?, Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				RevWriteOffUploadDetail detail = detailsList.get(i);

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
		String sql = "Update REV_WRITE_OFF_UPLOAD set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where HeaderID = ?";

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
	public void update(RevWriteOffUploadDetail detail) {
		String sql = "Update REV_WRITE_OFF_UPLOAD set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where ID = ?";

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
		sql.append(" From REV_WRITE_OFF_UPLOAD hu");
		sql.append(" Inner Join FILE_UPLOAD_HEADER uh on uh.ID = hu.HeaderID");
		sql.append(" Where uh.ID = :HEADER_ID");

		return sql.toString();
	}

	@Override
	public long saveLog(RevWriteOffUploadDetail detail, FileUploadHeader header) {
		StringBuilder sql = new StringBuilder("Insert into WRITE_OFF_UPLOAD_LOG");
		sql.append(" (FinId, FinReference, Remarks, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, Event");
		sql.append(") Values(?, ?, ?, ?, ?, ?, ?, ?)");

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
		String sql = "Select Status From REV_WRITE_OFF_UPLOAD Where FinReference = ? and Status is null";

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
		sql.append(" From REV_WRITE_OFF_UPLOAD wu");
		sql.append(" Inner Join FILE_UPLOAD_HEADER uh on uh.Id = wu.HeaderID");
		sql.append(" Where wu.FinReference = ? and uh.Id <> ? and uh.progress in (?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.queryForObject(sql.toString(), Integer.class, finReference, headerID,
				Status.DOWNLOADED.getValue(), Status.IMPORT_IN_PROCESS.getValue(), Status.IMPORTED.getValue(),
				Status.IN_PROCESS.getValue()) > 0;
	}

	@Override
	public String save(FinanceWriteoff fwo, String type) {
		StringBuilder sql = new StringBuilder("Insert Into FIN_WRITE_OFF_DETAIL_LOG");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FinID, FinReference, WriteoffDate, SeqNo, WrittenoffPri, WrittenoffPft, CurODPri, CurODPft");
		sql.append(", UnPaidSchdPri, UnPaidSchdPft, PenaltyAmount, ProvisionedAmount, WriteoffPrincipal");
		sql.append(", WriteoffProfit, AdjAmount, Remarks, WrittenoffSchFee, UnpaidSchFee, WriteoffSchFee)");
		sql.append(" Values( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, fwo.getFinID());
			ps.setString(index++, fwo.getFinReference());
			ps.setDate(index++, JdbcUtil.getDate(fwo.getWriteoffDate()));
			ps.setInt(index++, fwo.getSeqNo());
			ps.setBigDecimal(index++, fwo.getWrittenoffPri());
			ps.setBigDecimal(index++, fwo.getWrittenoffPft());
			ps.setBigDecimal(index++, fwo.getCurODPri());
			ps.setBigDecimal(index++, fwo.getCurODPft());
			ps.setBigDecimal(index++, fwo.getUnPaidSchdPri());
			ps.setBigDecimal(index++, fwo.getUnPaidSchdPft());
			ps.setBigDecimal(index++, fwo.getPenaltyAmount());
			ps.setBigDecimal(index++, fwo.getProvisionedAmount());
			ps.setBigDecimal(index++, fwo.getWriteoffPrincipal());
			ps.setBigDecimal(index++, fwo.getWriteoffProfit());
			ps.setBigDecimal(index++, fwo.getAdjAmount());
			ps.setString(index++, fwo.getRemarks());
			ps.setBigDecimal(index++, fwo.getWrittenoffSchFee());
			ps.setBigDecimal(index++, fwo.getUnpaidSchFee());
			ps.setBigDecimal(index, fwo.getWriteoffSchFee());
		});

		return fwo.getFinReference();
	}

	@Override
	public long getReceiptIdByRef(String finReference) {
		String sql = "Select Max(ReceiptId) From WRITE_OFF_UPLOAD_LOG Where FinReference = ? and Event = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			Long receiptID = this.jdbcOperations.queryForObject(sql, Long.class, finReference,
					UploadTypes.WRITE_OFF.name());

			if (receiptID == null) {
				return Long.MIN_VALUE;
			}

			return (long) receiptID;
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return Long.MIN_VALUE;
		}
	}
}