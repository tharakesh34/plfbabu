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
 * FileName    		:  MandateStatusDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  25-10-2016    														*
 *                                                                  						*
 * Modified Date    :  25-10-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 25-10-2016       PENNANT	                 0.1                                            * 
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

package com.pennant.backend.dao.mandate.impl;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.mandate.MandateStatusDAO;
import com.pennant.backend.model.mandate.MandateStatus;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
/**
 * DAO methods implementation for the <b>MandateStatus model</b> class.<br>
 * 
 */

public class MandateStatusDAOImpl extends BasicDao<MandateStatus> implements MandateStatusDAO {
   private static Logger logger = Logger.getLogger(MandateStatusDAOImpl.class);
	
	
	public MandateStatusDAOImpl(){
		super();
	}
	
	/**
	 * Fetch the Record  MandateStatus details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return MandateStatus
	 */
	@Override
	public MandateStatus getMandateStatusById(final long id, String type ) {
		logger.debug("Entering");
		MandateStatus mandateStatus = new MandateStatus();
		mandateStatus.setId(id);
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" mandateID,status,reason,changeDate,fileID,");
		
		if(type.contains("View")){
			sql.append("");
		}	
		sql.append(" From MandatesStatus");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where MandateID =:MandateID");
		
		logger.debug("sql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mandateStatus);
		RowMapper<MandateStatus> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(MandateStatus.class);
		
		try{
			mandateStatus = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			mandateStatus = null;
		}
		logger.debug("Leaving");
		return mandateStatus;
	}
	
	
	
	/**
	 * This method Deletes the Record from the MandatesStatus or MandatesStatus_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete MandateStatus by key MandateID
	 * 
	 * @param MandateStatus (mandateStatus)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(MandateStatus mandateStatus,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder sql = new StringBuilder("Delete From MandatesStatus");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where MandateID =:MandateID");
	
		logger.debug("sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mandateStatus);
		try{
			recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into MandatesStatus or MandatesStatus_Temp.
	 * it fetches the available Sequence form SeqMandatesStatus by using getNextidviewDAO().getNextId() method.  
	 *
	 * save MandateStatus 
	 * 
	 * @param MandateStatus (mandateStatus)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public long save(MandateStatus mandateStatus,String type) {
		logger.debug("Entering");
		if (mandateStatus.getId()==Long.MIN_VALUE){
			/*mandateStatus.setId(getNextidviewDAO().getNextId("SeqMandatesStatus"));
			logger.debug("get NextID:"+mandateStatus.getId());*/
		}
		
		StringBuilder sql =new StringBuilder("Insert Into MandatesStatus ");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (mandateID,status,reason,changeDate,fileID)");
		sql.append(" Values(:mandateID,:status,:reason,:changeDate,:fileID)");
		
		logger.debug("sql: " + sql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mandateStatus);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug("Leaving");
		return mandateStatus.getId();
	}
	
	/**
	 * This method updates the Record MandatesStatus or MandatesStatus_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update MandateStatus by key MandateID and Version
	 * 
	 * @param MandateStatus (mandateStatus)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public void update(MandateStatus mandateStatus,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	sql =new StringBuilder("Update MandatesStatus");
		sql.append(StringUtils.trimToEmpty(type)); 
		sql.append(" Set status=:status,reason=:reason,");
		sql.append(" changeDate=:changeDate,fileID=:fileID");
		sql.append(" Where MandateID =:MandateID");
		
		if (!type.endsWith("_Temp")){
			sql.append("  AND Version= :Version-1");
		}
		
		logger.debug("Sql: " + sql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mandateStatus);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

}