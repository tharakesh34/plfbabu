package com.pennant.pff.presentment.dao.impl;

import javax.sql.DataSource;

import org.springframework.batch.item.database.JdbcCursorItemReader;

import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pff.presentment.model.PresentmentDetail;

public class PresentmentItemReader extends JdbcCursorItemReader<PresentmentDetail> {

	public PresentmentItemReader(DataSource dataSource) {
		super.setDataSource(dataSource);
		super.setSql(getSql());
		// super.setVerifyCursorPosition(false);
		super.setRowMapper((rs, rowNum) -> {
			PresentmentDetail pd = new PresentmentDetail();

			try {
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

				pd.setHeaderId(JdbcUtil.getLong(rs.getObject("HeaderID")));

				pd.setInstrumentType(rs.getString("InstrumentType"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return pd;
		});
	}

	@Override
	public String getSql() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinId, FinReference, FinType, ProductCategory, FinBranch, EntityCode");
		sql.append(", BpiTreatment, GrcPeriodEndDate, GrcAdvType, AdvType, AdvStage");
		sql.append(", SchDate, DefSchdDate, SchSeq, InstNumber, BpiOrHoliday");
		sql.append(", ProfitSchd, PrincipalSchd, FeeSchd, TdsAmount");
		sql.append(", SchdPftPaid, SchdPriPaid, SchdFeePaid, TdsPaid");
		sql.append(", MandateId, MandateType, EmandateSource, MandateStatus, MandateExpiryDate");
		sql.append(", ChequeId, ChequeType, ChequeStatus, ChequeDate");
		sql.append(", PartnerBankId, BranchCode, BankCode, HeaderID, InstrumentType");
		sql.append(" From Presentment_Stage");
		sql.append(" Where ProcessingFlag = 0");

		return sql.toString();
	}

}
