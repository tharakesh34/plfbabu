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
 * FileName : NotesDAOImpl.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 26-04-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 * * 21-05-2018 Sai 0.2 1. PSD - Ticket: 126490 LMS > * Notes of the rejected or * resubmitted
 * disbursed tranche * is not visible. * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.NotesDAO;
import com.pennant.backend.model.Notes;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class NotesDAOImpl extends SequenceDao<Notes> implements NotesDAO {
	private static Logger logger = LogManager.getLogger(NotesDAOImpl.class);

	public NotesDAOImpl() {
		super();
	}

	public List<Notes> getNotesList(Notes notes, boolean isNotes) {
		StringBuilder selectSql = new StringBuilder(" Select NoteId, ModuleName, Reference, ");
		selectSql.append(" RemarkType, AlignType, T1.RoleCode, T1.Version, Remarks, InputBy, InputDate, ");
		selectSql.append(" T2.UsrLogin, T2.UsrFName , T2.UsrMName , T2.UsrLName From Notes T1 ");
		selectSql.append(" INNER JOIN SecUsers T2 on T2.UsrID = T1.InputBy ");
		selectSql.append(" Where Reference = :Reference and ModuleName = :ModuleName ");
		if (isNotes) {
			selectSql.append(" AND RemarkType IN('N', 'I') ");
		} else {
			selectSql.append(" AND RemarkType IN('R', 'C') ");
		}
		selectSql.append(" ORDER BY InputDate Desc ");

		logger.debug(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(notes);
		RowMapper<Notes> typeRowMapper = BeanPropertyRowMapper.newInstance(Notes.class);
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	public List<Notes> getNotesForAgreements(Notes notes) {
		logger.debug("Entering");
		StringBuilder selectSql = new StringBuilder(" Select NoteId, ModuleName, Reference, ");
		selectSql.append(" RemarkType, AlignType, T1.RoleCode, T1.Version, Remarks, InputBy, InputDate, ");
		selectSql.append(" T2.UsrLogin, T2.UsrFName , T2.UsrMName , T2.UsrLName From Notes T1 ");
		selectSql.append(" INNER JOIN SecUsers T2 on T2.UsrID = T1.InputBy ");
		selectSql.append(" Where Reference = :Reference and ModuleName = :ModuleName ");
		selectSql.append(" AND RemarkType IN('N', 'I', 'R', 'C') ");
		selectSql.append(" ORDER BY InputDate Asc ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(notes);
		RowMapper<Notes> typeRowMapper = BeanPropertyRowMapper.newInstance(Notes.class);
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public List<Notes> getNotesListByRole(Notes notes, boolean isNotes, String[] roleCodes) {
		logger.debug("Entering");
		StringBuilder selectSql = new StringBuilder(
				" Select T1.NoteId, T1.ModuleName, T1.Reference,  T1.RemarkType, T1.AlignType, T1.RoleCode, T1.Version, ");
		selectSql.append(" T1.Remarks, T1.InputBy, T1.InputDate, T2.UsrLogin,T3.RoleDesc  From Notes T1 ");
		selectSql.append(
				" INNER JOIN SecUsers T2 on T2.UsrID = T1.InputBy LEFT OUTER JOIN SecRoles T3 on T1.RoleCode = T3.RoleCd  ");
		selectSql.append(" Where T1.Reference = :Reference and T1.ModuleName = :ModuleName ");
		selectSql.append(" AND T1.RemarkType ='R'");
		if (roleCodes != null) {
			selectSql.append(" AND T1.RoleCode IN(" + getRolesWithFormat(roleCodes) + ")");
		}
		selectSql.append(" ORDER BY InputDate Desc ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(notes);
		RowMapper<Notes> typeRowMapper = BeanPropertyRowMapper.newInstance(Notes.class);
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	public String getRolesWithFormat(String[] roleCodes) {
		String tempRoleCodes = "'";
		if (roleCodes.length > 0) {
			for (int i = 0; i < roleCodes.length; i++) {
				if ("'".equals(tempRoleCodes)) {
					tempRoleCodes = tempRoleCodes.concat(roleCodes[i] + "'");
				} else {
					tempRoleCodes = tempRoleCodes.concat(", '" + roleCodes[i] + "'");
				}
			}
		}
		return tempRoleCodes;
	}

	private String commaJoin(List<String> moduleNames) {
		return moduleNames.stream().map(moduleName -> "?").collect(Collectors.joining(","));
	}

	public static List<Notes> sortNotes(List<Notes> notes) {
		return notes.stream().sorted((l1, l2) -> l1.getInputDate().compareTo(l2.getInputDate()))
				.collect(Collectors.toList());
	}

	@Override
	public List<Notes> getNotesListAsc(List<String> finReferences, String moduleName) {
		List<String> modules = new ArrayList<>();
		modules.add(moduleName);

		if ("financeMain".equals(moduleName)) {
			PennantStaticListUtil.getFinServiceEvents(true).forEach(event -> modules.add(event.getValue()));
		}

		StringBuilder sql = new StringBuilder(" Select NoteId, ModuleName, Reference, RemarkType");
		sql.append(", AlignType, T1.RoleCode, T1.Version, Remarks, InputBy, InputDate, T2.UsrLogin");
		sql.append(" From Notes T1 ");
		sql.append(" INNER JOIN SecUsers T2 on T2.UsrID = T1.InputBy");
		sql.append(" Where Reference In (");
		sql.append(commaJoin(finReferences));
		sql.append(")");
		sql.append(" and ModuleName In (");
		sql.append(commaJoin(modules));
		sql.append(")");
		sql.append(" and RemarkType = ?");

		logger.debug(Literal.SQL + sql.toString());

		List<Notes> list = jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			for (String finReference : finReferences) {
				ps.setString(index++, finReference);
			}

			for (String module : modules) {
				ps.setString(index++, module);
			}

			ps.setString(index, "N");
		}, (rs, rowNum) -> {
			Notes item = new Notes();

			item.setNoteId(rs.getLong("NoteId"));
			item.setModuleName(rs.getString("ModuleName"));
			item.setReference(rs.getString("Reference"));
			item.setRemarkType(rs.getString("RemarkType"));
			item.setAlignType(rs.getString("AlignType"));
			item.setRoleCode(rs.getString("RoleCode"));
			item.setVersion(rs.getInt("Version"));
			item.setRemarks(rs.getString("Remarks"));
			item.setInputBy(rs.getLong("InputBy"));
			item.setInputDate(rs.getTimestamp("InputDate"));
			item.setUsrLogin(rs.getString("UsrLogin"));

			return item;
		});

		return sortNotes(list);
	}

	public List<Notes> getNotesListAsc(String reference, List<String> moduleNames) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder(" Select NoteId, ModuleName, Reference, ");
		selectSql.append(
				" RemarkType, AlignType, T1.RoleCode, T1.Version, Remarks, InputBy, InputDate, T2.UsrLogin From Notes T1 ");
		selectSql.append(" INNER JOIN SecUsers T2 on T2.UsrID = T1.InputBy ");
		selectSql.append(" Where Reference = :Reference and RemarkType != :RemarkType and (ModuleName = :ModuleName");

		for (int i = 1; i < moduleNames.size(); i++) {
			selectSql.append(" or ModuleName = :ModuleName" + i);
			source.addValue("ModuleName" + i, moduleNames.get(i));
		}

		selectSql.append(") ORDER BY Version Desc,InputDate Asc ");

		logger.debug("selectSql: " + selectSql.toString());
		source.addValue("Reference", reference);
		source.addValue("RemarkType", "R");
		source.addValue("ModuleName", moduleNames.get(0));

		RowMapper<Notes> typeRowMapper = BeanPropertyRowMapper.newInstance(Notes.class);
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	@Override
	public void save(Notes notes) {
		StringBuilder sql = new StringBuilder("Insert Into Notes");
		sql.append("(NoteId, ModuleName, Reference, RemarkType, AlignType");
		sql.append(", RoleCode, Version, Remarks, InputBy, InputDate");
		sql.append(") Values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				notes.setId(getNextValue("SeqNotes"));

				ps.setLong(index++, notes.getNoteId());
				ps.setString(index++, notes.getModuleName());
				ps.setString(index++, notes.getReference());
				ps.setString(index++, notes.getRemarkType());
				ps.setString(index++, notes.getAlignType());
				ps.setString(index++, notes.getRoleCode());
				ps.setInt(index++, notes.getVersion());
				ps.setString(index++, notes.getRemarks());
				ps.setLong(index++, notes.getInputBy());
				ps.setTimestamp(index++, notes.getInputDate());
			});
		} catch (Exception e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}
	}

	public void delete(Notes notes) {
		logger.debug("Entering");
		StringBuilder deleteSql = new StringBuilder(" Delete From Notes ");
		deleteSql.append(" Where Reference = :Reference and ModuleName = :ModuleName and Version = :Version");
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(notes);
		try {
			this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	public void deleteAllNotes(Notes notes) {
		logger.debug("Entering");

		StringBuilder deleteSql = new StringBuilder(" Delete From Notes ");
		deleteSql.append(" Where Reference = :Reference and ModuleName = :ModuleName ");
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(notes);
		try {
			this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

}
