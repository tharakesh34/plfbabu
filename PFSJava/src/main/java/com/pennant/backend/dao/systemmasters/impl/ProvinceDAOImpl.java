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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.systemmasters.ProvinceDAO;
import com.pennant.backend.model.systemmasters.Province;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>Province model</b> class.<br>
 * 
 */
public class ProvinceDAOImpl extends BasisCodeDAO<Province> implements	ProvinceDAO {

	private static Logger logger = Logger.getLogger(ProvinceDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public ProvinceDAOImpl() {
		super();
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
		logger.debug(Literal.ENTERING);
		Province province = new Province();
		province.setCPCountry(cPCountry);
		province.setCPProvince(cPProvince);

		StringBuilder selectSql = new StringBuilder("SELECT CPCountry, CPProvince, CPProvinceName,SystemDefault,BankRefNo,CPIsActive," );
		selectSql.append(" TaxExempted, UnionTerritory, TaxStateCode, TaxAvailable, BusinessArea," );
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
			logger.error("Exception: ", e);
			province = null;
		}
		logger.debug(Literal.LEAVING);
		return province;
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
	public void delete(Province province, TableType tableType) {
		logger.debug(Literal.ENTERING);

		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder(" Delete From RMTCountryVsProvince");
		deleteSql.append(tableType.getSuffix());
		deleteSql.append(" Where CPCountry =:CPCountry and CPProvince = :CPProvince");
		deleteSql.append(QueryUtil.getConcurrencyCondition(tableType));
		
		logger.trace(Literal.SQL +deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(province);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);
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
	public String save(Province province, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder insertSql = new StringBuilder("Insert Into RMTCountryVsProvince");
		insertSql.append(tableType.getSuffix());
		insertSql.append(" (CPCountry, CPProvince, CPProvinceName,SystemDefault,BankRefNo,CPIsActive,");
		insertSql.append(" TaxExempted, UnionTerritory, TaxStateCode, TaxAvailable, BusinessArea," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:CPCountry, :CPProvince, :CPProvinceName,:SystemDefault,:BankRefNo, :CPIsActive," );
		insertSql.append(" :TaxExempted, :UnionTerritory, :TaxStateCode, :TaxAvailable, :BusinessArea," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.trace(Literal.SQL + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				province);
		try{
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		
		logger.debug(Literal.LEAVING);
		return null;
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
	@Override
	public void update(Province province, TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder("Update RMTCountryVsProvince");
		updateSql.append(tableType.getSuffix());
		updateSql.append(" Set CPProvinceName = :CPProvinceName, SystemDefault=:SystemDefault,BankRefNo=:BankRefNo,CPIsActive=:CPIsActive," );
		updateSql.append(" TaxExempted = :TaxExempted, UnionTerritory = :UnionTerritory, TaxStateCode = :TaxStateCode, TaxAvailable = :TaxAvailable, BusinessArea = :BusinessArea," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode," );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId," );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where CPCountry =:CPCountry  and  CPProvince = :CPProvince");
		updateSql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL +  updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				province);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Fetch the count of system default values by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Gender
	 */
	@Override
	public String getSystemDefaultCount(String cpprovince) {
		logger.debug(Literal.ENTERING);
		Province province = new Province();
		province.setCPProvince(cpprovince);
		province.setSystemDefault(true);

		StringBuilder selectSql = new StringBuilder();

		selectSql.append("SELECT CPProvince FROM  RMTCountryVsProvince_View ");
		selectSql.append(" Where CPProvince != :CPProvince and SystemDefault = :SystemDefault");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(province);
		String dftCPProvince = "";
		try {
			dftCPProvince = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
        } catch (Exception e) {
        	logger.warn("Exception: ", e);
        	dftCPProvince = "";
        }
		
		logger.debug(Literal.LEAVING);
		return dftCPProvince;

	}

	@Override
	public boolean isDuplicateKey(String cPCountry, String cPProvince, TableType tableType) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		String sql;
		String whereClause = "CPCountry = :cPCountry AND CPProvince =:cPProvince";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("RMTCountryVsProvince", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("RMTCountryVsProvince_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "RMTCountryVsProvince_Temp", "RMTCountryVsProvince" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("cPCountry", cPCountry);
		paramSource.addValue("cPProvince", cPProvince);

		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}
	
	@Override
	public boolean count(String taxStateCode,String cPProvince, TableType tableType){
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		String sql;
		String whereClause = "taxStateCode = :taxStateCode and cPProvince = :cPProvince" ;

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("RMTCountryVsProvince", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("RMTCountryVsProvince_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "RMTCountryVsProvince_Temp", "RMTCountryVsProvince" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("taxStateCode", taxStateCode);
		paramSource.addValue("cPProvince", cPProvince);

		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}


	@Override
	public int getBusinessAreaCount(String businessAreaValue, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		int count = 0;

		StringBuilder selectSql = new StringBuilder("Select Count(*) From RMTCountryVsProvince");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BusinessArea = :BusinessArea");
		logger.debug("selectSql: " + selectSql.toString());

		source = new MapSqlParameterSource();
		source.addValue("BusinessArea", businessAreaValue);

		try {
			count = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			logger.error(e);
		}

		logger.debug("Leaving");

		return count;
	}

	@Override
	public int geStateCodeCount(String taxStateCode, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		int count = 0;

		StringBuilder selectSql = new StringBuilder("Select Count(TaxStateCode) From RMTCountryVsProvince");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where TaxStateCode = :TaxStateCode");
		logger.debug("selectSql: " + selectSql.toString());

		source = new MapSqlParameterSource();
		source.addValue("TaxStateCode", taxStateCode);

		try {
			count = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			logger.error(e);
		}

		logger.debug("Leaving");

		return count;
	}
	
}