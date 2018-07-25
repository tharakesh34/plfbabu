package com.pennant.backend.dao.limits.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.limits.LimitInterfaceDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.limits.ClosedFacilityDetail;
import com.pennant.backend.model.limits.FinanceLimitProcess;
import com.pennant.backend.model.limits.LimitDetail;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

public class LimitInterfaceDAOImpl extends SequenceDao<FinanceLimitProcess> implements LimitInterfaceDAO {
    private static Logger logger = Logger.getLogger(LimitInterfaceDAOImpl.class);

	
	public LimitInterfaceDAOImpl() {
		super();
	}

	/**
	 * Method to save the Limit Utilization details
	 * 
	 * @param finLimitProcess
	 */
	@Override
	public void saveFinLimitUtil(FinanceLimitProcess finLimitProcess) {
		logger.debug("Entering");

		if(finLimitProcess.getId()== 0 || finLimitProcess.getId()==Long.MIN_VALUE){
			finLimitProcess.setFinLimitId(getNextId("SeqFinanceLimitProcess"));	
		}
		
		StringBuilder insertSql = new StringBuilder("INSERT INTO FinanceLimitProcess ");
		insertSql.append(" (FinLimitId, FinReference, RequestType, ReferenceNum, CustCIF, LimitRef, ResStatus,");
		insertSql.append("  ResMessage, ErrorCode, ErrorMsg, ValueDate, DealAmount)");
		insertSql.append(" Values(");
		insertSql.append(" :FinLimitId,:FinReference, :RequestType, :ReferenceNum, :CustCIF, :LimitRef, :ResStatus,");
		insertSql.append(" :ResMessage, :ErrorCode, :ErrorMsg, :ValueDate, :DealAmount)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finLimitProcess);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	/**
	 * Method for saving the Customer limit details
	 * 
	 * @param limitDetail
	 */
	@Override
    public void saveCustomerLimitDetails(LimitDetail limitDetail) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("INSERT INTO CustomerLimitDetails ");
		insertSql.append(" (CustCIF, LimitRef, LimitDesc, RevolvingType, LimitExpiryDate, LimitCcy,");
		insertSql.append("  ApprovedLimitCcy, ApprovedLimit, OutstandingAmtCcy, OutstandingAmt, BlockedAmtCcy, BlockedAmt,");
		insertSql.append("  ReservedAmtCcy, ReservedAmt, AvailableAmtCcy, AvailableAmt, Notes)");
		insertSql.append(" 	Values(");
		insertSql.append("  :CustCIF, :LimitRef, :LimitDesc, :RevolvingType, :LimitExpiryDate, :LimitCcy,");
		insertSql.append("  :ApprovedLimitCcy, :ApprovedLimit, :OutstandingAmtCcy, :OutstandingAmt, :BlockedAmtCcy, :BlockedAmt,");
		insertSql.append("  :ReservedAmtCcy, :ReservedAmt, :AvailableAmtCcy, :AvailableAmt, :Notes)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitDetail);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
	    
    }

	/**
	 * Method to fetch the limit Utilization details
	 * 
	 * @param limitUtilReq
	 */
	@Override
    public FinanceLimitProcess getLimitUtilDetails(FinanceLimitProcess financeLimitProcess) {
		logger.debug("Entering");
		
		FinanceLimitProcess limitProcess = new FinanceLimitProcess();
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select * from (Select FinLimitId , FinReference, RequestType, ReferenceNum , CustCIF, ");
		selectSql.append(" LimitRef, ResStatus, ResMessage, ErrorCode,ErrorMsg, ValueDate, ");
		selectSql.append(" DealAmount,ROW_NUMBER() over (order by Valuedate desc)");
		selectSql.append(" row_num from FinanceLimitProcess Where FinReference =:FinReference AND RequestType =:RequestType ");
		selectSql.append(" AND CustCIF =:CustCIF) T Where row_num <=1");
		
		logger.debug("selectSql: " + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeLimitProcess);
		RowMapper<FinanceLimitProcess> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceLimitProcess.class);

		try {
			limitProcess = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			limitProcess = null;
		}
		
		logger.debug("Leaving");
		return limitProcess;
    }
	
	/**
	 * Method for fetch Customer Limit Details based on the Limit Reference
	 * 
	 * @param limitRef
	 */
	@Override
    public LimitDetail getCustomerLimitDetails(String limitRef) {
		logger.debug("Entering");
		
		LimitDetail limitDetail = new LimitDetail();
		limitDetail.setLimitRef(limitRef);
		
		StringBuilder selectSql = new StringBuilder("SELECT  CustCIF, LimitRef, LimitDesc, RevolvingType,");
		selectSql.append("  LimitExpiryDate, LimitCcy, ApprovedLimitCcy, ApprovedLimit, OutstandingAmtCcy, OutstandingAmt,");
		selectSql.append("  ReservedAmtCcy, ReservedAmt, AvailableAmtCcy, AvailableAmt, Notes, BlockedAmtCcy, BlockedAmt ");
		selectSql.append("  FROM  CustomerLimitDetails");
		selectSql.append("  Where LimitRef =:LimitRef");
		
		logger.debug("selectSql: " + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitDetail);
		RowMapper<LimitDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LimitDetail.class);
		
		try{
			limitDetail = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			limitDetail = null;
		}
		logger.debug("Leaving");
		return limitDetail;
    }

	/**
	 * Method for Update Customer Limit Details
	 * 
	 * @param limitDetail
	 */
	@Override
    public void updateCustomerLimitDetails(LimitDetail limitDetail) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("UPDATE CustomerLimitDetails");
		updateSql.append("  Set LimitDesc =:LimitDesc,");
		updateSql.append("  RevolvingType =:RevolvingType, LimitExpiryDate =:LimitExpiryDate, LimitCcy =:LimitCcy, ");
		updateSql.append("  ApprovedLimitCcy =:ApprovedLimitCcy, ApprovedLimit =:ApprovedLimit,");
		updateSql.append("  OutstandingAmtCcy =:OutstandingAmtCcy, OutstandingAmt =:OutstandingAmt, BlockedAmtCcy =:BlockedAmtCcy,");
		updateSql.append("  BlockedAmt =:BlockedAmt, ReservedAmtCcy =:ReservedAmtCcy, ReservedAmt =:ReservedAmt,");
		updateSql.append("  AvailableAmtCcy =:AvailableAmtCcy, AvailableAmt =:AvailableAmt, Notes =:Notes ");
		updateSql.append("  Where LimitRef =:LimitRef AND CustCIF =:CustCIF");

		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitDetail);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	    
    }

	/**
	 * Method for save the Closed facility details into PFF DB
	 * 
	 * @param proClFacilityList
	 * @return boolean
	 */
	@Override
	public boolean saveClosedFacilityDetails(List<ClosedFacilityDetail> proClFacilityList) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into ClosedFaciltyDetails");
		insertSql.append(" (LimitReference, FacilityStatus, ClosedDate, Processed, ProcessedDate)");
		insertSql.append(" Values (:LimitReference, :FacilityStatus, :ClosedDate, :Processed, :ProcessedDate)");

		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(proClFacilityList.toArray());
		logger.debug("Leaving");
		
		int[] recordCount;
		try{	
			recordCount = this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
			if(recordCount.length > 0) {
				return true;
			} else {
				return false;
			}
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	@Override
	public FinanceMain getFinanceMainByRef(String dealID, String type, boolean isRejectFinance) {
		logger.debug("Entering");
		
		FinanceMain financeMain = new FinanceMain();
		financeMain.setId(dealID);
		StringBuilder selectSql = new StringBuilder("select  *  FROM " );
		/*StringBuilder selectSql = new StringBuilder("select  FinReference, GrcPeriodEndDate, FinRepaymentAmount," );
		selectSql.append(" DisbAccountid, RepayAccountid, FinAccount, FinCustPftAccount, FinCommitmentRef, FinLimitRef," );
		selectSql.append(" FinCcy, FinBranch, CustId, FinAmount, FeeChargeAmt, DownPayment, DownPayBank, DownPaySupl, DownPayAccount, SecurityDeposit, FinType, " );
		selectSql.append(" FinStartDate,GraceTerms, NumberOfTerms, NextGrcPftDate, NextRepayDate, LastRepayPftDate, NextRepayPftDate, " );
		selectSql.append(" LastRepayRvwDate, NextRepayRvwDate, FinAssetValue, FinCurrAssetValue,FinRepayMethod, " );
		selectSql.append(" RecordType, Version, ProfitDaysBasis , FeeChargeAmt, FinStatus, FinStsReason," );
		selectSql.append(" InitiateUser, BankName, Iban, AccountType, DdaReferenceNo, SecurityDeposit, MaturityDate " );*/
		if(isRejectFinance){
			selectSql.append(" RejectFinancemain");
		}else{
			selectSql.append(" Financemain");
		}
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMain);
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
		
		try {
			financeMain = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			financeMain = null;
		}
		logger.debug("Leaving");
		return financeMain;
    }
}
