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
 * FileName    		:  NotesDAOImpl.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 * 21-05-2018		Sai						 0.2          1. PSD - Ticket: 126490 LMS >     * 
 *                                                           Notes of the rejected or       * 
 *                                                           resubmitted disbursed tranche  * 
 *                                                           is not visible.                * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/
package com.pennant.backend.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.NotesDAO;
import com.pennant.backend.model.FinServicingEvent;
import com.pennant.backend.model.Notes;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennanttech.pennapps.core.resource.Literal;

public class NotesDAOImpl extends BasisNextidDaoImpl<Notes> implements NotesDAO{
	private static Logger logger = Logger.getLogger(NotesDAOImpl.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public NotesDAOImpl() {
		super();
	}
	
	public List<Notes> getNotesList(Notes notes, boolean isNotes){
		logger.debug("Entering");
		StringBuilder   selectSql = new StringBuilder(" Select NoteId, ModuleName, Reference, " );
		selectSql.append(" RemarkType, AlignType, T1.RoleCode, T1.Version, Remarks, InputBy, InputDate, " );
		selectSql.append(" T2.UsrLogin, T2.UsrFName , T2.UsrMName , T2.UsrLName From Notes T1 "); 
		selectSql.append(" INNER JOIN SecUsers T2 on T2.UsrID = T1.InputBy ");
		selectSql.append(" Where Reference = :Reference and ModuleName = :ModuleName " );
		if(isNotes){
			selectSql.append(" AND RemarkType IN('N', 'I') " );
		}else{
			selectSql.append(" AND RemarkType IN('R', 'C') " );
		}
		selectSql.append(" ORDER BY InputDate Desc ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(notes);
		RowMapper<Notes> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Notes.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}
	
	@Override
	public List<Notes> getNotesListByRole(Notes notes, boolean isNotes, String[] roleCodes){
		logger.debug("Entering");
		StringBuilder   selectSql = new StringBuilder(" Select T1.NoteId, T1.ModuleName, T1.Reference,  T1.RemarkType, T1.AlignType, T1.RoleCode, T1.Version, " );
		selectSql.append(" T1.Remarks, T1.InputBy, T1.InputDate, T2.UsrLogin,T3.RoleDesc  From Notes T1 "); 
		selectSql.append(" INNER JOIN SecUsers T2 on T2.UsrID = T1.InputBy LEFT OUTER JOIN SecRoles T3 on T1.RoleCode = T3.RoleCd  ");
		selectSql.append(" Where T1.Reference = :Reference and T1.ModuleName = :ModuleName " );
		selectSql.append(" AND T1.RemarkType ='R'" );
		if (roleCodes!=null) {
	        selectSql.append(" AND T1.RoleCode IN(" + getRolesWithFormat(roleCodes) + ")");
        }
		selectSql.append(" ORDER BY InputDate Desc ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(notes);
		RowMapper<Notes> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Notes.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
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
	
	// PSD - Ticket: 126490 LMS > Notes of the rejected or resubmitted disbursed tranche is not visible.
	@Override
	public List<Notes> getNotesListAsc(Notes notes) {
		logger.trace(Literal.ENTERING);

		// Set the servicing events as modules. 
		List<String> modules = new ArrayList<>();
		if ("financeMain".equals(notes.getModuleName())) {
			modules.add(notes.getModuleName());

			for (FinServicingEvent event : PennantStaticListUtil.getFinServiceEvents(true)) {
				modules.add(event.getValue());
			}
		}

		StringBuilder sql = new StringBuilder(" Select NoteId, ModuleName, Reference, RemarkType, ");
		sql.append(" AlignType, T1.RoleCode, T1.Version, Remarks, InputBy, InputDate, T2.UsrLogin From Notes T1 ");
		sql.append(" INNER JOIN SecUsers T2 on T2.UsrID = T1.InputBy ");
		sql.append(" Where Reference = :Reference ");
		if ("financeMain".equals(notes.getModuleName())) {
			sql.append(" and ModuleName in (:ModuleNames)");
		} else {
			sql.append(" and ModuleName = :ModuleName");
		}
		sql.append(" and RemarkType = :RemarkType");
		sql.append(" ORDER BY InputDate Asc ");
		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("Reference", notes.getReference());
		if ("financeMain".equals(notes.getModuleName())) {
			paramSource.addValue("ModuleNames", modules);
		} else {
			paramSource.addValue("ModuleName", notes.getModuleName());
		}
		paramSource.addValue("RemarkType", "N");

		RowMapper<Notes> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Notes.class);

		logger.debug(Literal.LEAVING);
		return namedParameterJdbcTemplate.query(sql.toString(), paramSource, typeRowMapper);
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

		RowMapper<Notes> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Notes.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public void save(Notes notes) {
		logger.debug("Entering");
		notes.setId(getNextidviewDAO().getNextId("SeqNotes"));
		
		StringBuilder  insertSql = 	new StringBuilder(" INSERT INTO Notes (NoteId, ModuleName, Reference , " );
		insertSql.append(" RemarkType, AlignType, RoleCode, Version, Remarks, InputBy, InputDate )");
		insertSql.append(" Values( :NoteId, :ModuleName, :Reference, :RemarkType, :AlignType, :RoleCode, " );
		insertSql.append(" :Version, :Remarks, :InputBy, :InputDate)");
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(notes);
		logger.debug("Leaving");
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
	}
	
	public void delete(Notes notes) {
		logger.debug("Entering");
		StringBuilder  deleteSql = new StringBuilder(" Delete From Notes " );
		deleteSql.append(" Where Reference = :Reference and ModuleName = :ModuleName and Version = :Version");
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(notes);
		try{
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		}catch(DataAccessException e){
			logger.error("Exception: ", e);
			throw e;
		}
		logger.debug("Leaving");
	}
	
	
	public void deleteAllNotes(Notes notes) {
		logger.debug("Entering");
		
		StringBuilder  deleteSql = new StringBuilder(" Delete From Notes " );
		deleteSql.append(" Where Reference = :Reference and ModuleName = :ModuleName ");
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(notes);
		try{
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		}catch(DataAccessException e){
			logger.error("Exception: ", e);
			throw e;
		}
		logger.debug("Leaving");
	}
	
}
