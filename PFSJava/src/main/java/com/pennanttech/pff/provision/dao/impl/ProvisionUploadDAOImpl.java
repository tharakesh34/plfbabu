package com.pennanttech.pff.provision.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.provision.dao.ProvisionUploadDAO;
import com.pennanttech.pff.provision.model.ProvisionUpload;

public class ProvisionUploadDAOImpl extends SequenceDao<ProvisionUpload> implements ProvisionUploadDAO {

	@Override
	public List<ProvisionUpload> getDetails(long headerID) {
		StringBuilder sql = new StringBuilder("Select ID, HeaderId, RecordSeq");
		sql.append(", FinID, FinReference, AssetClassCode");
		sql.append(", AssetSubClassCode, OverrideProvision, ProvisionPercentage");
		sql.append(", Progress, Status, ErrorCode, ErrorDesc");
		sql.append(" From Provision_Upload");
		sql.append(" Where HeaderId = ? and Status = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			ProvisionUpload pu = new ProvisionUpload();

			pu.setId(rs.getLong("ID"));
			pu.setHeaderId(rs.getLong("HeaderId"));
			pu.setRecordSeq(rs.getLong("RecordSeq"));
			pu.setReferenceID(JdbcUtil.getLong(rs.getObject("FinID")));
			pu.setReference(rs.getString("FinReference"));
			pu.setAssetClassCode(rs.getString("AssetClassCode"));
			pu.setAssetSubClassCode(rs.getString("AssetSubClassCode"));
			pu.setOverrideProvision(rs.getString("OverrideProvision"));
			pu.setProvisionPercentage(rs.getBigDecimal("ProvisionPercentage"));
			pu.setProgress(rs.getInt("Progress"));
			pu.setStatus(rs.getString("Status"));
			pu.setErrorCode(rs.getString("ErrorCode"));
			pu.setErrorDesc(rs.getString("ErrorDesc"));

			return pu;
		}, headerID, "S");
	}

	@Override
	public void update(List<ProvisionUpload> details) {
		String sql = "Update Provision_Upload set  Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				ProvisionUpload detail = details.get(i);

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
		String sql = "Update Provision_Upload set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where HeaderId = ?";

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
		sql.append("  mp.FinReference, mp.AssetClassCode, mp.AssetSubClassCode");
		sql.append(", mp.OverrideProvision, mp.ProvisionPercentage, mp.Status");
		sql.append(", mp.ErrorCode, mp.ErrorDesc, uh.ApprovedOn, uh.CreatedOn");
		sql.append(", su1.UsrLogin CreatedName, su2.UsrLogin ApprovedName");
		sql.append("  From Provision_Upload mp");
		sql.append("  Inner Join File_Upload_Header uh on uh.Id = mp.HeaderId");
		sql.append("  Inner Join SecUsers su1 on su1.UsrID = uh.CreatedBy");
		sql.append("  Left Join SecUsers su2 on su2.UsrID = uh.ApprovedBy");
		sql.append("  Where uh.Id = :HEADER_ID");

		return sql.toString();
	}

	@Override
	public Long getAssetClassId(String assetClassCode) {
		String sql = "Select Id From Asset_Class_Codes Where Code = ?";

		logger.debug(sql);

		try {
			return jdbcOperations.queryForObject(sql, Long.class, assetClassCode);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}

	@Override
	public Long getAssetSubClassId(long assetClassId, String assetSubClassCode) {
		String sql = "Select Id From Asset_Sub_Class_Codes Where Code = ? and AssetClassId = ?";

		logger.debug(sql);

		try {
			return jdbcOperations.queryForObject(sql, Long.class, assetSubClassCode, assetClassId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}
}