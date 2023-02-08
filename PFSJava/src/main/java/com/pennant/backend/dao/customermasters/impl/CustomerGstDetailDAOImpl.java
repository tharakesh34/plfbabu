package com.pennant.backend.dao.customermasters.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.customermasters.CustomerGstDetailDAO;
import com.pennant.backend.model.customermasters.CustomerGST;
import com.pennant.backend.model.customermasters.CustomerGSTDetails;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class CustomerGstDetailDAOImpl extends SequenceDao<CustomerGST> implements CustomerGstDetailDAO {
	private static Logger logger = LogManager.getLogger(CustomerGstDetailDAOImpl.class);

	public CustomerGstDetailDAOImpl() {
		super();
	}

	@Override
	public List<CustomerGST> getCustomerGSTById(long id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, CustId, GstNumber, Frequencytype, Version, LastMntOn, LastMntBy, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" from CustomerGST");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustId = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, id);
		}, (rs, rowNum) -> {
			CustomerGST custGst = new CustomerGST();

			custGst.setId(rs.getLong("Id"));
			custGst.setCustId(rs.getLong("CustId"));
			custGst.setGstNumber(rs.getString("GstNumber"));
			custGst.setFrequencytype(rs.getString("Frequencytype"));
			custGst.setVersion(rs.getInt("Version"));
			custGst.setLastMntOn(rs.getTimestamp("LastMntOn"));
			custGst.setLastMntBy(rs.getLong("LastMntBy"));
			custGst.setRecordStatus(rs.getString("RecordStatus"));
			custGst.setRoleCode(rs.getString("RoleCode"));
			custGst.setNextRoleCode(rs.getString("NextRoleCode"));
			custGst.setTaskId(rs.getString("TaskId"));
			custGst.setNextTaskId(rs.getString("NextTaskId"));
			custGst.setRecordType(rs.getString("RecordType"));
			custGst.setWorkflowId(rs.getLong("WorkflowId"));

			return custGst;
		});
	}

	private String commaJoin(List<Long> headerIdList) {
		return headerIdList.stream().map(e -> "?").collect(Collectors.joining(","));
	}

	@Override
	public List<CustomerGSTDetails> getCustomerGSTDetailsByCustomer(long headerId, String type) {
		List<Long> headerIdList = new ArrayList<>();
		headerIdList.add(headerId);

		return getCustomerGSTDetailsByCustomer(headerIdList, type);
	}

	@Override
	public List<CustomerGSTDetails> getCustomerGSTDetailsByCustomer(List<Long> headerIdList, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, HeaderId, Frequancy, FinancialYear, SalAmount, Version, LastMntOn, LastMntBy");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" from CustomerGSTDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where HeaderId In (");
		sql.append(commaJoin(headerIdList));
		sql.append(")");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			for (Long headerId : headerIdList) {
				ps.setLong(index++, headerId);
			}

		}, (rs, rowNum) -> {
			CustomerGSTDetails gst = new CustomerGSTDetails();

			gst.setId(rs.getLong("Id"));
			gst.setHeaderId(rs.getLong("HeaderId"));
			gst.setFrequancy(rs.getString("Frequancy"));
			gst.setFinancialYear(rs.getString("FinancialYear"));
			gst.setSalAmount(rs.getBigDecimal("SalAmount"));
			gst.setVersion(rs.getInt("Version"));
			gst.setLastMntOn(rs.getTimestamp("LastMntOn"));
			gst.setLastMntBy(rs.getLong("LastMntBy"));
			gst.setRecordStatus(rs.getString("RecordStatus"));
			gst.setRoleCode(rs.getString("RoleCode"));
			gst.setNextRoleCode(rs.getString("NextRoleCode"));
			gst.setTaskId(rs.getString("TaskId"));
			gst.setNextTaskId(rs.getString("NextTaskId"));
			gst.setRecordType(rs.getString("RecordType"));
			gst.setWorkflowId(rs.getLong("WorkflowId"));

			return gst;
		});

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
	public void delete(long id, String type) {
		logger.debug("Entering");
		CustomerGSTDetails customerGSTDetails = new CustomerGSTDetails();
		customerGSTDetails.setHeaderId(id);
		StringBuilder deleteSql = new StringBuilder(" Delete From customergstdetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where HeaderId= :HeaderId ");
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
		RowMapper<CustomerGST> typeRowMapper = BeanPropertyRowMapper.newInstance(CustomerGST.class);
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
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
		RowMapper<CustomerGST> typeRowMapper = BeanPropertyRowMapper.newInstance(CustomerGST.class);
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
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
		RowMapper<CustomerGST> typeRowMapper = BeanPropertyRowMapper.newInstance(CustomerGST.class);
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
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
	public int getVersion(long id) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Id", id);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT Version FROM CustomerGst");
		selectSql.append(" WHERE Id = :Id");

		logger.debug("insertSql: " + selectSql.toString());

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	@Override
	public List<CustomerGSTDetails> getCustomerGSTDetailsById(long headerId, String type) {
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
		RowMapper<CustomerGSTDetails> typeRowMapper = BeanPropertyRowMapper.newInstance(CustomerGSTDetails.class);

		List<CustomerGSTDetails> customerGstInfoDetails = this.jdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);

		logger.debug("Leaving");
		return customerGstInfoDetails;
	}

	@Override
	public int getCustomerGstInfoByCustGstNumber(long id, long custId, String gstNumber, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Id", id);
		source.addValue("CustId", custId);
		source.addValue("GstNumber", gstNumber);
		StringBuilder selectSql = new StringBuilder();
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
