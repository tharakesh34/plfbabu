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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.administration.SecurityRightDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.administration.SecurityRight;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennanttech.pff.core.Literal;

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
	public List<SecurityRight> getPageRights(SecurityRight secRight) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("select distinct RT.RightName ");
		sql.append("  from SecUserOperations UO ");
		sql.append("  inner join SecOperationRoles OPR on OPR.OprID = UO.OprID ");
		sql.append("  inner join SecRoles R on R.RoleID = OPR.RoleID ");
		sql.append("  inner join SecRoleGroups RG on RG.RoleID = R.RoleID ");
		sql.append("  inner join SecGroupRights GR on GR.GrpID = RG.GrpID ");
		sql.append("  inner join SecRights RT on RT.RightID = GR.RightID ");
		sql.append("  where RT.RightType <> 0  and UO.UsrID = :UsrID and  R.RoleApp = :LoginAppId ");

		if (StringUtils.isNotBlank(secRight.getRoleCd())) {
			sql.append("  and R.RoleCd = :RoleCd ");
		}

		sql.append("  and RT.Page = :Page ");

		if (StringUtils.isNotBlank(secRight.getMenuRight())) {
			sql.append(" and GR.GrpID in (select TGR.GrpID from SecGroupRights TGR");
			sql.append(" inner join SecRights TR on TR.RightID = TGR.RightID");
			sql.append(" where TR.RightName = :MenuRight )");
		}

		logger.debug(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(secRight);
		RowMapper<SecurityRight> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityRight.class);

		logger.debug(Literal.LEAVING);
		return this.namedParameterJdbcTemplate.query(sql.toString(), paramSource, rowMapper);
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
