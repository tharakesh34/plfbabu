package com.pennant.equation.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.coreinterface.model.EquationAccountType;
import com.pennant.coreinterface.model.EquationCurrency;
import com.pennant.coreinterface.model.EquationCustomerGroup;
import com.pennant.coreinterface.model.EquationCustomerRating;
import com.pennant.coreinterface.model.EquationCustomerType;
import com.pennant.coreinterface.model.EquationDepartment;
import com.pennant.coreinterface.model.EquationRelationshipOfficer;
import com.pennant.coreinterface.model.FinIncomeAccount;
import com.pennant.coreinterface.model.IncomeAccountTransaction;
import com.pennant.equation.dao.CoreInterfaceDAO;

public class CoreInterfaceDAOImpl implements CoreInterfaceDAO{
	private static Logger logger = Logger.getLogger(CoreInterfaceDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
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
			logger.error(e);
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
		StringBuilder selectSql = new StringBuilder("Select * From RelationshipOfficers");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(relationshipOfficer);
		RowMapper<EquationRelationshipOfficer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(EquationRelationshipOfficer.class);
		
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (DataAccessException e) {
			logger.error(e);
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
		StringBuilder selectSql = new StringBuilder("Select * From RMTCustTypes");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerType);
		RowMapper<EquationCustomerType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(EquationCustomerType.class);
		
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (DataAccessException e) {
			logger.error(e);
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
		StringBuilder selectSql = new StringBuilder("Select * From BMTDepartments");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(department);
		RowMapper<EquationDepartment> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(EquationDepartment.class);
		
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (DataAccessException e) {
			logger.error(e);
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
		StringBuilder selectSql = new StringBuilder("Select * From CustomerGroups");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerGroup);
		RowMapper<EquationCustomerGroup> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(EquationCustomerGroup.class);
		
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (DataAccessException e) {
			logger.error(e);
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
		StringBuilder selectSql = new StringBuilder("Select * From RMTAccountTypes");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(accountType);
		RowMapper<EquationAccountType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(EquationAccountType.class);
		
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (DataAccessException e) {
			logger.error(e);
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
		StringBuilder selectSql = new StringBuilder("Select * From CustomerRatings");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerRating);
		RowMapper<EquationCustomerRating> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(EquationCustomerRating.class);
		
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (DataAccessException e) {
			logger.error(e);
			throw e;
		}
	}
	
	/**
	 *  Method for saving Currency Details
	 */
	@Override
	public void saveCurrecnyDetails(List<EquationCurrency> currencyList){
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
			e.printStackTrace();
		}
	}
	
	/**
	 *  Method for saving RelationshipOfficer Details
	 */
	@Override
	public void saveRelationShipOfficerDetails(List<EquationRelationshipOfficer> relationshipOfficerList){
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
			e.printStackTrace();
		}
	}
	
	/**
	 *  Method for saving CustomerType Details
	 */
	@Override
	public void saveCustomerTypeDetails(List<EquationCustomerType> customerTypes){
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
			e.printStackTrace();
		}
	}
	
	/**
	 *  Method for saving Departments Details
	 */
	@Override
	public void saveDepartmentDetails(List<EquationDepartment> departments){
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
			e.printStackTrace();
		}
	}
	
	/**
	 *  Method for saving Departments Details
	 */
	@Override
	public void saveCustomerGroupDetails(List<EquationCustomerGroup> customerGroups){
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
			e.printStackTrace();
		}
	}
	
	/**
	 *  Method for saving Account Type  Details
	 */
	@Override
	public void saveAccountTypeDetails(List<EquationAccountType> accountTypes){
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
			e.printStackTrace();
		}
	}
	/**
	 *  Method for saving Account Type Nature Details
	 */
	@Override
	public void saveAccountTypeNatureDetails(List<EquationAccountType> accountTypes){
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
			e.printStackTrace();
		}
	}
	/**
	 *  Method for saving Customer Rating Details
	 */
	@Override
	public void saveCustomerRatingDetails(List<EquationCustomerRating> customerRatings){
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
			e.printStackTrace();
		}
	}

	/**
	 *  Method for updating Currency Details
	 */
	@Override
	public void updateCurrecnyDetails(List<EquationCurrency> currencyList){
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
			e.printStackTrace();
		}
	}
	
	/**
	 *  Method for updating RelationshipOfficer Details
	 */
	@Override
	public void updateRelationShipOfficerDetails(List<EquationRelationshipOfficer> relationshipOfficerList){
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
			e.printStackTrace();
		}
	}
	
	/**
	 *  Method for updating CustomerType Details
	 */
	@Override
	public void updateCustomerTypeDetails(List<EquationCustomerType> customerTypes){
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
			e.printStackTrace();
		}
	}

	/**
	 *  Method for updating Departments Details
	 */
	@Override
	public void updateDepartmentDetails(List<EquationDepartment> departments){
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
			e.printStackTrace();
		}
	}

	/**
	 *  Method for updating Departments Details
	 */
	@Override
	public void updateCustomerGroupDetails(List<EquationCustomerGroup> customerGroups){
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
			e.printStackTrace();
		}
	}
	
	/**
	 *  Method for updating Account Type  Details
	 */
	@Override
	public void updateAccountTypeDetails(List<EquationAccountType> accountTypes){
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update RMTAccountTypes");
		updateSql.append(" Set  AcTypeDesc = :AcTypeDesc, AcPurpose = :AcPurpose," );
		updateSql.append(" InternalAc = :InternalAc,CustSysAc = :CustSysAc, AcTypeIsActive = :AcTypeIsActive");
		updateSql.append(" Where AcType =:AcType");
		
		logger.debug("selectSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(accountTypes.toArray());
		
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  Method for updating Account Type Nature Details
	 */
	@Override
	public void updateAccountTypeNatureDetails(List<EquationAccountType> accountTypes){
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
			e.printStackTrace();
		}
	}
	/**
	 *  Method for updating CustomerRating Details
	 */
	@Override
	public void updateCustomerRatingDetails(List<EquationCustomerRating> customerRatings){
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
			e.printStackTrace();
		}
	}
	
	

	// ++++++++++++++++++ Month End Downloads  +++++++++++++++++++//
	
	
	
	/**
	 *  Method for saving FinIncomeAccount Details
	 */
	@Override
	public void saveIncomeAccounts(FinIncomeAccount finIncomeAccount) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder(" Insert into FinIncomeAccounts Select *, :LastMntOn  from ( " );		
		insertSql.append(" Select T1.FinReference,T1.Account as IncomeAccount,T3.CustId,T3.FinType,T1.AcCcy" );
	    insertSql.append(" from Postings T1 With(NoLock) Inner Join " );
		insertSql.append(" (Select finReference,MAX(ValueDate)ValueDate from Postings" );
		insertSql.append(" Where  TranDesc='Murabaha Income' and FinEvent='AMZ' Group By " );
		insertSql.append(" finReference)T2 ON   T1.finReference=T2.finReference and T1.ValueDate=T2.ValueDate" );
		insertSql.append(" INNER JOIN FinPftDetails T3 With(NoLock) ON T3.FinReference=T2.finReference" );
		insertSql.append(" Where  TranDesc='Murabaha Income' and FinEvent='AMZ' Union	" );
		insertSql.append(" Select T1.FinReference,T1.Account,T3.CustId,T3.FinType,T1.AcCcy from Postings" );
	    insertSql.append(" T1 With(NoLock) Inner Join " );
	    insertSql.append(" (Select finReference,MAX(ValueDate)ValueDate from Postings With(NoLock)" );
	    insertSql.append(" 	Where   FinEvent='AMZSUSP' and TranDesc='Murabaha Income' Group By finReference)T2 " );
		insertSql.append(" ON T1.finReference=T2.finReference and T1.ValueDate=T2.ValueDate " );
		insertSql.append(" INNER JOIN FinPftDetails T3 With(NoLock) ON T3.FinReference=T2.finReference" );
		insertSql.append(" Where   FinEvent='AMZSUSP' and TranDesc='Murabaha Income')T" );
		
		
		logger.debug("deleteSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finIncomeAccount);
		logger.debug("Leaving");
		try{	
			this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		}catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	
	/**
	 *  Method for fetching Income Account Details
	 */
	@Override
	public List<FinIncomeAccount> fetchIncomeAccountDetails(FinIncomeAccount finIncomeAccount) {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder("Select Distinct IncomeAccount,LastMntOn From FinIncomeAccounts ");
		selectSql.append(" Where LastMntOn = :LastMntOn");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finIncomeAccount);
		RowMapper<FinIncomeAccount> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinIncomeAccount.class);

		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
		}catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
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
			int result = this.namedParameterJdbcTemplate.queryForInt(selectSql.toString(), beanParameters);
			logger.debug("Leaving");
			return result > 0 ? true : false;
		}catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
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
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	
}
