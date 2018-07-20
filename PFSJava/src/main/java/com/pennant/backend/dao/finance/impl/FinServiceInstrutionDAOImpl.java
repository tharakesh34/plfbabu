package com.pennant.backend.dao.finance.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

public class FinServiceInstrutionDAOImpl extends SequenceDao<FinServiceInstruction> implements FinServiceInstrutionDAO {
	private static Logger logger = Logger.getLogger(FinServiceInstrutionDAOImpl.class);

	public FinServiceInstrutionDAOImpl() {
		super();
	}

	public void saveList(List<FinServiceInstruction> finServiceInstructionList, String type) {

		for (FinServiceInstruction finSerList : finServiceInstructionList) {
			if (finSerList.getServiceSeqId() == Long.MIN_VALUE) {
				finSerList.setServiceSeqId(getNextValue("SeqFinInstruction"));
				logger.debug("get NextID:" + finSerList.getServiceSeqId());
			}
		}
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into FinServiceInstruction");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (ServiceSeqId, FinEvent, FinReference, FromDate, ToDate,");
		insertSql.append(" PftDaysBasis, SchdMethod, ActualRate, BaseRate, SplRate, Margin, GrcPeriodEndDate,");
		insertSql.append(" RepayPftFrq, RepayRvwFrq, RepayCpzFrq, GrcPftFrq, GrcRvwFrq, GrcCpzFrq,");
		insertSql.append(" NextGrcRepayDate, RepayFrq, NextRepayDate, Amount, RecalType,");
		insertSql.append(" RecalFromDate, RecalToDate, PftIntact, Terms, ServiceReqNo, Remarks )");
		insertSql.append(" Values(:ServiceSeqId, :FinEvent, :FinReference, :FromDate, :ToDate,");
		insertSql.append(" :PftDaysBasis, :SchdMethod, :ActualRate, :BaseRate, :SplRate, :Margin, :GrcPeriodEndDate,");
		insertSql.append(" :RepayPftFrq, :RepayRvwFrq, :RepayCpzFrq, :GrcPftFrq, :GrcRvwFrq, :GrcCpzFrq,");
		insertSql.append(" :NextGrcRepayDate, :RepayFrq, :NextRepayDate, :Amount,");
		insertSql.append(" :RecalType, :RecalFromDate, :RecalToDate, :PftIntact, :Terms, :ServiceReqNo, :Remarks)");

		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(finServiceInstructionList.toArray());
		logger.debug("Leaving");
		try {
			this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	@Override
	public void deleteList(String finReference, String finEvent, String type) {
		logger.debug("Entering");

		FinServiceInstruction finServiceInstruction = new FinServiceInstruction();
		finServiceInstruction.setFinReference(finReference);
		finServiceInstruction.setFinEvent(finEvent);

		StringBuilder deleteSql = new StringBuilder("Delete From ");
		deleteSql.append(" FinServiceInstruction");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append("  Where  FinReference = :FinReference ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finServiceInstruction);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Fetch the Record Repay Instruction Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return RepayInstruction
	 */
	@Override
	public List<FinServiceInstruction> getFinServiceInstructions(final String finReference, String type,
			String finEvent) {
		logger.debug("Entering");

		FinServiceInstruction finServiceInstruction = new FinServiceInstruction();
		finServiceInstruction.setFinReference(finReference);
		finServiceInstruction.setFinEvent(finEvent);

		StringBuilder selectSql = new StringBuilder("Select ServiceSeqId, FinEvent, FinReference, FromDate,ToDate");
		selectSql.append(
				",PftDaysBasis,SchdMethod, ActualRate, BaseRate, SplRate, Margin, GrcPeriodEndDate,NextGrcRepayDate");
		selectSql.append(",RepayPftFrq, RepayRvwFrq, RepayCpzFrq, GrcPftFrq, GrcRvwFrq, GrcCpzFrq");
		selectSql.append(
				",RepayFrq, NextRepayDate, Amount, RecalType, RecalFromDate, RecalToDate, PftIntact, Terms ,ServiceReqNo,Remarks");
		selectSql.append(" From FinServiceInstruction");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference AND FinEvent =:FinEvent ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finServiceInstruction);
		RowMapper<FinServiceInstruction> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinServiceInstruction.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

}
