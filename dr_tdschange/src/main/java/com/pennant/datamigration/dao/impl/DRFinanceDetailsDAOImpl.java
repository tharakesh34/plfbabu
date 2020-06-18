package com.pennant.datamigration.dao.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.datamigration.dao.DRFinanceDetailsDAO;
import com.pennant.datamigration.model.BlockedFinance;
import com.pennant.datamigration.model.DREMIHoliday;
import com.pennant.datamigration.model.DRFinanceDetails;
import com.pennant.datamigration.model.DRRateReviewCases;
import com.pennant.datamigration.model.DRRateReviewScheduleChange;
import com.pennant.datamigration.model.DRTDSChange;
import com.pennant.datamigration.model.ExcessCorrections;
import com.pennant.datamigration.model.ScheduleDiff;
import com.pennant.datamigration.model.scheduleIssue;
import com.pennanttech.pennapps.core.jdbc.BasicDao;

public class DRFinanceDetailsDAOImpl extends BasicDao<DRFinanceDetails> implements DRFinanceDetailsDAO {
	private static Logger logger = Logger.getLogger(DRFinanceDetailsDAOImpl.class);;

	public List<DRFinanceDetails> getDRFinanceReferenceList() {
		final DRFinanceDetails drFinanceDetails = new DRFinanceDetails();
		final StringBuilder sql = new StringBuilder(
				"SELECT finReference, reconStatus, category, logKey From DRFinanceDetails ");
		sql.append(" WHERE reconStatus = 0 ");
		sql.append(" order by execorder");
		final SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				drFinanceDetails);
		final RowMapper<DRFinanceDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(DRFinanceDetails.class);
		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
	}

	public void saveDRFinanceReference(final DRFinanceDetails drFinance) {
		int recordCount = 0;
		logger.debug("Entering");
		final StringBuilder updateSql = new StringBuilder("Update ");
		updateSql.append(" DRFINANCEDETAILS");
		updateSql.append(" Set Status = :Status,  ReconStatus = :ReconStatus, Remarks=:Remarks, ");
		updateSql.append(" UpdRch = :UpdRch, UpdRcd = :UpdRcd, UpdRph = :UpdRph, UpdRpd = :UpdRpd,");
		updateSql.append(" UpdRsd = :UpdRsd, UpdFsd = :UpdFsd, UpdRad = :UpdRad, DltRch = :DltRch,");
		updateSql.append(" DltRcd = :DltRcd, DltRph = :DltRph, DltRpd = :DltRpd, DltRsd = :DltRsd,");
		updateSql.append(" DltRad = :DltRad, NewRph = :NewRph, NewRpd = :NewRpd, NewRsd = :NewRsd,");
		updateSql.append(" NewRad = :NewRad, UpdFea = :UpdFea, NewFea = :NewFea, DltFea = :DltFea, UpdPmd = :UpdPmd ");
		updateSql.append(" Where FinReference =:FinReference");
		logger.debug(("updateSql: " + updateSql.toString()));
		final SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(drFinance);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	public void prepareHC41() {
		StringBuilder sql = new StringBuilder();
		sql = new StringBuilder("TRUNCATE TABLE EXCESSCORRECTIONS");
		final DRFinanceDetails drFinance = new DRFinanceDetails();
		final SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(drFinance);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
		sql = new StringBuilder("INSERT INTO EXCESSCORRECTIONS (FINREFERENCE) ");
		sql.append(" SELECT FINREFERENCE FROM DRFINANCEDETAILS WHERE CATEGORY='HC41'");
		this.jdbcTemplate.update(sql.toString(), beanParameters);
		sql = new StringBuilder("MERGE INTO EXCESSCORRECTIONS EC ");
		sql.append(" USING ( SELECT FINREFERENCE, ACCOUNTTYPE, ");
		sql.append(" SUM(CASE WHEN DRORCR = 'D' THEN POSTAMOUNT*(-1) ELSE POSTAMOUNT END) POSTAMOUNT ");
		sql.append(" FROM POSTINGS WHERE ACCOUNTTYPE = 'XCESS'");
		sql.append(" GROUP BY FINREFERENCE, ACCOUNTTYPE) T2 ");
		sql.append(" ON (T2.FINREFERENCE = EC.FINREFERENCE) ");
		sql.append(" WHEN MATCHED THEN UPDATE SET EC.OLDGLBAL = T2.POSTAMOUNT");
		this.jdbcTemplate.update(sql.toString(), beanParameters);
		sql = new StringBuilder("UPDATE EXCESSCORRECTIONS SET ");
		sql.append(" OLDGLBAL = 0 WHERE OLDGLBAL IS NULL");
		this.jdbcTemplate.update(sql.toString(), beanParameters);
	}

	public List<DRRateReviewCases> getDRRateReviewCases() {
		final DRRateReviewCases drRateReviewCases = new DRRateReviewCases();
		final StringBuilder sql = new StringBuilder("SELECT * From RR_SCHD_ISSUE");
		final SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				drRateReviewCases);
		final RowMapper<DRRateReviewCases> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(DRRateReviewCases.class);
		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
	}

	public void saveDRRateReviewStatus(final DRRateReviewScheduleChange drRRS) {
		final StringBuilder insertSql = new StringBuilder("Insert Into");
		insertSql.append(" DRRATRVWSCHDCHG");
		insertSql.append(" (FinReference, RvwDate, RecalDate, OldRate, NewRate, OldEMI, NewEMI, ");
		insertSql.append(" OldLastEMI, NewLastEMI, DiffEMI, DiffLastEMI, ");
		insertSql.append(" OldInterest, NewInterest, DiffInterest, ");
		insertSql.append(" DiffMaxExpected, Category, ReconSts, Remarks, NoOfTerms, Flexi)");
		insertSql.append(" Values");
		insertSql.append(" (:FinReference, :RvwDate, :RecalDate, :OldRate, :NewRate, :OldEMI, :NewEMI,");
		insertSql.append(" :OldLastEMI, :NewLastEMI, :DiffEMI, :DiffLastEMI,");
		insertSql.append(" :OldInterest, :NewInterest, :DiffInterest, ");
		insertSql.append(" :DiffMaxExpected, :Category, :ReconSts, :Remarks, :NoOfTerms, :Flexi)");
		final SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(drRRS);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
	}

	public void addBlockedFinance(final BlockedFinance blockedFinance) {
		final StringBuilder insertSql = new StringBuilder("Insert Into");
		insertSql.append(" BlockedFinances");
		insertSql.append(" (FinReference, BlockedDate, Remarks) ");
		insertSql.append(" Values");
		insertSql.append(" (:FinReference, :BlockedDate, :Remarks) ");
		final SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				blockedFinance);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
	}

	public void removeBlockedFinance(final BlockedFinance blockedFinance) {
		final StringBuilder insertSql = new StringBuilder("Delete From");
		insertSql.append(" BlockedFinances");
		insertSql.append(" Where FinReference = :FinReference");
		final SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				blockedFinance);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
	}

	public void updateScheduleIssue(final scheduleIssue scheduleIssue) {
		final StringBuilder insertSql = new StringBuilder("Insert Into");
		insertSql.append(" ScheduleIssues");
		insertSql.append(" (FinReference, schDate, fieldName, oldAmount, newAmount) ");
		insertSql.append(" Values");
		insertSql.append(" (:FinReference, :schDate, :fieldName, :oldAmount, :newAmount) ");
		final SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				scheduleIssue);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
	}

	public void updateScheduleDiff(final ScheduleDiff scheduleDiff) {
		final StringBuilder insertSql = new StringBuilder("Insert Into");
		insertSql.append(" ScheduleDiff");
		insertSql.append(" (FINREFERENCE, SCHDATE, DISBAMOUNT, BALANCEFORPFTCAL, PROFITCALC, PROFITSCHD, ");
		insertSql.append(" PRINCIPALSCHD, REPAYAMOUNT, PROFITBALANCE, FEECHARGEAMT, CPZAMOUNT, ");
		insertSql.append(" CLOSINGBALANCE, SCHDPFTPAID, SCHDPRIPAID, TDSAMOUNT, TDSPAID, ");
		insertSql.append(" PARTIALPAIDAMT, LIMITDROP, ODLIMIT, AVAILABLELIMIT, NEW_DISBAMOUNT,");
		insertSql.append(" NEW_BALANCEFORPFTCAL, NEW_PROFITCALC, NEW_PROFITSCHD, NEW_PRINCIPALSCHD,");
		insertSql.append(" NEW_REPAYAMOUNT, NEW_PROFITBALANCE, NEW_FEECHARGEAMT, NEW_CPZAMOUNT, NEW_CLOSINGBALANCE, ");
		insertSql.append(" NEW_SCHDPFTPAID, NEW_SCHDPRIPAID, NEW_TDSAMOUNT, NEW_TDSPAID, NEW_PARTIALPAIDAMT, ");
		insertSql.append(" NEW_LIMITDROP, NEW_ODLIMIT, NEW_AVAILABLELIMIT, BpiOrHoliday, New_BpiOrHoliday)");
		insertSql.append(" Values");
		insertSql.append(" (:finReference, :schDate, :disbAmount, :balanceForPftCal, :profitCalc, :profitSchd,");
		insertSql.append(" :principalSchd, :repayAmount, :profitBalance, :feeChargeAmt, :cpzAmount, ");
		insertSql.append(" :closingBalance, :schdPftPaid, :schdPriPaid, :tDSAmount, :tDSPaid, ");
		insertSql.append(" :partialPaidAmt, :limitDrop, :oDLimit, :availableLimit, :new_DisbAmount,");
		insertSql.append(" :new_BalanceForPftCal, :new_ProfitCalc, :new_ProfitSchd, :new_PrincipalSchd, ");
		insertSql.append(
				" :new_RepayAmount, :new_ProfitBalance, :new_FeeChargeAmt, :new_CpzAmount, :new_ClosingBalance,");
		insertSql.append(" :new_SchdPftPaid, :new_SchdPriPaid, :new_TDSAmount, :new_TDSPaid, :new_PartialPaidAmt, ");
		insertSql.append(" :new_LimitDrop, :new_ODLimit, :new_AvailableLimit, :BpiOrHoliday, :New_BpiOrHoliday)");
		final SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scheduleDiff);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
	}

	public void saveExcessCorrection(final ExcessCorrections exCorrection) {
		final StringBuilder insertSql = new StringBuilder("Insert Into");
		insertSql.append(" EXCESSCORRECTIONS");
		insertSql.append(" (FinReference, ExcessID, OldExcessAmount, OldExcessUtilized, OldExcessBalance, ");
		insertSql.append(" NewExcessAmount, NewExcessUtilized, NewExcessBalance, OldRPHExcess, ");
		insertSql.append(" FeeExcess, OldGLBal, Status, Category)");
		insertSql.append(" Values");
		insertSql.append(" (:FinReference, :ExcessID, :OldExcessAmount, :OldExcessUtilized, :OldExcessBalance, ");
		insertSql.append(" :NewExcessAmount, :NewExcessUtilized, :NewExcessBalance, :OldRPHExcess, ");
		insertSql.append(" :FeeExcess, :OldGLBal, :Status, :Category)");
		final SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(exCorrection);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
	}

	public void updateExcessCorrection(final ExcessCorrections exCorrection) {
		final StringBuilder updateSql = new StringBuilder("UPDATE EXCESSCORRECTIONS SET ");
		updateSql.append(" ExcessID=:ExcessID, OldExcessAmount=:OldExcessAmount, ");
		updateSql.append(" OldExcessUtilized=:OldExcessUtilized, OldExcessBalance=:OldExcessBalance, ");
		updateSql.append(" OldExcessReserved=:OldExcessReserved, ");
		updateSql.append(" NewExcessAmount=:NewExcessAmount, NewExcessUtilized=:NewExcessUtilized,");
		updateSql.append(" NewExcessBalance=:NewExcessBalance, OldRPHExcess=:OldRPHExcess, ");
		updateSql.append(" FeeExcess=:FeeExcess, OldGLBal=:OldGLBal, Status=:Status, Category=:Category, ");
		updateSql.append(" PresentBounceExcess=:PresentBounceExcess, BpiExcess=:BpiExcess, ");
		updateSql.append(" DiffExcess=:DiffExcess, RefundExcess=:RefundExcess, FeaAdjust=:FeaAdjust, ");
		updateSql.append(" RphAdjust=:RphAdjust, GlAdjust=:GlAdjust, RadAdjust=:RadAdjust ");
		updateSql.append(" Where FinReference=:FinReference ");
		final SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(exCorrection);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);
	}

	public BigDecimal getOldGLBalance(final String finRef) {
		ExcessCorrections xc = new ExcessCorrections();
		xc.setFinReference(finRef);
		final StringBuilder selectSql = new StringBuilder(" Select OldGLBal ");
		selectSql.append(" From EXCESSCORRECTIONS ");
		selectSql.append(" Where FinReference =:FinReference");
		logger.debug(("selectSql: " + selectSql.toString()));
		final SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(xc);
		final RowMapper<ExcessCorrections> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ExcessCorrections.class);
		xc = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		return xc.getOldGLBal();
	}

	public List<DRFinanceDetails> getDRCorrectionDM(final String reasonCode) {
		final DRFinanceDetails drFin = new DRFinanceDetails();
		drFin.setCategory(reasonCode);
		final StringBuilder sql = new StringBuilder("SELECT Finreference From DR_CORRECTION_18MAR");
		sql.append("  Where DRREQUIRED = 1 And ReasonCode = :Category ");
		final SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(drFin);
		final RowMapper<DRFinanceDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(DRFinanceDetails.class);
		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
	}

	public void updateDMCorrectionSts(final DRFinanceDetails details) {
		final MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Remarks", details.getRemarks());
		source.addValue("Status", details.getReconStatus());
		source.addValue("FinRef", details.getFinReference());
		final StringBuilder updateSql = new StringBuilder("UPDATE DR_CORRECTION_18MAR SET ");
		updateSql.append(" REMARKS=:Remarks, CORRECTIONSTS=:Status ");
		updateSql.append(" Where FinReference=:FinRef ");
		this.jdbcTemplate.update(updateSql.toString(), source);
	}

	public List<DREMIHoliday> getDREMIHoliday() {
		StringBuilder sql = new StringBuilder("SELECT FINREFERENCE, EHSTARTDATE, EHENDDATE, EHINST, EHMETHOD ");
		sql.append("  From DR_EMIHOLIDAY WHERE EHSTATUS IS NULL ");

		RowMapper<DREMIHoliday> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DREMIHoliday.class);

		return this.jdbcTemplate.query(sql.toString(), new MapSqlParameterSource(), typeRowMapper);
	}

	public void updateDREMIHoliday(DREMIHoliday erEH) {
		final StringBuilder updateSql = new StringBuilder("UPDATE DR_EMIHOLIDAY SET ");
		updateSql.append(" AppDate=:AppDate, EHStatus=:EHStatus, EhStatusRemarks=:EhStatusRemarks,");
		updateSql.append(" BranchCode=:BranchCode, ProductType=:ProductType, FinType=:FinType,");
		updateSql.append(" OldBucket=:OldBucket, NewBucket=:NewBucket, Dpd=:Dpd, OldEMIOS=:OldEMIOS,");
		updateSql.append(" NewEMIOS=:NewEMIOS, OldBalTenure=:OldBalTenure, NewBalTenure=:NewBalTenure,");
		updateSql.append(" OldMaturity=:OldMaturity, NewMaturity=:NewMaturity, LastBilledDate=:LastBilledDate,");
		updateSql.append(" LastBilledInstNo=:LastBilledInstNo, ActLoanAmount=:ActLoanAmount, OldTenure=:OldTenure,");
		updateSql.append(" NewTenure=:NewTenure, OldInterest=:OldInterest, NewInterest=:NewInterest,");
		updateSql.append(" CpzInterest=:CpzInterest, BounceWaiver=:BounceWaiver, LastMntDate=:LastMntDate,");
		updateSql.append(" OldMaxUnPlannedEMI=:OldMaxUnPlannedEMI, NewMaxUnPlannedEMI=:NewMaxUnPlannedEMI,");
		updateSql.append(" OldAvailedUnPlanEMI=:OldAvailedUnPlanEMI, NewAvailedUnPlanEMI=:NewAvailedUnPlanEMI,");
		updateSql.append(" OldFinalEMI=:OldFinalEMI, NewFinalEMI=:NewFinalEMI");

		updateSql.append(" Where FinReference=:FinReference ");
		final SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(erEH);
		this.jdbcTemplate.update(updateSql.toString(), beanParameters);
	}
	
	// START - TDS Change
		/**
		 * 
		 * @return
		 */
		public List<DRTDSChange> getDRTDSChangeList() {

			DRTDSChange drEH = new DRTDSChange();
			StringBuilder sql = new StringBuilder(
					" SELECT FinReference, TDSStartDate FROM DR_TDSCHANGE WHERE STATUS IS NULL");

			SqlParameterSource beanParameters = (SqlParameterSource) new BeanPropertySqlParameterSource(drEH);
			RowMapper<DRTDSChange> typeRowMapper = (RowMapper<DRTDSChange>) BeanPropertyRowMapper
					.newInstance(DRTDSChange.class);

			return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
		}

		public void updateDRTDSChange(DRTDSChange drTDS) {
			final StringBuilder updateSql = new StringBuilder("UPDATE DR_TDSCHANGE SET ");

			updateSql.append(" AppDate = :AppDate, Status = :Status, Reason = :Reason,");
			updateSql.append(" TDSStartDate = :TDSStartDate, SchStartDate = :SchStartDate,");
			updateSql.append(" OldTDSAmt = :OldTDSAmt, NewTDSAmt = :NewTDSAmt, TDSChange = :TDSChange");

			updateSql.append(" Where FinReference = :FinReference ");
			final SqlParameterSource beanParameters = (SqlParameterSource) new BeanPropertySqlParameterSource(drTDS);
			this.jdbcTemplate.update(updateSql.toString(), beanParameters);
		}
		
		// END - TDS Change
}