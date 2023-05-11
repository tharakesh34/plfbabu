package com.pennant.pff.manualknockoff.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.manualknockoff.dao.ManualKnockOffUploadDAO;
import com.pennanttech.model.knockoff.ManualKnockOffUpload;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.file.UploadStatus;

public class ManualKnockOffUploadDAOImpl extends SequenceDao<ManualKnockOffUpload> implements ManualKnockOffUploadDAO {

	public ManualKnockOffUploadDAOImpl() {
		super();
	}

	@Override
	public List<ManualKnockOffUpload> getDetails(long headerID) {
		StringBuilder sql = new StringBuilder("Select Id, HeaderId");
		sql.append(", FinID, FinReference, RecordSeq, ExcessType, AllocationType, ReceiptAmount, FeeTypeCode");
		sql.append(", Progress, Status, ErrorCode, ErrorDesc");
		sql.append(" From MANUAL_KNOCKOFF_UPLOAD");
		sql.append(" Where HeaderId = ? and Status = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			ManualKnockOffUpload fc = new ManualKnockOffUpload();

			fc.setId(rs.getLong("Id"));
			fc.setHeaderId(rs.getLong("HeaderId"));
			fc.setReferenceID(JdbcUtil.getLong(rs.getObject("FinID")));
			fc.setReference(rs.getString("FinReference"));
			fc.setRecordSeq(rs.getLong("RecordSeq"));
			fc.setExcessType(rs.getString("ExcessType"));
			fc.setAllocationType(rs.getString("AllocationType"));
			fc.setReceiptAmount(rs.getBigDecimal("ReceiptAmount"));
			fc.setFeeTypeCode((rs.getString("FeeTypeCode")));
			fc.setProgress(rs.getInt("Progress"));
			fc.setStatus(rs.getString("Status"));
			fc.setErrorCode(rs.getString("ErrorCode"));
			fc.setErrorDesc(rs.getString("ErrorDesc"));

			return fc;
		}, headerID, "S");
	}

	public List<ManualKnockOffUpload> getAllocations(long uploadID, long headerID) {
		StringBuilder sql = new StringBuilder("Select UploadId, Code, Amount");
		sql.append(" From KNOCKOFF_UPLOAD_ALLOC");
		sql.append(" Where UploadId = ? and HeaderID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, uploadID);
			ps.setLong(2, headerID);
		}, (rs, rownum) -> {
			ManualKnockOffUpload fc = new ManualKnockOffUpload();

			fc.setId(rs.getLong("UploadId"));
			fc.setCode(rs.getString("Code"));
			fc.setAmount(rs.getBigDecimal("Amount"));

			return fc;
		});
	}

	@Override
	public long save(ManualKnockOffUpload mk) {
		StringBuilder sql = new StringBuilder("Insert into MANUAL_KNOCKOFF_UPLOAD");
		sql.append(" (HeaderId, FinID, FinReference, RecordSeq, ExcessType");
		sql.append(", AllocationType, ReceiptAmount, FeeTypeCode)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();

			this.jdbcOperations.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql.toString(), new String[] { "id" });
					int index = 0;

					ps.setLong(++index, mk.getHeaderId());
					ps.setObject(++index, mk.getReferenceID());
					ps.setString(++index, mk.getReference());
					ps.setLong(++index, mk.getRecordSeq());
					ps.setString(++index, mk.getExcessType());
					ps.setString(++index, mk.getAllocationType());
					ps.setBigDecimal(++index, mk.getReceiptAmount());
					ps.setString(++index, mk.getFeeTypeCode());

					return ps;
				}
			}, keyHolder);

			return keyHolder.getKey().longValue();
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public void saveAllocations(List<ManualKnockOffUpload> details) {
		StringBuilder sql = new StringBuilder("Insert into KNOCKOFF_UPLOAD_ALLOC");
		sql.append(" (HeaderID, UploadId, Code, Amount)");
		sql.append(" Values(?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				ManualKnockOffUpload detail = details.get(i);

				ps.setLong(++index, detail.getHeaderId());
				ps.setLong(++index, detail.getId());
				ps.setString(++index, detail.getCode());
				ps.setBigDecimal(++index, detail.getAmount());
			}

			@Override
			public int getBatchSize() {
				return details.size();
			}
		});
	}

	@Override
	public void update(List<ManualKnockOffUpload> details) {
		StringBuilder sql = new StringBuilder("Update MANUAL_KNOCKOFF_UPLOAD set");
		sql.append(" FinID = ?, ReceiptID = ?, Progress = ?");
		sql.append(", Status = ?, ErrorCode = ?, ErrorDesc = ?");
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				ManualKnockOffUpload detail = details.get(i);

				ps.setObject(++index, detail.getReferenceID());
				ps.setObject(++index, detail.getReceiptID());
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
		String sql = "Update MANUAL_KNOCKOFF_UPLOAD set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where HeaderId = ?";

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
		sql.append(" mk.FinReference, mk.ExcessType, mk.AllocationType, mk.ReceiptAmount, mk.FeeTypeCode");
		sql.append(", mk.Progress, mk.Status, mk.ErrorCode, mk.ErrorDesc");
		sql.append(", uh.CreatedOn, uh.CreatedBy, uh.ApprovedOn, uh.ApprovedBy");
		sql.append(", su1.UsrLogin CreatedName, su2.UsrLogin ApprovedName");
		sql.append(" From MANUAL_KNOCKOFF_UPLOAD mk");
		sql.append(" Inner Join File_Upload_Header uh on uh.ID = mk.HeaderId");
		sql.append(" Inner Join SecUsers su1 on su1.UsrID = uh.CreatedBy");
		sql.append(" Left Join SecUsers su2 on su2.UsrID = uh.ApprovedBy");
		sql.append(" Where uh.ID = :HEADER_ID");

		return sql.toString();
	}

	@Override
	public boolean isInProgress(long headerID, String reference) {
		StringBuilder sql = new StringBuilder("Select Count(mku.ID)");
		sql.append(" From MANUAL_KNOCKOFF_UPLOAD mku");
		sql.append(" Inner Join FILE_UPLOAD_HEADER uh on uh.Id = mku.HeaderID");
		sql.append(" Where mku.FinReference = ? and uh.Id <> ? and uh.progress not in (?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.queryForObject(sql.toString(), Integer.class, reference, headerID,
				UploadStatus.APPROVED.status(), UploadStatus.FAILED.status(), UploadStatus.REJECTED.status()) > 0;
	}

}
