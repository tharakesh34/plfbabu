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
 * FileName    		:  SubSectorDAOImpl.java                                                   * 	  
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
import com.pennant.backend.dao.systemmasters.SubSectorDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.systemmasters.SubSector;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>SubSector model</b> class.<br>
 * 
 */
public class SubSectorDAOImpl extends BasisCodeDAO<SubSector> implements SubSectorDAO {

	private static Logger logger = Logger.getLogger(SubSectorDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new SubSector
	 * 
	 * @return SubSector
	 */
	@Override
	public SubSector getSubSector() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("SubSector");
		SubSector subSector = new SubSector();
		if (workFlowDetails != null) {
			subSector.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return subSector;
	}

	/**
	 * This method get the module from method getSubSector() and set the new
	 * record flag as true and return SubSector()
	 * 
	 * @return SubSector
	 */
	@Override
	public SubSector getNewSubSector() {
		logger.debug("Entering");
		SubSector subSector = getSubSector();
		subSector.setNewRecord(true);
		logger.debug("Leaving");
		return subSector;
	}

	/**
	 * Fetch the Record Sub Sectors details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return SubSector
	 */
	@Override
	public SubSector getSubSectorById(final String id, String subSectorCode,String type) {
		logger.debug("Entering");
		SubSector subSector = new SubSector();
		subSector.setId(id);
		subSector.setSubSectorCode(subSectorCode);
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append("SELECT SectorCode, SubSectorCode, SubSectorDesc, SubSectorIsActive,");
		if(type.contains("View")){
			selectSql.append("lovDescSectorCodeName,");
		}
		selectSql.append(" Version, LastMntBy , LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" From BMTSubSectors");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where SectorCode=:sectorCode AND SubSectorCode=:subSectorCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(subSector);
		RowMapper<SubSector> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SubSector.class);

		try {
			subSector = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			subSector = null;
		}
		logger.debug("Leaving");
		return subSector;
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param SubSector
	 *            (subSector)
	 * @return SubSector
	 */
	@Override
	public void initialize(SubSector subSector) {
		super.initialize(subSector);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param SubSector
	 *            (subSector)
	 * @return void
	 */
	@Override
	public void refresh(SubSector subSector) {

	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BMTSubSectors or
	 * BMTSubSectors_Temp. if Record not deleted then throws DataAccessException
	 * with error 41003. delete Sub Sectors by key SectorCode
	 * 
	 * @param Sub
	 *            Sectors (subSector)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(SubSector subSector, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();
		
		deleteSql.append(" Delete From BMTSubSectors");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where SectorCode =:SectorCode AND SubSectorCode=:subSectorCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(subSector);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41004",subSector.getSectorCode(),subSector.getSubSectorCode(), 
					subSector.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.debug("Error in delete Method");
			logger.error(e);
			ErrorDetails errorDetails = getError("41006",subSector.getSectorCode(),subSector.getSubSectorCode(), 
					subSector.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BMTSubSectors or BMTSubSectors_Temp.
	 * 
	 * save Sub Sectors
	 * 
	 * @param Sub
	 *            Sectors (subSector)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void save(SubSector subSector, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();
		
		insertSql.append("Insert Into BMTSubSectors");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (SectorCode, SubSectorCode, SubSectorDesc, SubSectorIsActive,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:SectorCode, :SubSectorCode, :SubSectorDesc, :SubSectorIsActive,"); 
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(subSector);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method updates the Record BMTSubSectors or BMTSubSectors_Temp. if
	 * Record not updated then throws DataAccessException with error 41004.
	 * update Sub Sectors by key SectorCode and Version
	 * 
	 * @param Sub
	 *            Sectors (subSector)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(SubSector subSector, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();
		
		updateSql.append("Update BMTSubSectors");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set SectorCode = :SectorCode, SubSectorCode = :SubSectorCode, SubSectorDesc = :SubSectorDesc,");
		updateSql.append(" SubSectorIsActive = :SubSectorIsActive,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where SectorCode =:SectorCode AND SubSectorCode=:subSectorCode");
		if (!type.endsWith("_TEMP")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(subSector);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);

			ErrorDetails errorDetails = getError("41003",subSector.getSectorCode(),subSector.getSubSectorCode(), subSector.getUserDetails()
					.getUsrLanguage());
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
	private ErrorDetails  getError(String errorId, String sectorCode,String subSectorCode, String userLanguage){
		String[][] parms= new String[2][2]; 
		parms[1][0] = String.valueOf(sectorCode);
		parms[1][1] = subSectorCode;

		parms[0][0] = PennantJavaUtil.getLabel("label_SectorCode")+ ":" + parms[1][0];
		parms[0][1] = PennantJavaUtil.getLabel("label_SubSectorCode")+ ":" + parms[1][1];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

}