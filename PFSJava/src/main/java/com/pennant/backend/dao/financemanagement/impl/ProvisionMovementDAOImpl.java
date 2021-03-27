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
 * FileName    		:  ProvisionMovementDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-05-2012    														*
 *                                                                  						*
 * Modified Date    :  31-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-05-2012       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.financemanagement.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.financemanagement.ProvisionMovementDAO;
import com.pennant.backend.model.financemanagement.ProvisionMovement;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>ProvisionMovement model</b> class.<br>
 * 
 */

public class ProvisionMovementDAOImpl extends BasicDao<ProvisionMovement> implements ProvisionMovementDAO {
	private static Logger logger = LogManager.getLogger(ProvisionMovementDAOImpl.class);

	public ProvisionMovementDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Provision Movement Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return ProvisionMovement
	 */
	@Override
	public ProvisionMovement getProvisionMovementById(final String id, final Date movementDate, String type) {
		logger.debug(Literal.ENTERING);
		ProvisionMovement provisionMovement = new ProvisionMovement();

		provisionMovement.setId(id);
		provisionMovement.setProvMovementDate(movementDate);

		StringBuilder sql = new StringBuilder("Select FinReference, ProvMovementDate,");
		sql.append(" ProvMovementSeq, ProvCalDate, ProvisionedAmt, ProvisionAmtCal, ProvisionDue,");
		sql.append(" ProvisionPostSts, NonFormulaProv, UseNFProv, AutoReleaseNFP, PrincipalDue,");
		sql.append(" ProfitDue, DueFromDate, LastFullyPaidDate, LinkedTranId, ");
		sql.append(
				" AssetCode, AssetStageOrdr, NPA, ManualProvision, ProvChgLinkedTranId, PrvovisionRate, DueDays, PriBal");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append("");
		}
		sql.append(" From FinProvMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference =:FinReference AND ProvMovementDate =:ProvMovementDate ");
		sql.append(" and ProvMovementSeq =(SELECT MAX(ProvMovementSeq) FROM FinProvMovements ");
		sql.append(" Where FinReference =:FinReference AND ProvMovementDate =:ProvMovementDate ) ");

		logger.debug(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provisionMovement);
		RowMapper<ProvisionMovement> typeRowMapper = BeanPropertyRowMapper.newInstance(ProvisionMovement.class);
		try {
			provisionMovement = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			provisionMovement = null;
		}
		logger.debug(Literal.LEAVING);
		return provisionMovement;
	}

	/**
	 * Fetch the Record Provision Movement Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return ProvisionMovement
	 */
	@Override
	public List<ProvisionMovement> getProvisionMovementListById(final String id, String type) {
		logger.debug(Literal.ENTERING);
		ProvisionMovement provisionMovement = new ProvisionMovement();

		provisionMovement.setId(id);

		StringBuilder selectSql = new StringBuilder("Select FinReference, ProvMovementDate,");
		selectSql.append(" ProvMovementSeq, ProvCalDate, ProvisionedAmt, ProvisionAmtCal, ProvisionDue,");
		selectSql.append(" ProvisionPostSts, NonFormulaProv, UseNFProv, AutoReleaseNFP, PrincipalDue,");
		selectSql.append(" ProfitDue, DueFromDate, LastFullyPaidDate, LinkedTranId, ");
		selectSql.append(
				" AssetCode, AssetStageOrdr, NPA, ManualProvision, ProvChgLinkedTranId, PrvovisionRate, DueDays, PriBal");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append("");
		}
		selectSql.append(" From FinProvMovements");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference ");

		logger.debug(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provisionMovement);
		RowMapper<ProvisionMovement> typeRowMapper = BeanPropertyRowMapper.newInstance(ProvisionMovement.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * This method Deletes the Record from the FinProvMovements or FinProvMovements_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete Provision Movement Detail by key FinReference
	 * 
	 * @param Provision
	 *            Movement Detail (provisionMovement)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(ProvisionMovement provisionMovement, String type) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;

		StringBuilder sql = new StringBuilder("Delete From FinProvMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference =:FinReference");
		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provisionMovement);
		try {
			recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method insert new Records into FinProvMovements or FinProvMovements_Temp.
	 *
	 * save Provision Movement Detail
	 * 
	 * @param Provision
	 *            Movement Detail (provisionMovement)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(ProvisionMovement provisionMovement, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Insert Into FinProvMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FinReference, ProvMovementDate, ProvMovementSeq, ProvCalDate, ProvisionedAmt,");
		sql.append(" ProvisionAmtCal, ProvisionDue, ProvisionPostSts, NonFormulaProv, UseNFProv,");
		sql.append(" AutoReleaseNFP, PrincipalDue, ProfitDue, DueFromDate, LastFullyPaidDate, LinkedTranId, ");
		sql.append(
				" AssetCode, AssetStageOrdr, NPA, ManualProvision, ProvChgLinkedTranId, PrvovisionRate, DueDays, PriBal)");
		sql.append(" Values(:FinReference, :ProvMovementDate, :ProvMovementSeq, :ProvCalDate,");
		sql.append(" :ProvisionedAmt, :ProvisionAmtCal, :ProvisionDue, :ProvisionPostSts, :NonFormulaProv,");
		sql.append(
				" :UseNFProv, :AutoReleaseNFP, :PrincipalDue, :ProfitDue, :DueFromDate, :LastFullyPaidDate, :LinkedTranId,");
		sql.append(
				" :AssetCode, :AssetStageOrdr, :Npa, :ManualProvision, :ProvChgLinkedTranId, :PrvovisionRate, :DueDays, :PriBal)");

		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provisionMovement);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
		return provisionMovement.getId();
	}

	/**
	 * This method updates the Record FinProvMovements or FinProvMovements. if Record not updated then throws
	 * DataAccessException with error 41004. update Provision Movement Detail by key FinReference and Version
	 * 
	 * @param Provision
	 *            Movement Detail (provisionMovement)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(ProvisionMovement provisionMovement, String type) {
		int recordCount = 0;
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("Update FinProvMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set ProvisionedAmt = :ProvisionedAmt, ProvisionDue = :ProvisionDue, ");
		sql.append(" ProvisionPostSts = :ProvisionPostSts, LinkedTranId = :LinkedTranId, ");
		sql.append(
				" AssetCode = :AssetCode, AssetStageOrdr = :AssetStageOrdr, NPA = :NPA, ManualProvision = :ManualProvision, ");
		sql.append(
				" ProvChgLinkedTranId = :ProvChgLinkedTranId, PrvovisionRate = :PrvovisionRate, DueDays = :DueDays, PriBal = :PriBal");
		sql.append(" Where FinReference =:FinReference AND ProvMovementDate = :ProvMovementDate");
		sql.append(" AND ProvMovementSeq = :ProvMovementSeq");

		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provisionMovement);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}
}