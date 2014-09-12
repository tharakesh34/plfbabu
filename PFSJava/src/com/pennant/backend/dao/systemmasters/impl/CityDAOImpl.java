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
 * FileName    		:  CityDAOImpl.java                                                   * 	  
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
import com.pennant.backend.dao.systemmasters.CityDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>City model</b> class.<br>
 * 
 */
public class CityDAOImpl extends BasisCodeDAO<City> implements CityDAO {

	private static Logger logger = Logger.getLogger(CityDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new City
	 * 
	 * @return City
	 */
	@Override
	public City getCity() {
		logger.debug("Entering ");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("City");
		City city= new City();
		if (workFlowDetails!=null){
			city.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving ");
		return city;
	}

	/**
	 * This method get the module from method getCity() and set the new record
	 * flag as true and return City()
	 * 
	 * @return City
	 */
	@Override
	public City getNewCity() {
		logger.debug("Entering ");
		City city = getCity();
		city.setNewRecord(true);
		logger.debug("Leaving ");
		return city;
	}

	/**
	 * Fetch the Record  City details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return City
	 */
	@Override
	public City getCityById(final String pCCountry,String pCProvince,String pCCity, String type) {
		logger.debug("Entering ");
		City city = new City();
		city.setPCCountry(pCCountry);
		city.setPCProvince(pCProvince);
		city.setPCCity(pCCity);
		
		StringBuilder selectSql = new StringBuilder("SELECT PCCountry, PCProvince, PCCity, PCCityName,");
		if(type.contains("View")){
			selectSql.append(" LovDescPCProvinceName, LovDescPCCountryName," );
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode,  NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From RMTProvinceVsCity");
		selectSql.append(StringUtils.trimToEmpty(type)); 
		selectSql.append(" Where PCCountry =:PCCountry and PCProvince=:PCProvince and PCCity=:PCCity " );

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(city);
		RowMapper<City> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(City.class);
		
		try{
			city = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.error(e);
			city = null;
		}
		logger.debug("Leaving ");
		return city;
	}
	
	/**
	 * This method initialize the Record.
	 * @param City (city)
 	 * @return City
	 */
	@Override
	public void initialize(City city) {
		logger.debug("Entering ");
		super.initialize(city);
		logger.debug("Leaving ");
	}
	
	/**
	 * This method refresh the Record.
	 * @param City (city)
 	 * @return void
	 */
	@Override
	public void refresh(City city) {
		
	}
	
	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the RMTProvinceVsCity or
	 * RMTProvinceVsCity_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete City by key PCCountry
	 * 
	 * @param City
	 *            (city)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(City city,String type) {
		logger.debug("Entering ");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder(" Delete From RMTProvinceVsCity" );
		deleteSql.append(StringUtils.trimToEmpty(type) );
		deleteSql.append(" Where PCCountry =:PCCountry and PCProvince=:PCProvince and PCCity=:PCCity ");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(city);
		
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails=getError("41003", city.getPCCountry(),
						city.getPCProvince(),city.getPCCity(),  city.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006", city.getPCCountry(),
					city.getPCProvince(),city.getPCCity(),  city.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving ");
	}
	
	/**
	 * This method insert new Records into RMTProvinceVsCity or
	 * RMTProvinceVsCity_Temp.
	 * 
	 * save City
	 * 
	 * @param City
	 *            (city)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void save(City city,String type) {
		logger.debug("Entering ");
		
		StringBuilder insertSql = new StringBuilder("Insert Into RMTProvinceVsCity" );
		insertSql.append(StringUtils.trimToEmpty(type) );
		insertSql.append(" (PCCountry, PCProvince, PCCity, PCCityName," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:PCCountry, :PCProvince, :PCCity, :PCCityName," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode," );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(city);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving ");
	}
	
	/**
	 * This method updates the Record RMTProvinceVsCity or RMTProvinceVsCity_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update City by key PCCountry and Version
	 * 
	 * @param Ciry (city)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(City city,String type) {
		int recordCount = 0;
		logger.debug("Entering ");
		
		StringBuilder updateSql = new StringBuilder("Update RMTProvinceVsCity" );
		updateSql.append(StringUtils.trimToEmpty(type) ); 
		updateSql.append(" Set PCCountry = :PCCountry, PCProvince = :PCProvince, PCCity = :PCCity,");
		updateSql.append(" PCCityName = :PCCityName," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode," );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId," );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where PCCountry =:PCCountry and PCProvince=:PCProvince and PCCity=:PCCity ");
		
		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(city);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004", city.getPCCountry(),
					city.getPCProvince(),city.getPCCity(),  city.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving ");
	}
	
	private ErrorDetails  getError(String errorId, String country,String province,
			String city, String userLanguage){
		String[][] parms= new String[2][3]; 

		parms[1][0] = country;
		parms[1][1] = province;
		parms[1][2] = city;

		parms[0][0] = PennantJavaUtil.getLabel("label_PCCountry")+ ":" + parms[1][0]
		                +" "+ PennantJavaUtil.getLabel("label_PCProvince")+ ":" + parms[1][1];
		parms[0][1]= PennantJavaUtil.getLabel("label_PCCity")+ ":" + parms[1][2];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, 
				errorId, parms[0],parms[1]), userLanguage);
	}
	
}