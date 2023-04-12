package com.pennant.pff.lpp.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.model.lpp.upload.LPPUpload;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.lpp.dao.LPPUploadDAO;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class LPPUploadDAOImpl extends SequenceDao<LPPUpload> implements LPPUploadDAO {

	@Override
	public List<LPPUpload> getDetails(long headerID) {
		StringBuilder sql = new StringBuilder("Select ID, HeaderId, loanType");
		sql.append(", ApplyToExistingLoans, ApplyOverDue, FinID, FinReference");
		sql.append(", PenaltyType, IncludeGraceDays, GraceDays, CalculatedOn");
		sql.append(", AmountOrPercent, AllowWaiver, MaxWaiver, HoldStatus");
		sql.append(", Progress, Status, ErrorCode, ErrorDesc, Reason, Remarks, ODMinAmount");
		sql.append(" From LPP_UPLOAD");
		sql.append(" Where HeaderId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			LPPUpload lpp = new LPPUpload();

			lpp.setId(rs.getLong("ID"));
			lpp.setHeaderId(rs.getLong("HeaderId"));
			lpp.setReferenceID(JdbcUtil.getLong(rs.getObject("FinID")));
			lpp.setReference(rs.getString("FinReference"));
			lpp.setLoanType(rs.getString("LoanType"));
			lpp.setApplyToExistingLoans(rs.getString("ApplyToExistingLoans"));
			lpp.setApplyOverDue(rs.getString("ApplyOverDue"));
			lpp.setPenaltyType(rs.getString("PenaltyType"));
			lpp.setIncludeGraceDays(rs.getString("IncludeGraceDays"));
			lpp.setGraceDays(rs.getInt("GraceDays"));
			lpp.setCalculatedOn(rs.getString("CalculatedOn"));
			lpp.setAmountOrPercent(rs.getBigDecimal("AmountOrPercent"));
			lpp.setAllowWaiver(rs.getString("AllowWaiver"));
			lpp.setMaxWaiver(rs.getBigDecimal("MaxWaiver"));
			lpp.setHoldStatus(rs.getString("HoldStatus"));
			lpp.setProgress(rs.getInt("Progress"));
			lpp.setStatus(rs.getString("Status"));
			lpp.setErrorCode(rs.getString("ErrorCode"));
			lpp.setErrorDesc(rs.getString("ErrorDesc"));
			lpp.setODMinAmount(rs.getBigDecimal("ODMinAmount"));

			return lpp;
		}, headerID);
	}

	@Override
	public void update(List<LPPUpload> detailsList) {
		String sql = "Update LPP_UPLOAD set FinID = ?, Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ?, PickUpFlag = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				LPPUpload detail = detailsList.get(i);

				ps.setObject(++index, detail.getReferenceID());
				ps.setInt(++index, detail.getProgress());
				ps.setString(++index, (detail.getProgress() == EodConstants.PROGRESS_SUCCESS) ? "S" : "F");
				ps.setString(++index, detail.getErrorCode());
				ps.setString(++index, detail.getErrorDesc());
				ps.setInt(++index, (detail.getProgress() == EodConstants.PROGRESS_SUCCESS) ? 1 : 0);

				ps.setLong(++index, detail.getId());
			}

			@Override
			public int getBatchSize() {
				return detailsList.size();
			}
		});

	}

	@Override
	public void update(List<Long> headerIds, String errorCode, String errorDesc, int progress) {
		String sql = "Update LPP_UPLOAD set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where HeaderId = ?";

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
	public String getSqlQuery() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" lp.FinReference, lp.LoanType, lp.ApplyToExistingLoans");
		sql.append(", lp.ApplyOverDue, lp.PenaltyType, lp.IncludeGraceDays, lp.GraceDays");
		sql.append(", lp.CalculatedOn, lp.AmountOrPercent, lp.AllowWaiver, lp.MaxWaiver, lp.HoldStatus");
		sql.append(", lp.Reason, lp.Remarks, lp.Status, lp.ErrorCode, lp.ErrorDesc");
		sql.append(", uh.ApprovedOn, uh.CreatedOn");
		sql.append(", su1.UsrLogin CreatedName, su2.UsrLogin ApprovedName, lp.ODMinAmount");
		sql.append(" From LPP_Upload lp");
		sql.append(" Inner Join File_Upload_Header uh on uh.Id = lp.HeaderId");
		sql.append(" Inner Join SecUsers su1 on su1.UsrID = uh.CreatedBy");
		sql.append(" Left Join SecUsers su2 on su2.UsrID = uh.ApprovedBy");
		sql.append(" Where uh.Id = :HEADER_ID");

		return sql.toString();
	}

}
