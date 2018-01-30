package com.pennant.Interface.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.Interface.service.CustomerInterfaceService;
import com.pennant.Interface.service.DailyDownloadInterfaceService;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.smtmasters.PFSParameterDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.rmtmasters.FinTypeAccounting;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.model.systemmasters.SubSector;
import com.pennant.backend.util.PennantConstants;
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
import com.pennant.coreinterface.model.customer.InterfaceCustomerDetail;
import com.pennant.coreinterface.process.DailyDownloadProcess;
import com.pennant.equation.dao.CoreInterfaceDAO;

public class DailyDownloadInterfaceServiceImpl implements DailyDownloadInterfaceService {
	private static final Logger logger = Logger.getLogger(DailyDownloadInterfaceServiceImpl.class);
	
	private DailyDownloadProcess dailyDownloadProcess;
	private CoreInterfaceDAO coreInterfaceDAO;
	private PFSParameterDAO pFSParameterDAO;
	private CustomerInterfaceService customerInterfaceService;
	
	private EquationMasterMissedDetail masterMissedDetail;
	
	private Date dateValueDate = DateUtility.getAppValueDate();
	
	public DailyDownloadInterfaceServiceImpl(){
		super();
	}
	
	/**
	 * Method for Processing Currency Details
	 */
	@Override
	public boolean processCurrencyDetails(){
		logger.debug("Entering");
		
		boolean isExecuted = false;
		
		try{
			
			//Fetch Existing Currency Details
			List<EquationCurrency> existingCurrencies = getCoreInterfaceDAO().fetchCurrecnyDetails();
			
			//Import Currency Details
			List<EquationCurrency> currienciesList = getDailyDownloadProcess().importCurrencyDetails();

			List<EquationCurrency> saveCurrienciesList = new ArrayList<EquationCurrency>();
			List<EquationCurrency> updateCurrienciesList = new ArrayList<EquationCurrency>();
			
			if (existingCurrencies != null && !existingCurrencies.isEmpty()) {
				for (EquationCurrency eqtnCurrency : currienciesList) {
					if (checkCurrecnyExist(eqtnCurrency, existingCurrencies)) {
						updateCurrienciesList.add(eqtnCurrency);
					} else {
						
						eqtnCurrency.setCcyIsActive(true);
						eqtnCurrency.setVersion(1);
						eqtnCurrency.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						eqtnCurrency.setRecordType("");
						eqtnCurrency.setLastMntBy(1000);
						eqtnCurrency.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						eqtnCurrency.setRoleCode("");
						eqtnCurrency.setNextRoleCode("");
						eqtnCurrency.setTaskId("");
						eqtnCurrency.setNextTaskId("");
						eqtnCurrency.setWorkflowId(0);
						
						saveCurrienciesList.add(eqtnCurrency);
					}
				}
			} else {
				
				for (EquationCurrency eqtnCurrency : currienciesList) {
	                
					eqtnCurrency.setCcyIsActive(true);
					eqtnCurrency.setVersion(1);
					eqtnCurrency.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					eqtnCurrency.setRecordType("");
					eqtnCurrency.setLastMntBy(1000);
					eqtnCurrency.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					eqtnCurrency.setRoleCode("");
					eqtnCurrency.setNextRoleCode("");
					eqtnCurrency.setTaskId("");
					eqtnCurrency.setNextTaskId("");
					eqtnCurrency.setWorkflowId(0);
					
                }
				saveCurrienciesList.addAll(currienciesList);
			}

			if(!updateCurrienciesList.isEmpty()){
				getCoreInterfaceDAO().updateCurrecnyDetails(updateCurrienciesList);
				isExecuted = true;
			}
			if(!saveCurrienciesList.isEmpty()){
				getCoreInterfaceDAO().saveCurrecnyDetails(saveCurrienciesList);
				isExecuted = true;
			}
		}catch(Exception e){
			logger.error("Exception: ", e);
			isExecuted = false;
			saveErrorDetail(getMasterMissedDetail("Currency","ProcessingError",getExceptionDetails(e),dateValueDate));
		}
		logger.debug("Leaving");
		return isExecuted;
	}

	/**
	 * Method for Processing Relationship officer Details
	 */
	@Override
	public boolean processRelationshipOfficerDetails(){
		logger.debug("Entering");
		
		boolean isExecuted = false;
		List<EquationRelationshipOfficer> existingRelationshipOfficers;
		List<EquationRelationshipOfficer> relationshipOfficerList;
		
		try{
			
			existingRelationshipOfficers = getCoreInterfaceDAO().fetchRelationshipOfficerDetails();
			relationshipOfficerList =  getDailyDownloadProcess().importRelationShipOfficersDetails();

			List<EquationRelationshipOfficer> saveRelationshipOfficerList = new ArrayList<EquationRelationshipOfficer>();
			List<EquationRelationshipOfficer> updateRelationshipOfficerList = new ArrayList<EquationRelationshipOfficer>();
			
			if (existingRelationshipOfficers != null && !existingRelationshipOfficers.isEmpty()) {
				for (EquationRelationshipOfficer eqtnRelationshipOfficer : relationshipOfficerList) {
					if (checkRelationshipOfficerExist(eqtnRelationshipOfficer, existingRelationshipOfficers)) {
						updateRelationshipOfficerList.add(eqtnRelationshipOfficer);
					} else {
						
						eqtnRelationshipOfficer.setROfficerIsActive(true);
						eqtnRelationshipOfficer.setVersion(1);
						eqtnRelationshipOfficer.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						eqtnRelationshipOfficer.setRecordType("");
						eqtnRelationshipOfficer.setLastMntBy(1000);
						eqtnRelationshipOfficer.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						eqtnRelationshipOfficer.setRoleCode("");
						eqtnRelationshipOfficer.setNextRoleCode("");
						eqtnRelationshipOfficer.setTaskId("");
						eqtnRelationshipOfficer.setNextTaskId("");
						eqtnRelationshipOfficer.setWorkflowId(0);
						
						saveRelationshipOfficerList.add(eqtnRelationshipOfficer);
					}
				}
			} else {
				
				for (EquationRelationshipOfficer eqtnRelationshipOfficer : relationshipOfficerList) {
					eqtnRelationshipOfficer.setROfficerIsActive(true);
					eqtnRelationshipOfficer.setVersion(1);
					eqtnRelationshipOfficer.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					eqtnRelationshipOfficer.setRecordType("");
					eqtnRelationshipOfficer.setLastMntBy(1000);
					eqtnRelationshipOfficer.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					eqtnRelationshipOfficer.setRoleCode("");
					eqtnRelationshipOfficer.setNextRoleCode("");
					eqtnRelationshipOfficer.setTaskId("");
					eqtnRelationshipOfficer.setNextTaskId("");
					eqtnRelationshipOfficer.setWorkflowId(0);
                }
				
				saveRelationshipOfficerList.addAll(relationshipOfficerList);
			}

			if(!updateRelationshipOfficerList.isEmpty()){
				getCoreInterfaceDAO().updateRelationShipOfficerDetails(updateRelationshipOfficerList);
				isExecuted = true;
			}
			if(!saveRelationshipOfficerList.isEmpty()){
				getCoreInterfaceDAO().saveRelationShipOfficerDetails(saveRelationshipOfficerList);
				isExecuted = true;
			}
		}catch(Exception e){
			logger.error("Exception: ", e);
			isExecuted = false;
			saveErrorDetail(getMasterMissedDetail("RelationshipOfficer","ProcessingError",getExceptionDetails(e),dateValueDate));
		}
		logger.debug("Leaving");
		return isExecuted;
	}

	/**
	 * Method for Processing Customer Type Details
	 */
	@Override
	public boolean processCustomerTypeDetails(){
		logger.debug("Entering");
		boolean isExecuted = false;
		List<EquationCustomerType> existingCustomerTypes;
		List<EquationCustomerType> customerTypeList;
		try{
			existingCustomerTypes = getCoreInterfaceDAO().fetchCustomerTypeDetails();
			customerTypeList =   getDailyDownloadProcess().importCustomerTypeDetails();

			List<EquationCustomerType> saveCustomerTypeList = new ArrayList<EquationCustomerType>();
			List<EquationCustomerType> updateCustomerTypeList = new ArrayList<EquationCustomerType>();
			if (existingCustomerTypes != null && !existingCustomerTypes.isEmpty()) {
				for (EquationCustomerType eqtnCustomerType : customerTypeList) {
					if (checkCustomerTypeExist(eqtnCustomerType, existingCustomerTypes)) {
						updateCustomerTypeList.add(eqtnCustomerType);
					} else {
						
						eqtnCustomerType.setCustTypeIsActive(true);
						eqtnCustomerType.setVersion(1);
						eqtnCustomerType.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						eqtnCustomerType.setRecordType("");
						eqtnCustomerType.setLastMntBy(1000);
						eqtnCustomerType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						eqtnCustomerType.setRoleCode("");
						eqtnCustomerType.setNextRoleCode("");
						eqtnCustomerType.setTaskId("");
						eqtnCustomerType.setNextTaskId("");
						eqtnCustomerType.setWorkflowId(0);
						
						saveCustomerTypeList.add(eqtnCustomerType);
					}
				}
			} else {
				
				for (EquationCustomerType customerType : customerTypeList) {
					customerType.setCustTypeIsActive(true);
					customerType.setVersion(1);
					customerType.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					customerType.setRecordType("");
					customerType.setLastMntBy(1000);
					customerType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					customerType.setRoleCode("");
					customerType.setNextRoleCode("");
					customerType.setTaskId("");
					customerType.setNextTaskId("");
					customerType.setWorkflowId(0);
                }
				saveCustomerTypeList.addAll(customerTypeList);
			}

			if(!updateCustomerTypeList.isEmpty()){
				getCoreInterfaceDAO().updateCustomerTypeDetails(updateCustomerTypeList);
				isExecuted = true;
			}
			if(!saveCustomerTypeList.isEmpty()){
				getCoreInterfaceDAO().saveCustomerTypeDetails(saveCustomerTypeList);
				isExecuted = true;
			}
		}catch(Exception e){
			logger.error("Exception: ", e);
			isExecuted = false;
			saveErrorDetail(getMasterMissedDetail("CustomerType","ProcessingError",getExceptionDetails(e),dateValueDate));
		}
		logger.debug("Leaving");
		return isExecuted;
	}

	/**
	 * Method for Processing Department Details
	 */
	@Override
	public boolean processDepartmentDetails(){
		logger.debug("Entering");
		boolean isExecuted = false;
		List<EquationDepartment> existingDepartments;
		List<EquationDepartment> departmentList;
		try{
			existingDepartments = getCoreInterfaceDAO().fetchDepartmentDetails();
			departmentList = getDailyDownloadProcess().importDepartmentDetails();

			List<EquationDepartment> saveDepartmentList = new ArrayList<EquationDepartment>();
			List<EquationDepartment> updateDepartmentList = new ArrayList<EquationDepartment>();
			
			if (existingDepartments != null && !existingDepartments.isEmpty()) {
				for (EquationDepartment eqtnDepartment : departmentList) {
					if (checkDepartmentExist(eqtnDepartment, existingDepartments)) {
						updateDepartmentList.add(eqtnDepartment);
					} else {
						
						eqtnDepartment.setDeptIsActive(true);
						eqtnDepartment.setVersion(1);
						eqtnDepartment.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						eqtnDepartment.setRecordType("");
						eqtnDepartment.setLastMntBy(1000);
						eqtnDepartment.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						eqtnDepartment.setRoleCode("");
						eqtnDepartment.setNextRoleCode("");
						eqtnDepartment.setTaskId("");
						eqtnDepartment.setNextTaskId("");
						eqtnDepartment.setWorkflowId(0);
						
						saveDepartmentList.add(eqtnDepartment);
					}
				}
			} else {
				saveDepartmentList.addAll(departmentList);
			}

			if(!updateDepartmentList.isEmpty()){
				getCoreInterfaceDAO().updateDepartmentDetails(updateDepartmentList);
				isExecuted = true;
			}
			if(!saveDepartmentList.isEmpty()){
				getCoreInterfaceDAO().saveDepartmentDetails(saveDepartmentList);
				isExecuted = true;
			}
		}catch(Exception e){
			logger.error("Exception: ", e);
			isExecuted = false;
			saveErrorDetail(getMasterMissedDetail("Department","ProcessingError",getExceptionDetails(e),dateValueDate));
		}
		logger.debug("Leaving");
		return isExecuted;
	}

	/**
	 * Method for Processing Customer Group Details
	 */
	@Override
	public boolean processCustomerGroupDetails(){
		logger.debug("Entering");
		
		boolean isExecuted = false;
		List<EquationCustomerGroup> existingCustomerGroups;
		List<EquationCustomerGroup> customerGroupList;		
		try{
			existingCustomerGroups = getCoreInterfaceDAO().fetchCustomerGroupDetails();
			customerGroupList = getDailyDownloadProcess().importCustomerGroupDetails();

			List<EquationCustomerGroup> saveCustomerGroupList = new ArrayList<EquationCustomerGroup>();
			List<EquationCustomerGroup> updateCustomerGroupList = new ArrayList<EquationCustomerGroup>();
			
			if (existingCustomerGroups != null && !existingCustomerGroups.isEmpty()) {
				for (EquationCustomerGroup eqtnCustomerGroup : customerGroupList) {
					if (checkCustomerGroupExist(eqtnCustomerGroup, existingCustomerGroups)) {
						updateCustomerGroupList.add(eqtnCustomerGroup);
					} else {
						
						eqtnCustomerGroup.setCustGrpIsActive(true);
						eqtnCustomerGroup.setVersion(1);
						eqtnCustomerGroup.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						eqtnCustomerGroup.setRecordType("");
						eqtnCustomerGroup.setLastMntBy(1000);
						eqtnCustomerGroup.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						eqtnCustomerGroup.setRoleCode("");
						eqtnCustomerGroup.setNextRoleCode("");
						eqtnCustomerGroup.setTaskId("");
						eqtnCustomerGroup.setNextTaskId("");
						eqtnCustomerGroup.setWorkflowId(0);
						
						saveCustomerGroupList.add(eqtnCustomerGroup);
					}
				}
			} else {
				
				for (EquationCustomerGroup customerGroup : customerGroupList) {
					customerGroup.setCustGrpIsActive(true);
					customerGroup.setVersion(1);
					customerGroup.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					customerGroup.setRecordType("");
					customerGroup.setLastMntBy(1000);
					customerGroup.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					customerGroup.setRoleCode("");
					customerGroup.setNextRoleCode("");
					customerGroup.setTaskId("");
					customerGroup.setNextTaskId("");
					customerGroup.setWorkflowId(0);
                }
				saveCustomerGroupList.addAll(customerGroupList);
			}

			if(!updateCustomerGroupList.isEmpty()){
				getCoreInterfaceDAO().updateCustomerGroupDetails(updateCustomerGroupList);
				isExecuted = true;
			}
			if(!saveCustomerGroupList.isEmpty()){
				getCoreInterfaceDAO().saveCustomerGroupDetails(saveCustomerGroupList);
				isExecuted = true;
			}
		}catch(Exception e){
			logger.error("Exception: ", e);
			isExecuted = false;
			saveErrorDetail(getMasterMissedDetail("CustomerGroup","ProcessingError",getExceptionDetails(e),dateValueDate));
		}
		logger.debug("Leaving");
		return isExecuted;
	}
	
	/**
	 * Method for Processing Account Type  Details
	 */
	@Override
	public boolean processAccountTypeDetails(){
		logger.debug("Entering");
		
		boolean isExecuted = false;
		List<EquationAccountType> existingAccountTypes;
		List<EquationAccountType> accountTypeList;		
		try{
			existingAccountTypes = getCoreInterfaceDAO().fetchAccountTypeDetails();
			accountTypeList = getDailyDownloadProcess().importAccountTypeDetails();
			
			List<EquationAccountType> saveAccountTypeList = new ArrayList<EquationAccountType>();
			List<EquationAccountType> updateAccountTypeList = new ArrayList<EquationAccountType>();
			
			if (existingAccountTypes != null && !existingAccountTypes.isEmpty()) {
				for (EquationAccountType accountType : accountTypeList) {
					if (checkAccountTypeExist(accountType, existingAccountTypes)) {
						updateAccountTypeList.add(accountType);
					} else {
						
						accountType.setAcTypeIsActive(true);
						accountType.setVersion(1);
						accountType.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						accountType.setRecordType("");
						accountType.setLastMntBy(1000);
						accountType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						accountType.setRoleCode("");
						accountType.setNextRoleCode("");
						accountType.setTaskId("");
						accountType.setNextTaskId("");
						accountType.setWorkflowId(0);
						
						saveAccountTypeList.add(accountType);
					}
				}
			} else {
				
				for (EquationAccountType accountType : accountTypeList) {
					accountType.setAcTypeIsActive(true);
					accountType.setVersion(1);
					accountType.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					accountType.setRecordType("");
					accountType.setLastMntBy(1000);
					accountType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					accountType.setRoleCode("");
					accountType.setNextRoleCode("");
					accountType.setTaskId("");
					accountType.setNextTaskId("");
					accountType.setWorkflowId(0);
				}
				saveAccountTypeList.addAll(accountTypeList);
			}

				if(!updateAccountTypeList.isEmpty()){
					getCoreInterfaceDAO().updateAccountTypeDetails(updateAccountTypeList);
					getCoreInterfaceDAO().updateAccountTypeNatureDetails(updateAccountTypeList);
					isExecuted = true;
				}
				if(!saveAccountTypeList.isEmpty()){
					getCoreInterfaceDAO().saveAccountTypeDetails(saveAccountTypeList);
					getCoreInterfaceDAO().saveAccountTypeNatureDetails(saveAccountTypeList);
					isExecuted = true;
				}
		}catch(Exception e){
			logger.error("Exception: ", e);
			isExecuted = false;
			saveErrorDetail(getMasterMissedDetail("AccountType","ProcessingError",getExceptionDetails(e),dateValueDate));
		}
		logger.debug("Leaving");
		return isExecuted;
	}
	/**
	 * Method for Processing Customer Rating  Details
	 */
	@Override
	public boolean processCustomerRatingDetails(){
		logger.debug("Entering");

		boolean isExecuted = false;
		List<EquationCustomerRating> existingcuCustomerRatings;
		List<EquationCustomerRating> customerRatingsList;
		List<EquationMasterMissedDetail> masterValueMissedDetails = new ArrayList<EquationMasterMissedDetail>();
		EquationMasterMissedDetail masterMissedDetail;
		try{
			existingcuCustomerRatings = getCoreInterfaceDAO().fetchCustomerRatingDetails();
			customerRatingsList = getDailyDownloadProcess().importCustomerRatingDetails();

			List<Long> customerIdList = getCoreInterfaceDAO().fetchCustomerIdDetails();

			List<EquationCustomerRating> saveCustomerRatingsList = new ArrayList<EquationCustomerRating>();
			List<EquationCustomerRating> updateCustomerRatingsList = new ArrayList<EquationCustomerRating>();

			if (existingcuCustomerRatings != null && !existingcuCustomerRatings.isEmpty()) {
				for (EquationCustomerRating customerRating : customerRatingsList) {

					if(!valueExistInMaster(customerRating.getCustID(),customerIdList)){
						masterMissedDetail = new EquationMasterMissedDetail();
						masterMissedDetail.setModule("CustomerRatings");
						masterMissedDetail.setLastMntOn(dateValueDate);
						masterMissedDetail.setFieldName("CustID");
						masterMissedDetail.setDescription("CustID : '"+customerRating.getCustID()+"',CustRatingType : '"+customerRating.getCustRatingType()+"'.CustID Does Not Exist In Customers Table");
						masterValueMissedDetails.add(masterMissedDetail);	
					}else{
						if (checkCustomerRatingExist(customerRating, existingcuCustomerRatings)) {
							updateCustomerRatingsList.add(customerRating);
						} else {

							customerRating.setVersion(1);
							customerRating.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
							customerRating.setRecordType("");
							customerRating.setLastMntBy(1000);
							customerRating.setLastMntOn(new Timestamp(System.currentTimeMillis()));
							customerRating.setRoleCode("");
							customerRating.setNextRoleCode("");
							customerRating.setTaskId("");
							customerRating.setNextTaskId("");
							customerRating.setWorkflowId(0);

							saveCustomerRatingsList.add(customerRating);
						}
					}
				}
			} else {

				for (EquationCustomerRating customerRating : customerRatingsList) {
					customerRating.setVersion(1);
					customerRating.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					customerRating.setRecordType("");
					customerRating.setLastMntBy(1000);
					customerRating.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					customerRating.setRoleCode("");
					customerRating.setNextRoleCode("");
					customerRating.setTaskId("");
					customerRating.setNextTaskId("");
					customerRating.setWorkflowId(0);
				}
				saveCustomerRatingsList.addAll(customerRatingsList);
			}

			if(!updateCustomerRatingsList.isEmpty()){
				getCoreInterfaceDAO().updateCustomerRatingDetails(updateCustomerRatingsList);
				isExecuted = true;
			}
			if(!saveCustomerRatingsList.isEmpty()){
				getCoreInterfaceDAO().saveCustomerRatingDetails(saveCustomerRatingsList);
				isExecuted = true;
			}
			if(!masterValueMissedDetails.isEmpty()){
				getCoreInterfaceDAO().saveMasterValueMissedDetails(masterValueMissedDetails);
				isExecuted = true;
			}
		}catch(Exception e){
			logger.error("Exception: ", e);
			isExecuted = false;
			saveErrorDetail(getMasterMissedDetail("CustomerRating","ProcessingError",getExceptionDetails(e),dateValueDate));
		}
		logger.debug("Leaving");
		return isExecuted;
	}

	
	/**
	 * Method for Processing Country Details
	 */
	@Override
	public boolean processCountryDetails(){
		logger.debug("Entering");
		
		boolean isExecuted = false;
		
		try{
			
			//Fetch Existing Currency Details
			List<EquationCountry> existingCountries = getCoreInterfaceDAO().fetchCountryDetails();
			
			//Import Currency Details
			List<EquationCountry> countryList = getDailyDownloadProcess().importCountryDetails();

			List<EquationCountry> saveCountryList = new ArrayList<EquationCountry>();
			List<EquationCountry> updatecountryList = new ArrayList<EquationCountry>();
			
			if (existingCountries != null && !existingCountries.isEmpty()) {
				for (EquationCountry eqtnCountry : countryList) {
					if (checkCountryExist(eqtnCountry, existingCountries)) {
						updatecountryList.add(eqtnCountry);
					} else {
						eqtnCountry.setCountryParentLimit(new BigDecimal(9999));
						eqtnCountry.setCountryResidenceLimit(new BigDecimal(9999));
						eqtnCountry.setCountryRiskLimit(new BigDecimal(9999));
						eqtnCountry.setCountryIsActive(true);
						
						eqtnCountry.setVersion(1);
						eqtnCountry.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						eqtnCountry.setRecordType("");
						eqtnCountry.setLastMntBy(1000);
						eqtnCountry.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						eqtnCountry.setRoleCode("");
						eqtnCountry.setNextRoleCode("");
						eqtnCountry.setTaskId("");
						eqtnCountry.setNextTaskId("");
						eqtnCountry.setWorkflowId(0);
						
						saveCountryList.add(eqtnCountry);
					}
				}
			} else {
				
				for (EquationCountry eqtnCountry : countryList) {
					eqtnCountry.setCountryParentLimit(new BigDecimal(9999));
					eqtnCountry.setCountryResidenceLimit(new BigDecimal(9999));
					eqtnCountry.setCountryRiskLimit(new BigDecimal(9999));
					eqtnCountry.setCountryIsActive(true);
					
					eqtnCountry.setVersion(1);
					eqtnCountry.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					eqtnCountry.setRecordType("");
					eqtnCountry.setLastMntBy(1000);
					eqtnCountry.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					eqtnCountry.setRoleCode("");
					eqtnCountry.setNextRoleCode("");
					eqtnCountry.setTaskId("");
					eqtnCountry.setNextTaskId("");
					eqtnCountry.setWorkflowId(0);
					
                }
				saveCountryList.addAll(countryList);
			}

			if(!updatecountryList.isEmpty()){
				getCoreInterfaceDAO().updateCountryDetails(updatecountryList);
				isExecuted = true;
			}
			if(!saveCountryList.isEmpty()){
				getCoreInterfaceDAO().saveCountryDetails(saveCountryList);
				isExecuted = true;
			}
		}catch(Exception e){
			logger.error("Exception: ", e);
			isExecuted = false;
			saveErrorDetail(getMasterMissedDetail("Country","ProcessingError",getExceptionDetails(e),dateValueDate));
		}
		logger.debug("Leaving");
		return isExecuted;
	}

	
	/**
	 * Method for Processing Customer Status Code Details
	 */
	@Override
	public boolean processCustStatusCodeDetails(){
		logger.debug("Entering");
		
		boolean isExecuted = false;
		
		try{
			
			//Fetch Existing Customer Status Code Details
			List<EquationCustStatusCode> existingCustStatsuCodes = getCoreInterfaceDAO().fetchCustStatusCodeDetails();
			
			//Import Customer Status Code Details
			List<EquationCustStatusCode> custStsCodeList = getDailyDownloadProcess().importCustStausCodeDetails();

			List<EquationCustStatusCode> saveCustStsList = new ArrayList<EquationCustStatusCode>();
			List<EquationCustStatusCode> updateCustStsList = new ArrayList<EquationCustStatusCode>();
			
			if (existingCustStatsuCodes != null && !existingCustStatsuCodes.isEmpty()) {
				for (EquationCustStatusCode eqtnCustSts : custStsCodeList) {
					if (checkCustStsExist(eqtnCustSts, existingCustStatsuCodes)) {
						updateCustStsList.add(eqtnCustSts);
					} else {
						eqtnCustSts.setSuspendProfit(eqtnCustSts.getDueDays() >= 90 ? true : false);
						eqtnCustSts.setCustStsIsActive(true);
						
						eqtnCustSts.setVersion(1);
						eqtnCustSts.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						eqtnCustSts.setRecordType("");
						eqtnCustSts.setLastMntBy(1000);
						eqtnCustSts.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						eqtnCustSts.setRoleCode("");
						eqtnCustSts.setNextRoleCode("");
						eqtnCustSts.setTaskId("");
						eqtnCustSts.setNextTaskId("");
						eqtnCustSts.setWorkflowId(0);
						
						saveCustStsList.add(eqtnCustSts);
					}
				}
			} else {
				
				for (EquationCustStatusCode eqtnCustSts : custStsCodeList) {
					eqtnCustSts.setSuspendProfit(eqtnCustSts.getDueDays() >= 90 ? true : false);
					eqtnCustSts.setCustStsIsActive(true);
					
					eqtnCustSts.setVersion(1);
					eqtnCustSts.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					eqtnCustSts.setRecordType("");
					eqtnCustSts.setLastMntBy(1000);
					eqtnCustSts.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					eqtnCustSts.setRoleCode("");
					eqtnCustSts.setNextRoleCode("");
					eqtnCustSts.setTaskId("");
					eqtnCustSts.setNextTaskId("");
					eqtnCustSts.setWorkflowId(0);
					
                }
				saveCustStsList.addAll(custStsCodeList);
			}

			if(!updateCustStsList.isEmpty()){
				getCoreInterfaceDAO().updateCustStatusCodeDetails(updateCustStsList);
				isExecuted = true;
			}
			if(!saveCustStsList.isEmpty()){
				getCoreInterfaceDAO().saveCustStatusCodeDetails(saveCustStsList);
				isExecuted = true;
			}
		}catch(Exception e){
			logger.error("Exception: ", e);
			isExecuted = false;
			saveErrorDetail(getMasterMissedDetail("CustStatusCode","ProcessingError",getExceptionDetails(e),dateValueDate));
		}
		logger.debug("Leaving");
		return isExecuted;
	}

	/**
	 * Method for Processing Industry Details
	 */
	@Override
	public boolean processIndustryDetails(){
		logger.debug("Entering");
		
		boolean isExecuted = false;
		
		try{
			
			//Fetch Existing Industry Details
			List<EquationIndustry> existingIndustryCodes = getCoreInterfaceDAO().fetchIndustryDetails();
			
			//Import Industry Details
			List<EquationIndustry> industryList = getDailyDownloadProcess().importIndustryDetails();

			List<EquationIndustry> saveIndustryList = new ArrayList<EquationIndustry>();
			List<EquationIndustry> updateIndustryList = new ArrayList<EquationIndustry>();
			
			if (existingIndustryCodes != null && !existingIndustryCodes.isEmpty()) {
				for (EquationIndustry eqtnIndustry : industryList) {
					if (checkIndustryExist(eqtnIndustry, existingIndustryCodes)) {
						updateIndustryList.add(eqtnIndustry);
					} else {
					    eqtnIndustry.setSubSectorCode(eqtnIndustry.getIndustryCode());
						eqtnIndustry.setIndustryLimit(new BigDecimal(-1));
						eqtnIndustry.setIndustryIsActive(true);
						
						eqtnIndustry.setVersion(1);
						eqtnIndustry.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						eqtnIndustry.setRecordType("");
						eqtnIndustry.setLastMntBy(1000);
						eqtnIndustry.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						eqtnIndustry.setRoleCode("");
						eqtnIndustry.setNextRoleCode("");
						eqtnIndustry.setTaskId("");
						eqtnIndustry.setNextTaskId("");
						eqtnIndustry.setWorkflowId(0);
						
						saveIndustryList.add(eqtnIndustry);
					}
				}
			} else {
				
				for (EquationIndustry eqtnIndustry : industryList) {
					eqtnIndustry.setSubSectorCode(eqtnIndustry.getIndustryCode());
					eqtnIndustry.setIndustryLimit(new BigDecimal(-1));
					eqtnIndustry.setIndustryIsActive(true);
					
					eqtnIndustry.setVersion(1);
					eqtnIndustry.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					eqtnIndustry.setRecordType("");
					eqtnIndustry.setLastMntBy(1000);
					eqtnIndustry.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					eqtnIndustry.setRoleCode("");
					eqtnIndustry.setNextRoleCode("");
					eqtnIndustry.setTaskId("");
					eqtnIndustry.setNextTaskId("");
					eqtnIndustry.setWorkflowId(0);
					
                }
				saveIndustryList.addAll(industryList);
			}

			if(!updateIndustryList.isEmpty()){
				getCoreInterfaceDAO().updateIndustryDetails(updateIndustryList);
				isExecuted = true;
			}
			if(!saveIndustryList.isEmpty()){
				getCoreInterfaceDAO().saveIndustryDetails(saveIndustryList);
				isExecuted = true;
			}
		}catch(Exception e){
			logger.error("Exception: ", e);
			isExecuted = false;
			saveErrorDetail(getMasterMissedDetail("Industry","ProcessingError",getExceptionDetails(e),dateValueDate));
		}
		logger.debug("Leaving");
		return isExecuted;
	}
	
	/**
	 * Method for Processing Branch  Details
	 */
	@Override
	public boolean processBranchDetails(){
		logger.debug("Entering");
		
		boolean isExecuted = false;
		
		try{
			
			//Fetch Existing Branch Details
			List<EquationBranch> existingBranchs = getCoreInterfaceDAO().fetchBranchDetails();
			
			//Import Branch Details
			List<EquationBranch> branchList = getDailyDownloadProcess().importBranchDetails();

			List<EquationBranch> saveBranchList = new ArrayList<EquationBranch>();
			List<EquationBranch> updateBranchList = new ArrayList<EquationBranch>();
			
			if (existingBranchs != null && !existingBranchs.isEmpty()) {
				for (EquationBranch eqtnBranch : branchList) {
					if (checkBranchExist(eqtnBranch, existingBranchs)) {
						updateBranchList.add(eqtnBranch);
					} else {
						eqtnBranch.setBranchCity("MIGR");
						eqtnBranch.setBranchProvince("MIGR");
						eqtnBranch.setBranchCountry("BH");
						eqtnBranch.setBranchIsActive(true);
						
						eqtnBranch.setVersion(1);
						eqtnBranch.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						eqtnBranch.setRecordType("");
						eqtnBranch.setLastMntBy(1000);
						eqtnBranch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						eqtnBranch.setRoleCode("");
						eqtnBranch.setNextRoleCode("");
						eqtnBranch.setTaskId("");
						eqtnBranch.setNextTaskId("");
						eqtnBranch.setWorkflowId(0);
						
						saveBranchList.add(eqtnBranch);
					}
				}
			} else {
				
				for (EquationBranch eqtnBranch : branchList) {
					eqtnBranch.setBranchCity("MIGR");
					eqtnBranch.setBranchProvince("MIGR");
					eqtnBranch.setBranchCountry("BH");
					eqtnBranch.setBranchIsActive(true);
					
					eqtnBranch.setVersion(1);
					eqtnBranch.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					eqtnBranch.setRecordType("");
					eqtnBranch.setLastMntBy(1000);
					eqtnBranch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					eqtnBranch.setRoleCode("");
					eqtnBranch.setNextRoleCode("");
					eqtnBranch.setTaskId("");
					eqtnBranch.setNextTaskId("");
					eqtnBranch.setWorkflowId(0);
					
                }
				saveBranchList.addAll(branchList);
			}

			if(!updateBranchList.isEmpty()){
				getCoreInterfaceDAO().updateBranchDetails(updateBranchList);
				isExecuted = true;
			}
			if(!saveBranchList.isEmpty()){
				getCoreInterfaceDAO().saveBranchDetails(saveBranchList);
				isExecuted = true;
			}
		}catch(Exception e){
			logger.error("Exception: ", e);
			isExecuted = false;
			saveErrorDetail(getMasterMissedDetail("Branch","ProcessingError",getExceptionDetails(e),dateValueDate));
		}
		logger.debug("Leaving");
		return isExecuted;
	}
	
	
	/**
	 * Method for Processing Internal Account  Details
	 */
	@Override
	public boolean processInternalAccDetails(){
		logger.debug("Entering");
		
		boolean isExecuted = false;
		
		List<EquationMasterMissedDetail> masterValueMissedDetails = new ArrayList<EquationMasterMissedDetail>();
		EquationMasterMissedDetail masterMissedDetail;
		
		try{
			//Fetch Account Type Master Details 
			List<String>	masterAccountTypesList = getCoreInterfaceDAO().fetchAccountTypes();
			
			//Fetch Existing Internal Account Details
			List<EquationInternalAccount> existingInternalAccs = getCoreInterfaceDAO().fetchInternalAccDetails();
			
			//Import Internal Account Details
			List<EquationInternalAccount> internalAccList = getDailyDownloadProcess().importInternalAccDetails();

			List<EquationInternalAccount> saveInternalAccList = new ArrayList<EquationInternalAccount>();
			List<EquationInternalAccount> updateInternalAccList = new ArrayList<EquationInternalAccount>();
			
			if (existingInternalAccs != null && !existingInternalAccs.isEmpty()) {
				for (EquationInternalAccount eqtnIntAcc : internalAccList) {
					if(valueExistInMaster(eqtnIntAcc.getsIAAcType(), masterAccountTypesList)){
						if (checkAccIntExist(eqtnIntAcc, existingInternalAccs)) {
							updateInternalAccList.add(eqtnIntAcc);
						} else {

							eqtnIntAcc.setVersion(1);
							eqtnIntAcc.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
							eqtnIntAcc.setRecordType("");
							eqtnIntAcc.setLastMntBy(1000);
							eqtnIntAcc.setLastMntOn(new Timestamp(System.currentTimeMillis()));
							eqtnIntAcc.setRoleCode("");
							eqtnIntAcc.setNextRoleCode("");
							eqtnIntAcc.setTaskId("");
							eqtnIntAcc.setNextTaskId("");
							eqtnIntAcc.setWorkflowId(0);

							saveInternalAccList.add(eqtnIntAcc);
						}
					}else{
						masterMissedDetail = new EquationMasterMissedDetail();
						masterMissedDetail.setModule("SystemInternalAccounts");
						masterMissedDetail.setLastMntOn(dateValueDate);
						masterMissedDetail.setFieldName("sIAAcType");
						masterMissedDetail.setDescription("SIACode : "+eqtnIntAcc.getsIACode()+" , '"+eqtnIntAcc.getsIAAcType()+"' Value Does Not Exist In Master RMTAccountTypes Table ");
						masterValueMissedDetails.add(masterMissedDetail);	
					}
				}
			} else {
				for (EquationInternalAccount eqtnIntAcc : internalAccList) {
					if(valueExistInMaster(eqtnIntAcc.getsIAAcType(), masterAccountTypesList)){
						eqtnIntAcc.setVersion(1);
						eqtnIntAcc.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						eqtnIntAcc.setRecordType("");
						eqtnIntAcc.setLastMntBy(1000);
						eqtnIntAcc.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						eqtnIntAcc.setRoleCode("");
						eqtnIntAcc.setNextRoleCode("");
						eqtnIntAcc.setTaskId("");
						eqtnIntAcc.setNextTaskId("");
						eqtnIntAcc.setWorkflowId(0);
						
						saveInternalAccList.add(eqtnIntAcc);
					}else{
						masterMissedDetail = new EquationMasterMissedDetail();
						masterMissedDetail.setModule("SystemInternalAccounts");
						masterMissedDetail.setLastMntOn(dateValueDate);
						masterMissedDetail.setFieldName("sIAAcType");
						masterMissedDetail.setDescription("SIACode : "+eqtnIntAcc.getsIACode()+" , '"+eqtnIntAcc.getsIAAcType()+"' Value Does Not Exist In Master RMTAccountTypes Table ");
						masterValueMissedDetails.add(masterMissedDetail);	
					}
				}
			}

			if(updateInternalAccList != null && !updateInternalAccList.isEmpty()){
				getCoreInterfaceDAO().updateInternalAccDetails(updateInternalAccList);
				isExecuted = true;
			}
			if(saveInternalAccList != null && !saveInternalAccList.isEmpty()){
				getCoreInterfaceDAO().saveInternalAccDetails(saveInternalAccList);
				isExecuted = true;
			}
			if(!masterValueMissedDetails.isEmpty()){
				getCoreInterfaceDAO().saveMasterValueMissedDetails(masterValueMissedDetails);
				isExecuted = true;
			}
		}catch(Exception e){
			logger.error("Exception: ", e);
			isExecuted = false;
			saveErrorDetail(getMasterMissedDetail("SystemInternalAccount","ProcessingError",getExceptionDetails(e),dateValueDate));
		}
		logger.debug("Leaving");
		return isExecuted;
	}
	
	
	/**
	 * Method for Processing Abuser Details
	 */
	@Override
	public boolean processAbuserDetails(){
		logger.debug("Entering");
		
		boolean isExecuted = false;
		
		try{
			//Import Abuser Details From Core System
			List<EquationAbuser> abuserList = getDailyDownloadProcess().importAbuserDetails();

			if (abuserList != null && !abuserList.isEmpty()) {
				for (EquationAbuser eqtnAbuser : abuserList) {
						eqtnAbuser.setVersion(1);
						eqtnAbuser.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						eqtnAbuser.setRecordType("");
						eqtnAbuser.setLastMntBy(1000);
						eqtnAbuser.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						eqtnAbuser.setRoleCode("");
						eqtnAbuser.setNextRoleCode("");
						eqtnAbuser.setTaskId("");
						eqtnAbuser.setNextTaskId("");
						eqtnAbuser.setWorkflowId(0);
				}
			} 
			
			if(abuserList != null && !abuserList.isEmpty()){
				
				//Deleting the existing Abusers
				getCoreInterfaceDAO().deleteAbuserDetails();
				
				//Saving the new Abusers list
				getCoreInterfaceDAO().saveAbuserDetails(abuserList);
				
				isExecuted = true;
			}
		}catch(Exception e){
			logger.error("Exception: ", e);
			isExecuted = false;
			saveErrorDetail(getMasterMissedDetail("Abusers","ProcessingError",getExceptionDetails(e),dateValueDate));
		}
		logger.debug("Leaving");
		return isExecuted;
	}
	

	/**
	 * Method for Processing Customer Details
	 */
	@Override
	public boolean processCustomerDetails(){
		logger.debug("Entering");

		boolean isExecuted = false;

		try{
			Date dailyDownloadDate = SysParamUtil.getValueAsDate("DAILY_DOWNLOADS_DATE");

			List<String> existingCustomers;
			//Below condition fetches the customers who are newly created on the current date,so to fetch 
			//all old customers for the first time we have hard coded the below date
			if(dailyDownloadDate.compareTo(DateUtility.getDBDate("2014-04-04")) > 0){
				existingCustomers = getCoreInterfaceDAO().fetchExistingCustomers(dailyDownloadDate);
			}else{
				existingCustomers = getCoreInterfaceDAO().fetchExistingOldCustomers();
			}

			//Process Customer Numbers
			if(existingCustomers != null && !existingCustomers.isEmpty()){
				getDailyDownloadProcess().processCustomerNumbers(existingCustomers);
			}

			//Import Customer Details
			List<InterfaceCustomerDetail> cutomersList = getDailyDownloadProcess().importCustomerDetails();

			if (cutomersList != null && !cutomersList.isEmpty()) {
				List<Customer> updateCustomerList = new ArrayList<Customer>();
				List<CustomerAddres> saveAddressList = new ArrayList<CustomerAddres>(); 
				List<CustomerAddres> updateAddressList = new ArrayList<CustomerAddres>(); 
				List<CustomerPhoneNumber> savePhoneNumeberList = new ArrayList<CustomerPhoneNumber>();
				List<CustomerPhoneNumber> updatePhoneNumeberList = new ArrayList<CustomerPhoneNumber>();
				List<CustomerEMail> saveEmailList = new ArrayList<CustomerEMail>();
				List<CustomerEMail> updateEmailList = new ArrayList<CustomerEMail>();
				List<EquationMasterMissedDetail> masterValueMissedDetails = new ArrayList<EquationMasterMissedDetail>();

				List<CustomerAddres> existingCustomerAddress = getCoreInterfaceDAO().fetchExisitingCustomerAddress();
				List<CustomerPhoneNumber> existingCustPhoneNumbers = getCoreInterfaceDAO().fetchExisitingCustPhoneNumbers();
				List<CustomerEMail> existingCustEmails = getCoreInterfaceDAO().fetchExisitingCustEmails();
				List<CustomerDetails> customerDetails = new ArrayList<CustomerDetails>();

				for (InterfaceCustomerDetail interfaceCustomerDetail : cutomersList) {
					CustomerDetails customerDetail	= getCustomerInterfaceService().processCustInformation(interfaceCustomerDetail);
					if(customerDetail != null){
						customerDetails.add(customerDetail);
					}
				}

				List<CustomerDetails> customerDetailsList = getCustomerInterfaceService().validateMasterFieldDetails(customerDetails, dateValueDate);

				for (CustomerDetails cDetails : customerDetailsList) {
					if(cDetails != null){

						//*************** Customer ****************
						updateCustomerList.add(cDetails.getCustomer());

						//*************** Address Details ****************
						if(cDetails.getAddressList() != null && !cDetails.getAddressList().isEmpty()){
							for (CustomerAddres customerAddres : cDetails.getAddressList()) {
								if(customerAddressAlreadyExist(customerAddres, existingCustomerAddress)){
									updateAddressList.add(customerAddres);
								}else{
									customerAddres.setVersion(1);
									customerAddres.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
									customerAddres.setRecordType("");
									customerAddres.setLastMntBy(1000);
									customerAddres.setLastMntOn(new Timestamp(System.currentTimeMillis()));
									customerAddres.setRoleCode("");
									customerAddres.setNextRoleCode("");
									customerAddres.setTaskId("");
									customerAddres.setNextTaskId("");
									customerAddres.setWorkflowId(0);
									saveAddressList.add(customerAddres);
								}
							}
						}

						//*************** Phone Number Details ****************
						if(cDetails.getCustomerPhoneNumList() != null && !cDetails.getCustomerPhoneNumList().isEmpty()){
							for (CustomerPhoneNumber customerPhoneNumber : cDetails.getCustomerPhoneNumList()) {
								if(customerPhoneNumAlreadyExist(customerPhoneNumber, existingCustPhoneNumbers)){
									updatePhoneNumeberList.add(customerPhoneNumber);
								}else{
									customerPhoneNumber.setVersion(1);
									customerPhoneNumber.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
									customerPhoneNumber.setRecordType("");
									customerPhoneNumber.setLastMntBy(1000);
									customerPhoneNumber.setLastMntOn(new Timestamp(System.currentTimeMillis()));
									customerPhoneNumber.setRoleCode("");
									customerPhoneNumber.setNextRoleCode("");
									customerPhoneNumber.setTaskId("");
									customerPhoneNumber.setNextTaskId("");
									customerPhoneNumber.setWorkflowId(0);
									savePhoneNumeberList.add(customerPhoneNumber);
								}
							}
						}

						//*************** Email Details ****************
						if (cDetails.getCustomerEMailList() != null && !cDetails.getCustomerEMailList().isEmpty()) {
							for (CustomerEMail customerEMail : cDetails.getCustomerEMailList()) {
								if (StringUtils.isNotBlank(customerEMail.getCustEMailTypeCode())) {
									if (customerEmailAlreadyExist(customerEMail, existingCustEmails)) {
										updateEmailList.add(customerEMail);
									} else {
										customerEMail.setVersion(1);
										customerEMail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
										customerEMail.setRecordType("");
										customerEMail.setLastMntBy(1000);
										customerEMail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
										customerEMail.setRoleCode("");
										customerEMail.setNextRoleCode("");
										customerEMail.setTaskId("");
										customerEMail.setNextTaskId("");
										customerEMail.setWorkflowId(0);
										saveEmailList.add(customerEMail);
									}
								}
							}
						}
					}
				}

				masterValueMissedDetails.addAll(getCustomerInterfaceService().getMasterMissedDetails());

				if(updateCustomerList != null && !updateCustomerList.isEmpty()){
					try{
						isExecuted = false;
						getCoreInterfaceDAO().updateCustomerDetails(updateCustomerList);
						isExecuted = true;
					}catch(Exception e){
						logger.warn("Exception: ", e);
						masterValueMissedDetails.add(getMasterMissedDetail("Customers", "UpdatingError", getExceptionDetails(e), dateValueDate));	
					}
				}
				if(saveAddressList != null && !saveAddressList.isEmpty()){
					try{
						isExecuted = false;
						getCoreInterfaceDAO().saveCustomerAddresses(saveAddressList);
						isExecuted = true;
					}catch(Exception e){
						logger.warn("Exception: ", e);
						masterValueMissedDetails.add(getMasterMissedDetail("CustomerAddresses", "SavingError", getExceptionDetails(e), dateValueDate));	
					}
				}
				if(updateAddressList != null && !updateAddressList.isEmpty()){
					try{
						isExecuted = false;
						getCoreInterfaceDAO().updateAddressDetails(updateAddressList);
						isExecuted = true;
					}catch(Exception e){
						logger.warn("Exception: ", e);
						masterValueMissedDetails.add(getMasterMissedDetail("CustomerAddresses", "UpdatingError", getExceptionDetails(e), dateValueDate));	
					}
				}
				if(savePhoneNumeberList != null && !savePhoneNumeberList.isEmpty()){
					try{
						isExecuted = false;
						getCoreInterfaceDAO().saveCustomerPhoneNumbers(savePhoneNumeberList);
						isExecuted = true;
					}catch(Exception e){
						logger.warn("Exception: ", e);
						masterValueMissedDetails.add(getMasterMissedDetail("CustomerPhoneNumbers", "SavingError", getExceptionDetails(e), dateValueDate));	
					}
				}
				if(updatePhoneNumeberList != null && !updatePhoneNumeberList.isEmpty()){
					try{
						isExecuted = false;
						getCoreInterfaceDAO().updatePhoneNumberDetails(updatePhoneNumeberList);
						isExecuted = true;
					}catch(Exception e){
						logger.warn("Exception: ", e);
						masterValueMissedDetails.add(getMasterMissedDetail("CustomerPhoneNumbers", "UpdatingError", getExceptionDetails(e), dateValueDate));	
					}
				}
				if(saveEmailList != null && !saveEmailList.isEmpty()){
					try{
						isExecuted = false;
						getCoreInterfaceDAO().saveCustomerEmails(saveEmailList);
						isExecuted = true;
					}catch(Exception e){
						logger.warn("Exception: ", e);
						masterValueMissedDetails.add(getMasterMissedDetail("CustomerEmails", "SavingError", getExceptionDetails(e), dateValueDate));	
					}
				}
				if(updateEmailList != null && !updateEmailList.isEmpty()){
					try {
						isExecuted = false;
						getCoreInterfaceDAO().updateEMailDetails(updateEmailList);
						isExecuted = true;
					} catch (Exception e) {
						logger.warn("Exception: ", e);
						masterValueMissedDetails.add(getMasterMissedDetail("CustomerEmails", "UpdatingError", getExceptionDetails(e), dateValueDate));	
					}
				}
				if (!masterValueMissedDetails.isEmpty()) {
					getCoreInterfaceDAO().saveMasterValueMissedDetails(masterValueMissedDetails);
				}
				for (EquationMasterMissedDetail eMasterMissedDetail : masterValueMissedDetails) {
					if("SavingError".equalsIgnoreCase(eMasterMissedDetail.getFieldName())  || 
							"UpdatingError".equalsIgnoreCase(eMasterMissedDetail.getFieldName())){
						isExecuted = false;
						break;
					}
				}
			}

			updateDailyDownloadDate(dateValueDate);

		}catch(Exception e){
			logger.error("Exception: ", e);
			isExecuted = false;
			saveErrorDetail(getMasterMissedDetail("Customers","ProcessingError",getExceptionDetails(e),dateValueDate));
		}
		logger.debug("Leaving");
		return isExecuted;
	}
	
	
	
	/**
	 * Method for Processing Transaction Code Details
	 */
	@Override
	public boolean processTransactionCodeDetails(){
		logger.debug("Entering");
		
		boolean isExecuted = false;
		List<EquationTransactionCode> existingcuTransactionCodes;
		List<EquationTransactionCode> transactionCodesList;		
		try{
			existingcuTransactionCodes = getCoreInterfaceDAO().fetchTransactionCodeDetails();
			transactionCodesList = getDailyDownloadProcess().importTransactionCodeDetails();
			
			List<EquationTransactionCode> saveTransactionCodesList = new ArrayList<EquationTransactionCode>();
			List<EquationTransactionCode> updateTransactionCodesList = new ArrayList<EquationTransactionCode>();
			
			if (existingcuTransactionCodes != null && !existingcuTransactionCodes.isEmpty()) {
				for (EquationTransactionCode transactionCode : transactionCodesList) {
					if (checkTransactionCodeExist(transactionCode, existingcuTransactionCodes)) {
						updateTransactionCodesList.add(transactionCode);
					} else {
						transactionCode.setTranIsActive(true);
						transactionCode.setVersion(1);
						transactionCode.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						transactionCode.setRecordType("");
						transactionCode.setLastMntBy(1000);
						transactionCode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						transactionCode.setRoleCode("");
						transactionCode.setNextRoleCode("");
						transactionCode.setTaskId("");
						transactionCode.setNextTaskId("");
						transactionCode.setWorkflowId(0);
						
						saveTransactionCodesList.add(transactionCode);
					}
				}
			} else {
				
				for (EquationTransactionCode transactionCode : transactionCodesList) {
					transactionCode.setTranIsActive(true);
					transactionCode.setVersion(1);
					transactionCode.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					transactionCode.setRecordType("");
					transactionCode.setLastMntBy(1000);
					transactionCode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					transactionCode.setRoleCode("");
					transactionCode.setNextRoleCode("");
					transactionCode.setTaskId("");
					transactionCode.setNextTaskId("");
					transactionCode.setWorkflowId(0);
				}
				saveTransactionCodesList.addAll(transactionCodesList);
			}
			
			if(!updateTransactionCodesList.isEmpty()){
				getCoreInterfaceDAO().updateTransactionCodes(updateTransactionCodesList);
				isExecuted = true;
			}
			if(!saveTransactionCodesList.isEmpty()){
				getCoreInterfaceDAO().saveTransactionCodeDetails(saveTransactionCodesList);
				isExecuted = true;
			}
		}catch(Exception e){
			logger.error("Exception: ", e);
			isExecuted = false;
			saveErrorDetail(getMasterMissedDetail("TransactionCode","ProcessingError",getExceptionDetails(e),dateValueDate));
		}
		logger.debug("Leaving");
		return isExecuted;
	}
	
	/**
	 * Method for Processing Transaction Code Details
	 */
	@Override
	public boolean processIdentityTypeDetails(){
		logger.debug("Entering");
		
		boolean isExecuted = false;
		List<EquationIdentityType> existingIdentityTypes;
		List<EquationIdentityType> identityTypesList;		
		try{
			existingIdentityTypes = getCoreInterfaceDAO().fetchIdentityTypeDetails();
			identityTypesList = getDailyDownloadProcess().importIdentityTypeDetails();
			
			List<EquationIdentityType> saveIdentityTypesList = new ArrayList<EquationIdentityType>();
			List<EquationIdentityType> updateIdentityTypesList = new ArrayList<EquationIdentityType>();
			
			if (existingIdentityTypes != null && !existingIdentityTypes.isEmpty()) {
				for (EquationIdentityType identityType : identityTypesList) {
					if (checkIdentityTypeExist(identityType, existingIdentityTypes)) {
						updateIdentityTypesList.add(identityType);
					} else {
						identityType.setVersion(1);
						identityType.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						identityType.setRecordType("");
						identityType.setLastMntBy(1000);
						identityType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						identityType.setRoleCode("");
						identityType.setNextRoleCode("");
						identityType.setTaskId("");
						identityType.setNextTaskId("");
						identityType.setWorkflowId(0);
						
						saveIdentityTypesList.add(identityType);
					}
				}
			} else {
				for (EquationIdentityType identityType : identityTypesList) {
					identityType.setVersion(1);
					identityType.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					identityType.setRecordType("");
					identityType.setLastMntBy(1000);
					identityType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					identityType.setRoleCode("");
					identityType.setNextRoleCode("");
					identityType.setTaskId("");
					identityType.setNextTaskId("");
					identityType.setWorkflowId(0);
				}
				saveIdentityTypesList.addAll(identityTypesList);
			}
			
			if(!updateIdentityTypesList.isEmpty()){
				getCoreInterfaceDAO().updateIdentityTypes(updateIdentityTypesList);
				isExecuted = true;
			}
			if(!saveIdentityTypesList.isEmpty()){
				getCoreInterfaceDAO().saveIdentityTypeDetails(saveIdentityTypesList);
				isExecuted = true;
			}
		}catch(Exception e){
			logger.error("Exception: ", e);
			isExecuted = false;
			saveErrorDetail(getMasterMissedDetail("IdentityType","ProcessingError",getExceptionDetails(e),dateValueDate));
		}
		logger.debug("Leaving");
		return isExecuted;
	}

	
	private void updateDailyDownloadDate(Date valuedate){
		logger.debug("Entering");
		PFSParameter pFSParameter = new PFSParameter();
		pFSParameter.setSysParmValue(DateUtility.addDays(valuedate,1).toString());
		pFSParameter.setSysParmCode("DAILY_DOWNLOADS_DATE");
		getpFSParameterDAO().updateParmValue(pFSParameter);
		SysParamUtil.updateParamDetails("DAILY_DOWNLOADS_DATE",pFSParameter.getSysParmValue());
		logger.debug("Leaving");
	}
	
	private void saveErrorDetail(EquationMasterMissedDetail equationMasterMissedDetail){
		logger.debug("Entering");
		getCoreInterfaceDAO().saveMasterValueMissedDetail(equationMasterMissedDetail);
		logger.debug("Leaving");
	}
	
	private EquationMasterMissedDetail getMasterMissedDetail(String module, String fieldDetail, String description,
			Date valuedate) {
		logger.debug("Entering");
		masterMissedDetail = new EquationMasterMissedDetail();
		masterMissedDetail.setModule(module);
		masterMissedDetail.setFieldName(fieldDetail);
		masterMissedDetail.setDescription(description);
		masterMissedDetail.setLastMntOn(valuedate);
		logger.debug("Leaving");
		return masterMissedDetail;
	}
	
	private String getExceptionDetails(Exception e){
		logger.debug("Entering");
		String errMsg = "";
		if (e.getCause() == null) {
			errMsg = "NullPointerException";
		} else {
			if (e.getCause().getMessage() != null) {
				errMsg = e.getCause().getMessage().length() > 198 ? e.getCause().getMessage().substring(0,197) :
					e.getCause().getMessage();
			} else if (e.getLocalizedMessage() != null) {
				errMsg =  e.getLocalizedMessage().length() > 198 ? e.getLocalizedMessage().substring(0,197) :
					e.getLocalizedMessage();
			} else if (e.getMessage() != null) {
				errMsg =  e.getMessage().length() > 198 ? e.getMessage().substring(0,197) :
					e.getMessage();
			}
		}
		logger.debug("Leaving");
		return errMsg;
	}
	
	

	private boolean valueExistInMaster(String field,List<String> list){
		for (String value : list) {
	        if(StringUtils.trimToEmpty(field).equalsIgnoreCase(value)){
	        	return true;
	        }
        }
		return false;
	}
	
	private boolean valueExistInMaster(long field,List<Long> list){
		for (Long value : list) {
	        if(field == value){
	        	return true;
	        }
        }
		return false;
	}
	

	private boolean checkAccIntExist(EquationInternalAccount eqtnIntAcc,List<EquationInternalAccount> existingIntAccs){
		for (EquationInternalAccount intAcc : existingIntAccs) {
			if (StringUtils.trimToEmpty(eqtnIntAcc.getsIACode()).equals(intAcc.getsIACode())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean checkBranchExist(EquationBranch eqtnBranch,List<EquationBranch> existingBranchs){
		for (EquationBranch branch : existingBranchs) {
			if (StringUtils.trimToEmpty(eqtnBranch.getBranchCode()).equals(branch.getBranchCode())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean checkIndustryExist(EquationIndustry eqtnIndustry,List<EquationIndustry> existingIndustries){
		for (EquationIndustry industry : existingIndustries) {
			if (StringUtils.trimToEmpty(eqtnIndustry.getIndustryCode()).equals(industry.getIndustryCode())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean checkCustStsExist(EquationCustStatusCode eqtncuCode,List<EquationCustStatusCode> existingCustStatusCodes){
		for (EquationCustStatusCode statusCode : existingCustStatusCodes) {
			if (StringUtils.trimToEmpty(eqtncuCode.getCustStsCode()).equals(statusCode.getCustStsCode())) {
				return true;
			}
		}
		return false;
	}
	
	
	private boolean checkCountryExist(EquationCountry eqtnCountry,List<EquationCountry> existingCurrencies){
		for (EquationCountry country : existingCurrencies) {
			if (StringUtils.trimToEmpty(eqtnCountry.getCountryCode()).equals(country.getCountryCode())) {
				return true;
			}
		}
		return false;
	}
	
	
	private boolean checkCurrecnyExist(EquationCurrency eqtnCurrency,List<EquationCurrency> existingCurrencies){
		for (EquationCurrency currency : existingCurrencies) {
			if (StringUtils.trimToEmpty(eqtnCurrency.getCcyCode()).equals(currency.getCcyCode())) {
				return true;
			}
		}
		return false;
	}

	private boolean checkRelationshipOfficerExist(EquationRelationshipOfficer eqtnRelationshipOfficer,List<EquationRelationshipOfficer> existingRelationshipOfficers){
		for (EquationRelationshipOfficer relationshipOfficer : existingRelationshipOfficers) {
			if (StringUtils.trimToEmpty(eqtnRelationshipOfficer.getROfficerCode()).equals(relationshipOfficer.getROfficerCode())) {
				return true;
			}
		}
		return false;
	}

	private boolean checkCustomerTypeExist(EquationCustomerType eqtnCustomerType,List<EquationCustomerType> existingCustomerTypes){
		for (EquationCustomerType customerType : existingCustomerTypes) {
			if (StringUtils.trimToEmpty(eqtnCustomerType.getCustTypeCode()).equals(customerType.getCustTypeCode())) {
				return true;
			}
		}
		return false;
	}

	private boolean checkDepartmentExist(EquationDepartment eqtnDepartment,List<EquationDepartment> existingDepartments){
		for (EquationDepartment department : existingDepartments) {
			if (StringUtils.trimToEmpty(eqtnDepartment.getDeptCode()).equals(department.getDeptCode())) {
				return true;
			}
		}
		return false;
	}

	private boolean checkCustomerGroupExist(EquationCustomerGroup eqtnCustomerGroup,List<EquationCustomerGroup> existingCustomerGroups){
		for (EquationCustomerGroup customerGroup : existingCustomerGroups) {
			if (StringUtils.trimToEmpty(eqtnCustomerGroup.getCustGrpCode()).equals(customerGroup.getCustGrpCode())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean checkAccountTypeExist(EquationAccountType eqtnAccountType,List<EquationAccountType> existingAccountTypes){
		for (EquationAccountType accountType : existingAccountTypes) {
			if (StringUtils.trimToEmpty(eqtnAccountType.getAcType()).equals(accountType.getAcType())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean checkCustomerRatingExist(EquationCustomerRating eqtncCustomerRating,List<EquationCustomerRating> existingcCustomerRatings){
		for (EquationCustomerRating customerRating : existingcCustomerRatings) {
			if (eqtncCustomerRating.getCustID() == customerRating.getCustID() &&  
					StringUtils.trimToEmpty(eqtncCustomerRating.getCustRatingType()).equals(customerRating.getCustRatingType())) {
				return true;
			}
		}
		return false;
	}
	
	
	private boolean checkTransactionCodeExist(EquationTransactionCode eqnTransactionCode,List<EquationTransactionCode> existingTransactionCode){
		for (EquationTransactionCode transactionCode : existingTransactionCode) {
			if (eqnTransactionCode.getTranCode().equals(transactionCode.getTranCode())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean checkIdentityTypeExist(EquationIdentityType eqnEquationIdentityType,List<EquationIdentityType> existingEquationIdentityType){
		for (EquationIdentityType identityType : existingEquationIdentityType) {
			String idType = StringUtils.trimToEmpty(eqnEquationIdentityType.getIdentityType());
			if (idType.trim().equals(identityType.getIdentityType().trim())) {
				return true;
			}
			idType = idType.startsWith("0") ? idType.substring(1, idType.length()) : idType;
			if (idType.trim().equals(identityType.getIdentityType().trim())) {
				eqnEquationIdentityType.setIdentityType(idType);
				return true;
			}
		}
		return false;
	}


	
	// ****************** Month End Downloads  *******************//
	
	@Override
	public List<FinanceType> fetchFinanceTypeDetails() {
		List<FinanceType> financeTypeList = getCoreInterfaceDAO().fetchFinanceTypeDetails();
		if(financeTypeList != null && !financeTypeList.isEmpty()){
			Map<String,List<FinTypeAccounting>> finTypeAccountingMap = getFinTypeAccountingMap();
			if(finTypeAccountingMap.size() > 0){
				for (FinanceType finTypeTemp : financeTypeList) {
					String finType = finTypeTemp.getFinType().trim();
					if(finTypeAccountingMap.containsKey(finType)){
						finTypeTemp.setFinTypeAccountingList(finTypeAccountingMap.get(finType));
					}
				}
			}
		}
		return financeTypeList;
	}
	
	private Map<String,List<FinTypeAccounting>> getFinTypeAccountingMap(){
		Map<String,List<FinTypeAccounting>> finTypeAccountingMap = new HashMap<String, List<FinTypeAccounting>>();
		List<FinTypeAccounting> finTypeAccountingList = getCoreInterfaceDAO().fetchFinTypeAccountings(AccountEventConstants.ACCEVENT_AMZ);
		if(finTypeAccountingList != null){
			for (FinTypeAccounting finTypeAcc : finTypeAccountingList) {
				String finType = finTypeAcc.getFinType();
				if(finTypeAccountingMap.containsKey(finType)) {
					finTypeAccountingMap.get(finType).add(finTypeAcc);
				} else {
					finTypeAccountingMap.put(finType, new ArrayList<FinTypeAccounting>());
					finTypeAccountingMap.get(finType).add(finTypeAcc);
				}
			}
		}
		return finTypeAccountingMap;
	}
	
	@Override
	public List<TransactionEntry> fetchTransactionEntryDetails(long accountSetID) {
		return getCoreInterfaceDAO().fetchTransactionEntryDetails(accountSetID);
	}
	
	@Override
	public boolean processIncomeAccTransactions(Date prvMnthStartDate) {
		logger.debug("Entering");
		boolean isExecuted = true;
		try{

			List<IncomeAccountTransaction> tempIncomeAccounts = new ArrayList<IncomeAccountTransaction>();
			List<IncomeAccountTransaction> saveIncomeAccTransactions = new ArrayList<IncomeAccountTransaction>();

			IncomeAccountTransaction incomeAccountTransaction = new IncomeAccountTransaction();
			incomeAccountTransaction.setLastMntOn(prvMnthStartDate);
			
			//Check Whether This Month Income Account Transactions Already Exist
			boolean monthIncomeTxnsExist = getCoreInterfaceDAO().checkIncomeTransactionsExist(incomeAccountTransaction);

			if(!monthIncomeTxnsExist){

				//Fetch Existing Income Account Details
				List<IncomeAccountTransaction> incomeAccounts = getCoreInterfaceDAO().fetchIncomeAccountDetails();

				if(incomeAccounts!= null ) {

					//Import Income Account Transactions From Core System
					for (IncomeAccountTransaction incomeAccount : incomeAccounts) {
						incomeAccount.setLastMntOn(prvMnthStartDate);
						tempIncomeAccounts.add(incomeAccount);
						if(tempIncomeAccounts.size()==498){
							saveIncomeAccTransactions.addAll(getDailyDownloadProcess().importIncomeAccTransactions(tempIncomeAccounts));
							tempIncomeAccounts.clear();
						}
					}
					if(tempIncomeAccounts.size() > 0){
						saveIncomeAccTransactions.addAll(getDailyDownloadProcess().importIncomeAccTransactions(tempIncomeAccounts));
						tempIncomeAccounts.clear();
					}

					if(!saveIncomeAccTransactions.isEmpty()){
						getCoreInterfaceDAO().saveIncomeAccTransactions(saveIncomeAccTransactions);
					}
				}
			}
		}catch(Exception e){
			logger.error("Exception: ", e);
			isExecuted = false;
		}
		logger.debug("Leaving");
		return isExecuted;
	}
	
	public void updateFinProfitIncomeAccounts(List<FinanceProfitDetail> accounts){
		getCoreInterfaceDAO().updateFinProfitIncomeAccounts(accounts);
	}
	
	private boolean customerAddressAlreadyExist(CustomerAddres customerAddres,List<CustomerAddres> list){
		for (CustomerAddres cAddres : list) {
			if(customerAddres.getCustID() == cAddres.getCustID() && 
					StringUtils.trimToEmpty(customerAddres.getCustAddrType()).equalsIgnoreCase(cAddres.getCustAddrType())){
				return true;
			}
		}
		return false;
	}
	
	private boolean customerPhoneNumAlreadyExist(CustomerPhoneNumber customerPhoneNumber,List<CustomerPhoneNumber> list){
		for (CustomerPhoneNumber cPhoneNumber : list) {
			if(customerPhoneNumber.getPhoneCustID() == cPhoneNumber.getPhoneCustID() && 
					StringUtils.trimToEmpty(customerPhoneNumber.getPhoneTypeCode()).equalsIgnoreCase(cPhoneNumber.getPhoneTypeCode())){
				return true;
			}
		}
		return false;
	}
	
	private boolean customerEmailAlreadyExist(CustomerEMail customerEMail,List<CustomerEMail> list){
		for (CustomerEMail cEMail : list) {
			if(customerEMail.getCustID() == cEMail.getCustID() && 
					StringUtils.trimToEmpty(customerEMail.getCustEMailTypeCode()).equalsIgnoreCase(cEMail.getCustEMailTypeCode())){
				return true;
			}
		}
		return false;
	}
	
	// ****************** Single Customer Download*******************//
	
	/**
	 * 
	 * @param customer
	 * @return
	 * @throws Exception 
	 * @throws CustomerNotFoundException 
	 */
	@Override
	public void saveCustomerDetails(CustomerDetails customerDetails) throws Exception{
		logger.debug("Entering");
		if(customerDetails.getAddressList() != null && !customerDetails.getAddressList().isEmpty()){
				getCoreInterfaceDAO().saveCustomerAddresses(customerDetails.getAddressList());
		}
		if(customerDetails.getCustomerPhoneNumList() != null && !customerDetails.getCustomerPhoneNumList().isEmpty()){
			getCoreInterfaceDAO().saveCustomerPhoneNumbers(customerDetails.getCustomerPhoneNumList());
		}
		if(customerDetails.getCustomerEMailList() != null && !customerDetails.getCustomerEMailList().isEmpty()){
			getCoreInterfaceDAO().saveCustomerEmails(customerDetails.getCustomerEMailList());
		}
		if(customerDetails.getRatingsList() != null && !customerDetails.getRatingsList().isEmpty()){
			getCoreInterfaceDAO().saveRatingDetails(customerDetails.getRatingsList());
		}
		logger.debug("Leaving");
	}
	
	
	public void saveMasterValueMissedDetails(List<EquationMasterMissedDetail> masterMissedDetails){
		getCoreInterfaceDAO().saveMasterValueMissedDetails(masterMissedDetails);
	}
	
	public void updateObjectDetails(String updateQuery,Object object){
		getCoreInterfaceDAO().updateObjectDetails(updateQuery, object);
	}
	
	public List<String> fetchBranchCodes() {
    	return  getCoreInterfaceDAO().fetchBranchCodes();
    }
	public List<Long> fetchCustomerGroupCodes() {
    	return  getCoreInterfaceDAO().fetchCustomerGroupCodes();
    }
	public List<String> fetchCountryCodes() {
    	return  getCoreInterfaceDAO().fetchCountryCodes();
    }
	public List<String> fetchSalutationCodes() {
    	return  getCoreInterfaceDAO().fetchSalutationCodes();
    }
	public List<String> fetchRelationshipOfficerCodes() {
    	return  getCoreInterfaceDAO().fetchRelationshipOfficerCodes();
    }
	public List<String> fetchMaritalStatusCodes() {
    	return  getCoreInterfaceDAO().fetchMaritalStatusCodes();
    }
	public List<SubSector> fetchSubSectorCodes() {
    	return  getCoreInterfaceDAO().fetchSubSectorCodes();
    }
	public List<String> fetchEmpStsCodes() {
    	return  getCoreInterfaceDAO().fetchEmpStsCodes();
    }
	public List<String> fetchCurrencyCodes() {
    	return  getCoreInterfaceDAO().fetchCurrencyCodes();
    }
	public List<String> fetchCustTypeCodes() {
    	return  getCoreInterfaceDAO().fetchCustTypeCodes();
    }
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public DailyDownloadProcess getDailyDownloadProcess() {
		return dailyDownloadProcess;
	}
	public void setDailyDownloadProcess(DailyDownloadProcess dailyDownloadProcess) {
		this.dailyDownloadProcess = dailyDownloadProcess;
	}

	public CoreInterfaceDAO getCoreInterfaceDAO() {
    	return coreInterfaceDAO;
	}	
	public void setCoreInterfaceDAO(CoreInterfaceDAO coreInterfaceDAO) {
    	this.coreInterfaceDAO = coreInterfaceDAO;
    }

	public PFSParameterDAO getpFSParameterDAO() {
		return pFSParameterDAO;
	}
	public void setpFSParameterDAO(PFSParameterDAO pFSParameterDAO) {
		this.pFSParameterDAO = pFSParameterDAO;
	}

	public CustomerInterfaceService getCustomerInterfaceService() {
	    return customerInterfaceService;
    }

	public void setCustomerInterfaceService(CustomerInterfaceService customerInterfaceService) {
	    this.customerInterfaceService = customerInterfaceService;
    }
	
}
