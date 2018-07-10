/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  CustomerIncomeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-05-2011    														*
 *                                                                  						*
 * Modified Date    :  06-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-05-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.dao.customermasters.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.customermasters.CustomerIncomeDAO;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.dao.customer.income.IncomeDetailDAOImpl;

/**
 * DAO methods implementation for the <b>CustomerIncome model</b> class.<br>
 * 
 */
public class CustomerIncomeDAOImpl extends SequenceDao<CustomerIncome> implements CustomerIncomeDAO {
	private static Logger logger = Logger.getLogger(CustomerIncomeDAOImpl.class);

	public CustomerIncomeDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Customer Incomes details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CustomerIncome
	 */
	public CustomerIncome getCustomerIncomeById(CustomerIncome customerIncome, String type, String inputSource) {
		logger.debug(Literal.ENTERING);

		type = StringUtils.trimToEmpty(type).toLowerCase();

		String view = null;
		String table = null;
		if ("sampling".equals(inputSource)) {
			view = "sampling_income_details";
			table = "link_sampling_incomes";
		} else {
			view = "customer_income_details";
			table = "link_cust_incomes";
		}
		
		if (type.equals("_temp")) {
			view = view.concat("_view");
		} else {
			view = view.concat("_aview");
		}

		StringBuilder query = new StringBuilder();
		if (type.contains("view")) {
			query.append(" select custid, income, incometype, incomeExpense, category, margin,");
			query.append(" incometypedesc, categorydesc, ");
			query.append(" custcif, custshrtname, toccy, ");
			query.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
			query.append(" TaskId, NextTaskId, RecordType, WorkflowId");
			query.append(" from ");
			query.append(view);
			query.append(" Where linkid =:linkId and custid = :custId and incometype = :incomeType ");
			query.append(" and incomeExpense = :incomeExpense and category=:category");
		} else {
			query.append(" select cu.custid, incd.income, incd.incometype, incd.incomeExpense, incd.category, incd.margin,");
			query.append(" it.incometypedesc, ic.categorydesc, ");
			query.append(" cu.custcif, cu.custshrtname, cu.custbaseccy toccy, ");
			query.append(" incd.Version, incd.LastMntOn, incd.LastMntBy, incd.RecordStatus, incd.RoleCode, incd.NextRoleCode,");
			query.append(" incd.TaskId, incd.NextTaskId, incd.RecordType, incd.WorkflowId");
			query.append(" from income_details");
			query.append(type).append(" incd");
			query.append(" inner join ").append(table).append(" cin");
			query.append(" on cin.linkid = incd.linkid");
			query.append(" inner join bmtincometypes it on it.incometypecode=incd.incometype");
			query.append(" and it.incomeexpense=incd.incomeexpense and it.category=incd.category");
			query.append(" inner join (");
			query.append(" select custid, custcif, custshrtname, custbaseccy from customers_temp");
			query.append(" union ");
			query.append(" select custid,custcif,custshrtname, custbaseccy from customers cu ");
			query.append(" where not exists (select 1 from customers_temp where custid=cu.custid)");
			query.append(" ) cu on cu.custid = cin.custid");
			query.append(" inner join bmtincomecategory ic on ic.incomecategory = incd.category");
			query.append(" Where incd.linkid =:linkId and cu.custid = :custId and incd.incometype = :incomeType ");
			query.append(" and incd.incomeExpense = :incomeExpense and incd.category=:category");
		}
			
		logger.trace(Literal.SQL + query.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerIncome);
		RowMapper<CustomerIncome> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerIncome.class);

		try {
			customerIncome = this.jdbcTemplate.queryForObject(query.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
			customerIncome = null;
		}
		logger.debug(Literal.LEAVING);
		return customerIncome;
	}

	/**
	 * This method Deletes the Records from the CustomerIncomes or CustomerIncomes_Temp if records Existed in table.
	 * 
	 * @param customerId
	 *            (long)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void deleteByCustomer(final long customerId, String type, boolean isWIF) {
		logger.debug(Literal.ENTERING);

		CustomerIncome customerIncome = new CustomerIncome();
		customerIncome.setId(customerId);

		StringBuilder query = new StringBuilder();
		if (!isWIF) {
			query.append("delete from customer_incomes");
			query.append(StringUtils.trimToEmpty(type));
		} else {
			query.append("delete from WIFCustomerIncomes");
		}
		query.append(" where custid =:custId ");

		logger.debug(Literal.SQL + query.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerIncome);

		this.jdbcTemplate.update(query.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
	}

	
	@Override
	public void setLinkId(CustomerIncome customerIncome) {
		logger.debug(Literal.ENTERING);

		long linkId = customerIncome.getLinkId();

		if (linkId > 0) {
			return;
		}

		linkId = getLinkId(customerIncome.getCustId());

		if (linkId > 0) {
			customerIncome.setLinkId(linkId);
			return;
		}

		linkId = getNextValue(IncomeDetailDAOImpl.SEQUENCE_LINK);
		customerIncome.setLinkId(linkId);
		StringBuilder sql = new StringBuilder();
		sql.append("insert into link_cust_incomes values(:custid, :linkid)");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("custid", customerIncome.getCustId());
		source.addValue("linkid", linkId);

		this.jdbcTemplate.update(sql.toString(), source);
	}

	/**
	 * This method insert new Records into CustomerIncomes or CustomerIncomes_Temp.
	 * 
	 * save Customer Incomes
	 * 
	 * @param Customer
	 *            Incomes (customerIncome)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void saveBatch(List<CustomerIncome> customerIncome, String type, boolean isWIF) {
		logger.debug(Literal.ENTERING);

		StringBuilder query = new StringBuilder();
		if (!isWIF) {
			query.append(" Insert Into CustomerIncomes");
			query.append(StringUtils.trimToEmpty(type));
		} else {
			query.append(" Insert Into WIFCustomerIncomes");
		}

		query.append(" (CustID, CustIncomeType, CustIncome, IncomeExpense, Category,Margin, JointCust, ");
		query.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		query.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		query.append(" Values(:CustID, :CustIncomeType, :CustIncome, :IncomeExpense,:Category,:Margin, :JointCust, ");
		query.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		query.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.trace(Literal.SQL + query.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(customerIncome.toArray());
		logger.debug(Literal.LEAVING);
		this.jdbcTemplate.batchUpdate(query.toString(), beanParameters);
	}

	
	/**
	 * Fetch current version of the record.
	 * 
	 * @param customerIncome
	 * @return Integer
	 */
	@Override
	public int getVersion(CustomerIncome customerIncome) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" select version from customer_incomes_aview");
		sql.append(" where custid= :custId and incomeExpense= :incomeExpense");
		sql.append(" and incomeType= :incomeType and category =:category");

		
		int recordCount = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("custId", customerIncome.getCustId());
		source.addValue("incomeExpense", customerIncome.getIncomeExpense());
		source.addValue("incomeType", customerIncome.getIncomeType());
		source.addValue("jointCust", customerIncome.isJointCust());
		source.addValue("category", customerIncome.getCategory());
		
		logger.trace(Literal.SQL + sql.toString());
		
		try {
			recordCount = this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.error(dae);
			recordCount = 0;
		}
		logger.debug(Literal.LEAVING);

		return recordCount;
	}

	@Override
	public long getLinkId(long custId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select coalesce(max(linkid), 0) from link_cust_incomes where custid=:custid");

		long linkid = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("custid", custId);
		try {
			linkid = jdbcTemplate.queryForObject(sql.toString(), source, Long.class);
		} catch (DataAccessException e) {
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return linkid;
	}
	
	@Override
	public List<CustomerIncome> getIncomesByFinReference(String finReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder query = new StringBuilder();
		query.append("select * from customer_income_details_view ci");
		query.append(" inner join");
		query.append(" (select custid, finreference from financemain_view");
		query.append(" union all");
		query.append(" select jointaccountid custid, finreference from finjointaccountdetails_view) fm");
		query.append(" on fm.custid=ci.custid where fm.finreference = :finreference");

		logger.trace(Literal.SQL + query.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("finreference", finReference);
		RowMapper<CustomerIncome> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerIncome.class);

		logger.debug(Literal.LEAVING);

		try {
			return this.jdbcTemplate.query(query.toString(), parameterSource, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			return new ArrayList<>();
		}
	}
	
	@Override
	public List<CustomerIncome> getIncomesBySamplingId(long samplingId) {
		logger.debug(Literal.ENTERING);

		StringBuilder query = new StringBuilder();
		query.append("select * from income_details_view ci");
		query.append(" inner join link_sampling_incomes si on si.linkid = ci.linkid");
		query.append(" where si.samplingid = :samplingid");

		logger.trace(Literal.SQL + query.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("samplingid", samplingId);
		RowMapper<CustomerIncome> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerIncome.class);

		logger.debug(Literal.LEAVING);

		try {
			return this.jdbcTemplate.query(query.toString(), parameterSource, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			return new ArrayList<>();
		}
	}
}