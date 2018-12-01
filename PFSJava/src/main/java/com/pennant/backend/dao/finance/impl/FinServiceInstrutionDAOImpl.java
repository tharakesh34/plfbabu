package com.pennant.backend.dao.finance.impl;

import java.util.Date;
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
		logger.debug("Entering");

		for (FinServiceInstruction finSerList : finServiceInstructionList) {
			if (finSerList.getServiceSeqId() == Long.MIN_VALUE) {
				finSerList.setServiceSeqId(getNextValue("SeqFinInstruction"));
				logger.debug("get NextID:" + finSerList.getServiceSeqId());
			}
		}

		StringBuilder insertSql = new StringBuilder("Insert Into FinServiceInstruction");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (ServiceSeqId, FinEvent, FinReference, FromDate, ToDate,");
		insertSql.append(" PftDaysBasis, SchdMethod, ActualRate, BaseRate, SplRate, Margin, GrcPeriodEndDate,");
		insertSql.append(" RepayPftFrq, RepayRvwFrq, RepayCpzFrq, GrcPftFrq, GrcRvwFrq, GrcCpzFrq,");
		insertSql.append(" NextGrcRepayDate, RepayFrq, NextRepayDate, Amount, RecalType,");
		insertSql.append(" RecalFromDate, RecalToDate, PftIntact, Terms, ServiceReqNo, Remarks, PftChg, InstructionUID )");
		insertSql.append(" Values(:ServiceSeqId, :FinEvent, :FinReference, :FromDate, :ToDate,");
		insertSql.append(" :PftDaysBasis, :SchdMethod, :ActualRate, :BaseRate, :SplRate, :Margin, :GrcPeriodEndDate,");
		insertSql.append(" :RepayPftFrq, :RepayRvwFrq, :RepayCpzFrq, :GrcPftFrq, :GrcRvwFrq, :GrcCpzFrq,");
		insertSql.append(" :NextGrcRepayDate, :RepayFrq, :NextRepayDate, :Amount,");
		insertSql.append(
				" :RecalType, :RecalFromDate, :RecalToDate, :PftIntact, :Terms, :ServiceReqNo, :Remarks, :PftChg, :InstructionUID)");

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
	
	/**
	 * 
	 * @param finServiceInstruction
	 * @param type
	 */
	public void save(FinServiceInstruction finServiceInstruction, String type) {
		logger.debug("Entering");

		if (finServiceInstruction.getServiceSeqId() == Long.MIN_VALUE) {
			finServiceInstruction.setServiceSeqId(getNextValue("SeqFinInstruction"));
			logger.debug("get NextID:" + finServiceInstruction.getServiceSeqId());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into FinServiceInstruction");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (ServiceSeqId, FinEvent, FinReference, FromDate, ToDate,");
		insertSql.append(" PftDaysBasis, SchdMethod, ActualRate, BaseRate, SplRate, Margin, GrcPeriodEndDate,");
		insertSql.append(" RepayPftFrq, RepayRvwFrq, RepayCpzFrq, GrcPftFrq, GrcRvwFrq, GrcCpzFrq,");
		insertSql.append(" NextGrcRepayDate, RepayFrq, NextRepayDate, Amount, RecalType,");
		insertSql.append(" RecalFromDate, RecalToDate, PftIntact, Terms, ServiceReqNo, Remarks, PftChg, InstructionUID )");
		insertSql.append(" Values(:ServiceSeqId, :FinEvent, :FinReference, :FromDate, :ToDate,");
		insertSql.append(" :PftDaysBasis, :SchdMethod, :ActualRate, :BaseRate, :SplRate, :Margin, :GrcPeriodEndDate,");
		insertSql.append(" :RepayPftFrq, :RepayRvwFrq, :RepayCpzFrq, :GrcPftFrq, :GrcRvwFrq, :GrcCpzFrq,");
		insertSql.append(" :NextGrcRepayDate, :RepayFrq, :NextRepayDate, :Amount,");
		insertSql.append(" :RecalType, :RecalFromDate, :RecalToDate, :PftIntact, :Terms, :ServiceReqNo, :Remarks, :PftChg, :InstructionUID)");
		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finServiceInstruction);

		try {
			this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}

		logger.debug("Leaving");
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
				",RepayFrq, NextRepayDate, Amount, RecalType, RecalFromDate, RecalToDate, PftIntact, Terms ,ServiceReqNo,Remarks, PftChg, InstructionUID ");
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

	/**
	 * Fetch FinServiceInstruction Detail by finReference,fromDate,finEvent
	 * 
	 * @param finReference
	 * 
	 * @param fromDate
	 * 
	 * @param finEvent
	 * 
	 * @return List<FinServiceInstruction>
	 */
	@Override
	public List<FinServiceInstruction> getFinServiceInstAddDisbDetail(final String finReference, Date fromDate,
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
				",RepayFrq, NextRepayDate, Amount, RecalType, RecalFromDate, RecalToDate, PftIntact, Terms ,ServiceReqNo,Remarks, PftChg, InstructionUID ");
		selectSql.append(" From FinServiceInstruction");
		selectSql.append(" Where FinReference =:FinReference AND FromDate =:FromDate AND FinEvent =:FinEvent ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finServiceInstruction);
		RowMapper<FinServiceInstruction> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinServiceInstruction.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Fetch List of FinServiceInstruction Detail by finEvent and serviceReqNo
	 * 
	 * @param finEvent
	 * 
	 * @param serviceReqNo
	 * 
	 * @return List<FinServiceInstruction>
	 */

	@Override
	public boolean getFinServInstDetails(String finEvent, String serviceReqNo) {
		logger.debug("Entering");
		int count = 0;
		boolean flag = false;
		FinServiceInstruction finServiceInstruction = new FinServiceInstruction();
		finServiceInstruction.setFinEvent(finEvent);
		finServiceInstruction.setServiceReqNo(serviceReqNo);

		StringBuilder selectSql = new StringBuilder("Select count(*)");
		selectSql.append(" From FinServiceInstruction");
		selectSql.append(" Where FinEvent =:FinEvent and ServiceReqNo=:ServiceReqNo");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finServiceInstruction);
		count = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);

		if (count > 0) {
			flag = true;
		}
		logger.debug("Leaving");
		return flag;
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
	public List<FinServiceInstruction> getFinServInstByServiceReqNo(final String finReference, Date fromDate,
			String serviceReqNo, String finEvent) {
		logger.debug("Entering");

		FinServiceInstruction finServiceInstruction = new FinServiceInstruction();
		finServiceInstruction.setFinReference(finReference);
		finServiceInstruction.setServiceReqNo(serviceReqNo);
		finServiceInstruction.setFinEvent(finEvent);
		finServiceInstruction.setFromDate(fromDate);

		StringBuilder selectSql = new StringBuilder("Select ServiceSeqId, FinEvent, FinReference, FromDate,ToDate");
		selectSql.append(
				",PftDaysBasis,SchdMethod, ActualRate, BaseRate, SplRate, Margin, GrcPeriodEndDate,NextGrcRepayDate");
		selectSql.append(",RepayPftFrq, RepayRvwFrq, RepayCpzFrq, GrcPftFrq, GrcRvwFrq, GrcCpzFrq");
		selectSql.append(
				",RepayFrq, NextRepayDate, Amount, RecalType, RecalFromDate, RecalToDate, PftIntact, Terms ,ServiceReqNo,Remarks, PftChg, InstructionUID ");
		selectSql.append(" From FinServiceInstruction Where FinReference =:FinReference AND");
		selectSql.append(" FromDate=:FromDate AND FinEvent =:FinEvent AND ServiceReqNo =:ServiceReqNo ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finServiceInstruction);
		RowMapper<FinServiceInstruction> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinServiceInstruction.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public List<FinServiceInstruction> getFinServiceInstDetailsByServiceReqNo(String finReference,
			String serviceReqNo) {
		FinServiceInstruction finServiceInstruction = new FinServiceInstruction();
		finServiceInstruction.setFinReference(finReference);
		finServiceInstruction.setServiceReqNo(serviceReqNo);

		StringBuilder selectSql = new StringBuilder("Select ServiceSeqId, FinEvent, FinReference, FromDate,ToDate");
		selectSql.append(
				",PftDaysBasis,SchdMethod, ActualRate, BaseRate, SplRate, Margin, GrcPeriodEndDate,NextGrcRepayDate");
		selectSql.append(",RepayPftFrq, RepayRvwFrq, RepayCpzFrq, GrcPftFrq, GrcRvwFrq, GrcCpzFrq");
		selectSql.append(
				",RepayFrq, NextRepayDate, Amount, RecalType, RecalFromDate, RecalToDate, PftIntact, Terms ,ServiceReqNo,Remarks, PftChg, InstructionUID ");
		selectSql.append(
				" From FinServiceInstruction Where FinReference =:FinReference AND ServiceReqNo =:ServiceReqNo ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finServiceInstruction);
		RowMapper<FinServiceInstruction> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinServiceInstruction.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

}
