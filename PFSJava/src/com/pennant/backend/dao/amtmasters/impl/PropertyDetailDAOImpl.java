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
 * FileName    		:  PropertyDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-09-2011    														*
 *                                                                  						*
 * Modified Date    :  30-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-09-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.amtmasters.impl;


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

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.amtmasters.PropertyDetailDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.amtmasters.PropertyDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>PropertyDetail model</b> class.<br>
 * 
 */
public class PropertyDetailDAOImpl extends BasisNextidDaoImpl<PropertyDetail> implements PropertyDetailDAO {

	private static Logger logger = Logger.getLogger(PropertyDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the new PropertyDetail 
	 * @return PropertyDetail
	 */
	@Override
	public PropertyDetail getPropertyDetail() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("PropertyDetail");
		PropertyDetail propertyDetail= new PropertyDetail();
		if (workFlowDetails!=null){
			propertyDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return propertyDetail;
	}

	/**
	 * This method get the module from method getPropertyDetail() and 
	 * set the new record flag as true and return PropertyDetail()   
	 * @return PropertyDetail
	 */
	@Override
	public PropertyDetail getNewPropertyDetail() {
		logger.debug("Entering");
		PropertyDetail propertyDetail = getPropertyDetail();
		propertyDetail.setNewRecord(true);
		logger.debug("Leaving");
		return propertyDetail;
	}

	/**
	 * Fetch the Record  PropertyDetail details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return PropertyDetail
	 */
	@Override
	public PropertyDetail getPropertyDetailById(final long id, String type) {
		logger.debug("Entering");
		PropertyDetail propertyDetail = new PropertyDetail();
		propertyDetail.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("Select PropertyDetailId, PropertyDetailDesc,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, " );
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From AMTPropertyDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PropertyDetailId =:PropertyDetailId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(propertyDetail);
		RowMapper<PropertyDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				PropertyDetail.class);

		try{
			propertyDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			propertyDetail = null;
		}
		logger.debug("Leaving");
		return propertyDetail;
	}

	/**
	 * This method initialize the Record.
	 * @param PropertyDetail (propertyDetail)
	 * @return PropertyDetail
	 */
	@Override
	public void initialize(PropertyDetail propertyDetail) {
		super.initialize(propertyDetail);
	}
	
	/**
	 * This method refresh the Record.
	 * @param PropertyDetail (propertyDetail)
	 * @return void
	 */
	@Override
	public void refresh(PropertyDetail propertyDetail) {

	}

	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the AMTPropertyDetail or AMTPropertyDetail_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete PropertyDetail by key PropertyDetailId
	 * 
	 * @param PropertyDetail (propertyDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(PropertyDetail propertyDetail,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql =new StringBuilder();

		deleteSql.append("Delete From AMTPropertyDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where PropertyDetailId =:PropertyDetailId");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(propertyDetail);

		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",propertyDetail.getPropertyDetailId(),
						propertyDetail.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",propertyDetail.getPropertyDetailId(), 
					propertyDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into AMTPropertyDetail or AMTPropertyDetail_Temp.
	 * it fetches the available Sequence form SeqAMTPropertyDetail by using 
	 * getNextidviewDAO().getNextId() method.  
	 *
	 * save PropertyDetail 
	 * 
	 * @param PropertyDetail (propertyDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(PropertyDetail propertyDetail,String type) {
		logger.debug("Entering");
		if (propertyDetail.getId()==Long.MIN_VALUE){
			propertyDetail.setId(getNextidviewDAO().getNextId("SeqAMTPropertyDetail"));
			logger.debug("get NextID:"+propertyDetail.getId());
		}
		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into AMTPropertyDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (PropertyDetailId, PropertyDetailDesc,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, " );
		insertSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:PropertyDetailId, :PropertyDetailDesc,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, " );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(propertyDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return propertyDetail.getId();
	}

	/**
	 * This method updates the Record AMTPropertyDetail or AMTPropertyDetail_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update PropertyDetail by key PropertyDetailId and Version
	 * 
	 * @param PropertyDetail (propertyDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@SuppressWarnings("serial")
	@Override
	public void update(PropertyDetail propertyDetail,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update AMTPropertyDetail");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set PropertyDetailId = :PropertyDetailId, PropertyDetailDesc = :PropertyDetailDesc,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, " );
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, " );
		updateSql.append(" WorkflowId = :WorkflowId");
		updateSql.append(" Where PropertyDetailId =:PropertyDetailId");

		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(propertyDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",propertyDetail.getPropertyDetailId(),
					propertyDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method for getting the error details
	 * @param errorId (String)
	 * @param Id (String)
	 * @param userLanguage (String)
	 * @return ErrorDetails
	 */
	private ErrorDetails  getError(String errorId, long propertyDetailId, String userLanguage){
		String[][] parms= new String[2][1]; 
		parms[1][0] =String.valueOf(propertyDetailId);
		parms[0][0] = PennantJavaUtil.getLabel("label_PropertyDetailId")+":" +parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, 
				errorId, parms[0],parms[1]), userLanguage);
	}

}