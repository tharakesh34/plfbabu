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
 * FileName    		:  PenaltyDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.rmtmasters.impl;

import java.sql.Timestamp;

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

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.rmtmasters.PenaltyDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.rmtmasters.Penalty;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>Penalty model</b> class.<br>
 * 
 */
public class PenaltyDAOImpl extends BasisCodeDAO<Penalty> implements PenaltyDAO {

	private static Logger logger = Logger.getLogger(PenaltyDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the new Penalty 
	 * @return Penalty
	 */
	@Override
	public Penalty getPenalty() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("Penalty");
		Penalty penalty= new Penalty();
		if (workFlowDetails!=null){
			penalty.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return penalty;
	}

	/**
	 * This method get the module from method getPenalty() and set the new
	 * record flag as true and return Penalty()
	 * 
	 * @return Penalty
	 */
	@Override
	public Penalty getNewPenalty() {
		logger.debug("Entering");
		Penalty penalty = getPenalty();
		penalty.setNewRecord(true);
		logger.debug("Leaving");
		return penalty;
	}

	/**
	 * Fetch the Record  Penalties details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Penalty
	 */
	@Override
	public Penalty getPenaltyById(final String id, String type) {
		logger.debug("Entering");
		Penalty penalty = getPenalty();
		penalty.setId(id);

		StringBuilder selectSql = new StringBuilder("SELECT PenaltyType, PenaltyEffDate," );
		selectSql.append(" IsPenaltyCapitalize, IsPenaltyOnPriOnly, IsPenaltyAftGrace," );
		selectSql.append(" ODueGraceDays, PenaltyPriRateBasis, PenaltyPriBaseRate, PenaltyPriSplRate,");
		selectSql.append(" PenaltyPriNetRate, PenaltyIntRateBasis, PenaltyIntBaseRate,PenaltyIntSplRate,");
		selectSql.append(" PenaltyIntNetRate, PenaltyIsActive," );
		if(type.contains("View")){
			selectSql.append(" lovDescPenaltyTypeName," );
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM RMTPenalties" + StringUtils.trimToEmpty(type) );
		selectSql.append(" WHERE PenaltyType = :PenaltyType");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(penalty);
		RowMapper<Penalty> typeRowMapper = ParameterizedBeanPropertyRowMapper
									.newInstance(Penalty.class);

		try{
			penalty = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.error(e);
			penalty = null;
		}
		logger.debug("Leaving");
		return penalty;
	}

	/**
	 * This method initialize the Record.
	 * @param Penalty (penalty)
	 * @return Penalty
	 */
	@Override
	public void initialize(Penalty penalty) {
		super.initialize(penalty);
	}

	/**
	 * This method refresh the Record.
	 * @param Penalty (penalty)
	 * @return void
	 */
	@Override
	public void refresh(Penalty penalty) {
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the RMTPenalties or RMTPenalties_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Penalties by key PenaltyType
	 * 
	 * @param Penalties (penalty)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(Penalty penalty,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder(" Delete From RMTPenalties" );
		deleteSql.append(StringUtils.trimToEmpty(type) );
		deleteSql.append(" Where PenaltyType =:PenaltyType");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(penalty);

		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003", penalty.getPenaltyType(),
						penalty.getPenaltyEffDate(), penalty.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails=  getError("41006", penalty.getPenaltyType(),
					penalty.getPenaltyEffDate(), penalty.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving delete Method");
	}

	/**
	 * This method insert new Records into RMTPenalties or RMTPenalties_Temp.
	 *
	 * save Penalties 
	 * 
	 * @param Penalties (penalty)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(Penalty penalty,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder("Insert Into RMTPenalties" );
		insertSql.append(StringUtils.trimToEmpty(type) );
		insertSql.append(" (PenaltyType, PenaltyEffDate, IsPenaltyCapitalize, IsPenaltyOnPriOnly," );
		insertSql.append(" IsPenaltyAftGrace, ODueGraceDays, PenaltyPriRateBasis, PenaltyPriBaseRate," );
		insertSql.append(" PenaltyPriSplRate, PenaltyPriNetRate, PenaltyIntRateBasis,");
		insertSql.append(" PenaltyIntBaseRate, PenaltyIntSplRate, PenaltyIntNetRate, PenaltyIsActive," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:PenaltyType, :PenaltyEffDate, :IsPenaltyCapitalize," );
		insertSql.append(" :IsPenaltyOnPriOnly, :IsPenaltyAftGrace, :ODueGraceDays,");
		insertSql.append(" :PenaltyPriRateBasis, :PenaltyPriBaseRate," );
		insertSql.append(" :PenaltyPriSplRate, :PenaltyPriNetRate, :PenaltyIntRateBasis," );
		insertSql.append(" :PenaltyIntBaseRate, :PenaltyIntSplRate, :PenaltyIntNetRate, :PenaltyIsActive," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode," );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(penalty);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return penalty.getId();
	}

	/**
	 * This method updates the Record RMTPenalties or RMTPenalties_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Penalties by key PenaltyType and Version
	 * 
	 * @param Penalties (penalty)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(Penalty penalty,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder updateSql = new StringBuilder("Update RMTPenalties" );
		updateSql.append(StringUtils.trimToEmpty(type) ); 
		updateSql.append(" Set PenaltyType = :PenaltyType, PenaltyEffDate = :PenaltyEffDate," );
		updateSql.append(" IsPenaltyCapitalize = :IsPenaltyCapitalize," );
		updateSql.append(" IsPenaltyOnPriOnly = :IsPenaltyOnPriOnly," );
		updateSql.append(" IsPenaltyAftGrace = :IsPenaltyAftGrace, ODueGraceDays = :ODueGraceDays,");
		updateSql.append(" PenaltyPriRateBasis = :PenaltyPriRateBasis," );
		updateSql.append(" PenaltyPriBaseRate = :PenaltyPriBaseRate," );
		updateSql.append(" PenaltyPriSplRate = :PenaltyPriSplRate,PenaltyPriNetRate = :PenaltyPriNetRate,");
		updateSql.append(" PenaltyIntRateBasis = :PenaltyIntRateBasis,");
		updateSql.append(" PenaltyIntBaseRate = :PenaltyIntBaseRate," );
		updateSql.append(" PenaltyIntSplRate = :PenaltyIntSplRate,PenaltyIntNetRate = :PenaltyIntNetRate,");
		updateSql.append(" PenaltyIsActive = :PenaltyIsActive," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy," );
		updateSql.append(" LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode," );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId," );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where PenaltyType =:PenaltyType");

		if (!type.endsWith("_TEMP")){
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(penalty);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004", penalty.getPenaltyType(),
					penalty.getPenaltyEffDate(), penalty.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, String penaltyType,
			Timestamp penaltyEffDate, String userLanguage){
		String[][] parms= new String[2][2]; 
		
		parms[1][0] = penaltyType;
		parms[1][1] = penaltyEffDate.toString();

		parms[0][0] = PennantJavaUtil.getLabel("label_PenaltyType")+ ":" + parms[1][0];
		parms[0][1] = PennantJavaUtil.getLabel("label_PenaltyEffDate")+ ":" + parms[1][1];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
								errorId, parms[0],parms[1]), userLanguage);
	}
}