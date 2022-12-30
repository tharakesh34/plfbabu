package com.pennant.backend.dao.payment.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.dao.payment.PaymentInstructionUploadDAO;
import com.pennant.backend.model.payment.PaymentInstUploadDetail;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class PaymentInstructionUploadDAOImpl extends SequenceDao<PaymentInstUploadDetail>
		implements PaymentInstructionUploadDAO {

	@Override
	public void update(List<Long> headerIds, String errorCode, String errorDesc, int progress) {
		String sql = "Update PAYMINS_UPLOADS set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where HeaderID = ?";

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
	public List<PaymentInstUploadDetail> getDetails(long headerID) {
		String sql = "Select HeaderId, Id, FinReference, ExcessType, FeeType, PayAmount, Remarks, OverRideOverDue, Progress, ErrorCode, ErrorDesc From PAYMINS_UPLOADS Where HeaderId = ?";

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, headerID), (rs, Num) -> {
			PaymentInstUploadDetail piud = new PaymentInstUploadDetail();

			piud.setHeaderId(rs.getLong("HeaderId"));
			piud.setId(rs.getLong("Id"));
			piud.setReference(rs.getString("FinReference"));
			piud.setExcessType(rs.getString("ExcessType"));
			piud.setFeeType(rs.getString("FeeType"));
			piud.setPayAmount(rs.getBigDecimal("PayAmount"));
			piud.setRemarks(rs.getString("Remarks"));
			piud.setOverRide(rs.getString("OverRideOverDue"));
			piud.setProgress(rs.getInt("Progress"));
			piud.setErrorCode(rs.getString("ErrorCode"));
			piud.setErrorDesc(rs.getString("ErrorDesc"));
			return piud;
		});
	}

	@Override
	public void update(List<PaymentInstUploadDetail> detailsList) {
		String sql = "Update PAYMINS_UPLOADS set Progress = ?, FinId = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				PaymentInstUploadDetail detail = detailsList.get(i);

				ps.setInt(++index, detail.getProgress());
				ps.setLong(++index, detail.getReferenceID());
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
	public void update(PaymentInstUploadDetail detail) {
		String sql = "Update PAYMINS_UPLOADS set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
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
		sql.append(" pu.FinReference,  pu.ExcessType, pu.FeeType, pu.PayAmount, ");
		sql.append(" pu.Remarks, pu.OverRideOverDue, pu.Status, pu.ErrorDesc ");
		sql.append(" From PAYMINS_UPLOADS pu");
		sql.append(" Inner Join FILE_UPLOAD_HEADER uh on uh.ID = pu.HeaderID");
		sql.append(" Where uh.ID = :HEADER_ID");

		return sql.toString();
	}

}