package com.pennant.backend.dao.collateral.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.collateral.FinAssetTypeDAO;
import com.pennant.backend.model.finance.FinAssetTypes;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

public class FinAssetTypesDAOImpl extends SequenceDao<FinAssetTypes> implements FinAssetTypeDAO {
	private static Logger logger = Logger.getLogger(FinAssetTypesDAOImpl.class);

	public FinAssetTypesDAOImpl() {
		super();
	}

	/**
	 * This method insert new Records into FinAssetTypes or FinAssetTypes_Temp.
	 *
	 * save FinAsset Types
	 * 
	 * @param FinAsset
	 *            Types (finAssetTypes)
	 * @param type
	 *            (String) ""/_Temp
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void save(FinAssetTypes finAssetTypes, String type) {
		logger.debug("Entering");

		StringBuilder query = new StringBuilder();

		if (finAssetTypes.getAssetTypeId() == Long.MIN_VALUE) {
			finAssetTypes.setAssetTypeId(getNextValue("SeqFinASSETTYPES"));
			logger.debug("get NextID:" + finAssetTypes.getAssetTypeId());
		}

		query.append("Insert Into FinAssetTypes");
		query.append(StringUtils.trimToEmpty(type));
		query.append(" (AssetTypeId,Reference, AssetType, SeqNo, ");
		query.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		query.append(" Values(:AssetTypeId,:Reference, :AssetType, :SeqNo,");
		query.append(
				" :Version ,:LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + query.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAssetTypes);
		this.jdbcTemplate.update(query.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method updates the Record or FinAssetTypes_Temp. if Record not updated then throws DataAccessException with
	 * error 41004.
	 * 
	 * @param FinAssetTypes
	 *            (finAssetTypes)
	 * @param type
	 *            (String) ""/_Temp
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(FinAssetTypes finAssetTypes, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update FinAssetTypes");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(
				" Set AssetTypeId = :AssetTypeId, Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, ");
		updateSql.append(
				" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, ");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where Reference =:Reference AND AssetType =:AssetType AND SeqNo =:SeqNo ");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAssetTypes);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");

	}

	/**
	 * Method for Fetching List of Assigned FinAssetTypes to the Reference based on Module
	 */
	@Override
	public List<FinAssetTypes> getFinAssetTypesByFinRef(String reference, String type) {
		logger.debug("Entering");

		FinAssetTypes finAssetTypes = new FinAssetTypes();
		finAssetTypes.setReference(reference);

		StringBuilder selectSql = new StringBuilder("Select AssetTypeId, Reference , AssetType , SeqNo ,  ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From FinAssetTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where Reference =:Reference  ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAssetTypes);
		RowMapper<FinAssetTypes> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinAssetTypes.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Method for Fetching List of Assigned FinAssetTypes to the Reference
	 */
	@Override
	public FinAssetTypes getFinAssetTypesbyID(FinAssetTypes finAssetTypes, String type) {
		logger.debug("Entering");

		FinAssetTypes finAssetType = null;
		StringBuilder selectSql = new StringBuilder(" Select AssetTypeId, Reference, AssetType, SeqNo, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From FinAssetTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where Reference =:Reference AND AssetType=:AssetType AND SeqNo=:SeqNo");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAssetTypes);
		RowMapper<FinAssetTypes> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinAssetTypes.class);

		try {
			finAssetType = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.info(e);
			finAssetType = null;
		}
		logger.debug("Leaving");
		return finAssetType;
	}

	/**
	 * This method Deletes the Record from the FinAssetTypes or FinAssetTypes_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete FinAssetTypes
	 * 
	 * @param FinAssetTypes
	 *            (finAssetTypes)
	 * @param type
	 *            (String) ""/_Temp
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(FinAssetTypes finAssetTypes, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete FinAssetTypes");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where Reference = :Reference AND AssetType=:AssetType AND SeqNo=:SeqNo ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAssetTypes);
		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");

	}

	@Override
	public void deleteByReference(String reference, String type) {
		logger.debug("Entering");

		FinAssetTypes finAssetTypes = new FinAssetTypes();
		finAssetTypes.setReference(reference);

		StringBuilder sql = new StringBuilder("Delete From FinAssetTypes");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Reference = :Reference");

		logger.debug("deleteSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAssetTypes);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug("Leaving");
	}
}
