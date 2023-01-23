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

public class ManualKnockOffUploadDAOImpl extends SequenceDao<ManualKnockOffUpload> implements ManualKnockOffUploadDAO {

	public ManualKnockOffUploadDAOImpl() {
		super();
	}

	@Override
	public List<ManualKnockOffUpload> getDetails(long headerID) {
		StringBuilder sql = new StringBuilder("Select Id, HeaderId");
		sql.append(", FinID, FinReference, ExcessType, AllocationType, ReceiptAmount, AdviseID");
		sql.append(", Progress, Status, ErrorCode, ErrorDesc");
		sql.append(" From MANUAL_KNOCKOFF_UPLOAD");
		sql.append(" Where HeaderId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, headerID);
		}, (rs, rownum) -> {
			ManualKnockOffUpload fc = new ManualKnockOffUpload();

			fc.setId(rs.getLong("Id"));
			fc.setHeaderId(rs.getLong("HeaderId"));
			fc.setReferenceID(JdbcUtil.getLong(rs.getObject("FinID")));
			fc.setReference(rs.getString("FinReference"));
			fc.setExcessType(rs.getString("ExcessType"));
			fc.setAllocationType(rs.getString("AllocationType"));
			fc.setReceiptAmount(rs.getBigDecimal("ReceiptAmount"));
			fc.setAdviseId(JdbcUtil.getLong(rs.getObject("AdviseID")));
			fc.setProgress(rs.getInt("Progress"));
			fc.setStatus(rs.getString("Status"));
			fc.setErrorCode(rs.getString("ErrorCode"));
			fc.setErrorDesc(rs.getString("ErrorDesc"));

			return fc;
		});
	}

	public List<ManualKnockOffUpload> getAllocations(long uploadId) {
		StringBuilder sql = new StringBuilder("Select UploadId, Code, Amount");
		sql.append(" From MANUAL_KNOCKOFF_UPLOAD_ALLOC");
		sql.append(" Where UploadId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, uploadId);
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
		sql.append(" (HeaderId, FinID, FinReference, ExcessType, AllocationType, ReceiptAmount, AdviseID)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?)");

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
					ps.setString(++index, mk.getExcessType());
					ps.setString(++index, mk.getAllocationType());
					ps.setBigDecimal(++index, mk.getReceiptAmount());
					ps.setObject(++index, mk.getAdviseId());

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
		StringBuilder sql = new StringBuilder("Insert into MANUAL_KNOCKOFF_UPLOAD_ALLOC");
		sql.append(" (UploadId, Code, Amount)");
		sql.append(" Values(?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				ManualKnockOffUpload detail = details.get(i);

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
		sql.append(" FinID = ?, Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ?");
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				ManualKnockOffUpload detail = details.get(i);

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
		sql.append(" mk.FinReference, mk.ExcessType, mk.AllocationType, mk.ReceiptAmount, mk.AdviseID");
		sql.append(", mk.Progress, mk.Status, mk.ErrorCode, mk.ErrorDesc");
		sql.append(", uh.CreatedOn, uh.CreatedBy, uh.ApprovedOn, uh.ApprovedBy");
		sql.append(" From MANUAL_KNOCKOFF_UPLOAD mk");
		sql.append(" Inner Join File_Upload_Header uh on uh.ID = mk.HeaderId");
		sql.append(" Where uh.ID = :HEADER_ID");

		return sql.toString();
	}

}