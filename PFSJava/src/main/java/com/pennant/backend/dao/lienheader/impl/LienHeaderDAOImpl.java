package com.pennant.backend.dao.lienheader.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.lienheader.LienHeaderDAO;
import com.pennanttech.model.lien.LienHeader;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class LienHeaderDAOImpl extends SequenceDao<LienHeader> implements LienHeaderDAO {
	private static Logger logger = LogManager.getLogger(LienHeaderDAOImpl.class);

	public LienHeaderDAOImpl() {
		super();
	}

	@Override
	public long save(LienHeader lu) {
		StringBuilder sql = new StringBuilder("Insert Into Lien_Header");
		sql.append(" (ID, LienID, Reference, AccNumber, Marking, MarkingDate");
		sql.append(", DeMarking, DemarkingDate, LienReference, LienStatus, InterfaceStatus)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		if (lu.getId() <= 0) {
			lu.setLienID((getNextValue("SEQ_LIEN_HEADER_LIEN_ID")));
			lu.setId((getNextValue("SEQ_LIEN_HEADER_ID")));
		}

		lu.setLienReference(String.valueOf(lu.getLienID()));
		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 0;

				ps.setLong(++index, lu.getId());
				ps.setLong(++index, lu.getLienID());
				ps.setString(++index, lu.getReference());
				ps.setString(++index, lu.getAccountNumber());
				ps.setString(++index, lu.getMarking());
				ps.setDate(++index, JdbcUtil.getDate(lu.getMarkingDate()));
				ps.setString(++index, lu.getDemarking());
				ps.setDate(++index, JdbcUtil.getDate(lu.getDemarkingDate()));
				ps.setString(++index, lu.getLienReference());
				ps.setBoolean(++index, lu.isLienStatus());
				ps.setString(++index, lu.getInterfaceStatus());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return lu.getId();
	}

	@Override
	public void update(LienHeader lu) {
		StringBuilder sql = new StringBuilder("Update Lien_Header");
		sql.append(" Set ");
		sql.append(" AccNumber = ?, Marking = ?, MarkingDate = ?, DeMarking = ?, DemarkingDate = ?");
		sql.append(", LienReference = ?, LienStatus = ?, InterfaceStatus = ?");
		sql.append(" Where LienID = ? and Reference = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;
			ps.setString(++index, lu.getAccountNumber());
			ps.setString(++index, lu.getMarking());
			ps.setDate(++index, JdbcUtil.getDate(lu.getMarkingDate()));
			ps.setString(++index, lu.getDemarking());
			ps.setDate(++index, JdbcUtil.getDate(lu.getDemarkingDate()));
			ps.setString(++index, lu.getLienReference());
			ps.setBoolean(++index, lu.isLienStatus());
			ps.setString(++index, lu.getInterfaceStatus());
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
	public int getCountReference(String accNumber) {
		String sql = "Select Count(ID) From Lien_Header Where AccNumber = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, accNumber);
	}

	@Override
	public LienHeader getLienByReference(String finreference, String accNum) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" lh.LienID, lh.Reference, lh.AccNumber, lh.Marking, lh.MarkingDate,");
		sql.append(" lh.DeMarking, lh.DemarkingDate, lh.LienReference, lh.LienStatus, lh.InterfaceStatus");
		sql.append(" From Lien_Header lh");
		sql.append(" Inner Join Lien_Details ld ON lh.LienID = ld.LienID");
		sql.append(" Where ld.Reference = ? and lh.AccNumber = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				LienHeader lu = new LienHeader();

				lu.setLienID(rs.getLong("LienID"));
				lu.setReference(rs.getString("Reference"));
				lu.setAccountNumber(rs.getString("AccNumber"));
				lu.setMarking(rs.getString("Marking"));
				lu.setMarkingDate(rs.getTimestamp("MarkingDate"));
				lu.setDemarking(rs.getString(("DeMarking")));
				lu.setDemarkingDate(rs.getDate("DemarkingDate"));
				lu.setLienReference(rs.getString("LienReference"));
				lu.setLienStatus(rs.getBoolean("LienStatus"));
				lu.setInterfaceStatus(rs.getString("InterfaceStatus"));
				return lu;

			}, finreference, accNum);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public LienHeader getLienByAcc(String accnumber) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ID, LienID, Reference, AccNumber, Marking, MarkingDate,");
		sql.append(" DeMarking, DemarkingDate, LienReference, LienStatus, InterfaceStatus");
		sql.append(" From  Lien_Header");
		sql.append(" Where AccNumber = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				LienHeader lu = new LienHeader();

				lu.setId(rs.getLong("ID"));
				lu.setLienID(rs.getLong("LienID"));
				lu.setReference(rs.getString("Reference"));
				lu.setAccountNumber(rs.getString("AccNumber"));
				lu.setMarking(rs.getString("Marking"));
				lu.setMarkingDate(rs.getTimestamp("MarkingDate"));
				lu.setDemarking(rs.getString(("DeMarking")));
				lu.setDemarkingDate(rs.getDate("DemarkingDate"));
				lu.setLienReference(rs.getString("LienReference"));
				lu.setLienStatus(rs.getBoolean("LienStatus"));
				lu.setInterfaceStatus(rs.getString("InterfaceStatus"));
				return lu;

			}, accnumber);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<LienHeader> getLienHeaderList(String reference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" lh.LienID, lh.Reference, lh.AccNumber, lh.Marking, lh.MarkingDate,");
		sql.append(" lh.DeMarking, lh.DemarkingDate, lh.LienReference, lh.LienStatus, lh.InterfaceStatus");
		sql.append(" From Lien_Header lh");
		sql.append(" Inner Join Lien_Details ld ON lh.LienID = ld.LienID");
		sql.append(" Where ld.Reference = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
				LienHeader lu = new LienHeader();

				lu.setLienID(rs.getLong("LienID"));
				lu.setReference(rs.getString("Reference"));
				lu.setAccountNumber(rs.getString("AccNumber"));
				lu.setMarking(rs.getString("Marking"));
				lu.setMarkingDate(rs.getTimestamp("MarkingDate"));
				lu.setDemarking(rs.getString(("DeMarking")));
				lu.setDemarkingDate(rs.getDate("DemarkingDate"));
				lu.setLienReference(rs.getString("LienReference"));
				lu.setLienStatus(rs.getBoolean("LienStatus"));
				lu.setInterfaceStatus(rs.getString("InterfaceStatus"));
				return lu;

			}, reference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public LienHeader getLienByAccAndStatus(String accnumber, Boolean isActive) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ID, LienID, Reference, AccNumber, Marking, MarkingDate,");
		sql.append(" DeMarking, DemarkingDate, LienReference, LienStatus, InterfaceStatus");
		sql.append(" From  Lien_Header");
		sql.append(" Where AccNumber = ? and LienStatus = ? ");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				LienHeader lu = new LienHeader();

				lu.setId(rs.getLong("ID"));
				lu.setLienID(rs.getLong("LienID"));
				lu.setReference(rs.getString("Reference"));
				lu.setAccountNumber(rs.getString("AccNumber"));
				lu.setMarking(rs.getString("Marking"));
				lu.setMarkingDate(rs.getTimestamp("MarkingDate"));
				lu.setDemarking(rs.getString(("DeMarking")));
				lu.setDemarkingDate(rs.getDate("DemarkingDate"));
				lu.setLienReference(rs.getString("LienReference"));
				lu.setLienStatus(rs.getBoolean("LienStatus"));
				lu.setInterfaceStatus(rs.getString("InterfaceStatus"));
				return lu;

			}, accnumber, isActive);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}
}