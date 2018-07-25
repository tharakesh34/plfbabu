/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  LimitDetailDAOImpl.java                                              * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-03-2016    														*
 *                                                                  						*
 * Modified Date    :  31-03-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-03-2016       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */

package com.pennant.backend.dao.limit.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.limit.LimitDetailDAO;
import com.pennant.backend.model.limit.LimitDetails;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

/**
 * DAO methods implementation for the <b>LimitDetail model</b> class.<br>
 * 
 */

public class LimitDetailDAOImpl extends SequenceDao<LimitDetails> implements LimitDetailDAO {
    private static Logger logger = Logger.getLogger(LimitDetailDAOImpl.class);

	public LimitDetailDAOImpl() {
		super();

	}
	/**
	 * Fetch the Record  Limit Details details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return LimitDetail
	 */
	@Override
	public List<LimitDetails> getLimitDetailsByHeaderId(final long id, String type) {
		logger.debug("Entering");

		LimitDetails limitDetail = new LimitDetails();

		limitDetail.setLimitHeaderId(id);

		StringBuilder selectSql = new StringBuilder("Select DetailId, LimitHeaderId, LimitStructureDetailsID, ExpiryDate,Revolving, LimitSanctioned,ReservedLimit, UtilisedLimit, LimitCheck,LimitChkMethod");
		selectSql.append(", Version , CreatedBy,CreatedOn,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" ,LimitLineDesc,GroupName ,GroupCode, LimitLine,ItemSeq ,ItemPriority,Editable ,DisplayStyle,ItemLevel");
		}
		selectSql.append(" From LimitDetails");
		selectSql.append(StringUtils.trimToEmpty(type));

		selectSql.append(" Where LimitHeaderId =:LimitHeaderId");
		selectSql.append(" order by ItemPriority,ItemSeq ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitDetail);
		RowMapper<LimitDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LimitDetails.class);

		List<LimitDetails> detailsList= this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);		
		return detailsList;
	}
	
	/**
	 * Fetch the Record  Limit Details details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return LimitDetail
	 */
	@Override
	public List<LimitDetails> getLatestLimitExposures(final long id, String type) {
		logger.debug("Entering");

		LimitDetails limitDetail = new LimitDetails();
		limitDetail.setLimitHeaderId(id);

		StringBuilder selectSql = new StringBuilder(" Select DetailId, LimitHeaderId, LimitStructureDetailsID, LimitSanctioned, ReservedLimit, UtilisedLimit, NonRvlUtilised");
		selectSql.append(" From LimitDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where LimitHeaderId = :LimitHeaderId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitDetail);
		RowMapper<LimitDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LimitDetails.class);

		List<LimitDetails> detailsList = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		return detailsList;
	}
	

	


	/**
	 * This method Deletes the Record from the LimitDetails or LimitDetails_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Limit Details by key DetailId
	 * 
	 * @param Limit Details (limitDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void deletebyHeaderId(long headerId,String type) {
		logger.debug("Entering");
		LimitDetails limitDetail= new LimitDetails();
		limitDetail.setLimitHeaderId(headerId);
		int recordCount = 0;		
		StringBuilder deleteSql = new StringBuilder("Delete From LimitDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where LimitHeaderId =:LimitHeaderId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitDetail);
		try{
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into LimitDetails or LimitDetails_Temp.
	 * it fetches the available Sequence form SeqLimitDetails by using getNextidviewDAO().getNextId() method.  
	 *
	 * save Limit Details 
	 * 
	 * @param Limit Details (limitDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(LimitDetails limitDetail,String type) {
		logger.debug("Entering");
		if (limitDetail.getId()==Long.MIN_VALUE){
			limitDetail.setId(getNextId("SeqLimitDetails"));
			logger.debug("get NextID:"+limitDetail.getId());
		}

		StringBuilder insertSql =new StringBuilder("Insert Into LimitDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (DetailId, LimitHeaderId, LimitStructureDetailsID, ExpiryDate,Revolving, LimitSanctioned,  ReservedLimit, UtilisedLimit, LimitCheck,LimitChkMethod");
		insertSql.append(", Version , CreatedBy,CreatedOn,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:DetailId, :LimitHeaderId, :LimitStructureDetailsID, :ExpiryDate,:Revolving, :LimitSanctioned,  :ReservedLimit, :UtilisedLimit, :LimitCheck, :LimitChkMethod ");
		insertSql.append(", :Version ,:CreatedBy, :CreatedOn, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitDetail);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return limitDetail.getId();
	}

	/**
	 * This method updates the Record LimitDetails or LimitDetails_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Limit Details by key DetailId and Version
	 * 
	 * @param Limit Details (limitDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(LimitDetails limitDetail,String type) {
		logger.debug("Entering");

		int recordCount = 0;
		StringBuilder	updateSql =new StringBuilder("Update LimitDetails");
		updateSql.append(StringUtils.trimToEmpty(type)); 

		updateSql.append(" Set LimitHeaderId = :LimitHeaderId, LimitStructureDetailsID = :LimitStructureDetailsID, ExpiryDate = :ExpiryDate, Revolving = :Revolving," );
		updateSql.append(" LimitCheck = :LimitCheck, LimitChkMethod = :LimitChkMethod," );
		updateSql.append(" Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode," );
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" );

		// For Non Revolving Limits LimitSanctioned need to check update or not ?
		updateSql.append(", LimitSanctioned = :LimitSanctioned");

		// Below are Calculated fields hence those are excluded from this UPDATE statement 
		//updateSql.append(", ReservedLimit = :ReservedLimit, UtilisedLimit = :UtilisedLimit, NonRvlUtilised = :NonRvlUtilised");

		updateSql.append(" Where DetailId = :DetailId");
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version = :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitDetail);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
	@Override
	public void updateReserveUtilise(LimitDetails limitDetail, String type) {
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update LimitDetails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set   ReservedLimit = :ReservedLimit, UtilisedLimit = :UtilisedLimit");
		updateSql.append(" Where DetailId = :DetailId");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitDetail);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public int validationCheck(String limitGroup, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuilder	selectSql = new StringBuilder("Select Count(*) From LimitDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where GroupCode = :GroupCode");
		source.addValue("GroupCode", limitGroup);

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

	@Override
	public int limitItemCheck(String limitItem,String limitcategory ,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuilder	selectSql = new StringBuilder("Select Count(*) From LimitDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where  LimitLine = :LimitLine ");
		source.addValue("LimitLine", limitItem);
		source.addValue("LimitCategory", limitcategory);

		logger.debug("selectSql: " + selectSql.toString());

		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source,Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
		} finally {
			source = null;
			selectSql = null;
			logger.debug("Leaving");
		}

		return recordCount;
	}

	@Override
	public int limitStructureCheck(String structureCode, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuilder	selectSql = new StringBuilder("Select Count(*) From LimitHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where LimitStructureCode = :LimitStructureCode");
		source.addValue("LimitStructureCode", structureCode);

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

	@Override
	public List<LimitDetails> getLimitDetailsByCustID(long headerId) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("HeaderId", headerId);
		StringBuilder selectSql = new StringBuilder("Select  LimitLine ,LimitLineDesc,SqlRule  ");
		selectSql.append(" from LimitLines_view where LimitHeaderId=:HeaderId and SqlRule is not null");
		logger.debug("selectSql: " + selectSql.toString());

		RowMapper<LimitDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LimitDetails.class);
		logger.debug("Leaving");
		List<LimitDetails> limitDetailsList = this.jdbcTemplate.query(selectSql.toString(), source,typeRowMapper);
		return limitDetailsList;
	}

	@Override
	public List<LimitDetails> getLimitByLineAndgroup(long headerId,String limitItem,List<String> groupcode) {
		MapSqlParameterSource source=new MapSqlParameterSource();
		source.addValue("LimitLine", limitItem);
		source.addValue("GroupCodes", groupcode);
		source.addValue("LimitHeaderId", headerId);

		StringBuilder	selectSql = new StringBuilder("select DetailId, LimitHeaderId, LimitLine,GroupCode, LimitStructureDetailsID,LimitChkMethod, ExpiryDate, LimitSanctioned,  ReservedLimit, UtilisedLimit,");
		selectSql.append("LimitCheck, Revolving ,  Currency,Version , CreatedBy,CreatedOn,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId from ");
		selectSql.append(" LimitLines_View ");
		selectSql.append(" Where (LimitLine=:LimitLine OR GroupCode in (:GroupCodes)) AND  LimitHeaderId = :LimitHeaderId ");
		logger.debug("selectSql: " + selectSql.toString());


		RowMapper<LimitDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LimitDetails.class);
		logger.debug("Leaving");

		return  this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);	
	}

	@Override
	public ArrayList<LimitDetails> getLimitDetailsByLimitLine(long headeId,String type) {

		LimitDetails detail= new LimitDetails();
		detail.setLimitHeaderId(headeId);

		StringBuilder	selectSql = new StringBuilder("Select R.RuleCode  LimitLine,R.RuleCodeDesc  LimitLineDesc,R.SqlRule from Rules R where RuleCode in ( select LimitLine from LimitDetails_view where LimitLine is not null AND LimitHeaderId= :LimitHeaderId)");
		selectSql.append(StringUtils.trimToEmpty(type));		
		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		RowMapper<LimitDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LimitDetails.class);
		logger.debug("Leaving");
		ArrayList<LimitDetails> limitDetailsList=(ArrayList<LimitDetails>) this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	

		return limitDetailsList;
	}


	/**
	 * Method for fetch record count from Limit details.
	 * 
	 * @param structureId
	 * @return integer
	 */
	@Override
	public int getLimitDetailByStructureId(long structureId, String type) {
		logger.debug("Entering");

		LimitDetails limitDetails = new LimitDetails();
		limitDetails.setLimitStructureDetailsID(structureId);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) FROM LimitStructureDetails ");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE LimitStructureDetailsID = :LimitStructureDetailsID");

		logger.debug("selectSql: " + selectSql.toString());

		int recordCount = 0;
		try {
			SqlParameterSource beanParams = new BeanPropertySqlParameterSource(limitDetails);
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParams, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
		}

		logger.debug("Leaving");
		return recordCount;
	}
	
	/**
	 * Fetch the Record  Limit Details details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return LimitDetail
	 */
	@Override
	public LimitDetails getLimitLineByDetailId(final long id, String type) {
		logger.debug("Entering");

		LimitDetails limitDetail = new LimitDetails();
		limitDetail.setDetailId(id);

		StringBuilder sql = new StringBuilder("Select DetailId, LimitLine, LimitHeaderId, LimitStructureDetailsID, ExpiryDate,Revolving, LimitSanctioned,ReservedLimit,");
		sql.append(" UtilisedLimit, LimitCheck,LimitChkMethod");
		sql.append(", Version, CreatedBy, CreatedOn, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LimitLineDesc, GroupName, GroupCode, LimitLine, ItemSeq, ItemPriority, Editable, DisplayStyle, ItemLevel");
		}
		sql.append(" From LimitDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where DetailId = :DetailId" );

		logger.debug("sql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitDetail);
		RowMapper<LimitDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LimitDetails.class);

		try {
			limitDetail = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);	
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			limitDetail = null;
		}

		logger.debug("Leaving");
		return limitDetail;
	}
	
	/**
	 * This method Deletes the Record from the LimitDetails or LimitDetails_Temp.
	 * Using LimitStructureDetailid
	 */
	@Override
	public void deletebyLimitStructureDetailId(long strId, String type) {
		logger.debug("Entering");
		LimitDetails limitDetail= new LimitDetails();
		limitDetail.setLimitStructureDetailsID(strId);	
		StringBuilder deleteSql = new StringBuilder("Delete From LimitDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where LimitStructureDetailsID =:LimitStructureDetailsID");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitDetail);
		try{
			this.jdbcTemplate.update(deleteSql.toString(), beanParameters);			
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
		
	}

	/**
	 * Method for fetching LimitDetails for Institution Limits
	 */
	@Override
	public List<LimitDetails> getLimitDetails(long headerId) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("HeaderId", headerId);

		StringBuilder selectSql = new StringBuilder(
				"Select DetailId, LimitHeaderId, LimitStructureDetailsID, ExpiryDate, Revolving, LimitLine, LimitLineDesc, SqlRule,");
		selectSql.append(" LimitSanctioned, ReservedLimit, UtilisedLimit, LimitCheck, LimitChkMethod");

		selectSql.append(" From LimitLines_View");
		selectSql.append(" Where LimitHeaderId = :HeaderId");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<LimitDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LimitDetails.class);
		List<LimitDetails> limitDetailsList = this.jdbcTemplate.query(selectSql.toString(), source,
				typeRowMapper);

		logger.debug("Leaving");
		return limitDetailsList;
	}

	/**
	 * 
	 * @param limitDetailsList
	 * @param type
	 */
	@Override
	public void updateReserveUtiliseList(List<LimitDetails> limitDetailsList, String type) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update LimitDetails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set   ReservedLimit = :ReservedLimit, UtilisedLimit = :UtilisedLimit");
		updateSql.append(" Where DetailId = :DetailId");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(limitDetailsList.toArray());
		this.jdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

 	/**
	 * 
	 * @param limitDetailsLists
	 * @param type
	 * @return
	 */
	@Override
	public void saveList(List<LimitDetails> limitDetailsList, String type) {
		logger.debug("Entering");

		for (LimitDetails limitDetail : limitDetailsList) {
			if (limitDetail.getId() == Long.MIN_VALUE) {
				limitDetail.setId(getNextId("SeqLimitDetails"));
				logger.debug("get NextID:" + limitDetail.getId());
			}
		}

		StringBuilder insertSql = new StringBuilder("Insert Into LimitDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" (DetailId, LimitHeaderId, LimitStructureDetailsID, ExpiryDate,Revolving, LimitSanctioned,  ReservedLimit, UtilisedLimit, LimitCheck,LimitChkMethod");
		insertSql.append(
				", Version , CreatedBy,CreatedOn,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(
				" Values(:DetailId, :LimitHeaderId, :LimitStructureDetailsID, :ExpiryDate,:Revolving, :LimitSanctioned,  :ReservedLimit, :UtilisedLimit, :LimitCheck, :LimitChkMethod ");
		insertSql.append(
				", :Version ,:CreatedBy, :CreatedOn, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("updateSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(limitDetailsList.toArray());
		this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
}
