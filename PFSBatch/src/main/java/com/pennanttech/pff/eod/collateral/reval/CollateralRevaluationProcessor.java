package com.pennanttech.pff.eod.collateral.reval;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pff.eod.collateral.reval.model.CollateralRevaluation;

public class CollateralRevaluationProcessor extends BasicDao<CollateralRevaluation>
		implements ItemProcessor<CollateralRevaluation, CollateralRevaluation> {

	public CollateralRevaluation process(CollateralRevaluation collateral) throws Exception {

		String table = "COLLATERAL_".concat(collateral.getCollateralType()).concat("_ED");

		collateral.setTableName(table);

		setCurrentValue(collateral);

		BigDecimal collateralValue = collateral.getCollateralValue();
		BigDecimal currentValue = collateral.getMarketValue();
		BigDecimal numberOfUnits = new BigDecimal(collateral.getUnits());
		BigDecimal osp = collateral.getPos();

		// setting Updated LTV
		BigDecimal updatedLTV = osp.divide(currentValue, 2, RoundingMode.HALF_UP);
		collateral.setMarketLTV(updatedLTV);

		currentValue = currentValue.multiply(numberOfUnits);
		BigDecimal percentage = collateral.getMarketLTV().divide(new BigDecimal(100));
		BigDecimal bankValuation = currentValue.multiply(percentage);
		collateral.setBankValuation(bankValuation);
		
		return collateral;
	}

	private void updateCalculatedValue(CollateralRevaluation collateral) {
		StringBuilder sql = new StringBuilder();
		sql.append(" insert into Collateral_setup_log");
		sql.append(" (collateralRef, NoOfUnits, UnitPrice, BatchId, AuditImage, BankLtv,");
		sql.append(" ModifiedBy, ModifiedOn, FinReference)");
		sql.append(" values (:collateralRef, :NoOfUnits, :UnitPrice, :BatchId, :AuditImage, :BankLtv,");
		sql.append(" :ModifiedBy, :ModifiedOn, :FinReference)");

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(collateral);
		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (Exception e) {
			System.out.println(e);
		}
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
				collateralDetails.setUnits(rs.getInt(1));
				collateralDetails.setCollateralValue(rs.getBigDecimal(2));
			}

		});

	}
}