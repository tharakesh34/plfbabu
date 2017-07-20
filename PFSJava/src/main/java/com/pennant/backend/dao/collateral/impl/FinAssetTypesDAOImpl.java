package com.pennant.backend.dao.collateral.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.collateral.FinAssetTypeDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.finance.FinAssetTypes;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

public class FinAssetTypesDAOImpl extends BasisCodeDAO<FinAssetTypes> implements FinAssetTypeDAO {
private static Logger logger = Logger.getLogger(FinAssetTypesDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	
	public FinAssetTypesDAOImpl() {
		super();
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method insert new Records into FinAssetTypes or FinAssetTypes_Temp.
	 *
	 * save FinAsset Types
	 * 
	 * @param FinAsset Types (finAssetTypes)
	 * @param  type (String)
	 * 			""/_Temp          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void save(FinAssetTypes finAssetTypes, String type) {
		logger.debug("Entering");
		
		StringBuilder query =new StringBuilder("Insert Into FinAssetTypes");
		query.append(StringUtils.trimToEmpty(type));
		query.append(" (Reference, AssetType, SeqNo, ");
		query.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		query.append(" Values(:Reference, :AssetType, :SeqNo,");
		query.append(" :Version ,:LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + query.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAssetTypes);
		this.namedParameterJdbcTemplate.update(query.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method updates the Record  or FinAssetTypes_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * 
	 * @param FinAssetTypes (finAssetTypes)
	 * @param  type (String)
	 * 			""/_Temp          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(FinAssetTypes finAssetTypes, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql =new StringBuilder("Update FinAssetTypes");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, ");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, ");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where Reference =:Reference AND AssetType =:AssetType AND SeqNo =:SeqNo ");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAssetTypes);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
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

		StringBuilder selectSql = new StringBuilder("Select Reference , AssetType , SeqNo ,  ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From FinAssetTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where Reference =:Reference  ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAssetTypes);
		RowMapper<FinAssetTypes> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinAssetTypes.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
		        typeRowMapper);
	}

	/**
	 * Method for Fetching List of Assigned FinAssetTypes to the Reference 
	 */
	@Override
	public FinAssetTypes getFinAssetTypesbyID(FinAssetTypes finAssetTypes, String type) {
		logger.debug("Entering");

		FinAssetTypes finAssetType = null;
		StringBuilder selectSql = new StringBuilder(" Select Reference, AssetType, SeqNo, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From FinAssetTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where Reference =:Reference AND AssetType=:AssetType AND SeqNo=:SeqNo");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAssetTypes);
		RowMapper<FinAssetTypes> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinAssetTypes.class);
		
		try{
			finAssetType	= this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.info(e);
			finAssetType = null;
		}
		logger.debug("Leaving");
		return finAssetType;
	}

	/**
	 * This method Deletes the Record from the FinAssetTypes or FinAssetTypes_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete FinAssetTypes
	 * 
	 * @param FinAssetTypes (finAssetTypes)
	 * @param  type (String)
	 * 			""/_Temp          
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
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		}catch(DataAccessException e){
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
		this.namedParameterJdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug("Leaving");
	}
}
