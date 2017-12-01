package com.pennanttech.pff.external.dao;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;

public class NiyoginDAOImpl {
	private static final Logger				logger	= Logger.getLogger(NiyoginDAOImpl.class);
	protected DataSource					dataSource;
	protected JdbcTemplate					jdbcTemplate;
	protected NamedParameterJdbcTemplate	namedJdbcTemplate;

	protected DataSourceTransactionManager	transManager;
	protected DefaultTransactionDefinition	transDef;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		setTransManager(dataSource);
	}

	private void setTransManager(DataSource dataSource) {
		this.dataSource = dataSource;
		this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

		this.transManager = new DataSourceTransactionManager(dataSource);
		this.transDef = new DefaultTransactionDefinition();
		this.transDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		this.transDef.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		this.transDef.setTimeout(120);
	}

	public List<CustomerDetails> getCoApplicants(List<Long> customerIds, String type) throws InterfaceException {
		logger.debug(Literal.ENTERING);
		List<CustomerDetails> customerDetailList = new ArrayList<CustomerDetails>(1);
		List<Customer> customers = getCustomerByID(customerIds, type);

		for (Customer customer : customers) {
			CustomerDetails customerDetails = new CustomerDetails();
			customerDetails.setCustID(customer.getCustID());
			customerDetails.setAddressList(getCustomerAddresByCustomer(customer.getCustID(), type));
			customerDetails.setCustomerEMailList(getCustomerEmailByCustomer(customer.getCustID(), type));
			customerDetailList.add(customerDetails);
		}
		logger.debug(Literal.LEAVING);
		return customerDetailList;

	}

	/**
	 * Fetch the Record Customers details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return List<Customer>
	 */
	public List<Customer> getCustomerByID(List<Long> customerIds, String type) throws InterfaceException {
		logger.debug("Entering");
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuilder selectSql = new StringBuilder("SELECT CustID, CustCIF, CustCoreBank, CustCtgCode, CustTypeCode,");
		selectSql.append(
				" CustSalutationCode, CustFName, CustMName, CustLName, CustShrtName, CustFNameLclLng, CustMNameLclLng,");
		selectSql.append(
				" CustLNameLclLng, CustShrtNameLclLng, CustDftBranch, CustGenderCode, CustDOB, CustPOB, CustCOB,");
		selectSql.append(
				" CustPassportNo, CustMotherMaiden, CustIsMinor, CustReferedBy, CustDSA, CustDSADept, CustRO1, CustRO2,");
		selectSql.append(
				" CustGroupID, CustSts, CustStsChgDate, CustGroupSts, CustIsBlocked, CustIsActive, CustIsClosed,");
		selectSql.append(
				" CustInactiveReason, CustIsDecease, CustIsDormant, CustIsDelinquent, CustIsTradeFinCust, CustIsStaff,");
		selectSql.append(
				" CustTradeLicenceNum , CustTradeLicenceExpiry, CustPassportExpiry, CustVisaNum , CustVisaExpiry,");
		selectSql.append(
				" CustStaffID, CustIndustry, CustSector, CustSubSector, CustProfession, CustTotalIncome, CustMaritalSts,");
		selectSql.append(
				" CustEmpSts, CustSegment, CustSubSegment, CustIsBlackListed, CustBLRsnCode, CustIsRejected, CustRejectedRsn,");
		selectSql.append(
				" CustBaseCcy, CustLng, CustParentCountry, CustResdCountry, CustRiskCountry, CustNationality, CustClosedOn, ");
		selectSql.append(
				"CustStmtFrq, CustIsStmtCombined, CustStmtLastDate, CustStmtNextDate, CustStmtDispatchMode, CustFirstBusinessDate,");
		selectSql.append(
				" CustAddlVar81, CustAddlVar82, CustAddlVar83, CustAddlVar84, CustAddlVar85, CustAddlVar86, CustAddlVar87,");
		selectSql.append(
				" CustAddlVar88, CustAddlVar89, CustAddlDate1, CustAddlDate2, CustAddlDate3, CustAddlDate4, CustAddlDate5,");
		selectSql.append(
				" CustAddlVar1, CustAddlVar2, CustAddlVar3, CustAddlVar4, CustAddlVar5, CustAddlVar6, CustAddlVar7, CustAddlVar8, ");
		selectSql.append(
				" CustAddlVar9, CustAddlVar10, CustAddlVar11, CustAddlDec1, CustAddlDec2, CustAddlDec3, CustAddlDec4, CustAddlDec5,");
		selectSql.append(
				" CustAddlInt1, CustAddlInt2, CustAddlInt3, CustAddlInt4, CustAddlInt5,DedupFound,SkipDedup,CustTotalExpense,CustBlackListDate,NoOfDependents,CustCRCPR,");
		selectSql.append(
				" JointCust, JointCustName, JointCustDob, custRelation, ContactPersonName, EmailID, PhoneNumber, SalariedCustomer, custSuspSts,custSuspDate, custSuspTrigger, ");

		if (type.contains("View")) {
			selectSql.append(
					" lovDescCustTypeCodeName, lovDescCustMaritalStsName, lovDescCustEmpStsName,  lovDescCustStsName,");
			selectSql.append(
					" lovDescCustIndustryName, lovDescCustSectorName, lovDescCustSubSectorName, lovDescCustProfessionName, lovDescCustCOBName ,");
			selectSql.append(
					" lovDescCustSegmentName, lovDescCustNationalityName, lovDescCustGenderCodeName, lovDescCustDSADeptName, lovDescCustRO1Name, ");
			selectSql.append(
					" lovDescCustGroupStsName, lovDescCustDftBranchName, lovDescCustCtgCodeName,lovDescCustCtgType, lovDescCustSalutationCodeName ,");
			selectSql.append(
					" lovDescCustParentCountryName, lovDescCustResdCountryName , lovDescCustRiskCountryName , lovDescCustRO2Name , lovDescCustBLRsnCodeName,");
			selectSql.append(
					" lovDescCustRejectedRsnName, lovDesccustGroupIDName , lovDescCustSubSegmentName, lovDescCustLngName , lovDescDispatchModeDescName");
			selectSql.append(" ,lovDescTargetName,");
		}

		selectSql.append(
				" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  Customers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID IN(:customerIds)");

		logger.debug("selectSql: " + selectSql.toString());
		paramMap.addValue("customerIds", customerIds);
		RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);

		try {
			logger.debug("Leaving");
			return this.namedJdbcTemplate.query(selectSql.toString(), paramMap, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			throw new InterfaceException("9999", "Unable to Retrive  the CoApplicant Details.");
		}

	}

	/**
	 * Method For getting List of Customer related Addresses for Customer
	 */
	public List<CustomerAddres> getCustomerAddresByCustomer(final long custId, String type) {
		logger.debug("Entering");
		CustomerAddres customerAddres = new CustomerAddres();
		customerAddres.setId(custId);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT CustID, CustAddrType, CustAddrHNbr, CustFlatNbr, CustAddrStreet,");
		selectSql.append(" CustAddrLine1, CustAddrLine2, CustPOBox, CustAddrCity, CustAddrProvince,CustAddrPriority,");
		selectSql.append(
				" CustAddrCountry, CustAddrZIP, CustAddrPhone, CustAddrFrom,TypeOfResidence,CustAddrLine3,CustAddrLine4,CustDistrict,");
		if (type.contains("View")) {
			selectSql.append(" lovDescCustAddrTypeName, lovDescCustAddrCityName,");
			selectSql.append(" lovDescCustAddrProvinceName, lovDescCustAddrCountryName,lovDescCustAddrZip,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM CustomerAddresses");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :custID ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerAddres);
		RowMapper<CustomerAddres> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerAddres.class);

		List<CustomerAddres> customerAddresses = this.namedJdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
		logger.debug("Leaving");
		return customerAddresses;
	}

	/**
	 * Method to return the customer email based on given customer id
	 */
	public List<CustomerEMail> getCustomerEmailByCustomer(final long id, String type) {
		logger.debug("Entering");
		CustomerEMail customerEMail = new CustomerEMail();
		customerEMail.setId(id);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT CustID, CustEMail, CustEMailPriority, CustEMailTypeCode,");
		if (type.contains("View")) {
			selectSql.append(" lovDescCustEMailTypeCode,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM  CustomerEMails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :custID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerEMail);
		RowMapper<CustomerEMail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerEMail.class);

		List<CustomerEMail> customerEMails = this.namedJdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);

		logger.debug("Leaving");
		return customerEMails;
	}
	
	public long getPincodeGroupId(String pincode) {
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT GROUPID FROM PINCODES WHERE PINCODE =:PINCODE");
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("PINCODE", pincode);
		long grpid = 0;
		try {
			grpid = namedJdbcTemplate.queryForObject(selectSql.toString(), paramSource, Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		return grpid;
	}
}
