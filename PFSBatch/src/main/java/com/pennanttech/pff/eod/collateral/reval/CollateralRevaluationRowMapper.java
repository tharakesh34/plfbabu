package com.pennanttech.pff.eod.collateral.reval;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.pennanttech.pff.eod.collateral.reval.model.CollateralRevaluation;

public class CollateralRevaluationRowMapper implements RowMapper<CollateralRevaluation> {

	public CollateralRevaluation mapRow(ResultSet rs, int rowNum) throws SQLException {
		CollateralRevaluation cr = new CollateralRevaluation();

		cr.setId(rs.getLong("Id"));
		cr.setBatchId(rs.getLong("BatchId"));
		cr.setFinReference(rs.getString("FinReference"));
		cr.setFinID(rs.getLong("FinID"));
		cr.setCollateralType(rs.getString("CollateralType"));
		cr.setCollateralRef(rs.getString("CollateralRef"));
		cr.setCollateralCCY(rs.getString("CollateralCCY"));
		cr.setCollateralValue(rs.getBigDecimal("CollateralValue"));
		cr.setMarketValue(rs.getBigDecimal("MarketValue"));
		cr.setBankLTV(rs.getBigDecimal("BankLTV"));
		cr.setThresholdLTV(rs.getBigDecimal("ThresholdLTV"));
		cr.setCommodityId(rs.getInt("CommodityId"));
		cr.setPos(rs.getBigDecimal("POS"));
		cr.setValueDate(rs.getTimestamp("ValueDate"));
		cr.setAlertToRoles(rs.getString("AlertToRoles"));
		cr.setUserTemplateCode(rs.getString("UserTemplateCode"));
		cr.setCustomerTemplateCode(rs.getString("CustomerTemplateCode"));

		return cr;

	}

}
