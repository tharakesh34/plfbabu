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

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.administration.SecurityRightDAO;
import com.pennant.backend.model.administration.SecurityRight;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.util.QueryUtil;

public class SecurityRightDAOImpl extends SequenceDao<SecurityRight> implements SecurityRightDAO {
	private static Logger logger = LogManager.getLogger(SecurityRightDAOImpl.class);

	public SecurityRightDAOImpl() {
		super();
	}

	@Override
	public List<SecurityRight> getMenuRightsByUser(SecurityUser user) {
		StringBuilder sql = new StringBuilder("select distinct RT.RightName");
		sql.append(" from SecUserOperations uo");
		sql.append(" inner join SecOperationRoles opr on opr.OprID = uo.OprID");
		sql.append(" inner join SecRoles r on r.RoleID = opr.RoleID");
		sql.append(" inner join SecRoleGroups rg on rg.RoleID = r.RoleID");
		sql.append(" inner join SecGroupRights gr on gr.GrpID = rg.GrpID");
		sql.append(" inner join SecRights rt on rt.RightID = gr.RightID and rt.RightType = ?");
		sql.append(" where uo.UsrID = ?");

		logger.trace(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), ps -> {
			ps.setInt(1, 0);
			ps.setLong(2, user.getUsrID());
		}, (rs, rowNum) -> {
			SecurityRight right = new SecurityRight();

			right.setRightName(rs.getString("RightName"));

			return right;
		});
	}

	@Override
	public List<SecurityRight> getPageRights(SecurityRight right) {
		StringBuilder sql = new StringBuilder("select distinct RT.RightName");
		sql.append(" from SecUserOperations UO");
		sql.append(" inner join SecOperationRoles OPR on OPR.OprID = UO.OprID");
		sql.append(" inner join SecRoles R on R.RoleID = OPR.RoleID");
		sql.append(" inner join SecRoleGroups RG on RG.RoleID = R.RoleID");
		sql.append(" inner join SecGroupRights GR on GR.GrpID = RG.GrpID");
		sql.append(" inner join SecRights RT on RT.RightID = GR.RightID ");
		sql.append(" where UO.UsrID = :UsrID and R.RoleApp = :LoginAppId and RT.RightType <> 0 ");
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

		return jdbcTemplate.query(sql.toString(), paramSource, (rs, rowNum) -> {
			SecurityRight sr = new SecurityRight();

			sr.setRightName(rs.getString("RightName"));

			return sr;
		});
	}

	@Override
	public boolean isRightNameExists(String rightName) {
		logger.debug(Literal.ENTERING);

		boolean exists = false;
		// Prepare the parameter source.
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("rightName", rightName);
		String sql = QueryUtil.getCountQuery(new String[] { "SecRights" }, "rightName = :rightName ");
		logger.trace(Literal.SQL + sql);
		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);
		if (count > 0) {
			exists = true;
		}
		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public long save(SecurityRight right) {
		logger.debug(Literal.ENTERING);

		if (right.getId() == Long.MIN_VALUE) {
			right.setId(getNextValue("SeqSecRights"));
			logger.debug("get NextValue:" + right.getId());
		}
		StringBuilder sql = new StringBuilder();
		sql.append("Insert into SecRights(RightID,RightType,RightName,Page,Version,LastMntBy,LastMntOn, ");
		sql.append("RecordStatus, RoleCode,NextRoleCode,TaskId,NextTaskId,RecordType,WorkflowId) ");
		sql.append("Values(:RightID, :RightType, :RightName, :Page, :Version, :LastMntBy, :LastMntOn,");
		sql.append(":RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId )");
		logger.debug(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(right);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug(Literal.ENTERING);
		return right.getId();
	}

	@Override
	public void updateSeqSecRights() {
		logger.debug(Literal.ENTERING);
		String query = "UPDATE SEQSECRIGHTS SET SEQNO=(SELECT MAX(RIGHTID) FROM SECRIGHTS)";
		this.jdbcTemplate.getJdbcOperations().update(query);
		logger.debug(Literal.LEAVING);
	}
}
