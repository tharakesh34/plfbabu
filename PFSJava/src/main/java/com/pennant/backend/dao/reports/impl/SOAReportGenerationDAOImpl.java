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
 * * FileName : SOAReportGenerationDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * *
 * Modified Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * 24-05-2018 Srikanth 0.2 Merge the Code From Bajaj To Core *
 * 
 * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.reports.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.reports.SOAReportGenerationDAO;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.finance.AdviseDueTaxDetail;
import com.pennant.backend.model.finance.CrossLoanTransfer;
import com.pennant.backend.model.finance.FeeWaiverDetail;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeRefundDetails;
import com.pennant.backend.model.finance.FinFeeRefundHeader;
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
import com.pennant.backend.model.finance.RestructureCharge;
import com.pennant.backend.model.systemmasters.ApplicantDetail;
import com.pennant.backend.model.systemmasters.OtherFinanceDetail;
import com.pennant.backend.model.systemmasters.StatementOfAccount;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.presentment.model.PresentmentDetail;

/**
 * DAO methods implementation for the <b>ReportConfiguration model</b> class.<br>
 * 
 */

public class SOAReportGenerationDAOImpl extends BasicDao<StatementOfAccount> implements SOAReportGenerationDAO {
	private static Logger logger = LogManager.getLogger(SOAReportGenerationDAOImpl.class);

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

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, ClosingStatus, FinStartDate, FeeChargeAmt, FinCurrAssetValue, FInApprovedDate, FinType");
		sql.append(", FinCategory, FixedRateTenor, FixedTenorRate, NumberOfTerms, RepayProfitRate, RepayBaseRate");
		sql.append(", FinCcy, RepaySpecialRate, RepayMargin, advemiterms, advanceemi, MaturityDate, CustId, CalTerms");
		sql.append(", AdvType, GrcAdvType, DownPayment, Promotioncode, PromotionSeqId");
		sql.append(", FinAssetValue, RepayRateBasis, GraceTerms, ClosedDate, FinIsActive, Restructure");
		sql.append(" FROM FinanceMain Where FinReference = :FinReference");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finMain);
		RowMapper<FinanceMain> typeRowMapper = BeanPropertyRowMapper.newInstance(FinanceMain.class);

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
		selectSql.append(
				" ,PrincipalSchd ,FeeSchd,SchdPriPaid,SchdPftPaid,SchdFeePaid, RepayOnSchDate, Cpzamount, cpzOnSchDate,pftOnSchDate, baseRate");
		selectSql.append(" FROM FINSCHEDULEDETAILS");
		selectSql.append(" Where FinReference = :FinReference");

		logger.trace(Literal.SQL + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finSchdDetail);
		RowMapper<FinanceScheduleDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(FinanceScheduleDetail.class);

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
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" LlDate, AmtToBeReleased, PaymentType, LlReferenceNo, TransactionRef, ValueDate, Lastmnton");
		sql.append(", PaymentDetail ");
		sql.append(" From FinAdvancePayments");
		sql.append(" Where (Status not in (?,?) or Status is null) And FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, "CANCELED");
			ps.setString(2, "REJECTED");
			ps.setString(3, finReference);
		}, (rs, rowNum) -> {
			FinAdvancePayments fap = new FinAdvancePayments();

			fap.setLLDate(JdbcUtil.getDate(rs.getDate("LlDate")));
			fap.setAmtToBeReleased(rs.getBigDecimal("AmtToBeReleased"));
			fap.setPaymentType(rs.getString("PaymentType"));
			fap.setLLReferenceNo(rs.getString("LlReferenceNo"));
			fap.setTransactionRef(rs.getString("TransactionRef"));
			fap.setValueDate(JdbcUtil.getDate(rs.getDate("ValueDate")));
			fap.setLastMntOn(rs.getTimestamp("Lastmnton"));
			fap.setPaymentDetail(rs.getString("PaymentDetail"));

			return fap;
		});
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
					" And PostDate <= (Select to_date(sysparmvalue, 'yyyy-MM-DD') sysparmvalue From smtparameters where SYSPARMCODE='APP_DATE')");
		}
		logger.trace(Literal.SQL + selectSql.toString());

		RowMapper<PaymentInstruction> typeRowMapper = BeanPropertyRowMapper.newInstance(PaymentInstruction.class);

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
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" TotPenaltyAmt, TotWaived, TotPenaltyPaid, FinODSchdDate, FinODTillDate");
		sql.append(", LPIAmt, LPIPaid, LPIWaived");
		sql.append(" FROM FinODDetails");
		sql.append(" Where FinReference = ? and (TotPenaltyAmt > ? or LPIAmt > ?)");

		logger.trace(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, finReference);
			ps.setInt(2, 0);
			ps.setInt(3, 0);
		}, (rs, rowNum) -> {
			FinODDetails fod = new FinODDetails();
			fod.setTotPenaltyAmt(rs.getBigDecimal("TotPenaltyAmt"));
			fod.setTotWaived(rs.getBigDecimal("TotWaived"));
			fod.setTotPenaltyPaid(rs.getBigDecimal("TotPenaltyPaid"));
			fod.setFinODSchdDate(JdbcUtil.getDate(rs.getDate("FinODSchdDate")));
			fod.setFinODTillDate(JdbcUtil.getDate(rs.getDate("FinODTillDate")));
			fod.setLPIAmt(rs.getBigDecimal("LPIAmt"));
			fod.setLPIPaid(rs.getBigDecimal("LPIPaid"));
			fod.setLPIWaived(rs.getBigDecimal("LPIWaived"));

			return fod;
		});
	}

	/**
	 * get the Manual Advise List
	 */
	@Override
	public List<ManualAdvise> getManualAdvise(String finReference, Date valueDate) {
		logger.debug(Literal.ENTERING);

		ManualAdvise manualAdvise = new ManualAdvise();
		manualAdvise.setFinReference(finReference);
		manualAdvise.setValueDate(valueDate);
		List<ManualAdvise> manualAdviseList = new ArrayList<ManualAdvise>();

		StringBuilder selectSql = new StringBuilder();

		selectSql.append(
				" Select T1.FinReference, T1.PostDate, T1.AdviseAmount, T1.AdviseType, T1.ReceiptId, T1.BounceId, T1.Adviseid, T1.FeeTypeId, T1.BalanceAmt,");
		selectSql.append(" T1.WaivedAmount, T1.PaidAmount, T2.FeeTypeDesc, T1.ValueDate,T2.TaxComponent,");
		selectSql.append(
				" T1.PaidCGST, T1.PaidSGST, T1.PaidUGST, T1.PaidIGST, T1.PaidCESS, T1.WaivedCGST, T1.WaivedSGST,");
		selectSql.append(" T1.WaivedIGST, T1.WaivedUGST, T1.WaivedCESS, T3.Reason BounceCodeDesc, T1.Remarks");
		selectSql.append(" FROM ManualAdvise T1");
		selectSql.append(" Left Join FEETYPES T2 ON T2.FeeTypeId = T1.FeeTypeId");
		selectSql.append(" Left Join Bouncereasons T3 ON T1.bounceid = T3.BounceId");
		selectSql.append(" Where FinReference = :FinReference and T1.AdviseAmount > 0");
		selectSql.append(" and ValueDate <= :ValueDate and (Status is null or Status ='M')");
		selectSql.append(" ORDER by T1.adviseID");

		logger.trace(Literal.SQL + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(manualAdvise);
		RowMapper<ManualAdvise> typeRowMapper = BeanPropertyRowMapper.newInstance(ManualAdvise.class);

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

		StringBuilder sql = new StringBuilder();
		sql.append(" select * from (");
		sql.append(
				"Select MA.FinReference,MA.Adviseid, FW.waiverid,MAM.MovementId, Movementdate, movementamount, F.TaxComponent");
		sql.append(
				", MAM.WaivedAmount, MAM.TaxHeaderId, F.FEETYPEDESC, MA.ValueDate, FW.currwaivergst, FW.curractualwaiver, F.FeeTypeCode");
		sql.append(" from ManualAdvise MA ");
		sql.append(" INNER JOIN ManualAdviseMovements MAM on MAM.Adviseid = MA.Adviseid ");
		sql.append(" LEFT JOIN FEETYPES F ON F.FEETYPEID = MA.FEETYPEID");
		sql.append(" INNER JOIN (select FW.FinReference, FWD.FeeTypeCode, FW.WaiverID, FWD.Adviseid");
		sql.append(", FWD.currwaivergst,FWD.curractualwaiver from  feewaiverheader FW ");
		sql.append(" INNER JOIN feewaiverdetails FWD on FWD.waiverid = FW.waiverid) FW ON");
		sql.append(" FW.FinReference = MA.FinReference and FW.Adviseid = MA.Adviseid and FW.WaiverID = MAM.WaiverID");
		sql.append(" where FW.Adviseid  not in  (-1, -2, -3) and F.FeeTypeCode is not null ");
		sql.append(" union all ");
		sql.append(
				" Select MA.FinReference,MA.Adviseid,FW.waiverid,MAM.MovementId, Movementdate, movementamount, F.TaxComponent");
		sql.append(
				", MAM.WaivedAmount, MAM.TaxHeaderId, F.FEETYPEDESC, MA.ValueDate, FW.currwaivergst, FW.curractualwaiver, F.FeeTypeCode");
		sql.append(" from ManualAdvise MA ");
		sql.append(" INNER JOIN ManualAdviseMovements MAM on MAM.Adviseid = MA.Adviseid ");
		sql.append(" LEFT JOIN FEETYPES F ON F.FEETYPEID = MA.FEETYPEID");
		sql.append(" LEFT JOIN (select FW.FinReference, FWD.FeeTypeCode, FW.WaiverID, FWD.Adviseid");
		sql.append(", FWD.currwaivergst,FWD.curractualwaiver from feewaiverheader FW");
		sql.append(" INNER JOIN feewaiverdetails FWD on FWD.waiverid = FW.waiverid) FW");
		sql.append(" on FW.FinReference = MA.FinReference and FW.WaiverID = MAM.WaiverID");
		sql.append(" where FW.Adviseid  in  (-1, -2, -3) and F.FeeTypeCode is not null ");
		sql.append(
				" ) ma where ma.FinReference = :FinReference and ma.WaivedAmount > 0 and ((ma.curractualwaiver > 0 or ma.currwaivergst > 0) or (ma.curractualwaiver != 0 and ma.curractualwaiver <=600))");
		sql.append(" order by ma.MovementId, ma.Adviseid");

		logger.trace(Literal.SQL + sql.toString());

		RowMapper<ManualAdviseMovements> typeRowMapper = BeanPropertyRowMapper.newInstance(ManualAdviseMovements.class);

		try {
			manualAdviseMovementsList = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			manualAdviseMovementsList = new ArrayList<ManualAdviseMovements>();
		}

		logger.debug(Literal.LEAVING);

		return manualAdviseMovementsList;
	}

	@Override
	public List<FinFeeDetail> getFinFeedetails(String finReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ffd.FinID, ffd.FinReference, ffd.RemainingFee, ffd.PaidAmount, ffd.Postdate, ffd.FeeTypeID");
		sql.append(", ffd.VasReference, ffd.Finevent, ffd.Originationfee, ffd.FeeSchedulemethod");
		sql.append(", ft.FeeTypeCode, ft.FeeTypedesc, ffd.WaivedAmount, ft.TaxComponent");
		sql.append(" From FinFeeDetail ffd");
		sql.append(" Left Join FeeTypes ft ON ft.FeeTypeId = ffd.FeeTypeId");
		sql.append(" WHERE ffd.FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			FinFeeDetail fee = new FinFeeDetail();

			fee.setFinID(rs.getLong("FinID"));
			fee.setFinReference(rs.getString("FinReference"));
			fee.setRemainingFee(rs.getBigDecimal("RemainingFee"));
			fee.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			fee.setPostDate(JdbcUtil.getDate(rs.getDate("Postdate")));
			fee.setFeeTypeID(rs.getLong("FeeTypeID"));
			fee.setVasReference(rs.getString("VasReference"));
			fee.setFinEvent(rs.getString("Finevent"));
			fee.setOriginationFee(rs.getBoolean("Originationfee"));
			fee.setFeeScheduleMethod(rs.getString("FeeSchedulemethod"));
			fee.setFeeTypeCode(rs.getString("FeeTypeCode"));
			fee.setFeeTypeDesc(rs.getString("FeeTypedesc"));
			fee.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			fee.setTaxComponent(rs.getString("TaxComponent"));

			return fee;
		}, finReference);
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
		sql.append(" Select  ReceiptID, AllocationType, PaidAmount, TDSPaid, WaivedAmount");
		sql.append(" From ReceiptAllocationDetail");
		sql.append(" Where ReceiptId in (Select ReceiptId from FinReceiptHeader where Reference = :FinReference)");

		logger.trace(Literal.SQL + sql.toString());

		RowMapper<ReceiptAllocationDetail> typeRowMapper = BeanPropertyRowMapper
				.newInstance(ReceiptAllocationDetail.class);

		try {
			finReceiptAllocationDetailsList = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finReceiptAllocationDetailsList = new ArrayList<ReceiptAllocationDetail>();
		} finally {
			logger.debug(Literal.LEAVING);
		}

		return finReceiptAllocationDetailsList;
	}

	/**
	 * get the FinReceiptHeaders List
	 */
	@Override
	public List<FinReceiptHeader> getFinReceiptHeaders(String finReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ReceiptID, ReceiptModeStatus, ReceiptDate, ReceiptMode, BounceDate");
		sql.append(", ReceiptPurpose, RefWaiverAmt, RecAppDate, ReceivedDate, ValueDate, TDSAmount");
		sql.append(" From FinReceiptHeader");
		sql.append(" Where Reference = ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;

				ps.setString(index, finReference);
			}
		}, new RowMapper<FinReceiptHeader>() {

			@Override
			public FinReceiptHeader mapRow(ResultSet rs, int rowNum) throws SQLException {
				FinReceiptHeader frh = new FinReceiptHeader();
				frh.setReceiptID(rs.getLong("ReceiptID"));
				frh.setReceiptModeStatus(rs.getString("ReceiptModeStatus"));
				frh.setReceiptDate(JdbcUtil.getDate(rs.getDate("ReceiptDate")));
				frh.setReceiptMode(rs.getString("ReceiptMode"));
				frh.setBounceDate(JdbcUtil.getDate(rs.getDate("BounceDate")));
				frh.setReceiptPurpose(rs.getString("ReceiptPurpose"));
				frh.setRefWaiverAmt(rs.getBigDecimal("RefWaiverAmt"));
				frh.setRecAppDate(JdbcUtil.getDate(rs.getDate("RecAppDate")));
				frh.setReceivedDate(JdbcUtil.getDate(rs.getDate("ReceivedDate")));
				frh.setValueDate(JdbcUtil.getDate(rs.getDate("ValueDate")));
				frh.setTdsAmount(rs.getBigDecimal("TDSAmount"));

				return frh;
			}
		});

	}

	/**
	 * get the FinReceiptDetails List
	 */
	@Override
	public List<FinReceiptDetail> getFinReceiptDetails(String finReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder selectSql = new StringBuilder();

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("Reference", finReference);
		// 30-08-2019:Added ValueDate
		selectSql.append(
				" SELECT ReceiptID,PaymentType,PaymentRef,FavourNumber,Amount,Status,ReceiptSeqID,ReceivedDate,ValueDate,TransactionRef  from FinReceiptDetail");
		selectSql.append(" Where ReceiptId in (Select ReceiptId from FINRECEIPTHEADER where Reference = :Reference) ");

		logger.trace(Literal.SQL + selectSql.toString());

		RowMapper<FinReceiptDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(FinReceiptDetail.class);

		return jdbcTemplate.query(selectSql.toString(), paramMap, typeRowMapper);
	}

	/**
	 * get the Statement of Account records
	 */
	@Override
	public StatementOfAccount getSOALoanDetails(String finReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT FM.FINID, FM.FINREFERENCE");
		sql.append(", CASE WHEN RF.PRODUCTCATEGORY = 'ODFCLITY' THEN FM.FINASSETVALUE");
		sql.append(" ELSE FP.TOTALPRISCHD END LOANAMOUNT");
		sql.append(", FM.FINASSETVALUE");
		sql.append(", FM.REPAYBASERATE PLRRATE, FM.REPAYMARGIN VARIANCE");
		sql.append(
				", CASE WHEN rf.PRODUCTCATEGORY = 'CD' THEN fm.REPAYPROFITRATE ELSE FM.EFFECTIVERATEOFRETURN END IRR");
		sql.append(", FP.CURREDUCINGRATE ROI");
		sql.append(", FP.NOINST TENURE, FM.REPAYFRQ, FP.TOTALPRIPAID EMIRECEIVEDPRI ");
		sql.append(", FP.TOTALPFTPAID EMIRECEIVEDPFT, 0.00 PREFERREDCARDLIMIT, FP.PRVRPYSCHPRI PREVINSTAMTPRI");
		sql.append(", FP.PRVRPYSCHPFT PREVINSTAMTPFT");
		sql.append(
				", CASE WHEN FM.RVWRATEAPPLFOR IS NULL OR FM.RVWRATEAPPLFOR = '#' THEN 'FIXED' ELSE 'FLOATING' END INTRATETYPE");
		sql.append(", CASE WHEN FM.REPAYRATEBASIS ='R' THEN 'REDUCING' WHEN FM.REPAYRATEBASIS ='F' THEN 'FLAT'");
		sql.append(" WHEN FM.REPAYRATEBASIS ='C' THEN 'FLAT CONVERTING TO REDUCE' ELSE '' END RATETYPE");
		sql.append(", FP.LATESTDISBDATE LASTDISBURSALDATE, FP.FIRSTREPAYDATE FIRSTDUEDATE");
		sql.append(
				", CASE WHEN  FM.CLOSINGSTATUS ='E' THEN FM.CLOSEDDATE ELSE FM.MATURITYDATE END ENDINSTALLMENTDATE, FM.DOWNPAYMENT ADVINSTAMT");
		sql.append(", FM.SVAMOUNT, FM.FINISACTIVE, FM.CLOSINGSTATUS, FM.CLOSEDDATE");
		sql.append(", FP.FUTUREINST FUTUREINSTNO, FP.TOTALPRISCHD FUTUREPRI1");
		sql.append(", FP.TDSCHDPRI FUTUREPRI2, FP.TOTALPFTSCHD FUTURERPYPFT1");
		sql.append(", FP.TDSCHDPFT FUTURERPYPFT2, FP.TOTCHARGESPAID CHARGE_COLL_CUST");
		sql.append(", FP.UPFRONTFEE UPFRONT_INT_CUST, 0 INT_PAID_DEALER_UPFRONT");
		sql.append(", 0 PRE_EMI_INT_PAID, '' REPO_STATUS");
		sql.append(", '' REPO_DATE, '' SALE_DATE ");
		sql.append(", '' RELEASE_DATE, FP.LATESTRPYDATE ");
		sql.append(", C.CCYMINORCCYUNITS, C.CCYEDITFIELD, COALESCE(FP.TOTALPFTCPZ, 0) TOTALPFTCPZ");
		sql.append(", FM.APPLICATIONNO, FM.VANCODE, FP.CURODDAYS DPDCOUNT");
		sql.append(" FROM FINANCEMAIN FM ");
		sql.append(" INNER JOIN FINPFTDETAILS FP ON FP.FINREFERENCE = FM.FINREFERENCE");
		sql.append(" INNER JOIN RMTFINANCETYPES RF ON RF.FINTYPE = FM.FINTYPE");
		sql.append(" INNER JOIN RMTCURRENCIES C ON C.CCYCODE = FP.FINCCY");
		sql.append(" Where FM.FinReference = :FinReference");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("FinReference", finReference);

		RowMapper<StatementOfAccount> typeRowMapper = BeanPropertyRowMapper.newInstance(StatementOfAccount.class);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), parameterSource, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * get the Finance profit Details List
	 */
	@Override
	public FinanceProfitDetail getFinanceProfitDetails(String finReference) {
		logger.debug(Literal.ENTERING);

		FinanceProfitDetail financeProfitDetail = new FinanceProfitDetail();
		financeProfitDetail.setFinReference(finReference);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustId, FinStartDate, LinkedFinRef, ClosedlinkedFinRef, FinBranch, FinType, FinPurpose");
		sql.append(", MaturityDate, NoPAIDINST, TotalPFTPAID, TotalPRIPAID, TotalPRIBAL, TotalPFTBAL, NOInst");
		sql.append(", NSchdDate, NSchdPri, NSchdPft, TotalTenor, CurReducingRate");
		sql.append(", tdschdpri, totalpripaid, NOODInst, FinAmount");
		sql.append(" FROM  FinPftDetails  Where FinReference = :FinReference");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeProfitDetail);
		RowMapper<FinanceProfitDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(FinanceProfitDetail.class);

		try {
			financeProfitDetail = jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
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
		selectSql.append(" T7.CustEMail, T8.SaluationDesc CustSalutation, T1.CustCtgCode");
		selectSql.append(" From Customers T1");
		selectSql.append(" Left Join CustomerAddresses T2 ON T1.CustID = T2.CustID and T2.custAddrPriority = 5");
		selectSql.append(" Left Join Bmtcountries T3 on T3.CountryCode = T2.CustAddrCountry");
		selectSql.append(" Left Join RmtProvinceVsCity T4 on T4.PcCity = T2.CustAddrCity");
		selectSql.append(" Left Join RmtCountryVsProvince T5 on T5.CpProvince = T2.custAddrProvince");
		selectSql.append(" Left Join CustomerPhoneNumbers T6 ON T1.CustID = T6.PhoneCustID  and PHONETYPEPRIORITY = 5");
		selectSql.append(" Left Join CustomerEMails T7 on T7.CustID = T1.CustID and CustEmailPriority = 5");
		selectSql.append(" Left Join BMTSalutations T8 ON T1.CustSalutationCode = T8.SalutationCode");
		selectSql.append(" and T1.CustGenderCode = T8.SalutationGenderCode");
		selectSql.append(" Where T1.CustID = :CustID");

		logger.trace(Literal.SQL + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(statementOfAccount);
		RowMapper<StatementOfAccount> typeRowMapper = BeanPropertyRowMapper.newInstance(StatementOfAccount.class);

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
			RowMapper<StatementOfAccount> typeRowMapper = BeanPropertyRowMapper.newInstance(StatementOfAccount.class);
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * get the FinExcessAmountList
	 */
	@Override
	public List<FinExcessAmount> getFinExcessAmountsList(String finReference) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select Amount, AmountType, BalanceAmt FROM FinExcessAmount");
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			FinExcessAmount excessAmount = new FinExcessAmount();
			excessAmount.setAmount(rs.getBigDecimal("Amount"));
			excessAmount.setAmountType(rs.getString("AmountType"));
			excessAmount.setBalanceAmt(rs.getBigDecimal("BalanceAmt"));
			return excessAmount;
		}, finReference);

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

		RowMapper<FinRepayHeader> typeRowMapper = BeanPropertyRowMapper.newInstance(FinRepayHeader.class);

		try {
			finRepayHeadersList = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finRepayHeadersList = new ArrayList<FinRepayHeader>();
		} finally {
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
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Date.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
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
		sql.append(
				" EMIno, PRH.PresentmentType FROM PRESENTMENTDETAILS PRD Left Join Mandates M ON M.MandateId = PRD.MandateId ");
		sql.append(" Left join BOUNCEREASONS BR ON PRD.BOUNCEID  = BR.BOUNCEID ");
		sql.append(" inner join PRESENTMENTHEADER PRH on PRH.id = PRD.Presentmentid");
		sql.append(" Where PRD.RECEIPTID != 0 and FinReference = :FinReference");

		logger.trace(Literal.SQL + sql.toString());

		RowMapper<PresentmentDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(PresentmentDetail.class);

		try {
			presentmentDetailsList = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			presentmentDetailsList = new ArrayList<PresentmentDetail>();
		} finally {
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

		RowMapper<RepayScheduleDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(RepayScheduleDetail.class);

		try {
			finRepayScheduleDetailsList = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finRepayScheduleDetailsList = new ArrayList<RepayScheduleDetail>();
		} finally {
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

		RowMapper<VASRecording> typeRowMapper = BeanPropertyRowMapper.newInstance(VASRecording.class);

		try {
			vasRecordingsList = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			vasRecordingsList = new ArrayList<VASRecording>();
		} finally {
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
					" WHERE T1.Schdate <= (Select to_date(sysparmvalue, 'yyyy-MM-DD')  sysparmvalue from smtparameters where SYSPARMCODE='APP_DATE')");
		}
		sql.append(" And T1.SchAmount !=0 And T2.FinReference = :FinReference");

		logger.trace(Literal.SQL + sql.toString());

		RowMapper<FinFeeScheduleDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(FinFeeScheduleDetail.class);

		try {
			finFeeScheduleDetailsList = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finFeeScheduleDetailsList = new ArrayList<FinFeeScheduleDetail>();
		} finally {
			logger.debug(Literal.LEAVING);
		}

		return finFeeScheduleDetailsList;
	}

	@Override
	public EventProperties getEventPropertiesList(String configName) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource parameterMap = new MapSqlParameterSource();
		parameterMap = new MapSqlParameterSource();
		parameterMap.addValue("ConfigName", configName);

		StringBuilder sql = new StringBuilder("Select * from  DATA_ENGINE_EVENT_PROPERTIES");
		sql.append(" Where config_id in (Select Id from DATA_ENGINE_CONFIG where Name = :ConfigName)");

		logger.trace(Literal.SQL + sql.toString());

		try {
			RowMapper<EventProperties> typeRowMapper = BeanPropertyRowMapper.newInstance(EventProperties.class);
			return this.jdbcTemplate.queryForObject(sql.toString(), parameterMap, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<String> getSOAFinTypes() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select FinType from  SOA_FinTypes");
		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcTemplate.queryForList(sql.toString(), new MapSqlParameterSource(), String.class);
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

		RowMapper<ApplicantDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(ApplicantDetail.class);

		try {
			applicantDetails = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (DataAccessException e) {
			applicantDetails = new ArrayList<ApplicantDetail>();
		} finally {
			logger.debug(Literal.LEAVING);
		}

		return applicantDetails;
	}

	@Override
	public List<OtherFinanceDetail> getCustOtherFinDetails(long custID, String finReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, FinType, 'Primary Customer' ApplicantType, CustCif CustCIF");
		sql.append(", bp.ProductDesc product");
		sql.append(" From FinanceMain fm");
		sql.append(" Inner Join BMTProduct bp on bp.ProductCode = FM.FinCategory");
		sql.append(" Inner Join Customers c on c.CustID = fm.CustID");
		sql.append(" Where fm.CustId = ? and Finreference != ?");
		sql.append(" Union All ");
		sql.append(" Select fm.finreference, fm.fintype, 'Co-Applicant' ApplicantType, ja.CustCif CustCIF");
		sql.append(", bp.ProductDesc product");
		sql.append(" From FinJointAccountDetails ja");
		sql.append(" Inner Join FinanceMain fm on fm.finreference = ja.finreference");
		sql.append(" Inner Join BMTProduct bp on bp.ProductCode = FM.FinCategory");
		sql.append(" Where fm.CustID = ?");
		sql.append(" Union All ");
		sql.append(" Select fm.Finreference, fm.Fintype, 'Borrower' ApplicantType, Guarantorcif CustCIF");
		sql.append(", bp.ProductDesc product");
		sql.append(" From FinGuarantorsDetails gr");
		sql.append(" Inner Join FinanceMain fm on fm.finreference = gr.finreference");
		sql.append(" Inner Join BMTProduct bp on bp.ProductCode = FM.FinCategory");
		sql.append(" Where fm.CustID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, custID);
			ps.setString(index++, finReference);
			ps.setLong(index++, custID);
			ps.setLong(index, custID);
		}, (rs, rowNum) -> {
			OtherFinanceDetail ofd = new OtherFinanceDetail();

			ofd.setFinReference(rs.getString("FinReference"));
			ofd.setFinType(rs.getString("FinType"));
			ofd.setApplicantType(rs.getString("ApplicantType"));
			ofd.setCustCIF(rs.getString("CustCIF"));

			return ofd;
		});
	}

	@Override
	public List<FeeWaiverDetail> getFeeWaiverDetail(String finReference) {

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		List<FeeWaiverDetail> feeWaiverDetails = null;

		StringBuilder sql = new StringBuilder();
		sql.append(" select finreference, fwh.valuedate,fwd.CURRWAIVERAMOUNT, fwd.FeetypeCode ");
		sql.append(", fwd.waiverId, fwd.feetypedesc, fwh.postingdate, fwh.valuedate ");
		sql.append(" From FEEWAIVERHEADER fwh ");
		sql.append(" inner join  FEEWAIVERdetails fwd on fwh.WAIVERID=fwd.WAIVERID  ");
		sql.append(" where   fwh.FinReference =:FinReference");

		logger.trace(Literal.SQL + sql.toString());

		RowMapper<FeeWaiverDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(FeeWaiverDetail.class);

		try {
			feeWaiverDetails = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (DataAccessException e) {
			feeWaiverDetails = new ArrayList<FeeWaiverDetail>();
		} finally {
			logger.debug(Literal.LEAVING);
		}

		return feeWaiverDetails;
	}

	@Override
	public List<String> getCustLoanDetails(long custID) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustID", custID);

		StringBuilder sql = new StringBuilder();
		sql.append(" Select finReference from FinanceMain ");
		sql.append(" Where CustId = :CustID");
		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcTemplate.queryForList(sql.toString(), source, String.class);
	}

	@Override
	public List<FinanceDisbursement> getFinanceDisbursementByFinRef(String finReference) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder selectSql = new StringBuilder("Select FinReference, DisbDate, DisbSeq, ");
		selectSql.append(" DisbAmount, DisbReqDate, DisbIsActive ");
		selectSql.append(" From FinDisbursementDetails");
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinanceDisbursement> typeRowMapper = BeanPropertyRowMapper.newInstance(FinanceDisbursement.class);

		return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	@Override
	public Map<Long, List<ReceiptAllocationDetail>> getReceiptAllocationDetailsMap(String finReference) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		Map<Long, List<ReceiptAllocationDetail>> finReceiptAllocationDetailsMap = null;

		StringBuilder sql = new StringBuilder();
		sql.append(
				" Select  ReceiptID, AllocationType, PaidAmount, TypeDesc,AllocationTo, WaivedAmount  From ReceiptAllocationDetail_View");
		sql.append(" Where ReceiptId in (Select ReceiptId from FinReceiptHeader where Reference = :FinReference)");

		logger.trace(Literal.SQL + sql.toString());

		try {
			finReceiptAllocationDetailsMap = this.jdbcTemplate.query(sql.toString(), source,
					new ResultSetExtractor<Map<Long, List<ReceiptAllocationDetail>>>() {

						@Override
						public Map<Long, List<ReceiptAllocationDetail>> extractData(ResultSet rs)
								throws SQLException, DataAccessException {
							List<ReceiptAllocationDetail> radList = null;
							Map<Long, List<ReceiptAllocationDetail>> radMap = new HashMap<>();
							while (rs.next()) {
								ReceiptAllocationDetail rad = new ReceiptAllocationDetail();
								rad.setReceiptID(rs.getLong("ReceiptID"));
								rad.setAllocationType(rs.getString("AllocationType"));
								rad.setPaidAmount(rs.getBigDecimal("PaidAmount"));
								rad.setTypeDesc(rs.getString("TypeDesc"));
								rad.setAllocationTo(rs.getLong("AllocationTo"));
								rad.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
								if (radMap.containsKey(rad.getReceiptID())) {
									radList = radMap.get(rad.getReceiptID());
								} else {
									radList = new ArrayList<>();
								}
								radList.add(rad);
								radMap.put(rad.getReceiptID(), radList);
							}

							return radMap;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			finReceiptAllocationDetailsMap = new HashMap<>();
		} finally {
			logger.debug(Literal.LEAVING);
		}

		return finReceiptAllocationDetailsMap;
	}

	/**
	 * get the FinAdvancePayments list of Cancelled Loan.
	 */
	@Override
	public List<FinAdvancePayments> getFinAdvPaymentsForCancelLoan(String finReference) {
		logger.debug(Literal.ENTERING);

		FinAdvancePayments finAdvPayment = new FinAdvancePayments();
		finAdvPayment.setFinReference(finReference);

		List<FinAdvancePayments> FinAdvancePaymentslist;

		StringBuilder selectSql = new StringBuilder();

		selectSql.append(
				" Select LlDate,AmtToBeReleased,PaymentType,LlReferenceNo,TransactionRef,ValueDate FROM FinAdvancePayments");
		selectSql.append(" Where (Status in ('CANCELED','REJECTED') or Status is null)");
		selectSql.append(" And FinReference = :FinReference");

		logger.trace(Literal.SQL + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAdvPayment);
		RowMapper<FinAdvancePayments> typeRowMapper = BeanPropertyRowMapper.newInstance(FinAdvancePayments.class);

		try {
			FinAdvancePaymentslist = jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			FinAdvancePaymentslist = new ArrayList<FinAdvancePayments>();
		}

		logger.debug(Literal.LEAVING);

		return FinAdvancePaymentslist;
	}

	@Override
	public List<FinFeeRefundHeader> getFinFeeRefundHeader(String finReference) {
		logger.debug(Literal.ENTERING);

		FinFeeRefundHeader finFeeRefundHeader = new FinFeeRefundHeader();
		finFeeRefundHeader.setFinReference(finReference);
		List<FinFeeRefundHeader> list;

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT HeaderId,LinkedTranId From FinFeeRefundHeader");
		selectSql.append(" Where FinReference = :FinReference");

		logger.trace(Literal.SQL + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeRefundHeader);
		RowMapper<FinFeeRefundHeader> typeRowMapper = BeanPropertyRowMapper.newInstance(FinFeeRefundHeader.class);

		try {
			list = jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			list = new ArrayList<>();
		}

		logger.debug(Literal.LEAVING);

		return list;
	}

	@Override
	public List<FinFeeRefundDetails> getFinFeeRefundDetails(String finReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder selectSql = new StringBuilder();

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("FinReference", finReference);
		// 30-08-2019:Added ValueDate
		selectSql.append(
				" SELECT Id, HeaderID, r.FeeId, RefundAmount, RefundAmtGST, RefundAmtOriginal, r.LastMntOn, t.feetypedesc as feeTypeCode from FinFeeRefundDetails r");
		selectSql.append(" left join FinFeeDetail f on f.feeid = r.feeid");
		selectSql.append(" left join feetypes t on t.feetypeid = f.feetypeid ");
		selectSql.append(
				" Where HeaderID in (Select HeaderId from FinFeeRefundHeader where FinReference = :FinReference) ");

		logger.trace(Literal.SQL + selectSql.toString());

		RowMapper<FinFeeRefundDetails> typeRowMapper = BeanPropertyRowMapper.newInstance(FinFeeRefundDetails.class);

		return jdbcTemplate.query(selectSql.toString(), paramMap, typeRowMapper);
	}

	@Override
	public String getFinGSTINDetails(String stateCode, String entityCode) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" TaxCode ProviderGSTIN");
		sql.append(" From TaxDetail");
		sql.append(" Where StateCode = ? and EntityCode = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new RowMapper<String>() {
				@Override
				public String mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs.getString("ProviderGSTIN");
				}
			}, stateCode, entityCode);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public StatementOfAccount getFinEntity(String finType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" dd.EntityCode, e.EntityDesc, e.StateCode");
		sql.append(" From RMTFinanceTypes ft");
		sql.append(" Inner Join SMTDivisionDetail dd on ft.FinDivision = dd.DivisionCode");
		sql.append(" Inner Join Entity e on dd.EntityCode = e.EntityCode");
		sql.append(" where FinType = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), new RowMapper<StatementOfAccount>() {
				@Override
				public StatementOfAccount mapRow(ResultSet rs, int rowNum) throws SQLException {
					StatementOfAccount soa = new StatementOfAccount();

					soa.setEntityCode(rs.getString("EntityCode"));
					soa.setEntityDesc(rs.getString("EntityDesc"));
					soa.setStateCode(rs.getString("StateCode"));

					return soa;
				}

			}, finType);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return new StatementOfAccount();
		}
	}

	@Override
	public StatementOfAccount getCustGSTINDetails(String finReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ftd.taxnumber CustGSTIN, cp.cpprovincename PlaceOfSupply");
		sql.append(" From FinTaxDetail ftd");
		sql.append(" Inner Join RmtCountryVsProvince cp on cp.cpprovince = ftd.province");
		sql.append(" where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), new RowMapper<StatementOfAccount>() {
				@Override
				public StatementOfAccount mapRow(ResultSet rs, int rowNum) throws SQLException {
					StatementOfAccount soa = new StatementOfAccount();

					soa.setCustGSTIN(rs.getString("CustGSTIN"));
					soa.setPlaceOfSupply(rs.getString("PlaceOfSupply"));

					return soa;
				}
			}, finReference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return new StatementOfAccount();
		}
	}

	public Map<Long, Integer> getInstNumber(String finReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" frd.receiptid, instnumber");
		sql.append(" from FinScheduleDetails fsd");
		sql.append(" INNER JOIN FinRepayDetails frd on  fsd.finreference = frd.finreference");
		sql.append(" and fsd.schdate = frd.finschddate");
		sql.append(" where fsd.schdate = (select Max(finschddate) from FinRepayDetails");
		sql.append(" where receiptid= frd.receiptid) and fsd.finreference =  ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), ps -> {
				ps.setString(1, finReference);
			}, new ResultSetExtractor<Map<Long, Integer>>() {

				@Override
				public Map<Long, Integer> extractData(ResultSet rs) throws SQLException, DataAccessException {
					Map<Long, Integer> rcMap = new HashMap<>();
					while (rs.next()) {
						rcMap.put(rs.getLong("ReceiptId"), rs.getInt("InstNumber"));
					}
					return rcMap;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Records are not found in FinScheduleDetails table for the FinReference >> " + finReference);
		}

		return new HashMap<>();
	}

	@Override
	public List<RestructureCharge> getRestructureChargeList(String finReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" RC.AlocType, RC.Capitalized, RC.ActualAmount, RC.TotalAmount, RC.FeeCode");
		sql.append(" From RESTRUCTURE_CHARGES RC");
		sql.append(" INNER JOIN Restructure_Details RD on RD.Id = RC.RestructureId");
		sql.append(" Where RD.FinReference = ? and RC.Capitalized = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, finReference);
			ps.setInt(2, 1);
		}, (rs, i) -> {
			RestructureCharge rc = new RestructureCharge();

			rc.setAlocType(rs.getString("AlocType"));
			rc.setCapitalized(rs.getBoolean("Capitalized"));
			rc.setActualAmount(rs.getBigDecimal("ActualAmount"));
			rc.setTotalAmount(rs.getBigDecimal("TotalAmount"));
			rc.setFeeCode(rs.getString("FeeCode"));

			return rc;
		});
	}

	@Override
	public AdviseDueTaxDetail getAdviseDueTaxDetails(long adviseId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AdviseId, TaxType, Amount, CGST, SGST, UGST, IGST, CESS, TotalGST");
		sql.append(" From AdviseDueTaxdetail");
		sql.append(" Where AdviseId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				AdviseDueTaxDetail due = new AdviseDueTaxDetail();

				due.setAdviseID(rs.getLong("AdviseId"));
				due.setTaxType(rs.getString("TaxType"));
				due.setAmount(rs.getBigDecimal("Amount"));
				due.setCGST(rs.getBigDecimal("CGST"));
				due.setSGST(rs.getBigDecimal("SGST"));
				due.setUGST(rs.getBigDecimal("UGST"));
				due.setIGST(rs.getBigDecimal("IGST"));
				due.setCESS(rs.getBigDecimal("CESS"));
				due.setTotalGST(rs.getBigDecimal("TotalGST"));

				return due;
			}, adviseId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<CrossLoanTransfer> getCrossLoanDetail(String finReference, boolean fromRef) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" clt.ID, clt.CustId, clt.ReceiptId, clt.FromFinID, clt.ToFinID, clt.FromFinReference");
		sql.append(", clt.ToFinReference, clt.TransferAmount, clt.ExcessId, clt.ToLinkedTranId");
		sql.append(", clt.FromLinkedTranId, clt.ExcessAmount, clt.UtiliseAmount, clt.ReserveAmount");
		sql.append(", clt.AvailableAmount, clt.ToExcessId, clt.ExcessType, frh.ValueDate");
		sql.append(" from Cross_Loan_Transfer clt");
		sql.append(" Inner Join FinReceiptHeader frh on frh.ReceiptID = clt.ReceiptId");

		if (fromRef) {
			sql.append("  where FromFinReference = ?");
		} else {
			sql.append("  where ToFinReference = ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, finReference);
		}, (rs, i) -> {
			CrossLoanTransfer clt = new CrossLoanTransfer();

			clt.setId(rs.getLong("ID"));
			clt.setCustId(rs.getLong("CustId"));
			clt.setReceiptId(rs.getLong("ReceiptId"));
			clt.setFromFinID(rs.getLong("FromFinID"));
			clt.setToFinID(rs.getLong("ToFinID"));
			clt.setFromFinReference(rs.getString("FromFinReference"));
			clt.setToFinReference(rs.getString("ToFinReference"));
			clt.setTransferAmount(rs.getBigDecimal("TransferAmount"));
			clt.setExcessId(rs.getLong("ExcessId"));
			clt.setToLinkedTranId(rs.getLong("ToLinkedTranId"));
			clt.setFromLinkedTranId(rs.getLong("FromLinkedTranId"));

			clt.setExcessAmount(rs.getBigDecimal("ExcessAmount"));
			clt.setUtiliseAmount(rs.getBigDecimal("UtiliseAmount"));
			clt.setReserveAmount(rs.getBigDecimal("ReserveAmount"));
			clt.setAvailableAmount(rs.getBigDecimal("AvailableAmount"));
			clt.setToExcessId(rs.getLong("ToExcessId"));
			clt.setExcessType(rs.getString("ExcessType"));
			clt.setValueDate(JdbcUtil.getDate(rs.getDate("ValueDate")));
			/*
			 * clt.setReceiptAmount(rs.getBigDecimal("ReceiptAmount"));
			 * clt.setTransactionRef(rs.getString("TransactionRef"));
			 * clt.setToLinkedTranId(rs.getLong("ToLinkedTranId"));
			 * clt.setFromLinkedTranId(rs.getLong("FromLinkedTranId"));
			 */

			return clt;
		});
	}
}