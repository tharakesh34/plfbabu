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
 * FileName    		:  VasMovementDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-12-2011    														*
 *                                                                  						*
 * Modified Date    :  12-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-12-2011       Pennant	                 0.1                                            * 
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.applicationmaster.VasMovementDAO;
import com.pennant.backend.model.finance.VasMovement;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

import oracle.net.aso.m;

/**
 * DAO methods implementation for the <b>VasMovement model</b> class.<br>
 * 
 */
public class VasMovementDAOImpl extends SequenceDao<VasMovement> implements VasMovementDAO {
	private static Logger logger = Logger.getLogger(VasMovementDAOImpl.class);

	public VasMovementDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Check List details by key field
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return VasMovement
	 */
	@Override
	public VasMovement getVasMovementById(final String finreference, String type) {
		logger.debug("Entering");

		VasMovement vasMovement = new VasMovement();
		vasMovement.setFinReference(finreference);

		StringBuilder selectSql = new StringBuilder("");
		selectSql.append(" Select vasmovementid,finreference,Version , LastMntBy, LastMntOn, ");
		selectSql.append(" RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",finamount,finstartdate,maturitydate,CustCif,fintype  ");
		}
		selectSql.append(" From VasMovement");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where finReference =:finReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vasMovement);
		RowMapper<VasMovement> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(VasMovement.class);

		try {
			vasMovement = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			vasMovement = null;
		}
		logger.debug("Leaving");
		return vasMovement;
	}

	/**
	 * This method Deletes the Record from the BMTVasMovement or BMTVasMovement_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Check List by key VasMovementId
	 * 
	 * @param Check
	 *            List (vasMovement)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(VasMovement vasMovement, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From VasMovement");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where finReference =:finReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		MapSqlParameterSource mapSql = new MapSqlParameterSource();
		mapSql.addValue("finReference", vasMovement.getFinReference());

		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), mapSql);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BMTVasMovement or BMTVasMovement_Temp. it fetches the available Sequence form
	 * SeqBMTVasMovement by using getNextidviewDAO().getNextId() method.
	 *
	 * save Check List
	 * 
	 * @param Check
	 *            List (vasMovement)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(VasMovement vasMovement, String type) {
		logger.debug("Entering");
		if (vasMovement.getId() <= 0) {
			vasMovement.setId(getNextId("SeqBMTCheckList"));
			logger.debug("get NextID:" + vasMovement.getId());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into VasMovement");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (VasMovementId,FinReference,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:VasMovementId,:FinReference,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vasMovement);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return vasMovement.getId();
	}

	/**
	 * This method updates the Record BMTVasMovement or BMTVasMovement_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Check List by key VasMovementId and Version
	 * 
	 * @param Check
	 *            List (vasMovement)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(VasMovement vasMovement, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update VasMovement");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(
				" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinReference =:FinReference");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vasMovement);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
}