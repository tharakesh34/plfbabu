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
 * FileName    		:  SecurityRightDAOImpl.java												*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  2-08-2011 														*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 2-08-2011       Pennant	                 0.1                                            * 
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

import java.util.List;

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
import com.pennant.backend.dao.administration.SecurityRightDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.administration.SecurityRight;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

public class SecurityRightDAOImpl extends BasisNextidDaoImpl<SecurityRight> implements SecurityRightDAO {

	private static Logger logger = Logger.getLogger(SecurityRightDAOImpl.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the new SecurityRight 
	 * @return SecurityRight
	 */
	@Override
	public SecurityRight getSecurityRight() {
		logger.debug("Entering ");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("SecurityRight");
		SecurityRight secRight= new SecurityRight();
		if (workFlowDetails!=null){
			secRight.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving ");
		return secRight;
	}

	/**
	 * This method get the module from method getSecurityRight() and 
	 * set the new record flag as true and return SecurityRight()   
	 * @return SecurityRight
	 */
	@Override
	public SecurityRight getNewSecurityRight() {
		logger.debug("Entering ");
		SecurityRight secRight = getSecurityRight();
		secRight.setNewRecord(true);
		logger.debug("Leaving ");
		return secRight;
	}

	/**
	 * Fetch the Record  Security Rights details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return SecurityRight
	 */
	@Override
	public SecurityRight getSecurityRightByID(final long id, String type) {
		logger.debug("Entering ");
		SecurityRight secRight = getSecurityRight();
		secRight.setId(id);

		StringBuilder   selectSql = new StringBuilder  ("Select RightID, RightType, RightName, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, " );
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" From SecRights");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where RightID =:RightID");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secRight);
		RowMapper<SecurityRight> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityRight.class);

		try{
			secRight = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			secRight = null;
		}
		logger.debug("Leaving ");
		return secRight;
	}

	/**
	 * Fetch the Record  Security Rights details by key field
	 * 
	 * @param rightName (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return SecurityRight
	 */
	@Override
	public SecurityRight getSecurityRightByRightName(final String rightName,String type) {
		logger.debug("Entering ");
		SecurityRight secRight = getSecurityRight();
		secRight.setRightName(rightName);

		StringBuilder   selectSql = new StringBuilder  ("Select RightID, RightType, RightName, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode," );
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" From SecRights");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where RightName =:RightName");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secRight);
		RowMapper<SecurityRight> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				SecurityRight.class);

		try{
			secRight = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), 
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			secRight = null;
		}
		logger.debug("Leaving ");
		return secRight;
	}

	/**
	 * This method initialise the Record.
	 * @param SecurityRight (secRight)
	 * @return SecurityRight
	 */
	@Override
	public void initialize(SecurityRight secRight) {
		super.initialize(secRight);
	}
	/**
	 * This method refresh the Record.
	 * @param SecurityRight (secRight)
	 * @return void
	 */
	@Override
	public void refresh(SecurityRight secRight) {

	}

	public List<SecurityRight> getAllRights(int type) {
		return null;
	}

	public List<SecurityRight> getAllRights() {
		return null;
	}

	public int getCountAllSecurityRights() {
		return 0;
	}

	public SecurityRight getRightById(Long rightId) {
		return null;
	}
	/**
	 * This method selects List<SecurityRight> from UserRights_View with usrID condition.
	 * @param user (SecUser)
	 * @return List<SecurityRight> 
	 */
	public List<SecurityRight> getRightsByUser(SecurityUser user) {
		logger.debug("Entering ");
		String selectSql = 	" Select distinct RightID, RightType, RightName, Version, UsrID , AppCode from UserRights_View  where UsrID = :UsrID";

		logger.debug("selectSql: " + selectSql);
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(user);
		RowMapper<SecurityRight> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityRight.class);
		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.query(selectSql, beanParameters,typeRowMapper);
	}

	/**
	 * This method updates the Record SecRights or SecRights_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Security Rights by key RightID and Version
	 * 
	 * @param Security Rights (secRight)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(SecurityRight secRight,String type) {
		logger.debug("Entering ");
		int recordCount = 0;
		
		StringBuilder   updateSql = new StringBuilder("Update SecRights");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set RightID = :RightID, RightType = :RightType, RightName = :RightName, " );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, ");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId " );
		updateSql.append(" Where RightID =:RightID ");

		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		logger.debug("updateSql:" + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secRight);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetail= getError("41004",secRight.getRightName(),secRight.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetail.getError()) {};
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method Deletes the Record from the SecRights or SecRights_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Security Rights by key RightID
	 * 
	 * @param Security Rights (secRight)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(SecurityRight secRight,String type) {
		logger.debug("Entering ");
		int recordCount = 0;
		
		StringBuilder   deleteSql =new StringBuilder("Delete From SecRights"); 
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where RightID =:RightID");
		logger.debug("deleteSql:" + deleteSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secRight);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				
				ErrorDetails errorDetail= getError("41003",secRight.getRightName(),
						secRight.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetail.getError()) {};
			}
		}catch(DataAccessException e){
			logger.debug("Error delete Method");
			ErrorDetails errorDetail= getError("41006",secRight.getRightName(),
					secRight.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetail.getError()) {};
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method insert new Records into SecRights or SecRights_Temp.
	 * it fetches the available Sequence form SeqSecRights by using 
	 * getNextidviewDAO().getNextId() method.  
	 *
	 * save Security Rights 
	 * 
	 * @param Security Rights (secRight)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(SecurityRight secRight,String type) {
		logger.debug("Entering ");
		if (secRight.getId()==Long.MIN_VALUE){
			secRight.setId(getNextidviewDAO().getNextId("SeqSecRights"));
			logger.debug("get NextID:"+secRight.getId());
		}

		
		StringBuilder  insertSql = 	new StringBuilder("Insert Into SecRights");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (RightID, RightType, RightName," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode,");
		insertSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:RightID, :RightType, :RightName, " );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, " );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql:" + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secRight);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving ");
		return secRight.getId();
	}
	
	/**
	 * Setting DataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This Method selects all MenuRights from UserRights_View 
	 * @param user (SecUser)
	 * @return List<SecurityRight>
	 */
	@Override
	public List<SecurityRight> getMenuRightsByUser(SecurityUser user) {
		logger.debug("Entering ");
		
		StringBuilder   selectSql = new StringBuilder  (" Select distinct RightID, RightType, RightName, " );
		selectSql.append(" Version , UsrID , AppCode from UserRights_View ");
		selectSql.append(" where RightType=0  and UsrID = :UsrID and AppCode=:loginAppCode");
		logger.debug("selectSql:" + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(user);
		RowMapper<SecurityRight> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				SecurityRight.class);
		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
	}
	
	/**
	 * This method selects all PageRights from  UserRights_View
	 * @param secRight (SecurityRight)
	 * @return List<SecurityRight>
	 */
	@Override
	public List<SecurityRight> getPageRights(SecurityRight secRight) {
		logger.debug("Entering ");
		StringBuilder   selectSql = new StringBuilder  (" Select distinct RightID, RightType, RightName, " );
		selectSql.append(" Version , UsrID , AppCode from UserRights_View ");
		selectSql.append(" WHERE RightType <> 0  and UsrID = :UsrID and  AppCode=:loginAppCode");
		selectSql.append(" and RightName like '%");
		selectSql.append(secRight.getRightName());
		selectSql.append("%' ");
		logger.debug("selectSql:" + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secRight);
		RowMapper<SecurityRight> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				SecurityRight.class);
		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
	}
	/**
	 *  This method selects all RoleRights from SecRolesRights_View
	 *  @param secRight (SecurityRight)
	 *  @return List<SecurityRight>
	 */
	@Override
	public List<SecurityRight> getRoleRights(SecurityRight secRight) {
		logger.debug("Entering ");
		
		StringBuilder   selectSql = new StringBuilder  (" Select distinct RoleCd, GrpCode, " );
		selectSql.append(" RightName , RightType , AppCode , UsrID " );
		selectSql.append(" FROM SecRolesRights_View ");
		selectSql.append(" where RightType=3 and RoleCd = :RoleCd and  AppCode=:loginAppCode");
		selectSql.append(" and RightName like '%");
		selectSql.append(secRight.getRightName());
		selectSql.append("%' AND UsrId=:UsrID");
		logger.debug("selectSql:" + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secRight);
		RowMapper<SecurityRight> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityRight.class);
		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
	}
	
	@Override
	public List<SecurityRight> getRoleRights(SecurityRight secRight,String[] roles) {
		logger.debug("Entering ");
		
		String userRoles = "";
		
		for (int i = 0; i < roles.length; i++) {
			if(i<(roles.length-1)){
				userRoles = userRoles.concat("'"+roles[i]+"',");	
			}else{
				userRoles = userRoles.concat("'"+roles[i]+"'");
			}
		} 
		
		StringBuilder   selectSql = new StringBuilder  (" Select distinct RoleCd, GrpCode, " );
		selectSql.append(" RightName , RightType , AppCode , UsrID from SecRolesRights_View ");
		selectSql.append(" where RightType=3 and RoleCd In (");
		selectSql.append(userRoles);
		selectSql.append(")  and  AppCode=:loginAppCode");
		selectSql.append(" and RightName like '%");
		selectSql.append(secRight.getRightName());
		selectSql.append("%' AND UsrId=:UsrID");
		logger.debug("selectSql:" + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secRight);
		RowMapper<SecurityRight> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				SecurityRight.class);
		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
	}
	
	public List<SecurityRight> getAllRights(List<Integer> aListOfRightTyps) {
		return null;
	}

	public List<SecurityRight> getRightsLikeRightName(String aRightName) {
		return null;
	}

	public List<SecurityRight> getRightsLikeRightNameAndType(String aRightName,
			int aRightType) {
		return null;
	}

	public List<SecurityRight> getRightsLikeRightNameAndTypes(String aRightName,
			List<Integer> listOfRightTyps) {
		return null;
	}
	
	private ErrorDetails  getError(String errorId, String rightName, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = rightName;
		parms[0][1] = PennantJavaUtil.getLabel("label_RightName");
		
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, 
				errorId, parms[0],parms[1]), userLanguage);
	}

}
