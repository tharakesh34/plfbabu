package com.pennanttech.pff.branchchange.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.model.branchchange.upload.BranchChangeUpload;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.branchchange.dao.BranchChangeUploadDAO;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class BranchChangeUploadDAOImpl extends SequenceDao<BranchChangeUpload> implements BranchChangeUploadDAO {

	@Override
	public List<BranchChangeUpload> getDetails(long headerID) {
		StringBuilder sql = new StringBuilder("Select ID, HeaderId");
		sql.append(", FinID, FinReference, RecordSeq, BranchCode, Remarks");
		sql.append(", Progress, Status, ErrorCode, ErrorDesc");
		sql.append(" From BRANCH_CHANGE_UPLOAD");
		sql.append(" Where HeaderId = ? and Status = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			BranchChangeUpload bc = new BranchChangeUpload();

			bc.setId(rs.getLong("ID"));
			bc.setHeaderId(rs.getLong("HeaderId"));
			bc.setReferenceID(JdbcUtil.getLong(rs.getObject("FinID")));
			bc.setReference(rs.getString("FinReference"));
			bc.setRecordSeq(rs.getLong("RecordSeq"));
			bc.setBranchCode(rs.getString("BranchCode"));
			bc.setRemarks(rs.getString("Remarks"));
			bc.setProgress(rs.getInt("Progress"));
			bc.setStatus(rs.getString("Status"));
			bc.setErrorCode(rs.getString("ErrorCode"));
			bc.setErrorDesc(rs.getString("ErrorDesc"));

			return bc;
		}, headerID, "S");
	}

	@Override
	public void update(List<BranchChangeUpload> detailsList) {
		String sql = "Update BRANCH_CHANGE_UPLOAD set FinID = ?, Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				BranchChangeUpload detail = detailsList.get(i);

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
		String sql = "Update BRANCH_CHANGE_UPLOAD set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where HeaderId = ?";

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
		sql.append(" bc.FinReference, bc.BranchCode, bc.Remarks");
		sql.append(", bc.Status, bc.ErrorCode, bc.ErrorDesc");
		sql.append(", uh.ApprovedOn, uh.CreatedOn");
		sql.append(", su1.UsrLogin CreatedName, su2.UsrLogin ApprovedName");
		sql.append(" From BRANCH_CHANGE_UPLOAD bc");
		sql.append(" Inner Join File_Upload_Header uh on uh.Id = bc.HeaderId");
		sql.append(" Inner Join SecUsers su1 on su1.UsrID = uh.CreatedBy");
		sql.append(" Left Join SecUsers su2 on su2.UsrID = uh.ApprovedBy");
		sql.append(" Where uh.Id = :HEADER_ID");

		return sql.toString();
	}

	@Override
	public boolean isInSettlement(long finID, String type) {

		StringBuilder sql = new StringBuilder("Select count(FinID) From Fin_Settlement_Header");
		sql.append(type);
		sql.append(" Where FinID = ?");

		logger.debug(sql.toString());

		return jdbcOperations.queryForObject(sql.toString(), Integer.class, finID) > 0;
	}

	@Override
	public boolean isInlinkingDelinking(long finID, String type) {

		StringBuilder sql = new StringBuilder("Select count(FinID) From LinkedFinances");
		sql.append(type);
		sql.append(" Where FinID = ?");

		logger.debug(sql.toString());

		return jdbcOperations.queryForObject(sql.toString(), Integer.class, finID) > 0;
	}

	@Override
	public boolean getReceiptQueueList(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" count(Reference) From FinReceiptHeader_Temp fr");
		sql.append(" Inner Join FinanceMain fm on fm.Finreference = fr.Reference");
		sql.append(" Where fm.FinID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.queryForObject(sql.toString(), Integer.class, finID) > 0;
	}
}