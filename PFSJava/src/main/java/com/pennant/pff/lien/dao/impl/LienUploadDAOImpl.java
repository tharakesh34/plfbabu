package com.pennant.pff.lien.dao.impl;

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
import com.pennant.pff.lien.dao.LienUploadDAO;
import com.pennanttech.model.lien.LienUpload;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class LienUploadDAOImpl extends SequenceDao<LienUpload> implements LienUploadDAO {

	public LienUploadDAOImpl() {
		super();
	}

	@Override
	public List<LienUpload> getDetails(long headerID) {
		StringBuilder sql = new StringBuilder("Select ID, HEADERID,");
		sql.append(" LIENID, SOURCE, REFERENCE, ACCNUMBER, MARKING, MARKINGDATE, MARKINGREASON,");
		sql.append(" DEMARKING, DEMARKINGREASON, DEMARKINGDATE, LIENREFERENCE, LIENSTATUS, INTERFACESTATUS,");
		sql.append(" REMARKS, STATUS, ACTION,");
		sql.append(" PROGRESS, ERRORCODE, ERRORDESC");
		sql.append(" From LIEN_UPLOAD");
		sql.append(" Where HEADERID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, headerID);
		}, (rs, rownum) -> {
			LienUpload lu = new LienUpload();

			lu.setId(rs.getLong("ID"));
			lu.setHeaderId(rs.getLong("HEADERID"));
			lu.setLienID(rs.getLong("LIENID"));
			lu.setSource(rs.getString("SOURCE"));
			lu.setReference(rs.getString("REFERENCE"));
			lu.setAccNumber(rs.getString("ACCNUMBER"));
			lu.setMarking(rs.getString("MARKING"));
			lu.setMarkingDate(rs.getTimestamp("MARKINGDATE"));
			lu.setMarkingReason(rs.getString("MARKINGREASON"));
			lu.setDemarking(rs.getString(("DEMARKING")));
			lu.setDemarkingReason(rs.getString("DEMARKINGREASON"));
			lu.setDemarkingDate(rs.getDate("DEMARKINGDATE"));
			lu.setLienReference(rs.getString("LIENREFERENCE"));
			lu.setLienstatus(rs.getBoolean("LIENSTATUS"));
			lu.setInterfaceStatus(rs.getString("INTERFACESTATUS"));
			lu.setRemarks(rs.getString("REMARKS"));
			lu.setStatus(rs.getString("STATUS"));
			lu.setAction(rs.getString("ACTION"));
			lu.setProgress(rs.getInt("PROGRESS"));
			lu.setErrorCode(rs.getString("ERRORCODE"));
			lu.setErrorDesc(rs.getString("ERRORDESC"));

			return lu;
		});
	}

	@Override
	public long save(LienUpload lu) {
		StringBuilder sql = new StringBuilder("Insert into LIEN_UPLOAD");
		sql.append(" (HEADERID, LIENID, SOURCE, REFERENCE, ACCNUMBER, ACTION, MARKING, MARKINGDATE, MARKINGREASON,");
		sql.append(" DEMARKING, DEMARKINGREASON, DEMARKINGDATE, LIENREFERENCE, LIENSTATUS, INTERFACESTATUS,");
		sql.append(" REMARKS, STATUS )");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?,");
		sql.append(" ?, ?, ?, ?, ?, ?, ?,");
		sql.append(" ?, ?, ?)");

		lu.setLienReference(String.valueOf((getNextValue("SEQ_LIEN_REF"))));

		lu.setLienID((getNextValue("SEQ_LIEN_ID")));

		logger.debug(Literal.SQL + sql.toString());

		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();

			this.jdbcOperations.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql.toString(), new String[] { "id" });
					int index = 1;

					ps.setLong(index++, lu.getHeaderId());
					ps.setLong(index++, lu.getLienID());
					ps.setString(index++, lu.getSource());
					ps.setString(index++, lu.getReference());
					ps.setString(index++, lu.getAccNumber());
					ps.setString(index++, lu.getAction());
					ps.setString(index++, lu.getMarking());
					ps.setDate(index++, JdbcUtil.getDate(lu.getMarkingDate()));
					ps.setString(index++, lu.getMarkingReason());
					ps.setString(index++, lu.getDemarking());
					ps.setDate(index++, JdbcUtil.getDate(lu.getDemarkingDate()));
					ps.setString(index++, lu.getDemarkingReason());
					ps.setString(index++, lu.getLienReference());
					ps.setBoolean(index++, lu.getLienstatus());
					ps.setString(index++, lu.getInterfaceStatus());
					ps.setString(index++, lu.getRemarks());
					ps.setString(index++, lu.getStatus());

					return ps;
				}
			}, keyHolder);

			return keyHolder.getKey().longValue();
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public void update(LienUpload lu, long headerID) {

		StringBuilder sql = new StringBuilder("Update LIEN_UPLOAD");
		sql.append(" Set HeaderID = ?, LIENID = ?, SOURCE = ?, REFERENCE = ?,");
		sql.append(" ACCNUMBER = ?, ACTION = ?, MARKING = ?, MARKINGDATE = ?,");
		sql.append(" MARKINGREASON = ?, DEMARKING = ?, DEMARKINGREASON = ?,");
		sql.append(" DEMARKINGDATE = ?, LIENREFERENCE = ?, LIENSTATUS = ?,");
		sql.append(" INTERFACESTATUS = ?,");
		sql.append(" REMARKS = ?, STATUS = ?,");
		sql.append(" PROGRESS = ?, ERRORCODE = ?, ERRORDESC = ?");
		sql.append(" Where HEADERID = ?");

		if (lu.getLienID() <= 0) {
			lu.setLienReference(String.valueOf((getNextValue("SEQ_LIEN_REF"))));
			lu.setLienID((getNextValue("SEQ_LIEN_ID")));
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, lu.getHeaderId());
			ps.setLong(index++, lu.getLienID());
			ps.setString(index++, lu.getSource());
			ps.setString(index++, lu.getReference());
			ps.setString(index++, lu.getAccNumber());
			ps.setString(index++, lu.getAction());
			ps.setString(index++, lu.getMarking());
			ps.setDate(index++, JdbcUtil.getDate(lu.getMarkingDate()));
			ps.setString(index++, lu.getMarkingReason());
			ps.setString(index++, lu.getDemarking());
			ps.setString(index++, lu.getDemarkingReason());
			ps.setDate(index++, JdbcUtil.getDate(lu.getDemarkingDate()));
			ps.setString(index++, lu.getLienReference());
			ps.setBoolean(index++, lu.getLienstatus());
			ps.setString(index++, lu.getInterfaceStatus());
			ps.setString(index++, lu.getRemarks());
			ps.setString(index++, lu.getStatus());
			ps.setInt(index++, lu.getProgress());
			ps.setString(index++, lu.getErrorCode());
			ps.setString(index++, lu.getErrorDesc());
			ps.setLong(index, headerID);

		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

		logger.debug("Leaving");
	}

	@Override
	public void updateStatus(List<LienUpload> details) {
		StringBuilder sql = new StringBuilder("Update LIEN_UPLOAD set");
		sql.append(" Reference = ?, AccNumber = ?, Progress = ?");
		sql.append(", Status = ?, ErrorCode = ?, ErrorDesc = ?");
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				LienUpload detail = details.get(i);

				ps.setString(++index, detail.getReference());
				ps.setString(++index, detail.getAccNumber());
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
	public void updateRejectStatus(List<Long> headerIds, String errorCode, String errorDesc, int progress) {
		String sql = "Update LIEN_UPLOAD set Progress = ?, Status = ?, ErrorCode = ?, ErrorDesc = ? Where HeaderId = ?";

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
		sql.append("  lu.Source, lu.Reference, lu.AccNumber,");
		sql.append("  lu.Action, lu.Status,");
		sql.append("  lu.ERRORCODE, lu.ERRORDESC");
		sql.append("  From LIEN_UPLOAD lu");
		sql.append("  Inner Join FILE_UPLOAD_HEADER uh on uh.ID = lu.HeaderID");
		sql.append("  Where uh.ID = :HEADER_ID");

		return sql.toString();
	}

}