package com.pennant.backend.dao;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.Sequence;
import com.pennanttech.pennapps.core.resource.Literal;

public class SequenceDao<T> extends BasicDao<T> {
	private static final Logger log = LogManager.getLogger(Sequence.class);

	/**
	 * Creates a new Basic ID DAO.
	 */
	protected SequenceDao() {
		super();
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	public void updateSequence(String seqName, long updatedSeqNo) {
		log.trace(Literal.ENTERING);
		switch (App.DATABASE) {
		case ORACLE:
			updateOracleSeq(seqName, updatedSeqNo);
		case POSTGRES:
			updatePostgresSeq(seqName, updatedSeqNo);
		default:
			updateSeq(seqName, updatedSeqNo);
		}
		log.trace(Literal.LEAVING);
	}

	/**
	 * This method will update the SeqNo with specified value in sql server
	 * 
	 * @param seqName
	 * @param updatedSeqNo
	 */
	private void updateSeq(String seqName, long updatedSeqNo) {
		log.trace(Literal.ENTERING);
		try {
			StringBuilder sql = new StringBuilder("exec updateSequence :seqName, :updatedSeqNo");
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("seqName", seqName);
			parameters.addValue("updatedSeqNo", updatedSeqNo);
			this.jdbcTemplate.update(sql.toString(), parameters);
		} catch (DataAccessException e) {
			log.error(Literal.EXCEPTION, "Error while updating a sequence in updateOracleSeq for " + seqName);
		}
		log.trace(Literal.LEAVING);

	}

	/**
	 * This method will update the SeqNo with specified value in Postgres
	 * 
	 * @param seqName
	 * @param updatedSeqNo
	 */
	private void updatePostgresSeq(String seqName, long updatedSeqNo) {
		log.trace(Literal.ENTERING);
		try {
			StringBuilder sql = new StringBuilder("alter sequence ");
			sql.append(seqName);
			sql.append(" restart with :updatedSeqNo ");
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("updatedSeqNo", updatedSeqNo);
			this.jdbcTemplate.update(sql.toString(), parameters);
		} catch (DataAccessException e) {
			log.error(Literal.EXCEPTION, "Error while updating a sequence in updateOracleSeq for " + seqName);
		}
		log.trace(Literal.LEAVING);
	}

	/**
	 * This method will update the SeqNo with specified value in Oracle
	 * 
	 * @param seqName
	 * @param updatedSeqNo
	 */
	private void updateOracleSeq(String seqName, long updatedSeqNo) {
		log.trace(Literal.ENTERING);
		try {
			StringBuilder sql = new StringBuilder("alter sequence ");
			sql.append(seqName);
			sql.append(" restart start with :updatedSeqNo");
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("updatedSeqNo", updatedSeqNo);
			this.jdbcTemplate.update(sql.toString(), parameters);
		} catch (DataAccessException e) {
			log.error(Literal.EXCEPTION, "Error while updating a sequence in updateOracleSeq for " + seqName);
		}
		log.trace(Literal.LEAVING);
	}
}
