package com.pennant.backend.dao.receipts.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.backend.dao.receipts.CrossLoanKnockOffUploadDAO;
import com.pennant.backend.model.crossloanknockoff.CrossLoanKnockoffUpload;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class CrossLoanKnockOffUploadDAOImpl extends SequenceDao<CrossLoanKnockoffUpload>
		implements CrossLoanKnockOffUploadDAO {

	@Override
	public List<CrossLoanKnockoffUpload> loadRecordData(long id) {
		StringBuilder sql = new StringBuilder("");
		sql.append("Select Id, HeaderID, FromFinID, ToFinID, FromFinReference, TOFinReference, RecordSeq, ExcessType");
		sql.append(", ExcessAmount, AllocationType, FeeTypeCode, Progress, Status, ErrorCode, ErrorDesc");
		sql.append(" From CROSS_LOAN_KNOCKOFF_UPLOAD");
		sql.append(" Where HeaderID = ? and Status = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), (rs, rownum) -> {
			CrossLoanKnockoffUpload upload = new CrossLoanKnockoffUpload();

			upload.setReference(rs.getString("TOFinReference"));
			upload.setReferenceID(JdbcUtil.getLong(rs.getObject("ToFinID")));
			upload.setId(rs.getLong("Id"));
			upload.setHeaderId(rs.getLong("HeaderID"));
			upload.setFromFinID(JdbcUtil.getLong(rs.getObject("FromFinID")));
			upload.setToFinID(JdbcUtil.getLong(rs.getObject("ToFinID")));
			upload.setFromFinReference(rs.getString("FromFinReference"));
			upload.setToFinReference(rs.getString("TOFinReference"));
			upload.setRecordSeq(rs.getLong("RecordSeq"));
			upload.setExcessType(rs.getString("ExcessType"));
			upload.setExcessAmount(rs.getBigDecimal("ExcessAmount"));
			upload.setAllocationType(rs.getString("AllocationType"));
			upload.setFeeTypeCode(rs.getString("FeeTypeCode"));
			upload.setProgress(rs.getInt("Progress"));
			upload.setStatus(rs.getString("Status"));
			upload.setErrorCode(rs.getString("ErrorCode"));
			upload.setErrorDesc(rs.getString("ErrorDesc"));

			return upload;
		}, id, "S");
	}

	@Override
	public List<CrossLoanKnockoffUpload> getAllocations(long uploadId, long headerId) {
		StringBuilder sql = new StringBuilder("Select Feeid, UploadId, Code, Amount");
		sql.append(" From KNOCKOFF_UPLOAD_ALLOC");
		sql.append(" Where UploadId = ? and HeaderID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, uploadId);
			ps.setLong(2, headerId);
		}, (rs, rownum) -> {
			CrossLoanKnockoffUpload fc = new CrossLoanKnockoffUpload();

			fc.setId(rs.getLong("UploadId"));
			fc.setFeeId(rs.getLong("Feeid"));
			fc.setCode(rs.getString("Code"));
			fc.setAmount(rs.getBigDecimal("Amount"));

			return fc;
		});
	}

	@Override
	public void update(List<CrossLoanKnockoffUpload> details) {
		StringBuilder sql = new StringBuilder("Update CROSS_LOAN_KNOCKOFF_UPLOAD set");
		sql.append(" FromFinID = ?, ToFinID = ?, Progress = ?");
		sql.append(", Status = ?, ErrorCode = ?, ErrorDesc = ?");
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				CrossLoanKnockoffUpload detail = details.get(i);

				ps.setObject(++index, detail.getFromFinID());
				ps.setObject(++index, detail.getToFinID());
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
		String sql = "Update CROSS_LOAN_KNOCKOFF_UPLOAD set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where HeaderId = ?";

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

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" cu.Id, cu.HeaderID, cu.FromFinReference, cu.TOFinReference, cu.ExcessType");
		sql.append(", cu.ExcessAmount, cu.AllocationType, cu.FeeTypeCode, cu.Progress, cu.Status");
		sql.append(", cu.ErrorCode, cu.ErrorDesc, uh.CreatedOn, uh.ApprovedOn");
		sql.append(", su1.UsrLogin CreatedName, su2.UsrLogin ApproverName");
		sql.append(" From CROSS_LOAN_KNOCKOFF_UPLOAD cu");
		sql.append(" Inner Join File_Upload_Header uh on uh.ID = cu.HeaderID");
		sql.append(" Inner Join SecUsers su1 on su1.UsrID = uh.CreatedBy");
		sql.append(" Left Join SecUsers su2 on su2.UsrID = uh.ApprovedBy");
		sql.append(" Where uh.ID = :HEADER_ID");

		return sql.toString();
	}

	@Override
	public void saveAllocations(List<CrossLoanKnockoffUpload> details) {
		StringBuilder sql = new StringBuilder("Insert into KNOCKOFF_UPLOAD_ALLOC");
		sql.append(" (UploadId, Code, Amount, Headerid)");
		sql.append(" Values(?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				CrossLoanKnockoffUpload detail = details.get(i);

				ps.setLong(++index, detail.getId());
				ps.setString(++index, detail.getCode());
				ps.setBigDecimal(++index, detail.getAmount());
				ps.setLong(++index, detail.getHeaderId());
			}

			@Override
			public int getBatchSize() {
				return details.size();
			}
		});
	}

	@Override
	public long save(CrossLoanKnockoffUpload ck) {
		StringBuilder sql = new StringBuilder("Insert into CROSS_LOAN_KNOCKOFF_UPLOAD");
		sql.append(" (HeaderId, FromFinID, ToFinID, FromFinReference, TOFinReference, RecordSeq, ExcessType");
		sql.append(", ExcessAmount, AllocationType, FeeTypeCode)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();

			this.jdbcOperations.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql.toString(), new String[] { "id" });
					int index = 0;

					ps.setLong(++index, ck.getHeaderId());
					ps.setObject(++index, ck.getFromFinID());
					ps.setObject(++index, ck.getToFinID());
					ps.setString(++index, ck.getFromFinReference());
					ps.setString(++index, ck.getToFinReference());
					ps.setLong(++index, ck.getRecordSeq());
					ps.setString(++index, ck.getExcessType());
					ps.setBigDecimal(++index, ck.getExcessAmount());
					ps.setString(++index, ck.getAllocationType());
					ps.setString(++index, ck.getFeeTypeCode());

					return ps;
				}
			}, keyHolder);

			return keyHolder.getKey().longValue();
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}
}
