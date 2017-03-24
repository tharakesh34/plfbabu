package com.pennant.backend.dao.finance.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.OverdraftScheduleDetailDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.finance.OverdraftMovements;
import com.pennant.backend.model.finance.OverdraftScheduleDetail;

public class OverdraftScheduleDetailDAOImpl extends BasisCodeDAO<OverdraftScheduleDetail> implements OverdraftScheduleDetailDAO {

	private static Logger logger = Logger.getLogger(OverdraftScheduleDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public OverdraftScheduleDetailDAOImpl() {
		super();
	}

	@Override
	public void saveList(List<OverdraftScheduleDetail> overdraftScheduleDetail, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into ");

		insertSql.append(" OverdraftScheduleDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, DroplineDate, ActualRate, BaseRate,");
		insertSql.append(" SplRate, Margin, DroplineRate, LimitDrop,ODLimit,LimitIncreaseAmt)");
	
		insertSql.append(" Values(:FinReference, :DroplineDate, :ActualRate, :BaseRate,");
		insertSql.append(" :SplRate, :Margin, :DroplineRate, :LimitDrop, :ODLimit,:LimitIncreaseAmt) ");
	
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils
		        .createBatch(overdraftScheduleDetail.toArray());
		try {
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		} catch(Exception e) {
			logger.error("Exception", e);
			throw e;
		}
		
		logger.debug("Leaving");
	}


	/**
	 * Method for fetch Overdraft Details
	 */
	@Override
	public List<OverdraftScheduleDetail> getOverdraftScheduleDetails(String finRef, String type, boolean isWIF) {
		logger.debug("Entering");

		OverdraftScheduleDetail detail = new OverdraftScheduleDetail();
		
		detail.setFinReference(finRef);
		
		StringBuilder selectSql = new StringBuilder(" Select FinReference, DroplineDate, ActualRate, BaseRate,");
		selectSql.append(" SplRate, Margin, DroplineRate, LimitDrop,ODLimit,LimitIncreaseAmt ");
		if (isWIF) {
			selectSql.append(" From  WIFOverdraftScheduleDetail");
		} else {
			selectSql.append(" From OverdraftScheduleDetail");
		}
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference  order by DroplineDate asc");

		logger.debug("selectSql: " + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				detail);
		RowMapper<OverdraftScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(OverdraftScheduleDetail.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
	}
	
	/**
	 * Method for deletion overdraft schedule details
	 * @param id
	 * @param type
	 * @param isWIF
	 */
	@Override
	public void deleteByFinReference(String id, String type, boolean isWIF) {
		logger.debug("Entering");
		OverdraftScheduleDetail scheduleDetail = new OverdraftScheduleDetail();
		scheduleDetail.setFinReference(id);

		StringBuilder deleteSql = new StringBuilder("Delete From ");
		if (isWIF) {
			deleteSql.append(" WIFOverdraftScheduleDetail");
		} else {
			deleteSql.append(" OverdraftScheduleDetail");
		}

		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
		        scheduleDetail);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	/**
	 * Method to save the overdraft movements
	 * @param Overdraft Movements
	 */
	@Override
	public void saveOverdraftMovement(OverdraftMovements overdraftMovements) {
		logger.debug("Entering");
		
		overdraftMovements.setODSeqID(getodSeqID()+1);
		
		StringBuilder insertSql = new StringBuilder("Insert Into ");
		insertSql.append(" OverdraftMovements");
		insertSql.append(" (FinReference,ODSeqID, DroplineDate, Tenor, ODExpiryDate,");
		insertSql.append(" DroplineFrq, LimitChange, ODLimit,ValueDate)");
		insertSql.append(" Values(:FinReference, :ODSeqID,:DroplineDate, :Tenor, :ODExpiryDate,");
		insertSql.append(" :DroplineFrq, :LimitChange, :ODLimit, :ValueDate) ");
	
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(overdraftMovements);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		
	}
	
	
	private long getodSeqID() {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select COALESCE(max(odSeqID), 0) From OverdraftMovements");
		long odSeq= 0;
		logger.debug("selectSql: " + selectSql.toString());
		try {
			odSeq = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), new MapSqlParameterSource(), Long.class);
			
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			odSeq =  Long.MIN_VALUE;
		}
		logger.debug("Leaving");
		return odSeq;
	}
}