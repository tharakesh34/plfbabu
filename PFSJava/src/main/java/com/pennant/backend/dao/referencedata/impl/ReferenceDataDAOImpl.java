package com.pennant.backend.dao.referencedata.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.referencedata.ReferenceDataDAO;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.TargetDetail;
import com.pennant.backend.model.rmtmasters.CustomerType;
import com.pennant.backend.model.staticparms.Language;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.GeneralDepartment;
import com.pennant.backend.model.systemmasters.IncomeType;
import com.pennant.backend.model.systemmasters.Industry;
import com.pennant.backend.model.systemmasters.MaritalStatusCode;
import com.pennant.backend.model.systemmasters.Salutation;
import com.pennant.backend.model.systemmasters.Sector;
import com.pennant.backend.model.systemmasters.Segment;
import com.pennant.webservice.model.ReferenceData;

public class ReferenceDataDAOImpl implements ReferenceDataDAO {

	private static final Logger logger = Logger.getLogger(ReferenceDataDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public ReferenceDataDAOImpl() {
		super();
	}

	/**
	 * Method for fetch PFF Master Table name by mapping the category
	 * 
	 * @param category
	 * @return String
	 * 
	 */
	@Override
	public String getPFFMasterName(String category) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Category", category);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT PFFTableName  FROM PFFMasters");
		selectSql.append(" Where Category =:Category");

		logger.debug("selectSql: " + selectSql.toString());

		RowMapper<String> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(String.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			return  null;
		}
	}

	/**
	 * Method for Fetching MDM code by providing table name
	 * 
	 * @return String
	 * @param mdmCode
	 * @param localTableName
	 */
	@Override
	public String getMasterCode(String mdmCode, String localTableName) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Code", mdmCode);
		source.addValue("TableName", localTableName);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT Code FROM  =:TableName");
		selectSql.append(" Where Code =:Code");

		logger.debug("selectSql: " + selectSql.toString());

		RowMapper<String> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(String.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			return  null;
		}
	}

	/**
	 * Method for save the new MDM codes
	 * 
	 */
	@Override
	public void saveMCMMasters(List<ReferenceData> saveMasterList, String tableName) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into "+ tableName);
		insertSql.append(" (Code, Value, Description )");
		insertSql.append(" Values( ");
		insertSql.append(" :Code, :Value, :Description)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(saveMasterList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	/**
	 * Method for update the MDM codes
	 * 
	 */
	@Override
	public void updateMCMMasters(List<ReferenceData> updateMasterList, String tableName) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update "+tableName);
		updateSql.append(" Set Code = :Code, Description = :Description" );
		updateSql.append(" Where Value =:Value");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(updateMasterList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);

		logger.debug("Leaving");

	}

	@Override
	public void saveCurrencyMaster(List<Currency> saveCcyMasterList) {
		logger.debug("Entering ");

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

		logger.debug("insertSql: "+ insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(saveCcyMasterList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);

		logger.debug("Leaving ");
	}

	@Override
	public void updateCurrencyMaster(List<Currency> updateCcyMasterList) {
		logger.debug("Entering ");

		StringBuilder updateSql = new StringBuilder("Update RMTCurrencies" );
		updateSql.append(" Set CcyDesc = :CcyDesc ");
		updateSql.append(" Where CcyCode =:CcyCode ");

		logger.debug("updateSql: "+ updateSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(updateCcyMasterList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);

		logger.debug("Leaving ");
	}

	/**
	 * Method for Processing Currency Details
	 */
	@Override
	public List<Currency> fetchCurrecnyDetails() {
		logger.debug("Entering");

		Currency currency = new Currency();
		StringBuilder selectSql = new StringBuilder("Select CcyCode From RMTCurrencies");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(currency);
		RowMapper<Currency> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Currency.class);

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
	public List<Industry> fetchIndustryDetails() {
		logger.debug("Entering");

		Industry  industry = new Industry();
		StringBuilder selectSql = new StringBuilder("Select  IndustryCode  from BMTIndustries");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(industry);
		RowMapper<Industry> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Industry.class);

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
	public List<CustomerType> fetchCustTypeDetails() {
		logger.debug("Entering");

		CustomerType  customerType = new CustomerType();
		StringBuilder selectSql = new StringBuilder("Select  CustTypeCode  from RMTCustTypes");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerType);
		RowMapper<CustomerType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerType.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	@Override
	public void saveIndustryMaster(List<Industry> saveMasterList) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into BMTIndustries" );
		insertSql.append(" (IndustryCode, SubSectorCode, IndustryDesc, IndustryLimit, IndustryIsActive," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:IndustryCode, :SubSectorCode, :IndustryDesc, :IndustryLimit, :IndustryIsActive," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(saveMasterList.toArray());
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	@Override
	public void updateIndustryMaster(List<Industry> updateMasterList) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update BMTIndustries" );
		updateSql.append(" Set IndustryDesc = :IndustryDesc ");
		updateSql.append(" Where IndustryCode = :IndustryCode ");
		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(updateMasterList.toArray());

		logger.debug("Leaving");
		try{
			this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	@Override
	public void saveCustTypeDetails(List<CustomerType> custTypeList) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into RMTCustTypes");
		insertSql.append(" (CustTypeCode, CustTypeCtg, CustTypeDesc, CustTypeIsActive,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:CustTypeCode, :CustTypeCtg, :CustTypeDesc, :CustTypeIsActive,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(custTypeList.toArray());

		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	@Override
	public void updateCustTypeDetails(List<CustomerType> custTypeList) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update RMTCustTypes");
		updateSql.append(" Set CustTypeDesc = :CustTypeDesc" );
		updateSql.append(" Where CustTypeCode =:CustTypeCode");

		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(custTypeList.toArray());

		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	@Override
	public List<Sector> fetchSectorDetails() {
		logger.debug("Entering");

		Sector  sector = new Sector();
		StringBuilder selectSql = new StringBuilder("Select  SectorCode  from BMTSectors");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sector);
		RowMapper<Sector> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Sector.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	@Override
	public void saveSectorDetails(List<Sector> saveSectorList) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into BMTSectors");
		insertSql.append(" (SectorCode, SectorDesc, SectorLimit, SectorIsActive,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:SectorCode, :SectorDesc, :SectorLimit, :SectorIsActive,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		insertSql.append(" :RecordType, :WorkflowId)");

		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(saveSectorList.toArray());

		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	@Override
	public void updateSectorDetails(List<Sector> updateSectorList) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update BMTSectors");
		updateSql.append(" Set SectorDesc = :SectorDesc" );
		updateSql.append(" Where SectorCode =:SectorCode");

		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(updateSectorList.toArray());

		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	@Override
	public List<Country> fetchCountryDetails() {
		logger.debug("Entering");

		Country country = new Country();
		StringBuilder selectSql = new StringBuilder("Select  CountryCode  from BMTCountries");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(country);
		RowMapper<Country> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Country.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	@Override
	public void saveCountryDetails(List<Country> countryList) {
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

	@Override
	public void updateCountryDetails(List<Country> countryList) {
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

	@Override
	public List<MaritalStatusCode> fetchMaritalStsDetails() {
		logger.debug("Entering");

		MaritalStatusCode maritalStatusCode = new MaritalStatusCode();
		StringBuilder selectSql = new StringBuilder("Select  MaritalStsCode  from BMTMaritalStatusCodes");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(maritalStatusCode);
		RowMapper<MaritalStatusCode> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(MaritalStatusCode.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	@Override
	public void saveMaritalStsDetails(List<MaritalStatusCode> maritalStsList) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into BMTMaritalStatusCodes");
		insertSql.append(" (MaritalStsCode, MaritalStsDesc, MaritalStsIsActive,SystemDefault,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:MaritalStsCode, :MaritalStsDesc, :MaritalStsIsActive, :SystemDefault,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		insertSql.append(" :RecordType, :WorkflowId)");

		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(maritalStsList.toArray());
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
		logger.debug("Leaving");
	}

	@Override
	public void updateMaritalStsDetails(List<MaritalStatusCode> maritalStsList) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update BMTMaritalStatusCodes" );
		updateSql.append(" Set MaritalStsDesc = :MaritalStsDesc ");
		updateSql.append(" Where MaritalStsCode = :MaritalStsCode ");
		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(maritalStsList.toArray());

		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	@Override
	public List<Branch> fetchBranchDetails() {
		logger.debug("Entering");

		Branch branch = new Branch();
		StringBuilder selectSql = new StringBuilder("Select  BranchCode  from RMTBranches");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(branch);
		RowMapper<Branch> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Branch.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	@Override
	public void saveBranchDetails(List<Branch> branchList) {
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

	@Override
	public void updateBranchDetails(List<Branch> branchList) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update RMTBranches" );
		updateSql.append(" Set BranchDesc = :BranchDesc ");
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

	@Override
	public List<Salutation> fetchSalutationDetails() {
		logger.debug("Entering");

		Salutation salutation = new Salutation();
		StringBuilder selectSql = new StringBuilder("Select  SalutationCode  from BMTSalutations");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(salutation);
		RowMapper<Salutation> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Salutation.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	@Override
	public void saveSalutationDetails(List<Salutation> salutationList) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into BMTSalutations");
		insertSql.append(" (SalutationCode, SaluationDesc, SalutationIsActive,SalutationGenderCode,SystemDefault,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:SalutationCode, :SaluationDesc, :SalutationIsActive, :SalutationGenderCode,:SystemDefault,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		insertSql.append(" :RecordType, :WorkflowId)");

		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(salutationList.toArray());
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	@Override
	public void updateSalutationDetails(List<Salutation> salutationList) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update BMTSalutations" );
		updateSql.append(" Set SaluationDesc = :SaluationDesc ");
		updateSql.append(" Where SalutationCode = :SalutationCode ");
		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(salutationList.toArray());

		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	@Override
	public List<Language> fetchLanguageDetails() {
		logger.debug("Entering");

		Language language = new Language();
		StringBuilder selectSql = new StringBuilder("Select LngCode from BMTLanguage");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(language);
		RowMapper<Language> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Language.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	@Override
	public void saveLanguageDetails(List<Language> languageList) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into BMTLanguage");
		insertSql.append(" (LngCode, LngDesc, LngNumber, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:LngCode, :LngDesc, :LngNumber, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, ");
		insertSql.append(" :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(languageList.toArray());
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	@Override
	public void updateLanguageDetails(List<Language> languageList) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update BMTLanguage" );
		updateSql.append(" Set LngDesc = :LngDesc ");
		updateSql.append(" Where LngCode = :LngCode ");
		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(languageList.toArray());

		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	@Override
	public List<Segment> fetchSegmentDetails() {
		logger.debug("Entering");

		Segment segment = new Segment();
		StringBuilder selectSql = new StringBuilder("Select SegmentCode from BMTSegments");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(segment);
		RowMapper<Segment> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Segment.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	@Override
	public void saveSegmentDetails(List<Segment> segmentList) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into BMTSegments");
		insertSql.append(" (SegmentCode, SegmentDesc, SegmentIsActive,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:SegmentCode, :SegmentDesc, :SegmentIsActive,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		insertSql.append(" :RecordType, :WorkflowId)");

		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(segmentList.toArray());
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	@Override
	public void updateSegmentDetails(List<Segment> segmentList) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update BMTSegments" );
		updateSql.append(" Set SegmentDesc = :SegmentDesc ");
		updateSql.append(" Where SegmentCode = :SegmentCode ");
		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(segmentList.toArray());

		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	@Override
	public List<GeneralDepartment> fetchgenDepartmentDetails() {
		logger.debug("Entering");

		GeneralDepartment genDepartment = new GeneralDepartment();
		StringBuilder selectSql = new StringBuilder("Select GenDepartment from RMTGenDepartments");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(genDepartment);
		RowMapper<GeneralDepartment> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(GeneralDepartment.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	@Override
	public void saveDepartmentDetails(List<GeneralDepartment> genDepartmentList) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into RMTGenDepartments" );
		insertSql.append(" (GenDepartment, GenDeptDesc,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:GenDepartment, :GenDeptDesc," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(genDepartmentList.toArray());
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	@Override
	public void updateDepartmentDetails(List<GeneralDepartment> genDepartmentList) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update RMTGenDepartments" );
		updateSql.append(" Set GenDeptDesc = :GenDeptDesc ");
		updateSql.append(" Where GenDepartment = :GenDepartment ");
		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(genDepartmentList.toArray());

		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	@Override
	public List<IncomeType> fetchIncomeTypeDetails() {
		logger.debug("Entering");

		IncomeType incomeType = new IncomeType();
		StringBuilder selectSql = new StringBuilder("Select IncomeTypeCode from BMTIncomeTypes");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(incomeType);
		RowMapper<IncomeType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(IncomeType.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	@Override
	public void saveIncomeTypeDetails(List<IncomeType> incomeTypeList) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into BMTIncomeTypes");
		insertSql.append(" ( IncomeExpense, Category,IncomeTypeCode, IncomeTypeDesc,Margin, IncomeTypeIsActive," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId," );
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:IncomeExpense,:Category,:IncomeTypeCode, :IncomeTypeDesc,:Margin, :IncomeTypeIsActive, " );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");

		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(incomeTypeList.toArray());
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	@Override
	public void updateIncomeTypeDetails(List<IncomeType> incomeTypeList) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update BMTIncomeTypes" );
		updateSql.append(" Set IncomeTypeDesc = :IncomeTypeDesc ");
		updateSql.append(" Where IncomeTypeCode = :IncomeTypeCode ");
		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(incomeTypeList.toArray());

		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	@Override
	public List<TargetDetail> fetchTargetDetails() {
		logger.debug("Entering");

		TargetDetail targetDetail = new TargetDetail();
		StringBuilder selectSql = new StringBuilder("Select TargetCode from TargetDetails");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(targetDetail);
		RowMapper<TargetDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(TargetDetail.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		}catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	@Override
	public void saveTargetDetails(List<TargetDetail> targetDetailList) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into TargetDetails");
		insertSql.append(" (TargetCode, TargetDesc, Active,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId," );
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values (:TargetCode, :TargetDesc, :Active,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");

		logger.debug("selectSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(targetDetailList.toArray());
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	@Override
	public void updateTargetDetails(List<TargetDetail> targetDetailList) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update TargetDetails" );
		updateSql.append(" Set TargetDesc = :TargetDesc ");
		updateSql.append(" Where TargetCode = :TargetCode ");
		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(targetDetailList.toArray());

		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * Method for fetch list of MDM codes in respective table
	 * 
	 *  @param tableName
	 *  @return List<ReferenceData>
	 * 
	 */
	@Override
	public List<ReferenceData> fetchMDMCodes(String tableName) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("TableName", tableName);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT Code FROM  =:TableName");

		logger.debug("selectSql: " + selectSql.toString());

		RowMapper<ReferenceData> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ReferenceData.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			return  null;
		}
	}

}
