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
 * FileName    		:  ReinstateFinanceDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.ReinstateFinanceDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ReinstateFinance;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;

/**
 * DAO methods implementation for the <b>ReinstateFinance model</b> class.<br>
 * 
 */
/**
 * @author manoj.c
 *
 */
public class ReinstateFinanceDAOImpl extends BasicDao<ReinstateFinance> implements ReinstateFinanceDAO {
	private static Logger logger = LogManager.getLogger(ReinstateFinanceDAOImpl.class);

	public ReinstateFinanceDAOImpl() {
		super();
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new ReinstateFinance
	 * 
	 * @return ReinstateFinance
	 */
	@Override
	public ReinstateFinance getReinstateFinance() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("ReinstateFinance");
		ReinstateFinance reinstateFinance = new ReinstateFinance();
		if (workFlowDetails != null) {
			reinstateFinance.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return reinstateFinance;
	}

	/**
	 * This method get the module from method getReinstateFinance() and set the new record flag as true and return
	 * ReinstateFinance()
	 * 
	 * @return ReinstateFinance
	 */
	@Override
	public ReinstateFinance getNewReinstateFinance() {
		logger.debug("Entering");
		ReinstateFinance reinstateFinance = getReinstateFinance();
		reinstateFinance.setNewRecord(true);
		logger.debug("Leaving");
		return reinstateFinance;
	}

	/**
	 * Fetch the Record ReinstateFinance Details details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return ReinstateFinance
	 */
	@Override
	public ReinstateFinance getReinstateFinanceById(String finReference, String type) {
		logger.debug("Entering");
		ReinstateFinance reinstateFinance = new ReinstateFinance();
		reinstateFinance.setFinReference(finReference);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" Select FinReference,");
		if (type.contains("View")) {
			selectSql.append(" FinPreApprovedRef,");
		}
		selectSql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  ReinstateFinance");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(reinstateFinance);
		RowMapper<ReinstateFinance> typeRowMapper = BeanPropertyRowMapper.newInstance(ReinstateFinance.class);

		try {
			reinstateFinance = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			reinstateFinance = null;
		}
		logger.debug("Leaving");
		return reinstateFinance;
	}

	@Override
	public ReinstateFinance getFinanceDetailsById(String finReference) {
		logger.debug("Entering");

		ReinstateFinance reinstateFinance = new ReinstateFinance();
		reinstateFinance.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT T1.FinReference, T2.CustCIF, T2.CustShrtName, T1.FinType, ");
		selectSql.append(
				" T3.FinTypeDesc LovDescFinTypeName, T1.FinBranch, T4.BranchDesc LovDescFinBranchName, T1.FinCcy,  ");
		selectSql.append(
				" FinAmount, DownPayment, FinStartDate, MaturityDate, TotalProfit, T7.UsrLogin RejectedBy, T1.LastMntOn RejectedOn, ");
		selectSql.append(" T9.Activity RejectStatus, T9.Remarks RejectRemarks ");
		selectSql
				.append(" ,T1.ScheduleMethod, T1.GrcPeriodEndDate, T1.AllowGrcPeriod, T1.ProfitDaysBasis, T3.Product ");
		selectSql.append(" From RejectFinanceMain T1 LEFT OUTER JOIN  ");
		selectSql.append(" Customers T2 ON T1.CustID = T2.CustID LEFT OUTER JOIN  ");
		selectSql.append(" RMTFinanceTypes T3 ON T1.FinType = T3.FinType LEFT OUTER JOIN  ");
		selectSql.append(" RMTBranches T4 ON T1.FinBranch = T4.BranchCode LEFT OUTER JOIN  ");
		selectSql.append(" BMTRejectCodes T6 ON T1.RejectStatus = T6.RejectCode LEFT OUTER JOIN  ");
		selectSql.append(" SecUsers T7 ON T1.LastMntBy = T7.UsrID LEFT OUTER JOIN");
		selectSql.append(
				" (SELECT REFERENCE,MAX(ID) AS ID FROM REASONHEADER GROUP BY REFERENCE)  T8 ON T8.REFERENCE = T1.FINREFERENCE");
		selectSql.append(
				" LEFT JOIN REASONHEADER T9 ON T9.ID=T8.ID AND T9.REFERENCE = T1.FINREFERENCE Where FinReference = :FinReference");

		SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(reinstateFinance);
		RowMapper<ReinstateFinance> rowMapper = BeanPropertyRowMapper.newInstance(ReinstateFinance.class);

		logger.debug("Leaving");
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), sqlParameterSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			return null;
		}
	}

	/**
	 * This method Deletes the Record from the BMTReinstateFinances or BMTReinstateFinances_Temp. if Record not deleted
	 * then throws DataAccessException with error 41003. delete ReinstateFinance Details by key ReinstateFinanceLevel
	 * 
	 * @param ReinstateFinance
	 *            Details (reinstateFinance)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(ReinstateFinance reinstateFinance, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("Delete From ReinstateFinance");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where  FinReference =:FinReference ");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(reinstateFinance);

		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BMTReinstateFinances or BMTReinstateFinances_Temp.
	 * 
	 * save ReinstateFinance Details
	 * 
	 * @param ReinstateFinance
	 *            Details (reinstateFinance)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(ReinstateFinance reinstateFinance, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into ReinstateFinance");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:FinReference, ");
		insertSql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(reinstateFinance);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return reinstateFinance.getFinReference();
	}

	/**
	 * This method updates the Record BMTReinstateFinances or BMTReinstateFinances_Temp. if Record not updated then
	 * throws DataAccessException with error 41004. update ReinstateFinance Details by key ReinstateFinanceLevel and
	 * Version
	 * 
	 * @param ReinstateFinance
	 *            Details (reinstateFinance)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(ReinstateFinance reinstateFinance, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update ReinstateFinance");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, ");
		updateSql.append(
				" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append("  Where FinReference =:FinReference ");
		if (!type.endsWith("_Temp")) {
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(reinstateFinance);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	//Reinstating Finance and Child Details From Rejected Details

	@Override
	public void processReInstateFinance(FinanceMain financeMain) {
		logger.debug("Entering");

		//Saving finance  child details into finance related tables.

		String finRef = financeMain.getFinReference();

		saveFinanceChildDetail(
				"INSERT INTO DocumentDetails_Temp SELECT * FROM  RejectDocumentDetails WHERE ReferenceId = :FinReference",
				finRef);
		saveFinanceChildDetail("FinAgreementDetail_Temp", "RejectFinAgreementDetail", finRef);
		saveFinanceChildDetail("FinanceEligibilityDetail", "RejectFinanceEligibilityDetail", finRef);
		saveFinanceChildDetail("FinanceScoreHeader", "RejectFinanceScoreHeader", finRef);
		saveFinanceChildDetail("FinContributorHeader_Temp", "RejectFinContributorHeader", finRef);
		saveFinanceChildDetail("FinContributorDetail_Temp", "RejectFinContributorDetail", finRef);
		saveFinanceChildDetail("FinDisbursementDetails_Temp", "RejectFinDisbursementdetails", finRef);
		saveFinanceChildDetail("FinRepayinstruction_Temp", "RejectFinRepayinstruction", finRef);
		saveFinanceChildDetail("FinScheduledetails_Temp", "RejectFinScheduledetails", finRef);
		saveFinanceChildDetail("FinDedupDetail", "RejectFinDedupDetail", finRef);
		saveFinanceChildDetail("FinBlackListDetail", "RejectFinBlackListDetail", finRef);
		saveFinanceChildDetail("FinODPenaltyRates_Temp", "RejectFinODPenaltyRates", finRef);
		saveFinanceChildDetail("FinFeeCharges_Temp", "RejectFinFeeCharges", finRef);

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(
				" INSERT INTO FinanceScoreDetail SELECT D.HeaderID,  D.SubGroupID, D.RuleId, D.MaxScore, D.ExecScore ");
		insertSql
				.append(" FROM RejectFinanceScoreDetail D INNER JOIN FinanceScoreHeader H ON D.HeaderID = H.HeaderId ");
		insertSql.append(" WHERE FinReference = :FinReference ");
		saveFinanceChildDetail(insertSql.toString(), finRef);
		insertSql.delete(0, insertSql.length());

		//Deleting finance and child details from Reject Tables.

		deleteChildDetailsByQuery("Delete FROM  RejectDocumentDetails WHERE ReferenceId = :FinReference", finRef);
		deleteChildDetailsByTableName("RejectFinAgreementDetail", finRef);
		deleteChildDetailsByTableName("RejectFinanceEligibilityDetail", finRef);
		deleteChildDetailsByTableName("RejectFinContributorDetail", finRef);
		deleteChildDetailsByTableName("RejectFinContributorHeader", finRef);
		deleteChildDetailsByTableName("RejectFinDisbursementdetails", finRef);
		deleteChildDetailsByTableName("RejectFinRepayinstruction", finRef);
		deleteChildDetailsByTableName("RejectFinScheduledetails", finRef);
		deleteChildDetailsByTableName("RejectFinDedupDetail", finRef);
		deleteChildDetailsByTableName("RejectFinBlackListDetail", finRef);
		deleteChildDetailsByTableName("RejectFinODPenaltyRates", finRef);
		deleteChildDetailsByTableName("RejectFinFeeCharges", finRef);

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From RejectFinanceScoreDetail Where HeaderID in (");
		deleteSql.append(" SELECT HeaderID FROM RejectFinanceScoreHeader ");
		deleteSql.append(" WHERE FinReference = :FinReference ) ");
		deleteChildDetailsByQuery(deleteSql.toString(), finRef);
		deleteSql.delete(0, insertSql.length());

		deleteChildDetailsByTableName("RejectFinanceScoreHeader", finRef);

		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into FinanceMain_Temp.
	 * 
	 * save Finance Main Detail
	 * 
	 * @param Finance
	 *            Main Detail (financeMain)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	private void saveFinanceChildDetail(String parentTable, String childTable, String finReference) {
		logger.debug("Entering");

		MapSqlParameterSource mapSource = new MapSqlParameterSource();
		mapSource.addValue("FinReference", finReference);

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" INSERT INTO ");
		insertSql.append(parentTable);
		insertSql.append(" SELECT * FROM ");
		insertSql.append(childTable);
		insertSql.append(" WHERE FinReference = :FinReference ");

		this.jdbcTemplate.update(insertSql.toString(), mapSource);
		logger.debug("Leaving");
	}

	private void saveFinanceChildDetail(String insertSql, String finReference) {
		logger.debug("Entering");

		MapSqlParameterSource mapSource = new MapSqlParameterSource();
		mapSource.addValue("FinReference", finReference);

		this.jdbcTemplate.update(insertSql, mapSource);
		logger.debug("Leaving");
	}

	private void deleteChildDetailsByTableName(String tableName, String finReference) {
		logger.debug("Entering");

		MapSqlParameterSource mapSource = new MapSqlParameterSource();
		mapSource.addValue("FinReference", finReference);

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From ");
		deleteSql.append(tableName);
		deleteSql.append(" WHERE FinReference = :FinReference ");

		this.jdbcTemplate.update(deleteSql.toString(), mapSource);
		logger.debug("Leaving");
	}

	private void deleteChildDetailsByQuery(String deleteSql, String finReference) {
		logger.debug("Entering");

		MapSqlParameterSource mapSource = new MapSqlParameterSource();
		mapSource.addValue("FinReference", finReference);

		this.jdbcTemplate.update(deleteSql, mapSource);
		logger.debug("Leaving");
	}

	/**
	 * This method Deletes the Record from the RejectFinanceMain.
	 * 
	 * @param finReference
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void deleteRejectFinance(ReinstateFinance reinstateFinance) {
		logger.debug("Entering");

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("Delete From RejectFinanceMain ");
		deleteSql.append(" Where  FinReference =:FinReference ");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(reinstateFinance);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
}