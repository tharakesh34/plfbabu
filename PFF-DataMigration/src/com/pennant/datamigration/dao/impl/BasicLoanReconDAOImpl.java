package com.pennant.datamigration.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.datamigration.dao.BasicLoanReconDAO;
import com.pennant.datamigration.model.BasicLoanRecon;
import com.pennant.datamigration.model.FeeTypeVsGLMapping;
import com.pennant.datamigration.model.SourceDataSummary;
import com.pennant.datamigration.model.SourceReport;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.resource.Literal;

public class BasicLoanReconDAOImpl implements BasicLoanReconDAO {
	private static Logger logger = Logger.getLogger(BasicLoanReconDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public BasicLoanReconDAOImpl() {
		super();
	}

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	public void saveRecon(BasicLoanRecon basicLoanRecon) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into dm_basicloanrecon ");

		sql.append(" (FinReference, Branch, Fintype, CustID, GraceExist, BpiExist,");
		sql.append(" SrcSanctionedAmount, SrcDisbursedAmount, SrcUnDisbursedAmount, ");
		sql.append(" SanctionedAmount, DisbursedAmount, UnDisbursedAmount, SrcEMISchd, PlfEMISchd,");
		sql.append(" DifEMISchd, SrcIntSchd, PlfIntSchd, DifIntSchd, SrcPriSchd, PlfPriSchd,");
		sql.append(" DifPriSchd, SrcEMIReceived, PlfEMIReceived, DifEMIReceived, SrcIntReceived,");
		sql.append(" PlfIntReceived, DifIntReceived, SrcPriReceived, PlfPriReceived, DifPriReceived, SrcEMIPastDue,");
		sql.append("  PlfEMIPastDue, DifEMIPastDue, SrcIntPastDue, PlfIntPastDue, DifIntPastDue, SrcPriPastDue,");
		sql.append("  PlfPriPastDue, DifPriPastDue, SrcActiveLPPDue, PlfActiveLPPDue, DifActiveLPPDue,");
		sql.append("  Errors, Warnings, Information)");

		sql.append(" Values(:FinReference, :Branch, :Fintype, :CustID, :GraceExist, :BpiExist,");
		sql.append(" :SrcSanctionedAmount, :SrcDisbursedAmount, :SrcUnDisbursedAmount, ");
		sql.append(" :SanctionedAmount, :DisbursedAmount, :UnDisbursedAmount, :SrcEMISchd, :PlfEMISchd,");
		sql.append(" :DifEMISchd, :SrcIntSchd, :PlfIntSchd, :DifIntSchd, :SrcPriSchd, :PlfPriSchd,");
		sql.append(" :DifPriSchd, :SrcEMIReceived, :PlfEMIReceived, :DifEMIReceived, :SrcIntReceived,");
		sql.append(
				" :PlfIntReceived, :DifIntReceived, :SrcPriReceived, :PlfPriReceived, :DifPriReceived, :SrcEMIPastDue,");
		sql.append(" :PlfEMIPastDue, :DifEMIPastDue, :SrcIntPastDue, :PlfIntPastDue, :DifIntPastDue, :SrcPriPastDue,");
		sql.append(" :PlfPriPastDue, :DifPriPastDue, :SrcActiveLPPDue, :PlfActiveLPPDue, :DifActiveLPPDue, ");
		sql.append("  :Errors, :Warnings, :Information)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(basicLoanRecon);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void cleanDestination() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		int recordCount = 0;

		// Delete from Basic Loan Recon
		sql = new StringBuilder("delete from dm_basicloanrecon");
		BasicLoanRecon blr = new BasicLoanRecon();
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(blr);
		recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		//Delete from Source Summary
		sql = new StringBuilder("delete from dm_sourcedatasummary");
		SourceDataSummary sds = new SourceDataSummary();
		paramSource = new BeanPropertySqlParameterSource(sds);
		recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		//Delete from Finance Main
		sql = new StringBuilder("delete from financemain");
		FinanceMain fm = new FinanceMain();
		paramSource = new BeanPropertySqlParameterSource(fm);
		recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		//Delete from Disbursement Details
		sql = new StringBuilder("delete from findisbursementdetails");
		FinanceDisbursement fdd = new FinanceDisbursement();
		paramSource = new BeanPropertySqlParameterSource(fdd);
		recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		//Delete from disbursement instructions
		sql = new StringBuilder("delete from finadvancepayments");
		FinAdvancePayments fap = new FinAdvancePayments();
		paramSource = new BeanPropertySqlParameterSource(fap);
		recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		//Delete from disbursement instructions
		sql = new StringBuilder("delete from finscheduledetails");
		FinanceScheduleDetail fsd = new FinanceScheduleDetail();
		paramSource = new BeanPropertySqlParameterSource(fsd);
		recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		//Delete from FinFeeDetails
		sql = new StringBuilder("delete from finfeedetail");
		FinFeeDetail ffd = new FinFeeDetail();
		paramSource = new BeanPropertySqlParameterSource(ffd);
		recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		//Delete from Repay Instructions
		sql = new StringBuilder("delete from finrepayinstruction");
		RepayInstruction ri = new RepayInstruction();
		paramSource = new BeanPropertySqlParameterSource(ri);
		recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		//Delete from Receipt Header
		sql = new StringBuilder("delete from finreceiptheader");
		FinReceiptHeader rch = new FinReceiptHeader();
		paramSource = new BeanPropertySqlParameterSource(rch);
		recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		//Delete from Receipt Details
		sql = new StringBuilder("delete from finreceiptdetail");
		FinReceiptDetail rcd = new FinReceiptDetail();
		paramSource = new BeanPropertySqlParameterSource(rcd);
		recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		//Delete from Allocation Details
		sql = new StringBuilder("delete from receiptallocationdetail");
		ReceiptAllocationDetail rad = new ReceiptAllocationDetail();
		paramSource = new BeanPropertySqlParameterSource(rad);
		recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		//Delete from Fin Excess Amounts
		sql = new StringBuilder("delete from finexcessamount");
		FinExcessAmount fea = new FinExcessAmount();
		paramSource = new BeanPropertySqlParameterSource(fea);
		recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		//Delete from Fin Excess Movements
		sql = new StringBuilder("delete from finexcessmovement");
		FinExcessMovement fem = new FinExcessMovement();
		paramSource = new BeanPropertySqlParameterSource(fem);
		recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		//Delete from Fin Repay Header
		sql = new StringBuilder("delete from finrepayheader");
		FinRepayHeader rph = new FinRepayHeader();
		paramSource = new BeanPropertySqlParameterSource(rph);
		recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		//Delete from Fin Repay Schedule Details
		sql = new StringBuilder("delete from finrepayscheduledetail");
		RepayScheduleDetail rsd = new RepayScheduleDetail();
		paramSource = new BeanPropertySqlParameterSource(rsd);
		recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		//Delete from Fin Repay Deatils
		sql = new StringBuilder("delete from finrepaydetails");
		FinanceRepayments frp = new FinanceRepayments();
		paramSource = new BeanPropertySqlParameterSource(frp);
		recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		//Delete from Presentment Details
		sql = new StringBuilder("delete from presentmentdetails");
		PresentmentDetail prd = new PresentmentDetail();
		paramSource = new BeanPropertySqlParameterSource(prd);
		recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		//Delete from Service Instructions
		sql = new StringBuilder("delete from finserviceinstruction");
		FinServiceInstruction fsi = new FinServiceInstruction();
		paramSource = new BeanPropertySqlParameterSource(fsi);
		recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		//Delete from Manual Advises
		sql = new StringBuilder("delete from ManualAdvise");
		ManualAdvise ma = new ManualAdvise();
		paramSource = new BeanPropertySqlParameterSource(ma);
		recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		//Delete from Manual Advises
		sql = new StringBuilder("delete from ManualAdviseMovements");
		ManualAdviseMovements mam = new ManualAdviseMovements();
		paramSource = new BeanPropertySqlParameterSource(mam);
		recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		//Delete from Fin OD Details
		sql = new StringBuilder("delete from finoddetails");
		FinODDetails fod = new FinODDetails();
		paramSource = new BeanPropertySqlParameterSource(fod);
		recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		//Delete from Fin OD Penalty Rates
		sql = new StringBuilder("delete from finodpenaltyrates");
		FinODPenaltyRate frat = new FinODPenaltyRate();
		paramSource = new BeanPropertySqlParameterSource(frat);
		recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		//Delete from Fin Provisions
		sql = new StringBuilder("delete from finprovisions");
		Provision pro = new Provision();
		paramSource = new BeanPropertySqlParameterSource(pro);
		recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		//Delete from FinPftDeatils
		sql = new StringBuilder("delete from finPftDetails");
		FinanceProfitDetail fpd = new FinanceProfitDetail();
		paramSource = new BeanPropertySqlParameterSource(fpd);
		recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		//Delete from Postings
		sql = new StringBuilder("delete from postings");
		ReturnDataSet rds = new ReturnDataSet();
		paramSource = new BeanPropertySqlParameterSource(rds);
		recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		logger.debug(Literal.LEAVING);
	}

	public List<FinanceType> getDMFinTypes(String type) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuilder selectSql = new StringBuilder(" select  * from rmtfinancetypes ");
		selectSql.append(" where fintype in (Select distinct fintype from financemain");
		selectSql.append(type.trim());
		selectSql.append(")");

		RowMapper<FinanceType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceType.class);

		List<FinanceType> finTypeList = this.namedParameterJdbcTemplate.query(selectSql.toString(), source,
				typeRowMapper);

		logger.debug("Leaving");
		return finTypeList;
	}

	public SourceReport getSourceReportDetails(String lnno) {
		logger.debug("Entering");

		SourceReport esr = new SourceReport();
		esr.setLnno(lnno);

		StringBuilder selectSql = new StringBuilder("select * from Source_Report");
		selectSql.append(" Where Lnno =:Lnno");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(esr);
		RowMapper<SourceReport> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SourceReport.class);

		try {
			esr = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			esr = null;
		}
		logger.debug("Leaving");
		return esr;
	}

	public List<FeeTypeVsGLMapping> getFeeTypeVsGLMappings() {
		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuilder selectSql = new StringBuilder(" select  * from DM_FeeTypes ");
		selectSql.append(" order by feetypeid");

		RowMapper<FeeTypeVsGLMapping> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FeeTypeVsGLMapping.class);

		List<FeeTypeVsGLMapping> feeTypeVsGLMapping = this.namedParameterJdbcTemplate.query(selectSql.toString(),
				source, typeRowMapper);

		return feeTypeVsGLMapping;
	}

}
