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
	private static Logger				logger	= Logger.getLogger(SecurityRightDAOImpl.class);

	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public SecurityRightDAOImpl() {
		super();
	}

	@Override
	public List<SecurityRight> getMenuRightsByUser(SecurityUser user) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("select distinct RightName");
		sql.append(" from UserRights_View");
		sql.append(" where RightType = 0 and UsrID = :UsrID and loginAppId = :loginAppId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(user);
		RowMapper<SecurityRight> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityRight.class);

		logger.debug(Literal.LEAVING);
		return namedParameterJdbcTemplate.query(sql.toString(), paramSource, rowMapper);
	}

	@Override
	public List<SecurityRight> getPageRights(SecurityRight right) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("select distinct RT.RightName");
		sql.append(" from SecUserOperations UO");
		sql.append(" inner join SecOperationRoles OPR on OPR.OprID = UO.OprID");
		sql.append(" inner join SecRoles R on R.RoleID = OPR.RoleID");
		sql.append(" inner join SecRoleGroups RG on RG.RoleID = R.RoleID");
		sql.append(" inner join SecGroupRights GR on GR.GrpID = RG.GrpID");
		sql.append(" inner join SecRights RT on RT.RightID = GR.RightID ");
		sql.append(" where RT.RightType <> 0 and UO.UsrID = :UsrID and R.RoleApp = :LoginAppId");
		if (StringUtils.isNotBlank(right.getRoleCd())) {
			sql.append(" and R.RoleCd = :RoleCd");
		}
		sql.append(" and RT.Page = :Page");
		if (StringUtils.isNotBlank(right.getMenuRight())) {
			sql.append(" and GR.GrpID in ( select TGR.GrpID from SecGroupRights TGR inner join SecRights TR");
			sql.append(" on TR.RightID = TGR.RightID where TR.RightName = :MenuRight )");
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(right);
		RowMapper<SecurityRight> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityRight.class);

		logger.debug(Literal.LEAVING);
		return namedParameterJdbcTemplate.query(sql.toString(), paramSource, rowMapper);
	}

	@Override
	public List<SecurityRight> getRoleRights(SecurityRight secRight, String[] roles) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("select distinct RightName");
		sql.append(" from SecRolesRights_View");
		sql.append(" where RightType = :RightType and RoleCd in (:RoleCd)");
		sql.append(" and AppId = :loginAppId and Page = :Page and UsrId = :UsrID");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(secRight);
		RowMapper<SecurityRight> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityRight.class);

		logger.debug(Literal.LEAVING);
		return namedParameterJdbcTemplate.query(sql.toString(), paramSource, rowMapper);
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
