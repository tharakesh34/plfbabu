package com.pennant.pff.receipt.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.receipt.dao.ReceiptStatusUploadDAO;
import com.pennant.pff.receipt.model.ReceiptStatusUpload;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class ReceiptStatusUploadDAOImpl extends SequenceDao<ReceiptStatusUpload> implements ReceiptStatusUploadDAO {

	public ReceiptStatusUploadDAOImpl() {
		super();
	}

	@Override
	public List<ReceiptStatusUpload> getDetails(long headerID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ID, HeaderId, ReceiptId, RecordSeq, StatusRM, RealizationDate ");
		sql.append(", BounceDate, BounceReason, BounceRemarks");
		sql.append(" From Receipt_Status_UPLOAD");
		sql.append(" Where HeaderId = ? and Status = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			ReceiptStatusUpload rsu = new ReceiptStatusUpload();

			rsu.setId(rs.getLong("ID"));
			rsu.setHeaderId(rs.getLong("HeaderId"));
			rsu.setReceiptId(rs.getLong("ReceiptId"));
			rsu.setRecordSeq(rs.getLong("RecordSeq"));
			rsu.setStatusRM(rs.getString("StatusRM"));
			rsu.setRealizationDate(rs.getDate("RealizationDate"));
			rsu.setBounceDate(rs.getDate("BounceDate"));
			rsu.setBounceReason(rs.getString("BounceReason"));
			rsu.setBounceRemarks(rs.getString("BounceRemarks"));

			return rsu;
		}, headerID, "S");
	}

	@Override
	public void update(List<ReceiptStatusUpload> details) {
		String sql = "Update Receipt_Status_UPLOAD set FinID = ?, Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where ID = ?";
		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				ReceiptStatusUpload detail = details.get(i);

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
	public void update(List<Long> headerIdList, String errCode, String errDesc) {
		String sql = "Update Receipt_Status_UPLOAD set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where HeaderId = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;

				long headerID = headerIdList.get(i);

				ps.setInt(++index, -1);
				ps.setString(++index, "R");
				ps.setString(++index, errCode);
				ps.setString(++index, errDesc);

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
		sql.append(" rs.ReceiptId, rs.StatusRM, rs.RealizationDate, rs.BounceDate, rs.BounceReason");
		sql.append(" , rs.BounceRemarks, rs.Status, rs.ErrorCode, rs.ErrorDesc");
		sql.append(", uh.CreatedOn, su1.UsrLogin CreatedName, uh.ApprovedOn");
		sql.append(", su2.UsrLogin ApprovedName");
		sql.append(" From Receipt_Status_UPLOAD rs");
		sql.append(" Inner Join File_Upload_Header uh on uh.Id = rs.HeaderId");
		sql.append(" Inner Join SecUsers su1 on su1.UsrID = uh.CreatedBy");
		sql.append(" Left Join SecUsers su2 on su2.UsrID = uh.ApprovedBy");
		sql.append(" Where uh.Id = :HEADER_ID");

		return sql.toString();
	}
}