/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software ,  unless 
 * otherwise stated ,  the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials ,  in whole or in part ,  in any manner ,  
 * without the prior written consent of the copyright holder ,  is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		: SecurityUserRolesDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-06-2011    														*
 *                                                                  						*
 * Modified Date    :  10-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *  10-08-2011       Pennant	                 0.1                                            * 
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

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.administration.SecurityUserRolesDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.administration.SecurityUserRoles;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class SecurityUserRolesDAOImpl extends BasisNextidDaoImpl<SecurityUser> implements SecurityUserRolesDAO{

	private static Logger logger = Logger.getLogger(SecurityUserRolesDAOImpl .class);
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method returns new SecurityUserRoles Object
	 */
	public SecurityUserRoles getSecurityUserRoles(){
		logger.debug("Entering ");
		return new SecurityUserRoles();
	}
	/**
	 * @param dataSource the dataSource to set
	 */

	public void setDataSource(DataSource dataSource) {	
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This Method selects the records from UserRoles_AView table with UsrID condition
	 * @param secuser(SecUser)
	 * @return List<SecurityUserRoles>
	 */
	public List<SecurityUserRoles> getSecUserRolesByUsrID(SecurityUser secUser){
		logger.debug("Entering ");
		StringBuilder  selectSql = new StringBuilder (" SELECT UsrRoleID , UsrID , RoleID , " );
		selectSql.append(" Version , LastMntBy , LastMntOn , RecordStatus, ");
		selectSql.append(" RoleCode , NextRoleCode , TaskId , RecordType , WorkflowId , " );
		selectSql.append(" lovDescUserLogin , lovDescRoleCd " );
		selectSql.append(" FROM UserRoles_AView where UsrID=:UsrID");
		logger.debug("selectSql : " + selectSql.toString());      
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secUser);
		RowMapper< SecurityUserRoles> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				SecurityUserRoles.class);
		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.query(selectSql.toString() , 
				beanParameters , typeRowMapper);
	}
	/**
	 * This Method selects the records from UserRoles_AView table with UsrIDand RoleID condition
	 * @param userId (long)
	 * @param roleId (long)
	 * @return secUserRoles (SecurityUserRoles)
	 */
	public SecurityUserRoles getUserRolesByUsrAndRoleIds(long userId , long roleId){

		logger.debug("Entering ");
		SecurityUserRoles secUserRoles =getSecurityUserRoles();
		secUserRoles.setUsrID(userId);
		secUserRoles.setRoleID(roleId);
		
		StringBuilder  selectSql = new StringBuilder (" SELECT  UsrRoleID , UsrID , RoleID , " );
		selectSql.append(" Version , LastMntBy , LastMntOn , RecordStatus , RoleCode , " );
		selectSql.append(" NextRoleCode , TaskId , RecordType , WorkflowId ");
		selectSql.append(" FROM SecUserRoles where UsrID=:UsrID and RoleID=:RoleID ");
		logger.debug("selectSql : " + selectSql.toString());      
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secUserRoles);
		RowMapper< SecurityUserRoles> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				SecurityUserRoles.class);

		try {
			secUserRoles = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString() ,  
					beanParameters ,  typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			secUserRoles = null;
		}
		logger.debug("Leaving ");
		return secUserRoles;	

	}

	/**
	 * This method deletes the record from SecUserRoles with UsrID and RoleID condition
	 * @param securityUserRoles(SecurityUserRoles)
	 * @throws DataAccessException
	 */
	@SuppressWarnings("serial")
	public void  delete(SecurityUserRoles securityUserRoles ){
		logger.debug("Entering ");
		int recordCount = 0;
		StringBuilder  deleteSql = new StringBuilder ("Delete from SecUserRoles  " );
		deleteSql.append(" where UsrID=:UsrID and RoleID =:RoleID");
		logger.debug("deleteSql:"+deleteSql);
		
		try{
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityUserRoles);
			recordCount = this.namedParameterJdbcTemplate.update( deleteSql.toString() , beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails=getError("41004" ,  securityUserRoles.getLovDescUserLogin()
						 , securityUserRoles.getLovDescRoleCd() ,  
						 securityUserRoles.getUserDetails().getUsrLanguage()); 
				throw new DataAccessException(errorDetails.getError()){};

			}
		}catch(DataAccessException e){
			logger.debug("error in while deleting  UserRoles"+e.toString());
			ErrorDetails errorDetails=getError("41006" ,  securityUserRoles.getLovDescUserLogin()
					 , securityUserRoles.getLovDescRoleCd() ,  
					 securityUserRoles.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving ");
	}  

	/**
	 * This method inserts new record into SecUserRoles table 
	 * @param  securityUserRoles(SecurityUserRoles)
	 */
	public void  save( SecurityUserRoles  securityUserRoles){
		logger.debug("Entering ");

		if ( (securityUserRoles.getId()==Long.MIN_VALUE)){
			logger.debug(Long.MIN_VALUE);
			securityUserRoles.setId(getNextidviewDAO().getNextId("SeqSecUserRoles"));
			logger.debug("get NextID:"+ securityUserRoles.getId());
		}
		
		StringBuilder  insertSql = new StringBuilder ("INSERT INTO SecUserRoles " );
		insertSql.append(" ( UsrRoleID , UsrID , RoleID , " );
		insertSql.append(" Version , LastMntBy , LastMntOn , RecordStatus , RoleCode , " );
		insertSql.append(" NextRoleCode , TaskId , RecordType , WorkflowId) " );
		insertSql.append(" VALUES(:UsrRoleID , :UsrID , :RoleID , " );
		insertSql.append(" :Version , :LastMntBy , :LastMntOn , :RecordStatus , :RoleCode, ");
		insertSql.append(" :NextRoleCode , :TaskId , :RecordType , :WorkflowId) ");
		
		logger.debug("insertSql:"+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityUserRoles);
		this.namedParameterJdbcTemplate.update(insertSql.toString() ,  beanParameters);
		logger.debug("Leaving ");
	}

	/**
	 * This method get  RoleIds count from UserRoles_View
	 * @param RoleId (long)
	 * @return List<Long RoleIDs>
	 */
	public int getRoleIdCount(long RoleId){
		int status;
		logger.debug("Entering ");
		Map<String ,  Long> namedParamters=Collections.singletonMap("RoleId" ,  RoleId);
		StringBuilder  selectSql = new StringBuilder ("SELECT COUNT(*) FROM UserRoles_View " );
		selectSql.append(" where RoleId=:RoleId ");
		logger.debug("selectSql: " + selectSql.toString());      

		try{
			status=this.namedParameterJdbcTemplate.queryForInt(selectSql.toString() ,  namedParamters);
		}catch (EmptyResultDataAccessException e) {
			status=0;
		}

		logger.debug("Leaving getRoleIdCount()");
		return status;
	}
	
	/**
	 * This method get  UserId count from UserRoles_View
	 * @param RoleId (long)
	 * @return List<Long RoleIDs>
	 */
	public int getUserIdCount(long userId){
		int status;
		logger.debug("Entering ");
		Map<String ,  Long> namedParamters=Collections.singletonMap("UsrID" ,  userId);
		StringBuilder  selectSql = new StringBuilder ("SELECT COUNT(*) FROM UserRoles_View " );
		selectSql.append(" where UsrID=:UsrID ");
		logger.debug("selectSql: " + selectSql.toString());      

		try{
			status=this.namedParameterJdbcTemplate.queryForInt(selectSql.toString() ,  namedParamters);
		}catch (EmptyResultDataAccessException e) {
			status=0;
		}

		logger.debug("Leaving getRoleIdCount()");
		return status;
	}
	
	/**
	 * This method fetches the records from SecRoles_View 
	 *      a) if isAssigned is "true"  fetches assigned roles from SecRoles_View
	 *      b) if isAssigned is "false" fetches unassigned roles from SecRoles_View
	 *  @param userId (long)
	 *  @param isAssigned (boolean)
	 *  @return SecurityRoleList (ArrayList)
	 *  
	 */
	@Override
	public  List<SecurityRole> getRolesByUserId(long userId , boolean isAssigned){
		logger.debug("Entering ");
		
		List< SecurityRole>  secRolesList=new ArrayList< SecurityRole>();
		SecurityUser user = new SecurityUser ();
		user.setUsrID(userId);
		StringBuilder  selectSql = new StringBuilder ();
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(user);
		RowMapper< SecurityRole> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				SecurityRole.class);
		
		if(isAssigned){
			selectSql.append("select * from SecRoles_View where RoleID in");
			selectSql.append(" (select RoleID from UserRoles_AView where UsrID = :UsrID)");
		}
		else{
			selectSql.append("select * from SecRoles_View where RoleID not in");
			selectSql.append(" (select RoleID from UserRoles_AView where UsrID = :UsrID)");	
		}
		logger.debug("selectSql:"+selectSql);
		secRolesList = this.namedParameterJdbcTemplate.query(selectSql.toString() , 
				beanParameters , typeRowMapper);
		logger.debug("Leaving ");
		return secRolesList;
	}

	private ErrorDetails  getError(String errorId ,  String userlogin ,  String roleCode ,
			String userLanguage){
		String[][] parms= new String[2][2]; 
		
		parms[1][0] = userlogin;
		parms[1][1] = roleCode;
		parms[0][0] = PennantJavaUtil.getLabel("label_UsrLogin")+parms[1][0];
		parms[0][1] = PennantJavaUtil.getLabel("label_RoleCd")+parms[1][1];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD ,
				errorId ,  parms[0] , parms[1]) ,  userLanguage);
	}
}