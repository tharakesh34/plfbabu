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
 *																							*
 * FileName    		:  SysNotificationDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-06-2015    														*
 *                                                                  						*
 * Modified Date    :  11-06-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-06-2015       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.applicationmaster.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.applicationmaster.SysNotificationDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.applicationmaster.SysNotification;
import com.pennant.backend.model.applicationmaster.SysNotificationDetails;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>SysNotification model</b> class.<br>
 * 
 */
public class SysNotificationDAOImpl extends BasisNextidDaoImpl<SysNotification> implements SysNotificationDAO {
	private static Logger logger = Logger.getLogger(SysNotificationDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


	public SysNotificationDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record  Cheque Purpose details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return SysNotification
	 */
	@Override
	public SysNotification getSysNotificationById(final long id, String type) {
		logger.debug("Entering");
		SysNotification sysNotification = new SysNotification();

		sysNotification.setSysNotificationId(id);

		StringBuilder selectSql = new StringBuilder("Select SysNotificationId, QueryCode, Description, TemplateCode, Doctype, DocName, DocImage,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" ,lovDescQueryDesc, lovDescTemplateDesc, lovDescSqlQuery");
		}
		selectSql.append(" From SysNotification");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where SysNotificationId = :SysNotificationId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sysNotification);
		RowMapper<SysNotification> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SysNotification.class);

		try{
			sysNotification = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			sysNotification = null;
		}
		logger.debug("Leaving");
		return sysNotification;
	}

	/**
	 * To Set  dataSource
	 * @param dataSource
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the SysNotification or SysNotification_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Cheque Purpose by key Code
	 * 
	 * @param Cheque Purpose (sysNotification)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(long id,String type) {
		logger.debug("Entering");
		int recordCount = 0;

		SysNotification sysNotification = new SysNotification();
		sysNotification.setSysNotificationId(id);

		StringBuilder deleteSql = new StringBuilder("Delete From SysNotification");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where SysNotificationId = :SysNotificationId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sysNotification);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into SysNotification or SysNotification_Temp.
	 *
	 * save Cheque Purpose 
	 * 
	 * @param Cheque Purpose (sysNotification)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public long save(SysNotification sysNotification,String type) {
		logger.debug("Entering");

		if (sysNotification.getId() == Long.MIN_VALUE) {
			sysNotification.setSysNotificationId(getNextidviewDAO().getNextId("SeqSysNotification"));
			logger.debug("Next ID ; " + sysNotification.getSysNotificationId());
		}

		StringBuilder insertSql =new StringBuilder("Insert Into SysNotification");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (SysNotificationId, QueryCode, Description, TemplateCode, Doctype, DocName, DocImage");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:SysNotificationId, :QueryCode, :Description, :TemplateCode, :Doctype, :DocName, :DocImage");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sysNotification);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return sysNotification.getSysNotificationId();

	}

	/**
	 * This method updates the Record SysNotification or SysNotification_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Cheque Purpose by key Code and Version
	 * 
	 * @param Cheque Purpose (sysNotification)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(SysNotification sysNotification,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update SysNotification");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set QueryCode = :QueryCode, Description = :Description, TemplateCode = :TemplateCode, Doctype = :Doctype, DocName = :DocName, DocImage = :DocImage,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where SysNotificationId = :SysNotificationId");

		/*if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}*/

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sysNotification);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public List<SysNotificationDetails> getCustomerDetails(String whereClause) {
		logger.debug("Entering");

		SysNotificationDetails details = new SysNotificationDetails();

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select CustID, CustCIF, CustShrtName, FinReference, FinBranch, FinCcy, FinCurODDays, FinCuRODAmt, FinPurpose ");
		selectSql.append(" FROM  CustAlertsOD_View");
		selectSql.append(" WHERE ");
		selectSql.append(whereClause);
		selectSql.append(" ORDER BY CustCIF");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(details);
		RowMapper<SysNotificationDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SysNotificationDetails.class);

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);

	}

	@Override
	public long getTemplateId(String templateCode) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		source = new MapSqlParameterSource();
		source.addValue("TemplateCode", templateCode);

		StringBuilder sql = new StringBuilder();
		sql.append(" Select TemplateId from Templates Where TemplateCode = :TemplateCode");

		logger.debug("Query: "+ sql.toString());

		try {
			return this.namedParameterJdbcTemplate.queryForObject(sql.toString(), source, Long.class);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		} finally {
			logger.debug("Leaving");
			source = null;
		}
		return 0;
	}

	@Override
	public String getCustomerEMail(long custID) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		source = new MapSqlParameterSource();
		source.addValue("CustID", custID);

		StringBuilder sql = new StringBuilder();
		sql.append("select CustEMail from (select CustEMail,row_number() over (order by CustEMail) row_num from CustomerEMails where CustID = :CustID AND CustEMailPriority = 1)T where row_num <= 1");

		logger.debug("Query: "+ sql.toString());

		try {
			return this.namedParameterJdbcTemplate.queryForObject(sql.toString(), source, String.class);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		} finally {
			logger.debug("Leaving");
			source = null;
		}
		return "";
	}
}