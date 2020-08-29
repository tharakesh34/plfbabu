/**
Copyright 2011 - Pennant Technologies
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
 *
 * FileName    		:  InstitutionLimitRebuild.java											*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  23-06-2017															*
 *                                                                  
 * Modified Date    :  23-06-2017															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-06-2017       Pennant	                 0.1          Added for Bajaj Demo.             * 
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

package com.pennant.backend.service.limitservice.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.dao.customermasters.CustomerAddresDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.limit.LimitDetailDAO;
import com.pennant.backend.dao.limit.LimitGroupLinesDAO;
import com.pennant.backend.dao.limit.LimitHeaderDAO;
import com.pennant.backend.dao.limit.LimitStructureDetailDAO;
import com.pennant.backend.dao.limit.LimitTransactionDetailsDAO;
import com.pennant.backend.dao.rulefactory.impl.LimitRuleDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.limit.LimitDetails;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.model.limit.LimitReferenceMapping;
import com.pennant.backend.model.limit.LimitStructureDetail;
import com.pennant.backend.model.limit.LimitTransactionDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.LimitFilterQuery;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.LimitConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.eod.step.StepUtil;

public class InstitutionLimitRebuild {
	private static Logger logger = LogManager.getLogger(LimitManagement.class);

	@Autowired
	private CustomerDAO customerDAO;
	@Autowired
	private LimitDetailDAO limitDetailDAO;
	@Autowired
	private LimitHeaderDAO limitHeaderDAO;
	@Autowired
	private LimitRuleDAO limitRuleDAO;
	@Autowired
	private FinanceDisbursementDAO financeDisbursementDAO;
	@Autowired
	private RuleExecutionUtil ruleExecutionUtil;
	@Autowired
	private LimitGroupLinesDAO limitGroupLinesDAO;
	@Autowired
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	@Autowired
	private LimitStructureDetailDAO limitStructureDetailDAO;
	@Autowired
	private CustomerAddresDAO customerAddresDAO;
	@Autowired
	private LimitTransactionDetailsDAO limitTransactionDetailDAO;

	public void executeLimitRebuildProcess() throws DatatypeConfigurationException {
		logger.debug(Literal.ENTERING);

		List<LimitHeader> limitHeaderList = limitHeaderDAO.getLimitHeaders(TableType.MAIN_TAB.getSuffix());

		Map<String, Set<String>> fieldMap = getLimitFieldMap();

		for (LimitHeader limitHeader : limitHeaderList) {
			String ruleCode = limitHeader.getRuleCode();
			long headerId = limitHeader.getHeaderId();

			LimitFilterQuery limitFilterQuery = limitRuleDAO.getLimitRuleByQueryCode(ruleCode,
					RuleConstants.MODULE_IRLFILTER, "_AView");
			List<LimitDetails> limitDetailsList = limitDetailDAO.getLimitDetails(headerId);

			processStructuralChanges(limitDetailsList, limitHeader);

			resetLimitDetails(limitDetailsList);

			// Finance List Based on SQL Rule
			String sqlQuery = limitFilterQuery.getSQLQuery();

			List<FinanceMain> financeMains = new ArrayList<FinanceMain>();
			financeMains.addAll(limitHeaderDAO.getInstitutionLimitFields(fieldMap.get("fm_"), sqlQuery, false));
			financeMains.addAll(limitHeaderDAO.getInstitutionLimitFields(fieldMap.get("fm_"), sqlQuery, true));

			Map<Long, List<FinanceMain>> custmains = new HashMap<Long, List<FinanceMain>>();

			StepUtil.INSTITUTION_LIMITS_UPDATE.setTotalRecords(financeMains.size());

			for (FinanceMain financeMain : financeMains) {

				if (!(FinanceConstants.CLOSE_STATUS_CANCELLED.equals(financeMain.getClosingStatus())
						|| PennantConstants.RCD_STATUS_CANCELLED.equals(financeMain.getRecordStatus()))) {

					List<FinanceMain> list = custmains.get(financeMain.getCustID());
					if (list == null) {
						list = new ArrayList<FinanceMain>();
						custmains.put(financeMain.getCustID(), list);
					}
					list.add(financeMain);
				}

			}

			for (Entry<Long, List<FinanceMain>> entry : custmains.entrySet()) {
				processInstutionalLimit(limitHeader, limitDetailsList, entry.getValue(), fieldMap);
			}

			limitDetailDAO.updateReserveUtiliseList(limitDetailsList, "");
		}

		logger.debug(Literal.LEAVING);
	}

	private void processInstutionalLimit(LimitHeader limitHeader, List<LimitDetails> limitDetailsList,
			List<FinanceMain> finMains, Map<String, Set<String>> fieldMap) {
		logger.debug(Literal.ENTERING);

		if (finMains == null || finMains.isEmpty()) {
			return;
		}

		Map<String, FinanceType> finTypeMap = new HashMap<>();
		FinanceType financeType = null;
		int processedRecords = 0;
		LimitReferenceMapping mapping = null;
		for (FinanceMain finMain : finMains) {
			StepUtil.INSTITUTION_LIMITS_UPDATE.setProcessedRecords(++processedRecords);
			String finType = finMain.getFinType();
			financeType = finTypeMap.computeIfAbsent(finType, ft -> getFinanceTye(finType, fieldMap.get("ft_")));

			Customer customer = customerDAO.getCustomerByID(finMain.getCustID(), "");

			/**
			 * Commented the below code since customer address details are not using.
			 */
			/*
			 * CustomerAddres custAddress = customerAddresDAO.getHighPriorityCustAddr(finMain.getCustID(), ""); if
			 * (custAddress != null) { customer.setCustAddrProvince(custAddress.getCustAddrProvince()); }
			 */

			mapping = identifyLine(finMain, financeType, customer, limitHeader.getHeaderId(), limitDetailsList);
			processRebuild(finMain, limitHeader, limitHeader.getHeaderId(), limitDetailsList, mapping);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * 
	 * @param finMain
	 * @param limitHeader
	 * @param limitDetailsList
	 * @param mapping
	 */
	private void processRebuild(FinanceMain finMain, LimitHeader limitHeader, long inProgressHeaderID,
			List<LimitDetails> limitDetailsList, LimitReferenceMapping mapping) {
		logger.debug(Literal.LEAVING);

		String finRef = finMain.getFinReference();
		String finCategory = finMain.getFinCategory();
		String finCcy = finMain.getFinCcy();
		String limitCcy = limitHeader.getLimitCcy();
		List<LimitDetails> list = getCustomerLimitDetails(mapping);
		boolean addTempblock = false;

		// calculate reserve and utilized
		BigDecimal tranReseervAmt = finMain.getFinAssetValue();

		if (finMain.isLimitValid()) {
			//check the there is block in not then don not proceed
			LimitTransactionDetail transaction = getTransaction(finRef, inProgressHeaderID, 0);
			if (transaction == null) {
				// then no need block the amount
				return;
			}
		}

		BigDecimal tranUtilisedAmt = BigDecimal.ZERO;
		List<FinanceDisbursement> disbursementDetailList = financeDisbursementDAO.getFinanceDisbursementDetails(finRef,
				"", false);

		for (FinanceDisbursement disbursement : disbursementDetailList) {
			if (StringUtils.equals(disbursement.getDisbStatus(), FinanceConstants.DISB_STATUS_CANCEL)) {
				continue;
			}
			tranUtilisedAmt = tranUtilisedAmt.add(disbursement.getDisbAmount()).add(disbursement.getFeeChargeAmt());
		}
		tranUtilisedAmt = tranUtilisedAmt.subtract(finMain.getDownPayment());

		if (finMain.getFinCurrAssetValue().compareTo(tranReseervAmt) == 0) {
			addTempblock = true;
		}

		// convert to limit currency
		BigDecimal limitReserveAmt = CalculationUtil.getConvertedAmount(finCcy, limitCcy, tranReseervAmt);
		BigDecimal limitUtilisedAmt = CalculationUtil.getConvertedAmount(finCcy, limitCcy, tranUtilisedAmt);

		// update reserve and utilization
		for (LimitDetails details : list) {
			LimitDetails limitToUpdate = getLimitdetails(limitDetailsList, details);

			// add to reserve
			limitToUpdate.setReservedLimit(limitToUpdate.getReservedLimit().add(limitReserveAmt));

			// if records are not approved then we are considering reserve only
			if (finMain.isLimitValid()) {
				continue;
			}

			//not required in case of multiple disbursement with max disbursement check
			if (addTempblock) {
				// for Under process loans in addDisbursment
				LimitTransactionDetail transaction = getTransaction(finRef, inProgressHeaderID, -1);
				if (transaction != null) {
					// add to reserve
					limitToUpdate.setReservedLimit(limitToUpdate.getReservedLimit().add(transaction.getLimitAmount()));
				}
			}

			// remove from reserve
			if (limitReserveAmt.compareTo(limitUtilisedAmt) >= 0) {
				limitToUpdate.setReservedLimit(limitToUpdate.getReservedLimit().subtract(limitUtilisedAmt));
			} else {
				limitToUpdate.setReservedLimit(limitToUpdate.getReservedLimit().subtract(limitReserveAmt));
			}
			// add to utilized
			limitToUpdate.setUtilisedLimit(limitToUpdate.getUtilisedLimit().add(limitUtilisedAmt));
		}

		// if records are not approved then we are considering reserve only
		if (finMain.isLimitValid()) {
			return;
		}

		// update revolving nature by payments made
		BigDecimal repay = financeScheduleDetailDAO.getPriPaidAmount(finRef);
		BigDecimal repayLimit = CalculationUtil.getConvertedAmount(finCcy, limitCcy, repay);

		for (LimitDetails details : list) {
			LimitDetails limitToUpdate = getLimitdetails(limitDetailsList, details);
			limitToUpdate.setUtilisedLimit(limitToUpdate.getUtilisedLimit().subtract(repayLimit));
			if (FinanceConstants.PRODUCT_ODFACILITY.equals(finCategory)) {
				limitToUpdate.setReservedLimit(limitToUpdate.getReservedLimit().add(repayLimit));
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private LimitTransactionDetail getTransaction(String finRef, long headerid, int type) {
		return limitTransactionDetailDAO.getTransaction(LimitConstants.FINANCE, finRef, LimitConstants.BLOCK, headerid,
				type);
	}

	/**
	 * 
	 * @param limitDetailsList
	 * @param limitHeader
	 * @throws DatatypeConfigurationException
	 */
	private void processStructuralChanges(List<LimitDetails> limitDetailsList, LimitHeader limitHeader)
			throws DatatypeConfigurationException {
		logger.debug(Literal.ENTERING);

		List<LimitDetails> list = new ArrayList<LimitDetails>();
		List<LimitStructureDetail> structureList = limitStructureDetailDAO
				.getLimitStructureDetailById(limitHeader.getLimitStructureCode(), "");

		for (LimitStructureDetail limitStructureDetail : structureList) {
			if (!isExist(limitDetailsList, limitStructureDetail)) {
				LimitDetails details = getLimitDetails(limitStructureDetail, limitHeader);
				list.add(details);
			}
		}
		// Add New Structural Changes
		if (!list.isEmpty()) {
			limitDetailDAO.saveList(list, "");
			limitDetailsList.addAll(list);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * 
	 * @param limitDetailsList
	 * @param limitStructureDetail
	 * @return
	 */
	private boolean isExist(List<LimitDetails> limitDetailsList, LimitStructureDetail limitStructureDetail) {
		for (LimitDetails limitDetails : limitDetailsList) {
			if (limitDetails.getLimitStructureDetailsID() == limitStructureDetail.getLimitStructureDetailsID()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param limitStructureDetail
	 * @param limitHeader
	 * @return
	 * @throws DatatypeConfigurationException
	 */
	private LimitDetails getLimitDetails(LimitStructureDetail limitStructureDetail, LimitHeader limitHeader)
			throws DatatypeConfigurationException {

		LimitDetails limitDetail = new LimitDetails();

		limitDetail.setDetailId(Long.MIN_VALUE);
		limitDetail.setLimitHeaderId(limitHeader.getHeaderId());
		limitDetail.setLimitStructureDetailsID(limitStructureDetail.getLimitStructureDetailsID());
		limitDetail.setExpiryDate(limitHeader.getLimitExpiryDate());

		limitDetail.setLimitSanctioned(BigDecimal.ZERO);
		limitDetail.setReservedLimit(BigDecimal.ZERO);
		limitDetail.setUtilisedLimit(BigDecimal.ZERO);

		limitDetail.setLimitCheck(false);
		limitDetail.setLimitChkMethod(LimitConstants.LIMIT_CHECK_RESERVED);

		limitDetail.setVersion(1);
		limitDetail.setCreatedBy(limitStructureDetail.getCreatedBy());
		limitDetail.setCreatedOn(limitStructureDetail.getCreatedOn());
		limitDetail.setLastMntBy(limitStructureDetail.getLastMntBy());
		limitDetail.setLastMntOn(limitStructureDetail.getLastMntOn());
		limitDetail.setRecordStatus("Approved");
		limitDetail.setRoleCode("");
		limitDetail.setNextRoleCode("");
		limitDetail.setTaskId("");
		limitDetail.setNextTaskId("");
		limitDetail.setRecordType("");
		limitDetail.setWorkflowId(0);
		limitDetail.setRevolving(true);

		return limitDetail;
	}

	/**
	 * 
	 * @param limitDetailsList
	 */
	private void resetLimitDetails(List<LimitDetails> limitDetailsList) {
		for (LimitDetails limitDetail : limitDetailsList) {
			limitDetail.setReservedLimit(BigDecimal.ZERO);
			limitDetail.setUtilisedLimit(BigDecimal.ZERO);
		}
	}

	/**
	 * 
	 * @param list
	 * @param limitofind
	 * @return
	 */
	private LimitDetails getLimitdetails(List<LimitDetails> list, LimitDetails limitofind) {
		for (LimitDetails limitDetails : list) {
			if (limitofind.getDetailId() == limitDetails.getDetailId()) {
				return limitDetails;
			}

		}
		return null;
	}

	/**
	 * @param finMain
	 * @param financeType
	 * @param headerId
	 * @param limitDetailsList2
	 * @return
	 */
	private LimitReferenceMapping identifyLine(FinanceMain finMain, FinanceType financeType, Customer customer,
			long headerId, List<LimitDetails> limitDetailsList) {
		logger.debug(Literal.ENTERING);

		String finRef = finMain.getFinReference();
		LimitReferenceMapping mapping = new LimitReferenceMapping();
		mapping.setReferenceCode(LimitConstants.FINANCE);
		mapping.setReferenceNumber(finRef);
		mapping.setHeaderId(headerId);
		mapping.setNewRecord(true);

		Map<String, Object> dataMap = getDataMap(finMain, financeType, customer);
		if (limitDetailsList != null && limitDetailsList.size() > 0) {
			boolean uncalssifed = true;
			for (LimitDetails details : limitDetailsList) {

				if (StringUtils.isBlank(details.getSqlRule())) {
					continue;
				}

				boolean ruleResult = (boolean) ruleExecutionUtil.executeRule(details.getSqlRule(), dataMap, "",
						RuleReturnType.BOOLEAN);
				if (ruleResult) {
					mapping.setLimitLine(details.getLimitLine());
					uncalssifed = false;
					break;
				}
			}

			if (uncalssifed) {
				mapping.setLimitLine(LimitConstants.LIMIT_ITEM_UNCLSFD);
			}
		}

		logger.debug(Literal.LEAVING);
		return mapping;
	}

	private Map<String, Object> getDataMap(FinanceMain financeMain, FinanceType financeType, Customer customer) {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		if (financeMain != null) {
			dataMap.putAll(financeMain.getDeclaredFieldValues());
		}
		if (financeType != null) {
			dataMap.putAll(financeType.getDeclaredFieldValues());
		}
		if (customer != null) {
			dataMap.putAll(customer.getDeclaredFieldValues());
		}

		return dataMap;
	}

	private List<LimitDetails> getCustomerLimitDetails(LimitReferenceMapping mapping) {
		logger.debug(Literal.ENTERING);

		long headerId = mapping.getHeaderId();
		String limitLine = mapping.getLimitLine();
		List<LimitDetails> list = new ArrayList<LimitDetails>();
		List<String> groupCodes = new ArrayList<>();
		groupCodes.add(LimitConstants.LIMIT_ITEM_TOTAL);

		if (!limitLine.equals(LimitConstants.LIMIT_ITEM_UNCLSFD)) {
			String groupCode = limitGroupLinesDAO.getGroupByLineAndHeader(limitLine, headerId);
			String parentGroup = groupCode;

			while (!StringUtils.isEmpty(parentGroup)) {
				groupCodes.add(parentGroup);
				parentGroup = limitGroupLinesDAO.getGroupByGroupAndHeader(parentGroup, headerId);
			}
		}

		list = limitDetailDAO.getLimitByLineAndgroup(headerId, limitLine, groupCodes);

		logger.debug(Literal.LEAVING);
		return list;
	}

	private FinanceType getFinanceTye(String finType, Set<String> fields) {
		return limitHeaderDAO.getLimitFieldsByFinTpe(finType, fields);
	}

	private Map<String, Set<String>> getLimitFieldMap() {
		Map<String, Set<String>> limitFieldMap = new HashMap<>();

		List<String> limitFields = limitHeaderDAO.getLimitRuleFields();

		Set<String> fm = new HashSet<>();
		Set<String> ct = new HashSet<>();
		Set<String> ft = new HashSet<>();

		for (String field : limitFields) {
			if (StringUtils.startsWithIgnoreCase(field, "fm_")) {
				fm.add(StringUtils.substring(field, 3));
			} else if (StringUtils.startsWithIgnoreCase(field, "ct_")) {
				ct.add(StringUtils.substring(field, 3));
			} else if (StringUtils.startsWithIgnoreCase(field, "ft_")) {
				ft.add(StringUtils.substring(field, 3));
			}
		}

		fm.add("finReference");
		fm.add("closingStatus");
		fm.add("recordStatus");

		limitFieldMap.put("fm_", fm);
		limitFieldMap.put("ct_", ct);
		limitFieldMap.put("ft_", ft);

		return limitFieldMap;
	}

}