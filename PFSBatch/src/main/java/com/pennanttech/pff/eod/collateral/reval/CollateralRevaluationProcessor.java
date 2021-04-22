package com.pennanttech.pff.eod.collateral.reval;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pff.eod.collateral.reval.model.CollateralRevaluation;
import com.pennanttech.pff.eod.step.StepUtil;

public class CollateralRevaluationProcessor extends BasicDao<CollateralRevaluation>
		implements ItemProcessor<CollateralRevaluation, CollateralRevaluation> {

	public CollateralRevaluation process(CollateralRevaluation collateral) throws Exception {

		long processedRecords = StepUtil.COLLATERAL_REVALUATION.getProcessedRecords();
		processedRecords = processedRecords + 1;
		StepUtil.COLLATERAL_REVALUATION.setProcessedRecords(processedRecords);

		String table = "COLLATERAL_".concat(collateral.getCollateralType()).concat("_ED");

		collateral.setTableName(table);

		List<CollateralRevaluation> collData = getCurrentValue(collateral);

		BigDecimal collcurrentValue = BigDecimal.ZERO;
		for (CollateralRevaluation hsnData : collData) {
			collcurrentValue = collcurrentValue.add(hsnData.getCurrentValue().multiply(hsnData.getNoOfUnits()));
		}

		collateral.setCollHSNData(collData);
		collateral.setUnitPrice(BigDecimal.ZERO);
		collateral.setNoOfUnits(BigDecimal.ZERO);

		collateral.setCurrentCollateralValue(collcurrentValue);

		BigDecimal osp = collateral.getPos();

		// setting Updated LTV
		BigDecimal currentBankLTV = osp.divide(collcurrentValue, 9, RoundingMode.HALF_DOWN);
		currentBankLTV = currentBankLTV.multiply(new BigDecimal(100));
		collateral.setCurrentBankLTV(currentBankLTV);

		BigDecimal currentBankValuation = collcurrentValue.multiply(currentBankLTV).divide(new BigDecimal(100), 0,
				RoundingMode.HALF_DOWN);

		collateral.setCurrentBankValuation(currentBankValuation);

		return collateral;
	}

	public List<CollateralRevaluation> getCurrentValue(final CollateralRevaluation collateralDetails) {
		List<CollateralRevaluation> collData = new ArrayList<>();

		StringBuilder sql = new StringBuilder();
		sql.append("select ce.NoOfUnits, ce.UnitPrice, ce.HSNCode, co.currentValue, ce.reference As CollateralRef");
		sql.append(", ut.TemplateCode userTemplateCode, cust.TemplateCode customerTemplateCode");
		sql.append(", co.alertToRoles from ");
		sql.append(" ");
		sql.append(collateralDetails.getTableName());
		sql.append(" ce inner join commodities co on co.hsnCode = ce.hsnCode");
		sql.append(" left join Templates ut on ut.TemplateId = co.userTemplate");
		sql.append(" left join Templates cust on cust.TemplateId = co.customertemplate");
		sql.append(" where reference = :reference");

		MapSqlParameterSource source = new MapSqlParameterSource();

		source.addValue("reference", collateralDetails.getCollateralRef());

		RowMapper<CollateralRevaluation> typeRowMapper = BeanPropertyRowMapper.newInstance(CollateralRevaluation.class);
		try {
			collData = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {

		}

		return collData;
	}
}