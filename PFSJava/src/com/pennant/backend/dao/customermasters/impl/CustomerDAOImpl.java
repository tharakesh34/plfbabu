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
 * FileName    		:  CustomerDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  27-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2011       Pennant	                 0.1                                            * 
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.Abuser;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.ProspectCustomer;
import com.pennant.backend.model.reports.AvailFinance;
import com.pennant.backend.model.reports.AvailPastDue;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>Customer model</b> class.<br>
 * 
 */
public class CustomerDAOImpl extends BasisNextidDaoImpl<Customer> implements CustomerDAO {

	private static Logger logger = Logger.getLogger(CustomerDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new Customer 
	 * @return Customer
	 */
	@Override
	public Customer getCustomer(boolean createNew) {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=null;
		if(!createNew){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Customer");
		}else{
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CustomerQDE");
		}
		Customer customer= new Customer();
		if (workFlowDetails!=null){
			customer.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return customer;
	}

	/**
	 * This method get the module from method getCustomer() and set the new record flag as true and return Customer()   
	 * @return Customer
	 */
	@Override
	public Customer getNewCustomer(boolean createNew) {
		logger.debug("Entering");
		Customer customer = getCustomer(createNew);
		customer.setNewRecord(true);
		customer.setCustCOB(SystemParameterDetails.getSystemParameterValue("CURR_SYSTEM_COUNTRY").toString());
		customer.setCustBaseCcy(SystemParameterDetails.getSystemParameterValue("APP_DFT_CURR").toString());
		customer.setCustLng(SystemParameterDetails.getSystemParameterValue("APP_LNG").toString());
		customer.setCustParentCountry(SystemParameterDetails.getSystemParameterValue("APP_DFT_NATION").toString());
		customer.setCustResdCountry(SystemParameterDetails.getSystemParameterValue("CURR_SYSTEM_COUNTRY").toString());
		customer.setCustRiskCountry(SystemParameterDetails.getSystemParameterValue("APP_DFT_NATION").toString());
		customer.setCustNationality(SystemParameterDetails.getSystemParameterValue("APP_DFT_NATION").toString());
		customer.setCustGroupID(0);
		logger.debug("Leaving");
		return customer;
	}

	/**
	 * Fetch the Record  Customers details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Customer
	 */
	@Override
	public Customer getCustomerByID(final long id, String type) {
		logger.debug("Entering");
		Customer customer = new Customer();
		customer.setId(id);		
		
		StringBuilder selectSql = new StringBuilder("SELECT CustID, CustCIF, CustCoreBank, CustCtgCode, CustTypeCode,");
		selectSql.append(" CustSalutationCode, CustFName, CustMName, CustLName, CustShrtName, CustFNameLclLng, CustMNameLclLng,");
		selectSql.append(" CustLNameLclLng, CustShrtNameLclLng, CustDftBranch, CustGenderCode, CustDOB, CustPOB, CustCOB,");
		selectSql.append(" CustPassportNo, CustMotherMaiden, CustIsMinor, CustReferedBy, CustDSA, CustDSADept, CustRO1, CustRO2,");
		selectSql.append(" CustGroupID, CustSts, CustStsChgDate, CustGroupSts, CustIsBlocked, CustIsActive, CustIsClosed,");
		selectSql.append(" CustInactiveReason, CustIsDecease, CustIsDormant, CustIsDelinquent, CustIsTradeFinCust, CustIsStaff,");
		selectSql.append(" CustTradeLicenceNum , CustTradeLicenceExpiry, CustPassportExpiry, CustVisaNum , CustVisaExpiry," );
		selectSql.append(" CustStaffID, CustIndustry, CustSector, CustSubSector, CustProfession, CustTotalIncome, CustMaritalSts,");
		selectSql.append(" CustEmpSts, CustSegment, CustSubSegment, CustIsBlackListed, CustBLRsnCode, CustIsRejected, CustRejectedRsn," );
		selectSql.append(" CustBaseCcy, CustLng, CustParentCountry, CustResdCountry, CustRiskCountry, CustNationality, CustClosedOn, " );
		selectSql.append("CustStmtFrq, CustIsStmtCombined, CustStmtLastDate, CustStmtNextDate, CustStmtDispatchMode, CustFirstBusinessDate,");
		selectSql.append(" CustAddlVar81, CustAddlVar82, CustAddlVar83, CustAddlVar84, CustAddlVar85, CustAddlVar86, CustAddlVar87," );
		selectSql.append(" CustAddlVar88, CustAddlVar89, CustAddlDate1, CustAddlDate2, CustAddlDate3, CustAddlDate4, CustAddlDate5," );
		selectSql.append(" CustAddlVar1, CustAddlVar2, CustAddlVar3, CustAddlVar4, CustAddlVar5, CustAddlVar6, CustAddlVar7, CustAddlVar8, " );
		selectSql.append(" CustAddlVar9, CustAddlVar10, CustAddlVar11, CustAddlDec1, CustAddlDec2, CustAddlDec3, CustAddlDec4, CustAddlDec5," );
		selectSql.append(" CustAddlInt1, CustAddlInt2, CustAddlInt3, CustAddlInt4, CustAddlInt5,DedupFound,SkipDedup,CustTotalExpense,CustBlackListDate,NoOfDependents,CustCRCPR," );
		selectSql.append(" JointCust, JointCustName, JointCustDob, custRelation, ContactPersonName, EmailID, PhoneNumber, SalariedCustomer," );
		
		if(type.contains("View")){
			selectSql.append(" lovDescCustTypeCodeName, lovDescCustMaritalStsName, lovDescCustEmpStsName, lovDescCustBaseCcyName, lovDescCustStsName,");
			selectSql.append(" lovDescCustIndustryName, lovDescCustSectorName, lovDescCustSubSectorName, lovDescCustProfessionName, lovDescCustCOBName ,");
			selectSql.append(" lovDescCustSegmentName, lovDescCustNationalityName, lovDescCustGenderCodeName, lovDescCustDSADeptName, lovDescCustRO1Name, ");
			selectSql.append(" lovDescCustGroupStsName, lovDescCustDftBranchName, lovDescCustCtgCodeName,lovDescCustCtgType, lovDescCustSalutationCodeName ,");
			selectSql.append(" lovDescCustParentCountryName, lovDescCustResdCountryName , lovDescCustRiskCountryName , lovDescCustRO2Name , lovDescCustBLRsnCodeName,");
			selectSql.append(" lovDescCustRejectedRsnName, lovDesccustGroupIDName , lovDescCustSubSegmentName, lovDescCustLngName , lovDescDispatchModeDescName, " );
			selectSql.append(" lovDescCcyFormatter,");
		}
			
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  Customers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID =:CustID");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);
		
		try{
			customer = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			customer = null;
		}
		logger.debug("Leaving");
		return customer;
	}
	
	/**
	 * Fetch the Record  Customers details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Customer
	 */
	@Override
	public Customer getCustomerForPostings(final long custId) {
		logger.debug("Entering");
		Customer customer = new Customer();
		customer.setId(custId);		
		
		StringBuilder selectSql = new StringBuilder("SELECT CustCIF, CustCOB, CustCtgCode, CustIndustry," );
		selectSql.append(" CustIsStaff, CustNationality, CustParentCountry, CustResdCountry," );
		selectSql.append(" CustRiskCountry, CustSector, CustSubSector, CustTypeCode" );
		
		selectSql.append(" FROM  Customers");
		selectSql.append(" Where CustID =:CustID");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);
		
		try{
			customer = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			customer = null;
		}
		logger.debug("Leaving");
		return customer;
	}
	
	/**
	 * This method initialize the Record.
	 * @param Customer (customer)
 	 * @return Customer
	 */
	@Override
	public void initialize(Customer customer) {
		super.initialize(customer);
	}
	
	/**
	 * This method refresh the Record.
	 * @param Customer (customer)
 	 * @return void
	 */
	@Override
	public void refresh(Customer customer) {
		
	}
	
	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the Customers or Customers_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Customers by key CustID
	 * 
	 * @param Customers (customer)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(Customer customer,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From Customers" );
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CustID =:CustID");
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);

		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003", customer.getCustCIF(), customer.getCustCtgCode(), customer.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006", customer.getCustCIF(), customer.getCustCtgCode(), customer.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into Customers or Customers_Temp.
	 *
	 * save Customers 
	 * 
	 * @param Customers (customer)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(Customer customer,String type) {
		logger.debug("Entering");
		
		if(customer.getCustID()==0 || customer.getCustID()==Long.MIN_VALUE){
			customer.setCustID(getNextidviewDAO().getNextId("Seq"+PennantJavaUtil.getTabelMap("Customer")));	
		}
		StringBuilder insertSql = new StringBuilder("Insert Into Customers" );
		insertSql.append(StringUtils.trimToEmpty(type) );
		insertSql.append("(CustID, CustCIF, CustCoreBank, CustCtgCode, CustTypeCode, CustSalutationCode, CustFName," );
		insertSql.append(" CustMName, CustLName, CustShrtName, CustFNameLclLng, CustMNameLclLng, CustLNameLclLng," );
		insertSql.append(" CustShrtNameLclLng, CustDftBranch, CustGenderCode, CustDOB, CustPOB, CustCOB, CustPassportNo," );
		insertSql.append(" CustMotherMaiden, CustIsMinor, CustReferedBy, CustDSA, CustDSADept, CustRO1, CustRO2, CustGroupID," );
		insertSql.append(" CustSts, CustStsChgDate, CustGroupSts, CustIsBlocked, CustIsActive, CustIsClosed, CustInactiveReason," );
		insertSql.append(" CustIsDecease, CustIsDormant, CustIsDelinquent, CustIsTradeFinCust,CustTradeLicenceNum ," );
		insertSql.append(" CustTradeLicenceExpiry,CustPassportExpiry,CustVisaNum ,CustVisaExpiry, CustIsStaff, CustStaffID," );
		insertSql.append(" CustIndustry, CustSector, CustSubSector, CustProfession, CustTotalIncome, CustMaritalSts, CustEmpSts," );
		insertSql.append(" CustSegment, CustSubSegment, CustIsBlackListed, CustBLRsnCode, CustIsRejected, CustRejectedRsn," );
		insertSql.append(" CustBaseCcy, CustLng, CustParentCountry, CustResdCountry, CustRiskCountry, CustNationality," );
		insertSql.append(" CustClosedOn, CustStmtFrq, CustIsStmtCombined, CustStmtLastDate, CustStmtNextDate, CustStmtDispatchMode," );
		insertSql.append(" CustFirstBusinessDate, CustAddlVar81, CustAddlVar82, CustAddlVar83, CustAddlVar84, CustAddlVar85," );
		insertSql.append(" CustAddlVar86, CustAddlVar87, CustAddlVar88, CustAddlVar89, CustAddlDate1, CustAddlDate2, CustAddlDate3," );
        insertSql.append(" CustAddlDate4, CustAddlDate5, CustAddlVar1, CustAddlVar2, CustAddlVar3, CustAddlVar4, CustAddlVar5," );
        insertSql.append(" CustAddlVar6, CustAddlVar7, CustAddlVar8, CustAddlVar9, CustAddlVar10, CustAddlVar11, CustAddlDec1," );
        insertSql.append(" CustAddlDec2, CustAddlDec3, CustAddlDec4, CustAddlDec5, CustAddlInt1, CustAddlInt2, CustAddlInt3, CustAddlInt4,CustAddlInt5 ," );
        insertSql.append(" DedupFound,SkipDedup,CustTotalExpense,CustBlackListDate,NoOfDependents,CustCRCPR," );
        insertSql.append(" JointCust, JointCustName, JointCustDob, custRelation, ContactPersonName, EmailID, PhoneNumber, SalariedCustomer," );
        
        insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)" );
        insertSql.append(" Values(:CustID, :CustCIF, :CustCoreBank, :CustCtgCode, :CustTypeCode, :CustSalutationCode, :CustFName, :CustMName," );
        insertSql.append(" :CustLName, :CustShrtName, :CustFNameLclLng, :CustMNameLclLng, :CustLNameLclLng, :CustShrtNameLclLng, :CustDftBranch," );
        insertSql.append(" :CustGenderCode, :CustDOB, :CustPOB, :CustCOB, :CustPassportNo, :CustMotherMaiden, :CustIsMinor, :CustReferedBy," );
        insertSql.append(" :CustDSA, :CustDSADept, :CustRO1, :CustRO2, :CustGroupID, :CustSts, :CustStsChgDate, :CustGroupSts, :CustIsBlocked," );
        insertSql.append(" :CustIsActive, :CustIsClosed, :CustInactiveReason, :CustIsDecease, :CustIsDormant, :CustIsDelinquent," );
        insertSql.append(" :CustIsTradeFinCust, :CustTradeLicenceNum ,:CustTradeLicenceExpiry, :CustPassportExpiry, :CustVisaNum , :CustVisaExpiry," );
        insertSql.append(" :CustIsStaff, :CustStaffID, :CustIndustry, :CustSector, :CustSubSector, :CustProfession, :CustTotalIncome," );
        insertSql.append(" :CustMaritalSts, :CustEmpSts, :CustSegment, :CustSubSegment, :CustIsBlackListed, :CustBLRsnCode, :CustIsRejected," );
        insertSql.append(" :CustRejectedRsn, :CustBaseCcy, :CustLng, :CustParentCountry, :CustResdCountry, :CustRiskCountry, :CustNationality," );
        insertSql.append(" :CustClosedOn, :CustStmtFrq, :CustIsStmtCombined, :CustStmtLastDate, :CustStmtNextDate, :CustStmtDispatchMode," );
        insertSql.append(" :CustFirstBusinessDate, :CustAddlVar81, :CustAddlVar82, :CustAddlVar83, :CustAddlVar84, :CustAddlVar85, :CustAddlVar86," );
        insertSql.append(" :CustAddlVar87, :CustAddlVar88, :CustAddlVar89, :CustAddlDate1, :CustAddlDate2, :CustAddlDate3, :CustAddlDate4," );
        insertSql.append(" :CustAddlDate5, :CustAddlVar1, :CustAddlVar2, :CustAddlVar3, :CustAddlVar4, :CustAddlVar5, :CustAddlVar6, :CustAddlVar7," );
        insertSql.append(" :CustAddlVar8, :CustAddlVar9, :CustAddlVar10, :CustAddlVar11, :CustAddlDec1, :CustAddlDec2, :CustAddlDec3, :CustAddlDec4," );
        insertSql.append(" :CustAddlDec5, :CustAddlInt1, :CustAddlInt2, :CustAddlInt3, :CustAddlInt4, :CustAddlInt5," );
        insertSql.append(" :DedupFound,:SkipDedup,:CustTotalExpense,:CustBlackListDate,:NoOfDependents,:CustCRCPR," );
        insertSql.append(" :JointCust, :JointCustName, :JointCustDob, :custRelation, :ContactPersonName, :EmailID, :PhoneNumber, :SalariedCustomer," );
        insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

        logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
		return customer.getId();
	}
	
	/**
	 * This method updates the Record Customers or Customers_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Customers by key CustID and Version
	 * 
	 * @param Customers (customer)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(Customer customer,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update Customers");
		updateSql.append(StringUtils.trimToEmpty(type) ); 
		updateSql.append(" Set CustID = :CustID, CustCIF = :CustCIF, CustCoreBank = :CustCoreBank, CustCtgCode = :CustCtgCode," );
		updateSql.append(" CustTypeCode = :CustTypeCode, CustSalutationCode = :CustSalutationCode, CustFName = :CustFName," );
		updateSql.append(" CustMName = :CustMName, CustLName = :CustLName, CustShrtName = :CustShrtName, CustFNameLclLng = :CustFNameLclLng," );
		updateSql.append(" CustMNameLclLng = :CustMNameLclLng, CustLNameLclLng = :CustLNameLclLng, CustShrtNameLclLng = :CustShrtNameLclLng," );
		updateSql.append(" CustDftBranch = :CustDftBranch, CustGenderCode = :CustGenderCode, CustDOB = :CustDOB, CustPOB = :CustPOB," );
		updateSql.append(" CustCOB = :CustCOB, CustPassportNo = :CustPassportNo, CustMotherMaiden = :CustMotherMaiden, CustIsMinor = :CustIsMinor," );
		updateSql.append(" CustReferedBy = :CustReferedBy, CustDSA = :CustDSA, CustDSADept = :CustDSADept, CustRO1 = :CustRO1," );
		updateSql.append(" CustRO2 = :CustRO2, custRelation = :custRelation, CustGroupID = :CustGroupID, CustSts = :CustSts, CustStsChgDate = :CustStsChgDate," );
		updateSql.append(" CustGroupSts = :CustGroupSts, CustIsBlocked = :CustIsBlocked, CustIsActive = :CustIsActive, CustIsClosed = :CustIsClosed," );
		updateSql.append(" CustInactiveReason = :CustInactiveReason, CustIsDecease = :CustIsDecease, CustIsDormant = :CustIsDormant," );
		updateSql.append(" CustIsDelinquent = :CustIsDelinquent, CustIsTradeFinCust = :CustIsTradeFinCust, CustTradeLicenceNum = :CustTradeLicenceNum," );
		updateSql.append(" CustTradeLicenceExpiry= :CustTradeLicenceExpiry,CustPassportExpiry = :CustPassportExpiry,CustVisaNum = :CustVisaNum, " );
		updateSql.append(" CustVisaExpiry = :CustVisaExpiry, CustIsStaff = :CustIsStaff, CustStaffID = :CustStaffID, CustIndustry = :CustIndustry," );
		updateSql.append(" CustSector = :CustSector, CustSubSector = :CustSubSector, CustProfession = :CustProfession," );
		updateSql.append(" CustTotalIncome = :CustTotalIncome, CustMaritalSts = :CustMaritalSts, CustEmpSts = :CustEmpSts," );
		updateSql.append(" CustSegment = :CustSegment, CustSubSegment = :CustSubSegment, CustIsBlackListed = :CustIsBlackListed," );
		updateSql.append(" CustBLRsnCode = :CustBLRsnCode, CustIsRejected = :CustIsRejected, CustRejectedRsn = :CustRejectedRsn," );
		updateSql.append(" CustBaseCcy = :CustBaseCcy, CustLng = :CustLng, CustParentCountry = :CustParentCountry, CustResdCountry = :CustResdCountry," );
		updateSql.append(" CustRiskCountry = :CustRiskCountry, CustNationality = :CustNationality, CustClosedOn = :CustClosedOn," );
		updateSql.append(" CustStmtFrq = :CustStmtFrq, CustIsStmtCombined = :CustIsStmtCombined, CustStmtLastDate = :CustStmtLastDate," );
		updateSql.append(" CustStmtNextDate = :CustStmtNextDate, CustStmtDispatchMode = :CustStmtDispatchMode," );
		updateSql.append(" CustFirstBusinessDate = :CustFirstBusinessDate, CustAddlVar81 = :CustAddlVar81, CustAddlVar82 = :CustAddlVar82," );
		updateSql.append(" CustAddlVar83 = :CustAddlVar83, CustAddlVar84 = :CustAddlVar84, CustAddlVar85 = :CustAddlVar85," );
		updateSql.append(" CustAddlVar86 = :CustAddlVar86, CustAddlVar87 = :CustAddlVar87, CustAddlVar88 = :CustAddlVar88," );
		updateSql.append(" CustAddlVar89 = :CustAddlVar89, CustAddlDate1 = :CustAddlDate1, CustAddlDate2 = :CustAddlDate2," );
		updateSql.append(" CustAddlDate3 = :CustAddlDate3, CustAddlDate4 = :CustAddlDate4, CustAddlDate5 = :CustAddlDate5," );
		updateSql.append(" CustAddlVar1 = :CustAddlVar1, CustAddlVar2 = :CustAddlVar2, CustAddlVar3 = :CustAddlVar3, CustAddlVar4 = :CustAddlVar4," );
		updateSql.append(" CustAddlVar5 = :CustAddlVar5, CustAddlVar6 = :CustAddlVar6, CustAddlVar7 = :CustAddlVar7, CustAddlVar8 = :CustAddlVar8," );
		updateSql.append(" CustAddlVar9 = :CustAddlVar9, CustAddlVar10 = :CustAddlVar10, CustAddlVar11 = :CustAddlVar11, CustAddlDec1 = :CustAddlDec1," );
		updateSql.append(" CustAddlDec2 = :CustAddlDec2, CustAddlDec3 = :CustAddlDec3, CustAddlDec4 = :CustAddlDec4, CustAddlDec5 = :CustAddlDec5," );
		updateSql.append(" CustAddlInt1 = :CustAddlInt1, CustAddlInt2 = :CustAddlInt2, CustAddlInt3 = :CustAddlInt3, CustAddlInt4 = :CustAddlInt4," );
		updateSql.append(" CustAddlInt5 = :CustAddlInt5,DedupFound=:DedupFound,SkipDedup=:SkipDedup,CustTotalExpense=:CustTotalExpense," );
		updateSql.append(" CustBlackListDate = :CustBlackListDate, NoOfDependents=:NoOfDependents,CustCRCPR=:CustCRCPR," );
		updateSql.append(" JointCust = :JointCust, JointCustName = :JointCustName, JointCustDob = :JointCustDob," );
		updateSql.append(" ContactPersonName = :ContactPersonName, EmailID = :EmailID, PhoneNumber = :PhoneNumber, SalariedCustomer = :SalariedCustomer," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode," );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where CustID =:CustID");
		
		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			
			ErrorDetails errorDetails= getError("41004", customer.getCustCIF(), customer.getCustCtgCode(), customer.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	public List<Customer> getCustomerByCif(long custId, String cifId) {
		logger.debug("Entering");
		Customer customer = new Customer();
		customer.setId(custId);		
		customer.setCustCIF(cifId);
		
		StringBuilder selectSql = new StringBuilder("SELECT CustID, CustCIF");
		selectSql.append(" FROM  dbo.Customers_View");
		if(custId==0 || custId == Long.MIN_VALUE){
			selectSql.append(" Where CustCIF=:CustCIF");
		}else{
			selectSql.append(" Where CustID <>:CustID and CustCIF=:CustCIF");	
		}
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}
	
	public Customer getCustomerByCIF(String cifId,String type) {
		logger.debug("Entering");
		Customer customer = new Customer();
		customer.setCustCIF(cifId);
		
		StringBuilder selectSql = new StringBuilder("SELECT CustID, CustFName, CustMName, CustLName, CustShrtName, custDftBranch");
		if(type.contains("View")){
			selectSql.append(" ,LovDescCustStsName");
		}
		selectSql.append(" FROM  Customers");
		selectSql.append(StringUtils.trimToEmpty(type) ); 
		selectSql.append(" Where CustCIF=:CustCIF");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);
		
		try{
			customer = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			customer = null;
		}
		logger.debug("Leaving");
		return customer;	
	}
	
	@Override
	public String getNewProspectCustomerCIF(){
		return "PC"+getNextidviewDAO().getNextId("SeqProspectCustomer");
	}
	
	@Override
    public List<FinanceProfitDetail> getCustFinAmtDetails(long custId, CustomerEligibilityCheck eligibilityCheck) {
		logger.debug("Entering");
		List<FinanceProfitDetail> financeProfitDetailsList = new ArrayList<FinanceProfitDetail>();
		
		Customer customer = new Customer();
		customer.setCustID(custId);
		
		StringBuilder selectSql = new StringBuilder(" Select T1.FinCcy, T1.TotalPriBal, T1.TotalPftBal," );
		selectSql.append(" T1.ODprofit, T1.ODPrincipal ");
		selectSql.append(" from   dbo.FinPftDetails AS T1  ");
		selectSql.append(" Where CustID=:CustID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<FinanceProfitDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceProfitDetail.class);
		
		try{
			financeProfitDetailsList = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		}catch (EmptyResultDataAccessException e) {
			financeProfitDetailsList = null;
		}
		customer = null;
		logger.debug("Leaving");
		return financeProfitDetailsList;
    }
	
	@Override
	public String getCustEmpDesg(long custID) {
		logger.debug("Entering");

		String custEmpDesg = "";
		CustomerEmploymentDetail detail = new CustomerEmploymentDetail();
		detail.setCustID(custID);

		StringBuilder selectSql = new StringBuilder(" SELECT CustEmpDesg " );
		selectSql.append(" FROM  CustomerEmpDetails ");
		selectSql.append(" WHERE CustID=:CustID AND CurrentEmployer = 1 ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);

		try{
			custEmpDesg = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		}catch (EmptyResultDataAccessException e) {
			custEmpDesg = "";
		}
		detail = null;
		logger.debug("Leaving"); 
		return custEmpDesg == null ? "" : custEmpDesg;
	}
	
	@Override
	public String getCustCurEmpAlocType(long custID) {
		logger.debug("Entering");

		String custCurEmpAloctype = "";
		CustomerEmploymentDetail detail = new CustomerEmploymentDetail();
		detail.setCustID(custID);

		StringBuilder selectSql = new StringBuilder(" SELECT EmpAlocationType " );
		selectSql.append(" FROM  CustomerEmpDetails_AView ");
		selectSql.append(" WHERE CustID=:CustID AND CurrentEmployer = 1 ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);

		try{
			custCurEmpAloctype = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		}catch (EmptyResultDataAccessException e) {
			custCurEmpAloctype = "";
		}
		detail = null;
		logger.debug("Leaving"); 
		return custCurEmpAloctype == null ? "" : custCurEmpAloctype;
	}
	
	@Override
    public BigDecimal getCustRepayOtherTotal(long custID) {
		logger.debug("Entering");

		BigDecimal custRepayOther = BigDecimal.ZERO;
		CustomerIncome detail = new CustomerIncome();
		detail.setCustID(custID);

		StringBuilder selectSql = new StringBuilder(" SELECT CustRepayOther " );
		selectSql.append(" FROM  CustOthExpense_View ");
		selectSql.append(" WHERE CustID=:CustID ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);

		try{
			custRepayOther = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, BigDecimal.class);
		}catch (EmptyResultDataAccessException e) {
			custRepayOther = BigDecimal.ZERO;
		}
		detail = null;
		logger.debug("Leaving"); 
		return custRepayOther;
    }
	
	@Override
    public BigDecimal getCustRepayBankTotal(long custID) {
		logger.debug("Entering");

		BigDecimal custRepayBank = BigDecimal.ZERO;
		CustomerIncome detail = new CustomerIncome();
		detail.setCustID(custID);

		StringBuilder selectSql = new StringBuilder(" SELECT CustRepayBank " );
		selectSql.append(" FROM  CustBankExpense_View ");
		selectSql.append(" WHERE CustID=:CustID ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);

		try{
			custRepayBank = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, BigDecimal.class);
			custRepayBank = PennantApplicationUtil.unFormateAmount(custRepayBank, 3);
		}catch (EmptyResultDataAccessException e) {
			custRepayBank = BigDecimal.ZERO;
		}
		detail = null;
		logger.debug("Leaving"); 
		return custRepayBank;
    }
	
	@Override
    public String getCustWorstSts(long custID) {
		logger.debug("Entering");

		String custWorstSts = "";
		CustomerEmploymentDetail detail = new CustomerEmploymentDetail();
		detail.setCustID(custID);

		StringBuilder selectSql = new StringBuilder(" SELECT T1.CustStsCode FROM BMTCustStatusCodes T1 ");
		selectSql.append(" INNER JOIN (SELECT MAX(DueDays) MaxDays from FinanceMain F  WITH(NOLOCK), BMTCustStatusCodes S ");
		selectSql.append(" WHERE F.FinStatus = S.CustStsCode and F.CustID = :CustID)T2 ON T1.DueDays=T2.MaxDays  ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);

		try{
			custWorstSts = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		}catch (EmptyResultDataAccessException e) {
			custWorstSts = "";
		}
		detail = null;
		logger.debug("Leaving"); 
		return custWorstSts == null ? "" : custWorstSts;
    }
	
	@Override
    public String getCustWorstStsbyCurFinSts(long custID, String finReference, String curFinSts) {
		logger.debug("Entering");

		String custWorstSts = "";
		FinanceMain main = new FinanceMain();
		main.setCustID(custID);
		main.setFinReference(finReference);
		main.setFinStatus(curFinSts);

		StringBuilder selectSql = new StringBuilder(" Select CustStsCode from BMTCustStatusCodes ");
		selectSql.append(" WHERE DueDays= (Select MAX(MaxODDays)MaxDays from (select MAX(DueDays)MaxODDays from FinanceMain F  WITH(NOLOCK), BMTCustStatusCodes S  ");
		selectSql.append(" WHERE F.FinStatus = S.CustStsCode and F.CustID = :CustID and F.FinReference <> :FinReference	UNION ");
		selectSql.append(" Select DueDays from BMTCustStatusCodes where CustStsCode=:FinStatus ) T )  ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(main);

		try{
			custWorstSts = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		}catch (EmptyResultDataAccessException e) {
			custWorstSts = "";
		}
		main = null;
		logger.debug("Leaving"); 
		return custWorstSts == null ? "" : custWorstSts;
    }
	
	@Override
	public String getCustWorstStsDesc(long custID) {
		logger.debug("Entering");
		
		String custWorstSts = "";
		CustomerEmploymentDetail detail = new CustomerEmploymentDetail();
		detail.setCustID(custID);
		
		StringBuilder selectSql = new StringBuilder(" SELECT T1.CustStsDescription FROM BMTCustStatusCodes T1 ");
		selectSql.append(" INNER JOIN (SELECT MAX(DueDays) MaxDays from FinanceMain F  WITH(NOLOCK), BMTCustStatusCodes S ");
		selectSql.append(" WHERE F.FinStatus = S.CustStsCode and F.CustID = :CustID)T2 ON T1.DueDays=T2.MaxDays  ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		
		try{
			custWorstSts = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		}catch (EmptyResultDataAccessException e) {
			custWorstSts = "";
		}
		logger.debug("Leaving"); 
		return custWorstSts == null ? "" : custWorstSts;
	}

	/**
	 * Method for Checking JOint Customer Existence in Customer Details Data
	 */
	@Override
    public boolean isJointCustExist(long custID) {
		logger.debug("Entering");

		boolean jointCustExist = false;
		Customer detail = new Customer();
		detail.setCustID(custID);

		StringBuilder selectSql = new StringBuilder(" SELECT JointCust " );
		selectSql.append(" FROM  Customers_AView ");
		selectSql.append(" WHERE CustID=:CustID ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);

		try{
			jointCustExist = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Boolean.class);
		}catch (EmptyResultDataAccessException e) {
			jointCustExist = false;
		}
		
		detail = null;
		logger.debug("Leaving"); 
		return jointCustExist;
    }	

	@Override
    public long saveWIFCustomer(WIFCustomer customer) {

		logger.debug("Entering");
		
		if(customer.getCustID() == 0 || customer.getCustID() == Long.MIN_VALUE){
			customer.setCustID(getNextidviewDAO().getNextId("SeqWIFCustomer"));	
		}
		
		StringBuilder insertSql = new StringBuilder("Insert Into WIFCustomers" );
		insertSql.append(" (CustID , CustCRCPR , CustCtgCode , CustTypeCode , CustShrtName , CustGenderCode , CustDOB , " );
		insertSql.append(" CustSector , CustSubSector , CustMaritalSts , CustEmpSts , CustIsBlackListed , CustBlackListDate , " );
		insertSql.append(" NoOfDependents , CustBaseCcy , CustNationality , JointCust, ExistCustID, ElgRequired, CustEmpAloc)" );
		insertSql.append(" VALUES (:CustID , :CustCRCPR , :CustCtgCode , :CustTypeCode , :CustShrtName , :CustGenderCode , :CustDOB , " );
		insertSql.append(" :CustSector , :CustSubSector , :CustMaritalSts , :CustEmpSts , :CustIsBlackListed , :CustBlackListDate , " );
		insertSql.append(" :NoOfDependents , :CustBaseCcy , :CustNationality ,:JointCust, :ExistCustID, :ElgRequired ,:CustEmpAloc)" );

        logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
		return customer.getCustID();
	
    }
	
	@SuppressWarnings("serial")
    @Override
    public void updateWIFCustomer(WIFCustomer customer) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder(" Update WIFCustomers");
		updateSql.append(" Set CustID=:CustID , CustCRCPR=:CustCRCPR , CustCtgCode=:CustCtgCode , CustTypeCode=:CustTypeCode , " );
		updateSql.append(" CustShrtName=:CustShrtName , CustGenderCode=:CustGenderCode , CustDOB=:CustDOB , CustSector=:CustSector , " );
		updateSql.append(" CustSubSector=:CustSubSector , CustMaritalSts=:CustMaritalSts , CustEmpSts=:CustEmpSts ,CustEmpAloc=:CustEmpAloc, " );
		updateSql.append(" CustIsBlackListed=:CustIsBlackListed , CustBlackListDate=:CustBlackListDate , NoOfDependents=:NoOfDependents , " );
		updateSql.append(" CustBaseCcy=:CustBaseCcy , CustNationality=:CustNationality , JointCust=:JointCust , ExistCustID=:ExistCustID, ElgRequired=:ElgRequired" );
		updateSql.append(" WHERE CustID=:CustID ");
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			
			ErrorDetails errorDetails= getError("41004", customer.getCustCRCPR(), customer.getCustCtgCode(), PennantConstants.default_Language);
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	
    }
	
	/**
	 * Fetch the Record  Customers details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Customer
	 */
	@Override
	public WIFCustomer getWIFCustomerByID(final long custId, String custCRCPR, String type) {
		logger.debug("Entering");
		WIFCustomer customer = new WIFCustomer();
		customer.setCustID(custId);
		customer.setCustCRCPR(custCRCPR);
		
		StringBuilder selectSql = new StringBuilder(" SELECT CustID , CustCtgCode , CustTypeCode , CustShrtName , CustGenderCode , " );
		selectSql.append(" CustDOB , CustSector , CustSubSector , CustMaritalSts , CustEmpSts , CustIsBlackListed , CustBlackListDate ," );
		selectSql.append(" NoOfDependents , CustBaseCcy , CustNationality , CustCRCPR , JointCust, ExistCustID, ElgRequired , CustEmpAloc ");
		if(type.contains("View")){
			selectSql.append(" ,lovDescCustTypeCodeName, lovDescCustMaritalStsName, lovDescCustEmpStsName, lovDescCustBaseCcyName,lovDescCustNationalityName, ");
			selectSql.append(" lovDescCustSectorName, lovDescCustSubSectorName, lovDescCustGenderCodeName, lovDescCustCtgCodeName, lovDescCustEmpAlocName, lovDescCustEmpName ");
		}
		selectSql.append(" FROM WIFCustomers" );
		selectSql.append(StringUtils.trimToEmpty(type) ); 
		
		if(custCRCPR == null){
			selectSql.append(" Where CustID =:CustID");
		}else{
			selectSql.append(" Where CustCRCPR =:CustCRCPR");
		}
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<WIFCustomer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(WIFCustomer.class);
		
		try{
			customer = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			customer = null;
		}
		logger.debug("Leaving");
		return customer;
	}
	
	/**
	 * Fetch the Record  Customers details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Customer
	 */
	@Override
	public String getCustomerByCRCPR(final String custCRCPR, String type) {
		logger.debug("Entering");
		
		String custCIF = null;
		WIFCustomer customer = new WIFCustomer();
		customer.setCustCRCPR(custCRCPR);
		
		StringBuilder selectSql = new StringBuilder(" SELECT CustCIF ");
		selectSql.append(" FROM Customers" );
		selectSql.append(StringUtils.trimToEmpty(type) ); 
		selectSql.append(" Where CustCRCPR =:CustCRCPR");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		
		try{
			custCIF = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);	
		}catch (EmptyResultDataAccessException e) {
			custCIF = null;
		}
		logger.debug("Leaving");
		return custCIF;
	}

	/**
	 * Fetch the Record  Customers details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Customer
	 */
	@Override
	public Date getCustBlackListedDate(final String custCRCPR, String type) {
		logger.debug("Entering");
		
		Date blackListedDate = null;
		Abuser abuser = new Abuser();
		abuser.setAbuserIDNumber(custCRCPR);
		
		StringBuilder selectSql = new StringBuilder(" SELECT AbuserExpDate ");
		selectSql.append(" FROM EQNAbuserList" );
		selectSql.append(StringUtils.trimToEmpty(type) ); 
		selectSql.append(" Where AbuserIDNumber =:AbuserIDNumber");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(abuser);
		
		try{
			blackListedDate = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Date.class);	
		}catch (EmptyResultDataAccessException e) {
			blackListedDate = null;
		}
		logger.debug("Leaving");
		return blackListedDate;
	}
	
	public void updateProspectCustomer(Customer customer){
		logger.debug("Entering");
		long custID = customer.getCustID();
		customer.setCustID(Long.parseLong(customer.getCustCoreBank()));
		customer.setCustCIF(customer.getCustCoreBank());
		if(isAvailableCustomer(customer.getCustID(),"")){
			//this.update(customer,"");
		}else{
			this.save(customer, "");
		}	
		customer.setCustGroupID(custID);
		if(custID != 0 ){
			StringBuilder updateSql = new StringBuilder(" Update Commitments set CustID = :CustID ");
			updateSql.append(" where CustID = :CustGroupID ");
			updateCustID(updateSql.toString(), customer);
			updateSql.delete(0, updateSql.length());

			updateSql.append(" Update CustAdditionalDetails set CustID = :CustID ");
			updateSql.append(" where CustID = :CustGroupID ");
			updateCustID(updateSql.toString(), customer);
			updateSql.delete(0, updateSql.length());
			
			updateSql.append(" Update CustIdentities set IdCustID = :CustID ");
			updateSql.append(" where IdCustID = :CustGroupID ");
			updateCustID(updateSql.toString(), customer);
			updateSql.delete(0, updateSql.length());
			
			updateSql.append(" Update CustomerAddresses set CustID = :CustID ");
			updateSql.append(" where CustID = :CustGroupID ");
			updateCustID(updateSql.toString(), customer);
			updateSql.delete(0, updateSql.length());
			
			updateSql.append(" Update CustomerDocuments set CustID = :CustID ");
			updateSql.append(" where CustID = :CustGroupID ");
			updateCustID(updateSql.toString(), customer);
			updateSql.delete(0, updateSql.length());
			
			updateSql.append(" Update CustomerPhoneNumbers set PhoneCustID = :CustID ");
			updateSql.append(" where PhoneCustID = :CustGroupID ");
			updateCustID(updateSql.toString(), customer);
			updateSql.delete(0, updateSql.length());
			
			updateSql.append(" Update CustomerDirectorDetail set CustID = :CustID ");
			updateSql.append(" where CustID = :CustGroupID ");
			updateCustID(updateSql.toString(), customer);
			updateSql.delete(0, updateSql.length());
			
			updateSql.append(" Update CustomerEmails set CustID = :CustID ");
			updateSql.append(" where CustID = :CustGroupID ");
			updateCustID(updateSql.toString(), customer);
			updateSql.delete(0, updateSql.length());

			updateSql.append(" Update CustomerEmpDetails set CustID = :CustID ");
			updateSql.append(" where CustID = :CustGroupID ");
			updateCustID(updateSql.toString(), customer);
			updateSql.delete(0, updateSql.length());
			
			updateSql.append(" Update CustomerIncomes set CustID = :CustID ");
			updateSql.append(" where CustID = :CustGroupID ");
			updateCustID(updateSql.toString(), customer);
			updateSql.delete(0, updateSql.length());

			updateSql.append(" Update CustomerRatings set CustID = :CustID ");
			updateSql.append(" where CustID = :CustGroupID ");
			updateCustID(updateSql.toString(), customer);
			updateSql.delete(0, updateSql.length());
			
			updateSql.append(" Update FinanceMain_Temp set CustID = :CustID ");
			updateSql.append(" where CustID = :CustGroupID ");
			updateCustID(updateSql.toString(), customer);
			updateSql.delete(0, updateSql.length());
			
			updateSql.append(" Update FacilityHeader_Temp set CustID = :CustID ");
			updateSql.append(" where CustID = :CustGroupID ");
			updateCustID(updateSql.toString(), customer);
			updateSql.delete(0, updateSql.length());
			
			updateSql.append(" delete from Customers where CustID = :CustGroupID ");
			updateCustID(updateSql.toString(), customer);
			updateSql.delete(0, updateSql.length());
		}
		logger.debug("Leaving");
	}

	public void updateCustID (String updateSql, Customer customer){
		logger.debug("Entering");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		this.namedParameterJdbcTemplate.update(updateSql, beanParameters);
		logger.debug("Leaving");
	}	

	@Override
    public ProspectCustomer getProspectCustomer(String finReference, String type) {
		logger.debug("Entering");
		
		ProspectCustomer prospectCustomer = new ProspectCustomer();
		prospectCustomer.setFinReference(finReference);
		
		StringBuilder selectSql = new StringBuilder(" SELECT CustID , CustCIF , CustShrtName, CustTypeCtg, CustDftBranch  " );
		selectSql.append(" FROM WIFProspectCustomer" );
		selectSql.append(StringUtils.trimToEmpty(type) ); 
		selectSql.append(" Where FinReference =:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(prospectCustomer);
		RowMapper<ProspectCustomer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ProspectCustomer.class);
		
		try{
			prospectCustomer = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			prospectCustomer = null;
		}
		logger.debug("Leaving");
		return prospectCustomer;
    }
	
	public boolean isAvailableCustomer(final long id, String type) {
		logger.debug("Entering");
		Customer customer = new Customer();
		customer.setId(id);		
		
		StringBuilder selectSql = new StringBuilder("SELECT CustID ");
 		selectSql.append(" FROM  Customers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID =:CustID");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);
		
		try{
			customer = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			customer = null;
		}
		logger.debug("Leaving");
		return customer ==null?false:true;
	}
	
	/**
	 * Method for Fetch Customer CR/CPR number For Checking Black listed data
	 */
	@Override
    public String getCustCRCPRById(long custId, String type) {
		logger.debug("Entering");
		
		String custCRCPR = "";
		Customer customer = new Customer();
		customer.setCustID(custId);	
		
		StringBuilder selectSql = new StringBuilder("SELECT CustCRCPR ");
 		selectSql.append(" FROM  Customers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID =:CustID");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		
		try{
			custCRCPR = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);	
		}catch (EmptyResultDataAccessException e) {
			custCRCPR = "";
		}
		logger.debug("Leaving");
		return custCRCPR;
    }
	@Override
	public void updateFromFacility(Customer customer,String type) {
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update Customers");
		updateSql.append(StringUtils.trimToEmpty(type) ); 
		updateSql.append(" Set CustCOB = :CustCOB, CustRiskCountry = :CustRiskCountry, CustDOB = :CustDOB, CustSector = :CustSector" );
		updateSql.append(" Where CustID =:CustID");
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		logger.debug("Leaving");
	}
	
	@Override
    public AvailPastDue getCustPastDueDetailByCustId(AvailPastDue pastDue, String limitCcy) {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder(" select T1.CustID,  " );
		selectSql.append(" SUM([dbo].[UDF_ConvertCurrency](FinCurODAmt, T2.FinCcy, '" );
		selectSql.append(limitCcy.trim());
		selectSql.append("')) AS PastDueAmount, MAX(FinCurODDays) AS DueDays ,MIN(FinODSchdDate) As PastDueFrom " );
		selectSql.append(" FROM FinODDetails T1 INNER JOIN FinanceMain T2 ON T1.FinReference = T2.FinReference " );
		selectSql.append(" where FinCurODAmt>0 AND T1.CustID =:CustID Group By T1.CustID");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(pastDue);
		RowMapper<AvailPastDue> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(AvailPastDue.class);
		
		try{
			pastDue = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			pastDue = null;
		}
		logger.debug("Leaving");
		return pastDue;
    }

	@Override
	public Customer getCustomerByID(final long id) {
		logger.debug("Entering");
		Customer customer = new Customer();
		customer.setId(id);		
		
		StringBuilder selectSql = new StringBuilder("SELECT  CustCtgCode, CustStsChgDate");
		selectSql.append(" FROM  Customers");
		selectSql.append(" Where CustID =:CustID");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);
		
		try{
			customer = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			customer = null;
		}
		logger.debug("Leaving");
		return customer;
	}
		
	/**
	 * Fetch the Customer Finance Details
	 * 
	 * @param curBD
	 * @param nextBD
	 * @return
	 */
	@Override
	public List<AvailFinance> getCustomerFinanceDetailById(long custId) {
		logger.debug("Entering");
		AvailFinance availFinance = new AvailFinance();
		availFinance.setCustId(custId);
		
		StringBuilder selectSql = new StringBuilder("SELECT FinReference , FinCcy , FinAmount, DrawnPrinciple , OutStandingBal, FinStartDate, NoInst," );
		selectSql.append(" CcySpotRate , LastRepay , MaturityDate , ProfitRate , RepayFrq , Status, CcyEditField, FinDivision , FinDivisionDesc"); 
 		selectSql.append(" from AvailFinance_View ");
 		selectSql.append(" Where CustId = :CustId");
 		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(availFinance);
		RowMapper<AvailFinance> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(AvailFinance.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
	}
	
	
	private ErrorDetails  getError(String errorId, String custCIF,String custCtgCode, String userLanguage){
		String[][] parms= new String[2][2]; 

		parms[1][0] = custCIF;
		parms[1][1] = custCtgCode;

		parms[0][0] = PennantJavaUtil.getLabel("label_CustCIF")+ ":" + parms[1][0] ;
		parms[0][1]= PennantJavaUtil.getLabel("label_CustCtgCode")+ ":" + parms[1][1];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

}