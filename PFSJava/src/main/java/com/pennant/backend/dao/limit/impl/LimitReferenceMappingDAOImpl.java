package com.pennant.backend.dao.limit.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.limit.LimitReferenceMappingDAO;
import com.pennant.backend.model.limit.LimitReferenceMapping;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class LimitReferenceMappingDAOImpl extends SequenceDao<LimitReferenceMapping>
		implements LimitReferenceMappingDAO {
	private static Logger logger = LogManager.getLogger(LimitReferenceMappingDAOImpl.class);

	public LimitReferenceMappingDAOImpl() {
		super();
	}

	/**
	 * This method insert new Records into LIMIT_DETAILS or LIMIT_DETAILS_Temp. it fetches the available Sequence form
	 * SeqLIMIT_DETAILS by using getNextidviewDAO().getNextId() method.
	 * 
	 * save Limit Details
	 * 
	 * @param Limit Details (limitDetail)
	 * @param type  (String) ""/_Temp/_View
	 * @return long id
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(LimitReferenceMapping limitReferenceMapping) {
		logger.debug(Literal.ENTERING);
		if (limitReferenceMapping.getId() == Long.MIN_VALUE) {
			limitReferenceMapping.setId(getNextValue("SeqLimitReferenceMapping"));
		}

		StringBuilder sql = new StringBuilder("Insert Into LimitReferenceMapping");
		sql.append(" (ReferenceId, ReferenceCode,ReferenceNumber, HeaderId,LimitLine)");
		sql.append(" Values(:ReferenceId, :ReferenceCode, :ReferenceNumber, :HeaderId, :LimitLine)");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitReferenceMapping);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
		return limitReferenceMapping.getId();
	}

	/**
	 * 
	 * @param lmtReferenceMapping
	 */
	@Override
	public void saveBatch(List<LimitReferenceMapping> lmtReferenceMapping) {
		logger.debug(Literal.ENTERING);

		for (LimitReferenceMapping mapping : lmtReferenceMapping) {
			if (mapping.getId() == Long.MIN_VALUE) {
				mapping.setId(getNextValue("SeqLimitReferenceMapping"));
			}
		}

		StringBuilder sql = new StringBuilder("Insert Into LimitReferenceMapping");
		sql.append(" (ReferenceId, ReferenceCode,ReferenceNumber, HeaderId,LimitLine) ");
		sql.append(" Values(:ReferenceId, :ReferenceCode,:ReferenceNumber,:HeaderId,:LimitLine)");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(lmtReferenceMapping.toArray());
		this.jdbcTemplate.batchUpdate(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Fetch the Record Limit items by key field
	 * 
	 * @param id   (long)
	 * @param type (String) ""/_Temp/_View
	 * @return LimitReferenceMapping
	 */
	@Override
	public LimitReferenceMapping getLimitReferencemapping(String reference, long headerId) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select ReferenceId, ReferenceCode, ReferenceNumber, HeaderId, LimitLine");
		sql.append(" From LimitReferenceMapping");
		sql.append(" Where ReferenceNumber = ? and HeaderId = ?");

		logger.trace(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				LimitReferenceMapping fee = new LimitReferenceMapping();

				fee.setReferenceId(rs.getLong("ReferenceId"));
				fee.setReferenceCode(rs.getString("ReferenceCode"));
				fee.setReferenceNumber(rs.getString("ReferenceNumber"));
				fee.setHeaderId(rs.getLong("HeaderId"));
				fee.setLimitLine(rs.getString("LimitLine"));

				return fee;
			}, reference, headerId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Limit mapping details not available for the specified Reference {}, HeaderId {}", reference,
					headerId);
		}
		return null;

	}

	@Override
	public boolean deleteReferencemapping(long referenceId) {
		StringBuilder sql = new StringBuilder("Delete From LimitReferenceMapping");
		sql.append(" Where ReferenceId =:ReferenceId");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReferenceId", referenceId);

		return this.jdbcTemplate.update(sql.toString(), source) > 0;

	}

	@Override
	public boolean deleteByHeaderID(long headerID) {
		StringBuilder sql = new StringBuilder("Delete From LimitReferenceMapping");
		sql.append(" Where HeaderID =:HeaderID");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("HeaderID", headerID);

		return this.jdbcTemplate.update(sql.toString(), source) > 0;

	}

	/**
	 * This method get the List LimitReferenceMapping .
	 * 
	 * 
	 * @param LimitReferenceMapping (limitDetail)
	 * @param type                  (String) ""/_Temp/_View
	 * @return {@link List} of {@link LimitReferenceMapping}
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public List<LimitReferenceMapping> getLimitReferences(long headerid, String limitLine) {
		LimitReferenceMapping referenceMapping = new LimitReferenceMapping();
		referenceMapping.setHeaderId(headerid);
		referenceMapping.setLimitLine(limitLine);

		StringBuilder sql = new StringBuilder("Select T1.ReferenceId, T1.ReferenceCode");
		sql.append(", T1.ReferenceNumber, T1.LimitLine, T1.HeaderId");
		sql.append(" From LimitReferenceMapping T1 ");
		sql.append(" Where HeaderId = :HeaderId and LimitLine = :LimitLine");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(referenceMapping);
		RowMapper<LimitReferenceMapping> typeRowMapper = BeanPropertyRowMapper.newInstance(LimitReferenceMapping.class);

		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public int isLimitLineExist(String lmtline) {
		StringBuilder sql = new StringBuilder("Select Count(*) From LimitReferenceMapping");
		sql.append(" Where LimitLine = :LimitLine");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("LimitLine", lmtline);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e.toString());
		}

		return 0;
	}

}
