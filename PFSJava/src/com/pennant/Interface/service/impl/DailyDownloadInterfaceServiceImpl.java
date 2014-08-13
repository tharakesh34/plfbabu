package com.pennant.Interface.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.Interface.service.DailyDownloadInterfaceService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.model.EquationAccountType;
import com.pennant.coreinterface.model.EquationCurrency;
import com.pennant.coreinterface.model.EquationCustomerGroup;
import com.pennant.coreinterface.model.EquationCustomerRating;
import com.pennant.coreinterface.model.EquationCustomerType;
import com.pennant.coreinterface.model.EquationDepartment;
import com.pennant.coreinterface.model.EquationRelationshipOfficer;
import com.pennant.coreinterface.service.DailyDownloadProcess;
import com.pennant.equation.dao.CoreInterfaceDAO;

public class DailyDownloadInterfaceServiceImpl implements DailyDownloadInterfaceService {
	
	private final static Logger logger = Logger.getLogger(DailyDownloadInterfaceServiceImpl.class);
	
	private DailyDownloadProcess dailyDownloadProcess;
	private CoreInterfaceDAO coreInterfaceDAO;
	
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
	public boolean processCustomerRatingDetails(){
		logger.debug("Entering");
		
		boolean isExecuted = false;
		List<EquationCustomerRating> existingcuCustomerRatings;
		List<EquationCustomerRating> customerRatingsList;		
		try{
			existingcuCustomerRatings = getCoreInterfaceDAO().fetchCustomerRatingDetails();
			customerRatingsList = getDailyDownloadProcess().importCustomerRatingDetails();
			
			List<EquationCustomerRating> saveCustomerRatingsList = new ArrayList<EquationCustomerRating>();
			List<EquationCustomerRating> updateCustomerRatingsList = new ArrayList<EquationCustomerRating>();
			
			if (existingcuCustomerRatings != null && !existingcuCustomerRatings.isEmpty()) {
				for (EquationCustomerRating customerRating : customerRatingsList) {
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
		}catch(Exception e){
			logger.error(e);
			isExecuted = false;
		}
		logger.debug("Leaving");
		return isExecuted;
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
