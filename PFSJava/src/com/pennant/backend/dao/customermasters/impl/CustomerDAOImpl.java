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
import com.pennant.backend.model.customermasters.Customer;
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
		customer.setCustSts(PennantConstants.NONE);
		customer.setCustGroupID(PennantConstants.ZERO);
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
		Customer customer = getCustomer(false);
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
		selectSql.append(" CustAddlInt1, CustAddlInt2, CustAddlInt3, CustAddlInt4, CustAddlInt5," );
		
		if(type.contains("View")){
			selectSql.append(" lovDescCustTypeCodeName, lovDescCustMaritalStsName, lovDescCustEmpStsName, lovDescCustBaseCcyName, lovDescCustStsName,");
			selectSql.append(" lovDescCustIndustryName, lovDescCustSectorName, lovDescCustSubSectorName, lovDescCustProfessionName, lovDescCustCOBName ,");
			selectSql.append(" lovDescCustSegmentName, lovDescCustNationalityName, lovDescCustGenderCodeName, lovDescCustDSADeptName, lovDescCustRO1Name, ");
			selectSql.append(" lovDescCustGroupStsName, lovDescCustDftBranchName, lovDescCustCtgCodeName,lovDescCustCtgType, lovDescCustSalutationCodeName ,");
			selectSql.append(" lovDescCustParentCountryName, lovDescCustResdCountryName , lovDescCustRiskCountryName , lovDescCustRO2Name , lovDescCustBLRsnCodeName,");
			selectSql.append(" lovDescCustRejectedRsnName, lovDesccustGroupIDName , lovDescCustSubSegmentName, lovDescCustLngName , lovDescDispatchModeDescName, " );
			selectSql.append(" lovDescCcyFormatter,");
		}

		if(!StringUtils.trimToEmpty(type).equalsIgnoreCase("")){
			selectSql.append("maintModule, "); 
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
		Customer customer = getCustomer(false);
		customer.setId(custId);		
		
		StringBuilder selectSql = new StringBuilder("SELECT CustCIF, CustCOB, CustCtgCode, CustIndustry," );
		selectSql.append(" CustIsStaff, CustNationality, CustParentCountry, CustResdCountry," );
		selectSql.append(" CustRiskCountry, CustSector, CustSegment, CustSubSector, CustSubSegment, CustTypeCode" );
		
		selectSql.append(" FROM  dbo.Customers");
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
        
        if(type.contains("Temp")){
			insertSql.append(" MaintModule, "); 
		}
        
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

        if(type.contains("Temp")){
			insertSql.append(" :MaintModule, "); 
		}
        
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
		updateSql.append(" CustRO2 = :CustRO2, CustGroupID = :CustGroupID, CustSts = :CustSts, CustStsChgDate = :CustStsChgDate," );
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
		updateSql.append(" CustAddlInt5 = :CustAddlInt5," );
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
		Customer customer = getCustomer(false);
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
	
	public Customer getCustomerByCIF(String cifId) {
		logger.debug("Entering");
		Customer customer = getCustomer(false);
		customer.setCustCIF(cifId);
		
		StringBuilder selectSql = new StringBuilder("SELECT CustID, CustFName, CustMName, CustLName, CustShrtName, custDftBranch");
		selectSql.append(" FROM  dbo.Customers ");
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

	private ErrorDetails  getError(String errorId, String custCIF,String custCtgCode, String userLanguage){
		String[][] parms= new String[2][2]; 

		parms[1][0] = custCIF;
		parms[1][1] = custCtgCode;

		parms[0][0] = PennantJavaUtil.getLabel("label_CustCIF")+ ":" + parms[1][0] ;
		parms[0][1]= PennantJavaUtil.getLabel("label_CustCtgCode")+ ":" + parms[1][1];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

	
	
}