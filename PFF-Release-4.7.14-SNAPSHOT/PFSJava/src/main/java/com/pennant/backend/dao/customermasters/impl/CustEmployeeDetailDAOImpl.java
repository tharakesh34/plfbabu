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
 * FileName    		:  CustEmployeeDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-05-2011    														*
 *                                                                  						*
 * Modified Date    :  06-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.customermasters.impl;


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

import com.pennant.backend.dao.customermasters.CustEmployeeDetailDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.customermasters.CustEmployeeDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>CustEmployeeDetail model</b> class.<br>
 * 
 */
public class CustEmployeeDetailDAOImpl extends BasisCodeDAO<CustEmployeeDetail> implements CustEmployeeDetailDAO {

	private static Logger logger = Logger.getLogger(CustEmployeeDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public CustEmployeeDetailDAOImpl() {
		super();
	}
	
	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * Fetch the Record  Customer Bank details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CustEmployeeDetail
	 */
	@Override
	public CustEmployeeDetail getCustEmployeeDetailById(final long id,String type) {
		logger.debug("Entering");
		CustEmployeeDetail custEmployeeDetail = new CustEmployeeDetail();
		custEmployeeDetail.setId(id);
		StringBuilder selectSql = new StringBuilder();	
		selectSql.append(" SELECT CustID, EmpStatus, EmpSector, Profession, EmpName, EmpNameForOthers, EmpDesg, ");
		selectSql.append(" EmpDept, EmpFrom, MonthlyIncome, OtherIncome, AdditionalIncome, ");
		if(type.contains("View")){
			selectSql.append(" lovDescEmpStatus,lovDescEmpSector,lovDescProfession,lovDescEmpName,lovDescEmpDesg,");
			selectSql.append(" lovDescEmpDept,lovDescOtherIncome,lovDescCustShrtName,lovDescCustCIF, EmpAlocType,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM  CustEmployeeDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :CustID");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(custEmployeeDetail);
		RowMapper<CustEmployeeDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				CustEmployeeDetail.class);

		try{
			custEmployeeDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			custEmployeeDetail = null;
		}
		logger.debug("Leaving");
		return custEmployeeDetail;
	}

	
	/**
	 * This method Deletes the Record from the CustEmployeeDetail or CustEmployeeDetail_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Customer Bank by key CustID
	 * 
	 * @param Customer Bank (custEmployeeDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(CustEmployeeDetail custEmployeeDetail,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder(" Delete From CustEmployeeDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustID =:CustID");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(custEmployeeDetail);

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

	/**
	 * This method insert new Records into CustEmployeeDetail or CustEmployeeDetail_Temp.
	 *
	 * save Customer Bank 
	 * 
	 * @param Customer Bank (custEmployeeDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(CustEmployeeDetail custEmployeeDetail,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into CustEmployeeDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CustID, EmpStatus, EmpSector, Profession, EmpName, EmpNameForOthers, EmpDesg," );
		insertSql.append(" EmpDept , EmpFrom, MonthlyIncome, OtherIncome, AdditionalIncome," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CustID, :EmpStatus, :EmpSector, :Profession, :EmpName , :EmpNameForOthers, :EmpDesg,");
		insertSql.append(" :EmpDept , :EmpFrom, :MonthlyIncome, :OtherIncome, :AdditionalIncome," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(custEmployeeDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return custEmployeeDetail.getId();
	}

	/**
	 * This method updates the Record CustEmployeeDetail or CustEmployeeDetail_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Customer Bank by key CustID and Version
	 * 
	 * @param Customer Bank (custEmployeeDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CustEmployeeDetail custEmployeeDetail,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" Update CustEmployeeDetail");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set EmpStatus = :EmpStatus, EmpSector = :EmpSector," );
		updateSql.append(" Profession = :Profession, EmpName = :EmpName, EmpNameForOthers = :EmpNameForOthers,  EmpDesg = :EmpDesg,");
		updateSql.append(" EmpDept = :EmpDept, EmpFrom = :EmpFrom, MonthlyIncome = :MonthlyIncome,");
		updateSql.append(" OtherIncome = :OtherIncome, AdditionalIncome = :AdditionalIncome,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode," );
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where CustID =:CustID ");
		if (!type.endsWith("_Temp")){
			updateSql.append(" AND Version= :Version-1");
		}
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(custEmployeeDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
}