package com.pennant.backend.dao.limit.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.limit.LimitReferenceMappingDAO;
import com.pennant.backend.model.limit.LimitReferenceMapping;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

public class LimitReferenceMappingDAOImpl extends SequenceDao<LimitReferenceMapping>
		implements LimitReferenceMappingDAO {
	private static Logger				logger	= Logger.getLogger(LimitReferenceMappingDAOImpl.class);

	
	public LimitReferenceMappingDAOImpl() {
		super();
	}

	

	/**
	 * This method insert new Records into LIMIT_DETAILS or LIMIT_DETAILS_Temp. it fetches the available Sequence form
	 * SeqLIMIT_DETAILS by using getNextidviewDAO().getNextId() method.
	 * 
	 * save Limit Details
	 * 
	 * @param Limit
	 *            Details (limitDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return long id
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(LimitReferenceMapping limitReferenceMapping) {
		logger.debug("Entering");
		if (limitReferenceMapping.getId() == Long.MIN_VALUE) {
			limitReferenceMapping.setId(getNextValue("SeqLimitReferenceMapping"));
			logger.debug("get NextID:" + limitReferenceMapping.getId());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into LimitReferenceMapping");
		insertSql.append(" (ReferenceId, ReferenceCode,ReferenceNumber, HeaderId,LimitLine ) ");
		insertSql.append(" Values(:ReferenceId, :ReferenceCode,:ReferenceNumber,:HeaderId,:LimitLine )");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitReferenceMapping);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return limitReferenceMapping.getId();
	}
	
	/**
	 * 
	 * @param lmtReferenceMapping
	 */
	@Override
	public void saveBatch(List<LimitReferenceMapping> lmtReferenceMapping) {
		logger.debug("Entering");

		for (LimitReferenceMapping mapping : lmtReferenceMapping) {
			if (mapping.getId() == Long.MIN_VALUE) {
				mapping.setId(getNextValue("SeqLimitReferenceMapping"));
				logger.debug("get NextID:" + mapping.getId());
			}
		}

		StringBuilder insertSql = new StringBuilder("Insert Into LimitReferenceMapping");
		insertSql.append(" (ReferenceId, ReferenceCode,ReferenceNumber, HeaderId,LimitLine ) ");
		insertSql.append(" Values(:ReferenceId, :ReferenceCode,:ReferenceNumber,:HeaderId,:LimitLine )");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(lmtReferenceMapping.toArray());
		this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
	}
	

	/**
	 * Fetch the Record Limit items by key field
	 * 
	 * @param id
	 *            (long)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return LimitReferenceMapping
	 */
	@Override
	public LimitReferenceMapping getLimitReferencemapping(String reference, long headerId) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReferenceNumber", reference);
		source.addValue("HeaderId", headerId);

		StringBuilder selectSql = new StringBuilder(
				"Select ReferenceId, ReferenceCode,ReferenceNumber,HeaderId,LimitLine ");
		selectSql.append(" From LimitReferenceMapping");
		selectSql.append(" Where ReferenceNumber =:ReferenceNumber and HeaderId=:HeaderId");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<LimitReferenceMapping> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(LimitReferenceMapping.class);

		logger.debug("Leaving");
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.debug(e);

		}
		return null;

	}

	@Override
	public boolean deleteReferencemapping(long referenceId) {

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReferenceId", referenceId);
		StringBuilder deleteSql = new StringBuilder("Delete From LimitReferenceMapping");
		deleteSql.append(" Where ReferenceId =:ReferenceId");
		logger.debug("deleteSql: " + deleteSql.toString());

		try {
			this.jdbcTemplate.update(deleteSql.toString(), source);

		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
		}

		return true;
	}

	@Override
	public boolean deleteByHeaderID(long headerID) {

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("HeaderID", headerID);
		StringBuilder deleteSql = new StringBuilder("Delete From LimitReferenceMapping");
		deleteSql.append(" Where HeaderID =:HeaderID");
		logger.debug("deleteSql: " + deleteSql.toString());

		try {
			this.jdbcTemplate.update(deleteSql.toString(), source);
		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
		}

		return true;
	}

	/**
	 * This method get the List LimitReferenceMapping .
	 * 
	 * 
	 * @param LimitReferenceMapping
	 *            (limitDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return {@link List} of {@link LimitReferenceMapping}
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public List<LimitReferenceMapping> getLimitReferences(long headerid, String limitLine) {
		LimitReferenceMapping referenceMapping = new LimitReferenceMapping();
		referenceMapping.setHeaderId(headerid);
		referenceMapping.setLimitLine(limitLine);

		StringBuilder selectSql = new StringBuilder(" Select T1.ReferenceId, T1.ReferenceCode,");
		selectSql.append(" T1.ReferenceNumber, T1.LimitLine, T1.HeaderId");
		selectSql.append(" From LimitReferenceMapping T1 ");
		selectSql.append(" Where  HeaderId=:HeaderId and LimitLine=:LimitLine");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(referenceMapping);
		RowMapper<LimitReferenceMapping> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(LimitReferenceMapping.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public int isLimitLineExist(String lmtline) {
		int recordCount = 0;
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuilder selectSql = new StringBuilder("Select Count(*) From LimitReferenceMapping");
		selectSql.append(" Where LimitLine = :LimitLine");
		source.addValue("LimitLine", lmtline);

		logger.debug("selectSql: " + selectSql.toString());

		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
		} finally {
			source = null;
			selectSql = null;
			logger.debug("Leaving");
		}

		return recordCount;
	}

}
