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
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.staticparms.ExtendedFieldHeaderDAO;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.util.CollateralConstants;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>ExtendedFieldHeader model</b> class.<br>
 */
public class ExtendedFieldHeaderDAOImpl extends BasisNextidDaoImpl<ExtendedFieldHeader> implements ExtendedFieldHeaderDAO {

	private static Logger logger = Logger.getLogger(ExtendedFieldHeaderDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private NamedParameterJdbcTemplate adtNamedParameterJdbcTemplate;

	public ExtendedFieldHeaderDAOImpl() {
		super();
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
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, ");
		selectSql.append(" PreValidationReq, PostValidationReq, PreValidation, PostValidation ");
		selectSql.append(" From ExtendedFieldHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ModuleId = :ModuleId ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(extendedFieldHeader);
		RowMapper<ExtendedFieldHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ExtendedFieldHeader.class);

		try{
			extendedFieldHeader = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			extendedFieldHeader = null;
		}
		logger.debug("Leaving");
		return extendedFieldHeader;
	}

	/**
	 * Fetch by Module and Submodule names
	 */
	public ExtendedFieldHeader getExtendedFieldHeaderByModuleName(final String moduleName, String subModuleName, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;

		source = new MapSqlParameterSource();
		source.addValue("ModuleName", moduleName);
		source.addValue("SubModuleName", subModuleName);

		StringBuilder selectSql = new StringBuilder("Select ModuleId, ModuleName,");
		selectSql.append(" SubModuleName, TabHeading, NumberOfColumns, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, ");
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, ");
		selectSql.append(" PreValidationReq, PostValidationReq, PreValidation, PostValidation ");
		selectSql.append(" From ExtendedFieldHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ModuleName = :ModuleName AND SubModuleName = :SubModuleName");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<ExtendedFieldHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ExtendedFieldHeader.class);

		try {
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception :", e);
		}
		logger.debug("Leaving");
		return null;
	}

	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setAuditDataSource(DataSource dataSource) {
		this.adtNamedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
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
	@Override
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
				throw new ConcurrencyException();
			}
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);
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
	public long save(ExtendedFieldHeader extendedFieldHeader, String type) {
		logger.debug("Entering");

		if (extendedFieldHeader.getId() == Long.MIN_VALUE) {
			extendedFieldHeader.setId(getNextidviewDAO().getNextId("SeqExtendedFieldHeader"));
			logger.debug("get NextID:" + extendedFieldHeader.getId());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into ExtendedFieldHeader");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (ModuleId, ModuleName, SubModuleName, TabHeading, NumberOfColumns, ");
		insertSql.append(" PreValidationReq, PostValidationReq, PreValidation, PostValidation, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, ");
		insertSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:ModuleId, :ModuleName, :SubModuleName, :TabHeading, :NumberOfColumns, ");
		insertSql.append(" :PreValidationReq, :PostValidationReq, :PreValidation, :PostValidation, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, ");
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
	@Override
	public void update(ExtendedFieldHeader extendedFieldHeader,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder	updateSql =new StringBuilder("Update ExtendedFieldHeader");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set  ModuleName = :ModuleName, " );
		updateSql.append(" SubModuleName = :SubModuleName, TabHeading = :TabHeading, " );
		updateSql.append(" NumberOfColumns = :NumberOfColumns, ");
		updateSql.append(" PreValidationReq = :PreValidationReq, PostValidationReq = :PostValidationReq, ");
		updateSql.append(" PreValidation = :PreValidation, PostValidation = :PostValidation, ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, " );
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where ModuleId =:ModuleId");

		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(extendedFieldHeader);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Creating table structure for New Module & SubModule combination
	 * @param tableName
	 */
	@Override
	public void createTable(String module, String subModule) {
		logger.debug("Entering");
		int numbOfTables = 3;
		if (StringUtils.equals(module, CollateralConstants.MODULE_NAME)) {
			numbOfTables = 7;
		}
		for (int i = 0; i < numbOfTables; i++) {

			// For SQL server
			StringBuilder syntax = new StringBuilder();
			String tableName = getTableName(module, subModule, i);
			if (App.DATABASE == App.Database.SQL_SERVER) {
				syntax.append("create table ");
				syntax.append(tableName);
				if (i == 2 || i == 6) {
					syntax.append(" (AuditId 		bigint NOT NULL, ");
					syntax.append(" AuditDate 		datetime NOT NULL, ");
					syntax.append(" AuditSeq 		int NOT NULL, ");
					syntax.append(" AuditImage 		char(1) NOT NULL, ");
				} else {
					syntax.append(" ( ");
					if (i == 3) {
						syntax.append(" VerificationId 		bigint NOT NULL, ");
					}
				}
				syntax.append(" Reference 		varchar(20) NOT NULL, ");
				syntax.append(" SeqNo			int NOT NULL, ");
				syntax.append("	Version			int NOT NULL,");
				syntax.append("	LastMntBy 		bigint NULL,");
				syntax.append("	LastMntOn 		datetime NULL,");
				syntax.append("	RecordStatus 	varchar(50) NULL,");
				syntax.append("	RoleCode 		varchar(100) NULL,");
				syntax.append("	NextRoleCode 	varchar(200) NULL,");
				syntax.append("	TaskId 			varchar(50) NULL,");
				syntax.append("	NextTaskId		varchar(200) NULL,");
				syntax.append("	RecordType		varchar(50) NULL,");
				syntax.append("	WorkflowId 		bigint NULL,");
				syntax.append(" CONSTRAINT PK_" + tableName);
				if (i == 2 || i == 6) {
					syntax.append(" PRIMARY KEY (AuditId ,  AuditDate, AuditSeq, AuditImage ))");
				} else {
					if (i == 3) {
						syntax.append(" PRIMARY KEY (VerificationId , Reference ,  SeqNo ))");
					} else {
						syntax.append(" PRIMARY KEY (Reference ,  SeqNo ))");
					}
				}

				// Oracle DB Scripts
			} else if (App.DATABASE == App.Database.ORACLE) {
				syntax.append("create table ");
				syntax.append(tableName);
				if (i == 2 || i == 6) {
					syntax.append(" (AuditId 		number(19,0) NOT NULL, ");
					syntax.append(" AuditDate 		date NOT NULL, ");
					syntax.append(" AuditSeq 		number(10,0) NOT NULL, ");
					syntax.append(" AuditImage 		char(1) NOT NULL, ");
				} else {
					syntax.append(" ( ");
					if (i == 3) {
						syntax.append(" VerificationId 		number(19,0) NOT NULL, ");
					}
				}
				syntax.append(" Reference 		varchar2(20) NOT NULL, ");
				syntax.append(" SeqNo 			number(10,0) NOT NULL, ");
				syntax.append("	Version 		number(10,0) NOT NULL,");
				syntax.append("	LastMntBy 		number(19,0) NULL,");
				syntax.append("	LastMntOn 		date NULL,");
				syntax.append("	RecordStatus 	varchar2(50) NULL,");
				syntax.append("	RoleCode 		varchar2(100) NULL,");
				syntax.append("	NextRoleCode 	varchar2(200) NULL,");
				syntax.append("	TaskId 			varchar2(50) NULL,");
				syntax.append("	NextTaskId 		varchar2(200) NULL,");
				syntax.append("	RecordType 		varchar2(50) NULL,");
				syntax.append("	WorkflowId 		number(19,0) NULL,");
				syntax.append(" CONSTRAINT PK_" + tableName);
				if (i == 2 || i == 6) {
					syntax.append(" PRIMARY KEY (AuditId ,  AuditDate, AuditSeq, AuditImage ))");
				} else {
					if (i == 3) {
						syntax.append(" PRIMARY KEY (VerificationId , Reference ,  SeqNo ))");
					} else {
						syntax.append(" PRIMARY KEY (Reference ,  SeqNo ))");
					}
				}
			} else if (App.DATABASE == App.Database.POSTGRES) {
				syntax.append("create table ");
				syntax.append(tableName);
				if (i == 2 || i == 6) {
					syntax.append(" (AuditId 		bigint NOT NULL, ");
					syntax.append(" AuditDate 		timestamp NOT NULL, ");
					syntax.append(" AuditSeq 		integer NOT NULL, ");
					syntax.append(" AuditImage 		char(1) NOT NULL, ");
				} else {
					syntax.append(" ( ");
					if (i == 3) {
						syntax.append(" VerificationId 		bigint NOT NULL, ");
					}
				}
				syntax.append(" Reference 		varchar(20) NOT NULL, ");
				syntax.append(" SeqNo 			integer NOT NULL, ");
				syntax.append("	Version 		integer NOT NULL,");
				syntax.append("	LastMntBy 		bigint NULL,");
				syntax.append("	LastMntOn 		timestamp NULL,");
				syntax.append("	RecordStatus 	varchar(50) NULL,");
				syntax.append("	RoleCode 		varchar(100) NULL,");
				syntax.append("	NextRoleCode 	varchar(200) NULL,");
				syntax.append("	TaskId 			varchar(50) NULL,");
				syntax.append("	NextTaskId 		varchar(200) NULL,");
				syntax.append("	RecordType 		varchar(50) NULL,");
				syntax.append("	WorkflowId 		bigint NULL,");
				syntax.append(" CONSTRAINT PK_" + tableName);
				if (i == 2 || i == 6) {
					syntax.append(" PRIMARY KEY (AuditId ,  AuditDate, AuditSeq, AuditImage ))");
				} else {
					if (i == 3) {
						syntax.append(" PRIMARY KEY (VerificationId , Reference ,  SeqNo ))");
					} else {
						syntax.append(" PRIMARY KEY (Reference ,  SeqNo ))");
					}
				}
			}

			try {
				logger.debug("createsql: " + syntax.toString());
				if (i == 2 || i == 6) {// Audit DB
					this.adtNamedParameterJdbcTemplate.getJdbcOperations().update(syntax.toString());
				} else {
					this.namedParameterJdbcTemplate.getJdbcOperations().update(syntax.toString());
				}
			} catch (Exception e) {
				logger.debug("Exception: ", e);
			}

		}

	}
	
	/**
	 * Method to prepare tableName based on given module,submodule and count
	 * 
	 * 
	 * @param module
	 * @param subModule
	 * @param count
	 * @return if count =0 module_subModule_ED_Temp,<br>
	 *         if count =1 module_subModule_ED, <br>
	 *         if count =2 Adtmodule_subModule_ED,<br>
	 *         if count =3 module_subModule_ED_TV,<br>
	 *         if count =4 VERIFICATION_subModule_TV_Temp, <br>
	 *         if count =5 VERIFICATION_subModule_TV and<br>
	 *         if count =6 AdtVERIFICATION_subModule_TV
	 * 
	 */
	private static String getTableName(String module, String subModule, int count) {
		StringBuilder syntax = new StringBuilder();
		String tableType = "";
		if (count == 0 || count == 4) {
			tableType = "_Temp";
		} else if (count == 2 || count == 6) {
			syntax.append("Adt");
		} else if (count == 3) {
			tableType = "_TV";
		}
		if (count >= 4) {
			module = CollateralConstants.VERIFICATION_MODULE;
		}
		syntax.append(module);
		syntax.append("_");
		syntax.append(subModule);
		if (count < 4) {
			syntax.append("_ED");
		} else if (count >= 4) {
			syntax.append("_TV");
		}
		syntax.append(StringUtils.trimToEmpty(tableType));
		return syntax.toString();
	}
	
	
	/**
	 * Method for Creating table structure for New Module & SubModule combination
	 * @param tableName
	 */
	@Override
	public void dropTable(String module, String subModule){/*
		logger.debug("Entering");

		for (int i = 0; i < 3; i++) {
			String tableType = "";
			if(i == 0){
				tableType = "_Temp";
			}

			//For SQL server
			StringBuilder syntax = new StringBuilder();
			syntax.append("drop table ");
			if(i == 2){
				syntax.append("Adt");
			}
			syntax.append(module);
			syntax.append("_");
			syntax.append(subModule);
			syntax.append("_ED");
			syntax.append(StringUtils.trimToEmpty(tableType));

			try {
				logger.debug("dropsql: " + syntax.toString());
				if(i == 2){// Audit DB
					this.adtNamedParameterJdbcTemplate.getJdbcOperations().update(syntax.toString());
				}else{
					this.namedParameterJdbcTemplate.getJdbcOperations().update(syntax.toString());
				}
			} catch (Exception e) {
				logger.debug("Exception: ", e);
			}

		}
	*/}

}