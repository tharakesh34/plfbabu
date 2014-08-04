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
 * FileName    		:  ExtendedFieldHeaderDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  28-12-2011    														*
 *                                                                  						*
 * Modified Date    :  28-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 28-12-2011       Pennant	                 0.1                                            * 
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
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.staticparms.ExtendedFieldHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.staticparms.ExtendedFieldHeader;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>ExtendedFieldHeader model</b> class.<br>
 */
public class ExtendedFieldHeaderDAOImpl extends BasisNextidDaoImpl<ExtendedFieldHeader> implements ExtendedFieldHeaderDAO {

	private static Logger logger = Logger.getLogger(ExtendedFieldHeaderDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the new ExtendedFieldHeader 
	 * @return ExtendedFieldHeader
	 */
	@Override
	public ExtendedFieldHeader getExtendedFieldHeader() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("ExtendedFieldHeader");
		ExtendedFieldHeader extendedFieldHeader= new ExtendedFieldHeader();
		if (workFlowDetails!=null){
			extendedFieldHeader.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return extendedFieldHeader;
	}

	/**
	 * This method get the module from method getExtendedFieldHeader() and set
	 * the new record flag as true and return ExtendedFieldHeader()
	 * 
	 * @return ExtendedFieldHeader
	 */
	@Override
	public ExtendedFieldHeader getNewExtendedFieldHeader() {
		logger.debug("Entering");
		ExtendedFieldHeader extendedFieldHeader = getExtendedFieldHeader();
		extendedFieldHeader.setNewRecord(true);
		logger.debug("Leaving");
		return extendedFieldHeader;
	}

	/**
	 * Fetch the Record  Extended Field Header details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return ExtendedFieldHeader
	 */
	@Override
	public ExtendedFieldHeader getExtendedFieldHeaderById(final long id, String type) {
		logger.debug("Entering");
		ExtendedFieldHeader extendedFieldHeader = new ExtendedFieldHeader();

		extendedFieldHeader.setId(id);

		StringBuilder selectSql = new StringBuilder("Select ModuleId, ModuleName," );
		selectSql.append(" SubModuleName, TabHeading, NumberOfColumns, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode," );
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" From ExtendedFieldHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ModuleId = :ModuleId ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(extendedFieldHeader);
		RowMapper<ExtendedFieldHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ExtendedFieldHeader.class);

		try{
			extendedFieldHeader = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			extendedFieldHeader = null;
		}
		logger.debug("Leaving");
		return extendedFieldHeader;
	}

	/**
	 * Fetch by Module and Submodule names
	 */
	public ExtendedFieldHeader getExtendedFieldHeaderByModuleName(final String moduleName,
			String subModuleName, String type) {
		logger.debug("Entering");
		
		ExtendedFieldHeader extendedFieldHeader = new ExtendedFieldHeader();

		extendedFieldHeader.setModuleName(moduleName);
		extendedFieldHeader.setSubModuleName(subModuleName);

		StringBuilder selectSql = new StringBuilder("Select ModuleId, ModuleName," );
		selectSql.append(" SubModuleName, TabHeading, NumberOfColumns, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, " );
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" From ExtendedFieldHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ModuleName = :ModuleName AND SubModuleName = :SubModuleName");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(extendedFieldHeader);
		RowMapper<ExtendedFieldHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ExtendedFieldHeader.class);

		try{
			extendedFieldHeader = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			extendedFieldHeader = null;
		}
		logger.debug("Leaving");
		return extendedFieldHeader;
	}

	/**
	 * This method initialise the Record.
	 * @param ExtendedFieldHeader (extendedFieldHeader)
	 * @return ExtendedFieldHeader
	 */
	@Override
	public void initialize(ExtendedFieldHeader extendedFieldHeader) {
		super.initialize(extendedFieldHeader);
	}
	
	/**
	 * This method refresh the Record.
	 * @param ExtendedFieldHeader (extendedFieldHeader)
	 * @return void
	 */
	@Override
	public void refresh(ExtendedFieldHeader extendedFieldHeader) {

	}

	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the ExtendedFieldHeader or ExtendedFieldHeader_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Extended Field Header by key ModuleId
	 * 
	 * @param Extended Field Header (extendedFieldHeader)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(ExtendedFieldHeader extendedFieldHeader,String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From ExtendedFieldHeader");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ModuleId =:ModuleId");
		
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(extendedFieldHeader);
		
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",Labels.getLabel(extendedFieldHeader.getModuleName()) ,
						extendedFieldHeader.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",Labels.getLabel(extendedFieldHeader.getModuleName()),
					extendedFieldHeader.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into ExtendedFieldHeader or ExtendedFieldHeader_Temp.
	 * it fetches the available Sequence form SeqExtendedFieldHeader by using getNextidviewDAO().getNextId() method.  
	 *
	 * save Extended Field Header 
	 * 
	 * @param Extended Field Header (extendedFieldHeader)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(ExtendedFieldHeader extendedFieldHeader,String type) {
		logger.debug("Entering");
		
		if (extendedFieldHeader.getId()==Long.MIN_VALUE){
			extendedFieldHeader.setId(getNextidviewDAO().getNextId("SeqExtendedFieldHeader"));
			logger.debug("get NextID:"+extendedFieldHeader.getId());
		}

		StringBuilder insertSql =new StringBuilder("Insert Into ExtendedFieldHeader");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (ModuleId, ModuleName, SubModuleName, TabHeading, NumberOfColumns, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, " );
		insertSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:ModuleId, :ModuleName, :SubModuleName, :TabHeading, :NumberOfColumns, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, " );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(extendedFieldHeader);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
		return extendedFieldHeader.getId();
	}

	/**
	 * This method updates the Record ExtendedFieldHeader or ExtendedFieldHeader_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Extended Field Header by key ModuleId and Version
	 * 
	 * @param Extended Field Header (extendedFieldHeader)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(ExtendedFieldHeader extendedFieldHeader,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder	updateSql =new StringBuilder("Update ExtendedFieldHeader");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set ModuleId = :ModuleId, ModuleName = :ModuleName, " );
		updateSql.append(" SubModuleName = :SubModuleName, TabHeading = :TabHeading, " );
		updateSql.append(" NumberOfColumns = :NumberOfColumns, ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, " );
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where ModuleId =:ModuleId");

		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(extendedFieldHeader);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",extendedFieldHeader.getModuleName() ,extendedFieldHeader.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	private ErrorDetails  getError(String errorId, String ModuleId, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = ModuleId;
		parms[0][0] = PennantJavaUtil.getLabel("label_ModuleName")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId,
				parms[0],parms[1]), userLanguage);
	}


}