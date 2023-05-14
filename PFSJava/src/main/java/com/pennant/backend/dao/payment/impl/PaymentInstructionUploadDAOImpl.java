package com.pennant.backend.dao.payment.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.dao.payment.PaymentInstructionUploadDAO;
import com.pennant.backend.model.payment.PaymentInstUploadDetail;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class PaymentInstructionUploadDAOImpl extends SequenceDao<PaymentInstUploadDetail>
		implements PaymentInstructionUploadDAO {

	@Override
	public void update(List<Long> headerIds, String errorCode, String errorDesc) {
		String sql = "Update PAYMINS_UPLOAD set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where HeaderID = ?";

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
	public List<PaymentInstUploadDetail> getDetails(long headerID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" HeaderId, Id, FinID, FinReference, RecordSeq, ExcessType");
		sql.append(", FeeType, PayAmount, Remarks, OverRideOverDue, Progress, Status, ErrorCode, ErrorDesc");
		sql.append(" From PAYMINS_UPLOAD");
		sql.append(" Where HeaderId = ? and Status = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), (rs, rownum) -> {
			PaymentInstUploadDetail piud = new PaymentInstUploadDetail();

			piud.setHeaderId(rs.getLong("HeaderId"));
			piud.setId(rs.getLong("Id"));
			piud.setReferenceID(JdbcUtil.getLong(rs.getObject("FinID")));
			piud.setReference(rs.getString("FinReference"));
			piud.setRecordSeq(rs.getLong("RecordSeq"));
			piud.setExcessType(rs.getString("ExcessType"));
			piud.setFeeType(rs.getString("FeeType"));
			piud.setPayAmount(rs.getBigDecimal("PayAmount"));
			piud.setRemarks(rs.getString("Remarks"));
			piud.setOverRide(rs.getString("OverRideOverDue"));
			piud.setProgress(rs.getInt("Progress"));
			piud.setStatus(rs.getString("Status"));
			piud.setErrorCode(rs.getString("ErrorCode"));
			piud.setErrorDesc(rs.getString("ErrorDesc"));
			return piud;
		}, headerID, "S");
	}

	@Override
	public PaymentInstUploadDetail getDetails(long headerID, long detailId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" HeaderId, Id, FinReference, FinId, ExcessType, FeeType, PayAmount, Remarks");
		sql.append(", OverRideOverDue, Progress, ErrorCode, ErrorDesc");
		sql.append(" From PAYMINS_UPLOAD");
		sql.append(" Where HeaderId = ? and id = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
			PaymentInstUploadDetail piud = new PaymentInstUploadDetail();

			piud.setHeaderId(rs.getLong("HeaderId"));
			piud.setId(rs.getLong("Id"));
			piud.setReference(rs.getString("FinReference"));
			piud.setReferenceID(rs.getLong("FinId"));
			piud.setExcessType(rs.getString("ExcessType"));
			piud.setFeeType(rs.getString("FeeType"));
			piud.setPayAmount(rs.getBigDecimal("PayAmount"));
			piud.setRemarks(rs.getString("Remarks"));
			piud.setOverRide(rs.getString("OverRideOverDue"));
			piud.setProgress(rs.getInt("Progress"));
			piud.setErrorCode(rs.getString("ErrorCode"));
			piud.setErrorDesc(rs.getString("ErrorDesc"));
			return piud;
		}, headerID, detailId);
	}

	@Override
	public void update(List<PaymentInstUploadDetail> detailsList) {
		String sql = "Update PAYMINS_UPLOAD set Progress = ?, FinId = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				PaymentInstUploadDetail detail = detailsList.get(i);

				ps.setInt(++index, detail.getProgress());
				ps.setObject(++index, detail.getReferenceID());
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
		String sql = "Update PAYMINS_UPLOAD set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where ID = ?";

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
		sql.append(" pu.FinReference, pu.ExcessType, pu.FeeType, pu.PayAmount");
		sql.append(", pu.Remarks, pu.OverRideOverDue, pu.Status, pu.ErrorDesc");
		sql.append(" From PAYMINS_UPLOAD pu");
		sql.append(" Inner Join FILE_UPLOAD_HEADER uh on uh.ID = pu.HeaderID");
		sql.append(" Where uh.ID = :HEADER_ID");

		return sql.toString();
	}

	@Override
	public List<Integer> getHeaderStatusCnt(long uploadId) {
		String sql = "Select Progress From PAYMINS_UPLOAD Where HeaderId = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.query(sql, ps -> ps.setLong(1, uploadId), (rs, rowNum) -> {
			if (rs.getString("Progress") == null)
				return null;

			return rs.getInt("Progress");
		});
	}

	@Override
	public void uploadHeaderStatusCnt(FileUploadHeader header) {
		StringBuilder sql = new StringBuilder("Update FILE_UPLOAD_HEADER");
		sql.append(" Set TotalRecords = ?, SuccessRecords = ?, FailureRecords = ?");
		sql.append(", Progress = ?, Remarks = Remarks + ?");
		sql.append(", ApprovedBy = ?, ApprovedOn = ?");
		sql.append(", Version = Version + ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, header.getTotalRecords());
			ps.setLong(++index, header.getSuccessRecords());
			ps.setLong(++index, header.getFailureRecords());
			ps.setInt(++index, header.getProgress());
			ps.setString(++index, header.getRemarks());
			ps.setObject(++index, header.getApprovedBy());
			ps.setTimestamp(++index, header.getApprovedOn());
			ps.setInt(++index, 1);
			ps.setLong(++index, header.getLastMntBy());
			ps.setTimestamp(++index, header.getLastMntOn());
			ps.setString(++index, header.getRecordStatus());
			ps.setString(++index, header.getRoleCode());
			ps.setString(++index, header.getNextRoleCode());
			ps.setString(++index, header.getTaskId());
			ps.setString(++index, header.getNextTaskId());
			ps.setString(++index, header.getRecordType());
			ps.setLong(++index, header.getWorkflowId());
			ps.setLong(++index, header.getId());
		});
	}
}