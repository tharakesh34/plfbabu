package com.pennant.backend.dao.customermasters.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.customermasters.CustomerCardSalesInfoDAO;
import com.pennant.backend.model.customermasters.CustCardSales;
import com.pennant.backend.model.customermasters.CustCardSalesDetails;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class CustomerCardSalesInfoDAOImpl extends SequenceDao<CustCardSales> implements CustomerCardSalesInfoDAO {
	private static Logger logger = Logger.getLogger(CustomerCardSalesInfoDAOImpl.class);

	public CustomerCardSalesInfoDAOImpl() {
		super();
	}

	@Override
	public CustCardSales getCustomerCardSalesInfoById(long id, String type) {
		CustCardSales customerCardSalesInfo = new CustCardSales();
		customerCardSalesInfo.setId(id);
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT Id, MerchantId, CustID");
		if (type.contains(" View")) {
			sql.append(" ");
		}
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId ");
		sql.append(" FROM  CUSTCARDSALES");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where MerchantId = :MerchantId");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerCardSalesInfo);
		RowMapper<CustCardSales> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustCardSales.class);

		try {
			customerCardSalesInfo = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			customerCardSalesInfo = null;
		}
		return customerCardSalesInfo;
	}

	public List<CustCardSales> getCardSalesInfoByCustomer(final long id, String type) {
		CustCardSales customerCardSalesInfo = new CustCardSales();
		customerCardSalesInfo.setCustID(id);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT Id, MerchantId, CustID");
		if (type.contains("View")) {
			sql.append(" ");
		}
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId ");
		sql.append(" FROM  CUSTCARDSALES");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustID = :CustID");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerCardSalesInfo);
		RowMapper<CustCardSales> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustCardSales.class);

		List<CustCardSales> custCardSalesInformation = this.jdbcTemplate.query(sql.toString(), beanParameters,
				typeRowMapper);

		return custCardSalesInformation;
	}

	@Override
	public void delete(CustCardSales customerCardSalesInfo, String type) {
		int recordCount = 0;
		StringBuilder sql = new StringBuilder(" Delete From CUSTCARDSALES");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where MerchantId = :MerchantId");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerCardSalesInfo);

		try {
			recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	public void deleteByCustomer(long custID, String type) {
		CustCardSales customerCardSalesInfo = new CustCardSales();
		customerCardSalesInfo.setCustID(custID);
		StringBuilder sql = new StringBuilder();
		sql.append(" Delete From CUSTCARDSALES");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustID = :CustID");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerCardSalesInfo);

		try {
			this.jdbcTemplate.update(sql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public long save(CustCardSales customerCardSalesInfo, String type) {

		if (customerCardSalesInfo.getId() == Long.MIN_VALUE) {
			customerCardSalesInfo.setId(getNextValue("SEQCUSTCARDSALES"));
		}
		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into CUSTCARDSALES");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(Id, MerchantId, CustID, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(:Id, :MerchantId, :CustID, :Version, :LastMntBy, :LastMntOn, :RecordStatus");
		sql.append(", :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerCardSalesInfo);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		return customerCardSalesInfo.getId();
	}

	@Override
	public void update(CustCardSales customerCardSalesInfo, String type) {
		int recordCount = 0;

		StringBuilder sql = new StringBuilder();
		sql.append(" Update CUSTCARDSALES");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set MerchantId = :MerchantId");
		sql.append(", Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus");
		sql.append(", RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId");
		sql.append(", RecordType = :RecordType, WorkflowId = :WorkflowId ");
		sql.append(" Where Id = :Id");
		if (!type.endsWith("_Temp")) {
			sql.append(" AND Version= :Version-1");
		}

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerCardSalesInfo);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public int getVersion(long id) {

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("BankId", id);

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT Version FROM CUSTCARDSALES");

		sql.append(" WHERE Id = :Id");

		logger.trace(Literal.SQL + sql.toString());

		int recordCount = 0;
		try {
			recordCount = this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.error(dae);
			recordCount = 0;
		}
		return recordCount;
	}

	@Override
	public CustCardSales getCustomerCardSalesInfoByCustId(CustCardSales customerCardSalesInfo, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT Id, MerchantId, CustID");
		if (type.contains("View")) {
			sql.append(" ");
		}
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId ");
		sql.append(" FROM  CUSTCARDSALES");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustID = :CustID and MerchantId =:MerchantId and Id = :Id ");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerCardSalesInfo);
		RowMapper<CustCardSales> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustCardSales.class);
		try {
			customerCardSalesInfo = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			customerCardSalesInfo = null;
		}
		return customerCardSalesInfo;
	}

	@Override
	public List<CustCardSalesDetails> getCardSalesInfoSubDetailById(long CardSaleId, String type) {
		CustCardSalesDetails cardMonthSalesInfo = new CustCardSalesDetails();
		cardMonthSalesInfo.setCardSalesId(CardSaleId);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT Id, CardSalesId, Month, SalesAmount, NoOfSettlements, TotalNoOfCredits, TotalNoOfDebits");
		sql.append(", TotalCreditValue, TotalDebitValue, InwardBounce, OutwardBounce");
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		sql.append(" FROM  CUSTCARDSALESDETAILS");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CardSalesId = :CardSalesId ");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(cardMonthSalesInfo);
		RowMapper<CustCardSalesDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CustCardSalesDetails.class);

		List<CustCardSalesDetails> bankInfoSubDetails = this.jdbcTemplate.query(sql.toString(), beanParameters,
				typeRowMapper);

		return bankInfoSubDetails;
	}

	@Override
	public long save(CustCardSalesDetails custCardMnthSaleInfo, String type) {
		StringBuilder sql = new StringBuilder();
		if (custCardMnthSaleInfo.getId() == Long.MIN_VALUE) {
			custCardMnthSaleInfo.setId(getNextValue("SEQCUSTCARDSALESDETAILS"));
		}
		sql.append(" Insert Into CUSTCARDSALESDETAILS");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (Id, CardSalesId, Month, SalesAmount, NoOfSettlements, TotalNoOfCredits, TotalNoOfDebits");
		sql.append(", TotalCreditValue, TotalDebitValue, InwardBounce, OutwardBounce");
		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(
				" Values(:Id, :CardSalesId, :Month, :SalesAmount, :NoOfSettlements, :TotalNoOfCredits, :TotalNoOfDebits");
		sql.append(", :TotalCreditValue, :TotalDebitValue, :InwardBounce, :OutwardBounce");
		sql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode");
		sql.append(", :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(custCardMnthSaleInfo);

		this.jdbcTemplate.update(sql.toString(), beanParameters);

		return custCardMnthSaleInfo.getId();
	}

	@Override
	public void update(CustCardSalesDetails custCArdMonthSales, String type) {
		int recordCount = 0;

		StringBuilder sql = new StringBuilder();
		sql.append(" Update CUSTCARDSALESDETAILS");
		sql.append(StringUtils.trimToEmpty(type));

		sql.append(" Set Month = :Month, SalesAmount = :SalesAmount, NoOfSettlements = :NoOfSettlements");
		sql.append(", TotalNoOfCredits = :TotalNoOfCredits, TotalNoOfDebits = :TotalNoOfDebits");
		sql.append(", TotalCreditValue = :TotalCreditValue, TotalDebitValue = :TotalDebitValue");
		sql.append(", InwardBounce = :InwardBounce, OutwardBounce = :OutwardBounce");
		sql.append(", Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn");
		sql.append(", RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode");
		sql.append(", TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId ");
		sql.append(" Where Id = :Id");
		if (!type.endsWith("_Temp")) {
			//sql.append(" AND Version= :Version-1");
		}

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(custCArdMonthSales);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(CustCardSalesDetails custCardSaleInfoDetail, String type) {
		StringBuilder sql = new StringBuilder(" Delete From CUSTCARDSALESDETAILS");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CardSalesId =:CardSalesId");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(custCardSaleInfoDetail);

		this.jdbcTemplate.update(sql.toString(), beanParameters);
	}

	@Override
	public int getCustomerCardSalesInfoByCustMerchantId(long custId, String merchantId, long Id, String type) {
		CustCardSales customerCardSalesInfo = new CustCardSales();
		customerCardSalesInfo.setCustID(custId);
		customerCardSalesInfo.setMerchantId(merchantId);

		StringBuilder sql = new StringBuilder("SELECT COUNT(*)");
		sql.append(" From CUSTCARDSALES");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustID = :custID and MerchantId = :MerchantId And Id != :Id");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerCardSalesInfo);

		try {
			int merchantIdCount = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, Integer.class);
			return merchantIdCount;
		} catch (Exception e) {
			throw e;
		}
	}

}
