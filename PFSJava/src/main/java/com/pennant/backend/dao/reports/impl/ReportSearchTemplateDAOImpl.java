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
 * FileName    		:  ReportSearchTemplateDAOImpl.java								        *                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  5-09-2012															*
 *                                                                  
 * Modified Date    :  5-09-2012														    *
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 5-09-2012	       Pennant	                 0.1                                        * 
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
package com.pennant.backend.dao.reports.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.reports.ReportSearchTemplateDAO;
import com.pennant.backend.model.reports.ReportSearchTemplate;

public class ReportSearchTemplateDAOImpl  implements ReportSearchTemplateDAO{
	private static Logger logger = Logger.getLogger(ReportSearchTemplateDAOImpl .class);
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public ReportSearchTemplateDAOImpl() {
		super();
	}
	
	/**
	 * This Method saves the values into REPORTSEARCHTEMPLATE
	 */
	@Override
	public void save(ReportSearchTemplate reportSearchTemplate) {
		logger.debug("Entering");
		try{
			StringBuilder  insertSql = new StringBuilder ("INSERT INTO REPORTSEARCHTEMPLATE ( ReportID,TemplateName");
			insertSql.append(",FieldID,UsrID,FieldValue,Filter,FieldType,Version,LastMntBy,LastMntOn,RecordStatus");
			insertSql.append(",RoleCode,NextRoleCode,TaskId,RecordType,WorkflowId) values (:ReportID,:TemplateName");
			insertSql.append(",:FieldID,:UsrID,:FieldValue,:Filter,:FieldType,:Version,:LastMntBy,:LastMntOn,:RecordStatus,:RoleCode");
			insertSql.append(",:NextRoleCode,:TaskId,:RecordType,:WorkflowId) ");
			logger.debug("insertSql:"+ insertSql.toString());
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(reportSearchTemplate);
			this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
			logger.debug("Leaving");
		}catch (Exception e){
			logger.debug("Exception: ", e);
		}
	}
	/**
	 * This Method selects the values from REPORTSEARCHTEMPLATE by 
	 */
	@Override
	public List<ReportSearchTemplate> getReportSearchTemplateByReportId(long reportID, long usrID) {
		logger.debug("Entering ");
		ReportSearchTemplate aReportSearchTemplate =new ReportSearchTemplate();
		aReportSearchTemplate.setReportID(reportID);
		aReportSearchTemplate.setUsrID(usrID);

		StringBuilder  selectSql = new StringBuilder ();
		selectSql.append("SELECT  ReportID,TemplateName,FieldID,UsrID,FieldValue,Filter,FieldType ");
		selectSql.append(",Version,LastMntBy,LastMntOn,RecordStatus");
		selectSql.append(",RoleCode,NextRoleCode,TaskId,RecordType,WorkflowId ");
		selectSql.append(" FROM REPORTSEARCHTEMPLATE  where ReportID=:ReportID and UsrID in (-1,:UsrID)");	
		logger.debug("selectSql : " + selectSql.toString()); 
		try{
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(aReportSearchTemplate);
			RowMapper<ReportSearchTemplate> typeRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(ReportSearchTemplate.class);
			logger.debug("Leaving ");
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (Exception e){
			logger.debug("Exception: ", e);
			return null;
		}
	}
	/**
	 * This Method selects the count of values  from REPORTSEARCHTEMPLATE by templateName
	 */
	@Override
	public int  getRecordCountByTemplateName(long reportId,long usrId,String templateName){
		int status;
		logger.debug("Entering ");
		ReportSearchTemplate aReportSearchTemplate=new ReportSearchTemplate();
		aReportSearchTemplate.setUsrID(usrId);
		aReportSearchTemplate.setReportID(reportId);
		aReportSearchTemplate.setTemplateName(templateName);
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(aReportSearchTemplate);
		StringBuilder  selectSql = new StringBuilder ("SELECT COUNT(*) FROM REPORTSEARCHTEMPLATE where templateName=:templateName and ");
		selectSql.append("usrID=:usrID and reportID=:reportID");
		logger.debug("selectSql: " + selectSql.toString());      

		try{
			status=this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		}catch (EmptyResultDataAccessException e) {
			status=0;
			logger.error("Exception: ", e);
		}

		logger.debug("Leaving");
		return status;
	}
	@Override
	public boolean delete(long reportId, long usrId, String templateName) {
		logger.debug("Entering");
		ReportSearchTemplate aReportSearchTemplate=new ReportSearchTemplate();
		aReportSearchTemplate.setUsrID(usrId);
		aReportSearchTemplate.setReportID(reportId);
		aReportSearchTemplate.setTemplateName(templateName);
		StringBuilder   deleteSql = new StringBuilder  ("Delete From REPORTSEARCHTEMPLATE");
		deleteSql.append(" Where templateName=:templateName and usrID=:usrID and reportID=:reportID ");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(aReportSearchTemplate);
		logger.debug("deleteSql:"+deleteSql.toString());
		
		int recordCount = 0;
		boolean rcdDeleted = false; 
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if(recordCount != 0){
				rcdDeleted = true;
			}
		}catch(DataAccessException e){
			logger.debug("Exception: ", e);
			rcdDeleted = false;
		}
		logger.debug("Leaving");
		return rcdDeleted;
	}

	/**
	 * @param dataSource the dataSource to set
	 */

	public void setDataSource(DataSource dataSource) {	
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

}
