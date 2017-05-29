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

import com.pennant.backend.dao.ddapayments.DDAPaymentResponseDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ddapayments.DDAPayments;

public class DDAPaymentResponseDAOImpl  extends BasisNextidDaoImpl<DDAPayments> implements DDAPaymentResponseDAO {

	private static Logger logger = Logger.getLogger(DDAPaymentResponseDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public DDAPaymentResponseDAOImpl() {
		super();
	}

	@Override
	public List<DDAPayments> getDDAPaymentResDetails() {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT DDARefNo, DirectDebitRefNo, T24Data");
		selectSql.append(" FROM DDS_PFF_DD503");

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
	 * Method to delete DDA Payment Initiation and Response details
	 * 
	 */
	@Override
	public void deleteDDAPaymentResDetails() {
		logger.debug("Entering");

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("DELETE FROM DDS_PFF_DD503");

		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(DDAPayments.class);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

		// Update Seq table to 0
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("UPDATE SeqDDS_PFF_DD503 SET SeqNo = 0");

		logger.debug("updateSql: " + deleteSql.toString());

		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Method to save the previous day DDA payment's response data
	 * 
	 */
	@Override
	public void logDDAPaymentResDetails(List<DDAPayments> ddaPaymentResList) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into DDS_PFF_DD503_LOG");
		insertSql.append(" (DDARefNo, DirectDebitRefNo, T24Data)");
		insertSql.append(" Values(:DDARefNo, :DirectDebitRefNo, :T24Data)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(ddaPaymentResList.toArray());
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
