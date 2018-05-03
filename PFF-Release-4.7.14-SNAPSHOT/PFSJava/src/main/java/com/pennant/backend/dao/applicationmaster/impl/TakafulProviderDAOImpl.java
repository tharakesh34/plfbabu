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
 * FileName    		:  TakafulProviderDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-07-2013    														*
 *                                                                  						*
 * Modified Date    :  31-07-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-07-2013       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.applicationmaster.impl;


import java.util.ArrayList;
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

import com.pennant.backend.dao.applicationmaster.TakafulProviderDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.applicationmaster.TakafulProvider;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>TakafulProvider model</b> class.<br>
 * 
 */

public class TakafulProviderDAOImpl extends BasisCodeDAO<TakafulProvider> implements TakafulProviderDAO {

	private static Logger logger = Logger.getLogger(TakafulProviderDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public TakafulProviderDAOImpl() {
		super();
	}
	
	/**
	 * Fetch the Record  Employer Detail details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return TakafulProvider
	 */
	@Override
	public TakafulProvider getTakafulProviderById(final String id, String type) {
		logger.debug("Entering");
		TakafulProvider takafulProvider = new TakafulProvider();
		takafulProvider.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select TakafulCode, TakafulName, TakafulType, AccountNumber, TakafulRate,");
		selectSql.append(" EstablishedDate, Street, HouseNumber, AddrLine1, AddrLine2, Country, Province, City, ZipCode, Phone,");
		selectSql.append(" Fax, EmailId, WebSite, ContactPerson, ContactPersonNo, ProviderType, ExpiryDate,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId ");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",lovDescCountryDesc,lovDescProvinceDesc,lovDescCityDesc");
		}
		selectSql.append(" From TakafulProvider");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where TakafulCode = :TakafulCode");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(takafulProvider);
		RowMapper<TakafulProvider> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(TakafulProvider.class);
		
		try{
			takafulProvider = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			takafulProvider = null;
		}
		logger.debug("Leaving");
		return takafulProvider;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the TakafulProvider or TakafulProvider_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Employer Detail by key EmployerId
	 * 
	 * @param Employer Detail (takafulProvider)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(TakafulProvider takafulProvider, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From TakafulProvider");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where TakafulCode = :TakafulCode");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(takafulProvider);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into TakafulProvider or TakafulProvider_Temp.
	 * it fetches the available Sequence form SeqTakafulProvider by using getNextidviewDAO().getNextId() method.  
	 *
	 * save Employer Detail 
	 * 
	 * @param Employer Detail (takafulProvider)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public void save(TakafulProvider takafulProvider,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into TakafulProvider");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (TakafulCode, TakafulName, TakafulType, AccountNumber, TakafulRate, EstablishedDate, Street,HouseNumber, AddrLine1, ");
		insertSql.append("  AddrLine2, Country, Province, City, ZipCode, Phone, Fax, EmailId, WebSite, ContactPerson, ContactPersonNo, ProviderType, ExpiryDate, ");
		insertSql.append("  Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:TakafulCode, :TakafulName, :TakafulType, :AccountNumber, :TakafulRate, :EstablishedDate, :Street, :HouseNumber, :AddrLine1, ");
		insertSql.append("  :AddrLine2, :Country, :Province, :City, :ZipCode, :Phone, :Fax, :EmailId, :WebSite, :ContactPerson, :ContactPersonNo, :ProviderType, :ExpiryDate, ");
		insertSql.append("  :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(takafulProvider);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	/**
	 * This method updates the Record TakafulProvider or TakafulProvider_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Employer Detail by key EmployerId and Version
	 * 
	 * @param Employer Detail (takafulProvider)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(TakafulProvider takafulProvider, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update TakafulProvider");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		
		updateSql.append(" Set TakafulName = :TakafulName, TakafulType = :TakafulType,");
		updateSql.append(" AccountNumber = :AccountNumber , TakafulRate = :TakafulRate, EstablishedDate = :EstablishedDate,");
		updateSql.append(" Street = :Street , HouseNumber = :HouseNumber, AddrLine1 = :AddrLine1, AddrLine2 = :AddrLine2,");
		updateSql.append(" Country = :Country , Province = :Province, City = :City, ZipCode = :ZipCode, Phone = :Phone, Fax = :Fax,");
		updateSql.append(" EmailId = :EmailId, WebSite = :WebSite, ContactPerson = :ContactPerson, ContactPersonNo = :ContactPersonNo, ExpiryDate= :ExpiryDate, ProviderType= :ProviderType,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where TakafulCode = :TakafulCode");
		
		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(takafulProvider);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
	@Override
	public List<TakafulProvider> getTakafulProviders() {
		logger.debug("Entering");
		 List<TakafulProvider> takafulProvider = new  ArrayList<TakafulProvider>(1);
		
		StringBuilder selectSql = new StringBuilder("Select TakafulCode, TakafulName, TakafulType, AccountNumber, TakafulRate");
		selectSql.append(" From TakafulProvider");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(takafulProvider);
		RowMapper<TakafulProvider> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(TakafulProvider.class);
		
		try{
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			takafulProvider = null;
		}
		logger.debug("Leaving");
		return takafulProvider;
	}
}