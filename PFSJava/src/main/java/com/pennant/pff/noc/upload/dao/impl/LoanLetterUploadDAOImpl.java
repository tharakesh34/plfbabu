package com.pennant.pff.noc.upload.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.noc.upload.dao.LoanLetterUploadDAO;
import com.pennant.pff.noc.upload.model.LoanLetterUpload;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class LoanLetterUploadDAOImpl extends SequenceDao<LoanLetterUpload> implements LoanLetterUploadDAO {

	public LoanLetterUploadDAOImpl() {
		super();
	}

	@Override
	public List<LoanLetterUpload> getDetails(long headerID) {
		StringBuilder sql = new StringBuilder("Select ID, HeaderId, RecordSeq");
		sql.append(", FinID, FinReference, LetterType, ModeOfTransfer, WaiverCharges");
		sql.append(", Progress, Status, ErrorCode, ErrorDesc");
		sql.append(" From Loan_Letter_Upload");
		sql.append(" Where HeaderId = ? and Status = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			LoanLetterUpload noc = new LoanLetterUpload();

			noc.setId(rs.getLong("ID"));
			noc.setHeaderId(rs.getLong("HeaderId"));
			noc.setRecordSeq(rs.getLong("RecordSeq"));
			noc.setReferenceID(JdbcUtil.getLong(rs.getObject("FinID")));
			noc.setReference(rs.getString("FinReference"));
			noc.setLetterType(rs.getString("LetterType"));
			noc.setModeOfTransfer(rs.getString("ModeOfTransfer"));
			noc.setWaiverCharges(rs.getString("WaiverCharges"));
			noc.setProgress(rs.getInt("Progress"));
			noc.setStatus(rs.getString("Status"));
			noc.setErrorCode(rs.getString("ErrorCode"));
			noc.setErrorDesc(rs.getString("ErrorDesc"));

			return noc;
		}, headerID, "S");
	}

	@Override
	public void update(List<LoanLetterUpload> detailsList) {
		String sql = "Update Loan_Letter_Upload set  FinID = ?, Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				LoanLetterUpload detail = detailsList.get(i);

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
		String sql = "Update Loan_Letter_Upload set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where HeaderId = ?";

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
		sql.append(" noc.FinReference, noc.LetterType, noc.ModeOfTransfer, noc.WaiverCharges");
		sql.append(", noc.Progress, noc.Status, noc.ErrorCode, noc.ErrorDesc");
		sql.append(", uh.CreatedOn, su1.UsrLogin CreatedName, uh.ApprovedOn");
		sql.append(", su2.UsrLogin ApprovedName");
		sql.append(" From Loan_Letter_Upload noc");
		sql.append(" Inner Join File_Upload_Header uh on uh.Id = noc.HeaderId");
		sql.append(" Inner Join SecUsers su1 on su1.UsrID = uh.CreatedBy");
		sql.append(" Left Join SecUsers su2 on su2.UsrID = uh.ApprovedBy");
		sql.append(" Where uh.Id = :HEADER_ID");

		return sql.toString();
	}

	@Override
	public String getCanceltype(String finReference) {
		String sql = "Select CancelType From ReasonHeader Where Reference = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, String.class, finReference);
		} catch (Exception e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean getByReference(long finID, String letterType) {
		String sql = "Select count(FinID) From LOAN_LETTERS_STAGE Where FinID = ? and LetterType = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Boolean.class, finID, letterType);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return false;
	}

	@Override
	public FinTypeFees getFeeWaiverAllowed(String fintype, String finEvent) {
		String sql = "Select MaxWaiverPerc from FinTypeFees Where FinType = ? and FinEvent = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				FinTypeFees ftf = new FinTypeFees();

				ftf.setMaxWaiverPerc(rs.getBigDecimal("MaxWaiverPerc"));

				return ftf;

			}, fintype, finEvent);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isInProgress(String reference, long headerID) {
		StringBuilder sql = new StringBuilder("Select Count(llu.ID)");
		sql.append(" From Loan_Letter_Upload llu");
		sql.append(" Inner Join FILE_UPLOAD_HEADER uh on uh.Id = llu.HeaderID");
		sql.append(" Where llu.FinReference = ? and llu.Progress = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.queryForObject(sql.toString(), Integer.class, reference,
				EodConstants.PROGRESS_IN_PROCESS) > 0;
	}
}