package com.pennant.pff.holdmarking.upload.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.holdmarking.upload.dao.HoldMarkingUploadDAO;
import com.pennant.pff.holdmarking.upload.model.HoldMarkingUpload;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class HoldMarkingUploadDAOImpl extends SequenceDao<HoldMarkingUpload> implements HoldMarkingUploadDAO {

	public HoldMarkingUploadDAOImpl() {
		super();
	}

	@Override
	public List<HoldMarkingUpload> getDetails(long headerID) {
		StringBuilder sql = new StringBuilder("Select ID, HeaderId, RecordSeq");
		sql.append(", FinID, Type, AccountNumber, Amount, Reference, Remarks");
		sql.append(", Progress, Status, ErrorCode, ErrorDesc");
		sql.append(" From Hold_Marking_Upload");
		sql.append(" Where HeaderId = ? and Status = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			HoldMarkingUpload hm = new HoldMarkingUpload();

			hm.setId(rs.getLong("ID"));
			hm.setHeaderId(rs.getLong("HeaderId"));
			hm.setRecordSeq(rs.getLong("RecordSeq"));
			hm.setReferenceID(JdbcUtil.getLong(rs.getObject("FinID")));
			hm.setType(rs.getString("Type"));
			hm.setAccountNumber(rs.getString("AccountNumber"));
			hm.setAmount(rs.getBigDecimal("Amount"));
			hm.setReference(rs.getString("Reference"));
			hm.setRemarks(rs.getString("Remarks"));
			hm.setProgress(rs.getInt("Progress"));
			hm.setStatus(rs.getString("Status"));
			hm.setErrorCode(rs.getString("ErrorCode"));
			hm.setErrorDesc(rs.getString("ErrorDesc"));

			return hm;
		}, headerID, "S");
	}

	@Override
	public void update(List<HoldMarkingUpload> details) {
		String sql = "Update Hold_Marking_Upload set FinID = ?, Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;

				HoldMarkingUpload detail = details.get(i);

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
	public void update(List<Long> headerIds, String errorCode, String errorDesc) {
		String sql = "Update Hold_Marking_Upload set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where HeaderId = ?";

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
		sql.append(" hm.Type, hm.AccountNumber, hm.Amount, hm.Reference, hm.Remarks, hm.Status");
		sql.append(", hm.ErrorCode, hm.ErrorDesc, uh.ApprovedOn, uh.CreatedOn");
		sql.append(", su1.UsrLogin CreatedName, su2.UsrLogin ApprovedName");
		sql.append("  From Hold_Marking_Upload hm");
		sql.append("  Inner Join File_Upload_Header uh on uh.Id = hm.HeaderId");
		sql.append("  Inner Join SecUsers su1 on su1.UsrID = uh.CreatedBy");
		sql.append("  Left Join SecUsers su2 on su2.UsrID = uh.ApprovedBy");
		sql.append("  Where uh.Id = :HEADER_ID");

		return sql.toString();
	}

	@Override
	public boolean isValidateType(long finId, String accountNumber) {
		String sql = "Select count(ID) From Hold_Marking_Header Where FinId = ? and AccountNumber  = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, finId, accountNumber) > 0;
	}

	@Override
	public int getReference(String reference, String accountNumber, int progressSuccess) {
		String sql = "Select count(ID) From Hold_Marking_Upload Where Reference = ? and AccountNumber  = ? and Progress = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, reference, accountNumber, progressSuccess);
	}

	@Override
	public void delete(String reference, String accountNumber, int progressSuccess) {
		String sql = "Delete from Hold_Marking_Upload where Reference = ? and AccountNumber  = ? and Progress = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, reference, accountNumber, progressSuccess);
	}

	@Override
	public long save(HoldMarkingUpload hm) {
		StringBuilder sql = new StringBuilder("Insert into Hold_Marking_Upload_Log");
		sql.append(" (ID, HeaderId, FinID, Type, AccountNumber, Amount, Reference, Progress, Remarks, Status");
		sql.append(", ErrorCode, ErrorDesc, CreatedOn, CreatedBy, ApprovedOn, ApprovedBy)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, hm.getId());
			ps.setLong(++index, hm.getHeaderId());
			ps.setObject(++index, hm.getReferenceID());
			ps.setString(++index, hm.getType());
			ps.setString(++index, hm.getAccountNumber());
			ps.setBigDecimal(++index, hm.getAmount());
			ps.setString(++index, hm.getReference());
			ps.setInt(++index, hm.getProgress());
			ps.setString(++index, hm.getRemarks());
			ps.setString(++index, hm.getStatus());
			ps.setString(++index, hm.getErrorCode());
			ps.setString(++index, hm.getErrorDesc());
			ps.setTimestamp(++index, hm.getCreatedOn());
			ps.setLong(++index, hm.getCreatedBy());
			ps.setTimestamp(++index, hm.getApprovedOn());
			ps.setLong(++index, hm.getApprovedBy());
		});

		return hm.getId();
	}
}