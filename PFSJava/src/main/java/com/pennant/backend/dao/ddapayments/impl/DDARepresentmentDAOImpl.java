package com.pennant.backend.dao.ddapayments.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.ddapayments.DDARepresentmentDAO;
import com.pennant.backend.model.ddapayments.DDAPayments;
import com.pennant.backend.model.finance.DdaPresentment;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

public class DDARepresentmentDAOImpl extends SequenceDao<DDAPayments> implements DDARepresentmentDAO {
private static Logger logger = Logger.getLogger(DDARepresentmentDAOImpl.class);


	public DDARepresentmentDAOImpl() {
		super();
	}

	/**
	 * Method to save the DDA Payment Representment details to DDS Edge table
	 * 
	 */
	@Override
	public void save(DDAPayments ddaRepresentment) {
		logger.debug("Entering");

		if (ddaRepresentment.getId() == 0
				|| ddaRepresentment.getId() == Long.MIN_VALUE) {
			ddaRepresentment.setDdaSeqId(getNextId("SeqDDS_PFF_DD500_SETTLED"));
		}

		ddaRepresentment.setDirectDebitRefNo(ddaRepresentment
				.getdDAReferenceNo() + ddaRepresentment.getDdaSeqId());

		StringBuilder insertSql = new StringBuilder("Insert Into DDS_PFF_DD500_SETTLED");
		insertSql.append(" (DDARefNo, DirectDebitRefNo, PFFData)");
		insertSql.append(" Values(:DDARefNo, :DirectDebitRefNo, :PFFData)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(ddaRepresentment);
		this.jdbcTemplate.update(insertSql.toString(),
				beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Method to save the DDA-Representment details to PFF Log tables
	 * 
	 */
	@Override
	public void logRepresentmentData(DDAPayments ddaRepresentment) {
		logger.debug("Entering");

		ddaRepresentment.setDirectDebitRefNo(ddaRepresentment
				.getdDAReferenceNo() + ddaRepresentment.getDdaSeqId());

		StringBuilder insertSql = new StringBuilder(
				"Insert Into DDS_PFF_DD500_SETTLED_LOG");
		insertSql.append(" (DDARefNo, DirectDebitRefNo, PFFData)");
		insertSql.append(" Values(:DDARefNo, :DirectDebitRefNo, :PFFData)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				ddaRepresentment);
		this.jdbcTemplate.update(insertSql.toString(),
				beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public void represent(List<DdaPresentment> list) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder("update DDS_PFF_DD500_LOG ");
		sql.append("set DDARefNo = :DDARefNo ");
		sql.append("where DirectDebitRefNo = :HostReference");

		for (DdaPresentment presentment : list) {
			SqlParameterSource source = new BeanPropertySqlParameterSource(
					presentment);
			this.jdbcTemplate.update(sql.toString(), source);
		}

		logger.debug("Leaving");
	}

	
}
