package com.pennant.pff.presentment.dao.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.pennant.backend.util.ReceiptUploadConstants.ReceiptDetailStatus;
import com.pennant.backend.util.RepayConstants;
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
	public List<RePresentmentUploadDetail> loadRecordData(long id) {
		String sql = "Select HeaderId, Id, FinID, FinReference, DueDate, Progress, Remarks From REPRESENT_UPLOADS Where HeaderId = ?";

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, id), (rs, Num) -> {
			RePresentmentUploadDetail rpud = new RePresentmentUploadDetail();

			rpud.setHeaderId(rs.getLong("HeaderId"));
			rpud.setId(rs.getLong("Id"));
			rpud.setReferenceID(JdbcUtil.getLong(rs.getObject("FinID")));
			rpud.setReference(rs.getString("FinReference"));
			rpud.setDueDate(rs.getDate("DueDate"));
			rpud.setProgress(rs.getInt("Progress"));
			rpud.setRemarks(rs.getString("Remarks"));
			return rpud;
		});
	}

	@Override
	public void saveDetail(RePresentmentUploadDetail detail) {
		String sql = "Insert Into REPRESENT_UPLOADS (HeaderId, FinID, FinReference, DueDate, Progress, Remarks) Values (?, ?, ?, ?, ?, ?)";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, ps -> {
			int index = 0;

			ps.setLong(++index, detail.getHeaderId());
			ps.setObject(++index, detail.getReferenceID());
			ps.setString(++index, detail.getReference());
			ps.setDate(++index, JdbcUtil.getDate(detail.getDueDate()));
			ps.setInt(++index, detail.getProgress());
			ps.setString(++index, detail.getRemarks());
		});
	}

	@Override
	public List<String> isDuplicateExists(String reference, Date dueDate, long headerID) {
		StringBuilder sql = new StringBuilder("Select FileName From FILE_UPLOAD_HEADER_TEMP");
		sql.append(" Where Type = ? and Id IN (");
		sql.append(" Select HeaderId From REPRESENT_UPLOADS");
		sql.append(" Where FinReference = ?  and DueDate = ?");
		sql.append(" and HeaderId <> ? and Progress = ?");
		sql.append(")");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 0;

			ps.setString(++index, UploadTypes.RE_PRESENTMENT);
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

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, UploadTypes.RE_PRESENTMENT,
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
	public List<RePresentmentUploadDetail> getDataForReport(long fileID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ru.FinReference, ru.DueDate, ru.PresentmentID, uh.CreatedOn, ru.Progress");
		sql.append(", ru.Remarks, uh.CreatedBy, uh.ApprovedBy");
		sql.append(" From REPRESENT_UPLOADS ru");
		sql.append(" Inner Join FILE_UPLOAD_HEADER").append(type);
		sql.append(" uh on uh.ID = ru.HeaderID");
		sql.append(" Where uh.ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			RePresentmentUploadDetail rud = new RePresentmentUploadDetail();

			rud.setReference(rs.getString("FinReference"));
			rud.setDueDate(JdbcUtil.getDate(rs.getDate("DueDate")));
			rud.setPresentmentID(JdbcUtil.getLong(rs.getObject("PresentmentID")));
			rud.setCreatedOn(rs.getTimestamp("CreatedOn"));
			rud.setProgress(rs.getInt("Progress"));
			rud.setRemarks(rs.getString("Remarks"));
			rud.setCreatedBy(rs.getLong("CreatedBy"));
			rud.setApprovedBy(rs.getLong("ApprovedBy"));

			return rud;
		}, fileID);
	}
}
