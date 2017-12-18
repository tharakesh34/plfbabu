package com.pennanttech.pff.external.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.model.systemmasters.City;
import com.pennanttech.dataengine.util.DateUtil;
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

	/**
	 * Fetch the Record City details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return City
	 */
	public City getCityById(final String pCCountry, String pCProvince, String pCCity, String type) {
		logger.debug(Literal.ENTERING);
		City city = new City();
		city.setPCCountry(pCCountry);
		city.setPCProvince(pCProvince);
		city.setPCCity(pCCity);

		StringBuilder selectSql = new StringBuilder(
				"SELECT PCCountry, PCProvince, PCCity, PCCityName, PCCityClassification, BankRefNo, CityIsActive,");
		if (type.contains("View")) {
			selectSql.append(" LovDescPCProvinceName, LovDescPCCountryName,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode,  NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From RMTProvinceVsCity");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PCCountry =:PCCountry and PCProvince=:PCProvince and PCCity=:PCCity ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(city);
		RowMapper<City> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(City.class);

		try {
			city = this.namedJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			city = null;
		}
		logger.debug(Literal.LEAVING);
		return city;
	}

	/**
	 * Method for fetch the ExtendedFieldDetails based on given fieldaNames
	 * 
	 * @param fieldNames
	 * @return extendedFieldDetailList
	 * @throws Exception
	 */
	public List<ExtendedFieldDetail> getExtendedFieldDetailsByFieldName(Set<String> fieldNames) throws Exception {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder("Select ModuleId, FieldName, FieldType, ");
		sql.append(" FieldLength, FieldPrec, FieldLabel, FieldMandatory, FieldConstraint, ");
		sql.append(" FieldSeqOrder, FieldList, FieldDefaultValue, FieldMinValue, ");
		sql.append(" FieldMaxValue, FieldUnique, MultiLine, ParentTag, InputElement,Editable, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, ");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From ExtendedFieldDetail  WHERE FIELDNAME IN(:fieldNames)");
		paramMap.addValue("fieldNames", fieldNames);
		logger.debug("selectSql: " + sql.toString());
		try {
			RowMapper<ExtendedFieldDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(ExtendedFieldDetail.class);
			logger.debug(Literal.LEAVING);
			return this.namedJdbcTemplate.query(sql.toString(), paramMap, typeRowMapper);

		} catch (Exception e) {
			logger.error("Exception", e);
			throw new InterfaceException("9999", "Unable to Retrive  the ExtendedFieldDetail.");
		}
	}

	/**
	 * Method for get the SMTParameter value
	 * 
	 * @param sysParmCode
	 * @param type
	 * @return
	 */
	public Object getSMTParameter(String sysParmCode, Class<?> type) {
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();
		paramMap = new MapSqlParameterSource();

		sql.append("SELECT SYSPARMVALUE FROM SMTPARAMETERS where SYSPARMCODE = :SYSPARMCODE");
		paramMap.addValue("SYSPARMCODE", sysParmCode);

		try {
			return namedJdbcTemplate.queryForObject(sql.toString(), paramMap, type);
		} catch (Exception e) {
			logger.error("The parameter code " + sysParmCode + " not configured.");
		} finally {
			paramMap = null;
			sql = null;
		}
		return null;
	}

	protected int updateParameter(String sysParmCode, Object value) throws Exception {
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();
		paramMap = new MapSqlParameterSource();

		sql.append("UPDATE SMTPARAMETERS SET SYSPARMVALUE = :SYSPARMVALUE where SYSPARMCODE = :SYSPARMCODE");
		paramMap.addValue("SYSPARMCODE", sysParmCode);
		paramMap.addValue("SYSPARMVALUE", value);

		try {
			return namedJdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error("Entering", e);
			throw new Exception("Unable to update the " + sysParmCode + ".");
		}
	}

	protected Date getValueDate() {
		String appDate;
		try {
			appDate = (String) getSMTParameter("APP_VALUEDATE", String.class);
			return DateUtil.parse(appDate, "yyyy-MM-dd"); // FIXME Deriving Application date should be from single place for all modules.
		} catch (Exception e) {

		}
		return null;
	}

	public static MapSqlParameterSource getMapSqlParameterSource(Map<String, Object> map) {
		MapSqlParameterSource parmMap = new MapSqlParameterSource();

		for (Entry<String, Object> entry : map.entrySet()) {
			parmMap.addValue(entry.getKey(), entry.getValue());
		}

		return parmMap;
	}

	protected long getSeq(String seqName) {
		logger.debug("Entering");
		StringBuilder sql = null;

		try {
			sql = new StringBuilder();
			sql.append("UPDATE ").append(seqName);
			sql.append(" SET SEQNO = SEQNO + 1");
			this.namedJdbcTemplate.update(sql.toString(), new MapSqlParameterSource());
		} catch (Exception e) {
			logger.error("Exception", e);
		}

		try {
			sql = new StringBuilder();
			sql.append("SELECT SEQNO FROM ").append(seqName);
			return this.namedJdbcTemplate.queryForObject(sql.toString(), new MapSqlParameterSource(), Long.class);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		logger.error(Literal.LEAVING);
		return 0;
	}

	
	/**
	 * Method for get the Email's of a Customer
	 * 
	 * @param customerIds
	 * @param type
	 * @return
	 */
	public List<CustomerEMail> getCustomersEmails(Set<Long> customerIds, String type) {

		logger.debug(Literal.ENTERING);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" SELECT CustID, CustEMail, CustEMailPriority, CustEMailTypeCode,");
		if (type.contains("_View")) {
			selectSql.append(" lovDescCustEMailTypeCode,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM  CustomerEMails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID IN(:customerIds)");

		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		paramMap.addValue("customerIds", customerIds);
		logger.debug("selectSql: " + selectSql.toString());
		try {
			logger.debug(Literal.LEAVING);
			RowMapper<CustomerEMail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerEMail.class);
			logger.debug(Literal.LEAVING);
			return this.namedJdbcTemplate.query(selectSql.toString(), paramMap, typeRowMapper);
		} catch (Exception e) {
			logger.error("Exception", e);
			throw new InterfaceException("9999", "Unable to Retrive  the Customer Email  Details.");
		}
	}

}
