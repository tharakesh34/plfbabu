/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
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
import java.util.stream.Collectors;

import javax.ws.rs.ProcessingException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgresql.util.PGobject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;

import com.pennant.Interface.service.CustomerInterfaceService;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.BlackListCustomerDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.custdedup.CustomerDedupDAO;
import com.pennant.backend.dao.dedup.DedupParmDAO;
import com.pennant.backend.dao.findedup.FinanceDedupeDAO;
import com.pennant.backend.dao.masters.MasterDefDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.model.blacklist.FinBlacklistCustomer;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.dedup.DedupParm;
import com.pennant.backend.model.finance.FinanceDedup;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.model.DedupCustomerDetail;
import com.pennanttech.model.DedupCustomerResponse;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.CustomerDedupService;

/**
 * Service implementation for methods that depends on <b>DedupParm</b>.<br>
 * 
 */
public class DedupParmServiceImpl extends GenericService<DedupParm> implements DedupParmService {
	private static final Logger logger = LogManager.getLogger(DedupParmServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private DedupParmDAO dedupParmDAO;
	private BlackListCustomerDAO blacklistCustomerDAO;
	private FinanceDedupeDAO financeDedupeDAO;
	private CustomerInterfaceService customerInterfaceService;
	private CustomerDedupDAO customerDedupDAO;
	private static MasterDefDAO masterDefDAO;

	@Autowired(required = false)
	private CustomerDedupService customerDedupService;

	public DedupParmServiceImpl() {
		super();
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}
		String tableType = "";
		DedupParm dedupParm = (DedupParm) auditHeader.getAuditDetail().getModelData();

		if (dedupParm.isWorkflow()) {
			tableType = "_Temp";
		}

		if (dedupParm.isNew()) {
			dedupParmDAO.save(dedupParm, tableType);
		} else {
			dedupParmDAO.update(dedupParm, tableType);
		}

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		DedupParm dedupParm = (DedupParm) auditHeader.getAuditDetail().getModelData();
		dedupParmDAO.delete(dedupParm, "");

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public DedupParm getDedupParmById(String id, String queryModule, String querySubCode) {
		return dedupParmDAO.getDedupParmByID(id, queryModule, querySubCode, "_View");
	}

	@Override
	public DedupParm getApprovedDedupParmById(String id, String queryModule, String querySubCode) {
		return dedupParmDAO.getDedupParmByID(id, queryModule, querySubCode, "");
	}

	@Override
	public List<CustomerDedup> fetchCustomerDedupDetails(String userRole, CustomerDedup cd, String curLoginUser,
			String finType) throws InterfaceException {

		List<FinanceReferenceDetail> queryCodeList = new ArrayList<>();
		FinanceReferenceDetail rd = new FinanceReferenceDetail();

		if (StringUtils.isNotEmpty(finType)) {
			rd.setMandInputInStage(userRole + ",");
			rd.setFinType(finType);
			queryCodeList = getQueryCodeList(rd, "_ACDView");
		}

		if (StringUtils.isNotEmpty(finType) && CollectionUtils.isEmpty(queryCodeList)) {
			return new ArrayList<>();
		}

		List<DedupParm> dedupParmList = new ArrayList<>();
		List<CustomerDedup> overridedCustDedupList = new ArrayList<>();

		List<CustomerDedup> customerDedupList = new ArrayList<>();
		List<DedupParm> list = getDedupParmByModule(FinanceConstants.DEDUP_CUSTOMER, cd.getCustCtgCode(), "");

		if (StringUtils.isNotEmpty(finType) && CollectionUtils.isNotEmpty(queryCodeList)
				&& CollectionUtils.isNotEmpty(list)) {
			for (FinanceReferenceDetail frd : queryCodeList) {
				// to get previously overridden data
				String lovName = frd.getLovDescNamelov();

				List<CustomerDedup> cdList = customerDedupDAO.fetchOverrideCustDedupData(cd.getCustCIF(), lovName,
						FinanceConstants.DEDUP_CUSTOMER);

				cdList.forEach(l1 -> l1.setOverridenby(l1.getOverrideUser()));
				overridedCustDedupList.addAll(cdList);

				dedupParmList.addAll(
						list.stream().filter(l1 -> l1.getQueryCode().equals(lovName)).collect(Collectors.toList()));
			}
			// to get the de dup details based on the de dup parameters i.e query's list from both application and core
			// banking
			customerDedupList.addAll(getCustomerDedup(cd, dedupParmList));
		} else {
			if (CollectionUtils.isNotEmpty(list)) {
				for (DedupParm dedupParm : list) {
					// to get previously overridden data
					List<CustomerDedup> custDedupList = customerDedupDAO.fetchOverrideCustDedupData(cd.getCustCIF(),
							dedupParm.getQueryCode(), FinanceConstants.DEDUP_CUSTOMER);

					custDedupList.forEach(l1 -> l1.setOverridenby(l1.getOverrideUser()));
					overridedCustDedupList.addAll(custDedupList);
				}
				// to get the de dup details based on the de dup parameters i.e query's list from both application and
				// core banking
				customerDedupList.addAll(getCustomerDedup(cd, list));
			}
		}
		customerDedupList = doSetCustomerDeDupGrouping(customerDedupList);

		boolean newUser = false;
		// Checking for duplicate records in overrideBlacklistCustomers and currentBlacklistCustomers
		try {
			if (CollectionUtils.isNotEmpty(overridedCustDedupList) && CollectionUtils.isNotEmpty(customerDedupList)) {

				for (CustomerDedup previousDedup : overridedCustDedupList) {
					for (CustomerDedup currentDedup : customerDedupList) {
						if (previousDedup.getCustCIF().equals(currentDedup.getCustCIF())) {
							String overrideUser = previousDedup.getOverrideUser();
							currentDedup.setOverridenby(overrideUser);

							if (overrideUser.contains(curLoginUser)) {
								currentDedup.setOverrideUser(overrideUser);
								newUser = false;
							} else {
								currentDedup.setOverrideUser(
										overrideUser + PennantConstants.DELIMITER_COMMA + curLoginUser);
								newUser = true;
							}
							// Checking for New Rule
							if (isRuleChanged(previousDedup.getDedupRule(), currentDedup.getDedupRule())) {
								currentDedup.setNewRule(true);
								if (previousDedup.getCustCIF().equals(currentDedup.getCustCIF())) {
									currentDedup.setNewCustDedupRecord(false);
								} else {
									currentDedup.setNewCustDedupRecord(true);
									currentDedup.setOverride(false);
								}
							} else {
								currentDedup.setNewCustDedupRecord(false);
							}

							if (newUser) {
								currentDedup.setOverride(previousDedup.isOverride());
							}
						}
					}
				}
			} else if (!overridedCustDedupList.isEmpty() && customerDedupList.isEmpty()) {
				customerDedupList.addAll(overridedCustDedupList);
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return customerDedupList;
	}

	private List<CustomerDedup> resetDedupCustData(List<CustomerDedup> customerDedupList,
			List<FinanceReferenceDetail> queryCodeList) {
		Map<String, Boolean> queryOverrideMap = new HashMap<>();

		queryCodeList.forEach(l1 -> queryOverrideMap.put(l1.getLovDescNamelov(), l1.isOverRide()));

		for (CustomerDedup custDedup : customerDedupList) {
			if (custDedup.getDedupRule() == null) {
				continue;
			}

			String[] dedupRuleList = custDedup.getDedupRule().split(",");

			for (String dedupRule : dedupRuleList) {
				if (queryOverrideMap.containsKey(dedupRule)) {
					Boolean override = queryOverrideMap.get(dedupRule);
					custDedup.setOverride(override);
					if (custDedup.isOverride()) {
						custDedup.setOverride(override);
					} else {
						custDedup.setOverride(false);
						break;
					}
				}
			}

			if (queryOverrideMap.containsKey(custDedup.getDedupRule())) {
				custDedup.setOverride(queryOverrideMap.get(custDedup.getDedupRule()));
			}

		}
		return customerDedupList;
	}

	private List<CustomerDedup> doSetCustomerDeDupGrouping(List<CustomerDedup> customerDedupList) {
		try {
			for (int i = 0; i < customerDedupList.size(); i++) {
				CustomerDedup icustDedupList = customerDedupList.get(i);
				for (int j = i + 1; j <= customerDedupList.size() - 1; j++) {
					CustomerDedup jcustDedupList = customerDedupList.get(j);
					if (icustDedupList.getCustCIF().equals(jcustDedupList.getCustCIF())) {
						if (!icustDedupList.getDedupRule().contains(jcustDedupList.getDedupRule())) {
							icustDedupList
									.setDedupRule(icustDedupList.getDedupRule() + "," + jcustDedupList.getDedupRule());
						}
						icustDedupList
								.setQueryField(icustDedupList.getQueryField() + "," + jcustDedupList.getQueryField());
						if (!jcustDedupList.isOverride()) {
							icustDedupList.setOverride(jcustDedupList.isOverride());
						}
						customerDedupList.remove(j);
						j--;
					}
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return customerDedupList;
	}

	@Override
	public List<CustomerDedup> getCustomerDedup(CustomerDedup cd, List<DedupParm> dedupParmList)
			throws InterfaceException {
		logger.debug(Literal.ENTERING);

		if (CollectionUtils.isEmpty(dedupParmList)) {
			return new ArrayList<>();
		}

		List<CustomerDedup> customerDedupList = new ArrayList<>();

		List<String> fieldNames = dedupParmDAO.getRuleFieldNames(cd.getCustCtgCode() + FinanceConstants.DEDUP_CUSTOMER);

		// To Check duplicate customer in core banking
		customerDedupList.addAll(customerInterfaceService.fetchCustomerDedupDetails(cd));

		// TO Check duplicate customer in Local database
		for (DedupParm dedupParm : dedupParmList) {
			String sqlQuery = dedupParm.getSQLQuery();
			List<CustomerDedup> list = customerDedupDAO.fetchCustomerDedupDetails(cd, sqlQuery);

			String queryCode = dedupParm.getQueryCode();
			String finReference = cd.getFinReference();

			for (CustomerDedup custDedup : list) {
				custDedup.setDedupRule(queryCode);
				custDedup.setFinReference(finReference);
				custDedup.setQueryField(getQueryFields(sqlQuery, fieldNames));
				custDedup.setModule(FinanceConstants.DEDUP_FINANCE);
				customerDedupList.add(custDedup);
			}
		}
		logger.debug(Literal.LEAVING);
		return customerDedupList;
	}

	@Override
	public List<CustomerDedup> fetchCustomerDedupDetails(String userRole, CustomerDetails aCustomerDetails) {
		Customer customer = aCustomerDetails.getCustomer();
		DedupParm dedupParm = getApprovedDedupParmById(userRole, "Customer", customer.getCustCtgCode());

		if (dedupParm == null) {
			return new ArrayList<>();
		}
		String replaceString = "";

		String sqlQuery = StringUtils.trimToEmpty(dedupParm.getSQLQuery());
		if (sqlQuery.contains(PennantConstants.CUST_DEDUP_LIST_BUILD_EQUAL)) {
			replaceString = PennantConstants.CUST_DEDUP_LIST_BUILD_EQUAL;
		} else if (sqlQuery.contains(PennantConstants.CUST_DEDUP_LIST_BUILD_LIKE)) {
			replaceString = PennantConstants.CUST_DEDUP_LIST_BUILD_LIKE;
		}

		if (StringUtils.isEmpty(replaceString)) {
			return dedupParmDAO.fetchCustomerDedupDetails(aCustomerDetails.getCustDedup(), dedupParm.getSQLQuery());
		}

		StringBuilder rule = new StringBuilder();

		List<CustomerDocument> custDocuments = aCustomerDetails.getCustomerDocumentsList();
		if (CollectionUtils.isNotEmpty(custDocuments)) {
			for (CustomerDocument customerDocument : custDocuments) {
				if (StringUtils.isNotEmpty(rule.toString())) {
					rule.append("or");
				}
				rule.append("(" + PennantConstants.CUST_DEDUP_LISTFILED2 + " = '");
				rule.append(customerDocument.getCustDocType());
				rule.append("' AND " + PennantConstants.CUST_DEDUP_LISTFILED3 + " = '");
				rule.append(customerDocument.getCustDocTitle());
				rule.append("')");
			}
		} else {
			rule.append(PennantConstants.CUST_DEDUP_LISTFILED2 + " IN (");
		}

		dedupParm.setSQLQuery(dedupParm.getSQLQuery().replace(replaceString, rule.toString()));

		logger.debug(dedupParm.getSQLQuery());

		return dedupParmDAO.fetchCustomerDedupDetails(aCustomerDetails.getCustDedup(), dedupParm.getSQLQuery());
	}

	@Override
	public List<FinanceDedup> fetchFinDedupDetails(String userRole, FinanceDedup fd, String curLoginUser,
			String finType) {
		FinanceReferenceDetail frd = new FinanceReferenceDetail();
		frd.setMandInputInStage(userRole + ",");
		frd.setFinType(finType);
		List<FinanceReferenceDetail> queryCodeList = dedupParmDAO.getQueryCodeList(frd, "_AFDView");

		if (CollectionUtils.isEmpty(queryCodeList)) {
			return new ArrayList<>();
		}

		List<FinanceDedup> excdFinDedupList = new ArrayList<>();
		List<FinanceDedup> newFinDedupList = new ArrayList<>();

		List<String> fieldNameList = dedupParmDAO.getRuleFieldNames(FinanceConstants.DEDUP_FINANCE);
		for (FinanceReferenceDetail queryCode : queryCodeList) {
			String finReference = fd.getFinReference();
			String lovDescName = queryCode.getLovDescNamelov();
			List<FinanceDedup> excdList = financeDedupeDAO.fetchOverrideDedupData(finReference, lovDescName);

			DedupParm dedupParm = getApprovedDedupParmById(lovDescName, FinanceConstants.DEDUP_FINANCE, "L");

			if (dedupParm != null) {
				String sqlQuery = dedupParm.getSQLQuery();

				List<FinanceDedup> newTempList = dedupParmDAO.fetchFinDedupDetails(fd, sqlQuery);

				if (CollectionUtils.isNotEmpty(newTempList) && sqlQuery != null) {
					for (FinanceDedup newDedup : newTempList) {
						newDedup.setDedupeRule("," + dedupParm.getQueryCode() + ",");
						newDedup.setFinReference(finReference);
						newDedup.setRules(getQueryFields(sqlQuery, fieldNameList));
					}
					newFinDedupList.addAll(newTempList);
				}

				if (CollectionUtils.isNotEmpty(excdList) && sqlQuery != null) {
					for (FinanceDedup excdDedup : excdList) {
						excdDedup.setRules(getQueryFields(sqlQuery, fieldNameList));
						excdDedup.setDedupeRule("," + dedupParm.getQueryCode() + ",");
						excdDedup.setOverrideUser(excdDedup.getOverrideUser());
					}
					excdFinDedupList.addAll(excdList);
				}
			}
		}

		doSetIsNewRecord(excdFinDedupList, newFinDedupList);
		excdFinDedupList = doSetFinDeDupGrouping(excdFinDedupList, newFinDedupList);
		dosetOverrideOrNot(excdFinDedupList, queryCodeList);

		return excdFinDedupList;
	}

	private List<FinanceDedup> dosetOverrideOrNot(List<FinanceDedup> fdList, List<FinanceReferenceDetail> frdList) {
		Map<String, Boolean> queryOverrideMap = new HashMap<>();
		Map<String, String> overrideRuleDesc = new HashMap<>();

		frdList.forEach(l1 -> queryOverrideMap.put(l1.getLovDescNamelov(), l1.isOverRide()));

		Map<String, Boolean> matchedOverMap = new HashMap<>();

		for (FinanceDedup financeDedup : fdList) {
			String[] rulesList = financeDedup.getDedupeRule().split(",");
			for (String rule : rulesList) {
				if (queryOverrideMap.containsKey(rule)) {
					Boolean queryOveride = queryOverrideMap.get(rule);
					financeDedup.setOverride(queryOveride);
					if (financeDedup.isOverride()) {
						financeDedup.setOverride(queryOveride);
					} else {
						financeDedup.setOverride(false);
						matchedOverMap.put(rule, queryOveride);
					}
				}
			}

			for (FinanceReferenceDetail referenceDetail : frdList) {
				String lovDescName = referenceDetail.getLovDescNamelov();
				if (matchedOverMap.containsKey(lovDescName)) {
					overrideRuleDesc.put(lovDescName, referenceDetail.getLovDescRefDesc());
				}
			}
			financeDedup.setOverridenMap(overrideRuleDesc);
		}

		return fdList;
	}

	private void doSetIsNewRecord(List<FinanceDedup> excdFinDedupList, List<FinanceDedup> newFinDedupList) {
		if (CollectionUtils.isEmpty(excdFinDedupList) && CollectionUtils.isNotEmpty(newFinDedupList)) {
			newFinDedupList.forEach(l1 -> l1.setNewRecord(true));
		} else if (CollectionUtils.isNotEmpty(excdFinDedupList) && CollectionUtils.isEmpty(newFinDedupList)) {
			excdFinDedupList.forEach(l1 -> l1.setNewRecord(false));
		} else {
			for (FinanceDedup oldDedup : excdFinDedupList) {
				for (FinanceDedup newdedup : newFinDedupList) {
					if (oldDedup.getDupReference().equals(newdedup.getDupReference())) {
						if (isRuleChanged(oldDedup.getDedupeRule(), newdedup.getDedupeRule())
								|| isRuleChanged(oldDedup.getStage(), newdedup.getStage())) {
							newdedup.setNewRecord(false);
						} else {
							newdedup.setNewRecord(true);
						}
					}
				}
			}
		}
	}

	public List<FinanceDedup> doSetFinDeDupGrouping(List<FinanceDedup> excdDedupList, List<FinanceDedup> newDedupList) {
		List<FinanceDedup> groupFinDedupList = new ArrayList<>();

		groupFinDedupList.addAll(excdDedupList);
		groupFinDedupList.addAll(newDedupList);

		Map<String, FinanceDedup> excdDedupMap = new HashMap<>();

		for (FinanceDedup financeDedup : groupFinDedupList) {
			if (!excdDedupMap.containsKey(financeDedup.getDupReference())) {
				excdDedupMap.put(financeDedup.getDupReference(), financeDedup);
			}
		}

		for (FinanceDedup newDedup : groupFinDedupList) {
			if (excdDedupMap.containsKey(newDedup.getDupReference())) {
				FinanceDedup excdDedup = excdDedupMap.get(newDedup.getDupReference());

				if (!excdDedup.getDedupeRule().contains(newDedup.getDedupeRule())) {
					excdDedup.setDedupeRule(excdDedup.getDedupeRule() + newDedup.getDedupeRule().substring(1));
				}
				if (!StringUtils.trimToEmpty(excdDedup.getStage())
						.contains(StringUtils.trimToEmpty(newDedup.getStage()))) {
					excdDedup.setStage(excdDedup.getStage() + "," + StringUtils.trimToEmpty(newDedup.getStage()));
				}

				excdDedup.setRules(excdDedup.getRules() + "," + newDedup.getRules());
			} else {
				excdDedupMap.put(newDedup.getDupReference(), newDedup);
			}
		}

		excdDedupList = new ArrayList<>(excdDedupMap.values());

		return excdDedupList;
	}

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		DedupParm dedupParm = new DedupParm();
		BeanUtils.copyProperties((DedupParm) auditHeader.getAuditDetail().getModelData(), dedupParm);

		if (dedupParm.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			dedupParmDAO.delete(dedupParm, "");

		} else {
			dedupParm.setRoleCode("");
			dedupParm.setNextRoleCode("");
			dedupParm.setTaskId("");
			dedupParm.setNextTaskId("");
			dedupParm.setWorkflowId(0);

			if (dedupParm.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				dedupParm.setRecordType("");
				dedupParmDAO.save(dedupParm, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				dedupParm.setRecordType("");
				dedupParmDAO.update(dedupParm, "");
			}
		}

		dedupParmDAO.delete(dedupParm, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(dedupParm);

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		DedupParm dedupParm = (DedupParm) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		dedupParmDAO.delete(dedupParm, "_Temp");

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		auditDetail.setErrorDetails(new ArrayList<>());
		DedupParm dedupParm = (DedupParm) auditDetail.getModelData();

		DedupParm tempDedupParm = null;
		if (dedupParm.isWorkflow()) {
			tempDedupParm = dedupParmDAO.getDedupParmByID(dedupParm.getQueryCode(), dedupParm.getQueryModule(),
					dedupParm.getQuerySubCode(), "_Temp");
		}

		DedupParm befDedupParm = dedupParmDAO.getDedupParmByID(dedupParm.getQueryCode(), dedupParm.getQueryModule(),
				dedupParm.getQuerySubCode(), "");

		DedupParm oldDedupParm = dedupParm.getBefImage();

		String[] valueParm = new String[1];
		String[] errParm = new String[1];

		valueParm[0] = dedupParm.getQueryCode();
		errParm[0] = PennantJavaUtil.getLabel("label_QueryCode") + ":" + valueParm[0];

		if (dedupParm.isNew()) { // for New record or new record into work flow

			if (!dedupParm.isWorkflow()) {// With out Work flow only new records
				if (befDedupParm != null) { // Record Already Exists in the
					// table then error
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow
				if (dedupParm.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
																							// records
																							// type
																							// is new
					if (befDedupParm != null || tempDedupParm != null) { // if
																			// records
																			// already
																			// exists
																			// in
																			// the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befDedupParm == null || tempDedupParm != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
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
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));
				} else {
					if (oldDedupParm != null && !oldDedupParm.getLastMntOn().equals(befDedupParm.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, null));
						} else {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, null));
						}
					}
				}
			} else {

				if (tempDedupParm == null) { // if records not exists in the
												// Work flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempDedupParm != null && oldDedupParm != null
						&& !oldDedupParm.getLastMntOn().equals(tempDedupParm.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !dedupParm.isWorkflow()) {
			dedupParm.setBefImage(befDedupParm);
		}

		return auditDetail;
	}

	@Override
	public List<BlackListCustomers> fetchBlackListCustomers(String userRole, String finType,
			BlackListCustomers blCustData, String curUser) {
		logger.debug(Literal.ENTERING);

		List<BlackListCustomers> blackListCustomers = new ArrayList<>();
		List<FinBlacklistCustomer> overrideBlackList = new ArrayList<>();
		boolean newUser = false;

		FinanceReferenceDetail frd = new FinanceReferenceDetail();
		frd.setMandInputInStage(userRole + ",");
		frd.setFinType(finType);
		List<FinanceReferenceDetail> queryCodeList = dedupParmDAO.getQueryCodeList(frd, "_ABDView");

		String blackList = FinanceConstants.DEDUP_BLACKLIST;
		String custCtgCode = blCustData.getCustCtgCode();

		if (CollectionUtils.isEmpty(queryCodeList) && "".equals(finType)) {
			queryCodeList = new ArrayList<>();
			List<DedupParm> dedupList = dedupParmDAO.getDedupParmByModule(blackList, custCtgCode, "");
			for (DedupParm dedupParm : dedupList) {
				FinanceReferenceDetail detail = new FinanceReferenceDetail();
				detail.setLovDescNamelov(dedupParm.getQueryCode());
				detail.setOverRide(true);
				queryCodeList.add(detail);
			}
		}

		if (queryCodeList == null) {
			return blackListCustomers;
		}

		List<DedupParm> dedupParmList = new ArrayList<>();

		for (FinanceReferenceDetail queryCode : queryCodeList) {
			String descName = queryCode.getLovDescNamelov();
			String reference = blCustData.getFinReference();
			String cif = blCustData.getCustCIF();
			List<FinBlacklistCustomer> list = blacklistCustomerDAO.fetchOverrideBlackListData(reference, descName, cif);

			DedupParm dedupParm = getApprovedDedupParmById(descName, blackList, custCtgCode);

			if (dedupParm != null) {
				dedupParmList.add(dedupParm);
			}

			list.forEach(l1 -> l1.setOverridenby(l1.getOverrideUser()));
			overrideBlackList.addAll(list);
		}

		if (CollectionUtils.isNotEmpty(dedupParmList)) {
			blackListCustomers.addAll(getBlackListCustomer(blCustData, dedupParmList));
			if (CollectionUtils.isEmpty(dedupParmList)) {
				return blackListCustomers;
			}
			blackListCustomers = resetBlackListedCustData(blackListCustomers, queryCodeList);
		} else {
			for (FinBlacklistCustomer blackListCust : overrideBlackList) {
				if (!blackListCust.getOverrideUser().contains(curUser)) {
					blackListCust.setOverridenby(blackListCust.getOverrideUser());
					blackListCust.setOverrideUser(blackListCust.getOverrideUser() + "," + curUser);
					newUser = false;
				}
			}
		}

		blackListCustomers = doSetDeDupGrouping(blackListCustomers);

		if (CollectionUtils.isNotEmpty(overrideBlackList) && CollectionUtils.isEmpty(blackListCustomers)) {
			blackListCustomers = doSetFinBlacklistCustomers(overrideBlackList);
			return blackListCustomers;
		} else if (CollectionUtils.isEmpty(overrideBlackList) || CollectionUtils.isEmpty(blackListCustomers)) {
			return blackListCustomers;
		}

		for (FinBlacklistCustomer previousBlacklist : overrideBlackList) {
			for (BlackListCustomers currentBlacklist : blackListCustomers) {
				if (previousBlacklist.getFinReference().equals(currentBlacklist.getFinReference())) {
					if (previousBlacklist.getCustCIF().equals(currentBlacklist.getCustCIF())) {
						String prvOverrideUser = previousBlacklist.getOverrideUser();
						currentBlacklist.setOverridenby(prvOverrideUser);

						if (prvOverrideUser.contains(curUser)) {
							currentBlacklist.setOverrideUser(prvOverrideUser);
							newUser = false;
						} else {
							currentBlacklist.setOverrideUser(prvOverrideUser + "," + curUser);
						}

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

		logger.debug(Literal.ENTERING);
		return blackListCustomers;
	}

	private boolean isRuleChanged(String overrideListRule, String newListRule) {
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

		return false;
	}

	private List<BlackListCustomers> doSetFinBlacklistCustomers(List<FinBlacklistCustomer> overrideBlackList) {
		List<BlackListCustomers> list = new ArrayList<>();

		for (FinBlacklistCustomer finBlacklist : overrideBlackList) {
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
			blacklistCustomer.setEmployer(Long.parseLong((finBlacklist.getEmployer())));
			blacklistCustomer.setWatchListRule(finBlacklist.getWatchListRule());
			blacklistCustomer.setOverride(finBlacklist.isOverride());
			blacklistCustomer.setOverrideUser(finBlacklist.getOverrideUser());
			blacklistCustomer.setMobileNumber(finBlacklist.getMobileNumber());
			blacklistCustomer.setNewBlacklistRecord(finBlacklist.isNewBlacklistRecord());
			list.add(blacklistCustomer);
		}

		return list;
	}

	private List<BlackListCustomers> getBlackListCustomer(BlackListCustomers blCustData,
			List<DedupParm> dedupParmList) {

		if (CollectionUtils.isEmpty(dedupParmList)) {
			return new ArrayList<>();
		}

		List<BlackListCustomers> blackListCustomerList = new ArrayList<>();

		String custCtgCode = blCustData.getCustCtgCode();
		List<String> fieldNameList = dedupParmDAO.getRuleFieldNames(custCtgCode + FinanceConstants.DEDUP_BLACKLIST);

		for (DedupParm dedupParm : dedupParmList) {
			String sqlQuery = dedupParm.getSQLQuery();
			List<BlackListCustomers> list = blacklistCustomerDAO.fetchBlackListedCustomers(blCustData, sqlQuery);

			for (BlackListCustomers blkList : list) {
				blkList.setWatchListRule(dedupParm.getQueryCode());
				blkList.setFinReference(blCustData.getFinReference());
				blkList.setQueryField(getQueryFields(sqlQuery, fieldNameList));
				blkList.setSourceCIF(blCustData.getCustCIF());
				blackListCustomerList.add(blkList);
			}
		}

		return blackListCustomerList;
	}

	private String getQueryFields(String sqlQuery, List<String> fieldNameList) {
		String ruleSplitRegex = "[^a-zA-Z0-9_]+";

		if (StringUtils.isBlank(sqlQuery) || fieldNameList == null) {
			return "";
		}

		String queryFieldArray[] = sqlQuery.split(ruleSplitRegex);
		String value = "";
		String ruleString = "";
		try {
			for (int i = 0; i < queryFieldArray.length; i++) {
				if (App.DATABASE == Database.POSTGRES) {
					for (Object dbRuleField : fieldNameList) {
						if (dbRuleField instanceof PGobject) {
							PGobject pGobject = (PGobject) dbRuleField;
							if (queryFieldArray[i].equalsIgnoreCase(pGobject.getValue())) {
								value = value + queryFieldArray[i].trim() + PennantConstants.DELIMITER_COMMA;
							}
						} else if (dbRuleField instanceof String) {
							if (queryFieldArray[i].equalsIgnoreCase(dbRuleField.toString())) {
								value = value + queryFieldArray[i].trim() + PennantConstants.DELIMITER_COMMA;
							}
						}
					}
				} else {
					for (String dbRuleField : fieldNameList) {
						if (queryFieldArray[i].equalsIgnoreCase(dbRuleField)) {
							value = value + queryFieldArray[i].trim() + PennantConstants.DELIMITER_COMMA;
						}
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
			logger.error(Literal.EXCEPTION, e);
		}

		return "";
	}

	private List<BlackListCustomers> doSetDeDupGrouping(List<BlackListCustomers> blackListCustomerList) {
		try {
			for (int i = 0; i < blackListCustomerList.size(); i++) {
				BlackListCustomers iBlackList = blackListCustomerList.get(i);
				for (int j = i + 1; j <= blackListCustomerList.size() - 1; j++) {
					BlackListCustomers jBlackList = blackListCustomerList.get(j);
					if (iBlackList.getCustCIF().equals(jBlackList.getCustCIF())) {
						if (!iBlackList.getWatchListRule().contains(jBlackList.getWatchListRule())) {
							iBlackList.setWatchListRule(
									iBlackList.getWatchListRule() + "," + jBlackList.getWatchListRule());
						}
						iBlackList.setQueryField(iBlackList.getQueryField() + "," + jBlackList.getQueryField());
						if (!jBlackList.isOverride()) {
							iBlackList.setOverride(jBlackList.isOverride());
						}
						blackListCustomerList.remove(j);
						j--;
					}
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return blackListCustomerList;
	}

	private List<BlackListCustomers> resetBlackListedCustData(List<BlackListCustomers> blackListCustomers,
			List<FinanceReferenceDetail> queryCodeList) {
		Map<String, Boolean> queryOverrideMap = new HashMap<>();

		queryCodeList.forEach(l1 -> queryOverrideMap.put(l1.getLovDescNamelov(), l1.isOverRide()));

		for (BlackListCustomers blackListCust : blackListCustomers) {
			String[] watchList = blackListCust.getWatchListRule().split(",");
			for (String key : watchList) {
				if (queryOverrideMap.containsKey(key)) {
					blackListCust.setOverride(queryOverrideMap.get(key));
					if (blackListCust.isOverride()) {
						blackListCust.setOverride(queryOverrideMap.get(key));
					} else {
						blackListCust.setOverride(false);
						break;
					}
				}
			}
			if (queryOverrideMap.containsKey(blackListCust.getWatchListRule())) {
				blackListCust.setOverride(queryOverrideMap.get(blackListCust.getWatchListRule()));
			}
		}

		return blackListCustomers;
	}

	@Override
	public List<FinanceReferenceDetail> getQueryCodeList(FinanceReferenceDetail financeRefDetail, String tableType) {
		return dedupParmDAO.getQueryCodeList(financeRefDetail, tableType);
	}

	@Override
	public List<CustomerDedup> getDedupCustomerDetails(CustomerDetails details, String finType, String ref) {
		logger.debug(Literal.ENTERING);

		if (customerDedupService == null) {
			logger.debug(Literal.LEAVING);
			return null;
		}

		DedupCustomerDetail dedupCustomerDetail = preparededupRequest(details, finType, ref);
		DedupCustomerResponse response = new DedupCustomerResponse();
		try {
			response = customerDedupService.invokeDedup(dedupCustomerDetail);
			if (response.getErrorCode() != null && response.getErrorCode() != "00") {
				throw new InterfaceException(response.getErrorCode(),
						Labels.getLabel("Dedupe_other_system_Error_Response") + response.getErrorDesc());
			}
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			if (e instanceof ProcessingException) {
				throw new InterfaceException("9999", Labels.getLabel("Dedupe_other_system_Process_Error"));
			} else {
				if (StringUtils.trimToNull(e.getMessage()) != null) {
					throw new InterfaceException("9999", e.getMessage());
				} else {
					throw new InterfaceException("9999", Labels.getLabel("Dedupe_other_system_Process_Error"));
				}
			}
		}

		List<CustomerDedup> customerDedup = getDedupData(response, details);
		if ("No Match".equalsIgnoreCase(response.getErrorDesc())) {
			WSReturnStatus status = new WSReturnStatus();
			status.setReturnText("No Match");
			details.setReturnStatus(status);
		}

		logger.debug(Literal.LEAVING);
		return customerDedup;
	}

	private List<CustomerDedup> getDedupData(DedupCustomerResponse response, CustomerDetails details) {
		if (response == null) {
			return new ArrayList<>();
		}

		List<DedupCustomerDetail> dcdList = response.getDedupCustomerDetails();
		if (CollectionUtils.isEmpty(dcdList)) {
			return new ArrayList<>();
		}

		List<CustomerDedup> cdList = new ArrayList<>();
		logger.debug("Response dedup list :" + dcdList.size());

		for (DedupCustomerDetail dedupDetail : dcdList) {
			CustomerDedup cd = new CustomerDedup();
			cd.setCustCIF(details.getCustomer().getCustCIF());
			Customer customer = dedupDetail.getCustomer();
			cd.setCustShrtName(customer.getCustShrtName());
			cd.setCustDOB(customer.getCustDOB());
			cd.setCustFName(customer.getCustFName());
			cd.setCustCRCPR(customer.getCustCRCPR());
			cd.setCustCoreBank(customer.getCustCoreBank());
			cd.setSourceSystem(customer.getSourceSystem());
			cd.setSourceSystem(customer.getSourceSystem());
			cd.setCustCoreBank(customer.getCustCoreBank());

			for (CustomerPhoneNumber phonenumber : dedupDetail.getCustomerPhoneNumList()) {
				if ("MOBILE".equals(phonenumber.getPhoneTypeCode())) {
					cd.setPhoneNumber(phonenumber.getPhoneNumber());
					break;
				}
			}

			for (CustomerAddres addresstype : dedupDetail.getAddressList()) {
				if ("OFFICE".equals(addresstype.getCustAddrType()) || "HOME".equals(addresstype.getCustAddrType())) {
					cd.setAddress(StringUtils.trimToEmpty(addresstype.getCustAddrLine1() + ","
							+ addresstype.getCustAddrCity() + "," + addresstype.getCustAddrZIP()));
					break;
				}
			}

			cdList.add(cd);
		}

		return cdList;
	}

	private DedupCustomerDetail preparededupRequest(CustomerDetails customerDetails, String finType, String ref) {
		DedupCustomerDetail dcd = new DedupCustomerDetail();

		if (customerDetails == null) {
			return new DedupCustomerDetail();
		}

		Customer customer = customerDetails.getCustomer();

		if (customer == null) {
			return new DedupCustomerDetail();
		}

		dcd.setFinReference(ref);
		dcd.setCustID(customer.getCustID());
		dcd.setCustCIF(customer.getCustCIF());
		dcd.setCustomer(customer);
		dcd.setFinType(finType);
		dcd.setCustomerDocumentsList(customerDetails.getCustomerDocumentsList());
		dcd.setAddressList(customerDetails.getAddressList());
		dcd.setCustomerPhoneNumList(customerDetails.getCustomerPhoneNumList());
		dcd.setCustomerEMailList(customerDetails.getCustomerEMailList());

		return dcd;
	}

	// Loan Customer dedup execution//
	@Override
	public List<CustomerDedup> doCustomerDedup(CustomerDetails customerDetails, LoggedInUser userDetails,
			String finType, String userRole) {

		if (customerDetails == null || customerDetails.getCustomer() == null) {
			return new ArrayList<>();
		}

		CustomerDedup customerDedup = doSetCustomerDedup(customerDetails);

		return fetchCustomerDedupDetails(userRole, customerDedup, userDetails.getUserName(), finType);

	}

	private static CustomerDedup doSetCustomerDedup(CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);

		String mobileNumber = "";
		String emailid = "";
		String aadharId = "";
		String aadhar = masterDefDAO.getMasterCode("DOC_TYPE", "AADHAAR");
		String passPort = masterDefDAO.getMasterCode("DOC_TYPE", "PASSPORT");

		Customer customer = customerDetails.getCustomer();
		if (customerDetails.getCustomerPhoneNumList() != null) {
			for (CustomerPhoneNumber custPhone : customerDetails.getCustomerPhoneNumList()) {
				if (String.valueOf(custPhone.getPhoneTypePriority()).equals(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
					mobileNumber = PennantApplicationUtil.formatPhoneNumber(custPhone.getPhoneCountryCode(),
							custPhone.getPhoneAreaCode(), custPhone.getPhoneNumber());
					break;
				}
			}
		}
		if (customerDetails.getCustomerEMailList() != null) {
			for (CustomerEMail email : customerDetails.getCustomerEMailList()) {
				if (String.valueOf(email.getCustEMailPriority()).equals(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
					emailid = email.getCustEMail();
					break;
				}
			}
		}
		// Aadhar
		if (customerDetails.getCustomerDocumentsList() != null) {
			for (CustomerDocument document : customerDetails.getCustomerDocumentsList()) {
				if (document.getCustDocCategory().equals(aadhar)) {
					aadharId = document.getCustDocTitle();
					break;
				}
			}
		}
		// Passport
		if (customerDetails.getCustomerDocumentsList() != null) {
			for (CustomerDocument document : customerDetails.getCustomerDocumentsList()) {
				if (document.getCustDocCategory().equals(passPort)) {
					passPort = document.getCustDocTitle();
					break;
				}
			}
		}

		CustomerDedup customerDedup = new CustomerDedup();
		customerDedup.setFinReference(customer.getCustCIF());
		customerDedup.setCustId(customer.getCustID());
		customerDedup.setCustCIF(customer.getCustCIF());
		customerDedup.setCustFName(customer.getCustFName());
		customerDedup.setCustLName(customer.getCustLName());
		customerDedup.setCustShrtName(customer.getCustShrtName());
		customerDedup.setCustDOB(customer.getCustDOB());
		customerDedup.setCustCRCPR(customer.getCustCRCPR());
		customerDedup.setAadharNumber(aadharId);
		customerDedup.setCustCtgCode(customer.getCustCtgCode());
		customerDedup.setCustDftBranch(customer.getCustDftBranch());
		customerDedup.setCustSector(customer.getCustSector());
		customerDedup.setCustSubSector(customer.getCustSubSector());
		customerDedup.setCustNationality(customer.getCustNationality());
		customerDedup.setCustPassportNo(passPort);
		customerDedup.setCustTradeLicenceNum(customer.getCustTradeLicenceNum());
		customerDedup.setCustVisaNum(customer.getCustVisaNum());
		customerDedup.setMobileNumber(mobileNumber);
		customerDedup.setCustPOB(customer.getCustPOB());
		customerDedup.setCustResdCountry(customer.getCustResdCountry());
		customerDedup.setCustEMail(emailid);

		logger.debug(Literal.LEAVING);
		return customerDedup;

	}

	@SuppressWarnings("rawtypes")
	@Override
	public List validate(String resultQuery, CustomerDedup customerDedup) {
		return dedupParmDAO.validate(resultQuery, customerDedup);
	}

	@Override
	public List<DedupParm> getDedupParmByModule(String queryModule, String querySubCode, String type) {
		return dedupParmDAO.getDedupParmByModule(queryModule, querySubCode, type);
	}

	@Override
	public List<CollateralSetup> queryExecution(String query, Map<String, Object> fielValueMap) {
		return dedupParmDAO.queryExecution(query, fielValueMap);
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
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

	public void setBlacklistCustomerDAO(BlackListCustomerDAO blacklistCustomerDAO) {
		this.blacklistCustomerDAO = blacklistCustomerDAO;
	}

	public void setCustomerDedupDAO(CustomerDedupDAO customerDedupDAO) {
		this.customerDedupDAO = customerDedupDAO;
	}

	public void setCustomerInterfaceService(CustomerInterfaceService customerInterfaceService) {
		this.customerInterfaceService = customerInterfaceService;
	}

	public static void setMasterDefDAO(MasterDefDAO masterDefDAO) {
		DedupParmServiceImpl.masterDefDAO = masterDefDAO;
	}

}