package com.pennant.datamigration.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.applicationmaster.Assignment;
import com.pennant.backend.model.applicationmaster.BusinessVertical;
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
import com.pennant.datamigration.model.ClientRescheduleData;
import com.pennant.datamigration.model.CutOffDateSchedule;
import com.pennant.datamigration.model.DRCorrections;
import com.pennant.datamigration.model.FeeTypeVsGLMapping;
import com.pennant.datamigration.model.ScheduleRate;
import com.pennant.datamigration.model.SourceReport;
import com.pennant.datamigration.model.StatusCount;
import com.pennant.datamigration.model.TabAgreementDate;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;

public class BasicLoanReconDAOImpl implements BasicLoanReconDAO
{
    private static Logger logger;
    private DataSource dataSource;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    static {
        BasicLoanReconDAOImpl.logger = Logger.getLogger(BasicLoanReconDAOImpl.class);
    }
    
    public void setDataSource(final DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.dataSource = dataSource;
    }
    
    public void saveRecon(final BasicLoanRecon detail) {
        final StringBuilder sql = new StringBuilder("insert  into dm_basicloanrecon ");
        sql.append(" (FinReference, Branch, Fintype, CustID, GraceExist, BpiExist,");
        sql.append(" SrcSanctionedAmount, SrcDisbursedAmount, SrcUnDisbursedAmount, ");
        sql.append(" SanctionedAmount, DisbursedAmount, UnDisbursedAmount, SrcEMISchd, PlfEMISchd,");
        sql.append(" DifEMISchd, SrcIntSchd, PlfIntSchd, DifIntSchd, SrcPriSchd, PlfPriSchd, PlfCpz, ");
        sql.append(" DifPriSchd, SrcEMIReceived, PlfEMIReceived, DifEMIReceived, SrcIntReceived,");
        sql.append(" PlfIntReceived, DifIntReceived, SrcPriReceived, PlfPriReceived, DifPriReceived, SrcEMIPastDue,");
        sql.append("  PlfEMIPastDue, DifEMIPastDue, SrcIntPastDue, PlfIntPastDue, DifIntPastDue, SrcPriPastDue,");
        sql.append("  PlfPriPastDue, DifPriPastDue, SrcActiveLPPDue, PlfActiveLPPDue, DifActiveLPPDue,");
        sql.append("  Errors, Warnings, Information, FirstIntDiff, LastIntAdjusted, PastIntReset, ");
        sql.append("  PosDifference, ReconStatus, SrcDtaIntSchd, SrcDtaPriSchd, SrcDtaIntRcv, SrcDtaPriRcv, ");
        sql.append("  UcLoan, UcSchdBuild, InQDP, SrcDtaIntDue, SrcDtaPriDue)");
        sql.append(" Values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,");
        sql.append(" ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        Connection connection = null;
        PreparedStatement pst = null;
        try {
            connection = DataSourceUtils.getConnection(this.dataSource);
            pst = connection.prepareStatement(sql.toString());
            pst.setString(1, detail.getFinReference());
            pst.setString(2, detail.getBranch());
            pst.setString(3, detail.getFintype());
            pst.setLong(4, detail.getCustID());
            pst.setBoolean(5, detail.isGraceExist());
            pst.setBoolean(6, detail.isBpiExist());
            pst.setBigDecimal(7, detail.getSrcSanctionedAmount());
            pst.setBigDecimal(8, detail.getSrcDisbursedAmount());
            pst.setBigDecimal(9, detail.getSrcUnDisbursedAmount());
            pst.setBigDecimal(10, detail.getSanctionedAmount());
            pst.setBigDecimal(11, detail.getDisbursedAmount());
            pst.setBigDecimal(12, detail.getUnDisbursedAmount());
            pst.setBigDecimal(13, detail.getSrcEMISchd());
            pst.setBigDecimal(14, detail.getPlfEMISchd());
            pst.setBigDecimal(15, detail.getDifEMISchd());
            pst.setBigDecimal(16, detail.getSrcIntSchd());
            pst.setBigDecimal(17, detail.getPlfIntSchd());
            pst.setBigDecimal(18, detail.getDifIntSchd());
            pst.setBigDecimal(19, detail.getSrcPriSchd());
            pst.setBigDecimal(20, detail.getPlfPriSchd());
            pst.setBigDecimal(21, detail.getPlfCpz());
            pst.setBigDecimal(22, detail.getDifPriSchd());
            pst.setBigDecimal(23, detail.getSrcEMIReceived());
            pst.setBigDecimal(24, detail.getPlfEMIReceived());
            pst.setBigDecimal(25, detail.getDifEMIReceived());
            pst.setBigDecimal(26, detail.getSrcIntReceived());
            pst.setBigDecimal(27, detail.getPlfIntReceived());
            pst.setBigDecimal(28, detail.getDifIntReceived());
            pst.setBigDecimal(29, detail.getSrcPriReceived());
            pst.setBigDecimal(30, detail.getPlfPriReceived());
            pst.setBigDecimal(31, detail.getDifPriReceived());
            pst.setBigDecimal(32, detail.getSrcEMIPastDue());
            pst.setBigDecimal(33, detail.getPlfEMIPastDue());
            pst.setBigDecimal(34, detail.getDifEMIPastDue());
            pst.setBigDecimal(35, detail.getSrcIntPastDue());
            pst.setBigDecimal(36, detail.getPlfIntPastDue());
            pst.setBigDecimal(37, detail.getDifIntPastDue());
            pst.setBigDecimal(38, detail.getSrcPriPastDue());
            pst.setBigDecimal(39, detail.getPlfPriPastDue());
            pst.setBigDecimal(40, detail.getDifPriPastDue());
            pst.setBigDecimal(41, detail.getSrcActiveLPPDue());
            pst.setBigDecimal(42, detail.getPlfActiveLPPDue());
            pst.setBigDecimal(43, detail.getDifActiveLPPDue());
            pst.setString(44, detail.getErrors());
            pst.setString(45, detail.getWarnings());
            pst.setString(46, detail.getInformation());
            pst.setBigDecimal(47, detail.getFirstIntDiff());
            pst.setBigDecimal(48, detail.getLastIntAdjusted());
            pst.setBigDecimal(49, detail.getPastIntReset());
            pst.setBigDecimal(50, detail.getPosDifference());
            pst.setInt(51, detail.getReconStatus());
            pst.setBigDecimal(52, detail.getSrcDtaIntSchd());
            pst.setBigDecimal(53, detail.getSrcDtaPriSchd());
            pst.setBigDecimal(54, detail.getSrcDtaIntRcv());
            pst.setBigDecimal(55, detail.getSrcDtaPriRcv());
            pst.setBoolean(56, detail.isUcLoan());
            pst.setBoolean(57, detail.isUcSchdBuild());
            pst.setBoolean(58, detail.isInQDP());
            pst.setBigDecimal(59, detail.getSrcDtaIntDue());
            pst.setBigDecimal(60, detail.getSrcDtaPriDue());
            pst.executeUpdate();
        }
        catch (Exception e) {
            BasicLoanReconDAOImpl.logger.error((Object)"Exception", (Throwable)e);
            throw new AppException(e.getMessage(), (Throwable)e);
        }
        finally {
            if (pst != null) {
                try {
                    pst.close();
                }
                catch (SQLException ex) {}
            }
        }
        if (pst != null) {
            try {
                pst.close();
            }
            catch (SQLException ex2) {}
        }
    }
    
    public void cleanDestination() {
        StringBuilder sql = new StringBuilder();
        sql = new StringBuilder("TRUNCATE TABLE StatusCount");
        final StatusCount sc = new StatusCount();
        SqlParameterSource paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)sc);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("DELETE FROM DM_BASICLOANRECON WHERE FINREFERENCE IN (");
        sql.append("SELECT FINREFERENCE FROM FINANCEMAIN_STG2 WHERE PROGRESS = 0");
        sql.append(")");
        final BasicLoanRecon blr = new BasicLoanRecon();
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)blr);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("DELETE FROM FINDISBURSEMENTDETAILS WHERE FINID IN (");
        sql.append("SELECT FINID FROM FINANCEMAIN_STG2 WHERE PROGRESS = 0");
        sql.append(")");
        final FinanceDisbursement fdd = new FinanceDisbursement();
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)fdd);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("DELETE FROM FINADVANCEPAYMENTS WHERE FINID IN (");
        sql.append("SELECT FINID FROM FINANCEMAIN_STG2 WHERE PROGRESS = 0");
        sql.append(")");
        final FinAdvancePayments fap = new FinAdvancePayments();
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)fap);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("DELETE FROM FINSCHEDULEDETAILS WHERE FINID IN (");
        sql.append("SELECT FINID FROM FINANCEMAIN_STG2 WHERE PROGRESS = 0");
        sql.append(")");
        final FinanceScheduleDetail fsd = new FinanceScheduleDetail();
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)fsd);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("DELETE FROM FINFEEDETAIL WHERE FINID IN (");
        sql.append("SELECT FINID FROM FINANCEMAIN_STG2 WHERE PROGRESS = 0");
        sql.append(")");
        final FinFeeDetail ffd = new FinFeeDetail();
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)ffd);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("DELETE FROM FINREPAYINSTRUCTION WHERE FINID IN (");
        sql.append("SELECT FINID FROM FINANCEMAIN_STG2 WHERE PROGRESS = 0");
        sql.append(")");
        final RepayInstruction ri = new RepayInstruction();
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)ri);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("DELETE FROM FINRECEIPTDETAIL WHERE RECEIPTID IN (");
        sql.append("SELECT RECEIPTID FROM FINRECEIPTHEADER RCH INNER JOIN FINANCEMAIN_STG2 FM");
        sql.append(" ON RCH.FINID = FM.FINID WHERE PROGRESS = 0)");
        final FinReceiptDetail rcd = new FinReceiptDetail();
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)rcd);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("DELETE FROM RECEIPTALLOCATIONDETAIL WHERE RECEIPTID IN (");
        sql.append("SELECT RECEIPTID FROM FINRECEIPTHEADER RCH INNER JOIN FINANCEMAIN_STG2 FM");
        sql.append(" ON RCH.FINID = FM.FINID WHERE PROGRESS = 0)");
        final ReceiptAllocationDetail rad = new ReceiptAllocationDetail();
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)rad);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("DELETE FROM FINREPAYSCHEDULEDETAIL WHERE FINID IN (");
        sql.append("SELECT FINID FROM FINANCEMAIN_STG2 WHERE PROGRESS = 0");
        sql.append(")");
        final FinRepayHeader rph = new FinRepayHeader();
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)rph);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("DELETE FROM FINREPAYDETAILS WHERE FINID IN (");
        sql.append("SELECT FINID FROM FINANCEMAIN_STG2 WHERE PROGRESS = 0");
        sql.append(")");
        final RepayScheduleDetail rsd = new RepayScheduleDetail();
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)rsd);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("DELETE FROM FINREPAYHEADER WHERE FINID IN (");
        sql.append("SELECT FINID FROM FINANCEMAIN_STG2 WHERE PROGRESS = 0");
        sql.append(")");
        final FinanceRepayments frp = new FinanceRepayments();
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)frp);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("DELETE FROM FINRECEIPTHEADER WHERE FINID IN (");
        sql.append("SELECT FINID FROM FINANCEMAIN_STG2 WHERE PROGRESS = 0");
        sql.append(")");
        final FinReceiptHeader rch = new FinReceiptHeader();
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)rch);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("DELETE FROM FINEXCESSMOVEMENT WHERE EXCESSID IN (");
        sql.append("SELECT FE.EXCESSID FROM FINEXCESSAMOUNT FE INNER JOIN FINANCEMAIN_STG2 FM");
        sql.append(" ON FE.FINREFERENCE = FM.FINREFERENCE WHERE PROGRESS = 0)");
        final FinExcessMovement fem = new FinExcessMovement();
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)fem);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("DELETE FROM FINEXCESSAMOUNT WHERE FINREFERENCE IN (");
        sql.append("SELECT FINREFERENCE FROM FINANCEMAIN_STG2 WHERE PROGRESS = 0");
        sql.append(")");
        final FinExcessAmount fea = new FinExcessAmount();
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)fea);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("DELETE FROM PRESENTMENTDETAILS WHERE FINREFERENCE IN (");
        sql.append("SELECT FINREFERENCE FROM FINANCEMAIN_STG2 WHERE PROGRESS = 0");
        sql.append(")");
        final PresentmentDetail prd = new PresentmentDetail();
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)prd);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("DELETE FROM FINSERVICEINSTRUCTION WHERE FINREFERENCE IN (");
        sql.append("SELECT FINREFERENCE FROM FINANCEMAIN_STG2 WHERE PROGRESS = 0");
        sql.append(")");
        final FinServiceInstruction fsi = new FinServiceInstruction();
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)fsi);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("DELETE FROM MANUALADVISEMOVEMENTS WHERE ADVISEID IN (");
        sql.append("SELECT MA.ADVISEID FROM MANUALADVISE MA INNER JOIN FINANCEMAIN_STG2 FM");
        sql.append(" ON MA.FINREFERENCE = FM.FINREFERENCE WHERE PROGRESS = 0)");
        final ManualAdviseMovements mam = new ManualAdviseMovements();
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)mam);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("DELETE FROM MANUALADVISE WHERE FINREFERENCE IN (");
        sql.append("SELECT FINREFERENCE FROM FINANCEMAIN_STG2 WHERE PROGRESS = 0");
        sql.append(")");
        final ManualAdvise ma = new ManualAdvise();
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)ma);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("DELETE FROM FINODDETAILS WHERE FINREFERENCE IN (");
        sql.append("SELECT FINREFERENCE FROM FINANCEMAIN_STG2 WHERE PROGRESS = 0");
        sql.append(")");
        final FinODDetails fod = new FinODDetails();
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)fod);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("DELETE FROM FINODPENALTYRATES WHERE FINREFERENCE IN (");
        sql.append("SELECT FINREFERENCE FROM FINANCEMAIN_STG2 WHERE PROGRESS = 0");
        sql.append(")");
        final FinODPenaltyRate frat = new FinODPenaltyRate();
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)frat);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("DELETE FROM FINPROVISIONS WHERE FINREFERENCE IN (");
        sql.append("SELECT FINREFERENCE FROM FINANCEMAIN_STG2 WHERE PROGRESS = 0");
        sql.append(")");
        final Provision pro = new Provision();
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)pro);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("DELETE FROM FINPFTDETAILS WHERE FINREFERENCE IN (");
        sql.append("SELECT FINREFERENCE FROM FINANCEMAIN_STG2 WHERE PROGRESS = 0");
        sql.append(")");
        final FinanceProfitDetail fpd = new FinanceProfitDetail();
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)fpd);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("DELETE FROM POSTINGS WHERE FINREFERENCE IN (");
        sql.append("SELECT FINREFERENCE FROM FINANCEMAIN_STG2 WHERE PROGRESS = 0");
        sql.append(")");
        final ReturnDataSet rds = new ReturnDataSet();
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)rds);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("DELETE FROM POSTINGS_MIGR WHERE FINREFERENCE IN (");
        sql.append("SELECT FINREFERENCE FROM FINANCEMAIN_STG2 WHERE PROGRESS = 0");
        sql.append(")");
        final ReturnDataSet rdsMigr = new ReturnDataSet();
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)rdsMigr);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("DELETE FROM PAYMENTDETAILS PAYD WHERE PAYMENTID IN");
        sql.append(" (SELECT PAYMENTID FROM PAYMENTHEADER PAYH INNER JOIN FINANCEMAIN_STG2 FM ");
        sql.append(" ON PAYH.FINREFERENCE = FM.FINREFERENCE WHERE PROGRESS = 0)");
        final ReturnDataSet payD = new ReturnDataSet();
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)payD);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("DELETE FROM PAYMENTINSTRUCTIONS PAYI WHERE PAYMENTID IN");
        sql.append(" (SELECT PAYMENTID FROM PAYMENTHEADER PAYH INNER JOIN FINANCEMAIN_STG2 FM ");
        sql.append(" ON PAYH.FINREFERENCE = FM.FINREFERENCE WHERE PROGRESS = 0)");
        final ReturnDataSet payI = new ReturnDataSet();
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)payI);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("DELETE FROM PAYMENTHEADER WHERE FINREFERENCE IN (");
        sql.append("SELECT FINREFERENCE FROM FINANCEMAIN_STG2 WHERE PROGRESS = 0");
        sql.append(")");
        final ReturnDataSet payH = new ReturnDataSet();
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)payH);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("DELETE FROM FINFEERECEIPTS WHERE RECEIPTID IN");
        sql.append(" (SELECT RECEIPTID FROM FINRECEIPTHEADER RCH INNER JOIN FINANCEMAIN_STG2 FM");
        sql.append(" ON RCH.REFERENCE = FM.FINREFERENCE WHERE PROGRESS = 0)");
        final ReturnDataSet ffr = new ReturnDataSet();
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)ffr);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("DELETE FROM FINWRITEOFFDETAIL WHERE FINREFERENCE IN (");
        sql.append("SELECT FINREFERENCE FROM FINANCEMAIN_STG2 WHERE PROGRESS = 0");
        sql.append(")");
        final ReturnDataSet fwd = new ReturnDataSet();
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)fwd);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("DELETE FROM FINANCEMAIN WHERE FINID IN");
        sql.append(" (SELECT FINID FROM FINANCEMAIN_STG2 WHERE PROGRESS = 0)");
        final FinanceMain fm = new FinanceMain();
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)fm);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("DELETE FROM FINANCEMAIN_TEMP WHERE FINID IN");
        sql.append(" (SELECT FINID FROM FINANCEMAIN_STG2 WHERE PROGRESS = 0)");
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)fm);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
        sql = new StringBuilder("UPDATE FINANCEMAIN_STG2 SET INVESTMENTREF = 1 WHERE QDP_NO_LANNO = 1;");
        paramSource = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)fm);
        this.namedParameterJdbcTemplate.update(sql.toString(), paramSource);
    }
    
    public List<FinanceType> getDMFinTypes(final String type) {
        final MapSqlParameterSource source = new MapSqlParameterSource();
        final StringBuilder selectSql = new StringBuilder(" select  * from rmtfinancetypes ");
        final RowMapper<FinanceType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceType.class);
        final List<FinanceType> finTypeList = this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
        return finTypeList;
    }
    
    public SourceReport getSourceReportDetails(long finID) {
		SourceReport esr = new SourceReport();
		esr.setFinID(finID);

		StringBuilder sql = new StringBuilder();
		sql.append("Select ");
		sql.append("Lan_No, FinID, Total_Disbursed_Amount,Total_UnDisbursed_Amount,Loan_Amount, ");
		sql.append("Total_EMI_Amount,Total_Interest_Amount, Total_Principal_Amount, EMI_Received_Amount, ");
		sql.append("Principal_Received_Amount, Interest_Received_Amount, EMI_Outstanding_Amount,");
		sql.append("Principle_Outstanding,Interest_Outstanding, Tot_Pastdue_Amount, Tot_Pastdue_Principal,");
		sql.append(" Tot_Pastdue_Interest, Interest_Prev_Month, Odc_Due, Odc_Colln, Odc_Tbc");
		sql.append(" FROM SOURCE_REPORT Where FinID =:FinID");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(esr);

		try {
			esr = this.namedParameterJdbcTemplate.queryForObject(sql.toString(), beanParameters,
					new RowMapper<SourceReport>() {
						public SourceReport mapRow(ResultSet rs, int rowNum) throws SQLException {
							SourceReport esr = new SourceReport();
							esr.setInterest_Prev_Month(rs.getBigDecimal("Interest_Prev_Month"));
							esr.setTot_Pastdue_Principal(rs.getBigDecimal("Tot_Pastdue_Principal"));
							esr.setEMI_Outstanding_Amount(rs.getBigDecimal("EMI_Outstanding_Amount"));
							esr.setTotal_EMI_Amount(rs.getBigDecimal("Total_EMI_Amount"));
							esr.setOdc_Colln(rs.getBigDecimal("Odc_Colln"));
							esr.setFinID(rs.getLong("FinID"));
							esr.setEMI_Received_Amount(rs.getBigDecimal("EMI_Received_Amount"));
							esr.setTotal_Interest_Amount(rs.getBigDecimal("Total_Interest_Amount"));
							esr.setTotal_Disbursed_Amount(rs.getBigDecimal("Total_Disbursed_Amount"));
							esr.setTot_Pastdue_Amount(rs.getBigDecimal("Tot_Pastdue_Amount"));
							esr.setPrinciple_Outstanding(rs.getBigDecimal("Principle_Outstanding"));
							esr.setOdc_Due(rs.getBigDecimal("Odc_Due"));
							esr.setInterest_Received_Amount(rs.getBigDecimal("Interest_Received_Amount"));
							esr.setInterest_Outstanding(rs.getBigDecimal("Interest_Outstanding"));
							esr.setOdc_Tbc(rs.getBigDecimal("Odc_Tbc"));
							esr.setPrincipal_Received_Amount(rs.getBigDecimal("Principal_Received_Amount"));
							esr.setTot_Pastdue_Interest(rs.getBigDecimal("Tot_Pastdue_Interest"));
							esr.setTotal_UnDisbursed_Amount(rs.getBigDecimal("Total_UnDisbursed_Amount"));
							esr.setLoan_Amount(rs.getBigDecimal("Loan_Amount"));
							esr.setLan_No(rs.getString("Lan_No"));
							esr.setTotal_Principal_Amount(rs.getBigDecimal("Total_Principal_Amount"));
							return esr;
						}
					});

		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			esr = null;
		}
		return esr;
	}
    
    public List<ScheduleRate> getScheduleRates(long finID) {
		ScheduleRate schdRate = new ScheduleRate();
		schdRate.setFinID(finID);
		
		StringBuilder selectSql = new StringBuilder("Select ");
		selectSql.append("finReference, finID, schDate, calculatedRate,");
		selectSql.append("baseRate, splRate, mrgRate, actRate");
		selectSql.append(" FROM CALRATE_SCHDDETAILS Where FinID =:FinID ORDER By SchDate ");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(schdRate);

		List<ScheduleRate> rateList = null;
		try {
			rateList = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
					new RowMapper<ScheduleRate>() {
						public ScheduleRate mapRow(ResultSet rs, int rowNum) throws SQLException {
							ScheduleRate sr = new ScheduleRate();
							sr.setSplRate(rs.getString("splRate"));
							sr.setCalculatedRate(rs.getBigDecimal("calculatedRate"));
							sr.setSchDate(rs.getDate("schDate"));
							sr.setMrgRate(rs.getBigDecimal("mrgRate"));
							sr.setFinReference(rs.getString("finReference"));
							sr.setBaseRate(rs.getString("baseRate"));
							sr.setFinID(rs.getLong("finID"));
							return sr;
						}
					});

		} catch (EmptyResultDataAccessException e) {
			rateList = new ArrayList<ScheduleRate>();
		}
		return rateList;
	}
    public List<FeeTypeVsGLMapping> getFeeTypeVsGLMappings() {
        final MapSqlParameterSource source = new MapSqlParameterSource();
        final StringBuilder selectSql = new StringBuilder(" select  * from DM_FeeTypes ");
        selectSql.append(" order by feetypeid");
        final RowMapper<FeeTypeVsGLMapping> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FeeTypeVsGLMapping.class);
        final List<FeeTypeVsGLMapping> feeTypeVsGLMapping = this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
        return feeTypeVsGLMapping;
    }
    
    public List<BusinessVertical> getBusinessVerticals() {
        final MapSqlParameterSource source = new MapSqlParameterSource();
        final StringBuilder selectSql = new StringBuilder(" select  id, Code from BUSINESS_VERTICAL ");
        selectSql.append(" order by id");
        final RowMapper<BusinessVertical> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BusinessVertical.class);
        final List<BusinessVertical> bvList = this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
        return bvList;
    }
    
    public TabAgreementDate getTabAgreementDate(long finID) {
		TabAgreementDate tad = new TabAgreementDate();
		tad.setFinID(finID);

		StringBuilder selectSql = new StringBuilder("select ");
		selectSql.append(" FinID, AgreementNo, DisbursalDate, INT_START_DATE IntStartDate ");
		selectSql.append(" From TAB_AGREEMENT_DATE where FinID = :FinID");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(tad);

		try {
			tad = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					new RowMapper<TabAgreementDate>() {
						public TabAgreementDate mapRow(ResultSet rs, int rowNum) throws SQLException {
							TabAgreementDate tad = new TabAgreementDate();
							tad.setIntStartDate(rs.getDate("IntStartDate"));
							tad.setFinID(rs.getLong("FinID"));
							tad.setAgreementNo(rs.getString("AgreementNo"));
							tad.setDisbursalDate(rs.getDate("DisbursalDate"));
							return tad;
						}
					});

		} catch (EmptyResultDataAccessException e) {
			// logger.warn("Exception: ", e);
			tad = null;
		}
		return tad;
	}
	
	public List<ClientRescheduleData> getClientRescheduleData(String agreementNo) {
		ClientRescheduleData crd = new ClientRescheduleData();
		crd.setAgreementNo(agreementNo);

		StringBuilder selectSql = new StringBuilder("select ");
		selectSql.append(" agreementNo, transaction_Date, bulk_Refund, additional_Disbursement, ");
		selectSql.append(" roi, pso_At_Transaction, closing_Pos_Post_Transaction, gap_Interest, ");
		//selectSql.append(" repayment_Effective_Date, gap_Period ");
		selectSql.append(" repayment_Effective_Date ");
		selectSql.append(" FROM MIGR_RESCH_DATA");
		selectSql.append(" Where AgreementNo =:AgreementNo ORDER By repayment_Effective_Date, Transaction_Date ");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(crd);

		List<ClientRescheduleData> crdList = null;
		try {
			crdList = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
					new RowMapper<ClientRescheduleData>() {
						public ClientRescheduleData mapRow(ResultSet rs, int rowNum) throws SQLException {
							ClientRescheduleData crd = new ClientRescheduleData();
							crd.setPso_At_Transaction(rs.getBigDecimal("pso_At_Transaction"));
							//crd.setGap_Period(rs.getString("gap_Period"));
							crd.setBulk_Refund(rs.getBigDecimal("bulk_Refund"));
							crd.setTransaction_Date(rs.getDate("transaction_Date"));
							crd.setClosing_Pos_Post_Transaction(rs.getBigDecimal("closing_Pos_Post_Transaction"));
							crd.setRepayment_Effective_Date(rs.getDate("repayment_Effective_Date"));
							crd.setAdditional_Disbursement(rs.getBigDecimal("additional_Disbursement"));
							crd.setAgreementNo(rs.getString("agreementNo"));
							crd.setRoi(rs.getBigDecimal("roi"));
							crd.setGap_Interest(rs.getBigDecimal("gap_Interest"));
							return crd;
						}
					});

		} catch (EmptyResultDataAccessException e) {
			crdList = new ArrayList<ClientRescheduleData>();
		}
		return crdList;
	}
	
	public Assignment getAssignment(long id) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT id, sharingPercentage");
		sql.append(" From Assignment");
		sql.append(" Where id = :id");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		Assignment assignment = new Assignment();
		assignment.setId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(assignment);
		RowMapper<Assignment> rowMapper = BeanPropertyRowMapper.newInstance(Assignment.class);

		try {
			assignment = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			//logger.error("Exception: ", e);
			assignment = null;
		}

		logger.debug(Literal.LEAVING);
		return assignment;
	}
	
    
    public DRCorrections getDRCorrections(final String finReference) {
        DRCorrections dc = new DRCorrections();
        dc.setFinReference(finReference);
        final StringBuilder sql = new StringBuilder();
        sql.append("Select ");
        sql.append("FinReference, ReasonCode, DrRequired");
        sql.append(" FROM DR_CORRECTION_18MAR Where FinReference =:FinReference");
        final SqlParameterSource beanParameters = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)dc);
        RowMapper<DRCorrections> typeRowMapper = BeanPropertyRowMapper.newInstance(DRCorrections.class);
        try {
            dc = (DRCorrections)this.namedParameterJdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
        }
        catch (EmptyResultDataAccessException e) {
            BasicLoanReconDAOImpl.logger.warn((Object)"Exception: ", (Throwable)e);
            dc = null;
        }
        return dc;
    }
    
    public CutOffDateSchedule getCutOffDateSchedule(final String finReference) {
        CutOffDateSchedule cds = new CutOffDateSchedule();
        cds.setFinReference(finReference);
        final StringBuilder sql = new StringBuilder();
        sql.append("Select ");
        sql.append("FinReference, SchDate, ProfitCalc, ProfitSchd, ");
        sql.append("PrincipalSchd, RepayAmount, CalculatedRate, CpzAmount, ");
        sql.append("PartialPaidAMT, PresentmentID ");
        sql.append(" FROM FINSCHEDULEDETAILS_COMPARE14 Where FinReference =:FinReference");
        final SqlParameterSource beanParameters = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)cds);
        RowMapper<CutOffDateSchedule> typeRowMapper = BeanPropertyRowMapper.newInstance(CutOffDateSchedule.class);
        try {
            cds = (CutOffDateSchedule)this.namedParameterJdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
        }
        catch (EmptyResultDataAccessException e) {
            BasicLoanReconDAOImpl.logger.warn((Object)"Exception: ", (Throwable)e);
            cds = null;
        }
        return cds;
    }
    
    public CutOffDateSchedule getEMICorrSchedule(final String finReference) {
        CutOffDateSchedule cds = new CutOffDateSchedule();
        cds.setFinReference(finReference);
        final StringBuilder sql = new StringBuilder();
        sql.append("Select ");
        sql.append("FinReference, SchDate, PFTCAL_STG*100 ProfitCalc, PFT_STG*100 ProfitSchd, ");
        sql.append("PRI_STG *100 PrincipalSchd,RPY_STG *100 RepayAmount ");
        sql.append(" FROM FINSCHEDULEDETAILS_COMPARE18 Where FinReference =:FinReference");
        final SqlParameterSource beanParameters = (SqlParameterSource)new BeanPropertySqlParameterSource((Object)cds);
        RowMapper<CutOffDateSchedule> typeRowMapper = BeanPropertyRowMapper.newInstance(CutOffDateSchedule.class);
        try {
            cds = (CutOffDateSchedule)this.namedParameterJdbcTemplate.queryForObject(sql.toString(), beanParameters,typeRowMapper);
        }
        catch (EmptyResultDataAccessException e) {
            BasicLoanReconDAOImpl.logger.warn((Object)"Exception: ", (Throwable)e);
            cds = null;
        }
        return cds;
    }
}