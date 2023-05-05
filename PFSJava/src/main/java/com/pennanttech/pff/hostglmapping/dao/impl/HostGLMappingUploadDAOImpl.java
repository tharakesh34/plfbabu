package com.pennanttech.pff.hostglmapping.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.model.hostglmapping.upload.HostGLMappingUpload;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.hostglmapping.dao.HostGLMappingUploadDAO;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class HostGLMappingUploadDAOImpl extends SequenceDao<HostGLMappingUpload> implements HostGLMappingUploadDAO {

	@Override
	public List<HostGLMappingUpload> getDetails(long headerID) {
		StringBuilder sql = new StringBuilder("Select ID, HeaderId, RecordSeq");
		sql.append(", HostGLCode, AccountType, LoanType, CostCentreCode");
		sql.append(", ProfitCentreCode, OpenedDate, AllowManualEntries, GLDescription");
		sql.append(", Progress, Status, ErrorCode, ErrorDesc");
		sql.append(" From HOST_GL_UPLOAD");
		sql.append(" Where HeaderId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			HostGLMappingUpload hg = new HostGLMappingUpload();

			hg.setId(rs.getLong("ID"));
			hg.setHeaderId(rs.getLong("HeaderId"));
			hg.setRecordSeq(rs.getLong("RecordSeq"));
			hg.setHostGLCode(rs.getString("HostGLCode"));
			hg.setAccountType(rs.getString("AccountType"));
			hg.setLoanType(rs.getString("LoanType"));
			hg.setCostCentreCode(rs.getString("CostCentreCode"));
			hg.setProfitCentreCode(rs.getString("ProfitCentreCode"));
			hg.setOpenedDate(rs.getDate("OpenedDate"));
			hg.setAllowManualEntries(rs.getString("AllowManualEntries"));
			hg.setGLDescription(rs.getString("GLDescription"));
			hg.setProgress(rs.getInt("Progress"));
			hg.setStatus(rs.getString("Status"));
			hg.setErrorCode(rs.getString("ErrorCode"));
			hg.setErrorDesc(rs.getString("ErrorDesc"));

			return hg;
		}, headerID);
	}

	@Override
	public void update(List<HostGLMappingUpload> detailsList) {
		String sql = "Update HOST_GL_UPLOAD set  Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				HostGLMappingUpload detail = detailsList.get(i);

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
	public void update(List<Long> headerIds, String errorCode, String errorDesc, int progress) {
		String sql = "Update HOST_GL_UPLOAD set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where HeaderId = ?";

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
		sql.append("  hg.HostGLCode, hg.AccountType, hg.LoanType, hg.CostCentreCode");
		sql.append(", hg.ProfitCentreCode, hg.OpenedDate, hg.AllowManualEntries, hg.GLDescription, hg.Status");
		sql.append(", hg.ErrorCode, hg.ErrorDesc, uh.ApprovedOn, uh.CreatedOn");
		sql.append(", su1.UsrLogin CreatedName, su2.UsrLogin ApprovedName");
		sql.append("  From HOST_GL_UPLOAD hg");
		sql.append("  Inner Join File_Upload_Header uh on uh.Id = hg.HeaderId");
		sql.append("  Inner Join SecUsers su1 on su1.UsrID = uh.CreatedBy");
		sql.append("  Left Join SecUsers su2 on su2.UsrID = uh.ApprovedBy");
		sql.append("  Where uh.Id = :HEADER_ID");

		return sql.toString();
	}

	@Override
	public boolean isDuplicateKey(String glcode, TableType tableType) {
		String sql;
		String whereClause = "Account = ?";

		Object[] args = new Object[] { glcode };

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("AccountMapping", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("AccountMapping_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "AccountMapping_Temp", "AccountMapping" }, whereClause);

			args = new Object[] { glcode, glcode };

			break;
		}

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, Integer.class, args) > 0;
	}

}
