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
 * FileName    		:  ProvinceDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.systemmasters.impl;

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
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.systemmasters.ProvinceDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>Province model</b> class.<br>
 * 
 */
public class ProvinceDAOImpl extends BasisCodeDAO<Province> implements	ProvinceDAO {

	private static Logger logger = Logger.getLogger(ProvinceDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new Province
	 * 
	 * @return Province
	 */
	@Override
	public Province getProvince() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil
		.getWorkFlowDetails("Province");
		Province province = new Province();
		if (workFlowDetails != null) {
			province.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return province;
	}

	/**
	 * This method get the module from method getProvince() and set the new
	 * record flag as true and return Province()
	 * 
	 * @return Province
	 */
	@Override
	public Province getNewProvince() {
		logger.debug("Entering");
		Province province = getProvince();
		province.setNewRecord(true);
		logger.debug("Leaving");
		return province;
	}

	/**
	 * Fetch the Record Province details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Province
	 */
	public Province getProvinceById(final String cPCountry, String cPProvince,String type) {
		logger.debug("Entering");
		Province province = new Province();
		province.setCPCountry(cPCountry);
		province.setCPProvince(cPProvince);

		StringBuilder selectSql = new StringBuilder("SELECT CPCountry, CPProvince, CPProvinceName," );
		if(type.contains("View")){
			selectSql.append(" lovDescCPCountryName, ");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId " );
		selectSql.append(" FROM  RMTCountryVsProvince");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CPCountry = :cPCountry AND CPProvince =:cPProvince ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(province);
		RowMapper<Province> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Province.class);

		try {
			province = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), 
					beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			province = null;
		}
		logger.debug("Leaving");
		return province;
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param Province
	 *            (province)
	 * @return Province
	 */
	@Override
	public void initialize(Province province) {
		super.initialize(province);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param Province
	 *            (province)
	 * @return void
	 */
	@Override
	public void refresh(Province province) {

	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the RMTCountryVsProvince or
	 * RMTCountryVsProvince_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Province by key CPCountry
	 * 
	 * @param Province
	 *            (province)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(Province province, String type) {
		logger.debug("Entering");

		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder(" Delete From RMTCountryVsProvince");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CPCountry =:CPCountry and CPProvince = :CPProvince");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(province);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",province.getCPCountry(),
						province.getCPProvince(), province.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		} catch (DataAccessException e) {
			logger.error(e);
			ErrorDetails errorDetails=  getError("41006",province.getCPCountry(),
					province.getCPProvince(), province.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into RMTCountryVsProvince or
	 * RMTCountryVsProvince_Temp.
	 * 
	 * save Province
	 * 
	 * @param Province
	 *            (province)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void save(Province province, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into RMTCountryVsProvince");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CPCountry, CPProvince, CPProvinceName,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:CPCountry, :CPProvince, :CPProvinceName," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				province);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	/**
	 * This method updates the Record RMTCountryVsProvince or
	 * RMTCountryVsProvince_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Province by key CPCountry
	 * and Version
	 * 
	 * @param Province
	 *            (province)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(Province province, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update RMTCountryVsProvince");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set CPCountry = :CPCountry, CPProvince = :CPProvince," );
		updateSql.append(" CPProvinceName = :CPProvinceName," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode," );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId," );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where CPCountry =:CPCountry  and  CPProvince = :CPProvince");

		if (!type.endsWith("_TEMP")) {
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				province);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);
			ErrorDetails errorDetails= getError("41004",province.getCPCountry(), 
					province.getCPProvince(), province.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId,String country, String province,String userLanguage){
		String[][] parms= new String[2][2]; 
		
		parms[1][0] = country;
		parms[1][1] = province;

		parms[0][0] = PennantJavaUtil.getLabel("label_CPCountry")+ ":" + parms[1][0];
		parms[0][1] = PennantJavaUtil.getLabel("label_CPProvince")+ ":" + parms[1][1];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, 
				errorId, parms[0],parms[1]), userLanguage);
	}
}