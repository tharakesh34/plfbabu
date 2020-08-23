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
 * FileName    		:  ProvisionDAOImpl.java                                                   * 	  
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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.financemanagement.ProvisionDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.financemanagement.ProvisionMovement;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods implementation for the <b>Provision model</b> class.<br>
 * 
 */
public class ProvisionDAOImpl extends BasicDao<Provision> implements ProvisionDAO {
	private static Logger logger = Logger.getLogger(ProvisionDAOImpl.class);

	public ProvisionDAOImpl() {
		super();
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new Provision
	 * 
	 * @return Provision
	 */

	@Override
	public Provision getProvision() {
		logger.debug(Literal.ENTERING);
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Provision");
		Provision provision = new Provision();
		if (workFlowDetails != null) {
			provision.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug(Literal.LEAVING);
		return provision;
	}

	/**
	 * This method get the module from method getProvision() and set the new record flag as true and return Provision()
	 * 
	 * @return Provision
	 */

	@Override
	public Provision getNewProvision() {
		logger.debug(Literal.ENTERING);
		Provision provision = getProvision();
		provision.setNewRecord(true);
		logger.debug(Literal.LEAVING);
		return provision;
	}

	/**
	 * Fetch the Record Provision details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Provision
	 */
	@Override
	public Provision getProvisionById(final String id, String type) {
		logger.debug(Literal.ENTERING);

		Provision provision = new Provision();
		provision.setId(id);

		StringBuilder sql = new StringBuilder("Select FinReference, FinBranch, FinType, ");
		sql.append(" CustID, ProvisionCalDate, ProvisionedAmt, ProvisionAmtCal, ProvisionDue, ");
		sql.append(" NonFormulaProv, UseNFProv, AutoReleaseNFP, PrincipalDue, ProfitDue, ");
		sql.append(" DueFromDate, LastFullyPaidDate, DueDays, priBal, AssetCode,");
		sql.append(" AssetStageOrdr, NPA, ManualProvision,PrvovisionRate,");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(" FinCcy, lovDescCustCIF, lovDescCustShrtName , ");
		}
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, ");
		sql.append(" NextTaskId, RecordType, WorkflowId");
		sql.append(" From FinProvisions");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference =:FinReference");

		logger.debug(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provision);
		RowMapper<Provision> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Provision.class);

		try {
			provision = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			provision = null;
		}
		logger.debug(Literal.LEAVING);
		return provision;
	}

	/**
	 * This method Deletes the Record from the FinProvisions or FinProvisions_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Provision by key FinReference
	 * 
	 * @param Provision
	 *            (provision)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(Provision provision, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Delete From FinProvisions");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference =:FinReference");
		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provision);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method insert new Records into FinProvisions or FinProvisions_Temp.
	 *
	 * save Provision
	 * 
	 * @param Provision
	 *            (provision)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(Provision provision, String type) {
		StringBuilder sql = new StringBuilder("Insert Into FinProvisions");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FinReference, FinBranch, FinType, CustID, ProvisionCalDate,");
		sql.append(" ProvisionedAmt, ProvisionAmtCal, ProvisionDue, NonFormulaProv, UseNFProv, AutoReleaseNFP,");
		sql.append(" PrincipalDue, ProfitDue, DueFromDate, LastFullyPaidDate, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append(" RecordType, WorkflowId,");
		sql.append(" DueDays,DpdBucketID,NpaBucketID,PftBal,PriBal,PrvovisionRate,");
		sql.append(" AssetCode, assetStageOrdr, NPA, ProvLinkedTranId, ProvChgLinkedTranId, ManualProvision) ");
		sql.append(" Values(:FinReference, :FinBranch, :FinType, :CustID, :ProvisionCalDate,");
		sql.append(" :ProvisionedAmt, :ProvisionAmtCal, :ProvisionDue, :NonFormulaProv,");
		sql.append(" :UseNFProv, :AutoReleaseNFP, :PrincipalDue, :ProfitDue, :DueFromDate, :LastFullyPaidDate, ");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,  ");
		sql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId,");
		sql.append(" :DueDays,:DpdBucketID,:NpaBucketID,:PftBal,:PriBal,:PrvovisionRate,");
		sql.append(" :AssetCode, :AssetStageOrdr, :Npa, :ProvLinkedTranId, :ProvChgLinkedTranId, :ManualProvision) ");
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provision);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
		return provision.getId();
	}

	/**
	 * This method updates the Record FinProvisions or FinProvisions_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Provision by key FinReference and Version
	 * 
	 * @param Provision
	 *            (provision)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(Provision provision, String type) {
		int recordCount = 0;
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("Update FinProvisions");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set FinBranch = :FinBranch,");
		sql.append(" FinType = :FinType, CustID = :CustID, ProvisionCalDate = :ProvisionCalDate,");
		sql.append(" ProvisionedAmt = :ProvisionedAmt, ProvisionAmtCal = :ProvisionAmtCal,");
		sql.append(" ProvisionDue = :ProvisionDue, NonFormulaProv = :NonFormulaProv,");
		sql.append(" UseNFProv = :UseNFProv, AutoReleaseNFP = :AutoReleaseNFP,");
		sql.append(" PrincipalDue = :PrincipalDue, ProfitDue = :ProfitDue, ");
		sql.append(" DueFromDate = :DueFromDate, LastFullyPaidDate = :LastFullyPaidDate, ");
		sql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, ");
		sql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		sql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId,");
		sql.append(" AssetCode = :AssetCode, assetStageOrdr = :assetStageOrdr, NPA = :Npa, ");
		sql.append(" PrvovisionRate = :PrvovisionRate, provLinkedTranId = :provLinkedTranId,");
		sql.append(" provChgLinkedTranId = :provChgLinkedTranId, manualProvision = :manualProvision ");
		sql.append(" Where FinReference =:FinReference");

		logger.debug(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provision);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for updating Provisioned Amount in Finance Provision Record
	 * 
	 * @param provisionMovement
	 * @param type
	 */
	@Override
	public void updateProvAmt(ProvisionMovement provisionMovement, String type) {
		int recordCount = 0;
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("Update FinProvisions");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set ProvisionedAmt = :ProvisionedAmt, ProvisionDue = :ProvisionDue ");
		sql.append(" Where FinReference =:FinReference ");
		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provisionMovement);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<Provision> getProcessedProvisions() {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder();
		sql.append(" Select  FinReference, provisionCalDate,provisionAmt as provisionedAmt, provisionAmtCal,");
		sql.append(" nonFormulaProv, useNFProv, prevProvisionCalDate, prevProvisionedAmt, transRef");
		sql.append(" From FinProcessedprovisions");

		logger.debug(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new Provision());
		RowMapper<Provision> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Provision.class);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public String saveProcessedProvisions(Provision provision) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Insert Into FinProcessedprovisions");
		sql.append(" (FinReference, provisionCalDate, provisionAmt, provisionAmtCal, nonFormulaProv,");
		sql.append(" useNFProv, prevProvisionCalDate, prevProvisionedAmt, transRef)");
		sql.append(" Values(:FinReference, :ProvisionCalDate, :ProvisionedAmt, :ProvisionAmtCal, ");
		sql.append(" :NonFormulaProv, :UseNFProv, :PrevProvisionCalDate, :PrevProvisionedAmt, :TransRef)");
		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provision);
		try {
			this.jdbcTemplate.update(sql.toString(), beanParameters);
		} catch (DataAccessException e) {
			logger.debug(e);
		}
		logger.debug(Literal.LEAVING);
		return provision.getId();
	}

	@Override
	public Provision getCurNPABucket(final String id) {
		Provision provision = new Provision();
		provision.setId(id);

		StringBuilder sql = new StringBuilder("Select NpaBucketID ");
		sql.append(" From FinProvisions");
		if (App.DATABASE == Database.SQL_SERVER) {
			sql.append(EodConstants.SQL_NOLOCK);
		}
		sql.append(" Where FinReference =:FinReference");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provision);
		RowMapper<Provision> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Provision.class);

		try {
			provision = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
			provision = null;
		}
		return provision;
	}

	@Override
	public void updateProvisonAmounts(Provision provision) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("Update FinProvisions");
		sql.append(" Set ProvisionCalDate = :ProvisionCalDate,ProvisionAmtCal=:ProvisionAmtCal,");
		sql.append(" ProvisionedAmt = :ProvisionedAmt, DueDays = :DueDays, DpdBucketID = :DpdBucketID,");
		sql.append(" NpaBucketID = :NpaBucketID, PftBal = :PftBal, PriBal = :PriBal,");
		sql.append(" PrvovisionRate = :PrvovisionRate, DueFromDate =:DueFromDate ");
		sql.append(" Where FinReference =:FinReference ");

		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provision);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public Provision getDMProvisionById(final String id, String type) {
		Provision provision = new Provision();

		provision.setId(id);

		StringBuilder sql = new StringBuilder("Select FinReference, FinBranch, FinType, ");
		sql.append(" CustID, ProvisionCalDate, ProvisionedAmt, ProvisionAmtCal, ProvisionDue, ");
		sql.append(" NonFormulaProv, UseNFProv, AutoReleaseNFP, PrincipalDue, ProfitDue, ");
		sql.append(" DueFromDate, LastFullyPaidDate ");
		sql.append(" From FinProvisions");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference =:FinReference");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provision);
		RowMapper<Provision> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Provision.class);

		try {
			provision = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			provision = null;
		}
		return provision;
	}

	@Override
	public boolean isProvisionExists(String finReference, TableType type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		StringBuilder sql = new StringBuilder("Select Count(*) From FinProvisions");
		sql.append(StringUtils.trimToEmpty(type.getSuffix()));
		sql.append(" Where FinReference =:FinReference");

		logger.debug(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		if (jdbcTemplate.queryForObject(sql.toString(), source, Integer.class) > 0) {
			return true;
		}
		logger.debug(Literal.LEAVING);
		return false;
	}

}