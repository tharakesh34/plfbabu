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
 * FileName    		:  SOAReportGenerationDAOImpl.java                                      * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.reports.impl;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.reports.SOAReportGenerationDAO;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.payment.PaymentInstruction;
import com.pennant.backend.model.systemmasters.SOASummaryReport;
import com.pennant.backend.model.systemmasters.StatementOfAccount;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>ReportConfiguration model</b> class.<br>
 * 
 */

public class SOAReportGenerationDAOImpl extends BasisCodeDAO<StatementOfAccount> implements SOAReportGenerationDAO {

	private static Logger logger = Logger.getLogger(SOAReportGenerationDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public SOAReportGenerationDAOImpl() {
		super();
	}
	
	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	

	@Override
	public FinanceMain getFinanceMain(String finReference) {
		FinanceMain finMain = new FinanceMain();
		finMain.setFinReference(finReference);
		
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" Select * FROM  FinanceMain");
		selectSql.append(" Where FinReference =:FinReference");

		logger.trace(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finMain);
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);

		try {
			finMain = namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			finMain = null;
		}

		logger.debug(Literal.LEAVING);
		
		return finMain;
	}
	
	@Override
	public List<FinanceScheduleDetail> getFinScheduleDetails(String finReference) {
		FinanceScheduleDetail finSchdDetail = new FinanceScheduleDetail();
		finSchdDetail.setFinReference(finReference);
		
		List<FinanceScheduleDetail> list;
		
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" Select * FROM");
		selectSql.append("  FINSCHEDULEDETAILS");
		selectSql.append(" Where FinReference =:FinReference");

		logger.trace(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finSchdDetail);
		RowMapper<FinanceScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceScheduleDetail.class);

		try {
			list = namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			list = new ArrayList<>();
		}

		logger.debug(Literal.LEAVING);
		
		return list;
	}
	
	@Override
	public List<FinAdvancePayments> getFinAdvancePayments(String finReference) {
		FinAdvancePayments finAdvPayment = new FinAdvancePayments();
		finAdvPayment.setFinReference(finReference);
		
		List<FinAdvancePayments> list;
		
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append(" Select * FROM");
		selectSql.append(" FINADVANCEPAYMENTS");
		selectSql.append(" Where (Status !='CANCELED' and LLDate <= (Select to_date(sysparmvalue, 'YYYY-MM-DD')  sysparmvalue from smtparameters where SYSPARMCODE='APP_DATE'))");
		selectSql.append(" And FinReference =:FinReference");
		
		logger.trace(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAdvPayment);
		RowMapper<FinAdvancePayments> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinAdvancePayments.class);
		
		try {
			list = namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			list = new ArrayList<>();
		}
		
		logger.debug(Literal.LEAVING);
		
		return list;
	}
	
	@Override
	public List<PaymentInstruction> getPaymentInstructions(String finReference) {
		PaymentInstruction paymentIns = new PaymentInstruction();
		List<PaymentInstruction> list;
		
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append(" Select * FROM");
		selectSql.append(" PaymentInstructions");
		selectSql.append(" Where Paymentid in (Select Paymentid from paymentheader where FINREFERENCE = '" + finReference + "')");
		selectSql.append(" And (Status!='CANCELED' and PostDate <= (Select to_date(sysparmvalue, 'YYYY-MM-DD')  sysparmvalue from smtparameters where SYSPARMCODE='APP_DATE'))");
		
		logger.trace(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(paymentIns);
		RowMapper<PaymentInstruction> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PaymentInstruction.class);
		
		try {
			list = namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			list = new ArrayList<>();
		}
		
		logger.debug(Literal.LEAVING);
		
		return list;
	}
	
	@Override
	public List<FinODDetails> getFinODDetails(String finReference) {
		FinODDetails finODDetails = new FinODDetails();
		List<FinODDetails> list;
		
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append(" Select * FROM FinODDetails");
		selectSql.append(" Where TOtPenaltyAmt > 0");
		
		logger.trace(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);
		RowMapper<FinODDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinODDetails.class);
		
		try {
			list = namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			list = new ArrayList<>();
		}
		
		logger.debug(Literal.LEAVING);
		
		return list;
	}
	
	@Override
	public List<ManualAdvise> getManualAdvise(String finReference) {
		ManualAdvise manualAdvise = new ManualAdvise();
		manualAdvise.setFinReference(finReference);
		List<ManualAdvise> list;
		
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append(" Select * FROM ManualAdvise");
		selectSql.append(" Where FinReference = :FinReference");
		
		logger.trace(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(manualAdvise);
		RowMapper<ManualAdvise> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ManualAdvise.class);
		
		try {
			list = namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			list = new ArrayList<>();
		}
		
		logger.debug(Literal.LEAVING);
		
		return list;
	}

	@Override
	public StatementOfAccount getSOALoanDetails(String finReference) {
		StatementOfAccount statementOfAccount = new StatementOfAccount();
		statementOfAccount.setFinReference(finReference);
		
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("Select FINREFERENCE, LOANAMOUNT, PLRRATE, VARIANCE, IRR, ROI, TENURE, EMIRECEIVEDPRI, EMIRECEIVEDPFT, PREFERREDCARDLIMIT, PREVINSTAMTPRI, PREVINSTAMTPFT");
		selectSql.append(", INTRATETYPE, LASTDISBURSALDATE, FIRSTDUEDATE, ENDINSTALLMENTDATE, ADVINSTAMT, FINISACTIVE, CLOSINGSTATUS, FUTUREINSTNO, FUTUREPRI1, FUTUREPRI2");
		selectSql.append(", FUTURERPYPFT1, FUTURERPYPFT2, CHARGE_COLL_CUST, UPFRONT_INT_CUST, INT_PAID_DEALER_UPFRONT, PRE_EMI_INT_PAID, REPO_STATUS");
		selectSql.append(", REPO_DATE, SALE_DATE, RELEASE_DATE, LATESTRPYDATE, CCYMINORCCYUNITS, CCYEDITFIELD");
		selectSql.append("  FROM  RPT_SOA_LOAN_VIEW");
		selectSql.append("  Where FinReference =:FinReference");

		logger.trace(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(statementOfAccount);
		RowMapper<StatementOfAccount> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(StatementOfAccount.class);

		try {
			statementOfAccount = namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			statementOfAccount = null;
		}

		logger.debug(Literal.LEAVING);
		
		return statementOfAccount;
	}

	@Override
	public FinanceProfitDetail getFinanceProfitDetails(String finReference) {
		logger.debug(Literal.ENTERING);
		
		FinanceProfitDetail financeProfitDetail = new FinanceProfitDetail();
		financeProfitDetail.setFinReference(finReference);
		
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("  Select * FROM  FinPftDetails");
		selectSql.append("  Where FinReference = :FinReference");

		logger.trace(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeProfitDetail);
		RowMapper<FinanceProfitDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceProfitDetail.class);

		try {
			financeProfitDetail = namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			financeProfitDetail = null;
		}

		logger.debug(Literal.LEAVING);
		
		return financeProfitDetail;
	}
	
	@Override
	public int getFinanceProfitDetailActiveCount(long custId, boolean active) {
		logger.debug(Literal.ENTERING);
		
		MapSqlParameterSource source = null;
		int activeCount = 0;

		StringBuilder selectSql = new StringBuilder("Select Count(*) From FinPftDetails");
		selectSql.append(" Where CustID = :CustID");
		if (active) {
			selectSql.append(" And FinIsActive = :FinIsActive");
		} else {
			selectSql.append(" And FinIsActive <> :FinIsActive");
		}
		logger.debug("selectSql: " + selectSql.toString());

		source = new MapSqlParameterSource();
		source.addValue("FinIsActive", 1);
		source.addValue("CustID", custId);

		try {
			activeCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			logger.error(e);
		}

		logger.debug("Leaving");

		return activeCount;
	}
	
	@Override
	public StatementOfAccount getSOACustomerDetails(long custId) {
		logger.debug(Literal.ENTERING);
		
		StringBuilder selectSql = new StringBuilder();
		StatementOfAccount statementOfAccount = new StatementOfAccount();
		statementOfAccount.setCustID(custId);

		selectSql.append(" Select  CustShrtname, CustCIF, T1.CustID, CustAddrHNbr, CustFlatNbr, CustAddrStreet, CustPOBox, CustAddrCity, ");
		selectSql.append(" CustAddrProvince, CustAddrCountry, PhoneCountryCode, PhoneAreaCode, T3.PhoneNumber, CustEMail");
		selectSql.append(" from Customers T1 Left join");
		selectSql.append(" CustomerAddresses T2 ON T1.CustID = T2.CustID and t2.custAddrPriority = 5 Left join");
		selectSql.append(" CustomerPhoneNumbers T3 ON T1.CustID = T3.PhoneCustID  and PHONETYPEPRIORITY = 5 Left join");
		selectSql.append(" CustomerEMails T4 on T4.CustID = T1.CustID and CustEmailPriority = 5");
		selectSql.append("  Where T1.CustID = :CustID");

		logger.trace(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(statementOfAccount);
		RowMapper<StatementOfAccount> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(StatementOfAccount.class);

		try {
			statementOfAccount = namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			statementOfAccount = null;
		}

		logger.debug(Literal.LEAVING);
		
		return statementOfAccount;
	}

	@Override
	public StatementOfAccount getSOAProductDetails(String finBranch, String finType) {
		logger.debug(Literal.ENTERING);
		
		StringBuilder selectSql = new StringBuilder();
		StatementOfAccount statementOfAccount = new StatementOfAccount();

		selectSql.append(" Select T3.productdesc FinType, T4.branchdesc FinBranch ");
		selectSql.append(" From RMTFinanceTypes T2 inner join ");
		selectSql.append(" BMTProduct T3 on T3.ProductCode = T2.FinCategory inner join");
		selectSql.append(" RMTBRANCHES T4 on T4.branchcode = '" + finBranch + "' ");
		selectSql.append(" Where T2.FinType = '" + finType + "'");

		logger.trace(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(statementOfAccount);
		RowMapper<StatementOfAccount> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(StatementOfAccount.class);

		try {
			statementOfAccount = namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			statementOfAccount = null;
		}

		logger.debug(Literal.LEAVING);
		
		return statementOfAccount;
	}
	
	@Override
	public SOASummaryReport getFinExcessAmountOfSummaryReport(String finReference) {
		logger.debug(Literal.ENTERING);
		
		StringBuilder selectSql = new StringBuilder();
		SOASummaryReport soaSummaryReport = new SOASummaryReport();
		
		selectSql.append(" Select 'Unadjusted Amount' Component, Coalesce(SUM(BalanceAmt),0) Due, 0 Receipt, Coalesce(SUM(BalanceAmt),0) OverDue");   
		selectSql.append(" From FinPftDetails t1 left join ");
		selectSql.append(" Finexcessamount t2 on T1.finreference = T2.finreference");
		selectSql.append(" Where T1.FinReference = '" + finReference + "'");
		selectSql.append(" Group by T1.Finreference, finccy ");
		
		logger.trace(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(soaSummaryReport);
		RowMapper<SOASummaryReport> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SOASummaryReport.class);
		
		try {
			soaSummaryReport = namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			soaSummaryReport = null;
		}
		
		logger.debug(Literal.LEAVING);
		
		return soaSummaryReport;
	}
	
}