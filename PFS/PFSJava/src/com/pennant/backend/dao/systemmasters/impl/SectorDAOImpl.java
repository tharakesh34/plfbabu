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
 * FileName    		:  SectorDAOImpl.java                                                   * 	  
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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.systemmasters.SectorDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.systemmasters.Sector;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>Sector model</b> class.<br>
 * 
 */
public class SectorDAOImpl extends BasisCodeDAO<Sector> implements SectorDAO {

	private static Logger logger = Logger.getLogger(SectorDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new Sector
	 * 
	 * @return Sector
	 */
	@Override
	public Sector getSector() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Sector");
		Sector sector = new Sector();
		if (workFlowDetails != null) {
			sector.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return sector;
	}

	/**
	 * This method get the module from method getSector() and set the new record
	 * flag as true and return Sector()
	 * 
	 * @return Sector
	 */
	@Override
	public Sector getNewSector() {
		logger.debug("Entering");
		Sector sector = getSector();
		sector.setNewRecord(true);
		logger.debug("Leaving");
		return sector;
	}

	/**
	 * Fetch the Record Sectors details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Sector
	 */
	@Override
	public Sector getSectorById(final String id, String type) {
		logger.debug("Entering");
		Sector sector = getSector();
		sector.setId(id);
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append("Select SectorCode, SectorDesc, SectorLimit, SectorIsActive,");
		/*if(type.contains("View")){
			selectSql.append("");
		}*/
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From BMTSectors");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where SectorCode =:SectorCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sector);
		RowMapper<Sector> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Sector.class);
		try {
			sector = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			sector = null;
		}
		logger.debug("Leaving");
		return sector;
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param Sector
	 *            (sector)
	 * @return Sector
	 */
	@Override
	public void initialize(Sector sector) {
		super.initialize(sector);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param Sector
	 *            (sector)
	 * @return void
	 */
	@Override
	public void refresh(Sector sector) {

	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BMTSectors or BMTSectors_Temp. if
	 * Record not deleted then throws DataAccessException with error 41003.
	 * delete Sectors by key SectorCode
	 * 
	 * @param Sectors
	 *            (sector)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(Sector sector, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();
		
		deleteSql.append("Delete From BMTSectors");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where SectorCode =:SectorCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sector);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41004",sector.getSectorCode(), sector.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.debug("Error in delete Method");
			logger.error(e);
			ErrorDetails errorDetails = getError("41006",sector.getSectorCode(), sector.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BMTSectors or BMTSectors_Temp.
	 * 
	 * save Sectors
	 * 
	 * @param Sectors
	 *            (sector)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(Sector sector, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into BMTSectors");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (SectorCode, SectorDesc, SectorLimit, SectorIsActive,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:SectorCode, :SectorDesc, :SectorLimit, :SectorIsActive,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sector);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return sector.getId();
	}

	/**
	 * This method updates the Record BMTSectors or BMTSectors_Temp. if Record
	 * not updated then throws DataAccessException with error 41004. update
	 * Sectors by key SectorCode and Version
	 * 
	 * @param Sectors
	 *            (sector)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(Sector sector, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();
		
		updateSql.append("Update BMTSectors");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set SectorCode = :SectorCode, SectorDesc = :SectorDesc, SectorLimit = :SectorLimit,");
		updateSql.append(" SectorIsActive = :SectorIsActive,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where SectorCode =:SectorCode");
		if (!type.endsWith("_TEMP")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sector);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);

			ErrorDetails errorDetails = getError("41003",sector.getSectorCode(), sector.getUserDetails().getUsrLanguage());
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
	private ErrorDetails  getError(String errorId, String sectorCode, String userLanguage){
		String[][] parms= new String[2][2]; 
		parms[1][0] = String.valueOf(sectorCode);
		parms[0][0] = PennantJavaUtil.getLabel("label_Sector_Code")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

}