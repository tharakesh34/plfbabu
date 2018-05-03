package com.pennant.backend.dao.ddapayments.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.ddapayments.DDAPaymentInitiateDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ddapayments.DDAPayments;

public class DDAPaymentInitiateDAOImpl extends BasisNextidDaoImpl<DDAPayments> implements DDAPaymentInitiateDAO {

	private static Logger logger = Logger.getLogger(DDAPaymentInitiateDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public DDAPaymentInitiateDAOImpl() {
		super();
	}

	/**
	 * Method to save the DDA Payment Initiation details
	 * 
	 */
	@Override
	public void saveDDAPaymentInitDetails(DDAPayments ddaPaymentInitiation) {
		logger.debug("Entering");

		if(ddaPaymentInitiation.getId()== 0 || ddaPaymentInitiation.getId()==Long.MIN_VALUE){
			ddaPaymentInitiation.setDdaSeqId(getNextidviewDAO().getNextExtId("SeqDDS_PFF_DD500"));	
		}

		ddaPaymentInitiation.setDirectDebitRefNo(ddaPaymentInitiation.getdDAReferenceNo()+ddaPaymentInitiation.getDdaSeqId());

		StringBuilder insertSql = new StringBuilder("Insert Into DDS_PFF_DD500");
		insertSql.append(" (DDARefNo, DirectDebitRefNo, PFFData)");
		insertSql.append(" Values(:DDARefNo, :DirectDebitRefNo, :PFFData)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(ddaPaymentInitiation);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Method to delete DDA Payment Initiation and Response details
	 * 
	 */
	@Override
	public void deleteDDAPaymentInitDetails() {
		logger.debug("Entering");

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("DELETE FROM DDS_PFF_DD500");

		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(DDAPayments.class);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		
		// Update Seq table to 0
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("UPDATE SeqDDS_PFF_DD500 SET SeqNo = 0");

		logger.debug("updateSql: " + deleteSql.toString());

		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Method for Fetch DDA Payment Initiation details
	 * 
	 * @return List<DDAPayments>
	 * 
	 */
	@Override
	public List<DDAPayments> fetchDDAInitDetails() {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT DDARefNo, DirectDebitRefNo, PFFData");
		selectSql.append(" FROM DDS_PFF_DD500");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<DDAPayments> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DDAPayments.class);
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), typeRowMapper);
		} catch(EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return null;
		}
	}

	/**
	 * Method for Log the DDA Payment Initiation details into log table
	 * 
	 */
	@Override
	public void logDDAPaymentInitDetails(List<DDAPayments> backUpDDAPaymentList) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into DDS_PFF_DD500_LOG");
		insertSql.append(" (DDARefNo, DirectDebitRefNo, PFFData)");
		insertSql.append(" Values(:DDARefNo, :DirectDebitRefNo, :PFFData)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(backUpDDAPaymentList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
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

}
