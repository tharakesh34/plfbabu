package com.pennant.pff.noc.upload.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.util.NOCConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.noc.upload.dao.CourierDetailUploadDAO;
import com.pennant.pff.noc.upload.model.CourierDetailUpload;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class CourierDetailUploadDAOImpl extends SequenceDao<CourierDetailUpload> implements CourierDetailUploadDAO {

	public CourierDetailUploadDAOImpl() {
		super();
	}

	@Override
	public List<CourierDetailUpload> getDetails(long headerID) {
		StringBuilder sql = new StringBuilder("Select Id, HeaderId, RecordSeq");
		sql.append(", FinID, FinReference, LetterType, LetterDate, DispatchDate, CourierAgency, DeliveryStatus");
		sql.append(", DeliveryDate, Progress, Status, ErrorCode, ErrorDesc");
		sql.append(" From Courier_Details_Upload");
		sql.append(" Where HeaderId = ? and Status = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), (rs, rownum) -> {
			CourierDetailUpload cd = new CourierDetailUpload();

			cd.setId(rs.getLong("Id"));
			cd.setHeaderId(rs.getLong("HeaderId"));
			cd.setRecordSeq(rs.getLong("RecordSeq"));
			cd.setReferenceID(rs.getLong("FinID"));
			cd.setReference(rs.getString("FinReference"));
			cd.setLetterType(rs.getString("LetterType"));
			cd.setLetterDate(rs.getDate("LetterDate"));
			cd.setDispatchDate(rs.getDate("DispatchDate"));
			cd.setCourierAgency(rs.getString("CourierAgency"));
			cd.setDeliveryStatus(rs.getString("DeliveryStatus"));
			cd.setDeliveryDate(rs.getDate("deliveryDate"));
			cd.setProgress(rs.getInt("Progress"));
			cd.setStatus(rs.getString("Status"));
			cd.setErrorCode(rs.getString("ErrorCode"));
			cd.setErrorDesc(rs.getString("ErrorDesc"));

			return cd;
		}, headerID, "S");
	}

	@Override
	public void update(List<CourierDetailUpload> detailsList) {
		StringBuilder sql = new StringBuilder("Update Courier_Details_Upload Set");
		sql.append(" FinID = ?, Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ?");
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				CourierDetailUpload detail = detailsList.get(i);

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
		String sql = "Update Courier_Details_Upload Set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where HeaderId = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;

				ps.setInt(++index, -1);
				ps.setString(++index, "R");
				ps.setString(++index, errorCode);
				ps.setString(++index, errorDesc);

				ps.setLong(++index, headerIds.get(i));
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
		sql.append(" lcd.FinReference, lcd.LetterType, lcd.LetterDate, lcd.DispatchDate, lcd.CourierAgency");
		sql.append(", lcd.DeliveryStatus, lcd.DeliveryDate, lcd.Status, lcd.ErrorCode, lcd.ErrorDesc");
		sql.append(", uh.CreatedBy, uh.ApprovedBy, uh.CreatedOn, uh.ApprovedOn");
		sql.append(" From Courier_Details_Upload lcd");
		sql.append(" Inner Join File_Upload_Header uh on uh.ID = lcd.HeaderId");
		sql.append(" Inner Join SecUsers su1 on su1.UsrID = uh.CreatedBy");
		sql.append(" Left Join SecUsers su2 on su2.UsrID = uh.ApprovedBy");
		sql.append(" Where uh.ID = :HEADER_ID");

		return sql.toString();
	}

	@Override
	public Long isFileExist(Date letterDate) {
		String sql = "select FinId from Loan_Letters where GeneratedDate = ? and DispatchDate is not null";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return jdbcOperations.queryForObject(sql, Long.class, letterDate);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isValidRecord(long finID, String letterType, Date letterDate) {
		String sql = "Select count(FinID) From LOAN_LETTERS Where FinID = ? and LetterType = ? and GeneratedOn = ? and ModeOfTransfer = ?";

		logger.debug(Literal.SQL.concat(sql));
		return jdbcOperations.queryForObject(sql, Integer.class, finID, letterType, letterDate,
				NOCConstants.MODE_COURIER) > 0;

	}

	@Override
	public String isValidCourierMode(long finID, String letterType, Date letterDate) {
		String sql = "Select ModeOfTransfer From LOAN_LETTERS Where FinID = ? and LetterType = ? and GeneratedOn = ?";
		logger.debug(Literal.SQL.concat(sql));
		try {
			return jdbcOperations.queryForObject(sql, String.class, finID, letterType, letterDate);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void update(CourierDetailUpload cdu, long id) {
		StringBuilder sql = new StringBuilder("Update LOAN_LETTERS");
		sql.append(" Set CourierAgency = ?, DispatchDate = ?, DeliveryStatus = ?, DeliveryDate = ?");
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 0;
				ps.setString(++index, cdu.getCourierAgency());
				ps.setDate(++index, JdbcUtil.getDate(cdu.getDispatchDate()));
				ps.setString(++index, cdu.getDeliveryStatus());
				ps.setDate(++index, JdbcUtil.getDate(cdu.getDeliveryDate()));

				ps.setLong(++index, id);

			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

}