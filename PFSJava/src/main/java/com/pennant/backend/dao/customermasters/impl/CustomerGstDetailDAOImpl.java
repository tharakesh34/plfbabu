package com.pennant.backend.dao.customermasters.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.customermasters.CustomerGstDetailDAO;
import com.pennant.backend.model.customermasters.CustomerGST;
import com.pennant.backend.model.customermasters.CustomerGSTDetails;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class CustomerGstDetailDAOImpl extends SequenceDao<CustomerGST> implements CustomerGstDetailDAO {
	private static Logger logger = Logger.getLogger(CustomerGstDetailDAOImpl.class);

	public CustomerGstDetailDAOImpl() {
		super();
	}

	@Override
	public List<CustomerGST> getCustomerGSTById(long id, String type) {
		logger.debug("Entering");
		List<CustomerGST> customerGSTlist = new ArrayList<>();
		CustomerGST customerGST = new CustomerGST();
		customerGST.setCustId(id);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT Id, CustId, GstNumber, Frequencytype,");
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM  customergst");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustId = :CustId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerGST);
		RowMapper<CustomerGST> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerGST.class);
		try {
			customerGSTlist = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (Exception e) {
			logger.error(e);
		}
		logger.debug("Leaving");

		return customerGSTlist;

	}

	@Override
	public List<CustomerGSTDetails> getCustomerGSTDetailsByCustomer(long headerId, String type) {
		logger.debug("Entering");
		CustomerGSTDetails customerGSTDetails = new CustomerGSTDetails();
		customerGSTDetails.setHeaderId(headerId);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT Id,HeaderId, Frequancy, FinancialYear, SalAmount,");
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM  customergstdetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where HeaderId= :HeaderId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerGSTDetails);
		RowMapper<CustomerGSTDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CustomerGSTDetails.class);

		List<CustomerGSTDetails> customerGstInfoDetails = this.jdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);

		logger.debug("Leaving");
		return customerGstInfoDetails;
	}

	public long save(CustomerGST customerGST, String type) {
		logger.debug(Literal.ENTERING);

		if (customerGST.getId() == Long.MIN_VALUE) {
			customerGST.setId(getNextValue("SEQGSTHEADERDETAILS"));
			logger.debug("get NextID:" + customerGST.getId());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into CustomerGST");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (Id, CustId, GstNumber, FrequencyType ,");
		insertSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		insertSql.append(" NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:Id, :CustId, :GstNumber, :Frequencytype,");
		insertSql.append(" :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		logger.trace(Literal.SQL + insertSql);
		try {
			SqlParameterSource paramSourceGst = new BeanPropertySqlParameterSource(customerGST);
			jdbcTemplate.update(insertSql.toString(), paramSourceGst);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
		return customerGST.getId();

	}

	@Override
	public void delete(CustomerGST customerGST, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder(" Delete From customergst");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustId =:CustId");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerGST);

		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	public void deleteCustomerGSTByCustomer(long custId, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		CustomerGST customerGST = new CustomerGST();
		customerGST.setCustId(custId);
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From customergst");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustId =:CustId");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerGST);

		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	@Override
	public void save(CustomerGSTDetails customerGSTDetails, String type) {
		logger.debug("Entering");

		// Get the identity sequence number.
		if (customerGSTDetails.getId() == Long.MIN_VALUE) {
			customerGSTDetails.setId(getNextValue("SEQGSTHEADERDETAILS"));
			logger.debug("get NextID:" + customerGSTDetails.getId());
		}
		StringBuilder insertSql = new StringBuilder("Insert Into CustomerGSTDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (Id, headerId, frequancy,financialYear,salAmount,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		insertSql.append(" NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:Id, :headerId, :frequancy, :financialYear, :salAmount,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		logger.debug("insertSql: " + insertSql.toString());
		try {
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerGSTDetails);
			this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug("Leaving");
	}

	@Override
	public void saveCustomerGSTDetailsBatch(List<CustomerGSTDetails> customerGSTDetailsList, String type) {
		logger.debug("Entering");

		for (CustomerGSTDetails customerGSTDetails : customerGSTDetailsList) {
			if (customerGSTDetails.getId() == Long.MIN_VALUE) {
				customerGSTDetails.setId(getNextValue("SEQGSTHEADERDETAILS"));
				logger.debug("get NextID:" + customerGSTDetails.getId());
			}
		}

		StringBuilder insertSql = new StringBuilder("Insert Into CustomerGSTDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (Id, headerId, frequancy,financialYear,salAmount,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		insertSql.append(" NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:Id,:headerId, :gstNumber, :frequancy, :financialYear, :salAmount,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerGSTDetailsList);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public void update(CustomerGST customerGST, String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" Update customergst");
		updateSql.append(StringUtils.trimToEmpty(type));

		updateSql.append(" Set Id = :Id, GstNumber = :GstNumber, Frequencytype = :Frequencytype,");

		updateSql.append(" Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(
				" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where Id = :Id ");
		if (!type.endsWith("_Temp")) {
			updateSql.append("AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerGST);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public void delete(CustomerGSTDetails customerGSTDetails, String type) {
		logger.debug("Entering");
		StringBuilder deleteSql = new StringBuilder(" Delete From customergstdetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where Id=:Id ");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerGSTDetails);

		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public CustomerGST getCustomerGSTByGstNumber(CustomerGST customerGST, String type) {

		logger.debug("Entering");
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT Id, CustId, GstNumber, Frequencytype,");
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM  customergst");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE ");
		selectSql.append("CustId = :CustId and GstNumber= :GstNumber");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerGST);
		RowMapper<CustomerGST> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerGST.class);
		try {
			customerGST = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			customerGST = null;
		}
		logger.debug("Leaving");
		return customerGST;

	}

	@Override
	public CustomerGST getCustomerGstByCustId(CustomerGST customerGST, String type) {
		logger.debug("Entering");
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT Id, CustId, GstNumber, Frequencytype,");
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM  customergst");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustId = :CustId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerGST);
		RowMapper<CustomerGST> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerGST.class);
		try {
			customerGST = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			customerGST = null;
		}
		logger.debug("Leaving");
		return customerGST;
	}

	@Override
	public CustomerGST getCustomerGstByCustId(long id, String type) {
		logger.debug("Entering");

		CustomerGST customerGST = new CustomerGST();
		customerGST.setId(id);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT Id, CustId, GstNumber, Frequencytype,");
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM  customergst");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where Id = :Id");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerGST);
		RowMapper<CustomerGST> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerGST.class);
		try {
			customerGST = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			customerGST = null;
		}
		logger.debug("Leaving");
		return customerGST;
	}

	@Override
	public void update(CustomerGSTDetails customerGSTDetails, String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" Update customergstdetails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(
				" Set HeaderId = :HeaderId, Frequancy = :Frequancy, FinancialYear = :FinancialYear, SalAmount = :SalAmount,");
		updateSql.append(" Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(
				" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where Id = :Id ");
		if (!type.endsWith("_Temp")) {
			updateSql.append("AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerGSTDetails);

		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public int getVersion(long id, String addrType) {
		return 0;
	}

	@Override
	public List<CustomerGSTDetails> getCustomerGSTDetailsById(long headerId, String type) {
		return null;

	}

	@Override
	public int getCustomerGstInfoByCustGstNumber(long id, long custId, String gstNumber, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Id", id);
		source.addValue("CustId", custId);
		source.addValue("GstNumber", gstNumber);
		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT count(*) FROM CustomerGST");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" WHERE ");
		selectSql.append(" Id =:Id and  CustId = :CustId and GstNumber= :GstNumber");
		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			recordCount = 0;
		}
		logger.debug("Leaving");
		return recordCount;
	}

}
