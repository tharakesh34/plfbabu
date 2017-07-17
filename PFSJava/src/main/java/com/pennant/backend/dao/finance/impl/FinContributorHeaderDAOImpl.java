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
 * FileName    		:  FinContributorHeaderDAOImpl.java                                          * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-01-2013    														*
 *                                                                  						*
 * Modified Date    :  																		*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-02-2012       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.finance.impl;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinContributorHeaderDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FinContributorHeader;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>FinContributorHeader model</b> class.<br>
 * 
 */
public class FinContributorHeaderDAOImpl extends BasisCodeDAO<FinContributorHeader> implements FinContributorHeaderDAO {

	private static Logger logger = Logger.getLogger(FinContributorHeaderDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public FinContributorHeaderDAOImpl() {
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the new FinContributorHeader
	 * 
	 * @return FinContributorHeader
	 */

	@Override
	public FinContributorHeader getFinContributorHeader() {
		logger.debug("Entering");

		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("FinContributorHeader");
		FinContributorHeader contributorHeader = new FinContributorHeader();
		if (workFlowDetails != null) {
			contributorHeader.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return contributorHeader;
	}

	/**
	 * This method get the module from method getFinContributorHeader() 
	 * and set the new record flag as true and return
	 * FinContributorHeader()
	 * 
	 * @return FinContributorHeader
	 */

	@Override
	public FinContributorHeader getNewFinContributorHeader() {
		logger.debug("Entering");
		FinContributorHeader contributorHeader = getFinContributorHeader();
		contributorHeader.setNewRecord(true);
		logger.debug("Leaving");
		return contributorHeader;
	}

	/**
	 * Fetch the Record Finance Main Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinContributorHeader
	 */
	@Override
	public FinContributorHeader getFinContributorHeaderById(final String id, String type) {
		logger.debug("Entering");
		FinContributorHeader contributorHeader = new FinContributorHeader();

		contributorHeader.setId(id);

		StringBuilder selectSql = new StringBuilder("SELECT FinReference , MinContributors , " );
		selectSql.append(" MaxContributors , MinContributionAmt , MaxContributionAmt , " );
		selectSql.append(" CurContributors , CurContributionAmt , CurBankInvestment , " );
		selectSql.append(" AvgMudaribRate , AlwContributorsToLeave , AlwContributorsToJoin , ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, ");
		selectSql.append(" NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" From FinContributorHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(contributorHeader);
		RowMapper<FinContributorHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(FinContributorHeader.class);

		try {
			contributorHeader = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
			        beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			contributorHeader = null;
		}
		logger.debug("Leaving");
		return contributorHeader;
	}

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the FinContributorHeader or FinContributorHeader_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Finance Main Detail by key FinReference
	 * 
	 * @param Finance
	 *            Main Detail (contributorHeader)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(String finReference, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		FinContributorHeader contributorHeader = new FinContributorHeader();
		contributorHeader.setFinReference(finReference);

		StringBuilder deleteSql = new StringBuilder("Delete From ");
		deleteSql.append(" FinContributorHeader");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(contributorHeader);
		
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),
			        beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into FinContributorHeader or FinContributorHeader_Temp.
	 * 
	 * save Finance Main Detail
	 * 
	 * @param Finance
	 *            Main Detail (contributorHeader)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(FinContributorHeader contributorHeader, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into ");
		insertSql.append(" FinContributorHeader");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" ( FinReference , MinContributors , MaxContributors , " );
		insertSql.append(" MinContributionAmt , MaxContributionAmt , CurContributors , " );
		insertSql.append(" CurContributionAmt , CurBankInvestment , AvgMudaribRate , " );
		insertSql.append(" AlwContributorsToLeave , AlwContributorsToJoin , " );
		insertSql.append(" Version , LastMntBy , LastMntOn , RecordStatus , RoleCode , " );
		insertSql.append(" NextRoleCode , TaskId , NextTaskId , RecordType , WorkflowId) ");
		insertSql.append(" VALUES ( :FinReference , :MinContributors , :MaxContributors , " );
		insertSql.append(" :MinContributionAmt , :MaxContributionAmt , :CurContributors , " );
		insertSql.append(" :CurContributionAmt , :CurBankInvestment , :AvgMudaribRate , " );
		insertSql.append(" :AlwContributorsToLeave , :AlwContributorsToJoin , " );
		insertSql.append(" :Version , :LastMntBy , :LastMntOn , :RecordStatus , :RoleCode , " );
		insertSql.append(" :NextRoleCode , :TaskId , :NextTaskId , :RecordType , :WorkflowId) ");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(contributorHeader);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return contributorHeader.getId();
	}

	/**
	 * This method updates the Record FinContributorHeader or FinContributorHeader_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Finance Main Detail by key FinReference and Version
	 * 
	 * @param Finance
	 *            Main Detail (contributorHeader)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(FinContributorHeader contributorHeader, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update ");
		updateSql.append(" FinContributorHeader");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" SET MinContributors =:MinContributors , " );
		updateSql.append(" MaxContributors =:MaxContributors , MinContributionAmt =:MinContributionAmt , " );
		updateSql.append(" MaxContributionAmt =:MaxContributionAmt , CurContributors =:CurContributors , " );
		updateSql.append(" CurContributionAmt =:CurContributionAmt , CurBankInvestment =:CurBankInvestment , " );
		updateSql.append(" AvgMudaribRate =:AvgMudaribRate , AlwContributorsToLeave =:AlwContributorsToLeave , " );
		updateSql.append(" AlwContributorsToJoin =:AlwContributorsToJoin , " );
		updateSql.append(" Version =:Version , LastMntBy =:LastMntBy , LastMntOn=:LastMntOn , " );
		updateSql.append(" RecordStatus=:RecordStatus , RoleCode=:RoleCode , NextRoleCode=:NextRoleCode , " );
		updateSql.append(" TaskId=:TaskId , NextTaskId=:NextTaskId , RecordType=:RecordType , WorkflowId=:WorkflowId ");
		updateSql.append(" Where FinReference =:FinReference");
		
		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(contributorHeader);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

}