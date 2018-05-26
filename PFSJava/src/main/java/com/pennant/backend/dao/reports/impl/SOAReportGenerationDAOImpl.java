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
 * 24-05-2018       Srikanth                 0.2           Merge the Code From Bajaj To Core                                                   * 
                                                                                         * 
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
import java.util.Date;
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
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.systemmasters.StatementOfAccount;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
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
	

	/**
	 * get the Finance Main records
	 */
	@Override
	public FinanceMain getFinanceMain(String finReference) {
		logger.debug(Literal.ENTERING);
		
		FinanceMain finMain = new FinanceMain();
		finMain.setFinReference(finReference);
		
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" Select ClosingStatus,FinStartDate,FeeChargeAmt,FinCurrAssetValue,FInApprovedDate,FinType FROM  FinanceMain");
		selectSql.append(" Where FinReference = :FinReference");

		logger.trace(Literal.SQL + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finMain);
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);

		try {
			finMain = namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finMain = null;
		}

		logger.debug(Literal.LEAVING);
		
		return finMain;
	}
	
	/**
	 * get the FinScheduleDetails List
	 */
	@Override
	public List<FinanceScheduleDetail> getFinScheduleDetails(String finReference) {
		logger.debug(Literal.ENTERING);
		
		FinanceScheduleDetail finSchdDetail = new FinanceScheduleDetail();
		finSchdDetail.setFinReference(finReference);
		
		List<FinanceScheduleDetail> finSchdDetailsList;
		

		StringBuilder selectSql = new StringBuilder(" Select FinReference, SchDate, SchSeq, DisbOnSchDate, RepayAmount");
		selectSql.append(" ,DisbAmount ,FeeChargeAmt,BpiOrHoliday,PartialPaidAmt,InstNumber, ProfitSchd");
		selectSql.append(" ,PrincipalSchd ,FeeSchd,SchdPriPaid,SchdPftPaid,SchdFeePaid FROM FINSCHEDULEDETAILS");
		selectSql.append(" Where FinReference = :FinReference");

		logger.trace(Literal.SQL + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finSchdDetail);
		RowMapper<FinanceScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceScheduleDetail.class);

		try {
			finSchdDetailsList = namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finSchdDetailsList = new ArrayList<FinanceScheduleDetail>();
		}

		logger.debug(Literal.LEAVING);
		
		return finSchdDetailsList;
	}
	
	/**
	 * get the FinAdvancePayments list
	 */
	@Override
	public List<FinAdvancePayments> getFinAdvancePayments(String finReference) {
		logger.debug(Literal.ENTERING);
		
		FinAdvancePayments finAdvPayment = new FinAdvancePayments();
		finAdvPayment.setFinReference(finReference);
		
		List<FinAdvancePayments> FinAdvancePaymentslist;
		
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append(" Select LlDate,AmtToBeReleased,PaymentType,LlReferenceNo,TransactionRef,ValueDate FROM FinAdvancePayments");
		selectSql.append(" Where (Status not in ('CANCELED','REJECTED') or Status is null)");
		selectSql.append(" And FinReference = :FinReference");
		
		logger.trace(Literal.SQL + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAdvPayment);
		RowMapper<FinAdvancePayments> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinAdvancePayments.class);
		
		try {
			FinAdvancePaymentslist = namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			FinAdvancePaymentslist = new ArrayList<FinAdvancePayments>();
		}
		
		logger.debug(Literal.LEAVING);
		
		return FinAdvancePaymentslist;
	}
	
	/**
	 * get the PaymentInstructions list
	 */
	@Override
	public List<PaymentInstruction> getPaymentInstructions(String finReference) {
		logger.debug(Literal.ENTERING);
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		
		List<PaymentInstruction> paymentInstructionsList;
		
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append(" Select * FROM PaymentInstructions");
		selectSql.append(" Where PaymentId In (Select PaymentId from PaymentHeader where FinReference = :FinReference)");
		selectSql.append(" And (Status!='CANCELED' or Status is null)");
		if (App.DATABASE.name() == Database.SQL_SERVER.name()) {
		selectSql.append(" And PostDate <= (Select sysparmvalue From smtparameters where SYSPARMCODE='APP_DATE')");
		} else {
		selectSql.append(" And PostDate <= (Select to_date(sysparmvalue, 'YYYY-MM-DD') sysparmvalue From smtparameters where SYSPARMCODE='APP_DATE')");
		}
		logger.trace(Literal.SQL + selectSql.toString());
		
		RowMapper<PaymentInstruction> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PaymentInstruction.class);
		
		try {
			paymentInstructionsList =  this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			paymentInstructionsList = new ArrayList<PaymentInstruction>();
		}
		
		logger.debug(Literal.LEAVING);
		
		return paymentInstructionsList;
	}
	
	/**
	 * get the FinODDetails List
	 */
	@Override
	public List<FinODDetails> getFinODDetails(String finReference) {
		logger.debug(Literal.ENTERING);
		
		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinReference(finReference);
		List<FinODDetails> finODDetailslist;
		
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append(" Select TotPenaltyAmt,TotWaived,TotPenaltyPaid,FinODSchdDate,FinODTillDate FROM FinODDetails");
		selectSql.append(" Where TOtPenaltyAmt > 0 and FinReference = :FinReference");
		
		logger.trace(Literal.SQL + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);
		RowMapper<FinODDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinODDetails.class);
		
		try {
			finODDetailslist = namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finODDetailslist = new ArrayList<>();
		}
		
		logger.debug(Literal.LEAVING);
		
		return finODDetailslist;
	}
	
	/**
	 * get the Manual Advise List
	 */
	@Override
	public List<ManualAdvise> getManualAdvise(String finReference) {
		logger.debug(Literal.ENTERING);
		
		ManualAdvise manualAdvise = new ManualAdvise();
		manualAdvise.setFinReference(finReference);
		List<ManualAdvise> manualAdviseList = new ArrayList<ManualAdvise>();
		
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append(" Select T1.FinReference, T1.PostDate, T1.AdviseAmount, T1.AdviseType, T1.ReceiptId, T1.BounceId, T1.Adviseid, T1.FeeTypeId, T1.BalanceAmt,");
		selectSql.append(" T1.WaivedAmount, T1.PaidAmount, T2.FeeTypeDesc, T1.ValueDate,T2.TaxComponent ");
		selectSql.append(" FROM ManualAdvise T1 Left Join");
		selectSql.append(" FEETYPES T2 ON T2.FeeTypeId = T1.FeeTypeId  ");
		selectSql.append(" Where FinReference = :FinReference");
		
		logger.trace(Literal.SQL + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(manualAdvise);
		RowMapper<ManualAdvise> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ManualAdvise.class);
		
		try {
			manualAdviseList = namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			manualAdviseList = new ArrayList<ManualAdvise>();
		}
		
		logger.debug(Literal.LEAVING);
		
		return manualAdviseList;
	}

	/**
	 * get the Manual Advise Movements List
	 */
	@Override
	public List<ManualAdviseMovements> getManualAdviseMovements(String finReference) {
		logger.debug(Literal.ENTERING);
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		
		List<ManualAdviseMovements> manualAdviseMovementsList;
		
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append(" SELECT T1.Movementdate, T2.WaivedAmount, F.FEETYPEDESC, T2.ValueDate");
		selectSql.append(" FROM ManualAdviseMovements T1 INNER JOIN ");
		selectSql.append(" ManualAdvise T2 on T1.Adviseid = T2.Adviseid LEFT JOIN ");
		selectSql.append(" FEETYPES F ON F.FEETYPEID = T2.FEETYPEID ");
		selectSql.append(" WHERE T2.WaivedAmount > 0 ");
		selectSql.append(" And  T2.FinReference = :FinReference");
		
		logger.trace(Literal.SQL + selectSql.toString());
	
		RowMapper<ManualAdviseMovements> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ManualAdviseMovements.class);
		
		try {
			manualAdviseMovementsList =  this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			manualAdviseMovementsList = new ArrayList<ManualAdviseMovements>();
		}
		
		logger.debug(Literal.LEAVING);
		
		return manualAdviseMovementsList;
	}
	
	/**
	 * get the FinFeeDetails List
	 */
	@Override
	public List<FinFeeDetail> getFinFeedetails(String finReference) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		List<FinFeeDetail> finFeeDetailsList = null;

		StringBuilder sql = new StringBuilder();
		sql.append(" Select T1.FinReference, T1.RemainingFee, T1.PaidAmount, T1.Postdate, T1.FeeTypeID, T1.VasReference,");
		sql.append(" T1.Originationfee, T1.FeeSchedulemethod, T2.FeeTypeCode, T2.FeeTypedesc, T1.WaivedAmount, T2.TaxComponent ");
		sql.append(" From  FinFeeDetail T1");
		sql.append(" Left Join FeeTypes T2 ON T2.FeeTypeId = T1.FeeTypeId");
		sql.append(" WHERE T1.FinReference = :FinReference");

		logger.trace(Literal.SQL + sql.toString());
		
		RowMapper<FinFeeDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinFeeDetail.class);

		try {
			finFeeDetailsList = this.namedParameterJdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finFeeDetailsList = new ArrayList<FinFeeDetail>();
		} finally {
			source = null;
			sql = null;
			logger.debug(Literal.LEAVING);
		}

		return finFeeDetailsList;
	}
	
	/**
	 * get the Receipt Allocation Details List
	 */
	@Override
	public List<ReceiptAllocationDetail> getReceiptAllocationDetailsList(String finReference) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		List<ReceiptAllocationDetail> finReceiptAllocationDetailsList = null;

		StringBuilder sql = new StringBuilder();
		sql.append(" Select  ReceiptID, AllocationType, PaidAmount  From ReceiptAllocationDetail");
		sql.append(" Where ReceiptId in (Select ReceiptId from FinReceiptHeader where Reference = :FinReference)");

		logger.trace(Literal.SQL + sql.toString());
		
		RowMapper<ReceiptAllocationDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ReceiptAllocationDetail.class);

		try {
			finReceiptAllocationDetailsList = this.namedParameterJdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finReceiptAllocationDetailsList = new ArrayList<ReceiptAllocationDetail>();
		} finally {
			source = null;
			sql = null;
			logger.debug(Literal.LEAVING);
		}

		return finReceiptAllocationDetailsList;
	}
	
	/**
	 * get the FinReceiptHeaders List
	 */
	@Override
	public List<FinReceiptHeader> getFinReceiptHeaders(String finReference) {
		logger.debug(Literal.ENTERING);
		
		FinReceiptHeader finReceiptHeader = new FinReceiptHeader();
		finReceiptHeader.setReference(finReference);
		List<FinReceiptHeader> list;
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT ReceiptID,ReceiptModeStatus,ReceiptDate,ReceiptMode,BounceDate From FinReceiptHeader");
		selectSql.append(" Where Reference = :Reference");
		
		logger.trace(Literal.SQL + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finReceiptHeader);
		RowMapper<FinReceiptHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinReceiptHeader.class);
		
		try {
			list = namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			list = new ArrayList<>();
		}
		
		logger.debug(Literal.LEAVING);
		
		return list;
	}
	
	/**
	 * get the FinReceiptDetails List
	 */
	@Override
	public List<FinReceiptDetail> getFinReceiptDetails(String finReference) {
		logger.debug(Literal.ENTERING);
		
		List<FinReceiptDetail> list;
		StringBuilder selectSql = new StringBuilder();
		
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("Reference", finReference);
		selectSql.append(" SELECT ReceiptID,PaymentType,PaymentRef,FavourNumber,Amount,Status,ReceiptSeqID,ReceivedDate,TransactionRef  from FinReceiptDetail");
		selectSql.append(" Where ReceiptId in (Select ReceiptId from FINRECEIPTHEADER where Reference = :Reference) ");
		
		logger.trace(Literal.SQL + selectSql.toString());
		
		RowMapper<FinReceiptDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinReceiptDetail.class);
		
		try {
			list =  namedParameterJdbcTemplate.query(selectSql.toString(), paramMap, typeRowMapper);
		} catch (Exception e) {
			list = new ArrayList<FinReceiptDetail>();
		}
		
		logger.debug(Literal.LEAVING);
		
		return list;
	}
	
	/**
	 * get the Statement of Account records
	 */
	@Override
	public StatementOfAccount getSOALoanDetails(String finReference) {
		logger.debug(Literal.ENTERING);
		
		StatementOfAccount statementOfAccount = new StatementOfAccount();
		statementOfAccount.setFinReference(finReference);
		
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" Select FINREFERENCE, LOANAMOUNT, PLRRATE, VARIANCE, IRR, ROI, TENURE, EMIRECEIVEDPRI, EMIRECEIVEDPFT, PREFERREDCARDLIMIT,");
		selectSql.append(" PREVINSTAMTPRI, PREVINSTAMTPFT, INTRATETYPE, LASTDISBURSALDATE, FIRSTDUEDATE, ENDINSTALLMENTDATE, ADVINSTAMT, FINISACTIVE,");
		selectSql.append(" CLOSINGSTATUS, FUTUREINSTNO, FUTUREPRI1, FUTUREPRI2, FUTURERPYPFT1, FUTURERPYPFT2, CHARGE_COLL_CUST CHARGECOLLCUST,");
		selectSql.append(" UPFRONT_INT_CUST UPFRONTINTCUST, INT_PAID_DEALER_UPFRONT INTPAIDDEALERUPFRONT, PRE_EMI_INT_PAID PREEMIINTPAID, REPO_STATUS REPOSTATUS,");
		selectSql.append(" LATESTRPYDATE, CCYMINORCCYUNITS, CCYEDITFIELD");
		selectSql.append(" FROM  RPT_SOA_LOAN_VIEW");
		selectSql.append(" Where FinReference = :FinReference");

		logger.trace(Literal.SQL + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(statementOfAccount);
		RowMapper<StatementOfAccount> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(StatementOfAccount.class);

		try {
			statementOfAccount = namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			statementOfAccount = null;
		}

		logger.debug(Literal.LEAVING);
		
		return statementOfAccount;
	}

	/**
	 * get the Finance profit Details List
	 */
	@Override
	public FinanceProfitDetail getFinanceProfitDetails(String finReference) {
		logger.debug(Literal.ENTERING);
		
		FinanceProfitDetail financeProfitDetail = new FinanceProfitDetail();
		financeProfitDetail.setFinReference(finReference);
		
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("  Select CustId,FinStartDate,LinkedFinRef,ClosedlinkedFinRef,FinBranch,FinType,FinPurpose,");
		selectSql.append("  MaturityDate,NoPAIDINST,TotalPFTPAID,TotalPRIPAID,TotalPRIBAL,TotalPFTBAL,NOInst FROM  FinPftDetails");
		selectSql.append("  Where FinReference = :FinReference");

		logger.trace(Literal.SQL + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeProfitDetail);
		RowMapper<FinanceProfitDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceProfitDetail.class);

		try {
			financeProfitDetail = namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			financeProfitDetail = null;
		}

		logger.debug(Literal.LEAVING);
		
		return financeProfitDetail;
	}
	
	/**
	 * get the finance Profit Details Active/InActive count
	 */
	@Override
	public int getFinanceProfitDetailActiveCount(long custId, boolean active) {
		logger.debug(Literal.ENTERING);
		
		int activeCount = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinIsActive", 1);
		source.addValue("CustID", custId);

		StringBuilder selectSql = new StringBuilder("Select Count(*) From FinPftDetails");
		selectSql.append(" Where CustID = :CustID");
		
		if (active) {
			selectSql.append(" And FinIsActive = :FinIsActive");
		} else {
			selectSql.append(" And FinIsActive <> :FinIsActive");
		}
		
		logger.debug("selectSql: " + selectSql.toString());

		try {
			activeCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (DataAccessException e) {
			activeCount = 0;
		}

		logger.debug("Leaving");

		return activeCount;
	}
	
	/**
	 * get the Statement of Account Records having customer details
	 */
	@Override
	public StatementOfAccount getSOACustomerDetails(long custId) {
		logger.debug(Literal.ENTERING);
		
		StringBuilder selectSql = new StringBuilder();
		StatementOfAccount statementOfAccount = new StatementOfAccount();
		statementOfAccount.setCustID(custId);

		selectSql.append(" Select T1.CustShrtname, T1.CustCIF, T1.CustID,");
		selectSql.append(" T2.CustAddrHNbr, T2.CustFlatNbr, T2.CustAddrStreet, T2.CustPOBox, T2.custaddrline1, T2.custaddrline2, T2.custAddrZip,");
		selectSql.append(" T3.CountryDesc CustAddrCountry,");
		selectSql.append(" T4.PCCityName CustAddrCity,");
		selectSql.append(" T5.CpProvinceName CustAddrProvince,");
		selectSql.append(" T6.PhoneCountryCode, T6.PhoneAreaCode, T6.PhoneNumber,");
		selectSql.append(" T7.CustEMail");
		selectSql.append(" From Customers T1");
		selectSql.append(" Left Join CustomerAddresses T2 ON T1.CustID = T2.CustID and T2.custAddrPriority = 5");		 
		selectSql.append(" Left Join Bmtcountries T3 on T3.CountryCode = T2.CustAddrCountry");
		selectSql.append(" Left Join RmtProvinceVsCity T4 on T4.PcCity = T2.CustAddrCity");
		selectSql.append(" Left Join RmtCountryVsProvince T5 on T5.CpProvince = T2.custAddrProvince");		 
		selectSql.append(" Left Join CustomerPhoneNumbers T6 ON T1.CustID = T6.PhoneCustID  and PHONETYPEPRIORITY = 5");
		selectSql.append(" Left Join CustomerEMails T7 on T7.CustID = T1.CustID and CustEmailPriority = 5");
		selectSql.append(" Where T1.CustID = :CustID");

		logger.trace(Literal.SQL + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(statementOfAccount);
		RowMapper<StatementOfAccount> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(StatementOfAccount.class);

		try {
			statementOfAccount = namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			statementOfAccount = null;
		}

		logger.debug(Literal.LEAVING);
		
		return statementOfAccount;
	}

	/**
	 * get the Statement of Account Records having Product details
	 */
	@Override
	public StatementOfAccount getSOAProductDetails(String finBranch, String finType) {
		logger.debug(Literal.ENTERING);

		StatementOfAccount statementOfAccount = null;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinBranch", finBranch);
		source.addValue("FinType", finType);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select T2.productdesc FinType, T3.branchdesc FinBranch");
		selectSql.append(" From RMTFinanceTypes T1");
		selectSql.append(" Inner Join BMTProduct T2 on T2.ProductCode = T1.FinCategory");
		selectSql.append(" Inner Join  RMTBRANCHES T3 on T3.branchcode = :FinBranch");
		selectSql.append(" Where T1.FinType = :FinType");

		logger.trace(Literal.SQL + selectSql.toString());
		
		try {
			RowMapper<StatementOfAccount> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(StatementOfAccount.class);
			statementOfAccount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (Exception e) {
			statementOfAccount = null;
		} finally {
			source = null;
			selectSql = null;
			logger.debug(Literal.LEAVING);
		}

		return statementOfAccount;
	}
	
	/**
	 * get the FinExcessAmountList
	 */
	@Override
	public List<FinExcessAmount> getFinExcessAmountsList(String finReference) {
		logger.debug(Literal.ENTERING);
		
		FinExcessAmount finSchdDetail = new FinExcessAmount();
		finSchdDetail.setFinReference(finReference);
		
		List<FinExcessAmount> finExcessAmountList = null;
		
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" Select BalanceAmt FROM FinExcessAmount");
		selectSql.append(" Where FinReference = :FinReference");

		logger.trace(Literal.SQL + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finSchdDetail);
		RowMapper<FinExcessAmount> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinExcessAmount.class);

		try {
			finExcessAmountList = namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finExcessAmountList = new ArrayList<FinExcessAmount>();
		}

		logger.debug(Literal.LEAVING);
		
		return finExcessAmountList;
	}

	@Override
	public List<FinRepayHeader> getFinRepayHeadersList(String finReference) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		List<FinRepayHeader> finRepayHeadersList = null;

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ReceiptSeqID,RepayID FROM FinRepayHeader");
		sql.append(" WHERE FinReference = :FinReference");

		logger.trace(Literal.SQL + sql.toString());
		
		RowMapper<FinRepayHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinRepayHeader.class);

		try {
			finRepayHeadersList = this.namedParameterJdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finRepayHeadersList = new ArrayList<FinRepayHeader>();
		} finally {
			source = null;
			sql = null;
			logger.debug(Literal.LEAVING);
		}

		return finRepayHeadersList;
	}

	/**
	 * get the Max Schedule Date in FinScheduleDetails
	 */
	@Override
	public Date getMaxSchDate(String finReference) {
		logger.debug(Literal.ENTERING);

		Date  maxSchDate = null;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select Distinct  max(T2.schdate)maxschdate");
		selectSql.append(" from FinanceMain T1");
		selectSql.append(" Inner Join FinScheduleDetails T2 on T1.FinReference = T2.FinReference");
		selectSql.append(" where T1.closingstatus = 'E' and T1.FinReference =  :FinReference");
		selectSql.append(" group by T1.FinReference");

		logger.trace(Literal.SQL + selectSql.toString());
		
		try {
			maxSchDate = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Date.class);
		} catch (Exception e) {
			maxSchDate = null;
		} finally {
			source = null;
			selectSql = null;
			logger.debug(Literal.LEAVING);
		}

		return maxSchDate;
	}

	/**
	 * get the Presentment Details List
	 */
	@Override
	public List<PresentmentDetail> getPresentmentDetailsList(String finReference) {
		logger.debug(Literal.ENTERING);
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		List<PresentmentDetail> presentmentDetailsList = null;

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT Distinct ReceiptId FROM PRESENTMENTDETAILS");
		sql.append(" Where RECEIPTID != 0 and FinReference = :FinReference");
		
		logger.trace(Literal.SQL + sql.toString());
		
		RowMapper<PresentmentDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PresentmentDetail.class);
		
		try {
			presentmentDetailsList =  this.namedParameterJdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			presentmentDetailsList = new ArrayList<PresentmentDetail>();
		}  finally {
			source = null;
			sql = null;
			logger.debug(Literal.LEAVING);
		}

		return presentmentDetailsList;
	}

	/**
	 * get the Repay Schedule Details List
	 */
	@Override
	public List<RepayScheduleDetail> getRepayScheduleDetailsList(String finReference) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		List<RepayScheduleDetail> finRepayScheduleDetailsList = null;

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT RepayID,TdsSchdPayNow,PftSchdWaivedNow,PriSchdWaivedNow,WaivedAmt FROM FinRepayscheduledetail");
		sql.append(" WHERE FinReference = :FinReference");

		logger.trace(Literal.SQL + sql.toString());
		
		RowMapper<RepayScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(RepayScheduleDetail.class);

		try {
			finRepayScheduleDetailsList = this.namedParameterJdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finRepayScheduleDetailsList = new ArrayList<RepayScheduleDetail>();
		} finally {
			source = null;
			sql = null;
			logger.debug(Literal.LEAVING);
		}

		return finRepayScheduleDetailsList;
	}

	/**
	 * get the VAS Recordings List
	 */
	@Override
	public List<VASRecording> getVASRecordingsList(String finReference) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		List<VASRecording> vasRecordingsList = null;

		StringBuilder sql = new StringBuilder();
		sql.append(" Select T1.PrimaryLinkRef, T1.ProductCode, T2.ProductDesc,T1.VasReference");
		sql.append(" From VasRecording T1");
		sql.append(" Inner Join VasStructure T2 on T1.ProductCode = T2.ProductCode");
		sql.append(" WHERE T1.PrimaryLinkRef = :FinReference");

		logger.trace(Literal.SQL + sql.toString());
		
		RowMapper<VASRecording> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(VASRecording.class);

		try {
			vasRecordingsList = this.namedParameterJdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			vasRecordingsList = new ArrayList<VASRecording>();
		} finally {
			source = null;
			sql = null;
			logger.debug(Literal.LEAVING);
		}

		return vasRecordingsList;
	}

	/**
	 * get the Fin Fee Schedule Details List
	 */
	@Override
	public List<FinFeeScheduleDetail> getFinFeeScheduleDetailsList(String finReference) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		List<FinFeeScheduleDetail> finFeeScheduleDetailsList = null;

		StringBuilder sql = new StringBuilder();
		sql.append(" Select T1.FeeId, T1.Schdate, T1.SchAmount, T2.FinReference, T3.FeeTypeCode, T3.FeeTypeDesc");
		sql.append(" From FinFeeScheduleDetail T1");
		sql.append(" Inner Join FinFeeDetail T2 on T2.FeeId = T1.FeeId");
		sql.append(" Inner Join FeeTypes T3 on T3.FeeTypeId = T2.FeeTypeId");
		if (App.DATABASE.name() == Database.SQL_SERVER.name()) {
			sql.append(" WHERE T1.Schdate <= (Select sysparmvalue from smtparameters where SYSPARMCODE='APP_DATE')");
		} else {
			sql.append(" WHERE T1.Schdate <= (Select to_date(sysparmvalue, 'YYYY-MM-DD')  sysparmvalue from smtparameters where SYSPARMCODE='APP_DATE')");
		}
		sql.append(" And T1.SchAmount !=0 And T2.FinReference = :FinReference");

		logger.trace(Literal.SQL + sql.toString());
		
		RowMapper<FinFeeScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinFeeScheduleDetail.class);

		try {
			finFeeScheduleDetailsList = this.namedParameterJdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finFeeScheduleDetailsList = new ArrayList<FinFeeScheduleDetail>();
		} finally {
			source = null;
			sql = null;
			logger.debug(Literal.LEAVING);
		}

		return finFeeScheduleDetailsList;
	}
	
	@Override
	public EventProperties getEventPropertiesList(String configName) {

		logger.debug(Literal.ENTERING);

		EventProperties statementOfAccount = null;
		MapSqlParameterSource parameterMap = new MapSqlParameterSource();
		parameterMap = new MapSqlParameterSource();
		parameterMap.addValue("ConfigName", configName);

		StringBuilder sql = new StringBuilder("Select * from  DATA_ENGINE_EVENT_PROPERTIES");
		sql.append(" Where config_id in (Select Id from DATA_ENGINE_CONFIG where Name = :ConfigName)");

		logger.trace(Literal.SQL + sql.toString());
		
		try {
			RowMapper<EventProperties> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(EventProperties.class);
			statementOfAccount = this.namedParameterJdbcTemplate.queryForObject(sql.toString(), parameterMap, typeRowMapper);
		} catch (Exception e) {
			statementOfAccount = null;
		} finally {
			parameterMap = null;
			sql = null;
			logger.debug(Literal.LEAVING);
		}

		return statementOfAccount;
	}
	@Override
	public List<String> getSOAFinTypes() {

		logger.debug(Literal.ENTERING);
		List<String> list = null;
		StringBuilder sql = new StringBuilder("Select FinType from  SOA_FinTypes");
		logger.trace(Literal.SQL + sql.toString());
		
		try {
			list = this.namedParameterJdbcTemplate.queryForList(sql.toString(),new MapSqlParameterSource(),String.class);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION);
		} finally {
			sql = null;
			logger.debug(Literal.LEAVING);
		}

		return list;
	}
	
}