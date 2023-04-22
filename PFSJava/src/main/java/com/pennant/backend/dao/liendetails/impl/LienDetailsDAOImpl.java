package com.pennant.backend.dao.liendetails.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cronutils.utils.StringUtils;
import com.pennant.backend.dao.liendetails.LienDetailsDAO;
import com.pennanttech.model.lien.LienDetails;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class LienDetailsDAOImpl extends SequenceDao<LienDetails> implements LienDetailsDAO {
	private static Logger logger = LogManager.getLogger(LienDetailsDAOImpl.class);

	public LienDetailsDAOImpl() {
		super();
	}

	@Override
	public void save(LienDetails lu) {
		if (lu.getLienID() <= 0) {
			lu.setLienReference(String.valueOf((getNextValue("SEQ_LIEN_REF"))));
			lu.setLienID((getNextValue("SEQ_LIEN_ID")));
		}

		StringBuilder sql = new StringBuilder("Insert Into Lien_Details");
		sql.append(" (LienID, HeaderID, Reference, Marking, MarkingDate, MarkingReason");
		sql.append(", DeMarking, DemarkingReason, DemarkingDate, LienReference, LienStatus, Source");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn");
		sql.append(", LastMntBy, LastMntOn)");
		sql.append(" values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 0;

				ps.setLong(++index, lu.getLienID());
				ps.setLong(++index, lu.getHeaderID());
				ps.setString(++index, lu.getReference());
				ps.setString(++index, lu.getMarking());
				ps.setDate(++index, JdbcUtil.getDate(lu.getMarkingDate()));
				ps.setString(++index, lu.getMarkingReason());
				ps.setString(++index, lu.getDemarking());
				ps.setDate(++index, JdbcUtil.getDate(lu.getDemarkingDate()));
				ps.setString(++index, lu.getDemarkingReason());
				ps.setString(++index, lu.getLienReference());
				ps.setBoolean(++index, lu.isLienStatus());
				ps.setString(++index, lu.getSource());
				ps.setInt(++index, lu.getVersion());
				ps.setObject(++index, lu.getCreatedBy());
				ps.setTimestamp(++index, lu.getCreatedOn());
				ps.setObject(++index, lu.getApprovedBy());
				ps.setTimestamp(++index, lu.getApprovedOn());
				ps.setLong(++index, lu.getLastMntBy());
				ps.setTimestamp(++index, lu.getLastMntOn());

			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public void update(LienDetails lu) {
		StringBuilder sql = new StringBuilder("Update Lien_Details");
		sql.append(" Set ");
		sql.append(" Marking = ?, MarkingDate = ?");
		sql.append(", MarkingReason = ?, DeMarking = ?, DemarkingReason = ?");
		sql.append(", DemarkingDate = ?, LienReference = ?, LienStatus = ?");
		sql.append(", Source = ?, Version = ?,");
		sql.append(", ApprovedBy = ?, ApprovedOn = ?, LastMntBy = ?, LastMntOn = ? ");
		sql.append(" Where LienID = ? and Reference = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setString(++index, lu.getMarking());
			ps.setDate(++index, JdbcUtil.getDate(lu.getMarkingDate()));
			ps.setString(++index, lu.getMarkingReason());
			ps.setString(++index, lu.getDemarking());
			ps.setString(++index, lu.getDemarkingReason());
			ps.setDate(++index, JdbcUtil.getDate(lu.getDemarkingDate()));
			ps.setString(++index, lu.getLienReference());
			ps.setBoolean(++index, lu.isLienStatus());
			ps.setString(++index, lu.getSource());
			ps.setInt(++index, lu.getVersion());
			ps.setObject(++index, lu.getApprovedBy());
			ps.setTimestamp(++index, lu.getApprovedOn());
			ps.setLong(++index, lu.getLastMntBy());
			ps.setTimestamp(++index, lu.getLastMntOn());

			ps.setLong(++index, lu.getLienID());
			ps.setString(++index, lu.getReference());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(long lienId, String finreference) {
		String sql = "Delete From Lien_Details Where LienID = ? and Reference = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			this.jdbcOperations.update(sql, lienId, finreference);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public LienDetails getLienById(String finreference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" (LienID, HeaderID, Reference, Marking, MarkingDate, MarkingReason");
		sql.append(", DeMarking, DemarkingReason, DemarkingDate, LienReference, LienStatus, Source");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn");
		sql.append(", LastMntBy, LastMntOn)");
		sql.append("  From  Lien_Details");
		sql.append(" Where Reference = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				LienDetails lu = new LienDetails();

				lu.setLienID(rs.getLong("LienID"));
				lu.setHeaderID(rs.getLong("HeaderID"));
				lu.setReference(rs.getString("Reference"));
				lu.setMarking(rs.getString("Marking"));
				lu.setMarkingDate(rs.getTimestamp("MarkingDate"));
				lu.setMarkingReason(rs.getString("MarkingReason"));
				lu.setDemarking(rs.getString(("DeMarking")));
				lu.setDemarkingReason(rs.getString("DemarkingReason"));
				lu.setDemarkingDate(rs.getDate("DemarkingDate"));
				lu.setLienReference(rs.getString("LienReference"));
				lu.setLienStatus(rs.getBoolean("LienStatus"));
				lu.setSource(rs.getString("Source"));
				lu.setVersion(rs.getInt("Version"));
				lu.setCreatedBy(JdbcUtil.getLong(rs.getObject("CreatedBy")));
				lu.setCreatedOn(rs.getTimestamp("CreatedOn"));
				lu.setApprovedBy(JdbcUtil.getLong(rs.getObject("ApprovedBy")));
				lu.setApprovedOn(rs.getTimestamp("ApprovedOn"));
				lu.setLastMntBy(rs.getLong("LastMntBy"));
				lu.setLastMntOn(rs.getTimestamp("LastMntOn"));
				return lu;
			}, finreference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public int getCountReference(String AccNumber) {
		String sql = "Select count(ID) From Lien_Details Where AccNumber = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, AccNumber);
	}

	@Override
	public List<LienDetails> getLienDtlsByRefAndAcc(String reference, String AccNumber) {
		logger.debug(Literal.ENTERING);

		String key = "";
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ld.LienID, ld.HeaderID, ld.Reference, ld.Source, ld.Marking, ld.MarkingDate, ld.MarkingReason,");
		sql.append(" ld.DeMarking, ld.DemarkingReason, ld.DemarkingDate, ld.LienReference, ld.LienStatus,");
		sql.append(" lh.AccNumber ");
		sql.append(" From Lien_Details ld ");
		sql.append(" Left Join Lien_Header lh on ld.LienReference = lh.LienReference ");
		if (!StringUtils.isEmpty(reference)) {
			key = reference;
			sql.append(" Where ld.Reference =?");
		} else {
			key = AccNumber;
			sql.append(" Where lh.AccNumber =?");
		}

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
				LienDetails lu = new LienDetails();
				lu.setLienID(rs.getLong("LienID"));
				lu.setHeaderID(rs.getLong("HeaderID"));
				lu.setReference(rs.getString("Reference"));
				lu.setSource(rs.getString("Source"));
				lu.setMarking(rs.getString("Marking"));
				lu.setMarkingDate(rs.getTimestamp("MarkingDate"));
				lu.setMarkingReason(rs.getString("MarkingReason"));
				lu.setDemarking(rs.getString(("DeMarking")));
				lu.setDemarkingReason(rs.getString("DemarkingReason"));
				lu.setDemarkingDate(rs.getDate("DemarkingDate"));
				lu.setLienReference(rs.getString("LienReference"));
				lu.setLienStatus(rs.getBoolean("LienStatus"));
				lu.setAccountNumber(rs.getString("AccNumber"));
				return lu;
			}, key);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<LienDetails> getLienListByLienId(Long lienId) {

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" (LienID, HeaderID, Reference, Marking, MarkingDate, MarkingReason");
		sql.append(", DeMarking, DemarkingReason, DemarkingDate, LienReference, LienStatus, Source");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn");
		sql.append(", LastMntBy, LastMntOn)");
		sql.append("  From  Lien_Details");
		sql.append("  Where LienID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
				LienDetails lu = new LienDetails();

				lu.setLienID(rs.getLong("LienID"));
				lu.setHeaderID(rs.getLong("HeaderID"));
				lu.setReference(rs.getString("Reference"));
				lu.setMarking(rs.getString("Marking"));
				lu.setMarkingDate(rs.getTimestamp("MarkingDate"));
				lu.setMarkingReason(rs.getString("MarkingReason"));
				lu.setDemarking(rs.getString(("DeMarking")));
				lu.setDemarkingReason(rs.getString("DemarkingReason"));
				lu.setDemarkingDate(rs.getDate("DemarkingDate"));
				lu.setLienReference(rs.getString("LienReference"));
				lu.setLienStatus(rs.getBoolean("LienStatus"));
				lu.setSource(rs.getString("Source"));
				lu.setVersion(rs.getInt("Version"));
				lu.setCreatedBy(JdbcUtil.getLong(rs.getObject("CreatedBy")));
				lu.setCreatedOn(rs.getTimestamp("CreatedOn"));
				lu.setApprovedBy(JdbcUtil.getLong(rs.getObject("ApprovedBy")));
				lu.setApprovedOn(rs.getTimestamp("ApprovedOn"));
				lu.setLastMntBy(rs.getLong("LastMntBy"));
				lu.setLastMntOn(rs.getTimestamp("LastMntOn"));

				return lu;

			}, lienId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

}
