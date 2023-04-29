package com.pennant.pff.receipt.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.model.LoanClosure;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.receipt.dao.LoanClosureUploadDAO;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.file.UploadContants.Status;

public class LoanClosureUploadDAOImpl extends SequenceDao<LoanClosure> implements LoanClosureUploadDAO {

	@Override
	public List<LoanClosure> loadRecordData(long headerID) {

		String sql = getQuery();

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql, ps -> ps.setLong(1, headerID), (rs, rowNum) -> {
			LoanClosure lc = new LoanClosure();

			lc.setId(rs.getLong("Id"));
			lc.setHeaderId(rs.getLong("HeaderId"));
			lc.setReferenceID(JdbcUtil.getLong(rs.getObject("FinId")));
			lc.setReference(rs.getString("Reference"));
			lc.setRemarks(rs.getString("Remarks"));
			lc.setReasonCode(JdbcUtil.getLong(rs.getObject("ReasonCode")));
			lc.setClosureType(rs.getString("Closuretype"));
			lc.setStatus(rs.getString("Status"));
			lc.setProgress(rs.getInt("Progress"));
			lc.setErrorCode(rs.getString("ErrorCode"));
			lc.setErrorDesc(rs.getString("ErrorDesc"));

			return lc;
		});
	}

	private String getQuery() {
		String sql = "Select Id, HeaderId, FinId, Reference, Remarks, ReasonCode, Closuretype, Progress, Status, ErrorCode, ErrorDesc From Loan_Closure_Upload Where HeaderId = ?";
		return sql;
	}

	@Override
	public void update(List<LoanClosure> details) {
		String sql = "Update Loan_Closure_Upload set  Reference = ?, FinId = ?, Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				LoanClosure detail = details.get(i);

				ps.setObject(++index, detail.getReference());
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
	public void update(List<Long> headerIdList, String errorCode, String errorDesc, int progressFailed) {
		String sql = "Update Loan_Closure_Upload set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where HeaderID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;

				long headerID = headerIdList.get(i);

				ps.setInt(++index, progressFailed);
				ps.setString(++index, (progressFailed == EodConstants.PROGRESS_SUCCESS) ? "S" : "F");
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(LoanClosure detail) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isInProgress(String reference, long headerID) {
		StringBuilder sql = new StringBuilder("Select Count(Reference)");
		sql.append(" From Loan_Closure_Upload cu");
		sql.append(" Inner Join FILE_UPLOAD_HEADER uh on uh.Id = cu.HeaderID");
		sql.append(" Where cu.Reference = ? and uh.Id <> ? and uh.progress in (?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.queryForObject(sql.toString(), Integer.class, reference, headerID,
				Status.DOWNLOADED.getValue(), Status.IMPORT_IN_PROCESS.getValue(), Status.IMPORTED.getValue(),
				Status.IN_PROCESS.getValue()) > 0;
	}

	@Override
	public boolean isInMaintanance(String reference) {
		String sql = "Select Count(Reference) From Customers_Temp where CustCif = ?";

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, Integer.class, reference) > 0;
	}

}
