package com.pennant.pff.presentment.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.util.ReceiptUploadConstants.ReceiptDetailStatus;
import com.pennant.backend.util.RepayConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.presentment.dao.RePresentmentUploadDAO;
import com.pennant.pff.presentment.model.RePresentmentUploadDetail;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.file.UploadTypes;
import com.pennanttech.pff.presentment.model.PresentmentDetail;

public class RePresentmentUploadDAOImpl extends SequenceDao<RePresentmentUploadDetail>
		implements RePresentmentUploadDAO {

	@Override
	public List<RePresentmentUploadDetail> getDetails(long headerID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" HeaderId, Id, FinID, FinReference");
		sql.append(", RecordSeq, DueDate, Progress, ErrorCode, ErrorDesc");
		sql.append(" From REPRESENT_UPLOADS ");
		sql.append(" Where HeaderId = ? and Status = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			RePresentmentUploadDetail rpud = new RePresentmentUploadDetail();

			rpud.setHeaderId(rs.getLong("HeaderId"));
			rpud.setId(rs.getLong("Id"));
			rpud.setReferenceID(JdbcUtil.getLong(rs.getObject("FinID")));
			rpud.setReference(rs.getString("FinReference"));
			rpud.setRecordSeq(rs.getLong("RecordSeq"));
			rpud.setDueDate(rs.getDate("DueDate"));
			rpud.setProgress(rs.getInt("Progress"));
			rpud.setErrorCode(rs.getString("ErrorCode"));
			rpud.setErrorDesc(rs.getString("ErrorDesc"));
			return rpud;
		}, headerID, "S");
	}

	@Override
	public List<String> isDuplicateExists(String reference, Date dueDate, long headerID) {
		StringBuilder sql = new StringBuilder("Select FileName From FILE_UPLOAD_HEADER");
		sql.append(" Where Type = ? and Id IN (");
		sql.append(" Select HeaderId From REPRESENT_UPLOADS");
		sql.append(" Where FinReference = ?  and DueDate = ?");
		sql.append(" and HeaderId <> ? and Progress = ?");
		sql.append(")");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 0;

			ps.setString(++index, UploadTypes.RE_PRESENTMENT.name());
			ps.setString(++index, reference);
			ps.setDate(++index, JdbcUtil.getDate(dueDate));
			ps.setLong(++index, headerID);
			ps.setInt(++index, ReceiptDetailStatus.SUCCESS.getValue());

		}, (rs, roNum) -> {
			return rs.getString(1);
		});
	}

	@Override
	public boolean isProcessed(String reference, Date dueDate) {
		StringBuilder sql = new StringBuilder("Select count(ID) From FILE_UPLOAD_HEADER Where Type = ? and Id IN (");
		sql.append("Select HeaderId From REPRESENT_UPLOADS ru");
		sql.append(" Inner Join PresentmentDetails pd on pd.PRESENTMENTID = ru.PRESENTMENTID and pd.Status != ?");
		sql.append(" Where ru.FinReference = ? and ru.DueDate = ? and ru.Progress = ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, UploadTypes.RE_PRESENTMENT.name(),
				RepayConstants.PEXC_BOUNCE, reference, JdbcUtil.getDate(dueDate),
				ReceiptDetailStatus.SUCCESS.getValue()) > 0;
	}

	@Override
	public String getBounceCode(String reference, Date dueDate) {
		StringBuilder sql = new StringBuilder("Select pd.Status, br.BounceCode");
		sql.append(" From PresentmentDetails pd");
		sql.append(" Left Join BounceReasons br on br.BounceId = pd.BounceID");
		sql.append(" Where pd.FinReference = ? and pd.SchDate = ?");
		sql.append(" Order By pd.Lastmnton desc");

		logger.debug(Literal.SQL.concat(sql.toString()));

		List<PresentmentDetail> list = this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, reference);
			ps.setDate(2, JdbcUtil.getDate(dueDate));
		}, (rs, rowNum) -> {
			PresentmentDetail pd = new PresentmentDetail();

			pd.setStatus(rs.getString(1));

			pd.setBounceCode(rs.getString(2));
			return pd;
		});

		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		PresentmentDetail pd = list.get(0);

		if (!RepayConstants.PEXC_BOUNCE.equals(pd.getStatus())) {
			return null;
		}

		return pd.getBounceCode();
	}

	@Override
	public void update(List<RePresentmentUploadDetail> detailsList) {
		String sql = "Update REPRESENT_UPLOADS set FinID = ?, Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				RePresentmentUploadDetail detail = detailsList.get(i);

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
	public void update(List<Long> headerIds, String errorCode, String errorDesc, int progress) {
		String sql = "Update REPRESENT_UPLOADS set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where HeaderID = ?";

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
		sql.append(" ru.FinReference, ru.DueDate, ru.PresentmentID, uh.CreatedOn");
		sql.append(", ru.Status, ru.ErrorCode, ru.ErrorDesc, uh.CreatedBy, uh.ApprovedBy");
		sql.append(" From REPRESENT_UPLOADS ru");
		sql.append(" Inner Join FILE_UPLOAD_HEADER uh on uh.ID = ru.HeaderID");
		sql.append(" Where uh.ID = :HEADER_ID");

		return sql.toString();
	}
}
