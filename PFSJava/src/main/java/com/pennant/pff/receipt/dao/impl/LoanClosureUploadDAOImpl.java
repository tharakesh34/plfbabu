package com.pennant.pff.receipt.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.backend.model.loanclosure.LoanClosureUpload;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.receipt.dao.LoanClosureUploadDAO;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.file.UploadStatus;

public class LoanClosureUploadDAOImpl extends SequenceDao<LoanClosureUpload> implements LoanClosureUploadDAO {

	public LoanClosureUploadDAOImpl() {
		super();
	}

	@Override
	public List<LoanClosureUpload> loadRecordData(long headerID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, HeaderId, RecordSeq, FinId, FinReference, Remarks");
		sql.append(", ReasonCode, Closuretype, AllocationType");
		sql.append(", Progress, Status, ErrorCode, ErrorDesc");
		sql.append(" From Loan_Closure_Upload");
		sql.append(" Where HeaderId = ? and Status = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			LoanClosureUpload lcu = new LoanClosureUpload();

			lcu.setId(rs.getLong("Id"));
			lcu.setHeaderId(rs.getLong("HeaderId"));
			lcu.setRecordSeq(rs.getLong("RecordSeq"));
			lcu.setReferenceID(JdbcUtil.getLong(rs.getObject("FinId")));
			lcu.setReference(rs.getString("FinReference"));
			lcu.setRemarks(rs.getString("Remarks"));
			lcu.setReasonCode(rs.getString("ReasonCode"));
			lcu.setClosureType(rs.getString("Closuretype"));
			lcu.setAllocationType(rs.getString("AllocationType"));
			lcu.setProgress(rs.getInt("Progress"));
			lcu.setStatus(rs.getString("Status"));
			lcu.setErrorCode(rs.getString("ErrorCode"));
			lcu.setErrorDesc(rs.getString("ErrorDesc"));

			return lcu;
		}, headerID, "S");
	}

	@Override
	public long save(LoanClosureUpload lcu) {
		StringBuilder sql = new StringBuilder("Insert into Loan_Closure_Upload");
		sql.append(" (HeaderId, RecordSeq, FinID, FinReference, Remarks, ReasonCode, Closuretype, AllocationType)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();

			this.jdbcOperations.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql.toString(), new String[] { "id" });
					int index = 0;

					ps.setLong(++index, lcu.getHeaderId());
					ps.setLong(++index, lcu.getRecordSeq());
					ps.setObject(++index, lcu.getReferenceID());
					ps.setString(++index, lcu.getReference());
					ps.setString(++index, lcu.getRemarks());
					ps.setString(++index, lcu.getReasonCode());
					ps.setString(++index, lcu.getClosureType());
					ps.setString(++index, lcu.getAllocationType());

					return ps;
				}
			}, keyHolder);

			return keyHolder.getKey().longValue();
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	public List<LoanClosureUpload> getAllocations(long uploadID, long headerID) {
		StringBuilder sql = new StringBuilder("Select UploadId, Code, Amount");
		sql.append(" From KNOCKOFF_UPLOAD_ALLOC");
		sql.append(" Where UploadId = ? and HeaderID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, uploadID);
			ps.setLong(2, headerID);
		}, (rs, rownum) -> {
			LoanClosureUpload fc = new LoanClosureUpload();

			fc.setId(rs.getLong("UploadId"));
			fc.setCode(rs.getString("Code"));
			fc.setAmount(rs.getBigDecimal("Amount"));

			return fc;
		});
	}

	@Override
	public void saveAllocations(List<LoanClosureUpload> details) {
		StringBuilder sql = new StringBuilder("Insert into KNOCKOFF_UPLOAD_ALLOC");
		sql.append(" (HeaderID, UploadId, Code, Amount)");
		sql.append(" Values(?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				LoanClosureUpload detail = details.get(i);

				ps.setLong(++index, detail.getHeaderId());
				ps.setLong(++index, detail.getId());
				ps.setString(++index, detail.getCode());
				ps.setBigDecimal(++index, detail.getAmount());
			}

			@Override
			public int getBatchSize() {
				return details.size();
			}
		});
	}

	@Override
	public void update(List<LoanClosureUpload> details) {
		String sql = "Update Loan_Closure_Upload set FinId = ?, Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				LoanClosureUpload detail = details.get(i);

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
	public void update(List<Long> headerIdList, String errorCode, String errorDesc) {
		String sql = "Update Loan_Closure_Upload set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where HeaderID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;

				long headerID = headerIdList.get(i);

				ps.setInt(++index, -1);
				ps.setString(++index, "R");
				ps.setString(++index, errorCode);
				ps.setString(++index, errorDesc);

				ps.setLong(++index, headerID);
			}

			@Override
			public int getBatchSize() {
				return headerIdList.size();
			}
		});
	}

	@Override
	public String getSqlQuery() {
		StringBuilder sql = new StringBuilder("SELECT");
		sql.append(" lcu.FinReference, lcu.Remarks, lcu.ReasonCode, lcu.Closuretype, lcu.AllocationType");
		sql.append(", lcu.Status, lcu.Progress, lcu.ErrorCode, lcu.ErrorDesc");
		sql.append(" From LOAN_CLOSURE_UPLOAD lcu");
		sql.append(" Inner Join FILE_UPLOAD_HEADER uh on uh.ID = lcu.HeaderID");
		sql.append(" Where uh.ID = :HEADER_ID");

		return sql.toString();
	}

	@Override
	public boolean getReason(String code) {
		String sql = "Select count(code) From Reasons_Aview where code = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, code) > 0;
	}

	@Override
	public boolean isInProgress(String reference, long headerID) {
		StringBuilder sql = new StringBuilder("Select Count(ID)");
		sql.append(" From Loan_Closure_Upload cu");
		sql.append(" Inner Join FILE_UPLOAD_HEADER uh on uh.Id = cu.HeaderID");
		sql.append(" Where cu.Reference = ? and uh.Id <> ? and uh.progress in (?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.queryForObject(sql.toString(), Integer.class, reference, headerID,
				UploadStatus.DOWNLOADED.status(), UploadStatus.IN_PROCESS.status(), UploadStatus.IMPORTED.status()) > 0;
	}

}
