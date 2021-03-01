package com.pennanttech.pff.external.pan.dao.impl;

import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.model.PrimaryAccount;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.pan.dao.PrimaryAccountDAO;

public class PrimaryAccountDAOImpl extends BasicDao<PrimaryAccount> implements PrimaryAccountDAO {
	private static Logger logger = LogManager.getLogger(PrimaryAccountDAOImpl.class);

	public PrimaryAccountDAOImpl() {
		super();
	}

	/**
	 * This method insert new Records into cust_kyv_validation.
	 *
	 * save cust_kyv_validation
	 * 
	 * @param PrimaryAccount
	 *            (primaryAccount)
	 * 
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void savePanVerificationDetails(PrimaryAccount primaryAccount) {
		logger.debug(Literal.ENTERING);
		primaryAccount.setType("PAN");
		primaryAccount.setDocumentNumber(primaryAccount.getPanNumber());

		if (StringUtils.isNotBlank(primaryAccount.getCustFName())) {
			primaryAccount.setDocumentName(primaryAccount.getCustFName());
		}

		if (StringUtils.isNotBlank(primaryAccount.getCustMName())) {
			primaryAccount.setDocumentName(primaryAccount.getDocumentName().concat(primaryAccount.getCustMName()));
		}

		if (StringUtils.isNotBlank(primaryAccount.getCustLName())) {
			primaryAccount.setDocumentName(primaryAccount.getDocumentName().concat(primaryAccount.getCustLName()));
		}

		primaryAccount.setVerifiedOn(new Timestamp(System.currentTimeMillis()));
		StringBuilder sql = new StringBuilder("Insert Into CUST_KYC_VALIDATION");
		sql.append("(Type, Document_Number, Document_Name, Issued_Country, Issued_Authority");
		sql.append(", Issued_On, Expiry_Date, Verified_on, Verified, Remarks )");
		sql.append(" Values(:Type, :DocumentNumber, :DocumentName, :IssuedCountry, :IssuedAuthority");
		sql.append(", :IssuedOn, :ExpiryDate, :VerifiedOn, :Verified, :Remarks )");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(primaryAccount);
		try {
			this.jdbcTemplate.update(sql.toString(), beanParameters);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Fetch the Record PrimaryAccount details by key field
	 * 
	 * @param panNo
	 * 
	 */
	@Override
	public int isPanVerified(String panNo) {
		logger.debug(Literal.ENTERING);

		PrimaryAccount primaryAccount = new PrimaryAccount();
		primaryAccount.setDocumentNumber(panNo);
		StringBuilder selectSql = new StringBuilder("SELECT Count(*)");
		selectSql.append(" from CUST_KYC_VALIDATION");
		selectSql.append(" Where Document_Number =:DocumentNumber");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(primaryAccount);
		int recordCount = 0;
		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (Exception e) {
			logger.debug("Exception: ", e);
			recordCount = 0;
		}
		logger.debug(Literal.LEAVING);

		return recordCount;
	}

	@Override
	public PrimaryAccount getPrimaryAccountDetails(String primaryID) {

		logger.debug(Literal.ENTERING);
		PrimaryAccount primaryAccount = new PrimaryAccount();
		primaryAccount.setDocumentNumber(primaryID);
		StringBuilder sql = new StringBuilder("select Document_Number, Document_Name");
		sql.append(" from CUST_KYC_VALIDATION");
		sql.append(" Where Document_Number =:DocumentNumber");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(primaryAccount);
		RowMapper<PrimaryAccount> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PrimaryAccount.class);
		try {
			primaryAccount = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			primaryAccount = null;
		}
		logger.debug(Literal.LEAVING);
		return primaryAccount;

	}
}
