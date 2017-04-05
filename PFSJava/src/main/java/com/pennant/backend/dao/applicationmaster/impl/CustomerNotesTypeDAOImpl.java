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
 * FileName    		:  CustomerNotesTypeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.applicationmaster.CustomerNotesTypeDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.CustomerNotesType;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>CustomerNotesType model</b> class.<br>
 * 
 */
public class CustomerNotesTypeDAOImpl extends BasisCodeDAO<CustomerNotesType> implements CustomerNotesTypeDAO {

	private static Logger logger = Logger.getLogger(CustomerNotesTypeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public CustomerNotesTypeDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Customer Notes Type details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CustomerNotesType
	 */
	@Override
	public CustomerNotesType getCustomerNotesTypeById(final String id, String type) {
		logger.debug("Entering");

		CustomerNotesType customerNotesType = new CustomerNotesType();
		customerNotesType.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("SELECT CustNotesTypeCode, CustNotesTypeIsActive, CustNotesTypeArchiveFrq, CustNotesTypeIsPerminent,");
		selectSql.append(" CustNotesTypeDesc,");
		if(type.contains("View")){
			selectSql.append(" lovDescCustNotesTypeArcFrqName,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  BMTCustNotesTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustNotesTypeCode =:custNotesTypeCode") ;

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerNotesType);
		RowMapper<CustomerNotesType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerNotesType.class);

		try {
			customerNotesType = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			customerNotesType = null;
		}
		logger.debug("Leaving");
		return customerNotesType;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BMTCustNotesTypes or
	 * BMTCustNotesTypes_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Customer Notes Type by key
	 * CustNotesTypeCode
	 * 
	 * @param Customer
	 *            Notes Type (customerNotesType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(CustomerNotesType customerNotesType, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append("Delete From BMTCustNotesTypes");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustNotesTypeCode =:CustNotesTypeCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerNotesType);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),	beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41004", customerNotesType.getCustNotesTypeCode(),
					customerNotesType.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.debug("Error in delete Method");
			logger.error("Exception: ", e);
			ErrorDetails errorDetails= getError("41006", customerNotesType.getCustNotesTypeCode(),
					customerNotesType.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BMTCustNotesTypes or
	 * BMTCustNotesTypes_Temp.
	 * 
	 * save Customer Notes Type
	 * 
	 * @param Customer
	 *            Notes Type (customerNotesType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(CustomerNotesType customerNotesType, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into BMTCustNotesTypes");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CustNotesTypeCode, CustNotesTypeDesc, CustNotesTypeIsPerminent, CustNotesTypeArchiveFrq," );
		insertSql.append(" CustNotesTypeIsActive,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId," );
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:CustNotesTypeCode, :CustNotesTypeDesc, :CustNotesTypeIsPerminent,:CustNotesTypeArchiveFrq,");
		insertSql.append(" :CustNotesTypeIsActive,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");

		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerNotesType);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return customerNotesType.getId();
	}

	/**
	 * This method updates the Record BMTCustNotesTypes or
	 * BMTCustNotesTypes_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Customer Notes Type by key
	 * CustNotesTypeCode and Version
	 * 
	 * @param Customer
	 *            Notes Type (customerNotesType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(CustomerNotesType customerNotesType, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();
		
		updateSql.append("Update BMTCustNotesTypes");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CustNotesTypeDesc = :CustNotesTypeDesc,");
		updateSql.append(" CustNotesTypeIsPerminent = :CustNotesTypeIsPerminent, CustNotesTypeArchiveFrq = :CustNotesTypeArchiveFrq,");
		updateSql.append(" CustNotesTypeIsActive= :CustNotesTypeIsActive ," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId," );
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where CustNotesTypeCode =:CustNotesTypeCode ");
		if (!type.endsWith("_Temp")){
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerNotesType);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);

			ErrorDetails errorDetails= getError("41003", customerNotesType.getCustNotesTypeCode(), 
					customerNotesType.getUserDetails().getUsrLanguage());	
			throw new DataAccessException(errorDetails.getError()) {
			};
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
	private ErrorDetails  getError(String errorId, String custNotesTypeCode,String userLanguage){
		String[][] parms= new String[2][2]; 
		parms[1][0] = custNotesTypeCode;
		parms[0][0] = PennantJavaUtil.getLabel("label_CustNotesTypeCode")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}
}