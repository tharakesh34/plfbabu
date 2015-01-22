package com.pennant.Interface.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.Interface.service.DailyDownloadInterfaceService;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.systemmasters.SubSector;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.model.CustomerInterfaceData;
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
import com.pennant.coreinterface.service.DailyDownloadProcess;
import com.pennant.equation.dao.CoreInterfaceDAO;

public class DailyDownloadInterfaceServiceImpl implements DailyDownloadInterfaceService {
	
	private final static Logger logger = Logger.getLogger(DailyDownloadInterfaceServiceImpl.class);
	
	private DailyDownloadProcess dailyDownloadProcess;
	private CoreInterfaceDAO coreInterfaceDAO;
	
	private static final String DEFAULT_CCY 		 = "BHD";
	private static final String DEFAULT_COUNTRY 	 = "BH";
	private static final String PHONE_TYEP_MOBILE 	 = "MOBILE";
	private static final String PHONE_TYEP_OFFICE 	 = "OFFICE";
	private static final String PHONE_TYEP_RESIDENCE = "WORK";
	private static final String PHONE_TYEP_OTHER 	 = "GENERAL";


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

			if(updateCurrienciesList != null && !updateCurrienciesList.isEmpty()){
				getCoreInterfaceDAO().updateCurrecnyDetails(updateCurrienciesList);
				isExecuted = true;
			}
			if(saveCurrienciesList != null && !saveCurrienciesList.isEmpty()){
				getCoreInterfaceDAO().saveCurrecnyDetails(saveCurrienciesList);
				isExecuted = true;
			}
		}catch(Exception e){
			logger.error(e);
			isExecuted = false;
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

			if(updateRelationshipOfficerList != null && !updateRelationshipOfficerList.isEmpty()){
				getCoreInterfaceDAO().updateRelationShipOfficerDetails(updateRelationshipOfficerList);
				isExecuted = true;
			}
			if(saveRelationshipOfficerList != null && !saveRelationshipOfficerList.isEmpty()){
				getCoreInterfaceDAO().saveRelationShipOfficerDetails(saveRelationshipOfficerList);
				isExecuted = true;
			}
		}catch(Exception e){
			logger.error(e);
			isExecuted = false;
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

			if(updateCustomerTypeList != null && !updateCustomerTypeList.isEmpty()){
				getCoreInterfaceDAO().updateCustomerTypeDetails(updateCustomerTypeList);
				isExecuted = true;
			}
			if(saveCustomerTypeList != null && !saveCustomerTypeList.isEmpty()){
				getCoreInterfaceDAO().saveCustomerTypeDetails(saveCustomerTypeList);
				isExecuted = true;
			}
		}catch(Exception e){
			logger.error(e);
			isExecuted = false;
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

			if(updateDepartmentList != null && !updateDepartmentList.isEmpty()){
				getCoreInterfaceDAO().updateDepartmentDetails(updateDepartmentList);
				isExecuted = true;
			}
			if(saveDepartmentList != null && !saveDepartmentList.isEmpty()){
				getCoreInterfaceDAO().saveDepartmentDetails(saveDepartmentList);
				isExecuted = true;
			}
		}catch(Exception e){
			logger.error(e);
			isExecuted = false;
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

			if(updateCustomerGroupList != null && !updateCustomerGroupList.isEmpty()){
				getCoreInterfaceDAO().updateCustomerGroupDetails(updateCustomerGroupList);
				isExecuted = true;
			}
			if(saveCustomerGroupList != null && !saveCustomerGroupList.isEmpty()){
				getCoreInterfaceDAO().saveCustomerGroupDetails(saveCustomerGroupList);
				isExecuted = true;
			}
		}catch(Exception e){
			logger.error(e);
			isExecuted = false;
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

				if(updateAccountTypeList != null && !updateAccountTypeList.isEmpty()){
					getCoreInterfaceDAO().updateAccountTypeDetails(updateAccountTypeList);
					getCoreInterfaceDAO().updateAccountTypeNatureDetails(updateAccountTypeList);
					isExecuted = true;
				}
				if(saveAccountTypeList != null && !saveAccountTypeList.isEmpty()){
					getCoreInterfaceDAO().saveAccountTypeDetails(saveAccountTypeList);
					getCoreInterfaceDAO().saveAccountTypeNatureDetails(saveAccountTypeList);
					isExecuted = true;
				}
		}catch(Exception e){
			logger.error(e);
			isExecuted = false;
		}
		logger.debug("Leaving");
		return isExecuted;
	}
	/**
	 * Method for Processing Customer Rating  Details
	 */
	@Override
	public boolean processCustomerRatingDetails(Date valuedate){
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
						masterMissedDetail.setLastMntOn(valuedate);
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

			if(updateCustomerRatingsList != null && !updateCustomerRatingsList.isEmpty()){
				getCoreInterfaceDAO().updateCustomerRatingDetails(updateCustomerRatingsList);
				isExecuted = true;
			}
			if(saveCustomerRatingsList != null && !saveCustomerRatingsList.isEmpty()){
				getCoreInterfaceDAO().saveCustomerRatingDetails(saveCustomerRatingsList);
				isExecuted = true;
			}
			if(masterValueMissedDetails != null && !masterValueMissedDetails.isEmpty()){
				getCoreInterfaceDAO().saveMasterValueMissedDetails(masterValueMissedDetails);
				isExecuted = true;
			}
		}catch(Exception e){
			logger.error(e);
			isExecuted = false;
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

			if(updatecountryList != null && !updatecountryList.isEmpty()){
				getCoreInterfaceDAO().updateCountryDetails(updatecountryList);
				isExecuted = true;
			}
			if(saveCountryList != null && !saveCountryList.isEmpty()){
				getCoreInterfaceDAO().saveCountryDetails(saveCountryList);
				isExecuted = true;
			}
		}catch(Exception e){
			logger.error(e);
			isExecuted = false;
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

			if(updateCustStsList != null && !updateCustStsList.isEmpty()){
				getCoreInterfaceDAO().updateCustStatusCodeDetails(updateCustStsList);
				isExecuted = true;
			}
			if(saveCustStsList != null && !saveCustStsList.isEmpty()){
				getCoreInterfaceDAO().saveCustStatusCodeDetails(saveCustStsList);
				isExecuted = true;
			}
		}catch(Exception e){
			logger.error(e);
			isExecuted = false;
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
			List<EquationIndustry> existingCustStatsuCodes = getCoreInterfaceDAO().fetchIndustryDetails();
			
			//Import Industry Details
			List<EquationIndustry> industryList = getDailyDownloadProcess().importIndustryDetails();

			List<EquationIndustry> saveIndustryList = new ArrayList<EquationIndustry>();
			List<EquationIndustry> updateIndustryList = new ArrayList<EquationIndustry>();
			
			if (existingCustStatsuCodes != null && !existingCustStatsuCodes.isEmpty()) {
				for (EquationIndustry eqtnIndustry : industryList) {
					if (checkIndustryExist(eqtnIndustry, existingCustStatsuCodes)) {
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

			if(updateIndustryList != null && !updateIndustryList.isEmpty()){
				getCoreInterfaceDAO().updateIndustryDetails(updateIndustryList);
				isExecuted = true;
			}
			if(saveIndustryList != null && !saveIndustryList.isEmpty()){
				getCoreInterfaceDAO().saveIndustryDetails(saveIndustryList);
				isExecuted = true;
			}
		}catch(Exception e){
			logger.error(e);
			isExecuted = false;
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

			if(updateBranchList != null && !updateBranchList.isEmpty()){
				getCoreInterfaceDAO().updateBranchDetails(updateBranchList);
				isExecuted = true;
			}
			if(saveBranchList != null && !saveBranchList.isEmpty()){
				getCoreInterfaceDAO().saveBranchDetails(saveBranchList);
				isExecuted = true;
			}
		}catch(Exception e){
			logger.error(e);
			isExecuted = false;
		}
		logger.debug("Leaving");
		return isExecuted;
	}
	
	
	/**
	 * Method for Processing Internal Account  Details
	 */
	@Override
	public boolean processInternalAccDetails(Date valuedate){
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
						masterMissedDetail.setModule("System Internal Accounts");
						masterMissedDetail.setLastMntOn(valuedate);
						masterMissedDetail.setFieldName("sIAAcType");
						masterMissedDetail.setDescription("SIACode : "+eqtnIntAcc.getsIACode()+" , '"+eqtnIntAcc.getsIAAcType()+"' Value Does Not Exist In Master RMTAccountTypes Table ");
						masterValueMissedDetails.add(masterMissedDetail);	
					}
				}
			} else {
				
				for (EquationInternalAccount eqtnIntAcc : internalAccList) {
	                
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
					
                }
				saveInternalAccList.addAll(internalAccList);
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
			logger.error(e);
			isExecuted = false;
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
			logger.error(e);
			isExecuted = false;
		}
		logger.debug("Leaving");
		return isExecuted;
	}
	

	/**
	 * Method for Processing Customer Details
	 */
	@Override
	public boolean processCustomerDetails(Date valuedate){
		logger.debug("Entering");
		
		boolean isExecuted = false;
		
		try{
			
			//Import Currency Details
			List<CustomerInterfaceData> cutomersList = getDailyDownloadProcess().importCustomerDetails();
			
			if (cutomersList != null && !cutomersList.isEmpty()) {
				List<Customer> customerList = new ArrayList<Customer>();
				List<CustomerAddres> addressList = new ArrayList<CustomerAddres>();
				List<CustomerPhoneNumber> phoneNumeberList = new ArrayList<CustomerPhoneNumber>();
				List<CustomerEMail> emailList = new ArrayList<CustomerEMail>();
				List<EquationMasterMissedDetail> masterValueMissedDetails = new ArrayList<EquationMasterMissedDetail>();
				CustomerEMail customerEMail = null; 
				CustomerPhoneNumber customerPhoneNumber = null;
				EquationMasterMissedDetail masterMissedDetail;
				
				List<String> addressTypeMasterList = getCoreInterfaceDAO().fetchAddressTypes();
				List<String> emailTypeMasterList = getCoreInterfaceDAO().fetchEMailTypes();
				
				for (CustomerInterfaceData customerInterfaceData : cutomersList) {

					//+++++++++++++++ Customer ++++++++++++++++
					String custCIF = customerInterfaceData.getCustCIF();
					Long custid=Long.parseLong(custCIF);
					Customer customer = new Customer();
					customer.setNewRecord(true);
					customer.setCustID(custid);
					customer.setCustCIF(custCIF);
					customer.setCustFName(customerInterfaceData.getCustFName());
					customer.setCustIsBlocked(getBoolean(customerInterfaceData.getCustIsBlocked()));
					customer.setCustIsActive(getBoolean(customerInterfaceData.getCustIsActive())); 
					customer.setCustDftBranch(customerInterfaceData.getCustDftBranch());
					customer.setCustGroupID(StringUtils.trimToEmpty(customerInterfaceData.getGroupName()).equals("") ? 0 : Long.parseLong(customerInterfaceData.getGroupName()));
					customer.setCustParentCountry(customerInterfaceData.getCustParentCountry());
					customer.setCustRiskCountry(customerInterfaceData.getCustRiskCountry());
					customer.setCustSalutationCode(customerInterfaceData.getCustSalutationCode()); 
					customer.setLovDescCustSalutationCodeName(customerInterfaceData.getCustSalutationCode()); 
					customer.setCustPassportNo(customerInterfaceData.getCustPassportNo());
					customer.setCustPassportExpiry(formatCYMDDate(customerInterfaceData.getCustPassportExpiry()));
					
					customer.setCustShrtName(customerInterfaceData.getCustShrtName());
					customer.setCustFNameLclLng(customerInterfaceData.getCustFNameLclLng());
					customer.setCustShrtNameLclLng(customerInterfaceData.getCustShrtNameLclLng());
					customer.setCustCOB(customerInterfaceData.getCustCOB());
					customer.setCustRO1(customerInterfaceData.getCustRO1()); 
					customer.setCustIsClosed(getBoolean(customerInterfaceData.getCustIsClosed()));
					customer.setCustIsDecease(getBoolean(customerInterfaceData.getCustIsDecease()));
					customer.setCustIsTradeFinCust(getBoolean(customerInterfaceData.getCustIsTradeFinCust()));
					customer.setCustSector(customerInterfaceData.getCustSector()); 
					customer.setCustSubSector(customerInterfaceData.getCustSubSector()); 
					customer.setCustMaritalSts(customerInterfaceData.getCustMaritalSts());
					customer.setCustEmpSts(customerInterfaceData.getCustEmpSts());
					customer.setCustBaseCcy(StringUtils.trimToEmpty(customerInterfaceData.getCustBaseCcy()).equals("") ? DEFAULT_CCY : customerInterfaceData.getCustBaseCcy());
					customer.setLovDescCustBaseCcyName(customer.getCustBaseCcy());//lov
					customer.setCustResdCountry(customerInterfaceData.getCustResdCountry());
					customer.setCustNationality(customerInterfaceData.getCustResdCountry());
					customer.setCustClosedOn(formatCYMDDate(customerInterfaceData.getCustClosedOn().toString())); 
					customer.setCustFirstBusinessDate(new Timestamp(formatCYMDDate(customerInterfaceData.getCustFirstBusinessDate().toString()).getTime()));
					customer.setCustRelation(customerInterfaceData.getCustRelation());
					customerList.add(customer);

					//<!-- Address Details--> 
					if (!StringUtils.trimToEmpty(customerInterfaceData.getCustAddrType()).equals("")) {
						if(valueExistInMaster(customerInterfaceData.getCustAddrType(),addressTypeMasterList)){
							CustomerAddres customerAddres = new CustomerAddres();
							customerAddres.setRecordType(PennantConstants.RCD_ADD);
							customerAddres.setCustID(custid);
							customerAddres.setLovDescCustCIF(custCIF);
							customerAddres.setCustAddrType(customerInterfaceData.getCustAddrType());
							customerAddres.setLovDescCustAddrTypeName(customerInterfaceData.getCustAddrType());
							customerAddres.setCustAddrHNbr(customerInterfaceData.getCustAddrHNbr());
							customerAddres.setCustFlatNbr(customerInterfaceData.getCustFlatNbr());
							customerAddres.setCustAddrStreet(customerInterfaceData.getCustAddrStreet());
							customerAddres.setCustAddrLine1(customerInterfaceData.getCustAddrLine1());
							customerAddres.setCustAddrLine2(customerInterfaceData.getCustAddrLine2());
							customerAddres.setCustAddrZIP(customerInterfaceData.getCustAddrZIP());
							customerAddres.setCustAddrPhone(customerInterfaceData.getCustAddrPhone());
							addressList.add(customerAddres);
						}else{
							masterMissedDetail = new EquationMasterMissedDetail();
							masterMissedDetail.setModule("Address Details");
							masterMissedDetail.setLastMntOn(valuedate);
							masterMissedDetail.setFieldName("CustAddrType");
							masterMissedDetail.setDescription("Customer : "+customerInterfaceData.getCustCIF()+" , '"+customerInterfaceData.getCustAddrType()+"' Value Does Not Exist In Master BMTAddressTypes Table ");
							masterValueMissedDetails.add(masterMissedDetail);	
						}
					}

					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					//<!-- customer phone numbers  1,2,3,4-->	
					String custOfficePhone=StringUtils.trimToEmpty(customerInterfaceData.getCustOfficePhone());
					//Length Mismatch from Equation
					if (!custOfficePhone.equals("") && custOfficePhone.length()<12) {
						customerPhoneNumber = new CustomerPhoneNumber();
						customerPhoneNumber.setRecordType(PennantConstants.RCD_ADD);
						customerPhoneNumber.setPhoneCustID(custid);
						customerPhoneNumber.setLovDescCustCIF(custCIF);
						customerPhoneNumber.setPhoneTypeCode(PHONE_TYEP_OFFICE);
						customerPhoneNumber.setPhoneCountryCode(DEFAULT_COUNTRY);
						customerPhoneNumber.setPhoneAreaCode(DEFAULT_COUNTRY);
						customerPhoneNumber.setPhoneNumber(custOfficePhone);
						phoneNumeberList.add(customerPhoneNumber);
					}
					String custMobile=StringUtils.trimToEmpty(customerInterfaceData.getCustMobile());
					//Length Mismatch from Equation
					if (!custMobile.equals("") && custMobile.length()<12) {
						customerPhoneNumber = new CustomerPhoneNumber();
						customerPhoneNumber.setRecordType(PennantConstants.RCD_ADD);
						customerPhoneNumber.setPhoneCustID(custid);
						customerPhoneNumber.setLovDescCustCIF(custCIF);
						customerPhoneNumber.setPhoneTypeCode(PHONE_TYEP_MOBILE);
						customerPhoneNumber.setPhoneCountryCode(DEFAULT_COUNTRY);
						customerPhoneNumber.setPhoneAreaCode(DEFAULT_COUNTRY);
						customerPhoneNumber.setPhoneNumber(custMobile);
						phoneNumeberList.add(customerPhoneNumber);
					}
					String custResPhone=StringUtils.trimToEmpty(customerInterfaceData.getCustResPhone());
					//Length Mismatch from Equation
					if (!custResPhone.equals("") && custResPhone.length()<12) {
						customerPhoneNumber = new CustomerPhoneNumber();
						customerPhoneNumber.setRecordType(PennantConstants.RCD_ADD);
						customerPhoneNumber.setPhoneCustID(custid);
						customerPhoneNumber.setLovDescCustCIF(custCIF);
						customerPhoneNumber.setPhoneTypeCode(PHONE_TYEP_RESIDENCE);
						customerPhoneNumber.setPhoneCountryCode(DEFAULT_COUNTRY);
						customerPhoneNumber.setPhoneAreaCode(DEFAULT_COUNTRY);
						customerPhoneNumber.setPhoneNumber(custResPhone);
						phoneNumeberList.add(customerPhoneNumber);
					}
					String custOtherPhone=StringUtils.trimToEmpty(customerInterfaceData.getCustOtherPhone());
					//Length Mismatch from Equation
					if (!custOtherPhone.equals("") && custOtherPhone.length()<12) {
						customerPhoneNumber = new CustomerPhoneNumber();
						customerPhoneNumber.setRecordType(PennantConstants.RCD_ADD);
						customerPhoneNumber.setPhoneCustID(custid);
						customerPhoneNumber.setLovDescCustCIF(custCIF);
						customerPhoneNumber.setPhoneTypeCode(PHONE_TYEP_OTHER);
						customerPhoneNumber.setPhoneCountryCode(DEFAULT_COUNTRY);
						customerPhoneNumber.setPhoneAreaCode(DEFAULT_COUNTRY);
						customerPhoneNumber.setPhoneNumber(custOtherPhone);
						phoneNumeberList.add(customerPhoneNumber);
					}
					//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					//<!-- Email Details 1 and 2-->
					if (!StringUtils.trimToEmpty(customerInterfaceData.getCustEMail1()).equals("") && 
							!StringUtils.trimToEmpty(customerInterfaceData.getCustEMailTypeCode1()).equals("")) {
						if(valueExistInMaster(customerInterfaceData.getCustEMailTypeCode1(),emailTypeMasterList)){
							customerEMail = new CustomerEMail();
							customerEMail.setRecordType(PennantConstants.RCD_ADD);
							customerEMail.setCustID(custid);
							customerEMail.setLovDescCustCIF(custCIF);
							customerEMail.setLovDescCustEMailTypeCode(customerInterfaceData.getCustEMailTypeCode1());
							customerEMail.setCustEMailPriority(1);
							customerEMail.setCustEMailTypeCode(customerInterfaceData.getCustEMailTypeCode1());
							customerEMail.setCustEMail(customerInterfaceData.getCustEMail1());
							emailList.add(customerEMail);
						}else{
							masterMissedDetail = new EquationMasterMissedDetail();
							masterMissedDetail.setModule("Email Details");
							masterMissedDetail.setLastMntOn(valuedate);
							masterMissedDetail.setFieldName("CustEMailTypeCode");
							masterMissedDetail.setDescription("Customer : "+customerInterfaceData.getCustCIF()+" , '"+customerInterfaceData.getCustEMailTypeCode1()+"' Value Does Not Exist In Master BMTEMailTypes Table ");
							masterValueMissedDetails.add(masterMissedDetail);
						}
					}
					if (!StringUtils.trimToEmpty(customerInterfaceData.getCustEMail2()).equals("") && 
							!StringUtils.trimToEmpty(customerInterfaceData.getCustEMailTypeCode2()).equals("")) {
						if(valueExistInMaster(customerInterfaceData.getCustEMailTypeCode2(),emailTypeMasterList)){
							customerEMail = new CustomerEMail();
							customerEMail.setRecordType(PennantConstants.RCD_ADD);
							customerEMail.setCustID(custid);
							customerEMail.setLovDescCustCIF(custCIF);
							customerEMail.setLovDescCustEMailTypeCode(customerInterfaceData.getCustEMailTypeCode2());
							customerEMail.setCustEMailPriority(2);
							customerEMail.setCustEMailTypeCode(customerInterfaceData.getCustEMailTypeCode2());
							customerEMail.setCustEMail(customerInterfaceData.getCustEMail2());
							emailList.add(customerEMail);
						}else{
							masterMissedDetail = new EquationMasterMissedDetail();
							masterMissedDetail.setModule("Email Details");
							masterMissedDetail.setLastMntOn(valuedate);
							masterMissedDetail.setFieldName("CustEMailTypeCode");
							masterMissedDetail.setDescription("Customer : "+customerInterfaceData.getCustCIF()+" , '"+customerInterfaceData.getCustEMailTypeCode2()+"' Value Does Not Exist In Master BMTEMailTypes Table ");
							masterValueMissedDetails.add(masterMissedDetail);
						}
					}

					//<!-- Employee Details-->
                  /* if(!StringUtils.trimToEmpty(customerInterfaceData.getCustEmpName()).equals("")){
					customerEmploymentDetail = new CustomerEmploymentDetail();
					customerEmploymentDetail.setCustID(custid);
					customerEmploymentDetail.setCustEmpName(Long.valueOf(customerInterfaceData.getCustEmpName()));
					customerEmploymentDetail.setCustEmpFrom(formatCYMDDate(customerInterfaceData.getCustEmpFrom().toString()));
					customerEmploymentDetail.setCustEmpDesg(customerInterfaceData.getCustEmpDesg());
					emplomentList.add(customerEmploymentDetail);
                   }*/
					//+++++++++++++++++++++++++++++++++++++++++++++++++
				}
				
				if(customerList != null && !customerList.isEmpty()){
				
					//Fetching customer related Master details
					List<String> branchCodeMasterList = getCoreInterfaceDAO().fetchBranchCodes();
					List<Long> custGrpCodeMasterList = getCoreInterfaceDAO().fetchCustomerGroupCodes();
					List<String> countryCodeMasterList = getCoreInterfaceDAO().fetchCountryCodes();
					List<String> salutationCodeMasterList = getCoreInterfaceDAO().fetchSalutationCodes();
					List<String> rShipOfficerCodeMasterList = getCoreInterfaceDAO().fetchRelationshipOfficerCodes();
					List<SubSector> subSectorCodeMasterList = getCoreInterfaceDAO().fetchSubSectorCodes();
					List<String> maritalStatusCodeMasterList = getCoreInterfaceDAO().fetchMaritalStatusCodes();
					List<String> custEmpStsCodeMasterList = getCoreInterfaceDAO().fetchEmpStsCodes();
					List<String> currencyCodeMasterList = getCoreInterfaceDAO().fetchCurrencyCodes();
					
					for (Customer customer : customerList) {
						masterMissedDetail = new EquationMasterMissedDetail();
						masterMissedDetail.setModule("Customers");
						masterMissedDetail.setLastMntOn(valuedate);
						if(!StringUtils.trimToEmpty(customer.getCustDftBranch()).equals("") && 
								!valueExistInMaster(customer.getCustDftBranch(),branchCodeMasterList)){
							masterMissedDetail.setFieldName("CustDftBranch");
							masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustDftBranch()+"' Value Does Not Exist In Master RMTBranches Table ");
							customer.setCustDftBranch(""); //Making it empty to ignore the empty field updates in query while updating the record 
							masterValueMissedDetails.add(masterMissedDetail);	
						}
						if(customer.getCustGroupID() != 0 && !valueExistInMaster(customer.getCustGroupID(),custGrpCodeMasterList)){
							masterMissedDetail.setFieldName("CustGroupID");
							masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustGroupID()+"' Value Does Not Exist In Master CustomerGroups Table ");
							customer.setCustGroupID(0);
							masterValueMissedDetails.add(masterMissedDetail);	
						}
						if(!StringUtils.trimToEmpty(customer.getCustParentCountry()).equals("") && 
								!valueExistInMaster(customer.getCustParentCountry(),countryCodeMasterList)){
							masterMissedDetail.setFieldName("CustParentCountry");
							masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustParentCountry()+"' Value Does Not Exist In Master BMTCountries Table ");
							customer.setCustParentCountry("");
							masterValueMissedDetails.add(masterMissedDetail);	
						}
						if(!StringUtils.trimToEmpty(customer.getCustRiskCountry()).equals("") && 
								!valueExistInMaster(customer.getCustRiskCountry(),countryCodeMasterList)){
							masterMissedDetail.setFieldName("CustRiskCountry");
							masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustRiskCountry()+"' Value Does Not Exist In Master BMTCountries Table ");
							customer.setCustRiskCountry("");
							masterValueMissedDetails.add(masterMissedDetail);	
						}
						if(!StringUtils.trimToEmpty(customer.getCustResdCountry()).equals("") &&
								!valueExistInMaster(customer.getCustResdCountry(),countryCodeMasterList)){
							masterMissedDetail.setFieldName("CustResdCountry");
							masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustResdCountry()+"' Value Does Not Exist In Master BMTCountries Table ");
							customer.setCustResdCountry("");
							masterValueMissedDetails.add(masterMissedDetail);	
						}
						if(!StringUtils.trimToEmpty(customer.getCustSalutationCode()).equals("") &&
								!valueExistInMaster(customer.getCustSalutationCode(),salutationCodeMasterList)){
							masterMissedDetail.setFieldName("CustSalutationCode");
							masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustSalutationCode()+"' Value Does Not Exist In Master BMTSalutations Table ");
							customer.setCustSalutationCode("");
							masterValueMissedDetails.add(masterMissedDetail);	
						}
						if(!StringUtils.trimToEmpty(customer.getCustRO1()).equals("") &&
								!valueExistInMaster(customer.getCustRO1(),rShipOfficerCodeMasterList)){
							masterMissedDetail.setFieldName("CustRO1");
							masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustRO1()+"' Value Does Not Exist In Master RelationshipOfficers Table ");
							customer.setCustRO1("");
							masterValueMissedDetails.add(masterMissedDetail);	
						}
						if(!StringUtils.trimToEmpty(customer.getCustSector()).equals("")  && 
								!StringUtils.trimToEmpty(customer.getCustSubSector()).equals("")  &&
								!valueExistInMaster(customer,subSectorCodeMasterList)){
							masterMissedDetail.setFieldName("CustSector/CustSubSector");
							masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , CustSector:'"+customer.getCustSector()+
									"' and CustSubSector:'"+customer.getCustSubSector()+"' Values Does Not Exist In Master BMTSubSectors Table ");
							customer.setCustSector("");
							masterValueMissedDetails.add(masterMissedDetail);
						}
						
						if(!StringUtils.trimToEmpty(customer.getCustMaritalSts()).equals("") &&
								!valueExistInMaster(customer.getCustMaritalSts(),maritalStatusCodeMasterList)){
							masterMissedDetail.setFieldName("CustMaritalSts");
							masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustMaritalSts()+"' Value Does Not Exist In Master BMTMaritalStatusCodes Table ");
							customer.setCustMaritalSts("");
							masterValueMissedDetails.add(masterMissedDetail);	
						}
						if(!StringUtils.trimToEmpty(customer.getCustEmpSts()).equals("") &&
								!valueExistInMaster(customer.getCustEmpSts(),custEmpStsCodeMasterList)){
							masterMissedDetail.setFieldName("CustEmpSts");
							masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustEmpSts()+"' Value Does Not Exist In Master BMTEmpStsCodes Table ");
							customer.setCustEmpSts("");
							masterValueMissedDetails.add(masterMissedDetail);	
						}
						if(!StringUtils.trimToEmpty(customer.getCustBaseCcy()).equals("") &&
								!valueExistInMaster(customer.getCustBaseCcy(),currencyCodeMasterList)){
							masterMissedDetail.setFieldName("CustBaseCcy");
							masterMissedDetail.setDescription("Customer : "+customer.getCustCIF()+" , '"+customer.getCustBaseCcy()+"' Value Does Not Exist In Master RMTCurrencies Table ");
							customer.setCustBaseCcy("");
							masterValueMissedDetails.add(masterMissedDetail);	
						}
						
                    }
					getCoreInterfaceDAO().updateCustomerDetails(customerList);
					getCoreInterfaceDAO().saveMasterValueMissedDetails(masterValueMissedDetails);
					isExecuted = true;
				}
				if(addressList != null && !addressList.isEmpty()){
					isExecuted = false;
					getCoreInterfaceDAO().updateAddressDetails(addressList);
					isExecuted = true;
				}
				if(phoneNumeberList != null && !phoneNumeberList.isEmpty()){
					isExecuted = false;
					getCoreInterfaceDAO().updatePhoneNumberDetails(phoneNumeberList);
					isExecuted = true;
				}
				if(emailList != null && !emailList.isEmpty()){
					isExecuted = false;
					getCoreInterfaceDAO().updateEMailDetails(emailList);
					isExecuted = true;
				}
				/*if(emplomentList != null && !emplomentList.isEmpty()){
					getCoreInterfaceDAO().updateEmploymentDetails(emplomentList);
					isExecuted = true;
				}*/
			}
			
		}catch(Exception e){
			logger.error(e);
			e.printStackTrace();
			isExecuted = false;
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
			
			if(updateTransactionCodesList != null && !updateTransactionCodesList.isEmpty()){
				getCoreInterfaceDAO().updateTransactionCodes(updateTransactionCodesList);
				isExecuted = true;
			}
			if(saveTransactionCodesList != null && !saveTransactionCodesList.isEmpty()){
				getCoreInterfaceDAO().saveTransactionCodeDetails(saveTransactionCodesList);
				isExecuted = true;
			}
		}catch(Exception e){
			logger.error(e);
			isExecuted = false;
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
			
			if(updateIdentityTypesList != null && !updateIdentityTypesList.isEmpty()){
				getCoreInterfaceDAO().updateIdentityTypes(updateIdentityTypesList);
				isExecuted = true;
			}
			if(saveIdentityTypesList != null && !saveIdentityTypesList.isEmpty()){
				getCoreInterfaceDAO().saveIdentityTypeDetails(saveIdentityTypesList);
				isExecuted = true;
			}
		}catch(Exception e){
			logger.error(e);
			isExecuted = false;
		}
		logger.debug("Leaving");
		return isExecuted;
	}


	private boolean valueExistInMaster(String field,List<String> list){
		for (String value : list) {
	        if(StringUtils.trimToEmpty(field).equalsIgnoreCase(value.toString())){
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
	
	private boolean valueExistInMaster(Customer customer ,List<SubSector> list){
		for (SubSector subSector : list) {
	        if(StringUtils.trimToEmpty(customer.getCustSector()).equalsIgnoreCase(subSector.getSectorCode()) && 
	        		StringUtils.trimToEmpty(customer.getCustSubSector()).equalsIgnoreCase(subSector.getSubSectorCode())){
	        	return true;
	        }
        }
		return false;
	}
	
	
	private boolean getBoolean(String string) {
		if (StringUtils.trimToEmpty(string).equals("Y") || StringUtils.trimToEmpty(string).equals("1")) {
			return true;
		} else {
			return false;
		}

	}
	

	private Date formatCYMDDate(String date) {
		try {
			return 	DateUtility.convertDateFromAS400(new BigDecimal(date));
		} catch (Exception e) {
			return null;
		}

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


	
	// ++++++++++++++++++ Month End Downloads  +++++++++++++++++++//

	
	@Override
    public List<FinanceType> fetchFinanceTypeDetails() {
		return getCoreInterfaceDAO().fetchFinanceTypeDetails();
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

					if(saveIncomeAccTransactions != null && !saveIncomeAccTransactions.isEmpty()){
						getCoreInterfaceDAO().saveIncomeAccTransactions(saveIncomeAccTransactions);
					}
				}
			}
		}catch(Exception e){
			logger.error(e);
			isExecuted = false;
		}
		logger.debug("Leaving");
		return isExecuted;
	}
	
	public void updateFinProfitIncomeAccounts(List<FinanceProfitDetail> accounts){
		getCoreInterfaceDAO().updateFinProfitIncomeAccounts(accounts);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
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

}
