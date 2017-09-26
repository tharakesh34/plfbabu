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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.systemmasters.CityDAO;
import com.pennant.backend.model.systemmasters.City;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>City model</b> class.<br>
 * 
 */
public class CityDAOImpl extends BasisCodeDAO<City> implements CityDAO {

	private static Logger logger = Logger.getLogger(CityDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public CityDAOImpl() {
		super();
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
		logger.debug(Literal.ENTERING);
		City city = new City();
		city.setPCCountry(pCCountry);
		city.setPCProvince(pCProvince);
		city.setPCCity(pCCity);
		
		StringBuilder selectSql = new StringBuilder("SELECT PCCountry, PCProvince, PCCity, PCCityName, PCCityClassification, BankRefNo, CityIsActive,");
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
			logger.error("Exception: ", e);
			city = null;
		}
		logger.debug(Literal.LEAVING);
		return city;
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
	public void delete(City city, TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder(" Delete From RMTProvinceVsCity");
		deleteSql.append(tableType.getSuffix());
		deleteSql.append(" Where PCCountry =:PCCountry and PCProvince=:PCProvince and PCCity=:PCCity ");
		deleteSql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(city);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
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
	public String save(City city, TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		StringBuilder insertSql = new StringBuilder("Insert Into RMTProvinceVsCity" );
		insertSql.append(tableType.getSuffix());
		insertSql.append(" (PCCountry, PCProvince, PCCity, PCCityName, PCCityClassification, BankRefNo, CityIsActive," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:PCCountry, :PCProvince, :PCCity, :PCCityName, :PCCityClassification, :BankRefNo, :CityIsActive," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode," );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.trace(Literal.SQL + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(city);
		try {
			this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return null;
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
	@Override
	public void update(City city, TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder("Update RMTProvinceVsCity" );
		updateSql.append(tableType.getSuffix());
		updateSql.append(" Set PCCityName = :PCCityName, PCCityClassification = :PCCityClassification, BankRefNo = :BankRefNo, CityIsActive = :CityIsActive," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode," );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId," );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where PCCountry =:PCCountry and PCProvince=:PCProvince and PCCity=:PCCity ");
		updateSql.append(QueryUtil.getConcurrencyCondition(tableType));
		
		logger.trace(Literal.SQL + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(city);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isDuplicateKey(String country, String state, String city, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "PCCountry = :country and PCProvince = :state and PCCity = :city";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("RMTProvinceVsCity", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("RMTProvinceVsCity_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "RMTProvinceVsCity_Temp", "RMTProvinceVsCity" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("country", country);
		paramSource.addValue("state", state);
		paramSource.addValue("city", city);

		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}
	
	@Override
	public int getPCProvinceCount(String pcProvince, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		int count = 0;

		StringBuilder selectSql = new StringBuilder("Select Count(*) from RMTProvinceVsCity");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PCProvince = :PCProvince");
		logger.debug("selectSql: " + selectSql.toString());

		source = new MapSqlParameterSource();
		source.addValue("PCProvince", pcProvince);

		try {
			count = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			logger.warn("Exception: ", e);
			count = 0;
		}

		logger.debug("Leaving");

		return count;
	}

}