package com.pennant.eod.dao.impl;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.eod.beans.PaymentRecoveryHeader;
import com.pennant.eod.dao.PaymentRecoveryHeaderDAO;

public class PaymentRecoveryHeaderDAOImpl implements PaymentRecoveryHeaderDAO {

	private static Logger				logger	= Logger.getLogger(PaymentRecoveryHeaderDAOImpl.class);

	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public void save(PaymentRecoveryHeader header) {
		logger.debug(" Entering ");
		StringBuilder insertSql = new StringBuilder("Insert Into PaymentRecoveryHeader");
		insertSql.append("(BatchRefNumber,BatchType,FileName,FileCreationDate,NumberofRecords) Values ");
		insertSql.append("(:BatchRefNumber, :BatchType, :FileName, :FileCreationDate, :NumberofRecords)");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

	}

	@Override
	public void updateCount(PaymentRecoveryHeader header) {
		logger.debug(" Entering ");
		StringBuilder insertSql = new StringBuilder("Update PaymentRecoveryHeader");
		insertSql.append(" set NumberofRecords = (select COUNT(*) from PaymentRecoveryDetail where BatchRefNumber=:BatchRefNumber)  ");
		insertSql.append(" where BatchRefNumber = :BatchRefNumber");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

	}

	@Override
	public PaymentRecoveryHeader getPaymentRecoveryHeader(String batchReferenceNumber) {
		logger.debug(" Entering ");

		PaymentRecoveryHeader header = new PaymentRecoveryHeader();
		header.setBatchRefNumber(batchReferenceNumber);

		StringBuilder selectSql = new StringBuilder("SELECT BatchRefNumber,BatchType,FileName,FileCreationDate,NumberofRecords");
		selectSql.append(" From PaymentRecoveryHeader  where BatchRefNumber=:BatchRefNumber");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header);
		RowMapper<PaymentRecoveryHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PaymentRecoveryHeader.class);

		try {
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		}
		return null;

	}
}
