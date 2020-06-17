package com.pennant.datamigration.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.datamigration.dao.SourceDataSummaryDAO;
import com.pennant.datamigration.model.SourceDataSummary;
import com.pennant.datamigration.model.SourceStatus;
import com.pennant.datamigration.model.StatusCount;
import com.pennanttech.pennapps.core.ConcurrencyException;

public class SourceDataSummaryDAOImpl implements SourceDataSummaryDAO {
   private static Logger logger = Logger.getLogger(SourceDataSummaryDAOImpl.class);
   private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

   public void setDataSource(DataSource dataSource) {
      this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
   }

   public void saveSummary(SourceDataSummary dataSummary) {
      logger.debug("Entering");
      StringBuilder sql = new StringBuilder("insert into dm_sourcedatasummary ");
      sql.append(" (fm_FinReference, fm_CustID, fm_FinBranch, fm_NumberOfTerms, fm_RepayProfitRate, fm_TotalGrossPft,");
      sql.append(" fm_TotalRepayAmt, fm_FirstRepay, fm_LastRepay, fm_FinStartDate, fm_FinAmount, fm_FinRepaymentAmount,");
      sql.append(" fm_FeeChargeAmt, fm_JointAc, fm_JointCustID, fm_MandateID, fm_AlowBPI, fm_BPITreatment,");
      sql.append(" fm_AlwMultiDisb, fm_BPIAmount, fm_DeductFeeDisb, fdd_TotDisbCount, fdd_TotDisbAmount,");
      sql.append(" fdd_TotFeeChargeAmt, fap_TotInstructions, fap_TotInstructAmount, fsd_ProfitSchd,");
      sql.append(" fsd_PrincipalSchd, fsd_RepayAmount, fsd_SchdPftPaid, fsd_SchdPriPaid, rch_TotalReceiptAmount,");
      sql.append(" rch_TotalWaivedAmount, rch_TotalFeeAmount, rcd_ReceiptIDFound, rcd_TotalReceiptAmount,");
      sql.append(" rad_TOTPAID_NIA, rad_TOTPAID_PRIN, rad_TOTPAID_CHDIS, rad_TOTPAID_INT, rad_TOTPAID_TPF,");
      sql.append(" rad_TOTPAID_IT, rad_TOTPAID_PF, rad_TOTPAID_FRE, rad_TOTPAID_DOCC, rad_TOTPAID_APF,");
      sql.append(" rad_TOTPAID_GINS, rad_TOTPAID_EXS  , rad_TOTPAID_PAC, rad_TOTPAID_INS, rad_TOTPAID_RI,");
      sql.append(" rad_TOTPAID_CS, rad_TOTPAID_CRS, rad_TOTPAID_INST, rad_TOTPAID_ADMIN, rad_TOTPAID_SC,");
      sql.append(" rad_TOTPAID_ODC, rph_TotalRepayAmt, rph_TotalPriAmount, rph_TotalPftAmount, rph_ReceiptSeqIDFound,");
      sql.append(" rph_RepayIDFound, rpsd_ProfitSchd, rpsd_ProfitSchdPaid, rpsd_PrincipalSchd, rpsd_PrincipalSchdPaid,");
      sql.append(" pd_PresentmentIDFound, pd_MandateIDFound, pd_TotalSchAmtDue, pd_TotalPresentmentAmt, pd_ReceiptIDFound,");
      sql.append(" ma_FeeTypeIDFound, ma_TotalAdviseAmount, ma_TotalPaidAmount, ma_ReceiptIDFound,");
      sql.append(" mam_AdviseIDFound, mam_TotalMovementAmount, mam_TotalPaidAmount, mam_ReceiptIDFound,");
      sql.append(" fod_TotalODAmount, fod_TotalODPrincipal, fod_TotalODProfit, fod_TotalPenaltyAmount,");
      sql.append(" fod_TotalPenaltyPaid, fod_TotalPenaltyBal, prv_DueDays, prv_DPDBucketID,");
      sql.append(" prv_NPABucketID, prv_PrincipalDue, prv_ProfitDue, prv_ProvisionAmtCal, ffd_FeeTypeIDFound,");
      sql.append(" ffd_TotalDisbCalFee, ffd_TotalActualFee, ffd_TotalPaidFee, fea_Amount, fea_UtilizedAmount,");
      sql.append(" fea_BalanceAmount, errors, warnings, Information, fm_Cpz, fm_CpzAmount, ");
      sql.append(" fm_FinAssetValue, fm_FinCurrAssetValue, fdd_FirstDisbAmount)");
      sql.append(" Values(:fm_FinReference, :fm_CustID, :fm_FinBranch, :fm_NumberOfTerms, :fm_RepayProfitRate, :fm_TotalGrossPft,");
      sql.append(" :fm_TotalRepayAmt, :fm_FirstRepay, :fm_LastRepay, :fm_FinStartDate, :fm_FinAmount, :fm_FinRepaymentAmount,");
      sql.append(" :fm_FeeChargeAmt, :fm_JointAc, :fm_JointCustID, :fm_MandateID, :fm_AlowBPI, :fm_BPITreatment,");
      sql.append(" :fm_AlwMultiDisb, :fm_BPIAmount, :fm_DeductFeeDisb, :fdd_TotDisbCount, :fdd_TotDisbAmount,");
      sql.append(" :fdd_TotFeeChargeAmt, :fap_TotInstructions, :fap_TotInstructAmount, :fsd_ProfitSchd,");
      sql.append(" :fsd_PrincipalSchd, :fsd_RepayAmount, :fsd_SchdPftPaid, :fsd_SchdPriPaid, :rch_TotalReceiptAmount,");
      sql.append(" :rch_TotalWaivedAmount, :rch_TotalFeeAmount, :rcd_ReceiptIDFound, :rcd_TotalReceiptAmount,");
      sql.append(" :rad_TOTPAID_NIA, :rad_TOTPAID_PRIN, :rad_TOTPAID_CHDIS, :rad_TOTPAID_INT, :rad_TOTPAID_TPF,");
      sql.append(" :rad_TOTPAID_IT, :rad_TOTPAID_PF, :rad_TOTPAID_FRE, :rad_TOTPAID_DOCC, :rad_TOTPAID_APF,");
      sql.append(" :rad_TOTPAID_GINS, :rad_TOTPAID_EXS  , :rad_TOTPAID_PAC, :rad_TOTPAID_INS, :rad_TOTPAID_RI,");
      sql.append(" :rad_TOTPAID_CS, :rad_TOTPAID_CRS, :rad_TOTPAID_INST, :rad_TOTPAID_ADMIN, :rad_TOTPAID_SC,");
      sql.append(" :rad_TOTPAID_ODC, :rph_TotalRepayAmt, :rph_TotalPriAmount, :rph_TotalPftAmount, :rph_ReceiptSeqIDFound,");
      sql.append(" :rph_RepayIDFound, :rpsd_ProfitSchd, :rpsd_ProfitSchdPaid, :rpsd_PrincipalSchd, :rpsd_PrincipalSchdPaid,");
      sql.append(" :pd_PresentmentIDFound, :pd_MandateIDFound, :pd_TotalSchAmtDue, :pd_TotalPresentmentAmt, :pd_ReceiptIDFound,");
      sql.append(" :ma_FeeTypeIDFound, :ma_TotalAdviseAmount, :ma_TotalPaidAmount, :ma_ReceiptIDFound,");
      sql.append(" :mam_AdviseIDFound, :mam_TotalMovementAmount, :mam_TotalPaidAmount, :mam_ReceiptIDFound,");
      sql.append(" :fod_TotalODAmount, :fod_TotalODPrincipal, :fod_TotalODProfit, :fod_TotalPenaltyAmount,");
      sql.append(" :fod_TotalPenaltyPaid, :fod_TotalPenaltyBal, :prv_DueDays, :prv_DPDBucketID,");
      sql.append(" :prv_NPABucketID, :prv_PrincipalDue, :prv_ProfitDue, :prv_ProvisionAmtCal, :ffd_FeeTypeIDFound,");
      sql.append(" :ffd_TotalDisbCalFee, :ffd_TotalActualFee, :ffd_TotalPaidFee, :fea_Amount, :fea_UtilizedAmount,");
      sql.append(" :fea_BalanceAmount, :errors, :warnings, :Information, :fm_Cpz, :fm_CpzAmount, ");
      sql.append(" :fm_FinAssetValue, :fm_FinCurrAssetValue, :fdd_FirstDisbAmount)");
      logger.trace("SQL: " + sql.toString());
      BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(dataSummary);

      try {
         this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
      } catch (DuplicateKeyException var5) {
         throw new ConcurrencyException(var5);
      }

      logger.debug("Leaving");
   }

   public void deleteSummary(SourceDataSummary dataSummary) {
      logger.debug("Entering");
      StringBuilder sql = new StringBuilder("delete from dm_sourcedatasummary");
      logger.trace("SQL: " + sql.toString());
      SqlParameterSource paramSource = new BeanPropertySqlParameterSource(dataSummary);
      boolean var4 = false;

      try {
         int var7 = this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
      } catch (DataAccessException var6) {
      }

      logger.debug("Leaving");
   }

   public List<SourceStatus> getSummaryStatus() {
      logger.debug("Entering");
      new ArrayList();
      StringBuilder selectSql = new StringBuilder("select FM_FINREFERENCE FINREFERENCE, ERRORS, WARNINGS, INFORMATION from DM_SOURCEDATASUMMARY");
      selectSql.append(" WHERE (COALESCE(ERRORS, 'X') <> 'X' OR COALESCE(WARNINGS, 'X') <> 'X' OR COALESCE(INFORMATION, 'X') <> 'X')");
      logger.debug("selectSql: " + selectSql.toString());
      ParameterizedBeanPropertyRowMapper typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SourceStatus.class);

      List statusData;
      try {
         statusData = this.namedParameterJdbcTemplate.query(selectSql.toString(), typeRowMapper);
      } catch (EmptyResultDataAccessException var5) {
         logger.warn("Exception: ", var5);
         statusData = null;
      }

      logger.debug("Leaving");
      return statusData;
   }

   public void saveStatusCount(List<StatusCount> statusCount) {
      logger.debug("Entering");
      StringBuilder sql = new StringBuilder("insert into StatusCount ");
      sql.append(" (Code, Status, Count)");
      sql.append(" Values (:Code, :Status, :Count)");
      logger.trace("SQL: " + sql.toString());
      SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(statusCount.toArray());
      this.namedParameterJdbcTemplate.batchUpdate(sql.toString(), beanParameters);
      logger.debug("Leaving");
   }
}