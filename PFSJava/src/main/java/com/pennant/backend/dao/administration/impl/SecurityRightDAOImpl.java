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
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.administration.SecurityRightDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.administration.SecurityRight;
import com.pennant.backend.model.administration.SecurityUser;

public class SecurityRightDAOImpl extends BasisNextidDaoImpl<SecurityRight> implements SecurityRightDAO {
	private static Logger logger = Logger.getLogger(SecurityRightDAOImpl.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public SecurityRightDAOImpl() {
		super();
	}

	

	/**
	 * This Method selects all MenuRights by UserId
	 * 
	 * @param user
	 *            (SecUser)
	 * @return {@link List} of {@link SecurityRight}
	 */
	@Override
	public List<SecurityRight> getMenuRightsByUser(SecurityUser user) {
		logger.debug("Entering ");

		StringBuilder selectSql = new StringBuilder(" Select distinct RightName ");
		selectSql.append(" from UserRights_View ");
		selectSql.append(" where RightType=0  and UsrID = :UsrID and loginAppId=:loginAppId");
		logger.debug("selectSql:" + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(user);
		RowMapper<SecurityRight> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityRight.class);
		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * This method selects all PageRights by userId
	 * 
	 * @param secRight
	 *            (SecurityRight)
	 * @return {@link List} of {@link SecurityRight}
	 */
	@Override
	public List<SecurityRight> getPageRights(SecurityRight secRight, String menuRightName) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder("select distinct RT.RightName ");
		sql.append("  from SecUsers U ");
		sql.append("  inner join SecUserOperations UO on UO.UsrID = U.UsrID ");
		sql.append("  inner join SecOperationRoles OPR on OPR.OprID = UO.OprID ");
		sql.append("  inner join SecRoles R on R.RoleID = OPR.RoleID ");
		sql.append("  inner join SecRoleGroups RG on RG.RoleID = R.RoleID ");
		sql.append("  inner join SecGroups G on G.GrpID = RG.GrpID ");
		sql.append("  inner join SecGroupRights GR on GR.GrpID = G.GrpID ");
		sql.append("  inner join SecRights RT on RT.RightID = GR.RightID ");
		sql.append("  inner join PTApplicationDetails A on A.AppID = R.RoleApp ");
		sql.append("  where RT.RightType <> 0  and U.UsrID = :UsrID and  A.AppCode = :loginAppCode ");
		
		if (StringUtils.isNotBlank(secRight.getRoleCd())) {
			sql.append("  and R.RoleCd = :RoleCd ");
		}
		
		sql.append("  and RT.Page = :Page ");

		if (StringUtils.isNotBlank(menuRightName)) {
			sql.append(" and G.GrpCode in (select GrpCode from SecGroupRights_View ");
			sql.append(" where RightName = '");
			sql.append(menuRightName);
			sql.append("')");
		}

		logger.debug("selectSql:" + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secRight);
		RowMapper<SecurityRight> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityRight.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * This method gets the rights for the specified user, dialog page, and the role.
	 * 
	 * @param secRight
	 *            (SecurityRight)
	 * @return {@link List} of {@link SecurityRight}
	 */
	@Override
	public List<SecurityRight> getRoleRights(SecurityRight secRight, String menuRightName) {
		logger.debug("Entering ");

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("RightType", "3");
		paramSource.addValue("RoleCd", secRight.getRoleCd());
		paramSource.addValue("AppId", secRight.getLoginAppId());
		paramSource.addValue("Page", secRight.getPage());
		paramSource.addValue("UsrId", secRight.getUsrID());
		paramSource.addValue("RightName", menuRightName);

		StringBuilder sql = new StringBuilder(" select distinct RightName ");
		sql.append(" FROM SecRolesRights_View ");
		sql.append(" where RightType = :RightType and RoleCd = :RoleCd and AppId = :AppId ");
		sql.append(" and Page = :Page AND UsrId = :UsrId ");

		// Checking Rights Based on Menu Right Item
		if (StringUtils.isNotBlank(menuRightName)) {
			sql.append(" AND GrpID IN (select GrpID from SecGroupRights_View ");
			sql.append(" WHERE RightName = :RightName) ");
		}

		logger.debug("selectSql:" + sql.toString());
		RowMapper<SecurityRight> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityRight.class);

		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.query(sql.toString(), paramSource, typeRowMapper);
	}

	@Override
	public List<SecurityRight> getRoleRights(SecurityRight secRight, String[] roles) {
		logger.debug("Entering ");

		StringBuilder sql = new StringBuilder(" select distinct RightName ");
		sql.append(" from SecRolesRights_View ");
		sql.append(" where RightType= :RightType and RoleCd In (:RoleCd) and ");
		sql.append(" AppId = :loginAppId and Page = :Page AND UsrId = :UsrID");

		logger.debug("selectSql:" + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secRight);
		RowMapper<SecurityRight> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityRight.class);

		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Setting DataSource
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
}
