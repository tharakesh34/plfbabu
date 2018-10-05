package com.pennant.datamigration.dao.impl;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.datamigration.dao.SourceDataSummaryDAO;
import com.pennant.datamigration.model.SourceDataSummary;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;

public class SourceDataSummaryDAOImpl implements SourceDataSummaryDAO {
	private static Logger				logger	= Logger.getLogger(SourceDataSummaryDAOImpl.class);

	// Spring Named JDBC Template
		private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

		public SourceDataSummaryDAOImpl() {
			super();
		}

		public void setDataSource(DataSource dataSource) {
			this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		}

		public void saveSummary(SourceDataSummary dataSummary) {
			logger.debug(Literal.ENTERING);

			// Prepare the SQL.
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
			                    
			
			// Execute the SQL, binding the arguments.
			logger.trace(Literal.SQL + sql.toString());
			SqlParameterSource paramSource = new BeanPropertySqlParameterSource(dataSummary);

			try {
				namedParameterJdbcTemplate.update(sql.toString(), paramSource);
			} catch (DuplicateKeyException e) {
				throw new ConcurrencyException(e);
			}

			logger.debug(Literal.LEAVING);
		}

		public void deleteSummary(SourceDataSummary dataSummary) {
			// TODO Auto-generated method stub
			logger.debug(Literal.ENTERING);

			// Prepare the SQL.
			StringBuilder sql = new StringBuilder("delete from dm_sourcedatasummary");

			// Execute the SQL, binding the arguments.
			logger.trace(Literal.SQL + sql.toString());
			SqlParameterSource paramSource = new BeanPropertySqlParameterSource(dataSummary);
			int recordCount = 0;

			try {
				recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);
			} catch (DataAccessException e) {
				//TODO
			}

			logger.debug(Literal.LEAVING);
		}


}
