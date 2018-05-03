package com.pennant.equation.dao.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.RelationshipOfficer;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.model.customermasters.CustomerGroup;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.customermasters.CustomerRating;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.model.rmtmasters.CustomerType;
import com.pennant.backend.model.rmtmasters.FinTypeAccounting;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.systemmasters.AddressType;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.EMailType;
import com.pennant.backend.model.systemmasters.EmpStsCode;
import com.pennant.backend.model.systemmasters.MaritalStatusCode;
import com.pennant.backend.model.systemmasters.Salutation;
import com.pennant.backend.model.systemmasters.SubSector;
import com.pennant.coreinterface.model.EquationAbuser;
import com.pennant.coreinterface.model.EquationAccountType;
import com.pennant.coreinterface.model.EquationBranch;
import com.pennant.coreinterface.model.EquationCountry;
import com.pennant.coreinterface.model.EquationCurrency;
import com.pennant.coreinterface.model.EquationCustStatusCode;
import com.pennant.coreinterface.model.EquationCustomerGroup;
import com.pennant.coreinterface.model.EquationCustomerRating;
import com.pennant.coreinterface.model.EquationCustomerType;
import com.pennant.coreinterface.model.EquationDepartment;
import com.pennant.coreinterface.model.EquationIdentityType;
import com.pennant.coreinterface.model.EquationIndustry;
import com.pennant.coreinterface.model.EquationInternalAccount;
import com.pennant.coreinterface.model.EquationMasterMissedDetail;
import com.pennant.coreinterface.model.EquationRelationshipOfficer;
import com.pennant.coreinterface.model.EquationTransactionCode;
import com.pennant.coreinterface.model.IncomeAccountTransaction;
import com.pennant.equation.dao.CoreInterfaceDAO;

public class CoreInterfaceDAOImpl implements CoreInterfaceDAO{
	private static Logger logger = Logger.getLogger(CoreInterfaceDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private NamedParameterJdbcTemplate auditNamedParameterJdbcTemplate;

	public CoreInterfaceDAOImpl() {
		
	}
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	public void setAuditDataSource(DataSource dataSource) {
		this.auditNamedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	/**
	 *  Method for fetching Currency Details
	 */
	@Override
	public List<EquationCurrency> fetchCurrecnyDetails() {
		logger.debug("Entering");
		
		EquationCurrency  currency = new EquationCurrency();
		StringBuilder selectSql = new StringBuilder("Select CcyCode From RMTCurrencies");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(currency);
		RowMapper<EquationCurrency> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(EquationCurrency.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for fetching RelationshipOfficer Details
	 */
	@Override
	public List<EquationRelationshipOfficer> fetchRelationshipOfficerDetails() {
		logger.debug("Entering");
		
		EquationRelationshipOfficer  relationshipOfficer = new EquationRelationshipOfficer();
		StringBuilder selectSql = new StringBuilder("Select ROfficerCode From RelationshipOfficers");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(relationshipOfficer);
		RowMapper<EquationRelationshipOfficer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(EquationRelationshipOfficer.class);
		
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	
	/**
	 *  Method for fetching CustomerType Details
	 */
	@Override
	public List<EquationCustomerType> fetchCustomerTypeDetails() {
		logger.debug("Entering");
		
		EquationCustomerType  customerType = new EquationCustomerType();
		StringBuilder selectSql = new StringBuilder("Select CustTypeCode From RMTCustTypes");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerType);
		RowMapper<EquationCustomerType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(EquationCustomerType.class);
		
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	
	/**
	 *  Method for fetching Department Details
	 */
	@Override
	public List<EquationDepartment> fetchDepartmentDetails() {
		logger.debug("Entering");
		
		EquationDepartment  department = new EquationDepartment();
		StringBuilder selectSql = new StringBuilder("Select DeptCode From BMTDepartments");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(department);
		RowMapper<EquationDepartment> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(EquationDepartment.class);
		
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for fetching Department Details
	 */
	@Override
	public List<EquationCustomerGroup> fetchCustomerGroupDetails() {
		logger.debug("Entering");
		
		EquationCustomerGroup  customerGroup = new EquationCustomerGroup();
		StringBuilder selectSql = new StringBuilder("Select CustGrpCode From CustomerGroups");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerGroup);
		RowMapper<EquationCustomerGroup> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(EquationCustomerGroup.class);
		
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for fetching Account Type  Details
	 */
	@Override
	public List<EquationAccountType> fetchAccountTypeDetails() {
		logger.debug("Entering");
		
		EquationAccountType  accountType = new EquationAccountType();
		StringBuilder selectSql = new StringBuilder("Select AcType From RMTAccountTypes");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountType);
		RowMapper<EquationAccountType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(EquationAccountType.class);
		
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for fetching Account Type  Details
	 */
	@Override
	public List<EquationCustomerRating> fetchCustomerRatingDetails() {
		logger.debug("Entering");
		
		EquationCustomerRating  customerRating = new EquationCustomerRating();
		StringBuilder selectSql = new StringBuilder("Select CustID,CustRatingType From CustomerRatings");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerRating);
		RowMapper<EquationCustomerRating> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(EquationCustomerRating.class);
		
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for fetching Country Details
	 */
	@Override
	public List<EquationCountry> fetchCountryDetails() {
		logger.debug("Entering");
		
		EquationCountry  country = new EquationCountry();
		StringBuilder selectSql = new StringBuilder("Select CountryCode  from BMTCountries");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(country);
		RowMapper<EquationCountry> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(EquationCountry.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	
	/**
	 *  Method for fetching Customer Status Code Details
	 */
	@Override
	public List<EquationCustStatusCode> fetchCustStatusCodeDetails() {
		logger.debug("Entering");
		
		EquationCustStatusCode  statusCode = new EquationCustStatusCode();
		StringBuilder selectSql = new StringBuilder("Select CustStsCode  from BMTCustStatusCodes");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(statusCode);
		RowMapper<EquationCustStatusCode> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(EquationCustStatusCode.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for fetching Industry Details
	 */
	@Override
	public List<EquationIndustry> fetchIndustryDetails() {
		logger.debug("Entering");
		
		EquationIndustry  industry = new EquationIndustry();
		StringBuilder selectSql = new StringBuilder("Select  IndustryCode  from BMTIndustries");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(industry);
		RowMapper<EquationIndustry> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(EquationIndustry.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for fetching Branch Details
	 */
	@Override
	public List<EquationBranch> fetchBranchDetails() {
		logger.debug("Entering");
		
		EquationBranch  branch = new EquationBranch();
		StringBuilder selectSql = new StringBuilder("Select  BranchCode  from RMTBranches");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(branch);
		RowMapper<EquationBranch> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(EquationBranch.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	
	/**
	 *  Method for fetching Internal Account Details
	 */
	@Override
	public List<EquationInternalAccount> fetchInternalAccDetails() {
		logger.debug("Entering");
		
		EquationInternalAccount  internalAccount = new EquationInternalAccount();
		StringBuilder selectSql = new StringBuilder("Select  SIACode  from SystemInternalAccountDef");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(internalAccount);
		RowMapper<EquationInternalAccount> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(EquationInternalAccount.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/*
	 *  Method for fetching Transaction Code  Details
	 */
	@Override
	public List<EquationTransactionCode> fetchTransactionCodeDetails() {
		logger.debug("Entering");
		
		EquationTransactionCode  transactionCode = new EquationTransactionCode();
		StringBuilder selectSql = new StringBuilder("Select TranCode From BMTTransactionCode");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(transactionCode);
		RowMapper<EquationTransactionCode> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(EquationTransactionCode.class);
		
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for fetching Identity Type  Details
	 */
	@Override
	public List<EquationIdentityType> fetchIdentityTypeDetails() {
		logger.debug("Entering");
		
		EquationIdentityType  identityType = new EquationIdentityType();
		StringBuilder selectSql = new StringBuilder("Select IdentityType From BMTIdentityType");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(identityType);
		RowMapper<EquationIdentityType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(EquationIdentityType.class);
		
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	
	/**
	 *  Method for saving Currency Details
	 * @throws Exception 
	 */
	@Override
	public void saveCurrecnyDetails(List<EquationCurrency> currencyList) throws Exception{
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into RMTCurrencies" );
		insertSql.append(" (CcyCode, CcyNumber, CcyDesc, CcySwiftCode, CcyEditField," );
		insertSql.append(" CcyMinorCcyUnits, CcyDrRateBasisCode, CcyCrRateBasisCode," );
		insertSql.append(" CcyIsIntRounding, CcySpotRate, CcyIsReceprocal, CcyUserRateBuy," );
		insertSql.append(" CcyUserRateSell, CcyIsMember, CcyIsGroup, CcyIsAlwForLoans, CcyIsAlwForDepo," );
		insertSql.append(" CcyIsAlwForAc, CcyIsActive, CcyMinorCcyDesc, CcySymbol," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CcyCode, :CcyNumber, :CcyDesc, :CcySwiftCode, :CcyEditField," );
		insertSql.append(" :CcyMinorCcyUnits, :CcyDrRateBasisCode, :CcyCrRateBasisCode," );
		insertSql.append(" :CcyIsIntRounding, :CcySpotRate, :CcyIsReceprocal, :CcyUserRateBuy," );
		insertSql.append(" :CcyUserRateSell, :CcyIsMember, :CcyIsGroup, :CcyIsAlwForLoans," );
		insertSql.append(" :CcyIsAlwForDepo, :CcyIsAlwForAc, :CcyIsActive, :CcyMinorCcyDesc, :CcySymbol,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(currencyList.toArray());
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for saving RelationshipOfficer Details
	 * @throws Exception 
	 */
	@Override
	public void saveRelationShipOfficerDetails(List<EquationRelationshipOfficer> relationshipOfficerList) throws Exception{
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into RelationshipOfficers");
		insertSql.append(" (ROfficerCode, ROfficerDesc, ROfficerDeptCode, ROfficerIsActive," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:ROfficerCode, :ROfficerDesc, :ROfficerDeptCode, :ROfficerIsActive,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(relationshipOfficerList.toArray());

		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for saving CustomerType Details
	 * @throws Exception 
	 */
	@Override
	public void saveCustomerTypeDetails(List<EquationCustomerType> customerTypes) throws Exception{
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder("Insert Into RMTCustTypes");
		insertSql.append(" (CustTypeCode, CustTypeCtg, CustTypeDesc, CustTypeIsActive,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:CustTypeCode, :CustTypeCtg, :CustTypeDesc, :CustTypeIsActive,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(customerTypes.toArray());
		
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for saving Departments Details
	 * @throws Exception 
	 */
	@Override
	public void saveDepartmentDetails(List<EquationDepartment> departments) throws Exception{
		logger.debug("Entering");
		
		StringBuilder insertSql  = new StringBuilder("Insert Into BMTDepartments");
		insertSql.append(" (DeptCode, DeptDesc, DeptIsActive," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId," );
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:DeptCode, :DeptDesc, :DeptIsActive, " );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(departments.toArray());
		
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for saving Departments Details
	 * @throws Exception 
	 */
	@Override
	public void saveCustomerGroupDetails(List<EquationCustomerGroup> customerGroups) throws Exception{
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder(" Insert Into CustomerGroups");
		insertSql.append(" (CustGrpID, CustGrpCode, CustGrpDesc, CustGrpRO1, CustGrpLimit, CustGrpIsActive," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values( :CustGrpID, :CustGrpCode, :CustGrpDesc, :CustGrpRO1, :CustGrpLimit, :CustGrpIsActive, " );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(customerGroups.toArray());
		
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for saving Account Type  Details
	 * @throws Exception 
	 */
	@Override
	public void saveAccountTypeDetails(List<EquationAccountType> accountTypes) throws Exception{
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder("Insert Into RMTAccountTypes" );
		insertSql.append(" (AcType, AcTypeDesc, AcPurpose, AcHeadCode," );
		insertSql.append(" InternalAc, CustSysAc, AcTypeIsActive, AcLmtCategory, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:AcType, :AcTypeDesc, :AcPurpose, :AcHeadCode, " );
		insertSql.append(" :InternalAc, :CustSysAc,:AcTypeIsActive, :AcLmtCategory," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode," );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(accountTypes.toArray());
		
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	/**
	 *  Method for saving Account Type Nature Details
	 * @throws Exception 
	 */
	@Override
	public void saveAccountTypeNatureDetails(List<EquationAccountType> accountTypes) throws Exception{
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder(" Insert Into AccountTypeNatures");
		insertSql.append(" (AcType, AcTypeNature1, AcTypeNature2, AcTypeNature3, AcTypeNature4, AcTypeNature5," );
		insertSql.append(" AcTypeNature6 , AcTypeNature7, AcTypeNature8, AcTypeNature9, AcTypeNature10)");
		insertSql.append(" Values( :AcType, :AcTypeNature1, :AcTypeNature2, :AcTypeNature3, :AcTypeNature4, :AcTypeNature5, " );
		insertSql.append(" :AcTypeNature6 , :AcTypeNature7, :AcTypeNature8, :AcTypeNature9, :AcTypeNature10)");
		
		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(accountTypes.toArray());
		
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	/**
	 *  Method for saving Customer Rating Details
	 * @throws Exception 
	 */
	@Override
	public void saveCustomerRatingDetails(List<EquationCustomerRating> customerRatings) throws Exception{
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into CustomerRatings" );
		insertSql.append(" (CustID, CustRatingType, CustRatingCode, CustRating, ValueType," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:CustID, :CustRatingType, :CustRatingCode, :CustRating, :ValueType, " );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(customerRatings.toArray());
		
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	
	/**
	 *  Method for saving Country Details
	 * @throws Exception 
	 */
	@Override
	public void saveCountryDetails(List<EquationCountry> countryList) throws Exception{
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into BMTCountries" );
		insertSql.append(" (CountryCode, CountryDesc, CountryParentLimit, CountryResidenceLimit, CountryRiskLimit, CountryIsActive," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CountryCode, :CountryDesc, :CountryParentLimit, :CountryResidenceLimit, :CountryRiskLimit, :CountryIsActive," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(countryList.toArray());
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for saving Country Details
	 * @throws Exception 
	 */
	@Override
	public void saveCustStatusCodeDetails(List<EquationCustStatusCode> custStsList) throws Exception{
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into BMTCustStatusCodes" );
		insertSql.append(" (CustStsCode, CustStsDescription, DueDays, SuspendProfit, CustStsIsActive," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CustStsCode, :CustStsDescription, :DueDays, :SuspendProfit, :CustStsIsActive," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(custStsList.toArray());
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for saving Country Details
	 * @throws Exception 
	 */
	@Override
	public void saveIndustryDetails(List<EquationIndustry> industryList) throws Exception{
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into BMTIndustries" );
		insertSql.append(" (IndustryCode, SubSectorCode, IndustryDesc, IndustryLimit, IndustryIsActive," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:IndustryCode, :SubSectorCode, :IndustryDesc, :IndustryLimit, :IndustryIsActive," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(industryList.toArray());
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	
	/**
	 *  Method for saving Branch Details
	 * @throws Exception 
	 */
	@Override
	public void saveBranchDetails(List<EquationBranch> branchList) throws Exception{
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into RMTBranches" );
		insertSql.append(" (BranchCode, BranchDesc, BranchAddrLine1, BranchAddrLine2, BranchPOBox," );
		insertSql.append(" BranchCity, BranchProvince, BranchCountry, BranchFax, BranchTel, BranchSwiftBankCde," );
		insertSql.append(" BranchSwiftCountry, BranchSwiftLocCode, BranchSwiftBrnCde, BranchSortCode, BranchIsActive," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:BranchCode, :BranchDesc, :BranchAddrLine1, :BranchAddrLine2, :BranchPOBox," );
		insertSql.append(" :BranchCity , :BranchProvince, :BranchCountry, :BranchFax, :BranchTel, :BranchSwiftBankCde," );
		insertSql.append(" :BranchSwiftCountry, :BranchSwiftLocCode, :BranchSwiftBrnCde, :BranchSortCode, :BranchIsActive," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(branchList.toArray());
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for saving Internal Account Details
	 * @throws Exception 
	 */
	@Override
	public void saveInternalAccDetails(List<EquationInternalAccount> branchList) throws Exception{
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder("Insert Into SystemInternalAccountDef" );
		insertSql.append(" (SIACode, SIAName, SIAShortName, SIAAcType, SIANumber," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:SIACode, :SIAName, :SIAShortName, :SIAAcType, :SIANumber," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(branchList.toArray());
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	
	/**
	 *  Method for updating Currency Details
	 * @throws Exception 
	 */
	@Override
	public void updateCurrecnyDetails(List<EquationCurrency> currencyList) throws Exception{
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update RMTCurrencies" );
		updateSql.append(" Set CcySpotRate = :CcySpotRate, CcyIsReceprocal = :CcyIsReceprocal,");
		updateSql.append(" CcyUserRateBuy = :CcyUserRateBuy, CcyUserRateSell = :CcyUserRateSell" );
		updateSql.append(" Where CcyCode =:CcyCode ");
		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(currencyList.toArray());

		logger.debug("Leaving");
		try{	
		   this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for updating RelationshipOfficer Details
	 * @throws Exception 
	 */
	@Override
	public void updateRelationShipOfficerDetails(List<EquationRelationshipOfficer> relationshipOfficerList) throws Exception{
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update RelationshipOfficers" );
		updateSql.append(" Set ROfficerDesc = :ROfficerDesc");
		updateSql.append(" Where ROfficerCode =:ROfficerCode ");
		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(relationshipOfficerList.toArray());

		logger.debug("Leaving");
		try{	
		 this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for updating CustomerType Details
	 * @throws Exception 
	 */
	@Override
	public void updateCustomerTypeDetails(List<EquationCustomerType> customerTypes) throws Exception{
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update RMTCustTypes");
		updateSql.append(" Set CustTypeDesc = :CustTypeDesc" );
		updateSql.append(" Where CustTypeCode =:CustTypeCode");
		
		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(customerTypes.toArray());
		
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	/**
	 *  Method for updating Departments Details
	 * @throws Exception 
	 */
	@Override
	public void updateDepartmentDetails(List<EquationDepartment> departments) throws Exception{
		logger.debug("Entering");
		
		StringBuilder updateSql  = new StringBuilder("Update BMTDepartments");
		updateSql.append(" Set DeptDesc = :DeptDesc" );
		updateSql.append(" Where DeptCode =:DeptCode ");
		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(departments.toArray());
		
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	/**
	 *  Method for updating Departments Details
	 * @throws Exception 
	 */
	@Override
	public void updateCustomerGroupDetails(List<EquationCustomerGroup> customerGroups) throws Exception{
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update CustomerGroups");
		updateSql.append(" Set CustGrpDesc = :CustGrpDesc, CustGrpRO1 = :CustGrpRO1 " );
		updateSql.append(" Where CustGrpID =:CustGrpID ");
		
		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(customerGroups.toArray());
		
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for updating Account Type  Details
	 * @throws Exception 
	 */
	@Override
	public void updateAccountTypeDetails(List<EquationAccountType> accountTypes) throws Exception{
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update RMTAccountTypes");
		updateSql.append(" Set AcTypeDesc = :AcTypeDesc, AcPurpose = :AcPurpose," );
		updateSql.append(" InternalAc = :InternalAc, CustSysAc = :CustSysAc, AcTypeIsActive = :AcTypeIsActive");
		updateSql.append(" Where AcType =:AcType");
		
		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(accountTypes.toArray());
		
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for updating Account Type Nature Details
	 * @throws Exception 
	 */
	@Override
	public void updateAccountTypeNatureDetails(List<EquationAccountType> accountTypes) throws Exception{
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update AccountTypeNatures");
		updateSql.append(" Set  AcTypeNature1 = :AcTypeNature1, AcTypeNature2 = :AcTypeNature2, AcTypeNature3 = :AcTypeNature3," );
		updateSql.append(" AcTypeNature4 = :AcTypeNature4, AcTypeNature5 = :AcTypeNature5, AcTypeNature6 = :AcTypeNature6," );
		updateSql.append(" AcTypeNature7 = :AcTypeNature7, AcTypeNature8 = :AcTypeNature8, AcTypeNature9 = :AcTypeNature9," );
		updateSql.append(" AcTypeNature10 = :AcTypeNature10  Where AcType =:AcType");
		
		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(accountTypes.toArray());
		
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	/**
	 *  Method for updating CustomerRating Details
	 * @throws Exception 
	 */
	@Override
	public void updateCustomerRatingDetails(List<EquationCustomerRating> customerRatings) throws Exception{
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update CustomerRatings" );
		updateSql.append(" Set CustRatingCode = :CustRatingCode,CustRating = :CustRating" );
		updateSql.append(" Where CustID =:CustID and CustRatingType = :CustRatingType ");
		
		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(customerRatings.toArray());
		
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for updating Country Details
	 * @throws Exception 
	 */
	@Override
	public void updateCountryDetails(List<EquationCountry> countryList) throws Exception{
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update BMTCountries" );
		updateSql.append(" Set CountryDesc = :CountryDesc ");
		updateSql.append(" Where CountryCode = :CountryCode ");
		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(countryList.toArray());

		logger.debug("Leaving");
		try{	
		   this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for updating Customer Status Code Details
	 * @throws Exception 
	 */
	@Override
	public void updateCustStatusCodeDetails(List<EquationCustStatusCode> custStsList) throws Exception{
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update BMTCustStatusCodes" );
		updateSql.append(" Set DueDays = :DueDays , CustStsDescription = :CustStsDescription ");
		updateSql.append(" Where CustStsCode = :CustStsCode ");
		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(custStsList.toArray());

		logger.debug("Leaving");
		try{	
		   this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	
	/**
	 *  Method for updating Industry Details
	 * @throws Exception 
	 */
	@Override
	public void updateIndustryDetails(List<EquationIndustry> industryList) throws Exception{
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update BMTIndustries" );
		updateSql.append(" Set IndustryDesc = :IndustryDesc ");
		updateSql.append(" Where IndustryCode = :IndustryCode ");
		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(industryList.toArray());

		logger.debug("Leaving");
		try{	
		   this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	
	/**
	 *  Method for updating Branch Details
	 * @throws Exception 
	 */
	@Override
	public void updateBranchDetails(List<EquationBranch> branchList) throws Exception{
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update RMTBranches" );
		updateSql.append(" Set BranchDesc = :BranchDesc, BranchAddrLine1 = :BranchAddrLine1,");
		updateSql.append(" BranchAddrLine2 = :BranchAddrLine2, BranchFax = :BranchFax,");
		updateSql.append(" BranchTel = :BranchTel, BranchSwiftBankCde = :BranchSwiftBankCde,");
		updateSql.append(" BranchSwiftCountry = :BranchSwiftCountry, BranchSwiftLocCode = :BranchSwiftLocCode,");
		updateSql.append(" BranchSwiftBrnCde = :BranchSwiftBrnCde, BranchSortCode = :BranchSortCode");
		updateSql.append(" Where BranchCode = :BranchCode ");
		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(branchList.toArray());

		logger.debug("Leaving");
		try{	
		   this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for updating Internal Account Details
	 * @throws Exception 
	 */
	@Override
	public void updateInternalAccDetails(List<EquationInternalAccount> intAccList) throws Exception{
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update SystemInternalAccountDef" );
		updateSql.append(" Set SIAName = :SIAName,  SIAShortName = :SIAShortName,");
		updateSql.append("SIAAcType = :SIAAcType, SIANumber = :SIANumber");
		updateSql.append(" Where SIACode = :SIACode ");
		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(intAccList.toArray());

		logger.debug("Leaving");
		try{	
		   this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	
	/**
	 *  Method for saving Abuse Details
	 * @throws Exception 
	 */
	@Override
	public void saveAbuserDetails(List<EquationAbuser> abuserList) throws Exception{
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into EQNAbuserList" );
		insertSql.append(" (AbuserIDType, AbuserIDNumber, AbuserExpDate, Version, LastMntBy," );
		insertSql.append("  LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId," );
		insertSql.append("  NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:AbuserIDType, :AbuserIDNumber, :AbuserExpDate, :Version, :LastMntBy," );
		insertSql.append(" :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId," );
		insertSql.append(" :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(abuserList.toArray());

		logger.debug("Leaving");
		try{	
		   this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	
	/**
	 *  Method for Saving Master Fields Missing And Exceptional Detail
	 * @throws Exception 
	 */
	@Override
	public void saveMasterValueMissedDetail(EquationMasterMissedDetail masterMissedDetails){
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into EqtnMasterMissedDetails");
		insertSql.append(" (Module, FieldName, Description, LastMntOn)");
		insertSql.append(" Values(:Module, :FieldName, :Description, :LastMntOn)");
		
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(masterMissedDetails);
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
		}
	}
	
	
	
	/**
	 *  Method for Saving Master Fields Missing And Exceptional Details
	 * @throws Exception 
	 */
	@Override
	public void saveMasterValueMissedDetails(List<EquationMasterMissedDetail> masterMissedDetails){
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into EqtnMasterMissedDetails");
		insertSql.append(" (Module, FieldName, Description, LastMntOn)");
		insertSql.append(" Values(:Module, :FieldName, :Description, :LastMntOn)");
		
		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(masterMissedDetails.toArray());

		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
		}
	}
	
	/**
	 *  Method for saving Transaction Codes
	 * @throws Exception 
	 */
	@Override
	public void saveTransactionCodeDetails(List<EquationTransactionCode> transactionCodes) throws Exception{
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder("Insert Into BMTTransactionCode");
		insertSql.append(" (TranCode, TranDesc, TranType, TranIsActive,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:TranCode, :TranDesc, :TranType, :TranIsActive,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(transactionCodes.toArray());
		
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for saving Identity Types
	 * @throws Exception 
	 */
	@Override
	public void saveIdentityTypeDetails(List<EquationIdentityType> identityTypes) throws Exception{
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder("Insert Into BMTIdentityType");
		insertSql.append(" (IdentityType, IdentityDesc,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:IdentityType, :IdentityDesc,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(identityTypes.toArray());
		
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	
	
	/**
	 *  Method for deleting abuser Details
	 * @throws Exception 
	 */
	@Override
	public void deleteAbuserDetails() throws Exception {
		logger.debug("Entering");
		StringBuilder deleteSql = new StringBuilder(" Delete From EQNAbuserList " );
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new EquationAbuser());
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for fetching Branch Codes
	 */
	@Override
	public List<String> fetchBranchCodes() {
		logger.debug("Entering");
		
		Branch  branch = new Branch();
		StringBuilder selectSql = new StringBuilder("Select DISTINCT BranchCode From RMTBranches");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(branch);
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), beanParameters,String.class);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for fetching Branch Codes
	 */
	@Override
	public List<String> fetchExistingCustomers(Date valuedate) {
		logger.debug("Entering");
		Customer  customer = new Customer();
		customer.setLastMntOn(new Timestamp(valuedate.getTime()));
		StringBuilder selectSql = new StringBuilder(" Select Distinct AuditReference From AuditHeader ");
		selectSql.append(" Where AuditModule='CustomerDetails' And AuditTranType='A'  And  LEN(AuditReference) <= 6  ");
		selectSql.append(" And AuditDate>= :LastMntOn ");
		logger.debug("selectSql: " + selectSql.toString());
 		logger.debug("Leaving");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		logger.debug("Leaving");
		try {
			return this.auditNamedParameterJdbcTemplate.queryForList(selectSql.toString(), beanParameters,String.class);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for fetching Branch Codes
	 */
	@Override
	public List<String> fetchExistingOldCustomers() {
		logger.debug("Entering");
		Customer  customer = new Customer();
		StringBuilder selectSql = new StringBuilder(" Select Distinct CustCIF From Customers ");
		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), beanParameters,String.class);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	
	/**
	 *  Method for fetching Customer Group Codes
	 */
	@Override
	public List<Long> fetchCustomerGroupCodes() {
		logger.debug("Entering");
		
		CustomerGroup  customerGroup = new CustomerGroup();
		StringBuilder selectSql = new StringBuilder("Select Distinct CustGrpID from CustomerGroups");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerGroup);
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), beanParameters,Long.class);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for fetching Country Codes
	 */
	@Override
	public List<String> fetchCountryCodes() {
		logger.debug("Entering");
		
		Country  country = new Country();
		StringBuilder selectSql = new StringBuilder("Select DISTINCT CountryCode From BMTCountries");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(country);
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), beanParameters,String.class);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	

	/**
	 *  Method for fetching Salutation Codes
	 */
	@Override
	public List<String> fetchSalutationCodes() {
		logger.debug("Entering");
		
		Salutation  salutation = new Salutation();
		StringBuilder selectSql = new StringBuilder("Select DISTINCT SalutationCode From BMTSalutations");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(salutation);
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), beanParameters,String.class);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for fetching RelationshipOfficers  Codes
	 */
	@Override
	public List<String> fetchRelationshipOfficerCodes() {
		logger.debug("Entering");
		
		RelationshipOfficer  relationshipOfficer = new RelationshipOfficer();
		StringBuilder selectSql = new StringBuilder("Select DISTINCT ROfficerCode From RelationshipOfficers");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(relationshipOfficer);
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), beanParameters,String.class);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	
	/**
	 *  Method for fetching SubSector Codes
	 */
	@Override
	public List<SubSector> fetchSubSectorCodes() {
		logger.debug("Entering");
		
		SubSector  subSector = new SubSector();
		StringBuilder selectSql = new StringBuilder("Select DISTINCT SectorCode,SubSectorCode From BMTSubSectors");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(subSector);
		RowMapper<SubSector> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SubSector.class);
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	
	
	/**
	 *  Method for fetching Marital Status Codes
	 */
	@Override
	public List<String> fetchMaritalStatusCodes() {
		logger.debug("Entering");
		
		MaritalStatusCode  maritalStsCode = new MaritalStatusCode();
		StringBuilder selectSql = new StringBuilder("Select DISTINCT MaritalStsCode From BMTMaritalStatusCodes");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(maritalStsCode);
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), beanParameters,String.class);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	
	/**
	 *  Method for fetching Employee Status Codes
	 */
	@Override
	public List<String> fetchEmpStsCodes() {
		logger.debug("Entering");
		
		EmpStsCode  empStsCode = new EmpStsCode();
		StringBuilder selectSql = new StringBuilder("Select DISTINCT EmpStsCode From BMTEmpStsCodes");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(empStsCode);
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), beanParameters,String.class);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for fetching Currency Codes
	 */
	@Override
	public List<String> fetchCurrencyCodes() {
		logger.debug("Entering");
		
		Currency  currency = new Currency();
		StringBuilder selectSql = new StringBuilder("Select DISTINCT CcyCode From RMTCurrencies");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(currency);
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), beanParameters,String.class);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for fetching CustTypeCodes
	 */
	@Override
	public List<String> fetchCustTypeCodes() {
		logger.debug("Entering");
		
		CustomerType  customerType = new CustomerType();
		StringBuilder selectSql = new StringBuilder("Select Distinct CustTypeCode from RMTCustTypes");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerType);
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), beanParameters,String.class);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for fetching Address Types
	 */
	@Override
	public List<String> fetchAddressTypes() {
		logger.debug("Entering");
		
		AddressType  addressType = new AddressType();
		StringBuilder selectSql = new StringBuilder("Select DISTINCT AddrTypeCode From BMTAddressTypes");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(addressType);
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), beanParameters,String.class);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for fetching Customer IDs
	 */
	@Override
	public List<Long> fetchCustomerIdDetails() {
		logger.debug("Entering");
		
		Customer  customer = new Customer();
		StringBuilder selectSql = new StringBuilder("Select Distinct CustID from Customers");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), beanParameters,Long.class);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for fetching Email Types
	 */
	@Override
	public List<String> fetchEMailTypes() {
		logger.debug("Entering");
		
		EMailType  eMailType = new EMailType();
		StringBuilder selectSql = new StringBuilder("Select DISTINCT EmailTypeCode From BMTEMailTypes");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(eMailType);
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), beanParameters,String.class);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for fetching Account Types
	 */
	@Override
	public List<String> fetchAccountTypes() {
		logger.debug("Entering");
		
		AccountType  accountType = new AccountType();
		StringBuilder selectSql = new StringBuilder("Select Distinct Actype From RMTAccountTypes");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountType);
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), beanParameters,String.class);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	
	/**
	 *  Method for updating Customer Details
	 */
	@Override
	public void updateCustomerDetails(List<Customer> customerList){
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update Customers " );
		updateSql.append("  Set  CustFName = :CustFName ," );
		updateSql.append(" CustIsClosed = :CustIsClosed, CustIsActive = :CustIsActive, CustIsMinor = :CustIsMinor, CustDOB = :CustDOB, " );
		updateSql.append(" CustGenderCode = :CustGenderCode, CustPOB = :CustPOB, CustCoreBank = :CustCoreBank, CustTotalIncome = :CustTotalIncome, " );
		updateSql.append(" CustPassportNo = :CustPassportNo, CustPassportExpiry = :CustPassportExpiry, CustShrtName = :CustShrtName, " );
		updateSql.append(" CustFNameLclLng = :CustFNameLclLng, CustShrtNameLclLng = :CustShrtNameLclLng, CustIsBlocked = :CustIsBlocked," );
		updateSql.append(" CustIsDecease = :CustIsDecease, CustIsTradeFinCust = :CustIsTradeFinCust, CustClosedOn = :CustClosedOn," );
		updateSql.append(" CustFirstBusinessDate = :CustFirstBusinessDate, CustRelation = :CustRelation," );
		updateSql.append(" CustDftBranch = (CASE WHEN :CustDftBranch=''  THEN CustDftBranch ELSE :CustDftBranch END)," );
		updateSql.append(" CustGroupID = (CASE WHEN :CustGroupID = -1  THEN CustGroupID ELSE :CustGroupID END)," );
		updateSql.append(" CustTypeCode = (CASE WHEN :CustTypeCode=''  THEN CustTypeCode ELSE :CustTypeCode END)," );
		updateSql.append(" CustCtgCode = (CASE WHEN :CustCtgCode=''  THEN CustCtgCode ELSE :CustCtgCode END)," );
		updateSql.append(" CustCOB = (CASE WHEN :CustCOB='' THEN CustCOB ELSE :CustCOB END)," );
		updateSql.append(" CustParentCountry = (CASE WHEN :CustParentCountry='' THEN CustParentCountry ELSE :CustParentCountry END)," );
		updateSql.append(" CustRiskCountry = (CASE WHEN :CustRiskCountry=''  THEN CustRiskCountry ELSE :CustRiskCountry END)," );
		updateSql.append(" CustSalutationCode = (CASE WHEN :CustSalutationCode=''  THEN CustSalutationCode ELSE :CustSalutationCode END)," );
		updateSql.append(" CustRO1 = (CASE WHEN :CustRO1=''  THEN CustRO1 ELSE :CustRO1 END)," );
		 // commented below CustSector and CustSubSector due to miss match of Subsector codes in Equation and PFF 
		//updateSql.append(" CustSector = (CASE WHEN :CustSector='' THEN CustSector ELSE :CustSector END)," );
		//updateSql.append(" CustSubSector = (CASE WHEN :CustSubSector=''  THEN CustSubSector ELSE :CustSubSector END)," );
		updateSql.append(" CustMaritalSts = (CASE WHEN :CustMaritalSts=''  THEN CustMaritalSts ELSE :CustMaritalSts END)," );
		updateSql.append(" CustEmpSts = (CASE WHEN :CustEmpSts=''  THEN CustEmpSts ELSE :CustEmpSts END)," );
		updateSql.append(" CustBaseCcy = (CASE WHEN :CustBaseCcy=''  THEN CustBaseCcy ELSE :CustBaseCcy END)," );
		updateSql.append(" CustResdCountry = (CASE WHEN :CustResdCountry=''  THEN CustResdCountry ELSE :CustResdCountry END),  " );
		updateSql.append(" CustNationality = (CASE WHEN :CustNationality=''  THEN CustNationality ELSE :CustNationality END)  " );
		updateSql.append(" Where CustID = :CustID");
		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(customerList.toArray());

		logger.debug("Leaving");
		try{	
		   this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
		}
	}
	
	/**
	 *  Method for updating Customer Details
	 * @throws Exception 
	 */
	@Override
	public void updateAddressDetails(List<CustomerAddres> customerList) throws Exception{
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update CustomerAddresses ");
		updateSql.append(" Set CustAddrHNbr = :CustAddrHNbr, CustFlatNbr = :CustFlatNbr, CustPOBox = :CustPOBox," );
		updateSql.append(" CustAddrStreet = :CustAddrStreet, CustAddrLine1 = :CustAddrLine1," );
		updateSql.append(" CustAddrLine2 = :CustAddrLine2, CustAddrZIP = :CustAddrZIP, CustAddrPhone = :CustAddrPhone " );
		updateSql.append(" Where CustID =:CustID AND CustAddrType =:custAddrType");
		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(customerList.toArray());

		logger.debug("Leaving");
		try{	
		   this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for updating Customer Details
	 * @throws Exception 
	 */
	@Override
	public void updatePhoneNumberDetails(List<CustomerPhoneNumber> customerList) throws Exception{
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" Update CustomerPhoneNumbers");
		updateSql.append(" Set PhoneCountryCode = :PhoneCountryCode, PhoneAreaCode = :PhoneAreaCode," );
		updateSql.append(" PhoneNumber = :PhoneNumber" );
		updateSql.append(" Where PhoneCustID =:PhoneCustID AND PhoneTypeCode=:PhoneTypeCode");
		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(customerList.toArray());

		logger.debug("Leaving");
		try{	
		   this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for updating Customer Details
	 * @throws Exception 
	 */
	@Override
	public void updateEMailDetails(List<CustomerEMail> emailList) throws Exception{
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder(" Update CustomerEMails " );
		updateSql.append(" Set  CustEMailPriority = :CustEMailPriority, CustEMail = :CustEMail ");
		updateSql.append(" Where CustID =:CustID AND CustEMailTypeCode =:custEMailTypeCode ");
		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(emailList.toArray());

		logger.debug("Leaving");
		try{	
		   this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for updating Customer Details
	 */
	@Override
	public void updateEmploymentDetails(List<CustomerEmploymentDetail> employmentList){
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" Update CustomerEmpDetails ");
		updateSql.append(" Set  CustEmpDesg = :CustEmpDesg , CustEmpFrom = :CustEmpFrom ");
		updateSql.append(" Where CustID =:CustID and CustEmpName=:CustEmpName");
		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(employmentList.toArray());

		logger.debug("Leaving");
		try{	
		   this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
		}
	}
	
	/**
	 *  Method for updating Transaction Codes
	 */
	@Override
	public void updateTransactionCodes(List<EquationTransactionCode> transactionCodes){
		logger.debug("Entering");
		
		StringBuilder updateSql  = new StringBuilder("Update BMTTransactionCode");
		updateSql.append(" Set TranDesc = :TranDesc, TranType = :TranType" );
		updateSql.append(" Where TranCode =:TranCode ");
		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(transactionCodes.toArray());
		
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
		}
	}
	
	/**
	 *  Method for updating Identity Types
	 */
	@Override
	public void updateIdentityTypes(List<EquationIdentityType> identityTypes){
		logger.debug("Entering");
		
		StringBuilder updateSql  = new StringBuilder("Update BMTIdentityType");
		updateSql.append(" Set IdentityDesc = :IdentityDesc " );
		updateSql.append(" Where IdentityType =:IdentityType ");
		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(identityTypes.toArray());
		
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
		}
	}
	

	// ****************** Month End Downloads  *******************//
	

	/**
	 *  Method for fetching Income Account Details
	 */
	@Override
	public List<IncomeAccountTransaction> fetchIncomeAccountDetails() {
		logger.debug("Entering");
		
		IncomeAccountTransaction incomeAccountTransaction = new IncomeAccountTransaction();
		StringBuilder selectSql = new StringBuilder("Select Distinct IncomeAccount From FinPftDetails ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(incomeAccountTransaction);
		RowMapper<IncomeAccountTransaction> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(IncomeAccountTransaction.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (Exception e) {
			logger.error("Exception: ", e);
		}
		return null;
	}
	
	
	/**
	 *  Method for checking Income Transactions Exist For Current Month
	 */
	@Override
	public boolean checkIncomeTransactionsExist(IncomeAccountTransaction incomeAccountTransaction) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("Select count(*) from IncomeAccTxns ");
		selectSql.append(" Where LastMntOn = :LastMntOn");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(incomeAccountTransaction);

		try {
			int result = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
			logger.debug("Leaving");
			return result > 0 ? true : false;
		}catch (Exception e) {
			logger.error("Exception: ", e);
		}
		return false;
	}
	
	
	
	/**
	 *  Method for saving Income Account Transactions
	 */
	@Override
	public void saveIncomeAccTransactions(List<IncomeAccountTransaction> incomeAccountTransactions){
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into IncomeAccTxns" );
		insertSql.append(" (IncomeAccount, ProfitAmount, ManualAmount, PffPostingAmount, LastMntOn)" );
		insertSql.append(" Values(:IncomeAccount, :ProfitAmount, :ManualAmount, :PffPostingAmount, :LastMntOn)");
		
		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(incomeAccountTransactions.toArray());
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
		}
	}
	
	/**
	 *  Method for fetching FinanceType Details
	 */
	@Override
	public List<FinanceType> fetchFinanceTypeDetails() {
		logger.debug("Entering");
		
		FinanceType  financeType = new FinanceType();
		StringBuilder selectSql = new StringBuilder("SELECT  FinType, FinCategory, FinDivision,");
		selectSql.append(" FinCcy, FinAcType  From  RMTFinanceTypes");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeType);
		RowMapper<FinanceType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceType.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for fetching FinTypeAccounting Details
	 */
	@Override
	public List<FinTypeAccounting> fetchFinTypeAccountings(String event){
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Event", event);
		StringBuilder selectSql = new StringBuilder("SELECT FinType, Event,	AccountSetID From FinTypeAccounting ");
		if(StringUtils.isNotEmpty(event)){
			selectSql.append(" Where Event = :Event ");
		}
		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinTypeAccounting> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinTypeAccounting.class);
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			return null;
		}
	}
	
	
	/**
	 *  Method for fetching Currency Details
	 */
	@Override
	public List<TransactionEntry> fetchTransactionEntryDetails(long accountSetID) {
		logger.debug("Entering");
		
		TransactionEntry  transactionEntry = new TransactionEntry();
		transactionEntry.setAccountSetid(accountSetID);
		StringBuilder selectSql = new StringBuilder("Select AccountSetid, TransOrder, TransDesc," );
		selectSql.append(" Debitcredit, ShadowPosting, Account, AccountType, AccountBranch, AccountSubHeadRule,");
		selectSql.append(" TranscationCode, RvsTransactionCode, AmountRule, FeeCode, ChargeType, EntryByInvestment,  ");
		selectSql.append(" OpenNewFinAc  From RMTTransactionEntry Where AccountSetid = :AccountSetid and Debitcredit='C' ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(transactionEntry);
		RowMapper<TransactionEntry> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(TransactionEntry.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	
	/**
	 * Method for Updation of Repayment Account ID on Finance Basic Details Maintenance
	 */
	
	@Override
	public void updateFinProfitIncomeAccounts(List<FinanceProfitDetail> incomeAccounts) {
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update FinPftDetails ");
		updateSql.append(" Set IncomeAccount = FinBranch + :IncomeAccount + "); 
		updateSql.append(" (Select CcyNumber from RMTCurrencies Where CcyCode = FinCcy) ");
		updateSql.append(" Where FinType = :FinType");
		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(incomeAccounts.toArray());

		logger.debug("Leaving");
		try{	
		   this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
		}
    }
	
	
	/**
	 *  Method for fetching Address Types
	 */
	@Override
	public List<CustomerAddres> fetchExisitingCustomerAddress() {
		logger.debug("Entering");
		
		CustomerAddres  customerAddres = new CustomerAddres();
		StringBuilder selectSql = new StringBuilder("Select CustID,CustAddrType From CustomerAddresses");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerAddres);
		RowMapper<CustomerAddres> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerAddres.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for fetching CustomerPhoneNumbers
	 */
	@Override
	public List<CustomerPhoneNumber> fetchExisitingCustPhoneNumbers() {
		logger.debug("Entering");
		
		CustomerPhoneNumber  customerPhoneNumber = new CustomerPhoneNumber();
		StringBuilder selectSql = new StringBuilder("Select PhoneCustID,PhoneTypeCode From CustomerPhoneNumbers");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerPhoneNumber);
		RowMapper<CustomerPhoneNumber> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerPhoneNumber.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	/**
	 *  Method for fetching CustomerEMails
	 */
	@Override
	public List<CustomerEMail> fetchExisitingCustEmails() {
		logger.debug("Entering");
		
		CustomerEMail  customerEMail = new CustomerEMail();
		StringBuilder selectSql = new StringBuilder("Select CustID,CustEMailTypeCode From CustomerEMails");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerEMail);
		RowMapper<CustomerEMail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerEMail.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
     //****************** Single Customer Download  *******************//
	
	@Override
	public void saveCustomerAddresses(List<CustomerAddres> customerAddres) throws Exception {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into CustomerAddresses");
		insertSql.append(" (CustID, CustAddrType, CustAddrHNbr, CustFlatNbr, CustAddrStreet," );
		insertSql.append(" CustAddrLine1, CustAddrLine2, CustPOBox, CustAddrCountry, CustAddrProvince," );
		insertSql.append(" CustAddrCity, CustAddrZIP, CustAddrPhone,CustAddrFrom,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId," );
		insertSql.append(" NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CustID, :CustAddrType, :CustAddrHNbr, :CustFlatNbr, :CustAddrStreet,");
		insertSql.append(" :CustAddrLine1, :CustAddrLine2, :CustPOBox, :CustAddrCountry, :CustAddrProvince,");
		insertSql.append(" :CustAddrCity, :CustAddrZIP, :CustAddrPhone, :CustAddrFrom,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(customerAddres.toArray());
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	
	@Override
	public void saveCustomerPhoneNumbers(List<CustomerPhoneNumber> customerPhoneNumbers) throws Exception {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into CustomerPhoneNumbers");
		insertSql.append(" (PhoneCustID, PhoneTypeCode, PhoneCountryCode, PhoneAreaCode, PhoneNumber," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:PhoneCustID, :PhoneTypeCode, :PhoneCountryCode,:PhoneAreaCode,:PhoneNumber,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(customerPhoneNumbers.toArray());
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	@Override
	public void saveCustomerEmails(List<CustomerEMail> customerEMails) throws Exception {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into CustomerEMails");
		insertSql.append(" (CustID, CustEMailTypeCode, CustEMailPriority, CustEMail," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CustID, :CustEMailTypeCode, :CustEMailPriority, :CustEMail,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(customerEMails.toArray());
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}
	
	@Override
	public void saveRatingDetails(List<CustomerRating> customerRatings) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into CustomerRatings" );
		insertSql.append(" (CustID, CustRatingType, CustRatingCode, CustRating, ValueType," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:CustID, :CustRatingType, :CustRatingCode, :CustRating, :ValueType, " );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(customerRatings.toArray());
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
		}
	}
	
	@Override
	public void updateObjectDetails(String updateQuery,Object object) {
		int recordCount = 0;
		logger.debug("Entering");
		logger.debug("updateSql: "+ updateQuery);
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(object);
		recordCount = this.namedParameterJdbcTemplate.update(updateQuery, beanParameters);
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
		}
		logger.debug("Leaving");
	}
	
}
