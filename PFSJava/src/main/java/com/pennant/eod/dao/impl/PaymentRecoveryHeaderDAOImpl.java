package com.pennant.eod.dao.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.eod.beans.PaymentRecoveryHeader;
import com.pennant.eod.dao.PaymentRecoveryHeaderDAO;
import com.pennanttech.pennapps.core.jdbc.BasicDao;

public class PaymentRecoveryHeaderDAOImpl extends BasicDao<PaymentRecoveryHeader> implements PaymentRecoveryHeaderDAO {
	private static Logger logger = LogManager.getLogger(PaymentRecoveryHeaderDAOImpl.class);

	@Override
	public void save(PaymentRecoveryHeader header) {
		logger.debug(" Entering ");
		StringBuilder insertSql = new StringBuilder("Insert Into PaymentRecoveryHeader");
		insertSql.append("(BatchRefNumber,BatchType,FileName,FileCreationDate,NumberofRecords) Values ");
		insertSql.append("(:BatchRefNumber, :BatchType, :FileName, :FileCreationDate, :NumberofRecords)");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

	}

	@Override
	public void updateCount(PaymentRecoveryHeader header) {
		logger.debug(" Entering ");
		StringBuilder insertSql = new StringBuilder("Update PaymentRecoveryHeader");
		insertSql.append(
				" set NumberofRecords = (select COUNT(*) from PaymentRecoveryDetail where BatchRefNumber=:BatchRefNumber)  ");
		insertSql.append(" where BatchRefNumber = :BatchRefNumber");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

	}

	@Override
	public PaymentRecoveryHeader getPaymentRecoveryHeader(String batchReferenceNumber) {
		logger.debug(" Entering ");

		PaymentRecoveryHeader header = new PaymentRecoveryHeader();
		header.setBatchRefNumber(batchReferenceNumber);

		StringBuilder selectSql = new StringBuilder(
				"SELECT BatchRefNumber,BatchType,FileName,FileCreationDate,NumberofRecords");
		selectSql.append(" From PaymentRecoveryHeader  where BatchRefNumber=:BatchRefNumber");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header);
		RowMapper<PaymentRecoveryHeader> typeRowMapper = BeanPropertyRowMapper.newInstance(PaymentRecoveryHeader.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		}
		return null;
	}
}
