package com.pennant.backend.dao.finance.putcall.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.putcall.FinOptionDAO;
import com.pennant.backend.model.finance.finoption.FinOption;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class FinOptionDAOImpl extends SequenceDao<FinOption> implements FinOptionDAO {
	private static Logger logger = LogManager.getLogger(FinOptionDAOImpl.class);

	@Override
	public String save(FinOption finoption, TableType tableType) {
		// Prepare the SQL.
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder();

		sql.append("Insert into FIN_OPTIONS");
		sql.append(tableType.getSuffix());
		sql.append("(Id, FinReference, OptionType, currentOptionDate, Frequency, NoticePeriodDays");
		sql.append(", AlertDays, OptionExercise, NextOptionDate, AlertType, AlertToRoles");
		sql.append(", UserTemplate, CustomerTemplate, Remarks");
		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values");
		sql.append("(:Id, :FinReference, :OptionType, :currentOptionDate, :Frequency, :NoticePeriodDays");
		sql.append(", :AlertDays, :OptionExercise, :NextOptionDate, :AlertType ");
		sql.append(", :AlertToRoles, :UserTemplate, :CustomerTemplate, :Remarks");
		sql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode");
		sql.append(", :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (finoption.getId() == Long.MIN_VALUE) {
			finoption.setId(getNextValue("SEQ_FIN_OPTIONS"));
		}

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(finoption);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(finoption.getId());
	}

	@Override
	public void update(FinOption finoption, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();

		sql.append("Update FIN_OPTIONS");
		sql.append(tableType.getSuffix());
		sql.append("  Set currentOptionDate = :currentOptionDate");
		sql.append(", Frequency = :Frequency, NoticePeriodDays = :NoticePeriodDays, AlertDays = :AlertDays");
		sql.append(", OptionExercise = :OptionExercise, NextOptionDate = :NextOptionDate, AlertType = :AlertType");
		sql.append(", AlertToRoles =:AlertToRoles, UserTemplate =:UserTemplate, CustomerTemplate =:CustomerTemplate");
		sql.append(", Remarks = :Remarks, LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode");
		sql.append(", NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId");
		sql.append(", RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where Id = :Id ");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(finoption);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);

	}

	@Override
	public void delete(FinOption finoption, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("delete from FIN_OPTIONS");
		sql.append(tableType.getSuffix());
		sql.append(" where id = :id ");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(finoption);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public FinOption getFinOption(long id, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSelectQuery(tableType);
		sql.append(tableType.getSuffix());
		sql.append(" Where id = :id");

		logger.trace(Literal.SQL + sql.toString());

		FinOption finOption = new FinOption();
		finOption.setId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(finOption);
		RowMapper<FinOption> rowMapper = BeanPropertyRowMapper.newInstance(FinOption.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public boolean isDuplicateKey(FinOption finOption, TableType tableType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<FinOption> getPutCallAlertList() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();

		sql.append("select distinct fo.id, fo.FinReference, fo.CurrentOptionDate, fo.AlertDays");
		sql.append(", fo.NoticePeriodDays, foa.AlertSentOn");
		sql.append(", ut.TemplateCode UserTemplateCode, cust.TemplateCode CustomerTemplateCode");
		sql.append(", fo.AlertToRoles, fo.OptionType");
		sql.append(", fo.Frequency, fo.NextOptionDate, fo.AlertType, fpd.TotalPriBal, fpd.PenaltyPaid");
		sql.append(", fpd.pftAmz, fpd.TdSchdPftBal, fpd.PftAccrued, fpd.PenaltyDue,  fpd.PenaltyWaived ");
		sql.append(" from fin_options fo");
		sql.append(" inner join finpftdetails fpd on fpd.finreference = fo.finreference");
		sql.append(" left join (select finOptiontId, max(alertsentOn) alertsentOn from fin_option_alerts");
		sql.append(" group by finOptiontId) foa on foa.finOptiontId = fo.id");
		sql.append(" left join Templates ut on ut.TemplateId = fo.UserTemplate");
		sql.append(" left join Templates cust on cust.TemplateId = fo.CustomerTemplate");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();

		RowMapper<FinOption> typeRowMapper = BeanPropertyRowMapper.newInstance(FinOption.class);

		try {
			return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public List<FinOption> getFinOptions(String finreference, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSelectQuery(tableType);
		sql.append(tableType.getSuffix());
		sql.append(" Where FinReference = :FinReference");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finreference);

		RowMapper<FinOption> rowMapper = BeanPropertyRowMapper.newInstance(FinOption.class);

		try {
			return jdbcTemplate.query(sql.toString(), source, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	private StringBuilder getSelectQuery(TableType tableType) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select Id, FinReference, OptionType, CurrentOptionDate, Frequency, NoticePeriodDays");
		sql.append(", AlertDays, OptionExercise, NextOptionDate, AlertType ");
		sql.append(", AlertToRoles,  UserTemplate, CustomerTemplate, Remarks");
		if (tableType.getSuffix().contains("View")) {
			sql.append(", CustomerTemplateCode, UserTemplateCode ");
		}
		sql.append(", Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From FIN_OPTIONS");
		return sql;
	}

	@Override
	public void deleteByFinRef(String loanReference, String tableType) {
		logger.debug("Entering");
		FinOption finOption = new FinOption();
		finOption.setFinReference(loanReference);

		StringBuilder deleteSql = new StringBuilder("Delete From FIN_OPTIONS");
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" Where FinReference = :FinReference ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finOption);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");

	}

}
