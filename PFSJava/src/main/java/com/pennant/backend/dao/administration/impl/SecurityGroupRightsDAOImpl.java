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
 *
 * FileName    		: SecurityGroupRightsDAOImpl.java														*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-07-2011															*
 *                                                                  
 * Modified Date    :  10-08-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-08-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.administration.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.administration.SecurityGroupRightsDAO;
import com.pennant.backend.model.administration.SecurityGroup;
import com.pennant.backend.model.administration.SecurityGroupRights;
import com.pennant.backend.model.administration.SecurityRight;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

public class SecurityGroupRightsDAOImpl extends SequenceDao<SecurityGroup> implements SecurityGroupRightsDAO {
	private static Logger logger = LogManager.getLogger(SecurityGroupRightsDAOImpl.class);

	public SecurityGroupRightsDAOImpl() {
		super();
	}
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public SecurityGroupRights getSecurityGroupRights() {
		return new SecurityGroupRights();
	}

	/**
	 * This method selects the {@link List} of {@link SecurityGroupRights} from SecGroupRights
	 * 
	 * @param SecurityGroup
	 *            (SecurityGroup)
	 * @returns {@link List} of {@link SecurityGroupRights}
	 */
	public List<SecurityGroupRights> getSecurityGroupRightsByGrpId(SecurityGroup securityGroup) {
		logger.debug("Entering ");

		StringBuilder selectSql = new StringBuilder("SELECT GrpRightID , GrpID , RightID , ");
		selectSql.append(" Version , LastMntBy , LastMntOn , RecordStatus , RoleCode , ");
		selectSql.append(" NextRoleCode , TaskId , NextTaskId , RecordType , WorkflowId , ");
		selectSql.append(" LovDescGrpCode , LovDescRightName ");
		selectSql.append(" FROM SecGroupRights_Aview where GrpID =:GrpID ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityGroup);
		RowMapper<SecurityGroupRights> typeRowMapper = BeanPropertyRowMapper.newInstance(SecurityGroupRights.class);
		logger.debug("Leaving ");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * This method selects the {@link List} of {@link SecurityGroupRights} from SecGroupRights
	 * 
	 * @param SecurityGroup
	 *            (SecurityGroup)
	 * @returns {@link List} of {@link SecurityGroupRights}
	 */
	public List<SecurityGroupRights> getSecurityGroupRightsByGrpId(long grpId) {

		logger.debug("Entering ");
		Map<String, Long> namedParamters = Collections.singletonMap("GrpID", grpId);
		StringBuilder selectSql = new StringBuilder(
				"SELECT GrpRightID,GrpID,RightID,AccessType,Version,LastMntBy,LastMntOn,RecordStatus,RoleCode,");
		selectSql.append("NextRoleCode,TaskId,NextTaskId,RecordType,WorkflowId, LovDescGrpCode ,");
		selectSql.append("LovDescRightName FROM SecGroupRights_Aview where GrpID =:GrpID");

		logger.debug("selectSql: " + selectSql.toString());

		RowMapper<SecurityGroupRights> typeRowMapper = BeanPropertyRowMapper.newInstance(SecurityGroupRights.class);
		logger.debug("Leaving ");
		return this.jdbcTemplate.query(selectSql.toString(), namedParamters, typeRowMapper);
	}

	/**
	 * This method saves new record into SecGroupRights table
	 * 
	 * @param securityGroupRights
	 *            (SecurityGroupRights)
	 */
	public void save(SecurityGroupRights securityGroupRights) {
		logger.debug("Entering ");

		StringBuilder insertSql = new StringBuilder(" INSERT INTO SecGroupRights ");
		insertSql.append(" (GrpRightID , GrpID , RightID , ");
		insertSql.append(" Version , LastMntBy , LastMntOn, RecordStatus , RoleCode , ");
		insertSql.append(" NextRoleCode , TaskId , NextTaskId , RecordType , WorkflowId ) ");
		insertSql.append(" VALUES (:GrpRightID , :GrpID , :RightID , ");
		insertSql.append(" :Version , :LastMntBy , :LastMntOn , :RecordStatus , :RoleCode , ");
		insertSql.append(" :NextRoleCode , :TaskId , :NextTaskId , :RecordType , :WorkflowId )");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityGroupRights);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving ");

	}

	/**
	 * This method deletes the record from SecGroupRights where GrpID,RightID condition
	 * 
	 * @param securityGroupRights
	 *            (SecurityGroupRights)
	 * @throws DataAccessException
	 */
	public void delete(SecurityGroupRights securityGroupRights) {
		logger.debug("Entering ");

		String deleteSql = "delete from  SecGroupRights where GrpID=:GrpID and RightID=:RightID";
		logger.debug("deleteSql:" + deleteSql);
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityGroupRights);
		try {
			long recordCount = this.jdbcTemplate.update(deleteSql, beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug("Leaving ");
	}

	/**
	 * This method get GroupIds count from SecRoleGroups_view
	 * 
	 * @return int
	 */
	public int getGroupIdCount(long groupId) {
		int status;
		logger.debug("Entering ");
		Map<String, Long> namedParamters = Collections.singletonMap("GrpID", groupId);

		String groupIdCountSql = "SELECT COUNT(*) FROM SecGroupRights_view where GrpID=:GrpID ";
		logger.debug("getGroupIdCountSql: " + groupIdCountSql);

		try {
			status = this.jdbcTemplate.queryForObject(groupIdCountSql, namedParamters, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			status = 0;
		}

		logger.debug("Leaving");
		return status;
	}

	/**
	 * This method get GroupIds count from SecRoleGroups_view
	 * 
	 * @return int
	 */
	public int getRightIdCount(long rightID) {
		int status;
		logger.debug("Entering ");

		Map<String, Long> namedParamters = Collections.singletonMap("RightID", rightID);
		String righIdCountSql = "SELECT COUNT(*) FROM SecGroupRights_view where RightID=:RightID ";
		logger.debug("selectSql: " + righIdCountSql);

		try {
			status = this.jdbcTemplate.queryForObject(righIdCountSql, namedParamters, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			status = 0;
		}

		logger.debug("Leaving ");
		return status;
	}

	/**
	 * This method fetches {@link List} of {@link SecurityRight} records from secRights_View 1) if isAssigned is "true"
	 * fetches assigned rights if isAssigned is "false" fetches unassigned rights
	 * 
	 * @param grpID(long)
	 * @param isAssigned(boolean)
	 * @return {@link List} of {@link SecurityRight}
	 */
	@Override
	public List<SecurityRight> getRightsByGroupId(long grpID, boolean isAssigned) {
		logger.debug("Entering");

		List<SecurityRight> secRightList = new ArrayList<SecurityRight>();
		SecurityGroup secGroups = new SecurityGroup();
		secGroups.setGrpID(grpID);
		String selectSql = "";
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secGroups);
		RowMapper<SecurityRight> typeRowMapper = BeanPropertyRowMapper.newInstance(SecurityRight.class);

		if (isAssigned) {
			selectSql = "select * from secRights_View where rightid in (select rightid from SecGroupRights_view where grpID = :grpID)";
		} else {
			selectSql = "select * from secRights_View where rightid not in (select rightid from SecGroupRights_view where grpID = :grpID)";
		}

		logger.debug("selectSql: " + selectSql);
		secRightList = this.jdbcTemplate.query(selectSql, beanParameters, typeRowMapper);

		logger.debug("Leaving");
		return secRightList;
	}

	/**
	 * This method fetches SecurityGroupRights record from SecGroupRights_Aview with "GrpId And RightId" condition
	 * 
	 * @param grpId(long)
	 * @param rightId(long)
	 * @return secGroupRights(SecurityGroupRights)
	 */
	public SecurityGroupRights getGroupRightsByGrpAndRightIds(long grpId, long rightId) {

		logger.debug("Entering");
		SecurityGroupRights secGroupRights = getSecurityGroupRights();
		secGroupRights.setGrpID(grpId);
		secGroupRights.setRightID(rightId);

		StringBuilder selectSql = new StringBuilder(" SELECT GrpRightID , GrpID , RightID, ");
		selectSql.append(" Version , LastMntBy , LastMntOn , RecordStatus , RoleCode , ");
		selectSql.append(" NextRoleCode , TaskId , NextTaskId , RecordType , WorkflowId , ");
		selectSql.append(" LovDescGrpCode , LovDescRightName ");
		selectSql.append(" FROM SecGroupRights_AView where GrpID =:GrpID and RightID=:RightID ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secGroupRights);
		RowMapper<SecurityGroupRights> typeRowMapper = BeanPropertyRowMapper.newInstance(SecurityGroupRights.class);

		try {
			secGroupRights = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			secGroupRights = null;
		}
		logger.debug("Leaving ");
		return secGroupRights;
	}

	@Override
	public long getNextValue() {
		return getNextValue("SeqSecGroupRights");
	}
}
