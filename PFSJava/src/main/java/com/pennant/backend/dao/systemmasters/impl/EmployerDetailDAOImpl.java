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
 * FileName    		:  EmployerDetailDAOImpl.java                                                   * 	  
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

package com.pennant.backend.dao.systemmasters.impl;


import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.systemmasters.EmployerDetailDAO;
import com.pennant.backend.model.systemmasters.EmployerDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>EmployerDetail model</b> class.<br>
 * 
 */

public class EmployerDetailDAOImpl extends BasisNextidDaoImpl<EmployerDetail> implements EmployerDetailDAO {

	private static Logger logger = Logger.getLogger(EmployerDetailDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public EmployerDetailDAOImpl() {
		super();
	}
	
	/**
	 * Fetch the Record  Employer Detail details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return EmployerDetail
	 */
	@Override
	public EmployerDetail getEmployerDetailById(final long id, String type) {
		logger.debug("Entering");
		EmployerDetail employerDetail = new EmployerDetail();
		
		employerDetail.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select EmployerId, EmpIndustry, EmpName, EstablishDate, EmpAddrHNbr, EmpFlatNbr, EmpAddrStreet, EmpAddrLine1, EmpAddrLine2, EmpPOBox, EmpCountry, EmpProvince, EmpCity, EmpPhone, EmpFax, EmpTelexNo, EmpEmailId, EmpWebSite, ContactPersonName, ContactPersonNo, EmpAlocationType,BankRefNo,EmpIsActive");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",lovDescIndustryDesc,lovDescCountryDesc,lovDescProvinceName,lovDescCityName");
		}
		selectSql.append(" From EmployerDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where EmployerId =:EmployerId");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(employerDetail);
		RowMapper<EmployerDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(EmployerDetail.class);
		
		try{
			employerDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			employerDetail = null;
		}
		logger.debug("Leaving");
		return employerDetail;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the EmployerDetail or EmployerDetail_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Employer Detail by key EmployerId
	 * 
	 * @param Employer Detail (employerDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(EmployerDetail employerDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From EmployerDetail");
		deleteSql.append(tableType.getSuffix());
		deleteSql.append(" Where EmployerId =:EmployerId");
		deleteSql.append(QueryUtil.getConcurrencyCondition(tableType));
		logger.trace(Literal.SQL + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(employerDetail);
		try{
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
	 * This method insert new Records into EmployerDetail or EmployerDetail_Temp.
	 * it fetches the available Sequence form SeqEmployerDetail by using getNextidviewDAO().getNextId() method.  
	 *
	 * save Employer Detail 
	 * 
	 * @param Employer Detail (employerDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(EmployerDetail employerDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);
		if (employerDetail.getId()==Long.MIN_VALUE){
			employerDetail.setId(getNextidviewDAO().getNextId("SeqEmployerDetail"));
			logger.debug("get NextID:"+employerDetail.getId());
		}
		
		StringBuilder insertSql =new StringBuilder("Insert Into EmployerDetail");
		insertSql.append(tableType.getSuffix());
		insertSql.append(" (EmployerId, EmpIndustry, EmpName, EstablishDate, EmpAddrHNbr, EmpFlatNbr, EmpAddrStreet, EmpAddrLine1, EmpAddrLine2, EmpPOBox, EmpCountry, EmpProvince, EmpCity, EmpPhone, EmpFax, EmpTelexNo, EmpEmailId, EmpWebSite, ContactPersonName, ContactPersonNo, EmpAlocationType,BankRefNo,EmpIsActive");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:EmployerId, :EmpIndustry, :EmpName, :EstablishDate, :EmpAddrHNbr, :EmpFlatNbr, :EmpAddrStreet, :EmpAddrLine1, :EmpAddrLine2, :EmpPOBox, :EmpCountry, :EmpProvince, :EmpCity, :EmpPhone, :EmpFax, :EmpTelexNo, :EmpEmailId, :EmpWebSite, :ContactPersonName, :ContactPersonNo, :EmpAlocationType, :BankRefNo, :EmpIsActive");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.trace(Literal.SQL + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(employerDetail);
		try{
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
		return String.valueOf(employerDetail.getId());
	}
	
	/**
	 * This method updates the Record EmployerDetail or EmployerDetail_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Employer Detail by key EmployerId and Version
	 * 
	 * @param Employer Detail (employerDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(EmployerDetail employerDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder("Update EmployerDetail");
		updateSql.append(tableType.getSuffix());
		updateSql
				.append(" Set EmpIndustry = :EmpIndustry, EmpName = :EmpName, EstablishDate = :EstablishDate, EmpAddrHNbr = :EmpAddrHNbr, EmpFlatNbr = :EmpFlatNbr, EmpAddrStreet = :EmpAddrStreet, EmpAddrLine1 = :EmpAddrLine1, EmpAddrLine2 = :EmpAddrLine2, EmpPOBox = :EmpPOBox, EmpCountry = :EmpCountry, EmpProvince = :EmpProvince, EmpCity = :EmpCity, EmpPhone = :EmpPhone, EmpFax = :EmpFax, EmpTelexNo = :EmpTelexNo, EmpEmailId = :EmpEmailId, EmpWebSite = :EmpWebSite, ContactPersonName = :ContactPersonName, ContactPersonNo = :ContactPersonNo, EmpAlocationType = :EmpAlocationType, BankRefNo = :BankRefNo, EmpIsActive = :EmpIsActive");
		updateSql
				.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where EmployerId =:EmployerId");
		//updateSql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(employerDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isDuplicateKey(long employerId, TableType tableType) {
		// TODO Auto-generated method stub
		return false;
	}

	
}