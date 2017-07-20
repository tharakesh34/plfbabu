package com.pennant.backend.dao.limit.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.limit.LimitGroupItemsDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.limit.LimitGroupItems;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

public class LimitGroupItemsDAOImpl extends BasisCodeDAO<LimitGroupItems> implements LimitGroupItemsDAO {

	private static Logger logger = Logger.getLogger(LimitGroupItemsDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new LimitGroupItems 
	 * @return LimitGroupItems
	 */

	@Override
	public LimitGroupItems getLimitGroupItems() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("LimitGroup");
		LimitGroupItems limitGroupItemsItems= new LimitGroupItems();
		if (workFlowDetails!=null){
			limitGroupItemsItems.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return limitGroupItemsItems;
	}


	/**
	 * This method get the module from method getLimitGroupItems() and set the new record flag as true and return LimitGroupItems()   
	 * @return LimitGroupItems
	 */


	@Override
	public LimitGroupItems getNewLimitGroupItems() {
		logger.debug("Entering");
		LimitGroupItems limitGroupItemsItems = getLimitGroupItems();
		limitGroupItemsItems.setNewRecord(true);
		logger.debug("Leaving");
		return limitGroupItemsItems;
	}

	/**
	 * Fetch the Record  Limit Group details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return LimitGroupItems
	 */
	@Override
	public List<LimitGroupItems> getLimitGroupItemsById(final String id, String type) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = null;
		
		StringBuilder selectSql = new StringBuilder("Select LimitGroupCode, GroupCode, ItemCode, ItemCodes,ItemSeq");
		selectSql.append(", Version , CreatedBy,CreatedOn,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
		}
		selectSql.append(" From LimitGroupItems");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where LimitGroupCode = :LimitGroupCode");
		selectSql.append("  order by ItemSeq");
		
		logger.debug("selectSql: " + selectSql.toString());
		source = new MapSqlParameterSource();
		source.addValue("LimitGroupCode", id);
		
		RowMapper<LimitGroupItems> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LimitGroupItems.class);
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		} finally {
			source = null;
			selectSql = null;
			logger.debug("Leaving");
		}
		return null;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the LimitGroupItems or LimitGroupItems_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Limit Group by key GroupCode
	 * 
	 * @param Limit Group (limitGroupItems)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(String limitGroupCode,String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		
		StringBuilder deleteSql = new StringBuilder("Delete From LimitGroupItems");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where LimitGroupCode = :LimitGroupCode");
		logger.debug("deleteSql: " + deleteSql.toString());
		
		source = new MapSqlParameterSource();
		source.addValue("LimitGroupCode", limitGroupCode);

		try{
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into LimitGroupItems or LimitGroupItems_Temp.
	 *
	 * save Limit Group 
	 * 
	 * @param Limit Group (limitGroupItems)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(LimitGroupItems limitGroupItems,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into LimitGroupItems");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (LimitGroupCode, GroupCode, ItemCode, ItemCodes,ItemSeq");
		insertSql.append(", Version , CreatedBy,CreatedOn,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:LimitGroupCode, :GroupCode, :ItemCode, :ItemCodes,:ItemSeq");
		insertSql.append(", :Version ,:CreatedBy, :CreatedOn, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitGroupItems);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return limitGroupItems.getId();
	}
	
	/**
	 * This method updates the Record LimitGroupItems or LimitGroupItems_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Limit Group by key GroupCode and Version
	 * 
	 * @param Limit Group (limitGroupItems)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public void update(LimitGroupItems limitGroupItems,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update LimitGroupItems");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set GroupCode = :GroupCode, ItemCode = :ItemCode , ItemCodes = :ItemCodes ,ItemSeq =:ItemSeq");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		if(limitGroupItems.getGroupCode() != null) {
			updateSql.append(" Where LimitGroupCode = :LimitGroupCode AND GroupCode = :GroupCode");
		} else {
			updateSql.append(" Where LimitGroupCode = :LimitGroupCode AND ItemCode = :ItemCode");
		}
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitGroupItems);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	

	@Override
	public void deleteLimitGroupItems(LimitGroupItems limitGroupItems, String type) {
		
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From LimitGroupItems");
		deleteSql.append(StringUtils.trimToEmpty(type));
		if(limitGroupItems.getGroupCode() != null) {
			deleteSql.append(" Where LimitGroupCode = :LimitGroupCode AND GroupCode = :GroupCode");
		} else {
			deleteSql.append(" Where LimitGroupCode = :LimitGroupCode AND ItemCode = :ItemCode");
		}
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitGroupItems);
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
	public String getItemCodes(LimitGroupItems lmtGrpItems, String limitGroupCode) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		StringBuilder selectSql;
		
		if(lmtGrpItems.getItemCode() == null) {
			selectSql = new StringBuilder("SELECT STUFF((SELECT '|' + CAST(ItemCodes as varchar)");
		} else {
			selectSql = new StringBuilder("SELECT STUFF((SELECT '|' + CAST(ItemCode as varchar)");
		}
		
		selectSql.append(" FROM LimitGroupItems");
		
		selectSql.append(" Where LimitGroupCode = :LimitGroupCode");
		selectSql.append(" FOR XML PATH(''), TYPE).value('.', 'varchar(max)'),1,1,'');");
				
		logger.debug("selectSql: " + selectSql.toString());
		source = new MapSqlParameterSource();
		source.addValue("LimitGroupCode", limitGroupCode);
		
		try {
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		} finally {
			source = null;
			selectSql = null;
			logger.debug("Leaving");
		}
		return new String();
	}
	
	@Override
	public List<LimitGroupItems> getLimitGroupItemById(final String id, String type) {
		logger.debug("Entering");
		LimitGroupItems limitGroupItems = getLimitGroupItems();
		
		limitGroupItems.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select LimitGroupCode, GroupCode, ItemCode, ItemCodes,ItemSeq");
		selectSql.append(", Version , CreatedBy,CreatedOn,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append("");
		}
		selectSql.append(" From LimitGroupItems");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where LimitGroupCode = :LimitGroupCode");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitGroupItems);
		RowMapper<LimitGroupItems> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LimitGroupItems.class);
		
		logger.debug("Leaving");
			return  this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public int validationCheck(String limitGroup, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuilder	selectSql = new StringBuilder("Select Count(*) From LimitGroupItems");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where GroupCode = :GroupCode");
		source.addValue("GroupCode", limitGroup);
		
		logger.debug("selectSql: " + selectSql.toString());
		
		try {
			
			recordCount = this.namedParameterJdbcTemplate.queryForInt(selectSql.toString(), source);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		} finally {
			source = null;
			selectSql = null;
			logger.debug("Leaving");
		}
		
		return recordCount;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public int limitItemCheck(String limitItem,String limitCategory, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuilder	selectSql = new StringBuilder("Select Count(*) From LimitGroupItems");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ItemCode = :ItemCode AND LimitCategory = :LimitCategory ");
		source.addValue("ItemCode", limitItem);
		source.addValue("LimitCategory", limitCategory);

		
		logger.debug("selectSql: " + selectSql.toString());
		
		try {
			recordCount = this.namedParameterJdbcTemplate.queryForInt(selectSql.toString(), source);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		} finally {
			source = null;
			selectSql = null;
			logger.debug("Leaving");
		}
		
		return recordCount;
	}
	
}