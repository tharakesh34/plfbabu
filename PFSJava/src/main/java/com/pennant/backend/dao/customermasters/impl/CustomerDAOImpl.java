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
import java.math.RoundingMode;
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
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.Abuser;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceExposure;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.ProspectCustomer;
import com.pennant.backend.model.reports.AvailPastDue;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.App.Database;
import com.pennanttech.pff.core.DependencyFoundException;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>Customer model</b> class.<br>
 * 
 */
public class CustomerDAOImpl extends BasisNextidDaoImpl<Customer> implements CustomerDAO {
	private static Logger logger = Logger.getLogger(CustomerDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public CustomerDAOImpl() {
		super();
	}
	
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
	 * This method get the module from method getCustomer() and set the new
	 * record flag as true and return Customer()
	 * 
	 * @return Customer
	 */
	@Override
	public Customer getNewCustomer(boolean createNew) {
		logger.debug("Entering");

		Customer customer = getCustomer(createNew);
		customer.setNewRecord(true);
		
		PFSParameter parameter = SysParamUtil.getSystemParameterObject("CURR_SYSTEM_COUNTRY");
		customer.setCustCOB(parameter.getSysParmValue().trim());
		customer.setLovDescCustCOBName(parameter.getSysParmDescription());
		
		parameter = SysParamUtil.getSystemParameterObject("APP_DFT_CURR");
		customer.setCustBaseCcy(parameter.getSysParmValue().trim());
		
		parameter = SysParamUtil.getSystemParameterObject("APP_LNG");
		customer.setCustLng(parameter.getSysParmValue().trim());
		customer.setLovDescCustLngName(parameter.getSysParmDescription());
		
		parameter = SysParamUtil.getSystemParameterObject("APP_DFT_NATION");
		customer.setCustParentCountry(parameter.getSysParmValue().trim());
		customer.setCustRiskCountry(parameter.getSysParmValue().trim());
		customer.setCustNationality(parameter.getSysParmValue().trim());
		customer.setLovDescCustParentCountryName(parameter.getSysParmDescription());
		customer.setLovDescCustRiskCountryName(parameter.getSysParmDescription());
		customer.setLovDescCustNationalityName(parameter.getSysParmDescription());
		
		parameter = SysParamUtil.getSystemParameterObject("CURR_SYSTEM_COUNTRY");
		customer.setCustResdCountry(parameter.getSysParmValue().trim());
		customer.setLovDescCustResdCountryName(parameter.getSysParmDescription());
		
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
		selectSql.append(" JointCust, JointCustName, JointCustDob, custRelation, ContactPersonName, EmailID, PhoneNumber, SalariedCustomer, custSuspSts,custSuspDate, custSuspTrigger, " );
		
		
		if(type.contains("View")){
			selectSql.append(" lovDescCustTypeCodeName, lovDescCustMaritalStsName, lovDescCustEmpStsName,  lovDescCustStsName,");
			selectSql.append(" lovDescCustIndustryName, lovDescCustSectorName, lovDescCustSubSectorName, lovDescCustProfessionName, lovDescCustCOBName ,");
			selectSql.append(" lovDescCustSegmentName, lovDescCustNationalityName, lovDescCustGenderCodeName, lovDescCustDSADeptName, lovDescCustRO1Name, ");
			selectSql.append(" lovDescCustGroupStsName, lovDescCustDftBranchName, lovDescCustCtgCodeName,lovDescCustCtgType, lovDescCustSalutationCodeName ,");
			selectSql.append(" lovDescCustParentCountryName, lovDescCustResdCountryName , lovDescCustRiskCountryName , lovDescCustRO2Name , lovDescCustBLRsnCodeName,");
			selectSql.append(" lovDescCustRejectedRsnName, lovDesccustGroupIDName , lovDescCustSubSegmentName, lovDescCustLngName , lovDescDispatchModeDescName" );
			selectSql.append(" ,lovDescTargetName,");
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
			logger.warn("Exception: ", e);
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
			logger.warn("Exception: ", e);
			customer = null;
		}
		logger.debug("Leaving");
		return customer;
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
	@Override
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
				throw new ConcurrencyException();
			}
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);
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
			customer.setCustID(getNextidviewDAO().getNextId("SeqCustomers"));	
		}
		//FIXME : To be discussed 
		
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
	@Override
	public void update(Customer customer,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update Customers");
		updateSql.append(StringUtils.trimToEmpty(type) ); 
		updateSql.append(" Set CustCtgCode = :CustCtgCode,CustCoreBank =:CustCoreBank, CustTypeCode = :CustTypeCode, CustSalutationCode = :CustSalutationCode, CustFName = :CustFName," );
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
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public boolean isDuplicateCif(long custId, String cif) {
		logger.debug(Literal.ENTERING);

		boolean exists = false;

		// Prepare the parameter source.
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("CustID", custId);
		paramSource.addValue("CustCIF", cif);

		// Check whether the document id exists for another customer.
		String sql = QueryUtil.getCountQuery(new String[] { "Customers_Temp", "Customers" },
				"CustID != :CustID and CustCIF = :CustCIF");

		logger.trace(Literal.SQL + sql);
		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	public Customer getCustomerByCIF(String cifId,String type) {
		logger.debug("Entering");
		Customer customer = new Customer();
		customer.setCustCIF(cifId);
		
		StringBuilder selectSql = new StringBuilder("SELECT CustID, CustCIF, CustFName, CustMName, CustLName,CustDOB, CustShrtName, CustCRCPR, ");
		selectSql.append(" CustPassportNo, CustCtgCode, CustNationality, CustDftBranch, Version, CustBaseCcy, PhoneNumber, EmailId");
		if(type.contains("View")){
			selectSql.append(" ,LovDescCustStsName");
		}
		selectSql.append(" FROM  Customers");
		selectSql.append(StringUtils.trimToEmpty(type) ); 
		selectSql.append(" Where CustCIF = :CustCIF");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);
		
		try{
			customer = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			customer = null;
		}
		logger.debug("Leaving");
		return customer;	
	}
	
	public Customer checkCustomerByCIF(String cif,String type) {
		logger.debug("Entering");
		Customer customer = new Customer();
		customer.setCustCIF(cif);
		
		StringBuilder selectSql = new StringBuilder("SELECT CustID");
		selectSql.append(" FROM  Customers");
		selectSql.append(StringUtils.trimToEmpty(type) ); 
		selectSql.append(" Where CustCIF=:CustCIF");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);
		
		try{
			customer = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			if (!type.equals(TableType.TEMP_TAB.getSuffix())) {
				logger.warn("Exception: ", e);	
			}
			customer = null;
		}
		logger.debug("Leaving");
		return customer;	
	}
	
	/**
	 * fetch customer and Employee details
	 * 
	 * @return WIFCustomer
	 */
	public WIFCustomer getWIFCustomerByCIF(long cifId,String type) {
		logger.debug("Entering");
		WIFCustomer wifCustomer = new WIFCustomer();
		wifCustomer.setCustID(cifId);
		
		StringBuilder selectSql = new StringBuilder("SELECT T1.CustCRCPR, T1.CustFName, T1.CustShrtName, T1.CustTypeCode, T1.CustCtgCode, T1.CustDOB, T1.custNationality,");
		selectSql.append(" T1.custGenderCode, T1.custSalutationCode, T1.custMaritalSts, T1.CustEmpSts,T1.CustTotalIncome as TotalIncome,T1.custTotalExpense as TotalExpense,T1.CustBaseCcy,T1.CustSubSector,");
		selectSql.append(" T1.NoOfDependents,T1.SalariedCustomer,T1.LovDescCustMaritalStsName,T1.LovDescCustCtgCodeName,T1.LovDescCustTypeCodeName,T1.LovDescCustNationalityName,T1.LovDescCustEmpStsName ");
		selectSql.append(" FROM  Customers_View  T1  ");
		selectSql.append(" Where T1.CustID=:CustID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(wifCustomer);
		RowMapper<WIFCustomer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(WIFCustomer.class);
		
		try{
			wifCustomer = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			wifCustomer = null;
		}
		logger.debug("Leaving");
		return wifCustomer;	
	}
	
	@Override
	public String getNewProspectCustomerCIF(){
		return String.valueOf(getNextidviewDAO().getNextId("SeqProspectCustomer"));
	}
	
	@Override
    public List<FinanceProfitDetail> getCustFinAmtDetails(long custId, CustomerEligibilityCheck eligibilityCheck) {
		logger.debug("Entering");
		List<FinanceProfitDetail> financeProfitDetailsList = new ArrayList<FinanceProfitDetail>();
		
		Customer customer = new Customer();
		customer.setCustID(custId);
		
		StringBuilder selectSql = new StringBuilder("select FinCcy, TotalPriBal, TotalPftBal" );
		selectSql.append(", ODprofit, ODPrincipal");
		selectSql.append(" from FinPftDetails");
		selectSql.append(" where CustID = :CustID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<FinanceProfitDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceProfitDetail.class);
		
		try{
			financeProfitDetailsList = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
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
			logger.warn("Exception: ", e);
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
			logger.warn("Exception: ", e);
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
			logger.warn("Exception: ", e);
			custRepayOther = BigDecimal.ZERO;
		}
		detail = null;
		logger.debug("Leaving"); 
		return custRepayOther;
    }
	
	@Override
	public BigDecimal getCustRepayBankTotal(long custID) {
		logger.debug("Entering");
		List<CustomerIncome> CustomerIncomeDetailsList = new ArrayList<CustomerIncome>();

		BigDecimal custRepayBank = BigDecimal.ZERO;
		CustomerIncome detail = new CustomerIncome();
		detail.setCustID(custID);

		StringBuilder selectSql = new StringBuilder("Select CustId,TotalRepayAmt,MaturityDate,FinStartDate,FinCcy,");
		selectSql.append("'"+SysParamUtil.getAppCurrency()+"'"+" toCcy");
		selectSql.append(" FROM FinanceMain WHERE FinIsActive = 1 and CustID=:CustID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		RowMapper<CustomerIncome> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerIncome.class);

		try {
			CustomerIncomeDetailsList = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			CustomerIncomeDetailsList = null;
		}
		if (CustomerIncomeDetailsList != null && !CustomerIncomeDetailsList.isEmpty()) {
			for (CustomerIncome customerIncome : CustomerIncomeDetailsList) {
				int months = DateUtility.getMonthsBetween(customerIncome.getFinStartDate(),
						customerIncome.getMaturityDate());
				if (months == 0) {
					custRepayBank = custRepayBank.add(CalculationUtil.getConvertedAmount(customerIncome.getFinCcy(),
							customerIncome.getToCcy(), customerIncome.getTotalRepayAmt()));
				} else {
					custRepayBank = custRepayBank.add(CalculationUtil.getConvertedAmount(customerIncome.getFinCcy(),
							customerIncome.getToCcy(),
							customerIncome.getTotalRepayAmt().divide(new BigDecimal(months), RoundingMode.HALF_UP)));
				}

			}
			custRepayBank = PennantApplicationUtil.formateAmount(custRepayBank,
					CurrencyUtil.getFormat(SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY)));
		}

		detail = null;
		logger.debug("Leaving");

		return custRepayBank;

	}

	@Override
	public BigDecimal getCustRepayProcBank(long custID, String curFinReference) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("select CustId CustCif, TotalRepayAmt, MaturityDate, FinStartDate,");
		sql.append(" FinCcy");
		sql.append(" from FinanceMain_Temp where CustID = :CustID and RcdMaintainSts is null");
		sql.append(" and FinReference <> :FinReference");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("CustID", custID);
		paramSource.addValue("FinReference", curFinReference);

		RowMapper<FinanceExposure> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceExposure.class);

		List<FinanceExposure> financeExposures = new ArrayList<>();
		try {
			financeExposures = this.namedParameterJdbcTemplate.query(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		String toCcy = SysParamUtil.getAppCurrency();
		BigDecimal totalRepayAmt = BigDecimal.ZERO;
		BigDecimal repayAmt;
		int months;

		for (FinanceExposure finExposure : financeExposures) {
			months = DateUtility.getMonthsBetween(finExposure.getFinStartDate(), finExposure.getMaturityDate(), true);
			repayAmt = finExposure.getTotalRepayAmt();

			if (months > 0) {
				repayAmt = repayAmt.divide(new BigDecimal(months), RoundingMode.HALF_UP);
			}

			totalRepayAmt = totalRepayAmt
					.add(CalculationUtil.getConvertedAmount(finExposure.getFinCCY(), toCcy, repayAmt));
		}

		totalRepayAmt = PennantApplicationUtil.formateAmount(totalRepayAmt,
				CurrencyUtil.getFormat(SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY)));

		logger.debug(Literal.LEAVING);
		return totalRepayAmt;
	}

	@Override
	public FinanceExposure getCoAppRepayBankTotal(String custCIF) {
		logger.debug("Entering");
		
		FinanceExposure exposure = null;
		Customer detail = new Customer();
		detail.setCustCIF(custCIF);
		
		StringBuilder selectSql = new StringBuilder(" SELECT CustBaseCcy FinCCY, CustTotalIncome FinanceAmt, ");
		selectSql.append(" CustTotalExpense OverdueAmt, CustRepayBank CurrentExpoSure, CustProcBank CurrentExpoSureinBaseCCY  " );
		selectSql.append(" FROM  CoAppBankExpense_View WHERE CustCIF=:CustCIF ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		RowMapper<FinanceExposure> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceExposure.class);
		
		try{
			exposure = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			exposure = null;
		}
		detail = null;
		logger.debug("Leaving"); 
		return exposure;
	}
	
	@Override
	public String getCustWorstSts(long custID) {
		logger.debug("Entering");

		String result = "";

		// Build the query
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT T1.CustStsCode");
		sql.append(" FROM BMTCustStatusCodes T1");
		sql.append(" INNER JOIN (");
		sql.append("SELECT MAX(DueDays) MaxDays from FinanceMain F");
		if (App.DATABASE == Database.SQL_SERVER) {
			sql.append(" WITH(NOLOCK)");
		}
		sql.append(", BMTCustStatusCodes S");
		sql.append(" WHERE F.FinStatus = S.CustStsCode and F.CustID = :CustID");
		sql.append(") T2 ON T1.DueDays = T2.MaxDays");
		logger.debug("selectSql: " + sql.toString());

		// Prepare the parameter source
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustID", custID);

		try {
			result = this.namedParameterJdbcTemplate.queryForObject(sql.toString(), source,
			        String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			result = "";
		}

		logger.debug("Leaving");
		return StringUtils.trimToEmpty(result);
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
		selectSql.append(" WHERE DueDays= (Select MAX(MaxODDays)MaxDays from (select MAX(DueDays)MaxODDays from FinanceMain F ");
		if (App.DATABASE == Database.SQL_SERVER) {
			selectSql.append(" WITH(NOLOCK) ");
		}
		selectSql.append(", BMTCustStatusCodes S  ");
		selectSql.append(" WHERE F.FinStatus = S.CustStsCode and F.CustID = :CustID and F.FinReference <> :FinReference	UNION ");
		selectSql.append(" Select DueDays from BMTCustStatusCodes where CustStsCode=:FinStatus ) T )  ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(main);

		try{
			custWorstSts = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
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
		selectSql.append(" INNER JOIN (SELECT MAX(DueDays) MaxDays from FinanceMain F ");
		if (App.DATABASE == Database.SQL_SERVER) {
			selectSql.append(" WITH(NOLOCK) ");
		}
		selectSql.append(" , BMTCustStatusCodes S  WHERE F.FinStatus = S.CustStsCode and F.CustID = :CustID)T2 ON T1.DueDays=T2.MaxDays  ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		
		try{
			custWorstSts = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
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
			logger.warn("Exception: ", e);
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
		insertSql.append(" NoOfDependents , CustBaseCcy , CustNationality , JointCust, ExistCustID, ElgRequired, " );
		insertSql.append(" SalariedCustomer,EmpName,EmpDept,EmpDesg,TotalIncome,TotalExpense,CustSalutationCode,CustSegment)" );
		insertSql.append(" VALUES (:CustID , :CustCRCPR , :CustCtgCode , :CustTypeCode , :CustShrtName , :CustGenderCode , :CustDOB , " );
		insertSql.append(" :CustSector , :CustSubSector , :CustMaritalSts , :CustEmpSts , :CustIsBlackListed , :CustBlackListDate , " );
		insertSql.append(" :NoOfDependents , :CustBaseCcy , :CustNationality ,:JointCust, :ExistCustID, :ElgRequired ," );
		insertSql.append(" :SalariedCustomer,:EmpName,:EmpDept,:EmpDesg,:TotalIncome,:TotalExpense,:CustSalutationCode,:CustSegment)" );
        logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
		return customer.getCustID();
	
    }
	
	@Override
    public void updateWIFCustomer(WIFCustomer customer) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder(" Update WIFCustomers");
		updateSql.append(" Set CustCRCPR=:CustCRCPR , CustCtgCode=:CustCtgCode , CustTypeCode=:CustTypeCode , " );
		updateSql.append(" CustShrtName=:CustShrtName , CustGenderCode=:CustGenderCode , CustDOB=:CustDOB , CustSector=:CustSector , " );
		updateSql.append(" CustSubSector=:CustSubSector , CustMaritalSts=:CustMaritalSts , CustEmpSts=:CustEmpSts , " );
		updateSql.append(" CustIsBlackListed=:CustIsBlackListed , CustBlackListDate=:CustBlackListDate , NoOfDependents=:NoOfDependents , " );
		updateSql.append(" CustBaseCcy=:CustBaseCcy , CustNationality=:CustNationality , JointCust=:JointCust , ExistCustID=:ExistCustID, ElgRequired=:ElgRequired ," );
		updateSql.append(" SalariedCustomer=:SalariedCustomer,EmpName=:EmpName,EmpDept=:EmpDept,EmpDesg=:EmpDesg,TotalIncome=:TotalIncome,");
		updateSql.append("TotalExpense=:TotalExpense,CustSalutationCode=:CustSalutationCode,CustSegment=:CustSegment");
		updateSql.append(" WHERE CustID=:CustID ");
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
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
		selectSql.append(" NoOfDependents , CustBaseCcy , CustNationality , CustCRCPR , JointCust, ExistCustID, ElgRequired , ");
		selectSql.append(" SalariedCustomer,EmpName,EmpDept,EmpDesg,TotalIncome,TotalExpense,CustSalutationCode,CustSegment ");
		if(type.contains("View")){
			selectSql.append(" ,lovDescCustTypeCodeName, lovDescCustMaritalStsName, lovDescCustEmpStsName, lovDescCustNationalityName, ");
			selectSql.append(" lovDescCustSectorName, lovDescCustSubSectorName, lovDescCustGenderCodeName, lovDescCustCtgCodeName, ");
			selectSql.append(" lovDescEmpName,lovDescEmpDept,lovDescEmpDesg,lovDescCustSegmentName ");
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
			logger.warn("Exception: ", e);
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
	public String getCustomerByCRCPR(final String custCRCPR,String type) {
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
			logger.warn("Exception: ", e);
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
			logger.warn("Exception: ", e);
			blackListedDate = null;
		}
		logger.debug("Leaving");
		return blackListedDate;
	}
	
	public void updateProspectCustomer(Customer customer){
		logger.debug("Entering");
		long custID = customer.getCustID();
		
		if(custID != 0 ){
			StringBuilder updateSql = new StringBuilder(" Update Customers set CustCoreBank = :CustCoreBank ");
			updateSql.append(" where CustID = :custID ");
			updateCustID(updateSql.toString(), customer);
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
		
		StringBuilder selectSql = new StringBuilder(" SELECT CustID , CustCIF , CustShrtName, CustCtgCode, CustDftBranch  " );
		selectSql.append(" FROM WIFProspectCustomer" );
		selectSql.append(StringUtils.trimToEmpty(type) ); 
		selectSql.append(" Where FinReference =:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(prospectCustomer);
		RowMapper<ProspectCustomer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ProspectCustomer.class);
		
		try{
			prospectCustomer = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
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
			logger.warn("Exception: ", e);
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
			logger.warn("Exception: ", e);
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
		selectSql.append("')) PastDueAmount, MAX(FinCurODDays) DueDays ,MIN(FinODSchdDate) PastDueFrom " );
		selectSql.append(" FROM FinODDetails T1 INNER JOIN FinanceMain T2 ON T1.FinReference = T2.FinReference " );
		selectSql.append(" where FinCurODAmt>0 AND T1.CustID =:CustID Group By T1.CustID");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(pastDue);
		RowMapper<AvailPastDue> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(AvailPastDue.class);
		
		try{
			pastDue = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
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
		
		StringBuilder selectSql = new StringBuilder("SELECT CustCIF, CustID, CustGroupID,CustCtgCode, CustStsChgDate, CustShrtName, CustCRCPR ");
		selectSql.append(" FROM  Customers");
		selectSql.append(" Where CustID =:CustID");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);
		
		try{
			customer = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
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
	public List<FinanceEnquiry> getCustomerFinanceDetailById(long custId) {
		logger.debug("Entering");
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("CustID", custId);
		
		StringBuilder selectSql = new StringBuilder("SELECT FinReference, FinType, FinStatus, FinStartDate,FinCcy, ");
		selectSql.append(" FinAmount, DownPayment,FeeChargeAmt, InsuranceAmt, FinRepaymentAmount, NumberOfTerms "); 
		//selectSql.append(" from FinanceEnquiry_View ");
		selectSql.append(" from FinanceMain ");
 		selectSql.append(" Where CustId = :CustID");
 		
		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinanceEnquiry> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceEnquiry.class);
		
		List<FinanceEnquiry> financesList = this.namedParameterJdbcTemplate.query(selectSql.toString(), mapSqlParameterSource,typeRowMapper);
		logger.debug("Leaving");
		return financesList;
	}
	
	@Override
	public boolean financeExistForCustomer(final long id, String type) {
		logger.debug("Entering");
		int count = 0;
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("CustID", id);

		StringBuilder selectSql = new StringBuilder("SELECT  COUNT(CustID)  FROM  FinanceMain");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :CustID");

		logger.debug("selectSql: " + selectSql.toString());
		try{
			count = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), mapSqlParameterSource, Integer.class);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			count = 0;
		}
		logger.debug("Leaving");
		return count > 0 ? true : false;
	}

	@Override
    public long getCustCRCPRByCustId(String custCRCPR, String type) {
	logger.debug("Entering");
		
		Long custID = null;
		WIFCustomer customer = new WIFCustomer();
		customer.setCustCRCPR(custCRCPR);
		
		StringBuilder selectSql = new StringBuilder(" SELECT CustId ");
		selectSql.append(" FROM Customers" );
		selectSql.append(StringUtils.trimToEmpty(type) ); 
		selectSql.append(" Where CustCRCPR =:CustCRCPR");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		
		try{
			custID = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,Long.class );	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			custID = Long.valueOf(0);
		}
		logger.debug("Leaving");
		return custID;
    }

	@Override
    public WIFCustomer getWIFByCustCRCPR(String custCRCPR, String type) {
		logger.debug("Entering");

		WIFCustomer customer = new WIFCustomer();
		customer.setCustCRCPR(custCRCPR);

		StringBuilder selectSql = new StringBuilder(" SELECT * ");
		selectSql.append(" FROM WIFCustomers" );
		selectSql.append(StringUtils.trimToEmpty(type) ); 
		selectSql.append(" Where CustCRCPR =:CustCRCPR");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<WIFCustomer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(WIFCustomer.class);
		try{
			customer = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,typeRowMapper );	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			customer = null;
		}
		logger.debug("Leaving");
		return customer;
    }

	@Override
	public boolean isDuplicateCrcpr(long custId, String custCRCPR) {
		logger.debug(Literal.ENTERING);

		boolean exists = false;

		// Prepare the parameter source.
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("CustID", custId);
		paramSource.addValue("CustCRCPR", custCRCPR);

		// Check whether the document id exists for another customer.
		String sql = QueryUtil.getCountQuery(new String[] { "Customers_Temp", "Customers" },
				"CustID != :CustID and CustCRCPR = :CustCRCPR");

		logger.trace(Literal.SQL + sql);
		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	/**
	 * Method for check OldProspectCIF exists in other tables and update with newCIF
	 * 
	 * @param oldCustCIF
	 * @param newCustCIF
	 */
	@Override
	public void updateProspectCustCIF(String oldCustCIF, String newCustCIF) {
		logger.debug("Entering");

		if (!StringUtils.isBlank(oldCustCIF) && !StringUtils.isBlank(newCustCIF)) {

			MapSqlParameterSource source = new MapSqlParameterSource();
			source.addValue("OldCustCIF", oldCustCIF);
			source.addValue("NewCustCIF", newCustCIF);

			StringBuilder updateSql = new StringBuilder(" Update Customers set CustCIF =:NewCustCIF, CustCoreBank =:NewCustCIF");
			if(isExistsProspectCIF("Customers", oldCustCIF)) {
				updateSql.append(" where CustCIF =:OldCustCIF ");
				updateCustCIF(updateSql.toString(), source);
			}
			updateSql.delete(0, updateSql.length());
			
			updateSql = new StringBuilder(" Update FinBlackListDetail set CustCIF =:NewCustCIF ");
			if(isExistsProspectCIF("FinBlackListDetail", oldCustCIF)) {
				updateSql.append(" where CustCIF = :OldCustCIF ");
				updateCustCIF(updateSql.toString(), source);
			}
			updateSql.delete(0, updateSql.length());

			if(isExistsProspectCIF("CustomerDedupDetail", oldCustCIF)) {
				updateSql.append(" Update CustomerDedupDetail set CustCIF =:NewCustCIF ");
				updateSql.append(" where CustCIF = :OldCustCIF ");
				updateCustCIF(updateSql.toString(), source);
			}
			updateSql.delete(0, updateSql.length());

			if(isExistsProspectCIF("FinDedupDetail", oldCustCIF)) {
				updateSql.append(" Update FinDedupDetail set CustCIF =:NewCustCIF ");
				updateSql.append(" where CustCIF = :OldCustCIF ");
				updateCustCIF(updateSql.toString(), source);
			}
			updateSql.delete(0, updateSql.length());

			logger.debug("Leaving");
		}

	}

	/**
	 * Method for Check Is ProspectCustCIF Exists or not with old CustCIF
	 * 
	 * @param tableName
	 * @param oldCustCIF
	 * @return
	 */
	private boolean isExistsProspectCIF(String tableName, String oldCustCIF) {
		logger.debug("Entering");
		
		List<String> objList = null;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustCIF", oldCustCIF);
		
		StringBuilder selectSql = new StringBuilder("SELECT CustCIF FROM ");
		selectSql.append(tableName);
		selectSql.append(" WHERE CustCIF=:CustCIF");
		
		logger.debug("selectSql: " + selectSql.toString());
		
		logger.debug("Leaving");
		
		try {
			objList = this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), source, String.class);
			if(objList != null && !objList.isEmpty()) {
				return true;
			}
		} catch(EmptyResultDataAccessException ex) {
			logger.warn("Exception: ", ex);
			return false;
		}
		return false;
    }

	/**
	 * Method for Update Corebank CustCIF for Prospect Customer
	 * 
	 * @param updateSql
	 * @param source
	 */
	private void updateCustCIF(String updateSql, MapSqlParameterSource source) {
		logger.debug("Entering");
		this.namedParameterJdbcTemplate.update(updateSql, source);
		logger.debug("Leaving");
	}

	/**
	 * Method for fetch Customer core bank id
	 * 
	 * @param CustCIF
	 * @return String
	 */
	@Override
	public String getCustCoreBankIdByCIF(String custCIF) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustCIF", custCIF);

		StringBuilder selectSql = new StringBuilder("SELECT CustCoreBank ");
		selectSql.append(" FROM  Customers");
		selectSql.append(" Where CustCIF=:CustCIF");

		logger.debug("selectSql: " + selectSql.toString());

		logger.debug("Leaving");
		try{
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, String.class);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			return null;
		}
	}

	/**
	 * Method for generate new CustCIF for core bank 
	 */
	@Override
    public String getNewCoreCustomerCIF() {
		logger.debug("Entering");
		
		String coreCustCIF = String.valueOf(getNextidviewDAO().getNextId("SeqCorebankCustomer"));
		
		logger.debug("Leaving");
		return StringUtils.leftPad(coreCustCIF, 7, "0");
    }

	@Override
    public void updateCorebankCustCIF(String coreCustCIF) {
		logger.debug("Entering");
		
		getNextidviewDAO().setSeqNumber("SeqCorebankCustomer", (Long.parseLong(coreCustCIF)) - 1);
		
		logger.debug("Leaving");
    }

	@Override
    public void updateCustSuspenseDetails(Customer aCustomer, String tableType) {
		logger.debug("Entering");
		
		StringBuffer updateSql = new StringBuffer();
		updateSql.append("UPDATE Customers");
		updateSql.append(tableType);
		updateSql.append(" SET CustSuspSts =:CustSuspSts, CustSuspDate =:CustSuspDate, CustSuspTrigger =:CustSuspTrigger,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode," );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" WHERE CustID =:CustID");
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(aCustomer);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		logger.debug("Leaving");
    }

	@Override
    public void saveCustSuspMovements(Customer aCustomer) {
		logger.debug("Entering");
		
		StringBuffer insertSql = new StringBuffer();
		insertSql.append("INSERT INTO CustSuspMovements ");
		insertSql.append("(CustID, CustSuspEffDate, CustSuspAprDate, CustSuspMvtType, CustSuspRemarks) ");
		insertSql.append(" VALUES(:CustID, :CustSuspEffDate, :CustSuspAprDate, :CustSuspMvtType, :CustSuspRemarks) ");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(aCustomer);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
    }

	@Override
    public String getCustSuspRemarks(long custID) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustID", custID);
		
		StringBuffer selectSql = new StringBuffer();
		selectSql.append(" Select T1.CustSuspRemarks FROM CustSuspMovements T1 INNER JOIN ");
		selectSql.append(" (Select CustID,MAX(CustSuspEffDate) MaxSuspEffDate FROM CustSuspMovements Group by CustID) T2 ");
		selectSql.append(" ON T1.CustID =T2.CustID and T1.CustSuspEffDate =T2.MaxSuspEffDate where T1.CustID=:CustID");
		
		logger.debug("insertSql: " + selectSql.toString());
		
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
		} catch(EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return null;
		}
		
    }

	@Override
    public Customer getSuspendCustomer(Long custID) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustID", custID);
		
		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT CustID, CustCIF, CustShrtName, CustDftBranch, CustSts, CustStsChgDate, custSuspSts,");
		selectSql.append(" custSuspDate, custSuspTrigger From Customers ");
		selectSql.append(" Where CustID =:CustID AND custSuspTrigger = 'M'");
		
		logger.debug("insertSql: " + selectSql.toString());
		
		logger.debug("Leaving");
		try {
			RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch(EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return null;
		}
		
    }

	@Override
	public ArrayList<Customer> getCustomerByLimitRule(String queryCode,
			String sqlQuery) {
		ArrayList<Customer> custList= new ArrayList<Customer>();
	
		logger.debug("insertSql: " + queryCode);
		
		logger.debug("Leaving");
		try {
			RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);
			custList= (ArrayList<Customer>) this.namedParameterJdbcTemplate.query(queryCode, typeRowMapper);
		} catch(EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return null;
		}
		
		return custList;
	}

	/**
	 * Method for get total number of records from specified master table.<br>
	 * 
	 * @param tableName
	 * @param columnName
	 * @param value
	 * 
	 * @return int
	 */
	@Override
	public int getLookupCount(String tableName, String columnName, String value) {
		logger.debug("Entering");
		
		MapSqlParameterSource source=new MapSqlParameterSource();
		source.addValue("ColumnName", columnName);
		source.addValue("Value", value);
		
		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT COUNT(*) FROM ");
		selectSql.append(tableName);
		selectSql.append(" WHERE ");
		selectSql.append(columnName);
		selectSql.append("= :Value");
		
		logger.debug("insertSql: " + selectSql.toString());
		int recordCount = 0;
		try {
			recordCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch(EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			recordCount = 0;
		}
		logger.debug("Leaving");
		
		return recordCount;
	}
	
	/**
	 * Get customer count by cust CIF.
	 * 
	 * @param custCIF
	 * @param type
	 * @return Integer
	 */
	@Override
	public int getCustomerCountByCIF(String custCIF, String type) {
		logger.debug("Entering");
		Customer customer = new Customer();
		customer.setCustCIF(custCIF);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT COUNT(*) FROM Customers ");
		selectSql.append(" WHERE CustCIF = :CustCIF");

		logger.debug("SelectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);

		int recordCount = 0;
		try {
			recordCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception :", e);
			recordCount = 0;
		}

		logger.debug("Leaving");
		return recordCount;
	}
	
	/**
	 * Get Customer Core Bank Id
	 * 
	 * @param custCoreBank
	 */
	public boolean getCustomerByCoreBankId(String custCoreBank) {
		logger.debug("Entering");
		Customer customer = new Customer();
		customer.setCustCoreBank(custCoreBank);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT COUNT(*) FROM Customers ");
		selectSql.append(" WHERE CustCoreBank = :CustCoreBank");

		logger.debug("SelectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);

		int recordCount = 0;
		try {
			recordCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception :", e);
			recordCount = 0;
		}

		logger.debug("Leaving");
		return recordCount > 0 ? true : false;
	}
	
	@Override
    public void updateCustStatus(String custStatus,Date statusChgdate,long custId) {
		logger.debug("Entering");
		MapSqlParameterSource source=new MapSqlParameterSource();
		source.addValue("CustSts", custStatus);
		source.addValue("CustStsChgDate", statusChgdate);
		source.addValue("CustId", custId);
		StringBuilder selectSql = new StringBuilder("Update Customers  " );
		selectSql.append(" Set CustSts = :CustSts, CustStsChgDate= :CustStsChgDate WHERE CustId=:CustId ");
		logger.debug("selectSql: " + selectSql.toString());
		
		logger.debug("Leaving");
		this.namedParameterJdbcTemplate.update(selectSql.toString(), source);
    }

	@Override
	public Customer getCustomerStatus(long custId) {

		logger.debug("Entering");
		Customer customer = new Customer();
		customer.setId(custId);		
		
		StringBuilder selectSql = new StringBuilder("SELECT  CustSts");
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
	
	public Customer getCustomerEOD(final long id) {
		logger.debug("Entering");
		Customer customer = new Customer();
		customer.setId(id);		
		
		StringBuilder selectSql = new StringBuilder("SELECT CustID, CustCIF, CustCoreBank, CustCtgCode, ");
		selectSql.append(" CustTypeCode, CustDftBranch, CustPOB, CustCOB, CustGroupID,  ");
		selectSql.append(" CustSts, CustStsChgDate, CustIsStaff, CustIndustry, CustSector, CustSubSector, ");
		selectSql.append(" CustEmpSts, CustSegment, CustSubSegment, CustAppDate, " );
		selectSql.append(" CustParentCountry, CustResdCountry, CustRiskCountry, CustNationality, " );
		selectSql.append(" SalariedCustomer, custSuspSts,custSuspDate, custSuspTrigger " );
		
		selectSql.append(" FROM  Customers");
		selectSql.append(" Where CustID =:CustID");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);
		
		try{
			customer = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			customer = null;
		}
		logger.debug("Leaving");
		return customer;
	}
	
	@Override
    public void updateCustAppDate(long custId,Date custAppDate, String newCustStatus) {
		logger.debug("Entering");
		MapSqlParameterSource source=new MapSqlParameterSource();
		source.addValue("CustAppDate", custAppDate);
		source.addValue("CustId", custId);
		
		StringBuilder selectSql = new StringBuilder("Update Customers  " );
		selectSql.append(" Set CustAppDate = :CustAppDate ");

		if (newCustStatus !=null) {
			source.addValue("NewCustStatus", newCustStatus);
			selectSql.append(", CustSts = :NewCustStatus ");
		}

		selectSql.append(" WHERE CustId=:CustId ");
		logger.debug("selectSql: " + selectSql.toString());
		
		logger.debug("Leaving");
		this.namedParameterJdbcTemplate.update(selectSql.toString(), source);
    }
	
	@Override
	public Date getCustAppDate(long custId) {
		logger.debug("Entering");
		MapSqlParameterSource source=new MapSqlParameterSource();
		source.addValue("CustId", custId);
		StringBuilder selectSql = new StringBuilder("select CustAppDate from Customers" );
		selectSql.append(" WHERE CustId=:CustId ");
		logger.debug("selectSql: " + selectSql.toString());
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source,Date.class);
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
	public List<Customer> getCustomerByGroupID(final long custGroupID) {
		logger.debug("Entering");
		Customer customer = new Customer();
		customer.setCustGroupID(custGroupID);
		
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
		selectSql.append(" CustAddlInt1, CustAddlInt2, CustAddlInt3, CustAddlInt4, CustAddlInt5,DedupFound,SkipDedup,CustTotalExpense,");
		selectSql.append(" CustBlackListDate,NoOfDependents,CustCRCPR," );
		selectSql.append(" JointCust, JointCustName, JointCustDob, custRelation, ContactPersonName, EmailID, PhoneNumber,");
		selectSql.append(" SalariedCustomer, custSuspSts,custSuspDate, custSuspTrigger " );
		selectSql.append(" FROM  Customers");
		selectSql.append(" Where CustGroupID =:CustGroupID");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);
		List<Customer> list = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		logger.debug("Leaving");
		return list;
	}
	
}	