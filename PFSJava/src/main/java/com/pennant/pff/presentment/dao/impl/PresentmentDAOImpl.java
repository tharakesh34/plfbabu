package com.pennant.pff.presentment.dao.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.dao.financemanagement.impl.PresentmentDetailDAOImpl;
import com.pennant.backend.model.payment.PaymentHeader;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.pff.mandate.InstrumentType;
import com.pennant.pff.presentment.ExcludeReasonCode;
import com.pennant.pff.presentment.dao.PresentmentDAO;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.presentment.model.PresentmentDetail;
import com.pennanttech.pff.presentment.model.PresentmentHeader;

public class PresentmentDAOImpl extends SequenceDao<PaymentHeader> implements PresentmentDAO {
	private static Logger logger = LogManager.getLogger(PresentmentDetailDAOImpl.class);

	public PresentmentDAOImpl() {
		super();
	}

	@Override
	public int extarct(Date dueDate) {
		StringBuilder sql = new StringBuilder(getExtractQuery(null));
		sql.append(" Where (SchDate = ? or DefSchddate = ?)");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setInt(index++, 1);
			ps.setDate(index++, JdbcUtil.getDate(dueDate));
			ps.setDate(index++, JdbcUtil.getDate(dueDate));
		});

		sql = new StringBuilder(getExtractQuery(InstrumentType.PDC.name()));
		sql.append(" Where (SchDate = ? or DefSchddate = ?) and ChequeType = ?");

		logger.debug(Literal.SQL + sql.toString());

		recordCount = recordCount + this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setInt(index++, 1);
			ps.setDate(index++, JdbcUtil.getDate(dueDate));
			ps.setDate(index++, JdbcUtil.getDate(dueDate));
			ps.setString(index++, InstrumentType.PDC.name());
		});
		return recordCount;

	}

	@Override
	public int extarct(String instrumentType, Date dueDate) {
		StringBuilder sql = new StringBuilder(getExtractQuery(instrumentType));
		sql.append(" Where (SchDate = ? or DefSchddate = ?) and MandateType = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setInt(index++, 1);
			ps.setDate(index++, JdbcUtil.getDate(dueDate));
			ps.setDate(index++, JdbcUtil.getDate(dueDate));
			ps.setString(index++, instrumentType);
		});
		return recordCount;
	}

	@Override
	public int extarct(String instrumentType, Date fromDate, Date toDate) {
		StringBuilder sql = new StringBuilder(getExtractQuery(instrumentType));
		sql.append(" Where (SchDate >= ? and SchDate <= ?) or (DefSchddate >= ? and DefSchddate <= ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setInt(index++, 1);

			ps.setDate(index++, JdbcUtil.getDate(fromDate));
			ps.setDate(index++, JdbcUtil.getDate(toDate));
			ps.setDate(index++, JdbcUtil.getDate(fromDate));
			ps.setDate(index++, JdbcUtil.getDate(toDate));

		});
		return recordCount;
	}

	@Override
	public int clearByNoDues() {
		String sql = "Delete From Presentment_Stage Where (ProfitSchd + PrincipalSchd + FeeSchd + TdsAmount) - (SchdPftPaid + SchdPriPaid + SchdFeePaid + TdsPaid) <= ? and ProductCategory != ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.update(sql, ps -> {
			ps.setBigDecimal(1, BigDecimal.ZERO);
			ps.setString(2, FinanceConstants.PRODUCT_ODFACILITY);
		});
	}

	@Override
	public int clearByInstrumentType(String instrumentType) {
		String sql = "Delete From Presentment_Stage Where MandateType != ? or ChequeType != ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.update(sql, instrumentType, instrumentType);
	}

	@Override
	public int clearByInstrumentType(String instrumentType, String emnadateSource) {
		String sql = "Delete From Presentment_Stage Where MandateType != ? and EmandateSource != ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.update(sql, instrumentType, emnadateSource);
	}

	@Override
	public int clearByLoanType(String loanType) {
		String sql = "Delete From Presentment_Stage Where FinType != ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.update(sql, loanType);
	}

	@Override
	public int clearByLoanBranch(String loanBranch) {
		String sql = "Delete From Presentment_Stage Where FinBranch != ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.update(sql, loanBranch);
	}

	@Override
	public int clearByEntityCode(String entityCode) {
		String sql = "Delete From Presentment_Stage Where EntityCode != ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.update(sql, entityCode);
	}

	@Override
	public int clearByExistingRecord() {
		StringBuilder sql = new StringBuilder();
		sql.append(" Delete From Presentment_Stage Where ID in (");
		sql.append(" Select ps.ID from Presentment_Stage ps");
		sql.append(" Inner Join PresentmentDetails pd on pd.FinID = ps.FinID and pd.SchDate = ps.SchDate");
		sql.append(" Where pd.ExcludeReason in (?, ?, ?, ?)");
		sql.append(" )");

		logger.debug(Literal.SQL.concat(sql.toString()));

		int count = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setInt(index++, ExcludeReasonCode.EMI_INCLUDE.id());
			ps.setInt(index++, ExcludeReasonCode.EMI_IN_ADVANCE.id());
			ps.setInt(index++, ExcludeReasonCode.INT_ADV.id());
			ps.setInt(index, ExcludeReasonCode.EMI_ADV.id());
		});

		sql = new StringBuilder();
		sql.append(" Delete From Presentment_Stage Where ID in (");
		sql.append(" Select ps.ID from Presentment_Stage ps");
		sql.append(" Inner Join PresentmentDetails pd on pd.FinID = ps.FinID and pd.SchDate = ps.SchDate");
		sql.append(" Inner Join PresentmentHeader ph on ph.ID = pd.PresentmentID");
		sql.append(" Inner Join Mandates m on m.MandateID = ps.MandateID");
		sql.append(" Where pd.ExcludeReason = ? and ph.status in (1, 2, 3) and m.StartDate <= ps.SchDate");
		sql.append(" )");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return count = count + this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setInt(index++, ExcludeReasonCode.MANUAL_EXCLUDE.id());
		});

	}

	@Override
	public int clearByRepresentment() {
		StringBuilder sql = new StringBuilder();
		sql.append(" Delete From Presentment_Stage Where ID not in (");
		sql.append(" Select ps.ID from Presentment_Stage ps");
		sql.append(" Inner Join PresentmentDetails pd on pd.FinID = ps.FinID and pd.SchDate = ps.SchDate");
		sql.append(" Where pd.Status in (?, ?)");
		sql.append(" )");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, "B");
			ps.setString(index++, "F");
		});
	}

	@Override
	public void updatePartnerBankID() {
		String sql = "Update Presentment_Stage set PartnerBankId = ? Where PartnerBankId is null";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql.toString(), -1);
	}

	@Override
	public int clearByManualExclude() {
		StringBuilder sql = new StringBuilder();
		sql.append(" Delete From Presentment_Stage Where ID in (");
		sql.append(" Select ps.ID from Presentment_Stage ps");
		sql.append(" Inner Join PresentmentDetails pd on pd.FinID = ps.FinID and pd.SchDate = ps.SchDate");
		sql.append(" Inner Join PresentmentHeader ph on ph.ID = pd.PresentmentID");
		sql.append(" Inner Join Mandates m On m.MandateID = ps.MandateID");
		sql.append(" Where pd.ExcludeReason = ? and ph.status in (1, 2, 3) and m.StartDate <= ps.SchDate");
		sql.append(" )");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setInt(index++, ExcludeReasonCode.MANUAL_EXCLUDE.id());
		});

	}

	/*
	 * @Override public void orderByPartnerBankIdAndBankCode() { StringBuilder sql = new StringBuilder();
	 * sql.append("Select BankCode, DefSchddate, EntityCode, PartnerBankId From Presentment_Stage");
	 * sql.append(" ORDER BY BankCode, DefSchddate, EntityCode, PartnerBankId"); }
	 * 
	 * @Override public void orderByBankCode() { StringBuilder sql = new StringBuilder();
	 * sql.append("Select BankCode, DefSchddate, EntityCode From Presentment_Stage");
	 * sql.append(" ORDER BY BankCode, DefSchddate, EntityCode"); }
	 * 
	 * @Override public void orderByPartnerBankId() { StringBuilder sql = new StringBuilder();
	 * sql.append("Select DefSchddate, EntityCode, PartnerBankId From Presentment_Stage");
	 * sql.append(" ORDER BY DefSchddate, EntityCode, PartnerBankId"); }
	 * 
	 * @Override public void orderByData() { StringBuilder sql = new StringBuilder();
	 * sql.append("Select DefSchddate, EntityCode From Presentment_Stage");
	 * sql.append(" ORDER BY DefSchddate, EntityCode"); }
	 */

	private StringBuilder getExtractQuery(String instrumentType) {
		StringBuilder sql = new StringBuilder();

		if (InstrumentType.isPDC(instrumentType)) {
			sql.append("Insert Into Presentment_Stage (");
			sql.append(" FinId, FinReference, FinType, ProductCategory, FinBranch, EntityCode");
			sql.append(", BpiTreatment, GrcPeriodEndDate, GrcAdvType, AdvType, AdvStage");
			sql.append(", SchDate, DefSchdDate, SchSeq, InstNumber, BpiOrHoliday");
			sql.append(", ProfitSchd, PrincipalSchd, FeeSchd, TdsAmount");
			sql.append(", SchdPftPaid, SchdPriPaid, SchdFeePaid, TdsPaid");
			sql.append(", InstrumentType, ChequeId, ChequeType, ChequeStatus, ChequeDate");
			sql.append(", PartnerBankId, BranchCode, BankCode");
			sql.append(") Select");
			sql.append(" fm.FinId, fm.FinReference, fm.FinType, fm.ProductCategory, fm.FinBranch, sdd.EntityCode");
			sql.append(", fm.BpiTreatment, fm.GrcPeriodEndDate, fm.GrcAdvType, fm.AdvType, fm.AdvStage");
			sql.append(", fsd.SchDate, fsd.DefSchdDate, fsd.SchSeq, fsd.InstNumber, fsd.BpiOrHoliday");
			sql.append(", fsd.ProfitSchd, fsd.PrincipalSchd, fsd.FeeSchd, fsd.TdsAmount");
			sql.append(", fsd.SchdPftPaid, fsd.SchdPriPaid, fsd.SchdFeePaid, fsd.TdsPaid");
			sql.append(", cd.ChequeType, cd.ChequeDetailsId, cd.ChequeType, cd.ChequeStatus, cd.ChequeDate");
			sql.append(", null PartnerBankId, b.BranchCode, bb.BankCode");
			sql.append(" From FinScheduleDetails fsd");
			sql.append(" Inner Join FinanceMain fm On fm.FinID = fsd.FinID and fm.FinIsActive = ?");
			sql.append(" Inner Join RmtFinanceTypes ft on ft.FinType = fm.FinType");
			sql.append(" Inner Join ChequeHeader ch on ch.FinId = fm.FinId");
			sql.append(" Inner Join ChequeDetail cd on cd.HeaderId = ch.HeaderId and cd.EmiRefNo = fsd.InstNumber");
			sql.append(" Inner Join RmtBranches b on b.BranchCode = fm.FinBranch");
			sql.append(" Inner Join BankBranches bb on bb.BankBranchId = cd.BankBranchId");
			sql.append(" Inner Join SmtDivisionDetail sdd On sdd.DivisionCode = ft.FinDivision");
		} else if (InstrumentType.isDAS(instrumentType)) {
			sql.append("Insert Into Presentment_Stage (");
			sql.append(" FinId, FinReference, FinType, ProductCategory, FinBranch, EntityCode");
			sql.append(", BpiTreatment, GrcPeriodEndDate, GrcAdvType, AdvType, AdvStage");
			sql.append(", SchDate, DefSchdDate, SchSeq, InstNumber, BpiOrHoliday");
			sql.append(", ProfitSchd, PrincipalSchd, FeeSchd, TdsAmount");
			sql.append(", SchdPftPaid, SchdPriPaid, SchdFeePaid, TdsPaid");
			sql.append(", InstrumentType, MandateId, MandateType, EmandateSource, MandateStatus, MandateExpiryDate");
			sql.append(", PartnerBankId, BranchCode");// , BankCode
			sql.append(") Select");
			sql.append(" fm.FinId, fm.FinReference, fm.FinType, fm.ProductCategory, fm.FinBranch, sdd.EntityCode");
			sql.append(", fm.BpiTreatment, fm.GrcPeriodEndDate, fm.GrcAdvType, fm.AdvType, fm.AdvStage");
			sql.append(", fsd.SchDate, fsd.DefSchdDate, fsd.SchSeq, fsd.InstNumber, fsd.BpiOrHoliday");
			sql.append(", fsd.ProfitSchd, fsd.PrincipalSchd, fsd.FeeSchd, fsd.TdsAmount");
			sql.append(", fsd.SchdPftPaid, fsd.SchdPriPaid, fsd.SchdFeePaid, fsd.TdsPaid");
			sql.append(", m.MandateType, fm.MandateId, m.MandateType, m.EmandateSource, m.Status, m.ExpiryDate");
			sql.append(", m.PartnerBankId, b.BranchCode");// , bb.BankCode
			sql.append(" From FinScheduleDetails fsd");
			sql.append(" Inner Join FinanceMain fm On fm.FinID = fsd.FinID and fm.FinIsActive = ?");
			sql.append(" Inner Join RmtFinanceTypes ft On ft.FinType = fm.FinType");
			sql.append(" Inner Join Mandates m ON m.MandateId = fm.MandateId");
			sql.append(" Inner Join RmtBranches b On b.BranchCode = fm.FinBranch");
			// sql.append(" Inner Join BankBranches bb On bb.BankBranchId = m.BankBranchId");
			sql.append(" Inner Join SmtDivisionDetail sdd On sdd.DivisionCode = ft.FinDivision");

		} else {
			sql.append("Insert Into Presentment_Stage (");
			sql.append(" FinId, FinReference, FinType, ProductCategory, FinBranch, EntityCode");
			sql.append(", BpiTreatment, GrcPeriodEndDate, GrcAdvType, AdvType, AdvStage");
			sql.append(", SchDate, DefSchdDate, SchSeq, InstNumber, BpiOrHoliday");
			sql.append(", ProfitSchd, PrincipalSchd, FeeSchd, TdsAmount");
			sql.append(", SchdPftPaid, SchdPriPaid, SchdFeePaid, TdsPaid");
			sql.append(", InstrumentType, MandateId, MandateType, EmandateSource, MandateStatus, MandateExpiryDate");
			sql.append(", PartnerBankId, BranchCode, BankCode");
			sql.append(") Select");
			sql.append(" fm.FinId, fm.FinReference, fm.FinType, fm.ProductCategory, fm.FinBranch, sdd.EntityCode");
			sql.append(", fm.BpiTreatment, fm.GrcPeriodEndDate, fm.GrcAdvType, fm.AdvType, fm.AdvStage");
			sql.append(", fsd.SchDate, fsd.DefSchdDate, fsd.SchSeq, fsd.InstNumber, fsd.BpiOrHoliday");
			sql.append(", fsd.ProfitSchd, fsd.PrincipalSchd, fsd.FeeSchd, fsd.TdsAmount");
			sql.append(", fsd.SchdPftPaid, fsd.SchdPriPaid, fsd.SchdFeePaid, fsd.TdsPaid");
			sql.append(", m.MandateType, fm.MandateId, m.MandateType, m.EmandateSource, m.Status, m.ExpiryDate");
			sql.append(", m.PartnerBankId, b.BranchCode, bb.BankCode");
			sql.append(" From FinScheduleDetails fsd");
			sql.append(" Inner Join FinanceMain fm On fm.FinID = fsd.FinID and fm.FinIsActive = ?");
			sql.append(" Inner Join RmtFinanceTypes ft On ft.FinType = fm.FinType");
			sql.append(" Inner Join Mandates m ON m.MandateId = fm.MandateId");
			sql.append(" Inner Join RmtBranches b On b.BranchCode = fm.FinBranch");
			sql.append(" Inner Join BankBranches bb On bb.BankBranchId = m.BankBranchId");
			sql.append(" Inner Join SmtDivisionDetail sdd On sdd.DivisionCode = ft.FinDivision");

		}

		return sql;
	}

	@Override
	public List<PresentmentDetail> getPresentmentDetails() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinId, FinReference, FinType, ProductCategory, FinBranch, EntityCode");
		sql.append(", BpiTreatment, GrcPeriodEndDate, GrcAdvType, AdvType, AdvStage");
		sql.append(", SchDate, DefSchdDate, SchSeq, InstNumber, BpiOrHoliday");
		sql.append(", ProfitSchd, PrincipalSchd, FeeSchd, TdsAmount");
		sql.append(", SchdPftPaid, SchdPriPaid, SchdFeePaid, TdsPaid");
		sql.append(", MandateId, MandateType, EmandateSource, MandateStatus, MandateExpiryDate");
		sql.append(", ChequeId, ChequeType, ChequeStatus, ChequeDate");
		sql.append(", PartnerBankId, BranchCode, BankCode");
		sql.append(" From Presentment_Stage");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			PresentmentDetail pd = new PresentmentDetail();

			pd.setFinID(rs.getLong("FinId"));
			pd.setFinReference(rs.getString("FinReference"));
			pd.setFinType(rs.getString("FinType"));
			pd.setProductCategory(rs.getString("ProductCategory"));
			pd.setFinBranch(rs.getString("FinBranch"));
			pd.setEntityCode(rs.getString("EntityCode"));
			pd.setBpiTreatment(rs.getString("BpiTreatment"));
			pd.setGrcPeriodEndDate(rs.getDate("GrcPeriodEndDate"));
			pd.setGrcAdvType(rs.getString("GrcAdvType"));
			pd.setAdvType(rs.getString("AdvType"));
			pd.setAdvStage(rs.getString("AdvStage"));
			pd.setSchDate(rs.getDate("SchDate"));
			pd.setDefSchdDate(rs.getDate("DefSchdDate"));
			pd.setSchSeq(rs.getInt("SchSeq"));
			pd.setInstNumber(rs.getInt("InstNumber"));
			pd.setBpiOrHoliday(rs.getString("BpiOrHoliday"));
			pd.setProfitSchd(rs.getBigDecimal("ProfitSchd"));
			pd.setPrincipalSchd(rs.getBigDecimal("PrincipalSchd"));
			pd.setFeeSchd(rs.getBigDecimal("FeeSchd"));
			pd.settDSAmount(rs.getBigDecimal("TdsAmount"));
			pd.setSchdPftPaid(rs.getBigDecimal("SchdPftPaid"));
			pd.setSchdPriPaid(rs.getBigDecimal("SchdPriPaid"));
			pd.setSchdFeePaid(rs.getBigDecimal("SchdFeePaid"));
			pd.setTdsPaid(rs.getBigDecimal("TdsPaid"));
			pd.setMandateId(JdbcUtil.getLong(rs.getObject("MandateId")));
			pd.setMandateType(rs.getString("MandateType"));
			pd.setEmandateSource(rs.getString("EmandateSource"));
			pd.setMandateStatus(rs.getString("MandateStatus"));
			pd.setMandateExpiryDate(rs.getDate("MandateExpiryDate"));
			pd.setChequeId(JdbcUtil.getLong(rs.getObject("ChequeId")));
			pd.setChequeType(rs.getString("ChequeType"));
			pd.setChequeStatus(rs.getString("ChequeStatus"));
			pd.setChequeDate(rs.getDate("ChequeDate"));
			pd.setPartnerBankId(JdbcUtil.getLong(rs.getObject("PartnerBankId")));
			pd.setBranchCode(rs.getString("BranchCode"));
			pd.setBankCode(rs.getString("BankCode"));

			return pd;
		});
	}

	@Override
	public List<PresentmentDetail> getGroupByDefault() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" DefSchdDate, EntityCode, InstrumentType");
		sql.append(" From Presentment_Stage");
		sql.append(" Group By DefSchdDate, EntityCode, InstrumentType");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			PresentmentDetail pd = new PresentmentDetail();

			pd.setDefSchdDate(JdbcUtil.getDate(rs.getDate("DefSchdDate")));
			pd.setEntityCode(rs.getString("EntityCode"));
			pd.setInstrumentType(rs.getString("InstrumentType"));

			return pd;
		});
	}

	@Override
	public List<PresentmentDetail> getGroupByPartnerBankAndBank() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" DefSchdDate, BankCode, EntityCode, PartnerBankId, InstrumentType");
		sql.append(" From Presentment_Stage");
		sql.append(" Group By DefSchdDate, BankCode, EntityCode, PartnerBankId, InstrumentType");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			PresentmentDetail pd = new PresentmentDetail();

			pd.setDefSchdDate(JdbcUtil.getDate(rs.getDate("DefSchdDate")));
			pd.setBankCode(rs.getString("BankCode"));
			pd.setEntityCode(rs.getString("EntityCode"));
			pd.setPartnerBankId(JdbcUtil.getLong(rs.getObject("PartnerBankId")));
			pd.setInstrumentType(rs.getString("InstrumentType"));

			return pd;
		});
	}

	@Override
	public List<PresentmentDetail> getGroupByBank() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" DefSchdDate, BankCode, EntityCode, InstrumentType");
		sql.append(" From Presentment_Stage");
		sql.append(" Group By DefSchdDate, BankCode, EntityCode, InstrumentType");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			PresentmentDetail pd = new PresentmentDetail();

			pd.setDefSchdDate(JdbcUtil.getDate(rs.getDate("DefSchdDate")));
			pd.setBankCode(rs.getString("BankCode"));
			pd.setEntityCode(rs.getString("EntityCode"));
			pd.setInstrumentType(rs.getString("InstrumentType"));

			return pd;
		});
	}

	@Override
	public List<PresentmentDetail> getGroupByPartnerBank() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" DefSchdDate, EntityCode, PartnerBankId, InstrumentType");
		sql.append(" From Presentment_Stage");
		sql.append(" Group By DefSchdDate, EntityCode, PartnerBankId, InstrumentType");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			PresentmentDetail pd = new PresentmentDetail();

			pd.setDefSchdDate(JdbcUtil.getDate(rs.getDate("DefSchdDate")));
			pd.setEntityCode(rs.getString("EntityCode"));
			pd.setPartnerBankId(JdbcUtil.getLong(rs.getObject("PartnerBankId")));
			pd.setInstrumentType(rs.getString("InstrumentType"));

			return pd;
		});
	}

	@Override
	public void updateHeaderIdByDefault(List<PresentmentDetail> list) {
		StringBuilder sql = new StringBuilder("Update");
		sql.append(" Presentment_Stage");
		sql.append(" set HeaderID = ?");
		sql.append(" Where DefSchdDate = ? and EntityCode = ? and InstrumentType = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 1;
				PresentmentDetail pd = list.get(i);

				ps.setLong(index++, pd.getHeaderId());
				ps.setDate(index++, JdbcUtil.getDate(pd.getDefSchdDate()));
				ps.setString(index++, pd.getEntityCode());
				ps.setString(index, pd.getInstrumentType());
			}

			@Override
			public int getBatchSize() {
				return list.size();
			}
		});
	}

	@Override
	public void updateHeaderIdByPartnerBankAndBank(List<PresentmentDetail> list) {
		StringBuilder sql = new StringBuilder("Update");
		sql.append(" Presentment_Stage");
		sql.append(" set HeaderID = ?");
		sql.append(" Where DefSchdDate = ? and BankCode = ? and EntityCode = ?");
		sql.append(" and PartnerBankId = ? and InstrumentType = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 1;
				PresentmentDetail pd = list.get(i);

				ps.setLong(index++, pd.getHeaderId());
				ps.setDate(index++, JdbcUtil.getDate(pd.getDefSchdDate()));
				ps.setString(index++, pd.getBankCode());
				ps.setString(index++, pd.getEntityCode());
				ps.setObject(index++, pd.getPartnerBankId());
				ps.setString(index, pd.getInstrumentType());
			}

			@Override
			public int getBatchSize() {
				return list.size();
			}
		});
	}

	@Override
	public void updateHeaderIdByBank(List<PresentmentDetail> list) {
		StringBuilder sql = new StringBuilder("Update");
		sql.append(" Presentment_Stage");
		sql.append(" set HeaderID = ?");
		sql.append(" Where DefSchdDate = ? and BankCode = ? and EntityCode = ? and InstrumentType = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 1;
				PresentmentDetail pd = list.get(i);

				ps.setLong(index++, pd.getHeaderId());
				ps.setDate(index++, JdbcUtil.getDate(pd.getDefSchdDate()));
				ps.setString(index++, pd.getBankCode());
				ps.setString(index++, pd.getEntityCode());
				ps.setString(index, pd.getInstrumentType());
			}

			@Override
			public int getBatchSize() {
				return list.size();
			}
		});
	}

	@Override
	public void updateHeaderIdByPartnerBank(List<PresentmentDetail> list) {
		StringBuilder sql = new StringBuilder("Update");
		sql.append(" Presentment_Stage");
		sql.append(" set HeaderID = ?");
		sql.append(" Where DefSchdDate = ? and EntityCode = ? and PartnerBankId = ? and InstrumentType = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 1;
				PresentmentDetail pd = list.get(i);

				ps.setLong(index++, pd.getHeaderId());
				ps.setDate(index++, JdbcUtil.getDate(pd.getDefSchdDate()));
				ps.setString(index++, pd.getEntityCode());
				ps.setObject(index++, pd.getPartnerBankId());
				ps.setString(index, pd.getInstrumentType());
			}

			@Override
			public int getBatchSize() {
				return list.size();
			}
		});
	}

	@Override
	public long saveList(List<PresentmentDetail> presentments) {

		StringBuilder sql = new StringBuilder();
		sql.append(" Insert into PresentmentDetails");
		sql.append(" (Id, PresentmentId, PresentmentRef, FinID, FinReference, SchDate, MandateId");
		sql.append(", SchAmtDue, SchPriDue, SchPftDue, SchFeeDue, SchInsDue, SchPenaltyDue, AdvanceAmt, ExcessID");
		sql.append(", AdviseAmt, PresentmentAmt, ExcludeReason, BounceID, EmiNo, TDSAmount, Status, ReceiptID");
		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		return jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int index) throws SQLException {
				int i = 1;
				PresentmentDetail pd = presentments.get(index);

				ps.setLong(i++, pd.getId());
				ps.setLong(i++, pd.getHeaderId());
				ps.setString(i++, pd.getPresentmentRef());
				ps.setLong(i++, pd.getFinID());
				ps.setString(i++, pd.getFinReference());
				ps.setDate(i++, JdbcUtil.getDate(pd.getSchDate()));
				ps.setObject(i++, pd.getMandateId());
				ps.setBigDecimal(i++, pd.getSchAmtDue());
				ps.setBigDecimal(i++, pd.getSchPriDue());
				ps.setBigDecimal(i++, pd.getSchPftDue());
				ps.setBigDecimal(i++, pd.getSchFeeDue());
				ps.setBigDecimal(i++, pd.getSchInsDue());
				ps.setBigDecimal(i++, pd.getSchPenaltyDue());
				ps.setBigDecimal(i++, pd.getAdvanceAmt());
				ps.setLong(i++, pd.getExcessID());
				ps.setBigDecimal(i++, pd.getAdviseAmt());
				ps.setBigDecimal(i++, pd.getPresentmentAmt());
				ps.setLong(i++, pd.getExcludeReason());
				ps.setLong(i++, pd.getBounceID());
				ps.setInt(i++, pd.getEmiNo());
				ps.setBigDecimal(i++, pd.gettDSAmount());
				ps.setString(i++, pd.getStatus());
				ps.setLong(i++, pd.getReceiptID());
				ps.setInt(i++, pd.getVersion());
				ps.setLong(i++, pd.getLastMntBy());
				ps.setTimestamp(i++, pd.getLastMntOn());
				ps.setString(i++, pd.getRecordStatus());
				ps.setString(i++, pd.getRoleCode());
				ps.setString(i++, pd.getNextRoleCode());
				ps.setString(i++, pd.getTaskId());
				ps.setString(i++, pd.getNextTaskId());
				ps.setString(i++, pd.getRecordType());
				ps.setLong(i++, pd.getWorkflowId());

			}

			@Override
			public int getBatchSize() {
				return presentments.size();
			}
		}).length;
	}

	@Override
	public long savePresentmentHeader(PresentmentHeader ph) {
		StringBuilder sql = new StringBuilder("insert into");
		sql.append(" PresentmentHeader");
		sql.append("(Id, Reference, PresentmentDate, PartnerBankId, FromDate, ToDate, PresentmentType");
		sql.append(", Status, MandateType, EmandateSource, FinBranch, Schdate, LoanType, ImportStatusId");
		sql.append(", TotalRecords, ProcessedRecords, SuccessRecords, FailedRecords, Version, LastMntBy");
		sql.append(", LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType");
		sql.append(", WorkflowId, dBStatusId, bankCode, EntityCode");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?");
		sql.append(")");

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, ph.getId());
			ps.setString(index++, ph.getReference());
			ps.setDate(index++, JdbcUtil.getDate(ph.getPresentmentDate()));
			ps.setObject(index++, ph.getPartnerBankId());
			ps.setDate(index++, JdbcUtil.getDate(ph.getFromDate()));
			ps.setDate(index++, JdbcUtil.getDate(ph.getToDate()));
			ps.setString(index++, ph.getPresentmentType());
			ps.setInt(index++, ph.getStatus());
			ps.setString(index++, ph.getMandateType());
			ps.setString(index++, ph.getEmandateSource());
			ps.setString(index++, ph.getFinBranch());
			ps.setDate(index++, JdbcUtil.getDate(ph.getSchdate()));
			ps.setString(index++, ph.getLoanType());
			ps.setLong(index++, ph.getImportStatusId());
			ps.setInt(index++, ph.getTotalRecords());
			ps.setInt(index++, ph.getProcessedRecords());
			ps.setInt(index++, ph.getSuccessRecords());
			ps.setInt(index++, ph.getFailedRecords());
			ps.setInt(index++, ph.getVersion());
			ps.setLong(index++, ph.getLastMntBy());
			ps.setTimestamp(index++, ph.getLastMntOn());
			ps.setString(index++, ph.getRecordStatus());
			ps.setString(index++, ph.getRoleCode());
			ps.setString(index++, ph.getNextRoleCode());
			ps.setString(index++, ph.getTaskId());
			ps.setString(index++, ph.getNextTaskId());
			ps.setString(index++, ph.getRecordType());
			ps.setLong(index++, ph.getWorkflowId());
			ps.setLong(index++, ph.getdBStatusId());
			ps.setString(index++, ph.getBankCode());
			ps.setString(index++, ph.getEntityCode());
		});

		return ph.getId();
	}

	@Override
	public int updateSchdWithPresentmentId(List<PresentmentDetail> presenetments) {
		StringBuilder sql = new StringBuilder();
		sql.append("Update FinScheduleDetails Set PresentmentId = ?");
		sql.append(" Where FinID = ? and SchDate = ? and  SchSeq = ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int index) throws SQLException {
				PresentmentDetail pd = presenetments.get(index);
				ps.setLong(1, pd.getId());
				ps.setLong(2, pd.getFinID());
				ps.setDate(3, JdbcUtil.getDate(pd.getSchDate()));
				ps.setInt(4, pd.getSchSeq());
			}

			@Override
			public int getBatchSize() {
				return presenetments.size();
			}
		}).length;
	}

	@Override
	public void clearQueue() {
		jdbcOperations.update("TRUNCATE TABLE PRESENTMENT_STAGE");
	}

	@Override
	public long getNextValue() {
		return getNextValue("SeqPresentmentDetails");
	}

	@Override
	public long getSeqNumber(String tableName) {
		return getNextValue(tableName);
	}

}
