package com.pennant.pff.bulkfeewaiverupload.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.bulkfeewaiverupload.dao.BulkFeeWaiverUploadDAO;
import com.pennanttech.model.bulkfeewaiverupload.BulkFeeWaiverUpload;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class BulkFeeWaiverUploadDAOImpl extends SequenceDao<BulkFeeWaiverUpload> implements BulkFeeWaiverUploadDAO {

	public BulkFeeWaiverUploadDAOImpl() {
		super();
	}

	@Override
	public List<BulkFeeWaiverUpload> getDetails(long headerID) {
		StringBuilder sql = new StringBuilder("Select Id, HeaderId");
		sql.append(", FinID, FinReference, RecordSeq, FeeTypeCode, WaivedAmount, Remarks, Progress, Status");
		sql.append(", ErrorCode, ErrorDesc");
		sql.append(" From FEE_WAIVER_UPLOAD");
		sql.append(" Where HeaderId = ? and Status = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), (rs, rownum) -> {
			BulkFeeWaiverUpload fw = new BulkFeeWaiverUpload();

			fw.setId(rs.getLong("Id"));
			fw.setHeaderId(rs.getLong("HeaderId"));
			fw.setReferenceID(rs.getLong("FinID"));
			fw.setReference(rs.getString("FinReference"));
			fw.setRecordSeq(rs.getLong("RecordSeq"));
			fw.setFeeTypeCode(rs.getString("FeeTypeCode"));
			fw.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			fw.setRemarks(rs.getString("Remarks"));
			fw.setProgress(rs.getInt("Progress"));
			fw.setStatus(rs.getString("Status"));
			fw.setErrorCode(rs.getString("ErrorCode"));
			fw.setErrorDesc(rs.getString("ErrorDesc"));

			return fw;
		}, headerID, "S");
	}

	@Override
	public void update(List<BulkFeeWaiverUpload> details) {
		StringBuilder sql = new StringBuilder("Update FEE_WAIVER_UPLOAD Set");
		sql.append(" FinID = ?, Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ?");
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				BulkFeeWaiverUpload detail = details.get(i);

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
	public void update(List<Long> headerIds, String errorCode, String errorDesc, int progress) {
		String sql = "Update FEE_WAIVER_UPLOAD Set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where HeaderId = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;

				ps.setInt(++index, progress);
				ps.setString(++index, (progress == EodConstants.PROGRESS_SUCCESS) ? "S" : "F");
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
		sql.append(" fw.FinReference, fw.FeeTypeCode, fw.WaivedAmount, fw.Remarks");
		sql.append(", fw.Status, fw.ErrorCode, fw.ErrorDesc");
		sql.append(", uh.CreatedOn, uh.CreatedBy, uh.ApprovedOn, uh.ApprovedBy");
		sql.append(", su1.UsrLogin CreatedName, su2.UsrLogin ApprovedName");
		sql.append(" From FEE_WAIVER_UPLOAD fw");
		sql.append(" Inner Join File_Upload_Header uh on uh.ID = fw.HeaderId");
		sql.append(" Inner Join SecUsers su1 on su1.UsrID = uh.CreatedBy");
		sql.append(" Left Join SecUsers su2 on su2.UsrID = uh.ApprovedBy");
		sql.append(" Where uh.ID = :HEADER_ID");

		return sql.toString();
	}
}
