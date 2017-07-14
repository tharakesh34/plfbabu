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
 * FileName    		:  DedupParmServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-08-2011    														*
 *                                                                  						*
 * Modified Date    :  23-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-08-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.dedup.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.Interface.service.CustomerInterfaceService;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.BlackListCustomerDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.custdedup.CustomerDedupDAO;
import com.pennant.backend.dao.dedup.DedupParmDAO;
import com.pennant.backend.dao.findedup.FinanceDedupeDAO;
import com.pennant.backend.dao.policecase.PoliceCaseDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.PoliceCaseDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.model.blacklist.FinBlacklistCustomer;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.dedup.DedupParm;
import com.pennant.backend.model.finance.FinanceDedup;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.policecase.PoliceCase;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.model.DedupCustomerDetail;
import com.pennanttech.model.DedupCustomerResponse;
import com.pennanttech.pff.core.InterfaceException;
import com.pennanttech.service.CustomerDedupService;

/**
 * Service implementation for methods that depends on <b>DedupParm</b>.<br>
 * 
 */
public class DedupParmServiceImpl extends GenericService<DedupParm> implements DedupParmService {
	private static final Logger logger = Logger.getLogger(DedupParmServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private DedupParmDAO dedupParmDAO;
	private BlackListCustomerDAO blacklistCustomerDAO;
	private FinanceDedupeDAO financeDedupeDAO;
	private CustomerInterfaceService customerInterfaceService;
	private PoliceCaseDAO policeCaseDAO;
	private CustomerDedupDAO customerDedupDAO;
	private CustomerDedupService customerDedupService;

	public DedupParmServiceImpl() {
		super();
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public DedupParmDAO getDedupParmDAO() {
		return dedupParmDAO;
	}
	public void setDedupParmDAO(DedupParmDAO dedupParmDAO) {
		this.dedupParmDAO = dedupParmDAO;
	}

	public FinanceDedupeDAO getFinanceDedupeDAO() {
		return financeDedupeDAO;
	}
	public void setFinanceDedupeDAO(FinanceDedupeDAO financeDedupeDAO) {
		this.financeDedupeDAO = financeDedupeDAO;
	}

	public BlackListCustomerDAO getBlacklistCustomerDAO() {
		return blacklistCustomerDAO;
	}
	public void setBlacklistCustomerDAO(BlackListCustomerDAO blacklistCustomerDAO) {
		this.blacklistCustomerDAO = blacklistCustomerDAO;
	}
	public PoliceCaseDAO getPoliceCaseDAO() {
		return policeCaseDAO;
	}
	public void setPoliceCaseDAO(PoliceCaseDAO policeCaseDAO) {
		this.policeCaseDAO = policeCaseDAO;
	}

	public CustomerDedupDAO getCustomerDedupDAO() {
		return customerDedupDAO;
	}

	public void setCustomerDedupDAO(CustomerDedupDAO customerDedupDAO) {
		this.customerDedupDAO = customerDedupDAO;
	}
	public CustomerInterfaceService getCustomerInterfaceService() {
		return customerInterfaceService;
	} 
	public void setCustomerInterfaceService(CustomerInterfaceService customerInterfaceService) {
		this.customerInterfaceService = customerInterfaceService;
	}
	public void setCustomerDedupService(CustomerDedupService customerDedupService) {
		this.customerDedupService = customerDedupService;
	}
	@SuppressWarnings("rawtypes")
	@Override
	public List validate(String resultQuery,CustomerDedup customerDedup ) {
		return getDedupParmDAO().validate(resultQuery,customerDedup);
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * DedupParams/DedupParams_Temp by using DedupParmDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using DedupParmDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtDedupParams by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");	

		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		DedupParm dedupParm = (DedupParm) auditHeader.getAuditDetail().getModelData();

		if (dedupParm.isWorkflow()) {
			tableType="_Temp";
		}

		if (dedupParm.isNew()) {
			getDedupParmDAO().save(dedupParm,tableType);
		}else{
			getDedupParmDAO().update(dedupParm,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table DedupParams by using DedupParmDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtDedupParams by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader,"delete");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		DedupParm dedupParm = (DedupParm) auditHeader.getAuditDetail().getModelData();
		getDedupParmDAO().delete(dedupParm,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getDedupParmById fetch the details by using DedupParmDAO's
	 * getDedupParmById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return DedupParm
	 */
	@Override
	public DedupParm getDedupParmById(String id,String queryModule,String querySubCode) {
		return getDedupParmDAO().getDedupParmByID(id,queryModule,querySubCode,"_View");
	}

	/**
	 * getApprovedDedupParmById fetch the details by using DedupParmDAO's
	 * getDedupParmById method . with parameter id and type as blank. it fetches
	 * the approved records from the DedupParams.
	 * 
	 * @param id
	 *            (String)
	 * @return DedupParm
	 */
	@Override
	public DedupParm getApprovedDedupParmById(String id,String queryModule,String querySubCode) {
		return getDedupParmDAO().getDedupParmByID(id,queryModule,querySubCode,"");
	}

	@Override
	public List<CustomerDedup> fetchCustomerDedupDetails(String userRole, CustomerDedup customerDedup, String curLoginUser, String finType) throws InterfaceException {

		List<CustomerDedup> customerDedupList = new ArrayList<CustomerDedup>();
		List<CustomerDedup> overridedCustDedupList =  new ArrayList<CustomerDedup>();
		boolean newUser = false;

		//Fetch List of Query Details Existing to check Finance Dedupe based on Finance Type & Stage
		FinanceReferenceDetail referenceDetail = new FinanceReferenceDetail();
		referenceDetail.setMandInputInStage(userRole+",");
		referenceDetail.setFinType(finType);
		List<FinanceReferenceDetail> queryCodeList = getDedupParmDAO().getQueryCodeList(referenceDetail, "_ACDView");

		if(queryCodeList != null) {

			List<DedupParm> dedupParmList = new ArrayList<DedupParm>();
			//Fetch Builded SQL Query based on Query Code  

			for (FinanceReferenceDetail queryCode : queryCodeList) {

				//get override Customers Dedup
				List<CustomerDedup> custDedupList = getCustomerDedupDAO().fetchOverrideCustDedupData(customerDedup.getFinReference(), 
						queryCode.getLovDescNamelov(),FinanceConstants.DEDUP_FINANCE);

				DedupParm dedupParm = getApprovedDedupParmById(queryCode.getLovDescNamelov(),	FinanceConstants.DEDUP_CUSTOMER, customerDedup.getCustCtgCode());

				if(dedupParm != null){
					dedupParmList.add(dedupParm);
				}
				if(!custDedupList.isEmpty()){
					for(CustomerDedup custDedup:custDedupList) {
						custDedup.setOverridenby(custDedup.getOverrideUser());
						overridedCustDedupList.add(custDedup);
					}
				}
				custDedupList = null;
				dedupParm = null;
			}

			//Using Queries Fetch dedup Customer Data either from Interface or 
			//Existing Black Listed Table(Daily Download) Data
			if (!dedupParmList.isEmpty()) {
				customerDedupList.addAll(getCustomerDedup(customerDedup, dedupParmList));
				if (!customerDedupList.isEmpty()) {
					customerDedupList = resetDedupCustData(customerDedupList, queryCodeList);
				} else {
					return customerDedupList;
				}
			} else {
				for (CustomerDedup custDedup : overridedCustDedupList) {
					if (!custDedup.getOverrideUser().contains(curLoginUser)) {
						custDedup.setOverridenby(custDedup.getOverrideUser());
						custDedup.setOverrideUser(custDedup.getOverrideUser() + "," + curLoginUser);
						newUser = false;
					}
				}

			}
		} else {
			return customerDedupList;
		}

		// Grouping Black List Customer which are having same result of Data
		customerDedupList = doSetCustomerDeDupGrouping(customerDedupList);

		// Checking for duplicate records in overrideBlacklistCustomers and currentBlacklistCustomers
		try {
			if (!overridedCustDedupList.isEmpty() && !customerDedupList.isEmpty()) {

				for (CustomerDedup previousDedup: overridedCustDedupList) {
					for (CustomerDedup currentDedup: customerDedupList) {
						if (previousDedup.getCustCIF().equals(currentDedup.getCustCIF())) {
							currentDedup.setOverridenby(previousDedup.getOverrideUser());
							if(previousDedup.getOverrideUser().contains(curLoginUser)) {
								currentDedup.setOverrideUser(previousDedup.getOverrideUser());
								newUser = false;
							} else {
								currentDedup.setOverrideUser(previousDedup.getOverrideUser() + "," + curLoginUser);
							}
							//Checking for New Rule
							if (isRuleChanged(previousDedup.getDedupRule(),currentDedup.getDedupRule())) {
								currentDedup.setNewRule(true);
								if(previousDedup.getCustCIF().equals(currentDedup.getCustCIF())) {
									currentDedup.setNewCustDedupRecord(false);
								} else {
									currentDedup.setNewCustDedupRecord(true);
									currentDedup.setOverride(false);
								}
							} else {
								currentDedup.setNewCustDedupRecord(false);
							}

							if(newUser) {
								currentDedup.setOverride(previousDedup.isOverride());
							}
						}
					}
				}
			} else if (!overridedCustDedupList.isEmpty() && customerDedupList.isEmpty()) {
				customerDedupList.addAll(overridedCustDedupList);
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return customerDedupList;

    }
	
	private List<CustomerDedup> resetDedupCustData(List<CustomerDedup> customerDedupList, List<FinanceReferenceDetail> queryCodeList) {
		logger.debug("Entering");

		//Check Override Condition based on Rule definitions on Process Editor
		HashMap<String, Boolean> queryOverrideMap = new HashMap<String, Boolean>();
		for (FinanceReferenceDetail referenceDetail : queryCodeList) {
			queryOverrideMap.put(referenceDetail.getLovDescNamelov(), referenceDetail.isOverRide());			
		}

		//Reset Override COndition based on Query Code Executions
		for (CustomerDedup custDedup : customerDedupList) {
			if(custDedup.getDedupRule() != null) {
				String[] dedupRuleList = custDedup.getDedupRule().split(",");
				for(int i = 0; i< dedupRuleList.length; i++) {
					if(queryOverrideMap.containsKey(dedupRuleList[i])){
						custDedup.setOverride(queryOverrideMap.get(dedupRuleList[i]));
						if(custDedup.isOverride()) {
							custDedup.setOverride(queryOverrideMap.get(dedupRuleList[i]));
						} else {
							custDedup.setOverride(false);
							break;
						}
					}
				}
				if(queryOverrideMap.containsKey(custDedup.getDedupRule())){
					custDedup.setOverride(queryOverrideMap.get(custDedup.getDedupRule()));
				}
			}
		}
		logger.debug("Leaving");
		return customerDedupList;
	}
	private List<CustomerDedup> doSetCustomerDeDupGrouping(List<CustomerDedup> customerDedupList) {
		logger.debug("Entering");
		try {
			for (int i = 0; i < customerDedupList.size(); i++) {
				CustomerDedup icustDedupList = customerDedupList.get(i);
				for (int j = i + 1; j <= customerDedupList.size() - 1; j++) {
					CustomerDedup jcustDedupList = customerDedupList.get(j);
					if (icustDedupList.getCustCIF().equals(jcustDedupList.getCustCIF())) {
						if (!icustDedupList.getDedupRule().contains(jcustDedupList.getDedupRule())) {
							icustDedupList.setDedupRule(icustDedupList.getDedupRule() + "," + jcustDedupList.getDedupRule());
						}
						icustDedupList.setQueryField(icustDedupList.getQueryField() + "," + jcustDedupList.getQueryField());
						if(!jcustDedupList.isOverride()) {
							icustDedupList.setOverride(jcustDedupList.isOverride());
						}
						customerDedupList.remove(j);
						j--;
					}
				}
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return customerDedupList;
	}

	@Override
	public List<CustomerDedup> getCustomerDedup(CustomerDedup customerDedup, List<DedupParm> dedupParmList) throws InterfaceException {
		logger.debug("Entering");

		List<CustomerDedup> customerDedupList = new ArrayList<CustomerDedup>();

		if(dedupParmList != null && !dedupParmList.isEmpty()){

			List<String> fieldNameList = getDedupParmDAO().getRuleFieldNames(customerDedup.getCustCtgCode()+FinanceConstants.DEDUP_CUSTOMER);

			//To Check duplicate customer in core banking
			List<CustomerDedup> coreCustDedupList = getCustomerInterfaceService().fetchCustomerDedupDetails(customerDedup);
			
			if(!coreCustDedupList.isEmpty()) {
				customerDedupList.addAll(coreCustDedupList);
			}

			//TO Check duplicate customer  in Local database
			for (DedupParm dedupParm : dedupParmList) {
				List<CustomerDedup> list = getCustomerDedupDAO().fetchCustomerDedupDetails(customerDedup, dedupParm.getSQLQuery());

				for (int i = 0; i < list.size(); i++) {
					CustomerDedup custDedup = list.get(i);
					custDedup.setDedupRule(dedupParm.getQueryCode());
					custDedup.setFinReference(customerDedup.getFinReference());
					custDedup.setQueryField(getQueryFields(dedupParm.getSQLQuery(),fieldNameList));
					custDedup.setModule(FinanceConstants.DEDUP_FINANCE);
					customerDedupList.add(custDedup);
				}
			}            
		}
		logger.debug("Leaving");
		return customerDedupList;
	}

	@Override	
	public List<CustomerDedup> fetchCustomerDedupDetails(String userRole, CustomerDetails aCustomerDetails){
		DedupParm dedupParm = getApprovedDedupParmById(userRole, "Customer", aCustomerDetails.getCustomer().getCustCtgCode());
		if (dedupParm!=null ) {
			String replaceString="";
			if (StringUtils.trimToEmpty(dedupParm.getSQLQuery()).contains(PennantConstants.CUST_DEDUP_LIST_BUILD_EQUAL)) {
				replaceString=PennantConstants.CUST_DEDUP_LIST_BUILD_EQUAL;
			}else if ( StringUtils.trimToEmpty(dedupParm.getSQLQuery()).contains(PennantConstants.CUST_DEDUP_LIST_BUILD_LIKE)) {
				replaceString=PennantConstants.CUST_DEDUP_LIST_BUILD_LIKE;
			}
			if (StringUtils.isNotEmpty(replaceString)) {
				StringBuilder rule = new StringBuilder();
				//CustDocType = :CustDocType AND CustDocTitle = :CustDocTitle
				if (aCustomerDetails.getCustomerDocumentsList() != null
						&& aCustomerDetails.getCustomerDocumentsList().size() > 0) {
					for (CustomerDocument customerDocument : aCustomerDetails
							.getCustomerDocumentsList()) {
						if (StringUtils.isNotEmpty(rule.toString())) {
							rule.append("or");
						}
						rule.append("(" + PennantConstants.CUST_DEDUP_LISTFILED2 + " = '");
						rule.append(customerDocument.getCustDocType());
						rule.append("' AND " + PennantConstants.CUST_DEDUP_LISTFILED3 + " = '");
						rule.append(customerDocument.getCustDocTitle());
						rule.append("')");
					}
				}else{
					rule.append(PennantConstants.CUST_DEDUP_LISTFILED2 + " IN (");
				}
				dedupParm.setSQLQuery(dedupParm.getSQLQuery().replace(replaceString,rule.toString()));
			}
			logger.debug(dedupParm.getSQLQuery());

		}
		if(dedupParm!=null){
			return getDedupParmDAO().fetchCustomerDedupDetails(aCustomerDetails.getCustDedup() ,dedupParm.getSQLQuery());	
		}
		return new ArrayList<CustomerDedup>();
	}	

	/**
	 * Method for Fetching Dedup Finance List using Customer Dedup Details
	 */
	@Override	
	public List<FinanceDedup> fetchFinDedupDetails(String userRole, FinanceDedup aFinanceDedup,String curLoginUser,String  finType){

		DedupParm dedupParm=null;
		List<FinanceDedup> newFinDedupList = new ArrayList<FinanceDedup>();
		
		//Fetch List of Query Details Existing to check Finance De-dupe based on Finance Type & Stage
		FinanceReferenceDetail referenceDetail = new FinanceReferenceDetail();
		referenceDetail.setMandInputInStage(userRole+",");
		referenceDetail.setFinType(finType);
		List<FinanceReferenceDetail> queryCodeList = getDedupParmDAO().getQueryCodeList(referenceDetail, "_AFDView");

		if (queryCodeList == null || queryCodeList.isEmpty() ) {
			
			referenceDetail= null;
			queryCodeList = null;
			return newFinDedupList;
		}

		// Fetch Rule parameters for Rebuilding Data 
		List<FinanceDedup> excdFinDedupList =  new ArrayList<FinanceDedup>();
		List<String> fieldNameList = getDedupParmDAO().getRuleFieldNames(FinanceConstants.DEDUP_FINANCE);
		for (FinanceReferenceDetail queryCode : queryCodeList) {

			//First to fetch  Overridden/executed list 
			List<FinanceDedup> tempExcdFinDedupList = getFinanceDedupeDAO().fetchOverrideDedupData(aFinanceDedup.getFinReference() , queryCode.getLovDescNamelov());

			// Query Code From Query Builder
			dedupParm = getApprovedDedupParmById(queryCode.getLovDescNamelov(),	FinanceConstants.DEDUP_FINANCE, "L");

			if (dedupParm != null) {

				// Finance de-dupe New list with Query
				List<FinanceDedup> tempNewFinDedupList = getDedupParmDAO().fetchFinDedupDetails(aFinanceDedup,	dedupParm.getSQLQuery());
				
				if (!tempNewFinDedupList.isEmpty() && dedupParm.getSQLQuery() != null) {
					for (FinanceDedup newDedup : tempNewFinDedupList) {
						newDedup.setDedupeRule(","+dedupParm.getQueryCode()+",");
						newDedup.setFinReference(aFinanceDedup.getFinReference());
						newDedup.setRules(getQueryFields(dedupParm.getSQLQuery(),fieldNameList));
					}
					newFinDedupList.addAll(tempNewFinDedupList);
				}
			
			// Setting Rule Fields for HighLighting Colors
				if (tempExcdFinDedupList != null && dedupParm.getSQLQuery() != null) {
					for (FinanceDedup excdDedup : tempExcdFinDedupList) {
						excdDedup.setRules(getQueryFields(dedupParm.getSQLQuery(),fieldNameList));
						excdDedup.setDedupeRule(","+dedupParm.getQueryCode()+",");
						excdDedup.setOverrideUser(excdDedup.getOverrideUser());
					}
					excdFinDedupList.addAll(tempExcdFinDedupList);
				}
			}
		}

		//Checking for New Rule 
		doSetIsNewRecord(excdFinDedupList,newFinDedupList);
		excdFinDedupList = doSetFinDeDupGrouping(excdFinDedupList,newFinDedupList);
		dosetOverrideOrNot(excdFinDedupList,queryCodeList);
		dedupParm=null;
		queryCodeList=null;
		newFinDedupList= null;
		
		logger.debug("Leaving");
		return excdFinDedupList;
	}	
	/**
	 * To Check  weather Override checked or not in finance Processor Editor.
	 * @param FinanceDedup
	 * @param newFinDedupList
	 * @param queryCodeList 
	 * @return
	 */
	private List<FinanceDedup> dosetOverrideOrNot(List<FinanceDedup> newFinDedupList,List<FinanceReferenceDetail> queryCodeList) {
		logger.debug("Entering");

		//Check Override Condition based on Rule definitions on Process Editor
		HashMap<String, Boolean> queryOverrideMap = new HashMap<String, Boolean>();
		HashMap<String, String> overrideRuleDesc = new HashMap<String, String>();
		for (FinanceReferenceDetail referenceDetail : queryCodeList) {
			queryOverrideMap.put(referenceDetail.getLovDescNamelov(), referenceDetail.isOverRide());
		}
		//To check which rule not allow Override Condition based on Query Code Executions.
		HashMap<String, Boolean> matchedOverMap = new HashMap<String, Boolean>();
		for (FinanceDedup financeDedup : newFinDedupList) {
			String[] rulesList = financeDedup.getDedupeRule().split(",");
			for(int i = 0; i< rulesList.length; i++) {
				if(queryOverrideMap.containsKey(rulesList[i])){
					financeDedup.setOverride(queryOverrideMap.get(rulesList[i]));
					if(financeDedup.isOverride()) {
						financeDedup.setOverride(queryOverrideMap.get(rulesList[i]));
					} else {
						financeDedup.setOverride(false);
						matchedOverMap.put(rulesList[i],queryOverrideMap.get(rulesList[i]));
					}
				}
			}
			
			for (FinanceReferenceDetail referenceDetail : queryCodeList) {
				if(matchedOverMap.containsKey(referenceDetail.getLovDescNamelov())){
					overrideRuleDesc.put(referenceDetail.getLovDescNamelov(),referenceDetail.getLovDescRefDesc());
				}
			}
			financeDedup.setOverridenMap(overrideRuleDesc);
		}
		logger.debug("Leaving");
		return newFinDedupList;
	}
	/**
	 * This doSetIsNewrecord  is used to set new record or not
	 * if is new record insert otherwise updated.
	 */
	private void doSetIsNewRecord(List<FinanceDedup> excdFinDedupList,
			List<FinanceDedup> newFinDedupList) {

		if(excdFinDedupList.isEmpty() && !newFinDedupList.isEmpty()){
			for (FinanceDedup financeDedupNewList : newFinDedupList) {
				financeDedupNewList.setNewRecord(true);
			}

		}else if(!excdFinDedupList.isEmpty()&& newFinDedupList.isEmpty()){
			for (FinanceDedup fetchDedupOldList : excdFinDedupList) {
				fetchDedupOldList.setNewRecord(false);
			}
		}else{
			for (FinanceDedup oldDedup : excdFinDedupList) {
				for (FinanceDedup newdedup : newFinDedupList) {
					if(oldDedup.getDupReference().equals(newdedup.getDupReference())) {
						if (isRuleChanged(oldDedup.getDedupeRule(),newdedup.getDedupeRule()) ||
								isRuleChanged(oldDedup.getStage(),newdedup.getStage())) {
							newdedup.setNewRecord(false);
						}else {
							newdedup.setNewRecord(true);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Grouping  the FinanceDedup list  based on the Rule
	 * @param financeDedupList
	 * @return
	 */
	public List<FinanceDedup> doSetFinDeDupGrouping(List<FinanceDedup> excdDedupList, List<FinanceDedup> newDedupList) {
		
		List<FinanceDedup> groupFinDedupList =  new ArrayList<FinanceDedup>();
		
		groupFinDedupList.addAll(excdDedupList);
		groupFinDedupList.addAll(newDedupList);
		
		// For Easy Identification Prepare Map List for
		Map<String, FinanceDedup> excdDedupMap = new HashMap<String, FinanceDedup>();
		for (FinanceDedup financeDedup : groupFinDedupList) {
	        if(!excdDedupMap.containsKey(financeDedup.getDupReference())){
	        	excdDedupMap.put(financeDedup.getDupReference(), financeDedup);
	        }
        }
		
		// Do set Grouping New Dedup List with Existing Data 
		for (FinanceDedup newDedup : groupFinDedupList) {
	        if(excdDedupMap.containsKey(newDedup.getDupReference())){
	        	
	        	FinanceDedup excdDedup = excdDedupMap.get(newDedup.getDupReference());
	        	
	        	if(!excdDedup.getDedupeRule().contains(newDedup.getDedupeRule())) {
	        		excdDedup.setDedupeRule(excdDedup.getDedupeRule()+newDedup.getDedupeRule().substring(1));
	 		   }
	 		   if(!StringUtils.trimToEmpty(excdDedup.getStage()).contains(StringUtils.trimToEmpty(newDedup.getStage()))) {
	 			  excdDedup.setStage(excdDedup.getStage()+","+StringUtils.trimToEmpty(newDedup.getStage()));
	 		   }
	 		   
	 		  excdDedup.setRules(excdDedup.getRules()+","+newDedup.getRules());
	        }else{
	        	excdDedupMap.put(newDedup.getDupReference(), newDedup);
	        }
        }
		
		excdDedupList = new ArrayList<FinanceDedup>(excdDedupMap.values());
		excdDedupMap = null;
		
		return excdDedupList;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getDedupParmDAO().delete with parameters dedupParm,"" b) NEW Add
	 * new record in to main table by using getDedupParmDAO().save with
	 * parameters dedupParm,"" c) EDIT Update record in the main table by using
	 * getDedupParmDAO().update with parameters dedupParm,"" 3) Delete the
	 * record from the workFlow table by using getDedupParmDAO().delete with
	 * parameters dedupParm,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtDedupParams by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtDedupParams by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		DedupParm dedupParm = new DedupParm();
		BeanUtils.copyProperties((DedupParm) auditHeader.getAuditDetail()
				.getModelData(), dedupParm);

		if (dedupParm.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getDedupParmDAO().delete(dedupParm,"");

		} else {
			dedupParm.setRoleCode("");
			dedupParm.setNextRoleCode("");
			dedupParm.setTaskId("");
			dedupParm.setNextTaskId("");
			dedupParm.setWorkflowId(0);

			if (dedupParm.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType=PennantConstants.TRAN_ADD;
				dedupParm.setRecordType("");
				getDedupParmDAO().save(dedupParm,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				dedupParm.setRecordType("");
				getDedupParmDAO().update(dedupParm,"");
			}
		}

		getDedupParmDAO().delete(dedupParm,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(dedupParm);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getDedupParmDAO().delete with parameters
	 * dedupParm,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtDedupParams by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader,"doReject");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		DedupParm dedupParm = (DedupParm) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getDedupParmDAO().delete(dedupParm,"_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from
	 * the auditHeader. 2) fetch the details from the tables 3) Validate the
	 * Record based on the record details. 4) Validate for any business
	 * validation. 5) for any mismatch conditions Fetch the error details from
	 * getDedupParmDAO().getErrorDetail with Error ID and language as
	 * parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader,
			String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),
				auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");

		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());
		DedupParm dedupParm = (DedupParm) auditDetail.getModelData();

		DedupParm tempDedupParm = null;
		if (dedupParm.isWorkflow()) {
			tempDedupParm = getDedupParmDAO().getDedupParmByID(
					dedupParm.getQueryCode(),dedupParm.getQueryModule(),dedupParm.getQuerySubCode(), "_Temp");
		}
		DedupParm befDedupParm = getDedupParmDAO().getDedupParmByID(
				dedupParm.getQueryCode(),dedupParm.getQueryModule(),dedupParm.getQuerySubCode(), "");

		DedupParm oldDedupParm = dedupParm.getBefImage();

		String[] valueParm = new String[1];
		String[] errParm = new String[1];

		valueParm[0] = dedupParm.getQueryCode();
		errParm[0] = PennantJavaUtil.getLabel("label_QueryCode") + ":" + valueParm[0];

		if (dedupParm.isNew()) { // for New record or new record into work flow

			if (!dedupParm.isWorkflow()) {// With out Work flow only new records
				if (befDedupParm != null) { // Record Already Exists in the
					// table then error
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
				}
			} else { // with work flow
				if (dedupParm.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
					// is new
					if (befDedupParm != null || tempDedupParm != null) { // if records already exists in
						// the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befDedupParm == null || tempDedupParm != null) {
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!dedupParm.isWorkflow()) { // With out Work flow for update and
				// delete

				if (befDedupParm == null) { // if records not exists in the main
					// table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41002",errParm,null));
				} else {
					if (oldDedupParm != null
							&& !oldDedupParm.getLastMntOn().equals(
									befDedupParm.getLastMntOn())) {
						if (StringUtils.trimToEmpty(
								auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41003",errParm,null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}
			} else {

				if (tempDedupParm == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempDedupParm != null && oldDedupParm != null
						&& !oldDedupParm.getLastMntOn().equals(
								tempDedupParm.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !dedupParm.isWorkflow()) {
			dedupParm.setBefImage(befDedupParm);
		}

		return auditDetail;
	}

	/**
	 * Method for Fetch Black List Customer Details based on Rule conditions
	 */
	@Override
	public List<BlackListCustomers> fetchBlackListCustomers(String userRole,String finType,
			BlackListCustomers blCustData, String curUser) {
		logger.debug("Entering");

		List<BlackListCustomers> blackListCustomers = new ArrayList<BlackListCustomers>();
		List<FinBlacklistCustomer> overrideBlackList = new ArrayList<FinBlacklistCustomer>();
		boolean newUser = false;

		// Get QueryCode and override values from DB
		FinanceReferenceDetail financeRefDetail = new FinanceReferenceDetail();
		financeRefDetail.setMandInputInStage(userRole + ",");
		financeRefDetail.setFinType(finType);
		List<FinanceReferenceDetail> queryCodeList = getDedupParmDAO().getQueryCodeList(
				financeRefDetail,"_ABDView");

		if(queryCodeList != null) {

			List<DedupParm> dedupParmList = new ArrayList<DedupParm>();

			//Fetch Builded SQL Query based on Query Code  
			DedupParm dedupParm = null;
			for (FinanceReferenceDetail queryCode : queryCodeList) {

				//get override Blacklist Customers
				List<FinBlacklistCustomer> exeBlackList = getBlacklistCustomerDAO().fetchOverrideBlackListData(blCustData.getFinReference(), queryCode.getLovDescNamelov());
				dedupParm = getApprovedDedupParmById(queryCode.getLovDescNamelov(),	FinanceConstants.DEDUP_BLACKLIST, blCustData.getCustCtgCode());
				if(dedupParm != null){
					dedupParmList.add(dedupParm);
				}
				if(!exeBlackList.isEmpty()) {
					for(FinBlacklistCustomer blackListedCust:exeBlackList) {
						blackListedCust.setOverridenby(blackListedCust.getOverrideUser());
						overrideBlackList.add(blackListedCust);
					}
				}
				exeBlackList = null;
				dedupParm = null;
			}

			//Using Queries Fetch Black Listed Customer Data either from Interface or 
			//Existing Black Listed Table(Daily Download) Data
			if (!dedupParmList.isEmpty()) {
				blackListCustomers.addAll(getBlackListCustomer(blCustData, dedupParmList));
				if (!blackListCustomers.isEmpty()) {
					blackListCustomers = resetBlackListedCustData(blackListCustomers, queryCodeList);
				} else {
					return blackListCustomers;
				}
			} else {
				for (int i = 0; i < overrideBlackList.size(); i++) {
					if (!overrideBlackList.get(i).getOverrideUser().contains(curUser)) {
						overrideBlackList.get(i).setOverridenby(overrideBlackList.get(i).getOverrideUser());
						overrideBlackList.get(i).setOverrideUser(overrideBlackList.get(i).getOverrideUser() + "," + curUser);
						newUser = false;
					}
				}
			}
		} else {
			return blackListCustomers;
		}

		// Grouping Black List Customer which are having same result of Data
		blackListCustomers = doSetDeDupGrouping(blackListCustomers);

		// Checking for duplicate records in overrideBlacklistCustomers and currentBlacklistCustomers
		try {
			if (!overrideBlackList.isEmpty() && !blackListCustomers.isEmpty()) {
				for (FinBlacklistCustomer previousBlacklist : overrideBlackList) {
					for (BlackListCustomers currentBlacklist : blackListCustomers) {
						if (previousBlacklist.getFinReference().equals(currentBlacklist.getFinReference())) {
							if (previousBlacklist.getCustCIF().equals(currentBlacklist.getCustCIF())) {
								currentBlacklist.setOverridenby(previousBlacklist.getOverrideUser());

								if (previousBlacklist.getOverrideUser().contains(curUser)) {
									currentBlacklist.setOverrideUser(previousBlacklist.getOverrideUser());
									newUser = false;
								} else {
									currentBlacklist.setOverrideUser(previousBlacklist.getOverrideUser() + "," + curUser);
								}

								//Checking for New Rule
								if (isRuleChanged(previousBlacklist.getWatchListRule(), currentBlacklist.getWatchListRule())) {
									currentBlacklist.setNewRule(true);
									if (previousBlacklist.getCustCIF().equals(currentBlacklist.getCustCIF())) {
										currentBlacklist.setNewBlacklistRecord(false);
									} else {
										currentBlacklist.setNewBlacklistRecord(true);
										currentBlacklist.setOverride(false);
									}
								} else {
									currentBlacklist.setNewBlacklistRecord(false);
								}

								if (newUser) {
									currentBlacklist.setOverride(previousBlacklist.isOverride());
								}
							}
						}
					}
				}
			} else if (!overrideBlackList.isEmpty() && blackListCustomers.isEmpty()) {
				blackListCustomers = doSetFinBlacklistCustomers(overrideBlackList);
			}
		} catch(Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return blackListCustomers;
	}

	/**
	 * Checking for Rule weather it is added or removed
	 * @param overrideListRule
	 * @param newListRule
	 * @return
	 */
	private boolean isRuleChanged(String overrideListRule, String newListRule) {
		logger.debug("Entering");

		String[] exeRuleList = StringUtils.trimToEmpty(overrideListRule).split(",");
		String[] newRuleList = StringUtils.trimToEmpty(newListRule).split(",");
		if (exeRuleList.length != newRuleList.length) {
			return true;
		} else {
			for (String newRule : newRuleList) {
				if (!Arrays.toString(exeRuleList).contains(newRule)) {
					return true;
				}
			}
		}
		logger.debug("Leaving");
		
		return false;
	}

	/**
	 * Method for Preparation of BlackList Customers Data from Already overridden Data in case of rules Deletion from Process editor
	 * @param overrideBlackList
	 * @return
	 */
	private List<BlackListCustomers> doSetFinBlacklistCustomers(List<FinBlacklistCustomer> overrideBlackList) {
		logger.debug("Entering");
		List<BlackListCustomers> list = new ArrayList<BlackListCustomers>();
		for(FinBlacklistCustomer finBlacklist: overrideBlackList) {
			BlackListCustomers blacklistCustomer = new BlackListCustomers();			
			blacklistCustomer.setCustCIF(finBlacklist.getCustCIF());
			blacklistCustomer.setFinReference(finBlacklist.getFinReference());
			blacklistCustomer.setCustFName(finBlacklist.getCustFName());
			blacklistCustomer.setCustLName(finBlacklist.getCustLName());
			blacklistCustomer.setCustShrtName(finBlacklist.getCustShrtName());
			blacklistCustomer.setCustDOB(finBlacklist.getCustDOB());
			blacklistCustomer.setCustCRCPR(finBlacklist.getCustCRCPR());
			blacklistCustomer.setCustPassportNo(finBlacklist.getCustPassportNo());
			blacklistCustomer.setCustNationality(finBlacklist.getCustNationality());
			blacklistCustomer.setEmployer(finBlacklist.getEmployer());
			blacklistCustomer.setWatchListRule(finBlacklist.getWatchListRule());
			blacklistCustomer.setOverride(finBlacklist.isOverride());
			blacklistCustomer.setOverrideUser(finBlacklist.getOverrideUser());
			blacklistCustomer.setMobileNumber(finBlacklist.getMobileNumber());
			blacklistCustomer.setNewBlacklistRecord(finBlacklist.isNewBlacklistRecord());
			list.add(blacklistCustomer);
		}
		logger.debug("Leaving");

		return list;
	}


	private List<BlackListCustomers> getBlackListCustomer(BlackListCustomers blCustData, List<DedupParm> dedupParmList) {
		logger.debug("Entering");

		List<BlackListCustomers> blackListCustomerList = new ArrayList<BlackListCustomers>();

		if(dedupParmList != null && !dedupParmList.isEmpty()){

			List<String> fieldNameList = getDedupParmDAO().getRuleFieldNames(blCustData.getCustCtgCode()+FinanceConstants.DEDUP_BLACKLIST);

			for (DedupParm dedupParm : dedupParmList) {
				List<BlackListCustomers> list = getBlacklistCustomerDAO().fetchBlackListedCustomers(blCustData, dedupParm.getSQLQuery());

				for (int i = 0; i < list.size(); i++) {
					BlackListCustomers blkList = list.get(i);
					blkList.setWatchListRule(dedupParm.getQueryCode());
					blkList.setFinReference(blCustData.getFinReference());
					blkList.setQueryField(getQueryFields(dedupParm.getSQLQuery(),fieldNameList));
					blackListCustomerList.add(blkList);
				}
			}
		}

		logger.debug("Leaving");
		return blackListCustomerList;
	}

	/**
	 * getQuery fields from Sql Query
	 * @param sqlQuery
	 * @param fieldNameList
	 * @return
	 */
	private String getQueryFields(String sqlQuery, List<String> fieldNameList) {
		logger.debug("Entering");
		
		String ruleSplitRegex = "[^a-zA-Z0-9_]+";
		
		if(StringUtils.isBlank(sqlQuery) || fieldNameList == null) {
			return "";
		}
		String queryFieldArray[] = sqlQuery.split(ruleSplitRegex);
		String value = "";
		String ruleString = "";
		try {
			for (int i = 0; i < queryFieldArray.length; i++) {
				for (String dbRuleField : fieldNameList) {
					if (queryFieldArray[i].equalsIgnoreCase(dbRuleField)) {
						value = value + queryFieldArray[i].trim() + PennantConstants.DELIMITER_COMMA;
					}
				}
			}
			String ruleCode[] = value.split(PennantConstants.DELIMITER_COMMA);
			for (String finalValue : ruleCode) {
				if (!ruleString.contains(finalValue)) {
					ruleString = ruleString + finalValue + PennantConstants.DELIMITER_COMMA;
				}
			}
			if (StringUtils.isNotEmpty(ruleString)) {
				return ruleString.substring(0, ruleString.length() - 1);
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return "";
	}

	/**
	 * Group the BlackList Customers based on the Rule
	 * @param blackListCustomerList
	 * @return
	 */
	private List<BlackListCustomers> doSetDeDupGrouping(List<BlackListCustomers> blackListCustomerList) {
		logger.debug("Entering");

		try {
			for (int i = 0; i < blackListCustomerList.size(); i++) {
				BlackListCustomers iBlackList = blackListCustomerList.get(i);
				for (int j = i + 1; j <= blackListCustomerList.size() - 1; j++) {
					BlackListCustomers jBlackList = blackListCustomerList.get(j);
					if (iBlackList.getCustCIF().equals(jBlackList.getCustCIF())) {
						if (!iBlackList.getWatchListRule().contains(jBlackList.getWatchListRule())) {
							iBlackList.setWatchListRule(iBlackList.getWatchListRule() + ","
									+ jBlackList.getWatchListRule());
						}
						iBlackList.setQueryField(iBlackList.getQueryField() + ","
								+ jBlackList.getQueryField());
						if(!jBlackList.isOverride()) {
							iBlackList.setOverride(jBlackList.isOverride());
						}
						blackListCustomerList.remove(j);
						j--;
					}
				}
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return blackListCustomerList;
	}
	/**
	 * Method for Resetting Override condition data
	 * @param blackListCustomers
	 * @param queryCodeList
	 * @param dedupParmList 
	 * @return
	 */
	private List<BlackListCustomers> resetBlackListedCustData(List<BlackListCustomers> blackListCustomers,List<FinanceReferenceDetail> queryCodeList) {
		logger.debug("Entering");

		//Check Override Condition based on Rule definitions on Process Editor
		HashMap<String, Boolean> queryOverrideMap = new HashMap<String, Boolean>();
		for (FinanceReferenceDetail referenceDetail : queryCodeList) {
			queryOverrideMap.put(referenceDetail.getLovDescNamelov(), referenceDetail.isOverRide());			
		}

		//Reset Override COndition based on Query Code Executions
		for (BlackListCustomers blackListCust : blackListCustomers) {
			String[] watchList = blackListCust.getWatchListRule().split(",");
			for(int i = 0; i< watchList.length; i++) {
				if(queryOverrideMap.containsKey(watchList[i])){
					blackListCust.setOverride(queryOverrideMap.get(watchList[i]));
					if(blackListCust.isOverride()) {
						blackListCust.setOverride(queryOverrideMap.get(watchList[i]));
					} else {
						blackListCust.setOverride(false);
						break;
					}
				}
			}
			if(queryOverrideMap.containsKey(blackListCust.getWatchListRule())){
				blackListCust.setOverride(queryOverrideMap.get(blackListCust.getWatchListRule()));
			}
		}
		logger.debug("Leaving");
		return blackListCustomers;
	}
	@Override
	public List<FinanceReferenceDetail> getQueryCodeList(FinanceReferenceDetail financeRefDetail, String tableType) {
		return getDedupParmDAO().getQueryCodeList(financeRefDetail, tableType);
	}

	/**
	 * Method for Fetching Police Case Details on Creation of Finance
	 */
	@Override
	public List<PoliceCaseDetail> fetchPoliceCaseCustomers(String userRole, String finType,
			PoliceCaseDetail policeCaseData,String curUser) {
		logger.debug("Entering");


		List<PoliceCaseDetail> policeCase = new ArrayList<PoliceCaseDetail>();
		List<PoliceCase> exePoliceCase = new ArrayList<PoliceCase>();
		List<PoliceCase> overridePoliceCaseList = new ArrayList<PoliceCase>();
		boolean newUser = false;

		FinanceReferenceDetail financeRefDetail = new FinanceReferenceDetail();
		financeRefDetail.setMandInputInStage(userRole + ",");
		financeRefDetail.setFinType(finType);
		List<FinanceReferenceDetail> queryCodeList = getDedupParmDAO().getQueryCodeList(financeRefDetail,"_APCView");

		if(queryCodeList !=null) {
			List<DedupParm> dedupParmList = new ArrayList<DedupParm>();
			DedupParm dedupParm = null;
			for (FinanceReferenceDetail queryCode : queryCodeList) {
				exePoliceCase = getPoliceCaseDAO().fetchPoliceCase(policeCaseData.getFinReference(), queryCode.getLovDescNamelov());
				dedupParm = getApprovedDedupParmById(queryCode.getLovDescNamelov(),FinanceConstants.DEDUP_POLICE, policeCaseData.getCustCtgCode());
				if(dedupParm!=null){
					dedupParmList.add(dedupParm);
				}if(!exePoliceCase.isEmpty()){
					for(PoliceCase policecheck:exePoliceCase){
						policecheck.setOverridenby(policecheck.getOverrideUser());
						overridePoliceCaseList.add(policecheck);
					}
					overridePoliceCaseList.addAll(exePoliceCase);
				}
				exePoliceCase = null;
				dedupParm = null;
			}

			//Using Queries Fetch PoliceCase Listed Customer Data either from Interface or 
			//Existing PoliceCase Listed Table(Daily Download) Data
			if (!dedupParmList.isEmpty()) {
				policeCase.addAll(getPoliceCaseListCustomer(policeCaseData, dedupParmList));
				if (!policeCase.isEmpty()) {
					policeCase = resetPoliceCaseList(policeCase, queryCodeList);
				} else {
					return policeCase;
				}
			}else{
				for (int i = 0; i < overridePoliceCaseList.size(); i++) {
					if (!overridePoliceCaseList.get(i).getOverrideUser().contains(curUser)) {
						overridePoliceCaseList.get(i).setOverridenby(overridePoliceCaseList.get(i).getOverrideUser());
						overridePoliceCaseList.get(i).setOverrideUser(overridePoliceCaseList.get(i).getOverrideUser() + "," + curUser);
						newUser = false;
					}
				}
			}

		} else {
			return policeCase;
		}

		// Grouping PoliceCase List Customer which are having same result of Data
		policeCase = dosetPoliceCaseGrouping(policeCase);
		// Checking for duplicate records in overridePoliceCaseCustomers and currentPoliceCaseCustomers

		try{
			if (!overridePoliceCaseList.isEmpty() && !policeCase.isEmpty()) {

				for (int i = 0; i < overridePoliceCaseList.size(); i++) {
					for (int j = 0; j < policeCase.size(); j++) {
						if (overridePoliceCaseList.get(i).getFinReference().equals(policeCase.get(j).getFinReference())) {
							if (overridePoliceCaseList.get(i).getCustCIF().equals(policeCase.get(j).getCustCIF())) {
								policeCase.get(j).setOverridenby(overridePoliceCaseList.get(i).getOverrideUser());
								if(overridePoliceCaseList.get(i).getOverrideUser().contains(curUser)){
									policeCase.get(j).setOverrideUser(overridePoliceCaseList.get(i).getOverrideUser());
									newUser = false;
								}else{
									policeCase.get(j).setOverrideUser(overridePoliceCaseList.get(i).getOverrideUser() + "," + curUser);
								}

								if(isRuleChanged(overridePoliceCaseList.get(i).getPoliceCaseRule(), policeCase.get(j).getPoliceCaseRule())){
									policeCase.get(j).setNewRule(true);
									if(overridePoliceCaseList.get(i).getCustCIF().equals(policeCase.get(j).getCustCIF())){
										policeCase.get(j).setNewPolicecaseRecord(false);
									}else{
										policeCase.get(j).setNewPolicecaseRecord(true);
										policeCase.get(j).setOverride(false);
									}
								}else{
									policeCase.get(j).setNewPolicecaseRecord(false);
								}if(newUser){
									policeCase.get(j).setOverride(overridePoliceCaseList.get(i).isOverride());
								}
							}
						}
					}
				}
			}else if (!overridePoliceCaseList.isEmpty() && policeCase.isEmpty()) {
				policeCase = doSetFinPoliceCaseCustomers(overridePoliceCaseList);
			}
		} catch(Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return policeCase;
	}
	/**
	 * Method for Preparation of PoliceCase Customers Data from Already overridden Data in case of rules Deletion from Process editor
	 * @param overridePoliceCaseList
	 * @return
	 */
	private List<PoliceCaseDetail> doSetFinPoliceCaseCustomers(List<PoliceCase> overridePoliceCaseList) {
		logger.debug("Entering");
		List<PoliceCaseDetail> list = new ArrayList<PoliceCaseDetail>();
		for(PoliceCase finPoliceCaselist: overridePoliceCaseList) {
			PoliceCaseDetail policecaseCustomerList = new PoliceCaseDetail();			
			policecaseCustomerList.setCustCIF(finPoliceCaselist.getCustCIF());
			policecaseCustomerList.setFinReference(finPoliceCaselist.getFinReference());
			policecaseCustomerList.setCustFName(finPoliceCaselist.getCustFName());
			policecaseCustomerList.setCustLName(finPoliceCaselist.getCustLName());
			policecaseCustomerList.setCustDOB(finPoliceCaselist.getCustDOB());
			policecaseCustomerList.setCustCRCPR(finPoliceCaselist.getCustCRCPR());
			policecaseCustomerList.setCustPassportNo(finPoliceCaselist.getCustPassportNo());
			policecaseCustomerList.setCustNationality(finPoliceCaselist.getCustNationality());
			policecaseCustomerList.setPoliceCaseRule(finPoliceCaselist.getPoliceCaseRule());
			policecaseCustomerList.setOverride(finPoliceCaselist.isOverride());
			policecaseCustomerList.setOverrideUser(finPoliceCaselist.getOverrideUser());
			policecaseCustomerList.setMobileNumber(finPoliceCaselist.getMobileNumber());
			policecaseCustomerList.setNewPolicecaseRecord(finPoliceCaselist.isNewPolicecaseRecord());
			policecaseCustomerList.setRules(finPoliceCaselist.getRules());
			list.add(policecaseCustomerList);
		}
		logger.debug("Leaving");

		return list;
	}

	private List<PoliceCaseDetail> getPoliceCaseListCustomer(PoliceCaseDetail policeCaseData, List<DedupParm> dedupParmList) {
		logger.debug("Entering");
		List<PoliceCaseDetail> policeCaseCustomerList = new ArrayList<PoliceCaseDetail>();

		if(dedupParmList != null && !dedupParmList.isEmpty()){
			
			List<String> fieldNameList = getDedupParmDAO().getRuleFieldNames(policeCaseData.getCustCtgCode()+FinanceConstants.DEDUP_POLICE);

			for (DedupParm dedupParm : dedupParmList) {
				List<PoliceCaseDetail> list = getPoliceCaseDAO().fetchCorePolice(policeCaseData ,dedupParm.getSQLQuery());

				for (int i = 0; i < list.size(); i++) {
					PoliceCaseDetail policeCaseList = list.get(i);
					policeCaseList.setPoliceCaseRule(dedupParm.getQueryCode());
					policeCaseList.setFinReference(policeCaseData.getFinReference());
					policeCaseList.setRules(getQueryFields(dedupParm.getSQLQuery(),fieldNameList));
					policeCaseCustomerList.add(policeCaseList);
				}
			}
		}
		logger.debug("Leaving");
		return policeCaseCustomerList;
	}

	private List<PoliceCaseDetail> dosetPoliceCaseGrouping(List<PoliceCaseDetail> policeCaseList) {
		List<PoliceCaseDetail> policecase = new ArrayList<PoliceCaseDetail>();
		policecase.addAll(policeCaseList);

		for (int i = 0; i < policecase.size() - 1; i++) {
			for (int j = i + 1; j < policecase.size(); j++) {
				if (policecase.get(i).getCustCIF().equals(policecase.get(j).getCustCIF())) {
					if(policecase.get(i).getPoliceCaseRule().contains(policecase.get(j).getPoliceCaseRule())){
						policecase.get(i).setPoliceCaseRule(policecase.get(i).getPoliceCaseRule());
					}else{
						policecase.get(i).setPoliceCaseRule(policecase.get(i).getPoliceCaseRule()+','+policecase.get(j).getPoliceCaseRule());
						if(policecase.get(i).isNewRule()!=policecase.get(j).isNewRule()){
							policecase.get(i).setNewRule(true);
							policecase.get(i).setOverride(false);
						}
					}
					policecase.get(i).setRules(policecase.get(i).getRules()+','+policecase.get(j).getRules());
					policecase.remove(j);
					j--;

				}
			}
		}
		return policecase;

	}
	/**
	 * Method for Resetting Override condition based on Process Editor Configuration
	 * @param policeCase
	 * @param queryCodeList
	 * @return
	 */
	private List<PoliceCaseDetail> resetPoliceCaseList(List<PoliceCaseDetail> policeCase,
			List<FinanceReferenceDetail> queryCodeList) {
		HashMap<String, Boolean> queryOverrideMap = new HashMap<String, Boolean>();
		for (FinanceReferenceDetail referenceDetail : queryCodeList) {
			queryOverrideMap.put(referenceDetail.getLovDescNamelov(), referenceDetail.isOverRide());			
		}

		//Reset Override COndition based on Query Code Executions
		for (PoliceCaseDetail policeCaseList : policeCase) {
			String[] policecaserule = policeCaseList.getPoliceCaseRule().split(",");
			for(int i=0;i<policecaserule.length;i++){
				if(queryOverrideMap.containsKey(policecaserule[i])){
					policeCaseList.setOverride(queryOverrideMap.get(policecaserule[i]));
					if(policeCaseList.isOverride()) {
						policeCaseList.setOverride(queryOverrideMap.get(policecaserule[i]));
					} else {
						policeCaseList.setOverride(false);
						break;
					}
				}
			}
			if(queryOverrideMap.containsKey(policeCaseList.getPoliceCaseRule())){
				policeCaseList.setOverride(queryOverrideMap.get(policeCaseList.getPoliceCaseRule()));
			}

		}
		logger.debug("Leaving");
		return policeCase;
	}
	
	
	@Override
	public List<CustomerDedup> getDedupCustomerDetails(CustomerDetails details) {
		DedupCustomerDetail dedupCustomerDetail = preparededupRequest(details);
		DedupCustomerResponse response = new DedupCustomerResponse();
		try {
			response = customerDedupService.invokeDedup(dedupCustomerDetail);
		} catch (Exception e) {
			logger.error(e);
		}

		List<CustomerDedup> customerDedup = getDedupData(response,details);
		return customerDedup;
	}
	
	private List<CustomerDedup> getDedupData(DedupCustomerResponse response,CustomerDetails details) {
		List<CustomerDedup> custDedupList = new ArrayList<CustomerDedup>();
		if(response != null && response.getDedupCustomerDetails() != null) {
			for(DedupCustomerDetail dedupDetail:response.getDedupCustomerDetails()) {
				CustomerDedup customerDedup = new CustomerDedup();
				customerDedup.setCustCIF(details.getCustomer().getCustCIF());
				customerDedup.setCustShrtName(dedupDetail.getCustomer().getCustShrtName());
				customerDedup.setCustDOB(dedupDetail.getCustomer().getCustDOB());
				customerDedup.setCustFName(dedupDetail.getCustomer().getCustFName());
				customerDedup.setCustCRCPR(dedupDetail.getCustomer().getCustCRCPR());
				customerDedup.setCustCoreBank(dedupDetail.getCustomer().getCustCoreBank());
				customerDedup.setSourceSystem(dedupDetail.getCustomer().getSourceSystem());
				customerDedup.setSourceSystem(dedupDetail.getCustomer().getSourceSystem());
				customerDedup.setCustCoreBank(dedupDetail.getCustomer().getCustCoreBank());
				
				for (CustomerPhoneNumber phonenumber : dedupDetail.getCustomerPhoneNumList()) {
					if(StringUtils.equals(phonenumber.getPhoneTypeCode(), "MOBILE")){
						customerDedup.setPhoneNumber(phonenumber.getPhoneNumber());
						break;
					}
				}
				for (CustomerAddres addresstype : dedupDetail.getAddressList()) {
					if(StringUtils.equals(addresstype.getCustAddrType(), "OFFICE")){
						customerDedup.setAddress(StringUtils.trimToEmpty(addresstype.getCustAddrLine1() +","
								+ addresstype.getCustAddrCity() +","+ addresstype.getCustAddrZIP()));
						break;
					}
					if(StringUtils.equals(addresstype.getCustAddrType(), "HOME")){
						customerDedup.setAddress(StringUtils.trimToEmpty(addresstype.getCustAddrLine1() +","
								+ addresstype.getCustAddrCity() +","+ addresstype.getCustAddrZIP()));
						break;
					}
				}
				
				custDedupList.add(customerDedup);
				// Add appScore
				//if(StringUtils.equals(response.getC, str2))
			}
		}
		return custDedupList;
	}
	
	private DedupCustomerDetail preparededupRequest(CustomerDetails customerDetails) {

		DedupCustomerDetail dedupCustomerDetail = new DedupCustomerDetail();
		Customer customer = customerDetails.getCustomer();
		if (customerDetails != null && customer != null) {
			dedupCustomerDetail.setFinReference("");
			dedupCustomerDetail.setCustID(customer.getCustID());
			dedupCustomerDetail.setCustCIF(customer.getCustCIF());

			dedupCustomerDetail.setCustomer(customer);
			//dedupCustomerDetail.setFinType(financeDetail.getFinScheduleData().getFinanceMain().getFinType());
			// customer documents
			if (customerDetails.getCustomerDocumentsList() != null) {
				dedupCustomerDetail.setCustomerDocumentsList(customerDetails.getCustomerDocumentsList());
			}
			// customer Address
			if (customerDetails.getAddressList() != null) {
				dedupCustomerDetail.setAddressList(customerDetails.getAddressList());
			}
			// customer phone numbers
			if (customerDetails.getCustomerPhoneNumList() != null) {
				dedupCustomerDetail.setCustomerPhoneNumList(customerDetails.getCustomerPhoneNumList());
			}
			// customer emails
			if (customerDetails.getCustomerEMailList() != null) {
				dedupCustomerDetail.setCustomerEMailList(customerDetails.getCustomerEMailList());
			}
		}

		return dedupCustomerDetail;
	}

	@Override
	public List<DedupParm> getDedupParmByModule(String queryModule, String querySubCode, String type) {
		return	   getDedupParmDAO().getDedupParmByModule(queryModule, querySubCode, type);
	}
}