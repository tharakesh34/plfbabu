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
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/
package com.pennant.backend.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.NotesDAO;
import com.pennant.backend.model.Notes;

public class NotesDAOImpl extends BasisNextidDaoImpl<Notes> implements NotesDAO{
	
	private static Logger logger = Logger.getLogger(NotesDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
	public Notes getNewNotes() {
		return new Notes();
	}

	@Override
	public List<Notes> getNotesByID(Notes notes) {
		logger.debug("Entering");
		StringBuilder   selectSql = new StringBuilder("Select NoteId, ModuleName, Reference,  " );
		selectSql.append(" RemarkType, AlignType, Version, Remarks, InputBy, InputDate " );
		selectSql.append(" From Notes ");
		selectSql.append(" Where Reference = :Reference and ModuleName = :ModuleName and Version = :Version");
		logger.debug("selectSql: " + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(notes);
		RowMapper<Notes> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Notes.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}
	
	public List<Notes> getNotesList(Notes notes){
		logger.debug("Entering");
		StringBuilder   selectSql = new StringBuilder(" Select NoteId, ModuleName, Reference, " );
		selectSql.append(" RemarkType, AlignType, T1.Version, Remarks, InputBy, InputDate, T2.UsrLogin From Notes T1 "); 
		selectSql.append(" INNER JOIN SecUsers T2 on T2.UsrID = T1.InputBy ");
		selectSql.append(" Where Reference = :Reference and ModuleName = :ModuleName ORDER BY InputDate Desc ");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(notes);
		RowMapper<Notes> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Notes.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public void initialize(Notes notes) {
		super.initialize(notes);
	}

	@Override
	public void refresh(Notes notes) {
	}

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public void save(Notes notes) {
		logger.debug("Entering");
		notes.setId(getNextidviewDAO().getNextId("SeqNotes"));
		
		StringBuilder  insertSql = 	new StringBuilder(" INSERT INTO Notes (NoteId, ModuleName, Reference , " );
		insertSql.append(" RemarkType, AlignType, Version, Remarks, InputBy, InputDate )");
		insertSql.append(" Values( :NoteId, :ModuleName, :Reference, :RemarkType, :AlignType, " );
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
			logger.debug("Error delete Method");
			logger.error(e);
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
			logger.debug("Error delete Method");
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving");
	}
	
}
