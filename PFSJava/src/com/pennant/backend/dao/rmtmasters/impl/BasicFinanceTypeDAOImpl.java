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
 * FileName    		:  BasicFinanceTypeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.rmtmasters.impl;

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
import com.pennant.backend.dao.rmtmasters.BasicFinanceTypeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.rmtmasters.BasicFinanceType;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>BasicFinanceType model</b> class.<br>
 * 
 */
public class BasicFinanceTypeDAOImpl extends BasisCodeDAO<BasicFinanceType>
		implements BasicFinanceTypeDAO {

	private static Logger logger = Logger
			.getLogger(BasicFinanceTypeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new BasicFinanceType
	 * 
	 * @return BasicFinanceType
	 */
	@Override
	public BasicFinanceType getBasicFinanceType() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil
				.getWorkFlowDetails("BasicFinanceType");
		BasicFinanceType basicFinanceType = new BasicFinanceType();
		if (workFlowDetails != null) {
			basicFinanceType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return basicFinanceType;
	}

	/**
	 * This method get the module from method getBasicFinanceType() and set the
	 * new record flag as true and return BasicFinanceType()
	 * 
	 * @return BasicFinanceType
	 */
	@Override
	public BasicFinanceType getNewBasicFinanceType() {
		logger.debug("Entering");
		BasicFinanceType basicFinanceType = getBasicFinanceType();
		basicFinanceType.setNewRecord(true);
		logger.debug("Leaving");
		return basicFinanceType;
	}

	/**
	 * Fetch the Record Basic Finance Types details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return BasicFinanceType
	 */
	@Override
	public BasicFinanceType getBasicFinanceTypeById(final String id, String type) {
		logger.debug("Entering");
		BasicFinanceType basicFinanceType = new BasicFinanceType();
		basicFinanceType.setId(id);

		String selectListSql = "Select FinBasicType, FinBasicDesc, Version , LastMntBy, LastMntOn," +
				" RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId"+
				" From RMTBasicFinanceTypes" + StringUtils.trimToEmpty(type)+
				" Where FinBasicType =:FinBasicType";

		logger.debug("selectListSql: " + selectListSql);
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				basicFinanceType);
		RowMapper<BasicFinanceType> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(BasicFinanceType.class);

		try {
			basicFinanceType = this.namedParameterJdbcTemplate.queryForObject(
					selectListSql, beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			basicFinanceType = null;
		}
		logger.debug("Leaving");
		return basicFinanceType;
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param BasicFinanceType
	 *            (basicFinanceType)
	 * @return BasicFinanceType
	 */
	@Override
	public void initialize(BasicFinanceType basicFinanceType) {
		super.initialize(basicFinanceType);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param BasicFinanceType
	 *            (basicFinanceType)
	 * @return void
	 */
	@Override
	public void refresh(BasicFinanceType basicFinanceType) {

	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(
				dataSource);
	}

	/**
	 * This method Deletes the Record from the RMTBasicFinanceTypes or
	 * RMTBasicFinanceTypes_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Basic Finance Types by key
	 * FinBasicType
	 * 
	 * @param Basic
	 *            Finance Types (basicFinanceType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(BasicFinanceType basicFinanceType, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		String deleteSql =  " Delete From RMTBasicFinanceTypes"+ StringUtils.trimToEmpty(type)+
							" Where FinBasicType =:FinBasicType";
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				basicFinanceType);
		
		String[] valueParm = new String[1];
		String[] errParm= new String[1];

		valueParm[0] = basicFinanceType.getFinBasicType();
		errParm[0] = PennantJavaUtil.getLabel("label_FinBasicType") + ":"+ valueParm[0];
		
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql,
					beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails= ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41003",
								errParm,valueParm), basicFinanceType.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		} catch (DataAccessException e) {
			logger.error(e);
			ErrorDetails errorDetails= ErrorUtil.getErrorDetail(
					new ErrorDetails(PennantConstants.KEY_FIELD, "41006", 
							errParm,valueParm), basicFinanceType.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into RMTBasicFinanceTypes or
	 * RMTBasicFinanceTypes_Temp.
	 * 
	 * save Basic Finance Types
	 * 
	 * @param Basic
	 *            Finance Types (basicFinanceType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(BasicFinanceType basicFinanceType, String type) {
		logger.debug("Entering");

		String insertSql = "Insert Into RMTBasicFinanceTypes"+ StringUtils.trimToEmpty(type)+
				" (FinBasicType, FinBasicDesc, Version , LastMntBy, LastMntOn, RecordStatus," +
				" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)"+
				" Values(:FinBasicType, :FinBasicDesc, :Version , :LastMntBy, :LastMntOn," +
				" :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)";
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				basicFinanceType);
		this.namedParameterJdbcTemplate.update(insertSql, beanParameters);

		logger.debug("Leaving");
		return basicFinanceType.getId();
	}

	/**
	 * This method updates the Record RMTBasicFinanceTypes or
	 * RMTBasicFinanceTypes_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Basic Finance Types by key
	 * FinBasicType and Version
	 * 
	 * @param Basic
	 *            Finance Types (basicFinanceType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(BasicFinanceType basicFinanceType, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		String updateSql = "Update RMTBasicFinanceTypes"+ StringUtils.trimToEmpty(type)+
				" Set FinBasicType = :FinBasicType, FinBasicDesc = :FinBasicDesc," +
				" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," +
				" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode," +
				" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId"+
				" Where FinBasicType =:FinBasicType";

		if (!type.endsWith("_TEMP")) {
			updateSql = updateSql.trim() + "  AND Version= :Version-1";
		}

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				basicFinanceType);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql,
				beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			String[] errParm = {
					PennantJavaUtil.getLabel("label_FinBasicType"),basicFinanceType.getId() };
			ErrorDetails errorDetails = getErrorDetailsDAO().getErrorDetail("41004",
					basicFinanceType.getUserDetails().getUsrLanguage(),errParm);
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
}