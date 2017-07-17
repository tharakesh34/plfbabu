/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ******************************************************************************************** 
 * FILE HEADER *
 ******************************************************************************************** 
 * * FileName : TreasuaryFinHeaderDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 04-11-2013 * *
 * Modified Date : 04-11-2013 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 04-11-2013 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.dao.finance.impl;

import java.util.List;

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

import com.pennant.backend.dao.finance.TreasuaryFinHeaderDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.InvestmentFinHeader;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>TreasuaryFinHeader model</b> class.<br>
 * 
 */

public class TreasuaryFinHeaderDAOImpl extends BasisCodeDAO<InvestmentFinHeader> implements
        TreasuaryFinHeaderDAO {

	private static Logger logger = Logger.getLogger(TreasuaryFinHeaderDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public TreasuaryFinHeaderDAOImpl() {
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the new TreasuaryFinHeader
	 * 
	 * @return TreasuaryFinHeader
	 */

	@Override
	public InvestmentFinHeader getTreasuaryFinHeader() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("InvestmentFinHeader");
		InvestmentFinHeader investmentFinance = new InvestmentFinHeader();
		if (workFlowDetails != null) {
			investmentFinance.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return investmentFinance;
	}

	/**
	 * This method get the module from method getTreasuaryFinHeader() and set the new record flag as true and return
	 * TreasuaryFinHeader()
	 * 
	 * @return TreasuaryFinHeader
	 */

	@Override
	public InvestmentFinHeader getNewTreasuaryFinHeader() {
		logger.debug("Entering");
		InvestmentFinHeader investmentFinance = getTreasuaryFinHeader();
		investmentFinance.setNewRecord(true);
		logger.debug("Leaving");
		return investmentFinance;
	}

	/**
	 * Fetch the Record TreasuaryFinHeader details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return TreasuaryFinHeader
	 */
	@Override
	public InvestmentFinHeader getTreasuaryFinHeaderById(final String id, String type) {
		logger.debug("Entering");

		InvestmentFinHeader investmentFinance = new InvestmentFinHeader();
		investmentFinance.setInvestmentRef(id);

		StringBuilder selectSql = new StringBuilder(
		        "Select InvestmentRef, TotPrincipalAmt, finCcy, ProfitDaysBasis, ");
		selectSql
		        .append(" StartDate, MaturityDate, PrincipalInvested, PrincipalMaturity, PrincipalDueToInvest,");
		selectSql.append(" AvgPftRate, ApprovalRequired,");
		selectSql
		        .append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql
			        .append(" ,lovDescfinCcyName, lovDescFinFormatter");
		}
		selectSql.append(" From InvestmentFinHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where InvestmentRef =:InvestmentRef");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(investmentFinance);
		RowMapper<InvestmentFinHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(InvestmentFinHeader.class);

		try {
			investmentFinance = this.namedParameterJdbcTemplate.queryForObject(
			        selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			investmentFinance = null;
		}
		logger.debug("Leaving");
		return investmentFinance;
	}

	/**
	 * Fetch the Record TreasuaryFinHeader details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return TreasuaryFinHeader
	 */
	@Override
	public InvestmentFinHeader getTreasuaryFinHeader(final String finReference, String tableType) {
		logger.debug("Entering");
		List<InvestmentFinHeader> invHeadeList = null;
		InvestmentFinHeader investmentFinHeader = null;
		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);
		
		StringBuilder selectSql = new StringBuilder(
		        "Select InvestmentRef, TotPrincipalAmt, finCcy,");
		selectSql
		        .append(" StartDate, MaturityDate, PrincipalInvested, PrincipalMaturity, PrincipalDueToInvest,");
		selectSql.append(" AvgPftRate, ApprovalRequired,");
		selectSql
		        .append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, ");
		if (StringUtils.trimToEmpty(tableType).contains("View")) {
			selectSql.append("lovDescfinCcyName, lovDescFinFormatter");
		}
		selectSql.append(" From InvestmentFinHeader");
		selectSql.append(StringUtils.trimToEmpty(tableType));
		selectSql.append(" Where InvestmentRef = ");
		selectSql
		        .append(" (select InvestmentRef FROM FinanceMain_Temp where FinReference=:FinReference)");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		RowMapper<InvestmentFinHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(InvestmentFinHeader.class);

		try {
			invHeadeList = this.namedParameterJdbcTemplate.query(selectSql.toString(),
			        beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			investmentFinHeader = new InvestmentFinHeader();
		} finally {
			financeMain = null;
		}

		if (invHeadeList != null && !invHeadeList.isEmpty()) {
			investmentFinHeader = invHeadeList.get(0);
		}
		logger.debug("Leaving");
		return investmentFinHeader;
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
	 * This method Deletes the Record from the TreasuaryFinHeader or TreasuaryFinHeader_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete TreasuaryFinHeader by key FinReference
	 * 
	 * @param InvestmentFinHeader
	 *            (treasuaryFinance)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(InvestmentFinHeader investmentFinance, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From InvestmentFinHeader");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where InvestmentRef =:InvestmentRef");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(investmentFinance);
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
	 * This method insert new Records into TreasuaryFinHeaders or TreasuaryFinHeaders_Temp.
	 * 
	 * save TreasuaryFinHeader
	 * 
	 * @param InvestmentFinHeader
	 *            (treasuaryFinance)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(InvestmentFinHeader investmentFinance, String type) {
		logger.debug("Entering");
		SqlParameterSource beanParameters = null;
		StringBuilder insertSql = null;

		insertSql = new StringBuilder("Insert Into InvestmentFinHeader");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql
		        .append(" (InvestmentRef, TotPrincipalAmt, finCcy, ProfitDaysBasis, StartDate, MaturityDate, PrincipalInvested, PrincipalMaturity, PrincipalDueToInvest,");
		insertSql.append(" AvgPftRate, ApprovalRequired, TotalDealsApproved, ");
		insertSql
		        .append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql
		        .append(" Values(:InvestmentRef, :TotPrincipalAmt, :finCcy, :ProfitDaysBasis, :StartDate, :MaturityDate, :PrincipalInvested, :PrincipalMaturity, :PrincipalDueToInvest,");
		insertSql.append(" :AvgPftRate, :ApprovalRequired, :TotalDealsApproved,");
		insertSql
		        .append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		beanParameters = new BeanPropertySqlParameterSource(investmentFinance);

		try {
			this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		} finally {
			insertSql = null;
			beanParameters = null;
		}

		logger.debug("Leaving");
		return investmentFinance.getId();
	}

	/**
	 * This method updates the Record TreasuaryFinHeader or TreasuaryFinHeader_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update TreasuaryFinHeader by key FinReference and Version
	 * 
	 * @param InvestmentFinHeader
	 *            (treasuaryFinance)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(InvestmentFinHeader investmentFinance, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = null;
		SqlParameterSource beanParameters = null;

		updateSql = new StringBuilder("Update InvestmentFinHeader");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql
		        .append(" Set TotPrincipalAmt = :TotPrincipalAmt, finCcy = :finCcy, ProfitDaysBasis = :ProfitDaysBasis, StartDate = :StartDate, MaturityDate = :MaturityDate, PrincipalInvested = :PrincipalInvested, PrincipalMaturity = :PrincipalMaturity, PrincipalDueToInvest = :PrincipalDueToInvest,");
		updateSql
		        .append(" AvgPftRate = :AvgPftRate, ApprovalRequired = :ApprovalRequired, TotalDealsApproved = :TotalDealsApproved, ");
		updateSql
		        .append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where InvestmentRef =:InvestmentRef");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		beanParameters = new BeanPropertySqlParameterSource(investmentFinance);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public List<FinanceMain> getInvestmentDealList(InvestmentFinHeader investmentFinHeader,  String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT FinReference, FinType, FinCcy, FinBranch, CustID, FinAmount,  ProfitDaysBasis, ScheduleMethod, TotalRepayAmt, RepayProfitRate, ");
		selectSql.append(" FinStartDate, MaturityDate, LastRepayDate, LastRepayPftDate, LastRepayRvwDate, LastRepayCpzDate, ");
		
		if (StringUtils.containsIgnoreCase(type, "VIEW")) {
			selectSql.append(" lovDescCustCif, lovDescCustShrtName, lovDescProductCodeName, lovDescFinFormatter,");
			selectSql.append(" lovDescFinBranchName, lovDescFinTypeName, lovDescFinCcyName,");
		}

		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  Investment_Deal_");
		selectSql.append(type);
		selectSql.append(" Where InvestmentRef = :InvestmentRef ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(investmentFinHeader);
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public FinanceMain getInvestmentDealById(FinanceMain financeMain, String tableType) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT FinReference, FinType, FinCcy, FinBranch, CustID, FinAmount,  TotalProfit, ProfitDaysBasis,");
		selectSql.append(" ScheduleMethod, TotalRepayAmt, RepayProfitRate, ");
		selectSql.append(" FinStartDate, MaturityDate, LastRepayDate, LastRepayPftDate, LastRepayRvwDate, LastRepayCpzDate,  ");
		selectSql.append(" DisbAccountId, RepayAccountId,  ");

		if (StringUtils.containsIgnoreCase(tableType, "VIEW")) {
			selectSql .append(" lovDescCustCif, lovDescCustShrtName, lovDescProductCodeName, lovDescFinFormatter,");
			selectSql.append(" lovDescFinBranchName, lovDescFinTypeName, lovDescFinCcyName,");
		}

		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  Investment_Deal");
		selectSql.append(tableType);
		selectSql.append(" Where FinReference = :FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(FinanceMain.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
			        beanParameters, typeRowMapper);
		} catch (Exception e) {
			logger.warn("Exception: ", e);
			return financeMain;
		}
	}

	@Override
	public void updateDealsStatus(String investmentReference) {
		logger.debug("Entering");
		int count = 0;
		SqlParameterSource beanParameters = null;
		StringBuilder query = null;

		InvestmentFinHeader header = new InvestmentFinHeader();
		header.setInvestmentRef(investmentReference);

		beanParameters = new BeanPropertySqlParameterSource(header);
		query = new StringBuilder(
		        "SELECT count(*) FROM FinanceMain_Temp Where InvestmentRef = :InvestmentRef");
		logger.debug("selectSql: " + query.toString());

		try {
			count = this.namedParameterJdbcTemplate.queryForObject(query.toString(), beanParameters, Integer.class);

			if (count == 0) {
				header.setTotalDealsApproved(true);
			}

			query = new StringBuilder("Update InvestmentFinHeader");
			query.append(" Set TotalDealsApproved = :TotalDealsApproved ");
			query.append(" Where InvestmentRef =:InvestmentRef");

			logger.debug("updateSql: " + query.toString());
			this.namedParameterJdbcTemplate.update(query.toString(), beanParameters);
		} catch (Exception e) {
			logger.warn("Exception: ", e);
		} finally {
			query = null;
			beanParameters = null;
		}

		logger.debug("Leaving");
	}

}