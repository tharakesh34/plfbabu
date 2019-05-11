package com.pennant.backend.dao.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.LMSServiceLog;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

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
		insertSql.append(
				" RecalFromDate, RecalToDate, PftIntact, Terms, ServiceReqNo, Remarks, PftChg, InstructionUID, LinkedTranID )");
		insertSql.append(" Values(:ServiceSeqId, :FinEvent, :FinReference, :FromDate, :ToDate,");
		insertSql.append(" :PftDaysBasis, :SchdMethod, :ActualRate, :BaseRate, :SplRate, :Margin, :GrcPeriodEndDate,");
		insertSql.append(" :RepayPftFrq, :RepayRvwFrq, :RepayCpzFrq, :GrcPftFrq, :GrcRvwFrq, :GrcCpzFrq,");
		insertSql.append(" :NextGrcRepayDate, :RepayFrq, :NextRepayDate, :Amount,");
		insertSql.append(
				" :RecalType, :RecalFromDate, :RecalToDate, :PftIntact, :Terms, :ServiceReqNo, :Remarks, :PftChg, :InstructionUID, :LinkedTranID)");

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
		insertSql.append(
				" RecalFromDate, RecalToDate, PftIntact, Terms, ServiceReqNo, Remarks, PftChg, InstructionUID, LinkedTranID )");
		insertSql.append(" Values(:ServiceSeqId, :FinEvent, :FinReference, :FromDate, :ToDate,");
		insertSql.append(" :PftDaysBasis, :SchdMethod, :ActualRate, :BaseRate, :SplRate, :Margin, :GrcPeriodEndDate,");
		insertSql.append(" :RepayPftFrq, :RepayRvwFrq, :RepayCpzFrq, :GrcPftFrq, :GrcRvwFrq, :GrcCpzFrq,");
		insertSql.append(" :NextGrcRepayDate, :RepayFrq, :NextRepayDate, :Amount,");
		insertSql.append(
				" :RecalType, :RecalFromDate, :RecalToDate, :PftIntact, :Terms, :ServiceReqNo, :Remarks, :PftChg, :InstructionUID, :LinkedTranID)");
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

		StringBuilder sql = new StringBuilder();
		sql.append("select ServiceSeqId, FinEvent, FinReference, FromDate, ToDate");
		sql.append(", PftDaysBasis, SchdMethod, ActualRate, BaseRate, SplRate, Margin, GrcPeriodEndDate");
		sql.append(", NextGrcRepayDate, RepayPftFrq, RepayRvwFrq, RepayCpzFrq, GrcPftFrq, GrcRvwFrq, GrcCpzFrq");
		sql.append(", RepayFrq, NextRepayDate, Amount, RecalType, RecalFromDate, RecalToDate, PftIntact, Terms");
		sql.append(", ServiceReqNo, Remarks, PftChg, InstructionUID, LinkedTranID");
		sql.append(" From FinServiceInstruction");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference =:FinReference AND FinEvent =:FinEvent ");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("FinReference", finReference);
		parameterSource.addValue("FinEvent", finEvent);

		RowMapper<FinServiceInstruction> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinServiceInstruction.class);

		logger.debug(Literal.LEAVING);
		try {
			return this.jdbcTemplate.query(sql.toString(), parameterSource, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		return new ArrayList<>();
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
				",RepayFrq, NextRepayDate, Amount, RecalType, RecalFromDate, RecalToDate, PftIntact, Terms ,ServiceReqNo,Remarks, PftChg, InstructionUID, LinkedTranID ");
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
				",RepayFrq, NextRepayDate, Amount, RecalType, RecalFromDate, RecalToDate, PftIntact, Terms ,ServiceReqNo,Remarks, PftChg, InstructionUID, LinkedTranID ");
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
		logger.debug("Entering");
		FinServiceInstruction finServiceInstruction = new FinServiceInstruction();
		finServiceInstruction.setFinReference(finReference);
		finServiceInstruction.setServiceReqNo(serviceReqNo);

		StringBuilder selectSql = new StringBuilder("Select ServiceSeqId, FinEvent, FinReference, FromDate,ToDate");
		selectSql.append(
				",PftDaysBasis,SchdMethod, ActualRate, BaseRate, SplRate, Margin, GrcPeriodEndDate,NextGrcRepayDate");
		selectSql.append(",RepayPftFrq, RepayRvwFrq, RepayCpzFrq, GrcPftFrq, GrcRvwFrq, GrcCpzFrq");
		selectSql.append(
				",RepayFrq, NextRepayDate, Amount, RecalType, RecalFromDate, RecalToDate, PftIntact, Terms ,ServiceReqNo,Remarks, PftChg, InstructionUID, LinkedTranID ");
		selectSql.append(
				" From FinServiceInstruction Where FinReference =:FinReference AND ServiceReqNo =:ServiceReqNo ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finServiceInstruction);
		RowMapper<FinServiceInstruction> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinServiceInstruction.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public List<LMSServiceLog> getLMSServiceLogList(String notificationFlag) {
		logger.debug(Literal.ENTERING);
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		
		StringBuilder sql = new StringBuilder("Select Event, FinReference, OldRate, NewRate, EffectiveDate,");
		sql.append(" NotificationFlag From LMSServiceLog Where  NotificationFlag = :NotificationFlag ");
		logger.debug(Literal.SQL + sql.toString());
		
		source.addValue("NotificationFlag", notificationFlag);
		
		RowMapper<LMSServiceLog> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LMSServiceLog.class);
		
		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	@Override
	public void updateNotificationFlag(String finReference, String notificationFlag) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder updateSql = new StringBuilder("update LMSServiceLog set NotificationFlag = :NotificationFlag");
		updateSql.append(" Where FinReference =:FinReference");
		logger.debug(Literal.SQL + updateSql.toString());

		source.addValue("NotificationFlag", notificationFlag);

		try {
			this.jdbcTemplate.update(updateSql.toString(), source);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public BigDecimal getOldRate(String finReference, Date schdate) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select calculatedrate from finscheduledetails where Finreference = :FinReference ");
		sql.append("and Schdate = (select max(schdate) from finscheduledetails ");
		sql.append("where Finreference = :FinReference and Schdate <= :Schdate) ");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("FinReference", finReference);
		paramSource.addValue("Schdate", schdate);

		BigDecimal result = BigDecimal.ZERO;
		try {
			result = this.jdbcTemplate.queryForObject(sql.toString(), paramSource, BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
		}
		logger.debug(Literal.LEAVING);
		return result;
	}

	@Override
	public void saveLMSServiceLOGList(List<LMSServiceLog> lmsServiceLog) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Insert into LMSServiceLog");
		sql.append(" (Event, FinReference, OldRate, NewRate, EffectiveDate, NotificationFlag)");
		sql.append(" Values(:Event, :FinReference, :OldRate, :NewRate, :EffectiveDate, :NotificationFlag)");
		
		logger.trace("selectSql: " + sql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(lmsServiceLog.toArray());
		logger.debug("Leaving");
		try {
			this.jdbcTemplate.batchUpdate(sql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}

		logger.debug(Literal.LEAVING);
	}
}
