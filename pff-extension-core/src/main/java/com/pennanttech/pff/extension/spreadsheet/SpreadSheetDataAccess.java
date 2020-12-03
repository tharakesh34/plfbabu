package com.pennanttech.pff.extension.spreadsheet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.CreditReviewData;
import com.pennant.backend.model.finance.CreditReviewDetails;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.verification.model.Verification;

public class SpreadSheetDataAccess extends BasicDao<FinCreditReviewDetails> {
	public CreditReviewDetails getCreditReviewDetailsByLoanType(CreditReviewDetails crd) {
		return crd;
	}

	public CreditReviewData getCreditReviewDataByRef(String finReference, CreditReviewDetails crd) {
		return null;
	}

	public List<FinCreditReviewDetails> getFinCreditRevDetailIds(long customerId) {
		FinCreditReviewDetails finCreditReviewDetails = new FinCreditReviewDetails();
		finCreditReviewDetails.setCustomerId(customerId);

		StringBuilder sql = new StringBuilder();
		sql.append("Select DetailId, AuditYear");
		sql.append(" FROM FinCreditReviewDetails_view Where CustomerId= ?");

		List<FinCreditReviewDetails> list = new ArrayList<>();
		try {
			list = jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setLong(1, customerId);

				}
			}, new RowMapper<FinCreditReviewDetails>() {

				@Override
				public FinCreditReviewDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinCreditReviewDetails crd = new FinCreditReviewDetails();
					crd.setDetailId(rs.getLong("DetailId"));
					crd.setAuditYear(rs.getString("AuditYear"));
					return crd;
				}
			});
		} catch (Exception e) {

		}

		return list;
	}

	public String getMaxAuditYearByCustomerId(long customerId, String type) {
		logger.debug("Entering");
		FinCreditReviewDetails creditReviewDetails = new FinCreditReviewDetails();
		creditReviewDetails.setCustomerId(customerId);
		int maxAuditYear = 0;
		SqlParameterSource beanParameters;

		StringBuilder selectSql = new StringBuilder(
				"SELECT COALESCE(Max(AuditYear), '0') FROM  FinCreditReviewDetails_View ");
		selectSql.append(" where CustomerId = :CustomerId");
		logger.debug("selectSql: " + selectSql.toString());
		beanParameters = new BeanPropertySqlParameterSource(creditReviewDetails);
		try {
			maxAuditYear = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return String.valueOf(maxAuditYear);
	}

	public Map<String, Object> getFinCreditRevSummaryDetails(long detailid) {
		Map<String, Object> map = new HashMap<>();

		StringBuilder sql = new StringBuilder();
		sql.append("select SubcategoryCode, ItemValue from Fincreditreviewsummary_View");
		sql.append(" Where DetailId = :DetailId");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("DetailId", detailid);

		try {
			this.jdbcTemplate.query(sql.toString(), source, new RowMapper<Map<String, Object>>() {

				@Override
				public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
					map.put(rs.getString("SubcategoryCode"), rs.getBigDecimal("ItemValue"));
					return map;
				}
			});
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		return map;
	}

	public Customer getCustomerDetailForFinancials(String custCIF, String tableType) {
		logger.debug(Literal.ENTERING);
		Customer customer = new Customer();
		customer.setCustCIF(custCIF);

		StringBuilder selectSql = new StringBuilder("SELECT CustFName, CustMName, CustLName, CustShrtName, CustDOB,");
		selectSql.append("lovDescCustTypeCodeName, custCtgCode");

		selectSql.append(" FROM  Customers");
		selectSql.append(StringUtils.trimToEmpty(tableType));
		selectSql.append(" Where CustCIF = :CustCIF");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);

		try {
			customer = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			customer = null;
		}
		logger.debug(Literal.LEAVING);
		return customer;
	}

	public String getExtFieldIndustryMargin(String tableName, String type, String industry, String segment,
			String product) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Type", type);
		source.addValue("Industry", industry);
		source.addValue("Segment", segment);
		source.addValue("Product", product);

		StringBuilder sql = new StringBuilder("SELECT Margin From ".concat(tableName));
		sql.append(" where Type = :Type and");
		sql.append(" Industry = :Industry and");
		sql.append(" Segment = :Segment and");
		sql.append(" Product = :Product");

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
		}
		return null;
	}

	public String getExtFieldDesc(String tableName, String value) {

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("value", value);

		StringBuilder sql = new StringBuilder("SELECT Value From ".concat(tableName));
		sql.append(" where key = :value");

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
		}
		return null;
	}

	public List<Map<String, Object>> getExtendedFieldMap(String reference, String tableName, String type) {
		logger.debug(Literal.ENTERING);

		List<Map<String, Object>> renderMap = null;

		type = StringUtils.trimToEmpty(type);

		type = type.toLowerCase();

		StringBuilder sql = new StringBuilder();
		if (StringUtils.startsWith(type, "_view")) {
			sql.append("select * from (select * from ");
			sql.append(tableName);
			sql.append("_temp");
			sql.append(" t1  union all select * from ");
			sql.append(tableName);
			sql.append(" t1  where not exists (select 1 from ");
			sql.append(tableName);
			sql.append("_temp");
			sql.append(" where reference = t1.reference and seqno = t1.seqno)) t where t.reference = :reference ");
		} else {
			sql.append("select * from ");
			sql.append(tableName);
			sql.append(StringUtils.trimToEmpty(type));
			sql.append(" where reference = :reference order by seqno");
		}
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("reference", reference);
		try {
			renderMap = this.jdbcTemplate.queryForList(sql.toString(), source);
		} catch (Exception e) {
			logger.error(Literal.ENTERING, e);
			renderMap = new ArrayList<>();
		}

		logger.debug(Literal.LEAVING);
		return renderMap;
	}

	public Verification getVerificationStatus(String reference, int verificationType, String addressType,
			String custCif) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("select status from verifications");
		sql.append(
				" where keyReference = :keyReference and verificationType = :verificationType and referencefor = :referencefor and Reference =:Reference");

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("keyReference", reference);
		paramMap.addValue("verificationType", verificationType);
		paramMap.addValue("referencefor", addressType);
		paramMap.addValue("Reference", custCif);
		RowMapper<Verification> rowMapper = BeanPropertyRowMapper.newInstance(Verification.class);
		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramMap, rowMapper);
		} catch (Exception e) {
			logger.error(e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

}
