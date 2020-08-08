package com.pennanttech.pff.eod.auto.knockoff.reval;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.model.finance.AutoKnockOffExcess;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;

import AutoKnockOffExcess.AutoKnockOffExcessDetails;

public class AutoKnockOffProcessRowMapper implements RowMapper<AutoKnockOffExcess> {

	@Override
	public AutoKnockOffExcess mapRow(ResultSet rs, int rowNum) throws SQLException {
		AutoKnockOffExcess knockOff = new AutoKnockOffExcess();
		knockOff.setID(rs.getLong("ExcessID"));
		knockOff.setFinReference(rs.getString("FinReference"));
		knockOff.setValueDate(JdbcUtil.getDate(rs.getDate("ValueDate")));
		knockOff.setBalanceAmount(rs.getBigDecimal("BalanceAmount"));
		knockOff.setAmountType(rs.getString("AmountType"));
		knockOff.setPayableID(rs.getLong("PayableId"));
		knockOff.setExecutionDay(rs.getString("ExecutionDay"));
		knockOff.setThresholdValue(rs.getString("ThresholdValue"));

		AutoKnockOffExcessDetails excessDetails = new AutoKnockOffExcessDetails();
		excessDetails.setID(rs.getLong("ID"));
		excessDetails.setExcessID(rs.getLong("ExcessID"));
		excessDetails.setKnockOffID(rs.getLong("KnockOffID"));
		excessDetails.setCode(rs.getString("Code"));
		excessDetails.setDescription(rs.getString("Description"));
		excessDetails.setExecutionDays(rs.getString("ExecutionDays"));
		excessDetails.setFinType(rs.getString("FinType"));
		excessDetails.setFeeTypeCode(rs.getString("FeeTypeCode"));
		excessDetails.setKnockOffOrder(rs.getString("KnockOffOrder"));
		excessDetails.setFeeOrder(rs.getInt("FeeOrder"));
		excessDetails.setFinCcy(rs.getString("FinCcy"));
		excessDetails.setFrhCount(rs.getInt("FrhCount"));
		excessDetails.setFmtCount(rs.getInt("FmtCount"));

		List<AutoKnockOffExcessDetails> excessDts = new ArrayList<>();
		excessDts.add(excessDetails);

		knockOff.setExcessDetails(excessDts);

		return knockOff;
	}

}
