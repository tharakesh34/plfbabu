package com.pennant.pff.presentment.dao.impl;

import java.util.Collections;

import javax.sql.DataSource;

import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;

import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pff.presentment.model.PresentmentDetail;

public class PresentmentItemReader extends JdbcPagingItemReaderBuilder<PresentmentDetail> {

	public PresentmentItemReader() {
		super();
	}

	public PresentmentItemReader(DataSource dataSource) {
		super.dataSource(dataSource);
		super.fetchSize(100000);
		super.selectClause(getSql());
		super.fromClause("From Presentment_Extraction_Stage");
		super.whereClause("Where ProcessingFlag = 0");
		super.saveState(false);
		super.sortKeys(Collections.singletonMap("ID", Order.ASCENDING));

		super.rowMapper((rs, rowNum) -> {
			PresentmentDetail pd = new PresentmentDetail();

			pd.setId(rs.getLong("Id"));
			pd.setHeaderId(JdbcUtil.getLong(rs.getObject("HeaderID")));
			pd.setDueDate(rs.getDate("DueDate"));
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
			pd.setChequeAmount(rs.getBigDecimal("ChequeAmount"));
			pd.setPartnerBankId(JdbcUtil.getLong(rs.getObject("PartnerBankId")));
			pd.setBranchCode(rs.getString("BranchCode"));
			pd.setBankCode(rs.getString("BankCode"));
			pd.setInstrumentType(rs.getString("InstrumentType"));
			pd.setEmployeeNo(rs.getString("EmployeeNo"));
			pd.setEmployerId(JdbcUtil.getLong(rs.getObject("EmployerId")));
			pd.setEmployerName(rs.getString("EmployerName"));
			return pd;
		});
	}

	private String getSql() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, HeaderID, DueDate, FinId, FinReference, FinType, ProductCategory, FinBranch, EntityCode");
		sql.append(", BpiTreatment, GrcPeriodEndDate, GrcAdvType, AdvType, AdvStage");
		sql.append(", SchDate, DefSchdDate, SchSeq, InstNumber, BpiOrHoliday");
		sql.append(", ProfitSchd, PrincipalSchd, FeeSchd, TdsAmount");
		sql.append(", SchdPftPaid, SchdPriPaid, SchdFeePaid, TdsPaid");
		sql.append(", MandateId, MandateType, EmandateSource, MandateStatus, MandateExpiryDate");
		sql.append(", ChequeId, ChequeType, ChequeStatus, ChequeDate");
		sql.append(", PartnerBankId, BranchCode, BankCode, InstrumentType");
		sql.append(", EmployeeNo, EmployerId, EmployerName, ChequeAmount");

		return sql.toString();
	}
}
