package com.pennanttech.pff.eod.collateral.reval;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pff.eod.collateral.reval.model.CollateralRevaluation;

public class CollateralRevaluationProcessor extends BasicDao<CollateralRevaluation>
		implements ItemProcessor<CollateralRevaluation, CollateralRevaluation> {

	public CollateralRevaluation process(CollateralRevaluation collateral) throws Exception {
		String table = "COLLATERAL_".concat(collateral.getCollateralType()).concat("_ED");

		collateral.setTableName(table);

		setCurrentValue(collateral);

		BigDecimal numberOfUnits = collateral.getNoOfUnits();
		BigDecimal currentCollateralValue = collateral.getMarketValue();
		currentCollateralValue = currentCollateralValue.multiply(numberOfUnits);
		collateral.setCurrentCollateralValue(currentCollateralValue);

		BigDecimal osp = collateral.getPos();

		// setting Updated LTV
		BigDecimal currentBankLTV = osp.divide(currentCollateralValue, 0, RoundingMode.HALF_DOWN);
		collateral.setCurrentBankLTV(currentBankLTV);

		BigDecimal currentBankValuation = currentCollateralValue.multiply(currentBankLTV).divide(new BigDecimal(100), 0,
				RoundingMode.HALF_DOWN);

		collateral.setCurrentBankValuation(currentBankValuation);

		return collateral;
	}

	public void setCurrentValue(final CollateralRevaluation collateralDetails) {
		StringBuilder sql = new StringBuilder();
		sql.append("select NoOfUnits, UnitPrice from ");
		sql.append(collateralDetails.getTableName());
		sql.append(" where reference = :reference");

		MapSqlParameterSource source = new MapSqlParameterSource();

		source.addValue("reference", collateralDetails.getCollateralRef());

		jdbcTemplate.query(sql.toString(), source, new RowCallbackHandler() {
			public void processRow(ResultSet rs) throws SQLException {
				collateralDetails.setNoOfUnits(rs.getBigDecimal(1));
				collateralDetails.setUnitPrice(rs.getBigDecimal(2));
			}
		});

	}
}