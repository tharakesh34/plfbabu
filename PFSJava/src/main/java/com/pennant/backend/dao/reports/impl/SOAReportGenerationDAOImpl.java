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

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.reports.SOAReportGenerationDAO;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.finance.FeeWaiverDetail;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.systemmasters.ApplicantDetail;
import com.pennant.backend.model.systemmasters.OtherFinanceDetail;
import com.pennant.backend.model.systemmasters.StatementOfAccount;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>ReportConfiguration model</b> class.<br>
 * 
 */

public class SOAReportGenerationDAOImpl extends BasicDao<StatementOfAccount> implements SOAReportGenerationDAO {
	private static Logger logger = Logger.getLogger(SOAReportGenerationDAOImpl.class);

	public SOAReportGenerationDAOImpl() {
		super();
	}

	/**
	 * get the Finance Main records
	 */
	@Override
	public FinanceMain getFinanceMain(String finReference) {
		logger.debug(Literal.ENTERING);

		FinanceMain finMain = new FinanceMain();
		finMain.setFinReference(finReference);

		StringBuilder sql = new StringBuilder();
		sql.append("select ClosingStatus, FinStartDate, FeeChargeAmt, FinCurrAssetValue, FInApprovedDate");
		sql.append(
				", FinType, FinCategory, FixedRateTenor, FixedTenorRate, NumberOfTerms, RepayProfitRate, RepayBaseRate");
		sql.append(", FinCcy, RepaySpecialRate, RepayMargin, advemiterms, advanceemi, MaturityDate, CustId");
		sql.append(", AdvType, GrcAdvType , DownPayment");
		sql.append(", promotioncode, promotionSeqId FROM FinanceMain Where FinReference = :FinReference");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finMain);
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);

		try {
			finMain = jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
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

		StringBuilder selectSql = new StringBuilder(
				" Select FinReference, SchDate, SchSeq, DisbOnSchDate, RepayAmount");
		selectSql.append(
				" ,DisbAmount ,FeeChargeAmt,BpiOrHoliday,PartialPaidAmt,InstNumber, ClosingBalance, Balanceforpftcal, ProfitSchd, CalculatedRate");
		selectSql.append(" ,PrincipalSchd ,FeeSchd,SchdPriPaid,SchdPftPaid,SchdFeePaid FROM FINSCHEDULEDETAILS");
		selectSql.append(" Where FinReference = :FinReference");

		logger.trace(Literal.SQL + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finSchdDetail);
		RowMapper<FinanceScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceScheduleDetail.class);

		try {
			finSchdDetailsList = jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
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

		selectSql.append(
				" Select LlDate,AmtToBeReleased,PaymentType,LlReferenceNo,TransactionRef,ValueDate FROM FinAdvancePayments");
		selectSql.append(" Where (Status not in ('CANCELED','REJECTED') or Status is null)");
		selectSql.append(" And FinReference = :FinReference");

		logger.trace(Literal.SQL + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAdvPayment);
		RowMapper<FinAdvancePayments> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinAdvancePayments.class);

		try {
			FinAdvancePaymentslist = jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
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
		selectSql
				.append(" Where PaymentId In (Select PaymentId from PaymentHeader where FinReference = :FinReference)");
		selectSql.append(" And (Status!='CANCELED' or Status is null)");
		if (App.DATABASE.name() == Database.SQL_SERVER.name()) {
			selectSql.append(" And PostDate <= (Select sysparmvalue From smtparameters where SYSPARMCODE='APP_DATE')");
		} else {
			selectSql.append(
					" And PostDate <= (Select to_date(sysparmvalue, 'YYYY-MM-DD') sysparmvalue From smtparameters where SYSPARMCODE='APP_DATE')");
		}
		logger.trace(Literal.SQL + selectSql.toString());

		RowMapper<PaymentInstruction> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(PaymentInstruction.class);

		try {
			paymentInstructionsList = this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
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

		selectSql.append(
				" Select TotPenaltyAmt,TotWaived,TotPenaltyPaid,FinODSchdDate,FinODTillDate,LPIAmt FROM FinODDetails");
		selectSql.append(" Where TOtPenaltyAmt > 0 and FinReference = :FinReference");

		logger.trace(Literal.SQL + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);
		RowMapper<FinODDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinODDetails.class);

		try {
			finODDetailslist = jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
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

		selectSql.append(
				" Select T1.FinReference, T1.PostDate, T1.AdviseAmount, T1.AdviseType, T1.ReceiptId, T1.BounceId, T1.Adviseid, T1.FeeTypeId, T1.BalanceAmt,");
		selectSql.append(" T1.WaivedAmount, T1.PaidAmount, T2.FeeTypeDesc, T1.ValueDate,T2.TaxComponent,");
		selectSql.append(" T1.PaidCGST, T1.PaidSGST, T1.PaidUGST, T1.PaidIGST");
		selectSql.append(" FROM ManualAdvise T1");
		selectSql.append(" Left Join FEETYPES T2 ON T2.FeeTypeId = T1.FeeTypeId");
		selectSql.append(" Where FinReference = :FinReference");

		logger.trace(Literal.SQL + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(manualAdvise);
		RowMapper<ManualAdvise> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ManualAdvise.class);

		try {
			manualAdviseList = jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
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

		selectSql.append(" SELECT T1.Movementdate, T1.movementamount, T1.WaivedAmount, F.FEETYPEDESC, T2.ValueDate");
		selectSql.append(" FROM ManualAdviseMovements T1 INNER JOIN ");
		selectSql.append(" ManualAdvise T2 on T1.Adviseid = T2.Adviseid LEFT JOIN ");
		selectSql.append(" FEETYPES F ON F.FEETYPEID = T2.FEETYPEID ");
		selectSql.append(" WHERE T2.WaivedAmount > 0 ");
		selectSql.append(" And  T2.FinReference = :FinReference");

		logger.trace(Literal.SQL + selectSql.toString());

		RowMapper<ManualAdviseMovements> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ManualAdviseMovements.class);

		try {
			manualAdviseMovementsList = this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
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
		sql.append(
				" Select T1.FinReference, T1.RemainingFee, T1.PaidAmount, T1.Postdate, T1.FeeTypeID, T1.VasReference,");
		sql.append(
				" T1.Originationfee, T1.FeeSchedulemethod, T2.FeeTypeCode, T2.FeeTypedesc, T1.WaivedAmount, T2.TaxComponent ");
		sql.append(" From  FinFeeDetail T1");
		sql.append(" Left Join FeeTypes T2 ON T2.FeeTypeId = T1.FeeTypeId");
		sql.append(" WHERE T1.FinReference = :FinReference");

		logger.trace(Literal.SQL + sql.toString());

		RowMapper<FinFeeDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinFeeDetail.class);

		try {
			finFeeDetailsList = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
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

		RowMapper<ReceiptAllocationDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ReceiptAllocationDetail.class);

		try {
			finReceiptAllocationDetailsList = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
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
		selectSql
				.append(" SELECT ReceiptID,ReceiptModeStatus,ReceiptDate,ReceiptMode,BounceDate From FinReceiptHeader");
		selectSql.append(" Where Reference = :Reference");

		logger.trace(Literal.SQL + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finReceiptHeader);
		RowMapper<FinReceiptHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinReceiptHeader.class);

		try {
			list = jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
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
		// 30-08-2019:Added ValueDate
		selectSql.append(
				" SELECT ReceiptID,PaymentType,PaymentRef,FavourNumber,Amount,Status,ReceiptSeqID,ReceivedDate,ValueDate,TransactionRef  from FinReceiptDetail");
		selectSql.append(" Where ReceiptId in (Select ReceiptId from FINRECEIPTHEADER where Reference = :Reference) ");

		logger.trace(Literal.SQL + selectSql.toString());

		RowMapper<FinReceiptDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinReceiptDetail.class);

		try {
			list = jdbcTemplate.query(selectSql.toString(), paramMap, typeRowMapper);
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

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT FM.FINREFERENCE");
		sql.append(", CASE WHEN RF.PRODUCTCATEGORY = 'ODFCLITY' THEN FM.FINASSETVALUE");
		sql.append(" WHEN RF.PRODUCTCATEGORY = 'CD' THEN FP.FINAMOUNT ELSE FP.TOTALPRISCHD END LOANAMOUNT");
		sql.append(", FM.REPAYBASERATE PLRRATE, FM.REPAYMARGIN VARIANCE");
		sql.append(
				", CASE WHEN rf.PRODUCTCATEGORY = 'CD' THEN fm.REPAYPROFITRATE ELSE FM.EFFECTIVERATEOFRETURN END IRR");
		sql.append(", FP.CURREDUCINGRATE ROI");
		sql.append(", FP.NOINST TENURE, FM.REPAYFRQ, FP.TOTALPRIPAID EMIRECEIVEDPRI ");
		sql.append(", FP.TOTALPFTPAID EMIRECEIVEDPFT, 0.00 PREFERREDCARDLIMIT, FP.PRVRPYSCHPRI PREVINSTAMTPRI");
		sql.append(", FP.PRVRPYSCHPFT PREVINSTAMTPFT");
		sql.append(
				", CASE WHEN FM.RVWRATEAPPLFOR IS NULL OR FM.RVWRATEAPPLFOR = '#' THEN 'FIXED' ELSE 'FLOATING' END INTRATETYPE");
		sql.append(", FP.LATESTDISBDATE LASTDISBURSALDATE, FP.FIRSTREPAYDATE FIRSTDUEDATE");
		sql.append(", FM.MATURITYDATE ENDINSTALLMENTDATE, FM.DOWNPAYMENT ADVINSTAMT, FM.SVAMOUNT");
		sql.append(", FM.FINISACTIVE, FM.CLOSINGSTATUS, FM.CLOSEDDATE");
		sql.append(", FP.FUTUREINST FUTUREINSTNO, FP.TOTALPRISCHD FUTUREPRI1");
		sql.append(", FP.TDSCHDPRI FUTUREPRI2, FP.TOTALPFTSCHD FUTURERPYPFT1");
		sql.append(", FP.TDSCHDPFT FUTURERPYPFT2, FP.TOTCHARGESPAID CHARGE_COLL_CUST");
		sql.append(", FP.UPFRONTFEE UPFRONT_INT_CUST, 0 INT_PAID_DEALER_UPFRONT");
		sql.append(", 0 PRE_EMI_INT_PAID, '' REPO_STATUS");
		sql.append(", '' REPO_DATE, '' SALE_DATE ");
		sql.append(", '' RELEASE_DATE, FP.LATESTRPYDATE ");
		sql.append(", C.CCYMINORCCYUNITS, C.CCYEDITFIELD, COALESCE(FP.TOTALPFTCPZ, 0) TOTALPFTCPZ");
		sql.append(" FROM FINANCEMAIN FM ");
		sql.append(" INNER JOIN FINPFTDETAILS FP ON FP.FINREFERENCE = FM.FINREFERENCE");
		sql.append(" INNER JOIN RMTFINANCETYPES RF ON RF.FINTYPE = FM.FINTYPE");
		sql.append(" INNER JOIN RMTCURRENCIES C ON C.CCYCODE = FP.FINCCY");
		sql.append(" Where FM.FinReference = :FinReference");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("FinReference", finReference);

		RowMapper<StatementOfAccount> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(StatementOfAccount.class);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), parameterSource, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		logger.debug(Literal.LEAVING);

		return null;
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
		selectSql.append("  MaturityDate,NoPAIDINST,TotalPFTPAID,TotalPRIPAID,TotalPRIBAL,TotalPFTBAL,NOInst, ");
		selectSql.append("  NSchdDate,NSchdPri,NSchdPft, TotalTenor, CurReducingRate FROM  FinPftDetails");
		selectSql.append("  Where FinReference = :FinReference");

		logger.trace(Literal.SQL + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeProfitDetail);
		RowMapper<FinanceProfitDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceProfitDetail.class);

		try {
			financeProfitDetail = jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
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
			activeCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
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
		selectSql.append(
				" T2.CustAddrHNbr, T2.CustFlatNbr, T2.CustAddrStreet, T2.CustPOBox, T2.custaddrline1, T2.custaddrline2, T2.custAddrZip,");
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
		RowMapper<StatementOfAccount> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(StatementOfAccount.class);

		try {
			statementOfAccount = jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
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
			RowMapper<StatementOfAccount> typeRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(StatementOfAccount.class);
			statementOfAccount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
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

		selectSql.append(" Select Amount, AmountType, BalanceAmt  FROM FinExcessAmount");
		selectSql.append(" Where FinReference = :FinReference");

		logger.trace(Literal.SQL + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finSchdDetail);
		RowMapper<FinExcessAmount> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinExcessAmount.class);

		try {
			finExcessAmountList = jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
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
			finRepayHeadersList = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
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

		Date maxSchDate = null;
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
			maxSchDate = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Date.class);
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
		sql.append(
				" SELECT Distinct PRD.ReceiptId,PRD.SchDate,BR.REASON BounceReason, PRD.Status, PRH.MandateType, M.MandateRef, ");
		sql.append(" EMIno FROM PRESENTMENTDETAILS PRD Left Join Mandates M ON M.MandateId = PRD.MandateId ");
		sql.append(" Left join BOUNCEREASONS BR ON PRD.BOUNCEID  = BR.BOUNCEID ");
		sql.append(" inner join PRESENTMENTHEADER PRH on PRH.id = PRD.Presentmentid");
		sql.append(" Where PRD.RECEIPTID != 0 and FinReference = :FinReference");

		logger.trace(Literal.SQL + sql.toString());

		RowMapper<PresentmentDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(PresentmentDetail.class);

		try {
			presentmentDetailsList = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			presentmentDetailsList = new ArrayList<PresentmentDetail>();
		} finally {
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
		sql.append(
				" SELECT RepayID,TdsSchdPayNow,PftSchdWaivedNow,PriSchdWaivedNow,WaivedAmt FROM FinRepayscheduledetail");
		sql.append(" WHERE FinReference = :FinReference");

		logger.trace(Literal.SQL + sql.toString());

		RowMapper<RepayScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(RepayScheduleDetail.class);

		try {
			finRepayScheduleDetailsList = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
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
			vasRecordingsList = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
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
			sql.append(
					" WHERE T1.Schdate <= (Select to_date(sysparmvalue, 'YYYY-MM-DD')  sysparmvalue from smtparameters where SYSPARMCODE='APP_DATE')");
		}
		sql.append(" And T1.SchAmount !=0 And T2.FinReference = :FinReference");

		logger.trace(Literal.SQL + sql.toString());

		RowMapper<FinFeeScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinFeeScheduleDetail.class);

		try {
			finFeeScheduleDetailsList = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
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
			RowMapper<EventProperties> typeRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(EventProperties.class);
			statementOfAccount = this.jdbcTemplate.queryForObject(sql.toString(), parameterMap, typeRowMapper);
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
			list = this.jdbcTemplate.queryForList(sql.toString(), new MapSqlParameterSource(), String.class);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION);
		} finally {
			sql = null;
			logger.debug(Literal.LEAVING);
		}

		return list;
	}

	@Override
	public List<ApplicantDetail> getApplicantDetails(String finReference) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		List<ApplicantDetail> applicantDetails = null;

		StringBuilder sql = new StringBuilder();
		sql.append(
				" Select JA.custcif CustCIF, Cust.CustShrtName CustName, PN.Phonenumber PhoneNum, 'Co-Applicant' ApplicantType");
		sql.append(" from Financemain FM Inner Join FinJointAccountDetails JA ON FM.Finreference = JA.Finreference");
		sql.append(" Inner Join Customers Cust ON Cust.CustCIF =  JA.Custcif ");
		sql.append(
				" Inner join CustomerPhonenumbers PN ON PN.Phonecustid = Cust.CustID And PN.PhoneTypePriority = 5  ");
		sql.append(" where FM.Finreference = :FinReference");
		sql.append(" Union All");
		sql.append(
				" Select GR.guarantorcif CustCIF,Cust.CustShrtName CustName,PN.phonenumber PhoneNum, 'Borrower' ApplicantType");
		sql.append(" from Financemain FM Inner Join finguarantorsdetails GR ON FM.finreference = GR.finreference");
		sql.append(" Inner Join Customers Cust ON Cust.CustCIF =  GR.guarantorcif  ");
		sql.append(" Inner Join CustomerPhonenumbers PN ON PN.PhoneCustid = Cust.custID And PN.PhoneTypePriority = 5");
		sql.append(" where FM.Finreference = :FinReference");

		logger.trace(Literal.SQL + sql.toString());

		RowMapper<ApplicantDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ApplicantDetail.class);

		try {
			applicantDetails = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (DataAccessException e) {
			applicantDetails = new ArrayList<ApplicantDetail>();
		} finally {
			source = null;
			sql = null;
			logger.debug(Literal.LEAVING);
		}

		return applicantDetails;
	}

	@Override
	public List<OtherFinanceDetail> getCustOtherFinDetails(long custID, String finReference) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("CustID", custID);

		List<OtherFinanceDetail> otherFinanceDetails = null;

		StringBuilder sql = new StringBuilder();
		sql.append(" Select finReference, FinType, 'Primary Customer' ApplicantType from FinanceMain ");
		sql.append(" Where CustId = :CustID And Finreference != :FinReference Union All ");

		sql.append(" Select FM.finreference, FM.fintype, 'Co-Applicant' ApplicantType from");
		sql.append(" FinJointAccountDetails JA Inner Join FinanceMain FM  ON FM.finreference = JA.finreference");
		sql.append(" where FM.CustID = :CustID Union All ");

		sql.append(" Select FM.Finreference, FM.Fintype, 'Borrower' ApplicantType");
		sql.append(" from FinGuarantorsDetails GR Inner Join FinanceMain FM ON FM.finreference = GR.finreference ");
		sql.append(" where FM.CustID = :CustID");

		logger.trace(Literal.SQL + sql.toString());

		RowMapper<OtherFinanceDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(OtherFinanceDetail.class);

		try {
			otherFinanceDetails = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (DataAccessException e) {
			otherFinanceDetails = new ArrayList<OtherFinanceDetail>();
		} finally {
			source = null;
			sql = null;
			logger.debug(Literal.LEAVING);
		}

		return otherFinanceDetails;
	}

	@Override
	public List<FeeWaiverDetail> getFeeWaiverDetail(String finReference) {

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		List<FeeWaiverDetail> feeWaiverDetails = null;

		StringBuilder sql = new StringBuilder();
		sql.append(" select finreference, fwh.valuedate,fwd.CURRWAIVERAMOUNT, fwd.FeetypeCode ");
		sql.append(" From FEEWAIVERHEADER fwh ");
		sql.append(" inner join  FEEWAIVERdetails fwd on fwh.WAIVERID=fwd.WAIVERID  ");
		sql.append(" where   fwh.FinReference =:FinReference");

		logger.trace(Literal.SQL + sql.toString());

		RowMapper<FeeWaiverDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FeeWaiverDetail.class);

		try {
			feeWaiverDetails = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (DataAccessException e) {
			feeWaiverDetails = new ArrayList<FeeWaiverDetail>();
		} finally {
			source = null;
			sql = null;
			logger.debug(Literal.LEAVING);
		}

		return feeWaiverDetails;
	}

	@Override
	public List<String> getCustLoanDetails(long custID) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustID", custID);
		List<String> list = null;
		StringBuilder sql = new StringBuilder();
		sql.append(" Select finReference from FinanceMain ");
		sql.append(" Where CustId = :CustID");
		logger.trace(Literal.SQL + sql.toString());
		try {
			list = this.jdbcTemplate.queryForList(sql.toString(), source, String.class);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION);
		} finally {
			sql = null;
			logger.debug(Literal.LEAVING);
		}

		return list;
	}

	@Override
	public List<FinanceDisbursement> getFinanceDisbursementByFinRef(String finReference) {
		logger.debug("Entering");
		List<FinanceDisbursement> financeDisbursements = null;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder selectSql = new StringBuilder("Select FinReference, DisbDate, DisbSeq, ");
		selectSql.append(" DisbAmount, DisbReqDate, DisbDisbursed, DisbIsActive ");
		selectSql.append(" From FinDisbursementDetails");
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinanceDisbursement> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceDisbursement.class);

		try {
			financeDisbursements = this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			financeDisbursements = null;
		}
		logger.debug("Leaving");
		return financeDisbursements;
	}
}