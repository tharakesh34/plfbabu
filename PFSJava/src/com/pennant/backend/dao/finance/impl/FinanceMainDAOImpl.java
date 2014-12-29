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
 * * FileName : FinanceMainDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 15-11-2011 * * Modified
 * Date : 15-11-2011 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 15-11-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.dao.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.AuditTransaction;
import com.pennant.backend.model.finance.BulkDefermentChange;
import com.pennant.backend.model.finance.BulkProcessDetails;
import com.pennant.backend.model.finance.CustomerFinanceDetail;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.reports.AvailCommitment;
import com.pennant.backend.model.reports.AvailFinance;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>FinanceMain model</b> class.<br>
 * 
 */
public class FinanceMainDAOImpl extends BasisCodeDAO<FinanceMain> implements FinanceMainDAO {

	private static Logger logger = Logger.getLogger(FinanceMainDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the new FinanceMain
	 * 
	 * @return FinanceMain
	 */
	@Override
	public FinanceMain getFinanceMain(boolean isWIF) {
		logger.debug("Entering");

		String wfModuleName = "";

		if (isWIF) {
			wfModuleName = "WIFFinanceMain";
		} else {
			wfModuleName = "FinanceMain";
		}

		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails(wfModuleName);
		FinanceMain financeMain = new FinanceMain();
		if (workFlowDetails != null) {
			financeMain.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return financeMain;
	}

	/**
	 * This method get the module from method getFinanceMain() and set the new record flag as true and return
	 * FinanceMain()
	 * 
	 * @return FinanceMain
	 */

	@Override
	public FinanceMain getNewFinanceMain(boolean isWIF) {
		logger.debug("Entering");
		FinanceMain financeMain = getFinanceMain(isWIF);
		financeMain.setNewRecord(true);
		logger.debug("Leaving");
		return financeMain;
	}
	
	/**
	 * Method for Check Have to Creating New Finance Accessibility for User or not
	 */
	@Override
    public boolean checkFirstTaskOwnerAccess(String productCode, long usrLogin) {
		logger.debug("Entering");

		int finTypeCount = 0;
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT FinTypeCount From FinanceCreationAccess_View");
		//selectSql.append(" Where ProductCode =:ProductCode AND UsrID =:UsrID ");
		selectSql.append(" Where UsrID =:UsrID ");

		logger.debug("selectSql: " + selectSql.toString());
		Map<String, String> parameterMap=new HashMap<String, String>();
		parameterMap.put("ProductCode", productCode);
		parameterMap.put("UsrID",String.valueOf(usrLogin));
		try {
			finTypeCount = this.namedParameterJdbcTemplate.queryForInt(selectSql.toString(), parameterMap);
		} catch (EmptyResultDataAccessException e) {
			finTypeCount = 0;
		}
		 
		logger.debug("Leaving");
		return finTypeCount > 0 ? true : false;
    }

	/**
	 * Fetch the Record Finance Main Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceMain
	 */
	@Override
	public FinanceMain getFinanceMainById(final String id, String type, boolean isWIF) {
		logger.debug("Entering");
		
		FinanceMain financeMain = new FinanceMain();
		financeMain.setId(id);

		StringBuilder selectSql = new StringBuilder("SELECT FinReference,GraceTerms, NumberOfTerms, GrcPeriodEndDate, AllowGrcPeriod,");
		selectSql.append(" GraceBaseRate, GraceSpecialRate, GrcPftRate, GrcPftFrq,NextGrcPftDate, AllowGrcPftRvw,");
		selectSql.append(" GrcPftRvwFrq, NextGrcPftRvwDate, AllowGrcCpz, GrcCpzFrq, NextGrcCpzDate,RepayBaseRate,");
		selectSql.append(" RepaySpecialRate, RepayProfitRate, RepayFrq, NextRepayDate, RepayPftFrq, NextRepayPftDate,");
		selectSql.append(" AllowRepayRvw,RepayRvwFrq, NextRepayRvwDate, AllowRepayCpz, RepayCpzFrq, NextRepayCpzDate,");
		selectSql.append(" MaturityDate, CpzAtGraceEnd,DownPayment, ReqRepayAmount, TotalProfit, DownPayBank, DownPaySupl, ");
		selectSql.append(" TotalCpz,TotalGrossPft,TotalGracePft,TotalGraceCpz,TotalGrossGrcPft, TotalRepayAmt,");
		selectSql.append(" GrcRateBasis, RepayRateBasis, FinType,FinRemarks, FinCcy, ScheduleMethod,");
		selectSql.append(" ProfitDaysBasis, ReqMaturity, CalTerms, CalMaturity, FirstRepay, LastRepay,");
		selectSql.append(" FinStartDate, FinAmount, FinRepaymentAmount, CustID, Defferments, FrqDefferments, ");
		selectSql.append(" FinBranch, FinSourceID, AllowedDefRpyChange, AvailedDefRpyChange, AllowedDefFrqChange,");
		selectSql.append(" AvailedDefFrqChange, RecalType, FinAssetValue, DisbAccountId, RepayAccountId, FinIsActive, ");
		selectSql.append(" LastRepayDate, LastRepayPftDate,LastRepayRvwDate, LastRepayCpzDate, AllowGrcRepay, GrcSchdMthd,");
		selectSql.append(" GrcMargin, RepayMargin, FinCommitmentRef, DepreciationFrq, FinCurrAssetValue,FinContractDate,");
		selectSql.append(" NextDepDate, LastDepDate, FinAccount, FinCustPftAccount, NextDepDate, LastDepDate, FinAccount, FinCustPftAccount,");
		selectSql.append(" AlwIndRate, IndBaseRate, GrcAlwIndRate, GrcIndBaseRate, ClosingStatus, FinApprovedDate, ");
		selectSql.append(" AnualizedPercRate , EffectiveRateOfReturn , FinRepayPftOnFrq , GrcProfitDaysBasis, StepFinance , StepPolicy, AlwManualSteps, NoOfSteps, ");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(" lovDescFinCcyName,lovDescScheduleMethodName, lovDescProfitDaysBasisName,");
			selectSql.append(" lovDescGraceBaseRateName,lovDescGraceSpecialRateName,lovDescFinTypeName,");
			selectSql.append(" lovDescRepayBaseRateName,lovDescRepaySpecialRateName,lovDescFinFormatter, lovDescAssetCodeName,");
			selectSql.append(" lovDescIndBaseRateName, lovDescGrcIndBaseRateName, FinRvwRateApplFor, FinGrcRvwRateApplFor,");
			selectSql.append(" lovDescCustCIF, lovDescCustShrtName, lovDescCustCtgTypeName, lovDescFinBranchName, ");
			if (!isWIF) {
				selectSql.append(" lovDescFinPurposeName, lovDescCustFName, lovDescCustLName, lovDescCustCoreBank,");
				selectSql.append(" lovDescSalutationName, lovDescJointCustCIF ,lovDescJointCustShrtName," );
				selectSql.append(" lovDescAccruedTillLBD, lovDescCommitmentRefName, lovDescFinScheduleOn,lovDescAuthorization1Name,lovDescAuthorization2Name,");
				selectSql.append(" lovDescProductCodeName,AvailCommitAmount, lovDescFinDivision, ");
			}			
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, ");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");

		if (isWIF) {
			selectSql.append(" From WIFFinanceMain");
		} else {
			selectSql.append(" , InvestmentRef , DownPayAccount,  SecurityDeposit, RcdMaintainSts, FinRepayMethod, ");
			selectSql.append(" SecurityCollateral , CustomerAcceptance , Approved , CbbApprovalRequired , CbbApproved , Discrepancy , LimitApproved, ");
			selectSql.append(" MigratedFinance, ScheduleMaintained, ScheduleRegenerated, CustDSR,JointAccount,JointCustId,");
			selectSql.append(" Blacklisted, FeeChargeAmt, LimitValid, OverrideLimit, FinPurpose,FinStatus, FinStsReason,Authorization1,Authorization2 ");
			selectSql.append(" From FinanceMain");
		}
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);

		try {
			financeMain = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
			        beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			financeMain = null;
		}
		logger.debug("Leaving");
		return financeMain;
	}
	
	/**
	 * Fetch the Record Finance Main Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceMain
	 */
	@Override
	public FinanceMain getFinanceMainForPftCalc(final String id) {
		logger.debug("Entering");
		
		FinanceMain financeMain = new FinanceMain();
		financeMain.setId(id);

		StringBuilder selectSql = new StringBuilder("SELECT FinReference, CustId, GrcPeriodEndDate, NextRepayPftDate, NextRepayRvwDate, FinIsActive, " );
		selectSql.append(" ProfitDaysBasis, FinStartDate, FinAssetValue, LastRepayPftDate,LastRepayRvwDate,FinCurrAssetValue, MaturityDate, FinStatus, FinStsReason, ");
		selectSql.append(" ClosingStatus, LastRepayDate, NextRepayDate ");
		selectSql.append(" From FinanceMain");
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);

		try {
			financeMain = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
			        beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			financeMain = null;
		}
		logger.debug("Leaving");
		return financeMain;
	}
	
	/**
	 * Fetch the Record Finance Main Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceMain
	 */
	@Override
	public FinanceMain getFinanceMainForRpyCancel(final String id) {
		logger.debug("Entering");
		
		FinanceMain financeMain = new FinanceMain();
		financeMain.setId(id);

		StringBuilder selectSql = new StringBuilder("SELECT FinReference, CustId, GrcPeriodEndDate, NextRepayPftDate, NextRepayRvwDate, " );
		selectSql.append(" FinStatus, FinAmount, FeeChargeAmt, FinRepaymentAmount, " );
		selectSql.append(" ProfitDaysBasis, FinStartDate, FinAssetValue, LastRepayPftDate,LastRepayRvwDate,FinCurrAssetValue, MaturityDate ");
		selectSql.append(" From FinanceMain");
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);

		try {
			financeMain = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
			        beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			financeMain = null;
		}
		logger.debug("Leaving");
		return financeMain;
	}

	/**
	 * Fetch the Record Finance Main Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceMain
	 */
	@Override
	public CustomerFinanceDetail getCustomerFinanceMainById(String id,String type){
		logger.debug("Entering");
		CustomerFinanceDetail customerFinanceDetail = new CustomerFinanceDetail();
		customerFinanceDetail.setFinReference(id); 
 		
		StringBuilder selectSql = new StringBuilder(" SELECT TOP 1 FinReference, FinBranch, CustID, CustCIF, CustShrtName," );
		selectSql.append(" CustDocType, CustDocTitle, PhoneNumber, CustEmail, RoleCode, NextRoleCode, RecordType, DeptDesc, RoleDesc, NextRoleDesc ");
		selectSql.append(" from CustomerFinanceDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");
		 
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerFinanceDetail);
		RowMapper<CustomerFinanceDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerFinanceDetail.class);
		
		try {
			customerFinanceDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			customerFinanceDetail = null;
		}
		logger.debug("Leaving");
		return customerFinanceDetail;
	}
	
	/**
	 * Fetch the Record Finance Main Detail details by key field
	 * 
	 * @param finReference (String)
	 * 
	 * @return FinanceMain
	 */
	@Override
	public FinanceMain getFinanceMainForBatch(final String finReference) {
		logger.debug("Entering");
		
		FinanceMain financeMain = new FinanceMain();
		financeMain.setId(finReference);
		
		StringBuilder selectSql = new StringBuilder("select  FinReference, GrcPeriodEndDate, FinRepaymentAmount," );
		selectSql.append(" DisbAccountid, RepayAccountid, FinAccount, FinCustPftAccount, FinCommitmentRef," );
		selectSql.append(" FinCcy, FinBranch, CustId, FinAmount, FeeChargeAmt, DownPayment, DownPayBank, DownPaySupl, DownPayAccount, SecurityDeposit, FinType, " );
		selectSql.append(" FinStartDate,GraceTerms, NumberOfTerms, NextGrcPftDate, nextRepayDate, LastRepayPftDate, NextRepayPftDate, " );
		selectSql.append(" LastRepayRvwDate, NextRepayRvwDate, FinAssetValue, FinCurrAssetValue," );
		selectSql.append(" RecordType, Version, ProfitDaysBasis , FeeChargeAmt, FinStatus, FinStsReason, SecurityDeposit, MaturityDate " );
		selectSql.append(" FROM Financemain");
		selectSql.append(" Where FinReference =:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
		
		try {
			financeMain = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			financeMain = null;
		}
		logger.debug("Leaving");
		return financeMain;
	}

	/**
	 * Fetch the Records Finance Main Detail details by key field
	 * 
	 * @param curBD
	 * @param nextBD
	 * @return
	 */
	@Override
	public List<String> getFinanceMainListByBatch(final Date curBD, final Date nextBD, String type) {
		logger.debug("Entering");

		FinanceMain financeMain = getNewFinanceMain(false);
		financeMain.setFinStartDate(curBD);
		financeMain.setMaturityDate(nextBD);

		StringBuilder selectSql = new StringBuilder("SELECT FinReference ");
		selectSql.append(" From FinanceMain");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinStartDate >=:FinStartDate AND MaturityDate <:MaturityDate");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), beanParameters,
		        String.class);

	}

	/**
	 * This method initialise the Record.
	 * 
	 * @param FinanceMain
	 *            (financeMain)
	 * @return FinanceMain
	 */
	@Override
	public void initialize(FinanceMain financeMain) {
		super.initialize(financeMain);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param FinanceMain
	 *            (financeMain)
	 * @return void
	 */
	@Override
	public void refresh(FinanceMain financeMain) {

	}

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	
	/*public void setAdtDataSource(DataSource dataSource) {
		this.adtNamedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}*/

	/**
	 * This method Deletes the Record from the FinanceMain or FinanceMain_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Finance Main Detail by key FinReference
	 * 
	 * @param Finance
	 *            Main Detail (financeMain)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(FinanceMain financeMain, String type, boolean isWIF) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From ");
		if (isWIF) {
			deleteSql.append(" WIFFinanceMain");
		} else {
			deleteSql.append(" FinanceMain");
		}
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),  beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails = getError("41003", financeMain.getId(), financeMain.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) { };
			}
		} catch (DataAccessException e) {
			logger.error(e);
			ErrorDetails errorDetails = getError("41006", financeMain.getId(), financeMain.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) { };
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into FinanceMain or FinanceMain_Temp.
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


	public String saveInvestmentFinance(FinanceMain financeMain, String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder("Insert Into ");
	
		insertSql.append(" FinanceMain");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, InvestmentRef , FinType, FinCcy, FinBranch, FinAmount, FinStartDate,");
		insertSql.append(" MaturityDate, CustID, RepayProfitRate , TotalRepayAmt ,");
		insertSql.append(" TotalProfit, ProfitDaysBasis, ScheduleMethod, disbAccountId, repayAccountId,");
		insertSql.append(" LastRepayDate, LastRepayPftDate, LastRepayRvwDate , LastRepayCpzDate ,");
		insertSql.append(" GraceTerms, NumberOfTerms, AllowGrcPeriod, AllowGrcPftRvw , AllowGrcCpz ,");
		insertSql.append(" AllowRepayRvw, AllowRepayCpz, CpzAtGraceEnd , CalTerms ,");
		insertSql.append(" Defferments, FrqDefferments, AllowedDefRpyChange , AvailedDefRpyChange ,");
		insertSql.append(" AllowedDefFrqChange, AvailedDefFrqChange, FinIsActive , AllowGrcRepay ,");
		insertSql.append(" AlwIndRate, GrcAlwIndRate, FinRepayPftOnFrq , SecurityCollateral ,");
		insertSql.append(" CustomerAcceptance, CbbApprovalRequired, CbbApproved , LimitApproved ,");
		insertSql.append(" MigratedFinance, ScheduleMaintained, ScheduleRegenerated , Blacklisted ,");		
		insertSql.append(" GrcProfitDaysBasis, StepFinance , StepPolicy, AlwManualSteps, NoOfSteps, ");		
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		insertSql.append(" NextTaskId, RecordType, WorkflowId)");
		
		insertSql.append(" Values(:FinReference, :InvestmentRef, :FinType, :FinCcy, :FinBranch, :FinAmount, :FinStartDate,");
		insertSql.append(" :MaturityDate, :CustID, :RepayProfitRate , :TotalRepayAmt ,");
		insertSql.append(" :TotalProfit, :ProfitDaysBasis, :ScheduleMethod, :DisbAccountId, :RepayAccountId,");
		insertSql.append(" :LastRepayDate, :LastRepayPftDate, :LastRepayRvwDate, :LastRepayCpzDate,");
		insertSql.append(" :GraceTerms, :NumberOfTerms, :AllowGrcPeriod, :AllowGrcPftRvw, :AllowGrcCpz,");
		insertSql.append(" :AllowRepayRvw, :AllowRepayCpz, :CpzAtGraceEnd, :CalTerms,");
		insertSql.append(" :Defferments, :FrqDefferments, :AllowedDefRpyChange, :AvailedDefRpyChange,");
		insertSql.append(" :AllowedDefFrqChange, :AvailedDefFrqChange, :FinIsActive , :AllowGrcRepay,");
		insertSql.append(" :AlwIndRate, :GrcAlwIndRate, :FinRepayPftOnFrq , :SecurityCollateral,");
		insertSql.append(" :CustomerAcceptance, :CbbApprovalRequired, :CbbApproved, :LimitApproved,");
		insertSql.append(" :MigratedFinance, :ScheduleMaintained, :ScheduleRegenerated, :Blacklisted,");
		insertSql.append(" :GrcProfitDaysBasis, :StepFinance, :StepPolicy, :AlwManualSteps, :NoOfSteps, ");
		insertSql.append(" :Version ,:LastMntBy,:LastMntOn,:RecordStatus,:RoleCode,:NextRoleCode,:TaskId,");
		insertSql.append(" :NextTaskId,:RecordType,:WorkflowId)");
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return financeMain.getId();
	}
	
	@Override
	public String save(FinanceMain financeMain, String type, boolean isWIF) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into ");
		if (isWIF) {
			insertSql.append(" WIFFinanceMain");
		} else {
			insertSql.append(" FinanceMain");
		}
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, GraceTerms,  NumberOfTerms, GrcPeriodEndDate, AllowGrcPeriod,");
		insertSql.append(" GraceBaseRate, GraceSpecialRate, GrcPftRate, GrcPftFrq,NextGrcPftDate, AllowGrcPftRvw,");
		insertSql.append(" GrcPftRvwFrq, NextGrcPftRvwDate, AllowGrcCpz, GrcCpzFrq, NextGrcCpzDate,RepayBaseRate,");
		insertSql.append(" RepaySpecialRate, RepayProfitRate, RepayFrq, NextRepayDate, RepayPftFrq, NextRepayPftDate,");
		insertSql.append(" AllowRepayRvw,RepayRvwFrq, NextRepayRvwDate, AllowRepayCpz, RepayCpzFrq, NextRepayCpzDate,");
		insertSql.append(" MaturityDate, CpzAtGraceEnd,DownPayment, DownPayBank, DownPaySupl, ReqRepayAmount, TotalProfit,");
		insertSql.append(" TotalCpz,TotalGrossPft,TotalGracePft, TotalGraceCpz,TotalGrossGrcPft, TotalRepayAmt,");
		insertSql.append("  GrcRateBasis, RepayRateBasis,FinType,FinRemarks, FinCcy, ScheduleMethod,FinContractDate,");
		insertSql.append(" ProfitDaysBasis, ReqMaturity, CalTerms, CalMaturity, FirstRepay, LastRepay,");
		insertSql.append(" FinStartDate, FinAmount, FinRepaymentAmount, CustID, Defferments,FrqDefferments,");
		insertSql.append(" FinBranch, FinSourceID, AllowedDefRpyChange, AvailedDefRpyChange, AllowedDefFrqChange,");
		insertSql.append(" AvailedDefFrqChange, RecalType, FinIsActive,FinAssetValue, disbAccountId, repayAccountId, ");
		insertSql.append(" LastRepayDate, LastRepayPftDate, LastRepayRvwDate, LastRepayCpzDate, AllowGrcRepay, GrcSchdMthd,");
		insertSql.append(" GrcMargin, RepayMargin, FinCommitmentRef, DepreciationFrq, FinCurrAssetValue,");
		insertSql.append(" NextDepDate, LastDepDate, FinAccount, FinCustPftAccount, ClosingStatus, FinApprovedDate, ");
		insertSql.append(" AlwIndRate, IndBaseRate, GrcAlwIndRate, GrcIndBaseRate,DedupFound,SkipDedup,Blacklisted,");
		insertSql.append(" GrcProfitDaysBasis, StepFinance , StepPolicy, AlwManualSteps, NoOfSteps, ");
		insertSql.append(" AnualizedPercRate , EffectiveRateOfReturn , FinRepayPftOnFrq, ");
		if (!isWIF) {
			insertSql.append(" InvestmentRef, MigratedFinance, ScheduleMaintained, ScheduleRegenerated,CustDSR,Authorization1,Authorization2,");
			insertSql.append(" FeeChargeAmt,LimitValid, OverrideLimit,FinPurpose,FinStatus, FinStsReason,");
			insertSql.append(" JointAccount,JointCustId,DownPayAccount, SecurityDeposit, RcdMaintainSts,FinRepayMethod, ");
			insertSql.append(" SecurityCollateral , CustomerAcceptance , Approved , CbbApprovalRequired , CbbApproved , Discrepancy , LimitApproved, ");
		}
		
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		insertSql.append(" NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:FinReference,:GraceTerms, :NumberOfTerms, :GrcPeriodEndDate, :AllowGrcPeriod,");
		insertSql.append(" :GraceBaseRate, :GraceSpecialRate,:GrcPftRate,:GrcPftFrq,:NextGrcPftDate,:AllowGrcPftRvw,");
		insertSql.append(" :GrcPftRvwFrq,:NextGrcPftRvwDate,:AllowGrcCpz,:GrcCpzFrq,:NextGrcCpzDate,:RepayBaseRate,");
		insertSql.append(" :RepaySpecialRate,:RepayProfitRate,:RepayFrq,:NextRepayDate,:RepayPftFrq,:NextRepayPftDate,");
		insertSql.append(" :AllowRepayRvw,:RepayRvwFrq,:NextRepayRvwDate,:AllowRepayCpz,:RepayCpzFrq,:NextRepayCpzDate,");
		insertSql.append(" :MaturityDate,:CpzAtGraceEnd,:DownPayment, :DownPayBank, :DownPaySupl, :ReqRepayAmount,:TotalProfit,");
		insertSql.append(" :TotalCpz,:TotalGrossPft,:TotalGracePft,:TotalGraceCpz,:TotalGrossGrcPft, :TotalRepayAmt,");
		insertSql.append(" :GrcRateBasis,:RepayRateBasis, :FinType,:FinRemarks,:FinCcy,:ScheduleMethod,:FinContractDate,");
		insertSql.append(" :ProfitDaysBasis,:ReqMaturity,:CalTerms,:CalMaturity,:FirstRepay,:LastRepay,");
		insertSql.append(" :FinStartDate,:FinAmount,:FinRepaymentAmount,:CustID,:Defferments,:FrqDefferments,");
		insertSql.append(" :FinBranch, :FinSourceID, :AllowedDefRpyChange, :AvailedDefRpyChange, :AllowedDefFrqChange,");
		insertSql.append(" :AvailedDefFrqChange, :RecalType, :FinIsActive,:FinAssetValue, :disbAccountId, :repayAccountId, ");
		insertSql.append(" :LastRepayDate, :LastRepayPftDate, :LastRepayRvwDate, :LastRepayCpzDate,:AllowGrcRepay, :GrcSchdMthd,");
		insertSql.append(" :GrcMargin, :RepayMargin, :FinCommitmentRef, :DepreciationFrq, :FinCurrAssetValue,");
		insertSql.append(" :NextDepDate, :LastDepDate, :FinAccount, :FinCustPftAccount, :ClosingStatus , :FinApprovedDate, ");
		insertSql.append(" :AlwIndRate, :IndBaseRate, :GrcAlwIndRate, :GrcIndBaseRate, :DedupFound,:SkipDedup,:Blacklisted,");
		insertSql.append(" :GrcProfitDaysBasis, :StepFinance , :StepPolicy, :AlwManualSteps, :NoOfSteps, ");
		insertSql.append(" :AnualizedPercRate , :EffectiveRateOfReturn , :FinRepayPftOnFrq, ");
		if (!isWIF) {
			insertSql.append("  :InvestmentRef, :MigratedFinance, :ScheduleMaintained, :ScheduleRegenerated, :CustDSR, :Authorization1, :Authorization2, ");
			insertSql.append(" :FeeChargeAmt, :LimitValid, :OverrideLimit,:FinPurpose,:FinStatus, :FinStsReason,");
			insertSql.append(" :JointAccount,:JointCustId , :DownPayAccount,  :SecurityDeposit, :RcdMaintainSts,:FinRepayMethod, ");
			insertSql.append(" :SecurityCollateral , :CustomerAcceptance , :Approved , :CbbApprovalRequired , :CbbApproved , :Discrepancy , :LimitApproved, ");
		}
		insertSql.append(" :Version ,:LastMntBy,:LastMntOn,:RecordStatus,:RoleCode,:NextRoleCode,:TaskId,");
		insertSql.append(" :NextTaskId,:RecordType,:WorkflowId)");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return financeMain.getId();
	}
	

	/**
	 * This method updates the Record FinanceMain or FinanceMain_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Finance Main Detail by key FinReference and Version
	 * 
	 * @param Finance
	 *            Main Detail (financeMain)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@SuppressWarnings("serial")
		public void updateInvestmentFinance(FinanceMain financeMain, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update ");
		 
		updateSql.append(" FinanceMain");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set FinType = :FinType,  FinCcy = :FinCcy, FinBranch = :FinBranch,");
		updateSql.append(" FinAmount = :FinAmount, FinStartDate = :FinStartDate, ");
		updateSql.append(" MaturityDate = :MaturityDate, CustID = :CustID,");
		updateSql.append(" RepayProfitRate = :RepayProfitRate, TotalRepayAmt= :TotalRepayAmt, ");
		updateSql.append(" TotalProfit = :TotalProfit, ProfitDaysBasis= :ProfitDaysBasis, ");
		updateSql.append(" ScheduleMethod = :ScheduleMethod, ");
		updateSql.append(" DisbAccountId = :DisbAccountId, RepayAccountId= :RepayAccountId, ");
		updateSql.append(" LastRepayDate = :LastRepayDate, LastRepayPftDate = :LastRepayPftDate, ");
		updateSql.append(" LastRepayRvwDate = :LastRepayRvwDate, LastRepayCpzDate = :LastRepayCpzDate, ");
		updateSql.append(" NumberOfTerms = :NumberOfTerms, GraceTerms=:GraceTerms, AllowGrcPeriod = :AllowGrcPeriod, ");
		updateSql.append(" AllowGrcPftRvw = :AllowGrcPftRvw, AllowGrcCpz = :AllowGrcCpz, ");
		updateSql.append(" AllowRepayRvw = :AllowRepayRvw, AllowRepayCpz = :AllowRepayCpz, ");
		updateSql.append(" CpzAtGraceEnd = :CpzAtGraceEnd, CalTerms = :CalTerms, ");
		updateSql.append(" Defferments = :Defferments, FrqDefferments = :FrqDefferments, ");
		updateSql.append(" AllowedDefRpyChange = :AllowedDefRpyChange, AvailedDefRpyChange = :AvailedDefRpyChange, ");
		updateSql.append(" AllowedDefFrqChange = :AllowedDefFrqChange, AvailedDefFrqChange = :AvailedDefFrqChange, ");
		updateSql.append(" FinIsActive = :FinIsActive, AllowGrcRepay = :AllowGrcRepay, ");
		updateSql.append(" AlwIndRate = :AlwIndRate, GrcAlwIndRate = :GrcAlwIndRate, ");
		updateSql.append(" FinRepayPftOnFrq = :FinRepayPftOnFrq, SecurityCollateral = :SecurityCollateral, ");
		updateSql.append(" CustomerAcceptance = :CustomerAcceptance, CbbApprovalRequired = :CbbApprovalRequired, ");
		updateSql.append(" CbbApproved = :CbbApproved, LimitApproved = :LimitApproved, ");
		updateSql.append(" MigratedFinance = :MigratedFinance, ScheduleMaintained = :ScheduleMaintained, ");
		updateSql.append(" ScheduleRegenerated = :ScheduleRegenerated, Blacklisted = :Blacklisted, GrcProfitDaysBasis = :GrcProfitDaysBasis,");
		updateSql.append(" StepFinance = :StepFinance, StepPolicy = :StepPolicy, AlwManualSteps = :AlwManualSteps, NoOfSteps = :NoOfSteps, ");
		
		/*updateSql.append(" NextGrcPftDate = :NextGrcPftDate, AllowGrcPftRvw = :AllowGrcPftRvw,");
		updateSql.append(" GrcPftRvwFrq = :GrcPftRvwFrq, NextGrcPftRvwDate = :NextGrcPftRvwDate,");
		updateSql.append(" AllowGrcCpz = :AllowGrcCpz, GrcCpzFrq = :GrcCpzFrq, NextGrcCpzDate = :NextGrcCpzDate,");
		updateSql.append(" RepayBaseRate = :RepayBaseRate, RepaySpecialRate = :RepaySpecialRate,");
		updateSql.append("  RepayFrq = :RepayFrq, NextRepayDate = :NextRepayDate,");
		updateSql.append(" RepayPftFrq = :RepayPftFrq, NextRepayPftDate = :NextRepayPftDate,");
		updateSql.append(" AllowRepayRvw = :AllowRepayRvw, RepayRvwFrq = :RepayRvwFrq,");
		updateSql.append(" NextRepayRvwDate = :NextRepayRvwDate, AllowRepayCpz = :AllowRepayCpz,");
		updateSql.append(" RepayCpzFrq = :RepayCpzFrq, NextRepayCpzDate = :NextRepayCpzDate,");
		updateSql.append(" MaturityDate = :MaturityDate, CpzAtGraceEnd = :CpzAtGraceEnd, DownPayment = :DownPayment,");
		updateSql.append(" DownPayBank=:DownPayBank, DownPaySupl=:DownPaySupl, ");
		updateSql.append(" ReqRepayAmount = :ReqRepayAmount,");
		updateSql.append(" TotalProfit = :TotalProfit,TotalCpz= :TotalCpz, TotalGrossPft = :TotalGrossPft,");
		updateSql.append(" TotalGracePft= :TotalGracePft,TotalGraceCpz= :TotalGraceCpz,TotalGrossGrcPft= :TotalGrossGrcPft,");
		updateSql.append("  GrcRateBasis = :GrcRateBasis, RepayRateBasis = :RepayRateBasis, ");
		updateSql.append(" FinRemarks = :FinRemarks, FinCcy = :FinCcy, ScheduleMethod = :ScheduleMethod,");
		updateSql.append(" ProfitDaysBasis = :ProfitDaysBasis, ReqMaturity = :ReqMaturity, CalTerms = :CalTerms,");
		updateSql.append(" CalMaturity = :CalMaturity, FirstRepay = :FirstRepay, LastRepay = :LastRepay,");
		updateSql.append(" FinContractDate= :FinContractDate,");
		updateSql.append(" FinRepaymentAmount = :FinRepaymentAmount,  Defferments = :Defferments,");
		updateSql.append(" FrqDefferments= :FrqDefferments,  FinSourceID= :FinSourceID,");
		updateSql.append(" AllowedDefRpyChange= :AllowedDefRpyChange, AvailedDefRpyChange= :AvailedDefRpyChange,");
		updateSql.append(" AllowedDefFrqChange= :AllowedDefFrqChange, AvailedDefFrqChange= :AvailedDefFrqChange,");
		updateSql.append(" RecalType=:RecalType, FinIsActive= :FinIsActive,FinAssetValue= :FinAssetValue,");
		updateSql.append(" disbAccountId= :disbAccountId, repayAccountId= :repayAccountId,");
		updateSql.append(" LastRepayDate= :LastRepayDate,LastRepayPftDate= :LastRepayPftDate,");
		updateSql.append(" LastRepayRvwDate= :LastRepayRvwDate, LastRepayCpzDate= :LastRepayCpzDate,AllowGrcRepay= :AllowGrcRepay,");
		updateSql.append(" GrcSchdMthd= :GrcSchdMthd, GrcMargin= :GrcMargin,RepayMargin= :RepayMargin,");
		updateSql.append(" FinCommitmentRef= :FinCommitmentRef, DepreciationFrq= :DepreciationFrq, FinCurrAssetValue= :FinCurrAssetValue,");
		updateSql.append(" NextDepDate= :NextDepDate, LastDepDate= :LastDepDate,FinAccount=:FinAccount,");
		updateSql.append(" AlwIndRate = :AlwIndRate, IndBaseRate = :IndBaseRate, GrcAlwIndRate = :GrcAlwIndRate,");
		updateSql.append(" GrcIndBaseRate = :GrcIndBaseRate, FinCustPftAccount=:FinCustPftAccount,ClosingStatus= :ClosingStatus, ");
		updateSql.append(" FinApprovedDate= :FinApprovedDate,"); 
		updateSql.append(" AnualizedPercRate =:AnualizedPercRate , EffectiveRateOfReturn =:EffectiveRateOfReturn , FinRepayPftOnFrq =:FinRepayPftOnFrq ,");
		*/
		/*if (!isWIF) {
			updateSql.append(" MigratedFinance = :MigratedFinance,ScheduleMaintained = :ScheduleMaintained, ScheduleRegenerated = :ScheduleRegenerated,");
			updateSql.append(" FeeChargeAmt=:FeeChargeAmt, LimitValid= :LimitValid, OverrideLimit= :OverrideLimit,FinPurpose=:FinPurpose, " );
			updateSql.append(" FinStatus=:FinStatus , FinStsReason=:FinStsReason, CustDSR=:CustDSR, Authorization1=:Authorization1,Authorization2=:Authorization2,");
			updateSql.append(" JointAccount=:JointAccount, JointCustId=:JointCustId, DownPayAccount=:DownPayAccount,  SecurityDeposit = :SecurityDeposit, ");
			updateSql.append(" SecurityCollateral=:SecurityCollateral , CustomerAcceptance=:CustomerAcceptance , Approved=:Approved, " );
			updateSql.append(" CbbApprovalRequired=:CbbApprovalRequired , CbbApproved=:CbbApproved , Discrepancy=:Discrepancy , LimitApproved=:LimitApproved, ");
		}*/
		
		updateSql.append(" Version = :Version,LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinReference =:FinReference");
		if (!type.endsWith("_TEMP") ) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", financeMain.getId(), financeMain.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) { };
		}
		logger.debug("Leaving");
	}

	/**
	 * This method updates the Record FinanceMain or FinanceMain_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Finance Main Detail by key FinReference and Version
	 * 
	 * @param Finance
	 *            Main Detail (financeMain)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@SuppressWarnings("serial")
	@Override
	public void update(FinanceMain financeMain, String type, boolean isWIF) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update ");
		if (isWIF) {
			updateSql.append(" WIFFinanceMain");
		} else {
			updateSql.append(" FinanceMain");
		}
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set  NumberOfTerms = :NumberOfTerms,GraceTerms=:GraceTerms, ");
		updateSql.append(" GrcPeriodEndDate = :GrcPeriodEndDate, AllowGrcPeriod = :AllowGrcPeriod,");
		updateSql.append(" GraceBaseRate = :GraceBaseRate, GraceSpecialRate = :GraceSpecialRate,");
		updateSql.append(" GrcPftRate = :GrcPftRate, GrcPftFrq = :GrcPftFrq,");
		updateSql.append(" NextGrcPftDate = :NextGrcPftDate, AllowGrcPftRvw = :AllowGrcPftRvw,");
		updateSql.append(" GrcPftRvwFrq = :GrcPftRvwFrq, NextGrcPftRvwDate = :NextGrcPftRvwDate,");
		updateSql.append(" AllowGrcCpz = :AllowGrcCpz, GrcCpzFrq = :GrcCpzFrq, NextGrcCpzDate = :NextGrcCpzDate,");
		updateSql.append(" RepayBaseRate = :RepayBaseRate, RepaySpecialRate = :RepaySpecialRate,");
		updateSql.append(" RepayProfitRate = :RepayProfitRate, RepayFrq = :RepayFrq, NextRepayDate = :NextRepayDate,");
		updateSql.append(" RepayPftFrq = :RepayPftFrq, NextRepayPftDate = :NextRepayPftDate,");
		updateSql.append(" AllowRepayRvw = :AllowRepayRvw, RepayRvwFrq = :RepayRvwFrq,");
		updateSql.append(" NextRepayRvwDate = :NextRepayRvwDate, AllowRepayCpz = :AllowRepayCpz,");
		updateSql.append(" RepayCpzFrq = :RepayCpzFrq, NextRepayCpzDate = :NextRepayCpzDate,");
		updateSql.append(" MaturityDate = :MaturityDate, CpzAtGraceEnd = :CpzAtGraceEnd, DownPayment = :DownPayment,");
		updateSql.append(" DownPayBank=:DownPayBank, DownPaySupl=:DownPaySupl, ReqRepayAmount = :ReqRepayAmount,");
		updateSql.append(" TotalProfit = :TotalProfit,TotalCpz= :TotalCpz, TotalGrossPft = :TotalGrossPft,");
		updateSql.append(" TotalGracePft= :TotalGracePft,TotalGraceCpz= :TotalGraceCpz,TotalGrossGrcPft= :TotalGrossGrcPft,");
		updateSql.append(" TotalRepayAmt= :TotalRepayAmt, GrcRateBasis = :GrcRateBasis, RepayRateBasis = :RepayRateBasis, FinType = :FinType,");
		updateSql.append(" FinRemarks = :FinRemarks, FinCcy = :FinCcy, ScheduleMethod = :ScheduleMethod,");
		updateSql.append(" ProfitDaysBasis = :ProfitDaysBasis, ReqMaturity = :ReqMaturity, CalTerms = :CalTerms,");
		updateSql.append(" CalMaturity = :CalMaturity, FirstRepay = :FirstRepay, LastRepay = :LastRepay,");
		updateSql.append(" FinStartDate = :FinStartDate, FinAmount = :FinAmount,FinContractDate= :FinContractDate,");
		updateSql.append(" FinRepaymentAmount = :FinRepaymentAmount, CustID = :CustID,Defferments = :Defferments,");
		updateSql.append(" FrqDefferments= :FrqDefferments, FinBranch =:FinBranch, FinSourceID= :FinSourceID,");
		updateSql.append(" AllowedDefRpyChange= :AllowedDefRpyChange, AvailedDefRpyChange= :AvailedDefRpyChange,");
		updateSql.append(" AllowedDefFrqChange= :AllowedDefFrqChange, AvailedDefFrqChange= :AvailedDefFrqChange,");
		updateSql.append(" RecalType=:RecalType, FinIsActive= :FinIsActive,FinAssetValue= :FinAssetValue,");
		updateSql.append(" disbAccountId= :disbAccountId, repayAccountId= :repayAccountId,");
		updateSql.append(" LastRepayDate= :LastRepayDate,LastRepayPftDate= :LastRepayPftDate,");
		updateSql.append(" LastRepayRvwDate= :LastRepayRvwDate, LastRepayCpzDate= :LastRepayCpzDate,AllowGrcRepay= :AllowGrcRepay,");
		updateSql.append(" GrcSchdMthd= :GrcSchdMthd, GrcMargin= :GrcMargin,RepayMargin= :RepayMargin,");
		updateSql.append(" FinCommitmentRef= :FinCommitmentRef, DepreciationFrq= :DepreciationFrq, FinCurrAssetValue= :FinCurrAssetValue,");
		updateSql.append(" NextDepDate= :NextDepDate, LastDepDate= :LastDepDate,FinAccount=:FinAccount, AlwIndRate = :AlwIndRate, ");
		updateSql.append(" IndBaseRate = :IndBaseRate, GrcAlwIndRate = :GrcAlwIndRate,");
		updateSql.append(" GrcIndBaseRate = :GrcIndBaseRate, FinCustPftAccount=:FinCustPftAccount,ClosingStatus= :ClosingStatus, FinApprovedDate= :FinApprovedDate,"); 
		updateSql.append(" AnualizedPercRate =:AnualizedPercRate , EffectiveRateOfReturn =:EffectiveRateOfReturn , FinRepayPftOnFrq =:FinRepayPftOnFrq ,");
		updateSql.append(" GrcProfitDaysBasis = :GrcProfitDaysBasis, StepFinance = :StepFinance, StepPolicy = :StepPolicy,");
		updateSql.append(" AlwManualSteps = :AlwManualSteps, NoOfSteps = :NoOfSteps, ");
		
		if (!isWIF) {
			updateSql.append(" MigratedFinance = :MigratedFinance,ScheduleMaintained = :ScheduleMaintained, ScheduleRegenerated = :ScheduleRegenerated,");
			updateSql.append(" FeeChargeAmt=:FeeChargeAmt, LimitValid= :LimitValid, OverrideLimit= :OverrideLimit,FinPurpose=:FinPurpose, " );
			updateSql.append(" FinStatus=:FinStatus , FinStsReason=:FinStsReason, CustDSR=:CustDSR, Authorization1=:Authorization1,Authorization2=:Authorization2,");
			updateSql.append(" JointAccount=:JointAccount, JointCustId=:JointCustId, DownPayAccount=:DownPayAccount,  SecurityDeposit = :SecurityDeposit, RcdMaintainSts=:RcdMaintainSts, ");
			updateSql.append(" FinRepayMethod=:FinRepayMethod, SecurityCollateral=:SecurityCollateral , CustomerAcceptance=:CustomerAcceptance , Approved=:Approved, " );
			updateSql.append(" CbbApprovalRequired=:CbbApprovalRequired , CbbApproved=:CbbApproved , Discrepancy=:Discrepancy , LimitApproved=:LimitApproved, ");
		}
		
		updateSql.append(" Version = :Version,LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinReference =:FinReference");
		if (!type.endsWith("_TEMP") && !isWIF) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", financeMain.getId(), financeMain.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) { };
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to return the customer details based on given customer id.
	 */
	public boolean isFinReferenceExists(final String id, String type, boolean isWIF) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setId(id);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT FinReference");

		if (isWIF) {
			selectSql.append(" From WIFFinanceMain");
		} else {
			selectSql.append(" From FinanceMain");
		}

		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);

		try {
			financeMain = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),  beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			financeMain = null;
		}
		logger.debug("Leaving");
		return financeMain == null? false:true;
	}

	@Override
	public void listUpdate(ArrayList<FinanceMain> financeMain, String type) {
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set FinReference = :FinReference, NumberOfTerms = :NumberOfTerms, GraceTerms=:GraceTerms, ");
		updateSql.append(" GrcPeriodEndDate = :GrcPeriodEndDate, AllowGrcPeriod = :AllowGrcPeriod,");
		updateSql.append(" GraceBaseRate = :GraceBaseRate, GraceSpecialRate = :GraceSpecialRate,");
		updateSql.append(" GrcPftRate = :GrcPftRate, GrcPftFrq = :GrcPftFrq,");
		updateSql.append(" NextGrcPftDate = :NextGrcPftDate, AllowGrcPftRvw = :AllowGrcPftRvw,");
		updateSql.append(" GrcPftRvwFrq = :GrcPftRvwFrq, NextGrcPftRvwDate = :NextGrcPftRvwDate,");
		updateSql.append(" AllowGrcCpz = :AllowGrcCpz, GrcCpzFrq = :GrcCpzFrq, NextGrcCpzDate = :NextGrcCpzDate,");
		updateSql.append(" RepayBaseRate = :RepayBaseRate, RepaySpecialRate = :RepaySpecialRate,");
		updateSql.append(" RepayProfitRate = :RepayProfitRate, RepayFrq = :RepayFrq, NextRepayDate = :NextRepayDate,");
		updateSql.append(" RepayPftFrq = :RepayPftFrq, NextRepayPftDate = :NextRepayPftDate,");
		updateSql.append(" AllowRepayRvw = :AllowRepayRvw, RepayRvwFrq = :RepayRvwFrq,");
		updateSql.append(" NextRepayRvwDate = :NextRepayRvwDate, AllowRepayCpz = :AllowRepayCpz,");
		updateSql.append(" RepayCpzFrq = :RepayCpzFrq, NextRepayCpzDate = :NextRepayCpzDate,");
		updateSql.append(" MaturityDate = :MaturityDate, CpzAtGraceEnd = :CpzAtGraceEnd, DownPayment = :DownPayment,");
		updateSql.append(" DownPayBank=:DownPayBank, DownPaySupl=:DownPaySupl, DownPayAccount=:DownPayAccount,  SecurityDeposit = :SecurityDeposit,");
		updateSql.append(" ReqRepayAmount = :ReqRepayAmount, TotalProfit = :TotalProfit,TotalCpz= :TotalCpz, TotalGrossPft = :TotalGrossPft,");
		updateSql.append(" TotalGracePft= :TotalGracePft,TotalGraceCpz= :TotalGraceCpz,TotalGrossGrcPft= :TotalGrossGrcPft,");
		updateSql.append(" TotalRepayAmt= :TotalRepayAmt, GrcRateBasis = :GrcRateBasis, RepayRateBasis = :RepayRateBasis, FinType = :FinType,");
		updateSql.append(" FinRemarks = :FinRemarks, FinCcy = :FinCcy, ScheduleMethod = :ScheduleMethod,");
		updateSql.append(" ProfitDaysBasis = :ProfitDaysBasis, ReqMaturity = :ReqMaturity, CalTerms = :CalTerms,");
		updateSql.append(" CalMaturity = :CalMaturity, FirstRepay = :FirstRepay, LastRepay = :LastRepay,");
		updateSql.append(" FinStartDate = :FinStartDate, FinAmount = :FinAmount,FinContractDate=:FinContractDate,");
		updateSql.append(" FinRepaymentAmount = :FinRepaymentAmount, CustID = :CustID,Defferments = :Defferments, ");
		updateSql.append(" FrqDefferments= :FrqDefferments, FinBranch =:FinBranch, FinSourceID= :FinSourceID,");
		updateSql.append(" AllowedDefRpyChange= :AllowedDefRpyChange, AvailedDefRpyChange= :AvailedDefRpyChange,");
		updateSql.append(" AllowedDefFrqChange= :AllowedDefFrqChange, AvailedDefFrqChange= :AvailedDefFrqChange,");
		updateSql.append(" RecalType=:RecalType, FinIsActive= :FinIsActive,FinAssetValue= :FinAssetValue,");
		updateSql.append(" disbAccountId= :disbAccountId, repayAccountId= :repayAccountId,FinPurpose=:FinPurpose,");
		updateSql.append(" LastRepayDate= :LastRepayDate,LastRepayPftDate= :LastRepayPftDate, FinStatus=:FinStatus, FinStsReason=:FinStsReason,");
		updateSql.append(" LastRepayRvwDate= :LastRepayRvwDate, LastRepayCpzDate= :LastRepayCpzDate,AllowGrcRepay= :AllowGrcRepay,");
		updateSql.append(" GrcSchdMthd= :GrcSchdMthd, GrcMargin= :GrcMargin, RepayMargin= :RepayMargin,");
		updateSql.append(" FinCommitmentRef= :FinCommitmentRef, DepreciationFrq= :DepreciationFrq, FinCurrAssetValue= :FinCurrAssetValue,");
		updateSql.append(" NextDepDate= :NextDepDate, LastDepDate= :LastDepDate,FinAccount=:FinAccount,");
		updateSql.append(" AlwIndRate = :AlwIndRate, IndBaseRate = :IndBaseRate, GrcAlwIndRate = :GrcAlwIndRate,");
		updateSql.append(" GrcIndBaseRate = :GrcIndBaseRate, FinCustPftAccount= :FinCustPftAccount, ClosingStatus= :ClosingStatus, ");
		updateSql.append(" FinApprovedDate= :FinApprovedDate, FeeChargeAmt=:FeeChargeAmt, LimitValid= :LimitValid, OverrideLimit= :OverrideLimit,");
		updateSql.append(" AnualizedPercRate =:AnualizedPercRate , EffectiveRateOfReturn =:EffectiveRateOfReturn , " );
		updateSql.append(" FinRepayPftOnFrq =:FinRepayPftOnFrq , CustDSR=:CustDSR, Authorization1=:Authorization1,Authorization2=:Authorization2,");
		updateSql.append(" JointAccount=:JointAccount,JointCustId=:JointCustId, DownPayAccount=:DownPayAccount,  SecurityDeposit =:SecurityDeposit, RcdMaintainSts=:RcdMaintainSts,");
		updateSql.append(" FinRepayMethod=:FinRepayMethod, SecurityCollateral=:SecurityCollateral , CustomerAcceptance=:CustomerAcceptance , Approved=:Approved, " );
		updateSql.append(" CbbApprovalRequired=:CbbApprovalRequired , CbbApproved=:CbbApproved , Discrepancy=:Discrepancy , LimitApproved=:LimitApproved, ");
		updateSql.append(" Version = :Version,LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(" GrcProfitDaysBasis = :GrcProfitDaysBasis, StepFinance = :StepFinance, StepPolicy = :StepPolicy,");
		updateSql.append(" AlwManualSteps = :AlwManualSteps, NoOfSteps = :NoOfSteps, ");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinReference =:FinReference");
		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(financeMain
		        .toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@SuppressWarnings("serial")
	@Override
	public void updateFinAccounts(String finReference, String finAccount) {
		int recordCount = 0;
		
		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);
		financeMain.setFinAccount(finAccount);
		
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(" Set FinAccount =:FinAccount ");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", financeMain.getId(), financeMain
			        .getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) { };
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for get the Actual Profit Amount
	 * @throws ClassNotFoundException 
	 * @throws DataAccessException 
	 */
    @Override
    public List<BigDecimal> getActualPftBal(String finReference, String type) {
		logger.debug("Entering");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT TotalProfit, TotalCpz From FinanceMain");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference ='" + finReference + "'");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		logger.debug("Leaving");

		financeMain = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),beanParameters,typeRowMapper);
		List<BigDecimal> list = new ArrayList<BigDecimal>();
		list.add(financeMain.getTotalProfit());
		list.add(financeMain.getTotalCpz());
		return list;
	}

	@SuppressWarnings("serial")
    @Override
    public void updateRepaymentAmount(String finReference, BigDecimal finAmount, BigDecimal repaymentAmount, 
    		String finStatus, String finStsReason, boolean isCancelProc) {
		logger.debug("Entering");
		
		int recordCount = 0;
		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);
		financeMain.setFinRepaymentAmount(repaymentAmount);
		financeMain.setFinStatus(finStatus);
		financeMain.setFinStsReason(finStsReason);
		
		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(" Set FinRepaymentAmount =:FinRepaymentAmount ");
		if(finAmount.subtract(repaymentAmount).compareTo(BigDecimal.ZERO) <= 0){
			financeMain.setFinIsActive(false);
			financeMain.setClosingStatus(PennantConstants.CLOSE_STATUS_MATURED);
			updateSql.append(" , FinIsActive = :FinIsActive, ClosingStatus =:ClosingStatus ");
		}else if(isCancelProc){
			financeMain.setFinIsActive(true);
			financeMain.setClosingStatus("");
			updateSql.append(" , FinIsActive = :FinIsActive, ClosingStatus =:ClosingStatus ");
		}
		
		updateSql.append(" , FinStatus = :FinStatus , FinStsReason = :FinStsReason ");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", financeMain.getId(), financeMain
			        .getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) { };
		}
    }
	
	/**
	 * Method for get the Finance Details and FinanceShedule Details
	 */
	@Override
	public List<FinanceEnquiry> getFinanceDetailsByCustId(long custId) {
		logger.debug("Entering");
		FinanceEnquiry finEnquiry =new FinanceEnquiry();
		finEnquiry.setCustID(custId);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT FinReference , FinBranch , FinType , FinCcy , ScheduleMethod , ProfitDaysBasis , FinStartDate , NumberOfTerms , " );
		selectSql.append(" CustID , FinAmount , GrcPeriodEndDate , MaturityDate , FinRepaymentAmount , FinIsActive , AllowGrcPeriod , " );
		selectSql.append(" LovDescFinFormatter , LovDescFinCcyName , LovDescScheduleMethodName , LovDescProfitDaysBasisName , LovDescFinTypeName , " );
		selectSql.append(" LovDescCustCIF , LovDescCustShrtName , LovDescFinBranchName , Blacklisted , LovDescFinScheduleOn , FeeChargeAmt , " );
		selectSql.append(" ClosingStatus , CustTypeCtg , GraceTerms , lovDescFinDivision , lovDescProductCodeName , Defferments ");
		selectSql.append(" FROM FinanceEnquiry_View ");
		selectSql.append(" Where CustID=:CustID and IsNull(ClosingStatus,'') != 'C'");
		selectSql.append(" ORDER BY FinType, FinCcy ");
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finEnquiry);
		RowMapper<FinanceEnquiry> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(FinanceEnquiry.class);

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
		        typeRowMapper);
	}

	@SuppressWarnings("serial")
    @Override
    public void updateCustCIF(long custID, String finReference) {
		logger.debug("Entering");
		
		int recordCount = 0;
		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);
		financeMain.setCustID(custID);
		
		StringBuilder updateSql = new StringBuilder("Update FinanceMain_Temp");
		updateSql.append(" Set CustID =:CustID ");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", financeMain.getId(), financeMain
			        .getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) { };
		}
	    logger.debug("Leaving");
    }

	@SuppressWarnings("serial")
    @Override
	public void updateFinBlackListStatus(String finReference) {
		logger.debug("Entering");

		int recordCount = 0;
		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);
		financeMain.setBlacklisted(true);

		StringBuilder updateSql = new StringBuilder("Update FinanceMain");
		updateSql.append(" Set Blacklisted =:Blacklisted ");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", financeMain.getId(), financeMain
					.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) { };
		}
		logger.debug("Leaving");
	}

	@Override
    public List<String> getFinanceReferenceList() {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("SELECT FinReference From FinanceMain");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource("");
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), beanParameters,
		        String.class);
    }
	public FinanceSummary getFinanceProfitDetails(String finRef) {
		logger.debug("Entering");
		FinanceSummary summary = new FinanceSummary();
		
		StringBuilder selectSql = new StringBuilder("Select * from FinanceProfitEnquiry_View");
		selectSql.append("  where FinReference = '"+finRef+"'");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(summary);
		RowMapper<FinanceSummary> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceSummary.class);
		try {
			summary = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			summary = new FinanceSummary();
		}
		logger.debug("Leaving");
		return summary;
	}

	/**
	 * Method for fetching List Of IJARAH Finance for Bulk Rate Change
	 */
	@Override
    public List<BulkProcessDetails> getIjaraBulkRateFinList(Date fromDate, Date toDate) {
		logger.debug("Entering");
		
		BulkProcessDetails changeFinance = new BulkProcessDetails();
		changeFinance.setLovDescEventFromDate(fromDate);
		changeFinance.setLovDescEventToDate(toDate);

		StringBuilder selectSql = new StringBuilder(" SELECT FinReference, FinType, " );
		selectSql.append(" FinCcy, ScheduleMethod, ProfitDaysBasis, CustCIF, FinBranch, " );
		selectSql.append(" ProductCode, EventFromDate, EventToDate " );
		selectSql.append(" FROM IjarahFinance( :LovDescEventFromDate, :LovDescEventToDate )");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(changeFinance);
		RowMapper<BulkProcessDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BulkProcessDetails.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
    }

	/**
	 * Method for Fetch List of Finance for Bulk Deferment Process
	 */
	@Override
    public List<BulkDefermentChange> getBulkDefermentFinList(Date fromDate, Date toDate) {
		logger.debug("Entering");
		
		BulkDefermentChange changeFinance = new BulkDefermentChange();
		changeFinance.setEventFromDate(fromDate);
		changeFinance.setEventToDate(toDate);

		StringBuilder selectSql = new StringBuilder(" SELECT FinReference, FinType, " );
		selectSql.append(" FinCcy, ScheduleMethod, ProfitDaysBasis, CustCIF, FinBranch, " );
		selectSql.append(" ProductCode, EventFromDate " );
		selectSql.append(" FROM BulkDefermentFinance( :EventFromDate, :EventToDate )");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(changeFinance);
		RowMapper<BulkDefermentChange> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BulkDefermentChange.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
    }

	/**
	 * Fetch the Records Finance Main Detail details by key field
	 * 
	 * @param curBD
	 * @param nextBD
	 * @return
	 */
	@Override
	public List<AuditTransaction> getFinTransactionsList(String id, boolean approvedFinance) {
		logger.debug("Entering");
		CustomerFinanceDetail customerFinanceDetail = new CustomerFinanceDetail();
		customerFinanceDetail.setFinReference(id); 
		customerFinanceDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		customerFinanceDetail.setRecordStatus("Saved");
		StringBuilder selectSql = new StringBuilder(" ");
		selectSql.append("SELECT AuditReference, AuditDate, RoleCode, RoleDesc, LastMntBy, RecordStatus, RecordType, UsrName "); 
 		selectSql.append(" from FinanceStatusApprovalInquiry_View ");
 		selectSql.append(" Where AuditReference =:FinReference and RecordType = :RecordType ");
 		if(approvedFinance){
 			selectSql.append(" and RecordStatus <> :RecordStatus");
 		}
 		selectSql.append(" Order by AuditDate ");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerFinanceDetail);
		RowMapper<AuditTransaction> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(AuditTransaction.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
	}
	
	/**
	 * 
	 */
	public Boolean saveRejectFinanceDetails(FinanceMain financeMain){
		logger.debug("Entering");

		saveRejectFinanace(financeMain);

		StringBuilder insertSql = new StringBuilder(" Insert into RejectDocumentDetails select * from ");
		insertSql.append(" DocumentDetails  where ReferenceId = :FinReference ");
		savetoDFinanceMain(insertSql.toString(), financeMain);
		insertSql.delete(0, insertSql.length());

		insertSql.append(" Insert into RejectFinAgreementDetail select * from ");
		insertSql.append(" FinAgreementDetail  where FinReference = :FinReference ");
		savetoDFinanceMain(insertSql.toString(), financeMain);
		insertSql.delete(0, insertSql.length());

		insertSql.append(" Insert into RejectFinanceEligibilityDetail select * from ");
		insertSql.append(" FinanceEligibilityDetail  where FinReference = :FinReference ");
		savetoDFinanceMain(insertSql.toString(), financeMain);
		insertSql.delete(0, insertSql.length());

		insertSql.append(" Insert into RejectFinanceScoreHeader select * from ");
		insertSql.append(" FinanceScoreHeader  where FinReference = :FinReference ");
		savetoDFinanceMain(insertSql.toString(), financeMain);
		insertSql.delete(0, insertSql.length());

		insertSql.append(" Insert into RejectFinanceScoreDetail select detail.HeaderID, ");
		insertSql.append("detail.SubGroupID, detail.RuleId, detail.MaxScore, detail.ExecScore" );
		insertSql.append(" from FinanceScoreDetail detail ");
		insertSql.append("inner join RejectFinanceScoreHeader header on detail.HeaderID = header.HeaderId ");
		insertSql.append(" where FinReference = :FinReference ");
		savetoDFinanceMain(insertSql.toString(), financeMain);
		insertSql.delete(0, insertSql.length());

		insertSql.append(" Insert into RejectFinBillingHeader select * from ");
		insertSql.append(" FinBillingHeader  where FinReference = :FinReference ");
		savetoDFinanceMain(insertSql.toString(), financeMain);
		insertSql.delete(0, insertSql.length());

		insertSql.append(" Insert into RejectFinContributorDetail select * from ");
		insertSql.append(" FinContributorDetail  where FinReference = :FinReference ");
		savetoDFinanceMain(insertSql.toString(), financeMain);
		insertSql.delete(0, insertSql.length());

		insertSql.append(" Insert into RejectFinAgreementDetail select * from ");
		insertSql.append(" FinAgreementDetail  where FinReference = :FinReference ");
		savetoDFinanceMain(insertSql.toString(), financeMain);
		insertSql.delete(0, insertSql.length());

		insertSql.append(" Insert into RejectFinContributorHeader select * from ");
		insertSql.append(" FinContributorHeader  where FinReference = :FinReference ");
		savetoDFinanceMain(insertSql.toString(), financeMain);
		insertSql.delete(0, insertSql.length());

		insertSql.append(" Insert into RejectFinDisbursementdetails select * from ");
		insertSql.append(" finDisbursementdetails  where FinReference = :FinReference ");
		savetoDFinanceMain(insertSql.toString(), financeMain);
		insertSql.delete(0, insertSql.length());

		insertSql.append(" Insert into RejectFinRepayinstruction select * from ");
		insertSql.append(" FinRepayinstruction  where FinReference = :FinReference ");
		savetoDFinanceMain(insertSql.toString(), financeMain);
		insertSql.delete(0, insertSql.length());

		insertSql.append(" Insert into RejectFinScheduledetails select * from ");
		insertSql.append(" finScheduledetails  where FinReference = :FinReference ");
		savetoDFinanceMain(insertSql.toString(), financeMain);
		insertSql.delete(0, insertSql.length()); 

		return true;
	}

	/**
	 * 
	 */
	public void savetoDFinanceMain (String insertSql, FinanceMain financeMain){
		logger.debug("Entering");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		this.namedParameterJdbcTemplate.update(insertSql, beanParameters);
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into FinanceMain or FinanceMain_Temp.
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
  	public String saveRejectFinanace(FinanceMain financeMain) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into ");
		insertSql.append(" RejectFinanceMain ");
 		insertSql.append(" (FinReference, GraceTerms, NumberOfTerms, GrcPeriodEndDate, AllowGrcPeriod,");
		insertSql.append(" GraceBaseRate, GraceSpecialRate, GrcPftRate, GrcPftFrq,NextGrcPftDate, AllowGrcPftRvw,");
		insertSql.append(" GrcPftRvwFrq, NextGrcPftRvwDate, AllowGrcCpz, GrcCpzFrq, NextGrcCpzDate,RepayBaseRate,");
		insertSql.append(" RepaySpecialRate, RepayProfitRate, RepayFrq, NextRepayDate, RepayPftFrq, NextRepayPftDate,");
		insertSql.append(" AllowRepayRvw,RepayRvwFrq, NextRepayRvwDate, AllowRepayCpz, RepayCpzFrq, NextRepayCpzDate,");
		insertSql.append(" MaturityDate, CpzAtGraceEnd,DownPayment, DownPayBank, DownPaySupl, ReqRepayAmount, TotalProfit,");
		insertSql.append(" TotalCpz,TotalGrossPft,TotalGracePft, TotalGraceCpz,TotalGrossGrcPft, TotalRepayAmt,");
		insertSql.append(" GrcRateBasis, RepayRateBasis,FinType,FinRemarks, FinCcy, ScheduleMethod,FinContractDate,");
		insertSql.append(" ProfitDaysBasis, ReqMaturity, CalTerms, CalMaturity, FirstRepay, LastRepay,");
		insertSql.append(" FinStartDate, FinAmount, FinRepaymentAmount, CustID, Defferments,FrqDefferments,");
		insertSql.append(" FinBranch, FinSourceID, AllowedDefRpyChange, AvailedDefRpyChange, AllowedDefFrqChange,");
		insertSql.append(" AvailedDefFrqChange, RecalType, FinIsActive,FinAssetValue, disbAccountId, repayAccountId, ");
		insertSql.append(" LastRepayDate, LastRepayPftDate, LastRepayRvwDate, LastRepayCpzDate, AllowGrcRepay, GrcSchdMthd,");
		insertSql.append(" GrcMargin, RepayMargin, FinCommitmentRef, DepreciationFrq, FinCurrAssetValue,");
		insertSql.append(" NextDepDate, LastDepDate, FinAccount, FinCustPftAccount, ClosingStatus, FinApprovedDate, ");
		insertSql.append(" AlwIndRate, IndBaseRate, GrcAlwIndRate, GrcIndBaseRate,DedupFound,SkipDedup,Blacklisted,");
		insertSql.append(" AnualizedPercRate , EffectiveRateOfReturn , FinRepayPftOnFrq ,");
		insertSql.append(" MigratedFinance, ScheduleMaintained, ScheduleRegenerated,CustDSR,Authorization1,Authorization2,");
		insertSql.append(" FeeChargeAmt,LimitValid, OverrideLimit,FinPurpose,FinStatus, FinStsReason,");
		insertSql.append(" JointAccount,JointCustId,DownPayAccount, SecurityDeposit,  ");
		insertSql.append(" GrcProfitDaysBasis, StepFinance, StepPolicy, AlwManualSteps, NoOfSteps, ");
		
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		insertSql.append(" NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:FinReference,:GraceTerms, :NumberOfTerms, :GrcPeriodEndDate, :AllowGrcPeriod,");
		insertSql.append(" :GraceBaseRate, :GraceSpecialRate,:GrcPftRate,:GrcPftFrq,:NextGrcPftDate,:AllowGrcPftRvw,");
		insertSql.append(" :GrcPftRvwFrq,:NextGrcPftRvwDate,:AllowGrcCpz,:GrcCpzFrq,:NextGrcCpzDate,:RepayBaseRate,");
		insertSql.append(" :RepaySpecialRate,:RepayProfitRate,:RepayFrq,:NextRepayDate,:RepayPftFrq,:NextRepayPftDate,");
		insertSql.append(" :AllowRepayRvw,:RepayRvwFrq,:NextRepayRvwDate,:AllowRepayCpz,:RepayCpzFrq,:NextRepayCpzDate,");
		insertSql.append(" :MaturityDate,:CpzAtGraceEnd,:DownPayment, :DownPayBank, :DownPaySupl, :ReqRepayAmount,:TotalProfit,");
		insertSql.append(" :TotalCpz,:TotalGrossPft,:TotalGracePft,:TotalGraceCpz,:TotalGrossGrcPft, :TotalRepayAmt,");
		insertSql.append(" :GrcRateBasis,:RepayRateBasis, :FinType,:FinRemarks,:FinCcy,:ScheduleMethod,:FinContractDate,");
		insertSql.append(" :ProfitDaysBasis,:ReqMaturity,:CalTerms,:CalMaturity,:FirstRepay,:LastRepay,");
		insertSql.append(" :FinStartDate,:FinAmount,:FinRepaymentAmount,:CustID,:Defferments,:FrqDefferments,");
		insertSql.append(" :FinBranch, :FinSourceID, :AllowedDefRpyChange, :AvailedDefRpyChange, :AllowedDefFrqChange,");
		insertSql.append(" :AvailedDefFrqChange, :RecalType, :FinIsActive,:FinAssetValue, :disbAccountId, :repayAccountId, ");
		insertSql.append(" :LastRepayDate, :LastRepayPftDate, :LastRepayRvwDate, :LastRepayCpzDate,:AllowGrcRepay, :GrcSchdMthd,");
		insertSql.append(" :GrcMargin, :RepayMargin, :FinCommitmentRef, :DepreciationFrq, :FinCurrAssetValue,");
		insertSql.append(" :NextDepDate, :LastDepDate, :FinAccount, :FinCustPftAccount, :ClosingStatus , :FinApprovedDate, ");
		insertSql.append(" :AlwIndRate, :IndBaseRate, :GrcAlwIndRate, :GrcIndBaseRate, :DedupFound,:SkipDedup,:Blacklisted,");
		insertSql.append(" :AnualizedPercRate , :EffectiveRateOfReturn , :FinRepayPftOnFrq ,");
		insertSql.append(" :MigratedFinance, :ScheduleMaintained, :ScheduleRegenerated, :CustDSR, :Authorization1, :Authorization2, ");
		insertSql.append(" :FeeChargeAmt, :LimitValid, :OverrideLimit,:FinPurpose,:FinStatus, :FinStsReason,");
		insertSql.append(" :JointAccount,:JointCustId , :DownPayAccount,  :SecurityDeposit,");
		insertSql.append(" :GrcProfitDaysBasis, :StepFinance, :StepPolicy, :AlwManualSteps, :NoOfSteps, ");
		insertSql.append(" :Version ,:LastMntBy,:LastMntOn,:RecordStatus,:RoleCode,:NextRoleCode,:TaskId,");
		insertSql.append(" :NextTaskId,:RecordType,:WorkflowId)");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return financeMain.getId();
	}
	
  	/**
	 * Fetch the Records Finance Main Detail details by key field
	 * 
	 * @param curBD
	 * @param nextBD
	 * @return
	 */
	@Override
	public List<AvailFinance> getFinanceDetailByCmtRef(String cmtRef, long custId) {
		logger.debug("Entering");
		AvailCommitment commitment = new AvailCommitment();
		commitment.setCmtReference(cmtRef);
		commitment.setCustId(custId);
		
		StringBuilder selectSql = new StringBuilder("SELECT FinReference , FinCcy , FinAmount, DrawnPrinciple , OutStandingBal, " );
		selectSql.append(" CcySpotRate , LastRepay , MaturityDate , ProfitRate , RepayFrq , Status, CcyEditField, FinDivision , FinDivisionDesc"); 
 		selectSql.append(" from AvailFinance_View ");
 		selectSql.append(" Where FinCommitmentRef =:CmtReference AND OutStandingBal > 0 ");
 		if(custId != 0){
 			selectSql.append(" AND CustId =:CustId ");
 		}
 		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commitment);
		RowMapper<AvailFinance> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(AvailFinance.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
	}
	
	/**
	 * Method for Updation of Effective Rate of Return for Finance
	 * 
	 * @param curBD
	 * @param nextBD
	 * @return
	 */
	@SuppressWarnings("serial")
    @Override
	public void updateFinanceERR(String finReference, Date lastRepayDate,Date lastRepayPftDate, BigDecimal effectiveRate, String type) {
		logger.debug("Entering");
		
		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finReference);
		financeMain.setEffectiveRateOfReturn(effectiveRate);
		financeMain.setLastRepayDate(lastRepayDate);
		financeMain.setLastRepayPftDate(lastRepayPftDate);
		
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder(" Update FinanceMain " );
		updateSql.append(" SET EffectiveRateOfReturn =:EffectiveRateOfReturn, LastRepayDate =:LastRepayDate, LastRepayPftDate=:LastRepayPftDate ");
		updateSql.append(" Where FinReference =:FinReference ");
 		
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", financeMain.getId(), financeMain.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) { };
		}
		logger.debug("Leaving");
	}

	private ErrorDetails getError(String errorId, String finReference, String userLanguage) {
		String[][] parms = new String[2][1];
		parms[1][0] = finReference;
		parms[0][0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId,
		        parms[0], parms[1]), userLanguage);
	}

}