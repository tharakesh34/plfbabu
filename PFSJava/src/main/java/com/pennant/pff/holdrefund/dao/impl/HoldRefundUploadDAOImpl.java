package com.pennant.pff.holdrefund.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.ReceiptUploadConstants.ReceiptDetailStatus;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.holdrefund.dao.HoldRefundUploadDAO;
import com.pennant.pff.holdrefund.model.FinanceHoldDetail;
import com.pennant.pff.holdrefund.model.HoldRefundUploadDetail;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.file.UploadContants.Status;
import com.pennanttech.pff.file.UploadTypes;

public class HoldRefundUploadDAOImpl extends SequenceDao<HoldRefundUploadDetail> implements HoldRefundUploadDAO {

	public HoldRefundUploadDAOImpl() {
		super();
	}

	@Override
	public List<HoldRefundUploadDetail> getDetails(long headerID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" HeaderId, Id, FinID, FinReference, RecordSeq");
		sql.append(", HoldStatus, Reason, Remarks, Progress, ErrorCode, ErrorDesc");
		sql.append(" From HOLD_REFUND_UPLOAD");
		sql.append(" Where HeaderId = ? and Status = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			HoldRefundUploadDetail hrud = new HoldRefundUploadDetail();

			hrud.setHeaderId(rs.getLong("HeaderId"));
			hrud.setId(rs.getLong("Id"));
			hrud.setReferenceID(JdbcUtil.getLong(rs.getObject("FinID")));
			hrud.setReference(rs.getString("FinReference"));
			hrud.setRecordSeq(rs.getLong("RecordSeq"));
			hrud.setHoldStatus(rs.getString("HoldStatus"));
			hrud.setReason(rs.getString("Reason"));
			hrud.setRemarks(rs.getString("Remarks"));
			hrud.setProgress(rs.getInt("Progress"));
			hrud.setErrorCode(rs.getString("ErrorCode"));
			hrud.setErrorDesc(rs.getString("ErrorDesc"));
			return hrud;
		}, headerID, "S");
	}

	@Override
	public List<String> isDuplicateExists(String reference, Date dueDate, long headerID) {
		StringBuilder sql = new StringBuilder("Select FileName From FILE_UPLOAD_HEADER");
		sql.append(" Where Type = ? and Id IN (");
		sql.append(" Select HeaderId From HOLD_REFUND_UPLOAD");
		sql.append(" Where FinReference = ?  and DueDate = ?");
		sql.append(" and HeaderId <> ? and Progress = ?");
		sql.append(")");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 0;

			ps.setString(++index, UploadTypes.HOLD_REFUND.name());
			ps.setString(++index, reference);
			ps.setDate(++index, JdbcUtil.getDate(dueDate));
			ps.setLong(++index, headerID);
			ps.setInt(++index, ReceiptDetailStatus.SUCCESS.getValue());

		}, (rs, roNum) -> {
			return rs.getString(1);
		});
	}

	@Override
	public void update(List<HoldRefundUploadDetail> detailsList) {
		String sql = "Update HOLD_REFUND_UPLOAD set FinID = ?, Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				HoldRefundUploadDetail detail = detailsList.get(i);

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
		String sql = "Update HOLD_REFUND_UPLOAD set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where HeaderID = ?";

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
		sql.append(" hu.FinReference, hu.HoldStatus, hu.Reason, hu.Remarks, uh.CreatedOn");
		sql.append(", hu.Status, hu.ErrorCode, hu.ErrorDesc, uh.CreatedBy, uh.ApprovedBy");
		sql.append(" From HOLD_REFUND_UPLOAD hu");
		sql.append(" Inner Join FILE_UPLOAD_HEADER uh on uh.ID = hu.HeaderID");
		sql.append(" Where uh.ID = :HEADER_ID");

		return sql.toString();
	}

	@Override
	public long save(HoldRefundUploadDetail detail) {
		StringBuilder sql = new StringBuilder("Insert into Fin_Hold_Details");
		sql.append(" (FinId, HoldStatus, Reason, Remarks");
		sql.append(") Values(?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		KeyHolder keyHolder = new GeneratedKeyHolder();

		this.jdbcOperations.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql.toString(), new String[] { "id" });

				int index = 0;

				ps.setObject(++index, detail.getReferenceID());
				ps.setString(++index, detail.getHoldStatus());
				ps.setString(++index, detail.getReason());
				ps.setString(++index, detail.getRemarks());

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
	public long saveLog(HoldRefundUploadDetail detail, FileUploadHeader header) {
		StringBuilder sql = new StringBuilder("Insert into HOLD_REFUND_UPLOAD_LOG");
		sql.append(" (FinId, FinReference, HoldStatus, Reason, Remarks, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn");
		sql.append(") Values(?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		KeyHolder keyHolder = new GeneratedKeyHolder();

		this.jdbcOperations.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql.toString(), new String[] { "id" });

				int index = 0;

				ps.setObject(++index, detail.getReferenceID());
				ps.setString(++index, detail.getReference());
				ps.setString(++index, detail.getHoldStatus());
				ps.setString(++index, detail.getReason());
				ps.setString(++index, detail.getRemarks());
				ps.setLong(++index, header.getCreatedBy());
				ps.setTimestamp(++index, header.getCreatedOn());
				ps.setLong(++index, header.getLastMntBy());
				ps.setTimestamp(++index, header.getLastMntOn());

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
	public String getHoldRefundStatus(long finId) {
		String sql = "Select HoldStatus From Fin_Hold_Details Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, String.class, finId);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public String getStatus(String finReference) {
		String sql = "Select Status From HOLD_REFUND_UPLOAD Where FinReference = ? and Status is null";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, String.class, finReference);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public int updateFinHoldDetail(HoldRefundUploadDetail detail) {
		StringBuilder sql = new StringBuilder("Update");
		sql.append(" Fin_Hold_Details");
		sql.append(" Set HoldStatus = ?, Reason = ?, Remarks = ?");
		sql.append(" Where FinId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setString(++index, detail.getHoldStatus());
			ps.setString(++index, detail.getReason());
			ps.setString(++index, detail.getRemarks());
			ps.setObject(++index, detail.getReferenceID());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		return recordCount;
	}

	@Override
	public boolean isFinIDExists(long finId) {
		StringBuilder sql = new StringBuilder("Select count(*) From Fin_Hold_Details");
		sql.append(" Where finID =:finID");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, finId) > 0;
	}

	@Override
	public FinanceHoldDetail getFinanceHoldDetails(long finID, String type, boolean isWIF) {
		logger.debug(Literal.ENTERING);

		FinanceHoldDetail financeHoldDetail = new FinanceHoldDetail();
		financeHoldDetail.setFinID(finID);
		StringBuilder sql = new StringBuilder();
		sql.append("Select FinID, HoldStatus, Reason From Fin_Hold_Details ");
		sql.append(" Where FinID =:FinID");

		logger.debug("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeHoldDetail);
		RowMapper<FinanceHoldDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(FinanceHoldDetail.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}

	}

	@Override
	public void releaseHoldOnLoans(Date closureDate) {
		StringBuilder sql = new StringBuilder("Update Fin_Hold_Details Set HoldStatus = ?");
		sql.append("  Where FinID in (");
		sql.append(" Select fm.FinId");
		sql.append(" From FinanceMain fm");
		sql.append(" Inner Join Fin_Hold_Details fh On fh.FinId = fm.FinId");
		sql.append(" Where fm.FinIsActive = ? and fh.HoldStatus = ? and fm.ClosedDate <= ?");
		sql.append(" )");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setString(++index, FinanceConstants.FEE_REFUND_RELEASE);
			ps.setBoolean(++index, false);
			ps.setString(++index, FinanceConstants.FEE_REFUND_HOLD);
			ps.setDate(++index, JdbcUtil.getDate(closureDate));
		});

	}

	@Override
	public boolean isInProgress(String finReference, long headerID) {
		StringBuilder sql = new StringBuilder("Select Count(FinReference)");
		sql.append(" From HOLD_REFUND_UPLOAD ru");
		sql.append(" Inner Join FILE_UPLOAD_HEADER uh on uh.Id = ru.HeaderID");
		sql.append(" Where ru.FinReference = ? and uh.Id <> ? and uh.progress in (?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.queryForObject(sql.toString(), Integer.class, finReference, headerID,
				Status.DOWNLOADED.getValue(), Status.IMPORT_IN_PROCESS.getValue(), Status.IMPORTED.getValue(),
				Status.IN_PROCESS.getValue()) > 0;
	}
}
