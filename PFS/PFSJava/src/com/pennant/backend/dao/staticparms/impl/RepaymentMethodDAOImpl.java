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
 * FileName    		:  RepaymentMethodDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-09-2011    														*
 *                                                                  						*
 * Modified Date    :  12-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-09-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.staticparms.impl;

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
import com.pennant.backend.dao.staticparms.RepaymentMethodDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.staticparms.RepaymentMethod;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>RepaymentMethod model</b> class.<br>
 * 
 */
public class RepaymentMethodDAOImpl extends BasisCodeDAO<RepaymentMethod> implements RepaymentMethodDAO {

	private static Logger logger = Logger.getLogger(RepaymentMethodDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new RepaymentMethod
	 * 
	 * @return RepaymentMethod
	 */

	@Override
	public RepaymentMethod getRepaymentMethod() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("RepaymentMethod");
		RepaymentMethod repaymentMethod = new RepaymentMethod();
		if (workFlowDetails != null) {
			repaymentMethod.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return repaymentMethod;
	}

	/**
	 * This method get the module from method getRepaymentMethod() and set the
	 * new record flag as true and return RepaymentMethod()
	 * 
	 * @return RepaymentMethod
	 */

	@Override
	public RepaymentMethod getNewRepaymentMethod() {
		logger.debug("Entering");
		RepaymentMethod repaymentMethod = getRepaymentMethod();
		repaymentMethod.setNewRecord(true);
		logger.debug("Leaving");
		return repaymentMethod;
	}

	/**
	 * Fetch the Record RepaymentMethod details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return RepaymentMethod
	 */
	@Override
	public RepaymentMethod getRepaymentMethodById(final String id, String type) {
		logger.debug("Entering");
		RepaymentMethod repaymentMethod = getRepaymentMethod();
		repaymentMethod.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("Select RepayMethod, RepayMethodDesc,");
		/*if(type.contains("View")){
			selectSql.append("");
		}*/
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From BMTRepayMethod" + StringUtils.trimToEmpty(type));
		selectSql.append(" Where RepayMethod =:RepayMethod");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(repaymentMethod);
		RowMapper<RepaymentMethod> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(RepaymentMethod.class);

		try {
			repaymentMethod = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			repaymentMethod = null;
		}
		logger.debug("Leaving");
		return repaymentMethod;
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param RepaymentMethod
	 *            (repaymentMethod)
	 * @return RepaymentMethod
	 */
	@Override
	public void initialize(RepaymentMethod repaymentMethod) {
		super.initialize(repaymentMethod);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param RepaymentMethod
	 *            (repaymentMethod)
	 * @return void
	 */
	@Override
	public void refresh(RepaymentMethod repaymentMethod) {

	}

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BMTRepayMethod or
	 * BMTRepayMethod_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete RepaymentMethod by key
	 * RepayMethod
	 * 
	 * @param Repayment
	 *            Method (repaymentMethod)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(RepaymentMethod repaymentMethod, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append("Delete From BMTRepayMethod");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where RepayMethod =:RepayMethod");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(repaymentMethod);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41004",repaymentMethod.getRepayMethod(), 
					repaymentMethod.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.debug("Error in delete Method");
			logger.error(e);
			ErrorDetails errorDetails = getError("41006",repaymentMethod.getRepayMethod(), 
					repaymentMethod.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BMTRepayMethod or
	 * BMTRepayMethod_Temp.
	 * 
	 * save RepaymentMethod
	 * 
	 * @param Repayment
	 *            Method (repaymentMethod)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(RepaymentMethod repaymentMethod, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into BMTRepayMethod");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (RepayMethod, RepayMethodDesc,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId," );
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:RepayMethod, :RepayMethodDesc,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(repaymentMethod);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return repaymentMethod.getId();
	}

	/**
	 * This method updates the Record BMTRepayMethod or BMTRepayMethod_Temp. if
	 * Record not updated then throws DataAccessException with error 41004.
	 * update RepaymentMethod by key RepayMethod and Version
	 * 
	 * @param Repayment
	 *            Method (repaymentMethod)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(RepaymentMethod repaymentMethod, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update BMTRepayMethod");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set RepayMethod = :RepayMethod, RepayMethodDesc = :RepayMethodDesc,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where RepayMethod =:RepayMethod");
		if (!type.endsWith("_TEMP")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(repaymentMethod);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);

			ErrorDetails errorDetails = getError("41003",repaymentMethod.getRepayMethod(), repaymentMethod.getUserDetails().getUsrLanguage());
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
	private ErrorDetails  getError(String errorId, String repayMethod, String userLanguage){
		String[][] parms= new String[2][1]; 
		parms[1][0] = String.valueOf(repayMethod);
		parms[0][0] = PennantJavaUtil.getLabel("label_RepayMethod")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

}