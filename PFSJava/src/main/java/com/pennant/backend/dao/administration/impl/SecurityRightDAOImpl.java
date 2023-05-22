/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 *
 * FileName : SecurityRightDAOImpl.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 2-08-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 2-08-2011 Pennant 0.1 * * * * * * * * *
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
		StringBuilder sql = new StringBuilder("Select distinct RT.RightName");
		sql.append(" from SecUserOperations uo");
		sql.append(" Inner Join SecOperationRoles opr on opr.OprID = uo.OprID");
		sql.append(" Inner Join SecRoles r on r.RoleID = opr.RoleID");
		sql.append(" Inner Join SecRoleGroups rg on rg.RoleID = r.RoleID");
		sql.append(" Inner Join SecGroupRights gr on gr.GrpID = rg.GrpID");
		sql.append(" Inner Join SecRights rt on rt.RightID = gr.RightID and rt.RightType = ?");
		sql.append(" Where uo.UsrID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

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
		StringBuilder sql = new StringBuilder("Select distinct RT.RightName");
		sql.append(" From SecUserOperations UO");
		sql.append(" Inner Join SecOperationRoles OPR on OPR.OprID = UO.OprID");
		sql.append(" Inner Join SecRoles R on R.RoleID = OPR.RoleID");
		sql.append(" Inner Join SecRoleGroups RG on RG.RoleID = R.RoleID");
		sql.append(" Inner Join SecGroupRights GR on GR.GrpID = RG.GrpID");
		sql.append(" Inner Join SecRights RT on RT.RightID = GR.RightID ");
		sql.append(" Where UO.UsrID = ? and R.RoleApp = ? and RT.RightType <> ? ");
		sql.append(" and RT.Page = ?");

		if (StringUtils.isNotBlank(right.getRoleCd())) {
			sql.append(" and R.RoleCd = ?");
		}

		if (StringUtils.isNotBlank(right.getMenuRight())) {
			sql.append(" and GR.GrpID in (Select TGR.GrpID From SecGroupRights TGR");
			sql.append(" Inner Join SecRights TR on TR.RightID = TGR.RightID");
			sql.append(" Where TR.RightName = ?)");
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, right.getUsrID());
			ps.setLong(++index, right.getLoginAppId());
			ps.setInt(++index, 0);
			ps.setString(++index, right.getPage());

			if (StringUtils.isNotBlank(right.getRoleCd())) {
				ps.setString(++index, right.getRoleCd());
			}

			if (StringUtils.isNotBlank(right.getMenuRight())) {
				ps.setString(++index, right.getMenuRight());
			}
		}, (rs, rowNum) -> {
			SecurityRight sr = new SecurityRight();

			sr.setRightName(rs.getString("RightName"));

			return sr;
		});
	}

	@Override
	public boolean isRightNameExists(String rightName) {
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("RightName", rightName);

		String sql = QueryUtil.getCountQuery(new String[] { "SecRights" }, "RightName = :RightName ");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcTemplate.queryForObject(sql, paramSource, Integer.class) > 0;
	}

	@Override
	public long save(SecurityRight right) {
		if (right.getId() == Long.MIN_VALUE) {
			right.setId(getNextValue("SeqSecRights"));
		}

		StringBuilder sql = new StringBuilder();
		sql.append("Insert into SecRights(RightID,RightType,RightName,Page,Version,LastMntBy,LastMntOn, ");
		sql.append("RecordStatus, RoleCode,NextRoleCode,TaskId,NextTaskId,RecordType,WorkflowId) ");
		sql.append("Values(:RightID, :RightType, :RightName, :Page, :Version, :LastMntBy, :LastMntOn,");
		sql.append(":RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId )");

		logger.debug(Literal.SQL.concat(sql.toString()));

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(right);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		return right.getId();
	}

	@Override
	public void updateSeqSecRights() {
		String sql = "UPDATE SEQSECRIGHTS SET SEQNO=(SELECT MAX(RIGHTID) FROM SECRIGHTS)";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcTemplate.getJdbcOperations().update(sql);
	}
}
